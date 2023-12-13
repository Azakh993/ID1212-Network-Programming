from flask import Flask
from flask_socketio import SocketIO

from config.config import SECRET_KEY
from controllers.auth_controller import login
from controllers.booking_list_controller import manage_booking_lists, \
    delete_booking_list
from controllers.booking_slots_controller import show_booking_slots, manage_booking_slots
from controllers.reservations_controller import user_reservations

app = Flask(__name__)
app.secret_key = SECRET_KEY
socketio = SocketIO(app)

app.add_url_rule("/courses/<course_code>/login",
                 view_func=login, methods=["GET", "POST"])

app.add_url_rule("/courses/<course_code>/booking-lists",
                 view_func=manage_booking_lists, methods=["GET", "POST"])
app.add_url_rule("/courses/<course_code>/booking-lists/<booking_list_id>",
                 view_func=delete_booking_list, methods=["DELETE"])

app.add_url_rule("/courses/<course_code>/booking-lists/<booking_list_id>/bookable-slots",
                 view_func=show_booking_slots, methods=["GET"])
app.add_url_rule("/courses/<course_code>/booking-lists/<booking_list_id>/bookable-slots/<sequence_id>",
                 view_func=manage_booking_slots, methods=["POST", "DELETE"])

app.add_url_rule("/courses/<course_code>/my-bookings",
                 view_func=user_reservations, methods=["GET", "DELETE"])

if __name__ == '__main__':
    socketio.run(app, debug=True)
