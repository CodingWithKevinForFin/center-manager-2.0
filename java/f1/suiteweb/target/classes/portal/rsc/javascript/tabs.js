//#######################
//##### Tabs #####


function Tabs(portlet,parentId){
    var that = this;
    this.portlet=portlet;
    this.location=this.portlet.location;
    this.divElement = this.portlet.divElement;
    
	this.allowAdd=true;
	this.tabs=[];
	this.blinkTimers=[];
	this.isContainer=true;
	this.hasArrowButtons = true;
    
	this.selectColor='#aaaaaa';
	this.unselectColor='#dddddd';
	this.selectTextColor='#000000';
	this.unselectTextColor='#000000'
		
	this.initDOM();
	this.tabLeftPos=null;
}

var ADD_SVG_PREFIX=SVG_PREFIX+
	'viewBox="0 0 1000 1000" ><path style="fill:' ;
var ADD_SVG_SUFFIX=';" d="M729.471,457.513h-186.991V270.519c0-23.473-19.006-42.498-42.479-42.498 c-23.473,0-42.498,19.006-42.498,42.498v186.991H270.512c-23.473,0-42.479,19.006-42.479,42.498c0,23.473,19.006,42.479,42.479,42.479 h186.975v187.011c0,23.476,19.022,42.479,42.498,42.479c23.473,0,42.498-19.006,42.498-42.479v-187.011h186.975 c23.476,0,42.498-19.006,42.498-42.479C771.97,476.516,752.944,457.513,729.471,457.513z"'+
	'/>'+ SVG_SUFFIX;





Tabs.prototype.initDOM=function(){
	var that = this;
	this.tabDiv=nw('div','portal_tabpane');
	
	this.scrollPaneElement=nw('div','portal_tabScrollPane');
	
	this.scrollPane=new ScrollPane(this.scrollPaneElement,0,this.tabDiv);
	this.scrollPane.isvscrollDefault=false;
	this.scrollPane.DOM.paneElement.style.background="unset";
	this.scrollPane.DOM.paneElement.style.overflow="unset";
	this.scrollPane.onScroll=function(){that.onScroll()};
	
	this.contentDiv=nw('div');
	this.contentDiv.style.left='0px';
	this.contentDiv.style.right='0px';
	this.contentDiv.style.bottom='0px';
	this.contentDiv.onSwipe=function(x,y,x2,y2){that.onSwipe(x,y,x2,y2);};
	this.contentDiv.onSwipeDone=function(x,y,x2,y2){that.onSwipeDone(x,y,x2,y2);};
	
	this.newTabSpan=nw('span','portal_tab portal_tab_newtabicon');
	
	this.arrowContainer=nw('div','portal_arrows');
	this.arrowLeft=nw('div','portal_tab portal_tabArrow');
	this.arrowLeft.innerHTML="<abbr title='Previous Tab'>&#8249;</abbr>";
	this.arrowLeft.onclick=function(){that.onPrev();};
	this.arrowRight=nw('div','portal_tab portal_tabArrow');
	this.arrowRight.innerHTML="<abbr title='Next Tab'>&#8250;</abbr>";
	this.arrowRight.onclick=function(){that.onNext();};
	this.tabsBar=nw('div','portal_tabsBar');
	
	this.divElement.style.background='white';
	
	this.tabsBar.appendChild(this.scrollPaneElement);
	this.tabsBar.appendChild(this.arrowContainer);
	this.arrowContainer.appendChild(this.arrowLeft);
	this.arrowContainer.appendChild(this.arrowRight);
	this.divElement.appendChild(this.contentDiv);
	this.divElement.appendChild(this.tabsBar);
}
Tabs.prototype.onPrev=function(){
	if(this.activeTab > 0){
		//Get tab thats not hidden
		var nextTab = this.activeTab-1;
		var find = true;
		while(find){
			if(this.tabs[nextTab].isHidden == false)
				find = false;
			else{
				if(nextTab > 0){
					nextTab--;
				}
				else
					return;
			}
		}
	    this.callBack('tab',{tabindex:this.tabs[nextTab].tabid});
	    removeAllChildren(this.contentDiv);
	}
}

Tabs.prototype.onNext=function(){
	if(this.activeTab < this.tabs.length-1){
		//Get tab thats not hidden
		var nextTab = this.activeTab+1;
		var find = true;
		while(find){
			if(this.tabs[nextTab].isHidden == false)
				find = false;
			else{
				if(nextTab < this.tabs.length-1){
					nextTab++;
				}
				else
					return;
			}
		}
		
		this.callBack('tab',{tabindex:this.tabs[nextTab].tabid});
		removeAllChildren(this.contentDiv);
	}
}

Tabs.prototype.onScroll=function(){
	var fullWidth = this.tabDiv.offsetWidth;
	var tabsWidth = this.isVertical?this.location.height:this.location.width;
	//Reset position if no need for scrolling
	if(fullWidth <= tabsWidth){
		this.tabLeftPos = 0;
		this.scrollPane.DOM.innerpaneElement.style.left=toPx(this.tabLeftPos);
		this.scrollPane.DOM.innerpaneElement.style.right=null;
		return;
	}
	
	var pos = this.scrollPane.getClipLeft(); 
	pos = min(pos,fullWidth-(tabsWidth-2*this.tabHeight));
	if(this.isFarAligned)
		pos = - pos;
	
	this.tabLeftPos = -pos;
	this.scrollPane.DOM.innerpaneElement.style.left=toPx(this.tabLeftPos);
	this.scrollPane.DOM.innerpaneElement.style.right=null;
}
Tabs.prototype.createMenu=function(i, tabid){
	//Create element
	var tabMenu=nw('span','portal_tab_menu');
	this.tabs[i].tabMenu = tabMenu;
	tabMenu.tabid=tabid;
	
    //Add to dom
	this.tabs[i].tabInner.appendChild(tabMenu);
	
    //Add onclick for menu
	var that = this;
	tabMenu.onclick=function(e){that.onMenu(e); e.stopPropagation();};
	
	//Default style
	tabMenu.style.position = "relative";
	tabMenu.style.display = "inline";
	tabMenu.style.top = "0px";
	//	    tabMenu.style.right = "4px";	
	//		tabMenu.style.float = "right";
	//Custom Style
	tabMenu.style.backgroundImage=this.menuImageUnselected;
	tabMenu.style.backgroundSize=toPx(this.menuArrowSize);
	tabMenu.style.padding=this.menuArrowSize+"px";
}
Tabs.prototype.createDraggableTabs=function(){
	var dragDoc = getDocument(this.tabDiv);
	var that = this;
	var onDragEndFunc = function(oldIdx, newIdx){
		that.callBack('moveTab',{tabindex:oldIdx, nwtabindex:newIdx});
	};
	var getTabId = function(draggableElement){
		var loc = draggableElement.__idx;
		loc += draggableElement.__indexX - draggableElement.__origIndexX;

//		liveDebugger.onStat("getTabId", loc);
		return loc;
	};

	var getMousePos = function(event){
		var tabDivRect = that.tabDiv.getBoundingClientRect();
		var rect = this.calcContainerRect();
		var r = {};

		if(that.isVertical == false) { // Horizontal
			if(that.isFarAligned == false){ // Horizontal Left Align
				r.x = that.scrollPane.getClipLeft() + event.x - rect.left; 
				r.y = 0;
			}
			else if (that.isFarAligned == true){ // Horizontal Right Align
				r.x = (event.x - rect.left) + (rect.left - tabDivRect.left); 
				r.y = 0;
			}
		}
		else{
			if(that.isFarAligned == false){ // Horizontal Left Align // Bottom
				r.x = that.scrollPane.getClipLeft() + rect.height - (event.y - rect.top);
				r.y = 0;
			}
			else if (that.isFarAligned == true){ // Vertical Top Align // Top
				r.x = (rect.top + tabDivRect.height)  - event.y ;
				r.y = 0;
			}
		}
//		liveDebugger.onStat("mousePos",JSON.stringify(r));
		return r;
	};

	if(this.draggableContainer)
		this.draggableContainer.clear();
	

	var calcContainerRect = function(){
		var tabBarRect = that.tabsBar.getBoundingClientRect();
		return tabBarRect;
	};
	
	var onMousePositionHandler= function(absMousePos, absContainerPos,  mouseOffset, elementWidth, moveX, moveY){
		if(moveY)
			return;
		
		var origContainerPos;
		
		if(that.isVertical == false) { // Horizontal
			var diffPos = absMousePos.x - absContainerPos.x;

			var relContainerPos = that.scrollPane.getClipLeft(); 
			var containerSize = that.scrollPane.getClipWidth();
			
			origContainerPos = relContainerPos; 
			if(that.isFarAligned == false){ // Working Bottom
				if(diffPos <= (elementWidth/2)){
					var np = relContainerPos + containerSize * -0.018;
					that.scrollPane.setClipLeft(np);
				}
				else if(diffPos >= (containerSize - elementWidth/2)){
					var np = relContainerPos + containerSize * +0.018;
					that.scrollPane.setClipLeft(np);
				}
			}
			else if(that.isFarAligned == true){ // Working Top
				if(diffPos <= (elementWidth/2)){
					var np = relContainerPos + containerSize * +0.018;
					that.scrollPane.setClipLeft(np);
				}
				else if(diffPos >= (containerSize - elementWidth/2)){
					var np = relContainerPos + containerSize * -0.018;
					that.scrollPane.setClipLeft(np);
				}
			}
		}
		else if(that.isVertical == true) { // Vertical
			
			var relContainerPos = that.scrollPane.getClipLeft(); 
			var containerSize = that.scrollPane.getClipWidth();

			var diffPos = (absContainerPos.y + containerSize) - absMousePos.y;
			origContainerPos = relContainerPos; 

			if(that.isFarAligned == false){ // Working //Bottom
				if(diffPos <= (elementWidth/2)){
					var np = relContainerPos + containerSize * -0.018;
					that.scrollPane.setClipLeft(np);
				}
				else if(diffPos >= (containerSize - elementWidth/2)){
					var np = relContainerPos + containerSize * +0.018;
					that.scrollPane.setClipLeft(np);
				}
			}
			else if(that.isFarAligned == true){ // Working //Top
				if(diffPos <= (elementWidth/2)){
					var np = relContainerPos + containerSize * +0.018;
					that.scrollPane.setClipLeft(np);
				}
				else if(diffPos >= (containerSize - elementWidth/2)){
					var np = relContainerPos + containerSize * -0.018;
					that.scrollPane.setClipLeft(np);
				}
			}
		}
		
		var moved = relContainerPos != origContainerPos;
		return moved;
	};

	this.draggableContainer = new ContainerWithDraggableElements(this.tabDiv, calcContainerRect, null, onDragEndFunc, onMousePositionHandler, null, getTabId, getMousePos, true, true, false);


	var posOffset = 0;
	var ntabs = this.tabs.length;
	var flip = this.isFarAligned == true;
				
	// Tabs start padding and offset
	if(flip == false){
		posOffset += this.tabPaddingStart + abs(this.beginningPadding);
	}

	for(var i = 0; i != ntabs;i++){
		var tabMeta = this.tabs[i];
		// if tab is hidden and not in dev mode
		if(tabMeta.isHidden == true && !(this.showTabsOverride))
			continue;

			var tabContainer = tabMeta.tabInner;
			var tabRect = tabContainer.getBoundingClientRect();
	
			var calcWidth = null;
			var calcHeight = null;
			var calcLeft = null;
			var calcTop = null;
	
			if(this.isVertical == false ){
				calcWidth = tabRect.width;
				calcHeight = tabRect.height;
				calcTop = 0;
				
				// Tabs spacing
				if(flip == true)
				posOffset += this.tabSpacing;
	
				calcLeft = posOffset;
				
				posOffset += tabRect.width;
				// Tabs spacing
				if(flip == false)
				posOffset += this.tabSpacing;
	
			}
			else if(this.isVertical == true ){ // TOP Vertical
				calcWidth = tabRect.height;
				calcHeight = tabRect.width;
				
				calcTop = 0;
	
				if(flip == true)
				posOffset += this.tabSpacing;
	
				calcLeft = posOffset;
				posOffset += tabRect.height; 
	
	
				// Tabs spacing
				if(flip == false)
				posOffset += this.tabSpacing;
			}
	
			var relTabRect = new Rect(calcLeft,calcTop,calcWidth,calcHeight);
			var calcTabRect = function(){
				return relTabRect;
			}
			if(this.hideArrangeTabs == true){
				var de = new DraggableElement(this.draggableContainer, tabContainer, tabContainer, calcTabRect, tabMeta.tabid);
				this.draggableContainer.addDraggableElement(de);
			}
			else{
				tabContainer.style.position="absolute";
				tabContainer.style.left=toPx(relTabRect.left);
				tabContainer.style.top=toPx(relTabRect.top);
				tabContainer.style.width=toPx(relTabRect.width);
				tabContainer.style.height=toPx(relTabRect.height);
			}
	}

	// Tabs start padding and offset
	if(flip == true ){
		posOffset += this.tabPaddingStart + abs(this.beginningPadding);
	}
//	liveDebugger.onStat("tabDivWidth", JSON.stringify(posOffset));
	this.tabDiv.style.width=toPx(abs(posOffset));
}
Tabs.prototype.createTabsElementsDom=function(){
	this.tabDiv.style.fontSize=toPx(fl(this.fontSize));
	this.tabDiv.style.fontFamily=this.fontFamily;
	this.clearBlinkTimers();
	for(var i = 0; i < this.tabs.length; i++){
		var tabMeta = this.tabs[i];
		var isHidden = tabMeta.isHidden;
		// if tab is hidden and not in dev mode
		if(isHidden == true && !(this.showTabsOverride))
			continue;
		var tabName = tabMeta.tabName;
		var tabNameHtml = tabMeta.tabNameHtml;
		var tabid = tabMeta.tabid;
		
		//Create elements
		var tabContainer = nw('span', 'portal_tab');
		var tabText = nw('abbr');
	    this.tabs[i].tabInner = tabContainer;
	    this.tabs[i].tabText = tabText;
	    tabContainer.tabid=tabid;
	    tabText.tabid=tabid;
	    
	    //Add to dom
	    tabContainer.appendChild(tabText);
	    this.tabDiv.appendChild(tabContainer);
	    
	    if(this.allowMenu)
	    	this.createMenu(i, tabid);
	    
	    //Set html and hoverover
	    tabText.title = tabName;
	    tabText.innerHTML=tabNameHtml;
	    tabContainer.id=tabMeta.hids;
	    
	    
	    //Default Style
	    tabText.style.textDecoration="none";
	  	tabText.style.display="inline-block";
		tabText.style.overflow="hidden";
	    if (isHidden==true && this.showTabsOverride) {
	    	tabText.innerHTML=tabNameHtml + "&nbsp;(Hidden)";
	    	tabText.style.opacity=.6;
		}
	    
	    //User Styles
		tabContainer.style.height=toPx(this.tabHeight);
		tabContainer.style.paddingTop=toPx(fl((this.tabHeight-this.fontSize)/2));
		tabContainer.style.paddingLeft=toPx(clip(this.leftRadius,2,10));
		tabContainer.style.paddingRight=toPx(clip(this.rightRadius,2,10));
//		if(this.isFarAligned)
//			tabContainer.style.marginLeft=toPx(this.tabSpacing);
//		else
		tabContainer.style.marginRight=toPx(this.tabSpacing);
		
		
	    if(this.isBottom){
	    	tabContainer.style.borderBottomLeftRadius=toPx(this.leftRadius);
	    	tabContainer.style.borderBottomRightRadius=toPx(this.rightRadius);
	    }else{
	    	tabContainer.style.borderTopLeftRadius=toPx(this.leftRadius);
	    	tabContainer.style.borderTopRightRadius=toPx(this.rightRadius);
	    }
	    
	    
	    var bg;
	    var textColor;
	    var f=function(e){that.onTabSelected(e);that.onTabClicked(e);};
	    var f2=function(e){that.onTabClicked(e);};
	    var that = this;
	    if(i==this.activeTab){
	    	tabContainer.onclick=f2;
	    	//      makeEditable(tabText,tab.allowTitleEdit);
	    	tabContainer.ondblclick=function(){
	    		if(amiEditDesktopArgs!=null && amiEditDesktopArgs.edit == true)
	    			that.callBack('renameTabDiag',{tabindex:this.tabid});
	    	};
	    	tabText.onEdit=function(old,nuw){ 
	    		that.callBack('renameTab',{tabindex:this.tabid,text:nuw});
	    	};
	    	if(tabMeta.selectColor)
	    		bg=tabMeta.selectColor;
	    	else
	    		bg=this.selectColor;
      
	    	if(tabMeta.selectTextColor)
	    		textColor=tabMeta.selectTextColor;
	    	else
	    		textColor=this.selectTextColor;
      		
      		if (tabMeta.blinkColor) {
				this.blinkTab(tabContainer, tabMeta, bg);
				tabContainer.classList.add("tab_blink");
			}

      		if (this.selBorderSize && this.selBorderColor) {
      			tabContainer.style.borderBottom=this.selBorderSize + 'px solid ' + this.selBorderColor;
      		}
	    	tabContainer.style.boxShadow=this.selectShadow;
	    	tabContainer.style.zIndex=2;
	    }else{
	    	tabContainer.onclick=f;
	    	tabContainer.style.boxShadow=this.unselectShadow;
	    	tabContainer.style.zIndex=1;
       
	    	if(tabMeta.unselectColor)
	    		bg=tabMeta.unselectColor;
	    	else
	    		bg=this.unselectColor;
      
	    	if(tabMeta.unselectTextColor)
	    		textColor=tabMeta.unselectTextColor;
	    	else
	    		textColor=this.unselectTextColor;
	    		
      		if (tabMeta.blinkColor) {
				this.blinkTab(tabContainer, tabMeta, bg);
				tabContainer.classList.add("tab_blink");
			}
      		tabContainer.style.borderBottom='';
	    }
	    tabContainer.style.background=bg;
	    tabContainer.style.color=textColor;
	}
	this.createDraggableTabs();
	this.arrowContainer.style.fontSize=toPx(fl(this.fontSize));
	this.arrowLeft.style.paddingTop=toPx(fl((this.tabHeight-this.fontSize)/2));
	this.arrowRight.style.paddingTop=toPx(fl((this.tabHeight-this.fontSize)/2));
	this.arrowLeft.style.width=toPx(this.tabHeight);
	this.arrowRight.style.width=toPx(this.tabHeight);
	this.arrowLeft.style.height=toPx(this.tabHeight);
	this.arrowRight.style.height=toPx(this.tabHeight);
	this.arrowLeft.style.position="relative";
	this.arrowRight.style.position="relative";
	this.arrowLeft.style.color=this.unselectTextColor;
	this.arrowRight.style.color=this.unselectTextColor;
	this.arrowLeft.style.background=this.unselectColor;
	this.arrowRight.style.background=this.unselectColor;
	
	if(this.bgColor)
		this.scrollPaneElement.style.background=this.bgColor;
	if(this.isBottom){
		this.scrollPaneElement.style.borderTopWidth=toPx(this.tabPaddingBottom);
		this.scrollPaneElement.style.borderBottomWidth=toPx(this.tabPaddingTop);
		this.scrollPaneElement.style.borderTopColor=this.borderColor;
		this.scrollPaneElement.style.borderBottomColor="transparent";
	}
	else{
		this.scrollPaneElement.style.paddingTop=toPx(this.tabPaddingTop);
		this.scrollPaneElement.style.borderTopWidth=toPx(0);
		this.scrollPaneElement.style.borderBottomWidth=toPx(this.tabPaddingBottom);
		this.scrollPaneElement.style.borderBottomColor=this.borderColor;
		this.scrollPaneElement.style.borderTopColor="transparent";
	}
	//New Tab Button
	var putNewTabAtEnd = true;
	var that = this;
	if(this.hasAddButton){
		this.newTabSpan.style.display="inline-block";
		this.newTabSpan.style.height=toPx(this.tabHeight);
		this.newTabSpan.style.width=toPx(this.tabHeight);
		this.newTabSpan.style.borderColor=this.addButtonColor;
		this.newTabSpan.style.backgroundImage=ADD_SVG_PREFIX + encodeURIComponent(this.addButtonColor) + ADD_SVG_SUFFIX;
		if(putNewTabAtEnd){
			this.tabDiv.appendChild(this.newTabSpan);
		}
		else
			this.tabDiv.insertBefore(this.newTabSpan, this.addButtonTd);
		
//		this.newTabSpan.style.width=toPx(this.tabHeight);
		this.newTabSpan.onclick=function(e){that.onNewTab(e)};
			
	}
	else{
		this.newTabSpan.style.display="none";
	}
	this.updatePosition();
}
Tabs.prototype.clearBlinkTimers=function() {
	for (var i = 0;  i < this.blinkTimers.length; i++)
		clearInterval(this.blinkTimers[i]);
	this.blinkTimers = [];
}
Tabs.prototype.blinkTab=function(tabToBlink, tabMeta, unblinkColor) {
	var count = 0;
	var blinkTimer = setInterval(function() { 
		if (count == 0) {
			tabToBlink.style.backgroundColor=tabMeta.blinkColor;
			count++;
		} else if (count > 0) {
			tabToBlink.style.backgroundColor=unblinkColor;
			count = 0;
		}
	}, tabMeta.blinkPeriod);
	this.blinkTimers.push(blinkTimer);
}
Tabs.prototype.focusTab=function(){
	//Move to Active Tab
	var rp = false;
	if(this.activeTab!=null ){
		var activeTabMeta = this.tabs[this.activeTab];
		if( activeTabMeta && activeTabMeta.tabInner){
			if(this.tabLeftPos ==null){
				this.tabLeftPos = 0;
			}
			var tl = activeTabMeta.tabInner.offsetLeft;
			var tr = activeTabMeta.tabInner.offsetLeft + activeTabMeta.tabInner.offsetWidth;
			var sl = -this.tabLeftPos;
			var sr = -(this.tabLeftPos - this.scrollPaneElement.offsetWidth);
			var fullWidth = this.tabDiv.offsetWidth;
			if(this.isFarAligned){
				var ph = fullWidth - tl
				tl = fullWidth -tr;
				tr = ph;
				sl =  this.tabLeftPos;
				sr = this.tabLeftPos+this.scrollPaneElement.offsetWidth;
			}
			if(tl < sl){
				rp = true;
			}
			if(tr > sr){
				rp = true;
			}
			if(rp){
				if(abs(sl-tl)<=(sr-tr)){
					this.tabLeftPos = -tl;
				}
				else{
					this.tabLeftPos = this.scrollPaneElement.offsetWidth-tr;
				}
				
				this.scrollPane.setClipLeft(-this.tabLeftPos);
			}
		}
	}
}
Tabs.prototype.onSwipe=function(x,y,x2,y2){
	if(x>0 && this.activeTab>0)
	  this.contentDiv.style.left=toPx(x);
	else if(x<0 && this.activeTab<this.tabs.length-1)
	  this.contentDiv.style.left=toPx(x);
	else
	  this.contentDiv.style.left=toPx(0);
	
}
Tabs.prototype.onSwipeDone=function(x,y,x2,y2){
	if(x>100 && this.activeTab>0){
      this.callBack('tab',{tabindex:this.tabs[this.activeTab-1].tabid});
      removeAllChildren(this.contentDiv);
	}
	else if(x<-100 && this.activeTab<this.tabs.length-1){
      this.callBack('tab',{tabindex:this.tabs[this.activeTab+1].tabid});
      removeAllChildren(this.contentDiv);
	}
	this.contentDiv.style.left=toPx(0);
}

Tabs.prototype.showAdd=function(e){
  var that=this;
  this.callBack('showAddPortletDialog',{});
}
Tabs.prototype.addChild=function(childId){
      removeAllChildren(this.contentDiv);
	if(this.contentDiv.hasChildNodes())
		alert('tab should not have multiple children visible!');
    this.contentDiv.appendChild(portletManager.getPortlet(childId).divElement);
    this.portlet.addChild(childId);
};

Tabs.prototype.initTabs=function(showTabsOverride,allowAdd,allowMenu,addButtonText,bgColor,selectedColor,unselectedColor,selectTextColor,unselectTextColor,selectShadow,unselectShadow,isHidden,tabHeight,tabPaddingTop,tabPaddingBottom,tabSpacing,fontSize,beginningPadding,isFarAligned,isBottom,isVertical, leftRadius,rightRadius,menuArrowColor,menuArrowSize,tabFloatSize,tabPaddingStart,hasAddButton,addButtonColor,borderColor,fontFamily,selBorderColor,selBorderSize, hideArrangeTabs){
	this.maxWidth = 100;
	this.showTabsOverride = showTabsOverride;
	this.parentHeight = this.location.height;
	this.leftRadius=leftRadius;
	this.rightRadius=rightRadius;
	this.tabFloatSize=tabFloatSize;
	this.tabPaddingStart=tabPaddingStart;
	this.tabHeight=tabHeight;
	this.tabPaddingTop=tabPaddingTop;
	this.tabPaddingBottom=tabPaddingBottom;
	this.tabSpacing=tabSpacing;
	this.fontSize=fontSize;
	this.beginningPadding=beginningPadding;
	this.isFarAligned=isFarAligned;
	this.isBottom=isBottom;
	this.isVertical=isVertical;
	this.selectShadow=selectShadow;
	this.unselectShadow=unselectShadow;
	this.isHidden=isHidden;
	this.bgColor=bgColor;
	this.selBorderColor=selBorderColor;
	this.selBorderSize=selBorderSize;
//    this.tabDiv.style.bottom=this.tabDiv.style.top=this.tabDiv.style.left=this.tabDiv.style.right=this.contentDiv.style.bottom=this.contentDiv.style.top=this.contentDiv.style.left=this.contentDiv.style.right='0px';
    this.allowAdd=allowAdd;
    this.allowMenu=allowMenu;
    this.selectedColor=selectedColor;
//    this.updatePosition();
    this.menuArrowColor=menuArrowColor;
    this.menuArrowSize=menuArrowSize;
    this.divElement.style.overflow= "visible";
//    if(this.bgColor != null)
//    	this.divElement.style.background = this.bgColor;
    if(selectedColor != null)
    	this.selectColor = selectedColor;
    if(unselectedColor != null)
    	this.unselectColor = unselectedColor;
    if(selectTextColor != null)
    	this.selectTextColor = selectTextColor;
    if(unselectTextColor != null)
    	this.unselectTextColor = unselectTextColor;
    this.borderColor = borderColor;
    this.hasAddButton=hasAddButton;
    this.addButtonColor = addButtonColor;
    this.menuImageSelected=SVG_PREFIX+'x="0px" y="0px" viewBox="0 0 15 15" style="enable-background:new 0 0 15 15;" xml:space="preserve"> <g fill="'+(this.menuArrowColor!=null ? this.menuArrowColor : this.selectTextColor).replace('#',"%23")+'"><polygon points="7.6,15 3.9,7.6 0.2,0.2 7.6,0.2 14.9,0.2 11.2,7.6"/> </g> '+SVG_SUFFIX;
    this.menuImageUnselected=SVG_PREFIX+'x="0px" y="0px" viewBox="0 0 15 15" style="enable-background:new 0 0 15 15;" xml:space="preserve"> <g fill="'+(this.menuArrowColor!=null ? this.menuArrowColor : this.unselectTextColor).replace('#',"%23")+'"><polygon points="7.6,15 3.9,7.6 0.2,0.2 7.6,0.2 14.9,0.2 11.2,7.6"/> </g> '+SVG_SUFFIX;
	this.tabs=[];
	this.fontFamily=fontFamily;
	this.hideArrangeTabs = hideArrangeTabs;
//	this.tabsInner=[];
//	this.tabsText=[];
//	this.tabsMenu=[];
};

Tabs.prototype.updateScrolling=function(tabsWidth,fullHeight){
	//For Scrolling
	var fullWidth = this.tabDiv.offsetWidth;
	var tabsWidth = this.isVertical?this.location.height:this.location.width;
	if(fullWidth <= tabsWidth)
		this.hasArrowButtons = false;
	else 
		this.hasArrowButtons = true;
	
	if(this.hasArrowButtons){
		this.arrowContainer.style.display="block";
		this.scrollPaneElement.style.width=toPx(tabsWidth-2*(this.tabHeight)-this.tabSpacing);
	}
	else{
		this.arrowContainer.style.display="none";
		this.scrollPaneElement.style.width=toPx(tabsWidth);
	}
		
//	this.scrollPaneElement.style.marginTop=toPx(this.tabPaddingTop);
	this.scrollPaneElement.style.height=toPx(noNaN(this.tabHeight,0));//+noNaN(this.tabPaddingTop,0));
	this.arrowContainer.style.right="0px";
	if(this.isBottom){
		this.arrowContainer.style.bottom="unset";
		this.arrowContainer.style.top="0px";
	}
	else{
		this.arrowContainer.style.top="unset";
		this.arrowContainer.style.bottom="0px";
	}
	
	this.arrowContainer.style.width=toPx(this.tabHeight*2);
	this.arrowContainer.style.height=toPx(this.tabHeight);
	if (!this.isHidden){
		//Set the height, and scroll size of scrollpane
//	    this.tabDiv.style.height=toPx(fullHeight);
//	    this.scrollPane.DOM.paneElement.width=toPx(tabsWidth);
    	this.scrollPane.hscroll.setClipLength(tabsWidth);
    	var fullWidth = this.tabDiv.offsetWidth;
    	if(this.hasArrowButtons)
    		this.scrollPane.hscroll.setPaneLength(fullWidth+2*(this.tabHeight)+this.tabSpacing);
    	else
    		this.scrollPane.hscroll.setPaneLength(fullWidth);
		
	}
	//End for Scrolling
}
Tabs.prototype.updatePosition=function(){
	var fullHeight=this.isHidden ? 0 : (noNaN(this.tabHeight,0)+noNaN(this.tabPaddingTop,0)+noNaN(this.tabPaddingBottom));
	var fullHeightPx=toPx(fullHeight);
	var contentOffsetPx=this.tabFloatSize!=-1 ? '0px' : fullHeightPx;
	var tabsWidth = this.location.width;
	if(this.isVertical)
		tabsWidth = this.location.height;
	else
		tabsWidth = this.location.width;
	
	if (!this.isHidden){
    	
    	// Align to the right, bottom
		if(this.isFarAligned){
			this.scrollPaneElement.style.display="inline-flex";
			this.scrollPaneElement.style.flexDirection="row-reverse";
			this.tabDiv.style.paddingRight=toPx(this.beginningPadding+this.tabPaddingStart);
			this.tabDiv.style.paddingLeft=null;
		}
		else{
			this.scrollPaneElement.style.display="flex";
			this.scrollPaneElement.style.flexDirection="row";
			this.tabDiv.style.paddingLeft=toPx(this.beginningPadding + this.tabPaddingStart);
			this.tabDiv.style.paddingRight=null;
		}
		var offsetPos = this.tabFloatSize != -1? 0: -fullHeight;
		if(this.isBottom){
			if(this.isVertical){
				this.contentDiv.style.right=contentOffsetPx;
				this.tabDiv.style.paddingLeft="25px"; // the size of the bgb
		    	rotateDivElement(this.tabsBar,-90,fullHeight,tabsWidth,this.location.width+offsetPos,0);
			}else{
				this.contentDiv.style.bottom=contentOffsetPx;
		    	rotateDivElement(this.tabsBar,0,tabsWidth,fullHeight,0,this.location.height+offsetPos);
			}
		}else{
			var offsetPos = this.tabFloatSize == -1? 0: -fullHeight;
			if(this.isVertical){
				this.contentDiv.style.left=contentOffsetPx;
				this.tabDiv.style.paddingLeft="25px"; // the size of the bgb
				this.tabDiv.style.right=null;
		    	rotateDivElement(this.tabsBar,-90,fullHeight,tabsWidth,offsetPos,0);
			}else{
			    this.contentDiv.style.top=contentOffsetPx;
				
			    this.tabDiv.style.bottom=null;
		    	rotateDivElement(this.tabsBar,0,tabsWidth,fullHeight,0,offsetPos);
			}
		}
	}
	this.updateScrolling(tabsWidth,fullHeight);
}
Tabs.prototype.setSize=function(width,height){
  this.portlet.setSize(width,height);
  this.updatePosition(); 
};

Tabs.prototype.rp=function(){
    removeAllChildren(this.tabDiv);
    if(this.isHidden == false){
    	this.tabsBar.style.display="inline-block";
    	this.createTabsElementsDom();
    }
    else
    	this.tabsBar.style.display="none";
    	
}
Tabs.prototype.setActiveTab=function(tabIndex){
  this.activeTab=tabIndex;
};

Tabs.prototype.addTab=function(location,tabName, tabNameHtml,allowTitleEdit,selectColor,unselectColor,selectTextColor,unselectTextColor,isHidden,blinkColor,blinkPeriod,hids){
  //if(this.tabs[location]){
	  //this.tabs[location].tabName = tabName;
	  //this.tabs[location].tabNameHtml = tabNameHtml;
    //this.tabs[location].element.innerHTML=tabNameHtml;
    //this.tabs[location].element.id=hids;
    //return;
  //}
  var tabMeta = {};
  tabMeta.tabName = tabName;
  tabMeta.tabNameHtml = tabNameHtml;
  tabMeta.selectColor=selectColor;
  tabMeta.unselectColor=unselectColor;
  tabMeta.selectTextColor=selectTextColor;
  tabMeta.unselectTextColor=unselectTextColor;
  tabMeta.blinkColor=blinkColor;
  tabMeta.blinkPeriod= blinkPeriod == null ? 100 : Math.abs(blinkPeriod);
  tabMeta.isHidden = isHidden;
  tabMeta.tabid=location;
  tabMeta.allowTitleEdit=allowTitleEdit;
  tabMeta.hids=hids;
  this.tabs[location]=tabMeta;
}



Tabs.prototype.onNewTab=function(e){
  this.callBack('newTab',{});
};
Tabs.prototype.onTabSelected=function(e){
  this.callBack('tab',{tabindex:getMouseTarget(e).tabid});
};
Tabs.prototype.onTabClicked=function(e){
	var prevTab=this.activeTab;
	this.callBack('tabClick',{tabindex:getMouseTarget(e).tabid,prevTab:prevTab});
};
Tabs.prototype.onMenu=function(e){
	var prevTab=this.activeTab;
  this.callBack('onMenu',{tabindex:getMouseTarget(e).tabid,prevTab:prevTab,x:this.portlet.owningWindow.MOUSE_POSITION_X,y:this.portlet.owningWindow.MOUSE_POSITION_Y});
};
