package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ami_plot_js_1 extends AbstractHttpHandler{

	public amiweb_ami_plot_js_1() {
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
            "\r\n"+
            "\r\n"+
            "function AmiPlotPortlet(portletId,parentId){\r\n"+
            "  this.portlet=new Portlet(this,portletId,null);\r\n"+
            "  this.isContainer=false;\r\n"+
            "  var that=this;\r\n"+
            "  this.divElement.style.background='#FFFFFF';\r\n"+
            "  this.clickLayer=nw('canvas');\r\n"+
            "  this.clickLayer.style.left='0px';\r\n"+
            "  this.clickLayer.style.top='0px';\r\n"+
            "  this.clickLayer.style.right='0px';\r\n"+
            "  this.clickLayer.style.bottom='0px';\r\n"+
            "  this.clickLayer.style.position='absolute';\r\n"+
            "  this.clickLayerContext=this.clickLayer.getContext('2d');\r\n"+
            "  \r\n"+
            "  \r\n"+
            "  //MOBILE SCROLLING\r\n"+
            "  this.clickLayer.ontouchstart=function(e){that.onTouchstart(e)};\r\n"+
            "  this.clickLayer.ontouchend=function(e){that.onTouchend(e)};\r\n"+
            "  this.clickLayer.ontouchmove=function(e){that.onTouchmove(e)};\r\n"+
            "  \r\n"+
            "  this.clickLayer.onmousedown=function(e){that.onMousedown(e)};\r\n"+
            "  this.clickLayer.onmouseup=function(e){that.onMouseup(e)};\r\n"+
            "  this.clickLayer.onmousemove=function(e){that.onMousemove(e)};\r\n"+
            "  this.clickLayer.onmouseout=function(e){that.onMouseout(e)};\r\n"+
            "//  this.clickLayer.ondblclick=function(e){that.onDblclick(e)};\r\n"+
            "  this.clickLayer.addEventListener('touchend', function(e) {\r\n"+
            "	    that.onDblTap(e);\r\n"+
            "	});\r\n"+
            "  this.clickLayer.tabIndex=1000;\r\n"+
            "  this.context = this.clickLayer.getContext('2d');\r\n"+
            "  this.context.translate(.5,.5);\r\n"+
            "  this.canvas=this.clickLayer;\r\n"+
            "  \r\n"+
            "  this.zoomMoveConsumedFlag=true;\r\n"+
            "  this.zoomMoveXPending=0;\r\n"+
            "  this.zoomMoveYPending=0;\r\n"+
            "  this.gridCanvas=nw('canvas');\r\n"+
            "  this.gridCanvas.style.position='absolute';\r\n"+
            "  this.gridContext = this.gridCanvas.getContext('2d');\r\n"+
            "  this.gridContext.translate(.5,.5);\r\n"+
            "  this.divElement.appendChild(this.gridCanvas);\r\n"+
            "  this.divElement.appendChild(this.clickLayer);\r\n"+
            "  this.pgid=sessionStorage.getItem('");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
          out.print(
            "',null);\r\n"+
            "} \r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.onMousedown=function(e){\r\n"+
            "  var that=this;\r\n"+
            "  var doc=getDocument(this.divElement);\r\n"+
            "  if(doc.onmouseup==null){\r\n"+
            "	this.trackingMouseDrag=true;\r\n"+
            "    doc.onmouseup=function(e){that.onMouseup(e)};\r\n"+
            "    doc.onmousemove=function(e){that.onMousemove(e)};\r\n"+
            "    this.clickLayer.onmousemove=null;\r\n"+
            "  }\r\n"+
            "  var button=getMouseButton(e);\r\n"+
            "  var p=getMouseLayerPoint(e);\r\n"+
            "  if(button==1 && !e.ctrlKey && !e.shiftKey && this.isZoom){\r\n"+
            "	  this.startPos=p;\r\n"+
            "	  this.currentPos=p;\r\n"+
            "	  this.movedPos=false;\r\n"+
            "	  return;\r\n"+
            "  }\r\n"+
            "  var x=p.x;\r\n"+
            "  var y=p.y;\r\n"+
            "  this.shiftStart=[x,y];\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.onMouseup=function(e){\r\n"+
            "  var doc=getDocument(this.divElement);\r\n"+
            "  if(this.trackingMouseDrag){\r\n"+
            "	this.trackingMouseDrag=false;\r\n"+
            "    doc.onmouseup=null;\r\n"+
            "    doc.onmousemove=null;\r\n"+
            "    var that=this;\r\n"+
            "    this.clickLayer.onmousemove=function(e){that.onMousemove(e)};\r\n"+
            "  }\r\n"+
            "  this.timer=null;\r\n"+
            "  var button=getMouseButton(e);\r\n"+
            "  if(this.movedPos){\r\n"+
            "	  this.currentPos=null;\r\n"+
            "	  this.startPos=null;\r\n"+
            "	  this.movedPos=false;\r\n"+
            "	  return;\r\n"+
            "  }else{\r\n"+
            "	  this.currentPos=null;\r\n"+
            "	  this.startPos=null;\r\n"+
            "	  this.movedPos=false;\r\n"+
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
            "      this.callBack('select',{x:x,y:y,w:w,h:h,isCtrl:e.ctrlKey,isShift:e.shiftKey});\r\n"+
            "      return;\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);\r\n"+
            "  this.callBack('onMouse',{x:x,y:y,b:button,c:e.ctrlKey,s:e.shiftKey});\r\n"+
            "  return;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//MOBILE SCROLLING ON TOUCH DOWN\r\n"+
            "AmiPlotPortlet.prototype.onTouchstart=function(e){\r\n"+
            "	  var that=this;\r\n"+
            "	  // 500ms delay for touch hold context menu (zooming out)\r\n"+
            "	  // if moved before 500ms, dont show menu\r\n"+
            "	  this.longTouchTimer = setTimeout(() => {\r\n"+
            "			if(this.isZoom){\r\n"+
            "				this.callBack('onMouse',{x:x,y:y,b:2,c:e.ctrlKey,s:e.shiftKey});\r\n"+
            "				return;\r\n"+
            "			}\r\n"+
            "	  }, 500);\r\n"+
            "	  // dragging zoomed in chart\r\n"+
            "	  var doc=getDocument(this.divElement);\r\n"+
            "	  if(doc.onmouseup==null){\r\n"+
            "		this.trackingMouseDrag=true;\r\n"+
            "	    doc.onmouseup=function(e){that.onMouseup(e)};\r\n"+
            "	    doc.onmousemove=function(e){that.onMousemove(e)};\r\n"+
            "	  }\r\n"+
            "	  var button=getMouseButton(e);\r\n"+
            "	  var p=getMouseLayerPoint(e);\r\n"+
            "	  if(button==1 && !e.ctrlKey && !e.shiftKey && this.isZoom){\r\n"+
            "		  this.startPos=p;\r\n"+
            "		  this.currentPos=p;\r\n"+
            "		  this.movedPos=false;\r\n"+
            "		  return;\r\n"+
            "	  }\r\n"+
            "	  var x=p.x;\r\n"+
            "	  var y=p.y;\r\n"+
            "	  this.shiftStart=[x,y];\r\n"+
            "	}\r\n"+
            "\r\n"+
            "//MOBILE SCROLLING ON TOUCH UP\r\n"+
            "AmiPlotPortlet.prototype.onTouchend=function(e){\r\n"+
            "	//clear touch timer for menu.\r\n"+
            "	clearTimeout(this.longTouchTimer);\r\n"+
            "	this.longTouchTimer=null;\r\n"+
            "	\r\n"+
            "	//borrowing code from on mouse up\r\n"+
            "	  var doc=getDocument(this.divElement);\r\n"+
            "	  if(this.trackingMouseDrag){\r\n"+
            "		this.trackingMouseDrag=false;\r\n"+
            "	    doc.onmouseup=null;\r\n"+
            "	    doc.onmousemove=null;\r\n"+
            "	    var that=this;\r\n"+
            "	    this.clickLayer.onmousemove=function(e){that.onMousemove(e)};\r\n"+
            "	  }\r\n"+
            "	  this.timer=null;\r\n"+
            "	  var button=getMouseButton(e);\r\n"+
            "	  if(this.movedPos){\r\n"+
            "		  this.currentPos=null;\r\n"+
            "		  this.startPos=null;\r\n"+
            "		  this.movedPos=false;\r\n"+
            "		  return;\r\n"+
            "	  }else{\r\n"+
            "		  this.currentPos=null;\r\n"+
            "		  this.startPos=null;\r\n"+
            "		  this.movedPos=false;\r\n"+
            "	  }\r\n"+
            "	  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);\r\n"+
            "	  return;\r\n"+
            "	}\r\n"+
            "\r\n"+
            "//MOBILE SCROLLING ON TOUCH MOVE\r\n"+
            "AmiPlotPortlet.prototype.onTouchmove=function(e){\r\n"+
            "	//clear touch timer for menu.\r\n"+
            "	clearTimeout(this.longTouchTimer);\r\n"+
            "	this.longTouchTimer=null;\r\n"+
            "	\r\n"+
            "	//borrowing code from mouse move\r\n"+
            "	  var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "	  if(this.lastMoveEvent!=null){\r\n"+
            "		  p2=getMousePointRelativeTo(this.lastMoveEvent,this.canvas);\r\n"+
            "		  if(p.x==p2.x && p.y==p2.y)\r\n"+
            "			  return;\r\n"+
            "	  }\r\n"+
            "	  var button=getMouseButton(e);\r\n"+
            "		  if(this.currentPos!=null){\r\n"+
            "			var dx=p.x-this.currentPos.x;\r\n"+
            "			var dy=p.y-this.currentPos.y;\r\n"+
            "			if(dx!=0 || dy!=0){\r\n"+
            "				if(this.onMouseDragged)\r\n"+
            "					this.onMouseDragged(e,dx,dy);\r\n"+
            "				this.currentPos=p;\r\n"+
            "				if(!this.movedPos && abs(p.x-this.startPos.x)>1 || abs(p.y-this.startPos.y)>1)\r\n"+
            "					this.movedPos=true;\r\n"+
            "			}\r\n"+
            "		  return;\r\n"+
            "	  }\r\n"+
            "	  this.lastMoveEvent=e;\r\n"+
            "	  if(this.timer!=null){\r\n"+
            "		  clearTimeout(this.timer);\r\n"+
            "	  }	  \r\n"+
            "	  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);\r\n"+
            "	}\r\n"+
            "AmiPlotPortlet.prototype.onMousemove=function(e){\r\n"+
            "  var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "  if(this.lastMoveEvent!=null){\r\n"+
            "	  p2=getMousePointRelativeTo(this.lastMoveEvent,this.canvas);\r\n"+
            "	  if(p.x==p2.x && p.y==p2.y)\r\n"+
            "		  return;\r\n"+
            "  }\r\n"+
            "  var button=getMouseButton(e);\r\n"+
            "	  if(this.currentPos!=null){\r\n"+
            "		var dx=p.x-this.currentPos.x;\r\n"+
            "		var dy=p.y-this.currentPos.y;\r\n"+
            "		if(dx!=0 || dy!=0){\r\n"+
            "			if(this.onMouseDragged)\r\n"+
            "				this.onMouseDragged(e,dx,dy);\r\n"+
            "			this.currentPos=p;\r\n"+
            "			if(!this.movedPos && abs(p.x-this.startPos.x)>1 || abs(p.y-this.startPos.y)>1)\r\n"+
            "				this.movedPos=true;\r\n"+
            "		}\r\n"+
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
            "    this.context.strokeStyle=this.selectionBoxBorderColor;//'rgba(64,64,64,.9)';\r\n"+
            "    this.context.fillStyle=this.selectionBoxFillColor;//'rgba(64,64,64,.9)';\r\n"+
            "    var xPos=t[0]+.5,yPos=t[1]+.5,wPos=x-t[0],hPos=y-t[1];\r\n"+
            "	this.context.fillRect(xPos,yPos,wPos,hPos);\r\n"+
            "	this.context.strokeRect(xPos,yPos,wPos,hPos);\r\n"+
            "    this.context.stroke();\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.onMouseDragged=function(e,dx,dy){\r\n"+
            "  this.zoomMoveXPending+=dx;\r\n"+
            "  this.zoomMoveYPending+=dy;\r\n"+
            "  if(this.zoomMoveConsumedFlag && (this.zoomMoveXPending!=0 || this.zoomMoveYPending!=0)){\r\n"+
            "    this.callBack('zoomMove',{dx:this.zoomMoveXPending,dy:this.zoomMoveYPending});\r\n"+
            "    this.zoomMoveConsumedFlag=false;\r\n"+
            "    this.zoomMoveXPending=0;\r\n"+
            "    this.zoomMoveYPending=0;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.zoomMoveConsumed=function(){\r\n"+
            "	this.zoomMoveConsumedFlag=true;\r\n"+
            "  if(this.zoomMoveXPending!=0 || this.zoomMoveYPending!=0){\r\n"+
            "    this.callBack('zoomMove',{dx:this.zoomMoveXPending,dy:this.zoomMoveYPending});\r\n"+
            "    this.zoomMoveConsumedFlag=false;\r\n"+
            "    this.zoomMoveXPending=0;\r\n"+
            "    this.zoomMoveYPending=0;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.handleKeydown=function(e){\r\n"+
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
            "	  if(e.key==\"ArrowLeft\")\r\n"+
            "		  this.onMouseDragged(e,distance,0);\r\n"+
            "	  else if(e.key==\"ArrowRight\")\r\n"+
            "		  this.onMouseDragged(e,-distance,0);\r\n"+
            "	  else if(e.key==\"ArrowUp\")\r\n"+
            "		    this.onMouseDragged(e,0,distance);\r\n"+
            "	  else if(e.key==\"ArrowDown\")\r\n"+
            "		  this.onMouseDragged(e,0,-distance);\r\n"+
            "	}\r\n"+
            "//	if(e.key==\"Control\")\r\n"+
            "//		this.canvas.style.cursor=\"move\";\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.handleKeyup=function(e){\r\n"+
            "//	if(e.key==\"Control\")\r\n"+
            "//		this.canvas.style.cursor=\"default\";\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.onMouseout=function(e){\r\n"+
            "  if(this.timer!=null)\r\n"+
            "	  clearTimeout(this.timer);\r\n"+
            "  this.canvas.style.cursor=\"default\";\r\n"+
            "  this.fireOnHover(0,0);\r\n"+
            "  this.clearHover();\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.handleWheel=function(e){\r\n"+
            "  if(e.target == this.canvas) { // scroll only when mouse is on the plot, not on legend.\r\n"+
            "	  var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "	  var delta;\r\n"+
            "	  if(e.deltaMode == WheelEvent.DOM_DELTA_PIXEL)\r\n"+
            "		  delta = e.deltaY/-100;\r\n"+
            "	  else\r\n"+
            "		  delta = e.deltaY/-1\r\n"+
            "	  this.setZoom(p.x,p.y,delta);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.onDblTap = (function() {\r\n"+
            "    let lastTapTime = 0;\r\n"+
            "    const doubleTapDelay = 200; // Maximum time (ms) between taps for it to be considered a double tap\r\n"+
            "\r\n"+
            "    return function(e) {\r\n"+
            "        const currentTime = new Date().getTime();\r\n"+
            "        const timeSinceLastTap = currentTime - lastTapTime;\r\n"+
            "\r\n"+
            "        if (timeSinceLastTap < doubleTapDelay && timeSinceLastTap > 0) {\r\n"+
            "          var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "          this.setZoom(p.x,p.y,5);\r\n"+
            "        }\r\n"+
            "\r\n"+
            "        lastTapTime = currentTime;\r\n"+
            "    };\r\n"+
            "})();\r\n"+
            "AmiPlotPortlet.prototype.onMouseStill=function(e){\r\n"+
            "    var p=getMouseLayerPoint(this.lastMoveEvent);\r\n"+
            "    if(this.shiftStart!=null)\r\n"+
            "    	return;\r\n"+
            "   this.fireOnHover(p.x,p.y);\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.fireOnHover=function(x,y){\r\n"+
            "  if(x==this.hoverRequestX && y==this.hoverRequestY)\r\n"+
            "	  return;\r\n"+
            "    this.hoverRequestX=x;\r\n"+
            "    this.hoverRequestY=y;\r\n"+
            "    this.callBack('hover',{x:x,y:y});\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.setZoom=function(x,y,delta){\r\n"+
            "  this.callBack('zoom',{x:x,y:y,delta:delta});\r\n"+
            "  this.clearHover();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.setHover=function(x,y,sel,name,xAlign,yAlign){\r\n"+
            "	if(x==0 && y==0)\r\n"+
            "		return;\r\n"+
            "  if(this.hoverRequest==sel){\r\n"+
            "	const rootBody = getRootNodeBody(this.divElement);\r\n"+
            "	if(this.tooltipDiv!=null)\r\n"+
            "	  rootBody.removeChild(this.tooltipDiv);\r\n"+
            "    this.tooltipDiv=nw(\"div\",\"ami_chart_tooltip\");\r\n"+
            "	var div=this.tooltipDiv;\r\n"+
            "	this.hoverX=MOUSE_POSITION_X;\r\n"+
            "	this.hoverY=MOUSE_POSITION_Y;\r\n"+
            "	div.innerHTML=name;\r\n"+
            "	if(div.firstChild!=null && div.firstChild.tagName=='DIV'){\r\n"+
            "		this.tooltipDiv=div.firstChild;\r\n"+
            "	    div=this.tooltipDiv;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	rootBody.appendChild(div);\r\n"+
            "	var origin = this.divElement.getBoundingClientRect();\r\n"+
            "	var rect=new Rect().readFromElement(div);\r\n"+
            "	var h=rect.height+4;\r\n"+
            "	var w=rect.width+6;\r\n"+
            "	div.style.width=toPx(w);\r\n"+
            "	div.style.height=toPx(h);\r\n"+
            "	switch(xAlign){\r\n"+
            "	  case ALIGN_LEFT: div.style.");
          out.print(
            "left=toPx(origin.x + x); break;\r\n"+
            "	  case ALIGN_RIGHT: div.style.left=toPx(origin.x + x-w); break;\r\n"+
            "	  default: div.style.left=toPx(origin.x + x-w/2); break;\r\n"+
            "	}\r\n"+
            "	switch(yAlign){\r\n"+
            "	  case ALIGN_TOP: div.style.top=toPx(origin.y + y); break;\r\n"+
            "	  case ALIGN_BOTTOM: div.style.top=toPx(origin.y + y-h); break;\r\n"+
            "	  default: div.style.top=toPx(origin.y + y-h/2); break;\r\n"+
            "	}\r\n"+
            "	ensureInDiv(div,rootBody);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.clearHover=function(){\r\n"+
            "  if(this.tooltipDiv!=null){\r\n"+
            "	  try {\r\n"+
            "		getDocument(this.divElement).body.removeChild(this.tooltipDiv);\r\n"+
            "	  } catch (e) {\r\n"+
            "		console.log(\"unable to clear chart hover: \" + e);\r\n"+
            "	  }\r\n"+
            "    this.tooltipDiv=null;\r\n"+
            "  }\r\n"+
            "  this.hoverRequest=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.clearSelectRegion=function(options){\r\n"+
            "	if(this.shiftStart==null)\r\n"+
            "		return;\r\n"+
            "	this.shiftStart=null;\r\n"+
            "    this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.init=function(options){\r\n"+
            "  this.bgColor=options.bgColor;\r\n"+
            "  this.selectionBoxBorderColor=options.selBoxBorderColor;///'rgba(64,64,64,.7)';//options.selectionBoxBorderColor;\r\n"+
            "  this.selectionBoxFillColor=options.selBoxFillColor;//'rgba(64,64,64,.1)';//options.selectionBoxBorderColor;\r\n"+
            "//  this.selectionBoxFillColor=options.selectionBoxFillColor;\r\n"+
            "  \r\n"+
            "  this.divElement.style.background=this.bgColor;\r\n"+
            "  this.clearPlot();\r\n"+
            "  this.clearLayers();\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.setSize=function(width,height){\r\n"+
            "	this.gridCanvas.style.width=toPx(width);\r\n"+
            "	this.gridCanvas.style.height=toPx(height);\r\n"+
            "	this.gridCanvas.width=width;\r\n"+
            "	this.gridCanvas.height=height;\r\n"+
            "    this.width=width;\r\n"+
            "    this.height=height;\r\n"+
            "	this.divElement.style.width=toPx(width);\r\n"+
            "	this.divElement.style.height=toPx(height);\r\n"+
            "	this.clickLayer.style.width=toPx(width);\r\n"+
            "	this.clickLayer.style.height=toPx(height);\r\n"+
            "	this.clickLayer.width=width;\r\n"+
            "	this.clickLayer.height=height;\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.clearPlot=function(){\r\n"+
            "  this.isZoom=false;\r\n"+
            "  this.clickLayer.backgroundImage=null;\r\n"+
            "  this.clickLayer.backgroundPosition='0px 0px';\r\n"+
            "  this.clickLayer.backgroundSize=null;\r\n"+
            "  this.clickLayer.backgroundRepeat='no-repeat';\r\n"+
            "  this.gridCanvas.backgroundImage=null;\r\n"+
            "  this.gridCanvas.backgroundPosition='0px 0px';\r\n"+
            "  this.gridCanvas.backgroundSize=null;\r\n"+
            "  this.gridCanvas.backgroundRepeat='no-repeat';\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.showWait=function(){\r\n"+
            "    if(this.waitPaneDiv==null){\r\n"+
            "       this.waitPaneDiv=nw(\"div\",\"wait_plot\");\r\n"+
            "       this.waitPaneDiv.waiting=true;\r\n"+
            "       this.divElement.appendChild(this.waitPaneDiv);\r\n"+
            "    }\r\n"+
            "	this.waitPaneDiv.style.display=\"inline\";\r\n"+
            "};\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.hideWait=function(){\r\n"+
            "	if(this.waitPaneDiv && this.waitPaneDiv.waiting)\r\n"+
            "		this.waitPaneDiv.style.display=\"none\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.setImage=function(layerPos,imageId,w,h,x,y,zx,zy){\r\n"+
            "	if(layerPos!=-1)\r\n"+
            "	  this.isZoom=zy>1 || zx>1;\r\n"+
            "	var layer=layerPos==-1 ? this.clickLayer :  this.gridCanvas;\r\n"+
            "	if(layerPos == 0){\r\n"+
            "		this.showWait();\r\n"+
            "	}\r\n"+
            "	if(layer.img!=null){\r\n"+
            "		layer.img.onload=null;\r\n"+
            "	}\r\n"+
            "	var that=layer;\r\n"+
            "	var plot = this;\r\n"+
            "	if(imageId==null){\r\n"+
            "        that.style.backgroundImage=null;\r\n"+
            "        that.style.backgroundPosition='0px 0px';\r\n"+
            "        that.backgroundSize=null;\r\n"+
            "        that.style.backgroundRepeat='no-repeat';\r\n"+
            "		that.img=null;\r\n"+
            "		return;\r\n"+
            "	}else{\r\n"+
            "		layer.img=nw('img');\r\n"+
            "		layer.img.src=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.ami.web.pages.AmiWebPages.URL_DYNAMIC_IMAGE);
          out.print(
            "?portletId=\"+imageId+\"&now=+\"+Date.now()+\"&");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
          out.print(
            "=\"+this.pgid;\r\n"+
            "		layer.img.data={w:w,h:h,x:x,y:y,zx:zx,zy:zy};\r\n"+
            "		layer.img.onload=function(){\r\n"+
            "			that.style.backgroundImage=\"url(\"+that.img.src+\")\";\r\n"+
            "			that.style.backgroundPosition='0px 0px';\r\n"+
            "			that.style.backgroundSize=null;\r\n"+
            "			that.style.backgroundRepeat='no-repeat';\r\n"+
            "			that.currentImg=that.img;\r\n"+
            "			that.img.onload=null;\r\n"+
            "			that.img=null;\r\n"+
            "			plot.hideWait();\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(that.currentImg!=null){\r\n"+
            "	  var od=that.currentImg.data;\r\n"+
            "	  var xShift=x-od.x*zx/od.zx;\r\n"+
            "	  var yShift=y-od.y*zy/od.zy;\r\n"+
            "	  var wShift=(zx / od.zx)* od.w;\r\n"+
            "	  var hShift=(zy / od.zy) * od.h;\r\n"+
            "	  layer.style.backgroundPosition=xShift+'px '+yShift+'px';\r\n"+
            "	  layer.style.backgroundSize=wShift +'px '+hShift+'px';\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.clearLayers=function(options){\r\n"+
            "	for(var i in this.layers){\r\n"+
            "		var layer=this.layers[i];\r\n"+
            "		if(layer!=null)\r\n"+
            "		  this.divElement.removeChild(layer.canvas);\r\n"+
            "	}\r\n"+
            "	this.layers=[];\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.addLayer=function(pos,layer){\r\n"+
            "	layer.plot = this;\r\n"+
            "	layer.layerPos = pos;\r\n"+
            "	var t=this.divElement.firstChild;\r\n"+
            "	this.layers[pos]=layer;\r\n"+
            "	this.divElement.insertBefore(layer.canvas,this.gridCanvas.nextSibling);\r\n"+
            "	layer.setSize(this.width,this.height);\r\n"+
            "}\r\n"+
            "AmiPlotPortlet.prototype.getLayer=function(pos){\r\n"+
            "	return this.layers[pos];\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiPlotPortlet.prototype.close=function(type,param){\r\n"+
            "	this.clearHover();\r\n"+
            "}");

	}
	
}