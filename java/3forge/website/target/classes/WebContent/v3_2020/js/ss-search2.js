// Initialize the view
populateDefaultSSView();

// Global variables
var ssContainer = document.getElementById("ss-container");
var ssSearchbox = document.getElementById("ss-searchbox");
ssSearchbox.addEventListener("input", onSearchTermChange);

var prevSearchTerm = null;
var searchTerm = null;

// This is no longer necessary as we're not switching between categories
// var category = "datasource"; 

// Event listener for search input
function onSearchTermChange() {
    $.getJSON("data/supported-software2.json", function(data) {
        prevSearchTerm = searchTerm;
        searchTerm = ssSearchbox.value;
        
        if (searchTerm) {
            populateSS(filterByName(data, searchTerm));
        } else {
            populateSS(data); // Display all items if no search term is entered
        }
    }).fail(function(error) {
        console.log("INVALID JSON: ", error);
    });
}

// Filter by name function, category filtering is removed
function filterByName(data, name) {
    var regExpName = new RegExp(`${name}`, 'gi');
    var resultSet = data.filter(function (element) {
        try {
            return element.name.match(regExpName);
        } catch (error) {
            // do nothing
        }
    });
    return resultSet;
}

// Helper functions
function populateDefaultSSView() {
    $.getJSON("data/supported-software2.json", function(data) {
        populateSS(data); // Populate with all items by default
    }).fail(function(error) {
        console.log("INVALID JSON: ", error);
    });
}

function populateSS(ssItems) {
    ssContainer.innerHTML = "";
    enableGenericView(ssContainer);

    var animationDuration = 250; // milliseconds
    var animationHop = 50;

    if (ssItems.length == 0) {
        var notFoundMessageContainer = createNotFoundMessage();
        notFoundMessageContainer.style.animation = "slide-up " + animationDuration + "ms cubic-bezier(0.36, 0.62, 0, 1.38)";
        ssContainer.appendChild(notFoundMessageContainer);
    } else {
        for (var ssItem of ssItems) {
            var ssItemContainer = createSSItem(ssItem);
            ssItemContainer.style.animation = "slide-up " + animationDuration + "ms cubic-bezier(0.36, 0.62, 0, 1.38)";
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
    softwareName.style.textAlign = "center";
    softwareName.setAttribute("id", ssItem.id);
    softwareName.setAttribute("class", "ss-item-text-label");
    softwareName.style.transition = "opacity 0.1s ease-in";

    var textNode = document.createElement("div");
    textNode.setAttribute("class", "ss-item-text-overlay");
    var prefix = "ol";
    textNode.setAttribute("id", prefix.concat(ssItem.id));
    var text = document.createElement("div");
    text.setAttribute("class", "ss-item-text");
    text.appendChild(document.createTextNode(ssItem.name));
    textNode.appendChild(text);

    var imgNode = document.createElement("div");
    imgNode.setAttribute("class", "ss-item-img");
    var img = document.createElement("img");
    img.setAttribute("src", ssItem.imgSrc);
    img.setAttribute('loading', 'lazy');
    img.setAttribute('alt', ssItem.name);
    imgNode.appendChild(img);
    
	ssItemContainer.appendChild(softwareName);
    ssItemContainer.appendChild(imgNode);
    ssItemContainer.appendChild(textNode);
    
     // Toggle visibility of the label when overlay is shown
    textNode.addEventListener("mouseenter", function() {
        var labelId = ssItem.id.replace(prefix, ""); // Remove 'ol' prefix
        var labelElement = document.getElementById(labelId);
        if (labelElement) {
            labelElement.style.visibility = "hidden";
        }
    });

    textNode.addEventListener("mouseleave", function() {
        var labelId = ssItem.id.replace(prefix, ""); // Remove 'ol' prefix
        var labelElement = document.getElementById(labelId);
        if (labelElement) {
            labelElement.style.visibility = "visible";
        }
    });

    return ssItemContainer;
}

function createNotFoundMessage() {
    var container = document.createElement("div");
    container.setAttribute("class", "not-found-container");

    var textContainer = document.createElement("div");
    textContainer.setAttribute("class", "not-found-text");
    var msg = "Not seeing what you are looking for? Please contact us as it may be in development or on the roadmap.";
    var textNode = document.createTextNode(msg);
    textContainer.appendChild(textNode);

    var btnHolder = document.createElement("div");
    btnHolder.setAttribute("class", "btn-holder");
    var btn = document.createElement("a");
    btn.setAttribute("class", "btn xl");
    btn.setAttribute("href", "contact.html");
    btn.appendChild(document.createTextNode("Contact Us"));
    btnHolder.appendChild(btn);

    container.appendChild(textContainer);
    container.appendChild(btnHolder);
    return container;
}

function enableGenericView(container) {
    container.classList.remove("detailed-view");
    container.classList.add("generic-view");
}

// Detailed view functions are not needed for this version
// function populateSSWithDetails(ssItems) { ... }
// function createSSItemWithDetails(ssItem) { ... }
