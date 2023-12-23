// noinspection JSUnresolvedReference,JSUnusedLocalSymbols

/**
 * Initializes event listeners and WebSocket connections when the DOM content is fully loaded.
 * It calls the `setupWebSocketListeners` and `setupEventListeners` functions.
 */
document.addEventListener("DOMContentLoaded", () => {
	const socket = setupWebSocketListeners();
	setupEventListeners(socket);
});

/**
 * Sets up WebSocket listeners and establishes a WebSocket connection.
 * It listens for the "update_booking_slots" event and triggers a fetch for the latest slot data.
 * @returns {WebSocket} The WebSocket instance.
 */
function setupWebSocketListeners() {
	const socket = io.connect('http://' + window.location.hostname + ':' + location.port);

	socket.on('update_booking_slots', function(data) {
		fetchLatestSlotsListData(course_code);
	});

	return socket;
}

/**
 * Sets up event listeners for various elements in the HTML document.
 * It listens for the "click" event on buttons and invokes corresponding functions.
 * @param {WebSocket} socket - The WebSocket instance.
 */
function setupEventListeners(socket) {
	if (admin === 'True') {
		document.getElementById("bookForStudentBtn").addEventListener("click", () => {
			const bookForStudent = true
			bookSelectedSlot(socket, bookForStudent)
		});
		document.getElementById("removeBookingBtn").addEventListener("click", () => removeBooking(socket));
	}
	document.getElementById("bookBtn").addEventListener("click", () => {
		const bookForStudent = false
		bookSelectedSlot(socket, bookForStudent)
	});
	document.getElementById("backBtn").addEventListener("click", backToBookingList);

}

/**
 * Books a selected slot for a student.
 * It sends a POST request to reserve a slot.
 * @param {WebSocket} socket - The WebSocket instance.
 * @param {boolean} bookForStudent - Indicates whether the booking is for a student.
 */
function bookSelectedSlot(socket, bookForStudent) {
	const courseCode = course_code
	const selectedBookingListID = booking_list_id;
	const selectedSlotSequenceID = getSelectedSlotSequenceID()
	const available = verifySlotAvailability(selectedSlotSequenceID)

	if (!available) {
		alert("This slot is not available.");
		return;
	}
	const method = "POST"
	let body;

	if (bookForStudent) {
		const username = document.getElementById("inputUsername" + selectedSlotSequenceID).value;

		if (username === null || username.length === 0) {
			alert("Please provide a valid username.")
			return;
		}
		body = JSON.stringify({
			username: username
		})
	} else {
		body = JSON.stringify({})
	}

	const URI = `/courses/${courseCode}/booking-lists/${selectedBookingListID}/bookable-slots/${selectedSlotSequenceID}`
	const requestOptions = setRequestOptions(method, body)
	sendRequest(socket, URI, requestOptions)
}

/**
 * Removes a booking from a selected slot.
 * It sends a DELETE request to cancel a reservation.
 * @param {WebSocket} socket - The WebSocket instance.
 */
function removeBooking(socket) {
	const courseCode = course_code
	const selectedBookingListID = booking_list_id;
	const selectedSlotSequenceID = getSelectedSlotSequenceID()

	const available = verifySlotAvailability(selectedSlotSequenceID)
	if (available) {
		alert("This slot is not booked.");
		return;
	}

	const method = "DELETE"
	const body = JSON.stringify({})
	const requestOptions = setRequestOptions(method, body)
	const URI = `/courses/${courseCode}/booking-lists/${selectedBookingListID}/bookable-slots/${selectedSlotSequenceID}`
	sendRequest(socket, URI, requestOptions)
}

/**
 * Retrieves the selected slot sequence ID based on user input.
 * @returns {string} The selected slot sequence ID.
 */
function getSelectedSlotSequenceID() {
	const selectedSlot = document.querySelector("input[name='selectedSlot']:checked");

	if (!selectedSlot) {
		alert("Please select a slot.");
		return;
	}

	return selectedSlot.value;
}

/**
 * Verifies the availability of a selected slot based on its data attribute.
 * @param {string} selectedSlotSequenceID - The selected slot sequence ID.
 * @returns {boolean} True if the slot is available; otherwise, false.
 */
function verifySlotAvailability(selectedSlotSequenceID) {
	const slotAvailability = document.querySelector(
		`input[name='selectedSlot'][value='${selectedSlotSequenceID}']`
	).getAttribute('data-availability');

	return slotAvailability !== "Booked";
}

/**
 * Sets request options for HTTP requests.
 * @param {string} method - The HTTP request method (e.g., "POST", "DELETE").
 * @param {Object} requestBody - The request body to be sent as JSON.
 * @returns {Object} The request options object.
 */
function setRequestOptions(method, requestBody) {
	return {
		method: method,
		body: requestBody,
		headers: {
			"Content-Type": "application/json"
		}
	};
}

/**
 * Sends an HTTP request to the server using Fetch API and handles the response.
 * @param {WebSocket} socket - The WebSocket instance.
 * @param {string} URI - The URI for the HTTP request.
 * @param {Object} requestOptions - The request options object.
 */
function sendRequest(socket, URI, requestOptions) {
	fetch(URI, requestOptions)
		.then((response) => handleResponse(response, socket))
		.catch(handleError);
}

/**
 * Redirects the user to the booking list page.
 */
function backToBookingList() {
	const courseCode = course_code;
	window.location.href = `/courses/${courseCode}/booking-lists`;
}

/**
 * Handles the response from the server based on the HTTP status code.
 * It processes various response statuses and triggers UI updates.
 * @param {Response} response - The HTTP response from the server.
 * @param {WebSocket} socket - The WebSocket instance.
 */
function handleResponse(response, socket) {
	const courseCode = course_code;
	switch (response.status) {
		case 200:
			return response.json();
		case 201:
			alert("Reservation made successfully!");
			socket.emit('booking_slots_changed');
			return fetchLatestSlotsListData(courseCode)
		case 204:
			alert("Reservation removed successfully!");
			socket.emit('booking_slots_changed');
			return fetchLatestSlotsListData(courseCode)
		case 400:
			alert("Invalid request.");
			return
		case 403:
			alert("You can only book one slot per booking list!");
			return
		case 404:
			alert("Provided username is not registered in course.");
			return
		default:
			throw new Error("Request failed: " + response.status);
	}
}

/**
 * Updates the UI with the latest slot data received from the server.
 * @param {Object} jsonData - JSON data containing the latest slot information.
 */
function updateSlotsListUI(jsonData) {
	const tableBody = document.querySelector("#slotsTable tbody");

	const existingRows = tableBody.querySelectorAll("tr");
	existingRows.forEach(row => tableBody.removeChild(row));

	if (jsonData.available_slots === null || jsonData.available_slots.length === 0) {
		const newRow = document.createElement("tr");
		newRow.innerHTML = `<td style="text-align: center" colspan="4">No slots available</td>`;
		tableBody.appendChild(newRow);
		return;
	}

	jsonData.available_slots.forEach(slot => {
		const newRow = createSlotRow(slot);
		tableBody.appendChild(newRow);
	});
}

/**
 * Creates a new row in the slots table based on slot information.
 * @param {Object} slot - The slot data.
 * @returns {HTMLElement} The created table row element.
 */
function createSlotRow(slot) {
	const newRow = document.createElement("tr");

	newRow.innerHTML = `
        <td><input type="radio" name="selectedSlot" value="${slot.sequence_id}" data-availability="${slot.user_id ? 'Booked' : 'Available'}"></td>
        <td>${slot.start_time}</td>
        <td>${slot.user_id ? 'Booked' : 'Available'}</td>
        ${admin === 'True' ? `
            <td ${!slot.username ? 'style="display: none"' : ''}>${slot.username || 'None'}</td>
            <td ${slot.username ? 'style="display: none"' : ''}><input type="text" id="inputUsername${slot.sequence_id}" placeholder="Username" style="width: 50%;"></td>
        ` : ''}
    `;


	return newRow;
}

/**
 * Fetches the latest slot data for the booking list.
 * It sends a GET request to retrieve the slot information from the server.
 * @param {string} courseCode - The course code for which to fetch slot data.
 */
function fetchLatestSlotsListData(courseCode) {
	fetch(`/courses/${courseCode}/booking-lists/${booking_list_id}/bookable-slots`, {
			method: "GET",
			headers: {
				"Accept": "application/json"
			}
		})
		.then(handleResponse)
		.then(updateSlotsListUI)
		.catch(handleError);
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