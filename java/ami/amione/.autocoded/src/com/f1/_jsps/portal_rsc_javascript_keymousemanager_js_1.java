package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_keymousemanager_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_keymousemanager_js_1() {
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
            "//#########################\r\n"+
            "//##### KeyMouseManager #####\r\n"+
            "\r\n"+
            "var kmm;\r\n"+
            "\r\n"+
            "function KeyMouseManager(window){\r\n"+
            "	this.owningWindow = window;\r\n"+
            "	var document = window.document;\r\n"+
            "	document.addEventListener(\"click\", this.clickManager, true);\r\n"+
            "	document.addEventListener('dblclick', this.dblclickManager, true);\r\n"+
            "	document.addEventListener('mousedown', this.mousedownManager, true);\r\n"+
            "	document.addEventListener('mouseup', this.mouseupManager, true);\r\n"+
            "	document.addEventListener('contextmenu', this.contextmenuManager, true);\r\n"+
            "	document.addEventListener('mousemove', this.mousemoveManager, true);\r\n"+
            "	\r\n"+
            "	document.addEventListener('keydown', this.keydownManager, true);\r\n"+
            "	document.addEventListener('keyup', this.keyupManager, true);\r\n"+
            "	document.addEventListener('keypress', this.keypressManager, true);\r\n"+
            "	document.addEventListener('wheel', this.wheelManager, true);\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "KeyMouseManager.prototype.clickedTarget;\r\n"+
            "KeyMouseManager.prototype.activePortletId;\r\n"+
            "\r\n"+
            "\r\n"+
            "KeyMouseManager.prototype.activate=function(e){\r\n"+
            "	this.clickedTarget = e.target;\r\n"+
            "	\r\n"+
            "	var div = this.clickedTarget;\r\n"+
            "	while(div != null && !div.portletId){\r\n"+
            "		div = div.parentElement;\r\n"+
            "	}\r\n"+
            "	var  activePortletId = div!= null? div.portletId: null;\r\n"+
            "	if(activePortletId!=null)\r\n"+
            "	  this.setActivePortletId(activePortletId,true);\r\n"+
            "};\r\n"+
            "KeyMouseManager.prototype.setActivePortletId=function(portletId,fire){\r\n"+
            "	if(this.activePortletId == portletId)\r\n"+
            "		return;\r\n"+
            "	this.activePortletId = portletId;\r\n"+
            "	if(this.activePortletId!=null && fire!=false)\r\n"+
            "  	  portletManager.onUserActivePortlet(this.activePortletId);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "// For manager methods below this refers to the document.\r\n"+
            "KeyMouseManager.prototype.clickManager=function(e){\r\n"+
            "	portletManager.onUserSpecialClick(e,0,kmm.activePortletId);\r\n"+
            "	if(kmm.activePortletId != null){\r\n"+
            "		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);\r\n"+
            "		if(portlet && portlet.handleClick)\r\n"+
            "			portlet.handleClick(e);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "KeyMouseManager.prototype.dblclickManager=function(e){\r\n"+
            "	portletManager.onUserSpecialClick(e,2,kmm.activePortletId);\r\n"+
            "	if(kmm.activePortletId != null){\r\n"+
            "		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);\r\n"+
            "		if(portlet && portlet.handleDblclick)\r\n"+
            "			portlet.handleDblclick(e);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "KeyMouseManager.prototype.mousedownManager=function(e){\r\n"+
            "	kmm.activate(e);\r\n"+
            "	portletManager.onUserSpecialClick(e,3,kmm.activePortletId);\r\n"+
            "	if(kmm.activePortletId != null){\r\n"+
            "		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);\r\n"+
            "		if(portlet && portlet.handleMousedown)\r\n"+
            "			portlet.handleMousedown(e);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "KeyMouseManager.prototype.mouseupManager=function(e){\r\n"+
            "	portletManager.onUserSpecialClick(e,4,kmm.activePortletId);\r\n"+
            "	if(kmm.activePortletId != null){\r\n"+
            "		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);\r\n"+
            "		if(portlet && portlet.handleMouseup)\r\n"+
            "			portlet.handleMouseup(e);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "KeyMouseManager.prototype.mousemoveManager=function(e){\r\n"+
            "	if(currentContextMenu){\r\n"+
            "		currentContextMenu.handleMousemove(e);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "KeyMouseManager.prototype.wheelManager=function(e){\r\n"+
            "	kmm.activate(e);\r\n"+
            "	if(currentContextMenu){\r\n"+
            "		currentContextMenu.handleWheel(e);\r\n"+
            "	}\r\n"+
            "	else if(kmm.activePortletId != null){\r\n"+
            "		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);\r\n"+
            "		if(portlet && portlet.handleWheel)\r\n"+
            "			portlet.handleWheel(e);\r\n"+
            "	}\r\n"+
            "	if(typeof disableWheel == 'undefined' || disableWheel == false){\r\n"+
            "		wheel(e,true);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "KeyMouseManager.prototype.contextmenuManager=function(e){\r\n"+
            "	portletManager.onUserSpecialClick(e,1,kmm.activePortletId);\r\n"+
            "	if(kmm.activePortletId != null){\r\n"+
            "		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);\r\n"+
            "		if(portlet && portlet.handleContextmenu)\r\n"+
            "			portlet.handleContextmenu(e);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "KeyMouseManager.prototype.keydownManager=function(e){\r\n"+
            "	kmm.stopPropagationToBrowser(e);\r\n"+
            "	var tgtType=document.activeElement.tagName;\r\n"+
            "	if(currentGlass){\r\n"+
            "		currentGlass.handleKeydown(e);\r\n"+
            "	}\r\n"+
            "	if(currentContextMenu){\r\n"+
            "		if(currentContextMenu.bypass != true)\r\n"+
            "			currentContextMenu.handleKeydown(e);\r\n"+
            "		else if(currentContextMenu.customHandleKeydown != null){\r\n"+
            "			currentContextMenu.customHandleKeydown(e);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else if(currentColorPicker){\r\n"+
            "		currentColorPicker.handleKeydown(e);\r\n"+
            "	}\r\n"+
            "	else if(currentHelpBox){\r\n"+
            "		currentHelpBox.handleKeydown(e);\r\n"+
            "	}\r\n"+
            "	else { \r\n"+
            "	 	  portletManager.onUserSpecialKey(e);\r\n"+
            "	}\r\n"+
            "	if(kmm.clickedTarget && kmm.activePortletId){\r\n"+
            "		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);\r\n"+
            "		if(portlet && portlet.handleKeydown)\r\n"+
            "			portlet.handleKeydown(e, kmm.clickedTarget);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "KeyMouseManager.prototype.keyupManager=function(e){\r\n"+
            "	kmm.stopPropagationToBrowser(e);\r\n"+
            "	if(currentContextMenu){\r\n"+
            "		if(currentContextMenu.bypass != true)\r\n"+
            "			e.stopPropagation();\r\n"+
            "	}\r\n"+
            "	else if(kmm.clickedTarget && kmm.activePortletId){\r\n"+
            "		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);\r\n"+
            "		if(portlet && portlet.handleKeyup)\r\n"+
            "			portlet.handleKeyup(e, kmm.clickedTarget);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "KeyMouseManager.prototype.keypressManager=function(e){\r\n"+
            "	kmm.stopPropagationToBrowser(e);\r\n"+
            "	if(currentContextMenu){\r\n"+
            "		currentContextMenu.handleKeypress(e);\r\n"+
            "	}\r\n"+
            "	else if(kmm.clickedTarget && kmm.activePortletId){\r\n"+
            "		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);\r\n"+
            "		if(portlet && portlet.handleKeypress)\r\n"+
            "			portlet.handleKeypress(e, kmm.clickedTarget);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "KeyMouseManager.prototype.stopPropagationToBrowser=function(e){\r\n"+
            "	if(e.ctrlKey){\r\n"+
            "	  if(e.key==\"d\"){\r\n"+
            "	    e.stopPropagation();\r\n"+
            "	    e.preventDefault();\r\n"+
            "	  }\r\n"+
            "	}else if(e.altKey){\r\n"+
            "	  if(e.key==\"f\" || e.key==\"d\"){\r\n"+
            "	    e.stopPropagation();\r\n"+
            "	    e.preventDefault();\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "");

	}
	
}