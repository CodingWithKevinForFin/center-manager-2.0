function rotate() {
		var arrow = document.getElementById("speed-arrow");
		var id;
		if (event.target.classList.contains("rt-workflows")) {
			arrow.style.transform = "rotate(-80deg)";
			id = "rt-workflows";
			
		} else if (event.target.classList.contains("uiux")) {
			arrow.style.transform = "rotate(-40deg)";
			id = "uiux";
		} else if (event.target.classList.contains("team-collab")) {
			arrow.style.transform = "rotate(0deg)";
			id = "team-collab";
		} else if (event.target.classList.contains("sec-rob")) {
			arrow.style.transform = "rotate(40deg)";
			id = "sec-rob";
		} else if (event.target.classList.contains("data-int")) {
			arrow.style.transform = "rotate(80deg)";
			id = "data-int";
		} 
		if (id) {
			// hide the siblings first, then show  the clicked div
			var siblings = $("#" + id).siblings();
			for (var sibling of siblings)
				$(sibling).css("display", "none");
			$("#" + id).fadeIn();
		} else
			console.log("Problem displaying speeddial content");
		
		// only color the clicked title	
		$(".desc-title").css("color", "");
		$(".rt-workflows").css("color", "#4a4a4a");
		$(event.target).css("color", "#f16900");
	}