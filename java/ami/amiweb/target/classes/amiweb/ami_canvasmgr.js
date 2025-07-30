var ALIGN_TOP=-1;
var ALIGN_LEFT=-1;
var ALIGN_MIDDLE=0;
var ALIGN_BOTTOM=1;
var ALIGN_RIGHT=1;

function CanvasMap(canvas,flipX,flipY,transpose){
  this.reset(canvas,flipX,flipY,transpose);
}

CanvasMap.prototype.reset=function(canvas,flipX,flipY,transpose){
  this.shiftX=0;
  this.shiftY=0;
  this.canvas=canvas;
  this.context = this.canvas.getContext('2d');
  this.flipX=flipX;
  this.flipY=flipY;
  this.transpose=transpose;
  this.updateCanvasSize();
  this.fontHeight=14;
  this.textRects=[];
}

CanvasMap.prototype.setFont=function(fontStyle,fontSize,fontFamily){
  this.fontStyle=fontStyle;
  this.fontFamily=fontFamily;
  this.fontSize=fontSize;
  this.context.font=this.fontStyle+" "+this.fontSize+"px "+this.fontFamily;
}

CanvasMap.prototype.setShift=function(x,y){
  this.shiftX=x;
  this.shiftY=y;
}

CanvasMap.prototype.updateCanvasSize=function(){
  this.width=1*(this.transpose ? this.canvas.height : this.canvas.width);
  this.height=1*(this.transpose ? this.canvas.width : this.canvas.height);
}

CanvasMap.prototype.getWidth=function(){
	return this.width;
}
CanvasMap.prototype.getHeight=function(){
	return this.height;
}

CanvasMap.prototype.mapX=function(x,y){
	if(this.transpose){
		return this.flipY ? (this.height - y-1-this.shiftY) : y+this.shiftY;
	}else{
		return this.flipX ? (this.width - x-1 - this.shiftX) : x + this.shiftX;
	}
}
CanvasMap.prototype.mapY=function(x,y){
	if(this.transpose){
		return this.flipX ? (this.width - x-1 -this.shiftX) : x+this.shiftX;
	}else{
		return this.flipY ? (this.height - y-1-this.shiftY) : y+this.shiftY;
	}
}
CanvasMap.prototype.mapAngle=function(start){
	if(this.flipY){
	  if(this.flipX)
	    return -Math.PI+start;
	  else
	    return PI2-start;
	}else if(this.flipX)
	  return Math.PI-start;
	else
	  return start;
}

CanvasMap.prototype.mapW=function(w,h){
	if(this.transpose){
		return this.flipY ? -h : h;
	}else{
		return this.flipX ? -w : w;
	}
}
CanvasMap.prototype.mapH=function(w,h){
	if(this.transpose){
		return this.flipX ? -w : w;
	}else{
		return this.flipY ? -h : h;
	}
}
CanvasMap.prototype.moveTo=function(x,y){
	this.context.moveTo(this.mapX(x,y),this.mapY(x,y));
}
CanvasMap.prototype.quadTo=function(x,y,x2,y2){
	this.context.quadraticCurveTo(this.mapX(x,y),this.mapY(x,y),this.mapX(x2,y2),this.mapY(x2,y2));
}
CanvasMap.prototype.lineTo=function(x,y){
	this.context.lineTo(this.mapX(x,y),this.mapY(x,y));
}
CanvasMap.prototype.closePath=function(){
	this.context.closePath();
}
CanvasMap.prototype.arc=function(x,y,size,start,end,clockwise){
	if(size<0)
		size=0;
	if(this.flipY){
	  if(this.flipX)
	    this.context.arc(this.mapX(x,y),this.mapY(x,y),size,-Math.PI+start,-Math.PI+end,clockwise);
	  else
	    this.context.arc(this.mapX(x,y),this.mapY(x,y),size,PI2-start,PI2-end,!clockwise);
	}else if(this.flipX)
	  this.context.arc(this.mapX(x,y),this.mapY(x,y),size,Math.PI-start,Math.PI-end,!clockwise);
	else
	  this.context.arc(this.mapX(x,y),this.mapY(x,y),size,start,end,clockwise);
}

CanvasMap.prototype.strokeRect=function(left,top,width,height){
    this.context.strokeRect(this.mapX(left,top),this.mapY(left,top),this.mapW(width,height),this.mapH(width,height));
}
CanvasMap.prototype.fillRect=function(left,top,width,height){
    this.context.fillRect(this.mapX(left,top),this.mapY(left,top),this.mapW(width,height),this.mapH(width,height));
}
CanvasMap.prototype.rect=function(left,top,width,height){
    this.context.rect(this.mapX(left,top),this.mapY(left,top),this.mapW(width,height),this.mapH(width,height));
}
CanvasMap.prototype.fillOvalCenteredAt=function(centerX,centerY,radiusWidth,radiusHeight){
  this.fillOval(centerX-radiusWidth,centerY-radiusHeight,radiusWidth*2,radiusHeight*2);
}

CanvasMap.prototype.fillOval=function(left,top,width,height){
  var x=this.mapX(left,top);
  var y=this.mapY(left,top);
  var w=this.mapW(width,height);
  var h=this.mapH(width,height);
  drawCanvasOval(this.context,x,y,w,h);
}

function drawCanvasOval(context,x,y,w,h){
    var midX=x+w/2;
    var midY=y+h/2;
    if(Math.abs(w)===Math.abs(h)){
      var t=Math.abs(w)/2;
	  context.moveTo(midX+t, midY);
	  context.arc(midX, midY, t, 0,PI2, false);
    }else if(context.ellipse){
      var t=Math.abs(w)/2;
      var t2=Math.abs(h)/2;
	  context.moveTo(midX+t, midY);
	  context.ellipse(midX, midY, t,t2, 0,0,PI2, false);
//      context.save();
//      context.translate(midX,midY);
//      context.scale(Math.abs(w/h),1);
//	  context.arc(0, 0, Math.abs(h)/2, 0,PI2, false);
//      context.restore();
    }
}

CanvasMap.prototype.clear=function(){
  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);
  this.textRects=[];
}

//TODO: does not properly check for rotated text
CanvasMap.prototype.drawTextIfSpace=function(text,x,y,rotate,xAlign,yAlign){
	var w=this.context.measureText(text).width-2;
	var h=this.fontSize-2;
	var xOrig=x;
	var yOrig=y;
	if(xAlign==ALIGN_RIGHT)
		x-=w;
	else if(xAlign==ALIGN_MIDDLE)
		x-=w/2;
	if(yAlign==ALIGN_TOP)
		y-=h;
	else if(yAlign==ALIGN_MIDDLE)
		y-=h/2;
	var yy=y+h;
	var xx=x+w;
	var topIdx=fl(x/10);//let's assume 10px is smallest font.
	var botIdx=fl(xx/10)+1;
	for(var j=topIdx;j<botIdx;j++){
	  var textRects=this.textRects[j];
	  if(textRects!=null)//nothing at this row
	    for(var i=0,l=textRects.length;i<l;i+=4)
		  if(xx>=textRects[i] && x<=textRects[i+2] && yy>=textRects[i+1] && y<=textRects[i+3]) 
			return false;
	}
	for(var j=topIdx;j<botIdx;j++){
	  var textRects=this.textRects[j];
	  if(textRects==null)
		  textRects=this.textRects[j]=[];
	   textRects.push(x,y,xx,yy);
	}
	this.drawText(text,xOrig,yOrig,rotate,xAlign,yAlign);
	return true;
}
CanvasMap.prototype.drawText=function(text,x,y,rotate,xAlign,yAlign){
	
	var rotateOffset=0;
	if(this.transpose)
	  rotate+=90;
	if(this.flipX)
	  rotate+=180;
	if(rotate<0)
		rotate=360-((-rotate)%360);
	else
	    rotate%=360;
    if(rotate>90 && rotate<270){
      rotate=(rotate+180)%360;
      xAlign=-xAlign;
      yAlign=-yAlign;
    }
	if(xAlign===ALIGN_LEFT)
		this.context.textAlign='left';
	else if(xAlign===ALIGN_RIGHT)
		this.context.textAlign='right';
	else
		this.context.textAlign='center';
	if(yAlign===ALIGN_TOP)
		this.context.textBaseline='top';
	else if(yAlign===ALIGN_BOTTOM)
		this.context.textBaseline='bottom';
	else
		this.context.textBaseline='middle';
	
	if(rotate%360===0)
      this.context.fillText(text,this.mapX(x,y),this.mapY(x,y));
	else{
	  this.context.save();
	  this.context.translate(this.mapX(x,y), this.mapY(x,y));
	  this.context.rotate(rotate*PI_180);
      this.context.fillText(text,0,0);
	  this.context.restore();
	}
}

function CanvasMouseManager(canvas){
  var that=this;
  this.canvas=canvas;
  this.canvas.onmousedown=function(e){that.onMousedown(e)};
  this.canvas.onmouseup=function(e){that.onMouseup(e)};
  this.canvas.onmousemove=function(e){that.onMousemove(e)};
  this.canvas.onmouseout=function(e){that.onMouseout(e)};
  this.canvas.onMouseWheel=function(e,delta){that.onMousewheel(e,delta)};
  this.canvas.ondblclick=function(e){that.onDblclick(e)};
  this.canvas.onkeydown=function(e){that.onKeyDown(e)};
  this.canvas.onkeyup=function(e){that.onKeyUp(e)};
  this.canvas.tabIndex=1000;
  this.context = this.canvas.getContext('2d');
  this.context.translate(.5,.5);
  this.shapesById={};
  this.shapesSelected={};
  this.shiftStart=null;
  this.timer=null;
}

CanvasMouseManager.prototype.clear=function(){
  this.width=this.canvas.width;
  this.height=this.canvas.height;
}

CanvasMouseManager.prototype.onMouseup=function(e){
	
  if(this.trackingMouseDrag){
	this.trackingMouseDrag=false;
    document.onmouseup=null;
    document.onmousemove=null;
  }
  this.timer=null;
  var button=getMouseButton(e);
  if(button==3){
	  this.startMove=null;
	  return;
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
      this.fireSelectedChanged(x,y,w,h,e.ctrlKey,e.shiftKey);
      return;
    }
  }
  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);
    this.fireMouseClicked(x,y,button,e.ctrlKey,e.shiftKey);
  return;
}

CanvasMouseManager.prototype.fireSelectedChanged=function(x,y,w,h,isCtrl,isShift){
	if(this.onSelectedChanged){
		this.onSelectedChanged(x,y,w,h,isCtrl,isShift);
	}
}
CanvasMouseManager.prototype.fireMouseClicked=function(x,y,button,ctrl,shift){
	if(this.onMouseClicked)
		this.onMouseClicked(this,x,y,button,ctrl,shift);
}



CanvasMouseManager.prototype.onMousedown=function(e){
  var that=this;
  if(document.onmouseup==null){
	this.trackingMouseDrag=true;
    document.onmouseup=function(e){that.onMouseup(e)};
    document.onmousemove=function(e){that.onMousemove(e)};
  }
  var button=getMouseButton(e);
  var p=getMouseLayerPoint(e);
  if(button==3 || (e.ctrlKey && button==1)){
	  this.startMove=p;
	  return;
  }
  var x=p.x;
  var y=p.y;
  this.shiftStart=[x,y];
}

CanvasMouseManager.prototype.repaint=function(){
    this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);
    for(var i in this.shapesSelected)
	  this.drawShape(this.shapesSelected[i],true);
}

CanvasMouseManager.prototype.clearSelectRegion=function(){
	if(this.shiftStart==null)
		return;
	this.shiftStart=null;
}

CanvasMouseManager.prototype.onKeyDown=function(e){
	if(e.key=="Control")
		this.canvas.style.cursor="move";
}
CanvasMouseManager.prototype.onKeyUp=function(e){
	if(e.key=="Control")
		this.canvas.style.cursor="default";
}
CanvasMouseManager.prototype.onMouseout=function(e){
  if(this.timer!=null)
	  clearTimeout(this.timer);
  this.canvas.style.cursor="default";
  this.onHover(this,0,0,null,0,0);
}
CanvasMouseManager.prototype.onMousewheel=function(e,delta){
  var p=getMousePointRelativeTo(e,this.canvas);
  this.onZoom(p.x,p.y,delta);
}
CanvasMouseManager.prototype.onDblclick=function(e){
  var p=getMousePointRelativeTo(e,this.canvas);
  this.onZoom(p.x,p.y,5);
}
CanvasMouseManager.prototype.onMouseStill=function(e){
    var p=getMouseLayerPoint(this.lastMoveEvent);
    if(this.shiftStart!=null)
    	return;
   this.onHover(this,p.x,p.y);
	
}



CanvasMouseManager.prototype.onMousemove=function(e){
  if(this.onMouseHasMoved)
	this.onMouseHasMoved(e);
  var p=getMousePointRelativeTo(e,this.canvas);
  if(this.lastMoveEvent!=null){
	  p2=getMousePointRelativeTo(this.lastMoveEvent,this.canvas);
	  if(p.x==p2.x && p.y==p2.y)
		  return;
  }
  var button=getMouseButton(e);
  if(button==3 || (button==1 && e.ctrlKey)){
	  if(this.startMove!=null){
		var dx=p.x-this.startMove.x;
		var dy=p.y-this.startMove.y;
		if(dx!=0 || dy!=0){
			if(this.onMouseDragged)
				this.onMouseDragged(e,dx,dy);
			this.startMove=p;
		}
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
    this.context.strokeStyle='rgba(64,64,64,.9)';
	this.context.strokeRect(t[0],t[1],x-t[0],y-t[1]);
    this.context.stroke();
  }
}


