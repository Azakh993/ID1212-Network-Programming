from flask import render_template, session, jsonify, make_response
from services.auth import authenticate_user


def show_login_page(course_code):
    return render_template("login.html", course_code=course_code)


def authenticate(course_code, username, password):
    user = authenticate_user(course_code, username, password)

    if user is None:
        rendered_template = render_template("login.html", course_code=course_code)
        response = make_response(rendered_template)
        response.status_code = 401
        return response

    session['user_id'] = user.id
    success_response = jsonify({"success": True})
    return make_response(success_response, 200)
