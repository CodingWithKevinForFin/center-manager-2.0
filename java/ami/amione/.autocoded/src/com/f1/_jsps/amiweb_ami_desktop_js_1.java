package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ami_desktop_js_1 extends AbstractHttpHandler{

	public amiweb_ami_desktop_js_1() {
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
            "\r\n"+
            "\r\n"+
            "AMI_LNCLR='#00AA00';\r\n"+
            "function isDeveloperMode(){\r\n"+
            "	if(amiEditDesktopArgs)\r\n"+
            "		return amiEditDesktopArgs.edit;\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "function amiLinkingOnMouseMove(e){\r\n"+
            "  var srcPortlet=portletManager.getPortletNoThrow(amiUserLinkingSource);\r\n"+
            "  var frPoint=srcPortlet==null ? new Point(0,0) : new Rect().readFromElement(srcPortlet.divElement).getMidpoint();\r\n"+
            "  var context = amiUserLinkingCanvas.getContext('2d');\r\n"+
            "  context.clearRect(0,0,amiUserLinkingCanvas.width,amiUserLinkingCanvas.height);\r\n"+
            "  context.beginPath();\r\n"+
            "  context.strokeStyle='#888888';\r\n"+
            "  context.lineWidth=3;\r\n"+
            "  var toPoint = e==null ? new Point(MOUSE_POSITION_X,MOUSE_POSITION_Y) :getMousePoint(e);\r\n"+
            "  var element=document.elementFromPoint(toPoint.x,toPoint.y);\r\n"+
            "  var rect=new Rect();\r\n"+
            "  amiUserLinkingDest=null;\r\n"+
            "  for(var i in amiLinkableDivs){\r\n"+
            "    var d=amiLinkableDivs[i];\r\n"+
            "  	if(rect.readFromElement(d).pointInside(toPoint)){\r\n"+
            "      context.strokeStyle=AMI_LNCLR;\r\n"+
            "      toPoint=rect.getMidpoint();\r\n"+
            "      amiUserLinkingDest=i.portletId;\r\n"+
            "  	  break;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  drawArc(context,toPoint.x,toPoint.y,frPoint.x,frPoint.y,30,1,10);\r\n"+
            "  context.stroke();\r\n"+
            "}\r\n"+
            "\r\n"+
            "var amiUserLinkingDest=null;\r\n"+
            "var amiUserLinkingCanvas=null;\r\n"+
            "var amiUserLinkingSource=null;\r\n"+
            "function amiStopLink(id){\r\n"+
            "	if(amiUserLinkingCanvas!=null){\r\n"+
            "      document.body.removeChild(amiUserLinkingCanvas);\r\n"+
            "      amiUserLinkingCanvas=null;\r\n"+
            "      document.removeEventListener('mousemove', amiLinkingOnMouseMove, false)\r\n"+
            "	}\r\n"+
            "	amiUserLinkingSource = null;\r\n"+
            "}\r\n"+
            "function amiStartLink(id){\r\n"+
            "	amiUserLinkingSource=id;\r\n"+
            "	if(amiUserLinkingCanvas!=null){\r\n"+
            "      document.body.removeChild(amiUserLinkingCanvas);\r\n"+
            "      amiUserLinkingCanvas=null;\r\n"+
            "	}\r\n"+
            "    document.addEventListener('mousemove', amiLinkingOnMouseMove, false)\r\n"+
            "    amiUserLinkingCanvas=nw('canvas','ami_user_linking_canvas');\r\n"+
            "    amiUserLinkingCanvas.width=getDocumentWidth(window);\r\n"+
            "    amiUserLinkingCanvas.height=getDocumentHeight(window);\r\n"+
            "    document.body.appendChild(amiUserLinkingCanvas);\r\n"+
            "    var context = amiUserLinkingCanvas.getContext('2d');\r\n"+
            "    context.strokeStyle=AMI_LNCLR;\r\n"+
            "    context.lineWidth=4;\r\n"+
            "    amiLinkingOnMouseMove(null);\r\n"+
            "}\r\n"+
            "var amiLinkCanvas=null;\r\n"+
            "\r\n"+
            "function removeAmiLinkCanvas(){\r\n"+
            "	if(amiLinkCanvas!=null){\r\n"+
            "      document.body.removeChild(amiLinkCanvas);\r\n"+
            "      amiLinkCanvas=null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "function removeAmiUserLinkCanvas(){\r\n"+
            "	if(amiUserLinkingCanvas != null){\r\n"+
            "		document.body.removeChild(amiUserLinkingCanvas);\r\n"+
            "		amiUserLinkingCanvas = null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function amiDialogArrow(px,py,x,y,xx,yy){\r\n"+
            "	var insideX =  ((x <= px) &&(px <= xx)) ||  ((x >= px) &&(px >= xx));\r\n"+
            "	var insideY =  ((y <= py) &&(py <= yy)) ||  ((y >= py) &&(py >= yy));\r\n"+
            "	var inside = insideX && insideY;\r\n"+
            "	if(px!=null && !inside){\r\n"+
            "	  if(amiUserLinkingCanvas==null){\r\n"+
            "        amiUserLinkingCanvas=nw('canvas','ami_user_linking_canvas');\r\n"+
            "        amiUserLinkingCanvas.width=getDocumentWidth(window);\r\n"+
            "        amiUserLinkingCanvas.height=getDocumentHeight(window);\r\n"+
            "	  }\r\n"+
            "      document.body.appendChild(amiUserLinkingCanvas);\r\n"+
            "      var context = amiUserLinkingCanvas.getContext('2d');\r\n"+
            "      context.clearRect(0,0,amiUserLinkingCanvas.width,amiUserLinkingCanvas.height);\r\n"+
            "      context.strokeStyle='rgba(0,118,08,.2)';\r\n"+
            "      context.fillStyle='rgba(0,118,08,.1)';\r\n"+
            "      context.lineWidth=1;\r\n"+
            "      context.beginPath();\r\n"+
            "      x++; y++;xx--;yy--;\r\n"+
            "      \r\n"+
            "      \r\n"+
            "      var xdiff=px<x ? x-px : (px>xx ? xx-px : 0);\r\n"+
            "      var ydiff=py<y ? y-py : (py>yy ? yy-py : 0);\r\n"+
            "      var tx1,ty1,tx2,ty2,tx3=null,ty3;\r\n"+
            "      if(ydiff==0){\r\n"+
            "    	 ty1=y;\r\n"+
            "    	 ty2=yy;\r\n"+
            "    	 tx1=tx2=xdiff>0 ? x : xx;\r\n"+
            "      }else if(xdiff==0){\r\n"+
            "    	 tx1=x;\r\n"+
            "    	 tx2=xx;\r\n"+
            "    	 ty1=ty2=ydiff>0 ? y : yy;\r\n"+
            "      }else {\r\n"+
            "    	 if(xdiff>0 != ydiff>0){\r\n"+
            "    	   tx1=x; tx2=xx; ty1=y; ty2=yy;\r\n"+
            "         }else{\r\n"+
            "    	   tx1=xx; tx2=x; ty1=y; ty2=yy;\r\n"+
            "         }\r\n"+
            "    	 tx3=xdiff>0 ? x : xx;\r\n"+
            "    	 ty3=ydiff>0 ? y : yy;\r\n"+
            "      }\r\n"+
            "//      if(Math.abs(ydiff)<Math.abs(xdiff)){\r\n"+
            "//    	 ty1=y;\r\n"+
            "//    	 ty2=yy;\r\n"+
            "//    	 tx1=tx2=xdiff>0 ? x : xx;\r\n"+
            "//      }else {\r\n"+
            "//    	 tx1=x;\r\n"+
            "//    	 tx2=xx;\r\n"+
            "//    	 ty1=ty2=ydiff>0 ? y : yy;\r\n"+
            "//      }\r\n"+
            "      \r\n"+
            "      context.moveTo(px,py);\r\n"+
            "     \r\n"+
            "      context.lineTo(tx1,ty1);\r\n"+
            "	  if(py > ty1)\r\n"+
            "	  	context.lineTo(tx1,ty2);\r\n"+
            "	  else\r\n"+
            "	    context.lineTo(tx2,ty1);\r\n"+
            "      context.lineTo(tx2,ty2);\r\n"+
            "      context.lineTo(px,py);\r\n"+
            "      context.fill();\r\n"+
            "      context.stroke();\r\n"+
            "      if(tx3!=null){\r\n"+
            "        context.lineTo(tx3,ty3);\r\n"+
            "        context.strokeStyle='rgba(0,118,08,.1)';\r\n"+
            "        context.stroke();\r\n"+
            "      }\r\n"+
            "      \r\n"+
            "      \r\n"+
            "//      var dx1=Math.abs(x-px) < Math.abs(xx-px) ? x : xx;\r\n"+
            "//      var dy1=Math.abs(y-py) < Math.abs(yy-py) ? y : yy;\r\n"+
            "//      \r\n"+
            "      \r\n"+
            "      \r\n"+
            "      \r\n"+
            "//      context.moveTo(px,py);\r\n"+
            "//      context.lineTo(dx2,dy2);\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "	  if(amiUserLinkingCanvas!=null){\r\n"+
            "		  var context = amiUserLinkingCanvas.getContext('2d');\r\n"+
            "		  context.clearRect(0,0,amiUserLinkingCanvas.width,amiUserLinkingCanvas.height);\r\n"+
            "//		  amiUserLinkingCanvas = null;\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function amiOverlayForLinking(p1,text,style){\r\n"+
            "	var portlet=portletManager.getPortletNoThrow(p1);\r\n"+
            "	if(portlet){\r\n"+
            "		if(portlet.amiDiv2){\r\n"+
            "		  portlet.divElement.removeChild(portlet.amiDiv2);\r\n"+
            "		  portlet.amiDiv=null;\r\n"+
            "		}\r\n"+
            "	    var div=nw('div');\r\n"+
            "	    applyStyle(div,style);\r\n"+
            "	    div.innerHTML=text;\r\n"+
            "	    portlet.amiDiv2=div;\r\n"+
            "	    portlet.divElement.appendChild(div);\r\n"+
            "	  }\r\n"+
            "}\r\n"+
            "function amiDrawLinking(p1,p2){\r\n"+
            "	if(amiUserLinkingCanvas!=null){\r\n"+
            "      document.body.removeChild(amiUserLinkingCanvas);\r\n"+
            "      amiUserLinkingCanvas=null;\r\n"+
            "	}\r\n"+
            "	if(p1!=null && p2!=null){\r\n"+
            "      amiUserLinkingCanvas=nw('canvas','ami_user_linking_canvas');\r\n"+
            "      amiUserLinkingCanvas.width=getDocumentWidth(window);\r\n"+
            "      amiUserLinkingCanvas.height=getDocumentHeight(window);\r\n"+
            "      document.body.appendChild(amiUserLinkingCanvas);\r\n"+
            "      var context = amiUserLinkingCanvas.getContext('2d');\r\n"+
            "      context.strokeStyle='white';\r\n"+
            "      context.lineWidth=8;\r\n"+
            "      context.beginPath();\r\n"+
            "      var frmPortlet=portletManager.getPortletNoThrow(p1);\r\n"+
            "      var toPortlet=portletManager.getPortletNoThrow(p2);\r\n"+
            "      var frPoint=frmPortlet==null ? new Point(0,0) : new Rect().readFromElement(frmPortlet.divElement).getMidpoint();\r\n"+
            "      var toPoint= toPortlet==null ? new Point(0,0) : new Rect().readFromElement( toPortlet.divElement).getMidpoint();\r\n"+
            "      drawArc(context,toPoint.x,toPoint.y,frPoint.x,frPoint.y,0,1,30);\r\n"+
            "      context.stroke();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function amiscript(script){\r\n"+
            "  portletAjax({portletId:amiEditDesktopArgs.callbackPortlet.portletId,script:script,type:'amiscript'});\r\n"+
            "}\r\n"+
            "			\r\n"+
            "var amiLinkableDivs=[];\r\n"+
            "var amiLinkCounts={};\r\n"+
            "var amiEditDesktopArgs={};\r\n"+
            "function amiEditDesktopMode(edit){\r\n"+
            "	amiEditDesktopArgs.edit=edit;\r\n"+
            "}\r\n"+
            "function amiEditDesktop(edit,callbackPortlet,portletSettings,help,windowLinks,desktopId,desktopPortletIds){\r\n"+
            "	amiEditDesktopArgs.edit=edit;\r\n"+
            "	amiEditDesktopArgs.callbackPortlet=callbackPortlet;\r\n"+
            "	amiEditDesktopArgs.portletSettings=portletSettings;\r\n"+
            "	amiEditDesktopArgs.help=help;\r\n"+
            "	amiEditDesktopArgs.windowLinks=windowLinks;\r\n"+
            "	amiEditDesktopArgs.desktopId=desktopId;\r\n"+
            "	amiEditDesktopArgs.desktopPortletIds=desktopPortletIds;\r\n"+
            "	amiEditDesktopRefresh();\r\n"+
            "}\r\n"+
            "\r\n"+
            "function amiEditDesktopRefresh(){\r\n"+
            "	var edit=amiEditDesktopArgs.edit;\r\n"+
            "	var callbackPortlet=amiEditDesktopArgs.callbackPortlet;\r\n"+
            "	var portletSettings=amiEditDesktopArgs.portletSettings;\r\n"+
            "	var help=amiEditDesktopArgs.help;\r\n"+
            "	var windowLinks=amiEditDesktopArgs.windowLinks;\r\n"+
            "	var desktopId=amiEditDesktopArgs.desktopId;\r\n"+
            "	var desktopPortletIds=amiEditDesktopArgs.desktopPortletIds;\r\n"+
            "    amiLinkCounts={};\r\n"+
            "	amiLinkableDivs=[];\r\n"+
            "	//Remove linking canvas if a link hasn't been started\r\n"+
            "	if(amiUserLinkingSource == null)\r\n"+
            "		removeAmiUserLinkCanvas();\r\n"+
            "	removeAmiLinkCanvas();\r\n"+
            "    var desktop=portletManager.getPortlet(desktopId).desktop;\r\n"+
            "    for(var i in portletSettings)\r\n"+
            "      removeAmiEditButton(i);\r\n"+
            "    for(var i in desktopPortletIds){\r\n"+
            "    	var w=desktop.getWindow(desktopPortletIds[i].id);\r\n"+
            "    	if(w!=null){\r\n"+
            "    	  if(w.amiDiv){\r\n"+
            "	        w.getHeaderDiv().removeChild(w.amiDiv);\r\n"+
            "	        w.amiDiv=null;\r\n"+
            "    	  }\r\n"+
            "    	  if(w.amiDiv2){\r\n"+
            "	        w.getHeaderDiv().removeChild(w.amiDiv2);\r\n"+
            "	        w.amiDiv2=null;\r\n"+
            "    	  }\r\n"+
            "    	  if(w.shader){\r\n"+
            "  	        w.getHeaderDiv().removeChild(w.shader);\r\n"+
            "  	        w.shader=null;\r\n"+
            "      	  }\r\n"+
            "	}\r\n"+
            "    }\r\n"+
            "	if(edit){\r\n"+
            "			//addAmiEditButton(callbackPortlet,portlet.portletId,help);\r\n"+
            "		for(var i in portletSettings)\r\n"+
            "			addAmiEditButton(callbackPortlet,i,portletSettings[i],help);\r\n"+
            "        amiLinkCanvas=nw('canvas','ami_link_canvas');\r\n"+
            "        document.body.appendChild(amiLinkCanvas);\r\n"+
            "        var context = amiLinkCanvas.getContext('2d');\r\n"+
            "        var rect=new Rect();\r\n"+
            "        amiLinkCanvas.width=getDocumentWidth(window);\r\n"+
            "        amiLinkCanvas.height=getDocumentHeight(window);\r\n"+
            "        context.clearRect(0,0,amiLinkCanvas.width,amiLinkCanvas.height);\r\n"+
            "        context.strokeStyle=AMI_LNCLR;\r\n"+
            "        context.lineWidth=4;\r\n"+
            "        for(var j in windowLinks){\r\n"+
            "          var wl=windowLinks[j];\r\n"+
            "          var links=wl.links;\r\n"+
            "          context.clearRect(wl.x-5,wl.y+8,wl.w+9,wl.h+25);\r\n"+
            "          for(var i in links){\r\n"+
            "        	var link=links[i];\r\n"+
            "        	var frPoint,toPoint;\r\n"+
            "        	if(link.frPortlet!=null){\r\n"+
            "        	  frPoint=rect.readFromElement(portletManager.getPortletNoThrow(link.frPortlet).divElement).getMidpoint();\r\n"+
            "        	} else if(link.frTab){\r\n"+
            "        	  var tab=portletManager.getPortletNoThrow(link.frTab);\r\n"+
            "        	  frPoint=rect.readFromElement(tab.tabs[link.frTabIndex] ).getMidpoint();\r\n"+
            "        	}\r\n"+
            "        	\r\n"+
            "        	if(link.toPortlet!=null){\r\n"+
            "        	  toPoint=rect.readFromElement(portletManager.getPortletNoThrow(link.toPortlet).divElement).getMidpoint();\r\n"+
            "        	} else if(link.toTab){\r\n"+
            "        	  var tab=portletManager.getPortletNoThrow(link.toTab);\r\n"+
            "        	  toPoint=rect.readFromElement(tab.tabs[link.toTabIndex] ).getMidpoint();\r\n"+
            "        	}\r\n"+
            "        	var cntId=frPoint.x+\":\"+frPoint.y+\":\"+toPoint.x+\":\"+toPoint.y;\r\n"+
            "");
          out.print(
            "        	var cnt=amiLinkCounts[cntId];\r\n"+
            "        	if(cnt==null)\r\n"+
            "        		cnt=1;\r\n"+
            "        	else\r\n"+
            "        		cnt++;\r\n"+
            "        	amiLinkCounts[cntId]=cnt;\r\n"+
            "            drawArc(context,rd(frPoint.x),rd(frPoint.y),rd(toPoint.x),rd(toPoint.y), 10+20*cnt,1,10);\r\n"+
            "            context.stroke();\r\n"+
            "          }\r\n"+
            "        }\r\n"+
            "        for(var i in desktopPortletIds){\r\n"+
            "        	var t=desktopPortletIds[i];\r\n"+
            "        	  addWindowEditButton(callbackPortlet,desktop,t);\r\n"+
            "        }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "function addWindowEditButton(callbackPortlet,desktop,t){\r\n"+
            "  var id=t.id;\r\n"+
            "  var type=t.type;\r\n"+
            "  var w=desktop.getWindow(id);\r\n"+
            "  if(w==null)\r\n"+
            "	return;\r\n"+
            "  var div=nw('div','ami_desktop_edit_button_window');\r\n"+
            "  if(type=='M')\r\n"+
            "    div.style.marginBottom='-18px';  \r\n"+
            "  w.getHeaderDiv().appendChild(div);\r\n"+
            "  w.amiDiv=div;\r\n"+
            "  div.onmouseup=function(){onAmiEditWindowButtonClicked(callbackPortlet,id)};\r\n"+
            "  if(!t.d){\r\n"+
            "    var div=nw('div','ami_desktop_edit_button_default_location');\r\n"+
            "    div.style.marginLeft='28px';  \r\n"+
            "    w.getHeaderDiv().appendChild(div);\r\n"+
            "    w.amiDiv2=div;\r\n"+
            "    div.onmouseup=function(){onAmiSetDefaultWindowButtonClicked(callbackPortlet,id)};\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "function removeAmiEditButton(i){\r\n"+
            "	var portlet=portletManager.getPortletNoThrow(i);\r\n"+
            "	if(!portlet)\r\n"+
            "		return;\r\n"+
            "	if(portlet.amiDiv){\r\n"+
            "		portlet.divElement.removeChild(portlet.amiDiv);\r\n"+
            "		portlet.amiDiv=null;\r\n"+
            "	    portlet.onDividerLocation=null;\r\n"+
            "	}\r\n"+
            "	if(portlet.amiDiv2){\r\n"+
            "		portlet.divElement.removeChild(portlet.amiDiv2);\r\n"+
            "		portlet.amiDiv2=null;\r\n"+
            "	}\r\n"+
            "	if(portlet.shader){\r\n"+
            "		portlet.divElement.removeChild(portlet.shader);\r\n"+
            "		portlet.shader=null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "function callbackUpdateDashboard(callbackPortlet){\r\n"+
            "  callbackPortlet.callBack('update_dashboard',{});\r\n"+
            "}\r\n"+
            "function isTransientOrReadOnly(settings) {\r\n"+
            "	return settings.isTransient || settings.isReadOnly;\r\n"+
            "}\r\n"+
            "function getResolvedDividerIconPath(isVertical, settings) {\r\n"+
            "	var iconName = \"\";\r\n"+
            "	if (isVertical) {\r\n"+
            "		if(settings.locked==\"ratio\")\r\n"+
            "	      iconName = isTransientOrReadOnly(settings) ? \"transient_config_button_v_lock_ratio.svg\" : \"config_button_v_lock_ratio.png\";\r\n"+
            "	    else if(settings.locked==\"top\")\r\n"+
            "	      iconName = isTransientOrReadOnly(settings) ? \"transient_config_button_v_lock_left.svg\" :\"config_button_v_lock_left.png\";\r\n"+
            "	    else if(settings.locked==\"bottom\")\r\n"+
            "	      iconName = isTransientOrReadOnly(settings) ? \"transient_config_button_v_lock_right.svg\": \"config_button_v_lock_right.png\";\r\n"+
            "	    else\r\n"+
            "	      iconName = isTransientOrReadOnly(settings) ? \"transient_config_button_v.svg\" : \"config_button_v.png\";\r\n"+
            "	} else {\r\n"+
            "		if(settings.locked==\"ratio\")\r\n"+
            "	      iconName = isTransientOrReadOnly(settings) ?  \"transient_config_button_h_lock_ratio.svg\" : \"config_button_h_lock_ratio.png\";\r\n"+
            "	    else if(settings.locked==\"top\")\r\n"+
            "	      iconName = isTransientOrReadOnly(settings) ? \"transient_config_button_h_lock_top.svg\" : \"config_button_h_lock_top.png\";\r\n"+
            "	    else if(settings.locked==\"bottom\")\r\n"+
            "	      iconName = isTransientOrReadOnly(settings) ? \"transient_config_button_h_lock_bottom.svg\" : \"config_button_h_lock_bottom.png\";\r\n"+
            "	    else\r\n"+
            "	      iconName = isTransientOrReadOnly(settings) ? \"transient_config_button_h.svg\" : \"config_button_h.png\";\r\n"+
            "	}\r\n"+
            "	var output = \"url('../portal/rsc/ami/\" + iconName +  \"')\";\r\n"+
            "	return output;\r\n"+
            "}\r\n"+
            "function addAmiEditButton(callbackPortlet,portletId,settings,help){\r\n"+
            "	var portlet=portletManager.getPortletNoThrow(portletId);\r\n"+
            "	var div,div2;\r\n"+
            "	if(!portlet)\r\n"+
            "		return;\r\n"+
            "	if(portlet.amiDiv)//TODO: should be needed\r\n"+
            "		return;\r\n"+
            "	if(settings.type=='divider'){ \r\n"+
            "	  var dividerPortlet=portletManager.getPortletNoThrow(settings.innerId);\r\n"+
            "	    labelDiv = nw('div', 'ami_desktop_divider_label');\r\n"+
            "		labelDiv.innerHTML = settings.label;\r\n"+
            "		labelDiv.style.textShadow = \"-2px -1px white,-2px 0px white,-2px 1px white, -1px -2px white,-1px -1px white,-1px 0px white,-1px 1px white,-1px 2px white, 0px -2px white, 0px -1px white, 0px 1px white, 0px 2px white, 1px -2px white, 1px -1px white, 1px 0px white, 1px 1px white, 1px 2px white, 2px -1px white, 2px 0px white, 2px 1px white\";\r\n"+
            "	  if(dividerPortlet.vertical){\r\n"+
            "		var divIconCss = isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_v ami_desktop_edit_button_v_transient' : 'ami_desktop_edit_button_v';\r\n"+
            "	    div=nw('div', divIconCss );\r\n"+
            "		labelDiv.style.marginTop=\"-14px\";\r\n"+
            "		div.appendChild(labelDiv);\r\n"+
            "	    div.style.backgroundImage=getResolvedDividerIconPath(dividerPortlet.vertical, settings);\r\n"+
            "	    div.style.left=toPx(dividerPortlet.offset+dividerPortlet.thickness/2 + getAbsoluteLeft(dividerPortlet.divElement)-getAbsoluteLeft(portlet.divElement));\r\n"+
            "	    if(!settings.isDefault){\r\n"+
            "	      div2=nw('div', 'ami_desktop_edit_button_default_location' );\r\n"+
            "	      div2.style.left=toPx(dividerPortlet.offset+dividerPortlet.thickness/2 + getAbsoluteLeft(dividerPortlet.divElement)-getAbsoluteLeft(portlet.divElement));\r\n"+
            "	      div2.style.margin='-40px -10px';\r\n"+
            "	    }\r\n"+
            "	    dividerPortlet.onDividerLocation=function(offset){\r\n"+
            "	      div.style.left=toPx(dividerPortlet.offset+dividerPortlet.thickness/2 + getAbsoluteLeft(dividerPortlet.divElement)-getAbsoluteLeft(portlet.divElement));\r\n"+
            "	      if(div2!=null)\r\n"+
            "	        div2.style.left=toPx(dividerPortlet.offset+dividerPortlet.thickness/2 + getAbsoluteLeft(dividerPortlet.divElement)-getAbsoluteLeft(portlet.divElement));\r\n"+
            "	      removeAmiLinkCanvas();\r\n"+
            "	    }\r\n"+
            "	  }else{\r\n"+
            "		var divIconCss = isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_h ami_desktop_edit_button_h_transient' : 'ami_desktop_edit_button_h';\r\n"+
            "	    div=nw('div', divIconCss );\r\n"+
            "		labelDiv.style.marginTop='20px';\r\n"+
            "		labelDiv.style.marginLeft='6px';\r\n"+
            "		div.appendChild(labelDiv);\r\n"+
            "	    div.style.backgroundImage=getResolvedDividerIconPath(dividerPortlet.vertical, settings);\r\n"+
            "  	    div.style.top=toPx(dividerPortlet.offset+dividerPortlet.thickness/2+ getAbsoluteTop(dividerPortlet.divElement)-getAbsoluteTop(portlet.divElement));\r\n"+
            "	    if(!settings.isDefault){\r\n"+
            "	      div2=nw('div', 'ami_desktop_edit_button_default_location' );\r\n"+
            "  	      div2.style.top=toPx(dividerPortlet.offset+dividerPortlet.thickness/2+ getAbsoluteTop(dividerPortlet.divElement)-getAbsoluteTop(portlet.divElement));\r\n"+
            "	      div2.style.margin='-11px 20px';\r\n"+
            "	    }\r\n"+
            "	    dividerPortlet.onDividerLocation=function(offset){\r\n"+
            "  	      div.style.top=toPx(dividerPortlet.offset+dividerPortlet.thickness/2+ getAbsoluteTop(dividerPortlet.divElement)-getAbsoluteTop(portlet.divElement));\r\n"+
            "	      if(div2!=null)\r\n"+
            "  	        div2.style.top=toPx(dividerPortlet.offset+dividerPortlet.thickness/2+ getAbsoluteTop(dividerPortlet.divElement)-getAbsoluteTop(portlet.divElement));\r\n"+
            "	      removeAmiLinkCanvas();\r\n"+
            "	    }\r\n"+
            "	  }\r\n"+
            "	  makeDraggable(div,dividerPortlet.dividerDragger,!dividerPortlet.vertical,dividerPortlet.vertical);\r\n"+
            "      div.ondragging=function(e,x,y){if(Math.abs(x)>4|| Math.abs(y)>4|| div.onmouseup==null){\r\n"+
            "    	  div.onmouseup=null;\r\n"+
            "    	  dividerPortlet.dividerDragger.ondragging(e,x,y);}};\r\n"+
            "      div.ondraggingEnd=function(e,x,y) {\r\n"+
            "    	  div.onmouseup=function(){\r\n"+
            "		  onAmiEditButtonClicked(callbackPortlet,id,settings)\r\n"+
            "		  };\r\n"+
            "		  dividerPortlet.dividerDragger.ondraggingEnd(e,x,y);\r\n"+
            "      }\r\n"+
            "	}else if(portlet instanceof DesktopPortlet){\r\n"+
            "	  portlet.desktop.onWindowLocation=function(left,top,width,height,isMax,zindex,active,fromBackend){\r\n"+
            "	      removeAmiLinkCanvas();\r\n"+
            "	      if(fromBackend)\r\n"+
            "	        callbackUpdateDashboard(callbackPortlet);\r\n"+
            "	  }\r\n"+
            "	}else if(portlet instanceof AmiAxisPortlet){\r\n"+
            "	  div=nw('div', isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_axis ami_desktop_edit_button_transient' : 'ami_desktop_edit_button_axis');\r\n"+
            "	}else if(portlet instanceof AmiPlotPortlet){\r\n"+
            "	  div=nw('div', isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_plot ami_desktop_edit_button_transient' : 'ami_desktop_edit_button_plot');\r\n"+
            "	}else if(settings.type=='tab'){\r\n"+
            "//	  var tabPortlet = portletManager.getPortletNoThrow(settings.innerId); might be useful later\r\n"+
            "	  div=nw('div', isTransientOrReadOnly(settings) ? 'ami_desktop_edit_button_tab ami_desktop_edit_button_transient' : 'ami_desktop_edit_button_tab');\r\n"+
            "	  labelDiv = nw('div', 'ami_desktop_tabpanel_label');\r\n"+
            "	  labelDiv.innerHTML = settings.label;\r\n"+
            "	  labelDiv.style.textShadow = \"-2px -1px white,-2px 0px white,-2px 1px white, -1px -2px white,-1px -1px white,-1px 0px white,-1px 1px white,-1px 2px white, 0px -2px white, 0px -1px white, 0px 1px white, 0px 2px white, 1px -2px white, 1px -1px white, 1px 0px white, 1px 1px white, 1px 2px white, 2px -1px white, 2px 0px white, 2px 1px white\";\r\n"+
            "	  div.appendChild(labelDiv);\r\n"+
            "	  if(settings.d){\r\n"+
            "	    div2=nw('div', 'ami_desktop_edit_button_tab_default_location' );\r\n"+
            "	    if(settings.right){\r\n"+
            "	       div2.style.right='20px';\r\n"+
            "	    }else{\r\n"+
            "	       div2.style.left='20px';\r\n"+
            "	    }\r\n"+
            "	    if(settings.bottom){\r\n"+
            "	       div2.style.bottom='0px';\r\n"+
            "	    }else{\r\n"+
            "	       div2.style.top='0px';\r\n"+
            "	    }\r\n"+
            "	  }\r\n"+
            "\r\n"+
            "	  \r\n"+
            "	  if(settings.right) { \r\n"+
            "		  div.style.right='1px';\r\n"+
            "		  labelDiv.style.right=toPx(36);\r\n"+
            "	  }else {\r\n"+
            "		  div.style.left='1px';\r\n"+
            "		  labelDiv.style.left=toPx(36);\r\n"+
            "	  }\r\n"+
            "	  if(settings.bottom){ \r\n"+
            "		  div.style.bottom='1px'; \r\n"+
            "	  } else { \r\n"+
            "		  div.style.top='1px';\r\n"+
            "	  }\r\n"+
            "	}else{\r\n"+
            "		var buttonCssClass = 'ami_desktop_edit_button';\r\n"+
            "		buttonCssClass = settings.designMode == true? 'ami_desktop_edit_button ami_desktop_design_button': buttonCssClass;\r\n"+
            "		buttonCssClass = isTransientOrReadOnly(settings) == true ? buttonCssClass + \" ami_desktop_edit_button_transient\" : buttonCssClass;\r\n"+
            "	      div=nw('div',buttonCssClass);\r\n"+
            "		if(help){\r\n"+
            "	      div.innerHTML=\"<div class='ami_help_bubble2' >\"+help+\"</div>\";\r\n"+
            "		}else{\r\n"+
            "	      div.innerHTML=settings.label;\r\n"+
            "		}\r\n"+
            "		amiLinkableDivs[amiLinkableDivs.length]=div;\r\n"+
            "	}\r\n"+
            "	if(div!=null){\r\n"+
            "	  div.portletId=portletId;\r\n"+
            "	  portlet.divElement.appendChild(div);\r\n"+
            "	  var id=portletId;\r\n"+
            "	  div.onmouseup=function(){\r\n"+
            "		  onAmiEditButtonClicked(callbackPortlet,id,settings)\r\n"+
            "		  };\r\n"+
            "	  portlet.amiDiv=div;\r\n"+
            "	}\r\n"+
            "	if(div2!=null){\r\n"+
            "	  div2.portletId=portletId;\r\n"+
            "	  portlet.divElement.appendChild(div2);\r\n"+
            "	  var id=portletId;");
          out.print(
            "\r\n"+
            "	  div2.onmouseup=function(){onAmiSetDefaultButtonClicked(callbackPortlet,id,settings)};\r\n"+
            "	  portlet.amiDiv2=div2;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "function onAmiSetDefaultWindowButtonClicked(callbackPortlet,id){\r\n"+
            "	  callbackPortlet.callBack('set_default_window',{id:id});\r\n"+
            "}\r\n"+
            "\r\n"+
            "function onAmiEditWindowButtonClicked(callbackPortlet,id){\r\n"+
            "	  callbackPortlet.callBack('edit_window',{id:id});\r\n"+
            "}\r\n"+
            "function onAmiSetDefaultButtonClicked(callbackPortlet,id,settings){\r\n"+
            " callbackPortlet.callBack('set_default',{id:id});\r\n"+
            "}\r\n"+
            "function onAmiEditButtonClicked(callbackPortlet,id,settings){\r\n"+
            "	var p=portletManager.getPortletNoThrow(id);\r\n"+
            "	if(p){\r\n"+
            "	  callbackPortlet.callBack('edit_portlet',{id:id});\r\n"+
            "	  if(amiUserLinkingCanvas==null){\r\n"+
            "	    if(settings.type=='divider'){\r\n"+
            "	    	var j=1;\r\n"+
            "          	p=portletManager.getPortletNoThrow(settings.innerId);\r\n"+
            "	    	for(var i in p.childPortletIds){\r\n"+
            "	          var cp=portletManager.getPortletNoThrow(i);\r\n"+
            "	          if(cp.shader==null){\r\n"+
            "	            var div=nw('div','ami_desktop_edit_select');\r\n"+
            "	            var div2=nw('div','ami_desktop_edit_select2');\r\n"+
            "	            div.innerHTML=''+j;\r\n"+
            "	            j++;\r\n"+
            "	            div.appendChild(div2);\r\n"+
            "	            cp.shader=div;\r\n"+
            "	            cp.divElement.appendChild(div);\r\n"+
            "	          }\r\n"+
            "	    	}\r\n"+
            "	    }else{\r\n"+
            "	      if(p.shader==null){\r\n"+
            "	        var div=nw('div','ami_desktop_edit_select');\r\n"+
            "	        var div2=nw('div','ami_desktop_edit_select2');\r\n"+
            "	        div.appendChild(div2);\r\n"+
            "	        p.shader=div;\r\n"+
            "	        p.divElement.appendChild(div);\r\n"+
            "	      }\r\n"+
            "	    }\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function amiSetCustomCss(css){\r\n"+
            "	var windows=portletManager.windows;\r\n"+
            "	for(var i in windows){\r\n"+
            "	   amiSetCustomCssForWindow(windows[i],css);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "function amiSetCustomCssForWindow(window,css){\r\n"+
            "//	log(portletManager);\r\n"+
            "	var styleTag = window.document.getElementById (\"ami_custom_styles\");\r\n"+
            "	var sheet = styleTag.sheet ;\r\n"+
            "	while(sheet.cssRules.length>0)\r\n"+
            "	    sheet.deleteRule (sheet.cssRules.length-1);\r\n"+
            "	for(var v in css){\r\n"+
            "		try{\r\n"+
            "	      sheet.insertRule(css[v]);\r\n"+
            "		}catch(error){\r\n"+
            "			log(\"For css: \"+css[v]);\r\n"+
            "			log(error);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "");

	}
	
}