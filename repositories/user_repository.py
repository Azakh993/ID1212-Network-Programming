"""
User Repository

This module contains functions for retrieving and managing user-related data in the database.

"""

from models.registration import UserCourseRegistration
from models.user import User
from repositories import session
from repositories.repository_util import get_first_from_database, add_to_database


def get_user_by_username(username):
    """
    Retrieve a user by their username.

    Parameters
    ----------
    username : str
        The username of the user to retrieve.

    Returns
    -------
    User or None
        The User object for the specified username or None if not found.

    """

    return get_first_from_database(session.query(User).filter(User.username == username))


def get_user_by_username_and_course_code(course_code, username):
    """
    Retrieve a user by their username and course code.

    Parameters
    ----------
    course_code : str
        The course code for which the user is registered.
    username : str
        The username of the user to retrieve.

    Returns
    -------
    User or None
        The User object for the specified username and course code or None if not found.

    """

    return get_first_from_database(((session.query(User)
                                     .join(UserCourseRegistration))
                                    .filter(UserCourseRegistration.course_id == course_code.upper(),
                                            User.username == username)))


def get_user_by_user_id(user_id):
    """
    Retrieve a user by their user ID.

    Parameters
    ----------
    user_id : int
        The ID of the user to retrieve.

    Returns
    -------
    User or None
        The User object for the specified user ID or None if not found.

    """

    return get_first_from_database(session.query(User).filter_by(id=user_id))


def insert_users(user_to_add):
    """
    Insert a new user into the database.

    Parameters
    ----------
    user_to_add : User
        The User object to be inserted into the database.

    """

    add_to_database(user_to_add)
