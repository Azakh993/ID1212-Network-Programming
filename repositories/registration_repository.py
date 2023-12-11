from models.registration import UserCourseRegistration
from repositories import session
from repositories.repository_util import get_first_from_database


def retrieve_user_privileges(course_code, user_id):
    enrollment_entry = get_first_from_database((session.query(UserCourseRegistration)
                                                .filter(UserCourseRegistration.course_id == course_code.upper(),
                                                        UserCourseRegistration.user_id == user_id)))
    user_privileges = enrollment_entry.admin
    return user_privileges
