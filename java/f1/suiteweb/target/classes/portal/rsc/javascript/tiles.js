
function TilesPanel(containerElement){
	var that=this;
	this.containerElement=containerElement;
	this.tilesElement=nw('div','tiles_panel');
	this.containerElement.appendChild(this.tilesElement);
    this.tilesElement.style.left='0px';
    this.vscrollElement=nw("div");
	this.tileWidth=100;
	this.tilePadding=10;
	this.tileHeight= 50;
	this.tileDivsPool={};
	this.tilesOffset=0;
    this.clipTop=0;
    this.vscroll=new ScrollBar(this.vscrollElement,false);
    this.vscroll.onscroll=function(){that.onScroll()};
    this.vscrollVisible=false;
    this.scrollSize=15;
    this.tilesCount=0;
    this.tilesByPosition=[];
    this.tilesById={};
    this.clipTop=0;
    this.clipBottom=-1;
    this.selectedTiles=new SelectedRows();
    this.align='left';
//    this.containerElement.onMouseWheel=function(e,delta){that.onMouseWheel(e,delta);};
    this.onTileMouseClickFunc=function(e){e.stopPropagation();return that.onTileMouseClick(e);};
    this.onTileMouseDblClickFunc=function(e){e.stopPropagation();return that.onTileMouseDblClick(e);};
    this.onTileKeyDownFunc=function(e){e.stopPropagation();return that.onTileKeyDown(e);};
    this.tilesElement.onmousedown=function(e){return that.onBackgroundClick(e);};
    this.tilesElement.ondblclick=function(e){return that.onBackgroundDblClick(e);};
    this.activeTilePos=-1;
}

TilesPanel.prototype.onBackgroundClick=function(e){
	if(this.tilesElement!=getMouseTarget(e))
		return;
	var button=getMouseButton(e);
	this.selectedTiles.parseString("");
	this.activeTilePos=-1;
	if(button===2){
	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);
	  if(this.onUserContextMenu!=null)
		  this.onUserContextMenu(e);
	}else{
	  if(this.onUserSelected!=null){
		this.onUserSelected(this.activeTilePos,this.selectedTiles.toString());
	  }
	  this.repaint();
	}
	return true;
}

TilesPanel.prototype.onBackgroundDblClick=function(e){
	if(this.onUserDblClick!=null)
	  this.onUserDblClick(e,-1);
	return true;
}

TilesPanel.prototype.onTileKeyDown=function(e){
	var tileDiv=getMouseTarget(e);
	var tile=tileDiv.tile;
    var pos=this.activeTilePos;
    if(pos===-1)
    	return;
	var active=this.tilesByPosition[this.activeTolPos];
	var newPos=-1;
	if(e.keyCode===38){//up
	  newPos=pos-this.columnsCount;
	}else if(e.keyCode===40){//down
	  newPos=pos+this.columnsCount;
	}else if(e.keyCode===37){//left
	  newPos=pos-1;
	}else if(e.keyCode===39){//right
	  newPos=pos+1;
	}
	//var newActiveTile=this.tilesByPosition[newPos];
	//if(newActiveTile!=null){
	if(newPos>=0 && newPos<this.tilesCount)
		this.selectTile(newPos,e.ctrlKey,e.shiftKey);
	//}
	
	//else if(e.keyCode==33)//pgup
	//else if(e.keyCode==34)//pgdn
}
TilesPanel.prototype.onTileMouseDblClick=function(e){
	var tileDiv=getMouseTarget(e);
	var button=getMouseButton(e);
	var tile=tileDiv.tile;
	if(tile===null)
		return;
	if(this.onUserDblClick!=null){
	  this.onUserDblClick(e,tile.pos);
	}
	return true;
}
TilesPanel.prototype.onTileMouseClick=function(e){
	var tileDiv=getMouseTarget(e);
	var button=getMouseButton(e);
	var tile=tileDiv.tile;
	if(tile===null)
		return;
	var pos=tile.pos;
	if(button===2){
	  if(!this.selectedTiles.isSelected(pos)){
		  this.selectedTiles.clear();
		  this.selectTile(pos,e.ctrlKey,e.shiftKey);
		  this.repaint();
	  }
	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);
	  if(this.onUserContextMenu!=null)
		  this.onUserContextMenu(e);
	}else{
	  this.selectTile(pos,e.ctrlKey,e.shiftKey)
	}
	return true;
}
TilesPanel.prototype.selectTile=function(pos,ctrlKey,shiftKey){
	var before=this.selectedTiles.toString();
	var beforeActiveTilePos=this.activeTilePos;
	var repaint=false;
    if(shiftKey && this.multiselect){
      this.selectedTiles.add(this.activeTilePos===-1 ? pos : this.activeTilePos,pos);
      this.setActiveTilePos(pos);
      repaint=true;
    }else if(ctrlKey && this.multiselect){
      if(this.selectedTiles.isSelected(pos))
  	    this.selectedTiles.remove(pos);
      else{
  	    this.selectedTiles.add(pos);
        this.setActiveTilePos(pos);
      }
    }else {
    	if(!this.selectedTiles.isEmpty()){
  	       this.selectedTiles.clear();
  	       repaint=true;
    	}
  	    this.selectedTiles.add(pos);
        this.setActiveTilePos(pos);
    }
    	
    
    if(repaint){
	  this.repaint();
      var t=this.tileDivsPool[this.activeTilePos];
    }else{
      var tile=this.tilesByPosition[pos];
	  this.applyStyle(this.tileDivsPool[tile.pos],tile);
	}
    
	var after=this.selectedTiles.toString();
		this.onUserSelected(this.activeTilePos,this.selectedTiles.toString());
}
TilesPanel.prototype.setSelectedTiles=function(sel){
	this.selectedTiles.parseString(sel);
}
TilesPanel.prototype.setActiveTilePos=function(pos){
  var oldActive=this.tilesByPosition[this.activeTilePos];
  this.activeTilePos=pos;
  if(oldActive!=null){
    var oldDiv=this.tileDivsPool[oldActive.pos];
    if(oldDiv!=null)
      this.applyStyle(oldDiv,oldActive);
  }
  var active=this.tilesById[this.activeTileId];
  if(active!=null){
    var activeDiv=this.tileDivsPool[active.pos];
    if(activeDiv!=null)
      this.applyStyle(activeDiv,active);
    this.activeTilePos=this.activeTileId;
  }
}

TilesPanel.prototype.addTile=function(id,pos,name,style,sel){
	var tile={
			id:id,
			name:name,
			style:style,
			pos:1*pos,
			sel:sel===true
			};
	this.tilesByPosition[pos]=tile;
	this.tilesById[tile.id]=tile;
	if(between(pos,this.clipTop,this.clipBottom-1)){
	  var tileDiv=this.tileDivsPool[pos];
	  if(tileDiv!=null){
		  this.applyStyle(tileDiv,tile);
	  }
	}
}


TilesPanel.prototype.setLocation=function(x,y,width,height){
    this.width=width;
	this.height=height;
}

TilesPanel.prototype.setTilesCount=function(count){
    this.tilesCount=count;
    this.updateBounds();
    this.repaint();
}
	

TilesPanel.prototype.setLocation=function(x,y,width,height){
    this.width=width;
	this.height=height;
    new Rect(x,y,width,height).writeToElement(this.containerElement);
	this.vscroll.setLocation(this.width-this.scrollSize,0,this.scrollSize,this.height);
    this.updateBounds();
    this.repaint();
}


TilesPanel.prototype.updateBounds=function(){
	this.tilesPerRow=Math.max(1,fl((this.width-this.scrollSize) / (this.tilePadding+this.tileWidth)));
	this.totalHeight=cl(this.tilesCount / this.tilesPerRow) * (this.tilePadding+this.tileHeight);
	var vscrollVisible=this.totalHeight>this.height;
	if(vscrollVisible!=this.vscrollVisible){
		this.vscrollVisible=vscrollVisible;
		if(this.vscrollVisible){
          this.vscroll.setClipTop(0);
          this.containerElement.appendChild(this.vscrollElement);
          this.onScroll();
		}else{
          this.containerElement.removeChild(this.vscrollElement);
          this.tilesElement.style.top='0px';
		}
	}
	var width=this.width;
	if(this.vscrollVisible)
  	  width-=this.scrollSize;
	var xInc=this.tilePadding+this.tileWidth;
	var yInc=this.tilePadding+this.tileHeight;
	this.columnsCount=Math.max(1,fl(width / xInc));
	this.rowsVisible=Math.max(1,1+cl(this.height / yInc));
    this.tilesVisible=this.columnsCount *this.rowsVisible;
	if(this.vscrollVisible){
      this.vscroll.setPaneLength(this.totalHeight);
      this.vscroll.setClipLength(this.height);
  	  width-=this.scrollSize;
	}else{
	  this.setClipzone(0,this.tilesVisible);
	}
	if(this.cssStyle!=null)
	  applyStyle(this.tilesElement,this.cssStyle);
}

TilesPanel.prototype.clearData=function(){
	this.tilesByPosition=[];
	this.tilesById={};
	for(var i in this.tileDivsPool)
		this.tilesElement.removeChild(this.tileDivsPool[i]);
	this.tileDivsPool={};
}
TilesPanel.prototype.initTiles=function(multiselect){
	this.multiselect=multiselect;
}

TilesPanel.prototype.addChildren=function(data){
	for(var i in data){
		var child=data[i];
		this.addTile(child.i,child.p,child.n,child.s);
	}
}

TilesPanel.prototype.setOptions=function(options){
	if(options.tileWidth!=null)
		this.tileWidth=fl(options.tileWidth);
	if(options.align!=null)
		this.align=options.align;
	if(options.tileWidth!=null)
		this.tileHeight=fl(options.tileHeight);
	if(options.tilePadding!=null)
		this.tilePadding=fl(options.tilePadding);
	if(options.cssStyle!=null)
		this.cssStyle=options.cssStyle;
	this.updateBounds();
}

TilesPanel.prototype.repaint=function(){
	var minPadding=fl(this.tilePadding/2);
	var width=this.width;
	var top=0;
	if(this.vscrollVisible){
		width-=this.scrollSize;
	}
    this.tilesElement.style.width=toPx(this.width);
    this.tilesElement.style.height=toPx(this.height+this.tileHeight+this.tilePadding);
	var xInc=this.tilePadding+this.tileWidth;
	var yInc=this.tilePadding+this.tileHeight;
	var extra=fl((width - this.columnsCount * xInc)/this.columnsCount);
	xInc+=extra;
	var xPadding=fl(Math.max(minPadding,(this.tilePadding+extra)/2));
	var yPadding=this.tilePadding;//Math.min(xPadding,minPadding*2);
	var i=this.clipTop;
	
	var pos=this.clipTop;
	outer:for(var r=0;r<this.rowsVisible;r++){
	  var tilesOnThisRow = Math.min(this.columnsCount,this.tilesCount-i);
	  var leftAdjust=0;
	  var spacing=0;
	  if(this.align==='center')
	      leftAdjust=(this.width-(xPadding+tilesOnThisRow*xInc))/2;
	  else if(this.align==='right')
	      leftAdjust=this.width-(xPadding+tilesOnThisRow*xInc);
	  else if(this.align==='justify'){
	      leftAdjust=this.width-(xPadding+tilesOnThisRow*xInc);
	      leftAdjust/=tilesOnThisRow+1;
	      xInc+=leftAdjust;
	  }
		  
	  if(tilesOnThisRow<1)
		  break;
		  
	  for(var c=0;c<this.columnsCount;c++){
		  var pos=i++;
		  var x=leftAdjust+xPadding+c*xInc;
		  var y=yPadding+r*yInc;
		  var tileDiv=this.tileDivsPool[pos];
		  var tile=this.tilesByPosition[pos];
		  if(tile===undefined && tileDiv===undefined){ //no data and no div means no work
			  continue;
		  }else if(tileDiv==null){
    	      var tileDiv=nw('div','tiles_tile');
	          tileDiv.tile=tile;
              tileDiv.tabIndex=0;
	          tileDiv.onmousedown=this.onTileMouseClickFunc;
	          tileDiv.ondblclick=this.onTileMouseDblClickFunc;
              tileDiv.onkeydown=this.onTileKeyDownFunc;
    	      this.tileDivsPool[pos]=tileDiv;
	          this.tilesElement.appendChild(tileDiv);
		  } else if(tile==null){
			  this.tilesElement.removeChild(tileDiv);
			  continue;
		  }
		  tileDiv.style.left=toPx(x);
		  tileDiv.style.top=toPx(y);
    	  tileDiv.style.width=toPx(this.tileWidth);
    	  tileDiv.style.height=toPx(this.tileHeight);
		  this.applyStyle(tileDiv,tile);
		  x+=xInc;
	  }
	}
}


TilesPanel.prototype.applyStyle=function(tileDiv,tile){
	tileDiv.innerHTML=tile.name;
	if(tile.pos===this.activeTilePos){
	  if(this.selectedTiles.isSelected(tile.pos))
	    tileDiv.className='tiles_tile tiles_selected tiles_active';
	  else
	    tileDiv.className='tiles_tile tiles_active';
	}else if(this.selectedTiles.isSelected(tile.pos))
	  tileDiv.className='tiles_tile tiles_selected';
	else
	  tileDiv.className='tiles_tile';
	applyStyle(tileDiv,tile.style);
}
TilesPanel.prototype.onMouseWheel=function(e,delta){
	var den=e.shiftKey ? 1 : 10;
	if(e.altKey){
      if(this.hscrollVisible){
	    this.hscroll.goPage(-delta/den);
      }
    }else
	  this.vscroll.goPage(-delta/den);
}


TilesPanel.prototype.onScroll=function(){
  var v=Math.round(this.vscroll.getClipTop())/(this.tileHeight+this.tilePadding);
  var top=fl(v);
  this.tilesElement.style.top=toPx(fl((fl(v+1)-v-1)*(this.tileHeight+this.tilePadding)));
  var clipTop=top*this.columnsCount;
  var clipBottom=clipTop+this.tilesVisible;
  this.setClipzone(clipTop,clipBottom);
  this.repaint();
}
TilesPanel.prototype.setClipzone=function(clipTop,clipBottom){

  if(isNaN(clipTop) || (this.clipTop===clipTop && this.clipBottom===clipBottom))//no change?
	  return;
  else if(clipTop>this.clipBottom || clipBottom < this.clipTop){//clip zone completely new (no overlap w/ existing) ?
	  for(var i in this.tileDivsPool){
		var tile=this.tileDivsPool[i];
		if(tile!=null)
		  this.tilesElement.removeChild(tile);
	  }
	  this.tileDivsPool={};
  }else{ // clip zone moved, but there is some overlap
	  for(var i=this.clipTop;i<clipTop;i++){
		var tile=this.tileDivsPool[i];
		if(tile!=null){
		  delete this.tileDivsPool[i];
		  this.tilesElement.removeChild(tile);
		}
	  }
	  for(var i=clipBottom;i<this.clipBottom;i++){
		var tile=this.tileDivsPool[i];
		if(tile!=null){
		  delete this.tileDivsPool[i];
		  this.tilesElement.removeChild(tile);
		}
	  }
  }
  this.clipTop=clipTop;
  this.clipBottom=clipBottom;
  if(this.onClipzoneChanged)
    this.onClipzoneChanged(this.clipTop,this.clipBottom);
}
TilesPanel.prototype.showContextMenu=function(menu){
  this.createMenu(menu).show(this.contextMenuPoint);
}
TilesPanel.prototype.createMenu=function(menu){
   var that=this;
   var r=new Menu(getWindow(this.containerElement));
   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e,id);} );
   return r;
}