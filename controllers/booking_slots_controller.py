"""
Booking Slots Controller

This module contains controllers for managing booking slots, including
displaying available slots, booking slots, and removing reservations.

"""

from flask import request, render_template, session

from services import booking_list_service as bls
from services import reservations_service as rs
from util import utility as util


@util.validate_course_code
@util.validate_user_login
def show_booking_slots(course_code, booking_list_id):
    """
    Display available booking slots for a booking list.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking list.
    booking_list_id : int
        The ID of the booking list.

    Returns
    -------
    Response
        Rendered HTML page for GET request or a JSON response for GET JSON request.

    """

    processed_booking_list = bls.generate_processed_booking_list(booking_list_id)
    if util.json_request():
        response_data = {
            "available_slots": rs.serialized_available_slots(booking_list_id),
            "admin": util.get_user_privileges(course_code, session.get("user_id")),
            "booking_list": processed_booking_list
        }
        return util.send_response(util.HTTP_200_OK, response_data)

    return render_template("bookable_slots.html",
                           course_code=course_code, booking_list=processed_booking_list,
                           available_slots=rs.generate_available_slots(booking_list_id),
                           admin=util.get_user_privileges(course_code, session.get("user_id")))


@util.validate_course_code
@util.validate_user_login
def manage_booking_slots(course_code, booking_list_id, sequence_id):
    """
    Manage booking slots by booking or releasing slots.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking list.
    booking_list_id : int
        The ID of the booking list.
    sequence_id : int
        The ID of the booking slot sequence.

    Returns
    -------
    Response
        A JSON response containing status information.

    """

    if request.method == "POST":
        username = request.get_json().get("username")
        code, response_data = rs.book_slot(course_code, session.get("user_id"), booking_list_id, sequence_id, username)
        return util.send_response(code, response_data)

    if request.method == "DELETE":
        return remove_reservation(booking_list_id, sequence_id, course_code=course_code)


@util.validate_privileges
def remove_reservation(booking_list_id, slot_sequence_id, course_code):
    """
    Remove a reservation from a booking slot.

    Parameters
    ----------
    booking_list_id : int
        The ID of the booking list.
    slot_sequence_id : int
        The ID of the booking slot sequence.
    course_code : str
        The course code associated with the booking list.

    Returns
    -------
    Response
        A JSON response containing status information.

    """

    status_code, response_data = rs.remove_slot_reservation(booking_list_id, slot_sequence_id)
    return util.send_response(status_code, response_data)
