from models.registration import UserCourseRegistration
from models.user import User
from repositories import session
from repositories.repository_util import get_first_from_database, add_to_database


def get_user_by_username(username):
    return get_first_from_database(session.query(User).filter(User.username == username))


def get_user_by_username_and_course_code(course_code, username):
    return get_first_from_database(((session.query(User)
                                     .join(UserCourseRegistration))
                                    .filter(UserCourseRegistration.course_id == course_code.upper(),
                                            User.username == username)))


def get_user_by_user_id(user_id):
    return get_first_from_database(session.query(User).filter_by(id=user_id))


def insert_users(user_to_add):
    add_to_database(user_to_add)
