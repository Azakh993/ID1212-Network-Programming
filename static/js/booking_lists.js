document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("addBookingListBtn").addEventListener("click", function () {
        const newBookingRow = document.getElementById("newBookingRow");
        if (newBookingRow.style.display === "none") {
            newBookingRow.style.display = "table-row";
        } else {
            newBookingRow.style.display = "none";
        }
    });

    document.getElementById("newBookingRowSubmitBtn").addEventListener("click", function () {
        const courseCode = course_code
        const time = document.getElementById("inputTime").value;
        const description = document.getElementById("inputDescription").value;
        const location = document.getElementById("inputLocation").value;
        const length = document.getElementById("inputLength").value;
        const slots = document.getElementById("inputSlots").value;

        function updateBookingListUI(json_data) {
            const tableBody = document.querySelector("#bookingTable tbody");
            tableBody.innerHTML = "";

            json_data.booking_lists.forEach((booking) => {
                const newRow = document.createElement("tr");
                newRow.innerHTML = `
                        <td><input type="radio" name="selectedBooking" value="${booking.id}"></td>
                        <td>${booking.time}</td>
                        <td>${booking.description}</td>
                        <td>${booking.location}</td>
                        <td>${booking.interval} min</td>
                        <td style="text-align: center;">${booking.slots}</td>
                    `;
                tableBody.appendChild(newRow);
            });
        }

        fetch(`/${courseCode}/booking-lists`, {
            method: "PUT",
            body: JSON.stringify({time, description, location, length, slots}),
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => {
                if (response.status === 201) {
                    return response.json();
                } else {
                    alert("Failed to create booking list");
                }
            }).then((json_data) => {
            updateBookingListUI(json_data);
        })
            .catch((error) => {
                console.error("Error:", error);
            });
    });


    document.getElementById("selectBookingBtn").addEventListener("click", function () {
        const selectedBooking = document.querySelector("input[name='selectedBooking']:checked");
        if (selectedBooking) {
            var selectedBookingId = selectedBooking.value;
            alert("Selected booking ID: " + selectedBookingId);
        } else {
            alert("Please select a booking.");
        }
    });

    document.getElementById("showMyBookingsBtn").addEventListener("click", function () {
        // Handle showing user's bookings here
    });

    document.getElementById("removeBookingListBtn").addEventListener("click", function () {
        // Handle showing user's bookings here
    });
});
