from flask import Flask, request, session, redirect, url_for

from config.config import SECRET_KEY
from controllers.auth_controller import authenticate, show_login_page
from controllers.booking_list_controller import show_lists_page, add_new_list, delete_list
from controllers.booking_slots_controller import show_slots_page, book_slot, remove_reservation
from controllers.reservations_controller import show_reservations_page, remove_user_reservation

app = Flask(__name__)
app.secret_key = SECRET_KEY


def validate_course_code(course_code):
    return course_code is not None


def validate_user_login(course_code):
    user_id = session.get("user_id")
    if not validate_course_code(course_code):
        return False
    return user_id is not None


@app.route("/courses/<course_code>/login", methods=["GET", "POST"])
def login(course_code):
    if not validate_course_code(course_code):
        return ValueError("Course code is invalid")

    if request.method == "GET":
        return show_login_page(course_code)

    if request.method == "POST":
        json_data = request.get_json()
        username = json_data.get("username")
        password = json_data.get('password')
        return authenticate(course_code, username, password)


@app.route("/courses/<course_code>/booking-lists", methods=["GET", "POST"])
def booking_lists(course_code):
    if not validate_user_login(course_code):
        return redirect(url_for('login', course_code=course_code))

    if request.method == "GET":
        return show_lists_page(course_code, session.get("user_id"))

    if request.method == "POST":
        return add_new_list(course_code, session.get("user_id"))


@app.route("/courses/<course_code>/booking-lists/<booking_list_id>", methods=["DELETE"])
def delete_booking_list(course_code, booking_list_id):
    if not validate_user_login(course_code):
        return redirect(url_for('login', course_code=course_code))

    return delete_list(course_code, session.get("user_id"), booking_list_id)


@app.route("/courses/<course_code>/booking-lists/<booking_list_id>/bookable-slots", methods=["GET"])
def show_booking_slots(course_code, booking_list_id):
    if not validate_user_login(course_code):
        return redirect(url_for('login', course_code=course_code))

    return show_slots_page(course_code, session.get("user_id"), booking_list_id)


@app.route("/courses/<course_code>/booking-lists/<booking_list_id>/bookable-slots/<sequence_id>",
           methods=["POST", "DELETE"])
def manage_booking_slots(course_code, booking_list_id, sequence_id):
    if not validate_user_login(course_code):
        return redirect(url_for('login', course_code=course_code))

    if request.method == "POST":
        return book_slot(course_code, session.get("user_id"), booking_list_id, sequence_id)

    if request.method == "DELETE":
        return remove_reservation(course_code, session.get("user_id"), booking_list_id, sequence_id)


@app.route("/courses/<course_code>/my-bookings", methods=["GET", "DELETE"])
def user_reservations(course_code):
    if not validate_user_login(course_code):
        return redirect(url_for('login', course_code=course_code))

    if request.method == "GET":
        return show_reservations_page(course_code, session.get("user_id"))

    if request.method == "DELETE":
        reservation_id = request.get_json().get("reservation_id")
        return remove_user_reservation(reservation_id)


if __name__ == '__main__':
    app.run(debug=True)
