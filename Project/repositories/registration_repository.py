"""
User Course Registration Repository

This module contains functions for retrieving user course registration information from the database.

"""

from models.registration import UserCourseRegistration
from repositories import session
from repositories.repository_util import get_first_from_database, add_to_database


def retrieve_user_privileges(course_code, user_id):
    """
    Retrieve the user privileges for a specific user in a course.

    Parameters
    ----------
    course_code : str
        The course code for which user privileges are to be retrieved.
    user_id : int
        The user ID for which privileges are to be retrieved.

    Returns
    -------
    bool
        True if the user has admin privileges in the course, False otherwise.

    """

    enrollment_entry = get_first_from_database((session.query(UserCourseRegistration)
                                                .filter(UserCourseRegistration.course_id == course_code.upper(),
                                                        UserCourseRegistration.user_id == user_id)))
    user_privileges = enrollment_entry.admin
    return user_privileges


def insert_enrollment_entry(enrolled_entry):
    """
    Insert an enrollment entry into the database.

    Parameters
    ----------
    enrolled_entry : UserCourseRegistration
        The UserCourseRegistration object representing the enrollment entry to be inserted.

    """

    add_to_database(enrolled_entry)
