var modal = document.getElementById("product-img-modal");
var modalImg = document.getElementById("modal-img");
var captionText = document.getElementById("modal-caption");
function onProductImgClick(){
	var clickedImg = event.target;
	modal.style.display = "block";
	modalImg.src = clickedImg.src;
	captionText.innerHTML = clickedImg.alt;
}
function closeModal() {
	if (event.target.getAttribute("id") == "product-img-modal" || event.target.getAttribute("id") == "modal-close-btn")
		modal.style.display = "none";
}