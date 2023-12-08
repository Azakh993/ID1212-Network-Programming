from flask import Flask, redirect, url_for, session, request, jsonify, make_response
from config.config import SECRET_KEY
from controllers import login

app = Flask(__name__)
app.secret_key = SECRET_KEY


def course_code_is_valid(course_code):
    if course_code is None:
        error_response = jsonify({"error": "Invalid course code!"})
        return make_response(error_response, 404)
    return True


@app.route("/<course_code>/login", methods=["GET", "POST"])
def login_page(course_code):
    if course_code_is_valid(course_code):
        if request.method == "GET":
            return login.show_login_page()
        elif request.method == "POST":
            username = request.form['username']
            password = request.form['password']
            return login.authenticate(username, password)
    return redirect(url_for('login_page', course_code=course_code))



if __name__ == '__main__':
    app.run(debug=True)
