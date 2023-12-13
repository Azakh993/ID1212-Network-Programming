document.addEventListener("DOMContentLoaded", () => {
    const socket = setupWebSocketListeners();
    setupEventListeners(socket);
});

function setupWebSocketListeners() {
    const socket = io.connect('http://' + window.location.hostname + ':' + location.port);

    socket.on('personal_bookings_changed', function (data) {
        fetchLatestReservationsListData(course_code);
    });
    return socket;
}

function setupEventListeners(socket) {
    document.getElementById("cancelReservationBtn").addEventListener("click", () => cancelSelectedReservation(socket));
    document.getElementById("backBtn").addEventListener("click", backToBookingList);

}

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
        body: JSON.stringify({reservation_id: selectedReservationID}),
        headers: {"Content-Type": "application/json"}
    };

    fetch(`/courses/${courseCode}/my-bookings`, requestOptions)
        .then(handleResponse)
        .then(() => {
            socket.emit('booking_lists_changed')
            socket.emit('booking_slots_changed')
            alert("Reservation removed successfully.");
        })
        .catch(handleError);
}

function backToBookingList() {
    const courseCode = course_code;
    window.location.href = `/courses/${courseCode}/booking-lists`;
}

function handleResponse(response) {
    const courseCode = course_code;
    switch (response.status) {
        case 200:
            return response.json()
        case 201:
            return fetchLatestReservationsListData(courseCode)
        case 204:
            return fetchLatestReservationsListData(courseCode)
        default:
            throw new Error("Request failed: " + response.status)
    }
}

function updateReservationsListUI(jsonData) {
    const tableBody = document.querySelector("#myReservationsTable tbody");

    const existingRows = tableBody.querySelectorAll("tr");
    existingRows.forEach(row => tableBody.removeChild(row));

    jsonData.reservations.forEach(reservation => {
        const newRow = createReservationRow(reservation);
        tableBody.appendChild(newRow);
    });
}

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

function fetchLatestReservationsListData(courseCode) {
    fetch(`/courses/${courseCode}/my-bookings`, {
        method: "GET",
        headers: {"Accept": "application/json"}
    })
        .then(handleResponse)
        .then(updateReservationsListUI)
        .catch(handleError);
}

function handleError(error) {
    console.error("Error:", error);
    alert("An error occurred, please try again.");
}

