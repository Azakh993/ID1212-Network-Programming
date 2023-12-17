document.getElementById("login-form").addEventListener("submit", handleLoginFormSubmit);

function handleLoginFormSubmit(event) {
	event.preventDefault();

	const courseCode = course_code;
	const username = document.getElementById("username").value;
	const password = document.getElementById("password").value;
	const errorMessage = document.getElementById("error-message");

	authenticateUser(courseCode, username, password, errorMessage);
}

function authenticateUser(courseCode, username, password) {
	const requestOptions = {
		method: "POST",
		body: JSON.stringify({
			username: username,
			password: password
		}),
		headers: {
			"Content-Type": "application/json",
		},
	};

	fetch(`/courses/${courseCode}/login`, requestOptions)
		.then(handleAuthenticationResponse)
		.catch(handleAuthenticationError);
}

function handleAuthenticationResponse(response) {
	if (response.status === 204) {
		redirectUserAfterAuthentication();
	} else {
		displayAuthenticationError();
	}
}

function redirectUserAfterAuthentication() {
	const courseCode = course_code;
	window.location.href = `/courses/${courseCode}/booking-lists`;
}

function displayAuthenticationError() {
	const errorMessage = document.getElementById("error-message");
	errorMessage.innerHTML = "<p>Invalid username or password</p>";
	throw new Error("Authentication failed");
}

function handleAuthenticationError(error) {
	console.error("Error:", error);
}