

AMI_LNCLR='#00AA00';
function isDeveloperMode(){
	if(amiEditDesktopArgs)
		return amiEditDesktopArgs.edit;
	return false;
}
function amiLinkingOnMouseMove(e){
  var srcPortlet=portletManager.getPortletNoThrow(amiUserLinkingSource);
  var frPoint=srcPortlet==null ? new Point(0,0) : new Rect().readFromElement(srcPortlet.divElement).getMidpoint();
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
    var d=amiLinkableDivs[i];
  	if(rect.readFromElement(d).pointInside(toPoint)){
      context.strokeStyle=AMI_LNCLR;
      toPoint=rect.getMidpoint();
      amiUserLinkingDest=i.portletId;
  	  break;
    }
  }
  drawArc(context,toPoint.x,toPoint.y,frPoint.x,frPoint.y,30,1,10);
  context.stroke();
}

var amiUserLinkingDest=null;
var amiUserLinkingCanvas=null;
var amiUserLinkingSource=null;
function amiStopLink(id){
	if(amiUserLinkingCanvas!=null){
      document.body.removeChild(amiUserLinkingCanvas);
      amiUserLinkingCanvas=null;
      document.removeEventListener('mousemove', amiLinkingOnMouseMove, false)
	}
	amiUserLinkingSource = null;
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
    context.strokeStyle=AMI_LNCLR;
    context.lineWidth=4;
    amiLinkingOnMouseMove(null);
}
var amiLinkCanvas=null;

function removeAmiLinkCanvas(){
	if(amiLinkCanvas!=null){
      document.body.removeChild(amiLinkCanvas);
      amiLinkCanvas=null;
	}
}
function removeAmiUserLinkCanvas(){
	if(amiUserLinkingCanvas != null){
		document.body.removeChild(amiUserLinkingCanvas);
		amiUserLinkingCanvas = null;
	}
}

function amiDialogArrow(px,py,x,y,xx,yy){
	var insideX =  ((x <= px) &&(px <= xx)) ||  ((x >= px) &&(px >= xx));
	var insideY =  ((y <= py) &&(py <= yy)) ||  ((y >= py) &&(py >= yy));
	var inside = insideX && insideY;
	if(px!=null && !inside){
	  if(amiUserLinkingCanvas==null){
        amiUserLinkingCanvas=nw('canvas','ami_user_linking_canvas');
        amiUserLinkingCanvas.width=getDocumentWidth(window);
        amiUserLinkingCanvas.height=getDocumentHeight(window);
	  }
      document.body.appendChild(amiUserLinkingCanvas);
      var context = amiUserLinkingCanvas.getContext('2d');
      context.clearRect(0,0,amiUserLinkingCanvas.width,amiUserLinkingCanvas.height);
      context.strokeStyle='rgba(0,118,08,.2)';
      context.fillStyle='rgba(0,118,08,.1)';
      context.lineWidth=1;
      context.beginPath();
      x++; y++;xx--;yy--;
      
      
      var xdiff=px<x ? x-px : (px>xx ? xx-px : 0);
      var ydiff=py<y ? y-py : (py>yy ? yy-py : 0);
      var tx1,ty1,tx2,ty2,tx3=null,ty3;
      if(ydiff==0){
    	 ty1=y;
    	 ty2=yy;
    	 tx1=tx2=xdiff>0 ? x : xx;
      }else if(xdiff==0){
    	 tx1=x;
    	 tx2=xx;
    	 ty1=ty2=ydiff>0 ? y : yy;
      }else {
    	 if(xdiff>0 != ydiff>0){
    	   tx1=x; tx2=xx; ty1=y; ty2=yy;
         }else{
    	   tx1=xx; tx2=x; ty1=y; ty2=yy;
         }
    	 tx3=xdiff>0 ? x : xx;
    	 ty3=ydiff>0 ? y : yy;
      }
//      if(Math.abs(ydiff)<Math.abs(xdiff)){
//    	 ty1=y;
//    	 ty2=yy;
//    	 tx1=tx2=xdiff>0 ? x : xx;
//      }else {
//    	 tx1=x;
//    	 tx2=xx;
//    	 ty1=ty2=ydiff>0 ? y : yy;
//      }
      
      context.moveTo(px,py);
     
      context.lineTo(tx1,ty1);
	  if(py > ty1)
	  	context.lineTo(tx1,ty2);
	  else
	    context.lineTo(tx2,ty1);
      context.lineTo(tx2,ty2);
      context.lineTo(px,py);
      context.fill();
      context.stroke();
      if(tx3!=null){
        context.lineTo(tx3,ty3);
        context.strokeStyle='rgba(0,118,08,.1)';
        context.stroke();
      }
      
      
//      var dx1=Math.abs(x-px) < Math.abs(xx-px) ? x : xx;
//      var dy1=Math.abs(y-py) < Math.abs(yy-py) ? y : yy;
//      
      
      
      
//      context.moveTo(px,py);
//      context.lineTo(dx2,dy2);
	}
	else{
	  if(amiUserLinkingCanvas!=null){
		  var context = amiUserLinkingCanvas.getContext('2d');
		  context.clearRect(0,0,amiUserLinkingCanvas.width,amiUserLinkingCanvas.height);
//		  amiUserLinkingCanvas = null;
	  }
	}
}

function amiOverlayForLinking(p1,text,style){
	var portlet=portletManager.getPortletNoThrow(p1);
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
	if(p1!=null && p2!=null){
      amiUserLinkingCanvas=nw('canvas','ami_user_linking_canvas');
      amiUserLinkingCanvas.width=getDocumentWidth(window);
      amiUserLinkingCanvas.height=getDocumentHeight(window);
      document.body.appendChild(amiUserLinkingCanvas);
      var context = amiUserLinkingCanvas.getContext('2d');
      context.strokeStyle='white';
      context.lineWidth=8;
      context.beginPath();
      var frmPortlet=portletManager.getPortletNoThrow(p1);
      var toPortlet=portletManager.getPortletNoThrow(p2);
      var frPoint=frmPortlet==null ? new Point(0,0) : new Rect().readFromElement(frmPortlet.divElement).getMidpoint();
      var toPoint= toPortlet==null ? new Point(0,0) : new Rect().readFromElement( toPortlet.divElement).getMidpoint();
      drawArc(context,toPoint.x,toPoint.y,frPoint.x,frPoint.y,0,1,30);
      context.stroke();
	}
}

function amiscript(script){
  portletAjax({portletId:amiEditDesktopArgs.callbackPortlet.portletId,script:script,type:'amiscript'});
}
			
var amiLinkableDivs=[];
var amiLinkCounts={};
var amiEditDesktopArgs={};
function amiEditDesktopMode(edit){
	amiEditDesktopArgs.edit=edit;
}
function amiEditDesktop(edit,callbackPortlet,portletSettings,help,windowLinks,desktopId,desktopPortletIds){
	amiEditDesktopArgs.edit=edit;
	amiEditDesktopArgs.callbackPortlet=callbackPortlet;
	amiEditDesktopArgs.portletSettings=portletSettings;
	amiEditDesktopArgs.help=help;
	amiEditDesktopArgs.windowLinks=windowLinks;
	amiEditDesktopArgs.desktopId=desktopId;
	amiEditDesktopArgs.desktopPortletIds=desktopPortletIds;
	amiEditDesktopRefresh();
}

function amiEditDesktopRefresh(){
	var edit=amiEditDesktopArgs.edit;
	var callbackPortlet=amiEditDesktopArgs.callbackPortlet;
	var portletSettings=amiEditDesktopArgs.portletSettings;
	var help=amiEditDesktopArgs.help;
	var windowLinks=amiEditDesktopArgs.windowLinks;
	var desktopId=amiEditDesktopArgs.desktopId;
	var desktopPortletIds=amiEditDesktopArgs.desktopPortletIds;
    amiLinkCounts={};
	amiLinkableDivs=[];
	//Remove linking canvas if a link hasn't been started
	if(amiUserLinkingSource == null)
		removeAmiUserLinkCanvas();
	removeAmiLinkCanvas();
    var desktop=portletManager.getPortlet(desktopId).desktop;
    for(var i in portletSettings)
      removeAmiEditButton(i);
    for(var i in desktopPortletIds){
    	var w=desktop.getWindow(desktopPortletIds[i].id);
    	if(w!=null){
    	  if(w.amiDiv){
	        w.getHeaderDiv().removeChild(w.amiDiv);
	        w.amiDiv=null;
    	  }
    	  if(w.amiDiv2){
	        w.getHeaderDiv().removeChild(w.amiDiv2);
	        w.amiDiv2=null;
    	  }
    	  if(w.shader){
  	        w.getHeaderDiv().removeChild(w.shader);
  	        w.shader=null;
      	  }
	}
    }
	if(edit){
			//addAmiEditButton(callbackPortlet,portlet.portletId,help);
		for(var i in portletSettings)
			addAmiEditButton(callbackPortlet,i,portletSettings[i],help);
        amiLinkCanvas=nw('canvas','ami_link_canvas');
        document.body.appendChild(amiLinkCanvas);
        var context = amiLinkCanvas.getContext('2d');
        var rect=new Rect();
        amiLinkCanvas.width=getDocumentWidth(window);
        amiLinkCanvas.height=getDocumentHeight(window);
        context.clearRect(0,0,amiLinkCanvas.width,amiLinkCanvas.height);
        context.strokeStyle=AMI_LNCLR;
        context.lineWidth=4;
        for(var j in windowLinks){
          var wl=windowLinks[j];
          var links=wl.links;
          context.clearRect(wl.x-5,wl.y+8,wl.w+9,wl.h+25);
          for(var i in links){
        	var link=links[i];
        	var frPoint,toPoint;
        	if(link.frPortlet!=null){
        	  frPoint=rect.readFromElement(portletManager.getPortletNoThrow(link.frPortlet).divElement).getMidpoint();
        	} else if(link.frTab){
        	  var tab=portletManager.getPortletNoThrow(link.frTab);
        	  frPoint=rect.readFromElement(tab.tabs[link.frTabIndex] ).getMidpoint();
        	}
        	
        	if(link.toPortlet!=null){
        	  toPoint=rect.readFromElement(portletManager.getPortletNoThrow(link.toPortlet).divElement).getMidpoint();
        	} else if(link.toTab){
        	  var tab=portletManager.getPortletNoThrow(link.toTab);
        	  toPoint=rect.readFromElement(tab.tabs[link.toTabIndex] ).getMidpoint();
        	}
        	var cntId=frPoint.x+":"+frPoint.y+":"+toPoint.x+":"+toPoint.y;
        	var cnt=amiLinkCounts[cntId];
        	if(cnt==null)
        		cnt=1;
        	else
        		cnt++;
        	amiLinkCounts[cntId]=cnt;
            drawArc(context,rd(frPoint.x),rd(frPoint.y),rd(toPoint.x),rd(toPoint.y), 10+20*cnt,1,10);
            context.stroke();
          }
        }
        for(var i in desktopPortletIds){
        	var t=desktopPortletIds[i];
        	  addWindowEditButton(callbackPortlet,desktop,t);
        }
	}
}
function addWindowEditButton(callbackPortlet,desktop,t){
  var id=t.id;
  var type=t.type;
  var w=desktop.getWindow(id);
  if(w==null)
	return;
  var div=nw('div','ami_desktop_edit_button_window');
  if(type=='M')
    div.style.marginBottom='-18px';  
  w.getHeaderDiv().appendChild(div);
  w.amiDiv=div;
  div.onmouseup=function(){onAmiEditWindowButtonClicked(callbackPortlet,id)};
  if(!t.d){
    var div=nw('div','ami_desktop_edit_button_default_location');
    div.style.marginLeft='28px';  
    w.getHeaderDiv().appendChild(div);
    w.amiDiv2=div;
    div.onmouseup=function(){onAmiSetDefaultWindowButtonClicked(callbackPortlet,id)};
  }
}
function removeAmiEditButton(i){
	var portlet=portletManager.getPortletNoThrow(i);
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
	if(portlet.shader){
		portlet.divElement.removeChild(portlet.shader);
		portlet.shader=null;
	}
}
function callbackUpdateDashboard(callbackPortlet){
  callbackPortlet.callBack('update_dashboard',{});
}
function isTransientOrReadOnly(settings) {
	return settings.isTransient || settings.isReadOnly;
}
function getResolvedDividerIconPath(isVertical, settings) {
	var iconName = "";
	if (isVertical) {
		if(settings.locked=="ratio")
	      iconName = isTransientOrReadOnly(settings) ? "transient_config_button_v_lock_ratio.svg" : "config_button_v_lock_ratio.png";
	    else if(settings.locked=="top")
	      iconName = isTransientOrReadOnly(settings) ? "transient_config_button_v_lock_left.svg" :"config_button_v_lock_left.png";
	    else if(settings.locked=="bottom")
	      iconName = isTransientOrReadOnly(settings) ? "transient_config_button_v_lock_right.svg": "config_button_v_lock_right.png";
	    else
	      iconName = isTransientOrReadOnly(settings) ? "transient_config_button_v.svg" : "config_button_v.png";
	} else {
		if(settings.locked=="ratio")
	      iconName = isTransientOrReadOnly(settings) ?  "transient_config_button_h_lock_ratio.svg" : "config_button_h_lock_ratio.png";
	    else if(settings.locked=="top")
	      iconName = isTransientOrReadOnly(settings) ? "transient_config_button_h_lock_top.svg" : "config_button_h_lock_top.png";
	    else if(settings.locked=="bottom")
	      iconName = isTransientOrReadOnly(settings) ? "transient_config_button_h_lock_bottom.svg" : "config_button_h_lock_bottom.png";
	    else
	      iconName = isTransientOrReadOnly(settings) ? "transient_config_button_h.svg" : "config_button_h.png";
	}
	var output = "url('../portal/rsc/ami/" + iconName +  "')";
	return output;
}
function addAmiEditButton(callbackPortlet,portletId,settings,help){
	var portlet=portletManager.getPortletNoThrow(portletId);
	var div,div2;
	if(!portlet)
		return;
	if(portlet.amiDiv)//TODO: should be needed
		return;
	if(settings.type=='divider'){ 
	  var dividerPortlet=portletManager.getPortletNoThrow(settings.innerId);
	    labelDiv = nw('div', 'ami_desktop_divider_label');
		labelDiv.innerHTML = settings.label;
		labelDiv.style.textShadow = "-2px -1px white,-2px 0px white,-2px 1px white, -1px -2px white,-1px -1px white,-1px 0px white,-1px 1px white,-1px 2px white, 0px -2px white, 0px -1px white, 0px 1px white, 0px 2px white, 1px -2px white, 1px -1px white, 1px 0px white, 1px 1px white, 1px 2px white, 2px -1px white, 2px 0px white, 2px 1px white";
	  if(dividerPortlet.vertical){
		var divIconCss = isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_v ami_desktop_edit_button_v_transient' : 'ami_desktop_edit_button_v';
	    div=nw('div', divIconCss );
		labelDiv.style.marginTop="-14px";
		div.appendChild(labelDiv);
	    div.style.backgroundImage=getResolvedDividerIconPath(dividerPortlet.vertical, settings);
	    div.style.left=toPx(dividerPortlet.offset+dividerPortlet.thickness/2 + getAbsoluteLeft(dividerPortlet.divElement)-getAbsoluteLeft(portlet.divElement));
	    if(!settings.isDefault){
	      div2=nw('div', 'ami_desktop_edit_button_default_location' );
	      div2.style.left=toPx(dividerPortlet.offset+dividerPortlet.thickness/2 + getAbsoluteLeft(dividerPortlet.divElement)-getAbsoluteLeft(portlet.divElement));
	      div2.style.margin='-40px -10px';
	    }
	    dividerPortlet.onDividerLocation=function(offset){
	      div.style.left=toPx(dividerPortlet.offset+dividerPortlet.thickness/2 + getAbsoluteLeft(dividerPortlet.divElement)-getAbsoluteLeft(portlet.divElement));
	      if(div2!=null)
	        div2.style.left=toPx(dividerPortlet.offset+dividerPortlet.thickness/2 + getAbsoluteLeft(dividerPortlet.divElement)-getAbsoluteLeft(portlet.divElement));
	      removeAmiLinkCanvas();
	    }
	  }else{
		var divIconCss = isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_h ami_desktop_edit_button_h_transient' : 'ami_desktop_edit_button_h';
	    div=nw('div', divIconCss );
		labelDiv.style.marginTop='20px';
		labelDiv.style.marginLeft='6px';
		div.appendChild(labelDiv);
	    div.style.backgroundImage=getResolvedDividerIconPath(dividerPortlet.vertical, settings);
  	    div.style.top=toPx(dividerPortlet.offset+dividerPortlet.thickness/2+ getAbsoluteTop(dividerPortlet.divElement)-getAbsoluteTop(portlet.divElement));
	    if(!settings.isDefault){
	      div2=nw('div', 'ami_desktop_edit_button_default_location' );
  	      div2.style.top=toPx(dividerPortlet.offset+dividerPortlet.thickness/2+ getAbsoluteTop(dividerPortlet.divElement)-getAbsoluteTop(portlet.divElement));
	      div2.style.margin='-11px 20px';
	    }
	    dividerPortlet.onDividerLocation=function(offset){
  	      div.style.top=toPx(dividerPortlet.offset+dividerPortlet.thickness/2+ getAbsoluteTop(dividerPortlet.divElement)-getAbsoluteTop(portlet.divElement));
	      if(div2!=null)
  	        div2.style.top=toPx(dividerPortlet.offset+dividerPortlet.thickness/2+ getAbsoluteTop(dividerPortlet.divElement)-getAbsoluteTop(portlet.divElement));
	      removeAmiLinkCanvas();
	    }
	  }
	  makeDraggable(div,dividerPortlet.dividerDragger,!dividerPortlet.vertical,dividerPortlet.vertical);
      div.ondragging=function(e,x,y){if(Math.abs(x)>4|| Math.abs(y)>4|| div.onmouseup==null){
    	  div.onmouseup=null;
    	  dividerPortlet.dividerDragger.ondragging(e,x,y);}};
      div.ondraggingEnd=function(e,x,y) {
    	  div.onmouseup=function(){
		  onAmiEditButtonClicked(callbackPortlet,id,settings)
		  };
		  dividerPortlet.dividerDragger.ondraggingEnd(e,x,y);
      }
	}else if(portlet instanceof DesktopPortlet){
	  portlet.desktop.onWindowLocation=function(left,top,width,height,isMax,zindex,active,fromBackend){
	      removeAmiLinkCanvas();
	      if(fromBackend)
	        callbackUpdateDashboard(callbackPortlet);
	  }
	}else if(portlet instanceof AmiAxisPortlet){
	  div=nw('div', isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_axis ami_desktop_edit_button_transient' : 'ami_desktop_edit_button_axis');
	}else if(portlet instanceof AmiPlotPortlet){
	  div=nw('div', isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_plot ami_desktop_edit_button_transient' : 'ami_desktop_edit_button_plot');
	}else if(settings.type=='tab'){
//	  var tabPortlet = portletManager.getPortletNoThrow(settings.innerId); might be useful later
	  div=nw('div', isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_tab ami_desktop_edit_button_transient' : 'ami_desktop_edit_button_tab');
	  labelDiv = nw('div', 'ami_desktop_tabpanel_label');
	  labelDiv.innerHTML = settings.label;
	  labelDiv.style.textShadow = "-2px -1px white,-2px 0px white,-2px 1px white, -1px -2px white,-1px -1px white,-1px 0px white,-1px 1px white,-1px 2px white, 0px -2px white, 0px -1px white, 0px 1px white, 0px 2px white, 1px -2px white, 1px -1px white, 1px 0px white, 1px 1px white, 1px 2px white, 2px -1px white, 2px 0px white, 2px 1px white";
	  div.appendChild(labelDiv);
	  if(settings.d){
	    div2=nw('div', 'ami_desktop_edit_button_tab_default_location' );
	    if(settings.right){
	       div2.style.right='20px';
	    }else{
	       div2.style.left='20px';
	    }
	    if(settings.bottom){
	       div2.style.bottom='0px';
	    }else{
	       div2.style.top='0px';
	    }
	  }

	  
	  if(settings.right) { 
		  div.style.right='1px';
		  labelDiv.style.right=toPx(36);
	  }else {
		  div.style.left='1px';
		  labelDiv.style.left=toPx(36);
	  }
	  if(settings.bottom){ 
		  div.style.bottom='1px'; 
	  } else { 
		  div.style.top='1px';
	  }
	}else{
		var buttonCssClass = 'ami_desktop_edit_button';
		buttonCssClass = settings.designMode == true? 'ami_desktop_edit_button ami_desktop_design_button': buttonCssClass;
		buttonCssClass = isTransientOrReadOnly(settings) == true ? buttonCssClass + " ami_desktop_edit_button_transient" : buttonCssClass;
	      div=nw('div',buttonCssClass);
		if(help){
	      div.innerHTML="<div class='ami_help_bubble2' >"+help+"</div>";
		}else{
	      div.innerHTML=settings.label;
		}
		amiLinkableDivs[amiLinkableDivs.length]=div;
	}
	if(div!=null){
	  div.portletId=portletId;
	  portlet.divElement.appendChild(div);
	  var id=portletId;
	  div.onmouseup=function(){
		  onAmiEditButtonClicked(callbackPortlet,id,settings)
		  };
	  portlet.amiDiv=div;
	}
	if(div2!=null){
	  div2.portletId=portletId;
	  portlet.divElement.appendChild(div2);
	  var id=portletId;
	  div2.onmouseup=function(){onAmiSetDefaultButtonClicked(callbackPortlet,id,settings)};
	  portlet.amiDiv2=div2;
	}
}
function onAmiSetDefaultWindowButtonClicked(callbackPortlet,id){
	  callbackPortlet.callBack('set_default_window',{id:id});
}

function onAmiEditWindowButtonClicked(callbackPortlet,id){
	  callbackPortlet.callBack('edit_window',{id:id});
}
function onAmiSetDefaultButtonClicked(callbackPortlet,id,settings){
 callbackPortlet.callBack('set_default',{id:id});
}
function onAmiEditButtonClicked(callbackPortlet,id,settings){
	var p=portletManager.getPortletNoThrow(id);
	if(p){
	  callbackPortlet.callBack('edit_portlet',{id:id});
	  if(amiUserLinkingCanvas==null){
	    if(settings.type=='divider'){
	    	var j=1;
          	p=portletManager.getPortletNoThrow(settings.innerId);
	    	for(var i in p.childPortletIds){
	          var cp=portletManager.getPortletNoThrow(i);
	          if(cp.shader==null){
	            var div=nw('div','ami_desktop_edit_select');
	            var div2=nw('div','ami_desktop_edit_select2');
	            div.innerHTML=''+j;
	            j++;
	            div.appendChild(div2);
	            cp.shader=div;
	            cp.divElement.appendChild(div);
	          }
	    	}
	    }else{
	      if(p.shader==null){
	        var div=nw('div','ami_desktop_edit_select');
	        var div2=nw('div','ami_desktop_edit_select2');
	        div.appendChild(div2);
	        p.shader=div;
	        p.divElement.appendChild(div);
	      }
	    }
	  }
	}
}

function amiSetCustomCss(css){
	var windows=portletManager.windows;
	for(var i in windows){
	   amiSetCustomCssForWindow(windows[i],css);
	}
}
function amiSetCustomCssForWindow(window,css){
//	log(portletManager);
	var styleTag = window.document.getElementById ("ami_custom_styles");
	var sheet = styleTag.sheet ;
	while(sheet.cssRules.length>0)
	    sheet.deleteRule (sheet.cssRules.length-1);
	for(var v in css){
		try{
	      sheet.insertRule(css[v]);
		}catch(error){
			log("For css: "+css[v]);
			log(error);
		}
	}
}

