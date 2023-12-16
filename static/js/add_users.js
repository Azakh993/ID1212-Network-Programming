document.addEventListener("DOMContentLoaded", () => {
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById("addUsersBtn").addEventListener("click", addUsers);
    document.getElementById("backBtn").addEventListener("click", backToBookingList);

}

function addUsers() {
    const usernames = document.getElementById("usernames").value;
    const password = document.getElementById("password").value;
    const elevated_privileges = document.getElementById("elevated-privileges").value;
    const courseCode = course_code;

    const requestOptions = {
        method: "POST",
        body: JSON.stringify({usernames: usernames, password: password, elevated_privileges: elevated_privileges}),
        headers: {"Content-Type": "application/json"}
    };

    fetch(`/courses/${courseCode}/add-users`, requestOptions)
        .then(handleResponse)
        .catch(handleError);
}

function backToBookingList() {
    const courseCode = course_code;
    window.location.href = `/courses/${courseCode}/booking-lists`;
}

function handleResponse(response) {
    const courseCode = course_code;
    console.log("Response: ", response);
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

function handleError(error) {
    console.error("Error:", error);
    alert("An error occurred, please try again.");
}

