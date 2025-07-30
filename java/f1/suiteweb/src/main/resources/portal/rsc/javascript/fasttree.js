function FastTree(element) {
	var that = this;
	this.valueElements=new Object2d();
	this.treeElement = nw("div");
	this.treeElement.style.height = '100%';
	this.treeElement.style.width = '100%';

	this.data = [];
	this.visibleRowsByUid = {};// uid -> data
	this.containerElement = element;
	this.scrollPaneElement = nw("div");
	this.containerElement.appendChild(this.scrollPaneElement);
	this.scrollPane = new ScrollPane(this.scrollPaneElement, this.scrollSize, this.treeElement);
    this.scrollPane.onScroll=function(){that.onScroll()};
	this.visibleRowsCount = 0;
	this.headerElement = nw("div");
	this.headerElement.className = "header";
	var startX;
	const delta = 6;
	this.headerElement.addEventListener('mousedown', function(e){
		startX = e.pageX;
	});
	
	this.headerElement.addEventListener('mouseup', function(e){
		const diffx = Math.abs(e.pageX-startX);
		if (diffx < delta) {
			that.onHeaderClicked(e);
		}
	});
	
	this.containerElement.appendChild(this.headerElement);
	this.treeElement.tabIndex = 0;
	this.treeElement.style.outline = 'none';

	this.pinnedBorderElement = nw("div");
	this.pinnedBorderWidth = 5;
	this.pinnedBorderElement.style.display = "none";
	this.pinnedBorderElement.style.zIndex = 1;
	this.pinnedBorderElement.style.pointerEvents = "none";
	this.pinnedBorderElement.style.width = toPx(this.pinnedBorderWidth);
	this.pinnedBorderElement.style.borderWidth = "0px 2px 0px 2px";
	this.pinnedBorderElement.style.borderStyle = "inset";
	this.pinnedBorderElement.style.borderColor = "#999999";
	this.pinnedBorderElement.style.background = "#b3b3b3";
	this.containerElement.appendChild(this.pinnedBorderElement);
	this.columns = [];
	this.pinning = 2;
	this.defaultHeaderHeightPx = 18;
	this.headerHeightPx = this.defaultHeaderHeightPx;
	this.defaultSearchBarHeight = 20;
	this.searchBarHeight = this.defaultSearchBarHeight;
	this.headerBarHidden = false;
	this.searchBarHidden = false;
	this.hideHeaderDivider = false;
    this.onCellMouseUpFunc=function(e){return that.onCellMouseUp(e);};
    this.onCellMouseDownFunc=function(e){e.stopPropagation();return that.onCellMouseDown(e);};
    this.onCellMouseMoveFunc=function(e){return that.onCellMouseMove(e);};
    this.treeElement.onmousedown=function(e){e.stopPropagation();return that.onWhiteSpaceMouseDown(e);};
	this.selectedRows = new SelectedRows();
	this.currentRow = -1;
	this.quickColumnFilterHeight = this.rowHeight+10;
	this.quickColumnFilterHidden = true;//quickfilter is displayed
    this.selectedClassname='cell_selected_default';
    this.activeClassname='cell_active_default';
    
    //MOBILE SUPPORT - FOR DRAG SCROLL
    this.longTouchTimer;
    this.isSelecting = false;
    this.isDragging = false;
    this.currPoint = new Point(0,0);
    this.currTarget;
    this.treeElement.ontouchstart=function(e) { that.createGlass();that.onTouchDragStart(e);};
    this.treeElement.ontouchmove=function(e) { that.onTouchDragMove(e); that.onScroll();};
    this.treeElement.ontouchend= function(e){ that.isSelecting=false; clearTimeout(that.longTouchTimer); that.longTouchTimer=null; that.hideGlass();};
}
FastTree.prototype.cornerSize = 15;
FastTree.prototype.activeRowUid = -1;
FastTree.prototype.rowHeight = 15;
FastTree.prototype.leftPaddingPx = 4;
FastTree.prototype.topPaddingPx = 4;
FastTree.prototype.defaultFontSize = "18px";
FastTree.prototype.defaultHeaderHeightPx;
FastTree.prototype.headerHeightPx = 0;
FastTree.prototype.defaultSearchBarHeight;
FastTree.prototype.searchBarHeight;
FastTree.prototype.upperRow = 0;
FastTree.prototype.scrollSize = 15;
FastTree.prototype.treatNameClickAsSelect = true;
FastTree.prototype.hideCheckBoxes = false;
FastTree.prototype.selectedRows;
FastTree.prototype.selectedClassname;
FastTree.prototype.activeClassname;

FastTree.prototype.quickColumnFilterHidden;
FastTree.prototype.quickColumnBackgroundColor;
FastTree.prototype.quickColumnFontColor;
FastTree.prototype.quickColumnFontSz;
FastTree.prototype.quickColumnBorderColor;
FastTree.prototype.quickColumnFilterHeight;


FastTree.prototype.setQuickFilterAutocomplete=function(colPos,mapOfValuesOrNull){
	var col = this.columns[colPos];
	var oldVal = col.quickColumnFilterElement.value;
	col.quickColumnFilterElement.clearOptions2();
	for (let key in mapOfValuesOrNull) {
		col.quickColumnFilterElement.addOptionDisplayAction(key, mapOfValuesOrNull[key]);
	}
	col.quickColumnFilterElement.autocomplete(col.filterText);
};

FastTree.prototype.updateQuickFilterStyles=function(){
	if(this.quickColumnFilterHidden == true)
		return;
	for ( var x = 0; x < this.columns.length; x++) {
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

FastTree.prototype.setMetrics = function(rowHeightPx, leftPaddingPx, topPaddingPx, headerHeightPx) {
	this.rowHeight = rowHeightPx;
	this.leftPaddingPx = leftPaddingPx;
	this.topPaddingPx = topPaddingPx;
	this.headerHeightPx = headerHeightPx;
	this.setLocation(this.x, this.y, this.width, this.height);
}
FastTree.prototype.setLocation = function(x, y, width, height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;

	this.searchBarHeight = this.searchBarHidden ? 0 : this.defaultSearchBarHeight;
	if (this.headerBarHidden) {
		this.oldHeaderHeightPx = this.headerHeightPx;
		this.headerHeightPx = 0;
		}
		else {
		if (this.headerHeightPx == 0)
			this.headerHeightPx = this.oldHeaderHeightPx;
	}
	//TODOFILTER
	var quickFilterHeight = this.quickColumnFilterHidden == false?  this.quickColumnFilterHeight : 0 ;
	
	var scrollYPos = this.y + this.headerHeightPx + quickFilterHeight;
	var scrollHSize = this.height - this.headerHeightPx - this.searchBarHeight - quickFilterHeight;
		
	new Rect(x, y + this.searchBarHeight, width, height - this.searchBarHeight).writeToElement(this.containerElement);
	this.scrollPane.setLocation(this.x, scrollYPos, this.width, scrollHSize);
	this.updateColumns();
}

FastTree.prototype.updateColumns = function() {
	var rect = new Rect();
	this.updateHeaderBarHidden();
	if (this.hideHeaderDivider) {
		this.headerElement.style.backgroundImage = "none";
		this.headerElement.style.boxShadow = "none";
	} else {
		this.headerElement.style.backgroundImage = null;
		this.headerElement.style.boxShadow = null;
	}
	//TODOFILTER
	var fullHeaderHeight = this.quickColumnFilterHidden == false? this.headerHeightPx + this.quickColumnFilterHeight : this.headerHeightPx;
	new Rect(0, 0, Math.max(this.totalWidth, this.width), fullHeaderHeight).writeToElement(this.headerElement);
	for ( var i = 0; i < this.columns.length; i++) {
		//this.columns[i].headerElement.style.fontWeight = i < this.pinning ? 'bold' : 'normal';
		this.columns[i].headerElement.style.borderColor = this.cellBorderColor;
		this.columns[i].headerElement.style.fontSize = this.headerFontSize;
//		if (this.fontFamily != null)
//			applyStyle(this.columns[i].headerElement, this.fontFamily);
//		else
//			applyStyle(this.columns[i].headerElement, "_fm=arial");
		
		// this.columns[i].headerElement.style.justifyContent = this.verticalAlign;
		if (this.hideHeaderDivider) {
			this.columns[i].headerElement.style.backgroundImage = "none";
			this.columns[i].headerElement.style.boxShadow = "none";
		} else
			this.columns[i].headerElement.style.backgroundImage = null;

		this.updateColumnFilter(i);
		rect.setLocation(this.columns[i].offset, 0, this.columns[i].width, fullHeaderHeight).writeToElement(this.columns[i].headerElement);
		var box = this.getColumnBox(i);
		rect.setLocation(box.x - 5, 0, 7, fullHeaderHeight).writeToElement(this.columns[i].grabElement);
	}
	this.updateColumnWidths();
	this.updateAllWidths();
	this.updateBounds();
}
FastTree.prototype.updateColumnFilter=function(i){
	if(this.quickColumnFilterHidden == false){ //qf
		this.columns[i].quickColumnFilterElement.style.width=toPx(this.columns[i].width-4);
	}
	this.columns[i].quickColumnFilterElement.style.display = this.quickColumnFilterHidden == false?null:"none";
};
FastTree.prototype.getColumnAtPoint = function(x) {
	for ( var i = 0; i < this.columns.length; i++) {
		var col = new Rect().readFromElementRelatedToWindow(this.columns[i].headerElement);
		if (col.getLeft() <= x && col.getRight() >= x)
			return i;
	}
	return -1;
}

FastTree.prototype.updateBounds = function() {
	this.visibleRowsCount = Math.ceil(this.scrollPane.DOM.paneElement.clientHeight / this.rowHeight);
	new Rect(0, 0, max(this.totalWidth, this.width), this.height - this.scrollSize - this.rowHeight * 2).writeToElement(this.treeElement);
	this.determineColumnClipping();
	this.updateScrollTable()
	this.updateCells();
}

FastTree.prototype.visibleRowsByUid = {};// uid -> data
FastTree.prototype.toRemoveRows = []

FastTree.prototype.updateCells = function() {
	if (this.onUpdateCellsStart != null)
		this.onUpdateCellsStart(this);
	var bottomRow = this.upperRow + this.visibleRowsCount + 2;
	for ( var i in this.visibleRowsByUid) {
		var data = this.visibleRowsByUid[i];
		if (data.position < this.upperRow || data.position >= bottomRow){
			this.treeElement.removeChild(data.row);
			delete this.visibleRowsByUid[data.uid];
		}
	}
	var topCellsOffset = this.scrollPane.getClipTop();
	for ( var y =  this.upperRow; y < bottomRow; y++) {
		var data = this.data[y];
		if (data == null) 
			continue;
		var row=data.row;
		if (this.visibleRowsByUid[data.uid] == null) {
			this.treeElement.appendChild(row);
			this.visibleRowsByUid[data.uid] = data;
		}
		this.updateWidths(data);
		row.style.top = toPx(rd(this.rowHeight*y - topCellsOffset));
	}
	this.scrollPane.setPaneSize(this.totalWidth, this.totalHeight - this.topPaddingPx - this.headerHeightPx);
	if (this.onUpdateCellsEnd != null)
		this.onUpdateCellsEnd(this);
}

FastTree.prototype.clearData = function(start) {
	this.data.length = start;
	for ( var i in this.visibleRowsByUid) {
		var data = this.visibleRowsByUid[i];
		if (data.position >= start){
		  this.treeElement.removeChild(data.row);
		  delete this.visibleRowsByUid[data.uid];
		}
	}
}

FastTree.prototype.removeData = function(data) {
	delete this.data[data.position];
	if (this.visibleRowsByUid[data.uid] != null) {
		this.treeElement.removeChild(data.row);
		delete this.visibleRowsByUid[data.uid];
	}
}
FastTree.prototype.setDataInitRow = function(uid, position, depth, name) {
	var data = this.data[position];
	if (data != null && uid != data.uid) {
		this.removeData(data);
		data = null;
	}
	if (data == null) {
		var text = nw('div');
		var div = nw('div');
		var row = nw('div');
		div.appendChild(text);
		div.text = text;
		div.uid = uid;

		data = { uid : uid,
		name : name,
		depth : depth,
		position : position,
		row : row,
		div : div,
		cells : []
		};
		this.data[position] = data;
	}else {
		data.name=name;
	}
	return data;
};
FastTree.prototype.setDataInitCells = function(data, cnt) {
	var row = data.row;
	var position = data.position;
	if(data.cells.length > 0)
		return;
    // Inserting in reverse order allows columns to be pinned to the front.
	for ( var i = cnt - 1; i >= 0; i--) {
		var cell = nw('div', 'tree_cell');
		cell.colpos = i;
		cell.data=data;
		data.cells[i] = cell;
		cell.onmousedown = this.onCellMouseDownFunc;
		cell.onmousemove = this.onCellMouseMoveFunc;
		if(this.leftCol<=i && i<=this.rightCol+1)
		  row.appendChild(cell);
	}
	data.cells[0].appendChild(data.div);

};
FastTree.prototype.setDataAttachHandlers = function(data, hasChildren, iconClassName, iconStyle, hasCheckbox) {
	var div = data.div;
	var uid = data.uid;
	var that = this;
	if (!this.treatNameClickAsSelect)
		div.text.onmouseup = function(e) {
			that.onCellClicked(e, "nameclicked", uid);
		};
	else {
		div.text.onmouseup = function(e) {
			if (getMouseButton(e) == 2) { // right button
				that.onCellClicked(e, "nameclicked", uid);
			}
		};
	}
	if (hasChildren === true && div.expandIcon == null) {
		expandIcon = nw('div');
		div.appendChild(expandIcon);
		div.expandIcon = expandIcon;
		expandIcon.onmousedown = function(e) {
			e.stopPropagation();
			that.onCellClicked(e, "expand", uid);
		};
	}
	if (this.hideCheckBoxes == false && hasCheckbox === true && div.chkboxIcon == null) {
		chkboxIcon = nw('div');
		div.appendChild(chkboxIcon);
		div.chkboxIcon = chkboxIcon;
		chkboxIcon.onmousedown = function(e) {
			e.stopPropagation();
			that.onCellClicked(e, "checkbox", uid);
		};
	}
	if (iconClassName != null || iconStyle != null && div.customIcon == null) {
		customIcon = nw('div');
		div.appendChild(customIcon);
		div.customIcon = customIcon;
		if (!this.treatNameClickAsSelect)
			customIcon.onmousedown = function(e) {
				e.stopPropagation();
				that.onCellClicked(e, "nameclicked", uid);
			};
	}

};
FastTree.prototype.setDataValueStyle=function(arguments, data){
	for ( var i = 1, n = 15; n < arguments.length;) {
		var value2 = arguments[n++];
		var style2 = arguments[n++];
		var cell = data.cells[i];

		if (this.columns[i].jsFormatterType && this.columns[i].jsFormatterType === "spark_line") {
			if (cell.spark == null) {
				cell.spark = new InlineLineChart(cell, value2, ",");
				cell.spark.drawSvg(cell.clientWidth, cell.clientHeight);
			} else {
				cell.spark.setText(value2, ',');
				cell.spark.drawSvg(cell.clientWidth, cell.clientHeight);
			}
		} else {
			cell.innerHTML = value2;
		}

		cell.cellStyle = style2;
		applyStyle(cell, style2);
		i++;
	}
	
}
function getBg(val){
	var entries=(val==null) ? [] : val.split('\|');
	for(var i=0;i<entries.length;i++){
		var keyValue=entries[i].split(/=(.*)?/);
		var key=keyValue[0];
		var value=keyValue[1];
		if(key=="_bg")
			return value;
	}
	return null;
}
FastTree.prototype.setDataCellsStyle=function(data, cnt, style){
	if (this.fontSize != null)
		data.div.style.height = this.fontSize;
	else
		data.div.style.height = this.defaultFontSize;
	data.row.style.height = toPx(this.rowHeight);
	var position = data.position;
	for ( var i = cnt - 1; i >= 0; i--) {
		var cell = data.cells[i] ;
		if (this.selectedRows.isSelected(data.position)) {
			cell.className='';
			cell.classList.add("tree_cell");
			cell.classList.add(data.uid==this.activeRowUid ? this.activeClassname : this.selectedClassname);
		} else {
			cell.className = this.columns.length>1 && data.position % 2 ? 'tree_cell cell_greybar' : 'tree_cell cell_whitebar';
		}
		
		if(cell.cellStyle && (cell.cellStyle.includes("bg")) && getBg(cell.cellStyle)!=undefined)//add check for the cell background color is not undefined("|bg="). If it is undefined then go to else block
			applyStyle(cell, cell.cellStyle);
		else {
			if (position % 2 == 0)
				applyStyle(cell, this.bgColor);
			else
				applyStyle(cell, this.grayBarColor);
		}
		if (!cell.cellStyle || cell.cellStyle && !cell.cellStyle.includes("fg")) {
			applyStyle(cell, this.fontStyle);
		}
		cell.style.transition=this.flashStyle;
		cell.style.borderColor = this.cellBorderColor;
		cell.style.borderBottomWidth = this.cellBottomDivider;
		cell.style.borderRightWidth = this.cellRightDivider;
		cell.style.fontSize = this.fontSize;
		cell.style.justifyContent = this.verticalAlign;
		
		if(this.cellPadHt != null){
      		cell.style.paddingLeft = toPx(this.cellPadHt);
      		cell.style.paddingRight = toPx(this.cellPadHt);
  		}
	}
	applyStyle(data.cells[0], style);
}
	this.setDataTextIcons
FastTree.prototype.setDataTextIcons=function(data, expanded, cssClass, hasChildren, iconClassName, iconStyle, hasCheckbox, isChecked, hasChecked){
	var div = data.div;
	var expandIcon;
	var chkboxIcon;
	var customIcon;
	var text;
	// PROCESS TEXT
	text = div.text;
	text.innerHTML = data.name;
	if (cssClass)
		text.className = 'portal_tree_text ' + cssClass;
	else
		text.className = 'portal_tree_text';

	var left = 0;
	applyStyle(data.row, data.rowStyle);

	// PROCESS ICON
	expandIcon = div.expandIcon;
	chkboxIcon = div.chkboxIcon;
	customIcon = div.customIcon;
	if (hasChildren === true) {
		if (expanded === true) {
			expandIcon.innerHTML = "&#x25bc;"
			expandIcon.className = 'portal_tree_node_icon_expand';
		} else {
			expandIcon.innerHTML = "&#x25ba;"
			expandIcon.className = 'portal_tree_node_icon_collapse';
		}
		expandIcon.style.left = toPx(left);
		expandIcon.style.display="";
	}
	else{
		if(expandIcon != null)
			expandIcon.style.display="none";
	}
	left += 10;
	if (this.hideCheckBoxes == false && hasCheckbox === true) {
		chkboxIcon.className = 'portal_tree_node_icon_checkbox';
		if (isChecked) {
			chkboxIcon.innerHTML = "&#x2714;"// checkbox
			chkboxIcon.style.fontSize = '9px';
			chkboxIcon.style.padding = '0px 2px';
		} else if (hasChecked) {
			chkboxIcon.innerHTML = "&#x25fe;"// box
			chkboxIcon.style.fontSize = '7px';
			chkboxIcon.style.padding = '1px 3px';
		} else {
			chkboxIcon.innerHTML = ""// checkbox
		}
		chkboxIcon.style.border = '1px solid ' + div.style.color;
		chkboxIcon.style.width = '13px';
		chkboxIcon.style.height = '13px';
		chkboxIcon.style.top = '0px';
		chkboxIcon.style.left = toPx(left);
		left += 15;
	}
	if (customIcon != null) {
		customIcon.className = iconClassName == null ? "portlet_custom_icon" : ("portlet_custom_icon " + iconClassName);
		customIcon.style.left = toPx(left);
		applyStyle(customIcon, iconStyle);
		left += 18;
	}
	text.style.left = toPx(left);
}

FastTree.prototype.setData = function(uid, position, name, style, rowStyle, depth, expanded, cssClass, hasChildren, selected, iconClassName, iconStyle, hasCheckbox, isChecked,
		hasChecked) {
	var cnt = 1 + (arguments.length - 15) / 2;
	
	var data = this.setDataInitRow(uid, position, depth, name);
	data.style = style;
	data.formatValues = [] ;
	for(var i=15;i<arguments.length;i++){
		data.formatValues.push(arguments[i++]);
	}
	data.expanded = expanded;
	data.hasChildren = hasChildren;
	data.rowStyle = rowStyle;
	data.selected = selected;
	if (this.activeRowUid != -1 && this.activeRowUid == uid) {
		this.currentRow = position;
	}
	if (selected && !this.selectedRows.isSelected(position)) {
		this.selectedRows.add(position);
	} else if (!selected && this.selectedRows.isSelected(position)) {
		this.selectedRows.remove(position,null);
	}
	this.setDataInitCells(data, cnt);
	this.setDataCellsStyle(data, cnt, style);
	this.setDataAttachHandlers(data, hasChildren, iconClassName, iconStyle, hasCheckbox);
	this.setDataValueStyle(arguments, data);
	this.setDataTextIcons(data, expanded, cssClass, hasChildren, iconClassName, iconStyle, hasCheckbox, isChecked, hasChecked);
	this.setCellFlash(data, cnt,uid,style);
}

// borrowed from fastTable
FastTree.prototype.ensureRowVisible=function(row){
	var max= this.rowsCount-1;
	row=between(row,0,max);
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

	if(pos != null) {
		this.scrollPane.setClipTop(pos);
	}

}

FastTree.prototype.expandRow = function(e) {
	var selected = this.visibleRowsByUid[this.activeRowUid];
	if (selected.hasChildren) {
		this.onCellClicked(e, "expand", this.data[this.currentRow].uid);
	}
}

FastTree.prototype.handleKeyDown = function(e, diff) {
	var selected = this.visibleRowsByUid[this.activeRowUid];
	var nextPos = this.currentRow + diff;
	if (nextPos > this.rowsCount -1 || nextPos < 0) {
		return;
	}
	this.ensureRowVisible(nextPos);
	this.selectRow2(e, nextPos,null, false);
}

// remove previous row's active style and set current row's active style
FastTree.prototype.setActiveStyle = function(prev, cur) {
		var data = this.data[cur];
		var prevData = this.data[prev];
		var cells = data.cells;
		
		for (var i = 0; i < cells.length; i++){
			cells[i].className = "";
			cells[i].classList.add("tree_cell");
			cells[i].classList.add(this.activeClassname);
		}
		if (prevData != null) {
			var prevCells = prevData.cells;
			if (this.isSelected(prevData)) {
				for (var i = 0; i < prevCells.length; i++){
					prevCells[i].classList.remove(this.activeClassname);
					prevCells[i].classList.add(this.selectedClassname);
				}
			} else {
				for (var i = 0; i < prevCells.length; i++){
					prevCells[i].classList.remove(this.selectedClassname);
					prevCells[i].classList.remove(this.activeClassname);
				}
			}
		}
}

// different signature than selectRow()
FastTree.prototype.selectRow2 = function(e, row, col, isDragging) {
	var ctrlKey = e.ctrlKey;
	var shiftKey = e.shiftKey;
	var before = this.selectedRows.toString();
	var beforeCurrentRow = this.currentRow;
	if (!isDragging && this.dragSelect) {
		this.dragStart = row;
	}
	if (ctrlKey && shiftKey) {
		if (this.currentRow == -1)
			this.selectedRows.remove(row);
		else if (this.currentRow < row)
			this.selectedRows.remove(this.currentRow, row);
		else
			this.selectedRows.remove(row, this.currentRow);
	} else if (ctrlKey) {
		// this part is different from selectRow()
		if (beforeCurrentRow == -1) {
			return;
		}
		// if the next row is selected and the previous row is different from the next row
		if (this.selectedRows.isSelected(row)) {
			this.selectedRows.remove(beforeCurrentRow);
		} else  {
			this.selectedRows.add(row);
		} 
	} else if (shiftKey) {
		if(this.currentRow==-1)
    		this.selectedRows.add(row);
    	else if(this.currentRow<row)
    		this.selectedRows.add(this.currentRow,row);
    	else
    		this.selectedRows.add(row,this.currentRow);
	} else if (this.dragSelect) {
		this.selectedRows.clear();
		if (row > this.dragStart)
			this.selectedRows.add(this.dragStart, row);
		else
			this.selectedRows.add(row, this.dragStart);
	} else {
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
	var after = this.selectedRows.toString();
	if (this.currentRow != beforeCurrentRow) {
		this.setActiveStyle(beforeCurrentRow, this.currentRow);		
	}
	this.activeRowUid = this.currentRow!= -1?this.data[this.currentRow].uid:-1;
	
	if (after!=before || this.currentRow!=beforeCurrentRow)
		this.onUserSelected(e, this.activeRowUid, after, col, this.activeRowUid);

	return false;
}

FastTree.prototype.updatePinnedColumns = function() {
	if (!this.pinning)
		return;
	//Moves column headers to pinned location
    var h=Math.round(this.scrollPane.getClipLeft());
    var rect=new Rect();
    //QuickFilter
	var fullHeaderHeight = this.quickColumnFilterHidden == false? this.headerHeightPx + this.quickColumnFilterHeight : this.headerHeightPx;
    for(var i=0;i<this.pinning && i<this.columns.length;i++){
      rect.setLocation(h+this.columns[i].offset,0,this.columns[i].width,fullHeaderHeight).writeToElement(this.columns[i].headerElement);
      var box = this.getColumnBox(i);
      rect.setLocation(h+box.x-5,0,7,fullHeaderHeight).writeToElement(this.columns[i].grabElement);
    }
}

FastTree.prototype.updateAllWidths = function() {
	var rowsCount = this.visibleRowsCount + 2;
	var that = this;
	for ( var cellY = 0; cellY < this.visibleRowsCount + 2; cellY++) {
		var y = cellY + this.upperRow;
		var data = this.data[y];
		if (data != null)
			this.updateWidths(data);
	}
}
FastTree.prototype.updateWidths = function(data) {
	var div = data.div;
	var left = data.depth + this.leftPaddingPx;
	var cells = data.cells;
	if (this.columns.length > 1) {
		var width = this.columns[0].width;
		if (width <= left) {
			left = width - 1;
		}
		div.style.left = toPx(left);
		div.style.width = toPx(width - left);
	} else {
		div.style.left = toPx(left);
		div.style.width = '100%';
	}
	var isSelected=this.isSelected(data);
	var isActive=data.uid==this.activeRowUid;
	data.row.style.width = this.columns.length>1 ? toPx(this.totalWidth) : '100%';
	for ( var x = 0; x < this.columns.length; x++) {
		var pos = 0;
		var cell = data.cells[x];
	    if(x<this.pinning || this.leftCol<=x && x<=this.rightCol+1){
	    	var col=this.columns[x];
	        if(cell.parentNode!=data.row)
	            data.row.appendChild(cell);
		    if(isSelected){
				var wasActive = !isActive && cell.className.includes(this.activeClassname);
				cell.className = "";
				cell.classList.add("tree_cell");
				cell.classList.add(isActive ? this.activeClassname : this.selectedClassname);
				cell.style.transition = cell.isFlashing == true && !wasActive? cell.style.transition : '';
		    } else {
				cell.className = this.columns.length>1 && data.position % 2 ? 'tree_cell cell_greybar' : 'tree_cell cell_whitebar';
		    }
		    if(data.rowStyle){
			  applyStyle(cell, data.rowStyle);
			  applyStyle(cell, cell.cellStyle);
		    }
		    if (x < this.pinning){
				cell.style.left=toPx(col.offset + this.scrollPane.getClipLeft());
				cell.style.zIndex=100;
		    }else 
		        cell.style.left=toPx(col.offset);
		    cell.style.width=this.columns.length >1 ? toPx(col.width) : '100%';
		    if (this.columns[x].jsFormatterType && this.columns[x].jsFormatterType === "spark_line") {
			    var value = cell.textContent;
			    if (cell.spark == null) {
			    } else {
				    cell.spark.drawSvg(cell.clientWidth, cell.clientHeight);
			    }
		    }else if (this.columns[x].jsFormatterType && this.columns[x].jsFormatterType === "checkbox") {
				var value = data.formatValues[x-1];
				var style = data.style;
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
			}
	    }else if(cell.parentNode==data.row)
	            data.row.removeChild(cell);
	}
}

FastTree.prototype.applyCellCheckboxConfig=function(cell, value, style) {
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
	cell.checkbox.setSize(cell.clientHeight - parseInt(this.cellBottomDivider, 10) - 5, cell.clientHeight - parseInt(this.cellBottomDivider, 10) - 5);
	cell.checkbox.element.style.margin = "2px 0px 0px";
	cell.appendChild(cell.checkbox.element);
}


FastTree.prototype.isSelected = function(data) {
	if (this.mouseDraggingStartPos == null)
		return data.selected;
	if (data.position >= this.mouseDraggingStartPos && data.position <= this.mouseDraggingEndPos)
		return true;
	if (data.position >= this.mouseDraggingEndPos && data.position <= this.mouseDraggingStartPos)
		return true;
	return false;
}

//MOBILE SUPPORT - create glass for scroll
FastTree.prototype.createGlass=function(){
	var that = this;
	this.disabledGlassDiv=nw('div', "disable_glass_clear");
	this.scrollPaneElement.style.zIndex=10000;
	this.treeElement.appendChild(this.disabledGlassDiv);
}
FastTree.prototype.hideGlass=function(){
	var count = this.treeElement.childElementCount;
	const elementsToRemove = this.treeElement.querySelectorAll(".disable_glass_clear");
	elementsToRemove.forEach(element => {this.treeElement.removeChild(element);});
	this.disabledGlassDiv=null;
	this.scrollPaneElement.style.zIndex=null;
}

//MOBILE SUPPORT - on touch start
FastTree.prototype.onTouchDragStart=function(e){
	this.currTarget = getMouseTarget(e);
	this.currPoint = getMousePoint(e);
	// setting timer for context menu
	this.longTouchTimer = setTimeout(() => {
		this.isSelecting = true;
		this.hideGlass();
		this.selectRow2(e,this.currTarget.data.position,this.currTarget.colpos,false);
  }, 500); // Long touch threshold (500ms)

}

FastTree.prototype.onTouchDragMove=function(e){
	//drag seelct. 
	if(this.isSelecting === true){
		this.hideGlass();
		var t = getMousePoint(e);
		var t1 = document.elementFromPoint(t.x, t.y);
		if(t1 == null)
			return;
		this.dragSelect = true;
		this.selectRow2(e,t1.data.position,t1.colpos,true);
	}
	//drag scroll
	else{
		clearTimeout(this.longTouchTimer);
		this.longTouchTimer=null;
		var that = this;
		if(e.target != document.querySelector(".disable_glass_clear")){
			var newRef = this.treeElement.querySelectorAll(".disable_glass_clear");
			that.target = newRef[0];
		}
		
		var diffx = that.currPoint.x - getMousePoint(e).x;
		var diffy = that.currPoint.y - getMousePoint(e).y;
		that.scrollPane.hscroll.goPage(0.01 * diffx);
		that.scrollPane.vscroll.goPage(0.01 * diffy);
		that.currPoint = getMousePoint(e);
		
	}
}

FastTree.prototype.onCellMouseUp = function(e) {
	this.stopOutsideDragging();
	this.dragSelect = false;
	var doc = getDocument(this.containerElement).onmouseup = null;
}
FastTree.prototype.onCellMouseMove = function(e) {
	var cell = getMouseTarget(e);
	var data=cell.data;
	var shiftKey = e.shiftKey;
	var ctrlKey = e.ctrlKey;
	while (data == null) {
		cell = cell.parentNode;
		if (cell == null)
			return;
		data = cell.data;
	}
	var col = cell.colpos;
	if (this.columns[col].clickable && !shiftKey && !ctrlKey && e.buttons == 0 && this.selectedRows.isSelected(data.position)) {
		var top = this.getUpperRowVisible();
		var bot = this.getLowerRowVisible();
		while (top <= bot) {
			var cell = this.cellElements.get(col, top - this.upperRow);
			if (cell != null) {
				if (this.selectedRows.isSelected(top)) {
					cell.style.textDecoration = 'underline';
					cell.style.cursor = 'pointer';
				} else {
					cell.style.textDecoration = null;
					cell.style.cursor = null;
				}
			}
			top++;
		}
	}
}
FastTree.prototype.onCellMouseDown = function(e) {
	var that = this;
	var shiftKey = e.shiftKey;
	var ctrlKey = e.ctrlKey;
	var cell = getMouseTarget(e);
	var data = cell.data;
	while (data == null) {
		cell = cell.parentNode;
		if (cell == null)
			return;
		data = cell.data;
	}
	var col = cell.colpos;
	
	var doc = getDocument(this.containerElement);
	doc.onmouseup = this.onCellMouseUpFunc;
	doc.onmousemove=function(e){that.onOutsideMouseDragging(e);};
	var button = getMouseButton(e);
	if (button == 2) { // right click
		if (!this.selectedRows.isSelected(data.position)) {
			this.selectedRows.clear();
			this.selectRow(data.position, col, shiftKey, ctrlKey, false);
		} 
		this.contextMenuPoint = getMousePoint(e).move(-4, -4);
		this.contextMenuCurrentColumn = -1;
		var col = cell.colpos;
		if (this.onUserSelected)
			this.onUserSelected(null, this.activeRowUid, this.selectedRows.toString(), col);
	} else {
		if (!ctrlKey && !shiftKey) {
			var clickable = (this.columns[col].clickable && this.selectedRows.isSelected(row));
			if (clickable)
				return;
		}
		if (!e.shiftKey && !e.ctrlKey) {
			this.dragSelect = true;
		}
		this.selectRow(data.position, col, shiftKey, ctrlKey, false);
	}
}
FastTree.prototype.onOutsideMouseDragging = function(e) {
	var point = getMousePoint(e);
	var r = new Rect();
	r.readFromElement(this.containerElement);
	if (r.getBottom() < point.getY())
		this.outsideMouseDraggingDelta = (point.getY() - r.getBottom()) / 50;
	else if (r.getTop() > point.getY())
		this.outsideMouseDraggingDelta = (point.getY() - r.getTop()) / 50;
	else {
		this.outsideMouseDraggingDelta = 0;
		if (this.dragSelect && isMouseInside(e, this.containerElement, 0)) {
			var cell = getMouseTarget(e);
			var data = cell.data;
			while (data == null) {
				cell = cell.parentNode;
				if (cell == null)
					return;
				data = cell.data;
			}
			var col = cell.colpos;
			this.selectRow(data.position, col, false, false, true);
		}
	}

}
FastTree.prototype.stopOutsideDragging = function() {
	if (this.dragOutsideTimer != null) {
		window.clearInterval(this.dragOutsideTimer);
		this.dragOutsideTimer = null;
	}
}
FastTree.prototype.selectRow = function(row, col, shiftKey, ctrlKey, isDragging) {
	var before = this.selectedRows.toString();
	var beforeCurrentRow = this.currentRow;
	if (!isDragging && this.dragSelect) {
		this.dragStart = row;
	}
	if (ctrlKey && shiftKey) {
		if (this.currentRow == -1)
			this.selectedRows.remove(row);
		else if (this.currentRow < row)
			this.selectedRows.remove(this.currentRow, row);
		else
			this.selectedRows.remove(row, this.currentRow);
	} else if (ctrlKey) {
		if (this.selectedRows.isSelected(row)) {
			this.selectedRows.remove(row);
		} else
			this.selectedRows.add(row);
		
	} else if (shiftKey) {
		if (this.currentRow == -1)
			this.selectedRows.add(row);
		else if (this.currentRow < row)
			this.selectedRows.add(this.currentRow, row);
		else
			this.selectedRows.add(row, this.currentRow);
	} else if (this.dragSelect) {
		this.selectedRows.clear();
		if (row > this.dragStart)
			this.selectedRows.add(this.dragStart, row);
		else
			this.selectedRows.add(row, this.dragStart);
	} else {
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

	var after = this.selectedRows.toString();
	this.activeRowUid = this.currentRow!= -1?this.data[this.currentRow].uid:-1;
		this.onUserSelected(null, this.activeRowUid, after, col, this.activeRowUid);

	return false;
}
// Deprecated?
FastTree.prototype.onSelectMouseMove = function(e, data) {
	if (this.mouseDraggingStart == null)
		return;
	this.onUserSelected(e, "selected_range", this.mouseDraggingStart + "-" + data.uid, this.getColAt(e));
	this.activeRowUid = data.uid;
	this.mouseDraggingEndPos = data.position;
	this.updateAllWidths();
}

// Deprecated?
FastTree.prototype.onSelectMouseUp = function(e,data){	
	var that=this;
	if (this.mouseDraggingStart == null)
		return;
	this.onUserSelected(e, "selected_range", this.mouseDraggingStart + "-" + data.uid, this.getColAt(e));
	this.activeRowUid = data.uid;
	this.mouseDraggingStartPos = this.mouseDraggingStartPos;
	this.mouseDraggingEndPos = data.position;
	this.updateAllWidths();
	this.mouseDraggingStart = null;
}

// Deprecated?
FastTree.prototype.onSelectMouseDown = function(e, data) {
	var shiftKey = e.shiftKey;
	var ctrlKey = e.ctrlKey;
	var button = getMouseButton(e);
	this.contextMenuPoint = getMousePoint(e).move(-4, -4);
	if (!ctrlKey && !shiftKey && button == 1) {
		this.mouseDraggingStart = data.uid;
		this.mouseDraggingStartPos = data.position;
		this.mouseDraggingEndPos = data.position;
	}
	this.contextMenuCurrentColumn = -1;
	this.activeRowUid = data.uid;
	this.onUserSelected(e, "selected", data.uid, this.getColAt(e));
	this.updateAllWidths();
}

FastTree.prototype.onCellClicked = function(e, action, uid) {
	this.contextMenuPoint = getMousePoint(e).move(-4, -4);
	this.contextMenuCurrentColumn = -1;
	this.onUserSelectedTree(e, action, uid, this.getColAt(e));
}
FastTree.prototype.getColAt = function(mouseEvent) {
	return getMouseTarget(mouseEvent).colpos;
}

FastTree.prototype.setClipOffset = function(upperRow) {
	var delta = upperRow - this.upperRow;
	if (delta == 0)
		return;
	this.upperRow = upperRow;
}

FastTree.prototype.setSize = function(x, y) {
	this.rowsCount = y;
	this.totalHeight = y * this.rowHeight + this.topPaddingPx + this.headerHeightPx;
	if (y < this.getLowerRowVisible()) {
		this.upperRow = Math.max(y - this.visibleRowsCount, 0);
	}
	this.updateCells();
	this.updatePinnedBorder();
}
FastTree.prototype.getLowerRowVisible = function() {
	return this.upperRow + this.visibleRowsCount;
}

FastTree.prototype.leftCellsOffset = 0;
FastTree.prototype.pinnedLeftCellsOffset = 0;
FastTree.prototype.pinnedTopCellsOffset = 0;
FastTree.prototype.onScroll = function() {
	this.updateScrollTable();
	if(this.scrollPane.hscrollChanged != 0){
		this.updatePinnedBorder();
	}
	
	this.leftCellsOffset += this.scrollPane.hscrollChanged; 
	// without below two lines, cells won't show up as you scroll down into invisible rows
	var v = Math.round(this.scrollPane.getClipTop()) / this.rowHeight;
	this.setClipOffset(Math.floor(v));
	
	// ensures pinned column stays when horizontal scroll bar moves
	this.updatePinnedColumns();
	if (this.onUpdateCellsEnd != null)
		this.onUpdateCellsEnd(this);
	
	var leftCol=this.leftCol;
	var rightCol=this.rightCol;
	this.determineColumnClipping();
	this.updateCells();
}

FastTree.prototype.determineColumnClipping = function(){	
	var leftCol=this.pinning;
	var rightCol=this.columns.length;
	var clipLeft=this.scrollPane.getClipLeft();
	var clipRight=clipLeft+this.scrollPane.getClipWidth();
	for(var i=this.pinning;i<this.columns.length;i++){
		var box=this.getColumnBox(i);
		if(box.x<=clipLeft && clipLeft<=box.x+box.w)
			leftCol=i;
		if(box.x<=clipRight && clipRight<=box.x+box.w)
			rightCol=i;
	}
	this.leftCol=leftCol;
	this.rightCol=rightCol;
}
FastTree.prototype.getLeftColVisible = function(){	
	return this.leftCol;
}
FastTree.prototype.getRightColVisible = function(){	
	return this.rightCol;
}
FastTree.prototype.updateScrollTable = function(){	
  //Update scroll position
  var h=Math.round(this.scrollPane.getClipLeft());
  this.headerElement.style.left=toPx(-h);
  this.treeElement.style.left=toPx(-h);
}
FastTree.prototype.getUpperRowVisible = function() {
	return this.upperRow;
}

FastTree.prototype.getLowerRowVisible = function() {
	return this.upperRow + this.visibleRowsCount;
}

FastTree.prototype.showContextMenu = function(menu) {
	this.createMenu(menu).show(this.contextMenuPoint);
}
FastTree.prototype.createMenu = function(menu) {
	var that = this;
	var r = new Menu(getWindow(this.containerElement));
	if (this.contextMenuCurrentColumn == -1)
	   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e,id);} );
	else
	   r.createMenu(menu, function(e,id){that.onUserHeaderMenuItem(e,id,that.contextMenuCurrentColumn);} );
	return r;
}

FastTree.prototype.setOptions = function(options) {
	// QuickFilter
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
	//ScrollBorderColor
	{
		var borderColor = options.scrollBorderColor;
		this.scrollPane.hscroll.DOM.trackElement.style.borderColor=borderColor;
		this.scrollPane.hscroll.DOM.handleElement.style.borderColor=borderColor;
		this.scrollPane.hscroll.DOM.backElement.style.borderColor=borderColor;
		this.scrollPane.hscroll.DOM.forwardElement.style.borderColor=borderColor;
		this.scrollPane.vscroll.DOM.trackElement.style.borderColor=borderColor;
		this.scrollPane.vscroll.DOM.handleElement.style.borderColor=borderColor;
		this.scrollPane.vscroll.DOM.backElement.style.borderColor=borderColor;
		this.scrollPane.vscroll.DOM.forwardElement.style.borderColor=borderColor;
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
	
	if (options.fontSize != null) {
		this.fontSize = toPx(options.fontSize);
	}
	if (options.fontFamily != null) {
		this.fontFamily = options.fontFamily;
		applyStyle(this.treeElement, this.fontFamily);
	}if (options.rowHeight != null) {
		this.rowHeight = options.rowHeight;
	}
	if (options.headerFontSize != null) {
		this.headerFontSize = toPx(options.headerFontSize);
	}
	if (options.headerRowHeight != null) {
		this.headerHeightPx = options.headerRowHeight;
	}
	if (options.hideCheckBoxes != null)
		this.hideCheckBoxes = !!(options.hideCheckBoxes);
	if (options.headerDividerHidden != null)
		this.hideHeaderDivider = options.headerDividerHidden === "true" ? true : false;
	if (options.headerBarHidden != null) {
		this.headerBarHidden = options.headerBarHidden === "true" ? true : false;
	}
	if (options.searchBarHidden != null) {
		this.searchBarHidden = options.searchBarHidden == "true" ? true : false;
	}
	if (options.cellBottomDivider != null) {
		this.cellBottomDivider = toPx(options.cellBottomDivider);
	}else this.cellBottomDivider="0px";

	if (options.cellRightDivider != null) {
		this.cellRightDivider = toPx(options.cellRightDivider);
	}else this.cellRightDivider="1px";
	if (options.verticalAlign != null) {
		this.verticalAlign = options.verticalAlign;
	}else this.verticalAlign = "center";
	applyStyle(this.scrollPane.DOM.paneElement, options.backgroundStyle);
	if (options.scrollBarWidth != null) {
		this.scrollSize = parseInt(options.scrollBarWidth);
		this.scrollPane.scrollSize = this.scrollSize;
	}
	if(options.cellPadHt != null)
		this.cellPadHt = options.cellPadHt;
	this.bgColor = options.backgroundStyle;
	this.fontStyle = options.fontStyle;
	this.grayBarColor = options.grayBarStyle;
	this.headerBgStyle = options.headerBgStyle;
	this.headerFontStyle = options.headerFontStyle;
	this.flashUpColor = options.flashUpColor;
	this.flashDnColor = options.flashDnColor;
	this.flashMs = options.flashMs; 
	if (this.flashMs == null) 
		this.flashMs = 0;
    this.flashStyle='background '+this.flashMs+'ms linear';
	

	this.filteredColumnBgColor = options.filteredBg;
	this.filteredColumnFontColor = options.filteredFont;
	this.cellBorderColor = options.cellBorderStyle;
	this.treatNameClickAsSelect = options.treatNameClickAsSelect;
	if(options.cellActiveBg) // active selection background color
		this.activeClassname=options.cellActiveBg==null ? 'cell_active_default' : toCssForColor(this.treeElement,options.cellActiveBg);
	if(options.cellSelectedBg) // selection background color
		this.selectedClassname=options.cellSelectedBg==null ? 'cell_selected_default' : toCssForColor(this.treeElement,options.cellSelectedBg);
	applyStyle(this.scrollPane.vscroll.DOM.gripElement, options.gripColor);
	applyStyle(this.scrollPane.vscroll.DOM.trackElement, options.trackColor);
	applyStyle(this.scrollPane.vscroll.DOM.forwardElement, options.scrollButtonColor);
	applyStyle(this.scrollPane.vscroll.DOM.backElement, options.scrollButtonColor);
	applyStyle(this.scrollPane.hscroll.DOM.gripElement, options.gripColor);
	applyStyle(this.scrollPane.hscroll.DOM.trackElement, options.trackColor);
	applyStyle(this.scrollPane.hscroll.DOM.forwardElement, options.scrollButtonColor);
	applyStyle(this.scrollPane.hscroll.DOM.backElement, options.scrollButtonColor);
	
	this.scrollPane.hscroll.DOM.applyTinySquareColor(options.scrollBarCornerColor);
	this.scrollPane.vscroll.DOM.applyTinySquareColor(options.scrollBarCornerColor);

	// Set scroll icon colors...
	scrollIconsColor = options.scrollIconsColor || '#000000';
	this.scrollPane.updateIconsColor(scrollIconsColor);

	if (options.menuBarBg != null) {
		this.headerElement.style.backgroundColor = options.menuBarBg;
		this.menuBarBg = options.menuBarBg;
	} else {
		this.headerElement.style.backgroundColor = "#535353";
		applyStyle(this.headerElement, this.headerBgStyle);
		applyStyle(this.headerElement, this.headerFontStyle);
		if (this.hideHeaderDivider == true)
			this.headerElement.style.backgroundImage = "none";
	}
	if (options.menuFontColor != null) {
		this.headerElement.style.color = options.menuFontColor;
	} else {
		this.headerElement.style.color = "white";
	}
	this.updateColumns();
}

FastTree.prototype.updateHeaderBarHidden = function() {
	if (this.headerBarHidden == true || this.columns.length <= 0) {
		this.headerElement.style.display = "none";
		this.oldHeaderHeightPx = this.headerHeightPx;
		this.headerHeightPx = 0;
	} else {
		this.headerElement.style.display = "block";
		if (this.headerHeightPx == 0)
			this.headerHeightPx = this.oldHeaderHeightPx != null ? this.oldHeaderHeightPx : this.defaultHeaderHeightPx;
	}
}

FastTree.prototype.initColumns = function(pinning, cols) {
	this.pinning = pinning;
	removeAllChildren(this.headerElement);
	this.columns = [];
	this.hiddenColumns = [];
    resetPushDraggableContainer(this.headerElement);
	for ( var i = 0; i < cols.length; i++) {
		var col = cols[i];
		var cssClass = col.cssClass;
		var id = col.id;
		var name = col.name;
		var visible = col.visible;
		var sort = col.sort;
		var clickable = col.clickable;
		var jsFormatterType = col.jsFormatterType;
		var className = '';
		var headerStyle = col.headerStyle;
		var filter = col.filter;
		var filterText = col.filterText;
		var hids = col.hids;
		if (sort == '')
			className = '';
		else if (sort == 1)
			className = '';
		else if (sort == 0)
			className = '';
		else if (sort == 3)
			className = 'asc';
		else if (sort == 2)
			className = 'des';
		if (col.filter)// {
			className = 'header_filtered ' + className;
		var width = col.width;
		if (visible) {
			var column = this.addColumn(id, name, width, className, cssClass, headerStyle, null, jsFormatterType, filter, filterText);
			column.setHIDS(hids);
			column.headerElement.style.backgroundColor = "#535353";
			applyStyle(column.headerElement, this.headerBgStyle);
			if (!column.columnHeaderStyle)
				applyStyle(column.headerElement, this.headerFontStyle);
			if (this.headerBarHidden == true)
				column.headerElement.style.backgroundImage = "none";
			if (col.filter) {
				column.hasFilter = true;
				if (this.filteredColumnBgColor != null) {
					applyStyle(column.headerElement, this.filteredColumnBgColor);
				} else {
					column.headerElement.style.backgroundColor = "#ff7f00";
				}
				if (this.filteredColumnFontColor != null) {
					applyStyle(column.headerElement, this.filteredColumnFontColor);
				} else {
					column.headerElement.style.color = "white";
				}
			}
			else{
				column.hasFilter = false;
			}
			column.clickable = clickable;
			if (i == 0) {
			}
		} else
			this.hiddenColumns[id] = name;
	}
	this.updateColumnWidths();
	this.updateColumns();
	this.updatePinnedBorder();
	this.updatePinnedColumns();
	this.updateQuickFilterStyles();
	this.determineColumnClipping();
}

FastTree.prototype.getColumnBox = function(colIndex) {
	var box = null;
	if (colIndex < this.columns.length) {
		box = {};
		var col = this.columns[colIndex];
		var header = col.headerElement;

		var h1 = this.totalHeight; // Height of rows + header
		var h = this.containerElement.offsetHeight - this.scrollSize; // Height of the container - the scrollbar
		h = h < h1 ? h : h1;

		var x = header.offsetLeft + header.offsetWidth;
		if (colIndex < this.pinning) {
			x -= this.scrollPane.hscroll.clipTop
		}

		box.w = col.width;
		box.h = h;
		box.x = x;
		box.y = 0;
		box.i = colIndex;
	}
	return box;
}

FastTree.prototype.updatePinnedBorder = function() {
	if(this.pinning == 0){
	   this.pinnedBorderElement.style.display = "none"; 
	}
	if(this.pinning && this.pinning <= this.columns.length){
		var col = this.columns[this.pinning-1];
		var left = col.offset+ col.width;
		var height = this.containerElement.offsetHeight - this.scrollSize; // Height of the container - the scrollbar
		if(this.hideHeaderDivider == "false")
			height += parseInt(this.headerHeight);
		this.pinnedBorderElement.style.left = toPx(left);
		this.pinnedBorderElement.style.height = toPx(height);
		if(this.pinning && this.scrollPane.hscroll.clipTop > this.pinnedBorderWidth/2){
		  this.pinnedBorderElement.style.display = "inherit"; 
		}else{
		  this.pinnedBorderElement.style.display = "none"; 
		}
	}
}
FastTree.prototype.setPinnedBorderWidth = function(width) {
	this.pinnedBorderWidth = width;
	this.pinnedBorderElement.style.width = toPx(width);
}
FastTree.prototype.applyPinnedBorderStyle = function(style) {
	var dummyElement = {style:{}};
	applyStyle(dummyElement, style);
	Object.assign(this.pinnedBorderElement.style, dummyElement.style);
}

FastTree.prototype.setColumnWidth = function(location, width) {
	var rect = new Rect();
	this.columns[location].width = width;
	this.updateColumnWidths();
}
FastTree.prototype.updateColumnWidths = function(location, width) {
	this.totalWidth = 0;
	for ( var i = 0; i < this.columns.length; i++) {
		this.columns[i].offset = this.totalWidth;
		this.totalWidth += this.columns[i].width;
	}
}
FastTree.prototype.addColumn = function(id, name, width, headerClassName, cellClassName, headerStyle, location, jsFormatterType, filter, filterText) {
	if (location == null)
		location = this.columns.length;
	var offset = location == 0 ? 0 : this.columns[location - 1].getRightOffset();
	var that=this; // owner
	var column = new FastTableColumn(id, name, offset, location, width, headerClassName, cellClassName != null ? cellClassName : "", true, headerStyle, that);
	
	if(id!=0){
	makePushDraggable(this.headerElement, column.headerElement, column.invisibleElement, true, false, null, 
   		function(a,b,c){that.onDragColumnEndCallback(a,b,c);}, 
   		function(a,b,c,d,e,f){return that.onDragMousePosition(a,b,c,d,e,f);}, 
   	false);
   	}
	
	column.invisibleElement.location = location;
	column.headerElement.location = location;// TODO this is wrong if not adding to the end (following columns need to be updated!)
   column.invisibleElement.onclick=function(e){that.onHeaderClicked(e);};
	column.headerElement.style.borderColor = this.cellBorderColor;
	  column.buttonsUpElement.onmousedown=function(e){that.onHeaderButtonClicked(e,'__sort_3',column.location);};
	   column.buttonsDnElement.onmousedown=function(e){that.onHeaderButtonClicked(e,'__sort_2',column.location);};
	column.grabElement.style.zIndex = location < this.pinning ? '1' : '0';
	column.grabElement.style.opacity = "0";
	column.grabElement.location = location;
   column.grabElement.ondragging=function(e,x,y){that.onHeaderDragging(e,x,y);};
   column.grabElement.clipDragging=function(e,rect){that.clipHeaderDragging(e,rect);};
   column.grabElement.onmouseenter=function(e){that.showGrabElement(e, that, location);};
   column.grabElement.onmouseleave=function(e){that.hideGrabElement(e, that, location);};
	column.grabElement.style.borderColor = this.cellBorderColor;
	column.jsFormatterType = jsFormatterType;
	if (this.hideHeaderDivider == true)
		column.headerElement.style.backgroundImage = "none";
	makeDraggable(column.grabElement, column.grabElement, false, true);
	this.headerElement.insertBefore(column.headerElement, this.headerElement.firstChild);
	this.headerElement.insertBefore(column.grabElement, this.headerElement.firstChild);
	this.columns.splice(location, 0, column);

   //TODOFILTER
   column.filter = filter;
   column.filterText = filterText;
   column.updateQuickFilterValue();
   
	var rect = new Rect();
	this.setColumnWidth(location, width);
	applyStyle(column.headerElement, column.columnHeaderStyle);
	return column;
}

FastTree.prototype.onDragColumnEndCallback=function(oldIndex,newIndex,success){
	if(oldIndex!=newIndex)
		this.callback('moveColumn',{oldPos:oldIndex,newPos:newIndex  });
}
FastTree.prototype.onDragMousePosition=function(mouseX, containerOffset, mouseOffset, elementWidth, moveX, moveY){
	if(moveY)
		return;
	
	var leftEdge = this.scrollPane.getClipLeft();
	var rightEdge = leftEdge + this.scrollPane.getClipWidth();
	var draggedLeftEdge = (mouseX - containerOffset - mouseOffset);
	var draggedRightEdge = (mouseX - containerOffset - mouseOffset + elementWidth);
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

FastTree.prototype.showGrabElement = function(e, that, location) {
	if (e.buttons == 0) {
		var col = that.columns[location].grabElement;
		col.style.zIndex = location < that.pinning ? '2' : '1';
	}
}
FastTree.prototype.hideGrabElement = function(e, that, location) {
	if (e.buttons == 0) {
		var col = that.columns[location].grabElement;
		col.style.zIndex = location < that.pinning ? '1' : '0';
	}
}

FastTree.prototype.onHeaderButtonClicked = function(e, id, col) {
	this.onUserHeaderMenuItem(e, id, col);
}
FastTree.prototype.onHeaderClicked = function(e) {
	e.stopPropagation();
	var target = getMouseTarget(e);
	if (this.menu != null)
		this.menu.hide();
	if (target == this.headerElement) {
		this.contextMenuCurrentColumn = -2;
		var point = new Rect().readFromElement(target).getLowerLeft();
		point.x = getMousePoint(e).x - 4;
		this.contextMenuPoint = point;
	} else {
		this.contextMenuCurrentColumn = target.location;
		var point = new Rect().readFromElement(target).getLowerLeft();
		this.contextMenuPoint = point;
	}
	if (this.onUserHeaderMenu != null && this.contextMenuCurrentColumn != null && this.headerBarHidden == false)
		this.onUserHeaderMenu(e, this.contextMenuCurrentColumn);
}

FastTree.prototype.onHeaderDragging = function(e, x, y) {
	var left = new Rect().readFromElementRelatedToParent(e).getLeft();
	var width = left - this.columns[e.location].offset + 3;
	if (e.location < this.pinning) {
		width -= fl(this.scrollPane.getClipLeft());
	}
	this.setColumnWidth(e.location, width);
	if (this.onUserColumnResize != null)
		this.onUserColumnResize(e, e.location, width);
	this.updateColumns();
	this.updatePinnedBorder();
	this.updatePinnedColumns();
	return false;
}

FastTree.prototype.clipHeaderDragging = function(e, rect) {
	var min = this.columns[e.location].offset + 5;
	if (min > rect.getLeft())
		rect.setLeft(min);
}
FastTree.prototype.ensurePosVisible = function(pos) {
	this.updateAllWidths();
	this.scrollPane.setClipTop(Math.max(0, pos * this.rowHeight - 100));
}
FastTree.prototype.setActiveRowUid = function(activeRow) {
	this.activeRowUid = activeRow;
}

FastTree.prototype.onWhiteSpaceMouseDown=function(e){
	var that=this;
	if(e.target != this.treeElement)
		return;
    if(e.shiftKey  || e.ctrlKey)
      return;
	var button=getMouseButton(e);
	if(button==2){
	  this.clearSelected(e);
	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);
      this.contextMenuCurrentColumn=-1;
	  if(this.onUserContextMenu!=null)
		  this.onUserContextMenu(e);
	}else{
	  this.clearSelected(e);
	}
}

FastTree.prototype.selectAll=function(e){
	this.selectedRows.add(0,this.rowsCount-1);
	this.onUserSelected(e,this.activeRowUid,this.selectedRows.toString(), this.getColumnAtPoint(MOUSE_POSITION_X),null);
}
FastTree.prototype.clearSelected=function(e){
	this.selectedRows.clear();
	this.currentRow = -1;
	this.onUserSelected(e,null,this.selectedRows.toString(), this.getColumnAtPoint(MOUSE_POSITION_X), null);
}

FastTree.prototype.setCellFlash=function(data,cnt,uid,style){
	for ( var i = cnt - 1; i >= 1; i--) {
		var cell = data.cells[i];
		var value = cell.textContent;
		if (this.flashMs>0){
			var oldValue=this.valueElements.set(i,uid,value);
		    if(oldValue && oldValue!=value && this){
				if(cell.flashTimeout!=null)
					clearTimeout(cell.flashTimeout);
				if (cell.resetTransition!=null)
					clearTimeout(cell.resetTransition);
				var flashDir;
				if(this.flashUpColor==this.flashDnColor)
					flashDir=1;
				else{
					var n1=parseNumber(oldValue);
					var n2=parseNumber(value);
					if(Number.isNaN(n1) || Number.isNaN(n2))
						flashDir=oldValue<value? 1 : 2;
					else
						flashDir=n1 < n2? 1 : 2;
			    }
				var that=this;
				cell.style.transition = '';
				cell.style.backgroundColor=flashDir==1 ? this.flashUpColor : this.flashDnColor;	
				cell.isFlashing = true;
				var func=function(cell2){
					cell2.flashTimeout=null;
					cell2.style.transition=this.flashStyle;
					that.applyStyle(cell2,style,true,data,cnt);
				};
				var func2=function(cell2){
					cell2.style.transition='';
					cell2.resetTransition=null;
					cell2.isFlashing = false;
				};
				cell.flashTimeout=window.setTimeout(func,this.flashMs, cell);
				cell.resetTransition=window.setTimeout(func2,this.flashMs*2, cell);
			}
		}
	}
}

FastTree.prototype.applyStyle=function(target,val,force,data,cnt){
    if(!force && target.resetTransition!=null)
        return;
	target.className='';
	this.setDataCellsStyle(data, cnt, val);
	if(val != null && "" != val && target.checkbox == null)
		applyStyle(target,val);
	if(target.cellClassName){
		target.className+=" "+target.cellClassName;
	}
}

//

// ########################
// ##### Tree Panels ######

function TPCallback(input, pid, callback, nodeuid){
	var p = getPortletManager().getPortlet(pid);
	if (p == null)
		return;
	p.callBack(callback,{val:input.value, uid:nodeuid});
}

function TPcheck(input, pid, nodeUid) {
	var p = getPortletManager().getPortlet(pid);
	if (p == null)
		return;

	p.callBack('treePanelsRerunDmOnChange',{val:input.checked, uid:nodeUid});
}

function TPvarname(input, pid, nodeUid) {
	var p = getPortletManager().getPortlet(pid);
	if (p == null)
		return;

	p.callBack('treePanelsTgtVarName',{val:input.value, uid:nodeUid});
}

function TPformatter(input, pid, nodeUid) {
	var p = getPortletManager().getPortlet(pid);
	if (p == null)
		return;

	p.callBack('treePanelsFormatter',{val:input.value, uid:nodeUid});
}

//########################
//##### Set Defaults######
function onActionChanged(input, nodeUid, portletId) {
	var portlet = getPortletManager().getPortlet(portletId);
	if (portlet == null)
		return;
	if(input.value == 'NO_DEFAULT') {
		input.style.backgroundColor = "#F8D7DA";
		input.style.color = "#721C24";
		input.style.fontWeight = "bold";
	}
	portlet.callBack('onActionMenuChanged', {nodeUid: nodeUid, action: input.value});
}