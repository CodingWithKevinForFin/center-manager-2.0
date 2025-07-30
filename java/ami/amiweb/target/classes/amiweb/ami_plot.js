


function AmiPlotPortlet(portletId,parentId){
  this.portlet=new Portlet(this,portletId,null);
  this.isContainer=false;
  var that=this;
  this.divElement.style.background='#FFFFFF';
  this.clickLayer=nw('canvas');
  this.clickLayer.style.left='0px';
  this.clickLayer.style.top='0px';
  this.clickLayer.style.right='0px';
  this.clickLayer.style.bottom='0px';
  this.clickLayer.style.position='absolute';
  this.clickLayerContext=this.clickLayer.getContext('2d');
  
  
  //MOBILE SCROLLING
  this.clickLayer.ontouchstart=function(e){that.onTouchstart(e)};
  this.clickLayer.ontouchend=function(e){that.onTouchend(e)};
  this.clickLayer.ontouchmove=function(e){that.onTouchmove(e)};
  
  this.clickLayer.onmousedown=function(e){that.onMousedown(e)};
  this.clickLayer.onmouseup=function(e){that.onMouseup(e)};
  this.clickLayer.onmousemove=function(e){that.onMousemove(e)};
  this.clickLayer.onmouseout=function(e){that.onMouseout(e)};
//  this.clickLayer.ondblclick=function(e){that.onDblclick(e)};
  this.clickLayer.addEventListener('touchend', function(e) {
	    that.onDblTap(e);
	});
  this.clickLayer.tabIndex=1000;
  this.context = this.clickLayer.getContext('2d');
  this.context.translate(.5,.5);
  this.canvas=this.clickLayer;
  
  this.zoomMoveConsumedFlag=true;
  this.zoomMoveXPending=0;
  this.zoomMoveYPending=0;
  this.gridCanvas=nw('canvas');
  this.gridCanvas.style.position='absolute';
  this.gridContext = this.gridCanvas.getContext('2d');
  this.gridContext.translate(.5,.5);
  this.divElement.appendChild(this.gridCanvas);
  this.divElement.appendChild(this.clickLayer);
  this.pgid=sessionStorage.getItem('<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID"/>',null);
} 

AmiPlotPortlet.prototype.onMousedown=function(e){
  var that=this;
  var doc=getDocument(this.divElement);
  if(doc.onmouseup==null){
	this.trackingMouseDrag=true;
    doc.onmouseup=function(e){that.onMouseup(e)};
    doc.onmousemove=function(e){that.onMousemove(e)};
    this.clickLayer.onmousemove=null;
  }
  var button=getMouseButton(e);
  var p=getMouseLayerPoint(e);
  if(button==1 && !e.ctrlKey && !e.shiftKey && this.isZoom){
	  this.startPos=p;
	  this.currentPos=p;
	  this.movedPos=false;
	  return;
  }
  var x=p.x;
  var y=p.y;
  this.shiftStart=[x,y];
}

AmiPlotPortlet.prototype.onMouseup=function(e){
  var doc=getDocument(this.divElement);
  if(this.trackingMouseDrag){
	this.trackingMouseDrag=false;
    doc.onmouseup=null;
    doc.onmousemove=null;
    var that=this;
    this.clickLayer.onmousemove=function(e){that.onMousemove(e)};
  }
  this.timer=null;
  var button=getMouseButton(e);
  if(this.movedPos){
	  this.currentPos=null;
	  this.startPos=null;
	  this.movedPos=false;
	  return;
  }else{
	  this.currentPos=null;
	  this.startPos=null;
	  this.movedPos=false;
  }
  var p=getMousePointRelativeTo(e,this.canvas);
  var t=getMouseTarget(e);
  var x=p.x;
  var y=p.y;
  if(this.shiftStart!=null){
    var x2=this.shiftStart[0];
    var y2=this.shiftStart[1];
    var w=x2-x;
    var h=y2-y;
    if(Math.abs(w)>2 && Math.abs(h)>2){
      this.callBack('select',{x:x,y:y,w:w,h:h,isCtrl:e.ctrlKey,isShift:e.shiftKey});
      return;
    }
  }
  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);
  this.callBack('onMouse',{x:x,y:y,b:button,c:e.ctrlKey,s:e.shiftKey});
  return;
}

//MOBILE SCROLLING ON TOUCH DOWN
AmiPlotPortlet.prototype.onTouchstart=function(e){
	  var that=this;
	  // 500ms delay for touch hold context menu (zooming out)
	  // if moved before 500ms, dont show menu
	  this.longTouchTimer = setTimeout(() => {
			if(this.isZoom){
				this.callBack('onMouse',{x:x,y:y,b:2,c:e.ctrlKey,s:e.shiftKey});
				return;
			}
	  }, 500);
	  // dragging zoomed in chart
	  var doc=getDocument(this.divElement);
	  if(doc.onmouseup==null){
		this.trackingMouseDrag=true;
	    doc.onmouseup=function(e){that.onMouseup(e)};
	    doc.onmousemove=function(e){that.onMousemove(e)};
	  }
	  var button=getMouseButton(e);
	  var p=getMouseLayerPoint(e);
	  if(button==1 && !e.ctrlKey && !e.shiftKey && this.isZoom){
		  this.startPos=p;
		  this.currentPos=p;
		  this.movedPos=false;
		  return;
	  }
	  var x=p.x;
	  var y=p.y;
	  this.shiftStart=[x,y];
	}

//MOBILE SCROLLING ON TOUCH UP
AmiPlotPortlet.prototype.onTouchend=function(e){
	//clear touch timer for menu.
	clearTimeout(this.longTouchTimer);
	this.longTouchTimer=null;
	
	//borrowing code from on mouse up
	  var doc=getDocument(this.divElement);
	  if(this.trackingMouseDrag){
		this.trackingMouseDrag=false;
	    doc.onmouseup=null;
	    doc.onmousemove=null;
	    var that=this;
	    this.clickLayer.onmousemove=function(e){that.onMousemove(e)};
	  }
	  this.timer=null;
	  var button=getMouseButton(e);
	  if(this.movedPos){
		  this.currentPos=null;
		  this.startPos=null;
		  this.movedPos=false;
		  return;
	  }else{
		  this.currentPos=null;
		  this.startPos=null;
		  this.movedPos=false;
	  }
	  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);
	  return;
	}

//MOBILE SCROLLING ON TOUCH MOVE
AmiPlotPortlet.prototype.onTouchmove=function(e){
	//clear touch timer for menu.
	clearTimeout(this.longTouchTimer);
	this.longTouchTimer=null;
	
	//borrowing code from mouse move
	  var p=getMousePointRelativeTo(e,this.canvas);
	  if(this.lastMoveEvent!=null){
		  p2=getMousePointRelativeTo(this.lastMoveEvent,this.canvas);
		  if(p.x==p2.x && p.y==p2.y)
			  return;
	  }
	  var button=getMouseButton(e);
		  if(this.currentPos!=null){
			var dx=p.x-this.currentPos.x;
			var dy=p.y-this.currentPos.y;
			if(dx!=0 || dy!=0){
				if(this.onMouseDragged)
					this.onMouseDragged(e,dx,dy);
				this.currentPos=p;
				if(!this.movedPos && abs(p.x-this.startPos.x)>1 || abs(p.y-this.startPos.y)>1)
					this.movedPos=true;
			}
		  return;
	  }
	  this.lastMoveEvent=e;
	  if(this.timer!=null){
		  clearTimeout(this.timer);
	  }	  
	  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);
	}
AmiPlotPortlet.prototype.onMousemove=function(e){
  var p=getMousePointRelativeTo(e,this.canvas);
  if(this.lastMoveEvent!=null){
	  p2=getMousePointRelativeTo(this.lastMoveEvent,this.canvas);
	  if(p.x==p2.x && p.y==p2.y)
		  return;
  }
  var button=getMouseButton(e);
	  if(this.currentPos!=null){
		var dx=p.x-this.currentPos.x;
		var dy=p.y-this.currentPos.y;
		if(dx!=0 || dy!=0){
			if(this.onMouseDragged)
				this.onMouseDragged(e,dx,dy);
			this.currentPos=p;
			if(!this.movedPos && abs(p.x-this.startPos.x)>1 || abs(p.y-this.startPos.y)>1)
				this.movedPos=true;
		}
	  return;
  }
  this.lastMoveEvent=e;
  if(this.timer!=null){
	  clearTimeout(this.timer);
  }
  var that=this;
  this.timer=setTimeout(function(){that.onMouseStill(e);}, 100);
  if(button==0)
	  return;
  
  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);
  var x=p.x;
  var y=p.y;
  if(this.shiftStart!=null){
	var t=this.shiftStart;
    this.context.lineWidth='1';
    this.context.beginPath();
    this.context.strokeStyle=this.selectionBoxBorderColor;//'rgba(64,64,64,.9)';
    this.context.fillStyle=this.selectionBoxFillColor;//'rgba(64,64,64,.9)';
    var xPos=t[0]+.5,yPos=t[1]+.5,wPos=x-t[0],hPos=y-t[1];
	this.context.fillRect(xPos,yPos,wPos,hPos);
	this.context.strokeRect(xPos,yPos,wPos,hPos);
    this.context.stroke();
  }
}
AmiPlotPortlet.prototype.onMouseDragged=function(e,dx,dy){
  this.zoomMoveXPending+=dx;
  this.zoomMoveYPending+=dy;
  if(this.zoomMoveConsumedFlag && (this.zoomMoveXPending!=0 || this.zoomMoveYPending!=0)){
    this.callBack('zoomMove',{dx:this.zoomMoveXPending,dy:this.zoomMoveYPending});
    this.zoomMoveConsumedFlag=false;
    this.zoomMoveXPending=0;
    this.zoomMoveYPending=0;
  }
}
AmiPlotPortlet.prototype.zoomMoveConsumed=function(){
	this.zoomMoveConsumedFlag=true;
  if(this.zoomMoveXPending!=0 || this.zoomMoveYPending!=0){
    this.callBack('zoomMove',{dx:this.zoomMoveXPending,dy:this.zoomMoveYPending});
    this.zoomMoveConsumedFlag=false;
    this.zoomMoveXPending=0;
    this.zoomMoveYPending=0;
  }
}

AmiPlotPortlet.prototype.handleKeydown=function(e){
	if(e.key==" "){
		    this.setZoom(this.canvas.width/2,this.canvas.height/2,-100);
	}else if(e.ctrlKey){
	  var distance=e.shiftKey ? 3 : 1;
	  if(e.key=="ArrowUp" || e.key=="ArrowRight")
		    this.setZoom(this.canvas.width/2,this.canvas.height/2,distance);
	  if(e.key=="ArrowDown"|| e.key=="ArrowLeft")
		    this.setZoom(this.canvas.width/2,this.canvas.height/2,-distance);
	}else{
	  var distance=e.shiftKey ? 100 : 10;
	  if(e.key=="ArrowLeft")
		  this.onMouseDragged(e,distance,0);
	  else if(e.key=="ArrowRight")
		  this.onMouseDragged(e,-distance,0);
	  else if(e.key=="ArrowUp")
		    this.onMouseDragged(e,0,distance);
	  else if(e.key=="ArrowDown")
		  this.onMouseDragged(e,0,-distance);
	}
//	if(e.key=="Control")
//		this.canvas.style.cursor="move";
}
AmiPlotPortlet.prototype.handleKeyup=function(e){
//	if(e.key=="Control")
//		this.canvas.style.cursor="default";
}
AmiPlotPortlet.prototype.onMouseout=function(e){
  if(this.timer!=null)
	  clearTimeout(this.timer);
  this.canvas.style.cursor="default";
  this.fireOnHover(0,0);
  this.clearHover();
}
AmiPlotPortlet.prototype.handleWheel=function(e){
  if(e.target == this.canvas) { // scroll only when mouse is on the plot, not on legend.
	  var p=getMousePointRelativeTo(e,this.canvas);
	  var delta;
	  if(e.deltaMode == WheelEvent.DOM_DELTA_PIXEL)
		  delta = e.deltaY/-100;
	  else
		  delta = e.deltaY/-1
	  this.setZoom(p.x,p.y,delta);
  }
}
AmiPlotPortlet.prototype.onDblTap = (function() {
    let lastTapTime = 0;
    const doubleTapDelay = 200; // Maximum time (ms) between taps for it to be considered a double tap

    return function(e) {
        const currentTime = new Date().getTime();
        const timeSinceLastTap = currentTime - lastTapTime;

        if (timeSinceLastTap < doubleTapDelay && timeSinceLastTap > 0) {
          var p=getMousePointRelativeTo(e,this.canvas);
          this.setZoom(p.x,p.y,5);
        }

        lastTapTime = currentTime;
    };
})();
AmiPlotPortlet.prototype.onMouseStill=function(e){
    var p=getMouseLayerPoint(this.lastMoveEvent);
    if(this.shiftStart!=null)
    	return;
   this.fireOnHover(p.x,p.y);
}
AmiPlotPortlet.prototype.fireOnHover=function(x,y){
  if(x==this.hoverRequestX && y==this.hoverRequestY)
	  return;
    this.hoverRequestX=x;
    this.hoverRequestY=y;
    this.callBack('hover',{x:x,y:y});
}




AmiPlotPortlet.prototype.setZoom=function(x,y,delta){
  this.callBack('zoom',{x:x,y:y,delta:delta});
  this.clearHover();
}



AmiPlotPortlet.prototype.setHover=function(x,y,sel,name,xAlign,yAlign){
	if(x==0 && y==0)
		return;
  if(this.hoverRequest==sel){
	const rootBody = getRootNodeBody(this.divElement);
	if(this.tooltipDiv!=null)
	  rootBody.removeChild(this.tooltipDiv);
    this.tooltipDiv=nw("div","ami_chart_tooltip");
	var div=this.tooltipDiv;
	this.hoverX=MOUSE_POSITION_X;
	this.hoverY=MOUSE_POSITION_Y;
	div.innerHTML=name;
	if(div.firstChild!=null && div.firstChild.tagName=='DIV'){
		this.tooltipDiv=div.firstChild;
	    div=this.tooltipDiv;
	}
	
	rootBody.appendChild(div);
	var origin = this.divElement.getBoundingClientRect();
	var rect=new Rect().readFromElement(div);
	var h=rect.height+4;
	var w=rect.width+6;
	div.style.width=toPx(w);
	div.style.height=toPx(h);
	switch(xAlign){
	  case ALIGN_LEFT: div.style.left=toPx(origin.x + x); break;
	  case ALIGN_RIGHT: div.style.left=toPx(origin.x + x-w); break;
	  default: div.style.left=toPx(origin.x + x-w/2); break;
	}
	switch(yAlign){
	  case ALIGN_TOP: div.style.top=toPx(origin.y + y); break;
	  case ALIGN_BOTTOM: div.style.top=toPx(origin.y + y-h); break;
	  default: div.style.top=toPx(origin.y + y-h/2); break;
	}
	ensureInDiv(div,rootBody);
  }
}
AmiPlotPortlet.prototype.clearHover=function(){
  if(this.tooltipDiv!=null){
	  try {
		getDocument(this.divElement).body.removeChild(this.tooltipDiv);
	  } catch (e) {
		console.log("unable to clear chart hover: " + e);
	  }
    this.tooltipDiv=null;
  }
  this.hoverRequest=null;
}


AmiPlotPortlet.prototype.clearSelectRegion=function(options){
	if(this.shiftStart==null)
		return;
	this.shiftStart=null;
    this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);
}


AmiPlotPortlet.prototype.init=function(options){
  this.bgColor=options.bgColor;
  this.selectionBoxBorderColor=options.selBoxBorderColor;///'rgba(64,64,64,.7)';//options.selectionBoxBorderColor;
  this.selectionBoxFillColor=options.selBoxFillColor;//'rgba(64,64,64,.1)';//options.selectionBoxBorderColor;
//  this.selectionBoxFillColor=options.selectionBoxFillColor;
  
  this.divElement.style.background=this.bgColor;
  this.clearPlot();
  this.clearLayers();
}

AmiPlotPortlet.prototype.setSize=function(width,height){
	this.gridCanvas.style.width=toPx(width);
	this.gridCanvas.style.height=toPx(height);
	this.gridCanvas.width=width;
	this.gridCanvas.height=height;
    this.width=width;
    this.height=height;
	this.divElement.style.width=toPx(width);
	this.divElement.style.height=toPx(height);
	this.clickLayer.style.width=toPx(width);
	this.clickLayer.style.height=toPx(height);
	this.clickLayer.width=width;
	this.clickLayer.height=height;
}
AmiPlotPortlet.prototype.clearPlot=function(){
  this.isZoom=false;
  this.clickLayer.backgroundImage=null;
  this.clickLayer.backgroundPosition='0px 0px';
  this.clickLayer.backgroundSize=null;
  this.clickLayer.backgroundRepeat='no-repeat';
  this.gridCanvas.backgroundImage=null;
  this.gridCanvas.backgroundPosition='0px 0px';
  this.gridCanvas.backgroundSize=null;
  this.gridCanvas.backgroundRepeat='no-repeat';
}

AmiPlotPortlet.prototype.showWait=function(){
    if(this.waitPaneDiv==null){
       this.waitPaneDiv=nw("div","wait_plot");
       this.waitPaneDiv.waiting=true;
       this.divElement.appendChild(this.waitPaneDiv);
    }
	this.waitPaneDiv.style.display="inline";
};

AmiPlotPortlet.prototype.hideWait=function(){
	if(this.waitPaneDiv && this.waitPaneDiv.waiting)
		this.waitPaneDiv.style.display="none";
}



AmiPlotPortlet.prototype.setImage=function(layerPos,imageId,w,h,x,y,zx,zy){
	if(layerPos!=-1)
	  this.isZoom=zy>1 || zx>1;
	var layer=layerPos==-1 ? this.clickLayer :  this.gridCanvas;
	if(layerPos == 0){
		this.showWait();
	}
	if(layer.img!=null){
		layer.img.onload=null;
	}
	var that=layer;
	var plot = this;
	if(imageId==null){
        that.style.backgroundImage=null;
        that.style.backgroundPosition='0px 0px';
        that.backgroundSize=null;
        that.style.backgroundRepeat='no-repeat';
		that.img=null;
		return;
	}else{
		layer.img=nw('img');
		layer.img.src="<f1:out value="com.f1.ami.web.pages.AmiWebPages.URL_DYNAMIC_IMAGE"/>?portletId="+imageId+"&now=+"+Date.now()+"&<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID"/>="+this.pgid;
		layer.img.data={w:w,h:h,x:x,y:y,zx:zx,zy:zy};
		layer.img.onload=function(){
			that.style.backgroundImage="url("+that.img.src+")";
			that.style.backgroundPosition='0px 0px';
			that.style.backgroundSize=null;
			that.style.backgroundRepeat='no-repeat';
			that.currentImg=that.img;
			that.img.onload=null;
			that.img=null;
			plot.hideWait();
		}
	}
	
	if(that.currentImg!=null){
	  var od=that.currentImg.data;
	  var xShift=x-od.x*zx/od.zx;
	  var yShift=y-od.y*zy/od.zy;
	  var wShift=(zx / od.zx)* od.w;
	  var hShift=(zy / od.zy) * od.h;
	  layer.style.backgroundPosition=xShift+'px '+yShift+'px';
	  layer.style.backgroundSize=wShift +'px '+hShift+'px';
	}
}


AmiPlotPortlet.prototype.clearLayers=function(options){
	for(var i in this.layers){
		var layer=this.layers[i];
		if(layer!=null)
		  this.divElement.removeChild(layer.canvas);
	}
	this.layers=[];
}
AmiPlotPortlet.prototype.addLayer=function(pos,layer){
	layer.plot = this;
	layer.layerPos = pos;
	var t=this.divElement.firstChild;
	this.layers[pos]=layer;
	this.divElement.insertBefore(layer.canvas,this.gridCanvas.nextSibling);
	layer.setSize(this.width,this.height);
}
AmiPlotPortlet.prototype.getLayer=function(pos){
	return this.layers[pos];
}

AmiPlotPortlet.prototype.close=function(type,param){
	this.clearHover();
}