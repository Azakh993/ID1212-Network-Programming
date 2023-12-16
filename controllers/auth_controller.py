from flask import render_template, session, request

from services import auth_service as auth
from util import utility as util


@util.validate_course_code
def login(course_code):
    if request.method == "GET":
        return render_template("login.html", course_code=course_code)
    elif request.method == "POST":
        return handle_login_request(course_code)


@util.validate_course_code
@util.validate_user_login
@util.validate_privileges
def add_users(course_code):
    if request.method == "GET":
        return render_template("add_users.html", course_code=course_code)
    elif request.method == "POST":
        json_data = request.get_json()
        return add_and_enroll_users(course_code, json_data)


@util.validate_privileges
def add_and_enroll_users(course_code, json_data):
    user_addition_dto = UserAdditionDTO(json_data)
    responses = auth.insert_new_users_and_enrollments(course_code, user_addition_dto)
    return util.send_response(util.HTTP_207_MULTI_STATUS, responses)


@util.validate_course_code
def logout():
    session.clear()
    return util.send_response(util.HTTP_204_NO_CONTENT)


def handle_login_request(course_code):
    json_data = request.get_json()
    user = auth.authenticate(course_code, json_data.get("username"), json_data.get('password'))

    if user:
        session['user_id'] = user.id
        return util.send_response(util.HTTP_204_NO_CONTENT)
    return util.send_response(util.HTTP_401_UNAUTHORIZED, {"error": "Unauthorized!"})


class UserAdditionDTO:
    def __init__(self, json_data):
        self.usernames = json_data.get("usernames").split(",")
        self.password = json_data.get("password")
        self.elevated_privileges = json_data.get("elevated_privileges")
