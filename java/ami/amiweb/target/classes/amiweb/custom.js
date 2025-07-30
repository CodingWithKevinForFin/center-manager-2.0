var PI2=Math.PI*2;
var PI_180=Math.PI/180;

var ROTATE_INTERVAL=null;
function rotateDiv(a){
	var element=getElement(a);
	if(!element.deg)
	  element.deg=0;
	element.speed=1;
	ROTATE_INTERVAL=window.setInterval(function(){
		element.deg+=element.speed;
		if(element.speed<2) element.speed+=.025;
		else if(element.speed<4) element.speed+=.012;
		rotate(rd(element.deg),element);
	}, 25);
}

function stopRotateDiv(a){
	clearInterval(ROTATE_INTERVAL);
	ROTATE_INTERVAL=null;
}

function rotate(deg, elmt){
	var s="rotate(" + deg + "deg)";
	elmt.style.transform=s;
	elmt.style['-moz-transform']=s;
	elmt.style['-o-transform']=s;
	elmt.style['-webkit-transform']=s;
	elmt.style['-ms-transform']=s;
	var size=500+deg*1;
	if(size>2500)
		size=2500;
	elmt.style.backgroundSize=toPx(size)+" "+toPx(size);
	elmt.style.backgroundPosition=toPx((690-size)/2)+" "+toPx((690-size)/2);
}


function radiate(cm,oldX,oldA,x,a){
  if(oldX==x){
	  //cm.moveTo(rd(radiansToX(oldX,oldA)),rd(radiansToY(oldX,oldA)));
	  if(a<oldA)
	    cm.arc(0,0,oldX,oldA,a,true);
	  else
	    cm.arc(0,0,oldX,oldA,a,false);
  }else{
    var parts=cl(Math.abs(a-oldA)/(Math.PI/8));
    if(parts==0){
      var x0=radiansToX(oldX,oldA);
      var y0=radiansToY(oldX,oldA);
      var x2=radiansToX(x,a);
      var y2=radiansToY(x,a);
      //cm.moveTo(x0,y0);
      cm.lineTo(x2,y2);
    } else if(parts==1){
      radiate2(cm,oldX,oldA,x,a);
    }else{
      var diffA=(a-oldA)/parts;
      var diffX=(x-oldX)/parts;
      for(var i=0;i<parts;i++){
        radiate2(cm,oldX+diffX*i,oldA+diffA*i,oldX+diffX*(i+1),oldA+diffA*(i+1));
      }
    }
  }
}

function radiate2(cm,oldX,oldA,x,a){
  var x0=radiansToX(oldX,oldA);
  var y0=radiansToY(oldX,oldA);
  var x2=radiansToX(x,a);
  var y2=radiansToY(x,a);
  var angle=(oldA+a)/2;
  var ax=(oldX+x)/2;
  var x1=radiansToX(ax,angle);
  var y1=radiansToY(ax,angle);
  var cpX=2*x1-(x0+x2)/2;
  var cpY=2*y1-(y0+y2)/2;
  //cm.moveTo(x0,y0);
  cm.quadTo(cpX,cpY,x2,y2);
}

function radiansToX(r,a){
  return r*Math.cos(a);
}

function radiansToY(r,a){
  return r*Math.sin(a);
}


var AMIGUISERVICES={};
function registerGuiService(id,service){
//	var thatId=id;
	AMIGUISERVICES[id]=service;
	if(service.registerAmiGuiServicePeer){
		service.registerAmiGuiServicePeer(new AmiGuiServicePeer(id));
	}else{
		err("AMI Gui Service "+id+" does not implement member function: registerAmiGuiServicePeer(peer)");
	}
		
//	service.sendToServer=function(method,data){
//		var method=arguments[0];
//		var args = Array.prototype.slice.call(arguments);
//	}
}
function callAmiGuiServiceJavascript(id,method,args){
	var service=AMIGUISERVICES[id];
	if(service){
		var fn=service[method];
		if(typeof fn ==="function"){
			fn.apply(service,JSON.parse(args));
		}else{
		  err("AMI Gui Service "+id+" does not have function: "+method);
		}
	}else{
		err("AMI Gui Service not found: "+id+" (call registerGuiService(...) first");
	}
}

function callAmiGuiService(id,method,dataArray){
	var params={};
	params.type="service";
	params.subtype="guiservice";
	params.serviceId="AMI_SERVICE";
	params.guiserviceid=id
	params.gsmethod=method;
	params.gsdata=JSON.stringify(dataArray);
	portletManager.portletAjax(params);
}

function AmiGuiServicePeer(id){
	this.id=id;
}

AmiGuiServicePeer.prototype.sendToServer=function(method,data){
  var args = Array.prototype.slice.call(arguments);
  callAmiGuiService(this.id,method,args.splice(1));
}

function amiJsCallback(target,action,params){
  var args = Array.prototype.slice.call(arguments);
  if(args.length<2){
    alert("usage:   aimJsCallback(domObject, customString, ...addtional_args...)");
    return;
  }
  args=args.splice(1);
  while(target.parentNode!=null){
    if(target.customCb!=null){
      target.customCb('amiCustomCallback',args);
      return;
    }
    target=target.parentNode;
  }
  alert("Not a valid target");
}

