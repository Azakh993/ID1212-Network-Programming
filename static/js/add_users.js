document.addEventListener("DOMContentLoaded", () => {
	setupEventListeners();
});

function setupEventListeners() {
	document.getElementById("addUsersBtn").addEventListener("click", addUsers);
	document.getElementById("backBtn").addEventListener("click", backToBookingList);

}

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

function backToBookingList() {
	const courseCode = course_code;
	window.location.href = `/courses/${courseCode}/booking-lists`;
}

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

function handleError(error) {
	console.error("Error:", error);
	alert("An error occurred, please try again.");
}