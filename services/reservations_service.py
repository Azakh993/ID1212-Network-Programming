from datetime import datetime, timedelta

from models import Reservation
from repositories.booking_list_repository import retrieve_booking_list
from repositories.reservation_repository import (retrieve_reservations, insert_reservation, retrieve_user_reservations,
                                                 delete_reservation)
from repositories.user_repository import get_user_by_user_id


def generate_available_slots(booking_list):
    available_slots = []
    max_slots = booking_list.max_slots
    interval = booking_list.interval

    start_time = datetime.strptime(booking_list.time, "%Y-%m-%d %H:%M")

    for i in range(max_slots):
        string_date_time = format_datetime(start_time)
        available_slots.append(AvailableSlotDTO(booking_list.id, i, string_date_time))
        start_time += timedelta(minutes=interval)

    reservations = retrieve_reservations(booking_list.id)
    if reservations is not None:
        for reservation in reservations:
            user_id = reservation.user_id
            username = get_user_by_user_id(user_id).username

            available_slots[reservation.sequence_id].user_id = user_id
            available_slots[reservation.sequence_id].username = username

    return available_slots


def generate_json_ready_available_slots(booking_list):
    available_slots = generate_available_slots(booking_list)
    json_available_slots = [{
        "list_id": available_slot.list_id,
        "sequence_id": available_slot.sequence_id,
        "start_time": available_slot.start_time,
        "user_id": available_slot.user_id,
        "username": available_slot.username
    } for available_slot in available_slots]
    return json_available_slots


def generate_json_ready_reservation(reservation):
    return {
        "id": reservation.id,
        "list_id": reservation.list_id,
        "user_id": reservation.user_id,
        "sequence_id": reservation.sequence_id
    }


def reserve_slot(user_id, booking_list_id, sequence_id):
    new_reservation = Reservation(
        list_id=booking_list_id,
        user_id=user_id,
        sequence_id=sequence_id
    )

    try:
        insert_reservation(new_reservation)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return None
    return new_reservation


def generate_user_reservation_entries(course_code, user_id):
    user_reservations = retrieve_user_reservations(course_code, user_id)

    if user_reservations is None:
        return None

    user_reservation_entries = []

    for reservation in user_reservations:
        booking_list = retrieve_booking_list(reservation.list_id)
        reservation_start_time = booking_list.time + timedelta(minutes=booking_list.interval * reservation.sequence_id)

        user_reservation_entries.append({
            "id": reservation.id,
            "start_time": format_datetime(reservation_start_time),
            "description": booking_list.description,
            "location": booking_list.location,
            "length": booking_list.interval
        })

    return user_reservation_entries


def remove_user_reservation(reservation_id):
    try:
        delete_reservation(reservation_id)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return False
    return True


def format_datetime(datetime_to_format):
    return datetime_to_format.strftime("%Y-%m-%d %H:%M")


class AvailableSlotDTO:
    def __init__(self, booking_list_id, sequence_id, start_time):
        self.list_id = booking_list_id
        self.sequence_id = sequence_id
        self.start_time = start_time
        self.user_id = None
        self.username = None
