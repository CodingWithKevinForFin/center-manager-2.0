// set variables
var nums = document.querySelectorAll('h4');
var num1, num2, num3, num404;
var timer1, timer2, timer3, timer404;

// anvil numbers animation
function calculations() {
  var number = parseInt(num1.textContent);
  if (number < 1000) {
    num1.textContent = (number + 1 + "B");
  } else {
    clearInterval(timer1);
    num1.textContent = ("1T+");
  }
}

function orders() {
  var number = parseInt(num2.textContent);
  if (number < 100) {
    num2.textContent = (number + 1 + "M");
  } else {
    clearInterval(timer2);
    num2.textContent = ("100M");
  }
}

function events() {
  var number = parseInt(num3.textContent);
  if (number < 1000) {
    num3.textContent = (number + 1 + "K");
  } else {
    clearInterval(timer3);
    num3.textContent = ("1M");
  }
}

// 404 page animation
function errorPage() {
  var number = parseInt(num404.textContent);
  if (number < 404) {
    num404.textContent = (number + 1);
  } else {
    clearInterval(timer404);
  }
}

function startTimer() {

  // determine if anivl or 404 page

  if (nums.length == 3) {

    // define variables
    num1 = nums[0];
    num2 = nums[1];
    num3 = nums[2];

    try { // try the animation

        timer1 = setInterval(calculations, 20);
        timer2 = setInterval(orders, 20);
        timer3 = setInterval(events, 20);

    } catch (e) { // write in numbers manually in event of error
        console.log('error');
        num1.innerHTML = "1T+";
        num2.innerHTML = "100M";
        num3.innerHTML = "1M";

    } finally {
        num1.className = "full";
        num2.className = "full";
        num3.className = "full";
    }

  } else { //if 404

    num404 = nums[0];

    try { // try the animation

        timer404 = setInterval(errorPage, 20);

    } catch (e) { // write in 404 in event of an error
        console.log('error');
        num404.innerHTML = "404";

    } finally {
        num404.className = "full";
      }

  }

}



window.addEventListener('load', startTimer);
// window.addEventListener('load', appear);
