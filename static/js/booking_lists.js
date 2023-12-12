document.addEventListener("DOMContentLoaded", () => {
    setupEventListeners();
});

function setupEventListeners() {
    if (admin === "True") {
        document.getElementById("addBookingListBtn").addEventListener("click", toggleNewBookingRow);
        document.getElementById("newBookingRowSubmitBtn").addEventListener("click", submitNewBooking);
        document.getElementById("removeBookingListBtn").addEventListener("click", removeBookingList);
    }
    document.getElementById("selectBookingBtn").addEventListener("click", selectBooking);
    document.getElementById("showMyBookingsBtn").addEventListener("click", showMyBookings);
}

function toggleNewBookingRow() {
    const newBookingRow = document.getElementById("newBookingRow");
    newBookingRow.style.display = newBookingRow.style.display === "none" ? "table-row" : "none";
}

function submitNewBooking() {
    const courseCode = course_code;
    const bookingData = getBookingFormData();

    if (!isFormDataValid(bookingData)) {
        alert("Please fill in all fields.");
        return;
    }

    const requestOptions = {
        method: "PUT",
        body: JSON.stringify(bookingData),
        headers: {"Content-Type": "application/json"}
    };

    fetch(`/courses/${courseCode}/booking-lists`, requestOptions)
        .then(handleResponse)
        .catch(handleError);
}

function getBookingFormData() {
    return {
        time: document.getElementById("inputTime").value,
        description: document.getElementById("inputDescription").value,
        location: document.getElementById("inputLocation").value,
        length: document.getElementById("inputLength").value,
        slots: document.getElementById("inputSlots").value
    };
}

function isFormDataValid(data) {
    return Object.values(data).every(value => value);
}

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

function showMyBookings() {
    const courseCode = course_code;
    window.location.href = `/courses/${courseCode}/my-bookings`;
}

function removeBookingList() {
    const courseCode = course_code;
    const selectedBooking = document.querySelector('input[name="selectedBooking"]:checked');
    const selectedBookingID = selectedBooking.value;

    if (!selectedBooking) {
        alert("No booking selected.");
        return;
    }

    const requestOptions = {
        method: "DELETE",
        headers: {"Content-Type": "application/json"}
    };

    fetch(`/courses/${courseCode}/booking-lists/${selectedBookingID}`, requestOptions)
        .then(handleResponse)
        .catch(handleError);
}

function handleResponse(response) {
    const courseCode = course_code;
    if (response.status === 200) {
        return response.json();
    }

    if (response.status === 204 || response.status === 201) {
        return fetchLatestBookingListData(courseCode)
    }

    throw new Error("Request failed: " + response.status);
}

function updateBookingListUI(jsonData) {
    const tableBody = document.querySelector("#bookingTable tbody");

    const existingRows = tableBody.querySelectorAll("tr:not(#newBookingRow)");
    existingRows.forEach(row => tableBody.removeChild(row));

    jsonData.booking_lists.forEach(booking => {
        const newRow = createBookingRow(booking);
        tableBody.appendChild(newRow);
    });
}

function createBookingRow(booking) {
    const newRow = document.createElement("tr");

    newRow.innerHTML = `
        <td><input type="radio" name="selectedBooking" value="${booking.id}"></td>
        <td>${booking.time}</td>
        <td>${booking.description}</td>
        <td>${booking.location}</td>
        <td>${booking.interval} min</td>
        <td style="text-align: center;">${booking.available_slots}</td>
    `;

    return newRow;
}

function fetchLatestBookingListData(courseCode) {
    fetch(`/courses/${courseCode}/booking-lists`, {
        method: "GET",
        headers: {"Accept": "application/json"}
    })
        .then(handleResponse)
        .then(updateBookingListUI)
        .catch(handleError);
}

function handleError(error) {
    console.error("Error:", error);
    alert("An error occurred, please try again.");
}
