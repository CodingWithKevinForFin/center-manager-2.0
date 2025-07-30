package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_graph_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_graph_js_1() {
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
            "function GraphPanel(containerElement){\r\n"+
            "	var that=this;\r\n"+
            "	this.containerElement=containerElement;\r\n"+
            "	this.graphcontainerElement=nw('div');\r\n"+
            "	this.scrollPane=new ScrollPane(this.containerElement, 15, this.graphcontainerElement);	\r\n"+
            "	this.containerElement.className+=' graph_panel';\r\n"+
            "	this.canvas=nw('canvas','graph_panel');\r\n"+
            "    this.context = this.canvas.getContext('2d');\r\n"+
            "	this.graphcontainerElement.appendChild(this.canvas);\r\n"+
            "	this.nodesById={};\r\n"+
            "	this.edgesById={};\r\n"+
            "	this.selected={};\r\n"+
            "	this.maxNodeX=0;\r\n"+
            "	this.maxNodeY=0;\r\n"+
            "	this.bend=8;\r\n"+
            "	this.arrowSize=this.bend/2.5;\r\n"+
            "	makeDraggable(this.canvas);\r\n"+
            "    this.canvas.ondraggingStart=function(e,event){that.onBackgroundDraggingStart(e,event);};\r\n"+
            "    this.canvas.ondragging=function(e,diffx,diffy){that.onBackgroundDragging(e,diffx,diffy);};\r\n"+
            "    this.canvas.ondraggingEnd=function(e,diffx,diffy,event){that.onBackgroundDraggingEnd(e,diffx,diffy,event);};\r\n"+
            "    this.selectionBox=nw('div','graph_panel_selectbox');\r\n"+
            "    this.containerElement.tabIndex=1;\r\n"+
            "    this.containerElement.onkeydown=function(e){that.handleKeyDown(e)};\r\n"+
            "    this.scrollPane.onScroll=function(){that.onScroll()};\r\n"+
            "    this.scrollPane.DOM.paneElement.style.overflow=\"visible\";\r\n"+
            "    this.pixel=this.context.createImageData(1,1);\r\n"+
            "    this.pixel.data[0]=128;\r\n"+
            "    this.pixel.data[1]=128;\r\n"+
            "    this.pixel.data[2]=128;\r\n"+
            "    this.pixel.data[3]=128;\r\n"+
            "    this.gridSize=1;\r\n"+
            "    this.snapSize=1;\r\n"+
            "    \r\n"+
            "	//this.addEdge(1,1,2);\r\n"+
            "	//this.addEdge(2,2,3);\r\n"+
            "	//this.addEdge(3,3,1);\r\n"+
            "	//this.addEdge(4,4,1);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.onScroll=function(){\r\n"+
            "	this.scrollPane.DOM.innerpaneElement.style.top=toPx(-this.scrollPane.getClipTop());\r\n"+
            "	this.scrollPane.DOM.innerpaneElement.style.left=toPx(-this.scrollPane.getClipLeft());\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.ensureVisibleNode=function(id){\r\n"+
            "	if(this.nodesById.hasOwnProperty(id)){\r\n"+
            "		var node = this.nodesById[id];\r\n"+
            "		var rectNode = node.getBoundingClientRect();\r\n"+
            "		this.scrollPane.ensureVisible(rectNode);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.ensureVisibleNodes=function(nodeIds){\r\n"+
            "	var nodeIdsArray = nodeIds.split(\"|\");\r\n"+
            "	var len = nodeIdsArray.length;\r\n"+
            "	for(var i = (len - 1); i >= 0; i--){\r\n"+
            "		var id = parseInt(nodeIdsArray[i]);\r\n"+
            "		this.ensureVisibleNode(id);\r\n"+
            "	}	\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.handleKeyDown=function(e){\r\n"+
            "	if (e.keyCode==37 || e.keyCode==38 || e.keyCode==39 || e.keyCode==40)\r\n"+
            "		this.onUserDirectionKey(e);\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.addNodes=function(json){\r\n"+
            "	for(var i in json){\r\n"+
            "	  var n=json[i];\r\n"+
            "	  var sel=n.sel==true;\r\n"+
            "	  if(sel)\r\n"+
            "	     this.selected[n.id]=true;\r\n"+
            "	  else\r\n"+
            "		  delete this.selected[n.id];\r\n"+
            "	  this.addNode(n.id,n.x,n.y,n.w,n.h,n.n,n.s,sel,n.o);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.selectNodes=function(json){\r\n"+
            "	for(var i in json){\r\n"+
            "		var n=json[i];\r\n"+
            "		var node = this.nodesById[n.id]; \r\n"+
            "		\r\n"+
            "		if(node == null)\r\n"+
            "			continue;\r\n"+
            "		\r\n"+
            "		var sel=n.sel==true;\r\n"+
            "		if(sel == node.sel)\r\n"+
            "			continue;\r\n"+
            "		node.sel = sel;\r\n"+
            "		if(sel == true){\r\n"+
            "			this.selected[n.id]=true;\r\n"+
            "			node.classList.add(\"graph_node_selected\");\r\n"+
            "		}else{\r\n"+
            "			delete this.selected[n.id];\r\n"+
            "			node.classList.remove(\"graph_node_selected\");\r\n"+
            "			\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.removeNodes=function(json){\r\n"+
            "	for(var i in json){\r\n"+
            "	  this.removeNode(json[i]);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.addEdges=function(json){\r\n"+
            "	for(var i in json){\r\n"+
            "	  var n=json[i];\r\n"+
            "	  this.addEdge(n.id,n.n1,n.n2,n.idx,n.d,n.c);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.removeEdges=function(json){\r\n"+
            "	for(var i in json){\r\n"+
            "	  this.removeEdge(json[i]);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.setGridSnap=function(grid,snap){\r\n"+
            "    this.gridSize=grid;\r\n"+
            "    this.snapSize=snap;\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.clearData=function(){\r\n"+
            "	for(var node in this.nodesById)\r\n"+
            "	  this.graphcontainerElement.removeChild(this.nodesById[node]);\r\n"+
            "	this.nodesById={};\r\n"+
            "	this.edgesById={};\r\n"+
            "	this.selected={};\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.clearEdges=function(){\r\n"+
            "	this.edgesById={};\r\n"+
            "}\r\n"+
            "// Duplicate GraphPanel.prototype.repaint=function(){\r\n"+
            "GraphPanel.prototype.setSize=function(w,h){\r\n"+
            "	this.width=w;\r\n"+
            "	this.height=h;\r\n"+
            "//	this.containerElement.style.width=toPx(w);\r\n"+
            "//	this.containerElement.style.height=toPx(h);\r\n"+
            "	this.scrollPane.setLocation(0,0,this.width,this.height);\r\n"+
            "	this.repaint();\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.getCanvasWidth=function(){\r\n"+
            "	var w=this.width-20;\r\n"+
            "	for(var i in this.nodesById)\r\n"+
            "		w=Math.max(w,this.nodesById[i].x+this.nodesById[i].w/2);\r\n"+
            "	return w;\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.getCanvasHeight=function(){\r\n"+
            "	var h=this.height-20;\r\n"+
            "	for(var i in this.nodesById)\r\n"+
            "		h=Math.max(h,this.nodesById[i].y+this.nodesById[i].h/2);\r\n"+
            "	return h;\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.repaint=function(){\r\n"+
            "	var w=this.getCanvasWidth();\r\n"+
            "	var h=this.getCanvasHeight();\r\n"+
            "	this.scrollPane.setPaneSize(w, h);\r\n"+
            "	h=Math.max(this.height,h);\r\n"+
            "	w=Math.max(this.width,w);\r\n"+
            "//	this.graphcontainerElement.style.width=toPx(w);\r\n"+
            "//	this.graphcontainerElement.style.height=toPx(h);\r\n"+
            "	this.canvas.width=w;\r\n"+
            "	this.canvas.height=h;\r\n"+
            "    this.context.clearRect(0,0,this.canvas.width,this.canvas.height);\r\n"+
            "    if(this.gridSize>1){\r\n"+
            "      this.context.strokeStyle = '#EEEEEE';\r\n"+
            "      this.context.lineWidth = 1;\r\n"+
            "	  this.context.beginPath();\r\n"+
            "      for(var x=.5;x<w;x+=this.gridSize){\r\n"+
            "	    this.context.moveTo(x,0);\r\n"+
            "	    this.context.lineTo(x,h);\r\n"+
            "      }\r\n"+
            "      for(var y=.5;y<h;y+=this.gridSize){\r\n"+
            "	    this.context.moveTo(0,y);\r\n"+
            "	    this.context.lineTo(w,y);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "	this.context.stroke();\r\n"+
            "    for(var i in this.edgesById)\r\n"+
            "    	this.drawEdge(i);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "GraphPanel.prototype.addNode=function(id,x,y,w,h,innerHTML,cssStyle,sel,options){\r\n"+
            "	var div=this.nodesById[id];\r\n"+
            "	var that=this;\r\n"+
            "	if(div==null){\r\n"+
            "	  div=nw('div','graph_node');\r\n"+
            "	  this.nodesById[id]=div;\r\n"+
            "	  this.graphcontainerElement.appendChild(div);\r\n"+
            "	}\r\n"+
            "	var movable=options & 2;\r\n"+
            "	var selectable=options & 1;\r\n"+
            "	if(movable){\r\n"+
            "	  if(div.ondragging==null){\r\n"+
            "	    makeDraggable(div,div);\r\n"+
            "        div.ondraggingStart=function(e,event){return that.onDraggingStart(e,event);};\r\n"+
            "        div.ondragging=function(e,diffx,diffy){that.onDragging(e,diffx,diffy);};\r\n"+
            "        div.ondraggingEnd=function(e,diffx,diffy,event){that.onDraggingEnd(e,diffx,diffy,event);};\r\n"+
            "	  }\r\n"+
            "	}else{\r\n"+
            "	  if(div.ondragging!=null){\r\n"+
            "	    removeDraggable(div,div);/* TODO: implement method */\r\n"+
            "        div.ondraggingStart=null;\r\n"+
            "        div.ondragging=null;\r\n"+
            "        div.ondraggingEnd=null;\r\n"+
            "	  }\r\n"+
            "	  if(selectable){\r\n"+
            "        div.onmousedown=function(e){that.onUserClick(div.id,e.shiftKey,e.ctrlKey,getMouseButton(e));};\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "	div.ondblclick=function(e){that.onUserDblClick(div.id)};\r\n"+
            "\r\n"+
            "	var style=div.style;\r\n"+
            "	style.left=toPx(x-fl(w/2));\r\n"+
            "	style.top=toPx(y-fl(h/2));\r\n"+
            "	style.width=toPx(w);\r\n"+
            "	style.height=toPx(h);\r\n"+
            "	div.x=x;\r\n"+
            "	div.cssStyle=cssStyle;\r\n"+
            "	div.y=y;\r\n"+
            "	div.w=w;\r\n"+
            "	div.h=h;\r\n"+
            "	div.sel=sel;\r\n"+
            "	div.id=id;\r\n"+
            "	div.innerHTML=innerHTML;\r\n"+
            "	if(div.sel)\r\n"+
            "		div.className='graph_node graph_node_selected';\r\n"+
            "	else\r\n"+
            "		div.className='graph_node';\r\n"+
            "	applyStyle(div,div.cssStyle);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.onBackgroundDraggingStart=function(div,e){\r\n"+
            "	this.graphcontainerElement.appendChild(this.selectionBox);\r\n"+
            "	var p=getMouseLayerPoint(e);\r\n"+
            "	this.selectionBox.startX=p.x;\r\n"+
            "	this.selectionBox.startY=p.y;\r\n"+
            "	this.selectionBox.style.left=toPx(p.x);\r\n"+
            "	this.selectionBox.style.top=toPx(p.y);\r\n"+
            "	this.selectionBox.style.width=toPx(1);\r\n"+
            "	this.selectionBox.style.height=toPx(1);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.onBackgroundDragging=function(div,diffx,diffy){\r\n"+
            "	if(diffx<0){\r\n"+
            "	  this.selectionBox.style.left=toPx(this.selectionBox.startX+diffx);\r\n"+
            "	  this.selectionBox.style.width=toPx(-diffx);\r\n"+
            "	}else\r\n"+
            "	  this.selectionBox.style.width=toPx(diffx);\r\n"+
            "	if(diffy<0){\r\n"+
            "	  this.selectionBox.style.top=toPx(this.selectionBox.startY+diffy);\r\n"+
            "	  this.selectionBox.style.height=toPx(-diffy);\r\n"+
            "	}else\r\n"+
            "	  this.selectionBox.style.height=toPx(diffy);\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.onBackgroundDraggingEnd=function(div,diffx,diffy,e){\r\n"+
            "	this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "	this.graphcontainerElement.removeChild(this.selectionBox);\r\n"+
            "    var button=getMouseButton(e);\r\n"+
            "    if(this.onUserSelect)\r\n"+
            "      this.onUserSelect(this.selectionBox.startX,this.selectionBox.startY,diffx,diffy,e.shiftKey,e.ctrlKey,button);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.onDraggingStart=function(div,e){\r\n"+
            "    var button=getMouseButton(e);\r\n"+
            "    var isSelected=this.selected[div.id]==true;\r\n"+
            "    \r\n"+
            "    if(e.ctrlKey || e.shiftKey){\r\n"+
            "      if(this.onUserClick){\r\n"+
            "	    this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "        this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);\r\n"+
            "      }\r\n"+
            "      return false;\r\n"+
            "    }\r\n"+
            "    if(!isSelected){\r\n"+
            "    	if(button==2){\r\n"+
            "           if(this.onUserClick){\r\n"+
            "	         this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "             this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);\r\n"+
            "           }\r\n"+
            "    	   return false;\r\n"+
            "    	}\r\n"+
            "    	else{\r\n"+
            "    	  this.selected={};\r\n"+
            "    	  this.selected[div.id]=true;\r\n"+
            "          if(this.onUserClick){\r\n"+
            "	         this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "             this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);\r\n"+
            "          }\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "    if(button==2){\r\n"+
            "           if(this.onUserClick){\r\n"+
            "	         this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "             this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);\r\n"+
            "           }\r\n"+
            "    	   return false;\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "    \r\n"+
            "	div.xstart=div.x;\r\n"+
            "	div.ystart=div.y;\r\n"+
            "	for(var i in this.selected){\r\n"+
            "	  var node=this.nodesById[i];\r\n"+
            "	  node.xstart=node.x;\r\n"+
            "	  node.ystart=node.y;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function snap(val,snapSize){\r\n"+
            "	return rd(val/snapSize) * snapSize;\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.onDragging=function(div,diffx,diffy){\r\n"+
            "	if(diffx==0 && diffy==0)\r\n"+
            "		return;\r\n"+
            "//	div.x=snap(diffx+div.xstart,25);\r\n"+
            "//	div.y=snap(diffy+div.ystart,25);\r\n"+
            "	for(var i in this.selected){\r\n"+
            "	  var node=this.nodesById[i];\r\n"+
            "	  var w=fl(node.w/2);\r\n"+
            "	  var h=fl(node.h/2);\r\n"+
            "	  var x=snap(diffx+node.xstart-w,this.snapSize);\r\n"+
            "	  var y=snap(diffy+node.ystart-h,this.snapSize);\r\n"+
            "	  node.x=x+w;\r\n"+
            "	  node.y=y+h;\r\n"+
            "	  node.style.left=toPx(x);\r\n"+
            "	  node.style.top=toPx(y);\r\n"+
            "	}\r\n"+
            "	this.repaint();\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.onDraggingEnd=function(div,diffx,");
          out.print(
            "diffy,e){\r\n"+
            "	this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "	if(diffx!=0 || diffy!=0){\r\n"+
            "	  if(this.onNodeMoved){\r\n"+
            "   		if(!isNaN(div.x) && !isNaN(div.y))\r\n"+
            "		  this.onNodeMoved(div.id,div.x,div.y);\r\n"+
            "	    for(var i in this.selected){\r\n"+
            "	      var node=this.nodesById[i];\r\n"+
            "   		  if(!isNaN(node.x) && !isNaN(node.y))\r\n"+
            "		    this.onNodeMoved(node.id,node.x,node.y);\r\n"+
            "	    }\r\n"+
            "	  }\r\n"+
            "	}else{\r\n"+
            "      var button=getMouseButton(e);\r\n"+
            "      if(this.onUserClick && button==1){\r\n"+
            "        var button=getMouseButton(e);\r\n"+
            "        this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);\r\n"+
            "      }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "GraphPanel.prototype.addEdge=function(eid,id1,id2,idx,d,color){\r\n"+
            "	this.edgesById[eid]={n1:id1,n2:id2,idx:idx,dir:d,color:color};\r\n"+
            "	this.drawEdge(eid);\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.removeEdge=function(eid){\r\n"+
            "	delete this.edgesById[eid];\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.removeNode=function(nid){\r\n"+
            "	var div=this.nodesById[nid];\r\n"+
            "	delete this.nodesById[nid];\r\n"+
            "	this.graphcontainerElement.removeChild(div);\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.drawEdge=function(eid){\r\n"+
            "	var edge=this.edgesById[eid];\r\n"+
            "	var n1=this.nodesById[edge.n1];\r\n"+
            "	var n2=this.nodesById[edge.n2];\r\n"+
            "	var dir=edge.dir;\r\n"+
            "/*	if(n1.id<n2.id){\r\n"+
            "		var t=n1;\r\n"+
            "		n1=n2;\r\n"+
            "		n2=t;\r\n"+
            "		if(dir==2)\r\n"+
            "			dir=1;\r\n"+
            "		else if(dir==1)\r\n"+
            "			dir=2;\r\n"+
            "	}*/\r\n"+
            "	var i=edge.idx/4;\r\n"+
            "//	if(i!=0){\r\n"+
            "//	  if(i%2 == 0 )\r\n"+
            "//		  i=i/2;\r\n"+
            "////	  else\r\n"+
            "//		  //i=(i-1)/-2-1;\r\n"+
            "//	}\r\n"+
            "      this.context.strokeStyle = edge.color || 'black';\r\n"+
            "	if(n1==n2){\r\n"+
            "	  var r=(i+2)*this.bend/2;\r\n"+
            "	  this.context.beginPath();\r\n"+
            "      this.context.lineWidth=1;\r\n"+
            "      this.context.save();\r\n"+
            "      this.context.scale(1,.5);\r\n"+
            "	  this.context.arc((n1.x+n1.w/2+r-this.bend/2),n1.y*2, r,0,Math.PI*2,false);\r\n"+
            "      this.context.restore();\r\n"+
            "	  this.context.stroke();\r\n"+
            "	  if(dir){\r\n"+
            "		  var midx=n1.x+r*2+n1.w/2-this.bend/2;\r\n"+
            "		  var midy=n1.y;\r\n"+
            "	      this.context.beginPath();\r\n"+
            "          this.context.lineWidth=2;\r\n"+
            "		  if(dir==1){\r\n"+
            "		    this.context.moveTo(midx-this.arrowSize,midy-this.arrowSize+1);\r\n"+
            "		    this.context.lineTo(midx,midy+1);\r\n"+
            "		    this.context.lineTo(midx+this.arrowSize,midy-this.arrowSize+1);\r\n"+
            "		  }else{\r\n"+
            "		    this.context.moveTo(midx-this.arrowSize,midy+this.arrowSize-1);\r\n"+
            "		    this.context.lineTo(midx,midy-1);\r\n"+
            "		    this.context.lineTo(midx+this.arrowSize,midy+this.arrowSize-1);\r\n"+
            "		  }\r\n"+
            "	      this.context.stroke();\r\n"+
            "	  }\r\n"+
            "	}else{\r\n"+
            "      this.context.lineWidth=1;\r\n"+
            "	  drawArc(this.context,n1.x,n1.y,n2.x,n2.y,i*this.bend,dir,this.arrowSize);\r\n"+
            "	}\r\n"+
            "	this.context.stroke();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function drawArc(con,x1,y1,x2,y2,i,direction,arrowSize){\r\n"+
            "	con.beginPath();\r\n"+
            "	var midX,midY,distance,d;\r\n"+
            "	if(i!=0 || direction!=0){\r\n"+
            "	  midX=(x1+x2)/2;\r\n"+
            "	  midY=(y1+y2)/2;\r\n"+
            "	  distance=Math.sqrt(sq(x1-x2)+sq(y1-y2));\r\n"+
            "	  d=distance/2;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(i==0){\r\n"+
            "		con.moveTo(x1,y1);\r\n"+
            "		con.lineTo(x2,y2);\r\n"+
            "	}else if(i<0){\r\n"+
            "	  i=-i;\r\n"+
            "	  var n=(sq(d)-sq(i))/(2*i);\r\n"+
            "	  var radius=n+i;\r\n"+
            "	  centerX=midX+(y2-y1)*n/distance;\r\n"+
            "	  centerY=midY-(x2-x1)*n/distance;\r\n"+
            "	  midX=midX-(y2-y1)*i/distance;\r\n"+
            "	  midY=midY+(x2-x1)*i/distance;\r\n"+
            "	  var angle=Math.atan(((centerY - y1) / (centerX-x1)));\r\n"+
            "	  var angle2=Math.atan(d/n)*2; \r\n"+
            "	  var start=Math.PI + angle;\r\n"+
            "	  con.stroke();\r\n"+
            "	  con.beginPath();\r\n"+
            "	  if(centerX<x1)\r\n"+
            "		 start+=Math.PI;\r\n"+
            "	  arcFix(con,centerX,centerY,radius,start-angle2,start,false);\r\n"+
            "	}else{\r\n"+
            "	  var distance=Math.sqrt(sq(x1-x2)+sq(y1-y2));\r\n"+
            "	  var d=distance/2;\r\n"+
            "	  var n=(sq(d)-sq(i))/(2*i);\r\n"+
            "	  var radius=n+i;\r\n"+
            "	  centerX=midX+(y1-y2)*n/(distance);\r\n"+
            "	  centerY=midY-(x1-x2)*n/(distance);\r\n"+
            "	  midX=midX+(y2-y1)*i/distance;\r\n"+
            "	  midY=midY-(x2-x1)*i/distance;\r\n"+
            "	  var angle=Math.atan(((centerY - y1) / (centerX-x1)));\r\n"+
            "	  var angle2=Math.atan(d/n)*2; \r\n"+
            "	  var start=Math.PI + angle;\r\n"+
            "	  if(centerX<x1)\r\n"+
            "		  start+=Math.PI;\r\n"+
            "	  arcFix(con,centerX,centerY,radius,start,start+angle2,false);\r\n"+
            "	}\r\n"+
            "	con.stroke();\r\n"+
            "	if(direction){\r\n"+
            "	    con.beginPath();\r\n"+
            "		var dy=(y2-y1)*arrowSize/distance;\r\n"+
            "		var dx=(x2-x1)*arrowSize/distance;\r\n"+
            "		con.moveTo(midX-dx+dy,midY-dx-dy);\r\n"+
            "		if(direction==1)\r\n"+
            "		  con.lineTo(midX,midY);\r\n"+
            "		else\r\n"+
            "		  con.lineTo(midX,midY);\r\n"+
            "		con.lineTo(midX-dy-dx,midY-dy+dx);\r\n"+
            "		\r\n"+
            "	    con.stroke();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function arcFix(con,centerX,centerY,radius,start,end,fill){\r\n"+
            "	if(start>end){\r\n"+
            "	  con.arc(centerX,centerY,radius,start,end,fill);\r\n"+
            "	}else{\r\n"+
            "	  var step=100/radius;\r\n"+
            "	  while(start+step<end){\r\n"+
            "	    con.arc(centerX,centerY,radius,start,start+=step,fill);\r\n"+
            "      }\r\n"+
            "	  con.arc(centerX,centerY,radius,start,end,fill);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "GraphPanel.prototype.showContextMenu=function(menu){\r\n"+
            "  this.createMenu(menu).show(this.contextMenuPoint);\r\n"+
            "}\r\n"+
            "GraphPanel.prototype.createMenu=function(menu){\r\n"+
            "   var that=this;\r\n"+
            "   var r=new Menu(getWindow(this.containerElement));\r\n"+
            "   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e, id);} );\r\n"+
            "   return r;\r\n"+
            "}\r\n"+
            "");

	}
	
}