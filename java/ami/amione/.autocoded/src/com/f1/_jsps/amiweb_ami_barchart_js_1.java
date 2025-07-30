package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ami_barchart_js_1 extends AbstractHttpHandler{

	public amiweb_ami_barchart_js_1() {
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
            "AmiChartLayer_Bar.prototype.LINE_TYPE_DIRECT = 0;\r\n"+
            "AmiChartLayer_Bar.prototype.LINE_TYPE_HORZ = 1;\r\n"+
            "AmiChartLayer_Bar.prototype.LINE_TYPE_VERT = 2;\r\n"+
            "\r\n"+
            "AmiChartLayer_Bar.prototype.setSize=function(width,height){\r\n"+
            "	this.canvas.style.width=toPx(width);\r\n"+
            "	this.canvas.style.height=toPx(height);\r\n"+
            "	this.canvas.width=width;\r\n"+
            "	this.canvas.height=height;\r\n"+
            "    this.context.translate(.5,.5);\r\n"+
            "    if(this.canvasMap!=null){\r\n"+
            "      this.canvasMap.updateCanvasSize();\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function AmiChartLayer_Bar(){\r\n"+
            "  this.divElement=nw('div');\r\n"+
            "  var that=this;\r\n"+
            "  this.isContainer=false;\r\n"+
            "  this.fontStyle=\"\";\r\n"+
            "  this.fontSize=14;\r\n"+
            "  this.fontFamily=\"arial\";\r\n"+
            "  this.canvas=nw('canvas');\r\n"+
            "  this.canvas.style.position='absolute';\r\n"+
            "  this.context = this.canvas.getContext('2d');\r\n"+
            "  this.context.translate(.5,.5);\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiChartLayer_Bar.prototype.plot;\r\n"+
            "\r\n"+
            "AmiChartLayer_Bar.prototype.clearData=function(){\r\n"+
            "  this.series={};\r\n"+
            "  this.groupsByPosition=[];\r\n"+
            "}\r\n"+
            "AmiChartLayer_Bar.prototype.init=function(options){\r\n"+
            "  this.isInit=true;\r\n"+
            "  this.canvas.style.opacity=options.opacity/100;\r\n"+
            "  this.hGrid=options.hGrid;\r\n"+
            "  this.vGrid=options.vGrid;\r\n"+
            "  this.hMajorGrid=options.hMajorGrid;\r\n"+
            "  this.vMajorGrid=options.vMajorGrid;\r\n"+
            "  this.hGroupSize=options.hGroupSize;\r\n"+
            "  this.vGroupSize=options.vGroupSize;\r\n"+
            "  this.hGridColor=options.hGridColor;\r\n"+
            "  this.vGridColor=options.vGridColor;\r\n"+
            "  this.hMidGridColor=options.hMidGridColor;\r\n"+
            "  this.vMidGridColor=options.vMidGridColor;\r\n"+
            "  this.hMajorGridColor=options.hMajorGridColor;\r\n"+
            "  this.vMajorGridColor=options.vMajorGridColor;\r\n"+
            "  \r\n"+
            "  this.hGridSize=options.hGridSize;\r\n"+
            "  this.vGridSize=options.vGridSize;\r\n"+
            "  this.hMidGridSize=options.hMidGridSize;\r\n"+
            "  this.vMidGridSize=options.vMidGridSize;\r\n"+
            "  this.hMajorGridSize=options.hMajorGridSize;\r\n"+
            "  this.vMajorGridSize=options.vMajorGridSize;\r\n"+
            "  \r\n"+
            "  this.borderColor=options.borderColor;\r\n"+
            "  this.flipX=options.flipX;\r\n"+
            "  this.posZoomX=options.posZoomX;\r\n"+
            "  this.flipY=options.flipY;\r\n"+
            "  this.posZoomY=options.posZoomY;\r\n"+
            "  this.posOffsetX=options.posOffsetX;\r\n"+
            "  this.posOffsetY=options.posOffsetY;\r\n"+
            "  this.canvasMap=new CanvasMap(this.canvas,this.flipX,this.flipY,false);\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiChartLayer_Bar.prototype.drawGrid=function(gridContext,gridCanvas){\r\n"+
            "  this.canvasMap.reset(gridCanvas,this.flipX,this.flipY,false);\r\n"+
            "  gridContext.lineWidth = 1;\r\n"+
            "  gridContext.lineJoin=\"miter\";\r\n"+
            "  this.canvasMap.setFont(this.fontStyle,this.fontSize,this.fontFamily);\r\n"+
            "  if(this.vMajorGridColor!=null){\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    gridContext.lineWidth = this.vMajorGridSize;\r\n"+
            "    gridContext.strokeStyle=this.vMajorGridColor;\r\n"+
            "	for(i in this.vGrid){\r\n"+
            "	  var t1=this.vGrid[i]-this.vGroupSize/2;\r\n"+
            "	  for(j in this.vMajorGrid){\r\n"+
            "	    var t=rd(t1+this.vMajorGrid[j]);\r\n"+
            "	    this.canvasMap.moveTo(0,t);\r\n"+
            "	    this.canvasMap.lineTo(this.canvasMap.getWidth(),t);\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "    gridContext.stroke();\r\n"+
            "  }\r\n"+
            "  if(this.vMidGridColor!=null){\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    gridContext.lineWidth = this.vMidGridSize;\r\n"+
            "    gridContext.strokeStyle=this.vMidGridColor;\r\n"+
            "    var last=null;\r\n"+
            "	for(i in this.vGrid){\r\n"+
            "	  var val=this.vGrid[i];\r\n"+
            "	  if(last!=null){\r\n"+
            "	    var t=rd((val+last)/2);\r\n"+
            "	    this.canvasMap.moveTo(0,t);\r\n"+
            "	    this.canvasMap.lineTo(this.canvasMap.getWidth(),t);\r\n"+
            "	  }\r\n"+
            "	  last=val;\r\n"+
            "	}\r\n"+
            "    gridContext.stroke();\r\n"+
            "  }\r\n"+
            "  if(this.vGridColor!=null){\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    gridContext.lineWidth = this.vGridSize;\r\n"+
            "    gridContext.strokeStyle=this.vGridColor;\r\n"+
            "	for(var i in this.vGrid){\r\n"+
            "	  var t=rd(this.vGrid[i]);\r\n"+
            "	  this.canvasMap.moveTo(0,t);\r\n"+
            "	  this.canvasMap.lineTo(this.canvasMap.getWidth(),t);\r\n"+
            "	}\r\n"+
            "    gridContext.stroke();\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  if(this.hMajorGridColor!=null){\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    gridContext.lineWidth = this.hMajorGridSize;\r\n"+
            "    gridContext.strokeStyle=this.hMajorGridColor;\r\n"+
            "	for(i in this.hGrid){\r\n"+
            "	  var t1=this.hGrid[i]-this.hGroupSize/2;\r\n"+
            "	  for(j in this.hMajorGrid){\r\n"+
            "	    var t=rd(t1+this.hMajorGrid[j]);\r\n"+
            "	    this.canvasMap.moveTo(t,0);\r\n"+
            "	    this.canvasMap.lineTo(t,this.canvasMap.getHeight());\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "    gridContext.stroke();\r\n"+
            "  }\r\n"+
            "  if(this.hMidGridColor!=null){\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    gridContext.lineWidth = this.hMidGridSize;\r\n"+
            "    gridContext.strokeStyle=this.hMidGridColor;\r\n"+
            "    var last=null;\r\n"+
            "	for(i in this.hGrid){\r\n"+
            "	  var val=this.hGrid[i];\r\n"+
            "	  if(last!=null){\r\n"+
            "	    var t=rd((val+last)/2);\r\n"+
            "	    this.canvasMap.moveTo(t,0);\r\n"+
            "	    this.canvasMap.lineTo(t,this.canvasMap.getHeight());\r\n"+
            "	  }\r\n"+
            "	  last=val;\r\n"+
            "	}\r\n"+
            "    gridContext.stroke();\r\n"+
            "  }\r\n"+
            "  if(this.hGridColor!=null){\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    gridContext.lineWidth = this.hGridSize;\r\n"+
            "    gridContext.strokeStyle=this.hGridColor;\r\n"+
            "	for(i in this.hGrid){\r\n"+
            "	  var t=rd(this.hGrid[i]);\r\n"+
            "	  this.canvasMap.moveTo(t,0);\r\n"+
            "	  this.canvasMap.lineTo(t,this.canvasMap.getHeight());\r\n"+
            "	}\r\n"+
            "    gridContext.stroke();\r\n"+
            "  }\r\n"+
            "  this.canvasMap.rect(1,1,this.canvasMap.getWidth()+1,this.canvasMap.getHeight()+1);\r\n"+
            "  gridContext.clip();\r\n"+
            "  if(this.borderColor!=null){\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    gridContext.strokeStyle=this.borderColor\r\n"+
            "    this.canvasMap.strokeRect(this.lPadding,this.tPadding,this.canvasMap.getWidth()-this.lPadding-this.rPadding-1,this.canvasMap.getHeight()-this.tPadding-this.bPadding-1);\r\n"+
            "    gridContext.stroke();\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiChartLayer_Bar.prototype.draw=function(){\r\n"+
            "  var start=Date.now();\r\n"+
            "  this.canvasMap.reset(this.canvas,this.flipX,this.flipY,false);\r\n"+
            "  this.canvasMap.clear();\r\n"+
            "  this.canvasMouseManager.clearForTarget(this);\r\n"+
            "  if(this.canvasMap==null || this.isInit===false || this.groupsByPosition==null)\r\n"+
            "    return;\r\n"+
            "  for(var j=this.groupsByPosition.length-1;j>=0;j--){\r\n"+
            "	var groups=this.groupsByPosition[j];\r\n"+
            "	if (groups == null)\r\n"+
            "		continue;\r\n"+
            "	for(var k=groups.length-1;k>=0;k--){\r\n"+
            "	  var ser=groups[k];\r\n"+
            "      var idPrefix=ser.seriesId+'.'+ser.groupId+'.';\r\n"+
            "      var lineType=ser.lineType;\r\n"+
            "	  var xPos=ser.xPos;\r\n"+
            "	  var yPos=ser.yPos;\r\n"+
            "	  var x2Pos=ser.x2Pos;\r\n"+
            "	  var y2Pos=ser.y2Pos;\r\n"+
            "	  var mColor=ser.mColor;\r\n"+
            "	  var mBorderColor=ser.mBorderColor;\r\n"+
            "	  var mBorderSize=ser.mBorderSize;\r\n"+
            "	  var mWidth=ser.mWidth;\r\n"+
            "	  var mHeight=ser.mHeight;\r\n"+
            "	  var mTop=ser.mTop;\r\n"+
            "	  var mBottom=ser.mBottom;\r\n"+
            "	  var mLeft=ser.mLeft;\r\n"+
            "	  var mRight=ser.mRight;\r\n"+
            "	  var mShape=ser.mShape;\r\n"+
            "	  var isSelectedList=ser.isSelected;\r\n"+
            "	  var isSelected;\r\n"+
            "	  if(isSelectedList!=null){\r\n"+
            "		  isSelected={};\r\n"+
            "		  for(var i=0;i<isSelectedList.length;i++)\r\n"+
            "			  isSelected[isSelectedList[i]]=true;\r\n"+
            "	  }else\r\n"+
            "		  isSelected=null;\r\n"+
            "	  var len=ser.size;\r\n"+
            "	  var sel=ser.sel;\r\n"+
            "	  var desc=ser.desc;\r\n"+
            "	  var descColor=ser.descColor;\r\n"+
            "	  var descSz=ser.descSz;\r\n"+
            "	  var descFontFam=ser.descFontFam;\r\n"+
            "	  var descPos=ser.descPos;\r\n"+
            "	  if(ser.lineSize!=null && ser.lineSize>0 && ser.lineColor!=null && xPos!=null & yPos!=null && x2Pos!=null & y2Pos!=null){\r\n"+
            "        this.context.beginPath();\r\n"+
            "        var oldX,oldY,oldX2,oldY2;\r\n"+
            "        var first=true;\r\n"+
            "        var lastFillStyle=null;\r\n"+
            "	    for(var i=0;i<len;i++){\r\n"+
            "          var x=deref(xPos,i);\r\n"+
            "          var y=deref(yPos,i);\r\n"+
            "          var x2=deref(x2Pos,i);\r\n"+
            "          var y2=deref(y2Pos,i);\r\n"+
            "          if(x==null || y==null)\r\n"+
            "        	  continue;\r\n"+
            "          x=rd(this.scaleX(x));\r\n"+
            "          y=rd(this.scaleY(y));\r\n"+
            "          x2=rd(this.scaleX(x2));\r\n"+
            "          y2=rd(this.scaleY(y2));\r\n"+
            "          if(!first){\r\n"+
            "			  \r\n"+
            "			  var p1x=oldX;\r\n"+
            "			  var p1y=oldY;\r\n"+
            "			  var p2x=x;\r\n"+
            "			  var p2y=y;\r\n"+
            "			  var p3x=x2;\r\n"+
            "			  var p3y=y2;\r\n"+
            "			  var p4x=oldX2;\r\n"+
            "			  var p4y=oldY2;\r\n"+
            "				  \r\n"+
            "			  var t=deref(ser.fillColor,i);\r\n"+
            "			  if(t!=null && oldX2!=null && oldY2!=null){\r\n"+
            "				if(t!=lastFillStyle){\r\n"+
            "			      this.context.fill();\r\n"+
            "                  this.context.fillStyle=lastFillStyle=t;\r\n"+
            "			      this.context.beginPath();\r\n"+
            "				}\r\n"+
            "			    this.canvasMap.moveTo(p1x,p1y);\r\n"+
            "			    if(lineType>this.LINE_TYPE_DIRECT)\r\n"+
            "			      this.canvasMap.lineTo(lineType==this.LINE_TYPE_VERT ? p1x : x,lineType==this.LINE_TYPE_VERT ? y : p1y);\r\n"+
            "			    this.canvasMap.lineTo(x,y);\r\n"+
            "			    this.canvasMap.lineTo(p3x,p3y);\r\n"+
            "			    if(lineType>this.LINE_TYPE_DIRECT)\r\n"+
            "			      this.canvasMap.lineTo(lineType==this.LINE_TYPE_VERT ? oldX2 : p3x,lineType==this.LINE_TYPE_VERT ? p3y : oldY2);\r\n"+
            "			    this.canvasMap.lineTo(oldX2,oldY2);\r\n"+
            "			  }\r\n"+
            "			  \r\n"+
            "		  }else\r\n"+
            "            first=false;\r\n"+
            "          oldX=x;\r\n"+
            "          oldY=y;\r\n"+
            "          oldX2=x2;\r\n"+
            "          oldY2=y2;\r\n"+
            "	    }\r\n"+
            "		this.context.fill();\r\n"+
            "	  }\r\n"+
            "	  if(ser.lineSize!=null && ser.lineSize>0 && ser.lineColor!=null && xPos!=null & yPos!=null){\r\n"+
            "        this.context.beginPath();\r\n"+
            "        var oldX,oldY,oldX2,oldY2;\r\n"+
            "        var first=true;\r\n"+
            "        var lastLineWidth=null;\r\n"+
            "        var lastStrokeStyle=null;\r\n"+
            "	    for(var i=0;i<len;i++){\r\n"+
            "          var x=deref(xPos,i);\r\n"+
            "          var y=deref(yPos,i);\r\n"+
            "          var x2=deref(x2Pos,i);\r\n"+
            "          var y2=deref(y2Pos,i);\r\n"+
            "          if(x==null || y==null)\r\n"+
            "        	  continue;\r\n"+
            "          x=rd(this.scaleX(x));\r\n"+
            "          y=rd(this.scaleY(y));\r\n"+
            "          x2=rd(this.scaleX(x2));\r\n"+
            "          y2=rd(this.scaleY(y2));\r\n"+
            "          \r\n"+
            "          if(!first){\r\n"+
            "			  \r\n"+
            "			  var p1x=oldX;\r\n"+
            "			  var p1y=oldY;\r\n"+
            "			  var p2x=x;\r\n"+
            "			  var p2y=y;\r\n"+
            "			  var p3x=x2;\r\n"+
            "			  var p3y=y2;\r\n"+
            "			  var p4x=oldX2;\r\n"+
            "			  var p4y=oldY2;\r\n"+
            "				  \r\n"+
            "			  var t=deref(ser.fillBorderSize,i);\r\n"+
            "			  var t2=deref(ser.fillBorderColor,i);\r\n"+
            "			  if(t>0 && t2&& oldX2!=null && oldY2!=null){\r\n"+
            "				if(lastLineWidth!=t || lastStrokeStyle!=t2){\r\n"+
            "                  this.context.stroke();\r\n"+
            "                  this.context.lineWidth=lastLineWidth=t;\r\n"+
            "                  this.context.strokeStyle=lastStrokeStyle=t2;\r\n"+
            "			      this.context.beginPath();\r\n"+
            "				}\r\n"+
            "			    this.canvasMap.moveTo(p2x,p2y);\r\n"+
            "			    this.canvasMap.lineTo(p3x,p3y);\r\n"+
            "			    this.canvasMap.moveTo(p4x,p4y);\r\n"+
            "			    this.canvasMap.lineTo(p1x,p1y);\r\n"+
            "			  }\r\n"+
            "              \r\n"+
            "			  t=deref(ser.lineSize,i);\r\n"+
            "			  t2=deref(ser.lineColor,i);\r\n"+
            "			  if(t>0 && t2){\r\n"+
            "				if(lastLineWidth!=t || lastStrokeStyle!=t2){\r\n"+
            "                  this.context.stroke();\r\n"+
            "                  this.context.lineWidth=lastLineWidth=t;\r\n"+
            "                  this.context.strokeStyle=la");
          out.print(
            "stStrokeStyle=t2;\r\n"+
            "			      this.context.beginPath();\r\n"+
            "				}\r\n"+
            "			    this.canvasMap.moveTo(oldX,oldY);\r\n"+
            "			    if(lineType>this.LINE_TYPE_DIRECT)\r\n"+
            "			      this.canvasMap.lineTo(lineType==this.LINE_TYPE_VERT ? oldX : x,lineType==this.LINE_TYPE_VERT ? y : oldY);\r\n"+
            "			    this.canvasMap.lineTo(x,y);\r\n"+
            "			  }\r\n"+
            "              \r\n"+
            "			  t=deref(ser.line2Size,i);\r\n"+
            "			  t2=deref(ser.line2Color,i);\r\n"+
            "			  if(t>0 && t2 && x2!=null && y2!=null){\r\n"+
            "				if(lastLineWidth!=t || lastStrokeStyle!=t2){\r\n"+
            "                  this.context.stroke();\r\n"+
            "                  this.context.lineWidth=lastLineWidth=t;\r\n"+
            "                  this.context.strokeStyle=lastStrokeStyle=t2;\r\n"+
            "			      this.context.beginPath();\r\n"+
            "				}\r\n"+
            "			    this.canvasMap.moveTo(p3x,p3y);\r\n"+
            "			    if(lineType>this.LINE_TYPE_DIRECT)\r\n"+
            "			      this.canvasMap.lineTo(lineType==this.LINE_TYPE_VERT ? oldX2 : p3x,lineType==this.LINE_TYPE_VERT ? p3y : oldY2);\r\n"+
            "			    this.canvasMap.lineTo(oldX2,oldY2);\r\n"+
            "			  }\r\n"+
            "		  }else\r\n"+
            "            first=false;\r\n"+
            "          oldX=x;\r\n"+
            "          oldY=y;\r\n"+
            "          oldX2=x2;\r\n"+
            "          oldY2=y2;\r\n"+
            "	    }\r\n"+
            "        this.context.stroke();\r\n"+
            "	  }\r\n"+
            "      this.context.beginPath();\r\n"+
            "      var lastLineWidth=null;\r\n"+
            "      var lastStrokeStyle=null;\r\n"+
            "      var lastFillStyle=null;\r\n"+
            "	  for(var i=0;i<len;i++){\r\n"+
            "        var x=deref(xPos,i);\r\n"+
            "        var y=deref(yPos,i);\r\n"+
            "        var strokeStyle=deref(mBorderColor,i);\r\n"+
            "        var borderSize=deref(mBorderSize,i);\r\n"+
            "        var fillStyle=deref(mColor,i);\r\n"+
            "        this.context.fillStyle=lastFillStyle=fillStyle;\r\n"+
            "        this.context.lineWidth=lastLineWidth=borderSize==null ? null : (borderSize*2);\r\n"+
            "        this.context.strokeStyle=lastStrokeStyle=strokeStyle;\r\n"+
            "		this.context.beginPath();\r\n"+
            "        var shape=deref(mShape,i);\r\n"+
            "	    var width=deref(mWidth,i)/2;\r\n"+
            "	    var height=deref(mHeight,i)/2;\r\n"+
            "	    \r\n"+
            "        x=this.scaleX(x);\r\n"+
            "        y=this.scaleY(y);\r\n"+
            "	    var _b=mBottom==null && y!=null && height!=null ? rd(y+height) : rd(this.scaleY(deref(mBottom,i)));\r\n"+
            "	    var _t=mTop   ==null && y!=null && height!=null ? rd(y-height) : rd(this.scaleY(deref(mTop,i)));\r\n"+
            "	    var _l=mLeft  ==null && x!=null && width!=null ? rd(x-width) : rd(this.scaleX(deref(mLeft,i)));\r\n"+
            "	    var _r=mRight ==null && x!=null && width!=null ? rd(x+width) : rd(this.scaleX(deref(mRight,i)));\r\n"+
            "	    if(_b==null || _t==null || _l==null || _r==null)\r\n"+
            "	    	continue;\r\n"+
            "	    \r\n"+
            "        var _w=_r-_l;\r\n"+
            "        var _h=_b-_t;\r\n"+
            "        _l-=.5; _t-=.5;\r\n"+
            "	    var shapeChar=null;\r\n"+
            "	    if(shape && (strokeStyle||fillStyle)){\r\n"+
            "          if(shape=='circle'){\r\n"+
            "            if(borderSize>0 && borderSize>Math.abs(_w) || borderSize>Math.abs(_h)){//catch border bug\r\n"+
            "        	  var tw=Math.abs(_w/2)+borderSize;\r\n"+
            "        	  var th=Math.abs(_h/2)+borderSize;\r\n"+
            "              this.context.beginPath();\r\n"+
            "              this.context.fillStyle=strokeStyle;\r\n"+
            "		      this.canvasMap.fillOval(_l-borderSize,_t-borderSize,_w+1+borderSize*2,_h+1+borderSize*2);\r\n"+
            "              this.context.fill();\r\n"+
            "              this.context.beginPath();\r\n"+
            "              this.context.fillStyle=fillStyle;\r\n"+
            "		      this.canvasMap.fillOval(_l,_t,_w,_h);\r\n"+
            "              this.context.fill();\r\n"+
            "              this.context.beginPath();\r\n"+
            "            }else{\r\n"+
            "		      this.canvasMap.fillOval(_l,_t,_w,_h);\r\n"+
            "              if(borderSize>0 && strokeStyle)\r\n"+
            "                this.context.stroke();\r\n"+
            "              if(fillStyle)\r\n"+
            "                this.context.fill();\r\n"+
            "            }\r\n"+
            "		    shapeChar='c';\r\n"+
            "          }else if(shape=='square'){\r\n"+
            "		    this.canvasMap.rect(_l,_t,_w,_h);\r\n"+
            "            if(borderSize>0 && strokeStyle)\r\n"+
            "              this.context.stroke();\r\n"+
            "          if(fillStyle)\r\n"+
            "            this.context.fill();\r\n"+
            "		    shapeChar='r';\r\n"+
            "          }else if(shape=='triangle'){\r\n"+
            "            this.canvasMap.moveTo(rd((_r+_l)/2),_t);\r\n"+
            "            this.canvasMap.lineTo(_r,_b-.5);\r\n"+
            "            this.canvasMap.lineTo(_l,_b-.5);\r\n"+
            "            this.canvasMap.closePath();\r\n"+
            "            if(borderSize>0 && strokeStyle)\r\n"+
            "              this.context.stroke();\r\n"+
            "            if(fillStyle)\r\n"+
            "              this.context.fill();\r\n"+
            "		    shapeChar='t';\r\n"+
            "          }else if(shape=='hbar'){\r\n"+
            "        	 _l=-.5;\r\n"+
            "        	 _r=_w=this.canvas.width;\r\n"+
            "		    this.canvasMap.rect(_l,_t,_w,_h);\r\n"+
            "		    shapeChar='r';\r\n"+
            "            if(borderSize>0 && strokeStyle)\r\n"+
            "              this.context.stroke();\r\n"+
            "            if(fillStyle)\r\n"+
            "              this.context.fill();\r\n"+
            "          }else if(shape=='vbar'){\r\n"+
            "        	_t=-.5;\r\n"+
            "        	_h=_b=this.canvas.height;\r\n"+
            "		    this.canvasMap.rect(_l,_t,_w,_h);\r\n"+
            "		    shapeChar='r';\r\n"+
            "            if(borderSize>0 && strokeStyle)\r\n"+
            "              this.context.stroke();\r\n"+
            "            if(fillStyle)\r\n"+
            "              this.context.fill();\r\n"+
            "          }\r\n"+
            "          if(shapeChar!=null)\r\n"+
            "        	this.canvasMouseManager.addRect(this.canvasMap.mapX(_l,_t),this.canvasMap.mapY(_l,_t),this.canvasMap.mapW(_w,_h),this.canvasMap.mapH(_w,_h),this,idPrefix+i,isSelected!=null && isSelected[i],false != deref(sel,i),shapeChar);\r\n"+
            "	    }\r\n"+
            "        var description=deref(desc,i);\r\n"+
            "        if(description!=null){\r\n"+
            "          var pos=deref(descPos,i);\r\n"+
            "          this.canvasMap.setFont(this.fontStyle,deref(descSz),descFontFam);\r\n"+
            "		  this.context.beginPath();\r\n"+
            "          var descriptionColor=deref(descColor,i);\r\n"+
            "          this.context.fillStyle=descriptionColor;\r\n"+
            "          if(pos==null || pos=='center'){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,(_b+_t)/2,0,ALIGN_MIDDLE,ALIGN_MIDDLE);\r\n"+
            "          }if(pos==(this.flipY ? 'bottom' : 'top')){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,min(_b,_t),0,ALIGN_MIDDLE,!this.flipY ? ALIGN_BOTTOM : ALIGN_TOP);\r\n"+
            "          }else if(pos==(this.flipY ? 'top' : 'bottom')){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,max(_b,_t),0,ALIGN_MIDDLE,this.flipY ? ALIGN_BOTTOM : ALIGN_TOP);\r\n"+
            "          }else if(pos==(this.flipX ? 'right' : 'left')){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,min(_r,_l)-2,(_t+_b)/2,0,ALIGN_RIGHT,ALIGN_MIDDLE);\r\n"+
            "          }else if(pos==(this.flipX ? 'left' : 'right')){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,max(_r,_l)+2,(_t+_b)/2,0,ALIGN_LEFT,ALIGN_MIDDLE);\r\n"+
            "          }\r\n"+
            "        }\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "  }\r\n"+
            "  this.canvasMouseManager.repaint();\r\n"+
            "    var end=Date.now();\r\n"+
            "    log(\"Draw: \"+(end-start));\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiChartLayer_Bar.prototype.scaleX=function(x){\r\n"+
            "	if(x==null)\r\n"+
            "		return null;\r\n"+
            "	return x*this.posZoomX + this.posOffsetX;\r\n"+
            "}\r\n"+
            "AmiChartLayer_Bar.prototype.scaleY=function(y){\r\n"+
            "	if(y==null)\r\n"+
            "		return null;\r\n"+
            "	return y*this.posZoomY + this.posOffsetY;\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiChartLayer_Bar.prototype.addSeries=function(id,name,data){\r\n"+
            "	this.series[id+\"-\"+name]=data;\r\n"+
            "	var groups=this.groupsByPosition[data.position];\r\n"+
            "	if(groups==null)\r\n"+
            "		groups=this.groupsByPosition[data.position]=[];\r\n"+
            "	groups[groups.length]=data;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "");

	}
	
}