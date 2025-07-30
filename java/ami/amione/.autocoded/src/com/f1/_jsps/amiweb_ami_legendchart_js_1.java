package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ami_legendchart_js_1 extends AbstractHttpHandler{

	public amiweb_ami_legendchart_js_1() {
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
            "function AmiChartLayer_Legend(){\r\n"+
            "	var that=this;\r\n"+
            "	this.isContainer=false;\r\n"+
            "	this.fontStyle=\"\";\r\n"+
            "	this.nameColor=\"#000000\";\r\n"+
            "	this.fontSize=20;\r\n"+
            "	this.fontFamily=\"arial\";\r\n"+
            "	this.canvas=nw('div', 'legend');\r\n"+
            "	this.canvas.style.position='absolute';\r\n"+
            "  	this.resizerDim;\r\n"+
            "  	this.resizerPadding;\r\n"+
            "  	this.resizer=nw('div', 'legend_resizer');\r\n"+
            "  	this.isResizing=false;\r\n"+
            "  	this.isEventActive = false;\r\n"+
            "  	this.isResizerVisible = false;\r\n"+
            "  	this.isMouseIn=false;\r\n"+
            "  	this.canvas.appendChild(this.resizer);\r\n"+
            "  	this.titleDiv=nw('div', 'legend_title');\r\n"+
            "  	this.canvas.appendChild(this.titleDiv);\r\n"+
            "  	this.seriesDiv=nw('div', 'legend_series');\r\n"+
            "  	this.canvas.appendChild(this.seriesDiv);\r\n"+
            "  	this.makeDraggable(this.canvas);\r\n"+
            "  	this.makeResizeable(this.resizer);\r\n"+
            "  	this.canvas.onmouseenter = function() {\r\n"+
            "  		that.isMouseIn = true;\r\n"+
            "  		that.showResizer();\r\n"+
            "  		that.enableSmartOpacity();\r\n"+
            "  	}\r\n"+
            "  	this.canvas.onmouseleave = function() {\r\n"+
            "  		that.isMouseIn = false;\r\n"+
            "  		if (!that.isEventActive) {\r\n"+
            "  			that.hideResizer();\r\n"+
            "  			that.disableSmartOpacity();\r\n"+
            "  		}\r\n"+
            "  	}\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.plot;\r\n"+
            "\r\n"+
            "AmiChartLayer_Legend.prototype.init=function(options){\r\n"+
            "  this.keyPosition=options.keyPosition;\r\n"+
            "  this.fontFamily=options.fontFamily;\r\n"+
            "  this.opacity=options.opacity/100;\r\n"+
            "  this.x=options.x;\r\n"+
            "  this.y=options.y;\r\n"+
            "  this.w=options.w;\r\n"+
            "  this.h=options.h;\r\n"+
            "  this.plotWidth=options.pltWd;\r\n"+
            "  this.plotHeight=options.pltHt;\r\n"+
            "  this.fontFamily=options.fontFamily;\r\n"+
            "  this.namePosition=options.namePosition;\r\n"+
            "  this.seriesNames=options.seriesNames;\r\n"+
            "  this.series=options.series;\r\n"+
            "  this.name=options.name;\r\n"+
            "  this.nameColor=options.nameColor;\r\n"+
            "  this.nameSize=options.nameSize;\r\n"+
            "  this.labelSize=options.labelSize;\r\n"+
            "  this.borderColor=options.borderColor;\r\n"+
            "  this.backgroundColor=options.backgroundColor;\r\n"+
            "  this.checkboxColor=options.checkboxColor;\r\n"+
            "  this.checkboxCheckColor=options.checkboxCheckColor;\r\n"+
            "  this.checkboxBorderColor=options.checkboxBorderColor;\r\n"+
            "  this.r=options.r;\r\n"+
            "  this.g=options.g;\r\n"+
            "  this.b=options.b;\r\n"+
            "  this.hPadding=options.hPadding;\r\n"+
            "  this.vPadding=options.vPadding;\r\n"+
            "  this.setSize(this.w, this.h);\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.showResizer=function(){\r\n"+
            "	this.resizer.style.display = \"block\";\r\n"+
            "	this.isResizerVisible = true;\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.hideResizer=function(){\r\n"+
            "	this.resizer.style.display = \"none\";\r\n"+
            "	this.isResizerVisible = false;\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.enableSmartOpacity = function() {\r\n"+
            "  	this.setOpacity(1);\r\n"+
            "  	this.setSmartOpacity(this.opacity);\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.disableSmartOpacity = function() {\r\n"+
            "  	this.setSmartOpacity(1);\r\n"+
            "  	this.setOpacity(this.opacity);\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.setSmartOpacity=function(opacity){\r\n"+
            "	var bg =\"rgba(\" + this.r + \",\" + this.g + \",\" + this.b + \",\" + opacity + \")\";\r\n"+
            "	this.canvas.style.backgroundColor=bg;\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.setOpacity=function(opacity){\r\n"+
            "	this.canvas.style.opacity=opacity;\r\n"+
            "}\r\n"+
            "// ensures legend stays within the chart/plot\r\n"+
            "AmiChartLayer_Legend.prototype.ensureVisible=function(x, y) {\r\n"+
            "	if (this.isResizing) {\r\n"+
            "		if (this.canvas.offsetLeft + this.w > this.plotWidth - this.hPadding)\r\n"+
            "			this.setWidth(this.plotWidth - this.canvas.offsetLeft - this.hPadding);\r\n"+
            "		if (this.canvas.offsetTop + this.h > this.plotHeight - this.vPadding) \r\n"+
            "			this.setHeight(this.plotHeight - this.canvas.offsetTop - this.vPadding);\r\n"+
            "	} else { // dragging\r\n"+
            "		if (x < this.hPadding) \r\n"+
            "			this.canvas.style.left = toPx(this.hPadding);\r\n"+
            "		if (y < this.vPadding)\r\n"+
            "			this.canvas.style.top = toPx(this.vPadding);\r\n"+
            "		if (y + this.h > this.plotHeight - this.vPadding) \r\n"+
            "			this.canvas.style.top = toPx(this.plotHeight - this.vPadding - this.h);\r\n"+
            "		if (x + this.w > this.plotWidth - this.hPadding)\r\n"+
            "			this.canvas.style.left = toPx(this.plotWidth - this.hPadding - this.w);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.setSize=function(width,height){\r\n"+
            "	this.canvas.style.width=toPx(width);\r\n"+
            "	this.canvas.style.height=toPx(height);\r\n"+
            "	this.w = width;\r\n"+
            "	this.h = height;\r\n"+
            "	this.resizerDim=12;\r\n"+
            "	this.resizerPadding=8;\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.setWidth=function(width) {\r\n"+
            "	this.canvas.style.width=toPx(width);\r\n"+
            "	this.w=width;\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.setHeight=function(height) {\r\n"+
            "	this.canvas.style.height=toPx(height);\r\n"+
            "	this.h=height;\r\n"+
            "}\r\n"+
            "/* returns true if legend dimension (either width or height) ends up being greater than the plot dimension*/\r\n"+
            "AmiChartLayer_Legend.prototype.isLegendOversized=function() {\r\n"+
            "	return this.w + 2 * this.hPadding > this.plotWidth || this.h + 2 * this.vPadding > this.plotHeight;\r\n"+
            "}\r\n"+
            "/* returns true if any of the sides gets clipped. (useful when legend is dragged super fast to an edge)*/\r\n"+
            "AmiChartLayer_Legend.prototype.isAnySideClipped=function() {\r\n"+
            "	return this.isTopClipped() || this.isRightClipped() || this.isBottomClipped() || this.isLeftClipped(); \r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.isTopClipped=function() {\r\n"+
            "	return this.canvas.offsetTop - this.vPadding < 0;\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.isRightClipped=function() {\r\n"+
            "	return this.canvas.offsetLeft + this.w + this.hPadding > this.plotWidth; \r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.isBottomClipped=function() {\r\n"+
            "	return this.canvas.offsetTop + this.h + this.vPadding > this.plotHeight;\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.isLeftClipped=function() {\r\n"+
            "	return this.canvas.offsetLeft - this.hPadding < 0;\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.addTitle=function() {\r\n"+
            "	this.titleDiv.innerHTML=\"\";\r\n"+
            "	if (this.namePosition != -1 && this.name  && this.nameColor) {\r\n"+
            "		this.titleDiv.appendChild(document.createTextNode(this.name));\r\n"+
            "		applyStyle(this.titleDiv, \"_fg=\"+this.nameColor);\r\n"+
            "		applyStyle(this.titleDiv, \"_fm=\"+this.fontStyle);\r\n"+
            "		applyStyle(this.titleDiv, \"_fs=\"+this.nameSize);\r\n"+
            "		if (this.namePosition == 5)\r\n"+
            "			this.titleDiv.style.textAlign = 'left';\r\n"+
            "		 else if (this.namePosition == 9) \r\n"+
            "			this.titleDiv.style.textAlign = 'right';\r\n"+
            "		 else \r\n"+
            "			this.titleDiv.style.textAlign = 'center';\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.addSeries=function() {\r\n"+
            "	this.seriesDiv.innerHTML=\"\";\r\n"+
            "	var seriesContainer=nw(\"ul\");\r\n"+
            "	this.seriesDiv.appendChild(seriesContainer);\r\n"+
            "	\r\n"+
            "	for (var i in this.series) {\r\n"+
            "		var t = this.series[i];\r\n"+
            "		// creating dom elements\r\n"+
            "		var series = nw('li', 'series');\r\n"+
            "		var seriesShape = nw('canvas', 'series_shape');\r\n"+
            "		var seriesName = nw('span', 'series_name');\r\n"+
            "		var seriesCheckboxWrapper = nw('div', 'series_checkbox_wrapper');\r\n"+
            "		var checkMark = nw('div', 'checkMark');\r\n"+
            "		var seriesCheckbox = nw('input', 'series_checkbox');\r\n"+
            "		seriesCheckbox.type = 'checkbox';\r\n"+
            "		seriesCheckbox.value = '';\r\n"+
            "		seriesName.innerHTML = this.series[i].name;\r\n"+
            "		seriesCheckboxWrapper.appendChild(seriesCheckbox);\r\n"+
            "		seriesCheckboxWrapper.appendChild(checkMark);\r\n"+
            "		series.appendChild(seriesCheckboxWrapper);\r\n"+
            "		//series.appendChild(seriesCheckbox);\r\n"+
            "		if (t.lineSize > 0 || t.dash || t.shape != null)\r\n"+
            "			series.appendChild(seriesShape);\r\n"+
            "		series.appendChild(seriesName);\r\n"+
            "		seriesContainer.appendChild(series);\r\n"+
            "		if (t.checked) {\r\n"+
            "			seriesCheckbox.checked = true;\r\n"+
            "			checkMark.style.display = \"block\";\r\n"+
            "		} else\r\n"+
            "			checkMark.style.display = \"none\";\r\n"+
            "			\r\n"+
            "		seriesCheckbox.onclick = this.handleCheckbox.bind(this, t.grouping, t.series, seriesCheckbox);\r\n"+
            "		checkMark.onclick = this.handleCheckMark.bind(this, t.grouping, t.series, seriesCheckbox, seriesCheckboxWrapper, checkMark);\r\n"+
            "		// checkbox styling\r\n"+
            "		seriesCheckboxWrapper.style.backgroundColor = this.checkboxColor;\r\n"+
            "		seriesCheckboxWrapper.style.border = \"1px solid \" + this.checkboxBorderColor;\r\n"+
            "		checkMark.style.borderBottom = \"2px solid \" + this.checkboxCheckColor;\r\n"+
            "		checkMark.style.borderRight = \"2px solid \" + this.checkboxCheckColor;\r\n"+
            "		\r\n"+
            "		\r\n"+
            "		// adjust dimension on the canvas containing shape based on labelSize\r\n"+
            "		if (this.labelSize == 0) {\r\n"+
            "			seriesShape.width = 0;\r\n"+
            "			seriesShape.height = 0;\r\n"+
            "		} else if (this.labelSize < 30) { \r\n"+
            "			seriesShape.width = 25;\r\n"+
            "			seriesShape.height = 12;\r\n"+
            "		} else {\r\n"+
            "			seriesShape.width = this.labelSize;\r\n"+
            "			seriesShape.height = seriesName.getBoundingClientRect().height / 2;\r\n"+
            "		}\r\n"+
            "		var ct = seriesShape.getContext('2d');\r\n"+
            "		ct.strokeStyle=t.color ? t.color : (t.shapeColor ? t.shapeColor : \"#000000\");\r\n"+
            "		ct.fillStyle=t.shapeColor ? t.shapeColor : (t.color ? t.color : \"#000000\");\r\n"+
            "		// horizontal line\r\n"+
            "		if (t.lineSize > 0 || t.dash) {\r\n"+
            "			ct.beginPath();\r\n"+
            "			ct.lineWidth = 1;\r\n"+
            "	    	if (t.dash)\r\n"+
            "	    		ct.setLineDash([ct.lineWidth,3]);\r\n"+
            "	    	ct.moveTo(0, seriesShape.height/2 + .5); // adding .5 to make the line crisp\r\n"+
            "	    	ct.lineTo(seriesShape.width, seriesShape.height/2 + .5);\r\n"+
            "	    	ct.closePath();\r\n"+
            "	    	ct.stroke();\r\n"+
            "		}\r\n"+
            "	    // drawing shape begins\r\n"+
            "    	if (t.shape != null) {\r\n"+
            "	    	ct.beginPath();\r\n"+
            "			var offsetWidth = 9; // left and right padding of the shape (excluding the horizontal bar)\r\n"+
            "			var offsetHeight = 2; // top and bottom padding ^^^\r\n"+
            "			if (t.border)\r\n"+
            "				ct.strokeStyle = t.border;\r\n"+
            "			if (t.shape=='square') {				\r\n"+
            "				ct.moveTo(seriesShape.width/2, offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth, offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(offsetWidth, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(offsetWidth, offsetHeight);\r\n"+
            "			} else if (t.shape=='triangle') {\r\n"+
            "				ct.moveTo(seriesShape.width/2, offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth/1.222, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(offsetWidth/1.222, seriesShape.height - offsetHeight);\r\n"+
            "			} else if (t.shape=='circle') {\r\n"+
            "				ct.arc(seriesShape.width/2, seriesShape.height/2, seriesShape.height/2 - offsetHeight > 0 ? seriesShape.height/2 - offsetHeight : 0, 0, PI2, false);\r\n"+
            "			} else if (t.shape=='vbar') {\r\n"+
            "				ct.clearRect(0, 0, seriesShape.width, seriesShape.height);\r\n"+
            "				ct.moveTo(seriesShape.width/2 - 2, offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width/2 + 2, offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width/2 + 2, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(seriesSh");
          out.print(
            "ape.width/2 - 2, seriesShape.height - offsetHeight);\r\n"+
            "			} else if (t.shape=='hbar') {\r\n"+
            "				ct.clearRect(0, 0, seriesShape.width, seriesShape.height);\r\n"+
            "				ct.moveTo(offsetWidth, seriesShape.height/2 - 2); \r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth, seriesShape.height/2 - 2);\r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth, seriesShape.height/2 + 2);\r\n"+
            "				ct.lineTo(offsetWidth, seriesShape.height/2 + 2);\r\n"+
            "			} else if (t.shape=='diamond'){\r\n"+
            "				ct.moveTo(seriesShape.width/2, offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth/1.222, seriesShape.height / 2);\r\n"+
            "				ct.lineTo(seriesShape.width/2, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(offsetWidth/1.222, seriesShape.height / 2);\r\n"+
            "			} else if (t.shape=='pentagon'){\r\n"+
            "				ct.moveTo(seriesShape.width / 2, offsetHeight - 1);\r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth / 1.222, seriesShape.height / 2 - offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width / 1.5 - 1, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width / 3 + 1, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(offsetWidth / 1.222, seriesShape.height / 2 - offsetHeight);\r\n"+
            "			} else if (t.shape=='hexagon'){\r\n"+
            "				ct.moveTo(seriesShape.width / 2.5, offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width / 1.5, offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth / 1.555, seriesShape.height / 2);\r\n"+
            "				ct.lineTo(seriesShape.width / 1.5, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width / 2.5, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width / 3.333, seriesShape.height / 2);\r\n"+
            "			} else if (t.shape=='cross'){\r\n"+
            "				ct.strokeStyle = 2;\r\n"+
            "				ct.moveTo(offsetWidth, offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth, seriesShape.height - offsetHeight);\r\n"+
            "				ct.moveTo(offsetWidth, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width - offsetWidth, offsetHeight);\r\n"+
            "				ct.stroke();\r\n"+
            "			} else if (t.shape=='tick'){\r\n"+
            "				ct.strokeStyle = 2;\r\n"+
            "				ct.moveTo(seriesShape.width / 2, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width / 3, offsetHeight);\r\n"+
            "				ct.moveTo(seriesShape.width / 2, seriesShape.height - offsetHeight);\r\n"+
            "				ct.lineTo(seriesShape.width / 2 + 10, seriesShape.height - (offsetHeight + 20));\r\n"+
            "				ct.stroke();\r\n"+
            "			}\r\n"+
            "			ct.closePath();\r\n"+
            "			if (t.border) {\r\n"+
            "				ct.strokeStyle = t.border;\r\n"+
            "				ct.lineWidth = 2;\r\n"+
            "				ct.setLineDash([]);\r\n"+
            "				ct.stroke();\r\n"+
            "			}\r\n"+
            "			ct.fill();\r\n"+
            "			ct.restore();\r\n"+
            "			\r\n"+
            "    	}\r\n"+
            "    	if(t.color)\r\n"+
            "    		applyStyle(seriesName, \"_fg=\" + t.color);\r\n"+
            "    	else\r\n"+
            "    		applyStyle(seriesName, \"_fg=\" + t.shapeColor);\r\n"+
            "	}\r\n"+
            "	// make the all the series visible at the bottom of scroll  \r\n"+
            "	seriesContainer.style.paddingBottom = toPx(this.titleDiv.clientHeight + 10);\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.draw=function(){\r\n"+
            "	// legend\r\n"+
            "	// TODO: check whether mouse is inside legend on refresh, if so then use smart opacity, otherwise use reg opacity\r\n"+
            "	if (this.isMouseIn)\r\n"+
            "		this.enableSmartOpacity();\r\n"+
            "	else\r\n"+
            "		this.disableSmartOpacity();\r\n"+
            "	this.canvas.style.left=toPx(this.x);\r\n"+
            "	this.canvas.style.top=toPx(this.y);\r\n"+
            "	\r\n"+
            "	//resizer\r\n"+
            "	this.resizer.style.width=toPx(this.resizerDim);\r\n"+
            "	this.resizer.style.height=toPx(this.resizerDim);\r\n"+
            "	this.resizer.style.left=toPx(this.w - this.resizerDim - this.resizerPadding);\r\n"+
            "	this.resizer.style.top=toPx(this.h - this.resizerDim - this.resizerPadding);\r\n"+
            "	if ((this.r + this.g + this.b) / 3 < 120 )\r\n"+
            "		this.resizer.style.backgroundImage =  \"url('rsc/resizer-light.svg')\";\r\n"+
            "	else\r\n"+
            "		this.resizer.style.backgroundImage =  \"url('rsc/resizer-dark.svg')\";\r\n"+
            "	\r\n"+
            "	this.addTitle();\r\n"+
            "	this.addSeries();\r\n"+
            "	\r\n"+
            "	if (this.borderColor)\r\n"+
            "		this.canvas.style.border='1px solid ' + this.borderColor;\r\n"+
            "	applyStyle(this.canvas, \"_fm=\" + this.fontFamily);\r\n"+
            "	applyStyle(this.canvas, \"_fs=\" + this.labelSize);\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.makeResizeable=function(elmnt) {\r\n"+
            "	var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;\r\n"+
            "	if (this.resizer)\r\n"+
            "		this.resizer.onmousedown = dragMouseDown.bind(this);\r\n"+
            "	\r\n"+
            "	  function dragMouseDown(e) {\r\n"+
            "			this.isResizing = true;\r\n"+
            "			this.isEventActive = true;\r\n"+
            "		    e = e || window.event;\r\n"+
            "		    e.preventDefault();\r\n"+
            "		    // get the mouse cursor position at startup\r\n"+
            "		    pos3 = e.clientX;\r\n"+
            "		    pos4 = e.clientY;\r\n"+
            "		    document.onmousemove = onResizerDrag.bind(this);\r\n"+
            "		    document.onmouseup = onResizerDragEnd.bind(this);\r\n"+
            "	  }\r\n"+
            "	  \r\n"+
            "	  function onResizerDrag(e) {\r\n"+
            "		if (this.isResizing) {\r\n"+
            "		    e = e || window.event;\r\n"+
            "		    e.preventDefault();\r\n"+
            "		    // calculate the new cursor position\r\n"+
            "		    pos1 = pos3 - e.clientX;\r\n"+
            "		    pos2 = pos4 - e.clientY;\r\n"+
            "		    pos3 = e.clientX;\r\n"+
            "		    pos4 = e.clientY;\r\n"+
            "		    // set the element's new position\r\n"+
            "		    var rect = this.canvas.getBoundingClientRect();\r\n"+
            "		    this.setSize(rect.width - pos1, rect.height - pos2);\r\n"+
            "		    elmnt.style.top = toPx(pos1 + rect.height - this.resizerDim - this.resizerPadding);\r\n"+
            "		    elmnt.style.left = toPx(pos2 + rect.width - this.resizerDim - this.resizerPadding);\r\n"+
            "		    this.ensureVisible(this.canvas.offsetLeft - pos1, this.canvas.offsetTop - pos2);\r\n"+
            "		}\r\n"+
            "	  }\r\n"+
            "\r\n"+
            "	  function onResizerDragEnd() {\r\n"+
            "		  var m = {};\r\n"+
            "		  m.w = this.canvas.clientWidth;\r\n"+
            "		  m.h = this.canvas.clientHeight;\r\n"+
            "		  m.layerPos = this.layerPos;\r\n"+
            "		  this.plot.callBack('onResizeLegend', m);\r\n"+
            "		  //TODO: figure out the behavior when resized super fast and legend ends up being clipped.\r\n"+
            "//		  if (!this.isLegendOversized() && this.isAnySideClipped())\r\n"+
            "//			  this.ensureVisible(this.canvas.offsetLeft - pos1, this.canvas.offsetTop - pos2);\r\n"+
            "		  this.isResizing = false;\r\n"+
            "		  this.isEventActive = false;\r\n"+
            "		  document.onmouseup = null;\r\n"+
            "		  document.onmousemove = null;\r\n"+
            "		  if (this.isMouseIn)\r\n"+
            "			  this.enableSmartOpacity();\r\n"+
            "		  else { \r\n"+
            "			  this.disableSmartOpacity();\r\n"+
            "			  this.hideResizer();\r\n"+
            "		  }\r\n"+
            "	  } \r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiChartLayer_Legend.prototype.makeDraggable=function(elmnt) {\r\n"+
            "	var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;\r\n"+
            "	if (this.canvas)\r\n"+
            "		this.canvas.onmousedown = dragMouseDown.bind(this);\r\n"+
            "	\r\n"+
            "  	function dragMouseDown(e) {\r\n"+
            "		if (!this.isResizing) {\r\n"+
            "			this.isEventActive = true;\r\n"+
            "		    e = e || window.event;\r\n"+
            "		    e.preventDefault();\r\n"+
            "		    // get the mouse cursor position at startup\r\n"+
            "		    pos3 = e.clientX;\r\n"+
            "		    pos4 = e.clientY;\r\n"+
            "		    document.onmousemove = onLegendDrag.bind(this);\r\n"+
            "		    document.onmouseup = onLegendDragEnd.bind(this);\r\n"+
            "		}\r\n"+
            "  	}\r\n"+
            "\r\n"+
            "  	function onLegendDrag(e) {\r\n"+
            "	    e = e || window.event;\r\n"+
            "	    e.preventDefault();\r\n"+
            "	    // calculate the new cursor position\r\n"+
            "		pos1 = pos3 - e.clientX;\r\n"+
            "		pos2 = pos4 - e.clientY;\r\n"+
            "		pos3 = e.clientX;\r\n"+
            "		pos4 = e.clientY;\r\n"+
            "		// set the element's new position\r\n"+
            "		elmnt.style.top = (elmnt.offsetTop - pos2) + \"px\";\r\n"+
            "		elmnt.style.left = (elmnt.offsetLeft - pos1) + \"px\";\r\n"+
            "  		if (!this.isLegendOversized())\r\n"+
            "  			this.ensureVisible(elmnt.offsetLeft - pos1, elmnt.offsetTop - pos2);\r\n"+
            "	  }\r\n"+
            "\r\n"+
            "	function onLegendDragEnd() {\r\n"+
            "		var m = {};\r\n"+
            "		m.x = this.canvas.offsetLeft;\r\n"+
            "		m.y = this.canvas.offsetTop;\r\n"+
            "		m.layerPos = this.layerPos;\r\n"+
            "		this.plot.callBack('onDragLegend', m);\r\n"+
            "		this.isEventActive = false;\r\n"+
            "	    document.onmouseup = null;\r\n"+
            "	    document.onmousemove = null;\r\n"+
            "	    if (this.isMouseIn)\r\n"+
            "	    	this.enableSmartOpacity();\r\n"+
            "	    else {\r\n"+
            "	    	this.disableSmartOpacity();\r\n"+
            "	    	this.hideResizer();\r\n"+
            "	    }\r\n"+
            "		if (!this.isLegendOversized() && this.isAnySideClipped())\r\n"+
            "  			this.ensureVisible(elmnt.offsetLeft - pos1, elmnt.offsetTop - pos2);\r\n"+
            "	  } \r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.handleCheckMark=function(groupingId, seriesId, checkbox, checkboxWrapper, checkMark) {\r\n"+
            "	if (checkbox.checked)\r\n"+
            "		checkMark.style.display = \"block\";\r\n"+
            "	else\r\n"+
            "		checkMark.style.display = \"none\";\r\n"+
            "	checkbox.click();\r\n"+
            "}\r\n"+
            "AmiChartLayer_Legend.prototype.handleCheckbox=function(groupingId, seriesId, checkbox) {\r\n"+
            "	var m = {};\r\n"+
            "	m.layerPos = this.layerPos;\r\n"+
            "	m.grouping = groupingId;\r\n"+
            "	m.series = seriesId;\r\n"+
            "	m.checked = checkbox.checked;\r\n"+
            "	this.plot.callBack('onCheckboxLegend', m);\r\n"+
            "}");

	}
	
}