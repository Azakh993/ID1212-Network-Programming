from functools import wraps

from flask import jsonify, session, redirect, url_for, request, make_response

from repositories import registration_repository as rr
from repositories.course_repository import get_course_by_code

HTTP_200_OK = 200
HTTP_201_CREATED = 201
HTTP_204_NO_CONTENT = 204
HTTP_207_MULTI_STATUS = 207
HTTP_400_BAD_REQUEST = 400
HTTP_401_UNAUTHORIZED = 401
HTTP_403_FORBIDDEN = 403
HTTP_404_NOT_FOUND = 404
HTTP_409_CONFLICT = 409
HTTP_410_GONE = 410
HTTP_422_UNPROCESSABLE_ENTITY = 422


def validate_course_code(function):
    @wraps(function)
    def decorated_function(*args, **kwargs):
        course_code = kwargs.get('course_code')
        course = get_course_by_code(course_code.upper())
        if course is None:
            return redirect('/invalid_course_code')
        return function(*args, **kwargs)

    return decorated_function


def validate_user_login(function):
    @wraps(function)
    def decorated_function(*args, **kwargs):
        if 'user_id' not in session:
            return redirect(url_for('login', course_code=kwargs.get('course_code')))
        return function(*args, **kwargs)

    return decorated_function


def validate_privileges(function):
    @wraps(function)
    def decorated_function(*args, **kwargs):
        user_id = session.get('user_id')
        course_code = kwargs.get('course_code')
        elevated_privileges = get_user_privileges(course_code=course_code, user_id=user_id)

        if not elevated_privileges:
            return send_response(HTTP_401_UNAUTHORIZED, {"error": "User does not have elevated privileges!"})
        return function(*args, **kwargs)

    return decorated_function


def send_response(status_code, response_data=None):
    if response_data is None:
        return make_response(jsonify({}), status_code)
    return make_response(jsonify(response_data), status_code)


def json_request():
    return request.headers.get('Accept') == 'application/json'


def get_user_privileges(course_code, user_id):
    return rr.retrieve_user_privileges(course_code, user_id)
