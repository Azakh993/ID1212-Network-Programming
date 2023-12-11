document.addEventListener("DOMContentLoaded", () => {
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById("bookBtn").addEventListener("click", bookSelectedSlot);
    document.getElementById("backBtn").addEventListener("click", backToBookingList);

}

function bookSelectedSlot() {
    const selectedBookingListID = booking_list_id;
    const selectedSlot = document.querySelector("input[name='selectedSlot']:checked");
    if (!selectedSlot) {
        alert("Please select a slot.");
        return;
    }

    const selectedSlotSequenceID = selectedSlot.value;
    const courseCode = course_code;

    const slotAvailability = document.querySelector(
        `input[name='selectedSlot'][value='${selectedSlotSequenceID}']`
    ).getAttribute('data-availability');

    if (slotAvailability === "Booked") {
        alert("This slot is not available.");
        return;
    }

    const requestOptions = {
        method: "PUT",
        body: JSON.stringify({slot_sequence_id: selectedSlotSequenceID}),
        headers: {"Content-Type": "application/json"}
    };

    fetch(`/courses/${courseCode}/booking-lists/${selectedBookingListID}/bookable-slots`, requestOptions)
        .then(handleResponse)
        .then(() => {
            alert("Slot booked successfully.");
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
        return fetchLatestSlotsListData(courseCode)
    }

    throw new Error("Request failed: " + response.status);
}

function updateSlotsListUI(jsonData) {
    const tableBody = document.querySelector("#slotsTable tbody");

    const existingRows = tableBody.querySelectorAll("tr");
    existingRows.forEach(row => tableBody.removeChild(row));

    console.log(jsonData)


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
        ${admin ? `
            <td>${slot.username}</td>
            <td><input type="text" id="inputUsername" placeholder="Username" style="width: 100%;"></td>
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

