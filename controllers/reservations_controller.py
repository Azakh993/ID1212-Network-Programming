from flask import request, render_template, make_response, jsonify

from services.reservations_service import generate_user_reservation_entries, remove_reservation


def show_reservations_page(course_code, user_id):
    user_reservation_entries = generate_user_reservation_entries(course_code, user_id)

    json_request = request.headers.get('Accept') == 'application/json'
    if json_request:
        response_data = {"reservations": user_reservation_entries}
        return make_response(jsonify(response_data), 200)

    return render_template("my_bookings.html", reservations=user_reservation_entries, course_code=course_code)


def remove_user_reservation(reservation_id):
    successful_removal = remove_reservation(reservation_id)

    if successful_removal:
        return make_response(jsonify({}), 204)

    return make_response(jsonify({"error": "Reservation not found"}), 404)
