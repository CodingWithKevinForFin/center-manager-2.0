document.addEventListener('DOMContentLoaded', function() {

	const button = document.querySelector(".shiny");
	
	const readout = document.querySelector("p");
	
	button.addEventListener("mousemove", (e) => {
	  const { x, y } = button.getBoundingClientRect();
	  button.style.setProperty("--x", e.clientX - x);
	  button.style.setProperty("--y", e.clientY - y);
	});

	
	(function() {
	  // Init
	  var container = document.getElementById("container"),
	    inner = document.getElementById("inner"),
	    helper = document.getElementById("helper");
	    helper.style.visibility = "hidden";
	    map = document.getElementById("map");
	    nyglow = document.getElementById("nyglow");
	    ldnglow = document.getElementById("ldnglow");
	    sgeglow = document.getElementById("sgeglow");
	    ldnpic = document.getElementById("ldnpic");
	    nyo = document.getElementById("nyo");
	    nyaddress = document.getElementById("nyaddress");
	    ldno = document.getElementById("ldno");
	    ldnaddress = document.getElementById("ldnaddress");
	    sgeo = document.getElementById("sgeo");
	    sgeaddress = document.getElementById("sgeaddress");
	    backbutton = document.getElementById("back");
	
	  console.log(container);
	
	  // Mouse
	  var mouse = {
	    _x: 0,
	    _y: 0,
	    x: 0,
	    y: 0,
	    updatePosition: function(event) {
	      var e = event || window.event;
	      this.x = e.clientX - this._x;
	      this.y = (e.clientY - this._y) * -1;
	    },
	    setOrigin: function(e) {
	      this._x = e.offsetLeft + Math.floor(e.offsetWidth / 2);
	      this._y = e.offsetTop + Math.floor(e.offsetHeight / 2);
	    },
	    show: function() {
	      return "(" + this.x + ", " + this.y + ")";
	    },
	    changeimage: function() {
	      if(this.x >= -300 && this.x <= -211 && this.y <= 131 && this.y >= 61)
	        {
	        	if(map.src ="images/3forgemap.webp")
	        	{
		          console.log("New York");
		          nyglow.style.top = "203px";
		          ldnglow.style.width = "0px", sgeglow.style.width = "0px";
		          map.src ="images/3forgemap-newyork.png";
		          map.style.width = "400px";
		          nycpic.style.display = "initial";
		          nyaddress.style.display = "initial";
		          nyo.style.display = "initial";
		          backbutton.style.display="flex";
		          backbutton.style.top="-577px";
	          	}
	        }
	      if(this.x >= -170 && this.x <= -49 && this.y <= 148 && this.y >= 83)
	        {
	        	if(map.src ="images/3forgemap.webp")
	        	{
		          console.log("London");
		          ldnglow.style.top = "139px", ldnglow.style.left = "137px";
		          nyglow.style.width = "0px", sgeglow.style.width = "0px";
	  	          map.src ="images/3forgemap-london.png";
		          map.style.width = "400px";
		          ldnpic.style.display = "initial";
		          ldnaddress.style.display = "initial";
		          ldno.style.display = "initial";
		          backbutton.style.display="flex";
		          backbutton.style.top="-348px";
	        	}
	        }
	      if(this.x >= 145 && this.x <= 288 && this.y <= -25 && this.y >= -92)
	        {
				if(map.src ="images/3forgemap.webp")
	        	{
		          console.log("Singapore");
		          sgeglow.style.top = "107px", sgeglow.style.left = "182px";
		          nyglow.style.width = "0px", ldnglow.style.width = "0px";
	  	          map.src ="images/3forgemap-singapore.png";
		          map.style.width = "400px";
		          sgepic.style.display = "initial";
		          sgeaddress.style.display = "initial";
		          sgeo.style.display = "initial";
		          backbutton.style.display="flex";
		          backbutton.style.top="-250px";
	        	}
	        }
    	  if(this.x <= -425 && this.y >= 209 && this.y <= 261)
	    	  { 
		    	nycpic.style.display = "none";
		    	nyaddress.style.display = "none";
		    	nyo.style.display = "none";
	    		ldnpic.style.display = "none";
		    	ldnaddress.style.display = "none";
		    	ldno.style.display = "none";
	    		sgepic.style.display = "none";
		    	sgeaddress.style.display = "none";
		    	sgeo.style.display = "none";
		    	map.src ="images/3forgemap.webp";
		    	map.style.width="initial";
		    	nyglow.style.top="109px", nyglow.style.left="212px", nyglow.style.width="100px";
		    	sgeglow.style.top="274px", sgeglow.style.left="726px", sgeglow.style.width="100px";
		    	ldnglow.style.top="94px", ldnglow.style.left="379px", ldnglow.style.width="100px";
		    	backbutton.style.display="none";
		    	backbutton.style.top="-348px";
		     }
	    },
	    resetmap: function(){
	    	if(this.x <= -425 && this.y >= 209 && this.y <= 261)
	    	  { 
		    	map.src ="images/3forgemap.webp";
		    	nycpic.style.display = "none";
		    	nyaddress.style.display = "none";
		    	nyo.style.display = "none";
	    		ldnpic.style.display = "none";
		    	ldnaddress.style.display = "none";
		    	ldno.style.display = "none";
	    		sgepic.style.display = "none";
		    	sgeaddress.style.display = "none";
		    	sgeo.style.display = "none";
		    	backbutton.style.display="none";
		     }
	    }
	  };
	
	  // Track the mouse position relative to the center of the container.
	  mouse.setOrigin(container);
	
	  //--------------------------------------------------
	
	  var counter = 0;
	  var updateRate = 1;
	  var isTimeToUpdate = function() {
	    return counter++ % updateRate === 0;
	  };
	
	  //--------------------------------------------------
	
	  var onMouseEnterHandler = function(event) {
	    helper.className = "";
	    update(event);
	  };
	
	  var onMouseLeaveHandler = function() {
	    inner.style = "";
	    helper.className = "hidden";
	  };
	
	  var onMouseMoveHandler = function(event) {
	    if (isTimeToUpdate()) {
	      update(event);
	    }
	    displayMousePositionHelper(event);
	  };
	  
	  var onMouseClickHandler = function(event){
	    mouse.changeimage();
	  }
	  
	  var onButtonClickHandler = function(event){
	  	mouse.resetmap();
	  }
	
	  //--------------------------------------------------
	
	  var update = function(event) {
	    mouse.updatePosition(event);
	    updateTransformStyle(
	      (mouse.y / inner.offsetHeight / 4).toFixed(2),
	      (mouse.x / inner.offsetWidth / 4).toFixed(2)
	    );
	  };
	  
	  var updateTransformStyle = function(x, y) {
	    var style = "rotateX(" + x + "deg) rotateY(" + y + "deg)";
	    inner.style.transform = style;
	    inner.style.webkitTransform = style;
	    inner.style.mozTransform = style;
	    inner.style.msTransform = style;
	    inner.style.oTransform = style;
	  };
	
	  var displayMousePositionHelper = function(event) {
	    var e = event || window.event;
	    helper.innerHTML = mouse.show();
	    helper.style = "top:"+(e.clientY-container.offsetTop)+"px;"
	                 + "left:"+(e.clientX-container.offsetLeft)+"px;";
	  };
	  
	  var imageChanger = function(event){
	    console.log("test");
	  };
	
	  //--------------------------------------------------
	
	  container.onmouseenter = onMouseEnterHandler;
	  container.onmouseleave = onMouseLeaveHandler;
	  container.onmousemove = onMouseMoveHandler;
	  container.onclick = onMouseClickHandler;
	  backbutton.onclick = onButtonClickHandler;
	  
      // Function to update mouse offset and area
	    var updateMouseOffsetAndArea = function() {
	        const { x, y } = button.getBoundingClientRect();
	        button.style.setProperty("--x", mouse.x - x);
	        button.style.setProperty("--y", mouse.y - y);
	    };
	
	    // Event listener for window resize
	    window.addEventListener('resize', function() {
	        // Update mouse offset and area
	        updateMouseOffsetAndArea();
	    });
	})();
})
