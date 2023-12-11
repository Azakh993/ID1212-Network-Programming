from datetime import datetime, timedelta

from repositories.reservation_repository import retrieve_reservations
from repositories.user_repository import get_user_by_user_id


def generate_available_slots(booking_list):
    available_slots = []
    max_slots = booking_list.max_slots
    interval = booking_list.interval

    format_string = "%Y-%m-%d %H:%M"
    start_time = datetime.strptime(booking_list.time, format_string)

    for i in range(max_slots):
        available_slots.append(AvailableSlotDTO(booking_list.id, i, start_time))
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


class AvailableSlotDTO:
    def __init__(self, booking_list_id, sequence_id, start_time):
        self.list_id = booking_list_id
        self.sequence_id = sequence_id
        self.start_time = start_time
        self.user_id = None
        self.username = None
