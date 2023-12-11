from models.registration import UserCourseRegistration
from models.user import User
from repositories import session
from repositories.repository_util import get_first_from_database


def get_user_by_username(course_code, username):
    return get_first_from_database(((session.query(User)
                                     .join(UserCourseRegistration))
                                    .filter(UserCourseRegistration.course_id == course_code.upper(),
                                            User.username == username)))


def get_user_by_user_id(user_id):
    get_first_from_database(session.query(User).filter_by(id=user_id))
