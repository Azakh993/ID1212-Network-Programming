from flask import Flask, request, session, redirect, url_for

from config.config import SECRET_KEY
from controllers.auth_controller import authenticate, show_login_page
from controllers.available_slots_controller import show_slots_page
from controllers.booking_list_controller import show_lists_page, add_new_list, delete_list

app = Flask(__name__)
app.secret_key = SECRET_KEY


def course_code_is_valid(course_code):
    return course_code is not None


def login_is_invalid(user_id):
    return user_id is None


@app.route("/courses/<course_code>/login", methods=["GET", "POST"])
def login_page(course_code):
    if course_code_is_valid(course_code):
        if request.method == "GET":
            return show_login_page(course_code)
        elif request.method == "POST":
            json_data = request.get_json()
            username = json_data.get("username")
            password = json_data.get('password')
            return authenticate(course_code, username, password)
    else:
        return ValueError("Course code is invalid")


@app.route("/courses/<course_code>/booking-lists", methods=["GET", "PUT", "DELETE"])
def list_page(course_code):
    user_id = session.get("user_id")
    if course_code_is_valid(course_code):
        if login_is_invalid(user_id):
            return redirect(url_for('login_page', course_code=course_code))

        if request.method == "GET":
            return show_lists_page(course_code, user_id)

        if request.method == "PUT":
            return add_new_list(course_code, user_id)

        if request.method == "DELETE":
            return delete_list(course_code, user_id)


@app.route("/courses/<course_code>/booking-lists/<booking_list_id>/bookable-slots", methods=["GET", "POST"])
def slots_page(course_code, booking_list_id):
    user_id = session.get("user_id")
    if course_code_is_valid(course_code):
        if login_is_invalid(user_id):
            return redirect(url_for('login_page', course_code=course_code))

        if request.method == "GET":
            return show_slots_page(course_code, user_id, booking_list_id)

        if request.method == "POST":
            return show_slots_page(course_code, user_id, booking_list_id)


if __name__ == '__main__':
    app.run(debug=True)
