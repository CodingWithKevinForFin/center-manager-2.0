function AmiChartLayer_Legend(){
	var that=this;
	this.isContainer=false;
	this.fontStyle="";
	this.nameColor="#000000";
	this.fontSize=20;
	this.fontFamily="arial";
	this.canvas=nw('div', 'legend');
	this.canvas.style.position='absolute';
  	this.resizerDim;
  	this.resizerPadding;
  	this.resizer=nw('div', 'legend_resizer');
  	this.isResizing=false;
  	this.isEventActive = false;
  	this.isResizerVisible = false;
  	this.isMouseIn=false;
  	this.canvas.appendChild(this.resizer);
  	this.titleDiv=nw('div', 'legend_title');
  	this.canvas.appendChild(this.titleDiv);
  	this.seriesDiv=nw('div', 'legend_series');
  	this.canvas.appendChild(this.seriesDiv);
  	this.makeDraggable(this.canvas);
  	this.makeResizeable(this.resizer);
  	this.canvas.onmouseenter = function() {
  		that.isMouseIn = true;
  		that.showResizer();
  		that.enableSmartOpacity();
  	}
  	this.canvas.onmouseleave = function() {
  		that.isMouseIn = false;
  		if (!that.isEventActive) {
  			that.hideResizer();
  			that.disableSmartOpacity();
  		}
  	}
}
AmiChartLayer_Legend.prototype.plot;

AmiChartLayer_Legend.prototype.init=function(options){
  this.keyPosition=options.keyPosition;
  this.fontFamily=options.fontFamily;
  this.opacity=options.opacity/100;
  this.x=options.x;
  this.y=options.y;
  this.w=options.w;
  this.h=options.h;
  this.plotWidth=options.pltWd;
  this.plotHeight=options.pltHt;
  this.fontFamily=options.fontFamily;
  this.namePosition=options.namePosition;
  this.seriesNames=options.seriesNames;
  this.series=options.series;
  this.name=options.name;
  this.nameColor=options.nameColor;
  this.nameSize=options.nameSize;
  this.labelSize=options.labelSize;
  this.borderColor=options.borderColor;
  this.backgroundColor=options.backgroundColor;
  this.checkboxColor=options.checkboxColor;
  this.checkboxCheckColor=options.checkboxCheckColor;
  this.checkboxBorderColor=options.checkboxBorderColor;
  this.r=options.r;
  this.g=options.g;
  this.b=options.b;
  this.hPadding=options.hPadding;
  this.vPadding=options.vPadding;
  this.setSize(this.w, this.h);
}
AmiChartLayer_Legend.prototype.showResizer=function(){
	this.resizer.style.display = "block";
	this.isResizerVisible = true;
}
AmiChartLayer_Legend.prototype.hideResizer=function(){
	this.resizer.style.display = "none";
	this.isResizerVisible = false;
}
AmiChartLayer_Legend.prototype.enableSmartOpacity = function() {
  	this.setOpacity(1);
  	this.setSmartOpacity(this.opacity);
}
AmiChartLayer_Legend.prototype.disableSmartOpacity = function() {
  	this.setSmartOpacity(1);
  	this.setOpacity(this.opacity);
}
AmiChartLayer_Legend.prototype.setSmartOpacity=function(opacity){
	var bg ="rgba(" + this.r + "," + this.g + "," + this.b + "," + opacity + ")";
	this.canvas.style.backgroundColor=bg;
}
AmiChartLayer_Legend.prototype.setOpacity=function(opacity){
	this.canvas.style.opacity=opacity;
}
// ensures legend stays within the chart/plot
AmiChartLayer_Legend.prototype.ensureVisible=function(x, y) {
	if (this.isResizing) {
		if (this.canvas.offsetLeft + this.w > this.plotWidth - this.hPadding)
			this.setWidth(this.plotWidth - this.canvas.offsetLeft - this.hPadding);
		if (this.canvas.offsetTop + this.h > this.plotHeight - this.vPadding) 
			this.setHeight(this.plotHeight - this.canvas.offsetTop - this.vPadding);
	} else { // dragging
		if (x < this.hPadding) 
			this.canvas.style.left = toPx(this.hPadding);
		if (y < this.vPadding)
			this.canvas.style.top = toPx(this.vPadding);
		if (y + this.h > this.plotHeight - this.vPadding) 
			this.canvas.style.top = toPx(this.plotHeight - this.vPadding - this.h);
		if (x + this.w > this.plotWidth - this.hPadding)
			this.canvas.style.left = toPx(this.plotWidth - this.hPadding - this.w);
	}
}
AmiChartLayer_Legend.prototype.setSize=function(width,height){
	this.canvas.style.width=toPx(width);
	this.canvas.style.height=toPx(height);
	this.w = width;
	this.h = height;
	this.resizerDim=12;
	this.resizerPadding=8;
}
AmiChartLayer_Legend.prototype.setWidth=function(width) {
	this.canvas.style.width=toPx(width);
	this.w=width;
}
AmiChartLayer_Legend.prototype.setHeight=function(height) {
	this.canvas.style.height=toPx(height);
	this.h=height;
}
/* returns true if legend dimension (either width or height) ends up being greater than the plot dimension*/
AmiChartLayer_Legend.prototype.isLegendOversized=function() {
	return this.w + 2 * this.hPadding > this.plotWidth || this.h + 2 * this.vPadding > this.plotHeight;
}
/* returns true if any of the sides gets clipped. (useful when legend is dragged super fast to an edge)*/
AmiChartLayer_Legend.prototype.isAnySideClipped=function() {
	return this.isTopClipped() || this.isRightClipped() || this.isBottomClipped() || this.isLeftClipped(); 
}
AmiChartLayer_Legend.prototype.isTopClipped=function() {
	return this.canvas.offsetTop - this.vPadding < 0;
}
AmiChartLayer_Legend.prototype.isRightClipped=function() {
	return this.canvas.offsetLeft + this.w + this.hPadding > this.plotWidth; 
}
AmiChartLayer_Legend.prototype.isBottomClipped=function() {
	return this.canvas.offsetTop + this.h + this.vPadding > this.plotHeight;
}
AmiChartLayer_Legend.prototype.isLeftClipped=function() {
	return this.canvas.offsetLeft - this.hPadding < 0;
}
AmiChartLayer_Legend.prototype.addTitle=function() {
	this.titleDiv.innerHTML="";
	if (this.namePosition != -1 && this.name  && this.nameColor) {
		this.titleDiv.appendChild(document.createTextNode(this.name));
		applyStyle(this.titleDiv, "_fg="+this.nameColor);
		applyStyle(this.titleDiv, "_fm="+this.fontStyle);
		applyStyle(this.titleDiv, "_fs="+this.nameSize);
		if (this.namePosition == 5)
			this.titleDiv.style.textAlign = 'left';
		 else if (this.namePosition == 9) 
			this.titleDiv.style.textAlign = 'right';
		 else 
			this.titleDiv.style.textAlign = 'center';
	}
}
AmiChartLayer_Legend.prototype.addSeries=function() {
	this.seriesDiv.innerHTML="";
	var seriesContainer=nw("ul");
	this.seriesDiv.appendChild(seriesContainer);
	
	for (var i in this.series) {
		var t = this.series[i];
		// creating dom elements
		var series = nw('li', 'series');
		var seriesShape = nw('canvas', 'series_shape');
		var seriesName = nw('span', 'series_name');
		var seriesCheckboxWrapper = nw('div', 'series_checkbox_wrapper');
		var checkMark = nw('div', 'checkMark');
		var seriesCheckbox = nw('input', 'series_checkbox');
		seriesCheckbox.type = 'checkbox';
		seriesCheckbox.value = '';
		seriesName.innerHTML = this.series[i].name;
		seriesCheckboxWrapper.appendChild(seriesCheckbox);
		seriesCheckboxWrapper.appendChild(checkMark);
		series.appendChild(seriesCheckboxWrapper);
		//series.appendChild(seriesCheckbox);
		if (t.lineSize > 0 || t.dash || t.shape != null)
			series.appendChild(seriesShape);
		series.appendChild(seriesName);
		seriesContainer.appendChild(series);
		if (t.checked) {
			seriesCheckbox.checked = true;
			checkMark.style.display = "block";
		} else
			checkMark.style.display = "none";
			
		seriesCheckbox.onclick = this.handleCheckbox.bind(this, t.grouping, t.series, seriesCheckbox);
		checkMark.onclick = this.handleCheckMark.bind(this, t.grouping, t.series, seriesCheckbox, seriesCheckboxWrapper, checkMark);
		// checkbox styling
		seriesCheckboxWrapper.style.backgroundColor = this.checkboxColor;
		seriesCheckboxWrapper.style.border = "1px solid " + this.checkboxBorderColor;
		checkMark.style.borderBottom = "2px solid " + this.checkboxCheckColor;
		checkMark.style.borderRight = "2px solid " + this.checkboxCheckColor;
		
		
		// adjust dimension on the canvas containing shape based on labelSize
		if (this.labelSize == 0) {
			seriesShape.width = 0;
			seriesShape.height = 0;
		} else if (this.labelSize < 30) { 
			seriesShape.width = 25;
			seriesShape.height = 12;
		} else {
			seriesShape.width = this.labelSize;
			seriesShape.height = seriesName.getBoundingClientRect().height / 2;
		}
		var ct = seriesShape.getContext('2d');
		ct.strokeStyle=t.color ? t.color : (t.shapeColor ? t.shapeColor : "#000000");
		ct.fillStyle=t.shapeColor ? t.shapeColor : (t.color ? t.color : "#000000");
		// horizontal line
		if (t.lineSize > 0 || t.dash) {
			ct.beginPath();
			ct.lineWidth = 1;
	    	if (t.dash)
	    		ct.setLineDash([ct.lineWidth,3]);
	    	ct.moveTo(0, seriesShape.height/2 + .5); // adding .5 to make the line crisp
	    	ct.lineTo(seriesShape.width, seriesShape.height/2 + .5);
	    	ct.closePath();
	    	ct.stroke();
		}
	    // drawing shape begins
    	if (t.shape != null) {
	    	ct.beginPath();
			var offsetWidth = 9; // left and right padding of the shape (excluding the horizontal bar)
			var offsetHeight = 2; // top and bottom padding ^^^
			if (t.border)
				ct.strokeStyle = t.border;
			if (t.shape=='square') {				
				ct.moveTo(seriesShape.width/2, offsetHeight);
				ct.lineTo(seriesShape.width - offsetWidth, offsetHeight);
				ct.lineTo(seriesShape.width - offsetWidth, seriesShape.height - offsetHeight);
				ct.lineTo(offsetWidth, seriesShape.height - offsetHeight);
				ct.lineTo(offsetWidth, offsetHeight);
			} else if (t.shape=='triangle') {
				ct.moveTo(seriesShape.width/2, offsetHeight);
				ct.lineTo(seriesShape.width - offsetWidth/1.222, seriesShape.height - offsetHeight);
				ct.lineTo(offsetWidth/1.222, seriesShape.height - offsetHeight);
			} else if (t.shape=='circle') {
				ct.arc(seriesShape.width/2, seriesShape.height/2, seriesShape.height/2 - offsetHeight > 0 ? seriesShape.height/2 - offsetHeight : 0, 0, PI2, false);
			} else if (t.shape=='vbar') {
				ct.clearRect(0, 0, seriesShape.width, seriesShape.height);
				ct.moveTo(seriesShape.width/2 - 2, offsetHeight);
				ct.lineTo(seriesShape.width/2 + 2, offsetHeight);
				ct.lineTo(seriesShape.width/2 + 2, seriesShape.height - offsetHeight);
				ct.lineTo(seriesShape.width/2 - 2, seriesShape.height - offsetHeight);
			} else if (t.shape=='hbar') {
				ct.clearRect(0, 0, seriesShape.width, seriesShape.height);
				ct.moveTo(offsetWidth, seriesShape.height/2 - 2); 
				ct.lineTo(seriesShape.width - offsetWidth, seriesShape.height/2 - 2);
				ct.lineTo(seriesShape.width - offsetWidth, seriesShape.height/2 + 2);
				ct.lineTo(offsetWidth, seriesShape.height/2 + 2);
			} else if (t.shape=='diamond'){
				ct.moveTo(seriesShape.width/2, offsetHeight);
				ct.lineTo(seriesShape.width - offsetWidth/1.222, seriesShape.height / 2);
				ct.lineTo(seriesShape.width/2, seriesShape.height - offsetHeight);
				ct.lineTo(offsetWidth/1.222, seriesShape.height / 2);
			} else if (t.shape=='pentagon'){
				ct.moveTo(seriesShape.width / 2, offsetHeight - 1);
				ct.lineTo(seriesShape.width - offsetWidth / 1.222, seriesShape.height / 2 - offsetHeight);
				ct.lineTo(seriesShape.width / 1.5 - 1, seriesShape.height - offsetHeight);
				ct.lineTo(seriesShape.width / 3 + 1, seriesShape.height - offsetHeight);
				ct.lineTo(offsetWidth / 1.222, seriesShape.height / 2 - offsetHeight);
			} else if (t.shape=='hexagon'){
				ct.moveTo(seriesShape.width / 2.5, offsetHeight);
				ct.lineTo(seriesShape.width / 1.5, offsetHeight);
				ct.lineTo(seriesShape.width - offsetWidth / 1.555, seriesShape.height / 2);
				ct.lineTo(seriesShape.width / 1.5, seriesShape.height - offsetHeight);
				ct.lineTo(seriesShape.width / 2.5, seriesShape.height - offsetHeight);
				ct.lineTo(seriesShape.width / 3.333, seriesShape.height / 2);
			} else if (t.shape=='cross'){
				ct.strokeStyle = 2;
				ct.moveTo(offsetWidth, offsetHeight);
				ct.lineTo(seriesShape.width - offsetWidth, seriesShape.height - offsetHeight);
				ct.moveTo(offsetWidth, seriesShape.height - offsetHeight);
				ct.lineTo(seriesShape.width - offsetWidth, offsetHeight);
				ct.stroke();
			} else if (t.shape=='tick'){
				ct.strokeStyle = 2;
				ct.moveTo(seriesShape.width / 2, seriesShape.height - offsetHeight);
				ct.lineTo(seriesShape.width / 3, offsetHeight);
				ct.moveTo(seriesShape.width / 2, seriesShape.height - offsetHeight);
				ct.lineTo(seriesShape.width / 2 + 10, seriesShape.height - (offsetHeight + 20));
				ct.stroke();
			}
			ct.closePath();
			if (t.border) {
				ct.strokeStyle = t.border;
				ct.lineWidth = 2;
				ct.setLineDash([]);
				ct.stroke();
			}
			ct.fill();
			ct.restore();
			
    	}
    	if(t.color)
    		applyStyle(seriesName, "_fg=" + t.color);
    	else
    		applyStyle(seriesName, "_fg=" + t.shapeColor);
	}
	// make the all the series visible at the bottom of scroll  
	seriesContainer.style.paddingBottom = toPx(this.titleDiv.clientHeight + 10);
}
AmiChartLayer_Legend.prototype.draw=function(){
	// legend
	// TODO: check whether mouse is inside legend on refresh, if so then use smart opacity, otherwise use reg opacity
	if (this.isMouseIn)
		this.enableSmartOpacity();
	else
		this.disableSmartOpacity();
	this.canvas.style.left=toPx(this.x);
	this.canvas.style.top=toPx(this.y);
	
	//resizer
	this.resizer.style.width=toPx(this.resizerDim);
	this.resizer.style.height=toPx(this.resizerDim);
	this.resizer.style.left=toPx(this.w - this.resizerDim - this.resizerPadding);
	this.resizer.style.top=toPx(this.h - this.resizerDim - this.resizerPadding);
	if ((this.r + this.g + this.b) / 3 < 120 )
		this.resizer.style.backgroundImage =  "url('rsc/resizer-light.svg')";
	else
		this.resizer.style.backgroundImage =  "url('rsc/resizer-dark.svg')";
	
	this.addTitle();
	this.addSeries();
	
	if (this.borderColor)
		this.canvas.style.border='1px solid ' + this.borderColor;
	applyStyle(this.canvas, "_fm=" + this.fontFamily);
	applyStyle(this.canvas, "_fs=" + this.labelSize);
}
AmiChartLayer_Legend.prototype.makeResizeable=function(elmnt) {
	var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
	if (this.resizer)
		this.resizer.onmousedown = dragMouseDown.bind(this);
	
	  function dragMouseDown(e) {
			this.isResizing = true;
			this.isEventActive = true;
		    e = e || window.event;
		    e.preventDefault();
		    // get the mouse cursor position at startup
		    pos3 = e.clientX;
		    pos4 = e.clientY;
		    document.onmousemove = onResizerDrag.bind(this);
		    document.onmouseup = onResizerDragEnd.bind(this);
	  }
	  
	  function onResizerDrag(e) {
		if (this.isResizing) {
		    e = e || window.event;
		    e.preventDefault();
		    // calculate the new cursor position
		    pos1 = pos3 - e.clientX;
		    pos2 = pos4 - e.clientY;
		    pos3 = e.clientX;
		    pos4 = e.clientY;
		    // set the element's new position
		    var rect = this.canvas.getBoundingClientRect();
		    this.setSize(rect.width - pos1, rect.height - pos2);
		    elmnt.style.top = toPx(pos1 + rect.height - this.resizerDim - this.resizerPadding);
		    elmnt.style.left = toPx(pos2 + rect.width - this.resizerDim - this.resizerPadding);
		    this.ensureVisible(this.canvas.offsetLeft - pos1, this.canvas.offsetTop - pos2);
		}
	  }

	  function onResizerDragEnd() {
		  var m = {};
		  m.w = this.canvas.clientWidth;
		  m.h = this.canvas.clientHeight;
		  m.layerPos = this.layerPos;
		  this.plot.callBack('onResizeLegend', m);
		  //TODO: figure out the behavior when resized super fast and legend ends up being clipped.
//		  if (!this.isLegendOversized() && this.isAnySideClipped())
//			  this.ensureVisible(this.canvas.offsetLeft - pos1, this.canvas.offsetTop - pos2);
		  this.isResizing = false;
		  this.isEventActive = false;
		  document.onmouseup = null;
		  document.onmousemove = null;
		  if (this.isMouseIn)
			  this.enableSmartOpacity();
		  else { 
			  this.disableSmartOpacity();
			  this.hideResizer();
		  }
	  } 
}

AmiChartLayer_Legend.prototype.makeDraggable=function(elmnt) {
	var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
	if (this.canvas)
		this.canvas.onmousedown = dragMouseDown.bind(this);
	
  	function dragMouseDown(e) {
		if (!this.isResizing) {
			this.isEventActive = true;
		    e = e || window.event;
		    e.preventDefault();
		    // get the mouse cursor position at startup
		    pos3 = e.clientX;
		    pos4 = e.clientY;
		    document.onmousemove = onLegendDrag.bind(this);
		    document.onmouseup = onLegendDragEnd.bind(this);
		}
  	}

  	function onLegendDrag(e) {
	    e = e || window.event;
	    e.preventDefault();
	    // calculate the new cursor position
		pos1 = pos3 - e.clientX;
		pos2 = pos4 - e.clientY;
		pos3 = e.clientX;
		pos4 = e.clientY;
		// set the element's new position
		elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
		elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
  		if (!this.isLegendOversized())
  			this.ensureVisible(elmnt.offsetLeft - pos1, elmnt.offsetTop - pos2);
	  }

	function onLegendDragEnd() {
		var m = {};
		m.x = this.canvas.offsetLeft;
		m.y = this.canvas.offsetTop;
		m.layerPos = this.layerPos;
		this.plot.callBack('onDragLegend', m);
		this.isEventActive = false;
	    document.onmouseup = null;
	    document.onmousemove = null;
	    if (this.isMouseIn)
	    	this.enableSmartOpacity();
	    else {
	    	this.disableSmartOpacity();
	    	this.hideResizer();
	    }
		if (!this.isLegendOversized() && this.isAnySideClipped())
  			this.ensureVisible(elmnt.offsetLeft - pos1, elmnt.offsetTop - pos2);
	  } 
}
AmiChartLayer_Legend.prototype.handleCheckMark=function(groupingId, seriesId, checkbox, checkboxWrapper, checkMark) {
	if (checkbox.checked)
		checkMark.style.display = "block";
	else
		checkMark.style.display = "none";
	checkbox.click();
}
AmiChartLayer_Legend.prototype.handleCheckbox=function(groupingId, seriesId, checkbox) {
	var m = {};
	m.layerPos = this.layerPos;
	m.grouping = groupingId;
	m.series = seriesId;
	m.checked = checkbox.checked;
	this.plot.callBack('onCheckboxLegend', m);
}