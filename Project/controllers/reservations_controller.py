"""
Reservations Controller

This module contains controllers for managing user reservations, including
displaying user reservations and removing user reservations.

"""

from flask import request, render_template, session

from services import reservations_service as rs
from util import utility as util


@util.validate_course_code
@util.validate_user_login
def user_reservations(course_code):
    """
    Display user reservations or remove a user reservation.

    Parameters
    ----------
    course_code : str
        The course code associated with the user reservations.

    Returns
    -------
    Response
        Rendered HTML page for GET request, a JSON response for GET JSON request,
        or a JSON response for DELETE request.

    """

    if request.method == "GET":
        return _show_reservations_page(course_code, session.get("user_id"))

    if request.method == "DELETE":
        return _remove_user_reservation(request.get_json().get("reservation_id"))


def _show_reservations_page(course_code, user_id):
    """
    Show the user reservations page or return user reservation data.

    Parameters
    ----------
    course_code : str
        The course code associated with the user reservations.
    user_id : int
        The user ID associated with the user reservations.

    Returns
    -------
    Response
        Rendered HTML page for GET request or a JSON response for GET JSON request.

    """

    user_reservation_entries = rs.generate_user_reservation_entries(course_code, user_id)

    if util.json_request():
        return util.send_response(util.HTTP_200_OK, {"reservations": user_reservation_entries})

    return render_template("my_bookings.html", reservations=user_reservation_entries, course_code=course_code)


def _remove_user_reservation(reservation_id):
    """
    Remove a user reservation.

    Parameters
    ----------
    reservation_id : int
        The ID of the reservation to be removed.

    Returns
    -------
    Response
        A JSON response containing status information.

    """

    return util.send_response(*rs.remove_reservation(reservation_id))
