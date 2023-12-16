from models import Course
from repositories import session
from repositories.repository_util import get_first_from_database


def get_course_by_code(course_code):
    return get_first_from_database(session.query(Course).filter_by(id=course_code))
