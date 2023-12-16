from flask import Flask
from flask_socketio import SocketIO

from config.config import SECRET_KEY
from controllers import auth_controller as auth_ctrl
from controllers import booking_list_controller as bl_ctrl
from controllers import booking_slots_controller as bs_ctrl
from controllers import error_page_controller as ep_ctrl
from controllers import reservations_controller as r_ctrl
from controllers import websocket_controller as ws_ctrl

app = Flask(__name__)
app.secret_key = SECRET_KEY
socketio = SocketIO(app)

socketio.on_event('new_booking_list_added', ws_ctrl.emit_get_new_booking_list)
socketio.on_event('existing_booking_list_removed', ws_ctrl.emit_remove_existing_booking_list)
socketio.on_event('update_booking_list', ws_ctrl.emit_update_booking_lists)
socketio.on_event('booking_slots_changed', ws_ctrl.emit_update_booking_slots)
socketio.on_event('personal_bookings_changed', ws_ctrl.emit_update_personal_bookings)

app.add_url_rule("/courses/<course_code>/login",
                 view_func=auth_ctrl.login,
                 methods=["GET", "POST"])
app.add_url_rule("/courses/<course_code>/logout",
                 view_func=auth_ctrl.logout,
                 methods=["GET"])
app.add_url_rule("/courses/<course_code>/add-users",
                 view_func=auth_ctrl.add_users,
                 methods=["GET", "POST"])
app.add_url_rule("/courses/<course_code>/booking-lists",
                 view_func=bl_ctrl.manage_booking_lists,
                 methods=["GET", "POST"])
app.add_url_rule("/courses/<course_code>/booking-lists/<booking_list_id>",
                 view_func=bl_ctrl.remove_booking_list,
                 methods=["DELETE"])
app.add_url_rule("/courses/<course_code>/booking-lists/<booking_list_id>/bookable-slots",
                 view_func=bs_ctrl.show_booking_slots,
                 methods=["GET"])
app.add_url_rule("/courses/<course_code>/booking-lists/<booking_list_id>/bookable-slots/<sequence_id>",
                 view_func=bs_ctrl.manage_booking_slots,
                 methods=["POST", "DELETE"])
app.add_url_rule("/courses/<course_code>/my-bookings",
                 view_func=r_ctrl.user_reservations,
                 methods=["GET", "DELETE"])
app.add_url_rule("/invalid_course_code",
                 view_func=ep_ctrl.show_invalid_course_code_page,
                 methods=["GET"])

if __name__ == '__main__':
    socketio.run(app, debug=True, log_output=True, use_reloader=True)
