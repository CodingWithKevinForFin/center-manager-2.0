// Function to set a custom cookie indicating the user's choice regarding cookies
function setCookieHubCookie(value) {
    // Set a cookie named 'cookiehub' with the provided value
    document.cookie = 'cookiehub=' + value + '; expires=' + calculateExpirationDate() + '; path=/';
}

// Function to calculate the expiration date for the cookie (30 days from the current day)
function calculateExpirationDate() {
    var currentDate = new Date();
    var expirationDate = new Date(currentDate.getTime() + (30 * 24 * 60 * 60 * 1000)); // 30 days in milliseconds
    return expirationDate.toUTCString();
}

// Check if the custom cookie indicating the user's choice regarding cookies is set
window.addEventListener('load', function() {
    var cookieValue = getCookie('cookiehub');
    if (!cookieValue) {
        // If the custom cookie is not set, display the CookieHub banner
        var cpm = {};
        window.cookiehub.load(cpm);
    }
});

// Set the custom cookie when the "Accept All Cookies" or "Deny All Cookies" button is clicked
document.addEventListener('click', function(event) {
    var target = event.target;
    if (target.classList.contains('ch2-allow-all-btn')) {
        // Set the custom cookie to indicate acceptance of cookies
        setCookieHubCookie('allow');
    } else if (target.classList.contains('ch2-deny-all-btn')) {
        // Set the custom cookie to indicate denial of all cookies
        setCookieHubCookie('deny');
    }
});

var count = 0; 
function openCookieHubWindow() {
	if (count == 0) {
		window.cookiehub.load();
		count++;
	}
	if (count >= 1) {			
		window.cookiehub.openDialog();
		count++;
	}
}

cookiesets = document.getElementById("cookiesettings");

cookiesets.addEventListener("click", function(event){
	event.preventDefault();
	openCookieHubWindow(); 
	return false;
});