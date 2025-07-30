package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ami_radialchart_js_1 extends AbstractHttpHandler{

	public amiweb_ami_radialchart_js_1() {
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
            "function AmiChartLayer_Radial(){\r\n"+
            "  this.divElement=nw('div');\r\n"+
            "  var that=this;\r\n"+
            "  this.isContainer=false;\r\n"+
            "  this.fontStyle=\"\";\r\n"+
            "  this.fontSize=10;\r\n"+
            "  this.fontFamily=\"arial\";\r\n"+
            "  this.canvas=nw('canvas');\r\n"+
            "  this.canvas.style.position='absolute';\r\n"+
            "  this.context = this.canvas.getContext('2d');\r\n"+
            "  this.context.translate(.5,.5);\r\n"+
            "  this.canvasMap=new CanvasMap(this.canvas,this.flipX,!this.flipY,false);\r\n"+
            "}\r\n"+
            "AmiChartLayer_Radial.prototype.plot;\r\n"+
            "AmiChartLayer_Radial.prototype.init=function(options){\r\n"+
            "  this.series={};\r\n"+
            "  this.groupsByPosition=[];\r\n"+
            "  this.isInit=true;\r\n"+
            "  this.canvas.style.opacity=options.opacity/100;\r\n"+
            "  this.flipX=options.flipX;\r\n"+
            "  this.flipY=options.flipY;\r\n"+
            "  this.canvasMap=new CanvasMap(this.canvas,this.flipX,!this.flipY,false);\r\n"+
            "  this.borderColor=options.borderColor;\r\n"+
            "  this.circleColor=options.circleColor;\r\n"+
            "  this.spokesColor=options.spokesColor;\r\n"+
            "  this.borderSize=noNull(options.borderSize,0);\r\n"+
            "  this.circleSize=noNull(options.circleSize,0);\r\n"+
            "  this.spokesSize=noNull(options.spokesSize,0);\r\n"+
            "  this.spokesCount=options.spokesCount;\r\n"+
            "  this.circlesCount=options.circlesCount;\r\n"+
            "  this.innerPaddingPx=options.innerPaddingPx;\r\n"+
            "  this.outerPaddingPx=options.outerPaddingPx;\r\n"+
            "  this.sAngle=options.sAngle*PI_180;\r\n"+
            "  this.eAngle=options.eAngle*PI_180;\r\n"+
            "  if(this.sAngle>this.eAngle){\r\n"+
            "	  var t=this.sAngle;\r\n"+
            "	  this.sAngle=this.eAngle;\r\n"+
            "	  this.eAngle=t;\r\n"+
            "  }\r\n"+
            "  this.lAngle=options.lAngle*PI_180;\r\n"+
            "  this.rMax=options.rMax;\r\n"+
            "  this.rMin=options.rMin;\r\n"+
            "  this.fontColor=options.fontColor;\r\n"+
            "  this.fontSize=options.fontSize;\r\n"+
            "  this.xPos=options.xPos;\r\n"+
            "  this.yPos=options.yPos;\r\n"+
            "  this.x2Pos=options.x2Pos;\r\n"+
            "  this.y2Pos=options.y2Pos;\r\n"+
            "  this.aLabels=options.aLabels;\r\n"+
            "  this.rLabels=options.rLabels;\r\n"+
            "  this.updateCanvasShift();\r\n"+
            "}\r\n"+
            "AmiChartLayer_Radial.prototype.setZoom=function(zoom,zoomX,zoomY){\r\n"+
            "  this.zoom=zoom;\r\n"+
            "  this.zoomX=zoomX;\r\n"+
            "  this.zoomY=zoomY;\r\n"+
            "  this.updateCanvasShift();\r\n"+
            "}\r\n"+
            "AmiChartLayer_Radial.prototype.updateCanvasShift=function(zoom,zoomX,zoomY){\r\n"+
            "    //this.canvasMap.setShift(rd((this.zoomX-.5+this.xPos)*this.zoom*this.canvasMap.getWidth()-this.canvasMap.getWidth()*.5*this.zoom+this.canvasMap.getWidth()/2),rd((this.zoomY-.5+this.yPos)*this.zoom*this.canvasMap.getHeight()));\r\n"+
            "	var x=this.flipX ? -this.zoomX : this.zoomX;\r\n"+
            "	var y=this.flipY ? -this.zoomY : this.zoomY;\r\n"+
            "	var z=this.zoom;\r\n"+
            "	var w=this.canvasMap.getWidth();\r\n"+
            "	var h=this.canvasMap.getHeight();\r\n"+
            "    //this.canvasMap.setShift(rd((this.zoomX-.5+this.xPos)*this.zoom*this.canvasMap.getWidth()-this.canvasMap.getWidth()*.5*this.zoom+this.canvasMap.getWidth()/2),rd((this.zoomY-.5+this.yPos)*this.zoom*this.canvasMap.getHeight()));\r\n"+
            "    this.canvasMap.setShift(rd(-x+w*this.xPos),rd(-y+h*this.yPos));\r\n"+
            "}\r\n"+
            "AmiChartLayer_Radial.prototype.setSize=function(width,height){\r\n"+
            "  this.canvas.style.width=toPx(width);\r\n"+
            "  this.canvas.style.height=toPx(height);\r\n"+
            "  this.canvas.width=width;\r\n"+
            "  this.canvas.height=height;\r\n"+
            "  this.context.translate(.5,.5);\r\n"+
            "  if(this.canvasMap!=null){\r\n"+
            "    this.canvasMap.updateCanvasSize();\r\n"+
            "    this.updateCanvasShift();\r\n"+
            "  }\r\n"+
            "  this.draw();\r\n"+
            "}\r\n"+
            "AmiChartLayer_Radial.prototype.draw=function(){\r\n"+
            "  if(this.canvasMap==null || !this.isInit)\r\n"+
            "    return;\r\n"+
            "  this.canvasMap.reset(this.canvas,this.flipX,!this.flipY,false);\r\n"+
            "  this.context.save();\r\n"+
            "  this.updateCanvasShift();\r\n"+
            "  this.canvasMouseManager.clearForTarget(this);\r\n"+
            "  this.canvasMap.clear();\r\n"+
            "  var oldX,oldY,oldX2,oldY2;\r\n"+
            "  var vScale=this.canvasMap.getHeight() * (1+2*Math.abs(this.yPos-.5))*this.zoom;\r\n"+
            "  var hScale=this.canvasMap.getWidth()* (1+2*Math.abs(this.xPos-.5))*this.zoom;\r\n"+
            "  var minSize=Math.min(vScale,hScale);\r\n"+
            "  var outer=minSize/2-this.outerPaddingPx;\r\n"+
            "  var rScale=(outer-this.innerPaddingPx)/(this.rMax-this.rMin);\r\n"+
            "  for(var j=this.groupsByPosition.length-1;j>=0;j--){\r\n"+
            "	var groups=this.groupsByPosition[j];\r\n"+
            "	for(var k=groups.length-1;k>=0;k--){\r\n"+
            "	  var ser=groups[k];\r\n"+
            "      var idPrefix=ser.seriesId+'.'+ser.groupId+'.';\r\n"+
            "	  var xPos=ser.xPos;\r\n"+
            "	  var yPos=ser.yPos;\r\n"+
            "	  var mColor=ser.mColor;\r\n"+
            "	  var mBorderColor=ser.mBorderColor;\r\n"+
            "	  var desc=ser.desc;\r\n"+
            "	  var descColor=ser.descColor;\r\n"+
            "	  var descSz=ser.descSz;\r\n"+
            "	  var descFontFam=ser.descFontFam;\r\n"+
            "	  var descPos=ser.descPos;\r\n"+
            "	  var mBorderSize=ser.mBorderSize;\r\n"+
            "	  var mWidth=ser.mWidth;\r\n"+
            "	  var mHeight=ser.mHeight;\r\n"+
            "	  var mTop=ser.mTop;\r\n"+
            "	  var mBottom=ser.mBottom;\r\n"+
            "	  var mLeft=ser.mLeft;\r\n"+
            "	  var mRight=ser.mRight;\r\n"+
            "	  var mShape=ser.mShape;\r\n"+
            "	  var isSelected=ser.isSelected;\r\n"+
            "	  var len=ser.size;\r\n"+
            "	  var sel=ser.sel;\r\n"+
            "	  this.context.beginPath();\r\n"+
            "	  \r\n"+
            "	  var x2Pos=ser.x2Pos;\r\n"+
            "	  var y2Pos=ser.y2Pos;\r\n"+
            "	  \r\n"+
            "	  if(ser.lineSize!=null && ser.lineSize>0 && ser.lineColor!=null){\r\n"+
            "        this.context.beginPath();\r\n"+
            "	    for(var i=0;i<len;i++){\r\n"+
            "          var x=deref(xPos,i);\r\n"+
            "          var y=deref(yPos,i);\r\n"+
            "          var x2=deref(x2Pos,i);\r\n"+
            "          var y2=deref(y2Pos,i);\r\n"+
            "	      y=(y-this.rMin)*rScale;\r\n"+
            "	      y+=this.innerPaddingPx;\r\n"+
            "          x=x*PI_180;\r\n"+
            "	      y2=(y2-this.rMin)*rScale;\r\n"+
            "	      y2+=this.innerPaddingPx;\r\n"+
            "          x2=x2*PI_180;\r\n"+
            "          if(x==null || y==null)\r\n"+
            "       	    continue;\r\n"+
            "		  if(i!=0){\r\n"+
            "			  var p1x=rd(radiansToX(oldY,oldX));\r\n"+
            "			  var p1y=rd(radiansToY(oldY,oldX));\r\n"+
            "			  var p2x=rd(radiansToX(y,x));\r\n"+
            "			  var p2y=rd(radiansToY(y,x));\r\n"+
            "			  var p3x=rd(radiansToX(y2,x2));\r\n"+
            "			  var p3y=rd(radiansToY(y2,x2));\r\n"+
            "			  var p4x=rd(radiansToX(oldY2,oldX2));\r\n"+
            "			  var p4y=rd(radiansToY(oldY2,oldX2));\r\n"+
            "				  \r\n"+
            "			  var t,t2;\r\n"+
            "			  var t=deref(ser.fillColor,i);\r\n"+
            "			  if(t!=null){\r\n"+
            "			    this.context.beginPath();\r\n"+
            "			    this.canvasMap.moveTo(p1x,p1y);\r\n"+
            "			    radiate(this.canvasMap,oldY,oldX,y,x);\r\n"+
            "                this.context.fillStyle=t;\r\n"+
            "			    this.canvasMap.lineTo(p3x,p3y);\r\n"+
            "			    radiate(this.canvasMap,y2,x2,oldY2,oldY2);\r\n"+
            "			    this.context.fill();\r\n"+
            "			  }\r\n"+
            "			  \r\n"+
            "			  t=deref(ser.fillBorderSize,i);\r\n"+
            "			  t2=deref(ser.fillBorderColor,i);\r\n"+
            "			  if(t>0 && t2){\r\n"+
            "                this.context.lineWidth=t;\r\n"+
            "                this.context.strokeStyle=t2;\r\n"+
            "			    this.context.beginPath();\r\n"+
            "			    this.canvasMap.moveTo(p2x,p2y);\r\n"+
            "			    this.canvasMap.lineTo(p3x,p3y);\r\n"+
            "			    this.canvasMap.moveTo(p4x,p4y);\r\n"+
            "			    this.canvasMap.lineTo(p1x,p1y);\r\n"+
            "                this.context.stroke();\r\n"+
            "			  }\r\n"+
            "              \r\n"+
            "			  t=deref(ser.lineSize,i);\r\n"+
            "			  t2=deref(ser.lineColor,i);\r\n"+
            "			  if(t>0 && t2){\r\n"+
            "                this.context.lineWidth=t;\r\n"+
            "                this.context.strokeStyle=t2;\r\n"+
            "			    this.context.beginPath();\r\n"+
            "			    this.canvasMap.moveTo(p1x,p1y);\r\n"+
            "			    radiate(this.canvasMap,oldY,oldX,y,x);\r\n"+
            "                this.context.stroke();\r\n"+
            "			  }\r\n"+
            "              \r\n"+
            "			  t=deref(ser.line2Size,i);\r\n"+
            "			  t2=deref(ser.line2Color,i);\r\n"+
            "			  if(t>0 && t2){\r\n"+
            "                this.context.lineWidth=t;\r\n"+
            "                this.context.strokeStyle=t2;\r\n"+
            "			    this.context.beginPath();\r\n"+
            "			    this.canvasMap.moveTo(p3x,p3y);\r\n"+
            "			    radiate(this.canvasMap,y2,x2,oldY2,oldX2);\r\n"+
            "                this.context.stroke();\r\n"+
            "			  }\r\n"+
            "              \r\n"+
            "			  \r\n"+
            "		  }\r\n"+
            "          oldX=x;\r\n"+
            "          oldY=y;\r\n"+
            "          oldX2=x2;\r\n"+
            "          oldY2=y2;\r\n"+
            "	    }\r\n"+
            "	  }\r\n"+
            "	  for(var i=0;i<len;i++){\r\n"+
            "        var x=deref(xPos,i);\r\n"+
            "        var y=deref(yPos,i);\r\n"+
            "        this.context.beginPath();\r\n"+
            "        this.context.strokeStyle=deref(mBorderColor,i);\r\n"+
            "        var borderSize=deref(mBorderSize,i);\r\n"+
            "        if(borderSize>0){\r\n"+
            "          this.context.lineWidth=borderSize;\r\n"+
            "        }\r\n"+
            "        this.context.fillStyle=deref(mColor,i);\r\n"+
            "        var shape=deref(mShape,i);\r\n"+
            "	    var width=deref(mWidth,i)/2;\r\n"+
            "	    var height=deref(mHeight,i)/2;\r\n"+
            "        var description=deref(desc,i);\r\n"+
            "        if(shape=='wedge'){\r\n"+
            "	      var _b=mBottom==null ? add(y,height) : deref(mBottom,i);\r\n"+
            "	      var _t=mTop   ==null ? sub(y,height) : deref(mTop,i);\r\n"+
            "	      var _l=mLeft  ==null ? sub(x,width) : deref(mLeft,i);\r\n"+
            "	      var _r=mRight ==null ? add(x,width) : deref(mRight,i);\r\n"+
            "	      if(_b==null || _t==null || _l==null || _r==null )\r\n"+
            "	    	  continue;\r\n"+
            "	      _t=(_t-this.rMin)*rScale+this.innerPaddingPx;\r\n"+
            "	      _b=(_b-this.rMin)*rScale+this.innerPaddingPx;\r\n"+
            "          _l*=PI_180;\r\n"+
            "          _r*=PI_180;\r\n"+
            "		  radiate(this.canvasMap,_t,_l,_t,_r);\r\n"+
            "		  radiate(this.canvasMap,_t,_r,_b,_r);\r\n"+
            "		  radiate(this.canvasMap,_b,_r,_b,_l);\r\n"+
            "		  radiate(this.canvasMap,_b,_l,_t,_l);\r\n"+
            "          this.context.closePath();\r\n"+
            "		  var midA=(_l+_r) / 2;\r\n"+
            "	      var ty=radiansToY(_t,midA);\r\n"+
            "	      var tx=radiansToX(_t,midA);\r\n"+
            "	      //if(diff(_l,_r) * _t > 10)\r\n"+
            "          //this.canvasMap.drawText(description,tx,ty,(-midA)/PI_180,-1,0);\r\n"+
            "          //console.log([this.canvasMap.mapX(tx/4,ty/4),this.canvasMap.mapY(tx/4,ty/4),100,100]);\r\n"+
            "	      \r\n"+
            "          this.context.fill();\r\n"+
            "          \r\n"+
            "          if(description!=null){\r\n"+
            "            var descriptionColor=deref(descColor,i);\r\n"+
            "            this.context.fillStyle=descriptionColor;\r\n"+
            "            this.canvasMap.setFont(this.fontStyle,deref(descSz),descFontFam);\r\n"+
            "            this.canvasMap.drawTextIfSpace(description,tx,ty,0,angleToAlignH(midA),-angleToAlignV(midA));\r\n"+
            "          }\r\n"+
            "          if(borderSize>0)\r\n"+
            "            this.context.stroke();\r\n"+
            "          this.canvasMouseManager.addWedge(this.canvasMap.mapX(0,0),this.canvasMap.mapY(0,0),_b,_t,this.canvasMap.mapAngle(_l),this.canvasMap.mapAngle(_r),this,idPrefix+i,isSelected!=null && isSelected[i],deref(sel,i));\r\n"+
            "        }else{\r\n"+
            "	      y=(y-this.rMin)*rScale;\r\n"+
            "	      y+=this.innerPaddingPx;\r\n"+
            "          x=x*PI_180;\r\n"+
            "	      var yr=radiansToY(y,x);\r\n"+
            "	      var xr=radiansToX(y,x);\r\n"+
            "	      var _b=mBottom==null ? rd(yr+height) : rd(deref(mBottom,i)*vScale);\r\n"+
            "	      var _t=mTop   ==null ? rd(yr-height) : rd(deref(mTop,i)*vScale);\r\n"+
            "	      var _l=mLeft  ==null ? rd(xr-width) : rd(deref(mLeft,i)*hScale);\r\n"+
            "	      var _r=mRight ==null ? rd(xr+width) : rd(deref(mRight,i)*hScale);\r\n"+
            "          var _w=_r-_l;\r\n"+
            "          var _h=_b-_t;\r\n"+
            "	      var addShape=false;\r\n"+
            "          if(shape=='circle'){\r\n"+
            "		    this.canvasMap.fillOval(_l,_t,_w,_h);\r\n"+
            "		    addShape=true;\r\n"+
            "          }else if(shape=='square'){\r\n"+
            "		    this.canvasMap.fillRect");
          out.print(
            "(_l,_t,_w,_h);\r\n"+
            "            if(borderSize>0)\r\n"+
            "		      this.canvasMap.strokeRect(_l,_t,_w,_h);\r\n"+
            "		    addShape=true;\r\n"+
            "          }else if(shape=='triangle'){\r\n"+
            "            this.canvasMap.moveTo(rd((_r+_l)/2),_b-height);\r\n"+
            "            this.canvasMap.lineTo(_r,_b);\r\n"+
            "            this.canvasMap.lineTo(_l,_b);\r\n"+
            "            this.canvasMap.closePath();\r\n"+
            "		    addShape=true;\r\n"+
            "          }\r\n"+
            "          if(addShape){\r\n"+
            "        	  this.canvasMouseManager.addRect(this.canvasMap.mapX(_l,_t),this.canvasMap.mapY(_l,_t),this.canvasMap.mapW(_w,_h),this.canvasMap.mapH(_w,_h),this,idPrefix+i,isSelected!=null && isSelected[i],deref(sel,i));\r\n"+
            "          }\r\n"+
            "          this.context.fill();\r\n"+
            "          if(borderSize>0)\r\n"+
            "            this.context.stroke();\r\n"+
            "        var description=deref(desc,i);\r\n"+
            "        if(description!=null){\r\n"+
            "          var pos=deref(descPos,i);\r\n"+
            "          this.canvasMap.setFont(this.fontStyle,deref(descSz),descFontFam);\r\n"+
            "          var descriptionColor=deref(descColor,i);\r\n"+
            "          this.context.fillStyle=descriptionColor;\r\n"+
            "          if(pos==null)\r\n"+
            "        	  pos='top';\r\n"+
            "          if(pos==(this.flipY ? 'bottom' : 'top')){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,max(_b,_t),0,ALIGN_MIDDLE,!this.flipY ? ALIGN_BOTTOM : ALIGN_TOP);\r\n"+
            "          }else if(pos==(this.flipY ? 'top' : 'bottom')){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,min(_b,_t),0,ALIGN_MIDDLE,this.flipY ? ALIGN_BOTTOM : ALIGN_TOP);\r\n"+
            "          }else if(pos==(this.flipX ? 'right' : 'left')){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,min(_r,_l)-2,(_t+_b)/2,0,ALIGN_RIGHT,ALIGN_MIDDLE);\r\n"+
            "          }else if(pos==(this.flipX ? 'left' : 'right')){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,max(_r,_l)+2,(_t+_b)/2,0,ALIGN_LEFT,ALIGN_MIDDLE);\r\n"+
            "          }else if(pos=='center'){\r\n"+
            "              this.canvasMap.drawTextIfSpace(description,(_r+_l)/2,(_t+_b)/2,0,ALIGN_MIDDLE,ALIGN_MIDDLE);\r\n"+
            "          }\r\n"+
            "          }\r\n"+
            "	    }\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "  }\r\n"+
            "  this.context.restore();\r\n"+
            "  this.canvasMouseManager.repaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiChartLayer_Radial.prototype.addSeries=function(id,name,data){\r\n"+
            "	this.series[id+\"-\"+name]=data;\r\n"+
            "	var groups=this.groupsByPosition[data.position];\r\n"+
            "	if(groups==null)\r\n"+
            "		groups=this.groupsByPosition[data.position]=[];\r\n"+
            "	groups[groups.length]=data;\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiChartLayer_Radial.prototype.drawGrid=function(gridContext,gridCanvas){\r\n"+
            "  if(this.canvasMap==null || !this.isInit)\r\n"+
            "    return;\r\n"+
            "  this.canvasMap.reset(gridCanvas,this.flipX,!this.flipY,false);\r\n"+
            "  this.updateCanvasShift();\r\n"+
            "//  gridContext.clearRect(-1,-1,gridCanvas.width+2,gridCanvas.height+2);\r\n"+
            "  gridContext.beginPath();\r\n"+
            "  gridContext.lineWidth = 1;\r\n"+
            "  gridContext.lineJoin=\"miter\";\r\n"+
            "  gridContext.fillStyle=this.fontColor;\r\n"+
            "  this.canvasMap.setFont(this.fontStyle,this.fontSize,this.fontFamily);\r\n"+
            "  var vScale=this.canvasMap.getHeight() * (1+2*Math.abs(this.yPos-.5))*this.zoom;\r\n"+
            "  var hScale=this.canvasMap.getWidth()* (1+2*Math.abs(this.xPos-.5))*this.zoom;\r\n"+
            "  var minSize=Math.min(vScale,hScale);\r\n"+
            "  var outer=minSize/2-this.outerPaddingPx;\r\n"+
            "  var rScale=(outer-this.innerPaddingPx)/(this.rMax-this.rMin);\r\n"+
            "  if(this.borderSize>0){\r\n"+
            "    gridContext.lineWidth=this.borderSize;\r\n"+
            "    gridContext.strokeStyle=this.borderColor;\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    this.canvasMap.arc(0,0,this.innerPaddingPx,this.sAngle,this.eAngle,false);\r\n"+
            "    gridContext.stroke();\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    this.canvasMap.arc(0,0,outer,this.sAngle,this.eAngle,false);\r\n"+
            "    gridContext.stroke();\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  \r\n"+
            "  gridContext.lineWidth=this.spokesSize;\r\n"+
            "  gridContext.strokeStyle=this.spokesColor;\r\n"+
            "  var degrees=(this.eAngle-this.sAngle)/this.spokesCount;\r\n"+
            "  for(var i=0;i<this.spokesCount+1;i++){\r\n"+
            "    gridContext.beginPath();\r\n"+
            "    var d=this.sAngle+i*degrees;\r\n"+
            "    var angle=rd(d/PI_180*4)/4;\r\n"+
            "      \r\n"+
            "    var rx=radiansToX(this.innerPaddingPx,d);\r\n"+
            "    var ry=radiansToY(this.innerPaddingPx,d);\r\n"+
            "    this.canvasMap.moveTo(rx,ry);\r\n"+
            "    rx=radiansToX(outer,d);\r\n"+
            "    ry=radiansToY(outer,d);\r\n"+
            "      \r\n"+
            "    if(this.fontColor!=null){\r\n"+
            "      if(i!=this.spokesCount || this.sAngle!=0 || this.eAngle!=PI2){\r\n"+
            "        this.canvasMap.drawText(\" \"+this.aLabels[i]+\" \",rx,ry ,this.flipX==this.flipY ? -angle : angle,-1,0);\r\n"+
            "    	if(this.spokesSize>0){\r\n"+
            "          this.canvasMap.lineTo(rx,ry);\r\n"+
            "          gridContext.stroke();\r\n"+
            "    	}\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  var spacing=(outer-this.innerPaddingPx)/this.circlesCount;\r\n"+
            "  gridContext.lineWidth=this.circleSize;\r\n"+
            "  gridContext.strokeStyle=this.circleColor;\r\n"+
            "  for(var i=1;i<this.circlesCount;i++){\r\n"+
            "	var t=(i*spacing)+this.innerPaddingPx;\r\n"+
            "	if(this.circleSize>0){\r\n"+
            "      gridContext.beginPath();\r\n"+
            "      this.canvasMap.arc(0,0,t,this.sAngle,this.eAngle,false);\r\n"+
            "      gridContext.stroke();\r\n"+
            "    }  \r\n"+
            "    gridContext.beginPath();\r\n"+
            "//    var val=i*spacing/rScale;\r\n"+
            "    var rx=radiansToX(t,this.lAngle);\r\n"+
            "    var ry=radiansToY(t,this.lAngle);\r\n"+
            "    if(this.fontColor!=null){\r\n"+
            "      if(this.rLabels!=null){\r\n"+
            "         angle=-this.lAngle/PI_180;\r\n"+
            "        this.canvasMap.drawText(this.rLabels[i],rx,ry,angle,0,(-270<=angle && angle<-90? 1 : -1 ));\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "    gridContext.stroke();\r\n"+
            "  }\r\n"+
            "}");

	}
	
}