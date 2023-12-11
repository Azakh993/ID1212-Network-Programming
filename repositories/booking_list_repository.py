from models.booking_list import BookingList
from repositories import session


def retrieve_booking_lists(course_code):
    try:
        return session.query(BookingList).filter(BookingList.course_id == course_code.upper()).all()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise


def insert_booking_list(new_booking_list):
    try:
        session.add(new_booking_list)
        session.commit()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise


def delete_booking_list(course_code, booking_id):
    try:
        (session.query(BookingList)
         .filter(BookingList.course_id == course_code.upper(), BookingList.id == booking_id)
         .delete())
        session.commit()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise
