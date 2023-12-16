from flask import abort

from util import utility as util


def show_invalid_course_code_page():
    return abort(util.HTTP_404_NOT_FOUND, "This course code does not exist!")
