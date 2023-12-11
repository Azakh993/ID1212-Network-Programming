from models.reservation import Reservation
from repositories import session
from repositories.repository_util import get_all_from_database, add_to_database


def retrieve_reservations(booking_list_id):
    return get_all_from_database(session.query(Reservation)
                                 .filter_by(list_id=booking_list_id))


def insert_reservation(new_reservation):
    add_to_database(new_reservation)
