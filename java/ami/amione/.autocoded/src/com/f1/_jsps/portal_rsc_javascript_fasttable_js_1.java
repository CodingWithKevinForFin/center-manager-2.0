package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_fasttable_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_fasttable_js_1() {
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
            "function SelectedRows(){\r\n"+
            "	this.ranges=new Array();\r\n"+
            "	this.rows=new Array();\r\n"+
            "//	this.rowsUid = new Set();\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectedRows.prototype.isEmpty=function(){\r\n"+
            "	return this.ranges.length==0 && this.rows.length==0;\r\n"+
            "}\r\n"+
            "SelectedRows.prototype.isSelected=function(rownum){\r\n"+
            "	if(this.rows[rownum]!=null)\r\n"+
            "		return true;\r\n"+
            "//	console.log(this.rows);\r\n"+
            "	for(var i=0;i<this.ranges.length;i++)\r\n"+
            "		if(this.ranges[i][0]<=rownum && rownum<=this.ranges[i][1])\r\n"+
            "			return true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "// returns the next selected.\r\n"+
            "SelectedRows.prototype.getNext=function() {\r\n"+
            "	if (this.isEmpty()) return -1;\r\n"+
            "	for (var i=0;i<this.rows.length;i++) {\r\n"+
            "		if (this.rows[i])\r\n"+
            "			return i;\r\n"+
            "	}\r\n"+
            "	// not empty, but the values are all empty.\r\n"+
            "	return -1;\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectedRows.prototype.add=function(start,end){\r\n"+
            "	if(end==null)\r\n"+
            "		end=start;\r\n"+
            "	else if(end<start){\r\n"+
            "	  var t=end;\r\n"+
            "	  end=start;\r\n"+
            "	  start=t;\r\n"+
            "	}\r\n"+
            "//	console.log(\"adding index from\", start, \"to\", end);\r\n"+
            "	this.remove(start,end);\r\n"+
            "	if(end-start<2){\r\n"+
            "		while(start<=end) {\r\n"+
            "			this.rows[start++]=true;\r\n"+
            "		}\r\n"+
            "	} else {\r\n"+
            "	    this.ranges.push([start,end]);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectedRows.prototype.remove=function(start,end){\r\n"+
            "	if(end==null)\r\n"+
            "		end=start;\r\n"+
            "	if(end-start < 100){\r\n"+
            "	  for(var i=start;i<=end;i++) {\r\n"+
            "		  delete this.rows[i];\r\n"+
            "	  }\r\n"+
            "	}else{\r\n"+
            "	  for(var i in this.rows){\r\n"+
            "	    if(i>end)\r\n"+
            "	      break;\r\n"+
            "	    if(i>=start) {\r\n"+
            "	    	delete this.rows[i];\r\n"+
            "	    }\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "	for(var i=0;i<this.ranges.length;i++){\r\n"+
            "		var e=this.ranges[i];\r\n"+
            "		var low=e[0];\r\n"+
            "		var high=e[1];\r\n"+
            "		if(high <start || low > end)\r\n"+
            "			continue;\r\n"+
            "		if(start<=low && end >=high){// outside\r\n"+
            "		  this.ranges.splice(i,1);\r\n"+
            "		  i--;\r\n"+
            "		}else if(start>low && end<high){// inside\r\n"+
            "			e[1]=start-1;\r\n"+
            "		    this.ranges[this.ranges.length]=[end+1,high];\r\n"+
            "		}else if(start>low){// begins with\r\n"+
            "			e[1]=start-1;\r\n"+
            "		}else{// ends with\r\n"+
            "			e[0]=end+1;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectedRows.prototype.toString=function(){\r\n"+
            "	r=\"\";\r\n"+
            "	for(var i in this.rows){\r\n"+
            "		if(r.length)\r\n"+
            "			r+=\",\";\r\n"+
            "		r+=i;\r\n"+
            "	}\r\n"+
            "	for(var i in this.ranges){\r\n"+
            "		if(r.length)\r\n"+
            "			r+=\",\";\r\n"+
            "		var b=this.ranges[i][0];\r\n"+
            "		var e=this.ranges[i][1];\r\n"+
            "		if(b==e)\r\n"+
            "		  r+=b;\r\n"+
            "		else\r\n"+
            "		  r+=b+'-'+e;\r\n"+
            "	}\r\n"+
            "	return r;\r\n"+
            "}\r\n"+
            "SelectedRows.prototype.parseString=function(string){\r\n"+
            "	this.clear();\r\n"+
            "	if(string==null || string==\"\")\r\n"+
            "		return;\r\n"+
            "	var parts=string.split(\",\");\r\n"+
            "	for(var i in parts){\r\n"+
            "		var startEnd=parts[i].split(\"-\");\r\n"+
            "		if(startEnd.length==1)\r\n"+
            "			this.rows[parseInt(startEnd[0])]=true;\r\n"+
            "		else {\r\n"+
            "			this.ranges.push([parseInt(startEnd[0]),parseInt(startEnd[1])]);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "SelectedRows.prototype.clear=function(){\r\n"+
            "	this.ranges=[];\r\n"+
            "	this.rows=[];\r\n"+
            "//	this.rowsUid.clear();\r\n"+
            "}\r\n"+
            "\r\n"+
            "function FastTableColumn(id,name,offset,location,width,headerClassName,cellClassName,hasSortButtons,columnHeaderStyle, owner,hasHover){\r\n"+
            "	var that=this;\r\n"+
            "	this.owner= owner; //table or tree\r\n"+
            "	var ownerClassName = owner.constructor.name;\r\n"+
            "//	console.log(ownerClassName);\r\n"+
            "	this.id=id;\r\n"+
            "	this.name=name;\r\n"+
            "	this.location=location;\r\n"+
            "	this.width=width;\r\n"+
            "	this.offset=offset;\r\n"+
            "	this.headerClassName=headerClassName;\r\n"+
            "	this.cellClassName=cellClassName;\r\n"+
            "	this.columnHeaderStyle=columnHeaderStyle;\r\n"+
            "    this.headerElement=nw(\"div\");\r\n"+
            "    this.grabElement=nw(\"div\");\r\n"+
            "    this.grabElement.className=\"header_grab\";\r\n"+
            "    this.headerElement.innerHTML=name;\r\n"+
            "    this.headerElement.className=\"header \"+headerClassName;\r\n"+
            "	applyStyle(this.headerElement,columnHeaderStyle);\r\n"+
            "    this.invisibleElement=nw(\"div\", \"header_invisible_layer\");\r\n"+
            "    this.headerElement.appendChild(this.invisibleElement);\r\n"+
            "    this.hasHover=hasHover;\r\n"+
            "    if(hasSortButtons){\r\n"+
            "      this.buttonsUpElement=nw(\"div\",\"header_buttons_up\");\r\n"+
            "      this.buttonsDnElement=nw(\"div\",\"header_buttons_dn\");\r\n"+
            "      this.invisibleElement.appendChild(this.buttonsUpElement);\r\n"+
            "      this.invisibleElement.appendChild(this.buttonsDnElement);\r\n"+
            "    }\r\n"+
            "    //TODOFILTER\r\n"+
            "	this.quickColumnFilterElement = nw(\"combo-box\",\"column_quick_filter editComboBox\");\r\n"+
            "	const shadow = this.quickColumnFilterElement.shadowRoot;\r\n"+
            "    if (ownerClassName == \"FastTree\") {\r\n"+
            "    	//MOBILE SUPPORT - support pressing enter to search column filter instead of going to next column filter.\r\n"+
            "    	const inputElement = shadow.querySelector('input');\r\n"+
            "    	if (inputElement) {\r\n"+
            "    		inputElement.setAttribute('enterkeyhint', 'go');\r\n"+
            "    	}\r\n"+
            "    	shadow.querySelector('style').textContent = `\r\n"+
            "			.combo-box{\r\n"+
            "	    		box-sizing:border-box;\r\n"+
            "	    		border:none;\r\n"+
            "	    		width:100%;\r\n"+
            "	    		height:100%;\r\n"+
            "	    	}\r\n"+
            "	    	.combo-box:focus{\r\n"+
            "	    		outline:none!important;\r\n"+
            "	    	}\r\n"+
            "	`;\r\n"+
            "    } else if (ownerClassName == \"FastTable\") {\r\n"+
            "    	//MOBILE SUPPORT - support pressing enter to search column filter instead of going to next column filter.\r\n"+
            "    	const inputElement = shadow.querySelector('input');\r\n"+
            "    	if (inputElement) {\r\n"+
            "    		inputElement.setAttribute('enterkeyhint', 'go');\r\n"+
            "    	}\r\n"+
            "    	shadow.querySelector('style').textContent = `\r\n"+
            "			.combo-box{\r\n"+
            "	    		box-sizing:border-box;\r\n"+
            "	    		border:none;\r\n"+
            "	    		width:100%;\r\n"+
            "	    		height:100%;\r\n"+
            "	    		position:absolute;\r\n"+
            "	    		left:0;\r\n"+
            "	    	}\r\n"+
            "	    	.combo-box:focus{\r\n"+
            "	    		outline:none!important;\r\n"+
            "	    	}\r\n"+
            "	`;\r\n"+
            "    }\r\n"+
            "	this.quickColumnFilterElement.setMinWidth(125);\r\n"+
            "    this.headerElement.appendChild(this.quickColumnFilterElement);\r\n"+
            "    //clear\r\n"+
            "	//this.clearQuickFilterElement = nw(\"div\", \"column_quick_filter_clear\");\r\n"+
            "    //this.headerElement.appendChild(this.clearQuickFilterElement);\r\n"+
            "	this.quickColumnFilterElement.isQuickColumnFilterElement=true;\r\n"+
            "	this.quickColumnFilterElement.style.display=\"none\";\r\n"+
            "	//MOBILE SUPPORT - enables the user to click on the filter.\r\n"+
            "	this.quickColumnFilterElement.ontouchstart=function(e){\r\n"+
            "		e.stopPropagation();\r\n"+
            "	};\r\n"+
            "	\r\n"+
            "	var that = this;\r\n"+
            "	this.quickColumnFilterElement.hideGlass= function(){\r\n"+
            "		if (ownerClassName == \"FastTree\")\r\n"+
            "		{\r\n"+
            "			kmm.setActivePortletId(that.owner.treePortlet.portletId,false);\r\n"+
            "		}\r\n"+
            "		else if (ownerClassName == \"FastTable\")\r\n"+
            "		{\r\n"+
            "			kmm.setActivePortletId(that.owner.portlet.portletId,false);\r\n"+
            "		}\r\n"+
            "	};\r\n"+
            "    this.quickColumnFilterElement.onfocus=function(e){\r\n"+
            "    	if (that.owner && that.owner.repaint!=null)\r\n"+
            "    		that.owner.repaint(); //TODO: makes the filter menu appear on the correct table (fix could be better)\r\n"+
            "    	if(!currentContextMenu)\r\n"+
            "    		that.quickColumnFilterGetOptions(e, that, that.owner);\r\n"+
            "    };\r\n"+
            "	var onkeydown = function(e){\r\n"+
            "		if(that.quickColumnFilterElement.handleKeydown && that.quickColumnFilterElement.handleKeydown(e)){\r\n"+
            "	\r\n"+
            "		}\r\n"+
            "		if(e.key==\"Tab\"){\r\n"+
            "			e.preventDefault();\r\n"+
            "		}\r\n"+
            "    };\r\n"+
            "	this.quickColumnFilterElement.setCustomKeydownHandler(onkeydown);\r\n"+
            "    var origSelect = this.quickColumnFilterElement.select;\r\n"+
            "    this.quickColumnFilterElement.select= function(i){\r\n"+
            "    	origSelect.call(that.quickColumnFilterElement, i);	\r\n"+
            "   		var col = that;\r\n"+
            "		col.filterText = col.quickColumnFilterElement.value;\r\n"+
            "		var value = col.filterText != null?col.filterText:\"\";\r\n"+
            "   		that.quickColumnFilterSetFilterValue(null, that, that.owner, value);\r\n"+
            "    };\r\n"+
            "\r\n"+
            "	this.quickColumnFilterElement.handleKeydown=function(e){\r\n"+
            "		var shiftKey = e.shiftKey;\r\n"+
            "		var ctrlKey = e.ctrlKey;\r\n"+
            "		var altKey = e.altKey;\r\n"+
            "		if(shiftKey == true || ctrlKey == true || altKey == true || e.key==\"Tab\")\r\n"+
            "			return false;\r\n"+
            "		\r\n"+
            "		if(currentContextMenu){\r\n"+
            "			currentContextMenu.handleKeydown(e);\r\n"+
            "			return true;\r\n"+
            "		}\r\n"+
            "		else \r\n"+
            "			return false;\r\n"+
            "		\r\n"+
            "	};\r\n"+
            "    // This is for getting suggestions when deleting values\r\n"+
            "    this.quickColumnFilterElement.onkeyup=function(e){\r\n"+
            "    	if(e.key.length == 1 || e.key == \"Backspace\" || e.key == \"Delete\")\r\n"+
            "			that.quickColumnFilterGetOptions(e, that, that.owner);\r\n"+
            "		else if(e.key==\"Enter\") {\r\n"+
            "    		var col = that;\r\n"+
            "    		col.filterText = col.quickColumnFilterElement.value;\r\n"+
            "			var value = col.filterText != null?col.filterText:\"\";\r\n"+
            "    		that.quickColumnFilterSetFilterValue(e, that, that.owner, value);\r\n"+
            "    	}\r\n"+
            "     	else if(e.key==\"Escape\"){\r\n"+
            "    		var col = that;\r\n"+
            "			col.filterText = \"\";\r\n"+
            "			var value = col.filterText;\r\n"+
            "    		that.quickColumnFilterSetFilterValue(e, that, that.owner, value);\r\n"+
            "    	}\r\n"+
            "		\r\n"+
            "    };\r\n"+
            "}\r\n"+
            "FastTableColumn.prototype.name;\r\n"+
            "FastTableColumn.prototype.offset;\r\n"+
            "FastTableColumn.prototype.width;\r\n"+
            "FastTableColumn.prototype.headerClassName;\r\n"+
            "FastTableColumn.prototype.cellClassName;\r\n"+
            "FastTableColumn.prototype.offset;\r\n"+
            "FastTableColumn.prototype.location;\r\n"+
            "FastTableColumn.prototype.headerElement;\r\n"+
            "FastTableColumn.prototype.grabElement;\r\n"+
            "FastTableColumn.prototype.quickColumnFilterElement;\r\n"+
            "FastTableColumn.prototype.filter;\r\n"+
            "FastTableColumn.prototype.filterText;\r\n"+
            "FastTableColumn.prototype.hasHover;\r\n"+
            "\r\n"+
            "FastTableColumn.prototype.setHIDS=function(hids){\r\n"+
            "	this.headerElement.id=hids;\r\n"+
            "}\r\n"+
            "FastTableColumn.prototype.getRightOffset=function(){\r\n"+
            "	return this.offset+this.width;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableColumn.prototype.updateQuickFilterValue=function(){\r\n"+
            "	if(this.filter==true){\r\n"+
            "		this.quickColumnFilterElement.setValue(this.filterText);\r\n"+
            "	}\r\n"+
            "	else\r\n"+
            "		this.quickColumnFilterElement.setValue(\"\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableColumn.prototype.quickColumnFilterSetFilterValue=function(e, col, that, value){\r\n"+
            " 	that.callback('columnFilter',{pos:col.location,val:value});\r\n"+
            " 	col.quickColumnFilterElement.blur();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableColumn.prototype.quickColumnFilterGetOptions=function(e, col, that){\r\n"+
            "//	console.log(\"quickColumnFilterGetOptions called\");\r\n"+
            "	var value = col.quickColumnFilterElement.value != null?col.quickColumnFilterElement.value:\"\";\r\n"+
            " 	that.callback('getColumnFilterOptions',{__CONFLATE:col.location,pos:col.location,val:value});\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.setAutocomplete=function(colPos,mapOfValuesOrNull){\r\n"+
            "	var col = this.columns[colPos];\r\n"+
            "	var oldVal = col.quickColumnFilterElement.value;\r\n"+
            "	col.quickColumnFilterElement.clearOptions2();\r\n"+
            "	for (let key in mapOfValuesOrNull) {\r\n"+
            "		col.quickColumnFilterElement.addOptionDisplayAction(key, mapOfValuesOrNull[key]);\r\n"+
            "	}\r\n"+
            "	//col.quickColumnFilterElement.setValue(oldVal);\r\n"+
            "	col.quickColumnFilterElement.autocomplete(col.filterText);\r\n"+
            "	\r\n"+
            "};\r\n"+
            "\r\n"+
            "//MOBILE SUPPORT - FOR DRAG SELECT\r\n"+
            "FastTable.prototype.onTouchDragStart=function(e){\r\n"+
            "	th");
          out.print(
            "is.currPoint = getMousePoint(e);\r\n"+
            "	\r\n"+
            "	this.longTouchTimer = setTimeout(() => {\r\n"+
            "		this.isSelecting = true;\r\n"+
            "		this.hideGlass();\r\n"+
            "		var t = this.currPoint;\r\n"+
            "		var t1 = document.elementFromPoint(t.x, t.y).rowLocation;\r\n"+
            "		if(t1 == null)\r\n"+
            "			return;\r\n"+
            "		this.selectRow(e,t1,false,false,false);\r\n"+
            "  }, 500); // Long touch threshold (500ms)\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "//MOBILE SUPPORT - FOR DRAG SELECT\r\n"+
            "FastTable.prototype.onTouchDragMove=function(e){\r\n"+
            "	//drag seelct.\r\n"+
            "	if(this.isSelecting === true){\r\n"+
            "		this.hideGlass();\r\n"+
            "		var t = getMousePoint(e);\r\n"+
            "		var t1 = document.elementFromPoint(t.x, t.y).rowLocation;\r\n"+
            "		if(t1 == null)\r\n"+
            "			return;\r\n"+
            "		this.dragSelect = true;\r\n"+
            "		this.selectRow(e,t1,false,false,true);\r\n"+
            "	}\r\n"+
            "	// drag scroll\r\n"+
            "	else{\r\n"+
            "		clearTimeout(this.longTouchTimer);\r\n"+
            "		this.longTouchTimer=null;\r\n"+
            "		var that = this;\r\n"+
            "		if(e.target != that.disabledGlassDiv){\r\n"+
            "			var newRef = that.disabledGlassDiv;\r\n"+
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
            "function FastTable(element){\r\n"+
            "  var that=this;\r\n"+
            "  this.cellElements=new Object2d();\r\n"+
            "	// store previous values to check if we need to flash\r\n"+
            "  this.valueElements=new Object2d();\r\n"+
            "  this.data=new Object2d(); // TODO: is this getting used?\r\n"+
            "  this.containerElement=element;\r\n"+
            "  this.tableElement=nw(\"div\");\r\n"+
            "  this.pinnedTable=nw(\"div\",\"__pinnedContainer\");\r\n"+
            "  this.scrollPaneElement=nw(\"div\");\r\n"+
            "  this.tableElement.className='table';\r\n"+
            "  this.tableElement.tabIndex=0;\r\n"+
            "  this.headerElement=nw(\"div\");\r\n"+
            "  this.pinnedBorderElement=nw(\"div\");\r\n"+
            "  this.selectedRowsElement=nw(\"div\");\r\n"+
            "  this.pinnedBorderWidth = 5;\r\n"+
            "  this.pinnedBorderElement.style.display=\"none\";\r\n"+
            "  this.pinnedBorderElement.style.zIndex = 3;\r\n"+
            "  this.pinnedBorderElement.style.pointerEvents = \"none\";\r\n"+
            "  this.pinnedBorderElement.style.width = toPx(this.pinnedBorderWidth);\r\n"+
            "  this.pinnedBorderElement.style.borderWidth = \"0px 2px 0px 2px\";\r\n"+
            "  this.pinnedBorderElement.style.borderStyle = \"inset\";\r\n"+
            "  this.pinnedBorderElement.style.borderColor = \"#999999\";\r\n"+
            "  this.pinnedBorderElement.style.background = \"#b3b3b3\";\r\n"+
            "  this.columns=new Array();\r\n"+
            "  this.scrollSize=15;\r\n"+
            "  this.containerElement.appendChild(this.scrollPaneElement);\r\n"+
            "  this.containerElement.appendChild(this.headerElement);\r\n"+
            "  this.containerElement.appendChild(this.pinnedBorderElement);	\r\n"+
            "  this.tableElement.appendChild(this.selectedRowsElement);\r\n"+
            "  this.tooltipPadL=6;\r\n"+
            "  this.tooltipPadT=4;\r\n"+
            "  \r\n"+
            "  this.selectedRowsElement.style.pointerEvents=\"none\";\r\n"+
            "  this.selectedRowsElement.style.zIndex=3;\r\n"+
            "  this.scrollPane=new ScrollPane(this.scrollPaneElement,this.scrollSize,this.tableElement);\r\n"+
            "  this.scrollPane.onScroll=function(){that.onScroll()};\r\n"+
            "  this.scrollPaneElement.appendChild(this.pinnedTable);\r\n"+
            "  // below has no effect\r\n"+
            "  this.pinnedTable.onMouseWheel=function(e,delta){\r\n"+
            "	  that.scrollPane.onMouseWheel(e,delta);\r\n"+
            "  };\r\n"+
            "  \r\n"+
            "  // options\r\n"+
            "  this.selectedClassname='cell_selected_default';\r\n"+
            "  this.activeClassname='cell_active_default';\r\n"+
            "  this.useGreybars=true;\r\n"+
            "  this.rowHeight=18;\r\n"+
            "  this.quickColumnFilterHeight = this.rowHeight+10;\r\n"+
            "  this.headerHeight = this.rowHeight;\r\n"+
            "  this.headerFontSize = 13;\r\n"+
            "  this.greyBarColor=null;\r\n"+
            "  this.bgStyle=null;\r\n"+
            "  this.defaultFontColor=null;\r\n"+
            "  this.searchBarColor=null;\r\n"+
            "  this.filteredColumnBgColor=null;\r\n"+
            "  this.filteredColumnFontColor=null;\r\n"+
            "  \r\n"+
            "  this.totalWidth=0;\r\n"+
            "  this.totalPinnedWidth=0;\r\n"+
            "  this.totalHeight=this.headerHeight;\r\n"+
            "  this.selectedRows=new SelectedRows();\r\n"+
            "  this.headerElement.className=\"header\";\r\n"+
            "  this.currentRow=0;\r\n"+
            "  this.onCellMouseOverFunc=function(e){return that.onCellMouseOver(e);};\r\n"+
            "  this.onCellMouseMoveFunc=function(e){return that.onCellMouseMove(e);};\r\n"+
            "  this.onCellMouseOutFunc=function(e){return that.onCellMouseOut(e);};\r\n"+
            "  this.onCellMouseDownFunc=function(e){e.stopPropagation();return that.onCellMouseDown(e);};\r\n"+
            "  this.onCellMouseUpFunc=function(e){that.hideGlass();return that.onCellMouseUp(e);};\r\n"+
            "  \r\n"+
            "  this.tableElement.onmouseleave=function() { \r\n"+
            "	  this.expectTooltip=false;\r\n"+
            "	  that.onMouseLeave();\r\n"+
            "  };\r\n"+
            "  this.tableElement.onmousemove=function(e) { that.onMouseMove(e);};\r\n"+
            "  this.tableElement.onmousedown=this.onCellMouseDownFunc;\r\n"+
            "  this.tableElement.onmousedown=function(e){e.stopPropagation();return that.onWhiteSpaceMouseDown(e);};\r\n"+
            "//below has no effect\r\n"+
            "  this.scrollPaneElement.onmousedown=function(e){\r\n"+
            "	  e.stopPropagation();\r\n"+
            "	  return that.onWhiteSpaceMouseDown(e);\r\n"+
            "  };\r\n"+
            "  this.headerElement.onclick=function(e){if(e.button==0)that.onHeaderClicked(e,null);};\r\n"+
            "  this.pinning=0;\r\n"+
            "  this.userSelectSeqnum=0;\r\n"+
            "  this.userScrollSeqnum=0;\r\n"+
            "  this.widthChanged = false;\r\n"+
            "  this.heightChanged = false;\r\n"+
            "  this.quickColumnFilterHidden = true;//quickfilter is displayed\r\n"+
            "  \r\n"+
            "  //MOBILE SUPPORT - FOR DRAG SCROLL\r\n"+
            "  this.longTouchTimer;\r\n"+
            "  this.isSelecting = false;\r\n"+
            "  this.isDragging = false;\r\n"+
            "  this.currPoint = new Point(0,0);\r\n"+
            "  this.tableElement.ontouchstart=function(e) { that.onTouchDragStart(e); that.createGlass();};\r\n"+
            "  this.tableElement.ontouchmove=function(e) { that.onTouchDragMove(e); that.onScroll();};\r\n"+
            "  this.tableElement.ontouchend= function(e){ that.isSelecting=false; clearTimeout(that.longTouchTimer); that.longTouchTimer=null; that.hideGlass();};\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "// elements\r\n"+
            "FastTable.prototype.tableElement;\r\n"+
            "FastTable.prototype.columns;\r\n"+
            "FastTable.prototype.hiddenColumns;\r\n"+
            "FastTable.prototype.headerGrabElements;\r\n"+
            "FastTable.prototype.cellElements;\r\n"+
            "FastTable.prototype.contextMenuCurrentColumn;\r\n"+
            "FastTable.prototype.useGreybars;\r\n"+
            "FastTable.prototype.greyBarColor;\r\n"+
            "FastTable.prototype.defaultFontColor;\r\n"+
            "FastTable.prototype.searchBarColor;\r\n"+
            "FastTable.prototype.menuBarColor;\r\n"+
            "FastTable.prototype.filteredColumnBgColor;\r\n"+
            "FastTable.prototype.filteredColumnFontColor;\r\n"+
            "FastTable.prototype.quickColumnFilterHidden;\r\n"+
            "FastTable.prototype.quickColumnBackgroundColor;\r\n"+
            "FastTable.prototype.quickColumnFontColor;\r\n"+
            "FastTable.prototype.quickColumnFontSz;\r\n"+
            "FastTable.prototype.quickColumnBorderColor;\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "// Header\r\n"+
            "FastTable.prototype.headerHeight=20;\r\n"+
            "\r\n"+
            "\r\n"+
            "// cell sizes\r\n"+
            "FastTable.prototype.rowHeight=18;\r\n"+
            "FastTable.prototype.quickColumnFilterHeight;\r\n"+
            "FastTable.prototype.cornerSize=15;\r\n"+
            "\r\n"+
            "// table size\r\n"+
            "FastTable.prototype.height=200;\r\n"+
            "FastTable.prototype.width=1000;\r\n"+
            "\r\n"+
            "// upper left corner\r\n"+
            "FastTable.prototype.upperRow=0;\r\n"+
            "FastTable.prototype.lowerRow=0;\r\n"+
            "FastTable.prototype.visibleRowsCount=0;\r\n"+
            "\r\n"+
            "FastTable.prototype.data;\r\n"+
            "FastTable.prototype.totalWidth;\r\n"+
            "FastTable.prototype.selectedRows;\r\n"+
            "FastTable.prototype.totalRows=0;\r\n"+
            "\r\n"+
            "FastTable.prototype.onMouseMove=function(point){\r\n"+
            "  var cell=getMouseTarget(point);\r\n"+
            "  if (cell != null && this.onHover) {\r\n"+
            "	var row=cell.rowLocation;\r\n"+
            "    while(row==null){\r\n"+
            "	  cell=cell.parentNode;\r\n"+
            "	  if(cell==null) {\r\n"+
            "        this.clearHover();\r\n"+
            "		return;\r\n"+
            "      }\r\n"+
            "	  row=cell.rowLocation;\r\n"+
            "	}  \r\n"+
            "	this.currentMousePoint=point;\r\n"+
            "//	this.onHover(cell.colLocation, cell.rowLocation);\r\n"+
            "  } else {\r\n"+
            "	this.clearHover();\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.onMouseLeave=function(){\r\n"+
            "  this.clearHover();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.setHover=function(x,y,value,xAlign,yAlign){\r\n"+
            "	if (this.expectTooltip === false)\r\n"+
            "		return;\r\n"+
            "  var cell = this.cellElements.get(x,y);\r\n"+
            "  const rootBody = getRootNodeBody(this.tableElement);\r\n"+
            "  if(this.tooltipDiv!=null || cell == undefined)\r\n"+
            "	  this.clearHover();\r\n"+
            "  this.tooltipDiv=nw(\"div\",\"ami_table_tooltip\");\r\n"+
            "  var div=this.tooltipDiv;\r\n"+
            "  this.hoverX=this.currentMousePoint.x;\r\n"+
            "  this.hoverY=this.currentMousePoint.y;\r\n"+
            "  div.innerHTML=value;\r\n"+
            "  if(div.firstChild!=null && div.firstChild.tagName=='DIV'){\r\n"+
            "    this.tooltipDiv=div.firstChild;\r\n"+
            "    div=this.tooltipDiv;\r\n"+
            "  }\r\n"+
            "  rootBody.appendChild(div);\r\n"+
            "  var rect=new Rect().readFromElement(div);\r\n"+
            "  // add padding to size\r\n"+
            "  var h=rect.height+this.tooltipPadL;\r\n"+
            "  var w=rect.width+6+this.tooltipPadT;\r\n"+
            "  div.style.width=toPx(w);\r\n"+
            "  div.style.height=toPx(h);\r\n"+
            "	// TODO i don't think we need to use a switch here.... we always pass in the same value\r\n"+
            "  switch(xAlign){\r\n"+
            "    case ALIGN_LEFT: div.style.left=toPx(this.hoverX+x); break;\r\n"+
            "    case ALIGN_RIGHT: div.style.left=toPx(this.hoverX-w-this.tooltipPadL); break;\r\n"+
            "    default: div.style.left=toPx(this.hoverX-w/2); break;\r\n"+
            "  }\r\n"+
            "  switch(yAlign){\r\n"+
            "    case ALIGN_TOP: div.style.top=toPx(this.hoverY); break;\r\n"+
            "    case ALIGN_BOTTOM: div.style.top=toPx(this.hoverY-h-this.tooltipPadT); break;\r\n"+
            "    default: div.style.top=toPx(this.hoverY-h/2); break;\r\n"+
            "  }\r\n"+
            "  ensureInDiv(div,rootBody);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.clearHover=function(){\r\n"+
            "  if(this.tooltipDiv!=null){\r\n"+
            "    try {\r\n"+
            "		getDocument(this.tableElement).body.removeChild(this.tooltipDiv);\r\n"+
            "    } catch (e) {\r\n"+
            "    	console.log(\"unable to clear table hover: \" + e);\r\n"+
            "    }\r\n"+
            "    this.tooltipDiv=null;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.onOutsideMouseDragging=function(e){\r\n"+
            "  var point=getMousePoint(e);\r\n"+
            "  var r=new Rect();\r\n"+
            "  r.readFromElement(this.containerElement);\r\n"+
            "  if(r.getBottom()<point.getY())\r\n"+
            "	  this.outsideMouseDraggingDelta=(point.getY() - r.getBottom())/50 ;\r\n"+
            "  else if(r.getTop()>point.getY())\r\n"+
            "	  this.outsideMouseDraggingDelta=(point.getY() - r.getTop())/50 ;\r\n"+
            "  else{\r\n"+
            "	  this.outsideMouseDraggingDelta=0;\r\n"+
            "	  if(this.dragSelect){\r\n"+
            "	    var cell=getMouseTarget(e);\r\n"+
            "	    var row=cell.rowLocation;\r\n"+
            "	    while(row==null){\r\n"+
            "	      cell=cell.parentNode;\r\n"+
            "	      if(cell==null)\r\n"+
            "	    	  break;\r\n"+
            "	      row=cell.rowLocation;\r\n"+
            "	    }\r\n"+
            "	    if(row!=null){\r\n"+
            "	  	  this.selectRow(e,row,false,false,true);\r\n"+
            "	   }\r\n"+
            "	  }  \r\n"+
            "  }\r\n"+
            "	  \r\n"+
            "}\r\n"+
            "FastTable.prototype.onOutsideDragging=function(e){\r\n"+
            "	if(this.outsideMouseDraggingDelta){\r\n"+
            "	   if(this.outsideMouseDraggingDelta>0){\r\n"+
            "	  	  this.selectRow(e,this.getUpperRowVisible(),false,false,true);\r\n"+
            "	   }else{\r\n"+
            "	  	  this.selectRow(e,this.getLowerRowVisible(),false,false,true);\r\n"+
            "	   }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.stopOutsideDragging=function(){\r\n"+
            "  if(this.dragOutsideTimer!=null){\r\n"+
            "    window.clearInterval(this");
          out.print(
            ".dragOutsideTimer);\r\n"+
            "    this.dragOutsideTimer=null;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTable.prototype.addColumn=function(id,name,width,headerClassName,cellClassName,headerStyle,location,jsFormatterType,filter,filterText,isFixed,hasHover){\r\n"+
            "   if(location == null)\r\n"+
            "     location=this.columns.length;\r\n"+
            "   var offset=location==0 ? 0: this.columns[location-1].getRightOffset();\r\n"+
            "   var that=this;\r\n"+
            "   var column=new FastTableColumn(id,name,offset,location,width,headerClassName,cellClassName!=null ? cellClassName : \"\",true,headerStyle, that,hasHover);\r\n"+
            "//   console.log(\"id: \",id,\"name: \", name, \"offset: \", offset, \"location: \", location, \"width: \", width, \"headerClassName: \", headerClassName, \"cellClassName: \", cellClassName); \r\n"+
            "//   console.log(column);\r\n"+
            "   column.headerElement.location=location;\r\n"+
            "   makePushDraggable(this.headerElement, column.headerElement, column.invisibleElement, true, false, null, \r\n"+
            "   		function(a,b,c){that.onDragColumnEndCallback(a,b,c);}, \r\n"+
            "   		function(a,b,c,d,e,f){return that.onDragMousePosition(a,b,c,d,e,f);}, \r\n"+
            "   false);\r\n"+
            "\r\n"+
            "\r\n"+
            "   column.invisibleElement.location=location;// TODO this is wrong if not adding\r\n"+
            "											// to the end (following columns\r\n"+
            "											// need to be updated!)\r\n"+
            "   column.invisibleElement.onmouseup=function(e){if(e.button == 0) that.onHeaderClicked(e,column); else that.onFilter(column.location);};\r\n"+
            "   column.buttonsUpElement.onmousedown=function(e){that.onHeaderButtonClicked(e,'__sort_3',column.location);};\r\n"+
            "   column.buttonsDnElement.onmousedown=function(e){that.onHeaderButtonClicked(e,'__sort_2',column.location);};\r\n"+
            "   column.headerElement.style.borderColor=this.cellBorderColor;\r\n"+
            "   column.grabElement.style.opacity = \"0\";   \r\n"+
            "   column.grabElement.location=location;\r\n"+
            "   column.grabElement.onclick=function(e){e.stopPropagation();};\r\n"+
            "   column.grabElement.ondragging=function(e,x,y){that.onHeaderDragging(e,x,y,column);};\r\n"+
            "   column.grabElement.ondraggingEnd=function(e,x,y){that.hideGrabElement(that,e.location);};\r\n"+
            "   column.grabElement.clipDragging=function(e,rect){that.clipHeaderDragging(e,rect);};\r\n"+
            "   column.grabElement.ondblclick=function(e){that.dblClickGrabElement(e,that, location);};\r\n"+
            "   column.grabElement.style.borderColor=this.cellBorderColor;\r\n"+
            "   column.jsFormatterType=jsFormatterType;\r\n"+
            "   if(this.hideHeaderDivider==\"true\")\r\n"+
            "	   column.headerElement.style.boxShadow=\"none\";\r\n"+
            "//     column.headerElement.style.backgroundImage=\"none\";\r\n"+
            "   column.grabElement.onmouseenter=function(e){if(e.buttons==0)that.showGrabElement(that, location);};\r\n"+
            "   column.grabElement.onmouseleave=function(e){if(e.buttons==0)that.hideGrabElement(that, location);};\r\n"+
            "   makeDraggable(column.grabElement,column.grabElement,false,true);\r\n"+
            "//   makeDraggable2(null, column.grabElement,column.grabElement,true,false);\r\n"+
            "   this.headerElement.appendChild(column.headerElement);\r\n"+
            "   this.headerElement.appendChild(column.grabElement);\r\n"+
            "   this.columns.splice(location,0,column);\r\n"+
            "//   column.width=width;\r\n"+
            "   //TODOFILTER\r\n"+
            "   column.filter = filter;\r\n"+
            "   column.filterText = filterText;\r\n"+
            "   column.updateQuickFilterValue();\r\n"+
            "   if (isFixed && !amiEditDesktopArgs.edit) {\r\n"+
            "	   column.grabElement.style.display='none'; // hide grabber\r\n"+
            "   }\r\n"+
            "   applyStyle(column.headerElement,column.columnHeaderStyle);\r\n"+
            "   return column;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.fixColumns=function(m){\r\n"+
            "	for (var i=0; i < m.length; i++) {\r\n"+
            "		var ref = m[i];\r\n"+
            "		var column = this.findColumnById(ref.id);\r\n"+
            "		if (!column)\r\n"+
            "			continue; // mismatch between backend and frontend... should never happen\r\n"+
            "		if (amiEditDesktopArgs.edit) {\r\n"+
            "			// can interact in dev mode, similar to locked divider\r\n"+
            "		   column.grabElement.style.display='revert';\r\n"+
            "		   continue;\r\n"+
            "		}\r\n"+
            "	   column.grabElement.style.display=ref.fix ? 'none':'revert';\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.findColumnById=function(id){\r\n"+
            "	if (this.columns.length == 0)\r\n"+
            "		return;\r\n"+
            "	for (var i =0; i < this.columns.length; i++) {\r\n"+
            "		if (this.columns[i].id === id)\r\n"+
            "			return this.columns[i];\r\n"+
            "	}\r\n"+
            "	return null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.onDragColumnEndCallback=function(oldIndex,newIndex,success){\r\n"+
            "    //err(oldIndex+\"==>\"+newIndex);\r\n"+
            "	if(oldIndex!=newIndex)\r\n"+
            "		this.callback('moveColumn',{oldPos:oldIndex,newPos:newIndex  });\r\n"+
            "}\r\n"+
            "FastTable.prototype.onDragMousePosition=function(mouseX, containerOffset, mouseOffset, elementWidth, moveX, moveY){\r\n"+
            "	if(moveY)\r\n"+
            "		return;\r\n"+
            "	\r\n"+
            "	var leftEdge = this.scrollPane.getClipLeft();\r\n"+
            "	var rightEdge = leftEdge + this.scrollPane.getClipWidth();\r\n"+
            "	var draggedLeftEdge = (mouseX - containerOffset - mouseOffset);\r\n"+
            "	var draggedRightEdge = (mouseX - containerOffset - mouseOffset + elementWidth);\r\n"+
            "	/*\r\n"+
            "	arguments[6] = rightEdge;\r\n"+
            "	arguments[7] = draggedRightEdge;\r\n"+
            "	arguments[8] = draggedRightEdge > rightEdge;\r\n"+
            "	//arguments[8] = leftEdge;\r\n"+
            "	//arguments[9] = draggedLeftEdge;\r\n"+
            "	*/\r\n"+
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
            "FastTable.prototype.dblClickGrabElement=function(e, that, col){\r\n"+
            "	that.callback('headerMenuitem',{action:\"__autosize\",col:col});\r\n"+
            "}\r\n"+
            "FastTable.prototype.showGrabElement=function(that, location){\r\n"+
            "	var col = that.columns[location].grabElement;\r\n"+
            "	col.style.opacity=1;\r\n"+
            "//	col.style.zIndex=location<that.pinning ? '2' : '1';\r\n"+
            "}\r\n"+
            "FastTable.prototype.hideGrabElement=function(that, location){\r\n"+
            "	var col = that.columns[location].grabElement;\r\n"+
            "	col.style.opacity=0;\r\n"+
            "	if (this.columns[location].jsFormatterType && this.columns[location].jsFormatterType === \"spark_line\")\r\n"+
            "		that.callback('headerMenuitem',{action:\"__draggingEnd\",col:location});\r\n"+
            "//	col.style.zIndex=location<that.pinning ? '1' : '0';\r\n"+
            "}\r\n"+
            "FastTable.prototype.onHeaderButtonClicked=function(e,id,col){\r\n"+
            "   this.onUserHeaderMenuItem(e,id,col);\r\n"+
            "}\r\n"+
            "FastTable.prototype.onHeaderClicked=function(e,column){\r\n"+
            "//	e.stopPropagation();\r\n"+
            "   var target=getMouseTarget(e);\r\n"+
            "   if(this.menu!=null)\r\n"+
            "	   this.menu.hide();\r\n"+
            "   if(target==this.headerElement){\r\n"+
            "     this.contextMenuCurrentColumn=-2;\r\n"+
            "     var point = new Rect().readFromElement(target).getLowerLeft();\r\n"+
            "     point.x=getMousePoint(e).x-4;\r\n"+
            "     this.contextMenuPoint=point;\r\n"+
            "   }else{\r\n"+
            "     this.contextMenuCurrentColumn=target.location;\r\n"+
            "     var point = new Rect().readFromElement(target).getLowerLeft();\r\n"+
            "     var mousePosX = this.portlet.owningWindow.MOUSE_POSITION_X;\r\n"+
            "     var colIndex = this.getColumnAtPoint(mousePosX);\r\n"+
            "     if (this.isColumnOverflownLeft(colIndex))\r\n"+
            "    	 point.x = mousePosX;\r\n"+
            "     this.contextMenuPoint=point;\r\n"+
            "   }\r\n"+
            "   if(this.onUserHeaderMenu!=null && this.contextMenuCurrentColumn!=null) {\r\n"+
            "   	  this.curseqnum=nextSendSeqnum;\r\n"+
            "      this.onUserHeaderMenu(e,this.contextMenuCurrentColumn);\r\n"+
            "   }\r\n"+
            "}\r\n"+
            "FastTable.prototype.isColumnOverflownLeft=function(colIndex) {\r\n"+
            "	var columnOffset = this.columns[colIndex].offset;\r\n"+
            "	var clipLeft = this.scrollPane.getClipLeft();\r\n"+
            "	\r\n"+
            "	return clipLeft > columnOffset; \r\n"+
            "}\r\n"+
            "FastTable.prototype.onCellMouseUp=function(e){\r\n"+
            "	this.stopOutsideDragging();\r\n"+
            "	this.dragSelect=false;\r\n"+
            "    var doc=getDocument(this.containerElement).onmouseup=null;\r\n"+
            "}\r\n"+
            "FastTable.prototype.onWhiteSpaceMouseDown=function(e){\r\n"+
            "	var that=this;\r\n"+
            "	if(e.target != this.tableElement)\r\n"+
            "		return;\r\n"+
            "    if(e.shiftKey  || e.ctrlKey)\r\n"+
            "      return;\r\n"+
            "	var button=getMouseButton(e);\r\n"+
            "	//mobile scroll stuff\r\n"+
            "	var x = getMousePoint(e).x;\r\n"+
            "	var y = getMousePoint(e).y;\r\n"+
            "	if(button==2){\r\n"+
            "	  this.clearSelected();\r\n"+
            "	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "      this.contextMenuCurrentColumn=-1;\r\n"+
            "	  if(this.onUserContextMenu!=null)\r\n"+
            "		  this.onUserContextMenu(e);\r\n"+
            "	}else{\r\n"+
            "	  this.clearSelected();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.scroll=function(e, r){\r\n"+
            "	var loc = this.currentRow;\r\n"+
            "	if(loc!=null){\r\n"+
            "		var selRow = loc+r;\r\n"+
            "		if (selRow > this.data.getHeight()-1 || selRow < 0) {\r\n"+
            "			return;\r\n"+
            "		}\r\n"+
            "//		selRow=between(selRow,0,this.data.getHeight()-1);\r\n"+
            "		this.ensureRowVisible(selRow);\r\n"+
            "		this.selectRow(e,selRow,e.shiftKey, e.ctrlKey,false);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.selectAll=function(){\r\n"+
            "	if(this.data.height > 0){\r\n"+
            "		this.selectedRows.add(0,this.data.height-1);\r\n"+
            "		this.flagSelectionChanged = true;\r\n"+
            "		this.repaint();\r\n"+
            "		this.fireUserSelected(null,this.currentRow,this.selectedRows.toString());\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.clearSelected=function(){\r\n"+
            "	this.selectedRows.clear();\r\n"+
            "	this.currentRow = -1;\r\n"+
            "	this.flagSelectionChanged = true;\r\n"+
            "	this.repaint();\r\n"+
            "	this.fireUserSelected(null, null, this.selectedRows.toString());\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.openCopyMenu=function(e){\r\n"+
            "	if(isMouseInside({pageX:this.portlet.owningWindow.MOUSE_POSITION_X, pageY:this.portlet.owningWindow.MOUSE_POSITION_Y}, this.scrollPaneElement, 0)){\r\n"+
            "		var col = this.getColumnAtPoint(this.portlet.owningWindow.MOUSE_POSITION_X);\r\n"+
            "		this.callback('copyRows',{e:e,col:col})\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.jumpToColWithText=function(e){\r\n"+
            "	if(e.target.isQuickColumnFilterElement==true)\r\n"+
            "		return;\r\n"+
            "	var ch = e.key; \r\n"+
            "	if(ch != \"Enter\"){\r\n"+
            "		var now = Date.now();\r\n"+
            "		if(!this.lastUserNavigateCharTime || (now - this.lastUserNavigateCharTime > 2000)){\r\n"+
            "			this.lastText = \"\";\r\n"+
            "		}\r\n"+
            "		if(ch){\r\n"+
            "			this.lastUserNavigateCharTime = now;\r\n"+
            "			this.lastText += ch;\r\n"+
            "			var loc = this.currentRow;\r\n"+
            "			this.onUserNavigate(e, loc, \"text\", this.lastText);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.nextColWithText=function(e){\r\n"+
            "	this.lastUserNavigateCharTime = 0;\r\n"+
            "	var loc = this.currentRow;\r\n"+
            "	this.onUserNavigate(e, loc, \"retext\", this.lastText);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.clearLastText=function(){\r\n"+
            "	this.lastText = \"\";\r\n"+
            "	this.lastUserNavigateCharTime = null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.jumpToNextUniqueCol=function(e){\r\n"+
            "	if(!this.lastText || this.lastText.trim() == \"\"){\r\n"+
            "		var loc = this.currentRow;\r\n"+
            "		this.onUserNavigate(e, loc, e.shiftKey ? \"up\": \"dn\");\r\n"+
            "		this.clearLastText();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.getColumnAtPoint = function(x){\r\n"+
            "    for(var i");
          out.print(
            "=0;i<this.columns.length;i++){\r\n"+
            "        var col=new Rect().readFromElementRelatedToWindow(this.columns[i].headerElement);\r\n"+
            "        if(col.getLeft()<=x && col.getRight()>=x)\r\n"+
            "      	  return i;\r\n"+
            "      }\r\n"+
            "      return -1;\r\n"+
            "}\r\n"+
            "FastTable.prototype.ensureRowVisible=function(row){\r\n"+
            "  row=between(row,0,this.data.getHeight()-1);\r\n"+
            "  var rowTop = this.rowHeight * row;\r\n"+
            "  var rowBot = this.rowHeight * (row+1);\r\n"+
            "  var pageTop = this.scrollPane.getClipTop();\r\n"+
            "  var pageHeight = this.scrollPane.getClipHeight();\r\n"+
            "	if (this.scrollPane.hscrollVisible) {\r\n"+
            "		// page is smaller if we have horizontal scrollbar\r\n"+
            "		pageHeight -= this.scrollSize;\r\n"+
            "	}\r\n"+
            "  \r\n"+
            "  var pos = null;\r\n"+
            "  // Ensure the bottom of the row is above the bottom of the page\r\n"+
            "  if(pageTop + pageHeight < rowBot){ \r\n"+
            "	  pos = rowBot - pageHeight;\r\n"+
            "  }\r\n"+
            "  // Ensure the top of the row is below the top of the page\r\n"+
            "  if(pageTop > rowTop) {\r\n"+
            "	  pos = rowTop;\r\n"+
            "  }\r\n"+
            "  if(pos != null)\r\n"+
            "	  this.scrollPane.setClipTop(pos);\r\n"+
            "}\r\n"+
            "// Align 0 - top, 1 - bottom\r\n"+
            "FastTable.prototype.ensureRowVisibleWithAlign=function(row, align){\r\n"+
            "	row=between(row,0,this.data.getHeight()-1);\r\n"+
            "	if (align == 0) {\r\n"+
            "		var rowTop = this.rowHeight * row;\r\n"+
            "		this.scrollPane.setClipTop(rowTop);\r\n"+
            "	}\r\n"+
            "	else if(align == 1){\r\n"+
            "		var pageHeight = this.scrollPane.getClipHeight();\r\n"+
            "		var rowBot = this.rowHeight * (row+1);\r\n"+
            "		this.scrollPane.setClipTop(rowBot - pageHeight);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.onCellMouseOver=function(e){\r\n"+
            "	var cell=getMouseTarget(e);\r\n"+
            "	this.onCellMouseMove(e);\r\n"+
            "	// we are in a new cell, let's request a new hover\r\n"+
            "	// TODO tooltip is a column setting, it feels like we should call this onColumnOut/In...\r\n"+
            "	this.currentMousePoint=e;\r\n"+
            "	if(this.columns[cell.colLocation].hasHover) {\r\n"+
            "	  this.onHover(cell.colLocation, cell.rowLocation);\r\n"+
            "	  this.expectTooltip=true;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.onCellMouseOut=function(e){\r\n"+
            "	var cell=getMouseTarget(e);\r\n"+
            "	var row=cell.rowLocation;\r\n"+
            "	this.expectTooltip=false;\r\n"+
            "	// out of a cell, let's delete the old hover\r\n"+
            "	// TODO tooltip is a column setting, it feels like we should clear onColumnOut/In...\r\n"+
            "	this.clearHover();\r\n"+
            "	while(row==null){\r\n"+
            "		cell=cell.parentNode;\r\n"+
            "		if(cell==null) {\r\n"+
            "			return;\r\n"+
            "		}\r\n"+
            "		row=cell.rowLocation;\r\n"+
            "	}\r\n"+
            "	var col=cell.colLocation;\r\n"+
            "	\r\n"+
            "	if(this.columns[col] !=null){\r\n"+
            "	if(this.columns[col].clickable){\r\n"+
            "    	  cell.style.cursor=null;\r\n"+
            "          var top=this.getUpperRowVisible();\r\n"+
            "	      var bot=this.getLowerRowVisible();\r\n"+
            "	      while(top<=bot){\r\n"+
            "              var cell=this.cellElements.get(col,top);\r\n"+
            "              if(cell!=null){\r\n"+
            "    	        cell.style.textDecoration=null;\r\n"+
            "    	        cell.style.cursor=null;\r\n"+
            "              }\r\n"+
            "	    	top++;\r\n"+
            "	      }\r\n"+
            "	}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.onCellMouseMove=function(e){\r\n"+
            "    var shiftKey=e.shiftKey;\r\n"+
            "    var ctrlKey=e.ctrlKey;\r\n"+
            "    \r\n"+
            "	var cell=getMouseTarget(e);\r\n"+
            "	var row=cell.rowLocation;\r\n"+
            "	if (this.tooltipDiv != null) {\r\n"+
            "		// update tooltip location\r\n"+
            "	  const rootBody = getRootNodeBody(this.tableElement);\r\n"+
            "	  var div=this.tooltipDiv;\r\n"+
            "	  var rect=new Rect().readFromElement(div);\r\n"+
            "	  var h=rect.height;\r\n"+
            "	  var w=rect.width;\r\n"+
            "	  var hoverX = e.x;\r\n"+
            "	  var hoverY = e.y;\r\n"+
            "	  // using ALIGN_RIGHT/ALIGN_BOTTOM (constant)\r\n"+
            "		div.style.left=toPx(hoverX-w-this.tooltipPadL);\r\n"+
            "		div.style.top=toPx(hoverY-h-this.tooltipPadT);\r\n"+
            "	  ensureInDiv(div,rootBody);\r\n"+
            "	}\r\n"+
            "	while(row==null){\r\n"+
            "		cell=cell.parentNode;\r\n"+
            "		if(cell==null)\r\n"+
            "			return;\r\n"+
            "		row=cell.rowLocation;\r\n"+
            "	}\r\n"+
            "	var col=cell.colLocation;\r\n"+
            "	\r\n"+
            "	if(this.columns[col] !=null){\r\n"+
            "	if(this.columns[col].clickable && !shiftKey && !ctrlKey && e.buttons==0 && this.selectedRows.isSelected(row)){\r\n"+
            "      var top=this.getUpperRowVisible();\r\n"+
            "	  var bot=this.getLowerRowVisible();\r\n"+
            "	  while(top<=bot){\r\n"+
            "        var cell=this.cellElements.get(col,top);\r\n"+
            "        if(cell!=null){\r\n"+
            "	      if(this.selectedRows.isSelected(top)){\r\n"+
            "    	    cell.style.textDecoration='underline';\r\n"+
            "    	    cell.style.cursor='pointer';\r\n"+
            "	      }else{\r\n"+
            "    	    cell.style.textDecoration=null;\r\n"+
            "    	    cell.style.cursor=null;\r\n"+
            "	      }\r\n"+
            "        }\r\n"+
            "	    top++;\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.onCellMouseDown=function(e){\r\n"+
            "	var that=this;\r\n"+
            "    var shiftKey=e.shiftKey;\r\n"+
            "    var ctrlKey=e.ctrlKey;\r\n"+
            "    \r\n"+
            "	var cell=getMouseTarget(e);\r\n"+
            "	var row=cell.rowLocation;\r\n"+
            "	while(row==null){\r\n"+
            "		cell=cell.parentNode;\r\n"+
            "		if(cell==null)\r\n"+
            "			return;\r\n"+
            "		row=cell.rowLocation;\r\n"+
            "	}\r\n"+
            "	var col=cell.colLocation;\r\n"+
            "	\r\n"+
            "    var doc=getDocument(this.containerElement);\r\n"+
            "	doc.onmouseup=this.onCellMouseUpFunc;\r\n"+
            "	doc.onmousemove=function(e){that.onOutsideMouseDragging(e);};\r\n"+
            "	var button=getMouseButton(e);\r\n"+
            "	if(button==2){\r\n"+
            "		//redundant\r\n"+
            "	  if(!this.selectedRows.isSelected(row)){\r\n"+
            "		  this.selectedRows.clear();\r\n"+
            "		  this.selectRow(e,row,shiftKey,ctrlKey,false);\r\n"+
            "	  }\r\n"+
            "	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "      this.contextMenuCurrentColumn=-1;\r\n"+
            "	  if(this.onUserContextMenu!=null){\r\n"+
            "	      this.curseqnum=nextSendSeqnum;\r\n"+
            "		  this.onUserContextMenu(e);\r\n"+
            "	  }\r\n"+
            "	}else{\r\n"+
            "	  if(!ctrlKey && !shiftKey && this.columns[col] != null){\r\n"+
            "		  // the first time you click on a cell, clickable is undefined because the selection has not yet been registered.\r\n"+
            "		  // clickable will be true the second time you select the same cell\r\n"+
            "	    var clickable=(this.columns[col].clickable && this.selectedRows.isSelected(row));\r\n"+
            "	    var oneClick=this.columns[col].oneClick;\r\n"+
            "		this.callback(\"cellClicked\",{columnIndex:col,rowIndex:row,clickable:clickable,oneClick:oneClick});\r\n"+
            "		if(clickable)\r\n"+
            "		  return;\r\n"+
            "	  } \r\n"+
            "      if(!e.shiftKey  && !e.ctrlKey){// TODO: why does this block exist?\r\n"+
            "	    this.dragSelect=true;\r\n"+
            "      }\r\n"+
            "	  this.selectRow(e,row,shiftKey,ctrlKey,false);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.selectRow=function(e,row,shiftKey,ctrlKey,isDragging){\r\n"+
            "	var before=this.selectedRows.toString();\r\n"+
            "	var beforeCurrentRow=this.currentRow;\r\n"+
            "	if(!isDragging && this.dragSelect){\r\n"+
            "		this.dragStart=row;\r\n"+
            "	}\r\n"+
            "    if(ctrlKey && shiftKey){\r\n"+
            "    	if(this.currentRow==-1){\r\n"+
            "    		this.selectedRows.remove(row);\r\n"+
            "    	}\r\n"+
            "    	else if(this.currentRow<row){\r\n"+
            "    		this.selectedRows.remove(this.currentRow,row);\r\n"+
            "   		}\r\n"+
            "    	else{\r\n"+
            "    		this.selectedRows.remove(row,this.currentRow);\r\n"+
            "    	}\r\n"+
            "    }else if(ctrlKey){\r\n"+
            "    	var sel = this.selectedRows.isSelected(row);\r\n"+
            "    	if (getMouseButton(e) == 1 && sel) {\r\n"+
            "			this.selectedRows.remove(row);\r\n"+
            "    	} else if (sel && beforeCurrentRow != row) {\r\n"+
            "    		this.selectedRows.remove(beforeCurrentRow);\r\n"+
            "    	} else\r\n"+
            "    		this.selectedRows.add(row);\r\n"+
            "    }else if(shiftKey){\r\n"+
            "    	if(this.currentRow==-1)\r\n"+
            "    		this.selectedRows.add(row);\r\n"+
            "    	else if(this.currentRow<row)\r\n"+
            "    		this.selectedRows.add(this.currentRow,row);\r\n"+
            "    	else\r\n"+
            "    		this.selectedRows.add(row,this.currentRow);\r\n"+
            "    }else if(this.dragSelect){\r\n"+
            "    	this.selectedRows.clear();\r\n"+
            "    	if(row>this.dragStart){\r\n"+
            "    		this.selectedRows.add(this.dragStart,row);\r\n"+
            "    	}\r\n"+
            "    	else{\r\n"+
            "    		this.selectedRows.add(row,this.dragStart);\r\n"+
            "    	}\r\n"+
            "    }else{\r\n"+
            "    	this.selectedRows.clear();\r\n"+
            "  		this.selectedRows.add(row);\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "    // ensure something is selected after deselect from ctrl + click\r\n"+
            "    if (this.selectedRows.isSelected(row)) {\r\n"+
            "		this.currentRow = row;\r\n"+
            "	} else if (this.selectedRows.isSelected(beforeCurrentRow)) {\r\n"+
            "		this.currentRow = beforeCurrentRow;\r\n"+
            "	} else if (!this.selectedRows.isEmpty()) {\r\n"+
            "		this.currentRow = this.selectedRows.getNext();\r\n"+
            "	} else\r\n"+
            "		this.currentRow = -1;\r\n"+
            "    \r\n"+
            "    this.flagRowsRepaint = true;\r\n"+
            "    this.repaint(false);\r\n"+
            "    \r\n"+
            "	var after=this.selectedRows.toString();\r\n"+
            "	if(after!=before || this.currentRow!=beforeCurrentRow)\r\n"+
            "		this.fireUserSelected(null,this.currentRow,this.selectedRows.toString());\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.fireUserSelected=function(e,activeRow,selectedRows){\r\n"+
            "	this.userSelectSeqnum++;\r\n"+
            "	this.onUserSelected(e,activeRow,selectedRows,this.userSelectSeqnum);\r\n"+
            "}\r\n"+
            "  \r\n"+
            "//called strickly by backend\r\n"+
            "FastTable.prototype.setActiveRow=function(activeRow,seqnum){\r\n"+
            "	if(seqnum<this.userSelectSeqnum)\r\n"+
            "		return;\r\n"+
            "	this.currentRow=activeRow;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//called strickly by backend\r\n"+
            "FastTable.prototype.setSelectedRows=function(selectedRows,seqnum){\r\n"+
            "	if(seqnum<this.userSelectSeqnum)\r\n"+
            "		return;\r\n"+
            "	this.selectedRows.parseString(selectedRows);\r\n"+
            "}\r\n"+
            "FastTable.prototype.getActiveRow=function(){\r\n"+
            "	return this.currentRow;\r\n"+
            "}\r\n"+
            "FastTable.prototype.getSelectedRows=function(){\r\n"+
            "	return this.selectedRows.toString();\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateSortAndFilter=function(colsSort){\r\n"+
            "	// use delta\r\n"+
            "	for(var i=0;i<colsSort.length;i++){\r\n"+
            "		var sort = colsSort[i].s;\r\n"+
            "		var index = colsSort[i].i; \r\n"+
            "		var filter = colsSort[i].f; \r\n"+
            "		var filterText = colsSort[i].ft; \r\n"+
            "		var col = this.columns[index];\r\n"+
            "		if (sort != null) {\r\n"+
            "			col.sort=sort;\r\n"+
            "			this.setColumnSort(index, sort);\r\n"+
            "		}\r\n"+
            "		if (filter != null) {\r\n"+
            "			col.filter=filter;\r\n"+
            "			col.filterText=filterText;\r\n"+
            "			this.setColumnFilter(index);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.setColumnSort=function(index, sort){\r\n"+
            "        var col = this.columns[index];\r\n"+
            "        if(col == undefined)\r\n"+
            "                return;\r\n"+
            "        var className='';\r\n"+
            "        if(sort=='')\r\n"+
            "                className='';\r\n"+
            "        else if(sort==1)\r\n"+
            "                className='';\r\n"+
            "        else if(sort==0)\r\n"+
            "                className='';\r\n"+
            "        else if(sort==3)\r\n"+
            "                className='asc';\r\n"+
            "        else if(sort==2)\r\n"+
            "                className='des';\r\n"+
            "        if(col.filter)// {\r\n"+
            "                className='header_filtered '+className;\r\n"+
            "\r\n"+
            "        col.headerClassName=className;\r\n"+
            "    col.headerElement.className=\"header \"+ className;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.setColumnFilter=function(index){\r\n"+
            "	var col = this.columns[index];\r\n"+
            "        \r\n"+
            "	if(col.filter){\r\n"+
            "		col.hasFilter=true;\r\n"+
            "		if(this.filteredColumnBgColor!=null){\r\n"+
            "			col.headerElement.style.backgroundColor=this.filteredColumnBgColor;\r\n"+
            "		} else {\r\n"+
            "			col.headerElement.style.backgroundColor=\"#ff7f00\";\r\n"+
            "		}\r\n"+
            "		if(this.filteredColumnFontColor!=null){\r\n"+
            "			col.headerElement.style.color=this.filteredColumnFontColor;\r\n"+
            "		} else {\r\n"+
            "			col.headerElement.style.color=\"white\";\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		co");
          out.print(
            "l.hasFilter=false;\r\n"+
            "		col.headerElement.style.backgroundColor=null;\r\n"+
            "		col.headerElement.style.color=null;\r\n"+
            "	}\r\n"+
            "	col.updateQuickFilterValue();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTable.prototype.initColumns=function(pinning,cols){\r\n"+
            "	this.pinning=pinning;\r\n"+
            "	this.currentRow=-1;\r\n"+
            "	this.selectedRows.clear();\r\n"+
            "	removeAllChildren(this.headerElement);\r\n"+
            "	// Instead of looping through all elements, go through visible elements;\r\n"+
            "	this.deleteAllVisibleCells();\r\n"+
            "	this.cellElements.clear();\r\n"+
            "	this.valueElements.clear();\r\n"+
            "	this.cellElements.setSize(this.totalColumns,this.totalRows);\r\n"+
            "	this.data.clear();\r\n"+
            "	this.columns=[];\r\n"+
            "	this.hiddenColumns=[];\r\n"+
            "\r\n"+
            "    var startCol=this.columns.length;\r\n"+
            "    resetPushDraggableContainer(this.headerElement);\r\n"+
            "	for(var i=0;i<cols.length;i++){\r\n"+
            "		var col=cols[i];\r\n"+
            "		var cssClass=col.cssClass;\r\n"+
            "		var id=col.id;\r\n"+
            "		var name=col.name;\r\n"+
            "		var visible=col.visible;\r\n"+
            "		var sort=col.sort;\r\n"+
            "		var clickable=col.clickable;\r\n"+
            "		var oneClick=col.oneClick;\r\n"+
            "		var isFixed=col.fix;\r\n"+
            "		var hasHover=col.hasHover;\r\n"+
            "		var headerStyle=col.headerStyle;\r\n"+
            "		var className='';\r\n"+
            "		var filter = col.filter;\r\n"+
            "		var filterText = col.filterText;\r\n"+
            "		var hids = col.hids;\r\n"+
            "		if(sort=='')\r\n"+
            "			className='';\r\n"+
            "		else if(sort==1)\r\n"+
            "			className='';\r\n"+
            "		else if(sort==0)\r\n"+
            "			className='';\r\n"+
            "		else if(sort==3)\r\n"+
            "			className='asc';\r\n"+
            "		else if(sort==2)\r\n"+
            "			className='des';\r\n"+
            "		if(col.filter)// {\r\n"+
            "			className='header_filtered '+className;\r\n"+
            "		var width=col.width;\r\n"+
            "		if(visible){\r\n"+
            "			var column=this.addColumn(id,name,width,className,cssClass,headerStyle,null,col.jsFormatterType, filter, filterText, isFixed,hasHover);\r\n"+
            "			column.setHIDS(hids);\r\n"+
            "			if(col.filter){\r\n"+
            "				column.hasFilter=true;\r\n"+
            "				if(this.filteredColumnBgColor!=null){\r\n"+
            "					column.headerElement.style.backgroundColor=this.filteredColumnBgColor;\r\n"+
            "				} else {\r\n"+
            "					column.headerElement.style.backgroundColor=\"#ff7f00\";\r\n"+
            "				}\r\n"+
            "				if(this.filteredColumnFontColor!=null){\r\n"+
            "					column.headerElement.style.color=this.filteredColumnFontColor;\r\n"+
            "				} else {\r\n"+
            "					column.headerElement.style.color=\"white\";\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "			else{\r\n"+
            "				column.hasFilter=false;\r\n"+
            "			}\r\n"+
            "		    column.clickable=clickable;\r\n"+
            "		    column.oneClick=oneClick;\r\n"+
            "		    column.isFixed=isFixed;\r\n"+
            "		}else\r\n"+
            "			this.hiddenColumns[id]=name;\r\n"+
            "	}\r\n"+
            "	this.updateColumnOffsets(startCol);\r\n"+
            "	this.stateInit = true; // lets FastTablePortlet know when to start sending clipzone\r\n"+
            "	this.flagInitColumns=true;\r\n"+
            "	this.repaint(false);\r\n"+
            "}\r\n"+
            "\r\n"+
            "//TODO:Remove Not used\r\n"+
            "FastTable.prototype.getColumnBox=function(colIndex){\r\n"+
            "	var box = null;\r\n"+
            "	if(colIndex < this.columns.length){\r\n"+
            "		box = {};\r\n"+
            "		var col = this.columns[colIndex];\r\n"+
            "		var header = col.headerElement;\r\n"+
            "		\r\n"+
            "		var h1 = this.totalHeight + this.headerHeight; // Height of rows + header\r\n"+
            "//		err(header.offsetHeight);\r\n"+
            "//		err(this.headerHeight);\r\n"+
            "		var h = this.containerElement.offsetHeight - this.scrollSize; // Height of the container - the scrollbar\r\n"+
            "		h = h < h1 ? h: h1;\r\n"+
            "\r\n"+
            "		var x = header.offsetLeft + header.offsetWidth;\r\n"+
            "		if(colIndex < this.pinning){\r\n"+
            "			x -= this.scrollPane.hscroll.clipTop\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		box.w = col.width;\r\n"+
            "		box.h = h;\r\n"+
            "		box.x = x;\r\n"+
            "		box.y = 0;\r\n"+
            "	}\r\n"+
            "	return box;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.firstVisibleColumn = null;\r\n"+
            "FastTable.prototype.lastVisibleColumn = null;\r\n"+
            "FastTable.prototype.updateVisibleColumns=function(){\r\n"+
            "	this.firstVisiblePinnedColumn = null;\r\n"+
            "	this.lastVisiblePinnedColumn = null;\r\n"+
            "	this.firstVisibleColumn = null;\r\n"+
            "	this.lastVisibleColumn = null;\r\n"+
            "	var left;\r\n"+
            "	var right;\r\n"+
            "	if(this.pinning > 0){\r\n"+
            "		left = 0;\r\n"+
            "		right = this.scrollPane.getClipWidth();\r\n"+
            "		\r\n"+
            "	    for(var i=0;i<this.pinning;i++){\r\n"+
            "	      var col = this.columns[i];\r\n"+
            "	      var colRight = col.getRightOffset();\r\n"+
            "	      if(colRight > left){\r\n"+
            "	    	  this.firstVisiblePinnedColumn = i;\r\n"+
            "	    	  break;\r\n"+
            "	      }\r\n"+
            "	    }\r\n"+
            "	    for(var i=this.pinning-1;i>= 0;i--){\r\n"+
            "	      var col = this.columns[i];\r\n"+
            "	      var colLeft =  col.offset;\r\n"+
            "	      if(colLeft < right){\r\n"+
            "	    	  this.lastVisiblePinnedColumn = i;\r\n"+
            "	    	  break;\r\n"+
            "	      }\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(this.totalPinnedWidth >= this.scrollPane.getClipWidth()){\r\n"+
            "		this.firstVisibleColumn = null;\r\n"+
            "		this.lastVisibleColumn = null;\r\n"+
            "		\r\n"+
            "	}else{\r\n"+
            "		var clipLeft = this.scrollPane.getClipLeft();\r\n"+
            "		clipLeft += Math.min(this.totalPinnedWidth, this.scrollPane.getClipWidth());\r\n"+
            "	    for(var i=0;i<this.columns.length;i++){\r\n"+
            "	      var col = this.columns[i];\r\n"+
            "	      var colRight = col.getRightOffset();\r\n"+
            "	      if(colRight > clipLeft){\r\n"+
            "	    	  this.firstVisibleColumn = i;\r\n"+
            "	    	  break;\r\n"+
            "	      }\r\n"+
            "	    }\r\n"+
            "	    \r\n"+
            "		var clipRight = this.scrollPane.getClipLeft() + this.scrollPane.getClipWidth();\r\n"+
            "	    for(var i=this.columns.length-1;i>= 0;i--){\r\n"+
            "	      var col = this.columns[i];\r\n"+
            "	      var colLeft =  col.offset;\r\n"+
            "	      if(colLeft < clipRight){\r\n"+
            "	    	  this.lastVisibleColumn = i;\r\n"+
            "	    	  break;\r\n"+
            "	      }\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "FastTable.prototype.updatePinnedBorder=function(){\r\n"+
            "	if(this.pinning == 0){\r\n"+
            "	   this.pinnedBorderElement.style.display = \"none\"; \r\n"+
            "	   this.pinnedTable.style.display=\"none\";\r\n"+
            "	}\r\n"+
            "	if(this.pinning && this.pinning <= this.columns.length){\r\n"+
            "		this.pinnedTable.style.display=null;\r\n"+
            "		this.pinnedTable.style.width=toPx(this.totalPinnedWidth);\r\n"+
            "		\r\n"+
            "		var col = this.columns[this.pinning-1];\r\n"+
            "		var left = col.offset+ col.width;\r\n"+
            "		var height = this.scrollPane.getClipHeight();\r\n"+
            "//		if(this.hideHeaderDivider == \"false\")\r\n"+
            "			height += parseInt(this.headerHeight);\r\n"+
            "		if(this.quickColumnFilterHeight !=null)\r\n"+
            "			height += this.quickColumnFilterHeight;\r\n"+
            "		\r\n"+
            "		\r\n"+
            "		this.pinnedBorderElement.style.left = toPx(left-5);\r\n"+
            "		this.pinnedBorderElement.style.height = toPx(height);\r\n"+
            "		if(this.pinning && this.scrollPane.hscroll.clipTop > this.pinnedBorderWidth/2){\r\n"+
            "			this.pinnedBorderElement.style.display = \"inherit\"; \r\n"+
            "		}else{\r\n"+
            "			this.pinnedBorderElement.style.display = \"none\"; \r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.setPinnedBorderWidth=function(width){\r\n"+
            "	this.pinnedBorderWidth = width;\r\n"+
            "	this.pinnedBorderElement.style.width = toPx(width);\r\n"+
            "}\r\n"+
            "FastTable.prototype.applyPinnedBorderStyle=function(style){\r\n"+
            "	var dummyElement = {style:{}};\r\n"+
            "	applyStyle(dummyElement, style);\r\n"+
            "	Object.assign(this.pinnedBorderElement.style, dummyElement.style);\r\n"+
            "}\r\n"+
            "FastTable.prototype.clearData=function(){\r\n"+
            "//  this.valueElements.clear();\r\n"+
            "//	this.data.clear();\r\n"+
            "//	this.deleteAllCells();\r\n"+
            "//	this.firstVisiblePinnedColumn = null;\r\n"+
            "//	this.lastVisiblePinnedColumn = null;\r\n"+
            "//	this.firstVisibleColumn = null;\r\n"+
            "//	this.lastVisibleColumn = null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//set row data \r\n"+
            "\r\n"+
            "FastTable.prototype.srd=function(){\r\n"+
            "	var y=arguments[0];\r\n"+
            "	var uid=arguments[1];\r\n"+
            "	for(var i=2;i<arguments.length;){\r\n"+
            "	    var x=arguments[i++];\r\n"+
            "	    if(x==-3){\r\n"+
            "	    	var start=arguments[i++];\r\n"+
            "	    	var cnt=arguments[i++];\r\n"+
            "	    	while(cnt--)\r\n"+
            "		      this.setData(uid,start++,y,arguments[i++],arguments[i++])\r\n"+
            "	    }else if(x>=0)\r\n"+
            "		    this.setData(uid,x,y,arguments[i++],arguments[i++])\r\n"+
            "	    else if(x==-1)\r\n"+
            "	        this.setRowTxColor(y,arguments[i++]);//,rowTxColor,rowBgColor);\r\n"+
            "	    else if(x==-2)\r\n"+
            "	        this.setRowBgColor(y,arguments[i++]);//,rowTxColor,rowBgColor);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.setRowTxColor = function(y,rowTxColor){\r\n"+
            "    var isVisibleY = this.upperRow <= y && y <= this.lowerRow;\r\n"+
            "    \r\n"+
            "    if(!isVisibleY)\r\n"+
            "    	return;\r\n"+
            "	this.data.set(0,y,rowTxColor);\r\n"+
            "    this.updateCellsStylesForRow(y);\r\n"+
            "}\r\n"+
            "FastTable.prototype.setRowBgColor = function(y,rowBgColor){\r\n"+
            "    var isVisibleY = this.upperRow <= y && y <= this.lowerRow;\r\n"+
            "    \r\n"+
            "    if(!isVisibleY)\r\n"+
            "    	return;\r\n"+
            "	this.data.set(1,y,rowBgColor);\r\n"+
            "    this.updateCellsStylesForRow(y);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.setColumnWidth=function(location,width){\r\n"+
            "   this.columns[location].width=width;\r\n"+
            "   this.updateColumnOffsets(location);\r\n"+
            "	\r\n"+
            "   return;\r\n"+
            "//   this.totalWidth=this.columns[location].offset+this.columns[location].width;\r\n"+
            "//   for(var i=location+1;i<this.columns.length;i++){\r\n"+
            "//     this.columns[i].offset=this.totalWidth;\r\n"+
            "//	 this.totalWidth+=this.columns[i].width;\r\n"+
            "//   }\r\n"+
            "//   this.scrollPane.setPaneSize(this.totalWidth,this.totalHeight);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.updateColumnOffsets=function(startCol){\r\n"+
            "	if(this.columns.length == 0){\r\n"+
            "		this.totalWidth = 0;\r\n"+
            "		this.totalPinnedWidth = 0;\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	this.totalWidth=this.columns[startCol].getRightOffset();\r\n"+
            "	if(this.pinning == 0)\r\n"+
            "		this.totalPinnedWidth = 0;\r\n"+
            "	if(startCol < this.pinning){\r\n"+
            "		this.totalPinnedWidth = this.totalWidth; \r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	for(var i = startCol+1; i < this.columns.length; i++){\r\n"+
            "		this.columns[i].offset=this.totalWidth;\r\n"+
            "		this.totalWidth+=this.columns[i].width;\r\n"+
            "		if(i < this.pinning){\r\n"+
            "			this.totalPinnedWidth = this.totalWidth; \r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.onHeaderDragging=function(e,x,y,column){\r\n"+
            "	var left = e.offsetLeft;\r\n"+
            "	var width=left-this.columns[e.location].offset+3;\r\n"+
            "	if(e.location<this.pinning){\r\n"+
            "		width-=fl(this.scrollPane.getClipLeft());\r\n"+
            "	}\r\n"+
            "	this.setColumnWidth(e.location,width);\r\n"+
            "    if(this.onUserColumnResize!=null)\r\n"+
            "    	this.onUserColumnResize(e,e.location,width);\r\n"+
            "	\r\n"+
            "   	this.flagColumnsMoving = true;\r\n"+
            "   	this.repaint(false);\r\n"+
            "    return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.clipHeaderDragging=function(e,rect){\r\n"+
            "	var min=this.columns[e.location].offset+5;\r\n"+
            "	if(min>rect.getLeft())\r\n"+
            "		rect.setLeft(min);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.fireSizeChanged=function(){\r\n"+
            "	if(this.widthChanged == true || this.heightChanged == true){\r\n"+
            "		this.flagSizeChanged = true;\r\n"+
            "		this.repaint(false);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateContainerElement=function(){\r\n"+
            "    new Rect(0,this.menuHeight,this.width,this.height).writeToElement(this.containerElement);\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateScrollPaneElement=function(){\r\n"+
            "	var x = 0;\r\n"+
            "	var y = parseInt(this.headerHeight);\r\n"+
            "	var w = this.width;\r\n"+
            "	var h = this.height - this.headerHeight;\r\n"+
            "	//TODOFILTER\r\n"+
            "	if(this.quickColumnFilterHidden == false){\r\n"+
            "		h -= this.quickColumnFilterHeight;\r\n"+
            "		y += this.quickColumnFilterHeight;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "    this.scrollPane.setLocation(x,y,w,h);\r\n"+
            "	//this.scrollPane.setLocation(0,this.headerHeight,this.width,this.height-this.headerHeight);\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateTabl");
          out.print(
            "eElements=function(){\r\n"+
            "    this.tableElement.style.width=toPx(this.width);\r\n"+
            "    this.tableElement.style.height=toPx(this.height-this.headerHeight);\r\n"+
            "    this.pinnedTable.style.height=toPx(this.height-this.headerHeight);\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateHeaderElement=function(){\r\n"+
            "	//TODOFILTER \r\n"+
            "\r\n"+
            "	var headerElementHeight = parseInt(this.headerHeight);\r\n"+
            "	if(this.quickColumnFilterHidden == false){\r\n"+
            "		headerElementHeight += this.quickColumnFilterHeight;\r\n"+
            "	}\r\n"+
            "    new Rect(0,0,Math.max(this.totalWidth+this.scrollSize,this.width),headerElementHeight).writeToElement(this.headerElement);\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateScrollPaneSize=function(){\r\n"+
            "    this.scrollPane.setPaneSize(this.totalWidth,this.totalHeight);\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateHeaderScroll=function(){	\r\n"+
            "  //Update horizontal scroll position\r\n"+
            "  var leftPos=Math.round(this.scrollPane.getClipLeft());\r\n"+
            "  this.headerElement.style.left=toPx(-this.scrollPane.getClipLeft());\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateVisibleRowsCount=function(){\r\n"+
            "	//TODO:check for unnecessary calls;\r\n"+
            "	this.visibleRowsCount=this.getLowerRowVisible()-this.getUpperRowVisible()+1;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.nwCell=function(cellX, cellY){\r\n"+
            "	  var columnClassName=\"table_cell \"+this.columns[cellX].cellClassName;\r\n"+
            "	  cell=nw(\"div\",columnClassName);\r\n"+
            "	  this.cellElements.set(cellX,cellY,cell);\r\n"+
            "	  cell.rowLocation=cellY;\r\n"+
            "	  cell.colLocation=cellX;\r\n"+
            "	  cell.tabIndex=0;\r\n"+
            "	  cell.columnClassName=columnClassName;\r\n"+
            "	  cell.onmousedown=this.onCellMouseDownFunc;\r\n"+
            "	  cell.onmouseover=this.onCellMouseOverFunc;\r\n"+
            "	  cell.onmousemove=this.onCellMouseMoveFunc;\r\n"+
            "	  cell.onmouseout=this.onCellMouseOutFunc;\r\n"+
            "	  if(cellX<this.pinning)\r\n"+
            "		  this.pinnedTable.appendChild(cell);\r\n"+
            "	  else\r\n"+
            "		  this.tableElement.appendChild(cell);\r\n"+
            "	  return cell;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.deleteAllVisibleCells=function(){\r\n"+
            "	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)\r\n"+
            "		return;\r\n"+
            "	\r\n"+
            "	if(this.pinning > 0)\r\n"+
            "		if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)\r\n"+
            "			for(var y = this.upperRow; y <= this.lowerRow; y++){\r\n"+
            "				for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){\r\n"+
            "					this.removeCell(x,y);\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)\r\n"+
            "		for(var y = this.upperRow; y <= this.lowerRow; y++){\r\n"+
            "			for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){\r\n"+
            "				this.removeCell(x,y);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	\r\n"+
            "	this.firstVisiblePinnedColumn = null;\r\n"+
            "	this.lastVisiblePinnedColumn = null;\r\n"+
            "	this.firstVisibleColumn = null;\r\n"+
            "	this.lastVisibleColumn = null;\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateCellsLocations=function(){\r\n"+
            "	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)\r\n"+
            "		return;\r\n"+
            "	\r\n"+
            "	if(this.pinning > 0)\r\n"+
            "		if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)\r\n"+
            "			for(var y = this.upperRow; y <= this.lowerRow; y++){\r\n"+
            "				for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){\r\n"+
            "					var cell=this.cellElements.get(x,y);\r\n"+
            "					if(cell != null){\r\n"+
            "						this.applyCellSize(cell, x, y);\r\n"+
            "						this.applyCellLocation(cell, x, y);\r\n"+
            "					}\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)\r\n"+
            "		for(var y = this.upperRow; y <= this.lowerRow; y++){\r\n"+
            "			for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){\r\n"+
            "				var cell=this.cellElements.get(x,y);\r\n"+
            "				if(cell != null){\r\n"+
            "					this.applyCellSize(cell, x, y);\r\n"+
            "					this.applyCellLocation(cell,x,y);\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateCellsStyles=function(){\r\n"+
            "	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)\r\n"+
            "		return;\r\n"+
            "	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)\r\n"+
            "		for(var y = this.upperRow; y <= this.lowerRow; y++){\r\n"+
            "			for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){\r\n"+
            "				var cell=this.cellElements.get(x,y);\r\n"+
            "				if(cell != null)\r\n"+
            "					this.applyStyle(cell, cell._style);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)\r\n"+
            "		for(var y = this.upperRow; y <= this.lowerRow; y++){\r\n"+
            "			for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){\r\n"+
            "				var cell=this.cellElements.get(x,y);\r\n"+
            "				if(cell != null)\r\n"+
            "					this.applyStyle(cell, cell._style);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateCellsStylesRow=function(){\r\n"+
            "	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)\r\n"+
            "		return;\r\n"+
            "	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)\r\n"+
            "		for(var y = this.upperRow; y <= this.lowerRow; y++){\r\n"+
            "			this.updateCellStylesForRow(y);\r\n"+
            "		}\r\n"+
            "	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)\r\n"+
            "		for(var y = this.upperRow; y <= this.lowerRow; y++){\r\n"+
            "			this.updateCellStylesForRow(y);\r\n"+
            "		}\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateCellsStylesForRow=function(y){\r\n"+
            "	if(this.cellElements.getWidth() == 0 || this.cellElements.getHeight()== 0)\r\n"+
            "		return;\r\n"+
            "	if(y<this.upperRow || y>this.lowerRow)\r\n"+
            "		return;\r\n"+
            "	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)\r\n"+
            "			for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){\r\n"+
            "				var cell=this.cellElements.get(x,y);\r\n"+
            "				if(cell != null)\r\n"+
            "					this.applyStyle(cell, cell._style);\r\n"+
            "			}\r\n"+
            "	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)\r\n"+
            "			for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){\r\n"+
            "				var cell=this.cellElements.get(x,y);\r\n"+
            "				if(cell != null)\r\n"+
            "					this.applyStyle(cell, cell._style);\r\n"+
            "			}\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateCells=function(){\r\n"+
            "	return;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.calcCellWidth=function(x){\r\n"+
            "	return this.columns[x].width;\r\n"+
            "}\r\n"+
            "FastTable.prototype.calcCellHeight=function(y){\r\n"+
            "	return parseInt(this.rowHeight);\r\n"+
            "}\r\n"+
            "FastTable.prototype.calcCellLeft=function(x){\r\n"+
            "	return x < this.pinning? this.columns[x].offset: this.columns[x].offset - Math.round(this.scrollPane.getClipLeft());\r\n"+
            "}\r\n"+
            "FastTable.prototype.calcCellTop=function(y){\r\n"+
            "	if(y > this.totalRows || y < 0)\r\n"+
            "		return -1;\r\n"+
            "	return y * this.rowHeight - this.scrollPane.getClipTop();\r\n"+
            "}\r\n"+
            "FastTable.prototype.calcRowFromTop=function(y){\r\n"+
            "	return Math.floor( (y + this.scrollPane.getClipTop())/ this.rowHeight);\r\n"+
            "}\r\n"+
            "FastTable.prototype.calcColFromLeft=function(x){\r\n"+
            "	var col = -1;\r\n"+
            "	if(x < 0)\r\n"+
            "		return col;\r\n"+
            "	if(x <= this.totalPinnedWidth){\r\n"+
            "		for(var i = 0; i < this.pinning; i++){\r\n"+
            "			var colmeta = this.columns[i];\r\n"+
            "			if(colmeta.offset <= x && x < (colmeta.offset + colmeta.width)){\r\n"+
            "				col = i;\r\n"+
            "				break;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		x+= this.scrollPane.getClipLeft();\r\n"+
            "		for(var i = this.pinning; i < this.totalColumns; i++){\r\n"+
            "			var colmeta = this.columns[i];\r\n"+
            "			if(colmeta.offset <= x && x < (colmeta.offset + colmeta.width)){\r\n"+
            "				col = i;\r\n"+
            "				break;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	return col;\r\n"+
            "//	return Math.floor( y/ this.rowHeight);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.applyCellSize=function(cell, x, y){\r\n"+
            "		cell.style.width=toPx(this.columns[x].width);\r\n"+
            "	cell.style.height=toPx(this.rowHeight);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.applyCellLocation=function(cell, x, y){\r\n"+
            "	cell.rowLocation=y;\r\n"+
            "	cell.colLocation=x;\r\n"+
            "	\r\n"+
            "	\r\n"+
            "	if(x < this.pinning){\r\n"+
            "		var topPos=this.scrollPane.getClipTop();\r\n"+
            "		cell.style.left=toPx(this.columns[x].offset);		\r\n"+
            "		cell.style.top=toPx(y*this.rowHeight-topPos);\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		var topPos=this.scrollPane.getClipTop();\r\n"+
            "		var leftPos=Math.round(this.scrollPane.getClipLeft());\r\n"+
            "		cell.style.left=toPx(this.columns[x].offset-leftPos);\r\n"+
            "		cell.style.top=toPx(y*this.rowHeight-topPos);\r\n"+
            "	}\r\n"+
            "		\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTable.prototype.updateStyle=function(cell){\r\n"+
            "  cell.style.display='inline-flex';\r\n"+
            "  var y = cell.rowLocation;\r\n"+
            "  var x = cell.colLocation;\r\n"+
            "  \r\n"+
            "  // Selection\r\n"+
            "  var selected=this.selectedRows.isSelected(y);\r\n"+
            "  var cn=cell.columnClassName;\r\n"+
            "  \r\n"+
            "  if(this.currentRow==y){\r\n"+
            "    cn+=' '+ this.activeClassname; \r\n"+
            "  }else if(selected){\r\n"+
            "    cn+=' '+ this.selectedClassname; \r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  //Row Text + Background Color\r\n"+
            "  var rowTxColor=this.data.get(0,y);\r\n"+
            "  var rowBgColor=this.data.get(1,y);\r\n"+
            "  if(rowBgColor){\r\n"+
            "	  cell.style.backgroundColor=rowBgColor;\r\n"+
            "  }else if(y%2 || !this.useGreybars){\r\n"+
            "	  if (this.greyBarColor != null){\r\n"+
            "		  cell.style.backgroundColor=null;\r\n"+
            "	  }\r\n"+
            "	  applyStyle(cell,this.bgStyle);\r\n"+
            "	  cell.style.backgroundImage=null;\r\n"+
            "  }else{\r\n"+
            "	  cell.style.backgroundColor=this.greyBarColor;\r\n"+
            "	  cell.style.backgroundImage=null;\r\n"+
            "  }\r\n"+
            "  if(rowTxColor){\r\n"+
            "	  cell.style.color=rowTxColor;\r\n"+
            "  }else\r\n"+
            "	  cell.style.color=this.defaultFontColor;\r\n"+
            "\r\n"+
            "  //Borders and dividers\r\n"+
            "  if(this.cellBorderColor != null)\r\n"+
            "	  cell.style.borderColor=this.cellBorderColor;\r\n"+
            "  if(this.cellBottomDivider != null)\r\n"+
            "	  cell.style.borderBottomWidth=toPx(this.cellBottomDivider);\r\n"+
            "  if(this.cellRightDivider != null)\r\n"+
            "	  cell.style.borderRightWidth=toPx(this.cellRightDivider);\r\n"+
            "  if(this.verticalAlign != null)\r\n"+
            "	  cell.style.justifyContent=this.verticalAlign;\r\n"+
            "  if(this.cellPadHt != null){\r\n"+
            "      cell.style.paddingLeft = toPx(this.cellPadHt);\r\n"+
            "      cell.style.paddingRight = toPx(this.cellPadHt);\r\n"+
            "  }\r\n"+
            " \r\n"+
            "  if (cell.checkbox)\r\n"+
            "  	this.applyCellCheckboxConfig(cell, cell.isChecked, cell.checkboxStyles);\r\n"+
            "  //Class name\r\n"+
            "  cell.className=cn;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.setOptions=function(options){\r\n"+
            "	//TODOFILTER\r\n"+
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
            "	if(options.useGreybars!=null)\r\n"+
            "		th");
          out.print(
            "is.useGreybars=\"true\"==options.useGreybars;\r\n"+
            "	if(options.rowHeight!=null)\r\n"+
            "		this.rowHeight=options.rowHeight;\r\n"+
            "	if(options.headerRowHeight != null){\r\n"+
            "		this.headerHeight = options.headerRowHeight;\r\n"+
            "	}\r\n"+
            "	if(options.headerFontSize != null){\r\n"+
            "		this.headerFontSize = options.headerFontSize;\r\n"+
            "	}\r\n"+
            "	if(options.cellBottomDivider != null){\r\n"+
            "		this.cellBottomDivider = options.cellBottomDivider;\r\n"+
            "	}else this.cellBottomDivider=0;\r\n"+
            "	\r\n"+
            "	if(options.cellRightDivider != null){\r\n"+
            "		this.cellRightDivider = options.cellRightDivider;\r\n"+
            "	}else this.cellRightDivider=1;\r\n"+
            "	if(options.verticalAlign != null){\r\n"+
            "		this.verticalAlign = options.verticalAlign;\r\n"+
            "	}else this.verticalAlign = \"center\";\r\n"+
            "	if(options.cellPadHt != null)\r\n"+
            "		this.cellPadHt = options.cellPadHt;\r\n"+
            "	\r\n"+
            "//	this.tableElement.className=\"\";\r\n"+
            "//	this.pinnedTable.className=\"\";\r\n"+
            "	applyStyle(this.tableElement,options.backgroundStyle);\r\n"+
            "	applyStyle(this.pinnedTable,options.backgroundStyle);\r\n"+
            "	applyStyle(this.scrollPane.DOM.paneElement,options.backgroundStyle);\r\n"+
            "	this.tableElement.className='table '+this.tableElement.className;\r\n"+
            "	this.pinnedTable.className='table '+this.pinnedTable.className;\r\n"+
            "	\r\n"+
            "	applyStyle(this.tableElement,options.tableStyle);\r\n"+
            "	applyStyle(this.pinnedTable,options.tableStyle);\r\n"+
            "\r\n"+
            "	if(options.fontFamily != null)\r\n"+
            "		this.fontFamily = options.fontFamily;\r\n"+
            "	if(options.fontSize != null)\r\n"+
            "		this.fontSize = options.fontSize;\r\n"+
            "	if(this.fontSize != null){\r\n"+
            "		applyStyle(this.tableElement,\"style.fontSize=\"+toPx(this.fontSize));\r\n"+
            "		applyStyle(this.pinnedTable,\"style.fontSize=\"+toPx(this.fontSize));\r\n"+
            "	}\r\n"+
            "	if(this.fontFamily != null){\r\n"+
            "		applyStyle(this.tableElement,\"style.fontFamily=\"+this.fontFamily);\r\n"+
            "		applyStyle(this.pinnedTable,\"style.fontSize=\"+this.fontFamily);\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	\r\n"+
            "	this.bgStyle = options.backgroundStyle;\r\n"+
            "	this.greyBarColor = options.greyBarColor;\r\n"+
            "	this.flashUpColor = options.flashUpColor;\r\n"+
            "	this.flashDnColor = options.flashDnColor;\r\n"+
            "	this.flashMs = options.flashMs;\r\n"+
            "	if(this.flashMs==null)\r\n"+
            "	  this.flashMs=0;\r\n"+
            "    this.flashStyle='background '+this.flashMs+'ms linear';\r\n"+
            "	if(this.bgStyle==null || this.bgStyle=='_bg=null')// 2nd check is for\r\n"+
            "														// legacy purposes\r\n"+
            "		this.bgStyle='_bg=#FFFFFF';\r\n"+
            "	if(this.greyBarColor==null)\r\n"+
            "		this.greyBarColor='#EEEEEE';\r\n"+
            "	this.defaultFontColor=options.defaultFontColor;\r\n"+
            "	this.hideHeaderDivider = options.hideHeaderDivider;\r\n"+
            "	this.cellBorderColor = options.cellBorderColor;\r\n"+
            "	if(options.scrollBarWidth!=null){\r\n"+
            "	  this.scrollSize = (options.scrollBarWidth * 1);\r\n"+
            "	  this.scrollPane.setSize(options.scrollBarWidth * 1);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	// scroll bar radius\r\n"+
            "	if (options.scrollBarRadius) {\r\n"+
            "		this.scrollPane.hscroll.DOM.applyBorderRadius(options.scrollBarRadius);\r\n"+
            "		this.scrollPane.vscroll.DOM.applyBorderRadius(options.scrollBarRadius);\r\n"+
            "	}\r\n"+
            "	// scroll bar hide arrows\r\n"+
            "	if (options.scrollBarHideArrows) {\r\n"+
            "		this.scrollPane.hscroll.DOM.hideArrows(options.scrollBarHideArrows);\r\n"+
            "		this.scrollPane.vscroll.DOM.hideArrows(options.scrollBarHideArrows);\r\n"+
            "		\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.scrollPane.hscroll.DOM.applyColors(options.gripColor, options.trackColor, options.trackButtonColor, options.scrollBorderColor, options.scrollBarCornerColor);\r\n"+
            "	this.scrollPane.vscroll.DOM.applyColors(options.gripColor, options.trackColor, options.trackButtonColor, options.scrollBorderColor, options.scrollBarCornerColor);\r\n"+
            "	\r\n"+
            "	\r\n"+
            "	// Set scroll icon colors...\r\n"+
            "	scrollIconsColor=options.scrollIconsColor || '#000000';\r\n"+
            "	this.scrollPane.updateIconsColor(scrollIconsColor);\r\n"+
            "	\r\n"+
            "//	this.setLocation(0,0,this.width,this.height);\r\n"+
            "	this.filteredColumnBgColor = options.filteredColumnBgColor;	 \r\n"+
            "	this.filteredColumnFontColor = options.filteredColumnFontColor;\r\n"+
            "	this.menuBarBg=options.menuBarBg;\r\n"+
            "	this.menuFontColor=options.menuFontColor;\r\n"+
            "	\r\n"+
            "	this.activeClassname=options.cellActiveBg==null ? 'cell_active_default' : toCssForColor(this.tableElement,options.cellActiveBg);\r\n"+
            "	this.selectedClassname=options.cellSelectedBg==null ? 'cell_selected_default' : toCssForColor(this.tableElement,options.cellSelectedBg);\r\n"+
            "	if((this.firstVisibleColumn != null && this.lastVisibleColumn != null) || (this.firstVisiblePinnedColumn != null || this.lastVisiblePinnedColumn)){\r\n"+
            "		this.flagOptionsSet=true;\r\n"+
            "		this.repaint(false);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "var COLORS_TO_STYLES_NEXT_ID=0;\r\n"+
            "function toCssForColor(e,color){\r\n"+
            "    var i=parseColorAlpha(color);\r\n"+
            "    var COLORS_TO_STYLES=getWindow(e).COLORS_TO_STYLES;\r\n"+
            "    if(COLORS_TO_STYLES==null)\r\n"+
            "      COLORS_TO_STYLES=getWindow(e).COLORS_TO_STYLES={};\r\n"+
            "    var r=COLORS_TO_STYLES[color];\r\n"+
            "    if(r!=null)\r\n"+
            "    	return r;\r\n"+
            "	var imgurl=toImageData(e,i[0],i[1],i[2],i[3]);\r\n"+
            "	r='ft_custom_'+(COLORS_TO_STYLES_NEXT_ID++);\r\n"+
            "    setCssClassProperty(e,r,'background-image',imgurl);\r\n"+
            "    COLORS_TO_STYLES[color]=r;\r\n"+
            "    return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTable.prototype.updateColumns=function(){\r\n"+
            "	if(this.columns.length == 0)\r\n"+
            "		return;\r\n"+
            "//    var rect=new Rect();\r\n"+
            "    var f = this.firstVisibleColumn;\r\n"+
            "    var l = this.lastVisibleColumn;\r\n"+
            "    var fp = this.firstVisiblePinnedColumn;\r\n"+
            "    var lp = this.lastVisiblePinnedColumn;\r\n"+
            "    var visible = f != null && l != null;\r\n"+
            "    var visiblePinned = fp != null && lp != null;\r\n"+
            "    \r\n"+
            "    var left = this.scrollPane.getClipLeft();\r\n"+
            "    \r\n"+
            "    //TODOFILTER\r\n"+
            "	\r\n"+
            "    for(var i=0;i<this.columns.length;i++){\r\n"+
            "    	if(visible && f <= i && i <= l){\r\n"+
            "    		this.columns[i].headerElement.style.left=toPx(this.columns[i].offset);\r\n"+
            "    		this.columns[i].headerElement.style.width=toPx(this.columns[i].width);\r\n"+
            "\r\n"+
            "    		\r\n"+
            "    		this.columns[i].grabElement.style.left=toPx(this.columns[i].getRightOffset()-3);\r\n"+
            "//		    rect.setLocation(this.columns[i].offset,0,this.columns[i].width,this.headerHeight).writeToElement(this.columns[i].headerElement);\r\n"+
            "//		    rect.setLocation(this.columns[i].getRightOffset()-3,0,7,this.headerHeight).writeToElement(this.columns[i].grabElement);\r\n"+
            "    		this.columns[i].headerElement.style.display = null;\r\n"+
            "    		if (!this.columns[i].isFixed)\r\n"+
            "				this.columns[i].grabElement.style.display = null;\r\n"+
            "    		\r\n"+
            "			if(this.quickColumnFilterHidden == false){\r\n"+
            "	    		//this.columns[i].quickColumnFilterElement.style.left=toPx(this.columns[i].offset);\r\n"+
            "	    		this.columns[i].quickColumnFilterElement.style.width=toPx(this.columns[i].width-4);\r\n"+
            "				//this.columns[i].quickColumnFilterElement.style.display = null;\r\n"+
            "	    	}\r\n"+
            "   			this.columns[i].quickColumnFilterElement.style.display = this.quickColumnFilterHidden == false?null:\"none\";\r\n"+
            "\r\n"+
            "    	}\r\n"+
            "    	else if(visiblePinned && fp <= i && i <= lp){\r\n"+
            "    		this.columns[i].headerElement.style.left=toPx(this.columns[i].offset+left);\r\n"+
            "    		this.columns[i].headerElement.style.width=toPx(this.columns[i].width);\r\n"+
            "    		this.columns[i].grabElement.style.left=toPx(this.columns[i].getRightOffset()+left-3);\r\n"+
            "    		\r\n"+
            "//		    rect.setLocation(this.columns[i].offset+left,0,this.columns[i].width,this.headerHeight).writeToElement(this.columns[i].headerElement);\r\n"+
            "//		    rect.setLocation(this.columns[i].getRightOffset()+left-3,0,7,this.headerHeight).writeToElement(this.columns[i].grabElement);\r\n"+
            "    		this.columns[i].headerElement.style.display = null;\r\n"+
            "    		this.columns[i].grabElement.style.display = null;\r\n"+
            "\r\n"+
            "			if(this.quickColumnFilterHidden == false){\r\n"+
            "	    		//this.columns[i].quickColumnFilterElement.style.left=toPx(this.columns[i].offset+left);\r\n"+
            "	    		this.columns[i].quickColumnFilterElement.style.width=toPx(this.columns[i].width-4);\r\n"+
            "				//this.columns[i].quickColumnFilterElement.style.display = null;\r\n"+
            "	    	}\r\n"+
            "   			this.columns[i].quickColumnFilterElement.style.display = this.quickColumnFilterHidden == false?null:\"none\";\r\n"+
            "    	}\r\n"+
            "    	else{\r\n"+
            "    		this.columns[i].headerElement.style.display = \"none\";\r\n"+
            "    		this.columns[i].grabElement.style.display = \"none\";\r\n"+
            "	    	this.columns[i].quickColumnFilterElement.style.display = \"none\";\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.applyColumnStyle=function(i){\r\n"+
            "	var headerElement=i==-1 ? this.headerElement : this.columns[i].headerElement;\r\n"+
            "	\r\n"+
            "	//Header fontSize, height, and fontFamily\r\n"+
            "	headerElement.style.fontSize = toPx(this.headerFontSize);\r\n"+
            "	headerElement.style.fontFamily=this.fontFamily || \"arial\";\r\n"+
            "	//TODOFILTER\r\n"+
            "	var headerElementHeight = parseInt(this.headerHeight);\r\n"+
            "	if(this.quickColumnFilterHidden == false){\r\n"+
            "		headerElementHeight += this.quickColumnFilterHeight;\r\n"+
            "		if(i != -1){\r\n"+
            "			this.columns[i].quickColumnFilterElement.style.height= toPx(this.quickColumnFilterHeight -7);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	headerElement.style.height = toPx(headerElementHeight);\r\n"+
            "	\r\n"+
            "	//Header divider bar\r\n"+
            "	if(this.hideHeaderDivider==\"true\")\r\n"+
            "		headerElement.style.boxShadow=\"none\";\r\n"+
            "//		headerElement.style.backgroundImage=\"none\";\r\n"+
            "	else\r\n"+
            "//		headerElement.style.boxShadow=null;\r\n"+
            "		if(this.cellBorderColor != null)\r\n"+
            "			headerElement.style.boxShadow=\"inset 0px -8px 5px -5px \"+ this.cellBorderColor;\r\n"+
            "		else\r\n"+
            "			headerElement.style.boxShadow=null;\r\n"+
            "	\r\n"+
            "	headerElement.style.color=this.menuFontColor!=null ? this.menuFontColor : \"white\";\r\n"+
            "	\r\n"+
            "	if(i!=-1){\r\n"+
            "		var col=this.columns[i];\r\n"+
            "		// Header border \r\n"+
            "		headerElement.style.borderColor = this.cellBorderColor;\r\n"+
            "		headerElement.style.borderRightWidth=toPx(this.cellRightDivider);\r\n"+
            "//			headerElement.style.borderBottomWidth=this.cellBottomDivider;\r\n"+
            "//			col.grabElement.style.borderColor=this.cellBorderColor;\r\n"+
            "		col.grabElement.style.background=this.cellBorderColor;\r\n"+
            "		col.grabElement.style.borderColor=\"#ffffff\";\r\n"+
            "		col.grabElement.style.borderWidth=\"0px 2px\";\r\n"+
            "		\r\n"+
            "		headerElement.style.backgroundColor=this.menuBarBg!=null ? this.menuBarBg : \"#535353\";\r\n"+
            "		if(col.hasFilter){\r\n"+
            "			if(this.filteredColumnBgColor!=null){\r\n"+
            "				headerElement.style.backgroundColor=this.filteredColumnBgColor;\r\n"+
            "			} else {\r\n"+
            "				headerElement.style.backgroundColor=\"#ff7f00\";\r\n"+
            "			}\r\n"+
            "			if(this.filteredColumnFontColor!=null){\r\n"+
            "				headerElement.style.color=this.filteredColumnFontColor;\r\n"+
            "			} else {\r\n"+
            "				headerElement.style.color=\"white\";\r\n"+
            "			}\r\n"+
            "		}else{\r\n"+
            "			headerElement.style.backgroundColor=this.menuBarBg!=null ? this.menuBarBg : \"#535353\";\r\n"+
            "		}\r\n"+
            "		applyStyle(col.headerElement,col.columnHeaderStyle);\r\n"+
            "	}else{\r\n"+
            "		heade");
          out.print(
            "rElement.style.backgroundColor=this.menuBarBg!=null ? this.menuBarBg : \"#535353\";\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "//TODOFILTER\r\n"+
            "FastTable.prototype.updateQuickFilterStyles=function(){\r\n"+
            "	if(this.quickColumnFilterHidden == true)\r\n"+
            "		return;\r\n"+
            "	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)\r\n"+
            "		for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){\r\n"+
            "			if(x == -1)\r\n"+
            "				continue;\r\n"+
            "			var quickColumnFilterElement = this.columns[x].quickColumnFilterElement;\r\n"+
            "			quickColumnFilterElement.input.style.backgroundColor= this.quickColumnBackgroundColor;\r\n"+
            "			quickColumnFilterElement.input.style.color= this.quickColumnFontColor;\r\n"+
            "			quickColumnFilterElement.input.style.border= \"1px solid \" + this.quickColumnBorderColor;\r\n"+
            "			quickColumnFilterElement.input.style.fontSize=toPx(this.quickColumnFontSz);\r\n"+
            "		}\r\n"+
            "	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)\r\n"+
            "		for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){\r\n"+
            "			if(x == -1)\r\n"+
            "				continue;\r\n"+
            "			var quickColumnFilterElement = this.columns[x].quickColumnFilterElement;\r\n"+
            "			quickColumnFilterElement.input.style.backgroundColor= this.quickColumnBackgroundColor;\r\n"+
            "			quickColumnFilterElement.input.style.color= this.quickColumnFontColor;\r\n"+
            "			quickColumnFilterElement.input.style.border= \"1px solid \" + this.quickColumnBorderColor;\r\n"+
            "			quickColumnFilterElement.input.style.fontSize=toPx(this.quickColumnFontSz);\r\n"+
            "		}\r\n"+
            "	return;\r\n"+
            "}\r\n"+
            "FastTable.prototype.updateQuickFilterValues=function(){\r\n"+
            "	if(this.quickColumnFilterHidden == true)\r\n"+
            "		return;\r\n"+
            "	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)\r\n"+
            "		for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){\r\n"+
            "			if(x == -1)\r\n"+
            "				continue;\r\n"+
            "			var quickColumnFilterElement = this.columns[x].quickColumnFilterElement;\r\n"+
            "		}\r\n"+
            "	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)\r\n"+
            "		for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){\r\n"+
            "			if(x == -1)\r\n"+
            "				continue;\r\n"+
            "			var quickColumnFilterElement = this.columns[x].quickColumnFilterElement;\r\n"+
            "		}\r\n"+
            "	return;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.updateColumnStyles=function(){\r\n"+
            "	//Sets header bar height\r\n"+
            "	//TODOFILTER\r\n"+
            "	//this.scrollPane.y = this.headerHeight;\r\n"+
            "	var scrollPaneElementTop = parseInt(this.headerHeight);\r\n"+
            "	if(this.quickColumnFilterHidden == false)\r\n"+
            "		scrollPaneElementTop += this.quickColumnFilterHeight;\r\n"+
            "\r\n"+
            "	this.scrollPaneElement.style.top = scrollPaneElementTop;\r\n"+
            "	\r\n"+
            "	this.applyColumnStyle(-1);\r\n"+
            "	\r\n"+
            "	if(this.columns.length == 0)\r\n"+
            "		return;\r\n"+
            "	if(this.firstVisibleColumn != null && this.lastVisibleColumn != null)\r\n"+
            "		for(var x = this.firstVisibleColumn; x <= this.lastVisibleColumn; x++){\r\n"+
            "			this.applyColumnStyle(x);\r\n"+
            "		}\r\n"+
            "	if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null)\r\n"+
            "		for(var x = this.firstVisiblePinnedColumn; x <= this.lastVisiblePinnedColumn; x++){\r\n"+
            "			this.applyColumnStyle(x);\r\n"+
            "		}\r\n"+
            "	return;\r\n"+
            "}\r\n"+
            "FastTable.prototype.getUpperRowVisible=function(){\r\n"+
            "	return Math.max(this.upperRow,0);\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.getLowerRowVisible=function(){\r\n"+
            "	return this.lowerRow;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTable.prototype.cellPool=new Array();\r\n"+
            "\r\n"+
            "FastTable.prototype.removeCell = function(x,y){\r\n"+
            "    var cell=this.cellElements.set(x,y,null);\r\n"+
            "	if(cell==null)\r\n"+
            "		return;\r\n"+
            "    if(this.flashMs>0)\r\n"+
            "	  this.valueElements.remove(x,cell.uid);\r\n"+
            "	if(cell.parentElement)\r\n"+
            "		cell.parentElement.removeChild(cell);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTable.prototype.setClipZone=function(){\r\n"+
            "	var oldUpperRow = this.getUpperRowVisible();\r\n"+
            "	var oldLowerRow = this.getLowerRowVisible();\r\n"+
            "	var oldLeftCol = this.firstVisibleColumn;\r\n"+
            "	var oldRightCol = this.lastVisibleColumn;\r\n"+
            "	var verticalScroll = false;\r\n"+
            "	\r\n"+
            "	//Updates new clipzone (left,right,top,bottom) and sends unused elements to garbage\r\n"+
            "	this.upperRow = Math.floor(this.scrollPane.getClipTop()/this.rowHeight);\r\n"+
            "	this.lowerRow = Math.ceil((this.scrollPane.getClipTop()+this.scrollPane.getClipHeight())/this.rowHeight)-1;\r\n"+
            "	\r\n"+
            "	this.updateVisibleColumns();\r\n"+
            "    \r\n"+
            "	if(this.onUpdateCellsEnd!=null)\r\n"+
            "		this.onUpdateCellsEnd(this);\r\n"+
            "	\r\n"+
            "  \r\n"+
            "	var isPinned = false;\r\n"+
            "	if(this.firstVisiblePinnedColumn !=null && this.lastVisiblePinnedColumn != null)\r\n"+
            "		isPinned = true;\r\n"+
            "	//Scrolled down\r\n"+
            "	if(this.upperRow > oldUpperRow){\r\n"+
            "		var l = this.upperRow <= oldLowerRow? this.upperRow : oldLowerRow+1;\r\n"+
            "		for(var y=oldUpperRow; y < l; y++){\r\n"+
            "			this.data.remove(0, y);\r\n"+
            "			this.data.remove(1, y);\r\n"+
            "			for(var x=this.firstVisibleColumn;x<=this.lastVisibleColumn;x++)\r\n"+
            "				this.removeCell(x,y);\r\n"+
            "			if(isPinned === true)\r\n"+
            "				for(var x= this.firstVisiblePinnedColumn; x<=this.lastVisiblePinnedColumn;x++)\r\n"+
            "					this.removeCell(x,y);\r\n"+
            "				\r\n"+
            "		}\r\n"+
            "		verticalScroll=true;\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	//Scrolled up	\r\n"+
            "	if(this.lowerRow < oldLowerRow){\r\n"+
            "		var l = this.lowerRow >= oldUpperRow? this.lowerRow : oldUpperRow-1;\r\n"+
            "		for(var y=oldLowerRow; y > l; y--){\r\n"+
            "			this.data.remove(0, y);\r\n"+
            "			this.data.remove(1, y);\r\n"+
            "			for(var x=this.firstVisibleColumn;x<=this.lastVisibleColumn;x++)\r\n"+
            "				this.removeCell(x,y);\r\n"+
            "			if(isPinned === true)\r\n"+
            "				for(var x= this.firstVisiblePinnedColumn; x<=this.lastVisiblePinnedColumn;x++)\r\n"+
            "					this.removeCell(x,y);\r\n"+
            "		}\r\n"+
            "		verticalScroll=true;\r\n"+
            "	}\r\n"+
            "	// if window restore, we need to clear diagonal data (e.g. scroll up + scroll left)\r\n"+
            "	var end = verticalScroll ? this.totalRows - 1 : this.lowerRow;\r\n"+
            "	//Scrolled right\r\n"+
            "	if(this.firstVisibleColumn != null && oldLeftCol != null)\r\n"+
            "		if(this.firstVisibleColumn > oldLeftCol){\r\n"+
            "			var l = this.firstVisibleColumn <= oldRightCol? this.firstVisibleColumn : oldRightCol+1;\r\n"+
            "			for(var y=this.upperRow; y <= end; y++)\r\n"+
            "				for(var x=oldLeftCol;x<l;x++)\r\n"+
            "					this.removeCell(x,y);\r\n"+
            "		}\r\n"+
            "	\r\n"+
            "	//Scrolled left\r\n"+
            "	if(this.lastVisibleColumn != null && oldRightCol != null)\r\n"+
            "		if(this.lastVisibleColumn < oldRightCol){\r\n"+
            "			var l = this.lastVisibleColumn >= oldLeftCol? this.lastVisibleColumn : oldLeftCol-1;\r\n"+
            "			for(var y=this.upperRow; y <= end; y++)\r\n"+
            "				for(var x=oldRightCol;x>l;x--)\r\n"+
            "					this.removeCell(x,y);\r\n"+
            "		\r\n"+
            "		}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTable.prototype.flagRowsRepaint = false;\r\n"+
            "FastTable.prototype.repaint=function(flagFromServer){\r\n"+
            "	ttt= this;\r\n"+
            "	if(flagFromServer){\r\n"+
            "		\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		if(this.flagInitColumns == true){\r\n"+
            "//			err(\"init\");\r\n"+
            "			this.flagInitColumns = false;\r\n"+
            "		    this.updateVisibleColumns();\r\n"+
            "			this.updateVisibleRowsCount();\r\n"+
            "		    this.updateContainerElement();\r\n"+
            "		    this.updateScrollPaneElement();\r\n"+
            "		    this.updateTableElements();\r\n"+
            "		    this.updateHeaderElement();\r\n"+
            "		    this.updateScrollPaneSize();\r\n"+
            "		    this.updateHeaderScroll();\r\n"+
            "			this.setClipZone();\r\n"+
            "		    this.updateColumns();\r\n"+
            "			this.updateColumnStyles();\r\n"+
            "			this.updateQuickFilterStyles();\r\n"+
            "			this.updatePinnedBorder();\r\n"+
            "			this.updatePinnedColumns();\r\n"+
            "		}\r\n"+
            "		else if(this.flagOptionsSet == true){\r\n"+
            "//			err(\"setoptions\");\r\n"+
            "			this.updateColumnStyles();\r\n"+
            "			this.updateQuickFilterStyles()\r\n"+
            "			this.updateVisibleRowsCount();\r\n"+
            "		    this.updateContainerElement();\r\n"+
            "		    this.updateScrollPaneElement();\r\n"+
            "		    this.updateTableElements();\r\n"+
            "		    this.updateHeaderElement();\r\n"+
            "		    this.updateScrollPaneSize();\r\n"+
            "		    this.updateHeaderScroll();\r\n"+
            "			this.setClipZone();\r\n"+
            "		    this.updateColumns();\r\n"+
            "			this.updatePinnedBorder();\r\n"+
            "			this.updatePinnedColumns();\r\n"+
            "			this.updateCellsLocations();\r\n"+
            "			this.updateCellsStyles();\r\n"+
            "			this.flagOptionsSet = false;\r\n"+
            "		}\r\n"+
            "		else if(this.flagScrollMoved == true){\r\n"+
            "//			err(\"scrollmoved\");\r\n"+
            "			this.flagScrollMoved = false;\r\n"+
            "		    this.updateHeaderScroll();\r\n"+
            "			this.setClipZone();\r\n"+
            "			this.updateCellsLocations();\r\n"+
            "			\r\n"+
            "			if(this.scrollPane.hscrollChanged != 0){\r\n"+
            "				this.updateColumns();\r\n"+
            "				this.updateColumnStyles();\r\n"+
            "				this.updateQuickFilterStyles();\r\n"+
            "				this.updatePinnedBorder();\r\n"+
            "				this.updatePinnedColumns();\r\n"+
            "			}\r\n"+
            "			\r\n"+
            "		}\r\n"+
            "		else if(this.flagColumnsMoving == true){\r\n"+
            "//			err(\"colmove\");\r\n"+
            "			this.updateTableElements();\r\n"+
            "			this.updateHeaderElement();\r\n"+
            "			this.updateScrollPaneSize();\r\n"+
            "		    this.updateHeaderScroll();\r\n"+
            "    	\r\n"+
            "			this.setClipZone();\r\n"+
            "			this.updateColumns();\r\n"+
            "			this.updateCellsLocations();\r\n"+
            "			this.updatePinnedBorder();\r\n"+
            "			this.updatePinnedColumns();\r\n"+
            "			this.flagColumnsMoving = false;\r\n"+
            "		}\r\n"+
            "		else if(this.flagTotalSizeChanged == true){\r\n"+
            "//			err(\"totalSizeChanged\");\r\n"+
            "			this.flagTotalSizeChanged = false;\r\n"+
            "			this.updateVisibleRowsCount();\r\n"+
            "			this.updateHeaderElement();\r\n"+
            "			this.updateScrollPaneSize();\r\n"+
            "    		//\r\n"+
            "		    this.updateHeaderScroll();\r\n"+
            "			this.updateVisibleColumns();\r\n"+
            "			this.setClipZone();\r\n"+
            "			this.updateColumns();\r\n"+
            "			this.updateColumnStyles();\r\n"+
            "			this.updateQuickFilterStyles();\r\n"+
            "			this.updatePinnedBorder();\r\n"+
            "			this.updatePinnedColumns();\r\n"+
            "		}\r\n"+
            "		else if(this.flagSizeChanged == true){\r\n"+
            "//			err(\"sizechange\");\r\n"+
            "			this.flagSizeChanged = false;\r\n"+
            "			this.updateVisibleRowsCount();\r\n"+
            "			this.updateContainerElement();\r\n"+
            "			this.updateScrollPaneElement();\r\n"+
            "			this.updateTableElements();\r\n"+
            "			if(this.widthChanged)\r\n"+
            "				this.updateHeaderElement();\r\n"+
            "			//\r\n"+
            "		    this.updateHeaderScroll();\r\n"+
            "			this.setClipZone();\r\n"+
            "			this.updateColumns();\r\n"+
            "			this.updateColumnStyles();\r\n"+
            "			this.updateQuickFilterStyles();\r\n"+
            "			this.updatePinnedBorder();\r\n"+
            "			this.updatePinnedColumns();\r\n"+
            "			this.widthChanged = false;\r\n"+
            "			this.heightChanged = false;\r\n"+
            "		}\r\n"+
            "		else if(this.flagRowsRepaint == true){\r\n"+
            "//			err(\"repaintrows\");\r\n"+
            "			this.flagRowsRepaint = false;\r\n"+
            "			this.updateCellsStyles();\r\n"+
            "//			this.updateCellsStylesRow();\r\n"+
            "		}\r\n"+
            "		else if(this.flagSelectionChanged == true){\r\n"+
            "//			err(\"updatesel\");\r\n"+
            "			this.flagSelectionChanged = false;\r\n"+
            "			this.updateCellsStyles();\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "//	log(\"LOG# Table: \" + this.tableElement.childElementCount + \" Pinned Table: \" + this.pinnedTable.childElementCount);\r\n"+
            "}\r\n"+
            "FastTable.prototype.setData=function(uid,x,y,value,style){\r\n"+
            "    var isVisibleX = false;\r\n"+
            "    var isVisibleY = this.upperRow <= y && y <= this.lowerRow;\r\n"+
            "    if(this.firstVisiblePinnedColumn != null && this.lastVisiblePinnedColumn != null){\r\n"+
            "    	isVisibleX = this.firstVisiblePinnedColumn <= x && x <= this.lastVisiblePinnedColumn; \r\n"+
            "    }\r\n"+
            "   ");
          out.print(
            " if(this.firstVisibleColumn != null && this.lastVisibleColumn != null){\r\n"+
            "    	isVisibleX = isVisibleX || this.firstVisibleColumn <= x && x <= this.lastVisibleColumn; \r\n"+
            "    }\r\n"+
            "    var isVisible = isVisibleX && isVisibleY;\r\n"+
            "    \r\n"+
            "    if(!isVisible)\r\n"+
            "    	return;\r\n"+
            "	var cell=this.cellElements.get(x,y);\r\n"+
            "	if(cell==null)\r\n"+
            "		cell = this.nwCell(x,y);\r\n"+
            "    cell.style.display='inline-flex';\r\n"+
            "    this.applyCellSize(cell, x, y);\r\n"+
            "    this.applyCellLocation(cell, x, y);\r\n"+
            "    cell.uid=uid;\r\n"+
            "    //Format Value\r\n"+
            "    this.formatValue(cell, x, y, value, style);\r\n"+
            "    //Apply Style\r\n"+
            "    cell._style = style;\r\n"+
            "    // if update speed > flash speed, then at this point transition hasn't been nullify yet\r\n"+
            "    // in this case we need to force apply style so it honors the flashing colors\r\n"+
            "    this.applyStyle(cell,style,true);\r\n"+
            "    if(this.flashMs>0){\r\n"+
            "    	// Here is how the timeline for flashing work:\r\n"+
            "    	// if flashing ms set to 500ms, then we will set the bg color immediately and it will remain there for 500ms. Then for the next 500ms, it will fade back to the original bg color.\r\n"+
            "    	// So setting flashing to 500ms actually means 500ms * 2.\r\n"+
            "    	// 			0 ------------ 500ms ------------ 1s\r\n"+
            "    	//      set bg color     begin fade        fade complete\r\n"+
            "      var oldValue=this.valueElements.set(x,uid,value);\r\n"+
            "      // oldValue is undefined if this is an insert or the value was deleted and there are several updates to that value.\r\n"+
            "      if(oldValue && oldValue!=value){\r\n"+
            "    	  // in case the update speed > flashing duration, we need to remove the old timeout\r\n"+
            "        if(cell.flashTimeout!=null)\r\n"+
            "          clearTimeout(cell.flashTimeout);\r\n"+
            "        if (cell.resetTransition!=null)\r\n"+
            "        	clearTimeout(cell.resetTransition);\r\n"+
            "        var flashDir;\r\n"+
            "        if(this.flashUpColor==this.flashDnColor)\r\n"+
            "          flashDir=1;\r\n"+
            "        else{\r\n"+
            "          var n1=parseNumber(oldValue); // need to use locale specific parsing\r\n"+
            "          var n2=parseNumber(value);\r\n"+
            "          if(isNaN(n1) || isNaN(n2))\r\n"+
            "            flashDir=oldValue<value? 1 : 2;\r\n"+
            "          else\r\n"+
            "            flashDir=n1 < n2? 1 : 2;\r\n"+
            "        }\r\n"+
            "        var that=this;\r\n"+
            "        cell.style.backgroundColor=flashDir==1 ? this.flashUpColor : this.flashDnColor;\r\n"+
            "		var func=function(){\r\n"+
            "			cell.style.transition=that.flashStyle; 		\r\n"+
            "			cell.flashTimeout=null;\r\n"+
            "			// fade back to original bg color\r\n"+
            "			that.applyStyle(cell,style,true);\r\n"+
            "		};\r\n"+
            "		var func2=function(){\r\n"+
            "			cell.style.transition=''; 		\r\n"+
            "			cell.resetTransition=null;\r\n"+
            "			// no need to apply style here\r\n"+
            "		};\r\n"+
            "		// apply timeout\r\n"+
            "		cell.flashTimeout=window.setTimeout(func,this.flashMs);\r\n"+
            "		// reset the transition after flashing is over. This is to avoid triggering transition effect when changing styles\r\n"+
            "		cell.resetTransition=window.setTimeout(func2,this.flashMs*2);\r\n"+
            "     } else {\r\n"+
            "    	 // TODO: handle cases where prev value is undefined because we deleted it but we should show the flash color\r\n"+
            "     }\r\n"+
            "   }\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.formatValue=function(cell, x, y, value, style){\r\n"+
            "    var jsft=this.columns[x].jsFormatterType;\r\n"+
            "    if(jsft === \"spark_line\"){\r\n"+
            "    	k = cell;\r\n"+
            "    	if(cell.spark == null){\r\n"+
            "    		cell.spark = new InlineLineChart(cell, value, \",\");\r\n"+
            "    	}else{\r\n"+
            "    		cell.spark.setText(value,',');\r\n"+
            "    	}\r\n"+
            "   		cell.spark.drawSvg(cell.clientWidth- this.cellRightDivider, cell.clientHeight-this.cellBottomDivider);\r\n"+
            "    } else if (jsft === \"checkbox\") {\r\n"+
            "    	if(value !== \"N/A\" ){\r\n"+
            "	    	if (cell.checkbox == null) {\r\n"+
            "	    		cell.checkbox = new CheckboxField();\r\n"+
            "				cell.checkbox.element.onmousedown = function() {\r\n"+
            "					// do nothing\r\n"+
            "				}\r\n"+
            "	    	}\r\n"+
            "\r\n"+
            "	    	cell.innerHTML = \"\";	    \r\n"+
            "			this.applyCellCheckboxConfig(cell, value, style); \r\n"+
            "			 \r\n"+
            "		} else{\r\n"+
            "			cell.innerHTML=value;\r\n"+
            "    		cell.oldValue=value;\r\n"+
            "    		cell.checkbox = null;\r\n"+
            "		}  	\r\n"+
            "    } else{\r\n"+
            "    	cell.innerHTML=value;\r\n"+
            "    	cell.oldValue=value; \r\n"+
            "    }\r\n"+
            "}\r\n"+
            "FastTable.prototype.applyCellCheckboxConfig=function(cell, value, style) {\r\n"+
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
            "	cell.checkbox.setSize(cell.clientHeight - this.cellBottomDivider - 5, cell.clientHeight - this.cellBottomDivider - 5);\r\n"+
            "	cell.checkbox.element.style.margin = \"2px 0px 0px\";\r\n"+
            "	cell.appendChild(cell.checkbox.element);\r\n"+
            "}\r\n"+
            "FastTable.prototype.applyStyle=function(target,val,force){\r\n"+
            "	// if resetTransition is not null, then cell flashing is in progress. Stop any attempt to change the style during cell flashing.\r\n"+
            "	// fyi the transitioning effect happens if we change the cell bg color.\r\n"+
            "    if(!force && target.resetTransition!=null) {\r\n"+
            "        return;\r\n"+
            "    }\r\n"+
            "	target.className='';\r\n"+
            "	this.updateStyle(target);	\r\n"+
            "	if(val != null && \"\" != val && target.checkbox == null)\r\n"+
            "		applyStyle(target,val);\r\n"+
            "	if(target.cellClassName){\r\n"+
            "		target.className+=\" \"+target.cellClassName;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.setColsRowsCount=function(x,y){\r\n"+
            "  this.data.setSize(x+2,y);\r\n"+
            "  this.cellElements.ensureSize(x,y);\r\n"+
            "	this.setTotalRows(y);\r\n"+
            "  this.totalColumns = x;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.setTotalRows=function(rows){\r\n"+
            "  for(var y=this.totalRows-1; y >= rows; y--){\r\n"+
            "	 this.data.remove(0, y);\r\n"+
            "	 this.data.remove(1, y);\r\n"+
            "    for(var x=0;x<this.cellElements.getWidth();x++)\r\n"+
            "      if(this.cellElements.get(x,y) != null)\r\n"+
            "        this.removeCell(x,y);\r\n"+
            "  }\r\n"+
            "  this.totalRows = rows;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.setTotalWidthHeight=function(width,rows){\r\n"+
            "	this.setTotalRows(rows);\r\n"+
            "	this.totalWidth = width;\r\n"+
            "	this.totalHeight = rows * this.rowHeight;\r\n"+
            "	this.flagTotalSizeChanged = true;\r\n"+
            "	if(this.totalWidth <= this.width){\r\n"+
            "		this.scrollPane.setClipLeft(0);\r\n"+
            "	}\r\n"+
            "	this.repaint(false);\r\n"+
            "	var scrollSize = this.scrollPane.hscrollVisible ? this.scrollPane.scrollSize : 0;\r\n"+
            "	this.callback('tableSizeChanged',{\r\n"+
            "	  							left:this.scrollPane.getClipLeft(),\r\n"+
            "	  							top:this.scrollPane.getClipTop(),\r\n"+
            "	  							height:this.scrollPane.getClipHeight() - scrollSize,\r\n"+
            "	  							contentWidth:this.scrollPane.paneWidth,\r\n"+
            "	  							contentHeight:this.scrollPane.paneHeight\r\n"+
            "	  						   });\r\n"+
            "}\r\n"+
            "\r\n"+
            "//called strickly from backend\r\n"+
            "FastTable.prototype.setScroll=function(left,top,userSeqnum){\r\n"+
            "	if(this.userScrollSeqnum>userSeqnum)\r\n"+
            "		return;\r\n"+
            "	this.scrollPane.setClipLeft(left);\r\n"+
            "	this.scrollPane.setClipTop(top);\r\n"+
            "}\r\n"+
            "FastTable.prototype.getRowsCount = function(){\r\n"+
            "	return this.data.getHeight();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.onFilter=function(col){\r\n"+
            "	this.callback('openFilter', {col:col});\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.onScroll=function(){\r\n"+
            "  this.userScrollSeqnum++;\r\n"+
            "  var scrollSize = this.scrollPane.hscrollVisible ? this.scrollSize : 0;\r\n"+
            "  this.callback('tableScroll',{\r\n"+
            "	  							left:this.scrollPane.getClipLeft(),\r\n"+
            "	  							top:this.scrollPane.getClipTop(),\r\n"+
            "	  							userSeqnum:this.userScrollSeqnum,\r\n"+
            "	  							height:this.scrollPane.getClipHeight() - scrollSize,\r\n"+
            "	  							contentWidth:this.scrollPane.paneWidth,\r\n"+
            "	  							contentHeight:this.scrollPane.paneHeight\r\n"+
            "	  						   }\r\n"+
            "  				);\r\n"+
            "  this.flagScrollMoved = true;\r\n"+
            "  this.repaint(false);\r\n"+
            "  if(this.editListener != null && this.editListener.active == true){\r\n"+
            "	  this.editListener.repaint();\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "FastTable.prototype.updatePinnedColumns=function(){\r\n"+
            "	for(var i = this.firstVisibleColumn; i <= this.lastVisibleColumn; i++){\r\n"+
            "		if(i == null)\r\n"+
            "			break;\r\n"+
            "		var column = this.columns[i];\r\n"+
            "		column.headerElement.style.fontWeight='normal';\r\n"+
            "        applyStyle(column.headerElement,column.columnHeaderStyle);\r\n"+
            "		column.headerElement.style.zIndex='0';\r\n"+
            "		column.grabElement.style.zIndex='1';\r\n"+
            "	}\r\n"+
            "	for(var i = this.firstVisiblePinnedColumn; i <= this.lastVisiblePinnedColumn; i++){\r\n"+
            "		if(i == null)\r\n"+
            "			return;\r\n"+
            "		var column = this.columns[i];\r\n"+
            "        applyStyle(column.headerElement,column.columnHeaderStyle);\r\n"+
            "		column.headerElement.style.fontWeight='bold';\r\n"+
            "		column.headerElement.style.zIndex='1';\r\n"+
            "		column.grabElement.style.zIndex='2';\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.showContextMenu=function(menu){\r\n"+
            "   if(menu==null)\r\n"+
            "	   return;\r\n"+
            "   if(menu.curseqnum!=null && menu.curseqnum!=this.curseqnum)\r\n"+
            "	   return;\r\n"+
            "  this.createMenu(menu).show(this.contextMenuPoint);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTable.prototype.createMenu=function(menu){\r\n"+
            "   var that=this;\r\n"+
            "   var r=new Menu(getWindow(this.containerElement));\r\n"+
            "   if(this.contextMenuCurrentColumn == -1){\r\n"+
            "	   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e,id);} );\r\n"+
            "   }else if(this.contextMenuCurrentColumn != null){\r\n"+
            "	   r.createMenu(menu, function(e,id){that.onUserHeaderMenuItem(e,id,that.contextMenuCurrentColumn);} );\r\n"+
            "   }\r\n"+
            "   return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTable.prototype.showHelp=function(dom){\r\n"+
            "	var div=nw(div,'help_container');\r\n"+
            "	for(var i in dom){\r\n"+
            "		var group=dom[i];\r\n"+
            "		var titleDiv=nw('span','help_group');\r\n"+
            "		titleDiv.innerHTML=group.title;\r\n"+
            "		div.appendChild(titleDiv);\r\n"+
            "	    for(var j in group.items){\r\n"+
            "	    	var item=group.items[j];\r\n"+
            "	    	var entry=nw('span','help_entry');\r\n"+
            "	    	var title=nw('span','help_title');\r\n"+
            "	    	var delim=nw('span');\r\n"+
            "	    	delim.innerHTML=' - ';\r\n"+
            "	    	title.innerHTML=item.title;\r\n"+
            "	    	var text=nw('span','help_text');\r\n"+
            "	    	text.innerHTML=item.help;\r\n"+
            "	    	entry.appendChild(title);\r\n"+
            "	    	entry.appendChild(delim);\r\n"+
            "	    	entry.appendChil");
          out.print(
            "d(text);\r\n"+
            "	    	div.appendChild(entry);\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "	var d=new Dialog(div);\r\n"+
            "	d.setSize(500,500);\r\n"+
            "	d.setTitle(\"Help\");\r\n"+
            "	d.addButton(\"close\");\r\n"+
            "	d.setCanResize(true);\r\n"+
            "	d.show();\r\n"+
            "}\r\n"+
            "\r\n"+
            "function Object2d(){\r\n"+
            "	this.data=new Map();\r\n"+
            "}\r\n"+
            "Object2d.prototype.data=null;\r\n"+
            "Object2d.prototype.width=0;\r\n"+
            "Object2d.prototype.height=0;\r\n"+
            "\r\n"+
            "Object2d.prototype.getKey=function(col,row){\r\n"+
            "	return col*100000000+row;\r\n"+
            "}\r\n"+
            "Object2d.prototype.get=function(col,row){\r\n"+
            "	return this.data.get(this.getKey(col,row));\r\n"+
            "}\r\n"+
            "Object2d.prototype.set=function(col,row,val){\r\n"+
            "    var key=this.getKey(col,row);\r\n"+
            "	var curr = this.data.get(key);\r\n"+
            "	if(val==null)\r\n"+
            "		this.data.delete(key);\r\n"+
            "	else\r\n"+
            "		this.data.set(key,val);\r\n"+
            "	return curr;\r\n"+
            "}\r\n"+
            "Object2d.prototype.remove=function(col,row){\r\n"+
            "	this.data.delete(this.getKey(col,row));\r\n"+
            "}\r\n"+
            "Object2d.prototype.setSize=function(ncols,nrows){\r\n"+
            "	this.width=ncols;\r\n"+
            "	this.height=nrows;\r\n"+
            "}\r\n"+
            "Object2d.prototype.ensureSize=function(x,y){\r\n"+
            "	if(x+1 > this.width)\r\n"+
            "		this.setWidth(x+1);\r\n"+
            "	if(y+1 > this.height)\r\n"+
            "		this.setHeight(y+1);\r\n"+
            "}\r\n"+
            "Object2d.prototype.setWidth=function(w){\r\n"+
            "	this.width=w;\r\n"+
            "}\r\n"+
            "Object2d.prototype.setHeight=function(h){\r\n"+
            "	this.height=h;\r\n"+
            "}\r\n"+
            "Object2d.prototype.getWidth=function(){\r\n"+
            "	return this.width;\r\n"+
            "}\r\n"+
            "Object2d.prototype.getHeight=function(){\r\n"+
            "	return this.height;\r\n"+
            "}\r\n"+
            "Object2d.prototype.clear=function(){\r\n"+
            "	this.data.clear();\r\n"+
            "}\r\n"+
            "\r\n"+
            "function FastTableEditor(tablePortlet){\r\n"+
            "	this.tablePortlet = tablePortlet;\r\n"+
            "	this.tablePortlet.table.editListener = this;\r\n"+
            "	this.active = false;\r\n"+
            "	this.focusedEdit = null;\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.EDIT_DISABLED=0;\r\n"+
            "FastTableEditor.prototype.EDIT_READONLY = 1;\r\n"+
            "FastTableEditor.prototype.EDIT_TEXTFIELD = 2;\r\n"+
            "FastTableEditor.prototype.EDIT_SELECT = 3;\r\n"+
            "FastTableEditor.prototype.EDIT_COMBOBOX = 4;\r\n"+
            "FastTableEditor.prototype.EDIT_DATERANGE_FIELD = 5;\r\n"+
            "FastTableEditor.prototype.EDIT_DATE_FIELD = 6;\r\n"+
            "FastTableEditor.prototype.EDIT_CHECKBOX = 7;\r\n"+
            "FastTableEditor.prototype.EDIT_NUMBERIC = 8;\r\n"+
            "FastTableEditor.prototype.EDIT_MASKED = 9;\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.focusClosestCellToMouse=function(){\r\n"+
            "	var r = new Rect(MOUSE_POSITION_X, MOUSE_POSITION_Y,1,1);\r\n"+
            "	var t = new Rect().readFromElementRelatedToWindow(this.tablePortlet.table.tableElement);\r\n"+
            "	r.left-=t.getLeft();\r\n"+
            "	r.top-=t.getTop();\r\n"+
            "	\r\n"+
            "	var col = this.tablePortlet.table.calcColFromLeft(r.left);\r\n"+
            "	var row = this.tablePortlet.table.calcRowFromTop(r.top);\r\n"+
            "	this.focusEditCell(col,row);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.elementIsFullyVisibleX=function(cell){\r\n"+
            "	var col = cell.x;\r\n"+
            "	if(col < this.tablePortlet.table.pinning) {\r\n"+
            "		var x = this.tablePortlet.table.calcCellLeft(col);\r\n"+
            "		var w = this.tablePortlet.table.calcCellWidth(col);\r\n"+
            "		var vs = this.tablePortlet.table.scrollPane.vscrollVisible? this.tablePortlet.table.scrollSize:0;\r\n"+
            "		var tw = this.tablePortlet.table.width -w -vs;\r\n"+
            "		var pw = tw < pw? tw: this.tablePortlet.table.pinnedTable.offsetWidth;\r\n"+
            "		return (0 <= x && x < (pw))\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		var x = this.tablePortlet.table.calcCellLeft(col);\r\n"+
            "		var w = this.tablePortlet.table.calcCellWidth(col);\r\n"+
            "		var vs = this.tablePortlet.table.scrollPane.vscrollVisible? this.tablePortlet.table.scrollSize:0;\r\n"+
            "		return (0 <= x && x <= (this.tablePortlet.table.width - w - vs))\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.elementIsFullyVisibleY=function(cell){\r\n"+
            "	var row = cell.y;\r\n"+
            "	var y = this.tablePortlet.table.calcCellTop(row);\r\n"+
            "	var h = this.tablePortlet.table.calcCellHeight(row);\r\n"+
            "	var hs = this.tablePortlet.table.scrollPane.hscrollVisible? this.tablePortlet.table.scrollSize:0;\r\n"+
            "	var hh = parseInt(this.tablePortlet.table.headerHeight);\r\n"+
            "	return (0 <= y && y < (this.tablePortlet.table.height -h -hs -hh))\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.focusEditCell=function(col,row){\r\n"+
            "	if(typeof this.colsMap[col] == \"undefined\"){\r\n"+
            "		col = this.cols[0];\r\n"+
            "	}\r\n"+
            "	var element = this.cellElements.get(col,row);\r\n"+
            "	if(typeof element ==\"undefined\"){\r\n"+
            "		element = this.cellElements.get(this.cols[0],this.rows[0]);\r\n"+
            "	}\r\n"+
            "	if(typeof element !=\"undefined\"){\r\n"+
            "		if(this.focusedEdit)\r\n"+
            "			this.focusedEdit.blur();\r\n"+
            "		this.focusedEdit = element;\r\n"+
            "		\r\n"+
            "		var colVisible = this.elementIsFullyVisibleX(element);\r\n"+
            "		var rowVisible = this.elementIsFullyVisibleY(element);\r\n"+
            "			\r\n"+
            "		if(!colVisible || !rowVisible){\r\n"+
            "			var rowPos = element.y * parseInt(this.tablePortlet.table.rowHeight);\r\n"+
            "			this.tablePortlet.callBack(\"make_visible\",{c:element.x, r:element.y, rp:rowPos, snapCol:!colVisible, snapRow:!rowVisible});\r\n"+
            "		}\r\n"+
            "		else\r\n"+
            "			element.focus();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.edit=function(m){\r\n"+
            "	kmm.setActivePortletId(this.tablePortlet.portletId);\r\n"+
            "	if(m.length == 0)\r\n"+
            "		return;\r\n"+
            "	if(this.cols && this.rows && this.cols.length > 0 && this.rows.length > 0)\r\n"+
            "		this.deactivate();\r\n"+
            "	this.sortCells(m);\r\n"+
            "	this.cells = m;\r\n"+
            "	this.cellElements= new Object2d();\r\n"+
            "	this.valueElements= new Object2d();\r\n"+
            "	this.cols=[];\r\n"+
            "	this.rows=[];\r\n"+
            "	this.colsMap={};\r\n"+
            "	this.rowsMap={};\r\n"+
            "	this.createGlass();\r\n"+
            "	this.createDOM();\r\n"+
            "	this.createCellDOM();\r\n"+
            "	this.active = true;\r\n"+
            "	this.repaint();\r\n"+
            "	this.focusClosestCellToMouse();\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.sortCells=function(m){\r\n"+
            "	m.sort(function(a,b){ return a.x -b.x || a.y - b.y; });\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.removeElements=function(){\r\n"+
            "	for(var i = 0; i < this.cols.length; i++){\r\n"+
            "		var x = this.cols[i];\r\n"+
            "		for(var j = 0; j < this.rows.length; j++){\r\n"+
            "			var y = this.rows[j];\r\n"+
            "			var e = this.cellElements.get(x,y);\r\n"+
            "			e.parentElement.removeChild(e);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.deactivate=function(){\r\n"+
            "	if(this.active == false)\r\n"+
            "		return;\r\n"+
            "	this.hideGlass();\r\n"+
            "	this.tablePortlet.table.headerElement.style.pointerEvents=\"initial\";\r\n"+
            "	this.tablePortlet.headerDiv.style.pointerEvents=\"initial\";\r\n"+
            "	this.removeElements();\r\n"+
            "	this.cells = [];\r\n"+
            "	this.cols=[];\r\n"+
            "	this.rows=[];\r\n"+
            "	this.colsMap={};\r\n"+
            "	this.rowsMap={};\r\n"+
            "	this.cellElements.clear();\r\n"+
            "	this.cellElements = null;\r\n"+
            "	this.element.removeChild(this.movingElement);\r\n"+
            "	this.element.removeChild(this.pinnedElement);\r\n"+
            "	this.tablePortlet.table.tableElement.removeChild(this.element);\r\n"+
            "	this.focusedEdit = null;\r\n"+
            "	this.movingElement = null;\r\n"+
            "	this.pinnedElement = null;\r\n"+
            "	this.element = null;\r\n"+
            "	this.active=false;\r\n"+
            "	if(currentContextMenu)\r\n"+
            "		currentContextMenu.hideAll();\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.cancelEdit=function(){\r\n"+
            "	this.deactivate();\r\n"+
            "	this.tablePortlet.callBack(\"rows_edit_cancel\",{});\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.sendRows=function(){\r\n"+
            "	var c = [];\r\n"+
            "	for(var i = 0; i < this.cells.length; i++){\r\n"+
            "		c[i] = {};\r\n"+
            "		c[i].x = this.cells[i].x;\r\n"+
            "		c[i].y = this.cells[i].y;\r\n"+
            "		if(c[i].x >= 0)\r\n"+
            "			c[i].v = this.cellElements.get(this.cells[i].x, this.cells[i].y).value;\r\n"+
            "		else\r\n"+
            "			c[i].v = this.cells[i].v;\r\n"+
            "	}\r\n"+
            "	this.tablePortlet.callBack(\"rows_edit\", {cells:JSON.stringify(c), submit:true});\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.sendCell=function(ce){\r\n"+
            "	//ce cellElement\r\n"+
            "	var c = [{x:ce.x,y:ce.y,v:ce.value}];\r\n"+
            "	this.tablePortlet.callBack(\"rows_edit\", {cells:JSON.stringify(c)});\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.cellHandleKeydown=function(e){\r\n"+
            "	if(this.focusedEdit != null && this.focusedEdit.handleKeydown!=null){\r\n"+
            "		return this.focusedEdit.handleKeydown(e);\r\n"+
            "	}\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.handleKeydown=function(e, _target){\r\n"+
            "	var shiftKey = e.shiftKey;\r\n"+
            "	var ctrlKey = e.ctrlKey;\r\n"+
            "	var altKey = e.altKey;\r\n"+
            "	if(this.cellHandleKeydown(e)){\r\n"+
            "		\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		switch(e.key){	\r\n"+
            "			case \"Enter\":\r\n"+
            "				if(shiftKey == false && altKey == false && ctrlKey == false)\r\n"+
            "					this.sendRows();\r\n"+
            "				break;\r\n"+
            "			case \"Escape\":\r\n"+
            "				this.cancelEdit();\r\n"+
            "				break;\r\n"+
            "			case \"Tab\":\r\n"+
            "				this.handleTab(e, shiftKey);\r\n"+
            "				break;\r\n"+
            "			case \"ArrowLeft\":\r\n"+
            "				if(shiftKey)\r\n"+
            "					this.handleArrow(e,-1,0);\r\n"+
            "				break;\r\n"+
            "			case \"ArrowRight\":\r\n"+
            "				if(shiftKey)\r\n"+
            "					this.handleArrow(e,1,0);\r\n"+
            "				break;\r\n"+
            "			case \"ArrowUp\":\r\n"+
            "				if(shiftKey)\r\n"+
            "					this.handleArrow(e,0,-1);\r\n"+
            "				break;\r\n"+
            "			case \"ArrowDown\":\r\n"+
            "				if(shiftKey)\r\n"+
            "					this.handleArrow(e,0,1);\r\n"+
            "				break;\r\n"+
            "			default:\r\n"+
            "				break;	\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.handleArrow=function(e,xdir, ydir){\r\n"+
            "	e.preventDefault();\r\n"+
            "	e.stopPropagation();\r\n"+
            "	if(this.focusedEdit){\r\n"+
            "		var col = this.focusedEdit.x;\r\n"+
            "		var row = this.focusedEdit.y;\r\n"+
            "		if(xdir != 0){\r\n"+
            "			var cIndex = this.colsMap[col] + xdir;\r\n"+
            "			if(0 <=cIndex && cIndex < this.cols.length){\r\n"+
            "				col = this.cols[cIndex];\r\n"+
            "			}\r\n"+
            "			else return;\r\n"+
            "		}\r\n"+
            "		if(ydir != 0){\r\n"+
            "			var rIndex = this.rowsMap[row] + ydir;\r\n"+
            "			if(0 <=rIndex && rIndex < this.rows.length){\r\n"+
            "				row = this.rows[rIndex];\r\n"+
            "			}\r\n"+
            "			else return;\r\n"+
            "		}\r\n"+
            "		this.focusEditCell(col,row);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.handleTab=function(e, reverse){\r\n"+
            "	if(this.focusedEdit){\r\n"+
            "		this.focusNextEdit(this.focusedEdit.x,this.focusedEdit.y, true, reverse);\r\n"+
            "	}\r\n"+
            "	e.preventDefault();\r\n"+
            "	e.stopPropagation();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.focusNextEdit=function(x, y, horizFirst, reverse){\r\n"+
            "	var cIndex = this.colsMap[x];\r\n"+
            "	var rIndex = this.rowsMap[y];\r\n"+
            "	if(typeof cIndex ==\"undefined\" || typeof rIndex == \"undefined\"){\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	if(horizFirst == true){\r\n"+
            "		if(reverse == false){\r\n"+
            "			if(cIndex == (this.cols.length-1)){\r\n"+
            "				cIndex = 0;\r\n"+
            "				if(rIndex == (this.rows.length-1)){\r\n"+
            "					rIndex = 0;\r\n"+
            "				}\r\n"+
            "				else\r\n"+
            "					rIndex++;\r\n"+
            "			}\r\n"+
            "			else\r\n"+
            "				cIndex++;\r\n"+
            "		}\r\n"+
            "		else{\r\n"+
            "			if(cIndex == 0){\r\n"+
            "				cIndex = this.cols.length-1;\r\n"+
            "				if(rIndex == 0){\r\n"+
            "					rIndex = this.rows.length-1;\r\n"+
            "				}\r\n"+
            "				else\r\n"+
            "					rIndex--;\r\n"+
            "			}\r\n"+
            "			else\r\n"+
            "				cIndex--;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		//What key to use\r\n"+
            "	}\r\n"+
            "	this.focusEditCell(this.cols[cIndex],this.rows[rIndex]);\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.createCellDOM=function(){\r\n"+
            "	for(var i = 0; i < this.cells.length; i++){\r\n"+
            "		var cell = this.cells[i];\r\n"+
            "		if(cell.x < 0)\r\n"+
            "			continue;\r\n"+
            "		if(typeof this.colsMap[cell.x] == \"undefined\"){\r\n"+
            "			this.colsMap[cell.x]=this.cols.length;\r\n"+
            "			this.cols.push(cell.x);\r\n"+
            "		}\r\n"+
            "		if(typeof this.rowsMap[cell.y] == \"undefined\"){\r\n"+
            "			this.rowsMap[cell.y]=this.rows.length;\r\n"+
            "			this.rows.push(cell.y);\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		var element;\r\n"+
            "		var that = this;\r\n"+
            "		var changeFunc = function(){\r\n"+
            "			that.sendCell(this);\r\n"+
            "		};\r\n"+
            "//		if(cell.t == )\r\n"+
            "		if(cell.t == this.EDIT_DISABLED");
          out.print(
            "){\r\n"+
            "			//Disabled Do Nothing\r\n"+
            "		}\r\n"+
            "		else if(cell.t == this.EDIT_READONLY){\r\n"+
            "			//Readonly Do Nothing\r\n"+
            "		}\r\n"+
            "		else if(cell.t == this.EDIT_TEXTFIELD){\r\n"+
            "			//TextField\r\n"+
            "			element = nw(\"input\",\"editCell\");\r\n"+
            "			element.oninput=changeFunc;\r\n"+
            "		}\r\n"+
            "		else if(cell.t == this.EDIT_SELECT){\r\n"+
            "			//Select\r\n"+
            "			element = nw(\"select\",\"editCell\");\r\n"+
            "			element.onchange=changeFunc;\r\n"+
            "			var olist = cell.o;\r\n"+
            "			if(!olist.includes(cell.v))\r\n"+
            "				olist.unshift(cell.v);\r\n"+
            "//			olist.sort();\r\n"+
            "			for(var m = 0; m < olist.length; m++){\r\n"+
            "				var option = nw(\"option\");\r\n"+
            "				var orginalText = olist[m].replace(/^\\s+/, match => '&nbsp;'.repeat(match.length)).replace(/\\s+$/, match => '&nbsp;'.repeat(match.length))\r\n"+
            "				option.innerHTML = orginalText;\r\n"+
            "				element.options.add(option);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		else if(cell.t == this.EDIT_COMBOBOX){\r\n"+
            "			//Combo\r\n"+
            "			element = nw(\"combo-box\",\"editComboBox\");\r\n"+
            "			element.onchange=changeFunc;\r\n"+
            "			element.setMinWidth(125);\r\n"+
            "			var olist = cell.o;\r\n"+
            "			//Don't need to add current value;\r\n"+
            "//			if(!olist.includes(cell.v))\r\n"+
            "//				olist.push(cell.v);\r\n"+
            "//			olist.sort();\r\n"+
            "			for(var m = 0; m < olist.length; m++){\r\n"+
            "				element.addOption(olist[m]);\r\n"+
            "			}\r\n"+
            "			element.setValue(cell.v);\r\n"+
            "		}\r\n"+
            "		else if (cell.t == this.EDIT_CHECKBOX) {\r\n"+
            "			element = nw(\"div\", \"editCheckbox editCell\");\r\n"+
            "			var editableCheckbox = new CheckboxField();\r\n"+
            "			editableCheckbox.setValue(cell.v);\r\n"+
            "			element.checkboxField = editableCheckbox;\r\n"+
            "			var toggle = function(e) {\r\n"+
            "				var element = this.parentElement; \r\n"+
            "				var editableCheckbox = element.checkboxField;\r\n"+
            "				editableCheckbox.toggleChecked();\r\n"+
            "				element.value = editableCheckbox.getValue();\r\n"+
            "				this.value = element.value + \"\";\r\n"+
            "				this.x = cell.x;\r\n"+
            "				this.y = cell.y;\r\n"+
            "				that.sendCell(this);\r\n"+
            "			};\r\n"+
            "			editableCheckbox.element.onmousedown=toggle;\r\n"+
            "			var height = this.tablePortlet.table.calcCellHeight(cell.y);\r\n"+
            "			editableCheckbox.setSize(height - 5, height - 5);\r\n"+
            "			editableCheckbox.element.style.margin = \"1px 0 0\";\r\n"+
            "			element.appendChild(editableCheckbox.element);\r\n"+
            "		} else if (cell.t == this.EDIT_MASKED) {\r\n"+
            "			element = nw(\"input\", \"editMasked editCell\");\r\n"+
            "			element.setAttribute(\"type\", \"password\");\r\n"+
            "			element.oninput=changeFunc;\r\n"+
            "		}\r\n"+
            "		else if (cell.t == this.EDIT_DATE_FIELD) {\r\n"+
            "			element = nw(\"div\", \"editDate\");\r\n"+
            "			var chooser = new DateChooser(element, false);\r\n"+
            "			chooser.input.classList.add(\"date_edit\");\r\n"+
            "			chooser.setDisableFutureDays([cell.dfd,false]);\r\n"+
            "			chooser.setEnableLastNDays([cell.lnd,null]);\r\n"+
            "			// hack the default calendar styles in\r\n"+
            "			chooser.setHoverBgColor(\"#CCCCCC\");\r\n"+
            "			chooser.setColors(\"#a9a9a9\");\r\n"+
            "			chooser.setOnEdit(true);\r\n"+
            "			chooser.onClickDay=chooser.handleClickFromEditCell;\r\n"+
            "			\r\n"+
            "			chooser.onChange=changeFuncCustom;\r\n"+
            "			var changeFuncCustom=function(e){\r\n"+
            "				this.value = chooser.getValue();\r\n"+
            "				this.x = cell.x;\r\n"+
            "				this.y = cell.y; \r\n"+
            "				that.sendCell(this);\r\n"+
            "			};\r\n"+
            "			\r\n"+
            "			//double click on cell: grab the content from the underlying cell, TRY parsing the date and set the chooser input.\r\n"+
            "			try {\r\n"+
            "				chooser.setValue(cell.v);\r\n"+
            "			} catch (err) {\r\n"+
            "				// invalid cell value. expecting format: cell.v = \"20201205 - 20201210\"\r\n"+
            "			}\r\n"+
            "			// Enter Key\r\n"+
            "			chooser.hideGlass=function() {\r\n"+
            "				kmm.setActivePortletId(that.tablePortlet.portletId,false);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		else if (cell.t == this.EDIT_DATERANGE_FIELD) {\r\n"+
            "			element = nw(\"div\", \"editDateRange\");\r\n"+
            "			var chooser = new DateChooser(element, true);\r\n"+
            "			chooser.input2.style.display=\"none\";\r\n"+
            "			chooser.dash.style.display=\"none\";\r\n"+
            "			chooser.input.classList.add(\"daterange_edit\");\r\n"+
            "			// TODO ADD disable future day and enable last N day for end calendar too\r\n"+
            "			chooser.setDisableFutureDays([cell.dfd,false]);\r\n"+
            "			chooser.setEnableLastNDays([cell.lnd,null]);\r\n"+
            "			// hack the default calendar styles in\r\n"+
            "			chooser.setHoverBgColor(\"#CCCCCC\");\r\n"+
            "			chooser.setColors(\"#a9a9a9\");\r\n"+
            "			chooser.setOnEdit(true);\r\n"+
            "			chooser.onClickDay=chooser.handleClickFromEditCell;\r\n"+
            "			\r\n"+
            "			chooser.onChange=changeFuncCustom;\r\n"+
            "			var changeFuncCustom=function(e){\r\n"+
            "				this.value = chooser.getValue();\r\n"+
            "				this.x = cell.x;\r\n"+
            "				this.y = cell.y; \r\n"+
            "				that.sendCell(this);\r\n"+
            "			};\r\n"+
            "			\r\n"+
            "			//double click on cell: grab the content from the underlying cell, TRY parsing the dates and set the chooser input.\r\n"+
            "			try {\r\n"+
            "				var dates = cell.v.split(\"-\");\r\n"+
            "				chooser.setValue(dates[0].trim());\r\n"+
            "				chooser.setValue2(dates[1].trim());\r\n"+
            "				chooser.input.value = cell.v;\r\n"+
            "			} catch (err) {\r\n"+
            "				// invalid cell value. expecting format: cell.v = \"20201205 - 20201210\"\r\n"+
            "			}\r\n"+
            "			// Enter Key\r\n"+
            "			chooser.hideGlass=function() {\r\n"+
            "				kmm.setActivePortletId(that.tablePortlet.portletId,false);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		else{\r\n"+
            "			//TextField and NumericField\r\n"+
            "			element = nw(\"input\",\"editCell\");\r\n"+
            "			element.oninput=changeFunc;\r\n"+
            "		}\r\n"+
            "		element.value = cell.v;\r\n"+
            "		var cellClassName = this.tablePortlet.table.columns[cell.x].cellClassName;\r\n"+
            "		element.classList.add(cellClassName);\r\n"+
            "		element.tabIndex=-1;\r\n"+
            "		element.x = cell.x;\r\n"+
            "		element.y = cell.y;\r\n"+
            "		element.isPinned = cell.x < this.tablePortlet.table.pinning;\r\n"+
            "		this.cellElements.set(cell.x, cell.y, element);\r\n"+
            "		if(element.isPinned)\r\n"+
            "			this.pinnedElement.appendChild(element);\r\n"+
            "		else\r\n"+
            "			this.movingElement.appendChild(element);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.repaint=function(){\r\n"+
            "\r\n"+
            "	//Sets flag to say the focused cell wasn't in view\r\n"+
            "		\r\n"+
            "	//Set position\r\n"+
            "	for(var i = 0; i < this.cells.length; i++){\r\n"+
            "		var cell = this.cells[i];\r\n"+
            "		var element = this.cellElements.get(cell.x,cell.y);\r\n"+
            "		if(cell.x < 0)\r\n"+
            "			continue;\r\n"+
            "		if(this.cells[i].y < this.tablePortlet.table.upperRow  || this.cells[i].y > this.tablePortlet.table.lowerRow){\r\n"+
            "			element.style.display=\"none\";\r\n"+
            "			element.isVisible = false;\r\n"+
            "			continue;\r\n"+
            "		}\r\n"+
            "		if(cell.x >= this.tablePortlet.table.pinning){\r\n"+
            "			if(this.cells[i].x < this.tablePortlet.table.firstVisibleColumn || this.cells[i].x > this.tablePortlet.table.lastVisibleColumn){\r\n"+
            "				element.style.display=\"none\";\r\n"+
            "				element.isVisible = false;\r\n"+
            "				continue;\r\n"+
            "			}\r\n"+
            "		}else{\r\n"+
            "			if(this.cells[i].x < this.tablePortlet.table.firstVisiblePinnedColumn || this.cells[i].x > this.tablePortlet.table.lastVisiblePinnedColumn){\r\n"+
            "				element.style.display=\"none\";\r\n"+
            "				element.isVisible = false;\r\n"+
            "				continue;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		var left = this.tablePortlet.table.calcCellLeft(cell.x);\r\n"+
            "		var top = this.tablePortlet.table.calcCellTop(cell.y);\r\n"+
            "		var width = this.tablePortlet.table.calcCellWidth(cell.x);\r\n"+
            "		var height = this.tablePortlet.table.calcCellHeight(cell.y);\r\n"+
            "		if(cell.x >= this.tablePortlet.table.pinning){\r\n"+
            "			left -= this.tablePortlet.table.totalPinnedWidth;\r\n"+
            "		}\r\n"+
            "		element.isVisible = true;\r\n"+
            "		element.style.display = \"initial\";	\r\n"+
            "		element.style.left = toPx(left);\r\n"+
            "		element.style.top = toPx(top);\r\n"+
            "		element.style.width = toPx(width);\r\n"+
            "		element.style.height = toPx(height);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	if(this.focusedEdit && this.focusedEdit.isVisible){\r\n"+
            "		var colVisible = this.elementIsFullyVisibleX(this.focusedEdit);\r\n"+
            "		var rowVisible = this.elementIsFullyVisibleY(this.focusedEdit);\r\n"+
            "		if(colVisible && rowVisible){\r\n"+
            "			this.focusedEdit.focus();\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	//Not as important, apply alignment\r\n"+
            "	for(var i = 0; i < this.cells.length; i++){\r\n"+
            "		var cell = this.cells[i];\r\n"+
            "		if(cell.x < 0)\r\n"+
            "			continue;\r\n"+
            "		var element = this.cellElements.get(cell.x,cell.y);\r\n"+
            "		if(this.cells[i].y < this.tablePortlet.table.upperRow  || this.cells[i].y > this.tablePortlet.table.lowerRow){\r\n"+
            "			continue;\r\n"+
            "		}\r\n"+
            "		element.style.justifyContent=this.tablePortlet.table.verticalAlign;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastTableEditor.prototype.createGlass=function(){\r\n"+
            "	var that = this;\r\n"+
            "	this.disabledGlassDiv=nw('div', \"disable_glass_clear\");\r\n"+
            "	this.tablePortlet.table.scrollPaneElement.style.zIndex=10000;\r\n"+
            "	this.disabledGlassDiv.onclick = function(e){ \r\n"+
            "	    that.sendRows(); \r\n"+
            "	    that.disabledGlassDiv.style.pointerEvents='none';\r\n"+
            "	    var target=getDocument(e.target).elementFromPoint(e.clientX,e.clientY);\r\n"+
            "	    if(target!=null)\r\n"+
            "	      target.dispatchEvent(new MouseEvent(e.type,e));\r\n"+
            "	};\r\n"+
            "	this.tablePortlet.table.containerElement.appendChild(this.disabledGlassDiv);\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.hideGlass=function(){\r\n"+
            "	this.tablePortlet.table.containerElement.removeChild(this.disabledGlassDiv);\r\n"+
            "	this.disabledGlassDiv=null;\r\n"+
            "	this.tablePortlet.table.scrollPaneElement.style.zIndex=null;\r\n"+
            "}\r\n"+
            "FastTable.prototype.createGlass=function(){\r\n"+
            "	var that = this;\r\n"+
            "	this.disabledGlassDiv=nw('div', \"disable_glass_clear\");\r\n"+
            "	this.disabledGlassDiv.style.zIndex=10000\r\n"+
            "	this.scrollPaneElement.style.zIndex=10000;\r\n"+
            "	this.tableElement.appendChild(this.disabledGlassDiv);\r\n"+
            "}\r\n"+
            "FastTable.prototype.hideGlass=function(){\r\n"+
            "	var count = this.tableElement.childElementCount;\r\n"+
            "	const elementsToRemove = this.tableElement.querySelectorAll(\".disable_glass_clear\");\r\n"+
            "	elementsToRemove.forEach(element => {this.tableElement.removeChild(element);});\r\n"+
            "	this.disabledGlassDiv=null;\r\n"+
            "	this.scrollPaneElement.style.zIndex=null;\r\n"+
            "}\r\n"+
            "FastTableEditor.prototype.createDOM=function(){\r\n"+
            "	this.element = nw(\"div\",\"tableEditor\"); \r\n"+
            "	this.tablePortlet.table.tableElement.appendChild(this.element);\r\n"+
            "	this.movingElement = nw(\"div\", \"tableEditorMoving\");\r\n"+
            "	this.pinnedElement = nw(\"div\", \"tableEditorPinned\");\r\n"+
            "	this.element.appendChild(this.movingElement);\r\n"+
            "	this.element.appendChild(this.pinnedElement);\r\n"+
            "	this.movingElement.style.left=toPx(this.tablePortlet.table.totalPinnedWidth);\r\n"+
            "	this.movingElement.style.width=toPx(this.tablePortlet.table.width - this.tablePortlet.table.totalPinnedWidth);\r\n"+
            "	this.pinnedElement.style.width=toPx(this.tablePortlet.table.totalPinnedWidth);\r\n"+
            "	if(!(this.tablePortlet.table.pinning > 0)){\r\n"+
            "		this.pinnedElement.style.display=\"none\";\r\n"+
            "	}\r\n"+
            "	this.movingElement.style.top=\"0px\";\r\n"+
            "	this.pinnedElement.style.top=\"0px\";\r\n"+
            "	this.pinnedElement.style.left=\"0px\";\r\n"+
            "	\r\n"+
            "	this.movingElement.tabIndex=-1;\r\n"+
            "	this.pinnedElement.tabIndex=-1;\r\n"+
            "	this.element.tabIndex=-1;\r\n"+
            "	\r\n"+
            "	this.tablePortlet.table.headerElement.style.pointerEvents=\"none\";\r\n"+
            "	this.tablePortlet.headerDiv.style.pointerEvents=\"none\";\r\n"+
            "	var that = this;\r\n"+
            "//	this.element.onclick = fun");
          out.print(
            "ction(e){ if(e.target== that.element){that.focusedEdit=null;that.sendRows();} else if(e.target.isVisible == true) that.focusedEdit = e.target};\r\n"+
            "	this.movingElement.onclick = function(e){ if(e.target== that.movingElement){that.focusedEdit=null;that.sendRows();} else if(e.target.isVisible == true) that.focusedEdit = e.target};\r\n"+
            "	this.pinnedElement.onclick = function(e){ if(e.target== that.pinnedElement){that.focusedEdit=null;that.sendRows();} else if(e.target.isVisible == true) that.focusedEdit = e.target};\r\n"+
            "}\r\n"+
            "");

	}
	
}