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
        .then((response) => {
        if (response.status === 200) {
            return response.json();
        } else {
            errorMessage.innerHTML = "<p>Invalid username or password</p>";
            throw new Error("Authentication failed");
        }
    })
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