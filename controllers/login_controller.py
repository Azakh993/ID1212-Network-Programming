from flask import render_template, session, jsonify
from services.authentication_service import authenticate_user


def show_login_page(course_code):
    session["course_code"] = course_code
    return render_template("login.html")


def login(username, password):
    user = authenticate_user(username, password)

    if user is None:
        return jsonify({"error": "Invalid username or password"})

    session['user_id'] = user.id
    return jsonify({"success": True})
