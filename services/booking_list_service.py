"""
Booking List Service

This module provides services related to booking lists, including retrieving, adding, and processing booking lists.

"""

import copy

from sqlalchemy.exc import IntegrityError

from models.booking_list import BookingList
from repositories import booking_list_repository as blr
from repositories import reservation_repository as rr
from util import utility as util


def get_booking_lists(course_code=None, booking_list_id=None):
    """
    Retrieve booking lists.

    If `course_code` is provided, this function returns a list of processed booking lists for the given course.
    If `booking_list_id` is provided, it returns a processed booking list for the specified ID.

    Parameters
    ----------
    course_code : str, optional
        The course code associated with the booking lists (default is None).
    booking_list_id : int, optional
        The ID of the booking list to retrieve (default is None).

    Returns
    -------
    list or dict
        A list of processed booking lists for a course or a processed booking list for a specific ID.

    """

    if course_code:
        booking_lists = blr.retrieve_booking_lists(course_code)
        if booking_lists is not None:
            return _processed_booking_lists(booking_lists)

    elif booking_list_id:
        booking_list = blr.retrieve_booking_list(booking_list_id)
        if booking_list is not None:
            return _processed_booking_lists(booking_list)[0]

    return []


def erase_booking_list(course_code, booking_list_id):
    """
    Erase a booking list.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking list.
    booking_list_id : int
        The ID of the booking list to erase.

    Returns
    -------
    tuple
        A tuple containing the status code and a response dictionary.

    """

    try:
        blr.delete_booking_list(course_code, booking_list_id)
    except IntegrityError:
        return util.HTTP_422_UNPROCESSABLE_ENTITY, {"error": "Booking list is in use"}
    return util.HTTP_410_GONE, {'bookingListId': booking_list_id}


def add_booking_list(course_code, booking_list_dto):
    """
    Add a new booking list.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking list.
    booking_list_dto : BookingListDTO
        Data Transfer Object (DTO) containing booking list information.

    Returns
    -------
    tuple
        A tuple containing the status code and a response dictionary.

    """

    if _invalid_booking_list(booking_list_dto):
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

    return util.HTTP_201_CREATED, {'newBookingList': _serialized_booking_list(new_booking_list)}


def generate_processed_booking_list(booking_list_id):
    """
    Generate a processed booking list for the given ID.

    Parameters
    ----------
    booking_list_id : int
        The ID of the booking list to process.

    Returns
    -------
    dict or None
        A processed booking list dictionary or None if not found.

    """

    booking_list = blr.retrieve_booking_list(booking_list_id)
    if booking_list is not None:
        return _processed_booking_lists([booking_list])[0]
    return None


def _processed_booking_lists(booking_lists):
    """
    Process a list of booking lists.

    Parameters
    ----------
    booking_lists : list of BookingList
        A list of booking list objects to process.

    Returns
    -------
    list of dict
        A list of processed booking list dictionaries.

    """

    booking_lists_without_seconds = []
    for booking_list in booking_lists:
        list_copy = copy.deepcopy(booking_list)
        list_copy.time = booking_list.time
        list_copy.max_slots = _calculate_available_slots(list_copy)
        booking_lists_without_seconds.append(_serialized_booking_list(list_copy))
    return booking_lists_without_seconds


def _calculate_available_slots(booking_list):
    """
    Calculate the number of available slots in a booking list.

    Parameters
    ----------
    booking_list : BookingList
        The booking list object to calculate available slots for.

    Returns
    -------
    int
        The number of available slots.

    """

    reservations = rr.retrieve_reservations(booking_list.id)
    return booking_list.max_slots - len(reservations)


def _serialized_booking_list(booking_list):
    """
    Serialize a booking list object into a dictionary.

    Parameters
    ----------
    booking_list : BookingList
        The booking list object to serialize.

    Returns
    -------
    dict
        A dictionary representing the serialized booking list.

    """

    json_booking_list = {
        "id": booking_list.id,
        "description": booking_list.description,
        "location": booking_list.location,
        "time": booking_list.time.strftime('%Y-%m-%d %H:%M'),
        "interval": booking_list.interval,
        "available_slots": booking_list.max_slots
    }
    return json_booking_list


def _invalid_booking_list(booking_list_dto):
    """
    Check if a booking list DTO is invalid.

    Parameters
    ----------
    booking_list_dto : BookingListDTO
        Data Transfer Object (DTO) containing booking list information.

    Returns
    -------
    bool
        True if the booking list DTO is invalid, False otherwise.

    """

    return any(value is None for value in (
        booking_list_dto.description,
        booking_list_dto.location,
        booking_list_dto.time,
        booking_list_dto.interval,
        booking_list_dto.max_slots
    ))
