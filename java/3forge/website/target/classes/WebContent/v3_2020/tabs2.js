function openCity2(evt, cityName2) {
  var i, x, tablinks;
  x = document.getElementsByClassName("city2");
  for (i = 0; i < x.length; i++) {
    x[i].style.display = "none";
  }
  tablinks = document.getElementsByClassName("tablink2");
  for (i = 0; i < x.length; i++) {
    tablinks[i].className = tablinks[i].className.replace(" animborder", "");
  }
  document.getElementById(cityName2).style.display = "block";
  evt.currentTarget.firstElementChild.className += " animborder";
}