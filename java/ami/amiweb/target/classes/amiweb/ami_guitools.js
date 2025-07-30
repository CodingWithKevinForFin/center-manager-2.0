function GuiTools(){
	var that=this;
	this.init=false;
	if(document != null){
		document.addEventListener('onLoad', function(){that.sendInit();});
	}
	else
		if(window != null)
			window.addEventListener('onLoad', function(){that.sendInit();});
}

GuiTools.prototype.init;

GuiTools.prototype.registerAmiGuiServicePeer=function(peer){
	this.peer=peer;
	this.init=true;
	this.sendInit();
}

GuiTools.prototype.sendInit=function(){
	if(this.peer != null && this.init==true)
		this.peer.sendToServer('onInit', {});
}


GuiTools.prototype.fetch=async function(resource, options, correlationId){
	try{
		const response = await fetch(resource, options);
		try{
			var rw = {}; 
			rw.ok = response.ok;
			rw.url = response.url;
			rw.type = response.type;
			rw.status = response.status;
			rw.statusText = response.statusText;
			rw.headers = response.headers;
			rw.redirected = response.redirected;
			const blob = await response.blob();
			rw.blob = {type:blob.type};
			const ab = await blob.arrayBuffer();

			var b64 = btoa(String.fromCharCode(...new Uint8Array(ab)));
			rw.blob.dataBase64 = b64;
//			var decoder = new TextDecoder();
//			var decoded = decoder.decode(ab);
//			var reader = new FileReader();
//			var that = this;
//			reader.onload = function() {
//				rw.blob = reader.result;
//
//				that.peer.sendToServer('onResponse', correlationId, rw);
//		    }; 
//		    reader.onerror = function(err) {
//		        rw.error=reader.error;
//		        that.peer.sendToServer('onResponse', correlationId, rw);
//		    }; 
//		    reader.readAsDataURL(blob);
			
			this.peer.sendToServer('onResponse', correlationId, rw);
		} catch (error){
			var errDetails = {msg: 'Error occured getting response data', response:response, resource:resource, options: options, err:error.toString(), id: correlationId};
			this.peer.sendToServer('onError',errDetails);
		}
	} catch (error){
		var errDetails = {msg: 'Error occured sending fetch', resource:resource, options: options, err:error.toString(), id: correlationId};
		this.peer.sendToServer('onError',errDetails);
	}
}