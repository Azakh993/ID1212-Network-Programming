from models import BookingList, User
from models.reservation import Reservation
from repositories import session
from repositories.repository_util import get_all_from_database, add_to_database, delete_from_database


def retrieve_reservations(booking_list_id):
    return get_all_from_database(session.query(Reservation)
                                 .filter_by(list_id=booking_list_id))


def insert_reservation(new_reservation):
    add_to_database(new_reservation)


def retrieve_user_reservations(course_code, user_id):
    return get_all_from_database(((session.query(Reservation)
                                   .join(BookingList, BookingList.id == Reservation.list_id))
                                  .filter(BookingList.course_id == course_code.upper(),
                                          User.id == user_id)))


def delete_reservation(reservation_id):
    delete_from_database(session.query(Reservation).filter(Reservation.id == reservation_id))