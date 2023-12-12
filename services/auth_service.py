from repositories.registration_repository import retrieve_user_privileges
from repositories.user_repository import get_user_by_username


def authenticate_user(course_code, username, password):
    user = get_user_by_username(course_code, username)

    if user is not None and user.password == password:
        return user

    return None


def get_user_privileges(course_code, user_id):
    return retrieve_user_privileges(course_code, user_id)


def retrieve_user_by_username(course_code, username):
    return get_user_by_username(course_code, username)
