from flask import render_template, session, jsonify, make_response, request

from controllers.controller_util import validate_course_code
from services.auth_service import authenticate_user


@validate_course_code
def login(course_code):
    if request.method == "GET":
        return render_template("login.html", course_code=course_code)

    if request.method == "POST":
        json_data = request.get_json()
        username = json_data.get("username")
        password = json_data.get('password')
        return authenticate(course_code, username, password)


def authenticate(course_code, username, password):
    user = authenticate_user(course_code, username, password)
    if user is None:
        error_message = {"error": "Unauthorized!"}
        return make_response(jsonify(error_message), 401)

    session['user_id'] = user.id
    return make_response('', 204)
