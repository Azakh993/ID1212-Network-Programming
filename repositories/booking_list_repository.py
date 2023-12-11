from models.booking_list import BookingList
from repositories import session
from repositories.repository_util import get_all_from_database, get_first_from_database, add_to_database, \
    delete_from_database


def retrieve_booking_lists(course_code):
    return get_all_from_database(session.query(BookingList)
                                 .filter(BookingList.course_id == course_code.upper()))


def retrieve_booking_list(booking_list_id):
    return get_first_from_database(session.query(BookingList)
                                   .filter(BookingList.id == booking_list_id))


def insert_booking_list(new_booking_list):
    add_to_database(new_booking_list)


def delete_booking_list(course_code, booking_id):
    delete_from_database(session.query(BookingList)
                         .filter(BookingList.course_id == course_code.upper(), BookingList.id == booking_id))
