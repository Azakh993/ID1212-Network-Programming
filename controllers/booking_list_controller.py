from flask import render_template, request, session

from services import booking_list_service as bls
from util import utility as util


class BookingListDTO:
    def __init__(self, json_data, course_code, user_id):
        self.course_id = course_code
        self.user_id = user_id
        self.description = json_data["description"]
        self.location = json_data.get("location")
        self.time = json_data.get("time")
        self.interval = json_data.get("length")
        self.max_slots = json_data.get("slots")


@util.validate_course_code
@util.validate_user_login
def manage_booking_lists(course_code):
    if request.method == "GET":
        if util.json_request():
            return util.send_response(util.HTTP_200_OK, {"booking_lists": bls.get_booking_lists(course_code)})

        return render_template("booking_lists.html",
                               course_code=course_code,
                               booking_lists=bls.get_booking_lists(course_code),
                               user_privileges=util.get_user_privileges(course_code, session.get("user_id")))

    if request.method == "POST":
        return add_new_list(course_code=course_code, user_id=session.get("user_id"))


@util.validate_course_code
@util.validate_user_login
@util.validate_privileges
def remove_booking_list(course_code, booking_list_id):
    return util.send_response(*bls.erase_booking_list(course_code, booking_list_id))


@util.validate_privileges
def add_new_list(course_code, user_id):
    json_data = request.get_json()
    booking_list_dto = BookingListDTO(json_data, course_code, user_id)
    status_code, response_data = bls.add_booking_list(course_code, booking_list_dto)
    return util.send_response(status_code, response_data)
