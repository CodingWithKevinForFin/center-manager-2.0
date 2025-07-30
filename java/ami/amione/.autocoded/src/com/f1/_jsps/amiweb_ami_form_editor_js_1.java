package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ami_form_editor_js_1 extends AbstractHttpHandler{

	public amiweb_ami_form_editor_js_1() {
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
            "function FormEditor(form){\r\n"+
            "  this.form=form;\r\n"+
            "  this.canvas=nw('canvas');\r\n"+
            "  this.context = this.canvas.getContext('2d');\r\n"+
            "//  this.canvasElement=nw('div');\r\n"+
            "//  this.canvasElement.appendChild(this.canvas);\r\n"+
            "//  this.canvasElement.style.zIndex=10;\r\n"+
            "//  this.canvasElement.style.pointerEvents='painted';\r\n"+
            "  this.divElement=nw('div', 'portal_form_canvas');\r\n"+
            "  this.divElement.appendChild(this.canvas);\r\n"+
            "  var that=this;\r\n"+
            "  this.canvas.onmousedown=function(e){that.onMouseDown(e);};\r\n"+
            "  this.canvas.onmouseup=function(e){that.onMouseUp(e);};\r\n"+
            "  this.canvas.onmouseout=function(e){that.onMouseOut(e);};\r\n"+
            "  this.canvas.onmousemove=function(e){that.onMouseMove(e);};\r\n"+
            "  this.canvas.onMouseWheel=function(e,delta){that.onMouseWheel(e,delta);};\r\n"+
            "  this.canvasMap=new CanvasMap(this.canvas,false,false,false);\r\n"+
            "  this.form.editor=this;\r\n"+
            "  this.isVisible=false;\r\n"+
            "  this.reset();\r\n"+
            "}\r\n"+
            "FormEditor.prototype.guides={};\r\n"+
            "FormEditor.prototype.rects={};\r\n"+
            "\r\n"+
            "FormEditor.prototype.reset=function(e){\r\n"+
            "  this.snapSize=-1;\r\n"+
            "  this.guides={};\r\n"+
            "  this.rects={};\r\n"+
            "  this.activeItem=null;\r\n"+
            "  this.activeItemType=null;\r\n"+
            "  this.multiSelected={};\r\n"+
            "  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormEditor.prototype.setVisible=function(isVisible){\r\n"+
            "  if(this.isVisible==isVisible)\r\n"+
            "	  return;\r\n"+
            "  this.isVisible=isVisible;\r\n"+
            "  if(this.isVisible)\r\n"+
            "    this.form.formDOMManager.formContainer.appendChild(this.divElement);\r\n"+
            "  else\r\n"+
            "    this.form.formDOMManager.formContainer.removeChild(this.divElement);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FormEditor.prototype.onMouseWheel=function(e,delta){\r\n"+
            "	if(this.activeItem!=null){\r\n"+
            "	    if(this.snapSize!=-1 && !e.shiftKey)\r\n"+
            "	      delta*=this.snapSize;\r\n"+
            "		var ai=this.activeItem;\r\n"+
            "        this.mouseWheelStart=getMousePointRelativeTo(e,this.divElement);\r\n"+
            "        if(this.activeItemType=='G'){ \r\n"+
            "        	ai.px+=ai.isVertical ? delta : -delta; \r\n"+
            "        	this.callSetGuidePos(ai);\r\n"+
            "        }else{\r\n"+
            "	      if(this.justSnappedX() || this.justSnappedY())\r\n"+
            "		    return 0;\r\n"+
            "		  switch(this.activeItemType){\r\n"+
            "		    case 'L':{ \r\n"+
            "		       var deltaX=this.snap(ai.x,delta,true); \r\n"+
            "		       this.moveSelected(deltaX,0,-deltaX,0,true);\r\n"+
            "		       break;\r\n"+
            "		    }\r\n"+
            "		    case 'T':{ \r\n"+
            "		      var deltaY=this.snap(ai.y,-delta,false); \r\n"+
            "		      this.moveSelected(0,deltaY,0,-deltaY,true);\r\n"+
            "		      break;\r\n"+
            "		    }\r\n"+
            "		    case 'R':{ \r\n"+
            "		      var deltaX=this.snap(ai.x+ai.w,delta,true); \r\n"+
            "		      this.moveSelected(0,0,deltaX,0,true);\r\n"+
            "		      break;\r\n"+
            "		    }\r\n"+
            "		    case 'B':{ \r\n"+
            "		      var deltaY=this.snap(ai.y+ai.h,-delta,false); \r\n"+
            "		      this.moveSelected(0,0,0,deltaY,true);\r\n"+
            "		      break;\r\n"+
            "		    }\r\n"+
            "		    case 'BL':{ \r\n"+
            "		      var deltaY=this.snap(ai.y+ai.h,-delta,false); \r\n"+
            "		      var deltaX=this.snap(ai.x,delta,true); \r\n"+
            "		      this.moveSelected(deltaX,0,-deltaX,deltaY,true);\r\n"+
            "		      break;\r\n"+
            "		    }\r\n"+
            "		    case 'TR':{ \r\n"+
            "		      var deltaY=this.snap(ai.y,-delta,false); \r\n"+
            "		      var deltaX=this.snap(ai.x+ai.w,delta,true); \r\n"+
            "		      this.moveSelected(0,deltaY,deltaX,-deltaY,true);\r\n"+
            "		      break;\r\n"+
            "		    }\r\n"+
            "		    case 'BR':{ \r\n"+
            "		      var deltaY=this.snap(ai.y+ai.h,delta,false); \r\n"+
            "		      var deltaX=this.snap(ai.x+ai.w,delta,true); \r\n"+
            "		      this.moveSelected(0,0,deltaX,deltaY,true);\r\n"+
            "		      break;\r\n"+
            "		    }\r\n"+
            "		    case 'TL':{\r\n"+
            "		      var deltaY=this.snap(ai.y,-delta,false); \r\n"+
            "		      var deltaX=this.snap(ai.x,-delta,true); \r\n"+
            "		      this.moveSelected(deltaX,deltaY,-deltaX,-deltaY,true);\r\n"+
            "		      break;\r\n"+
            "		    }\r\n"+
            "	      }\r\n"+
            "        }\r\n"+
            "		this.repaint();\r\n"+
            "	}else\r\n"+
            "		this.mouseWheelStart=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormEditor.prototype.moveSelected=function(x,y,w,h,sendToBackend){\r\n"+
            "	//if(this.activeItem!=null && this.activeItemType!='G' && !this.multiSelected[this.activeItem.id]){\r\n"+
            "		//var rect=this.activeItem;\r\n"+
            "		//rect.x+=x; rect.y+=y; rect.w+=w; rect.h+=h;\r\n"+
            "		//if(rect.w<2) rect.w=2;\r\n"+
            "		//if(rect.h<2) rect.h=2;\r\n"+
            "		//if(sendToBackend)\r\n"+
            "		  //this.callSetRectPos(rect);\r\n"+
            "	//}\r\n"+
            "	for(var i in this.multiSelected){\r\n"+
            "		var rect=this.rects[i];\r\n"+
            "		rect.x+=x; rect.y+=y; rect.w+=w; rect.h+=h;\r\n"+
            "		if(rect.w<2) rect.w=2;\r\n"+
            "		if(rect.h<2) rect.h=2;\r\n"+
            "		if(sendToBackend)\r\n"+
            "		  this.callSetRectPos(rect);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FormEditor.prototype.justSnappedX=function(){\r\n"+
            "	if(this.snappedMillisX>0){\r\n"+
            "	  if(Date.now()<this.snappedMillisX)\r\n"+
            "		return true;\r\n"+
            "	  this.snappedMillisX=0;\r\n"+
            "	}\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "FormEditor.prototype.justSnappedY=function(){\r\n"+
            "	if(this.snappedMillisY>0){\r\n"+
            "	  if(Date.now()<this.snappedMillisY)\r\n"+
            "		return true;\r\n"+
            "	  this.snappedMillisY=0;\r\n"+
            "	}\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FormEditor.prototype.snap=function(pos,delta,isX){\r\n"+
            "	if(delta==0)\r\n"+
            "	  return 0;\r\n"+
            "	var total=pos+delta;\r\n"+
            "	if(this.snapSize==-1){\r\n"+
            "	  var bestD=10;\r\n"+
            "	  var best=null;\r\n"+
            "	  for(var id in this.guides){\r\n"+
            "	    var guide=this.guides[id];\r\n"+
            "	    if(!guide.isVertical==isX) continue;\r\n"+
            "	    var d=Math.abs(guide.px-total);\r\n"+
            "	    if(d<bestD) \r\n"+
            "	      best=guide; bestD=d;\r\n"+
            "	  }\r\n"+
            "	  return best!=null ? best.px-pos : delta;\r\n"+
            "	}\r\n"+
            "	var best=null;\r\n"+
            "	var snapPos=Math.round(total/this.snapSize)*this.snapSize;\r\n"+
            "	var r=snapPos-pos;\r\n"+
            "	var bestD=Math.abs(total-snapPos);\r\n"+
            "	var best=null;\r\n"+
            "	for(var id in this.guides){\r\n"+
            "	  var guide=this.guides[id];\r\n"+
            "	  if(!guide.isVertical==isX) continue;\r\n"+
            "	  var d=Math.abs(guide.px-total);\r\n"+
            "	  if(d<bestD){ r=guide.px-pos; bestD=d; }\r\n"+
            "	}\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormEditor.prototype.callSetRectPos=function(ai){\r\n"+
            "  this.form.callBack('setRectPos',{id:ai.id, t:ai, x:ai.x, y:ai.y, w:ai.w, h:ai.h});\r\n"+
            "}\r\n"+
            "FormEditor.prototype.callSetGuidePos=function(ai){\r\n"+
            "  this.form.callBack('setGuidePos',{id:ai.id,pos:ai.px}); \r\n"+
            "}\r\n"+
            "FormEditor.prototype.onMouseMove=function(e){\r\n"+
            "  this.selectRect=null;\r\n"+
            "  var point=getMousePointRelativeTo(e,this.divElement);\r\n"+
            "  point.x=Math.max(0,point.x);\r\n"+
            "  point.y=Math.max(0,point.y);\r\n"+
            "  var px=point.x;\r\n"+
            "  var py=point.y;\r\n"+
            "  if(this.mouseWheelStart!=null){\r\n"+
            "	  if(Math.abs(this.mouseWheelStart.x-px)<10 && Math.abs(this.mouseWheelStart.y-py)<10)\r\n"+
            "		  return;\r\n"+
            "	  this.mouseWheelStart=null;\r\n"+
            "  }\r\n"+
            "  var snap=!e.shiftKey;\r\n"+
            "  if(this.dragStart!=null){\r\n"+
            "	  var ai=this.activeItem;\r\n"+
            "	  var dx=px-this.dragStart.x;\r\n"+
            "	  var dy=py-this.dragStart.y;\r\n"+
            "      if(this.activeItem==null){\r\n"+
            "	    this.selectRect={x:this.dragStart.x,y:this.dragStart.y,w:dx,h:dy};\r\n"+
            "	    this.repaint();\r\n"+
            "	    return;\r\n"+
            "      }\r\n"+
            "      // disable pointer event when dragging\r\n"+
            "	 for (var b of amiLinkableDivs) {\r\n"+
            "		 b.style.pointerEvents='none';\r\n"+
            "	 }\r\n"+
            "	  var origDy=dy;\r\n"+
            "	  var origDx=dx;\r\n"+
            "	  var t=this.activeItemType;\r\n"+
            "	  var jsy=false;//this.justSnappedY() && Math.abs(dy)<20;\r\n"+
            "	  var jsx=false;//this.justSnappedX() && Math.abs(dx)<20;\r\n"+
            "	  if(t=='G'){\r\n"+
            "		  if(ai.isVertical) ai.px+=dx; else ai.px+=dy; \r\n"+
            "	  }if(t=='C'){\r\n"+
            "	       if(snap){\r\n"+
            "	         dy=this.snap(ai.y,dy,false);\r\n"+
            "	         dx=this.snap(ai.x,dx,true);\r\n"+
            "	       }\r\n"+
            "	       this.moveSelected(dx,dy,0,0,false);\r\n"+
            "	  }else {\r\n"+
            "		    if(t=='T' || t=='TL' || t=='TR'){\r\n"+
            "		      if(snap)\r\n"+
            "	            dy=this.snap(ai.y,dy,false);\r\n"+
            "	          this.moveSelected(0,dy,0,-dy,false);\r\n"+
            "	        }\r\n"+
            "		    if(t=='B' || t=='BL' || t=='BR'){\r\n"+
            "		      if(snap)\r\n"+
            "	            dy=this.snap(ai.y+ai.h,dy,false);\r\n"+
            "	          this.moveSelected(0,0,0,dy,false);\r\n"+
            "	        }\r\n"+
            "		    if(t=='L' || t=='TL' || t=='BL'){\r\n"+
            "		      if(snap)\r\n"+
            "	            dx=this.snap(ai.x,dx,true); \r\n"+
            "	          this.moveSelected(dx,0,-dx,0,false);\r\n"+
            "	        }\r\n"+
            "		    if(t=='R' || t=='TR' || t=='BR'){\r\n"+
            "		      if(snap)\r\n"+
            "	            dx=this.snap(ai.x+ai.w,dx,true); \r\n"+
            "	          this.moveSelected(0,0,dx,0,false);\r\n"+
            "	        }\r\n"+
            "	  }\r\n"+
            "		  \r\n"+
            "      this.dragStart=point;\r\n"+
            "      this.dragStart.y-=origDy-dy;\r\n"+
            "      this.dragStart.x-=origDx-dx;\r\n"+
            "      this.repaint();\r\n"+
            "	  return;\r\n"+
            "  }\r\n"+
            "  // ensure pointer event is always on\r\n"+
            "  for (var b of amiLinkableDivs) {\r\n"+
            "	 b.style.pointerEvents='auto';\r\n"+
            " }\r\n"+
            "  var best=null;\r\n"+
            "  var bestT=null;\r\n"+
            "  var bestD=8;\r\n"+
            "  var d;\r\n"+
            "   for(var id in this.rects){\r\n"+
            "	  var rect=this.rects[id];\r\n"+
            "	  var x=rect.x, y=rect.y, xx=rect.x+rect.w, yy=rect.y+rect.h;\r\n"+
            "	  var s=rect.w>6 && rect.h>6 ? 2 : 0;\r\n"+
            "	  var h=x+s< px && px < xx-s;\r\n"+
            "	  var v=y+s< py && py < yy-s;\r\n"+
            "	  if(h && v){\r\n"+
            "	      best=rect;\r\n"+
            "	      bestT='C';\r\n"+
            "	      bestD=0;\r\n"+
            "	  }else if(h){\r\n"+
            "		  d=abs(py-y);\r\n"+
            "		  if(d<bestD){ best=rect; bestT='T'; bestD=d; }\r\n"+
            "		  d=abs(py-yy);\r\n"+
            "		  if(d<bestD){ best=rect; bestT='B'; bestD=d; }\r\n"+
            "	  }else if(v){\r\n"+
            "		  var d=abs(px-x);\r\n"+
            "		  if(d<bestD){ best=rect; bestT='L'; bestD=d; }\r\n"+
            "		  d=abs(px-xx);\r\n"+
            "		  if(d<bestD){ best=rect; bestT='R'; bestD=d; }\r\n"+
            "	  }else if(s>0){\r\n"+
            "		  d=max(abs(px-x),abs(py-y));\r\n"+
            "		  if(d<bestD){ best=rect; bestT='TL'; bestD=d; }\r\n"+
            "		  d=max(abs(px-x),abs(py-yy));\r\n"+
            "		  if(d<bestD){ best=rect; bestT='BL'; bestD=d; }\r\n"+
            "		  d=max(abs(px-xx),abs(py-y));\r\n"+
            "		  if(d<bestD){ best=rect; bestT='TR'; bestD=d; }\r\n"+
            "		  d=max(abs(px-xx),abs(py-yy));\r\n"+
            "		  if(d<bestD){ best=rect; bestT='BR'; bestD=d; }\r\n"+
            "	  }\r\n"+
            "   }\r\n"+
            "  \r\n"+
            "     for(var id in this.guides){\r\n"+
            "	    var guide=this.guides[id];\r\n"+
            "	    d=abs((guide.isVertical ? px : py)-guide.px);\r\n"+
            "	    if(d>=bestD)\r\n"+
            "	      continue;\r\n"+
            "	    bestD=d;\r\n"+
            "	    bestT='G';\r\n"+
            "	    best=guide;\r\n"+
            "     }\r\n"+
            "     if(best!=null){\r\n"+
            "    	 if(bestT=='G')\r\n"+
            "           this.canvas.style.cursor=best.isVertical ?  'ew-resize' :'ns-resize';\r\n"+
            "    	 else if(bestT=='C'){\r\n"+
            "            this.canvas.style.cursor='move';\r\n"+
            "    	 }else if(bestT=='T' || bestT=='B')\r\n"+
            "           this.canvas.style.cursor='ns-resize';\r\n"+
            "    	 else if(bestT=='L' || bestT=='R')\r\n"+
            "           this.canvas.style.cursor='ew-resize';\r\n"+
            "    	 else if(bestT=='TL' || bestT=='BR')\r\n"+
            "           this.canvas.style.cursor='nwse-resize';\r\n"+
            "    	 else if(bestT=='TR' || bestT=='BL')\r\n"+
            "           this.canvas.style.cursor='nesw-resize';\r\n"+
            "     }else\r\n"+
            "        this.canvas.style.cursor='default';\r\n"+
            "     if(this.activeItem!=best || this.activeItemType!=bestT){\r\n"+
            "	     this.activeItem=best;\r\n"+
            "	     this.activeItemType=bestT;\r\n"+
            "	     this.repaint();\r\n"+
            "     }\r\n"+
            "}\r\n"+
            "FormEditor.prototype.onMouseUp=function(e){\r\n"+
            "  if(this.trackingMouseDrag){\r\n"+
            "	this.trackingMouseDrag=false;\r\n"+
            "    document.onmouseup=null;\r\n"+
            "    document.onmousemove=null;\r\n"+
            "  }\r\n"+
            "  if(this.dragStart!=null && this.activeItem!=null){\r\n"+
            "	  var ai=thi");
          out.print(
            "s.activeItem;\r\n"+
            "	  if(this.activeItemType=='G')\r\n"+
            "	    this.form.callBack('setGuidePos',{id:this.activeItem.id,pos:this.activeItem.px});\r\n"+
            "	  else \r\n"+
            "		  this.moveSelected(0, 0,0, 0, true);\r\n"+
            "  }\r\n"+
            "  this.mouseWheelStart=null;\r\n"+
            "  this.dragStart=null;\r\n"+
            "  if(this.selectRect!=null){\r\n"+
            "	  var sr=this.selectRect;\r\n"+
            "	  for(var id in this.rects){\r\n"+
            "		var r=this.rects[id];\r\n"+
            "		if(isBetween(r.x+r.w/2,sr.x,sr.x+sr.w) && \r\n"+
            "		   isBetween(r.y+r.h/2,sr.y,sr.y+sr.h)){\r\n"+
            "		  this.multiSelected[r.id]=true;\r\n"+
            "		}\r\n"+
            "	  }\r\n"+
            "	  this.selectRect=null;\r\n"+
            "	  this.sendSelectedToBackend();\r\n"+
            "	  this.repaint();\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "FormEditor.prototype.onMouseOut=function(e){\r\n"+
            "	if (this.activeItem) {\r\n"+
            "		this.activeItem=null;\r\n"+
            "		this.activeItemType=null;\r\n"+
            "		this.repaint();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormEditor.prototype.onMouseDown=function(e){\r\n"+
            "	\r\n"+
            "	if(document.onmouseup==null){\r\n"+
            "	  var that=this;\r\n"+
            "	  this.trackingMouseDrag=true;\r\n"+
            "	  document.onmouseup=function(e){that.onMouseUp(e)};\r\n"+
            "	  document.onmousemove=function(e){that.onMouseMove(e)};\r\n"+
            "	}\r\n"+
            "\r\n"+
            "    this.mouseWheelStart=null;\r\n"+
            "    var p=getMousePoint(e);\r\n"+
            "	if(getMouseButton(e)==2){\r\n"+
            "		if(this.activeItem!=null)\r\n"+
            "		  this.form.callBack('editItem',{t:this.activeItemType,x:p.x,y:p.y,id:this.activeItem.id});\r\n"+
            "		else \r\n"+
            "		  this.form.callBack('editModeMenu',{x:p.x,y:p.y});\r\n"+
            "	}else{\r\n"+
            "        this.dragStart=getMousePointRelativeTo(e,this.divElement);\r\n"+
            "	}\r\n"+
            "	if(this.activeItemType!='G'){\r\n"+
            "	  if(this.activeItem!=null){\r\n"+
            "	    if(e.ctrlKey){\r\n"+
            "		if(this.multiSelected[this.activeItem.id])\r\n"+
            "			delete this.multiSelected[this.activeItem.id];\r\n"+
            "		else \r\n"+
            "			this.multiSelected[this.activeItem.id]=true;\r\n"+
            "	    }else if(e.shiftKey){\r\n"+
            "		  this.multiSelected[this.activeItem.id]=true;\r\n"+
            "	    }else if(!this.multiSelected[this.activeItem.id]){\r\n"+
            "	      this.multiSelected={};\r\n"+
            "		  this.multiSelected[this.activeItem.id]=true;\r\n"+
            "	    }\r\n"+
            "	  }else if(!e.shiftKey){\r\n"+
            "	    this.multiSelected={};\r\n"+
            "	  }\r\n"+
            "      this.sendSelectedToBackend();\r\n"+
            "	}\r\n"+
            "	this.repaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormEditor.prototype.sendSelectedToBackend=function(){\r\n"+
            "//  this.form.callBack('editModeSelection',{values:joinMap(',','=',Object.keys(this.multiSelected).toString())});\r\n"+
            "  this.form.callBack('editModeSelection',{values:JSON.stringify(Object.keys(this.multiSelected)), active:this.activeItem != null? this.activeItem.id :null});\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormEditor.prototype.setSize=function(width,height){\r\n"+
            "	  width = Math.max(this.form.formDOMManager.formContainer.clientWidth,width);\r\n"+
            "	  height = Math.max(this.form.formDOMManager.formContainer.clientHeight,height);\r\n"+
            "	  this.canvas.style.width=toPx(width);\r\n"+
            "	  this.canvas.style.height=toPx(height);\r\n"+
            "	  this.canvas.width=width;\r\n"+
            "	  this.canvas.height=height;\r\n"+
            "      this.context.translate(.5,.5);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FormEditor.prototype.addGuide=function(id,px,isVertical){\r\n"+
            "	this.guides[id]={id:id,px:px,isVertical:isVertical};\r\n"+
            "}\r\n"+
            "FormEditor.prototype.addRect=function(id,x,y,w,h,isSelected){\r\n"+
            "	this.rects[id]={id:id,x:x,y:y,w:w,h:h};\r\n"+
            "	if(isSelected)\r\n"+
            "	  this.multiSelected[id]=true;\r\n"+
            "	else\r\n"+
            "	  delete this.multiSelected[id];\r\n"+
            "}\r\n"+
            "FormEditor.prototype.removeGuide=function(id){\r\n"+
            "	delete this.guides[id];\r\n"+
            "}\r\n"+
            "FormEditor.prototype.setSnap=function(snapSize){\r\n"+
            "    this.snapSize=snapSize;\r\n"+
            "}\r\n"+
            "FormEditor.prototype.removeRect=function(id){\r\n"+
            "	delete this.rects[id];\r\n"+
            "}\r\n"+
            "FormEditor.prototype.repaint=function(){\r\n"+
            "    this.canvasMap.clear();\r\n"+
            "//	this.context.fillStyle='rgba(0,0,0,0)';\r\n"+
            "//	this.canvasMap.fillRect(0,0,this.context.width,this.context.height);\r\n"+
            "    this.context.lineWidth=1;\r\n"+
            "    if(this.snapSize>1){\r\n"+
            "		this.context.strokeStyle='#00AA00';\r\n"+
            "	    this.context.beginPath();\r\n"+
            "      for(var x=0;x<this.canvas.width+1;x+=this.snapSize){\r\n"+
            "        for(var y=0;y<this.canvas.height+1;y+=this.snapSize){\r\n"+
            "	      this.canvasMap.moveTo(x,y);\r\n"+
            "	      this.canvasMap.lineTo(x+1,y+1);\r\n"+
            "        }\r\n"+
            "      }\r\n"+
            "	    this.context.stroke();\r\n"+
            "      \r\n"+
            "    }\r\n"+
            "	for(var id in this.guides){\r\n"+
            "		var guide=this.guides[id];\r\n"+
            "		this.context.fillStyle=this.context.strokeStyle=guide==this.activeItem ? '#00AA00' : (this.activeItem==null ? 'rgba(200,200,200,.5)' : \"#AAAAAA\") ;\r\n"+
            "	    this.context.beginPath();\r\n"+
            "	    if(guide.isVertical){\r\n"+
            "	      this.canvasMap.moveTo(guide.px,0);\r\n"+
            "	      this.canvasMap.lineTo(guide.px,this.canvas.height+1);\r\n"+
            "	      if(guide==this.activeItem)\r\n"+
            "			this.canvasMap.drawText(guide.px+\" px\",guide.px-2,2,0,ALIGN_RIGHT,ALIGN_TOP);\r\n"+
            "	    }else{\r\n"+
            "	      this.canvasMap.moveTo(0,guide.px);\r\n"+
            "	      this.canvasMap.lineTo(this.canvas.width+1,guide.px);\r\n"+
            "	      if(guide==this.activeItem)\r\n"+
            "			this.canvasMap.drawText(guide.px+\" px\",2,guide.px-2,0,ALIGN_LEFT,ALIGN_BOTTOM);\r\n"+
            "	    }\r\n"+
            "	    this.context.stroke();\r\n"+
            "	}\r\n"+
            "	for(var id in this.rects){\r\n"+
            "		var rect=this.rects[id];\r\n"+
            "		var isActive=rect==this.activeItem;\r\n"+
            "		var isSelected=this.multiSelected[rect.id];\r\n"+
            "		if(!isSelected && !isActive)\r\n"+
            "			continue;\r\n"+
            "		this.context.fillStyle=isActive? 'rgba(0,255,0,.3)' : (isSelected ? 'rgba(0,255,0,.1)' : 'rgba(0,0,0,.0)');\r\n"+
            "		\r\n"+
            "	    this.canvasMap.fillRect(rect.x,rect.y,rect.w,rect.h);\r\n"+
            "	    \r\n"+
            "//	    this.context.clearRect(rect.x+3,rect.y+3,rect.w-6,rect.h-6);\r\n"+
            "		this.context.fillStyle=this.context.strokeStyle=isSelected||isActive? '#00AA00' : '#888888';\r\n"+
            "	    this.context.beginPath();\r\n"+
            "	    this.canvasMap.strokeRect(rect.x,rect.y,rect.w,rect.h);\r\n"+
            "	    if(isActive || isSelected){\r\n"+
            "			this.canvasMap.drawText(\"(\"+rect.x+\", \"+rect.y+\") \",rect.x,rect.y,45,ALIGN_RIGHT,ALIGN_MIDDLE);\r\n"+
            "			this.canvasMap.drawText(\" (\"+(rect.x+rect.w)+\", \"+(rect.y+rect.h)+\") \",rect.x+rect.w,rect.y+rect.h,45,ALIGN_LEFT,ALIGN_MIDDLE);\r\n"+
            "			if(rect.w<28)\r\n"+
            "			  this.canvasMap.drawText(\"(\"+rect.w+\" w) \",rect.x+rect.w/2,rect.y,-90,ALIGN_LEFT,ALIGN_MIDDLE);\r\n"+
            "			else\r\n"+
            "			  this.canvasMap.drawText(\"(\"+rect.w+\" w)\",rect.x+rect.w/2,rect.y,0,ALIGN_MIDDLE,ALIGN_BOTTOM);\r\n"+
            "			if(rect.h<28)\r\n"+
            "			  this.canvasMap.drawText(\" (\"+rect.h+\" h)\",rect.x+rect.w,rect.y+rect.h/2,0,ALIGN_LEFT,ALIGN_MIDDLE);\r\n"+
            "			else\r\n"+
            "			  this.canvasMap.drawText(\"(\"+rect.h+\" h)\",rect.x+rect.w,rect.y+rect.h/2,90,ALIGN_MIDDLE,ALIGN_BOTTOM);\r\n"+
            "	      }\r\n"+
            "	    this.context.stroke();\r\n"+
            "	}\r\n"+
            "	if(this.selectRect!=null){\r\n"+
            "		this.context.strokeStyle='#444444';\r\n"+
            "		this.context.save();\r\n"+
            "        this.context.lineWidth=2;\r\n"+
            "        this.context.translate(-.5,-.5);\r\n"+
            "		this.context.setLineDash([3,3]);\r\n"+
            "	    this.canvasMap.strokeRect(this.selectRect.x,this.selectRect.y,this.selectRect.w,this.selectRect.h);\r\n"+
            "		this.context.restore();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "");

	}
	
}