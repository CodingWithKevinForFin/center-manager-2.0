package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_helpbox_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_helpbox_js_1() {
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
            "function getTextWidth(text, font){\r\n"+
            "	var context = getTextWidth.canvas.getContext(\"2d\");\r\n"+
            "    context.font = font;\r\n"+
            "    var metrics = context.measureText(text);\r\n"+
            "    return metrics.width;\r\n"+
            "}\r\n"+
            "getTextWidth.canvas = nw(\"canvas\");\r\n"+
            "\r\n"+
            "function HelpBox(window, hasGlass, cssClass,outerCssClass){\r\n"+
            "	var that = this;\r\n"+
            "	this.window=window;\r\n"+
            "	this.outElement=nw(\"div\");\r\n"+
            "	this.outElement.style.top=\"0px\";\r\n"+
            "	this.outElement.style.left=\"0px\";\r\n"+
            "	this.outElement.style.bottom=\"0px\";\r\n"+
            "	this.outElement.style.right=\"0px\";\r\n"+
            "	this.outElement.style.userSelect=\"inherit\";\r\n"+
            "	this.divElement=nw(\"div\");\r\n"+
            "	this.outElement.appendChild(this.divElement);\r\n"+
            "	this.glassElement=nw(\"div\");\r\n"+
            "	var nostyle = \"_cna=ami_help_box_nostyle\";\r\n"+
            "	// we need the LAYOUT_DEFAULT classname to enable the user css class\r\n"+
            "	if(outerCssClass != null)\r\n"+
            "		applyStyle(this.outElement,outerCssClass);\r\n"+
            "	applyStyle(this.divElement,cssClass==null?nostyle:cssClass);\r\n"+
            "	this.hasGlass = hasGlass == null? true: hasGlass;\r\n"+
            "	this.glassElement.classList.add(\"disable_glass_clear\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "currentHelpBox = null;\r\n"+
            "HelpBox.prototype.window;\r\n"+
            "HelpBox.prototype.divElement;\r\n"+
            "HelpBox.prototype.outElement;\r\n"+
            "HelpBox.prototype.glassElement;\r\n"+
            "HelpBox.prototype.helpText;\r\n"+
            "HelpBox.prototype.textWidth;\r\n"+
            "HelpBox.prototype.hasGlass;\r\n"+
            "\r\n"+
            "HelpBox.prototype.init=function(text, textWidth){\r\n"+
            "	if(text != null)\r\n"+
            "		this.setText(text);\r\n"+
            "	if(textWidth != null)\r\n"+
            "		this.setTextWidth(textWidth);\r\n"+
            "}\r\n"+
            "\r\n"+
            "HelpBox.prototype.setText=function(text){\r\n"+
            "	this.divElement.innerHTML = text;\r\n"+
            "	this.helpText = text;\r\n"+
            "}\r\n"+
            "\r\n"+
            "HelpBox.prototype.setTextWidth=function(textWidth){\r\n"+
            "	this.textWidth = textWidth;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//HelpBox.prototype.autoSize=function(){\r\n"+
            "//	var textWidth = this.textWidth != null? this.textWidth : getTextWidth(this.helpText, getComputedStyle(this.divElement).font);\r\n"+
            "//	var widthToHeightR = 5/1;\r\n"+
            "//	var lineHeight = parseInt(getComputedStyle(this.divElement).fontSize, 10) + 2;\r\n"+
            "//	\r\n"+
            "//	var boxWidth = Math.sqrt(widthToHeightR*(textWidth * lineHeight));\r\n"+
            "//	\r\n"+
            "//	var minWidth = 180;\r\n"+
            "//	boxWidth = boxWidth > minWidth ? boxWidth: minWidth; \r\n"+
            "//}\r\n"+
            "HelpBox.prototype.autoPosition=function(mouseEvent){\r\n"+
            "	var r = mouseEvent.target.getBoundingClientRect();\r\n"+
            "	this.divElement.style.top = toPx(r.bottom);\r\n"+
            "	this.divElement.style.left = toPx(mouseEvent.x-10);\r\n"+
            "	ensureInWindow(this.divElement);\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "HelpBox.prototype.show=function(mouseEvent){\r\n"+
            "	if(currentHelpBox != null){\r\n"+
            "		currentHelpBox.hide();\r\n"+
            "	}\r\n"+
            "	currentHelpBox = this;\r\n"+
            "	if(this.hasGlass==true)\r\n"+
            "		this.window.document.body.appendChild(this.glassElement);\r\n"+
            "	this.window.document.body.appendChild(this.outElement);\r\n"+
            "//	this.autoSize();\r\n"+
            "	this.autoPosition(mouseEvent);\r\n"+
            "}\r\n"+
            "\r\n"+
            "HelpBox.prototype.showIfMouseInside=function(mouseEvent, element){\r\n"+
            "	var inside = isMouseInside(mouseEvent, element, null);\r\n"+
            "	if(inside==true)\r\n"+
            "		this.show(mouseEvent);\r\n"+
            "}\r\n"+
            "\r\n"+
            "HelpBox.prototype.hide=function(){\r\n"+
            "	if(currentHelpBox != null){\r\n"+
            "		this.window.document.body.removeChild(this.outElement);\r\n"+
            "		if(this.hasGlass)\r\n"+
            "			this.window.document.body.removeChild(this.glassElement);\r\n"+
            "		currentHelpBox = null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "HelpBox.prototype.autoHide=function(state, element){\r\n"+
            "	var that = this;\r\n"+
            "	if(state == true || state == null){\r\n"+
            "		if(this.divElement.onmouseleave == null){\r\n"+
            "			this.glassElement.onmousemove=function(e){\r\n"+
            "				var inside = isMouseInside(e, element, null);\r\n"+
            "				if(!inside)	that.hide();};\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.glassElement.onmousemove = null;\r\n"+
            "		this.glassElement.onmousedown=function(e){\r\n"+
            "			that.hide(e, that.label);\r\n"+
            "		};\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "HelpBox.prototype.handleKeydown=function(e){\r\n"+
            "	if(e.key === \"Escape\" && e.shiftKey == false && e.ctrlKey == false && e.altKey == false){\r\n"+
            "		this.hide();\r\n"+
            "	}\r\n"+
            "}");

	}
	
}