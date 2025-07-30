package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_tabs_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_tabs_js_1() {
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
            "//#######################\r\n"+
            "//##### Tabs #####\r\n"+
            "\r\n"+
            "\r\n"+
            "function Tabs(portlet,parentId){\r\n"+
            "    var that = this;\r\n"+
            "    this.portlet=portlet;\r\n"+
            "    this.location=this.portlet.location;\r\n"+
            "    this.divElement = this.portlet.divElement;\r\n"+
            "    \r\n"+
            "	this.allowAdd=true;\r\n"+
            "	this.tabs=[];\r\n"+
            "	this.blinkTimers=[];\r\n"+
            "	this.isContainer=true;\r\n"+
            "	this.hasArrowButtons = true;\r\n"+
            "    \r\n"+
            "	this.selectColor='#aaaaaa';\r\n"+
            "	this.unselectColor='#dddddd';\r\n"+
            "	this.selectTextColor='#000000';\r\n"+
            "	this.unselectTextColor='#000000'\r\n"+
            "		\r\n"+
            "	this.initDOM();\r\n"+
            "	this.tabLeftPos=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "var ADD_SVG_PREFIX=SVG_PREFIX+\r\n"+
            "	'viewBox=\"0 0 1000 1000\" ><path style=\"fill:' ;\r\n"+
            "var ADD_SVG_SUFFIX=';\" d=\"M729.471,457.513h-186.991V270.519c0-23.473-19.006-42.498-42.479-42.498 c-23.473,0-42.498,19.006-42.498,42.498v186.991H270.512c-23.473,0-42.479,19.006-42.479,42.498c0,23.473,19.006,42.479,42.479,42.479 h186.975v187.011c0,23.476,19.022,42.479,42.498,42.479c23.473,0,42.498-19.006,42.498-42.479v-187.011h186.975 c23.476,0,42.498-19.006,42.498-42.479C771.97,476.516,752.944,457.513,729.471,457.513z\"'+\r\n"+
            "	'/>'+ SVG_SUFFIX;\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "Tabs.prototype.initDOM=function(){\r\n"+
            "	var that = this;\r\n"+
            "	this.tabDiv=nw('div','portal_tabpane');\r\n"+
            "	\r\n"+
            "	this.scrollPaneElement=nw('div','portal_tabScrollPane');\r\n"+
            "	\r\n"+
            "	this.scrollPane=new ScrollPane(this.scrollPaneElement,0,this.tabDiv);\r\n"+
            "	this.scrollPane.isvscrollDefault=false;\r\n"+
            "	this.scrollPane.DOM.paneElement.style.background=\"unset\";\r\n"+
            "	this.scrollPane.DOM.paneElement.style.overflow=\"unset\";\r\n"+
            "	this.scrollPane.onScroll=function(){that.onScroll()};\r\n"+
            "	\r\n"+
            "	this.contentDiv=nw('div');\r\n"+
            "	this.contentDiv.style.left='0px';\r\n"+
            "	this.contentDiv.style.right='0px';\r\n"+
            "	this.contentDiv.style.bottom='0px';\r\n"+
            "	this.contentDiv.onSwipe=function(x,y,x2,y2){that.onSwipe(x,y,x2,y2);};\r\n"+
            "	this.contentDiv.onSwipeDone=function(x,y,x2,y2){that.onSwipeDone(x,y,x2,y2);};\r\n"+
            "	\r\n"+
            "	this.newTabSpan=nw('span','portal_tab portal_tab_newtabicon');\r\n"+
            "	\r\n"+
            "	this.arrowContainer=nw('div','portal_arrows');\r\n"+
            "	this.arrowLeft=nw('div','portal_tab portal_tabArrow');\r\n"+
            "	this.arrowLeft.innerHTML=\"<abbr title='Previous Tab'>&#8249;</abbr>\";\r\n"+
            "	this.arrowLeft.onclick=function(){that.onPrev();};\r\n"+
            "	this.arrowRight=nw('div','portal_tab portal_tabArrow');\r\n"+
            "	this.arrowRight.innerHTML=\"<abbr title='Next Tab'>&#8250;</abbr>\";\r\n"+
            "	this.arrowRight.onclick=function(){that.onNext();};\r\n"+
            "	this.tabsBar=nw('div','portal_tabsBar');\r\n"+
            "	\r\n"+
            "	this.divElement.style.background='white';\r\n"+
            "	\r\n"+
            "	this.tabsBar.appendChild(this.scrollPaneElement);\r\n"+
            "	this.tabsBar.appendChild(this.arrowContainer);\r\n"+
            "	this.arrowContainer.appendChild(this.arrowLeft);\r\n"+
            "	this.arrowContainer.appendChild(this.arrowRight);\r\n"+
            "	this.divElement.appendChild(this.contentDiv);\r\n"+
            "	this.divElement.appendChild(this.tabsBar);\r\n"+
            "}\r\n"+
            "Tabs.prototype.onPrev=function(){\r\n"+
            "	if(this.activeTab > 0){\r\n"+
            "		//Get tab thats not hidden\r\n"+
            "		var nextTab = this.activeTab-1;\r\n"+
            "		var find = true;\r\n"+
            "		while(find){\r\n"+
            "			if(this.tabs[nextTab].isHidden == false)\r\n"+
            "				find = false;\r\n"+
            "			else{\r\n"+
            "				if(nextTab > 0){\r\n"+
            "					nextTab--;\r\n"+
            "				}\r\n"+
            "				else\r\n"+
            "					return;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	    this.callBack('tab',{tabindex:this.tabs[nextTab].tabid});\r\n"+
            "	    removeAllChildren(this.contentDiv);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Tabs.prototype.onNext=function(){\r\n"+
            "	if(this.activeTab < this.tabs.length-1){\r\n"+
            "		//Get tab thats not hidden\r\n"+
            "		var nextTab = this.activeTab+1;\r\n"+
            "		var find = true;\r\n"+
            "		while(find){\r\n"+
            "			if(this.tabs[nextTab].isHidden == false)\r\n"+
            "				find = false;\r\n"+
            "			else{\r\n"+
            "				if(nextTab < this.tabs.length-1){\r\n"+
            "					nextTab++;\r\n"+
            "				}\r\n"+
            "				else\r\n"+
            "					return;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		this.callBack('tab',{tabindex:this.tabs[nextTab].tabid});\r\n"+
            "		removeAllChildren(this.contentDiv);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "Tabs.prototype.onScroll=function(){\r\n"+
            "	var fullWidth = this.tabDiv.offsetWidth;\r\n"+
            "	var tabsWidth = this.isVertical?this.location.height:this.location.width;\r\n"+
            "	//Reset position if no need for scrolling\r\n"+
            "	if(fullWidth <= tabsWidth){\r\n"+
            "		this.tabLeftPos = 0;\r\n"+
            "		this.scrollPane.DOM.innerpaneElement.style.left=toPx(this.tabLeftPos);\r\n"+
            "		this.scrollPane.DOM.innerpaneElement.style.right=null;\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	var pos = this.scrollPane.getClipLeft(); \r\n"+
            "	pos = min(pos,fullWidth-(tabsWidth-2*this.tabHeight));\r\n"+
            "	if(this.isFarAligned)\r\n"+
            "		pos = - pos;\r\n"+
            "	\r\n"+
            "	this.tabLeftPos = -pos;\r\n"+
            "	this.scrollPane.DOM.innerpaneElement.style.left=toPx(this.tabLeftPos);\r\n"+
            "	this.scrollPane.DOM.innerpaneElement.style.right=null;\r\n"+
            "}\r\n"+
            "Tabs.prototype.createMenu=function(i, tabid){\r\n"+
            "	//Create element\r\n"+
            "	var tabMenu=nw('span','portal_tab_menu');\r\n"+
            "	this.tabs[i].tabMenu = tabMenu;\r\n"+
            "	tabMenu.tabid=tabid;\r\n"+
            "	\r\n"+
            "    //Add to dom\r\n"+
            "	this.tabs[i].tabInner.appendChild(tabMenu);\r\n"+
            "	\r\n"+
            "    //Add onclick for menu\r\n"+
            "	var that = this;\r\n"+
            "	tabMenu.onclick=function(e){that.onMenu(e); e.stopPropagation();};\r\n"+
            "	\r\n"+
            "	//Default style\r\n"+
            "	tabMenu.style.position = \"relative\";\r\n"+
            "	tabMenu.style.display = \"inline\";\r\n"+
            "	tabMenu.style.top = \"0px\";\r\n"+
            "	//	    tabMenu.style.right = \"4px\";	\r\n"+
            "	//		tabMenu.style.float = \"right\";\r\n"+
            "	//Custom Style\r\n"+
            "	tabMenu.style.backgroundImage=this.menuImageUnselected;\r\n"+
            "	tabMenu.style.backgroundSize=toPx(this.menuArrowSize);\r\n"+
            "	tabMenu.style.padding=this.menuArrowSize+\"px\";\r\n"+
            "}\r\n"+
            "Tabs.prototype.createDraggableTabs=function(){\r\n"+
            "	var dragDoc = getDocument(this.tabDiv);\r\n"+
            "	var that = this;\r\n"+
            "	var onDragEndFunc = function(oldIdx, newIdx){\r\n"+
            "		that.callBack('moveTab',{tabindex:oldIdx, nwtabindex:newIdx});\r\n"+
            "	};\r\n"+
            "	var getTabId = function(draggableElement){\r\n"+
            "		var loc = draggableElement.__idx;\r\n"+
            "		loc += draggableElement.__indexX - draggableElement.__origIndexX;\r\n"+
            "\r\n"+
            "//		liveDebugger.onStat(\"getTabId\", loc);\r\n"+
            "		return loc;\r\n"+
            "	};\r\n"+
            "\r\n"+
            "	var getMousePos = function(event){\r\n"+
            "		var tabDivRect = that.tabDiv.getBoundingClientRect();\r\n"+
            "		var rect = this.calcContainerRect();\r\n"+
            "		var r = {};\r\n"+
            "\r\n"+
            "		if(that.isVertical == false) { // Horizontal\r\n"+
            "			if(that.isFarAligned == false){ // Horizontal Left Align\r\n"+
            "				r.x = that.scrollPane.getClipLeft() + event.x - rect.left; \r\n"+
            "				r.y = 0;\r\n"+
            "			}\r\n"+
            "			else if (that.isFarAligned == true){ // Horizontal Right Align\r\n"+
            "				r.x = (event.x - rect.left) + (rect.left - tabDivRect.left); \r\n"+
            "				r.y = 0;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		else{\r\n"+
            "			if(that.isFarAligned == false){ // Horizontal Left Align // Bottom\r\n"+
            "				r.x = that.scrollPane.getClipLeft() + rect.height - (event.y - rect.top);\r\n"+
            "				r.y = 0;\r\n"+
            "			}\r\n"+
            "			else if (that.isFarAligned == true){ // Vertical Top Align // Top\r\n"+
            "				r.x = (rect.top + tabDivRect.height)  - event.y ;\r\n"+
            "				r.y = 0;\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "//		liveDebugger.onStat(\"mousePos\",JSON.stringify(r));\r\n"+
            "		return r;\r\n"+
            "	};\r\n"+
            "\r\n"+
            "	if(this.draggableContainer)\r\n"+
            "		this.draggableContainer.clear();\r\n"+
            "	\r\n"+
            "\r\n"+
            "	var calcContainerRect = function(){\r\n"+
            "		var tabBarRect = that.tabsBar.getBoundingClientRect();\r\n"+
            "		return tabBarRect;\r\n"+
            "	};\r\n"+
            "	\r\n"+
            "	var onMousePositionHandler= function(absMousePos, absContainerPos,  mouseOffset, elementWidth, moveX, moveY){\r\n"+
            "		if(moveY)\r\n"+
            "			return;\r\n"+
            "		\r\n"+
            "		var origContainerPos;\r\n"+
            "		\r\n"+
            "		if(that.isVertical == false) { // Horizontal\r\n"+
            "			var diffPos = absMousePos.x - absContainerPos.x;\r\n"+
            "\r\n"+
            "			var relContainerPos = that.scrollPane.getClipLeft(); \r\n"+
            "			var containerSize = that.scrollPane.getClipWidth();\r\n"+
            "			\r\n"+
            "			origContainerPos = relContainerPos; \r\n"+
            "			if(that.isFarAligned == false){ // Working Bottom\r\n"+
            "				if(diffPos <= (elementWidth/2)){\r\n"+
            "					var np = relContainerPos + containerSize * -0.018;\r\n"+
            "					that.scrollPane.setClipLeft(np);\r\n"+
            "				}\r\n"+
            "				else if(diffPos >= (containerSize - elementWidth/2)){\r\n"+
            "					var np = relContainerPos + containerSize * +0.018;\r\n"+
            "					that.scrollPane.setClipLeft(np);\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "			else if(that.isFarAligned == true){ // Working Top\r\n"+
            "				if(diffPos <= (elementWidth/2)){\r\n"+
            "					var np = relContainerPos + containerSize * +0.018;\r\n"+
            "					that.scrollPane.setClipLeft(np);\r\n"+
            "				}\r\n"+
            "				else if(diffPos >= (containerSize - elementWidth/2)){\r\n"+
            "					var np = relContainerPos + containerSize * -0.018;\r\n"+
            "					that.scrollPane.setClipLeft(np);\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		else if(that.isVertical == true) { // Vertical\r\n"+
            "			\r\n"+
            "			var relContainerPos = that.scrollPane.getClipLeft(); \r\n"+
            "			var containerSize = that.scrollPane.getClipWidth();\r\n"+
            "\r\n"+
            "			var diffPos = (absContainerPos.y + containerSize) - absMousePos.y;\r\n"+
            "			origContainerPos = relContainerPos; \r\n"+
            "\r\n"+
            "			if(that.isFarAligned == false){ // Working //Bottom\r\n"+
            "				if(diffPos <= (elementWidth/2)){\r\n"+
            "					var np = relContainerPos + containerSize * -0.018;\r\n"+
            "					that.scrollPane.setClipLeft(np);\r\n"+
            "				}\r\n"+
            "				else if(diffPos >= (containerSize - elementWidth/2)){\r\n"+
            "					var np = relContainerPos + containerSize * +0.018;\r\n"+
            "					that.scrollPane.setClipLeft(np);\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "			else if(that.isFarAligned == true){ // Working //Top\r\n"+
            "				if(diffPos <= (elementWidth/2)){\r\n"+
            "					var np = relContainerPos + containerSize * +0.018;\r\n"+
            "					that.scrollPane.setClipLeft(np);\r\n"+
            "				}\r\n"+
            "				else if(diffPos >= (containerSize - elementWidth/2)){\r\n"+
            "					var np = relContainerPos + containerSize * -0.018;\r\n"+
            "					that.scrollPane.setClipLeft(np);\r\n"+
            "				}\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "		\r\n"+
            "		var moved = relContainerPos != origContainerPos;\r\n"+
            "		return moved;\r\n"+
            "	};\r\n"+
            "\r\n"+
            "	this.draggableContainer = new ContainerWithDraggableElements(this.tabDiv, calcContainerRect, null, onDragEndFunc, onMousePositionHandler, null, getTabId, getMousePos, true, true, false);\r\n"+
            "\r\n"+
            "\r\n"+
            "	var posOffset = 0;\r\n"+
            "	var ntabs = this.tabs.length;\r\n"+
            "	var flip = this.isFarAligned == true;\r\n"+
            "				\r\n"+
            "	// Tabs start padding and offset\r\n"+
            "	if(flip == false){\r\n"+
            "		posOffset += this.tabPaddingStart + abs(this.beginningPadding);\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	for(var i = 0; i != ntabs;i++){\r\n"+
            "		var tabMeta = this.tabs[i];\r\n"+
            "		// if tab is hidden and not in dev mode\r\n"+
            "		if(tabMeta.isHidden == true && !(this.showTabsOverride))\r\n"+
            "			continue;\r\n"+
            "\r\n"+
            "			var tabContainer = tabMeta.tabInner;\r\n"+
            "			var tabRect = tabContainer.getBoundingClientRect();\r\n"+
            "	\r\n"+
            "			var calcWidth = null;\r\n"+
            "			var calcHeight = null;\r\n"+
            "			var calcLeft = null;\r\n"+
            "			var calcTop = null;\r\n"+
            "	\r\n"+
            "			if(this.isVertical == false ){\r\n"+
            "				calcWidth = tabRect.width;\r\n"+
            "				calcHeight = tabRect.height;\r\n"+
            "				calcTop = 0;\r\n"+
            "				\r\n"+
            "				// Tabs spacing\r\n"+
            "				if(flip == true)\r\n"+
            "				posOffset += this.tabSpacing;\r\n"+
            "	\r\n"+
            "				calcLeft = posOffset;\r\n"+
            "				\r\n"+
            "				posOffset += tabRect.width;\r\n"+
            "				// Tabs spacing\r\n"+
            "				if(flip == false)\r\n"+
            "				posOffset += thi");
          out.print(
            "s.tabSpacing;\r\n"+
            "	\r\n"+
            "			}\r\n"+
            "			else if(this.isVertical == true ){ // TOP Vertical\r\n"+
            "				calcWidth = tabRect.height;\r\n"+
            "				calcHeight = tabRect.width;\r\n"+
            "				\r\n"+
            "				calcTop = 0;\r\n"+
            "	\r\n"+
            "				if(flip == true)\r\n"+
            "				posOffset += this.tabSpacing;\r\n"+
            "	\r\n"+
            "				calcLeft = posOffset;\r\n"+
            "				posOffset += tabRect.height; \r\n"+
            "	\r\n"+
            "	\r\n"+
            "				// Tabs spacing\r\n"+
            "				if(flip == false)\r\n"+
            "				posOffset += this.tabSpacing;\r\n"+
            "			}\r\n"+
            "	\r\n"+
            "			var relTabRect = new Rect(calcLeft,calcTop,calcWidth,calcHeight);\r\n"+
            "			var calcTabRect = function(){\r\n"+
            "				return relTabRect;\r\n"+
            "			}\r\n"+
            "			if(this.hideArrangeTabs == true){\r\n"+
            "				var de = new DraggableElement(this.draggableContainer, tabContainer, tabContainer, calcTabRect, tabMeta.tabid);\r\n"+
            "				this.draggableContainer.addDraggableElement(de);\r\n"+
            "			}\r\n"+
            "			else{\r\n"+
            "				tabContainer.style.position=\"absolute\";\r\n"+
            "				tabContainer.style.left=toPx(relTabRect.left);\r\n"+
            "				tabContainer.style.top=toPx(relTabRect.top);\r\n"+
            "				tabContainer.style.width=toPx(relTabRect.width);\r\n"+
            "				tabContainer.style.height=toPx(relTabRect.height);\r\n"+
            "			}\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	// Tabs start padding and offset\r\n"+
            "	if(flip == true ){\r\n"+
            "		posOffset += this.tabPaddingStart + abs(this.beginningPadding);\r\n"+
            "	}\r\n"+
            "//	liveDebugger.onStat(\"tabDivWidth\", JSON.stringify(posOffset));\r\n"+
            "	this.tabDiv.style.width=toPx(abs(posOffset));\r\n"+
            "}\r\n"+
            "Tabs.prototype.createTabsElementsDom=function(){\r\n"+
            "	this.tabDiv.style.fontSize=toPx(fl(this.fontSize));\r\n"+
            "	this.tabDiv.style.fontFamily=this.fontFamily;\r\n"+
            "	this.clearBlinkTimers();\r\n"+
            "	for(var i = 0; i < this.tabs.length; i++){\r\n"+
            "		var tabMeta = this.tabs[i];\r\n"+
            "		var isHidden = tabMeta.isHidden;\r\n"+
            "		// if tab is hidden and not in dev mode\r\n"+
            "		if(isHidden == true && !(this.showTabsOverride))\r\n"+
            "			continue;\r\n"+
            "		var tabName = tabMeta.tabName;\r\n"+
            "		var tabNameHtml = tabMeta.tabNameHtml;\r\n"+
            "		var tabid = tabMeta.tabid;\r\n"+
            "		\r\n"+
            "		//Create elements\r\n"+
            "		var tabContainer = nw('span', 'portal_tab');\r\n"+
            "		var tabText = nw('abbr');\r\n"+
            "	    this.tabs[i].tabInner = tabContainer;\r\n"+
            "	    this.tabs[i].tabText = tabText;\r\n"+
            "	    tabContainer.tabid=tabid;\r\n"+
            "	    tabText.tabid=tabid;\r\n"+
            "	    \r\n"+
            "	    //Add to dom\r\n"+
            "	    tabContainer.appendChild(tabText);\r\n"+
            "	    this.tabDiv.appendChild(tabContainer);\r\n"+
            "	    \r\n"+
            "	    if(this.allowMenu)\r\n"+
            "	    	this.createMenu(i, tabid);\r\n"+
            "	    \r\n"+
            "	    //Set html and hoverover\r\n"+
            "	    tabText.title = tabName;\r\n"+
            "	    tabText.innerHTML=tabNameHtml;\r\n"+
            "	    tabContainer.id=tabMeta.hids;\r\n"+
            "	    \r\n"+
            "	    \r\n"+
            "	    //Default Style\r\n"+
            "	    tabText.style.textDecoration=\"none\";\r\n"+
            "	  	tabText.style.display=\"inline-block\";\r\n"+
            "		tabText.style.overflow=\"hidden\";\r\n"+
            "	    if (isHidden==true && this.showTabsOverride) {\r\n"+
            "	    	tabText.innerHTML=tabNameHtml + \"&nbsp;(Hidden)\";\r\n"+
            "	    	tabText.style.opacity=.6;\r\n"+
            "		}\r\n"+
            "	    \r\n"+
            "	    //User Styles\r\n"+
            "		tabContainer.style.height=toPx(this.tabHeight);\r\n"+
            "		tabContainer.style.paddingTop=toPx(fl((this.tabHeight-this.fontSize)/2));\r\n"+
            "		tabContainer.style.paddingLeft=toPx(clip(this.leftRadius,2,10));\r\n"+
            "		tabContainer.style.paddingRight=toPx(clip(this.rightRadius,2,10));\r\n"+
            "//		if(this.isFarAligned)\r\n"+
            "//			tabContainer.style.marginLeft=toPx(this.tabSpacing);\r\n"+
            "//		else\r\n"+
            "		tabContainer.style.marginRight=toPx(this.tabSpacing);\r\n"+
            "		\r\n"+
            "		\r\n"+
            "	    if(this.isBottom){\r\n"+
            "	    	tabContainer.style.borderBottomLeftRadius=toPx(this.leftRadius);\r\n"+
            "	    	tabContainer.style.borderBottomRightRadius=toPx(this.rightRadius);\r\n"+
            "	    }else{\r\n"+
            "	    	tabContainer.style.borderTopLeftRadius=toPx(this.leftRadius);\r\n"+
            "	    	tabContainer.style.borderTopRightRadius=toPx(this.rightRadius);\r\n"+
            "	    }\r\n"+
            "	    \r\n"+
            "	    \r\n"+
            "	    var bg;\r\n"+
            "	    var textColor;\r\n"+
            "	    var f=function(e){that.onTabSelected(e);that.onTabClicked(e);};\r\n"+
            "	    var f2=function(e){that.onTabClicked(e);};\r\n"+
            "	    var that = this;\r\n"+
            "	    if(i==this.activeTab){\r\n"+
            "	    	tabContainer.onclick=f2;\r\n"+
            "	    	//      makeEditable(tabText,tab.allowTitleEdit);\r\n"+
            "	    	tabContainer.ondblclick=function(){\r\n"+
            "	    		if(amiEditDesktopArgs!=null && amiEditDesktopArgs.edit == true)\r\n"+
            "	    			that.callBack('renameTabDiag',{tabindex:this.tabid});\r\n"+
            "	    	};\r\n"+
            "	    	tabText.onEdit=function(old,nuw){ \r\n"+
            "	    		that.callBack('renameTab',{tabindex:this.tabid,text:nuw});\r\n"+
            "	    	};\r\n"+
            "	    	if(tabMeta.selectColor)\r\n"+
            "	    		bg=tabMeta.selectColor;\r\n"+
            "	    	else\r\n"+
            "	    		bg=this.selectColor;\r\n"+
            "      \r\n"+
            "	    	if(tabMeta.selectTextColor)\r\n"+
            "	    		textColor=tabMeta.selectTextColor;\r\n"+
            "	    	else\r\n"+
            "	    		textColor=this.selectTextColor;\r\n"+
            "      		\r\n"+
            "      		if (tabMeta.blinkColor) {\r\n"+
            "				this.blinkTab(tabContainer, tabMeta, bg);\r\n"+
            "				tabContainer.classList.add(\"tab_blink\");\r\n"+
            "			}\r\n"+
            "\r\n"+
            "      		if (this.selBorderSize && this.selBorderColor) {\r\n"+
            "      			tabContainer.style.borderBottom=this.selBorderSize + 'px solid ' + this.selBorderColor;\r\n"+
            "      		}\r\n"+
            "	    	tabContainer.style.boxShadow=this.selectShadow;\r\n"+
            "	    	tabContainer.style.zIndex=2;\r\n"+
            "	    }else{\r\n"+
            "	    	tabContainer.onclick=f;\r\n"+
            "	    	tabContainer.style.boxShadow=this.unselectShadow;\r\n"+
            "	    	tabContainer.style.zIndex=1;\r\n"+
            "       \r\n"+
            "	    	if(tabMeta.unselectColor)\r\n"+
            "	    		bg=tabMeta.unselectColor;\r\n"+
            "	    	else\r\n"+
            "	    		bg=this.unselectColor;\r\n"+
            "      \r\n"+
            "	    	if(tabMeta.unselectTextColor)\r\n"+
            "	    		textColor=tabMeta.unselectTextColor;\r\n"+
            "	    	else\r\n"+
            "	    		textColor=this.unselectTextColor;\r\n"+
            "	    		\r\n"+
            "      		if (tabMeta.blinkColor) {\r\n"+
            "				this.blinkTab(tabContainer, tabMeta, bg);\r\n"+
            "				tabContainer.classList.add(\"tab_blink\");\r\n"+
            "			}\r\n"+
            "      		tabContainer.style.borderBottom='';\r\n"+
            "	    }\r\n"+
            "	    tabContainer.style.background=bg;\r\n"+
            "	    tabContainer.style.color=textColor;\r\n"+
            "	}\r\n"+
            "	this.createDraggableTabs();\r\n"+
            "	this.arrowContainer.style.fontSize=toPx(fl(this.fontSize));\r\n"+
            "	this.arrowLeft.style.paddingTop=toPx(fl((this.tabHeight-this.fontSize)/2));\r\n"+
            "	this.arrowRight.style.paddingTop=toPx(fl((this.tabHeight-this.fontSize)/2));\r\n"+
            "	this.arrowLeft.style.width=toPx(this.tabHeight);\r\n"+
            "	this.arrowRight.style.width=toPx(this.tabHeight);\r\n"+
            "	this.arrowLeft.style.height=toPx(this.tabHeight);\r\n"+
            "	this.arrowRight.style.height=toPx(this.tabHeight);\r\n"+
            "	this.arrowLeft.style.position=\"relative\";\r\n"+
            "	this.arrowRight.style.position=\"relative\";\r\n"+
            "	this.arrowLeft.style.color=this.unselectTextColor;\r\n"+
            "	this.arrowRight.style.color=this.unselectTextColor;\r\n"+
            "	this.arrowLeft.style.background=this.unselectColor;\r\n"+
            "	this.arrowRight.style.background=this.unselectColor;\r\n"+
            "	\r\n"+
            "	if(this.bgColor)\r\n"+
            "		this.scrollPaneElement.style.background=this.bgColor;\r\n"+
            "	if(this.isBottom){\r\n"+
            "		this.scrollPaneElement.style.borderTopWidth=toPx(this.tabPaddingBottom);\r\n"+
            "		this.scrollPaneElement.style.borderBottomWidth=toPx(this.tabPaddingTop);\r\n"+
            "		this.scrollPaneElement.style.borderTopColor=this.borderColor;\r\n"+
            "		this.scrollPaneElement.style.borderBottomColor=\"transparent\";\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.scrollPaneElement.style.paddingTop=toPx(this.tabPaddingTop);\r\n"+
            "		this.scrollPaneElement.style.borderTopWidth=toPx(0);\r\n"+
            "		this.scrollPaneElement.style.borderBottomWidth=toPx(this.tabPaddingBottom);\r\n"+
            "		this.scrollPaneElement.style.borderBottomColor=this.borderColor;\r\n"+
            "		this.scrollPaneElement.style.borderTopColor=\"transparent\";\r\n"+
            "	}\r\n"+
            "	//New Tab Button\r\n"+
            "	var putNewTabAtEnd = true;\r\n"+
            "	var that = this;\r\n"+
            "	if(this.hasAddButton){\r\n"+
            "		this.newTabSpan.style.display=\"inline-block\";\r\n"+
            "		this.newTabSpan.style.height=toPx(this.tabHeight);\r\n"+
            "		this.newTabSpan.style.width=toPx(this.tabHeight);\r\n"+
            "		this.newTabSpan.style.borderColor=this.addButtonColor;\r\n"+
            "		this.newTabSpan.style.backgroundImage=ADD_SVG_PREFIX + encodeURIComponent(this.addButtonColor) + ADD_SVG_SUFFIX;\r\n"+
            "		if(putNewTabAtEnd){\r\n"+
            "			this.tabDiv.appendChild(this.newTabSpan);\r\n"+
            "		}\r\n"+
            "		else\r\n"+
            "			this.tabDiv.insertBefore(this.newTabSpan, this.addButtonTd);\r\n"+
            "		\r\n"+
            "//		this.newTabSpan.style.width=toPx(this.tabHeight);\r\n"+
            "		this.newTabSpan.onclick=function(e){that.onNewTab(e)};\r\n"+
            "			\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.newTabSpan.style.display=\"none\";\r\n"+
            "	}\r\n"+
            "	this.updatePosition();\r\n"+
            "}\r\n"+
            "Tabs.prototype.clearBlinkTimers=function() {\r\n"+
            "	for (var i = 0;  i < this.blinkTimers.length; i++)\r\n"+
            "		clearInterval(this.blinkTimers[i]);\r\n"+
            "	this.blinkTimers = [];\r\n"+
            "}\r\n"+
            "Tabs.prototype.blinkTab=function(tabToBlink, tabMeta, unblinkColor) {\r\n"+
            "	var count = 0;\r\n"+
            "	var blinkTimer = setInterval(function() { \r\n"+
            "		if (count == 0) {\r\n"+
            "			tabToBlink.style.backgroundColor=tabMeta.blinkColor;\r\n"+
            "			count++;\r\n"+
            "		} else if (count > 0) {\r\n"+
            "			tabToBlink.style.backgroundColor=unblinkColor;\r\n"+
            "			count = 0;\r\n"+
            "		}\r\n"+
            "	}, tabMeta.blinkPeriod);\r\n"+
            "	this.blinkTimers.push(blinkTimer);\r\n"+
            "}\r\n"+
            "Tabs.prototype.focusTab=function(){\r\n"+
            "	//Move to Active Tab\r\n"+
            "	var rp = false;\r\n"+
            "	if(this.activeTab!=null ){\r\n"+
            "		var activeTabMeta = this.tabs[this.activeTab];\r\n"+
            "		if( activeTabMeta && activeTabMeta.tabInner){\r\n"+
            "			if(this.tabLeftPos ==null){\r\n"+
            "				this.tabLeftPos = 0;\r\n"+
            "			}\r\n"+
            "			var tl = activeTabMeta.tabInner.offsetLeft;\r\n"+
            "			var tr = activeTabMeta.tabInner.offsetLeft + activeTabMeta.tabInner.offsetWidth;\r\n"+
            "			var sl = -this.tabLeftPos;\r\n"+
            "			var sr = -(this.tabLeftPos - this.scrollPaneElement.offsetWidth);\r\n"+
            "			var fullWidth = this.tabDiv.offsetWidth;\r\n"+
            "			if(this.isFarAligned){\r\n"+
            "				var ph = fullWidth - tl\r\n"+
            "				tl = fullWidth -tr;\r\n"+
            "				tr = ph;\r\n"+
            "				sl =  this.tabLeftPos;\r\n"+
            "				sr = this.tabLeftPos+this.scrollPaneElement.offsetWidth;\r\n"+
            "			}\r\n"+
            "			if(tl < sl){\r\n"+
            "				rp = true;\r\n"+
            "			}\r\n"+
            "			if(tr > sr){\r\n"+
            "				rp = true;\r\n"+
            "			}\r\n"+
            "			if(rp){\r\n"+
            "				if(abs(sl-tl)<=(sr-tr)){\r\n"+
            "					this.tabLeftPos = -tl;\r\n"+
            "				}\r\n"+
            "				else{\r\n"+
            "					this.tabLeftPos = this.scrollPaneElement.offsetWidth-tr;\r\n"+
            "				}\r\n"+
            "				\r\n"+
            "				this.scrollPane.setClipLeft(-this.tabLeftPos);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "Tabs.prototype.onSwipe=function(x,y,x2,y2){\r\n"+
            "	if(x>0 && this.activeTab>0)\r\n"+
            "	  this.contentDiv.style.left=toPx(x);\r\n"+
            "	else if(x<0 && this.activeTab<this.tabs.length-1)\r\n"+
            "	  this.contentDiv.style.left=toPx(x);\r\n"+
            "	else\r\n"+
            "	  this.contentDiv.style.left=toPx(0);\r\n"+
            "	\r\n"+
            "}\r\n"+
            "Tabs.prototype.onSwipeDone=function(x,y,x2,y2){\r\n"+
            "	if(x>100 && this.activeTab>0){\r\n"+
            "      this.callBack('tab',{tabindex:this.tabs[this.activeTab-1].tabid});\r\n"+
            "      removeAllChildren(this.contentDiv);\r\n"+
            "	}\r\n"+
            "	else if(x<-100 && this.activeTab<this.tabs.length-1){\r\n"+
            "      this.callBack('tab',{tabindex:this.tabs[this.activeTab+1].tabid});\r\n"+
            "      removeAllChildren(this.contentDiv);\r\n"+
            "	}\r\n"+
            "	this.contentDiv.style.left=toPx(0);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Tabs.prototype.showAdd=function(e){\r\n"+
            "  var that=this;\r\n"+
            "  this.callBack('s");
          out.print(
            "howAddPortletDialog',{});\r\n"+
            "}\r\n"+
            "Tabs.prototype.addChild=function(childId){\r\n"+
            "      removeAllChildren(this.contentDiv);\r\n"+
            "	if(this.contentDiv.hasChildNodes())\r\n"+
            "		alert('tab should not have multiple children visible!');\r\n"+
            "    this.contentDiv.appendChild(portletManager.getPortlet(childId).divElement);\r\n"+
            "    this.portlet.addChild(childId);\r\n"+
            "};\r\n"+
            "\r\n"+
            "Tabs.prototype.initTabs=function(showTabsOverride,allowAdd,allowMenu,addButtonText,bgColor,selectedColor,unselectedColor,selectTextColor,unselectTextColor,selectShadow,unselectShadow,isHidden,tabHeight,tabPaddingTop,tabPaddingBottom,tabSpacing,fontSize,beginningPadding,isFarAligned,isBottom,isVertical, leftRadius,rightRadius,menuArrowColor,menuArrowSize,tabFloatSize,tabPaddingStart,hasAddButton,addButtonColor,borderColor,fontFamily,selBorderColor,selBorderSize, hideArrangeTabs){\r\n"+
            "	this.maxWidth = 100;\r\n"+
            "	this.showTabsOverride = showTabsOverride;\r\n"+
            "	this.parentHeight = this.location.height;\r\n"+
            "	this.leftRadius=leftRadius;\r\n"+
            "	this.rightRadius=rightRadius;\r\n"+
            "	this.tabFloatSize=tabFloatSize;\r\n"+
            "	this.tabPaddingStart=tabPaddingStart;\r\n"+
            "	this.tabHeight=tabHeight;\r\n"+
            "	this.tabPaddingTop=tabPaddingTop;\r\n"+
            "	this.tabPaddingBottom=tabPaddingBottom;\r\n"+
            "	this.tabSpacing=tabSpacing;\r\n"+
            "	this.fontSize=fontSize;\r\n"+
            "	this.beginningPadding=beginningPadding;\r\n"+
            "	this.isFarAligned=isFarAligned;\r\n"+
            "	this.isBottom=isBottom;\r\n"+
            "	this.isVertical=isVertical;\r\n"+
            "	this.selectShadow=selectShadow;\r\n"+
            "	this.unselectShadow=unselectShadow;\r\n"+
            "	this.isHidden=isHidden;\r\n"+
            "	this.bgColor=bgColor;\r\n"+
            "	this.selBorderColor=selBorderColor;\r\n"+
            "	this.selBorderSize=selBorderSize;\r\n"+
            "//    this.tabDiv.style.bottom=this.tabDiv.style.top=this.tabDiv.style.left=this.tabDiv.style.right=this.contentDiv.style.bottom=this.contentDiv.style.top=this.contentDiv.style.left=this.contentDiv.style.right='0px';\r\n"+
            "    this.allowAdd=allowAdd;\r\n"+
            "    this.allowMenu=allowMenu;\r\n"+
            "    this.selectedColor=selectedColor;\r\n"+
            "//    this.updatePosition();\r\n"+
            "    this.menuArrowColor=menuArrowColor;\r\n"+
            "    this.menuArrowSize=menuArrowSize;\r\n"+
            "    this.divElement.style.overflow= \"visible\";\r\n"+
            "//    if(this.bgColor != null)\r\n"+
            "//    	this.divElement.style.background = this.bgColor;\r\n"+
            "    if(selectedColor != null)\r\n"+
            "    	this.selectColor = selectedColor;\r\n"+
            "    if(unselectedColor != null)\r\n"+
            "    	this.unselectColor = unselectedColor;\r\n"+
            "    if(selectTextColor != null)\r\n"+
            "    	this.selectTextColor = selectTextColor;\r\n"+
            "    if(unselectTextColor != null)\r\n"+
            "    	this.unselectTextColor = unselectTextColor;\r\n"+
            "    this.borderColor = borderColor;\r\n"+
            "    this.hasAddButton=hasAddButton;\r\n"+
            "    this.addButtonColor = addButtonColor;\r\n"+
            "    this.menuImageSelected=SVG_PREFIX+'x=\"0px\" y=\"0px\" viewBox=\"0 0 15 15\" style=\"enable-background:new 0 0 15 15;\" xml:space=\"preserve\"> <g fill=\"'+(this.menuArrowColor!=null ? this.menuArrowColor : this.selectTextColor).replace('#',\"%23\")+'\"><polygon points=\"7.6,15 3.9,7.6 0.2,0.2 7.6,0.2 14.9,0.2 11.2,7.6\"/> </g> '+SVG_SUFFIX;\r\n"+
            "    this.menuImageUnselected=SVG_PREFIX+'x=\"0px\" y=\"0px\" viewBox=\"0 0 15 15\" style=\"enable-background:new 0 0 15 15;\" xml:space=\"preserve\"> <g fill=\"'+(this.menuArrowColor!=null ? this.menuArrowColor : this.unselectTextColor).replace('#',\"%23\")+'\"><polygon points=\"7.6,15 3.9,7.6 0.2,0.2 7.6,0.2 14.9,0.2 11.2,7.6\"/> </g> '+SVG_SUFFIX;\r\n"+
            "	this.tabs=[];\r\n"+
            "	this.fontFamily=fontFamily;\r\n"+
            "	this.hideArrangeTabs = hideArrangeTabs;\r\n"+
            "//	this.tabsInner=[];\r\n"+
            "//	this.tabsText=[];\r\n"+
            "//	this.tabsMenu=[];\r\n"+
            "};\r\n"+
            "\r\n"+
            "Tabs.prototype.updateScrolling=function(tabsWidth,fullHeight){\r\n"+
            "	//For Scrolling\r\n"+
            "	var fullWidth = this.tabDiv.offsetWidth;\r\n"+
            "	var tabsWidth = this.isVertical?this.location.height:this.location.width;\r\n"+
            "	if(fullWidth <= tabsWidth)\r\n"+
            "		this.hasArrowButtons = false;\r\n"+
            "	else \r\n"+
            "		this.hasArrowButtons = true;\r\n"+
            "	\r\n"+
            "	if(this.hasArrowButtons){\r\n"+
            "		this.arrowContainer.style.display=\"block\";\r\n"+
            "		this.scrollPaneElement.style.width=toPx(tabsWidth-2*(this.tabHeight)-this.tabSpacing);\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.arrowContainer.style.display=\"none\";\r\n"+
            "		this.scrollPaneElement.style.width=toPx(tabsWidth);\r\n"+
            "	}\r\n"+
            "		\r\n"+
            "//	this.scrollPaneElement.style.marginTop=toPx(this.tabPaddingTop);\r\n"+
            "	this.scrollPaneElement.style.height=toPx(noNaN(this.tabHeight,0));//+noNaN(this.tabPaddingTop,0));\r\n"+
            "	this.arrowContainer.style.right=\"0px\";\r\n"+
            "	if(this.isBottom){\r\n"+
            "		this.arrowContainer.style.bottom=\"unset\";\r\n"+
            "		this.arrowContainer.style.top=\"0px\";\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.arrowContainer.style.top=\"unset\";\r\n"+
            "		this.arrowContainer.style.bottom=\"0px\";\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.arrowContainer.style.width=toPx(this.tabHeight*2);\r\n"+
            "	this.arrowContainer.style.height=toPx(this.tabHeight);\r\n"+
            "	if (!this.isHidden){\r\n"+
            "		//Set the height, and scroll size of scrollpane\r\n"+
            "//	    this.tabDiv.style.height=toPx(fullHeight);\r\n"+
            "//	    this.scrollPane.DOM.paneElement.width=toPx(tabsWidth);\r\n"+
            "    	this.scrollPane.hscroll.setClipLength(tabsWidth);\r\n"+
            "    	var fullWidth = this.tabDiv.offsetWidth;\r\n"+
            "    	if(this.hasArrowButtons)\r\n"+
            "    		this.scrollPane.hscroll.setPaneLength(fullWidth+2*(this.tabHeight)+this.tabSpacing);\r\n"+
            "    	else\r\n"+
            "    		this.scrollPane.hscroll.setPaneLength(fullWidth);\r\n"+
            "		\r\n"+
            "	}\r\n"+
            "	//End for Scrolling\r\n"+
            "}\r\n"+
            "Tabs.prototype.updatePosition=function(){\r\n"+
            "	var fullHeight=this.isHidden ? 0 : (noNaN(this.tabHeight,0)+noNaN(this.tabPaddingTop,0)+noNaN(this.tabPaddingBottom));\r\n"+
            "	var fullHeightPx=toPx(fullHeight);\r\n"+
            "	var contentOffsetPx=this.tabFloatSize!=-1 ? '0px' : fullHeightPx;\r\n"+
            "	var tabsWidth = this.location.width;\r\n"+
            "	if(this.isVertical)\r\n"+
            "		tabsWidth = this.location.height;\r\n"+
            "	else\r\n"+
            "		tabsWidth = this.location.width;\r\n"+
            "	\r\n"+
            "	if (!this.isHidden){\r\n"+
            "    	\r\n"+
            "    	// Align to the right, bottom\r\n"+
            "		if(this.isFarAligned){\r\n"+
            "			this.scrollPaneElement.style.display=\"inline-flex\";\r\n"+
            "			this.scrollPaneElement.style.flexDirection=\"row-reverse\";\r\n"+
            "			this.tabDiv.style.paddingRight=toPx(this.beginningPadding+this.tabPaddingStart);\r\n"+
            "			this.tabDiv.style.paddingLeft=null;\r\n"+
            "		}\r\n"+
            "		else{\r\n"+
            "			this.scrollPaneElement.style.display=\"flex\";\r\n"+
            "			this.scrollPaneElement.style.flexDirection=\"row\";\r\n"+
            "			this.tabDiv.style.paddingLeft=toPx(this.beginningPadding + this.tabPaddingStart);\r\n"+
            "			this.tabDiv.style.paddingRight=null;\r\n"+
            "		}\r\n"+
            "		var offsetPos = this.tabFloatSize != -1? 0: -fullHeight;\r\n"+
            "		if(this.isBottom){\r\n"+
            "			if(this.isVertical){\r\n"+
            "				this.contentDiv.style.right=contentOffsetPx;\r\n"+
            "				this.tabDiv.style.paddingLeft=\"25px\"; // the size of the bgb\r\n"+
            "		    	rotateDivElement(this.tabsBar,-90,fullHeight,tabsWidth,this.location.width+offsetPos,0);\r\n"+
            "			}else{\r\n"+
            "				this.contentDiv.style.bottom=contentOffsetPx;\r\n"+
            "		    	rotateDivElement(this.tabsBar,0,tabsWidth,fullHeight,0,this.location.height+offsetPos);\r\n"+
            "			}\r\n"+
            "		}else{\r\n"+
            "			var offsetPos = this.tabFloatSize == -1? 0: -fullHeight;\r\n"+
            "			if(this.isVertical){\r\n"+
            "				this.contentDiv.style.left=contentOffsetPx;\r\n"+
            "				this.tabDiv.style.paddingLeft=\"25px\"; // the size of the bgb\r\n"+
            "				this.tabDiv.style.right=null;\r\n"+
            "		    	rotateDivElement(this.tabsBar,-90,fullHeight,tabsWidth,offsetPos,0);\r\n"+
            "			}else{\r\n"+
            "			    this.contentDiv.style.top=contentOffsetPx;\r\n"+
            "				\r\n"+
            "			    this.tabDiv.style.bottom=null;\r\n"+
            "		    	rotateDivElement(this.tabsBar,0,tabsWidth,fullHeight,0,offsetPos);\r\n"+
            "			}\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	this.updateScrolling(tabsWidth,fullHeight);\r\n"+
            "}\r\n"+
            "Tabs.prototype.setSize=function(width,height){\r\n"+
            "  this.portlet.setSize(width,height);\r\n"+
            "  this.updatePosition(); \r\n"+
            "};\r\n"+
            "\r\n"+
            "Tabs.prototype.rp=function(){\r\n"+
            "    removeAllChildren(this.tabDiv);\r\n"+
            "    if(this.isHidden == false){\r\n"+
            "    	this.tabsBar.style.display=\"inline-block\";\r\n"+
            "    	this.createTabsElementsDom();\r\n"+
            "    }\r\n"+
            "    else\r\n"+
            "    	this.tabsBar.style.display=\"none\";\r\n"+
            "    	\r\n"+
            "}\r\n"+
            "Tabs.prototype.setActiveTab=function(tabIndex){\r\n"+
            "  this.activeTab=tabIndex;\r\n"+
            "};\r\n"+
            "\r\n"+
            "Tabs.prototype.addTab=function(location,tabName, tabNameHtml,allowTitleEdit,selectColor,unselectColor,selectTextColor,unselectTextColor,isHidden,blinkColor,blinkPeriod,hids){\r\n"+
            "  //if(this.tabs[location]){\r\n"+
            "	  //this.tabs[location].tabName = tabName;\r\n"+
            "	  //this.tabs[location].tabNameHtml = tabNameHtml;\r\n"+
            "    //this.tabs[location].element.innerHTML=tabNameHtml;\r\n"+
            "    //this.tabs[location].element.id=hids;\r\n"+
            "    //return;\r\n"+
            "  //}\r\n"+
            "  var tabMeta = {};\r\n"+
            "  tabMeta.tabName = tabName;\r\n"+
            "  tabMeta.tabNameHtml = tabNameHtml;\r\n"+
            "  tabMeta.selectColor=selectColor;\r\n"+
            "  tabMeta.unselectColor=unselectColor;\r\n"+
            "  tabMeta.selectTextColor=selectTextColor;\r\n"+
            "  tabMeta.unselectTextColor=unselectTextColor;\r\n"+
            "  tabMeta.blinkColor=blinkColor;\r\n"+
            "  tabMeta.blinkPeriod= blinkPeriod == null ? 100 : Math.abs(blinkPeriod);\r\n"+
            "  tabMeta.isHidden = isHidden;\r\n"+
            "  tabMeta.tabid=location;\r\n"+
            "  tabMeta.allowTitleEdit=allowTitleEdit;\r\n"+
            "  tabMeta.hids=hids;\r\n"+
            "  this.tabs[location]=tabMeta;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "Tabs.prototype.onNewTab=function(e){\r\n"+
            "  this.callBack('newTab',{});\r\n"+
            "};\r\n"+
            "Tabs.prototype.onTabSelected=function(e){\r\n"+
            "  this.callBack('tab',{tabindex:getMouseTarget(e).tabid});\r\n"+
            "};\r\n"+
            "Tabs.prototype.onTabClicked=function(e){\r\n"+
            "	var prevTab=this.activeTab;\r\n"+
            "	this.callBack('tabClick',{tabindex:getMouseTarget(e).tabid,prevTab:prevTab});\r\n"+
            "};\r\n"+
            "Tabs.prototype.onMenu=function(e){\r\n"+
            "	var prevTab=this.activeTab;\r\n"+
            "  this.callBack('onMenu',{tabindex:getMouseTarget(e).tabid,prevTab:prevTab,x:this.portlet.owningWindow.MOUSE_POSITION_X,y:this.portlet.owningWindow.MOUSE_POSITION_Y});\r\n"+
            "};\r\n"+
            "");

	}
	
}