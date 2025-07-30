package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_form_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_form_js_1() {
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
            "function Form(portlet, parentId){\r\n"+
            "    var that = this;\r\n"+
            "    this.portlet=portlet;\r\n"+
            "    this.location=this.portlet.location;\r\n"+
            "    this.formDOMManager = new FormDOMManager(that, this.portlet.divElement);\r\n"+
            "}\r\n"+
            "Form.prototype.portlet;\r\n"+
            "Form.prototype.location;\r\n"+
            "Form.prototype.formDOMManager;\r\n"+
            "Form.prototype._activeField;\r\n"+
            "\r\n"+
            "Form.prototype.getId=function(e,target){\r\n"+
            "	return this.portlet.instance.portletId;\r\n"+
            "}\r\n"+
            "Form.prototype.handleKeydown=function(e,target){\r\n"+
            "\r\n"+
            "}\r\n"+
            "Form.prototype.setHtmlLayout=function(htmlLayout,rotate){\r\n"+
            "    this.rotate=rotate;\r\n"+
            "    var hasHtmlLayout = this.htmlLayout!=null;\r\n"+
            "    var hasHiddenHtmlLayout = this.hiddenHtmlLayout != null;\r\n"+
            "	if(hasHtmlLayout){\r\n"+
            "	  this.htmlLayout=null;\r\n"+
            "	}\r\n"+
            "	if(htmlLayout!=null){\r\n"+
            "	  this.hiddenHtmlLayout=htmlLayout;\r\n"+
            "	}else if(hasHiddenHtmlLayout){\r\n"+
            "		this.hiddenHtmlLayout = null;\r\n"+
            "    }\r\n"+
            "    this.formDOMManager.setHtmlLayout(hasHtmlLayout, hasHiddenHtmlLayout, htmlLayout);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.setButtonStyle=function(buttonHeight,buttonPaddingT,buttonPaddingB,buttonPanelStyle,buttonsStyle,buttonSpacing){\r\n"+
            "	this.buttonHeight= buttonHeight;\r\n"+
            "	this.buttonPaddingT= buttonPaddingT;\r\n"+
            "	this.buttonPaddingB= buttonPaddingB;\r\n"+
            "	this.buttonPanelStyle=buttonPanelStyle;\r\n"+
            "	this.buttonSpacing=buttonSpacing;\r\n"+
            "	this.buttonsStyle=buttonsStyle;\r\n"+
            "    this.updateLayout();\r\n"+
            "    this.formDOMManager.setButtonStyle(this.buttonPaddingT, this.buttonPaddingB);\r\n"+
            "}\r\n"+
            "Form.prototype.updateLayout=function(width,height){\r\n"+
            "	var buttonsHeight=this.hasButtons ? toPx(this.buttonPaddingB+this.buttonPaddingT+this.buttonHeight) : \"0px\";\r\n"+
            "	this.buttonsContainerHeight = buttonsHeight;\r\n"+
            "    this.formDOMManager.updateLayout(buttonsHeight);\r\n"+
            "}\r\n"+
            "//Form.prototype.handleKeyDown=function(e){\r\n"+
            "//	if (e.keyCode==37 || e.keyCode==38 || e.keyCode==39 || e.keyCode==40)\r\n"+
            "//		this.onUserDirectionKey(e);\r\n"+
            "//}\r\n"+
            "Form.prototype.setScroll=function(clipLeft, clipTop){\r\n"+
            "    this.clipTop=clipTop;\r\n"+
            "    this.clipLeft=clipLeft;\r\n"+
            "	this.formDOMManager.scrollPane.setClipTop(clipTop);\r\n"+
            "	this.formDOMManager.scrollPane.setClipLeft(clipLeft);\r\n"+
            "	this.onScroll();\r\n"+
            "}\r\n"+
            "Form.prototype.onScroll=function(){\r\n"+
            "	this.formDOMManager.scrollPane.DOM.innerpaneElement.style.top=toPx(-this.formDOMManager.scrollPane.getClipTop());\r\n"+
            "	this.formDOMManager.scrollPane.DOM.innerpaneElement.style.left=toPx(-this.formDOMManager.scrollPane.getClipLeft());\r\n"+
            "	if((this.clipTop == this.formDOMManager.scrollPane.getClipTop()) && (this.clipLeft == this.formDOMManager.scrollPane.getClipLeft()))\r\n"+
            "		return;\r\n"+
            "//	portletManager.onUserSpecialScroll(this.formDOMManager.scrollPane.getClipLeft(), this.formDOMManager.scrollPane.getClipTop(), this.portlet.divElement.id);\r\n"+
            "	portletManager.onUserSpecialScroll(this.formDOMManager.scrollPane.getClipLeft(), this.formDOMManager.scrollPane.getClipTop(), this.portlet.instance.portletId);\r\n"+
            "	this.clipTop = this.formDOMManager.scrollPane.getClipTop();\r\n"+
            "	this.clipLeft = this.formDOMManager.scrollPane.getClipLeft();\r\n"+
            "}\r\n"+
            "Form.prototype.getContainerSize=function(){\r\n"+
            "	var w = 0;//this.width-this.scrollPane.scrollSize;\r\n"+
            "	var h = 0;//this.height-this.scrollPane.scrollSize;\r\n"+
            "	var containerLeft = this.formDOMManager.formContainer.getBoundingClientRect().left;\r\n"+
            "	var containerTop = this.formDOMManager.formContainer.getBoundingClientRect().top;\r\n"+
            "	for(var i in this.fields){\r\n"+
            "		var field = this.fields[i];\r\n"+
            "		if(field != null && field.container != null && field.label != null){\r\n"+
            "			var labelRight = field.label.getBoundingClientRect().right - containerLeft;\r\n"+
            "			var fieldRight = field.container.getBoundingClientRect().right - containerLeft;\r\n"+
            "			\r\n"+
            "			w = Math.max(w, fieldRight, labelRight);\r\n"+
            "			var labelBottom = field.label.getBoundingClientRect().bottom - containerTop;\r\n"+
            "			var fieldBottom = field.container.getBoundingClientRect().bottom - containerTop;\r\n"+
            "			h = Math.max(h, fieldBottom, labelBottom);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "//    if(this.width > w && this.height > h){\r\n"+
            "//		w = this.width;\r\n"+
            "//		h = this.height;\r\n"+
            "//	}\r\n"+
            "	return {w:w,h:h};\r\n"+
            "}\r\n"+
            "Form.prototype.getButtonsHeight=function(){\r\n"+
            "	if(this.hasButtons)\r\n"+
            "		return this.buttonPaddingB+this.buttonPaddingT+this.buttonHeight;\r\n"+
            "	else\r\n"+
            "		return 0;\r\n"+
            "}\r\n"+
            "Form.prototype.setSize=function(width,height){\r\n"+
            "	//Window Size\r\n"+
            "	this.width = width;\r\n"+
            "    this.height = height;\r\n"+
            "    this.formDOMManager.setSize(width,height);\r\n"+
            "}\r\n"+
            "Form.prototype.repaint=function(){\r\n"+
            "	//Contained Size\r\n"+
            "	if(this.width == undefined || this.height == undefined)\r\n"+
            "		return;\r\n"+
            "    this.formDOMManager.repaint();\r\n"+
            "	this.formDOMManager.scrollPane.setClipTop(this.clipTop);\r\n"+
            "	this.formDOMManager.scrollPane.setClipLeft(this.clipLeft);\r\n"+
            "	this.onScroll();\r\n"+
            "}\r\n"+
            "Form.prototype.getInputWidth=function(){\r\n"+
            "	return this.location.width-this.labelWidth;\r\n"+
            "}\r\n"+
            "Form.prototype.getWidthStretchPadding=function(){\r\n"+
            "	return this.widthStretchPadding;\r\n"+
            "}\r\n"+
            "Form.prototype.getInputsWidth=function(){\r\n"+
            "  return this.location.width-this.labelWidth;\r\n"+
            "}\r\n"+
            "Form.prototype.onTab=function(field, e){\r\n"+
            "	e.preventDefault();\r\n"+
            "//	this.onKey(field.getId(),e,{pos:-1});\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.addField=function(field, cssStyle, isFieldHidden){\r\n"+
            "    this.fields[field.getId()]=field;\r\n"+
            "    field.form=this;\r\n"+
            "    this.formDOMManager.addField(field,cssStyle, isFieldHidden);\r\n"+
            "    field.setHidden(isFieldHidden);\r\n"+
            "   \r\n"+
            "}\r\n"+
            "Form.prototype.removeField=function(id){\r\n"+
            "    field=this.fields[id];\r\n"+
            "    if(field==null)\r\n"+
            "    	return;\r\n"+
            "    delete this.fields[id];\r\n"+
            "    field.form=null;\r\n"+
            "    this.formDOMManager.removeField(field);\r\n"+
            "   \r\n"+
            "}\r\n"+
            "Form.prototype.onChange=function(fieldId,values){\r\n"+
            "	values.fieldId=fieldId;\r\n"+
            "	values.mid = this.fields[fieldId].getModificationNumber(); \r\n"+
            "//	err([\"Form.onChange\",...arguments,values.mid]);\r\n"+
            "	this.callBack('onchange',values);\r\n"+
            "}\r\n"+
            "Form.prototype.onKey=function(fieldId,e,values){\r\n"+
            "	values.fieldId=fieldId;\r\n"+
            "	values.keycode=e.keyCode;\r\n"+
            "	values.shift=e.shiftKey ? true : false;\r\n"+
            "	values.ctrl=e.ctrlKey ? true : false;\r\n"+
            "	values.alt=e.altKey ? true : false;\r\n"+
            "	this.callBack('onkey',values);\r\n"+
            "}\r\n"+
            "Form.prototype.setScrollOptions=function(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows, cornerCl){\r\n"+
            "	this.formDOMManager.setScrollOptions(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows, cornerCl);\r\n"+
            "}\r\n"+
            "Form.prototype.addButton=function(id,name,cssStyle){\r\n"+
            "    return this.formDOMManager.addButton(id,name,cssStyle);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.onButton=function(buttonId){\r\n"+
            "	this.callBack('onbutton',{buttonId:buttonId});\r\n"+
            "}\r\n"+
            "Form.prototype.onClick=function(fieldId,values){\r\n"+
            "	values.fieldId=fieldId;\r\n"+
            "	this.callBack('onclick',{fieldId:fieldId});\r\n"+
            "}\r\n"+
            "Form.prototype.getField=function(fieldId){\r\n"+
            "	var r=this.fields[fieldId];\r\n"+
            "	if(r==null)\r\n"+
            "		alert(\"field not found for portlet \"+this.getId()+\": \"+fieldId);\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.reset=function(){\r\n"+
            "	this.fields=[];\r\n"+
            "    this.hasButtons=false;\r\n"+
            "    this.formDOMManager.reset();\r\n"+
            "}\r\n"+
            "Form.prototype.resetButtons=function(){\r\n"+
            "    this.hasButtons=false;\r\n"+
            "    this.formDOMManager.resetButtons();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.setCssStyle=function(cssStyle){\r\n"+
            "	this.formDOMManager.setCssStyle(cssStyle);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.setLabelWidth=function(labelWidth, labelPadding, labelsStyle,fieldSpacing,widthStretchPadding){\r\n"+
            "	if(labelWidth==0)\r\n"+
            "		labelWidth=1;\r\n"+
            "	this.labelsStyle=labelsStyle;\r\n"+
            "	this.fieldSpacing=fieldSpacing;\r\n"+
            "	this.labelWidth=labelWidth;\r\n"+
            "	this.widthStretchPadding=widthStretchPadding;\r\n"+
            "	this.labelPaddingPx=toPx(labelPadding); \r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.showButtonContextMenu=function(menu){\r\n"+
            "	   this.createMenu(menu,true).show(this.contextMenuPoint);\r\n"+
            "}\r\n"+
            "Form.prototype.createMenu=function(menu,isButton,fieldId){\r\n"+
            "   var that=this;\r\n"+
            "   that.btn=isButton;\r\n"+
            "   var r=new Menu(getWindow(this.portlet.divElement));\r\n"+
            "   r.setFieldId(fieldId);\r\n"+
            "   r.onClose=function(){\r\n"+
            "	   if (this.fieldId)\r\n"+
            "		   that.focusField(this.fieldId);\r\n"+
            "   };\r\n"+
            "   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e, that.btn, that.contextMenuFieldid, id);} );\r\n"+
            "   return r;\r\n"+
            "}\r\n"+
            "Form.prototype.showContextMenu=function(menu,fieldId){\r\n"+
            "	this.createMenu(menu,false,fieldId).show(this.contextMenuPoint);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.onUserContextMenu=function(e,fieldid,cursorPos){\r\n"+
            "  this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "  this.contextMenuFieldid=fieldid;\r\n"+
            "  this.callBack('menu',{'fieldId':fieldid,'cursorPos':cursorPos});\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.onUserButtonContextMenu=function(e,buttonId,cursorPos){\r\n"+
            "    this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "    this.contextMenuFieldid=buttonId;\r\n"+
            "    this.callBack('menubutton',{'buttonId':buttonId,'cursorPos':cursorPos});\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.onTitleClicked=function(e,fid){\r\n"+
            "    this.callBack('onTitleClicked',{fieldId:fid,x:MOUSE_POSITION_X,y:MOUSE_POSITION_Y});\r\n"+
            "}\r\n"+
            "Form.prototype.focusField=function(fieldId){\r\n"+
            "	this.fields[fieldId].focusField();\r\n"+
            "	getWindow(this.portlet.divElement).kmm.setActivePortletId(this.getId(),false);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.onUserContextMenuItem=function(e,isButton,fid,id){\r\n"+
            "    if(isButton)\r\n"+
            "        this.callBack('menubuttonitem',{'buttonId':fid,'action':id});\r\n"+
            "    else\r\n"+
            "        this.callBack('menuitem',{'fieldId':fid,'action':id});\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.onCustom=function(fieldId, action, values){\r\n"+
            "	values.fieldId=fieldId;\r\n"+
            "	values.action = action;\r\n"+
            "	this.callBack('oncustom', values);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Form.prototype.onFieldCallBack=function(fieldId, action, values){\r\n"+
            "	values.fieldId=fieldId;\r\n"+
            "	values.action = action;\r\n"+
            "	this.callBack('onFieldCB', values);\r\n"+
            "}\r\n"+
            "Form.prototype.onFieldExtensionCallBack=function(fieldId, extId, action, values){\r\n"+
            "	values.fieldId=fieldId;\r\n"+
            "	values.extId = extId;\r\n"+
            "	values.action = action;\r\n"+
            "	this.callBack('onExtCB', values);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function FormDOMManager(_formPortlet, divElement){\r\n"+
            "    this._formPortlet = _formPortlet\r\n"+
            "    this.divElement = divElement;\r\n"+
            "    this.divElement.tabIndex=-1;\r\n"+
            "\r\n"+
            "    this.formContainer = nw('div', 'portal_form_container');\r\n"+
            "    this.form=nw('div','portal_form');\r\n"+
            "    this.buttonsContainer=nw('div');\r\n"+
            "    this.buttons=nw('div','portal_form_buttons');\r\n"+
            "    this.scrollContainer=nw('div');\r\n"+
            "    this.hiddenDivElement = nw(");
          out.print(
            "'div', 'portal_form_hidden');\r\n"+
            "    \r\n"+
            "    //  this.divElement.appendChild(this.formContainer);\r\n"+
            "  this.formContainer.appendChild(this.form);\r\n"+
            "  this.divElement.appendChild(this.buttons);\r\n"+
            "  this.formContainer.appendChild(this.hiddenDivElement);\r\n"+
            "  this.buttons.appendChild(this.buttonsContainer);\r\n"+
            "  this.buttonsContainer.style.left='0px';\r\n"+
            "  this.buttonsContainer.style.right='0px';\r\n"+
            "  this.divElement.appendChild(this.scrollContainer);\r\n"+
            "  this.divElement.style.left='0px';\r\n"+
            "  this.divElement.style.top='0px';\r\n"+
            "  this.divElement.style.bottom='0px';\r\n"+
            "  this.divElement.style.right='0px';\r\n"+
            "  this.scrollContainer.style.left='0px';\r\n"+
            "  this.scrollContainer.style.top='0px';\r\n"+
            "  this.scrollContainer.style.bottom='0px';\r\n"+
            "  this.scrollContainer.style.right='0px';\r\n"+
            "\r\n"+
            "  this.scrollPane = new ScrollPane(this.scrollContainer, 15, this.formContainer);\r\n"+
            "  this.scrollPane.DOM.paneElement.style.overflow=\"visible\";\r\n"+
            "  var that = this;\r\n"+
            "  this.scrollPane.onScroll=function(){that._formPortlet.onScroll()};\r\n"+
            "//MOBILE SUPPORT - scroll for formpanel\r\n"+
            "  this.currPoint = new Point(0,0);\r\n"+
            "  this.formContainer.ontouchmove=function(e) { if(e.target != that.hiddenDivElement) return; that.onTouchDragMove(e);};\r\n"+
            "}\r\n"+
            "FormDOMManager.prototype._formPortlet;\r\n"+
            "FormDOMManager.prototype.divElement;\r\n"+
            "FormDOMManager.prototype.formContainer;\r\n"+
            "FormDOMManager.prototype.form;\r\n"+
            "FormDOMManager.prototype.buttonsContainer;\r\n"+
            "FormDOMManager.prototype.buttons;\r\n"+
            "FormDOMManager.prototype.scrollContainer;\r\n"+
            "FormDOMManager.prototype.hiddenDivElement;\r\n"+
            "FormDOMManager.prototype.scrollPane;\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "FormDOMManager.prototype.setCssStyle=function(cssStyle){\r\n"+
            "	this.scrollPane.DOM.paneElement.style.background = \"none\";\r\n"+
            "	if(this.htmlLayout==null){\r\n"+
            "	  applyStyle(this.formContainer,cssStyle);\r\n"+
            "	  applyStyle(this.hiddenDivElement,cssStyle);\r\n"+
            "	  applyStyle(this.buttons,cssStyle);\r\n"+
            "	}else{\r\n"+
            "	  applyStyle(this.formContainer,cssStyle);\r\n"+
            "	  applyStyle(this.hiddenDivElement,cssStyle);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "//MOBILE SUPPORT - scroll for formpanel\r\n"+
            "FormDOMManager.prototype.onTouchDragMove=function(e){\r\n"+
            "	var diffx = this.currPoint.x - getMousePoint(e).x;\r\n"+
            "	var diffy = this.currPoint.y - getMousePoint(e).y;\r\n"+
            "	this.scrollPane.hscroll.goPage(0.01 * min(max(diffx, -3), 3));\r\n"+
            "	this.scrollPane.vscroll.goPage(0.01 * min(max(diffy, -3), 3));\r\n"+
            "	this.currPoint = getMousePoint(e);\r\n"+
            "}\r\n"+
            "FormDOMManager.prototype.reset=function(){\r\n"+
            "    removeAllChildren(this.form);\r\n"+
            "    this.resetButtons();\r\n"+
            "}\r\n"+
            "FormDOMManager.prototype.resetButtons=function(){\r\n"+
            "	removeAllChildren(this.buttonsContainer);\r\n"+
            "	this.buttons.style.height=\"0px\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormDOMManager.prototype.addButton=function(id,name,cssStyle){\r\n"+
            "    var btn=nw('button');\r\n"+
            "    btn.buttonId=id;\r\n"+
            "    btn.innerHTML=name;\r\n"+
            "    var that=this;\r\n"+
            "    btn.onclick=function(e){that._formPortlet.onButton(getMouseTarget(e).buttonId);};\r\n"+
            "    btn.oncontextmenu=function(e){that.formPortlet.onUserButtonContextMenu(e,id,getMouseTarget(e).buttonId);};\r\n"+
            "    btn.style.height=toPx(this._formPortlet.buttonHeight);\r\n"+
            "    btn.style.marginLeft=toPx(this._formPortlet.buttonSpacing);\r\n"+
            "    btn.style.marginRight=toPx(this._formPortlet.buttonSpacing);\r\n"+
            "    btn.cssStyle=cssStyle;\r\n"+
            "    if(!this._formPortlet.hasButtons){\r\n"+
            "      this._formPortlet.hasButtons=true;\r\n"+
            "      this._formPortlet.updateLayout();\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "    if(this._formPortlet.htmlLayout!=null){\r\n"+
            "        var element=getDocument(this.divElement).getElementById(\"formbutton_\"+this._formPortlet.getId()+\"_\"+id);\r\n"+
            "        if(element!=null)\r\n"+
            "            element.appendChild(btn);\r\n"+
            "    }else{\r\n"+
            "      this.buttonsContainer.appendChild(btn);\r\n"+
            "    }\r\n"+
            "    applyStyle(btn,this._formPortlet.buttonsStyle);\r\n"+
            "    applyStyle(btn,cssStyle);\r\n"+
            "    return btn;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormDOMManager.prototype.setScrollOptions=function(width, gripColor, trackColor, trackButtonColor, iconsColor, borderColor, borderRadius, hideArrows, cornerColor){\r\n"+
            "	this.scrollPane.hscroll.DOM.hideArrows(hideArrows);\r\n"+
            "	this.scrollPane.vscroll.DOM.hideArrows(hideArrows);\r\n"+
            "	this.scrollPane.setSize(width * 1);\r\n"+
            "	this.scrollPane.hscroll.DOM.applyColors(gripColor, trackColor, trackButtonColor, borderColor,cornerColor);\r\n"+
            "	this.scrollPane.vscroll.DOM.applyColors(gripColor, trackColor, trackButtonColor, borderColor,cornerColor);\r\n"+
            "	if (borderRadius!=null) {\r\n"+
            "		this.scrollPane.hscroll.DOM.applyBorderRadius(borderRadius);\r\n"+
            "		this.scrollPane.vscroll.DOM.applyBorderRadius(borderRadius);\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	this.scrollPane.updateIconsColor(iconsColor);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormDOMManager.prototype.removeField=function(field){\r\n"+
            "  var container=field.container;\r\n"+
            "  container.removeChild(field.getElement());\r\n"+
            "  this.form.removeChild(container);\r\n"+
            "  this.form.removeChild(field.getLabelElement());\r\n"+
            "}\r\n"+
            "FormDOMManager.prototype.addField=function(field, cssStyle, isFieldHidden){\r\n"+
            "    var container;\r\n"+
            "    if(isFieldHidden == true){\r\n"+
            "		container=getDocument(this.divElement).getElementById(\"formfield_\"+this._formPortlet.getId()+\"_\"+field.getId());\r\n"+
            "  		if(container != null)\r\n"+
            "  			container.style.position=\"static\";\r\n"+
            "    }\r\n"+
            "    else {\r\n"+
            "    	if(this._formPortlet.htmlLayout!=null){\r\n"+
            "    		container=getDocument(this.divElement).getElementById(\"formfield_\"+this._formPortlet.getId()+\"_\"+field.getId());\r\n"+
            "    	}else{\r\n"+
            "    		//If the field isn't displayed in the hiddenHtmlLayout add it to the form\r\n"+
            "    		if(container == null){\r\n"+
            "    			container=nw('div');\r\n"+
            "				this.form.appendChild(container);\r\n"+
            "    			this.form.appendChild(field.getLabelElement());\r\n"+
            "    		}\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "    if(container!=null){\r\n"+
            "		container.appendChild(field.getElement());\r\n"+
            "      field.container=container;\r\n"+
            "    }\r\n"+
            "    field.setLabelStyle(this._formPortlet.labelsStyle);\r\n"+
            "    field.setFieldStyle(cssStyle);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormDOMManager.prototype.repaint=function(){\r\n"+
            "	//Contained Size\r\n"+
            "	this.scrollPane.setLocation(0, 0, this._formPortlet.width, max(0,this._formPortlet.height-this.buttons.clientHeight));\r\n"+
            "	var size = this._formPortlet.getContainerSize();\r\n"+
            "	var w = size.w;\r\n"+
            "	var h = size.h;\r\n"+
            "	var mw=Math.max(w,this._formPortlet.width);\r\n"+
            "	var mh=Math.max(h,this._formPortlet.height);\r\n"+
            "	this.formContainer.style.height=mh;\r\n"+
            "	this.formContainer.style.width=mw;\r\n"+
            "	if(w != 0)\r\n"+
            "		this.hiddenDivElement.style.width=toPx(mw);\r\n"+
            "	if(h != 0)\r\n"+
            "		this.hiddenDivElement.style.height=toPx(mh);\r\n"+
            "	if(w != 0 && h != 0)\r\n"+
            "		rotateDivElement(this.formContainer,this._formPortlet.rotate,mw,mh,0,0);\r\n"+
            "	this.scrollContainer.style.overflow='clip';\r\n"+
            "	  \r\n"+
            "	this.scrollPane.setPaneSize(w,h);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormDOMManager.prototype.setSize=function(width,height){\r\n"+
            "	this.scrollPane.setLocation(0, 0, width, height-this.buttons.clientHeight);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormDOMManager.prototype.updateLayout=function(buttonsHeight){\r\n"+
            "    //	this.form.style.bottom=buttonsHeight;\r\n"+
            "	this.buttons.style.height=buttonsHeight;\r\n"+
            "	applyStyle(this.buttons,this._formPortlet.buttonPanelStyle);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormDOMManager.prototype.setButtonStyle=function(buttonPaddingT,buttonPaddingB){\r\n"+
            "    this.buttonsContainer.style.top=toPx(buttonPaddingT);\r\n"+
            "    this.buttonsContainer.style.bottom=toPx(buttonPaddingB);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormDOMManager.prototype.setHtmlLayout=function(hasHtmlLayout, hasHiddenHtmlLayout, htmlLayout){\r\n"+
            "	if(hasHtmlLayout){ //reset\r\n"+
            "	  removeAllChildren(this.formContainer);\r\n"+
            "      this.formContainer.appendChild(this.form);\r\n"+
            "      this.form.appendChild(this.buttons);\r\n"+
            "	}\r\n"+
            "	if(htmlLayout!=null){\r\n"+
            "	  this.hiddenDivElement.innerHTML=htmlLayout;\r\n"+
            "	  var childs=this.hiddenDivElement.children;\r\n"+
            "	  for(var i in childs){\r\n"+
            "		  var child=childs[i];\r\n"+
            "		  if(child.nodeName==\"SCRIPT\"){\r\n"+
            "			  eval(child.text);\r\n"+
            "	      }\r\n"+
            "	  }\r\n"+
            "	}else if(hasHiddenHtmlLayout != null){\r\n"+
            "		removeAllChildren(this.hiddenDivElement);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "//  FormField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "\r\n"+
            "function FormField(portlet,id,title){\r\n"+
            "  this.label=nw('div','portal_form_label');\r\n"+
            "  this.labelSpan = nw('span');\r\n"+
            "  this.portlet=portlet;\r\n"+
            "  this.id=id;\r\n"+
            "  this.title=title;\r\n"+
            "  this.label.appendChild(this.labelSpan);\r\n"+
            "  this.labelSpan.innerHTML=title;\r\n"+
            "  this.value = null;\r\n"+
            "  this.modificationNumber = -1;\r\n"+
            "  this.isFieldHidden = false;\r\n"+
            "  this.inputList = [];\r\n"+
            "}\r\n"+
            "FormField.prototype.width;\r\n"+
            "FormField.prototype.height;\r\n"+
            "FormField.prototype.container;\r\n"+
            "FormField.prototype.element;\r\n"+
            "FormField.prototype.modificationNumber;\r\n"+
            "FormField.prototype.value;\r\n"+
            "FormField.prototype.isFieldHidden;\r\n"+
            "FormField.prototype.extensions;\r\n"+
            "FormField.prototype.inputList;\r\n"+
            "\r\n"+
            "FormField.prototype.initField=function(title,titleClickable,disabled,fieldHidden,labelHidden,hids,zindex){\r\n"+
            "	this.setTitle(title,titleClickable);\r\n"+
            "	this.setDisabled(disabled);\r\n"+
            "	this.setFieldHidden(fieldHidden);\r\n"+
            "	this.setLabelHidden(labelHidden);\r\n"+
            "	this.setHIDS(hids);\r\n"+
            "	this.setZIndex(zindex);\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormField.prototype.setFieldValue=function(value, modificationNumber){\r\n"+
            "        this.setModificationNumber(modificationNumber);\r\n"+
            "        this.setValue(value);\r\n"+
            "}\r\n"+
            "FormField.prototype.setFieldPosition=function(x,y,w,h,lx,ly,lw,lh,a,s,padding){\r\n"+
            "	if(this.htmlLayout==null)\r\n"+
            "	  this.setPosition(x,y,w,h);\r\n"+
            "	else\r\n"+
            "	  this.setPosition(null,null,w,h);\r\n"+
            "	this.setLabelPosition(lx,ly,lw,lh);\r\n"+
            "	this.setLabelAlignment(a, s, toPx(padding));\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormField.prototype.setFieldStyleOptions=function(labelColor, bold, italic, underline, fontFamily,labelFontSize){\r\n"+
            "	this.setStyleOptions(labelColor, bold, italic, underline, fontFamily);\r\n"+
            "	this.setLabelSize(labelFontSize);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormField.prototype.addExtension=function(extension){\r\n"+
            "	if(this.extensions==null)\r\n"+
            "		this.extensions=[];\r\n"+
            "	this.extensions.push(extension);\r\n"+
            "}\r\n"+
            "FormField.prototype.addExtensionAt=function(extension, index){\r\n"+
            "	if(this.extensions==null)\r\n"+
            "		this.extensions=[];\r\n"+
            "	this.extensions[index] = extension;\r\n"+
            "}\r\n"+
            "FormField.prototype.hasExtensions=function(){\r\n"+
            "	return this.extensions != null && this.extensions.length > 0;\r\n"+
            "}\r\n"+
            "FormField.prototype.getExtension=function(i){\r\n"+
            "	return this.extensions[i];\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FormField.prototype.setModificationNumber=function(modificationNumber){\r\n"+
            "	this.modificationNumber = modificationNumber;\r\n"+
            "}\r\n"+
            "FormField.pr");
          out.print(
            "ototype.getModificationNumber=function(){\r\n"+
            "	return this.modificationNumber;\r\n"+
            "}\r\n"+
            "FormField.prototype.setHidden=function(hidden){\r\n"+
            "	this.getElement().style.position=hidden?\"static\":\"absolute\";\r\n"+
            "}  		\r\n"+
            "FormField.prototype.setValue=function(value){\r\n"+
            "	this.value = value;\r\n"+
            "}\r\n"+
            "FormField.prototype.getValue=function(){\r\n"+
            "	return this.value;\r\n"+
            "}\r\n"+
            "FormField.prototype.getHeight=function(){\r\n"+
            "  return this.height;\r\n"+
            "};\r\n"+
            "FormField.prototype.getWidth=function(){\r\n"+
            "  return this.width;\r\n"+
            "};\r\n"+
            "FormField.prototype.focusField=function(inputInd){\r\n"+
            "	if(this.inputList.length != 0 && inputInd != null){\r\n"+
            "		this.inputList[inputInd].focus();\r\n"+
            "		return true;\r\n"+
            "	}\r\n"+
            "	else if(this.input){\r\n"+
            "		this.input.focus();\r\n"+
            "		return true;\r\n"+
            "	}\r\n"+
            "	return false;\r\n"+
            "};\r\n"+
            "FormField.prototype.setPosition=function(x,y,width,height){\r\n"+
            "  this.width=width;\r\n"+
            "  this.height=height;\r\n"+
            "  this.x=x;\r\n"+
            "  this.y=y;\r\n"+
            "  if(this.container==null)\r\n"+
            "	  return;\r\n"+
            "  if(x!=null && y!=null){\r\n"+
            "    this.container.style.left=toPx(x);\r\n"+
            "    this.container.style.top=toPx(y);\r\n"+
            "    this.container.style.textAlign='left';\r\n"+
            "    this.container.style.width=toPx(width);\r\n"+
            "    this.container.style.height=toPx(height);\r\n"+
            "  }else{\r\n"+
            "    this.container.style.position='relative';\r\n"+
            "    this.container.style.display='inline-block';\r\n"+
            "    this.container.style.textAlign='left';\r\n"+
            "    //TODO: must be able to set\r\n"+
            "//    this.container.style.width=toPx(width);\r\n"+
            "//    this.container.style.height=toPx(height);\r\n"+
            "  }\r\n"+
            "  if(this.hasExtensions()){\r\n"+
            "	  for(var i = 0; i < this.extensions.length; i++){\r\n"+
            "		  this.extensions[i].onFieldReposition();\r\n"+
            "	  }\r\n"+
            "  }\r\n"+
            "  this.onFieldSizeChanged();\r\n"+
            "};\r\n"+
            "\r\n"+
            "FormField.prototype.setLabelHidden=function(hidden){\r\n"+
            "	this.label.style.display = hidden==true?\"none\":null;\r\n"+
            "}\r\n"+
            "FormField.prototype.setLabelPosition=function(x,y,width,height){\r\n"+
            "  this.labelX=x;\r\n"+
            "  this.labelY=y;\r\n"+
            "  this.labelWidth=width;\r\n"+
            "  this.labelHeight=height;\r\n"+
            "  this.label.style.left=toPx(x);\r\n"+
            "  this.label.style.top=toPx(y);\r\n"+
            "  this.label.style.width=toPx(width);\r\n"+
            "  this.label.style.height=toPx(height);\r\n"+
            "  if (!parseInt(this.label.style.zIndex)) // if this hasn't been set already\r\n"+
            "	  this.label.style.zIndex = -1;\r\n"+
            "};\r\n"+
            "\r\n"+
            "FormField.prototype.setLabelAlignment=function(alignment, side, padding){ \r\n"+
            "	switch(side){\r\n"+
            "		case 0: // top\r\n"+
            "			switch (alignment) {\r\n"+
            "				case 0: // top-left\r\n"+
            "					this.label.style.alignItems = \"flex-end\";\r\n"+
            "					this.label.style.justifyContent = \"flex-start\";\r\n"+
            "					this.label.style.textAlign = \"start\";\r\n"+
            "					this.label.style.paddingBottom = padding;\r\n"+
            "					break;\r\n"+
            "				case 1: // top-center\r\n"+
            "					this.label.style.alignItems = \"flex-end\";\r\n"+
            "					this.label.style.justifyContent = \"center\";\r\n"+
            "					this.label.style.textAlign = \"center\";\r\n"+
            "					this.label.style.paddingBottom = padding;\r\n"+
            "					break;\r\n"+
            "				case 2: // top-right\r\n"+
            "					this.label.style.alignItems = \"flex-end\";\r\n"+
            "					this.label.style.justifyContent = \"flex-end\";\r\n"+
            "					this.label.style.textAlign = \"end\";\r\n"+
            "					this.label.style.paddingBottom = padding;\r\n"+
            "					break;\r\n"+
            "				default:\r\n"+
            "					this.label.style.alignItems = \"center\";\r\n"+
            "					this.label.style.justifyContent = \"flex-end\";\r\n"+
            "					this.label.style.textAlign = \"end\";\r\n"+
            "					this.label.style.paddingRight = padding;\r\n"+
            "					break;\r\n"+
            "			}\r\n"+
            "			break;\r\n"+
            "		case 3: // right\r\n"+
            "			switch (alignment) {\r\n"+
            "				case 0: // right-top\r\n"+
            "					this.label.style.alignItems = \"flex-start\";\r\n"+
            "					this.label.style.justifyContent = \"flex-start\";\r\n"+
            "					this.label.style.textAlign = \"start\";\r\n"+
            "					this.label.style.paddingLeft = padding;\r\n"+
            "					break;\r\n"+
            "				case 1: // right-center\r\n"+
            "					this.label.style.alignItems = \"center\";\r\n"+
            "					this.label.style.justifyContent = \"flex-start\";\r\n"+
            "					this.label.style.textAlign = \"start\";\r\n"+
            "					this.label.style.paddingLeft = padding;\r\n"+
            "					break;\r\n"+
            "				case 2: // right-bottom\r\n"+
            "					this.label.style.alignItems = \"flex-end\";\r\n"+
            "					this.label.style.justifyContent = \"flex-start\";\r\n"+
            "					this.label.style.textAlign = \"start\";\r\n"+
            "					this.label.style.paddingLeft = padding;\r\n"+
            "					break;\r\n"+
            "				default:\r\n"+
            "					this.label.style.alignItems = \"center\";\r\n"+
            "					this.label.style.justifyContent = \"flex-end\";\r\n"+
            "					this.label.style.textAlign = \"end\";\r\n"+
            "					this.label.style.paddingRight = padding;\r\n"+
            "					break;\r\n"+
            "			}\r\n"+
            "			break;\r\n"+
            "		case 1: // bottom\r\n"+
            "			switch (alignment) {\r\n"+
            "				case 2: // bottom-right\r\n"+
            "					this.label.style.alignItems = \"flex-start\";\r\n"+
            "					this.label.style.justifyContent = \"flex-end\";\r\n"+
            "					this.label.style.textAlign = \"end\";\r\n"+
            "					this.label.style.paddingTop = padding;\r\n"+
            "					break;\r\n"+
            "				case 1: // bottom-center\r\n"+
            "					this.label.style.alignItems = \"flex-start\";\r\n"+
            "					this.label.style.justifyContent = \"center\";\r\n"+
            "					this.label.style.textAlign = \"center\";\r\n"+
            "					this.label.style.paddingTop = padding;\r\n"+
            "					break;\r\n"+
            "				case 0: // bottom-left\r\n"+
            "					this.label.style.alignItems = \"flex-start\";\r\n"+
            "					this.label.style.justifyContent = \"flex-start\";\r\n"+
            "					this.label.style.textAlign = \"start\";\r\n"+
            "					this.label.style.paddingTop = padding;\r\n"+
            "					break;\r\n"+
            "				default:\r\n"+
            "					this.label.style.alignItems = \"center\";\r\n"+
            "					this.label.style.justifyContent = \"flex-end\";\r\n"+
            "					this.label.style.textAlign = \"end\";\r\n"+
            "					this.label.style.paddingRight = padding;\r\n"+
            "					break;\r\n"+
            "			}\r\n"+
            "			break;\r\n"+
            "		case 2: // left\r\n"+
            "			switch (alignment) {\r\n"+
            "				case 2: // left-bottom\r\n"+
            "					this.label.style.alignItems = \"flex-end\";\r\n"+
            "					this.label.style.justifyContent = \"flex-end\";\r\n"+
            "					this.label.style.textAlign = \"end\";\r\n"+
            "					this.label.style.paddingRight = padding;\r\n"+
            "					break;\r\n"+
            "				case 1: // left-center\r\n"+
            "					this.label.style.alignItems = \"center\";\r\n"+
            "					this.label.style.justifyContent = \"flex-end\";\r\n"+
            "					this.label.style.textAlign = \"end\";\r\n"+
            "					this.label.style.paddingRight = padding;\r\n"+
            "					break;\r\n"+
            "				case 0: // left-top\r\n"+
            "					this.label.style.alignItems = \"flex-start\";\r\n"+
            "					this.label.style.justifyContent = \"flex-end\";\r\n"+
            "					this.label.style.textAlign = \"end\";\r\n"+
            "					this.label.style.paddingRight = padding;\r\n"+
            "					break;\r\n"+
            "				default:\r\n"+
            "					this.label.style.alignItems = \"center\";\r\n"+
            "					this.label.style.justifyContent = \"flex-end\";\r\n"+
            "					this.label.style.textAlign = \"end\";\r\n"+
            "					this.label.style.paddingRight = padding;\r\n"+
            "					break;\r\n"+
            "			}\r\n"+
            "			break;\r\n"+
            "		default:\r\n"+
            "			this.label.style.alignItems = \"center\";\r\n"+
            "			this.label.style.justifyContent = \"flex-end\";\r\n"+
            "			this.label.style.textAlign = \"end\";\r\n"+
            "			this.label.style.paddingRight = padding;\r\n"+
            "			break;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormField.prototype.getTitle=function(){\r\n"+
            "  return this.title;\r\n"+
            "};\r\n"+
            "FormField.prototype.setTitle=function(title,titleIsClickable){\r\n"+
            "  this.title=title;\r\n"+
            "  this.labelSpan.innerHTML=title;\r\n"+
            "\r\n"+
            "  this.titleIsClickable=titleIsClickable;\r\n"+
            "  this.setTitleClickable(this.titleIsClickable);\r\n"+
            "  var that=this;\r\n"+
            "  this.labelSpan.onclick=function(e){that.onTitleClicked(e)};\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "FormField.prototype.setTitleClickable=function(clickable){\r\n"+
            "  if(clickable){\r\n"+
            "    addClassName(this.labelSpan,\"portal_form_label_help\",false);\r\n"+
            "  }else\r\n"+
            "    removeClassName(this.labelSpan,\"portal_form_label_help\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormField.prototype.onTitleClicked=function(e){\r\n"+
            "	if(this.titleIsClickable)\r\n"+
            "         this.portlet.onTitleClicked(e,this.id);\r\n"+
            "}\r\n"+
            "FormField.prototype.setFieldHidden=function(hidden){\r\n"+
            "	this.isFieldHidden = hidden == true;\r\n"+
            "	if(this.container != null)\r\n"+
            "		this.container.style.display = hidden == true?\"none\":null; \r\n"+
            "	else\r\n"+
            "		this.element.style.display = hidden == true?\"none\":null; \r\n"+
            "}\r\n"+
            "FormField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.style.pointerEvents= this.disabled?\"none\":null;\r\n"+
            "	if(this.input) \r\n"+
            "		this.input.disabled=this.disabled;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormField.prototype.setZIndex=function(index){\r\n"+
            "	if(this.container != null)\r\n"+
            "		this.container.style.zIndex=index;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormField.prototype.getId=function(){\r\n"+
            "  return this.id;\r\n"+
            "};\r\n"+
            "FormField.prototype.getElement=function(){\r\n"+
            "  return this.element;\r\n"+
            "};\r\n"+
            "FormField.prototype.getLabelElement=function(){\r\n"+
            "  return this.label;\r\n"+
            "};\r\n"+
            "FormField.prototype.setFieldStyle=function(style){\r\n"+
            "	applyStyle(this.getElement(),style);\r\n"+
            "};\r\n"+
            "\r\n"+
            "FormField.prototype.setLabelStyle=function(style){\r\n"+
            "	applyStyle(this.getLabelElement(),style);\r\n"+
            "};\r\n"+
            "FormField.prototype.setContainer=function(container){\r\n"+
            "	this.container=container;\r\n"+
            "};\r\n"+
            "FormField.prototype.getContainer=function(){\r\n"+
            "	return this.container;\r\n"+
            "};\r\n"+
            "\r\n"+
            "FormField.prototype.handleTab=function(ind, e){\r\n"+
            "	if(e.keyCode==9) \r\n"+
            "		return this.form.onTab(this,e);\r\n"+
            "}\r\n"+
            "FormField.prototype.callBack=function(action, values){\r\n"+
            "	this.portlet.onFieldCallBack(this.id, action, values);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormField.prototype.onExtensionCallback=function(extIndex, action, values){\r\n"+
            "	this.portlet.onFieldExtensionCallBack(this.id, extIndex, action, values);\r\n"+
            "}\r\n"+
            "FormField.prototype.onFieldEvent=function(e, eventType){\r\n"+
            "	this.portlet.callBack(\"onFieldEvent\", {feType:eventType, fieldId:this.id});\r\n"+
            "};\r\n"+
            "FormField.prototype.setStyleOptions=function(labelColor, bold, italic, underline, fontFamily){\r\n"+
            "	this.label.style.color=labelColor;\r\n"+
            "	if (bold==true) {\r\n"+
            "		this.label.style.fontWeight=\"bold\";\r\n"+
            "	} else if(bold==false) {\r\n"+
            "		this.label.style.fontWeight=\"normal\";\r\n"+
            "	}\r\n"+
            "	if (italic==true) {\r\n"+
            "		this.label.style.fontStyle=\"italic\";\r\n"+
            "	} else if(italic==false) {\r\n"+
            "		this.label.style.fontStyle=\"normal\";\r\n"+
            "	}\r\n"+
            "	if (underline==true) {\r\n"+
            "		this.label.style.textDecoration=\"underline\";\r\n"+
            "	} else if(underline==false){\r\n"+
            "		this.label.style.textDecoration=\"none\";\r\n"+
            "	}\r\n"+
            "	this.label.style.fontFamily=fontFamily;\r\n"+
            "}\r\n"+
            "FormField.prototype.setLabelSize=function(labelFontSize){\r\n"+
            "	this.label.style.fontSize=toPx(labelFontSize);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FormField.prototype.setHelpBox=function(text, textWidth, style){\r\n"+
            "  var outerStyle=\"_cna=\"+this.form.portlet.instance.hcsc;\r\n"+
            "  this.helpBox=new HelpBox(getWindow(this.labelSpan), true, style,outerStyle);\r\n"+
            "  this.initHelpBox(true);\r\n"+
            "  this.helpBox.init(text, textWidth);\r\n"+
            "  this.helpBox.autoHide(false, this.labelSpan);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FormField.prototype.setHIDS=function(hids){\r\n"+
            "   this.element.id=hids;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormField.prototype.getHelpBox=function(e){\r\n"+
            "  var that = this;\r\n"+
            "  this.portlet.onCustom(that.id, \"getHelpBox\", {});\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FormField.prototype.initHelpBox=function(hasHelpBox");
          out.print(
            "){\r\n"+
            "  var that=this;\r\n"+
            "  if(this.element.parentNode === null)\r\n"+
            "	  return;\r\n"+
            "  if(hasHelpBox == true){\r\n"+
            "	  this.labelSpan.onclick=function(e){\r\n"+
            "		  if(that.helpBox!=null)\r\n"+
            "			  that.helpBox.showIfMouseInside(e, that.labelSpan);\r\n"+
            "		  else {\r\n"+
            "			  that.getHelpBox(e);\r\n"+
            "		  };\r\n"+
            "	  };\r\n"+
            "\r\n"+
            "//	  this.labelSpan.onmouseleave=function(e){if(that.helpBox!=null) that.helpBox.hide(e, that.label); };\r\n"+
            "  }\r\n"+
            "  else{\r\n"+
            "	  this.labelSpan.onmouseenter=null;\r\n"+
            "	  this.labelSpan.onmousemove=null;\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// ButtonField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "ButtonField.prototype=new FormField();\r\n"+
            "ButtonField.prototype.disableWhenClicked=false;\r\n"+
            "ButtonField.prototype.disabledDueToClick=false;\r\n"+
            "\r\n"+
            "function ButtonField(portlet,id,title){\r\n"+
            "	\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "    this.element=nw('button','form_field_button');\r\n"+
            "    this.element.style.cursor='pointer';\r\n"+
            "    this.element.style.left='0px';\r\n"+
            "	var that=this;\r\n"+
            "	this.input = this.element;\r\n"+
            "    this.element.onclick=function(e){\r\n"+
            "    	if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id);\r\n"+
            "	// MOBILE SUPPORT - added ontouchend for mobile support\r\n"+
            "    	if(getMouseButton(e)==1 || this.element.ontouchend) {\r\n"+
            "    	    if(!that.disabledDueToClick){\r\n"+
            "    	      if(that.disableWhenClicked){\r\n"+
            "    	          that.disabledDueToClick=true;\r\n"+
            "	          that.element.style.cursor='not-allowed';\r\n"+
            "    	        }\r\n"+
            "              that.portlet.callBack('menuitem',{'fieldId':that.id,'action':'button_clicked'});\r\n"+
            "            }\r\n"+
            "    	} \r\n"+
            "    };\r\n"+
            "    this.element.onchange=function(){that.portlet.onChange(that.id,{value:that.getValue()});};\r\n"+
            "    this.element.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "    this.element.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "//    this.element.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e);}\r\n"+
            "    this.element.onkeydown=function(e){that.handleTab(null,e);}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ButtonField.prototype.shouldDisableAfterFirstClick=function(shouldDisable,disabledDueToClick){\r\n"+
            "	this.disableWhenClicked = shouldDisable;\r\n"+
            "	this.disabledDueToClick=disabledDueToClick;\r\n"+
            "	if(this.disabledDueToClick){\r\n"+
            "	  this.element.style.cursor='not-allowed';\r\n"+
            "	}else\r\n"+
            "	  this.element.style.cursor='pointer';\r\n"+
            "}\r\n"+
            "ButtonField.prototype.setFieldStyle=function(style){\r\n"+
            "	this.input.className=\"form_field_button\";\r\n"+
            "	applyStyle(this.input,style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ButtonField.prototype.setValue=function(value){\r\n"+
            "  this.element.innerHTML=value;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ButtonField.prototype.getValue=function(){\r\n"+
            "  return this.element.value;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ButtonField.prototype.onFieldSizeChanged=function(){\r\n"+
            "  this.element.style.height=toPx(this.height);\r\n"+
            "  this.element.style.width=toPx(this.width);\r\n"+
            "}\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// PasswordField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "\r\n"+
            "PasswordField.prototype=new FormField();\r\n"+
            "\r\n"+
            "\r\n"+
            "function PasswordField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "    this.input=nw('input', 'passwordField');\r\n"+
            "    this.input.type=\"password\";\r\n"+
            "    this.input.setAttribute(\"autocomplete\", \"false\");\r\n"+
            "    this.element=nw('div', 'password-field-container');\r\n"+
            "    this.iconDiv = nw('div', 'password-icon-div');\r\n"+
            "    this.element.style.display=\"inline-block\";\r\n"+
            "    this.element.appendChild(this.input);\r\n"+
            "    this.element.appendChild(this.iconDiv);\r\n"+
            "	var that=this;\r\n"+
            "    this.input.oninput=function(e){that.onInput(e);};\r\n"+
            "    this.iconDiv.onmousedown=function(e){that.onMousedown(e);};\r\n"+
            "    this.iconDiv.onmouseup=function(e){that.onMouseup(e);};\r\n"+
            "    this.iconDiv.onmouseout=function(e){that.onMouseout(e);};\r\n"+
            "    //MOBILE SUPPORT - touch support for passwordfield\r\n"+
            "    this.iconDiv.ontouchstart=function(e){that.onMousedown(e);};\r\n"+
            "    this.iconDiv.ontouchend=function(e){that.onMouseup(e);};\r\n"+
            "    this.input.onkeydown=function(e){that.onKeyDown(e);};\r\n"+
            "    this.input.onfocus=function(e){that.onFocus(e);};\r\n"+
            "    this.input.onblur=function(e){that.onBlur(e);};\r\n"+
            "	this._flagNewPass=true;\r\n"+
            "	this._oldPassword=\"\";\r\n"+
            "}\r\n"+
            "PasswordField.prototype._flagNewPass;\r\n"+
            "PasswordField.prototype._oldPassword;\r\n"+
            "PasswordField.prototype.onFieldSizeChanged=function(){\r\n"+
            "  var h=toPx(this.height);\r\n"+
            "  this.input.style.width=toPx(this.width);\r\n"+
            "  this.iconDiv.style.width=toPx(this.width * .10);\r\n"+
            "  \r\n"+
            "  this.input.style.height=h;\r\n"+
            "  this.iconDiv.style.height=h;\r\n"+
            "  \r\n"+
            "  this.input.style.paddingLeft = toPx(this.iconDiv.clientWidth + 5);\r\n"+
            "}\r\n"+
            "PasswordField.prototype.init=function(bgCl, fontCl, fs, bdrCl, fontFam, borderRadius, borderWidth) {\r\n"+
            "	if(bgCl!=null){\r\n"+
            "		var colors = parseColor(bgCl);\r\n"+
            "		this.fieldBgClHex = toColor(colors[0], colors[1], colors[2]);\r\n"+
            "		// click field -> mousedown -> bg image set -> focus -> init -> we have bg image already\r\n"+
            "		if (!this.iconDiv.style.backgroundImage) {\r\n"+
            "			if (this.shouldUseWhiteIcon(bgCl))\r\n"+
            "				this.iconDiv.style.backgroundImage = \"url('rsc/pass-show-white.svg')\";\r\n"+
            "			else\r\n"+
            "				this.iconDiv.style.backgroundImage = \"url('rsc/pass-show-black.svg')\";\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.input.style.color=fontCl;\r\n"+
            "	this.input.style.background=bgCl;\r\n"+
            "	this.input.style.fontSize=toPx(fs);\r\n"+
            "	this.input.style.border=\"1px solid \" + bdrCl;\r\n"+
            "	this.input.style.fontFamily = fontFam;\r\n"+
            "	this.input.style.borderRadius = toPx(borderRadius);\r\n"+
            "	this.input.style.borderWidth = toPx(borderWidth);\r\n"+
            "}\r\n"+
            "PasswordField.prototype.shouldUseWhiteIcon=function(hexColor) { // bgcl should be in hex format\r\n"+
            "	var colorsRGB = parseColor(hexColor);\r\n"+
            "	if (colorsRGB.length === 3) {\r\n"+
            "		return (colorsRGB[0] + colorsRGB[1] + colorsRGB[2]) / 3 < 120;\r\n"+
            "	}\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "PasswordField.prototype.setFieldStyle=function(style){\r\n"+
            "	applyStyle(this.input,style);\r\n"+
            "};\r\n"+
            "PasswordField.prototype.getValue=function() {\r\n"+
            "	return this.input.value;\r\n"+
            "}\r\n"+
            "PasswordField.prototype.setValue=function(val) {\r\n"+
            "	// The server will only send a masked text `*******` so directly setting it is fine\r\n"+
            "	this._flagNewPass = false;\r\n"+
            "	this._oldPassword = \"\";\r\n"+
            "	var pass = this.input.value;\r\n"+
            "	if(pass == null || \"\" == pass.trim() )\r\n"+
            "	    this.input.value = val;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "PasswordField.prototype.onInput=function(e){\r\n"+
            "    this.handleTab(null,e);\r\n"+
            "	this._flagNewPass = false;\r\n"+
            "	this._oldPassword = \"\";\r\n"+
            "    var obfuscated = this.getObfuscated(this.input.value);\r\n"+
            "	this.portlet.onChange(this.id, {value: obfuscated});\r\n"+
            "}\r\n"+
            "PasswordField.prototype.getObfuscated=function(val) {\r\n"+
            "	try {\r\n"+
            "		var v = window.btoa(val);\r\n"+
            "		return v;\r\n"+
            "	} catch(e) {\r\n"+
            "		return val;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "PasswordField.prototype.getUnobfuscated=function(val) {\r\n"+
            "	try {\r\n"+
            "		var v = window.atob(val);\r\n"+
            "		return v;\r\n"+
            "	} catch (e) {\r\n"+
            "		return val;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "PasswordField.prototype.onMousedown=function(e){\r\n"+
            "	this.input.type=\"text\";\r\n"+
            "	// maintain focus when clicking on eye-con\r\n"+
            "	this.input.focus();\r\n"+
            "	if (this.shouldUseWhiteIcon(this.fieldBgClHex))\r\n"+
            "		this.iconDiv.style.backgroundImage = \"url('rsc/pass-hide-white.svg')\";\r\n"+
            "	else\r\n"+
            "		this.iconDiv.style.backgroundImage = \"url('rsc/pass-hide-black.svg')\";\r\n"+
            "	// stop it from firing blur, we want to keep the field focused because eye-con is part of it\r\n"+
            "	e.preventDefault();\r\n"+
            "}\r\n"+
            "PasswordField.prototype.onMouseup=function(e){\r\n"+
            "	this.input.type=\"password\";\r\n"+
            "	if (this.shouldUseWhiteIcon(this.fieldBgClHex))\r\n"+
            "		this.iconDiv.style.backgroundImage = \"url('rsc/pass-show-white.svg')\";\r\n"+
            "	else\r\n"+
            "		this.iconDiv.style.backgroundImage = \"url('rsc/pass-show-black.svg')\";\r\n"+
            "}\r\n"+
            "PasswordField.prototype.onMouseout=function(e){\r\n"+
            "	this.input.type=\"password\";\r\n"+
            "	if (this.shouldUseWhiteIcon(this.fieldBgClHex))\r\n"+
            "		this.iconDiv.style.backgroundImage = \"url('rsc/pass-show-white.svg')\";\r\n"+
            "	else\r\n"+
            "		this.iconDiv.style.backgroundImage = \"url('rsc/pass-show-black.svg')\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "PasswordField.prototype.onFocus=function(e){\r\n"+
            "	this._flagNewPass = true;\r\n"+
            "	this._oldPassword = this.input.value;\r\n"+
            "	this.input.value = \"\";\r\n"+
            "	this.onFieldEvent(e, \"onFocus\");\r\n"+
            "}\r\n"+
            "PasswordField.prototype.onBlur=function(e){\r\n"+
            "	if(this._flagNewPass == true){\r\n"+
            "	this._flagNewPass = false;\r\n"+
            "	this.input.value = this._oldPassword;\r\n"+
            "	this._oldPassword = \"\";\r\n"+
            "	}\r\n"+
            "	this.onFieldEvent(e, \"onBlur\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "PasswordField.prototype.onKeyDown=function(e){\r\n"+
            "	if(e.keyCode==9)\r\n"+
            "		return this.form.onTab(this,e);\r\n"+
            "	\r\n"+
            "	this._flagNewPass = false;\r\n"+
            "	this._oldPassword = \"\";\r\n"+
            "	var that = this;\r\n"+
            "	if(e.keyCode==13 || (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){\r\n"+
            "		var pos=getCursorPosition(this.element);\r\n"+
            "		this.portlet.onCustom(this.id, \"updateCursor\", {pos:pos});\r\n"+
            "	    this.portlet.onKey(this.id,e,{pos:pos});\r\n"+
            "	    if(e.keyCode==9)\r\n"+
            "	      e.preventDefault();\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// TextField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "TextField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function TextField(portlet,id,title){\r\n"+
            "	hasButton=true;\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "    this.input=nw('input');\r\n"+
            "    this.input.setAttribute(\"autocomplete\", \"nope\");\r\n"+
            "    this.element=nw('div');\r\n"+
            "    this.element.style.display=\"inline-block\";\r\n"+
            "    this.element.appendChild(this.input);\r\n"+
            "	var that=this;\r\n"+
            "	this.oldValue = \"\";\r\n"+
            "    this.input.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.input));};\r\n"+
            "    this.input.oninput=function(){that.onChangeDiff();};\r\n"+
            "    this.input.onkeydown=function(e){that.onKeyDown(e);};\r\n"+
            "    this.input.onfocus=function(e){that.onFieldEvent(e, \"onFocus\");};\r\n"+
            "    this.input.onblur=function(e){that.onFieldEvent(e, \"onBlur\");};\r\n"+
            "}\r\n"+
            "TextField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	if(this.input)\r\n"+
            "		this.input.disabled=this.disabled;\r\n"+
            "}\r\n"+
            "TextField.prototype.setHidden=function(hidden){\r\n"+
            "	if(hidden){\r\n"+
            "		this.element.style.display=\"inline-block\";\r\n"+
            "		this.element.style.width=\"auto\";\r\n"+
            "		this.element.style.height=\"auto\";\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.element.style.display=\"block\";\r\n"+
            "		this.element.style.width=\"100%\";\r\n"+
            "		this.element.st");
          out.print(
            "yle.height=\"100%\";\r\n"+
            "	}\r\n"+
            "	this.element.style.position=\"static\";\r\n"+
            "	this.input.style.position=hidden?\"static\":\"absolute\";\r\n"+
            "}\r\n"+
            "TextField.prototype.onChangeDiff=function(){\r\n"+
            "	var newValue = this.getValue();\r\n"+
            "	var change = strDiff(this.oldValue, newValue);\r\n"+
            "	this.oldValue = newValue;\r\n"+
            "	this.portlet.onChange(this.id, {c:change.c,s:change.s,e:change.e,mid:this.getModificationNumber()});\r\n"+
            "}\r\n"+
            "TextField.prototype.onChangeDiffSelect=function(){\r\n"+
            "	var newValue = this.getValue();\r\n"+
            "	var change = strDiff(this.oldValue, newValue);\r\n"+
            "	this.oldValue = newValue;\r\n"+
            "	var isSetValueEvent = true;\r\n"+
            "	this.portlet.onChange(this.id, {c:change.c,s:change.s,e:change.e,mid:this.getModificationNumber(), sv:isSetValueEvent});\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextField.prototype.setFieldStyle=function(style){\r\n"+
            "	this.input.className=\"\";\r\n"+
            "	applyStyle(this.input,style);\r\n"+
            "};\r\n"+
            "\r\n"+
            "TextField.prototype.onKeyDown=function(e){\r\n"+
            "    this.handleTab(null,e);\r\n"+
            "	if(e.keyCode==13 ||  (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){\r\n"+
            "		var pos=getCursorPosition(this.element);\r\n"+
            "		this.portlet.onCustom(this.id, \"updateCursor\", {pos:pos});\r\n"+
            "	    this.portlet.onKey(this.id,e,{pos:pos});\r\n"+
            "	    if(e.keyCode==9)//TAB\r\n"+
            "	      e.preventDefault();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "TextField.prototype.changeSelection=function(start,end){\r\n"+
            "	setSelection(this.input,start,end);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextField.prototype.init=function(maxLength,isPassword,hasButton,callbackKeys){\r\n"+
            "	var that=this;\r\n"+
            "    this.callbackKeys=callbackKeys;\r\n"+
            "	if(hasButton && this.button==null){\r\n"+
            "      this.button=nw('div','textfield_plusbutton');\r\n"+
            "      this.button.onmousedown=function(e){that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.input));};\r\n"+
            "      this.button.style.top='0px';\r\n"+
            "      this.element.appendChild(this.button);\r\n"+
            "    }else if(!hasButton && this.button!=null){\r\n"+
            "      this.element.removeChild(this.button);\r\n"+
            "      this.button=null;\r\n"+
            "    }\r\n"+
            "  this.input.maxLength=maxLength;\r\n"+
            "  if(isPassword)\r\n"+
            "	  this.input.type='password';\r\n"+
            "  else\r\n"+
            "	  this.input.type='';\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "TextField.prototype.onFieldSizeChanged=function(){\r\n"+
            "  var h=toPx(this.height);\r\n"+
            "  if(this.button){\r\n"+
            "	 var t=toPx(this.width-this.height);\r\n"+
            "    this.input.style.width=t;\r\n"+
            "    this.button.style.left=t;\r\n"+
            "    this.button.style.width=h;\r\n"+
            "    this.button.style.height=h;\r\n"+
            "  }else{\r\n"+
            "    this.input.style.width=toPx(this.width);\r\n"+
            "  }\r\n"+
            "  this.input.style.height=h;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextField.prototype.setValue=function(value){\r\n"+
            "  //Does not fire oninput or onchange, so we need to reset the oldValue to the currentValue \r\n"+
            "  if (value == null){\r\n"+
            "	  this.input.value=\"\";\r\n"+
            "	  //this.oldValue = \"\";\r\n"+
            "	  this.oldValue = null;\r\n"+
            "  }else{\r\n"+
            "	  this.input.value=value;\r\n"+
            "	  //this.oldValue = value;\r\n"+
            "	  this.oldValue = null;\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "TextField.prototype.setValueAndFire=function(value){\r\n"+
            "	this.input.value = value;\r\n"+
            "	this.onChangeDiff();\r\n"+
            "}\r\n"+
            "TextField.prototype.setValueAndFireSelect=function(value){\r\n"+
            "	this.input.value = value;\r\n"+
            "	this.onChangeDiffSelect();\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextField.prototype.getValue=function(){\r\n"+
            "  return this.input.value;\r\n"+
            "};\r\n"+
            "\r\n"+
            "TextField.prototype.moveCursor=function(cursorPos){\r\n"+
            "	this.input.focus();\r\n"+
            "	setCursorPosition(this.input,cursorPos);\r\n"+
            "};\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// TitleField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "TitleField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function TitleField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "    this.element=nw('div','portal_form_title');\r\n"+
            "	var that=this;\r\n"+
            "    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};\r\n"+
            "    this.init();\r\n"+
            "}\r\n"+
            "\r\n"+
            "TitleField.prototype.init=function(){\r\n"+
            "  var that=this;\r\n"+
            "  if(this.element.parentNode === null)\r\n"+
            "	  return;\r\n"+
            "  this.element.parentNode.style.verticalAlign='bottom';\r\n"+
            "};\r\n"+
            "TitleField.prototype.onFieldSizeChanged=function(){\r\n"+
            "  var w=toPx(this.width),h=toPx(this.height);\r\n"+
            "  this.element.style.width=w;\r\n"+
            "  this.element.style.height=h;\r\n"+
            "  this.label.style.width=w;\r\n"+
            "  this.label.style.height=h;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TitleField.prototype.setValue=function(value){\r\n"+
            "  this.element.innerHTML=value;\r\n"+
            "};\r\n"+
            "\r\n"+
            "TitleField.prototype.getValue=function(){\r\n"+
            "  return this.element.innerHTML;\r\n"+
            "};\r\n"+
            "TitleField.prototype.setFieldStyle=function(style){\r\n"+
            "  this.element.className=\"portal_form_title\";\r\n"+
            "  applyStyle(this.element,style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// ColorField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "ColorField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function ColorField(portlet,id,title){\r\n"+
            "	\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "    this.element=nw('div');\r\n"+
            "    this.element.style.cursor='pointer';\r\n"+
            "    this.element.style.position='absolute';\r\n"+
            "    this.element.style.left='0px';\r\n"+
            "    this.element.style.background='white url(rsc/checkers.png) repeat-y right';\r\n"+
            "    this.input = nw('input');\r\n"+
            "    this.element.appendChild(this.input);\r\n"+
            "	var that=this;\r\n"+
            "    this.element.onclick=function(){that.element.blur();that.showChooser();};\r\n"+
            "    this.input.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "    this.input.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "    this.input.onkeydown=function(e){\r\n"+
            "    	if(e.keyCode==13 || e.keyCode==32)//enter of spacebar\r\n"+
            "    	  return that.showChooser();\r\n"+
            "      that.handleTab(null,e);\r\n"+
            "    };\r\n"+
            "    this.noColorText = 'no color';\r\n"+
            "}\r\n"+
            "ColorField.prototype.setFieldStyle=function(style){	\r\n"+
            "  this.input.className=\"\";\r\n"+
            "  applyStyle(this.input,style);\r\n"+
            "}\r\n"+
            "ColorField.prototype.init=function(allowNull,alpha,hasButton,displayText) {\r\n"+
            "  this.allowNull=allowNull;\r\n"+
            "  this.alpha=alpha;\r\n"+
            "  this.displayText=displayText;\r\n"+
            "  if(hasButton){\r\n"+
            "    if(this.button==null){\r\n"+
            "	  var that=this;\r\n"+
            "      this.button=nw('div','textfield_plusbutton');\r\n"+
            "      this.button.style.position='relative';\r\n"+
            "      this.button.onmousedown=function(e){that.portlet.onUserContextMenu(e,that.id, -1);};\r\n"+
            "      if(this.element.parentElement!=null)\r\n"+
            "        this.element.parentElement.appendChild(this.button);\r\n"+
            "	  }\r\n"+
            "  }else{\r\n"+
            "	 if(this.button!=null){\r\n"+
            "       if(this.element.parentElement!=null)\r\n"+
            "         this.element.parentElement.removeChild(this.button);\r\n"+
            "	   this.button=null;\r\n"+
            "	 }\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "\r\n"+
            "ColorField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.input.disabled=disabled?'disabled':'';\r\n"+
            "}\r\n"+
            "\r\n"+
            "var currentColorPicker = null;\r\n"+
            "\r\n"+
            "ColorField.prototype.showChooser=function(){\r\n"+
            "	this.colorPicker=new ColorPicker(true,this.getValue(),getWindow(this.element),this.allowNull);\r\n"+
            "	var rect=new Rect();\r\n"+
            "	rect.readFromElement(this.element);\r\n"+
            "	var that=this;\r\n"+
            "	this.colorPicker.onOk=function(){that.onColorPickerOk(); currentColorPicker = null;};\r\n"+
            "	this.colorPicker.onCancel=function(){that.onColorPickerCancel(); currentColorPicker=null; };\r\n"+
            "	this.colorPicker.onColorChanged=function(){that.onColorChanged()};\r\n"+
            "	this.colorPicker.onNoColor=function(){that.onColorPickerNoColor(); currentColorPicker=null; };\r\n"+
            "	currentColorPicker = this.colorPicker;\r\n"+
            "	this.colorPicker.show(rect.getRight(),rect.top);\r\n"+
            "	if(this.alpha)\r\n"+
            "	  this.colorPicker.setAlphaEnabled();\r\n"+
            "    this.portlet.onClick(this.id,{});\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorField.prototype.addCustomColor=function(colors){\r\n"+
            "	if(this.colorPicker!=null){\r\n"+
            "	    for(var i=0;i<colors.length;i++)\r\n"+
            "	      this.colorPicker.addColorChoice(colors[i]);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorField.prototype.onColorPickerOk=function(){\r\n"+
            "	this.setValue(this.colorPicker.getColor());\r\n"+
            "    this.portlet.onChange(this.id,{value:this.getValue(),status:'ok'});\r\n"+
            "	this.colorPicker.hide();\r\n"+
            "	this.colorPicker=null;\r\n"+
            "	this.input.focus();\r\n"+
            "}\r\n"+
            "ColorField.prototype.onColorPickerNoColor=function(){\r\n"+
            "	this.setValue(null);\r\n"+
            "    this.portlet.onChange(this.id,{value:null,status:'nocolor'});\r\n"+
            "	this.colorPicker.hide();\r\n"+
            "	this.colorPicker=null;\r\n"+
            "	this.input.focus();\r\n"+
            "}\r\n"+
            "ColorField.prototype.onColorPickerCancel=function(){\r\n"+
            "	this.setValue(this.colorPicker.getOrigColor());\r\n"+
            "    this.portlet.onChange(this.id,{value:this.getValue(),status:'cancel'});\r\n"+
            "	this.colorPicker.hide();\r\n"+
            "	this.colorPicker=null;\r\n"+
            "	this.input.focus();\r\n"+
            "}\r\n"+
            "ColorField.prototype.onColorChanged=function(){\r\n"+
            "    if(this.value==this.colorPicker.getColor())\r\n"+
            "      return;\r\n"+
            "	this.setValue(this.colorPicker.getColor());\r\n"+
            "    this.portlet.onChange(this.id,{value:this.getValue(),status:'momentary'});\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorField.prototype.onFieldSizeChanged=function(){\r\n"+
            "  var h=toPx(this.height);\r\n"+
            "  if(this.button){\r\n"+
            "	var t=toPx(this.width-this.height);\r\n"+
            "    this.input.style.width=t;\r\n"+
            "    this.button.style.left=t;\r\n"+
            "    this.button.style.width=h;\r\n"+
            "    this.button.style.height=h;\r\n"+
            "  }else{\r\n"+
            "    this.input.style.width=toPx(this.width);\r\n"+
            "  }\r\n"+
            "  this.input.style.height=toPx(this.height);\r\n"+
            "};\r\n"+
            "ColorField.prototype.setNoColorText=function(noColorText){\r\n"+
            "	this.noColorText = noColorText;\r\n"+
            "};\r\n"+
            "ColorField.prototype.setValue=function(value){\r\n"+
            "  this.value=value;\r\n"+
            "  if(value){\r\n"+
            "	var color ;\r\n"+
            "	if(value.length==9)\r\n"+
            "	  color = parseColorAlpha(value);\r\n"+
            "	else \r\n"+
            "	  color = parseColor(value);\r\n"+
            "    this.input.style.background=value;\r\n"+
            "    if(this.width<40)\r\n"+
            "      this.input.style.color=value;\r\n"+
            "    else if(color[3]<128)\r\n"+
            "      this.input.style.color='#000000';\r\n"+
            "    else if((color[0]+color[1]+color[2])/3<120)\r\n"+
            "      this.input.style.color='#ffffff';\r\n"+
            "	else\r\n"+
            "      this.input.style.color='#000000';\r\n"+
            "      if(this.displayText!=null)\r\n"+
            "        this.input.value=this.displayText;\r\n"+
            "      else\r\n"+
            "        this.input.value=value;\r\n"+
            "  }else{\r\n"+
            "	  this.input.style.background='#FFFFFF';\r\n"+
            "	  this.input.style.color='#AAAAAA';\r\n"+
            "	  this.input.value=this.noColorText;\r\n"+
            "  }\r\n"+
            "  if (this.input.disabled){\r\n"+
            "	  this.input.style.color='#555555';\r\n"+
            "	  this.input.style.borderColor='#C0C0C0';\r\n"+
            "      this.input.style.cursor='not-allowed';\r\n"+
            "  } else {\r\n"+
            "      this.input.style.cursor='pointer';\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "ColorField.prototype.getValue=function(){\r\n"+
            "  return this.value;\r\n"+
            "};\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// DayChooserField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "DayChooserField");
          out.print(
            ".prototype=new FormField();\r\n"+
            "\r\n"+
            "function DayChooserField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	var that = this;\r\n"+
            "    this.element=nw('div', 'dateformfield');\r\n"+
            "    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};\r\n"+
            "    this.element.style.display = \"inline-block\";\r\n"+
            "    this.element.onkeydown=function(e){that.onKeyDown(e);};\r\n"+
            "\r\n"+
            "    this.ratio=0.5; // TODO make this a user setting in editor\r\n"+
            "//    this.setIsRange(false);\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.onChangeCallback=function(){\r\n"+
            "	var that = this;\r\n"+
            "	var lYmd = this.chooser.getSelectedYmd();\r\n"+
            "	var rYmd = this.chooser.getSelectedYmd2(); // returns an object containing time broken down to hour,min,sec,ms\r\n"+
            "	// no change, no op\r\n"+
            "	if(lYmd == this.prevlYmd && rYmd == this.prevrYmd)\r\n"+
            "		return;\r\n"+
            "	this.prevlYmd=lYmd;\r\n"+
            "	this.prevrYmd=rYmd;\r\n"+
            "	that.portlet.onChange(that.id,{ymd:lYmd,ymd2:rYmd});\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setDateDisplayFormat=function(format){\r\n"+
            "	if (this.chooser) {\r\n"+
            "		this.chooser.setDateDisplayFormat(format);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DayChooserField.prototype.setFieldStyle=function(style){\r\n"+
            "	if(this.chooser){\r\n"+
            "		this.chooser.input.className=\"cal_input\";\r\n"+
            "		applyStyle(this.chooser.input,style);\r\n"+
            "		if(this.chooser.input2){\r\n"+
            "			this.chooser.input2.className=\"cal_input\";\r\n"+
            "			applyStyle(this.chooser.input2,style);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "DayChooserField.prototype.onKeyDown=function(e){\r\n"+
            "    if(e.keyCode==9){\r\n"+
            "    	if(this.chooser != null)\r\n"+
            "    		this.chooser.hideCalendar();\r\n"+
            "    }\r\n"+
            "    this.handleTab(null,e);\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setHidden=function(hidden){\r\n"+
            "	this.element.style.position=hidden?\"static\":\"absolute\";\r\n"+
            "//	this.chooser.input.style.position = hidden?\"static\":\"absolute\";\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.initCalendar=function(isRange){\r\n"+
            "	var that=this;\r\n"+
            "	if(this.chooser!=null){\r\n"+
            "		if(this.chooser.isRange == isRange)\r\n"+
            "			return;\r\n"+
            "		this.chooser.hideCalendar();\r\n"+
            "	}\r\n"+
            "	this.chooser=new DateChooser(this.element,isRange);\r\n"+
            "    this.chooser.input.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "    this.chooser.input.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "	var changeFunc = function(){ that.onChangeCallback(); };\r\n"+
            "	var closeFunc = function() {\r\n"+
            "		that.chooser.input.focus();\r\n"+
            "	}\r\n"+
            "	var glassFunc = function(e) {\r\n"+
            "		// this is an onclick event\r\n"+
            "		if (that.chooser.rootDiv) {\r\n"+
            "			var isIn = isMouseInside(e, that.chooser.rootDiv,0);\r\n"+
            "			if (!isIn) {\r\n"+
            "	//			// clicked outside the glass or the field itself, hide calendar first\r\n"+
            "				that.chooser.hideCalendar();\r\n"+
            "				if (!isMouseInside(e, that.chooser.input, 0)) {\r\n"+
            "					// send blur to backend if user clicked anywhere outside of the field itself\r\n"+
            "					that.chooser.input.blur();\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "		} \r\n"+
            "	}\r\n"+
            "	var glassFunc2 = function(e) {\r\n"+
            "		if (!isMouseInside(e, that.timeInput.input, 0)) {\r\n"+
            "			// send blur to backend if user clicked anywhere outside of the field itself\r\n"+
            "			that.timeInput.input.onblur();\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	this.chooser.onChange=changeFunc;\r\n"+
            "	// need to tell backend this field is blurred\r\n"+
            "	this.chooser.handleCloseButton=closeFunc;\r\n"+
            "	this.chooser.onGlass=glassFunc;\r\n"+
            "	//Add inputs to inputList\r\n"+
            "	this.inputList = [];\r\n"+
            "	if(this.chooser.input){\r\n"+
            "		this.chooser.input.inputid = this.inputList.length;\r\n"+
            "		this.inputList.push(this.chooser.input);\r\n"+
            "        this.chooser.input.onfocus=function(e){ that.onFieldEvent(e,\"onFocus\"); };\r\n"+
            "        this.chooser.input.onblur=function(e){ \r\n"+
            "        	// this event has the highest priority (before any of our custom hooks are fired)\r\n"+
            "        	// js will call blur when user clicks on anywhere that is not on the date portion of the field\r\n"+
            "        	// if user clicked on a day, onClickDay will fire after this\r\n"+
            "        	if (that.chooser.rootDiv) {\r\n"+
            "        		// maintain focus if calendar still open\r\n"+
            "        		that.chooser.input.focus();\r\n"+
            "        	} else {\r\n"+
            "        		// field is focused, but no calendar\r\n"+
            "				that.onFieldEvent(e,\"onBlur\"); \r\n"+
            "        	}\r\n"+
            "		};\r\n"+
            "	    	\r\n"+
            "	}\r\n"+
            "	if(isRange && this.chooser.input2){\r\n"+
            "		this.chooser.input2.inputid = this.inputList.length;\r\n"+
            "		this.inputList.push(this.chooser.input2);\r\n"+
            "        this.chooser.input2.onfocus=function(e){ that.onFieldEvent(e,\"onFocus\"); };\r\n"+
            "        this.chooser.input2.onblur=function(e){ \r\n"+
            "        	if (that.chooser.rootDiv) {\r\n"+
            "        		that.chooser.input.focus();\r\n"+
            "        	} else {\r\n"+
            "				that.onFieldEvent(e,\"onBlur\"); \r\n"+
            "        	}\r\n"+
            "		};\r\n"+
            "		// ensure field is focused when clicking on dash\r\n"+
            "		this.chooser.onDashClicked=function(e){that.chooser.input.focus();};\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	if (this.chooser.isRange) {\r\n"+
            "		const cStyle=getComputedStyle(this.chooser.input);\r\n"+
            "		const tStyle=getComputedStyle(this.chooser.input2);\r\n"+
            "		const available=1.0*this.width-pxToInt(cStyle.paddingRight)-pxToInt(tStyle.paddingLeft)- this.chooser.dash.offsetWidth;\r\n"+
            "		\r\n"+
            "		this.chooser.input.style.width = toPx(roundDecimals(available*this.ratio,0));\r\n"+
            "		this.chooser.input2.style.width = toPx(roundDecimals(available*(1-this.ratio),0));\r\n"+
            "	} \r\n"+
            "	this.element.style.width=toPx(this.width);\r\n"+
            "	this.element.style.height=toPx(this.height);\r\n"+
            "\r\n"+
            "	this.chooser.setContainerSize(this.width,this.height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setValue=function(value){\r\n"+
            "  var startAndEnd=value.split(',');\r\n"+
            "  if(startAndEnd[0]!='null')\r\n"+
            "    this.chooser.setValue(startAndEnd[0]);\r\n"+
            "  else{\r\n"+
            "	  this.chooser.input.value=\"\";\r\n"+
            "	  \r\n"+
            "  }\r\n"+
            "  if(startAndEnd[1]!='null')\r\n"+
            "    this.chooser.setValue2(startAndEnd[1]);\r\n"+
            "  else{\r\n"+
            "	  if(this.chooser.input2)\r\n"+
            "	  this.chooser.input2.value=\"\";\r\n"+
            "	  \r\n"+
            "  }\r\n"+
            "};\r\n"+
            "DayChooserField.prototype.getValue=function(){\r\n"+
            "  return this.element.value;\r\n"+
            "};\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setColors=function(headerColor){\r\n"+
            "	this.chooser.setColors(headerColor);\r\n"+
            "};\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.style.pointerEvents= this.disabled?\"none\":null;\r\n"+
            "	if(this.chooser != null && this.chooser.input!= null)\r\n"+
            "		this.chooser.input.disabled=this.disabled;\r\n"+
            "}\r\n"+
            "DayChooserField.prototype.focusField=function(inputInd){\r\n"+
            "	if(this.chooser!=null)\r\n"+
            "		this.chooser.input.focus();\r\n"+
            "};\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setCalendarBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setCalendarBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setBtnBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setBtnBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setBtnFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setBtnFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setCalendarBtnFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setBtnFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setCalendarYearFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setYearFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DayChooserField.prototype.setCalendarSelYearFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setSelYearFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DayChooserField.prototype.setCalendarMonthFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setMonthFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DayChooserField.prototype.setCalendarSelMonthFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setSelMonthFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DayChooserField.prototype.setCalendarSelMonthBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setSelMonthBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DayChooserField.prototype.setCalendarWeekFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setWeekFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DayChooserField.prototype.setCalendarWeekBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setWeekBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DayChooserField.prototype.setCalendarDayFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setDayFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setCalendarXDayFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setXDayFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setCalendarHoverBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setHoverBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setEnableLastNDays=function(days) {\r\n"+
            "	if (this.chooser) {\r\n"+
            "		this.chooser.setEnableLastNDays(days);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "DayChooserField.prototype.setDisableFutureDays=function(flag) {\r\n"+
            "	if (this.chooser) {\r\n"+
            "		this.chooser.setDisableFutureDays(flag);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// TimeChooserField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "TimeChooserField.prototype=new FormField();\r\n"+
            "function TimeChooserField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	var that = this;\r\n"+
            "	this.element=nw(\"div\", \"timeformfield\");\r\n"+
            "    this.element.style.display = \"inline-block\";\r\n"+
            "    this.element.tabIndex=0;\r\n"+
            "//    this.element.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e);}\r\n"+
            "    this.element.onkeydown=function(e){that.onKeyDown(e);};\r\n"+
            "}\r\n"+
            "TimeChooserField.prototype.onChangeCallback=function(){\r\n"+
            "	var that = this;\r\n"+
            "	var start;\r\n"+
            "	var end;\r\n"+
            "	start = this.timeInput.getValue();\r\n"+
            "	if(that.isRange && that.timeInput2){\r\n"+
            "		end = this.timeInput2.getValue();\r\n"+
            "	}\r\n"+
            "	that.portlet.onChange(that.id,{start:start,end:end});\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeChooserField.prototype.setFieldStyle=function(style){\r\n"+
            "	if(this.timeInput){\r\n"+
            "		this.timeInput.getInput().className=\"time_input\";\r\n"+
            "		applyStyle(this.timeInput.getInput(), style);\r\n"+
            "	}\r\n"+
            "	if(this.timeInput2){\r\n"+
            "		this.timeInput2.getInput().className=\"time_input\";\r\n"+
            "		applyStyle(this.timeInput2.getInput(), style);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeChooserField.prototype.onKeyDown=function(e){\r\n"+
            "    if(e.keyCode==9){\r\n"+
            "    	if(this.timeInput != null)\r\n"+
            "    		this.timeInput.timeChooser.hide();\r\n"+
            "    	if(this.timeInput2 != null)\r\n"+
            "    		this.timeInput2.timeChooser.hide();\r\n"+
            "    }\r\n"+
            "    if(e.keyCode==13 || e.keyCode==32){//enter of spacebar\r\n"+
            "    	if(this.timeInput != null)\r\n"+
            "    		this.timeInput.timeChooser.show();\r\n"+
            "	}\r\n"+
            "    this.handleTab(null,e);\r\n"+
            "}\r\n"+
            "TimeChooserField.prototype.setValue=function(value){");
          out.print(
            "\r\n"+
            "  var startAndEnd=value.split(',');\r\n"+
            "  if(startAndEnd[0]!='null'){\r\n"+
            "	  var timeComp = parseInt(startAndEnd[0]);\r\n"+
            "	  if(this.timeInput)\r\n"+
            "	  this.timeInput.setValueLong(timeComp);\r\n"+
            "  }\r\n"+
            "  else {\r\n"+
            "	  if(this.timeInput){\r\n"+
            "		  this.timeInput.input.value=\"\";\r\n"+
            "	  }\r\n"+
            "  }\r\n"+
            "  if(startAndEnd[1]!='null'){\r\n"+
            "	  var timeComp = parseInt(startAndEnd[1]);\r\n"+
            "	  if(this.timeInput2)\r\n"+
            "	  this.timeInput2.setValueLong(timeComp);\r\n"+
            "  }\r\n"+
            "  else {\r\n"+
            "	  if(this.timeInput2){\r\n"+
            "		  this.timeInput2.input.value=\"\";\r\n"+
            "	  }\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeChooserField.prototype.getValue=function(){\r\n"+
            "	//TODO not needed or used\r\n"+
            "	return null;\r\n"+
            "	\r\n"+
            "}\r\n"+
            "TimeChooserField.prototype.initCalendar=function(isRange){\r\n"+
            "	this.isRange = isRange;\r\n"+
            "	var that = this;\r\n"+
            "	this.focusedInput = 0;\r\n"+
            "	var changeFunc = function(){ that.onChangeCallback(); };\r\n"+
            "	if(this.timeInput == null){\r\n"+
            "		this.timeInput = new TimeInput(this.element);\r\n"+
            "		this.timeInput.onChange = changeFunc;\r\n"+
            "	}\r\n"+
            "	if(this.timeInput2 == null && isRange){\r\n"+
            "		this.dash = nw(\"span\",\"cal_dash\")\r\n"+
            "		this.dash.innerHTML=' - '; \r\n"+
            "		this.element.appendChild(this.dash);\r\n"+
            "		this.timeInput2 = new TimeInput(this.element);\r\n"+
            "		this.timeInput2.onChange = changeFunc;\r\n"+
            "	}\r\n"+
            "	//Add inputs to inputList\r\n"+
            "	this.inputList = [];\r\n"+
            "	if(this.timeInput){\r\n"+
            "		var glassFunc = function(e) {\r\n"+
            "			// time widget is hidden already, so checking element here\r\n"+
            "			if (!isMouseInside(e, that.element,0)) {\r\n"+
            "				// click outside of field, let's execute onblur\r\n"+
            "				that.timeInput.input.onblur();\r\n"+
            "			}\r\n"+
            "		};\r\n"+
            "		this.timeInput.getInput().inputid = this.inputList.length;\r\n"+
            "		this.inputList.push(this.timeInput.getInput());\r\n"+
            "		this.timeInput.timeChooser.onGlass=glassFunc;\r\n"+
            "        this.timeInput.input.onfocus=function(e){\r\n"+
            "        	that.onFieldEvent(e,\"onFocus\");\r\n"+
            "    	};\r\n"+
            "        this.timeInput.input.onblur=function(e){\r\n"+
            "        	if (!that.timeInput.timeChooser.rootDiv) {\r\n"+
            "				that.onFieldEvent(e,\"onBlur\");\r\n"+
            "        	}\r\n"+
            "        };\r\n"+
            "	}\r\n"+
            "	if(isRange && this.timeInput2){\r\n"+
            "		var glassFunc2 = function(e) {\r\n"+
            "			if (!isMouseInside(e, that.element,0)) {\r\n"+
            "				// click outside of field, let's execute onblur\r\n"+
            "				that.timeInput2.input.onblur();\r\n"+
            "			}\r\n"+
            "		};\r\n"+
            "		this.timeInput2.getInput().inputid = this.inputList.length;\r\n"+
            "		this.inputList.push(this.timeInput2.getInput());\r\n"+
            "		this.timeInput2.timeChooser.onGlass=glassFunc2;\r\n"+
            "        this.timeInput2.input.onfocus=function(e){\r\n"+
            "        	that.focusedInput = 1;\r\n"+
            "        	that.onFieldEvent(e,\"onFocus\");\r\n"+
            "        };\r\n"+
            "        this.timeInput2.input.onblur=function(e){\r\n"+
            "        	if (!that.timeInput2.timeChooser.rootDiv)  {\r\n"+
            "        		that.onFieldEvent(e,\"onBlur\");\r\n"+
            "        	}\r\n"+
            "        };\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "TimeChooserField.prototype.onFieldSizeChanged=function(){\r\n"+
            "  this.element.style.width=toPx(this.width);\r\n"+
            "  this.element.style.height=toPx(this.height);\r\n"+
            "  if(this.isRange == false){\r\n"+
            "	  var input = this.timeInput.getInput();\r\n"+
            "	  input.style.width=toPx(this.width);\r\n"+
            "	  input.style.height=toPx(this.height);\r\n"+
            "  }\r\n"+
            "  else{\r\n"+
            "	  var inputWidth = this.width/2 - 6;\r\n"+
            "	  var input = this.timeInput.getInput();\r\n"+
            "	  input.style.width=toPx(inputWidth);\r\n"+
            "	  input.style.height=toPx(this.height);\r\n"+
            "	  \r\n"+
            "	  var input2 = this.timeInput2.getInput();\r\n"+
            "	  input2.style.width=toPx(inputWidth);\r\n"+
            "	  input2.style.height=toPx(this.height);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeChooserField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.style.pointerEvents= this.disabled?\"none\":null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeChooserField.prototype.setHidden=function(hidden){\r\n"+
            "	this.element.style.position=hidden?\"static\":\"absolute\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeChooserField.prototype.focusField=function(inputInd){\r\n"+
            "	// we already know which one is focused, and we store it in this.focusedInput\r\n"+
            "	if (this.timeInput != null)\r\n"+
            "		this.timeInput.input.focus();\r\n"+
            "};\r\n"+
            "\r\n"+
            "TimeChooserField.prototype.setTimeDisplayFormat=function(format){\r\n"+
            "	if (this.timeInput) {\r\n"+
            "		this.timeInput.setTimeDisplayFormat(format);\r\n"+
            "	}\r\n"+
            "	if (this.timeInput2) {\r\n"+
            "		this.timeInput2.setTimeDisplayFormat(format);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// DateTimeChooserField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function DateTimeChooserField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	var that = this;\r\n"+
            "    this.element=nw('div',\"dateformfield\");\r\n"+
            "    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};\r\n"+
            "    this.element.style.display = \"inline-block\";\r\n"+
            "    this.element.tabIndex=0;\r\n"+
            "//  this.element.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e);}\r\n"+
            "    this.element.onkeydown=function(e){that.onKeyDown(e);};\r\n"+
            "    // track changes\r\n"+
            "    this.prevDate=null;\r\n"+
            "    this.prevTime=null;\r\n"+
            "    this.ratio=0.65; // TODO make this a user setting in editor\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.onChangeCallback=function(){\r\n"+
            "	if(this.isRange)\r\n"+
            "		return;\r\n"+
            "	var that = this;\r\n"+
            "	var date = this.chooser.getValues();\r\n"+
            "	var time = this.timeInput.getValues(); // returns an object containing time broken down to hour,min,sec,ms\r\n"+
            "	var timeV =this.timeInput.getValue(); // returns a single value that is time in ms\r\n"+
            "	// no change, no op\r\n"+
            "	if(date == this.prevDate && timeV == this.prevTime)\r\n"+
            "		return;\r\n"+
            "	this.prevDate=date;\r\n"+
            "	this.prevTime=timeV;\r\n"+
            "	var m = {};\r\n"+
            "	if (date==null || timeV==null) {\r\n"+
            "		// back end needs both date and time to calculate the correct ms, if we only have one, then don't save it\r\n"+
            "		m.clearAll=true;\r\n"+
            "	} else {\r\n"+
            "		m.s_yy = date.year;\r\n"+
            "		m.s_MM = date.month;\r\n"+
            "		m.s_dd = date.day;\r\n"+
            "		\r\n"+
            "		m.s_HH = time.hours;\r\n"+
            "		m.s_mm = time.minutes;\r\n"+
            "		m.s_ss = time.seconds;\r\n"+
            "		m.s_SSS = time.millis;\r\n"+
            "	}\r\n"+
            "	that.portlet.onChange(that.id,m);\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setFieldStyle=function(style){\r\n"+
            "	\r\n"+
            "//	applyStyle(this.element,style);\r\n"+
            "	if(this.chooser){\r\n"+
            "		this.chooser.input.className=\"cal_input\";\r\n"+
            "		applyStyle(this.chooser.input,style);\r\n"+
            "		if(this.chooser.input2){\r\n"+
            "			this.chooser.input2.className=\"cal_input\";\r\n"+
            "			applyStyle(this.chooser.input2,style);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	if(this.timeInput){\r\n"+
            "		this.timeInput.getInput().className=\"time_input\";\r\n"+
            "		applyStyle(this.timeInput.getInput(), style);\r\n"+
            "	}\r\n"+
            "	// TODO this.timeInput2 is deprecated?\r\n"+
            "	if(this.isRange && this.timeInput2){\r\n"+
            "		this.timeInput2.getInput().className=\"time_input\";\r\n"+
            "		applyStyle(this.timeInput2.getInput(), style);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.setDateDisplayFormat=function(format){\r\n"+
            "	if (this.chooser){\r\n"+
            "		this.chooser.setDateDisplayFormat(format);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.setTimeDisplayFormat=function(format) {\r\n"+
            "	if (this.timeInput) {\r\n"+
            "		this.timeInput.setTimeDisplayFormat(format);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.onKeyDown=function(e){\r\n"+
            "    if(e.keyCode==9){\r\n"+
            "    	if(this.chooser != null)\r\n"+
            "    		this.chooser.hideCalendar();\r\n"+
            "    	if(this.timeInput != null)\r\n"+
            "    		this.timeInput.timeChooser.hide();\r\n"+
            "    }\r\n"+
            "    this.handleTab(null,e);\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setHidden=function(hidden){\r\n"+
            "	this.element.style.position=hidden?\"static\":\"absolute\";\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.initCalendar=function(isRange){\r\n"+
            "	var that=this;\r\n"+
            "	if(this.chooser!=null){\r\n"+
            "		if(this.chooser.isRange == isRange)\r\n"+
            "			return;\r\n"+
            "		this.chooser.hideCalendar();\r\n"+
            "	}\r\n"+
            "	this.chooser=new DateChooser(this.element,isRange);\r\n"+
            "	\r\n"+
            "	var changeFunc = function(){ that.onChangeCallback(); };\r\n"+
            "	var closeFunc = function() {\r\n"+
            "		that.chooser.input.focus();\r\n"+
            "	}\r\n"+
            "	var glassFunc = function(e) {\r\n"+
            "		// this is an onclick event\r\n"+
            "		if (that.chooser.rootDiv) {\r\n"+
            "			var isIn = isMouseInside(e, that.chooser.rootDiv,0);\r\n"+
            "			if (!isIn) {\r\n"+
            "	//			// clicked outside the glass or the field itself, hide calendar first\r\n"+
            "				that.chooser.hideCalendar();\r\n"+
            "				if (!isMouseInside(e, that.chooser.input, 0)) {\r\n"+
            "					// send blur to backend if user clicked anywhere outside of the field itself\r\n"+
            "					that.chooser.input.blur();\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "		} \r\n"+
            "	}\r\n"+
            "	var glassFunc2 = function(e) {\r\n"+
            "		if (!isMouseInside(e, that.timeInput.input, 0)) {\r\n"+
            "			// send blur to backend if user clicked anywhere outside of the field itself\r\n"+
            "			that.timeInput.input.onblur();\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	this.timeInput = new TimeInput(this.element);\r\n"+
            "	this.chooser.onChange=changeFunc;\r\n"+
            "	// need to tell backend this field is blurred\r\n"+
            "	this.chooser.handleCloseButton=closeFunc;\r\n"+
            "	this.chooser.onGlass=glassFunc;\r\n"+
            "	this.timeInput.timeChooser.onGlass = glassFunc2;\r\n"+
            "	this.timeInput.onChange = changeFunc;\r\n"+
            "	//Add inputs to inputList\r\n"+
            "	this.inputList = [];\r\n"+
            "	if(this.chooser){\r\n"+
            "		this.chooser.input.inputid = this.inputList.length;\r\n"+
            "		this.inputList.push(this.chooser.input);\r\n"+
            "        this.chooser.input.onfocus=function(e){ that.onFieldEvent(e,\"onFocus\"); };\r\n"+
            "        this.chooser.input.onblur=function(e){ \r\n"+
            "        	// this event has the highest priority (before any of our custom hooks are fired)\r\n"+
            "        	// js will call blur when user clicks on anywhere that is not on the date portion of the field\r\n"+
            "        	// if user clicked on a day, onClickDay will fire after this\r\n"+
            "        	if (that.chooser.rootDiv) {\r\n"+
            "        		// maintain focus if calendar still open\r\n"+
            "        		that.chooser.input.focus();\r\n"+
            "        	} else {\r\n"+
            "        		// field is focused, but no calendar\r\n"+
            "				that.onFieldEvent(e,\"onBlur\"); \r\n"+
            "        	}\r\n"+
            "		};\r\n"+
            "	}\r\n"+
            "	if(this.timeInput){\r\n"+
            "		this.timeInput.getInput().inputid = this.inputList.length;\r\n"+
            "		this.inputList.push(this.timeInput.getInput());\r\n"+
            "        this.timeInput.input.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "        this.timeInput.input.onblur=function(e){if (that.timeInput.timeChooser.rootDiv == null){ that.onFieldEvent(e,\"onBlur\");}};\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	const cStyle=getComputedStyle(this.chooser.input);\r\n"+
            "	const tStyle=getComputedStyle(this.timeInput.input);\r\n"+
            "	const available=this.width-pxToInt(cStyle.paddingLeft)-pxToInt(cStyle.paddingRight)-pxToInt(tStyle.paddingLeft)-pxToInt(tStyle.paddingRight);\r\n"+
            "	\r\n"+
            "	this.chooser.input.style.width = toPx(available*this.ratio);\r\n"+
            "	this.timeInpu");
          out.print(
            "t.input.style.width = toPx(available*(1.0-this.ratio));\r\n"+
            "	this.element.style.width=toPx(this.width);\r\n"+
            "	this.element.style.height=toPx(this.height);\r\n"+
            "	this.chooser.input.style.height=toPx(this.height);\r\n"+
            "	if(this.timeInput){\r\n"+
            "	  this.timeInput.getInput().style.height=toPx(this.height);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setValue=function(value){\r\n"+
            "  var startAndEnd=value.split(',');\r\n"+
            "  var startDate = startAndEnd[0];\r\n"+
            "  var startTime = startAndEnd[1];\r\n"+
            "  if(startDate != 'null' && startTime != 'null'){\r\n"+
            "	  this.chooser.parseInput(startDate,1);\r\n"+
            "	  this.timeInput.setValueLong(parseInt(startTime));\r\n"+
            "  }\r\n"+
            "  else{\r\n"+
            "	 this.chooser.input.value=\"\"; \r\n"+
            "	 this.timeInput.input.value=\"\";\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "//	debugger;\r\n"+
            "//  var startAndEnd=value.split(',');\r\n"+
            "//  if(startAndEnd[0]!='null'){\r\n"+
            "//	  var dt = new Date(parseInt(startAndEnd[0]));\r\n"+
            "//	  var yyyyMMdd = this.chooser.toYmd(dt.getFullYear(), dt.getMonth()+1, dt.getDate());\r\n"+
            "//	  this.chooser.setValue(yyyyMMdd);\r\n"+
            "//	  var timeComp = parseInt(startAndEnd[0]);\r\n"+
            "//	  this.timeInput.setValueUTC(timeComp);\r\n"+
            "//  }\r\n"+
            "//  if(startAndEnd[1]!='null'){\r\n"+
            "//	  var dt = new Date(parseInt(startAndEnd[1]));\r\n"+
            "//	  var yyyyMMdd = this.chooser.toYmd(dt.getFullYear(), dt.getMonth()+1, dt.getDate());\r\n"+
            "//	  this.chooser.setValue2(yyyyMMdd);\r\n"+
            "//	  var timeComp = parseInt(startAndEnd[1]);\r\n"+
            "//	  this.timeInput.setValueUTC(timeComp);\r\n"+
            "//  }\r\n"+
            "};\r\n"+
            "DateTimeChooserField.prototype.getValue=function(){\r\n"+
            "	//TODO not used\r\n"+
            "	return null;\r\n"+
            "};\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setColors=function(headerColor){\r\n"+
            "	this.chooser.setColors(headerColor);\r\n"+
            "};\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.style.pointerEvents= this.disabled?\"none\":null;\r\n"+
            "	if(this.chooser != null && this.chooser.input!= null)\r\n"+
            "		this.chooser.input.disabled=this.disabled;\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.focusField=function(inputInd){\r\n"+
            "	if (this.chooser != null)\r\n"+
            "		this.chooser.input.focus();\r\n"+
            "};\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setCalendarBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setCalendarBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setBtnBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setBtnBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setBtnFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setBtnFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setCalendarBtnFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setBtnFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setCalendarYearFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setYearFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.setCalendarSelYearFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setSelYearFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.setCalendarMonthFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setMonthFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.setCalendarSelMonthFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setSelMonthFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.setCalendarSelMonthBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setSelMonthBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.setCalendarWeekFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setWeekFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.setCalendarWeekBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setWeekBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateTimeChooserField.prototype.setCalendarDayFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setDayFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setCalendarXDayFgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setXDayFgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setCalendarHoverBgColor=function(color) {\r\n"+
            "	if (this.chooser) {		\r\n"+
            "		this.chooser.setHoverBgColor(color);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setEnableLastNDays=function(days) {\r\n"+
            "	if (this.chooser) {\r\n"+
            "		this.chooser.setEnableLastNDays(days);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "DateTimeChooserField.prototype.setDisableFutureDays=function(flag) {\r\n"+
            "	if (this.chooser) {\r\n"+
            "		this.chooser.setDisableFutureDays(flag);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// NumericRangeField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "NumericRangeField.prototype=new FormField();\r\n"+
            "// This is the slider field in the GUI\r\n"+
            "function NumericRangeField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "    this.element=nw('div', \"\");\r\n"+
            "    this.element.style.display = \"inline-block\";\r\n"+
            "    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};\r\n"+
            "    this.element.onkeydown=function(e){that.onKeyDown(e);};\r\n"+
            "    var that=this;\r\n"+
            "}\r\n"+
            "\r\n"+
            "NumericRangeField.prototype.setFieldStyle=function(style){\r\n"+
            "  this.element.className=\"\";\r\n"+
            "  applyStyle(this.element,style);\r\n"+
            "}\r\n"+
            "NumericRangeField.prototype.setHidden=function(hidden){\r\n"+
            "	this.element.style.position=hidden?\"static\":\"absolute\";\r\n"+
            "	this.hidden=hidden;\r\n"+
            "}\r\n"+
            "NumericRangeField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.element.style.pointerEvents= disabled? \"none\":\"auto\";\r\n"+
            "	this.disabled=disabled;\r\n"+
            "}\r\n"+
            "\r\n"+
            "NumericRangeField.prototype.applySliderStyles=function(style,sliderValFontColor,sliderValBgColor,sliderValBorderColor,gripColor,leftTrackColor,trackColor,sliderValBorderRadius,sliderValBorderWidth,sliderValFontFam) {\r\n"+
            "	applyStyle(this.slider.lowguide,\"_bg=\"+leftTrackColor);\r\n"+
            "    applyStyle(this.slider.guide, \"_bg=\"+trackColor);\r\n"+
            "    applyStyle(this.slider.grabber, \"_bg=\"+gripColor);\r\n"+
            "    applyStyle(this.slider.grabber2, \"_bg=\"+gripColor);\r\n"+
            "    applyStyle(this.slider.val, \"_fg=\"+sliderValFontColor);\r\n"+
            "    applyStyle(this.slider.val, \"_bg=\"+sliderValBgColor);\r\n"+
            "    applyStyle(this.slider.val, \"style.borderColor=\"+sliderValBorderColor);\r\n"+
            "	applyStyle(this.slider.val, \"style.borderRadius=\"+ toPx(sliderValBorderRadius));\r\n"+
            "	applyStyle(this.slider.val, \"style.borderWidth=\"+ toPx(sliderValBorderWidth));\r\n"+
            "	applyStyle(this.slider.val, \"style.fontFamily=\"+sliderValFontFam);\r\n"+
            "	// below requires height and width scaling\r\n"+
            "//	applyStyle(this.slider.val, \"style.fontSize=\"+toPx(sliderValFontSize));\r\n"+
            "    applyStyle(this.slider.input,style);\r\n"+
            "    applyStyle(this.element,style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "NumericRangeField.prototype.initSlider=function(value,minVal,maxVal,precision,step,width,textHidden, sliderHidden, nullable,defaultValue){\r\n"+
            "  if(this.slider!=null){\r\n"+
            "	this.slider.setRange(minVal,maxVal,step,precision);\r\n"+
            "	this.slider.setValue(value,true,true,null);\r\n"+
            "	this.slider.resize(this.width,textHidden);\r\n"+
            "	this.slider.setSliderHidden(sliderHidden);\r\n"+
            "	return;\r\n"+
            "  }\r\n"+
            "  var that=this;\r\n"+
            "  this.slider=new Slider(this.element,value,minVal,maxVal,width,precision,step,textHidden,null,sliderHidden, nullable,defaultValue, null);\r\n"+
            "  this.slider.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e);};\r\n"+
            "  this.slider.val.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "  this.slider.val.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "  this.slider.onValueChanged=function(rawValue,value,fromSliding){\r\n"+
            "  	if(fromSliding){\r\n"+
            "        if(that.movingInterval==null)\r\n"+
            "  	    that.movingInterval=window.setInterval(function(){that.checkMovementInterval();},200);\r\n"+
            "  	  that.isMoving=true;\r\n"+
            "  	}else{\r\n"+
            "        if(that.movingInterval!=null){\r\n"+
            "	        window.clearInterval(that.movingInterval);\r\n"+
            "          that.movingInterval=null;\r\n"+
            "        }\r\n"+
            "        that.portlet.onChange(that.id,{value:that.slider.getValue()});\r\n"+
            "  	  that.isMoving=false;\r\n"+
            "  	}\r\n"+
            "  };\r\n"+
            "	this.slider.onNullableChanged=function(_value){\r\n"+
            "	  	that.portlet.onChange(that.id, {value: _value});\r\n"+
            "	};\r\n"+
            "  \r\n"+
            "\r\n"+
            "    this.slider.val.fieldId = this.id;\r\n"+
            "    this.slider.val2.fieldId = this.id;\r\n"+
            "    this.slider.lowguide.fieldId = this.id;\r\n"+
            "    this.slider.guide.fieldId = this.id;\r\n"+
            "    this.slider.grabber.fieldId = this.id;\r\n"+
            "    this.slider.grabber2.fieldId = this.id;\r\n"+
            "    this.slider.resize(this.width,textHidden);\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	if(this.slider != null && this.element!= null){\r\n"+
            "	this.slider.setWidth(this.width);\r\n"+
            "	this.slider.setHeight(this.height);\r\n"+
            "	this.element.style.width=toPx(this.width);\r\n"+
            "	this.element.style.height=toPx(this.height);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeField.prototype.checkMovementInterval=function(){\r\n"+
            "	if(this.isMoving){\r\n"+
            "		this.isMoving=false;\r\n"+
            "	}else{\r\n"+
            "      this.portlet.onChange(this.id,{value:this.slider.getValue()});\r\n"+
            "	  window.clearInterval(this.movingInterval);\r\n"+
            "      this.movingInterval=null;\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeField.prototype.setValue=function(value){\r\n"+
            "  this.slider.setValue(value,true,false);\r\n"+
            "  this.element.value=value;\r\n"+
            "};\r\n"+
            "NumericRangeField.prototype.getValue=function(){\r\n"+
            "	  return this.slider.getValue();\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.style.pointerEvents= this.disabled?\"none\":null;\r\n"+
            "	if(this.slider!= null && this.slider.val != null)\r\n"+
            "		this.slider.val.disabled = this.disabled;\r\n"+
            "}\r\n"+
            "NumericRangeField.prototype.focusField=function(){\r\n"+
            "	if(this.slider!= null && this.slider.val != null){\r\n"+
            "		this.slider.val.focus();\r\n"+
            "	   return true;\r\n"+
            "	}\r\n"+
            "	return false;\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeField.prototype.onKeyDown=function(e){\r\n"+
            "	if(e.keyCode==9)\r\n"+
            "		return this.form.onTab(this,e);\r\n"+
            "	var that = this;\r\n"+
            "	if(e.keyCode==13 || (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){\r\n"+
            "		var pos=getCursorPosition(this.element);\r\n"+
            "		this.portlet.onCustom(this.id, \"updateCursor\", {pos:pos});\r\n"+
            "	    this.portlet.onKey(this.id,e,{pos:pos});\r\n"+
            "	    if(e.keyCode==9)\r\n"+
            "	      e.preventDefault();\r\n"+
            "}\r\n"+
            "}\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// NumericRangeSubRangeField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "NumericRangeSubRangeField.prototyp");
          out.print(
            "e=new FormField();\r\n"+
            "// This is the range field in the GUI\r\n"+
            "function NumericRangeSubRangeField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "    this.element=nw('div');    \r\n"+
            "    this.element.style.display = \"inline-block\";\r\n"+
            "	var that=this;\r\n"+
            "    this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};\r\n"+
            "    this.element.onkeydown=function(e){that.onKeyDown(e);};\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "NumericRangeSubRangeField.prototype.setFieldStyle=function(style){\r\n"+
            "  this.element.className=\"\";\r\n"+
            "  applyStyle(this.element,style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "/*\r\n"+
            "NumericRangeSubRangeField.prototype.onKeyDown=function(e){\r\n"+
            "    this.handleTab(null,e);\r\n"+
            "}\r\n"+
            "*/\r\n"+
            "\r\n"+
            "NumericRangeSubRangeField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.style.pointerEvents= disabled? \"none\":\"auto\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "NumericRangeSubRangeField.prototype.applySliderStyles=function(style,sliderValFontColor,sliderValBgColor,sliderValBorderColor,gripColor,trackColor,leftTrackColor,sliderValBorderRadius,sliderValBorderWidth,sliderValFontFam) {\r\n"+
            "	applyStyle(this.slider.lowguide,\"_bg=\"+trackColor);\r\n"+
            "    applyStyle(this.slider.guide, \"_bg=\"+trackColor);\r\n"+
            "    applyStyle(this.slider.grabber, \"_bg=\"+gripColor);\r\n"+
            "    applyStyle(this.slider.grabber2, \"_bg=\"+gripColor);\r\n"+
            "    applyStyle(this.slider.val, \"_fg=\"+sliderValFontColor);\r\n"+
            "    applyStyle(this.slider.val2, \"_fg=\"+sliderValFontColor);\r\n"+
            "    applyStyle(this.slider.val, \"_bg=\"+sliderValBgColor);\r\n"+
            "    applyStyle(this.slider.val2, \"_bg=\"+sliderValBgColor);\r\n"+
            "    applyStyle(this.slider.val, \"style.borderColor=\"+sliderValBorderColor);\r\n"+
            "    applyStyle(this.slider.val2, \"style.borderColor=\"+sliderValBorderColor);\r\n"+
            "	applyStyle(this.slider.val, \"style.borderRadius=\"+ toPx(sliderValBorderRadius));\r\n"+
            "	applyStyle(this.slider.val2, \"style.borderRadius=\"+toPx(sliderValBorderRadius));\r\n"+
            "	applyStyle(this.slider.val, \"style.borderWidth=\"+ toPx(sliderValBorderWidth));\r\n"+
            "	applyStyle(this.slider.val2, \"style.borderWidth=\"+toPx(sliderValBorderWidth));\r\n"+
            "	applyStyle(this.slider.val, \"style.fontFamily=\"+sliderValFontFam);\r\n"+
            "	applyStyle(this.slider.val2, \"style.fontFamily=\"+sliderValFontFam);\r\n"+
            "	// below requires height and width scaling\r\n"+
            "//	applyStyle(this.slider.val, \"style.fontSize=\"+toPx(sliderValFontSize));\r\n"+
            "//	applyStyle(this.slider.val2, \"style.fontSize=\"+toPx(sliderValFontSize));\r\n"+
            "    applyStyle(this.slider.input,style);\r\n"+
            "    applyStyle(this.element,style);\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeSubRangeField.prototype.initSliders=function(value,value2,minVal,maxVal,width,precision,step,textHidden, sliderHidden, nullable, defaultValue, defaultValue2) {\r\n"+
            "	if (this.slider != null) {\r\n"+
            "		this.slider.setRange(minVal,maxVal,step,precision);\r\n"+
            "	    this.slider.setValue(value,true,true,value2);\r\n"+
            "	    this.slider.resize(this.width,textHidden);\r\n"+
            "	    return;\r\n"+
            "	}\r\n"+
            "	// init\r\n"+
            "	var that=this;\r\n"+
            "	this.slider=new Slider(this.element,value,minVal,maxVal,width,precision,step,textHidden,value2, sliderHidden, nullable, defaultValue, defaultValue2);\r\n"+
            "	this.slider.onValueChanged=function(rawValue,value,fromSliding){\r\n"+
            "		if(that.movingInterval==null)\r\n"+
            "		  that.movingInterval=window.setInterval(function(){that.checkMovementInterval();},200);\r\n"+
            "		that.isMoving=true;\r\n"+
            "	};\r\n"+
            "	this.slider.onNullableChanged=function(_value, _value2){\r\n"+
            "		  that.portlet.onChange(that.id, {value: _value, value2:_value2})\r\n"+
            "	};\r\n"+
            "	this.slider.val.fieldId = this.id;\r\n"+
            "	this.slider.val2.fieldId = this.id;\r\n"+
            "	this.slider.lowguide.fieldId = this.id;\r\n"+
            "	this.slider.guide.fieldId = this.id;\r\n"+
            "	this.slider.grabber.fieldId = this.id;\r\n"+
            "	this.slider.grabber2.fieldId = this.id;\r\n"+
            "    this.slider.val.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "    this.slider.val.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "    this.slider.val2.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "    this.slider.val2.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "	// prepare inputs\r\n"+
            "	this.inputList = [];\r\n"+
            "	this.slider.val.inputid = this.inputList.length;\r\n"+
            "	this.inputList.push(this.slider.val);\r\n"+
            "	this.slider.val2.inputid = this.inputList.length;\r\n"+
            "	this.inputList.push(this.slider.val2);\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeSubRangeField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	this.slider.setWidth(this.width);\r\n"+
            "	this.slider.setHeight(this.height);\r\n"+
            "	this.element.style.width=toPx(this.width);\r\n"+
            "	this.element.style.height=toPx(this.height);\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeSubRangeField.prototype.checkMovementInterval=function(){\r\n"+
            "	if(this.isMoving){\r\n"+
            "		this.isMoving=false;\r\n"+
            "	}else{\r\n"+
            "      this.portlet.onChange(this.id,{value:this.slider.getValue(),value2:this.slider.getValue2()});\r\n"+
            "	  window.clearInterval(this.movingInterval);\r\n"+
            "      this.movingInterval=null;\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "NumericRangeSubRangeField.prototype.setValue=function(value){\r\n"+
            "	  var startAndEnd=value.split(',');\r\n"+
            "	  this.slider.setValue(startAndEnd[0], true, false, startAndEnd[1]);\r\n"+
            "};\r\n"+
            "NumericRangeSubRangeField.prototype.getValue=function(){\r\n"+
            "  return this.slider.minVal + \",\" + this.slider.getMaxValue();\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeSubRangeField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.style.pointerEvents= this.disabled?\"none\":null;\r\n"+
            "	if(this.slider!= null){\r\n"+
            "		if(this.slider.val != null)\r\n"+
            "			this.slider.val.disabled = this.disabled;\r\n"+
            "		if(this.slider.val2 != null)\r\n"+
            "			this.slider.val2.disabled = this.disabled;\r\n"+
            "	} \r\n"+
            "}\r\n"+
            "NumericRangeSubRangeField.prototype.focusField=function(inputInd){\r\n"+
            "	if(inputInd != null){\r\n"+
            "		this.inputList[inputInd].focus();\r\n"+
            "		return true;\r\n"+
            "	}\r\n"+
            "	else if(this.slider!= null && this.slider.val != null){\r\n"+
            "		this.slider.val.focus();\r\n"+
            "	   return true;\r\n"+
            "	}\r\n"+
            "	return false;\r\n"+
            "};\r\n"+
            "\r\n"+
            "NumericRangeSubRangeField.prototype.onKeyDown=function(e){\r\n"+
            "	if(e.keyCode==9)\r\n"+
            "		return this.form.onTab(this,e);\r\n"+
            "	var that = this;\r\n"+
            "	if(e.keyCode==13 || (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){\r\n"+
            "		var pos=getCursorPosition(this.element);\r\n"+
            "		this.portlet.onCustom(this.id, \"updateCursor\", {pos:pos});\r\n"+
            "	    this.portlet.onKey(this.id,e,{pos:pos});\r\n"+
            "	    if(e.keyCode==9)\r\n"+
            "	      e.preventDefault();\r\n"+
            "}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// TextAreaField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "TextAreaField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function TextAreaField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	this.input=nw('textarea');\r\n"+
            "	this.input.style.resize='none';\r\n"+
            "    this.input.style.verticalAlign='middle';\r\n"+
            "    this.input.style.left='0px';\r\n"+
            "    this.element=nw('div');\r\n"+
            "    this.element.style.display=\"inline-block\";\r\n"+
            "    this.element.appendChild(this.input);\r\n"+
            "\r\n"+
            "	var that=this;\r\n"+
            "	this.oldValue= \"\";\r\n"+
            "    this.input.onblur=function(){that.onChangeDiff();};\r\n"+
            "    this.input.oninput=function(){that.onChangeDiff();};\r\n"+
            "    this.input.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.element));};\r\n"+
            "    this.input.onkeydown=function(e){that.onKeyDown(e);};\r\n"+
            "    this.input.onfocus=function(e){that.onFieldEvent(e, \"onFocus\");};\r\n"+
            "    this.input.onblur=function(e){that.onFieldEvent(e, \"onBlur\");};\r\n"+
            "}\r\n"+
            "TextAreaField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	if(this.input)\r\n"+
            "		this.input.disabled=this.disabled;\r\n"+
            "}\r\n"+
            "TextAreaField.prototype.setHidden=function(hidden){\r\n"+
            "	if(hidden){\r\n"+
            "		this.input.style.display=\"inline-block\";\r\n"+
            "		this.input.style.width=\"auto\";\r\n"+
            "		this.input.style.height=\"auto\";\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.input.style.display=\"block\";\r\n"+
            "		this.input.style.width=\"100%\";\r\n"+
            "		this.input.style.height=\"100%\";\r\n"+
            "	}\r\n"+
            "	this.input.style.position=\"static\";\r\n"+
            "	this.element.style.position=hidden?\"static\":\"absolute\";\r\n"+
            "}\r\n"+
            "TextAreaField.prototype.onChangeDiff=function(){\r\n"+
            "	var newValue = this.getValue();\r\n"+
            "	var change = strDiff(this.oldValue, newValue);\r\n"+
            "	this.oldValue = newValue;\r\n"+
            "	this.portlet.onChange(this.id, {c:change.c,s:change.s,e:change.e,mid:this.getModificationNumber()});\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextAreaField.prototype.onKeyDown=function(e){\r\n"+
            "	if(e.keyCode==9)\r\n"+
            "		return this.form.onTab(this,e);\r\n"+
            "	var that = this;\r\n"+
            "	if(e.keyCode==13 || (this.callbackKeys!=null && this.callbackKeys.indexOf(e.keyCode) != -1)){\r\n"+
            "		var pos=getCursorPosition(this.element);\r\n"+
            "		this.portlet.onCustom(this.id, \"updateCursor\", {pos:pos});\r\n"+
            "	    this.portlet.onKey(this.id,e,{pos:pos});\r\n"+
            "	    if(e.keyCode==9)//TAB\r\n"+
            "	      e.preventDefault();\r\n"+
            "	}\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextAreaField.prototype.setFieldStyle=function(style){\r\n"+
            "  this.input.className=\"textarea\";\r\n"+
            "  applyStyle(this.input,style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextAreaField.prototype.init=function(hasButton,callbackKeys){\r\n"+
            "  var that=this;\r\n"+
            "//  this.element.disabled=disabled;\r\n"+
            "  this.callbackKeys=callbackKeys;\r\n"+
            "  if(hasButton && this.button==null){\r\n"+
            "      this.button=nw('div','textfield_plusbutton');\r\n"+
            "      this.button.onmousedown=function(e){that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.input));};\r\n"+
            "      this.button.style.top='0px';\r\n"+
            "      this.button.style.left='94.5%';\r\n"+
            "      this.button.style.height='25px';\r\n"+
            "      this.button.style.width='25px';\r\n"+
            "      this.element.appendChild(this.button);\r\n"+
            "  }else if(!hasButton && this.button!=null){\r\n"+
            "      this.element.removeChild(this.button);\r\n"+
            "	  this.button=null;\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  if (hasButton) {\r\n"+
            "	  this.input.style.width = \"calc(100% - 25px)\";\r\n"+
            "  }else\r\n"+
            "	  this.input.style.width = \"100%\";\r\n"+
            "}\r\n"+
            "TextAreaField.prototype.onFieldSizeChanged=function(){\r\n"+
            "  this.element.style.width=toPx(this.width);\r\n"+
            "  this.element.style.height=toPx(this.height);\r\n"+
            "}\r\n"+
            "TextAreaField.prototype.setValue=function(value){\r\n"+
            "  if (value == null){\r\n"+
            "	  this.input.value=\"\";\r\n"+
            "	  this.oldValue = \"\";\r\n"+
            "  }else{\r\n"+
            "	  this.input.value=value;\r\n"+
            "	  this.oldValue = value;\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "TextAreaField.prototype.getValue=function(){\r\n"+
            "	return this.input.value;\r\n"+
            "};\r\n"+
            "TextAreaField.prototype.moveCursor=function(cursorPos){\r\n"+
            "  setCursorPosition(this.input,cursorPos);\r\n"+
            "};\r\n"+
            "TextAreaField.prototype.changeSelection=function(start,end){\r\n"+
            "	setSelection(this.input,start,end);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TextAreaField.prototype.applyTextStyle = func");
          out.print(
            "tion(){\r\n"+
            "	this.saveRangePosition();\r\n"+
            "	this.initTextStyle();\r\n"+
            "	this.restoreRangePosition();\r\n"+
            "}\r\n"+
            "function getNodeIndex(n){var i=0;while(n=n.previousSibling)i++;return i}\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// CheckboxField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "CheckboxField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function CheckboxField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	this.wrapper=nw('div', 'ckFieldWrapper');\r\n"+
            "    this.element=nw('div', 'checkboxField');\r\n"+
            "    this.element.appendChild(this.wrapper);\r\n"+
            "   	this.checkMark = nw('span', 'ckMark');\r\n"+
            "   	this.checkUnicode = \"&#10004;\"\r\n"+
            "   	this.checkMark.innerHTML = this.checkUnicode;\r\n"+
            "   	this.wrapper.appendChild(this.checkMark);\r\n"+
            "    this.element.tabIndex=1;\r\n"+
            "    this.element.type='checkbox';\r\n"+
            "    this.input = this.element;\r\n"+
            "	var that=this;\r\n"+
            "    this.element.onchange=function(){that.portlet.onChange(that.id,{value:that.getValue()});};\r\n"+
            "    this.element.onmousedown=function(e){if(getMouseButton(e)==1)that.toggleChecked(); that.portlet.onChange(that.id, {value:that.getValue()}); if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,-1);};\r\n"+
            "    this.getLabelElement().style.pointerEvents = \"all\";\r\n"+
            "//    this.getLabelElement().onmousedown=function(e){if(getMouseButton(e)==1)that.toggleChecked();that.portlet.onChange(that.id,{value:that.getValue()});};\r\n"+
            "    this.input.onkeydown=function(e){if(e.keyCode==9)return that.form.onTab(that,e); else if(e.keyCode==13 || e.keyCode==32) {that.toggleChecked();that.portlet.onChange(that.id, {value:that.getValue()});}}\r\n"+
            "    this.input.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "    this.input.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "//    this.getLabelElement().onmouseover=function(e){if(getMouseButton(e)==1)that.element.checked = !that.element.checked};\r\n"+
            "//    this.element.onmouseover=function(e){if(getMouseButton(e)==1)that.element.checked = !that.element.checked};\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "CheckboxField.prototype.focusField=function(){\r\n"+
            "    this.element.focus();\r\n"+
            "    return  true;\r\n"+
            "};\r\n"+
            "\r\n"+
            "CheckboxField.prototype.setFieldStyle=function(style){\r\n"+
            "  this.input.className=\"checkboxField\";\r\n"+
            "  applyStyle(this.wrapper,style);\r\n"+
            "  if(style && style.indexOf(\"_fg\") != -1){\r\n"+
            "  	  var fgColor = getStyleValue(\"_fg\", style);\r\n"+
            "  	  this.checkMark.style.color = fgColor;\r\n"+
            "  }	\r\n"+
            "}\r\n"+
            "CheckboxField.prototype.setCssStyle=function(style){\r\n"+
            "	  this.input.className=\"checkboxField\";\r\n"+
            "	  applyStyle(this.input,style);\r\n"+
            "	}\r\n"+
            "CheckboxField.prototype.applyStyles=function(bgColor, checkColor, borderColor) {\r\n"+
            "  	this.wrapper.style.border = \"1px solid \" + borderColor;\r\n"+
            "  	this.wrapper.style.backgroundColor = bgColor;\r\n"+
            "  	this.checkMark.style.color = checkColor;\r\n"+
            "}\r\n"+
            "CheckboxField.prototype.init=function(bgColor, checkColor, borderColor){\r\n"+
            "	this.applyStyles(bgColor, checkColor, borderColor);\r\n"+
            "}\r\n"+
            "CheckboxField.prototype.toggleChecked=function(){\r\n"+
            "	if(this.disabled)\r\n"+
            "		return;\r\n"+
            "	this.value = !!!this.value;\r\n"+
            "	this.element.checked=this.value;\r\n"+
            "	if(this.value){\r\n"+
            "		this.element.classList.add(\"checked\");\r\n"+
            "		this.checkMark.innerHTML = this.checkUnicode;\r\n"+
            "	}else {\r\n"+
            "		this.element.classList.remove(\"checked\");\r\n"+
            "		this.checkMark.innerHTML = \"\";\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "CheckboxField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.disabled = disabled;\r\n"+
            "	this.element.style.pointerEvents = disabled?\"none\":\"all\";\r\n"+
            "	this.element.style.opacity = disabled?\"0.4\":\"1.0\";\r\n"+
            "	this.getLabelElement().style.opacity = disabled ?\"0.4\":\"1.0\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "CheckboxField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	this.element.style.height=toPx(this.height-6);\r\n"+
            "	this.element.style.width=toPx(this.width-6);\r\n"+
            "	var size = this.height < this.width ? this.height : this.width; \r\n"+
            "   	this.wrapper.style.width = toPx(size);\r\n"+
            "   	this.wrapper.style.height = toPx(size);\r\n"+
            "   	this.checkMark.style.fontSize = toPx(size - 5);\r\n"+
            "}\r\n"+
            "CheckboxField.prototype.setSize=function(width, height) {\r\n"+
            "	this.element.style.height=toPx(width);\r\n"+
            "	this.element.style.width=toPx(height);\r\n"+
            "   	this.wrapper.style.width = toPx(width);\r\n"+
            "   	this.wrapper.style.height = toPx(height);\r\n"+
            "   	this.checkMark.style.fontSize = toPx(width - 5);\r\n"+
            "}\r\n"+
            "CheckboxField.prototype.setValue=function(value){\r\n"+
            "	this.value = value==\"true\";\r\n"+
            "	this.element.checked=this.value;\r\n"+
            "	if(this.value){\r\n"+
            "		this.element.classList.add(\"checked\");\r\n"+
            "		this.checkMark.innerHTML = this.checkUnicode;\r\n"+
            "	}else {\r\n"+
            "		this.element.classList.remove(\"checked\");\r\n"+
            "		this.checkMark.innerHTML = \"\";\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "CheckboxField.prototype.getValue=function(){\r\n"+
            "  return this.value;\r\n"+
            "};\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "//RadioButtonField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "RadioButtonField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function RadioButtonField(portlet, id, title) {\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	this.element=nw('input', 'radioButtonField');\r\n"+
            "	this.element.tabIndex=1;\r\n"+
            "	this.element.type='radio';\r\n"+
            "  	this.element.style.margin='3px 0px 0px 0px';\r\n"+
            "	this.input = this.element;\r\n"+
            "	this.groupedRadios;\r\n"+
            "	this.groupId;\r\n"+
            "	var that=this;\r\n"+
            "	this.input.onchange=function(){\r\n"+
            "		that.value = that.input.checked;\r\n"+
            "		that.portlet.onChange(that.id,{value:that.input.checked});\r\n"+
            "		that.repaintRadios();\r\n"+
            "	};\r\n"+
            "	this.input.onkeydown=function(e){\r\n"+
            "		that.handleTab(null,e);	\r\n"+
            "	};\r\n"+
            "	this.element.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "	this.element.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "	this.getLabelElement().style.pointerEvents = \"all\";\r\n"+
            "}\r\n"+
            "function paintRadio(radioInput, bgColor, fontColor) {\r\n"+
            "	fontColor = radioInput.checked ? fontColor : \"transparent\";\r\n"+
            "	var radius = parseFloat(radioInput.style.height);\r\n"+
            "	const svgContent = [\r\n"+
            "		    '<svg width=\"200\" height=\"200\" xmlns=\"http://www.w3.org/2000/svg\">',\r\n"+
            "		      '<circle cx=\"100\" cy=\"100\" r=\"' + radius + '\" fill=\"' + bgColor + '\" />',\r\n"+
            "		      '<circle cx=\"100\" cy=\"100\" r=\"' + radius/5 + '\" fill=\"' + fontColor + '\" />',\r\n"+
            "		    '</svg>'\r\n"+
            "		  ].join('');\r\n"+
            "	const encodedSvg = encodeURIComponent(svgContent).replace(/'/g, '%27').replace(/\"/g, '%22');\r\n"+
            "	radioInput.style.backgroundImage = 'url(\"data:image/svg+xml,' + encodedSvg + '\")';\r\n"+
            "}\r\n"+
            "RadioButtonField.prototype.init=function(groupId){\r\n"+
            "	this.groupId = groupId;\r\n"+
            "	this.input.setAttribute(\"name\", groupId);\r\n"+
            "}\r\n"+
            "	\r\n"+
            "RadioButtonField.prototype.setRadioStyle=function(bgColor,fontColor,borderColor,borderWidth) {\r\n"+
            "	this.bgColor = bgColor;\r\n"+
            "	this.fontColor = fontColor;\r\n"+
            "	this.input.style.backgroundColor=bgColor;\r\n"+
            "	this.input.style.borderColor=borderColor;\r\n"+
            "	this.input.style.borderWidth=toPx(borderWidth);\r\n"+
            "	this.input.style.borderStyle=\"solid\";\r\n"+
            "	// each input has its own bgColor/font Color\r\n"+
            "	this.input.bgColor = bgColor;\r\n"+
            "	this.input.fontColor = fontColor;\r\n"+
            "	this.repaintRadios(this.input, this.bgColor, this.fontColor);\r\n"+
            "}\r\n"+
            "RadioButtonField.prototype.repaintRadios=function() {\r\n"+
            "	const radios = this.getRadiosByGroupName(this.groupId);\r\n"+
            "	for (var radioInput of radios)\r\n"+
            "		paintRadio(radioInput, radioInput.bgColor, radioInput.fontColor);\r\n"+
            "}\r\n"+
            "RadioButtonField.prototype.getRadiosByGroupName=function(groupId) {\r\n"+
            "	return document.getElementsByName(groupId);\r\n"+
            "}\r\n"+
            "RadioButtonField.prototype.focusField=function(){\r\n"+
            "	this.input.focus();\r\n"+
            "	return  true;\r\n"+
            "}\r\n"+
            "RadioButtonField.prototype.setFieldStyle=function(style){\r\n"+
            "	this.input.className=\"radioButtonField\";\r\n"+
            "	applyStyle(this.input,style);\r\n"+
            "	paintRadio(this.input, this.bgColor, this.fontColor);\r\n"+
            "	paintRadio(this.input, this.input.bgColor, this.input.fontColor);\r\n"+
            "	paintRadio(this.input, this.bgColor, this.fontColor);\r\n"+
            "	this.input.style.borderRadius='1000px';//force round\r\n"+
            "}\r\n"+
            "RadioButtonField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.disabled = disabled;\r\n"+
            "	this.element.style.pointerEvents = disabled?\"none\":\"all\";\r\n"+
            "	this.element.style.opacity = disabled?\"0.4\":\"none\";\r\n"+
            "	this.getLabelElement().style.opacity = disabled ?\"0.4\":\"none\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "RadioButtonField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	// handle negative\r\n"+
            "	var w=max(this.width-6,0);\r\n"+
            "	var h=max(this.height-6,0);\r\n"+
            "	size=min(w,h);\r\n"+
            "	this.element.style.height=toPx(size);\r\n"+
            "	this.element.style.width=toPx(size);\r\n"+
            "	// offset should be based on actual width/height\r\n"+
            "	this.element.style.top=toPx(max(0,(this.element.style.height-size)/2));\r\n"+
            "	this.element.style.left=toPx(max(0,(this.element.style.width-size)/2));\r\n"+
            "	this.element.style.borderRadius=toPx(size/2);\r\n"+
            "}\r\n"+
            "RadioButtonField.prototype.setValue=function(value){\r\n"+
            "	this.value = value == \"true\";\r\n"+
            "	this.input.checked = this.value;\r\n"+
            "	this.repaintRadios(this.input, this.input.bgColor, this.input.fontColor);\r\n"+
            "}\r\n"+
            "RadioButtonField.prototype.getValue=function(){\r\n"+
            "	return this.value;\r\n"+
            "}\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// FileUploadField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "FileUploadField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function FileUploadField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	var height=toPx(20);\r\n"+
            "	var that=this;\r\n"+
            "	this.el=that.portlet.portlet.divElement;\r\n"+
            "	this.element=nw2('div',null,that.el);\r\n"+
            "	\r\n"+
            "    this.uploadFileButtonElement=nw2('button',null,that.el);\r\n"+
            "    this.uploadUrlButtonElement=nw2('button',null,that.el);\r\n"+
            "    this.inputElement=nw2('div',null,that.el);\r\n"+
            "    this.inputElement.style.overflow='hidden';\r\n"+
            "    \r\n"+
            "    this.inputElement.style.background='white';\r\n"+
            "    this.inputElement.style.cursor='pointer';\r\n"+
            "    this.inputElement.style.border='1px solid black';\r\n"+
            "    this.inputElement.innerHTML='&nbsp;';\r\n"+
            "\r\n"+
            "    this.inputElement.style.fontSize = \"12px\";\r\n"+
            "    this.inputElement.style.fontFamily = \"Arial\";\r\n"+
            "    \r\n"+
            "	this.element.style.whiteSpace='nowrap';\r\n"+
            "	this.element.style.display = \"inline-block\";\r\n"+
            "//	this.element.style.position = \"static\";\r\n"+
            "	this.inputElement.style.display='inline-block';\r\n"+
            "	this.uploadFileButtonElement.style.display='inline-block';\r\n"+
            "	this.uploadUrlButt");
          out.print(
            "onElement.style.display='inline-block';\r\n"+
            "	this.inputElement.style.position='relative';\r\n"+
            "	this.uploadFileButtonElement.style.position='relative';\r\n"+
            "	this.uploadUrlButtonElement.style.position='relative';\r\n"+
            "    this.inputElement.style.height=height;\r\n"+
            "	this.uploadUrlButtonElement.style.height=height;\r\n"+
            "	this.uploadFileButtonElement.style.height=height;	\r\n"+
            "    \r\n"+
            "    this.fileInput=nw2('input',null,that.el);\r\n"+
            "    this.fileInput.style.position='absolute';\r\n"+
            "    this.fileInput.type='file';\r\n"+
            "    this.fileInput.name='fileData';\r\n"+
            "    this.fileInput.style.display='none';\r\n"+
            "	\r\n"+
            "	\r\n"+
            "    this.inputElement.tabIndex=0;\r\n"+
            "    this.inputElement.onclick=function(){\r\n"+
            "    	// ensure uploading same file triggers onchange\r\n"+
            "    	that.fileInput.value =null;\r\n"+
            "    	that.fileInput.click(); \r\n"+
            "	};\r\n"+
            "    this.inputElement.onchange=function(){ that.portlet.onChange(that.id,{value:that.getValue()}); };\r\n"+
            "    this.inputElement.onkeydown=function(e){\r\n"+
            "    	if(e.keyCode==13 || e.keyCode==32)//enter of spacebar\r\n"+
            "    	  return that.fileInput.click();\r\n"+
            "    	else if(e.keyCode==9)\r\n"+
            "    	  return that.form.onTab(that,e);\r\n"+
            "    }\r\n"+
            "    this.uploadFileButtonElement.onclick=function(){ \r\n"+
            "    	// ensure uploading same file triggers onchange\r\n"+
            "    	that.fileInput.value =null;\r\n"+
            "    	that.fileInput.click(); \r\n"+
            "	};\r\n"+
            "    \r\n"+
            "    this.uploadUrlButtonElement.onclick=function(){ that.onUrlButton(); };\r\n"+
            "	\r\n"+
            "	//this.fileInput.onchange=function(){ that.onFileSelected(); };\r\n"+
            "    var stopInput = function(ev) {\r\n"+
            "        ev.preventDefault();\r\n"+
            "        ev.stopPropagation();\r\n"+
            "    };\r\n"+
            "    \r\n"+
            "    this.element.ondrag = stopInput;\r\n"+
            "    this.element.ondragstart = stopInput;\r\n"+
            "    this.element.ondragend = stopInput;\r\n"+
            "    this.element.ondragover = stopInput;\r\n"+
            "    this.element.ondragenter = stopInput;\r\n"+
            "    this.element.ondragleave = stopInput;\r\n"+
            "    \r\n"+
            "	this.fileInput.onchange=function(ev){ \r\n"+
            "        var eventType = \"on\"+event[\"type\"];\r\n"+
            "        that.processFile(ev, eventType);\r\n"+
            "    };\r\n"+
            "    this.element.ondrop=function(ev){\r\n"+
            "        ev.preventDefault();\r\n"+
            "        var eventType = event.type;\r\n"+
            "        that.processFile(ev, eventType);            \r\n"+
            "    };\r\n"+
            "    this.inputElement.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "    this.inputElement.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "    this.element.appendChild(this.inputElement);\r\n"+
            "    this.buildDiv();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FileUploadField.prototype.displayFileName=function(files){\r\n"+
            "    var filename = files[0].name;\r\n"+
            "    this.inputElement.classList.add(\"fileSelected\");\r\n"+
            "    this.inputElement.value=filename;\r\n"+
            "    this.inputElement.textContent=filename;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FileUploadField.prototype.processFile=function(ev,eventType){\r\n"+
            "    var files = null;\r\n"+
            "    if (eventType == \"onchange\") {\r\n"+
            "        // hardcoded for single file only\r\n"+
            "        files = ev.currentTarget.files;\r\n"+
            "    } else if (eventType=\"drop\") {\r\n"+
            "        files = ev.dataTransfer.files;\r\n"+
            "    } else {\r\n"+
            "        console.log(\"event type not of drop or onchange\");\r\n"+
            "    }\r\n"+
            "    this.displayFileName(files);\r\n"+
            "    this.upload(files, eventType);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FileUploadField.prototype.upload=function(files, eventType) {\r\n"+
            "    var frame=getHiddenIFrame();\r\n"+
            "    var that=this;\r\n"+
            "    var form=nw2('form',null,that.el);\r\n"+
            "    var filename = files[0].name;\r\n"+
            "    this.fileInput.files=files;\r\n"+
            "    form.method='post';\r\n"+
            "    form.enctype='multipart/form-data';\r\n"+
            "    form.target=frame.name;\r\n"+
            "    form.action=portletManager.callbackUrl;\r\n"+
            "    form.appendChild(this.fileInput);\r\n"+
            "    form.appendChild(this.createHiddenField('");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
          out.print(
            "',portletManager.pgid));\r\n"+
            "    form.appendChild(this.createHiddenField('action','upload'));\r\n"+
            "    form.appendChild(this.createHiddenField('type', eventType));\r\n"+
            "    form.appendChild(this.createHiddenField('portletId',this.portlet.getId()));\r\n"+
            "    form.appendChild(this.createHiddenField('fieldId',this.id));\r\n"+
            "    form.appendChild(this.createHiddenField('pageUid',portletManager.pageUid));\r\n"+
            "    form.appendChild(this.createHiddenField('mid',this.getModificationNumber()));\r\n"+
            "    getWindow(that.el).document.body.appendChild(form);\r\n"+
            "    form.submit();\r\n"+
            "    getWindow(that.el).document.body.removeChild(form);\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "FileUploadField.prototype.setFieldStyle=function(style){\r\n"+
            "  this.inputElement.className=\"\";\r\n"+
            "	applyStyle(this.inputElement,style);\r\n"+
            "}\r\n"+
            "FileUploadField.prototype.focusField=function(){\r\n"+
            "    this.inputElement.focus();\r\n"+
            "    return  true;\r\n"+
            "};\r\n"+
            "FileUploadField.prototype.setHidden=function(hidden){\r\n"+
            "	this.element.style.position= hidden?\"static\":\"absolute\";\r\n"+
            "	this.inputElement.style.position= hidden?\"static\":\"absolute\";\r\n"+
            "}\r\n"+
            "FileUploadField.prototype.buildDiv=function(){\r\n"+
            "    if(this.uploadFileButtonText){\r\n"+
            "      this.uploadFileButtonElement.innerHTML=this.uploadFileButtonText;\r\n"+
            "      this.element.appendChild(this.uploadFileButtonElement);\r\n"+
            "    }\r\n"+
            "    if(this.uploadUrlButtonText){\r\n"+
            "      this.uploadUrlButtonElement.innerHTML=this.uploadUrlButtonText;\r\n"+
            "      this.element.appendChild(this.uploadUrlButtonElement);\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "// Deprecated\r\n"+
            "FileUploadField.prototype.onFileSelected=function(){\r\n"+
            "	console.error(\"onFileSelected is deprecated\");\r\n"+
            "	var filename=this.fileInput.value.split(\"\\\\\");\r\n"+
            "	filename=filename[filename.length-1];\r\n"+
            "	this.inputElement.classList.add(\"fileSelected\");\r\n"+
            "	this.inputElement.value=filename;\r\n"+
            "	this.inputElement.textContent=filename;\r\n"+
            "	if(filename){\r\n"+
            "        var frame=getHiddenIFrame();\r\n"+
            "        var form=nw('form');\r\n"+
            "        form.method='post';\r\n"+
            "        form.enctype='multipart/form-data';\r\n"+
            "        form.target=frame.name;\r\n"+
            "        form.action=portletManager.callbackUrl;\r\n"+
            "        form.appendChild(this.fileInput);\r\n"+
            "        form.appendChild(this.createHiddenField('action','upload'));\r\n"+
            "        form.appendChild(this.createHiddenField('type','onchange'));\r\n"+
            "        form.appendChild(this.createHiddenField('portletId',this.portlet.getId()));\r\n"+
            "        form.appendChild(this.createHiddenField('value',filename));\r\n"+
            "        form.appendChild(this.createHiddenField('fieldId',this.id));\r\n"+
            "        form.appendChild(this.createHiddenField('pageUid',portletManager.pageUid));\r\n"+
            "        form.appendChild(this.createHiddenField('mid',this.getModificationNumber()));\r\n"+
            "        document.body.appendChild(form);\r\n"+
            "        form.submit();\r\n"+
            "        document.body.removeChild(form);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "FileUploadField.prototype.createHiddenField=function(name,value){\r\n"+
            "	var that=this;\r\n"+
            "  var r=nw2('input',null,that.el); \r\n"+
            "  r.type='hidden';\r\n"+
            "  r.name=name; \r\n"+
            "  r.value=value;\r\n"+
            "  return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FileUploadField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	this.uploadFileButtonElement.style.left=toPx(this.width + 2);\r\n"+
            "	if (this.uploadFileButtonElement.innerHTML) {\r\n"+
            "		this.uploadUrlButtonElement.style.left=toPx(parseInt(fromPx(this.uploadFileButtonElement.style.left)) + 2);\r\n"+
            "	} else {\r\n"+
            "		this.uploadUrlButtonElement.style.left=toPx(this.width + 2);;\r\n"+
            "	}\r\n"+
            " \r\n"+
            "  this.inputElement.style.width=toPx(this.width);\r\n"+
            "  this.inputElement.style.height=toPx(this.height);\r\n"+
            "  this.element.style.width=toPx(this.width);\r\n"+
            "  this.element.style.height=toPx(this.height);\r\n"+
            "};\r\n"+
            "FileUploadField.prototype.setButtonsText=function(uploadFileButtonText,uploadUrlButtonText){\r\n"+
            "  this.uploadFileButtonText=uploadFileButtonText;\r\n"+
            "  this.uploadUrlButtonText=uploadUrlButtonText;\r\n"+
            "  this.buildDiv();\r\n"+
            "};\r\n"+
            "FileUploadField.prototype.setValue=function(value){\r\n"+
            "  if(value==null)//IE Hack\r\n"+
            "    this.inputElement.value=\"\";\r\n"+
            "  else\r\n"+
            "    this.inputElement.innerHTML=value;\r\n"+
            "  if(value != null && value != \"\")\r\n"+
            "	this.inputElement.classList.add(\"fileSelected\");\r\n"+
            "  else{\r\n"+
            "	if(this.inputElement.classList.contains(\"fileSelected\"))\r\n"+
            "		this.inputElement.classList.remove(\"fileSelected\");\r\n"+
            "  }\r\n"+
            "	  \r\n"+
            "}\r\n"+
            "FileUploadField.prototype.getValue=function(){\r\n"+
            "  return this.inputElement.value;\r\n"+
            "};\r\n"+
            "\r\n"+
            "FileUploadField.prototype.onUrlButton=function(){\r\n"+
            "    this.portlet.onChange(this.id,{action:'urlClicked'});\r\n"+
            "}\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// ToggleButtonsField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "ToggleButtonsField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function ToggleButtonsField(portlet, id, title){\r\n"+
            "	FormField.call(this, portlet, id, title);\r\n"+
            "	this.element=nw(\"div\");\r\n"+
            "	this.options=[];\r\n"+
            "	this.cssStyle={};\r\n"+
            "	this.buttonCssStyle={};\r\n"+
            "	this.onCssStyle={};\r\n"+
            "	this.offCssStyle={};\r\n"+
            "	\r\n"+
            "	this.mode=\"S\";\r\n"+
            "	this._class = \"form_field_toggle_buttons\";\r\n"+
            "	this.btn_class = \"form_field_toggle_button\";\r\n"+
            "	this.btnOn_class = \"form_field_toggle_button_on\";\r\n"+
            "	this.btnOff_class = \"form_field_toggle_button_off\";\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.options;\r\n"+
            "ToggleButtonsField.prototype._class;\r\n"+
            "ToggleButtonsField.prototype.btn_class;\r\n"+
            "ToggleButtonsField.prototype.btnOn_class;\r\n"+
            "ToggleButtonsField.prototype.btnOff_class;\r\n"+
            "ToggleButtonsField.prototype.cssStyle;\r\n"+
            "ToggleButtonsField.prototype.buttonCssStyle;\r\n"+
            "ToggleButtonsField.prototype.onCssStyle;\r\n"+
            "ToggleButtonsField.prototype.offCssStyle;\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.setFieldStyle=function(style){\r\n"+
            "  this.element.className=\"\";\r\n"+
            "  applyStyle(this.element,style);\r\n"+
            "}\r\n"+
            "ToggleButtonsField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.element.style.pointerEvents= disabled? \"none\":\"auto\";\r\n"+
            "	if(this.disabled){\r\n"+
            "		for(var i in this.options)\r\n"+
            "			this.options[i].element.classList.add(\"form_field_toggle_disabled\");\r\n"+
            "	}else{\r\n"+
            "		for(var i in this.options)\r\n"+
            "			this.options[i].element.classList.remove(\"form_field_toggle_disabled\");\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.clear=function(){\r\n"+
            "	if(this.mode === \"S\"){\r\n"+
            "		for(var i = 0; i < this.options.length; i++){\r\n"+
            "			this.element.removeChild(this.options[i].element);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else if (this.mode === \"T\"){\r\n"+
            "		this.element.removeChild(this.options[0].element);\r\n"+
            "	}\r\n"+
            "	this.options=[];\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.init=function(){\r\n"+
            "	var that=this;\r\n"+
            "	this.element.classList.add(this._class);\r\n"+
            "	var btn_on = null;\r\n"+
            "	if(this.mode === \"S\"){\r\n"+
            "		for(var i = 0; i < this.options.length; i++){\r\n"+
            "		    if(this.options[i].element!=null)\r\n"+
            "			    this.element.removeChild(this.options[i].element);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else if (this.mode === \"T\"){\r\n"+
            "		if(this.options[0].element!=null)\r\n"+
            "		    this.element.removeChild(this.options[0].element);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(this.mode === \"S\"){\r\n"+
            "		this.buttonOn=null;\r\n"+
            "		for(var i = 0; i < this.options.length; i++){\r\n"+
            "			var btn = nw(\"button\");\r\n"+
            "			btn._index = i;\r\n"+
            "			btn.textContent = this.options[i].text;\r\n"+
            "			btn.classList.add(this.btn_class);\r\n"+
            "			btn.classList.add(this.btnOff_class);\r\n"+
            "			if(this.disabled)\r\n"+
            "				btn.classList.add(\"form_field_toggle_disabled\");\r\n"+
            "			\r\n"+
            "			btn.onclick= function(e){\r\n"+
            "				toggleButtonsFieldToggle(that, e);\r\n"+
            "			};\r\n"+
            "			\r\n"+
            "			this.options[i].state = false;\r\n"+
            "			this.options[i].element = btn;\r\n"+
            "			this.element.appendChild(btn);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else if(this.mode === \"T\"){\r\n"+
            "		if(this.options.length == 0){\r\n"+
            "			return;\r\n"+
            "		}\r\n"+
            "		var btn = nw(\"button\");\r\n"+
            "		btn._index = 0;\r\n"+
            "		btn.textContent = this.options[0].text;\r\n"+
            "		btn.classList.add(this.btn_class);\r\n"+
            "		btn.classList.add(this.btnOff_class);\r\n"+
            "		btn.onclick= function(e){\r\n"+
            "			toggleButtonsFieldToggle(that, e);\r\n"+
            "		};\r\n"+
            "		this.options[0].state = true;\r\n"+
            "		this.options[0].element = btn;\r\n"+
            "		this.element.appendChild(btn);\r\n"+
            "		for(var i = 1; i < this.options.length; i++){\r\n"+
            "			this.options[i].element = btn;\r\n"+
            "			this.options[i].state = false;\r\n"+
            "		}\r\n"+
            "		this.buttonOn = 0;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.addOption=function(key, name, cssStyle){\r\n"+
            "	var index = this.options.length;\r\n"+
            "	this.options.push({\"key\":parseInt(key), \"text\":name});\r\n"+
            "	if(cssStyle != null){\r\n"+
            "		this.setCssStyleAtIndex(cssStyle, index);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "ToggleButtonsField.prototype.fireOnChange=function(){\r\n"+
            "	this.portlet.onChange(this.id,{value: this.options[this.buttonOn].key});\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.setMode=function(m){\r\n"+
            "	this.mode = m;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.setValue=function(value){\r\n"+
            "	value = parseInt(value);\r\n"+
            "	if(this.mode === \"S\"){\r\n"+
            "		if(!this.buttonOn || (this.buttonOn && this.options[this.buttonOn].key !== value)){\r\n"+
            "			for(var i=0; i < this.options.length; i++){\r\n"+
            "				if(value === this.options[i].key){\r\n"+
            "					toggleButtonsFieldToggle(this, {\"target\":this.options[i].element}, true);\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else if(this.mode === \"T\"){\r\n"+
            "		if(this.buttonOn != value && value < this.options.length){\r\n"+
            "			var that = this;\r\n"+
            "			var button = that.options[0].element;\r\n"+
            "			if(value == 0){\r\n"+
            "				button.classList.remove(that.btnOn_class);\r\n"+
            "				button.classList.add(that.btnOff_class);\r\n"+
            "			}else{\r\n"+
            "				button.classList.remove(that.btnOff_class);\r\n"+
            "				button.classList.add(that.btnOn_class);\r\n"+
            "			}\r\n"+
            "			that.options[value].state = true;\r\n"+
            "			if(that.buttonOn != null)\r\n"+
            "				that.options[that.buttonOn].state = false;\r\n"+
            "			button.textContent = that.options[value].text;\r\n"+
            "			that.buttonOn = value;\r\n"+
            "			that.applyStyle(null,null);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "function toggleButtonsFieldToggle(that, e, noFire) {\r\n"+
            "	var buttonIndex = e.target._index;\r\n"+
            "	if (that.mode === \"S\") {\r\n"+
            "		if (buttonIndex != that.buttonOn || that.buttonOn == null) {\r\n"+
            "			if (that.options && that.options[buttonIndex] && that.options[buttonIndex].element) {\r\n"+
            "				if (that.options != null && that.buttonOn!=null && that.options[that.buttonOn] && that.options[that.buttonOn].element) {\r\n"+
            "					var button = that.options[that.buttonOn].element;\r\n"+
            "					button.classList.remove(that.btnOn_class);\r\n"+
            "					button.classList.add(that.btnOff_class);\r\n"+
            "					that.options[that.buttonOn].state = false;\r\n"+
            "					that.applyButtonStyle(button, that.offCssStyle);\r\n"+
            "				}\r\n"+
            "				var button = that.options[buttonIndex].element;\r\n"+
            "				button.classList.remove(that.btnOff_class);\r\n"+
            "				button.classList.add(that.btnOn_cla");
          out.print(
            "ss);\r\n"+
            "				that.options[buttonIndex].state = true;\r\n"+
            "				that.buttonOn = buttonIndex;\r\n"+
            "				\r\n"+
            "				that.applyButtonStyle(button, that.onCssStyle);\r\n"+
            "				if(noFire != true)\r\n"+
            "					that.fireOnChange();\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	} else if (that.mode === \"T\") {\r\n"+
            "		if (that.options && that.options[buttonIndex] && that.options[0].element) {\r\n"+
            "			if(that.buttonOn == null)\r\n"+
            "				that.buttonOn = 0;\r\n"+
            "			var nextIndex = (that.buttonOn+1) < that.options.length? that.buttonOn+1 : 0;\r\n"+
            "			\r\n"+
            "			var button = that.options[0].element;\r\n"+
            "			if(nextIndex == 0){\r\n"+
            "				button.classList.remove(that.btnOn_class);\r\n"+
            "				button.classList.add(that.btnOff_class);\r\n"+
            "			}else if(that.buttonOn == 0){\r\n"+
            "				button.classList.remove(that.btnOff_class);\r\n"+
            "				button.classList.add(that.btnOn_class);\r\n"+
            "			}\r\n"+
            "			button._index = nextIndex;\r\n"+
            "			that.options[that.buttonOn].state = false;\r\n"+
            "			that.options[nextIndex].state = true;\r\n"+
            "			button.textContent = that.options[nextIndex].text;\r\n"+
            "			that.buttonOn = nextIndex;\r\n"+
            "			that.applyStyle(null, null);\r\n"+
            "				\r\n"+
            "			if(noFire != true)\r\n"+
            "				that.fireOnChange();\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	this.cssStyle.width = toPx(this.width);\r\n"+
            "	this.cssStyle.height = toPx(this.height);\r\n"+
            "	this.applyStyle(null, null);\r\n"+
            "}\r\n"+
            "ToggleButtonsField.prototype.setSpacing=function(spacing){\r\n"+
            "	this.spacing = toPx(spacing);\r\n"+
            "}\r\n"+
            "ToggleButtonsField.prototype.setMinButtonWidth=function(minButtonWidth){\r\n"+
            "	this.buttonCssStyle.minWidth = toPx(minButtonWidth);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.applyStyle=function(style, buttonStyle){\r\n"+
            "	if(style != null){\r\n"+
            "		applyStyle(this.element, style);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	var len = this.options.length; \r\n"+
            "	\r\n"+
            "	if(this.mode === \"S\"){\r\n"+
            "		if(buttonStyle !=null){\r\n"+
            "			for(var i = 0; i < len; i++){\r\n"+
            "				if(this.options[i].element)\r\n"+
            "					applyStyle(this.options[i].element, buttonStyle);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		Object.assign(this.element.style, this.cssStyle);\r\n"+
            "		\r\n"+
            "		for(var i = 0; i < len; i++){\r\n"+
            "			if(this.options[i].element){\r\n"+
            "				Object.assign(this.options[i].element.style, this.buttonCssStyle);\r\n"+
            "				if(this.options[i].state){\r\n"+
            "					this.applyButtonStyle(this.options[i].element, this.onCssStyle);\r\n"+
            "				}\r\n"+
            "				else{\r\n"+
            "					this.applyButtonStyle(this.options[i].element, this.offCssStyle);\r\n"+
            "				}\r\n"+
            "				if(this.options[i].style != null)\r\n"+
            "					this.applyButtonStyle(this.options[i].element, this.options[i].style);\r\n"+
            "				this.options[i].element.style.height = this.cssStyle.height;\r\n"+
            "				if(i!=0){\r\n"+
            "					this.options[i].element.style.marginLeft = this.spacing;\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else if(this.mode === \"T\"){\r\n"+
            "		if(buttonStyle !=null){\r\n"+
            "			if(this.options[0].element)\r\n"+
            "				applyStyle(this.options[0].element, buttonStyle);\r\n"+
            "		}\r\n"+
            "		Object.assign(this.element.style, this.cssStyle);\r\n"+
            "		if(this.options[0].element){\r\n"+
            "			Object.assign(this.options[0].element.style, this.buttonCssStyle);\r\n"+
            "			if(this.buttonOn != 0){\r\n"+
            "				this.applyButtonStyle(this.options[0].element, this.onCssStyle);\r\n"+
            "			}\r\n"+
            "			else{\r\n"+
            "				this.applyButtonStyle(this.options[0].element, this.offCssStyle);\r\n"+
            "			}\r\n"+
            "			if(this.options[this.buttonOn].style != null)\r\n"+
            "				this.applyButtonStyle(this.options[0].element, this.options[this.buttonOn].style);\r\n"+
            "			this.options[0].element.style.height = this.cssStyle.height;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.applyButtonStyle=function(button,style){\r\n"+
            "	Object.assign(button.style, style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.setCssStyleAtIndex=function(style, index){\r\n"+
            "	var dummyElement = {style:{}, className:\"\"};\r\n"+
            "	applyStyle(dummyElement, style);\r\n"+
            "	\r\n"+
            "	if(this.options[index].style == null)\r\n"+
            "		this.options[index].style = {};\r\n"+
            "	Object.assign(this.options[index].style, dummyElement.style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.setCssStyle=function(style){\r\n"+
            "	var dummyElement = {style:{}, className:\"\"};\r\n"+
            "	applyStyle(dummyElement, style);\r\n"+
            "	Object.assign(this.cssStyle, dummyElement.style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ToggleButtonsField.prototype.setButtonCssStyle=function(style){\r\n"+
            "	var dummyElement = {style:{}, className:\"\"};\r\n"+
            "	applyStyle(dummyElement, style);\r\n"+
            "	Object.assign(this.buttonCssStyle, dummyElement.style);\r\n"+
            "}\r\n"+
            "ToggleButtonsField.prototype.setOnCssStyle=function(style){\r\n"+
            "	var dummyElement = {style:{}, className:\"\"};\r\n"+
            "	applyStyle(dummyElement, style);\r\n"+
            "	Object.assign(this.onCssStyle, dummyElement.style);\r\n"+
            "}\r\n"+
            "ToggleButtonsField.prototype.setOffCssStyle=function(style){\r\n"+
            "	var dummyElement = {style:{}, className:\"\"};\r\n"+
            "	applyStyle(dummyElement, style);\r\n"+
            "	Object.assign(this.offCssStyle, dummyElement.style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// MultiCheckboxField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype=new FormField();\r\n"+
            "function MultiCheckboxField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	this.disabled = false;\r\n"+
            "    this.element=nw('div', \"multicheckbox\");\r\n"+
            "    this.element.style.position='relative';\r\n"+
            "    this.element.tabIndex=1;\r\n"+
            "    this.clearElement = nw('div', \"multicheckbox_clear\");\r\n"+
            "    this.clearElement.innerText=\"\\u00D7\";\r\n"+
            "    \r\n"+
            "    this.input = this.element;\r\n"+
            "    \r\n"+
            "	this.options=new Map();\r\n"+
            "    this.checked = new Array();\r\n"+
            "    // for storing unconfirmed selections (menu still open)\r\n"+
            "    this.pending = new Array();\r\n"+
            "    \r\n"+
            "    var that= this;\r\n"+
            "    this.element.tabIndex=-1;\r\n"+
            "    this.element.onclick=function(e){\r\n"+
            "    	that.onFieldEvent(e,\"onFocus\");\r\n"+
            "    	that.show();};\r\n"+
            "    this.element.onfocus=function(e){ if(that.menu==null ) that.onFieldEvent(e,\"onFocus\"); };\r\n"+
            "    this.element.onblur=function(e){ if(that.menu==null ) that.onFieldEvent(e,\"onBlur\"); };\r\n"+
            "    	\r\n"+
            "    this.clearElement.onclick=function(e){that.clearSelected();e.stopPropagation();};\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.clearSelected=function(){\r\n"+
            "	this.checked.length = 0;\r\n"+
            "	this.fireOnChange();\r\n"+
            "};\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.validatePending=function(){\r\n"+
            "	// rebuild checked \r\n"+
            "	this.checked.length = 0;\r\n"+
            "	//validate and move pending to checked\r\n"+
            "	for (var j=0; j < this.pending.length; j++) {\r\n"+
            "		var val = this.options.get(this.pending[j]);\r\n"+
            "		if (val) {\r\n"+
            "			this.checked.push(this.pending[j]);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	this.pending.length = 0;\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.__updateInputBox=function(){\r\n"+
            "	this.element.innerText=\"\";\r\n"+
            "\r\n"+
            "	var cntmore = 0;\r\n"+
            "	var i = 0;\r\n"+
            "	\r\n"+
            "	var checkedVal;\r\n"+
            "	var totalWidth = 0;\r\n"+
            "	var availableWidth = this.element.clientWidth; \r\n"+
            "	if (this.checked.length > 0) {\r\n"+
            "		availableWidth -= 12;// - clearElement length\r\n"+
            "	}\r\n"+
            "	for(; i < this.checked.length; i++){\r\n"+
            "		var val = this.options.get(this.checked[i]);\r\n"+
            "		checkedVal = nw('div', \"multicheckeditem\");\r\n"+
            "		checkedVal.innerText = val;\r\n"+
            "		const cs=getComputedStyle(checkedVal);\r\n"+
            "		this.element.appendChild(checkedVal);\r\n"+
            "		totalWidth += checkedVal.offsetWidth + parseInt(cs.marginRight);\r\n"+
            "		// if it doesn't fit;\r\n"+
            "		if(availableWidth < totalWidth){\r\n"+
            "			cntmore = this.checked.length - i;\r\n"+
            "			// checkedVal width will change, so reapply new width\r\n"+
            "			totalWidth -= (checkedVal.offsetWidth + parseInt(cs.marginRight));\r\n"+
            "			checkedVal.innerText = \"\" + (cntmore) + ((cntmore==this.checked.length)?\" items\" :\" more\") ;\r\n"+
            "			totalWidth += checkedVal.offsetWidth + parseInt(cs.marginRight);\r\n"+
            "			i--;\r\n"+
            "			break;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	while(i >= 0 && (availableWidth < totalWidth)){\r\n"+
            "		cntmore++;\r\n"+
            "		var el = this.element.children[i];\r\n"+
            "		const cs=getComputedStyle(el);\r\n"+
            "		checkedVal.innerText = \"\" + (cntmore) + ((cntmore==this.checked.length)?\" items\" :\" more\") ;\r\n"+
            "		totalWidth -= (el.offsetWidth + parseInt(cs.marginRight));\r\n"+
            "		this.element.removeChild(el);\r\n"+
            "		i--;\r\n"+
            "	}\r\n"+
            "	if(this.checked.length > 0)\r\n"+
            "		this.element.appendChild(this.clearElement);\r\n"+
            "};\r\n"+
            "\r\n"+
            "// triggered from closing menu\r\n"+
            "MultiCheckboxField.prototype.fireOnChange=function(){\r\n"+
            "	this.validatePending();\r\n"+
            "	this.__updateInputBox();\r\n"+
            "	if(this.disabled != true){\r\n"+
            "		this.portlet.onChange(this.id,{value: this.checked.join(\",\") });\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.show=function(){\r\n"+
            "	if(currentContextMenu)\r\n"+
            "		currentContextMenu.hideAll();\r\n"+
            "	if (!this.options.size) {\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	var that= this;\r\n"+
            "	var r = new Rect();\r\n"+
            "	r.readFromElementRelatedToWindow(this.element);\r\n"+
            "	this.menu = new Menu(getWindow(this.element));\r\n"+
            "	var menu=this.menu;\r\n"+
            "	menu.setSize(this.width, this.height);\r\n"+
            "	menu.setCssStyle(\"_cna=multicheckbox_menulist\");\r\n"+
            "	menu.createMenu(this.createMenuJson(), function(e, action){});\r\n"+
            "	menu.show(new Point(r.left,r.top+this.height), true);\r\n"+
            "	menu.setCssStyle(this.style);\r\n"+
            "	menu.bypass=true;\r\n"+
            "	menu.onHide=function(){\r\n"+
            "		  that.fireOnChange();\r\n"+
            "		that.menu=null;\r\n"+
            "		};\r\n"+
            "	menu.onGlass=function(e){\r\n"+
            "		if(!isMouseInside(e,that.element,0)){\r\n"+
            "		  that.onFieldEvent(null,\"onBlur\");\r\n"+
            "		} else {\r\n"+
            "			  that.element.focus();\r\n"+
            "		}\r\n"+
            "		that.menu=null;\r\n"+
            "	}\r\n"+
            "	menu.customHandleKeydown = function(e){\r\n"+
            "		switch (e.key) {\r\n"+
            "		case \"Enter\":\r\n"+
            "			menu.hideAll();\r\n"+
            "			break;\r\n"+
            "		case \"Tab\":\r\n"+
            "			if (currentContextMenu) {\r\n"+
            "				currentContextMenu.hideAll();\r\n"+
            "				that.handleTab(null,e);	\r\n"+
            "			}\r\n"+
            "			// handle tabbing\r\n"+
            "			portletManager.onUserSpecialKey(e);\r\n"+
            "			break;\r\n"+
            "		case \" \": \r\n"+
            "			this.runMenuItemAction(e);\r\n"+
            "			break;\r\n"+
            "		default:\r\n"+
            "			menu.handleKeydown(e);\r\n"+
            "			break;\r\n"+
            "		}\r\n"+
            "	};\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.isChecked=function(option){\r\n"+
            "	var idx = this.checked.indexOf(option);\r\n"+
            "	return (idx!=-1);\r\n"+
            "};\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.toggleSelectedCheckBox=function(){\r\n"+
            "	if(currentContextMenu){\r\n"+
            "		var selected = currentContextMenu.getSelectedMenu();\r\n"+
            "		if(selected == null)\r\n"+
            "			return;\r\n"+
            "		var checkbox = selected.getElementsByTagName(\"input\")[0];\r\n"+
            "		var checked = checkbox.checked= !checkbox.checked;\r\n"+
            "		\r\n"+
            "		if(checked==true){\r\n"+
            "			this.pending.push(checkbox.name);\r\n"+
            "		}\r\n"+
            "		else{\r\n"+
            "			var idx = this.pending.indexOf(checkbox.name);\r\n"+
            "			if(idx != -1){\r\n"+
            "				this.pending.splice(idx,1);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.createMenuJson=function(){\r\n"+
            "	var menu = {};\r\n"+
            "	menu.children = [];\r\n"+
            "	menu.enabled = true;\r\n"+
            "	menu.text=\"\";\r\n"+
            "	menu.type=\"menu\";\r\n"+
            "	menu.style = \"_cna=test\";\r\n"+
            "\r\n"+
            "	var i = 0;\r\n"+
            "	var that = this;\r\n"+
            "	var fieldId = this.id;\r\n"+
            "	var portletId = this.portlet.getId();\r\n"+
            "	var jsGetField =");
          out.print(
            " 'g(\"'+portletId+'\").getField(\"'+fieldId+'\")';\r\n"+
            "	for(var [option, val] of this.options){\r\n"+
            "		var item = {};\r\n"+
            "		var checked=this.isChecked(option);\r\n"+
            "		if (checked) {\r\n"+
            "			this.pending.push(option);\r\n"+
            "		}\r\n"+
            "		item.text = \"<input type='checkbox' \"+ (checked?\"checked\":\"\") +\" name='\"+ option +\"' >\" + escapeHtml(val) +\"\";\r\n"+
            "		item.type = \"action\";\r\n"+
            "		item.enabled = \"true\";\r\n"+
            "		item.noIcon = true;\r\n"+
            "		item.autoclose = false;\r\n"+
            "		item.onclickJs = jsGetField+'.toggleSelectedCheckBox();';\r\n"+
            "		item.style = \"_cna=multicheckbox_menuitem\"\r\n"+
            "		menu.children[i] = item;\r\n"+
            "		i++;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "\r\n"+
            "	return menu;\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.setFieldStyle=function(style){\r\n"+
            "	this.style = style;\r\n"+
            "	applyStyle(this.element,style);\r\n"+
            "	applyStyle(this.clearElement,style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.setClearElementColor=function(color){\r\n"+
            "	this.clearElement.style.color=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.focusField=function(){\r\n"+
            "	this.element.focus();\r\n"+
            "	this.show();\r\n"+
            "    return true;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	this.__updateInputBox();\r\n"+
            "}\r\n"+
            "	\r\n"+
            "MultiCheckboxField.prototype.setValue=function(value){\r\n"+
            "	this.checked.length = 0;\r\n"+
            "	if(value != \"\")\r\n"+
            "		Array.prototype.push.apply(this.checked, value.split(','));\r\n"+
            "	this.__updateInputBox();\r\n"+
            "};\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.addOption=function(value,text,isSelected){\r\n"+
            "	this.options.set(value, text);\r\n"+
            "	if(isSelected == true){\r\n"+
            "		this.checked.push(value);\r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	if(this.disabled == true){\r\n"+
            "		this.element.style.pointerEvents=\"none\";\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.element.style.pointerEvents=\"auto\";\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "MultiCheckboxField.prototype.clear=function(){\r\n"+
            "	this.checked.length = 0;\r\n"+
            "	this.options.clear();\r\n"+
            "};\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// SelectField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "SelectField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function SelectField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	this.options={};\r\n"+
            "	// the select element\r\n"+
            "    this.element=nw('select');\r\n"+
            "    \r\n"+
            "    \r\n"+
            "    var that=this;\r\n"+
            "    this.element.style.position='relative';\r\n"+
            "    // this.select is wrapper for element\r\n"+
            "    this.select=new Select(this.element);\r\n"+
            "    this.select.element.style.position='relative';\r\n"+
            "    this.select.element.style.left='0px';\r\n"+
            "    this.select.element.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "    this.select.element.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "\r\n"+
            "    this.select.element.onmousedown=function(e){\r\n"+
            "    	if(getMouseButton(e)==2)\r\n"+
            "    		that.portlet.onUserContextMenu(e,that.id,-1);\r\n"+
            "	};\r\n"+
            "    this.select.element.onkeydown=function(e){\r\n"+
            "    	if(e.keyCode==9)\r\n"+
            "    		return that.form.onTab(that,e);\r\n"+
            "	};\r\n"+
            "	\r\n"+
            "    this.element.onchange=function(){\r\n"+
            "    	that.fireOnChange('change');\r\n"+
            "    };\r\n"+
            "    this.element.ondblclick=function(){that.fireOnChange('dblclick');};\r\n"+
            "    this.selectedValuesText=null;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "SelectField.prototype.setAttr=function(){\r\n"+
            "	this.element.setAttribute('id', 'customScroll');\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectField.prototype.setTextAlign=function(dir){\r\n"+
            "	if(dir)\r\n"+
            "	  this.select.element.dir=dir;\r\n"+
            "}\r\n"+
            "SelectField.prototype.setFieldStyle=function(style){\r\n"+
            "	this.select.element.className=\"\";\r\n"+
            "	applyStyle(this.element,style);\r\n"+
            "}\r\n"+
            "SelectField.prototype.focusField=function(){\r\n"+
            "    this.select.element.focus();\r\n"+
            "    return true;\r\n"+
            "};\r\n"+
            "\r\n"+
            "SelectField.prototype.fireOnChange=function(action){\r\n"+
            "	  var selectedValuesText=this.select.getSelectedValuesDelimited(',');\r\n"+
            "	  if(selectedValuesText==this.selectedValuesText && action!='dblclick')\r\n"+
            "		  return;\r\n"+
            "	  this.selectedValuesText=selectedValuesText;\r\n"+
            "	  this.portlet.onChange(this.id,{action:action,value:this.select.getSelectedValuesDelimited(',')});\r\n"+
            "}\r\n"+
            "SelectField.prototype.setScrollbarRadius=function(){\r\n"+
            "	this.element.style.setProperty(\"--scrollbar-radius\", \"15px\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectField.prototype.onFieldSizeChanged=function(){\r\n"+
            "  var h=toPx(this.height);\r\n"+
            "  if(this.button){\r\n"+
            "	 var t=toPx(this.width-this.height);\r\n"+
            "    this.select.element.style.width=t;\r\n"+
            "    this.button.style.left=t;\r\n"+
            "    this.button.style.width=h;\r\n"+
            "    this.button.style.height=h;\r\n"+
            "  }else{\r\n"+
            "    this.select.element.style.width=toPx(this.width);\r\n"+
            "  }\r\n"+
            "  this.select.element.style.height=h;\r\n"+
            "}\r\n"+
            "	\r\n"+
            "SelectField.prototype.setValue=function(value){\r\n"+
            " 		this.select.clearSelected();\r\n"+
            "  		this.select.setSelectedValueDelimited(value,',');\r\n"+
            "  		this.selectedValuesText=null;\r\n"+
            "};\r\n"+
            "SelectField.prototype.autoComplete=function(flags, pre, post, input, list){\r\n"+
            "	var out = [];\r\n"+
            "	var re = new RegExp(pre+input+post, flags);\r\n"+
            "	for(var i = 0; i < this.options.length; i++){\r\n"+
            "		if(typeof this.options[i] != 'undefined' && re.test(this.options[i]))\r\n"+
            "			out.push(i);\r\n"+
            "	}\r\n"+
            "	return out;\r\n"+
            "}\r\n"+
            "\r\n"+
            "// not used\r\n"+
            "SelectField.prototype.addOptions=function(listOptions, listKeysToAdd){\r\n"+
            "	this.selectedAcItem = -1;\r\n"+
            "	this.prevSelectedAcItem = -1;\r\n"+
            "	this.list.innerHTML='';\r\n"+
            "	this.listSelectItems=[];\r\n"+
            "	this.firstSelect=-1;\r\n"+
            "	this.lastSelect=-1;\r\n"+
            "	if(typeof listKeysToAdd == 'undefined'){\r\n"+
            "		for(var i = 0; i < listOptions.length; i++){\r\n"+
            "			if(typeof listOptions[i] != 'undefined'){\r\n"+
            "				var text = listOptions[i];\r\n"+
            "			\r\n"+
            "				var item = nw(\"div\", \"selectfield_ac_item\");\r\n"+
            "				item.textContent = text;\r\n"+
            "				item._value = i;\r\n"+
            "				this.list.appendChild(item);\r\n"+
            "				this.lastSelect++;\r\n"+
            "				this.listSelectItems[this.lastSelect]=item;\r\n"+
            "				item.acIndex = this.lastSelect;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		if(this.lastSelect != -1)\r\n"+
            "			this.firstSelect = 0;\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		for(var i = 0; i < listKeysToAdd.length; i++){\r\n"+
            "			var value = listKeysToAdd[i];\r\n"+
            "			var text = listOptions[value];\r\n"+
            "			\r\n"+
            "			var item = nw(\"div\", \"selectfield_ac_item\");\r\n"+
            "			item.textContent = text;\r\n"+
            "			item._value = value;\r\n"+
            "			this.list.appendChild(item);\r\n"+
            "			this.lastSelect++;\r\n"+
            "			this.listSelectItems[this.lastSelect]=item;\r\n"+
            "			item.acIndex = this.lastSelect;\r\n"+
            "		}\r\n"+
            "		if(this.lastSelect != -1)\r\n"+
            "			this.firstSelect = 0;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "SelectField.prototype.addOption=function(value,text,style,isSelected){\r\n"+
            "  this.options[value] = text;\r\n"+
            "  var option=this.select.addOption(value,text,isSelected);\r\n"+
            "  applyStyle(option,style);\r\n"+
            "  this.selectedValuesText=null;\r\n"+
            "};\r\n"+
            "\r\n"+
            "SelectField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.select.setDisabled(disabled);\r\n"+
            "	if(this.button != null){\r\n"+
            "		if(disabled == true){\r\n"+
            "			this.button.style.pointerEvents=\"none\";\r\n"+
            "			this.button.style.visibility=\"hidden\";\r\n"+
            "			this.element.style.background=disabled ? '#eaeaea' : '';\r\n"+
            "			this.element.style.color=disabled ? '#666666' : '';\r\n"+
            "		}\r\n"+
            "		else{\r\n"+
            "			this.button.style.pointerEvents=\"initial\";\r\n"+
            "			this.button.style.visibility=\"initial\";\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "SelectField.prototype.ensureSelectedVisible=function(){\r\n"+
            "	this.select.ensureSelectedVisible();\r\n"+
            "}\r\n"+
            "SelectField.prototype.setMulti=function(multiple){\r\n"+
            "	this.multiple=multiple;\r\n"+
            "  if(multiple){\r\n"+
            "    this.element.multiple='multiple';\r\n"+
            "    this.element.style.width=\"100%\";\r\n"+
            "    this.selectedValuesText='';\r\n"+
            "	var that=this;\r\n"+
            "  }else\r\n"+
            "    this.element.multiple='';\r\n"+
            "};\r\n"+
            "\r\n"+
            "SelectField.prototype.setScrollbarGripColor=function(color){\r\n"+
            "	this.element.style.setProperty('--scrollbar-gripcolor', color); \r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectField.prototype.setScrollbarTrackColor=function(color){\r\n"+
            "	this.element.style.setProperty('--scrollbar-trackcolor', color); \r\n"+
            "}\r\n"+
            "SelectField.prototype.clear=function(){\r\n"+
            "  this.select.clear();\r\n"+
            "};\r\n"+
            "\r\n"+
            "// not used\r\n"+
            "SelectField.prototype.handleKeydown=function(e){\r\n"+
            "	//err(e);\r\n"+
            "	if(e.keyCode==40){//Down\r\n"+
            "		if(typeof this.listSelectItems == 'undefined')\r\n"+
            "			return;	\r\n"+
            "		if(this.selectedAcItem != -1)\r\n"+
            "			this.prevSelectedAcItem = this.selectedAcItem;\r\n"+
            "		else\r\n"+
            "			this.prevSelectedAcItem = -1;\r\n"+
            "		if(this.selectedAcItem == -1)\r\n"+
            "			this.selectedAcItem = this.firstSelect;\r\n"+
            "		else if(this.selectedAcItem == this.lastSelect)\r\n"+
            "			this.selectedAcItem = this.firstSelect;\r\n"+
            "		else\r\n"+
            "			this.selectedAcItem++;\r\n"+
            "		this.setSelectedOption();\r\n"+
            "		\r\n"+
            "	}\r\n"+
            "	else if (e.keyCode==38){//Up\r\n"+
            "		if(typeof this.listSelectItems == 'undefined')\r\n"+
            "			return;	\r\n"+
            "		if(this.selectedAcItem != -1)\r\n"+
            "			this.prevSelectedAcItem = this.selectedAcItem;\r\n"+
            "		else\r\n"+
            "			this.prevSelectedAcItem = -1;\r\n"+
            "		if(this.selectedAcItem == -1)\r\n"+
            "			this.selectedAcItem = this.lastSelect;\r\n"+
            "		else if(this.selectedAcItem == this.firstSelect)\r\n"+
            "			this.selectedAcItem = this.lastSelect;\r\n"+
            "		else\r\n"+
            "			this.selectedAcItem--;\r\n"+
            "		this.setSelectedOption();\r\n"+
            "	}\r\n"+
            "	else if (e.keyCode==13){//Enter\r\n"+
            "		e.preventDefault();\r\n"+
            "		if(this.selectedAcItem > -1){\r\n"+
            "			var option = this.listSelectItems[this.selectedAcItem];\r\n"+
            "			var key = option._value;\r\n"+
            "			var text = this.options[key];\r\n"+
            "			this.input.value = text;\r\n"+
            "			this.list.style.display=\"none\";\r\n"+
            "			this.selectedOption = key;\r\n"+
            "			this.fireOnChange();\r\n"+
            "			this.input.blur();\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "SelectField.prototype.handleOnmousedown=function(e){\r\n"+
            "	if(e.target == this.list)\r\n"+
            "		return;\r\n"+
            "	if(this.selectedAcItem > -1){\r\n"+
            "		var option = this.listSelectItems[this.selectedAcItem];\r\n"+
            "		var key = option._value;\r\n"+
            "		var text = this.options[key];\r\n"+
            "		this.input.value = text;\r\n"+
            "		this.list.style.display=\"none\";\r\n"+
            "		this.selectedOption = key;\r\n"+
            "		this.fireOnChange();\r\n"+
            "		this.input.blur();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "SelectField.prototype.handleOnclick=function(e){\r\n"+
            "	if(typeof this.listSelectItems == 'undefined')\r\n"+
            "		this.addOptions(this.options, this.autoComplete(\"yi\",\"\",\"\",\"\", this.options));\r\n"+
            "//		this.addOptions(this.options, this.autoComplete(\"yi\",\"\",\"\",this.input.value, this.options));\r\n"+
            "}\r\n"+
            "SelectField.prototype.handleKeyup=function(e){\r\n"+
            "	if(e.keyCode==40){//Down\r\n"+
            "	}\r\n"+
            "	else if (e.keyCode==38){//Up\r\n"+
            "	}\r\n"+
            "	else\r\n"+
            "		this.addOptions(this.options, this.autoComplete(\"yi\",\"\",\"\",this.input.value, this.options));\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectField.prototype.setSelectedOption=function(){\r\n"+
            "	if(this.prevSelectedAcItem != -1){\r\n"+
            "		var option = this.listSelectItems[this.prevSelectedAcItem];\r\n"+
            "		option.classList.remove(\"selectfield_ac_item_active\");\r\n"+
            "	}\r\n"+
            "	if(this.selectedAcItem != -1){\r\n"+
            "		var option = this.listSelectItems[this.selectedAcIte");
          out.print(
            "m];\r\n"+
            "		option.classList.add(\"selectfield_ac_item_active\");\r\n"+
            "		\r\n"+
            "		//option.scrollIntoView();\r\n"+
            "		//err(option);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectField.prototype.handleMouseMove=function(e){\r\n"+
            "	if(typeof this.listSelectItems == 'undefined')\r\n"+
            "		return;	\r\n"+
            "	if(typeof e.target.acIndex == 'undefined')\r\n"+
            "		return;\r\n"+
            "	if(this.selectedAcItem != -1)\r\n"+
            "		this.prevSelectedAcItem = this.selectedAcItem;\r\n"+
            "	else\r\n"+
            "		this.prevSelectedAcItem = -1;\r\n"+
            "	this.selectedAcItem = e.target.acIndex;\r\n"+
            "	this.setSelectedOption();\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectField.prototype.handleWheel=function(e){\r\n"+
            "\r\n"+
            "}\r\n"+
            "SelectField.prototype.showList=function(e){\r\n"+
            "	var that = this;\r\n"+
            "	that.list.style.display = null;\r\n"+
            "	that.list.style.bottom=null;\r\n"+
            "	var doc = getDocument(that.list);\r\n"+
            "	var rect = that.list.getBoundingClientRect();\r\n"+
            "	var windowBottom = getWindow(that.list).outerHeight;\r\n"+
            "	if(rect.bottom > windowBottom){\r\n"+
            "		//err(that.height);\r\n"+
            "		//err(rect);\r\n"+
            "		var newB = (rect.height - that.height);\r\n"+
            "		//err(newB);\r\n"+
            "		that.list.style.bottom=toPx(newB);\r\n"+
            "	}\r\n"+
            "	disableWheel=true;\r\n"+
            "}\r\n"+
            "SelectField.prototype.hideList=function(e){\r\n"+
            "	var that = this;\r\n"+
            "	that.list.style.display=\"none\";\r\n"+
            "	disableWheel=false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "// not used\r\n"+
            "SelectField.prototype.init=function(hasButton){\r\n"+
            "  var that = this;\r\n"+
            "  if(hasButton && this.button == null){\r\n"+
            "	  var that=this;\r\n"+
            "     this.button=nw('div','selectfield_plusbutton');\r\n"+
            "     this.button.style.position='relative';\r\n"+
            "     this.button.onmousedown=function(e){that.portlet.onUserContextMenu(e,that.id, -1);};\r\n"+
            "     this.element.parentElement.appendChild(this.button);\r\n"+
            "  }else if(!hasButton && this.button!=null){\r\n"+
            "     this.element.parentElement.removeChild(this.button);\r\n"+
            "     this.button=null;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// PortletSelectField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "PortletSelectField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function PortletSelectField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "    this.element=nw('p','portlet_select_value');\r\n"+
            "	var that=this;\r\n"+
            "    this.element.onclick=function(){\r\n"+
            "    	that.portlet.onChange(that.id,{click:true});\r\n"+
            "        onSelectChild(function(id){that.onTargetSelected(id)});\r\n"+
            "    };\r\n"+
            "}\r\n"+
            "\r\n"+
            "PortletSelectField.prototype.onTargetSelected=function(id){\r\n"+
            "  var that=this;\r\n"+
            "  that.portlet.onChange(that.id,{value:id});\r\n"+
            "}\r\n"+
            "PortletSelectField.prototype.setValue=function(value){\r\n"+
            "  this.element.innerHTML=value;\r\n"+
            "};\r\n"+
            "PortletSelectField.prototype.getValue=function(){\r\n"+
            "  return this.element.innerHTML;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// DivField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "DivField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function DivField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	this.element=nw('div', 'divFieldElement');\r\n"+
            "	this.element.style.position=\"relative\";\r\n"+
            "	this.element.style.display=\"inline-block\";\r\n"+
            "}\r\n"+
            "DivField.prototype.setValue=function(value){\r\n"+
            "	this.element.innerHTML=value;\r\n"+
            "}\r\n"+
            "DivField.prototype.getValue=function(){\r\n"+
            "	return this.element.innerHTML;\r\n"+
            "}\r\n"+
            "DivField.prototype.setFieldStyle=function(style){\r\n"+
            "  this.element.className=\"divFieldElement\";\r\n"+
            "  applyStyle(this.element,style);\r\n"+
            "}\r\n"+
            "DivField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	this.element.style.width=toPx(this.width);\r\n"+
            "	this.element.style.height=toPx(this.height);\r\n"+
            "//	this.element.style.width=\"100%\";\r\n"+
            "//	this.element.style.height=\"100%\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "function FieldExtension(field, extensionIndex){\r\n"+
            "	this.extensionIndex = extensionIndex;\r\n"+
            "	this.field = field;\r\n"+
            "	if(field != null)\r\n"+
            "		this.field.addExtensionAt(this, this.extensionIndex);\r\n"+
            "}\r\n"+
            "FieldExtension.prototype.onFieldReposition=function(){\r\n"+
            "}\r\n"+
            "\r\n"+
            "FieldExtension.prototype.callBack=function(action, values){\r\n"+
            "	this.field.portlet.onFieldExtensionCallBack(this.field.id, this.extensionIndex, action, values);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FieldAutocompleteExtension.prototype=new FieldExtension();\r\n"+
            "\r\n"+
            "function FieldAutocompleteExtension(field, extensionIndex){\r\n"+
            "	FieldExtension.call(this,field, extensionIndex);\r\n"+
            "	var that = this;\r\n"+
            "	this.visibleSugLen = 20;\r\n"+
            "	this.entries = new Array();//caches all entries that are being viewed or were viewed\r\n"+
            "	this.rowHeight = 14;\r\n"+
            "	this.extensionMenu = null;\r\n"+
            "	this.field.input.onclick=function(e){ \r\n"+
            "		that.field.onExtensionCallback(0, \"onOpen\", {} );\r\n"+
            "	};\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FieldAutocompleteExtension.prototype.s=function(size){\r\n"+
            "	this.totalSuggestionSize = size;\r\n"+
            "}\r\n"+
            "FieldAutocompleteExtension.prototype.a=function(index, value){\r\n"+
            "	\r\n"+
            "	eee = this;\r\n"+
            "	let len = this.entries.length;\r\n"+
            "	if(len == index){\r\n"+
            "		this.entries.push(value);\r\n"+
            "	}\r\n"+
            "	else if(index > len){\r\n"+
            "		this.entries[index]= value;\r\n"+
            "	}\r\n"+
            "	else {\r\n"+
            "		this.entries.splice(index, 0, value);\r\n"+
            "}\r\n"+
            "}\r\n"+
            "FieldAutocompleteExtension.prototype.u=function(index, value){\r\n"+
            "	this.entries[index]= value;\r\n"+
            "}\r\n"+
            "FieldAutocompleteExtension.prototype.d=function(index){\r\n"+
            "	this.entries.splice(index, 1);\r\n"+
            "}\r\n"+
            "FieldAutocompleteExtension.prototype.hide=function(){\r\n"+
            "	if(currentContextMenu)\r\n"+
            "		currentContextMenu.hideAll(); \r\n"+
            "}\r\n"+
            "FieldAutocompleteExtension.prototype.clear=function(){\r\n"+
            " 	this.entries = new Array();\r\n"+
            " 	this.extensionMenu = null;\r\n"+
            "}\r\n"+
            "FieldAutocompleteExtension.prototype.onFieldReposition=function(){\r\n"+
            "}\r\n"+
            "FieldAutocompleteExtension.prototype.createMenuJson=function(lower, upper){ \r\n"+
            "	// Lower here is less than upper\r\n"+
            "	var menu = {};\r\n"+
            "	if (this.entries.length == 0)\r\n"+
            "		return menu;\r\n"+
            "	menu.children = [];\r\n"+
            "	menu.enabled = true;\r\n"+
            "	menu.text=\"\";\r\n"+
            "	menu.type=\"menu\"\r\n"+
            "	menu.style = \"_cna=test\"\r\n"+
            "	var i=0;\r\n"+
            "	arguments\r\n"+
            "\r\n"+
            "	var l = lower;\r\n"+
            "	var u = upper;\r\n"+
            "	if(typeof lower == 'undefined')	{\r\n"+
            "		if(typeof this.extensionMenu.upperRow == 'undefined') //extensionMenu.upper is less than extensionMenu.lower\r\n"+
            "			err(\"undefined row\");\r\n"+
            "		l = this.extensionMenu.upperRow;\r\n"+
            "		u = this.extensionMenu.lowerRow;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	//for (const [key, value] of this.suggestions.entries()){\r\n"+
            "	for(var key2 = l; key2 <= u; key2++){\r\n"+
            "		var item = {};\r\n"+
            "		//item.action= value;\r\n"+
            "		\r\n"+
            "		item.text = this.entries[key2];\r\n"+
            "		//item.text = htmlToText(this.entries[key2]);\r\n"+
            "\r\n"+
            "		item.action= key2;\r\n"+
            "		\r\n"+
            "		item.type =\"action\";\r\n"+
            "		item.enabled = \"true\";\r\n"+
            "		item.noIcon = true;\r\n"+
            "		item.style = \"_cna=selectfield_ac_item\"\r\n"+
            "		menu.children[i] = item;\r\n"+
            "		i++;\r\n"+
            "	}\r\n"+
            "	return menu;\r\n"+
            "}\r\n"+
            "FieldAutocompleteExtension.prototype.select=function(i){\r\n"+
            "	// Force it to send the whole value\r\n"+
            "	//this.field.setValue(null);\r\n"+
            "	//this.field.setValueAndFireSelect(i);\r\n"+
            "	this.field.onExtensionCallback(0, \"onSelect\", {sel: i});\r\n"+
            "	//call onclose after selection\r\n"+
            "	this.field.onExtensionCallback(0, \"onClose\", new Object());\r\n"+
            "	this.field.onFieldEvent(null, \"onAutocompleted\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "FieldAutocompleteExtension.prototype.updateSuggestions=function(){\r\n"+
            "	//this.suggestionsArray = this.entries.slice(this.extensionMenu.upperRow, this.extensionMenu.lowerRow+1);	\r\n"+
            "	//this.suggestions = new Map();\r\n"+
            "	//for(val of this.suggestionsArray){\r\n"+
            "	//	this.suggestions.set(val, htmlToText(val) )\r\n"+
            "	//}\r\n"+
            "	//*****************\r\n"+
            "}\r\n"+
            "\r\n"+
            "FieldAutocompleteExtension.prototype.updateMenu=function(updateSelection, startOrEndOfSuggestion){\r\n"+
            "	var menuJson = this.createMenuJson();//this is the new menu json\r\n"+
            "	//truncate the this.extensionMenu.tableElement, remove all rows(children nodes)\r\n"+
            "	this.extensionMenu.tableElement.innerHTML ='';\r\n"+
            "	var that=this;\r\n"+
            "	this.extensionMenu.createMenu(menuJson, function(e, action){that.select(action);});\r\n"+
            "	this.extensionMenu.setTableRowHeight(this.rowHeight);\r\n"+
            "	//TODO: update left position if there is horizontal scrollbar\r\n"+
            "	//	if(this.extensionMenu.scrollPane.hscrollVisible)\r\n"+
            "	//		this.extensionMenu.updateCellsLocations();\r\n"+
            "	const START_OF_SUGGESTION = 1;\r\n"+
            "	const END_OF_SUGGESTION = 2;\r\n"+
            "	const MIDDLE_OF_SUGGESTION = 3;\r\n"+
            "	if(updateSelection){\r\n"+
            "		var m = this.extensionMenu;\r\n"+
            "		var visibleRows = m.tableElement.children;\r\n"+
            "			switch(startOrEndOfSuggestion){\r\n"+
            "			case START_OF_SUGGESTION:\r\n"+
            "				m.selected = this.visibleSugLen - 1;\r\n"+
            "				if(visibleRows[m.selected])\r\n"+
            "					m.highlight(visibleRows[m.selected], true);\r\n"+
            "				break;\r\n"+
            "			case END_OF_SUGGESTION:\r\n"+
            "				m.selected = 0;\r\n"+
            "				if(visibleRows[m.selected])\r\n"+
            "					m.highlight(visibleRows[m.selected], true);\r\n"+
            "				break;\r\n"+
            "			case MIDDLE_OF_SUGGESTION:\r\n"+
            "				if(visibleRows[m.selected])\r\n"+
            "					m.highlight(visibleRows[m.selected], true);\r\n"+
            "				break;\r\n"+
            "		   }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FieldAutocompleteExtension.prototype.setRowSelected=function(idx){\r\n"+
            "	var m = this.extensionMenu;\r\n"+
            "	m.selected=idx;\r\n"+
            "	//m.tableElement.children are the menu items that are visible in view \r\n"+
            "	m.highlight(m.tableElement.children[m.selected], true);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FieldAutocompleteExtension.prototype.updateMenuDelta=function(){\r\n"+
            "	var menuJson = this.createMenuJson();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FieldAutocompleteExtension.prototype.show=function(lower,upper){\r\n"+
            "	//reset the upperrow and lowerrow if extensionMenu exists\r\n"+
            "	if(this.extensionMenu){\r\n"+
            "		this.extensionMenu.upperRow = 0;\r\n"+
            "		this.extensionMenu.lowerRow = this.visibleSugLen - 1;\r\n"+
            "	}\r\n"+
            "	if(currentContextMenu)\r\n"+
            "		currentContextMenu.hideAll();\r\n"+
            "	var menuJson = this.createMenuJson(lower, upper);\r\n"+
            "	if(menuJson == null || menuJson.children == null)\r\n"+
            "		return;\r\n"+
            "	if(menuJson.children.length == 0)\r\n"+
            "		return;\r\n"+
            "	var that= this;\r\n"+
            "	var r = new Rect();\r\n"+
            "	r.readFromElementRelatedToWindow(this.field.element);\r\n"+
            "	var menu = new Menu(getWindow(this.field.element));\r\n"+
            "	menu.setSize(this.field.width, this.field.height);\r\n"+
            "	menu.setCssStyle(\"_cna=selectfield_ac_list\");\r\n"+
            "	menu.createMenu(menuJson, function(e, action){that.select(action);});\r\n"+
            "	if(this.totalSuggestionSize > this.visibleSugLen){\r\n"+
            "		this.addScroll(menu);\r\n"+
            "		menu.divElement.style.setProperty('max-width', 'none','important');//override max-width:600px from menu css style\r\n"+
            "	}\r\n"+
            "	this.extensionMenu = menu;\r\n"+
            "	this.extensionMenu.totalSuggestionSize = this.totalSuggestionSize;\r\n"+
            "	this.extensionMenu.upperRow = 0;\r\n"+
            "	this.extensionMenu.lowerRow = this.visibleSugLen - 1;\r\n"+
            "	menu.show(new Point(r.left,r.top+this.field.height), true);\r\n"+
            "	if(this.totalSuggestionSize > this.visibleSugLen)\r\n"+
            "		menu.divElement.style.setProperty('over");
          out.print(
            "flow-x', 'hidden','important');\r\n"+
            "	menu.bypass=true;\r\n"+
            "	menu.customHandleKeydown = function(e){\r\n"+
            "		menu.handleKeydown(e);\r\n"+
            "		that.field.onKeyDown(e);\r\n"+
            "		if(e.keyCode==13 || e.keyCode == 9){\r\n"+
            "			menu.hideAll();\r\n"+
            "		}\r\n"+
            "		if (e.key == 9) // handle tabbing\r\n"+
            "			portletManager.onUserSpecialKey(e);\r\n"+
            "	};\r\n"+
            "	\r\n"+
            "}\r\n"+
            "FieldAutocompleteExtension.prototype.addScroll = function(menu){\r\n"+
            "	//add scroll bar to the menu\r\n"+
            "	menu.setTableRowHeight(this.rowHeight);\r\n"+
            "	menu.addScrollPane();\r\n"+
            "	menu.setMenuBoxWidthHeight(this.field.width,menu.tableElement.rowHeight*this.visibleSugLen);\r\n"+
            "	menu.initScroll(this.totalSuggestionSize, menu.tableElement.rowHeight, this.field.width);\r\n"+
            "	menu.noAutoScrollbar();\r\n"+
            "	\r\n"+
            "	//onBoundChanged() cb\r\n"+
            "	menu.extension = this;\r\n"+
            "}\r\n"+
            "\r\n"+
            "class ComboBox extends HTMLElement {\r\n"+
            "	constructor(){\r\n"+
            "		super();\r\n"+
            "		\r\n"+
            "		var that = this;\r\n"+
            "		this.options = new Map();\r\n"+
            "		this.suggestions = new Map();\r\n"+
            "		this.value=\"\";\r\n"+
            "		this.minWidth = -1;\r\n"+
            "		this.setMinWidth=function(newMin){\r\n"+
            "			this.minWidth = newMin;\r\n"+
            "		};\r\n"+
            "		this.setValue=function(value){\r\n"+
            "			this.value = value;\r\n"+
            "			this.input.value = value;\r\n"+
            "		};\r\n"+
            "		this.addOptionDisplayAction=function(display, action){\r\n"+
            "			this.options.set(display, action);	\r\n"+
            "		};\r\n"+
            "		this.addOption=function(value){\r\n"+
            "			this.options.set(value, value);	\r\n"+
            "		};\r\n"+
            "		this.clearOptions2=function(){\r\n"+
            "			this.options=new Map();\r\n"+
            "			this.suggestions=new Map();\r\n"+
            "		};\r\n"+
            "		this.clearOptions=function(){\r\n"+
            "			this.options=new Map();\r\n"+
            "			this.suggestions=new Map();\r\n"+
            "			this.value=null;\r\n"+
            "		};\r\n"+
            "		this.blur=function(){\r\n"+
            "			this.input.blur();\r\n"+
            "			if(currentContextMenu)\r\n"+
            "				currentContextMenu.hideAll();\r\n"+
            "		};\r\n"+
            "		this.focus=function(){\r\n"+
            "			this.input.focus();\r\n"+
            "			this.autocomplete();\r\n"+
            "		};\r\n"+
            "		this.createMenuJson=function(){\r\n"+
            "			var menu = {};\r\n"+
            "			menu.children = [];\r\n"+
            "			menu.enabled = true;\r\n"+
            "			menu.text=\"\";\r\n"+
            "			menu.type=\"menu\"\r\n"+
            "			menu.style = \"_cna=test\"\r\n"+
            "			var i = 0;\r\n"+
            "			for (let [key, value] of this.suggestions) {\r\n"+
            "				var item = {};\r\n"+
            "				item.text = key;\r\n"+
            "				item.action= value;\r\n"+
            "				item.type =\"action\";\r\n"+
            "				item.enabled = \"true\";\r\n"+
            "				item.noIcon = true;\r\n"+
            "				item.style = \"_cna=selectfield_ac_item\"\r\n"+
            "				menu.children[i] = item;\r\n"+
            "				i++;\r\n"+
            "			}\r\n"+
            "			return menu;\r\n"+
            "		};\r\n"+
            "		this.handleKeydown=function(e){\r\n"+
            "			var shiftKey = e.shiftKey;\r\n"+
            "			var ctrlKey = e.ctrlKey;\r\n"+
            "			var altKey = e.altKey;\r\n"+
            "			if(shiftKey == true || ctrlKey == true || altKey == true || e.key==\"Tab\")\r\n"+
            "				return false;\r\n"+
            "			\r\n"+
            "			if(currentContextMenu){\r\n"+
            "				currentContextMenu.handleKeydown(e);\r\n"+
            "				if(e.key==\"Enter\"){\r\n"+
            "					this.blur();\r\n"+
            "				}\r\n"+
            "				return true;\r\n"+
            "			}\r\n"+
            "			else \r\n"+
            "				return false;\r\n"+
            "		};\r\n"+
            "		this.select=function(i){\r\n"+
            "			//this.input.value = this.suggestions.get(i);\r\n"+
            "			//this.value = this.input.value;\r\n"+
            "			this.input.value = i;\r\n"+
            "			this.value = i;\r\n"+
            "			kmm.activePortletId = this.activePortletId;\r\n"+
            "			//this.input.focus();\r\n"+
            "		};\r\n"+
            "		\r\n"+
            "		this.customHandleKeydown = null;\r\n"+
            "		this.setCustomKeydownHandler = function(handler){\r\n"+
            "			this.customHandleKeydown = handler;\r\n"+
            "		};\r\n"+
            "		this.hideGlass=function(){\r\n"+
            "		};\r\n"+
            "		this.show=function(){\r\n"+
            "			if(currentContextMenu)\r\n"+
            "				currentContextMenu.hideAll();\r\n"+
            "			if(this.options == null)// || this.options.length == 0)\r\n"+
            "				return;\r\n"+
            "			var that= this;\r\n"+
            "			var r = new Rect();\r\n"+
            "			r.readFromElementRelatedToWindow(this.input);\r\n"+
            "			var menu = new Menu(getWindow(this.input));\r\n"+
            "			\r\n"+
            "			var width = this.input.offsetWidth;\r\n"+
            "			if(this.minWidth!=-1 && width < this.minWidth)\r\n"+
            "				width = this.minWidth;\r\n"+
            "				\r\n"+
            "			menu.setSize(width, this.input.offsetHeight);\r\n"+
            "			menu.setCssStyle(\"_cna=selectfield_ac_list\");\r\n"+
            "			menu.createMenu(this.createMenuJson(), function(e, action){that.select(action);});\r\n"+
            "			menu.show(new Point(r.left,r.top+this.input.offsetHeight), true, null, that);\r\n"+
            "			menu.bypass = true;\r\n"+
            "			if(this.customHandleKeydown !=null)\r\n"+
            "				menu.setCustomKeydownHandler(this.customHandleKeydown);\r\n"+
            "			\r\n"+
            "			this.activePortletId = kmm.activePortletId;\r\n"+
            "		};\r\n"+
            "		this.autocomplete=function(key){\r\n"+
            "			if(currentContextMenu)\r\n"+
            "				currentContextMenu.hideAll();\r\n"+
            "		\r\n"+
            "			//No need for autocomplete\r\n"+
            "			this.suggestions=this.options;\r\n"+
            "			this.show();\r\n"+
            "			return;\r\n"+
            "		};\r\n"+
            "		\r\n"+
            "		const shadow = this.attachShadow({mode: 'open'});\r\n"+
            "		this.shadow = shadow\r\n"+
            "		const input = nw(\"input\",\"combo-box\");\r\n"+
            "		this.input = input;\r\n"+
            "		this.input.setAttribute('tabindex', 0);\r\n"+
            "		this.input.onclick=function(e){that.autocomplete();};\r\n"+
            "		//No need to autocomplete\r\n"+
            "//		this.input.onkeydown=function(e){err(e);if(currentContextMenu)currentContextMenu.hideAll();};\r\n"+
            "		this.input.onkeyup=function(e){that.value=that.input.value; };\r\n"+
            "		this.input.onkeypress=function(e){if(currentContextMenu) currentContextMenu.deselect();};\r\n"+
            "//		this.input.onkeyup=function(e){that.value=that.input.value; that.autocomplete(e.key);};\r\n"+
            "		\r\n"+
            "	    const style = document.createElement('style');\r\n"+
            "	\r\n"+
            "	    style.textContent = `\r\n"+
            "	    	.combo-box{\r\n"+
            "	    		box-sizing:border-box;\r\n"+
            "	    		border:none;\r\n"+
            "	    		width:100%;\r\n"+
            "	    		height:100%;\r\n"+
            "	    	}\r\n"+
            "	    	.combo-box:focus{\r\n"+
            "	    		outline:none!important;\r\n"+
            "	    	}\r\n"+
            "	    `;\r\n"+
            "	\r\n"+
            "	    // Attach the created elements to the shadow dom\r\n"+
            "	    shadow.appendChild(style);\r\n"+
            "		shadow.appendChild(this.input);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "customElements.define('combo-box', ComboBox);\r\n"+
            "\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "// ColorGradientField\r\n"+
            "////////////////////////////////////////////////////////////////////////////////\r\n"+
            "ColorGradientField.prototype=new FormField();\r\n"+
            "\r\n"+
            "function ColorGradientField(portlet,id,title){\r\n"+
            "	\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	this.createGradientChooser();\r\n"+
            "    this.element=this.colorPicker.element;//nw('canvas');\r\n"+
            "    this.element.style.border=\"1px solid black\";\r\n"+
            "    this.element.style.cursor='pointer';\r\n"+
            "    this.element.style.position='absolute';\r\n"+
            "    this.element.style.left='0px';\r\n"+
            "    //this.context = this.element.getContext('2d');\r\n"+
            "	var that=this;\r\n"+
            "    this.noColorText = 'no color';\r\n"+
            "  this.colorPicker.element.onfocus=function(e){that.onFieldEvent(e,\"onFocus\");};\r\n"+
            "  this.colorPicker.element.onblur=function(e){that.onFieldEvent(e,\"onBlur\");};\r\n"+
            "  this.colorPicker.element.onkeydown=function(e){\r\n"+
            "      that.handleTab(null,e);\r\n"+
            "    };\r\n"+
            "}\r\n"+
            "ColorGradientField.prototype.setFieldStyle=function(style){\r\n"+
            "  this.element.className=\"\";\r\n"+
            "  applyStyle(this.element,style);\r\n"+
            "}\r\n"+
            "ColorGradientField.prototype.init=function(allowNull,alpha, borderColor, borderRadius, borderWidth){\r\n"+
            "  this.allowNull=allowNull;\r\n"+
            "  this.alpha=alpha;\r\n"+
            "  if (borderWidth != null && borderWidth > 0) {\r\n"+
            "	  this.element.style.border=\"none\";\r\n"+
            "	  this.colorPicker.canvas.style.border=toPx(borderWidth) + \" solid \" + borderColor;\r\n"+
            "  } else {\r\n"+
            "	  this.element.style.border=\"1px solid black\";\r\n"+
            "	  this.colorPicker.canvas.style.border= \"\";\r\n"+
            "  }\r\n"+
            "  if (borderRadius != null)\r\n"+
            "	  this.colorPicker.canvas.style.borderRadius = toPx(borderRadius);\r\n"+
            "  else\r\n"+
            "	  this.colorPicker.canvas.style.borderRadius = \"\";\r\n"+
            "	  \r\n"+
            "  this.colorPicker.setAlphaEnabled(this.alpha);\r\n"+
            "};\r\n"+
            "\r\n"+
            "ColorGradientField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.element.style.pointerEvents = disabled?\"none\":\"all\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorGradientField.prototype.createGradientChooser=function(){\r\n"+
            "	this.colorPicker=new GradientPicker(this.getValue(),0,100);//this.getValue(),0,100);\r\n"+
            "	var that=this;\r\n"+
            "	this.colorPicker.onGradientChanged=function(){that.onColorChanged()};\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorGradientField.prototype.addCustomColor=function(colors){\r\n"+
            "	if(this.colorPicker!=null){\r\n"+
            "	    for(var i=0;i<colors.length;i++)\r\n"+
            "	      this.colorPicker.addColorChoice(colors[i]);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorGradientField.prototype.onColorChanged=function(){\r\n"+
            "	this.value=(this.colorPicker.getGradient());\r\n"+
            "    this.portlet.onChange(this.id,{value:this.getValue()==null ? null : this.getValue().toString()});\r\n"+
            "	this.updateColorPicker();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "ColorGradientField.prototype.onFieldSizeChanged=function(){\r\n"+
            "  this.colorPicker.setSize(this.width,this.height);\r\n"+
            "};\r\n"+
            "ColorGradientField.prototype.setNoColorText=function(noColorText){\r\n"+
            "	this.noColorText = noColorText;\r\n"+
            "};\r\n"+
            "ColorGradientField.prototype.setValue=function(value){\r\n"+
            "  if(value==null || value=='')\r\n"+
            "    this.value=null;\r\n"+
            "  else{\r\n"+
            "    this.value=new ColorGradient();\r\n"+
            "    this.value.parseString(value);\r\n"+
            "    this.colorPicker.setGradient(this.value);\r\n"+
            "  }\r\n"+
            "	this.updateColorPicker();\r\n"+
            "};\r\n"+
            "\r\n"+
            "ColorGradientField.prototype.updateColorPicker=function(){\r\n"+
            "return;\r\n"+
            "    if(this.value==null){\r\n"+
            "      this.context.clearRect(0, 0, this.element.width, this.element.height);\r\n"+
            "    }else{\r\n"+
            "	var w=this.element.width;\r\n"+
            "	var h=this.element.height;\r\n"+
            "	var c=this.context;\r\n"+
            "	var g=this.value;\r\n"+
            "	var len=g.length();\r\n"+
            "	c.clearRect(0,0,w,h);\r\n"+
            "	if(len>0){\r\n"+
            "      var grad = this.context.createLinearGradient(w, 0, 0, 0);\r\n"+
            "      grad.addColorStop(0,g.getColorAtStep(0));\r\n"+
            "      var min=g.getMinValue();\r\n"+
            "      var max=g.getMaxValue();\r\n"+
            "      if(min<max){\r\n"+
            "        for(var i=0;i<len;i++){\r\n"+
            "          var v=g.getValueAtStep(i);\r\n"+
            "          grad.addColorStop((v-min) / (max-min) ,g.getColorAtStep(i));\r\n"+
            "        }  \r\n"+
            "      }else{\r\n"+
            "        for(var i=0;i<len;i++){\r\n"+
            "          var v=g.getValueAtStep(i);\r\n"+
            "          //grad.addColorStop(v,g.getColorAtStep(i));\r\n"+
            "        }  \r\n"+
            "      }\r\n"+
            "      grad.addColorStop(1,g.getColorAtStep(len-1));\r\n"+
            "      c.fillStyle=grad;\r\n"+
            "      c.fillRect(0,0,w,h);\r\n"+
            "	}\r\n"+
            "    \r\n"+
            "	}\r\n"+
            "};\r\n"+
            "\r\n"+
            "ColorGradientField.prototype.getValue=function(){\r\n"+
            "  return this.value;\r\n"+
            "};\r\n"+
            "\r\n"+
            "ColorGradientField.prototype.focusField=function(){\r\n"+
            "	this.colorPicker.element.focus();\r\n"+
            "	return true;\r\n"+
            "};\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "");

	}
	
}