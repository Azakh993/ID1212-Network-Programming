import copy

from models.booking_list import BookingList
from repositories.booking_list_repository import retrieve_booking_lists, insert_booking_list, delete_booking_list


def get_booking_lists(course_code):
    booking_lists = retrieve_booking_lists(course_code)

    if booking_lists is None:
        return []

    booking_lists_without_seconds = remove_seconds_from_booking_lists(booking_lists)

    return booking_lists_without_seconds


def remove_seconds_from_booking_lists(booking_lists):
    booking_lists_without_seconds = []
    for booking_list in booking_lists:
        list_copy = copy.deepcopy(booking_list)
        list_copy.time = list_copy.time.strftime('%Y-%m-%d %H:%M')
        booking_lists_without_seconds.append(list_copy)
    return booking_lists_without_seconds


def add_booking_list(course_code, booking_list_dto):
    new_booking_list = BookingList(
        course_id=course_code.upper(),
        description=booking_list_dto.description,
        location=booking_list_dto.location,
        time=booking_list_dto.time,
        interval=booking_list_dto.interval,
        max_slots=booking_list_dto.max_slots
    )

    try:
        insert_booking_list(new_booking_list)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return False
    return True


def is_invalid_booking_list(booking_list_dto):
    return any(value is None for value in (
        booking_list_dto.description,
        booking_list_dto.location,
        booking_list_dto.time,
        booking_list_dto.interval,
        booking_list_dto.max_slots
    ))


def generate_json_booking_lists(course_code):
    booking_lists = get_booking_lists(course_code)
    json_booking_lists = [{
        "id": booking_list.id,
        "description": booking_list.description,
        "location": booking_list.location,
        "time": booking_list.time,
        "interval": booking_list.interval,
        "max_slots": booking_list.max_slots
    } for booking_list in booking_lists]

    return json_booking_lists


def remove_booking_list(course_code, booking_id):
    try:
        delete_booking_list(course_code, booking_id)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return False
    return True
