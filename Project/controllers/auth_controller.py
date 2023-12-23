"""
Authentication Controller

This module contains functions for user authentication and user management related to course enrollment.

"""

from flask import render_template, session, request
from werkzeug.security import generate_password_hash

from services import auth_service as auth
from util import utility as util


@util.validate_course_code
def login(course_code):
    """
    Render the login page or handle a login request.

    Parameters
    ----------
    course_code : str
        The course code associated with the login.

    Returns
    -------
    Response
        Rendered HTML page for GET request or a response status for POST request.

    """

    if request.method == "GET":
        return render_template("login.html", course_code=course_code)
    elif request.method == "POST":
        return _handle_login_request(course_code)


@util.validate_course_code
@util.validate_user_login
@util.validate_privileges
def add_users(course_code):
    """
    Render the 'add_users' page or handle user addition and enrollment.

    Parameters
    ----------
    course_code : str
        The course code associated with the user addition.

    Returns
    -------
    Response
        Rendered HTML page for GET request or a response status for POST request.

    """

    if request.method == "GET":
        return render_template("add_users.html", course_code=course_code)
    elif request.method == "POST":
        json_data = request.get_json()
        return _add_and_enroll_users(json_data, course_code=course_code)


def _add_and_enroll_users(json_data, course_code):
    """
    Add and enroll users in the specified course.

    Parameters
    ----------
    json_data : dict
        JSON data containing user information.
    course_code : str
        The course code associated with the user addition.

    Returns
    -------
    Response
        A response status indicating the success or failure of the user addition.

    """

    user_addition_dto = UserAdditionDTO(json_data)
    responses = auth.insert_new_users_and_enrollments(course_code, user_addition_dto)
    return util.send_response(util.HTTP_207_MULTI_STATUS, responses)


def logout(course_code):
    """
    Log the user out and render the login page.

    Parameters
    ----------
    course_code : str
        The course code associated with the logout.

    Returns
    -------
    Response
        Rendered HTML login page.

    """

    session.clear()
    return render_template("login.html", course_code=course_code)


def _handle_login_request(course_code):
    """
    Handle a login request and authenticate the user.

    Parameters
    ----------
    course_code : str
        The course code associated with the login.

    Returns
    -------
    Response
        A response status indicating the success or failure of the login attempt.

    """

    json_data = request.get_json()
    user = auth.authenticate(course_code, json_data.get("username"), json_data.get('password'))

    if user:
        session['user_id'] = user.id
        return util.send_response(util.HTTP_204_NO_CONTENT)
    return util.send_response(util.HTTP_401_UNAUTHORIZED, {"error": "Unauthorized!"})


class UserAdditionDTO:
    """
    Data Transfer Object (DTO) for user addition.

    Attributes
    ----------
    usernames : list of str
        List of usernames to be added.
    hashed_password : str
        Hashed password for the users.
    elevated_privileges : bool
        Flag indicating elevated privileges for the users.

    """

    def __init__(self, json_data):
        """
         Initialize UserAdditionDTO with JSON data.

         Parameters
         ----------
         json_data : dict
             JSON data containing user information.

         """

        self.usernames = json_data.get("usernames").split(",")
        self.hashed_password = generate_password_hash(json_data.get("password"))
        self.elevated_privileges = json_data.get("elevated_privileges") == 'true'
