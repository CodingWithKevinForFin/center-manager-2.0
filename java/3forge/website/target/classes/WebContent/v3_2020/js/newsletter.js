(function() {
    // https://dashboard.emailjs.com/admin/account
    emailjs.init({
      publicKey: "I3dJ3vzRikpLEq5W-",
    });
})();

window.onload = function() {
    document.getElementById('email-signup').addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent the default form submission

        var emailInput = document.getElementById('EMBED_FORM_EMAIL_LABEL');
        var email = emailInput.value;
        var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        	        
        var honeypot3 = document.querySelector('input[name="username"]').value;
 	  	var honeypot4 = document.querySelector('input[name="state"]').value;
 	  	
  		if (honeypot3 || honeypot4) {
            alert('Spam detected.');
            return false;
      	}

        if (!emailPattern.test(email)) {
            emailInput.value = 'Invalid email entered.';
        } else {

            emailjs.sendForm('newsletter_service', 'newsletter_form', this)
                .then(() => {
                    console.log('SUCCESS!');
                    window.location.href = "thanks";
                }, (error) => {
                    console.log('FAILED...', error);
                    alert('There was an error with your submission. Please try again.');
                });
        }
    });
}