function Form(portlet, parentId){
    var that = this;
    this.portlet=portlet;
    this.location=this.portlet.location;
    this.formDOMManager = new FormDOMManager(that, this.portlet.divElement);
}
Form.prototype.portlet;
Form.prototype.location;
Form.prototype.formDOMManager;
Form.prototype._activeField;

Form.prototype.getId=function(e,target){
	return this.portlet.instance.portletId;
}
Form.prototype.handleKeydown=function(e,target){

}
Form.prototype.setHtmlLayout=function(htmlLayout,rotate){
    this.rotate=rotate;
    var hasHtmlLayout = this.htmlLayout!=null;
    var hasHiddenHtmlLayout = this.hiddenHtmlLayout != null;
	if(hasHtmlLayout){
	  this.htmlLayout=null;
	}
	if(htmlLayout!=null){
	  this.hiddenHtmlLayout=htmlLayout;
	}else if(hasHiddenHtmlLayout){
		this.hiddenHtmlLayout = null;
    }
    this.formDOMManager.setHtmlLayout(hasHtmlLayout, hasHiddenHtmlLayout, htmlLayout);
}

Form.prototype.setButtonStyle=function(buttonHeight,buttonPaddingT,buttonPaddingB,buttonPanelStyle,buttonsStyle,buttonSpacing){
	this.buttonHeight= buttonHeight;
	this.buttonPaddingT= buttonPaddingT;
	this.buttonPaddingB= buttonPaddingB;
	this.buttonPanelStyle=buttonPanelStyle;
	this.buttonSpacing=buttonSpacing;
	this.buttonsStyle=buttonsStyle;
    this.updateLayout();
    this.formDOMManager.setButtonStyle(this.buttonPaddingT, this.buttonPaddingB);
}
Form.prototype.updateLayout=function(width,height){
	var buttonsHeight=this.hasButtons ? toPx(this.buttonPaddingB+this.buttonPaddingT+this.buttonHeight) : "0px";
	this.buttonsContainerHeight = buttonsHeight;
    this.formDOMManager.updateLayout(buttonsHeight);
}
//Form.prototype.handleKeyDown=function(e){
//	if (e.keyCode==37 || e.keyCode==38 || e.keyCode==39 || e.keyCode==40)
//		this.onUserDirectionKey(e);
//}
Form.prototype.setScroll=function(clipLeft, clipTop){
    this.clipTop=clipTop;
    this.clipLeft=clipLeft;
	this.formDOMManager.scrollPane.setClipTop(clipTop);
	this.formDOMManager.scrollPane.setClipLeft(clipLeft);
	this.onScroll();
}
Form.prototype.onScroll=function(){
	this.formDOMManager.scrollPane.DOM.innerpaneElement.style.top=toPx(-this.formDOMManager.scrollPane.getClipTop());
	this.formDOMManager.scrollPane.DOM.innerpaneElement.style.left=toPx(-this.formDOMManager.scrollPane.getClipLeft());
	if((this.clipTop == this.formDOMManager.scrollPane.getClipTop()) && (this.clipLeft == this.formDOMManager.scrollPane.getClipLeft()))
		return;
//	portletManager.onUserSpecialScroll(this.formDOMManager.scrollPane.getClipLeft(), this.formDOMManager.scrollPane.getClipTop(), this.portlet.divElement.id);
	portletManager.onUserSpecialScroll(this.formDOMManager.scrollPane.getClipLeft(), this.formDOMManager.scrollPane.getClipTop(), this.portlet.instance.portletId);
	this.clipTop = this.formDOMManager.scrollPane.getClipTop();
	this.clipLeft = this.formDOMManager.scrollPane.getClipLeft();
}
Form.prototype.getContainerSize=function(){
	var w = 0;//this.width-this.scrollPane.scrollSize;
	var h = 0;//this.height-this.scrollPane.scrollSize;
	var containerLeft = this.formDOMManager.formContainer.getBoundingClientRect().left;
	var containerTop = this.formDOMManager.formContainer.getBoundingClientRect().top;
	for(var i in this.fields){
		var field = this.fields[i];
		if(field != null && field.container != null && field.label != null){
			var labelRight = field.label.getBoundingClientRect().right - containerLeft;
			var fieldRight = field.container.getBoundingClientRect().right - containerLeft;
			
			w = Math.max(w, fieldRight, labelRight);
			var labelBottom = field.label.getBoundingClientRect().bottom - containerTop;
			var fieldBottom = field.container.getBoundingClientRect().bottom - containerTop;
			h = Math.max(h, fieldBottom, labelBottom);
		}
	}
//    if(this.width > w && this.height > h){
//		w = this.width;
//		h = this.height;
//	}
	return {w:w,h:h};
}
Form.prototype.getButtonsHeight=function(){
	if(this.hasButtons)
		return this.buttonPaddingB+this.buttonPaddingT+this.buttonHeight;
	else
		return 0;
}
Form.prototype.setSize=function(width,height){
	//Window Size
	this.width = width;
    this.height = height;
    this.formDOMManager.setSize(width,height);
}
Form.prototype.repaint=function(){
	//Contained Size
	if(this.width == undefined || this.height == undefined)
		return;
    this.formDOMManager.repaint();
	this.formDOMManager.scrollPane.setClipTop(this.clipTop);
	this.formDOMManager.scrollPane.setClipLeft(this.clipLeft);
	this.onScroll();
}
Form.prototype.getInputWidth=function(){
	return this.location.width-this.labelWidth;
}
Form.prototype.getWidthStretchPadding=function(){
	return this.widthStretchPadding;
}
Form.prototype.getInputsWidth=function(){
  return this.location.width-this.labelWidth;
}
Form.prototype.onTab=function(field, e){
	e.preventDefault();
//	this.onKey(field.getId(),e,{pos:-1});
}

Form.prototype.addField=function(field, cssStyle, isFieldHidden){
    this.fields[field.getId()]=field;
    field.form=this;
    this.formDOMManager.addField(field,cssStyle, isFieldHidden);
    field.setHidden(isFieldHidden);
   
}
Form.prototype.removeField=function(id){
    field=this.fields[id];
    if(field==null)
    	return;
    delete this.fields[id];
    field.form=null;
    this.formDOMManager.removeField(field);
   
}
Form.prototype.onChange=function(fieldId,values){
	values.fieldId=fieldId;
	values.mid = this.fields[fieldId].getModificationNumber(); 
//	err(["Form.onChange",...arguments,values.mid]);
	this.callBack('onchange',values);
}
Form.prototype.onKey=function(fieldId,e,values){
	values.fieldId=fieldId;
	values.keycode=e.keyCode;
	values.shift=e.shiftKey ? true : false;
	values.ctrl=e.ctrlKey ? true : false;
	values.alt=e.altKey ? true : false;
	this.callBack('onkey',values);
}
Form.prototype.setScrollOptions=function(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows, cornerCl){
	this.formDOMManager.setScrollOptions(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows, cornerCl);
}
Form.prototype.addButton=function(id,name,cssStyle){
    return this.formDOMManager.addButton(id,name,cssStyle);
}

Form.prototype.onButton=function(buttonId){
	this.callBack('onbutton',{buttonId:buttonId});
}
Form.prototype.onClick=function(fieldId,values){
	values.fieldId=fieldId;
	this.callBack('onclick',{fieldId:fieldId});
}
Form.prototype.getField=function(fieldId){
	var r=this.fields[fieldId];
	if(r==null)
		alert("field not found for portlet "+this.getId()+": "+fieldId);
	return r;
}

Form.prototype.reset=function(){
	this.fields=[];
    this.hasButtons=false;
    this.formDOMManager.reset();
}
Form.prototype.resetButtons=function(){
    this.hasButtons=false;
    this.formDOMManager.resetButtons();
}

Form.prototype.setCssStyle=function(cssStyle){
	this.formDOMManager.setCssStyle(cssStyle);
}

Form.prototype.setLabelWidth=function(labelWidth, labelPadding, labelsStyle,fieldSpacing,widthStretchPadding){
	if(labelWidth==0)
		labelWidth=1;
	this.labelsStyle=labelsStyle;
	this.fieldSpacing=fieldSpacing;
	this.labelWidth=labelWidth;
	this.widthStretchPadding=widthStretchPadding;
	this.labelPaddingPx=toPx(labelPadding); 
	
}

Form.prototype.showButtonContextMenu=function(menu){
	   this.createMenu(menu,true).show(this.contextMenuPoint);
}
Form.prototype.createMenu=function(menu,isButton,fieldId){
   var that=this;
   that.btn=isButton;
   var r=new Menu(getWindow(this.portlet.divElement));
   r.setFieldId(fieldId);
   r.onClose=function(){
	   if (this.fieldId)
		   that.focusField(this.fieldId);
   };
   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e, that.btn, that.contextMenuFieldid, id);} );
   return r;
}
Form.prototype.showContextMenu=function(menu,fieldId){
	this.createMenu(menu,false,fieldId).show(this.contextMenuPoint);
}

Form.prototype.onUserContextMenu=function(e,fieldid,cursorPos){
  this.contextMenuPoint=getMousePoint(e).move(-4,-4);
  this.contextMenuFieldid=fieldid;
  this.callBack('menu',{'fieldId':fieldid,'cursorPos':cursorPos});
}

Form.prototype.onUserButtonContextMenu=function(e,buttonId,cursorPos){
    this.contextMenuPoint=getMousePoint(e).move(-4,-4);
    this.contextMenuFieldid=buttonId;
    this.callBack('menubutton',{'buttonId':buttonId,'cursorPos':cursorPos});
}

Form.prototype.onTitleClicked=function(e,fid){
    this.callBack('onTitleClicked',{fieldId:fid,x:MOUSE_POSITION_X,y:MOUSE_POSITION_Y});
}
Form.prototype.focusField=function(fieldId){
	this.fields[fieldId].focusField();
	getWindow(this.portlet.divElement).kmm.setActivePortletId(this.getId(),false);
}

Form.prototype.onUserContextMenuItem=function(e,isButton,fid,id){
    if(isButton)
        this.callBack('menubuttonitem',{'buttonId':fid,'action':id});
    else
        this.callBack('menuitem',{'fieldId':fid,'action':id});
}

Form.prototype.onCustom=function(fieldId, action, values){
	values.fieldId=fieldId;
	values.action = action;
	this.callBack('oncustom', values);
}

Form.prototype.onFieldCallBack=function(fieldId, action, values){
	values.fieldId=fieldId;
	values.action = action;
	this.callBack('onFieldCB', values);
}
Form.prototype.onFieldExtensionCallBack=function(fieldId, extId, action, values){
	values.fieldId=fieldId;
	values.extId = extId;
	values.action = action;
	this.callBack('onExtCB', values);
}


function FormDOMManager(_formPortlet, divElement){
    this._formPortlet = _formPortlet
    this.divElement = divElement;
    this.divElement.tabIndex=-1;

    this.formContainer = nw('div', 'portal_form_container');
    this.form=nw('div','portal_form');
    this.buttonsContainer=nw('div');
    this.buttons=nw('div','portal_form_buttons');
    this.scrollContainer=nw('div');
    this.hiddenDivElement = nw('div', 'portal_form_hidden');
    
    //  this.divElement.appendChild(this.formContainer);
  this.formContainer.appendChild(this.form);
  this.divElement.appendChild(this.buttons);
  this.formContainer.appendChild(this.hiddenDivElement);
  this.buttons.appendChild(this.buttonsContainer);
  this.buttonsContainer.style.left='0px';
  this.buttonsContainer.style.right='0px';
  this.divElement.appendChild(this.scrollContainer);
  this.divElement.style.left='0px';
  this.divElement.style.top='0px';
  this.divElement.style.bottom='0px';
  this.divElement.style.right='0px';
  this.scrollContainer.style.left='0px';
  this.scrollContainer.style.top='0px';
  this.scrollContainer.style.bottom='0px';
  this.scrollContainer.style.right='0px';

  this.scrollPane = new ScrollPane(this.scrollContainer, 15, this.formContainer);
  this.scrollPane.DOM.paneElement.style.overflow="visible";
  var that = this;
  this.scrollPane.onScroll=function(){that._formPortlet.onScroll()};
//MOBILE SUPPORT - scroll for formpanel
  this.currPoint = new Point(0,0);
  this.formContainer.ontouchmove=function(e) { if(e.target != that.hiddenDivElement) return; that.onTouchDragMove(e);};
}
FormDOMManager.prototype._formPortlet;
FormDOMManager.prototype.divElement;
FormDOMManager.prototype.formContainer;
FormDOMManager.prototype.form;
FormDOMManager.prototype.buttonsContainer;
FormDOMManager.prototype.buttons;
FormDOMManager.prototype.scrollContainer;
FormDOMManager.prototype.hiddenDivElement;
FormDOMManager.prototype.scrollPane;




FormDOMManager.prototype.setCssStyle=function(cssStyle){
	this.scrollPane.DOM.paneElement.style.background = "none";
	if(this.htmlLayout==null){
	  applyStyle(this.formContainer,cssStyle);
	  applyStyle(this.hiddenDivElement,cssStyle);
	  applyStyle(this.buttons,cssStyle);
	}else{
	  applyStyle(this.formContainer,cssStyle);
	  applyStyle(this.hiddenDivElement,cssStyle);
	}
}
//MOBILE SUPPORT - scroll for formpanel
FormDOMManager.prototype.onTouchDragMove=function(e){
	var diffx = this.currPoint.x - getMousePoint(e).x;
	var diffy = this.currPoint.y - getMousePoint(e).y;
	this.scrollPane.hscroll.goPage(0.01 * min(max(diffx, -3), 3));
	this.scrollPane.vscroll.goPage(0.01 * min(max(diffy, -3), 3));
	this.currPoint = getMousePoint(e);
}
FormDOMManager.prototype.reset=function(){
    removeAllChildren(this.form);
    this.resetButtons();
}
FormDOMManager.prototype.resetButtons=function(){
	removeAllChildren(this.buttonsContainer);
	this.buttons.style.height="0px";
}

FormDOMManager.prototype.addButton=function(id,name,cssStyle){
    var btn=nw('button');
    btn.buttonId=id;
    btn.innerHTML=name;
    var that=this;
    btn.onclick=function(e){that._formPortlet.onButton(getMouseTarget(e).buttonId);};
    btn.oncontextmenu=function(e){that.formPortlet.onUserButtonContextMenu(e,id,getMouseTarget(e).buttonId);};
    btn.style.height=toPx(this._formPortlet.buttonHeight);
    btn.style.marginLeft=toPx(this._formPortlet.buttonSpacing);
    btn.style.marginRight=toPx(this._formPortlet.buttonSpacing);
    btn.cssStyle=cssStyle;
    if(!this._formPortlet.hasButtons){
      this._formPortlet.hasButtons=true;
      this._formPortlet.updateLayout();
    }
    
    if(this._formPortlet.htmlLayout!=null){
        var element=getDocument(this.divElement).getElementById("formbutton_"+this._formPortlet.getId()+"_"+id);
        if(element!=null)
            element.appendChild(btn);
    }else{
      this.buttonsContainer.appendChild(btn);
    }
    applyStyle(btn,this._formPortlet.buttonsStyle);
    applyStyle(btn,cssStyle);
    return btn;
}

FormDOMManager.prototype.setScrollOptions=function(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows, cornerColor){
	this.scrollPane.hscroll.DOM.hideArrows(hideArrows);
	this.scrollPane.vscroll.DOM.hideArrows(hideArrows);
	this.scrollPane.setSize(width * 1);
	this.scrollPane.hscroll.DOM.applyColors(gripColor, trackColor, trackButtonColor, borderColor,cornerColor);
	this.scrollPane.vscroll.DOM.applyColors(gripColor, trackColor, trackButtonColor, borderColor,cornerColor);
	if (borderRadius!=null) {
		this.scrollPane.hscroll.DOM.applyBorderRadius(borderRadius);
		this.scrollPane.vscroll.DOM.applyBorderRadius(borderRadius);
	}

	this.scrollPane.updateIconsColor(iconsColor);
}

FormDOMManager.prototype.removeField=function(field){
  var container=field.container;
  container.removeChild(field.getElement());
  this.form.removeChild(container);
  this.form.removeChild(field.getLabelElement());
}
FormDOMManager.prototype.addField=function(field, cssStyle, isFieldHidden){
    var container;
    if(isFieldHidden == true){
		container=getDocument(this.divElement).getElementById("formfield_"+this._formPortlet.getId()+"_"+field.getId());
  		if(container != null)
  			container.style.position="static";
    }
    else {
    	if(this._formPortlet.htmlLayout!=null){
    		container=getDocument(this.divElement).getElementById("formfield_"+this._formPortlet.getId()+"_"+field.getId());
    	}else{
    		//If the field isn't displayed in the hiddenHtmlLayout add it to the form
    		if(container == null){
    			container=nw('div');
				this.form.appendChild(container);
    			this.form.appendChild(field.getLabelElement());
    		}
    	}
    }
    if(container!=null){
		container.appendChild(field.getElement());
      field.container=container;
    }
    field.setLabelStyle(this._formPortlet.labelsStyle);
    field.setFieldStyle(cssStyle);
}

FormDOMManager.prototype.repaint=function(){
	//Contained Size
	this.scrollPane.setLocation(0, 0, this._formPortlet.width, max(0,this._formPortlet.height-this.buttons.clientHeight));
	var size = this._formPortlet.getContainerSize();
	var w = size.w;
	var h = size.h;
	var mw=Math.max(w,this._formPortlet.width);
	var mh=Math.max(h,this._formPortlet.height);
	this.formContainer.style.height=mh;
	this.formContainer.style.width=mw;
	if(w != 0)
		this.hiddenDivElement.style.width=toPx(mw);
	if(h != 0)
		this.hiddenDivElement.style.height=toPx(mh);
	if(w != 0 && h != 0)
		rotateDivElement(this.formContainer,this._formPortlet.rotate,mw,mh,0,0);
	this.scrollContainer.style.overflow='clip';
	  
	this.scrollPane.setPaneSize(w,h);
}

FormDOMManager.prototype.setSize=function(width,height){
	this.scrollPane.setLocation(0, 0, width, height-this.buttons.clientHeight);
}

FormDOMManager.prototype.updateLayout=function(buttonsHeight){
    //	this.form.style.bottom=buttonsHeight;
	this.buttons.style.height=buttonsHeight;
	applyStyle(this.buttons,this._formPortlet.buttonPanelStyle);
}

FormDOMManager.prototype.setButtonStyle=function(buttonPaddingT,buttonPaddingB){
    this.buttonsContainer.style.top=toPx(buttonPaddingT);
    this.buttonsContainer.style.bottom=toPx(buttonPaddingB);
}

FormDOMManager.prototype.setHtmlLayout=function(hasHtmlLayout, hasHiddenHtmlLayout, htmlLayout){
	if(hasHtmlLayout){ //reset
	  removeAllChildren(this.formContainer);
      this.formContainer.appendChild(this.form);
      this.form.appendChild(this.buttons);
	}
	if(htmlLayout!=null){
	  this.hiddenDivElement.innerHTML=htmlLayout;
	  var childs=this.hiddenDivElement.children;
	  for(var i in childs){
		  var child=childs[i];
		  if(child.nodeName=="SCRIPT"){
			  eval(child.text);
	      }
	  }
	}else if(hasHiddenHtmlLayout != null){
		removeAllChildren(this.hiddenDivElement);
	}
}


////////////////////////////////////////////////////////////////////////////////
//  FormField
////////////////////////////////////////////////////////////////////////////////

function FormField(portlet,id,title){
  this.label=nw('div','portal_form_label');
  this.labelSpan = nw('span');
  this.portlet=portlet;
  this.id=id;
  this.title=title;
  this.label.appendChild(this.labelSpan);
  this.labelSpan.innerHTML=title;
  this.value = null;
  this.modificationNumber = -1;
  this.isFieldHidden = false;
  this.inputList = [];
}
FormField.prototype.width;
FormField.prototype.height;
FormField.prototype.container;
FormField.prototype.element;
FormField.prototype.modificationNumber;
FormField.prototype.value;
FormField.prototype.isFieldHidden;
FormField.prototype.extensions;
FormField.prototype.inputList;

FormField.prototype.initField=function(title,titleClickable,disabled,fieldHidden,labelHidden,hids,zindex){
	this.setTitle(title,titleClickable);
	this.setDisabled(disabled);
	this.setFieldHidden(fieldHidden);
	this.setLabelHidden(labelHidden);
	this.setHIDS(hids);
	this.setZIndex(zindex);
	
}

FormField.prototype.setFieldValue=function(value, modificationNumber){
        this.setModificationNumber(modificationNumber);
        this.setValue(value);
}
FormField.prototype.setFieldPosition=function(x,y,w,h,lx,ly,lw,lh,a,s,padding){
	if(this.htmlLayout==null)
	  this.setPosition(x,y,w,h);
	else
	  this.setPosition(null,null,w,h);
	this.setLabelPosition(lx,ly,lw,lh);
	this.setLabelAlignment(a, s, toPx(padding));
}

FormField.prototype.setFieldStyleOptions=function(labelColor, bold, italic, underline, fontFamily,labelFontSize){
	this.setStyleOptions(labelColor, bold, italic, underline, fontFamily);
	this.setLabelSize(labelFontSize);
}

FormField.prototype.addExtension=function(extension){
	if(this.extensions==null)
		this.extensions=[];
	this.extensions.push(extension);
}
FormField.prototype.addExtensionAt=function(extension, index){
	if(this.extensions==null)
		this.extensions=[];
	this.extensions[index] = extension;
}
FormField.prototype.hasExtensions=function(){
	return this.extensions != null && this.extensions.length > 0;
}
FormField.prototype.getExtension=function(i){
	return this.extensions[i];
}


FormField.prototype.setModificationNumber=function(modificationNumber){
	this.modificationNumber = modificationNumber;
}
FormField.prototype.getModificationNumber=function(){
	return this.modificationNumber;
}
FormField.prototype.setHidden=function(hidden){
	this.getElement().style.position=hidden?"static":"absolute";
}  		
FormField.prototype.setValue=function(value){
	this.value = value;
}
FormField.prototype.getValue=function(){
	return this.value;
}
FormField.prototype.getHeight=function(){
  return this.height;
};
FormField.prototype.getWidth=function(){
  return this.width;
};
FormField.prototype.focusField=function(inputInd){
	if(this.inputList.length != 0 && inputInd != null){
		this.inputList[inputInd].focus();
		return true;
	}
	else if(this.input){
		this.input.focus();
		return true;
	}
	return false;
};
FormField.prototype.setPosition=function(x,y,width,height){
  this.width=width;
  this.height=height;
  this.x=x;
  this.y=y;
  if(this.container==null)
	  return;
  if(x!=null && y!=null){
    this.container.style.left=toPx(x);
    this.container.style.top=toPx(y);
    this.container.style.textAlign='left';
    this.container.style.width=toPx(width);
    this.container.style.height=toPx(height);
  }else{
    this.container.style.position='relative';
    this.container.style.display='inline-block';
    this.container.style.textAlign='left';
    //TODO: must be able to set
//    this.container.style.width=toPx(width);
//    this.container.style.height=toPx(height);
  }
  if(this.hasExtensions()){
	  for(var i = 0; i < this.extensions.length; i++){
		  this.extensions[i].onFieldReposition();
	  }
  }
  this.onFieldSizeChanged();
};

FormField.prototype.setLabelHidden=function(hidden){
	this.label.style.display = hidden==true?"none":null;
}
FormField.prototype.setLabelPosition=function(x,y,width,height){
  this.labelX=x;
  this.labelY=y;
  this.labelWidth=width;
  this.labelHeight=height;
  this.label.style.left=toPx(x);
  this.label.style.top=toPx(y);
  this.label.style.width=toPx(width);
  this.label.style.height=toPx(height);
  if (!parseInt(this.label.style.zIndex)) // if this hasn't been set already
	  this.label.style.zIndex = -1;
};

FormField.prototype.setLabelAlignment=function(alignment, side, padding){ 
	switch(side){
		case 0: // top
			switch (alignment) {
				case 0: // top-left
					this.label.style.alignItems = "flex-end";
					this.label.style.justifyContent = "flex-start";
					this.label.style.textAlign = "start";
					this.label.style.paddingBottom = padding;
					break;
				case 1: // top-center
					this.label.style.alignItems = "flex-end";
					this.label.style.justifyContent = "center";
					this.label.style.textAlign = "center";
					this.label.style.paddingBottom = padding;
					break;
				case 2: // top-right
					this.label.style.alignItems = "flex-end";
					this.label.style.justifyContent = "flex-end";
					this.label.style.textAlign = "end";
					this.label.style.paddingBottom = padding;
					break;
				default:
					this.label.style.alignItems = "center";
					this.label.style.justifyContent = "flex-end";
					this.label.style.textAlign = "end";
					this.label.style.paddingRight = padding;
					break;
			}
			break;
		case 3: // right
			switch (alignment) {
				case 0: // right-top
					this.label.style.alignItems = "flex-start";
					this.label.style.justifyContent = "flex-start";
					this.label.style.textAlign = "start";
					this.label.style.paddingLeft = padding;
					break;
				case 1: // right-center
					this.label.style.alignItems = "center";
					this.label.style.justifyContent = "flex-start";
					this.label.style.textAlign = "start";
					this.label.style.paddingLeft = padding;
					break;
				case 2: // right-bottom
					this.label.style.alignItems = "flex-end";
					this.label.style.justifyContent = "flex-start";
					this.label.style.textAlign = "start";
					this.label.style.paddingLeft = padding;
					break;
				default:
					this.label.style.alignItems = "center";
					this.label.style.justifyContent = "flex-end";
					this.label.style.textAlign = "end";
					this.label.style.paddingRight = padding;
					break;
			}
			break;
		case 1: // bottom
			switch (alignment) {
				case 2: // bottom-right
					this.label.style.alignItems = "flex-start";
					this.label.style.justifyContent = "flex-end";
					this.label.style.textAlign = "end";
					this.label.style.paddingTop = padding;
					break;
				case 1: // bottom-center
					this.label.style.alignItems = "flex-start";
					this.label.style.justifyContent = "center";
					this.label.style.textAlign = "center";
					this.label.style.paddingTop = padding;
					break;
				case 0: // bottom-left
					this.label.style.alignItems = "flex-start";
					this.label.style.justifyContent = "flex-start";
					this.label.style.textAlign = "start";
					this.label.style.paddingTop = padding;
					break;
				default:
					this.label.style.alignItems = "center";
					this.label.style.justifyContent = "flex-end";
					this.label.style.textAlign = "end";
					this.label.style.paddingRight = padding;
					break;
			}
			break;
		case 2: // left
			switch (alignment) {
				case 2: // left-bottom
					this.label.style.alignItems = "flex-end";
					this.label.style.justifyContent = "flex-end";
					this.label.style.textAlign = "end";
					this.label.style.paddingRight = padding;
					break;
				case 1: // left-center
					this.label.style.alignItems = "center";
					this.label.style.justifyContent = "flex-end";
					this.label.style.textAlign = "end";
					this.label.style.paddingRight = padding;
					break;
				case 0: // left-top
					this.label.style.alignItems = "flex-start";
					this.label.style.justifyContent = "flex-end";
					this.label.style.textAlign = "end";
					this.label.style.paddingRight = padding;
					break;
				default:
					this.label.style.alignItems = "center";
					this.label.style.justifyContent = "flex-end";
					this.label.style.textAlign = "end";
					this.label.style.paddingRight = padding;
					break;
			}
			break;
		default:
			this.label.style.alignItems = "center";
			this.label.style.justifyContent = "flex-end";
			this.label.style.textAlign = "end";
			this.label.style.paddingRight = padding;
			break;
	}
}

FormField.prototype.getTitle=function(){
  return this.title;
};
FormField.prototype.setTitle=function(title,titleIsClickable){
  this.title=title;
  this.labelSpan.innerHTML=title;

  this.titleIsClickable=titleIsClickable;
  this.setTitleClickable(this.titleIsClickable);
  var that=this;
  this.labelSpan.onclick=function(e){that.onTitleClicked(e)};
};


FormField.prototype.setTitleClickable=function(clickable){
  if(clickable){
    addClassName(this.labelSpan,"portal_form_label_help",false);
  }else
    removeClassName(this.labelSpan,"portal_form_label_help");
}

FormField.prototype.onTitleClicked=function(e){
	if(this.titleIsClickable)
         this.portlet.onTitleClicked(e,this.id);
}
FormField.prototype.setFieldHidden=function(hidden){
	this.isFieldHidden = hidden == true;
	if(this.container != null)
		this.container.style.display = hidden == true?"none":null; 
	else
		this.element.style.display = hidden == true?"none":null; 
}
FormField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.style.pointerEvents= this.disabled?"none":null;
	if(this.input) 
		this.input.disabled=this.disabled;
}

FormField.prototype.setZIndex=function(index){
	if(this.container != null)
		this.container.style.zIndex=index;
}

FormField.prototype.getId=function(){
  return this.id;
};
FormField.prototype.getElement=function(){
  return this.element;
};
FormField.prototype.getLabelElement=function(){
  return this.label;
};
FormField.prototype.setFieldStyle=function(style){
	applyStyle(this.getElement(),style);
};

FormField.prototype.setLabelStyle=function(style){
	applyStyle(this.getLabelElement(),style);
};
FormField.prototype.setContainer=function(container){
	this.container=container;
};
FormField.prototype.getContainer=function(){
	return this.container;
};

FormField.prototype.handleTab=function(ind, e){
	if(e.keyCode==9) 
		return this.form.onTab(this,e);
}
FormField.prototype.callBack=function(action, values){
	this.portlet.onFieldCallBack(this.id, action, values);
}

FormField.prototype.onExtensionCallback=function(extIndex, action, values){
	this.portlet.onFieldExtensionCallBack(this.id, extIndex, action, values);
}
FormField.prototype.onFieldEvent=function(e, eventType){
	this.portlet.callBack("onFieldEvent", {feType:eventType, fieldId:this.id});
};
FormField.prototype.setStyleOptions=function(labelColor, bold, italic, underline, fontFamily){
	this.label.style.color=labelColor;
	if (bold==true) {
		this.label.style.fontWeight="bold";
	} else if(bold==false) {
		this.label.style.fontWeight="normal";
	}
	if (italic==true) {
		this.label.style.fontStyle="italic";
	} else if(italic==false) {
		this.label.style.fontStyle="normal";
	}
	if (underline==true) {
		this.label.style.textDecoration="underline";
	} else if(underline==false){
		this.label.style.textDecoration="none";
	}
	this.label.style.fontFamily=fontFamily;
}
FormField.prototype.setLabelSize=function(labelFontSize){
	this.label.style.fontSize=toPx(labelFontSize);
}


FormField.prototype.setHelpBox=function(text, textWidth, style){
  var outerStyle="_cna="+this.form.portlet.instance.hcsc;
  this.helpBox=new HelpBox(getWindow(this.labelSpan), true, style,outerStyle);
  this.initHelpBox(true);
  this.helpBox.init(text, textWidth);
  this.helpBox.autoHide(false, this.labelSpan);
}


FormField.prototype.setHIDS=function(hids){
   this.element.id=hids;
}

FormField.prototype.getHelpBox=function(e){
  var that = this;
  this.portlet.onCustom(that.id, "getHelpBox", {});
}


FormField.prototype.initHelpBox=function(hasHelpBox){
  var that=this;
  if(this.element.parentNode === null)
	  return;
  if(hasHelpBox == true){
	  this.labelSpan.onclick=function(e){
		  if(that.helpBox!=null)
			  that.helpBox.showIfMouseInside(e, that.labelSpan);
		  else {
			  that.getHelpBox(e);
		  };
	  };

//	  this.labelSpan.onmouseleave=function(e){if(that.helpBox!=null) that.helpBox.hide(e, that.label); };
  }
  else{
	  this.labelSpan.onmouseenter=null;
	  this.labelSpan.onmousemove=null;
  }
};


////////////////////////////////////////////////////////////////////////////////
// ButtonField
////////////////////////////////////////////////////////////////////////////////
ButtonField.prototype=new FormField();
ButtonField.prototype.disableWhenClicked=false;
ButtonField.prototype.disabledDueToClick=false;

function ButtonField(portlet,id,title){
	
	FormField.call(this,portlet,id,title);
    this.element=nw('button','form_field_button');
    this.element.style.cursor='pointer';
    this.element.style.left='0px';
	var that=this;
	this.input = this.element;
    this.element.onclick=function(e){
    	if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id);
	// MOBILE SUPPORT - added ontouchend for mobile support
    	if(getMouseButton(e)==1 || this.element.ontouchend) {
    	    if(!that.disabledDueToClick){
    	      if(that.disableWhenClicked){
    	          that.disabledDueToClick=true;
	          that.element.style.cursor='not-allowed';
    	        }
              that.portlet.callBack('menuitem',{'fieldId':that.id,'action':'button_clicked'});
            }
    	} 
    };
    this.element.onchange=function(){that.portlet.onChange(that.id,{value:that.getValue()});};
    this.element.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
    this.element.onblur=function(e){that.onFieldEvent(e,"onBlur");};
//    this.element.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e);}
    this.element.onkeydown=function(e){that.handleTab(null,e);}
}

ButtonField.prototype.shouldDisableAfterFirstClick=function(shouldDisable,disabledDueToClick){
	this.disableWhenClicked = shouldDisable;
	this.disabledDueToClick=disabledDueToClick;
	if(this.disabledDueToClick){
	  this.element.style.cursor='not-allowed';
	}else
	  this.element.style.cursor='pointer';
}
ButtonField.prototype.setFieldStyle=function(style){
	this.input.className="form_field_button";
	applyStyle(this.input,style);
}

ButtonField.prototype.setValue=function(value){
  this.element.innerHTML=value;
}

ButtonField.prototype.getValue=function(){
  return this.element.value;
}

ButtonField.prototype.onFieldSizeChanged=function(){
  this.element.style.height=toPx(this.height);
  this.element.style.width=toPx(this.width);
}

////////////////////////////////////////////////////////////////////////////////
// PasswordField
////////////////////////////////////////////////////////////////////////////////

PasswordField.prototype=new FormField();


function PasswordField(portlet,id,title){
	FormField.call(this,portlet,id,title);
    this.input=nw('input', 'passwordField');
    this.input.type="password";
    this.input.setAttribute("autocomplete", "false");
    this.element=nw('div', 'password-field-container');
    this.iconDiv = nw('div', 'password-icon-div');
    this.element.style.display="inline-block";
    this.element.appendChild(this.input);
    this.element.appendChild(this.iconDiv);
	var that=this;
    this.input.oninput=function(e){that.onInput(e);};
    this.iconDiv.onmousedown=function(e){that.onMousedown(e);};
    this.iconDiv.onmouseup=function(e){that.onMouseup(e);};
    this.iconDiv.onmouseout=function(e){that.onMouseout(e);};
    //MOBILE SUPPORT - touch support for passwordfield
    this.iconDiv.ontouchstart=function(e){that.onMousedown(e);};
    this.iconDiv.ontouchend=function(e){that.onMouseup(e);};
    this.input.onkeydown=function(e){that.onKeyDown(e);};
    this.input.onfocus=function(e){that.onFocus(e);};
    this.input.onblur=function(e){that.onBlur(e);};
	this._flagNewPass=true;
	this._oldPassword="";
}
PasswordField.prototype._flagNewPass;
PasswordField.prototype._oldPassword;
PasswordField.prototype.onFieldSizeChanged=function(){
  var h=toPx(this.height);
  this.input.style.width=toPx(this.width);
  this.iconDiv.style.width=toPx(this.width * .10);
  
  this.input.style.height=h;
  this.iconDiv.style.height=h;
  
  this.input.style.paddingLeft = toPx(this.iconDiv.clientWidth + 5);
}
PasswordField.prototype.init=function(bgCl, fontCl, fs, bdrCl, fontFam, borderRadius, borderWidth) {
	if(bgCl!=null){
		var colors = parseColor(bgCl);
		this.fieldBgClHex = toColor(colors[0], colors[1], colors[2]);
		// click field -> mousedown -> bg image set -> focus -> init -> we have bg image already
		if (!this.iconDiv.style.backgroundImage) {
			if (this.shouldUseWhiteIcon(bgCl))
				this.iconDiv.style.backgroundImage = "url('rsc/pass-show-white.svg')";
			else
				this.iconDiv.style.backgroundImage = "url('rsc/pass-show-black.svg')";
		}
	}
	
	this.input.style.color=fontCl;
	this.input.style.background=bgCl;
	this.input.style.fontSize=toPx(fs);
	this.input.style.border="1px solid " + bdrCl;
	this.input.style.fontFamily = fontFam;
	this.input.style.borderRadius = toPx(borderRadius);
	this.input.style.borderWidth = toPx(borderWidth);
}
PasswordField.prototype.shouldUseWhiteIcon=function(hexColor) { // bgcl should be in hex format
	var colorsRGB = parseColor(hexColor);
	if (colorsRGB.length === 3) {
		return (colorsRGB[0] + colorsRGB[1] + colorsRGB[2]) / 3 < 120;
	}
	return false;
}
PasswordField.prototype.setFieldStyle=function(style){
	applyStyle(this.input,style);
};
PasswordField.prototype.getValue=function() {
	return this.input.value;
}
PasswordField.prototype.setValue=function(val) {
	// The server will only send a masked text `*******` so directly setting it is fine
	this._flagNewPass = false;
	this._oldPassword = "";
	var pass = this.input.value;
	if(pass == null || "" == pass.trim() )
	    this.input.value = val;
}


PasswordField.prototype.onInput=function(e){
    this.handleTab(null,e);
	this._flagNewPass = false;
	this._oldPassword = "";
    var obfuscated = this.getObfuscated(this.input.value);
	this.portlet.onChange(this.id, {value: obfuscated});
}
PasswordField.prototype.getObfuscated=function(val) {
	try {
		var v = window.btoa(val);
		return v;
	} catch(e) {
		return val;
	}
}
PasswordField.prototype.getUnobfuscated=function(val) {
	try {
		var v = window.atob(val);
		return v;
	} catch (e) {
		return val;
	}
}
PasswordField.prototype.onMousedown=function(e){
	this.input.type="text";
	// maintain focus when clicking on eye-con
	this.input.focus();
	if (this.shouldUseWhiteIcon(this.fieldBgClHex))
		this.iconDiv.style.backgroundImage = "url('rsc/pass-hide-white.svg')";
	else
		this.iconDiv.style.backgroundImage = "url('rsc/pass-hide-black.svg')";
	// stop it from firing blur, we want to keep the field focused because eye-con is part of it
	e.preventDefault();
}
PasswordField.prototype.onMouseup=function(e){
	this.input.type="password";
	if (this.shouldUseWhiteIcon(this.fieldBgClHex))
		this.iconDiv.style.backgroundImage = "url('rsc/pass-show-white.svg')";
	else
		this.iconDiv.style.backgroundImage = "url('rsc/pass-show-black.svg')";
}
PasswordField.prototype.onMouseout=function(e){
	this.input.type="password";
	if (this.shouldUseWhiteIcon(this.fieldBgClHex))
		this.iconDiv.style.backgroundImage = "url('rsc/pass-show-white.svg')";
	else
		this.iconDiv.style.backgroundImage = "url('rsc/pass-show-black.svg')";
}

PasswordField.prototype.onFocus=function(e){
	this._flagNewPass = true;
	this._oldPassword = this.input.value;
	this.input.value = "";
	this.onFieldEvent(e, "onFocus");
}
PasswordField.prototype.onBlur=function(e){
	if(this._flagNewPass == true){
	this._flagNewPass = false;
	this.input.value = this._oldPassword;
	this._oldPassword = "";
	}
	this.onFieldEvent(e, "onBlur");
}

PasswordField.prototype.onKeyDown=function(e){
	if(e.keyCode==9)
		return this.form.onTab(this,e);
	
	this._flagNewPass = false;
	this._oldPassword = "";
	var that = this;
	if(e.keyCode==13 || (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){
		var pos=getCursorPosition(this.element);
		this.portlet.onCustom(this.id, "updateCursor", {pos:pos});
	    this.portlet.onKey(this.id,e,{pos:pos});
	    if(e.keyCode==9)
	      e.preventDefault();
    }
}

////////////////////////////////////////////////////////////////////////////////
// TextField
////////////////////////////////////////////////////////////////////////////////
TextField.prototype=new FormField();

function TextField(portlet,id,title){
	hasButton=true;
	FormField.call(this,portlet,id,title);
    this.input=nw('input');
    this.input.setAttribute("autocomplete", "nope");
    this.element=nw('div');
    this.element.style.display="inline-block";
    this.element.appendChild(this.input);
	var that=this;
	this.oldValue = "";
    this.input.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.input));};
    this.input.oninput=function(){that.onChangeDiff();};
    this.input.onkeydown=function(e){that.onKeyDown(e);};
    this.input.onfocus=function(e){that.onFieldEvent(e, "onFocus");};
    this.input.onblur=function(e){that.onFieldEvent(e, "onBlur");};
}
TextField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	if(this.input)
		this.input.disabled=this.disabled;
}
TextField.prototype.setHidden=function(hidden){
	if(hidden){
		this.element.style.display="inline-block";
		this.element.style.width="auto";
		this.element.style.height="auto";
	}
	else{
		this.element.style.display="block";
		this.element.style.width="100%";
		this.element.style.height="100%";
	}
	this.element.style.position="static";
	this.input.style.position=hidden?"static":"absolute";
}
TextField.prototype.onChangeDiff=function(){
	var newValue = this.getValue();
	var change = strDiff(this.oldValue, newValue);
	this.oldValue = newValue;
	this.portlet.onChange(this.id, {c:change.c,s:change.s,e:change.e,mid:this.getModificationNumber()});
}
TextField.prototype.onChangeDiffSelect=function(){
	var newValue = this.getValue();
	var change = strDiff(this.oldValue, newValue);
	this.oldValue = newValue;
	var isSetValueEvent = true;
	this.portlet.onChange(this.id, {c:change.c,s:change.s,e:change.e,mid:this.getModificationNumber(), sv:isSetValueEvent});
}

TextField.prototype.setFieldStyle=function(style){
	this.input.className="";
	applyStyle(this.input,style);
};

TextField.prototype.onKeyDown=function(e){
    this.handleTab(null,e);
	if(e.keyCode==13 ||  (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){
		var pos=getCursorPosition(this.element);
		this.portlet.onCustom(this.id, "updateCursor", {pos:pos});
	    this.portlet.onKey(this.id,e,{pos:pos});
	    if(e.keyCode==9)//TAB
	      e.preventDefault();
	}
}
TextField.prototype.changeSelection=function(start,end){
	setSelection(this.input,start,end);
}

TextField.prototype.init=function(maxLength,isPassword,hasButton,callbackKeys){
	var that=this;
    this.callbackKeys=callbackKeys;
	if(hasButton && this.button==null){
      this.button=nw('div','textfield_plusbutton');
      this.button.onmousedown=function(e){that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.input));};
      this.button.style.top='0px';
      this.element.appendChild(this.button);
    }else if(!hasButton && this.button!=null){
      this.element.removeChild(this.button);
      this.button=null;
    }
  this.input.maxLength=maxLength;
  if(isPassword)
	  this.input.type='password';
  else
	  this.input.type='';
};


TextField.prototype.onFieldSizeChanged=function(){
  var h=toPx(this.height);
  if(this.button){
	 var t=toPx(this.width-this.height);
    this.input.style.width=t;
    this.button.style.left=t;
    this.button.style.width=h;
    this.button.style.height=h;
  }else{
    this.input.style.width=toPx(this.width);
  }
  this.input.style.height=h;
}

TextField.prototype.setValue=function(value){
  //Does not fire oninput or onchange, so we need to reset the oldValue to the currentValue 
  if (value == null){
	  this.input.value="";
	  //this.oldValue = "";
	  this.oldValue = null;
  }else{
	  this.input.value=value;
	  //this.oldValue = value;
	  this.oldValue = null;
  }
};
TextField.prototype.setValueAndFire=function(value){
	this.input.value = value;
	this.onChangeDiff();
}
TextField.prototype.setValueAndFireSelect=function(value){
	this.input.value = value;
	this.onChangeDiffSelect();
}

TextField.prototype.getValue=function(){
  return this.input.value;
};

TextField.prototype.moveCursor=function(cursorPos){
	this.input.focus();
	setCursorPosition(this.input,cursorPos);
};

////////////////////////////////////////////////////////////////////////////////
// TitleField
////////////////////////////////////////////////////////////////////////////////
TitleField.prototype=new FormField();

function TitleField(portlet,id,title){
	FormField.call(this,portlet,id,title);
    this.element=nw('div','portal_form_title');
	var that=this;
    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};
    this.init();
}

TitleField.prototype.init=function(){
  var that=this;
  if(this.element.parentNode === null)
	  return;
  this.element.parentNode.style.verticalAlign='bottom';
};
TitleField.prototype.onFieldSizeChanged=function(){
  var w=toPx(this.width),h=toPx(this.height);
  this.element.style.width=w;
  this.element.style.height=h;
  this.label.style.width=w;
  this.label.style.height=h;
}

TitleField.prototype.setValue=function(value){
  this.element.innerHTML=value;
};

TitleField.prototype.getValue=function(){
  return this.element.innerHTML;
};
TitleField.prototype.setFieldStyle=function(style){
  this.element.className="portal_form_title";
  applyStyle(this.element,style);
}


////////////////////////////////////////////////////////////////////////////////
// ColorField
////////////////////////////////////////////////////////////////////////////////
ColorField.prototype=new FormField();

function ColorField(portlet,id,title){
	
	FormField.call(this,portlet,id,title);
    this.element=nw('div');
    this.element.style.cursor='pointer';
    this.element.style.position='absolute';
    this.element.style.left='0px';
    this.element.style.background='white url(rsc/checkers.png) repeat-y right';
    this.input = nw('input');
    this.element.appendChild(this.input);
	var that=this;
    this.element.onclick=function(){that.element.blur();that.showChooser();};
    this.input.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
    this.input.onblur=function(e){that.onFieldEvent(e,"onBlur");};
    this.input.onkeydown=function(e){
    	if(e.keyCode==13 || e.keyCode==32)//enter of spacebar
    	  return that.showChooser();
      that.handleTab(null,e);
    };
    this.noColorText = 'no color';
}
ColorField.prototype.setFieldStyle=function(style){	
  this.input.className="";
  applyStyle(this.input,style);
}
ColorField.prototype.init=function(allowNull,alpha,hasButton,displayText) {
  this.allowNull=allowNull;
  this.alpha=alpha;
  this.displayText=displayText;
  if(hasButton){
    if(this.button==null){
	  var that=this;
      this.button=nw('div','textfield_plusbutton');
      this.button.style.position='relative';
      this.button.onmousedown=function(e){that.portlet.onUserContextMenu(e,that.id, -1);};
      if(this.element.parentElement!=null)
        this.element.parentElement.appendChild(this.button);
	  }
  }else{
	 if(this.button!=null){
       if(this.element.parentElement!=null)
         this.element.parentElement.removeChild(this.button);
	   this.button=null;
	 }
  }
};

ColorField.prototype.setDisabled=function(disabled){
	this.input.disabled=disabled?'disabled':'';
}

var currentColorPicker = null;

ColorField.prototype.showChooser=function(){
	this.colorPicker=new ColorPicker(true,this.getValue(),getWindow(this.element),this.allowNull);
	var rect=new Rect();
	rect.readFromElement(this.element);
	var that=this;
	this.colorPicker.onOk=function(){that.onColorPickerOk(); currentColorPicker = null;};
	this.colorPicker.onCancel=function(){that.onColorPickerCancel(); currentColorPicker=null; };
	this.colorPicker.onColorChanged=function(){that.onColorChanged()};
	this.colorPicker.onNoColor=function(){that.onColorPickerNoColor(); currentColorPicker=null; };
	currentColorPicker = this.colorPicker;
	this.colorPicker.show(rect.getRight(),rect.top);
	if(this.alpha)
	  this.colorPicker.setAlphaEnabled();
    this.portlet.onClick(this.id,{});
}

ColorField.prototype.addCustomColor=function(colors){
	if(this.colorPicker!=null){
	    for(var i=0;i<colors.length;i++)
	      this.colorPicker.addColorChoice(colors[i]);
	}
}

ColorField.prototype.onColorPickerOk=function(){
	this.setValue(this.colorPicker.getColor());
    this.portlet.onChange(this.id,{value:this.getValue(),status:'ok'});
	this.colorPicker.hide();
	this.colorPicker=null;
	this.input.focus();
}
ColorField.prototype.onColorPickerNoColor=function(){
	this.setValue(null);
    this.portlet.onChange(this.id,{value:null,status:'nocolor'});
	this.colorPicker.hide();
	this.colorPicker=null;
	this.input.focus();
}
ColorField.prototype.onColorPickerCancel=function(){
	this.setValue(this.colorPicker.getOrigColor());
    this.portlet.onChange(this.id,{value:this.getValue(),status:'cancel'});
	this.colorPicker.hide();
	this.colorPicker=null;
	this.input.focus();
}
ColorField.prototype.onColorChanged=function(){
    if(this.value==this.colorPicker.getColor())
      return;
	this.setValue(this.colorPicker.getColor());
    this.portlet.onChange(this.id,{value:this.getValue(),status:'momentary'});
}

ColorField.prototype.onFieldSizeChanged=function(){
  var h=toPx(this.height);
  if(this.button){
	var t=toPx(this.width-this.height);
    this.input.style.width=t;
    this.button.style.left=t;
    this.button.style.width=h;
    this.button.style.height=h;
  }else{
    this.input.style.width=toPx(this.width);
  }
  this.input.style.height=toPx(this.height);
};
ColorField.prototype.setNoColorText=function(noColorText){
	this.noColorText = noColorText;
};
ColorField.prototype.setValue=function(value){
  this.value=value;
  if(value){
	var color ;
	if(value.length==9)
	  color = parseColorAlpha(value);
	else 
	  color = parseColor(value);
    this.input.style.background=value;
    if(this.width<40)
      this.input.style.color=value;
    else if(color[3]<128)
      this.input.style.color='#000000';
    else if((color[0]+color[1]+color[2])/3<120)
      this.input.style.color='#ffffff';
	else
      this.input.style.color='#000000';
      if(this.displayText!=null)
        this.input.value=this.displayText;
      else
        this.input.value=value;
  }else{
	  this.input.style.background='#FFFFFF';
	  this.input.style.color='#AAAAAA';
	  this.input.value=this.noColorText;
  }
  if (this.input.disabled){
	  this.input.style.color='#555555';
	  this.input.style.borderColor='#C0C0C0';
      this.input.style.cursor='not-allowed';
  } else {
      this.input.style.cursor='pointer';
  }
};
ColorField.prototype.getValue=function(){
  return this.value;
};

////////////////////////////////////////////////////////////////////////////////
// DayChooserField
////////////////////////////////////////////////////////////////////////////////
DayChooserField.prototype=new FormField();

function DayChooserField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	var that = this;
    this.element=nw('div', 'dateformfield');
    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};
    this.element.style.display = "inline-block";
    this.element.onkeydown=function(e){that.onKeyDown(e);};

    this.ratio=0.5; // TODO make this a user setting in editor
//    this.setIsRange(false);
}

DayChooserField.prototype.onChangeCallback=function(){
	var that = this;
	var lYmd = this.chooser.getSelectedYmd();
	var rYmd = this.chooser.getSelectedYmd2(); // returns an object containing time broken down to hour,min,sec,ms
	// no change, no op
	if(lYmd == this.prevlYmd && rYmd == this.prevrYmd)
		return;
	this.prevlYmd=lYmd;
	this.prevrYmd=rYmd;
	that.portlet.onChange(that.id,{ymd:lYmd,ymd2:rYmd});
}

DayChooserField.prototype.setDateDisplayFormat=function(format){
	if (this.chooser) {
		this.chooser.setDateDisplayFormat(format);
	}
}
DayChooserField.prototype.setFieldStyle=function(style){
	if(this.chooser){
		this.chooser.input.className="cal_input";
		applyStyle(this.chooser.input,style);
		if(this.chooser.input2){
			this.chooser.input2.className="cal_input";
			applyStyle(this.chooser.input2,style);
		}
	}
};

DayChooserField.prototype.onKeyDown=function(e){
    if(e.keyCode==9){
    	if(this.chooser != null)
    		this.chooser.hideCalendar();
    }
    this.handleTab(null,e);
}

DayChooserField.prototype.setHidden=function(hidden){
	this.element.style.position=hidden?"static":"absolute";
//	this.chooser.input.style.position = hidden?"static":"absolute";
	
}

DayChooserField.prototype.initCalendar=function(isRange){
	var that=this;
	if(this.chooser!=null){
		if(this.chooser.isRange == isRange)
			return;
		this.chooser.hideCalendar();
	}
	this.chooser=new DateChooser(this.element,isRange);
    this.chooser.input.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
    this.chooser.input.onblur=function(e){that.onFieldEvent(e,"onBlur");};
	var changeFunc = function(){ that.onChangeCallback(); };
	var closeFunc = function() {
		that.chooser.input.focus();
	}
	var glassFunc = function(e) {
		// this is an onclick event
		if (that.chooser.rootDiv) {
			var isIn = isMouseInside(e, that.chooser.rootDiv,0);
			if (!isIn) {
	//			// clicked outside the glass or the field itself, hide calendar first
				that.chooser.hideCalendar();
				if (!isMouseInside(e, that.chooser.input, 0)) {
					// send blur to backend if user clicked anywhere outside of the field itself
					that.chooser.input.blur();
				}
			}
		} 
	}
	var glassFunc2 = function(e) {
		if (!isMouseInside(e, that.timeInput.input, 0)) {
			// send blur to backend if user clicked anywhere outside of the field itself
			that.timeInput.input.onblur();
		}
	}
	this.chooser.onChange=changeFunc;
	// need to tell backend this field is blurred
	this.chooser.handleCloseButton=closeFunc;
	this.chooser.onGlass=glassFunc;
	//Add inputs to inputList
	this.inputList = [];
	if(this.chooser.input){
		this.chooser.input.inputid = this.inputList.length;
		this.inputList.push(this.chooser.input);
        this.chooser.input.onfocus=function(e){ that.onFieldEvent(e,"onFocus"); };
        this.chooser.input.onblur=function(e){ 
        	// this event has the highest priority (before any of our custom hooks are fired)
        	// js will call blur when user clicks on anywhere that is not on the date portion of the field
        	// if user clicked on a day, onClickDay will fire after this
        	if (that.chooser.rootDiv) {
        		// maintain focus if calendar still open
        		that.chooser.input.focus();
        	} else {
        		// field is focused, but no calendar
				that.onFieldEvent(e,"onBlur"); 
        	}
		};
	    	
	}
	if(isRange && this.chooser.input2){
		this.chooser.input2.inputid = this.inputList.length;
		this.inputList.push(this.chooser.input2);
        this.chooser.input2.onfocus=function(e){ that.onFieldEvent(e,"onFocus"); };
        this.chooser.input2.onblur=function(e){ 
        	if (that.chooser.rootDiv) {
        		that.chooser.input.focus();
        	} else {
				that.onFieldEvent(e,"onBlur"); 
        	}
		};
		// ensure field is focused when clicking on dash
		this.chooser.onDashClicked=function(e){that.chooser.input.focus();};
	}
}

DayChooserField.prototype.onFieldSizeChanged=function(){
	if (this.chooser.isRange) {
		const cStyle=getComputedStyle(this.chooser.input);
		const tStyle=getComputedStyle(this.chooser.input2);
		const available=1.0*this.width-pxToInt(cStyle.paddingRight)-pxToInt(tStyle.paddingLeft)- this.chooser.dash.offsetWidth;
		
		this.chooser.input.style.width = toPx(roundDecimals(available*this.ratio,0));
		this.chooser.input2.style.width = toPx(roundDecimals(available*(1-this.ratio),0));
	} 
	this.element.style.width=toPx(this.width);
	this.element.style.height=toPx(this.height);

	this.chooser.setContainerSize(this.width,this.height);
};

DayChooserField.prototype.setValue=function(value){
  var startAndEnd=value.split(',');
  if(startAndEnd[0]!='null')
    this.chooser.setValue(startAndEnd[0]);
  else{
	  this.chooser.input.value="";
	  
  }
  if(startAndEnd[1]!='null')
    this.chooser.setValue2(startAndEnd[1]);
  else{
	  if(this.chooser.input2)
	  this.chooser.input2.value="";
	  
  }
};
DayChooserField.prototype.getValue=function(){
  return this.element.value;
};

DayChooserField.prototype.setColors=function(headerColor){
	this.chooser.setColors(headerColor);
};

DayChooserField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.style.pointerEvents= this.disabled?"none":null;
	if(this.chooser != null && this.chooser.input!= null)
		this.chooser.input.disabled=this.disabled;
}
DayChooserField.prototype.focusField=function(inputInd){
	if(this.chooser!=null)
		this.chooser.input.focus();
};

DayChooserField.prototype.setCalendarBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setCalendarBgColor(color);
	}
}

DayChooserField.prototype.setBtnBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setBtnBgColor(color);
	}
}

DayChooserField.prototype.setBtnFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setBtnFgColor(color);
	}
}

DayChooserField.prototype.setCalendarBtnFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setBtnFgColor(color);
	}
}

DayChooserField.prototype.setCalendarYearFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setYearFgColor(color);
	}
}
DayChooserField.prototype.setCalendarSelYearFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setSelYearFgColor(color);
	}
}
DayChooserField.prototype.setCalendarMonthFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setMonthFgColor(color);
	}
}
DayChooserField.prototype.setCalendarSelMonthFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setSelMonthFgColor(color);
	}
}
DayChooserField.prototype.setCalendarSelMonthBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setSelMonthBgColor(color);
	}
}
DayChooserField.prototype.setCalendarWeekFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setWeekFgColor(color);
	}
}
DayChooserField.prototype.setCalendarWeekBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setWeekBgColor(color);
	}
}
DayChooserField.prototype.setCalendarDayFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setDayFgColor(color);
	}
}

DayChooserField.prototype.setCalendarXDayFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setXDayFgColor(color);
	}
}

DayChooserField.prototype.setCalendarHoverBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setHoverBgColor(color);
	}
}

DayChooserField.prototype.setEnableLastNDays=function(days) {
	if (this.chooser) {
		this.chooser.setEnableLastNDays(days);
	}
};

DayChooserField.prototype.setDisableFutureDays=function(flag) {
	if (this.chooser) {
		this.chooser.setDisableFutureDays(flag);
	}
};

////////////////////////////////////////////////////////////////////////////////
// TimeChooserField
////////////////////////////////////////////////////////////////////////////////
TimeChooserField.prototype=new FormField();
function TimeChooserField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	var that = this;
	this.element=nw("div", "timeformfield");
    this.element.style.display = "inline-block";
    this.element.tabIndex=0;
//    this.element.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e);}
    this.element.onkeydown=function(e){that.onKeyDown(e);};
}
TimeChooserField.prototype.onChangeCallback=function(){
	var that = this;
	var start;
	var end;
	start = this.timeInput.getValue();
	if(that.isRange && that.timeInput2){
		end = this.timeInput2.getValue();
	}
	that.portlet.onChange(that.id,{start:start,end:end});
}

TimeChooserField.prototype.setFieldStyle=function(style){
	if(this.timeInput){
		this.timeInput.getInput().className="time_input";
		applyStyle(this.timeInput.getInput(), style);
	}
	if(this.timeInput2){
		this.timeInput2.getInput().className="time_input";
		applyStyle(this.timeInput2.getInput(), style);
	}
}

TimeChooserField.prototype.onKeyDown=function(e){
    if(e.keyCode==9){
    	if(this.timeInput != null)
    		this.timeInput.timeChooser.hide();
    	if(this.timeInput2 != null)
    		this.timeInput2.timeChooser.hide();
    }
    if(e.keyCode==13 || e.keyCode==32){//enter of spacebar
    	if(this.timeInput != null)
    		this.timeInput.timeChooser.show();
	}
    this.handleTab(null,e);
}
TimeChooserField.prototype.setValue=function(value){
  var startAndEnd=value.split(',');
  if(startAndEnd[0]!='null'){
	  var timeComp = parseInt(startAndEnd[0]);
	  if(this.timeInput)
	  this.timeInput.setValueLong(timeComp);
  }
  else {
	  if(this.timeInput){
		  this.timeInput.input.value="";
	  }
  }
  if(startAndEnd[1]!='null'){
	  var timeComp = parseInt(startAndEnd[1]);
	  if(this.timeInput2)
	  this.timeInput2.setValueLong(timeComp);
  }
  else {
	  if(this.timeInput2){
		  this.timeInput2.input.value="";
	  }
  }
}

TimeChooserField.prototype.getValue=function(){
	//TODO not needed or used
	return null;
	
}
TimeChooserField.prototype.initCalendar=function(isRange){
	this.isRange = isRange;
	var that = this;
	this.focusedInput = 0;
	var changeFunc = function(){ that.onChangeCallback(); };
	if(this.timeInput == null){
		this.timeInput = new TimeInput(this.element);
		this.timeInput.onChange = changeFunc;
	}
	if(this.timeInput2 == null && isRange){
		this.dash = nw("span","cal_dash")
		this.dash.innerHTML=' - '; 
		this.element.appendChild(this.dash);
		this.timeInput2 = new TimeInput(this.element);
		this.timeInput2.onChange = changeFunc;
	}
	//Add inputs to inputList
	this.inputList = [];
	if(this.timeInput){
		var glassFunc = function(e) {
			// time widget is hidden already, so checking element here
			if (!isMouseInside(e, that.element,0)) {
				// click outside of field, let's execute onblur
				that.timeInput.input.onblur();
			}
		};
		this.timeInput.getInput().inputid = this.inputList.length;
		this.inputList.push(this.timeInput.getInput());
		this.timeInput.timeChooser.onGlass=glassFunc;
        this.timeInput.input.onfocus=function(e){
        	that.onFieldEvent(e,"onFocus");
    	};
        this.timeInput.input.onblur=function(e){
        	if (!that.timeInput.timeChooser.rootDiv) {
				that.onFieldEvent(e,"onBlur");
        	}
        };
	}
	if(isRange && this.timeInput2){
		var glassFunc2 = function(e) {
			if (!isMouseInside(e, that.element,0)) {
				// click outside of field, let's execute onblur
				that.timeInput2.input.onblur();
			}
		};
		this.timeInput2.getInput().inputid = this.inputList.length;
		this.inputList.push(this.timeInput2.getInput());
		this.timeInput2.timeChooser.onGlass=glassFunc2;
        this.timeInput2.input.onfocus=function(e){
        	that.focusedInput = 1;
        	that.onFieldEvent(e,"onFocus");
        };
        this.timeInput2.input.onblur=function(e){
        	if (!that.timeInput2.timeChooser.rootDiv)  {
        		that.onFieldEvent(e,"onBlur");
        	}
        };
	}
}


TimeChooserField.prototype.onFieldSizeChanged=function(){
  this.element.style.width=toPx(this.width);
  this.element.style.height=toPx(this.height);
  if(this.isRange == false){
	  var input = this.timeInput.getInput();
	  input.style.width=toPx(this.width);
	  input.style.height=toPx(this.height);
  }
  else{
	  var inputWidth = this.width/2 - 6;
	  var input = this.timeInput.getInput();
	  input.style.width=toPx(inputWidth);
	  input.style.height=toPx(this.height);
	  
	  var input2 = this.timeInput2.getInput();
	  input2.style.width=toPx(inputWidth);
	  input2.style.height=toPx(this.height);
  }
}

TimeChooserField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.style.pointerEvents= this.disabled?"none":null;
}

TimeChooserField.prototype.setHidden=function(hidden){
	this.element.style.position=hidden?"static":"absolute";
}

TimeChooserField.prototype.focusField=function(inputInd){
	// we already know which one is focused, and we store it in this.focusedInput
	if (this.timeInput != null)
		this.timeInput.input.focus();
};

TimeChooserField.prototype.setTimeDisplayFormat=function(format){
	if (this.timeInput) {
		this.timeInput.setTimeDisplayFormat(format);
	}
	if (this.timeInput2) {
		this.timeInput2.setTimeDisplayFormat(format);
	}
}

////////////////////////////////////////////////////////////////////////////////
// DateTimeChooserField
////////////////////////////////////////////////////////////////////////////////

DateTimeChooserField.prototype=new FormField();

function DateTimeChooserField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	var that = this;
    this.element=nw('div',"dateformfield");
    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};
    this.element.style.display = "inline-block";
    this.element.tabIndex=0;
//  this.element.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e);}
    this.element.onkeydown=function(e){that.onKeyDown(e);};
    // track changes
    this.prevDate=null;
    this.prevTime=null;
    this.ratio=0.65; // TODO make this a user setting in editor
}

DateTimeChooserField.prototype.onChangeCallback=function(){
	if(this.isRange)
		return;
	var that = this;
	var date = this.chooser.getValues();
	var time = this.timeInput.getValues(); // returns an object containing time broken down to hour,min,sec,ms
	var timeV =this.timeInput.getValue(); // returns a single value that is time in ms
	// no change, no op
	if(date == this.prevDate && timeV == this.prevTime)
		return;
	this.prevDate=date;
	this.prevTime=timeV;
	var m = {};
	if (date==null || timeV==null) {
		// back end needs both date and time to calculate the correct ms, if we only have one, then don't save it
		m.clearAll=true;
	} else {
		m.s_yy = date.year;
		m.s_MM = date.month;
		m.s_dd = date.day;
		
		m.s_HH = time.hours;
		m.s_mm = time.minutes;
		m.s_ss = time.seconds;
		m.s_SSS = time.millis;
	}
	that.portlet.onChange(that.id,m);
}

DateTimeChooserField.prototype.setFieldStyle=function(style){
	
//	applyStyle(this.element,style);
	if(this.chooser){
		this.chooser.input.className="cal_input";
		applyStyle(this.chooser.input,style);
		if(this.chooser.input2){
			this.chooser.input2.className="cal_input";
			applyStyle(this.chooser.input2,style);
		}
	}
	if(this.timeInput){
		this.timeInput.getInput().className="time_input";
		applyStyle(this.timeInput.getInput(), style);
	}
	// TODO this.timeInput2 is deprecated?
	if(this.isRange && this.timeInput2){
		this.timeInput2.getInput().className="time_input";
		applyStyle(this.timeInput2.getInput(), style);
	}
}
DateTimeChooserField.prototype.setDateDisplayFormat=function(format){
	if (this.chooser){
		this.chooser.setDateDisplayFormat(format);
	}
	
}
DateTimeChooserField.prototype.setTimeDisplayFormat=function(format) {
	if (this.timeInput) {
		this.timeInput.setTimeDisplayFormat(format);
	}
}
DateTimeChooserField.prototype.onKeyDown=function(e){
    if(e.keyCode==9){
    	if(this.chooser != null)
    		this.chooser.hideCalendar();
    	if(this.timeInput != null)
    		this.timeInput.timeChooser.hide();
    }
    this.handleTab(null,e);
}

DateTimeChooserField.prototype.setHidden=function(hidden){
	this.element.style.position=hidden?"static":"absolute";
}
DateTimeChooserField.prototype.initCalendar=function(isRange){
	var that=this;
	if(this.chooser!=null){
		if(this.chooser.isRange == isRange)
			return;
		this.chooser.hideCalendar();
	}
	this.chooser=new DateChooser(this.element,isRange);
	
	var changeFunc = function(){ that.onChangeCallback(); };
	var closeFunc = function() {
		that.chooser.input.focus();
	}
	var glassFunc = function(e) {
		// this is an onclick event
		if (that.chooser.rootDiv) {
			var isIn = isMouseInside(e, that.chooser.rootDiv,0);
			if (!isIn) {
	//			// clicked outside the glass or the field itself, hide calendar first
				that.chooser.hideCalendar();
				if (!isMouseInside(e, that.chooser.input, 0)) {
					// send blur to backend if user clicked anywhere outside of the field itself
					that.chooser.input.blur();
				}
			}
		} 
	}
	var glassFunc2 = function(e) {
		if (!isMouseInside(e, that.timeInput.input, 0)) {
			// send blur to backend if user clicked anywhere outside of the field itself
			that.timeInput.input.onblur();
		}
	}
	this.timeInput = new TimeInput(this.element);
	this.chooser.onChange=changeFunc;
	// need to tell backend this field is blurred
	this.chooser.handleCloseButton=closeFunc;
	this.chooser.onGlass=glassFunc;
	this.timeInput.timeChooser.onGlass = glassFunc2;
	this.timeInput.onChange = changeFunc;
	//Add inputs to inputList
	this.inputList = [];
	if(this.chooser){
		this.chooser.input.inputid = this.inputList.length;
		this.inputList.push(this.chooser.input);
        this.chooser.input.onfocus=function(e){ that.onFieldEvent(e,"onFocus"); };
        this.chooser.input.onblur=function(e){ 
        	// this event has the highest priority (before any of our custom hooks are fired)
        	// js will call blur when user clicks on anywhere that is not on the date portion of the field
        	// if user clicked on a day, onClickDay will fire after this
        	if (that.chooser.rootDiv) {
        		// maintain focus if calendar still open
        		that.chooser.input.focus();
        	} else {
        		// field is focused, but no calendar
				that.onFieldEvent(e,"onBlur"); 
        	}
		};
	}
	if(this.timeInput){
		this.timeInput.getInput().inputid = this.inputList.length;
		this.inputList.push(this.timeInput.getInput());
        this.timeInput.input.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
        this.timeInput.input.onblur=function(e){if (that.timeInput.timeChooser.rootDiv == null){ that.onFieldEvent(e,"onBlur");}};
	}
}



DateTimeChooserField.prototype.onFieldSizeChanged=function(){
	const cStyle=getComputedStyle(this.chooser.input);
	const tStyle=getComputedStyle(this.timeInput.input);
	const available=this.width-pxToInt(cStyle.paddingLeft)-pxToInt(cStyle.paddingRight)-pxToInt(tStyle.paddingLeft)-pxToInt(tStyle.paddingRight);
	
	this.chooser.input.style.width = toPx(available*this.ratio);
	this.timeInput.input.style.width = toPx(available*(1.0-this.ratio));
	this.element.style.width=toPx(this.width);
	this.element.style.height=toPx(this.height);
	this.chooser.input.style.height=toPx(this.height);
	if(this.timeInput){
	  this.timeInput.getInput().style.height=toPx(this.height);
	}
};

DateTimeChooserField.prototype.setValue=function(value){
  var startAndEnd=value.split(',');
  var startDate = startAndEnd[0];
  var startTime = startAndEnd[1];
  if(startDate != 'null' && startTime != 'null'){
	  this.chooser.parseInput(startDate,1);
	  this.timeInput.setValueLong(parseInt(startTime));
  }
  else{
	 this.chooser.input.value=""; 
	 this.timeInput.input.value="";
  }
  
//	debugger;
//  var startAndEnd=value.split(',');
//  if(startAndEnd[0]!='null'){
//	  var dt = new Date(parseInt(startAndEnd[0]));
//	  var yyyyMMdd = this.chooser.toYmd(dt.getFullYear(), dt.getMonth()+1, dt.getDate());
//	  this.chooser.setValue(yyyyMMdd);
//	  var timeComp = parseInt(startAndEnd[0]);
//	  this.timeInput.setValueUTC(timeComp);
//  }
//  if(startAndEnd[1]!='null'){
//	  var dt = new Date(parseInt(startAndEnd[1]));
//	  var yyyyMMdd = this.chooser.toYmd(dt.getFullYear(), dt.getMonth()+1, dt.getDate());
//	  this.chooser.setValue2(yyyyMMdd);
//	  var timeComp = parseInt(startAndEnd[1]);
//	  this.timeInput.setValueUTC(timeComp);
//  }
};
DateTimeChooserField.prototype.getValue=function(){
	//TODO not used
	return null;
};

DateTimeChooserField.prototype.setColors=function(headerColor){
	this.chooser.setColors(headerColor);
};

DateTimeChooserField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.style.pointerEvents= this.disabled?"none":null;
	if(this.chooser != null && this.chooser.input!= null)
		this.chooser.input.disabled=this.disabled;
}
DateTimeChooserField.prototype.focusField=function(inputInd){
	if (this.chooser != null)
		this.chooser.input.focus();
};

DateTimeChooserField.prototype.setCalendarBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setCalendarBgColor(color);
	}
}

DateTimeChooserField.prototype.setBtnBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setBtnBgColor(color);
	}
}

DateTimeChooserField.prototype.setBtnFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setBtnFgColor(color);
	}
}

DateTimeChooserField.prototype.setCalendarBtnFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setBtnFgColor(color);
	}
}

DateTimeChooserField.prototype.setCalendarYearFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setYearFgColor(color);
	}
}
DateTimeChooserField.prototype.setCalendarSelYearFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setSelYearFgColor(color);
	}
}
DateTimeChooserField.prototype.setCalendarMonthFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setMonthFgColor(color);
	}
}
DateTimeChooserField.prototype.setCalendarSelMonthFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setSelMonthFgColor(color);
	}
}
DateTimeChooserField.prototype.setCalendarSelMonthBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setSelMonthBgColor(color);
	}
}
DateTimeChooserField.prototype.setCalendarWeekFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setWeekFgColor(color);
	}
}
DateTimeChooserField.prototype.setCalendarWeekBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setWeekBgColor(color);
	}
}
DateTimeChooserField.prototype.setCalendarDayFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setDayFgColor(color);
	}
}

DateTimeChooserField.prototype.setCalendarXDayFgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setXDayFgColor(color);
	}
}

DateTimeChooserField.prototype.setCalendarHoverBgColor=function(color) {
	if (this.chooser) {		
		this.chooser.setHoverBgColor(color);
	}
}

DateTimeChooserField.prototype.setEnableLastNDays=function(days) {
	if (this.chooser) {
		this.chooser.setEnableLastNDays(days);
	}
};

DateTimeChooserField.prototype.setDisableFutureDays=function(flag) {
	if (this.chooser) {
		this.chooser.setDisableFutureDays(flag);
	}
};

////////////////////////////////////////////////////////////////////////////////
// NumericRangeField
////////////////////////////////////////////////////////////////////////////////
NumericRangeField.prototype=new FormField();
// This is the slider field in the GUI
function NumericRangeField(portlet,id,title){
	FormField.call(this,portlet,id,title);
    this.element=nw('div', "");
    this.element.style.display = "inline-block";
    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};
    this.element.onkeydown=function(e){that.onKeyDown(e);};
    var that=this;
}

NumericRangeField.prototype.setFieldStyle=function(style){
  this.element.className="";
  applyStyle(this.element,style);
}
NumericRangeField.prototype.setHidden=function(hidden){
	this.element.style.position=hidden?"static":"absolute";
	this.hidden=hidden;
}
NumericRangeField.prototype.setDisabled=function(disabled){
	this.element.style.pointerEvents= disabled? "none":"auto";
	this.disabled=disabled;
}

NumericRangeField.prototype.applySliderStyles=function(style,sliderValFontColor,sliderValBgColor,sliderValBorderColor,gripColor,leftTrackColor,trackColor,sliderValBorderRadius,sliderValBorderWidth,sliderValFontFam) {
	applyStyle(this.slider.lowguide,"_bg="+leftTrackColor);
    applyStyle(this.slider.guide, "_bg="+trackColor);
    applyStyle(this.slider.grabber, "_bg="+gripColor);
    applyStyle(this.slider.grabber2, "_bg="+gripColor);
    applyStyle(this.slider.val, "_fg="+sliderValFontColor);
    applyStyle(this.slider.val, "_bg="+sliderValBgColor);
    applyStyle(this.slider.val, "style.borderColor="+sliderValBorderColor);
	applyStyle(this.slider.val, "style.borderRadius="+ toPx(sliderValBorderRadius));
	applyStyle(this.slider.val, "style.borderWidth="+ toPx(sliderValBorderWidth));
	applyStyle(this.slider.val, "style.fontFamily="+sliderValFontFam);
	// below requires height and width scaling
//	applyStyle(this.slider.val, "style.fontSize="+toPx(sliderValFontSize));
    applyStyle(this.slider.input,style);
    applyStyle(this.element,style);
}

NumericRangeField.prototype.initSlider=function(value,minVal,maxVal,precision,step,width,textHidden, sliderHidden, nullable,defaultValue){
  if(this.slider!=null){
	this.slider.setRange(minVal,maxVal,step,precision);
	this.slider.setValue(value,true,true,null);
	this.slider.resize(this.width,textHidden);
	this.slider.setSliderHidden(sliderHidden);
	return;
  }
  var that=this;
  this.slider=new Slider(this.element,value,minVal,maxVal,width,precision,step,textHidden,null,sliderHidden, nullable,defaultValue, null);
  this.slider.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e);};
  this.slider.val.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
  this.slider.val.onblur=function(e){that.onFieldEvent(e,"onBlur");};
  this.slider.onValueChanged=function(rawValue,value,fromSliding){
  	if(fromSliding){
        if(that.movingInterval==null)
  	    that.movingInterval=window.setInterval(function(){that.checkMovementInterval();},200);
  	  that.isMoving=true;
  	}else{
        if(that.movingInterval!=null){
	        window.clearInterval(that.movingInterval);
          that.movingInterval=null;
        }
        that.portlet.onChange(that.id,{value:that.slider.getValue()});
  	  that.isMoving=false;
  	}
  };
	this.slider.onNullableChanged=function(_value){
	  	that.portlet.onChange(that.id, {value: _value});
	};
  

    this.slider.val.fieldId = this.id;
    this.slider.val2.fieldId = this.id;
    this.slider.lowguide.fieldId = this.id;
    this.slider.guide.fieldId = this.id;
    this.slider.grabber.fieldId = this.id;
    this.slider.grabber2.fieldId = this.id;
    this.slider.resize(this.width,textHidden);
};

NumericRangeField.prototype.onFieldSizeChanged=function(){
	if(this.slider != null && this.element!= null){
	this.slider.setWidth(this.width);
	this.slider.setHeight(this.height);
	this.element.style.width=toPx(this.width);
	this.element.style.height=toPx(this.height);
	}
};

NumericRangeField.prototype.checkMovementInterval=function(){
	if(this.isMoving){
		this.isMoving=false;
	}else{
      this.portlet.onChange(this.id,{value:this.slider.getValue()});
	  window.clearInterval(this.movingInterval);
      this.movingInterval=null;
	}
};

NumericRangeField.prototype.setValue=function(value){
  this.slider.setValue(value,true,false);
  this.element.value=value;
};
NumericRangeField.prototype.getValue=function(){
	  return this.slider.getValue();
};

NumericRangeField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.style.pointerEvents= this.disabled?"none":null;
	if(this.slider!= null && this.slider.val != null)
		this.slider.val.disabled = this.disabled;
}
NumericRangeField.prototype.focusField=function(){
	if(this.slider!= null && this.slider.val != null){
		this.slider.val.focus();
	   return true;
	}
	return false;
};

NumericRangeField.prototype.onKeyDown=function(e){
	if(e.keyCode==9)
		return this.form.onTab(this,e);
	var that = this;
	if(e.keyCode==13 || (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){
		var pos=getCursorPosition(this.element);
		this.portlet.onCustom(this.id, "updateCursor", {pos:pos});
	    this.portlet.onKey(this.id,e,{pos:pos});
	    if(e.keyCode==9)
	      e.preventDefault();
}
}
////////////////////////////////////////////////////////////////////////////////
// NumericRangeSubRangeField
////////////////////////////////////////////////////////////////////////////////
NumericRangeSubRangeField.prototype=new FormField();
// This is the range field in the GUI
function NumericRangeSubRangeField(portlet,id,title){
	FormField.call(this,portlet,id,title);
    this.element=nw('div');    
    this.element.style.display = "inline-block";
	var that=this;
    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};
    this.element.onkeydown=function(e){that.onKeyDown(e);};

}

NumericRangeSubRangeField.prototype.setFieldStyle=function(style){
  this.element.className="";
  applyStyle(this.element,style);
}

/*
NumericRangeSubRangeField.prototype.onKeyDown=function(e){
    this.handleTab(null,e);
}
*/

NumericRangeSubRangeField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.style.pointerEvents= disabled? "none":"auto";
}

NumericRangeSubRangeField.prototype.applySliderStyles=function(style,sliderValFontColor,sliderValBgColor,sliderValBorderColor,gripColor,trackColor,leftTrackColor,sliderValBorderRadius,sliderValBorderWidth,sliderValFontFam) {
	applyStyle(this.slider.lowguide,"_bg="+trackColor);
    applyStyle(this.slider.guide, "_bg="+trackColor);
    applyStyle(this.slider.grabber, "_bg="+gripColor);
    applyStyle(this.slider.grabber2, "_bg="+gripColor);
    applyStyle(this.slider.val, "_fg="+sliderValFontColor);
    applyStyle(this.slider.val2, "_fg="+sliderValFontColor);
    applyStyle(this.slider.val, "_bg="+sliderValBgColor);
    applyStyle(this.slider.val2, "_bg="+sliderValBgColor);
    applyStyle(this.slider.val, "style.borderColor="+sliderValBorderColor);
    applyStyle(this.slider.val2, "style.borderColor="+sliderValBorderColor);
	applyStyle(this.slider.val, "style.borderRadius="+ toPx(sliderValBorderRadius));
	applyStyle(this.slider.val2, "style.borderRadius="+toPx(sliderValBorderRadius));
	applyStyle(this.slider.val, "style.borderWidth="+ toPx(sliderValBorderWidth));
	applyStyle(this.slider.val2, "style.borderWidth="+toPx(sliderValBorderWidth));
	applyStyle(this.slider.val, "style.fontFamily="+sliderValFontFam);
	applyStyle(this.slider.val2, "style.fontFamily="+sliderValFontFam);
	// below requires height and width scaling
//	applyStyle(this.slider.val, "style.fontSize="+toPx(sliderValFontSize));
//	applyStyle(this.slider.val2, "style.fontSize="+toPx(sliderValFontSize));
    applyStyle(this.slider.input,style);
    applyStyle(this.element,style);
};

NumericRangeSubRangeField.prototype.initSliders=function(value,value2,minVal,maxVal,width,precision,step,textHidden, sliderHidden, nullable, defaultValue, defaultValue2) {
	if (this.slider != null) {
		this.slider.setRange(minVal,maxVal,step,precision);
	    this.slider.setValue(value,true,true,value2);
	    this.slider.resize(this.width,textHidden);
	    return;
	}
	// init
	var that=this;
	this.slider=new Slider(this.element,value,minVal,maxVal,width,precision,step,textHidden,value2, sliderHidden, nullable, defaultValue, defaultValue2);
	this.slider.onValueChanged=function(rawValue,value,fromSliding){
		if(that.movingInterval==null)
		  that.movingInterval=window.setInterval(function(){that.checkMovementInterval();},200);
		that.isMoving=true;
	};
	this.slider.onNullableChanged=function(_value, _value2){
		  that.portlet.onChange(that.id, {value: _value, value2:_value2})
	};
	this.slider.val.fieldId = this.id;
	this.slider.val2.fieldId = this.id;
	this.slider.lowguide.fieldId = this.id;
	this.slider.guide.fieldId = this.id;
	this.slider.grabber.fieldId = this.id;
	this.slider.grabber2.fieldId = this.id;
    this.slider.val.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
    this.slider.val.onblur=function(e){that.onFieldEvent(e,"onBlur");};
    this.slider.val2.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
    this.slider.val2.onblur=function(e){that.onFieldEvent(e,"onBlur");};
	// prepare inputs
	this.inputList = [];
	this.slider.val.inputid = this.inputList.length;
	this.inputList.push(this.slider.val);
	this.slider.val2.inputid = this.inputList.length;
	this.inputList.push(this.slider.val2);
};

NumericRangeSubRangeField.prototype.onFieldSizeChanged=function(){
	this.slider.setWidth(this.width);
	this.slider.setHeight(this.height);
	this.element.style.width=toPx(this.width);
	this.element.style.height=toPx(this.height);
};

NumericRangeSubRangeField.prototype.checkMovementInterval=function(){
	if(this.isMoving){
		this.isMoving=false;
	}else{
      this.portlet.onChange(this.id,{value:this.slider.getValue(),value2:this.slider.getValue2()});
	  window.clearInterval(this.movingInterval);
      this.movingInterval=null;
	}
};
NumericRangeSubRangeField.prototype.setValue=function(value){
	  var startAndEnd=value.split(',');
	  this.slider.setValue(startAndEnd[0], true, false, startAndEnd[1]);
};
NumericRangeSubRangeField.prototype.getValue=function(){
  return this.slider.minVal + "," + this.slider.getMaxValue();
};

NumericRangeSubRangeField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.style.pointerEvents= this.disabled?"none":null;
	if(this.slider!= null){
		if(this.slider.val != null)
			this.slider.val.disabled = this.disabled;
		if(this.slider.val2 != null)
			this.slider.val2.disabled = this.disabled;
	} 
}
NumericRangeSubRangeField.prototype.focusField=function(inputInd){
	if(inputInd != null){
		this.inputList[inputInd].focus();
		return true;
	}
	else if(this.slider!= null && this.slider.val != null){
		this.slider.val.focus();
	   return true;
	}
	return false;
};

NumericRangeSubRangeField.prototype.onKeyDown=function(e){
	if(e.keyCode==9)
		return this.form.onTab(this,e);
	var that = this;
	if(e.keyCode==13 || (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){
		var pos=getCursorPosition(this.element);
		this.portlet.onCustom(this.id, "updateCursor", {pos:pos});
	    this.portlet.onKey(this.id,e,{pos:pos});
	    if(e.keyCode==9)
	      e.preventDefault();
}
}


////////////////////////////////////////////////////////////////////////////////
// TextAreaField
////////////////////////////////////////////////////////////////////////////////
TextAreaField.prototype=new FormField();

function TextAreaField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	this.input=nw('textarea');
	this.input.style.resize='none';
    this.input.style.verticalAlign='middle';
    this.input.style.left='0px';
    this.element=nw('div');
    this.element.style.display="inline-block";
    this.element.appendChild(this.input);

	var that=this;
	this.oldValue= "";
    this.input.onblur=function(){that.onChangeDiff();};
    this.input.oninput=function(){that.onChangeDiff();};
    this.input.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.element));};
    this.input.onkeydown=function(e){that.onKeyDown(e);};
    this.input.onfocus=function(e){that.onFieldEvent(e, "onFocus");};
    this.input.onblur=function(e){that.onFieldEvent(e, "onBlur");};
}
TextAreaField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	if(this.input)
		this.input.disabled=this.disabled;
}
TextAreaField.prototype.setHidden=function(hidden){
	if(hidden){
		this.input.style.display="inline-block";
		this.input.style.width="auto";
		this.input.style.height="auto";
	}
	else{
		this.input.style.display="block";
		this.input.style.width="100%";
		this.input.style.height="100%";
	}
	this.input.style.position="static";
	this.element.style.position=hidden?"static":"absolute";
}
TextAreaField.prototype.onChangeDiff=function(){
	var newValue = this.getValue();
	var change = strDiff(this.oldValue, newValue);
	this.oldValue = newValue;
	this.portlet.onChange(this.id, {c:change.c,s:change.s,e:change.e,mid:this.getModificationNumber()});
}

TextAreaField.prototype.onKeyDown=function(e){
	if(e.keyCode==9)
		return this.form.onTab(this,e);
	var that = this;
	if(e.keyCode==13 || (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){
		var pos=getCursorPosition(this.element);
		this.portlet.onCustom(this.id, "updateCursor", {pos:pos});
	    this.portlet.onKey(this.id,e,{pos:pos});
	    if(e.keyCode==9)//TAB
	      e.preventDefault();
	}

}

TextAreaField.prototype.setFieldStyle=function(style){
  this.input.className="textarea";
  applyStyle(this.input,style);
}

TextAreaField.prototype.init=function(hasButton,callbackKeys){
  var that=this;
//  this.element.disabled=disabled;
  this.callbackKeys=callbackKeys;
  if(hasButton && this.button==null){
      this.button=nw('div','textfield_plusbutton');
      this.button.onmousedown=function(e){that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.input));};
      this.button.style.top='0px';
      this.button.style.left='94.5%';
      this.button.style.height='25px';
      this.button.style.width='25px';
      this.element.appendChild(this.button);
  }else if(!hasButton && this.button!=null){
      this.element.removeChild(this.button);
	  this.button=null;
  }
  
  if (hasButton) {
	  this.input.style.width = "calc(100% - 25px)";
  }else
	  this.input.style.width = "100%";
}
TextAreaField.prototype.onFieldSizeChanged=function(){
  this.element.style.width=toPx(this.width);
  this.element.style.height=toPx(this.height);
}
TextAreaField.prototype.setValue=function(value){
  if (value == null){
	  this.input.value="";
	  this.oldValue = "";
  }else{
	  this.input.value=value;
	  this.oldValue = value;
  }
};
TextAreaField.prototype.getValue=function(){
	return this.input.value;
};
TextAreaField.prototype.moveCursor=function(cursorPos){
  setCursorPosition(this.input,cursorPos);
};
TextAreaField.prototype.changeSelection=function(start,end){
	setSelection(this.input,start,end);
}

TextAreaField.prototype.applyTextStyle = function(){
	this.saveRangePosition();
	this.initTextStyle();
	this.restoreRangePosition();
}
function getNodeIndex(n){var i=0;while(n=n.previousSibling)i++;return i}

////////////////////////////////////////////////////////////////////////////////
// CheckboxField
////////////////////////////////////////////////////////////////////////////////
CheckboxField.prototype=new FormField();

function CheckboxField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	this.wrapper=nw('div', 'ckFieldWrapper');
    this.element=nw('div', 'checkboxField');
    this.element.appendChild(this.wrapper);
   	this.checkMark = nw('span', 'ckMark');
   	this.checkUnicode = "&#10004;"
   	this.checkMark.innerHTML = this.checkUnicode;
   	this.wrapper.appendChild(this.checkMark);
    this.element.tabIndex=1;
    this.element.type='checkbox';
    this.input = this.element;
	var that=this;
    this.element.onchange=function(){that.portlet.onChange(that.id,{value:that.getValue()});};
    this.element.onmousedown=function(e){if(getMouseButton(e)==1)that.toggleChecked(); that.portlet.onChange(that.id, {value:that.getValue()}); if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};
    this.getLabelElement().style.pointerEvents = "all";
//    this.getLabelElement().onmousedown=function(e){if(getMouseButton(e)==1)that.toggleChecked();that.portlet.onChange(that.id,{value:that.getValue()});};
    this.input.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e); else if(e.keyCode==13 || e.keyCode==32) {that.toggleChecked();that.portlet.onChange(that.id, {value:that.getValue()});}}
    this.input.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
    this.input.onblur=function(e){that.onFieldEvent(e,"onBlur");};
//    this.getLabelElement().onmouseover=function(e){if(getMouseButton(e)==1)that.element.checked = !that.element.checked};
//    this.element.onmouseover=function(e){if(getMouseButton(e)==1)that.element.checked = !that.element.checked};
}


CheckboxField.prototype.focusField=function(){
    this.element.focus();
    return  true;
};

CheckboxField.prototype.setFieldStyle=function(style){
  this.input.className="checkboxField";
  applyStyle(this.wrapper,style);
  if(style && style.indexOf("_fg") != -1){
  	  var fgColor = getStyleValue("_fg", style);
  	  this.checkMark.style.color = fgColor;
  }	
}
CheckboxField.prototype.setCssStyle=function(style){
	  this.input.className="checkboxField";
	  applyStyle(this.input,style);
	}
CheckboxField.prototype.applyStyles=function(bgColor, checkColor, borderColor) {
  	this.wrapper.style.border = "1px solid " + borderColor;
  	this.wrapper.style.backgroundColor = bgColor;
  	this.checkMark.style.color = checkColor;
}
CheckboxField.prototype.init=function(bgColor, checkColor, borderColor){
	this.applyStyles(bgColor, checkColor, borderColor);
}
CheckboxField.prototype.toggleChecked=function(){
	if(this.disabled)
		return;
	this.value = !!!this.value;
	this.element.checked=this.value;
	if(this.value){
		this.element.classList.add("checked");
		this.checkMark.innerHTML = this.checkUnicode;
	}else {
		this.element.classList.remove("checked");
		this.checkMark.innerHTML = "";
	}
}

CheckboxField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.disabled = disabled;
	this.element.style.pointerEvents = disabled?"none":"all";
	this.element.style.opacity = disabled?"0.4":"1.0";
	this.getLabelElement().style.opacity = disabled ?"0.4":"1.0";
}

CheckboxField.prototype.onFieldSizeChanged=function(){
	this.element.style.height=toPx(this.height-6);
	this.element.style.width=toPx(this.width-6);
	var size = this.height < this.width ? this.height : this.width; 
   	this.wrapper.style.width = toPx(size);
   	this.wrapper.style.height = toPx(size);
   	this.checkMark.style.fontSize = toPx(size - 5);
}
CheckboxField.prototype.setSize=function(width, height) {
	this.element.style.height=toPx(width);
	this.element.style.width=toPx(height);
   	this.wrapper.style.width = toPx(width);
   	this.wrapper.style.height = toPx(height);
   	this.checkMark.style.fontSize = toPx(width - 5);
}
CheckboxField.prototype.setValue=function(value){
	this.value = value=="true";
	this.element.checked=this.value;
	if(this.value){
		this.element.classList.add("checked");
		this.checkMark.innerHTML = this.checkUnicode;
	}else {
		this.element.classList.remove("checked");
		this.checkMark.innerHTML = "";
	}
};
CheckboxField.prototype.getValue=function(){
  return this.value;
};
////////////////////////////////////////////////////////////////////////////////
//RadioButtonField
////////////////////////////////////////////////////////////////////////////////
RadioButtonField.prototype=new FormField();

function RadioButtonField(portlet, id, title) {
	FormField.call(this,portlet,id,title);
	this.element=nw('input', 'radioButtonField');
	this.element.tabIndex=1;
	this.element.type='radio';
  	this.element.style.margin='3px 0px 0px 0px';
	this.input = this.element;
	this.groupedRadios;
	this.groupId;
	var that=this;
	this.input.onchange=function(){
		that.value = that.input.checked;
		that.portlet.onChange(that.id,{value:that.input.checked});
		that.repaintRadios();
	};
	this.input.onkeydown=function(e){
		that.handleTab(null,e);	
	};
	this.element.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
	this.element.onblur=function(e){that.onFieldEvent(e,"onBlur");};
	this.getLabelElement().style.pointerEvents = "all";
}
function paintRadio(radioInput, bgColor, fontColor) {
	fontColor = radioInput.checked ? fontColor : "transparent";
	var radius = parseFloat(radioInput.style.height);
	const svgContent = [
		    '<svg width="200" height="200" xmlns="http://www.w3.org/2000/svg">',
		      '<circle cx="100" cy="100" r="' + radius + '" fill="' + bgColor + '" />',
		      '<circle cx="100" cy="100" r="' + radius/5 + '" fill="' + fontColor + '" />',
		    '</svg>'
		  ].join('');
	const encodedSvg = encodeURIComponent(svgContent).replace(/'/g, '%27').replace(/"/g, '%22');
	radioInput.style.backgroundImage = 'url("data:image/svg+xml,' + encodedSvg + '")';
}
RadioButtonField.prototype.init=function(groupId){
	this.groupId = groupId;
	this.input.setAttribute("name", groupId);
}
	
RadioButtonField.prototype.setRadioStyle=function(bgColor,fontColor,borderColor,borderWidth) {
	this.bgColor = bgColor;
	this.fontColor = fontColor;
	this.input.style.backgroundColor=bgColor;
	this.input.style.borderColor=borderColor;
	this.input.style.borderWidth=toPx(borderWidth);
	this.input.style.borderStyle="solid";
	// each input has its own bgColor/font Color
	this.input.bgColor = bgColor;
	this.input.fontColor = fontColor;
	this.repaintRadios(this.input, this.bgColor, this.fontColor);
}
RadioButtonField.prototype.repaintRadios=function() {
	const radios = this.getRadiosByGroupName(this.groupId);
	for (var radioInput of radios)
		paintRadio(radioInput, radioInput.bgColor, radioInput.fontColor);
}
RadioButtonField.prototype.getRadiosByGroupName=function(groupId) {
	return document.getElementsByName(groupId);
}
RadioButtonField.prototype.focusField=function(){
	this.input.focus();
	return  true;
}
RadioButtonField.prototype.setFieldStyle=function(style){
	this.input.className="radioButtonField";
	applyStyle(this.input,style);
	paintRadio(this.input, this.bgColor, this.fontColor);
	paintRadio(this.input, this.input.bgColor, this.input.fontColor);
	paintRadio(this.input, this.bgColor, this.fontColor);
	this.input.style.borderRadius='1000px';//force round
}
RadioButtonField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.disabled = disabled;
	this.element.style.pointerEvents = disabled?"none":"all";
	this.element.style.opacity = disabled?"0.4":"none";
	this.getLabelElement().style.opacity = disabled ?"0.4":"none";
}

RadioButtonField.prototype.onFieldSizeChanged=function(){
	// handle negative
	var w=max(this.width-6,0);
	var h=max(this.height-6,0);
	size=min(w,h);
	this.element.style.height=toPx(size);
	this.element.style.width=toPx(size);
	// offset should be based on actual width/height
	this.element.style.top=toPx(max(0,(this.element.style.height-size)/2));
	this.element.style.left=toPx(max(0,(this.element.style.width-size)/2));
	this.element.style.borderRadius=toPx(size/2);
}
RadioButtonField.prototype.setValue=function(value){
	this.value = value == "true";
	this.input.checked = this.value;
	this.repaintRadios(this.input, this.input.bgColor, this.input.fontColor);
}
RadioButtonField.prototype.getValue=function(){
	return this.value;
}
////////////////////////////////////////////////////////////////////////////////
// FileUploadField
////////////////////////////////////////////////////////////////////////////////
FileUploadField.prototype=new FormField();

function FileUploadField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	var height=toPx(20);
	var that=this;
	this.el=that.portlet.portlet.divElement;
	this.element=nw2('div',null,that.el);
	
    this.uploadFileButtonElement=nw2('button',null,that.el);
    this.uploadUrlButtonElement=nw2('button',null,that.el);
    this.inputElement=nw2('div',null,that.el);
    this.inputElement.style.overflow='hidden';
    
    this.inputElement.style.background='white';
    this.inputElement.style.cursor='pointer';
    this.inputElement.style.border='1px solid black';
    this.inputElement.innerHTML='&nbsp;';

    this.inputElement.style.fontSize = "12px";
    this.inputElement.style.fontFamily = "Arial";
    
	this.element.style.whiteSpace='nowrap';
	this.element.style.display = "inline-block";
//	this.element.style.position = "static";
	this.inputElement.style.display='inline-block';
	this.uploadFileButtonElement.style.display='inline-block';
	this.uploadUrlButtonElement.style.display='inline-block';
	this.inputElement.style.position='relative';
	this.uploadFileButtonElement.style.position='relative';
	this.uploadUrlButtonElement.style.position='relative';
    this.inputElement.style.height=height;
	this.uploadUrlButtonElement.style.height=height;
	this.uploadFileButtonElement.style.height=height;	
    
    this.fileInput=nw2('input',null,that.el);
    this.fileInput.style.position='absolute';
    this.fileInput.type='file';
    this.fileInput.name='fileData';
    this.fileInput.style.display='none';
	
	
    this.inputElement.tabIndex=0;
    this.inputElement.onclick=function(){
    	// ensure uploading same file triggers onchange
    	that.fileInput.value =null;
    	that.fileInput.click(); 
	};
    this.inputElement.onchange=function(){ that.portlet.onChange(that.id,{value:that.getValue()}); };
    this.inputElement.onkeydown=function(e){
    	if(e.keyCode==13 || e.keyCode==32)//enter of spacebar
    	  return that.fileInput.click();
    	else if(e.keyCode==9)
    	  return that.form.onTab(that,e);
    }
    this.uploadFileButtonElement.onclick=function(){ 
    	// ensure uploading same file triggers onchange
    	that.fileInput.value =null;
    	that.fileInput.click(); 
	};
    
    this.uploadUrlButtonElement.onclick=function(){ that.onUrlButton(); };
	
	//this.fileInput.onchange=function(){ that.onFileSelected(); };
    var stopInput = function(ev) {
        ev.preventDefault();
        ev.stopPropagation();
    };
    
    this.element.ondrag = stopInput;
    this.element.ondragstart = stopInput;
    this.element.ondragend = stopInput;
    this.element.ondragover = stopInput;
    this.element.ondragenter = stopInput;
    this.element.ondragleave = stopInput;
    
	this.fileInput.onchange=function(ev){ 
        var eventType = "on"+event["type"];
        that.processFile(ev, eventType);
    };
    this.element.ondrop=function(ev){
        ev.preventDefault();
        var eventType = event.type;
        that.processFile(ev, eventType);            
    };
    this.inputElement.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
    this.inputElement.onblur=function(e){that.onFieldEvent(e,"onBlur");};
    this.element.appendChild(this.inputElement);
    this.buildDiv();
}

FileUploadField.prototype.displayFileName=function(files){
    var filename = files[0].name;
    this.inputElement.classList.add("fileSelected");
    this.inputElement.value=filename;
    this.inputElement.textContent=filename;
}

FileUploadField.prototype.processFile=function(ev,eventType){
    var files = null;
    if (eventType == "onchange") {
        // hardcoded for single file only
        files = ev.currentTarget.files;
    } else if (eventType="drop") {
        files = ev.dataTransfer.files;
    } else {
        console.log("event type not of drop or onchange");
    }
    this.displayFileName(files);
    this.upload(files, eventType);
}

FileUploadField.prototype.upload=function(files, eventType) {
    var frame=getHiddenIFrame();
    var that=this;
    var form=nw2('form',null,that.el);
    var filename = files[0].name;
    this.fileInput.files=files;
    form.method='post';
    form.enctype='multipart/form-data';
    form.target=frame.name;
    form.action=portletManager.callbackUrl;
    form.appendChild(this.fileInput);
    form.appendChild(this.createHiddenField('<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID"/>',portletManager.pgid));
    form.appendChild(this.createHiddenField('action','upload'));
    form.appendChild(this.createHiddenField('type', eventType));
    form.appendChild(this.createHiddenField('portletId',this.portlet.getId()));
    form.appendChild(this.createHiddenField('fieldId',this.id));
    form.appendChild(this.createHiddenField('pageUid',portletManager.pageUid));
    form.appendChild(this.createHiddenField('mid',this.getModificationNumber()));
    getWindow(that.el).document.body.appendChild(form);
    form.submit();
    getWindow(that.el).document.body.removeChild(form);

}

FileUploadField.prototype.setFieldStyle=function(style){
  this.inputElement.className="";
	applyStyle(this.inputElement,style);
}
FileUploadField.prototype.focusField=function(){
    this.inputElement.focus();
    return  true;
};
FileUploadField.prototype.setHidden=function(hidden){
	this.element.style.position= hidden?"static":"absolute";
	this.inputElement.style.position= hidden?"static":"absolute";
}
FileUploadField.prototype.buildDiv=function(){
    if(this.uploadFileButtonText){
      this.uploadFileButtonElement.innerHTML=this.uploadFileButtonText;
      this.element.appendChild(this.uploadFileButtonElement);
    }
    if(this.uploadUrlButtonText){
      this.uploadUrlButtonElement.innerHTML=this.uploadUrlButtonText;
      this.element.appendChild(this.uploadUrlButtonElement);
    }
}
// Deprecated
FileUploadField.prototype.onFileSelected=function(){
	console.error("onFileSelected is deprecated");
	var filename=this.fileInput.value.split("\\");
	filename=filename[filename.length-1];
	this.inputElement.classList.add("fileSelected");
	this.inputElement.value=filename;
	this.inputElement.textContent=filename;
	if(filename){
        var frame=getHiddenIFrame();
        var form=nw('form');
        form.method='post';
        form.enctype='multipart/form-data';
        form.target=frame.name;
        form.action=portletManager.callbackUrl;
        form.appendChild(this.fileInput);
        form.appendChild(this.createHiddenField('action','upload'));
        form.appendChild(this.createHiddenField('type','onchange'));
        form.appendChild(this.createHiddenField('portletId',this.portlet.getId()));
        form.appendChild(this.createHiddenField('value',filename));
        form.appendChild(this.createHiddenField('fieldId',this.id));
        form.appendChild(this.createHiddenField('pageUid',portletManager.pageUid));
        form.appendChild(this.createHiddenField('mid',this.getModificationNumber()));
        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);
	}
};

FileUploadField.prototype.createHiddenField=function(name,value){
	var that=this;
  var r=nw2('input',null,that.el); 
  r.type='hidden';
  r.name=name; 
  r.value=value;
  return r;
}

FileUploadField.prototype.onFieldSizeChanged=function(){
	this.uploadFileButtonElement.style.left=toPx(this.width + 2);
	if (this.uploadFileButtonElement.innerHTML) {
		this.uploadUrlButtonElement.style.left=toPx(parseInt(fromPx(this.uploadFileButtonElement.style.left)) + 2);
	} else {
		this.uploadUrlButtonElement.style.left=toPx(this.width + 2);;
	}
 
  this.inputElement.style.width=toPx(this.width);
  this.inputElement.style.height=toPx(this.height);
  this.element.style.width=toPx(this.width);
  this.element.style.height=toPx(this.height);
};
FileUploadField.prototype.setButtonsText=function(uploadFileButtonText,uploadUrlButtonText){
  this.uploadFileButtonText=uploadFileButtonText;
  this.uploadUrlButtonText=uploadUrlButtonText;
  this.buildDiv();
};
FileUploadField.prototype.setValue=function(value){
  if(value==null)//IE Hack
    this.inputElement.value="";
  else
    this.inputElement.innerHTML=value;
  if(value != null && value != "")
	this.inputElement.classList.add("fileSelected");
  else{
	if(this.inputElement.classList.contains("fileSelected"))
		this.inputElement.classList.remove("fileSelected");
  }
	  
}
FileUploadField.prototype.getValue=function(){
  return this.inputElement.value;
};

FileUploadField.prototype.onUrlButton=function(){
    this.portlet.onChange(this.id,{action:'urlClicked'});
}

////////////////////////////////////////////////////////////////////////////////
// ToggleButtonsField
////////////////////////////////////////////////////////////////////////////////
ToggleButtonsField.prototype=new FormField();

function ToggleButtonsField(portlet, id, title){
	FormField.call(this, portlet, id, title);
	this.element=nw("div");
	this.options=[];
	this.cssStyle={};
	this.buttonCssStyle={};
	this.onCssStyle={};
	this.offCssStyle={};
	
	this.mode="S";
	this._class = "form_field_toggle_buttons";
	this.btn_class = "form_field_toggle_button";
	this.btnOn_class = "form_field_toggle_button_on";
	this.btnOff_class = "form_field_toggle_button_off";
	
}

ToggleButtonsField.prototype.options;
ToggleButtonsField.prototype._class;
ToggleButtonsField.prototype.btn_class;
ToggleButtonsField.prototype.btnOn_class;
ToggleButtonsField.prototype.btnOff_class;
ToggleButtonsField.prototype.cssStyle;
ToggleButtonsField.prototype.buttonCssStyle;
ToggleButtonsField.prototype.onCssStyle;
ToggleButtonsField.prototype.offCssStyle;

ToggleButtonsField.prototype.setFieldStyle=function(style){
  this.element.className="";
  applyStyle(this.element,style);
}
ToggleButtonsField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.element.style.pointerEvents= disabled? "none":"auto";
	if(this.disabled){
		for(var i in this.options)
			this.options[i].element.classList.add("form_field_toggle_disabled");
	}else{
		for(var i in this.options)
			this.options[i].element.classList.remove("form_field_toggle_disabled");
	}
}

ToggleButtonsField.prototype.clear=function(){
	if(this.mode === "S"){
		for(var i = 0; i < this.options.length; i++){
			this.element.removeChild(this.options[i].element);
		}
	}
	else if (this.mode === "T"){
		this.element.removeChild(this.options[0].element);
	}
	this.options=[];
}

ToggleButtonsField.prototype.init=function(){
	var that=this;
	this.element.classList.add(this._class);
	var btn_on = null;
	if(this.mode === "S"){
		for(var i = 0; i < this.options.length; i++){
		    if(this.options[i].element!=null)
			    this.element.removeChild(this.options[i].element);
		}
	}
	else if (this.mode === "T"){
		if(this.options[0].element!=null)
		    this.element.removeChild(this.options[0].element);
	}
	
	if(this.mode === "S"){
		this.buttonOn=null;
		for(var i = 0; i < this.options.length; i++){
			var btn = nw("button");
			btn._index = i;
			btn.textContent = this.options[i].text;
			btn.classList.add(this.btn_class);
			btn.classList.add(this.btnOff_class);
			if(this.disabled)
				btn.classList.add("form_field_toggle_disabled");
			
			btn.onclick= function(e){
				toggleButtonsFieldToggle(that, e);
			};
			
			this.options[i].state = false;
			this.options[i].element = btn;
			this.element.appendChild(btn);
		}
	}
	else if(this.mode === "T"){
		if(this.options.length == 0){
			return;
		}
		var btn = nw("button");
		btn._index = 0;
		btn.textContent = this.options[0].text;
		btn.classList.add(this.btn_class);
		btn.classList.add(this.btnOff_class);
		btn.onclick= function(e){
			toggleButtonsFieldToggle(that, e);
		};
		this.options[0].state = true;
		this.options[0].element = btn;
		this.element.appendChild(btn);
		for(var i = 1; i < this.options.length; i++){
			this.options[i].element = btn;
			this.options[i].state = false;
		}
		this.buttonOn = 0;
	}
}

ToggleButtonsField.prototype.addOption=function(key, name, cssStyle){
	var index = this.options.length;
	this.options.push({"key":parseInt(key), "text":name});
	if(cssStyle != null){
		this.setCssStyleAtIndex(cssStyle, index);
	}
}
ToggleButtonsField.prototype.fireOnChange=function(){
	this.portlet.onChange(this.id,{value: this.options[this.buttonOn].key});
}

ToggleButtonsField.prototype.setMode=function(m){
	this.mode = m;
}

ToggleButtonsField.prototype.setValue=function(value){
	value = parseInt(value);
	if(this.mode === "S"){
		if(!this.buttonOn || (this.buttonOn && this.options[this.buttonOn].key !== value)){
			for(var i=0; i < this.options.length; i++){
				if(value === this.options[i].key){
					toggleButtonsFieldToggle(this, {"target":this.options[i].element}, true);
				}
			}
		}
	}
	else if(this.mode === "T"){
		if(this.buttonOn != value && value < this.options.length){
			var that = this;
			var button = that.options[0].element;
			if(value == 0){
				button.classList.remove(that.btnOn_class);
				button.classList.add(that.btnOff_class);
			}else{
				button.classList.remove(that.btnOff_class);
				button.classList.add(that.btnOn_class);
			}
			that.options[value].state = true;
			if(that.buttonOn != null)
				that.options[that.buttonOn].state = false;
			button.textContent = that.options[value].text;
			that.buttonOn = value;
			that.applyStyle(null,null);
		}
	}
}
function toggleButtonsFieldToggle(that, e, noFire) {
	var buttonIndex = e.target._index;
	if (that.mode === "S") {
		if (buttonIndex != that.buttonOn || that.buttonOn == null) {
			if (that.options && that.options[buttonIndex] && that.options[buttonIndex].element) {
				if (that.options != null && that.buttonOn!=null && that.options[that.buttonOn] && that.options[that.buttonOn].element) {
					var button = that.options[that.buttonOn].element;
					button.classList.remove(that.btnOn_class);
					button.classList.add(that.btnOff_class);
					that.options[that.buttonOn].state = false;
					that.applyButtonStyle(button, that.offCssStyle);
				}
				var button = that.options[buttonIndex].element;
				button.classList.remove(that.btnOff_class);
				button.classList.add(that.btnOn_class);
				that.options[buttonIndex].state = true;
				that.buttonOn = buttonIndex;
				
				that.applyButtonStyle(button, that.onCssStyle);
				if(noFire != true)
					that.fireOnChange();
			}
		}
	} else if (that.mode === "T") {
		if (that.options && that.options[buttonIndex] && that.options[0].element) {
			if(that.buttonOn == null)
				that.buttonOn = 0;
			var nextIndex = (that.buttonOn+1) < that.options.length? that.buttonOn+1 : 0;
			
			var button = that.options[0].element;
			if(nextIndex == 0){
				button.classList.remove(that.btnOn_class);
				button.classList.add(that.btnOff_class);
			}else if(that.buttonOn == 0){
				button.classList.remove(that.btnOff_class);
				button.classList.add(that.btnOn_class);
			}
			button._index = nextIndex;
			that.options[that.buttonOn].state = false;
			that.options[nextIndex].state = true;
			button.textContent = that.options[nextIndex].text;
			that.buttonOn = nextIndex;
			that.applyStyle(null, null);
				
			if(noFire != true)
				that.fireOnChange();
		}
	}
}

ToggleButtonsField.prototype.onFieldSizeChanged=function(){
	this.cssStyle.width = toPx(this.width);
	this.cssStyle.height = toPx(this.height);
	this.applyStyle(null, null);
}
ToggleButtonsField.prototype.setSpacing=function(spacing){
	this.spacing = toPx(spacing);
}
ToggleButtonsField.prototype.setMinButtonWidth=function(minButtonWidth){
	this.buttonCssStyle.minWidth = toPx(minButtonWidth);
}

ToggleButtonsField.prototype.applyStyle=function(style, buttonStyle){
	if(style != null){
		applyStyle(this.element, style);
	}
	
	var len = this.options.length; 
	
	if(this.mode === "S"){
		if(buttonStyle !=null){
			for(var i = 0; i < len; i++){
				if(this.options[i].element)
					applyStyle(this.options[i].element, buttonStyle);
			}
		}
		
		Object.assign(this.element.style, this.cssStyle);
		
		for(var i = 0; i < len; i++){
			if(this.options[i].element){
				Object.assign(this.options[i].element.style, this.buttonCssStyle);
				if(this.options[i].state){
					this.applyButtonStyle(this.options[i].element, this.onCssStyle);
				}
				else{
					this.applyButtonStyle(this.options[i].element, this.offCssStyle);
				}
				if(this.options[i].style != null)
					this.applyButtonStyle(this.options[i].element, this.options[i].style);
				this.options[i].element.style.height = this.cssStyle.height;
				if(i!=0){
					this.options[i].element.style.marginLeft = this.spacing;
				}
			}
		}
	}
	else if(this.mode === "T"){
		if(buttonStyle !=null){
			if(this.options[0].element)
				applyStyle(this.options[0].element, buttonStyle);
		}
		Object.assign(this.element.style, this.cssStyle);
		if(this.options[0].element){
			Object.assign(this.options[0].element.style, this.buttonCssStyle);
			if(this.buttonOn != 0){
				this.applyButtonStyle(this.options[0].element, this.onCssStyle);
			}
			else{
				this.applyButtonStyle(this.options[0].element, this.offCssStyle);
			}
			if(this.options[this.buttonOn].style != null)
				this.applyButtonStyle(this.options[0].element, this.options[this.buttonOn].style);
			this.options[0].element.style.height = this.cssStyle.height;
		}
	}
}

ToggleButtonsField.prototype.applyButtonStyle=function(button,style){
	Object.assign(button.style, style);
}

ToggleButtonsField.prototype.setCssStyleAtIndex=function(style, index){
	var dummyElement = {style:{}, className:""};
	applyStyle(dummyElement, style);
	
	if(this.options[index].style == null)
		this.options[index].style = {};
	Object.assign(this.options[index].style, dummyElement.style);
}

ToggleButtonsField.prototype.setCssStyle=function(style){
	var dummyElement = {style:{}, className:""};
	applyStyle(dummyElement, style);
	Object.assign(this.cssStyle, dummyElement.style);
}

ToggleButtonsField.prototype.setButtonCssStyle=function(style){
	var dummyElement = {style:{}, className:""};
	applyStyle(dummyElement, style);
	Object.assign(this.buttonCssStyle, dummyElement.style);
}
ToggleButtonsField.prototype.setOnCssStyle=function(style){
	var dummyElement = {style:{}, className:""};
	applyStyle(dummyElement, style);
	Object.assign(this.onCssStyle, dummyElement.style);
}
ToggleButtonsField.prototype.setOffCssStyle=function(style){
	var dummyElement = {style:{}, className:""};
	applyStyle(dummyElement, style);
	Object.assign(this.offCssStyle, dummyElement.style);
}

////////////////////////////////////////////////////////////////////////////////
// MultiCheckboxField
////////////////////////////////////////////////////////////////////////////////

MultiCheckboxField.prototype=new FormField();
function MultiCheckboxField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	this.disabled = false;
    this.element=nw('div', "multicheckbox");
    this.element.style.position='relative';
    this.element.tabIndex=1;
    this.clearElement = nw('div', "multicheckbox_clear");
    this.clearElement.innerText="\u00D7";
    
    this.input = this.element;
    
	this.options=new Map();
    this.checked = new Array();
    // for storing unconfirmed selections (menu still open)
    this.pending = new Array();
    
    var that= this;
    this.element.tabIndex=-1;
    this.element.onclick=function(e){
    	that.onFieldEvent(e,"onFocus");
    	that.show();};
    this.element.onfocus=function(e){ if(that.menu==null ) that.onFieldEvent(e,"onFocus"); };
    this.element.onblur=function(e){ if(that.menu==null ) that.onFieldEvent(e,"onBlur"); };
    	
    this.clearElement.onclick=function(e){that.clearSelected();e.stopPropagation();};
};


MultiCheckboxField.prototype.clearSelected=function(){
	this.checked.length = 0;
	this.fireOnChange();
};

MultiCheckboxField.prototype.validatePending=function(){
	// rebuild checked 
	this.checked.length = 0;
	//validate and move pending to checked
	for (var j=0; j < this.pending.length; j++) {
		var val = this.options.get(this.pending[j]);
		if (val) {
			this.checked.push(this.pending[j]);
		}
	}
	this.pending.length = 0;
}

MultiCheckboxField.prototype.__updateInputBox=function(){
	this.element.innerText="";

	var cntmore = 0;
	var i = 0;
	
	var checkedVal;
	var totalWidth = 0;
	var availableWidth = this.element.clientWidth; 
	if (this.checked.length > 0) {
		availableWidth -= 12;// - clearElement length
	}
	for(; i < this.checked.length; i++){
		var val = this.options.get(this.checked[i]);
		checkedVal = nw('div', "multicheckeditem");
		checkedVal.innerText = val;
		const cs=getComputedStyle(checkedVal);
		this.element.appendChild(checkedVal);
		totalWidth += checkedVal.offsetWidth + parseInt(cs.marginRight);
		// if it doesn't fit;
		if(availableWidth < totalWidth){
			cntmore = this.checked.length - i;
			// checkedVal width will change, so reapply new width
			totalWidth -= (checkedVal.offsetWidth + parseInt(cs.marginRight));
			checkedVal.innerText = "" + (cntmore) + ((cntmore==this.checked.length)?" items" :" more") ;
			totalWidth += checkedVal.offsetWidth + parseInt(cs.marginRight);
			i--;
			break;
		}
	}
	while(i >= 0 && (availableWidth < totalWidth)){
		cntmore++;
		var el = this.element.children[i];
		const cs=getComputedStyle(el);
		checkedVal.innerText = "" + (cntmore) + ((cntmore==this.checked.length)?" items" :" more") ;
		totalWidth -= (el.offsetWidth + parseInt(cs.marginRight));
		this.element.removeChild(el);
		i--;
	}
	if(this.checked.length > 0)
		this.element.appendChild(this.clearElement);
};

// triggered from closing menu
MultiCheckboxField.prototype.fireOnChange=function(){
	this.validatePending();
	this.__updateInputBox();
	if(this.disabled != true){
		this.portlet.onChange(this.id,{value: this.checked.join(",") });
	}
}

MultiCheckboxField.prototype.show=function(){
	if(currentContextMenu)
		currentContextMenu.hideAll();
	if (!this.options.size) {
		return;
	}
	var that= this;
	var r = new Rect();
	r.readFromElementRelatedToWindow(this.element);
	this.menu = new Menu(getWindow(this.element));
	var menu=this.menu;
	menu.setSize(this.width, this.height);
	menu.setCssStyle("_cna=multicheckbox_menulist");
	menu.createMenu(this.createMenuJson(), function(e, action){});
	menu.show(new Point(r.left,r.top+this.height), true);
	menu.setCssStyle(this.style);
	menu.bypass=true;
	menu.onHide=function(){
		  that.fireOnChange();
		that.menu=null;
		};
	menu.onGlass=function(e){
		if(!isMouseInside(e,that.element,0)){
		  that.onFieldEvent(null,"onBlur");
		} else {
			  that.element.focus();
		}
		that.menu=null;
	}
	menu.customHandleKeydown = function(e){
		switch (e.key) {
		case "Enter":
			menu.hideAll();
			break;
		case "Tab":
			if (currentContextMenu) {
				currentContextMenu.hideAll();
				that.handleTab(null,e);	
			}
			// handle tabbing
			portletManager.onUserSpecialKey(e);
			break;
		case " ": 
			this.runMenuItemAction(e);
			break;
		default:
			menu.handleKeydown(e);
			break;
		}
	};

}

MultiCheckboxField.prototype.isChecked=function(option){
	var idx = this.checked.indexOf(option);
	return (idx!=-1);
};

MultiCheckboxField.prototype.toggleSelectedCheckBox=function(){
	if(currentContextMenu){
		var selected = currentContextMenu.getSelectedMenu();
		if(selected == null)
			return;
		var checkbox = selected.getElementsByTagName("input")[0];
		var checked = checkbox.checked= !checkbox.checked;
		
		if(checked==true){
			this.pending.push(checkbox.name);
		}
		else{
			var idx = this.pending.indexOf(checkbox.name);
			if(idx != -1){
				this.pending.splice(idx,1);
			}
		}
	}
}

MultiCheckboxField.prototype.createMenuJson=function(){
	var menu = {};
	menu.children = [];
	menu.enabled = true;
	menu.text="";
	menu.type="menu";
	menu.style = "_cna=test";

	var i = 0;
	var that = this;
	var fieldId = this.id;
	var portletId = this.portlet.getId();
	var jsGetField = 'g("'+portletId+'").getField("'+fieldId+'")';
	for(var [option, val] of this.options){
		var item = {};
		var checked=this.isChecked(option);
		if (checked) {
			this.pending.push(option);
		}
		item.text = "<input type='checkbox' "+ (checked?"checked":"") +" name='"+ option +"' >" + escapeHtml(val) +"";
		item.type = "action";
		item.enabled = "true";
		item.noIcon = true;
		item.autoclose = false;
		item.onclickJs = jsGetField+'.toggleSelectedCheckBox();';
		item.style = "_cna=multicheckbox_menuitem"
		menu.children[i] = item;
		i++;
	}
	

	return menu;
}

MultiCheckboxField.prototype.setFieldStyle=function(style){
	this.style = style;
	applyStyle(this.element,style);
	applyStyle(this.clearElement,style);
}

MultiCheckboxField.prototype.setClearElementColor=function(color){
	this.clearElement.style.color=color;
}

MultiCheckboxField.prototype.focusField=function(){
	this.element.focus();
	this.show();
    return true;
};


MultiCheckboxField.prototype.onFieldSizeChanged=function(){
	this.__updateInputBox();
}
	
MultiCheckboxField.prototype.setValue=function(value){
	this.checked.length = 0;
	if(value != "")
		Array.prototype.push.apply(this.checked, value.split(','));
	this.__updateInputBox();
};

MultiCheckboxField.prototype.addOption=function(value,text,isSelected){
	this.options.set(value, text);
	if(isSelected == true){
		this.checked.push(value);
	}
};

MultiCheckboxField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	if(this.disabled == true){
		this.element.style.pointerEvents="none";
	}
	else{
		this.element.style.pointerEvents="auto";
	}
}




MultiCheckboxField.prototype.clear=function(){
	this.checked.length = 0;
	this.options.clear();
};

////////////////////////////////////////////////////////////////////////////////
// SelectField
////////////////////////////////////////////////////////////////////////////////
SelectField.prototype=new FormField();

function SelectField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	this.options={};
	// the select element
    this.element=nw('select');
    
    
    var that=this;
    this.element.style.position='relative';
    // this.select is wrapper for element
    this.select=new Select(this.element);
    this.select.element.style.position='relative';
    this.select.element.style.left='0px';
    this.select.element.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
    this.select.element.onblur=function(e){that.onFieldEvent(e,"onBlur");};

    this.select.element.onmousedown=function(e){
    	if(getMouseButton(e)==2)
    		that.portlet.onUserContextMenu(e,that.id,-1);
	};
    this.select.element.onkeydown=function(e){
    	if(e.keyCode==9)
    		return that.form.onTab(that,e);
	};
	
    this.element.onchange=function(){
    	that.fireOnChange('change');
    };
    this.element.ondblclick=function(){that.fireOnChange('dblclick');};
    this.selectedValuesText=null;
};


SelectField.prototype.setAttr=function(){
	this.element.setAttribute('id', 'customScroll');
}

SelectField.prototype.setTextAlign=function(dir){
	if(dir)
	  this.select.element.dir=dir;
}
SelectField.prototype.setFieldStyle=function(style){
	this.select.element.className="";
	applyStyle(this.element,style);
}
SelectField.prototype.focusField=function(){
    this.select.element.focus();
    return true;
};

SelectField.prototype.fireOnChange=function(action){
	  var selectedValuesText=this.select.getSelectedValuesDelimited(',');
	  if(selectedValuesText==this.selectedValuesText && action!='dblclick')
		  return;
	  this.selectedValuesText=selectedValuesText;
	  this.portlet.onChange(this.id,{action:action,value:this.select.getSelectedValuesDelimited(',')});
}
SelectField.prototype.setScrollbarRadius=function(){
	this.element.style.setProperty("--scrollbar-radius", "15px");
}

SelectField.prototype.onFieldSizeChanged=function(){
  var h=toPx(this.height);
  if(this.button){
	 var t=toPx(this.width-this.height);
    this.select.element.style.width=t;
    this.button.style.left=t;
    this.button.style.width=h;
    this.button.style.height=h;
  }else{
    this.select.element.style.width=toPx(this.width);
  }
  this.select.element.style.height=h;
}
	
SelectField.prototype.setValue=function(value){
 		this.select.clearSelected();
  		this.select.setSelectedValueDelimited(value,',');
  		this.selectedValuesText=null;
};
SelectField.prototype.autoComplete=function(flags, pre, post, input, list){
	var out = [];
	var re = new RegExp(pre+input+post, flags);
	for(var i = 0; i < this.options.length; i++){
		if(typeof this.options[i] != 'undefined' && re.test(this.options[i]))
			out.push(i);
	}
	return out;
}

// not used
SelectField.prototype.addOptions=function(listOptions, listKeysToAdd){
	this.selectedAcItem = -1;
	this.prevSelectedAcItem = -1;
	this.list.innerHTML='';
	this.listSelectItems=[];
	this.firstSelect=-1;
	this.lastSelect=-1;
	if(typeof listKeysToAdd == 'undefined'){
		for(var i = 0; i < listOptions.length; i++){
			if(typeof listOptions[i] != 'undefined'){
				var text = listOptions[i];
			
				var item = nw("div", "selectfield_ac_item");
				item.textContent = text;
				item._value = i;
				this.list.appendChild(item);
				this.lastSelect++;
				this.listSelectItems[this.lastSelect]=item;
				item.acIndex = this.lastSelect;
			}
		}
		if(this.lastSelect != -1)
			this.firstSelect = 0;
	}
	else{
		for(var i = 0; i < listKeysToAdd.length; i++){
			var value = listKeysToAdd[i];
			var text = listOptions[value];
			
			var item = nw("div", "selectfield_ac_item");
			item.textContent = text;
			item._value = value;
			this.list.appendChild(item);
			this.lastSelect++;
			this.listSelectItems[this.lastSelect]=item;
			item.acIndex = this.lastSelect;
		}
		if(this.lastSelect != -1)
			this.firstSelect = 0;
	}
}
SelectField.prototype.addOption=function(value,text,style,isSelected){
  this.options[value] = text;
  var option=this.select.addOption(value,text,isSelected);
  applyStyle(option,style);
  this.selectedValuesText=null;
};

SelectField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.select.setDisabled(disabled);
	if(this.button != null){
		if(disabled == true){
			this.button.style.pointerEvents="none";
			this.button.style.visibility="hidden";
			this.element.style.background=disabled ? '#eaeaea' : '';
			this.element.style.color=disabled ? '#666666' : '';
		}
		else{
			this.button.style.pointerEvents="initial";
			this.button.style.visibility="initial";
		}
	}
}



SelectField.prototype.ensureSelectedVisible=function(){
	this.select.ensureSelectedVisible();
}
SelectField.prototype.setMulti=function(multiple){
	this.multiple=multiple;
  if(multiple){
    this.element.multiple='multiple';
    this.element.style.width="100%";
    this.selectedValuesText='';
	var that=this;
  }else
    this.element.multiple='';
};

SelectField.prototype.setScrollbarGripColor=function(color){
	this.element.style.setProperty('--scrollbar-gripcolor', color); 
}

SelectField.prototype.setScrollbarTrackColor=function(color){
	this.element.style.setProperty('--scrollbar-trackcolor', color); 
}
SelectField.prototype.clear=function(){
  this.select.clear();
};

// not used
SelectField.prototype.handleKeydown=function(e){
	//err(e);
	if(e.keyCode==40){//Down
		if(typeof this.listSelectItems == 'undefined')
			return;	
		if(this.selectedAcItem != -1)
			this.prevSelectedAcItem = this.selectedAcItem;
		else
			this.prevSelectedAcItem = -1;
		if(this.selectedAcItem == -1)
			this.selectedAcItem = this.firstSelect;
		else if(this.selectedAcItem == this.lastSelect)
			this.selectedAcItem = this.firstSelect;
		else
			this.selectedAcItem++;
		this.setSelectedOption();
		
	}
	else if (e.keyCode==38){//Up
		if(typeof this.listSelectItems == 'undefined')
			return;	
		if(this.selectedAcItem != -1)
			this.prevSelectedAcItem = this.selectedAcItem;
		else
			this.prevSelectedAcItem = -1;
		if(this.selectedAcItem == -1)
			this.selectedAcItem = this.lastSelect;
		else if(this.selectedAcItem == this.firstSelect)
			this.selectedAcItem = this.lastSelect;
		else
			this.selectedAcItem--;
		this.setSelectedOption();
	}
	else if (e.keyCode==13){//Enter
		e.preventDefault();
		if(this.selectedAcItem > -1){
			var option = this.listSelectItems[this.selectedAcItem];
			var key = option._value;
			var text = this.options[key];
			this.input.value = text;
			this.list.style.display="none";
			this.selectedOption = key;
			this.fireOnChange();
			this.input.blur();
		}
	}
}
SelectField.prototype.handleOnmousedown=function(e){
	if(e.target == this.list)
		return;
	if(this.selectedAcItem > -1){
		var option = this.listSelectItems[this.selectedAcItem];
		var key = option._value;
		var text = this.options[key];
		this.input.value = text;
		this.list.style.display="none";
		this.selectedOption = key;
		this.fireOnChange();
		this.input.blur();
	}
}
SelectField.prototype.handleOnclick=function(e){
	if(typeof this.listSelectItems == 'undefined')
		this.addOptions(this.options, this.autoComplete("yi","","","", this.options));
//		this.addOptions(this.options, this.autoComplete("yi","","",this.input.value, this.options));
}
SelectField.prototype.handleKeyup=function(e){
	if(e.keyCode==40){//Down
	}
	else if (e.keyCode==38){//Up
	}
	else
		this.addOptions(this.options, this.autoComplete("yi","","",this.input.value, this.options));
	
}

SelectField.prototype.setSelectedOption=function(){
	if(this.prevSelectedAcItem != -1){
		var option = this.listSelectItems[this.prevSelectedAcItem];
		option.classList.remove("selectfield_ac_item_active");
	}
	if(this.selectedAcItem != -1){
		var option = this.listSelectItems[this.selectedAcItem];
		option.classList.add("selectfield_ac_item_active");
		
		//option.scrollIntoView();
		//err(option);
	}
	

}

SelectField.prototype.handleMouseMove=function(e){
	if(typeof this.listSelectItems == 'undefined')
		return;	
	if(typeof e.target.acIndex == 'undefined')
		return;
	if(this.selectedAcItem != -1)
		this.prevSelectedAcItem = this.selectedAcItem;
	else
		this.prevSelectedAcItem = -1;
	this.selectedAcItem = e.target.acIndex;
	this.setSelectedOption();
}

SelectField.prototype.handleWheel=function(e){

}
SelectField.prototype.showList=function(e){
	var that = this;
	that.list.style.display = null;
	that.list.style.bottom=null;
	var doc = getDocument(that.list);
	var rect = that.list.getBoundingClientRect();
	var windowBottom = getWindow(that.list).outerHeight;
	if(rect.bottom > windowBottom){
		//err(that.height);
		//err(rect);
		var newB = (rect.height - that.height);
		//err(newB);
		that.list.style.bottom=toPx(newB);
	}
	disableWheel=true;
}
SelectField.prototype.hideList=function(e){
	var that = this;
	that.list.style.display="none";
	disableWheel=false;
}

// not used
SelectField.prototype.init=function(hasButton){
  var that = this;
  if(hasButton && this.button == null){
	  var that=this;
     this.button=nw('div','selectfield_plusbutton');
     this.button.style.position='relative';
     this.button.onmousedown=function(e){that.portlet.onUserContextMenu(e,that.id, -1);};
     this.element.parentElement.appendChild(this.button);
  }else if(!hasButton && this.button!=null){
     this.element.parentElement.removeChild(this.button);
     this.button=null;
  }
}


////////////////////////////////////////////////////////////////////////////////
// PortletSelectField
////////////////////////////////////////////////////////////////////////////////
PortletSelectField.prototype=new FormField();

function PortletSelectField(portlet,id,title){
	FormField.call(this,portlet,id,title);
    this.element=nw('p','portlet_select_value');
	var that=this;
    this.element.onclick=function(){
    	that.portlet.onChange(that.id,{click:true});
        onSelectChild(function(id){that.onTargetSelected(id)});
    };
}

PortletSelectField.prototype.onTargetSelected=function(id){
  var that=this;
  that.portlet.onChange(that.id,{value:id});
}
PortletSelectField.prototype.setValue=function(value){
  this.element.innerHTML=value;
};
PortletSelectField.prototype.getValue=function(){
  return this.element.innerHTML;
};


////////////////////////////////////////////////////////////////////////////////
// DivField
////////////////////////////////////////////////////////////////////////////////
DivField.prototype=new FormField();

function DivField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	this.element=nw('div', 'divFieldElement');
	this.element.style.position="relative";
	this.element.style.display="inline-block";
}
DivField.prototype.setValue=function(value){
	this.element.innerHTML=value;
}
DivField.prototype.getValue=function(){
	return this.element.innerHTML;
}
DivField.prototype.setFieldStyle=function(style){
  this.element.className="divFieldElement";
  applyStyle(this.element,style);
}
DivField.prototype.onFieldSizeChanged=function(){
	this.element.style.width=toPx(this.width);
	this.element.style.height=toPx(this.height);
//	this.element.style.width="100%";
//	this.element.style.height="100%";
}

function FieldExtension(field, extensionIndex){
	this.extensionIndex = extensionIndex;
	this.field = field;
	if(field != null)
		this.field.addExtensionAt(this, this.extensionIndex);
}
FieldExtension.prototype.onFieldReposition=function(){
}

FieldExtension.prototype.callBack=function(action, values){
	this.field.portlet.onFieldExtensionCallBack(this.field.id, this.extensionIndex, action, values);
}


FieldAutocompleteExtension.prototype=new FieldExtension();

function FieldAutocompleteExtension(field, extensionIndex){
	FieldExtension.call(this,field, extensionIndex);
	var that = this;
	this.visibleSugLen = 20;
	this.entries = new Array();//caches all entries that are being viewed or were viewed
	this.rowHeight = 14;
	this.extensionMenu = null;
	this.field.input.onclick=function(e){ 
		that.field.onExtensionCallback(0, "onOpen", {} );
	};
	
}


FieldAutocompleteExtension.prototype.s=function(size){
	this.totalSuggestionSize = size;
}
FieldAutocompleteExtension.prototype.a=function(index, value){
	
	eee = this;
	let len = this.entries.length;
	if(len == index){
		this.entries.push(value);
	}
	else if(index > len){
		this.entries[index]= value;
	}
	else {
		this.entries.splice(index, 0, value);
}
}
FieldAutocompleteExtension.prototype.u=function(index, value){
	this.entries[index]= value;
}
FieldAutocompleteExtension.prototype.d=function(index){
	this.entries.splice(index, 1);
}
FieldAutocompleteExtension.prototype.hide=function(){
	if(currentContextMenu)
		currentContextMenu.hideAll(); 
}
FieldAutocompleteExtension.prototype.clear=function(){
 	this.entries = new Array();
 	this.extensionMenu = null;
}
FieldAutocompleteExtension.prototype.onFieldReposition=function(){
}
FieldAutocompleteExtension.prototype.createMenuJson=function(lower, upper){ 
	// Lower here is less than upper
	var menu = {};
	if (this.entries.length == 0)
		return menu;
	menu.children = [];
	menu.enabled = true;
	menu.text="";
	menu.type="menu"
	menu.style = "_cna=test"
	var i=0;
	arguments

	var l = lower;
	var u = upper;
	if(typeof lower == 'undefined')	{
		if(typeof this.extensionMenu.upperRow == 'undefined') //extensionMenu.upper is less than extensionMenu.lower
			err("undefined row");
		l = this.extensionMenu.upperRow;
		u = this.extensionMenu.lowerRow;
	}
	
	//for (const [key, value] of this.suggestions.entries()){
	for(var key2 = l; key2 <= u; key2++){
		var item = {};
		//item.action= value;
		
		item.text = this.entries[key2];
		//item.text = htmlToText(this.entries[key2]);

		item.action= key2;
		
		item.type ="action";
		item.enabled = "true";
		item.noIcon = true;
		item.style = "_cna=selectfield_ac_item"
		menu.children[i] = item;
		i++;
	}
	return menu;
}
FieldAutocompleteExtension.prototype.select=function(i){
	// Force it to send the whole value
	//this.field.setValue(null);
	//this.field.setValueAndFireSelect(i);
	this.field.onExtensionCallback(0, "onSelect", {sel: i});
	//call onclose after selection
	this.field.onExtensionCallback(0, "onClose", new Object());
	this.field.onFieldEvent(null, "onAutocompleted");
}

FieldAutocompleteExtension.prototype.updateSuggestions=function(){
	//this.suggestionsArray = this.entries.slice(this.extensionMenu.upperRow, this.extensionMenu.lowerRow+1);	
	//this.suggestions = new Map();
	//for(val of this.suggestionsArray){
	//	this.suggestions.set(val, htmlToText(val) )
	//}
	//*****************
}

FieldAutocompleteExtension.prototype.updateMenu=function(updateSelection, startOrEndOfSuggestion){
	var menuJson = this.createMenuJson();//this is the new menu json
	//truncate the this.extensionMenu.tableElement, remove all rows(children nodes)
	this.extensionMenu.tableElement.innerHTML ='';
	var that=this;
	this.extensionMenu.createMenu(menuJson, function(e, action){that.select(action);});
	this.extensionMenu.setTableRowHeight(this.rowHeight);
	//TODO: update left position if there is horizontal scrollbar
	//	if(this.extensionMenu.scrollPane.hscrollVisible)
	//		this.extensionMenu.updateCellsLocations();
	const START_OF_SUGGESTION = 1;
	const END_OF_SUGGESTION = 2;
	const MIDDLE_OF_SUGGESTION = 3;
	if(updateSelection){
		var m = this.extensionMenu;
		var visibleRows = m.tableElement.children;
			switch(startOrEndOfSuggestion){
			case START_OF_SUGGESTION:
				m.selected = this.visibleSugLen - 1;
				if(visibleRows[m.selected])
					m.highlight(visibleRows[m.selected], true);
				break;
			case END_OF_SUGGESTION:
				m.selected = 0;
				if(visibleRows[m.selected])
					m.highlight(visibleRows[m.selected], true);
				break;
			case MIDDLE_OF_SUGGESTION:
				if(visibleRows[m.selected])
					m.highlight(visibleRows[m.selected], true);
				break;
		   }
	}
}

FieldAutocompleteExtension.prototype.setRowSelected=function(idx){
	var m = this.extensionMenu;
	m.selected=idx;
	//m.tableElement.children are the menu items that are visible in view 
	m.highlight(m.tableElement.children[m.selected], true);
}

FieldAutocompleteExtension.prototype.updateMenuDelta=function(){
	var menuJson = this.createMenuJson();
}


FieldAutocompleteExtension.prototype.show=function(lower,upper){
	//reset the upperrow and lowerrow if extensionMenu exists
	if(this.extensionMenu){
		this.extensionMenu.upperRow = 0;
		this.extensionMenu.lowerRow = this.visibleSugLen - 1;
	}
	if(currentContextMenu)
		currentContextMenu.hideAll();
	var menuJson = this.createMenuJson(lower, upper);
	if(menuJson == null || menuJson.children == null)
		return;
	if(menuJson.children.length == 0)
		return;
	var that= this;
	var r = new Rect();
	r.readFromElementRelatedToWindow(this.field.element);
	var menu = new Menu(getWindow(this.field.element));
	menu.setSize(this.field.width, this.field.height);
	menu.setCssStyle("_cna=selectfield_ac_list");
	menu.createMenu(menuJson, function(e, action){that.select(action);});
	if(this.totalSuggestionSize > this.visibleSugLen){
		this.addScroll(menu);
		menu.divElement.style.setProperty('max-width', 'none','important');//override max-width:600px from menu css style
	}
	this.extensionMenu = menu;
	this.extensionMenu.totalSuggestionSize = this.totalSuggestionSize;
	this.extensionMenu.upperRow = 0;
	this.extensionMenu.lowerRow = this.visibleSugLen - 1;
	menu.show(new Point(r.left,r.top+this.field.height), true);
	if(this.totalSuggestionSize > this.visibleSugLen)
		menu.divElement.style.setProperty('overflow-x', 'hidden','important');
	menu.bypass=true;
	menu.customHandleKeydown = function(e){
		menu.handleKeydown(e);
		that.field.onKeyDown(e);
		if(e.keyCode==13 || e.keyCode == 9){
			menu.hideAll();
		}
		if (e.key == 9) // handle tabbing
			portletManager.onUserSpecialKey(e);
	};
	
}
FieldAutocompleteExtension.prototype.addScroll = function(menu){
	//add scroll bar to the menu
	menu.setTableRowHeight(this.rowHeight);
	menu.addScrollPane();
	menu.setMenuBoxWidthHeight(this.field.width,menu.tableElement.rowHeight*this.visibleSugLen);
	menu.initScroll(this.totalSuggestionSize, menu.tableElement.rowHeight, this.field.width);
	menu.noAutoScrollbar();
	
	//onBoundChanged() cb
	menu.extension = this;
}

class ComboBox extends HTMLElement {
	constructor(){
		super();
		
		var that = this;
		this.options = new Map();
		this.suggestions = new Map();
		this.value="";
		this.minWidth = -1;
		this.setMinWidth=function(newMin){
			this.minWidth = newMin;
		};
		this.setValue=function(value){
			this.value = value;
			this.input.value = value;
		};
		this.addOptionDisplayAction=function(display, action){
			this.options.set(display, action);	
		};
		this.addOption=function(value){
			this.options.set(value, value);	
		};
		this.clearOptions2=function(){
			this.options=new Map();
			this.suggestions=new Map();
		};
		this.clearOptions=function(){
			this.options=new Map();
			this.suggestions=new Map();
			this.value=null;
		};
		this.blur=function(){
			this.input.blur();
			if(currentContextMenu)
				currentContextMenu.hideAll();
		};
		this.focus=function(){
			this.input.focus();
			this.autocomplete();
		};
		this.createMenuJson=function(){
			var menu = {};
			menu.children = [];
			menu.enabled = true;
			menu.text="";
			menu.type="menu"
			menu.style = "_cna=test"
			var i = 0;
			for (let [key, value] of this.suggestions) {
				var item = {};
				item.text = key;
				item.action= value;
				item.type ="action";
				item.enabled = "true";
				item.noIcon = true;
				item.style = "_cna=selectfield_ac_item"
				menu.children[i] = item;
				i++;
			}
			return menu;
		};
		this.handleKeydown=function(e){
			var shiftKey = e.shiftKey;
			var ctrlKey = e.ctrlKey;
			var altKey = e.altKey;
			if(shiftKey == true || ctrlKey == true || altKey == true || e.key=="Tab")
				return false;
			
			if(currentContextMenu){
				currentContextMenu.handleKeydown(e);
				if(e.key=="Enter"){
					this.blur();
				}
				return true;
			}
			else 
				return false;
		};
		this.select=function(i){
			//this.input.value = this.suggestions.get(i);
			//this.value = this.input.value;
			this.input.value = i;
			this.value = i;
			kmm.activePortletId = this.activePortletId;
			//this.input.focus();
		};
		
		this.customHandleKeydown = null;
		this.setCustomKeydownHandler = function(handler){
			this.customHandleKeydown = handler;
		};
		this.hideGlass=function(){
		};
		this.show=function(){
			if(currentContextMenu)
				currentContextMenu.hideAll();
			if(this.options == null)// || this.options.length == 0)
				return;
			var that= this;
			var r = new Rect();
			r.readFromElementRelatedToWindow(this.input);
			var menu = new Menu(getWindow(this.input));
			
			var width = this.input.offsetWidth;
			if(this.minWidth!=-1 && width < this.minWidth)
				width = this.minWidth;
				
			menu.setSize(width, this.input.offsetHeight);
			menu.setCssStyle("_cna=selectfield_ac_list");
			menu.createMenu(this.createMenuJson(), function(e, action){that.select(action);});
			menu.show(new Point(r.left,r.top+this.input.offsetHeight), true, null, that);
			menu.bypass = true;
			if(this.customHandleKeydown !=null)
				menu.setCustomKeydownHandler(this.customHandleKeydown);
			
			this.activePortletId = kmm.activePortletId;
		};
		this.autocomplete=function(key){
			if(currentContextMenu)
				currentContextMenu.hideAll();
		
			//No need for autocomplete
			this.suggestions=this.options;
			this.show();
			return;
		};
		
		const shadow = this.attachShadow({mode: 'open'});
		this.shadow = shadow
		const input = nw("input","combo-box");
		this.input = input;
		this.input.setAttribute('tabindex', 0);
		this.input.onclick=function(e){that.autocomplete();};
		//No need to autocomplete
//		this.input.onkeydown=function(e){err(e);if(currentContextMenu)currentContextMenu.hideAll();};
		this.input.onkeyup=function(e){that.value=that.input.value; };
		this.input.onkeypress=function(e){if(currentContextMenu) currentContextMenu.deselect();};
//		this.input.onkeyup=function(e){that.value=that.input.value; that.autocomplete(e.key);};
		
	    const style = document.createElement('style');
	
	    style.textContent = `
	    	.combo-box{
	    		box-sizing:border-box;
	    		border:none;
	    		width:100%;
	    		height:100%;
	    	}
	    	.combo-box:focus{
	    		outline:none!important;
	    	}
	    `;
	
	    // Attach the created elements to the shadow dom
	    shadow.appendChild(style);
		shadow.appendChild(this.input);
	}
}
customElements.define('combo-box', ComboBox);

////////////////////////////////////////////////////////////////////////////////
// ColorGradientField
////////////////////////////////////////////////////////////////////////////////
ColorGradientField.prototype=new FormField();

function ColorGradientField(portlet,id,title){
	
	FormField.call(this,portlet,id,title);
	this.createGradientChooser();
    this.element=this.colorPicker.element;//nw('canvas');
    this.element.style.border="1px solid black";
    this.element.style.cursor='pointer';
    this.element.style.position='absolute';
    this.element.style.left='0px';
    //this.context = this.element.getContext('2d');
	var that=this;
    this.noColorText = 'no color';
  this.colorPicker.element.onfocus=function(e){that.onFieldEvent(e,"onFocus");};
  this.colorPicker.element.onblur=function(e){that.onFieldEvent(e,"onBlur");};
  this.colorPicker.element.onkeydown=function(e){
      that.handleTab(null,e);
    };
}
ColorGradientField.prototype.setFieldStyle=function(style){
  this.element.className="";
  applyStyle(this.element,style);
}
ColorGradientField.prototype.init=function(allowNull,alpha, borderColor, borderRadius, borderWidth){
  this.allowNull=allowNull;
  this.alpha=alpha;
  if (borderWidth != null && borderWidth > 0) {
	  this.element.style.border="none";
	  this.colorPicker.canvas.style.border=toPx(borderWidth) + " solid " + borderColor;
  } else {
	  this.element.style.border="1px solid black";
	  this.colorPicker.canvas.style.border= "";
  }
  if (borderRadius != null)
	  this.colorPicker.canvas.style.borderRadius = toPx(borderRadius);
  else
	  this.colorPicker.canvas.style.borderRadius = "";
	  
  this.colorPicker.setAlphaEnabled(this.alpha);
};

ColorGradientField.prototype.setDisabled=function(disabled){
	this.element.style.pointerEvents = disabled?"none":"all";
}

ColorGradientField.prototype.createGradientChooser=function(){
	this.colorPicker=new GradientPicker(this.getValue(),0,100);//this.getValue(),0,100);
	var that=this;
	this.colorPicker.onGradientChanged=function(){that.onColorChanged()};
}

ColorGradientField.prototype.addCustomColor=function(colors){
	if(this.colorPicker!=null){
	    for(var i=0;i<colors.length;i++)
	      this.colorPicker.addColorChoice(colors[i]);
	}
}

ColorGradientField.prototype.onColorChanged=function(){
	this.value=(this.colorPicker.getGradient());
    this.portlet.onChange(this.id,{value:this.getValue()==null ? null : this.getValue().toString()});
	this.updateColorPicker();
}



ColorGradientField.prototype.onFieldSizeChanged=function(){
  this.colorPicker.setSize(this.width,this.height);
};
ColorGradientField.prototype.setNoColorText=function(noColorText){
	this.noColorText = noColorText;
};
ColorGradientField.prototype.setValue=function(value){
  if(value==null || value=='')
    this.value=null;
  else{
    this.value=new ColorGradient();
    this.value.parseString(value);
    this.colorPicker.setGradient(this.value);
  }
	this.updateColorPicker();
};

ColorGradientField.prototype.updateColorPicker=function(){
return;
    if(this.value==null){
      this.context.clearRect(0, 0, this.element.width, this.element.height);
    }else{
	var w=this.element.width;
	var h=this.element.height;
	var c=this.context;
	var g=this.value;
	var len=g.length();
	c.clearRect(0,0,w,h);
	if(len>0){
      var grad = this.context.createLinearGradient(w, 0, 0, 0);
      grad.addColorStop(0,g.getColorAtStep(0));
      var min=g.getMinValue();
      var max=g.getMaxValue();
      if(min<max){
        for(var i=0;i<len;i++){
          var v=g.getValueAtStep(i);
          grad.addColorStop((v-min) / (max-min) ,g.getColorAtStep(i));
        }  
      }else{
        for(var i=0;i<len;i++){
          var v=g.getValueAtStep(i);
          //grad.addColorStop(v,g.getColorAtStep(i));
        }  
      }
      grad.addColorStop(1,g.getColorAtStep(len-1));
      c.fillStyle=grad;
      c.fillRect(0,0,w,h);
	}
    
	}
};

ColorGradientField.prototype.getValue=function(){
  return this.value;
};

ColorGradientField.prototype.focusField=function(){
	this.colorPicker.element.focus();
	return true;
};




