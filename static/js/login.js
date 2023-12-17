/**
 * Adds a submit event listener to the login form and handles the form submission.
 * It prevents the default form submission behavior and calls the `handleLoginFormSubmit` function.
 */
document.getElementById("login-form").addEventListener("submit", handleLoginFormSubmit);

/**
 * Handles the submission of the login form.
 * It prevents the default form submission behavior, extracts input values, and invokes the `authenticateUser` function.
 * @param {Event} event - The submit event object.
 */
function handleLoginFormSubmit(event) {
	event.preventDefault();

	const courseCode = course_code;
	const username = document.getElementById("username").value;
	const password = document.getElementById("password").value;
	authenticateUser(courseCode, username, password);
}

/**
 * Authenticates the user by sending a POST request to the server.
 * It includes the provided username and password in the request body.
 * @param {string} courseCode - The course code for which the user is trying to log in.
 * @param {string} username - The user's username.
 * @param {string} password - The user's password.
 */
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

/**
 * Handles the response from the authentication request.
 * It checks the HTTP response status and takes appropriate action.
 * @param {Response} response - The HTTP response from the server.
 */
function handleAuthenticationResponse(response) {
	if (response.status === 204) {
		redirectUserAfterAuthentication();
	} else {
		displayAuthenticationError();
	}
}

/**
 * Redirects the user to the booking lists page after successful authentication.
 * It retrieves the course code and constructs the redirect URL.
 */
function redirectUserAfterAuthentication() {
	const courseCode = course_code;
	window.location.href = `/courses/${courseCode}/booking-lists`;
}

/**
 * Displays an authentication error message to the user.
 * It updates the error message element with a message indicating invalid credentials.
 */
function displayAuthenticationError() {
	const errorMessage = document.getElementById("error-message");
	errorMessage.innerHTML = "<p>Invalid username or password</p>";
	throw new Error("Authentication failed");
}

/**
 * Handles authentication-related errors by logging them to the console.
 * @param {Error} error - The error object representing the encountered error.
 */
function handleAuthenticationError(error) {
	console.error("Error:", error);
}