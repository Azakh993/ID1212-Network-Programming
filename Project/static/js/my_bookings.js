// noinspection JSUnresolvedReference,JSUnusedLocalSymbols

/**
 * Adds an event listener to execute code when the DOM content is fully loaded.
 * It sets up WebSocket listeners and event listeners.
 */
document.addEventListener("DOMContentLoaded", () => {
	const socket = setupWebSocketListeners();
	setupEventListeners(socket);
});

/**
 * Sets up WebSocket listeners and establishes a connection to the server.
 * It listens for updates on personal bookings and fetches the latest data.
 * @returns {Socket} The WebSocket object for communication.
 */
function setupWebSocketListeners() {
	const socket = io.connect('http://' + window.location.hostname + ':' + location.port);

	socket.on('update_personal_bookings', function(data) {
		fetchLatestReservationsListData(course_code);
	});
	return socket;
}

/**
 * Sets up event listeners for canceling reservations and navigating back to the booking list.
 * @param {Socket} socket - The WebSocket object used for communication.
 */
function setupEventListeners(socket) {
	document.getElementById("cancelReservationBtn").addEventListener("click", () => cancelSelectedReservation(socket));
	document.getElementById("backBtn").addEventListener("click", backToBookingList);

}

/**
 * Cancels a selected reservation by sending a DELETE request to the server.
 * @param {Socket} socket - The WebSocket object used for communication.
 */
function cancelSelectedReservation(socket) {
	const selectedReservation = document.querySelector("input[name='selectedReservation']:checked");

	if (!selectedReservation) {
		alert("Please select a reservation.");
		return;
	}

	const selectedReservationID = selectedReservation.value;
	const courseCode = course_code;

	const requestOptions = {
		method: "DELETE",
		body: JSON.stringify({
			reservation_id: selectedReservationID
		}),
		headers: {
			"Content-Type": "application/json"
		}
	};

	fetch(`/courses/${courseCode}/my-bookings`, requestOptions)
		.then((response) => handleResponse(response, socket))
		.catch(handleError);
}

/**
 * Navigates back to the booking list page.
 */
function backToBookingList() {
	const courseCode = course_code;
	window.location.href = `/courses/${courseCode}/booking-lists`;
}

/**
 * Handles the response from the server after canceling a reservation.
 * It checks the HTTP response status and takes appropriate action.
 * @param {Response} response - The HTTP response from the server.
 * @param {Socket} socket - The WebSocket object used for communication.
 */
function handleResponse(response, socket) {
	const courseCode = course_code;
	switch (response.status) {
		case 200:
			return response.json()
		case 201:
			return fetchLatestReservationsListData(courseCode)
		case 204:
			alert("Reservation removed successfully.");
			socket.emit('personal_bookings_changed');
			return fetchLatestReservationsListData(courseCode)
		default:
			throw new Error("Request failed: " + response.status)
	}
}

/**
 * Updates the reservations list UI with the latest data received from the server.
 * @param {Object} jsonData - JSON data containing the latest reservation information.
 */
function updateReservationsListUI(jsonData) {
	const tableBody = document.querySelector("#bookingTable tbody");

	const existingRows = tableBody.querySelectorAll("tr");
	existingRows.forEach(row => tableBody.removeChild(row));

	jsonData.reservations.forEach(reservation => {
		const newRow = createReservationRow(reservation);
		tableBody.appendChild(newRow);
	});
}

/**
 * Creates a new row in the reservations list table based on reservation information.
 * @param {Object} reservation - The reservation data.
 * @returns {HTMLElement} The created table row element.
 */
function createReservationRow(reservation) {
	const newRow = document.createElement("tr");

	newRow.innerHTML = `
            <td><input type="radio" name="selectedReservation" value="${reservation.id}"></td>
            <td>${reservation.start_time}</td>
            <td>${reservation.description}</td>
            <td>${reservation.location}</td>
            <td>${reservation.length} min</td>
    `;

	return newRow;
}

/**
 * Fetches the latest reservation data for the user's bookings.
 * It sends a GET request to retrieve the reservations from the server.
 * @param {string} courseCode - The course code for which to fetch reservation data.
 */
function fetchLatestReservationsListData(courseCode) {
	fetch(`/courses/${courseCode}/my-bookings`, {
			method: "GET",
			headers: {
				"Accept": "application/json"
			}
		})
		.then(handleResponse)
		.then(updateReservationsListUI)
		.catch(handleError);
}

/**
 * Handles errors that occur during the request or response handling.
 * It logs errors to the console and displays an error message to the user via an alert.
 * @param {Error} error - The error object representing the encountered error.
 */
function handleError(error) {
	console.error("Error:", error);
	alert("An error occurred, please try again.");
}