function Slider(element, value, minVal, maxVal, widthPx, precision,step, textHidden, value2, sliderHidden, nullable, defaultValue, defaultValue2) {
	var that = this;
	this.element = element;
	this.defaultValue = defaultValue;
	this.defaultValue2 = defaultValue2;
	this.value = value;
	this.value2 = value2;
	this.width = widthPx;
	this.textHidden = textHidden;
	this.sliderHidden = sliderHidden;
	this.nullableEnabled = nullable;
	this.initConstants();
	this.initDomElements();
	this.initStyles();
	this.isNull = value == null && value2 == null;
	this.setRange(minVal,maxVal,step,precision);
	this.initRootdiv(that);
	this.initNullable(that, nullable);
	this.initInput(that);
	this.initGuide(that);
	this.initGrabber(that);
	// separate init for the two grabbers. Need separate check for grabber2
	this.initGrabber2(that);
	this.setValue(minVal, true, false, value2);
}

// MAIN
Slider.prototype.value;
Slider.prototype.value2;
Slider.prototype.minVal;
Slider.prototype.maxVal;
Slider.prototype.precision;

// DOMELEMENTS
Slider.prototype.element;
Slider.prototype.rootdiv;
Slider.prototype.slidingdiv;
Slider.prototype.guide;
Slider.prototype.lowguide;
Slider.prototype.grabber;
Slider.prototype.grabber2;
Slider.prototype.valdiv;

Slider.prototype.val;
Slider.prototype.val2;
Slider.prototype.nullable;

// OPTIONS
Slider.prototype.isShowing;
Slider.prototype.alwaysShow;
Slider.prototype.sliderHidden;
Slider.prototype.textHidden;
Slider.prototype.isNull;
Slider.prototype.nullableEnabled;

// STYLE
Slider.prototype.width;
Slider.prototype.height;
Slider.prototype.guideHeight;
Slider.prototype.grabberWidth = 12;
Slider.prototype.padding = 3;
Slider.prototype.textSize;

Slider.prototype.initConstants = function() {
	this.alwaysShow = true;
	this.height = 15;
	this.textSize = 0;
	this.grabberWidth = 12;
	this.guideHeight = 6;
	this.padding = 6;
	this.isShowing = false;
	this.isNull = false;
}

Slider.prototype.initDomElements = function() {
	this.rootdiv = nwDiv('slider', 0, 0, this.width, this.height);
	this.slidingdiv = nwDiv('', 0, 0, null, null);
	this.guide = nwDiv('slider_guide', 0, 0, null, null);
	this.lowguide = nwDiv('slider_guide', 0, 0, null, null);
	this.grabber = nwDiv('slider_grabber', 0, 0, null, null);
	this.grabber2 = nwDiv('slider_grabber', 0, 0, null, null);
	// see if grabber2 is active
	this.grabber2.activated=false;
	this.input = nw('input');
	this.valdiv = nwDiv('', 0, 0, null, null);
	this.val = nw('input', 'slider_val');
	this.val2 = nw('input', 'slider_val');
	this.nullable = nw('input');
	var that=this;
	this.val.onkeydown=function(e){if(that.onkeydown)that.onkeydown(e);};

	this.rootdiv.appendChild(this.nullable);
	this.rootdiv.appendChild(this.slidingdiv);
	this.slidingdiv.appendChild(this.guide);
	this.slidingdiv.appendChild(this.lowguide);
	this.slidingdiv.appendChild(this.grabber);
	this.slidingdiv.appendChild(this.grabber2);
	this.rootdiv.appendChild(this.valdiv);
	this.valdiv.appendChild(this.val);
	this.valdiv.appendChild(this.val2);
	this.element.appendChild(this.rootdiv);
}

Slider.prototype.setHeight = function(height) {
	this.height = height;
	this.updateBounds();
//	this.updateStyles();
}
Slider.prototype.setWidth = function(width) {
	this.width = width;
	this.updateBounds();
}

// controls slider movement
Slider.prototype.updateBounds = function() {
	var remainingWidth = this.width;
	var rect = new Rect(0, 0, this.width, this.height);
	rect.writeToElementRelatedToParent(this.rootdiv);

	if (this.nullableEnabled) {
		// nullable
		var checkboxWidth = 13;
		remainingWidth -= checkboxWidth;
	}
	if (this.textHidden != true) {
		var valWidth;
		var maxValWidth;
		if (this.sliderHidden != true) {
			valWidth = (Math.log10(Math.abs(this.maxVal)) + (this.precision == 0 ? 0 : (this.precision + 1))) * 7 + 10;
			var valWidth2 = (Math.log10(Math.abs(this.minVal)) + (this.precision == 0 ? 0 : (this.precision + 1))) * 7 + 10;
			valWidth = valWidth > valWidth2 ? valWidth : valWidth2;
			//		valWidth = valWidth / 15 * this.height;
			
			maxValWidth = this.value1 != null? this.width/3:this.width/2;
		}else{
			valWidth = this.width; 
			maxValWidth = this.value1 != null? this.width/2:this.width;
		}
		
		valWidth = valWidth > maxValWidth ? maxValWidth : valWidth;
		
		var count = 0;
		if (this.value != null) {
			// val
			var rect = new Rect(0, 0, valWidth, this.height);
			rect.writeToElementRelatedToParent(this.val);
			remainingWidth -= valWidth;
			count++;
		}

		if (this.value2 != null) {
			// val2
			var rect = new Rect(valWidth * count, 0, valWidth, this.height);
			rect.writeToElementRelatedToParent(this.val2);
			remainingWidth -= valWidth;
			count++;
		}
		// valdiv
		// give minimum 5px for left padding 
		var rect = new Rect(5, 0, valWidth * count, this.height);
		rect.writeToElementRelatedToParent(this.valdiv);
	}
	remainingWidth -= this.padding + 5;// Account for padding;
	this.slidingdiv.style.marginLeft = toPx(this.padding);
	remainingWidth -= this.padding;
	this.slidingdiv.style.marginRight = toPx(this.padding);
	this.scale = (max(1,remainingWidth)) / (this.maxVal - this.minVal);
	if (this.sliderHidden != true) {
		// slidingdiv
		var rect = new Rect(0, 0, max(1,remainingWidth), this.height);
		rect.writeToElementRelatedToParent(this.slidingdiv);
		// track
		var rect = new Rect(0, (this.height - this.guideHeight) / 2, max(1,remainingWidth), this.guideHeight);
		rect.writeToElementRelatedToParent(this.guide);

		var gW = this.value2 != null ? this.grabberWidth / 2 + 2 : this.grabberWidth;
		var gX = (this.value - this.minVal) * this.scale - this.grabberWidth / 2;
		var gX2 = this.value2 != null ? (this.value2 - this.minVal) * this.scale - 2 : null;
		// lowguide
		if (gX2 != null)
			rect = new Rect(gX + gW / 2, (this.height - this.guideHeight) / 2, (gX2 - gX), this.guideHeight);
		else
			rect = new Rect(0, (this.height - this.guideHeight) / 2, (this.value - this.minVal) * this.scale, this.guideHeight);
		rect.writeToElementRelatedToParent(this.lowguide);

		if (gX != null) {
			// grabber
			var rect = new Rect(gX, (this.height - this.grabberWidth) / 2, gW, this.grabberWidth);
			rect.writeToElementRelatedToParent(this.grabber);
		}
		if (gX2 != null) {
			// grabber2
			var rect = new Rect(gX2, (this.height - this.grabberWidth) / 2, gW, this.grabberWidth);
			rect.writeToElementRelatedToParent(this.grabber2);
		}
	}
}

Slider.prototype.initRootdivStyle = function() {
//	this.element.style.position = 'relative';
//	this.element.style.display = 'inline-block';
	// Rootdiv
	this.rootdiv.style.display = 'inline-block';
	this.rootdiv.style.position = this.element.style.position;
	applyStyle(this.rootdiv, "_bg=transparent");
	this.rootdiv.style.border = "none";
	// Input
	this.input.className = 'slider_input';
}

Slider.prototype.initNullableStyle = function() {
	// Nullable (checkbox)
	this.nullable.setAttribute("type", "checkbox");
}

Slider.prototype.initSlidingdivStyle = function() {
	// Slidingdiv
	this.slidingdiv.style.display = "inline-block";
	this.slidingdiv.style.position = "relative";
	// Guides
	this.guide.style.borderRadius = "3px";
	this.lowguide.style.borderRadius = "3px";
	// Grabbers
	this.updateGrabberStyle();
}

Slider.prototype.updateGrabberStyle=function() {
	if (this.value2 != null) {
		this.grabber.style.borderRadius = "10px 0px 0px 10px";
		this.grabber2.style.borderRadius = "0px 10px 10px 0px";
		this.grabber.style.width = "8px";
		this.grabber2.style.width = "8px";
	} else {
		this.grabber.style.borderRadius = "10px";
	}
}

Slider.prototype.initValdivStyle = function() {
	// Valdiv
	this.valdiv.style.display = "inline-block";
	this.valdiv.style.position = "relative";
	// Vals
	this.val.style.position = "absolute";
	this.val2.style.position = "absolute";
}

Slider.prototype.initStyles = function() {
	this.initRootdivStyle();
	this.initNullableStyle();
	this.initSlidingdivStyle();
	this.initValdivStyle();

	this.updateStyles();
}

Slider.prototype.updateStyles = function() {
	this.updateSlidingdiv();
	this.updateValdiv();
	this.updateNullable();
}

Slider.prototype.updateNullable = function() {
	this.nullable.style.display = !this.nullableEnabled ? "none" : "inline-block";
	if (this.nullableEnabled) {
		this.slidingdiv.style.opacity = this.isNull ? 0.4 : 1;
		this.lowguide.style.opacity = this.isNull ? 0 : 1;
		this.grabber.style.display = this.isNull ? "none" : "inline";
		this.grabber2.style.display = this.isNull || this.value2 == null? "none" : "inline";
		this.valdiv.style.display = this.isNull ? "none" : "inline-block";
	}
}
Slider.prototype.updateSlidingdiv = function() {
	this.slidingdiv.style.display = this.sliderHidden ? "none" : "inline-block";
	this.grabber2.style.display = this.value2 == null ? "none" : "inline";
}
Slider.prototype.updateValdiv = function() {
	this.valdiv.style.display = this.textHidden ? "none" : "inline-block";
	this.val.style.display = this.value == null ? "none" : "inline";
	this.val2.style.display = this.value2 == null ? "none" : "inline";
}

Slider.prototype.initNullable = function(that, nullable) {
	var that = this;
	this.nullable.checked = !this.isNull;
	this.nullable.onchange = function(e, isEnabled) {
		that.setValue(that.nullable.checked ? that.defaultValue : null, true, true, that.nullable.checked ? that.defaultValue2 : null);
	};
}
Slider.prototype.onNullChanged = function(isValueNull, fire) {
	this.isNull = (this.nullableEnabled && isValueNull);
	this.nullable.checked = !(isValueNull);
	this.updateNullable();
	if (fire)
		this.onNullableChanged((this.isNull == true) && (this.value != null) ? "null" : this.getValue(), (this.isNull == true) && (this.value2 != null) ? "null" : this.getValue2());
}
Slider.prototype.initGrabber = function(that) {
	this.grabber.ondraggingStart=function(dragElement){that.dragStartValue=that.value};
	this.grabber.ondragging=           function(dragElement,diffx,diffy,e){that.setValue(that.dragStartValue+diffx/that.scale,true,true,that.value2,true );that.input.selectionEnd=that.input.selectionStart;};
	this.grabber.ondraggingEnd=        function(dragElement,diffx,diffy,e){that.setValue(that.dragStartValue+diffx/that.scale,true,true,that.value2,false);that.dragStartValue=null;if(!isMouseInside(e,that.rootdiv)) that.hide();};
	makeDraggable(this.grabber, null, false, true);
}
Slider.prototype.initGrabber2 = function(that) {
	if (!isNaN(parseInt(this.value2))) {
		this.grabber2.activated= true;
		this.grabber2.ondraggingStart= function(dragElement){that.dragStartValue=that.value2};
		this.grabber2.ondragging=      function(dragElement,diffx,diffy,e){that.setValue(that.value,true,true,that.dragStartValue+diffx/that.scale,true );that.input.selectionEnd=that.input.selectionStart;};
		this.grabber2.ondraggingEnd=   function(dragElement,diffx,diffy,e){that.setValue(that.value,true,true,that.dragStartValue+diffx/that.scale,false);that.dragStartValue=null;if(!isMouseInside(e,that.rootdiv)) that.hide();};
		makeDraggable(this.grabber2, null, false, true);
		// apply grabber2 styles
		this.updateGrabberStyle();
	}
}
Slider.prototype.initGuide = function() {
	var that = this;
	var func=function(e){that.onclick(e);};
	this.guide.onmousedown = func;
	this.lowguide.onmousedown = func;
}
Slider.prototype.initInput = function(that) {
	this.input.value = this.value;
	this.input.onclick=function(e){that.show(e);};
	this.input.onfocus=function(e){that.show(e);};
	this.input.onkeyup=function(e){that.onInputKeyUp(e);};
	this.input.onmouseout=function(e){that.onMouseout(e);};
	this.val.onchange=function(){that.setValue(that.val.value,true,true, that.val2.value)};
	this.val2.onchange=function(){that.setValue(that.val.value,true,true, that.val2.value)};
}
Slider.prototype.initRootdiv = function(that) {
	  this.rootdiv.onmouseout=function(e){that.onMouseout(e);};
	this.rootdiv.onMouseWheel = function(e, delta) {
		if(that.isNull != true){
			e.stopPropagation();
			if (that.value2 == null) {
		    	that.setValue(that.getValue()+that.scaleMouseWheelDelta(delta),true,true,that.value2,true);
				return;
			}
			var first;
		  	var x = e.clientX - new Rect().readFromElement(that.guide).left;
		  	var hoverVal = x / that.scale + that.minVal;
			if(e.srcElement==that.val) 
				first=true;
			else if(e.srcElement==that.val2) 
				first=false;
			else if (hoverVal >= that.value2) 
				first=false;
			else if (hoverVal <= that.value) 
				first=true;
			else 
				first=Math.abs(hoverVal - that.value2) > Math.abs(hoverVal - that.value);
			if(that.value==that.value2){
				if(first == delta>0){
					that.setValue(that.getValue() + that.scaleMouseWheelDelta(delta), true, true, that.value2 + that.scaleMouseWheelDelta(delta),true);
					return;
				}
			}
			if(first)
				that.setValue(that.getValue() + that.scaleMouseWheelDelta(delta), true, true, that.value2,true);
			else 
				that.setValue(that.getValue(), true, true, that.value2 + that.scaleMouseWheelDelta(delta),true);
			
		}
	};
}

Slider.prototype.setRange = function(minVal, maxVal,step,precision) {
	this.minVal = minVal;
	this.maxVal = maxVal;
	this.step = step;
	this.precision=precision;
	this.updateBounds();
}

Slider.prototype.scaleMouseWheelDelta = function(delta) {
	return delta*this.step;
}

Slider.prototype.close = function(e) {
	this.hide();
	this.element.removeChild(input);
}

Slider.prototype.onMouseout = function(e) {
	var that=this;
	if (this.dragStartValue != null)
		return;
	var event = getMouseEvent(e);
	var e = event.toElement || event.relatedTarget;
	if (isChildOf(this.rootdiv, e))
		return;
	if(this.lastWasFromSliding)
      that.setValue(that.getValue(), true, true, that.value2,false);
	this.hide();
}

Slider.prototype.onInputKeyUp = function(e) {
	if (e.keyCode == 27 || e.keyCode == 13)
		this.hide();
	if (e.keyCode == 13)
		this.input.blur();
  else this.setValue(this.input.value,false,true)
}

Slider.prototype.show = function() {
	if (!this.isShowing) {
		var rect = new Rect();
		rect.readFromElement(this.input);
		this.rootdiv.style.left = toPx(rect.getLeft());
		this.rootdiv.style.top = toPx(rect.getTop() + 16);
		document.body.appendChild(this.rootdiv);
	}
	this.isShowing = true;
}
Slider.prototype.hide = function() {
	if (this.isShowing && !this.alwaysShow)
		document.body.removeChild(this.rootdiv);
	this.isShowing = false;
}
Slider.prototype.setAutohide = function(autohide) {
	this.alwaysShow = !autohide;
	if (this.alwaysShow)
		this.show();
}

Slider.prototype.setValue = function(value, updateInput, fireOnChanged, value2,fromSliding) {
	// ensure within bounds
	if (value)
		value=clip(value,this.minVal,this.maxVal);
	if (value2)
		value2=clip(value2,this.minVal,this.maxVal);
	if (this.nullableEnabled) {
		if (this.isNull && (value != null || value2 != null)) {
			this.onNullChanged(false, fireOnChanged);
		} else if (!this.isNull && (value == null && value2 == null)) {
			this.onNullChanged(true, fireOnChanged);
		}
	}
	if (value2 != null && value2 !== "") {
		var valueN = parseFloat(value);
		var valueN2 = parseFloat(value2);
		if (!isNaN(valueN) && !isNaN(valueN2)) {
			// finite numbers
			if (value === this.value) {
				// if left val didn't change
				if (valueN2 < valueN) // if right val moved past left val
					value2 = value; // Ensure max slider is to the right of the min slider
			} else if (value2 === this.value2) { // if right val didn't change
				if (valueN > valueN2) // if left slider moved past right slider
					value = value2; // Ensure min slider is to the left of the max slider
			} else { // if both left and right sliders didn't move
				if (valueN > valueN2) { // if left slider moved past right slider
					value = this.value2; // Ensure min slider is to the left of the max slider
					value2 = this.value2; // ensure right slider stays?
				}
			}
		}
	}
	// Set location of slider elements
	// this.step is 0 when we hit submit in the field editor. This is due to order of operation. setValue gets called first, then setRange
	value=this.applyStep(value); 
	// ensure left and right sliders are within bounds
	this.value = value == null ? null : Math.min(Math.max(this.minVal, value), this.maxVal);
	if (value2 != null && value2 !== ""){
		value2=this.applyStep(value2);
		this.value2 = Math.min(Math.max(this.minVal, value2), this.maxVal);
		// try to init grabber2 if haven't already
		if (!this.grabber2.activated) {
			var that=this;
			this.initGrabber2(that);
		}
		if(value>value2)
		  this.value=value=value2;
	}

	var fval = value == null ? null : this.value.toFixed(this.precision) * 1;
	var f2val = (value2 == null || value2 === "") ? null : this.value2.toFixed(this.precision) * 1;
	if (updateInput)
		this.input.value = fval;
	// show decimal
	this.val.value = fval == null? null: fval.toFixed(this.precision);
	this.val2.value=f2val == null? null: f2val.toFixed(this.precision); 

	this.updateBounds();
	this.updateStyles();

	this.lastWasFromSliding=fromSliding==true;
	if (this.onValueChanged && fireOnChanged){
		this.onValueChanged(this.value == null ? "null" : this.value, f2val,fromSliding==true);
	}
}

Slider.prototype.applyStep = function(value) {
	if(value==null)
		return null;
	value-=this.minVal;
	value/=this.step;
	value=Math.round(value);
	value*=this.step;
	value+=this.minVal;
	return value;
}
Slider.prototype.getValue = function() {
	if (this.value == null)
		return null;
	return this.value.toFixed(this.precision) * 1;
}
Slider.prototype.getValue2 = function() {
	if (this.value2 == null || this.precision == null)
		return null;
	return this.value2.toFixed(this.precision) * 1;
}

Slider.prototype.getMaxValue = function() {
	return this.value2 > this.value ? this.value2 : this.value;
}
Slider.prototype.getMinValue = function() {
	return this.value2 <= this.value ? this.value2 : this.value;
}
Slider.prototype.onclick = function(e) {
	var target = getMouseTarget(e);
	if (target == this.guide || target == this.lowguide) {
		var x = getMousePoint(e).x - new Rect().readFromElement(this.guide).left;
		var clickVal = x / this.scale + this.minVal;
		if (this.isNull)
			this.nullable.checked = true;
		if (this.value2 != null) {
		    if(Math.abs(clickVal-this.value) > Math.abs(clickVal-this.value2)){
				this.setValue(this.value, true, true, clickVal);
			} else {
				this.setValue(clickVal, true, true, this.value2);
			}
		} else {
			this.setValue(clickVal, true, true, this.value2);
		}
	}
	// this.setValue(x/this.scale,true,true,this.value2);
	// }else if(target==this.max){
	// this.setValue(this.value,true,true,this.);
	// }else if(target==this.min){
	// this.setValue(this.minVal,true,true,value2);
	// }
}
Slider.prototype.resize = function(width, textHidden) {
	this.width = width;
	this.textHidden = textHidden;
	this.updateBounds();
	this.updateValdiv();
}


Slider.prototype.setSliderHidden = function(sliderHidden) {
	if (sliderHidden == this.sliderHidden)
		return;
	this.sliderHidden=sliderHidden;
	this.updateBounds();
	this.updateStyles();
	
}