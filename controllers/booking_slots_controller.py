from flask import request, render_template, session

from services import booking_list_service as bls
from services import reservations_service as rs
from util import utility as util


@util.validate_course_code
@util.validate_user_login
def show_booking_slots(course_code, booking_list_id):
    processed_booking_list = bls.generate_processed_booking_list(booking_list_id)
    if util.json_request():
        response_data = {
            "available_slots": rs.serialized_available_slots(booking_list_id),
            "admin": util.get_user_privileges(course_code, session.get("user_id")),
            "booking_list": processed_booking_list
        }
        return util.send_response(util.HTTP_200_OK, response_data)

    return render_template("bookable_slots.html",
                           course_code=course_code, booking_list=processed_booking_list,
                           available_slots=rs.generate_available_slots(booking_list_id),
                           admin=util.get_user_privileges(course_code, session.get("user_id")))


@util.validate_course_code
@util.validate_user_login
def manage_booking_slots(course_code, booking_list_id, sequence_id):
    if request.method == "POST":
        username = request.get_json().get("username")
        code, response_data = rs.book_slot(course_code, session.get("user_id"), booking_list_id, sequence_id, username)
        return util.send_response(code, response_data)

    if request.method == "DELETE":
        return remove_reservation(booking_list_id, sequence_id, course_code=course_code)


@util.validate_privileges
def remove_reservation(booking_list_id, slot_sequence_id, course_code):
    status_code, response_data = rs.remove_slot_reservation(booking_list_id, slot_sequence_id)
    return util.send_response(status_code, response_data)
