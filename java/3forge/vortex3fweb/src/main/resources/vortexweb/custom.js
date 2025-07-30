
var CLOCK_INTERVAL=null;
var REGISTERED_CLOCKS={};

function ClockPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  //this.progressBackDiv=nw('div','progress_bar_back');
  //this.progressOuterDiv=nw('div','progress_bar_outer');
  //this.progressMessageDiv=nw('div','progress_bar_message');
  this.clockDiv=nw('div','portlet_clock');
  //this.divElement.appendChild(this.progressBackDiv);
  //this.progressBackDiv.appendChild(this.progressOuterDiv);
  //this.progressOuterDiv.appendChild(this.progressDiv);
  //this.progressOuterDiv.appendChild(this.progressMessageDiv);
  this.divElement.appendChild(this.clockDiv);
  this.clockDiv.onclick=function(e){that.onclick(e)};
  //this.init('',new Date().getTime());
  if(CLOCK_INTERVAL==null)
    CLOCK_INTERVAL=window.setInterval(function(){updateClocks()},500);
  REGISTERED_CLOCKS[portletId]=this;
};
ClockPortlet.prototype.close=function(){
  delete REGISTERED_CLOCKS[this.portletId];
}
ClockPortlet.prototype.init=function(tz,format,serverNow,fg,bg){
	this.tz=tz;
	var date=new Date();
	this.formatter=new DateFormatter(format,tz);//"Y-M-D h:m:sbbEST");
	this.clockDiv.style.color=fg;
	this.clockDiv.style.background=bg;
	var localNow=date.getTime();
	this.offset=serverNow-localNow;
	var now=date.getTime()+date.getTimezoneOffset()*60000;
	date.setTime(now);
	this.update(date);
}
ClockPortlet.prototype.update=function(date){
  if(this.formatter==null)
    return;
  date.setTime(date.getTime()+this.offset);
  this.clockDiv.innerHTML=this.formatter.format(date);
}

ClockPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  //this.progressOuterDiv.style.top=toPx(10);
  //this.progressOuterDiv.style.left=toPx(10);
  this.clockDiv.style.width=toPx(width);
  this.clockDiv.style.height=toPx(height);
};

ClockPortlet.prototype.onclick=function(text){
  this.callBack('onclick',{});
}

function updateClocks() {
	var date=new Date();
	var now=date.getTime()+date.getTimezoneOffset()*60000;
	for(var i in REGISTERED_CLOCKS){
		date.setTime(now);
		REGISTERED_CLOCKS[i].update(date);
	}
}
//ClockPortlet.prototype.setProgress=function(progress,text){
	//this.progressDiv.style.width=fl(100*progress)+"%";
	//this.progressMessageDiv.innerHTML=text;
//}


function rightAlign(pad, text, totalSize) {
	text=text.toString();
	pads=totalSize-text.length;
	if(pads<=0)
		return text;
	var r="";
	while(pads-->0)
		r+=pad;
	return r+text;
}


function DateFormatter(format,tz){
	this.formatters=[];
	for(var i=0;i<format.length;i++){
		var char=format[i];
		if(char=='\''){
			char='';
			i++;
			while(i<format.length-1 && format[i]!='\'')
				char+=format[i++];
	      this.formatters[this.formatters.length]=this.getFormatter(char,true,tz);
		}else{
	      this.formatters[this.formatters.length]=this.getFormatter(char,false,tz);
		}
	}
}

var WEEKDAYS=['SUN','MON','TUE','WED','THU','FRI','SAT'];

DateFormatter.prototype.getFormatter=function(type,isText,tz){
	if(isText)
	  return function(date){return type};
	switch(type){
	  case 'Y': return function(date){return rightAlign('0',date.getFullYear(),4)};
	  case 'M': return function(date){return rightAlign('0',date.getMonth()+1,2)};
	  case 'D': return function(date){return rightAlign('0',date.getDate(),2)};
	  case 'h': return function(date){return rightAlign('0',((date.getHours()+11)%12)+1,2)};
	  case 'H': return function(date){return rightAlign('0',date.getHours(),2)};
	  case 'm': return function(date){return rightAlign('0',date.getMinutes(),2)};
	  case 's': return function(date){return rightAlign('0',date.getSeconds(),2)};
	  case 'e': return function(date){return date.getTime()};
	  case ':': return function(date){return date.getMilliseconds()>=500 ? ':' : '&nbsp;'};
	  case 'w': return function(date){return WEEKDAYS[date.getDay()] };
	  case 'a': return function(date){return date.getHours()>11 ? 'PM' : 'AM' };
	  case 'z': return function(date){return tz};
	  case 'b': return function(date){return '<br>'};
	  default: return function(date){return type};
	}
}
DateFormatter.prototype.format=function(date){
	r='';
	for(var i in this.formatters)
		r+=this.formatters[i](date);
	return r;
}

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
		rotate(fl(element.deg),element);
	}, 25);
}
function stopRotateDiv(a){
	//var element=getElement(a);
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
	//elmt.setAttribute(
	        //"style", "transform:rotate(" + deg + "deg);"
	      //+ "-moz-transform: rotate(" + deg + "deg);"
	      //+ "-o-transform: rotate(" + deg + "deg);"
	      //+ "-webkit-transform:rotate(" + deg + "deg);"
	      //+ "-ms-transform:rotate("+ deg +"deg);"
	    //); 
	//}



function amiLinkingOnMouseMove(e){
  var frPoint=new Rect().readFromElement(Portlets[amiUserLinkingSource].divElement).getMidpoint();
  var context = amiUserLinkingCanvas.getContext('2d');
  context.clearRect(0,0,amiUserLinkingCanvas.width,amiUserLinkingCanvas.height);
  context.beginPath();
  context.strokeStyle='#888888';
  context.lineWidth=3;
  var toPoint = e==null ? new Point(MOUSE_POSITION_X,MOUSE_POSITION_Y) :getMousePoint(e);
  var element=document.elementFromPoint(toPoint.x,toPoint.y);
  var rect=new Rect();
  amiUserLinkingDest=null;
  for(var i in amiLinkableDivs){
  	if(rect.readFromElement(amiLinkableDivs[i]).pointInside(toPoint)){
      context.strokeStyle='#0000AA';
      toPoint=rect.getMidpoint();
      amiUserLinkingDest=i.portletId;
  	  break;
    }
  }
  drawArc(context,toPoint.x,toPoint.y,frPoint.x,frPoint.y,20,1,10);
  context.stroke();
}

var amiUserLinkingDest=null;
var amiUserLinkingCanvas=null;
function amiStopLink(id){
	if(amiUserLinkingCanvas!=null){
      document.body.removeChild(amiUserLinkingCanvas);
      amiUserLinkingCanvas=null;
      document.removeEventListener('mousemove', amiLinkingOnMouseMove, false)
	}
}
function amiStartLink(id){
	amiUserLinkingSource=id;
	if(amiUserLinkingCanvas!=null){
      document.body.removeChild(amiUserLinkingCanvas);
      amiUserLinkingCanvas=null;
	}
    document.addEventListener('mousemove', amiLinkingOnMouseMove, false)
    amiUserLinkingCanvas=nw('canvas','ami_user_linking_canvas');
    amiUserLinkingCanvas.width=getDocumentWidth(window);
    amiUserLinkingCanvas.height=getDocumentHeight(window);
    document.body.appendChild(amiUserLinkingCanvas);
    var context = amiUserLinkingCanvas.getContext('2d');
    context.strokeStyle='#0000AA';
    context.lineWidth=3;
    amiLinkingOnMouseMove(null);
}
var amiLinkCanvas=null;

function removeAmiLinkCanvas(){
	if(amiLinkCanvas!=null){
      document.body.removeChild(amiLinkCanvas);
      amiLinkCanvas=null;
	}
}

function amiOverlayForLinking(p1,text,style){
	var portlet=Portlets[p1];
	if(portlet){
		if(portlet.amiDiv2){
		  portlet.divElement.removeChild(portlet.amiDiv2);
		  portlet.amiDiv=null;
		}
	    var div=nw('div');
	    applyStyle(div,style);
	    div.innerHTML=text;
	    portlet.amiDiv2=div;
	    portlet.divElement.appendChild(div);
	  }
}
function amiDrawLinking(p1,p2){
	if(amiUserLinkingCanvas!=null){
      document.body.removeChild(amiUserLinkingCanvas);
      amiUserLinkingCanvas=null;
	}
	if(p1 && p2){
      amiUserLinkingCanvas=nw('canvas','ami_user_linking_canvas');
      amiUserLinkingCanvas.width=getDocumentWidth(window);
      amiUserLinkingCanvas.height=getDocumentHeight(window);
      document.body.appendChild(amiUserLinkingCanvas);
      var context = amiUserLinkingCanvas.getContext('2d');
      context.strokeStyle='white';
      context.lineWidth=8;
      context.beginPath();
      var frPoint=new Rect().readFromElement(Portlets[p1].divElement).getMidpoint();
      var toPoint=new Rect().readFromElement(Portlets[p2].divElement).getMidpoint();
      drawArc(context,toPoint.x,toPoint.y,frPoint.x,frPoint.y,0,1,30);
      context.stroke();
	}
}
			
var amiLinkableDivs=[];
function amiEditDesktop(edit,callbackPortlet,portletIds,help,windowLinks){
	amiLinkableDivs=[];
	removeAmiLinkCanvas();
    for(var i in portletIds)
      removeAmiEditButton(portletIds[i]);
	if(edit){
			//addAmiEditButton(callbackPortlet,portlet.portletId,help);
		for(var i in portletIds)
			addAmiEditButton(callbackPortlet,portletIds[i],help);
        amiLinkCanvas=nw('canvas','ami_link_canvas');
        document.body.appendChild(amiLinkCanvas);
        var context = amiLinkCanvas.getContext('2d');
        var rect=new Rect();
        amiLinkCanvas.width=getDocumentWidth(window);
        amiLinkCanvas.height=getDocumentHeight(window);
        context.clearRect(0,0,amiLinkCanvas.width,amiLinkCanvas.height);
        context.strokeStyle='#0000AA';
        context.lineWidth=3;
        for(var j in windowLinks){
          var wl=windowLinks[j];
          var links=wl.links;
          context.clearRect(wl.x-4,wl.y+8,wl.w+8,wl.h+25);
          for(var i in links){
        	var link=links[i];
        	var frPoint,toPoint;
        	if(link.frPortlet!=null){
        	  frPoint=rect.readFromElement(Portlets[link.frPortlet].divElement).getMidpoint();
        	} else if(link.frTab){
        	  var tab=Portlets[link.frTab];
        	  frPoint=rect.readFromElement(tab.tabs[link.frTabIndex] ).getMidpoint();
        	}
        	
        	if(link.toPortlet!=null){
        	  toPoint=rect.readFromElement(Portlets[link.toPortlet].divElement).getMidpoint();
        	} else if(link.toTab){
        	  var tab=Portlets[link.toTab];
        	  toPoint=rect.readFromElement(tab.tabs[link.toTabIndex] ).getMidpoint();
        	}
              drawArc(context,fl(frPoint.x),fl(frPoint.y),fl(toPoint.x),fl(toPoint.y), 20,1,10);
              context.stroke();
          }
        }
	}
}
function removeAmiEditButton(i){
	var portlet=Portlets[i];
	if(!portlet)
		return;
	if(portlet.amiDiv){
		portlet.divElement.removeChild(portlet.amiDiv);
		portlet.amiDiv=null;
	    portlet.onDividerLocation=null;
	}
	if(portlet.amiDiv2){
		portlet.divElement.removeChild(portlet.amiDiv2);
		portlet.amiDiv2=null;
	}
    //for(var i in portlet.childPortletIds){
	  //removeAmiEditButton(i);
	//}
}

function addAmiEditButton(callbackPortlet,i,help){
	var portlet=Portlets[i];
	var div;
	if(!portlet)
		return;
	if(portlet.amiDiv)//TODO: should be needed
		return;
	if(portlet instanceof DividerPortlet){
	  if(portlet.vertical){
	    div=nw('div','ami_desktop_edit_button_v');
	    div.style.left=toPx(portlet.offset+portlet.thickness/2);
	    portlet.onDividerLocation=function(offset){
	      div.style.left=toPx(portlet.offset+portlet.thickness/2);
  	      removeAmiLinkCanvas();
	    }
	  }else{
	    div=nw('div','ami_desktop_edit_button_h');
	    div.style.top=toPx(portlet.offset+portlet.thickness/2);
	    portlet.onDividerLocation=function(offset){
  	      div.style.top=toPx(portlet.offset+portlet.thickness/2);
  	      removeAmiLinkCanvas();
	    }
	  }
	}else if(portlet instanceof DesktopPortlet){
	  portlet.desktop.onWindowLocation=function(){
  	    removeAmiLinkCanvas();
	  }
	}else if(portlet instanceof TabPortlet){
	  div=nw('div','ami_desktop_edit_button_tab');
	}else{
		if(help){
	      div=nw('div','ami_desktop_edit_button');
	      div.innerHTML="<div class='ami_help_bubble2' >"+help+"</div>";
		}else
	      div=nw('div','ami_desktop_edit_button');
		amiLinkableDivs[amiLinkableDivs.length]=div;
	}
	if(div!=null){
	  div.portletId=i;
	  portlet.divElement.appendChild(div);
	  var id=i;
	  div.onmousedown=function(){onAmiEditButtonClicked(callbackPortlet,id)};
	  portlet.amiDiv=div;
	}
    //for(var i in portlet.childPortletIds){
	  //addAmiEditButton(callbackPortlet,i);
	//}
}

function onAmiEditButtonClicked(callbackPortlet,id){
	//div.style.background='blue'; 
	var p=Portlets[id];
	if(p){
	  callbackPortlet.callBack('edit_portlet',{id:id});
	  if(amiUserLinkingCanvas==null){
	    if(p instanceof DividerPortlet){
	    	var j=1;
	    	for(var i in p.childPortletIds){
	           var cp=Portlets[i];
	           var div=nw('div','ami_desktop_edit_select');
	           var div2=nw('div','ami_desktop_edit_select2');
	           div.innerHTML=''+j;
	           j++;
	           div.appendChild(div2);
	           cp.amiDiv2=div;
	           cp.divElement.appendChild(div);
	    	}
	    }else{
	      var div=nw('div','ami_desktop_edit_select');
	      var div2=nw('div','ami_desktop_edit_select2');
	      div.appendChild(div2);
	      p.amiDiv2=div;
	      p.divElement.appendChild(div);
	    }
	  }
	}
}

