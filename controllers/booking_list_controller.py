from flask import render_template

from services.auth_service import get_user_privileges
from services.booking_list_service import get_booking_lists


def show_lists_page(course_code, user_id):
    booking_lists = get_booking_lists(course_code)
    user_privileges = get_user_privileges(course_code, user_id)

    return render_template("booking_lists.html",
                           course_code=course_code, booking_lists=booking_lists, user_privileges=user_privileges)
