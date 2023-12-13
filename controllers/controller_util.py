from functools import wraps

from flask import jsonify, session, redirect, url_for

from services.auth_service import get_user_privileges


def validate_course_code(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        course_code = kwargs.get('course_code')
        if course_code is None:
            return jsonify(error="Course code is invalid"), 400
        return f(*args, **kwargs)

    return decorated_function


def validate_user_login(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user_id' not in session:
            return redirect(url_for('login', course_code=kwargs.get('course_code')))
        return f(*args, **kwargs)

    return decorated_function


def check_privileges(course_code, user_id):
    admin_privileges = get_user_privileges(course_code, user_id)

    if not admin_privileges:
        raise Exception("User is not an admin!")
