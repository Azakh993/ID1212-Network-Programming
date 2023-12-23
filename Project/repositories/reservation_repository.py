"""
Reservation Repository

This module contains functions for retrieving and managing reservations in the database.

"""

from models import BookingList
from models.reservation import Reservation
from repositories import session
from repositories.repository_util import get_all_from_database, add_to_database, delete_from_database, \
    get_first_from_database


def retrieve_reservations(booking_list_id):
    """
    Retrieve reservations associated with a booking list.

    Parameters
    ----------
    booking_list_id : int
        The ID of the booking list for which reservations are to be retrieved.

    Returns
    -------
    list of Reservation objects
        A list of reservations associated with the specified booking list.

    """

    return get_all_from_database(session.query(Reservation)
                                 .filter_by(list_id=booking_list_id))


def insert_reservation(new_reservation):
    """
    Insert a new reservation into the database.

    Parameters
    ----------
    new_reservation : Reservation
        The Reservation object to be inserted into the database.

    """

    add_to_database(new_reservation)


def retrieve_user_reservations(course_code, user_id):
    """
    Retrieve reservations for a user in a specific course.

    Parameters
    ----------
    course_code : str
        The course code for which reservations are to be retrieved.
    user_id : int
        The user ID for which reservations are to be retrieved.

    Returns
    -------
    list of Reservation objects
        A list of reservations for the specified user in the specified course.

    """

    return get_all_from_database(((session.query(Reservation)
                                   .join(BookingList, BookingList.id == Reservation.list_id))
                                  .filter(BookingList.course_id == course_code.upper(),
                                          Reservation.user_id == user_id)))


def retrieve_user_reservations_by_booking_list_id(user_id, booking_list_id):
    """
    Retrieve reservations for a user in a specific booking list.

    Parameters
    ----------
    user_id : int
        The user ID for which reservations are to be retrieved.
    booking_list_id : int
        The ID of the booking list for which reservations are to be retrieved.

    Returns
    -------
    Reservation or None
        The Reservation object for the specified user and booking list or None if not found.

    """

    return get_first_from_database(
        session.query(Reservation).filter(Reservation.user_id == user_id, Reservation.list_id == booking_list_id))


def delete_reservation(reservation_id):
    """
    Delete a reservation from the database.

    Parameters
    ----------
    reservation_id : int
        The ID of the reservation to be deleted.

    """

    delete_from_database(session.query(Reservation).filter(Reservation.id == reservation_id))


def delete_slot_reservation(booking_list_id, slot_sequence_id):
    """
    Delete a slot reservation from the database.

    Parameters
    ----------
    booking_list_id : int
        The ID of the booking list associated with the slot reservation.
    slot_sequence_id : int
        The sequence ID of the slot reservation to be deleted.

    """

    delete_from_database(session.query(Reservation)
                         .filter(Reservation.list_id == booking_list_id,
                                 Reservation.sequence_id == slot_sequence_id))
