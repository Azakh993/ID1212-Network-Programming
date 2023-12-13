from flask import request, render_template, make_response, jsonify, session

from controllers.controller_util import check_privileges, validate_course_code, validate_user_login
from services.auth_service import get_user_privileges, retrieve_user_by_username
from services.booking_list_service import get_booking_list
from services.reservations_service import generate_available_slots, generate_json_ready_available_slots, reserve_slot, \
    generate_json_ready_reservation, remove_slot_reservation, get_user_reservations_for_booking_list


@validate_course_code
@validate_user_login
def show_booking_slots(course_code, booking_list_id):
    user_privileges = get_user_privileges(course_code, session.get("user_id"))
    booking_list = get_booking_list(booking_list_id)
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


@validate_course_code
@validate_user_login
def manage_booking_slots(course_code, booking_list_id, sequence_id):
    if request.method == "POST":
        return book_slot(course_code, session.get("user_id"), booking_list_id, sequence_id)

    if request.method == "DELETE":
        return remove_reservation(course_code, session.get("user_id"), booking_list_id, sequence_id)


def render_slots_page(course_code, booking_list, user_privileges, available_slots):
    return render_template("bookable_slots.html",
                           course_code=course_code,
                           booking_list=booking_list,
                           admin=user_privileges,
                           available_slots=available_slots)


def book_slot(course_code, user_id, booking_id, slot_id):
    existing_reservations = check_for_other_reservations(user_id, booking_id)
    if existing_reservations:
        return send_error_response(403)

    booking_list = get_booking_list(booking_id)
    available_slots = generate_available_slots(booking_list)
    requested_slot_to_book = available_slots[int(slot_id)]

    if requested_slot_to_book.user_id is not None:
        return make_response(jsonify({"error": "Slot is already booked"}), 400)

    user_to_book = request.get_json().get("username")
    if user_to_book:
        check_privileges(course_code, user_id)
        valid_user = retrieve_user_by_username(course_code, user_to_book)
        if valid_user:
            user_id = valid_user.id
        else:
            return send_error_response(404)

    added_reservation = reserve_slot(user_id, booking_id, slot_id)

    if added_reservation:
        json_ready_reservation = generate_json_ready_reservation(added_reservation)
        response_data = {"newReservation": json_ready_reservation}
        return make_response(jsonify(response_data), 201)
    else:
        return send_error_response(400)


def check_for_other_reservations(user_id, booking_list_id):
    return get_user_reservations_for_booking_list(user_id, booking_list_id)


def remove_reservation(course_code, user_id, booking_list_id, slot_sequence_id):
    check_privileges(course_code, user_id)
    successful_removal = remove_slot_reservation(booking_list_id, slot_sequence_id)

    if successful_removal:
        return make_response(jsonify({}), 204)
    else:
        return send_error_response(404)


def send_error_response(status_code):
    error_message = {"error": "Reservation could not be added."}
    return make_response(jsonify(error_message), status_code)
