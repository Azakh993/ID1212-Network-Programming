from models.registration import UserCourseRegistration
from repositories import session


def retrieve_user_privileges(course_code, user_id):
    try:
        enrollment_entry = ((session.query(UserCourseRegistration)
                             .filter(UserCourseRegistration.course_id == course_code.upper(),
                                     UserCourseRegistration.user_id == user_id)
                             .first()))
        user_privileges = enrollment_entry.admin
        return user_privileges

    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise
