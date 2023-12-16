from werkzeug.security import check_password_hash

from models import User, UserCourseRegistration
from repositories import registration_repository as rr
from repositories import user_repository as ur
from util import utility as util


def authenticate(course_code, username, password):
    user = ur.get_user_by_username(course_code, username)
    if user is not None and check_password_hash(user.password, password):
        return user
    return None


def insert_new_users_and_enrollments(course_code, user_addition_dto):
    added_users, not_added_users = add_new_users(user_addition_dto.usernames, user_addition_dto.hashed_password)
    enrolled_users, not_enrolled_users = enroll_users(course_code, added_users, user_addition_dto.elevated_privileges)
    return generate_responses(added_users, enrolled_users, not_added_users, not_enrolled_users)


def add_new_users(usernames, password):
    new_users = [User(username=username, password=password) for username in usernames]
    failed_entries = []

    for user in new_users:
        try:
            ur.insert_users(user)
        except Exception as exception:
            print(f'Error: {str(exception)}')
            failed_entries.append(user.username)

    return (new_users, None) if len(failed_entries) == 0 else (new_users, failed_entries)


def enroll_users(course_code, users, privileges):
    new_enrollments = []
    failed_entries = []

    for user in users:
        try:
            entry = UserCourseRegistration(user_id=user.id, course_id=course_code.upper(), admin=bool(privileges))
            rr.insert_enrollment_entry(entry)
            new_enrollments.append(user)
        except Exception as exception:
            print(f'Error: {str(exception)}')
            failed_entries.append(user.username)

    return (new_enrollments, None) if len(failed_entries) == 0 else (new_enrollments, failed_entries)


def generate_responses(added_users, enrolled_users, failed_user_entries, failed_enroll_entries):
    responses = []

    if added_users:
        responses.append(
            {'status': util.HTTP_201_CREATED, 'data': [{'username': user.username} for user in added_users]})
    if enrolled_users:
        responses.append(
            {'status': util.HTTP_201_CREATED, 'data': [{'username': user.username} for user in enrolled_users]})
    if failed_user_entries:
        responses.append(
            {'status': util.HTTP_409_CONFLICT, 'data': [{'username': username} for username in failed_user_entries]})
    if failed_enroll_entries:
        responses.append(
            {'status': util.HTTP_409_CONFLICT, 'data': [{'username': username} for username in failed_enroll_entries]})

    return responses


def retrieve_user_by_username(course_code, username):
    return ur.get_user_by_username(course_code, username)
