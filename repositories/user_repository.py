from models.registration import UserCourseRegistration
from models.user import User
from repositories import session


def get_user_by_username(course_code, username):
    try:
        return (((session.query(User)
                  .join(UserCourseRegistration))
                 .filter(UserCourseRegistration.course_id == course_code.upper(),
                         User.username == username))
                .first())
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise


def get_user_by_user_id(user_id):
    try:
        return session.query(User).filter_by(id=user_id).first()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise
