from flask_socketio import emit


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
