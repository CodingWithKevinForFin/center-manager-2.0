function SelectedRows(){
    this.ranges=new Array();
    this.rows=new Array();
}

SelectedRows.prototype.isEmpty=function(){
    return this.ranges.length==0 && this.rows.length==0;
}
SelectedRows.prototype.isSelected=function(rownum){
    if(this.rows[rownum]!=null)
	return true;
    for(var i=0;i<this.ranges.length;i++)
	if(this.ranges[i][0]<=rownum && rownum<=this.ranges[i][1]) return true;
}

SelectedRows.prototype.add=function(start,end){
    if(end==null)
	end=start;
    else if(end<start){
	var t=end;
	end=start;
	start=t;
    }
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
}


//################
//##### Menu #####


function Menu(window){
    var that=this;
    this.window=window;
    this.divElement=nw("div");
    this.tableElement=nw("table");
    this.selectedRows = new SelectedRows();
    this.divElement.className='menu';
    this.divElement.appendChild(this.tableElement);
    this.disabled=false;
    this.visible=false;
    this.menuItemUndoHoverStyle = {};
    //this.divElement.onmouseout=function(e){that.onMouseOut(e);};
    this.tableElement.ontouchstart=function(e) { that.onTouchDragStart(e);};
	this.tableElement.ontouchmove=function(e) { that.onTouchDragMove(e);}; 
    this.childMenus=new Map();
 } 

var currentContextMenu=null;
Menu.prototype.divElement;
Menu.prototype.parent;
Menu.prototype.tableElement;
Menu.prototype.visible;
Menu.prototype.selected;
Menu.prototype.selectedBy;
Menu.prototype.selectedRows;
Menu.prototype.childMenus;
Menu.prototype.openMenuInterval;
Menu.prototype.runFirstItemOnEnter;


//Gets the index of a menuItem
Menu.prototype.getIndex=function(tr){
	return [].indexOf.call(this.tableElement.children, tr);
}

//Hide all menus in menu chain
Menu.prototype.hideAll=function(dontFire){
	if(this.parent)
		this.parent.hideAll(dontFire);
	else{
		this.hide(dontFire);
		currentContextMenu = null;
	}
	this.onClose();
}

//Hide submenu
Menu.prototype.hideChildren=function(){
  if(this.activeChild!=null){
	  this.activeChild.hide();
  }
}

Menu.prototype.getChildMenu=function(id){
	if(this.tableElement == null)
		return null;
	if(this.tableElement.children == null)
		return null;
	
	return this.tableElement.children[id];
}

Menu.prototype.getSelectedMenu=function(){
	return this.getChildMenu(this.selected);
}


Menu.prototype.setCssStyle=function(cssStyle){
	applyStyle(this.divElement,cssStyle);
}

Menu.prototype.setSize=function(width, height){
	this.divElement.style.width=toPx(width);
//	this.divElement.style.maxHeight=toPx(height);
	this.tableElement.style.width="100%";
}
//Hide the menu and it's children.
Menu.prototype.hide=function(dontFire){
if(!this.visible)
return;
this.visible=false;

var targetLoc = this.window.document.body;
if(this.targetLoc != null)
	  targetLoc = this.targetLoc;
	  
targetLoc.removeChild(this.divElement);
if(this.parent!=null && this.parent.activeChild==this)
this.parent.activeChild=null;
this.hideChildren();

if(this.onHide && (dontFire == false || dontFire == null))
	this.onHide(this);

if(this.glass!=null){
	targetLoc.removeChild(this.glass);
	this.glass=null;
}

var menuItems = this.tableElement.children;
if(this.selected != null)
	  this.highlight(menuItems[this.selected], false);		
this.selected = null;
};

Menu.prototype.deselect=function(){
	if(this.selected != null){
		this.highlight(this.tableElement.children[this.selected], false);
		this.selected = null;
	}
}

Menu.prototype.getTextDivFromRow=function(row) {
	var children = row.children;
	for (var i = 0; i < children.length; i++) {
		if (children[i].classList.contains("menu_item_text"))
			return children[i];
	} 
	return null;
}
Menu.prototype.getRightArrowFromRow=function(row) {
	var children = row.children;
	for (var i = 0; i < children.length; i++) {
		if (children[i].classList.contains("menu_right_arrow"))
			return children[i];
	} 
	return null;
}
Menu.prototype.highlight=function(row, onOff){
	if(row.firstChild){
		var rightArrow = this.getRightArrowFromRow(row);
		if(onOff){
			row.style.background = this.hoverBgCl;
			row.style.color= this.hoverFontCl;
			if (rightArrow != null)
				rightArrow.style.color= this.hoverFontCl;
		}else{
			row.style.background = this.menuItemUndoHoverStyle.bgCl == null ? "" : this.menuItemUndoHoverStyle.bgCl;
			row.style.color= this.menuItemUndoHoverStyle.fontCl == null ? "" : this.menuItemUndoHoverStyle.fontCl;
			if (rightArrow != null)
				rightArrow.style.color= this.menuItemUndoHoverStyle.fontCl == null ? "" : this.menuItemUndoHoverStyle.fontCl;
		}
	}
}
Menu.prototype.isEnabledMenuItem=function(row){
	return !row.classList.contains("menu_divider") && !row.classList.contains("disabled");
}

Menu.prototype.navigateMenu=function(key){
	var menuItems = this.tableElement.children;
	if(menuItems.length > 0){
		var oldSelected = this.selected;
		if(this.selected == null)
			this.selected = 0;
		this.highlight(menuItems[this.selected], false);
		
		var loops = 0;
		while((this.selected == oldSelected || !this.isEnabledMenuItem(menuItems[this.selected])) && loops < menuItems.length){
			if(key === "ArrowDown"){
				if(!this.scrollPane){
					// at the bottom of the suggestion
					if(this.selected == (menuItems.length - 1) || this.selected == (this.totalSuggestionSize - 1))
					this.selected = 0;
					else
						this.selected++;
				}else{
					if(this.selected == (menuItems.length - 1)){
						this.onKeydownCausingBoundsChange();
						break;
					}else
					this.selected++;
				}
			}
			else if (key === "ArrowUp"){
				if(!this.scrollPane){
					// at the top of the suggestion
					if(this.selected == 0)
						this.selected = this.totalSuggestionSize ? this.totalSuggestionSize - 1 : menuItems.length - 1;
					else
						this.selected--;
				}else{
				if(this.selected == 0){
						this.onKeyupCausingBoundsChange();
						break;
					}else
					this.selected--;
				}
			}
			loops++;
		}
		this.highlight(menuItems[this.selected], true);		
		this.ensureVisibleMenuItem(menuItems[this.selected]);
		this.selectedBy="KEYBOARD";
	}
	else{
		this.selected = null;
	}
}


Menu.prototype.onKeydownCausingBoundsChange= function(){
	//ensure index in bounds. If the lowerRow exceeds totalSuggSize, loop back to the beginning 
	var endOfSuggestion=false;
	if(this.lowerRow +1 >= this.extension.totalSuggestionSize){
		//update scrollbar position, set clip top to 0(all the way top)
		this.scrollPane.setClipTopNoFire(0);
		this.upperRow = 0;
		this.lowerRow = this.extension.visibleSugLen - 1 ;
		endOfSuggestion = true;
		this.extension.field.onExtensionCallback(0, "onBoundsChange",{top:this.scrollPane.getClipTop(),
			userSeqnum:this.userSeqnum,
			s:this.upperRow,
			e:this.lowerRow,
			updateSelection:true,
			startOrEndOfSuggestion:2});	
	}else{
		this.updateScrollbarPosition(true);
		this.upperRow++;
		this.lowerRow++;
		this.extension.field.onExtensionCallback(0, "onBoundsChange",{top:this.scrollPane.getClipTop(),
			userSeqnum:this.userSeqnum,
			s:this.upperRow,
			e:this.lowerRow,
			updateSelection:true,
			startOrEndOfSuggestion:3});	
	}
}

Menu.prototype.onKeyupCausingBoundsChange=function(){
	var startOfSuggestion=false;
	if(this.upperRow == 0 ){
		this.scrollPane.setClipTopNoFire(this.scrollPane.paneHeight - this.rowHeight);
		this.upperRow = this.extension.totalSuggestionSize - this.extension.visibleSugLen;
		this.lowerRow = this.extension.totalSuggestionSize - 1 ;
		startOfSuggestion = true;
		this.extension.field.onExtensionCallback(0, "onBoundsChange",{top:this.scrollPane.getClipTop(),
			userSeqnum:this.userSeqnum,
			s:this.upperRow,
			e:this.lowerRow,
			updateSelection:true,
			startOrEndOfSuggestion:1});
	}else{
		this.updateScrollbarPosition(false);
		this.upperRow--;
		this.lowerRow--;
		this.extension.field.onExtensionCallback(0, "onBoundsChange",{top:this.scrollPane.getClipTop(),
			userSeqnum:this.userSeqnum,
			s:this.upperRow,
			e:this.lowerRow,
			updateSelection:true,
			startOrEndOfSuggestion:3});
	}
	
}

//this is no fire
Menu.prototype.updateScrollbarPosition=function(isScrollDown){
	//update scrollbar position
	var pageTop = this.scrollPane.getClipTop();
	var pageHeight = this.scrollPane.getClipHeight();
	if(isScrollDown){
		var rowTop = this.rowHeight * (this.lowerRow+1);//(index+1)th row
		var rowBot = this.rowHeight * (this.lowerRow+2);
	}else{
		var rowTop = this.rowHeight * (this.upperRow-1);//(index+1)th row
		var rowBot = this.rowHeight * (this.upperRow);
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
		  this.scrollPane.setClipTopNoFire(pos);
}

Menu.prototype.ensureVisibleMenuItem=function(menuItem){
	var menuItemRect = menuItem.getBoundingClientRect();
	var menuRect = this.divElement.getBoundingClientRect();
	
	var menuItemTop = menuItemRect["top"];
	var menuTop = menuRect["top"];
	
	var menuItemBot = menuItemRect["bottom"];
	var menuBot = menuRect["bottom"];
	
	if(menuTop > menuItemTop){
		this.divElement.scrollTop -= (menuTop - menuItemTop);
	}
	else if(menuItemBot > menuBot){
		this.divElement.scrollTop += (menuItemBot - menuBot);
	}
}

Menu.prototype.navigateCloseSubMenu=function(){
	if(this.parent){
		this.hide();
		currentContextMenu=this.parent;
	}
}

Menu.prototype.navigateOpenSubMenu=function(){
	var selectedRow  = this.tableElement.children[this.selected];
	if(selectedRow){
		var r=new Rect();
		r.readFromElement(selectedRow);
		var childMenu = this.childMenus.get(this.getIndex(selectedRow));
		if(childMenu && childMenu.tableElement.childNodes.length > 0){
			var widthParentAndChildMenu = r.getRight();
			var widthParentWithOverflow = r.width;
			if (widthParentWithOverflow > 600)
				childMenu.show(new Point(widthParentAndChildMenu - (widthParentWithOverflow - 600), r.getTop()));
			else
				childMenu.show(new Point(r.getRight(),r.getTop()));
		} 
		return childMenu;
	}
	return null;
}

Menu.prototype.runMenuItemAction=function(e){
	var selectedRow = this.tableElement.children[this.selected];
	if(selectedRow){
		selectedRow.onclick(e);
	}
}

Menu.prototype.runFirstMenuItemAction=function(e){
	this.navigateMenu("ArrowDown");
	if(this.selected != null)
		this.runMenuItemAction(e);
}
Menu.prototype.handleKeypress=function(e){
	var stopPropagation = true;
	switch(e.key){
	default:
	    for(var i=0;i<this.tableElement.children.length;i++){
	    	var child=this.tableElement.children[i];
	    	if(child.keystroke==e.key){
	    		child.onclick(e);
			    stopPropagation = true;
	    	}
	    }
		if(!e.ctrlKey && !e.altKey && e.key.match(/^[a-z0-9_\.]$/i) != null){
			stopPropagation = false;
		}
		break;
	}
	if (stopPropagation){
		e.stopPropagation();
	}
}

Menu.prototype.customHandleKeydown = null;
Menu.prototype.setCustomKeydownHandler = function(handler){
	this.customHandleKeydown = handler;
}

Menu.prototype.handleKeydown=function(e){
	var stopPropagation = true;
	switch(e.key){
	case "Enter":
		if(this.childMenus.get(this.selected) == null){
			if(this.selected != null)
				this.runMenuItemAction(e);
			else if(this.runFirstItemOnEnter==true)
				this.runFirstMenuItemAction(e);
		}
		else{
			var child = this.navigateOpenSubMenu();
			if(child)
				child.navigateMenu("ArrowDown");
		}
		e.preventDefault();
		break;
	case "Escape":
		this.hideAll();
		break;
	case "ArrowDown":
		this.navigateMenu(e.key);
		break;
	case "ArrowUp":
		this.navigateMenu(e.key);
		break;
	case "ArrowLeft":
		this.navigateCloseSubMenu();
		break;
	case "ArrowRight":
		var child = this.navigateOpenSubMenu();
		if(child)
			child.navigateMenu("ArrowDown");
		break;
	case "Delete":
		stopPropagation = false;
		break;
	case "Backspace":
		stopPropagation = false;
		break;
	case " ":
		stopPropagation = false;
		break;
	default:
		if(!e.ctrlKey && !e.altKey && e.key.match(/^[a-z0-9_\.]$/i) != null){
			stopPropagation = false;
		}
		break;
	}
	if (stopPropagation){
		e.stopPropagation();
	}
}

Menu.prototype.handleWheel=function(e){
	this.divElement.scrollTop += e.deltaY;
}

Menu.prototype.handleMousemove=function(e){
	var target;
//	if(this.openMenuInterval != null){
//		clearInterval(this.openMenuInterval);
//		this.openMenuInterval = null;
//	}
	if(e.target.tagName === "TD"){
		target = e.target.parentElement;
	}else{ target = e.target;}
	var classes = target.classList;
	if(classes.contains("menu_item") && !classes.contains("disabled")){
		var targetMenu = this;
		var menuItems = this.tableElement.children;
		var index = -1;
		while(index == -1 && targetMenu){
			index = getIndexOf(menuItems, e.target.parentElement, 0);
			if(index > -1)
				break;
			else{
				targetMenu = targetMenu.parent;
				menuItems = targetMenu ? targetMenu.tableElement.children :null;
			}
		}
		
		if(index > -1){
			if(this != targetMenu){
				var that = this;
				var thatParent = this.parent;
				while(that.parent && that.parent != targetMenu){
					that.navigateCloseSubMenu();
					that = thatParent;
					thatParent = that.parent;
				}
				if(targetMenu.selected != index){
					that.navigateCloseSubMenu();
				}
			}
			else{ 
				if(targetMenu.selected != index){
					if(targetMenu.selected != null)
						targetMenu.highlight(menuItems[targetMenu.selected], false);
					targetMenu.selected = index;
					targetMenu.highlight(menuItems[targetMenu.selected], true);
					this.selectedBy="MOUSE";
				}
//				var func = function(w, currentIndex, menu){
//					if(menu.openMenuInterval != null && currentIndex == menu.selected){
//						w.clearInterval(menu.openMenuInterval);
//						menu.navigateOpenSubMenu();
//					}
//				}
				targetMenu.navigateOpenSubMenu();
//				this.openMenuInterval = this.window.setInterval(function(){func(this.window, index, targetMenu);}, 70);
			}
		}
	}
	else if(this.selected != null && this.selectedBy=="MOUSE"){
		var menuItems = this.tableElement.children;
		this.highlight(menuItems[this.selected], false);
		this.selected = null;
	}
}
Menu.prototype.isEmpty=function() {
	return this.tableElement.children == null || this.tableElement.children.length == 0;
}
Menu.prototype.show=function(p, keepYPosition, targetLoc, caller,anchorBottom){
    if (this.isEmpty())
    	return;
    if(!this.parent && currentContextMenu){
  	  currentContextMenu.hideAll(true);
    }
    currentContextMenu=this;
    if(this.visible)
  	return;
    
    if(targetLoc == null){
  	  if(this.window == null){
  		 log('no window!');
  		 return;
  	  }
  	  targetLoc = this.window.document.body;
    }
    targetLoc.appendChild(this.divElement);
    this.targetLoc = targetLoc;
    this.visible=true;
    var divPos=new Rect().readFromElement(this.divElement);
    if(this.parent){
  	if(this.parent.activeChild!=null)
  		this.parent.activeChild.hide();
  	if(this.disabled){
  		this.visible=false;
  		return;
  	}
      this.parent.activeChild=this;
      var bodyPos=new Rect().readFromElement(targetLoc);
      divPos.left=p.x;
      divPos.top=p.y;
      if(divPos.left<0)
        divPos=0;
      if(divPos.top<0)
        divPos.top=0;
      var h=bodyPos.getRight()-divPos.getRight();
      if(h<0){
        divPos.left=new Rect().readFromElement(this.parent.divElement).left-divPos.width+1;
      }
      this.divElement.style.left=toPx(divPos.left);
      this.divElement.style.top=toPx(divPos.top);
      ensureInWindow(this.divElement);
    }else{
      this.divElement.style.left=toPx(p.x);
      if(anchorBottom)
        this.divElement.style.bottom=toPx(document.body.offsetHeight-p.y);
      else
        this.divElement.style.top=toPx(p.y);
      if(keepYPosition == true){
      	containInWindow(this.divElement);
      	this.divElement.style.overflowX = "hidden";
      }else
      	ensureInWindow(this.divElement);
      this.glass=nw('div','disable_glass_clear');
      this.glass.style.zIndex='9989';
      var that=this;
  	var onHide = function(){
  		if(caller!=null)
  			if(caller.hideGlass){
  				caller.hideGlass();	
  			}
  			
  	};
  	this.glass.onclick=function(e, clickCallback){
  		e.preventDefault();
	    that.hideAll();
	    multiSelectFilter = that.multiSelectCallback();
	    if(caller != null)
			caller.select(multiSelectFilter);
	    that.selectedRows.clear();
	    flagSelectedRows = false;
	    if(that.onGlass!=null)that.onGlass(e);
	    onHide();}
  	targetLoc.appendChild(this.glass);
    }
  };

  Menu.prototype.applyBorderStyle=function(menu) {
	this.divElement.style.borderTopColor=menu.borderTpLfCl;
	this.divElement.style.borderLeftColor=menu.borderTpLfCl;
	this.divElement.style.borderBottomColor=menu.borderBtmRtCl;
	this.divElement.style.borderRightColor=menu.borderBtmRtCl;
}

Menu.prototype.setHoverStyle=function(menu) {
	this.hoverBgCl = menu.hoverBgCl ? menu.hoverBgCl : "#68c56f";
	this.hoverFontCl = menu.hoverFontCl ? menu.hoverFontCl : "#ffffff";

}
Menu.prototype.createMenu=function(menu, clickCallback){
	this.divElement.style.backgroundColor=menu.bgCl;
	this.applyBorderStyle(menu);
	this.setHoverStyle(menu);
	if(menu.children){
		for(var i = 0; i < menu.children.length; i++){
			this.addMenuItem(menu.children[i], clickCallback);
		}
	} 
}


Menu.prototype.setOptions=function(optionsJSON){
	if(optionsJSON.runFirstItemOnEnter!= null)
		this.runFirstItemOnEnter = optionsJSON.runFirstItemOnEnter;
}

Menu.prototype.addMenuItem = function(menu, clickCallback) {
	if (menu.type === "menu") {
		this.addMenu(menu, clickCallback);
	} else if (menu.type === "action") {
		this.addAction(menu, clickCallback);
	} else if (menu.type === "divider") {
		this.addDivider(menu);
	}
	
}

Menu.prototype.addMenu = function(menu, clickCallback) {
	var row = nw("tr");
	var text = nw("td");
	
	var m = new Menu(this.window);
	m.createMenu(menu, clickCallback);

	if (menu.enabled) {
		m.disabled = false;
		row.className = "menu_item parent";
		row.style.color=menu.fontCl;
	} else {
		m.disabled = true;
		row.className = 'menu_item parent disabled';
		row.style.background=menu.disBgColor;
		row.style.color=menu.disFontCl;
	}

	
	if (menu.text) {
		text.classList.add("menu_item_text");
		text.innerHTML = menu.text;
		this.menuItemUndoHoverStyle.fontCl=menu.fontCl;
		this.menuItemUndoHoverStyle.bgCl=menu.bgCl;
	}
	if (menu.style)
		applyStyle(row, menu.style);
	m.parent = this;

	if (menu.noIcon != true){
		var icon = nw("td");
		if (menu.backgroundImage) {
			icon.style.backgroundImage = "url('" + menu.backgroundImage + "')";
		}
		row.appendChild(icon);
	}
	row.appendChild(text);
	var rightArrow = nw("span");
	this.applyRightArrowStyle(rightArrow);
	row.appendChild(rightArrow);
	this.tableElement.appendChild(row);
	var index = [].indexOf.call(this.tableElement.children, row);
	this.childMenus.set(index,m);
}
Menu.prototype.applyRightArrowStyle=function(rightArrow) {
    var fontSize = 10;
    var rowHeight = 19;
    rightArrow.innerHTML = "&#9658;";
    rightArrow.style.fontSize = toPx(fontSize);
    rightArrow.style.marginLeft = toPx(-fontSize);
    rightArrow.style.marginTop = toPx(Math.floor(Math.abs((rowHeight - fontSize))/2));
    rightArrow.classList.add("menu_right_arrow");
}

Menu.prototype.multiSelectCallback = function () {
	let multiFilter = "";
	if(this.selectedRows.isEmpty())
		return multiFilter;
	for(var i=0; i< this.tableElement.children.length; i++){
		var row = this.tableElement.children[i];
		if(this.selectedRows.isSelected(i)){
			multiFilter += row._itemId;
			multiFilter += "|";
		}			
	}
	multiFilter = multiFilter.substring(0, multiFilter.length-1);
	return multiFilter;
} 


Menu.prototype.addAction = function(menu, clickCallback) {
	var row = nw("tr");
	var text = nw("td");
	if (menu.enabled) {
		row.className = 'menu_item';
		row._itemId = menu.action;
		this.isSelectMenu = menu.style != null && menu.style.includes('selectfield');
		var autoclose = menu.autoclose == false ? false : true;
		var f = menu.onclickJs != null ? new this.window.Function(menu.onclickJs) : null;
		row.id=menu.hids;
		
		if (autoclose){
			var that = this;
			if(that.isSelectMenu){
				row.addEventListener('mousedown', (e) => {
					if(!e.ctrlKey)
						this.dragSelect=true;
					this.selectRow(e, row.sectionRowIndex, e.ctrlKey, false);
					this.updateSelectedRows();
			    });
	
				row.addEventListener('mousemove', (e) => {
					this.selectRow(e, row.sectionRowIndex, e.ctrlKey, true);
					this.updateSelectedRows();
			    });
				
				row.addEventListener('mouseup', (e) => {
			        if (e.button === 2) 
						this.dragSelect=false;   
				});
			}
			row.onclick = function(e) {
				if(f)
					f.call(null);
				if(!that.isSelectMenu || that.selectedRows.isEmpty())
					clickCallback(e, this._itemId);
				else if(that.isSelectMenu){
					multiSelectFilter = that.multiSelectCallback();
					clickCallback(e, multiSelectFilter);
					that.selectedRows.clear();
				}
				that.hideAll();
				
			};
		}else
			row.onclick = function(e) {
				if(f)
					f.call(null);
				clickCallback(e, this._itemId);
			};
		row.keystroke= menu.keystroke;
		row.onclick._itemId = menu.action;
	} else {
		row.className = 'menu_item disabled';
	}

	

    if (menu.text) { // affects all the texts.
        if (!row.classList.contains("ami_edit_menu")) { // don't apply style on the menu header.
            text.classList.add("menu_item_text");
            if (row.classList.contains("disabled")) {
                row.style.background=menu.disBgColor;
                row.style.color=menu.disFontCl;
            } else {
                row.style.background=menu.bgCl;
                row.style.color=menu.fontCl;
                // this remembers the style before menu item is hovered. Used in highlight function.
                this.menuItemUndoHoverStyle.fontCl=menu.fontCl;
                this.menuItemUndoHoverStyle.bgCl=menu.bgCl;
            }
        }
        text.innerHTML = menu.text;
		//this is to reconcile different default inline-height in different browsers. Firefox has a different in-line height than other browsers, which will add extra padding to the font height
		text.style.lineHeight = 1;
    }
    if (menu.style)
        applyStyle(row, menu.style);
    if (menu.noIcon != true){
        var icon = nw("td");
        if (menu.backgroundImage) {
            icon.style.backgroundImage = "url('" + menu.backgroundImage + "')";
        }
        row.appendChild(icon);
    }
    row.appendChild(text);
    this.tableElement.appendChild(row);
}

Menu.prototype.updateSelectedRows=function(){
	for(var i=0; i< this.tableElement.children.length; i++){
		row = this.tableElement.children[i];
		if(this.selectedRows.isSelected(i) && !row.classList.contains('selected'))
			row.classList.add('selected');
		else if (!this.selectedRows.isSelected(i) && row.classList.contains('selected'))
			row.classList.remove('selected');
	}
}

Menu.prototype.selectRow=function(e,row,ctrlKey,isDragging){
    if(getMouseButton(e) != 2){
		return;
    }
	if(!isDragging && this.dragSelect)
		this.dragStart=row;
	if(ctrlKey){
		if(this.extension.field instanceof TextField)
			return;
		if (getMouseButton(e) == 2 && this.selectedRows.isSelected(row)) 
			this.selectedRows.remove(row);
		else
			this.selectedRows.add(row);
	}else if(this.dragSelect){
		if(this.extension.field instanceof TextField)
			return;
		this.selectedRows.clear();
		if(row>this.dragStart)
			this.selectedRows.add(this.dragStart,row);
		else
			this.selectedRows.add(row,this.dragStart);
		
	}else{
		this.selectedRows.clear();
		this.selectedRows.add(row);
	}

}

Menu.prototype.addDivider = function(menu) {
    var row = nw("tr");
    var cell = nw("td");
    cell.style.background=menu.divCl;
    if (menu.style)
        applyStyle(row, menu.style);
    cell.setAttribute("colspan", 2);
    row.appendChild(cell);
    this.tableElement.appendChild(row);
}

//Deprecated
Menu.prototype.addItem=function(name,_itemId,clickCallback,dontClose,onclickJs){
    var that=this;
    if(dontClose)
      return this.addItemInner(name,_itemId,clickCallback,null,null,false,onclickJs);
    else
      return this.addItemInner(name,_itemId,clickCallback,function(event){that.hideChildren();},null,true,onclickJs);
}

Menu.prototype.showFromParent=function(event){
  event=getMouseEvent(event);
  r=new Rect();
  r.readFromElement(getMouseTarget(event));
  this.show(new Point(r.getRight()+2,r.getTop()));
};
Menu.prototype.onClose=function(){
}
Menu.prototype.setFieldId=function(fieldId){
    this.fieldId=fieldId;
}
Menu.prototype.noAutoScrollbar = function() {
	//1. first remove the tiny squre from the scrollbar container
	const scrollbarContainer = this.divElement.children[1];
	if(scrollbarContainer){
		for(var child of scrollbarContainer.children){
			if(child.className=='tiny_square'){
				scrollbarContainer.removeChild(child);
				break;
			}
		}
	}
	//TODO: Not sure if this is necessary, this is to readjust the height of the scrollpane container
	var borderWidth = 1.1111;
	var scrollpaneContainerHeight = parseFloat(this.divElement.style.height) + 2*borderWidth;
	this.divElement.style.height =  `${scrollpaneContainerHeight}px`;
	
};
Menu.prototype.addScrollPane = function(){
	var that=this;
	this.scrollSize=15;
	this.scrollPane=new ScrollPane(this.divElement,this.scrollSize, this.tableElement);
	this.scrollPane.onScroll=function(){that.onScroll();};
}
Menu.prototype.onScroll=function(){
	this.userScrollSeqnum++;
	var isHscrollVisible = this.scrollPane.hscrollVisible;
	this.setClipZone();
	FieldExtension.prototype.callBack.call(this.extension,"onBoundsChange",{top:this.scrollPane.getClipTop(),
		userSeqnum:this.userSeqnum,
		s:this.upperRow,
		e:this.lowerRow});	
}

//TODO:This is for horizontal scrollbar for text field infinite scroll
Menu.prototype.updateCellsLocations=function(){
	//shift each row left or right
	for(var y = 0; y < this.tableElement.children.length; y++){
		var cell=this.getChildMenu(y);
		if(cell){
			var leftPos=Math.round(this.scrollPane.getClipLeft());
			cell.style.position = 'relative';
			cell.style.left=toPx(-leftPos);
		}
			
	}
}

/**
 * Given this menu structure:
 * <div class="menu selectfield_ac_list scrollpane_container" style="width: 199px; height: 142px; left: 192px; top: 453px; overflow-x: hidden;">
 * 		<div class="scrollpane" style="left: 0px; top: 0px; width: 184px; height: 142px;">
 * 			<table style="width: 100%;"></table></div>
 * 		<div class="scrollbar_container" style="left: 184px; top: 0px; width: 15px; height: 142px;">
 * 			<div class="scrollbar_track_v" style="left: 0px; top: 15px; width: 15px; height: 112px;"></div>		
 * 			<div class="scrollbar_handle_v" style="display: inline; left: 0px; top: 15px; width: 15px; height: 47px;">
 * 				<div class="scrollbar_grip_v"></div></div>
 * 			<div class="scrollbar_down" style="left: 0px; top: 127px; width: 15px; height: 15px;"></div>
 * 			<div class="scrollbar_up" style="left: 0px; top: 0px; width: 15px; height: 15px;"></div>
 * 		</div>
 * </div>
 * 
*/
//this set the width and height for the scrollpane container 
Menu.prototype.setMenuBoxWidthHeight=function(width,height){
	this.scrollPane.width = width;
	this.scrollPane.height = height;
}
//if there are 1000 total suggestions, we need this much space reserved for scrollpane height
Menu.prototype.initScroll=function(totalSize, rowHeight, rowWidth){
	this.userScrollSeqnum = 0;
	var totalHeight = totalSize*rowHeight;
	this.scrollPane.setPaneSize(rowWidth - this.scrollSize, totalHeight);//width,height; height should reflect the number of suggestions
}
Menu.prototype.setTableRowHeight=function(rowHeight){
	this.rowHeight = rowHeight;
	for(var row of this.tableElement.rows){
		this.tableElement.rowHeight = rowHeight;
		row.style.height = rowHeight;
	}
}
Menu.prototype.setClipZone=function(){
	this.lowerRow = Math.ceil((this.scrollPane.getClipTop()+this.scrollPane.getClipHeight())/this.tableElement.rowHeight)-1;
	this.upperRow = this.lowerRow - this.extension.visibleSugLen + 1;
}

//MOBILE SUPPORT - FOR SCROLLING
Menu.prototype.onTouchDragStart=function(e){
	this.currPoint = getMousePoint(e);
}

//MOBILE SUPPORT - FOR SCROLLING
Menu.prototype.onTouchDragMove=function(e){
		var that = this;		
		var diffx = that.currPoint.x - getMousePoint(e).x;
		var diffy = that.currPoint.y - getMousePoint(e).y;
		that.scrollPane.hscroll.goPage(0.01 * diffx);
		that.scrollPane.vscroll.goPage(0.01 * diffy);
		that.currPoint = getMousePoint(e);
}