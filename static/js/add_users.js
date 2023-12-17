/**
 * Initializes event listeners when the DOM content is fully loaded.
 * It calls the `setupEventListeners` function.
 */
document.addEventListener("DOMContentLoaded", () => {
	setupEventListeners();
});

/**
 * Sets up event listeners for various elements in the HTML document.
 * It listens for the "click" event on the "addUsersBtn" and "backBtn" elements.
 */
function setupEventListeners() {
	document.getElementById("addUsersBtn").addEventListener("click", addUsers);
	document.getElementById("backBtn").addEventListener("click", backToBookingList);

}


/**
 * Sends a POST request to add users to a course.
 * It retrieves user input from the HTML form and sends it to the server.
 */
function addUsers() {
	const usernames = document.getElementById("usernames").value;
	const password = document.getElementById("password").value;
	const elevated_privileges = document.getElementById("elevated-privileges").value;
	const courseCode = course_code;

	const requestOptions = {
		method: "POST",
		body: JSON.stringify({
			usernames: usernames,
			password: password,
			elevated_privileges: elevated_privileges
		}),
		headers: {
			"Content-Type": "application/json"
		}
	};

	fetch(`/courses/${courseCode}/add-users`, requestOptions)
		.then(handleResponse)
		.catch(handleError);
}

/**
 * Redirects the user to the booking list page for a specific course.
 * It retrieves the course code and redirects accordingly.
 */
function backToBookingList() {
	const courseCode = course_code;
	window.location.href = `/courses/${courseCode}/booking-lists`;
}

/**
 * Handles the response from the server based on the HTTP status code.
 * It can process single or multi-response scenarios.
 * @param {Response} response - The HTTP response from the server.
 * @returns {Promise} A promise that resolves with the parsed JSON response.
 * @throws {Error} If the request fails with an unexpected status code.
 */
async function handleResponse(response) {
	switch (response.status) {
		case 200:
			return response.json()
		case 207:
			const responseJson = await response.json()
			return processMultiResponse(responseJson)
		default:
			throw new Error("Request failed: " + response.status)
	}
}

/**
 * Processes a multi-response JSON object and displays a message to the user.
 * It categorizes the responses into added, enrolled, and failed users.
 * @param {Object[]} responseJson - An array of response objects.
 */
function processMultiResponse(responseJson) {
	const addedUsers = [];
	const enrolledUsers = [];
	const failedToAddUsers = [];
	const failedToEnrollUsers = [];

	responseJson.forEach((response) => {
		if (response.status === 201) {
			if (response.data.addedUsers) {
				addedUsers.push(...response.data.addedUsers.map((user) => user.username));
			}
			if (response.data.enrolledUsers) {
				enrolledUsers.push(...response.data.enrolledUsers.map((user) => user.username));
			}
		} else if (response.status === 409) {
			if (response.data.failedUserEntries) {
				failedToAddUsers.push(...response.data.failedUserEntries.map((user) => user.username));
			}
			if (response.data.failedEnrollEntries) {
				failedToEnrollUsers.push(...response.data.failedEnrollEntries.map((user) => user.username));
			}
		}
	});

	let message = "";
	if (addedUsers.length > 0) {
		message += `Added Users: ${addedUsers.join(', ')}\n`;
	}

	if (enrolledUsers.length > 0) {
		message += `Enrolled Users: ${enrolledUsers.join(', ')}\n`;
	}

	if (failedToAddUsers.length > 0) {
		message += `Failed to Add Users: ${failedToAddUsers.join(', ')}\n`;
	}

	if (failedToEnrollUsers.length > 0) {
		message += `Failed to Enroll Users: ${failedToEnrollUsers.join(', ')}\n`;
	}

	alert(message);
}

/**
 * Handles and logs errors that occur during the request or response handling.
 * Displays an error message to the user via an alert.
 * @param {Error} error - The error object representing the encountered error.
 */
function handleError(error) {
	console.error("Error:", error);
	alert("An error occurred, please try again.");
}