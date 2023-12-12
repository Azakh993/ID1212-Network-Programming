document.addEventListener("DOMContentLoaded", () => {
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById("cancelReservationBtn").addEventListener("click", cancelSelectedReservation);
    document.getElementById("backBtn").addEventListener("click", backToBookingList);

}

function cancelSelectedReservation() {
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
    if (response.status === 200) {
        return response.json();
    }

    if (response.status === 204 || response.status === 201) {
        return fetchLatestReservationsListData(courseCode)
    }

    throw new Error("Request failed: " + response.status);
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

