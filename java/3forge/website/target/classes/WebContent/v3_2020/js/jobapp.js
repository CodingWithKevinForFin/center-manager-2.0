subm = document.getElementById("submitapp");

function formValidate(formid, cssStyle = "error-message text-red-500 text-sm", errorMsg = "Please complete this required field") {
    const form = document.getElementById(formid);
    const inputs = form.querySelectorAll("input[required]");
    const selects = form.querySelectorAll("select[required]");

	function validateField(field) {
	    let errorMessage = field.nextElementSibling;
	    
	    if (!errorMessage || !errorMessage.classList.contains("error-message")) {
	        errorMessage = document.createElement("span");
	        errorMessage.innerHTML = "Please complete this required field";
	        errorMessage.classList.add("error-message", "text-red-500", "text-sm", "hidden");
	        field.parentNode.insertBefore(errorMessage, field.nextSibling);
	    }
	
	    if (field.tagName === "SELECT") {
	        // Ensure the user selects an actual option, not the default placeholder
	        if (!field.value || field.value === "--" || field.value === "Please select") {  
	            errorMessage.classList.remove("hidden");
	            return false;
	        }
	    } else if (!field.value.trim()) {
	        errorMessage.classList.remove("hidden");
	        return false;
	    }
	
	    errorMessage.classList.add("hidden");
	    return true;
	}

    inputs.forEach(input => {
        input.addEventListener("blur", () => validateField(input));
    });

    selects.forEach(select => {
        select.addEventListener("blur", () => validateField(select));
    });

    form.addEventListener("submit", (event) => {
	    let isValid = true;
	    let invalidFields = [];
	
	    inputs.forEach(input => {
	        if (!validateField(input)) {
	            isValid = false;
	            invalidFields.push(input.name || input.id || "Unnamed Input");
	        }
	    });
	
	    selects.forEach(select => {
	        if (!validateField(select)) {
	            isValid = false;
	            invalidFields.push(select.name || select.id || "Unnamed Select");
	        }
	    });
	
	    form.setAttribute('status', isValid ? 'valid' : 'invalid');
	
	    if (!isValid) {
	        event.preventDefault();
	        console.log("Form is invalid at submission. The following fields are missing values:");
	        console.log(invalidFields);
	    } else {
	        console.log("Form is valid at submission");
	    }
	});
}

function checkFormStatus(form) {
    const inputs = form.querySelectorAll("input[required]");
    const selects = form.querySelectorAll("select[required]");
    let isValid = true;
    let invalidFields = [];

    inputs.forEach(input => {
        if (!input.value.trim()) {
            isValid = false;
            invalidFields.push(input.name || input.id || "Unnamed Input");
        }
    });

    selects.forEach(select => {
        if (!select.value || select.value === "--" || select.value === "Please select") {  
            isValid = false;
            invalidFields.push(select.name || select.id || "Unnamed Select");
        }
    });

    form.setAttribute('status', isValid ? 'valid' : 'invalid');
    return invalidFields;
}

formValidate("form");

function thanksRedirect() {
	window.location.href = "thanks";
}

let validSubmit = false;

function validateForm(formid) {
    const form = document.getElementById(formid);
    const generalError = document.getElementById("general-error");
    
    checkFormStatus(form);
    
    const formStatus = form.getAttribute("status");

    console.log("general Error:", generalError);
    console.log("form:", form);
    console.log("form status:", formStatus);

    if (formStatus === "invalid") {
        console.log("testing invalid", formStatus);
        generalError.classList.remove("hidden");
        return false;
    } else {
        console.log("testing valid", formStatus);
        generalError.classList.add("hidden");
        return true;
    }
}

function submitApplicationMulti(event) {
    event.preventDefault();
    const generalError = document.getElementById("general-error");
    
    const form = document.getElementById("form");
    
    // Run validation check and get invalid fields
    const invalidFields = checkFormStatus(form);
    
    if (form.getAttribute("status") === "invalid") {
        console.log("Form is invalid, preventing submission");
        generalError.classList.remove("hidden");
        console.log("Invalid fields:", invalidFields);
        return false;
    }

    console.log("Form is valid, proceeding with submission");
    generalError.classList.add("hidden");
    validSubmit = true;

    if (validSubmit && ajaxNowFiles("submitApplication", "form")) {
        thanksRedirect();
    }
}

subm.addEventListener("click", (event) => {
	console.log("test");
	submitApplicationMulti(event);
});

document.addEventListener('DOMContentLoaded', function() {
	const otherCheckbox1 = document.getElementById('other-checkbox1');
	const otherCheckbox2 = document.getElementById('other-checkbox2');
	const otherCheckbox3 = document.getElementById('other-checkbox3');
	const otherTextboxContainer1 = document.getElementById('other-textbox-container1');
	const otherTextboxContainer2 = document.getElementById('other-textbox-container2');
	const otherTextboxContainer3 = document.getElementById('other-textbox-container3');
	const otherTextbox1 = document.getElementById('other-textbox1');
	const otherTextbox2 = document.getElementById('other-textbox2');
	const otherTextbox3 = document.getElementById('other-textbox3');

    // Toggle the display of the textbox when the checkbox is clicked
    otherCheckbox1.addEventListener('change', function() {
        if (this.checked) {
            otherTextboxContainer1.classList.remove('hidden');
            otherTextbox1.focus(); // Focus on the textbox when it appears
        } else {
            otherTextboxContainer1.classList.add('hidden');
            otherTextbox1.value = ''; // Clear the textbox when checkbox is unchecked
        }
    });

    // Update the value attribute based on the textbox input
    otherTextbox2.addEventListener('input', function() {
        otherCheckbox2.value = this.value;
    });
    
    	    otherCheckbox2.addEventListener('change', function() {
        if (this.checked) {
            otherTextboxContainer2.classList.remove('hidden');
            otherTextbox2.focus(); // Focus on the textbox when it appears
        } else {
            otherTextboxContainer2.classList.add('hidden');
            otherTextbox2.value = ''; // Clear the textbox when checkbox is unchecked
        }
    });

    // Update the value attribute based on the textbox input
    otherTextbox2.addEventListener('input', function() {
        otherCheckbox2.value = this.value;
    });
    
    	    otherCheckbox3.addEventListener('change', function() {
        if (this.checked) {
            otherTextboxContainer3.classList.remove('hidden');
            otherTextbox3.focus(); // Focus on the textbox when it appears
        } else {
            otherTextboxContainer3.classList.add('hidden');
            otherTextbox3.value = ''; // Clear the textbox when checkbox is unchecked
        }
    });

    // Update the value attribute based on the textbox input
    otherTextbox3.addEventListener('input', function() {
        otherCheckbox3.value = this.value;
    });
});


// Function to ensure only one checkbox per category is selected
function handleCheckboxSelection(name) {
    const checkboxes = document.querySelectorAll(`input[name="${name}"]`);
    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener("change", () => {
            if (checkbox.checked) {
                checkboxes.forEach((otherCheckbox) => {
                    if (otherCheckbox !== checkbox) {
                        otherCheckbox.checked = false;
                    }
                });
            }
        });
    });
}

handleCheckboxSelection("cat1");
handleCheckboxSelection("cat2");
handleCheckboxSelection("cat3");
handleCheckboxSelection("cat4");
handleCheckboxSelection("cat5");
handleCheckboxSelection("cat6");