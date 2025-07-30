
function SelectedRows(){
	this.ranges=new Array();
	this.rows=new Array();
//	this.rowsUid = new Set();
}

SelectedRows.prototype.isEmpty=function(){
	return this.ranges.length==0 && this.rows.length==0;
}
SelectedRows.prototype.isSelected=function(rownum){
	if(this.rows[rownum]!=null)
		return true;
//	console.log(this.rows);
	for(var i=0;i<this.ranges.length;i++)
		if(this.ranges[i][0]<=rownum && rownum<=this.ranges[i][1])
			return true;
}

// returns the next selected.
SelectedRows.prototype.getNext=function() {
	if (this.isEmpty()) return -1;
	for (var i=0;i<this.rows.length;i++) {
		if (this.rows[i])
			return i;
	}
	// not empty, but the values are all empty.
	return -1;
}

SelectedRows.prototype.add=function(start,end){
	if(end==null)
		end=start;
	else if(end<start){
	  var t=end;
	  end=start;
	  start=t;
	}
//	console.log("adding index from", start, "to", end);
	this.remove(start,end);
	if(end-start<2){
		while(start<=end) {
			this.rows[start++]=true;
		}
	} else {
	    this.ranges.push([start,end]);
	}
}

SelectedRows.prototype.remove=function(start,end){
	if(end==null)
		end=start;
	if(end-start < 100){
	  for(var i=start;i<=end;i++) {
		  delete this.rows[i];
	  }
	}else{
	  for(var i in this.rows){
	    if(i>end)
	      break;
	    if(i>=start) {
	    	delete this.rows[i];
	    }
	  }
	}
	for(var i=0;i<this.ranges.length;i++){
		var e=this.ranges[i];
		var low=e[0];
		var high=e[1];
		if(high <start || low > end)
			continue;
		if(start<=low && end >=high){// outside
		  this.ranges.splice(i,1);
		  i--;
		}else if(start>low && end<high){// inside
			e[1]=start-1;
		    this.ranges[this.ranges.length]=[end+1,high];
		}else if(start>low){// begins with
			e[1]=start-1;
		}else{// ends with
			e[0]=end+1;
		}
	}
}

SelectedRows.prototype.toString=function(){
	r="";
	for(var i in this.rows){
		if(r.length)
			r+=",";
		r+=i;
	}
	for(var i in this.ranges){
		if(r.length)
			r+=",";
		var b=this.ranges[i][0];
		var e=this.ranges[i][1];
		if(b==e)
		  r+=b;
		else
		  r+=b+'-'+e;
	}
	return r;
}
SelectedRows.prototype.parseString=function(string){
	this.clear();
	if(string==null || string=="")
		return;
	var parts=string.split(",");
	for(var i in parts){
		var startEnd=parts[i].split("-");
		if(startEnd.length==1)
			this.rows[parseInt(startEnd[0])]=true;
		else {
			this.ranges.push([parseInt(startEnd[0]),parseInt(startEnd[1])]);
		}
	}
}


SelectedRows.prototype.clear=function(){
	this.ranges=[];
	this.rows=[];
//	this.rowsUid.clear();
}

function FastTableColumn(id,name,offset,location,width,headerClassName,cellClassName,hasSortButtons,columnHeaderStyle, owner,hasHover){
	var that=this;
	this.owner= owner; //table or tree
	var ownerClassName = owner.constructor.name;
//	console.log(ownerClassName);
	this.id=id;
	this.name=name;
	this.location=location;
	this.width=width;
	this.offset=offset;
	this.headerClassName=headerClassName;
	this.cellClassName=cellClassName;
	this.columnHeaderStyle=columnHeaderStyle;
    this.headerElement=nw("div");
    this.grabElement=nw("div");
    this.grabElement.className="header_grab";
    this.headerElement.innerHTML=name;
    this.headerElement.className="header "+headerClassName;
	applyStyle(this.headerElement,columnHeaderStyle);
    this.invisibleElement=nw("div", "header_invisible_layer");
    this.headerElement.appendChild(this.invisibleElement);
    this.hasHover=hasHover;
    if(hasSortButtons){
      this.buttonsUpElement=nw("div","header_buttons_up");
      this.buttonsDnElement=nw("div","header_buttons_dn");
      this.invisibleElement.appendChild(this.buttonsUpElement);
      this.invisibleElement.appendChild(this.buttonsDnElement);
    }
    //TODOFILTER
	this.quickColumnFilterElement = nw("combo-box","column_quick_filter editComboBox");
	const shadow = this.quickColumnFilterElement.shadowRoot;
    if (ownerClassName == "FastTree") {
    	//MOBILE SUPPORT - support pressing enter to search column filter instead of going to next column filter.
    	const inputElement = shadow.querySelector('input');
    	if (inputElement) {
    		inputElement.setAttribute('enterkeyhint', 'go');
    	}
    	shadow.querySelector('style').textContent = `
			.combo-box{
	    		box-sizing:border-box;
	    		border:none;
	    		width:100%;
	    		height:100%;
	    	}
	    	.combo-box:focus{
	    		outline:none!important;
	    	}
	`;
    } else if (ownerClassName == "FastTable") {
    	//MOBILE SUPPORT - support pressing enter to search column filter instead of going to next column filter.
    	const inputElement = shadow.querySelector('input');
    	if (inputElement) {
    		inputElement.setAttribute('enterkeyhint', 'go');
    	}
    	shadow.querySelector('style').textContent = `
			.combo-box{
	    		box-sizing:border-box;
	    		border:none;
	    		width:100%;
	    		height:100%;
	    		position:absolute;
	    		left:0;
	    	}
	    	.combo-box:focus{
	    		outline:none!important;
	    	}
	`;
    }
	this.quickColumnFilterElement.setMinWidth(125);
    this.headerElement.appendChild(this.quickColumnFilterElement);
    //clear
	//this.clearQuickFilterElement = nw("div", "column_quick_filter_clear");
    //this.headerElement.appendChild(this.clearQuickFilterElement);
	this.quickColumnFilterElement.isQuickColumnFilterElement=true;
	this.quickColumnFilterElement.style.display="none";
	//MOBILE SUPPORT - enables the user to click on the filter.
	this.quickColumnFilterElement.ontouchstart=function(e){
		e.stopPropagation();
	};
	
	var that = this;
	this.quickColumnFilterElement.hideGlass= function(){
		if (ownerClassName == "FastTree")
		{
			kmm.setActivePortletId(that.owner.treePortlet.portletId,false);
		}
		else if (ownerClassName == "FastTable")
		{
			kmm.setActivePortletId(that.owner.portlet.portletId,false);
		}
	};
    this.quickColumnFilterElement.onfocus=function(e){
    	if (that.owner && that.owner.repaint!=null)
    		that.owner.repaint(); //TODO: makes the filter menu appear on the correct table (fix could be better)
    	if(!currentContextMenu)
    		that.quickColumnFilterGetOptions(e, that, that.owner);
    };
	var onkeydown = function(e){
		if(that.quickColumnFilterElement.handleKeydown && that.quickColumnFilterElement.handleKeydown(e)){
	
		}
		if(e.key=="Tab"){
			e.preventDefault();
		}
    };
	this.quickColumnFilterElement.setCustomKeydownHandler(onkeydown);
    var origSelect = this.quickColumnFilterElement.select;
    this.quickColumnFilterElement.select= function(i){
    	origSelect.call(that.quickColumnFilterElement, i);	
   		var col = that;
		col.filterText = col.quickColumnFilterElement.value;
		var value = col.filterText != null?col.filterText:"";
   		that.quickColumnFilterSetFilterValue(null, that, that.owner, value);
    };

	this.quickColumnFilterElement.handleKeydown=function(e){
		var shiftKey = e.shiftKey;
		var ctrlKey = e.ctrlKey;
		var altKey = e.altKey;
		if(shiftKey == true || ctrlKey == true || altKey == true || e.key=="Tab")
			return false;
		
		if(currentContextMenu){
			currentContextMenu.handleKeydown(e);
			return true;
		}
		else 
			return false;
		
	};
    // This is for getting suggestions when deleting values
    this.quickColumnFilterElement.onkeyup=function(e){
    	if(e.key.length == 1 || e.key == "Backspace" || e.key == "Delete")
			that.quickColumnFilterGetOptions(e, that, that.owner);
		else if(e.key=="Enter") {
    		var col = that;
    		col.filterText = col.quickColumnFilterElement.value;
			var value = col.filterText != null?col.filterText:"";
    		that.quickColumnFilterSetFilterValue(e, that, that.owner, value);
    	}
     	else if(e.key=="Escape"){
    		var col = that;
			col.filterText = "";
			var value = col.filterText;
    		that.quickColumnFilterSetFilterValue(e, that, that.owner, value);
    	}
		
    };
}
FastTableColumn.prototype.name;
FastTableColumn.prototype.offset;
FastTableColumn.prototype.width;
FastTableColumn.prototype.headerClassName;
FastTableColumn.prototype.cellClassName;
FastTableColumn.prototype.offset;
FastTableColumn.prototype.location;
FastTableColumn.prototype.headerElement;
FastTableColumn.prototype.grabElement;
FastTableColumn.prototype.quickColumnFilterElement;
FastTableColumn.prototype.filter;
FastTableColumn.prototype.filterText;
FastTableColumn.prototype.hasHover;

FastTableColumn.prototype.setHIDS=function(hids){
	this.headerElement.id=hids;
}
FastTableColumn.prototype.getRightOffset=function(){
	return this.offset+this.width;
}

FastTableColumn.prototype.updateQuickFilterValue=function(){
	if(this.filter==true){
		this.quickColumnFilterElement.setValue(this.filterText);
	}
	else
		this.quickColumnFilterElement.setValue("");
}

FastTableColumn.prototype.quickColumnFilterSetFilterValue=function(e, col, that, value){
 	that.callback('columnFilter',{pos:col.location,val:value});
 	col.quickColumnFilterElement.blur();
}

FastTableColumn.prototype.quickColumnFilterGetOptions=function(e, col, that){
//	console.log("quickColumnFilterGetOptions called");
	var value = col.quickColumnFilterElement.value != null?col.quickColumnFilterElement.value:"";
 	that.callback('getColumnFilterOptions',{__CONFLATE:col.location,pos:col.location,val:value});
}

FastTable.prototype.setAutocomplete=function(colPos,mapOfValuesOrNull){
	var col = this.columns[colPos];
	var oldVal = col.quickColumnFilterElement.value;
	col.quickColumnFilterElement.clearOptions2();
	for (let key in mapOfValuesOrNull) {
		col.quickColumnFilterElement.addOptionDisplayAction(key, mapOfValuesOrNull[key]);
	}
	//col.quickColumnFilterElement.setValue(oldVal);
	col.quickColumnFilterElement.autocomplete(col.filterText);
	
};

//MOBILE SUPPORT - FOR DRAG SELECT
FastTable.prototype.onTouchDragStart=function(e){
	this.currPoint = getMousePoint(e);
	
	this.longTouchTimer = setTimeout(() => {
		this.isSelecting = true;
		this.hideGlass();
		var t = this.currPoint;
		var t1 = document.elementFromPoint(t.x, t.y).rowLocation;
		if(t1 == null)
			return;
		this.selectRow(e,t1,false,false,false);
  }, 500); // Long touch threshold (500ms)

}

//MOBILE SUPPORT - FOR DRAG SELECT
FastTable.prototype.onTouchDragMove=function(e){
	//drag seelct.
	if(this.isSelecting === true){
		this.hideGlass();
		var t = getMousePoint(e);
		var t1 = document.elementFromPoint(t.x, t.y).rowLocation;
		if(t1 == null)
			return;
		this.dragSelect = true;
		this.selectRow(e,t1,false,false,true);
	}
	// drag scroll
	else{
		clearTimeout(this.longTouchTimer);
		this.longTouchTimer=null;
		var that = this;
		if(e.target != that.disabledGlassDiv){
			var newRef = that.disabledGlassDiv;
			that.target = newRef[0];
		}
		
		var diffx = that.currPoint.x - getMousePoint(e).x;
		var diffy = that.currPoint.y - getMousePoint(e).y;
		that.scrollPane.hscroll.goPage(0.01 * diffx);
		that.scrollPane.vscroll.goPage(0.01 * diffy);
		that.currPoint = getMousePoint(e);
		
	}
}

function FastTable(element){
  var that=this;
  this.cellElements=new Object2d();
	// store previous values to check if we need to flash
  this.valueElements=new Object2d();
  this.data=new Object2d(); // TODO: is this getting used?
  this.containerElement=element;
  this.tableElement=nw("div");
  this.pinnedTable=nw("div","__pinnedContainer");
  this.scrollPaneElement=nw("div");
  this.tableElement.className='table';
  this.tableElement.tabIndex=0;
  this.headerElement=nw("div");
  this.pinnedBorderElement=nw("div");
  this.selectedRowsElement=nw("div");
  this.pinnedBorderWidth = 5;
  this.pinnedBorderElement.style.display="none";
  this.pinnedBorderElement.style.zIndex = 3;
  this.pinnedBorderElement.style.pointerEvents = "none";
  this.pinnedBorderElement.style.width = toPx(this.pinnedBorderWidth);
  this.pinnedBorderElement.style.borderWidth = "0px 2px 0px 2px";
  this.pinnedBorderElement.style.borderStyle = "inset";
  this.pinnedBorderElement.style.borderColor = "#999999";
  this.pinnedBorderElement.style.background = "#b3b3b3";
  this.columns=new Array();
  this.scrollSize=15;
  this.containerElement.appendChild(this.scrollPaneElement);
  this.containerElement.appendChild(this.headerElement);
  this.containerElement.appendChild(this.pinnedBorderElement);	
  this.tableElement.appendChild(this.selectedRowsElement);
  this.tooltipPadL=6;
  this.tooltipPadT=4;
  
  this.selectedRowsElement.style.pointerEvents="none";
  this.selectedRowsElement.style.zIndex=3;
  this.scrollPane=new ScrollPane(this.scrollPaneElement,this.scrollSize,this.tableElement);
  this.scrollPane.onScroll=function(){that.onScroll()};
  this.scrollPaneElement.appendChild(this.pinnedTable);
  // below has no effect
  this.pinnedTable.onMouseWheel=function(e,delta){
	  that.scrollPane.onMouseWheel(e,delta);
  };
  
  // options
  this.selectedClassname='cell_selected_default';
  this.activeClassname='cell_active_default';
  this.useGreybars=true;
  this.rowHeight=18;
  this.quickColumnFilterHeight = this.rowHeight+10;
  this.headerHeight = this.rowHeight;
  this.headerFontSize = 13;
  this.greyBarColor=null;
  this.bgStyle=null;
  this.defaultFontColor=null;
  this.searchBarColor=null;
  this.filteredColumnBgColor=null;
  this.filteredColumnFontColor=null;
  
  this.totalWidth=0;
  this.totalPinnedWidth=0;
  this.totalHeight=this.headerHeight;
  this.selectedRows=new SelectedRows();
  this.headerElement.className="header";
  this.currentRow=0;
  this.onCellMouseOverFunc=function(e){return that.onCellMouseOver(e);};
  this.onCellMouseMoveFunc=function(e){return that.onCellMouseMove(e);};
  this.onCellMouseOutFunc=function(e){return that.onCellMouseOut(e);};
  this.onCellMouseDownFunc=function(e){e.stopPropagation();return that.onCellMouseDown(e);};
  this.onCellMouseUpFunc=function(e){that.hideGlass();return that.onCellMouseUp(e);};
  
  this.tableElement.onmouseleave=function() { 
	  this.expectTooltip=false;
	  that.onMouseLeave();
  };
  this.tableElement.onmousemove=function(e) { that.onMouseMove(e);};
  this.tableElement.onmousedown=this.onCellMouseDownFunc;
  this.tableElement.onmousedown=function(e){e.stopPropagation();return that.onWhiteSpaceMouseDown(e);};
//below has no effect
  this.scrollPaneElement.onmousedown=function(e){
	  e.stopPropagation();
	  return that.onWhiteSpaceMouseDown(e);
  };
  this.headerElement.onclick=function(e){if(e.button==0)that.onHeaderClicked(e,null);};
  this.pinning=0;
  this.userSelectSeqnum=0;
  this.userScrollSeqnum=0;
  this.widthChanged = false;
  this.heightChanged = false;
  this.quickColumnFilterHidden = true;//quickfilter is displayed
  
  //MOBILE SUPPORT - FOR DRAG SCROLL
  this.longTouchTimer;
  this.isSelecting = false;
  this.isDragging = false;
  this.currPoint = new Point(0,0);
  this.tableElement.ontouchstart=function(e) { that.onTouchDragStart(e); that.createGlass();};
  this.tableElement.ontouchmove=function(e) { that.onTouchDragMove(e); that.onScroll();};
  this.tableElement.ontouchend= function(e){ that.isSelecting=false; clearTimeout(that.longTouchTimer); that.longTouchTimer=null; that.hideGlass();};
}



// elements
FastTable.prototype.tableElement;
FastTable.prototype.columns;
FastTable.prototype.hiddenColumns;
FastTable.prototype.headerGrabElements;
FastTable.prototype.cellElements;
FastTable.prototype.contextMenuCurrentColumn;
FastTable.prototype.useGreybars;
FastTable.prototype.greyBarColor;
FastTable.prototype.defaultFontColor;
FastTable.prototype.searchBarColor;
FastTable.prototype.menuBarColor;
FastTable.prototype.filteredColumnBgColor;
FastTable.prototype.filteredColumnFontColor;
FastTable.prototype.quickColumnFilterHidden;
FastTable.prototype.quickColumnBackgroundColor;
FastTable.prototype.quickColumnFontColor;
FastTable.prototype.quickColumnFontSz;
FastTable.prototype.quickColumnBorderColor;



// Header
FastTable.prototype.headerHeight=20;


// cell sizes
FastTable.prototype.rowHeight=18;
FastTable.prototype.quickColumnFilterHeight;
FastTable.prototype.cornerSize=15;

// table size
FastTable.prototype.height=200;
FastTable.prototype.width=1000;

// upper left corner
FastTable.prototype.upperRow=0;
FastTable.prototype.lowerRow=0;
FastTable.prototype.visibleRowsCount=0;

FastTable.prototype.data;
FastTable.prototype.totalWidth;
FastTable.prototype.selectedRows;
FastTable.prototype.totalRows=0;

FastTable.prototype.onMouseMove=function(point){
  var cell=getMouseTarget(point);
  if (cell != null && this.onHover) {
	var row=cell.rowLocation;
    while(row==null){
	  cell=cell.parentNode;
	  if(cell==null) {
        this.clearHover();
		return;
      }
	  row=cell.rowLocation;
	}  
	this.currentMousePoint=point;
//	this.onHover(cell.colLocation, cell.rowLocation);
  } else {
	this.clearHover();
  }
}

FastTable.prototype.onMouseLeave=function(){
  this.clearHover();
}

FastTable.prototype.setHover=function(x,y,value,xAlign,yAlign){
	if (this.expectTooltip === false)
		return;
  var cell = this.cellElements.get(x,y);
  const rootBody = getRootNodeBody(this.tableElement);
  if(this.tooltipDiv!=null || cell == undefined)
	  this.clearHover();
  this.tooltipDiv=nw("div","ami_table_tooltip");
  var div=this.tooltipDiv;
  this.hoverX=this.currentMousePoint.x;
  this.hoverY=this.currentMousePoint.y;
  div.innerHTML=value;
  if(div.firstChild!=null && div.firstChild.tagName=='DIV'){
    this.tooltipDiv=div.firstChild;
    div=this.tooltipDiv;
  }
  rootBody.appendChild(div);
  var rect=new Rect().readFromElement(div);
  // add padding to size
  var h=rect.height+this.tooltipPadL;
  var w=rect.width+6+this.tooltipPadT;
  div.style.width=toPx(w);
  div.style.height=toPx(h);
	// TODO i don't think we need to use a switch here.... we always pass in the same value
  switch(xAlign){
    case ALIGN_LEFT: div.style.left=toPx(this.hoverX+x); break;
    case ALIGN_RIGHT: div.style.left=toPx(this.hoverX-w-this.tooltipPadL); break;
    default: div.style.left=toPx(this.hoverX-w/2); break;
  }
  switch(yAlign){
    case ALIGN_TOP: div.style.top=toPx(this.hoverY); break;
    case ALIGN_BOTTOM: div.style.top=toPx(this.hoverY-h-this.tooltipPadT); break;
    default: div.style.top=toPx(this.hoverY-h/2); break;
  }
  ensureInDiv(div,rootBody);
}

FastTable.prototype.clearHover=function(){
  if(this.tooltipDiv!=null){
    try {
		getDocument(this.tableElement).body.removeChild(this.tooltipDiv);
    } catch (e) {
    	console.log("unable to clear table hover: " + e);
    }
    this.tooltipDiv=null;
  }
}

FastTable.prototype.onOutsideMouseDragging=function(e){
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
	    var row=cell.rowLocation;
	    while(row==null){
	      cell=cell.parentNode;
	      if(cell==null)
	    	  break;
	      row=cell.rowLocation;
	    }
	    if(row!=null){
	  	  this.selectRow(e,row,false,false,true);
	   }
	  }  
  }
	  
}
FastTable.prototype.onOutsideDragging=function(e){
	if(this.outsideMouseDraggingDelta){
	   if(this.outsideMouseDraggingDelta>0){
	  	  this.selectRow(e,this.getUpperRowVisible(),false,false,true);
	   }else{
	  	  this.selectRow(e,this.getLowerRowVisible(),false,false,true);
	   }
	}
}

FastTable.prototype.stopOutsideDragging=function(){
  if(this.dragOutsideTimer!=null){
    window.clearInterval(this.dragOutsideTimer);
    this.dragOutsideTimer=null;
  }
}


FastTable.prototype.addColumn=function(id,name,width,headerClassName,cellClassName,headerStyle,location,jsFormatterType,filter,filterText,isFixed,hasHover){
   if(location == null)
     location=this.columns.length;
   var offset=location==0 ? 0: this.columns[location-1].getRightOffset();
   var that=this;
   var column=new FastTableColumn(id,name,offset,location,width,headerClassName,cellClassName!=null ? cellClassName : "",true,headerStyle, that,hasHover);
//   console.log("id: ",id,"name: ", name, "offset: ", offset, "location: ", location, "width: ", width, "headerClassName: ", headerClassName, "cellClassName: ", cellClassName); 
//   console.log(column);
   column.headerElement.location=location;
   makePushDraggable(this.headerElement, column.headerElement, column.invisibleElement, true, false, null, 
   		function(a,b,c){that.onDragColumnEndCallback(a,b,c);}, 
   		function(a,b,c,d,e,f){return that.onDragMousePosition(a,b,c,d,e,f);}, 
   false);


   column.invisibleElement.location=location;// TODO this is wrong if not adding
											// to the end (following columns
											// need to be updated!)
   column.invisibleElement.onmouseup=function(e){if(e.button == 0) that.onHeaderClicked(e,column); else that.onFilter(column.location);};
   column.buttonsUpElement.onmousedown=function(e){that.onHeaderButtonClicked(e,'__sort_3',column.location);};
   column.buttonsDnElement.onmousedown=function(e){that.onHeaderButtonClicked(e,'__sort_2',column.location);};
   column.headerElement.style.borderColor=this.cellBorderColor;
   column.grabElement.style.opacity = "0";   
   column.grabElement.location=location;
   column.grabElement.onclick=function(e){e.stopPropagation();};
   column.grabElement.ondragging=function(e,x,y){that.onHeaderDragging(e,x,y,column);};
   column.grabElement.ondraggingEnd=function(e,x,y){that.hideGrabElement(that,e.location);};
   column.grabElement.clipDragging=function(e,rect){that.clipHeaderDragging(e,rect);};
   column.grabElement.ondblclick=function(e){that.dblClickGrabElement(e,that, location);};
   column.grabElement.style.borderColor=this.cellBorderColor;
   column.jsFormatterType=jsFormatterType;
   if(this.hideHeaderDivider=="true")
	   column.headerElement.style.boxShadow="none";
//     column.headerElement.style.backgroundImage="none";
   column.grabElement.onmouseenter=function(e){if(e.buttons==0)that.showGrabElement(that, location);};
   column.grabElement.onmouseleave=function(e){if(e.buttons==0)that.hideGrabElement(that, location);};
   makeDraggable(column.grabElement,column.grabElement,false,true);
//   makeDraggable2(null, column.grabElement,column.grabElement,true,false);
   this.headerElement.appendChild(column.headerElement);
   this.headerElement.appendChild(column.grabElement);
   this.columns.splice(location,0,column);
//   column.width=width;
   //TODOFILTER
   column.filter = filter;
   column.filterText = filterText;
   column.updateQuickFilterValue();
   if (isFixed && !amiEditDesktopArgs.edit) {
	   column.grabElement.style.display='none'; // hide grabber
   }
   applyStyle(column.headerElement,column.columnHeaderStyle);
   return column;
}

FastTable.prototype.fixColumns=function(m){
	for (var i=0; i < m.length; i++) {
		var ref = m[i];
		var column = this.findColumnById(ref.id);
		if (!column)
			continue; // mismatch between backend and frontend... should never happen
		if (amiEditDesktopArgs.edit) {
			// can interact in dev mode, similar to locked divider
		   column.grabElement.style.display='revert';
		   continue;
		}
	   column.grabElement.style.display=ref.fix ? 'none':'revert';
	}
}

FastTable.prototype.findColumnById=function(id){
	if (this.columns.length == 0)
		return;
	for (var i =0; i < this.columns.length; i++) {
		if (this.columns[i].id === id)
			return this.columns[i];
	}
	return null;
}

FastTable.prototype.onDragColumnEndCallback=function(oldIndex,newIndex,success){
    //err(oldIndex+"==>"+newIndex);
	if(oldIndex!=newIndex)
		this.callback('moveColumn',{oldPos:oldIndex,newPos:newIndex  });
}
FastTable.prototype.onDragMousePosition=function(mouseX, containerOffset, mouseOffset, elementWidth, moveX, moveY){
	if(moveY)
		return;
	
	var leftEdge = this.scrollPane.getClipLeft();
	var rightEdge = leftEdge + this.scrollPane.getClipWidth();
	var draggedLeftEdge = (mouseX - containerOffset - mouseOffset);
	var draggedRightEdge = (mouseX - containerOffset - mouseOffset + elementWidth);
	/*
	arguments[6] = rightEdge;
	arguments[7] = draggedRightEdge;
	arguments[8] = draggedRightEdge > rightEdge;
	//arguments[8] = leftEdge;
	//arguments[9] = draggedLeftEdge;
	*/
	if(draggedLeftEdge < leftEdge){
		var np = leftEdge + this.scrollPane.getClipWidth() * -0.05;
		this.scrollPane.setClipLeft(np);
	}
	else if(draggedRightEdge > rightEdge){
		var np = leftEdge + this.scrollPane.getClipWidth() * 0.05;
		this.scrollPane.setClipLeft(np);
	}

	var moved = leftEdge != this.scrollPane.getClipLeft();
	return moved;
	
}

FastTable.prototype.dblClickGrabElement=function(e, that, col){
	that.callback('headerMenuitem',{action:"__autosize",col:col});
}
FastTable.prototype.showGrabElement=function(that, location){
	var col = that.columns[location].grabElement;
	col.style.opacity=1;
//	col.style.zIndex=location<that.pinning ? '2' : '1';
}
FastTable.prototype.hideGrabElement=function(that, location){
	var col = that.columns[location].grabElement;
	col.style.opacity=0;
	if (this.columns[location].jsFormatterType && this.columns[location].jsFormatterType === "spark_line")
		that.callback('headerMenuitem',{action:"__draggingEnd",col:location});
//	col.style.zIndex=location<that.pinning ? '1' : '0';
}
FastTable.prototype.onHeaderButtonClicked=function(e,id,col){
   this.onUserHeaderMenuItem(e,id,col);
}
FastTable.prototype.onHeaderClicked=function(e,column){
//	e.stopPropagation();
   var target=getMouseTarget(e);
   if(this.menu!=null)
	   this.menu.hide();
   if(target==this.headerElement){
     this.contextMenuCurrentColumn=-2;
     var point = new Rect().readFromElement(target).getLowerLeft();
     point.x=getMousePoint(e).x-4;
     this.contextMenuPoint=point;
   }else{
     this.contextMenuCurrentColumn=target.location;
     var point = new Rect().readFromElement(target).getLowerLeft();
     var mousePosX = this.portlet.owningWindow.MOUSE_POSITION_X;
     var colIndex = this.getColumnAtPoint(mousePosX);
     if (this.isColumnOverflownLeft(colIndex))
    	 point.x = mousePosX;
     this.contextMenuPoint=point;
   }
   if(this.onUserHeaderMenu!=null && this.contextMenuCurrentColumn!=null) {
   	  this.curseqnum=nextSendSeqnum;
      this.onUserHeaderMenu(e,this.contextMenuCurrentColumn);
   }
}
FastTable.prototype.isColumnOverflownLeft=function(colIndex) {
	var columnOffset = this.columns[colIndex].offset;
	var clipLeft = this.scrollPane.getClipLeft();
	
	return clipLeft > columnOffset; 
}
FastTable.prototype.onCellMouseUp=function(e){
	this.stopOutsideDragging();
	this.dragSelect=false;
    var doc=getDocument(this.containerElement).onmouseup=null;
}
FastTable.prototype.onWhiteSpaceMouseDown=function(e){
	var that=this;
	if(e.target != this.tableElement)
		return;
    if(e.shiftKey  || e.ctrlKey)
      return;
	var button=getMouseButton(e);
	//mobile scroll stuff
	var x = getMousePoint(e).x;
	var y = getMousePoint(e).y;
	if(button==2){
	  this.clearSelected();
	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);
      this.contextMenuCurrentColumn=-1;
	  if(this.onUserContextMenu!=null)
		  this.onUserContextMenu(e);
	}else{
	  this.clearSelected();
	}
}

FastTable.prototype.scroll=function(e, r){
	var loc = this.currentRow;
	if(loc!=null){
		var selRow = loc+r;
		if (selRow > this.data.getHeight()-1 || selRow < 0) {
			return;
		}
//		selRow=between(selRow,0,this.data.getHeight()-1);
		this.ensureRowVisible(selRow);
		this.selectRow(e,selRow,e.shiftKey, e.ctrlKey,false);
	}
}
FastTable.prototype.selectAll=function(){
	if(this.data.height > 0){
		this.selectedRows.add(0,this.data.height-1);
		this.flagSelectionChanged = true;
		this.repaint();
		this.fireUserSelected(null,this.currentRow,this.selectedRows.toString());
	}
}
FastTable.prototype.clearSelected=function(){
	this.selectedRows.clear();
	this.currentRow = -1;
	this.flagSelectionChanged = true;
	this.repaint();
	this.fireUserSelected(null, null, this.selectedRows.toString());
}

FastTable.prototype.openCopyMenu=function(e){
	if(isMouseInside({pageX:this.portlet.owningWindow.MOUSE_POSITION_X, pageY:this.portlet.owningWindow.MOUSE_POSITION_Y}, this.scrollPaneElement, 0)){
		var col = this.getColumnAtPoint(this.portlet.owningWindow.MOUSE_POSITION_X);
		this.callback('copyRows',{e:e,col:col})
	}
}
FastTable.prototype.jumpToColWithText=function(e){
	if(e.target.isQuickColumnFilterElement==true)
		return;
	var ch = e.key; 
	if(ch != "Enter"){
		var now = Date.now();
		if(!this.lastUserNavigateCharTime || (now - this.lastUserNavigateCharTime > 2000)){
			this.lastText = "";
		}
		if(ch){
			this.lastUserNavigateCharTime = now;
			this.lastText += ch;
			var loc = this.currentRow;
			this.onUserNavigate(e, loc, "text", this.lastText);
		}
	}
}
FastTable.prototype.nextColWithText=function(e){
	this.lastUserNavigateCharTime = 0;
	var loc = this.currentRow;
	this.onUserNavigate(e, loc, "retext", this.lastText);
}

FastTable.prototype.clearLastText=function(){
	this.lastText = "";
	this.lastUserNavigateCharTime = null;
}

FastTable.prototype.jumpToNextUniqueCol=function(e){
	if(!this.lastText || this.lastText.trim() == ""){
		var loc = this.currentRow;
		this.onUserNavigate(e, loc, e.shiftKey ? "up": "dn");
		this.clearLastText();
	}
}

FastTable.prototype.getColumnAtPoint = function(x){
    for(var i=0;i<this.columns.length;i++){
        var col=new Rect().readFromElementRelatedToWindow(this.columns[i].headerElement);
        if(col.getLeft()<=x && col.getRight()>=x)
      	  return i;
      }
      return -1;
}
FastTable.prototype.ensureRowVisible=function(row){
  row=between(row,0,this.data.getHeight()-1);
  var rowTop = this.rowHeight * row;
  var rowBot = this.rowHeight * (row+1);
  var pageTop = this.scrollPane.getClipTop();
  var pageHeight = this.scrollPane.getClipHeight();
	if (this.scrollPane.hscrollVisible) {
		// page is smaller if we have horizontal scrollbar
		pageHeight -= this.scrollSize;
	}
  
  var pos = null;
  // Ensure the bottom of the row is above the bottom of the page
  if(pageTop + pageHeight < rowBot){ 
	  pos = rowBot - pageHeight;
  }
  // Ensure the top of the row is below the top of the page
  if(pageTop > rowTop) {
	  pos = rowTop;
  }
  if(pos != null)
	  this.scrollPane.setClipTop(pos);
}
// Align 0 - top, 1 - bottom
FastTable.prototype.ensureRowVisibleWithAlign=function(row, align){
	row=between(row,0,this.data.getHeight()-1);
	if (align == 0) {
		var rowTop = this.rowHeight * row;
		this.scrollPane.setClipTop(rowTop);
	}
	else if(align == 1){
		var pageHeight = this.scrollPane.getClipHeight();
		var rowBot = this.rowHeight * (row+1);
		this.scrollPane.setClipTop(rowBot - pageHeight);
	}
}

FastTable.prototype.onCellMouseOver=function(e){
	var cell=getMouseTarget(e);
	this.onCellMouseMove(e);
	// we are in a new cell, let's request a new hover
	// TODO tooltip is a column setting, it feels like we should call this onColumnOut/In...
	this.currentMousePoint=e;
	if(this.columns[cell.colLocation].hasHover) {
	  this.onHover(cell.colLocation, cell.rowLocation);
	  this.expectTooltip=true;
	}
}
FastTable.prototype.onCellMouseOut=function(e){
	var cell=getMouseTarget(e);
	var row=cell.rowLocation;
	this.expectTooltip=false;
	// out of a cell, let's delete the old hover
	// TODO tooltip is a column setting, it feels like we should clear onColumnOut/In...
	this.clearHover();
	while(row==null){
		cell=cell.parentNode;
		if(cell==null) {
			return;
		}
		row=cell.rowLocation;
	}
	var col=cell.colLocation;
	
	if(this.columns[col] !=null){
	if(this.columns[col].clickable){
    	  cell.style.cursor=null;
          var top=this.getUpperRowVisible();
	      var bot=this.getLowerRowVisible();
	      while(top<=bot){
              var cell=this.cellElements.get(col,top);
              if(cell!=null){
    	        cell.style.textDecoration=null;
    	        cell.style.cursor=null;
              }
	    	top++;
	      }
	}
	}
}
FastTable.prototype.onCellMouseMove=function(e){
    var shiftKey=e.shiftKey;
    var ctrlKey=e.ctrlKey;
    
	var cell=getMouseTarget(e);
	var row=cell.rowLocation;
	if (this.tooltipDiv != null) {
		// update tooltip location
	  const rootBody = getRootNodeBody(this.tableElement);
	  var div=this.tooltipDiv;
	  var rect=new Rect().readFromElement(div);
	  var h=rect.height;
	  var w=rect.width;
	  var hoverX = e.x;
	  var hoverY = e.y;
	  // using ALIGN_RIGHT/ALIGN_BOTTOM (constant)
		div.style.left=toPx(hoverX-w-this.tooltipPadL);
		div.style.top=toPx(hoverY-h-this.tooltipPadT);
	  ensureInDiv(div,rootBody);
	}
	while(row==null){
		cell=cell.parentNode;
		if(cell==null)
			return;
		row=cell.rowLocation;
	}
	var col=cell.colLocation;
	
	if(this.columns[col] !=null){
	if(this.columns[col].clickable && !shiftKey && !ctrlKey && e.buttons==0 && this.selectedRows.isSelected(row)){
      var top=this.getUpperRowVisible();
	  var bot=this.getLowerRowVisible();
	  while(top<=bot){
        var cell=this.cellElements.get(col,top);
        if(cell!=null){
	      if(this.selectedRows.isSelected(top)){
    	    cell.style.textDecoration='underline';
    	    cell.style.cursor='pointer';
	      }else{
    	    cell.style.textDecoration=null;
    	    cell.style.cursor=null;
	      }
        }
	    top++;
	  }
	}
	}
}
FastTable.prototype.onCellMouseDown=function(e){
	var that=this;
    var shiftKey=e.shiftKey;
    var ctrlKey=e.ctrlKey;
    
	var cell=getMouseTarget(e);
	var row=cell.rowLocation;
	while(row==null){
		cell=cell.parentNode;
		if(cell==null)
			return;
		row=cell.rowLocation;
	}
	var col=cell.colLocation;
	
    var doc=getDocument(this.containerElement);
	doc.onmouseup=this.onCellMouseUpFunc;
	doc.onmousemove=function(e){that.onOutsideMouseDragging(e);};
	var button=getMouseButton(e);
	if(button==2){
		//redundant
	  if(!this.selectedRows.isSelected(row)){
		  this.selectedRows.clear();
		  this.selectRow(e,row,shiftKey,ctrlKey,false);
	  }
	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);
      this.contextMenuCurrentColumn=-1;
	  if(this.onUserContextMenu!=null){
	      this.curseqnum=nextSendSeqnum;
		  this.onUserContextMenu(e);
	  }
	}else{
	  if(!ctrlKey && !shiftKey && this.columns[col] != null){
		  // the first time you click on a cell, clickable is undefined because the selection has not yet been registered.
		  // clickable will be true the second time you select the same cell
	    var clickable=(this.columns[col].clickable && this.selectedRows.isSelected(row));
	    var oneClick=this.columns[col].oneClick;
		this.callback("cellClicked",{columnIndex:col,rowIndex:row,clickable:clickable,oneClick:oneClick});
		if(clickable)
		  return;
	  } 
      if(!e.shiftKey  && !e.ctrlKey){// TODO: why does this block exist?
	    this.dragSelect=true;
      }
	  this.selectRow(e,row,shiftKey,ctrlKey,false);
	}
}

FastTable.prototype.selectRow=function(e,row,shiftKey,ctrlKey,isDragging){
	var before=this.selectedRows.toString();
	var beforeCurrentRow=this.currentRow;
	if(!isDragging && this.dragSelect){
		this.dragStart=row;
	}
    if(ctrlKey && shiftKey){
    	if(this.currentRow==-1){
    		this.selectedRows.remove(row);
    	}
    	else if(this.currentRow<row){
    		this.selectedRows.remove(this.currentRow,row);
   		}
    	else{
    		this.selectedRows.remove(row,this.currentRow);
    	}
    }else if(ctrlKey){
    	var sel = this.selectedRows.isSelected(row);
    	if (getMouseButton(e) == 1 && sel) {
			this.selectedRows.remove(row);
    	} else if (sel && beforeCurrentRow != row) {
    		this.selectedRows.remove(beforeCurrentRow);
    	} else
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
    	if(row>this.dragStart){
    		this.selectedRows.add(this.dragStart,row);
    	}
    	else{
    		this.selectedRows.add(row,this.dragStart);
    	}
    }else{
    	this.selectedRows.clear();
  		this.selectedRows.add(row);
    }
    
    // ensure something is selected after deselect from ctrl + click
    if (this.selectedRows.isSelected(row)) {
		this.currentRow = row;
	} else if (this.selectedRows.isSelected(beforeCurrentRow)) {
		this.currentRow = beforeCurrentRow;
	} else if (!this.selectedRows.isEmpty()) {
		this.currentRow = this.selectedRows.getNext();
	} else
		this.currentRow = -1;
    
    this.flagRowsRepaint = true;
    this.repaint(false);
    
	var after=this.selectedRows.toString();
	if(after!=before || this.currentRow!=beforeCurrentRow)
		this.fireUserSelected(null,this.currentRow,this.selectedRows.toString());
	return false;
}

FastTable.prototype.fireUserSelected=function(e,activeRow,selectedRows){
	this.userSelectSeqnum++;
	this.onUserSelected(e,activeRow,selectedRows,this.userSelectSeqnum);
}
  
//called strickly by backend
FastTable.prototype.setActiveRow=function(activeRow,seqnum){
	if(seqnum<this.userSelectSeqnum)
		return;
	this.currentRow=activeRow;
}

//called strickly by backend
FastTable.prototype.setSelectedRows=function(selectedRows,seqnum){
	if(seqnum<this.userSelectSeqnum)
		return;
	this.selectedRows.parseString(selectedRows);
}
FastTable.prototype.getActiveRow=function(){
	return this.currentRow;
}
FastTable.prototype.getSelectedRows=function(){
	return this.selectedRows.toString();
}
FastTable.prototype.updateSortAndFilter=function(colsSort){
	// use delta
	for(var i=0;i<colsSort.length;i++){
		var sort = colsSort[i].s;
		var index = colsSort[i].i; 
		var filter = colsSort[i].f; 
		var filterText = colsSort[i].ft; 
		var col = this.columns[index];
		if (sort != null) {
			col.sort=sort;
			this.setColumnSort(index, sort);
		}
		if (filter != null) {
			col.filter=filter;
			col.filterText=filterText;
			this.setColumnFilter(index);
		}
	}
}
FastTable.prototype.setColumnSort=function(index, sort){
        var col = this.columns[index];
        if(col == undefined)
                return;
        var className='';
        if(sort=='')
                className='';
        else if(sort==1)
                className='';
        else if(sort==0)
                className='';
        else if(sort==3)
                className='asc';
        else if(sort==2)
                className='des';
        if(col.filter)// {
                className='header_filtered '+className;

        col.headerClassName=className;
    col.headerElement.className="header "+ className;
}

FastTable.prototype.setColumnFilter=function(index){
	var col = this.columns[index];
        
	if(col.filter){
		col.hasFilter=true;
		if(this.filteredColumnBgColor!=null){
			col.headerElement.style.backgroundColor=this.filteredColumnBgColor;
		} else {
			col.headerElement.style.backgroundColor="#ff7f00";
		}
		if(this.filteredColumnFontColor!=null){
			col.headerElement.style.color=this.filteredColumnFontColor;
		} else {
			col.headerElement.style.color="white";
		}
	}
	else{
		col.hasFilter=false;
		col.headerElement.style.backgroundColor=null;
		col.headerElement.style.color=null;
	}
	col.updateQuickFilterValue();
}


FastTable.prototype.initColumns=function(pinning,cols){
	this.pinning=pinning;
	this.currentRow=-1;
	this.selectedRows.clear();
	removeAllChildren(this.headerElement);
	// Instead of looping through all elements, go through visible elements;
	this.deleteAllVisibleCells();
	this.cellElements.clear();
	this.valueElements.clear();
	this.cellElements.setSize(this.totalColumns,this.totalRows);
	this.data.clear();
	this.columns=[];
	this.hiddenColumns=[];

    var startCol=this.columns.length;
    resetPushDraggableContainer(this.headerElement);
	for(var i=0;i<cols.length;i++){
		var col=cols[i];
		var cssClass=col.cssClass;
		var id=col.id;
		var name=col.name;
		var visible=col.visible;
		var sort=col.sort;
		var clickable=col.clickable;
		var oneClick=col.oneClick;
		var isFixed=col.fix;
		var hasHover=col.hasHover;
		var headerStyle=col.headerStyle;
		var className='';
		var filter = col.filter;
		var filterText = col.filterText;
		var hids = col.hids;
		if(sort=='')
			className='';
		else if(sort==1)
			className='';
		else if(sort==0)
			className='';
		else if(sort==3)
			className='asc';
		else if(sort==2)
			className='des';
		if(col.filter)// {
			className='header_filtered '+className;
		var width=col.width;
		if(visible){
			var column=this.addColumn(id,name,width,className,cssClass,headerStyle,null,col.jsFormatterType, filter, filterText, isFixed,hasHover);
			column.setHIDS(hids);
			if(col.filter){
				column.hasFilter=true;
				if(this.filteredColumnBgColor!=null){
					column.headerElement.style.backgroundColor=this.filteredColumnBgColor;
				} else {
					column.headerElement.style.backgroundColor="#ff7f00";
				}
				if(this.filteredColumnFontColor!=null){
					column.headerElement.style.color=this.filteredColumnFontColor;
				} else {
					column.headerElement.style.color="white";
				}
			}
			else{
				column.hasFilter=false;
			}
		    column.clickable=clickable;
		    column.oneClick=oneClick;
		    column.isFixed=isFixed;
		}else
			this.hiddenColumns[id]=name;
	}
	this.updateColumnOffsets(startCol);
	this.stateInit = true; // lets FastTablePortlet know when to start sending clipzone
	this.flagInitColumns=true;
	this.repaint(false);
}

//TODO:Remove Not used
FastTable.prototype.getColumnBox=function(colIndex){
	var box = null;
	if(colIndex < this.columns.length){
		box = {};
		var col = this.columns[colIndex];
		var header = col.headerElement;
		
		var h1 = this.totalHeight + this.headerHeight; // Height of rows + header
//		err(header.offsetHeight);
//		err(this.headerHeight);
		var h = this.containerElement.offsetHeight - this.scrollSize; // Height of the container - the scrollbar
		h = h < h1 ? h: h1;

		var x = header.offsetLeft + header.offsetWidth;
		if(colIndex < this.pinning){
			x -= this.scrollPane.hscroll.clipTop
		}
		
		box.w = col.width;
		box.h = h;
		box.x = x;
		box.y = 0;
	}
	return box;
}

FastTable.prototype.firstVisibleColumn = null;
FastTable.prototype.lastVisibleColumn = null;
FastTable.prototype.updateVisibleColumns=function(){
	this.firstVisiblePinnedColumn = null;
	this.lastVisiblePinnedColumn = null;
	this.firstVisibleColumn = null;
	this.lastVisibleColumn = null;
	var left;
	var right;
	if(this.pinning > 0){
		left = 0;
		right = this.scrollPane.getClipWidth();
		
	    for(var i=0;i<this.pinning;i++){
	      var col = this.columns[i];
	      var colRight = col.getRightOffset();
	      if(colRight > left){
	    	  this.firstVisiblePinnedColumn = i;
	    	  break;
	      }
	    }
	    for(var i=this.pinning-1;i>= 0;i--){
	      var col = this.columns[i];
	      var colLeft =  col.offset;
	      if(colLeft < right){
	    	  this.lastVisiblePinnedColumn = i;
	    	  break;
	      }
	    }
	}
	
	if(this.totalPinnedWidth >= this.scrollPane.getClipWidth()){
		this.firstVisibleColumn = null;
		this.lastVisibleColumn = null;
		
	}else{
		var clipLeft = this.scrollPane.getClipLeft();
		clipLeft += Math.min(this.totalPinnedWidth, this.scrollPane.getClipWidth());
	    for(var i=0;i<this.columns.length;i++){
	      var col = this.columns[i];
	      var colRight = col.getRightOffset();
	      if(colRight > clipLeft){
	    	  this.firstVisibleColumn = i;
	    	  break;
	      }
	    }
	    
		var clipRight = this.scrollPane.getClipLeft() + this.scrollPane.getClipWidth();
	    for(var i=this.columns.length-1;i>= 0;i--){
	      var col = this.columns[i];
	      var colLeft =  col.offset;
	      if(colLeft < clipRight){
	    	  this.lastVisibleColumn = i;
	    	  break;
	      }
	    }
	}
	
}
FastTable.prototype.updatePinnedBorder=function(){
	if(this.pinning == 0){
	   this.pinnedBorderElement.style.display = "none"; 
	   this.pinnedTable.style.display="none";
	}
	if(this.pinning && this.pinning <= this.columns.length){
		this.pinnedTable.style.display=null;
		this.pinnedTable.style.width=toPx(this.totalPinnedWidth);
		
		var col = this.columns[this.pinning-1];
		var left = col.offset+ col.width;
		var height = this.scrollPane.getClipHeight();
//		if(this.hideHeaderDivider == "false")
			height += parseInt(this.headerHeight);
		if(this.quickColumnFilterHeight !=null)
			height += this.quickColumnFilterHeight;
		
		
		this.pinnedBorderElement.style.left = toPx(left-5);
		this.pinnedBorderElement.style.height = toPx(height);
		if(this.pinning && this.scrollPane.hscroll.clipTop > this.pinnedBorderWidth/2){
			this.pinnedBorderElement.style.display = "inherit"; 
		}else{
			this.pinnedBorderElement.style.display = "none"; 
		}
	}
}
FastTable.prototype.setPinnedBorderWidth=function(width){
	this.pinnedBorderWidth = width;
	this.pinnedBorderElement.style.width = toPx(width);
}
FastTable.prototype.applyPinnedBorderStyle=function(style){
	var dummyElement = {style:{}};
	applyStyle(dummyElement, style);
	Object.assign(this.pinnedBorderElement.style, dummyElement.style);
}
FastTable.prototype.clearData=function(){
//  this.valueElements.clear();
//	this.data.clear();
//	this.deleteAllCells();
//	this.firstVisiblePinnedColumn = null;
//	this.lastVisiblePinnedColumn = null;
//	this.firstVisibleColumn = null;
//	this.lastVisibleColumn = null;
}

//set row data 

FastTable.prototype.srd=function(){
	var y=arguments[0];
	var uid=arguments[1];
	for(var i=2;i<arguments.length;){
	    var x=arguments[i++];
	    if(x==-3){
	    	var start=arguments[i++];
	    	var cnt=arguments[i++];
	    	while(cnt--)
		      this.setData(uid,start++,y,arguments[i++],arguments[i++])
	    }else if(x>=0)
		    this.setData(uid,x,y,arguments[i++],arguments[i++])
	    else if(x==-1)
	        this.setRowTxColor(y,arguments[i++]);//,rowTxColor,rowBgColor);
	    else if(x==-2)
	        this.setRowBgColor(y,arguments[i++]);//,rowTxColor,rowBgColor);
	}
}

FastTable.prototype.setRowTxColor = function(y,rowTxColor){
    var isVisibleY = this.upperRow <= y && y <= this.lowerRow;
    
    if(!isVisibleY)
    	return;
	this.data.set(0,y,rowTxColor);
    this.updateCellsStylesForRow(y);
}
FastTable.prototype.setRowBgColor = function(y,rowBgColor){
    var isVisibleY = this.upperRow <= y && y <= this.lowerRow;
    
    if(!isVisibleY)
    	return;
	this.data.set(1,y,rowBgColor);
    this.updateCellsStylesForRow(y);
}

FastTable.prototype.setColumnWidth=function(location,width){
   this.columns[location].width=width;
   this.updateColumnOffsets(location);
	
   return;
//   this.totalWidth=this.columns[location].offset+this.columns[location].width;
//   for(var i=location+1;i<this.columns.length;i++){
//     this.columns[i].offset=this.totalWidth;
//	 this.totalWidth+=this.columns[i].width;
//   }
//   this.scrollPane.setPaneSize(this.totalWidth,this.totalHeight);
}

FastTable.prototype.updateColumnOffsets=function(startCol){
	if(this.columns.length == 0){
		this.totalWidth = 0;
		this.totalPinnedWidth = 0;
		return;
	}
	this.totalWidth=this.columns[startCol].getRightOffset();
	if(this.pinning == 0)
		this.totalPinnedWidth = 0;
	if(startCol < this.pinning){
		this.totalPinnedWidth = this.totalWidth; 
	}
	
	for(var i = startCol+1; i < this.columns.length; i++){
		this.columns[i].offset=this.totalWidth;
		this.totalWidth+=this.columns[i].width;
		if(i < this.pinning){
			this.totalPinnedWidth = this.totalWidth; 
		}
	}
}

FastTable.prototype.onHeaderDragging=function(e,x,y,column){
	var left = e.offsetLeft;
	var width=left-this.columns[e.location].offset+3;
	if(e.location<this.pinning){
		width-=fl(this.scrollPane.getClipLeft());
	}
	this.setColumnWidth(e.location,width);
    if(this.onUserColumnResize!=null)
    	this.onUserColumnResize(e,e.location,width);
	
   	this.flagColumnsMoving = true;
   	this.repaint(false);
    return false;
}

FastTable.prototype.clipHeaderDragging=function(e,rect){
	var min=this.columns[e.location].offset+5;
	if(min>rect.getLeft())
		rect.setLeft(min);
}

FastTable.prototype.fireSizeChanged=function(){
	if(this.widthChanged == true || this.heightChanged == true){
		this.flagSizeChanged = true;
		this.repaint(false);
	}
}
FastTable.prototype.updateContainerElement=function(){
    new Rect(0,this.menuHeight,this.width,this.height).writeToElement(this.containerElement);
}
FastTable.prototype.updateScrollPaneElement=function(){
	var x = 0;
	var y = parseInt(this.headerHeight);
	var w = this.width;
	var h = this.height - this.headerHeight;
	//TODOFILTER
	if(this.quickColumnFilterHidden == false){
		h -= this.quickColumnFilterHeight;
		y += this.quickColumnFilterHeight;
	}
	
    this.scrollPane.setLocation(x,y,w,h);
	//this.scrollPane.setLocation(0,this.headerHeight,this.width,this.height-this.headerHeight);
}
FastTable.prototype.updateTableElements=function(){
    this.tableElement.style.width=toPx(this.width);
    this.tableElement.style.height=toPx(this.height-this.headerHeight);
    this.pinnedTable.style.height=toPx(this.height-this.headerHeight);
}
FastTable.prototype.updateHeaderElement=function(){
	//TODOFILTER 

	var headerElementHeight = parseInt(this.headerHeight);
	if(this.quickColumnFilterHidden == false){
		headerElementHeight += this.quickColumnFilterHeight;
	}
    new Rect(0,0,Math.max(this.totalWidth+this.scrollSize,this.width),headerElementHeight).writeToElement(this.headerElement);
}
FastTable.prototype.updateScrollPaneSize=function(){
    this.scrollPane.setPaneSize(this.totalWidth,this.totalHeight);
}
FastTable.prototype.updateHeaderScroll=function(){	
  //Update horizontal scroll position
  var leftPos=Math.round(this.scrollPane.getClipLeft());
  this.headerElement.style.left=toPx(-this.scrollPane.getClipLeft());
}
FastTable.prototype.updateVisibleRowsCount=function(){
	//TODO:check for unnecessary calls;
	this.visibleRowsCount=this.getLowerRowVisible()-this.getUpperRowVisible()+1;
}

FastTable.prototype.nwCell=function(cellX, cellY){
	  var columnClassName="table_cell "+this.columns[cellX].cellClassName;
	  cell=nw("div",columnClassName);
	  this.cellElements.set(cellX,cellY,cell);
	  cell.rowLocation=cellY;
	  cell.colLocation=cellX;
	  cell.tabIndex=0;
	  cell.columnClassName=columnClassName;
	  cell.onmousedown=this.onCellMouseDownFunc;
	  cell.onmouseover=this.onCellMouseOverFunc;
	  cell.onmousemove=this.onCellMouseMoveFunc;
	  cell.onmouseout=this.onCellMouseOutFunc;
	  if(cellX<this.pinning)
		  this.pinnedTable.appendChild(cell);
	  else
		  this.tableElement.appendChild(cell);
	  return cell;
}

FastTable.prototype.deleteAllVisibleCells=function(){
	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)
		return;
	
	if(this.pinning > 0)
		if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)
			for(var y = this.upperRow; y <= this.lowerRow; y++){
				for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){
					this.removeCell(x,y);
				}
			}
	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)
		for(var y = this.upperRow; y <= this.lowerRow; y++){
			for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){
				this.removeCell(x,y);
			}
		}
	
	this.firstVisiblePinnedColumn = null;
	this.lastVisiblePinnedColumn = null;
	this.firstVisibleColumn = null;
	this.lastVisibleColumn = null;
}
FastTable.prototype.updateCellsLocations=function(){
	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)
		return;
	
	if(this.pinning > 0)
		if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)
			for(var y = this.upperRow; y <= this.lowerRow; y++){
				for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){
					var cell=this.cellElements.get(x,y);
					if(cell != null){
						this.applyCellSize(cell, x, y);
						this.applyCellLocation(cell, x, y);
					}
				}
			}
	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)
		for(var y = this.upperRow; y <= this.lowerRow; y++){
			for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){
				var cell=this.cellElements.get(x,y);
				if(cell != null){
					this.applyCellSize(cell, x, y);
					this.applyCellLocation(cell,x,y);
				}
			}
		}
	
}
FastTable.prototype.updateCellsStyles=function(){
	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)
		return;
	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)
		for(var y = this.upperRow; y <= this.lowerRow; y++){
			for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){
				var cell=this.cellElements.get(x,y);
				if(cell != null)
					this.applyStyle(cell, cell._style);
			}
		}
	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)
		for(var y = this.upperRow; y <= this.lowerRow; y++){
			for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){
				var cell=this.cellElements.get(x,y);
				if(cell != null)
					this.applyStyle(cell, cell._style);
			}
		}
}
FastTable.prototype.updateCellsStylesRow=function(){
	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)
		return;
	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)
		for(var y = this.upperRow; y <= this.lowerRow; y++){
			this.updateCellStylesForRow(y);
		}
	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)
		for(var y = this.upperRow; y <= this.lowerRow; y++){
			this.updateCellStylesForRow(y);
		}
}
FastTable.prototype.updateCellsStylesForRow=function(y){
	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)
		return;
	if(y<this.upperRow || y>this.lowerRow)
		return;
	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)
			for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){
				var cell=this.cellElements.get(x,y);
				if(cell != null)
					this.applyStyle(cell, cell._style);
			}
	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)
			for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){
				var cell=this.cellElements.get(x,y);
				if(cell != null)
					this.applyStyle(cell, cell._style);
			}
}
FastTable.prototype.updateCells=function(){
	return;
}

FastTable.prototype.calcCellWidth=function(x){
	return this.columns[x].width;
}
FastTable.prototype.calcCellHeight=function(y){
	return parseInt(this.rowHeight);
}
FastTable.prototype.calcCellLeft=function(x){
	return x < this.pinning? this.columns[x].offset: this.columns[x].offset - Math.round(this.scrollPane.getClipLeft());
}
FastTable.prototype.calcCellTop=function(y){
	if(y > this.totalRows || y < 0)
		return -1;
	return y * this.rowHeight - this.scrollPane.getClipTop();
}
FastTable.prototype.calcRowFromTop=function(y){
	return Math.floor( (y + this.scrollPane.getClipTop())/ this.rowHeight);
}
FastTable.prototype.calcColFromLeft=function(x){
	var col = -1;
	if(x < 0)
		return col;
	if(x <= this.totalPinnedWidth){
		for(var i = 0; i < this.pinning; i++){
			var colmeta = this.columns[i];
			if(colmeta.offset <= x && x < (colmeta.offset + colmeta.width)){
				col = i;
				break;
			}
		}
	}
	else{
		x+= this.scrollPane.getClipLeft();
		for(var i = this.pinning; i < this.totalColumns; i++){
			var colmeta = this.columns[i];
			if(colmeta.offset <= x && x < (colmeta.offset + colmeta.width)){
				col = i;
				break;
			}
		}
	}
	return col;
//	return Math.floor( y/ this.rowHeight);
}

FastTable.prototype.applyCellSize=function(cell, x, y){
		cell.style.width=toPx(this.columns[x].width);
	cell.style.height=toPx(this.rowHeight);
}

FastTable.prototype.applyCellLocation=function(cell, x, y){
	cell.rowLocation=y;
	cell.colLocation=x;
	
	
	if(x < this.pinning){
		var topPos=this.scrollPane.getClipTop();
		cell.style.left=toPx(this.columns[x].offset);		
		cell.style.top=toPx(y*this.rowHeight-topPos);
	}
	else{
		var topPos=this.scrollPane.getClipTop();
		var leftPos=Math.round(this.scrollPane.getClipLeft());
		cell.style.left=toPx(this.columns[x].offset-leftPos);
		cell.style.top=toPx(y*this.rowHeight-topPos);
	}
		
}


FastTable.prototype.updateStyle=function(cell){
  cell.style.display='inline-flex';
  var y = cell.rowLocation;
  var x = cell.colLocation;
  
  // Selection
  var selected=this.selectedRows.isSelected(y);
  var cn=cell.columnClassName;
  
  if(this.currentRow==y){
    cn+=' '+ this.activeClassname; 
  }else if(selected){
    cn+=' '+ this.selectedClassname; 
  }
  
  //Row Text + Background Color
  var rowTxColor=this.data.get(0,y);
  var rowBgColor=this.data.get(1,y);
  if(rowBgColor){
	  cell.style.backgroundColor=rowBgColor;
  }else if(y%2 || !this.useGreybars){
	  if (this.greyBarColor != null){
		  cell.style.backgroundColor=null;
	  }
	  applyStyle(cell,this.bgStyle);
	  cell.style.backgroundImage=null;
  }else{
	  cell.style.backgroundColor=this.greyBarColor;
	  cell.style.backgroundImage=null;
  }
  if(rowTxColor){
	  cell.style.color=rowTxColor;
  }else
	  cell.style.color=this.defaultFontColor;

  //Borders and dividers
  if(this.cellBorderColor != null)
	  cell.style.borderColor=this.cellBorderColor;
  if(this.cellBottomDivider != null)
	  cell.style.borderBottomWidth=toPx(this.cellBottomDivider);
  if(this.cellRightDivider != null)
	  cell.style.borderRightWidth=toPx(this.cellRightDivider);
  if(this.verticalAlign != null)
	  cell.style.justifyContent=this.verticalAlign;
  if(this.cellPadHt != null){
      cell.style.paddingLeft = toPx(this.cellPadHt);
      cell.style.paddingRight = toPx(this.cellPadHt);
  }
 
  if (cell.checkbox)
  	this.applyCellCheckboxConfig(cell, cell.isChecked, cell.checkboxStyles);
  //Class name
  cell.className=cn;
}

FastTable.prototype.setOptions=function(options){
	//TODOFILTER
	if(options.quickColumnFilterBgCl  != null)
		this.quickColumnBackgroundColor = options.quickColumnFilterBgCl;
	if(options.quickColumnFilterFontCl != null)
		this.quickColumnFontColor = options.quickColumnFilterFontCl;
	if(options.quickColumnFilterFontSz != null)
		this.quickColumnFontSz = options.quickColumnFilterFontSz;
	if(options.quickColumnFilterBdrCl  != null)
		this.quickColumnBorderColor = options.quickColumnFilterBdrCl;
	if(options.quickColumnFilterHidden != null)
		this.quickColumnFilterHidden="false"==options.quickColumnFilterHidden?false:true;
	if(options.quickColumnFilterHeight != null)
		this.quickColumnFilterHeight = parseInt(options.quickColumnFilterHeight);
	if(options.useGreybars!=null)
		this.useGreybars="true"==options.useGreybars;
	if(options.rowHeight!=null)
		this.rowHeight=options.rowHeight;
	if(options.headerRowHeight != null){
		this.headerHeight = options.headerRowHeight;
	}
	if(options.headerFontSize != null){
		this.headerFontSize = options.headerFontSize;
	}
	if(options.cellBottomDivider != null){
		this.cellBottomDivider = options.cellBottomDivider;
	}else this.cellBottomDivider=0;
	
	if(options.cellRightDivider != null){
		this.cellRightDivider = options.cellRightDivider;
	}else this.cellRightDivider=1;
	if(options.verticalAlign != null){
		this.verticalAlign = options.verticalAlign;
	}else this.verticalAlign = "center";
	if(options.cellPadHt != null)
		this.cellPadHt = options.cellPadHt;
	
//	this.tableElement.className="";
//	this.pinnedTable.className="";
	applyStyle(this.tableElement,options.backgroundStyle);
	applyStyle(this.pinnedTable,options.backgroundStyle);
	applyStyle(this.scrollPane.DOM.paneElement,options.backgroundStyle);
	this.tableElement.className='table '+this.tableElement.className;
	this.pinnedTable.className='table '+this.pinnedTable.className;
	
	applyStyle(this.tableElement,options.tableStyle);
	applyStyle(this.pinnedTable,options.tableStyle);

	if(options.fontFamily != null)
		this.fontFamily = options.fontFamily;
	if(options.fontSize != null)
		this.fontSize = options.fontSize;
	if(this.fontSize != null){
		applyStyle(this.tableElement,"style.fontSize="+toPx(this.fontSize));
		applyStyle(this.pinnedTable,"style.fontSize="+toPx(this.fontSize));
	}
	if(this.fontFamily != null){
		applyStyle(this.tableElement,"style.fontFamily="+this.fontFamily);
		applyStyle(this.pinnedTable,"style.fontSize="+this.fontFamily);
	}

	
	this.bgStyle = options.backgroundStyle;
	this.greyBarColor = options.greyBarColor;
	this.flashUpColor = options.flashUpColor;
	this.flashDnColor = options.flashDnColor;
	this.flashMs = options.flashMs;
	if(this.flashMs==null)
	  this.flashMs=0;
    this.flashStyle='background '+this.flashMs+'ms linear';
	if(this.bgStyle==null || this.bgStyle=='_bg=null')// 2nd check is for
														// legacy purposes
		this.bgStyle='_bg=#FFFFFF';
	if(this.greyBarColor==null)
		this.greyBarColor='#EEEEEE';
	this.defaultFontColor=options.defaultFontColor;
	this.hideHeaderDivider = options.hideHeaderDivider;
	this.cellBorderColor = options.cellBorderColor;
	if(options.scrollBarWidth!=null){
	  this.scrollSize = (options.scrollBarWidth * 1);
	  this.scrollPane.setSize(options.scrollBarWidth * 1);
	}
	
	// scroll bar radius
	if (options.scrollBarRadius) {
		this.scrollPane.hscroll.DOM.applyBorderRadius(options.scrollBarRadius);
		this.scrollPane.vscroll.DOM.applyBorderRadius(options.scrollBarRadius);
	}
	// scroll bar hide arrows
	if (options.scrollBarHideArrows) {
		this.scrollPane.hscroll.DOM.hideArrows(options.scrollBarHideArrows);
		this.scrollPane.vscroll.DOM.hideArrows(options.scrollBarHideArrows);
		
	}
	
	this.scrollPane.hscroll.DOM.applyColors(options.gripColor, options.trackColor, options.trackButtonColor, options.scrollBorderColor, options.scrollBarCornerColor);
	this.scrollPane.vscroll.DOM.applyColors(options.gripColor, options.trackColor, options.trackButtonColor, options.scrollBorderColor, options.scrollBarCornerColor);
	
	
	// Set scroll icon colors...
	scrollIconsColor=options.scrollIconsColor || '#000000';
	this.scrollPane.updateIconsColor(scrollIconsColor);
	
//	this.setLocation(0,0,this.width,this.height);
	this.filteredColumnBgColor = options.filteredColumnBgColor;	 
	this.filteredColumnFontColor = options.filteredColumnFontColor;
	this.menuBarBg=options.menuBarBg;
	this.menuFontColor=options.menuFontColor;
	
	this.activeClassname=options.cellActiveBg==null ? 'cell_active_default' : toCssForColor(this.tableElement,options.cellActiveBg);
	this.selectedClassname=options.cellSelectedBg==null ? 'cell_selected_default' : toCssForColor(this.tableElement,options.cellSelectedBg);
	if((this.firstVisibleColumn != null && this.lastVisibleColumn != null) || (this.firstVisiblePinnedColumn != null || this.lastVisiblePinnedColumn)){
		this.flagOptionsSet=true;
		this.repaint(false);
	}
}

var COLORS_TO_STYLES_NEXT_ID=0;
function toCssForColor(e,color){
    var i=parseColorAlpha(color);
    var COLORS_TO_STYLES=getWindow(e).COLORS_TO_STYLES;
    if(COLORS_TO_STYLES==null)
      COLORS_TO_STYLES=getWindow(e).COLORS_TO_STYLES={};
    var r=COLORS_TO_STYLES[color];
    if(r!=null)
    	return r;
	var imgurl=toImageData(e,i[0],i[1],i[2],i[3]);
	r='ft_custom_'+(COLORS_TO_STYLES_NEXT_ID++);
    setCssClassProperty(e,r,'background-image',imgurl);
    COLORS_TO_STYLES[color]=r;
    return r;
}


FastTable.prototype.updateColumns=function(){
	if(this.columns.length == 0)
		return;
//    var rect=new Rect();
    var f = this.firstVisibleColumn;
    var l = this.lastVisibleColumn;
    var fp = this.firstVisiblePinnedColumn;
    var lp = this.lastVisiblePinnedColumn;
    var visible = f != null && l != null;
    var visiblePinned = fp != null && lp != null;
    
    var left = this.scrollPane.getClipLeft();
    
    //TODOFILTER
	
    for(var i=0;i<this.columns.length;i++){
    	if(visible && f <= i && i <= l){
    		this.columns[i].headerElement.style.left=toPx(this.columns[i].offset);
    		this.columns[i].headerElement.style.width=toPx(this.columns[i].width);

    		
    		this.columns[i].grabElement.style.left=toPx(this.columns[i].getRightOffset()-3);
//		    rect.setLocation(this.columns[i].offset,0,this.columns[i].width,this.headerHeight).writeToElement(this.columns[i].headerElement);
//		    rect.setLocation(this.columns[i].getRightOffset()-3,0,7,this.headerHeight).writeToElement(this.columns[i].grabElement);
    		this.columns[i].headerElement.style.display = null;
    		if (!this.columns[i].isFixed)
				this.columns[i].grabElement.style.display = null;
    		
			if(this.quickColumnFilterHidden == false){
	    		//this.columns[i].quickColumnFilterElement.style.left=toPx(this.columns[i].offset);
	    		this.columns[i].quickColumnFilterElement.style.width=toPx(this.columns[i].width-4);
				//this.columns[i].quickColumnFilterElement.style.display = null;
	    	}
   			this.columns[i].quickColumnFilterElement.style.display = this.quickColumnFilterHidden == false?null:"none";

    	}
    	else if(visiblePinned && fp <= i && i <= lp){
    		this.columns[i].headerElement.style.left=toPx(this.columns[i].offset+left);
    		this.columns[i].headerElement.style.width=toPx(this.columns[i].width);
    		this.columns[i].grabElement.style.left=toPx(this.columns[i].getRightOffset()+left-3);
    		
//		    rect.setLocation(this.columns[i].offset+left,0,this.columns[i].width,this.headerHeight).writeToElement(this.columns[i].headerElement);
//		    rect.setLocation(this.columns[i].getRightOffset()+left-3,0,7,this.headerHeight).writeToElement(this.columns[i].grabElement);
    		this.columns[i].headerElement.style.display = null;
    		this.columns[i].grabElement.style.display = null;

			if(this.quickColumnFilterHidden == false){
	    		//this.columns[i].quickColumnFilterElement.style.left=toPx(this.columns[i].offset+left);
	    		this.columns[i].quickColumnFilterElement.style.width=toPx(this.columns[i].width-4);
				//this.columns[i].quickColumnFilterElement.style.display = null;
	    	}
   			this.columns[i].quickColumnFilterElement.style.display = this.quickColumnFilterHidden == false?null:"none";
    	}
    	else{
    		this.columns[i].headerElement.style.display = "none";
    		this.columns[i].grabElement.style.display = "none";
	    	this.columns[i].quickColumnFilterElement.style.display = "none";
    	}
    }
}

FastTable.prototype.applyColumnStyle=function(i){
	var headerElement=i==-1 ? this.headerElement : this.columns[i].headerElement;
	
	//Header fontSize, height, and fontFamily
	headerElement.style.fontSize = toPx(this.headerFontSize);
	headerElement.style.fontFamily=this.fontFamily || "arial";
	//TODOFILTER
	var headerElementHeight = parseInt(this.headerHeight);
	if(this.quickColumnFilterHidden == false){
		headerElementHeight += this.quickColumnFilterHeight;
		if(i != -1){
			this.columns[i].quickColumnFilterElement.style.height= toPx(this.quickColumnFilterHeight -7);
		}
	}
	
	headerElement.style.height = toPx(headerElementHeight);
	
	//Header divider bar
	if(this.hideHeaderDivider=="true")
		headerElement.style.boxShadow="none";
//		headerElement.style.backgroundImage="none";
	else
//		headerElement.style.boxShadow=null;
		if(this.cellBorderColor != null)
			headerElement.style.boxShadow="inset 0px -8px 5px -5px "+ this.cellBorderColor;
		else
			headerElement.style.boxShadow=null;
	
	headerElement.style.color=this.menuFontColor!=null ? this.menuFontColor : "white";
	
	if(i!=-1){
		var col=this.columns[i];
		// Header border 
		headerElement.style.borderColor = this.cellBorderColor;
		headerElement.style.borderRightWidth=toPx(this.cellRightDivider);
//			headerElement.style.borderBottomWidth=this.cellBottomDivider;
//			col.grabElement.style.borderColor=this.cellBorderColor;
		col.grabElement.style.background=this.cellBorderColor;
		col.grabElement.style.borderColor="#ffffff";
		col.grabElement.style.borderWidth="0px 2px";
		
		headerElement.style.backgroundColor=this.menuBarBg!=null ? this.menuBarBg : "#535353";
		if(col.hasFilter){
			if(this.filteredColumnBgColor!=null){
				headerElement.style.backgroundColor=this.filteredColumnBgColor;
			} else {
				headerElement.style.backgroundColor="#ff7f00";
			}
			if(this.filteredColumnFontColor!=null){
				headerElement.style.color=this.filteredColumnFontColor;
			} else {
				headerElement.style.color="white";
			}
		}else{
			headerElement.style.backgroundColor=this.menuBarBg!=null ? this.menuBarBg : "#535353";
		}
		applyStyle(col.headerElement,col.columnHeaderStyle);
	}else{
		headerElement.style.backgroundColor=this.menuBarBg!=null ? this.menuBarBg : "#535353";
	}
}

//TODOFILTER
FastTable.prototype.updateQuickFilterStyles=function(){
	if(this.quickColumnFilterHidden == true)
		return;
	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)
		for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){
			if(x == -1)
				continue;
			var quickColumnFilterElement = this.columns[x].quickColumnFilterElement;
			quickColumnFilterElement.input.style.backgroundColor= this.quickColumnBackgroundColor;
			quickColumnFilterElement.input.style.color= this.quickColumnFontColor;
			quickColumnFilterElement.input.style.border= "1px solid " + this.quickColumnBorderColor;
			quickColumnFilterElement.input.style.fontSize=toPx(this.quickColumnFontSz);
		}
	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)
		for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){
			if(x == -1)
				continue;
			var quickColumnFilterElement = this.columns[x].quickColumnFilterElement;
			quickColumnFilterElement.input.style.backgroundColor= this.quickColumnBackgroundColor;
			quickColumnFilterElement.input.style.color= this.quickColumnFontColor;
			quickColumnFilterElement.input.style.border= "1px solid " + this.quickColumnBorderColor;
			quickColumnFilterElement.input.style.fontSize=toPx(this.quickColumnFontSz);
		}
	return;
}
FastTable.prototype.updateQuickFilterValues=function(){
	if(this.quickColumnFilterHidden == true)
		return;
	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)
		for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){
			if(x == -1)
				continue;
			var quickColumnFilterElement = this.columns[x].quickColumnFilterElement;
		}
	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)
		for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){
			if(x == -1)
				continue;
			var quickColumnFilterElement = this.columns[x].quickColumnFilterElement;
		}
	return;
}

FastTable.prototype.updateColumnStyles=function(){
	//Sets header bar height
	//TODOFILTER
	//this.scrollPane.y = this.headerHeight;
	var scrollPaneElementTop = parseInt(this.headerHeight);
	if(this.quickColumnFilterHidden == false)
		scrollPaneElementTop += this.quickColumnFilterHeight;

	this.scrollPaneElement.style.top = scrollPaneElementTop;
	
	this.applyColumnStyle(-1);
	
	if(this.columns.length == 0)
		return;
	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)
		for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){
			this.applyColumnStyle(x);
		}
	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)
		for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){
			this.applyColumnStyle(x);
		}
	return;
}
FastTable.prototype.getUpperRowVisible=function(){
	return Math.max(this.upperRow,0);
}

FastTable.prototype.getLowerRowVisible=function(){
	return this.lowerRow;
}


FastTable.prototype.cellPool=new Array();

FastTable.prototype.removeCell = function(x,y){
    var cell=this.cellElements.set(x,y,null);
	if(cell==null)
		return;
    if(this.flashMs>0)
	  this.valueElements.remove(x,cell.uid);
	if(cell.parentElement)
		cell.parentElement.removeChild(cell);
}


FastTable.prototype.setClipZone=function(){
	var oldUpperRow = this.getUpperRowVisible();
	var oldLowerRow = this.getLowerRowVisible();
	var oldLeftCol = this.firstVisibleColumn;
	var oldRightCol = this.lastVisibleColumn;
	var verticalScroll = false;
	
	//Updates new clipzone (left,right,top,bottom) and sends unused elements to garbage
	this.upperRow = Math.floor(this.scrollPane.getClipTop()/this.rowHeight);
	this.lowerRow = Math.ceil((this.scrollPane.getClipTop()+this.scrollPane.getClipHeight())/this.rowHeight)-1;
	
	this.updateVisibleColumns();
    
	if(this.onUpdateCellsEnd!=null)
		this.onUpdateCellsEnd(this);
	
  
	var isPinned = false;
	if(this.firstVisiblePinnedColumn !=null && this.lastVisiblePinnedColumn != null)
		isPinned = true;
	//Scrolled down
	if(this.upperRow > oldUpperRow){
		var l = this.upperRow <= oldLowerRow? this.upperRow : oldLowerRow+1;
		for(var y=oldUpperRow; y < l; y++){
			this.data.remove(0, y);
			this.data.remove(1, y);
			for(var x=this.firstVisibleColumn;x<=this.lastVisibleColumn;x++)
				this.removeCell(x,y);
			if(isPinned === true)
				for(var x= this.firstVisiblePinnedColumn; x<=this.lastVisiblePinnedColumn;x++)
					this.removeCell(x,y);
				
		}
		verticalScroll=true;
	}

	//Scrolled up	
	if(this.lowerRow < oldLowerRow){
		var l = this.lowerRow >= oldUpperRow? this.lowerRow : oldUpperRow-1;
		for(var y=oldLowerRow; y > l; y--){
			this.data.remove(0, y);
			this.data.remove(1, y);
			for(var x=this.firstVisibleColumn;x<=this.lastVisibleColumn;x++)
				this.removeCell(x,y);
			if(isPinned === true)
				for(var x= this.firstVisiblePinnedColumn; x<=this.lastVisiblePinnedColumn;x++)
					this.removeCell(x,y);
		}
		verticalScroll=true;
	}
	// if window restore, we need to clear diagonal data (e.g. scroll up + scroll left)
	var end = verticalScroll ? this.totalRows - 1 : this.lowerRow;
	//Scrolled right
	if(this.firstVisibleColumn != null && oldLeftCol != null)
		if(this.firstVisibleColumn > oldLeftCol){
			var l = this.firstVisibleColumn <= oldRightCol? this.firstVisibleColumn : oldRightCol+1;
			for(var y=this.upperRow; y <= end; y++)
				for(var x=oldLeftCol;x<l;x++)
					this.removeCell(x,y);
		}
	
	//Scrolled left
	if(this.lastVisibleColumn != null && oldRightCol != null)
		if(this.lastVisibleColumn < oldRightCol){
			var l = this.lastVisibleColumn >= oldLeftCol? this.lastVisibleColumn : oldLeftCol-1;
			for(var y=this.upperRow; y <= end; y++)
				for(var x=oldRightCol;x>l;x--)
					this.removeCell(x,y);
		
		}
	
}


FastTable.prototype.flagRowsRepaint = false;
FastTable.prototype.repaint=function(flagFromServer){
	ttt= this;
	if(flagFromServer){
		
	}
	else{
		if(this.flagInitColumns == true){
//			err("init");
			this.flagInitColumns = false;
		    this.updateVisibleColumns();
			this.updateVisibleRowsCount();
		    this.updateContainerElement();
		    this.updateScrollPaneElement();
		    this.updateTableElements();
		    this.updateHeaderElement();
		    this.updateScrollPaneSize();
		    this.updateHeaderScroll();
			this.setClipZone();
		    this.updateColumns();
			this.updateColumnStyles();
			this.updateQuickFilterStyles();
			this.updatePinnedBorder();
			this.updatePinnedColumns();
		}
		else if(this.flagOptionsSet == true){
//			err("setoptions");
			this.updateColumnStyles();
			this.updateQuickFilterStyles()
			this.updateVisibleRowsCount();
		    this.updateContainerElement();
		    this.updateScrollPaneElement();
		    this.updateTableElements();
		    this.updateHeaderElement();
		    this.updateScrollPaneSize();
		    this.updateHeaderScroll();
			this.setClipZone();
		    this.updateColumns();
			this.updatePinnedBorder();
			this.updatePinnedColumns();
			this.updateCellsLocations();
			this.updateCellsStyles();
			this.flagOptionsSet = false;
		}
		else if(this.flagScrollMoved == true){
//			err("scrollmoved");
			this.flagScrollMoved = false;
		    this.updateHeaderScroll();
			this.setClipZone();
			this.updateCellsLocations();
			
			if(this.scrollPane.hscrollChanged != 0){
				this.updateColumns();
				this.updateColumnStyles();
				this.updateQuickFilterStyles();
				this.updatePinnedBorder();
				this.updatePinnedColumns();
			}
			
		}
		else if(this.flagColumnsMoving == true){
//			err("colmove");
			this.updateTableElements();
			this.updateHeaderElement();
			this.updateScrollPaneSize();
		    this.updateHeaderScroll();
    	
			this.setClipZone();
			this.updateColumns();
			this.updateCellsLocations();
			this.updatePinnedBorder();
			this.updatePinnedColumns();
			this.flagColumnsMoving = false;
		}
		else if(this.flagTotalSizeChanged == true){
//			err("totalSizeChanged");
			this.flagTotalSizeChanged = false;
			this.updateVisibleRowsCount();
			this.updateHeaderElement();
			this.updateScrollPaneSize();
    		//
		    this.updateHeaderScroll();
			this.updateVisibleColumns();
			this.setClipZone();
			this.updateColumns();
			this.updateColumnStyles();
			this.updateQuickFilterStyles();
			this.updatePinnedBorder();
			this.updatePinnedColumns();
		}
		else if(this.flagSizeChanged == true){
//			err("sizechange");
			this.flagSizeChanged = false;
			this.updateVisibleRowsCount();
			this.updateContainerElement();
			this.updateScrollPaneElement();
			this.updateTableElements();
			if(this.widthChanged)
				this.updateHeaderElement();
			//
		    this.updateHeaderScroll();
			this.setClipZone();
			this.updateColumns();
			this.updateColumnStyles();
			this.updateQuickFilterStyles();
			this.updatePinnedBorder();
			this.updatePinnedColumns();
			this.widthChanged = false;
			this.heightChanged = false;
		}
		else if(this.flagRowsRepaint == true){
//			err("repaintrows");
			this.flagRowsRepaint = false;
			this.updateCellsStyles();
//			this.updateCellsStylesRow();
		}
		else if(this.flagSelectionChanged == true){
//			err("updatesel");
			this.flagSelectionChanged = false;
			this.updateCellsStyles();
		}
	}
//	log("LOG# Table: " + this.tableElement.childElementCount + " Pinned Table: " + this.pinnedTable.childElementCount);
}
FastTable.prototype.setData=function(uid,x,y,value,style){
    var isVisibleX = false;
    var isVisibleY = this.upperRow <= y && y <= this.lowerRow;
    if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null){
    	isVisibleX = this.firstVisiblePinnedColumn <= x && x <= this.lastVisiblePinnedColumn; 
    }
    if(this.firstVisibleColumn != null && this.lastVisibleColumn != null){
    	isVisibleX = isVisibleX || this.firstVisibleColumn <= x && x <= this.lastVisibleColumn; 
    }
    var isVisible = isVisibleX && isVisibleY;
    
    if(!isVisible)
    	return;
	var cell=this.cellElements.get(x,y);
	if(cell==null)
		cell = this.nwCell(x,y);
    cell.style.display='inline-flex';
    this.applyCellSize(cell, x, y);
    this.applyCellLocation(cell, x, y);
    cell.uid=uid;
    //Format Value
    this.formatValue(cell, x, y, value, style);
    //Apply Style
    cell._style = style;
    // if update speed > flash speed, then at this point transition hasn't been nullify yet
    // in this case we need to force apply style so it honors the flashing colors
    this.applyStyle(cell,style,true);
    if(this.flashMs>0){
    	// Here is how the timeline for flashing work:
    	// if flashing ms set to 500ms, then we will set the bg color immediately and it will remain there for 500ms. Then for the next 500ms, it will fade back to the original bg color.
    	// So setting flashing to 500ms actually means 500ms * 2.
    	// 			0 ------------ 500ms ------------ 1s
    	//      set bg color     begin fade        fade complete
      var oldValue=this.valueElements.set(x,uid,value);
      // oldValue is undefined if this is an insert or the value was deleted and there are several updates to that value.
      if(oldValue && oldValue!=value){
    	  // in case the update speed > flashing duration, we need to remove the old timeout
        if(cell.flashTimeout!=null)
          clearTimeout(cell.flashTimeout);
        if (cell.resetTransition!=null)
        	clearTimeout(cell.resetTransition);
        var flashDir;
        if(this.flashUpColor==this.flashDnColor)
          flashDir=1;
        else{
          var n1=parseNumber(oldValue); // need to use locale specific parsing
          var n2=parseNumber(value);
          if(isNaN(n1) || isNaN(n2))
            flashDir=oldValue<value? 1 : 2;
          else
            flashDir=n1 < n2? 1 : 2;
        }
        var that=this;
        cell.style.backgroundColor=flashDir==1 ? this.flashUpColor : this.flashDnColor;
		var func=function(){
			cell.style.transition=that.flashStyle; 		
			cell.flashTimeout=null;
			// fade back to original bg color
			that.applyStyle(cell,style,true);
		};
		var func2=function(){
			cell.style.transition=''; 		
			cell.resetTransition=null;
			// no need to apply style here
		};
		// apply timeout
		cell.flashTimeout=window.setTimeout(func,this.flashMs);
		// reset the transition after flashing is over. This is to avoid triggering transition effect when changing styles
		cell.resetTransition=window.setTimeout(func2,this.flashMs*2);
     } else {
    	 // TODO: handle cases where prev value is undefined because we deleted it but we should show the flash color
     }
   }
}

FastTable.prototype.formatValue=function(cell, x, y, value, style){
    var jsft=this.columns[x].jsFormatterType;
    if(jsft === "spark_line"){
    	k = cell;
    	if(cell.spark == null){
    		cell.spark = new InlineLineChart(cell, value, ",");
    	}else{
    		cell.spark.setText(value,',');
    	}
   		cell.spark.drawSvg(cell.clientWidth- this.cellRightDivider, cell.clientHeight-this.cellBottomDivider);
    } else if (jsft === "checkbox") {
    	if(value !== "N/A" ){
	    	if (cell.checkbox == null) {
	    		cell.checkbox = new CheckboxField();
				cell.checkbox.element.onmousedown = function() {
					// do nothing
				}
	    	}

	    	cell.innerHTML = "";	    
			this.applyCellCheckboxConfig(cell, value, style); 
			 
		} else{
			cell.innerHTML=value;
    		cell.oldValue=value;
    		cell.checkbox = null;
		}  	
    } else{
    	cell.innerHTML=value;
    	cell.oldValue=value; 
    }
}
FastTable.prototype.applyCellCheckboxConfig=function(cell, value, style) {
	cell.isChecked = value;
	cell.checkboxStyles = style;
	
	// bg and fg
	var fgIndex = cell.checkboxStyles.indexOf("_fg");
	var bgIndex = cell.checkboxStyles.indexOf("_bg");
	var checkColor;
	var checkboxColor;
	if (fgIndex != -1)
		checkColor = cell.checkboxStyles.substring(fgIndex + 4, fgIndex + 11);
	if (bgIndex != -1) {
		checkboxColor = cell.checkboxStyles.substring(bgIndex + 4, bgIndex + 11);
	}
	cell.checkbox.applyStyles(checkboxColor, checkColor, checkboxColor);
	
	// Alignment 
	var fmIndex = cell.checkboxStyles.indexOf("_fm");
	if (fmIndex != -1) {
		if (cell.checkboxStyles.indexOf("left") != -1)
			cell.style.alignItems = "flex-start";
		else if (cell.checkboxStyles.indexOf("center") != -1)
			cell.style.alignItems = "center";
		else if (cell.checkboxStyles.indexOf("right") != -1)
			cell.style.alignItems = "flex-end";
		
	}
	
	// others
	cell.checkbox.element.style.pointerEvents = "none";
	cell.checkbox.setValue(value);
	cell.checkbox.setSize(cell.clientHeight - this.cellBottomDivider - 5, cell.clientHeight - this.cellBottomDivider - 5);
	cell.checkbox.element.style.margin = "2px 0px 0px";
	cell.appendChild(cell.checkbox.element);
}
FastTable.prototype.applyStyle=function(target,val,force){
	// if resetTransition is not null, then cell flashing is in progress. Stop any attempt to change the style during cell flashing.
	// fyi the transitioning effect happens if we change the cell bg color.
    if(!force && target.resetTransition!=null) {
        return;
    }
	target.className='';
	this.updateStyle(target);	
	if(val != null && "" != val && target.checkbox == null)
		applyStyle(target,val);
	if(target.cellClassName){
		target.className+=" "+target.cellClassName;
	}
}

FastTable.prototype.setColsRowsCount=function(x,y){
  this.data.setSize(x+2,y);
  this.cellElements.ensureSize(x,y);
	this.setTotalRows(y);
  this.totalColumns = x;
}

FastTable.prototype.setTotalRows=function(rows){
  for(var y=this.totalRows-1; y >= rows; y--){
	 this.data.remove(0, y);
	 this.data.remove(1, y);
    for(var x=0;x<this.cellElements.getWidth();x++)
      if(this.cellElements.get(x,y) != null)
        this.removeCell(x,y);
  }
  this.totalRows = rows;
}

FastTable.prototype.setTotalWidthHeight=function(width,rows){
	this.setTotalRows(rows);
	this.totalWidth = width;
	this.totalHeight = rows * this.rowHeight;
	this.flagTotalSizeChanged = true;
	if(this.totalWidth <= this.width){
		this.scrollPane.setClipLeft(0);
	}
	this.repaint(false);
	var scrollSize = this.scrollPane.hscrollVisible ? this.scrollPane.scrollSize : 0;
	this.callback('tableSizeChanged',{
	  							left:this.scrollPane.getClipLeft(),
	  							top:this.scrollPane.getClipTop(),
	  							height:this.scrollPane.getClipHeight() - scrollSize,
	  							contentWidth:this.scrollPane.paneWidth,
	  							contentHeight:this.scrollPane.paneHeight
	  						   });
}

//called strickly from backend
FastTable.prototype.setScroll=function(left,top,userSeqnum){
	if(this.userScrollSeqnum>userSeqnum)
		return;
	this.scrollPane.setClipLeft(left);
	this.scrollPane.setClipTop(top);
}
FastTable.prototype.getRowsCount = function(){
	return this.data.getHeight();
}

FastTable.prototype.onFilter=function(col){
	this.callback('openFilter', {col:col});
}

FastTable.prototype.onScroll=function(){
  this.userScrollSeqnum++;
  var scrollSize = this.scrollPane.hscrollVisible ? this.scrollSize : 0;
  this.callback('tableScroll',{
	  							left:this.scrollPane.getClipLeft(),
	  							top:this.scrollPane.getClipTop(),
	  							userSeqnum:this.userScrollSeqnum,
	  							height:this.scrollPane.getClipHeight() - scrollSize,
	  							contentWidth:this.scrollPane.paneWidth,
	  							contentHeight:this.scrollPane.paneHeight
	  						   }
  				);
  this.flagScrollMoved = true;
  this.repaint(false);
  if(this.editListener != null && this.editListener.active == true){
	  this.editListener.repaint();
  }
}
FastTable.prototype.updatePinnedColumns=function(){
	for(var i = this.firstVisibleColumn; i <= this.lastVisibleColumn; i++){
		if(i == null)
			break;
		var column = this.columns[i];
		column.headerElement.style.fontWeight='normal';
        applyStyle(column.headerElement,column.columnHeaderStyle);
		column.headerElement.style.zIndex='0';
		column.grabElement.style.zIndex='1';
	}
	for(var i = this.firstVisiblePinnedColumn; i <= this.lastVisiblePinnedColumn; i++){
		if(i == null)
			return;
		var column = this.columns[i];
        applyStyle(column.headerElement,column.columnHeaderStyle);
		column.headerElement.style.fontWeight='bold';
		column.headerElement.style.zIndex='1';
		column.grabElement.style.zIndex='2';
	}
}

FastTable.prototype.showContextMenu=function(menu){
   if(menu==null)
	   return;
   if(menu.curseqnum!=null && menu.curseqnum!=this.curseqnum)
	   return;
  this.createMenu(menu).show(this.contextMenuPoint);
}


FastTable.prototype.createMenu=function(menu){
   var that=this;
   var r=new Menu(getWindow(this.containerElement));
   if(this.contextMenuCurrentColumn == -1){
	   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e,id);} );
   }else if(this.contextMenuCurrentColumn != null){
	   r.createMenu(menu, function(e,id){that.onUserHeaderMenuItem(e,id,that.contextMenuCurrentColumn);} );
   }
   return r;
}

FastTable.prototype.showHelp=function(dom){
	var div=nw(div,'help_container');
	for(var i in dom){
		var group=dom[i];
		var titleDiv=nw('span','help_group');
		titleDiv.innerHTML=group.title;
		div.appendChild(titleDiv);
	    for(var j in group.items){
	    	var item=group.items[j];
	    	var entry=nw('span','help_entry');
	    	var title=nw('span','help_title');
	    	var delim=nw('span');
	    	delim.innerHTML=' - ';
	    	title.innerHTML=item.title;
	    	var text=nw('span','help_text');
	    	text.innerHTML=item.help;
	    	entry.appendChild(title);
	    	entry.appendChild(delim);
	    	entry.appendChild(text);
	    	div.appendChild(entry);
	    }
	}
	var d=new Dialog(div);
	d.setSize(500,500);
	d.setTitle("Help");
	d.addButton("close");
	d.setCanResize(true);
	d.show();
}

function Object2d(){
	this.data=new Map();
}
Object2d.prototype.data=null;
Object2d.prototype.width=0;
Object2d.prototype.height=0;

Object2d.prototype.getKey=function(col,row){
	return col*100000000+row;
}
Object2d.prototype.get=function(col,row){
	return this.data.get(this.getKey(col,row));
}
Object2d.prototype.set=function(col,row,val){
    var key=this.getKey(col,row);
	var curr = this.data.get(key);
	if(val==null)
		this.data.delete(key);
	else
		this.data.set(key,val);
	return curr;
}
Object2d.prototype.remove=function(col,row){
	this.data.delete(this.getKey(col,row));
}
Object2d.prototype.setSize=function(ncols,nrows){
	this.width=ncols;
	this.height=nrows;
}
Object2d.prototype.ensureSize=function(x,y){
	if(x+1 > this.width)
		this.setWidth(x+1);
	if(y+1 > this.height)
		this.setHeight(y+1);
}
Object2d.prototype.setWidth=function(w){
	this.width=w;
}
Object2d.prototype.setHeight=function(h){
	this.height=h;
}
Object2d.prototype.getWidth=function(){
	return this.width;
}
Object2d.prototype.getHeight=function(){
	return this.height;
}
Object2d.prototype.clear=function(){
	this.data.clear();
}

function FastTableEditor(tablePortlet){
	this.tablePortlet = tablePortlet;
	this.tablePortlet.table.editListener = this;
	this.active = false;
	this.focusedEdit = null;
}
FastTableEditor.prototype.EDIT_DISABLED=0;
FastTableEditor.prototype.EDIT_READONLY = 1;
FastTableEditor.prototype.EDIT_TEXTFIELD = 2;
FastTableEditor.prototype.EDIT_SELECT = 3;
FastTableEditor.prototype.EDIT_COMBOBOX = 4;
FastTableEditor.prototype.EDIT_DATERANGE_FIELD = 5;
FastTableEditor.prototype.EDIT_DATE_FIELD = 6;
FastTableEditor.prototype.EDIT_CHECKBOX = 7;
FastTableEditor.prototype.EDIT_NUMBERIC = 8;
FastTableEditor.prototype.EDIT_MASKED = 9;

FastTableEditor.prototype.focusClosestCellToMouse=function(){
	var r = new Rect(MOUSE_POSITION_X, MOUSE_POSITION_Y,1,1);
	var t = new Rect().readFromElementRelatedToWindow(this.tablePortlet.table.tableElement);
	r.left-=t.getLeft();
	r.top-=t.getTop();
	
	var col = this.tablePortlet.table.calcColFromLeft(r.left);
	var row = this.tablePortlet.table.calcRowFromTop(r.top);
	this.focusEditCell(col,row);
}


FastTableEditor.prototype.elementIsFullyVisibleX=function(cell){
	var col = cell.x;
	if(col < this.tablePortlet.table.pinning) {
		var x = this.tablePortlet.table.calcCellLeft(col);
		var w = this.tablePortlet.table.calcCellWidth(col);
		var vs = this.tablePortlet.table.scrollPane.vscrollVisible? this.tablePortlet.table.scrollSize:0;
		var tw = this.tablePortlet.table.width -w -vs;
		var pw = tw < pw? tw: this.tablePortlet.table.pinnedTable.offsetWidth;
		return (0 <= x && x < (pw))
	}
	else{
		var x = this.tablePortlet.table.calcCellLeft(col);
		var w = this.tablePortlet.table.calcCellWidth(col);
		var vs = this.tablePortlet.table.scrollPane.vscrollVisible? this.tablePortlet.table.scrollSize:0;
		return (0 <= x && x <= (this.tablePortlet.table.width - w - vs))
	}
}

FastTableEditor.prototype.elementIsFullyVisibleY=function(cell){
	var row = cell.y;
	var y = this.tablePortlet.table.calcCellTop(row);
	var h = this.tablePortlet.table.calcCellHeight(row);
	var hs = this.tablePortlet.table.scrollPane.hscrollVisible? this.tablePortlet.table.scrollSize:0;
	var hh = parseInt(this.tablePortlet.table.headerHeight);
	return (0 <= y && y < (this.tablePortlet.table.height -h -hs -hh))
}

FastTableEditor.prototype.focusEditCell=function(col,row){
	if(typeof this.colsMap[col] == "undefined"){
		col = this.cols[0];
	}
	var element = this.cellElements.get(col,row);
	if(typeof element =="undefined"){
		element = this.cellElements.get(this.cols[0],this.rows[0]);
	}
	if(typeof element !="undefined"){
		if(this.focusedEdit)
			this.focusedEdit.blur();
		this.focusedEdit = element;
		
		var colVisible = this.elementIsFullyVisibleX(element);
		var rowVisible = this.elementIsFullyVisibleY(element);
			
		if(!colVisible || !rowVisible){
			var rowPos = element.y * parseInt(this.tablePortlet.table.rowHeight);
			this.tablePortlet.callBack("make_visible",{c:element.x, r:element.y, rp:rowPos, snapCol:!colVisible, snapRow:!rowVisible});
		}
		else
			element.focus();
	}
}

FastTableEditor.prototype.edit=function(m){
	kmm.setActivePortletId(this.tablePortlet.portletId);
	if(m.length == 0)
		return;
	if(this.cols && this.rows && this.cols.length > 0 && this.rows.length > 0)
		this.deactivate();
	this.sortCells(m);
	this.cells = m;
	this.cellElements= new Object2d();
	this.valueElements= new Object2d();
	this.cols=[];
	this.rows=[];
	this.colsMap={};
	this.rowsMap={};
	this.createGlass();
	this.createDOM();
	this.createCellDOM();
	this.active = true;
	this.repaint();
	this.focusClosestCellToMouse();
}
FastTableEditor.prototype.sortCells=function(m){
	m.sort(function(a,b){ return a.x -b.x || a.y - b.y; });
}
FastTableEditor.prototype.removeElements=function(){
	for(var i = 0; i < this.cols.length; i++){
		var x = this.cols[i];
		for(var j = 0; j < this.rows.length; j++){
			var y = this.rows[j];
			var e = this.cellElements.get(x,y);
			e.parentElement.removeChild(e);
		}
	}
}

FastTableEditor.prototype.deactivate=function(){
	if(this.active == false)
		return;
	this.hideGlass();
	this.tablePortlet.table.headerElement.style.pointerEvents="initial";
	this.tablePortlet.headerDiv.style.pointerEvents="initial";
	this.removeElements();
	this.cells = [];
	this.cols=[];
	this.rows=[];
	this.colsMap={};
	this.rowsMap={};
	this.cellElements.clear();
	this.cellElements = null;
	this.element.removeChild(this.movingElement);
	this.element.removeChild(this.pinnedElement);
	this.tablePortlet.table.tableElement.removeChild(this.element);
	this.focusedEdit = null;
	this.movingElement = null;
	this.pinnedElement = null;
	this.element = null;
	this.active=false;
	if(currentContextMenu)
		currentContextMenu.hideAll();
}
FastTableEditor.prototype.cancelEdit=function(){
	this.deactivate();
	this.tablePortlet.callBack("rows_edit_cancel",{});
}
FastTableEditor.prototype.sendRows=function(){
	var c = [];
	for(var i = 0; i < this.cells.length; i++){
		c[i] = {};
		c[i].x = this.cells[i].x;
		c[i].y = this.cells[i].y;
		if(c[i].x >= 0)
			c[i].v = this.cellElements.get(this.cells[i].x, this.cells[i].y).value;
		else
			c[i].v = this.cells[i].v;
	}
	this.tablePortlet.callBack("rows_edit", {cells:JSON.stringify(c), submit:true});
}
FastTableEditor.prototype.sendCell=function(ce){
	//ce cellElement
	var c = [{x:ce.x,y:ce.y,v:ce.value}];
	this.tablePortlet.callBack("rows_edit", {cells:JSON.stringify(c)});
}
FastTableEditor.prototype.cellHandleKeydown=function(e){
	if(this.focusedEdit != null && this.focusedEdit.handleKeydown!=null){
		return this.focusedEdit.handleKeydown(e);
	}
	return false;
}
FastTableEditor.prototype.handleKeydown=function(e, _target){
	var shiftKey = e.shiftKey;
	var ctrlKey = e.ctrlKey;
	var altKey = e.altKey;
	if(this.cellHandleKeydown(e)){
		
	}
	else{
		switch(e.key){	
			case "Enter":
				if(shiftKey == false && altKey == false && ctrlKey == false)
					this.sendRows();
				break;
			case "Escape":
				this.cancelEdit();
				break;
			case "Tab":
				this.handleTab(e, shiftKey);
				break;
			case "ArrowLeft":
				if(shiftKey)
					this.handleArrow(e,-1,0);
				break;
			case "ArrowRight":
				if(shiftKey)
					this.handleArrow(e,1,0);
				break;
			case "ArrowUp":
				if(shiftKey)
					this.handleArrow(e,0,-1);
				break;
			case "ArrowDown":
				if(shiftKey)
					this.handleArrow(e,0,1);
				break;
			default:
				break;	
		}
	}
}

FastTableEditor.prototype.handleArrow=function(e,xdir, ydir){
	e.preventDefault();
	e.stopPropagation();
	if(this.focusedEdit){
		var col = this.focusedEdit.x;
		var row = this.focusedEdit.y;
		if(xdir != 0){
			var cIndex = this.colsMap[col] + xdir;
			if(0 <=cIndex && cIndex < this.cols.length){
				col = this.cols[cIndex];
			}
			else return;
		}
		if(ydir != 0){
			var rIndex = this.rowsMap[row] + ydir;
			if(0 <=rIndex && rIndex < this.rows.length){
				row = this.rows[rIndex];
			}
			else return;
		}
		this.focusEditCell(col,row);
	}
}
FastTableEditor.prototype.handleTab=function(e, reverse){
	if(this.focusedEdit){
		this.focusNextEdit(this.focusedEdit.x,this.focusedEdit.y, true, reverse);
	}
	e.preventDefault();
	e.stopPropagation();
}

FastTableEditor.prototype.focusNextEdit=function(x, y, horizFirst, reverse){
	var cIndex = this.colsMap[x];
	var rIndex = this.rowsMap[y];
	if(typeof cIndex =="undefined" || typeof rIndex == "undefined"){
		return;
	}
	if(horizFirst == true){
		if(reverse == false){
			if(cIndex == (this.cols.length-1)){
				cIndex = 0;
				if(rIndex == (this.rows.length-1)){
					rIndex = 0;
				}
				else
					rIndex++;
			}
			else
				cIndex++;
		}
		else{
			if(cIndex == 0){
				cIndex = this.cols.length-1;
				if(rIndex == 0){
					rIndex = this.rows.length-1;
				}
				else
					rIndex--;
			}
			else
				cIndex--;
		}
	}
	else{
		//What key to use
	}
	this.focusEditCell(this.cols[cIndex],this.rows[rIndex]);
}
FastTableEditor.prototype.createCellDOM=function(){
	for(var i = 0; i < this.cells.length; i++){
		var cell = this.cells[i];
		if(cell.x < 0)
			continue;
		if(typeof this.colsMap[cell.x] == "undefined"){
			this.colsMap[cell.x]=this.cols.length;
			this.cols.push(cell.x);
		}
		if(typeof this.rowsMap[cell.y] == "undefined"){
			this.rowsMap[cell.y]=this.rows.length;
			this.rows.push(cell.y);
		}
		
		var element;
		var that = this;
		var changeFunc = function(){
			that.sendCell(this);
		};
//		if(cell.t == )
		if(cell.t == this.EDIT_DISABLED){
			//Disabled Do Nothing
		}
		else if(cell.t == this.EDIT_READONLY){
			//Readonly Do Nothing
		}
		else if(cell.t == this.EDIT_TEXTFIELD){
			//TextField
			element = nw("input","editCell");
			element.oninput=changeFunc;
		}
		else if(cell.t == this.EDIT_SELECT){
			//Select
			element = nw("select","editCell");
			element.onchange=changeFunc;
			var olist = cell.o;
			if(!olist.includes(cell.v))
				olist.unshift(cell.v);
//			olist.sort();
			for(var m = 0; m < olist.length; m++){
				var option = nw("option");
				var orginalText = olist[m].replace(/^\s+/, match => '&nbsp;'.repeat(match.length)).replace(/\s+$/, match => '&nbsp;'.repeat(match.length))
				option.innerHTML = orginalText;
				element.options.add(option);
			}
		}
		else if(cell.t == this.EDIT_COMBOBOX){
			//Combo
			element = nw("combo-box","editComboBox");
			element.onchange=changeFunc;
			element.setMinWidth(125);
			var olist = cell.o;
			//Don't need to add current value;
//			if(!olist.includes(cell.v))
//				olist.push(cell.v);
//			olist.sort();
			for(var m = 0; m < olist.length; m++){
				element.addOption(olist[m]);
			}
			element.setValue(cell.v);
		}
		else if (cell.t == this.EDIT_CHECKBOX) {
			element = nw("div", "editCheckbox editCell");
			var editableCheckbox = new CheckboxField();
			editableCheckbox.setValue(cell.v);
			element.checkboxField = editableCheckbox;
			var toggle = function(e) {
				var element = this.parentElement; 
				var editableCheckbox = element.checkboxField;
				editableCheckbox.toggleChecked();
				element.value = editableCheckbox.getValue();
				this.value = element.value + "";
				this.x = cell.x;
				this.y = cell.y;
				that.sendCell(this);
			};
			editableCheckbox.element.onmousedown=toggle;
			var height = this.tablePortlet.table.calcCellHeight(cell.y);
			editableCheckbox.setSize(height - 5, height - 5);
			editableCheckbox.element.style.margin = "1px 0 0";
			element.appendChild(editableCheckbox.element);
		} else if (cell.t == this.EDIT_MASKED) {
			element = nw("input", "editMasked editCell");
			element.setAttribute("type", "password");
			element.oninput=changeFunc;
		}
		else if (cell.t == this.EDIT_DATE_FIELD) {
			element = nw("div", "editDate");
			var chooser = new DateChooser(element, false);
			chooser.input.classList.add("date_edit");
			chooser.setDisableFutureDays([cell.dfd,false]);
			chooser.setEnableLastNDays([cell.lnd,null]);
			// hack the default calendar styles in
			chooser.setHoverBgColor("#CCCCCC");
			chooser.setColors("#a9a9a9");
			chooser.setOnEdit(true);
			chooser.onClickDay=chooser.handleClickFromEditCell;
			
			chooser.onChange=changeFuncCustom;
			var changeFuncCustom=function(e){
				this.value = chooser.getValue();
				this.x = cell.x;
				this.y = cell.y; 
				that.sendCell(this);
			};
			
			//double click on cell: grab the content from the underlying cell, TRY parsing the date and set the chooser input.
			try {
				chooser.setValue(cell.v);
			} catch (err) {
				// invalid cell value. expecting format: cell.v = "20201205 - 20201210"
			}
			// Enter Key
			chooser.hideGlass=function() {
				kmm.setActivePortletId(that.tablePortlet.portletId,false);
			}
		}
		else if (cell.t == this.EDIT_DATERANGE_FIELD) {
			element = nw("div", "editDateRange");
			var chooser = new DateChooser(element, true);
			chooser.input2.style.display="none";
			chooser.dash.style.display="none";
			chooser.input.classList.add("daterange_edit");
			// TODO ADD disable future day and enable last N day for end calendar too
			chooser.setDisableFutureDays([cell.dfd,false]);
			chooser.setEnableLastNDays([cell.lnd,null]);
			// hack the default calendar styles in
			chooser.setHoverBgColor("#CCCCCC");
			chooser.setColors("#a9a9a9");
			chooser.setOnEdit(true);
			chooser.onClickDay=chooser.handleClickFromEditCell;
			
			chooser.onChange=changeFuncCustom;
			var changeFuncCustom=function(e){
				this.value = chooser.getValue();
				this.x = cell.x;
				this.y = cell.y; 
				that.sendCell(this);
			};
			
			//double click on cell: grab the content from the underlying cell, TRY parsing the dates and set the chooser input.
			try {
				var dates = cell.v.split("-");
				chooser.setValue(dates[0].trim());
				chooser.setValue2(dates[1].trim());
				chooser.input.value = cell.v;
			} catch (err) {
				// invalid cell value. expecting format: cell.v = "20201205 - 20201210"
			}
			// Enter Key
			chooser.hideGlass=function() {
				kmm.setActivePortletId(that.tablePortlet.portletId,false);
			}
		}
		else{
			//TextField and NumericField
			element = nw("input","editCell");
			element.oninput=changeFunc;
		}
		element.value = cell.v;
		var cellClassName = this.tablePortlet.table.columns[cell.x].cellClassName;
		element.classList.add(cellClassName);
		element.tabIndex=-1;
		element.x = cell.x;
		element.y = cell.y;
		element.isPinned = cell.x < this.tablePortlet.table.pinning;
		this.cellElements.set(cell.x, cell.y, element);
		if(element.isPinned)
			this.pinnedElement.appendChild(element);
		else
			this.movingElement.appendChild(element);
	}
}

FastTableEditor.prototype.repaint=function(){

	//Sets flag to say the focused cell wasn't in view
		
	//Set position
	for(var i = 0; i < this.cells.length; i++){
		var cell = this.cells[i];
		var element = this.cellElements.get(cell.x,cell.y);
		if(cell.x < 0)
			continue;
		if(this.cells[i].y < this.tablePortlet.table.upperRow  || this.cells[i].y > this.tablePortlet.table.lowerRow){
			element.style.display="none";
			element.isVisible = false;
			continue;
		}
		if(cell.x >= this.tablePortlet.table.pinning){
			if(this.cells[i].x < this.tablePortlet.table.firstVisibleColumn || this.cells[i].x > this.tablePortlet.table.lastVisibleColumn){
				element.style.display="none";
				element.isVisible = false;
				continue;
			}
		}else{
			if(this.cells[i].x < this.tablePortlet.table.firstVisiblePinnedColumn || this.cells[i].x > this.tablePortlet.table.lastVisiblePinnedColumn){
				element.style.display="none";
				element.isVisible = false;
				continue;
			}
		}
		var left = this.tablePortlet.table.calcCellLeft(cell.x);
		var top = this.tablePortlet.table.calcCellTop(cell.y);
		var width = this.tablePortlet.table.calcCellWidth(cell.x);
		var height = this.tablePortlet.table.calcCellHeight(cell.y);
		if(cell.x >= this.tablePortlet.table.pinning){
			left -= this.tablePortlet.table.totalPinnedWidth;
		}
		element.isVisible = true;
		element.style.display = "initial";	
		element.style.left = toPx(left);
		element.style.top = toPx(top);
		element.style.width = toPx(width);
		element.style.height = toPx(height);
	}
	
	if(this.focusedEdit && this.focusedEdit.isVisible){
		var colVisible = this.elementIsFullyVisibleX(this.focusedEdit);
		var rowVisible = this.elementIsFullyVisibleY(this.focusedEdit);
		if(colVisible && rowVisible){
			this.focusedEdit.focus();
		}
	}
	
	//Not as important, apply alignment
	for(var i = 0; i < this.cells.length; i++){
		var cell = this.cells[i];
		if(cell.x < 0)
			continue;
		var element = this.cellElements.get(cell.x,cell.y);
		if(this.cells[i].y < this.tablePortlet.table.upperRow  || this.cells[i].y > this.tablePortlet.table.lowerRow){
			continue;
		}
		element.style.justifyContent=this.tablePortlet.table.verticalAlign;
	}
}

FastTableEditor.prototype.createGlass=function(){
	var that = this;
	this.disabledGlassDiv=nw('div', "disable_glass_clear");
	this.tablePortlet.table.scrollPaneElement.style.zIndex=10000;
	this.disabledGlassDiv.onclick = function(e){ 
	    that.sendRows(); 
	    that.disabledGlassDiv.style.pointerEvents='none';
	    var target=getDocument(e.target).elementFromPoint(e.clientX,e.clientY);
	    if(target!=null)
	      target.dispatchEvent(new MouseEvent(e.type,e));
	};
	this.tablePortlet.table.containerElement.appendChild(this.disabledGlassDiv);
}
FastTableEditor.prototype.hideGlass=function(){
	this.tablePortlet.table.containerElement.removeChild(this.disabledGlassDiv);
	this.disabledGlassDiv=null;
	this.tablePortlet.table.scrollPaneElement.style.zIndex=null;
}
FastTable.prototype.createGlass=function(){
	var that = this;
	this.disabledGlassDiv=nw('div', "disable_glass_clear");
	this.disabledGlassDiv.style.zIndex=10000
	this.scrollPaneElement.style.zIndex=10000;
	this.tableElement.appendChild(this.disabledGlassDiv);
}
FastTable.prototype.hideGlass=function(){
	var count = this.tableElement.childElementCount;
	const elementsToRemove = this.tableElement.querySelectorAll(".disable_glass_clear");
	elementsToRemove.forEach(element => {this.tableElement.removeChild(element);});
	this.disabledGlassDiv=null;
	this.scrollPaneElement.style.zIndex=null;
}
FastTableEditor.prototype.createDOM=function(){
	this.element = nw("div","tableEditor"); 
	this.tablePortlet.table.tableElement.appendChild(this.element);
	this.movingElement = nw("div", "tableEditorMoving");
	this.pinnedElement = nw("div", "tableEditorPinned");
	this.element.appendChild(this.movingElement);
	this.element.appendChild(this.pinnedElement);
	this.movingElement.style.left=toPx(this.tablePortlet.table.totalPinnedWidth);
	this.movingElement.style.width=toPx(this.tablePortlet.table.width - this.tablePortlet.table.totalPinnedWidth);
	this.pinnedElement.style.width=toPx(this.tablePortlet.table.totalPinnedWidth);
	if(!(this.tablePortlet.table.pinning > 0)){
		this.pinnedElement.style.display="none";
	}
	this.movingElement.style.top="0px";
	this.pinnedElement.style.top="0px";
	this.pinnedElement.style.left="0px";
	
	this.movingElement.tabIndex=-1;
	this.pinnedElement.tabIndex=-1;
	this.element.tabIndex=-1;
	
	this.tablePortlet.table.headerElement.style.pointerEvents="none";
	this.tablePortlet.headerDiv.style.pointerEvents="none";
	var that = this;
//	this.element.onclick = function(e){ if(e.target== that.element){that.focusedEdit=null;that.sendRows();} else if(e.target.isVisible == true) that.focusedEdit = e.target};
	this.movingElement.onclick = function(e){ if(e.target== that.movingElement){that.focusedEdit=null;that.sendRows();} else if(e.target.isVisible == true) that.focusedEdit = e.target};
	this.pinnedElement.onclick = function(e){ if(e.target== that.pinnedElement){that.focusedEdit=null;that.sendRows();} else if(e.target.isVisible == true) that.focusedEdit = e.target};
}
