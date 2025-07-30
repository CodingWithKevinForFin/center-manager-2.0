

function FastText(element){
  var that=this;
  this.ignoreOnScroll=true;
  this.containerElement=element;
  this.textElement=nw("div");
  this.textElement.className='text';
  this.scrollPaneDiv=nw('div');
  this.scrollPane=new ScrollPane(this.scrollPaneDiv,15,this.textElement);
  this.scrollPane.onScroll=function(){that.onScroll()};
  this.rowHeight=15;
  this.upperRow=0;
  this.bottomRow=0;
  this.containerElement.appendChild(this.scrollPaneDiv);
  this.data={};
  this.clipPadding=100;
  this.cells=[];
  this.labls=[];
  this.labelWidthPx=toPx(75);
  this.isInit=false;
  this.selectedRows=new SelectedRows();
  this.selectedRows.add(4,9);
  this.selectedRows.add(15,22);
  this.onCellMouseDownFunc=function(e){e.stopPropagation();return that.onCellMouseDown(e);};
  this.onCellMouseUpFunc=function(e){return that.onCellMouseUp(e);};
  this.onLabelMouseDownFunc=function(e){e.stopPropagation();return that.onLabelMouseDown(e);};
  this.scrollPane.onTickClick=function(id,pos){return that.onTickClick(id,pos)};
}
FastText.prototype.getScrollPane=function(){
  return this.scrollPane;
}

FastText.prototype.init=function(linesCount,rowHeight,maxChars,labelWidth,labelStyle,style){
	this.labelStyle=labelStyle;
	applyStyle(this.textElement,style);
    this.isInit=false;
    this.selectedRows.clear();
	this.linesCount=linesCount;
	this.rowHeight=rowHeight;
	this.rowHeightPx=toPx(rowHeight);
	this.maxChars=maxChars;
    this.labelWidthPx=toPx(labelWidth);
    this.labelWidth=labelWidth;
	this.scrollPane.setPaneSize(this.labelWidth+maxChars*7,linesCount*rowHeight);
	this.scrollPane.vscroll.setRange(0,this.linesCount-1);
	if(this.width)
		this.setLocation(0,0,this.width,this.height);
    for(var i in this.cells){
    	this.textElement.removeChild(this.cells[i]);
    	this.textElement.removeChild(this.labls[i]);
    }
    this.scrollPane.setClipTop(0);
    this.scrollPane.setClipLeft(0);
    this.data={};
    this.cells=[];
    this.labls=[];
	this.isInit=true;
    this.updateCells();
	this.updateColsVisible();
}



FastText.prototype.setData=function(data){
	for(var i=0;i<data.length;){
		var dat=data[i++];
		var pos=dat.p;
		this.data[pos]=dat;
		if(pos>=this.upperRow && pos<this.bottomRow){
			var offset=pos-this.upperRow;
			var cell=this.cells[offset];
			if(cell!=null){
			  cell.innerHTML=dat.v;
    		  resetAppliedStyles(cell);
			  applyStyle(cell,dat.s);
			  this.labls[offset].innerHTML=dat.l;
			}
	    }
	}
}

FastText.prototype.updateColsVisible=function(x,y,width,height){
  var colsVisible=fl((this.width-20-this.labelWidth)/7);
  if(isNaN(colsVisible))
	  return;
  if(colsVisible!=this.colsVisible){
    this.onColumnsVisible(colsVisible);
    this.colsVisible=colsVisible;
  }
}

FastText.prototype.setLocation=function(x,y,width,height){
    this.width=width;
	this.height=height;
	this.updateColsVisible();
	
    this.ignoreOnScroll=true;
	this.scrollPane.setLocation(x,y,width,height);
    this.ignoreOnScroll=false;
    new Rect(0,0,width,height).writeToElement(this.containerElement);
    new Rect(0,0,Math.max(width,this.maxChars*20),height+this.rowHeight).writeToElement(this.textElement);
    this.onScroll();
    this.updateCells();
    	
}

//FastText.prototype.updateBounds=function(){
    //this.visibleRowsCount=Math.ceil((this.height-this.headerHeight-(this.hscrollVisible ? this.scrollSize : 0))/this.rowHeight);
    //new Rect(0,0,Math.max(this.totalWidth,this.width),this.headerHeight).writeToElement(this.headerElement);
    //new Rect(0,this.headerHeight,max(this.totalWidth,this.width),this.height-(this.hscrollVisible ? this.scrollSize : 0)-this.headerHeight+this.rowHeight*2).writeToElement(this.textElement);
    //this.updateCells();
//}

FastText.prototype.updateCells=function(){
	if(!this.isInit)
		return false;
    for(i=0;i<=this.bottomRow-this.upperRow;i++){
    	var cell=this.cells[i];
    	var labl=this.labls[i];
    	var pos=i+this.upperRow;
    	if(cell==null){
    	  var top=toPx(i*this.rowHeight);
    	  cell=this.cells[i]=nw('div','text_contents');
    	  cell.style.left=this.labelWidthPx;
    	  cell.style.height=this.rowHeightPx;
    	  labl=this.labls[i]=nw('div','text_label');
    	  labl.onclick=this.onLabelMouseDownFunc;
    	  labl.style.left='0px';
    	  labl.style.width=this.labelWidthPx;
    	  labl.style.height=this.rowHeightPx;
    	  cell.style.top=top;
    	  cell.style.width='100%';
    	  cell.onmousedown=this.onCellMouseDownFunc;
    	  labl.style.top=top;
	      applyStyle(labl,this.labelStyle);
    	  this.textElement.appendChild(labl);
    	  this.textElement.appendChild(cell);
    	}
    	cell.pos=pos;
    	var dat=this.data[pos];
    	if(this.selectedRows.isSelected(pos))
    	  cell.className='text_contents text_selected';
    	else
    	  cell.className='text_contents';
    	if(dat!=null){
    		cell.innerHTML=dat.v;
    		resetAppliedStyles(cell);
			applyStyle(cell,dat.s);
    		labl.innerHTML=dat.l;
    	}else {
    		cell.innerHTML='';
    		resetAppliedStyles(cell);
			applyStyle(cell,'');
    		labl.innerHTML='';
    	}
    }
}


FastText.prototype.onScroll=function(){
  if(this.ignoreOnScroll)
    return;
   var v=Math.round(this.scrollPane.getClipTop())/this.rowHeight;
   var rows=Math.round(this.height/this.rowHeight)+1;
   this.textElement.style.top=toPx((Math.floor(v)-v)*this.rowHeight);
   this.textElement.style.left=toPx(-1*(this.scrollPane.getClipLeft()));
   v=Math.floor(v);
   this.upperRow=v;
   this.bottomRow=v+rows;
   this.updateCells();
   this.onClip(v,this.bottomRow);
}



FastText.prototype.onCellMouseDown=function(e){
	var that=this;
    var shiftKey=e.shiftKey;
    var ctrlKey=e.ctrlKey;
	var cell=getMouseTarget(e);
	var row=cell.pos;
	while(row==null){
	  cell=cell.parentNode;
	  if(cell==null)
	  return;
	    row=cell.pos;
	}
	document.onmouseup=this.onCellMouseUpFunc;
	document.onmousemove=function(e){that.onOutsideMouseDragging(e);};
	var button=getMouseButton(e);
	if(button==2){
	  var cell=getMouseTarget(e);
	  var row=cell.pos;
	  while(row==null){
	    cell=cell.parentNode;
	    row=cell.pos;
	  }
	  if(!this.selectedRows.isSelected(row)){
		  this.selectedRows.clear();
		  this.selectRow(row,shiftKey,ctrlKey,false);
	  }
	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);
	  if(this.onUserContextMenu!=null)
		  this.onUserContextMenu(e);
	var col=cell.colLocation;
	}else{
      if(!e.shiftKey  && !e.ctrlKey){//TODO: why does this block exist?
	    this.dragSelect=true;
      }
	  this.selectRow(row,shiftKey,ctrlKey,false);
      //this.dragOutsideTimer = window.setInterval(function(){that.onOutsideDragging();}, 100);
	}
}

FastText.prototype.selectRow=function(row,shiftKey,ctrlKey,isDragging){
	var before=this.selectedRows.toString();
	var beforeCurrentRow=this.currentRow;
	if(!isDragging && this.dragSelect){
		this.dragStart=row;
	}
    if(ctrlKey && shiftKey){
    	if(this.currentRow==-1)
    	  this.selectedRows.remove(row);
    	else if(this.currentRow<row)
    	  this.selectedRows.remove(this.currentRow,row);
    	else
    	  this.selectedRows.remove(row,this.currentRow);
    }else if(ctrlKey){
      if(this.selectedRows.isSelected(row))
  	    this.selectedRows.remove(row);
      else
  	    this.selectedRows.add(row);
    }else if(shiftKey){
    	if(this.currentRow==-1)
    	  this.selectedRows.add(row);
    	else if(this.currentRow<row)
    	  this.selectedRows.add(this.currentRow,row);
    	else
    	  this.selectedRows.add(row,this.currentRow);
    }else if(this.dragSelect){
    	this.selectedRows.clear();
    	if(row>this.dragStart)
    	  this.selectedRows.add(this.dragStart,row);
    	else
    	  this.selectedRows.add(row,this.dragStart);
    }else{
	  this.selectedRows.clear();
  	  this.selectedRows.add(row);
    }
    if(this.selectedRows.isSelected(row))
	  this.currentRow=row;
    else
	  this.currentRow=-1;
	this.updateCells();
	var after=this.selectedRows.toString();
	if(this.onUserSelected!=null && (after!=before || this.currentRow!=beforeCurrentRow))
		this.onUserSelected(this.selectedRows.toString());
	return false;
}

FastText.prototype.onOutsideMouseDragging=function(e){
  var point=getMousePoint(e);
  var r=new Rect();
  r.readFromElement(this.containerElement);
  if(r.getBottom()<point.getY())
	  this.outsideMouseDraggingDelta=(point.getY() - r.getBottom())/50 ;
  else if(r.getTop()>point.getY())
	  this.outsideMouseDraggingDelta=(point.getY() - r.getTop())/50 ;
  else{
	  this.outsideMouseDraggingDelta=0;
	  if(this.dragSelect){
	    var cell=getMouseTarget(e);
	    var row=cell.pos;
	    while(row==null){
	      cell=cell.parentNode;
	      if(cell==null)
	    	  break;
	      row=cell.pos;
	    }
	    if(row!=null)
	  	  this.selectRow(row,false,false,true);
	  }  
  }
	  
}

FastText.prototype.onCellMouseUp=function(e){
	this.stopOutsideDragging();
	this.dragSelect=false;
	document.onmouseup=null;
}

FastText.prototype.stopOutsideDragging=function(){
  if(this.dragOutsideTimer!=null){
    window.clearInterval(this.dragOutsideTimer);
    this.dragOutsideTimer=null;
  }
}

FastText.prototype.setTopLine=function(line){
  this.scrollPane.setClipTop(line*this.rowHeight);
}
FastText.prototype.onTickClick=function(pos){
	this.setTopLine(pos-fl((this.bottomRow-this.upperRow)/2));
}

FastText.prototype.setSelectedLines=function(text){
	this.selectedRows.parseString(text);
	this.updateCells();
}

FastText.prototype.showContextMenu=function(menu){
  this.createMenu(menu).show(this.contextMenuPoint);
}


FastText.prototype.createMenu=function(menu){
   var that=this;
   var r=new Menu(getWindow(this.containerElement));
   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e,id);} );
   return r;
}


FastText.prototype.setScrollTicks=function(data){
	this.scrollPane.clearTicks();
	var rheight=(this.height-45) / this.linesCount;
	var width=Math.floor(15 / data.length);
	for(var i=0;i<data.length;i++){
		var cat=data[i];
		var style=cat.style;
		var ticks=cat.ticks;
		var left=width*i;
		for(var j=0;j<ticks.length;){
			var pos=ticks[j++];
			var len=ticks[j++];
		    this.scrollPane.addTick(pos,left,pos*rheight-15,width,Math.max(2,len*rheight),style);
		}
	}
	
}

