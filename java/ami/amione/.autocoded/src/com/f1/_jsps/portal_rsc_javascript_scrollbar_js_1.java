package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_scrollbar_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_scrollbar_js_1() {
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
            "\r\n"+
            "var SCROLLBAR_GRIP_V_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> <g> <line style=\"fill:none;stroke:';\r\n"+
            "var SCROLLBAR_GRIP_V_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;\" x1=\"12\" y1=\"76.3\" x2=\"88\" y2=\"76.3\"/> </g> </g> <g id=\"horizontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> '+SVG_SUFFIX;\r\n"+
            "var SCROLLBAR_GRIP_H_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"horizontal_x5F_scrollbar\"> <g> <line style=\"fill:none;stroke:';\r\n"+
            "var SCROLLBAR_GRIP_H_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;\" x1=\"23.9\" y1=\"13.5\" x2=\"23.9\" y2=\"89.5\"/> </g> </g> <g id=\"save\"> </g> '+SVG_SUFFIX;\r\n"+
            "var SCROLLBAR_UP_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> <polyline style=\"fill:none;stroke:';\r\n"+
            "var SCROLLBAR_UP_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;\" points=\"11.9,69 50,31 88.1,69 	\"/> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"horizontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> '+SVG_SUFFIX;\r\n"+
            "var SCROLLBAR_DOWN_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> <polyline style=\"fill:none;stroke:';\r\n"+
            "var SCROLLBAR_DOWN_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;\" points=\"88.1,31 50,69 11.9,31 	\"/> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"horizontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> '+SVG_SUFFIX;\r\n"+
            "var SCROLLBAR_LEFT_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> <polyline style=\"fill:none;stroke:';\r\n"+
            "var SCROLLBAR_LEFT_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;\" points=\"69,88.1 31,50 69,11.9 	\"/> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"horizontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> '+SVG_SUFFIX;\r\n"+
            "var SCROLLBAR_RIGHT_IMAGE_PREFIX=SVG_PREFIX + ' x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> <polyline style=\"fill:none;stroke:';\r\n"+
            "var SCROLLBAR_RIGHT_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;\" points=\"32.3,11.9 70.3,50 32.3,88.1 	\"/> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"horizontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> '+SVG_SUFFIX;\r\n"+
            "\r\n"+
            "function ScrollBarDOM(element, scrollBar){\r\n"+
            "	this.scrollBar = scrollBar;\r\n"+
            "	var that = scrollBar;\r\n"+
            "	this.scrollElement=element;\r\n"+
            "	this.scrollElement.className=\"scrollbar_container\";\r\n"+
            "	\r\n"+
            "	this.trackElement=nw(\"div\");\r\n"+
            "	this.trackElement.className=that.isHorizontal ? \"scrollbar_track_h\" : \"scrollbar_track_v\";\r\n"+
            "	this.gripElement=nw('div',that.isHorizontal ? 'scrollbar_grip_h' : 'scrollbar_grip_v');\r\n"+
            "	this.handleElement=nw(\"div\");\r\n"+
            "	this.handleElement.className=that.isHorizontal ? \"scrollbar_handle_h\" : \"scrollbar_handle_v\";\r\n"+
            "	this.backElement=nw(\"div\");\r\n"+
            "	this.backElement.className=that.isHorizontal ? \"scrollbar_left\" : \"scrollbar_up\";\r\n"+
            "	this.forwardElement=nw(\"div\");\r\n"+
            "	this.forwardElement.className=that.isHorizontal ? \"scrollbar_right\" : \"scrollbar_down\";\r\n"+
            "	this.tinySquare=nw(\"div\", \"tiny_square\");\r\n"+
            "	this.tinySquare.style.display=\"block\";\r\n"+
            "	\r\n"+
            "	this.handleElement.appendChild(this.gripElement);\r\n"+
            "	this.scrollElement.appendChild(this.trackElement);\r\n"+
            "	this.scrollElement.appendChild(this.handleElement);\r\n"+
            "	this.scrollElement.appendChild(this.forwardElement);\r\n"+
            "	this.scrollElement.appendChild(this.tinySquare);\r\n"+
            "	this.scrollElement.appendChild(this.backElement);\r\n"+
            "	\r\n"+
            "\r\n"+
            "	makeDraggable(this.gripElement,this.handleElement,!that.isHorizontal,that.isHorizontal);\r\n"+
            "	this.gripElement.ondragging=function(e,x,y){that.onDragging(e,x,y);};\r\n"+
            "	this.gripElement.ondraggingEnd=function(e,x,y){that.onDraggingEnd(e,x,y);};\r\n"+
            "\r\n"+
            "	makeMouseRepeat(this.trackElement);\r\n"+
            "	this.trackElement.onMouseRepeat = function(e){that.onTrackClicked(e);};\r\n"+
            "\r\n"+
            "	makeMouseRepeat(this.backElement);\r\n"+
            "	this.backElement.onMouseRepeat=function(){that.goPage(-.25)};\r\n"+
            "\r\n"+
            "	makeMouseRepeat(this.forwardElement);\r\n"+
            "	this.forwardElement.onMouseRepeat=function(){that.goPage(.25)};\r\n"+
            "}\r\n"+
            "ScrollBarDOM.prototype.applyTinySquareColor=function(cornerColor){\r\n"+
            "	this.tinySquare.style.backgroundColor=cornerColor;\r\n"+
            "}\r\n"+
            "ScrollBarDOM.prototype.applyColors=function(gripColor, trackColor, trackButtonColor, borderColor, cornerColor){\r\n"+
            "	this.gripElement.style.backgroundColor=gripColor;\r\n"+
            "	this.trackElement.style.backgroundColor=trackColor;\r\n"+
            "	\r\n"+
            "	this.forwardElement.style.backgroundColor=trackButtonColor;\r\n"+
            "	this.tinySquare.style.backgroundColor=cornerColor;\r\n"+
            "	this.backElement.style.backgroundColor=trackButtonColor;\r\n"+
            "	this.trackElement.style.borderColor=borderColor;\r\n"+
            "	this.handleElement.style.borderColor=borderColor;\r\n"+
            "	this.forwardElement.style.borderColor=borderColor;\r\n"+
            "	this.backElement.style.borderColor=borderColor;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBarDOM.prototype.applyBorderRadius=function(borderRadius){\r\n"+
            "	var length;\r\n"+
            "	var sb = this.scrollBar;\r\n"+
            "	if (sb.isHorizontal) {\r\n"+
            "		length = this.handleElement.style.width;\r\n"+
            "		if (!length)\r\n"+
            "			length = sb.width;\r\n"+
            "	} else {\r\n"+
            "		length = this.handleElement.style.height;\r\n"+
            "		if (!length)\r\n"+
            "			length = sb.height;\r\n"+
            "	}\r\n"+
            "	if (typeof length == \"string\")\r\n"+
            "		length = Number(fromPx(length));\r\n"+
            "	var derived = length* (borderRadius/100.0);\r\n"+
            "	this.handleElement.style.borderRadius=derived + \"px\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBarDOM.prototype.hideArrows=function(hide){\r\n"+
            "	if (hide==\"true\" || hide==true) {\r\n"+
            "		this.forwardElement.style.display=\"none\";\r\n"+
            "		this.backElement.style.display=\"none\";\r\n"+
            "		this.scrollBar.setArrowHidden(true);\r\n"+
            "	} else  {\r\n"+
            "		this.forwardElement.style.display=\"block\";\r\n"+
            "		this.backElement.style.display=\"block\";\r\n"+
            "		this.scrollBar.setArrowHidden(false);\r\n"+
            "	}\r\n"+
            "	this.scrollBar.updateTrack();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBarDOM.prototype.repaintPositions=function(){\r\n"+
            "	//Positions the scrollbar and it's container elements\r\n"+
            "	var that = this.scrollBar;\r\n"+
            "    new Rect(that.x,that.y,that.width,that.height).writeToElement(this.scrollElement);\r\n"+
            "    var trackLength = that.trackLength;\r\n"+
            "    var trackOffset = that.trackOffset;\r\n"+
            "	new Rect(0,0,trackOffset,trackOffset).writeToElement(this.backElement);\r\n"+
            "    if(that.isHorizontal){\r\n"+
            "    	new Rect(trackOffset+trackLength,0,trackOffset,trackOffset).writeToElement(this.forwardElement);\r\n"+
            "    	var tinySquareLeft = trackLength+trackOffset*2;\r\n"+
            "		new Rect(tinySquareLeft,0,that.height,that.height).writeToElement(this.tinySquare);\r\n"+
            "    	if (that.flagArrowHidden) {\r\n"+
            "    		// trackOffset will be 0, so using height as replacement\r\n"+
            "    		new Rect(0,0,trackLength,that.height).writeToElement(this.trackElement);    		\r\n"+
            "    	} else {\r\n"+
            "    		new Rect(trackOffset,0,trackLength,trackOffset).writeToElement(this.trackElement);\r\n"+
            "    	}\r\n"+
            "    } else{\r\n"+
            "    	new Rect(0,trackOffset+trackLength,trackOffset,trackOffset).writeToElement(this.forwardElement);\r\n"+
            "		new Rect(trackLength+trackOffset*2,0,that.width,that.width).writeToElement(this.tinySquare);\r\n"+
            "    	if (that.flagArrowHidden) {\r\n"+
            "    		// trackOffset will be 0, so using width as replacement\r\n"+
            "    		new Rect(0,0,that.width,trackLength).writeToElement(this.trackElement);\r\n"+
            "    	}\r\n"+
            "    	else {\r\n"+
            "    		new Rect(0,trackOffset,trackOffset,trackLength).writeToElement(this.trackElement);\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "ScrollBarDOM.prototype.repaintCanFit = function(){\r\n"+
            "    if(this.scrollBar.paneLength<this.scrollBar.clipLength){\r\n"+
            "      this.handleElement.style.display='none';\r\n"+
            "    }else{\r\n"+
            "      this.handleElement.style.display='inline';\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBarDOM.prototype.repaintHandle = function(){\r\n"+
            "	var that = this.scrollBar;\r\n"+
            "    if(that.isHorizontal){\r\n"+
            "    	if (that.flagArrowHidden) {\r\n"+
            "    		// not using height since arrows are hidden\r\n"+
            "    		new Rect(that.handleOffset,0,that.handleSize,that.height).writeToElement(this.handleElement);\r\n"+
            "    	} else {\r\n"+
            "    		new Rect(that.ha");
          out.print(
            "ndleOffset+that.height,0,that.handleSize,that.height).writeToElement(this.handleElement);\r\n"+
            "    	}\r\n"+
            "    }else{\r\n"+
            "    	if (that.flagArrowHidden) {\r\n"+
            "    		// not using width since arrows are hidden\r\n"+
            "			new Rect(0,that.handleOffset,that.width,that.handleSize).writeToElement(this.handleElement);\r\n"+
            "    	} else {\r\n"+
            "			new Rect(0,that.handleOffset+that.width,that.width,that.handleSize).writeToElement(this.handleElement);\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "ScrollBarDOM.prototype.repaintHandlePos = function(){\r\n"+
            "	var that = this.scrollBar;\r\n"+
            "	if(that.isHorizontal)\r\n"+
            "		this.handleElement.style.left = toPx(that.handleOffset + that.height);\r\n"+
            "	else\r\n"+
            "		this.handleElement.style.top = toPx(that.handleOffset + that.width);\r\n"+
            "}\r\n"+
            "ScrollBarDOM.prototype.repaintHandleSize = function(){\r\n"+
            "	var that = this.scrollBar;\r\n"+
            "	if(that.isHorizontal)\r\n"+
            "		this.handleElement.style.width = toPx(that.handleSize);\r\n"+
            "	else\r\n"+
            "		this.handleElement.style.height = toPx(that.handleSize);\r\n"+
            "}\r\n"+
            "ScrollBarDOM.prototype.repaint=function(){\r\n"+
            "	var that = this.scrollBar;\r\n"+
            "	if(that.flagInit){\r\n"+
            "		this.repaintCanFit();\r\n"+
            "		this.repaintPositions();\r\n"+
            "		this.repaintHandle();\r\n"+
            "	}\r\n"+
            "	else if(that.flagClipLengthChanged){\r\n"+
            "		this.repaintCanFit();\r\n"+
            "		this.repaintPositions();\r\n"+
            "		this.repaintHandle();\r\n"+
            "	}\r\n"+
            "	else if(that.flagClipTopChanged){\r\n"+
            "		this.repaintCanFit();\r\n"+
            "		this.repaintPositions();\r\n"+
            "		this.repaintHandle();\r\n"+
            "	}\r\n"+
            "	else if(that.flagPaneLengthChanged){\r\n"+
            "		this.repaintCanFit();\r\n"+
            "		this.repaintPositions();\r\n"+
            "		this.repaintHandle();\r\n"+
            "	} else if (that.flagArrowHidden) {\r\n"+
            "		this.repaintCanFit();\r\n"+
            "		this.repaintPositions();\r\n"+
            "		this.repaintHandle();\r\n"+
            "	}\r\n"+
            "	that.flagInit = false;\r\n"+
            "	that.flagClipLengthChanged = false;\r\n"+
            "	that.flagClipTopChanged = false;\r\n"+
            "	that.flagPaneLengthChanged = false;\r\n"+
            "}\r\n"+
            "ScrollBarDOM.prototype.isPointAboveOrBelowHandle=function(e){\r\n"+
            "	var that = this.scrollBar;\r\n"+
            "  var point = getMousePoint(e);\r\n"+
            "  rect = new Rect().readFromElement(this.handleElement);\r\n"+
            "  var loc=that.isHorizontal ? point.getX() : point.getY();\r\n"+
            "  var top=that.isHorizontal ? rect.getLeft() : rect.getTop();\r\n"+
            "  var bot=that.isHorizontal ? rect.getRight() : rect.getBottom();\r\n"+
            "\r\n"+
            "  if(loc<top)\r\n"+
            "      return -1;\r\n"+
            "  else if(loc>bot)\r\n"+
            "      return 1;\r\n"+
            "  else\r\n"+
            "	  return 0;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function ScrollBar(element,isHorizontal){\r\n"+
            "	this.isHorizontal=isHorizontal ? true : false;\r\n"+
            "  	this.rangeTop=0;\r\n"+
            "  	this.rangeBottom=100;\r\n"+
            "  	this.DOM = new ScrollBarDOM(element, this);\r\n"+
            "	this.flagInit = true;\r\n"+
            "}\r\n"+
            "ScrollBar.prototype.width;\r\n"+
            "ScrollBar.prototype.height;\r\n"+
            "ScrollBar.prototype.rangeTop;\r\n"+
            "ScrollBar.prototype.rangeBottom;\r\n"+
            "ScrollBar.prototype.trackLength;\r\n"+
            "ScrollBar.prototype.trackOffset;\r\n"+
            "ScrollBar.prototype.x;\r\n"+
            "ScrollBar.prototype.y;\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollBar.prototype.clipLength; 	//Length of the window\r\n"+
            "ScrollBar.prototype.clipTop;		//Offset of the window\r\n"+
            "ScrollBar.prototype.paneLength; 	//Length of the world\r\n"+
            "\r\n"+
            "ScrollBar.prototype.flagInit=false;\r\n"+
            "ScrollBar.prototype.flagClipLengthChanged=false;\r\n"+
            "ScrollBar.prototype.flagClipTopChanged=false;\r\n"+
            "ScrollBar.prototype.flagPaneLengthChanged=false;\r\n"+
            "ScrollBar.prototype.flagArrowHidden=false;\r\n"+
            "ScrollBar.prototype.hasBothScrolls=false;\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollBar.prototype.setRange=function(top,bottom){\r\n"+
            "	this.rangeTop=top;\r\n"+
            "	this.rangeBottom=bottom;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.setLocation=function(x,y,width,height){\r\n"+
            "    this.width=width;\r\n"+
            "    this.height=height;\r\n"+
            "    this.x = x;\r\n"+
            "    this.y = y;\r\n"+
            "    if(this.isHorizontal){\r\n"+
            "    	this.calculateHTrackLength();\r\n"+
            "    	this.calculateHTrackOffset();\r\n"+
            "    	this.setClipLength(this.width);\r\n"+
            "    }\r\n"+
            "    else{\r\n"+
            "    	this.calculateVTrackLength();\r\n"+
            "    	this.calculateVTrackOffset();\r\n"+
            "    	this.setClipLength(this.height);\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.updateTrack=function() {\r\n"+
            "	var that=this;\r\n"+
            "	if (this.isHorizontal) {\r\n"+
            "		this.calculateHTrackOffset();\r\n"+
            "		this.calculateHTrackLength();\r\n"+
            "	} else {\r\n"+
            "		this.calculateVTrackOffset();\r\n"+
            "		this.calculateVTrackLength();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.setArrowHidden=function(hide) {\r\n"+
            "	this.flagArrowHidden=hide;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.calculateHTrackLength=function() {\r\n"+
            "	if (this.flagArrowHidden) {\r\n"+
            "		if (this.hasBothScrolls) {\r\n"+
            "			this.trackLength=this.width-this.height;\r\n"+
            "		} else {\r\n"+
            "			this.trackLength=this.width;\r\n"+
            "		}\r\n"+
            "	} else {\r\n"+
            "		if (this.hasBothScrolls) {\r\n"+
            "			if (this.height*3 < this.width)\r\n"+
            "				this.trackLength=this.width-this.height*3;\r\n"+
            "			else\r\n"+
            "				this.trackLength=this.width-this.height*2;	\r\n"+
            "			\r\n"+
            "		} else {\r\n"+
            "			this.trackLength=this.width-this.height*2;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.calculateHTrackOffset=function() {\r\n"+
            "	if (!this.flagArrowHidden)\r\n"+
            "		this.trackOffset=this.height;\r\n"+
            "	else\r\n"+
            "		this.trackOffset=0;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.calculateVTrackLength=function() {\r\n"+
            "	if (this.flagArrowHidden) {\r\n"+
            "		if (this.hasBothScrolls) {\r\n"+
            "			// account for tiny square\r\n"+
            "			this.trackLength=this.height-this.width;\r\n"+
            "		} else {\r\n"+
            "			// get all space\r\n"+
            "			this.trackLength=this.height;\r\n"+
            "		}\r\n"+
            "	} else {\r\n"+
            "		if (this.hasBothScrolls) {\r\n"+
            "			if (this.width*3 < this.height) {\r\n"+
            "				// two sides + tiny square\r\n"+
            "				this.trackLength=this.height-this.width*3;\r\n"+
            "			}\r\n"+
            "			else\r\n"+
            "				this.trackLength=this.height-this.width*2;\r\n"+
            "		} else {\r\n"+
            "			// two sides\r\n"+
            "			this.trackLength=this.height-this.width*2;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.calculateVTrackOffset=function() {\r\n"+
            "	if (!this.flagArrowHidden)\r\n"+
            "		this.trackOffset=this.width;\r\n"+
            "	else\r\n"+
            "		this.trackOffset=0;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollBar.prototype.onTrackClicked=function(e){\r\n"+
            "  var val = this.DOM.isPointAboveOrBelowHandle(e);\r\n"+
            "  if(val != 0)\r\n"+
            "	  this.goPage(val);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.goPage=function(inc){\r\n"+
            "	this.setClipTop(this.clipTop + this.clipLength*inc);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.onDragging=function(e,x,y){\r\n"+
            "  var pos;\r\n"+
            "  if (this.flagArrowHidden) {\r\n"+
            "	  // disregard arrow size in calculation if arrows are hidden\r\n"+
            "	  if (this.isHorizontal)\r\n"+
            "		  pos = this.DOM.handleElement.offsetLeft;\r\n"+
            "	  else\r\n"+
            "		  pos = this.DOM.handleElement.offsetTop;\r\n"+
            "  } else {\r\n"+
            "	  if(this.isHorizontal)\r\n"+
            "		  pos = this.DOM.handleElement.offsetLeft - this.height;\r\n"+
            "	  else\r\n"+
            "		  pos = this.DOM.handleElement.offsetTop - this.width;\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  this.setClipTop(pos/this.ratio);\r\n"+
            "  if(this.onDraggingListener)\r\n"+
            "	  this.onDraggingListener(true);\r\n"+
            "  return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.onDraggingEnd=function(e,x,y){\r\n"+
            "  if(this.onDraggingListener)\r\n"+
            "	  this.onDraggingListener(false);\r\n"+
            "  return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.setPaneLength=function(paneLength){\r\n"+
            "	if(this.paneLength == paneLength)\r\n"+
            "		return;\r\n"+
            "	this.paneLength=paneLength;\r\n"+
            "	\r\n"+
            "	this.flagPaneLengthChanged = true;\r\n"+
            "	this.updateScroll();\r\n"+
            "	this.DOM.repaint();\r\n"+
            "}\r\n"+
            "ScrollBar.prototype.getPaneLength=function(){\r\n"+
            "	return this.paneLength;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.setClipTop=function(top){\r\n"+
            "	if(this.clipTop == top)\r\n"+
            "		return;\r\n"+
            "	this.clipTop=top;\r\n"+
            "		  \r\n"+
            "	this.flagClipTopChanged=true;\r\n"+
            "	this.updateScroll();\r\n"+
            "	this.DOM.repaint();\r\n"+
            "  \r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.setClipTopNoFire=function(top){\r\n"+
            "	if(this.clipTop == top)\r\n"+
            "		return;\r\n"+
            "	this.clipTop=top;\r\n"+
            "		  \r\n"+
            "	this.flagClipTopChanged=true;\r\n"+
            "	this.updateScrollNoFire();\r\n"+
            "	this.DOM.repaint();\r\n"+
            "  \r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollBar.prototype.setClipLength=function(length){\r\n"+
            "  if(isNaN(length))\r\n"+
            "	  length=0;\r\n"+
            "  this.clipLength=length;\r\n"+
            "  \r\n"+
            "  this.flagClipLengthChanged=true;\r\n"+
            "  this.updateScroll();\r\n"+
            "  this.DOM.repaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.getClipLength=function(){\r\n"+
            "  return this.clipLength;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollBar.prototype.updateScroll=function(){\r\n"+
            "	if(this.clipTop+this.clipLength>this.paneLength)\r\n"+
            "		this.clipTop=this.paneLength-this.clipLength;\r\n"+
            "	if(this.clipTop<0)\r\n"+
            "		this.clipTop = 0;\r\n"+
            "	\r\n"+
            "	// below displays the h/v scroll bar\r\n"+
            "	if(this.paneLength != null && this.clipLength != null){\r\n"+
            "		// panel length is the total length of the window content (visible + invisible)\r\n"+
            "		var ratio = this.trackLength / this.paneLength;\r\n"+
            "		// clip Length should be the total length of the scroll container\r\n"+
            "		var handleSize=this.clipLength * ratio;\r\n"+
            "		var minLen = 45;\r\n"+
            "		if (handleSize < minLen) {\r\n"+
            "			var diff = minLen - handleSize;\r\n"+
            "			handleSize = minLen;\r\n"+
            "			ratio = (this.trackLength - diff) / this.paneLength;\r\n"+
            "		} else if (handleSize < this.trackOffset){\r\n"+
            "			ratio = (this.trackLength - this.trackOffset + handleSize) / this.paneLength;\r\n"+
            "			handleSize = this.trackOffset;\r\n"+
            "		}\r\n"+
            "		this.ratio = ratio;\r\n"+
            "		this.handleSize = handleSize;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	// below controls scroll bar movement when you scroll to invisible rows\r\n"+
            "	if(this.clipTop!=null && this.ratio!= null){\r\n"+
            "		this.handleOffset = this.clipTop * this.ratio;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "  if(this.onscroll)\r\n"+
            "    this.onscroll(this);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.updateScrollNoFire=function(){\r\n"+
            "	if(this.clipTop+this.clipLength>this.paneLength)\r\n"+
            "		this.clipTop=this.paneLength-this.clipLength;\r\n"+
            "	if(this.clipTop<0)\r\n"+
            "		this.clipTop = 0;\r\n"+
            "	\r\n"+
            "	// below displays the h/v scroll bar\r\n"+
            "	if(this.paneLength != null && this.clipLength != null){\r\n"+
            "		// panel length is the total length of the window content (visible + invisible)\r\n"+
            "		var ratio = this.trackLength / this.paneLength;\r\n"+
            "		// clip Length should be the total length of the scroll container\r\n"+
            "		var handleSize=this.clipLength * ratio;\r\n"+
            "		var minLen = 45;\r\n"+
            "		if (handleSize < minLen) {\r\n"+
            "			var diff = minLen - handleSize;\r\n"+
            "			handleSize = minLen;\r\n"+
            "			ratio = (this.trackLength - diff) / this.paneLength;\r\n"+
            "		} else if (handleSize < this.trackOffset){\r\n"+
            "			ratio = (this.trackLength - this.trackOffset + handleSize) / this.paneLength;\r\n"+
            "			handleSize = this.trackOffset;\r\n"+
            "		}\r\n"+
            "		this.ratio = ratio;\r\n"+
            "		this.handleSize = handleSize;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	// below controls scroll bar movement when you scroll to invisible rows\r\n"+
            "	if(this.clipTop!=null && this.ratio!= null){\r\n"+
            "		this.handleOffset = this.clipTop * this.ratio;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollBar.prototype.getClipTop=function(){\r\n"+
            "  return this.clipTop;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollBar.prototype.setHasBothScrolls=function(hbs){\r\n"+
            "  this.hasBothScrolls=hbs;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function ScrollPaneDOM(element, innerPane, scrollPane){\r\n"+
            "	this.scrollPane = scrollPane; \r\n"+
            "	var that = this.scrollPane;\r\n"+
            "	this.scrollPaneElement=element;\r\n"+
            "	this.hscrollElement=nw(\"div\");\r\n"+
            "	this.vscrollElement=nw(\"div\");\r\n"+
            "	this.paneElement=nw(\"div\");\r\n"+
            "	this.paneElement.classList.a");
          out.print(
            "dd('scrollpane');\r\n"+
            "	if(innerPane){\r\n"+
            "		this.innerpaneElement=innerPane;\r\n"+
            "	}else{\r\n"+
            "		this.innerpaneElement=nw(\"div\");\r\n"+
            "		this.innerpaneElement.classList.add('scrollpane_inner');\r\n"+
            "	}\r\n"+
            "	this.scrollPaneElement.classList.add('scrollpane_container');\r\n"+
            "	this.scrollPaneElement.appendChild(this.paneElement);\r\n"+
            "	this.paneElement.appendChild(this.innerpaneElement);\r\n"+
            "	//  this.scrollPaneElement.appendChild(this.paneElement);\r\n"+
            "	this.scrollPaneElement.appendChild(this.paneElement);\r\n"+
            "	\r\n"+
            "	this.innerpaneElement.onMouseWheel=function(e,delta){that.onMouseWheel(e,delta);};\r\n"+
            "	\r\n"+
            "  if(that.isAutohide){\r\n"+
            "    this.vscrollElement.onmouseenter=function(e){that.onMouseEntered(e);};\r\n"+
            "    this.vscrollElement.onmouseleave=function(e){that.onMouseExited(e);};\r\n"+
            "    this.hscrollElement.onmouseenter=function(e){that.onMouseEntered(e);};\r\n"+
            "    this.hscrollElement.onmouseleave=function(e){that.onMouseExited(e);};\r\n"+
            "    this.vscrollElement.style.opacity=0;\r\n"+
            "    this.hscrollElement.style.opacity=0;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "function ScrollPane(element,scrollSize,innerPane){\r\n"+
            "  var that=this;\r\n"+
            "  this.width=0;\r\n"+
            "  this.height=0;\r\n"+
            "  this.isAutohide=false;\r\n"+
            "  this.vscrollVisible=false;\r\n"+
            "  this.hscrollVisible=false;\r\n"+
            "  this.ticks=[];\r\n"+
            "  \r\n"+
            "  this.isvscrollDefault = true;\r\n"+
            "  this.scrollSize=scrollSize;\r\n"+
            "  if(innerPane){\r\n"+
            "	  this.autoMove=false;\r\n"+
            "  }else{\r\n"+
            "	this.autoMove=true;\r\n"+
            "  }\r\n"+
            "  this.onTickClickFunc=function(e){e.stopPropagation();return that.onTickClick(getMouseTarget(e).id,getMouseTarget(e).pos);};\r\n"+
            "  this.onMouseWheelFunc=function(e,delta){return that.onMouseWheel(e,delta);};\r\n"+
            "  \r\n"+
            "  this.DOM=new ScrollPaneDOM(element, innerPane, this);\r\n"+
            "  this.vscroll=new ScrollBar(this.DOM.vscrollElement,false);\r\n"+
            "  this.hscroll=new ScrollBar(this.DOM.hscrollElement,true);\r\n"+
            "  this.hscroll.clipTop = 0;\r\n"+
            "  this.vscroll.clipTop = 0;\r\n"+
            "  this.hscroll.onscroll=function(){that.onScrollbarsScroll()};\r\n"+
            "  this.vscroll.onscroll=function(){that.onScrollbarsScroll()};\r\n"+
            "  \r\n"+
            "  /*This never gets called?*/\r\n"+
            "  if(this.isAutohide){\r\n"+
            "    this.vscroll.onDraggingListener=function(isDragging){that.onDragging(isDragging);}\r\n"+
            "    this.hscroll.onDraggingListener=function(isDragging){that.onDragging(isDragging);}\r\n"+
            "    this.isScrollDragging=false;\r\n"+
            "    this.isMouseOver=false;\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  this.hscrollClipTop = this.hscroll.getClipTop();\r\n"+
            "  this.vscrollClipTop = this.vscroll.getClipTop();\r\n"+
            "  this.hscrollChanged = 0.0;\r\n"+
            "  this.vscrollChanged = 0.0;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollPane.prototype.hscrollClipTop;\r\n"+
            "ScrollPane.prototype.vscrollClipTop;\r\n"+
            "ScrollPane.prototype.hscrollChanged;\r\n"+
            "ScrollPane.prototype.vscrollChanged;\r\n"+
            "\r\n"+
            "ScrollPane.prototype.flagLocationPosChanged = false;\r\n"+
            "ScrollPane.prototype.flagLocationSizeChanged = false;\r\n"+
            "ScrollPane.prototype.flagPaneSizeChanged = false;\r\n"+
            "ScrollPane.prototype.flagPaneElementSizeChanged = false;\r\n"+
            "\r\n"+
            "ScrollPane.prototype.onMouseEntered=function(e){\r\n"+
            "	this.isMouseOver=true;\r\n"+
            "	this.DOM.vscrollElement.style.opacity=1;\r\n"+
            "	this.DOM.hscrollElement.style.opacity=1;\r\n"+
            "}\r\n"+
            "ScrollPane.prototype.onMouseExited=function(e){\r\n"+
            "	this.isMouseOver=false;\r\n"+
            "	if(this.isScrollDragging)\r\n"+
            "		return;\r\n"+
            "	this.DOM.vscrollElement.style.opacity=0;\r\n"+
            "	this.DOM.hscrollElement.style.opacity=0;\r\n"+
            "}\r\n"+
            "ScrollPane.prototype.onDragging=function(e){\r\n"+
            "	this.isScrollDragging=e;\r\n"+
            "	if(!this.isScrollDragging && !this.isMouseOver){\r\n"+
            "	  this.DOM.vscrollElement.style.opacity=0;\r\n"+
            "	  this.DOM.hscrollElement.style.opacity=0;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollPane.prototype.setSize=function(size){\r\n"+
            "	// Scrollbarsize;\r\n"+
            "	this.scrollSize=size;\r\n"+
            "	this.updateScroll();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.handleWheel=function(e){\r\n"+
            "	var den=e.shiftKey ? 1 : 10;\r\n"+
            "	var delta;\r\n"+
            "	if(e.deltaMode == WheelEvent.DOM_DELTA_PIXEL)\r\n"+
            "		delta = e.deltaY/-100;\r\n"+
            "	else\r\n"+
            "		delta = e.deltaY/-1;\r\n"+
            "\r\n"+
            "	//Decide which scroll direction to scroll\r\n"+
            "	if(e.target == this.vscroll.DOM.trackElement || e.target==this.vscroll.DOM.gripElement)\r\n"+
            "	    this.vscroll.goPage(-delta/den);\r\n"+
            "	else if(e.altKey || e.target == this.hscroll.DOM.trackElement || e.target==this.hscroll.DOM.gripElement)\r\n"+
            "	    this.hscroll.goPage(-delta/den);\r\n"+
            "    else{\r\n"+
            "    	//Default scroll vertical\r\n"+
            "    	if(this.isvscrollDefault)\r\n"+
            "    		this.vscroll.goPage(-delta/den);\r\n"+
            "    	else\r\n"+
            "    		this.hscroll.goPage(-delta/den);\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "ScrollPane.prototype.onMouseWheel=function(e,delta){\r\n"+
            "	if(e.target.className!='menu_item_text')\r\n"+
            "	return;\r\n"+
            "	var den=e.shiftKey ? 1 : 10;\r\n"+
            "	if(e.altKey){\r\n"+
            "	    this.hscroll.goPage(-delta/den);\r\n"+
            "    }else{\r\n"+
            "	    this.vscroll.goPage(-delta/den);\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "ScrollPane.prototype.onTickClick=function(id,pos){\r\n"+
            "	this.setClipTop(pos);\r\n"+
            "}\r\n"+
            "ScrollPane.prototype.lastRun=0;\r\n"+
            "ScrollPane.prototype.onScrollbarsScroll=function(){\r\n"+
            "    if(this.timeout==null){\r\n"+
            "    	var delay=this.lastRun+40-Date.now();//40=25 FPS\r\n"+
            "	    if(delay<=0){\r\n"+
            "            this.onScrollbarsScroll2();\r\n"+
            "	    }else{\r\n"+
            "            var that=this;\r\n"+
            "            this.timeout=setTimeout(function(){that.timeout=null;that.onScrollbarsScroll2();},delay);\r\n"+
            "	    }\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "ScrollPane.prototype.onScrollbarsScroll2=function(){\r\n"+
            "	this.lastRun=Date.now();\r\n"+
            "	//called alot\r\n"+
            "  if(this.hscrollClipTop != this.hscroll.getClipTop()){\r\n"+
            "	  this.hscrollChanged = this.hscroll.getClipTop() - this.hscrollClipTop;\r\n"+
            "	  this.hscrollClipTop = this.hscroll.getClipTop();\r\n"+
            "  }\r\n"+
            "  if(this.vscrollClipTop != this.vscroll.getClipTop()){\r\n"+
            "	  this.vscrollChanged = this.vscroll.getClipTop() - this.vscrollClipTop;\r\n"+
            "	  this.vscrollClipTop = this.vscroll.getClipTop();\r\n"+
            "  }\r\n"+
            "  if(!this.hscrollChanged && !this.vscrollChanged)\r\n"+
            "	  return;\r\n"+
            "  \r\n"+
            "  if(this.autoMove){\r\n"+
            "    this.DOM.innerpaneElement.style.left=-1*(this.hscroll.getClipTop());\r\n"+
            "    this.DOM.innerpaneElement.style.top=-1*(this.vscroll.getClipTop());\r\n"+
            "  }\r\n"+
            "  if(this.linkedPane){\r\n"+
            "    this.linkedPane.setClipTop(this.getClipTop());\r\n"+
            "  }\r\n"+
            "  if(this.onScroll)\r\n"+
            "	  this.onScroll();\r\n"+
            "  this.hscrollChanged = 0.0;\r\n"+
            "  this.vscrollChanged = 0.0;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.getClipWidth=function(){\r\n"+
            "	return this.hscroll.getClipLength();\r\n"+
            "}\r\n"+
            "ScrollPane.prototype.getClipHeight=function(){\r\n"+
            "	return this.vscroll.getClipLength();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.setLocation=function(x,y,width,height){\r\n"+
            "	if(this.x != x || this.y != y)\r\n"+
            "		this.flagLocationPosChanged = true;\r\n"+
            "	if(this.width!= width|| this.height!= height)\r\n"+
            "		this.flagLocationSizeChanged = true;\r\n"+
            "		\r\n"+
            "  this.width=width;\r\n"+
            "  this.height=height;\r\n"+
            "  this.x=x;\r\n"+
            "  this.y=y;\r\n"+
            "  this.updateScroll();\r\n"+
            "}\r\n"+
            "ScrollPane.prototype.setPaneSize=function(paneWidth,paneHeight){\r\n"+
            "  this.paneWidth=paneWidth;\r\n"+
            "  this.paneHeight=paneHeight;\r\n"+
            "  this.flagPaneSizeChanged = true;\r\n"+
            "  this.updateScroll();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.ensureVisible=function(rect){\r\n"+
            "	var rectScrollPane = this.DOM.paneElement.getBoundingClientRect();\r\n"+
            "	if(rect.left < rectScrollPane.left){\r\n"+
            "		this.setClipLeft(this.getClipLeft() - (rectScrollPane.left - rect.left));\r\n"+
            "	}\r\n"+
            "	else if(rect.right > rectScrollPane.right){\r\n"+
            "		this.setClipLeft(this.getClipLeft() - (rectScrollPane.right - rect.right));\r\n"+
            "	}\r\n"+
            "	if(rect.top < rectScrollPane.top){\r\n"+
            "		this.setClipTop(this.getClipTop() - (rectScrollPane.top - rect.top));\r\n"+
            "	}\r\n"+
            "	else if(rect.bottom > rectScrollPane.bottom){\r\n"+
            "		this.setClipTop(this.getClipTop() - (rectScrollPane.bottom - rect.bottom));\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ScrollPane.prototype.updateScroll=function(){\r\n"+
            "\r\n"+
            "  var hvis=true;\r\n"+
            "  var vvis=true;\r\n"+
            "  if(this.paneWidth<=this.width && this.paneHeight <= this.height){\r\n"+
            "	  hvis=false;\r\n"+
            "	  vvis=false;\r\n"+
            "  }else if(this.paneWidth<=this.width - this.scrollSize){\r\n"+
            "	  hvis=false;\r\n"+
            "  }else if(this.paneHeight<=this.height - this.scrollSize){\r\n"+
            "	  vvis=false;\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  var hSize=hvis ? this.scrollSize : 0;\r\n"+
            "  var vSize=vvis ? this.scrollSize : 0; \r\n"+
            "  if(hvis!=this.hscrollVisible){\r\n"+
            "	 if(hvis)\r\n"+
            "       this.DOM.scrollPaneElement.appendChild(this.DOM.hscrollElement);\r\n"+
            "	 else\r\n"+
            "       this.DOM.scrollPaneElement.removeChild(this.DOM.hscrollElement);\r\n"+
            "	 this.hscrollVisible=hvis;\r\n"+
            "  }\r\n"+
            "  if(vvis!=this.vscrollVisible){\r\n"+
            "	 if(vvis)\r\n"+
            "       this.DOM.scrollPaneElement.appendChild(this.DOM.vscrollElement);\r\n"+
            "	 else\r\n"+
            "       this.DOM.scrollPaneElement.removeChild(this.DOM.vscrollElement);\r\n"+
            "	 this.vscrollVisible=vvis;\r\n"+
            "  }\r\n"+
            "  if (this.hscrollVisible==true && this.vscrollVisible==true) {\r\n"+
            "	  this.vscroll.setHasBothScrolls(true);\r\n"+
            "	  this.hscroll.setHasBothScrolls(true);\r\n"+
            "	  // ensure last column is fully visible, taking into account vertical scrollbar width\r\n"+
            "	  this.hscroll.setPaneLength(this.paneWidth+hSize);\r\n"+
            "	  // ensure last row is fully visible\r\n"+
            "	  this.vscroll.setPaneLength(this.paneHeight+vSize);\r\n"+
            "  } else {\r\n"+
            "	  this.vscroll.setHasBothScrolls(false);\r\n"+
            "	  this.hscroll.setHasBothScrolls(false);\r\n"+
            "	  this.hscroll.setPaneLength(this.paneWidth);\r\n"+
            "	  this.vscroll.setPaneLength(this.paneHeight);\r\n"+
            "  }\r\n"+
            "  new Rect(this.x,this.y,this.width,this.height).writeToElement(this.DOM.scrollPaneElement);\r\n"+
            "  if (this.hscroll.flagArrowHidden==true && this.vscrollVisible==false) {\r\n"+
            "	  this.hscroll.setClipLength(this.width);\r\n"+
            "  } else {\r\n"+
            "	  //account for tiny square\r\n"+
            "	  this.hscroll.setClipLength(this.width-vSize);\r\n"+
            "  }\r\n"+
            "  if(this.isAutohide)\r\n"+
            "	    this.vscroll.setClipLength(this.height);\r\n"+
            "	  else\r\n"+
            "	    this.vscroll.setClipLength(this.height-hSize);\r\n"+
            "  this.hscroll.setLocation(0,this.height-this.scrollSize,this.width,this.scrollSize);\r\n"+
            "  this.vscroll.setLocation(this.width-this.scrollSize,0,this.scrollSize,this.height);	  \r\n"+
            "  \r\n"+
            "  if(this.isAutohide)\r\n"+
            "    new Rect(0,0,this.width,this.height).writeToElement(this.DOM.paneElement);\r\n"+
            "  else\r\n"+
            "    new Rect(0,0,this.width-vSize,this.height-hSize).writeToElement(this.DOM.paneElement);\r\n"+
            "  this.hscroll.updateScroll();\r\n"+
            "  this.vscroll.updateScroll();\r\n"+
            "  \r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.setPane=function(element){\r\n"+
            "  this.DOM.innerpaneElement.appendChild(element);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.getClipTop=function(){\r\n"+
            "  return this.vscroll.getClipTop();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.getClipLeft=function(){\r\n"+
            "  return this.hscroll.getClipTop();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.setClipTop=function(t){\r\n"+
            "  return this.vscroll.setClipTop(t);\r\n"+
            "}\r\n"+
            "\r\n"+
            "//add setClipTopNoFire(), which does not fire onScroll() cb\r\n"+
            "ScrollPane.prototype.setClipTopNoFire=function(t){\r\n"+
            "	  return this.v");
          out.print(
            "scroll.setClipTopNoFire(t);\r\n"+
            "	}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.linkToScrollBar=function(t){\r\n"+
            "	this.linkedPane=t;\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.setClipLeft=function(t){\r\n"+
            "  return this.hscroll.setClipTop(t);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.clearTicks=function(){\r\n"+
            "	for(var i in this.ticks)\r\n"+
            "		this.vscroll.DOM.trackElement.removeChild(this.ticks[i]);\r\n"+
            "	this.ticks=[];\r\n"+
            "}\r\n"+
            "\r\n"+
            "ScrollPane.prototype.addTick=function(id,left,top,width,height,style){\r\n"+
            "	top+=this.scrollSize;\r\n"+
            "	var div=nw('div','scrollbar_tick');\r\n"+
            "	div.style.left=toPx(left);\r\n"+
            "	div.style.top=toPx(top);\r\n"+
            "	div.style.width=toPx(width);\r\n"+
            "	div.style.height=toPx(height);\r\n"+
            "	div.style.backgroundColor=style;\r\n"+
            "	div.id=id;\r\n"+
            "	div.pos=top;\r\n"+
            "	div.onclick=this.onTickClickFunc;\r\n"+
            "	this.vscroll.DOM.trackElement.appendChild(div);\r\n"+
            "	this.ticks[this.ticks.length]=div;\r\n"+
            "}\r\n"+
            "ScrollPane.prototype.updateIconsColor=function(scrollIconsColor){\r\n"+
            "	if (scrollIconsColor == null)\r\n"+
            "		return;\r\n"+
            "	vScrollGripStyle=scrollIconsColor+';stroke-width:14;stroke-miterlimit:10;\" x1=\"12\" y1=\"24.1\" x2=\"88\" y2=\"24.1\"/> <line style=\"fill:none;stroke:'+scrollIconsColor+';stroke-width:14;stroke-miterlimit:10;\" x1=\"12\" y1=\"50\" x2=\"88\" y2=\"50\"/> <line style=\"fill:none;stroke:'+scrollIconsColor;\r\n"+
            "	hScrollGripStyle=scrollIconsColor+';stroke-width:14;stroke-miterlimit:10;\" x1=\"76.1\" y1=\"13.5\" x2=\"76.1\" y2=\"89.5\"/> <line style=\"fill:none;stroke:'+scrollIconsColor+';stroke-width:14;stroke-miterlimit:10;\" x1=\"50.2\" y1=\"13.5\" x2=\"50.2\" y2=\"89.5\"/> <line style=\"fill:none;stroke:'+scrollIconsColor;\r\n"+
            "	scrollUpStyle=scrollIconsColor;\r\n"+
            "	scrollDownStyle=scrollIconsColor;\r\n"+
            "	scrollLeftStyle=scrollIconsColor;\r\n"+
            "	scrollRightStyle=scrollIconsColor;\r\n"+
            "  	vScrollGripStyle=vScrollGripStyle.replace(/#/g, \"%23\");\r\n"+
            "  	hScrollGripStyle=hScrollGripStyle.replace(/#/g, \"%23\");\r\n"+
            "  	scrollUpStyle=scrollUpStyle.replace(/#/g, \"%23\");\r\n"+
            "  	scrollDownStyle=scrollDownStyle.replace(/#/g, \"%23\");\r\n"+
            "  	scrollLeftStyle=scrollLeftStyle.replace(/#/g, \"%23\");\r\n"+
            "  	scrollRightStyle=scrollRightStyle.replace(/#/g, \"%23\");\r\n"+
            "	this.vscroll.DOM.gripElement.style.backgroundImage=(SCROLLBAR_GRIP_V_IMAGE_PREFIX+vScrollGripStyle+SCROLLBAR_GRIP_V_IMAGE_SUFFIX);\r\n"+
            "	this.hscroll.DOM.gripElement.style.backgroundImage=(SCROLLBAR_GRIP_H_IMAGE_PREFIX+hScrollGripStyle+SCROLLBAR_GRIP_H_IMAGE_SUFFIX);\r\n"+
            "	this.vscroll.DOM.backElement.style.backgroundImage=(SCROLLBAR_UP_IMAGE_PREFIX+scrollUpStyle+SCROLLBAR_UP_IMAGE_SUFFIX);\r\n"+
            "	this.vscroll.DOM.forwardElement.style.backgroundImage=(SCROLLBAR_DOWN_IMAGE_PREFIX+scrollDownStyle+SCROLLBAR_DOWN_IMAGE_SUFFIX);\r\n"+
            "	this.hscroll.DOM.backElement.style.backgroundImage=(SCROLLBAR_LEFT_IMAGE_PREFIX+scrollLeftStyle+SCROLLBAR_LEFT_IMAGE_SUFFIX);\r\n"+
            "	this.hscroll.DOM.forwardElement.style.backgroundImage=(SCROLLBAR_RIGHT_IMAGE_PREFIX+scrollRightStyle+SCROLLBAR_RIGHT_IMAGE_SUFFIX);\r\n"+
            "}\r\n"+
            "");

	}
	
}