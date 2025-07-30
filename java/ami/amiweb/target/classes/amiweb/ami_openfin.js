//AMI Interface to Openfin's JS
function OpenFinServer(){
	var that=this;
	if (typeof finsemble !== 'undefined' && finsemble) // ignore if running in Finsemble
		return;
	//Look for OpenFin JS Object
	if(typeof fin !== 'undefined'){
		if (window.fdc3) {
			this.init();
		} else
			window.addEventListener('fdc3Ready', that.init());
		// override clipboard logic using OpenFin's own Clipboard API
		copyToClipboard=function(text) {
			// use openfin's API first
			 try {
				  fin.Clipboard.writeText({
					  data: text
				  });
			  } catch (err) {
				  console.error('unable to copy to clipboard in OpenFin... falling back to standard clipboard logic');
				  // otherwise use our normal clipboard logic
				  if (!navigator.clipboard) {
					  fallbackCopyTextToClipboard(text);
					  return;
				  }
				  try {
					  navigator.clipboard.writeText(text);
				  } catch (err) {
					  console.error('Fallback: Unable to copy', err);
				  }
			  }
		}
	}
	else{
		console.error('The fin API is not available - you are not running in OpenFin.');
	}
}

OpenFinServer.prototype.isInitialized= false;
OpenFinServer.prototype.peer = null; // AMI GUI PEER 
OpenFinServer.prototype.channelProviders=null;
OpenFinServer.prototype.channelClients=null;
OpenFinServer.prototype.hasNotificationListener=false;

OpenFinServer.prototype.responseCorrelationId=0;
OpenFinServer.prototype.responsePromises = new Map();

OpenFinServer.prototype.getNextId=function(){
	return ""+(this.responseCorrelationId++);
}

OpenFinServer.prototype.init=function(){
	this.channelProviders = new Map();
	this.channelClients = new Map();
	this.listeners = new Array();
	this.subscriptions = new Array();
	this.isInitialized=true;
	this.getFdcVersion();
	if(this.peer != null)
		this.sendOnInit();
}

OpenFinServer.prototype.getFdcVersion=async function(){
	const info=await fdc3.getInfo();
	this.fdc=info.fdc3Version;
}

OpenFinServer.prototype.checkInitialized=function(method, error){
	if(!this.isInitialized){
		this.peer.sendToServer('onError',method,error);
		return false;
	}
	return true;
}

OpenFinServer.prototype.raiseIntent=async function(intent, context){
	if (this.checkInitialized()==false) {
		return;
	}
	try {
		var intentResolution=await fdc3.raiseIntent(intent, context);
		if (this.fdc==="2.0") {
			const result = await intentResolution.getResult();
			result ? this.peer.sendToServer('onRaiseIntent', result): this.peer.sendToServer('onRaiseIntent', intentResolution); 
		} else {
			this.peer.sendToServer('onRaiseIntent', intentResolution);
		}
	}catch (err) {
		this.peer.sendToServer('onError','raiseIntent', err.message);
		return;
	}
}

OpenFinServer.prototype.raiseIntentByApp=async function(intent, context, appId){
	if (this.checkInitialized()==false) {
		return;
	}
	try {
		var intentResolution=await fdc3.raiseIntent(intent, context, appId);
		this.peer.sendToServer('onRaiseIntent', intentResolution);
	}catch (err) {
		this.peer.sendToServer('onError','raiseIntent', err.message);
		return;
	}
}

//broadcast this context to system channels
OpenFinServer.prototype.broadcast=async function(context){
	if (this.checkInitialized()==false) {
		return;
	}
	try {
		var res = await fdc3.broadcast(context);
  	} catch (err){
  		this.peer.sendToServer('onError','broadcast', err.message);
  		return;
  	}
}
// to receive intents
OpenFinServer.prototype.addIntentListener=async function(intent){
	// it looks like fdc3 doesn't have a remove func for this
	if (this.checkInitialized()==false) {
		return;
	}
	var that=this;
	const handler = (context) => {
		that.peer.sendToServer('onReceiveIntent',context);
	};
	try {
		const res=fdc3.addIntentListener(intent, handler);
	} catch (err) {
		this.peer.sendToServer('onError', 'addIntentListener', err.message);
	}
}
// receive broadcast
OpenFinServer.prototype.addContextListener=async function(contextType){
	if (this.checkInitialized()==false) {
		return;
	}
	var that = this;
	try {
		// can call unsubscribe
	  var res = await fdc3.addContextListener(contextType, (context, metadata) => {
			  that.peer.sendToServer('onContext',context,metadata);
		  }
	  );
	  this.listeners.push(res);
	} catch (err) {
	  this.peer.sendToServer('onError','addContextListener', err.message);
	  return;
	}
}

function isEmpty(str) {
    return (!str || str.length === 0 );
}

// fdc3
OpenFinServer.prototype.sendNotification=async function(options){
	if (options == null) {
	  this.peer.sendToServer('onError','sendNotification', 'options cannot be null');
	  return;
	}
	var bodyTitle=options.bodyTitle;
	var bodyText=options.bodyText;
	if (isEmpty(bodyTitle) || isEmpty(bodyText)) {
	  this.peer.sendToServer('onError','sendNotification', 'body title and/or body text must be non-empty string');
	  return;
	}
	var that=this;
	var indColor=options.indicatorColor;
	var indText =options.indicatorText;
	var iconUrl=options.iconUrl;
	var buttonsList = [];
	const prefix = 'button';
	var num = 1;
	// maximum notification is 8, per OpenFin
	while (options[prefix+num+'title'] != null && num < 9) {
		var btnObj= new Object();
		btnObj.submit=false; // not a form
		btnObj.onClick={
				data: options[prefix+num+'data']
		};
		btnObj.index=num-1;
		btnObj.iconUrl=options[prefix+num+'iconUrl'];
		var primary=options[prefix+num+'primary'];
		btnObj.cta=primary == null ? true:primary; // primary, default to true if null
		btnObj.title=options[prefix+num+'title'];
		btnObj.type='button';
		buttonsList.push(btnObj);
		num++;
	}

	const notif = 
    {
	  customData: options.customData,
      indicator: 
      {
        color: indColor,
        text: indText,
      },

      icon: iconUrl, 
      title: bodyTitle, // required
      body: bodyText, // required
      buttons: buttonsList
    };
	notifications.create(notif);
	if (this.hasNotificationListener() == false) {
		notifications.__3fhasListener = true;
		notifications.addEventListener('notification-action', (ev)=> {
			that.peer.sendToServer('onNotificationAction', JSON.stringify(ev));
		});
	}
	
}


OpenFinServer.prototype.hasNotificationListener=function(){
	return notifications.__3fhasListener == true;
}

OpenFinServer.prototype.sendOnInit=async function(){
	let app = await fin.Application.getCurrent();
	let identity = app.window.identity;
	this.peer.sendToServer('onInit', identity);
}

OpenFinServer.prototype.registerAmiGuiServicePeer=function(peer){
	this.peer=peer;
	if(this.isInitialized)
		this.sendOnInit();
}

// brings openfin window/view to front
OpenFinServer.prototype.setAsForeground=async function(){
	let type=fin.me.entityType;
	if (type === 'window') {
		let win = fin.Window.getCurrentSync();
		await win.setAsForeground();
	} else if (type==='view') {
		let viewIdentity = fin.View.getCurrentSync().identity;
		let view = fin.View.wrapSync(viewIdentity);
		let win = await view.getCurrentWindow();
		await win.setAsForeground();
	} else {
		this.peer.sendToServer('onError',  'setAsForeground','manifest type ', type,' is not supported for this operation. Only Window/View or their inline variants are supported.');
	}
}

// brings AMI window to the front of OpenFin stack
OpenFinServer.prototype.bringToFront=async function(){
	let type=fin.me.entityType;
	if (type === 'window') {
		let win = fin.Window.getCurrentSync();
		await win.bringToFront();
	} else if (type==='view') {
		let viewIdentity = fin.View.getCurrentSync().identity;
		let view = fin.View.wrapSync(viewIdentity);
		let win = await view.getCurrentWindow();
		await win.bringToFront();
	} else {
		this.peer.sendToServer('onError',  'bringToFront','manifest type ', type,' is not supported for this operation. Only Window/View or their inline variants are supported.');
	}
}
OpenFinServer.prototype.addListener=function(channel){
	if(!this.checkInitialized("addListener", "OpenFin fin not initialized"))
		return;
	
	var that = this;
	var l = function(sub_msg, sub_uuid, sub_name) {that.peer.sendToServer('onMessage', channel, sub_msg)};
	fin.InterApplicationBus.subscribe({uuid: "*"}, channel, l).catch(error => that.peer.sendToServer('onError', 'addListener', 'for channel '+channel+': ' +JSON.stringify(error)));
	this.subscriptions.push([channel, l]);	
}
OpenFinServer.prototype.sendMessage=function(channel, data){
	if(!this.checkInitialized("sendMessage", "OpenFin fin not initialized"))
		return;
	
	var that = this;
	fin.InterApplicationBus.publish(channel,data).catch(error => that.peer.sendToServer('onError', 'sendMessage', 'for channel '+channel+': ' +JSON.stringify(error)));
	// no need to trigger callback here, already handled
}
OpenFinServer.prototype.createProviderChannel=async function(channelId){
	if(!this.checkInitialized("createProviderChannel", "OpenFin fin not initialized"))
		return;
	if(!this.channelProviders.has(channelId)){
		let listener = await fin.InterApplicationBus.Channel.create(channelId, {}).catch(error => that.peer.sendToServer("onError", 'createProviderChannel', error));
		
		if(listener!= null)
			this.channelProviders.set(channelId, listener);
		else
			this.peer.sendToServer('onError', 'createProviderChannel', 'could not create channel ' + channelId );
	}
	else {
		this.peer.sendToServer('onError', 'createProviderChannel', 'is already listening to channel ' + channelId );
	}

}
OpenFinServer.prototype.destroyProviderChannel=async function(channelId){
	if(!this.checkInitialized("destroyProviderChannel", "OpenFin fin not initialized"))
		return;

	if(this.channelProviders.has(channelId)){
		let listener = this.channelProviders.get(channelId);
		await listener.destroy().catch(error => that.peer.sendToServer("onError", 'destroyProviderChannel', error));
		this.channelProviders.delete(channelId);
	}
	else{
		this.peer.sendToServer('onError', 'destroyProviderChannel', 'channel ' + channelId + ' does not exist');
	}

}
OpenFinServer.prototype.connectClientChannel=async function(channelId){
	if(!this.checkInitialized("connectClientChannel", "OpenFin fin not initialized"))
		return;
	if(!this.channelClients.has(channelId)){
		let listener = await fin.InterApplicationBus.Channel.connect(channelId).catch(error => that.peer.sendToServer("onError", 'connectClientChannel', error));

		if(listener!= null)
			this.channelClients.set(channelId, listener);
		else
			this.peer.sendToServer('onError', 'connectClientChannel', 'could not create channel ' + channelId );
	}
	else {
		this.peer.sendToServer('onError', 'connectClientChannel', 'channel ' + channelId + ' is already connected');
	}

}
OpenFinServer.prototype.disconnectClientChannel=async function(channelId){
	if(!this.checkInitialized("disconnectClientChannel", "OpenFin fin not initialized"))
		return;
	
	if(this.channelClients.has(channelId)){
		let listener = this.channelClients.get(channelId);
		await listener.disconnect().catch(error => that.peer.sendToServer("onError", 'disconnectClientChannel', error));
		this.channelClients.delete(channelId);
	}
	else{
		this.peer.sendToServer('onError', 'disconnectClientChannel', 'client channel ' + channelId + ' does not exist');
	}
}
OpenFinServer.prototype.registerProviderChannelListener=async function(channelId, action){
	if(!this.checkInitialized("registerProviderChannelListener", "OpenFin fin not initialized"))
		return;
	if(this.channelProviders.has(channelId)){
		let listener = this.channelProviders.get(channelId);
		if(!listener.subscriptions.has(action)){
			var that = this;
			
			await listener.register(action, async function(payload, identity){
				return (
						function(){
							var responseId = that.getNextId();
							var p = new Promise((resolve, reset) => { that.responsePromises.set(responseId, resolve); })
							async function response(){
								const result = await p;
								return result;
							}
							var response = response();
							that.peer.sendToServer('onChannelRequest', channelId, action, payload, identity, responseId);
							return response;
							
						})();
			});
		}
		else
			this.peer.sendToServer('onError', 'registerProviderChannelListener', 'channel ' + channelId + ' is already listening for action '+action);
	}
	else{
		this.peer.sendToServer('onError', 'registerProviderChannelListener', 'channel ' + channelId + ' does not exist');
	}

}
OpenFinServer.prototype.removeProviderChannelListener=async function(channelId, action){
	if(!this.checkInitialized("removeProviderChannelListener", "OpenFin fin not initialized"))
		return;
	if(this.channelProviders.has(channelId)){
		let listener = this.channelProviders.get(channelId);
		
		if(listener.subscriptions.has(action)){
			await listener.remove(action);
		}
		else
			this.peer.sendToServer('onError', 'removeProviderChannelListener', 'channel ' + channelId + ' is not listening for action ' +action);
	}
	else{
		this.peer.sendToServer('onError', 'removeProviderChannelListener', 'channel ' + channelId + ' does not exist');
	}

}
OpenFinServer.prototype.registerClientChannelListener=async function(channelId, action){
	if(!this.checkInitialized("registerClientChannelListener", "OpenFin fin not initialized"))
		return;
	if(this.channelClients.has(channelId)){
		let listener = this.channelClients.get(channelId);
		if(!listener.subscriptions.has(action)){
			var that = this;
			
			await listener.register(action, async function(payload, identity){
				return (
						function(){
							var responseId = that.getNextId();
							var p = new Promise((resolve, reset) => { that.responsePromises.set(responseId, resolve); })
							async function response(){
								const result = await p;
								return result;
							}
							var response = response();
							that.peer.sendToServer('onChannelRequest', channelId, action, payload, identity, responseId);
							return response;
							
						})();
			});
		}
		else
			this.peer.sendToServer('onError', 'registerClientChannelListener', 'channel ' + channelId + ' is already listening for action '+action);
	}
	else{
		this.peer.sendToServer('onError', 'registerClientChannelListener', 'channel ' + channelId + ' does not exist');
	}

}
OpenFinServer.prototype.removeClientChannelListener=async function(channelId, action){
	if(!this.checkInitialized("removeClientChannelListener", "OpenFin fin not initialized"))
		return;
	if(this.channelClients.has(channelId)){
		let listener = this.channelClients.get(channelId);

		if(listener.subscriptions.has(action)){
			await listener.remove(action);
		}
		else
			this.peer.sendToServer('onError', 'removeClientChannelListener', 'is not listening for action ' +action+ ' on channel ' +channelId);
	}
	else{
		this.peer.sendToServer('onError', 'removeClientChannelListener', 'is not listening for channel '+channelId);
	}

}

OpenFinServer.prototype.sendProviderChannelRequest=async function(requestCorrelationId, channelId, action, requestPayload, identity){
	if(!this.checkInitialized("sendProviderChannelRequest", "OpenFin fin not initialized"))
		return;
	var that = this;
	if(this.channelProviders.has(channelId)){
		const provider = this.channelProviders.get(channelId);
		var response=await provider.dispatch(identity, action, requestPayload).catch(error => that.peer.sendToServer("onError", 'sendProviderChannelRequest', error));
		this.peer.sendToServer('onChannelResponse',requestCorrelationId, "STATUS",response, channelId);
	}
	else{
		this.peer.sendToServer('onError', 'sendProviderChannelRequest', 'channel '+channelId + ' not found');
	}
}
OpenFinServer.prototype.sendClientChannelRequest=async function(requestCorrelationId, channelId, action, requestPayload){
	if(!this.checkInitialized("sendClientChannelRequest", "OpenFin fin not initialized"))
		return;
	
	var that=this;
	if(this.channelClients.has(channelId)){
		const client = this.channelClients.get(channelId);	
		var response=await client.dispatch(action, requestPayload).catch(error => that.peer.sendToServer("onError", 'sendClientChannelRequest', error));
		this.peer.sendToServer('onChannelResponse',requestCorrelationId, "SUCCESS",response, channelId);
	}
	else{
		this.peer.sendToServer('onError', 'sendClientChannelRequest', 'channel '+channelId + ' not found');
	}
}
OpenFinServer.prototype.sendChannelResponse= function(correlationId, responsePayload){
	if(!this.checkInitialized("sendChannelResponse", "OpenFin fin not initialized"))
		return;
	if(this.responsePromises.has(correlationId)){
		var resume = this.responsePromises.get(correlationId);
		if(resume == null)
			this.peer.sendToServer('onError', 'sendChannelResponse', 'could not not find request with correlationId ' + correlationId);
		this.responsePromises.delete(correlationId);
		resume(responsePayload);
	}
	else
		this.peer.sendToServer('onError', 'sendChannelResponse', 'could not not find request with correlationId ' + correlationId);
}


OpenFinServer.prototype.maximizeWindow=async function(windowId){
	let amiWindow = portletManager.getWindow(windowId);
	if (!amiWindow) {
		this.peer.sendToServer('onError', 'maximizeWindow', 'could not find window ' + windowId);
		return;
	}
	const win = await amiWindow.fin.Window.getCurrent();
	win.maximize();
}
OpenFinServer.prototype.restoreWindow=async function(windowId){
	let amiWindow = portletManager.getWindow(windowId);
	if (!amiWindow) {
		this.peer.sendToServer('onError', 'restoreWindow', 'could not find window ' + windowId);
		return;
	}
	const win = await amiWindow.fin.Window.getCurrent();
	win.restore();
}
OpenFinServer.prototype.minimizeWindow=async function(windowId){
	let amiWindow = portletManager.getWindow(windowId);
	if (!amiWindow) {
		this.peer.sendToServer('onError', 'minimizeWindow', 'could not find window ' + windowId);
		return;
	}
	const win = await amiWindow.fin.Window.getCurrent();
	win.minimize();
}
OpenFinServer.prototype.setLocation=async function(windowId, left, top, width, height){
	let amiWindow = portletManager.getWindow(windowId);
	if (!amiWindow) {
		this.peer.sendToServer('onError', 'setLocation', 'could not find window ' + windowId);
		return;
	}
	const win = await amiWindow.fin.Window.getCurrent();
	
	win.moveTo(left, top);
	win.resizeTo(width, height, "top-left");
}

OpenFinServer.prototype.open=async function(appInfo, context){
	if (this.checkInitialized()==false) {
		return;
	}
	try {
		let meta;
		if (context) {
			meta=await fdc3.open(appInfo, context);
		}
		else
			meta=await fdc3.open(appInfo);
		//map of appId and instanceId
	}catch (err) {
		this.peer.sendToServer('onError','open', err.message);
		return;
	}
}

OpenFinServer.prototype.closePlugin=function(){
	// listener.unsubscribe()
	console.log("closing openfin!");
	if (this.listeners != null) {
		// handles channel and context listeners
		for (var l of this.listeners) {
			l.unsubscribe();
		}
	}
	if (this.subscriptions != null) {
		// only handles addListener's listeners
		for (var s of this.subscriptions) {
			var channel = s[0];
			var handler = s[1];
			fin.InterApplicationBus.unsubscribe({uuid:'*'}, channel,handler);
		}
	}
}