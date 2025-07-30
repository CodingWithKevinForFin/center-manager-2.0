var faqSearchBox = document.getElementById("faq-searchbox");
faqSearchBox.addEventListener("keypress", onKeyPress);
var mainContainer = document.getElementById("faq-container");
var searchHighlightBgColor = "#fcff65";
var searchHighlightFgColor = "#444242";

populateCategories();

function reset() {
	faqSearchBox.value = "";
	populateCategories();
}
function onKeyPress() {
	if (event.key == "Enter") {
		var searchTerm = document.getElementById("faq-searchbox").value;
		if (searchTerm.trim() != "") {
			$.getJSON("data/faqs.json", function(data) {
				var results = searchFaq(searchTerm, data);
				if (results.length > 0)
					displaySearchResults(results, searchTerm);
				else
					mainContainer.innerHTML = "<div class='instructions'> Could not find any relevant information. Contact <i><b>support@3forge.com</b></i> if you have any specific questions.</span><span onclick=reset() id='show-categories-btn'>Back to Categories</span>";
			});
		} 
		else {
			populateCategories();
		}
	}
	
}
function displaySearchResults(results, searchTerm) {
	mainContainer.innerHTML = "";
	var regExpTerm = new RegExp(`${searchTerm}`, 'gi');
	var highlightStart = "<span style='background:" + searchHighlightBgColor + ";color:" + searchHighlightFgColor + "'>";
	var highlightStartLen = highlightStart.length;
	var highlightEnd = "</span>";
	var highlightEndLen = highlightEnd.length;
	for (result of results) {
		var resultContainer = document.createElement("div");
		resultContainer.setAttribute("class", "search-result-container");
		
		var categoryDiv = document.createElement("div");
		var questionDiv = document.createElement("div");
		var answerDiv = document.createElement("div");
		var linkDiv = null;
		
		categoryDiv.setAttribute("class", "category-div");
		questionDiv.setAttribute("class", "faq-question");
		answerDiv.setAttribute("class", "faq-answer");
		
		var questionHtml = result.question + "<span class='category-div'>" + result.category + "</span>";
		var answerHtml = result.answer;
		for (var match of result.matches) { 
			if (match.key == "question") {
				var before = questionHtml.slice(0, match.start);
				var middle = questionHtml.slice(match.start, match.end);
				var after = questionHtml.slice(match.end);
				questionHtml = before + highlightStart + middle + highlightEnd + after;
				break; //TODO: allow for multiple highlights
			}
		}
		for (var match of result.matches) { 
			if (match.key == "answer") {
				var before = answerHtml.slice(0, match.start);
				var middle = answerHtml.slice(match.start, match.end);
				var after = answerHtml.slice(match.end);
				answerHtml = before + highlightStart + middle + highlightEnd + after;
				break; //TODO: allow for multiple highlights 
			}
		}
		categoryDiv.innerHTML=result.category;
		questionDiv.innerHTML=questionHtml;
		answerDiv.innerHTML=answerHtml;
		//resultContainer.appendChild(categoryDiv);
		resultContainer.appendChild(questionDiv);
		resultContainer.appendChild(answerDiv);
		if (result.doclink) {
			var linkDiv = document.createElement("a");
			linkDiv.setAttribute("href", result.doclink);
			linkDiv.setAttribute("class", "faq-link");
			linkDiv.setAttribute("target", "_blank");
			linkDiv.appendChild(document.createTextNode("Read More"));
			
			/*
			linkDiv = document.createElement("div");
			linkDiv.setAttribute("class", "link-div");
			linkDiv.innerHTML=result.doclink;
			*/
			resultContainer.appendChild(linkDiv);
		}
		mainContainer.appendChild(resultContainer);
	}
}
function searchFaq(searchTerm, data) {
	var regExpTerm = new RegExp(`${searchTerm}`, 'gi');
	var resultSet =  data.filter(function (element) {
	 	try {
	 		var matches = [];
	 		while (null !== (match = regExpTerm.exec(element.question))) {
	 			var obj = {};
	 			obj.key = "question";
	 			obj.start = match.index
	 			obj.end = regExpTerm.lastIndex;
	 			matches.push(obj);
	 		}
	 		while (null !== (match = regExpTerm.exec(element.answer))) {
	 			var obj = {};
	 			obj.key = "answer";
	 			obj.start = match.index
	 			obj.end = regExpTerm.lastIndex;
	 			matches.push(obj);
	 		}
	 		if (matches.length > 0) {
	 			element.matches = matches;
	 			return element;
	 		}
		} catch (error) {
			// do nothing
		}
	});
	return resultSet;
}
function populateCategories() {
	mainContainer.innerHTML = "";
	$.getJSON("data/faqs.json", function(data) {
		var categories = new Set();
		data.map(function (element) {
			if (!categories.has(element.category))
				categories.add(element.category);
		});
		var catList = Array.from(categories);
		createCategories(catList);
	}).fail(function(error) {console.log("INVALID JSON: ", error);});
}
function createCategories(catList) {
	for (var cat of catList) {
		var catContainer = document.createElement("div");
		catContainer.setAttribute("class", "category");
		catContainer.setAttribute("id", cat);
		
		var catMinimalInfoContainer = document.createElement("div");
		catMinimalInfoContainer.setAttribute("class", "catMinimalInfoContainer");
		
		var left = document.createElement("div");
		var right = document.createElement("div");
		left.setAttribute("class", "catLeft");
		left.setAttribute("clickid", cat);
		left.setAttribute("shouldExpand", true);
		right.setAttribute("class", "catRight categoryName");
		right.appendChild(document.createTextNode(cat));
		catMinimalInfoContainer.appendChild(left);
		catMinimalInfoContainer.appendChild(right);
		catContainer.appendChild(catMinimalInfoContainer);
		mainContainer.appendChild(catContainer);
		left.addEventListener("click", onCategoryClick);
	}
}
function onCategoryClick() {
	var shouldExpand = event.target.getAttribute("shouldExpand");
	var category = event.target.getAttribute("clickid");
	if (shouldExpand == "true") {
		$.getJSON("data/faqs.json", function(data) {
			faqsForCat = data.filter(function (element) {
				return element.category === category;
			});
			populateFaqsForCategory(category, faqsForCat);
		}).fail(function(error) {console.log("INVALID JSON: ", error);});
		event.target.setAttribute("shouldExpand", false);
		event.target.innerHTML = "&#10003;";
		event.target.style.backgroundColor="#0e91bb";
	} else {
		document.getElementById(category + "-catFaqContainer").remove();
		event.target.setAttribute("shouldExpand", true);
		event.target.innerHTML = "";
		event.target.style.backgroundColor="white";
	}
}
function populateFaqsForCategory(category, faqs) {
	var categoryDiv = document.getElementById(category);
	var catFaqContainer = document.createElement("div");
	catFaqContainer.setAttribute("class", "catFaqContainer");
	catFaqContainer.setAttribute("id", category + "-catFaqContainer");
	categoryDiv.appendChild(catFaqContainer);
	for (var faq of faqs) {
		 var faqItemContainer = document.createElement("div");
		 var question = document.createElement("div");
		 var answer = document.createElement("div");
		 var link = document.createElement("a");
		 question.setAttribute("class", "faq-question");
		 answer.setAttribute("class", "faq-answer");
		 question.appendChild(document.createTextNode(faq["question"]));
		 answer.appendChild(document.createTextNode(faq["answer"]));
		 faqItemContainer.appendChild(question);
		 faqItemContainer.appendChild(answer);
		 var doclink = faq["doclink"];
		 if (doclink != "" && doclink != null) {
			 link.setAttribute("href", doclink);
			 link.setAttribute("class", "faq-link");
			 link.setAttribute("target", "_blank");
			 link.appendChild(document.createTextNode("Read More"));
			 faqItemContainer.appendChild(link);
		 }
		 catFaqContainer.appendChild(faqItemContainer);
	}
}