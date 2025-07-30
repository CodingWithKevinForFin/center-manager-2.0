function openCity1(evt, cityName) {
  var i, x, tablinks;
  x = document.getElementsByClassName("city1");
  for (i = 0; i < x.length; i++) {
    x[i].style.display = "none";
  }
  tablinks = document.getElementsByClassName("tablink1");
  for (i = 0; i < x.length; i++) {
    tablinks[i].className = tablinks[i].className.replace(" animborder", "");
  }
  document.getElementById(cityName).style.display = "block";
  evt.currentTarget.firstElementChild.className += " animborder";
}