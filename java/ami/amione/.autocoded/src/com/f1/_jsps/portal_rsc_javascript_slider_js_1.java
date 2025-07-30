package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_slider_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_slider_js_1() {
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
            "function Slider(element, value, minVal, maxVal, widthPx, precision,step, textHidden, value2, sliderHidden, nullable, defaultValue, defaultValue2) {\r\n"+
            "	var that = this;\r\n"+
            "	this.element = element;\r\n"+
            "	this.defaultValue = defaultValue;\r\n"+
            "	this.defaultValue2 = defaultValue2;\r\n"+
            "	this.value = value;\r\n"+
            "	this.value2 = value2;\r\n"+
            "	this.width = widthPx;\r\n"+
            "	this.textHidden = textHidden;\r\n"+
            "	this.sliderHidden = sliderHidden;\r\n"+
            "	this.nullableEnabled = nullable;\r\n"+
            "	this.initConstants();\r\n"+
            "	this.initDomElements();\r\n"+
            "	this.initStyles();\r\n"+
            "	this.isNull = value == null && value2 == null;\r\n"+
            "	this.setRange(minVal,maxVal,step,precision);\r\n"+
            "	this.initRootdiv(that);\r\n"+
            "	this.initNullable(that, nullable);\r\n"+
            "	this.initInput(that);\r\n"+
            "	this.initGuide(that);\r\n"+
            "	this.initGrabber(that);\r\n"+
            "	// separate init for the two grabbers. Need separate check for grabber2\r\n"+
            "	this.initGrabber2(that);\r\n"+
            "	this.setValue(minVal, true, false, value2);\r\n"+
            "}\r\n"+
            "\r\n"+
            "// MAIN\r\n"+
            "Slider.prototype.value;\r\n"+
            "Slider.prototype.value2;\r\n"+
            "Slider.prototype.minVal;\r\n"+
            "Slider.prototype.maxVal;\r\n"+
            "Slider.prototype.precision;\r\n"+
            "\r\n"+
            "// DOMELEMENTS\r\n"+
            "Slider.prototype.element;\r\n"+
            "Slider.prototype.rootdiv;\r\n"+
            "Slider.prototype.slidingdiv;\r\n"+
            "Slider.prototype.guide;\r\n"+
            "Slider.prototype.lowguide;\r\n"+
            "Slider.prototype.grabber;\r\n"+
            "Slider.prototype.grabber2;\r\n"+
            "Slider.prototype.valdiv;\r\n"+
            "\r\n"+
            "Slider.prototype.val;\r\n"+
            "Slider.prototype.val2;\r\n"+
            "Slider.prototype.nullable;\r\n"+
            "\r\n"+
            "// OPTIONS\r\n"+
            "Slider.prototype.isShowing;\r\n"+
            "Slider.prototype.alwaysShow;\r\n"+
            "Slider.prototype.sliderHidden;\r\n"+
            "Slider.prototype.textHidden;\r\n"+
            "Slider.prototype.isNull;\r\n"+
            "Slider.prototype.nullableEnabled;\r\n"+
            "\r\n"+
            "// STYLE\r\n"+
            "Slider.prototype.width;\r\n"+
            "Slider.prototype.height;\r\n"+
            "Slider.prototype.guideHeight;\r\n"+
            "Slider.prototype.grabberWidth = 12;\r\n"+
            "Slider.prototype.padding = 3;\r\n"+
            "Slider.prototype.textSize;\r\n"+
            "\r\n"+
            "Slider.prototype.initConstants = function() {\r\n"+
            "	this.alwaysShow = true;\r\n"+
            "	this.height = 15;\r\n"+
            "	this.textSize = 0;\r\n"+
            "	this.grabberWidth = 12;\r\n"+
            "	this.guideHeight = 6;\r\n"+
            "	this.padding = 6;\r\n"+
            "	this.isShowing = false;\r\n"+
            "	this.isNull = false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.initDomElements = function() {\r\n"+
            "	this.rootdiv = nwDiv('slider', 0, 0, this.width, this.height);\r\n"+
            "	this.slidingdiv = nwDiv('', 0, 0, null, null);\r\n"+
            "	this.guide = nwDiv('slider_guide', 0, 0, null, null);\r\n"+
            "	this.lowguide = nwDiv('slider_guide', 0, 0, null, null);\r\n"+
            "	this.grabber = nwDiv('slider_grabber', 0, 0, null, null);\r\n"+
            "	this.grabber2 = nwDiv('slider_grabber', 0, 0, null, null);\r\n"+
            "	// see if grabber2 is active\r\n"+
            "	this.grabber2.activated=false;\r\n"+
            "	this.input = nw('input');\r\n"+
            "	this.valdiv = nwDiv('', 0, 0, null, null);\r\n"+
            "	this.val = nw('input', 'slider_val');\r\n"+
            "	this.val2 = nw('input', 'slider_val');\r\n"+
            "	this.nullable = nw('input');\r\n"+
            "	var that=this;\r\n"+
            "	this.val.onkeydown=function(e){if(that.onkeydown)that.onkeydown(e);};\r\n"+
            "\r\n"+
            "	this.rootdiv.appendChild(this.nullable);\r\n"+
            "	this.rootdiv.appendChild(this.slidingdiv);\r\n"+
            "	this.slidingdiv.appendChild(this.guide);\r\n"+
            "	this.slidingdiv.appendChild(this.lowguide);\r\n"+
            "	this.slidingdiv.appendChild(this.grabber);\r\n"+
            "	this.slidingdiv.appendChild(this.grabber2);\r\n"+
            "	this.rootdiv.appendChild(this.valdiv);\r\n"+
            "	this.valdiv.appendChild(this.val);\r\n"+
            "	this.valdiv.appendChild(this.val2);\r\n"+
            "	this.element.appendChild(this.rootdiv);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.setHeight = function(height) {\r\n"+
            "	this.height = height;\r\n"+
            "	this.updateBounds();\r\n"+
            "//	this.updateStyles();\r\n"+
            "}\r\n"+
            "Slider.prototype.setWidth = function(width) {\r\n"+
            "	this.width = width;\r\n"+
            "	this.updateBounds();\r\n"+
            "}\r\n"+
            "\r\n"+
            "// controls slider movement\r\n"+
            "Slider.prototype.updateBounds = function() {\r\n"+
            "	var remainingWidth = this.width;\r\n"+
            "	var rect = new Rect(0, 0, this.width, this.height);\r\n"+
            "	rect.writeToElementRelatedToParent(this.rootdiv);\r\n"+
            "\r\n"+
            "	if (this.nullableEnabled) {\r\n"+
            "		// nullable\r\n"+
            "		var checkboxWidth = 13;\r\n"+
            "		remainingWidth -= checkboxWidth;\r\n"+
            "	}\r\n"+
            "	if (this.textHidden != true) {\r\n"+
            "		var valWidth;\r\n"+
            "		var maxValWidth;\r\n"+
            "		if (this.sliderHidden != true) {\r\n"+
            "			valWidth = (Math.log10(Math.abs(this.maxVal)) + (this.precision == 0 ? 0 : (this.precision + 1))) * 7 + 10;\r\n"+
            "			var valWidth2 = (Math.log10(Math.abs(this.minVal)) + (this.precision == 0 ? 0 : (this.precision + 1))) * 7 + 10;\r\n"+
            "			valWidth = valWidth > valWidth2 ? valWidth : valWidth2;\r\n"+
            "			//		valWidth = valWidth / 15 * this.height;\r\n"+
            "			\r\n"+
            "			maxValWidth = this.value1 != null? this.width/3:this.width/2;\r\n"+
            "		}else{\r\n"+
            "			valWidth = this.width; \r\n"+
            "			maxValWidth = this.value1 != null? this.width/2:this.width;\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		valWidth = valWidth > maxValWidth ? maxValWidth : valWidth;\r\n"+
            "		\r\n"+
            "		var count = 0;\r\n"+
            "		if (this.value != null) {\r\n"+
            "			// val\r\n"+
            "			var rect = new Rect(0, 0, valWidth, this.height);\r\n"+
            "			rect.writeToElementRelatedToParent(this.val);\r\n"+
            "			remainingWidth -= valWidth;\r\n"+
            "			count++;\r\n"+
            "		}\r\n"+
            "\r\n"+
            "		if (this.value2 != null) {\r\n"+
            "			// val2\r\n"+
            "			var rect = new Rect(valWidth * count, 0, valWidth, this.height);\r\n"+
            "			rect.writeToElementRelatedToParent(this.val2);\r\n"+
            "			remainingWidth -= valWidth;\r\n"+
            "			count++;\r\n"+
            "		}\r\n"+
            "		// valdiv\r\n"+
            "		// give minimum 5px for left padding \r\n"+
            "		var rect = new Rect(5, 0, valWidth * count, this.height);\r\n"+
            "		rect.writeToElementRelatedToParent(this.valdiv);\r\n"+
            "	}\r\n"+
            "	remainingWidth -= this.padding + 5;// Account for padding;\r\n"+
            "	this.slidingdiv.style.marginLeft = toPx(this.padding);\r\n"+
            "	remainingWidth -= this.padding;\r\n"+
            "	this.slidingdiv.style.marginRight = toPx(this.padding);\r\n"+
            "	this.scale = (max(1,remainingWidth)) / (this.maxVal - this.minVal);\r\n"+
            "	if (this.sliderHidden != true) {\r\n"+
            "		// slidingdiv\r\n"+
            "		var rect = new Rect(0, 0, max(1,remainingWidth), this.height);\r\n"+
            "		rect.writeToElementRelatedToParent(this.slidingdiv);\r\n"+
            "		// track\r\n"+
            "		var rect = new Rect(0, (this.height - this.guideHeight) / 2, max(1,remainingWidth), this.guideHeight);\r\n"+
            "		rect.writeToElementRelatedToParent(this.guide);\r\n"+
            "\r\n"+
            "		var gW = this.value2 != null ? this.grabberWidth / 2 + 2 : this.grabberWidth;\r\n"+
            "		var gX = (this.value - this.minVal) * this.scale - this.grabberWidth / 2;\r\n"+
            "		var gX2 = this.value2 != null ? (this.value2 - this.minVal) * this.scale - 2 : null;\r\n"+
            "		// lowguide\r\n"+
            "		if (gX2 != null)\r\n"+
            "			rect = new Rect(gX + gW / 2, (this.height - this.guideHeight) / 2, (gX2 - gX), this.guideHeight);\r\n"+
            "		else\r\n"+
            "			rect = new Rect(0, (this.height - this.guideHeight) / 2, (this.value - this.minVal) * this.scale, this.guideHeight);\r\n"+
            "		rect.writeToElementRelatedToParent(this.lowguide);\r\n"+
            "\r\n"+
            "		if (gX != null) {\r\n"+
            "			// grabber\r\n"+
            "			var rect = new Rect(gX, (this.height - this.grabberWidth) / 2, gW, this.grabberWidth);\r\n"+
            "			rect.writeToElementRelatedToParent(this.grabber);\r\n"+
            "		}\r\n"+
            "		if (gX2 != null) {\r\n"+
            "			// grabber2\r\n"+
            "			var rect = new Rect(gX2, (this.height - this.grabberWidth) / 2, gW, this.grabberWidth);\r\n"+
            "			rect.writeToElementRelatedToParent(this.grabber2);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.initRootdivStyle = function() {\r\n"+
            "//	this.element.style.position = 'relative';\r\n"+
            "//	this.element.style.display = 'inline-block';\r\n"+
            "	// Rootdiv\r\n"+
            "	this.rootdiv.style.display = 'inline-block';\r\n"+
            "	this.rootdiv.style.position = this.element.style.position;\r\n"+
            "	applyStyle(this.rootdiv, \"_bg=transparent\");\r\n"+
            "	this.rootdiv.style.border = \"none\";\r\n"+
            "	// Input\r\n"+
            "	this.input.className = 'slider_input';\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.initNullableStyle = function() {\r\n"+
            "	// Nullable (checkbox)\r\n"+
            "	this.nullable.setAttribute(\"type\", \"checkbox\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.initSlidingdivStyle = function() {\r\n"+
            "	// Slidingdiv\r\n"+
            "	this.slidingdiv.style.display = \"inline-block\";\r\n"+
            "	this.slidingdiv.style.position = \"relative\";\r\n"+
            "	// Guides\r\n"+
            "	this.guide.style.borderRadius = \"3px\";\r\n"+
            "	this.lowguide.style.borderRadius = \"3px\";\r\n"+
            "	// Grabbers\r\n"+
            "	this.updateGrabberStyle();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.updateGrabberStyle=function() {\r\n"+
            "	if (this.value2 != null) {\r\n"+
            "		this.grabber.style.borderRadius = \"10px 0px 0px 10px\";\r\n"+
            "		this.grabber2.style.borderRadius = \"0px 10px 10px 0px\";\r\n"+
            "		this.grabber.style.width = \"8px\";\r\n"+
            "		this.grabber2.style.width = \"8px\";\r\n"+
            "	} else {\r\n"+
            "		this.grabber.style.borderRadius = \"10px\";\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.initValdivStyle = function() {\r\n"+
            "	// Valdiv\r\n"+
            "	this.valdiv.style.display = \"inline-block\";\r\n"+
            "	this.valdiv.style.position = \"relative\";\r\n"+
            "	// Vals\r\n"+
            "	this.val.style.position = \"absolute\";\r\n"+
            "	this.val2.style.position = \"absolute\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.initStyles = function() {\r\n"+
            "	this.initRootdivStyle();\r\n"+
            "	this.initNullableStyle();\r\n"+
            "	this.initSlidingdivStyle();\r\n"+
            "	this.initValdivStyle();\r\n"+
            "\r\n"+
            "	this.updateStyles();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.updateStyles = function() {\r\n"+
            "	this.updateSlidingdiv();\r\n"+
            "	this.updateValdiv();\r\n"+
            "	this.updateNullable();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.updateNullable = function() {\r\n"+
            "	this.nullable.style.display = !this.nullableEnabled ? \"none\" : \"inline-block\";\r\n"+
            "	if (this.nullableEnabled) {\r\n"+
            "		this.slidingdiv.style.opacity = this.isNull ? 0.4 : 1;\r\n"+
            "		this.lowguide.style.opacity = this.isNull ? 0 : 1;\r\n"+
            "		this.grabber.style.display = this.isNull ? \"none\" : \"inline\";\r\n"+
            "		this.grabber2.style.display = this.isNull || this.value2 == null? \"none\" : \"inline\";\r\n"+
            "		this.valdiv.style.display = this.isNull ? \"none\" : \"inline-block\";\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Slider.prototype.updateSlidingdiv = function() {\r\n"+
            "	this.slidingdiv.style.display = this.sliderHidden ? \"none\" : \"inline-block\";\r\n"+
            "	this.grabber2.style.display = this.value2 == null ? \"none\" : \"inline\";\r\n"+
            "}\r\n"+
            "Slider.prototype.updateValdiv = function() {\r\n"+
            "	this.valdiv.style.display = this.textHidden ? \"none\" : \"inline-block\";\r\n"+
            "	this.val.style.display = this.value == null ? \"none\" : \"inline\";\r\n"+
            "	this.val2.style.display = this.value2 == null ? \"none\" : \"inline\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.initNullable = function(that, nullable) {\r\n"+
            "	var that = this;\r\n"+
            "	this.nullable.checked = !this.isNull;\r\n"+
            "	this.nullable.onchange = function(e, isEnabled) {\r\n"+
            "		that.setValue(that.nullable.checked ? that.defaultValue : null, true, true, that.nullable.checked ? that.defaultValue2 : null);\r\n"+
            "	};\r\n"+
            "}\r\n"+
            "Slider.prototype.onNullChanged = function(isValueNull, fire) {\r\n"+
            "	this.isNull = (this.nullableEnabled && isValueNull);\r\n"+
            "	this.nullable.checked = !(isValueNull);\r\n"+
            "	this.updateNullable();\r\n"+
            "	if (fire)\r\n"+
            "		this.onNullableChanged((this.isNull == true) && (this.value != null) ? \"null\" : this.getValue(), (this.isNull == true) && (this.value2 != null) ? \"null\" : this.getValue2());\r\n"+
            "}\r\n"+
            "Slider.prototype.initGrabber = functio");
          out.print(
            "n(that) {\r\n"+
            "	this.grabber.ondraggingStart=function(dragElement){that.dragStartValue=that.value};\r\n"+
            "	this.grabber.ondragging=           function(dragElement,diffx,diffy,e){that.setValue(that.dragStartValue+diffx/that.scale,true,true,that.value2,true );that.input.selectionEnd=that.input.selectionStart;};\r\n"+
            "	this.grabber.ondraggingEnd=        function(dragElement,diffx,diffy,e){that.setValue(that.dragStartValue+diffx/that.scale,true,true,that.value2,false);that.dragStartValue=null;if(!isMouseInside(e,that.rootdiv)) that.hide();};\r\n"+
            "	makeDraggable(this.grabber, null, false, true);\r\n"+
            "}\r\n"+
            "Slider.prototype.initGrabber2 = function(that) {\r\n"+
            "	if (!isNaN(parseInt(this.value2))) {\r\n"+
            "		this.grabber2.activated= true;\r\n"+
            "		this.grabber2.ondraggingStart= function(dragElement){that.dragStartValue=that.value2};\r\n"+
            "		this.grabber2.ondragging=      function(dragElement,diffx,diffy,e){that.setValue(that.value,true,true,that.dragStartValue+diffx/that.scale,true );that.input.selectionEnd=that.input.selectionStart;};\r\n"+
            "		this.grabber2.ondraggingEnd=   function(dragElement,diffx,diffy,e){that.setValue(that.value,true,true,that.dragStartValue+diffx/that.scale,false);that.dragStartValue=null;if(!isMouseInside(e,that.rootdiv)) that.hide();};\r\n"+
            "		makeDraggable(this.grabber2, null, false, true);\r\n"+
            "		// apply grabber2 styles\r\n"+
            "		this.updateGrabberStyle();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Slider.prototype.initGuide = function() {\r\n"+
            "	var that = this;\r\n"+
            "	var func=function(e){that.onclick(e);};\r\n"+
            "	this.guide.onmousedown = func;\r\n"+
            "	this.lowguide.onmousedown = func;\r\n"+
            "}\r\n"+
            "Slider.prototype.initInput = function(that) {\r\n"+
            "	this.input.value = this.value;\r\n"+
            "	this.input.onclick=function(e){that.show(e);};\r\n"+
            "	this.input.onfocus=function(e){that.show(e);};\r\n"+
            "	this.input.onkeyup=function(e){that.onInputKeyUp(e);};\r\n"+
            "	this.input.onmouseout=function(e){that.onMouseout(e);};\r\n"+
            "	this.val.onchange=function(){that.setValue(that.val.value,true,true, that.val2.value)};\r\n"+
            "	this.val2.onchange=function(){that.setValue(that.val.value,true,true, that.val2.value)};\r\n"+
            "}\r\n"+
            "Slider.prototype.initRootdiv = function(that) {\r\n"+
            "	  this.rootdiv.onmouseout=function(e){that.onMouseout(e);};\r\n"+
            "	this.rootdiv.onMouseWheel = function(e, delta) {\r\n"+
            "		if(that.isNull != true){\r\n"+
            "			e.stopPropagation();\r\n"+
            "			if (that.value2 == null) {\r\n"+
            "		    	that.setValue(that.getValue()+that.scaleMouseWheelDelta(delta),true,true,that.value2,true);\r\n"+
            "				return;\r\n"+
            "			}\r\n"+
            "			var first;\r\n"+
            "		  	var x = e.clientX - new Rect().readFromElement(that.guide).left;\r\n"+
            "		  	var hoverVal = x / that.scale + that.minVal;\r\n"+
            "			if(e.srcElement==that.val) \r\n"+
            "				first=true;\r\n"+
            "			else if(e.srcElement==that.val2) \r\n"+
            "				first=false;\r\n"+
            "			else if (hoverVal >= that.value2) \r\n"+
            "				first=false;\r\n"+
            "			else if (hoverVal <= that.value) \r\n"+
            "				first=true;\r\n"+
            "			else \r\n"+
            "				first=Math.abs(hoverVal - that.value2) > Math.abs(hoverVal - that.value);\r\n"+
            "			if(that.value==that.value2){\r\n"+
            "				if(first == delta>0){\r\n"+
            "					that.setValue(that.getValue() + that.scaleMouseWheelDelta(delta), true, true, that.value2 + that.scaleMouseWheelDelta(delta),true);\r\n"+
            "					return;\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "			if(first)\r\n"+
            "				that.setValue(that.getValue() + that.scaleMouseWheelDelta(delta), true, true, that.value2,true);\r\n"+
            "			else \r\n"+
            "				that.setValue(that.getValue(), true, true, that.value2 + that.scaleMouseWheelDelta(delta),true);\r\n"+
            "			\r\n"+
            "		}\r\n"+
            "	};\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.setRange = function(minVal, maxVal,step,precision) {\r\n"+
            "	this.minVal = minVal;\r\n"+
            "	this.maxVal = maxVal;\r\n"+
            "	this.step = step;\r\n"+
            "	this.precision=precision;\r\n"+
            "	this.updateBounds();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.scaleMouseWheelDelta = function(delta) {\r\n"+
            "	return delta*this.step;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.close = function(e) {\r\n"+
            "	this.hide();\r\n"+
            "	this.element.removeChild(input);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.onMouseout = function(e) {\r\n"+
            "	var that=this;\r\n"+
            "	if (this.dragStartValue != null)\r\n"+
            "		return;\r\n"+
            "	var event = getMouseEvent(e);\r\n"+
            "	var e = event.toElement || event.relatedTarget;\r\n"+
            "	if (isChildOf(this.rootdiv, e))\r\n"+
            "		return;\r\n"+
            "	if(this.lastWasFromSliding)\r\n"+
            "      that.setValue(that.getValue(), true, true, that.value2,false);\r\n"+
            "	this.hide();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.onInputKeyUp = function(e) {\r\n"+
            "	if (e.keyCode == 27 || e.keyCode == 13)\r\n"+
            "		this.hide();\r\n"+
            "	if (e.keyCode == 13)\r\n"+
            "		this.input.blur();\r\n"+
            "  else this.setValue(this.input.value,false,true)\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.show = function() {\r\n"+
            "	if (!this.isShowing) {\r\n"+
            "		var rect = new Rect();\r\n"+
            "		rect.readFromElement(this.input);\r\n"+
            "		this.rootdiv.style.left = toPx(rect.getLeft());\r\n"+
            "		this.rootdiv.style.top = toPx(rect.getTop() + 16);\r\n"+
            "		document.body.appendChild(this.rootdiv);\r\n"+
            "	}\r\n"+
            "	this.isShowing = true;\r\n"+
            "}\r\n"+
            "Slider.prototype.hide = function() {\r\n"+
            "	if (this.isShowing && !this.alwaysShow)\r\n"+
            "		document.body.removeChild(this.rootdiv);\r\n"+
            "	this.isShowing = false;\r\n"+
            "}\r\n"+
            "Slider.prototype.setAutohide = function(autohide) {\r\n"+
            "	this.alwaysShow = !autohide;\r\n"+
            "	if (this.alwaysShow)\r\n"+
            "		this.show();\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.setValue = function(value, updateInput, fireOnChanged, value2,fromSliding) {\r\n"+
            "	// ensure within bounds\r\n"+
            "	if (value)\r\n"+
            "		value=clip(value,this.minVal,this.maxVal);\r\n"+
            "	if (value2)\r\n"+
            "		value2=clip(value2,this.minVal,this.maxVal);\r\n"+
            "	if (this.nullableEnabled) {\r\n"+
            "		if (this.isNull && (value != null || value2 != null)) {\r\n"+
            "			this.onNullChanged(false, fireOnChanged);\r\n"+
            "		} else if (!this.isNull && (value == null && value2 == null)) {\r\n"+
            "			this.onNullChanged(true, fireOnChanged);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	if (value2 != null && value2 !== \"\") {\r\n"+
            "		var valueN = parseFloat(value);\r\n"+
            "		var valueN2 = parseFloat(value2);\r\n"+
            "		if (!isNaN(valueN) && !isNaN(valueN2)) {\r\n"+
            "			// finite numbers\r\n"+
            "			if (value === this.value) {\r\n"+
            "				// if left val didn't change\r\n"+
            "				if (valueN2 < valueN) // if right val moved past left val\r\n"+
            "					value2 = value; // Ensure max slider is to the right of the min slider\r\n"+
            "			} else if (value2 === this.value2) { // if right val didn't change\r\n"+
            "				if (valueN > valueN2) // if left slider moved past right slider\r\n"+
            "					value = value2; // Ensure min slider is to the left of the max slider\r\n"+
            "			} else { // if both left and right sliders didn't move\r\n"+
            "				if (valueN > valueN2) { // if left slider moved past right slider\r\n"+
            "					value = this.value2; // Ensure min slider is to the left of the max slider\r\n"+
            "					value2 = this.value2; // ensure right slider stays?\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	// Set location of slider elements\r\n"+
            "	// this.step is 0 when we hit submit in the field editor. This is due to order of operation. setValue gets called first, then setRange\r\n"+
            "	value=this.applyStep(value); \r\n"+
            "	// ensure left and right sliders are within bounds\r\n"+
            "	this.value = value == null ? null : Math.min(Math.max(this.minVal, value), this.maxVal);\r\n"+
            "	if (value2 != null && value2 !== \"\"){\r\n"+
            "		value2=this.applyStep(value2);\r\n"+
            "		this.value2 = Math.min(Math.max(this.minVal, value2), this.maxVal);\r\n"+
            "		// try to init grabber2 if haven't already\r\n"+
            "		if (!this.grabber2.activated) {\r\n"+
            "			var that=this;\r\n"+
            "			this.initGrabber2(that);\r\n"+
            "		}\r\n"+
            "		if(value>value2)\r\n"+
            "		  this.value=value=value2;\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	var fval = value == null ? null : this.value.toFixed(this.precision) * 1;\r\n"+
            "	var f2val = (value2 == null || value2 === \"\") ? null : this.value2.toFixed(this.precision) * 1;\r\n"+
            "	if (updateInput)\r\n"+
            "		this.input.value = fval;\r\n"+
            "	// show decimal\r\n"+
            "	this.val.value = fval == null? null: fval.toFixed(this.precision);\r\n"+
            "	this.val2.value=f2val == null? null: f2val.toFixed(this.precision); \r\n"+
            "\r\n"+
            "	this.updateBounds();\r\n"+
            "	this.updateStyles();\r\n"+
            "\r\n"+
            "	this.lastWasFromSliding=fromSliding==true;\r\n"+
            "	if (this.onValueChanged && fireOnChanged){\r\n"+
            "		this.onValueChanged(this.value == null ? \"null\" : this.value, f2val,fromSliding==true);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.applyStep = function(value) {\r\n"+
            "	if(value==null)\r\n"+
            "		return null;\r\n"+
            "	value-=this.minVal;\r\n"+
            "	value/=this.step;\r\n"+
            "	value=Math.round(value);\r\n"+
            "	value*=this.step;\r\n"+
            "	value+=this.minVal;\r\n"+
            "	return value;\r\n"+
            "}\r\n"+
            "Slider.prototype.getValue = function() {\r\n"+
            "	if (this.value == null)\r\n"+
            "		return null;\r\n"+
            "	return this.value.toFixed(this.precision) * 1;\r\n"+
            "}\r\n"+
            "Slider.prototype.getValue2 = function() {\r\n"+
            "	if (this.value2 == null || this.precision == null)\r\n"+
            "		return null;\r\n"+
            "	return this.value2.toFixed(this.precision) * 1;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Slider.prototype.getMaxValue = function() {\r\n"+
            "	return this.value2 > this.value ? this.value2 : this.value;\r\n"+
            "}\r\n"+
            "Slider.prototype.getMinValue = function() {\r\n"+
            "	return this.value2 <= this.value ? this.value2 : this.value;\r\n"+
            "}\r\n"+
            "Slider.prototype.onclick = function(e) {\r\n"+
            "	var target = getMouseTarget(e);\r\n"+
            "	if (target == this.guide || target == this.lowguide) {\r\n"+
            "		var x = getMousePoint(e).x - new Rect().readFromElement(this.guide).left;\r\n"+
            "		var clickVal = x / this.scale + this.minVal;\r\n"+
            "		if (this.isNull)\r\n"+
            "			this.nullable.checked = true;\r\n"+
            "		if (this.value2 != null) {\r\n"+
            "		    if(Math.abs(clickVal-this.value) > Math.abs(clickVal-this.value2)){\r\n"+
            "				this.setValue(this.value, true, true, clickVal);\r\n"+
            "			} else {\r\n"+
            "				this.setValue(clickVal, true, true, this.value2);\r\n"+
            "			}\r\n"+
            "		} else {\r\n"+
            "			this.setValue(clickVal, true, true, this.value2);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	// this.setValue(x/this.scale,true,true,this.value2);\r\n"+
            "	// }else if(target==this.max){\r\n"+
            "	// this.setValue(this.value,true,true,this.);\r\n"+
            "	// }else if(target==this.min){\r\n"+
            "	// this.setValue(this.minVal,true,true,value2);\r\n"+
            "	// }\r\n"+
            "}\r\n"+
            "Slider.prototype.resize = function(width, textHidden) {\r\n"+
            "	this.width = width;\r\n"+
            "	this.textHidden = textHidden;\r\n"+
            "	this.updateBounds();\r\n"+
            "	this.updateValdiv();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Slider.prototype.setSliderHidden = function(sliderHidden) {\r\n"+
            "	if (sliderHidden == this.sliderHidden)\r\n"+
            "		return;\r\n"+
            "	this.sliderHidden=sliderHidden;\r\n"+
            "	this.updateBounds();\r\n"+
            "	this.updateStyles();\r\n"+
            "	\r\n"+
            "}");

	}
	
}