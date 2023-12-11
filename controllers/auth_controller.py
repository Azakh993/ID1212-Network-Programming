from flask import render_template, session, jsonify, make_response

from services.auth_service import authenticate_user


def show_login_page(course_code):
    return render_template("login.html", course_code=course_code)


def authenticate(course_code, username, password):
    user = authenticate_user(course_code, username, password)
    if user is None:
        error_message = {"error": "Unauthorized!"}
        return make_response(jsonify(error_message), 401)

    session['user_id'] = user.id
    return make_response('', 204)
