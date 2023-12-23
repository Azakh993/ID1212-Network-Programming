"""
Booking List Controller

This module contains controllers for managing booking lists, including adding, removing, and retrieving booking lists.

"""

from flask import render_template, request, session

from services import booking_list_service as bls
from util import utility as util


@util.validate_course_code
@util.validate_user_login
def manage_booking_lists(course_code):
    """
    Render the 'booking_lists' page or handle booking list management, including adding and removing booking lists.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking lists.

    Returns
    -------
    Response
        Rendered HTML page for GET request or a JSON response for GET JSON request.
        A response for POST request.

    """

    if request.method == "GET":
        if util.json_request():
            return util.send_response(util.HTTP_200_OK, {"booking_lists": bls.get_booking_lists(course_code)})

        return render_template("booking_lists.html",
                               course_code=course_code,
                               booking_lists=bls.get_booking_lists(course_code),
                               user_privileges=util.get_user_privileges(course_code, session.get("user_id")))

    if request.method == "POST":
        return add_new_list(course_code=course_code, user_id=session.get("user_id"))


@util.validate_course_code
@util.validate_user_login
@util.validate_privileges
def remove_booking_list(course_code, booking_list_id):
    """
    Remove a booking list.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking list.
    booking_list_id : int
        The ID of the booking list to be removed.

    Returns
    -------
    Response
        A JSON response containing status information.

    """

    return util.send_response(*bls.erase_booking_list(course_code, booking_list_id))


@util.validate_privileges
def add_new_list(course_code, user_id):
    """
    Add a new booking list.

    Parameters
    ----------
    course_code : str
        The course code associated with the booking list.
    user_id : int
        The user ID associated with the booking list creator.

    Returns
    -------
    Response
        A JSON response containing status information.

    """

    json_data = request.get_json()
    booking_list_dto = BookingListDTO(json_data, course_code, user_id)
    status_code, response_data = bls.add_booking_list(course_code, booking_list_dto)
    return util.send_response(status_code, response_data)


class BookingListDTO:
    """
    Data Transfer Object (DTO) for booking list creation.

    Attributes
    ----------
    course_id : str
        The course code associated with the booking list.
    user_id : int
        The user ID associated with the booking list creator.
    description : str
        Description of the booking list.
    location : str, optional
        Location of the booking list (default is None).
    time : str, optional
        Time information for the booking list (default is None).
    interval : int, optional
        Length of the booking list interval (default is None).
    max_slots : int, optional
        Maximum number of slots for the booking list (default is None).

    """

    def __init__(self, json_data, course_code, user_id):
        """
        Initialize BookingListDTO with JSON data.

        Parameters
        ----------
        json_data : dict
            JSON data containing booking list information.
        course_code : str
            The course code associated with the booking list.
        user_id : int
            The user ID associated with the booking list creator.

        """

        self.course_id = course_code
        self.user_id = user_id
        self.description = json_data["description"]
        self.location = json_data.get("location")
        self.time = json_data.get("time")
        self.interval = json_data.get("length")
        self.max_slots = json_data.get("slots")
