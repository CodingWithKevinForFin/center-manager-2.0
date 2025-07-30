
function AmiAxisPortlet(portletId,parentId){
  var that=this;
  this.portlet=new Portlet(this,portletId,null);
  this.isContainer=false;
  this.divElement.style.background='#CCCCCC';
  this.canvas=nw('canvas');
  this.canvas.width=500;
  this.canvas.height=256;
  this.context = this.canvas.getContext('2d');
  this.divElement.appendChild(this.canvas);
  this.canvas.onMouseWheel=function(e,delta){that.onMousewheel(e,delta);};
  this.canvas.ondblclick=function(e){that.onDblclick(e)};
  this.zoomMoveConsumedFlag=true;
  this.zoomMovePending=0;
  this.lastDragPos=0;
  makeDraggable(this.canvas,this,true,true);
}
AmiAxisPortlet.prototype.ondragging=function(element,dx,dy,e){
  this.onDrag(this.orientation=='L' || this.orientation=='R' ? dy : dx);
}
AmiAxisPortlet.prototype.ondraggingEnd=function(element,dx,dy,e){
  this.onDrag(this.orientation=='L' || this.orientation=='R' ? dy : dx);
  this.lastDragPos=0;
}
AmiAxisPortlet.prototype.setZoomOffset=function(zoomOffset){
	this.zoomOffset=zoomOffset;
}
AmiAxisPortlet.prototype.onDrag=function(pos){
  var delta=pos-this.lastDragPos;
  if(delta==0)
	  return;
  this.lastDragPos=pos;
  this.zoomMovePending+=delta;
  if(this.zoomMoveConsumedFlag && this.zoomMovePending!=0){
    this.callBack('zoomMove',{delta:this.zoomMovePending});
    this.zoomMoveConsumedFlag=false;
    this.zoomMovePending=0;
  }
}


AmiAxisPortlet.prototype.zoomMoveConsumed=function(){
  this.zoomMoveConsumedFlag=true;
  if(this.zoomMovePending!=0){
    this.callBack('zoomMove',{delta:this.zoomMovePending});
    this.zoomMoveConsumedFlag=false;
    this.zoomMovePending=0;
  }
}

AmiAxisPortlet.prototype.setZoom=function(x,y,delta){
  var pos=this.orientation=='L' || this.orientation=='R' ? y : x;
  this.callBack('zoom',{pos:pos,delta:delta});
}
AmiAxisPortlet.prototype.onMousewheel=function(e,delta){
  var p=getMousePointRelativeTo(e,this.canvas);
  this.setZoom(p.x,p.y,delta);
}
AmiAxisPortlet.prototype.onDblclick=function(e){
  var p=getMousePointRelativeTo(e,this.canvas);
  this.setZoom(p.x,p.y,5);
}

AmiAxisPortlet.prototype.init=function(options){
  this.bgColor=options.bgColor;
  this.lineColor=options.lineColor;
  this.reverse=options.reverse;
  this.logBase=options.logBase;
  this.minorUnit=options.minorUnit;
  this.minorUnitCount=options.minorUnitCount;
  this.orientation=options.orientation;
  
  this.title=options.title;
  this.titleColor=options.titleColor;
  this.titleFont=options.titleFont;
  this.titlePadding=options.titlePadding;
  this.titleRotate=options.titleRotate;
  this.titleSize=options.titleSize;
  
  this.labelFontSize=options.labelFontSize;
  this.labelFontHeight=this.labelFontSize/3;
  this.labelFontFamily=options.labelFontFamily;
  this.labelRotate=clip(options.labelRotate,-90,90);
  this.labelFontColor=options.labelFontColor;
  this.labelPadding=options.labelPadding;
  this.labelFontStyle=options.labelFontStyle;
  
  this.numberFontSize=options.numberFontSize;
  this.numberFontHeight=this.numberFontSize/3;
  this.numberFontFamily=options.numberFontFamily;
  this.numberRotate=clip(options.numberRotate,-90,90);
  this.numberFontColor=options.numberFontColor;
  this.numberPadding=options.numberPadding;
  
  this.labelAlign=this.toAlign(this.labelRotate);
  this.labelBaseAlign=this.toBaseAlign(this.labelRotate);
  
  this.titleAlign=this.toAlign(this.titleRotate);
  this.titleBaseAlign=this.toBaseAlign(this.titleRotate);
  
  this.numberAlign=this.toAlign(this.numberRotate);
  this.numberBaseAlign=this.toBaseAlign(this.numberRotate);
  
  this.majorUnitSize=options.majorUnitSize;
  this.minorUnitSize=options.minorUnitSize;
  this.labelTickSize=options.labelTickSize;
  
//  this.zoomOffset=options.zoomOffset;
//  this.zoom=options.zoom;
  if(this.orientation==='T'){
    this.canvasMap=new CanvasMap(this.canvas,false,this.false,true);
  }else if(this.orientation==='B'){
    this.canvasMap=new CanvasMap(this.canvas,true,this.false,true);
  }else if(this.orientation==='L'){
    this.canvasMap=new CanvasMap(this.canvas,false,this.false,false);
  }else if(this.orientation==='R'){
    this.canvasMap=new CanvasMap(this.canvas,true,this.false,false);
  }
}

AmiAxisPortlet.prototype.toAlign=function(n){
  return n===-90 || n===90 ? 0 : -1;
}
AmiAxisPortlet.prototype.toBaseAlign=function(n){
  return n===-90 ? -1 : n===90 ? 1 : 0;
}

AmiAxisPortlet.prototype.setLabels=function(groupingSize,size,labels,numbers){
  this.groupingSize=groupingSize;
  this.labelsCount=size;
  this.labels=labels;
  this.numbers=numbers;
  this.drawLines();
}


AmiAxisPortlet.prototype.drawLines=function(){
  if(this.canvasMap==null)
    return;
  this.divElement.style.background=this.bgColor;
  this.context.clearRect(-1,-1,this.width+2,this.height+2);
  this.context.beginPath();
  this.context.lineWidth = 1;
  this.context.lineJoin="round";
  this.context.strokeStyle=this.lineColor;
  this.context.fillStyle=this.labelFontColor;
  
  //LABELS
  this.context.fillStyle=this.labelFontColor;
  this.context.font=this.labelFontStyle+" "+this.labelFontSize+"px "+this.labelFontFamily;
  var lastPos=0;
  var len=this.canvasMap.getHeight();
  for(var i=0;i<this.labels.length;i++){
  	var label=this.labels[i];
  	if(label.n==null)
  		continue;
  	var pos=this.zoomOffset+label.l+(this.reverse ? -this.groupingSize : +this.groupingSize ) /2;
  	var t=rd(pos);
  	if(t>=0 && t<len){
      this.canvasMap.moveTo(0,t);
      this.canvasMap.lineTo(this.labelTickSize,t);
      this.canvasMap.drawText(label.n,this.labelTickSize+this.labelPadding,pos,this.labelRotate,this.labelAlign,this.labelBaseAlign);
  	} }
  this.context.stroke();
  
  //NUMBERS
  this.context.fillStyle=this.numberFontColor;
  this.context.font=this.numberFontSize+"px "+this.numberFontFamily;
  lastPos=0;
  for(var i=0;i<this.labels.length;i++){
  	var pos=this.zoomOffset+this.labels[i].l;
    for(var j=0;j<this.numbers.length;j++){
  	  var num=this.numbers[j];
  	  var pos2=num.p+pos;
  	  var t=rd(pos2);
      if(num.n!=null && t>0 && t<len){
        this.canvasMap.moveTo(0,t);
        this.canvasMap.lineTo(this.majorUnitSize,t);
        this.canvasMap.drawText(num.n,this.majorUnitSize+this.numberPadding,t,this.numberRotate,this.numberAlign,this.numberBaseAlign);
      }
      if(this.minorUnitCount>0 && j+1<this.numbers.length){
    	var max=abs(this.numbers[j+1].p-this.numbers[j].p);
        for(var k=1;;k++){
          var n=k*this.minorUnit;
  	      if(n>=max)
  	    	  break;
  	      var pos3=rd(pos2 + (this.reverse ? -n : n));
  	      if(pos3>=0 && pos3<=len){
            this.canvasMap.moveTo(0,pos3);
            this.canvasMap.lineTo(this.minorUnitSize,pos3);
  	      }
        }
      }
    }
  }
  this.context.stroke();
  
  //TITLE
  this.context.fillStyle=this.titleColor;
  this.context.font=this.labelFontStyle+" "+this.titleSize+"px "+this.titleFont;
  this.canvasMap.drawText(this.title,this.majorUnitSize+this.titlePadding,this.canvasMap.getHeight()/2,this.titleRotate,this.titleAlign,this.titleBaseAlign);
  this.context.stroke();
  
}

AmiAxisPortlet.prototype.setSize=function(width,height){
    this.width=width;
    this.height=height;
	this.divElement.style.width=toPx(width);
	this.divElement.style.height=toPx(height);
	this.canvas.style.width=toPx(width);
	this.canvas.style.height=toPx(height);
	this.canvas.width=width;
	this.canvas.height=height;
    this.context.translate(.5,.5);
    if(this.canvasMap!=null){
      this.canvasMap.updateCanvasSize();
	  this.drawLines();
    }
}
AmiAxisPortlet.prototype.handleKeydown=function(e){
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
	  if(this.orientation=='L'  || this.orientation=='R' ){
	    if(e.key=="ArrowUp")
		    this.onDrag(this.lastDragPos+distance);
	    else if(e.key=="ArrowDown")
		  this.onDrag(this.lastDragPos-distance);
	  }else{
	    if(e.key=="ArrowLeft")
		  this.onDrag(this.lastDragPos+distance);
	    else if(e.key=="ArrowRight")
		  this.onDrag(this.lastDragPos-distance);
	  }
	}
//	if(e.key=="Control")
//		this.canvas.style.cursor="move";
}

