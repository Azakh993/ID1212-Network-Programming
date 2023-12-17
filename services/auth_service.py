from werkzeug.security import check_password_hash

from models import User, UserCourseRegistration
from repositories import registration_repository as rr
from repositories import user_repository as ur
from util import utility as util


def authenticate(course_code, username, password):
    user = ur.get_user_by_username_and_course_code(course_code, username)
    if user is not None and check_password_hash(user.password, password):
        return user
    return None


def insert_new_users_and_enrollments(course_code, user_addition_dto):
    usernames = [username for username in user_addition_dto.usernames]
    added_users, not_added_users = _add_new_users(usernames, user_addition_dto.hashed_password)
    enrolled_users, not_enrolled_users = _enroll_users(course_code, usernames, user_addition_dto.elevated_privileges)
    return _generate_responses(added_users, enrolled_users, not_added_users, not_enrolled_users)


def _add_new_users(usernames, password):
    new_users = []
    failed_entries = []

    for username in usernames:
        try:
            ur.insert_users(User(username=username, password=password))
            new_users.append(username)
        except Exception as exception:
            print(f'Error: {str(exception)}')
            failed_entries.append(username)

    return (new_users, None) if len(failed_entries) == 0 else (new_users, failed_entries)


def _enroll_users(course_code, usernames, privileges):
    new_enrollments = []
    failed_entries = []

    for username in usernames:
        try:
            user = ur.get_user_by_username(username)
            entry = UserCourseRegistration(user_id=user.id, course_id=course_code.upper(), admin=bool(privileges))
            rr.insert_enrollment_entry(entry)
            new_enrollments.append(username)
        except Exception as exception:
            print(f'Error: {str(exception)}')
            failed_entries.append(username)

    return (new_enrollments, None) if len(failed_entries) == 0 else (new_enrollments, failed_entries)


def _generate_responses(added_users, enrolled_users, failed_user_entries, failed_enroll_entries):
    responses = []

    def create_response(status_code, data_key, data_values):
        responses.append(
            {'status': status_code, 'data': {data_key: [{'username': username} for username in data_values]}})

    if added_users:
        create_response(util.HTTP_201_CREATED, 'addedUsers', added_users)
    if enrolled_users:
        create_response(util.HTTP_201_CREATED, 'enrolledUsers', enrolled_users)
    if failed_user_entries:
        create_response(util.HTTP_409_CONFLICT, 'failedUserEntries', failed_user_entries)
    if failed_enroll_entries:
        create_response(util.HTTP_409_CONFLICT, 'failedEnrollEntries', failed_enroll_entries)

    return responses
