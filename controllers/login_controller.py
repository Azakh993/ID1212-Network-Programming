from flask import render_template, request, url_for, redirect, session
from services.authentication_service import authenticate_user


def show_login_page():
    session.clear()
    return render_template("login.html")


def login():
    if request.method == "POST":
        username = request.form.get("username")
        password = request.form.get("password")

        user = authenticate_user(username, password)

        if user is None:
            return render_template("login.html", error="Invalid username or password")

        session['user_id'] = user.id
        return redirect(url_for("show_dashboard_page"))

    return show_login_page()
