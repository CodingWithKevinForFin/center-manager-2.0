//#########################
//##### KeyMouseManager #####

var kmm;

function KeyMouseManager(window){
	this.owningWindow = window;
	var document = window.document;
	document.addEventListener("click", this.clickManager, true);
	document.addEventListener('dblclick', this.dblclickManager, true);
	document.addEventListener('mousedown', this.mousedownManager, true);
	document.addEventListener('mouseup', this.mouseupManager, true);
	document.addEventListener('contextmenu', this.contextmenuManager, true);
	document.addEventListener('mousemove', this.mousemoveManager, true);
	
	document.addEventListener('keydown', this.keydownManager, true);
	document.addEventListener('keyup', this.keyupManager, true);
	document.addEventListener('keypress', this.keypressManager, true);
	document.addEventListener('wheel', this.wheelManager, true);
	
}

KeyMouseManager.prototype.clickedTarget;
KeyMouseManager.prototype.activePortletId;


KeyMouseManager.prototype.activate=function(e){
	this.clickedTarget = e.target;
	
	var div = this.clickedTarget;
	while(div != null && !div.portletId){
		div = div.parentElement;
	}
	var  activePortletId = div!= null? div.portletId: null;
	if(activePortletId!=null)
	  this.setActivePortletId(activePortletId,true);
};
KeyMouseManager.prototype.setActivePortletId=function(portletId,fire){
	if(this.activePortletId == portletId)
		return;
	this.activePortletId = portletId;
	if(this.activePortletId!=null && fire!=false)
  	  portletManager.onUserActivePortlet(this.activePortletId);
}



// For manager methods below this refers to the document.
KeyMouseManager.prototype.clickManager=function(e){
	portletManager.onUserSpecialClick(e,0,kmm.activePortletId);
	if(kmm.activePortletId != null){
		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);
		if(portlet && portlet.handleClick)
			portlet.handleClick(e);
	}
};

KeyMouseManager.prototype.dblclickManager=function(e){
	portletManager.onUserSpecialClick(e,2,kmm.activePortletId);
	if(kmm.activePortletId != null){
		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);
		if(portlet && portlet.handleDblclick)
			portlet.handleDblclick(e);
	}
};
KeyMouseManager.prototype.mousedownManager=function(e){
	kmm.activate(e);
	portletManager.onUserSpecialClick(e,3,kmm.activePortletId);
	if(kmm.activePortletId != null){
		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);
		if(portlet && portlet.handleMousedown)
			portlet.handleMousedown(e);
	}
};
KeyMouseManager.prototype.mouseupManager=function(e){
	portletManager.onUserSpecialClick(e,4,kmm.activePortletId);
	if(kmm.activePortletId != null){
		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);
		if(portlet && portlet.handleMouseup)
			portlet.handleMouseup(e);
	}
};
KeyMouseManager.prototype.mousemoveManager=function(e){
	if(currentContextMenu){
		currentContextMenu.handleMousemove(e);
	}
}
KeyMouseManager.prototype.wheelManager=function(e){
	kmm.activate(e);
	if(currentContextMenu){
		currentContextMenu.handleWheel(e);
	}
	else if(kmm.activePortletId != null){
		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);
		if(portlet && portlet.handleWheel)
			portlet.handleWheel(e);
	}
	if(typeof disableWheel == 'undefined' || disableWheel == false){
		wheel(e,true);
	}
}

KeyMouseManager.prototype.contextmenuManager=function(e){
	portletManager.onUserSpecialClick(e,1,kmm.activePortletId);
	if(kmm.activePortletId != null){
		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);
		if(portlet && portlet.handleContextmenu)
			portlet.handleContextmenu(e);
	}
};

KeyMouseManager.prototype.keydownManager=function(e){
	kmm.stopPropagationToBrowser(e);
	var tgtType=document.activeElement.tagName;
	if(currentGlass){
		currentGlass.handleKeydown(e);
	}
	if(currentContextMenu){
		if(currentContextMenu.bypass != true)
			currentContextMenu.handleKeydown(e);
		else if(currentContextMenu.customHandleKeydown != null){
			currentContextMenu.customHandleKeydown(e);
		}
	}
	else if(currentColorPicker){
		currentColorPicker.handleKeydown(e);
	}
	else if(currentHelpBox){
		currentHelpBox.handleKeydown(e);
	}
	else { 
	 	  portletManager.onUserSpecialKey(e);
	}
	if(kmm.clickedTarget && kmm.activePortletId){
		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);
		if(portlet && portlet.handleKeydown)
			portlet.handleKeydown(e, kmm.clickedTarget);
	}
};

KeyMouseManager.prototype.keyupManager=function(e){
	kmm.stopPropagationToBrowser(e);
	if(currentContextMenu){
		if(currentContextMenu.bypass != true)
			e.stopPropagation();
	}
	else if(kmm.clickedTarget && kmm.activePortletId){
		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);
		if(portlet && portlet.handleKeyup)
			portlet.handleKeyup(e, kmm.clickedTarget);
	}
};

KeyMouseManager.prototype.keypressManager=function(e){
	kmm.stopPropagationToBrowser(e);
	if(currentContextMenu){
		currentContextMenu.handleKeypress(e);
	}
	else if(kmm.clickedTarget && kmm.activePortletId){
		var portlet = portletManager.getPortletNoThrow(kmm.activePortletId);
		if(portlet && portlet.handleKeypress)
			portlet.handleKeypress(e, kmm.clickedTarget);
	}
};

KeyMouseManager.prototype.stopPropagationToBrowser=function(e){
	if(e.ctrlKey){
	  if(e.key=="d"){
	    e.stopPropagation();
	    e.preventDefault();
	  }
	}else if(e.altKey){
	  if(e.key=="f" || e.key=="d"){
	    e.stopPropagation();
	    e.preventDefault();
	  }
	}
}

