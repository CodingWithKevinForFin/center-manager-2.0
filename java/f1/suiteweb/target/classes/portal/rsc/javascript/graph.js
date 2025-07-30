

function GraphPanel(containerElement){
	var that=this;
	this.containerElement=containerElement;
	this.graphcontainerElement=nw('div');
	this.scrollPane=new ScrollPane(this.containerElement, 15, this.graphcontainerElement);	
	this.containerElement.className+=' graph_panel';
	this.canvas=nw('canvas','graph_panel');
    this.context = this.canvas.getContext('2d');
	this.graphcontainerElement.appendChild(this.canvas);
	this.nodesById={};
	this.edgesById={};
	this.selected={};
	this.maxNodeX=0;
	this.maxNodeY=0;
	this.bend=8;
	this.arrowSize=this.bend/2.5;
	makeDraggable(this.canvas);
    this.canvas.ondraggingStart=function(e,event){that.onBackgroundDraggingStart(e,event);};
    this.canvas.ondragging=function(e,diffx,diffy){that.onBackgroundDragging(e,diffx,diffy);};
    this.canvas.ondraggingEnd=function(e,diffx,diffy,event){that.onBackgroundDraggingEnd(e,diffx,diffy,event);};
    this.selectionBox=nw('div','graph_panel_selectbox');
    this.containerElement.tabIndex=1;
    this.containerElement.onkeydown=function(e){that.handleKeyDown(e)};
    this.scrollPane.onScroll=function(){that.onScroll()};
    this.scrollPane.DOM.paneElement.style.overflow="visible";
    this.pixel=this.context.createImageData(1,1);
    this.pixel.data[0]=128;
    this.pixel.data[1]=128;
    this.pixel.data[2]=128;
    this.pixel.data[3]=128;
    this.gridSize=1;
    this.snapSize=1;
    
	//this.addEdge(1,1,2);
	//this.addEdge(2,2,3);
	//this.addEdge(3,3,1);
	//this.addEdge(4,4,1);
}

GraphPanel.prototype.onScroll=function(){
	this.scrollPane.DOM.innerpaneElement.style.top=toPx(-this.scrollPane.getClipTop());
	this.scrollPane.DOM.innerpaneElement.style.left=toPx(-this.scrollPane.getClipLeft());
}

GraphPanel.prototype.ensureVisibleNode=function(id){
	if(this.nodesById.hasOwnProperty(id)){
		var node = this.nodesById[id];
		var rectNode = node.getBoundingClientRect();
		this.scrollPane.ensureVisible(rectNode);
	}
}

GraphPanel.prototype.ensureVisibleNodes=function(nodeIds){
	var nodeIdsArray = nodeIds.split("|");
	var len = nodeIdsArray.length;
	for(var i = (len - 1); i >= 0; i--){
		var id = parseInt(nodeIdsArray[i]);
		this.ensureVisibleNode(id);
	}	
}

GraphPanel.prototype.handleKeyDown=function(e){
	if (e.keyCode==37 || e.keyCode==38 || e.keyCode==39 || e.keyCode==40)
		this.onUserDirectionKey(e);
}
GraphPanel.prototype.addNodes=function(json){
	for(var i in json){
	  var n=json[i];
	  var sel=n.sel==true;
	  if(sel)
	     this.selected[n.id]=true;
	  else
		  delete this.selected[n.id];
	  this.addNode(n.id,n.x,n.y,n.w,n.h,n.n,n.s,sel,n.o);
	}
}
GraphPanel.prototype.selectNodes=function(json){
	for(var i in json){
		var n=json[i];
		var node = this.nodesById[n.id]; 
		
		if(node == null)
			continue;
		
		var sel=n.sel==true;
		if(sel == node.sel)
			continue;
		node.sel = sel;
		if(sel == true){
			this.selected[n.id]=true;
			node.classList.add("graph_node_selected");
		}else{
			delete this.selected[n.id];
			node.classList.remove("graph_node_selected");
			
		}
	}
}
GraphPanel.prototype.removeNodes=function(json){
	for(var i in json){
	  this.removeNode(json[i]);
	}
}
GraphPanel.prototype.addEdges=function(json){
	for(var i in json){
	  var n=json[i];
	  this.addEdge(n.id,n.n1,n.n2,n.idx,n.d,n.c);
	}
}
GraphPanel.prototype.removeEdges=function(json){
	for(var i in json){
	  this.removeEdge(json[i]);
	}
}
GraphPanel.prototype.setGridSnap=function(grid,snap){
    this.gridSize=grid;
    this.snapSize=snap;
}
GraphPanel.prototype.clearData=function(){
	for(var node in this.nodesById)
	  this.graphcontainerElement.removeChild(this.nodesById[node]);
	this.nodesById={};
	this.edgesById={};
	this.selected={};
}
GraphPanel.prototype.clearEdges=function(){
	this.edgesById={};
}
// Duplicate GraphPanel.prototype.repaint=function(){
GraphPanel.prototype.setSize=function(w,h){
	this.width=w;
	this.height=h;
//	this.containerElement.style.width=toPx(w);
//	this.containerElement.style.height=toPx(h);
	this.scrollPane.setLocation(0,0,this.width,this.height);
	this.repaint();
}
GraphPanel.prototype.getCanvasWidth=function(){
	var w=this.width-20;
	for(var i in this.nodesById)
		w=Math.max(w,this.nodesById[i].x+this.nodesById[i].w/2);
	return w;
}
GraphPanel.prototype.getCanvasHeight=function(){
	var h=this.height-20;
	for(var i in this.nodesById)
		h=Math.max(h,this.nodesById[i].y+this.nodesById[i].h/2);
	return h;
}

GraphPanel.prototype.repaint=function(){
	var w=this.getCanvasWidth();
	var h=this.getCanvasHeight();
	this.scrollPane.setPaneSize(w, h);
	h=Math.max(this.height,h);
	w=Math.max(this.width,w);
//	this.graphcontainerElement.style.width=toPx(w);
//	this.graphcontainerElement.style.height=toPx(h);
	this.canvas.width=w;
	this.canvas.height=h;
    this.context.clearRect(0,0,this.canvas.width,this.canvas.height);
    if(this.gridSize>1){
      this.context.strokeStyle = '#EEEEEE';
      this.context.lineWidth = 1;
	  this.context.beginPath();
      for(var x=.5;x<w;x+=this.gridSize){
	    this.context.moveTo(x,0);
	    this.context.lineTo(x,h);
      }
      for(var y=.5;y<h;y+=this.gridSize){
	    this.context.moveTo(0,y);
	    this.context.lineTo(w,y);
      }
    }
	this.context.stroke();
    for(var i in this.edgesById)
    	this.drawEdge(i);
}


GraphPanel.prototype.addNode=function(id,x,y,w,h,innerHTML,cssStyle,sel,options){
	var div=this.nodesById[id];
	var that=this;
	if(div==null){
	  div=nw('div','graph_node');
	  this.nodesById[id]=div;
	  this.graphcontainerElement.appendChild(div);
	}
	var movable=options & 2;
	var selectable=options & 1;
	if(movable){
	  if(div.ondragging==null){
	    makeDraggable(div,div);
        div.ondraggingStart=function(e,event){return that.onDraggingStart(e,event);};
        div.ondragging=function(e,diffx,diffy){that.onDragging(e,diffx,diffy);};
        div.ondraggingEnd=function(e,diffx,diffy,event){that.onDraggingEnd(e,diffx,diffy,event);};
	  }
	}else{
	  if(div.ondragging!=null){
	    removeDraggable(div,div);/* TODO: implement method */
        div.ondraggingStart=null;
        div.ondragging=null;
        div.ondraggingEnd=null;
	  }
	  if(selectable){
        div.onmousedown=function(e){that.onUserClick(div.id,e.shiftKey,e.ctrlKey,getMouseButton(e));};
	  }
	}
	div.ondblclick=function(e){that.onUserDblClick(div.id)};

	var style=div.style;
	style.left=toPx(x-fl(w/2));
	style.top=toPx(y-fl(h/2));
	style.width=toPx(w);
	style.height=toPx(h);
	div.x=x;
	div.cssStyle=cssStyle;
	div.y=y;
	div.w=w;
	div.h=h;
	div.sel=sel;
	div.id=id;
	div.innerHTML=innerHTML;
	if(div.sel)
		div.className='graph_node graph_node_selected';
	else
		div.className='graph_node';
	applyStyle(div,div.cssStyle);
}

GraphPanel.prototype.onBackgroundDraggingStart=function(div,e){
	this.graphcontainerElement.appendChild(this.selectionBox);
	var p=getMouseLayerPoint(e);
	this.selectionBox.startX=p.x;
	this.selectionBox.startY=p.y;
	this.selectionBox.style.left=toPx(p.x);
	this.selectionBox.style.top=toPx(p.y);
	this.selectionBox.style.width=toPx(1);
	this.selectionBox.style.height=toPx(1);
}

GraphPanel.prototype.onBackgroundDragging=function(div,diffx,diffy){
	if(diffx<0){
	  this.selectionBox.style.left=toPx(this.selectionBox.startX+diffx);
	  this.selectionBox.style.width=toPx(-diffx);
	}else
	  this.selectionBox.style.width=toPx(diffx);
	if(diffy<0){
	  this.selectionBox.style.top=toPx(this.selectionBox.startY+diffy);
	  this.selectionBox.style.height=toPx(-diffy);
	}else
	  this.selectionBox.style.height=toPx(diffy);
}
GraphPanel.prototype.onBackgroundDraggingEnd=function(div,diffx,diffy,e){
	this.contextMenuPoint=getMousePoint(e).move(-4,-4);
	this.graphcontainerElement.removeChild(this.selectionBox);
    var button=getMouseButton(e);
    if(this.onUserSelect)
      this.onUserSelect(this.selectionBox.startX,this.selectionBox.startY,diffx,diffy,e.shiftKey,e.ctrlKey,button);
}

GraphPanel.prototype.onDraggingStart=function(div,e){
    var button=getMouseButton(e);
    var isSelected=this.selected[div.id]==true;
    
    if(e.ctrlKey || e.shiftKey){
      if(this.onUserClick){
	    this.contextMenuPoint=getMousePoint(e).move(-4,-4);
        this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);
      }
      return false;
    }
    if(!isSelected){
    	if(button==2){
           if(this.onUserClick){
	         this.contextMenuPoint=getMousePoint(e).move(-4,-4);
             this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);
           }
    	   return false;
    	}
    	else{
    	  this.selected={};
    	  this.selected[div.id]=true;
          if(this.onUserClick){
	         this.contextMenuPoint=getMousePoint(e).move(-4,-4);
             this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);
          }
    	}
    }
    
    if(button==2){
           if(this.onUserClick){
	         this.contextMenuPoint=getMousePoint(e).move(-4,-4);
             this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);
           }
    	   return false;
    }
    
    
	div.xstart=div.x;
	div.ystart=div.y;
	for(var i in this.selected){
	  var node=this.nodesById[i];
	  node.xstart=node.x;
	  node.ystart=node.y;
	}
}

function snap(val,snapSize){
	return rd(val/snapSize) * snapSize;
}

GraphPanel.prototype.onDragging=function(div,diffx,diffy){
	if(diffx==0 && diffy==0)
		return;
//	div.x=snap(diffx+div.xstart,25);
//	div.y=snap(diffy+div.ystart,25);
	for(var i in this.selected){
	  var node=this.nodesById[i];
	  var w=fl(node.w/2);
	  var h=fl(node.h/2);
	  var x=snap(diffx+node.xstart-w,this.snapSize);
	  var y=snap(diffy+node.ystart-h,this.snapSize);
	  node.x=x+w;
	  node.y=y+h;
	  node.style.left=toPx(x);
	  node.style.top=toPx(y);
	}
	this.repaint();
}
GraphPanel.prototype.onDraggingEnd=function(div,diffx,diffy,e){
	this.contextMenuPoint=getMousePoint(e).move(-4,-4);
	if(diffx!=0 || diffy!=0){
	  if(this.onNodeMoved){
   		if(!isNaN(div.x) && !isNaN(div.y))
		  this.onNodeMoved(div.id,div.x,div.y);
	    for(var i in this.selected){
	      var node=this.nodesById[i];
   		  if(!isNaN(node.x) && !isNaN(node.y))
		    this.onNodeMoved(node.id,node.x,node.y);
	    }
	  }
	}else{
      var button=getMouseButton(e);
      if(this.onUserClick && button==1){
        var button=getMouseButton(e);
        this.onUserClick(div.id,e.shiftKey,e.ctrlKey,button);
      }
	}
}



GraphPanel.prototype.addEdge=function(eid,id1,id2,idx,d,color){
	this.edgesById[eid]={n1:id1,n2:id2,idx:idx,dir:d,color:color};
	this.drawEdge(eid);
}
GraphPanel.prototype.removeEdge=function(eid){
	delete this.edgesById[eid];
}
GraphPanel.prototype.removeNode=function(nid){
	var div=this.nodesById[nid];
	delete this.nodesById[nid];
	this.graphcontainerElement.removeChild(div);
}
GraphPanel.prototype.drawEdge=function(eid){
	var edge=this.edgesById[eid];
	var n1=this.nodesById[edge.n1];
	var n2=this.nodesById[edge.n2];
	var dir=edge.dir;
/*	if(n1.id<n2.id){
		var t=n1;
		n1=n2;
		n2=t;
		if(dir==2)
			dir=1;
		else if(dir==1)
			dir=2;
	}*/
	var i=edge.idx/4;
//	if(i!=0){
//	  if(i%2 == 0 )
//		  i=i/2;
////	  else
//		  //i=(i-1)/-2-1;
//	}
      this.context.strokeStyle = edge.color || 'black';
	if(n1==n2){
	  var r=(i+2)*this.bend/2;
	  this.context.beginPath();
      this.context.lineWidth=1;
      this.context.save();
      this.context.scale(1,.5);
	  this.context.arc((n1.x+n1.w/2+r-this.bend/2),n1.y*2, r,0,Math.PI*2,false);
      this.context.restore();
	  this.context.stroke();
	  if(dir){
		  var midx=n1.x+r*2+n1.w/2-this.bend/2;
		  var midy=n1.y;
	      this.context.beginPath();
          this.context.lineWidth=2;
		  if(dir==1){
		    this.context.moveTo(midx-this.arrowSize,midy-this.arrowSize+1);
		    this.context.lineTo(midx,midy+1);
		    this.context.lineTo(midx+this.arrowSize,midy-this.arrowSize+1);
		  }else{
		    this.context.moveTo(midx-this.arrowSize,midy+this.arrowSize-1);
		    this.context.lineTo(midx,midy-1);
		    this.context.lineTo(midx+this.arrowSize,midy+this.arrowSize-1);
		  }
	      this.context.stroke();
	  }
	}else{
      this.context.lineWidth=1;
	  drawArc(this.context,n1.x,n1.y,n2.x,n2.y,i*this.bend,dir,this.arrowSize);
	}
	this.context.stroke();
}


function drawArc(con,x1,y1,x2,y2,i,direction,arrowSize){
	con.beginPath();
	var midX,midY,distance,d;
	if(i!=0 || direction!=0){
	  midX=(x1+x2)/2;
	  midY=(y1+y2)/2;
	  distance=Math.sqrt(sq(x1-x2)+sq(y1-y2));
	  d=distance/2;
	}
	
	if(i==0){
		con.moveTo(x1,y1);
		con.lineTo(x2,y2);
	}else if(i<0){
	  i=-i;
	  var n=(sq(d)-sq(i))/(2*i);
	  var radius=n+i;
	  centerX=midX+(y2-y1)*n/distance;
	  centerY=midY-(x2-x1)*n/distance;
	  midX=midX-(y2-y1)*i/distance;
	  midY=midY+(x2-x1)*i/distance;
	  var angle=Math.atan(((centerY - y1) / (centerX-x1)));
	  var angle2=Math.atan(d/n)*2; 
	  var start=Math.PI + angle;
	  con.stroke();
	  con.beginPath();
	  if(centerX<x1)
		 start+=Math.PI;
	  arcFix(con,centerX,centerY,radius,start-angle2,start,false);
	}else{
	  var distance=Math.sqrt(sq(x1-x2)+sq(y1-y2));
	  var d=distance/2;
	  var n=(sq(d)-sq(i))/(2*i);
	  var radius=n+i;
	  centerX=midX+(y1-y2)*n/(distance);
	  centerY=midY-(x1-x2)*n/(distance);
	  midX=midX+(y2-y1)*i/distance;
	  midY=midY-(x2-x1)*i/distance;
	  var angle=Math.atan(((centerY - y1) / (centerX-x1)));
	  var angle2=Math.atan(d/n)*2; 
	  var start=Math.PI + angle;
	  if(centerX<x1)
		  start+=Math.PI;
	  arcFix(con,centerX,centerY,radius,start,start+angle2,false);
	}
	con.stroke();
	if(direction){
	    con.beginPath();
		var dy=(y2-y1)*arrowSize/distance;
		var dx=(x2-x1)*arrowSize/distance;
		con.moveTo(midX-dx+dy,midY-dx-dy);
		if(direction==1)
		  con.lineTo(midX,midY);
		else
		  con.lineTo(midX,midY);
		con.lineTo(midX-dy-dx,midY-dy+dx);
		
	    con.stroke();
	}
}

function arcFix(con,centerX,centerY,radius,start,end,fill){
	if(start>end){
	  con.arc(centerX,centerY,radius,start,end,fill);
	}else{
	  var step=100/radius;
	  while(start+step<end){
	    con.arc(centerX,centerY,radius,start,start+=step,fill);
      }
	  con.arc(centerX,centerY,radius,start,end,fill);
	}
}

GraphPanel.prototype.showContextMenu=function(menu){
  this.createMenu(menu).show(this.contextMenuPoint);
}
GraphPanel.prototype.createMenu=function(menu){
   var that=this;
   var r=new Menu(getWindow(this.containerElement));
   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e, id);} );
   return r;
}
