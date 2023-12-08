from repositories.user import get_user_by_username


def authenticate_user(course_code, username, password):
    user = get_user_by_username(course_code, username)

    if user is not None and user.password == password:
        return user

    return None
