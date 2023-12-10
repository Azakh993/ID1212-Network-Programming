from flask import render_template, request, jsonify, make_response

from services.auth_service import get_user_privileges
from services.booking_list_service import get_booking_lists, add_booking_list, is_invalid_booking_list, \
    generate_json_booking_lists


def show_lists_page(course_code, user_id):
    booking_lists = get_booking_lists(course_code)
    user_privileges = get_user_privileges(course_code, user_id)

    return render_template("booking_lists.html",
                           course_code=course_code, booking_lists=booking_lists, user_privileges=user_privileges)


def add_new_list(course_code, user_id):
    admin_privileges = get_user_privileges(course_code, user_id)

    if not admin_privileges:
        raise Exception("User is not an admin!")

    json_data = request.get_json()
    booking_list_dto = BookingListDTO(json_data, course_code)

    if is_invalid_booking_list(booking_list_dto):
        error_message = jsonify({"error": "Invalid booking data"})
        return make_response(error_message, 400)

    successful_entry = add_booking_list(course_code, booking_list_dto)

    if successful_entry:
        updated_booking_lists = generate_json_booking_lists(course_code)
        response_data = {"success": "Booking list added successfully", "booking_lists": updated_booking_lists}
        return make_response(jsonify(response_data), 201)


class BookingListDTO:
    def __init__(self, json_data, course_code):
        self.course_id = course_code
        self.description = json_data["description"]
        self.location = json_data.get("location")
        self.time = json_data.get("time")
        self.interval = json_data.get("length")
        self.max_slots = json_data.get("slots")
