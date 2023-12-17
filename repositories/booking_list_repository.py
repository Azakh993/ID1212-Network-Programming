"""
Booking List Repository

This module contains functions and utilities for interacting with the database and managing booking lists.
It provides functions for retrieving booking lists, inserting new booking lists,
and deleting booking lists from the database.

"""

from models.booking_list import BookingList
from repositories import session
from repositories.repository_util import get_all_from_database, get_first_from_database, add_to_database, \
    delete_from_database


def retrieve_booking_lists(course_code):
    """
    Retrieve a list of booking lists for a specific course code.

    Parameters
    ----------
    course_code : str
        The course code for which booking lists are to be retrieved.

    Returns
    -------
    list of BookingList objects
        A list of booking lists associated with the specified course code.

    """

    return get_all_from_database(session.query(BookingList)
                                 .filter(BookingList.course_id == course_code.upper()))


def retrieve_booking_list(booking_list_id):
    """
    Retrieve a booking list by its ID.

    Parameters
    ----------
    booking_list_id : int
        The ID of the booking list to retrieve.

    Returns
    -------
    BookingList or None
        The booking list with the specified ID or None if not found.

    """

    return get_first_from_database(session.query(BookingList)
                                   .filter(BookingList.id == booking_list_id))


def insert_booking_list(new_booking_list):
    """
    Insert a new booking list into the database.

    Parameters
    ----------
    new_booking_list : BookingList
        The BookingList object to be inserted into the database.

    """

    add_to_database(new_booking_list)


def delete_booking_list(course_code, booking_id):
    """
    Delete a booking list from the database.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking list.
    booking_id : int
        The ID of the booking list to be deleted.

    """

    delete_from_database(session.query(BookingList)
                         .filter(BookingList.course_id == course_code.upper(), BookingList.id == booking_id))
