package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ami_axis_js_1 extends AbstractHttpHandler{

	public amiweb_ami_axis_js_1() {
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
            "function AmiAxisPortlet(portletId,parentId){\r\n"+
            "  var that=this;\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.isContainer=false;\r\n"+
            "  this.divElement.style.background='#CCCCCC';\r\n"+
            "  this.canvas=nw('canvas');\r\n"+
            "  this.canvas.width=500;\r\n"+
            "  this.canvas.height=256;\r\n"+
            "  this.context = this.canvas.getContext('2d');\r\n"+
            "  this.divElement.appendChild(this.canvas);\r\n"+
            "  this.canvas.onMouseWheel=function(e,delta){that.onMousewheel(e,delta);};\r\n"+
            "  this.canvas.ondblclick=function(e){that.onDblclick(e)};\r\n"+
            "  this.zoomMoveConsumedFlag=true;\r\n"+
            "  this.zoomMovePending=0;\r\n"+
            "  this.lastDragPos=0;\r\n"+
            "  makeDraggable(this.canvas,this,true,true);\r\n"+
            "}\r\n"+
            "AmiAxisPortlet.prototype.ondragging=function(element,dx,dy,e){\r\n"+
            "  this.onDrag(this.orientation=='L' || this.orientation=='R' ? dy : dx);\r\n"+
            "}\r\n"+
            "AmiAxisPortlet.prototype.ondraggingEnd=function(element,dx,dy,e){\r\n"+
            "  this.onDrag(this.orientation=='L' || this.orientation=='R' ? dy : dx);\r\n"+
            "  this.lastDragPos=0;\r\n"+
            "}\r\n"+
            "AmiAxisPortlet.prototype.setZoomOffset=function(zoomOffset){\r\n"+
            "	this.zoomOffset=zoomOffset;\r\n"+
            "}\r\n"+
            "AmiAxisPortlet.prototype.onDrag=function(pos){\r\n"+
            "  var delta=pos-this.lastDragPos;\r\n"+
            "  if(delta==0)\r\n"+
            "	  return;\r\n"+
            "  this.lastDragPos=pos;\r\n"+
            "  this.zoomMovePending+=delta;\r\n"+
            "  if(this.zoomMoveConsumedFlag && this.zoomMovePending!=0){\r\n"+
            "    this.callBack('zoomMove',{delta:this.zoomMovePending});\r\n"+
            "    this.zoomMoveConsumedFlag=false;\r\n"+
            "    this.zoomMovePending=0;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "AmiAxisPortlet.prototype.zoomMoveConsumed=function(){\r\n"+
            "  this.zoomMoveConsumedFlag=true;\r\n"+
            "  if(this.zoomMovePending!=0){\r\n"+
            "    this.callBack('zoomMove',{delta:this.zoomMovePending});\r\n"+
            "    this.zoomMoveConsumedFlag=false;\r\n"+
            "    this.zoomMovePending=0;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiAxisPortlet.prototype.setZoom=function(x,y,delta){\r\n"+
            "  var pos=this.orientation=='L' || this.orientation=='R' ? y : x;\r\n"+
            "  this.callBack('zoom',{pos:pos,delta:delta});\r\n"+
            "}\r\n"+
            "AmiAxisPortlet.prototype.onMousewheel=function(e,delta){\r\n"+
            "  var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "  this.setZoom(p.x,p.y,delta);\r\n"+
            "}\r\n"+
            "AmiAxisPortlet.prototype.onDblclick=function(e){\r\n"+
            "  var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "  this.setZoom(p.x,p.y,5);\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiAxisPortlet.prototype.init=function(options){\r\n"+
            "  this.bgColor=options.bgColor;\r\n"+
            "  this.lineColor=options.lineColor;\r\n"+
            "  this.reverse=options.reverse;\r\n"+
            "  this.logBase=options.logBase;\r\n"+
            "  this.minorUnit=options.minorUnit;\r\n"+
            "  this.minorUnitCount=options.minorUnitCount;\r\n"+
            "  this.orientation=options.orientation;\r\n"+
            "  \r\n"+
            "  this.title=options.title;\r\n"+
            "  this.titleColor=options.titleColor;\r\n"+
            "  this.titleFont=options.titleFont;\r\n"+
            "  this.titlePadding=options.titlePadding;\r\n"+
            "  this.titleRotate=options.titleRotate;\r\n"+
            "  this.titleSize=options.titleSize;\r\n"+
            "  \r\n"+
            "  this.labelFontSize=options.labelFontSize;\r\n"+
            "  this.labelFontHeight=this.labelFontSize/3;\r\n"+
            "  this.labelFontFamily=options.labelFontFamily;\r\n"+
            "  this.labelRotate=clip(options.labelRotate,-90,90);\r\n"+
            "  this.labelFontColor=options.labelFontColor;\r\n"+
            "  this.labelPadding=options.labelPadding;\r\n"+
            "  this.labelFontStyle=options.labelFontStyle;\r\n"+
            "  \r\n"+
            "  this.numberFontSize=options.numberFontSize;\r\n"+
            "  this.numberFontHeight=this.numberFontSize/3;\r\n"+
            "  this.numberFontFamily=options.numberFontFamily;\r\n"+
            "  this.numberRotate=clip(options.numberRotate,-90,90);\r\n"+
            "  this.numberFontColor=options.numberFontColor;\r\n"+
            "  this.numberPadding=options.numberPadding;\r\n"+
            "  \r\n"+
            "  this.labelAlign=this.toAlign(this.labelRotate);\r\n"+
            "  this.labelBaseAlign=this.toBaseAlign(this.labelRotate);\r\n"+
            "  \r\n"+
            "  this.titleAlign=this.toAlign(this.titleRotate);\r\n"+
            "  this.titleBaseAlign=this.toBaseAlign(this.titleRotate);\r\n"+
            "  \r\n"+
            "  this.numberAlign=this.toAlign(this.numberRotate);\r\n"+
            "  this.numberBaseAlign=this.toBaseAlign(this.numberRotate);\r\n"+
            "  \r\n"+
            "  this.majorUnitSize=options.majorUnitSize;\r\n"+
            "  this.minorUnitSize=options.minorUnitSize;\r\n"+
            "  this.labelTickSize=options.labelTickSize;\r\n"+
            "  \r\n"+
            "//  this.zoomOffset=options.zoomOffset;\r\n"+
            "//  this.zoom=options.zoom;\r\n"+
            "  if(this.orientation==='T'){\r\n"+
            "    this.canvasMap=new CanvasMap(this.canvas,false,this.false,true);\r\n"+
            "  }else if(this.orientation==='B'){\r\n"+
            "    this.canvasMap=new CanvasMap(this.canvas,true,this.false,true);\r\n"+
            "  }else if(this.orientation==='L'){\r\n"+
            "    this.canvasMap=new CanvasMap(this.canvas,false,this.false,false);\r\n"+
            "  }else if(this.orientation==='R'){\r\n"+
            "    this.canvasMap=new CanvasMap(this.canvas,true,this.false,false);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiAxisPortlet.prototype.toAlign=function(n){\r\n"+
            "  return n===-90 || n===90 ? 0 : -1;\r\n"+
            "}\r\n"+
            "AmiAxisPortlet.prototype.toBaseAlign=function(n){\r\n"+
            "  return n===-90 ? -1 : n===90 ? 1 : 0;\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiAxisPortlet.prototype.setLabels=function(groupingSize,size,labels,numbers){\r\n"+
            "  this.groupingSize=groupingSize;\r\n"+
            "  this.labelsCount=size;\r\n"+
            "  this.labels=labels;\r\n"+
            "  this.numbers=numbers;\r\n"+
            "  this.drawLines();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "AmiAxisPortlet.prototype.drawLines=function(){\r\n"+
            "  if(this.canvasMap==null)\r\n"+
            "    return;\r\n"+
            "  this.divElement.style.background=this.bgColor;\r\n"+
            "  this.context.clearRect(-1,-1,this.width+2,this.height+2);\r\n"+
            "  this.context.beginPath();\r\n"+
            "  this.context.lineWidth = 1;\r\n"+
            "  this.context.lineJoin=\"round\";\r\n"+
            "  this.context.strokeStyle=this.lineColor;\r\n"+
            "  this.context.fillStyle=this.labelFontColor;\r\n"+
            "  \r\n"+
            "  //LABELS\r\n"+
            "  this.context.fillStyle=this.labelFontColor;\r\n"+
            "  this.context.font=this.labelFontStyle+\" \"+this.labelFontSize+\"px \"+this.labelFontFamily;\r\n"+
            "  var lastPos=0;\r\n"+
            "  var len=this.canvasMap.getHeight();\r\n"+
            "  for(var i=0;i<this.labels.length;i++){\r\n"+
            "  	var label=this.labels[i];\r\n"+
            "  	if(label.n==null)\r\n"+
            "  		continue;\r\n"+
            "  	var pos=this.zoomOffset+label.l+(this.reverse ? -this.groupingSize : +this.groupingSize ) /2;\r\n"+
            "  	var t=rd(pos);\r\n"+
            "  	if(t>=0 && t<len){\r\n"+
            "      this.canvasMap.moveTo(0,t);\r\n"+
            "      this.canvasMap.lineTo(this.labelTickSize,t);\r\n"+
            "      this.canvasMap.drawText(label.n,this.labelTickSize+this.labelPadding,pos,this.labelRotate,this.labelAlign,this.labelBaseAlign);\r\n"+
            "  	} }\r\n"+
            "  this.context.stroke();\r\n"+
            "  \r\n"+
            "  //NUMBERS\r\n"+
            "  this.context.fillStyle=this.numberFontColor;\r\n"+
            "  this.context.font=this.numberFontSize+\"px \"+this.numberFontFamily;\r\n"+
            "  lastPos=0;\r\n"+
            "  for(var i=0;i<this.labels.length;i++){\r\n"+
            "  	var pos=this.zoomOffset+this.labels[i].l;\r\n"+
            "    for(var j=0;j<this.numbers.length;j++){\r\n"+
            "  	  var num=this.numbers[j];\r\n"+
            "  	  var pos2=num.p+pos;\r\n"+
            "  	  var t=rd(pos2);\r\n"+
            "      if(num.n!=null && t>0 && t<len){\r\n"+
            "        this.canvasMap.moveTo(0,t);\r\n"+
            "        this.canvasMap.lineTo(this.majorUnitSize,t);\r\n"+
            "        this.canvasMap.drawText(num.n,this.majorUnitSize+this.numberPadding,t,this.numberRotate,this.numberAlign,this.numberBaseAlign);\r\n"+
            "      }\r\n"+
            "      if(this.minorUnitCount>0 && j+1<this.numbers.length){\r\n"+
            "    	var max=abs(this.numbers[j+1].p-this.numbers[j].p);\r\n"+
            "        for(var k=1;;k++){\r\n"+
            "          var n=k*this.minorUnit;\r\n"+
            "  	      if(n>=max)\r\n"+
            "  	    	  break;\r\n"+
            "  	      var pos3=rd(pos2 + (this.reverse ? -n : n));\r\n"+
            "  	      if(pos3>=0 && pos3<=len){\r\n"+
            "            this.canvasMap.moveTo(0,pos3);\r\n"+
            "            this.canvasMap.lineTo(this.minorUnitSize,pos3);\r\n"+
            "  	      }\r\n"+
            "        }\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  this.context.stroke();\r\n"+
            "  \r\n"+
            "  //TITLE\r\n"+
            "  this.context.fillStyle=this.titleColor;\r\n"+
            "  this.context.font=this.labelFontStyle+\" \"+this.titleSize+\"px \"+this.titleFont;\r\n"+
            "  this.canvasMap.drawText(this.title,this.majorUnitSize+this.titlePadding,this.canvasMap.getHeight()/2,this.titleRotate,this.titleAlign,this.titleBaseAlign);\r\n"+
            "  this.context.stroke();\r\n"+
            "  \r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiAxisPortlet.prototype.setSize=function(width,height){\r\n"+
            "    this.width=width;\r\n"+
            "    this.height=height;\r\n"+
            "	this.divElement.style.width=toPx(width);\r\n"+
            "	this.divElement.style.height=toPx(height);\r\n"+
            "	this.canvas.style.width=toPx(width);\r\n"+
            "	this.canvas.style.height=toPx(height);\r\n"+
            "	this.canvas.width=width;\r\n"+
            "	this.canvas.height=height;\r\n"+
            "    this.context.translate(.5,.5);\r\n"+
            "    if(this.canvasMap!=null){\r\n"+
            "      this.canvasMap.updateCanvasSize();\r\n"+
            "	  this.drawLines();\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "AmiAxisPortlet.prototype.handleKeydown=function(e){\r\n"+
            "	if(e.key==\" \"){\r\n"+
            "		    this.setZoom(this.canvas.width/2,this.canvas.height/2,-100);\r\n"+
            "	}else if(e.ctrlKey){\r\n"+
            "	  var distance=e.shiftKey ? 3 : 1;\r\n"+
            "	  if(e.key==\"ArrowUp\" || e.key==\"ArrowRight\")\r\n"+
            "		    this.setZoom(this.canvas.width/2,this.canvas.height/2,distance);\r\n"+
            "	  if(e.key==\"ArrowDown\"|| e.key==\"ArrowLeft\")\r\n"+
            "		    this.setZoom(this.canvas.width/2,this.canvas.height/2,-distance);\r\n"+
            "	}else{\r\n"+
            "	  var distance=e.shiftKey ? 100 : 10;\r\n"+
            "	  if(this.orientation=='L'  || this.orientation=='R' ){\r\n"+
            "	    if(e.key==\"ArrowUp\")\r\n"+
            "		    this.onDrag(this.lastDragPos+distance);\r\n"+
            "	    else if(e.key==\"ArrowDown\")\r\n"+
            "		  this.onDrag(this.lastDragPos-distance);\r\n"+
            "	  }else{\r\n"+
            "	    if(e.key==\"ArrowLeft\")\r\n"+
            "		  this.onDrag(this.lastDragPos+distance);\r\n"+
            "	    else if(e.key==\"ArrowRight\")\r\n"+
            "		  this.onDrag(this.lastDragPos-distance);\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "//	if(e.key==\"Control\")\r\n"+
            "//		this.canvas.style.cursor=\"move\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "");

	}
	
}