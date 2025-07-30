AmiChartLayer_Bar.prototype.LINE_TYPE_DIRECT = 0;
AmiChartLayer_Bar.prototype.LINE_TYPE_HORZ = 1;
AmiChartLayer_Bar.prototype.LINE_TYPE_VERT = 2;

AmiChartLayer_Bar.prototype.setSize=function(width,height){
	this.canvas.style.width=toPx(width);
	this.canvas.style.height=toPx(height);
	this.canvas.width=width;
	this.canvas.height=height;
    this.context.translate(.5,.5);
    if(this.canvasMap!=null){
      this.canvasMap.updateCanvasSize();
    }
}


function AmiChartLayer_Bar(){
  this.divElement=nw('div');
  var that=this;
  this.isContainer=false;
  this.fontStyle="";
  this.fontSize=14;
  this.fontFamily="arial";
  this.canvas=nw('canvas');
  this.canvas.style.position='absolute';
  this.context = this.canvas.getContext('2d');
  this.context.translate(.5,.5);
}

AmiChartLayer_Bar.prototype.plot;

AmiChartLayer_Bar.prototype.clearData=function(){
  this.series={};
  this.groupsByPosition=[];
}
AmiChartLayer_Bar.prototype.init=function(options){
  this.isInit=true;
  this.canvas.style.opacity=options.opacity/100;
  this.hGrid=options.hGrid;
  this.vGrid=options.vGrid;
  this.hMajorGrid=options.hMajorGrid;
  this.vMajorGrid=options.vMajorGrid;
  this.hGroupSize=options.hGroupSize;
  this.vGroupSize=options.vGroupSize;
  this.hGridColor=options.hGridColor;
  this.vGridColor=options.vGridColor;
  this.hMidGridColor=options.hMidGridColor;
  this.vMidGridColor=options.vMidGridColor;
  this.hMajorGridColor=options.hMajorGridColor;
  this.vMajorGridColor=options.vMajorGridColor;
  
  this.hGridSize=options.hGridSize;
  this.vGridSize=options.vGridSize;
  this.hMidGridSize=options.hMidGridSize;
  this.vMidGridSize=options.vMidGridSize;
  this.hMajorGridSize=options.hMajorGridSize;
  this.vMajorGridSize=options.vMajorGridSize;
  
  this.borderColor=options.borderColor;
  this.flipX=options.flipX;
  this.posZoomX=options.posZoomX;
  this.flipY=options.flipY;
  this.posZoomY=options.posZoomY;
  this.posOffsetX=options.posOffsetX;
  this.posOffsetY=options.posOffsetY;
  this.canvasMap=new CanvasMap(this.canvas,this.flipX,this.flipY,false);
}

AmiChartLayer_Bar.prototype.drawGrid=function(gridContext,gridCanvas){
  this.canvasMap.reset(gridCanvas,this.flipX,this.flipY,false);
  gridContext.lineWidth = 1;
  gridContext.lineJoin="miter";
  this.canvasMap.setFont(this.fontStyle,this.fontSize,this.fontFamily);
  if(this.vMajorGridColor!=null){
    gridContext.beginPath();
    gridContext.lineWidth = this.vMajorGridSize;
    gridContext.strokeStyle=this.vMajorGridColor;
	for(i in this.vGrid){
	  var t1=this.vGrid[i]-this.vGroupSize/2;
	  for(j in this.vMajorGrid){
	    var t=rd(t1+this.vMajorGrid[j]);
	    this.canvasMap.moveTo(0,t);
	    this.canvasMap.lineTo(this.canvasMap.getWidth(),t);
	  }
	}
    gridContext.stroke();
  }
  if(this.vMidGridColor!=null){
    gridContext.beginPath();
    gridContext.lineWidth = this.vMidGridSize;
    gridContext.strokeStyle=this.vMidGridColor;
    var last=null;
	for(i in this.vGrid){
	  var val=this.vGrid[i];
	  if(last!=null){
	    var t=rd((val+last)/2);
	    this.canvasMap.moveTo(0,t);
	    this.canvasMap.lineTo(this.canvasMap.getWidth(),t);
	  }
	  last=val;
	}
    gridContext.stroke();
  }
  if(this.vGridColor!=null){
    gridContext.beginPath();
    gridContext.lineWidth = this.vGridSize;
    gridContext.strokeStyle=this.vGridColor;
	for(var i in this.vGrid){
	  var t=rd(this.vGrid[i]);
	  this.canvasMap.moveTo(0,t);
	  this.canvasMap.lineTo(this.canvasMap.getWidth(),t);
	}
    gridContext.stroke();
  }
  
  if(this.hMajorGridColor!=null){
    gridContext.beginPath();
    gridContext.lineWidth = this.hMajorGridSize;
    gridContext.strokeStyle=this.hMajorGridColor;
	for(i in this.hGrid){
	  var t1=this.hGrid[i]-this.hGroupSize/2;
	  for(j in this.hMajorGrid){
	    var t=rd(t1+this.hMajorGrid[j]);
	    this.canvasMap.moveTo(t,0);
	    this.canvasMap.lineTo(t,this.canvasMap.getHeight());
	  }
	}
    gridContext.stroke();
  }
  if(this.hMidGridColor!=null){
    gridContext.beginPath();
    gridContext.lineWidth = this.hMidGridSize;
    gridContext.strokeStyle=this.hMidGridColor;
    var last=null;
	for(i in this.hGrid){
	  var val=this.hGrid[i];
	  if(last!=null){
	    var t=rd((val+last)/2);
	    this.canvasMap.moveTo(t,0);
	    this.canvasMap.lineTo(t,this.canvasMap.getHeight());
	  }
	  last=val;
	}
    gridContext.stroke();
  }
  if(this.hGridColor!=null){
    gridContext.beginPath();
    gridContext.lineWidth = this.hGridSize;
    gridContext.strokeStyle=this.hGridColor;
	for(i in this.hGrid){
	  var t=rd(this.hGrid[i]);
	  this.canvasMap.moveTo(t,0);
	  this.canvasMap.lineTo(t,this.canvasMap.getHeight());
	}
    gridContext.stroke();
  }
  this.canvasMap.rect(1,1,this.canvasMap.getWidth()+1,this.canvasMap.getHeight()+1);
  gridContext.clip();
  if(this.borderColor!=null){
    gridContext.beginPath();
    gridContext.strokeStyle=this.borderColor
    this.canvasMap.strokeRect(this.lPadding,this.tPadding,this.canvasMap.getWidth()-this.lPadding-this.rPadding-1,this.canvasMap.getHeight()-this.tPadding-this.bPadding-1);
    gridContext.stroke();
  }
}

AmiChartLayer_Bar.prototype.draw=function(){
  var start=Date.now();
  this.canvasMap.reset(this.canvas,this.flipX,this.flipY,false);
  this.canvasMap.clear();
  this.canvasMouseManager.clearForTarget(this);
  if(this.canvasMap==null || this.isInit===false || this.groupsByPosition==null)
    return;
  for(var j=this.groupsByPosition.length-1;j>=0;j--){
	var groups=this.groupsByPosition[j];
	if (groups == null)
		continue;
	for(var k=groups.length-1;k>=0;k--){
	  var ser=groups[k];
      var idPrefix=ser.seriesId+'.'+ser.groupId+'.';
      var lineType=ser.lineType;
	  var xPos=ser.xPos;
	  var yPos=ser.yPos;
	  var x2Pos=ser.x2Pos;
	  var y2Pos=ser.y2Pos;
	  var mColor=ser.mColor;
	  var mBorderColor=ser.mBorderColor;
	  var mBorderSize=ser.mBorderSize;
	  var mWidth=ser.mWidth;
	  var mHeight=ser.mHeight;
	  var mTop=ser.mTop;
	  var mBottom=ser.mBottom;
	  var mLeft=ser.mLeft;
	  var mRight=ser.mRight;
	  var mShape=ser.mShape;
	  var isSelectedList=ser.isSelected;
	  var isSelected;
	  if(isSelectedList!=null){
		  isSelected={};
		  for(var i=0;i<isSelectedList.length;i++)
			  isSelected[isSelectedList[i]]=true;
	  }else
		  isSelected=null;
	  var len=ser.size;
	  var sel=ser.sel;
	  var desc=ser.desc;
	  var descColor=ser.descColor;
	  var descSz=ser.descSz;
	  var descFontFam=ser.descFontFam;
	  var descPos=ser.descPos;
	  if(ser.lineSize!=null && ser.lineSize>0 && ser.lineColor!=null && xPos!=null & yPos!=null && x2Pos!=null & y2Pos!=null){
        this.context.beginPath();
        var oldX,oldY,oldX2,oldY2;
        var first=true;
        var lastFillStyle=null;
	    for(var i=0;i<len;i++){
          var x=deref(xPos,i);
          var y=deref(yPos,i);
          var x2=deref(x2Pos,i);
          var y2=deref(y2Pos,i);
          if(x==null || y==null)
        	  continue;
          x=rd(this.scaleX(x));
          y=rd(this.scaleY(y));
          x2=rd(this.scaleX(x2));
          y2=rd(this.scaleY(y2));
          if(!first){
			  
			  var p1x=oldX;
			  var p1y=oldY;
			  var p2x=x;
			  var p2y=y;
			  var p3x=x2;
			  var p3y=y2;
			  var p4x=oldX2;
			  var p4y=oldY2;
				  
			  var t=deref(ser.fillColor,i);
			  if(t!=null && oldX2!=null && oldY2!=null){
				if(t!=lastFillStyle){
			      this.context.fill();
                  this.context.fillStyle=lastFillStyle=t;
			      this.context.beginPath();
				}
			    this.canvasMap.moveTo(p1x,p1y);
			    if(lineType>this.LINE_TYPE_DIRECT)
			      this.canvasMap.lineTo(lineType==this.LINE_TYPE_VERT ? p1x : x,lineType==this.LINE_TYPE_VERT ? y : p1y);
			    this.canvasMap.lineTo(x,y);
			    this.canvasMap.lineTo(p3x,p3y);
			    if(lineType>this.LINE_TYPE_DIRECT)
			      this.canvasMap.lineTo(lineType==this.LINE_TYPE_VERT ? oldX2 : p3x,lineType==this.LINE_TYPE_VERT ? p3y : oldY2);
			    this.canvasMap.lineTo(oldX2,oldY2);
			  }
			  
		  }else
            first=false;
          oldX=x;
          oldY=y;
          oldX2=x2;
          oldY2=y2;
	    }
		this.context.fill();
	  }
	  if(ser.lineSize!=null && ser.lineSize>0 && ser.lineColor!=null && xPos!=null & yPos!=null){
        this.context.beginPath();
        var oldX,oldY,oldX2,oldY2;
        var first=true;
        var lastLineWidth=null;
        var lastStrokeStyle=null;
	    for(var i=0;i<len;i++){
          var x=deref(xPos,i);
          var y=deref(yPos,i);
          var x2=deref(x2Pos,i);
          var y2=deref(y2Pos,i);
          if(x==null || y==null)
        	  continue;
          x=rd(this.scaleX(x));
          y=rd(this.scaleY(y));
          x2=rd(this.scaleX(x2));
          y2=rd(this.scaleY(y2));
          
          if(!first){
			  
			  var p1x=oldX;
			  var p1y=oldY;
			  var p2x=x;
			  var p2y=y;
			  var p3x=x2;
			  var p3y=y2;
			  var p4x=oldX2;
			  var p4y=oldY2;
				  
			  var t=deref(ser.fillBorderSize,i);
			  var t2=deref(ser.fillBorderColor,i);
			  if(t>0 && t2&& oldX2!=null && oldY2!=null){
				if(lastLineWidth!=t || lastStrokeStyle!=t2){
                  this.context.stroke();
                  this.context.lineWidth=lastLineWidth=t;
                  this.context.strokeStyle=lastStrokeStyle=t2;
			      this.context.beginPath();
				}
			    this.canvasMap.moveTo(p2x,p2y);
			    this.canvasMap.lineTo(p3x,p3y);
			    this.canvasMap.moveTo(p4x,p4y);
			    this.canvasMap.lineTo(p1x,p1y);
			  }
              
			  t=deref(ser.lineSize,i);
			  t2=deref(ser.lineColor,i);
			  if(t>0 && t2){
				if(lastLineWidth!=t || lastStrokeStyle!=t2){
                  this.context.stroke();
                  this.context.lineWidth=lastLineWidth=t;
                  this.context.strokeStyle=lastStrokeStyle=t2;
			      this.context.beginPath();
				}
			    this.canvasMap.moveTo(oldX,oldY);
			    if(lineType>this.LINE_TYPE_DIRECT)
			      this.canvasMap.lineTo(lineType==this.LINE_TYPE_VERT ? oldX : x,lineType==this.LINE_TYPE_VERT ? y : oldY);
			    this.canvasMap.lineTo(x,y);
			  }
              
			  t=deref(ser.line2Size,i);
			  t2=deref(ser.line2Color,i);
			  if(t>0 && t2 && x2!=null && y2!=null){
				if(lastLineWidth!=t || lastStrokeStyle!=t2){
                  this.context.stroke();
                  this.context.lineWidth=lastLineWidth=t;
                  this.context.strokeStyle=lastStrokeStyle=t2;
			      this.context.beginPath();
				}
			    this.canvasMap.moveTo(p3x,p3y);
			    if(lineType>this.LINE_TYPE_DIRECT)
			      this.canvasMap.lineTo(lineType==this.LINE_TYPE_VERT ? oldX2 : p3x,lineType==this.LINE_TYPE_VERT ? p3y : oldY2);
			    this.canvasMap.lineTo(oldX2,oldY2);
			  }
		  }else
            first=false;
          oldX=x;
          oldY=y;
          oldX2=x2;
          oldY2=y2;
	    }
        this.context.stroke();
	  }
      this.context.beginPath();
      var lastLineWidth=null;
      var lastStrokeStyle=null;
      var lastFillStyle=null;
	  for(var i=0;i<len;i++){
        var x=deref(xPos,i);
        var y=deref(yPos,i);
        var strokeStyle=deref(mBorderColor,i);
        var borderSize=deref(mBorderSize,i);
        var fillStyle=deref(mColor,i);
        this.context.fillStyle=lastFillStyle=fillStyle;
        this.context.lineWidth=lastLineWidth=borderSize==null ? null : (borderSize*2);
        this.context.strokeStyle=lastStrokeStyle=strokeStyle;
		this.context.beginPath();
        var shape=deref(mShape,i);
	    var width=deref(mWidth,i)/2;
	    var height=deref(mHeight,i)/2;
	    
        x=this.scaleX(x);
        y=this.scaleY(y);
	    var _b=mBottom==null && y!=null && height!=null ? rd(y+height) : rd(this.scaleY(deref(mBottom,i)));
	    var _t=mTop   ==null && y!=null && height!=null ? rd(y-height) : rd(this.scaleY(deref(mTop,i)));
	    var _l=mLeft  ==null && x!=null && width!=null ? rd(x-width) : rd(this.scaleX(deref(mLeft,i)));
	    var _r=mRight ==null && x!=null && width!=null ? rd(x+width) : rd(this.scaleX(deref(mRight,i)));
	    if(_b==null || _t==null || _l==null || _r==null)
	    	continue;
	    
        var _w=_r-_l;
        var _h=_b-_t;
        _l-=.5; _t-=.5;
	    var shapeChar=null;
	    if(shape && (strokeStyle||fillStyle)){
          if(shape=='circle'){
            if(borderSize>0 && borderSize>Math.abs(_w) || borderSize>Math.abs(_h)){//catch border bug
        	  var tw=Math.abs(_w/2)+borderSize;
        	  var th=Math.abs(_h/2)+borderSize;
              this.context.beginPath();
              this.context.fillStyle=strokeStyle;
		      this.canvasMap.fillOval(_l-borderSize,_t-borderSize,_w+1+borderSize*2,_h+1+borderSize*2);
              this.context.fill();
              this.context.beginPath();
              this.context.fillStyle=fillStyle;
		      this.canvasMap.fillOval(_l,_t,_w,_h);
              this.context.fill();
              this.context.beginPath();
            }else{
		      this.canvasMap.fillOval(_l,_t,_w,_h);
              if(borderSize>0 && strokeStyle)
                this.context.stroke();
              if(fillStyle)
                this.context.fill();
            }
		    shapeChar='c';
          }else if(shape=='square'){
		    this.canvasMap.rect(_l,_t,_w,_h);
            if(borderSize>0 && strokeStyle)
              this.context.stroke();
          if(fillStyle)
            this.context.fill();
		    shapeChar='r';
          }else if(shape=='triangle'){
            this.canvasMap.moveTo(rd((_r+_l)/2),_t);
            this.canvasMap.lineTo(_r,_b-.5);
            this.canvasMap.lineTo(_l,_b-.5);
            this.canvasMap.closePath();
            if(borderSize>0 && strokeStyle)
              this.context.stroke();
            if(fillStyle)
              this.context.fill();
		    shapeChar='t';
          }else if(shape=='hbar'){
        	 _l=-.5;
        	 _r=_w=this.canvas.width;
		    this.canvasMap.rect(_l,_t,_w,_h);
		    shapeChar='r';
            if(borderSize>0 && strokeStyle)
              this.context.stroke();
            if(fillStyle)
              this.context.fill();
          }else if(shape=='vbar'){
        	_t=-.5;
        	_h=_b=this.canvas.height;
		    this.canvasMap.rect(_l,_t,_w,_h);
		    shapeChar='r';
            if(borderSize>0 && strokeStyle)
              this.context.stroke();
            if(fillStyle)
              this.context.fill();
          }
          if(shapeChar!=null)
        	this.canvasMouseManager.addRect(this.canvasMap.mapX(_l,_t),this.canvasMap.mapY(_l,_t),this.canvasMap.mapW(_w,_h),this.canvasMap.mapH(_w,_h),this,idPrefix+i,isSelected!=null && isSelected[i],false != deref(sel,i),shapeChar);
	    }
        var description=deref(desc,i);
        if(description!=null){
          var pos=deref(descPos,i);
          this.canvasMap.setFont(this.fontStyle,deref(descSz),descFontFam);
		  this.context.beginPath();
          var descriptionColor=deref(descColor,i);
          this.context.fillStyle=descriptionColor;
          if(pos==null || pos=='center'){
              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,(_b+_t)/2,0,ALIGN_MIDDLE,ALIGN_MIDDLE);
          }if(pos==(this.flipY ? 'bottom' : 'top')){
              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,min(_b,_t),0,ALIGN_MIDDLE,!this.flipY ? ALIGN_BOTTOM : ALIGN_TOP);
          }else if(pos==(this.flipY ? 'top' : 'bottom')){
              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,max(_b,_t),0,ALIGN_MIDDLE,this.flipY ? ALIGN_BOTTOM : ALIGN_TOP);
          }else if(pos==(this.flipX ? 'right' : 'left')){
              this.canvasMap.drawTextIfSpace(description,min(_r,_l)-2,(_t+_b)/2,0,ALIGN_RIGHT,ALIGN_MIDDLE);
          }else if(pos==(this.flipX ? 'left' : 'right')){
              this.canvasMap.drawTextIfSpace(description,max(_r,_l)+2,(_t+_b)/2,0,ALIGN_LEFT,ALIGN_MIDDLE);
          }
        }
	  }
	}
  }
  this.canvasMouseManager.repaint();
    var end=Date.now();
    log("Draw: "+(end-start));
}

AmiChartLayer_Bar.prototype.scaleX=function(x){
	if(x==null)
		return null;
	return x*this.posZoomX + this.posOffsetX;
}
AmiChartLayer_Bar.prototype.scaleY=function(y){
	if(y==null)
		return null;
	return y*this.posZoomY + this.posOffsetY;
}

AmiChartLayer_Bar.prototype.addSeries=function(id,name,data){
	this.series[id+"-"+name]=data;
	var groups=this.groupsByPosition[data.position];
	if(groups==null)
		groups=this.groupsByPosition[data.position]=[];
	groups[groups.length]=data;
}


