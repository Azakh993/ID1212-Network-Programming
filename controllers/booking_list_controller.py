from flask import render_template, request, jsonify, make_response, session
from sqlalchemy.exc import IntegrityError

from controllers.controller_util import check_privileges, validate_course_code, validate_user_login
from services.auth_service import get_user_privileges
from services.booking_list_service import get_booking_lists, add_booking_list, is_invalid_booking_list, \
    generate_json_ready_booking_lists, remove_booking_list, generate_json_ready_booking_list


@validate_course_code
@validate_user_login
def manage_booking_lists(course_code):
    if request.method == "GET":
        return show_lists_page(course_code, session.get("user_id"))

    if request.method == "POST":
        return add_new_list(course_code, session.get("user_id"))


@validate_course_code
@validate_user_login
def delete_booking_list(course_code, booking_list_id):
    check_privileges(course_code, session.get("user_id"))
    try:
        successful_removal = remove_booking_list(course_code, booking_list_id)

        if successful_removal:
            return make_response(jsonify({}), 204)
        else:
            return send_error_response(400)

    except IntegrityError:
        return send_error_response(422)


def show_lists_page(course_code, user_id):
    booking_lists = get_booking_lists(course_code)
    user_privileges = get_user_privileges(course_code, user_id)

    json_request = request.headers.get('Accept') == 'application/json'
    if json_request:
        return send_success_response(course_code, 200)

    return render_template("booking_lists.html",
                           course_code=course_code, booking_lists=booking_lists, user_privileges=user_privileges)


def add_new_list(course_code, user_id):
    check_privileges(course_code, user_id)

    json_data = request.get_json()
    booking_list_dto = BookingListDTO(json_data, course_code)

    if is_invalid_booking_list(booking_list_dto):
        error_message = jsonify({"error": "Invalid booking data"})
        return make_response(error_message, 400)

    added_booking_list = add_booking_list(course_code, booking_list_dto)

    if added_booking_list:
        json_ready_booking_list = generate_json_ready_booking_list(added_booking_list)
        response_data = {"newBookingList": json_ready_booking_list}
        return make_response(jsonify(response_data), 201)
    else:
        return send_error_response(400)


def send_success_response(course_code, status_code):
    updated_booking_lists = generate_json_ready_booking_lists(course_code)
    response_data = {"booking_lists": updated_booking_lists}
    return make_response(jsonify(response_data), status_code)


def send_error_response(status_code):
    error_message = {"error": "Invalid booking data"}
    return make_response(jsonify(error_message), status_code)


class BookingListDTO:
    def __init__(self, json_data, course_code):
        self.course_id = course_code
        self.description = json_data["description"]
        self.location = json_data.get("location")
        self.time = json_data.get("time")
        self.interval = json_data.get("length")
        self.max_slots = json_data.get("slots")
