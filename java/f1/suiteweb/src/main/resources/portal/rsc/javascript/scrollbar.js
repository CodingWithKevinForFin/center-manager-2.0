
var SCROLLBAR_GRIP_V_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="Layer_1"> </g> <g id="plus"> </g> <g id="check"> </g> <g id="popout"> </g> <g id="popin"> </g> <g id="minimize"> </g> <g id="maximize"> </g> <g id="xclose"> </g> <g id="dropdown"> </g> <g id="hidden"> </g> <g id="visible"> </g> <g id="search"> </g> <g id="scrolldown"> </g> <g id="scrollup"> </g> <g id="scrollleft"> </g> <g id="scrollright"> </g> <g id="vertical_x5F_scrollbar"> <g> <line style="fill:none;stroke:';
var SCROLLBAR_GRIP_V_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;" x1="12" y1="76.3" x2="88" y2="76.3"/> </g> </g> <g id="horizontal_x5F_scrollbar"> </g> <g id="save"> </g> '+SVG_SUFFIX;
var SCROLLBAR_GRIP_H_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="Layer_1"> </g> <g id="plus"> </g> <g id="check"> </g> <g id="popout"> </g> <g id="popin"> </g> <g id="minimize"> </g> <g id="maximize"> </g> <g id="xclose"> </g> <g id="dropdown"> </g> <g id="hidden"> </g> <g id="visible"> </g> <g id="search"> </g> <g id="scrolldown"> </g> <g id="scrollup"> </g> <g id="scrollleft"> </g> <g id="scrollright"> </g> <g id="vertical_x5F_scrollbar"> </g> <g id="horizontal_x5F_scrollbar"> <g> <line style="fill:none;stroke:';
var SCROLLBAR_GRIP_H_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;" x1="23.9" y1="13.5" x2="23.9" y2="89.5"/> </g> </g> <g id="save"> </g> '+SVG_SUFFIX;
var SCROLLBAR_UP_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="Layer_1"> </g> <g id="plus"> </g> <g id="check"> </g> <g id="popout"> </g> <g id="popin"> </g> <g id="minimize"> </g> <g id="maximize"> </g> <g id="xclose"> </g> <g id="dropdown"> </g> <g id="hidden"> </g> <g id="visible"> </g> <g id="search"> </g> <g id="scrolldown"> </g> <g id="scrollup"> <polyline style="fill:none;stroke:';
var SCROLLBAR_UP_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;" points="11.9,69 50,31 88.1,69 	"/> </g> <g id="scrollleft"> </g> <g id="scrollright"> </g> <g id="vertical_x5F_scrollbar"> </g> <g id="horizontal_x5F_scrollbar"> </g> <g id="save"> </g> '+SVG_SUFFIX;
var SCROLLBAR_DOWN_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="Layer_1"> </g> <g id="plus"> </g> <g id="check"> </g> <g id="popout"> </g> <g id="popin"> </g> <g id="minimize"> </g> <g id="maximize"> </g> <g id="xclose"> </g> <g id="dropdown"> </g> <g id="hidden"> </g> <g id="visible"> </g> <g id="search"> </g> <g id="scrolldown"> <polyline style="fill:none;stroke:';
var SCROLLBAR_DOWN_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;" points="88.1,31 50,69 11.9,31 	"/> </g> <g id="scrollup"> </g> <g id="scrollleft"> </g> <g id="scrollright"> </g> <g id="vertical_x5F_scrollbar"> </g> <g id="horizontal_x5F_scrollbar"> </g> <g id="save"> </g> '+SVG_SUFFIX;
var SCROLLBAR_LEFT_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="Layer_1"> </g> <g id="plus"> </g> <g id="check"> </g> <g id="popout"> </g> <g id="popin"> </g> <g id="minimize"> </g> <g id="maximize"> </g> <g id="xclose"> </g> <g id="dropdown"> </g> <g id="hidden"> </g> <g id="visible"> </g> <g id="search"> </g> <g id="scrolldown"> </g> <g id="scrollup"> </g> <g id="scrollleft"> <polyline style="fill:none;stroke:';
var SCROLLBAR_LEFT_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;" points="69,88.1 31,50 69,11.9 	"/> </g> <g id="scrollright"> </g> <g id="vertical_x5F_scrollbar"> </g> <g id="horizontal_x5F_scrollbar"> </g> <g id="save"> </g> '+SVG_SUFFIX;
var SCROLLBAR_RIGHT_IMAGE_PREFIX=SVG_PREFIX + ' x="0px" y="0px" viewBox="0 0 100 100" style="enable-background:new 0 0 100 100;" xml:space="preserve"> <g id="Layer_1"> </g> <g id="plus"> </g> <g id="check"> </g> <g id="popout"> </g> <g id="popin"> </g> <g id="minimize"> </g> <g id="maximize"> </g> <g id="xclose"> </g> <g id="dropdown"> </g> <g id="hidden"> </g> <g id="visible"> </g> <g id="search"> </g> <g id="scrolldown"> </g> <g id="scrollup"> </g> <g id="scrollleft"> </g> <g id="scrollright"> <polyline style="fill:none;stroke:';
var SCROLLBAR_RIGHT_IMAGE_SUFFIX=';stroke-width:14;stroke-miterlimit:10;" points="32.3,11.9 70.3,50 32.3,88.1 	"/> </g> <g id="vertical_x5F_scrollbar"> </g> <g id="horizontal_x5F_scrollbar"> </g> <g id="save"> </g> '+SVG_SUFFIX;

function ScrollBarDOM(element, scrollBar){
	this.scrollBar = scrollBar;
	var that = scrollBar;
	this.scrollElement=element;
	this.scrollElement.className="scrollbar_container";
	
	this.trackElement=nw("div");
	this.trackElement.className=that.isHorizontal ? "scrollbar_track_h" : "scrollbar_track_v";
	this.gripElement=nw('div',that.isHorizontal ? 'scrollbar_grip_h' : 'scrollbar_grip_v');
	this.handleElement=nw("div");
	this.handleElement.className=that.isHorizontal ? "scrollbar_handle_h" : "scrollbar_handle_v";
	this.backElement=nw("div");
	this.backElement.className=that.isHorizontal ? "scrollbar_left" : "scrollbar_up";
	this.forwardElement=nw("div");
	this.forwardElement.className=that.isHorizontal ? "scrollbar_right" : "scrollbar_down";
	this.tinySquare=nw("div", "tiny_square");
	this.tinySquare.style.display="block";
	
	this.handleElement.appendChild(this.gripElement);
	this.scrollElement.appendChild(this.trackElement);
	this.scrollElement.appendChild(this.handleElement);
	this.scrollElement.appendChild(this.forwardElement);
	this.scrollElement.appendChild(this.tinySquare);
	this.scrollElement.appendChild(this.backElement);
	

	makeDraggable(this.gripElement,this.handleElement,!that.isHorizontal,that.isHorizontal);
	this.gripElement.ondragging=function(e,x,y){that.onDragging(e,x,y);};
	this.gripElement.ondraggingEnd=function(e,x,y){that.onDraggingEnd(e,x,y);};

	makeMouseRepeat(this.trackElement);
	this.trackElement.onMouseRepeat = function(e){that.onTrackClicked(e);};

	makeMouseRepeat(this.backElement);
	this.backElement.onMouseRepeat=function(){that.goPage(-.25)};

	makeMouseRepeat(this.forwardElement);
	this.forwardElement.onMouseRepeat=function(){that.goPage(.25)};
}
ScrollBarDOM.prototype.applyTinySquareColor=function(cornerColor){
	this.tinySquare.style.backgroundColor=cornerColor;
}
ScrollBarDOM.prototype.applyColors=function(gripColor, trackColor, trackButtonColor, borderColor, cornerColor){
	this.gripElement.style.backgroundColor=gripColor;
	this.trackElement.style.backgroundColor=trackColor;
	
	this.forwardElement.style.backgroundColor=trackButtonColor;
	this.tinySquare.style.backgroundColor=cornerColor;
	this.backElement.style.backgroundColor=trackButtonColor;
	this.trackElement.style.borderColor=borderColor;
	this.handleElement.style.borderColor=borderColor;
	this.forwardElement.style.borderColor=borderColor;
	this.backElement.style.borderColor=borderColor;
}

ScrollBarDOM.prototype.applyBorderRadius=function(borderRadius){
	var length;
	var sb = this.scrollBar;
	if (sb.isHorizontal) {
		length = this.handleElement.style.width;
		if (!length)
			length = sb.width;
	} else {
		length = this.handleElement.style.height;
		if (!length)
			length = sb.height;
	}
	if (typeof length == "string")
		length = Number(fromPx(length));
	var derived = length* (borderRadius/100.0);
	this.handleElement.style.borderRadius=derived + "px";
}

ScrollBarDOM.prototype.hideArrows=function(hide){
	if (hide=="true" || hide==true) {
		this.forwardElement.style.display="none";
		this.backElement.style.display="none";
		this.scrollBar.setArrowHidden(true);
	} else  {
		this.forwardElement.style.display="block";
		this.backElement.style.display="block";
		this.scrollBar.setArrowHidden(false);
	}
	this.scrollBar.updateTrack();
}

ScrollBarDOM.prototype.repaintPositions=function(){
	//Positions the scrollbar and it's container elements
	var that = this.scrollBar;
    new Rect(that.x,that.y,that.width,that.height).writeToElement(this.scrollElement);
    var trackLength = that.trackLength;
    var trackOffset = that.trackOffset;
	new Rect(0,0,trackOffset,trackOffset).writeToElement(this.backElement);
    if(that.isHorizontal){
    	new Rect(trackOffset+trackLength,0,trackOffset,trackOffset).writeToElement(this.forwardElement);
    	var tinySquareLeft = trackLength+trackOffset*2;
		new Rect(tinySquareLeft,0,that.height,that.height).writeToElement(this.tinySquare);
    	if (that.flagArrowHidden) {
    		// trackOffset will be 0, so using height as replacement
    		new Rect(0,0,trackLength,that.height).writeToElement(this.trackElement);    		
    	} else {
    		new Rect(trackOffset,0,trackLength,trackOffset).writeToElement(this.trackElement);
    	}
    } else{
    	new Rect(0,trackOffset+trackLength,trackOffset,trackOffset).writeToElement(this.forwardElement);
		new Rect(trackLength+trackOffset*2,0,that.width,that.width).writeToElement(this.tinySquare);
    	if (that.flagArrowHidden) {
    		// trackOffset will be 0, so using width as replacement
    		new Rect(0,0,that.width,trackLength).writeToElement(this.trackElement);
    	}
    	else {
    		new Rect(0,trackOffset,trackOffset,trackLength).writeToElement(this.trackElement);
    	}
    }
}
ScrollBarDOM.prototype.repaintCanFit = function(){
    if(this.scrollBar.paneLength<this.scrollBar.clipLength){
      this.handleElement.style.display='none';
    }else{
      this.handleElement.style.display='inline';
    }
}

ScrollBarDOM.prototype.repaintHandle = function(){
	var that = this.scrollBar;
    if(that.isHorizontal){
    	if (that.flagArrowHidden) {
    		// not using height since arrows are hidden
    		new Rect(that.handleOffset,0,that.handleSize,that.height).writeToElement(this.handleElement);
    	} else {
    		new Rect(that.handleOffset+that.height,0,that.handleSize,that.height).writeToElement(this.handleElement);
    	}
    }else{
    	if (that.flagArrowHidden) {
    		// not using width since arrows are hidden
			new Rect(0,that.handleOffset,that.width,that.handleSize).writeToElement(this.handleElement);
    	} else {
			new Rect(0,that.handleOffset+that.width,that.width,that.handleSize).writeToElement(this.handleElement);
    	}
    }
}
ScrollBarDOM.prototype.repaintHandlePos = function(){
	var that = this.scrollBar;
	if(that.isHorizontal)
		this.handleElement.style.left = toPx(that.handleOffset + that.height);
	else
		this.handleElement.style.top = toPx(that.handleOffset + that.width);
}
ScrollBarDOM.prototype.repaintHandleSize = function(){
	var that = this.scrollBar;
	if(that.isHorizontal)
		this.handleElement.style.width = toPx(that.handleSize);
	else
		this.handleElement.style.height = toPx(that.handleSize);
}
ScrollBarDOM.prototype.repaint=function(){
	var that = this.scrollBar;
	if(that.flagInit){
		this.repaintCanFit();
		this.repaintPositions();
		this.repaintHandle();
	}
	else if(that.flagClipLengthChanged){
		this.repaintCanFit();
		this.repaintPositions();
		this.repaintHandle();
	}
	else if(that.flagClipTopChanged){
		this.repaintCanFit();
		this.repaintPositions();
		this.repaintHandle();
	}
	else if(that.flagPaneLengthChanged){
		this.repaintCanFit();
		this.repaintPositions();
		this.repaintHandle();
	} else if (that.flagArrowHidden) {
		this.repaintCanFit();
		this.repaintPositions();
		this.repaintHandle();
	}
	that.flagInit = false;
	that.flagClipLengthChanged = false;
	that.flagClipTopChanged = false;
	that.flagPaneLengthChanged = false;
}
ScrollBarDOM.prototype.isPointAboveOrBelowHandle=function(e){
	var that = this.scrollBar;
  var point = getMousePoint(e);
  rect = new Rect().readFromElement(this.handleElement);
  var loc=that.isHorizontal ? point.getX() : point.getY();
  var top=that.isHorizontal ? rect.getLeft() : rect.getTop();
  var bot=that.isHorizontal ? rect.getRight() : rect.getBottom();

  if(loc<top)
      return -1;
  else if(loc>bot)
      return 1;
  else
	  return 0;
}

function ScrollBar(element,isHorizontal){
	this.isHorizontal=isHorizontal ? true : false;
  	this.rangeTop=0;
  	this.rangeBottom=100;
  	this.DOM = new ScrollBarDOM(element, this);
	this.flagInit = true;
}
ScrollBar.prototype.width;
ScrollBar.prototype.height;
ScrollBar.prototype.rangeTop;
ScrollBar.prototype.rangeBottom;
ScrollBar.prototype.trackLength;
ScrollBar.prototype.trackOffset;
ScrollBar.prototype.x;
ScrollBar.prototype.y;


ScrollBar.prototype.clipLength; 	//Length of the window
ScrollBar.prototype.clipTop;		//Offset of the window
ScrollBar.prototype.paneLength; 	//Length of the world

ScrollBar.prototype.flagInit=false;
ScrollBar.prototype.flagClipLengthChanged=false;
ScrollBar.prototype.flagClipTopChanged=false;
ScrollBar.prototype.flagPaneLengthChanged=false;
ScrollBar.prototype.flagArrowHidden=false;
ScrollBar.prototype.hasBothScrolls=false;


ScrollBar.prototype.setRange=function(top,bottom){
	this.rangeTop=top;
	this.rangeBottom=bottom;
}

ScrollBar.prototype.setLocation=function(x,y,width,height){
    this.width=width;
    this.height=height;
    this.x = x;
    this.y = y;
    if(this.isHorizontal){
    	this.calculateHTrackLength();
    	this.calculateHTrackOffset();
    	this.setClipLength(this.width);
    }
    else{
    	this.calculateVTrackLength();
    	this.calculateVTrackOffset();
    	this.setClipLength(this.height);
    }
    
}

ScrollBar.prototype.updateTrack=function() {
	var that=this;
	if (this.isHorizontal) {
		this.calculateHTrackOffset();
		this.calculateHTrackLength();
	} else {
		this.calculateVTrackOffset();
		this.calculateVTrackLength();
	}
}

ScrollBar.prototype.setArrowHidden=function(hide) {
	this.flagArrowHidden=hide;
}

ScrollBar.prototype.calculateHTrackLength=function() {
	if (this.flagArrowHidden) {
		if (this.hasBothScrolls) {
			this.trackLength=this.width-this.height;
		} else {
			this.trackLength=this.width;
		}
	} else {
		if (this.hasBothScrolls) {
			if (this.height*3 < this.width)
				this.trackLength=this.width-this.height*3;
			else
				this.trackLength=this.width-this.height*2;	
			
		} else {
			this.trackLength=this.width-this.height*2;
		}
	}
}

ScrollBar.prototype.calculateHTrackOffset=function() {
	if (!this.flagArrowHidden)
		this.trackOffset=this.height;
	else
		this.trackOffset=0;
}

ScrollBar.prototype.calculateVTrackLength=function() {
	if (this.flagArrowHidden) {
		if (this.hasBothScrolls) {
			// account for tiny square
			this.trackLength=this.height-this.width;
		} else {
			// get all space
			this.trackLength=this.height;
		}
	} else {
		if (this.hasBothScrolls) {
			if (this.width*3 < this.height) {
				// two sides + tiny square
				this.trackLength=this.height-this.width*3;
			}
			else
				this.trackLength=this.height-this.width*2;
		} else {
			// two sides
			this.trackLength=this.height-this.width*2;
		}
	}
}

ScrollBar.prototype.calculateVTrackOffset=function() {
	if (!this.flagArrowHidden)
		this.trackOffset=this.width;
	else
		this.trackOffset=0;
}


ScrollBar.prototype.onTrackClicked=function(e){
  var val = this.DOM.isPointAboveOrBelowHandle(e);
  if(val != 0)
	  this.goPage(val);
}

ScrollBar.prototype.goPage=function(inc){
	this.setClipTop(this.clipTop + this.clipLength*inc);
}

ScrollBar.prototype.onDragging=function(e,x,y){
  var pos;
  if (this.flagArrowHidden) {
	  // disregard arrow size in calculation if arrows are hidden
	  if (this.isHorizontal)
		  pos = this.DOM.handleElement.offsetLeft;
	  else
		  pos = this.DOM.handleElement.offsetTop;
  } else {
	  if(this.isHorizontal)
		  pos = this.DOM.handleElement.offsetLeft - this.height;
	  else
		  pos = this.DOM.handleElement.offsetTop - this.width;
  }

  this.setClipTop(pos/this.ratio);
  if(this.onDraggingListener)
	  this.onDraggingListener(true);
  return false;
}

ScrollBar.prototype.onDraggingEnd=function(e,x,y){
  if(this.onDraggingListener)
	  this.onDraggingListener(false);
  return false;
}

ScrollBar.prototype.setPaneLength=function(paneLength){
	if(this.paneLength == paneLength)
		return;
	this.paneLength=paneLength;
	
	this.flagPaneLengthChanged = true;
	this.updateScroll();
	this.DOM.repaint();
}
ScrollBar.prototype.getPaneLength=function(){
	return this.paneLength;
}

ScrollBar.prototype.setClipTop=function(top){
	if(this.clipTop == top)
		return;
	this.clipTop=top;
		  
	this.flagClipTopChanged=true;
	this.updateScroll();
	this.DOM.repaint();
  
}

ScrollBar.prototype.setClipTopNoFire=function(top){
	if(this.clipTop == top)
		return;
	this.clipTop=top;
		  
	this.flagClipTopChanged=true;
	this.updateScrollNoFire();
	this.DOM.repaint();
  
}


ScrollBar.prototype.setClipLength=function(length){
  if(isNaN(length))
	  length=0;
  this.clipLength=length;
  
  this.flagClipLengthChanged=true;
  this.updateScroll();
  this.DOM.repaint();
}

ScrollBar.prototype.getClipLength=function(){
  return this.clipLength;
}


ScrollBar.prototype.updateScroll=function(){
	if(this.clipTop+this.clipLength>this.paneLength)
		this.clipTop=this.paneLength-this.clipLength;
	if(this.clipTop<0)
		this.clipTop = 0;
	
	// below displays the h/v scroll bar
	if(this.paneLength != null && this.clipLength != null){
		// panel length is the total length of the window content (visible + invisible)
		var ratio = this.trackLength / this.paneLength;
		// clip Length should be the total length of the scroll container
		var handleSize=this.clipLength * ratio;
		var minLen = 45;
		if (handleSize < minLen) {
			var diff = minLen - handleSize;
			handleSize = minLen;
			ratio = (this.trackLength - diff) / this.paneLength;
		} else if (handleSize < this.trackOffset){
			ratio = (this.trackLength - this.trackOffset + handleSize) / this.paneLength;
			handleSize = this.trackOffset;
		}
		this.ratio = ratio;
		this.handleSize = handleSize;
	}
	
	// below controls scroll bar movement when you scroll to invisible rows
	if(this.clipTop!=null && this.ratio!= null){
		this.handleOffset = this.clipTop * this.ratio;
	}
	
  if(this.onscroll)
    this.onscroll(this);
}

ScrollBar.prototype.updateScrollNoFire=function(){
	if(this.clipTop+this.clipLength>this.paneLength)
		this.clipTop=this.paneLength-this.clipLength;
	if(this.clipTop<0)
		this.clipTop = 0;
	
	// below displays the h/v scroll bar
	if(this.paneLength != null && this.clipLength != null){
		// panel length is the total length of the window content (visible + invisible)
		var ratio = this.trackLength / this.paneLength;
		// clip Length should be the total length of the scroll container
		var handleSize=this.clipLength * ratio;
		var minLen = 45;
		if (handleSize < minLen) {
			var diff = minLen - handleSize;
			handleSize = minLen;
			ratio = (this.trackLength - diff) / this.paneLength;
		} else if (handleSize < this.trackOffset){
			ratio = (this.trackLength - this.trackOffset + handleSize) / this.paneLength;
			handleSize = this.trackOffset;
		}
		this.ratio = ratio;
		this.handleSize = handleSize;
	}
	
	// below controls scroll bar movement when you scroll to invisible rows
	if(this.clipTop!=null && this.ratio!= null){
		this.handleOffset = this.clipTop * this.ratio;
	}
}



ScrollBar.prototype.getClipTop=function(){
  return this.clipTop;
}

ScrollBar.prototype.setHasBothScrolls=function(hbs){
  this.hasBothScrolls=hbs;
}

function ScrollPaneDOM(element, innerPane, scrollPane){
	this.scrollPane = scrollPane; 
	var that = this.scrollPane;
	this.scrollPaneElement=element;
	this.hscrollElement=nw("div");
	this.vscrollElement=nw("div");
	this.paneElement=nw("div");
	this.paneElement.classList.add('scrollpane');
	if(innerPane){
		this.innerpaneElement=innerPane;
	}else{
		this.innerpaneElement=nw("div");
		this.innerpaneElement.classList.add('scrollpane_inner');
	}
	this.scrollPaneElement.classList.add('scrollpane_container');
	this.scrollPaneElement.appendChild(this.paneElement);
	this.paneElement.appendChild(this.innerpaneElement);
	//  this.scrollPaneElement.appendChild(this.paneElement);
	this.scrollPaneElement.appendChild(this.paneElement);
	
	this.innerpaneElement.onMouseWheel=function(e,delta){that.onMouseWheel(e,delta);};
	
  if(that.isAutohide){
    this.vscrollElement.onmouseenter=function(e){that.onMouseEntered(e);};
    this.vscrollElement.onmouseleave=function(e){that.onMouseExited(e);};
    this.hscrollElement.onmouseenter=function(e){that.onMouseEntered(e);};
    this.hscrollElement.onmouseleave=function(e){that.onMouseExited(e);};
    this.vscrollElement.style.opacity=0;
    this.hscrollElement.style.opacity=0;
  }
}
function ScrollPane(element,scrollSize,innerPane){
  var that=this;
  this.width=0;
  this.height=0;
  this.isAutohide=false;
  this.vscrollVisible=false;
  this.hscrollVisible=false;
  this.ticks=[];
  
  this.isvscrollDefault = true;
  this.scrollSize=scrollSize;
  if(innerPane){
	  this.autoMove=false;
  }else{
	this.autoMove=true;
  }
  this.onTickClickFunc=function(e){e.stopPropagation();return that.onTickClick(getMouseTarget(e).id,getMouseTarget(e).pos);};
  this.onMouseWheelFunc=function(e,delta){return that.onMouseWheel(e,delta);};
  
  this.DOM=new ScrollPaneDOM(element, innerPane, this);
  this.vscroll=new ScrollBar(this.DOM.vscrollElement,false);
  this.hscroll=new ScrollBar(this.DOM.hscrollElement,true);
  this.hscroll.clipTop = 0;
  this.vscroll.clipTop = 0;
  this.hscroll.onscroll=function(){that.onScrollbarsScroll()};
  this.vscroll.onscroll=function(){that.onScrollbarsScroll()};
  
  /*This never gets called?*/
  if(this.isAutohide){
    this.vscroll.onDraggingListener=function(isDragging){that.onDragging(isDragging);}
    this.hscroll.onDraggingListener=function(isDragging){that.onDragging(isDragging);}
    this.isScrollDragging=false;
    this.isMouseOver=false;
  }
  
  this.hscrollClipTop = this.hscroll.getClipTop();
  this.vscrollClipTop = this.vscroll.getClipTop();
  this.hscrollChanged = 0.0;
  this.vscrollChanged = 0.0;
}


ScrollPane.prototype.hscrollClipTop;
ScrollPane.prototype.vscrollClipTop;
ScrollPane.prototype.hscrollChanged;
ScrollPane.prototype.vscrollChanged;

ScrollPane.prototype.flagLocationPosChanged = false;
ScrollPane.prototype.flagLocationSizeChanged = false;
ScrollPane.prototype.flagPaneSizeChanged = false;
ScrollPane.prototype.flagPaneElementSizeChanged = false;

ScrollPane.prototype.onMouseEntered=function(e){
	this.isMouseOver=true;
	this.DOM.vscrollElement.style.opacity=1;
	this.DOM.hscrollElement.style.opacity=1;
}
ScrollPane.prototype.onMouseExited=function(e){
	this.isMouseOver=false;
	if(this.isScrollDragging)
		return;
	this.DOM.vscrollElement.style.opacity=0;
	this.DOM.hscrollElement.style.opacity=0;
}
ScrollPane.prototype.onDragging=function(e){
	this.isScrollDragging=e;
	if(!this.isScrollDragging && !this.isMouseOver){
	  this.DOM.vscrollElement.style.opacity=0;
	  this.DOM.hscrollElement.style.opacity=0;
	}
}


ScrollPane.prototype.setSize=function(size){
	// Scrollbarsize;
	this.scrollSize=size;
	this.updateScroll();
}

ScrollPane.prototype.handleWheel=function(e){
	var den=e.shiftKey ? 1 : 10;
	var delta;
	if(e.deltaMode == WheelEvent.DOM_DELTA_PIXEL)
		delta = e.deltaY/-100;
	else
		delta = e.deltaY/-1;

	//Decide which scroll direction to scroll
	if(e.target == this.vscroll.DOM.trackElement || e.target==this.vscroll.DOM.gripElement)
	    this.vscroll.goPage(-delta/den);
	else if(e.altKey || e.target == this.hscroll.DOM.trackElement || e.target==this.hscroll.DOM.gripElement)
	    this.hscroll.goPage(-delta/den);
    else{
    	//Default scroll vertical
    	if(this.isvscrollDefault)
    		this.vscroll.goPage(-delta/den);
    	else
    		this.hscroll.goPage(-delta/den);
    }
}
ScrollPane.prototype.onMouseWheel=function(e,delta){
	if(e.target.className!='menu_item_text')
	return;
	var den=e.shiftKey ? 1 : 10;
	if(e.altKey){
	    this.hscroll.goPage(-delta/den);
    }else{
	    this.vscroll.goPage(-delta/den);
    }
}
ScrollPane.prototype.onTickClick=function(id,pos){
	this.setClipTop(pos);
}
ScrollPane.prototype.lastRun=0;
ScrollPane.prototype.onScrollbarsScroll=function(){
    if(this.timeout==null){
    	var delay=this.lastRun+40-Date.now();//40=25 FPS
	    if(delay<=0){
            this.onScrollbarsScroll2();
	    }else{
            var that=this;
            this.timeout=setTimeout(function(){that.timeout=null;that.onScrollbarsScroll2();},delay);
	    }
  }
}
ScrollPane.prototype.onScrollbarsScroll2=function(){
	this.lastRun=Date.now();
	//called alot
  if(this.hscrollClipTop != this.hscroll.getClipTop()){
	  this.hscrollChanged = this.hscroll.getClipTop() - this.hscrollClipTop;
	  this.hscrollClipTop = this.hscroll.getClipTop();
  }
  if(this.vscrollClipTop != this.vscroll.getClipTop()){
	  this.vscrollChanged = this.vscroll.getClipTop() - this.vscrollClipTop;
	  this.vscrollClipTop = this.vscroll.getClipTop();
  }
  if(!this.hscrollChanged && !this.vscrollChanged)
	  return;
  
  if(this.autoMove){
    this.DOM.innerpaneElement.style.left=-1*(this.hscroll.getClipTop());
    this.DOM.innerpaneElement.style.top=-1*(this.vscroll.getClipTop());
  }
  if(this.linkedPane){
    this.linkedPane.setClipTop(this.getClipTop());
  }
  if(this.onScroll)
	  this.onScroll();
  this.hscrollChanged = 0.0;
  this.vscrollChanged = 0.0;
}

ScrollPane.prototype.getClipWidth=function(){
	return this.hscroll.getClipLength();
}
ScrollPane.prototype.getClipHeight=function(){
	return this.vscroll.getClipLength();
}

ScrollPane.prototype.setLocation=function(x,y,width,height){
	if(this.x != x || this.y != y)
		this.flagLocationPosChanged = true;
	if(this.width!= width|| this.height!= height)
		this.flagLocationSizeChanged = true;
		
  this.width=width;
  this.height=height;
  this.x=x;
  this.y=y;
  this.updateScroll();
}
ScrollPane.prototype.setPaneSize=function(paneWidth,paneHeight){
  this.paneWidth=paneWidth;
  this.paneHeight=paneHeight;
  this.flagPaneSizeChanged = true;
  this.updateScroll();
}

ScrollPane.prototype.ensureVisible=function(rect){
	var rectScrollPane = this.DOM.paneElement.getBoundingClientRect();
	if(rect.left < rectScrollPane.left){
		this.setClipLeft(this.getClipLeft() - (rectScrollPane.left - rect.left));
	}
	else if(rect.right > rectScrollPane.right){
		this.setClipLeft(this.getClipLeft() - (rectScrollPane.right - rect.right));
	}
	if(rect.top < rectScrollPane.top){
		this.setClipTop(this.getClipTop() - (rectScrollPane.top - rect.top));
	}
	else if(rect.bottom > rectScrollPane.bottom){
		this.setClipTop(this.getClipTop() - (rectScrollPane.bottom - rect.bottom));
	}
}


ScrollPane.prototype.updateScroll=function(){

  var hvis=true;
  var vvis=true;
  if(this.paneWidth<=this.width && this.paneHeight <= this.height){
	  hvis=false;
	  vvis=false;
  }else if(this.paneWidth<=this.width - this.scrollSize){
	  hvis=false;
  }else if(this.paneHeight<=this.height - this.scrollSize){
	  vvis=false;
  }
  
  var hSize=hvis ? this.scrollSize : 0;
  var vSize=vvis ? this.scrollSize : 0; 
  if(hvis!=this.hscrollVisible){
	 if(hvis)
       this.DOM.scrollPaneElement.appendChild(this.DOM.hscrollElement);
	 else
       this.DOM.scrollPaneElement.removeChild(this.DOM.hscrollElement);
	 this.hscrollVisible=hvis;
  }
  if(vvis!=this.vscrollVisible){
	 if(vvis)
       this.DOM.scrollPaneElement.appendChild(this.DOM.vscrollElement);
	 else
       this.DOM.scrollPaneElement.removeChild(this.DOM.vscrollElement);
	 this.vscrollVisible=vvis;
  }
  if (this.hscrollVisible==true && this.vscrollVisible==true) {
	  this.vscroll.setHasBothScrolls(true);
	  this.hscroll.setHasBothScrolls(true);
	  // ensure last column is fully visible, taking into account vertical scrollbar width
	  this.hscroll.setPaneLength(this.paneWidth+hSize);
	  // ensure last row is fully visible
	  this.vscroll.setPaneLength(this.paneHeight+vSize);
  } else {
	  this.vscroll.setHasBothScrolls(false);
	  this.hscroll.setHasBothScrolls(false);
	  this.hscroll.setPaneLength(this.paneWidth);
	  this.vscroll.setPaneLength(this.paneHeight);
  }
  new Rect(this.x,this.y,this.width,this.height).writeToElement(this.DOM.scrollPaneElement);
  if (this.hscroll.flagArrowHidden==true && this.vscrollVisible==false) {
	  this.hscroll.setClipLength(this.width);
  } else {
	  //account for tiny square
	  this.hscroll.setClipLength(this.width-vSize);
  }
  if(this.isAutohide)
	    this.vscroll.setClipLength(this.height);
	  else
	    this.vscroll.setClipLength(this.height-hSize);
  this.hscroll.setLocation(0,this.height-this.scrollSize,this.width,this.scrollSize);
  this.vscroll.setLocation(this.width-this.scrollSize,0,this.scrollSize,this.height);	  
  
  if(this.isAutohide)
    new Rect(0,0,this.width,this.height).writeToElement(this.DOM.paneElement);
  else
    new Rect(0,0,this.width-vSize,this.height-hSize).writeToElement(this.DOM.paneElement);
  this.hscroll.updateScroll();
  this.vscroll.updateScroll();
  
}

ScrollPane.prototype.setPane=function(element){
  this.DOM.innerpaneElement.appendChild(element);
}

ScrollPane.prototype.getClipTop=function(){
  return this.vscroll.getClipTop();
}

ScrollPane.prototype.getClipLeft=function(){
  return this.hscroll.getClipTop();
}

ScrollPane.prototype.setClipTop=function(t){
  return this.vscroll.setClipTop(t);
}

//add setClipTopNoFire(), which does not fire onScroll() cb
ScrollPane.prototype.setClipTopNoFire=function(t){
	  return this.vscroll.setClipTopNoFire(t);
	}

ScrollPane.prototype.linkToScrollBar=function(t){
	this.linkedPane=t;
}

ScrollPane.prototype.setClipLeft=function(t){
  return this.hscroll.setClipTop(t);
}

ScrollPane.prototype.clearTicks=function(){
	for(var i in this.ticks)
		this.vscroll.DOM.trackElement.removeChild(this.ticks[i]);
	this.ticks=[];
}

ScrollPane.prototype.addTick=function(id,left,top,width,height,style){
	top+=this.scrollSize;
	var div=nw('div','scrollbar_tick');
	div.style.left=toPx(left);
	div.style.top=toPx(top);
	div.style.width=toPx(width);
	div.style.height=toPx(height);
	div.style.backgroundColor=style;
	div.id=id;
	div.pos=top;
	div.onclick=this.onTickClickFunc;
	this.vscroll.DOM.trackElement.appendChild(div);
	this.ticks[this.ticks.length]=div;
}
ScrollPane.prototype.updateIconsColor=function(scrollIconsColor){
	if (scrollIconsColor == null)
		return;
	vScrollGripStyle=scrollIconsColor+';stroke-width:14;stroke-miterlimit:10;" x1="12" y1="24.1" x2="88" y2="24.1"/> <line style="fill:none;stroke:'+scrollIconsColor+';stroke-width:14;stroke-miterlimit:10;" x1="12" y1="50" x2="88" y2="50"/> <line style="fill:none;stroke:'+scrollIconsColor;
	hScrollGripStyle=scrollIconsColor+';stroke-width:14;stroke-miterlimit:10;" x1="76.1" y1="13.5" x2="76.1" y2="89.5"/> <line style="fill:none;stroke:'+scrollIconsColor+';stroke-width:14;stroke-miterlimit:10;" x1="50.2" y1="13.5" x2="50.2" y2="89.5"/> <line style="fill:none;stroke:'+scrollIconsColor;
	scrollUpStyle=scrollIconsColor;
	scrollDownStyle=scrollIconsColor;
	scrollLeftStyle=scrollIconsColor;
	scrollRightStyle=scrollIconsColor;
  	vScrollGripStyle=vScrollGripStyle.replace(/#/g, "%23");
  	hScrollGripStyle=hScrollGripStyle.replace(/#/g, "%23");
  	scrollUpStyle=scrollUpStyle.replace(/#/g, "%23");
  	scrollDownStyle=scrollDownStyle.replace(/#/g, "%23");
  	scrollLeftStyle=scrollLeftStyle.replace(/#/g, "%23");
  	scrollRightStyle=scrollRightStyle.replace(/#/g, "%23");
	this.vscroll.DOM.gripElement.style.backgroundImage=(SCROLLBAR_GRIP_V_IMAGE_PREFIX+vScrollGripStyle+SCROLLBAR_GRIP_V_IMAGE_SUFFIX);
	this.hscroll.DOM.gripElement.style.backgroundImage=(SCROLLBAR_GRIP_H_IMAGE_PREFIX+hScrollGripStyle+SCROLLBAR_GRIP_H_IMAGE_SUFFIX);
	this.vscroll.DOM.backElement.style.backgroundImage=(SCROLLBAR_UP_IMAGE_PREFIX+scrollUpStyle+SCROLLBAR_UP_IMAGE_SUFFIX);
	this.vscroll.DOM.forwardElement.style.backgroundImage=(SCROLLBAR_DOWN_IMAGE_PREFIX+scrollDownStyle+SCROLLBAR_DOWN_IMAGE_SUFFIX);
	this.hscroll.DOM.backElement.style.backgroundImage=(SCROLLBAR_LEFT_IMAGE_PREFIX+scrollLeftStyle+SCROLLBAR_LEFT_IMAGE_SUFFIX);
	this.hscroll.DOM.forwardElement.style.backgroundImage=(SCROLLBAR_RIGHT_IMAGE_PREFIX+scrollRightStyle+SCROLLBAR_RIGHT_IMAGE_SUFFIX);
}
