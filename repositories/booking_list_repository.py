from models.booking_list import BookingList
from repositories import session


def retrieve_booking_lists(course_code):
    try:
        return session.query(BookingList).filter(BookingList.course_id == course_code.upper()).all()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise
