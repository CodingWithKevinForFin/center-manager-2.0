$(document).ready(function() {

  if (document.getElementById("timecard")) {

    var clockInterval = setInterval(clock, 100);

  };

});

function clock() {

  var timeCard = document.getElementById('timecard');
  var today = new Date();

  // get the year (for the copyright)
  var year = today.getFullYear();

  // get the date string
  var date = today.toDateString().split(' ');
  date.pop();
  printDate = date.join(' ');

  // get hours
  var hour = today.getHours();
  var hourFormatted = hour % 12 || 12;

  // get minutes and format
  var minute = today.getMinutes();
  var minuteString = minute.toString();

  if (minuteString.length == 1) {
    if (minute == '0') {
      minute = '00';
    } else {
      minute = '0' + minute;
    }
  }

  // set background based on hour
  switch (hour) {
    case 1:
      timeCard.className = 'ev-gradient';
      break;

    case 2:
      timeCard.className = 'ev-gradient';
      break;

    case 3:
      timeCard.className = 'ev-gradient';
      break;

    case 4:
      timeCard.className = 'ev-gradient';
      break;

    case 5:
      timeCard.className = 'am-gradient';
      break;

    case 6:
      timeCard.className = 'am-gradient';
      break;

    case 7:
      timeCard.className = 'am-gradient';
      break;

    case 8:
      timeCard.className = 'am-gradient';
      break;

    case 9:
      timeCard.className = 'am-gradient';
      break;

    case 10:
      timeCard.className = 'am-gradient';
      break;

    case 11:
      timeCard.className = 'am-gradient';
      break;

    case 12:
      timeCard.className = 'pm-gradient';
      break;

    case 13:
      timeCard.className = 'pm-gradient';
      break;

    case 14:
      timeCard.className = 'pm-gradient';
      break;

    case 15:
      timeCard.className = 'pm-gradient';
      break;

    case 16:
      timeCard.className = 'pm-gradient';
      break;

    case 17:
      timeCard.className = 'pm-gradient';
      break;

    case 18:
      timeCard.className = 'ev-gradient';
      break;

    case 19:
      timeCard.className = 'ev-gradient';
      break;

    case 20:
      timeCard.className = 'ev-gradient';
      break;

    case 21:
      timeCard.className = 'ev-gradient';
      break;

    case 22:
      timeCard.className = 'ev-gradient';
      break;

    case 23:
      timeCard.className = 'ev-gradient';
      break;

    default:
      timeCard.className = 'am-gradient';
      break;

  }

  // format the clock
  var currentTime = hourFormatted + ':' + minute;

  document.getElementById('clock').textContent = currentTime;
  document.getElementById('date').textContent = printDate;


};

function showForm() {

  var aboutYou = document.getElementById('about-you');
  var aboutYouForm = document.getElementById('about-you-form');

  aboutYou.style.display = 'none';
  aboutYouForm.style.display = 'block';

};

if (document.getElementById("update-about-info")) {

  var saveNewPasswordBtn = document.getElementById('save-new-password');
  var saveNewInfoBtn = document.getElementById('save-new-info');
  var okayMsg = document.getElementById('okay');

  function showError(text){

    document.getElementById('error').innerHTML=text;
    saveNewPasswordBtn.disabled = false;
    saveNewPasswordBtn.textContent = "Save changes";
    okayMsg.innerHTML=" ";

  };


  function passwordChanged(){
    showError('');
    okayMsg.innerHTML="Password Has been Changed";
    document.getElementById('form').reset();
    saveNewPasswordBtn.disabled = false;
    saveNewPasswordBtn.textContent = "Save changes";
  };

  function onChangeAccountResponse(success,msg){

    if(success){

      document.getElementById('uaokay').innerHTML="Account Updated";
      document.getElementById('uaerror').innerHTML="";
      document.getElementById('fname').className = 'input-changed';
      document.getElementById('fname').disabled = true;
      document.getElementById('lname').className = 'input-changed';
      document.getElementById('lname').disabled = true;
      document.getElementById('company').className = 'input-changed';
      document.getElementById('company').disabled = true;
      document.getElementById('role').className = 'input-changed';
      document.getElementById('role').disabled = true;
      document.getElementById('phone').className = 'input-changed';
      document.getElementById('phone').disabled = true;
      saveNewInfoBtn.style.display = 'none';

    } else{

      document.getElementById('uaokay').innerHTML="";
      document.getElementById('uaerror').innerHTML=msg;
      saveNewInfoBtn.disabled = false;
      saveNewInfoBtn.textContent = "Update information";
    }

  }

  function disableBtn() {
    saveNewInfoBtn.disabled = true;
    saveNewInfoBtn.textContent = "Saving...";
  }

  function disableBtn2() {
    saveNewPasswordBtn.disabled = true;
    saveNewPasswordBtn.textContent = "Saving...";
  }

  document.getElementById('update-about-info').addEventListener('click', showForm);
};

function onContactSupportResponse(success,msg){

  var errorMsg = document.getElementById('csError');
  var submitBtn = document.getElementById("contactSubmit");
  var inputs = document.getElementsByClassName('contactFormInput');
  var contactDelayMsg = document.getElementById('contactDelayMsg');

  if (success == 'sending') {

    //set message timeout
    setTimeout(showDelayMsg, 5000);
    setTimeout(updateDelayMsg, 10000);

    // clear error message
    errorMsg.innerHTML = ' ';
    errorMsg.style.color = 'blue';

    // change button
    submitBtn.disabled = true;
    submitBtn.textContent = "Sending...";

    // disable inputs
    for (var i = 0; i < inputs.length; i++) {
        inputs[i].disabled = true;
    }

  } else if (success == true) {

    // show confirmation modal
    $('#contactFormModal').modal('hide');
    $('#submitConfirmation').modal('show');

    // display ticket number
    errorMsg.innerHTML="Message #" + msg + ":";

    // revert button text
    submitBtn.textContent = "Contact Us";

  } else {

    // show error
    errorMsg.style.color = 'red';
    errorMsg.innerHTML = msg;

    // enable inputs
    for (var i = 0; i < inputs.length; i++) {
        inputs[i].disabled = false;
    }

    // revert button
    submitBtn.disabled = false;
    submitBtn.textContent = "Contact Us";

  }

  // show delay message
  function showDelayMsg() {

    contactDelayMsg.textContent = "If you are uploading attachments, this may take a while. Please do not refresh the page."

    if (errorMsg.innerHTML == ' ') {
      $('#contactDelayMsg').css('display', 'block');
    } else {
      $('#contactDelayMsg').css('display', 'none');
    }

  };

  // update delay message
  function updateDelayMsg() {

    contactDelayMsg.textContent = "An error may have ocurred with the form. To ensure your message reaches our support team, please email us directly at support@3forge.com"

  }

};

function onRequestQuoteResponse(success,msg){
  var errorMsg = document.getElementById('rqError');
  var submitBtn = document.getElementById("requestSubmit");
  var inputs = document.getElementsByClassName('quoteFormInput');
  var quoteDelayMsg = document.getElementById('quoteDelayMsg');

  if (success == 'sending') {

    //set message timeout
    setTimeout(showDelayMsg, 5000);

    // clear error msg
    errorMsg.innerHTML = ' ';
    errorMsg.style.color = 'blue';

    // change button
    submitBtn.disabled = true;
    submitBtn.textContent = "Requesting...";

    // disable inputs
    for (var i = 0; i < inputs.length; i++) {
        inputs[i].disabled = true;
    }

  } else if (success == true) {

    // show confirmation modal
    $('#purchaseModal').modal('hide');
    $('#submitConfirmation').modal('show');

    // display ticket number
    errorMsg.innerHTML = "Request #" + msg + ":";

    // revert button text
    submitBtn.textContent = "Request my free quote";

  } else {

    // show error
    errorMsg.style.color = 'red';
    errorMsg.innerHTML = msg;

    // revert button
    submitBtn.disabled = false;
    submitBtn.textContent = "Request my free quote";

    // enable inputs
    for (var i = 0; i < inputs.length; i++) {
        inputs[i].disabled = false;
    }

  }

  // show delay message
  function showDelayMsg() {

    if (errorMsg.innerHTML == ' ') {
      $('#quoteDelayMsg').css('display', 'block');
    } else {
      $('#quoteDelayMsg').css('display', 'none');
    }

  };
};
