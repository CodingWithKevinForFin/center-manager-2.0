var SEARCH_BUTTON_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="search"> <g> <circle style="fill:none;stroke:';
var SEARCH_BUTTON_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;" x1="58.6" y1="58.9" x2="87.7" y2="88"/> </g> </g> '+SVG_SUFFIX;
var DOWNLOAD_BUTTON_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="save"> <g> <line style="fill:none;stroke:';
var DOWNLOAD_BUTTON_IMAGE_SUFFIX=';stroke-width:12;stroke-miterlimit:10;" points="77.3,44.3 50,71.6 22.7,44.3"/> </g> </g> </g> '+SVG_SUFFIX;
var EXPAND_BUTTON_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="plus_1_"> <g> <line style="fill:none;stroke:';
var EXPAND_BUTTON_IMAGE_SUFFIX=';stroke-width:18;stroke-miterlimit:10;" x1="50" y1="12" x2="50" y2="88"/> </g> </g> '+SVG_SUFFIX;
var CONTRACT_BUTTON_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="minus"> <line style="fill:none;stroke:';
var CONTRACT_BUTTON_IMAGE_SUFFIX=';stroke-width:18;stroke-miterlimit:10;" x1="12" y1="50" x2="88" y2="50"/> </g> '+SVG_SUFFIX;
var PORTAL_DIALOG_HEADER_TITLE;

function g(id){
	return portletManager.getPortlet(id);
}
function rmp(id){
	return portletManager.removePortlet(id);
}
function gnt(id){
	return portletManager.getPortletNoThrow(id);
}
function getMainWindow(){
	return this.portletManager==null ? window : this.portletManager.mainWindow;
}

var portletManager;
 window.windowId=getWindowParam(window,'<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.WINDOWID"/>','0');
  
  
if(window.portletManager!=null){
	portletManager=window.portletManager;
	var EXPECTED_DOCUMENT=portletManager.mainWindow.document;
	//window.eventsForPopup=[];
	//window.setInterval(function(){pumpEventsForPopup();},5000);
	window.setInterval(function(){if(!(portletManager.mainWindow.document===EXPECTED_DOCUMENT)) window.close();},500);
}else{
	if(window.windowId!=0)
		window.close();
}

//var popupEvents={};
//function pumpEventsForPopup(){
//	if(!(portletManager.mainWindow.document===EXPECTED_DOCUMENT))
//		window.close();
//	err(portletManager.mainWindow.popupEvents);
//	var queue=portletManager.mainWindow.popupEvents[windowId];
//	if(queue==null || queue.length==0)
//		return;
//	for(var n in queue){
//		var codeAndTarget=queue[n];
//		var i=codeAndTarget[1];
//		eval(codeAndTarget[0]);
//	}
//	portletManager.mainWindow.popupEvents[windowId]=null;
//}
//
//
//function addPopupEvent(windowId,event,target){
//	var queue=popupEvents[windowId];
//	if(queue==null)
//	  queue=popupEvents[windowId]=[];
//	queue[queue.length]=[event,target];
//}


function postInit(windowId){
	if(portletManager==null){
		window.close();
		redirectToLogin();
	}else
	  portletManager.postInit(windowId);
}


var nextSendSeqnum=0;
function PortletManager(callbackUrl,pageUid,pollingMs,ajaxSafeMode,ajaxLoadingTimeoutMs,ajaxLoadingCheckPeriodMs,portalDialogHeaderTitle,buildVersion){
  var that=this;
  this.ajaxSafeMode=ajaxSafeMode;
  if(this.ajaxSafeMode)
	  log("ajax.safe.mode="+this.ajaxSafeMode);
  this.pgid=__PGID;
  this.callbackUrl=callbackUrl;
  this.pageUid=pageUid;
  this.pollingMs=pollingMs;
  this.setBlankPageLoadingAnimationVisible(false);
  this.portlets={};
  this.pendingPortalAjax=null;
  this.portalAjaxErrorsCount=0;
  this.lastPortalAjaxWasError=false;
  this.waitingPortalAjaxResponse=false;
  this.PORTLET_AJAX_REQUEST=new XMLHttpRequest(); // code for IE7+, Firefox, Chrome, Opera, Safari
  this.PORTLET_AJAX_REQUEST.responseType="arraybuffer";
  this.PORTLET_AJAX_REQUEST.onerror=function(o){that.onPortalAjaxError(o);};
  this.PORTLET_AJAX_REQUEST.onreadystatechange=function(o){that.portletAjaxCallback(o);};
  this.ajaxMaxSeqnum = null;
  this.expectedSeqNumProcessed=0;
  this.windows={};
  this.windows[0]=window;
  this.mainWindow=window;
  this.buildVersion = buildVersion;
  window.onbeforeunload=function(e){that.onBeforeClose(e);that.onUnload();};
  this._AJAX_POLLING_TIME_MS = Date.now();
  this._AJAX_POLLING_LATENCY_MS = 0;
  this._AJAX_POLLING_SAMPLE_SIZE = 3;
  this._AJAX_POLLING_COUNT = 0;
  this._AJAX_LOADING_MINTIME_COND_MS = ajaxLoadingTimeoutMs;
  this._AJAX_LOADING_CHECK_FREQ_MS = ajaxLoadingCheckPeriodMs;
  this.ajaxLoadingEnabled = false;
  this.ajaxLoadingSeqNum = null;
  this.lastAjaxRequestTime=null;
  this.isWaitingAjax = false;
  this.isShowLoadingAjax = false;
  this.loadingDialogs = {};
  this.loadingBarStyles= new Object();
  that.checkAjaxWaitingInterval=window.setInterval(function(){that.checkAjaxWaiting();},that._AJAX_LOADING_CHECK_FREQ_MS);
  PORTAL_DIALOG_HEADER_TITLE=portalDialogHeaderTitle;
}

PortletManager.prototype.onUnload = function(){
	this.callBack('unload',{});
}
PortletManager.prototype.updateAjaxPollingLatency = function(){
	if(this._AJAX_POLLING_COUNT >= this._AJAX_POLLING_SAMPLE_SIZE){
		this._AJAX_POLLING_TIME_MS += this._AJAX_POLLING_LATENCY_MS;
		this._AJAX_POLLING_LATENCY_MS = (Date.now() - this._AJAX_POLLING_TIME_MS)/this._AJAX_POLLING_COUNT;
	}
	else{
		this._AJAX_POLLING_COUNT++;
		this._AJAX_POLLING_LATENCY_MS = (Date.now() - this._AJAX_POLLING_TIME_MS)/this._AJAX_POLLING_COUNT;
	}
 	//err("polling latency", this._AJAX_POLLING_LATENCY_MS);
}

PortletManager.prototype.focusField=function(portletId,attachmentId){
	g(portletId).focusField(attachmentId);
}

PortletManager.prototype.showLoadingDialog=function(text, _window){
	if(_window == null)
		_window = window;
		
    var content=nw('div','dialog');
    content.innerHTML=text;
	var dialog= new Dialog(content, _window);
	var dialogBgColor = portletManager.loadingBarStyles.dialogBgColor;
	var headerBgColor = portletManager.loadingBarStyles.headerBgColor;
	var stripeColor1 = portletManager.loadingBarStyles.stripeColor1; 
	var stripeColor2 = portletManager.loadingBarStyles.stripeColor2;
	var stripeColor3 = portletManager.loadingBarStyles.stripeColor3;
	if (dialogBgColor) {
		dialog.setDialogBgColor(dialogBgColor);
	}
	if (headerBgColor) {
		dialog.setHeaderBgColor(headerBgColor);
	}
	buildSVGLoadAnimation(stripeColor1,stripeColor2,stripeColor3);
  	dialog.setHeaderTitle(PORTAL_DIALOG_HEADER_TITLE);
	dialog.setImageHtml(SVG_LOADING_ANIMATION);
    dialog.setGlassOpacity(0);
	dialog.setCanResize(false);
	dialog.setSize(320,86);
	dialog.setSmallerCloseButton();
	dialog.setImageSize(45, 45);
	dialog.setType("loading");
	dialog.show();
	return dialog;
}

PortletManager.prototype.storeLoadingDialogStyle=function(styles) {
	if (styles.dialogBgColor) {
		this.loadingBarStyles.dialogBgColor=styles.dialogBgColor;		
	}
	if (styles.headerBgColor) {
		this.loadingBarStyles.headerBgColor=styles.headerBgColor;
	}
	if (styles.stripeColor1) {
		this.loadingBarStyles.stripeColor1=styles.stripeColor1;		
	}
	if (styles.stripeColor2) {
		this.loadingBarStyles.stripeColor2=styles.stripeColor2;
	}
	if (styles.stripeColor3) {
		this.loadingBarStyles.stripeColor3=styles.stripeColor3;		
	}

}

PortletManager.prototype.checkEnableAjaxLoading=function(params){
	//if(params.type != "polling")
	//	err(params.type, params.portletId, params);
	if(this.ajaxLoadingEnabled == true)
		return;
	if(params == null)
		return;
	if(params.seqnum == null)
		return;
	if(params.portletId == null){
		if(params.type == "polling")
			return;
		if(params.type != "userClick" || params.type != "userKey" || params.type != "userScroll" || params.type != "userActivatePortlet" || params.type != "popupClosed" || params.type == "notificationClicked" || params.type == "notificationClosed")
			return;
	}
	this.ajaxLoadingEnabled = true;
	this.ajaxLoadingSeqNum = params.seqnum;
	this.lastAjaxRequestTime = null;
	
}
PortletManager.prototype.disableAjaxLoading=function(){
	this.ajaxLoadingEnabled = false;
	this.ajaxLoadingSeqNum = null;
}
PortletManager.prototype.updateLastAjaxTime=function(){
	if(this.ajaxLoadingEnabled == false)
		return;
	if(this.lastAjaxRequestTime != null)
		return;
	this.lastAjaxRequestTime = Date.now();
}
	
PortletManager.prototype.checkHideLoadingDialog = function(ajaxReq){
	if(this.ajaxLoadingEnabled == false)
		return;
	if(this.ajaxLoadingSeqNum > ajaxReq.maxSeqnum)
		return;
	if(this.isShowLoadingAjax == true){
		this.isShowLoadingAjax = false;
		closeDialogWindowsGeneric(this.loadingDialogs);
	}
	// Disable the loading now that it responds
	this.disableAjaxLoading();
}
PortletManager.prototype.checkAjaxWaiting=function(){
	if(this.ajaxLoadingEnabled == false || this.lastAjaxRequestTime == null)
		return;
	if(this.waitingPortalAjaxResponse == false)
		return;
	if(this.isShowLoadingAjax == true)
		return;
 
	var currentTime = Date.now();

	var diff = (currentTime - this.lastAjaxRequestTime);
	if(diff > (this._AJAX_LOADING_MINTIME_COND_MS + this._AJAX_POLLING_LATENCY_MS)){
		this.isShowLoadingAjax = true;
		alertDialogWindowsGeneric(this.showLoadingDialog, "", this.windows, this.loadingDialogs);	
	}
}

PortletManager.prototype.callRest=function(id,isPost,target,data,timeoutMs){
    var that=this;
    var restRequest=new XMLHttpRequest(); 
    restRequest.timeout=timeoutMs;
    restRequest.withCredentials=true;
    restRequest.onreadystatechange=function(o){that.onRestAjaxCallback(o);};
    restRequest.open(isPost ? "POST" : "GET" ,target,true);
    restRequest.setRequestHeader("Content-type","text/html"); 
    restRequest.send(data);
    restRequest.id=id;
}
PortletManager.prototype.onRestAjaxCallback=function(event){
	var origReq=event.srcElement || event.target;
	if(origReq.readyState!=4)
		return;
	var id=origReq.id;
	var text = origReq.response;
	var status=origReq.status;
	this.portletAjax({id: id,status:status,type:'restResponse',response: text});
}

PortletManager.prototype.onBeforeClose=function(id){
	for(var i in this.windows)
		if (i !=0) // we are leaving the main window anyway, so no need to close the first window.
			this.windows[i].close();
	for(var i in this.pendingNotifications) {
		var pn=this.pendingNotifications[i];
		pn.onclose=null;
		pn.close();
	}
}

PortletManager.prototype.getWindow=function(id){
	return this.windows[id];
}
PortletManager.prototype.closeWindows=function(){
	//for(var i in this.windows)
	  //this.windows[i].close();
}


PortletManager.prototype.onUserSpecialKey=function(e){
	var key=e.key;
	if(key=='Alt' || key=='Control' || key=='Shift')
		return;
	this.callBack('userKey',{k:key,s:e.shiftKey,a:e.altKey,c:e.ctrlKey});
}
PortletManager.prototype.onUserSpecialScroll=function(left, top, id){
//	this.callBack('userScroll', {l:left, t:top, id:id.substring(8)});
	this.callBack('userScroll', {l:left, t:top, id:id});
}

PortletManager.prototype.onUserSpecialClick=function(e, mouseEventType, pid){
	this.callBack('userClick',{x:e.clientX, y:e.clientY, b:e.button, c:e.ctrlKey, a:e.altKey, s:e.shiftKey, t:mouseEventType, pid:pid});
}
PortletManager.prototype.onUserActivePortlet=function(portletId){
	this.callBack('userActivatePortlet',{pid:portletId,x:this.mainWindow.MOUSE_POSITION_X,y:this.mainWindow.MOUSE_POSITION_Y});
}

PortletManager.prototype.showNotification=function(nid,title,body,icon){
	if(Notification.permission !== "granted"){
	  var that=this;
		Notification.requestPermission().then(function(result){
		if(result=="denied")
	      portletManager.callBack('notificationDenied',{nid:nid});
		else if(result=="granted"){
	      that.showNotificationInner(nid,title,body,icon);
		}
	  });
    } 
	this.showNotificationInner(nid,title,body,icon);
    //notification.onclick = functikkon () { onNotificationClicked(notificationId); };
}

PortletManager.prototype.pendingNotifications={};
PortletManager.prototype.pendingNotificationsNextId=0;

PortletManager.prototype.showNotificationInner=function(nid,title,body,icon){
    var notification = new Notification(title,{  body: body, icon: icon,requireInteraction:true});
    var that=this;
    notification.pmnid=that.pendingNotificationsNextId++;
    that.pendingNotifications[notification.pmnid]=notification;
    notification.onclick = function () {
        delete that.pendingNotifications[notification.pmnid];
    	notification.onclose=null;
    	notification.close();
    	getMainWindow().focus();
	    portletManager.callBack('notificationClicked',{nid:nid});
    };
    notification.onclose = function () {
//    	session.alert('notification');
        delete that.pendingNotifications[notification.pmnid];
	    portletManager.callBack('notificationClosed',{nid:nid});
    };
}

PortletManager.prototype.showPopupWindow=function(windowId,left,top,width,height,title){
	var w=window.open("<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL"/>?<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.WINDOWID"/>="+windowId+"&<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID"/>="+this.pgid,"_blank",'width='+width+',height='+height+',left='+left+',top='+top+',resizable=yes');
	
	if(w==null){
	  this.callBack('popupFailed',{popupWindowId:windowId});
      return;
	}
	w.portletManager=portletManager;
	w.titleOverride=title;
	w.windowId=windowId;
	this.windows[windowId]=w;
}

PortletManager.prototype.getPortlet=function(id){
	var r=this.portlets[id];
	if(r==null)
		throw new Error("id not found: "+id);
	return r;
}
PortletManager.prototype.getPortletNoThrow=function(id){
	var r=this.portlets[id];
	return r;
}

PortletManager.prototype.putPortlet=function(portlet){
	var id=portlet.portletId;
	if(this.portlets[id])
		throw new Error("id already exists: "+id);
	this.portlets[id]=portlet;
	return portlet;
}

PortletManager.prototype.removePortlet=function(id){
	var p=this.portlets[id];
	if(p==null)
		throw new Error("portlet id not found for remove: "+id);
	p.close();
	delete this.portlets[id];
}
PortletManager.prototype.removePortletRecursive=function(id){
	var p=this.portlets[id];
	if(p==null)
		throw new Error("portlet id not found for remove: "+id);
	delete this.portlets[id];
	for(var i in p.childPortletIds){
		this.removePortletRecursive(i);
	}
}

PortletManager.prototype.setPolling=function(pollingMs){
  this.pollingMs=pollingMs;
}
PortletManager.prototype.postInit=function(webWindowId){
  this.callBack('postInit',{webWindowId:webWindowId,<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID"/>:this.pgid});
}

PortletManager.prototype.onSeqnum=function(seqnum){
  if(seqnum!=this.expectedSeqNumProcessed){
	log("bad sequence number: "+seqnum + "!="+ this.expectedSeqNumProcessed);
    this.expectedSeqNumProcessed=seqnum;
  }
  this.expectedSeqNumProcessed++;
  
  //check for closed windows
  for(var i in this.windows){
	var w=this.windows[i];
	if(!w.location || !w.location.pathname || w.closed){
		this.callBack("popupClosed", {childWindowId:i});
		delete this.windows[i];
	}
  }
}
PortletManager.prototype.setBlankPageLoadingAnimationVisible=function(b){
	var blankPageLoadingAnimationContainer=getElement("blankpage-loading-animation-container");
	if (blankPageLoadingAnimationContainer == null)
		return;
	if(b)
		document.body.appendChild(blankPageLoadingAnimationContainer);
	else {
		document.body.removeChild(blankPageLoadingAnimationContainer);
		if (blankPageAnimationInterval)
			clearInterval(blankPageAnimationInterval);
	}
}
PortletManager.prototype.downloadFile=function(){
	var a = nw('A');
    a.href=a.download=this.callbackUrl+"?downloadFile=true&<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID"/>="+this.pgid;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

PortletManager.prototype.showSaveConfigDialog=function(txt){
	var text=nw('textarea');
	text.innerHTML=txt;
	text.style.position='absolute';
	text.style.overflow='auto';
	text.style.left='10px';
	text.style.top='10px';
	text.style.bottom='10px';
	text.style.right='10px';
	text.style.fontSize='12px';
	var d=new Dialog(text);
	d.setTitle('<f1:txt key="Save configuration"/>');
	d.setCanResize(true);
	d.setSize(800,600);
	d.onResize=function(loc){
		text.style.width=toPx(loc.width-30);
		text.style.height=toPx(loc.height-80);
	};
	d.show();
	text.select();
	text.focus();
};

PortletManager.prototype.showLoadConfigDialog=function(){
	var that=this;
	var text=nw('textarea');
	text.innerHTML='';
	text.style.position='absolute';
	text.style.overflow='auto';
	text.style.left='10px';
	text.style.top='10px';
	text.style.bottom='10px';
	text.style.right='10px';
	text.style.fontSize='12px';
	var d=new Dialog(text);
	d.setCanResize(true);
	d.setTitle('<f1:txt key="Load configuration"/>');
	d.setSize(800,600);
	d.onResize=function(loc){
		text.style.width=toPx(loc.width-30);
		text.style.height=toPx(loc.height-80);
	};
	d.addButton('<f1:txt key="load config"/>','ok');
	d.addButton('<f1:txt key="cancel"/>');
	d.show();
	d.onClose=function(e,d,reason){if(reason=='ok')that.callBack('loadConfig',{text:text.value});};
	text.focus();
};

PortletManager.prototype.onJsProcessed=function(pollingDelay){
//  this.showWait();
  if(this.hasQueuedPolling) return;
  this.hasQueuedPolling=true;
  this.updateAjaxPollingLatency();
  window.setTimeout("portletManager.pollAjax();", pollingDelay!=null ?  pollingDelay : this.pollingMs );
};

PortletManager.prototype.pollAjax=function(){
  this.hasQueuedPolling=false;
  this.callBack('polling',{});
};

PortletManager.prototype.callBack=function(type,params){
  params.type=type;
//  params.pageUid=this.pageUid;
//  params.seqnum=nextSendSeqnum++;
  this.portletAjax(params);
};

//PortletManager.prototype.showWait=function(text){
//	if(text!=null){
//      if(this.waitDiv==null){
//        this.waitDiv=nw("div","wait");
//        this.waitPaneDiv=nw("div","wait_pane");
//        document.body.appendChild(this.waitPaneDiv);
//        document.body.appendChild(this.waitDiv);
//      }
//	  this.waitDiv.style.display="inline";
//	  this.waitDiv.innerHTML=text;
//	  this.waitPaneDiv.style.display="inline";
//	}else if(this.waitDiv!=null){
//	  this.waitDiv.style.display="none";
//	  this.waitPaneDiv.style.display="none";
//	}
//};

PortletManager.prototype.playAudio=function(url){
	  var a=new Audio(url);
	  a.loop=false;
	  a.play();
}




//###################
//##### Portlet #####


function Portlet(instance,portletId,parentPortletId){
  var that=this;
  this.childPortletIds=[];
  instance.childPortletIds=this.childPortletIds;
  if(!instance.setSize)
    instance.setSize=this.setSize;
  if(!instance.setUserSelectable)
    instance.setUserSelectable=this.setUserSelectable;
  if(!instance.callBack)
    instance.callBack=this.callBack;
  if(!instance.removeChild)
    instance.removeChild=this.removeChild;
  if(!instance.close)
    instance.close=this.close;
  if(!instance.populatePortletMenu)
    instance.populatePortletMenu=function(menu){that.populatePortletMenu(menu);};
  if(!instance.setSockets)
    instance.setSockets=function(sockets){that.setSockets(sockets);};
  if(!instance.getPortlet)
    instance.getPortlet=function(){return that.getPortlet();};
  if(!instance.setOwningWindowId)
    instance.setOwningWindowId=function(id){return that.setOwningWindowId(id);};
  instance.setHIDS=function(id){return that.setHIDS(id);};
  instance.setHCSC=function(id){return that.setHCSC(id);};
  instance.location=new Rect();
  instance.portletId=portletId;
  instance.parentPortletId=parentPortletId;
  instance.divElement=nw('div');
  instance.divElement.portlet=instance;
//  instance.divElement.style.position='absolute';
  //instance.divElement.id="portlet_"+portletId;
  instance.divElement.portletId=portletId;
  instance.divElement.isPortletElement=true;
  instance.divElement.style.overflow='hidden';
  this.divElement=instance.divElement;
  this.location=instance.location;
  this.instance=instance;
}

//html id selector
Portlet.prototype.setHIDS=function(id){
   this.instance.divElement.id=id;
}
Portlet.prototype.setHCSC=function(c){
   if(this.instance.hcsc!=null)
     this.instance.divElement.classList.remove(this.instance.hcsc);
   this.instance.hcsc=c;
   if(this.instance.hcsc!=null)
     this.instance.divElement.classList.add(this.instance.hcsc);
}
Portlet.prototype.left = 0;
Portlet.prototype.top = 0;

Portlet.prototype.getPortlet=function(){
	return this;
}
Portlet.prototype.setOwningWindowId=function(owningWindowId){
	if(owningWindowId == this.owningWindowId)
		return;
	this.owningWindowId=owningWindowId;
	if(owningWindowId == null)
		this.owningWindow = null;
	else
		this.owningWindow=portletManager.getWindow(owningWindowId);
	this.instance.owningWindow=this.owningWindow;
	if (this.instance.setOwningWindow != null)
		this.instance.setOwningWindow(this.owningWindow);
	this.instance.owningWindowId=this.owningWindowId;
	
	//Update children keys
	var k = Object.keys(this.childPortletIds);
	if(k.length > 0)
		for(var i = 0; i < k.length;i++){
			var p = gnt(k[i]);
			if(p!= null) p.setOwningWindowId(this.owningWindowId);
		}
}

Portlet.prototype.populatePortletMenu=function(menu){
  var that=this;
  menu.addItem('<img src="rsc/asc.gif"> <f1:txt key="Delete this Component"/>',null,function(e){that.onUserDelete(e);});
  menu.addItem('<img src="rsc/autoasc.gif"> <f1:txt key="Wrap in a container"/>',null,function(e){that.onUserWrap(e);});
  for(var key in this.sockets){
	var k=new Object(key);
    menu.addItem('<img src="rsc/connect.gif">'+this.sockets[key],k,function(e,k){that.onUserConnect(e,k);});
  }
};
Portlet.prototype.onUserConnect=function(e,key){
	onSelectForConnect(this.instance.portletId,key);
};

Portlet.prototype.onUserDelete=function(menu){
  this.instance.parentPortlet.callBack('deleteChild',{childPortletId:this.instance.portletId});
};

Portlet.prototype.onUserWrap=function(menu){
  //this.instance.parentPortlet.callBack('wrapChild',{childPortletId:this.instance.portletId});
  //portletManager.showAddComponentsDialog('<f1:txt key="Wrap Component"/>',null,null,this.instance.parentPortlet,'wrapChild',{childPortletId:this.instance.portletId});
  this.instance.callBack('showWrapPortletDialog',{});
};
    
Portlet.prototype.close=function(type,params){
};

//Portlet.prototype.setInnerHTML=function(html){
  //this.divElement.innerHTML=html;
//};

Portlet.prototype.callBack=function(type,params){
  params.type=type;
  params.portletId=this.portletId;
//  params.pageUid=portletManager.pageUid;
//  params.seqnum=nextSendSeqnum++;
  portletManager.portletAjax(params);
};

Portlet.prototype.addChild=function(childId,width,height){
  var child=portletManager.getPortlet(childId);
  this.childPortletIds[childId]=true;
  child.parentPortlet=this.instance;
//	var k = Object.keys(this.childPortletIds);
//	if(k.length > 0)
//	err(k);
  child.setOwningWindowId(this.owningWindowId);
};
Portlet.prototype.removeChild=function(childId){
  var portlet=portletManager.getPortlet(childId);
  delete this.childPortletIds[childId];
  if(portlet==null)
	  alert('portlet not found for removal: '+childId);
  portlet.setOwningWindowId(null);
  //portlet.close();
  portlet.parentPortlet=null;
  var div=portlet.divElement;
  var parent=div.parentNode;
  if(parent!=null)
	  parent.removeChild(div);
};

Portlet.prototype.setSockets=function(sockets){
  this.sockets=sockets;
};

//Portlet.prototype.setVisible=function(isVisible){
  //this.divElement.style.display=isVisible ? 'inline' : 'none';
//}

Portlet.prototype.setSize=function(width,height){
  this.location.left=this.left;
  this.location.top=this.top;
  this.location.width=width;
  this.location.height=height;
  this.location.writeToElement(this.divElement);
};
Portlet.prototype.setUserSelectable=function(isSelectable){
	if(isSelectable)
      this.instance.divElement.portletSelectType='portletDiv';
};

//###########################
//##### Root Portlet #####

function RootPortlet(portletId){
  this.portlet=new Portlet(this,portletId,null);
  this.divElement.style.left='0px';
  this.divElement.style.right='0px';
  this.divElement.style.top='0px';
  this.divElement.style.bottom='0px';
  this.divElement.style.background='green';

  this.rootElement=nw('div');
  this.rootElement.style.background='white';
  this.rootElement.style.zIndex=0;
  this.rootElement.style.position="fixed";
  this.rootElement.style.width="100%";
  this.rootElement.style.height="100%";
  this.divElement.appendChild(this.rootElement);
  this.zi=0;

  this.isContainer=true;
  this.dialogs=[];
  this.root=null;
  var that=this;
  this.checkScreenLocationInterval=setInterval(function(){ that.checkScreenLocation(); }, 2000);

}
RootPortlet.prototype.checkScreenLocation = function(){
	if(this.owningWindow==null){
		this.callBack("popupClosed", {childWindowId:this.id});
		return;
	}
	if(this.screenX!=this.owningWindow.screenX || this.screenY!=this.owningWindow.screenY)
		this.onWindowResize();
}

RootPortlet.prototype.resizeTo = function(x,y,w,h){
	this.owningWindow.moveTo(x,y);
	var wDelta=this.owningWindow.outerWidth- this.owningWindow.innerWidth;
	var hDelta=this.owningWindow.outerHeight- this.owningWindow.innerHeight;
	this.owningWindow.resizeTo(w+wDelta,h+hDelta);
}

RootPortlet.prototype.init = function(windowId){
    var that=this;
	this.windowId=windowId;
	this.owningWindow=portletManager.getWindow(windowId);
	if(this.owningWindow==null){
		this.callBack("popupClosed", {childWindowId:windowId});
		return;
	}
    this.owningWindow.onresize=function(){that.onWindowResize();};
    this.owningWindow.document.addEventListener("visibilitychange", function(evt){that.onVisibilityChanged(evt);});
    this.onWindowResize();
    this.owningWindow.document.body.appendChild(this.divElement);
    this.owningWindow.kmm=new KeyMouseManager(this.owningWindow);
    
    this.owningWindow.addEventListener('focus', function(){that.onWindowFocusBlur(true)});
    this.owningWindow.addEventListener('blur', function(){that.onWindowFocusBlur(false)});
}
RootPortlet.prototype.setBrowserTitle = function(title){
    this.owningWindow.window.document.title=title;
}
RootPortlet.prototype.onWindowFocusBlur = function(focus){
	if(focus)
		this.gotFocus=true;
}
RootPortlet.prototype.onVisibilityChanged = function(evt){
  this.callBack('visibility',{visible:this.owningWindow.document.visibilityState != 'hidden'});
}
RootPortlet.prototype.onWindowResize = function(){
  this.screenX=this.owningWindow.screenX;
  this.screenY=this.owningWindow.screenY;
  if(this.owningWindow.document.visibilityState != 'hidden'){
	this.lastVisibleScreenX=this.screenX;
	this.lastVisibleScreenY=this.screenY;
  }
  var z = (1.0*window.outerWidth / window.innerWidth);
  this.callBack('location',{zoom:z,screenX:this.screenX,screenY:this.screenY, left:0,top:0,width:this.owningWindow.innerWidth,height:this.owningWindow.innerHeight,fullscreen:isFullScreen()});
};
RootPortlet.prototype.addChild=function(zIndex,childId,title,isModal){
  if(zIndex==0){
	this.root=portletManager.getPortlet(childId);
	this.rootElement.appendChild(this.root.divElement);
  }else{
	if(document.activeElement && document.activeElement.blur)
		document.activeElement.blur();
    var dw=new DesktopWindow(null,childId,portletManager.getPortlet(childId).divElement,title);
    dw.setStyleClassPrefix('portal_desktop');
    var that=this;
    this.dialogs[childId]=dw;
    dw.onUserButton=function(id,buttonId){if("close"==buttonId) that.onClose(id)};
    dw.onUserClosedWindow=function(id){that.onClose(id);}
    dw.onUserMovedWindow=function(id,x,y,w,h){that.onLocationChanged(id,x,y,w,h);}
    dw.setButtonsVisible({close:true,min:false,max:false,pop:false});
    dw.setAllowEditTitle(false);
    if(isModal){
      dw.splashDiv=nw('div');
      dw.splashDiv.style.left='0px';
      dw.splashDiv.style.top='0px';
      dw.splashDiv.style.bottom='0px';
      dw.splashDiv.style.right='0px';
      dw.splashDiv.style.position="fixed";
      dw.backgroundDiv=nw('div','dialog_pane');
      dw.backgroundDiv.style.zIndex=0;
      dw.splashDiv.appendChild(dw.backgroundDiv);
      dw.splashDiv.appendChild(dw.windowDiv);
      dw.splashDiv.style.zIndex=zIndex;
      dw.backgroundDiv.onclick=function(e){that.onOutsideClicked(e,childId)};
      dw.backgroundDiv.isGrey=true;
      this.divElement.appendChild(dw.splashDiv);
    }else{
//      dw.backgroundDiv=nw('div','dialog_pane');
//      dw.backgroundDiv.style.zIndex=0;
      dw.windowDiv.style.zIndex=zIndex;
//      dw.backgroundDiv.onclick=function(e){that.onOutsideClicked(e,childId)};
//      dw.backgroundDiv.isGrey=true;
//      this.divElement.appendChild(dw.backgroundDiv);
      this.divElement.appendChild(dw.windowDiv);
    }
  }
  this.portlet.addChild(childId);
}
RootPortlet.prototype.rebuildChildren=function(childId){
	removeAllChildren(this.divElement);
	for(var zIndex in this.childIds){
       var child=portletManager.getPortlet(this.childIds[zIndex]);
       if(zIndex==0){
	     var rootDiv=nw('div');
	     rootDiv.style.zIndex=0;
	     rootDiv.appendChild(child.divElement);
	     this.divElement.appendChild(rootDiv);
       }else{
	     var dialogDiv=nw('div');
	     dialogDiv.style.zIndex=zIndex;
	     dialogDiv.appendChild(child.divElement);
	     this.divElement.appendChild(dialogDiv);
       }
	}
}
RootPortlet.prototype.onOutsideClicked=function(e,id){
    var target=getMouseTarget(e);
    if(target.isGrey){
	  target.style.backgroundColor='transparent';
	}else{
	  target.style.backgroundColor='black';
	}
	target.isGrey=!target.isGrey;
	
	this.callBack('dialogOutsideClicked',{childId:id});
}
RootPortlet.prototype.onLocationChanged=function(id,x,y,w,h){
	this.callBack('dialogSized',{childId:id,left:x,top:y,width:w,height:h});
}
RootPortlet.prototype.onClose=function(id){
  this.callBack('dialogClosed',{childId:id});
}

RootPortlet.prototype.removeChild=function(childId){
  this.portlet.removeChild(childId);
  this.rebuildChildren();
}

RootPortlet.prototype.setDialogLocation=function(childId,left,top,width,height,isMax,zindex,hasCloseButton,shade,stylePrefix,headerSize,borderSize,outsideShowBlockIcon,isModal,options){
  var dialog=this.dialogs[childId];
  if(stylePrefix!=null)
    dialog.setStyleClassPrefix(stylePrefix);
  dialog.setWindowLocation(left,top,width,height,false,zindex,true,true,headerSize,borderSize);
  dialog.setButtonsVisible({close:hasCloseButton,min:false,max:false,pop:false});
  dialog.setOptions(options);
  if(isModal){
    dialog.splashDiv.style.zIndex=zindex;
  } else{
    dialog.windowDiv.style.zIndex=zindex;
  }
  if(!shade && isModal){
	  dialog.backgroundDiv.style.backgroundColor='transparent';
      dialog.backgroundDiv.isGrey=false;
  }
  if(dialog.backgroundDiv!=null)
    dialog.backgroundDiv.style.cursor=outsideShowBlockIcon ? 'not-allowed' : 'auto';
}



RootPortlet.prototype.removeChild=function(childId){
  var p=portletManager.getPortlet(childId);
  if(this.root==p){
    this.rootElement.removeChild(p.divElement);
    this.root=null;
  }else{
    var dialog=this.dialogs[childId];
    delete this.dialogs[childId];
    if(dialog.splashDiv)
      this.divElement.removeChild(dialog.splashDiv);
    else
      this.divElement.removeChild(dialog.windowDiv);
  }
}
RootPortlet.prototype.onUpdated=function(event){
};

RootPortlet.prototype.showMenu=function(x,y,menu, options){
	var that=this;
    var contextMenuPoint=(x==-1 && y==-1) ? new Point(this.owningWindow.MOUSE_POSITION_X,this.owningWindow.MOUSE_POSITION_Y) : new Point(x,y);
    this.menu=this.createMenu(menu);
    if(options != null)
    	this.menu.setOptions(options);
    this.menu.onHide=function(){that.onUserDismissedMenu()};
    if(options != null)
    	this.menu.show(contextMenuPoint, options.keepYPosition);
    else
    	this.menu.show(contextMenuPoint);
}
RootPortlet.prototype.closeMenu=function(){
	if(this.menu!= null){
	  this.menu.hideAll();
	  this.menu = null;
	}
}

RootPortlet.prototype.createMenu=function(menu){
   var that=this;
   var r=new Menu(getWindow(this.divElement));
   r.createMenu(menu, function(e,id){that.onUserMenuItem(e,id);} );
   return r;
}
RootPortlet.prototype.onUserDismissedMenu=function(e,id){
	this.menu = null;
    this.callBack('menudismissed',{});
}

RootPortlet.prototype.onUserMenuItem=function(e,id){
    this.callBack('menuitem',{id:id});
}

function closeMe(pid){
	var p=gnt(pid);
	if(p!=null)
	  p.owningWindow.close();
}

// this gets called if a panel is popped out
RootPortlet.prototype.focusMe=function(forceAfterMs){
	// first check for finsemble window
	if (this.owningWindow.finsembleWindow)
		this.owningWindow.finsembleWindow.focus();
	else 
		this.owningWindow.focus();
	var that=this;
	if(forceAfterMs>0){
	  that.gotFocus=false;
	  setTimeout(function(){
		  if(!that.gotFocus && !that.owningWindow.document.hasFocus()){
			  var windowId=that.owningWindow.windowId;
  			
	          var t=that.owningWindow.open("<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL"/>?<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.WINDOWID"/>="+(windowId),"_blank",'width='+that.owningWindow.innerWidth+',height='+that.owningWindow.innerHeight+',left='+that.lastVisibleScreenX+',top='+that.lastVisibleScreenY+',resizable=yes');
	          if(t==null){
	            this.callBack('popupFailed',{popupWindowId:windowId});
                return;
	          }
	          t.portletManager=that.owningWindow.portletManager;
	          t.titleOverride=that.owningWindow.titleOverride;
	          t.windowId=windowId;
	          t.portletManager.windows[windowId]=t;
              clearInterval(that.checkScreenLocationInterval);
              portletManager.removePortletRecursive(that.portletId);
	          that.owningWindow.close();
		  }
	  }, forceAfterMs);
	}
}

//###########################
//##### Desktop Portlet #####

function DesktopPortlet(portletId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.deskElement=nw('div');
  this.desktop=new Desktop(this.divElement);
  this.desktop.onUserClickedButton=function(e){that.callBack('showAddPortletDialog',{})};
  this.desktop.onUserDoubleclickedBackground=function(e){that.callBack('bgDoubleClicked',{})};
  
  this.desktop.onUserButton=function(id,buttonId){that.callBack('windowButton',{buttonId:buttonId,childId:id})};
  
//  this.desktop.onUserClosedWindow=function(id){that.callBack('windowClosed',{childId:id})};
  this.desktop.onUserFocusedWindow=function(id){that.callBack('windowFocus',{childId:id})};
//  this.desktop.onUserMaximizedWindow=function(id){that.callBack('windowMax',{childId:id})};
//  this.desktop.onUserMinimizedWindow=function(id){that.callBack('windowMin',{childId:id})};
  this.desktop.onUserMovedWindow=function(id,x,y,w,h){that.callBack('windowSized',{childId:id,left:x,top:y,width:w,height:h})};
  this.desktop.onUserRenamedWindow=function(id,text){that.callBack('renameWindow',{childId:id,text:text})};
}
DesktopPortlet.prototype.setDoclets=function(list){
	this.desktop.setDoclets(list);
}
DesktopPortlet.prototype.removeChild=function(id){
	this.desktop.removeChild(id);
    //portletManager.getPortlet(id).close();
}
DesktopPortlet.prototype.addChild=function(childId,title){
	this.desktop.addChild(childId,portletManager.getPortlet(childId).divElement,title);
    this.portlet.addChild(childId);
}
DesktopPortlet.prototype.setWindowLocation=function(childId,left,top,width,height,isMax,zindex,active,hasPopButton,hasMinButton,hasMaxButton,hasCloseButton,hasHeader,allowEditTitle,headerSize,borderSize,name,options){
    var innerWindow=this.desktop.getWindow(childId);
    if(innerWindow == null)
    	return;
    innerWindow.setTitle(name);
    innerWindow.setWindowLocation(left,top,width,height,isMax,zindex,active,true,headerSize,borderSize);
    innerWindow.setOptions(options);
    //innerWindow.setHasCloseMinMaxButton(hasCloseButton,hasMaxButton,hasMinButton);
    innerWindow.setButtonsVisible({hasHeader:hasHeader,close:hasCloseButton,min:hasMinButton,max:hasMaxButton,pop:hasPopButton});
    innerWindow.setHasHeader(hasHeader);
    innerWindow.setAllowEditTitle(allowEditTitle);
};

DesktopPortlet.prototype.onUpdated=function(event){
};

DesktopPortlet.prototype.setOptions=function(options){
    this.desktop.setOptions(options);
};


//###########################
//##### Grid Portlet ########

function GridPortlet(portletId){
  this.portlet=new Portlet(this,portletId,null);
  var that=this;
  this.windows=[];
};

GridPortlet.prototype.init=function(debugDepth){
	this.debugDepth=debugDepth;
};
GridPortlet.prototype.setStyle=function(cssClass,cssStyle){
	if(cssClass){
	  this.divElement.style.background='';
	  this.divElement.className=cssClass;
	}
	applyStyle(this.divElement,cssStyle);
};

var GRID_DEBUG_COLORS=["#FF0000","#00FF00","#0000FF","#FFFF00","#FF00FF","#00FFFF"];
GridPortlet.prototype.addChild=function(childId){
  var innerWindow=nw('div');
  this.windows[childId]=innerWindow;
  innerWindow.divElement=portletManager.getPortlet(childId).divElement;
  innerWindow.appendChild(innerWindow.divElement);
  this.divElement.appendChild(innerWindow);
  this.portlet.addChild(childId);
  if(this.debugDepth>-1){
    var debug=nw('div');
    debug.style.color=GRID_DEBUG_COLORS[this.debugDepth % GRID_DEBUG_COLORS.length];
    debug.style.opacity=.8;
    debug.style.border='2px solid '+debug.style.color;
    innerWindow.appendChild(debug);
    innerWindow.debug=debug;
    debug.style.pointerEvents='none';
  }
};
GridPortlet.prototype.setGridLocation=function(childId,x,y,w,h,z,style,overlayStyleCss,overlayHtml,debug){
  var innerWindow=this.windows[childId];
  innerWindow.style.left=toPx(x);
  innerWindow.style.top=toPx(y);
  innerWindow.style.width=toPx(w);
  innerWindow.style.height=toPx(h);
  innerWindow.style.zIndex=z;
  applyStyle(innerWindow,style);
  if(overlayStyleCss!=null || overlayHtml!=null){
	if(innerWindow.overlayDiv==null)
	  innerWindow.appendChild(innerWindow.overlayDiv=overlayStyleDiv=nw('div','overlay_div'));
    applyStyle(innerWindow.overlayDiv,overlayStyleCss);
    innerWindow.overlayDiv.innerHTML=overlayHtml;
  }else{
	if(innerWindow.overlayDiv!=null){
		innerWindow.removeChild(innerWindow.overlayDiv);
	    delete innerWindow.overlayDiv;
	}
  }
  if(this.debugDepth>-1){
    innerWindow.debug.style.width=innerWindow.divElement.style.width;
    innerWindow.debug.style.height=innerWindow.divElement.style.height;
    innerWindow.debug.innerHTML=debug;
    innerWindow.debug.style.fontSize='8px';
//    innerWindow.debug.style.width=toPx(fromPx(innerWindow.divElement.style.width)-this.debugDepth*2);
//    innerWindow.debug.style.height=toPx(fromPx(innerWindow.divElement.style.height)-this.debugDepth*2);
//    innerWindow.debug.style.top=toPx(this.debugDepth);
//    innerWindow.debug.style.left=toPx(this.debugDepth);
  }
};

GridPortlet.prototype.removeChild=function(childId){
  var innerWindow=this.windows[childId];
  delete this.windows[childId];
  this.divElement.removeChild(innerWindow);
  this.portlet.removeChild(childId);
};


//###############################
//##### Simple Text Portlet #####

function SimpleTextPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  this.divElement.style.background='white';
}

SimpleTextPortlet.prototype.setText = function(text){
  this.divElement.innerHTML=text;
};

//###############################
//##### Scroll Portlet #####

function ScrollPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  this.clipTop;
  this.clipLeft;
  this.innerWidth;
  this.innerHeight;
//  this.innerPortletLocation = new Rect();
  this.divElement.classList.add("scrollPortlet");
  this.scrollContainer=nw('div');
  this.scrollContainer.style.left='0px';
  this.scrollContainer.style.top='0px';
  this.scrollContainer.style.bottom='0px';
  this.scrollContainer.style.right='0px';
  
  this.innerWindow=nw('div'); 
  this.divElement.appendChild(this.scrollContainer);
  this.divElement.onmousedown=function(e){if(getMouseButton(e)==2)that.onUserContextMenu(e);};
  
  var that = this;
  this.scrollPane = new ScrollPane(this.scrollContainer, 15, this.innerWindow);
  this.scrollPane.DOM.paneElement.style.overflow="visible";
  this.scrollPane.onScroll=function(){that.onScroll()};
  
}

ScrollPortlet.prototype.innerPortletLocation;
ScrollPortlet.prototype.innerPortletId = null;
ScrollPortlet.prototype.clipLeft = 0;
ScrollPortlet.prototype.clipTop = 0;
ScrollPortlet.prototype.alignLeft = 0;
ScrollPortlet.prototype.alignTop = 0;
ScrollPortlet.prototype.width = null;
ScrollPortlet.prototype.height = null;
ScrollPortlet.prototype.innerWidth = null;
ScrollPortlet.prototype.innerHeight = null;
ScrollPortlet.prototype.gripColor = null;
ScrollPortlet.prototype.trackColor = null;
ScrollPortlet.prototype.trackButtonColor = null;
ScrollPortlet.prototype.borderColor = null;
ScrollPortlet.prototype.iconsColor = null;


ScrollPortlet.prototype.onUserContextMenu=function(e){
	if(isMouseInside(e,this.innerWindow.divElement, -0)==false){
//		this.divElement.onmousedown=function(e){if(getMouseButton(e)==2)that.onUserContextMenu(e);};
		  this.callBack('onCustomMenu',{x:this.owningWindow.MOUSE_POSITION_X,y:this.owningWindow.MOUSE_POSITION_Y});
	}
}

ScrollPortlet.prototype.removeChild=function(childId){
	if(this.innerPortletId == childId){
		this.portlet.removeChild(childId);
		if(this.innerWindow.divElement != null && this.innerWindow.divElement.parentElement == this.innerWindow)
			this.innerWindow.removeChild(this.innerWindow.divElement);
		this.innerWindow.divElement = null;
		this.innerPortletId = null;
	}
};
ScrollPortlet.prototype.addChild=function(childId){
  this.innerPortletId = childId;
  this.innerWindow.divElement=portletManager.getPortlet(childId).divElement;
  this.innerWindow.appendChild(this.innerWindow.divElement);
  this.portlet.addChild(childId);
};
ScrollPortlet.prototype.close=function(){
	if(this.innerPortletId != null)
		this.removeChild(this.innerPortletId)
}
ScrollPortlet.prototype.setInnerSize=function(width,height){
	this.innerWidth = width;
	this.innerHeight = height;
	this.innerWindow.style.width=toPx(Math.max(width,this.location.width));
  	this.innerWindow.style.height=toPx(Math.max(height,this.location.height));
	this.scrollPane.setPaneSize(width,height);
}
ScrollPortlet.prototype.setScroll=function(clipLeft,clipTop){
	if(this.clipLeft != clipLeft){
		this.clipLeft = clipLeft;
		this.scrollPane.setClipLeft(clipLeft);
	}
	if(this.clipTop != clipTop){
		this.clipTop = clipTop;
		this.scrollPane.setClipTop(clipTop);
	}
}

ScrollPortlet.prototype.setAlign=function(left,top){
	this.alignLeft = left;
	this.alignTop = top;
	this.updateAlign();
}
ScrollPortlet.prototype.updateAlign = function(){
	if(this.innerWindow.divElement){
		let innerPortlet = portletManager.getPortlet(this.innerPortletId);
		innerPortlet.left = this.alignLeft;
		innerPortlet.top = this.alignTop;
		this.innerWindow.divElement.style.left = toPx(this.alignLeft);
		this.innerWindow.divElement.style.top = toPx(this.alignTop);
	}
}
ScrollPortlet.prototype.setSize=function(width,height){
  this.location.left=this.left;
  this.location.top=this.top;
  this.location.width=width;
  this.location.height=height;
  this.location.writeToElement(this.divElement);
  this.scrollPane.setLocation(this.location.left,this.location.top, this.location.width,this.location.height);
}

ScrollPortlet.prototype.onScroll=function(){
	this.scrollPane.DOM.innerpaneElement.style.top=toPx(-this.scrollPane.vscroll.getClipTop());
	this.scrollPane.DOM.innerpaneElement.style.left=toPx(-this.scrollPane.hscroll.getClipTop());
	if((this.clipTop == this.scrollPane.getClipTop()) && (this.clipLeft == this.scrollPane.getClipLeft()))
		return;
//	portletManager.onUserSpecialScroll(this.formDOMManager.scrollPane.getClipLeft(), this.formDOMManager.scrollPane.getClipTop(), this.portlet.divElement.id);
//	let xt = this.scrollPane.getClipTop() - this.clipTop;
//	let xl = this.scrollPane.getClipLeft() - this.clipLeft;
	this.clipTop = this.scrollPane.getClipTop();
	this.clipLeft = this.scrollPane.getClipLeft();
	
    this.callBack('onscroll',{"t":parseInt(this.clipTop), "l":parseInt(this.clipLeft)});
	
}

ScrollPortlet.prototype.handleWheel=function(e){
	if(isMouseInside(e,this.innerWindow.divElement, -0)==false){
		this.scrollPane.handleWheel(e);
	}
	else{
		if(this.innerPortletId != null){
			var innerPortlet = portletManager.getPortlet(this.innerPortletId);
			innerPortlet.handleWheel(e);

		}
	}
}


ScrollPortlet.prototype.setOptions=function(values){
	if(values.hasOwnProperty("bgcl")){
		var bgColor = values["bgcl"];
		this.scrollPane.DOM.paneElement.style.backgroundColor=bgColor;
	}
	if(values.hasOwnProperty("gripcl")){
		this.gripColor = values["gripcl"];
	}
	if(values.hasOwnProperty("trackcl")){
		this.trackColor = values["trackcl"];
	}
	if(values.hasOwnProperty("btncl")){
		this.trackButtonColor = values["btncl"];
	}
	if(values.hasOwnProperty("iconscl")){
		this.iconsColor = values["iconscl"];
	}
	if(values.hasOwnProperty("bdrcl")){
		this.borderColor = values["bdrcl"];
	}
	
	if(values.hasOwnProperty("cornercl")){
		this.cornerColor = values["cornercl"];
	}
	if(values.hasOwnProperty("hideAw")){
		this.hideArrow = values["hideAw"];
	}
	
	if(values.hasOwnProperty("borderRad")){
		this.borderRadius= values["borderRad"];
	}
	
	this.scrollPane.hscroll.DOM.applyColors(this.gripColor, this.trackColor, this.trackButtonColor, this.borderColor, this.cornerColor);
	this.scrollPane.vscroll.DOM.applyColors(this.gripColor, this.trackColor, this.trackButtonColor, this.borderColor, this.cornerColor);
	if (this.hideArrow != null) {
		this.scrollPane.hscroll.DOM.hideArrows(this.hideArrow);
		this.scrollPane.vscroll.DOM.hideArrows(this.hideArrow);
	}
	this.scrollPane.updateScroll();
	if (this.borderRadius != null) {
		this.scrollPane.hscroll.DOM.applyBorderRadius(this.borderRadius);
		this.scrollPane.vscroll.DOM.applyBorderRadius(this.borderRadius);
	}
	
	this.scrollPane.updateIconsColor(this.iconsColor);
}
ScrollPortlet.prototype.setScrollBarWidth=function(width){
	if(width != null)
		this.scrollPane.setSize(width);

}
ScrollPortlet.prototype.setCssStyle=function(cssStyle){
  applyStyle(this.divElement,cssStyle);
}

//###############################
//##### HTML Portlet #####

function HtmlPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  var that=this;
  this.htmlPortlet=nw('div');
  this.htmlPortlet.style.left='0px';
  this.htmlPortlet.style.top='0px';
  this.htmlPortlet.style.right='0px';
  this.htmlPortlet.style.bottom='0px';
  this.divElement.appendChild(this.htmlPortlet);
  this.htmlPortlet.ondblclick=function(){that.callBack('click',{})};
  this.htmlPortlet.callback=function(event,id){
      point=getMousePoint(event).move(-4,-4);
      var m = {};
      for(var i = 1; i < parseInt(arguments.length/2); i++){
    	  m[arguments[i*2]] = arguments[i*2+1];
      }
      m.id=id;
      m.mouseX=point.x;
      m.mouseY=point.y;
	  that.callBack('callback',m)
  };
  this.htmlPortlet.portlet=that;
  this.portlet.divElement.customCb=function(type,params){that.callBack('customCallback',{customType:type,customParams:params});};
  //this.divElement.style.overflow='auto';
}

HtmlPortlet.prototype.setInnerHTML=function(html){
  this.htmlPortlet.innerHTML=html;
};


HtmlPortlet.prototype.scrollToBottom=function(){
  this.htmlPortlet.scrollTop = this.htmlPortlet.scrollHeight;
}

HtmlPortlet.prototype.getHtmlDiv=function(){
	return this.htmlPortlet;
}

HtmlPortlet.prototype.setCssClass=function(cssClass){
  this.htmlPortlet.className=cssClass;
};
HtmlPortlet.prototype.setCssStyle=function(cssStyle){
  applyStyle(this.htmlPortlet,cssStyle);
};

HtmlPortlet.prototype.setSupportsContextMenu=function(support){
	var that=this;
	if(support){
	  this.htmlPortlet.onclick=function(e){that.onUserContextMenu(e)};
	}else{
	  this.htmlPortlet.onclick=null;
	}
}
HtmlPortlet.prototype.onUserContextMenu=function(e){
    this.contextMenuPoint=getMousePoint(e).move(-4,-4);
	this.callBack('showmenu',{});
}
HtmlPortlet.prototype.showMenu=function(menu){
    this.createMenu(menu).show(this.contextMenuPoint);
}

HtmlPortlet.prototype.createMenu=function(menu){
   var that=this;
   var r=new Menu(getWindow(this.divElement));
   r.createMenu(menu, function(e,id){that.onUserMenuItem(e, id);} );
   return r;
}
HtmlPortlet.prototype.onUserMenuItem=function(e,id){
    this.callBack('menuitem',{id:id});
}


//###############################
//##### Jsp Portlet #####

function JspPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
}

//###############################
//##### Divider Portlet #####

function DividerPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  var that=this;
  this.isContainer=true;
  this.divider=nw('div');
  // swap is replaced by flip panel
  // this button has no width/height thus non-interactable
//  this.swapButton=nw('div');
  this.first=nw('div');
  this.second=nw('div');
//  this.swapButton.style.position='absolute';
  this.first.style.overflow="hidden";
  this.second.style.overflow="hidden";
  
//  this.swapButton.onclick=function(e){that.onSwap(e);};
  this.divElement.appendChild(this.first);
  this.divElement.appendChild(this.second);
//  this.divElement.appendChild(this.swapButton);
  this.divElement.appendChild(this.divider);
  this.divElement.onmousedown=function(e){if(getMouseButton(e)==2)that.onUserContextMenu(e);};
  
}

DividerPortlet.prototype.onUserContextMenu=function(e){
	if(isMouseInside(e,this.divider, -0)==true){
		  this.callBack('onCustomMenu',{x:this.owningWindow.MOUSE_POSITION_X,y:this.owningWindow.MOUSE_POSITION_Y});
	}
}

DividerPortlet.prototype.init=function(vertical,thickness,lock,bgColor,hoverColor){
  var that=this;
  this.thickness=thickness;
  this.vertical=vertical;
  this.lock=lock;
  this.bgColor=bgColor;
//  if(this.divider!=null)
//    this.divElement.removeChild(this.divider);
  this.divider.style.backgroundColor=this.bgColor;
  this.divider.className=this.vertical ? 'portlet_divider_v' : 'portlet_divider_h';
  if(!lock){
    this.divElement.ondblclick=null;
//    this.swapButton.className=this.vertical ? 'portlet_divider_vswap' : 'portlet_divider_hswap';
    if(this.dividerDragger==null){
      this.dividerDragger=nw('div');
      this.divElement.appendChild(this.dividerDragger);
      var dd=this.dividerDragger;
      this.dividerDragger.ondraggingEnd=function(e,x,y){that.OnUserMovedDivider(e,x,y);};
      this.dividerDragger.ondblclick=function(e){that.onUsrDblClick(e);};
      this.dividerDragger.ondragging=function(e,x,y){that.OnUserMovingDivider(e,x,y);};
      this.dividerDragger.onmouseleave=function(e){ dd.style.background="none"; };
    }
    this.dividerDragger.onmouseenter=function(e){ that.dividerDragger.style.background=hoverColor; };
    makeDraggable(this.dividerDragger,this.dividerDragger,!vertical,vertical);
    if(this.vertical){
      this.dividerDragger.style.left=null;
      this.dividerDragger.style.right=null;
      this.dividerDragger.style.height=null;
      this.dividerDragger.style.top='0px';
      this.dividerDragger.style.bottom='0px';
      this.dividerDragger.style.width=toPx(Math.max(thickness,4));
      this.dividerDragger.className='portlet_divider_v_dragger';
    }else{
      this.dividerDragger.style.top=null;
      this.dividerDragger.style.bottom=null;
      this.dividerDragger.style.width=null;
      this.dividerDragger.style.left='0px';
      this.dividerDragger.style.right='0px';
      this.dividerDragger.style.height=toPx(Math.max(thickness,4));
      this.dividerDragger.className='portlet_divider_h_dragger';
    }
  }else{
    if(this.divider.ondblclick==null)
       this.divider.ondblclick=function(e){that.onUsrDblClick(e);};
    if(this.dividerDragger!=null){
      this.divElement.removeChild(this.dividerDragger);
      this.dividerDragger=null;
    }
  }
  if(this.vertical){
    this.divider.style.left=null;
    this.divider.style.right=null;
    this.divider.style.height=null;
    this.divider.style.top='0px';
    this.divider.style.bottom='0px';
    this.divider.style.width=toPx(thickness);
  }else{
    this.divider.style.top=null;
    this.divider.style.bottom=null;
    this.divider.style.width=null;
    this.divider.style.left='0px';
    this.divider.style.right='0px';
    this.divider.style.height=toPx(thickness);
  }
  this.divider.style.backgroundColor=bgColor;
  this.first.style.top='0px';
  this.first.style.left='0px';
  this.first.style.bottom='0px';
  this.first.style.right='0px';
  this.second.style.top='0px';
  this.second.style.left='0px';
  this.second.style.bottom='0px';
  this.second.style.right='0px';
  this.dividerMovingInterval=null;
  this.dividerMovingOffset=null;
  this.dividerMovingProcessed=false;
  this.setDividerLocation(this.cleanOffset(this.offset));
};

DividerPortlet.prototype.onDividerMovingProcessed=function(){
    this.dividerMovingProcessed=true;
}
DividerPortlet.prototype.onUsrDblClick=function(e){
    this.callBack("onUsrDblClick",{});
}

DividerPortlet.prototype.OnUserMovedDivider=function(e,x,y){
	// return if there is no change
	if (!x && !y) return; 
  if(this.dividerMovingInterval!=null){
	window.clearInterval(this.dividerMovingInterval);
    this.dividerMovingInterval=null;
  }
  var loc=new Rect().readFromElementRelatedToParent(this.dividerDragger);
  var offset= ( this.vertical ? loc.left : loc.top ) ;
  var divRect = this.divElement.getBoundingClientRect();
  var size = ( this.vertical? divRect.width : divRect.height ) - 1;
  offset = max(offset, 0);
  offset = min(offset, size-this.thickness); 
  this.callBack('dividerMoved',{offset:offset});
};
DividerPortlet.prototype.checkMovementInterval=function(){
    if(!this.dividerMovingProcessed)
    	return;
	if(this.offset==this.dividerMovingOffset)
		return;
	this.dividerMovingOffset=this.offset;
    this.callBack('dividerMoving',{offset:this.offset});
    this.dividerMovingProcessed=false;
}

DividerPortlet.prototype.OnUserMovingDivider=function(e,x,y){
  // Get the current location of the divider;
  var loc=new Rect().readFromElementRelatedToParent(this.dividerDragger);
  
  // get the offset either left or top
  // Massage offset into the bounds;
  var offset=this.vertical ? loc.left : loc.top;
  offset = this.cleanOffset(offset);
  
  // return early if it hasn't changed
  if(this.offset==offset)
	  return;
  
  //callback for what?...
  if(this.dividerMovingInterval==null){
	var that=this;
    this.callBack('dividerMovingStarted',{offset:offset});
    this.dividerMovingProcessed=false;
	this.dividerMovingOffset=offset;
	var func=function(){that.checkMovementInterval();}
	this.dividerMovingInterval=window.setInterval(func,50);
  }
  this.moveDividerLocation(offset);
};

//DividerPortlet.prototype.onSwap=function(e){
//  this.callBack('swap',{});
//};

DividerPortlet.prototype.cleanOffset=function(offset){
  var ploc=this.portlet.location;
  if(offset<0)
    offset=0;
  else if(!this.vertical && offset>ploc.height-this.thickness)
    offset=ploc.height-this.thickness;
  else if(this.vertical && offset>ploc.width-this.thickness)
    offset=ploc.width-this.thickness;
  return offset;
}

DividerPortlet.prototype.moveDividerLocation=function(offset){
  var loc=this.portlet.location;
  this.offset=offset;
  
  var pxOffset = toPx(offset);
  //Do the vertical or the horizontal 
  if(this.vertical){
    this.divider.style.left=pxOffset;
    if(this.dividerDragger!=null)
      this.dividerDragger.style.left=pxOffset;
//    this.swapButton.style.left=pxOffset;
    
    this.first.style.width=pxOffset;
    
    this.second.style.left=toPx(offset+this.thickness);
    this.second.style.width=toPx(loc.width-offset-this.thickness);
  }else{
    this.divider.style.top=pxOffset
    if(this.dividerDragger!=null)
      this.dividerDragger.style.top=pxOffset;
//    this.swapButton.style.top=pxOffset;
    
    this.first.style.height=pxOffset;
    
    this.second.style.top=toPx(offset+this.thickness);
    this.second.style.height=toPx(loc.height-offset-this.thickness);
  }
  if(this.onDividerLocation)
	  this.onDividerLocation(offset)
	
}

DividerPortlet.prototype.setDividerLocation=function(offset){
  var loc=this.portlet.location;
  this.offset=offset;
  
  var pxOffset = toPx(offset);
  //Do the vertical or the horizontal 
  if(this.vertical){
    this.divider.style.left=pxOffset;
    if(this.dividerDragger!=null)
      this.dividerDragger.style.left=pxOffset;
//    this.swapButton.style.left=pxOffset;
    
    this.first.style.width=pxOffset;
    
    this.second.style.left=toPx(offset+this.thickness);
    this.second.style.width=toPx(loc.width-offset-this.thickness);
    
    this.first.style.height=toPx(loc.height);
    this.second.style.height=toPx(loc.height);
  }else{
    this.divider.style.top=pxOffset;
    if(this.dividerDragger!=null)
      this.dividerDragger.style.top=pxOffset;
//    this.swapButton.style.top=pxOffset;
    
    this.first.style.height=pxOffset;
    
    this.second.style.top=toPx(offset+this.thickness);
    this.second.style.height=toPx(loc.height-offset-this.thickness);
    
    this.first.style.width=toPx(loc.width);
    this.second.style.width=toPx(loc.width);
  }
  if(this.onDividerLocation)
	  this.onDividerLocation(offset)
};

DividerPortlet.prototype.addChild=function(loc,childId){
  if(loc==1)
    this.first.appendChild(portletManager.getPortlet(childId).divElement);
  else
    this.second.appendChild(portletManager.getPortlet(childId).divElement);
  this.portlet.addChild(childId);
};

//###############################
//##### Color Picker #####


function ColorPickerPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  this.windows=[];
  this.childIdsToPositions={};
  this.dividers=[];
  var that=this;
  this.minSize=10;
  this.isContainer=true;
  this.divElement.style.background='white';
}

ColorPickerPortlet.prototype.init=function(defaultColor,color,alpha){
  var that=this;
  this.colorPicker=new ColorPicker(false,color,this.divElement,false);
  if(alpha)
	  this.colorPicker.setAlphaEnabled();
  this.colorPicker.onOk=function(){that.onColorPickerOk()};
  this.colorPicker.onCancel=function(){that.onColorPickerCancel()};
  this.colorPicker.onColorChanged=function(color){that.onColorChanged(color)};
  this.colorPicker.onNoColor=function(){that.onColorPickerNoColor()};
  this.colorPicker.show(0,0);
}
ColorPickerPortlet.prototype.addColorChoice=function(choice){
  this.colorPicker.addColorChoice(choice);
}

ColorPickerPortlet.prototype.onColorPickerOk=function(){
    this.callBack('onOkay',{});
}
ColorPickerPortlet.prototype.onColorChanged=function(color){
    this.callBack('onColorChanged',{color:this.colorPicker.getColor()});
}
ColorPickerPortlet.prototype.onColorPickerCancel=function(){
    this.callBack('onCancel',{});
}
ColorPickerPortlet.prototype.onColorPickerNoColor=function(){
	this.colorPicker.values.color=null;
    this.callBack('onNoColor',{});
}


//###############################
//##### Multi Divider Portlet #####

function MultiDividerPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  this.windows=[];
  this.childIdsToPositions={};
  this.dividers=[];
  this.dividerDraggers=[];
  var that=this;
  this.minSize=10;
  this.isContainer=true;
  this.divElement.style.background='white';
}
MultiDividerPortlet.prototype.reset=function(){
  this.windows=[];
  this.childIdsToPositions={};
  this.dividers=[];
  this.dividerDraggers=[];
  removeAllChildren(this.divElement);
}
MultiDividerPortlet.prototype.addChild=function(pos,childId){
  var window=nw('div','fa');
  //var pos=this.windows.length;
  this.windows[pos]=window;
  window.style.overflow="hidden";
  this.childIdsToPositions[childId]=pos;
  window.appendChild(portletManager.getPortlet(childId).divElement);
  this.divElement.insertBefore(window,this.divElement.firstChild);
  this.portlet.addChild(childId);
  if(pos>0){
    var divider=nw('div');
    var dividerDragger = nw('div');
    divider.className=this.vertical ? 'portlet_divider_v' : 'portlet_divider_h';
    dividerDragger.className=this.vertical ? 'portlet_divider_v_dragger' : 'portlet_divider_h_dragger';
    dividerDragger.style.opacity = 0;
    this.vertical == true ?	dividerDragger.style.width=toPx(9):dividerDragger.style.height=toPx(4);
    this.dividers[pos-1]=divider;
    this.dividerDraggers[pos-1]=dividerDragger;
    divider.pos=pos;
    dividerDragger.pos=pos;
    var that=this;
    makeDraggable(dividerDragger,null,!this.vertical,this.vertical);
    dividerDragger.ondraggingEnd=function(e,x,y){that.OnUserMovedDivider(e,x,y);};
    dividerDragger.ondragging=function(e,x,y){that.OnUserMovingDivider(e,x,y);};
    this.divElement.appendChild(divider);
    this.divElement.appendChild(dividerDragger);
  }
};

MultiDividerPortlet.prototype.OnUserMovedDivider=function(e,x,y){
	var pos=this.determineDivPos(e.pos,x,y);
	this.applyPositions(pos,false,true);
    this.callBack('dividerMoved',{positions:pos.join(',')});
}

MultiDividerPortlet.prototype.OnUserMovingDivider=function(e,x,y){
	var pos=this.determineDivPos(e.pos,x,y);
	this.applyPositions(pos,false,true);
}


MultiDividerPortlet.prototype.determineDivPos=function(pos,x,y){
	var delta=this.vertical ? x : y;
	var clone=this.positions.slice(0);
	var offset=clone[pos]+delta;
	var spacing=this.thickness+this.minSize;
	if(delta>0){
		var max=(this.vertical ? this.location.width : this.location.height)-spacing*(clone.length-pos)+this.thickness;
		if(offset>max)
			offset=Math.max(max,clone[pos]);
		while(clone[pos]<offset){
			clone[pos++]=offset;
			offset+=spacing;
		}
	}else{
		var min=spacing*pos;
		if(offset<min)
			offset=Math.min(min,clone[pos]);
		while(clone[pos]>offset){
			clone[pos--]=offset;
			offset-=spacing;
		}
	}
	return clone;
}


MultiDividerPortlet.prototype.removeChild=function(childId){
  var pos=this.childIdsToPositions[childId];
  delete this.childIdsToPositions[childId];
  var window=this.windows[pos];
  delete this.windows[childId];
  this.divElement.removeChild(window);
  this.portlet.removeChild(childId);
  if(this.pos>0){
    var divider=this.dividers[pos-1];
    delete this.dividers[pos-1];
    this.divElement.removeChild(divider);
    var dividerDragger = this.dividerDraggers[pos-1];
    delete this.dividerDraggers[pos-1];
    this.divElement.removeChild(dividerDragger);
  }
};
MultiDividerPortlet.prototype.setPositions=function(positions){
	this.positions=positions;
	this.applyPositions(this.positions,true,true);
}

MultiDividerPortlet.prototype.applyPositions=function(positions,includePanels,includeDividers){
	if(this.vertical){
	  for(var i=0;i<positions.length;i++){
		  if(includePanels){
		    var window=this.windows[i];
		    window.style.top="0px";
		    window.style.bottom="0px";
		    window.style.left=toPx(positions[i]);
		    if(i==positions.length-1){
		      window.style.right="0px";
		    }else{
		      window.style.width=toPx(positions[i+1]-positions[i]-this.thickness);
		    }
		  }
		  if(i>0 && includeDividers){
			var divider=this.dividers[i-1];
		    divider.style.top="0px";
		    divider.style.bottom="0px";
		    divider.style.left=toPx(positions[i]-this.thickness);
		    divider.style.width=toPx(this.thickness);
		    var dividerDragger = this.dividerDraggers[i-1];
		    dividerDragger.style.top="0px";
		    dividerDragger.style.bottom="0px";
		    dividerDragger.style.left=toPx(positions[i]-this.thickness);
            if(this.color!=null)
              divider.style.background=this.color;
		  }
	  }
	}else{
	  for(var i=0;i<positions.length;i++){
		  if(includePanels){
		    var window=this.windows[i];
		    window.style.left="0px";
		    window.style.right="0px";
		    window.style.top=toPx(positions[i]);
		    if(i==positions.length-1){
		      window.style.bottom="0px";
		    }else{
		      window.style.height=toPx(positions[i+1]-positions[i]-this.thickness);
		    }
		  }
		  if(i>0 && includeDividers){
			var divider=this.dividers[i-1];
		    divider.style.left="0px";
		    divider.style.right="0px";
		    divider.style.top=toPx(positions[i]-this.thickness);
		    divider.style.height=toPx(this.thickness);
		    var dividerDragger = this.dividerDraggers[i-1];
		    dividerDragger.style.left="0px";
		    dividerDragger.style.right="0px";
		    dividerDragger.style.top=toPx(positions[i]-this.thickness);
            if(this.color!=null)
              divider.style.background=this.color;
		  }
	  }
	}
}

MultiDividerPortlet.prototype.init=function(vertical,thickness,lock,color,minSize){
  var that=this;
  this.thickness=thickness;
  this.vertical=vertical;
  this.lock=lock;
  this.color=color;
  this.minSize=minSize;
  this.reset();
};


//#######################
//##### Tab Portlet #####

function TabPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  var that=this;
  this.tabs=new Tabs(this.portlet,parentId);
//  this.portlet=this.tabs.portlet;
  this.tabs.callBack=function(action,params){that.callBack(action,params);};
	
}

TabPortlet.prototype.initTabs=function(isAWTP,allowAdd,allowMenu,addButtonText,bgColor,selectedColor,unselectedColor,selectTextColor,unselectTextColor,selectShadow,unselectShadow, isHidden,tabHeight,
		tabPaddingTop,tabPaddingBottom, tabSpacing,fontSize, beginningPadding,isFarAligned,isBottom,isVertical, leftRadius,rightRadius,menuArrowColor, menuArrowSize, tabFloatSize,tabPaddingStart,
		hasAddButton, addButtonColor,borderColor,fontFamily,selBorderColor,selBorderSize,hideArrangeTabs){ 
	this.tabs.initTabs(isAWTP,allowAdd,allowMenu,addButtonText,bgColor,selectedColor,unselectedColor,selectTextColor,unselectTextColor,selectShadow,unselectShadow, isHidden,tabHeight,
			tabPaddingTop,tabPaddingBottom,tabSpacing,fontSize,beginningPadding,isFarAligned,isBottom,isVertical, leftRadius,rightRadius,menuArrowColor, menuArrowSize,tabFloatSize, tabPaddingStart,
			hasAddButton,addButtonColor,borderColor,fontFamily,selBorderColor,selBorderSize,hideArrangeTabs);
}

TabPortlet.prototype.updatePosition=function(){
	this.tabs.updatePosition();
}
TabPortlet.prototype.getTabs=function(){
	return this.tabs;
}
TabPortlet.prototype.setSize=function(width,height){
	this.tabs.setSize(width,height);
}
TabPortlet.prototype.rp=function(){
	this.tabs.rp();
}
TabPortlet.prototype.focusTab=function(){
	this.tabs.focusTab();
}
TabPortlet.prototype.setActiveTab=function(tabIndex){
	this.tabs.setActiveTab(tabIndex);
}
TabPortlet.prototype.addTab=function(location,tabName, tabNameHtml,allowTitleEdit,selectColor,unselectColor,selectTextColor,unselectTextColor,isHidden,blinkColor,blinkPeriod,his){
	this.tabs.addTab(location,tabName, tabNameHtml,allowTitleEdit,selectColor,unselectColor,selectTextColor,unselectTextColor,isHidden,blinkColor,blinkPeriod,his);
}
TabPortlet.prototype.addChild=function(childId){
	this.tabs.addChild(childId);
}

TabPortlet.prototype.handleWheel=function(e){
	this.tabs.scrollPane.handleWheel(e);
}
//#########################
//##### Table Portlet #####

function TablePortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
}

//#########################
//##### FastTable Portlet #####

function FastTablePortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  this.headerHeight=20;
  this.headerHeight=0;
  this.tableDiv=nw("div");
  this.headerDiv=nw("div","table_search");
  this.titleDiv=nw("div","table_title");
  this.titleSpan=nw("span","table_title_span");
  this.searchInput=nw("input","table_search_input");
  //MOBILE SUPPORT - enables enter key in mobile to be pressed instead of tabbing to next field for search
  this.searchInput.setAttribute('enterkeyhint', 'go');    	
  this.searchInputButton=nw("div","table_search_button");
  makeEnterable(this.searchInput,this.searchInputButton);
  
  this.downloadInputButton=nw("div","table_download_button");
	  
  var that=this;
  this.table = new FastTable(this.tableDiv);
  this.table.portlet=this;
  this.searchInputButton.onclick=function(e){that.onUserSearch();};
  this.downloadInputButton.onclick=function(e){that.onUserDownload();};
  this.table.fetchdata=function(i,j,table){return that.onTableFetchData(i,j,table);};
  this.table.onUpdateCellsEnd=function(table){that.onTableUpdateCellsEnd(table);};
  this.table.onUserSort=function(e,col,type){that.onUserSort(e,col,type);};
  this.table.onUserColumnResize=function(e,col,width){that.onUserColumnResize(e,col,width);};
  this.table.onUserShowFilter=function(e,col){that.onUserShowFilter(e,col);};
  this.table.onUserFiltered=function(e,col,values){that.onUserFiltered(e,col,values);};
  this.table.onUserSelected=function(e,activeRow,selectedRows,userSeqnum){that.onUserSelected(e,activeRow,selectedRows,userSeqnum);};
  this.table.onUserNavigate=function(e,activeRow,action,text){that.onUserNavigate(e,activeRow,action,text);};
  this.table.onUserArrangedColumns=function(e,col,values){that.onUserArrangedColumns(e,col,values);};
  this.table.onUserContextMenu=function(e){that.onUserContextMenu(e);};
  this.table.onUserHeaderMenu=function(e,col){that.onUserHeaderMenu(e,col);};
  this.table.onUserContextMenuItem=function(e,id){that.onUserContextMenuItem(e,id);};
  this.table.onUserHeaderMenuItem=function(e,id,col){that.onUserHeaderMenuItem(e,id,col);};
  this.table.onUserHelp=function(e){that.onUserHelp(e);};
  this.table.onHover=function(x,y){that.onHover(x,y);};
  this.table.callback=function(method,args){that.callBack(method,args);};
  this.divElement.style.background='white';
  this.divElement.appendChild(this.tableDiv);
  this.divElement.appendChild(this.headerDiv);
  this.headerDiv.appendChild(this.titleDiv);
  this.headerDiv.appendChild(this.downloadInputButton);
  this.headerDiv.appendChild(this.searchInput);
  this.headerDiv.appendChild(this.searchInputButton);
  this.titleDiv.appendChild(this.titleSpan);
//  this.currentUpperRowVisible=-1;
//  this.currentLowerRowVisible=-1;
	this.table.menuHeight = this.headerHeight;
	this.headerDiv.style.height=toPx(this.headerHeight);
	this.enableEditing=true;
	this.fastTableEditor = null;
}

//Message structure:
//m: list of cells
//Cell structure:
//x x cell pos
//y y cell pos
//v value
//o option
FastTablePortlet.prototype.editRowsComplete=function(){
	if(this.fastTableEditor){
		this.fastTableEditor.deactivate();
	}
}
FastTablePortlet.prototype.setAutocomplete=function(col,vals){
    this.table.setAutocomplete(col,vals);
}
FastTablePortlet.prototype.editRows=function(m){
	if(this.enableEditing == true){
		if(this.fastTableEditor == null){
			this.fastTableEditor = new FastTableEditor(this); 
		}
		this.fastTableEditor.edit(m);
	}
}

FastTablePortlet.prototype.handleWheel=function(e){
	this.table.scrollPane.handleWheel(e);
}

FastTablePortlet.prototype.handleDblclick=function(e){
	//Checks if mouse is inside any of the visible cells 
	if(isMouseInside(e,this.table.scrollPane.DOM.paneElement, -0)){
		this.onUserDblclick(e, {action:"callRelationship"});
	}
}

FastTablePortlet.prototype.handleKeydown=function(e, _target){
	if(e.target != null && e.target.isQuickColumnFilterElement == true){
		return;
	}
	if(this.fastTableEditor && this.fastTableEditor.active == true){
		this.fastTableEditor.handleKeydown(e,_target);
		return;
	}
	if (_target.nodeName == "INPUT") {
		if (e.target == this.searchInput && e.key == "Escape") {
			this.setSearch(null);
			this.onUserSearch();
		}
		return;
	}
	var shiftKey = e.shiftKey;
	var ctrlKey = e.ctrlKey;
	var altKey = e.altKey;
	switch(e.key){	
	case " ":
		this.table.jumpToNextUniqueCol(e);
		break;
	case "ArrowLeft":
		this.callBack('ArrowLeft',{left:this.table.scrollPane.getClipLeft()});
		break;
	case "ArrowRight":
		this.callBack('ArrowRight',{left:this.table.scrollPane.getClipLeft()});
		break;
	case "ArrowDown":
		this.table.scroll(e,1);
		break;
	case "ArrowUp":
		this.table.scroll(e,-1);
		break;
	case "PageDown":
		this.table.scroll(e,this.table.visibleRowsCount);
		break;
	case "PageUp":
		this.table.scroll(e,-this.table.visibleRowsCount);
		break;
	case "Escape":
		this.table.clearSelected();
		break;
	case "Enter":
		this.table.nextColWithText(e);
		break;
	case "c":
	case "C":
		if(ctrlKey){
			this.table.openCopyMenu(e);
		}
		break;
	case "a":
	case "A":
		if(ctrlKey){
			this.table.selectAll();
		}
		break;
	default:
		break;	
	}	
};

FastTablePortlet.prototype.handleKeypress=function(e, _target){
	if(this.fastTableEditor && this.fastTableEditor.active == true){
		return;
	}
	this.table.jumpToColWithText(e);
};

FastTablePortlet.prototype.ensureRowVisible=function(row){
	this.table.ensureRowVisible(row);
}
FastTablePortlet.prototype.setOptions=function(options){
	if(options.menuBarHidden=="true"){
		this.headerHeight=0;
		this.menuBarHidden=true;
		this.headerDiv.style.visibility="hidden";
	} else{
		this.headerHeight=20;
		this.menuBarHidden=false;
		this.headerDiv.style.visibility="visible";
	}
	this.table.menuHeight=this.headerHeight;
	this.headerDiv.style.height=toPx(this.headerHeight);
	this.headerDiv.style.borderColor=options.searchBarDivColor;
	this.table.height = this.height - this.headerHeight;
	this.table.setOptions(options);
	this.searchInput.style.backgroundColor=options.searchBarColor;
	this.searchInput.style.color=options.searchBarFontColor;
	this.searchInput.style.borderColor=options.searchFieldBorderColor;
	this.searchFieldBackgroundInactiveColor = options.searchBarColor;
	this.searchFieldFontInactiveColor = options.searchBarFontColor;
	this.searchFieldBackgroundActiveColor = options.filteredColumnBgColor;
	this.searchFieldFontActiveColor = options.filteredColumnFontColor;
	searchButtonsColor=options.searchButtonsColor || '#ffffff';
  	var searchButtonSvgStyle=searchButtonsColor+';stroke-width:14;stroke-miterlimit:10;" cx="41.3" cy="41.2" r="24.7"/> <line style="fill:none;stroke:'+searchButtonsColor;
  	var downloadButtonSvgStyle=searchButtonsColor+';stroke-width:10;stroke-miterlimit:10;" x1="8" y1="91" x2="92" y2="91"/> <g> <line style="fill:none;stroke:'+searchButtonsColor+';stroke-width:12;stroke-miterlimit:10;" x1="50" y1="8" x2="50" y2="71"/> <polyline style="fill:none;stroke:'+searchButtonsColor;
  	searchButtonSvgStyle=searchButtonSvgStyle.replace(/#/g, "%23");
  	downloadButtonSvgStyle=downloadButtonSvgStyle.replace(/#/g, "%23");
  	this.searchInputButton.style.backgroundImage=(SEARCH_BUTTON_IMAGE_PREFIX+searchButtonSvgStyle+SEARCH_BUTTON_IMAGE_SUFFIX);
  	this.downloadInputButton.style.backgroundImage=(DOWNLOAD_BUTTON_IMAGE_PREFIX+downloadButtonSvgStyle+DOWNLOAD_BUTTON_IMAGE_SUFFIX);
	this.titleDiv.style.backgroundColor=options.titleBarColor;
	this.titleDiv.style.color=options.titleBarFontColor;	
	this.titleDiv.style.fontFamily=options.fontFamily || "arial";	
//		this.setSize(this.width,this.height);
}

FastTablePortlet.prototype.setTitle=function(title,isFiltered){
	//possible bug? isFiltered is false when quick column filter is active
	this.titleSpan.innerHTML=title;
	/* title should have the filtered header color if filter is set
	if(isFiltered)
		this.titleSpan.style.color = this.table.filteredColumnBgColor;
	else
		this.titleSpan.style.color = this.titleDiv.style.color;
	*/
};
FastTablePortlet.prototype.setSearch=function(expression){
	this.searchInput.value=expression;
	if(expression) {
		this.searchInput.style.backgroundColor = this.searchFieldBackgroundActiveColor;
		this.searchInput.style.color = this.searchFieldFontActiveColor;
	} else {
		this.searchInput.style.backgroundColor = this.searchFieldBackgroundInactiveColor;
		this.searchInput.style.color = this.searchFieldFontInactiveColor;
	}
};

FastTablePortlet.prototype.onUserDblclick=function(e, data){
	this.callBack('userDblclick', data);
}

FastTablePortlet.prototype.onUserShowFilter=function(e,col){
    this.callBack('showFilterDialog',{columnIndex:col});
};
FastTablePortlet.prototype.onUserContextMenu=function(e,col){
	this.table.curseqnum=nextSendSeqnum;
    this.callBack('showMenu',{});
};
FastTablePortlet.prototype.onUserHeaderMenu=function(e,col){
//	console.log(e);
    this.callBack('showHeaderMenu',{col:col});
};
FastTablePortlet.prototype.onUserContextMenuItem=function(e,id){
    this.callBack('menuitem',{action:id});
};
FastTablePortlet.prototype.onUserHeaderMenuItem=function(e,id,col){
    this.callBack('headerMenuitem',{action:id,col:col});
};

FastTablePortlet.prototype.onUserHelp=function(e,col){
    this.callBack('showhelp',{columnIndex:col});
};
FastTablePortlet.prototype.onUserFiltered=function(e,col,values){
    this.callBack('filter',{columnIndex:col,values:values});
};
FastTablePortlet.prototype.onUserSelected=function(e,activeRow,selectedRows,userSeqnum){
    this.callBack('userSelect',{activeRow:activeRow,selectedRows:selectedRows,userSeqnum:userSeqnum});
};
FastTablePortlet.prototype.onUserNavigate=function(e,activeRow,action,text){
    this.callBack('userNavigate',{activeRow:activeRow,action:action,text:text});
};
FastTablePortlet.prototype.onUserArrangedColumns=function(e,col,columns){
    this.callBack('columns',{columns:columns});
};
FastTablePortlet.prototype.onUserSearch=function(){
    //portletManager.showWait("Searching");
	var expression=this.searchInput.value;
	if(expression){
		// this.searchInput.className="table_search_input_active";
		this.searchInput.style.backgroundColor=this.searchFieldBackgroundActiveColor;
		this.searchInput.style.color=this.searchFieldFontActiveColor;
	}
	else{
		// this.searchInput.className="table_search_input";
		this.searchInput.style.backgroundColor=this.searchFieldBackgroundInactiveColor;
		this.searchInput.style.color=this.searchFieldFontInactiveColor;
	}
    this.callBack('search',{expression:expression});
};

FastTablePortlet.prototype.onUserDownload=function(){
	//portletManager.showWait("Downloading");
	this.callBack('download',{x:this.owningWindow.MOUSE_POSITION_X,y:this.owningWindow.MOUSE_POSITION_Y});
};

FastTablePortlet.prototype.onUserColumnResize=function(e,col,width){
    this.callBack('columnWidth',{columnIndex:col,width:width});
};

FastTablePortlet.prototype.onUserSort=function(e,col,type){
	//if(this.table.getRowsCount()>100000)
      //portletManager.showWait("Sorting");
   this.callBack('sort',{columnIndex:col,sortType:type});
};

FastTablePortlet.prototype.lastClipUpper;
FastTablePortlet.prototype.lastClipLower;
FastTablePortlet.prototype.lastClipLeft;
FastTablePortlet.prototype.lastClipRight;
FastTablePortlet.prototype.lastClipLeftPin;
FastTablePortlet.prototype.lastClipRightPin;
FastTablePortlet.prototype.onTableUpdateCellsEnd=function(i,j){
	if(this.table.stateInit != true)
		return;
	var upper=this.table.getUpperRowVisible();
	var lower=this.table.getLowerRowVisible();
	var leftPin = this.table.firstVisiblePinnedColumn;
	var rightPin= this.table.lastVisiblePinnedColumn;
	var left = this.table.firstVisibleColumn;
	var right= this.table.lastVisibleColumn;
	if (this.table.scrollPane.vscrollChanged == 0 && !this.rowVisibilityChanged(upper, lower, left, right, leftPin, rightPin)) {
		// not a vertical scroll and row visibility did not change
		return;
	}
	// vertical scroll
	// Not a v. scroll but row visibility changed
    this.lastClipUpper=upper;
    this.lastClipLower=lower;
    this.lastClipLeft=left;
    this.lastClipRight=right;
    this.lastClipLeftPin=leftPin;
    this.lastClipRightPin=rightPin;
	this.callBack('clipzone',{top:upper,bottom:lower, left:left, right:right, leftPin:leftPin, rightPin:rightPin});
};
FastTablePortlet.prototype.onTableFetchData=function(i,j){
  return '<img src="rsc/wait.gif"/>';
};

FastTablePortlet.prototype.onHover=function(x,y){
    this.callBack('hover',{x:x,y:y});
}

FastTablePortlet.prototype.rowVisibilityChanged=function(upper, lower, left, right, leftPin,rightPin){
	if (upper==this.lastClipUpper && lower==this.lastClipLower && left==this.lastClipLeft && right==this.lastClipRight && leftPin==this.lastClipLeftPin && rightPin==this.lastClipRightPin)
		return false;
	return true;
}

FastTablePortlet.prototype.init=function(){
};

FastTablePortlet.prototype.setSize=function(width,height){
	this.width=width;
	this.height=height;
	this.portlet.setSize(width,height);
	var newWidth = width;
	var newHeight = height - this.headerHeight;
	if(this.table.width != newWidth){
		this.table.widthChanged = true;
		this.table.width = newWidth;
	}
	if(this.table.height != newHeight){
		this.table.heightChanged = true;
		this.table.height = newHeight; 
	}
	this.table.fireSizeChanged();
	var scrollSize = this.table.scrollPane.hscrollVisible ? this.table.scrollPane.scrollSize : 0;
	this.table.callback('tableSizeChanged',{
	  							left:this.table.scrollPane.getClipLeft(),
	  							top:this.table.scrollPane.getClipTop(),
	  							height:this.table.scrollPane.getClipHeight() - scrollSize,
	  							contentWidth:this.table.scrollPane.paneWidth,
	  							contentHeight:this.table.scrollPane.paneHeight
	  						   });
};

FastTablePortlet.prototype.getFastTable=function(){
	return this.table;
};

FastTablePortlet.prototype.setHover=function(x,y,value,xAlign,yAlign){
  this.table.setHover(x,y,value, xAlign, yAlign);
}

FastTablePortlet.prototype.clearHover=function(){
  this.table.clearHover();
}

FastTablePortlet.prototype.close=function(type, param){
  this.table.clearHover();
}
//########################
//##### Tree Portlet #####


function TreePortlet(portletId,parentId){
	  var that=this;
	  this.portlet=new Portlet(this,portletId,null);
	  //this.nodes={};
	  //this.divElement.style.overflow='auto';
	  this.treeDiv=nw("div");
	  this.tree=new FastTree(this.treeDiv);
	  this.tree.treePortlet=that;
	  //this.tree.onUserExpand=function(e){that.onUserExpand(e,type);};
	  this.tree.callback=function(method,args){that.callBack(method,args);};
	  this.tree.onUserSelectedTree=function(e,action,uid,col){that.onUserSelectedTree(e,action,uid,col);};
	  this.tree.onUserSelected=function(e,activeRow,selectedRows, col, clicked){that.onUserSelected(e,activeRow,selectedRows,col, clicked);};
	  this.tree.onUpdateCellsEnd=function(table){that.onTreeUpdateCellsEnd(table);};
	  this.tree.onUserContextMenuItem=function(e,id){that.onUserContextMenuItem(e,id);};
      this.tree.onUserColumnResize=function(e,col,width){that.onUserColumnResize(e,col,width);};
      this.tree.onUserHeaderMenu=function(e,col){that.onUserHeaderMenu(e,col);};
      this.tree.onUserHeaderMenuItem=function(e,id,col){that.onUserHeaderMenuItem(e,id,col);};
      this.tree.onUserCopy=function(e,activeUid,col){that.onUserCopy(e,activeUid,col);};
	  this.titleDiv=nw("div","table_title");
	  this.searchInput=nw("input","table_search_input");
	  //MOBILE SUPPORT - enables enter key in mobile to be pressed instead of tabbing to next field for search
	  this.searchInput.setAttribute('enterkeyhint', 'go');
	  this.titleSpan=nw("span","table_title_span");
	  this.searchInputButton=nw("div","table_search_button");
	  makeEnterable(this.searchInput,this.searchInputButton);
	  this.expandInputButton=nw("div","expand_all_button");
//	  this.expandInputButton.innerHTML="<img src='rsc/btn_expand.gif'/>";
	  this.contractInputButton=nw("div","contract_all_button");
//	  this.contractInputButton.innerHTML="<img src='rsc/btn_contract.gif'/>";
//	  this.expandInputButton=nw("div","table_search_button");
//	  this.expandInputButton.innerHTML="<img src='rsc/btn_expand.gif'/>";
//	  this.contractInputButton=nw("div","table_search_button");
//	  this.contractInputButton.innerHTML="<img src='rsc/btn_contract.gif'/>";
	  this.headerDiv=nw("div","table_search");
	  this.headerDiv.appendChild(this.titleDiv);
	  this.headerDiv.appendChild(this.searchInput);
	  this.headerDiv.appendChild(this.searchInputButton);
	  this.headerDiv.appendChild(this.expandInputButton);
	  this.headerDiv.appendChild(this.contractInputButton);
	  this.searchInputButton.onclick=function(e){that.onUserSearch(e);}
	  this.expandInputButton.onclick=function(e){that.onUserExpandAll();}
	  this.contractInputButton.onclick=function(e){that.onUserContractAll();}
	  this.expandInputButton.style.right='200px';
	  this.contractInputButton.style.right='220px';
      this.divElement.style.background='white';
	  this.divElement.appendChild(this.treeDiv);
	  this.divElement.appendChild(this.headerDiv);
	  this.currentUpperRowVisible=-1;
	  this.currentLowerRowVisible=-1;
	  this.titleDiv.appendChild(this.titleSpan);
	}
	TreePortlet.prototype.handleWheel=function(e){
		this.tree.scrollPane.handleWheel(e);
	}
	TreePortlet.prototype.handleDblclick=function(e){
		//Checks if mouse is inside any of the visible cells 
		if(isMouseInside(e,this.tree.scrollPane.DOM.paneElement, -0)){
			this.onUserDblclick(e, {action:"callRelationship"});
		}
	}
	//this.treeElement.onkeydown=function(e){e.stopPropagation();that.onKeyDown(e);};

	TreePortlet.prototype.handleKeydown=function(e, _target){
		if(e.target != null && e.target.isQuickColumnFilterElement == true){
			return;
		}
		if (_target.nodeName == "INPUT") {
			if (e.target == this.searchInput && e.key == "Escape") {
				this.setSearch(null);
				this.onUserSearch(e);
			}
			return;
		}
		
		var shiftKey = e.shiftKey;
		var ctrlKey = e.ctrlKey;
		var altKey = e.altKey;
		switch(e.key){	
//		case " ":
//			this.table.jumpToNextUniqueCol(e);
//			break;
		case "ArrowLeft":
			this.tree.expandRow(e);
			break;
		case "ArrowRight":
			this.tree.expandRow(e);
			break;
		case "ArrowDown":
			this.tree.handleKeyDown(e, 1);
			break;
		case "ArrowUp":
			this.tree.handleKeyDown(e, -1);
			break;
		case "PageDown":
			this.tree.handleKeyDown(e,this.tree.visibleRowsCount);
			break;
		case "PageUp":
			this.tree.handleKeyDown(e,-this.tree.visibleRowsCount);
			break;
		case "Escape":
			this.tree.clearSelected(e);
			break;
		case "c":
			if(ctrlKey){
				this.onUserCopy(e, this.tree.activeRowUid, this.tree.getColumnAtPoint(MOUSE_POSITION_X));
			}
			break;
		case "a":
			if(ctrlKey){
				this.tree.selectAll(e);
			}
			break;
		default:
			break;	
		}	
	}

    TreePortlet.prototype.setOptions=function(jsonData){
      this.tree.setOptions(jsonData);
      if (jsonData.searchBgStyle != '_bg=null') {
    	  applyStyle(this.headerDiv,jsonData.searchBgStyle);
    	  this.headerDiv.style.backgroundImage='none';
      } else {
    	  this.headerDiv.style.backgroundImage=null;
      }
      applyStyle(this.searchInput,jsonData.searchFieldBgStyle);
      applyStyle(this.searchInput,jsonData.searchFieldFgStyle);
      if (jsonData.fontFamily != null)
      	applyStyle(this.titleDiv,jsonData.fontFamily);
	  else
      	applyStyle(this.titleDiv,"_fm=arial");

      this.searchFieldBackgroundInactiveColor = jsonData.searchFieldBgStyle;
      this.searchFieldFontInactiveColor = jsonData.searchFieldFgStyle;
	  this.searchFieldBackgroundActiveColor = jsonData.filteredBg;
	  this.searchFieldFontActiveColor = jsonData.filteredFont;
      this.searchBarHidden = jsonData.searchBarHidden;
      this.searchInput.style.borderColor=jsonData.searchFieldBorderColor;
      
      if (jsonData.searchBarHidden == "true"){
    	  this.headerDiv.style.visibility="hidden";
      } else {
    	  this.headerDiv.style.visibility="visible";
      }
      searchButtonsColor=jsonData.searchButtonsColor || '#ffffff';
  	  var searchButtonSvgStyle=searchButtonsColor+';stroke-width:14;stroke-miterlimit:10;" cx="41.3" cy="41.2" r="24.7"/> <line style="fill:none;stroke:'+searchButtonsColor;
  	  var expandButtonSvgStyle= searchButtonsColor+';stroke-width:18;stroke-miterlimit:10;" x1="12" y1="50" x2="88" y2="50"/> <line style="fill:none;stroke:'+searchButtonsColor;
  	  var contractButtonSvgStyle= searchButtonsColor;
  	  searchButtonSvgStyle=searchButtonSvgStyle.replace(/#/g, "%23");
  	  expandButtonSvgStyle=expandButtonSvgStyle.replace(/#/g, "%23");
  	  contractButtonSvgStyle=contractButtonSvgStyle.replace(/#/g, "%23");
  	  this.searchInputButton.style.backgroundImage=(SEARCH_BUTTON_IMAGE_PREFIX+searchButtonSvgStyle+SEARCH_BUTTON_IMAGE_SUFFIX);
  	  this.expandInputButton.style.backgroundImage=(EXPAND_BUTTON_IMAGE_PREFIX+expandButtonSvgStyle+EXPAND_BUTTON_IMAGE_SUFFIX);
  	  this.contractInputButton.style.backgroundImage=(CONTRACT_BUTTON_IMAGE_PREFIX+contractButtonSvgStyle+CONTRACT_BUTTON_IMAGE_SUFFIX);
    }

	TreePortlet.prototype.focusSearch=function(){
		this.searchInput.focus();
	}
	TreePortlet.prototype.onUserDblclick=function(e, data){
		this.callBack('userDblclick', data);
	}
	TreePortlet.prototype.onUserSearch=function(e){
	    //portletManager.showWait("Searching");
		var expression=this.searchInput.value;
		if (expression) {
			 applyStyle(this.searchInput, this.searchFieldBackgroundActiveColor);
		     applyStyle(this.searchInput, this.searchFieldFontActiveColor);
		} else {
			 applyStyle(this.searchInput, this.searchFieldBackgroundInactiveColor);
		     applyStyle(this.searchInput, this.searchFieldFontInactiveColor);
		}
		//var rev = typeof(e.shiftKey) == 'boolean' && e.shiftKey;
	    this.callBack('search',{expression:expression,reverse:e.shiftKey});
	}
	TreePortlet.prototype.setSearch=function(expression){
		this.searchInput.value=expression;
		if (expression) {
			 applyStyle(this.searchInput, this.searchFieldBackgroundActiveColor);
		     applyStyle(this.searchInput, this.searchFieldFontActiveColor);
		} else {
			 applyStyle(this.searchInput, this.searchFieldBackgroundInactiveColor);
		     applyStyle(this.searchInput, this.searchFieldFontInactiveColor);
		}
	}
	TreePortlet.prototype.setTitle=function(title){
		this.titleSpan.innerHTML=title;
	};
	TreePortlet.prototype.onUserExpandAll=function(){
	    //portletManager.showWait("expand All");
	    this.callBack('expandAll',{});
	}
	TreePortlet.prototype.onUserContractAll=function(){
	    //portletManager.showWait("contract All");
	    this.callBack('contractAll',{});
	}
	TreePortlet.prototype.onUserContextMenuItem=function(e,id){
	    this.callBack('menuitem',{action:id});
	}
    TreePortlet.prototype.onUserHeaderMenu=function(e,col){
//    	console.log(e);
        this.callBack('showHeaderMenu',{col:col});
    };
    TreePortlet.prototype.onUserHeaderMenuItem=function(e,id,col){
        this.callBack('headerMenuitem',{action:id,col:col});
    };
    TreePortlet.prototype.onUserCopy=function(e,uid,col){
        this.callBack('copy',{uid:uid,col:col});
    };

	TreePortlet.prototype.setSize=function(width,height){
	  this.portlet.setSize(width,height);
	  this.tree.setLocation(0,0,width,height);
	}
	TreePortlet.prototype.onTreeUpdateCellsEnd=function(i,j){
		var upper=this.tree.getUpperRowVisible();
		var lower=this.tree.getLowerRowVisible();
//		console.log(this.currentLowerRowVisible, this.currentUpperRowVisible)
		if(lower>0 && (this.currentLowerRowVisible!=lower || this.currentUpperRowVisible!=upper)){
			var nwClipzone = {top:upper,bottom:lower};
			this.currentLowerRowVisible=lower;
			this.currentUpperRowVisible=upper;
	        this.callBack('clipzone',nwClipzone);
		}
	}
	TreePortlet.prototype.getTree=function(i,j){
	    return this.tree;
	}

//	TreePortlet.prototype.onUserExpand=function(e,type){
//	  this.callBack('expand',{treeNodeUid:getMouseTarget(e).parentNode.uid});
//	}
	TreePortlet.prototype.onUserSelectedTree=function(e,action,uid,col){
	  var ctrlKey=e.ctrlKey;
	  var shiftKey=e.shiftKey;
	  var button=getMouseButton(e);
	  this.callBack(action,{ctrl:ctrlKey ? true : false, shift:shiftKey ? true : false,button:button,treeNodeUid:uid,col:col});//getMouseTarget(e).parentNode.uid});
	}

	TreePortlet.prototype.onUserSelected=function(e,activeRow,selectedRows, col, clicked){
	    this.callBack('userSelect',{button: getMouseButton(e), activeRow:activeRow,selectedRows:selectedRows, col:col, clicked:clicked});
	};

    TreePortlet.prototype.onUserColumnResize=function(e,col,width){
        this.callBack('columnWidth',{columnIndex:col,width:width});
    };
    
    //setAutocomplete
    TreePortlet.prototype.setQuickFilterAutocomplete=function(col, vals){
    	this.tree.setQuickFilterAutocomplete(col,vals);
    }
    
    /*
    // Might not be needed instead use init
    TreePortlet.prototype.setQuickFilterSearchText=function(col, expression){
    	
    }
    TreePortlet.prototype.callGetQuickFilterOptions=function(colLocation, filterValue){
     	this.callback('getColumnFilterOptions',{pos:col.colLocation,val:filterValue});
    }
    TreePortlet.prototype.callSetQuickFilterValue=function(colLocation, filterValue){
     	this.callback('columnFilter',{pos:col.colLocation,val:filterValue});
    }
    */
    

////##########################
////##### Select Portlet #####
//
//function SelectPortlet(portletId,parentId){
//  this.portlet=new Portlet(this,portletId,null);
//  this.selectElement=nw('select');
//  this.select=new Select(this.selectElement);
//  this.divElement.appendChild(this.selectElement);
//};
//
//SelectPortlet.prototype.setValue=function(value){
//  this.select.setSelectedValueDelimited(value,',');
//};
//
//SelectPortlet.prototype.clear=function(){
//  this.select.clear();
//};
//
//SelectPortlet.prototype.addOption=function(value,text,isSelected){
//  this.select.addOption(value,text,isSelected);
//};
//
//SelectPortlet.prototype.init=function(name,multiple,size){
//  this.selectElement.name=name;
//  if(multiple)
//    this.selectElement.multiple='multiple';
//  else
//    this.select.multiple='';
//  this.selectElement.size=size;
//};


//############################
//##### Piechart Portlet #####

function PieChartPortlet(portletId,parentId){
  
  this.portlet=new Portlet(this,portletId,null);
};

PieChartPortlet.prototype.updateData=function(){
	this.data=[];
	for(var i=0;i<arguments.length;i+=2){
	  var label=arguments[i];
	  var val=arguments[i+1];
	  this.data.push({value:val , label : label});
	}
	this.drawChart();
};

PieChartPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.drawChart();
};

PieChartPortlet.prototype.drawChart=function(){
  if(this.data==null)
    return;
  var w=this.portlet.location.width;
  var h=this.portlet.location.height;
  var r = Math.min(w,h)/2, color = d3.scale.category20c();
  var that=this;
  removeAllChildren(this.divElement);
  var vis = d3.select(this.divElement).append("svg:svg").data([this.data]).attr("width", w).attr("height", h).append("svg:g").attr("transform", "translate(" + r + "," + r + ")");    
  var arc = d3.svg.arc().outerRadius(r);
  var pie = d3.layout.pie().value(function(d) { return d.value; });    
  var arcs = vis.selectAll("g.slice").data(pie).enter().append("svg:g").attr("class", "slice");    
  arcs.append("svg:path") .attr("fill", function(d, i) { return color(i); } ) .attr("d", arc);                                    
  arcs.append("svg:text").attr("transform", function(d) {                    
    d.innerRadius = 0;
    d.outerRadius = r;
    return "translate(" + arc.centroid(d) + ")";        
  }).attr("text-anchor", "middle").text(function(d, i) { return that.data[i].label+" - "+that.data[i].value; });        
};

//#############################
//##### Linechart Portlet #####

function LineChartPortlet(portletId,parentId){

  this.portlet=new Portlet(this,portletId,null);
}

LineChartPortlet.prototype.updateData=function(data){
	this.data=data;
	this.drawChart();
};

LineChartPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.drawChart();
};

LineChartPortlet.prototype.drawChart=function(){
  if(this.data==null)
    return;
  var width=this.portlet.location.width;
  var height=this.portlet.location.height;
 
  removeAllChildren(this.divElement);
  
  var data1=this.data;
             //{year:30,p99:32,p95:50},
             //{year:31,p99:30,p95:51},
             //{year:32,p99:26,p95:52},
             //{year:33,p99:26,p95:53},
             //{year:34,p99:27,p95:53},
             //{year:35,p99:28,p95:53},
             //{year:36,p99:22,p95:53},
             //{year:37,p99:23,p95:53},
             //{year:38,p99:22,p95:53},
             //{year:39,p99:24,p95:53},
             //{year:40,p99:25,p95:53},
             //{year:41,p99:26,p95:53},
             //{year:42,p99:24,p95:53},
             //{year:43,p99:22,p95:53},
             //{year:44,p99:21,p95:53},
             //{year:45,p99:19,p95:53},
             //];

              /* Read CSV file: first row =>  year,top1,top5  */
              var label_array = new Array(),val_array1 = new Array();

              var sampsize = data1.length;
              var maxval=0;

              for (var i=0; i < sampsize; i++) {
                 label_array[i] = parseInt(data1[i].x);
                 val_array1[i] = { x: label_array[i], y: parseFloat(data1[i].y1)};//, z: parseFloat(data1[i].p95) };
                 //maxval = Math.max(maxval, parseFloat(data1[i].y1), parseFloat(data1[i].) );
                 maxval = Math.max(maxval, parseFloat(data1[i].y1));
               }

               maxval = (1 + Math.floor(maxval / 10)) * 10;

             var p=0;
             var w = width-p*2;
             var h = height-p*2;
             var x = d3.scale.linear().domain([ label_array[0], label_array[sampsize-1] ]).range([0, w]);
             var y = d3.scale.linear().domain([0, maxval]).range([h, 0]);

             var vis = d3.select(this.divElement).data([val_array1]).append("svg:svg").attr("width", width).attr("height", height)
             .append("svg:g").attr("transform", "translate(" + p + "," + p + ")");

             var xrules = vis.selectAll().data(x.ticks(30)).enter().append("svg:g").attr("class", "linechart_line");
             var yrules = vis.selectAll().data(y.ticks(25)).enter().append("svg:g").attr("class", "linechart_line");

             // Draw grid lines
             xrules.append("svg:line").attr("x1", x).attr("x2", x).attr("y1", 0).attr("y2", h - 1);
             yrules.append("svg:line").attr("y1", y).attr("y2", y).attr("x1", 0).attr("x2", w - 1);
 
             vis.append("svg:path") .attr("class", "line") .attr("fill", "none") .attr("stroke", "maroon") .attr("stroke-width", 2)
             .attr("d", d3.svg.line() .x(function(d) { return x(d.x); }) .y(function(d) { return y(d.y); }));

         vis.selectAll() .data(val_array1) .enter().append("svg:circle") .attr("class", "line") .attr("fill", "maroon" )
             .attr("cx", function(d) { return x(d.x); }) .attr("cy", function(d) { return y(d.y); }) .attr("r", 1);  
};

//###########################
//##### Treemap Portlet #####

function TreemapPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.chartDiv=nw('div');
  this.divElement.appendChild(this.chartDiv);
  var that=this;
  this.treemap=new TreeMap(this.chartDiv);
  this.treemap.onUserClick=function(id,shift,ctrl,btn){that.onUserClick(id,shift,ctrl,btn);};
  //MOBILE SUPPORT - heatmap touch support
  this.treemap.onTouchHold=function(id,shift,ctrl,btn){that.onTouchHold(id,shift,ctrl,btn);};
  this.treemap.onUserContextMenuItem=function(e,id,nodeId){that.onUserContextMenuItem(e,id,nodeId);};
  this.treemap.onUserGradient=function(colors){that.onUserGradient(colors);};
  this.treemap.onHover=function(x,y,id){that.onHover(x,y,id);};
  //this.treemap.onclick=function(x,y){that.onChartClicked(x,y)};
};
TreemapPortlet.prototype.handleKeydown=function(e){
    this.treemap.handleKeydown(e);
}

TreemapPortlet.prototype.setOwningWindow=function(win){
	this.owningWindow=win;
	this.treemap.owningWindow=win;
}

TreemapPortlet.prototype.updateData=function(data){
	this.drawMap();
};
TreemapPortlet.prototype.setHover=function(x,y,nid,value){
  this.treemap.setHover(x,y,nid,value);
}

TreemapPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.treemap.setSize(width,height);
};

TreemapPortlet.prototype.clearData=function(){
  this.treemap.clearData();
};
TreemapPortlet.prototype.addParents=function(data){
  this.treemap.addParents(data);
};
TreemapPortlet.prototype.addNodes=function(data){
  this.treemap.addNodes(data);
};
TreemapPortlet.prototype.removeNodes=function(data){
  this.treemap.removeNodes(data);
};
TreemapPortlet.prototype.setDepthStyles=function(data){
  this.treemap.setDepthStyles(data);
};
TreemapPortlet.prototype.removeParents=function(data){
  this.treemap.removeParents(data);
};
TreemapPortlet.prototype.addChildren=function(data){
  this.treemap.addChildren(data);
};

TreemapPortlet.prototype.removeChildren=function(data){
this.treemap.removeChildren(data);
};

TreemapPortlet.prototype.showContextMenu=function(data){
  this.treemap.showContextMenu(data);
};
TreemapPortlet.prototype.setHeatColors=function(data){
  this.treemap.setHeatColors(data);
};
TreemapPortlet.prototype.repaint=function(){
  this.treemap.repaint();
};
TreemapPortlet.prototype.setOptions=function(jsonData){
  this.treemap.setOptions(jsonData);
}

TreemapPortlet.prototype.onHover=function(x,y,id){
    this.callBack('hover',{nid:id,x:x,y:y});
}
TreemapPortlet.prototype.onUserClick=function(id,shift,ctrl,btn){
    this.callBack('click',{nid:id,shift:shift,ctrl:ctrl,btn:btn});
}
//MOBILE SUPPORT - touch hold for heatmap for context menu
TreemapPortlet.prototype.onTouchHold=function(id,shift,ctrl,btn){
    this.callBack('onTouch',{nid:id,shift:shift,ctrl:ctrl,btn:btn});
}
//MOBILE SUPPORT - cancel zoom for heatmap
TreemapPortlet.prototype.cancelZoom=function(){
  this.treemap.cancelZoom();
};

TreemapPortlet.prototype.onUserContextMenuItem=function(e,id,nodeId){
    this.callBack('menuitem',{action:id,nid:nodeId});
}
TreemapPortlet.prototype.onUserGradient=function(colors){
	var vals={stopsCount:colors.length};
	for(var i in colors){
		vals['stop'+i]=colors[i][0];
		vals['color'+i]=colors[i][4];
	}
    this.callBack('colors',vals);
}
TreemapPortlet.prototype.handleNodeStyleChange=function(jsonData){
	this.treemap.handleNodeStyleChange(jsonData);
}
 
TreemapPortlet.prototype.close=function(type,param){
	this.treemap.clearHover();
}
 
//#########################
//##### Image Portlet #####

function ImagePortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.map=nw('map');
  this.map.name='map'+portletId;
  this.imageElement=nw('img');
  this.imageElement.useMap='#'+this.map.name;
  this.imageElement.onclick=function(e){that.onImageClicked(e);};
  this.divElement.appendChild(this.imageElement);
  this.divElement.appendChild(this.map);
};

ImagePortlet.prototype.onImageClicked=function(e){
  var point=getMousePoint(e);
  this.callBack('click',{x:point.x,y:point.y});
};

ImagePortlet.prototype.setImageGenerator=function(value){
  this.input.value=value;
};

ImagePortlet.prototype.init=function(imageGeneratorId,uid){
  this.imageGeneratorId=imageGeneratorId;
  this.uid=uid;
};
ImagePortlet.prototype.imageChanged=function(uid){
  this.uid=uid;
  this.setSize(this.location.width,location.height);
};

ImagePortlet.prototype.clearTooltips=function(){
  while(this.map.childNodes.length>0)
    this.map.removeChild(this.map.firstChild);
};

ImagePortlet.prototype.addTooltip=function(left,top,width,height,text){
  var area=nw('area');
  area.shape='rect';
  area.coords=left+','+top+','+(left+width)+','+(top+height);
  area.title=text;
  area.href='#';
  this.map.appendChild(area);
};

ImagePortlet.prototype.sizeSize=function(width,height){
  this.portlet.sizeSize(width,height);
  this.imageElement.src=this.imageUrl+'?w='+width+'&h='+height+'&id='+this.imageGeneratorId+'&webWindowId='+window.windowId+'&uid='+this.uid;
};

//#########################
//##### Blank Portlet #####

function BlankPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.back=nw('div');
  this.back.className='portlet_blank';
  this.back.onclick=function(e){that.onAddButton(e);};
  this.addButton=nw('div');
  this.addButton.className='portlet_blank_add';
  this.divElement.appendChild(this.back);
  this.back.appendChild(this.addButton);
};


BlankPortlet.prototype.onAddButton=function(e){
  this.callBack('showAddPortletDialog',{});
};

BlankPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.addButton.style.top=toPx((height-100)/2);
  this.addButton.style.left=toPx((width-100)/2);
};

BlankPortlet.prototype.populatePortletMenu=function(menu){
  var that=this;
  menu.addItem('<img src="rsc/asc.gif"> <f1:txt key="Delete this Component"/>',null,function(e){that.portlet.onUserDelete(e);});
  menu.addItem('<img src="rsc/autoasc.gif"> <f1:txt key="Add a new component here"/>',null,function(e){that.onAddButton(e);});
};

function ChartPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.chartDiv=nw('div');
  this.divElement.appendChild(this.chartDiv);
  var that=this;
  this.chart=new FastChart(this.chartDiv);
  this.chart.onclick=function(x,y){that.onChartClicked(x,y)};
};
ChartPortlet.prototype.onChartClicked=function(x,y){
  this.callBack('userClick',{x:x,y:y});
}

ChartPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.chart.setLocation(0,0,width,height);
};

ChartPortlet.prototype.setData=function(type,jsonData,options){
	this.chart.setData(type,jsonData,options);
}

function ThreeDeePortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.chartDiv=nw('div');
  this.divElement.appendChild(this.chartDiv);
  var that=this;
  this.surface=new Surface(this.chartDiv);
  this.surface.onUserChangedPerspective=function(rotX,rotY,roxZ,zoom,fov){that.onUserChangedPerspective(rotX,rotY,roxZ,zoom,fov);};
  this.surface.onSelectionChanged=function(ids,toggleSelect,addSelect,clear){that.onSelectionChanged(ids,toggleSelect,addSelect,clear);};
  this.surface.onShowContextMenu=function(){that.onShowContextMenu();};
  this.surface.onHover=function(id,x,y){that.onHover(id,x,y);};
  onOptionsChanged=function(){that.onOptionChanged()};
  //this.surface.onclick=function(x,y){that.onChartClicked(x,y)};
};
ThreeDeePortlet.prototype.onChartClicked=function(x,y){
}
ThreeDeePortlet.prototype.onHover=function(id,x,y){
    this.callBack('onHover',{mouseX:x,mouseY:y,polyId:id});
}
ThreeDeePortlet.prototype.setHover=function(x,y,sel,name,xAlign,yAlign){
  this.surface.setHover(x,y,sel,name,xAlign,yAlign);
}
ThreeDeePortlet.prototype.onUserChangedPerspective=function(rx,ry,rz,zm,fov){
    this.callBack('onPerspective',{rx:rx,ry:ry,rz:rz,zm:zm,fov:fov});
};
ThreeDeePortlet.prototype.onSelectionChanged=function(ids,toggleSelect,addSelect,clear){
    this.callBack('onSelection',{ids:ids.join(','),action:clear ? 'clear' : (toggleSelect ? 'toggle' : addSelect ? 'add' : 'replace')});
};
ThreeDeePortlet.prototype.onShowContextMenu=function(){
    this.callBack('showContextMenu',{});
};

ThreeDeePortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.surface.setSize(width,height);
};

ThreeDeePortlet.prototype.setData=function(jsonData){
	this.surface.setData(jsonData);
}
ThreeDeePortlet.prototype.setOptions=function(jsonData){
	this.surface.setOptions(jsonData);
}
ThreeDeePortlet.prototype.repaintIfNeeded=function(){
	this.surface.repaintIfNeeded();
}
ThreeDeePortlet.prototype.close=function(){
	this.surface.close();
}



function TilesPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.tilesDiv=nw('div');
  this.divElement.appendChild(this.tilesDiv);
  var that=this;
  this.tiles=new TilesPanel(this.tilesDiv);
  this.tiles.onClipzoneChanged=function(top,bottom){that.onClipzoneChanged(top, bottom);};
  this.tiles.onUserSelected=function(active,selected){that.onUserSelected(active, selected);};
  this.tiles.onclick=function(x,y){that.onTileClicked(x,y)};
  this.tiles.onUserContextMenu=function(e){that.onUserContextMenu(e);};
  this.tiles.onUserDblClick=function(e,pos){that.onUserDblClick(e,pos);};
  this.tiles.onUserContextMenuItem=function(e,id,nodeId){that.onUserContextMenuItem(e,id,nodeId);};
};
TilesPortlet.prototype.handleWheel=function(e){
	var delta;
	if(e.deltaMode == WheelEvent.DOM_DELTA_PIXEL)
		delta = e.deltaY/-100;
	else
		delta = e.deltaY/-1
	
	this.tiles.onMouseWheel(e,delta);
}

TilesPortlet.prototype.onTileClicked=function(x,y){
  this.callBack('userClick',{x:x,y:y});
}

TilesPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.tiles.setLocation(0,0,width,height);
};

TilesPortlet.prototype.setData=function(type,jsonData,options){
	this.tiles.setData(type,jsonData,options);
}

TilesPortlet.prototype.clearData=function(){
  this.tiles.clearData();
};
TilesPortlet.prototype.initTiles=function(multiselect){
  this.tiles.initTiles(multiselect);
};
TilesPortlet.prototype.addChildren=function(data){
  this.tiles.addChildren(data);
};
TilesPortlet.prototype.setOptions=function(options){
  this.tiles.setOptions(options);
};
TilesPortlet.prototype.repaint=function(){
  this.tiles.repaint();
};
TilesPortlet.prototype.setTilesCount=function(count){
  this.tiles.setTilesCount(count);
};
TilesPortlet.prototype.showContextMenu=function(json){
  this.tiles.showContextMenu(json);
};
TilesPortlet.prototype.onUserContextMenuItem=function(e,id,nodeId){
    this.callBack('menuitem',{action:id,nid:nodeId});
}
TilesPortlet.prototype.onUserContextMenu=function(e,col){
    this.callBack('showMenu',{});
};
TilesPortlet.prototype.onUserDblClick=function(e,pos){
    this.callBack('dblclick',{pos:pos});
};

TilesPortlet.prototype.setActiveTilePos=function(pos){
  this.tiles.setActiveTilePos(pos);
};
TilesPortlet.prototype.setSelectedTiles=function(pos){
  this.tiles.setSelectedTiles(pos);
};
TilesPortlet.prototype.onClipzoneChanged=function(top,bottom){
  this.callBack('clipzone',{top:top,bottom:bottom});
};

TilesPortlet.prototype.onUserSelected=function(active,selected){
  this.callBack('select',{active:active,selected:selected});
};

function TextPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.textDiv=nw('div');
  this.divElement.appendChild(this.textDiv);
  var that=this;
  this.text=new FastText(this.textDiv);
  //this.graph.onNodeMoved=function(id,x,y){that.onNodeMoved(id,x,y);};
  this.text.onClip=function(top,bot){that.onClip(top,bot);};
  this.text.onUserSelected=function(selected){that.onUserSelected(selected);};
  this.text.onUserContextMenu=function(selected){that.onUserContextMenu(selected);};
  this.text.onUserContextMenuItem=function(e,id){that.onUserContextMenuItem(e,id);};
  this.text.onColumnsVisible=function(cols){that.onColumnsVisible(cols);};
};
TextPortlet.prototype.handleWheel=function(e){
	this.text.scrollPane.handleWheel(e);
}
TextPortlet.prototype.getText=function(){
	return this.text;
}

TextPortlet.prototype.onClip=function(top,bot){
    this.callBack('clip',{top:top,bot:bot});
}
TextPortlet.prototype.init=function(linesCount,rowHeight,maxChars,labelWidth,labelStyle,style){
	this.text.init(linesCount,rowHeight,maxChars,labelWidth,labelStyle,style);
}

TextPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.text.setLocation(0,0,width,height);
};
TextPortlet.prototype.setTopLine=function(line){
	this.text.setTopLine(line);
}
TextPortlet.prototype.setSelectedLines=function(lines){
	this.text.setSelectedLines(lines);
}
TextPortlet.prototype.setData=function(data){
  this.text.setData(data);
};
TextPortlet.prototype.setScrollTicks=function(ticks){
	this.text.setScrollTicks(ticks);
}

TextPortlet.prototype.onUserSelected=function(selected){
  this.callBack('select',{selected:selected});
};
TextPortlet.prototype.onUserContextMenu=function(e,col){
    this.callBack('showMenu',{});
};
TextPortlet.prototype.showContextMenu=function(data){
  this.text.showContextMenu(data);
};
TextPortlet.prototype.onUserContextMenuItem=function(e,id){
    this.callBack('menuitem',{action:id});
}
TextPortlet.prototype.onColumnsVisible=function(cols){
    this.callBack('colsVisible',{cols:cols});
}

function DropDownMenuPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.menuDiv=nw('div');
  this.divElement.appendChild(this.menuDiv);
  this.menuBar=new MenuBar(this.menuDiv);
  this.menuBar.onUserOverMenu=function(id){that.onUserOverMenu(id);};
  this.menuBar.onUserClickedMenuItem=function(id){that.onUserClickedMenuItem(id);};
  var that=this;
};

DropDownMenuPortlet.prototype.setMenus=function(menus){
	this.menuBar.setMenus(menus);
}
DropDownMenuPortlet.prototype.setOptions=function(options){
	this.menuBar.setOptions(options);
}
DropDownMenuPortlet.prototype.showMenu=function(id,menu){
	this.menuBar.showMenu(id,menu);
}
DropDownMenuPortlet.prototype.onUserOverMenu=function(id){
    this.callBack('showmenu',{id:id});
}
DropDownMenuPortlet.prototype.onUserClickedMenuItem=function(id){
    this.callBack('menuitem',{id:id});
}
DropDownMenuPortlet.prototype.setCssStyle=function(cssStyle){
	this.menuBar.setCssStyle(cssStyle);
}

function GraphPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.graphDiv=nw('div');
  this.divElement.appendChild(this.graphDiv);
  var that=this;
  this.graph=new GraphPanel(this.graphDiv);
  this.graph.onNodeMoved=function(id,x,y){that.onNodeMoved(id,x,y);};
  this.graph.onUserClick=function(id,shift,ctrl,button){that.onUserClick(id,shift,ctrl,button);};
  this.graph.onUserSelect=function(x,y,w,h,shift,ctrl,button){that.onUserSelect(x,y,w,h,shift,ctrl,button);};
  this.graph.onUserContextMenuItem=function(e,id){that.onUserContextMenuItem(e,id);};
  this.graph.onUserDblClick=function(id){that.onUserDblClick(id);};
  this.graph.onUserDirectionKey=function(e){that.callBack("graphKeyDown",{key:e.keyCode, ctrl:e.ctrlKey})};
};

GraphPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.graph.setSize(width,height);
};

GraphPortlet.prototype.addNodes=function(data){
	this.graph.addNodes(data);
}

GraphPortlet.prototype.selectNodes=function(data){
	this.graph.selectNodes(data);
}

GraphPortlet.prototype.ensureVisibleNode=function(id){
	this.graph.ensureVisibleNode(id);
};

GraphPortlet.prototype.ensureVisibleNodes=function(nodeIds){
	this.graph.ensureVisibleNodes(nodeIds);
}

GraphPortlet.prototype.addEdges=function(data){
	this.graph.addEdges(data);
}
GraphPortlet.prototype.clearData=function(){
	this.graph.clearData();
}
GraphPortlet.prototype.setGridSnap=function(grid,snap){
	this.graph.setGridSnap(grid,snap);
}
GraphPortlet.prototype.clearEdges=function(){
	this.graph.clearEdges();
}
GraphPortlet.prototype.repaint=function(){
	this.graph.repaint();
}
GraphPortlet.prototype.removeEdges=function(json){
	this.graph.removeEdges(json);
}
GraphPortlet.prototype.removeNodes=function(json){
	this.graph.removeNodes(json);
}

GraphPortlet.prototype.onNodeMoved=function(id,x,y){
    this.callBack('moveNode',{id:id,x:x,y:y});
}
GraphPortlet.prototype.onUserClick=function(id,shift,ctrl,button){
    this.callBack('click',{button:button,id:id,shift:shift,ctrl:ctrl});
}
GraphPortlet.prototype.onUserSelect=function(x,y,w,h,shift,ctrl,button){
    this.callBack('select',{x:x,y:y,w:w,h:h,shift:shift,ctrl:ctrl,button:button});
}
GraphPortlet.prototype.showContextMenu=function(json){
  this.graph.showContextMenu(json);
};
GraphPortlet.prototype.onUserContextMenuItem=function(e,id){
    this.callBack('menuitem',{action:id});
}
GraphPortlet.prototype.onUserDblClick=function(id){
	this.callBack('dblClick',{id:id});
}

GraphPortlet.prototype.handleWheel=function(e){
	if(e.target.fieldId == null){
		this.graph.scrollPane.handleWheel(e);
	}
}

function ProgressBarPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.progressBackDiv=nw('div','progress_bar_back');
  this.progressOuterDiv=nw('div','progress_bar_outer');
  this.progressMessageDiv=nw('div','progress_bar_message');
  this.progressDiv=nw('div','progress_bar');
  this.divElement.appendChild(this.progressBackDiv);
  this.progressBackDiv.appendChild(this.progressOuterDiv);
  this.progressOuterDiv.appendChild(this.progressDiv);
  this.progressOuterDiv.appendChild(this.progressMessageDiv);
  this.paddingT=10;
  this.paddingR=10;
  this.paddingB=10;
  this.paddingL=10;
};

ProgressBarPortlet.prototype.setStyle=function(paddingT,paddingR,paddingB,paddingL,style){
	this.paddingT=paddingT;
	this.paddingR=paddingR;
	this.paddingB=paddingB;
	this.paddingL=paddingL;
	this.style=style;
	this.updatePadding();
}
ProgressBarPortlet.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.updatePadding();
}
ProgressBarPortlet.prototype.updatePadding=function(){
  this.progressOuterDiv.style.top=toPx(this.paddingT);
  this.progressOuterDiv.style.left=toPx(this.paddingL);
  this.progressOuterDiv.style.width=toPx(Math.max(this.location.width-this.paddingR-this.paddingL,0));
  this.progressOuterDiv.style.height=toPx(Math.max(this.location.height-this.paddingT-this.paddingB,0));
  applyStyle(this.progressBackDiv,this.style);
};

ProgressBarPortlet.prototype.setProgress=function(progress,text,color){
	this.progressDiv.style.width=fl(100*progress)+"%";
	this.progressMessageDiv.innerHTML=text;
	this.progressDiv.style.background=color;
}


function redirectToLogin(){
    sessionStorage.setItem('<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID"/>',null);
	if(portletManager){
		portletManager.closeWindows();
        portletManager.pendingPortalAjax=null;
        portletManager.waitingPortalAjaxResponse=null;
	}
	window.location='<f1:out value="${loggedOutUrl}"/>';
}



//################
//##### Ajax #####



PortletManager.prototype.portletAjax=function(params){
  params.pageUid=portletManager.pageUid;
  params.seqnum=nextSendSeqnum++;
  params.<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID"/>=this.pgid;
  var paramsText=joinAndEncodeMap('&','=',params);
  this.checkEnableAjaxLoading(params);
  if(this.pendingPortalAjax!=null){
      this.ajaxMaxSeqnum = params.seqnum;
	  this.pendingPortalAjax+='\n'+paramsText;
  }
  else if(this.waitingPortalAjaxResponse){
	  this.pendingPortalAjax=paramsText;
	  this.updateLastAjaxTime();
  }
  else {
      this.ajaxMaxSeqnum = params.seqnum;
	  this.portletAjaxNow(paramsText);
	  this.updateLastAjaxTime();
  }
};



PortletManager.prototype.auditToServer=function(text){
    var params={type:'audit',text:text};
    params.pageUid=portletManager.pageUid;
    params.seqnum=nextSendSeqnum++;
    params.webWindowId=window.windowId;
    params.<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID"/>=this.pgid;
    ajax(portletManager.callbackUrl,true,true,params,null);
}


var TEXT_DECODER = new TextDecoder();
function decompressAndDecode(code){
	if(code == null || code.byteLength == 0)
		return "";
	var magic = new Uint8Array(code,0,2);
	if(magic[0] == 31){//} && magic[1] == 156){
		var inflated = pako.inflate(code);
		var decoded = TEXT_DECODER.decode(inflated);
		return decoded;
	}
	else 
		return TEXT_DECODER.decode(code);
}

var showed503=false;
PortletManager.prototype.portletAjaxCallback=function(event){
	var origReq=event.srcElement || event.target;
	if(origReq.readyState!=4)
		return;
	var code = decompressAndDecode(origReq.response);
	var status=origReq.status;
    try{
      this.waitingPortalAjaxResponse=false;
	  this.checkHideLoadingDialog(origReq);
      if(status==200){
        eval(code); 
      }else if(status==503){
    	if(!showed503){
		  showed503=true;
		  alertDialogWindowsGeneric(alertWarningDialog, ["Web balancer has reported web server is down. \n(Error 503)",""], this.windows, null);	
    	}
      }
      
      
      this.lastPortalAjaxWasError=false;
    }catch(e){
      var ticket=generateTicket();
	  this.portalAjaxErrorsCount++;	
      var lines=code.split('\n');
      var codeWithLineNums="";
      for(var i in lines){
    	  codeWithLineNums+=(parseInt(i)+1)+" "+lines[i]+"\n";
      }
      var txt=  '#### Version: '+ portletManager.buildVersion +' ####\n'+ ticket+': Error in ajax response from: '+portletManager.callbackUrl+' ####\n#### Request ####\n'+origReq.paramsText+'\n#### Throwable ####\n'+e.stack+'\n#### Response ####\n'+codeWithLineNums;      
      if(this.portalAjaxErrorsCount< 2 && !this.lastPortalAjaxWasError){
        log(txt);
		alertDialogWindowsGeneric(alertWarningDialog, ["Oops! Something went wrong", "There has been an error.<P> Please reference ticket: <B>"+ ticket + "</B></P> <BR> <P id=buildversionnum>" +  portletManager.buildVersion + "</P>", txt], this.windows, null);	
		var versionnum = document.getElementById("buildversionnum");
		versionnum.style.color = "#a09b9b";
		versionnum.style.fontWeight = "bold";
		versionnum.style.position = "relative";
		versionnum.style.top = "-44px";
		versionnum.style.left = "-4px";
      }
      if(this.portalAjaxErrorsCount<100)
  	    this.auditToServer(txt);
      this.lastPortalAjaxWasError=true;
    }
    if(this.pendingPortalAjax!=null &&  !this.waitingPortalAjaxResponse){
      var t=this.pendingPortalAjax;
      this.pendingPortalAjax=null;
      this.portletAjaxNow(t);
	  this.updateLastAjaxTime();
    }
  };

    
PortletManager.prototype.portletAjaxNow=function(paramsText){
  this.waitingPortalAjaxResponse=true;
  if(this.ajaxSafeMode=="on"){
    var PORTLET_AJAX_REQUEST=new XMLHttpRequest(); // code for IE7+, Firefox, Chrome, Opera, Safari
    var that=this;
    PORTLET_AJAX_REQUEST.onerror=function(o){that.onPortalAjaxError(o);};
    PORTLET_AJAX_REQUEST.onreadystatechange=function(o){that.portletAjaxCallback(o);};
    PORTLET_AJAX_REQUEST.open("POST",portletManager.callbackUrl,true);
    PORTLET_AJAX_REQUEST.setRequestHeader("Content-type","text/html"); 
    PORTLET_AJAX_REQUEST.send(paramsText);
    PORTLET_AJAX_REQUEST.paramsText=paramsText;
	PORTLET_AJAX_REQUEST.maxSeqnum=this.ajaxMaxSeqnum;
  }else if(this.ajaxSafeMode=="extra"){
    var PORTLET_AJAX_REQUEST=new XMLHttpRequest(); // code for IE7+, Firefox, Chrome, Opera, Safari
    var that=this;
    PORTLET_AJAX_REQUEST.onerror=function(o){that.onPortalAjaxError(o);};
    PORTLET_AJAX_REQUEST.onreadystatechange=function(o){that.portletAjaxCallback(o);};
    PORTLET_AJAX_REQUEST.open("POST",portletManager.callbackUrl,true);
    PORTLET_AJAX_REQUEST.setRequestHeader("Content-type","text/html"); 
    PORTLET_AJAX_REQUEST.paramsText=paramsText;
	PORTLET_AJAX_REQUEST.maxSeqnum=this.ajaxMaxSeqnum;
    window.setTimeout( PORTLET_AJAX_REQUEST.send(PORTLET_AJAX_REQUEST.paramsText) ,1);
  }else{
    this.PORTLET_AJAX_REQUEST.open("POST",portletManager.callbackUrl,true);
    this.PORTLET_AJAX_REQUEST.setRequestHeader("Content-type","text/html"); 
    this.PORTLET_AJAX_REQUEST.send(paramsText);
    this.PORTLET_AJAX_REQUEST.paramsText=paramsText;
	this.PORTLET_AJAX_REQUEST.maxSeqnum=this.ajaxMaxSeqnum;
  }
};

PortletManager.prototype.callbackHtmlPortlet=function(event,node,value){
	while(node!=null){
		if(node.callback)
			return node.callback(event,value);
		else
			node=node.parentNode;
	}
}

function callbackHtmlPortlet(event,node,value){
	portletManager.callbackHtmlPortlet(event,node,value);
}
PortletManager.prototype.onPortalAjaxError=function(o){
	var dialog = document.getElementById("alert_dialog");
	if (!dialog)
		alertDialogWindowsGeneric(alertWarningDialog, ["Web Server not responding", "It appears the web server is not responding.<BR>Please refresh to try again (press F5)", null], this.windows, null);	
}


function getPortletManager(){
	return portletManager;
}




function FormPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  var that=this;
  this.form=new Form(this.portlet,parentId);
  this.form.callBack=function(action,params){that.callBack(action,params);};
  this.portlet.divElement.customCb=function(type,params){that.callBack('customCallback',{customType:type,customParams:params});};
}

FormPortlet.prototype.handleWheel=function(e){
	if(e.target instanceof HTMLCanvasElement){
		//do not handle
	}
	else if(e.target.fieldId == null){
		this.form.formDOMManager.scrollPane.handleWheel(e);
	}
	else{
		var fid = e.target.fieldId;
		var field = this.form.getField(fid);
		if(field == null)
			return;
		if(field.disabled == true)
			this.form.formDOMManager.scrollPane.handleWheel(e);
		else if(field instanceof NumericRangeField && field.slider.isNull == true){
			this.form.formDOMManager.scrollPane.handleWheel(e);
		}
		else if(field instanceof NumericRangeSubRangeField && field.slider.isNull == true){
			this.form.formDOMManager.scrollPane.handleWheel(e);
		}
	}
}
FormPortlet.prototype.setHiddenHtmlLayout=function(htmlLayout,rotate){
 return this.form.setHiddenHtmlLayout(htmlLayout,rotate);
}

FormPortlet.prototype.setHtmlLayout=function(htmlLayout,rotate){
 return this.form.setHtmlLayout(htmlLayout,rotate);
}
FormPortlet.prototype.setButtonStyle=function(buttonHeight,buttonPaddingT,buttonPaddingB,buttonPanelStyle,buttonsStyle,buttonSpacing){
	  return this.form.setButtonStyle(buttonHeight,buttonPaddingT,buttonPaddingB,buttonPanelStyle,buttonsStyle,buttonSpacing);
}
FormPortlet.prototype.setSize=function(width,height){
  return this.form.setSize(width,height);
}
FormPortlet.prototype.setScroll=function(clipLeft, clipTop){
	this.form.setScroll(clipLeft,clipTop);
};

FormPortlet.prototype.repaint=function(){
	return this.form.repaint();
}

FormPortlet.prototype.addFieldAbsolute=function(field,cssStyle,x,y,w,h){
  return this.form.addFieldAbsolute(field,cssStyle,x,y,w,h);
}

FormPortlet.prototype.removeField=function(id){
	  return this.form.removeField(id);
}

FormPortlet.prototype.addField=function(field, cssStyle, isFieldHidden){
  return this.form.addField(field, cssStyle,isFieldHidden);
};

FormPortlet.prototype.setScrollOptions=function(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows, cornerColor){
  return this.form.setScrollOptions(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows,cornerColor);
};

FormPortlet.prototype.setValue=function(fieldId,value,modificationNumber){
  return this.form.setValue(fieldId,value,modificationNumber);
};
FormPortlet.prototype.setHeight=function(fieldId,height){
  return this.form.setHeight(fieldId,height);
};

FormPortlet.prototype.addButton=function(id,name,cssStyle){
  return this.form.addButton(id,name,cssStyle);
};
FormPortlet.prototype.getField=function(fieldId){
  return this.form.getField(fieldId);
};
FormPortlet.prototype.reset=function(){
  return this.form.reset();
};
FormPortlet.prototype.resetButtons=function(){
  return this.form.resetButtons();
};
FormPortlet.prototype.setCssStyle=function(cssStyle){
  return this.form.setCssStyle(cssStyle);
};
FormPortlet.prototype.setLabelWidth=function(labelWidth, labelPadding, labelsStyle,fieldSpacing,widthStretchPadding){
  return this.form.setLabelWidth(labelWidth,labelPadding, labelsStyle,fieldSpacing,widthStretchPadding);
};

FormPortlet.prototype.showContextMenu=function(menu){
  return this.form.showContextMenu(menu);
}

FormPortlet.prototype.showButtonContextMenu=function(menu){
  return this.form.showButtonContextMenu(menu);
}
FormPortlet.prototype.setFieldPosition=function(id,x,y,w,h){
  return this.form.setFieldPosition(id,x,y,w,h);
}
FormPortlet.prototype.setFieldLabelPosition=function(id,x,y,w,h,a,s,padding){
  return this.form.setFieldLabelPosition(id,x,y,w,h,a,s,padding);
}
FormPortlet.prototype.setFieldStyleOptions=function(id, labelColor, bold, italic, underline, fontFamily){
  return this.form.setFieldStyleOptions(id, labelColor, bold, italic, underline, fontFamily);
}
FormPortlet.prototype.setFieldLabelSize=function(id, labelFontSize){
  return this.form.setFieldLabelSize(id, labelFontSize);
}
FormPortlet.prototype.getForm=function(){
  return this.form;
}

FormPortlet.prototype.focusField=function(fieldId){
  return this.form.focusField(fieldId);
}
