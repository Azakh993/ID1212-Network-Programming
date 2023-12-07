from flask import Flask, redirect, url_for, render_template, session
from config.config import SECRET_KEY
from controllers import login_controller

app = Flask(__name__)
app.secret_key = SECRET_KEY


def course_code_set(course_code):
    current_repo = session.get('current_repo')
    if current_repo != course_code:
        return "Invalid repo. Please set the repo first."


@app.route("/<course_code>/set_course", methods=["GET"])
def set_course(course_code):
    session['course_code'] = course_code
    return redirect(url_for('show_login_page', course_code=course_code))


@app.route("/<course_code>/login", methods=["GET"])
def show_login_page(course_code):
    if course_code_set(course_code):
        return login_controller.show_login_page()


@app.route("/<course_code>/login", methods=["POST"])
def login(course_code):
    if course_code_set(course_code):
        return login_controller.login()


if __name__ == '__main__':
    app.run(debug=True)
