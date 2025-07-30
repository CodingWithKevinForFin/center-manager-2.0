populateDefaultSSView();
// global vars
var navContainer = document.getElementById("ss-nav-container");
var ssContainer = document.getElementById("ss-container");
var ssWrapper = document.getElementById("ss");
var ssSearchbox = document.getElementById("ss-searchbox");
ssSearchbox.addEventListener("input", onSearchTermChange);
var navItems = document.getElementsByClassName("ss-nav-item");
for (navItem of navItems) {
	navItem.addEventListener("click", onSearchTermChange);
}

var $activeNavItem = $("#datasource");
var category = "datasource";
var prevSearchTerm = null;
var searchTerm = null;
var detailedViewClassName = "detailed-view";
var genericViewClassName = "generic-view";
var activeNavItemClassName = "active-nav-item";

var categoryId2Name = new Map();
for (var navItem of navContainer.children) {
	categoryId2Name.set(navItem.getAttribute("id"), navItem.textContent);
}

function onSearchTermChange() {
	var callingObject = event.target;
	$.getJSON("data/newsupported-software.json", function(data) {
		prevSearchTerm = searchTerm;
		searchTerm = ssSearchbox.value;
		if (callingObject.classList.contains("ss-nav-item")) { // user clicked on a nav item
			//searchTerm = null;
			//ssSearchbox.value = null;
			category = callingObject.getAttribute("id");
			$activeNavItem.removeClass(activeNavItemClassName); // disable the previous active nav
			$activeNavItem = $("#" + category);
			$activeNavItem.addClass("active-nav-item"); // enable on new one.
		}
		if (searchTerm)
			populateSS(filterByNameAndCategory(data, searchTerm, category));
		 else {
			$activeNavItem.addClass(activeNavItemClassName);
			populateSS(filterByCategory(data, category));
		}
	}).fail(function(error) {console.log("INVALID JSON: ", error);});
}
function filterByCategory(data, category) {
	var regExpCategory = new RegExp(`^${category}$`, 'gi');
	var resultSet = data.filter(function (element) {
		return element.category.match(regExpCategory);	
	});
	return resultSet;
}
function filterByNameAndCategory(data, name, category) {
	var regExpName = new RegExp(`${name}`, 'gi');
	var regExpCategory = new RegExp(`^${category}$`, 'gi');
	var resultSet =  data.filter(function (element) {
	 	try {
			return element.name.match(regExpName) && element.category.match(regExpCategory);	
		} catch (error) {
			// do nothing
		}
	});
	return resultSet;
}
// helper functions
function populateDefaultSSView() {
	$.getJSON("data/supported-software.json", function(data) {
		var matches = data.filter(function (element) {
			var regExpCategory = new RegExp(`^${category}$`, 'gi');
			return element.category.match(regExpCategory);
		});	
		populateSS(matches);
		$activeNavItem.addClass("active-nav-item");
	}).fail(function(error) {console.log("INVALID JSON: ", error);});
}
function populateSS(ssItems) {
	ssContainer.innerHTML = "";
	enableGenericView(ssContainer);
	//ssSearchbox.placeholder = "Search " + $activeNavItem.text();
	var animationDuration = 250; // miliseconds
	var animationHop = 50;
	
	if (ssItems.length == 0) { 
		var notFoundMessageContainer = createNotFoundMessage();
		notFoundMessageContainer.style.animation = "slide-up " +  animationDuration + "ms cubic-bezier(0.36, 0.62, 0, 1.38)";	
		ssContainer.appendChild(notFoundMessageContainer);
	} else {
		for (ssItem of ssItems) {
			var ssItemContainer = createSSItem(ssItem);
			ssItemContainer.style.animation = "slide-up " +  animationDuration + "ms cubic-bezier(0.36, 0.62, 0, 1.38)";
			ssContainer.appendChild(ssItemContainer);
			animationDuration += animationHop;
		}
	}
}
function createSSItem(ssItem) {
		var ssItemContainer = document.createElement("div");
		ssItemContainer.setAttribute("class", "ss-item-container-generic");
		
	 	var softwareName = document.createElement("h2"); // Adjust heading level as needed
    	softwareName.appendChild(document.createTextNode(ssItem.name));
	    softwareName.style.position = "absolute";
	    softwareName.style.top = "0";
	    softwareName.style.left = "0";
	    softwareName.style.width = "100%"; // Constrain the width to 100% of its containing element
	    softwareName.style.fontSize = "16px";
	    softwareName.style.whiteSpace = "nowrap"; // Prevent wrapping
	    softwareName.style.overflow = "hidden"; // Hide overflow text
	    softwareName.style.textOverflow = "ellipsis";
		
		var textNode = document.createElement("div");
		textNode.setAttribute("class", "ss-item-text-overlay");
		var text = document.createElement("div");
		text.setAttribute("class", "ss-item-text");
		text.appendChild(document.createTextNode(ssItem.name));
		textNode.appendChild(text);
		
		//testing
		
		var imgNode = document.createElement("div");
		imgNode.setAttribute("class", "ss-item-img");
		var img = document.createElement("img");
		img.setAttribute("src", ssItem.imgSrc);
		if (ssItem.imgSrc === "documentation/v5/assets/images/kafka.png" || ssItem.imgSrc === "documentation/v5/assets/images/logos-sp1.png") {
        	img.style.filter = "invert(1)";
    	}
		imgNode.appendChild(img);
		
		//ssItemContainer.addEventListener("mouseenter", onSSItemHover);
		//ssItemContainer.addEventListener("mouseleave", onSSItemMouseLeave);
		
		ssItemContainer.appendChild(softwareName);
		ssItemContainer.appendChild(imgNode);
		ssItemContainer.appendChild(textNode);
		
		return ssItemContainer;
}
function createNotFoundMessage() {
	var container = document.createElement("div");
	container.setAttribute("class", "not-found-container");
	
	var textContainer = document.createElement("div");
	textContainer.setAttribute("class", "not-found-text");
	textContainer.setAttribute("class", "barlowify");
	textContainer.setAttribute("style", "font-size: 25px !important");
	var msg = "Not seeing what you are looking for? Please contact us as it may be in development or on the roadmap.";
	var textNode = document.createTextNode(msg);
	textContainer.appendChild(textNode);
	
	/*var btnHolder = document.createElement("div");
    btnHolder.setAttribute("class", "btn-holder");
    var btn = document.createElement("button");
    btn.setAttribute("class", "btn btn-theme1 btn-large btn-info submitform4");
    btn.appendChild(document.createTextNode("Contact Us"));
    btn.addEventListener("click", function() {
        window.location.href = "contact.html"; // Redirect to contact.html when the button is clicked
    });
    btnHolder.appendChild(btn);*/
	
	container.appendChild(textContainer);
	//container.appendChild(btnHolder);
	return container;
}
function enableDetailedView(container) {
	if (container.classList.contains(detailedViewClassName))
		return;
	if (container.classList.contains(genericViewClassName))
		container.classList.remove(genericViewClassName);
	container.classList.add(detailedViewClassName);
}
function enableGenericView(container) {
	if (container.classList.contains(genericViewClassName))
		return;
	if (container.classList.contains(detailedViewClassName))
		container.classList.remove(detailedViewClassName);
	container.classList.add(genericViewClassName);
}
function populateSSWithDetails(ssItems) {
	ssContainer.innerHTML = "";
	enableDetailedView(ssContainer);
	var animationDuration = 250; // miliseconds
	var animationHop = 50;
	
	if (ssItems.length == 0) { 
		var notFoundMessageContainer = createNotFoundMessage();
		notFoundMessageContainer.style.animation = "slide-up " +  animationDuration + "ms cubic-bezier(0.36, 0.62, 0, 1.38)";	
		ssContainer.appendChild(notFoundMessageContainer);
	} else {
		for (ssItem of ssItems) {
			var ssItemContainer = createSSItemWithDetails(ssItem);
			ssItemContainer.style.animation = "slide-up " +  animationDuration + "ms cubic-bezier(0.36, 0.62, 0, 1.38)";
			ssContainer.appendChild(ssItemContainer);
			animationDuration += animationHop;
		}
	}
}
function createSSItemWithDetails(ssItem) {
	var ssItemContainer = document.createElement("div");
	ssItemContainer.setAttribute("class", "ss-item-container-detailed");
	
	var leftContainer = document.createElement("div");
	leftContainer.setAttribute("class", "ss-item-container-left");
	var rightContainer = document.createElement("div");
	rightContainer.setAttribute("class", "ss-item-container-right");
	ssItemContainer.append(leftContainer, rightContainer);
	
	// left container items
	var textNode = document.createElement("div");
	textNode.setAttribute("class", "ss-item-text-overlay");
	var text = document.createElement("div");
	text.setAttribute("class", "ss-item-text");
	text.appendChild(document.createTextNode(ssItem.name));
	textNode.appendChild(text);
	
	var imgNode = document.createElement("div");
	imgNode.setAttribute("class", "ss-item-img");
	var img = document.createElement("img");
	img.setAttribute("src", ssItem.imgSrc);
	imgNode.appendChild(img);
	
	leftContainer.append(imgNode, textNode);
	
	//right container items
	var categoryNode = document.createElement("div");
	categoryNode.setAttribute("class", "category-container");
	var categoryText = document.createElement("div");
	categoryText.setAttribute("class", "category-text");
	categoryText.innerHTML = "<b>Category: </b>" + categoryId2Name.get(ssItem.category);
	//categoryText.appendChild(document.createTextNode("Category: " + categoryId2Name.get(ssItem.category)));
	categoryNode.appendChild(categoryText);
	rightContainer.appendChild(categoryNode);
	
	// optional right container items
	if (ssItem.adapterFile) {
		var adapterFileNode = document.createElement("div");
		adapterFileNode.setAttribute("class", "adapter-file-container");
		var adapterFileText = document.createElement("div");
		adapterFileText.setAttribute("class", "adapter-file-text");
		adapterFileText.innerHTML = "<b>Adapter file: </b>" + ssItem.adapterFile;
		//adapterFileText.appendChild(document.createTextNode(ssItem.adapterFile));
		adapterFileNode.appendChild(adapterFileText);
		rightContainer.appendChild(adapterFileNode);
	}
	if (ssItem.officialPageAddress) {
		var officialPageNode = document.createElement("div");
		officialPageNode.setAttribute("class", "off-page-container");
		var officialPageText = document.createElement("div");
		officialPageText.setAttribute("class", "off-page-text");
		officialPageText.innerHTML = "<a href=" + ssItem.officialPageAddress +  ">Official Doc</a>";
		//officialPageText.appendChild(document.createTextNode(ssItem.officialPageAddress));
		officialPageNode.appendChild(officialPageText);
		rightContainer.appendChild(officialPageNode);
	}
	
	return ssItemContainer;
}