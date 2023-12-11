from flask import Flask, request, jsonify, make_response, session, redirect, url_for

from config.config import SECRET_KEY
from controllers.auth_controller import authenticate, show_login_page
from controllers.booking_list_controller import show_lists_page, add_new_list, delete_list

app = Flask(__name__)
app.secret_key = SECRET_KEY


def course_code_is_valid(course_code):
    if course_code is None:
        error_response = jsonify({"error": "Invalid course code!"})
        return make_response(error_response, 404)
    return True


def login_is_invalid(user_id):
    return user_id is None


@app.route("/<course_code>/login", methods=["GET", "POST"])
def login_page(course_code):
    if course_code_is_valid(course_code):
        if request.method == "GET":
            return show_login_page(course_code)
        elif request.method == "POST":
            username = request.form['username']
            password = request.form['password']
            return authenticate(course_code, username, password)


@app.route("/<course_code>/booking-lists", methods=["GET", "POST", "PUT", "DELETE"])
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


if __name__ == '__main__':
    app.run(debug=True)
