package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_portal_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_portal_js_1() {
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
            "var SEARCH_BUTTON_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"search\"> <g> <circle style=\"fill:none;stroke:';\r\n"+
            "var SEARCH_BUTTON_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;\" x1=\"58.6\" y1=\"58.9\" x2=\"87.7\" y2=\"88\"/> </g> </g> '+SVG_SUFFIX;\r\n"+
            "var DOWNLOAD_BUTTON_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"save\"> <g> <line style=\"fill:none;stroke:';\r\n"+
            "var DOWNLOAD_BUTTON_IMAGE_SUFFIX=';stroke-width:12;stroke-miterlimit:10;\" points=\"77.3,44.3 50,71.6 22.7,44.3\"/> </g> </g> </g> '+SVG_SUFFIX;\r\n"+
            "var EXPAND_BUTTON_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"plus_1_\"> <g> <line style=\"fill:none;stroke:';\r\n"+
            "var EXPAND_BUTTON_IMAGE_SUFFIX=';stroke-width:18;stroke-miterlimit:10;\" x1=\"50\" y1=\"12\" x2=\"50\" y2=\"88\"/> </g> </g> '+SVG_SUFFIX;\r\n"+
            "var CONTRACT_BUTTON_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"minus\"> <line style=\"fill:none;stroke:';\r\n"+
            "var CONTRACT_BUTTON_IMAGE_SUFFIX=';stroke-width:18;stroke-miterlimit:10;\" x1=\"12\" y1=\"50\" x2=\"88\" y2=\"50\"/> </g> '+SVG_SUFFIX;\r\n"+
            "var PORTAL_DIALOG_HEADER_TITLE;\r\n"+
            "\r\n"+
            "function g(id){\r\n"+
            "	return portletManager.getPortlet(id);\r\n"+
            "}\r\n"+
            "function rmp(id){\r\n"+
            "	return portletManager.removePortlet(id);\r\n"+
            "}\r\n"+
            "function gnt(id){\r\n"+
            "	return portletManager.getPortletNoThrow(id);\r\n"+
            "}\r\n"+
            "function getMainWindow(){\r\n"+
            "	return this.portletManager==null ? window : this.portletManager.mainWindow;\r\n"+
            "}\r\n"+
            "\r\n"+
            "var portletManager;\r\n"+
            " window.windowId=getWindowParam(window,'");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.WINDOWID);
          out.print(
            "','0');\r\n"+
            "  \r\n"+
            "  \r\n"+
            "if(window.portletManager!=null){\r\n"+
            "	portletManager=window.portletManager;\r\n"+
            "	var EXPECTED_DOCUMENT=portletManager.mainWindow.document;\r\n"+
            "	//window.eventsForPopup=[];\r\n"+
            "	//window.setInterval(function(){pumpEventsForPopup();},5000);\r\n"+
            "	window.setInterval(function(){if(!(portletManager.mainWindow.document===EXPECTED_DOCUMENT)) window.close();},500);\r\n"+
            "}else{\r\n"+
            "	if(window.windowId!=0)\r\n"+
            "		window.close();\r\n"+
            "}\r\n"+
            "\r\n"+
            "//var popupEvents={};\r\n"+
            "//function pumpEventsForPopup(){\r\n"+
            "//	if(!(portletManager.mainWindow.document===EXPECTED_DOCUMENT))\r\n"+
            "//		window.close();\r\n"+
            "//	err(portletManager.mainWindow.popupEvents);\r\n"+
            "//	var queue=portletManager.mainWindow.popupEvents[windowId];\r\n"+
            "//	if(queue==null || queue.length==0)\r\n"+
            "//		return;\r\n"+
            "//	for(var n in queue){\r\n"+
            "//		var codeAndTarget=queue[n];\r\n"+
            "//		var i=codeAndTarget[1];\r\n"+
            "//		eval(codeAndTarget[0]);\r\n"+
            "//	}\r\n"+
            "//	portletManager.mainWindow.popupEvents[windowId]=null;\r\n"+
            "//}\r\n"+
            "//\r\n"+
            "//\r\n"+
            "//function addPopupEvent(windowId,event,target){\r\n"+
            "//	var queue=popupEvents[windowId];\r\n"+
            "//	if(queue==null)\r\n"+
            "//	  queue=popupEvents[windowId]=[];\r\n"+
            "//	queue[queue.length]=[event,target];\r\n"+
            "//}\r\n"+
            "\r\n"+
            "\r\n"+
            "function postInit(windowId){\r\n"+
            "	if(portletManager==null){\r\n"+
            "		window.close();\r\n"+
            "		redirectToLogin();\r\n"+
            "	}else\r\n"+
            "	  portletManager.postInit(windowId);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "var nextSendSeqnum=0;\r\n"+
            "function PortletManager(callbackUrl,pageUid,pollingMs,ajaxSafeMode,ajaxLoadingTimeoutMs,ajaxLoadingCheckPeriodMs,portalDialogHeaderTitle,buildVersion){\r\n"+
            "  var that=this;\r\n"+
            "  this.ajaxSafeMode=ajaxSafeMode;\r\n"+
            "  if(this.ajaxSafeMode)\r\n"+
            "	  log(\"ajax.safe.mode=\"+this.ajaxSafeMode);\r\n"+
            "  this.pgid=__PGID;\r\n"+
            "  this.callbackUrl=callbackUrl;\r\n"+
            "  this.pageUid=pageUid;\r\n"+
            "  this.pollingMs=pollingMs;\r\n"+
            "  this.setBlankPageLoadingAnimationVisible(false);\r\n"+
            "  this.portlets={};\r\n"+
            "  this.pendingPortalAjax=null;\r\n"+
            "  this.portalAjaxErrorsCount=0;\r\n"+
            "  this.lastPortalAjaxWasError=false;\r\n"+
            "  this.waitingPortalAjaxResponse=false;\r\n"+
            "  this.PORTLET_AJAX_REQUEST=new XMLHttpRequest(); // code for IE7+, Firefox, Chrome, Opera, Safari\r\n"+
            "  this.PORTLET_AJAX_REQUEST.responseType=\"arraybuffer\";\r\n"+
            "  this.PORTLET_AJAX_REQUEST.onerror=function(o){that.onPortalAjaxError(o);};\r\n"+
            "  this.PORTLET_AJAX_REQUEST.onreadystatechange=function(o){that.portletAjaxCallback(o);};\r\n"+
            "  this.ajaxMaxSeqnum = null;\r\n"+
            "  this.expectedSeqNumProcessed=0;\r\n"+
            "  this.windows={};\r\n"+
            "  this.windows[0]=window;\r\n"+
            "  this.mainWindow=window;\r\n"+
            "  this.buildVersion = buildVersion;\r\n"+
            "  window.onbeforeunload=function(e){that.onBeforeClose(e);that.onUnload();};\r\n"+
            "  this._AJAX_POLLING_TIME_MS = Date.now();\r\n"+
            "  this._AJAX_POLLING_LATENCY_MS = 0;\r\n"+
            "  this._AJAX_POLLING_SAMPLE_SIZE = 3;\r\n"+
            "  this._AJAX_POLLING_COUNT = 0;\r\n"+
            "  this._AJAX_LOADING_MINTIME_COND_MS = ajaxLoadingTimeoutMs;\r\n"+
            "  this._AJAX_LOADING_CHECK_FREQ_MS = ajaxLoadingCheckPeriodMs;\r\n"+
            "  this.ajaxLoadingEnabled = false;\r\n"+
            "  this.ajaxLoadingSeqNum = null;\r\n"+
            "  this.lastAjaxRequestTime=null;\r\n"+
            "  this.isWaitingAjax = false;\r\n"+
            "  this.isShowLoadingAjax = false;\r\n"+
            "  this.loadingDialogs = {};\r\n"+
            "  this.loadingBarStyles= new Object();\r\n"+
            "  that.checkAjaxWaitingInterval=window.setInterval(function(){that.checkAjaxWaiting();},that._AJAX_LOADING_CHECK_FREQ_MS);\r\n"+
            "  PORTAL_DIALOG_HEADER_TITLE=portalDialogHeaderTitle;\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.onUnload = function(){\r\n"+
            "	this.callBack('unload',{});\r\n"+
            "}\r\n"+
            "PortletManager.prototype.updateAjaxPollingLatency = function(){\r\n"+
            "	if(this._AJAX_POLLING_COUNT >= this._AJAX_POLLING_SAMPLE_SIZE){\r\n"+
            "		this._AJAX_POLLING_TIME_MS += this._AJAX_POLLING_LATENCY_MS;\r\n"+
            "		this._AJAX_POLLING_LATENCY_MS = (Date.now() - this._AJAX_POLLING_TIME_MS)/this._AJAX_POLLING_COUNT;\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this._AJAX_POLLING_COUNT++;\r\n"+
            "		this._AJAX_POLLING_LATENCY_MS = (Date.now() - this._AJAX_POLLING_TIME_MS)/this._AJAX_POLLING_COUNT;\r\n"+
            "	}\r\n"+
            " 	//err(\"polling latency\", this._AJAX_POLLING_LATENCY_MS);\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.focusField=function(portletId,attachmentId){\r\n"+
            "	g(portletId).focusField(attachmentId);\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.showLoadingDialog=function(text, _window){\r\n"+
            "	if(_window == null)\r\n"+
            "		_window = window;\r\n"+
            "		\r\n"+
            "    var content=nw('div','dialog');\r\n"+
            "    content.innerHTML=text;\r\n"+
            "	var dialog= new Dialog(content, _window);\r\n"+
            "	var dialogBgColor = portletManager.loadingBarStyles.dialogBgColor;\r\n"+
            "	var headerBgColor = portletManager.loadingBarStyles.headerBgColor;\r\n"+
            "	var stripeColor1 = portletManager.loadingBarStyles.stripeColor1; \r\n"+
            "	var stripeColor2 = portletManager.loadingBarStyles.stripeColor2;\r\n"+
            "	var stripeColor3 = portletManager.loadingBarStyles.stripeColor3;\r\n"+
            "	if (dialogBgColor) {\r\n"+
            "		dialog.setDialogBgColor(dialogBgColor);\r\n"+
            "	}\r\n"+
            "	if (headerBgColor) {\r\n"+
            "		dialog.setHeaderBgColor(headerBgColor);\r\n"+
            "	}\r\n"+
            "	buildSVGLoadAnimation(stripeColor1,stripeColor2,stripeColor3);\r\n"+
            "  	dialog.setHeaderTitle(PORTAL_DIALOG_HEADER_TITLE);\r\n"+
            "	dialog.setImageHtml(SVG_LOADING_ANIMATION);\r\n"+
            "    dialog.setGlassOpacity(0);\r\n"+
            "	dialog.setCanResize(false);\r\n"+
            "	dialog.setSize(320,86);\r\n"+
            "	dialog.setSmallerCloseButton();\r\n"+
            "	dialog.setImageSize(45, 45);\r\n"+
            "	dialog.setType(\"loading\");\r\n"+
            "	dialog.show();\r\n"+
            "	return dialog;\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.storeLoadingDialogStyle=function(styles) {\r\n"+
            "	if (styles.dialogBgColor) {\r\n"+
            "		this.loadingBarStyles.dialogBgColor=styles.dialogBgColor;		\r\n"+
            "	}\r\n"+
            "	if (styles.headerBgColor) {\r\n"+
            "		this.loadingBarStyles.headerBgColor=styles.headerBgColor;\r\n"+
            "	}\r\n"+
            "	if (styles.stripeColor1) {\r\n"+
            "		this.loadingBarStyles.stripeColor1=styles.stripeColor1;		\r\n"+
            "	}\r\n"+
            "	if (styles.stripeColor2) {\r\n"+
            "		this.loadingBarStyles.stripeColor2=styles.stripeColor2;\r\n"+
            "	}\r\n"+
            "	if (styles.stripeColor3) {\r\n"+
            "		this.loadingBarStyles.stripeColor3=styles.stripeColor3;		\r\n"+
            "	}\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.checkEnableAjaxLoading=function(params){\r\n"+
            "	//if(params.type != \"polling\")\r\n"+
            "	//	err(params.type, params.portletId, params);\r\n"+
            "	if(this.ajaxLoadingEnabled == true)\r\n"+
            "		return;\r\n"+
            "	if(params == null)\r\n"+
            "		return;\r\n"+
            "	if(params.seqnum == null)\r\n"+
            "		return;\r\n"+
            "	if(params.portletId == null){\r\n"+
            "		if(params.type == \"polling\")\r\n"+
            "			return;\r\n"+
            "		if(params.type != \"userClick\" || params.type != \"userKey\" || params.type != \"userScroll\" || params.type != \"userActivatePortlet\" || params.type != \"popupClosed\" || params.type == \"notificationClicked\" || params.type == \"notificationClosed\")\r\n"+
            "			return;\r\n"+
            "	}\r\n"+
            "	this.ajaxLoadingEnabled = true;\r\n"+
            "	this.ajaxLoadingSeqNum = params.seqnum;\r\n"+
            "	this.lastAjaxRequestTime = null;\r\n"+
            "	\r\n"+
            "}\r\n"+
            "PortletManager.prototype.disableAjaxLoading=function(){\r\n"+
            "	this.ajaxLoadingEnabled = false;\r\n"+
            "	this.ajaxLoadingSeqNum = null;\r\n"+
            "}\r\n"+
            "PortletManager.prototype.updateLastAjaxTime=function(){\r\n"+
            "	if(this.ajaxLoadingEnabled == false)\r\n"+
            "		return;\r\n"+
            "	if(this.lastAjaxRequestTime != null)\r\n"+
            "		return;\r\n"+
            "	this.lastAjaxRequestTime = Date.now();\r\n"+
            "}\r\n"+
            "	\r\n"+
            "PortletManager.prototype.checkHideLoadingDialog = function(ajaxReq){\r\n"+
            "	if(this.ajaxLoadingEnabled == false)\r\n"+
            "		return;\r\n"+
            "	if(this.ajaxLoadingSeqNum > ajaxReq.maxSeqnum)\r\n"+
            "		return;\r\n"+
            "	if(this.isShowLoadingAjax == true){\r\n"+
            "		this.isShowLoadingAjax = false;\r\n"+
            "		closeDialogWindowsGeneric(this.loadingDialogs);\r\n"+
            "	}\r\n"+
            "	// Disable the loading now that it responds\r\n"+
            "	this.disableAjaxLoading();\r\n"+
            "}\r\n"+
            "PortletManager.prototype.checkAjaxWaiting=function(){\r\n"+
            "	if(this.ajaxLoadingEnabled == false || this.lastAjaxRequestTime == null)\r\n"+
            "		return;\r\n"+
            "	if(this.waitingPortalAjaxResponse == false)\r\n"+
            "		return;\r\n"+
            "	if(this.isShowLoadingAjax == true)\r\n"+
            "		return;\r\n"+
            " \r\n"+
            "	var currentTime = Date.now();\r\n"+
            "\r\n"+
            "	var diff = (currentTime - this.lastAjaxRequestTime);\r\n"+
            "	if(diff > (this._AJAX_LOADING_MINTIME_COND_MS + this._AJAX_POLLING_LATENCY_MS)){\r\n"+
            "		this.isShowLoadingAjax = true;\r\n"+
            "		alertDialogWindowsGeneric(this.showLoadingDialog, \"\", this.windows, this.loadingDialogs);	\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.callRest=function(id,isPost,target,data,timeoutMs){\r\n"+
            "    var that=this;\r\n"+
            "    var restRequest=new XMLHttpRequest(); \r\n"+
            "    restRequest.timeout=timeoutMs;\r\n"+
            "    restRequest.withCredentials=true;\r\n"+
            "    restRequest.onreadystatechange=function(o){that.onRestAjaxCallback(o);};\r\n"+
            "    restRequest.open(isPost ? \"POST\" : \"GET\" ,target,true);\r\n"+
            "    restRequest.setRequestHeader(\"Content-type\",\"text/html\"); \r\n"+
            "    restRequest.send(data);\r\n"+
            "    restRequest.id=id;\r\n"+
            "}\r\n"+
            "PortletManager.prototype.onRestAjaxCallback=function(event){\r\n"+
            "	var origReq=event.srcElement || event.target;\r\n"+
            "	if(origReq.readyState!=4)\r\n"+
            "		return;\r\n"+
            "	var id=origReq.id;\r\n"+
            "	var text = origReq.response;\r\n"+
            "	var status=origReq.status;\r\n"+
            "	this.portletAjax({id: id,status:status,type:'restResponse',response: text});\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.onBeforeClose=function(id){\r\n"+
            "	for(var i in this.windows)\r\n"+
            "		if (i !=0) // we are leaving the main window anyway, so no need to close the first window.\r\n"+
            "			this.windows[i].close();\r\n"+
            "	for(var i in this.pendingNotifications) {\r\n"+
            "		var pn=this.pendingNotifications[i];\r\n"+
            "		pn.onclose=null;\r\n"+
            "		pn.close();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.getWindow=function(id){\r\n"+
            "	return this.windows[id];\r\n"+
            "}\r\n"+
            "PortletManager.prototype.closeWindows=function(){\r\n"+
            "	//for(var i in this.windows)\r\n"+
            "	  //this.windows[i].close();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "PortletManager.prototype.onUserSpecialKey=function(e){\r\n"+
            "	var key=e.key;\r\n"+
            "	if(key=='Alt' || key=='Control' || key=='Shift')\r\n"+
            "		return;\r\n"+
            "	this.callBack('userKey',{k:key,s:e.shiftKey,a:e.altKey,c:e.ctrlKey});\r\n"+
            "}\r\n"+
            "PortletManager.prototype.onUserSpecialScroll=function(left, top, id){\r\n"+
            "//	this.callBack('userScroll', {l:left, t:top, id:id.substring(8)});\r\n"+
            "	this.callBack('userScroll', {l:left, t:top, id:id});\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.onUserSpecialClick=function(e, mouseEventType, pid){\r\n"+
            "	this.callBack('userClick',{x:e.clientX, y:e.clientY, b:e.button, c:e.ctrlKey, a:e.altKey, s:e.shiftKey, t:mouseEventType, pid:pid});\r\n"+
            "}\r\n"+
            "PortletManager.prototype.onUserActivePortlet=function(portletId){\r\n"+
            "	this.callBack('userActivatePortlet',{pid:portletId,x:this.mainWindow.MOUSE_POSITION_X,y:this.mainWindow.MOUSE_POSITION_Y});\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.showNotification=function(nid,title,body,icon){\r\n"+
            "	if(Notification.permission !== \"granted\"){\r\n"+
            "	  var that=this;\r\n"+
            "		Notification.requestPermission().then(function(result){\r\n"+
            "		if(result==\"denied\")\r\n"+
            "	      portletManager.callBack('notificationDenied',{nid:nid});\r\n"+
            "		else if(result==\"granted\"){\r\n"+
            "	      t");
          out.print(
            "hat.showNotificationInner(nid,title,body,icon);\r\n"+
            "		}\r\n"+
            "	  });\r\n"+
            "    } \r\n"+
            "	this.showNotificationInner(nid,title,body,icon);\r\n"+
            "    //notification.onclick = functikkon () { onNotificationClicked(notificationId); };\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.pendingNotifications={};\r\n"+
            "PortletManager.prototype.pendingNotificationsNextId=0;\r\n"+
            "\r\n"+
            "PortletManager.prototype.showNotificationInner=function(nid,title,body,icon){\r\n"+
            "    var notification = new Notification(title,{  body: body, icon: icon,requireInteraction:true});\r\n"+
            "    var that=this;\r\n"+
            "    notification.pmnid=that.pendingNotificationsNextId++;\r\n"+
            "    that.pendingNotifications[notification.pmnid]=notification;\r\n"+
            "    notification.onclick = function () {\r\n"+
            "        delete that.pendingNotifications[notification.pmnid];\r\n"+
            "    	notification.onclose=null;\r\n"+
            "    	notification.close();\r\n"+
            "    	getMainWindow().focus();\r\n"+
            "	    portletManager.callBack('notificationClicked',{nid:nid});\r\n"+
            "    };\r\n"+
            "    notification.onclose = function () {\r\n"+
            "//    	session.alert('notification');\r\n"+
            "        delete that.pendingNotifications[notification.pmnid];\r\n"+
            "	    portletManager.callBack('notificationClosed',{nid:nid});\r\n"+
            "    };\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.showPopupWindow=function(windowId,left,top,width,height,title){\r\n"+
            "	var w=window.open(\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL);
          out.print(
            "?");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.WINDOWID);
          out.print(
            "=\"+windowId+\"&");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
          out.print(
            "=\"+this.pgid,\"_blank\",'width='+width+',height='+height+',left='+left+',top='+top+',resizable=yes');\r\n"+
            "	\r\n"+
            "	if(w==null){\r\n"+
            "	  this.callBack('popupFailed',{popupWindowId:windowId});\r\n"+
            "      return;\r\n"+
            "	}\r\n"+
            "	w.portletManager=portletManager;\r\n"+
            "	w.titleOverride=title;\r\n"+
            "	w.windowId=windowId;\r\n"+
            "	this.windows[windowId]=w;\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.getPortlet=function(id){\r\n"+
            "	var r=this.portlets[id];\r\n"+
            "	if(r==null)\r\n"+
            "		throw new Error(\"id not found: \"+id);\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "PortletManager.prototype.getPortletNoThrow=function(id){\r\n"+
            "	var r=this.portlets[id];\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.putPortlet=function(portlet){\r\n"+
            "	var id=portlet.portletId;\r\n"+
            "	if(this.portlets[id])\r\n"+
            "		throw new Error(\"id already exists: \"+id);\r\n"+
            "	this.portlets[id]=portlet;\r\n"+
            "	return portlet;\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.removePortlet=function(id){\r\n"+
            "	var p=this.portlets[id];\r\n"+
            "	if(p==null)\r\n"+
            "		throw new Error(\"portlet id not found for remove: \"+id);\r\n"+
            "	p.close();\r\n"+
            "	delete this.portlets[id];\r\n"+
            "}\r\n"+
            "PortletManager.prototype.removePortletRecursive=function(id){\r\n"+
            "	var p=this.portlets[id];\r\n"+
            "	if(p==null)\r\n"+
            "		throw new Error(\"portlet id not found for remove: \"+id);\r\n"+
            "	delete this.portlets[id];\r\n"+
            "	for(var i in p.childPortletIds){\r\n"+
            "		this.removePortletRecursive(i);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.setPolling=function(pollingMs){\r\n"+
            "  this.pollingMs=pollingMs;\r\n"+
            "}\r\n"+
            "PortletManager.prototype.postInit=function(webWindowId){\r\n"+
            "  this.callBack('postInit',{webWindowId:webWindowId,");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
          out.print(
            ":this.pgid});\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.onSeqnum=function(seqnum){\r\n"+
            "  if(seqnum!=this.expectedSeqNumProcessed){\r\n"+
            "	log(\"bad sequence number: \"+seqnum + \"!=\"+ this.expectedSeqNumProcessed);\r\n"+
            "    this.expectedSeqNumProcessed=seqnum;\r\n"+
            "  }\r\n"+
            "  this.expectedSeqNumProcessed++;\r\n"+
            "  \r\n"+
            "  //check for closed windows\r\n"+
            "  for(var i in this.windows){\r\n"+
            "	var w=this.windows[i];\r\n"+
            "	if(!w.location || !w.location.pathname || w.closed){\r\n"+
            "		this.callBack(\"popupClosed\", {childWindowId:i});\r\n"+
            "		delete this.windows[i];\r\n"+
            "	}\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "PortletManager.prototype.setBlankPageLoadingAnimationVisible=function(b){\r\n"+
            "	var blankPageLoadingAnimationContainer=getElement(\"blankpage-loading-animation-container\");\r\n"+
            "	if (blankPageLoadingAnimationContainer == null)\r\n"+
            "		return;\r\n"+
            "	if(b)\r\n"+
            "		document.body.appendChild(blankPageLoadingAnimationContainer);\r\n"+
            "	else {\r\n"+
            "		document.body.removeChild(blankPageLoadingAnimationContainer);\r\n"+
            "		if (blankPageAnimationInterval)\r\n"+
            "			clearInterval(blankPageAnimationInterval);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "PortletManager.prototype.downloadFile=function(){\r\n"+
            "	var a = nw('A');\r\n"+
            "    a.href=a.download=this.callbackUrl+\"?downloadFile=true&");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
          out.print(
            "=\"+this.pgid;\r\n"+
            "    document.body.appendChild(a);\r\n"+
            "    a.click();\r\n"+
            "    document.body.removeChild(a);\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletManager.prototype.showSaveConfigDialog=function(txt){\r\n"+
            "	var text=nw('textarea');\r\n"+
            "	text.innerHTML=txt;\r\n"+
            "	text.style.position='absolute';\r\n"+
            "	text.style.overflow='auto';\r\n"+
            "	text.style.left='10px';\r\n"+
            "	text.style.top='10px';\r\n"+
            "	text.style.bottom='10px';\r\n"+
            "	text.style.right='10px';\r\n"+
            "	text.style.fontSize='12px';\r\n"+
            "	var d=new Dialog(text);\r\n"+
            "	d.setTitle('");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"Save configuration");
          out.print(
            "');\r\n"+
            "	d.setCanResize(true);\r\n"+
            "	d.setSize(800,600);\r\n"+
            "	d.onResize=function(loc){\r\n"+
            "		text.style.width=toPx(loc.width-30);\r\n"+
            "		text.style.height=toPx(loc.height-80);\r\n"+
            "	};\r\n"+
            "	d.show();\r\n"+
            "	text.select();\r\n"+
            "	text.focus();\r\n"+
            "};\r\n"+
            "\r\n"+
            "PortletManager.prototype.showLoadConfigDialog=function(){\r\n"+
            "	var that=this;\r\n"+
            "	var text=nw('textarea');\r\n"+
            "	text.innerHTML='';\r\n"+
            "	text.style.position='absolute';\r\n"+
            "	text.style.overflow='auto';\r\n"+
            "	text.style.left='10px';\r\n"+
            "	text.style.top='10px';\r\n"+
            "	text.style.bottom='10px';\r\n"+
            "	text.style.right='10px';\r\n"+
            "	text.style.fontSize='12px';\r\n"+
            "	var d=new Dialog(text);\r\n"+
            "	d.setCanResize(true);\r\n"+
            "	d.setTitle('");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"Load configuration");
          out.print(
            "');\r\n"+
            "	d.setSize(800,600);\r\n"+
            "	d.onResize=function(loc){\r\n"+
            "		text.style.width=toPx(loc.width-30);\r\n"+
            "		text.style.height=toPx(loc.height-80);\r\n"+
            "	};\r\n"+
            "	d.addButton('");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"load config");
          out.print(
            "','ok');\r\n"+
            "	d.addButton('");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"cancel");
          out.print(
            "');\r\n"+
            "	d.show();\r\n"+
            "	d.onClose=function(e,d,reason){if(reason=='ok')that.callBack('loadConfig',{text:text.value});};\r\n"+
            "	text.focus();\r\n"+
            "};\r\n"+
            "\r\n"+
            "PortletManager.prototype.onJsProcessed=function(pollingDelay){\r\n"+
            "//  this.showWait();\r\n"+
            "  if(this.hasQueuedPolling) return;\r\n"+
            "  this.hasQueuedPolling=true;\r\n"+
            "  this.updateAjaxPollingLatency();\r\n"+
            "  window.setTimeout(\"portletManager.pollAjax();\", pollingDelay!=null ?  pollingDelay : this.pollingMs );\r\n"+
            "};\r\n"+
            "\r\n"+
            "PortletManager.prototype.pollAjax=function(){\r\n"+
            "  this.hasQueuedPolling=false;\r\n"+
            "  this.callBack('polling',{});\r\n"+
            "};\r\n"+
            "\r\n"+
            "PortletManager.prototype.callBack=function(type,params){\r\n"+
            "  params.type=type;\r\n"+
            "//  params.pageUid=this.pageUid;\r\n"+
            "//  params.seqnum=nextSendSeqnum++;\r\n"+
            "  this.portletAjax(params);\r\n"+
            "};\r\n"+
            "\r\n"+
            "//PortletManager.prototype.showWait=function(text){\r\n"+
            "//	if(text!=null){\r\n"+
            "//      if(this.waitDiv==null){\r\n"+
            "//        this.waitDiv=nw(\"div\",\"wait\");\r\n"+
            "//        this.waitPaneDiv=nw(\"div\",\"wait_pane\");\r\n"+
            "//        document.body.appendChild(this.waitPaneDiv);\r\n"+
            "//        document.body.appendChild(this.waitDiv);\r\n"+
            "//      }\r\n"+
            "//	  this.waitDiv.style.display=\"inline\";\r\n"+
            "//	  this.waitDiv.innerHTML=text;\r\n"+
            "//	  this.waitPaneDiv.style.display=\"inline\";\r\n"+
            "//	}else if(this.waitDiv!=null){\r\n"+
            "//	  this.waitDiv.style.display=\"none\";\r\n"+
            "//	  this.waitPaneDiv.style.display=\"none\";\r\n"+
            "//	}\r\n"+
            "//};\r\n"+
            "\r\n"+
            "PortletManager.prototype.playAudio=function(url){\r\n"+
            "	  var a=new Audio(url);\r\n"+
            "	  a.loop=false;\r\n"+
            "	  a.play();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "//###################\r\n"+
            "//##### Portlet #####\r\n"+
            "\r\n"+
            "\r\n"+
            "function Portlet(instance,portletId,parentPortletId){\r\n"+
            "  var that=this;\r\n"+
            "  this.childPortletIds=[];\r\n"+
            "  instance.childPortletIds=this.childPortletIds;\r\n"+
            "  if(!instance.setSize)\r\n"+
            "    instance.setSize=this.setSize;\r\n"+
            "  if(!instance.setUserSelectable)\r\n"+
            "    instance.setUserSelectable=this.setUserSelectable;\r\n"+
            "  if(!instance.callBack)\r\n"+
            "    instance.callBack=this.callBack;\r\n"+
            "  if(!instance.removeChild)\r\n"+
            "    instance.removeChild=this.removeChild;\r\n"+
            "  if(!instance.close)\r\n"+
            "    instance.close=this.close;\r\n"+
            "  if(!instance.populatePortletMenu)\r\n"+
            "    instance.populatePortletMenu=function(menu){that.populatePortletMenu(menu);};\r\n"+
            "  if(!instance.setSockets)\r\n"+
            "    instance.setSockets=function(sockets){that.setSockets(sockets);};\r\n"+
            "  if(!instance.getPortlet)\r\n"+
            "    instance.getPortlet=function(){return that.getPortlet();};\r\n"+
            "  if(!instance.setOwningWindowId)\r\n"+
            "    instance.setOwningWindowId=function(id){return that.setOwningWindowId(id);};\r\n"+
            "  instance.setHIDS=function(id){return that.setHIDS(id);};\r\n"+
            "  instance.setHCSC=function(id){return that.setHCSC(id);};\r\n"+
            "  instance.location=new Rect();\r\n"+
            "  instance.portletId=portletId;\r\n"+
            "  instance.parentPortletId=parentPortletId;\r\n"+
            "  instance.divElement=nw('div');\r\n"+
            "  instance.divElement.portlet=instance;\r\n"+
            "//  instance.divElement.style.position='absolute';\r\n"+
            "  //instance.divElement.id=\"portlet_\"+portletId;\r\n"+
            "  instance.divElement.portletId=portletId;\r\n"+
            "  instance.divElement.isPortletElement=true;\r\n"+
            "  instance.divElement.style.overflow='hidden';\r\n"+
            "  this.divElement=instance.divElement;\r\n"+
            "  this.location=instance.location;\r\n"+
            "  this.instance=instance;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//html id selector\r\n"+
            "Portlet.prototype.setHIDS=function(id){\r\n"+
            "   this.instance.divElement.id=id;\r\n"+
            "}\r\n"+
            "Portlet.prototype.setHCSC=function(c){\r\n"+
            "   if(this.instance.hcsc!=null)\r\n"+
            "     this.instance.divElement.classList.remove(this.instance.hcsc);\r\n"+
            "   this.instance.hcsc=c;\r\n"+
            "   if(this.instance.hcsc!=null)\r\n"+
            "     this.instance.divElement.classList.add(this.instance.hcsc);\r\n"+
            "}\r\n"+
            "Portlet.prototype.left = 0;\r\n"+
            "Portlet.prototype.top = 0;\r\n"+
            "\r\n"+
            "Portlet.prototype.getPortlet=function(){\r\n"+
            "	return this;\r\n"+
            "}\r\n"+
            "Portlet.prototype.setOwningWindowId=function(owningWindowId){\r\n"+
            "	if(owningWindowId == this.owningWindowId)\r\n"+
            "		return;\r\n"+
            "	this.owningWindowId=owningWindowId;\r\n"+
            "	if(owningWindowId == null)\r\n"+
            "		this.owningWindow = null;\r\n"+
            "	else\r\n"+
            "		this.owningWindow=portletManager.getWindow(owningWindowId);\r\n"+
            "	this.instance.owningWindow=this.owningWindow;\r\n"+
            "	if (this.instance.setOwningWindow != null)\r\n"+
            "		this.instance.setOwningWindow(this.owningWindow);\r\n"+
            "	this.instance.owningWindowId=this.owningWindowId;\r\n"+
            "	\r\n"+
            "	//Update children keys\r\n"+
            "	var k = Object.keys(this.childPortletIds);\r\n"+
            "	if(k.length > 0)\r\n"+
            "		for(var i = 0; i < k.length;i++){\r\n"+
            "			var p = gnt(k[i]);\r\n"+
            "			if(p!= null) p.setOwningWindowId(this.owningWindowId);\r\n"+
            "		}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Portlet.prototype.populatePortletMenu=function(menu){\r\n"+
            "  var that=this;\r\n"+
            "  menu.addItem('<img src=\"rsc/asc.gif\"> ");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"Delete this Component");
          out.print(
            "',null,function(e){that.onUserDelete(e);});\r\n"+
            "  menu.addItem('<img src=\"rsc/autoasc.gif\"> ");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"Wrap in a container");
          out.print(
            "',null,function(e){that.onUserWrap(e);});\r\n"+
            "  for(var key in this.sockets){\r\n"+
            "	var k=new Object(key);\r\n"+
            "    menu.addItem('<img src=\"rsc/connect.gif\">'+this.sockets[key],k,function(e,k){that.onUserConnect(e,k);});\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "Portlet.prototype.onUserConnect=function(e,key){\r\n"+
            "	onSelectForConnect(this.instance.portletId,key);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Portlet.prototype.onUserDelete=function(menu){\r\n"+
            "  this.instance.parentPortlet.callBack('deleteChild',{childPortletId:this.instance.portletId});\r\n"+
            "};\r\n"+
            "\r\n"+
            "Portlet.prototype.onUserWrap=function(menu){\r\n"+
            "  //this.instance.parentPortlet.callBack('wrapChild',{childPortletId:this.instance.portletId});\r\n"+
            "  //portletManager.showAddComponentsDialog('");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"Wrap Component");
          out.print(
            "',null,null,this.instance.parentPortlet,'wrapChild',{childPortletId:this.instance.portletId});\r\n"+
            "  this.instance.callBack('showWrapPortletDialog',{});\r\n"+
            "};\r\n"+
            "    \r\n"+
            "Portlet.prototype.close=function(type,params){\r\n"+
            "};\r\n"+
            "\r\n"+
            "//Portlet.prototype.setInnerHTML=function(html){\r\n"+
            "  //this.divElement.innerHTML=html;\r\n"+
            "//};\r\n"+
            "\r\n"+
            "Portlet.prototype.callBack=function(type,params){\r\n"+
            "  params.type=type;\r\n"+
            "  params.portletId=this.portletId;\r\n"+
            "//  params.pageUid=portletManager.pageUid;\r\n"+
            "//  params.seqnum=nextSendSeqnum++;\r\n"+
            "  portletManager.portletAjax(params);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Portlet.prototype.addChild=function(childId,width,height){\r\n"+
            "  var child=portletManager.getPortlet(childId);\r\n"+
            "  this.childPortletIds[childId]=true;\r\n"+
            "  child.parentPortlet=this.instance;\r\n"+
            "//	var k = Object.keys(this.childPortletIds);\r\n"+
            "//	if(k.length > 0)\r\n"+
            "//	err(k);\r\n"+
            "  child.setOwningWindowId(this.owningWindowId);\r\n"+
            "};\r\n"+
            "Portlet.prototype.removeChild=function(childId){\r\n"+
            "  var portlet=portletManager.getPortlet(childId);\r\n"+
            "  delete this.childPortletIds[childId];\r\n"+
            "  if(portlet==null)\r\n"+
            "	  alert('portlet not found for removal: '+childId);\r\n"+
            "  portlet.setOwningWindowId(null);\r\n"+
            "  //portlet.close();\r\n"+
            "  portlet.parentPortlet=null;\r\n"+
            "  var div=portlet.divElement;\r\n"+
            "  var parent=div.parentNode;\r\n"+
            "  if(parent!=null)\r\n"+
            "	  parent.removeChild(div);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Portlet.prototype.setSockets=function(sockets){\r\n"+
            "  this.sockets=sockets;\r\n"+
            "};\r\n"+
            "\r\n"+
            "//Portlet.prototype.setVisible=function(isVisible){\r\n"+
            "  //this.divElement.style.display=isVisible ? 'inline' : 'none';\r\n"+
            "//}\r\n"+
            "\r\n"+
            "Portlet.prototype.setSize=function(width,height){\r\n"+
            "  this.location.left=this.left;\r\n"+
            "  this.location.top=this.top;\r\n"+
            "  this.location.width=width;\r\n"+
            "  this.location.height=height;\r\n"+
            "  this.location.writeToElement(this.divElement);\r\n"+
            "};\r\n"+
            "Portlet.prototype.setUserSelectable=function(isSelectable){\r\n"+
            "	if(isSelectable)\r\n"+
            "      this.instance.divElement.portletSelectType='portletDiv';\r\n"+
            "};\r\n"+
            "\r\n"+
            "//###########################\r\n"+
            "//##### Root Portlet #####\r\n"+
            "\r\n"+
            "function RootPortlet(portletId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.divElement.style.left='0px';\r\n"+
            "  this.divElement.style.right='0px';\r\n"+
            "  this.divElement.style.top='0px';\r\n"+
            "  this.divElement.style.bottom='0px';\r\n"+
            "  this.divElement.style.background='green';\r\n"+
            "\r\n"+
            "  this.rootElement=nw('div');\r\n"+
            "  this.rootElement.style.background='white';\r\n"+
            "  this.rootElement.style.zIndex=0;\r\n"+
            "  this.rootElement.style.position=\"fixed\";\r\n"+
            "  this.rootElement.style.width=\"100%\";\r\n"+
            "  this.rootElement.style.height=\"100%\";\r\n"+
            "  this.divElement.appendChild(this.rootElement);\r\n"+
            "  this.zi=0;\r\n"+
            "\r\n"+
            "  this.isContainer=true;\r\n"+
            "  this.dialogs=[];\r\n"+
            "  this.root=null;\r\n"+
            "  var that=this;\r\n"+
            "  this.checkScreenLocationInterval=setInterval(function(){ that.checkScreenLocation(); }, 2000);\r\n"+
            "\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.checkScreenLocation = function(){\r\n"+
            "	if(this.owningWindow==null){\r\n"+
            "		this.callBack(\"popupClosed\", {childWindowId:this.id});\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	if(this.screenX!=this.owningWindow.screenX || this.screenY!=this.owningWindow.screenY)\r\n"+
            "		this.onWindowResize();\r\n"+
            "}\r\n"+
            "\r\n"+
            "RootPortlet.prototype.resizeTo = function(x,y,w,h){\r\n"+
            "	this.owningWindow.moveTo(x,y);\r\n"+
            "	var wDelta=this.owningWindow.outerWidth- this.owningWindow.innerWidth;\r\n"+
            "	var hDelta=this.owningWindow.outerHeight- this.owningWindow.innerHeight;\r\n"+
            "	this.owningWindow.resizeTo(w+wDelta,h+hDelta);\r\n"+
            "}\r\n"+
            "\r\n"+
            "RootPortlet.prototype.init = function(windowId){\r\n"+
            "    var that=this;\r\n"+
            "	this.windowId=windowId;\r\n"+
            "	this.owningWindow=portletManager.getWindow(windowId);\r\n"+
            "	if(this.owningWindow==null){\r\n"+
            "		this.callBack(\"popupClosed\", {childWindowId:windowId});\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "    this.owningWindow.onresize=function(){that.onWindowResize();};\r\n"+
            "    this.owningWindow.document.addEventListener(\"visibilitychange\", function(evt){that.onVisibilityChanged(evt);});\r\n"+
            "    this.onWindowResize();\r\n"+
            "    this.owningWindow.document.body.appendChild(this.divElement);\r\n"+
            "    this.owningWindow.kmm=new KeyMouseManager(this.owningWindow);\r\n"+
            "    \r\n"+
            "    this.owningWindow.addEventListener('focus', function(){that.onWindowFocusBlur(true)});\r\n"+
            "    this.owningWindow.addEventListener('blur', function(){that.onWindowFocusBlur(false)});\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.setBrowserTitle = function(title){\r\n"+
            "    this.owningWindow.window.document.title=title;\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.onWindowFocusBlur = function(focus){\r\n"+
            "	if(focus)\r\n"+
            "		this.gotFocus=true;\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.onVisibilityChanged = function(evt){\r\n"+
            "  this.callBack('visibility',{visible:this.owningWindow.document.visibilityState != 'hidden'});\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.onWindowResize = function(){\r\n"+
            "  this.screenX=this.owningWindow.screenX;\r\n"+
            "  this.screenY=this.owningWindow.screenY;\r\n"+
            "  if(this.owningWindow.document.visibilityState != 'hidden'){\r\n"+
            "	this.lastVisibleScreenX=this.screenX;\r\n"+
            "	this.lastVisibleScreenY=this.screenY;\r\n"+
            "  }\r\n"+
            "  var z = (1.0*window.outerWidth / window.innerWidth);\r\n"+
            "  this.callBack('location',{zoom:z,screenX:this.screenX,screenY:this.screenY, left:0,top:0,width:this.owningWindow.innerWidth,height:this.owningWindow.innerHeight,fullscreen:isFullScreen()});\r\n"+
            "};\r\n"+
            "RootPortlet.prototype.addChild=function(zIndex,childId,title,isModal){\r\n"+
            "  if(zIndex==0){\r\n"+
            "	this.root=portletManager.getPortlet(childId);\r\n"+
            "	this.rootElement.appendChild(this.root.divElement);\r\n"+
            "  }else{\r\n"+
            "	if(document.activeElement && document.activeElement.blur)\r\n"+
            "		document.activeElement.blur();\r\n"+
            "    var dw=new DesktopWindow(null,childId,portletManager.getPortlet(childId).divElement,title);\r\n"+
            "    dw.setStyleClassPrefix('portal_desktop');\r\n"+
            "    var that=this;\r\n"+
            "    this.dialogs[childId]=dw;\r\n"+
            "    dw.onUserButton=function(id,buttonId){if(\"close\"==buttonId) that.onClose(id)};\r\n"+
            "    dw.onUserClosedWindow=function(id){that.onClose(id);}\r\n"+
            "    dw.onUserMovedWindow=function(id,x,y,w,h){that.onLocationChanged(id,x,y,w,h);}\r\n"+
            "    dw.setButtonsVisible({close:true,min:false,max:false,pop:false});\r\n"+
            "    dw.setAllowEditTitle(false);\r\n"+
            "    if(isModal){\r\n"+
            "      dw.splashDiv=nw('div');\r\n"+
            "      dw.splashDiv.style.left='0px';\r\n"+
            "      dw.splashDiv.style.top='0px';\r\n"+
            "      dw.splashDiv.style.bottom='0px';\r\n"+
            "      dw.splashDiv.style.right='0px';\r\n"+
            "      dw.splashDiv.style.position=\"fixed\";\r\n"+
            "      dw.backgroundDiv=nw('div','dialog_pane');\r\n"+
            "      dw.backgroundDiv.style.zIndex=0;\r\n"+
            "      dw.splashDiv.appendChild(dw.backgroundDiv);\r\n"+
            "      dw.splashDiv.appendChild(dw.windowDiv);\r\n"+
            "      dw.splashDiv.style.zIndex=zIndex;\r\n"+
            "      dw.backgroundDiv.onclick=function(e){that.onOutsideClicked(e,childId)};\r\n"+
            "      dw.backgroundDiv.isGrey=true;\r\n"+
            "      this.divElement.appendChild(dw.splashDiv);\r\n"+
            "    }else{\r\n"+
            "//      dw.backgroundDiv=nw('div','dialog_pane');\r\n"+
            "//      dw.backgroundDiv.style.zIndex=0;\r\n"+
            "      dw.windowDiv.style.zIndex=zIndex;\r\n"+
            "//      dw.backgroundDiv.onclick=function(e){that.onOutsideClicked(e,childId)};\r\n"+
            "//      dw.backgroundDiv.isGrey=true;\r\n"+
            "//      this.divElement.appendChild(dw.backgroundDiv);\r\n"+
            "      this.divElement.appendChild(dw.windowDiv);\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  this.portlet.addChild(childId);\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.rebuildChildren=function(childId){\r\n"+
            "	removeAllChildren(this.divElement);\r\n"+
            "	for(var zIndex in this.childIds){\r\n"+
            "       var child=portletManager.getPortlet(this.childIds[zIndex]);\r\n"+
            "       if(zIndex==0){\r\n"+
            "	     var rootDiv=nw('div');\r\n"+
            "	     rootDiv.style.zIndex=0;\r\n"+
            "	     rootDiv.appendChild(child.divElement);\r\n"+
            "	     this.divElement.appendChild(rootDiv);\r\n"+
            "       }else{\r\n"+
            "	     var dialogDiv=nw('div');\r\n"+
            "	     dialogDiv.style.zIndex=zIndex;\r\n"+
            "	     dialogDiv.appendChild(child.divElement);\r\n"+
            "	     this.divElement.appendChild(dialogDiv);\r\n"+
            "       }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.onOutsideClicked=function(e,id){\r\n"+
            "    var target=getMouseTarget(e);\r\n"+
            "    if(target.isGrey){\r\n"+
            "	  target.style.backgroundColor='transparent';\r\n"+
            "	}else{\r\n"+
            "	  target.style.backgroundColor='black';\r\n"+
            "	}\r\n"+
            "	target.isGrey=!target.isGrey;\r\n"+
            "	\r\n"+
            "	this.callBack('dialogOutsideClicked',{childId:id});\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.onLocationChanged=function(id,x,y,w,h){\r\n"+
            "	this.callBack('dialogSized',{childId:id,left:x,top:y,width:w,height:h});\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.onClose=function(id){\r\n"+
            "  this.callBack('dialogClosed',{childId:id});\r\n"+
            "}\r\n"+
            "\r\n"+
            "RootPortlet.prototype.removeChild=function(childId){\r\n"+
            "  this.portlet.removeChild(childId);\r\n"+
            "  this.rebuildChildren();\r\n"+
            "}\r\n"+
            "\r\n"+
            "RootPortlet.prototype.setDialogLocation=function(childId,left,top,width,height,isMax,zindex,hasCloseButton,shade,stylePrefix,headerSize,borderSize,outsideShowBlockIcon,isModal,options){\r\n"+
            "  var dialog=this.dialogs[childId];\r\n"+
            "  if(stylePrefix!=null)\r\n"+
            "    dialog.setStyleClassPrefix(stylePrefix);\r\n"+
            "  dialog.setWindowLocation(left,top,width,height,false,zindex,true,true,headerSize,borderSize);\r\n"+
            "  dialog.setButtonsVisible({close:hasCloseButton,min:false,max:false,pop:false});\r\n"+
            "  dialog.setOptions(options);\r\n"+
            "  if(isModal){\r\n"+
            "    dialog.splashDiv.style.zIndex=zindex;\r\n"+
            "  } else{\r\n"+
            "    dialog.windowDiv.style.zIndex=zindex;\r\n"+
            "  }\r\n"+
            "  if(!shade && isModal){\r\n"+
            "	  dialog.backgroundDiv.style.backgroundColor='transparent';\r\n"+
            "      dialog.backgroundDiv.isGrey=false;\r\n"+
            "  }\r\n"+
            "  if(dialog.backgroundDiv!=null)\r\n"+
            "    dialog.backgroundDiv.style.cursor=outsideShowBlockIcon ? 'not-allowed' : 'auto';\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "RootPortlet.prototype.removeChild=function(childId){\r\n"+
            "  var p=portletManager.getPortlet(childId);\r\n"+
            "  if(this.root==p){\r\n"+
            "    this.rootElement.removeChild(p.divElement);\r\n"+
            "    this.root=null;\r\n"+
            "  }else{\r\n"+
            "    var dialog=this.dialogs[childId];\r\n"+
            "    delete this.dialogs[childId];\r\n"+
            "    if(dialog.splashDiv)\r\n"+
            "      this.divElement.removeChild(dialog.splashDiv);\r\n"+
            "    else\r\n"+
            "      this.divElement.removeChild(dialog.windowDiv);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.onUpdated=function(event){\r\n"+
            "};\r\n"+
            "\r\n"+
            "RootPortlet.prototype.showMenu=function(x,y,menu, options){\r\n"+
            "	var that=this;\r\n"+
            "    var contextMenuPoint=(x==-1 && y==-1) ? new Point(this.owningWindow.MOUSE_POSITION_X,this.owningWindow.MOUSE_POSITION_Y) : new Point(x,y);\r\n"+
            "    this.menu=this.createMenu(menu);\r\n"+
            "    if(options != null)\r\n"+
            "    	this.menu.setOptions(options);\r\n"+
            "    this.menu.onHide=function(){that.onUserDismissedMenu()};\r\n"+
            "    if(options != null)\r\n"+
            "    	this.menu.show(contextMenuPoint, options.keepYPosition);\r\n"+
            "    else\r\n"+
            "    	this.menu.show(contextMenuPoint);");
          out.print(
            "\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.closeMenu=function(){\r\n"+
            "	if(this.menu!= null){\r\n"+
            "	  this.menu.hideAll();\r\n"+
            "	  this.menu = null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "RootPortlet.prototype.createMenu=function(menu){\r\n"+
            "   var that=this;\r\n"+
            "   var r=new Menu(getWindow(this.divElement));\r\n"+
            "   r.createMenu(menu, function(e,id){that.onUserMenuItem(e,id);} );\r\n"+
            "   return r;\r\n"+
            "}\r\n"+
            "RootPortlet.prototype.onUserDismissedMenu=function(e,id){\r\n"+
            "	this.menu = null;\r\n"+
            "    this.callBack('menudismissed',{});\r\n"+
            "}\r\n"+
            "\r\n"+
            "RootPortlet.prototype.onUserMenuItem=function(e,id){\r\n"+
            "    this.callBack('menuitem',{id:id});\r\n"+
            "}\r\n"+
            "\r\n"+
            "function closeMe(pid){\r\n"+
            "	var p=gnt(pid);\r\n"+
            "	if(p!=null)\r\n"+
            "	  p.owningWindow.close();\r\n"+
            "}\r\n"+
            "\r\n"+
            "// this gets called if a panel is popped out\r\n"+
            "RootPortlet.prototype.focusMe=function(forceAfterMs){\r\n"+
            "	// first check for finsemble window\r\n"+
            "	if (this.owningWindow.finsembleWindow)\r\n"+
            "		this.owningWindow.finsembleWindow.focus();\r\n"+
            "	else \r\n"+
            "		this.owningWindow.focus();\r\n"+
            "	var that=this;\r\n"+
            "	if(forceAfterMs>0){\r\n"+
            "	  that.gotFocus=false;\r\n"+
            "	  setTimeout(function(){\r\n"+
            "		  if(!that.gotFocus && !that.owningWindow.document.hasFocus()){\r\n"+
            "			  var windowId=that.owningWindow.windowId;\r\n"+
            "  			\r\n"+
            "	          var t=that.owningWindow.open(\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL);
          out.print(
            "?");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.WINDOWID);
          out.print(
            "=\"+(windowId),\"_blank\",'width='+that.owningWindow.innerWidth+',height='+that.owningWindow.innerHeight+',left='+that.lastVisibleScreenX+',top='+that.lastVisibleScreenY+',resizable=yes');\r\n"+
            "	          if(t==null){\r\n"+
            "	            this.callBack('popupFailed',{popupWindowId:windowId});\r\n"+
            "                return;\r\n"+
            "	          }\r\n"+
            "	          t.portletManager=that.owningWindow.portletManager;\r\n"+
            "	          t.titleOverride=that.owningWindow.titleOverride;\r\n"+
            "	          t.windowId=windowId;\r\n"+
            "	          t.portletManager.windows[windowId]=t;\r\n"+
            "              clearInterval(that.checkScreenLocationInterval);\r\n"+
            "              portletManager.removePortletRecursive(that.portletId);\r\n"+
            "	          that.owningWindow.close();\r\n"+
            "		  }\r\n"+
            "	  }, forceAfterMs);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "//###########################\r\n"+
            "//##### Desktop Portlet #####\r\n"+
            "\r\n"+
            "function DesktopPortlet(portletId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.deskElement=nw('div');\r\n"+
            "  this.desktop=new Desktop(this.divElement);\r\n"+
            "  this.desktop.onUserClickedButton=function(e){that.callBack('showAddPortletDialog',{})};\r\n"+
            "  this.desktop.onUserDoubleclickedBackground=function(e){that.callBack('bgDoubleClicked',{})};\r\n"+
            "  \r\n"+
            "  this.desktop.onUserButton=function(id,buttonId){that.callBack('windowButton',{buttonId:buttonId,childId:id})};\r\n"+
            "  \r\n"+
            "//  this.desktop.onUserClosedWindow=function(id){that.callBack('windowClosed',{childId:id})};\r\n"+
            "  this.desktop.onUserFocusedWindow=function(id){that.callBack('windowFocus',{childId:id})};\r\n"+
            "//  this.desktop.onUserMaximizedWindow=function(id){that.callBack('windowMax',{childId:id})};\r\n"+
            "//  this.desktop.onUserMinimizedWindow=function(id){that.callBack('windowMin',{childId:id})};\r\n"+
            "  this.desktop.onUserMovedWindow=function(id,x,y,w,h){that.callBack('windowSized',{childId:id,left:x,top:y,width:w,height:h})};\r\n"+
            "  this.desktop.onUserRenamedWindow=function(id,text){that.callBack('renameWindow',{childId:id,text:text})};\r\n"+
            "}\r\n"+
            "DesktopPortlet.prototype.setDoclets=function(list){\r\n"+
            "	this.desktop.setDoclets(list);\r\n"+
            "}\r\n"+
            "DesktopPortlet.prototype.removeChild=function(id){\r\n"+
            "	this.desktop.removeChild(id);\r\n"+
            "    //portletManager.getPortlet(id).close();\r\n"+
            "}\r\n"+
            "DesktopPortlet.prototype.addChild=function(childId,title){\r\n"+
            "	this.desktop.addChild(childId,portletManager.getPortlet(childId).divElement,title);\r\n"+
            "    this.portlet.addChild(childId);\r\n"+
            "}\r\n"+
            "DesktopPortlet.prototype.setWindowLocation=function(childId,left,top,width,height,isMax,zindex,active,hasPopButton,hasMinButton,hasMaxButton,hasCloseButton,hasHeader,allowEditTitle,headerSize,borderSize,name,options){\r\n"+
            "    var innerWindow=this.desktop.getWindow(childId);\r\n"+
            "    if(innerWindow == null)\r\n"+
            "    	return;\r\n"+
            "    innerWindow.setTitle(name);\r\n"+
            "    innerWindow.setWindowLocation(left,top,width,height,isMax,zindex,active,true,headerSize,borderSize);\r\n"+
            "    innerWindow.setOptions(options);\r\n"+
            "    //innerWindow.setHasCloseMinMaxButton(hasCloseButton,hasMaxButton,hasMinButton);\r\n"+
            "    innerWindow.setButtonsVisible({hasHeader:hasHeader,close:hasCloseButton,min:hasMinButton,max:hasMaxButton,pop:hasPopButton});\r\n"+
            "    innerWindow.setHasHeader(hasHeader);\r\n"+
            "    innerWindow.setAllowEditTitle(allowEditTitle);\r\n"+
            "};\r\n"+
            "\r\n"+
            "DesktopPortlet.prototype.onUpdated=function(event){\r\n"+
            "};\r\n"+
            "\r\n"+
            "DesktopPortlet.prototype.setOptions=function(options){\r\n"+
            "    this.desktop.setOptions(options);\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "//###########################\r\n"+
            "//##### Grid Portlet ########\r\n"+
            "\r\n"+
            "function GridPortlet(portletId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  var that=this;\r\n"+
            "  this.windows=[];\r\n"+
            "};\r\n"+
            "\r\n"+
            "GridPortlet.prototype.init=function(debugDepth){\r\n"+
            "	this.debugDepth=debugDepth;\r\n"+
            "};\r\n"+
            "GridPortlet.prototype.setStyle=function(cssClass,cssStyle){\r\n"+
            "	if(cssClass){\r\n"+
            "	  this.divElement.style.background='';\r\n"+
            "	  this.divElement.className=cssClass;\r\n"+
            "	}\r\n"+
            "	applyStyle(this.divElement,cssStyle);\r\n"+
            "};\r\n"+
            "\r\n"+
            "var GRID_DEBUG_COLORS=[\"#FF0000\",\"#00FF00\",\"#0000FF\",\"#FFFF00\",\"#FF00FF\",\"#00FFFF\"];\r\n"+
            "GridPortlet.prototype.addChild=function(childId){\r\n"+
            "  var innerWindow=nw('div');\r\n"+
            "  this.windows[childId]=innerWindow;\r\n"+
            "  innerWindow.divElement=portletManager.getPortlet(childId).divElement;\r\n"+
            "  innerWindow.appendChild(innerWindow.divElement);\r\n"+
            "  this.divElement.appendChild(innerWindow);\r\n"+
            "  this.portlet.addChild(childId);\r\n"+
            "  if(this.debugDepth>-1){\r\n"+
            "    var debug=nw('div');\r\n"+
            "    debug.style.color=GRID_DEBUG_COLORS[this.debugDepth % GRID_DEBUG_COLORS.length];\r\n"+
            "    debug.style.opacity=.8;\r\n"+
            "    debug.style.border='2px solid '+debug.style.color;\r\n"+
            "    innerWindow.appendChild(debug);\r\n"+
            "    innerWindow.debug=debug;\r\n"+
            "    debug.style.pointerEvents='none';\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "GridPortlet.prototype.setGridLocation=function(childId,x,y,w,h,z,style,overlayStyleCss,overlayHtml,debug){\r\n"+
            "  var innerWindow=this.windows[childId];\r\n"+
            "  innerWindow.style.left=toPx(x);\r\n"+
            "  innerWindow.style.top=toPx(y);\r\n"+
            "  innerWindow.style.width=toPx(w);\r\n"+
            "  innerWindow.style.height=toPx(h);\r\n"+
            "  innerWindow.style.zIndex=z;\r\n"+
            "  applyStyle(innerWindow,style);\r\n"+
            "  if(overlayStyleCss!=null || overlayHtml!=null){\r\n"+
            "	if(innerWindow.overlayDiv==null)\r\n"+
            "	  innerWindow.appendChild(innerWindow.overlayDiv=overlayStyleDiv=nw('div','overlay_div'));\r\n"+
            "    applyStyle(innerWindow.overlayDiv,overlayStyleCss);\r\n"+
            "    innerWindow.overlayDiv.innerHTML=overlayHtml;\r\n"+
            "  }else{\r\n"+
            "	if(innerWindow.overlayDiv!=null){\r\n"+
            "		innerWindow.removeChild(innerWindow.overlayDiv);\r\n"+
            "	    delete innerWindow.overlayDiv;\r\n"+
            "	}\r\n"+
            "  }\r\n"+
            "  if(this.debugDepth>-1){\r\n"+
            "    innerWindow.debug.style.width=innerWindow.divElement.style.width;\r\n"+
            "    innerWindow.debug.style.height=innerWindow.divElement.style.height;\r\n"+
            "    innerWindow.debug.innerHTML=debug;\r\n"+
            "    innerWindow.debug.style.fontSize='8px';\r\n"+
            "//    innerWindow.debug.style.width=toPx(fromPx(innerWindow.divElement.style.width)-this.debugDepth*2);\r\n"+
            "//    innerWindow.debug.style.height=toPx(fromPx(innerWindow.divElement.style.height)-this.debugDepth*2);\r\n"+
            "//    innerWindow.debug.style.top=toPx(this.debugDepth);\r\n"+
            "//    innerWindow.debug.style.left=toPx(this.debugDepth);\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "GridPortlet.prototype.removeChild=function(childId){\r\n"+
            "  var innerWindow=this.windows[childId];\r\n"+
            "  delete this.windows[childId];\r\n"+
            "  this.divElement.removeChild(innerWindow);\r\n"+
            "  this.portlet.removeChild(childId);\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "//###############################\r\n"+
            "//##### Simple Text Portlet #####\r\n"+
            "\r\n"+
            "function SimpleTextPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.divElement.style.background='white';\r\n"+
            "}\r\n"+
            "\r\n"+
            "SimpleTextPortlet.prototype.setText = function(text){\r\n"+
            "  this.divElement.innerHTML=text;\r\n"+
            "};\r\n"+
            "\r\n"+
            "//###############################\r\n"+
            "//##### Scroll Portlet #####\r\n"+
            "\r\n"+
            "function ScrollPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.clipTop;\r\n"+
            "  this.clipLeft;\r\n"+
            "  this.innerWidth;\r\n"+
            "  this.innerHeight;\r\n"+
            "//  this.innerPortletLocation = new Rect();\r\n"+
            "  this.divElement.classList.add(\"scrollPortlet\");\r\n"+
            "  this.scrollContainer=nw('div');\r\n"+
            "  this.scrollContainer.style.left='0px';\r\n"+
            "  this.scrollContainer.style.top='0px';\r\n"+
            "  this.scrollContainer.style.bottom='0px';\r\n"+
            "  this.scrollContainer.style.right='0px';\r\n"+
            "  \r\n"+
            "  this.innerWindow=nw('div'); \r\n"+
            "  this.divElement.appendChild(this.scrollContainer);\r\n"+
            "  this.divElement.onmousedown=function(e){if(getMouseButton(e)==2)that.onUserContextMenu(e);};\r\n"+
            "  \r\n"+
            "  var that = this;\r\n"+
            "  this.scrollPane = new ScrollPane(this.scrollContainer, 15, this.innerWindow);\r\n"+
            "  this.scrollPane.DOM.paneElement.style.overflow=\"visible\";\r\n"+
            "  this.scrollPane.onScroll=function(){that.onScroll()};\r\n"+
            "  \r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPortlet.prototype.innerPortletLocation;\r\n"+
            "ScrollPortlet.prototype.innerPortletId = null;\r\n"+
            "ScrollPortlet.prototype.clipLeft = 0;\r\n"+
            "ScrollPortlet.prototype.clipTop = 0;\r\n"+
            "ScrollPortlet.prototype.alignLeft = 0;\r\n"+
            "ScrollPortlet.prototype.alignTop = 0;\r\n"+
            "ScrollPortlet.prototype.width = null;\r\n"+
            "ScrollPortlet.prototype.height = null;\r\n"+
            "ScrollPortlet.prototype.innerWidth = null;\r\n"+
            "ScrollPortlet.prototype.innerHeight = null;\r\n"+
            "ScrollPortlet.prototype.gripColor = null;\r\n"+
            "ScrollPortlet.prototype.trackColor = null;\r\n"+
            "ScrollPortlet.prototype.trackButtonColor = null;\r\n"+
            "ScrollPortlet.prototype.borderColor = null;\r\n"+
            "ScrollPortlet.prototype.iconsColor = null;\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollPortlet.prototype.onUserContextMenu=function(e){\r\n"+
            "	if(isMouseInside(e,this.innerWindow.divElement, -0)==false){\r\n"+
            "//		this.divElement.onmousedown=function(e){if(getMouseButton(e)==2)that.onUserContextMenu(e);};\r\n"+
            "		  this.callBack('onCustomMenu',{x:this.owningWindow.MOUSE_POSITION_X,y:this.owningWindow.MOUSE_POSITION_Y});\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPortlet.prototype.removeChild=function(childId){\r\n"+
            "	if(this.innerPortletId == childId){\r\n"+
            "		this.portlet.removeChild(childId);\r\n"+
            "		if(this.innerWindow.divElement != null && this.innerWindow.divElement.parentElement == this.innerWindow)\r\n"+
            "			this.innerWindow.removeChild(this.innerWindow.divElement);\r\n"+
            "		this.innerWindow.divElement = null;\r\n"+
            "		this.innerPortletId = null;\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "ScrollPortlet.prototype.addChild=function(childId){\r\n"+
            "  this.innerPortletId = childId;\r\n"+
            "  this.innerWindow.divElement=portletManager.getPortlet(childId).divElement;\r\n"+
            "  this.innerWindow.appendChild(this.innerWindow.divElement);\r\n"+
            "  this.portlet.addChild(childId);\r\n"+
            "};\r\n"+
            "ScrollPortlet.prototype.close=function(){\r\n"+
            "	if(this.innerPortletId != null)\r\n"+
            "		this.removeChild(this.innerPortletId)\r\n"+
            "}\r\n"+
            "ScrollPortlet.prototype.setInnerSize=function(width,height){\r\n"+
            "	this.innerWidth = width;\r\n"+
            "	this.innerHeight = height;\r\n"+
            "	this.innerWindow.style.width=toPx(Math.max(width,this.location.width));\r\n"+
            "  	this.innerWindow.style.height=toPx(Math.max(height,this.location.height));\r\n"+
            "	this.scrollPane.setPaneSize(width,height);\r\n"+
            "}\r\n"+
            "ScrollPortlet.prototype.setScroll=function(clipLeft,clipTop){\r\n"+
            "	if(this.clipLeft != clipLeft){\r\n"+
            "		this.clipLeft = clipLeft;\r\n"+
            "		this.scrollPane.setClipLeft(clipLeft);\r\n"+
            "	}\r\n"+
            "	if(this.clipTop != clipTop){\r\n"+
            "		this.clipTop = clipTop;\r\n"+
            "		this.scrollPane.setClipTop(clipTop);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPortlet.prototype.setAlign=function(left,top){\r\n"+
            "	this.alignLeft = left;\r\n"+
            "	this.alignTop = top;\r\n"+
            "	this.updateAlign();\r\n"+
            "}\r\n"+
            "ScrollPortlet.prototype.updateAlign = function(){\r\n"+
            "	if(this.innerWindow.divElement){\r\n"+
            "		let innerPortlet = portletMana");
          out.print(
            "ger.getPortlet(this.innerPortletId);\r\n"+
            "		innerPortlet.left = this.alignLeft;\r\n"+
            "		innerPortlet.top = this.alignTop;\r\n"+
            "		this.innerWindow.divElement.style.left = toPx(this.alignLeft);\r\n"+
            "		this.innerWindow.divElement.style.top = toPx(this.alignTop);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "ScrollPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.location.left=this.left;\r\n"+
            "  this.location.top=this.top;\r\n"+
            "  this.location.width=width;\r\n"+
            "  this.location.height=height;\r\n"+
            "  this.location.writeToElement(this.divElement);\r\n"+
            "  this.scrollPane.setLocation(this.location.left,this.location.top, this.location.width,this.location.height);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPortlet.prototype.onScroll=function(){\r\n"+
            "	this.scrollPane.DOM.innerpaneElement.style.top=toPx(-this.scrollPane.vscroll.getClipTop());\r\n"+
            "	this.scrollPane.DOM.innerpaneElement.style.left=toPx(-this.scrollPane.hscroll.getClipTop());\r\n"+
            "	if((this.clipTop == this.scrollPane.getClipTop()) && (this.clipLeft == this.scrollPane.getClipLeft()))\r\n"+
            "		return;\r\n"+
            "//	portletManager.onUserSpecialScroll(this.formDOMManager.scrollPane.getClipLeft(), this.formDOMManager.scrollPane.getClipTop(), this.portlet.divElement.id);\r\n"+
            "//	let xt = this.scrollPane.getClipTop() - this.clipTop;\r\n"+
            "//	let xl = this.scrollPane.getClipLeft() - this.clipLeft;\r\n"+
            "	this.clipTop = this.scrollPane.getClipTop();\r\n"+
            "	this.clipLeft = this.scrollPane.getClipLeft();\r\n"+
            "	\r\n"+
            "    this.callBack('onscroll',{\"t\":parseInt(this.clipTop), \"l\":parseInt(this.clipLeft)});\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPortlet.prototype.handleWheel=function(e){\r\n"+
            "	if(isMouseInside(e,this.innerWindow.divElement, -0)==false){\r\n"+
            "		this.scrollPane.handleWheel(e);\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		if(this.innerPortletId != null){\r\n"+
            "			var innerPortlet = portletManager.getPortlet(this.innerPortletId);\r\n"+
            "			innerPortlet.handleWheel(e);\r\n"+
            "\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollPortlet.prototype.setOptions=function(values){\r\n"+
            "	if(values.hasOwnProperty(\"bgcl\")){\r\n"+
            "		var bgColor = values[\"bgcl\"];\r\n"+
            "		this.scrollPane.DOM.paneElement.style.backgroundColor=bgColor;\r\n"+
            "	}\r\n"+
            "	if(values.hasOwnProperty(\"gripcl\")){\r\n"+
            "		this.gripColor = values[\"gripcl\"];\r\n"+
            "	}\r\n"+
            "	if(values.hasOwnProperty(\"trackcl\")){\r\n"+
            "		this.trackColor = values[\"trackcl\"];\r\n"+
            "	}\r\n"+
            "	if(values.hasOwnProperty(\"btncl\")){\r\n"+
            "		this.trackButtonColor = values[\"btncl\"];\r\n"+
            "	}\r\n"+
            "	if(values.hasOwnProperty(\"iconscl\")){\r\n"+
            "		this.iconsColor = values[\"iconscl\"];\r\n"+
            "	}\r\n"+
            "	if(values.hasOwnProperty(\"bdrcl\")){\r\n"+
            "		this.borderColor = values[\"bdrcl\"];\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(values.hasOwnProperty(\"cornercl\")){\r\n"+
            "		this.cornerColor = values[\"cornercl\"];\r\n"+
            "	}\r\n"+
            "	if(values.hasOwnProperty(\"hideAw\")){\r\n"+
            "		this.hideArrow = values[\"hideAw\"];\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(values.hasOwnProperty(\"borderRad\")){\r\n"+
            "		this.borderRadius= values[\"borderRad\"];\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.scrollPane.hscroll.DOM.applyColors(this.gripColor, this.trackColor, this.trackButtonColor, this.borderColor, this.cornerColor);\r\n"+
            "	this.scrollPane.vscroll.DOM.applyColors(this.gripColor, this.trackColor, this.trackButtonColor, this.borderColor, this.cornerColor);\r\n"+
            "	if (this.hideArrow != null) {\r\n"+
            "		this.scrollPane.hscroll.DOM.hideArrows(this.hideArrow);\r\n"+
            "		this.scrollPane.vscroll.DOM.hideArrows(this.hideArrow);\r\n"+
            "	}\r\n"+
            "	this.scrollPane.updateScroll();\r\n"+
            "	if (this.borderRadius != null) {\r\n"+
            "		this.scrollPane.hscroll.DOM.applyBorderRadius(this.borderRadius);\r\n"+
            "		this.scrollPane.vscroll.DOM.applyBorderRadius(this.borderRadius);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.scrollPane.updateIconsColor(this.iconsColor);\r\n"+
            "}\r\n"+
            "ScrollPortlet.prototype.setScrollBarWidth=function(width){\r\n"+
            "	if(width != null)\r\n"+
            "		this.scrollPane.setSize(width);\r\n"+
            "\r\n"+
            "}\r\n"+
            "ScrollPortlet.prototype.setCssStyle=function(cssStyle){\r\n"+
            "  applyStyle(this.divElement,cssStyle);\r\n"+
            "}\r\n"+
            "\r\n"+
            "//###############################\r\n"+
            "//##### HTML Portlet #####\r\n"+
            "\r\n"+
            "function HtmlPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  var that=this;\r\n"+
            "  this.htmlPortlet=nw('div');\r\n"+
            "  this.htmlPortlet.style.left='0px';\r\n"+
            "  this.htmlPortlet.style.top='0px';\r\n"+
            "  this.htmlPortlet.style.right='0px';\r\n"+
            "  this.htmlPortlet.style.bottom='0px';\r\n"+
            "  this.divElement.appendChild(this.htmlPortlet);\r\n"+
            "  this.htmlPortlet.ondblclick=function(){that.callBack('click',{})};\r\n"+
            "  this.htmlPortlet.callback=function(event,id){\r\n"+
            "      point=getMousePoint(event).move(-4,-4);\r\n"+
            "      var m = {};\r\n"+
            "      for(var i = 1; i < parseInt(arguments.length/2); i++){\r\n"+
            "    	  m[arguments[i*2]] = arguments[i*2+1];\r\n"+
            "      }\r\n"+
            "      m.id=id;\r\n"+
            "      m.mouseX=point.x;\r\n"+
            "      m.mouseY=point.y;\r\n"+
            "	  that.callBack('callback',m)\r\n"+
            "  };\r\n"+
            "  this.htmlPortlet.portlet=that;\r\n"+
            "  this.portlet.divElement.customCb=function(type,params){that.callBack('customCallback',{customType:type,customParams:params});};\r\n"+
            "  //this.divElement.style.overflow='auto';\r\n"+
            "}\r\n"+
            "\r\n"+
            "HtmlPortlet.prototype.setInnerHTML=function(html){\r\n"+
            "  this.htmlPortlet.innerHTML=html;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "HtmlPortlet.prototype.scrollToBottom=function(){\r\n"+
            "  this.htmlPortlet.scrollTop = this.htmlPortlet.scrollHeight;\r\n"+
            "}\r\n"+
            "\r\n"+
            "HtmlPortlet.prototype.getHtmlDiv=function(){\r\n"+
            "	return this.htmlPortlet;\r\n"+
            "}\r\n"+
            "\r\n"+
            "HtmlPortlet.prototype.setCssClass=function(cssClass){\r\n"+
            "  this.htmlPortlet.className=cssClass;\r\n"+
            "};\r\n"+
            "HtmlPortlet.prototype.setCssStyle=function(cssStyle){\r\n"+
            "  applyStyle(this.htmlPortlet,cssStyle);\r\n"+
            "};\r\n"+
            "\r\n"+
            "HtmlPortlet.prototype.setSupportsContextMenu=function(support){\r\n"+
            "	var that=this;\r\n"+
            "	if(support){\r\n"+
            "	  this.htmlPortlet.onclick=function(e){that.onUserContextMenu(e)};\r\n"+
            "	}else{\r\n"+
            "	  this.htmlPortlet.onclick=null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "HtmlPortlet.prototype.onUserContextMenu=function(e){\r\n"+
            "    this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "	this.callBack('showmenu',{});\r\n"+
            "}\r\n"+
            "HtmlPortlet.prototype.showMenu=function(menu){\r\n"+
            "    this.createMenu(menu).show(this.contextMenuPoint);\r\n"+
            "}\r\n"+
            "\r\n"+
            "HtmlPortlet.prototype.createMenu=function(menu){\r\n"+
            "   var that=this;\r\n"+
            "   var r=new Menu(getWindow(this.divElement));\r\n"+
            "   r.createMenu(menu, function(e,id){that.onUserMenuItem(e, id);} );\r\n"+
            "   return r;\r\n"+
            "}\r\n"+
            "HtmlPortlet.prototype.onUserMenuItem=function(e,id){\r\n"+
            "    this.callBack('menuitem',{id:id});\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "//###############################\r\n"+
            "//##### Jsp Portlet #####\r\n"+
            "\r\n"+
            "function JspPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "}\r\n"+
            "\r\n"+
            "//###############################\r\n"+
            "//##### Divider Portlet #####\r\n"+
            "\r\n"+
            "function DividerPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  var that=this;\r\n"+
            "  this.isContainer=true;\r\n"+
            "  this.divider=nw('div');\r\n"+
            "  // swap is replaced by flip panel\r\n"+
            "  // this button has no width/height thus non-interactable\r\n"+
            "//  this.swapButton=nw('div');\r\n"+
            "  this.first=nw('div');\r\n"+
            "  this.second=nw('div');\r\n"+
            "//  this.swapButton.style.position='absolute';\r\n"+
            "  this.first.style.overflow=\"hidden\";\r\n"+
            "  this.second.style.overflow=\"hidden\";\r\n"+
            "  \r\n"+
            "//  this.swapButton.onclick=function(e){that.onSwap(e);};\r\n"+
            "  this.divElement.appendChild(this.first);\r\n"+
            "  this.divElement.appendChild(this.second);\r\n"+
            "//  this.divElement.appendChild(this.swapButton);\r\n"+
            "  this.divElement.appendChild(this.divider);\r\n"+
            "  this.divElement.onmousedown=function(e){if(getMouseButton(e)==2)that.onUserContextMenu(e);};\r\n"+
            "  \r\n"+
            "}\r\n"+
            "\r\n"+
            "DividerPortlet.prototype.onUserContextMenu=function(e){\r\n"+
            "	if(isMouseInside(e,this.divider, -0)==true){\r\n"+
            "		  this.callBack('onCustomMenu',{x:this.owningWindow.MOUSE_POSITION_X,y:this.owningWindow.MOUSE_POSITION_Y});\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DividerPortlet.prototype.init=function(vertical,thickness,lock,bgColor,hoverColor){\r\n"+
            "  var that=this;\r\n"+
            "  this.thickness=thickness;\r\n"+
            "  this.vertical=vertical;\r\n"+
            "  this.lock=lock;\r\n"+
            "  this.bgColor=bgColor;\r\n"+
            "//  if(this.divider!=null)\r\n"+
            "//    this.divElement.removeChild(this.divider);\r\n"+
            "  this.divider.style.backgroundColor=this.bgColor;\r\n"+
            "  this.divider.className=this.vertical ? 'portlet_divider_v' : 'portlet_divider_h';\r\n"+
            "  if(!lock){\r\n"+
            "    this.divElement.ondblclick=null;\r\n"+
            "//    this.swapButton.className=this.vertical ? 'portlet_divider_vswap' : 'portlet_divider_hswap';\r\n"+
            "    if(this.dividerDragger==null){\r\n"+
            "      this.dividerDragger=nw('div');\r\n"+
            "      this.divElement.appendChild(this.dividerDragger);\r\n"+
            "      var dd=this.dividerDragger;\r\n"+
            "      this.dividerDragger.ondraggingEnd=function(e,x,y){that.OnUserMovedDivider(e,x,y);};\r\n"+
            "      this.dividerDragger.ondblclick=function(e){that.onUsrDblClick(e);};\r\n"+
            "      this.dividerDragger.ondragging=function(e,x,y){that.OnUserMovingDivider(e,x,y);};\r\n"+
            "      this.dividerDragger.onmouseleave=function(e){ dd.style.background=\"none\"; };\r\n"+
            "    }\r\n"+
            "    this.dividerDragger.onmouseenter=function(e){ that.dividerDragger.style.background=hoverColor; };\r\n"+
            "    makeDraggable(this.dividerDragger,this.dividerDragger,!vertical,vertical);\r\n"+
            "    if(this.vertical){\r\n"+
            "      this.dividerDragger.style.left=null;\r\n"+
            "      this.dividerDragger.style.right=null;\r\n"+
            "      this.dividerDragger.style.height=null;\r\n"+
            "      this.dividerDragger.style.top='0px';\r\n"+
            "      this.dividerDragger.style.bottom='0px';\r\n"+
            "      this.dividerDragger.style.width=toPx(Math.max(thickness,4));\r\n"+
            "      this.dividerDragger.className='portlet_divider_v_dragger';\r\n"+
            "    }else{\r\n"+
            "      this.dividerDragger.style.top=null;\r\n"+
            "      this.dividerDragger.style.bottom=null;\r\n"+
            "      this.dividerDragger.style.width=null;\r\n"+
            "      this.dividerDragger.style.left='0px';\r\n"+
            "      this.dividerDragger.style.right='0px';\r\n"+
            "      this.dividerDragger.style.height=toPx(Math.max(thickness,4));\r\n"+
            "      this.dividerDragger.className='portlet_divider_h_dragger';\r\n"+
            "    }\r\n"+
            "  }else{\r\n"+
            "    if(this.divider.ondblclick==null)\r\n"+
            "       this.divider.ondblclick=function(e){that.onUsrDblClick(e);};\r\n"+
            "    if(this.dividerDragger!=null){\r\n"+
            "      this.divElement.removeChild(this.dividerDragger);\r\n"+
            "      this.dividerDragger=null;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  if(this.vertical){\r\n"+
            "    this.divider.style.left=null;\r\n"+
            "    this.divider.style.right=null;\r\n"+
            "    this.divider.style.height=null;\r\n"+
            "    this.divider.style.top='0px';\r\n"+
            "    this.divider.style.bottom='0px';\r\n"+
            "    this.divider.style.width=toPx(thickness);\r\n"+
            "  }else{\r\n"+
            "    this.divider.style.top=null;\r\n"+
            "    this.divider.style.bottom=null;\r\n"+
            "    this.divider.style.width=null;\r\n"+
            "    this.divider.style.left='0px';\r\n"+
            "    this.divider.style.right='0px';\r\n"+
            "    this.divider.style.height=toPx(thickness);\r\n"+
            "  }\r\n"+
            "  this.divider.style.backgroundColor=bgColor;\r\n"+
            "  this.first.style.top='0px';\r\n"+
            "  this.first.style.left=");
          out.print(
            "'0px';\r\n"+
            "  this.first.style.bottom='0px';\r\n"+
            "  this.first.style.right='0px';\r\n"+
            "  this.second.style.top='0px';\r\n"+
            "  this.second.style.left='0px';\r\n"+
            "  this.second.style.bottom='0px';\r\n"+
            "  this.second.style.right='0px';\r\n"+
            "  this.dividerMovingInterval=null;\r\n"+
            "  this.dividerMovingOffset=null;\r\n"+
            "  this.dividerMovingProcessed=false;\r\n"+
            "  this.setDividerLocation(this.cleanOffset(this.offset));\r\n"+
            "};\r\n"+
            "\r\n"+
            "DividerPortlet.prototype.onDividerMovingProcessed=function(){\r\n"+
            "    this.dividerMovingProcessed=true;\r\n"+
            "}\r\n"+
            "DividerPortlet.prototype.onUsrDblClick=function(e){\r\n"+
            "    this.callBack(\"onUsrDblClick\",{});\r\n"+
            "}\r\n"+
            "\r\n"+
            "DividerPortlet.prototype.OnUserMovedDivider=function(e,x,y){\r\n"+
            "	// return if there is no change\r\n"+
            "	if (!x && !y) return; \r\n"+
            "  if(this.dividerMovingInterval!=null){\r\n"+
            "	window.clearInterval(this.dividerMovingInterval);\r\n"+
            "    this.dividerMovingInterval=null;\r\n"+
            "  }\r\n"+
            "  var loc=new Rect().readFromElementRelatedToParent(this.dividerDragger);\r\n"+
            "  var offset= ( this.vertical ? loc.left : loc.top ) ;\r\n"+
            "  var divRect = this.divElement.getBoundingClientRect();\r\n"+
            "  var size = ( this.vertical? divRect.width : divRect.height ) - 1;\r\n"+
            "  offset = max(offset, 0);\r\n"+
            "  offset = min(offset, size-this.thickness); \r\n"+
            "  this.callBack('dividerMoved',{offset:offset});\r\n"+
            "};\r\n"+
            "DividerPortlet.prototype.checkMovementInterval=function(){\r\n"+
            "    if(!this.dividerMovingProcessed)\r\n"+
            "    	return;\r\n"+
            "	if(this.offset==this.dividerMovingOffset)\r\n"+
            "		return;\r\n"+
            "	this.dividerMovingOffset=this.offset;\r\n"+
            "    this.callBack('dividerMoving',{offset:this.offset});\r\n"+
            "    this.dividerMovingProcessed=false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DividerPortlet.prototype.OnUserMovingDivider=function(e,x,y){\r\n"+
            "  // Get the current location of the divider;\r\n"+
            "  var loc=new Rect().readFromElementRelatedToParent(this.dividerDragger);\r\n"+
            "  \r\n"+
            "  // get the offset either left or top\r\n"+
            "  // Massage offset into the bounds;\r\n"+
            "  var offset=this.vertical ? loc.left : loc.top;\r\n"+
            "  offset = this.cleanOffset(offset);\r\n"+
            "  \r\n"+
            "  // return early if it hasn't changed\r\n"+
            "  if(this.offset==offset)\r\n"+
            "	  return;\r\n"+
            "  \r\n"+
            "  //callback for what?...\r\n"+
            "  if(this.dividerMovingInterval==null){\r\n"+
            "	var that=this;\r\n"+
            "    this.callBack('dividerMovingStarted',{offset:offset});\r\n"+
            "    this.dividerMovingProcessed=false;\r\n"+
            "	this.dividerMovingOffset=offset;\r\n"+
            "	var func=function(){that.checkMovementInterval();}\r\n"+
            "	this.dividerMovingInterval=window.setInterval(func,50);\r\n"+
            "  }\r\n"+
            "  this.moveDividerLocation(offset);\r\n"+
            "};\r\n"+
            "\r\n"+
            "//DividerPortlet.prototype.onSwap=function(e){\r\n"+
            "//  this.callBack('swap',{});\r\n"+
            "//};\r\n"+
            "\r\n"+
            "DividerPortlet.prototype.cleanOffset=function(offset){\r\n"+
            "  var ploc=this.portlet.location;\r\n"+
            "  if(offset<0)\r\n"+
            "    offset=0;\r\n"+
            "  else if(!this.vertical && offset>ploc.height-this.thickness)\r\n"+
            "    offset=ploc.height-this.thickness;\r\n"+
            "  else if(this.vertical && offset>ploc.width-this.thickness)\r\n"+
            "    offset=ploc.width-this.thickness;\r\n"+
            "  return offset;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DividerPortlet.prototype.moveDividerLocation=function(offset){\r\n"+
            "  var loc=this.portlet.location;\r\n"+
            "  this.offset=offset;\r\n"+
            "  \r\n"+
            "  var pxOffset = toPx(offset);\r\n"+
            "  //Do the vertical or the horizontal \r\n"+
            "  if(this.vertical){\r\n"+
            "    this.divider.style.left=pxOffset;\r\n"+
            "    if(this.dividerDragger!=null)\r\n"+
            "      this.dividerDragger.style.left=pxOffset;\r\n"+
            "//    this.swapButton.style.left=pxOffset;\r\n"+
            "    \r\n"+
            "    this.first.style.width=pxOffset;\r\n"+
            "    \r\n"+
            "    this.second.style.left=toPx(offset+this.thickness);\r\n"+
            "    this.second.style.width=toPx(loc.width-offset-this.thickness);\r\n"+
            "  }else{\r\n"+
            "    this.divider.style.top=pxOffset\r\n"+
            "    if(this.dividerDragger!=null)\r\n"+
            "      this.dividerDragger.style.top=pxOffset;\r\n"+
            "//    this.swapButton.style.top=pxOffset;\r\n"+
            "    \r\n"+
            "    this.first.style.height=pxOffset;\r\n"+
            "    \r\n"+
            "    this.second.style.top=toPx(offset+this.thickness);\r\n"+
            "    this.second.style.height=toPx(loc.height-offset-this.thickness);\r\n"+
            "  }\r\n"+
            "  if(this.onDividerLocation)\r\n"+
            "	  this.onDividerLocation(offset)\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "DividerPortlet.prototype.setDividerLocation=function(offset){\r\n"+
            "  var loc=this.portlet.location;\r\n"+
            "  this.offset=offset;\r\n"+
            "  \r\n"+
            "  var pxOffset = toPx(offset);\r\n"+
            "  //Do the vertical or the horizontal \r\n"+
            "  if(this.vertical){\r\n"+
            "    this.divider.style.left=pxOffset;\r\n"+
            "    if(this.dividerDragger!=null)\r\n"+
            "      this.dividerDragger.style.left=pxOffset;\r\n"+
            "//    this.swapButton.style.left=pxOffset;\r\n"+
            "    \r\n"+
            "    this.first.style.width=pxOffset;\r\n"+
            "    \r\n"+
            "    this.second.style.left=toPx(offset+this.thickness);\r\n"+
            "    this.second.style.width=toPx(loc.width-offset-this.thickness);\r\n"+
            "    \r\n"+
            "    this.first.style.height=toPx(loc.height);\r\n"+
            "    this.second.style.height=toPx(loc.height);\r\n"+
            "  }else{\r\n"+
            "    this.divider.style.top=pxOffset;\r\n"+
            "    if(this.dividerDragger!=null)\r\n"+
            "      this.dividerDragger.style.top=pxOffset;\r\n"+
            "//    this.swapButton.style.top=pxOffset;\r\n"+
            "    \r\n"+
            "    this.first.style.height=pxOffset;\r\n"+
            "    \r\n"+
            "    this.second.style.top=toPx(offset+this.thickness);\r\n"+
            "    this.second.style.height=toPx(loc.height-offset-this.thickness);\r\n"+
            "    \r\n"+
            "    this.first.style.width=toPx(loc.width);\r\n"+
            "    this.second.style.width=toPx(loc.width);\r\n"+
            "  }\r\n"+
            "  if(this.onDividerLocation)\r\n"+
            "	  this.onDividerLocation(offset)\r\n"+
            "};\r\n"+
            "\r\n"+
            "DividerPortlet.prototype.addChild=function(loc,childId){\r\n"+
            "  if(loc==1)\r\n"+
            "    this.first.appendChild(portletManager.getPortlet(childId).divElement);\r\n"+
            "  else\r\n"+
            "    this.second.appendChild(portletManager.getPortlet(childId).divElement);\r\n"+
            "  this.portlet.addChild(childId);\r\n"+
            "};\r\n"+
            "\r\n"+
            "//###############################\r\n"+
            "//##### Color Picker #####\r\n"+
            "\r\n"+
            "\r\n"+
            "function ColorPickerPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.windows=[];\r\n"+
            "  this.childIdsToPositions={};\r\n"+
            "  this.dividers=[];\r\n"+
            "  var that=this;\r\n"+
            "  this.minSize=10;\r\n"+
            "  this.isContainer=true;\r\n"+
            "  this.divElement.style.background='white';\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPickerPortlet.prototype.init=function(defaultColor,color,alpha){\r\n"+
            "  var that=this;\r\n"+
            "  this.colorPicker=new ColorPicker(false,color,this.divElement,false);\r\n"+
            "  if(alpha)\r\n"+
            "	  this.colorPicker.setAlphaEnabled();\r\n"+
            "  this.colorPicker.onOk=function(){that.onColorPickerOk()};\r\n"+
            "  this.colorPicker.onCancel=function(){that.onColorPickerCancel()};\r\n"+
            "  this.colorPicker.onColorChanged=function(color){that.onColorChanged(color)};\r\n"+
            "  this.colorPicker.onNoColor=function(){that.onColorPickerNoColor()};\r\n"+
            "  this.colorPicker.show(0,0);\r\n"+
            "}\r\n"+
            "ColorPickerPortlet.prototype.addColorChoice=function(choice){\r\n"+
            "  this.colorPicker.addColorChoice(choice);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPickerPortlet.prototype.onColorPickerOk=function(){\r\n"+
            "    this.callBack('onOkay',{});\r\n"+
            "}\r\n"+
            "ColorPickerPortlet.prototype.onColorChanged=function(color){\r\n"+
            "    this.callBack('onColorChanged',{color:this.colorPicker.getColor()});\r\n"+
            "}\r\n"+
            "ColorPickerPortlet.prototype.onColorPickerCancel=function(){\r\n"+
            "    this.callBack('onCancel',{});\r\n"+
            "}\r\n"+
            "ColorPickerPortlet.prototype.onColorPickerNoColor=function(){\r\n"+
            "	this.colorPicker.values.color=null;\r\n"+
            "    this.callBack('onNoColor',{});\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "//###############################\r\n"+
            "//##### Multi Divider Portlet #####\r\n"+
            "\r\n"+
            "function MultiDividerPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.windows=[];\r\n"+
            "  this.childIdsToPositions={};\r\n"+
            "  this.dividers=[];\r\n"+
            "  this.dividerDraggers=[];\r\n"+
            "  var that=this;\r\n"+
            "  this.minSize=10;\r\n"+
            "  this.isContainer=true;\r\n"+
            "  this.divElement.style.background='white';\r\n"+
            "}\r\n"+
            "MultiDividerPortlet.prototype.reset=function(){\r\n"+
            "  this.windows=[];\r\n"+
            "  this.childIdsToPositions={};\r\n"+
            "  this.dividers=[];\r\n"+
            "  this.dividerDraggers=[];\r\n"+
            "  removeAllChildren(this.divElement);\r\n"+
            "}\r\n"+
            "MultiDividerPortlet.prototype.addChild=function(pos,childId){\r\n"+
            "  var window=nw('div','fa');\r\n"+
            "  //var pos=this.windows.length;\r\n"+
            "  this.windows[pos]=window;\r\n"+
            "  window.style.overflow=\"hidden\";\r\n"+
            "  this.childIdsToPositions[childId]=pos;\r\n"+
            "  window.appendChild(portletManager.getPortlet(childId).divElement);\r\n"+
            "  this.divElement.insertBefore(window,this.divElement.firstChild);\r\n"+
            "  this.portlet.addChild(childId);\r\n"+
            "  if(pos>0){\r\n"+
            "    var divider=nw('div');\r\n"+
            "    var dividerDragger = nw('div');\r\n"+
            "    divider.className=this.vertical ? 'portlet_divider_v' : 'portlet_divider_h';\r\n"+
            "    dividerDragger.className=this.vertical ? 'portlet_divider_v_dragger' : 'portlet_divider_h_dragger';\r\n"+
            "    dividerDragger.style.opacity = 0;\r\n"+
            "    this.vertical == true ?	dividerDragger.style.width=toPx(9):dividerDragger.style.height=toPx(4);\r\n"+
            "    this.dividers[pos-1]=divider;\r\n"+
            "    this.dividerDraggers[pos-1]=dividerDragger;\r\n"+
            "    divider.pos=pos;\r\n"+
            "    dividerDragger.pos=pos;\r\n"+
            "    var that=this;\r\n"+
            "    makeDraggable(dividerDragger,null,!this.vertical,this.vertical);\r\n"+
            "    dividerDragger.ondraggingEnd=function(e,x,y){that.OnUserMovedDivider(e,x,y);};\r\n"+
            "    dividerDragger.ondragging=function(e,x,y){that.OnUserMovingDivider(e,x,y);};\r\n"+
            "    this.divElement.appendChild(divider);\r\n"+
            "    this.divElement.appendChild(dividerDragger);\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "MultiDividerPortlet.prototype.OnUserMovedDivider=function(e,x,y){\r\n"+
            "	var pos=this.determineDivPos(e.pos,x,y);\r\n"+
            "	this.applyPositions(pos,false,true);\r\n"+
            "    this.callBack('dividerMoved',{positions:pos.join(',')});\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiDividerPortlet.prototype.OnUserMovingDivider=function(e,x,y){\r\n"+
            "	var pos=this.determineDivPos(e.pos,x,y);\r\n"+
            "	this.applyPositions(pos,false,true);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "MultiDividerPortlet.prototype.determineDivPos=function(pos,x,y){\r\n"+
            "	var delta=this.vertical ? x : y;\r\n"+
            "	var clone=this.positions.slice(0);\r\n"+
            "	var offset=clone[pos]+delta;\r\n"+
            "	var spacing=this.thickness+this.minSize;\r\n"+
            "	if(delta>0){\r\n"+
            "		var max=(this.vertical ? this.location.width : this.location.height)-spacing*(clone.length-pos)+this.thickness;\r\n"+
            "		if(offset>max)\r\n"+
            "			offset=Math.max(max,clone[pos]);\r\n"+
            "		while(clone[pos]<offset){\r\n"+
            "			clone[pos++]=offset;\r\n"+
            "			offset+=spacing;\r\n"+
            "		}\r\n"+
            "	}else{\r\n"+
            "		var min=spacing*pos;\r\n"+
            "		if(offset<min)\r\n"+
            "			offset=Math.min(min,clone[pos]);\r\n"+
            "		while(clone[pos]>offset){\r\n"+
            "			clone[pos--]=offset;\r\n"+
            "			offset-=spacing;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	return clone;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "MultiDividerPortlet.prototype.removeChild=function(childId){\r\n"+
            "  var pos=this.childIdsToPositions[childId];\r\n"+
            "  delete this.childIdsToPositions[childId];\r\n"+
            "  var window=this.windows[pos];\r\n"+
            "  delete this.windows[childId];\r\n"+
            "  this.divElement.removeChild(window);\r\n"+
            "  this.portlet.removeChild(childId);\r\n"+
            "  if(this.pos>0){\r\n"+
            "    var divider=this.dividers[pos-1];\r\n"+
            "");
          out.print(
            "    delete this.dividers[pos-1];\r\n"+
            "    this.divElement.removeChild(divider);\r\n"+
            "    var dividerDragger = this.dividerDraggers[pos-1];\r\n"+
            "    delete this.dividerDraggers[pos-1];\r\n"+
            "    this.divElement.removeChild(dividerDragger);\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "MultiDividerPortlet.prototype.setPositions=function(positions){\r\n"+
            "	this.positions=positions;\r\n"+
            "	this.applyPositions(this.positions,true,true);\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiDividerPortlet.prototype.applyPositions=function(positions,includePanels,includeDividers){\r\n"+
            "	if(this.vertical){\r\n"+
            "	  for(var i=0;i<positions.length;i++){\r\n"+
            "		  if(includePanels){\r\n"+
            "		    var window=this.windows[i];\r\n"+
            "		    window.style.top=\"0px\";\r\n"+
            "		    window.style.bottom=\"0px\";\r\n"+
            "		    window.style.left=toPx(positions[i]);\r\n"+
            "		    if(i==positions.length-1){\r\n"+
            "		      window.style.right=\"0px\";\r\n"+
            "		    }else{\r\n"+
            "		      window.style.width=toPx(positions[i+1]-positions[i]-this.thickness);\r\n"+
            "		    }\r\n"+
            "		  }\r\n"+
            "		  if(i>0 && includeDividers){\r\n"+
            "			var divider=this.dividers[i-1];\r\n"+
            "		    divider.style.top=\"0px\";\r\n"+
            "		    divider.style.bottom=\"0px\";\r\n"+
            "		    divider.style.left=toPx(positions[i]-this.thickness);\r\n"+
            "		    divider.style.width=toPx(this.thickness);\r\n"+
            "		    var dividerDragger = this.dividerDraggers[i-1];\r\n"+
            "		    dividerDragger.style.top=\"0px\";\r\n"+
            "		    dividerDragger.style.bottom=\"0px\";\r\n"+
            "		    dividerDragger.style.left=toPx(positions[i]-this.thickness);\r\n"+
            "            if(this.color!=null)\r\n"+
            "              divider.style.background=this.color;\r\n"+
            "		  }\r\n"+
            "	  }\r\n"+
            "	}else{\r\n"+
            "	  for(var i=0;i<positions.length;i++){\r\n"+
            "		  if(includePanels){\r\n"+
            "		    var window=this.windows[i];\r\n"+
            "		    window.style.left=\"0px\";\r\n"+
            "		    window.style.right=\"0px\";\r\n"+
            "		    window.style.top=toPx(positions[i]);\r\n"+
            "		    if(i==positions.length-1){\r\n"+
            "		      window.style.bottom=\"0px\";\r\n"+
            "		    }else{\r\n"+
            "		      window.style.height=toPx(positions[i+1]-positions[i]-this.thickness);\r\n"+
            "		    }\r\n"+
            "		  }\r\n"+
            "		  if(i>0 && includeDividers){\r\n"+
            "			var divider=this.dividers[i-1];\r\n"+
            "		    divider.style.left=\"0px\";\r\n"+
            "		    divider.style.right=\"0px\";\r\n"+
            "		    divider.style.top=toPx(positions[i]-this.thickness);\r\n"+
            "		    divider.style.height=toPx(this.thickness);\r\n"+
            "		    var dividerDragger = this.dividerDraggers[i-1];\r\n"+
            "		    dividerDragger.style.left=\"0px\";\r\n"+
            "		    dividerDragger.style.right=\"0px\";\r\n"+
            "		    dividerDragger.style.top=toPx(positions[i]-this.thickness);\r\n"+
            "            if(this.color!=null)\r\n"+
            "              divider.style.background=this.color;\r\n"+
            "		  }\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiDividerPortlet.prototype.init=function(vertical,thickness,lock,color,minSize){\r\n"+
            "  var that=this;\r\n"+
            "  this.thickness=thickness;\r\n"+
            "  this.vertical=vertical;\r\n"+
            "  this.lock=lock;\r\n"+
            "  this.color=color;\r\n"+
            "  this.minSize=minSize;\r\n"+
            "  this.reset();\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "//#######################\r\n"+
            "//##### Tab Portlet #####\r\n"+
            "\r\n"+
            "function TabPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  var that=this;\r\n"+
            "  this.tabs=new Tabs(this.portlet,parentId);\r\n"+
            "//  this.portlet=this.tabs.portlet;\r\n"+
            "  this.tabs.callBack=function(action,params){that.callBack(action,params);};\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "TabPortlet.prototype.initTabs=function(isAWTP,allowAdd,allowMenu,addButtonText,bgColor,selectedColor,unselectedColor,selectTextColor,unselectTextColor,selectShadow,unselectShadow, isHidden,tabHeight,\r\n"+
            "		tabPaddingTop,tabPaddingBottom, tabSpacing,fontSize, beginningPadding,isFarAligned,isBottom,isVertical, leftRadius,rightRadius,menuArrowColor, menuArrowSize, tabFloatSize,tabPaddingStart,\r\n"+
            "		hasAddButton, addButtonColor,borderColor,fontFamily,selBorderColor,selBorderSize,hideArrangeTabs){ \r\n"+
            "	this.tabs.initTabs(isAWTP,allowAdd,allowMenu,addButtonText,bgColor,selectedColor,unselectedColor,selectTextColor,unselectTextColor,selectShadow,unselectShadow, isHidden,tabHeight,\r\n"+
            "			tabPaddingTop,tabPaddingBottom,tabSpacing,fontSize,beginningPadding,isFarAligned,isBottom,isVertical, leftRadius,rightRadius,menuArrowColor, menuArrowSize,tabFloatSize, tabPaddingStart,\r\n"+
            "			hasAddButton,addButtonColor,borderColor,fontFamily,selBorderColor,selBorderSize,hideArrangeTabs);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TabPortlet.prototype.updatePosition=function(){\r\n"+
            "	this.tabs.updatePosition();\r\n"+
            "}\r\n"+
            "TabPortlet.prototype.getTabs=function(){\r\n"+
            "	return this.tabs;\r\n"+
            "}\r\n"+
            "TabPortlet.prototype.setSize=function(width,height){\r\n"+
            "	this.tabs.setSize(width,height);\r\n"+
            "}\r\n"+
            "TabPortlet.prototype.rp=function(){\r\n"+
            "	this.tabs.rp();\r\n"+
            "}\r\n"+
            "TabPortlet.prototype.focusTab=function(){\r\n"+
            "	this.tabs.focusTab();\r\n"+
            "}\r\n"+
            "TabPortlet.prototype.setActiveTab=function(tabIndex){\r\n"+
            "	this.tabs.setActiveTab(tabIndex);\r\n"+
            "}\r\n"+
            "TabPortlet.prototype.addTab=function(location,tabName, tabNameHtml,allowTitleEdit,selectColor,unselectColor,selectTextColor,unselectTextColor,isHidden,blinkColor,blinkPeriod,his){\r\n"+
            "	this.tabs.addTab(location,tabName, tabNameHtml,allowTitleEdit,selectColor,unselectColor,selectTextColor,unselectTextColor,isHidden,blinkColor,blinkPeriod,his);\r\n"+
            "}\r\n"+
            "TabPortlet.prototype.addChild=function(childId){\r\n"+
            "	this.tabs.addChild(childId);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TabPortlet.prototype.handleWheel=function(e){\r\n"+
            "	this.tabs.scrollPane.handleWheel(e);\r\n"+
            "}\r\n"+
            "//#########################\r\n"+
            "//##### Table Portlet #####\r\n"+
            "\r\n"+
            "function TablePortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "}\r\n"+
            "\r\n"+
            "//#########################\r\n"+
            "//##### FastTable Portlet #####\r\n"+
            "\r\n"+
            "function FastTablePortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.headerHeight=20;\r\n"+
            "  this.headerHeight=0;\r\n"+
            "  this.tableDiv=nw(\"div\");\r\n"+
            "  this.headerDiv=nw(\"div\",\"table_search\");\r\n"+
            "  this.titleDiv=nw(\"div\",\"table_title\");\r\n"+
            "  this.titleSpan=nw(\"span\",\"table_title_span\");\r\n"+
            "  this.searchInput=nw(\"input\",\"table_search_input\");\r\n"+
            "  //MOBILE SUPPORT - enables enter key in mobile to be pressed instead of tabbing to next field for search\r\n"+
            "  this.searchInput.setAttribute('enterkeyhint', 'go');    	\r\n"+
            "  this.searchInputButton=nw(\"div\",\"table_search_button\");\r\n"+
            "  makeEnterable(this.searchInput,this.searchInputButton);\r\n"+
            "  \r\n"+
            "  this.downloadInputButton=nw(\"div\",\"table_download_button\");\r\n"+
            "	  \r\n"+
            "  var that=this;\r\n"+
            "  this.table = new FastTable(this.tableDiv);\r\n"+
            "  this.table.portlet=this;\r\n"+
            "  this.searchInputButton.onclick=function(e){that.onUserSearch();};\r\n"+
            "  this.downloadInputButton.onclick=function(e){that.onUserDownload();};\r\n"+
            "  this.table.fetchdata=function(i,j,table){return that.onTableFetchData(i,j,table);};\r\n"+
            "  this.table.onUpdateCellsEnd=function(table){that.onTableUpdateCellsEnd(table);};\r\n"+
            "  this.table.onUserSort=function(e,col,type){that.onUserSort(e,col,type);};\r\n"+
            "  this.table.onUserColumnResize=function(e,col,width){that.onUserColumnResize(e,col,width);};\r\n"+
            "  this.table.onUserShowFilter=function(e,col){that.onUserShowFilter(e,col);};\r\n"+
            "  this.table.onUserFiltered=function(e,col,values){that.onUserFiltered(e,col,values);};\r\n"+
            "  this.table.onUserSelected=function(e,activeRow,selectedRows,userSeqnum){that.onUserSelected(e,activeRow,selectedRows,userSeqnum);};\r\n"+
            "  this.table.onUserNavigate=function(e,activeRow,action,text){that.onUserNavigate(e,activeRow,action,text);};\r\n"+
            "  this.table.onUserArrangedColumns=function(e,col,values){that.onUserArrangedColumns(e,col,values);};\r\n"+
            "  this.table.onUserContextMenu=function(e){that.onUserContextMenu(e);};\r\n"+
            "  this.table.onUserHeaderMenu=function(e,col){that.onUserHeaderMenu(e,col);};\r\n"+
            "  this.table.onUserContextMenuItem=function(e,id){that.onUserContextMenuItem(e,id);};\r\n"+
            "  this.table.onUserHeaderMenuItem=function(e,id,col){that.onUserHeaderMenuItem(e,id,col);};\r\n"+
            "  this.table.onUserHelp=function(e){that.onUserHelp(e);};\r\n"+
            "  this.table.onHover=function(x,y){that.onHover(x,y);};\r\n"+
            "  this.table.callback=function(method,args){that.callBack(method,args);};\r\n"+
            "  this.divElement.style.background='white';\r\n"+
            "  this.divElement.appendChild(this.tableDiv);\r\n"+
            "  this.divElement.appendChild(this.headerDiv);\r\n"+
            "  this.headerDiv.appendChild(this.titleDiv);\r\n"+
            "  this.headerDiv.appendChild(this.downloadInputButton);\r\n"+
            "  this.headerDiv.appendChild(this.searchInput);\r\n"+
            "  this.headerDiv.appendChild(this.searchInputButton);\r\n"+
            "  this.titleDiv.appendChild(this.titleSpan);\r\n"+
            "//  this.currentUpperRowVisible=-1;\r\n"+
            "//  this.currentLowerRowVisible=-1;\r\n"+
            "	this.table.menuHeight = this.headerHeight;\r\n"+
            "	this.headerDiv.style.height=toPx(this.headerHeight);\r\n"+
            "	this.enableEditing=true;\r\n"+
            "	this.fastTableEditor = null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//Message structure:\r\n"+
            "//m: list of cells\r\n"+
            "//Cell structure:\r\n"+
            "//x x cell pos\r\n"+
            "//y y cell pos\r\n"+
            "//v value\r\n"+
            "//o option\r\n"+
            "FastTablePortlet.prototype.editRowsComplete=function(){\r\n"+
            "	if(this.fastTableEditor){\r\n"+
            "		this.fastTableEditor.deactivate();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTablePortlet.prototype.setAutocomplete=function(col,vals){\r\n"+
            "    this.table.setAutocomplete(col,vals);\r\n"+
            "}\r\n"+
            "FastTablePortlet.prototype.editRows=function(m){\r\n"+
            "	if(this.enableEditing == true){\r\n"+
            "		if(this.fastTableEditor == null){\r\n"+
            "			this.fastTableEditor = new FastTableEditor(this); \r\n"+
            "		}\r\n"+
            "		this.fastTableEditor.edit(m);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.handleWheel=function(e){\r\n"+
            "	this.table.scrollPane.handleWheel(e);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.handleDblclick=function(e){\r\n"+
            "	//Checks if mouse is inside any of the visible cells \r\n"+
            "	if(isMouseInside(e,this.table.scrollPane.DOM.paneElement, -0)){\r\n"+
            "		this.onUserDblclick(e, {action:\"callRelationship\"});\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.handleKeydown=function(e, _target){\r\n"+
            "	if(e.target != null && e.target.isQuickColumnFilterElement == true){\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	if(this.fastTableEditor && this.fastTableEditor.active == true){\r\n"+
            "		this.fastTableEditor.handleKeydown(e,_target);\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	if (_target.nodeName == \"INPUT\") {\r\n"+
            "		if (e.target == this.searchInput && e.key == \"Escape\") {\r\n"+
            "			this.setSearch(null);\r\n"+
            "			this.onUserSearch();\r\n"+
            "		}\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	var shiftKey = e.shiftKey;\r\n"+
            "	var ctrlKey = e.ctrlKey;\r\n"+
            "	var altKey = e.altKey;\r\n"+
            "	switch(e.key){	\r\n"+
            "	case \" \":\r\n"+
            "		this.table.jumpToNextUniqueCol(e);\r\n"+
            "		break;\r\n"+
            "	case \"ArrowLeft\":\r\n"+
            "		this.callBack('ArrowLeft',{left:this.table.scrollPane.getClipLeft()});\r\n"+
            "		break;\r\n"+
            "	case \"ArrowRight\":\r\n"+
            "		this.callBack('ArrowRight',{left:this.table.scrollPane.getClipLeft()});\r\n"+
            "		break;\r\n"+
            "	case \"ArrowDown\":\r\n"+
            "		this.table.scroll(e,1);\r\n"+
            "		break;\r\n"+
            "	case \"ArrowUp\":\r\n"+
            "		this.table.scroll(e,-1);\r\n"+
            "		break;\r\n"+
            "	case \"PageDown\":\r\n"+
            "		this.ta");
          out.print(
            "ble.scroll(e,this.table.visibleRowsCount);\r\n"+
            "		break;\r\n"+
            "	case \"PageUp\":\r\n"+
            "		this.table.scroll(e,-this.table.visibleRowsCount);\r\n"+
            "		break;\r\n"+
            "	case \"Escape\":\r\n"+
            "		this.table.clearSelected();\r\n"+
            "		break;\r\n"+
            "	case \"Enter\":\r\n"+
            "		this.table.nextColWithText(e);\r\n"+
            "		break;\r\n"+
            "	case \"c\":\r\n"+
            "	case \"C\":\r\n"+
            "		if(ctrlKey){\r\n"+
            "			this.table.openCopyMenu(e);\r\n"+
            "		}\r\n"+
            "		break;\r\n"+
            "	case \"a\":\r\n"+
            "	case \"A\":\r\n"+
            "		if(ctrlKey){\r\n"+
            "			this.table.selectAll();\r\n"+
            "		}\r\n"+
            "		break;\r\n"+
            "	default:\r\n"+
            "		break;	\r\n"+
            "	}	\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.handleKeypress=function(e, _target){\r\n"+
            "	if(this.fastTableEditor && this.fastTableEditor.active == true){\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	this.table.jumpToColWithText(e);\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.ensureRowVisible=function(row){\r\n"+
            "	this.table.ensureRowVisible(row);\r\n"+
            "}\r\n"+
            "FastTablePortlet.prototype.setOptions=function(options){\r\n"+
            "	if(options.menuBarHidden==\"true\"){\r\n"+
            "		this.headerHeight=0;\r\n"+
            "		this.menuBarHidden=true;\r\n"+
            "		this.headerDiv.style.visibility=\"hidden\";\r\n"+
            "	} else{\r\n"+
            "		this.headerHeight=20;\r\n"+
            "		this.menuBarHidden=false;\r\n"+
            "		this.headerDiv.style.visibility=\"visible\";\r\n"+
            "	}\r\n"+
            "	this.table.menuHeight=this.headerHeight;\r\n"+
            "	this.headerDiv.style.height=toPx(this.headerHeight);\r\n"+
            "	this.headerDiv.style.borderColor=options.searchBarDivColor;\r\n"+
            "	this.table.height = this.height - this.headerHeight;\r\n"+
            "	this.table.setOptions(options);\r\n"+
            "	this.searchInput.style.backgroundColor=options.searchBarColor;\r\n"+
            "	this.searchInput.style.color=options.searchBarFontColor;\r\n"+
            "	this.searchInput.style.borderColor=options.searchFieldBorderColor;\r\n"+
            "	this.searchFieldBackgroundInactiveColor = options.searchBarColor;\r\n"+
            "	this.searchFieldFontInactiveColor = options.searchBarFontColor;\r\n"+
            "	this.searchFieldBackgroundActiveColor = options.filteredColumnBgColor;\r\n"+
            "	this.searchFieldFontActiveColor = options.filteredColumnFontColor;\r\n"+
            "	searchButtonsColor=options.searchButtonsColor || '#ffffff';\r\n"+
            "  	var searchButtonSvgStyle=searchButtonsColor+';stroke-width:14;stroke-miterlimit:10;\" cx=\"41.3\" cy=\"41.2\" r=\"24.7\"/> <line style=\"fill:none;stroke:'+searchButtonsColor;\r\n"+
            "  	var downloadButtonSvgStyle=searchButtonsColor+';stroke-width:10;stroke-miterlimit:10;\" x1=\"8\" y1=\"91\" x2=\"92\" y2=\"91\"/> <g> <line style=\"fill:none;stroke:'+searchButtonsColor+';stroke-width:12;stroke-miterlimit:10;\" x1=\"50\" y1=\"8\" x2=\"50\" y2=\"71\"/> <polyline style=\"fill:none;stroke:'+searchButtonsColor;\r\n"+
            "  	searchButtonSvgStyle=searchButtonSvgStyle.replace(/#/g, \"%23\");\r\n"+
            "  	downloadButtonSvgStyle=downloadButtonSvgStyle.replace(/#/g, \"%23\");\r\n"+
            "  	this.searchInputButton.style.backgroundImage=(SEARCH_BUTTON_IMAGE_PREFIX+searchButtonSvgStyle+SEARCH_BUTTON_IMAGE_SUFFIX);\r\n"+
            "  	this.downloadInputButton.style.backgroundImage=(DOWNLOAD_BUTTON_IMAGE_PREFIX+downloadButtonSvgStyle+DOWNLOAD_BUTTON_IMAGE_SUFFIX);\r\n"+
            "	this.titleDiv.style.backgroundColor=options.titleBarColor;\r\n"+
            "	this.titleDiv.style.color=options.titleBarFontColor;	\r\n"+
            "	this.titleDiv.style.fontFamily=options.fontFamily || \"arial\";	\r\n"+
            "//		this.setSize(this.width,this.height);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.setTitle=function(title,isFiltered){\r\n"+
            "	//possible bug? isFiltered is false when quick column filter is active\r\n"+
            "	this.titleSpan.innerHTML=title;\r\n"+
            "	/* title should have the filtered header color if filter is set\r\n"+
            "	if(isFiltered)\r\n"+
            "		this.titleSpan.style.color = this.table.filteredColumnBgColor;\r\n"+
            "	else\r\n"+
            "		this.titleSpan.style.color = this.titleDiv.style.color;\r\n"+
            "	*/\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.setSearch=function(expression){\r\n"+
            "	this.searchInput.value=expression;\r\n"+
            "	if(expression) {\r\n"+
            "		this.searchInput.style.backgroundColor = this.searchFieldBackgroundActiveColor;\r\n"+
            "		this.searchInput.style.color = this.searchFieldFontActiveColor;\r\n"+
            "	} else {\r\n"+
            "		this.searchInput.style.backgroundColor = this.searchFieldBackgroundInactiveColor;\r\n"+
            "		this.searchInput.style.color = this.searchFieldFontInactiveColor;\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.onUserDblclick=function(e, data){\r\n"+
            "	this.callBack('userDblclick', data);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.onUserShowFilter=function(e,col){\r\n"+
            "    this.callBack('showFilterDialog',{columnIndex:col});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onUserContextMenu=function(e,col){\r\n"+
            "	this.table.curseqnum=nextSendSeqnum;\r\n"+
            "    this.callBack('showMenu',{});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onUserHeaderMenu=function(e,col){\r\n"+
            "//	console.log(e);\r\n"+
            "    this.callBack('showHeaderMenu',{col:col});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onUserContextMenuItem=function(e,id){\r\n"+
            "    this.callBack('menuitem',{action:id});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onUserHeaderMenuItem=function(e,id,col){\r\n"+
            "    this.callBack('headerMenuitem',{action:id,col:col});\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.onUserHelp=function(e,col){\r\n"+
            "    this.callBack('showhelp',{columnIndex:col});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onUserFiltered=function(e,col,values){\r\n"+
            "    this.callBack('filter',{columnIndex:col,values:values});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onUserSelected=function(e,activeRow,selectedRows,userSeqnum){\r\n"+
            "    this.callBack('userSelect',{activeRow:activeRow,selectedRows:selectedRows,userSeqnum:userSeqnum});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onUserNavigate=function(e,activeRow,action,text){\r\n"+
            "    this.callBack('userNavigate',{activeRow:activeRow,action:action,text:text});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onUserArrangedColumns=function(e,col,columns){\r\n"+
            "    this.callBack('columns',{columns:columns});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onUserSearch=function(){\r\n"+
            "    //portletManager.showWait(\"Searching\");\r\n"+
            "	var expression=this.searchInput.value;\r\n"+
            "	if(expression){\r\n"+
            "		// this.searchInput.className=\"table_search_input_active\";\r\n"+
            "		this.searchInput.style.backgroundColor=this.searchFieldBackgroundActiveColor;\r\n"+
            "		this.searchInput.style.color=this.searchFieldFontActiveColor;\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		// this.searchInput.className=\"table_search_input\";\r\n"+
            "		this.searchInput.style.backgroundColor=this.searchFieldBackgroundInactiveColor;\r\n"+
            "		this.searchInput.style.color=this.searchFieldFontInactiveColor;\r\n"+
            "	}\r\n"+
            "    this.callBack('search',{expression:expression});\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.onUserDownload=function(){\r\n"+
            "	//portletManager.showWait(\"Downloading\");\r\n"+
            "	this.callBack('download',{x:this.owningWindow.MOUSE_POSITION_X,y:this.owningWindow.MOUSE_POSITION_Y});\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.onUserColumnResize=function(e,col,width){\r\n"+
            "    this.callBack('columnWidth',{columnIndex:col,width:width});\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.onUserSort=function(e,col,type){\r\n"+
            "	//if(this.table.getRowsCount()>100000)\r\n"+
            "      //portletManager.showWait(\"Sorting\");\r\n"+
            "   this.callBack('sort',{columnIndex:col,sortType:type});\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.lastClipUpper;\r\n"+
            "FastTablePortlet.prototype.lastClipLower;\r\n"+
            "FastTablePortlet.prototype.lastClipLeft;\r\n"+
            "FastTablePortlet.prototype.lastClipRight;\r\n"+
            "FastTablePortlet.prototype.lastClipLeftPin;\r\n"+
            "FastTablePortlet.prototype.lastClipRightPin;\r\n"+
            "FastTablePortlet.prototype.onTableUpdateCellsEnd=function(i,j){\r\n"+
            "	if(this.table.stateInit != true)\r\n"+
            "		return;\r\n"+
            "	var upper=this.table.getUpperRowVisible();\r\n"+
            "	var lower=this.table.getLowerRowVisible();\r\n"+
            "	var leftPin = this.table.firstVisiblePinnedColumn;\r\n"+
            "	var rightPin= this.table.lastVisiblePinnedColumn;\r\n"+
            "	var left = this.table.firstVisibleColumn;\r\n"+
            "	var right= this.table.lastVisibleColumn;\r\n"+
            "	if (this.table.scrollPane.vscrollChanged == 0 && !this.rowVisibilityChanged(upper, lower, left, right, leftPin, rightPin)) {\r\n"+
            "		// not a vertical scroll and row visibility did not change\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	// vertical scroll\r\n"+
            "	// Not a v. scroll but row visibility changed\r\n"+
            "    this.lastClipUpper=upper;\r\n"+
            "    this.lastClipLower=lower;\r\n"+
            "    this.lastClipLeft=left;\r\n"+
            "    this.lastClipRight=right;\r\n"+
            "    this.lastClipLeftPin=leftPin;\r\n"+
            "    this.lastClipRightPin=rightPin;\r\n"+
            "	this.callBack('clipzone',{top:upper,bottom:lower, left:left, right:right, leftPin:leftPin, rightPin:rightPin});\r\n"+
            "};\r\n"+
            "FastTablePortlet.prototype.onTableFetchData=function(i,j){\r\n"+
            "  return '<img src=\"rsc/wait.gif\"/>';\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.onHover=function(x,y){\r\n"+
            "    this.callBack('hover',{x:x,y:y});\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.rowVisibilityChanged=function(upper, lower, left, right, leftPin,rightPin){\r\n"+
            "	if (upper==this.lastClipUpper && lower==this.lastClipLower && left==this.lastClipLeft && right==this.lastClipRight && leftPin==this.lastClipLeftPin && rightPin==this.lastClipRightPin)\r\n"+
            "		return false;\r\n"+
            "	return true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.init=function(){\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.setSize=function(width,height){\r\n"+
            "	this.width=width;\r\n"+
            "	this.height=height;\r\n"+
            "	this.portlet.setSize(width,height);\r\n"+
            "	var newWidth = width;\r\n"+
            "	var newHeight = height - this.headerHeight;\r\n"+
            "	if(this.table.width != newWidth){\r\n"+
            "		this.table.widthChanged = true;\r\n"+
            "		this.table.width = newWidth;\r\n"+
            "	}\r\n"+
            "	if(this.table.height != newHeight){\r\n"+
            "		this.table.heightChanged = true;\r\n"+
            "		this.table.height = newHeight; \r\n"+
            "	}\r\n"+
            "	this.table.fireSizeChanged();\r\n"+
            "	var scrollSize = this.table.scrollPane.hscrollVisible ? this.table.scrollPane.scrollSize : 0;\r\n"+
            "	this.table.callback('tableSizeChanged',{\r\n"+
            "	  							left:this.table.scrollPane.getClipLeft(),\r\n"+
            "	  							top:this.table.scrollPane.getClipTop(),\r\n"+
            "	  							height:this.table.scrollPane.getClipHeight() - scrollSize,\r\n"+
            "	  							contentWidth:this.table.scrollPane.paneWidth,\r\n"+
            "	  							contentHeight:this.table.scrollPane.paneHeight\r\n"+
            "	  						   });\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.getFastTable=function(){\r\n"+
            "	return this.table;\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.setHover=function(x,y,value,xAlign,yAlign){\r\n"+
            "  this.table.setHover(x,y,value, xAlign, yAlign);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.clearHover=function(){\r\n"+
            "  this.table.clearHover();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTablePortlet.prototype.close=function(type, param){\r\n"+
            "  this.table.clearHover();\r\n"+
            "}\r\n"+
            "//########################\r\n"+
            "//##### Tree Portlet #####\r\n"+
            "\r\n"+
            "\r\n"+
            "function TreePortlet(portletId,parentId){\r\n"+
            "	  var that=this;\r\n"+
            "	  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "	  //this.nodes={};\r\n"+
            "	  //this.divElement.style.overflow='auto';\r\n"+
            "	  this.treeDiv=nw(\"div\");\r\n"+
            "	  this.tree=new FastTree(this.treeDiv);\r\n"+
            "	  this.tree.treePortlet=that;\r\n"+
            "	  //this.tree.onUserExpand=function(e){that.onUserExpand(e,type);};\r\n"+
            "	  th");
          out.print(
            "is.tree.callback=function(method,args){that.callBack(method,args);};\r\n"+
            "	  this.tree.onUserSelectedTree=function(e,action,uid,col){that.onUserSelectedTree(e,action,uid,col);};\r\n"+
            "	  this.tree.onUserSelected=function(e,activeRow,selectedRows, col, clicked){that.onUserSelected(e,activeRow,selectedRows,col, clicked);};\r\n"+
            "	  this.tree.onUpdateCellsEnd=function(table){that.onTreeUpdateCellsEnd(table);};\r\n"+
            "	  this.tree.onUserContextMenuItem=function(e,id){that.onUserContextMenuItem(e,id);};\r\n"+
            "      this.tree.onUserColumnResize=function(e,col,width){that.onUserColumnResize(e,col,width);};\r\n"+
            "      this.tree.onUserHeaderMenu=function(e,col){that.onUserHeaderMenu(e,col);};\r\n"+
            "      this.tree.onUserHeaderMenuItem=function(e,id,col){that.onUserHeaderMenuItem(e,id,col);};\r\n"+
            "      this.tree.onUserCopy=function(e,activeUid,col){that.onUserCopy(e,activeUid,col);};\r\n"+
            "	  this.titleDiv=nw(\"div\",\"table_title\");\r\n"+
            "	  this.searchInput=nw(\"input\",\"table_search_input\");\r\n"+
            "	  //MOBILE SUPPORT - enables enter key in mobile to be pressed instead of tabbing to next field for search\r\n"+
            "	  this.searchInput.setAttribute('enterkeyhint', 'go');\r\n"+
            "	  this.titleSpan=nw(\"span\",\"table_title_span\");\r\n"+
            "	  this.searchInputButton=nw(\"div\",\"table_search_button\");\r\n"+
            "	  makeEnterable(this.searchInput,this.searchInputButton);\r\n"+
            "	  this.expandInputButton=nw(\"div\",\"expand_all_button\");\r\n"+
            "//	  this.expandInputButton.innerHTML=\"<img src='rsc/btn_expand.gif'/>\";\r\n"+
            "	  this.contractInputButton=nw(\"div\",\"contract_all_button\");\r\n"+
            "//	  this.contractInputButton.innerHTML=\"<img src='rsc/btn_contract.gif'/>\";\r\n"+
            "//	  this.expandInputButton=nw(\"div\",\"table_search_button\");\r\n"+
            "//	  this.expandInputButton.innerHTML=\"<img src='rsc/btn_expand.gif'/>\";\r\n"+
            "//	  this.contractInputButton=nw(\"div\",\"table_search_button\");\r\n"+
            "//	  this.contractInputButton.innerHTML=\"<img src='rsc/btn_contract.gif'/>\";\r\n"+
            "	  this.headerDiv=nw(\"div\",\"table_search\");\r\n"+
            "	  this.headerDiv.appendChild(this.titleDiv);\r\n"+
            "	  this.headerDiv.appendChild(this.searchInput);\r\n"+
            "	  this.headerDiv.appendChild(this.searchInputButton);\r\n"+
            "	  this.headerDiv.appendChild(this.expandInputButton);\r\n"+
            "	  this.headerDiv.appendChild(this.contractInputButton);\r\n"+
            "	  this.searchInputButton.onclick=function(e){that.onUserSearch(e);}\r\n"+
            "	  this.expandInputButton.onclick=function(e){that.onUserExpandAll();}\r\n"+
            "	  this.contractInputButton.onclick=function(e){that.onUserContractAll();}\r\n"+
            "	  this.expandInputButton.style.right='200px';\r\n"+
            "	  this.contractInputButton.style.right='220px';\r\n"+
            "      this.divElement.style.background='white';\r\n"+
            "	  this.divElement.appendChild(this.treeDiv);\r\n"+
            "	  this.divElement.appendChild(this.headerDiv);\r\n"+
            "	  this.currentUpperRowVisible=-1;\r\n"+
            "	  this.currentLowerRowVisible=-1;\r\n"+
            "	  this.titleDiv.appendChild(this.titleSpan);\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.handleWheel=function(e){\r\n"+
            "		this.tree.scrollPane.handleWheel(e);\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.handleDblclick=function(e){\r\n"+
            "		//Checks if mouse is inside any of the visible cells \r\n"+
            "		if(isMouseInside(e,this.tree.scrollPane.DOM.paneElement, -0)){\r\n"+
            "			this.onUserDblclick(e, {action:\"callRelationship\"});\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	//this.treeElement.onkeydown=function(e){e.stopPropagation();that.onKeyDown(e);};\r\n"+
            "\r\n"+
            "	TreePortlet.prototype.handleKeydown=function(e, _target){\r\n"+
            "		if(e.target != null && e.target.isQuickColumnFilterElement == true){\r\n"+
            "			return;\r\n"+
            "		}\r\n"+
            "		if (_target.nodeName == \"INPUT\") {\r\n"+
            "			if (e.target == this.searchInput && e.key == \"Escape\") {\r\n"+
            "				this.setSearch(null);\r\n"+
            "				this.onUserSearch(e);\r\n"+
            "			}\r\n"+
            "			return;\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		var shiftKey = e.shiftKey;\r\n"+
            "		var ctrlKey = e.ctrlKey;\r\n"+
            "		var altKey = e.altKey;\r\n"+
            "		switch(e.key){	\r\n"+
            "//		case \" \":\r\n"+
            "//			this.table.jumpToNextUniqueCol(e);\r\n"+
            "//			break;\r\n"+
            "		case \"ArrowLeft\":\r\n"+
            "			this.tree.expandRow(e);\r\n"+
            "			break;\r\n"+
            "		case \"ArrowRight\":\r\n"+
            "			this.tree.expandRow(e);\r\n"+
            "			break;\r\n"+
            "		case \"ArrowDown\":\r\n"+
            "			this.tree.handleKeyDown(e, 1);\r\n"+
            "			break;\r\n"+
            "		case \"ArrowUp\":\r\n"+
            "			this.tree.handleKeyDown(e, -1);\r\n"+
            "			break;\r\n"+
            "		case \"PageDown\":\r\n"+
            "			this.tree.handleKeyDown(e,this.tree.visibleRowsCount);\r\n"+
            "			break;\r\n"+
            "		case \"PageUp\":\r\n"+
            "			this.tree.handleKeyDown(e,-this.tree.visibleRowsCount);\r\n"+
            "			break;\r\n"+
            "		case \"Escape\":\r\n"+
            "			this.tree.clearSelected(e);\r\n"+
            "			break;\r\n"+
            "		case \"c\":\r\n"+
            "			if(ctrlKey){\r\n"+
            "				this.onUserCopy(e, this.tree.activeRowUid, this.tree.getColumnAtPoint(MOUSE_POSITION_X));\r\n"+
            "			}\r\n"+
            "			break;\r\n"+
            "		case \"a\":\r\n"+
            "			if(ctrlKey){\r\n"+
            "				this.tree.selectAll(e);\r\n"+
            "			}\r\n"+
            "			break;\r\n"+
            "		default:\r\n"+
            "			break;	\r\n"+
            "		}	\r\n"+
            "	}\r\n"+
            "\r\n"+
            "    TreePortlet.prototype.setOptions=function(jsonData){\r\n"+
            "      this.tree.setOptions(jsonData);\r\n"+
            "      if (jsonData.searchBgStyle != '_bg=null') {\r\n"+
            "    	  applyStyle(this.headerDiv,jsonData.searchBgStyle);\r\n"+
            "    	  this.headerDiv.style.backgroundImage='none';\r\n"+
            "      } else {\r\n"+
            "    	  this.headerDiv.style.backgroundImage=null;\r\n"+
            "      }\r\n"+
            "      applyStyle(this.searchInput,jsonData.searchFieldBgStyle);\r\n"+
            "      applyStyle(this.searchInput,jsonData.searchFieldFgStyle);\r\n"+
            "      if (jsonData.fontFamily != null)\r\n"+
            "      	applyStyle(this.titleDiv,jsonData.fontFamily);\r\n"+
            "	  else\r\n"+
            "      	applyStyle(this.titleDiv,\"_fm=arial\");\r\n"+
            "\r\n"+
            "      this.searchFieldBackgroundInactiveColor = jsonData.searchFieldBgStyle;\r\n"+
            "      this.searchFieldFontInactiveColor = jsonData.searchFieldFgStyle;\r\n"+
            "	  this.searchFieldBackgroundActiveColor = jsonData.filteredBg;\r\n"+
            "	  this.searchFieldFontActiveColor = jsonData.filteredFont;\r\n"+
            "      this.searchBarHidden = jsonData.searchBarHidden;\r\n"+
            "      this.searchInput.style.borderColor=jsonData.searchFieldBorderColor;\r\n"+
            "      \r\n"+
            "      if (jsonData.searchBarHidden == \"true\"){\r\n"+
            "    	  this.headerDiv.style.visibility=\"hidden\";\r\n"+
            "      } else {\r\n"+
            "    	  this.headerDiv.style.visibility=\"visible\";\r\n"+
            "      }\r\n"+
            "      searchButtonsColor=jsonData.searchButtonsColor || '#ffffff';\r\n"+
            "  	  var searchButtonSvgStyle=searchButtonsColor+';stroke-width:14;stroke-miterlimit:10;\" cx=\"41.3\" cy=\"41.2\" r=\"24.7\"/> <line style=\"fill:none;stroke:'+searchButtonsColor;\r\n"+
            "  	  var expandButtonSvgStyle= searchButtonsColor+';stroke-width:18;stroke-miterlimit:10;\" x1=\"12\" y1=\"50\" x2=\"88\" y2=\"50\"/> <line style=\"fill:none;stroke:'+searchButtonsColor;\r\n"+
            "  	  var contractButtonSvgStyle= searchButtonsColor;\r\n"+
            "  	  searchButtonSvgStyle=searchButtonSvgStyle.replace(/#/g, \"%23\");\r\n"+
            "  	  expandButtonSvgStyle=expandButtonSvgStyle.replace(/#/g, \"%23\");\r\n"+
            "  	  contractButtonSvgStyle=contractButtonSvgStyle.replace(/#/g, \"%23\");\r\n"+
            "  	  this.searchInputButton.style.backgroundImage=(SEARCH_BUTTON_IMAGE_PREFIX+searchButtonSvgStyle+SEARCH_BUTTON_IMAGE_SUFFIX);\r\n"+
            "  	  this.expandInputButton.style.backgroundImage=(EXPAND_BUTTON_IMAGE_PREFIX+expandButtonSvgStyle+EXPAND_BUTTON_IMAGE_SUFFIX);\r\n"+
            "  	  this.contractInputButton.style.backgroundImage=(CONTRACT_BUTTON_IMAGE_PREFIX+contractButtonSvgStyle+CONTRACT_BUTTON_IMAGE_SUFFIX);\r\n"+
            "    }\r\n"+
            "\r\n"+
            "	TreePortlet.prototype.focusSearch=function(){\r\n"+
            "		this.searchInput.focus();\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.onUserDblclick=function(e, data){\r\n"+
            "		this.callBack('userDblclick', data);\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.onUserSearch=function(e){\r\n"+
            "	    //portletManager.showWait(\"Searching\");\r\n"+
            "		var expression=this.searchInput.value;\r\n"+
            "		if (expression) {\r\n"+
            "			 applyStyle(this.searchInput, this.searchFieldBackgroundActiveColor);\r\n"+
            "		     applyStyle(this.searchInput, this.searchFieldFontActiveColor);\r\n"+
            "		} else {\r\n"+
            "			 applyStyle(this.searchInput, this.searchFieldBackgroundInactiveColor);\r\n"+
            "		     applyStyle(this.searchInput, this.searchFieldFontInactiveColor);\r\n"+
            "		}\r\n"+
            "		//var rev = typeof(e.shiftKey) == 'boolean' && e.shiftKey;\r\n"+
            "	    this.callBack('search',{expression:expression,reverse:e.shiftKey});\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.setSearch=function(expression){\r\n"+
            "		this.searchInput.value=expression;\r\n"+
            "		if (expression) {\r\n"+
            "			 applyStyle(this.searchInput, this.searchFieldBackgroundActiveColor);\r\n"+
            "		     applyStyle(this.searchInput, this.searchFieldFontActiveColor);\r\n"+
            "		} else {\r\n"+
            "			 applyStyle(this.searchInput, this.searchFieldBackgroundInactiveColor);\r\n"+
            "		     applyStyle(this.searchInput, this.searchFieldFontInactiveColor);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.setTitle=function(title){\r\n"+
            "		this.titleSpan.innerHTML=title;\r\n"+
            "	};\r\n"+
            "	TreePortlet.prototype.onUserExpandAll=function(){\r\n"+
            "	    //portletManager.showWait(\"expand All\");\r\n"+
            "	    this.callBack('expandAll',{});\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.onUserContractAll=function(){\r\n"+
            "	    //portletManager.showWait(\"contract All\");\r\n"+
            "	    this.callBack('contractAll',{});\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.onUserContextMenuItem=function(e,id){\r\n"+
            "	    this.callBack('menuitem',{action:id});\r\n"+
            "	}\r\n"+
            "    TreePortlet.prototype.onUserHeaderMenu=function(e,col){\r\n"+
            "//    	console.log(e);\r\n"+
            "        this.callBack('showHeaderMenu',{col:col});\r\n"+
            "    };\r\n"+
            "    TreePortlet.prototype.onUserHeaderMenuItem=function(e,id,col){\r\n"+
            "        this.callBack('headerMenuitem',{action:id,col:col});\r\n"+
            "    };\r\n"+
            "    TreePortlet.prototype.onUserCopy=function(e,uid,col){\r\n"+
            "        this.callBack('copy',{uid:uid,col:col});\r\n"+
            "    };\r\n"+
            "\r\n"+
            "	TreePortlet.prototype.setSize=function(width,height){\r\n"+
            "	  this.portlet.setSize(width,height);\r\n"+
            "	  this.tree.setLocation(0,0,width,height);\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.onTreeUpdateCellsEnd=function(i,j){\r\n"+
            "		var upper=this.tree.getUpperRowVisible();\r\n"+
            "		var lower=this.tree.getLowerRowVisible();\r\n"+
            "//		console.log(this.currentLowerRowVisible, this.currentUpperRowVisible)\r\n"+
            "		if(lower>0 && (this.currentLowerRowVisible!=lower || this.currentUpperRowVisible!=upper)){\r\n"+
            "			var nwClipzone = {top:upper,bottom:lower};\r\n"+
            "			this.currentLowerRowVisible=lower;\r\n"+
            "			this.currentUpperRowVisible=upper;\r\n"+
            "	        this.callBack('clipzone',nwClipzone);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	TreePortlet.prototype.getTree=function(i,j){\r\n"+
            "	    return this.tree;\r\n"+
            "	}\r\n"+
            "\r\n"+
            "//	TreePortlet.prototype.onUserExpand=function(e,type){\r\n"+
            "//	  this.callBack('expand',{treeNodeUid:getMouseTarget(e).parentNode.uid});\r\n"+
            "//	}\r\n"+
            "	TreePortlet.prototype.onUserSelectedTree=function(e,action,uid,col){\r\n"+
            "	  var ctrlKey=e.ctrlKey;\r\n"+
            "	  var shiftKey=e.shiftKey;\r\n"+
            "	  var button=getMouseButton(e);\r\n"+
            "	  this.callBack(action,{ctrl:ctrlKey ? true : false, shift:shiftKey ? true : false,button:button,treeNodeUid:uid,col:col});//getMouseTarget(e).parentNode.uid});\r\n"+
            "");
          out.print(
            "	}\r\n"+
            "\r\n"+
            "	TreePortlet.prototype.onUserSelected=function(e,activeRow,selectedRows, col, clicked){\r\n"+
            "	    this.callBack('userSelect',{button: getMouseButton(e), activeRow:activeRow,selectedRows:selectedRows, col:col, clicked:clicked});\r\n"+
            "	};\r\n"+
            "\r\n"+
            "    TreePortlet.prototype.onUserColumnResize=function(e,col,width){\r\n"+
            "        this.callBack('columnWidth',{columnIndex:col,width:width});\r\n"+
            "    };\r\n"+
            "    \r\n"+
            "    //setAutocomplete\r\n"+
            "    TreePortlet.prototype.setQuickFilterAutocomplete=function(col, vals){\r\n"+
            "    	this.tree.setQuickFilterAutocomplete(col,vals);\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "    /*\r\n"+
            "    // Might not be needed instead use init\r\n"+
            "    TreePortlet.prototype.setQuickFilterSearchText=function(col, expression){\r\n"+
            "    	\r\n"+
            "    }\r\n"+
            "    TreePortlet.prototype.callGetQuickFilterOptions=function(colLocation, filterValue){\r\n"+
            "     	this.callback('getColumnFilterOptions',{pos:col.colLocation,val:filterValue});\r\n"+
            "    }\r\n"+
            "    TreePortlet.prototype.callSetQuickFilterValue=function(colLocation, filterValue){\r\n"+
            "     	this.callback('columnFilter',{pos:col.colLocation,val:filterValue});\r\n"+
            "    }\r\n"+
            "    */\r\n"+
            "    \r\n"+
            "\r\n"+
            "////##########################\r\n"+
            "////##### Select Portlet #####\r\n"+
            "//\r\n"+
            "//function SelectPortlet(portletId,parentId){\r\n"+
            "//  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "//  this.selectElement=nw('select');\r\n"+
            "//  this.select=new Select(this.selectElement);\r\n"+
            "//  this.divElement.appendChild(this.selectElement);\r\n"+
            "//};\r\n"+
            "//\r\n"+
            "//SelectPortlet.prototype.setValue=function(value){\r\n"+
            "//  this.select.setSelectedValueDelimited(value,',');\r\n"+
            "//};\r\n"+
            "//\r\n"+
            "//SelectPortlet.prototype.clear=function(){\r\n"+
            "//  this.select.clear();\r\n"+
            "//};\r\n"+
            "//\r\n"+
            "//SelectPortlet.prototype.addOption=function(value,text,isSelected){\r\n"+
            "//  this.select.addOption(value,text,isSelected);\r\n"+
            "//};\r\n"+
            "//\r\n"+
            "//SelectPortlet.prototype.init=function(name,multiple,size){\r\n"+
            "//  this.selectElement.name=name;\r\n"+
            "//  if(multiple)\r\n"+
            "//    this.selectElement.multiple='multiple';\r\n"+
            "//  else\r\n"+
            "//    this.select.multiple='';\r\n"+
            "//  this.selectElement.size=size;\r\n"+
            "//};\r\n"+
            "\r\n"+
            "\r\n"+
            "//############################\r\n"+
            "//##### Piechart Portlet #####\r\n"+
            "\r\n"+
            "function PieChartPortlet(portletId,parentId){\r\n"+
            "  \r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "};\r\n"+
            "\r\n"+
            "PieChartPortlet.prototype.updateData=function(){\r\n"+
            "	this.data=[];\r\n"+
            "	for(var i=0;i<arguments.length;i+=2){\r\n"+
            "	  var label=arguments[i];\r\n"+
            "	  var val=arguments[i+1];\r\n"+
            "	  this.data.push({value:val , label : label});\r\n"+
            "	}\r\n"+
            "	this.drawChart();\r\n"+
            "};\r\n"+
            "\r\n"+
            "PieChartPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.drawChart();\r\n"+
            "};\r\n"+
            "\r\n"+
            "PieChartPortlet.prototype.drawChart=function(){\r\n"+
            "  if(this.data==null)\r\n"+
            "    return;\r\n"+
            "  var w=this.portlet.location.width;\r\n"+
            "  var h=this.portlet.location.height;\r\n"+
            "  var r = Math.min(w,h)/2, color = d3.scale.category20c();\r\n"+
            "  var that=this;\r\n"+
            "  removeAllChildren(this.divElement);\r\n"+
            "  var vis = d3.select(this.divElement).append(\"svg:svg\").data([this.data]).attr(\"width\", w).attr(\"height\", h).append(\"svg:g\").attr(\"transform\", \"translate(\" + r + \",\" + r + \")\");    \r\n"+
            "  var arc = d3.svg.arc().outerRadius(r);\r\n"+
            "  var pie = d3.layout.pie().value(function(d) { return d.value; });    \r\n"+
            "  var arcs = vis.selectAll(\"g.slice\").data(pie).enter().append(\"svg:g\").attr(\"class\", \"slice\");    \r\n"+
            "  arcs.append(\"svg:path\") .attr(\"fill\", function(d, i) { return color(i); } ) .attr(\"d\", arc);                                    \r\n"+
            "  arcs.append(\"svg:text\").attr(\"transform\", function(d) {                    \r\n"+
            "    d.innerRadius = 0;\r\n"+
            "    d.outerRadius = r;\r\n"+
            "    return \"translate(\" + arc.centroid(d) + \")\";        \r\n"+
            "  }).attr(\"text-anchor\", \"middle\").text(function(d, i) { return that.data[i].label+\" - \"+that.data[i].value; });        \r\n"+
            "};\r\n"+
            "\r\n"+
            "//#############################\r\n"+
            "//##### Linechart Portlet #####\r\n"+
            "\r\n"+
            "function LineChartPortlet(portletId,parentId){\r\n"+
            "\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "}\r\n"+
            "\r\n"+
            "LineChartPortlet.prototype.updateData=function(data){\r\n"+
            "	this.data=data;\r\n"+
            "	this.drawChart();\r\n"+
            "};\r\n"+
            "\r\n"+
            "LineChartPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.drawChart();\r\n"+
            "};\r\n"+
            "\r\n"+
            "LineChartPortlet.prototype.drawChart=function(){\r\n"+
            "  if(this.data==null)\r\n"+
            "    return;\r\n"+
            "  var width=this.portlet.location.width;\r\n"+
            "  var height=this.portlet.location.height;\r\n"+
            " \r\n"+
            "  removeAllChildren(this.divElement);\r\n"+
            "  \r\n"+
            "  var data1=this.data;\r\n"+
            "             //{year:30,p99:32,p95:50},\r\n"+
            "             //{year:31,p99:30,p95:51},\r\n"+
            "             //{year:32,p99:26,p95:52},\r\n"+
            "             //{year:33,p99:26,p95:53},\r\n"+
            "             //{year:34,p99:27,p95:53},\r\n"+
            "             //{year:35,p99:28,p95:53},\r\n"+
            "             //{year:36,p99:22,p95:53},\r\n"+
            "             //{year:37,p99:23,p95:53},\r\n"+
            "             //{year:38,p99:22,p95:53},\r\n"+
            "             //{year:39,p99:24,p95:53},\r\n"+
            "             //{year:40,p99:25,p95:53},\r\n"+
            "             //{year:41,p99:26,p95:53},\r\n"+
            "             //{year:42,p99:24,p95:53},\r\n"+
            "             //{year:43,p99:22,p95:53},\r\n"+
            "             //{year:44,p99:21,p95:53},\r\n"+
            "             //{year:45,p99:19,p95:53},\r\n"+
            "             //];\r\n"+
            "\r\n"+
            "              /* Read CSV file: first row =>  year,top1,top5  */\r\n"+
            "              var label_array = new Array(),val_array1 = new Array();\r\n"+
            "\r\n"+
            "              var sampsize = data1.length;\r\n"+
            "              var maxval=0;\r\n"+
            "\r\n"+
            "              for (var i=0; i < sampsize; i++) {\r\n"+
            "                 label_array[i] = parseInt(data1[i].x);\r\n"+
            "                 val_array1[i] = { x: label_array[i], y: parseFloat(data1[i].y1)};//, z: parseFloat(data1[i].p95) };\r\n"+
            "                 //maxval = Math.max(maxval, parseFloat(data1[i].y1), parseFloat(data1[i].) );\r\n"+
            "                 maxval = Math.max(maxval, parseFloat(data1[i].y1));\r\n"+
            "               }\r\n"+
            "\r\n"+
            "               maxval = (1 + Math.floor(maxval / 10)) * 10;\r\n"+
            "\r\n"+
            "             var p=0;\r\n"+
            "             var w = width-p*2;\r\n"+
            "             var h = height-p*2;\r\n"+
            "             var x = d3.scale.linear().domain([ label_array[0], label_array[sampsize-1] ]).range([0, w]);\r\n"+
            "             var y = d3.scale.linear().domain([0, maxval]).range([h, 0]);\r\n"+
            "\r\n"+
            "             var vis = d3.select(this.divElement).data([val_array1]).append(\"svg:svg\").attr(\"width\", width).attr(\"height\", height)\r\n"+
            "             .append(\"svg:g\").attr(\"transform\", \"translate(\" + p + \",\" + p + \")\");\r\n"+
            "\r\n"+
            "             var xrules = vis.selectAll().data(x.ticks(30)).enter().append(\"svg:g\").attr(\"class\", \"linechart_line\");\r\n"+
            "             var yrules = vis.selectAll().data(y.ticks(25)).enter().append(\"svg:g\").attr(\"class\", \"linechart_line\");\r\n"+
            "\r\n"+
            "             // Draw grid lines\r\n"+
            "             xrules.append(\"svg:line\").attr(\"x1\", x).attr(\"x2\", x).attr(\"y1\", 0).attr(\"y2\", h - 1);\r\n"+
            "             yrules.append(\"svg:line\").attr(\"y1\", y).attr(\"y2\", y).attr(\"x1\", 0).attr(\"x2\", w - 1);\r\n"+
            " \r\n"+
            "             vis.append(\"svg:path\") .attr(\"class\", \"line\") .attr(\"fill\", \"none\") .attr(\"stroke\", \"maroon\") .attr(\"stroke-width\", 2)\r\n"+
            "             .attr(\"d\", d3.svg.line() .x(function(d) { return x(d.x); }) .y(function(d) { return y(d.y); }));\r\n"+
            "\r\n"+
            "         vis.selectAll() .data(val_array1) .enter().append(\"svg:circle\") .attr(\"class\", \"line\") .attr(\"fill\", \"maroon\" )\r\n"+
            "             .attr(\"cx\", function(d) { return x(d.x); }) .attr(\"cy\", function(d) { return y(d.y); }) .attr(\"r\", 1);  \r\n"+
            "};\r\n"+
            "\r\n"+
            "//###########################\r\n"+
            "//##### Treemap Portlet #####\r\n"+
            "\r\n"+
            "function TreemapPortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.chartDiv=nw('div');\r\n"+
            "  this.divElement.appendChild(this.chartDiv);\r\n"+
            "  var that=this;\r\n"+
            "  this.treemap=new TreeMap(this.chartDiv);\r\n"+
            "  this.treemap.onUserClick=function(id,shift,ctrl,btn){that.onUserClick(id,shift,ctrl,btn);};\r\n"+
            "  //MOBILE SUPPORT - heatmap touch support\r\n"+
            "  this.treemap.onTouchHold=function(id,shift,ctrl,btn){that.onTouchHold(id,shift,ctrl,btn);};\r\n"+
            "  this.treemap.onUserContextMenuItem=function(e,id,nodeId){that.onUserContextMenuItem(e,id,nodeId);};\r\n"+
            "  this.treemap.onUserGradient=function(colors){that.onUserGradient(colors);};\r\n"+
            "  this.treemap.onHover=function(x,y,id){that.onHover(x,y,id);};\r\n"+
            "  //this.treemap.onclick=function(x,y){that.onChartClicked(x,y)};\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.handleKeydown=function(e){\r\n"+
            "    this.treemap.handleKeydown(e);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TreemapPortlet.prototype.setOwningWindow=function(win){\r\n"+
            "	this.owningWindow=win;\r\n"+
            "	this.treemap.owningWindow=win;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TreemapPortlet.prototype.updateData=function(data){\r\n"+
            "	this.drawMap();\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.setHover=function(x,y,nid,value){\r\n"+
            "  this.treemap.setHover(x,y,nid,value);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TreemapPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.treemap.setSize(width,height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "TreemapPortlet.prototype.clearData=function(){\r\n"+
            "  this.treemap.clearData();\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.addParents=function(data){\r\n"+
            "  this.treemap.addParents(data);\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.addNodes=function(data){\r\n"+
            "  this.treemap.addNodes(data);\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.removeNodes=function(data){\r\n"+
            "  this.treemap.removeNodes(data);\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.setDepthStyles=function(data){\r\n"+
            "  this.treemap.setDepthStyles(data);\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.removeParents=function(data){\r\n"+
            "  this.treemap.removeParents(data);\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.addChildren=function(data){\r\n"+
            "  this.treemap.addChildren(data);\r\n"+
            "};\r\n"+
            "\r\n"+
            "TreemapPortlet.prototype.removeChildren=function(data){\r\n"+
            "this.treemap.removeChildren(data);\r\n"+
            "};\r\n"+
            "\r\n"+
            "TreemapPortlet.prototype.showContextMenu=function(data){\r\n"+
            "  this.treemap.showContextMenu(data);\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.setHeatColors=function(data){\r\n"+
            "  this.treemap.setHeatColors(data);\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.repaint=function(){\r\n"+
            "  this.treemap.repaint();\r\n"+
            "};\r\n"+
            "TreemapPortlet.prototype.setOptions=function(jsonData){\r\n"+
            "  this.treemap.setOptions(jsonData);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TreemapPortlet.prototype.onHover=function(x,y,id){\r\n"+
            "    this.callBack('hover',{nid:id,x:x,y:y});\r\n"+
            "}\r\n"+
            "TreemapPortlet.prototype.onUserClick=function(id,shift,ctrl,btn){\r\n"+
            "    this.callBack('click',{nid:id,shift:shift,ctrl:ctrl,btn:btn});\r\n"+
            "}\r\n"+
            "//MOBILE SUPPORT - touch hold for heatmap for context menu\r\n"+
            "TreemapPortlet.prototype.onTouchHold=function(id,shift,ctrl,btn){\r\n"+
            "    this.callBack(");
          out.print(
            "'onTouch',{nid:id,shift:shift,ctrl:ctrl,btn:btn});\r\n"+
            "}\r\n"+
            "//MOBILE SUPPORT - cancel zoom for heatmap\r\n"+
            "TreemapPortlet.prototype.cancelZoom=function(){\r\n"+
            "  this.treemap.cancelZoom();\r\n"+
            "};\r\n"+
            "\r\n"+
            "TreemapPortlet.prototype.onUserContextMenuItem=function(e,id,nodeId){\r\n"+
            "    this.callBack('menuitem',{action:id,nid:nodeId});\r\n"+
            "}\r\n"+
            "TreemapPortlet.prototype.onUserGradient=function(colors){\r\n"+
            "	var vals={stopsCount:colors.length};\r\n"+
            "	for(var i in colors){\r\n"+
            "		vals['stop'+i]=colors[i][0];\r\n"+
            "		vals['color'+i]=colors[i][4];\r\n"+
            "	}\r\n"+
            "    this.callBack('colors',vals);\r\n"+
            "}\r\n"+
            "TreemapPortlet.prototype.handleNodeStyleChange=function(jsonData){\r\n"+
            "	this.treemap.handleNodeStyleChange(jsonData);\r\n"+
            "}\r\n"+
            " \r\n"+
            "TreemapPortlet.prototype.close=function(type,param){\r\n"+
            "	this.treemap.clearHover();\r\n"+
            "}\r\n"+
            " \r\n"+
            "//#########################\r\n"+
            "//##### Image Portlet #####\r\n"+
            "\r\n"+
            "function ImagePortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.map=nw('map');\r\n"+
            "  this.map.name='map'+portletId;\r\n"+
            "  this.imageElement=nw('img');\r\n"+
            "  this.imageElement.useMap='#'+this.map.name;\r\n"+
            "  this.imageElement.onclick=function(e){that.onImageClicked(e);};\r\n"+
            "  this.divElement.appendChild(this.imageElement);\r\n"+
            "  this.divElement.appendChild(this.map);\r\n"+
            "};\r\n"+
            "\r\n"+
            "ImagePortlet.prototype.onImageClicked=function(e){\r\n"+
            "  var point=getMousePoint(e);\r\n"+
            "  this.callBack('click',{x:point.x,y:point.y});\r\n"+
            "};\r\n"+
            "\r\n"+
            "ImagePortlet.prototype.setImageGenerator=function(value){\r\n"+
            "  this.input.value=value;\r\n"+
            "};\r\n"+
            "\r\n"+
            "ImagePortlet.prototype.init=function(imageGeneratorId,uid){\r\n"+
            "  this.imageGeneratorId=imageGeneratorId;\r\n"+
            "  this.uid=uid;\r\n"+
            "};\r\n"+
            "ImagePortlet.prototype.imageChanged=function(uid){\r\n"+
            "  this.uid=uid;\r\n"+
            "  this.setSize(this.location.width,location.height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "ImagePortlet.prototype.clearTooltips=function(){\r\n"+
            "  while(this.map.childNodes.length>0)\r\n"+
            "    this.map.removeChild(this.map.firstChild);\r\n"+
            "};\r\n"+
            "\r\n"+
            "ImagePortlet.prototype.addTooltip=function(left,top,width,height,text){\r\n"+
            "  var area=nw('area');\r\n"+
            "  area.shape='rect';\r\n"+
            "  area.coords=left+','+top+','+(left+width)+','+(top+height);\r\n"+
            "  area.title=text;\r\n"+
            "  area.href='#';\r\n"+
            "  this.map.appendChild(area);\r\n"+
            "};\r\n"+
            "\r\n"+
            "ImagePortlet.prototype.sizeSize=function(width,height){\r\n"+
            "  this.portlet.sizeSize(width,height);\r\n"+
            "  this.imageElement.src=this.imageUrl+'?w='+width+'&h='+height+'&id='+this.imageGeneratorId+'&webWindowId='+window.windowId+'&uid='+this.uid;\r\n"+
            "};\r\n"+
            "\r\n"+
            "//#########################\r\n"+
            "//##### Blank Portlet #####\r\n"+
            "\r\n"+
            "function BlankPortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.back=nw('div');\r\n"+
            "  this.back.className='portlet_blank';\r\n"+
            "  this.back.onclick=function(e){that.onAddButton(e);};\r\n"+
            "  this.addButton=nw('div');\r\n"+
            "  this.addButton.className='portlet_blank_add';\r\n"+
            "  this.divElement.appendChild(this.back);\r\n"+
            "  this.back.appendChild(this.addButton);\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "BlankPortlet.prototype.onAddButton=function(e){\r\n"+
            "  this.callBack('showAddPortletDialog',{});\r\n"+
            "};\r\n"+
            "\r\n"+
            "BlankPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.addButton.style.top=toPx((height-100)/2);\r\n"+
            "  this.addButton.style.left=toPx((width-100)/2);\r\n"+
            "};\r\n"+
            "\r\n"+
            "BlankPortlet.prototype.populatePortletMenu=function(menu){\r\n"+
            "  var that=this;\r\n"+
            "  menu.addItem('<img src=\"rsc/asc.gif\"> ");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"Delete this Component");
          out.print(
            "',null,function(e){that.portlet.onUserDelete(e);});\r\n"+
            "  menu.addItem('<img src=\"rsc/autoasc.gif\"> ");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"Add a new component here");
          out.print(
            "',null,function(e){that.onAddButton(e);});\r\n"+
            "};\r\n"+
            "\r\n"+
            "function ChartPortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.chartDiv=nw('div');\r\n"+
            "  this.divElement.appendChild(this.chartDiv);\r\n"+
            "  var that=this;\r\n"+
            "  this.chart=new FastChart(this.chartDiv);\r\n"+
            "  this.chart.onclick=function(x,y){that.onChartClicked(x,y)};\r\n"+
            "};\r\n"+
            "ChartPortlet.prototype.onChartClicked=function(x,y){\r\n"+
            "  this.callBack('userClick',{x:x,y:y});\r\n"+
            "}\r\n"+
            "\r\n"+
            "ChartPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.chart.setLocation(0,0,width,height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "ChartPortlet.prototype.setData=function(type,jsonData,options){\r\n"+
            "	this.chart.setData(type,jsonData,options);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function ThreeDeePortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.chartDiv=nw('div');\r\n"+
            "  this.divElement.appendChild(this.chartDiv);\r\n"+
            "  var that=this;\r\n"+
            "  this.surface=new Surface(this.chartDiv);\r\n"+
            "  this.surface.onUserChangedPerspective=function(rotX,rotY,roxZ,zoom,fov){that.onUserChangedPerspective(rotX,rotY,roxZ,zoom,fov);};\r\n"+
            "  this.surface.onSelectionChanged=function(ids,toggleSelect,addSelect,clear){that.onSelectionChanged(ids,toggleSelect,addSelect,clear);};\r\n"+
            "  this.surface.onShowContextMenu=function(){that.onShowContextMenu();};\r\n"+
            "  this.surface.onHover=function(id,x,y){that.onHover(id,x,y);};\r\n"+
            "  onOptionsChanged=function(){that.onOptionChanged()};\r\n"+
            "  //this.surface.onclick=function(x,y){that.onChartClicked(x,y)};\r\n"+
            "};\r\n"+
            "ThreeDeePortlet.prototype.onChartClicked=function(x,y){\r\n"+
            "}\r\n"+
            "ThreeDeePortlet.prototype.onHover=function(id,x,y){\r\n"+
            "    this.callBack('onHover',{mouseX:x,mouseY:y,polyId:id});\r\n"+
            "}\r\n"+
            "ThreeDeePortlet.prototype.setHover=function(x,y,sel,name,xAlign,yAlign){\r\n"+
            "  this.surface.setHover(x,y,sel,name,xAlign,yAlign);\r\n"+
            "}\r\n"+
            "ThreeDeePortlet.prototype.onUserChangedPerspective=function(rx,ry,rz,zm,fov){\r\n"+
            "    this.callBack('onPerspective',{rx:rx,ry:ry,rz:rz,zm:zm,fov:fov});\r\n"+
            "};\r\n"+
            "ThreeDeePortlet.prototype.onSelectionChanged=function(ids,toggleSelect,addSelect,clear){\r\n"+
            "    this.callBack('onSelection',{ids:ids.join(','),action:clear ? 'clear' : (toggleSelect ? 'toggle' : addSelect ? 'add' : 'replace')});\r\n"+
            "};\r\n"+
            "ThreeDeePortlet.prototype.onShowContextMenu=function(){\r\n"+
            "    this.callBack('showContextMenu',{});\r\n"+
            "};\r\n"+
            "\r\n"+
            "ThreeDeePortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.surface.setSize(width,height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "ThreeDeePortlet.prototype.setData=function(jsonData){\r\n"+
            "	this.surface.setData(jsonData);\r\n"+
            "}\r\n"+
            "ThreeDeePortlet.prototype.setOptions=function(jsonData){\r\n"+
            "	this.surface.setOptions(jsonData);\r\n"+
            "}\r\n"+
            "ThreeDeePortlet.prototype.repaintIfNeeded=function(){\r\n"+
            "	this.surface.repaintIfNeeded();\r\n"+
            "}\r\n"+
            "ThreeDeePortlet.prototype.close=function(){\r\n"+
            "	this.surface.close();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "function TilesPortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.tilesDiv=nw('div');\r\n"+
            "  this.divElement.appendChild(this.tilesDiv);\r\n"+
            "  var that=this;\r\n"+
            "  this.tiles=new TilesPanel(this.tilesDiv);\r\n"+
            "  this.tiles.onClipzoneChanged=function(top,bottom){that.onClipzoneChanged(top, bottom);};\r\n"+
            "  this.tiles.onUserSelected=function(active,selected){that.onUserSelected(active, selected);};\r\n"+
            "  this.tiles.onclick=function(x,y){that.onTileClicked(x,y)};\r\n"+
            "  this.tiles.onUserContextMenu=function(e){that.onUserContextMenu(e);};\r\n"+
            "  this.tiles.onUserDblClick=function(e,pos){that.onUserDblClick(e,pos);};\r\n"+
            "  this.tiles.onUserContextMenuItem=function(e,id,nodeId){that.onUserContextMenuItem(e,id,nodeId);};\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.handleWheel=function(e){\r\n"+
            "	var delta;\r\n"+
            "	if(e.deltaMode == WheelEvent.DOM_DELTA_PIXEL)\r\n"+
            "		delta = e.deltaY/-100;\r\n"+
            "	else\r\n"+
            "		delta = e.deltaY/-1\r\n"+
            "	\r\n"+
            "	this.tiles.onMouseWheel(e,delta);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPortlet.prototype.onTileClicked=function(x,y){\r\n"+
            "  this.callBack('userClick',{x:x,y:y});\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.tiles.setLocation(0,0,width,height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "TilesPortlet.prototype.setData=function(type,jsonData,options){\r\n"+
            "	this.tiles.setData(type,jsonData,options);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPortlet.prototype.clearData=function(){\r\n"+
            "  this.tiles.clearData();\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.initTiles=function(multiselect){\r\n"+
            "  this.tiles.initTiles(multiselect);\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.addChildren=function(data){\r\n"+
            "  this.tiles.addChildren(data);\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.setOptions=function(options){\r\n"+
            "  this.tiles.setOptions(options);\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.repaint=function(){\r\n"+
            "  this.tiles.repaint();\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.setTilesCount=function(count){\r\n"+
            "  this.tiles.setTilesCount(count);\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.showContextMenu=function(json){\r\n"+
            "  this.tiles.showContextMenu(json);\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.onUserContextMenuItem=function(e,id,nodeId){\r\n"+
            "    this.callBack('menuitem',{action:id,nid:nodeId});\r\n"+
            "}\r\n"+
            "TilesPortlet.prototype.onUserContextMenu=function(e,col){\r\n"+
            "    this.callBack('showMenu',{});\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.onUserDblClick=function(e,pos){\r\n"+
            "    this.callBack('dblclick',{pos:pos});\r\n"+
            "};\r\n"+
            "\r\n"+
            "TilesPortlet.prototype.setActiveTilePos=function(pos){\r\n"+
            "  this.tiles.setActiveTilePos(pos);\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.setSelectedTiles=function(pos){\r\n"+
            "  this.tiles.setSelectedTiles(pos);\r\n"+
            "};\r\n"+
            "TilesPortlet.prototype.onClipzoneChanged=function(top,bottom){\r\n"+
            "  this.callBack('clipzone',{top:top,bottom:bottom});\r\n"+
            "};\r\n"+
            "\r\n"+
            "TilesPortlet.prototype.onUserSelected=function(active,selected){\r\n"+
            "  this.callBack('select',{active:active,selected:selected});\r\n"+
            "};\r\n"+
            "\r\n"+
            "function TextPortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.textDiv=nw('div');\r\n"+
            "  this.divElement.appendChild(this.textDiv);\r\n"+
            "  var that=this;\r\n"+
            "  this.text=new FastText(this.textDiv);\r\n"+
            "  //this.graph.onNodeMoved=function(id,x,y){that.onNodeMoved(id,x,y);};\r\n"+
            "  this.text.onClip=function(top,bot){that.onClip(top,bot);};\r\n"+
            "  this.text.onUserSelected=function(selected){that.onUserSelected(selected);};\r\n"+
            "  this.text.onUserContextMenu=function(selected){that.onUserContextMenu(selected);};\r\n"+
            "  this.text.onUserContextMenuItem=function(e,id){that.onUserContextMenuItem(e,id);};\r\n"+
            "  this.text.onColumnsVisible=function(cols){that.onColumnsVisible(cols);};\r\n"+
            "};\r\n"+
            "TextPortlet.prototype.handleWheel=function(e){\r\n"+
            "	this.text.scrollPane.handleWheel(e);\r\n"+
            "}\r\n"+
            "TextPortlet.prototype.getText=function(){\r\n"+
            "	return this.text;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextPortlet.prototype.onClip=function(top,bot){\r\n"+
            "    this.callBack('clip',{top:top,bot:bot});\r\n"+
            "}\r\n"+
            "TextPortlet.prototype.init=function(linesCount,rowHeight,maxChars,labelWidth,labelStyle,style){\r\n"+
            "	this.text.init(linesCount,rowHeight,maxChars,labelWidth,labelStyle,style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.text.setLocation(0,0,width,height);\r\n"+
            "};\r\n"+
            "TextPortlet.prototype.setTopLine=function(line){\r\n"+
            "	this.text.setTopLine(line);\r\n"+
            "}\r\n"+
            "TextPortlet.prototype.setSelectedLines=function(lines){\r\n"+
            "	this.text.setSelectedLines(lines);\r\n"+
            "}\r\n"+
            "TextPortlet.prototype.setData=function(data){\r\n"+
            "  this.text.setData(data);\r\n"+
            "};\r\n"+
            "TextPortlet.prototype.setScrollTicks=function(ticks){\r\n"+
            "	this.text.setScrollTicks(ticks);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextPortlet.prototype.onUserSelected=function(selected){\r\n"+
            "  this.callBack('select',{selected:selected});\r\n"+
            "};\r\n"+
            "TextPortlet.prototype.onUserContextMenu=function(e,col){\r\n"+
            "    this.callBack('showMenu',{});\r\n"+
            "};\r\n"+
            "TextPortlet.prototype.showContextMenu=function(data){\r\n"+
            "  this.text.showContextMenu(data);\r\n"+
            "};\r\n"+
            "TextPortlet.prototype.onUserContextMenuItem=function(e,id){\r\n"+
            "    this.callBack('menuitem',{action:id});\r\n"+
            "}\r\n"+
            "TextPortlet.prototype.onColumnsVisible=function(cols){\r\n"+
            "    this.callBack('colsVisible',{cols:cols});\r\n"+
            "}\r\n"+
            "\r\n"+
            "function DropDownMenuPortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.menuDiv=nw('div');\r\n"+
            "  this.divElement.appendChild(this.menuDiv);\r\n"+
            "  this.menuBar=new MenuBar(this.menuDiv);\r\n"+
            "  this.menuBar.onUserOverMenu=function(id){that.onUserOverMenu(id);};\r\n"+
            "  this.menuBar.onUserClickedMenuItem=function(id){that.onUserClickedMenuItem(id);};\r\n"+
            "  var that=this;\r\n"+
            "};\r\n"+
            "\r\n"+
            "DropDownMenuPortlet.prototype.setMenus=function(menus){\r\n"+
            "	this.menuBar.setMenus(menus);\r\n"+
            "}\r\n"+
            "DropDownMenuPortlet.prototype.setOptions=function(options){\r\n"+
            "	this.menuBar.setOptions(options);\r\n"+
            "}\r\n"+
            "DropDownMenuPortlet.prototype.showMenu=function(id,menu){\r\n"+
            "	this.menuBar.showMenu(id,menu);\r\n"+
            "}\r\n"+
            "DropDownMenuPortlet.prototype.onUserOverMenu=function(id){\r\n"+
            "    this.callBack('showmenu',{id:id});\r\n"+
            "}\r\n"+
            "DropDownMenuPortlet.prototype.onUserClickedMenuItem=function(id){\r\n"+
            "    this.callBack('menuitem',{id:id});\r\n"+
            "}\r\n"+
            "DropDownMenuPortlet.prototype.setCssStyle=function(cssStyle){\r\n"+
            "	this.menuBar.setCssStyle(cssStyle);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function GraphPortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.graphDiv=nw('div');\r\n"+
            "  this.divElement.appendChild(this.graphDiv);\r\n"+
            "  var that=this;\r\n"+
            "  this.graph=new GraphPanel(this.graphDiv);\r\n"+
            "  this.graph.onNodeMoved=function(id,x,y){that.onNodeMoved(id,x,y);};\r\n"+
            "  this.graph.onUserClick=function(id,shift,ctrl,button){that.onUserClick(id,shift,ctrl,button);};\r\n"+
            "  this.graph.onUserSelect=function(x,y,w,h,shift,ctrl,button){that.onUserSelect(x,y,w,h,shift,ctrl,button);};\r\n"+
            "  this.graph.onUserContextMenuItem=function(e,id){that.onUserContextMenuItem(e,id);};\r\n"+
            "  this.graph.onUserDblClick=function(id){that.onUserDblClick(id);};\r\n"+
            "  this.graph.onUserDirectionKey=function(e){that.callBack(\"graphKeyDown\",{key:e.keyCode, ctrl:e.ctrlKey})};\r\n"+
            "};\r\n"+
            "\r\n"+
            "GraphPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.graph.setSize(width,height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "GraphPortlet.prototype.addNodes=function(data){\r\n"+
            "	this.graph.addNodes(data);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPortlet.prototype.selectNodes=function(data){\r\n"+
            "	this.graph.selectNodes(data);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPortlet.prototype.ensureVisibleNode=function(id){\r\n"+
            "	this.graph.ensureVisibleNode(id);\r\n"+
            "};\r\n"+
            "\r\n"+
            "GraphPortlet.prototype.ensureVisibleNodes=function(nodeIds){\r\n"+
            "	t");
          out.print(
            "his.graph.ensureVisibleNodes(nodeIds);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPortlet.prototype.addEdges=function(data){\r\n"+
            "	this.graph.addEdges(data);\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.clearData=function(){\r\n"+
            "	this.graph.clearData();\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.setGridSnap=function(grid,snap){\r\n"+
            "	this.graph.setGridSnap(grid,snap);\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.clearEdges=function(){\r\n"+
            "	this.graph.clearEdges();\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.repaint=function(){\r\n"+
            "	this.graph.repaint();\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.removeEdges=function(json){\r\n"+
            "	this.graph.removeEdges(json);\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.removeNodes=function(json){\r\n"+
            "	this.graph.removeNodes(json);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPortlet.prototype.onNodeMoved=function(id,x,y){\r\n"+
            "    this.callBack('moveNode',{id:id,x:x,y:y});\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.onUserClick=function(id,shift,ctrl,button){\r\n"+
            "    this.callBack('click',{button:button,id:id,shift:shift,ctrl:ctrl});\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.onUserSelect=function(x,y,w,h,shift,ctrl,button){\r\n"+
            "    this.callBack('select',{x:x,y:y,w:w,h:h,shift:shift,ctrl:ctrl,button:button});\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.showContextMenu=function(json){\r\n"+
            "  this.graph.showContextMenu(json);\r\n"+
            "};\r\n"+
            "GraphPortlet.prototype.onUserContextMenuItem=function(e,id){\r\n"+
            "    this.callBack('menuitem',{action:id});\r\n"+
            "}\r\n"+
            "GraphPortlet.prototype.onUserDblClick=function(id){\r\n"+
            "	this.callBack('dblClick',{id:id});\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPortlet.prototype.handleWheel=function(e){\r\n"+
            "	if(e.target.fieldId == null){\r\n"+
            "		this.graph.scrollPane.handleWheel(e);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function ProgressBarPortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.progressBackDiv=nw('div','progress_bar_back');\r\n"+
            "  this.progressOuterDiv=nw('div','progress_bar_outer');\r\n"+
            "  this.progressMessageDiv=nw('div','progress_bar_message');\r\n"+
            "  this.progressDiv=nw('div','progress_bar');\r\n"+
            "  this.divElement.appendChild(this.progressBackDiv);\r\n"+
            "  this.progressBackDiv.appendChild(this.progressOuterDiv);\r\n"+
            "  this.progressOuterDiv.appendChild(this.progressDiv);\r\n"+
            "  this.progressOuterDiv.appendChild(this.progressMessageDiv);\r\n"+
            "  this.paddingT=10;\r\n"+
            "  this.paddingR=10;\r\n"+
            "  this.paddingB=10;\r\n"+
            "  this.paddingL=10;\r\n"+
            "};\r\n"+
            "\r\n"+
            "ProgressBarPortlet.prototype.setStyle=function(paddingT,paddingR,paddingB,paddingL,style){\r\n"+
            "	this.paddingT=paddingT;\r\n"+
            "	this.paddingR=paddingR;\r\n"+
            "	this.paddingB=paddingB;\r\n"+
            "	this.paddingL=paddingL;\r\n"+
            "	this.style=style;\r\n"+
            "	this.updatePadding();\r\n"+
            "}\r\n"+
            "ProgressBarPortlet.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.updatePadding();\r\n"+
            "}\r\n"+
            "ProgressBarPortlet.prototype.updatePadding=function(){\r\n"+
            "  this.progressOuterDiv.style.top=toPx(this.paddingT);\r\n"+
            "  this.progressOuterDiv.style.left=toPx(this.paddingL);\r\n"+
            "  this.progressOuterDiv.style.width=toPx(Math.max(this.location.width-this.paddingR-this.paddingL,0));\r\n"+
            "  this.progressOuterDiv.style.height=toPx(Math.max(this.location.height-this.paddingT-this.paddingB,0));\r\n"+
            "  applyStyle(this.progressBackDiv,this.style);\r\n"+
            "};\r\n"+
            "\r\n"+
            "ProgressBarPortlet.prototype.setProgress=function(progress,text,color){\r\n"+
            "	this.progressDiv.style.width=fl(100*progress)+\"%\";\r\n"+
            "	this.progressMessageDiv.innerHTML=text;\r\n"+
            "	this.progressDiv.style.background=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function redirectToLogin(){\r\n"+
            "    sessionStorage.setItem('");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
          out.print(
            "',null);\r\n"+
            "	if(portletManager){\r\n"+
            "		portletManager.closeWindows();\r\n"+
            "        portletManager.pendingPortalAjax=null;\r\n"+
            "        portletManager.waitingPortalAjaxResponse=null;\r\n"+
            "	}\r\n"+
            "	window.location='");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("loggedOutUrl"));
          out.print(
            "';\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "//################\r\n"+
            "//##### Ajax #####\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "PortletManager.prototype.portletAjax=function(params){\r\n"+
            "  params.pageUid=portletManager.pageUid;\r\n"+
            "  params.seqnum=nextSendSeqnum++;\r\n"+
            "  params.");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
          out.print(
            "=this.pgid;\r\n"+
            "  var paramsText=joinAndEncodeMap('&','=',params);\r\n"+
            "  this.checkEnableAjaxLoading(params);\r\n"+
            "  if(this.pendingPortalAjax!=null){\r\n"+
            "      this.ajaxMaxSeqnum = params.seqnum;\r\n"+
            "	  this.pendingPortalAjax+='\\n'+paramsText;\r\n"+
            "  }\r\n"+
            "  else if(this.waitingPortalAjaxResponse){\r\n"+
            "	  this.pendingPortalAjax=paramsText;\r\n"+
            "	  this.updateLastAjaxTime();\r\n"+
            "  }\r\n"+
            "  else {\r\n"+
            "      this.ajaxMaxSeqnum = params.seqnum;\r\n"+
            "	  this.portletAjaxNow(paramsText);\r\n"+
            "	  this.updateLastAjaxTime();\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "PortletManager.prototype.auditToServer=function(text){\r\n"+
            "    var params={type:'audit',text:text};\r\n"+
            "    params.pageUid=portletManager.pageUid;\r\n"+
            "    params.seqnum=nextSendSeqnum++;\r\n"+
            "    params.webWindowId=window.windowId;\r\n"+
            "    params.");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
          out.print(
            "=this.pgid;\r\n"+
            "    ajax(portletManager.callbackUrl,true,true,params,null);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "var TEXT_DECODER = new TextDecoder();\r\n"+
            "function decompressAndDecode(code){\r\n"+
            "	if(code == null || code.byteLength == 0)\r\n"+
            "		return \"\";\r\n"+
            "	var magic = new Uint8Array(code,0,2);\r\n"+
            "	if(magic[0] == 31){//} && magic[1] == 156){\r\n"+
            "		var inflated = pako.inflate(code);\r\n"+
            "		var decoded = TEXT_DECODER.decode(inflated);\r\n"+
            "		return decoded;\r\n"+
            "	}\r\n"+
            "	else \r\n"+
            "		return TEXT_DECODER.decode(code);\r\n"+
            "}\r\n"+
            "\r\n"+
            "var showed503=false;\r\n"+
            "PortletManager.prototype.portletAjaxCallback=function(event){\r\n"+
            "	var origReq=event.srcElement || event.target;\r\n"+
            "	if(origReq.readyState!=4)\r\n"+
            "		return;\r\n"+
            "	var code = decompressAndDecode(origReq.response);\r\n"+
            "	var status=origReq.status;\r\n"+
            "    try{\r\n"+
            "      this.waitingPortalAjaxResponse=false;\r\n"+
            "	  this.checkHideLoadingDialog(origReq);\r\n"+
            "      if(status==200){\r\n"+
            "        eval(code); \r\n"+
            "      }else if(status==503){\r\n"+
            "    	if(!showed503){\r\n"+
            "		  showed503=true;\r\n"+
            "		  alertDialogWindowsGeneric(alertWarningDialog, [\"Web balancer has reported web server is down. \\n(Error 503)\",\"\"], this.windows, null);	\r\n"+
            "    	}\r\n"+
            "      }\r\n"+
            "      \r\n"+
            "      \r\n"+
            "      this.lastPortalAjaxWasError=false;\r\n"+
            "    }catch(e){\r\n"+
            "      var ticket=generateTicket();\r\n"+
            "	  this.portalAjaxErrorsCount++;	\r\n"+
            "      var lines=code.split('\\n');\r\n"+
            "      var codeWithLineNums=\"\";\r\n"+
            "      for(var i in lines){\r\n"+
            "    	  codeWithLineNums+=(parseInt(i)+1)+\" \"+lines[i]+\"\\n\";\r\n"+
            "      }\r\n"+
            "      var txt=  '#### Version: '+ portletManager.buildVersion +' ####\\n'+ ticket+': Error in ajax response from: '+portletManager.callbackUrl+' ####\\n#### Request ####\\n'+origReq.paramsText+'\\n#### Throwable ####\\n'+e.stack+'\\n#### Response ####\\n'+codeWithLineNums;      \r\n"+
            "      if(this.portalAjaxErrorsCount< 2 && !this.lastPortalAjaxWasError){\r\n"+
            "        log(txt);\r\n"+
            "		alertDialogWindowsGeneric(alertWarningDialog, [\"Oops! Something went wrong\", \"There has been an error.<P> Please reference ticket: <B>\"+ ticket + \"</B></P> <BR> <P id=buildversionnum>\" +  portletManager.buildVersion + \"</P>\", txt], this.windows, null);	\r\n"+
            "		var versionnum = document.getElementById(\"buildversionnum\");\r\n"+
            "		versionnum.style.color = \"#a09b9b\";\r\n"+
            "		versionnum.style.fontWeight = \"bold\";\r\n"+
            "		versionnum.style.position = \"relative\";\r\n"+
            "		versionnum.style.top = \"-44px\";\r\n"+
            "		versionnum.style.left = \"-4px\";\r\n"+
            "      }\r\n"+
            "      if(this.portalAjaxErrorsCount<100)\r\n"+
            "  	    this.auditToServer(txt);\r\n"+
            "      this.lastPortalAjaxWasError=true;\r\n"+
            "    }\r\n"+
            "    if(this.pendingPortalAjax!=null &&  !this.waitingPortalAjaxResponse){\r\n"+
            "      var t=this.pendingPortalAjax;\r\n"+
            "      this.pendingPortalAjax=null;\r\n"+
            "      this.portletAjaxNow(t);\r\n"+
            "	  this.updateLastAjaxTime();\r\n"+
            "    }\r\n"+
            "  };\r\n"+
            "\r\n"+
            "    \r\n"+
            "PortletManager.prototype.portletAjaxNow=function(paramsText){\r\n"+
            "  this.waitingPortalAjaxResponse=true;\r\n"+
            "  if(this.ajaxSafeMode==\"on\"){\r\n"+
            "    var PORTLET_AJAX_REQUEST=new XMLHttpRequest(); // code for IE7+, Firefox, Chrome, Opera, Safari\r\n"+
            "    var that=this;\r\n"+
            "    PORTLET_AJAX_REQUEST.onerror=function(o){that.onPortalAjaxError(o);};\r\n"+
            "    PORTLET_AJAX_REQUEST.onreadystatechange=function(o){that.portletAjaxCallback(o);};\r\n"+
            "    PORTLET_AJAX_REQUEST.open(\"POST\",portletManager.callbackUrl,true);\r\n"+
            "    PORTLET_AJAX_REQUEST.setRequestHeader(\"Content-type\",\"text/html\"); \r\n"+
            "    PORTLET_AJAX_REQUEST.send(paramsText);\r\n"+
            "    PORTLET_AJAX_REQUEST.paramsText=paramsText;\r\n"+
            "	PORTLET_AJAX_REQUEST.maxSeqnum=this.ajaxMaxSeqnum;\r\n"+
            "  }else if(this.ajaxSafeMode==\"extra\"){\r\n"+
            "    var PORTLET_AJAX_REQUEST=new XMLHttpRequest(); // code for IE7+, Firefox, Chrome, Opera, Safari\r\n"+
            "    var that=this;\r\n"+
            "    PORTLET_AJAX_REQUEST.onerror=function(o){that.onPortalAjaxError(o);};\r\n"+
            "    PORTLET_AJAX_REQUEST.onreadystatechange=function(o){that.portletAjaxCallback(o);};\r\n"+
            "    PORTLET_AJAX_REQUEST.open(\"POST\",portletManager.callbackUrl,true);\r\n"+
            "    PORTLET_AJAX_REQUEST.setRequestHeader(\"Content-type\",\"text/html\"); \r\n"+
            "    PORTLET_AJAX_REQUEST.paramsText=paramsText;\r\n"+
            "	PORTLET_AJAX_REQUEST.maxSeqnum=this.ajaxMaxSeqnum;\r\n"+
            "    window.setTimeout( PORTLET_AJAX_REQUEST.send(PORTLET_AJAX_REQUEST.paramsText) ,1);\r\n"+
            "  }else{\r\n"+
            "    this.PORTLET_AJAX_REQUEST.open(\"POST\",portletManager.callbackUrl,true);\r\n"+
            "    this.PORTLET_AJAX_REQUEST.setRequestHeader(\"Content-type\",\"text/html\"); \r\n"+
            "    this.PORTLET_AJAX_REQUEST.send(paramsText);\r\n"+
            "    this.PORTLET_AJAX_REQUEST.paramsText=paramsText;\r\n"+
            "	this.PORTLET_AJAX_REQUEST.maxSeqnum=this.ajaxMaxSeqnum;\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "PortletManager.prototype.callbackHtmlPortlet=function(event,node,value){\r\n"+
            "	while(node!=null){\r\n"+
            "		if(node.callback)\r\n"+
            "			return node.callback(event,value);\r\n"+
            "		else\r\n"+
            "			node=node.parentNode;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function callbackHtmlPortlet(event,node,value){\r\n"+
            "	portletManager.callbackHtmlPortlet(event,node,value);\r\n"+
            "}\r\n"+
            "PortletManager.prototype.onPortalAjaxError=function(o){\r\n"+
            "	var dialog = document.getElementById(\"alert_dialog\");\r\n"+
            "	if (!dialog)\r\n"+
            "		alertDialogWindowsGeneric(alertWarningDialog, [\"Web Server not responding\", \"It appears the web server is not responding.<BR>Please refresh to try again (press F5)\", null], this.windows, null);	\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function getPortletManager(){\r\n"+
            "	return portletManager;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "function FormPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  var that=this;\r\n"+
            "  this.form=new Form(this.portlet,parentId);\r\n"+
            "  this.form.callBack=function(action,params){that.callBack(action,params);};\r\n"+
            "  this.portlet.divElement.customCb=function(type,params){that.callBack('customCallback',{customType:type,customParams:params});};\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormPortlet.prototype.handleWheel=function(e){\r\n"+
            "	if(e.target instanceof HTMLCanvasElement){\r\n"+
            "		//do not handle\r\n"+
            "	}\r\n"+
            "	else if(e.target.fieldId == null){\r\n"+
            "		this.form.formDOMManager.scrollPane.handleWheel(e);\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		var fid = e.target.fieldId;\r\n"+
            "		var field = this.form.getField(fid);\r\n"+
            "		if(field == null)\r\n"+
            "			return;\r\n"+
            "		if(field.disabled == true)\r\n"+
            "			this.form.formDOMManager.scrollPane.handleWheel(e);\r\n"+
            "		else if(field instanceof NumericRangeField && field.slider.isNull == true){\r\n"+
            "			this.form.formDOMManager.scrollPane.handleWheel(e);\r\n"+
            "		}\r\n"+
            "		else if(field instanceof NumericRangeSubRangeField && field.slider.isNull == true){\r\n"+
            "			this.form.formDOMManager.scrollPane.handleWheel(e);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FormPortlet.prototype.setHiddenHtmlLayout=function(htmlLayout,rotate){\r\n"+
            " return this.form.setHiddenHtmlLayout(htmlLayout,rotate);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormPortlet.prototype.setHtmlLayout=function(htmlLayout,rotate){\r\n"+
            " return this.form.setHtmlLayout(htmlLayout,rotate);\r\n"+
            "}\r\n"+
            "FormPortlet.prototype.setButtonStyle=function(buttonHeight,buttonPaddingT,buttonPaddingB,buttonPanelStyle,buttonsStyle,buttonSpacing){\r\n"+
            "	  return this.form.setButtonStyle(buttonHeight,buttonPaddingT,buttonPaddingB,buttonPanelStyle,buttonsStyle,buttonSpacing);\r\n"+
            "}\r\n"+
            "FormPortlet.prototype.setSize=function(width,height){\r\n"+
            "  return this.form.setSize(width,height);\r\n"+
            "}\r\n"+
            "FormPortlet.prototype.setScroll=function(clipLeft, clipTop){\r\n"+
            "	this.form.setScroll(clipLeft,clipTop);\r\n"+
            "};\r\n"+
            "\r\n"+
            "FormPortlet.prototype.repaint=function(){\r\n"+
            "	return this.form.repaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormPortlet.prototype.addFieldAbsolute=function(field,cssStyle,x,y,w,h){\r\n"+
            "  return this.form.addFieldAbsolute(field,cssStyle,x,y,w,h);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormPortlet.prototype.removeField=function(id){\r\n"+
            "	  return this.form.removeField(id);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormPortlet.prototype.addField=function(field, cssStyle, isFieldHidden){\r\n"+
            "  return this.form.addField(field, cssStyle,isFieldHidden);\r\n"+
            "};\r\n"+
            "\r\n"+
            "FormPortlet.prototype.setScrollOptions=function(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows, cornerColor){\r\n"+
            "  return this.form.setScrollOptions(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows,cornerColor);\r\n"+
            "};\r\n"+
            "\r\n"+
            "FormPortlet.prototype.setValue=function(fieldId,value,modificationNumber){\r\n"+
            "  return this.form.setValue(fieldId,value,modificationNumber);\r\n"+
            "};\r\n"+
            "FormPortlet.prototype.setHeight=function(fieldId,height){\r\n"+
            "  return this.form.setHeight(fieldId,height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "FormPortlet.prototype.addButton=function(id,name,cssStyle){\r\n"+
            "  return this.form.addButton(id,name,cssStyle);\r\n"+
            "};\r\n"+
            "FormPortlet.prototype.getField=function(fieldId){\r\n"+
            "  return this.form.getField(fieldId);\r\n"+
            "};\r\n"+
            "FormPortlet.prototype.reset=function(){\r\n"+
            "  return this.form.reset();\r\n"+
            "};\r\n"+
            "FormPortlet.prototype.resetButtons=function(){\r\n"+
            "  return this.form.resetButtons();\r\n"+
            "};\r\n"+
            "FormPortlet.prototype.setCssStyle=function(cssStyle){\r\n"+
            "  return this.form.setCssStyle(cssStyle);\r\n"+
            "};\r\n"+
            "FormPortlet.prototype.setLabelWidth=function(labelWidth, labelPadding, labelsStyle,fieldSpacing,widthStretchPadding){\r\n"+
            "  return this.form.setLabelWidth(labelWidth,labelPadding, labelsStyle,fieldSpacing,widthStretchPadding);\r\n"+
            "};\r\n"+
            "\r\n"+
            "FormPortlet.prototype.showContextMenu=function(menu){\r\n"+
            "  return this.form.showContextMenu(menu);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormPortlet.prototype.showButtonContextMenu=function(menu){\r\n"+
            "  return this.form.showButtonContextMenu(menu);\r\n"+
            "}\r\n"+
            "FormPortlet.prototype.setFieldPosition=function(id,x,y,w,h){\r\n"+
            "  return this.form.setFieldPosition(id,x,y,w,h);\r\n"+
            "}\r\n"+
            "FormPortlet.prototype.setFieldLabelPosition=function(id,x,y,w,h,a,s,padding){\r\n"+
            "  return this.form.setFieldLabelPosition(id,x,y,w,h,a,s,padding);\r\n"+
            "}\r\n"+
            "FormPortlet.prototype.setFieldStyleOptions=function(id, labelColor, bold, italic, underline, fontFamily){\r\n"+
            "  return this.form.setFieldStyleOptions(id, labelColor, bold, italic, underline, fontFamily);\r\n"+
            "}\r\n"+
            "FormPortlet.prototype.setFieldLabelSize=function(id, labelFontSize){\r\n"+
            "  return this.form.setFieldLabelSize(id, labelFontSize);\r\n"+
            "}\r\n"+
            "FormPortlet.prototype.getForm=function(){\r\n"+
            "  return this.form;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormPortlet.prototype.focusField=function(fieldId){\r\n"+
            "  return this.form.focusField(fieldId);\r\n"+
            "}\r\n"+
            "");

	}
	
}