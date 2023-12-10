document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("addBookingListBtn").addEventListener("click", function () {
        const newBookingRow = document.getElementById("newBookingRow");
        if (newBookingRow.style.display === "none") {
            newBookingRow.style.display = "table-row";
        } else {
            newBookingRow.style.display = "none";
        }
    });

    document.getElementById("selectBookingBtn").addEventListener("click", function () {
        const selectedBooking = document.querySelector("input[name='selectedBooking']:checked");
        if (selectedBooking) {
            var selectedBookingId = selectedBooking.value;
            // Perform an action with the selected booking
            alert("Selected booking ID: " + selectedBookingId);
        } else {
            alert("Please select a booking.");
        }
    });

    document.getElementById("showMyBookingsBtn").addEventListener("click", function () {
        // Handle showing user's bookings here
    });
});
