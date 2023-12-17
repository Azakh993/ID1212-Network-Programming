from datetime import timedelta

from models import Reservation
from repositories import booking_list_repository as blr
from repositories import reservation_repository as rr
from repositories import user_repository as ur
from util import utility as util


class AvailableSlotDTO:
    def __init__(self, booking_list_id, sequence_id, start_time):
        self.list_id = booking_list_id
        self.sequence_id = sequence_id
        self.start_time = start_time
        self.user_id = None
        self.username = None


def generate_available_slots(booking_list_id):
    booking_list = blr.retrieve_booking_list(booking_list_id)

    if booking_list is None:
        return None

    available_slots = [AvailableSlotDTO(
        booking_list_id=booking_list.id,
        start_time=process_datetime(booking_list.time, booking_list.interval, i),
        sequence_id=i) for i in range(booking_list.max_slots)]

    reservations = rr.retrieve_reservations(booking_list.id)
    if reservations is not None:
        for reservation in reservations:
            available_slots[reservation.sequence_id].user_id = reservation.user_id
            available_slots[reservation.sequence_id].username = ur.get_user_by_user_id(reservation.user_id).username

    return available_slots


def book_slot(course_code, user_id, booking_list_id, slot_id, username):
    requested_slot = get_requested_slot(booking_list_id, slot_id)
    if not slot_is_available(requested_slot):
        return util.HTTP_422_UNPROCESSABLE_ENTITY, {"error: ": "Slot is already booked"}

    user_id = get_user_id_for_booking(course_code, user_id, username)
    if user_id is None:
        return util.HTTP_404_NOT_FOUND, {"error: ": "User not found for booking"}

    if user_has_existing_reservations(user_id, booking_list_id):
        return util.HTTP_403_FORBIDDEN, {"error: ": "User has existing reservations"}

    reserved_slot = reserve_slot(user_id, booking_list_id, slot_id)
    if reserved_slot is not None:
        return util.HTTP_201_CREATED, serialized_reservation(reserved_slot)

    else:
        return util.HTTP_400_BAD_REQUEST, {"error: ": "Reservation could not be added"}


def get_requested_slot(booking_list_id, slot_id):
    return generate_available_slots(booking_list_id)[int(slot_id)]


def slot_is_available(slot):
    return slot.user_id is None


def get_user_id_for_booking(course_code, user_id, username):
    if username:
        requested_user_is_privileged = util.get_user_privileges(course_code, user_id)
        if requested_user_is_privileged:
            valid_user = ur.get_user_by_username_and_course_code(course_code, username)
            return valid_user.id if valid_user else None
    return user_id


def serialized_available_slots(booking_list_id):
    available_slots = generate_available_slots(booking_list_id)
    if available_slots is None:
        return None

    json_available_slots = [{
        "list_id": available_slot.list_id,
        "sequence_id": available_slot.sequence_id,
        "start_time": available_slot.start_time,
        "user_id": available_slot.user_id,
        "username": available_slot.username
    } for available_slot in available_slots]
    return json_available_slots


def serialized_reservation(reservation):
    return {
        "id": reservation.id,
        "list_id": reservation.list_id,
        "user_id": reservation.user_id,
        "sequence_id": reservation.sequence_id
    }


def reserve_slot(user_id, booking_list_id, sequence_id):
    new_reservation = Reservation(list_id=booking_list_id, user_id=user_id, sequence_id=sequence_id)
    try:
        rr.insert_reservation(new_reservation)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return None
    return new_reservation


def generate_user_reservation_entries(course_code, user_id):
    user_reservations = rr.retrieve_user_reservations(course_code, user_id)
    if user_reservations is None:
        return None
    return [create_reservation_entry(reservation) for reservation in user_reservations]


def create_reservation_entry(reservation):
    booking_list = blr.retrieve_booking_list(reservation.list_id)
    start_time = calculate_start_time(booking_list, reservation.sequence_id)

    return {
        "id": reservation.id,
        "start_time": process_datetime(start_time),
        "description": booking_list.description,
        "location": booking_list.location,
        "length": booking_list.interval
    }


def calculate_start_time(booking_list, sequence_id):
    return booking_list.time + timedelta(minutes=booking_list.interval * sequence_id)


def process_datetime(datetime_to_process, interval=0, sequence_id=0):
    datetime_to_process += timedelta(minutes=interval * sequence_id)
    return datetime_to_process.strftime("%Y-%m-%d %H:%M")


def remove_reservation(reservation_id):
    try:
        rr.delete_reservation(reservation_id)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return util.HTTP_404_NOT_FOUND, {"error": "Reservation could not be removed."}
    return util.HTTP_204_NO_CONTENT, None


def remove_slot_reservation(booking_list_id, slot_sequence_id):
    try:
        rr.delete_slot_reservation(booking_list_id, slot_sequence_id)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return util.HTTP_404_NOT_FOUND, {"error": "Reservation could not be removed."}
    return util.HTTP_204_NO_CONTENT, None


def user_has_existing_reservations(user_id, booking_list_id):
    return rr.retrieve_user_reservations_by_booking_list_id(user_id, booking_list_id)
