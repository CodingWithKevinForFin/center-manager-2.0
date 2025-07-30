package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_treemap_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_treemap_js_1() {
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
            "function TreeMap(element){ \r\n"+
            "	  var that=this; \r\n"+
            "	  this.setRatio(4);\r\n"+
            "	  this.clearData();\r\n"+
            "	  this.element=element;\r\n"+
            "	  this.selectBorderColor1='#0000AA';\r\n"+
            "	  this.selectBorderColor2='#AAAAFF';\r\n"+
            "	  this.backgroundColor='white';\r\n"+
            "	  this.element.style.background=this.backgroundColor;\r\n"+
            "		\r\n"+
            "	  this.setStickyness(0);\r\n"+
            "	  \r\n"+
            "	  \r\n"+
            "	  this.canvas=nw('canvas');\r\n"+
            "	  this.canvas.style.position='absolute';\r\n"+
            "	  this.canvas.style.background='white';\r\n"+
            "	  this.canvas.style.left='0px';\r\n"+
            "	  this.canvas.style.top='0px';\r\n"+
            "	  this.canvas.width=1500;\r\n"+
            "	  this.canvas.height=700;\r\n"+
            "	  this.context = this.canvas.getContext('2d');\r\n"+
            "	  \r\n"+
            "	  this.canvas2=nw('canvas');\r\n"+
            "	  this.canvas2.style.position='absolute';\r\n"+
            "	  this.canvas2.style.left=this.canvas.style.left;\r\n"+
            "	  this.canvas2.style.top=this.canvas.style.top;\r\n"+
            "	  this.canvas2.width=this.canvas.width;\r\n"+
            "	  this.canvas2.height=this.canvas.width;\r\n"+
            "	  this.context2 = this.canvas2.getContext('2d');\r\n"+
            "	  \r\n"+
            "	  \r\n"+
            "	  this.zoom=1;\r\n"+
            "	  this.offsetX=0;\r\n"+
            "	  this.offsetY=0;\r\n"+
            "	  this.canvas2.onMouseWheel=function(e,delta){that.setZoom(e,delta);};\r\n"+
            "	  this.canvas2.onmousemove=function(e){that.onMouseMove(getMousePoint(e))};\r\n"+
            "	  this.canvas2.onmouseout=function(e){that.onMouseOut(e);};\r\n"+
            "	  this.canvas2.onmousedown=function(e){that.onMouseDown(e);};\r\n"+
            "	  \r\n"+
            "	  //MOBILE SUPPORT - support double click to zoom, touch move & touch hold for context menu\r\n"+
            "	  this.canvas2.ontouchmove=function(e){that.onTouchDrag(e);};\r\n"+
            "	  this.canvas2.ontouchend=function(e){that.onTouchup(e);};\r\n"+
            "	  this.canvas2.ontouchstart=function(e){e.stopPropagation(); that.onUserClick(that.activeNode ? that.activeNode.id : null,e.shiftKey,e.ctrlKey,getMouseButton(e));that.onTouchDown(e,null,null,null,0);};\r\n"+
            "	  this.canvas2.addEventListener('touchend', function(e) {\r\n"+
            "		    that.onDblTap(e);\r\n"+
            "		});\r\n"+
            "	  this.canvas2.oncontextmenu=function(e){if(that.onClick) that.onClick(e,that.activeNode)};\r\n"+
            "	  this.element.appendChild(this.canvas);\r\n"+
            "	  this.element.appendChild(this.canvas2);\r\n"+
            "	  this.context.lineCap='butt';\r\n"+
            "	  this.draggingXdiff=null;\r\n"+
            "	  this.draggingYdiff=null;\r\n"+
            "	  this.hoverRequestNid=null;\r\n"+
            "	  this.borderSizes=[];\r\n"+
            "	  this.textSizes=[];\r\n"+
            "	  this.bgColors=[];\r\n"+
            "	  this.borderColors=[];\r\n"+
            "	  this.textColors=[];\r\n"+
            "	  this.needsLayout=true;\r\n"+
            "	  this.textHAlign='center';\r\n"+
            "	  this.textVAlign='center';\r\n"+
            "	  this.touchXDiff=this.offsetX;\r\n"+
            "	  this.touchYDiff=this.offsetY;\r\n"+
            "	} \r\n"+
            "\r\n"+
            "    TreeMap.prototype.onMouseDown=function(e){\r\n"+
            "	  var button=getMouseButton(e);\r\n"+
            "	  if(button==1|| button==2){\r\n"+
            "	      this.wasDragged=false;\r\n"+
            "	      var point=getMouseLayerPoint(e);\r\n"+
            "		  this.draggingXdiff=point.x+this.offsetX;\r\n"+
            "		  this.draggingYdiff=point.y+this.offsetY;\r\n"+
            "		  this.canvas2.style.cursor='move';\r\n"+
            "		  var that=this;\r\n"+
            "          this.owningWindow.onmouseup=function(e){\r\n"+
            "        	  that.onMouseup(e)\r\n"+
            "        	  };\r\n"+
            "		  this.owningWindow.onmousemove=function(e){that.onMouseDrag(e)};\r\n"+
            "	  }\r\n"+
            "    }\r\n"+
            "    //MOBILE SUPPORT - on touch down\r\n"+
            "    TreeMap.prototype.onTouchDown=function(e){\r\n"+
            "    //touch hold delay for menu\r\n"+
            "  	  this.longTouchTimer = setTimeout(() => {\r\n"+
            "			if(this.zoom > 1){\r\n"+
            "				var p=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "				var x=p.x;\r\n"+
            "				var y=p.y;\r\n"+
            "				this.currentMousePoint=p;\r\n"+
            "				this.onTouchHold(this.activeNode ? this.activeNode.id : 1,false,false,2)\r\n"+
            "				return;\r\n"+
            "			}\r\n"+
            "	  }, 500);\r\n"+
            "    	\r\n"+
            "  	  var button=getMouseButton(e);\r\n"+
            "	  if(this.hoverDiv!=null)\r\n"+
            "		    getRootNodeBody(this.element).removeChild(this.hoverDiv);\r\n"+
            "	  this.hoverDiv=null;\r\n"+
            "  	  this.wasDragged=false;\r\n"+
            "  	  var point=getMousePoint(e);\r\n"+
            "  	  this.touchXDiff=point.x+this.offsetX;\r\n"+
            "  	  this.touchYDiff=point.y+this.offsetY;\r\n"+
            "  	  var that=this;\r\n"+
            "      this.owningWindow.ontouchend=function(e){\r\n"+
            "    	  that.onTouchup(e)\r\n"+
            "      };\r\n"+
            "  	  this.owningWindow.ontouchmove= function(e){\r\n"+
            "       	  that.onTouchDrag(e)\r\n"+
            "   	  };\r\n"+
            "   }\r\n"+
            "    \r\n"+
            "	TreeMap.prototype.onMouseDrag=function(e){\r\n"+
            "          var point=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "            this.setOffset(this.draggingXdiff-point.x,this.draggingYdiff-point.y);\r\n"+
            "    	    if(this.zoom>1)\r\n"+
            "    	      this.wasDragged=true;\r\n"+
            "	    this.currentMousePoint=point;\r\n"+
            "	    \r\n"+
            "	}\r\n"+
            "    \r\n"+
            "    TreeMap.prototype.clearHover=function(e){\r\n"+
            "  	  if(this.hoverDiv!=null){\r\n"+
            "	  		try {\r\n"+
            "	  			getDocument(this.element).body.removeChild(this.hoverDiv);\r\n"+
            "	  		} catch (e) {\r\n"+
            "	  			console.log(\"unable to clear heatmap hover: \" + e);\r\n"+
            "	  		}\r\n"+
            "	  		this.hoverDiv=null;\r\n"+
            "	  	  }\r\n"+
            "      }\r\n"+
            "\r\n"+
            "    //MOBILE SUPPORT - on touch move\r\n"+
            "	TreeMap.prototype.onTouchDrag=function(e){\r\n"+
            "		//clear touch hold delay for menu\r\n"+
            "		clearTimeout(this.longTouchTimer);\r\n"+
            "		this.longTouchTimer=null;\r\n"+
            "        var point=getMousePointRelativeTo(e,this.canvas);\r\n"+
            "        if(this.touchDiff != null){\r\n"+
            "        	var diffX = Math.min(10*this.zoom, Math.max(-10*this.zoom, this.touchDiff.x-point.x));\r\n"+
            "        	var diffY = Math.min(10*this.zoom, Math.max(-10*this.zoom, this.touchDiff.y-point.y));       	  \r\n"+
            "        }\r\n"+
            "        // checks if heatmap is zoomed. otherwise, dont drag\r\n"+
            "		if(this.zoom>1)\r\n"+
            "			this.setTouchOffset((isNaN(this.offsetX) ? 0 : this.offsetX)  + diffX,(isNaN(this.offsetY) ? 0 : this.offsetY)  + diffY);\r\n"+
            "    	   this.wasDragged=true;\r\n"+
            "    	   this.touchDiff= point;\r\n"+
            "	    \r\n"+
            "	}	\r\n"+
            "	TreeMap.prototype.onDblTap = (function() {\r\n"+
            "	    let lastTapTime = 0;\r\n"+
            "	    const doubleTapDelay = 200; // Maximum time (ms) between taps for it to be considered a double tap\r\n"+
            "\r\n"+
            "	    return function(e) {\r\n"+
            "	        const currentTime = new Date().getTime();\r\n"+
            "	        const timeSinceLastTap = currentTime - lastTapTime;\r\n"+
            "\r\n"+
            "	        if (timeSinceLastTap < doubleTapDelay && timeSinceLastTap > 0) {\r\n"+
            "	            const p = getMousePointRelativeTo(e, this.canvas);\r\n"+
            "	            this.setZoom(e, this.zoom * 1.5);\r\n"+
            "	        }\r\n"+
            "\r\n"+
            "	        lastTapTime = currentTime;\r\n"+
            "	    };\r\n"+
            "	})();\r\n"+
            "    TreeMap.prototype.onMouseOut=function(e){\r\n"+
            "	  if(this.hoverDiv!=null)\r\n"+
            "	    getRootNodeBody(this.element).removeChild(this.hoverDiv);\r\n"+
            "	  this.hoverDiv=null;\r\n"+
            "	  this.hoverRequestNid=null;\r\n"+
            "	  if(!this.showingMenu)\r\n"+
            "	    this.onMouseMove(null)\r\n"+
            "    }\r\n"+
            "    TreeMap.prototype.onMouseup=function(e){\r\n"+
            "    	clearTimeout(this.longTouchTimer);\r\n"+
            "    	this.longTouchTimer=null;\r\n"+
            "		this.draggingXdiff=null;\r\n"+
            "		this.draggingYdiff=null;\r\n"+
            "    	this.canvas2.style.cursor=null;\r\n"+
            "    	this.owningWindow.onmouseup=null;\r\n"+
            "    	this.owningWindow.onmousemove=null;\r\n"+
            "        if(!this.wasDragged){\r\n"+
            "	  if(this.onUserClick)\r\n"+
            "	    this.onUserClick(this.activeNode ? this.activeNode.id : null,e.shiftKey,e.ctrlKey,getMouseButton(e));\r\n"+
            "        }\r\n"+
            "    }\r\n"+
            "    //MOBILE SUPPORT - on touch up\r\n"+
            "    TreeMap.prototype.onTouchup=function(e){\r\n"+
            "    	clearTimeout(this.longTouchTimer);\r\n"+
            "    	this.longTouchTimer=null;\r\n"+
            "    	this.canvas2.style.cursor=null;\r\n"+
            "        this.owningWindow.ontouchend=null;\r\n"+
            "		this.owningWindow.ontouchmove=null;\r\n"+
            "		// if not dragged, select node\r\n"+
            "        if(!this.wasDragged){\r\n"+
            "		  if(this.onUserClick)\r\n"+
            "		    this.onUserClick(this.activeNode ? this.activeNode.id : null,e.shiftKey,e.ctrlKey,getMouseButton(e));\r\n"+
            "        }\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    /* 0=no sticky 1=very stick */\r\n"+
            "    TreeMap.prototype.setStickyness=function(o){\r\n"+
            "      if(o<0)o=0;\r\n"+
            "      else if(o>2)o=5;\r\n"+
            "	  this.aspectStickyness=1+o;\r\n"+
            "	  this.sortStickyness=1+o;\r\n"+
            "    }\r\n"+
            "  //MOBILE SUPPORT - cancel zoom menu for touch\r\n"+
            "    TreeMap.prototype.cancelZoom=function(){\r\n"+
            "    	this.zoom=1;\r\n"+
            "	    this.setOffset(0,0);\r\n"+
            "	    this.updateActiveNode();\r\n"+
            "    	this.repaint();\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    \r\n"+
            "    TreeMap.prototype.setZoom=function(e,delta){\r\n"+
            "		var point=getMouseLayerPoint(e);\r\n"+
            "		\r\n"+
            "		var pointX=point.x;\r\n"+
            "		var pointY=point.y;\r\n"+
            "		if(pointX==null)\r\n"+
            "		  pointX=this.canvas.width/2;\r\n"+
            "		if(pointY==null)\r\n"+
            "		  pointY=this.canvas.height/2;\r\n"+
            "    	var xpos=(pointX+this.offsetX)/this.zoom;\r\n"+
            "    	var ypos=(pointY+this.offsetY)/this.zoom;\r\n"+
            "    	\r\n"+
            "    	delta=delta/5;\r\n"+
            "    	this.zoom*=(1+delta);\r\n"+
            "    	if(this.zoom<1){\r\n"+
            "    		this.zoom=1;\r\n"+
            "    	    this.setOffset(0,0);\r\n"+
            "    	}else{\r\n"+
            "    	  this.setOffset(xpos*this.zoom-pointX,ypos*this.zoom-pointY);\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "    TreeMap.prototype.setOffset=function(x,y){\r\n"+
            "  	  var w=this.canvas.width;\r\n"+
            "  	  var h=this.canvas.height;\r\n"+
            "  	  this.offsetX=x;\r\n"+
            "  	  this.offsetY=y;\r\n"+
            "  	  if(this.zoom*w<w+this.offsetX)\r\n"+
            "  		  this.offsetX=(this.zoom-1)*w;\r\n"+
            "  	  if(this.zoom*h<h+this.offsetY)\r\n"+
            "  		  this.offsetY=(this.zoom-1)*h;\r\n"+
            "  	  \r\n"+
            "  	  if(this.offsetX<0)\r\n"+
            "  	    this.offsetX=0;\r\n"+
            "  	  if(this.offsetY<0)\r\n"+
            "  	    this.offsetY=0;\r\n"+
            "  	    this.activeNode=null;\r\n"+
            "  	this.repaint();\r\n"+
            "  }\r\n"+
            "    \r\n"+
            "  //MOBILE SUPPORT - touch offset for heatmap\r\n"+
            "    TreeMap.prototype.setTouchOffset=function(x,y){\r\n"+
            "  	  var w=this.canvas.width;\r\n"+
            "  	  var h=this.canvas.height;\r\n"+
            "  	  this.offsetX=x;\r\n"+
            "  	  this.offsetY=y;\r\n"+
            "  	  if(this.zoom*w<w+this.offsetX)\r\n"+
            "  		  this.offsetX=(this.zoom-1)*w;\r\n"+
            "  	  if(this.zoom*h<h+this.offsetY)\r\n"+
            "  		  this.offsetY=(this.zoom-1)*h;\r\n"+
            "  	  \r\n"+
            "  	  if(this.offsetX<0)\r\n"+
            "  	    this.offsetX=0;\r\n"+
            "  	  if(this.offsetY<0)\r\n"+
            "  	    this.offsetY=0;\r\n"+
            "  	    this.activeNode=null;\r\n"+
            "  	  this.repaint();\r\n"+
            "  }\r\n"+
            "    TreeMap.prototype.setOptions=function(o){\r\n"+
            "	  if(o.stickyness)this.setStickyness(o.stickyness*1);\r\n"+
            "	  if(o.selectBorderColor1)this.selectBorderColor1=o.selectBorderColor1;\r\n"+
            "	  if(o.selectBorderColor2)this.selectBorderColor2=o.selectBorderColor2;\r\n"+
            "	  if(o.ratio)this.setRatio(o.ratio*1);\r\n"+
            "	  if(o.backgroundColor)this.backgroundColor=o.backgroundColor;\r\n"+
            "	  if(o.backgroundColor)this.backgroundColor=o.backgroundColor;\r\n"+
            "	  if(o.textHAlign)this.textHAlign=o.textHAlign;\r\n"+
            "	  if(o.textVAlign)this.textVAlign=o.textVAlign;\r\n"+
            "	  if(o.fontFamily)this.fontFamily=o.fontFamily;\r\n"+
            "	  this.needsLayout=true;\r\n"+
            "    }    \r\n"+
            "\r\n"+
            "	TreeMap.prototype.clearData=function(data){\r\n"+
            "		this.nodes=[];\r\n"+
            "	    this.needsLayout=true;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	TreeMap.prototype.removeNodes=function(ids){\r\n"+
            "		for(var i=0;i<ids.length;i++)\r\n"+
            "		  this.removeNode(ids[i]);\r\n"+
            "	    this.needsLayout=true;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	TreeMap.prototype.setDepthStyles=function(data){\r\n"+
            "		for(var i=0;i<data.length;i++){\r\n"+
            "			var d=data[i];\r\n"+
            "			var depth=d.depth;\r\n"+
            "			this.borderSizes[depth]=d.borderSize;\r\n"+
            "			thi");
          out.print(
            "s.textSizes[depth]=d.textSize;\r\n"+
            "			this.borderColors[depth]=d.borderColor;\r\n"+
            "			this.textColors[depth]=d.textColor;\r\n"+
            "			this.bgColors[depth]=d.bgColor;\r\n"+
            "		}\r\n"+
            "	    this.needsLayout=true;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	TreeMap.prototype.addNodes=function(data){\r\n"+
            "		for(var i=0;i<data.length;i++)\r\n"+
            "		  this.addNode(data[i]);\r\n"+
            "	    this.needsLayout=true;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	TreeMap.prototype.addNode=function(data){\r\n"+
            "		var pid=data.pid;\r\n"+
            "		var parent;\r\n"+
            "		if(pid!=-1){\r\n"+
            "		  parent=this.nodes[pid];\r\n"+
            "		  if(parent==null && pid!=-1)//we'll create a dummy, with the expectation that the parent will arrive soon\r\n"+
            "			parent=this.nodes[data.pid]={id:data.pid,children:{},childrenCount:0};\r\n"+
            "	      parent.childrenSorted=null;\r\n"+
            "		}else\r\n"+
            "			parent=null;\r\n"+
            "		\r\n"+
            "		var id=data.id;\r\n"+
            "		var existing=this.nodes[id];\r\n"+
            "		if(existing==null)\r\n"+
            "			existing=this.nodes[id]={id:id,parent:parent,childrenCount:0};\r\n"+
            "		else \r\n"+
            "			existing.parent=parent;\r\n"+
            "		existing.size=data.v;\r\n"+
            "		existing.heat=data.h; \r\n"+
            "		existing.textColor=data.t; \r\n"+
            "		existing.selected=data.s==1;\r\n"+
            "		this.applyName(existing,data.n);\r\n"+
            "		if(pid==-1)\r\n"+
            "			this.rootNode=existing;\r\n"+
            "		else{\r\n"+
            "			if(parent.children==null)\r\n"+
            "				parent.children={};\r\n"+
            "			parent.children[id]=existing;\r\n"+
            "			parent.childrenCount++;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	TreeMap.prototype.removeNode=function(id){\r\n"+
            "		var node=this.nodes[id];\r\n"+
            "		if(node==null)\r\n"+
            "			return;\r\n"+
            "		  node.parent.childrenSorted=null;\r\n"+
            "		  if(--node.parent.childrenCount==0)\r\n"+
            "			  delete node.parent.children;\r\n"+
            "		  else\r\n"+
            "		    delete node.parent.children[id];\r\n"+
            "		delete this.nodes[id];\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	TreeMap.prototype.applyName=function(node,name){\r\n"+
            "		if(node.name==name)\r\n"+
            "			return;\r\n"+
            "		var names=name.split('\\n');\r\n"+
            "		var maxLen=this.context.measureText(names[0]).width;\r\n"+
            "		var maxName=names[0];\r\n"+
            "		for(var i=1;i<names.length;i++){\r\n"+
            "	       var len=this.context.measureText(names[i]).width;\r\n"+
            "	       if(len<maxLen)\r\n"+
            "	    	   continue;\r\n"+
            "	       maxLen=len;\r\n"+
            "	       maxName=names[i];\r\n"+
            "		}\r\n"+
            "		node.name=name;\r\n"+
            "		node.names=names;\r\n"+
            "		node.maxName=maxName;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "\r\n"+
            "		\r\n"+
            "	TreeMap.prototype.setSize=function(w,h){\r\n"+
            "		this.keyGradientWidth=200;\r\n"+
            "		this.width=w;\r\n"+
            "		this.height=h;\r\n"+
            "		this.element.width=w;\r\n"+
            "		this.element.height=h;\r\n"+
            "		this.canvas.width=this.width;\r\n"+
            "		this.canvas.height=this.height;\r\n"+
            "		this.canvas2.width=this.canvas.width;\r\n"+
            "		this.canvas2.height=this.canvas.height;\r\n"+
            "    	if(this.zoom*w<w+this.offsetX)\r\n"+
            "    	  this.offsetX=(this.zoom-1)*w;\r\n"+
            "    	if(this.zoom*h<h+this.offsetY)\r\n"+
            "    	  this.offsetY=(this.zoom-1)*h;\r\n"+
            "    	if(this.offsetX<0)\r\n"+
            "    	  this.offsetX=0;\r\n"+
            "    	if(this.offsetY<0)\r\n"+
            "    	  this.offsetY=0;\r\n"+
            "	    this.needsLayout=true;\r\n"+
            "		this.repaint();\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	TreeMap.prototype.onMouseMove=function(point){\r\n"+
            "		if(point!=null){\r\n"+
            "	      var t=new Rect().readFromElement(this.canvas2);\r\n"+
            "	      point.move(-t.left,-t.top);\r\n"+
            "		}\r\n"+
            "	    this.currentMousePoint=point;\r\n"+
            "	    this.updateActiveNode();\r\n"+
            "	    if(point!=null && this.onHover && this.activeNode){\r\n"+
            "	    	if(this.activeNode.id!=this.hoverRequestNid){\r\n"+
            "	    	  this.hoverRequestNid=this.activeNode.id;\r\n"+
            "	    	  this.onHover(point.x,point.y,this.activeNode.id);\r\n"+
            "	        }else\r\n"+
            "	           this.updateHoverLocation();\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	//MOBILE SUPPORT - touch move for heatmap\r\n"+
            "	TreeMap.prototype.onTouchMove=function(point){\r\n"+
            "		if(point!=null){\r\n"+
            "	      var t=new Rect().readFromElement(this.canvas2);\r\n"+
            "	      point.move(-t.left,-t.top);\r\n"+
            "		}\r\n"+
            "	    this.currentMousePoint=point;\r\n"+
            "	    this.updateActiveNode();\r\n"+
            "	    if(point!=null && this.onHover && this.activeNode){\r\n"+
            "	    	if(this.activeNode.id!=this.hoverRequestNid){\r\n"+
            "	    	  this.hoverRequestNid=this.activeNode.id;\r\n"+
            "	    	  this.onHover(point.x,point.y,this.activeNode.id);\r\n"+
            "	        }else\r\n"+
            "	           this.updateHoverLocation();\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "	TreeMap.prototype.setHover=function(nid,name){\r\n"+
            "	  if(this.activeNode==null || this.activeNode.id!=nid)\r\n"+
            "		  return;\r\n"+
            "		const rootBody = getRootNodeBody(this.element);\r\n"+
            "		if(this.hoverDiv!=null)\r\n"+
            "		  rootBody.removeChild(this.hoverDiv);\r\n"+
            "		this.hoverDiv=nw(\"div\",\"treemap_tooltip\");\r\n"+
            "		this.hoverDiv.innerHTML=name;\r\n"+
            "		if(this.hoverDiv.firstChild!=null && this.hoverDiv.firstChild.tagName=='DIV')\r\n"+
            "			this.hoverDiv=this.hoverDiv.firstChild;\r\n"+
            "		rootBody.appendChild(this.hoverDiv);\r\n"+
            "		this.updateHoverLocation();\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	TreeMap.prototype.updateHoverLocation=function(){\r\n"+
            "	  var div=this.hoverDiv;\r\n"+
            "	  if(div==null)\r\n"+
            "		  return;\r\n"+
            "	  var x=this.currentMousePoint.x;\r\n"+
            "	  var y=this.currentMousePoint.y;\r\n"+
            "	  var origin = this.element.getBoundingClientRect();\r\n"+
            "	  var rect=new Rect().readFromElement(div);\r\n"+
            "	  var h=rect.height+4;\r\n"+
            "	  var w=rect.width+6;\r\n"+
            "	  div.style.left=toPx(origin.x+x-w);\r\n"+
            "	  div.style.top=toPx(origin.y+y-h);\r\n"+
            "	  ensureInDiv(div,getRootNodeBody(this.element));\r\n"+
            "	}\r\n"+
            "\r\n"+
            "\r\n"+
            "	TreeMap.prototype.updateActiveNode=function(){\r\n"+
            "		var point=this.currentMousePoint;\r\n"+
            "		var node = null;\r\n"+
            "		if(point==null)\r\n"+
            "			node=null;\r\n"+
            "		else{\r\n"+
            "    	  var xpos=(point.x+this.offsetX)/this.zoom;\r\n"+
            "    	  var ypos=(point.y+this.offsetY)/this.zoom;\r\n"+
            "	      if(this.activeNode!=null && this.activeNode.x<=xpos && this.activeNode.x+this.activeNode.w>xpos && this.activeNode.y<=ypos && this.activeNode.y+this.activeNode.h>ypos){\r\n"+
            "	    	return;\r\n"+
            "	      }\r\n"+
            "	      node=this.findNodeAt(xpos,ypos,this.nodes);\r\n"+
            "		}\r\n"+
            "	    if(this.activeNode==node)\r\n"+
            "	    	return;\r\n"+
            "	    this.activeNode=node;\r\n"+
            "	    this.context2.clearRect(0,0,this.canvas2.width,this.canvas2.height);\r\n"+
            "	    return node;\r\n"+
            "	}\r\n"+
            "	TreeMap.prototype.updateWhiteHighlight=function(node){\r\n"+
            "		  if (!node) return;\r\n"+
            "	      this.context2.save();\r\n"+
            "		  this.context2.translate(-this.offsetX,-this.offsetY);\r\n"+
            "		  this.context2.scale(this.zoom,this.zoom);\r\n"+
            "		  this.context2.beginPath();\r\n"+
            "		  this.context2.fillStyle='white';\r\n"+
            "	      this.context2.globalAlpha=.3;\r\n"+
            "		  this.context2.fillRect(node.x,node.y,node.w,node.h);\r\n"+
            "		  this.context2.stroke();\r\n"+
            "	      this.context2.restore();\r\n"+
            "	}\r\n"+
            "	TreeMap.prototype.findNodeAt=function(x,y,data){\r\n"+
            "		for(var i in data){\r\n"+
            "			var parent=data[i];\r\n"+
            "			if(parent.x<=x && parent.x+parent.w>x && parent.y<=y && parent.y+parent.h>y){\r\n"+
            "			  if(parent.children)\r\n"+
            "				  return this.findNodeAt(x,y,parent.children);\r\n"+
            "			  else\r\n"+
            "				  return parent;\r\n"+
            "			}\r\n"+
            "				\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	TreeMap.prototype.fillChildRect=function(x,y,w,h,node){\r\n"+
            "	  var pixels=w*h;\r\n"+
            "	  if(w<1/this.zoom && h<1/this.zoom)\r\n"+
            "		  return;\r\n"+
            "	  this.context.fillStyle=node.heat;\r\n"+
            "	  this.context.fillRect(x,y,w,h);\r\n"+
            "	 \r\n"+
            "	  var c=node.textColor;\r\n"+
            "	  if(c==null)\r\n"+
            "	    c=this.textColors[node.depth];\r\n"+
            "	  if(w>10/this.zoom && h>10/this.zoom)\r\n"+
            "	    this.writeText2(x,y,w,h,this.textSizes[node.depth],c,node,2);\r\n"+
            "	  if(node.selected){\r\n"+
            "		this.drawBorder(x,y,w,h, this.selectBorderColor1,this.selectBorderColor2);\r\n"+
            "	  } else if (node.borderColor) {\r\n"+
            "		this.drawBorder(x,y,w,h, node.borderColor);\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "\r\n"+
            "	TreeMap.prototype.getFont=function(fontSize){\r\n"+
            "		return \"normal \"+fontSize+'px ' + this.fontFamily;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	\r\n"+
            "	TreeMap.prototype.writeText2=function(x,y,w,h,fontSize,fontColor,node,margin){\r\n"+
            "	  if(!node.names)\r\n"+
            "		  return;\r\n"+
            "	  margin=margin/this.zoom;\r\n"+
            "	  w-=margin+margin; \r\n"+
            "	  h-=margin+margin; \r\n"+
            "	  var hz=h*this.zoom;\r\n"+
            "	  var wz=w*this.zoom;\r\n"+
            "	  if(hz<6 || wz<6)\r\n"+
            "		  return; \r\n"+
            "	  \r\n"+
            "	  x+=margin;y+=margin;\r\n"+
            "	  var maxFs=fontSize;\r\n"+
            "	  \r\n"+
            "	  fontSize=cl(fontSize*this.zoom);\r\n"+
            "	  var names=node.names;\r\n"+
            "	  \r\n"+
            "	  this.context.font=this.getFont(fontSize);\r\n"+
            "	  var textHeight=fontSize;\r\n"+
            "	  if(textHeight>hz/names.length){\r\n"+
            "		  textHeight=fl(hz/names.length);\r\n"+
            "		  fontSize=textHeight;\r\n"+
            "	      this.context.font=this.getFont(fontSize);\r\n"+
            "	  }\r\n"+
            "	  var textWidth=this.context.measureText(node.maxName).width;\r\n"+
            "	  if(textWidth>wz){\r\n"+
            "		  fontSize=fontSize*(wz/textWidth);\r\n"+
            "	      this.context.font=this.getFont(fontSize);\r\n"+
            "	      textHeight=fontSize;\r\n"+
            "	  }\r\n"+
            "	  if(fontSize<8){\r\n"+
            "	    if(fontSize<6)\r\n"+
            "		  return;\r\n"+
            "		fontSize=8;\r\n"+
            "	    this.context.font=this.getFont(fontSize);\r\n"+
            "	  }\r\n"+
            "	  this.context.fillStyle=fontColor;\r\n"+
            "	  var centerX,centerY;\r\n"+
            "	  var hAlignScale;\r\n"+
            "	  var vAlignScale;\r\n"+
            "	  var padding;\r\n"+
            "	  if(node.depth==0){\r\n"+
            "	    hAlignScale=.5;\r\n"+
            "	    vAlignScale=.5;\r\n"+
            "	    padding=0;\r\n"+
            "	  }else{\r\n"+
            "	    hAlignScale=this.hAlignScale;\r\n"+
            "	    vAlignScale=this.vAlignScale;\r\n"+
            "	    padding=4;\r\n"+
            "	  }\r\n"+
            "	  padding/=this.zoom;\r\n"+
            "	  \r\n"+
            "      if(hAlignScale==0) this.context.textAlign='left';\r\n"+
            "	  else if(hAlignScale==1) this.context.textAlign='right';\r\n"+
            "	  else this.context.textAlign='center';\r\n"+
            "	  \r\n"+
            "	  if(vAlignScale==0) this.context.textBaseline='top';\r\n"+
            "	  else if(vAlignScale==1) this.context.textBaseline='bottom';\r\n"+
            "	  else this.context.textBaseline='middle';\r\n"+
            "	  \r\n"+
            "	  centerX=padding+x+((w-padding*2)*hAlignScale);\r\n"+
            "	  centerY=padding+y+((h-padding*2)*vAlignScale);\r\n"+
            "	  this.context.save();\r\n"+
            "	  this.context.beginPath();\r\n"+
            "	  this.context.rect(x,y,w,h);\r\n"+
            "	  this.context.translate(centerX,centerY);\r\n"+
            "	  this.context.clip();\r\n"+
            "	  this.context.scale(1/this.zoom,1/this.zoom);\r\n"+
            "	  var t=(names.length-1)/2;\r\n"+
            "	  for(var i=0;i<names.length;i++)\r\n"+
            "	    this.context.fillText(names[i],0,(i-t)*textHeight);\r\n"+
            "	  this.context.restore();\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	\r\n"+
            "\r\n"+
            "	TreeMap.prototype.repaint=function(){\r\n"+
            "	    this.context.save();\r\n"+
            "	    if(this.textHAlign=='left') this.hAlignScale=0;\r\n"+
            "	    else if(this.textHAlign=='right') this.hAlignScale=1;\r\n"+
            "	    else this.hAlignScale=.5;\r\n"+
            "	    \r\n"+
            "	    if(this.textVAlign=='top') this.vAlignScale=0;\r\n"+
            "	    else if(this.textVAlign=='bottom') this.vAlignScale=1;\r\n"+
            "	    else this.vAlignScale=.5;\r\n"+
            "	    \r\n"+
            "	    \r\n"+
            "	    \r\n"+
            "		this.context.translate(-this.offsetX,-this.offsetY);\r\n"+
            "		this.context.scale(this.zoom,this.zoom);\r\n"+
            "		this.maxHeat=null;\r\n"+
            "		var t=this.rootNode;\r\n"+
            "		if(t==null || t.children==null || (Object.keys(t.children).length == 0) ){\r\n"+
            "//            this.context.fillStyle=\"white\";\r\n"+
            "            this.context.fillStyle=this.backgroundColor;\r\n"+
            "            this.context.fillRect(0,0,this.canvas.width,this.canvas.height);\r\n"+
            "			return;\r\n"+
            "		}\r\n"+
            "		this.calcSizes(t,-1);\r\n"+
            "		this.childrenSorted=t.childrenSorted;\r\n"+
            "		var time1=Date.now();\r\n"+
            "		if(this.needsLayout)\r\n"+
            "	      this.layoutChildren(t,0,0,this.canvas.width,this.canvas.height,0,true,true);\r\n"+
            "		this.needsLayout=false;\r\n"+
            "		var time2=Date.now(");
          out.print(
            ");\r\n"+
            "	    \r\n"+
            "	    this.minX=(0+this.offsetX)/this.zoom;\r\n"+
            "	    this.maxX=(this.canvas.width+this.offsetX)/this.zoom;\r\n"+
            "	    \r\n"+
            "	    this.minY=(0+this.offsetY)/this.zoom;\r\n"+
            "	    this.maxY=(this.canvas.height+this.offsetY)/this.zoom;\r\n"+
            "	    \r\n"+
            "		var time3=Date.now();\r\n"+
            "	    this.paintChildren(t,true);\r\n"+
            "		var time4=Date.now();\r\n"+
            "	    var node = this.updateActiveNode();\r\n"+
            "	    if(node!=null){\r\n"+
            "	    	this.updateWhiteHighlight(node);\r\n"+
            "	    }\r\n"+
            "	    this.context.restore();\r\n"+
            "	}\r\n"+
            "	TreeMap.prototype.outsideClip=function(node){\r\n"+
            "		return (node.x+node.w<this.minX || node.y+node.h<this.minY || node.x>this.maxX || node.y>this.maxY);\r\n"+
            "	}\r\n"+
            "	TreeMap.prototype.paintChildren=function(node,isRoot){\r\n"+
            "		\r\n"+
            "		if(!node.children){\r\n"+
            "          this.fillChildRect(node.x,node.y,node.w,node.h,node);\r\n"+
            "          return;\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		var boxSize=node.w*node.h*this.zoom*this.zoom;\r\n"+
            "		var totSize=this.canvas.width*this.canvas.height;\r\n"+
            "		\r\n"+
            "		if(boxSize<totSize/500){\r\n"+
            "		      this.fillChildRect(node.x,node.y,node.w,node.h,node);\r\n"+
            "		}else{\r\n"+
            "		  if(!isRoot){\r\n"+
            "	        this.context.fillStyle=this.borderColors[node.depth+1];\r\n"+
            "	        this.context.fillRect(node.x,node.innerY,node.w,node.innerH);\r\n"+
            "		  }else{\r\n"+
            "	        this.context.fillStyle=this.borderColors[node.depth+1];\r\n"+
            "	        this.context.fillRect(node.x,node.y,node.w,node.h);\r\n"+
            "		  }\r\n"+
            "		  var childrenSorted=node.childrenSorted;\r\n"+
            "		  for(var i=0;i<childrenSorted.length;i++){\r\n"+
            "		    var child=childrenSorted[i];\r\n"+
            "		    if(this.outsideClip(child))\r\n"+
            "			  continue;\r\n"+
            "		    var x=child.x,y=child.y,w=child.w,h=child.h;\r\n"+
            "		    if(w && h){\r\n"+
            "		        this.paintChildren(child,false);\r\n"+
            "		    }\r\n"+
            "		  }\r\n"+
            "		}\r\n"+
            "		if(!isRoot){\r\n"+
            "	      this.context.fillStyle=node.heat;\r\n"+
            "	      if(boxSize<totSize/100){\r\n"+
            "	    	  this.context.globalAlpha=1-boxSize/(totSize/100);\r\n"+
            "	          this.context.fillRect(node.innerX,node.innerY,node.innerW,node.innerH);\r\n"+
            "	    	  this.context.globalAlpha=1;\r\n"+
            "	      }\r\n"+
            "	      this.context.fillRect(node.x,node.y,node.w,node.innerY-node.y);\r\n"+
            "	      var c=node.textColor;\r\n"+
            "          this.writeText2(node.x,node.y,node.w,node.h-node.innerH,this.textSizes[node.depth],c!=null ?  c: this.textColors[node.depth],node,2);\r\n"+
            "	      this.context.strokeStyle=this.borderColors[node.depth];\r\n"+
            "	      this.context.fillStyle=node.heat;\r\n"+
            "	      this.context.beginPath();\r\n"+
            "	      var t=Math.min(5,this.zoom*node.w/10)/this.zoom;\r\n"+
            "	      this.context.moveTo(node.x+t,node.innerY);\r\n"+
            "	      this.context.lineTo(node.x+t+t,node.innerY+t);\r\n"+
            "	      this.context.lineTo(node.x+t*3,node.innerY);\r\n"+
            "	      this.context.fill();\r\n"+
            "		  this.context.lineWidth=1/this.zoom;\r\n"+
            "	      this.context.beginPath();\r\n"+
            "	      this.context.moveTo(node.x,node.innerY+.5/this.zoom);\r\n"+
            "	      this.context.lineTo(node.x+t,node.innerY+.5/this.zoom);\r\n"+
            "	      this.context.lineTo(node.x+t+t,node.innerY+t+.5/this.zoom);\r\n"+
            "	      this.context.lineTo(node.x+t*3,node.innerY+.5/this.zoom);\r\n"+
            "	      this.context.lineTo(node.x+node.w,node.innerY+.5/this.zoom);\r\n"+
            "	      this.context.stroke();\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "\r\n"+
            "\r\n"+
            "	TreeMap.prototype.calcSizes=function(data,depth){\r\n"+
            "		data.depth=depth;\r\n"+
            "		var children=data.children;\r\n"+
            "		if(children!=null){\r\n"+
            "		    depth++;\r\n"+
            "		      data.childrenSorted=[];\r\n"+
            "		      var totSize=0;\r\n"+
            "		      for(var i in children){\r\n"+
            "		    	var c=children[i];\r\n"+
            "			    if(c.children)\r\n"+
            "			      this.calcSizes(c,depth);\r\n"+
            "			    else\r\n"+
            "			    	c.depth=depth;\r\n"+
            "		    	totSize+=c.size;\r\n"+
            "		        data.childrenSorted[data.childrenSorted.length]=c;\r\n"+
            "		      }\r\n"+
            "		      data.childrenSize=totSize;\r\n"+
            "		      data.childrenSorted.sort(function(a,b){return b.size-a.size;});\r\n"+
            "		    }\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	TreeMap.prototype.layoutChildren=function(data,x,y,w,h,isLeftMost,isTopMost){\r\n"+
            "	  var pbw=data.depth==-1 ? 0 : this.borderSizes[data.depth];\r\n"+
            "	  var pbh=data.depth==-1 ? 0 : this.borderSizes[data.depth];\r\n"+
            "	  if(pbw>w/20)\r\n"+
            "		  pbw=w/20;\r\n"+
            "	  if(pbh>h/20)\r\n"+
            "		  pbh=h/20;\r\n"+
            "	  \r\n"+
            "	  if(!isLeftMost){\r\n"+
            "	    data.x=x;\r\n"+
            "	    data.w=w-pbw;\r\n"+
            "	  }else{\r\n"+
            "	    data.x=x+pbw;\r\n"+
            "	    data.w=w-pbw-pbw;\r\n"+
            "	  }\r\n"+
            "	  \r\n"+
            "	  if(!isTopMost){\r\n"+
            "	    data.y=y;\r\n"+
            "	    data.h=h-pbh;\r\n"+
            "	  }else{\r\n"+
            "	    data.y=y+pbh;\r\n"+
            "	    data.h=h-pbh-pbh;\r\n"+
            "	  }\r\n"+
            "	  \r\n"+
            "	  \r\n"+
            "	  \r\n"+
            "	  data.innerX=data.x;\r\n"+
            "	  data.innerY=data.y;\r\n"+
            "	  data.innerW=data.w;\r\n"+
            "	  data.innerH=data.h;\r\n"+
            "	  \r\n"+
            "	  if(data.children && data.depth>=0){\r\n"+
            "		var titleSize=this.textSizes[data.depth]+1;\r\n"+
            "		if(titleSize*6>h)\r\n"+
            "		  titleSize=data.h/6;\r\n"+
            "	    data.innerY+=titleSize;\r\n"+
            "	    data.innerH-=titleSize;\r\n"+
            "	  }\r\n"+
            "	  \r\n"+
            "	  \r\n"+
            "	  if(data.children)\r\n"+
            "	    this.layout(data);\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	\r\n"+
            "	TreeMap.prototype.diffNums=function(a){\r\n"+
            "		return(a>1) ? diff(1/a,this.oneOverRatio): diff(a,this.ratio);\r\n"+
            "	}\r\n"+
            "	TreeMap.prototype.setRatio=function(ratio){\r\n"+
            "		this.ratio=ratio;\r\n"+
            "		this.oneOverRatio=1/ratio;\r\n"+
            "	}\r\n"+
            "	TreeMap.prototype.getRatio=function(ratio){\r\n"+
            "		return this.ratio;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	TreeMap.prototype.layout=function(dataset){\r\n"+
            "		var x=dataset.innerX;\r\n"+
            "		var y=dataset.innerY;\r\n"+
            "		var w=dataset.innerW;\r\n"+
            "		var h=dataset.innerH;\r\n"+
            "		var total=dataset.childrenSize;\r\n"+
            "		var dataOffset=0;\r\n"+
            "		var data=dataset.childrenSorted;\r\n"+
            "		var sticky=data.sticky;\r\n"+
            "		if(sticky==null)\r\n"+
            "		  sticky=data.sticky=[];\r\n"+
            "		var stickyDepth=0;\r\n"+
            "		while(dataOffset<data.length){\r\n"+
            "		  var pixelRatio=w*h/total;\r\n"+
            "		  var lastLayoutAspect=1000000;\r\n"+
            "		  var lastLayoutOffset=-1;\r\n"+
            "		  if(stickyDepth<data.sticky.length){\r\n"+
            "			lastLayoutAspect=sticky[stickyDepth].aspect;\r\n"+
            "			lastLayoutOffset=sticky[stickyDepth].offset;\r\n"+
            "		  }\r\n"+
            "		\r\n"+
            "		  var totalSize=0;\r\n"+
            "		  var endOffset=dataOffset;\r\n"+
            "		  var isWide=w>=h;\r\n"+
            "		  var t=(isWide ? h*h : w*w)/pixelRatio;\r\n"+
            "		  var lastAspect=null;\r\n"+
            "		  while(endOffset<data.length){\r\n"+
            "		    var val=data[endOffset].size;\r\n"+
            "		    var tot=totalSize+val;\r\n"+
            "		    var aspect=val*t/tot/tot;\r\n"+
            "		    if(isWide)\r\n"+
            "		      aspect=1/aspect;\r\n"+
            "		    if(lastAspect!=null && this.diffNums(aspect)>=this.diffNums(lastAspect))\r\n"+
            "		      break;\r\n"+
            "		    lastAspect=aspect;\r\n"+
            "		    totalSize=tot;\r\n"+
            "		    endOffset++;\r\n"+
            "		  }\r\n"+
            "		  if(lastLayoutOffset!=endOffset && lastLayoutOffset!=-1){\r\n"+
            "		    change=lastLayoutAspect/aspect;\r\n"+
            "		    if(change<this.aspectStickyness && this.ratio==1){\r\n"+
            "			  aspect=lastLayoutAspect;\r\n"+
            "			  endOffset=lastLayoutOffset;\r\n"+
            "			  totalSize=0;\r\n"+
            "			  for(var i=dataOffset;i<endOffset;i++)\r\n"+
            "		        totalSize+=data[i].size;\r\n"+
            "		    }else{\r\n"+
            "			  data.sticky=[];\r\n"+
            "			  sticky=null;\r\n"+
            "		   }\r\n"+
            "		  }\r\n"+
            "		  if(sticky!=null)\r\n"+
            "		    sticky[stickyDepth]={aspect:aspect,offset:endOffset};\r\n"+
            "		  if(isWide){\r\n"+
            "		    var split=endOffset==data.length ? w :  fl(pixelRatio*totalSize/h);\r\n"+
            "		    var yy=y;\r\n"+
            "		    var runSize=0;\r\n"+
            "		    for(var i=dataOffset;i<endOffset;i++){\r\n"+
            "			  var d=data[i];\r\n"+
            "			  runSize+=d.size;\r\n"+
            "			  var yy2=i+1==endOffset ? y+h : fl(y+runSize*h/totalSize);\r\n"+
            "			  this.layoutChildren(d,x,yy,split,yy2-yy,x==dataset.innerX,yy==dataset.innerY);\r\n"+
            "			  yy=yy2;\r\n"+
            "		    }\r\n"+
            "		    x+=split;\r\n"+
            "		    w-=split;\r\n"+
            "		  }else{\r\n"+
            "		    var split=endOffset==data.length ? h : fl(pixelRatio*totalSize/w);\r\n"+
            "		    var xx=x;\r\n"+
            "		    var runSize=0;\r\n"+
            "		    for(var i=dataOffset;i<endOffset;i++){\r\n"+
            "			  var d=data[i];\r\n"+
            "			  runSize+=d.size;\r\n"+
            "			  var xx2=i+1==endOffset ? x+w : fl(x+runSize*w/totalSize);\r\n"+
            "			  this.layoutChildren(d,xx,y,xx2-xx,split,xx==dataset.innerX,y==dataset.innerY);\r\n"+
            "			  xx=xx2;\r\n"+
            "		    }\r\n"+
            "		    y+=split;\r\n"+
            "		    h-=split;\r\n"+
            "		  }\r\n"+
            "		  stickyDepth++;\r\n"+
            "		  dataOffset=endOffset;\r\n"+
            "		  total-=totalSize;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "\r\n"+
            "TreeMap.prototype.showContextMenu=function(menu){\r\n"+
            "  var t=new Rect().readFromElement(this.canvas2);\r\n"+
            "  this.createMenu(menu).show(new Point(this.currentMousePoint).move(t.left-3,t.top-3));\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "TreeMap.prototype.createMenu=function(menu){\r\n"+
            "   this.showingMenu=true;\r\n"+
            "   var that=this;\r\n"+
            "   var activeNode=this.activeNode;\r\n"+
            "   var r=new Menu(getWindow(this.element));\r\n"+
            "   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e, id, activeNode ? activeNode.id: -1);} );\r\n"+
            "   r.onHide=function(){that.showingMenu=false;};\r\n"+
            "   return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TreeMap.prototype.handleKeydown=function(e){\r\n"+
            "	if(e.key==\" \"){\r\n"+
            "		    this.setZoom(e,-1000);\r\n"+
            "	}else if(e.ctrlKey){\r\n"+
            "	  var distance=e.shiftKey ? 3 : 1;\r\n"+
            "	  if(e.key==\"ArrowUp\" || e.key==\"ArrowRight\")\r\n"+
            "		    this.setZoom(e,distance);\r\n"+
            "	  if(e.key==\"ArrowDown\"|| e.key==\"ArrowLeft\")\r\n"+
            "		    this.setZoom(e,-distance);\r\n"+
            "	}else{\r\n"+
            "	  var distance=(e.shiftKey ? 100 : 10) ;\r\n"+
            "	  if(e.key==\"ArrowLeft\")\r\n"+
            "	      this.setOffset(this.offsetX-distance,this.offsetY);\r\n"+
            "	  else if(e.key==\"ArrowRight\")\r\n"+
            "	      this.setOffset(this.offsetX+distance,this.offsetY);\r\n"+
            "	  else if(e.key==\"ArrowUp\")\r\n"+
            "	      this.setOffset(this.offsetX,this.offsetY-distance);\r\n"+
            "	  else if(e.key==\"ArrowDown\")\r\n"+
            "	      this.setOffset(this.offsetX,this.offsetY+distance);\r\n"+
            "	}\r\n"+
            "//	if(e.key==\"Control\")\r\n"+
            "//		this.canvas.style.cursor=\"move\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "TreeMap.prototype.handleNodeStyleChange=function(changes){\r\n"+
            "	for (var i=0; i < changes.length; i++) {\r\n"+
            "		var data = changes[i];\r\n"+
            "		var node = this.nodes[data.nid];\r\n"+
            "		if (node != null) {\r\n"+
            "			node.borderColor=data.borderColor; // use during repaint\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "TreeMap.prototype.drawBorder=function(x,y,w,h,borderColor,borderColor2){\r\n"+
            "	var ss=this.context.strokeStyle;\r\n"+
            "	var n=1/this.zoom;\r\n"+
            "	this.context.lineWidth=n;\r\n"+
            "	var x2=(x)+.5*n;\r\n"+
            "	var y2=(y)+.5*n;\r\n"+
            "	this.context.strokeStyle=borderColor;\r\n"+
            "	this.context.beginPath();\r\n"+
            "	this.context.rect(x2,y2,w-1*n,h-1*n);\r\n"+
            "	this.context.rect(x2+2*n,y2+2*n,w-5*n,h-5*n);\r\n"+
            "	this.context.stroke();\r\n"+
            "	this.context.strokeStyle=borderColor2? borderColor2 : borderColor;\r\n"+
            "	this.context.beginPath();\r\n"+
            "	this.context.rect(x2+1*n,y2+1*n,w-3*n,h-3*n);\r\n"+
            "	this.context.rect(x2+3*n,y2+3*n,w-7*n,h-7*n);\r\n"+
            "	this.context.stroke();\r\n"+
            "	this.context.strokeStyle=ss;\r\n"+
            "}");

	}
	
}