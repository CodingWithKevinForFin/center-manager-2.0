package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_fasttree_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_fasttree_js_1() {
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
            "function FastTree(element) {\r\n"+
            "	var that = this;\r\n"+
            "	this.valueElements=new Object2d();\r\n"+
            "	this.treeElement = nw(\"div\");\r\n"+
            "	this.treeElement.style.height = '100%';\r\n"+
            "	this.treeElement.style.width = '100%';\r\n"+
            "\r\n"+
            "	this.data = [];\r\n"+
            "	this.visibleRowsByUid = {};// uid -> data\r\n"+
            "	this.containerElement = element;\r\n"+
            "	this.scrollPaneElement = nw(\"div\");\r\n"+
            "	this.containerElement.appendChild(this.scrollPaneElement);\r\n"+
            "	this.scrollPane = new ScrollPane(this.scrollPaneElement, this.scrollSize, this.treeElement);\r\n"+
            "    this.scrollPane.onScroll=function(){that.onScroll()};\r\n"+
            "	this.visibleRowsCount = 0;\r\n"+
            "	this.headerElement = nw(\"div\");\r\n"+
            "	this.headerElement.className = \"header\";\r\n"+
            "	var startX;\r\n"+
            "	const delta = 6;\r\n"+
            "	this.headerElement.addEventListener('mousedown', function(e){\r\n"+
            "		startX = e.pageX;\r\n"+
            "	});\r\n"+
            "	\r\n"+
            "	this.headerElement.addEventListener('mouseup', function(e){\r\n"+
            "		const diffx = Math.abs(e.pageX-startX);\r\n"+
            "		if (diffx < delta) {\r\n"+
            "			that.onHeaderClicked(e);\r\n"+
            "		}\r\n"+
            "	});\r\n"+
            "	\r\n"+
            "	this.containerElement.appendChild(this.headerElement);\r\n"+
            "	this.treeElement.tabIndex = 0;\r\n"+
            "	this.treeElement.style.outline = 'none';\r\n"+
            "\r\n"+
            "	this.pinnedBorderElement = nw(\"div\");\r\n"+
            "	this.pinnedBorderWidth = 5;\r\n"+
            "	this.pinnedBorderElement.style.display = \"none\";\r\n"+
            "	this.pinnedBorderElement.style.zIndex = 1;\r\n"+
            "	this.pinnedBorderElement.style.pointerEvents = \"none\";\r\n"+
            "	this.pinnedBorderElement.style.width = toPx(this.pinnedBorderWidth);\r\n"+
            "	this.pinnedBorderElement.style.borderWidth = \"0px 2px 0px 2px\";\r\n"+
            "	this.pinnedBorderElement.style.borderStyle = \"inset\";\r\n"+
            "	this.pinnedBorderElement.style.borderColor = \"#999999\";\r\n"+
            "	this.pinnedBorderElement.style.background = \"#b3b3b3\";\r\n"+
            "	this.containerElement.appendChild(this.pinnedBorderElement);\r\n"+
            "	this.columns = [];\r\n"+
            "	this.pinning = 2;\r\n"+
            "	this.defaultHeaderHeightPx = 18;\r\n"+
            "	this.headerHeightPx = this.defaultHeaderHeightPx;\r\n"+
            "	this.defaultSearchBarHeight = 20;\r\n"+
            "	this.searchBarHeight = this.defaultSearchBarHeight;\r\n"+
            "	this.headerBarHidden = false;\r\n"+
            "	this.searchBarHidden = false;\r\n"+
            "	this.hideHeaderDivider = false;\r\n"+
            "    this.onCellMouseUpFunc=function(e){return that.onCellMouseUp(e);};\r\n"+
            "    this.onCellMouseDownFunc=function(e){e.stopPropagation();return that.onCellMouseDown(e);};\r\n"+
            "    this.onCellMouseMoveFunc=function(e){return that.onCellMouseMove(e);};\r\n"+
            "    this.treeElement.onmousedown=function(e){e.stopPropagation();return that.onWhiteSpaceMouseDown(e);};\r\n"+
            "	this.selectedRows = new SelectedRows();\r\n"+
            "	this.currentRow = -1;\r\n"+
            "	this.quickColumnFilterHeight = this.rowHeight+10;\r\n"+
            "	this.quickColumnFilterHidden = true;//quickfilter is displayed\r\n"+
            "    this.selectedClassname='cell_selected_default';\r\n"+
            "    this.activeClassname='cell_active_default';\r\n"+
            "    \r\n"+
            "    //MOBILE SUPPORT - FOR DRAG SCROLL\r\n"+
            "    this.longTouchTimer;\r\n"+
            "    this.isSelecting = false;\r\n"+
            "    this.isDragging = false;\r\n"+
            "    this.currPoint = new Point(0,0);\r\n"+
            "    this.currTarget;\r\n"+
            "    this.treeElement.ontouchstart=function(e) { that.createGlass();that.onTouchDragStart(e);};\r\n"+
            "    this.treeElement.ontouchmove=function(e) { that.onTouchDragMove(e); that.onScroll();};\r\n"+
            "    this.treeElement.ontouchend= function(e){ that.isSelecting=false; clearTimeout(that.longTouchTimer); that.longTouchTimer=null; that.hideGlass();};\r\n"+
            "}\r\n"+
            "FastTree.prototype.cornerSize = 15;\r\n"+
            "FastTree.prototype.activeRowUid = -1;\r\n"+
            "FastTree.prototype.rowHeight = 15;\r\n"+
            "FastTree.prototype.leftPaddingPx = 4;\r\n"+
            "FastTree.prototype.topPaddingPx = 4;\r\n"+
            "FastTree.prototype.defaultFontSize = \"18px\";\r\n"+
            "FastTree.prototype.defaultHeaderHeightPx;\r\n"+
            "FastTree.prototype.headerHeightPx = 0;\r\n"+
            "FastTree.prototype.defaultSearchBarHeight;\r\n"+
            "FastTree.prototype.searchBarHeight;\r\n"+
            "FastTree.prototype.upperRow = 0;\r\n"+
            "FastTree.prototype.scrollSize = 15;\r\n"+
            "FastTree.prototype.treatNameClickAsSelect = true;\r\n"+
            "FastTree.prototype.hideCheckBoxes = false;\r\n"+
            "FastTree.prototype.selectedRows;\r\n"+
            "FastTree.prototype.selectedClassname;\r\n"+
            "FastTree.prototype.activeClassname;\r\n"+
            "\r\n"+
            "FastTree.prototype.quickColumnFilterHidden;\r\n"+
            "FastTree.prototype.quickColumnBackgroundColor;\r\n"+
            "FastTree.prototype.quickColumnFontColor;\r\n"+
            "FastTree.prototype.quickColumnFontSz;\r\n"+
            "FastTree.prototype.quickColumnBorderColor;\r\n"+
            "FastTree.prototype.quickColumnFilterHeight;\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTree.prototype.setQuickFilterAutocomplete=function(colPos,mapOfValuesOrNull){\r\n"+
            "	var col = this.columns[colPos];\r\n"+
            "	var oldVal = col.quickColumnFilterElement.value;\r\n"+
            "	col.quickColumnFilterElement.clearOptions2();\r\n"+
            "	for (let key in mapOfValuesOrNull) {\r\n"+
            "		col.quickColumnFilterElement.addOptionDisplayAction(key, mapOfValuesOrNull[key]);\r\n"+
            "	}\r\n"+
            "	col.quickColumnFilterElement.autocomplete(col.filterText);\r\n"+
            "};\r\n"+
            "\r\n"+
            "FastTree.prototype.updateQuickFilterStyles=function(){\r\n"+
            "	if(this.quickColumnFilterHidden == true)\r\n"+
            "		return;\r\n"+
            "	for ( var x = 0; x < this.columns.length; x++) {\r\n"+
            "		if(x == -1)\r\n"+
            "			continue;\r\n"+
            "		var quickColumnFilterElement = this.columns[x].quickColumnFilterElement;\r\n"+
            "		quickColumnFilterElement.input.style.backgroundColor= this.quickColumnBackgroundColor;\r\n"+
            "		quickColumnFilterElement.input.style.color= this.quickColumnFontColor;\r\n"+
            "		quickColumnFilterElement.input.style.border= \"1px solid \" + this.quickColumnBorderColor;\r\n"+
            "		quickColumnFilterElement.input.style.fontSize=toPx(this.quickColumnFontSz);\r\n"+
            "	}\r\n"+
            "	return;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.setMetrics = function(rowHeightPx, leftPaddingPx, topPaddingPx, headerHeightPx) {\r\n"+
            "	this.rowHeight = rowHeightPx;\r\n"+
            "	this.leftPaddingPx = leftPaddingPx;\r\n"+
            "	this.topPaddingPx = topPaddingPx;\r\n"+
            "	this.headerHeightPx = headerHeightPx;\r\n"+
            "	this.setLocation(this.x, this.y, this.width, this.height);\r\n"+
            "}\r\n"+
            "FastTree.prototype.setLocation = function(x, y, width, height) {\r\n"+
            "	this.x = x;\r\n"+
            "	this.y = y;\r\n"+
            "	this.width = width;\r\n"+
            "	this.height = height;\r\n"+
            "\r\n"+
            "	this.searchBarHeight = this.searchBarHidden ? 0 : this.defaultSearchBarHeight;\r\n"+
            "	if (this.headerBarHidden) {\r\n"+
            "		this.oldHeaderHeightPx = this.headerHeightPx;\r\n"+
            "		this.headerHeightPx = 0;\r\n"+
            "		}\r\n"+
            "		else {\r\n"+
            "		if (this.headerHeightPx == 0)\r\n"+
            "			this.headerHeightPx = this.oldHeaderHeightPx;\r\n"+
            "	}\r\n"+
            "	//TODOFILTER\r\n"+
            "	var quickFilterHeight = this.quickColumnFilterHidden == false?  this.quickColumnFilterHeight : 0 ;\r\n"+
            "	\r\n"+
            "	var scrollYPos = this.y + this.headerHeightPx + quickFilterHeight;\r\n"+
            "	var scrollHSize = this.height - this.headerHeightPx - this.searchBarHeight - quickFilterHeight;\r\n"+
            "		\r\n"+
            "	new Rect(x, y + this.searchBarHeight, width, height - this.searchBarHeight).writeToElement(this.containerElement);\r\n"+
            "	this.scrollPane.setLocation(this.x, scrollYPos, this.width, scrollHSize);\r\n"+
            "	this.updateColumns();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.updateColumns = function() {\r\n"+
            "	var rect = new Rect();\r\n"+
            "	this.updateHeaderBarHidden();\r\n"+
            "	if (this.hideHeaderDivider) {\r\n"+
            "		this.headerElement.style.backgroundImage = \"none\";\r\n"+
            "		this.headerElement.style.boxShadow = \"none\";\r\n"+
            "	} else {\r\n"+
            "		this.headerElement.style.backgroundImage = null;\r\n"+
            "		this.headerElement.style.boxShadow = null;\r\n"+
            "	}\r\n"+
            "	//TODOFILTER\r\n"+
            "	var fullHeaderHeight = this.quickColumnFilterHidden == false? this.headerHeightPx + this.quickColumnFilterHeight : this.headerHeightPx;\r\n"+
            "	new Rect(0, 0, Math.max(this.totalWidth, this.width), fullHeaderHeight).writeToElement(this.headerElement);\r\n"+
            "	for ( var i = 0; i < this.columns.length; i++) {\r\n"+
            "		//this.columns[i].headerElement.style.fontWeight = i < this.pinning ? 'bold' : 'normal';\r\n"+
            "		this.columns[i].headerElement.style.borderColor = this.cellBorderColor;\r\n"+
            "		this.columns[i].headerElement.style.fontSize = this.headerFontSize;\r\n"+
            "//		if (this.fontFamily != null)\r\n"+
            "//			applyStyle(this.columns[i].headerElement, this.fontFamily);\r\n"+
            "//		else\r\n"+
            "//			applyStyle(this.columns[i].headerElement, \"_fm=arial\");\r\n"+
            "		\r\n"+
            "		// this.columns[i].headerElement.style.justifyContent = this.verticalAlign;\r\n"+
            "		if (this.hideHeaderDivider) {\r\n"+
            "			this.columns[i].headerElement.style.backgroundImage = \"none\";\r\n"+
            "			this.columns[i].headerElement.style.boxShadow = \"none\";\r\n"+
            "		} else\r\n"+
            "			this.columns[i].headerElement.style.backgroundImage = null;\r\n"+
            "\r\n"+
            "		this.updateColumnFilter(i);\r\n"+
            "		rect.setLocation(this.columns[i].offset, 0, this.columns[i].width, fullHeaderHeight).writeToElement(this.columns[i].headerElement);\r\n"+
            "		var box = this.getColumnBox(i);\r\n"+
            "		rect.setLocation(box.x - 5, 0, 7, fullHeaderHeight).writeToElement(this.columns[i].grabElement);\r\n"+
            "	}\r\n"+
            "	this.updateColumnWidths();\r\n"+
            "	this.updateAllWidths();\r\n"+
            "	this.updateBounds();\r\n"+
            "}\r\n"+
            "FastTree.prototype.updateColumnFilter=function(i){\r\n"+
            "	if(this.quickColumnFilterHidden == false){ //qf\r\n"+
            "		this.columns[i].quickColumnFilterElement.style.width=toPx(this.columns[i].width-4);\r\n"+
            "	}\r\n"+
            "	this.columns[i].quickColumnFilterElement.style.display = this.quickColumnFilterHidden == false?null:\"none\";\r\n"+
            "};\r\n"+
            "FastTree.prototype.getColumnAtPoint = function(x) {\r\n"+
            "	for ( var i = 0; i < this.columns.length; i++) {\r\n"+
            "		var col = new Rect().readFromElementRelatedToWindow(this.columns[i].headerElement);\r\n"+
            "		if (col.getLeft() <= x && col.getRight() >= x)\r\n"+
            "			return i;\r\n"+
            "	}\r\n"+
            "	return -1;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.updateBounds = function() {\r\n"+
            "	this.visibleRowsCount = Math.ceil(this.scrollPane.DOM.paneElement.clientHeight / this.rowHeight);\r\n"+
            "	new Rect(0, 0, max(this.totalWidth, this.width), this.height - this.scrollSize - this.rowHeight * 2).writeToElement(this.treeElement);\r\n"+
            "	this.determineColumnClipping();\r\n"+
            "	this.updateScrollTable()\r\n"+
            "	this.updateCells();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.visibleRowsByUid = {};// uid -> data\r\n"+
            "FastTree.prototype.toRemoveRows = []\r\n"+
            "\r\n"+
            "FastTree.prototype.updateCells = function() {\r\n"+
            "	if (this.onUpdateCellsStart != null)\r\n"+
            "		this.onUpdateCellsStart(this);\r\n"+
            "	var bottomRow = this.upperRow + this.visibleRowsCount + 2;\r\n"+
            "	for ( var i in this.visibleRowsByUid) {\r\n"+
            "		var data = this.visibleRowsByUid[i];\r\n"+
            "		if (data.position < this.upperRow || data.position >= bottomRow){\r\n"+
            "			this.treeElement.removeChild(data.row);\r\n"+
            "			delete this.visibleRowsByUid[data.uid];\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	var topCellsOffset = this.scrollPane.getClipTop();\r\n"+
            "	for ( var y =  this.upperRow; y < bottomRow; y++) {\r\n"+
            "		var data = this.data[y];\r\n"+
            "		if (data == null) \r\n"+
            "			continue;\r\n"+
            "		var row=data.row;\r\n"+
            "		if (this.visibleRowsByUid[data.uid] == null) {\r\n"+
            "			this.treeElement.appendChild(row);\r\n"+
            "			this.visibleRowsByUid[data.uid] = data;\r\n"+
            "		}\r\n"+
            "	");
          out.print(
            "	this.updateWidths(data);\r\n"+
            "		row.style.top = toPx(rd(this.rowHeight*y - topCellsOffset));\r\n"+
            "	}\r\n"+
            "	this.scrollPane.setPaneSize(this.totalWidth, this.totalHeight - this.topPaddingPx - this.headerHeightPx);\r\n"+
            "	if (this.onUpdateCellsEnd != null)\r\n"+
            "		this.onUpdateCellsEnd(this);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.clearData = function(start) {\r\n"+
            "	this.data.length = start;\r\n"+
            "	for ( var i in this.visibleRowsByUid) {\r\n"+
            "		var data = this.visibleRowsByUid[i];\r\n"+
            "		if (data.position >= start){\r\n"+
            "		  this.treeElement.removeChild(data.row);\r\n"+
            "		  delete this.visibleRowsByUid[data.uid];\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.removeData = function(data) {\r\n"+
            "	delete this.data[data.position];\r\n"+
            "	if (this.visibleRowsByUid[data.uid] != null) {\r\n"+
            "		this.treeElement.removeChild(data.row);\r\n"+
            "		delete this.visibleRowsByUid[data.uid];\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTree.prototype.setDataInitRow = function(uid, position, depth, name) {\r\n"+
            "	var data = this.data[position];\r\n"+
            "	if (data != null && uid != data.uid) {\r\n"+
            "		this.removeData(data);\r\n"+
            "		data = null;\r\n"+
            "	}\r\n"+
            "	if (data == null) {\r\n"+
            "		var text = nw('div');\r\n"+
            "		var div = nw('div');\r\n"+
            "		var row = nw('div');\r\n"+
            "		div.appendChild(text);\r\n"+
            "		div.text = text;\r\n"+
            "		div.uid = uid;\r\n"+
            "\r\n"+
            "		data = { uid : uid,\r\n"+
            "		name : name,\r\n"+
            "		depth : depth,\r\n"+
            "		position : position,\r\n"+
            "		row : row,\r\n"+
            "		div : div,\r\n"+
            "		cells : []\r\n"+
            "		};\r\n"+
            "		this.data[position] = data;\r\n"+
            "	}else {\r\n"+
            "		data.name=name;\r\n"+
            "	}\r\n"+
            "	return data;\r\n"+
            "};\r\n"+
            "FastTree.prototype.setDataInitCells = function(data, cnt) {\r\n"+
            "	var row = data.row;\r\n"+
            "	var position = data.position;\r\n"+
            "	if(data.cells.length > 0)\r\n"+
            "		return;\r\n"+
            "    // Inserting in reverse order allows columns to be pinned to the front.\r\n"+
            "	for ( var i = cnt - 1; i >= 0; i--) {\r\n"+
            "		var cell = nw('div', 'tree_cell');\r\n"+
            "		cell.colpos = i;\r\n"+
            "		cell.data=data;\r\n"+
            "		data.cells[i] = cell;\r\n"+
            "		cell.onmousedown = this.onCellMouseDownFunc;\r\n"+
            "		cell.onmousemove = this.onCellMouseMoveFunc;\r\n"+
            "		if(this.leftCol<=i && i<=this.rightCol+1)\r\n"+
            "		  row.appendChild(cell);\r\n"+
            "	}\r\n"+
            "	data.cells[0].appendChild(data.div);\r\n"+
            "\r\n"+
            "};\r\n"+
            "FastTree.prototype.setDataAttachHandlers = function(data, hasChildren, iconClassName, iconStyle, hasCheckbox) {\r\n"+
            "	var div = data.div;\r\n"+
            "	var uid = data.uid;\r\n"+
            "	var that = this;\r\n"+
            "	if (!this.treatNameClickAsSelect)\r\n"+
            "		div.text.onmouseup = function(e) {\r\n"+
            "			that.onCellClicked(e, \"nameclicked\", uid);\r\n"+
            "		};\r\n"+
            "	else {\r\n"+
            "		div.text.onmouseup = function(e) {\r\n"+
            "			if (getMouseButton(e) == 2) { // right button\r\n"+
            "				that.onCellClicked(e, \"nameclicked\", uid);\r\n"+
            "			}\r\n"+
            "		};\r\n"+
            "	}\r\n"+
            "	if (hasChildren === true && div.expandIcon == null) {\r\n"+
            "		expandIcon = nw('div');\r\n"+
            "		div.appendChild(expandIcon);\r\n"+
            "		div.expandIcon = expandIcon;\r\n"+
            "		expandIcon.onmousedown = function(e) {\r\n"+
            "			e.stopPropagation();\r\n"+
            "			that.onCellClicked(e, \"expand\", uid);\r\n"+
            "		};\r\n"+
            "	}\r\n"+
            "	if (this.hideCheckBoxes == false && hasCheckbox === true && div.chkboxIcon == null) {\r\n"+
            "		chkboxIcon = nw('div');\r\n"+
            "		div.appendChild(chkboxIcon);\r\n"+
            "		div.chkboxIcon = chkboxIcon;\r\n"+
            "		chkboxIcon.onmousedown = function(e) {\r\n"+
            "			e.stopPropagation();\r\n"+
            "			that.onCellClicked(e, \"checkbox\", uid);\r\n"+
            "		};\r\n"+
            "	}\r\n"+
            "	if (iconClassName != null || iconStyle != null && div.customIcon == null) {\r\n"+
            "		customIcon = nw('div');\r\n"+
            "		div.appendChild(customIcon);\r\n"+
            "		div.customIcon = customIcon;\r\n"+
            "		if (!this.treatNameClickAsSelect)\r\n"+
            "			customIcon.onmousedown = function(e) {\r\n"+
            "				e.stopPropagation();\r\n"+
            "				that.onCellClicked(e, \"nameclicked\", uid);\r\n"+
            "			};\r\n"+
            "	}\r\n"+
            "\r\n"+
            "};\r\n"+
            "FastTree.prototype.setDataValueStyle=function(arguments, data){\r\n"+
            "	for ( var i = 1, n = 15; n < arguments.length;) {\r\n"+
            "		var value2 = arguments[n++];\r\n"+
            "		var style2 = arguments[n++];\r\n"+
            "		var cell = data.cells[i];\r\n"+
            "\r\n"+
            "		if (this.columns[i].jsFormatterType && this.columns[i].jsFormatterType === \"spark_line\") {\r\n"+
            "			if (cell.spark == null) {\r\n"+
            "				cell.spark = new InlineLineChart(cell, value2, \",\");\r\n"+
            "				cell.spark.drawSvg(cell.clientWidth, cell.clientHeight);\r\n"+
            "			} else {\r\n"+
            "				cell.spark.setText(value2, ',');\r\n"+
            "				cell.spark.drawSvg(cell.clientWidth, cell.clientHeight);\r\n"+
            "			}\r\n"+
            "		} else {\r\n"+
            "			cell.innerHTML = value2;\r\n"+
            "		}\r\n"+
            "\r\n"+
            "		cell.cellStyle = style2;\r\n"+
            "		applyStyle(cell, style2);\r\n"+
            "		i++;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "function getBg(val){\r\n"+
            "	var entries=(val==null) ? [] : val.split('\\|');\r\n"+
            "	for(var i=0;i<entries.length;i++){\r\n"+
            "		var keyValue=entries[i].split(/=(.*)?/);\r\n"+
            "		var key=keyValue[0];\r\n"+
            "		var value=keyValue[1];\r\n"+
            "		if(key==\"_bg\")\r\n"+
            "			return value;\r\n"+
            "	}\r\n"+
            "	return null;\r\n"+
            "}\r\n"+
            "FastTree.prototype.setDataCellsStyle=function(data, cnt, style){\r\n"+
            "	if (this.fontSize != null)\r\n"+
            "		data.div.style.height = this.fontSize;\r\n"+
            "	else\r\n"+
            "		data.div.style.height = this.defaultFontSize;\r\n"+
            "	data.row.style.height = toPx(this.rowHeight);\r\n"+
            "	var position = data.position;\r\n"+
            "	for ( var i = cnt - 1; i >= 0; i--) {\r\n"+
            "		var cell = data.cells[i] ;\r\n"+
            "		if (this.selectedRows.isSelected(data.position)) {\r\n"+
            "			cell.className='';\r\n"+
            "			cell.classList.add(\"tree_cell\");\r\n"+
            "			cell.classList.add(data.uid==this.activeRowUid ? this.activeClassname : this.selectedClassname);\r\n"+
            "		} else {\r\n"+
            "			cell.className = this.columns.length>1 && data.position % 2 ? 'tree_cell cell_greybar' : 'tree_cell cell_whitebar';\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		if(cell.cellStyle && (cell.cellStyle.includes(\"bg\")) && getBg(cell.cellStyle)!=undefined)//add check for the cell background color is not undefined(\"|bg=\"). If it is undefined then go to else block\r\n"+
            "			applyStyle(cell, cell.cellStyle);\r\n"+
            "		else {\r\n"+
            "			if (position % 2 == 0)\r\n"+
            "				applyStyle(cell, this.bgColor);\r\n"+
            "			else\r\n"+
            "				applyStyle(cell, this.grayBarColor);\r\n"+
            "		}\r\n"+
            "		if (!cell.cellStyle || cell.cellStyle && !cell.cellStyle.includes(\"fg\")) {\r\n"+
            "			applyStyle(cell, this.fontStyle);\r\n"+
            "		}\r\n"+
            "		cell.style.transition=this.flashStyle;\r\n"+
            "		cell.style.borderColor = this.cellBorderColor;\r\n"+
            "		cell.style.borderBottomWidth = this.cellBottomDivider;\r\n"+
            "		cell.style.borderRightWidth = this.cellRightDivider;\r\n"+
            "		cell.style.fontSize = this.fontSize;\r\n"+
            "		cell.style.justifyContent = this.verticalAlign;\r\n"+
            "		\r\n"+
            "		if(this.cellPadHt != null){\r\n"+
            "      		cell.style.paddingLeft = toPx(this.cellPadHt);\r\n"+
            "      		cell.style.paddingRight = toPx(this.cellPadHt);\r\n"+
            "  		}\r\n"+
            "	}\r\n"+
            "	applyStyle(data.cells[0], style);\r\n"+
            "}\r\n"+
            "	this.setDataTextIcons\r\n"+
            "FastTree.prototype.setDataTextIcons=function(data, expanded, cssClass, hasChildren, iconClassName, iconStyle, hasCheckbox, isChecked, hasChecked){\r\n"+
            "	var div = data.div;\r\n"+
            "	var expandIcon;\r\n"+
            "	var chkboxIcon;\r\n"+
            "	var customIcon;\r\n"+
            "	var text;\r\n"+
            "	// PROCESS TEXT\r\n"+
            "	text = div.text;\r\n"+
            "	text.innerHTML = data.name;\r\n"+
            "	if (cssClass)\r\n"+
            "		text.className = 'portal_tree_text ' + cssClass;\r\n"+
            "	else\r\n"+
            "		text.className = 'portal_tree_text';\r\n"+
            "\r\n"+
            "	var left = 0;\r\n"+
            "	applyStyle(data.row, data.rowStyle);\r\n"+
            "\r\n"+
            "	// PROCESS ICON\r\n"+
            "	expandIcon = div.expandIcon;\r\n"+
            "	chkboxIcon = div.chkboxIcon;\r\n"+
            "	customIcon = div.customIcon;\r\n"+
            "	if (hasChildren === true) {\r\n"+
            "		if (expanded === true) {\r\n"+
            "			expandIcon.innerHTML = \"&#x25bc;\"\r\n"+
            "			expandIcon.className = 'portal_tree_node_icon_expand';\r\n"+
            "		} else {\r\n"+
            "			expandIcon.innerHTML = \"&#x25ba;\"\r\n"+
            "			expandIcon.className = 'portal_tree_node_icon_collapse';\r\n"+
            "		}\r\n"+
            "		expandIcon.style.left = toPx(left);\r\n"+
            "		expandIcon.style.display=\"\";\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		if(expandIcon != null)\r\n"+
            "			expandIcon.style.display=\"none\";\r\n"+
            "	}\r\n"+
            "	left += 10;\r\n"+
            "	if (this.hideCheckBoxes == false && hasCheckbox === true) {\r\n"+
            "		chkboxIcon.className = 'portal_tree_node_icon_checkbox';\r\n"+
            "		if (isChecked) {\r\n"+
            "			chkboxIcon.innerHTML = \"&#x2714;\"// checkbox\r\n"+
            "			chkboxIcon.style.fontSize = '9px';\r\n"+
            "			chkboxIcon.style.padding = '0px 2px';\r\n"+
            "		} else if (hasChecked) {\r\n"+
            "			chkboxIcon.innerHTML = \"&#x25fe;\"// box\r\n"+
            "			chkboxIcon.style.fontSize = '7px';\r\n"+
            "			chkboxIcon.style.padding = '1px 3px';\r\n"+
            "		} else {\r\n"+
            "			chkboxIcon.innerHTML = \"\"// checkbox\r\n"+
            "		}\r\n"+
            "		chkboxIcon.style.border = '1px solid ' + div.style.color;\r\n"+
            "		chkboxIcon.style.width = '13px';\r\n"+
            "		chkboxIcon.style.height = '13px';\r\n"+
            "		chkboxIcon.style.top = '0px';\r\n"+
            "		chkboxIcon.style.left = toPx(left);\r\n"+
            "		left += 15;\r\n"+
            "	}\r\n"+
            "	if (customIcon != null) {\r\n"+
            "		customIcon.className = iconClassName == null ? \"portlet_custom_icon\" : (\"portlet_custom_icon \" + iconClassName);\r\n"+
            "		customIcon.style.left = toPx(left);\r\n"+
            "		applyStyle(customIcon, iconStyle);\r\n"+
            "		left += 18;\r\n"+
            "	}\r\n"+
            "	text.style.left = toPx(left);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.setData = function(uid, position, name, style, rowStyle, depth, expanded, cssClass, hasChildren, selected, iconClassName, iconStyle, hasCheckbox, isChecked,\r\n"+
            "		hasChecked) {\r\n"+
            "	var cnt = 1 + (arguments.length - 15) / 2;\r\n"+
            "	\r\n"+
            "	var data = this.setDataInitRow(uid, position, depth, name);\r\n"+
            "	data.style = style;\r\n"+
            "	data.formatValues = [] ;\r\n"+
            "	for(var i=15;i<arguments.length;i++){\r\n"+
            "		data.formatValues.push(arguments[i++]);\r\n"+
            "	}\r\n"+
            "	data.expanded = expanded;\r\n"+
            "	data.hasChildren = hasChildren;\r\n"+
            "	data.rowStyle = rowStyle;\r\n"+
            "	data.selected = selected;\r\n"+
            "	if (this.activeRowUid != -1 && this.activeRowUid == uid) {\r\n"+
            "		this.currentRow = position;\r\n"+
            "	}\r\n"+
            "	if (selected && !this.selectedRows.isSelected(position)) {\r\n"+
            "		this.selectedRows.add(position);\r\n"+
            "	} else if (!selected && this.selectedRows.isSelected(position)) {\r\n"+
            "		this.selectedRows.remove(position,null);\r\n"+
            "	}\r\n"+
            "	this.setDataInitCells(data, cnt);\r\n"+
            "	this.setDataCellsStyle(data, cnt, style);\r\n"+
            "	this.setDataAttachHandlers(data, hasChildren, iconClassName, iconStyle, hasCheckbox);\r\n"+
            "	this.setDataValueStyle(arguments, data);\r\n"+
            "	this.setDataTextIcons(data, expanded, cssClass, hasChildren, iconClassName, iconStyle, hasCheckbox, isChecked, hasChecked);\r\n"+
            "	this.setCellFlash(data, cnt,uid,style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "// borrowed from fastTable\r\n"+
            "FastTree.prototype.ensureRowVisible=function(row){\r\n"+
            "	var max= this.rowsCount-1;\r\n"+
            "	row=between(row,0,max);\r\n"+
            "	var rowTop = this.rowHeight * row;\r\n"+
            "	var rowBot = this.rowHeight * (row+1);\r\n"+
            "	var pageTop = this.scrollPane.getClipTop();\r\n"+
            "	var pageHeight = this.scrollPane.getClipHeight();\r\n"+
            "	if (this.scrollPane.hscrollVisible) {\r\n"+
            "		// page is smaller if we have horizontal scrollbar\r\n"+
            "		pageHeight -= this.scrollSize;\r\n"+
            "	}\r\n"+
            "	  \r\n"+
            "	var pos = null;\r\n"+
            "	  // Ensure the bottom of the row is above the bottom of the page\r\n"+
            "	if(pageTop + pageHeight < rowBot){ \r\n"+
            "		  pos = rowBot - pageHeight;\r\n"+
            "	}\r\n"+
            "	  // Ensure the top of the row is below the top of the page\r\n"+
            "	if(pageTop > rowTop) {\r\n"+
            "		  pos = rowTop;\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	if(pos != null) {\r\n"+
            "		t");
          out.print(
            "his.scrollPane.setClipTop(pos);\r\n"+
            "	}\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.expandRow = function(e) {\r\n"+
            "	var selected = this.visibleRowsByUid[this.activeRowUid];\r\n"+
            "	if (selected.hasChildren) {\r\n"+
            "		this.onCellClicked(e, \"expand\", this.data[this.currentRow].uid);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.handleKeyDown = function(e, diff) {\r\n"+
            "	var selected = this.visibleRowsByUid[this.activeRowUid];\r\n"+
            "	var nextPos = this.currentRow + diff;\r\n"+
            "	if (nextPos > this.rowsCount -1 || nextPos < 0) {\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	this.ensureRowVisible(nextPos);\r\n"+
            "	this.selectRow2(e, nextPos,null, false);\r\n"+
            "}\r\n"+
            "\r\n"+
            "// remove previous row's active style and set current row's active style\r\n"+
            "FastTree.prototype.setActiveStyle = function(prev, cur) {\r\n"+
            "		var data = this.data[cur];\r\n"+
            "		var prevData = this.data[prev];\r\n"+
            "		var cells = data.cells;\r\n"+
            "		\r\n"+
            "		for (var i = 0; i < cells.length; i++){\r\n"+
            "			cells[i].className = \"\";\r\n"+
            "			cells[i].classList.add(\"tree_cell\");\r\n"+
            "			cells[i].classList.add(this.activeClassname);\r\n"+
            "		}\r\n"+
            "		if (prevData != null) {\r\n"+
            "			var prevCells = prevData.cells;\r\n"+
            "			if (this.isSelected(prevData)) {\r\n"+
            "				for (var i = 0; i < prevCells.length; i++){\r\n"+
            "					prevCells[i].classList.remove(this.activeClassname);\r\n"+
            "					prevCells[i].classList.add(this.selectedClassname);\r\n"+
            "				}\r\n"+
            "			} else {\r\n"+
            "				for (var i = 0; i < prevCells.length; i++){\r\n"+
            "					prevCells[i].classList.remove(this.selectedClassname);\r\n"+
            "					prevCells[i].classList.remove(this.activeClassname);\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "}\r\n"+
            "\r\n"+
            "// different signature than selectRow()\r\n"+
            "FastTree.prototype.selectRow2 = function(e, row, col, isDragging) {\r\n"+
            "	var ctrlKey = e.ctrlKey;\r\n"+
            "	var shiftKey = e.shiftKey;\r\n"+
            "	var before = this.selectedRows.toString();\r\n"+
            "	var beforeCurrentRow = this.currentRow;\r\n"+
            "	if (!isDragging && this.dragSelect) {\r\n"+
            "		this.dragStart = row;\r\n"+
            "	}\r\n"+
            "	if (ctrlKey && shiftKey) {\r\n"+
            "		if (this.currentRow == -1)\r\n"+
            "			this.selectedRows.remove(row);\r\n"+
            "		else if (this.currentRow < row)\r\n"+
            "			this.selectedRows.remove(this.currentRow, row);\r\n"+
            "		else\r\n"+
            "			this.selectedRows.remove(row, this.currentRow);\r\n"+
            "	} else if (ctrlKey) {\r\n"+
            "		// this part is different from selectRow()\r\n"+
            "		if (beforeCurrentRow == -1) {\r\n"+
            "			return;\r\n"+
            "		}\r\n"+
            "		// if the next row is selected and the previous row is different from the next row\r\n"+
            "		if (this.selectedRows.isSelected(row)) {\r\n"+
            "			this.selectedRows.remove(beforeCurrentRow);\r\n"+
            "		} else  {\r\n"+
            "			this.selectedRows.add(row);\r\n"+
            "		} \r\n"+
            "	} else if (shiftKey) {\r\n"+
            "		if(this.currentRow==-1)\r\n"+
            "    		this.selectedRows.add(row);\r\n"+
            "    	else if(this.currentRow<row)\r\n"+
            "    		this.selectedRows.add(this.currentRow,row);\r\n"+
            "    	else\r\n"+
            "    		this.selectedRows.add(row,this.currentRow);\r\n"+
            "	} else if (this.dragSelect) {\r\n"+
            "		this.selectedRows.clear();\r\n"+
            "		if (row > this.dragStart)\r\n"+
            "			this.selectedRows.add(this.dragStart, row);\r\n"+
            "		else\r\n"+
            "			this.selectedRows.add(row, this.dragStart);\r\n"+
            "	} else {\r\n"+
            "		this.selectedRows.clear();\r\n"+
            "		this.selectedRows.add(row);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	// ensure something is selected after deselect from ctrl + click\r\n"+
            "	if (this.selectedRows.isSelected(row)) {\r\n"+
            "		this.currentRow = row;\r\n"+
            "	} else if (this.selectedRows.isSelected(beforeCurrentRow)) {\r\n"+
            "		this.currentRow = beforeCurrentRow;\r\n"+
            "	} else if (!this.selectedRows.isEmpty()) {\r\n"+
            "		this.currentRow = this.selectedRows.getNext();\r\n"+
            "	} else\r\n"+
            "		this.currentRow = -1;\r\n"+
            "	var after = this.selectedRows.toString();\r\n"+
            "	if (this.currentRow != beforeCurrentRow) {\r\n"+
            "		this.setActiveStyle(beforeCurrentRow, this.currentRow);		\r\n"+
            "	}\r\n"+
            "	this.activeRowUid = this.currentRow!= -1?this.data[this.currentRow].uid:-1;\r\n"+
            "	\r\n"+
            "	if (after!=before || this.currentRow!=beforeCurrentRow)\r\n"+
            "		this.onUserSelected(e, this.activeRowUid, after, col, this.activeRowUid);\r\n"+
            "\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.updatePinnedColumns = function() {\r\n"+
            "	if (!this.pinning)\r\n"+
            "		return;\r\n"+
            "	//Moves column headers to pinned location\r\n"+
            "    var h=Math.round(this.scrollPane.getClipLeft());\r\n"+
            "    var rect=new Rect();\r\n"+
            "    //QuickFilter\r\n"+
            "	var fullHeaderHeight = this.quickColumnFilterHidden == false? this.headerHeightPx + this.quickColumnFilterHeight : this.headerHeightPx;\r\n"+
            "    for(var i=0;i<this.pinning && i<this.columns.length;i++){\r\n"+
            "      rect.setLocation(h+this.columns[i].offset,0,this.columns[i].width,fullHeaderHeight).writeToElement(this.columns[i].headerElement);\r\n"+
            "      var box = this.getColumnBox(i);\r\n"+
            "      rect.setLocation(h+box.x-5,0,7,fullHeaderHeight).writeToElement(this.columns[i].grabElement);\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.updateAllWidths = function() {\r\n"+
            "	var rowsCount = this.visibleRowsCount + 2;\r\n"+
            "	var that = this;\r\n"+
            "	for ( var cellY = 0; cellY < this.visibleRowsCount + 2; cellY++) {\r\n"+
            "		var y = cellY + this.upperRow;\r\n"+
            "		var data = this.data[y];\r\n"+
            "		if (data != null)\r\n"+
            "			this.updateWidths(data);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTree.prototype.updateWidths = function(data) {\r\n"+
            "	var div = data.div;\r\n"+
            "	var left = data.depth + this.leftPaddingPx;\r\n"+
            "	var cells = data.cells;\r\n"+
            "	if (this.columns.length > 1) {\r\n"+
            "		var width = this.columns[0].width;\r\n"+
            "		if (width <= left) {\r\n"+
            "			left = width - 1;\r\n"+
            "		}\r\n"+
            "		div.style.left = toPx(left);\r\n"+
            "		div.style.width = toPx(width - left);\r\n"+
            "	} else {\r\n"+
            "		div.style.left = toPx(left);\r\n"+
            "		div.style.width = '100%';\r\n"+
            "	}\r\n"+
            "	var isSelected=this.isSelected(data);\r\n"+
            "	var isActive=data.uid==this.activeRowUid;\r\n"+
            "	data.row.style.width = this.columns.length>1 ? toPx(this.totalWidth) : '100%';\r\n"+
            "	for ( var x = 0; x < this.columns.length; x++) {\r\n"+
            "		var pos = 0;\r\n"+
            "		var cell = data.cells[x];\r\n"+
            "	    if(x<this.pinning || this.leftCol<=x && x<=this.rightCol+1){\r\n"+
            "	    	var col=this.columns[x];\r\n"+
            "	        if(cell.parentNode!=data.row)\r\n"+
            "	            data.row.appendChild(cell);\r\n"+
            "		    if(isSelected){\r\n"+
            "				var wasActive = !isActive && cell.className.includes(this.activeClassname);\r\n"+
            "				cell.className = \"\";\r\n"+
            "				cell.classList.add(\"tree_cell\");\r\n"+
            "				cell.classList.add(isActive ? this.activeClassname : this.selectedClassname);\r\n"+
            "				cell.style.transition = cell.isFlashing == true && !wasActive? cell.style.transition : '';\r\n"+
            "		    } else {\r\n"+
            "				cell.className = this.columns.length>1 && data.position % 2 ? 'tree_cell cell_greybar' : 'tree_cell cell_whitebar';\r\n"+
            "		    }\r\n"+
            "		    if(data.rowStyle){\r\n"+
            "			  applyStyle(cell, data.rowStyle);\r\n"+
            "			  applyStyle(cell, cell.cellStyle);\r\n"+
            "		    }\r\n"+
            "		    if (x < this.pinning){\r\n"+
            "				cell.style.left=toPx(col.offset + this.scrollPane.getClipLeft());\r\n"+
            "				cell.style.zIndex=100;\r\n"+
            "		    }else \r\n"+
            "		        cell.style.left=toPx(col.offset);\r\n"+
            "		    cell.style.width=this.columns.length >1 ? toPx(col.width) : '100%';\r\n"+
            "		    if (this.columns[x].jsFormatterType && this.columns[x].jsFormatterType === \"spark_line\") {\r\n"+
            "			    var value = cell.textContent;\r\n"+
            "			    if (cell.spark == null) {\r\n"+
            "			    } else {\r\n"+
            "				    cell.spark.drawSvg(cell.clientWidth, cell.clientHeight);\r\n"+
            "			    }\r\n"+
            "		    }else if (this.columns[x].jsFormatterType && this.columns[x].jsFormatterType === \"checkbox\") {\r\n"+
            "				var value = data.formatValues[x-1];\r\n"+
            "				var style = data.style;\r\n"+
            "		    	if(value !== \"N/A\" ){\r\n"+
            "			    	if (cell.checkbox == null) {\r\n"+
            "			    		cell.checkbox = new CheckboxField();\r\n"+
            "						cell.checkbox.element.onmousedown = function() {\r\n"+
            "							// do nothing\r\n"+
            "						}\r\n"+
            "			    	}\r\n"+
            "\r\n"+
            "			    	cell.innerHTML = \"\";	    \r\n"+
            "					this.applyCellCheckboxConfig(cell, value, style); \r\n"+
            "					 \r\n"+
            "				} else{\r\n"+
            "					cell.innerHTML=value;\r\n"+
            "		    		cell.oldValue=value;\r\n"+
            "		    		cell.checkbox = null;\r\n"+
            "				} \r\n"+
            "			}\r\n"+
            "	    }else if(cell.parentNode==data.row)\r\n"+
            "	            data.row.removeChild(cell);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.applyCellCheckboxConfig=function(cell, value, style) {\r\n"+
            "	cell.isChecked = value;\r\n"+
            "	cell.checkboxStyles = style;\r\n"+
            "	\r\n"+
            "	// bg and fg\r\n"+
            "	var fgIndex = cell.checkboxStyles.indexOf(\"_fg\");\r\n"+
            "	var bgIndex = cell.checkboxStyles.indexOf(\"_bg\");\r\n"+
            "	var checkColor;\r\n"+
            "	var checkboxColor;\r\n"+
            "	if (fgIndex != -1)\r\n"+
            "		checkColor = cell.checkboxStyles.substring(fgIndex + 4, fgIndex + 11);\r\n"+
            "	if (bgIndex != -1) {\r\n"+
            "		checkboxColor = cell.checkboxStyles.substring(bgIndex + 4, bgIndex + 11);\r\n"+
            "	}\r\n"+
            "	cell.checkbox.applyStyles(checkboxColor, checkColor, checkboxColor);\r\n"+
            "	\r\n"+
            "	// Alignment \r\n"+
            "	var fmIndex = cell.checkboxStyles.indexOf(\"_fm\");\r\n"+
            "	if (fmIndex != -1) {\r\n"+
            "		if (cell.checkboxStyles.indexOf(\"left\") != -1)\r\n"+
            "			cell.style.alignItems = \"flex-start\";\r\n"+
            "		else if (cell.checkboxStyles.indexOf(\"center\") != -1)\r\n"+
            "			cell.style.alignItems = \"center\";\r\n"+
            "		else if (cell.checkboxStyles.indexOf(\"right\") != -1)\r\n"+
            "			cell.style.alignItems = \"flex-end\";\r\n"+
            "		\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	// others\r\n"+
            "	cell.checkbox.element.style.pointerEvents = \"none\";\r\n"+
            "	cell.checkbox.setValue(value);\r\n"+
            "	cell.checkbox.setSize(cell.clientHeight - parseInt(this.cellBottomDivider, 10) - 5, cell.clientHeight - parseInt(this.cellBottomDivider, 10) - 5);\r\n"+
            "	cell.checkbox.element.style.margin = \"2px 0px 0px\";\r\n"+
            "	cell.appendChild(cell.checkbox.element);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTree.prototype.isSelected = function(data) {\r\n"+
            "	if (this.mouseDraggingStartPos == null)\r\n"+
            "		return data.selected;\r\n"+
            "	if (data.position >= this.mouseDraggingStartPos && data.position <= this.mouseDraggingEndPos)\r\n"+
            "		return true;\r\n"+
            "	if (data.position >= this.mouseDraggingEndPos && data.position <= this.mouseDraggingStartPos)\r\n"+
            "		return true;\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//MOBILE SUPPORT - create glass for scroll\r\n"+
            "FastTree.prototype.createGlass=function(){\r\n"+
            "	var that = this;\r\n"+
            "	this.disabledGlassDiv=nw('div', \"disable_glass_clear\");\r\n"+
            "	this.scrollPaneElement.style.zIndex=10000;\r\n"+
            "	this.treeElement.appendChild(this.disabledGlassDiv);\r\n"+
            "}\r\n"+
            "FastTree.prototype.hideGlass=function(){\r\n"+
            "	var count = this.treeElement.childElementCount;\r\n"+
            "	const elementsToRemove = this.treeElement.querySelectorAll(\".disable_glass_clear\");\r\n"+
            "	elementsToRemove.forEach(element => {this.treeElement.removeChild(element);});\r\n"+
            "	this.disabledGlassDiv=null;\r\n"+
            "	this.scrollPaneElement.style.zIndex=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//MOBILE SUPPORT - on touch start\r\n"+
            "FastTree.prototype.onTouchDragStart=function(e){\r\n"+
            "	this.currTarget = getMouseTarget(e);\r\n"+
            "	this.currPoint = getMousePoint(e);\r\n"+
            "	// setting timer for context menu\r\n"+
            "	this.longTouchTimer = setTimeout(() => {\r\n"+
            "		this.isSelecting = true;\r\n"+
            "		this.hideGlass();\r\n"+
            "		this.selectRow2(e,this.currTarget.data.position,this.currTarget.colpos,false);\r\n"+
            "  }, 500); // Long touch thres");
          out.print(
            "hold (500ms)\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.onTouchDragMove=function(e){\r\n"+
            "	//drag seelct. \r\n"+
            "	if(this.isSelecting === true){\r\n"+
            "		this.hideGlass();\r\n"+
            "		var t = getMousePoint(e);\r\n"+
            "		var t1 = document.elementFromPoint(t.x, t.y);\r\n"+
            "		if(t1 == null)\r\n"+
            "			return;\r\n"+
            "		this.dragSelect = true;\r\n"+
            "		this.selectRow2(e,t1.data.position,t1.colpos,true);\r\n"+
            "	}\r\n"+
            "	//drag scroll\r\n"+
            "	else{\r\n"+
            "		clearTimeout(this.longTouchTimer);\r\n"+
            "		this.longTouchTimer=null;\r\n"+
            "		var that = this;\r\n"+
            "		if(e.target != document.querySelector(\".disable_glass_clear\")){\r\n"+
            "			var newRef = this.treeElement.querySelectorAll(\".disable_glass_clear\");\r\n"+
            "			that.target = newRef[0];\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		var diffx = that.currPoint.x - getMousePoint(e).x;\r\n"+
            "		var diffy = that.currPoint.y - getMousePoint(e).y;\r\n"+
            "		that.scrollPane.hscroll.goPage(0.01 * diffx);\r\n"+
            "		that.scrollPane.vscroll.goPage(0.01 * diffy);\r\n"+
            "		that.currPoint = getMousePoint(e);\r\n"+
            "		\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.onCellMouseUp = function(e) {\r\n"+
            "	this.stopOutsideDragging();\r\n"+
            "	this.dragSelect = false;\r\n"+
            "	var doc = getDocument(this.containerElement).onmouseup = null;\r\n"+
            "}\r\n"+
            "FastTree.prototype.onCellMouseMove = function(e) {\r\n"+
            "	var cell = getMouseTarget(e);\r\n"+
            "	var data=cell.data;\r\n"+
            "	var shiftKey = e.shiftKey;\r\n"+
            "	var ctrlKey = e.ctrlKey;\r\n"+
            "	while (data == null) {\r\n"+
            "		cell = cell.parentNode;\r\n"+
            "		if (cell == null)\r\n"+
            "			return;\r\n"+
            "		data = cell.data;\r\n"+
            "	}\r\n"+
            "	var col = cell.colpos;\r\n"+
            "	if (this.columns[col].clickable && !shiftKey && !ctrlKey && e.buttons == 0 && this.selectedRows.isSelected(data.position)) {\r\n"+
            "		var top = this.getUpperRowVisible();\r\n"+
            "		var bot = this.getLowerRowVisible();\r\n"+
            "		while (top <= bot) {\r\n"+
            "			var cell = this.cellElements.get(col, top - this.upperRow);\r\n"+
            "			if (cell != null) {\r\n"+
            "				if (this.selectedRows.isSelected(top)) {\r\n"+
            "					cell.style.textDecoration = 'underline';\r\n"+
            "					cell.style.cursor = 'pointer';\r\n"+
            "				} else {\r\n"+
            "					cell.style.textDecoration = null;\r\n"+
            "					cell.style.cursor = null;\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "			top++;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTree.prototype.onCellMouseDown = function(e) {\r\n"+
            "	var that = this;\r\n"+
            "	var shiftKey = e.shiftKey;\r\n"+
            "	var ctrlKey = e.ctrlKey;\r\n"+
            "	var cell = getMouseTarget(e);\r\n"+
            "	var data = cell.data;\r\n"+
            "	while (data == null) {\r\n"+
            "		cell = cell.parentNode;\r\n"+
            "		if (cell == null)\r\n"+
            "			return;\r\n"+
            "		data = cell.data;\r\n"+
            "	}\r\n"+
            "	var col = cell.colpos;\r\n"+
            "	\r\n"+
            "	var doc = getDocument(this.containerElement);\r\n"+
            "	doc.onmouseup = this.onCellMouseUpFunc;\r\n"+
            "	doc.onmousemove=function(e){that.onOutsideMouseDragging(e);};\r\n"+
            "	var button = getMouseButton(e);\r\n"+
            "	if (button == 2) { // right click\r\n"+
            "		if (!this.selectedRows.isSelected(data.position)) {\r\n"+
            "			this.selectedRows.clear();\r\n"+
            "			this.selectRow(data.position, col, shiftKey, ctrlKey, false);\r\n"+
            "		} \r\n"+
            "		this.contextMenuPoint = getMousePoint(e).move(-4, -4);\r\n"+
            "		this.contextMenuCurrentColumn = -1;\r\n"+
            "		var col = cell.colpos;\r\n"+
            "		if (this.onUserSelected)\r\n"+
            "			this.onUserSelected(null, this.activeRowUid, this.selectedRows.toString(), col);\r\n"+
            "	} else {\r\n"+
            "		if (!ctrlKey && !shiftKey) {\r\n"+
            "			var clickable = (this.columns[col].clickable && this.selectedRows.isSelected(row));\r\n"+
            "			if (clickable)\r\n"+
            "				return;\r\n"+
            "		}\r\n"+
            "		if (!e.shiftKey && !e.ctrlKey) {\r\n"+
            "			this.dragSelect = true;\r\n"+
            "		}\r\n"+
            "		this.selectRow(data.position, col, shiftKey, ctrlKey, false);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTree.prototype.onOutsideMouseDragging = function(e) {\r\n"+
            "	var point = getMousePoint(e);\r\n"+
            "	var r = new Rect();\r\n"+
            "	r.readFromElement(this.containerElement);\r\n"+
            "	if (r.getBottom() < point.getY())\r\n"+
            "		this.outsideMouseDraggingDelta = (point.getY() - r.getBottom()) / 50;\r\n"+
            "	else if (r.getTop() > point.getY())\r\n"+
            "		this.outsideMouseDraggingDelta = (point.getY() - r.getTop()) / 50;\r\n"+
            "	else {\r\n"+
            "		this.outsideMouseDraggingDelta = 0;\r\n"+
            "		if (this.dragSelect && isMouseInside(e, this.containerElement, 0)) {\r\n"+
            "			var cell = getMouseTarget(e);\r\n"+
            "			var data = cell.data;\r\n"+
            "			while (data == null) {\r\n"+
            "				cell = cell.parentNode;\r\n"+
            "				if (cell == null)\r\n"+
            "					return;\r\n"+
            "				data = cell.data;\r\n"+
            "			}\r\n"+
            "			var col = cell.colpos;\r\n"+
            "			this.selectRow(data.position, col, false, false, true);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "\r\n"+
            "}\r\n"+
            "FastTree.prototype.stopOutsideDragging = function() {\r\n"+
            "	if (this.dragOutsideTimer != null) {\r\n"+
            "		window.clearInterval(this.dragOutsideTimer);\r\n"+
            "		this.dragOutsideTimer = null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTree.prototype.selectRow = function(row, col, shiftKey, ctrlKey, isDragging) {\r\n"+
            "	var before = this.selectedRows.toString();\r\n"+
            "	var beforeCurrentRow = this.currentRow;\r\n"+
            "	if (!isDragging && this.dragSelect) {\r\n"+
            "		this.dragStart = row;\r\n"+
            "	}\r\n"+
            "	if (ctrlKey && shiftKey) {\r\n"+
            "		if (this.currentRow == -1)\r\n"+
            "			this.selectedRows.remove(row);\r\n"+
            "		else if (this.currentRow < row)\r\n"+
            "			this.selectedRows.remove(this.currentRow, row);\r\n"+
            "		else\r\n"+
            "			this.selectedRows.remove(row, this.currentRow);\r\n"+
            "	} else if (ctrlKey) {\r\n"+
            "		if (this.selectedRows.isSelected(row)) {\r\n"+
            "			this.selectedRows.remove(row);\r\n"+
            "		} else\r\n"+
            "			this.selectedRows.add(row);\r\n"+
            "		\r\n"+
            "	} else if (shiftKey) {\r\n"+
            "		if (this.currentRow == -1)\r\n"+
            "			this.selectedRows.add(row);\r\n"+
            "		else if (this.currentRow < row)\r\n"+
            "			this.selectedRows.add(this.currentRow, row);\r\n"+
            "		else\r\n"+
            "			this.selectedRows.add(row, this.currentRow);\r\n"+
            "	} else if (this.dragSelect) {\r\n"+
            "		this.selectedRows.clear();\r\n"+
            "		if (row > this.dragStart)\r\n"+
            "			this.selectedRows.add(this.dragStart, row);\r\n"+
            "		else\r\n"+
            "			this.selectedRows.add(row, this.dragStart);\r\n"+
            "	} else {\r\n"+
            "		this.selectedRows.clear();\r\n"+
            "		this.selectedRows.add(row);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	// ensure something is selected after deselect from ctrl + click\r\n"+
            "	if (this.selectedRows.isSelected(row)) {\r\n"+
            "		this.currentRow = row;\r\n"+
            "	} else if (this.selectedRows.isSelected(beforeCurrentRow)) {\r\n"+
            "		this.currentRow = beforeCurrentRow;\r\n"+
            "	} else if (!this.selectedRows.isEmpty()) {\r\n"+
            "		this.currentRow = this.selectedRows.getNext();\r\n"+
            "	} else\r\n"+
            "		this.currentRow = -1;\r\n"+
            "\r\n"+
            "	var after = this.selectedRows.toString();\r\n"+
            "	this.activeRowUid = this.currentRow!= -1?this.data[this.currentRow].uid:-1;\r\n"+
            "		this.onUserSelected(null, this.activeRowUid, after, col, this.activeRowUid);\r\n"+
            "\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "// Deprecated?\r\n"+
            "FastTree.prototype.onSelectMouseMove = function(e, data) {\r\n"+
            "	if (this.mouseDraggingStart == null)\r\n"+
            "		return;\r\n"+
            "	this.onUserSelected(e, \"selected_range\", this.mouseDraggingStart + \"-\" + data.uid, this.getColAt(e));\r\n"+
            "	this.activeRowUid = data.uid;\r\n"+
            "	this.mouseDraggingEndPos = data.position;\r\n"+
            "	this.updateAllWidths();\r\n"+
            "}\r\n"+
            "\r\n"+
            "// Deprecated?\r\n"+
            "FastTree.prototype.onSelectMouseUp = function(e,data){	\r\n"+
            "	var that=this;\r\n"+
            "	if (this.mouseDraggingStart == null)\r\n"+
            "		return;\r\n"+
            "	this.onUserSelected(e, \"selected_range\", this.mouseDraggingStart + \"-\" + data.uid, this.getColAt(e));\r\n"+
            "	this.activeRowUid = data.uid;\r\n"+
            "	this.mouseDraggingStartPos = this.mouseDraggingStartPos;\r\n"+
            "	this.mouseDraggingEndPos = data.position;\r\n"+
            "	this.updateAllWidths();\r\n"+
            "	this.mouseDraggingStart = null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "// Deprecated?\r\n"+
            "FastTree.prototype.onSelectMouseDown = function(e, data) {\r\n"+
            "	var shiftKey = e.shiftKey;\r\n"+
            "	var ctrlKey = e.ctrlKey;\r\n"+
            "	var button = getMouseButton(e);\r\n"+
            "	this.contextMenuPoint = getMousePoint(e).move(-4, -4);\r\n"+
            "	if (!ctrlKey && !shiftKey && button == 1) {\r\n"+
            "		this.mouseDraggingStart = data.uid;\r\n"+
            "		this.mouseDraggingStartPos = data.position;\r\n"+
            "		this.mouseDraggingEndPos = data.position;\r\n"+
            "	}\r\n"+
            "	this.contextMenuCurrentColumn = -1;\r\n"+
            "	this.activeRowUid = data.uid;\r\n"+
            "	this.onUserSelected(e, \"selected\", data.uid, this.getColAt(e));\r\n"+
            "	this.updateAllWidths();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.onCellClicked = function(e, action, uid) {\r\n"+
            "	this.contextMenuPoint = getMousePoint(e).move(-4, -4);\r\n"+
            "	this.contextMenuCurrentColumn = -1;\r\n"+
            "	this.onUserSelectedTree(e, action, uid, this.getColAt(e));\r\n"+
            "}\r\n"+
            "FastTree.prototype.getColAt = function(mouseEvent) {\r\n"+
            "	return getMouseTarget(mouseEvent).colpos;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.setClipOffset = function(upperRow) {\r\n"+
            "	var delta = upperRow - this.upperRow;\r\n"+
            "	if (delta == 0)\r\n"+
            "		return;\r\n"+
            "	this.upperRow = upperRow;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.setSize = function(x, y) {\r\n"+
            "	this.rowsCount = y;\r\n"+
            "	this.totalHeight = y * this.rowHeight + this.topPaddingPx + this.headerHeightPx;\r\n"+
            "	if (y < this.getLowerRowVisible()) {\r\n"+
            "		this.upperRow = Math.max(y - this.visibleRowsCount, 0);\r\n"+
            "	}\r\n"+
            "	this.updateCells();\r\n"+
            "	this.updatePinnedBorder();\r\n"+
            "}\r\n"+
            "FastTree.prototype.getLowerRowVisible = function() {\r\n"+
            "	return this.upperRow + this.visibleRowsCount;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.leftCellsOffset = 0;\r\n"+
            "FastTree.prototype.pinnedLeftCellsOffset = 0;\r\n"+
            "FastTree.prototype.pinnedTopCellsOffset = 0;\r\n"+
            "FastTree.prototype.onScroll = function() {\r\n"+
            "	this.updateScrollTable();\r\n"+
            "	if(this.scrollPane.hscrollChanged != 0){\r\n"+
            "		this.updatePinnedBorder();\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.leftCellsOffset += this.scrollPane.hscrollChanged; \r\n"+
            "	// without below two lines, cells won't show up as you scroll down into invisible rows\r\n"+
            "	var v = Math.round(this.scrollPane.getClipTop()) / this.rowHeight;\r\n"+
            "	this.setClipOffset(Math.floor(v));\r\n"+
            "	\r\n"+
            "	// ensures pinned column stays when horizontal scroll bar moves\r\n"+
            "	this.updatePinnedColumns();\r\n"+
            "	if (this.onUpdateCellsEnd != null)\r\n"+
            "		this.onUpdateCellsEnd(this);\r\n"+
            "	\r\n"+
            "	var leftCol=this.leftCol;\r\n"+
            "	var rightCol=this.rightCol;\r\n"+
            "	this.determineColumnClipping();\r\n"+
            "	this.updateCells();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.determineColumnClipping = function(){	\r\n"+
            "	var leftCol=this.pinning;\r\n"+
            "	var rightCol=this.columns.length;\r\n"+
            "	var clipLeft=this.scrollPane.getClipLeft();\r\n"+
            "	var clipRight=clipLeft+this.scrollPane.getClipWidth();\r\n"+
            "	for(var i=this.pinning;i<this.columns.length;i++){\r\n"+
            "		var box=this.getColumnBox(i);\r\n"+
            "		if(box.x<=clipLeft && clipLeft<=box.x+box.w)\r\n"+
            "			leftCol=i;\r\n"+
            "		if(box.x<=clipRight && clipRight<=box.x+box.w)\r\n"+
            "			rightCol=i;\r\n"+
            "	}\r\n"+
            "	this.leftCol=leftCol;\r\n"+
            "	this.rightCol=rightCol;\r\n"+
            "}\r\n"+
            "FastTree.prototype.getLeftColVisible = function(){	\r\n"+
            "	return this.leftCol;\r\n"+
            "}\r\n"+
            "FastTree.prototype.getRightColVisible = function(){	\r\n"+
            "	return this.rightCol;\r\n"+
            "}\r\n"+
            "FastTree.prototype.updateScrollTable = function(){	\r\n"+
            "  //Update scroll position\r\n"+
            "  var h=Math.round(this.scrollPane.getClipLeft());\r\n"+
            "  this.headerElement.style.left=toPx(-h);\r\n"+
            "  this.treeElement.style.left=toPx(-h);\r\n"+
            "}\r\n"+
            "FastTree.prototype.getUpperRowVisible = function() {\r\n"+
            "	return this.upperRow;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.getLowerRowVisible = function() {\r\n"+
            "	return this.upperRow + this.visibleRowsCount");
          out.print(
            ";\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.showContextMenu = function(menu) {\r\n"+
            "	this.createMenu(menu).show(this.contextMenuPoint);\r\n"+
            "}\r\n"+
            "FastTree.prototype.createMenu = function(menu) {\r\n"+
            "	var that = this;\r\n"+
            "	var r = new Menu(getWindow(this.containerElement));\r\n"+
            "	if (this.contextMenuCurrentColumn == -1)\r\n"+
            "	   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e,id);} );\r\n"+
            "	else\r\n"+
            "	   r.createMenu(menu, function(e,id){that.onUserHeaderMenuItem(e,id,that.contextMenuCurrentColumn);} );\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.setOptions = function(options) {\r\n"+
            "	// QuickFilter\r\n"+
            "	if(options.quickColumnFilterBgCl  != null)\r\n"+
            "		this.quickColumnBackgroundColor = options.quickColumnFilterBgCl;\r\n"+
            "	if(options.quickColumnFilterFontCl != null)\r\n"+
            "		this.quickColumnFontColor = options.quickColumnFilterFontCl;\r\n"+
            "	if(options.quickColumnFilterFontSz != null)\r\n"+
            "		this.quickColumnFontSz = options.quickColumnFilterFontSz;\r\n"+
            "	if(options.quickColumnFilterBdrCl  != null)\r\n"+
            "		this.quickColumnBorderColor = options.quickColumnFilterBdrCl;\r\n"+
            "	if(options.quickColumnFilterHidden != null)\r\n"+
            "		this.quickColumnFilterHidden=\"false\"==options.quickColumnFilterHidden?false:true;\r\n"+
            "	if(options.quickColumnFilterHeight != null)\r\n"+
            "		this.quickColumnFilterHeight = parseInt(options.quickColumnFilterHeight);\r\n"+
            "	//ScrollBorderColor\r\n"+
            "	{\r\n"+
            "		var borderColor = options.scrollBorderColor;\r\n"+
            "		this.scrollPane.hscroll.DOM.trackElement.style.borderColor=borderColor;\r\n"+
            "		this.scrollPane.hscroll.DOM.handleElement.style.borderColor=borderColor;\r\n"+
            "		this.scrollPane.hscroll.DOM.backElement.style.borderColor=borderColor;\r\n"+
            "		this.scrollPane.hscroll.DOM.forwardElement.style.borderColor=borderColor;\r\n"+
            "		this.scrollPane.vscroll.DOM.trackElement.style.borderColor=borderColor;\r\n"+
            "		this.scrollPane.vscroll.DOM.handleElement.style.borderColor=borderColor;\r\n"+
            "		this.scrollPane.vscroll.DOM.backElement.style.borderColor=borderColor;\r\n"+
            "		this.scrollPane.vscroll.DOM.forwardElement.style.borderColor=borderColor;\r\n"+
            "	}\r\n"+
            "	// scroll bar radius\r\n"+
            "	\r\n"+
            "	if (options.scrollBarRadius) {\r\n"+
            "		this.scrollPane.hscroll.DOM.applyBorderRadius(options.scrollBarRadius);\r\n"+
            "		this.scrollPane.vscroll.DOM.applyBorderRadius(options.scrollBarRadius);\r\n"+
            "	}\r\n"+
            "	// scroll bar hide arrows\r\n"+
            "	if (options.scrollBarHideArrows) {\r\n"+
            "		this.scrollPane.hscroll.DOM.hideArrows(options.scrollBarHideArrows);\r\n"+
            "		this.scrollPane.vscroll.DOM.hideArrows(options.scrollBarHideArrows);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if (options.fontSize != null) {\r\n"+
            "		this.fontSize = toPx(options.fontSize);\r\n"+
            "	}\r\n"+
            "	if (options.fontFamily != null) {\r\n"+
            "		this.fontFamily = options.fontFamily;\r\n"+
            "		applyStyle(this.treeElement, this.fontFamily);\r\n"+
            "	}if (options.rowHeight != null) {\r\n"+
            "		this.rowHeight = options.rowHeight;\r\n"+
            "	}\r\n"+
            "	if (options.headerFontSize != null) {\r\n"+
            "		this.headerFontSize = toPx(options.headerFontSize);\r\n"+
            "	}\r\n"+
            "	if (options.headerRowHeight != null) {\r\n"+
            "		this.headerHeightPx = options.headerRowHeight;\r\n"+
            "	}\r\n"+
            "	if (options.hideCheckBoxes != null)\r\n"+
            "		this.hideCheckBoxes = !!(options.hideCheckBoxes);\r\n"+
            "	if (options.headerDividerHidden != null)\r\n"+
            "		this.hideHeaderDivider = options.headerDividerHidden === \"true\" ? true : false;\r\n"+
            "	if (options.headerBarHidden != null) {\r\n"+
            "		this.headerBarHidden = options.headerBarHidden === \"true\" ? true : false;\r\n"+
            "	}\r\n"+
            "	if (options.searchBarHidden != null) {\r\n"+
            "		this.searchBarHidden = options.searchBarHidden == \"true\" ? true : false;\r\n"+
            "	}\r\n"+
            "	if (options.cellBottomDivider != null) {\r\n"+
            "		this.cellBottomDivider = toPx(options.cellBottomDivider);\r\n"+
            "	}else this.cellBottomDivider=\"0px\";\r\n"+
            "\r\n"+
            "	if (options.cellRightDivider != null) {\r\n"+
            "		this.cellRightDivider = toPx(options.cellRightDivider);\r\n"+
            "	}else this.cellRightDivider=\"1px\";\r\n"+
            "	if (options.verticalAlign != null) {\r\n"+
            "		this.verticalAlign = options.verticalAlign;\r\n"+
            "	}else this.verticalAlign = \"center\";\r\n"+
            "	applyStyle(this.scrollPane.DOM.paneElement, options.backgroundStyle);\r\n"+
            "	if (options.scrollBarWidth != null) {\r\n"+
            "		this.scrollSize = parseInt(options.scrollBarWidth);\r\n"+
            "		this.scrollPane.scrollSize = this.scrollSize;\r\n"+
            "	}\r\n"+
            "	if(options.cellPadHt != null)\r\n"+
            "		this.cellPadHt = options.cellPadHt;\r\n"+
            "	this.bgColor = options.backgroundStyle;\r\n"+
            "	this.fontStyle = options.fontStyle;\r\n"+
            "	this.grayBarColor = options.grayBarStyle;\r\n"+
            "	this.headerBgStyle = options.headerBgStyle;\r\n"+
            "	this.headerFontStyle = options.headerFontStyle;\r\n"+
            "	this.flashUpColor = options.flashUpColor;\r\n"+
            "	this.flashDnColor = options.flashDnColor;\r\n"+
            "	this.flashMs = options.flashMs; \r\n"+
            "	if (this.flashMs == null) \r\n"+
            "		this.flashMs = 0;\r\n"+
            "    this.flashStyle='background '+this.flashMs+'ms linear';\r\n"+
            "	\r\n"+
            "\r\n"+
            "	this.filteredColumnBgColor = options.filteredBg;\r\n"+
            "	this.filteredColumnFontColor = options.filteredFont;\r\n"+
            "	this.cellBorderColor = options.cellBorderStyle;\r\n"+
            "	this.treatNameClickAsSelect = options.treatNameClickAsSelect;\r\n"+
            "	if(options.cellActiveBg) // active selection background color\r\n"+
            "		this.activeClassname=options.cellActiveBg==null ? 'cell_active_default' : toCssForColor(this.treeElement,options.cellActiveBg);\r\n"+
            "	if(options.cellSelectedBg) // selection background color\r\n"+
            "		this.selectedClassname=options.cellSelectedBg==null ? 'cell_selected_default' : toCssForColor(this.treeElement,options.cellSelectedBg);\r\n"+
            "	applyStyle(this.scrollPane.vscroll.DOM.gripElement, options.gripColor);\r\n"+
            "	applyStyle(this.scrollPane.vscroll.DOM.trackElement, options.trackColor);\r\n"+
            "	applyStyle(this.scrollPane.vscroll.DOM.forwardElement, options.scrollButtonColor);\r\n"+
            "	applyStyle(this.scrollPane.vscroll.DOM.backElement, options.scrollButtonColor);\r\n"+
            "	applyStyle(this.scrollPane.hscroll.DOM.gripElement, options.gripColor);\r\n"+
            "	applyStyle(this.scrollPane.hscroll.DOM.trackElement, options.trackColor);\r\n"+
            "	applyStyle(this.scrollPane.hscroll.DOM.forwardElement, options.scrollButtonColor);\r\n"+
            "	applyStyle(this.scrollPane.hscroll.DOM.backElement, options.scrollButtonColor);\r\n"+
            "	\r\n"+
            "	this.scrollPane.hscroll.DOM.applyTinySquareColor(options.scrollBarCornerColor);\r\n"+
            "	this.scrollPane.vscroll.DOM.applyTinySquareColor(options.scrollBarCornerColor);\r\n"+
            "\r\n"+
            "	// Set scroll icon colors...\r\n"+
            "	scrollIconsColor = options.scrollIconsColor || '#000000';\r\n"+
            "	this.scrollPane.updateIconsColor(scrollIconsColor);\r\n"+
            "\r\n"+
            "	if (options.menuBarBg != null) {\r\n"+
            "		this.headerElement.style.backgroundColor = options.menuBarBg;\r\n"+
            "		this.menuBarBg = options.menuBarBg;\r\n"+
            "	} else {\r\n"+
            "		this.headerElement.style.backgroundColor = \"#535353\";\r\n"+
            "		applyStyle(this.headerElement, this.headerBgStyle);\r\n"+
            "		applyStyle(this.headerElement, this.headerFontStyle);\r\n"+
            "		if (this.hideHeaderDivider == true)\r\n"+
            "			this.headerElement.style.backgroundImage = \"none\";\r\n"+
            "	}\r\n"+
            "	if (options.menuFontColor != null) {\r\n"+
            "		this.headerElement.style.color = options.menuFontColor;\r\n"+
            "	} else {\r\n"+
            "		this.headerElement.style.color = \"white\";\r\n"+
            "	}\r\n"+
            "	this.updateColumns();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.updateHeaderBarHidden = function() {\r\n"+
            "	if (this.headerBarHidden == true || this.columns.length <= 0) {\r\n"+
            "		this.headerElement.style.display = \"none\";\r\n"+
            "		this.oldHeaderHeightPx = this.headerHeightPx;\r\n"+
            "		this.headerHeightPx = 0;\r\n"+
            "	} else {\r\n"+
            "		this.headerElement.style.display = \"block\";\r\n"+
            "		if (this.headerHeightPx == 0)\r\n"+
            "			this.headerHeightPx = this.oldHeaderHeightPx != null ? this.oldHeaderHeightPx : this.defaultHeaderHeightPx;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.initColumns = function(pinning, cols) {\r\n"+
            "	this.pinning = pinning;\r\n"+
            "	removeAllChildren(this.headerElement);\r\n"+
            "	this.columns = [];\r\n"+
            "	this.hiddenColumns = [];\r\n"+
            "    resetPushDraggableContainer(this.headerElement);\r\n"+
            "	for ( var i = 0; i < cols.length; i++) {\r\n"+
            "		var col = cols[i];\r\n"+
            "		var cssClass = col.cssClass;\r\n"+
            "		var id = col.id;\r\n"+
            "		var name = col.name;\r\n"+
            "		var visible = col.visible;\r\n"+
            "		var sort = col.sort;\r\n"+
            "		var clickable = col.clickable;\r\n"+
            "		var jsFormatterType = col.jsFormatterType;\r\n"+
            "		var className = '';\r\n"+
            "		var headerStyle = col.headerStyle;\r\n"+
            "		var filter = col.filter;\r\n"+
            "		var filterText = col.filterText;\r\n"+
            "		var hids = col.hids;\r\n"+
            "		if (sort == '')\r\n"+
            "			className = '';\r\n"+
            "		else if (sort == 1)\r\n"+
            "			className = '';\r\n"+
            "		else if (sort == 0)\r\n"+
            "			className = '';\r\n"+
            "		else if (sort == 3)\r\n"+
            "			className = 'asc';\r\n"+
            "		else if (sort == 2)\r\n"+
            "			className = 'des';\r\n"+
            "		if (col.filter)// {\r\n"+
            "			className = 'header_filtered ' + className;\r\n"+
            "		var width = col.width;\r\n"+
            "		if (visible) {\r\n"+
            "			var column = this.addColumn(id, name, width, className, cssClass, headerStyle, null, jsFormatterType, filter, filterText);\r\n"+
            "			column.setHIDS(hids);\r\n"+
            "			column.headerElement.style.backgroundColor = \"#535353\";\r\n"+
            "			applyStyle(column.headerElement, this.headerBgStyle);\r\n"+
            "			if (!column.columnHeaderStyle)\r\n"+
            "				applyStyle(column.headerElement, this.headerFontStyle);\r\n"+
            "			if (this.headerBarHidden == true)\r\n"+
            "				column.headerElement.style.backgroundImage = \"none\";\r\n"+
            "			if (col.filter) {\r\n"+
            "				column.hasFilter = true;\r\n"+
            "				if (this.filteredColumnBgColor != null) {\r\n"+
            "					applyStyle(column.headerElement, this.filteredColumnBgColor);\r\n"+
            "				} else {\r\n"+
            "					column.headerElement.style.backgroundColor = \"#ff7f00\";\r\n"+
            "				}\r\n"+
            "				if (this.filteredColumnFontColor != null) {\r\n"+
            "					applyStyle(column.headerElement, this.filteredColumnFontColor);\r\n"+
            "				} else {\r\n"+
            "					column.headerElement.style.color = \"white\";\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "			else{\r\n"+
            "				column.hasFilter = false;\r\n"+
            "			}\r\n"+
            "			column.clickable = clickable;\r\n"+
            "			if (i == 0) {\r\n"+
            "			}\r\n"+
            "		} else\r\n"+
            "			this.hiddenColumns[id] = name;\r\n"+
            "	}\r\n"+
            "	this.updateColumnWidths();\r\n"+
            "	this.updateColumns();\r\n"+
            "	this.updatePinnedBorder();\r\n"+
            "	this.updatePinnedColumns();\r\n"+
            "	this.updateQuickFilterStyles();\r\n"+
            "	this.determineColumnClipping();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.getColumnBox = function(colIndex) {\r\n"+
            "	var box = null;\r\n"+
            "	if (colIndex < this.columns.length) {\r\n"+
            "		box = {};\r\n"+
            "		var col = this.columns[colIndex];\r\n"+
            "		var header = col.headerElement;\r\n"+
            "\r\n"+
            "		var h1 = this.totalHeight; // Height of rows + header\r\n"+
            "		var h = this.containerElement.offsetHeight - this.scrollSize; // Height of the container - the scrollbar\r\n"+
            "		h = h < h1 ? h : h1;\r\n"+
            "\r\n"+
            "		var x = header.offsetLeft + header.offsetWidth;\r\n"+
            "		if (colIndex < this.pinning) {\r\n"+
            "			x -= this.scrollPane.hscroll.clipTop\r\n"+
            "		}\r\n"+
            "\r\n"+
            "		box.w = col.width;\r\n"+
            "		box.h = h;\r\n"+
            "		box.x = x;\r\n"+
            "		box.y = 0;\r\n"+
            "		box.i = colIndex;\r\n"+
            "	}\r\n"+
            "	return box;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.updatePinnedBorder = fun");
          out.print(
            "ction() {\r\n"+
            "	if(this.pinning == 0){\r\n"+
            "	   this.pinnedBorderElement.style.display = \"none\"; \r\n"+
            "	}\r\n"+
            "	if(this.pinning && this.pinning <= this.columns.length){\r\n"+
            "		var col = this.columns[this.pinning-1];\r\n"+
            "		var left = col.offset+ col.width;\r\n"+
            "		var height = this.containerElement.offsetHeight - this.scrollSize; // Height of the container - the scrollbar\r\n"+
            "		if(this.hideHeaderDivider == \"false\")\r\n"+
            "			height += parseInt(this.headerHeight);\r\n"+
            "		this.pinnedBorderElement.style.left = toPx(left);\r\n"+
            "		this.pinnedBorderElement.style.height = toPx(height);\r\n"+
            "		if(this.pinning && this.scrollPane.hscroll.clipTop > this.pinnedBorderWidth/2){\r\n"+
            "		  this.pinnedBorderElement.style.display = \"inherit\"; \r\n"+
            "		}else{\r\n"+
            "		  this.pinnedBorderElement.style.display = \"none\"; \r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTree.prototype.setPinnedBorderWidth = function(width) {\r\n"+
            "	this.pinnedBorderWidth = width;\r\n"+
            "	this.pinnedBorderElement.style.width = toPx(width);\r\n"+
            "}\r\n"+
            "FastTree.prototype.applyPinnedBorderStyle = function(style) {\r\n"+
            "	var dummyElement = {style:{}};\r\n"+
            "	applyStyle(dummyElement, style);\r\n"+
            "	Object.assign(this.pinnedBorderElement.style, dummyElement.style);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.setColumnWidth = function(location, width) {\r\n"+
            "	var rect = new Rect();\r\n"+
            "	this.columns[location].width = width;\r\n"+
            "	this.updateColumnWidths();\r\n"+
            "}\r\n"+
            "FastTree.prototype.updateColumnWidths = function(location, width) {\r\n"+
            "	this.totalWidth = 0;\r\n"+
            "	for ( var i = 0; i < this.columns.length; i++) {\r\n"+
            "		this.columns[i].offset = this.totalWidth;\r\n"+
            "		this.totalWidth += this.columns[i].width;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTree.prototype.addColumn = function(id, name, width, headerClassName, cellClassName, headerStyle, location, jsFormatterType, filter, filterText) {\r\n"+
            "	if (location == null)\r\n"+
            "		location = this.columns.length;\r\n"+
            "	var offset = location == 0 ? 0 : this.columns[location - 1].getRightOffset();\r\n"+
            "	var that=this; // owner\r\n"+
            "	var column = new FastTableColumn(id, name, offset, location, width, headerClassName, cellClassName != null ? cellClassName : \"\", true, headerStyle, that);\r\n"+
            "	\r\n"+
            "	if(id!=0){\r\n"+
            "	makePushDraggable(this.headerElement, column.headerElement, column.invisibleElement, true, false, null, \r\n"+
            "   		function(a,b,c){that.onDragColumnEndCallback(a,b,c);}, \r\n"+
            "   		function(a,b,c,d,e,f){return that.onDragMousePosition(a,b,c,d,e,f);}, \r\n"+
            "   	false);\r\n"+
            "   	}\r\n"+
            "	\r\n"+
            "	column.invisibleElement.location = location;\r\n"+
            "	column.headerElement.location = location;// TODO this is wrong if not adding to the end (following columns need to be updated!)\r\n"+
            "   column.invisibleElement.onclick=function(e){that.onHeaderClicked(e);};\r\n"+
            "	column.headerElement.style.borderColor = this.cellBorderColor;\r\n"+
            "	  column.buttonsUpElement.onmousedown=function(e){that.onHeaderButtonClicked(e,'__sort_3',column.location);};\r\n"+
            "	   column.buttonsDnElement.onmousedown=function(e){that.onHeaderButtonClicked(e,'__sort_2',column.location);};\r\n"+
            "	column.grabElement.style.zIndex = location < this.pinning ? '1' : '0';\r\n"+
            "	column.grabElement.style.opacity = \"0\";\r\n"+
            "	column.grabElement.location = location;\r\n"+
            "   column.grabElement.ondragging=function(e,x,y){that.onHeaderDragging(e,x,y);};\r\n"+
            "   column.grabElement.clipDragging=function(e,rect){that.clipHeaderDragging(e,rect);};\r\n"+
            "   column.grabElement.onmouseenter=function(e){that.showGrabElement(e, that, location);};\r\n"+
            "   column.grabElement.onmouseleave=function(e){that.hideGrabElement(e, that, location);};\r\n"+
            "	column.grabElement.style.borderColor = this.cellBorderColor;\r\n"+
            "	column.jsFormatterType = jsFormatterType;\r\n"+
            "	if (this.hideHeaderDivider == true)\r\n"+
            "		column.headerElement.style.backgroundImage = \"none\";\r\n"+
            "	makeDraggable(column.grabElement, column.grabElement, false, true);\r\n"+
            "	this.headerElement.insertBefore(column.headerElement, this.headerElement.firstChild);\r\n"+
            "	this.headerElement.insertBefore(column.grabElement, this.headerElement.firstChild);\r\n"+
            "	this.columns.splice(location, 0, column);\r\n"+
            "\r\n"+
            "   //TODOFILTER\r\n"+
            "   column.filter = filter;\r\n"+
            "   column.filterText = filterText;\r\n"+
            "   column.updateQuickFilterValue();\r\n"+
            "   \r\n"+
            "	var rect = new Rect();\r\n"+
            "	this.setColumnWidth(location, width);\r\n"+
            "	applyStyle(column.headerElement, column.columnHeaderStyle);\r\n"+
            "	return column;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.onDragColumnEndCallback=function(oldIndex,newIndex,success){\r\n"+
            "	if(oldIndex!=newIndex)\r\n"+
            "		this.callback('moveColumn',{oldPos:oldIndex,newPos:newIndex  });\r\n"+
            "}\r\n"+
            "FastTree.prototype.onDragMousePosition=function(mouseX, containerOffset, mouseOffset, elementWidth, moveX, moveY){\r\n"+
            "	if(moveY)\r\n"+
            "		return;\r\n"+
            "	\r\n"+
            "	var leftEdge = this.scrollPane.getClipLeft();\r\n"+
            "	var rightEdge = leftEdge + this.scrollPane.getClipWidth();\r\n"+
            "	var draggedLeftEdge = (mouseX - containerOffset - mouseOffset);\r\n"+
            "	var draggedRightEdge = (mouseX - containerOffset - mouseOffset + elementWidth);\r\n"+
            "	if(draggedLeftEdge < leftEdge){\r\n"+
            "		var np = leftEdge + this.scrollPane.getClipWidth() * -0.05;\r\n"+
            "		this.scrollPane.setClipLeft(np);\r\n"+
            "	}\r\n"+
            "	else if(draggedRightEdge > rightEdge){\r\n"+
            "		var np = leftEdge + this.scrollPane.getClipWidth() * 0.05;\r\n"+
            "		this.scrollPane.setClipLeft(np);\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	var moved = leftEdge != this.scrollPane.getClipLeft();\r\n"+
            "	return moved;\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.showGrabElement = function(e, that, location) {\r\n"+
            "	if (e.buttons == 0) {\r\n"+
            "		var col = that.columns[location].grabElement;\r\n"+
            "		col.style.zIndex = location < that.pinning ? '2' : '1';\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTree.prototype.hideGrabElement = function(e, that, location) {\r\n"+
            "	if (e.buttons == 0) {\r\n"+
            "		var col = that.columns[location].grabElement;\r\n"+
            "		col.style.zIndex = location < that.pinning ? '1' : '0';\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.onHeaderButtonClicked = function(e, id, col) {\r\n"+
            "	this.onUserHeaderMenuItem(e, id, col);\r\n"+
            "}\r\n"+
            "FastTree.prototype.onHeaderClicked = function(e) {\r\n"+
            "	e.stopPropagation();\r\n"+
            "	var target = getMouseTarget(e);\r\n"+
            "	if (this.menu != null)\r\n"+
            "		this.menu.hide();\r\n"+
            "	if (target == this.headerElement) {\r\n"+
            "		this.contextMenuCurrentColumn = -2;\r\n"+
            "		var point = new Rect().readFromElement(target).getLowerLeft();\r\n"+
            "		point.x = getMousePoint(e).x - 4;\r\n"+
            "		this.contextMenuPoint = point;\r\n"+
            "	} else {\r\n"+
            "		this.contextMenuCurrentColumn = target.location;\r\n"+
            "		var point = new Rect().readFromElement(target).getLowerLeft();\r\n"+
            "		this.contextMenuPoint = point;\r\n"+
            "	}\r\n"+
            "	if (this.onUserHeaderMenu != null && this.contextMenuCurrentColumn != null && this.headerBarHidden == false)\r\n"+
            "		this.onUserHeaderMenu(e, this.contextMenuCurrentColumn);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.onHeaderDragging = function(e, x, y) {\r\n"+
            "	var left = new Rect().readFromElementRelatedToParent(e).getLeft();\r\n"+
            "	var width = left - this.columns[e.location].offset + 3;\r\n"+
            "	if (e.location < this.pinning) {\r\n"+
            "		width -= fl(this.scrollPane.getClipLeft());\r\n"+
            "	}\r\n"+
            "	this.setColumnWidth(e.location, width);\r\n"+
            "	if (this.onUserColumnResize != null)\r\n"+
            "		this.onUserColumnResize(e, e.location, width);\r\n"+
            "	this.updateColumns();\r\n"+
            "	this.updatePinnedBorder();\r\n"+
            "	this.updatePinnedColumns();\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.clipHeaderDragging = function(e, rect) {\r\n"+
            "	var min = this.columns[e.location].offset + 5;\r\n"+
            "	if (min > rect.getLeft())\r\n"+
            "		rect.setLeft(min);\r\n"+
            "}\r\n"+
            "FastTree.prototype.ensurePosVisible = function(pos) {\r\n"+
            "	this.updateAllWidths();\r\n"+
            "	this.scrollPane.setClipTop(Math.max(0, pos * this.rowHeight - 100));\r\n"+
            "}\r\n"+
            "FastTree.prototype.setActiveRowUid = function(activeRow) {\r\n"+
            "	this.activeRowUid = activeRow;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.onWhiteSpaceMouseDown=function(e){\r\n"+
            "	var that=this;\r\n"+
            "	if(e.target != this.treeElement)\r\n"+
            "		return;\r\n"+
            "    if(e.shiftKey  || e.ctrlKey)\r\n"+
            "      return;\r\n"+
            "	var button=getMouseButton(e);\r\n"+
            "	if(button==2){\r\n"+
            "	  this.clearSelected(e);\r\n"+
            "	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "      this.contextMenuCurrentColumn=-1;\r\n"+
            "	  if(this.onUserContextMenu!=null)\r\n"+
            "		  this.onUserContextMenu(e);\r\n"+
            "	}else{\r\n"+
            "	  this.clearSelected(e);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.selectAll=function(e){\r\n"+
            "	this.selectedRows.add(0,this.rowsCount-1);\r\n"+
            "	this.onUserSelected(e,this.activeRowUid,this.selectedRows.toString(), this.getColumnAtPoint(MOUSE_POSITION_X),null);\r\n"+
            "}\r\n"+
            "FastTree.prototype.clearSelected=function(e){\r\n"+
            "	this.selectedRows.clear();\r\n"+
            "	this.currentRow = -1;\r\n"+
            "	this.onUserSelected(e,null,this.selectedRows.toString(), this.getColumnAtPoint(MOUSE_POSITION_X), null);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.setCellFlash=function(data,cnt,uid,style){\r\n"+
            "	for ( var i = cnt - 1; i >= 1; i--) {\r\n"+
            "		var cell = data.cells[i];\r\n"+
            "		var value = cell.textContent;\r\n"+
            "		if (this.flashMs>0){\r\n"+
            "			var oldValue=this.valueElements.set(i,uid,value);\r\n"+
            "		    if(oldValue && oldValue!=value && this){\r\n"+
            "				if(cell.flashTimeout!=null)\r\n"+
            "					clearTimeout(cell.flashTimeout);\r\n"+
            "				if (cell.resetTransition!=null)\r\n"+
            "					clearTimeout(cell.resetTransition);\r\n"+
            "				var flashDir;\r\n"+
            "				if(this.flashUpColor==this.flashDnColor)\r\n"+
            "					flashDir=1;\r\n"+
            "				else{\r\n"+
            "					var n1=parseNumber(oldValue);\r\n"+
            "					var n2=parseNumber(value);\r\n"+
            "					if(Number.isNaN(n1) || Number.isNaN(n2))\r\n"+
            "						flashDir=oldValue<value? 1 : 2;\r\n"+
            "					else\r\n"+
            "						flashDir=n1 < n2? 1 : 2;\r\n"+
            "			    }\r\n"+
            "				var that=this;\r\n"+
            "				cell.style.transition = '';\r\n"+
            "				cell.style.backgroundColor=flashDir==1 ? this.flashUpColor : this.flashDnColor;	\r\n"+
            "				cell.isFlashing = true;\r\n"+
            "				var func=function(cell2){\r\n"+
            "					cell2.flashTimeout=null;\r\n"+
            "					cell2.style.transition=this.flashStyle;\r\n"+
            "					that.applyStyle(cell2,style,true,data,cnt);\r\n"+
            "				};\r\n"+
            "				var func2=function(cell2){\r\n"+
            "					cell2.style.transition='';\r\n"+
            "					cell2.resetTransition=null;\r\n"+
            "					cell2.isFlashing = false;\r\n"+
            "				};\r\n"+
            "				cell.flashTimeout=window.setTimeout(func,this.flashMs, cell);\r\n"+
            "				cell.resetTransition=window.setTimeout(func2,this.flashMs*2, cell);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTree.prototype.applyStyle=function(target,val,force,data,cnt){\r\n"+
            "    if(!force && target.resetTransition!=null)\r\n"+
            "        return;\r\n"+
            "	target.className='';\r\n"+
            "	this.setDataCellsStyle(data, cnt, val);\r\n"+
            "	if(val != null && \"\" != val && target.checkbox == null)\r\n"+
            "		applyStyle(target,val);\r\n"+
            "	if(target.cellClassName){\r\n"+
            "		target.className+=\" \"+target.cellClassName;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "//\r\n"+
            "\r\n"+
            "// ########################\r\n"+
            "// ##### Tree Panels ######\r\n"+
            "\r\n"+
            "function TPCallback(input, pid, callback, nodeuid){\r\n"+
            "	var p = getPortletManager().getPortlet(pid);\r\n"+
            "	if (p == null)\r\n"+
            "		ret");
          out.print(
            "urn;\r\n"+
            "	p.callBack(callback,{val:input.value, uid:nodeuid});\r\n"+
            "}\r\n"+
            "\r\n"+
            "function TPcheck(input, pid, nodeUid) {\r\n"+
            "	var p = getPortletManager().getPortlet(pid);\r\n"+
            "	if (p == null)\r\n"+
            "		return;\r\n"+
            "\r\n"+
            "	p.callBack('treePanelsRerunDmOnChange',{val:input.checked, uid:nodeUid});\r\n"+
            "}\r\n"+
            "\r\n"+
            "function TPvarname(input, pid, nodeUid) {\r\n"+
            "	var p = getPortletManager().getPortlet(pid);\r\n"+
            "	if (p == null)\r\n"+
            "		return;\r\n"+
            "\r\n"+
            "	p.callBack('treePanelsTgtVarName',{val:input.value, uid:nodeUid});\r\n"+
            "}\r\n"+
            "\r\n"+
            "function TPformatter(input, pid, nodeUid) {\r\n"+
            "	var p = getPortletManager().getPortlet(pid);\r\n"+
            "	if (p == null)\r\n"+
            "		return;\r\n"+
            "\r\n"+
            "	p.callBack('treePanelsFormatter',{val:input.value, uid:nodeUid});\r\n"+
            "}\r\n"+
            "\r\n"+
            "//########################\r\n"+
            "//##### Set Defaults######\r\n"+
            "function onActionChanged(input, nodeUid, portletId) {\r\n"+
            "	var portlet = getPortletManager().getPortlet(portletId);\r\n"+
            "	if (portlet == null)\r\n"+
            "		return;\r\n"+
            "	if(input.value == 'NO_DEFAULT') {\r\n"+
            "		input.style.backgroundColor = \"#F8D7DA\";\r\n"+
            "		input.style.color = \"#721C24\";\r\n"+
            "		input.style.fontWeight = \"bold\";\r\n"+
            "	}\r\n"+
            "	portlet.callBack('onActionMenuChanged', {nodeUid: nodeUid, action: input.value});\r\n"+
            "}");

	}
	
}