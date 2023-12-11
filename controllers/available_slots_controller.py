from flask import request, render_template, make_response, jsonify

from services.auth_service import get_user_privileges
from services.available_slots_service import generate_available_slots, generate_json_ready_available_slots
from services.booking_list_service import get_booking_list


def show_slots_page(course_code, user_id, booking_id):
    user_privileges = get_user_privileges(course_code, user_id)
    booking_list = get_booking_list(booking_id)
    available_slots = generate_available_slots(booking_list)

    json_request = request.headers.get('Content-Type') == 'application/json'
    if json_request:
        processed_booking_list = generate_json_ready_available_slots(booking_list)
        processed_available_slots = generate_json_ready_available_slots(booking_list)
        response_data = {"available_slots": processed_available_slots,
                         "admin": user_privileges, "booking_list": processed_booking_list}
        return make_response(jsonify(response_data), 200)

    return render_template("bookable_slots.html",
                           course_code=course_code, booking_list=booking_list, admin=user_privileges,
                           available_slots=available_slots)
