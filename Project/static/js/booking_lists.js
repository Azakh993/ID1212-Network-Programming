// noinspection JSUnresolvedReference,JSUnusedLocalSymbols

/**
 * Initializes event listeners, WebSocket connections, and an automatic page refresh timer when the DOM content is fully loaded.
 * It calls the `setupWebSocketListeners`, `setupEventListeners`, and sets a page refresh timer.
 */
document.addEventListener("DOMContentLoaded", () => {
	const socket = setupWebSocketListeners();
	setupEventListeners(socket);

	const refreshTime = 10 * 60 * 1000;
	setTimeout(() => {
		location.reload();
	}, refreshTime);
});

/**
 * Sets up WebSocket listeners and establishes a WebSocket connection.
 * It listens for various WebSocket events and triggers related actions.
 * @returns {WebSocket} The WebSocket instance.
 */
function setupWebSocketListeners() {
	const socket = io.connect('http://' + window.location.hostname + ':' + location.port);

	socket.on('new_booking_list_added', addBookingListRow);
	socket.on('existing_booking_list_removed', removeBookingListRow);
	socket.on('update_booking_list', function(data) {
		fetchLatestBookingListData(course_code);
	});
	return socket;
}

/**
 * Sets up event listeners for various elements in the HTML document.
 * It listens for "click" events on buttons and invokes corresponding functions.
 * @param {WebSocket} socket - The WebSocket instance.
 */
function setupEventListeners(socket) {
	if (admin === "True") {
		document.getElementById("addBookingListBtn").addEventListener("click", toggleNewBookingRow);
		document.getElementById("newBookingRowSubmitBtn").addEventListener("click", () => submitNewBooking(socket));
		document.getElementById("removeBookingListBtn").addEventListener("click", () => removeBookingList(socket));
		document.getElementById("addUsersBtn").addEventListener("click", goToAddUsersPage);
	}
	document.getElementById("selectBookingBtn").addEventListener("click", selectBooking);
	document.getElementById("showMyBookingsBtn").addEventListener("click", showMyBookings);
	document.getElementById("logoutBtn").addEventListener("click", logOut);
}

/**
 * Toggles the visibility of the new booking row.
 * It shows/hides the row for adding a new booking list.
 */
function toggleNewBookingRow() {
	const newBookingRow = document.getElementById("newBookingRow");
	newBookingRow.style.display = newBookingRow.style.display === "none" ? "table-row" : "none";
}

/**
 * Submits a new booking list to the server.
 * It retrieves form data, validates it, and sends a POST request to create a new booking list.
 * @param {WebSocket} socket - The WebSocket instance.
 */
function submitNewBooking(socket) {
	const bookingData = getBookingFormData();

	if (!isFormDataValid(bookingData)) {
		alert("Please fill in all fields.");
		return;
	}

	const courseCode = course_code;
	const URI = `/courses/${courseCode}/booking-lists`
	const requestOptions = {
		method: "POST",
		body: JSON.stringify(bookingData),
		headers: {
			"Content-Type": "application/json"
		}
	};
	sendRequest(socket, URI, requestOptions)
}

/**
 * Retrieves form data for creating a new booking list.
 * @returns {Object} The booking list data from the form.
 */
function getBookingFormData() {
	return {
		time: document.getElementById("inputTime").value,
		description: document.getElementById("inputDescription").value,
		location: document.getElementById("inputLocation").value,
		length: document.getElementById("inputLength").value,
		slots: document.getElementById("inputSlots").value
	};
}

/**
 * Validates form data to ensure all fields are filled.
 * @param {Object} data - The form data to validate.
 * @returns {boolean} True if all fields are filled; otherwise, false.
 */
function isFormDataValid(data) {
	return Object.values(data).every(value => value);
}

/**
 * Redirects the user to the bookable slots page for a selected booking list.
 */
function selectBooking() {
	const courseCode = course_code;
	const selectedBookingList = document.querySelector("input[name='selectedBooking']:checked");
	const selectedBookingListID = selectedBookingList.value;

	if (!selectedBookingList) {
		alert("No booking selected.");
		return;
	}
	window.location.href = `/courses/${courseCode}/booking-lists/${selectedBookingListID}/bookable-slots`;
}

/**
 * Redirects the user to the "My Bookings" page.
 */
function showMyBookings() {
	const courseCode = course_code;
	window.location.href = `/courses/${courseCode}/my-bookings`;
}

/**
 * Removes a selected booking list.
 * It sends a DELETE request to remove the selected booking list.
 * @param {WebSocket} socket - The WebSocket instance.
 */
function removeBookingList(socket) {
	const selectedBooking = document.querySelector('input[name="selectedBooking"]:checked');
	const selectedBookingID = selectedBooking.value;

	if (!selectedBooking) {
		alert("No booking selected.");
		return;
	}

	const courseCode = course_code;
	const URI = `/courses/${courseCode}/booking-lists/${selectedBookingID}`
	const requestOptions = {
		method: "DELETE",
		headers: {
			"Content-Type": "application/json"
		}
	};
	sendRequest(socket, URI, requestOptions)
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
 * Redirects the user to the "Add Users" page.
 */
function goToAddUsersPage() {
	const courseCode = course_code;
	window.location.href = `/courses/${courseCode}/add-users`;
}

/**
 * Logs the user out and redirects to the logout page.
 */
function logOut() {
	const courseCode = course_code;
	window.location.href = `/courses/${courseCode}/logout`;
}

/**
 * Handles the response from the server based on the HTTP status code.
 * It processes various response statuses and triggers UI updates.
 * @param {Response} response - The HTTP response from the server.
 * @param {WebSocket} socket - The WebSocket instance.
 */
async function handleResponse(response, socket) {
	switch (response.status) {
		case 200:
			return response.json()
		case 201:
			const newBookingListJson = await response.json();
			socket.emit('new_booking_list_added', newBookingListJson);
			return addBookingListRow(newBookingListJson);
		case 410:
			const removedBookingListIDJson = await response.json();
			socket.emit('existing_booking_list_removed', removedBookingListIDJson);
			return removeBookingListRow(removedBookingListIDJson);
		case 422:
			alert("There are booked slots in the list. Please remove them first.")
			return
		default:
			throw new Error("Request failed: " + response.status);
	}
}

/**
 * Adds a new booking list row to the UI.
 * @param {Object} newBookingListJson - JSON data representing the new booking list.
 */
function addBookingListRow(newBookingListJson) {
	const tableBody = document.querySelector("#bookingTable tbody");
	const newRow = createBookingRow(newBookingListJson.newBookingList);
	tableBody.appendChild(newRow);
}

/**
 * Removes a booking list row from the UI.
 * @param {Object} removedBookingListIDJson - JSON data containing the ID of the removed booking list.
 */
function removeBookingListRow(removedBookingListIDJson) {
	console.log(removedBookingListIDJson.bookingListId);
	const tableBody = document.querySelector("#bookingTable tbody");
	const rowToRemove = tableBody.querySelector(`input[value="${removedBookingListIDJson.bookingListId}"]`).parentElement.parentElement.parentElement;
	tableBody.removeChild(rowToRemove);
}

/**
 * Creates a new row in the booking list table based on booking list information.
 * @param {Object} booking_list - The booking list data.
 * @returns {HTMLElement} The created table row element.
 */
function createBookingRow(booking_list) {
	const newRow = document.createElement("tr");

	newRow.innerHTML = `
        <td><label><input type="radio" name="selectedBooking" value="${booking_list.id}"></label></td>
        <td>${booking_list.time}</td>
        <td>${booking_list.description}</td>
        <td>${booking_list.location}</td>
        <td>${booking_list.interval} min</td>
        <td style="text-align: center;">${booking_list.available_slots}</td>
    `;
	return newRow;
}

/**
 * Fetches the latest booking list data for the course.
 * It sends a GET request to retrieve the booking lists from the server.
 * @param {string} courseCode - The course code for which to fetch booking list data.
 */
function fetchLatestBookingListData(courseCode) {
	fetch(`/courses/${courseCode}/booking-lists`, {
			method: "GET",
			headers: {
				"Accept": "application/json"
			}
		})
		.then(handleResponse)
		.then(updateBookingListUI)
		.catch(handleError);
}


/**
 * Updates the booking list UI with the latest data received from the server.
 * @param {Object} jsonData - JSON data containing the latest booking list information.
 */
function updateBookingListUI(jsonData) {
	const tableBody = document.querySelector("#bookingTable tbody");

	const existingRows = tableBody.querySelectorAll("tr:not(#newBookingRow)");
	existingRows.forEach(row => tableBody.removeChild(row));

	jsonData.booking_lists.forEach(booking => {
		const newRow = createBookingRow(booking);
		tableBody.appendChild(newRow);
	});
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