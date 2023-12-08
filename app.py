from flask import Flask, redirect, url_for, session, request
from config.config import SECRET_KEY
from controllers import login

app = Flask(__name__)
app.secret_key = SECRET_KEY


def course_code_set(course_code):
    session_course_code = session.get('current_repo')
    if session_course_code != course_code:
        return "Invalid course_code. Please set the course_code first."


@app.route("/<course_code>/set_course", methods=["GET"])
def set_course(course_code):
    session['course_code'] = course_code
    return redirect(url_for('login_page', course_code=course_code))


@app.route("/<course_code>/login", methods=["GET", "POST"])
def login_page(course_code):
    if course_code_set(course_code):
        if request.method == "GET":
            return login.show_login_page(course_code)
        elif request.method == "POST":
            username = request.form['username']
            password = request.form['password']
            return login.authenticate(username, password)
    return redirect(url_for('login_page', course_code=course_code))



if __name__ == '__main__':
    app.run(debug=True)
