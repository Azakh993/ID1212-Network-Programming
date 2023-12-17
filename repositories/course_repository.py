"""
Course Repository

This module contains functions for retrieving course information from the database.

"""

from models import Course
from repositories import session
from repositories.repository_util import get_first_from_database


def get_course_by_code(course_code):
    """
    Retrieve a course by its course code.

    Parameters
    ----------
    course_code : str
        The course code for which course information is to be retrieved.

    Returns
    -------
    Course or None
        The Course object with the specified course code or None if not found.

    """

    return get_first_from_database(session.query(Course).filter_by(id=course_code))
