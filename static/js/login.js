document.getElementById("login-form").addEventListener("submit", function (event) {
        event.preventDefault();

        const courseCode = "{{ session.get('course_code') }}";
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;
        const errorMessage = document.getElementById("error-message");

        fetch(`/${courseCode}/login`, {
            method: "POST",
            body: new URLSearchParams({ username, password }),
            headers: {"Content-Type": "application/x-www-form-urlencoded",
            },
        })
        .then((response) => response.json())
        .then((data) => {
            if (data.error) {
                errorMessage.innerHTML = `<p>${data.error}</p>`;
            } else if (data.success) {
                window.location.href = "{{ url_for('show_login_page', course_code=courseCode) }}";
            }
        })
        .catch((error) => {
            console.error("Error:", error);
        });
    });