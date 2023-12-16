// noinspection JSUnresolvedReference

document.addEventListener("DOMContentLoaded", () => {
    const socket = setupWebSocketListeners();
    setupEventListeners(socket);
});

function setupWebSocketListeners() {
    const socket = io.connect('http://' + window.location.hostname + ':' + location.port);

    socket.on('update_booking_slots', function (data) {
        fetchLatestSlotsListData(course_code);
    });

    return socket;
}

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
        body = JSON.stringify({username: username})
    } else {
        body = JSON.stringify({})
    }

    const URI = `/courses/${courseCode}/booking-lists/${selectedBookingListID}/bookable-slots/${selectedSlotSequenceID}`
    const requestOptions = setRequestOptions(method, body)
    sendRequest(socket, URI, requestOptions)
}

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

function getSelectedSlotSequenceID() {
    const selectedSlot = document.querySelector("input[name='selectedSlot']:checked");

    if (!selectedSlot) {
        alert("Please select a slot.");
        return;
    }

    return selectedSlot.value;
}

function verifySlotAvailability(selectedSlotSequenceID) {
    const slotAvailability = document.querySelector(
        `input[name='selectedSlot'][value='${selectedSlotSequenceID}']`
    ).getAttribute('data-availability');

    return slotAvailability !== "Booked";
}

function setRequestOptions(method, requestBody) {
    return {
        method: method,
        body: requestBody,
        headers: {"Content-Type": "application/json"}
    };
}

function sendRequest(socket, URI, requestOptions) {
    fetch(URI, requestOptions)
        .then((response) => handleResponse(response, socket))
        .catch(handleError);
}

function backToBookingList() {
    const courseCode = course_code;
    window.location.href = `/courses/${courseCode}/booking-lists`;
}

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

function updateSlotsListUI(jsonData) {
    const tableBody = document.querySelector("#slotsTable tbody");

    const existingRows = tableBody.querySelectorAll("tr");
    existingRows.forEach(row => tableBody.removeChild(row));

    jsonData.available_slots.forEach(slot => {
        const newRow = createSlotRow(slot);
        tableBody.appendChild(newRow);
    });
}

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

function fetchLatestSlotsListData(courseCode) {
    fetch(`/courses/${courseCode}/booking-lists/${booking_list_id}/bookable-slots`, {
        method: "GET",
        headers: {"Accept": "application/json"}
    })
        .then(handleResponse)
        .then(updateSlotsListUI)
        .catch(handleError);
}

function handleError(error) {
    console.error("Error:", error);
    alert("An error occurred, please try again.");
}

