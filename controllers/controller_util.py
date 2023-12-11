from services.auth_service import get_user_privileges


def check_privileges(course_code, user_id):
    admin_privileges = get_user_privileges(course_code, user_id)

    if not admin_privileges:
        raise Exception("User is not an admin!")
