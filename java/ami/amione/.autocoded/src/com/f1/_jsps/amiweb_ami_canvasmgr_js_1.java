package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ami_canvasmgr_js_1 extends AbstractHttpHandler{

	public amiweb_ami_canvasmgr_js_1() {
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
            "var ALIGN_TOP=-1;\r\n"+
            "var ALIGN_LEFT=-1;\r\n"+
            "var ALIGN_MIDDLE=0;\r\n"+
            "var ALIGN_BOTTOM=1;\r\n"+
            "var ALIGN_RIGHT=1;\r\n"+
            "\r\n"+
            "function CanvasMap(canvas,flipX,flipY,transpose){\r\n"+
            "  this.reset(canvas,flipX,flipY,transpose);\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.reset=function(canvas,flipX,flipY,transpose){\r\n"+
            "  this.shiftX=0;\r\n"+
            "  this.shiftY=0;\r\n"+
            "  this.canvas=canvas;\r\n"+
            "  this.context = this.canvas.getContext('2d');\r\n"+
            "  this.flipX=flipX;\r\n"+
            "  this.flipY=flipY;\r\n"+
            "  this.transpose=transpose;\r\n"+
            "  this.updateCanvasSize();\r\n"+
            "  this.fontHeight=14;\r\n"+
            "  this.textRects=[];\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.setFont=function(fontStyle,fontSize,fontFamily){\r\n"+
            "  this.fontStyle=fontStyle;\r\n"+
            "  this.fontFamily=fontFamily;\r\n"+
            "  this.fontSize=fontSize;\r\n"+
            "  this.context.font=this.fontStyle+\" \"+this.fontSize+\"px \"+this.fontFamily;\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.setShift=function(x,y){\r\n"+
            "  this.shiftX=x;\r\n"+
            "  this.shiftY=y;\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.updateCanvasSize=function(){\r\n"+
            "  this.width=1*(this.transpose ? this.canvas.height : this.canvas.width);\r\n"+
            "  this.height=1*(this.transpose ? this.canvas.width : this.canvas.height);\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.getWidth=function(){\r\n"+
            "	return this.width;\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.getHeight=function(){\r\n"+
            "	return this.height;\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.mapX=function(x,y){\r\n"+
            "	if(this.transpose){\r\n"+
            "		return this.flipY ? (this.height - y-1-this.shiftY) : y+this.shiftY;\r\n"+
            "	}else{\r\n"+
            "		return this.flipX ? (this.width - x-1 - this.shiftX) : x + this.shiftX;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.mapY=function(x,y){\r\n"+
            "	if(this.transpose){\r\n"+
            "		return this.flipX ? (this.width - x-1 -this.shiftX) : x+this.shiftX;\r\n"+
            "	}else{\r\n"+
            "		return this.flipY ? (this.height - y-1-this.shiftY) : y+this.shiftY;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.mapAngle=function(start){\r\n"+
            "	if(this.flipY){\r\n"+
            "	  if(this.flipX)\r\n"+
            "	    return -Math.PI+start;\r\n"+
            "	  else\r\n"+
            "	    return PI2-start;\r\n"+
            "	}else if(this.flipX)\r\n"+
            "	  return Math.PI-start;\r\n"+
            "	else\r\n"+
            "	  return start;\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.mapW=function(w,h){\r\n"+
            "	if(this.transpose){\r\n"+
            "		return this.flipY ? -h : h;\r\n"+
            "	}else{\r\n"+
            "		return this.flipX ? -w : w;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.mapH=function(w,h){\r\n"+
            "	if(this.transpose){\r\n"+
            "		return this.flipX ? -w : w;\r\n"+
            "	}else{\r\n"+
            "		return this.flipY ? -h : h;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.moveTo=function(x,y){\r\n"+
            "	this.context.moveTo(this.mapX(x,y),this.mapY(x,y));\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.quadTo=function(x,y,x2,y2){\r\n"+
            "	this.context.quadraticCurveTo(this.mapX(x,y),this.mapY(x,y),this.mapX(x2,y2),this.mapY(x2,y2));\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.lineTo=function(x,y){\r\n"+
            "	this.context.lineTo(this.mapX(x,y),this.mapY(x,y));\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.closePath=function(){\r\n"+
            "	this.context.closePath();\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.arc=function(x,y,size,start,end,clockwise){\r\n"+
            "	if(size<0)\r\n"+
            "		size=0;\r\n"+
            "	if(this.flipY){\r\n"+
            "	  if(this.flipX)\r\n"+
            "	    this.context.arc(this.mapX(x,y),this.mapY(x,y),size,-Math.PI+start,-Math.PI+end,clockwise);\r\n"+
            "	  else\r\n"+
            "	    this.context.arc(this.mapX(x,y),this.mapY(x,y),size,PI2-start,PI2-end,!clockwise);\r\n"+
            "	}else if(this.flipX)\r\n"+
            "	  this.context.arc(this.mapX(x,y),this.mapY(x,y),size,Math.PI-start,Math.PI-end,!clockwise);\r\n"+
            "	else\r\n"+
            "	  this.context.arc(this.mapX(x,y),this.mapY(x,y),size,start,end,clockwise);\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.strokeRect=function(left,top,width,height){\r\n"+
            "    this.context.strokeRect(this.mapX(left,top),this.mapY(left,top),this.mapW(width,height),this.mapH(width,height));\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.fillRect=function(left,top,width,height){\r\n"+
            "    this.context.fillRect(this.mapX(left,top),this.mapY(left,top),this.mapW(width,height),this.mapH(width,height));\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.rect=function(left,top,width,height){\r\n"+
            "    this.context.rect(this.mapX(left,top),this.mapY(left,top),this.mapW(width,height),this.mapH(width,height));\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.fillOvalCenteredAt=function(centerX,centerY,radiusWidth,radiusHeight){\r\n"+
            "  this.fillOval(centerX-radiusWidth,centerY-radiusHeight,radiusWidth*2,radiusHeight*2);\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.fillOval=function(left,top,width,height){\r\n"+
            "  var x=this.mapX(left,top);\r\n"+
            "  var y=this.mapY(left,top);\r\n"+
            "  var w=this.mapW(width,height);\r\n"+
            "  var h=this.mapH(width,height);\r\n"+
            "  drawCanvasOval(this.context,x,y,w,h);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function drawCanvasOval(context,x,y,w,h){\r\n"+
            "    var midX=x+w/2;\r\n"+
            "    var midY=y+h/2;\r\n"+
            "    if(Math.abs(w)===Math.abs(h)){\r\n"+
            "      var t=Math.abs(w)/2;\r\n"+
            "	  context.moveTo(midX+t, midY);\r\n"+
            "	  context.arc(midX, midY, t, 0,PI2, false);\r\n"+
            "    }else if(context.ellipse){\r\n"+
            "      var t=Math.abs(w)/2;\r\n"+
            "      var t2=Math.abs(h)/2;\r\n"+
            "	  context.moveTo(midX+t, midY);\r\n"+
            "	  context.ellipse(midX, midY, t,t2, 0,0,PI2, false);\r\n"+
            "//      context.save();\r\n"+
            "//      context.translate(midX,midY);\r\n"+
            "//      context.scale(Math.abs(w/h),1);\r\n"+
            "//	  context.arc(0, 0, Math.abs(h)/2, 0,PI2, false);\r\n"+
            "//      context.restore();\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMap.prototype.clear=function(){\r\n"+
            "  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);\r\n"+
            "  this.textRects=[];\r\n"+
            "}\r\n"+
            "\r\n"+
            "//TODO: does not properly check for rotated text\r\n"+
            "CanvasMap.prototype.drawTextIfSpace=function(text,x,y,rotate,xAlign,yAlign){\r\n"+
            "	var w=this.context.measureText(text).width-2;\r\n"+
            "	var h=this.fontSize-2;\r\n"+
            "	var xOrig=x;\r\n"+
            "	var yOrig=y;\r\n"+
            "	if(xAlign==ALIGN_RIGHT)\r\n"+
            "		x-=w;\r\n"+
            "	else if(xAlign==ALIGN_MIDDLE)\r\n"+
            "		x-=w/2;\r\n"+
            "	if(yAlign==ALIGN_TOP)\r\n"+
            "		y-=h;\r\n"+
            "	else if(yAlign==ALIGN_MIDDLE)\r\n"+
            "		y-=h/2;\r\n"+
            "	var yy=y+h;\r\n"+
            "	var xx=x+w;\r\n"+
            "	var topIdx=fl(x/10);//let's assume 10px is smallest font.\r\n"+
            "	var botIdx=fl(xx/10)+1;\r\n"+
            "	for(var j=topIdx;j<botIdx;j++){\r\n"+
            "	  var textRects=this.textRects[j];\r\n"+
            "	  if(textRects!=null)//nothing at this row\r\n"+
            "	    for(var i=0,l=textRects.length;i<l;i+=4)\r\n"+
            "		  if(xx>=textRects[i] && x<=textRects[i+2] && yy>=textRects[i+1] && y<=textRects[i+3]) \r\n"+
            "			return false;\r\n"+
            "	}\r\n"+
            "	for(var j=topIdx;j<botIdx;j++){\r\n"+
            "	  var textRects=this.textRects[j];\r\n"+
            "	  if(textRects==null)\r\n"+
            "		  textRects=this.textRects[j]=[];\r\n"+
            "	   textRects.push(x,y,xx,yy);\r\n"+
            "	}\r\n"+
            "	this.drawText(text,xOrig,yOrig,rotate,xAlign,yAlign);\r\n"+
            "	return true;\r\n"+
            "}\r\n"+
            "CanvasMap.prototype.drawText=function(text,x,y,rotate,xAlign,yAlign){\r\n"+
            "	\r\n"+
            "	var rotateOffset=0;\r\n"+
            "	if(this.transpose)\r\n"+
            "	  rotate+=90;\r\n"+
            "	if(this.flipX)\r\n"+
            "	  rotate+=180;\r\n"+
            "	if(rotate<0)\r\n"+
            "		rotate=360-((-rotate)%360);\r\n"+
            "	else\r\n"+
            "	    rotate%=360;\r\n"+
            "    if(rotate>90 && rotate<270){\r\n"+
            "      rotate=(rotate+180)%360;\r\n"+
            "      xAlign=-xAlign;\r\n"+
            "      yAlign=-yAlign;\r\n"+
            "    }\r\n"+
            "	if(xAlign===ALIGN_LEFT)\r\n"+
            "		this.context.textAlign='left';\r\n"+
            "	else if(xAlign===ALIGN_RIGHT)\r\n"+
            "		this.context.textAlign='right';\r\n"+
            "	else\r\n"+
            "		this.context.textAlign='center';\r\n"+
            "	if(yAlign===ALIGN_TOP)\r\n"+
            "		this.context.textBaseline='top';\r\n"+
            "	else if(yAlign===ALIGN_BOTTOM)\r\n"+
            "		this.context.textBaseline='bottom';\r\n"+
            "	else\r\n"+
            "		this.context.textBaseline='middle';\r\n"+
            "	\r\n"+
            "	if(rotate%360===0)\r\n"+
            "      this.context.fillText(text,this.mapX(x,y),this.mapY(x,y));\r\n"+
            "	else{\r\n"+
            "	  this.context.save();\r\n"+
            "	  this.context.translate(this.mapX(x,y), this.mapY(x,y));\r\n"+
            "	  this.context.rotate(rotate*PI_180);\r\n"+
            "      this.context.fillText(text,0,0);\r\n"+
            "	  this.context.restore();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function CanvasMouseManager(canvas){\r\n"+
            "  var that=this;\r\n"+
            "  this.canvas=canvas;\r\n"+
            "  this.canvas.onmousedown=function(e){that.onMousedown(e)};\r\n"+
            "  this.canvas.onmouseup=function(e){that.onMouseup(e)};\r\n"+
            "  this.canvas.onmousemove=function(e){that.onMousemove(e)};\r\n"+
            "  this.canvas.onmouseout=function(e){that.onMouseout(e)};\r\n"+
            "  this.canvas.onMouseWheel=function(e,delta){that.onMousewheel(e,delta)};\r\n"+
            "  this.canvas.ondblclick=function(e){that.onDblclick(e)};\r\n"+
            "  this.canvas.onkeydown=function(e){that.onKeyDown(e)};\r\n"+
            "  this.canvas.onkeyup=function(e){that.onKeyUp(e)};\r\n"+
            "  this.canvas.tabIndex=1000;\r\n"+
            "  this.context = this.canvas.getContext('2d');\r\n"+
            "  this.context.translate(.5,.5);\r\n"+
            "  this.shapesById={};\r\n"+
            "  this.shapesSelected={};\r\n"+
            "  this.shiftStart=null;\r\n"+
            "  this.timer=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMouseManager.prototype.clear=function(){\r\n"+
            "  this.width=this.canvas.width;\r\n"+
            "  this.height=this.canvas.height;\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMouseManager.prototype.onMouseup=function(e){\r\n"+
            "	\r\n"+
            "  if(this.trackingMouseDrag){\r\n"+
            "	this.trackingMouseDrag=false;\r\n"+
            "    document.onmouseup=null;\r\n"+
            "    document.onmousemove=null;\r\n"+
            "  }\r\n"+
            "  this.timer=null;\r\n"+
            "  var button=getMouseButton(e);\r\n"+
            "  if(button==3){\r\n"+
            "	  this.startMove=null;\r\n"+
            "	  return;\r\n"+
            "  }\r\n"+
            "  var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "  var t=getMouseTarget(e);\r\n"+
            "  var x=p.x;\r\n"+
            "  var y=p.y;\r\n"+
            "  if(this.shiftStart!=null){\r\n"+
            "    var x2=this.shiftStart[0];\r\n"+
            "    var y2=this.shiftStart[1];\r\n"+
            "    var w=x2-x;\r\n"+
            "    var h=y2-y;\r\n"+
            "    if(Math.abs(w)>2 && Math.abs(h)>2){\r\n"+
            "      this.fireSelectedChanged(x,y,w,h,e.ctrlKey,e.shiftKey);\r\n"+
            "      return;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);\r\n"+
            "    this.fireMouseClicked(x,y,button,e.ctrlKey,e.shiftKey);\r\n"+
            "  return;\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMouseManager.prototype.fireSelectedChanged=function(x,y,w,h,isCtrl,isShift){\r\n"+
            "	if(this.onSelectedChanged){\r\n"+
            "		this.onSelectedChanged(x,y,w,h,isCtrl,isShift);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "CanvasMouseManager.prototype.fireMouseClicked=function(x,y,button,ctrl,shift){\r\n"+
            "	if(this.onMouseClicked)\r\n"+
            "		this.onMouseClicked(this,x,y,button,ctrl,shift);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "CanvasMouseManager.prototype.onMousedown=function(e){\r\n"+
            "  var that=this;\r\n"+
            "  if(document.onmouseup==null){\r\n"+
            "	this.trackingMouseDrag=true;\r\n"+
            "    document.onmouseup=function(e){that.onMouseup(e)};\r\n"+
            "    document.onmousemove=function(e){that.onMousemove(e)};\r\n"+
            "  }\r\n"+
            "  var button=getMouseButton(e);\r\n"+
            "  var p=getMouseLayerPoint(e);\r\n"+
            "  if(button==3 || (e.ctrlKey && button==1)){\r\n"+
            "	  this.startMove=p;\r\n"+
            "	  return;\r\n"+
            "  }\r\n"+
            "  var x=p.x;\r\n"+
            "  var y=p.y;\r\n"+
            "  this.shiftStart=[x,y];\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMouseManager.prototype.repaint=function(){\r\n"+
            "    this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);\r\n"+
            "    for(var i in this.shapesSelected)\r\n"+
            "	  this.drawShape(this.shapesSelected[i],true);\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMouseManager.prototype.clearSelectRegion=function(){\r\n"+
            "	if(this.shiftStart==null)\r\n"+
            "		return;\r\n"+
            "	this.shiftStart=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "CanvasMouseManager.prototype.onKeyDown=function(e){\r\n"+
            "	if(e.key==\"Control\")\r\n"+
            "		this.canvas.style.cursor=\"move\";\r\n"+
            "}\r\n"+
            "CanvasMouseManager.prototype.onKeyUp=function(e){\r\n"+
            "	if(e.key==\"Control\")\r\n"+
            "		this.canvas.style.cursor=\"default\";\r\n"+
            "}\r\n"+
            "CanvasMouseManager.prototype.onMouseout=function(e){\r\n"+
            "  if(this");
          out.print(
            ".timer!=null)\r\n"+
            "	  clearTimeout(this.timer);\r\n"+
            "  this.canvas.style.cursor=\"default\";\r\n"+
            "  this.onHover(this,0,0,null,0,0);\r\n"+
            "}\r\n"+
            "CanvasMouseManager.prototype.onMousewheel=function(e,delta){\r\n"+
            "  var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "  this.onZoom(p.x,p.y,delta);\r\n"+
            "}\r\n"+
            "CanvasMouseManager.prototype.onDblclick=function(e){\r\n"+
            "  var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "  this.onZoom(p.x,p.y,5);\r\n"+
            "}\r\n"+
            "CanvasMouseManager.prototype.onMouseStill=function(e){\r\n"+
            "    var p=getMouseLayerPoint(this.lastMoveEvent);\r\n"+
            "    if(this.shiftStart!=null)\r\n"+
            "    	return;\r\n"+
            "   this.onHover(this,p.x,p.y);\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "CanvasMouseManager.prototype.onMousemove=function(e){\r\n"+
            "  if(this.onMouseHasMoved)\r\n"+
            "	this.onMouseHasMoved(e);\r\n"+
            "  var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "  if(this.lastMoveEvent!=null){\r\n"+
            "	  p2=getMousePointRelativeTo(this.lastMoveEvent,this.canvas);\r\n"+
            "	  if(p.x==p2.x && p.y==p2.y)\r\n"+
            "		  return;\r\n"+
            "  }\r\n"+
            "  var button=getMouseButton(e);\r\n"+
            "  if(button==3 || (button==1 && e.ctrlKey)){\r\n"+
            "	  if(this.startMove!=null){\r\n"+
            "		var dx=p.x-this.startMove.x;\r\n"+
            "		var dy=p.y-this.startMove.y;\r\n"+
            "		if(dx!=0 || dy!=0){\r\n"+
            "			if(this.onMouseDragged)\r\n"+
            "				this.onMouseDragged(e,dx,dy);\r\n"+
            "			this.startMove=p;\r\n"+
            "		}\r\n"+
            "	  }\r\n"+
            "	  return;\r\n"+
            "  }\r\n"+
            "  this.lastMoveEvent=e;\r\n"+
            "  if(this.timer!=null){\r\n"+
            "	  clearTimeout(this.timer);\r\n"+
            "  }\r\n"+
            "  var that=this;\r\n"+
            "  this.timer=setTimeout(function(){that.onMouseStill(e);}, 100);\r\n"+
            "  if(button==0)\r\n"+
            "	  return;\r\n"+
            "  \r\n"+
            "  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);\r\n"+
            "  var x=p.x;\r\n"+
            "  var y=p.y;\r\n"+
            "  if(this.shiftStart!=null){\r\n"+
            "	var t=this.shiftStart;\r\n"+
            "    this.context.lineWidth='1';\r\n"+
            "    this.context.beginPath();\r\n"+
            "    this.context.strokeStyle='rgba(64,64,64,.9)';\r\n"+
            "	this.context.strokeRect(t[0],t[1],x-t[0],y-t[1]);\r\n"+
            "    this.context.stroke();\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "");

	}
	
}