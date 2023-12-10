import copy

from repositories.booking_list_repository import retrieve_booking_lists


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
