from flask import render_template, session, request
from werkzeug.security import generate_password_hash

from services import auth_service as auth
from util import utility as util


@util.validate_course_code
def login(course_code):
    if request.method == "GET":
        return render_template("login.html", course_code=course_code)
    elif request.method == "POST":
        return _handle_login_request(course_code)


@util.validate_course_code
@util.validate_user_login
@util.validate_privileges
def add_users(course_code):
    if request.method == "GET":
        return render_template("add_users.html", course_code=course_code)
    elif request.method == "POST":
        json_data = request.get_json()
        return _add_and_enroll_users(json_data, course_code=course_code)


def _add_and_enroll_users(json_data, course_code):
    user_addition_dto = UserAdditionDTO(json_data)
    responses = auth.insert_new_users_and_enrollments(course_code, user_addition_dto)
    return util.send_response(util.HTTP_207_MULTI_STATUS, responses)


def logout(course_code):
    session.clear()
    return render_template("login.html", course_code=course_code)


def _handle_login_request(course_code):
    json_data = request.get_json()
    user = auth.authenticate(course_code, json_data.get("username"), json_data.get('password'))

    if user:
        session['user_id'] = user.id
        return util.send_response(util.HTTP_204_NO_CONTENT)
    return util.send_response(util.HTTP_401_UNAUTHORIZED, {"error": "Unauthorized!"})


class UserAdditionDTO:
    def __init__(self, json_data):
        self.usernames = json_data.get("usernames").split(",")
        self.hashed_password = generate_password_hash(json_data.get("password"))
        self.elevated_privileges = json_data.get("elevated_privileges") == 'true'
