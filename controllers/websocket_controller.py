from flask_socketio import emit


def emit_get_new_booking_list(booking_list):
    emit('new_booking_list_added', booking_list, broadcast=True, include_self=False)


def emit_remove_existing_booking_list(booking_list_id):
    emit('existing_booking_list_removed', booking_list_id, broadcast=True, include_self=False)
    emit('update_booking_slots', broadcast=True)


def emit_update_booking_lists():
    emit('update_booking_list', broadcast=True, include_self=False)
    emit('update_booking_slots', broadcast=True)


def emit_update_booking_slots():
    emit('update_booking_slots', broadcast=True, include_self=False)
    emit('update_booking_list', broadcast=True)
    emit('update_personal_bookings', broadcast=True)


def emit_update_personal_bookings():
    emit('update_booking_list', broadcast=True)
    emit('update_booking_slots', broadcast=True)
