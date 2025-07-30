var $accordionItem = $(".accordion-item");

$accordionItem.hover(function() {
	var item = $(this)[0];
	var itemDetail = $(item).find("div.item-detail")[0];
	var itemTitle = $(item).find("div.item-title")[0];
	$(item).toggleClass("item-expand");
	$(itemDetail).toggleClass("accordion-item-show-detail");
	$(itemTitle).toggleClass("horizontal-text");
	$(itemTitle).toggleClass("title-strech");
	var itemSiblings = $(item).siblings();
	for (var sibling of itemSiblings)
		$(sibling).toggleClass("item-shrink");
});
