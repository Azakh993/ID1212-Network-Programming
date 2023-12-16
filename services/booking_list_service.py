import copy

from sqlalchemy.exc import IntegrityError

from models.booking_list import BookingList
from repositories import booking_list_repository as blr
from repositories import reservation_repository as rr
from util import utility as util


def get_booking_lists(course_code=None, booking_list_id=None):
    if course_code:
        booking_lists = blr.retrieve_booking_lists(course_code)
        if booking_lists is not None:
            return processed_booking_lists(booking_lists)

    elif booking_list_id:
        booking_list = blr.retrieve_booking_list(booking_list_id)
        if booking_list is not None:
            return processed_booking_lists(booking_list)[0]

    return []


def erase_booking_list(course_code, booking_id):
    try:
        blr.delete_booking_list(course_code, booking_id)
    except IntegrityError:
        return util.HTTP_422_UNPROCESSABLE_ENTITY, {"error": "Booking list is in use"}
    return util.HTTP_204_NO_CONTENT, None


def add_booking_list(course_code, booking_list_dto):
    if invalid_booking_list(booking_list_dto):
        return util.HTTP_400_BAD_REQUEST, {"error": "Invalid booking data"}

    new_booking_list = BookingList(
        course_id=course_code.upper(),
        user_id=booking_list_dto.user_id,
        description=booking_list_dto.description,
        location=booking_list_dto.location,
        time=booking_list_dto.time,
        interval=booking_list_dto.interval,
        max_slots=booking_list_dto.max_slots,
    )

    try:
        blr.insert_booking_list(new_booking_list)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return util.HTTP_422_UNPROCESSABLE_ENTITY, {"error": "Could not add booking list"}

    return util.HTTP_201_CREATED, {'newBookingList': serialized_booking_list(new_booking_list)}


def generate_processed_booking_list(booking_list_id):
    booking_list = [blr.retrieve_booking_list(booking_list_id)]
    if booking_list is not None:
        return processed_booking_lists(booking_list)[0]
    return None


def processed_booking_lists(booking_lists):
    booking_lists_without_seconds = []
    for booking_list in booking_lists:
        list_copy = copy.deepcopy(booking_list)
        list_copy.time = booking_list.time
        list_copy.max_slots = calculate_available_slots(list_copy)
        booking_lists_without_seconds.append(serialized_booking_list(list_copy))
    return booking_lists_without_seconds


def calculate_available_slots(booking_list):
    reservations = rr.retrieve_reservations(booking_list.id)
    return booking_list.max_slots - len(reservations)


def serialized_booking_list(booking_list):
    json_booking_list = {
        "id": booking_list.id,
        "description": booking_list.description,
        "location": booking_list.location,
        "time": booking_list.time.strftime('%Y-%m-%d %H:%M'),
        "interval": booking_list.interval,
        "available_slots": booking_list.max_slots
    }
    return json_booking_list


def invalid_booking_list(booking_list_dto):
    return any(value is None for value in (
        booking_list_dto.description,
        booking_list_dto.location,
        booking_list_dto.time,
        booking_list_dto.interval,
        booking_list_dto.max_slots
    ))
