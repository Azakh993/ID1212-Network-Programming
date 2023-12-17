"""
Reservations Service

This module provides services related to reservations, including generating available slots, booking slots,
removing reservations, and retrieving user reservation entries.

"""

from datetime import timedelta

from models import Reservation
from repositories import booking_list_repository as blr
from repositories import reservation_repository as rr
from repositories import user_repository as ur
from util import utility as util


def generate_available_slots(booking_list_id):
    """
     Generate available slots for a booking list.

     Parameters
     ----------
     booking_list_id : int
         The ID of the booking list for which available slots are generated.

     Returns
     -------
     list of AvailableSlotDTO or None
         A list of AvailableSlotDTO objects representing available slots, or None if the booking list is not found.

     """

    booking_list = blr.retrieve_booking_list(booking_list_id)

    if booking_list is None:
        return None

    available_slots = [AvailableSlotDTO(
        booking_list_id=booking_list.id,
        start_time=_process_datetime(booking_list.time, booking_list.interval, i),
        sequence_id=i) for i in range(booking_list.max_slots)]

    reservations = rr.retrieve_reservations(booking_list.id)
    if reservations is not None:
        for reservation in reservations:
            available_slots[reservation.sequence_id].user_id = reservation.user_id
            available_slots[reservation.sequence_id].username = ur.get_user_by_user_id(reservation.user_id).username

    return available_slots


def book_slot(course_code, user_id, booking_list_id, slot_id, username):
    """
    Book a slot in a booking list.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking list.
    user_id : int
        The ID of the user booking the slot.
    booking_list_id : int
        The ID of the booking list where the slot is being booked.
    slot_id : int
        The ID of the slot to be booked.
    username : str
        The username of the user booking the slot.

    Returns
    -------
    tuple
        A tuple containing the status code and a response dictionary.

    """

    requested_slot = _get_requested_slot(booking_list_id, slot_id)
    if not _slot_is_available(requested_slot):
        return util.HTTP_422_UNPROCESSABLE_ENTITY, {"error: ": "Slot is already booked"}

    user_id = _get_user_id_for_booking(course_code, user_id, username)
    if user_id is None:
        return util.HTTP_404_NOT_FOUND, {"error: ": "User not found for booking"}

    if user_has_existing_reservations(user_id, booking_list_id):
        return util.HTTP_403_FORBIDDEN, {"error: ": "User has existing reservations"}

    reserved_slot = _reserve_slot(user_id, booking_list_id, slot_id)
    if reserved_slot is not None:
        return util.HTTP_201_CREATED, _serialized_reservation(reserved_slot)

    else:
        return util.HTTP_400_BAD_REQUEST, {"error: ": "Reservation could not be added"}


def _get_requested_slot(booking_list_id, slot_id):
    """
    Get the requested slot by ID.

    Parameters
    ----------
    booking_list_id : int
        The ID of the booking list.
    slot_id : int
        The ID of the slot.

    Returns
    -------
    AvailableSlotDTO
        The requested slot.

    """

    return generate_available_slots(booking_list_id)[int(slot_id)]


def _slot_is_available(slot):
    """
    Check if a slot is available.

    Parameters
    ----------
    slot : AvailableSlotDTO
        The slot to check.

    Returns
    -------
    bool
        True if the slot is available, False otherwise.

    """

    return slot.user_id is None


def _get_user_id_for_booking(course_code, user_id, username):
    """
    Get the user ID for booking a slot.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking list.
    user_id : int
        The ID of the user making the reservation.
    username : str
        The username of the user to be booked.

    Returns
    -------
    int or None
        The user ID if found, None otherwise.

    """

    if username:
        requested_user_is_privileged = util.get_user_privileges(course_code, user_id)
        if requested_user_is_privileged:
            valid_user = ur.get_user_by_username_and_course_code(course_code, username)
            return valid_user.id if valid_user else None
    return user_id


def serialized_available_slots(booking_list_id):
    """
    Serialize available slots for a booking list.

    Parameters
    ----------
    booking_list_id : int
        The ID of the booking list for which to serialize available slots.

    Returns
    -------
    list of dict
        A list of serialized available slots.

    """

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


def _serialized_reservation(reservation):
    """
    Serialize a reservation.

    Parameters
    ----------
    reservation : Reservation
        The reservation to serialize.

    Returns
    -------
    dict
        A serialized reservation.

    """

    return {
        "id": reservation.id,
        "list_id": reservation.list_id,
        "user_id": reservation.user_id,
        "sequence_id": reservation.sequence_id
    }


def _reserve_slot(user_id, booking_list_id, sequence_id):
    """
    Reserve a slot.

    Parameters
    ----------
    user_id : int
        The ID of the user making the reservation.
    booking_list_id : int
        The ID of the booking list.
    sequence_id : int
        The ID of the slot sequence.

    Returns
    -------
    Reservation or None
        The reserved slot if successful, None otherwise.

    """

    new_reservation = Reservation(list_id=booking_list_id, user_id=user_id, sequence_id=sequence_id)
    try:
        rr.insert_reservation(new_reservation)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return None
    return new_reservation


def generate_user_reservation_entries(course_code, user_id):
    """
    Generate user reservation entries for a course and user.

    Parameters
    ----------
    course_code : str
        The course code associated with the reservations.
    user_id : int
        The ID of the user for whom to generate reservation entries.

    Returns
    -------
    list of dict
        A list of user reservation entries.

    """

    user_reservations = rr.retrieve_user_reservations(course_code, user_id)
    if user_reservations is None:
        return None
    return [_create_reservation_entry(reservation) for reservation in user_reservations]


def _create_reservation_entry(reservation):
    """
    Create a reservation entry.

    Parameters
    ----------
    reservation : Reservation
        The reservation to create an entry for.

    Returns
    -------
    dict
        A user reservation entry.

    """

    booking_list = blr.retrieve_booking_list(reservation.list_id)
    start_time = _calculate_start_time(booking_list, reservation.sequence_id)

    return {
        "id": reservation.id,
        "start_time": _process_datetime(start_time),
        "description": booking_list.description,
        "location": booking_list.location,
        "length": booking_list.interval
    }


def _calculate_start_time(booking_list, sequence_id):

    return booking_list.time + timedelta(minutes=booking_list.interval * sequence_id)


def _process_datetime(datetime_to_process, interval=0, sequence_id=0):
    """
    Process a datetime object.

    Parameters
    ----------
    datetime_to_process : str
        The datetime string to process.
    interval : int, optional
        The interval in minutes (default is 0).
    sequence_id : int, optional
        The sequence ID (default is 0).

    Returns
    -------
    str
        The processed datetime as a formatted string.

    """

    datetime_to_process += timedelta(minutes=interval * sequence_id)
    return datetime_to_process.strftime("%Y-%m-%d %H:%M")


def remove_reservation(reservation_id):
    """
    Remove a reservation by ID.

    Parameters
    ----------
    reservation_id : int
        The ID of the reservation to remove.

    Returns
    -------
    tuple
        A tuple containing the status code and a response dictionary.

    """

    try:
        rr.delete_reservation(reservation_id)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return util.HTTP_404_NOT_FOUND, {"error": "Reservation could not be removed."}
    return util.HTTP_204_NO_CONTENT, None


def remove_slot_reservation(booking_list_id, slot_sequence_id):
    """
    Remove a slot reservation.

    Parameters
    ----------
    booking_list_id : int
        The ID of the booking list containing the slot reservation.
    slot_sequence_id : int
        The ID of the slot sequence to remove.

    Returns
    -------
    tuple
        A tuple containing the status code and a response dictionary.

    """

    try:
        rr.delete_slot_reservation(booking_list_id, slot_sequence_id)
    except Exception as exception:
        print(f'Error: {str(exception)}')
        return util.HTTP_404_NOT_FOUND, {"error": "Reservation could not be removed."}
    return util.HTTP_204_NO_CONTENT, None


def user_has_existing_reservations(user_id, booking_list_id):
    """
    Check if a user has existing reservations for a booking list.

    Parameters
    ----------
    user_id : int
        The ID of the user.
    booking_list_id : int
        The ID of the booking list.

    Returns
    -------
    bool
        True if the user has existing reservations, False otherwise.

    """

    return rr.retrieve_user_reservations_by_booking_list_id(user_id, booking_list_id)


class AvailableSlotDTO:
    """
    Data Transfer Object (DTO) for available slots.

    Represents an available slot in a booking list, providing information about the slot's availability and details.

    Attributes
    ----------
    list_id : int
        The ID of the booking list associated with the slot.
    sequence_id : int
        The sequence ID of the slot.
    start_time : datetime
        The start time of the slot.
    user_id : int or None
        The ID of the user who booked the slot (None if not booked).
    username : str or None
        The username of the user who booked the slot (None if not booked).

    """
    def __init__(self, booking_list_id, sequence_id, start_time):
        """
        Initialize an AvailableSlotDTO object.

        Parameters
        ----------
        booking_list_id : int
            The ID of the booking list associated with the slot.
        sequence_id : int
            The sequence ID of the slot.
        start_time : datetime
            The start time of the slot.

        """

        self.list_id = booking_list_id
        self.sequence_id = sequence_id
        self.start_time = start_time
        self.user_id = None
        self.username = None
