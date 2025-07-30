function getTextWidth(text, font){
	var context = getTextWidth.canvas.getContext("2d");
    context.font = font;
    var metrics = context.measureText(text);
    return metrics.width;
}
getTextWidth.canvas = nw("canvas");

function HelpBox(window, hasGlass, cssClass,outerCssClass){
	var that = this;
	this.window=window;
	this.outElement=nw("div");
	this.outElement.style.top="0px";
	this.outElement.style.left="0px";
	this.outElement.style.bottom="0px";
	this.outElement.style.right="0px";
	this.outElement.style.userSelect="inherit";
	this.divElement=nw("div");
	this.outElement.appendChild(this.divElement);
	this.glassElement=nw("div");
	var nostyle = "_cna=ami_help_box_nostyle";
	// we need the LAYOUT_DEFAULT classname to enable the user css class
	if(outerCssClass != null)
		applyStyle(this.outElement,outerCssClass);
	applyStyle(this.divElement,cssClass==null?nostyle:cssClass);
	this.hasGlass = hasGlass == null? true: hasGlass;
	this.glassElement.classList.add("disable_glass_clear");
}

currentHelpBox = null;
HelpBox.prototype.window;
HelpBox.prototype.divElement;
HelpBox.prototype.outElement;
HelpBox.prototype.glassElement;
HelpBox.prototype.helpText;
HelpBox.prototype.textWidth;
HelpBox.prototype.hasGlass;

HelpBox.prototype.init=function(text, textWidth){
	if(text != null)
		this.setText(text);
	if(textWidth != null)
		this.setTextWidth(textWidth);
}

HelpBox.prototype.setText=function(text){
	this.divElement.innerHTML = text;
	this.helpText = text;
}

HelpBox.prototype.setTextWidth=function(textWidth){
	this.textWidth = textWidth;
}

//HelpBox.prototype.autoSize=function(){
//	var textWidth = this.textWidth != null? this.textWidth : getTextWidth(this.helpText, getComputedStyle(this.divElement).font);
//	var widthToHeightR = 5/1;
//	var lineHeight = parseInt(getComputedStyle(this.divElement).fontSize, 10) + 2;
//	
//	var boxWidth = Math.sqrt(widthToHeightR*(textWidth * lineHeight));
//	
//	var minWidth = 180;
//	boxWidth = boxWidth > minWidth ? boxWidth: minWidth; 
//}
HelpBox.prototype.autoPosition=function(mouseEvent){
	var r = mouseEvent.target.getBoundingClientRect();
	this.divElement.style.top = toPx(r.bottom);
	this.divElement.style.left = toPx(mouseEvent.x-10);
	ensureInWindow(this.divElement);
	
}

HelpBox.prototype.show=function(mouseEvent){
	if(currentHelpBox != null){
		currentHelpBox.hide();
	}
	currentHelpBox = this;
	if(this.hasGlass==true)
		this.window.document.body.appendChild(this.glassElement);
	this.window.document.body.appendChild(this.outElement);
//	this.autoSize();
	this.autoPosition(mouseEvent);
}

HelpBox.prototype.showIfMouseInside=function(mouseEvent, element){
	var inside = isMouseInside(mouseEvent, element, null);
	if(inside==true)
		this.show(mouseEvent);
}

HelpBox.prototype.hide=function(){
	if(currentHelpBox != null){
		this.window.document.body.removeChild(this.outElement);
		if(this.hasGlass)
			this.window.document.body.removeChild(this.glassElement);
		currentHelpBox = null;
	}
}

HelpBox.prototype.autoHide=function(state, element){
	var that = this;
	if(state == true || state == null){
		if(this.divElement.onmouseleave == null){
			this.glassElement.onmousemove=function(e){
				var inside = isMouseInside(e, element, null);
				if(!inside)	that.hide();};
		}
	}
	else{
		this.glassElement.onmousemove = null;
		this.glassElement.onmousedown=function(e){
			that.hide(e, that.label);
		};
	}
	
}

HelpBox.prototype.handleKeydown=function(e){
	if(e.key === "Escape" && e.shiftKey == false && e.ctrlKey == false && e.altKey == false){
		this.hide();
	}
}