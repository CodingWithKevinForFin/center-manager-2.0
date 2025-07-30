package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_ami_index2_js_1 extends AbstractHttpHandler{

	public portal_rsc_ami_index2_js_1() {
	}
  
	public boolean canHandle(HttpRequestResponse request){
	  return true;
	}

	public void handle(HttpRequestResponse request) throws java.io.IOException{
	  super.handle(request);
	  com.f1.utils.FastPrintStream out = request.getOutputStream();
	  HttpSession session = request.getSession(false);
	  HttpServer server = request.getHttpServer();
	  LocaleFormatter formatter = session == null ? server.getHttpSessionManager().getDefaultFormatter() : session.getFormatter();
          out.print(
            "    window.history.replaceState({}, document.title, \"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.ami.web.pages.AmiWebPages.URL_HELLO);
          out.print(
            "\");\r\n"+
            "  	var loginForm = document.getElementById(\"login-form\");\r\n"+
            "  	var loginBtn = document.getElementById(\"login\");\r\n"+
            "  	var loginBtnText = document.getElementById(\"main-text\");\r\n"+
            "  	var waitingText = document.getElementById(\"waiting\");\r\n"+
            "  	\r\n"+
            "  	var interval = null;\r\n"+
            "  	var intervalRateMs = 500;\r\n"+
            "  	var intervalCount = 0;\r\n"+
            "  	\r\n"+
            "  	function onLoginBtnPress() {\r\n"+
            "  		event.preventDefault();\r\n"+
            "  		\r\n"+
            "  		var message = \"\";\r\n"+
            "		//document.getElementById(\"error-message\").innerHTML = '");
com.f1.http.tag.OutTag.escapeHtml_Full(out,request.findAttribute("message"));
          out.print(
            "'; // clear the error messsage everytime clicks login.\r\n"+
            "		document.getElementById(\"error-message\").textContent = \"\"; // clear the error messsage everytime clicks login.\r\n"+
            "  		document.getElementById(\"error-message\").setAttribute(\"style\", \"height:16px\");\r\n"+
            "		\r\n"+
            "		loginBtn.disabled = true;\r\n"+
            "  		loginBtn.style.cursor = \"not-allowed\";\r\n"+
            "		loginBtnText.textContent = \"Logging In\";\r\n"+
            "		//waitingImg.src = waitingImgSrc;\r\n"+
            "		loginBtn.style.opacity = 0.5;\r\n"+
            "  		loginForm.submit();\r\n"+
            "  		interval = setInterval(checkStatus, intervalRateMs);\r\n"+
            "  	}\r\n"+
            "  	function checkStatus() {\r\n"+
            "  		intervalCount++;\r\n"+
            "		var errorMessage = document.getElementById(\"error-message\").textContent;\r\n"+
            "  		if (errorMessage) {\r\n"+
            "  			clearInterval(interval);\r\n"+
            "  			loginBtn.disabled = false;\r\n"+
            "  			loginBtn.style.cursor = \"auto\";\r\n"+
            "  			loginBtnText.textContent = \"Login\";\r\n"+
            "			loginBtn.style.opacity = 1;\r\n"+
            "  		} else {\r\n"+
            "  			if (intervalCount == 10)\r\n"+
            "  				loginBtnText.textContent = \"Still Working\";\r\n"+
            "  			if (waitingText.textContent === \"...\")\r\n"+
            "  				waitingText.textContent = \"\";\r\n"+
            "  			else\r\n"+
            "  				waitingText.textContent += \".\";\r\n"+
            "  		}\r\n"+
            "  	}\r\n"+
            "\r\n"+
            "  	// login page animation\r\n"+
            "    var t={}\r\n"+
            "\r\n"+
            "    function rand(min,max){\r\n"+
            "      return min+ Math.floor(Math.random()*(max-min))\r\n"+
            "    }\r\n"+
            "    var cw=window.innerWidth;\r\n"+
            "    var ch=window.innerHeight;\r\n"+
            "    var canvas=document.getElementById(\"canvas\");\r\n"+
            "    var c=canvas.getContext(\"2d\");\r\n"+
            "    var lastOnMove=Date.now();\r\n"+
            "    canvas.onmousemove= function(e){\r\n"+
            "        var now=Date.now();\r\n"+
            "        if(now-lastOnMove<10){\r\n"+
            "        	return;\r\n"+
            "        }\r\n"+
            "        lastOnMove=now;\r\n"+
            "    	\r\n"+
            "        var t=add(e.clientX,e.clientY);\r\n"+
            "        var dx=e.clientX-t.parts[0].x;\r\n"+
            "        var dy=e.clientY-t.parts[0].y;\r\n"+
            "        for(var i in t.parts){\r\n"+
            "        	t.parts[i].x+=dx;\r\n"+
            "        	t.parts[i].y+=dy;\r\n"+
            "        }\r\n"+
            "    }\r\n"+
            "    canvas.width=cw;\r\n"+
            "    canvas.height=ch;\r\n"+
            "\r\n"+
            "\r\n"+
            "    var shapes=[];\r\n"+
            "\r\n"+
            "    var ANGLE_DELTA=.003;\r\n"+
            "    var ACCELERATION=.03;\r\n"+
            "    var MIN_SPEED=5;\r\n"+
            "    var MAX_SPEED=25;\r\n"+
            "    var DENSITY=.00001;\r\n"+
            "    var PERIOD=40;\r\n"+
            "    var MIN_SIZE=5;\r\n"+
            "    var MAX_SIZE=20;\r\n"+
            "    var MIN_STEPS=50;\r\n"+
            "    var MAX_STEPS=200;\r\n"+
            "    var COLOR_BACKGROUND=\"#000000AA\";\r\n"+
            "    var COLOR_LINE_RGB=\"136,186,202\";\r\n"+
            "    \r\n"+
            "    var xMin=0;\r\n"+
            "    var xMax=cw;\r\n"+
            "    var yMin=0;\r\n"+
            "    var yMax=ch;\r\n"+
            "    var targetCount;\r\n"+
            "\r\n"+
            "    function distance(c1,c2){\r\n"+
            "      var dx=Math.abs(c1.x-c2.x);\r\n"+
            "      var dy=Math.abs(c1.y-c2.y);\r\n"+
            "      return Math.sqrt(dx*dx+dy*dy);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    \r\n"+
            "    function updateBounds(){\r\n"+
            "      targetCount=Math.floor((xMax-xMin)*(yMax-yMin)*DENSITY);\r\n"+
            "      var sw=window.innerWidth/cw;\r\n"+
            "      var sh=window.innerHeight/ch;\r\n"+
            "      cw=window.innerWidth;\r\n"+
            "      ch=window.innerHeight;\r\n"+
            "      canvas.width=cw;\r\n"+
            "      canvas.height=ch;\r\n"+
            "      xMin=0;\r\n"+
            "      xMax=cw;\r\n"+
            "      yMin=0;\r\n"+
            "      yMax=ch;\r\n"+
            "      for(var i=0;i<shapes.length;i++){\r\n"+
            "         for(var j=0;j<6;j++){\r\n"+
            "    	   shapes[i].parts[j].x*=sw;\r\n"+
            "    	   shapes[i].parts[j].y*=sh;\r\n"+
            "         } \r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "    updateBounds();\r\n"+
            "    \r\n"+
            "    function add(x,y){\r\n"+
            "        var size=rand(MIN_SIZE,MAX_SIZE);\r\n"+
            "    	var s=size*1.2;\r\n"+
            "    	var steps=rand(MIN_STEPS,MAX_STEPS);\r\n"+
            "        var angle=Math.random()*Math.PI*2;\r\n"+
            "        var c0=add2(x+s*0,y+s*0,\"#F16900\",steps,angle);\r\n"+
            "        var c1=add2(x+s*1,y+s*0,\"#F16900\",steps,angle);\r\n"+
            "        var c2=add2(x+s*2,y+s*0,\"#F16900\",steps,angle);\r\n"+
            "        var c3=add2(x+s*0,y+s*1,\"#0E91BB\",steps,angle);\r\n"+
            "        var c4=add2(x+s*1,y+s*1,\"#0E91BB\",steps,angle);\r\n"+
            "        var c5=add2(x+s*0,y+s*2,\"#77CEFA\",steps,angle);\r\n"+
            "        var r=shapes[shapes.length]={n:0,size:size,w:steps,parts:[c0,c1,c2,c3,c4,c5]};\r\n"+
            "        return r;\r\n"+
            "    }\r\n"+
            "    function add2(x,y,color,steps,angle){\r\n"+
            "    	angle+=Math.random()*Math.PI/2;\r\n"+
            "    	if(angle>Math.PI*2)\r\n"+
            "    	  angle-=Math.PI*2;\r\n"+
            "        var speed=rand(MIN_SPEED,MAX_SPEED)*.05;\r\n"+
            "        var angleDelta=rand(-10,10)*ANGLE_DELTA;\r\n"+
            "        for(var i=0;i<steps;i++){\r\n"+
            "          x-=Math.sin(angle)*speed;\r\n"+
            "          y-=Math.cos(angle)*speed;\r\n"+
            "          angle-=angleDelta;\r\n"+
            "          speed+=ACCELERATION;\r\n"+
            "        }\r\n"+
            "        var circle={x:x,y:y,a:angle,s:speed,c:color,ad:angleDelta};\r\n"+
            "        return circle;\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    var processCount=0;\r\n"+
            "    function process(){\r\n"+
            "      c.clearRect(0, 0, canvas.width, canvas.height);\r\n"+
            "      if(processCount<256){\r\n"+
            "        c.fillStyle=\"#000000\"+toHex(255-processCount*10);\r\n"+
            "        c.fillRect(0, 0, canvas.width, canvas.height);\r\n"+
            "      }\r\n"+
            "      processCount++;\r\n"+
            "      \r\n"+
            "\r\n"+
            "\r\n"+
            "      while(shapes.length<targetCount){\r\n"+
            "        add(rand(xMin,xMax),rand(yMin,yMax));\r\n"+
            "      }\r\n"+
            "        for(var i=0;i<shapes.length;i++){\r\n"+
            "          var shape=shapes[i];\r\n"+
            "          var n=shape.n;\r\n"+
            "          var w=shape.w;\r\n"+
            "          var size=Math.min(shape.size,n/2);\r\n"+
            "          var pct;\r\n"+
            "          if(n>w){\r\n"+
            "            pct=255-Math.min(255,(n-w)*16);\r\n"+
            "            if(pct==0){\r\n"+
            "              shapes.splice(i--,1);\r\n"+
            "              continue;\r\n"+
            "            }\r\n"+
            "          }else\r\n"+
            "            pct=Math.min(255,n*16);\r\n"+
            "          var txt=toHex(pct);\r\n"+
            "          for(var j=0;j<6;j++){\r\n"+
            "        	var circle=shape.parts[j];\r\n"+
            "            c.fillStyle=circle.c+txt; \r\n"+
            "            c.beginPath();\r\n"+
            "            c.roundRect(circle.x-size/2,circle.y-size/2,size,size,[size/4]);\r\n"+
            "            c.closePath();\r\n"+
            "            c.fill();\r\n"+
            "        	if(n<=w)\r\n"+
            "              circle.s-=ACCELERATION;\r\n"+
            "            circle.a=circle.a+circle.ad;\r\n"+
            "            var v=Math.cos(circle.a)*circle.s;\r\n"+
            "            var h=Math.sin(circle.a)*circle.s;\r\n"+
            "            circle.x+=h;\r\n"+
            "            circle.y+=v;\r\n"+
            "          }\r\n"+
            "          if(n+1==w){\r\n"+
            "        	var tv=0;\r\n"+
            "        	var th=0;\r\n"+
            "        	var ts=0;\r\n"+
            "            for(var j=0;j<6;j++){\r\n"+
            "        	  var circle=shape.parts[j];\r\n"+
            "              tv+=Math.cos(circle.a)*circle.s;\r\n"+
            "              th+=Math.sin(circle.a)*circle.s;\r\n"+
            "              ts+=circle.s;\r\n"+
            "            }\r\n"+
            "            ts/=6;\r\n"+
            "            var a=Math.atan2(th,tv);\r\n"+
            "            for(var j=0;j<6;j++){\r\n"+
            "        	  var circle=shape.parts[j];\r\n"+
            "              circle.a=a;\r\n"+
            "              circle.ad=0;\r\n"+
            "              circle.s=ts;\r\n"+
            "            }\r\n"+
            "          }\r\n"+
            "          shape.n++;\r\n"+
            "        }\r\n"+
            "    }\r\n"+
            "    function toHex(n){\r\n"+
            "    	if(n<1)\r\n"+
            "    		return \"00\";\r\n"+
            "    	else if(n<16)\r\n"+
            "    		return \"0\"+n.toString(16);\r\n"+
            "    	else if(n<256)\r\n"+
            "    		return n.toString(16);\r\n"+
            "    	return \"FF\";\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    ");
          if(com.f1.http.HttpUtils.toBoolean(request.findAttribute("login_animated"))){
            out.print(
              "\r\n"+
              "  		ROTATE_INTERVAL=window.setInterval(function(){\r\n"+
              "	   		process();\r\n"+
              "	  	}, PERIOD );\r\n"+
              "    ");
          }
          out.print(
            "\r\n"+
            "\r\n"+
            "	// browser notice\r\n"+
            "	var useragent = navigator.userAgent;\r\n"+
            "	if (useragent.match(/trident/i) || useragent.match(/msie/i) || useragent.match(/edge/i)) {\r\n"+
            "		document.getElementById('browsernotice').innerHTML = '<h3 class=\"browsernotice-warning-text\">Your browser may not be supported. Try switching to a different browser before logging in.</h3>';\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	// fast-login \r\n"+
            "    document.getElementById(\"username\").focus();\r\n"+
            "	function isEmpty(element) {\r\n"+
            "		return element.value.trim().length == 0 ? true : false;\r\n"+
            "	}\r\n"+
            "	var username = document.getElementById('username');\r\n"+
            "	var password = document.getElementById('password');\r\n"+
            "	var errorMessage = '");
com.f1.http.tag.OutTag.escapeHtml_Full(out,request.findAttribute("error"));
          out.print(
            "'.trim();\r\n"+
            "	username.focus();\r\n"+
            "	\r\n"+
            "	document.querySelector('input').addEventListener('keypress', function(event) {\r\n"+
            "		if (event.which == 13) {\r\n"+
            "			event.preventDefault();\r\n"+
            "			if (username == document.activeElement && isEmpty(password))\r\n"+
            "				password.focus();\r\n"+
            "			else if (!isEmpty(username) && !isEmpty(password))\r\n"+
            "				document.getElementById('login-form').submit();\r\n"+
            "		}\r\n"+
            "	});\r\n"+
            "	\r\n"+
            "	if (errorMessage === 'User not found')\r\n"+
            "		username.focus();\r\n"+
            "	else if (errorMessage === 'Incorrect Password')\r\n"+
            "		password.focus();");

	}
	
}