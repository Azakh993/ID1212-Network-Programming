"""
WebSocket Controller

This module contains controllers for WebSocket events used in the booking app.

"""


from flask_socketio import emit


def emit_get_new_booking_list(booking_list):
    """
    Emit a WebSocket event to notify clients of a new booking list being added.

    Parameters
    ----------
    booking_list : dict
        A dictionary representing the new booking list.

    Returns
    -------
    None

    """

    emit('new_booking_list_added', booking_list, broadcast=True, include_self=False)


def emit_remove_existing_booking_list(booking_list_id):
    """
    Emit a WebSocket event to notify clients of the removal of an existing booking list.

    Parameters
    ----------
    booking_list_id : int
        The ID of the removed booking list.

    Returns
    -------
    None

    """

    emit('existing_booking_list_removed', booking_list_id, broadcast=True, include_self=False)
    emit('update_booking_slots', broadcast=True)


def emit_update_booking_lists():
    """
    Emit a WebSocket event to notify clients to update booking lists.

    Returns
    -------
    None

    """

    emit('update_booking_list', broadcast=True, include_self=False)
    emit('update_booking_slots', broadcast=True)


def emit_update_booking_slots():
    """
     Emit a WebSocket event to notify clients to update booking slots.

     Returns
     -------
     None

     """

    emit('update_booking_slots', broadcast=True, include_self=False)
    emit('update_booking_list', broadcast=True)
    emit('update_personal_bookings', broadcast=True)


def emit_update_personal_bookings():
    """
    Emit a WebSocket event to notify clients to update personal bookings.

    Returns
    -------
    None

    """

    emit('update_booking_list', broadcast=True)
    emit('update_booking_slots', broadcast=True)
