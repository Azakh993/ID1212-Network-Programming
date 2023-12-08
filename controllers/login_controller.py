from flask import render_template, session, jsonify, make_response
from services.authentication_service import authenticate_user


def show_login_page(course_code):
    session["course_code"] = course_code
    return render_template("login.html")


def login(username, password):
    user = authenticate_user(username, password)

    if user is None:
        error_response = jsonify({"error": "Invalid username or password"})
        return make_response(error_response, 401)

    session['user_id'] = user.id
    success_response = jsonify({"success": True})
    return make_response(success_response, 200)
