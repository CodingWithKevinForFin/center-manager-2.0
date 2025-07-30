
function AmiChartLayer_Radial(){
  this.divElement=nw('div');
  var that=this;
  this.isContainer=false;
  this.fontStyle="";
  this.fontSize=10;
  this.fontFamily="arial";
  this.canvas=nw('canvas');
  this.canvas.style.position='absolute';
  this.context = this.canvas.getContext('2d');
  this.context.translate(.5,.5);
  this.canvasMap=new CanvasMap(this.canvas,this.flipX,!this.flipY,false);
}
AmiChartLayer_Radial.prototype.plot;
AmiChartLayer_Radial.prototype.init=function(options){
  this.series={};
  this.groupsByPosition=[];
  this.isInit=true;
  this.canvas.style.opacity=options.opacity/100;
  this.flipX=options.flipX;
  this.flipY=options.flipY;
  this.canvasMap=new CanvasMap(this.canvas,this.flipX,!this.flipY,false);
  this.borderColor=options.borderColor;
  this.circleColor=options.circleColor;
  this.spokesColor=options.spokesColor;
  this.borderSize=noNull(options.borderSize,0);
  this.circleSize=noNull(options.circleSize,0);
  this.spokesSize=noNull(options.spokesSize,0);
  this.spokesCount=options.spokesCount;
  this.circlesCount=options.circlesCount;
  this.innerPaddingPx=options.innerPaddingPx;
  this.outerPaddingPx=options.outerPaddingPx;
  this.sAngle=options.sAngle*PI_180;
  this.eAngle=options.eAngle*PI_180;
  if(this.sAngle>this.eAngle){
	  var t=this.sAngle;
	  this.sAngle=this.eAngle;
	  this.eAngle=t;
  }
  this.lAngle=options.lAngle*PI_180;
  this.rMax=options.rMax;
  this.rMin=options.rMin;
  this.fontColor=options.fontColor;
  this.fontSize=options.fontSize;
  this.xPos=options.xPos;
  this.yPos=options.yPos;
  this.x2Pos=options.x2Pos;
  this.y2Pos=options.y2Pos;
  this.aLabels=options.aLabels;
  this.rLabels=options.rLabels;
  this.updateCanvasShift();
}
AmiChartLayer_Radial.prototype.setZoom=function(zoom,zoomX,zoomY){
  this.zoom=zoom;
  this.zoomX=zoomX;
  this.zoomY=zoomY;
  this.updateCanvasShift();
}
AmiChartLayer_Radial.prototype.updateCanvasShift=function(zoom,zoomX,zoomY){
    //this.canvasMap.setShift(rd((this.zoomX-.5+this.xPos)*this.zoom*this.canvasMap.getWidth()-this.canvasMap.getWidth()*.5*this.zoom+this.canvasMap.getWidth()/2),rd((this.zoomY-.5+this.yPos)*this.zoom*this.canvasMap.getHeight()));
	var x=this.flipX ? -this.zoomX : this.zoomX;
	var y=this.flipY ? -this.zoomY : this.zoomY;
	var z=this.zoom;
	var w=this.canvasMap.getWidth();
	var h=this.canvasMap.getHeight();
    //this.canvasMap.setShift(rd((this.zoomX-.5+this.xPos)*this.zoom*this.canvasMap.getWidth()-this.canvasMap.getWidth()*.5*this.zoom+this.canvasMap.getWidth()/2),rd((this.zoomY-.5+this.yPos)*this.zoom*this.canvasMap.getHeight()));
    this.canvasMap.setShift(rd(-x+w*this.xPos),rd(-y+h*this.yPos));
}
AmiChartLayer_Radial.prototype.setSize=function(width,height){
  this.canvas.style.width=toPx(width);
  this.canvas.style.height=toPx(height);
  this.canvas.width=width;
  this.canvas.height=height;
  this.context.translate(.5,.5);
  if(this.canvasMap!=null){
    this.canvasMap.updateCanvasSize();
    this.updateCanvasShift();
  }
  this.draw();
}
AmiChartLayer_Radial.prototype.draw=function(){
  if(this.canvasMap==null || !this.isInit)
    return;
  this.canvasMap.reset(this.canvas,this.flipX,!this.flipY,false);
  this.context.save();
  this.updateCanvasShift();
  this.canvasMouseManager.clearForTarget(this);
  this.canvasMap.clear();
  var oldX,oldY,oldX2,oldY2;
  var vScale=this.canvasMap.getHeight() * (1+2*Math.abs(this.yPos-.5))*this.zoom;
  var hScale=this.canvasMap.getWidth()* (1+2*Math.abs(this.xPos-.5))*this.zoom;
  var minSize=Math.min(vScale,hScale);
  var outer=minSize/2-this.outerPaddingPx;
  var rScale=(outer-this.innerPaddingPx)/(this.rMax-this.rMin);
  for(var j=this.groupsByPosition.length-1;j>=0;j--){
	var groups=this.groupsByPosition[j];
	for(var k=groups.length-1;k>=0;k--){
	  var ser=groups[k];
      var idPrefix=ser.seriesId+'.'+ser.groupId+'.';
	  var xPos=ser.xPos;
	  var yPos=ser.yPos;
	  var mColor=ser.mColor;
	  var mBorderColor=ser.mBorderColor;
	  var desc=ser.desc;
	  var descColor=ser.descColor;
	  var descSz=ser.descSz;
	  var descFontFam=ser.descFontFam;
	  var descPos=ser.descPos;
	  var mBorderSize=ser.mBorderSize;
	  var mWidth=ser.mWidth;
	  var mHeight=ser.mHeight;
	  var mTop=ser.mTop;
	  var mBottom=ser.mBottom;
	  var mLeft=ser.mLeft;
	  var mRight=ser.mRight;
	  var mShape=ser.mShape;
	  var isSelected=ser.isSelected;
	  var len=ser.size;
	  var sel=ser.sel;
	  this.context.beginPath();
	  
	  var x2Pos=ser.x2Pos;
	  var y2Pos=ser.y2Pos;
	  
	  if(ser.lineSize!=null && ser.lineSize>0 && ser.lineColor!=null){
        this.context.beginPath();
	    for(var i=0;i<len;i++){
          var x=deref(xPos,i);
          var y=deref(yPos,i);
          var x2=deref(x2Pos,i);
          var y2=deref(y2Pos,i);
	      y=(y-this.rMin)*rScale;
	      y+=this.innerPaddingPx;
          x=x*PI_180;
	      y2=(y2-this.rMin)*rScale;
	      y2+=this.innerPaddingPx;
          x2=x2*PI_180;
          if(x==null || y==null)
       	    continue;
		  if(i!=0){
			  var p1x=rd(radiansToX(oldY,oldX));
			  var p1y=rd(radiansToY(oldY,oldX));
			  var p2x=rd(radiansToX(y,x));
			  var p2y=rd(radiansToY(y,x));
			  var p3x=rd(radiansToX(y2,x2));
			  var p3y=rd(radiansToY(y2,x2));
			  var p4x=rd(radiansToX(oldY2,oldX2));
			  var p4y=rd(radiansToY(oldY2,oldX2));
				  
			  var t,t2;
			  var t=deref(ser.fillColor,i);
			  if(t!=null){
			    this.context.beginPath();
			    this.canvasMap.moveTo(p1x,p1y);
			    radiate(this.canvasMap,oldY,oldX,y,x);
                this.context.fillStyle=t;
			    this.canvasMap.lineTo(p3x,p3y);
			    radiate(this.canvasMap,y2,x2,oldY2,oldY2);
			    this.context.fill();
			  }
			  
			  t=deref(ser.fillBorderSize,i);
			  t2=deref(ser.fillBorderColor,i);
			  if(t>0 && t2){
                this.context.lineWidth=t;
                this.context.strokeStyle=t2;
			    this.context.beginPath();
			    this.canvasMap.moveTo(p2x,p2y);
			    this.canvasMap.lineTo(p3x,p3y);
			    this.canvasMap.moveTo(p4x,p4y);
			    this.canvasMap.lineTo(p1x,p1y);
                this.context.stroke();
			  }
              
			  t=deref(ser.lineSize,i);
			  t2=deref(ser.lineColor,i);
			  if(t>0 && t2){
                this.context.lineWidth=t;
                this.context.strokeStyle=t2;
			    this.context.beginPath();
			    this.canvasMap.moveTo(p1x,p1y);
			    radiate(this.canvasMap,oldY,oldX,y,x);
                this.context.stroke();
			  }
              
			  t=deref(ser.line2Size,i);
			  t2=deref(ser.line2Color,i);
			  if(t>0 && t2){
                this.context.lineWidth=t;
                this.context.strokeStyle=t2;
			    this.context.beginPath();
			    this.canvasMap.moveTo(p3x,p3y);
			    radiate(this.canvasMap,y2,x2,oldY2,oldX2);
                this.context.stroke();
			  }
              
			  
		  }
          oldX=x;
          oldY=y;
          oldX2=x2;
          oldY2=y2;
	    }
	  }
	  for(var i=0;i<len;i++){
        var x=deref(xPos,i);
        var y=deref(yPos,i);
        this.context.beginPath();
        this.context.strokeStyle=deref(mBorderColor,i);
        var borderSize=deref(mBorderSize,i);
        if(borderSize>0){
          this.context.lineWidth=borderSize;
        }
        this.context.fillStyle=deref(mColor,i);
        var shape=deref(mShape,i);
	    var width=deref(mWidth,i)/2;
	    var height=deref(mHeight,i)/2;
        var description=deref(desc,i);
        if(shape=='wedge'){
	      var _b=mBottom==null ? add(y,height) : deref(mBottom,i);
	      var _t=mTop   ==null ? sub(y,height) : deref(mTop,i);
	      var _l=mLeft  ==null ? sub(x,width) : deref(mLeft,i);
	      var _r=mRight ==null ? add(x,width) : deref(mRight,i);
	      if(_b==null || _t==null || _l==null || _r==null )
	    	  continue;
	      _t=(_t-this.rMin)*rScale+this.innerPaddingPx;
	      _b=(_b-this.rMin)*rScale+this.innerPaddingPx;
          _l*=PI_180;
          _r*=PI_180;
		  radiate(this.canvasMap,_t,_l,_t,_r);
		  radiate(this.canvasMap,_t,_r,_b,_r);
		  radiate(this.canvasMap,_b,_r,_b,_l);
		  radiate(this.canvasMap,_b,_l,_t,_l);
          this.context.closePath();
		  var midA=(_l+_r) / 2;
	      var ty=radiansToY(_t,midA);
	      var tx=radiansToX(_t,midA);
	      //if(diff(_l,_r) * _t > 10)
          //this.canvasMap.drawText(description,tx,ty,(-midA)/PI_180,-1,0);
          //console.log([this.canvasMap.mapX(tx/4,ty/4),this.canvasMap.mapY(tx/4,ty/4),100,100]);
	      
          this.context.fill();
          
          if(description!=null){
            var descriptionColor=deref(descColor,i);
            this.context.fillStyle=descriptionColor;
            this.canvasMap.setFont(this.fontStyle,deref(descSz),descFontFam);
            this.canvasMap.drawTextIfSpace(description,tx,ty,0,angleToAlignH(midA),-angleToAlignV(midA));
          }
          if(borderSize>0)
            this.context.stroke();
          this.canvasMouseManager.addWedge(this.canvasMap.mapX(0,0),this.canvasMap.mapY(0,0),_b,_t,this.canvasMap.mapAngle(_l),this.canvasMap.mapAngle(_r),this,idPrefix+i,isSelected!=null && isSelected[i],deref(sel,i));
        }else{
	      y=(y-this.rMin)*rScale;
	      y+=this.innerPaddingPx;
          x=x*PI_180;
	      var yr=radiansToY(y,x);
	      var xr=radiansToX(y,x);
	      var _b=mBottom==null ? rd(yr+height) : rd(deref(mBottom,i)*vScale);
	      var _t=mTop   ==null ? rd(yr-height) : rd(deref(mTop,i)*vScale);
	      var _l=mLeft  ==null ? rd(xr-width) : rd(deref(mLeft,i)*hScale);
	      var _r=mRight ==null ? rd(xr+width) : rd(deref(mRight,i)*hScale);
          var _w=_r-_l;
          var _h=_b-_t;
	      var addShape=false;
          if(shape=='circle'){
		    this.canvasMap.fillOval(_l,_t,_w,_h);
		    addShape=true;
          }else if(shape=='square'){
		    this.canvasMap.fillRect(_l,_t,_w,_h);
            if(borderSize>0)
		      this.canvasMap.strokeRect(_l,_t,_w,_h);
		    addShape=true;
          }else if(shape=='triangle'){
            this.canvasMap.moveTo(rd((_r+_l)/2),_b-height);
            this.canvasMap.lineTo(_r,_b);
            this.canvasMap.lineTo(_l,_b);
            this.canvasMap.closePath();
		    addShape=true;
          }
          if(addShape){
        	  this.canvasMouseManager.addRect(this.canvasMap.mapX(_l,_t),this.canvasMap.mapY(_l,_t),this.canvasMap.mapW(_w,_h),this.canvasMap.mapH(_w,_h),this,idPrefix+i,isSelected!=null && isSelected[i],deref(sel,i));
          }
          this.context.fill();
          if(borderSize>0)
            this.context.stroke();
        var description=deref(desc,i);
        if(description!=null){
          var pos=deref(descPos,i);
          this.canvasMap.setFont(this.fontStyle,deref(descSz),descFontFam);
          var descriptionColor=deref(descColor,i);
          this.context.fillStyle=descriptionColor;
          if(pos==null)
        	  pos='top';
          if(pos==(this.flipY ? 'bottom' : 'top')){
              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,max(_b,_t),0,ALIGN_MIDDLE,!this.flipY ? ALIGN_BOTTOM : ALIGN_TOP);
          }else if(pos==(this.flipY ? 'top' : 'bottom')){
              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,min(_b,_t),0,ALIGN_MIDDLE,this.flipY ? ALIGN_BOTTOM : ALIGN_TOP);
          }else if(pos==(this.flipX ? 'right' : 'left')){
              this.canvasMap.drawTextIfSpace(description,min(_r,_l)-2,(_t+_b)/2,0,ALIGN_RIGHT,ALIGN_MIDDLE);
          }else if(pos==(this.flipX ? 'left' : 'right')){
              this.canvasMap.drawTextIfSpace(description,max(_r,_l)+2,(_t+_b)/2,0,ALIGN_LEFT,ALIGN_MIDDLE);
          }else if(pos=='center'){
              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,(_t+_b)/2,0,ALIGN_MIDDLE,ALIGN_MIDDLE);
          }
          }
	    }
	  }
	}
  }
  this.context.restore();
  this.canvasMouseManager.repaint();
}

AmiChartLayer_Radial.prototype.addSeries=function(id,name,data){
	this.series[id+"-"+name]=data;
	var groups=this.groupsByPosition[data.position];
	if(groups==null)
		groups=this.groupsByPosition[data.position]=[];
	groups[groups.length]=data;
}

AmiChartLayer_Radial.prototype.drawGrid=function(gridContext,gridCanvas){
  if(this.canvasMap==null || !this.isInit)
    return;
  this.canvasMap.reset(gridCanvas,this.flipX,!this.flipY,false);
  this.updateCanvasShift();
//  gridContext.clearRect(-1,-1,gridCanvas.width+2,gridCanvas.height+2);
  gridContext.beginPath();
  gridContext.lineWidth = 1;
  gridContext.lineJoin="miter";
  gridContext.fillStyle=this.fontColor;
  this.canvasMap.setFont(this.fontStyle,this.fontSize,this.fontFamily);
  var vScale=this.canvasMap.getHeight() * (1+2*Math.abs(this.yPos-.5))*this.zoom;
  var hScale=this.canvasMap.getWidth()* (1+2*Math.abs(this.xPos-.5))*this.zoom;
  var minSize=Math.min(vScale,hScale);
  var outer=minSize/2-this.outerPaddingPx;
  var rScale=(outer-this.innerPaddingPx)/(this.rMax-this.rMin);
  if(this.borderSize>0){
    gridContext.lineWidth=this.borderSize;
    gridContext.strokeStyle=this.borderColor;
    gridContext.beginPath();
    this.canvasMap.arc(0,0,this.innerPaddingPx,this.sAngle,this.eAngle,false);
    gridContext.stroke();
    gridContext.beginPath();
    this.canvasMap.arc(0,0,outer,this.sAngle,this.eAngle,false);
    gridContext.stroke();
  }
  
  
  gridContext.lineWidth=this.spokesSize;
  gridContext.strokeStyle=this.spokesColor;
  var degrees=(this.eAngle-this.sAngle)/this.spokesCount;
  for(var i=0;i<this.spokesCount+1;i++){
    gridContext.beginPath();
    var d=this.sAngle+i*degrees;
    var angle=rd(d/PI_180*4)/4;
      
    var rx=radiansToX(this.innerPaddingPx,d);
    var ry=radiansToY(this.innerPaddingPx,d);
    this.canvasMap.moveTo(rx,ry);
    rx=radiansToX(outer,d);
    ry=radiansToY(outer,d);
      
    if(this.fontColor!=null){
      if(i!=this.spokesCount || this.sAngle!=0 || this.eAngle!=PI2){
        this.canvasMap.drawText(" "+this.aLabels[i]+" ",rx,ry ,this.flipX==this.flipY ? -angle : angle,-1,0);
    	if(this.spokesSize>0){
          this.canvasMap.lineTo(rx,ry);
          gridContext.stroke();
    	}
      }
    }
  }
  var spacing=(outer-this.innerPaddingPx)/this.circlesCount;
  gridContext.lineWidth=this.circleSize;
  gridContext.strokeStyle=this.circleColor;
  for(var i=1;i<this.circlesCount;i++){
	var t=(i*spacing)+this.innerPaddingPx;
	if(this.circleSize>0){
      gridContext.beginPath();
      this.canvasMap.arc(0,0,t,this.sAngle,this.eAngle,false);
      gridContext.stroke();
    }  
    gridContext.beginPath();
//    var val=i*spacing/rScale;
    var rx=radiansToX(t,this.lAngle);
    var ry=radiansToY(t,this.lAngle);
    if(this.fontColor!=null){
      if(this.rLabels!=null){
         angle=-this.lAngle/PI_180;
        this.canvasMap.drawText(this.rLabels[i],rx,ry,angle,0,(-270<=angle && angle<-90? 1 : -1 ));
      }
    }
    gridContext.stroke();
  }
}