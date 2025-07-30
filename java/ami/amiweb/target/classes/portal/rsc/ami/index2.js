    window.history.replaceState({}, document.title, "<f1:out value="com.f1.ami.web.pages.AmiWebPages.URL_HELLO"/>");
  	var loginForm = document.getElementById("login-form");
  	var loginBtn = document.getElementById("login");
  	var loginBtnText = document.getElementById("main-text");
  	var waitingText = document.getElementById("waiting");
  	
  	var interval = null;
  	var intervalRateMs = 500;
  	var intervalCount = 0;
  	
  	function onLoginBtnPress() {
  		event.preventDefault();
  		
  		var message = "";
		//document.getElementById("error-message").innerHTML = '<f1:out escape="FULL" value="${message}"/>'; // clear the error messsage everytime clicks login.
		document.getElementById("error-message").textContent = ""; // clear the error messsage everytime clicks login.
  		document.getElementById("error-message").setAttribute("style", "height:16px");
		
		loginBtn.disabled = true;
  		loginBtn.style.cursor = "not-allowed";
		loginBtnText.textContent = "Logging In";
		//waitingImg.src = waitingImgSrc;
		loginBtn.style.opacity = 0.5;
  		loginForm.submit();
  		interval = setInterval(checkStatus, intervalRateMs);
  	}
  	function checkStatus() {
  		intervalCount++;
		var errorMessage = document.getElementById("error-message").textContent;
  		if (errorMessage) {
  			clearInterval(interval);
  			loginBtn.disabled = false;
  			loginBtn.style.cursor = "auto";
  			loginBtnText.textContent = "Login";
			loginBtn.style.opacity = 1;
  		} else {
  			if (intervalCount == 10)
  				loginBtnText.textContent = "Still Working";
  			if (waitingText.textContent === "...")
  				waitingText.textContent = "";
  			else
  				waitingText.textContent += ".";
  		}
  	}

  	// login page animation
    var t={}

    function rand(min,max){
      return min+ Math.floor(Math.random()*(max-min))
    }
    var cw=window.innerWidth;
    var ch=window.innerHeight;
    var canvas=document.getElementById("canvas");
    var c=canvas.getContext("2d");
    var lastOnMove=Date.now();
    canvas.onmousemove= function(e){
        var now=Date.now();
        if(now-lastOnMove<10){
        	return;
        }
        lastOnMove=now;
    	
        var t=add(e.clientX,e.clientY);
        var dx=e.clientX-t.parts[0].x;
        var dy=e.clientY-t.parts[0].y;
        for(var i in t.parts){
        	t.parts[i].x+=dx;
        	t.parts[i].y+=dy;
        }
    }
    canvas.width=cw;
    canvas.height=ch;


    var shapes=[];

    var ANGLE_DELTA=.003;
    var ACCELERATION=.03;
    var MIN_SPEED=5;
    var MAX_SPEED=25;
    var DENSITY=.00001;
    var PERIOD=40;
    var MIN_SIZE=5;
    var MAX_SIZE=20;
    var MIN_STEPS=50;
    var MAX_STEPS=200;
    var COLOR_BACKGROUND="#000000AA";
    var COLOR_LINE_RGB="136,186,202";
    
    var xMin=0;
    var xMax=cw;
    var yMin=0;
    var yMax=ch;
    var targetCount;

    function distance(c1,c2){
      var dx=Math.abs(c1.x-c2.x);
      var dy=Math.abs(c1.y-c2.y);
      return Math.sqrt(dx*dx+dy*dy);
    }

    
    function updateBounds(){
      targetCount=Math.floor((xMax-xMin)*(yMax-yMin)*DENSITY);
      var sw=window.innerWidth/cw;
      var sh=window.innerHeight/ch;
      cw=window.innerWidth;
      ch=window.innerHeight;
      canvas.width=cw;
      canvas.height=ch;
      xMin=0;
      xMax=cw;
      yMin=0;
      yMax=ch;
      for(var i=0;i<shapes.length;i++){
         for(var j=0;j<6;j++){
    	   shapes[i].parts[j].x*=sw;
    	   shapes[i].parts[j].y*=sh;
         } 
      }
    }
    
    updateBounds();
    
    function add(x,y){
        var size=rand(MIN_SIZE,MAX_SIZE);
    	var s=size*1.2;
    	var steps=rand(MIN_STEPS,MAX_STEPS);
        var angle=Math.random()*Math.PI*2;
        var c0=add2(x+s*0,y+s*0,"#F16900",steps,angle);
        var c1=add2(x+s*1,y+s*0,"#F16900",steps,angle);
        var c2=add2(x+s*2,y+s*0,"#F16900",steps,angle);
        var c3=add2(x+s*0,y+s*1,"#0E91BB",steps,angle);
        var c4=add2(x+s*1,y+s*1,"#0E91BB",steps,angle);
        var c5=add2(x+s*0,y+s*2,"#77CEFA",steps,angle);
        var r=shapes[shapes.length]={n:0,size:size,w:steps,parts:[c0,c1,c2,c3,c4,c5]};
        return r;
    }
    function add2(x,y,color,steps,angle){
    	angle+=Math.random()*Math.PI/2;
    	if(angle>Math.PI*2)
    	  angle-=Math.PI*2;
        var speed=rand(MIN_SPEED,MAX_SPEED)*.05;
        var angleDelta=rand(-10,10)*ANGLE_DELTA;
        for(var i=0;i<steps;i++){
          x-=Math.sin(angle)*speed;
          y-=Math.cos(angle)*speed;
          angle-=angleDelta;
          speed+=ACCELERATION;
        }
        var circle={x:x,y:y,a:angle,s:speed,c:color,ad:angleDelta};
        return circle;
    }

    var processCount=0;
    function process(){
      c.clearRect(0, 0, canvas.width, canvas.height);
      if(processCount<256){
        c.fillStyle="#000000"+toHex(255-processCount*10);
        c.fillRect(0, 0, canvas.width, canvas.height);
      }
      processCount++;
      


      while(shapes.length<targetCount){
        add(rand(xMin,xMax),rand(yMin,yMax));
      }
        for(var i=0;i<shapes.length;i++){
          var shape=shapes[i];
          var n=shape.n;
          var w=shape.w;
          var size=Math.min(shape.size,n/2);
          var pct;
          if(n>w){
            pct=255-Math.min(255,(n-w)*16);
            if(pct==0){
              shapes.splice(i--,1);
              continue;
            }
          }else
            pct=Math.min(255,n*16);
          var txt=toHex(pct);
          for(var j=0;j<6;j++){
        	var circle=shape.parts[j];
            c.fillStyle=circle.c+txt; 
            c.beginPath();
            c.roundRect(circle.x-size/2,circle.y-size/2,size,size,[size/4]);
            c.closePath();
            c.fill();
        	if(n<=w)
              circle.s-=ACCELERATION;
            circle.a=circle.a+circle.ad;
            var v=Math.cos(circle.a)*circle.s;
            var h=Math.sin(circle.a)*circle.s;
            circle.x+=h;
            circle.y+=v;
          }
          if(n+1==w){
        	var tv=0;
        	var th=0;
        	var ts=0;
            for(var j=0;j<6;j++){
        	  var circle=shape.parts[j];
              tv+=Math.cos(circle.a)*circle.s;
              th+=Math.sin(circle.a)*circle.s;
              ts+=circle.s;
            }
            ts/=6;
            var a=Math.atan2(th,tv);
            for(var j=0;j<6;j++){
        	  var circle=shape.parts[j];
              circle.a=a;
              circle.ad=0;
              circle.s=ts;
            }
          }
          shape.n++;
        }
    }
    function toHex(n){
    	if(n<1)
    		return "00";
    	else if(n<16)
    		return "0"+n.toString(16);
    	else if(n<256)
    		return n.toString(16);
    	return "FF";
    }

    <f1:if test="${login_animated}">
  		ROTATE_INTERVAL=window.setInterval(function(){
	   		process();
	  	}, PERIOD );
    </f1:if>

	// browser notice
	var useragent = navigator.userAgent;
	if (useragent.match(/trident/i) || useragent.match(/msie/i) || useragent.match(/edge/i)) {
		document.getElementById('browsernotice').innerHTML = '<h3 class="browsernotice-warning-text">Your browser may not be supported. Try switching to a different browser before logging in.</h3>';
	}
	
	// fast-login 
    document.getElementById("username").focus();
	function isEmpty(element) {
		return element.value.trim().length == 0 ? true : false;
	}
	var username = document.getElementById('username');
	var password = document.getElementById('password');
	var errorMessage = '<f1:out escape="FULL" value="${error}"/>'.trim();
	username.focus();
	
	document.querySelector('input').addEventListener('keypress', function(event) {
		if (event.which == 13) {
			event.preventDefault();
			if (username == document.activeElement && isEmpty(password))
				password.focus();
			else if (!isEmpty(username) && !isEmpty(password))
				document.getElementById('login-form').submit();
		}
	});
	
	if (errorMessage === 'User not found')
		username.focus();
	else if (errorMessage === 'Incorrect Password')
		password.focus();