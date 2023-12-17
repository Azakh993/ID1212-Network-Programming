from flask import request, render_template, session

from services import reservations_service as rs
from util import utility as util


@util.validate_course_code
@util.validate_user_login
def user_reservations(course_code):
    if request.method == "GET":
        return _show_reservations_page(course_code, session.get("user_id"))

    if request.method == "DELETE":
        return _remove_user_reservation(request.get_json().get("reservation_id"))


def _show_reservations_page(course_code, user_id):
    user_reservation_entries = rs.generate_user_reservation_entries(course_code, user_id)

    if util.json_request():
        return util.send_response(util.HTTP_200_OK, {"reservations": user_reservation_entries})

    return render_template("my_bookings.html", reservations=user_reservation_entries, course_code=course_code)


def _remove_user_reservation(reservation_id):
    return util.send_response(*rs.remove_reservation(reservation_id))
