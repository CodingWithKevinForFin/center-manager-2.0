package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_menu_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_menu_js_1() {
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
            "function SelectedRows(){\r\n"+
            "    this.ranges=new Array();\r\n"+
            "    this.rows=new Array();\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectedRows.prototype.isEmpty=function(){\r\n"+
            "    return this.ranges.length==0 && this.rows.length==0;\r\n"+
            "}\r\n"+
            "SelectedRows.prototype.isSelected=function(rownum){\r\n"+
            "    if(this.rows[rownum]!=null)\r\n"+
            "	return true;\r\n"+
            "    for(var i=0;i<this.ranges.length;i++)\r\n"+
            "	if(this.ranges[i][0]<=rownum && rownum<=this.ranges[i][1]) return true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectedRows.prototype.add=function(start,end){\r\n"+
            "    if(end==null)\r\n"+
            "	end=start;\r\n"+
            "    else if(end<start){\r\n"+
            "	var t=end;\r\n"+
            "	end=start;\r\n"+
            "	start=t;\r\n"+
            "    }\r\n"+
            "    this.remove(start,end);\r\n"+
            "    if(end-start<2){\r\n"+
            "	while(start<=end) {\r\n"+
            "	    this.rows[start++]=true;\r\n"+
            "	}\r\n"+
            "    } else {\r\n"+
            "	this.ranges.push([start,end]);\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectedRows.prototype.remove=function(start,end){\r\n"+
            "    if(end==null)\r\n"+
            "	end=start;\r\n"+
            "    if(end-start < 100){\r\n"+
            "	for(var i=start;i<=end;i++) {\r\n"+
            "	    delete this.rows[i];\r\n"+
            "	}\r\n"+
            "    }else{\r\n"+
            "	for(var i in this.rows){\r\n"+
            "	    if(i>end)\r\n"+
            "		break;\r\n"+
            "	    if(i>=start) {\r\n"+
            "		delete this.rows[i];\r\n"+
            "	    }\r\n"+
            "	}	\r\n"+
            "    }\r\n"+
            "    for(var i=0;i<this.ranges.length;i++){\r\n"+
            "	var e=this.ranges[i];\r\n"+
            "	var low=e[0];\r\n"+
            "	var high=e[1];\r\n"+
            "	if(high <start || low > end)\r\n"+
            "	    continue;\r\n"+
            "	if(start<=low && end >=high){// outside\r\n"+
            "	    this.ranges.splice(i,1);\r\n"+
            "	    i--;\r\n"+
            "	}else if(start>low && end<high){// inside\r\n"+
            "	    e[1]=start-1;\r\n"+
            "	    this.ranges[this.ranges.length]=[end+1,high];\r\n"+
            "	}else if(start>low){// begins with\r\n"+
            "	    e[1]=start-1;\r\n"+
            "	}else{// ends with\r\n"+
            "	    e[0]=end+1;\r\n"+
            "	}\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "SelectedRows.prototype.toString=function(){\r\n"+
            "    r=\"\";\r\n"+
            "    for(var i in this.rows){\r\n"+
            "	if(r.length)\r\n"+
            "	    r+=\",\";\r\n"+
            "	r+=i;\r\n"+
            "    }\r\n"+
            "    for(var i in this.ranges){\r\n"+
            "	if(r.length)\r\n"+
            "	    r+=\",\";\r\n"+
            "	var b=this.ranges[i][0];\r\n"+
            "	var e=this.ranges[i][1];\r\n"+
            "	if(b==e)\r\n"+
            "	    r+=b;\r\n"+
            "	else\r\n"+
            "	    r+=b+'-'+e;\r\n"+
            "    }\r\n"+
            "    return r;\r\n"+
            "}\r\n"+
            "SelectedRows.prototype.parseString=function(string){\r\n"+
            "    this.clear();\r\n"+
            "    if(string==null || string==\"\")\r\n"+
            "	return;\r\n"+
            "    var parts=string.split(\",\");\r\n"+
            "    for(var i in parts){\r\n"+
            "	var startEnd=parts[i].split(\"-\");\r\n"+
            "	if(startEnd.length==1)\r\n"+
            "	    this.rows[parseInt(startEnd[0])]=true;\r\n"+
            "	else {\r\n"+
            "	    this.ranges.push([parseInt(startEnd[0]),parseInt(startEnd[1])]);\r\n"+
            "	}\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "SelectedRows.prototype.clear=function(){\r\n"+
            "    this.ranges=[];\r\n"+
            "    this.rows=[];\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "//################\r\n"+
            "//##### Menu #####\r\n"+
            "\r\n"+
            "\r\n"+
            "function Menu(window){\r\n"+
            "    var that=this;\r\n"+
            "    this.window=window;\r\n"+
            "    this.divElement=nw(\"div\");\r\n"+
            "    this.tableElement=nw(\"table\");\r\n"+
            "    this.selectedRows = new SelectedRows();\r\n"+
            "    this.divElement.className='menu';\r\n"+
            "    this.divElement.appendChild(this.tableElement);\r\n"+
            "    this.disabled=false;\r\n"+
            "    this.visible=false;\r\n"+
            "    this.menuItemUndoHoverStyle = {};\r\n"+
            "    //this.divElement.onmouseout=function(e){that.onMouseOut(e);};\r\n"+
            "    this.tableElement.ontouchstart=function(e) { that.onTouchDragStart(e);};\r\n"+
            "	this.tableElement.ontouchmove=function(e) { that.onTouchDragMove(e);}; \r\n"+
            "    this.childMenus=new Map();\r\n"+
            " } \r\n"+
            "\r\n"+
            "var currentContextMenu=null;\r\n"+
            "Menu.prototype.divElement;\r\n"+
            "Menu.prototype.parent;\r\n"+
            "Menu.prototype.tableElement;\r\n"+
            "Menu.prototype.visible;\r\n"+
            "Menu.prototype.selected;\r\n"+
            "Menu.prototype.selectedBy;\r\n"+
            "Menu.prototype.selectedRows;\r\n"+
            "Menu.prototype.childMenus;\r\n"+
            "Menu.prototype.openMenuInterval;\r\n"+
            "Menu.prototype.runFirstItemOnEnter;\r\n"+
            "\r\n"+
            "\r\n"+
            "//Gets the index of a menuItem\r\n"+
            "Menu.prototype.getIndex=function(tr){\r\n"+
            "	return [].indexOf.call(this.tableElement.children, tr);\r\n"+
            "}\r\n"+
            "\r\n"+
            "//Hide all menus in menu chain\r\n"+
            "Menu.prototype.hideAll=function(dontFire){\r\n"+
            "	if(this.parent)\r\n"+
            "		this.parent.hideAll(dontFire);\r\n"+
            "	else{\r\n"+
            "		this.hide(dontFire);\r\n"+
            "		currentContextMenu = null;\r\n"+
            "	}\r\n"+
            "	this.onClose();\r\n"+
            "}\r\n"+
            "\r\n"+
            "//Hide submenu\r\n"+
            "Menu.prototype.hideChildren=function(){\r\n"+
            "  if(this.activeChild!=null){\r\n"+
            "	  this.activeChild.hide();\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.getChildMenu=function(id){\r\n"+
            "	if(this.tableElement == null)\r\n"+
            "		return null;\r\n"+
            "	if(this.tableElement.children == null)\r\n"+
            "		return null;\r\n"+
            "	\r\n"+
            "	return this.tableElement.children[id];\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.getSelectedMenu=function(){\r\n"+
            "	return this.getChildMenu(this.selected);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Menu.prototype.setCssStyle=function(cssStyle){\r\n"+
            "	applyStyle(this.divElement,cssStyle);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.setSize=function(width, height){\r\n"+
            "	this.divElement.style.width=toPx(width);\r\n"+
            "//	this.divElement.style.maxHeight=toPx(height);\r\n"+
            "	this.tableElement.style.width=\"100%\";\r\n"+
            "}\r\n"+
            "//Hide the menu and it's children.\r\n"+
            "Menu.prototype.hide=function(dontFire){\r\n"+
            "if(!this.visible)\r\n"+
            "return;\r\n"+
            "this.visible=false;\r\n"+
            "\r\n"+
            "var targetLoc = this.window.document.body;\r\n"+
            "if(this.targetLoc != null)\r\n"+
            "	  targetLoc = this.targetLoc;\r\n"+
            "	  \r\n"+
            "targetLoc.removeChild(this.divElement);\r\n"+
            "if(this.parent!=null && this.parent.activeChild==this)\r\n"+
            "this.parent.activeChild=null;\r\n"+
            "this.hideChildren();\r\n"+
            "\r\n"+
            "if(this.onHide && (dontFire == false || dontFire == null))\r\n"+
            "	this.onHide(this);\r\n"+
            "\r\n"+
            "if(this.glass!=null){\r\n"+
            "	targetLoc.removeChild(this.glass);\r\n"+
            "	this.glass=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "var menuItems = this.tableElement.children;\r\n"+
            "if(this.selected != null)\r\n"+
            "	  this.highlight(menuItems[this.selected], false);		\r\n"+
            "this.selected = null;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Menu.prototype.deselect=function(){\r\n"+
            "	if(this.selected != null){\r\n"+
            "		this.highlight(this.tableElement.children[this.selected], false);\r\n"+
            "		this.selected = null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.getTextDivFromRow=function(row) {\r\n"+
            "	var children = row.children;\r\n"+
            "	for (var i = 0; i < children.length; i++) {\r\n"+
            "		if (children[i].classList.contains(\"menu_item_text\"))\r\n"+
            "			return children[i];\r\n"+
            "	} \r\n"+
            "	return null;\r\n"+
            "}\r\n"+
            "Menu.prototype.getRightArrowFromRow=function(row) {\r\n"+
            "	var children = row.children;\r\n"+
            "	for (var i = 0; i < children.length; i++) {\r\n"+
            "		if (children[i].classList.contains(\"menu_right_arrow\"))\r\n"+
            "			return children[i];\r\n"+
            "	} \r\n"+
            "	return null;\r\n"+
            "}\r\n"+
            "Menu.prototype.highlight=function(row, onOff){\r\n"+
            "	if(row.firstChild){\r\n"+
            "		var rightArrow = this.getRightArrowFromRow(row);\r\n"+
            "		if(onOff){\r\n"+
            "			row.style.background = this.hoverBgCl;\r\n"+
            "			row.style.color= this.hoverFontCl;\r\n"+
            "			if (rightArrow != null)\r\n"+
            "				rightArrow.style.color= this.hoverFontCl;\r\n"+
            "		}else{\r\n"+
            "			row.style.background = this.menuItemUndoHoverStyle.bgCl == null ? \"\" : this.menuItemUndoHoverStyle.bgCl;\r\n"+
            "			row.style.color= this.menuItemUndoHoverStyle.fontCl == null ? \"\" : this.menuItemUndoHoverStyle.fontCl;\r\n"+
            "			if (rightArrow != null)\r\n"+
            "				rightArrow.style.color= this.menuItemUndoHoverStyle.fontCl == null ? \"\" : this.menuItemUndoHoverStyle.fontCl;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Menu.prototype.isEnabledMenuItem=function(row){\r\n"+
            "	return !row.classList.contains(\"menu_divider\") && !row.classList.contains(\"disabled\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.navigateMenu=function(key){\r\n"+
            "	var menuItems = this.tableElement.children;\r\n"+
            "	if(menuItems.length > 0){\r\n"+
            "		var oldSelected = this.selected;\r\n"+
            "		if(this.selected == null)\r\n"+
            "			this.selected = 0;\r\n"+
            "		this.highlight(menuItems[this.selected], false);\r\n"+
            "		\r\n"+
            "		var loops = 0;\r\n"+
            "		while((this.selected == oldSelected || !this.isEnabledMenuItem(menuItems[this.selected])) && loops < menuItems.length){\r\n"+
            "			if(key === \"ArrowDown\"){\r\n"+
            "				if(!this.scrollPane){\r\n"+
            "					// at the bottom of the suggestion\r\n"+
            "					if(this.selected == (menuItems.length - 1) || this.selected == (this.totalSuggestionSize - 1))\r\n"+
            "					this.selected = 0;\r\n"+
            "					else\r\n"+
            "						this.selected++;\r\n"+
            "				}else{\r\n"+
            "					if(this.selected == (menuItems.length - 1)){\r\n"+
            "						this.onKeydownCausingBoundsChange();\r\n"+
            "						break;\r\n"+
            "					}else\r\n"+
            "					this.selected++;\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "			else if (key === \"ArrowUp\"){\r\n"+
            "				if(!this.scrollPane){\r\n"+
            "					// at the top of the suggestion\r\n"+
            "					if(this.selected == 0)\r\n"+
            "						this.selected = this.totalSuggestionSize ? this.totalSuggestionSize - 1 : menuItems.length - 1;\r\n"+
            "					else\r\n"+
            "						this.selected--;\r\n"+
            "				}else{\r\n"+
            "				if(this.selected == 0){\r\n"+
            "						this.onKeyupCausingBoundsChange();\r\n"+
            "						break;\r\n"+
            "					}else\r\n"+
            "					this.selected--;\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "			loops++;\r\n"+
            "		}\r\n"+
            "		this.highlight(menuItems[this.selected], true);		\r\n"+
            "		this.ensureVisibleMenuItem(menuItems[this.selected]);\r\n"+
            "		this.selectedBy=\"KEYBOARD\";\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.selected = null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Menu.prototype.onKeydownCausingBoundsChange= function(){\r\n"+
            "	//ensure index in bounds. If the lowerRow exceeds totalSuggSize, loop back to the beginning \r\n"+
            "	var endOfSuggestion=false;\r\n"+
            "	if(this.lowerRow +1 >= this.extension.totalSuggestionSize){\r\n"+
            "		//update scrollbar position, set clip top to 0(all the way top)\r\n"+
            "		this.scrollPane.setClipTopNoFire(0);\r\n"+
            "		this.upperRow = 0;\r\n"+
            "		this.lowerRow = this.extension.visibleSugLen - 1 ;\r\n"+
            "		endOfSuggestion = true;\r\n"+
            "		this.extension.field.onExtensionCallback(0, \"onBoundsChange\",{top:this.scrollPane.getClipTop(),\r\n"+
            "			userSeqnum:this.userSeqnum,\r\n"+
            "			s:this.upperRow,\r\n"+
            "			e:this.lowerRow,\r\n"+
            "			updateSelection:true,\r\n"+
            "			startOrEndOfSuggestion:2});	\r\n"+
            "	}else{\r\n"+
            "		this.updateScrollbarPosition(true);\r\n"+
            "		this.upperRow++;\r\n"+
            "		this.lowerRow++;\r\n"+
            "		this.extension.field.onExtensionCallback(0, \"onBoundsChange\",{top:this.scrollPane.getClipTop(),\r\n"+
            "			userSeqnum:this.userSeqnum,\r\n"+
            "			s:this.upperRow,\r\n"+
            "			e:this.lowerRow,\r\n"+
            "			updateSelection:true,\r\n"+
            "			startOrEndOfSuggestion:3});	\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.onKeyupCausingBoundsChange=function(){\r\n"+
            "	var startOfSuggestion=false;\r\n"+
            "	if(this.upperRow == 0 ){\r\n"+
            "		this.scrollPane.setClipTopNoFire(this.scrollPane.paneHeight - this.rowHeight);\r\n"+
            "		this.upperRow = this.extension.totalSuggestionSize - this.extension.visibleSugLen;\r\n"+
            "		this.lowerRow = this.extension.totalSuggestionSize - 1 ;\r\n"+
            "		startOfSuggestion = true;\r\n"+
            "		this.extension.field.onExtensionCallback(0, \"onBoundsChange\",{top:this.scrollPane.getClipTop(),\r\n"+
            "			userSeqnum:this.userSeqnum,\r\n"+
            "			s:this.upperRow,\r\n"+
            "			e:this.lowerRow,\r\n"+
            "			updateSelection:true,\r\n"+
            "			startOrEndOfSuggestion:1});\r\n"+
            "	}else{\r\n"+
            "		this.updateScrollbarPosition(false);\r\n"+
            "		this.upperRow--;\r\n"+
            "		this.lowerRow--;\r\n"+
            "		this.extension.field.onExtensionCallback(0, \"onBoundsChange\",{top:this.scrollPane.getClipTop(),\r\n"+
            "			userSeqnum:this.userSeqnum,\r\n"+
            "			s:this.upperRow,\r\n"+
            "			e:this.lowerRow,\r\n"+
            "			updateSelection:true,\r\n"+
            "			startOrEndOfSuggestion:3});\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "//this is no fire\r\n"+
            "Menu.prototype.updateScrollbarPosition=function(isScrollDown){\r\n"+
            "	//update scrollbar position\r\n"+
            "	v");
          out.print(
            "ar pageTop = this.scrollPane.getClipTop();\r\n"+
            "	var pageHeight = this.scrollPane.getClipHeight();\r\n"+
            "	if(isScrollDown){\r\n"+
            "		var rowTop = this.rowHeight * (this.lowerRow+1);//(index+1)th row\r\n"+
            "		var rowBot = this.rowHeight * (this.lowerRow+2);\r\n"+
            "	}else{\r\n"+
            "		var rowTop = this.rowHeight * (this.upperRow-1);//(index+1)th row\r\n"+
            "		var rowBot = this.rowHeight * (this.upperRow);\r\n"+
            "	}\r\n"+
            "	  var pos = null;\r\n"+
            "	  // Ensure the bottom of the row is above the bottom of the page\r\n"+
            "	  if(pageTop + pageHeight < rowBot){ \r\n"+
            "		  pos = rowBot - pageHeight;\r\n"+
            "	  }\r\n"+
            "	  // Ensure the top of the row is below the top of the page\r\n"+
            "	  if(pageTop > rowTop) {\r\n"+
            "		  pos = rowTop;\r\n"+
            "	  }\r\n"+
            "	  if(pos != null)\r\n"+
            "		  this.scrollPane.setClipTopNoFire(pos);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.ensureVisibleMenuItem=function(menuItem){\r\n"+
            "	var menuItemRect = menuItem.getBoundingClientRect();\r\n"+
            "	var menuRect = this.divElement.getBoundingClientRect();\r\n"+
            "	\r\n"+
            "	var menuItemTop = menuItemRect[\"top\"];\r\n"+
            "	var menuTop = menuRect[\"top\"];\r\n"+
            "	\r\n"+
            "	var menuItemBot = menuItemRect[\"bottom\"];\r\n"+
            "	var menuBot = menuRect[\"bottom\"];\r\n"+
            "	\r\n"+
            "	if(menuTop > menuItemTop){\r\n"+
            "		this.divElement.scrollTop -= (menuTop - menuItemTop);\r\n"+
            "	}\r\n"+
            "	else if(menuItemBot > menuBot){\r\n"+
            "		this.divElement.scrollTop += (menuItemBot - menuBot);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.navigateCloseSubMenu=function(){\r\n"+
            "	if(this.parent){\r\n"+
            "		this.hide();\r\n"+
            "		currentContextMenu=this.parent;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.navigateOpenSubMenu=function(){\r\n"+
            "	var selectedRow  = this.tableElement.children[this.selected];\r\n"+
            "	if(selectedRow){\r\n"+
            "		var r=new Rect();\r\n"+
            "		r.readFromElement(selectedRow);\r\n"+
            "		var childMenu = this.childMenus.get(this.getIndex(selectedRow));\r\n"+
            "		if(childMenu && childMenu.tableElement.childNodes.length > 0){\r\n"+
            "			var widthParentAndChildMenu = r.getRight();\r\n"+
            "			var widthParentWithOverflow = r.width;\r\n"+
            "			if (widthParentWithOverflow > 600)\r\n"+
            "				childMenu.show(new Point(widthParentAndChildMenu - (widthParentWithOverflow - 600), r.getTop()));\r\n"+
            "			else\r\n"+
            "				childMenu.show(new Point(r.getRight(),r.getTop()));\r\n"+
            "		} \r\n"+
            "		return childMenu;\r\n"+
            "	}\r\n"+
            "	return null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.runMenuItemAction=function(e){\r\n"+
            "	var selectedRow = this.tableElement.children[this.selected];\r\n"+
            "	if(selectedRow){\r\n"+
            "		selectedRow.onclick(e);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.runFirstMenuItemAction=function(e){\r\n"+
            "	this.navigateMenu(\"ArrowDown\");\r\n"+
            "	if(this.selected != null)\r\n"+
            "		this.runMenuItemAction(e);\r\n"+
            "}\r\n"+
            "Menu.prototype.handleKeypress=function(e){\r\n"+
            "	var stopPropagation = true;\r\n"+
            "	switch(e.key){\r\n"+
            "	default:\r\n"+
            "	    for(var i=0;i<this.tableElement.children.length;i++){\r\n"+
            "	    	var child=this.tableElement.children[i];\r\n"+
            "	    	if(child.keystroke==e.key){\r\n"+
            "	    		child.onclick(e);\r\n"+
            "			    stopPropagation = true;\r\n"+
            "	    	}\r\n"+
            "	    }\r\n"+
            "		if(!e.ctrlKey && !e.altKey && e.key.match(/^[a-z0-9_\\.]$/i) != null){\r\n"+
            "			stopPropagation = false;\r\n"+
            "		}\r\n"+
            "		break;\r\n"+
            "	}\r\n"+
            "	if (stopPropagation){\r\n"+
            "		e.stopPropagation();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.customHandleKeydown = null;\r\n"+
            "Menu.prototype.setCustomKeydownHandler = function(handler){\r\n"+
            "	this.customHandleKeydown = handler;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.handleKeydown=function(e){\r\n"+
            "	var stopPropagation = true;\r\n"+
            "	switch(e.key){\r\n"+
            "	case \"Enter\":\r\n"+
            "		if(this.childMenus.get(this.selected) == null){\r\n"+
            "			if(this.selected != null)\r\n"+
            "				this.runMenuItemAction(e);\r\n"+
            "			else if(this.runFirstItemOnEnter==true)\r\n"+
            "				this.runFirstMenuItemAction(e);\r\n"+
            "		}\r\n"+
            "		else{\r\n"+
            "			var child = this.navigateOpenSubMenu();\r\n"+
            "			if(child)\r\n"+
            "				child.navigateMenu(\"ArrowDown\");\r\n"+
            "		}\r\n"+
            "		e.preventDefault();\r\n"+
            "		break;\r\n"+
            "	case \"Escape\":\r\n"+
            "		this.hideAll();\r\n"+
            "		break;\r\n"+
            "	case \"ArrowDown\":\r\n"+
            "		this.navigateMenu(e.key);\r\n"+
            "		break;\r\n"+
            "	case \"ArrowUp\":\r\n"+
            "		this.navigateMenu(e.key);\r\n"+
            "		break;\r\n"+
            "	case \"ArrowLeft\":\r\n"+
            "		this.navigateCloseSubMenu();\r\n"+
            "		break;\r\n"+
            "	case \"ArrowRight\":\r\n"+
            "		var child = this.navigateOpenSubMenu();\r\n"+
            "		if(child)\r\n"+
            "			child.navigateMenu(\"ArrowDown\");\r\n"+
            "		break;\r\n"+
            "	case \"Delete\":\r\n"+
            "		stopPropagation = false;\r\n"+
            "		break;\r\n"+
            "	case \"Backspace\":\r\n"+
            "		stopPropagation = false;\r\n"+
            "		break;\r\n"+
            "	case \" \":\r\n"+
            "		stopPropagation = false;\r\n"+
            "		break;\r\n"+
            "	default:\r\n"+
            "		if(!e.ctrlKey && !e.altKey && e.key.match(/^[a-z0-9_\\.]$/i) != null){\r\n"+
            "			stopPropagation = false;\r\n"+
            "		}\r\n"+
            "		break;\r\n"+
            "	}\r\n"+
            "	if (stopPropagation){\r\n"+
            "		e.stopPropagation();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.handleWheel=function(e){\r\n"+
            "	this.divElement.scrollTop += e.deltaY;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.handleMousemove=function(e){\r\n"+
            "	var target;\r\n"+
            "//	if(this.openMenuInterval != null){\r\n"+
            "//		clearInterval(this.openMenuInterval);\r\n"+
            "//		this.openMenuInterval = null;\r\n"+
            "//	}\r\n"+
            "	if(e.target.tagName === \"TD\"){\r\n"+
            "		target = e.target.parentElement;\r\n"+
            "	}else{ target = e.target;}\r\n"+
            "	var classes = target.classList;\r\n"+
            "	if(classes.contains(\"menu_item\") && !classes.contains(\"disabled\")){\r\n"+
            "		var targetMenu = this;\r\n"+
            "		var menuItems = this.tableElement.children;\r\n"+
            "		var index = -1;\r\n"+
            "		while(index == -1 && targetMenu){\r\n"+
            "			index = getIndexOf(menuItems, e.target.parentElement, 0);\r\n"+
            "			if(index > -1)\r\n"+
            "				break;\r\n"+
            "			else{\r\n"+
            "				targetMenu = targetMenu.parent;\r\n"+
            "				menuItems = targetMenu ? targetMenu.tableElement.children :null;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		if(index > -1){\r\n"+
            "			if(this != targetMenu){\r\n"+
            "				var that = this;\r\n"+
            "				var thatParent = this.parent;\r\n"+
            "				while(that.parent && that.parent != targetMenu){\r\n"+
            "					that.navigateCloseSubMenu();\r\n"+
            "					that = thatParent;\r\n"+
            "					thatParent = that.parent;\r\n"+
            "				}\r\n"+
            "				if(targetMenu.selected != index){\r\n"+
            "					that.navigateCloseSubMenu();\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "			else{ \r\n"+
            "				if(targetMenu.selected != index){\r\n"+
            "					if(targetMenu.selected != null)\r\n"+
            "						targetMenu.highlight(menuItems[targetMenu.selected], false);\r\n"+
            "					targetMenu.selected = index;\r\n"+
            "					targetMenu.highlight(menuItems[targetMenu.selected], true);\r\n"+
            "					this.selectedBy=\"MOUSE\";\r\n"+
            "				}\r\n"+
            "//				var func = function(w, currentIndex, menu){\r\n"+
            "//					if(menu.openMenuInterval != null && currentIndex == menu.selected){\r\n"+
            "//						w.clearInterval(menu.openMenuInterval);\r\n"+
            "//						menu.navigateOpenSubMenu();\r\n"+
            "//					}\r\n"+
            "//				}\r\n"+
            "				targetMenu.navigateOpenSubMenu();\r\n"+
            "//				this.openMenuInterval = this.window.setInterval(function(){func(this.window, index, targetMenu);}, 70);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	else if(this.selected != null && this.selectedBy==\"MOUSE\"){\r\n"+
            "		var menuItems = this.tableElement.children;\r\n"+
            "		this.highlight(menuItems[this.selected], false);\r\n"+
            "		this.selected = null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Menu.prototype.isEmpty=function() {\r\n"+
            "	return this.tableElement.children == null || this.tableElement.children.length == 0;\r\n"+
            "}\r\n"+
            "Menu.prototype.show=function(p, keepYPosition, targetLoc, caller,anchorBottom){\r\n"+
            "    if (this.isEmpty())\r\n"+
            "    	return;\r\n"+
            "    if(!this.parent && currentContextMenu){\r\n"+
            "  	  currentContextMenu.hideAll(true);\r\n"+
            "    }\r\n"+
            "    currentContextMenu=this;\r\n"+
            "    if(this.visible)\r\n"+
            "  	return;\r\n"+
            "    \r\n"+
            "    if(targetLoc == null){\r\n"+
            "  	  if(this.window == null){\r\n"+
            "  		 log('no window!');\r\n"+
            "  		 return;\r\n"+
            "  	  }\r\n"+
            "  	  targetLoc = this.window.document.body;\r\n"+
            "    }\r\n"+
            "    targetLoc.appendChild(this.divElement);\r\n"+
            "    this.targetLoc = targetLoc;\r\n"+
            "    this.visible=true;\r\n"+
            "    var divPos=new Rect().readFromElement(this.divElement);\r\n"+
            "    if(this.parent){\r\n"+
            "  	if(this.parent.activeChild!=null)\r\n"+
            "  		this.parent.activeChild.hide();\r\n"+
            "  	if(this.disabled){\r\n"+
            "  		this.visible=false;\r\n"+
            "  		return;\r\n"+
            "  	}\r\n"+
            "      this.parent.activeChild=this;\r\n"+
            "      var bodyPos=new Rect().readFromElement(targetLoc);\r\n"+
            "      divPos.left=p.x;\r\n"+
            "      divPos.top=p.y;\r\n"+
            "      if(divPos.left<0)\r\n"+
            "        divPos=0;\r\n"+
            "      if(divPos.top<0)\r\n"+
            "        divPos.top=0;\r\n"+
            "      var h=bodyPos.getRight()-divPos.getRight();\r\n"+
            "      if(h<0){\r\n"+
            "        divPos.left=new Rect().readFromElement(this.parent.divElement).left-divPos.width+1;\r\n"+
            "      }\r\n"+
            "      this.divElement.style.left=toPx(divPos.left);\r\n"+
            "      this.divElement.style.top=toPx(divPos.top);\r\n"+
            "      ensureInWindow(this.divElement);\r\n"+
            "    }else{\r\n"+
            "      this.divElement.style.left=toPx(p.x);\r\n"+
            "      if(anchorBottom)\r\n"+
            "        this.divElement.style.bottom=toPx(document.body.offsetHeight-p.y);\r\n"+
            "      else\r\n"+
            "        this.divElement.style.top=toPx(p.y);\r\n"+
            "      if(keepYPosition == true){\r\n"+
            "      	containInWindow(this.divElement);\r\n"+
            "      	this.divElement.style.overflowX = \"hidden\";\r\n"+
            "      }else\r\n"+
            "      	ensureInWindow(this.divElement);\r\n"+
            "      this.glass=nw('div','disable_glass_clear');\r\n"+
            "      this.glass.style.zIndex='9989';\r\n"+
            "      var that=this;\r\n"+
            "  	var onHide = function(){\r\n"+
            "  		if(caller!=null)\r\n"+
            "  			if(caller.hideGlass){\r\n"+
            "  				caller.hideGlass();	\r\n"+
            "  			}\r\n"+
            "  			\r\n"+
            "  	};\r\n"+
            "  	this.glass.onclick=function(e, clickCallback){\r\n"+
            "  		e.preventDefault();\r\n"+
            "	    that.hideAll();\r\n"+
            "	    multiSelectFilter = that.multiSelectCallback();\r\n"+
            "	    if(caller != null)\r\n"+
            "			caller.select(multiSelectFilter);\r\n"+
            "	    that.selectedRows.clear();\r\n"+
            "	    flagSelectedRows = false;\r\n"+
            "	    if(that.onGlass!=null)that.onGlass(e);\r\n"+
            "	    onHide();}\r\n"+
            "  	targetLoc.appendChild(this.glass);\r\n"+
            "    }\r\n"+
            "  };\r\n"+
            "\r\n"+
            "  Menu.prototype.applyBorderStyle=function(menu) {\r\n"+
            "	this.divElement.style.borderTopColor=menu.borderTpLfCl;\r\n"+
            "	this.divElement.style.borderLeftColor=menu.borderTpLfCl;\r\n"+
            "	this.divElement.style.borderBottomColor=menu.borderBtmRtCl;\r\n"+
            "	this.divElement.style.borderRightColor=menu.borderBtmRtCl;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.setHoverStyle=function(menu) {\r\n"+
            "	this.hoverBgCl = menu.hoverBgCl ? menu.hoverBgCl : \"#68c56f\";\r\n"+
            "	this.hoverFontCl = menu.hoverFontCl ? menu.hoverFontCl : \"#ffffff\";\r\n"+
            "\r\n"+
            "}\r\n"+
            "Menu.prototype.createMenu=function(menu, clickCallback){\r\n"+
            "	this.divElement.style.backgroundColor=menu.bgCl;\r\n"+
            "	this.applyBorderStyle(menu);\r\n"+
            "	this.setHoverStyle(menu);\r\n"+
            "	if(menu.children){\r\n"+
            "		for(var i = 0; i < menu.children.length; i++){\r\n"+
            "			this.addMenuItem(menu.children[i], clickCallback);\r\n"+
            "		}\r\n"+
            "	} \r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Menu.prototype.setOptions=function(optionsJSON){\r\n"+
            "	if(optionsJSON.runFirstItemOnEnter!= null)\r\n"+
            "		this.runFirstItemOnEnter = optionsJSON.runFirstItemOnEnter;\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.addMenuItem = function(menu, clickCallback) {\r\n"+
            "	if (menu.type === \"menu\") {\r\n"+
            "		this.addMenu(menu, clickCallback);\r\n"+
            "	} else if (menu.type === \"action\") {\r\n"+
            "		this.addAction(menu, clickCallback);\r\n"+
            "	} else if (menu.type === \"divider\") {\r\n"+
            "		this.addDivider(menu);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.addMenu = function(menu, clickCallback) {\r\n"+
            "	var row = nw(\"tr\");\r\n"+
            "	");
          out.print(
            "var text = nw(\"td\");\r\n"+
            "	\r\n"+
            "	var m = new Menu(this.window);\r\n"+
            "	m.createMenu(menu, clickCallback);\r\n"+
            "\r\n"+
            "	if (menu.enabled) {\r\n"+
            "		m.disabled = false;\r\n"+
            "		row.className = \"menu_item parent\";\r\n"+
            "		row.style.color=menu.fontCl;\r\n"+
            "	} else {\r\n"+
            "		m.disabled = true;\r\n"+
            "		row.className = 'menu_item parent disabled';\r\n"+
            "		row.style.background=menu.disBgColor;\r\n"+
            "		row.style.color=menu.disFontCl;\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	\r\n"+
            "	if (menu.text) {\r\n"+
            "		text.classList.add(\"menu_item_text\");\r\n"+
            "		text.innerHTML = menu.text;\r\n"+
            "		this.menuItemUndoHoverStyle.fontCl=menu.fontCl;\r\n"+
            "		this.menuItemUndoHoverStyle.bgCl=menu.bgCl;\r\n"+
            "	}\r\n"+
            "	if (menu.style)\r\n"+
            "		applyStyle(row, menu.style);\r\n"+
            "	m.parent = this;\r\n"+
            "\r\n"+
            "	if (menu.noIcon != true){\r\n"+
            "		var icon = nw(\"td\");\r\n"+
            "		if (menu.backgroundImage) {\r\n"+
            "			icon.style.backgroundImage = \"url('\" + menu.backgroundImage + \"')\";\r\n"+
            "		}\r\n"+
            "		row.appendChild(icon);\r\n"+
            "	}\r\n"+
            "	row.appendChild(text);\r\n"+
            "	var rightArrow = nw(\"span\");\r\n"+
            "	this.applyRightArrowStyle(rightArrow);\r\n"+
            "	row.appendChild(rightArrow);\r\n"+
            "	this.tableElement.appendChild(row);\r\n"+
            "	var index = [].indexOf.call(this.tableElement.children, row);\r\n"+
            "	this.childMenus.set(index,m);\r\n"+
            "}\r\n"+
            "Menu.prototype.applyRightArrowStyle=function(rightArrow) {\r\n"+
            "    var fontSize = 10;\r\n"+
            "    var rowHeight = 19;\r\n"+
            "    rightArrow.innerHTML = \"&#9658;\";\r\n"+
            "    rightArrow.style.fontSize = toPx(fontSize);\r\n"+
            "    rightArrow.style.marginLeft = toPx(-fontSize);\r\n"+
            "    rightArrow.style.marginTop = toPx(Math.floor(Math.abs((rowHeight - fontSize))/2));\r\n"+
            "    rightArrow.classList.add(\"menu_right_arrow\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.multiSelectCallback = function () {\r\n"+
            "	let multiFilter = \"\";\r\n"+
            "	if(this.selectedRows.isEmpty())\r\n"+
            "		return multiFilter;\r\n"+
            "	for(var i=0; i< this.tableElement.children.length; i++){\r\n"+
            "		var row = this.tableElement.children[i];\r\n"+
            "		if(this.selectedRows.isSelected(i)){\r\n"+
            "			multiFilter += row._itemId;\r\n"+
            "			multiFilter += \"|\";\r\n"+
            "		}			\r\n"+
            "	}\r\n"+
            "	multiFilter = multiFilter.substring(0, multiFilter.length-1);\r\n"+
            "	return multiFilter;\r\n"+
            "} \r\n"+
            "\r\n"+
            "\r\n"+
            "Menu.prototype.addAction = function(menu, clickCallback) {\r\n"+
            "	var row = nw(\"tr\");\r\n"+
            "	var text = nw(\"td\");\r\n"+
            "	if (menu.enabled) {\r\n"+
            "		row.className = 'menu_item';\r\n"+
            "		row._itemId = menu.action;\r\n"+
            "		this.isSelectMenu = menu.style != null && menu.style.includes('selectfield');\r\n"+
            "		var autoclose = menu.autoclose == false ? false : true;\r\n"+
            "		var f = menu.onclickJs != null ? new this.window.Function(menu.onclickJs) : null;\r\n"+
            "		row.id=menu.hids;\r\n"+
            "		\r\n"+
            "		if (autoclose){\r\n"+
            "			var that = this;\r\n"+
            "			if(that.isSelectMenu){\r\n"+
            "				row.addEventListener('mousedown', (e) => {\r\n"+
            "					if(!e.ctrlKey)\r\n"+
            "						this.dragSelect=true;\r\n"+
            "					this.selectRow(e, row.sectionRowIndex, e.ctrlKey, false);\r\n"+
            "					this.updateSelectedRows();\r\n"+
            "			    });\r\n"+
            "	\r\n"+
            "				row.addEventListener('mousemove', (e) => {\r\n"+
            "					this.selectRow(e, row.sectionRowIndex, e.ctrlKey, true);\r\n"+
            "					this.updateSelectedRows();\r\n"+
            "			    });\r\n"+
            "				\r\n"+
            "				row.addEventListener('mouseup', (e) => {\r\n"+
            "			        if (e.button === 2) \r\n"+
            "						this.dragSelect=false;   \r\n"+
            "				});\r\n"+
            "			}\r\n"+
            "			row.onclick = function(e) {\r\n"+
            "				if(f)\r\n"+
            "					f.call(null);\r\n"+
            "				if(!that.isSelectMenu || that.selectedRows.isEmpty())\r\n"+
            "					clickCallback(e, this._itemId);\r\n"+
            "				else if(that.isSelectMenu){\r\n"+
            "					multiSelectFilter = that.multiSelectCallback();\r\n"+
            "					clickCallback(e, multiSelectFilter);\r\n"+
            "					that.selectedRows.clear();\r\n"+
            "				}\r\n"+
            "				that.hideAll();\r\n"+
            "				\r\n"+
            "			};\r\n"+
            "		}else\r\n"+
            "			row.onclick = function(e) {\r\n"+
            "				if(f)\r\n"+
            "					f.call(null);\r\n"+
            "				clickCallback(e, this._itemId);\r\n"+
            "			};\r\n"+
            "		row.keystroke= menu.keystroke;\r\n"+
            "		row.onclick._itemId = menu.action;\r\n"+
            "	} else {\r\n"+
            "		row.className = 'menu_item disabled';\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	\r\n"+
            "\r\n"+
            "    if (menu.text) { // affects all the texts.\r\n"+
            "        if (!row.classList.contains(\"ami_edit_menu\")) { // don't apply style on the menu header.\r\n"+
            "            text.classList.add(\"menu_item_text\");\r\n"+
            "            if (row.classList.contains(\"disabled\")) {\r\n"+
            "                row.style.background=menu.disBgColor;\r\n"+
            "                row.style.color=menu.disFontCl;\r\n"+
            "            } else {\r\n"+
            "                row.style.background=menu.bgCl;\r\n"+
            "                row.style.color=menu.fontCl;\r\n"+
            "                // this remembers the style before menu item is hovered. Used in highlight function.\r\n"+
            "                this.menuItemUndoHoverStyle.fontCl=menu.fontCl;\r\n"+
            "                this.menuItemUndoHoverStyle.bgCl=menu.bgCl;\r\n"+
            "            }\r\n"+
            "        }\r\n"+
            "        text.innerHTML = menu.text;\r\n"+
            "		//this is to reconcile different default inline-height in different browsers. Firefox has a different in-line height than other browsers, which will add extra padding to the font height\r\n"+
            "		text.style.lineHeight = 1;\r\n"+
            "    }\r\n"+
            "    if (menu.style)\r\n"+
            "        applyStyle(row, menu.style);\r\n"+
            "    if (menu.noIcon != true){\r\n"+
            "        var icon = nw(\"td\");\r\n"+
            "        if (menu.backgroundImage) {\r\n"+
            "            icon.style.backgroundImage = \"url('\" + menu.backgroundImage + \"')\";\r\n"+
            "        }\r\n"+
            "        row.appendChild(icon);\r\n"+
            "    }\r\n"+
            "    row.appendChild(text);\r\n"+
            "    this.tableElement.appendChild(row);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.updateSelectedRows=function(){\r\n"+
            "	for(var i=0; i< this.tableElement.children.length; i++){\r\n"+
            "		row = this.tableElement.children[i];\r\n"+
            "		if(this.selectedRows.isSelected(i) && !row.classList.contains('selected'))\r\n"+
            "			row.classList.add('selected');\r\n"+
            "		else if (!this.selectedRows.isSelected(i) && row.classList.contains('selected'))\r\n"+
            "			row.classList.remove('selected');\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.selectRow=function(e,row,ctrlKey,isDragging){\r\n"+
            "    if(getMouseButton(e) != 2){\r\n"+
            "		return;\r\n"+
            "    }\r\n"+
            "	if(!isDragging && this.dragSelect)\r\n"+
            "		this.dragStart=row;\r\n"+
            "	if(ctrlKey){\r\n"+
            "		if(this.extension.field instanceof TextField)\r\n"+
            "			return;\r\n"+
            "		if (getMouseButton(e) == 2 && this.selectedRows.isSelected(row)) \r\n"+
            "			this.selectedRows.remove(row);\r\n"+
            "		else\r\n"+
            "			this.selectedRows.add(row);\r\n"+
            "	}else if(this.dragSelect){\r\n"+
            "		if(this.extension.field instanceof TextField)\r\n"+
            "			return;\r\n"+
            "		this.selectedRows.clear();\r\n"+
            "		if(row>this.dragStart)\r\n"+
            "			this.selectedRows.add(this.dragStart,row);\r\n"+
            "		else\r\n"+
            "			this.selectedRows.add(row,this.dragStart);\r\n"+
            "		\r\n"+
            "	}else{\r\n"+
            "		this.selectedRows.clear();\r\n"+
            "		this.selectedRows.add(row);\r\n"+
            "	}\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.addDivider = function(menu) {\r\n"+
            "    var row = nw(\"tr\");\r\n"+
            "    var cell = nw(\"td\");\r\n"+
            "    cell.style.background=menu.divCl;\r\n"+
            "    if (menu.style)\r\n"+
            "        applyStyle(row, menu.style);\r\n"+
            "    cell.setAttribute(\"colspan\", 2);\r\n"+
            "    row.appendChild(cell);\r\n"+
            "    this.tableElement.appendChild(row);\r\n"+
            "}\r\n"+
            "\r\n"+
            "//Deprecated\r\n"+
            "Menu.prototype.addItem=function(name,_itemId,clickCallback,dontClose,onclickJs){\r\n"+
            "    var that=this;\r\n"+
            "    if(dontClose)\r\n"+
            "      return this.addItemInner(name,_itemId,clickCallback,null,null,false,onclickJs);\r\n"+
            "    else\r\n"+
            "      return this.addItemInner(name,_itemId,clickCallback,function(event){that.hideChildren();},null,true,onclickJs);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Menu.prototype.showFromParent=function(event){\r\n"+
            "  event=getMouseEvent(event);\r\n"+
            "  r=new Rect();\r\n"+
            "  r.readFromElement(getMouseTarget(event));\r\n"+
            "  this.show(new Point(r.getRight()+2,r.getTop()));\r\n"+
            "};\r\n"+
            "Menu.prototype.onClose=function(){\r\n"+
            "}\r\n"+
            "Menu.prototype.setFieldId=function(fieldId){\r\n"+
            "    this.fieldId=fieldId;\r\n"+
            "}\r\n"+
            "Menu.prototype.noAutoScrollbar = function() {\r\n"+
            "	//1. first remove the tiny squre from the scrollbar container\r\n"+
            "	const scrollbarContainer = this.divElement.children[1];\r\n"+
            "	if(scrollbarContainer){\r\n"+
            "		for(var child of scrollbarContainer.children){\r\n"+
            "			if(child.className=='tiny_square'){\r\n"+
            "				scrollbarContainer.removeChild(child);\r\n"+
            "				break;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	//TODO: Not sure if this is necessary, this is to readjust the height of the scrollpane container\r\n"+
            "	var borderWidth = 1.1111;\r\n"+
            "	var scrollpaneContainerHeight = parseFloat(this.divElement.style.height) + 2*borderWidth;\r\n"+
            "	this.divElement.style.height =  `${scrollpaneContainerHeight}px`;\r\n"+
            "	\r\n"+
            "};\r\n"+
            "Menu.prototype.addScrollPane = function(){\r\n"+
            "	var that=this;\r\n"+
            "	this.scrollSize=15;\r\n"+
            "	this.scrollPane=new ScrollPane(this.divElement,this.scrollSize, this.tableElement);\r\n"+
            "	this.scrollPane.onScroll=function(){that.onScroll();};\r\n"+
            "}\r\n"+
            "Menu.prototype.onScroll=function(){\r\n"+
            "	this.userScrollSeqnum++;\r\n"+
            "	var isHscrollVisible = this.scrollPane.hscrollVisible;\r\n"+
            "	this.setClipZone();\r\n"+
            "	FieldExtension.prototype.callBack.call(this.extension,\"onBoundsChange\",{top:this.scrollPane.getClipTop(),\r\n"+
            "		userSeqnum:this.userSeqnum,\r\n"+
            "		s:this.upperRow,\r\n"+
            "		e:this.lowerRow});	\r\n"+
            "}\r\n"+
            "\r\n"+
            "//TODO:This is for horizontal scrollbar for text field infinite scroll\r\n"+
            "Menu.prototype.updateCellsLocations=function(){\r\n"+
            "	//shift each row left or right\r\n"+
            "	for(var y = 0; y < this.tableElement.children.length; y++){\r\n"+
            "		var cell=this.getChildMenu(y);\r\n"+
            "		if(cell){\r\n"+
            "			var leftPos=Math.round(this.scrollPane.getClipLeft());\r\n"+
            "			cell.style.position = 'relative';\r\n"+
            "			cell.style.left=toPx(-leftPos);\r\n"+
            "		}\r\n"+
            "			\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "/**\r\n"+
            " * Given this menu structure:\r\n"+
            " * <div class=\"menu selectfield_ac_list scrollpane_container\" style=\"width: 199px; height: 142px; left: 192px; top: 453px; overflow-x: hidden;\">\r\n"+
            " * 		<div class=\"scrollpane\" style=\"left: 0px; top: 0px; width: 184px; height: 142px;\">\r\n"+
            " * 			<table style=\"width: 100%;\"></table></div>\r\n"+
            " * 		<div class=\"scrollbar_container\" style=\"left: 184px; top: 0px; width: 15px; height: 142px;\">\r\n"+
            " * 			<div class=\"scrollbar_track_v\" style=\"left: 0px; top: 15px; width: 15px; height: 112px;\"></div>		\r\n"+
            " * 			<div class=\"scrollbar_handle_v\" style=\"display: inline; left: 0px; top: 15px; width: 15px; height: 47px;\">\r\n"+
            " * 				<div class=\"scrollbar_grip_v\"></div></div>\r\n"+
            " * 			<div class=\"scrollbar_down\" style=\"left: 0px; top: 127px; width: 15px; height: 15px;\"></div>\r\n"+
            " * 			<div class=\"scrollbar_up\" style=\"left: 0px; top: 0px; width: 15px; height: 15px;\"></div>\r\n"+
            " * 		</div>\r\n"+
            " * </div>\r\n"+
            " * \r\n"+
            "*/\r\n"+
            "//this set the width and height for the scrollpane container \r\n"+
            "Menu.prototype.setMenuBoxWidthHeight=function(width,height){\r\n"+
            "	this.scrollPane.width = width;\r\n"+
            "	this.scrollPane.height = height;\r\n"+
            "}\r\n"+
            "//if there are 1000 total suggestions, we need this much space reserved for scrollpane height\r\n"+
            "Menu.prototype.initScroll=function(totalSize, rowHeight, rowWidth){\r\n"+
            "	this.userScrollSeqnum = 0;\r\n"+
            "	var totalHeight = totalSize*rowHeight;\r\n"+
            "	this.scrollPane.setPaneSize(rowWi");
          out.print(
            "dth - this.scrollSize, totalHeight);//width,height; height should reflect the number of suggestions\r\n"+
            "}\r\n"+
            "Menu.prototype.setTableRowHeight=function(rowHeight){\r\n"+
            "	this.rowHeight = rowHeight;\r\n"+
            "	for(var row of this.tableElement.rows){\r\n"+
            "		this.tableElement.rowHeight = rowHeight;\r\n"+
            "		row.style.height = rowHeight;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Menu.prototype.setClipZone=function(){\r\n"+
            "	this.lowerRow = Math.ceil((this.scrollPane.getClipTop()+this.scrollPane.getClipHeight())/this.tableElement.rowHeight)-1;\r\n"+
            "	this.upperRow = this.lowerRow - this.extension.visibleSugLen + 1;\r\n"+
            "}\r\n"+
            "\r\n"+
            "//MOBILE SUPPORT - FOR SCROLLING\r\n"+
            "Menu.prototype.onTouchDragStart=function(e){\r\n"+
            "	this.currPoint = getMousePoint(e);\r\n"+
            "}\r\n"+
            "\r\n"+
            "//MOBILE SUPPORT - FOR SCROLLING\r\n"+
            "Menu.prototype.onTouchDragMove=function(e){\r\n"+
            "		var that = this;		\r\n"+
            "		var diffx = that.currPoint.x - getMousePoint(e).x;\r\n"+
            "		var diffy = that.currPoint.y - getMousePoint(e).y;\r\n"+
            "		that.scrollPane.hscroll.goPage(0.01 * diffx);\r\n"+
            "		that.scrollPane.vscroll.goPage(0.01 * diffy);\r\n"+
            "		that.currPoint = getMousePoint(e);\r\n"+
            "}");

	}
	
}