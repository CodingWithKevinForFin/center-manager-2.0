function TreeMap(element){ 
	  var that=this; 
	  this.setRatio(4);
	  this.clearData();
	  this.element=element;
	  this.selectBorderColor1='#0000AA';
	  this.selectBorderColor2='#AAAAFF';
	  this.backgroundColor='white';
	  this.element.style.background=this.backgroundColor;
		
	  this.setStickyness(0);
	  
	  
	  this.canvas=nw('canvas');
	  this.canvas.style.position='absolute';
	  this.canvas.style.background='white';
	  this.canvas.style.left='0px';
	  this.canvas.style.top='0px';
	  this.canvas.width=1500;
	  this.canvas.height=700;
	  this.context = this.canvas.getContext('2d');
	  
	  this.canvas2=nw('canvas');
	  this.canvas2.style.position='absolute';
	  this.canvas2.style.left=this.canvas.style.left;
	  this.canvas2.style.top=this.canvas.style.top;
	  this.canvas2.width=this.canvas.width;
	  this.canvas2.height=this.canvas.width;
	  this.context2 = this.canvas2.getContext('2d');
	  
	  
	  this.zoom=1;
	  this.offsetX=0;
	  this.offsetY=0;
	  this.canvas2.onMouseWheel=function(e,delta){that.setZoom(e,delta);};
	  this.canvas2.onmousemove=function(e){that.onMouseMove(getMousePoint(e))};
	  this.canvas2.onmouseout=function(e){that.onMouseOut(e);};
	  this.canvas2.onmousedown=function(e){that.onMouseDown(e);};
	  
	  //MOBILE SUPPORT - support double click to zoom, touch move & touch hold for context menu
	  this.canvas2.ontouchmove=function(e){that.onTouchDrag(e);};
	  this.canvas2.ontouchend=function(e){that.onTouchup(e);};
	  this.canvas2.ontouchstart=function(e){e.stopPropagation(); that.onUserClick(that.activeNode ? that.activeNode.id : null,e.shiftKey,e.ctrlKey,getMouseButton(e));that.onTouchDown(e,null,null,null,0);};
	  this.canvas2.addEventListener('touchend', function(e) {
		    that.onDblTap(e);
		});
	  this.canvas2.oncontextmenu=function(e){if(that.onClick) that.onClick(e,that.activeNode)};
	  this.element.appendChild(this.canvas);
	  this.element.appendChild(this.canvas2);
	  this.context.lineCap='butt';
	  this.draggingXdiff=null;
	  this.draggingYdiff=null;
	  this.hoverRequestNid=null;
	  this.borderSizes=[];
	  this.textSizes=[];
	  this.bgColors=[];
	  this.borderColors=[];
	  this.textColors=[];
	  this.needsLayout=true;
	  this.textHAlign='center';
	  this.textVAlign='center';
	  this.touchXDiff=this.offsetX;
	  this.touchYDiff=this.offsetY;
	} 

    TreeMap.prototype.onMouseDown=function(e){
	  var button=getMouseButton(e);
	  if(button==1|| button==2){
	      this.wasDragged=false;
	      var point=getMouseLayerPoint(e);
		  this.draggingXdiff=point.x+this.offsetX;
		  this.draggingYdiff=point.y+this.offsetY;
		  this.canvas2.style.cursor='move';
		  var that=this;
          this.owningWindow.onmouseup=function(e){
        	  that.onMouseup(e)
        	  };
		  this.owningWindow.onmousemove=function(e){that.onMouseDrag(e)};
	  }
    }
    //MOBILE SUPPORT - on touch down
    TreeMap.prototype.onTouchDown=function(e){
    //touch hold delay for menu
  	  this.longTouchTimer = setTimeout(() => {
			if(this.zoom > 1){
				var p=getMousePointRelativeTo(e,this.canvas);
				var x=p.x;
				var y=p.y;
				this.currentMousePoint=p;
				this.onTouchHold(this.activeNode ? this.activeNode.id : 1,false,false,2)
				return;
			}
	  }, 500);
    	
  	  var button=getMouseButton(e);
	  if(this.hoverDiv!=null)
		    getRootNodeBody(this.element).removeChild(this.hoverDiv);
	  this.hoverDiv=null;
  	  this.wasDragged=false;
  	  var point=getMousePoint(e);
  	  this.touchXDiff=point.x+this.offsetX;
  	  this.touchYDiff=point.y+this.offsetY;
  	  var that=this;
      this.owningWindow.ontouchend=function(e){
    	  that.onTouchup(e)
      };
  	  this.owningWindow.ontouchmove= function(e){
       	  that.onTouchDrag(e)
   	  };
   }
    
	TreeMap.prototype.onMouseDrag=function(e){
          var point=getMousePointRelativeTo(e,this.canvas);
            this.setOffset(this.draggingXdiff-point.x,this.draggingYdiff-point.y);
    	    if(this.zoom>1)
    	      this.wasDragged=true;
	    this.currentMousePoint=point;
	    
	}
    
    TreeMap.prototype.clearHover=function(e){
  	  if(this.hoverDiv!=null){
	  		try {
	  			getDocument(this.element).body.removeChild(this.hoverDiv);
	  		} catch (e) {
	  			console.log("unable to clear heatmap hover: " + e);
	  		}
	  		this.hoverDiv=null;
	  	  }
      }

    //MOBILE SUPPORT - on touch move
	TreeMap.prototype.onTouchDrag=function(e){
		//clear touch hold delay for menu
		clearTimeout(this.longTouchTimer);
		this.longTouchTimer=null;
        var point=getMousePointRelativeTo(e,this.canvas);
        if(this.touchDiff != null){
        	var diffX = Math.min(10*this.zoom, Math.max(-10*this.zoom, this.touchDiff.x-point.x));
        	var diffY = Math.min(10*this.zoom, Math.max(-10*this.zoom, this.touchDiff.y-point.y));       	  
        }
        // checks if heatmap is zoomed. otherwise, dont drag
		if(this.zoom>1)
			this.setTouchOffset((isNaN(this.offsetX) ? 0 : this.offsetX)  + diffX,(isNaN(this.offsetY) ? 0 : this.offsetY)  + diffY);
    	   this.wasDragged=true;
    	   this.touchDiff= point;
	    
	}	
	TreeMap.prototype.onDblTap = (function() {
	    let lastTapTime = 0;
	    const doubleTapDelay = 200; // Maximum time (ms) between taps for it to be considered a double tap

	    return function(e) {
	        const currentTime = new Date().getTime();
	        const timeSinceLastTap = currentTime - lastTapTime;

	        if (timeSinceLastTap < doubleTapDelay && timeSinceLastTap > 0) {
	            const p = getMousePointRelativeTo(e, this.canvas);
	            this.setZoom(e, this.zoom * 1.5);
	        }

	        lastTapTime = currentTime;
	    };
	})();
    TreeMap.prototype.onMouseOut=function(e){
	  if(this.hoverDiv!=null)
	    getRootNodeBody(this.element).removeChild(this.hoverDiv);
	  this.hoverDiv=null;
	  this.hoverRequestNid=null;
	  if(!this.showingMenu)
	    this.onMouseMove(null)
    }
    TreeMap.prototype.onMouseup=function(e){
    	clearTimeout(this.longTouchTimer);
    	this.longTouchTimer=null;
		this.draggingXdiff=null;
		this.draggingYdiff=null;
    	this.canvas2.style.cursor=null;
    	this.owningWindow.onmouseup=null;
    	this.owningWindow.onmousemove=null;
        if(!this.wasDragged){
	  if(this.onUserClick)
	    this.onUserClick(this.activeNode ? this.activeNode.id : null,e.shiftKey,e.ctrlKey,getMouseButton(e));
        }
    }
    //MOBILE SUPPORT - on touch up
    TreeMap.prototype.onTouchup=function(e){
    	clearTimeout(this.longTouchTimer);
    	this.longTouchTimer=null;
    	this.canvas2.style.cursor=null;
        this.owningWindow.ontouchend=null;
		this.owningWindow.ontouchmove=null;
		// if not dragged, select node
        if(!this.wasDragged){
		  if(this.onUserClick)
		    this.onUserClick(this.activeNode ? this.activeNode.id : null,e.shiftKey,e.ctrlKey,getMouseButton(e));
        }
    }

    /* 0=no sticky 1=very stick */
    TreeMap.prototype.setStickyness=function(o){
      if(o<0)o=0;
      else if(o>2)o=5;
	  this.aspectStickyness=1+o;
	  this.sortStickyness=1+o;
    }
  //MOBILE SUPPORT - cancel zoom menu for touch
    TreeMap.prototype.cancelZoom=function(){
    	this.zoom=1;
	    this.setOffset(0,0);
	    this.updateActiveNode();
    	this.repaint();
    }

    
    TreeMap.prototype.setZoom=function(e,delta){
		var point=getMouseLayerPoint(e);
		
		var pointX=point.x;
		var pointY=point.y;
		if(pointX==null)
		  pointX=this.canvas.width/2;
		if(pointY==null)
		  pointY=this.canvas.height/2;
    	var xpos=(pointX+this.offsetX)/this.zoom;
    	var ypos=(pointY+this.offsetY)/this.zoom;
    	
    	delta=delta/5;
    	this.zoom*=(1+delta);
    	if(this.zoom<1){
    		this.zoom=1;
    	    this.setOffset(0,0);
    	}else{
    	  this.setOffset(xpos*this.zoom-pointX,ypos*this.zoom-pointY);
    	}
    }
    TreeMap.prototype.setOffset=function(x,y){
  	  var w=this.canvas.width;
  	  var h=this.canvas.height;
  	  this.offsetX=x;
  	  this.offsetY=y;
  	  if(this.zoom*w<w+this.offsetX)
  		  this.offsetX=(this.zoom-1)*w;
  	  if(this.zoom*h<h+this.offsetY)
  		  this.offsetY=(this.zoom-1)*h;
  	  
  	  if(this.offsetX<0)
  	    this.offsetX=0;
  	  if(this.offsetY<0)
  	    this.offsetY=0;
  	    this.activeNode=null;
  	this.repaint();
  }
    
  //MOBILE SUPPORT - touch offset for heatmap
    TreeMap.prototype.setTouchOffset=function(x,y){
  	  var w=this.canvas.width;
  	  var h=this.canvas.height;
  	  this.offsetX=x;
  	  this.offsetY=y;
  	  if(this.zoom*w<w+this.offsetX)
  		  this.offsetX=(this.zoom-1)*w;
  	  if(this.zoom*h<h+this.offsetY)
  		  this.offsetY=(this.zoom-1)*h;
  	  
  	  if(this.offsetX<0)
  	    this.offsetX=0;
  	  if(this.offsetY<0)
  	    this.offsetY=0;
  	    this.activeNode=null;
  	  this.repaint();
  }
    TreeMap.prototype.setOptions=function(o){
	  if(o.stickyness)this.setStickyness(o.stickyness*1);
	  if(o.selectBorderColor1)this.selectBorderColor1=o.selectBorderColor1;
	  if(o.selectBorderColor2)this.selectBorderColor2=o.selectBorderColor2;
	  if(o.ratio)this.setRatio(o.ratio*1);
	  if(o.backgroundColor)this.backgroundColor=o.backgroundColor;
	  if(o.backgroundColor)this.backgroundColor=o.backgroundColor;
	  if(o.textHAlign)this.textHAlign=o.textHAlign;
	  if(o.textVAlign)this.textVAlign=o.textVAlign;
	  if(o.fontFamily)this.fontFamily=o.fontFamily;
	  this.needsLayout=true;
    }    

	TreeMap.prototype.clearData=function(data){
		this.nodes=[];
	    this.needsLayout=true;
	}
	
	TreeMap.prototype.removeNodes=function(ids){
		for(var i=0;i<ids.length;i++)
		  this.removeNode(ids[i]);
	    this.needsLayout=true;
	}
	
	TreeMap.prototype.setDepthStyles=function(data){
		for(var i=0;i<data.length;i++){
			var d=data[i];
			var depth=d.depth;
			this.borderSizes[depth]=d.borderSize;
			this.textSizes[depth]=d.textSize;
			this.borderColors[depth]=d.borderColor;
			this.textColors[depth]=d.textColor;
			this.bgColors[depth]=d.bgColor;
		}
	    this.needsLayout=true;
	}
	
	TreeMap.prototype.addNodes=function(data){
		for(var i=0;i<data.length;i++)
		  this.addNode(data[i]);
	    this.needsLayout=true;
	}
	
	TreeMap.prototype.addNode=function(data){
		var pid=data.pid;
		var parent;
		if(pid!=-1){
		  parent=this.nodes[pid];
		  if(parent==null && pid!=-1)//we'll create a dummy, with the expectation that the parent will arrive soon
			parent=this.nodes[data.pid]={id:data.pid,children:{},childrenCount:0};
	      parent.childrenSorted=null;
		}else
			parent=null;
		
		var id=data.id;
		var existing=this.nodes[id];
		if(existing==null)
			existing=this.nodes[id]={id:id,parent:parent,childrenCount:0};
		else 
			existing.parent=parent;
		existing.size=data.v;
		existing.heat=data.h; 
		existing.textColor=data.t; 
		existing.selected=data.s==1;
		this.applyName(existing,data.n);
		if(pid==-1)
			this.rootNode=existing;
		else{
			if(parent.children==null)
				parent.children={};
			parent.children[id]=existing;
			parent.childrenCount++;
		}
	}
	
	TreeMap.prototype.removeNode=function(id){
		var node=this.nodes[id];
		if(node==null)
			return;
		  node.parent.childrenSorted=null;
		  if(--node.parent.childrenCount==0)
			  delete node.parent.children;
		  else
		    delete node.parent.children[id];
		delete this.nodes[id];
	}
	
	TreeMap.prototype.applyName=function(node,name){
		if(node.name==name)
			return;
		var names=name.split('\n');
		var maxLen=this.context.measureText(names[0]).width;
		var maxName=names[0];
		for(var i=1;i<names.length;i++){
	       var len=this.context.measureText(names[i]).width;
	       if(len<maxLen)
	    	   continue;
	       maxLen=len;
	       maxName=names[i];
		}
		node.name=name;
		node.names=names;
		node.maxName=maxName;
	}
	

		
	TreeMap.prototype.setSize=function(w,h){
		this.keyGradientWidth=200;
		this.width=w;
		this.height=h;
		this.element.width=w;
		this.element.height=h;
		this.canvas.width=this.width;
		this.canvas.height=this.height;
		this.canvas2.width=this.canvas.width;
		this.canvas2.height=this.canvas.height;
    	if(this.zoom*w<w+this.offsetX)
    	  this.offsetX=(this.zoom-1)*w;
    	if(this.zoom*h<h+this.offsetY)
    	  this.offsetY=(this.zoom-1)*h;
    	if(this.offsetX<0)
    	  this.offsetX=0;
    	if(this.offsetY<0)
    	  this.offsetY=0;
	    this.needsLayout=true;
		this.repaint();
	}

	TreeMap.prototype.onMouseMove=function(point){
		if(point!=null){
	      var t=new Rect().readFromElement(this.canvas2);
	      point.move(-t.left,-t.top);
		}
	    this.currentMousePoint=point;
	    this.updateActiveNode();
	    if(point!=null && this.onHover && this.activeNode){
	    	if(this.activeNode.id!=this.hoverRequestNid){
	    	  this.hoverRequestNid=this.activeNode.id;
	    	  this.onHover(point.x,point.y,this.activeNode.id);
	        }else
	           this.updateHoverLocation();
	    }
	}
	
	//MOBILE SUPPORT - touch move for heatmap
	TreeMap.prototype.onTouchMove=function(point){
		if(point!=null){
	      var t=new Rect().readFromElement(this.canvas2);
	      point.move(-t.left,-t.top);
		}
	    this.currentMousePoint=point;
	    this.updateActiveNode();
	    if(point!=null && this.onHover && this.activeNode){
	    	if(this.activeNode.id!=this.hoverRequestNid){
	    	  this.hoverRequestNid=this.activeNode.id;
	    	  this.onHover(point.x,point.y,this.activeNode.id);
	        }else
	           this.updateHoverLocation();
	    }
	}
	TreeMap.prototype.setHover=function(nid,name){
	  if(this.activeNode==null || this.activeNode.id!=nid)
		  return;
		const rootBody = getRootNodeBody(this.element);
		if(this.hoverDiv!=null)
		  rootBody.removeChild(this.hoverDiv);
		this.hoverDiv=nw("div","treemap_tooltip");
		this.hoverDiv.innerHTML=name;
		if(this.hoverDiv.firstChild!=null && this.hoverDiv.firstChild.tagName=='DIV')
			this.hoverDiv=this.hoverDiv.firstChild;
		rootBody.appendChild(this.hoverDiv);
		this.updateHoverLocation();
	}

	TreeMap.prototype.updateHoverLocation=function(){
	  var div=this.hoverDiv;
	  if(div==null)
		  return;
	  var x=this.currentMousePoint.x;
	  var y=this.currentMousePoint.y;
	  var origin = this.element.getBoundingClientRect();
	  var rect=new Rect().readFromElement(div);
	  var h=rect.height+4;
	  var w=rect.width+6;
	  div.style.left=toPx(origin.x+x-w);
	  div.style.top=toPx(origin.y+y-h);
	  ensureInDiv(div,getRootNodeBody(this.element));
	}


	TreeMap.prototype.updateActiveNode=function(){
		var point=this.currentMousePoint;
		var node = null;
		if(point==null)
			node=null;
		else{
    	  var xpos=(point.x+this.offsetX)/this.zoom;
    	  var ypos=(point.y+this.offsetY)/this.zoom;
	      if(this.activeNode!=null && this.activeNode.x<=xpos && this.activeNode.x+this.activeNode.w>xpos && this.activeNode.y<=ypos && this.activeNode.y+this.activeNode.h>ypos){
	    	return;
	      }
	      node=this.findNodeAt(xpos,ypos,this.nodes);
		}
	    if(this.activeNode==node)
	    	return;
	    this.activeNode=node;
	    this.context2.clearRect(0,0,this.canvas2.width,this.canvas2.height);
	    return node;
	}
	TreeMap.prototype.updateWhiteHighlight=function(node){
		  if (!node) return;
	      this.context2.save();
		  this.context2.translate(-this.offsetX,-this.offsetY);
		  this.context2.scale(this.zoom,this.zoom);
		  this.context2.beginPath();
		  this.context2.fillStyle='white';
	      this.context2.globalAlpha=.3;
		  this.context2.fillRect(node.x,node.y,node.w,node.h);
		  this.context2.stroke();
	      this.context2.restore();
	}
	TreeMap.prototype.findNodeAt=function(x,y,data){
		for(var i in data){
			var parent=data[i];
			if(parent.x<=x && parent.x+parent.w>x && parent.y<=y && parent.y+parent.h>y){
			  if(parent.children)
				  return this.findNodeAt(x,y,parent.children);
			  else
				  return parent;
			}
				
		}
	}

	TreeMap.prototype.fillChildRect=function(x,y,w,h,node){
	  var pixels=w*h;
	  if(w<1/this.zoom && h<1/this.zoom)
		  return;
	  this.context.fillStyle=node.heat;
	  this.context.fillRect(x,y,w,h);
	 
	  var c=node.textColor;
	  if(c==null)
	    c=this.textColors[node.depth];
	  if(w>10/this.zoom && h>10/this.zoom)
	    this.writeText2(x,y,w,h,this.textSizes[node.depth],c,node,2);
	  if(node.selected){
		this.drawBorder(x,y,w,h, this.selectBorderColor1,this.selectBorderColor2);
	  } else if (node.borderColor) {
		this.drawBorder(x,y,w,h, node.borderColor);
	  }
	}
	

	TreeMap.prototype.getFont=function(fontSize){
		return "normal "+fontSize+'px ' + this.fontFamily;
	}
	
	
	TreeMap.prototype.writeText2=function(x,y,w,h,fontSize,fontColor,node,margin){
	  if(!node.names)
		  return;
	  margin=margin/this.zoom;
	  w-=margin+margin; 
	  h-=margin+margin; 
	  var hz=h*this.zoom;
	  var wz=w*this.zoom;
	  if(hz<6 || wz<6)
		  return; 
	  
	  x+=margin;y+=margin;
	  var maxFs=fontSize;
	  
	  fontSize=cl(fontSize*this.zoom);
	  var names=node.names;
	  
	  this.context.font=this.getFont(fontSize);
	  var textHeight=fontSize;
	  if(textHeight>hz/names.length){
		  textHeight=fl(hz/names.length);
		  fontSize=textHeight;
	      this.context.font=this.getFont(fontSize);
	  }
	  var textWidth=this.context.measureText(node.maxName).width;
	  if(textWidth>wz){
		  fontSize=fontSize*(wz/textWidth);
	      this.context.font=this.getFont(fontSize);
	      textHeight=fontSize;
	  }
	  if(fontSize<8){
	    if(fontSize<6)
		  return;
		fontSize=8;
	    this.context.font=this.getFont(fontSize);
	  }
	  this.context.fillStyle=fontColor;
	  var centerX,centerY;
	  var hAlignScale;
	  var vAlignScale;
	  var padding;
	  if(node.depth==0){
	    hAlignScale=.5;
	    vAlignScale=.5;
	    padding=0;
	  }else{
	    hAlignScale=this.hAlignScale;
	    vAlignScale=this.vAlignScale;
	    padding=4;
	  }
	  padding/=this.zoom;
	  
      if(hAlignScale==0) this.context.textAlign='left';
	  else if(hAlignScale==1) this.context.textAlign='right';
	  else this.context.textAlign='center';
	  
	  if(vAlignScale==0) this.context.textBaseline='top';
	  else if(vAlignScale==1) this.context.textBaseline='bottom';
	  else this.context.textBaseline='middle';
	  
	  centerX=padding+x+((w-padding*2)*hAlignScale);
	  centerY=padding+y+((h-padding*2)*vAlignScale);
	  this.context.save();
	  this.context.beginPath();
	  this.context.rect(x,y,w,h);
	  this.context.translate(centerX,centerY);
	  this.context.clip();
	  this.context.scale(1/this.zoom,1/this.zoom);
	  var t=(names.length-1)/2;
	  for(var i=0;i<names.length;i++)
	    this.context.fillText(names[i],0,(i-t)*textHeight);
	  this.context.restore();
	}
	
	

	TreeMap.prototype.repaint=function(){
	    this.context.save();
	    if(this.textHAlign=='left') this.hAlignScale=0;
	    else if(this.textHAlign=='right') this.hAlignScale=1;
	    else this.hAlignScale=.5;
	    
	    if(this.textVAlign=='top') this.vAlignScale=0;
	    else if(this.textVAlign=='bottom') this.vAlignScale=1;
	    else this.vAlignScale=.5;
	    
	    
	    
		this.context.translate(-this.offsetX,-this.offsetY);
		this.context.scale(this.zoom,this.zoom);
		this.maxHeat=null;
		var t=this.rootNode;
		if(t==null || t.children==null || (Object.keys(t.children).length == 0) ){
//            this.context.fillStyle="white";
            this.context.fillStyle=this.backgroundColor;
            this.context.fillRect(0,0,this.canvas.width,this.canvas.height);
			return;
		}
		this.calcSizes(t,-1);
		this.childrenSorted=t.childrenSorted;
		var time1=Date.now();
		if(this.needsLayout)
	      this.layoutChildren(t,0,0,this.canvas.width,this.canvas.height,0,true,true);
		this.needsLayout=false;
		var time2=Date.now();
	    
	    this.minX=(0+this.offsetX)/this.zoom;
	    this.maxX=(this.canvas.width+this.offsetX)/this.zoom;
	    
	    this.minY=(0+this.offsetY)/this.zoom;
	    this.maxY=(this.canvas.height+this.offsetY)/this.zoom;
	    
		var time3=Date.now();
	    this.paintChildren(t,true);
		var time4=Date.now();
	    var node = this.updateActiveNode();
	    if(node!=null){
	    	this.updateWhiteHighlight(node);
	    }
	    this.context.restore();
	}
	TreeMap.prototype.outsideClip=function(node){
		return (node.x+node.w<this.minX || node.y+node.h<this.minY || node.x>this.maxX || node.y>this.maxY);
	}
	TreeMap.prototype.paintChildren=function(node,isRoot){
		
		if(!node.children){
          this.fillChildRect(node.x,node.y,node.w,node.h,node);
          return;
		}
		
		var boxSize=node.w*node.h*this.zoom*this.zoom;
		var totSize=this.canvas.width*this.canvas.height;
		
		if(boxSize<totSize/500){
		      this.fillChildRect(node.x,node.y,node.w,node.h,node);
		}else{
		  if(!isRoot){
	        this.context.fillStyle=this.borderColors[node.depth+1];
	        this.context.fillRect(node.x,node.innerY,node.w,node.innerH);
		  }else{
	        this.context.fillStyle=this.borderColors[node.depth+1];
	        this.context.fillRect(node.x,node.y,node.w,node.h);
		  }
		  var childrenSorted=node.childrenSorted;
		  for(var i=0;i<childrenSorted.length;i++){
		    var child=childrenSorted[i];
		    if(this.outsideClip(child))
			  continue;
		    var x=child.x,y=child.y,w=child.w,h=child.h;
		    if(w && h){
		        this.paintChildren(child,false);
		    }
		  }
		}
		if(!isRoot){
	      this.context.fillStyle=node.heat;
	      if(boxSize<totSize/100){
	    	  this.context.globalAlpha=1-boxSize/(totSize/100);
	          this.context.fillRect(node.innerX,node.innerY,node.innerW,node.innerH);
	    	  this.context.globalAlpha=1;
	      }
	      this.context.fillRect(node.x,node.y,node.w,node.innerY-node.y);
	      var c=node.textColor;
          this.writeText2(node.x,node.y,node.w,node.h-node.innerH,this.textSizes[node.depth],c!=null ?  c: this.textColors[node.depth],node,2);
	      this.context.strokeStyle=this.borderColors[node.depth];
	      this.context.fillStyle=node.heat;
	      this.context.beginPath();
	      var t=Math.min(5,this.zoom*node.w/10)/this.zoom;
	      this.context.moveTo(node.x+t,node.innerY);
	      this.context.lineTo(node.x+t+t,node.innerY+t);
	      this.context.lineTo(node.x+t*3,node.innerY);
	      this.context.fill();
		  this.context.lineWidth=1/this.zoom;
	      this.context.beginPath();
	      this.context.moveTo(node.x,node.innerY+.5/this.zoom);
	      this.context.lineTo(node.x+t,node.innerY+.5/this.zoom);
	      this.context.lineTo(node.x+t+t,node.innerY+t+.5/this.zoom);
	      this.context.lineTo(node.x+t*3,node.innerY+.5/this.zoom);
	      this.context.lineTo(node.x+node.w,node.innerY+.5/this.zoom);
	      this.context.stroke();
		}
	}


	TreeMap.prototype.calcSizes=function(data,depth){
		data.depth=depth;
		var children=data.children;
		if(children!=null){
		    depth++;
		      data.childrenSorted=[];
		      var totSize=0;
		      for(var i in children){
		    	var c=children[i];
			    if(c.children)
			      this.calcSizes(c,depth);
			    else
			    	c.depth=depth;
		    	totSize+=c.size;
		        data.childrenSorted[data.childrenSorted.length]=c;
		      }
		      data.childrenSize=totSize;
		      data.childrenSorted.sort(function(a,b){return b.size-a.size;});
		    }
	}

	TreeMap.prototype.layoutChildren=function(data,x,y,w,h,isLeftMost,isTopMost){
	  var pbw=data.depth==-1 ? 0 : this.borderSizes[data.depth];
	  var pbh=data.depth==-1 ? 0 : this.borderSizes[data.depth];
	  if(pbw>w/20)
		  pbw=w/20;
	  if(pbh>h/20)
		  pbh=h/20;
	  
	  if(!isLeftMost){
	    data.x=x;
	    data.w=w-pbw;
	  }else{
	    data.x=x+pbw;
	    data.w=w-pbw-pbw;
	  }
	  
	  if(!isTopMost){
	    data.y=y;
	    data.h=h-pbh;
	  }else{
	    data.y=y+pbh;
	    data.h=h-pbh-pbh;
	  }
	  
	  
	  
	  data.innerX=data.x;
	  data.innerY=data.y;
	  data.innerW=data.w;
	  data.innerH=data.h;
	  
	  if(data.children && data.depth>=0){
		var titleSize=this.textSizes[data.depth]+1;
		if(titleSize*6>h)
		  titleSize=data.h/6;
	    data.innerY+=titleSize;
	    data.innerH-=titleSize;
	  }
	  
	  
	  if(data.children)
	    this.layout(data);
	}

	
	TreeMap.prototype.diffNums=function(a){
		return(a>1) ? diff(1/a,this.oneOverRatio): diff(a,this.ratio);
	}
	TreeMap.prototype.setRatio=function(ratio){
		this.ratio=ratio;
		this.oneOverRatio=1/ratio;
	}
	TreeMap.prototype.getRatio=function(ratio){
		return this.ratio;
	}
	
	TreeMap.prototype.layout=function(dataset){
		var x=dataset.innerX;
		var y=dataset.innerY;
		var w=dataset.innerW;
		var h=dataset.innerH;
		var total=dataset.childrenSize;
		var dataOffset=0;
		var data=dataset.childrenSorted;
		var sticky=data.sticky;
		if(sticky==null)
		  sticky=data.sticky=[];
		var stickyDepth=0;
		while(dataOffset<data.length){
		  var pixelRatio=w*h/total;
		  var lastLayoutAspect=1000000;
		  var lastLayoutOffset=-1;
		  if(stickyDepth<data.sticky.length){
			lastLayoutAspect=sticky[stickyDepth].aspect;
			lastLayoutOffset=sticky[stickyDepth].offset;
		  }
		
		  var totalSize=0;
		  var endOffset=dataOffset;
		  var isWide=w>=h;
		  var t=(isWide ? h*h : w*w)/pixelRatio;
		  var lastAspect=null;
		  while(endOffset<data.length){
		    var val=data[endOffset].size;
		    var tot=totalSize+val;
		    var aspect=val*t/tot/tot;
		    if(isWide)
		      aspect=1/aspect;
		    if(lastAspect!=null && this.diffNums(aspect)>=this.diffNums(lastAspect))
		      break;
		    lastAspect=aspect;
		    totalSize=tot;
		    endOffset++;
		  }
		  if(lastLayoutOffset!=endOffset && lastLayoutOffset!=-1){
		    change=lastLayoutAspect/aspect;
		    if(change<this.aspectStickyness && this.ratio==1){
			  aspect=lastLayoutAspect;
			  endOffset=lastLayoutOffset;
			  totalSize=0;
			  for(var i=dataOffset;i<endOffset;i++)
		        totalSize+=data[i].size;
		    }else{
			  data.sticky=[];
			  sticky=null;
		   }
		  }
		  if(sticky!=null)
		    sticky[stickyDepth]={aspect:aspect,offset:endOffset};
		  if(isWide){
		    var split=endOffset==data.length ? w :  fl(pixelRatio*totalSize/h);
		    var yy=y;
		    var runSize=0;
		    for(var i=dataOffset;i<endOffset;i++){
			  var d=data[i];
			  runSize+=d.size;
			  var yy2=i+1==endOffset ? y+h : fl(y+runSize*h/totalSize);
			  this.layoutChildren(d,x,yy,split,yy2-yy,x==dataset.innerX,yy==dataset.innerY);
			  yy=yy2;
		    }
		    x+=split;
		    w-=split;
		  }else{
		    var split=endOffset==data.length ? h : fl(pixelRatio*totalSize/w);
		    var xx=x;
		    var runSize=0;
		    for(var i=dataOffset;i<endOffset;i++){
			  var d=data[i];
			  runSize+=d.size;
			  var xx2=i+1==endOffset ? x+w : fl(x+runSize*w/totalSize);
			  this.layoutChildren(d,xx,y,xx2-xx,split,xx==dataset.innerX,y==dataset.innerY);
			  xx=xx2;
		    }
		    y+=split;
		    h-=split;
		  }
		  stickyDepth++;
		  dataOffset=endOffset;
		  total-=totalSize;
		}
	}

TreeMap.prototype.showContextMenu=function(menu){
  var t=new Rect().readFromElement(this.canvas2);
  this.createMenu(menu).show(new Point(this.currentMousePoint).move(t.left-3,t.top-3));
}


TreeMap.prototype.createMenu=function(menu){
   this.showingMenu=true;
   var that=this;
   var activeNode=this.activeNode;
   var r=new Menu(getWindow(this.element));
   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e, id, activeNode ? activeNode.id: -1);} );
   r.onHide=function(){that.showingMenu=false;};
   return r;
}

TreeMap.prototype.handleKeydown=function(e){
	if(e.key==" "){
		    this.setZoom(e,-1000);
	}else if(e.ctrlKey){
	  var distance=e.shiftKey ? 3 : 1;
	  if(e.key=="ArrowUp" || e.key=="ArrowRight")
		    this.setZoom(e,distance);
	  if(e.key=="ArrowDown"|| e.key=="ArrowLeft")
		    this.setZoom(e,-distance);
	}else{
	  var distance=(e.shiftKey ? 100 : 10) ;
	  if(e.key=="ArrowLeft")
	      this.setOffset(this.offsetX-distance,this.offsetY);
	  else if(e.key=="ArrowRight")
	      this.setOffset(this.offsetX+distance,this.offsetY);
	  else if(e.key=="ArrowUp")
	      this.setOffset(this.offsetX,this.offsetY-distance);
	  else if(e.key=="ArrowDown")
	      this.setOffset(this.offsetX,this.offsetY+distance);
	}
//	if(e.key=="Control")
//		this.canvas.style.cursor="move";
}

TreeMap.prototype.handleNodeStyleChange=function(changes){
	for (var i=0; i < changes.length; i++) {
		var data = changes[i];
		var node = this.nodes[data.nid];
		if (node != null) {
			node.borderColor=data.borderColor; // use during repaint
		}
	}
}

TreeMap.prototype.drawBorder=function(x,y,w,h,borderColor,borderColor2){
	var ss=this.context.strokeStyle;
	var n=1/this.zoom;
	this.context.lineWidth=n;
	var x2=(x)+.5*n;
	var y2=(y)+.5*n;
	this.context.strokeStyle=borderColor;
	this.context.beginPath();
	this.context.rect(x2,y2,w-1*n,h-1*n);
	this.context.rect(x2+2*n,y2+2*n,w-5*n,h-5*n);
	this.context.stroke();
	this.context.strokeStyle=borderColor2? borderColor2 : borderColor;
	this.context.beginPath();
	this.context.rect(x2+1*n,y2+1*n,w-3*n,h-3*n);
	this.context.rect(x2+3*n,y2+3*n,w-7*n,h-7*n);
	this.context.stroke();
	this.context.strokeStyle=ss;
}