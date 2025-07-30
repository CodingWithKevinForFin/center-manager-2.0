

function FormEditor(form){
  this.form=form;
  this.canvas=nw('canvas');
  this.context = this.canvas.getContext('2d');
//  this.canvasElement=nw('div');
//  this.canvasElement.appendChild(this.canvas);
//  this.canvasElement.style.zIndex=10;
//  this.canvasElement.style.pointerEvents='painted';
  this.divElement=nw('div', 'portal_form_canvas');
  this.divElement.appendChild(this.canvas);
  var that=this;
  this.canvas.onmousedown=function(e){that.onMouseDown(e);};
  this.canvas.onmouseup=function(e){that.onMouseUp(e);};
  this.canvas.onmouseout=function(e){that.onMouseOut(e);};
  this.canvas.onmousemove=function(e){that.onMouseMove(e);};
  this.canvas.onMouseWheel=function(e,delta){that.onMouseWheel(e,delta);};
  this.canvasMap=new CanvasMap(this.canvas,false,false,false);
  this.form.editor=this;
  this.isVisible=false;
  this.reset();
}
FormEditor.prototype.guides={};
FormEditor.prototype.rects={};

FormEditor.prototype.reset=function(e){
  this.snapSize=-1;
  this.guides={};
  this.rects={};
  this.activeItem=null;
  this.activeItemType=null;
  this.multiSelected={};
  this.context.clearRect(-1,-1,this.canvas.width+2,this.canvas.height+2);
}

FormEditor.prototype.setVisible=function(isVisible){
  if(this.isVisible==isVisible)
	  return;
  this.isVisible=isVisible;
  if(this.isVisible)
    this.form.formDOMManager.formContainer.appendChild(this.divElement);
  else
    this.form.formDOMManager.formContainer.removeChild(this.divElement);
}


FormEditor.prototype.onMouseWheel=function(e,delta){
	if(this.activeItem!=null){
	    if(this.snapSize!=-1 && !e.shiftKey)
	      delta*=this.snapSize;
		var ai=this.activeItem;
        this.mouseWheelStart=getMousePointRelativeTo(e,this.divElement);
        if(this.activeItemType=='G'){ 
        	ai.px+=ai.isVertical ? delta : -delta; 
        	this.callSetGuidePos(ai);
        }else{
	      if(this.justSnappedX() || this.justSnappedY())
		    return 0;
		  switch(this.activeItemType){
		    case 'L':{ 
		       var deltaX=this.snap(ai.x,delta,true); 
		       this.moveSelected(deltaX,0,-deltaX,0,true);
		       break;
		    }
		    case 'T':{ 
		      var deltaY=this.snap(ai.y,-delta,false); 
		      this.moveSelected(0,deltaY,0,-deltaY,true);
		      break;
		    }
		    case 'R':{ 
		      var deltaX=this.snap(ai.x+ai.w,delta,true); 
		      this.moveSelected(0,0,deltaX,0,true);
		      break;
		    }
		    case 'B':{ 
		      var deltaY=this.snap(ai.y+ai.h,-delta,false); 
		      this.moveSelected(0,0,0,deltaY,true);
		      break;
		    }
		    case 'BL':{ 
		      var deltaY=this.snap(ai.y+ai.h,-delta,false); 
		      var deltaX=this.snap(ai.x,delta,true); 
		      this.moveSelected(deltaX,0,-deltaX,deltaY,true);
		      break;
		    }
		    case 'TR':{ 
		      var deltaY=this.snap(ai.y,-delta,false); 
		      var deltaX=this.snap(ai.x+ai.w,delta,true); 
		      this.moveSelected(0,deltaY,deltaX,-deltaY,true);
		      break;
		    }
		    case 'BR':{ 
		      var deltaY=this.snap(ai.y+ai.h,delta,false); 
		      var deltaX=this.snap(ai.x+ai.w,delta,true); 
		      this.moveSelected(0,0,deltaX,deltaY,true);
		      break;
		    }
		    case 'TL':{
		      var deltaY=this.snap(ai.y,-delta,false); 
		      var deltaX=this.snap(ai.x,-delta,true); 
		      this.moveSelected(deltaX,deltaY,-deltaX,-deltaY,true);
		      break;
		    }
	      }
        }
		this.repaint();
	}else
		this.mouseWheelStart=null;
}

FormEditor.prototype.moveSelected=function(x,y,w,h,sendToBackend){
	//if(this.activeItem!=null && this.activeItemType!='G' && !this.multiSelected[this.activeItem.id]){
		//var rect=this.activeItem;
		//rect.x+=x; rect.y+=y; rect.w+=w; rect.h+=h;
		//if(rect.w<2) rect.w=2;
		//if(rect.h<2) rect.h=2;
		//if(sendToBackend)
		  //this.callSetRectPos(rect);
	//}
	for(var i in this.multiSelected){
		var rect=this.rects[i];
		rect.x+=x; rect.y+=y; rect.w+=w; rect.h+=h;
		if(rect.w<2) rect.w=2;
		if(rect.h<2) rect.h=2;
		if(sendToBackend)
		  this.callSetRectPos(rect);
	}
}


FormEditor.prototype.justSnappedX=function(){
	if(this.snappedMillisX>0){
	  if(Date.now()<this.snappedMillisX)
		return true;
	  this.snappedMillisX=0;
	}
	return false;
}
FormEditor.prototype.justSnappedY=function(){
	if(this.snappedMillisY>0){
	  if(Date.now()<this.snappedMillisY)
		return true;
	  this.snappedMillisY=0;
	}
	return false;
}


FormEditor.prototype.snap=function(pos,delta,isX){
	if(delta==0)
	  return 0;
	var total=pos+delta;
	if(this.snapSize==-1){
	  var bestD=10;
	  var best=null;
	  for(var id in this.guides){
	    var guide=this.guides[id];
	    if(!guide.isVertical==isX) continue;
	    var d=Math.abs(guide.px-total);
	    if(d<bestD) 
	      best=guide; bestD=d;
	  }
	  return best!=null ? best.px-pos : delta;
	}
	var best=null;
	var snapPos=Math.round(total/this.snapSize)*this.snapSize;
	var r=snapPos-pos;
	var bestD=Math.abs(total-snapPos);
	var best=null;
	for(var id in this.guides){
	  var guide=this.guides[id];
	  if(!guide.isVertical==isX) continue;
	  var d=Math.abs(guide.px-total);
	  if(d<bestD){ r=guide.px-pos; bestD=d; }
	}
	return r;
}

FormEditor.prototype.callSetRectPos=function(ai){
  this.form.callBack('setRectPos',{id:ai.id, t:ai, x:ai.x, y:ai.y, w:ai.w, h:ai.h});
}
FormEditor.prototype.callSetGuidePos=function(ai){
  this.form.callBack('setGuidePos',{id:ai.id,pos:ai.px}); 
}
FormEditor.prototype.onMouseMove=function(e){
  this.selectRect=null;
  var point=getMousePointRelativeTo(e,this.divElement);
  point.x=Math.max(0,point.x);
  point.y=Math.max(0,point.y);
  var px=point.x;
  var py=point.y;
  if(this.mouseWheelStart!=null){
	  if(Math.abs(this.mouseWheelStart.x-px)<10 && Math.abs(this.mouseWheelStart.y-py)<10)
		  return;
	  this.mouseWheelStart=null;
  }
  var snap=!e.shiftKey;
  if(this.dragStart!=null){
	  var ai=this.activeItem;
	  var dx=px-this.dragStart.x;
	  var dy=py-this.dragStart.y;
      if(this.activeItem==null){
	    this.selectRect={x:this.dragStart.x,y:this.dragStart.y,w:dx,h:dy};
	    this.repaint();
	    return;
      }
      // disable pointer event when dragging
	 for (var b of amiLinkableDivs) {
		 b.style.pointerEvents='none';
	 }
	  var origDy=dy;
	  var origDx=dx;
	  var t=this.activeItemType;
	  var jsy=false;//this.justSnappedY() && Math.abs(dy)<20;
	  var jsx=false;//this.justSnappedX() && Math.abs(dx)<20;
	  if(t=='G'){
		  if(ai.isVertical) ai.px+=dx; else ai.px+=dy; 
	  }if(t=='C'){
	       if(snap){
	         dy=this.snap(ai.y,dy,false);
	         dx=this.snap(ai.x,dx,true);
	       }
	       this.moveSelected(dx,dy,0,0,false);
	  }else {
		    if(t=='T' || t=='TL' || t=='TR'){
		      if(snap)
	            dy=this.snap(ai.y,dy,false);
	          this.moveSelected(0,dy,0,-dy,false);
	        }
		    if(t=='B' || t=='BL' || t=='BR'){
		      if(snap)
	            dy=this.snap(ai.y+ai.h,dy,false);
	          this.moveSelected(0,0,0,dy,false);
	        }
		    if(t=='L' || t=='TL' || t=='BL'){
		      if(snap)
	            dx=this.snap(ai.x,dx,true); 
	          this.moveSelected(dx,0,-dx,0,false);
	        }
		    if(t=='R' || t=='TR' || t=='BR'){
		      if(snap)
	            dx=this.snap(ai.x+ai.w,dx,true); 
	          this.moveSelected(0,0,dx,0,false);
	        }
	  }
		  
      this.dragStart=point;
      this.dragStart.y-=origDy-dy;
      this.dragStart.x-=origDx-dx;
      this.repaint();
	  return;
  }
  // ensure pointer event is always on
  for (var b of amiLinkableDivs) {
	 b.style.pointerEvents='auto';
 }
  var best=null;
  var bestT=null;
  var bestD=8;
  var d;
   for(var id in this.rects){
	  var rect=this.rects[id];
	  var x=rect.x, y=rect.y, xx=rect.x+rect.w, yy=rect.y+rect.h;
	  var s=rect.w>6 && rect.h>6 ? 2 : 0;
	  var h=x+s< px && px < xx-s;
	  var v=y+s< py && py < yy-s;
	  if(h && v){
	      best=rect;
	      bestT='C';
	      bestD=0;
	  }else if(h){
		  d=abs(py-y);
		  if(d<bestD){ best=rect; bestT='T'; bestD=d; }
		  d=abs(py-yy);
		  if(d<bestD){ best=rect; bestT='B'; bestD=d; }
	  }else if(v){
		  var d=abs(px-x);
		  if(d<bestD){ best=rect; bestT='L'; bestD=d; }
		  d=abs(px-xx);
		  if(d<bestD){ best=rect; bestT='R'; bestD=d; }
	  }else if(s>0){
		  d=max(abs(px-x),abs(py-y));
		  if(d<bestD){ best=rect; bestT='TL'; bestD=d; }
		  d=max(abs(px-x),abs(py-yy));
		  if(d<bestD){ best=rect; bestT='BL'; bestD=d; }
		  d=max(abs(px-xx),abs(py-y));
		  if(d<bestD){ best=rect; bestT='TR'; bestD=d; }
		  d=max(abs(px-xx),abs(py-yy));
		  if(d<bestD){ best=rect; bestT='BR'; bestD=d; }
	  }
   }
  
     for(var id in this.guides){
	    var guide=this.guides[id];
	    d=abs((guide.isVertical ? px : py)-guide.px);
	    if(d>=bestD)
	      continue;
	    bestD=d;
	    bestT='G';
	    best=guide;
     }
     if(best!=null){
    	 if(bestT=='G')
           this.canvas.style.cursor=best.isVertical ?  'ew-resize' :'ns-resize';
    	 else if(bestT=='C'){
            this.canvas.style.cursor='move';
    	 }else if(bestT=='T' || bestT=='B')
           this.canvas.style.cursor='ns-resize';
    	 else if(bestT=='L' || bestT=='R')
           this.canvas.style.cursor='ew-resize';
    	 else if(bestT=='TL' || bestT=='BR')
           this.canvas.style.cursor='nwse-resize';
    	 else if(bestT=='TR' || bestT=='BL')
           this.canvas.style.cursor='nesw-resize';
     }else
        this.canvas.style.cursor='default';
     if(this.activeItem!=best || this.activeItemType!=bestT){
	     this.activeItem=best;
	     this.activeItemType=bestT;
	     this.repaint();
     }
}
FormEditor.prototype.onMouseUp=function(e){
  if(this.trackingMouseDrag){
	this.trackingMouseDrag=false;
    document.onmouseup=null;
    document.onmousemove=null;
  }
  if(this.dragStart!=null && this.activeItem!=null){
	  var ai=this.activeItem;
	  if(this.activeItemType=='G')
	    this.form.callBack('setGuidePos',{id:this.activeItem.id,pos:this.activeItem.px});
	  else 
		  this.moveSelected(0, 0,0, 0, true);
  }
  this.mouseWheelStart=null;
  this.dragStart=null;
  if(this.selectRect!=null){
	  var sr=this.selectRect;
	  for(var id in this.rects){
		var r=this.rects[id];
		if(isBetween(r.x+r.w/2,sr.x,sr.x+sr.w) && 
		   isBetween(r.y+r.h/2,sr.y,sr.y+sr.h)){
		  this.multiSelected[r.id]=true;
		}
	  }
	  this.selectRect=null;
	  this.sendSelectedToBackend();
	  this.repaint();
  }
}
FormEditor.prototype.onMouseOut=function(e){
	if (this.activeItem) {
		this.activeItem=null;
		this.activeItemType=null;
		this.repaint();
	}
}

FormEditor.prototype.onMouseDown=function(e){
	
	if(document.onmouseup==null){
	  var that=this;
	  this.trackingMouseDrag=true;
	  document.onmouseup=function(e){that.onMouseUp(e)};
	  document.onmousemove=function(e){that.onMouseMove(e)};
	}

    this.mouseWheelStart=null;
    var p=getMousePoint(e);
	if(getMouseButton(e)==2){
		if(this.activeItem!=null)
		  this.form.callBack('editItem',{t:this.activeItemType,x:p.x,y:p.y,id:this.activeItem.id});
		else 
		  this.form.callBack('editModeMenu',{x:p.x,y:p.y});
	}else{
        this.dragStart=getMousePointRelativeTo(e,this.divElement);
	}
	if(this.activeItemType!='G'){
	  if(this.activeItem!=null){
	    if(e.ctrlKey){
		if(this.multiSelected[this.activeItem.id])
			delete this.multiSelected[this.activeItem.id];
		else 
			this.multiSelected[this.activeItem.id]=true;
	    }else if(e.shiftKey){
		  this.multiSelected[this.activeItem.id]=true;
	    }else if(!this.multiSelected[this.activeItem.id]){
	      this.multiSelected={};
		  this.multiSelected[this.activeItem.id]=true;
	    }
	  }else if(!e.shiftKey){
	    this.multiSelected={};
	  }
      this.sendSelectedToBackend();
	}
	this.repaint();
}

FormEditor.prototype.sendSelectedToBackend=function(){
//  this.form.callBack('editModeSelection',{values:joinMap(',','=',Object.keys(this.multiSelected).toString())});
  this.form.callBack('editModeSelection',{values:JSON.stringify(Object.keys(this.multiSelected)), active:this.activeItem != null? this.activeItem.id :null});
}

FormEditor.prototype.setSize=function(width,height){
	  width = Math.max(this.form.formDOMManager.formContainer.clientWidth,width);
	  height = Math.max(this.form.formDOMManager.formContainer.clientHeight,height);
	  this.canvas.style.width=toPx(width);
	  this.canvas.style.height=toPx(height);
	  this.canvas.width=width;
	  this.canvas.height=height;
      this.context.translate(.5,.5);
}

FormEditor.prototype.addGuide=function(id,px,isVertical){
	this.guides[id]={id:id,px:px,isVertical:isVertical};
}
FormEditor.prototype.addRect=function(id,x,y,w,h,isSelected){
	this.rects[id]={id:id,x:x,y:y,w:w,h:h};
	if(isSelected)
	  this.multiSelected[id]=true;
	else
	  delete this.multiSelected[id];
}
FormEditor.prototype.removeGuide=function(id){
	delete this.guides[id];
}
FormEditor.prototype.setSnap=function(snapSize){
    this.snapSize=snapSize;
}
FormEditor.prototype.removeRect=function(id){
	delete this.rects[id];
}
FormEditor.prototype.repaint=function(){
    this.canvasMap.clear();
//	this.context.fillStyle='rgba(0,0,0,0)';
//	this.canvasMap.fillRect(0,0,this.context.width,this.context.height);
    this.context.lineWidth=1;
    if(this.snapSize>1){
		this.context.strokeStyle='#00AA00';
	    this.context.beginPath();
      for(var x=0;x<this.canvas.width+1;x+=this.snapSize){
        for(var y=0;y<this.canvas.height+1;y+=this.snapSize){
	      this.canvasMap.moveTo(x,y);
	      this.canvasMap.lineTo(x+1,y+1);
        }
      }
	    this.context.stroke();
      
    }
	for(var id in this.guides){
		var guide=this.guides[id];
		this.context.fillStyle=this.context.strokeStyle=guide==this.activeItem ? '#00AA00' : (this.activeItem==null ? 'rgba(200,200,200,.5)' : "#AAAAAA") ;
	    this.context.beginPath();
	    if(guide.isVertical){
	      this.canvasMap.moveTo(guide.px,0);
	      this.canvasMap.lineTo(guide.px,this.canvas.height+1);
	      if(guide==this.activeItem)
			this.canvasMap.drawText(guide.px+" px",guide.px-2,2,0,ALIGN_RIGHT,ALIGN_TOP);
	    }else{
	      this.canvasMap.moveTo(0,guide.px);
	      this.canvasMap.lineTo(this.canvas.width+1,guide.px);
	      if(guide==this.activeItem)
			this.canvasMap.drawText(guide.px+" px",2,guide.px-2,0,ALIGN_LEFT,ALIGN_BOTTOM);
	    }
	    this.context.stroke();
	}
	for(var id in this.rects){
		var rect=this.rects[id];
		var isActive=rect==this.activeItem;
		var isSelected=this.multiSelected[rect.id];
		if(!isSelected && !isActive)
			continue;
		this.context.fillStyle=isActive? 'rgba(0,255,0,.3)' : (isSelected ? 'rgba(0,255,0,.1)' : 'rgba(0,0,0,.0)');
		
	    this.canvasMap.fillRect(rect.x,rect.y,rect.w,rect.h);
	    
//	    this.context.clearRect(rect.x+3,rect.y+3,rect.w-6,rect.h-6);
		this.context.fillStyle=this.context.strokeStyle=isSelected||isActive? '#00AA00' : '#888888';
	    this.context.beginPath();
	    this.canvasMap.strokeRect(rect.x,rect.y,rect.w,rect.h);
	    if(isActive || isSelected){
			this.canvasMap.drawText("("+rect.x+", "+rect.y+") ",rect.x,rect.y,45,ALIGN_RIGHT,ALIGN_MIDDLE);
			this.canvasMap.drawText(" ("+(rect.x+rect.w)+", "+(rect.y+rect.h)+") ",rect.x+rect.w,rect.y+rect.h,45,ALIGN_LEFT,ALIGN_MIDDLE);
			if(rect.w<28)
			  this.canvasMap.drawText("("+rect.w+" w) ",rect.x+rect.w/2,rect.y,-90,ALIGN_LEFT,ALIGN_MIDDLE);
			else
			  this.canvasMap.drawText("("+rect.w+" w)",rect.x+rect.w/2,rect.y,0,ALIGN_MIDDLE,ALIGN_BOTTOM);
			if(rect.h<28)
			  this.canvasMap.drawText(" ("+rect.h+" h)",rect.x+rect.w,rect.y+rect.h/2,0,ALIGN_LEFT,ALIGN_MIDDLE);
			else
			  this.canvasMap.drawText("("+rect.h+" h)",rect.x+rect.w,rect.y+rect.h/2,90,ALIGN_MIDDLE,ALIGN_BOTTOM);
	      }
	    this.context.stroke();
	}
	if(this.selectRect!=null){
		this.context.strokeStyle='#444444';
		this.context.save();
        this.context.lineWidth=2;
        this.context.translate(-.5,-.5);
		this.context.setLineDash([3,3]);
	    this.canvasMap.strokeRect(this.selectRect.x,this.selectRect.y,this.selectRect.w,this.selectRect.h);
		this.context.restore();
	}
}
