"""
Error Page Controller

This module contains a controller for handling invalid course codes.

"""

from flask import abort

from util import utility as util


def show_invalid_course_code_page():
    """
        Show an error page for an invalid course code.

        Returns
        -------
        Aborter
            This function raises a 404 Not Found error with a corresponding message.

        """

    return abort(util.HTTP_404_NOT_FOUND, "This course code does not exist!")
