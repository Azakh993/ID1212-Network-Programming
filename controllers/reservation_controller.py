from flask import request, render_template, make_response, jsonify

from controllers.controller_util import check_privileges
from services.auth_service import get_user_privileges
from services.booking_list_service import get_booking_list
from services.reservations_service import generate_available_slots, generate_json_ready_available_slots, reserve_slot, \
    generate_json_ready_reservation


def show_slots_page(course_code, user_id, booking_id):
    user_privileges = get_user_privileges(course_code, user_id)
    booking_list = get_booking_list(booking_id)
    available_slots = generate_available_slots(booking_list)

    json_request = request.headers.get('Accept') == 'application/json'
    if json_request:
        processed_booking_list = generate_json_ready_available_slots(booking_list)
        processed_available_slots = generate_json_ready_available_slots(booking_list)
        response_data = {
            "available_slots": processed_available_slots,
            "admin": user_privileges,
            "booking_list": processed_booking_list
        }
        return make_response(jsonify(response_data), 200)

    return render_slots_page(course_code, booking_list, user_privileges, available_slots)


def render_slots_page(course_code, booking_list, user_privileges, available_slots):
    return render_template("bookable_slots.html",
                           course_code=course_code,
                           booking_list=booking_list,
                           admin=user_privileges,
                           available_slots=available_slots)


def book_slot(course_code, user_id, booking_id, slot_id):
    check_privileges(course_code, user_id)

    booking_list = get_booking_list(booking_id)
    available_slots = generate_available_slots(booking_list)
    requested_slot_to_book = available_slots[int(slot_id)]

    if requested_slot_to_book.user_id is not None:
        return make_response(jsonify({"error": "Slot is already booked"}), 400)

    added_reservation = reserve_slot(user_id, booking_id, slot_id)

    if added_reservation:
        json_ready_reservation = generate_json_ready_reservation(added_reservation)
        response_data = {"newReservation": json_ready_reservation}
        return make_response(jsonify(response_data), 201)
    else:
        return send_error_response()


def send_error_response():
    error_message = {"error": "Reservation could not be added."}
    return make_response(jsonify(error_message), 400)
