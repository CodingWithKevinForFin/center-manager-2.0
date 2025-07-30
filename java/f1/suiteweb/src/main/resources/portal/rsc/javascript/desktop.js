
//###########################
//##### Desktop Portlet #####




var DESKTOP_BUTTON_IMAGE_PREFIX=SVG_PREFIX+' x="0px" y="0px" viewBox="0 0 12 12" style="enable-background:new 0 0 12 12;" xml:space="preserve"><g style="stroke-width:1;stroke-miterlimit:10;vector-effect:non-scaling-stroke;" ';// <g fill="#FFFFFF" stroke="#FFFFFF"> ';
var DESKTOP_BUTTON_IMAGE=[];
DESKTOP_BUTTON_IMAGE['close']='><path d="M2 2L10 10M2 10L10 2"/></g>'+SVG_SUFFIX;
DESKTOP_BUTTON_IMAGE['min']  ='><path d="M1 9h10" /></g> '+SVG_SUFFIX;
DESKTOP_BUTTON_IMAGE['max']  ='><path d="M2 2h8v8h-8v-8"/></g> '+SVG_SUFFIX;
DESKTOP_BUTTON_IMAGE['pop']  ='><path d="M4 8L10 2v4M10 2h-4"/><path d="M4 2h-2v8h8v-3"/></g> '+SVG_SUFFIX;
DESKTOP_BUTTON_IMAGE['maxAlt']  ='><path d="M2 4h6v6h-6v-6"/><path d="M4 4v-2h6v6h-2"/></g> '+SVG_SUFFIX;

function Desktop(divElement){
  this.divElement=divElement;
  var that=this;
  this.deskElement=nw('div');
  this.deskBackgroundElement=nw('div');
  this.deskBackgroundElement.style.top='0px';
  this.deskBackgroundElement.style.left='0px';
  this.deskBackgroundElement.style.right='0px';
  this.deskBackgroundElement.style.bottom='0px';
  
  this.deskElement.style.position='absolute';
  this.deskElement.style.top='0px';
  this.deskElement.style.left='0px';
  this.deskElement.style.right='0px';
  this.deskElement.style.bottom='0px';
  this.deskElement.className='portal_desktop';
  this.deskElement.style.overflow='hidden';
  this.deskContainerElement=nw('div', "ami_desktop_container");
  var _doc = getDocument(this.divElement);
  _doc.deskContainerElement=this.deskContainerElement;
  this.deskBackgroundElement.appendChild(this.deskContainerElement);
  this.deskContainerElement.appendChild(this.deskElement);
  this.divElement.appendChild(this.deskBackgroundElement);
  
  this.divElement.classList.add("amidivElement");
  this.deskContainerElement.scroll="no";
  this.deskContainerElement.tabindex="-1";

  
  this.docElement=nw('div');
  this.docTable=nw('table');
  this.docRow=nw('tr');
  this.docElement.appendChild(this.docTable);
  this.docTable.appendChild(this.docRow);
  this.divElement.appendChild(this.docElement);
  
  this.nextZindex=0;
  this.windows=[];
  this.doclets=[];
  var bottom=true;
  this.setDocletPosition('bottom');
  this.isContainer=true;
  this.addButton=nw('td');
  this.addButton.onclick=function(e){if(that.onUserClickedButton) that.onUserClickedButton(e);};
  this.deskElement.ondblclick=function(e){if(getMouseTarget(e)==that.deskElement && that.onUserDoubleclickedBackground) that.onUserDoubleclickedBackground(e)};
  
  this.options={};
  this.setStyleClassPrefix('portal_desktop');
}

Desktop.prototype.setOptions=function(options){
	if(options.docletPosition!=null)
		this.setDocletPosition(options.docletPosition);
	if(options.styleClassPrefix!=null)
		this.setStyleClassPrefix(options.styleClassPrefix);
	var colorChanged=false;
	this.options=options;
//	if(options.windowColor!=null){ this.options.windowColor=options.windowColor; colorChanged=true; }
//	if(options.windowColorUp!=null){ this.options.windowColorUp=options.windowColorUp; colorChanged=true; }
//	if(options.windowColorDown!=null){ this.options.windowColorDown=options.windowColorDown; colorChanged=true; }
//	if(options.windowColorText!=null){ this.options.windowColorText=options.windowColorText; colorChanged=true; }
//	if(options.windowColorButton!=null){ this.options.windowColorButton=options.windowColorButton; colorChanged=true; }
	if(options.windowColorButtonUp!=null){ this.options.windowColorButtonUp=options.windowColorButtonUp; colorChanged=true; }
//	if(options.windowColorButtonDown!=null){ this.options.windowColorButtonDown=options.windowColorButtonDown; colorChanged=true; }
//	if(options.windowFontStyle!=null){ this.options.windowFontStyle=options.windowFontStyle; colorChanged=true; }
	if(options.backgroundInnerHTML!=null){this.deskElement.innerHTML=options.backgroundInnerHTML;}
    for(var i in this.windows)
	  this.windows[i].onOptionsChanged();
	if(options.desktopStyle!=null)
		this.setDesktopStyle(options.desktopStyle);
	if (options.bgImage != null)
		this.deskContainerElement.style.backgroundImage = "url('" + options.bgImage + "')";
}

Desktop.prototype.setDesktopStyle=function(style){
	this.deskStyle=style;
	applyStyle(this.deskBackgroundElement,this.deskStyle)
}

Desktop.prototype.setStyleClassPrefix=function(prefix){
  this.stylePrefix=prefix;
  this.docElement.className=this.stylePrefix+'_dashboard';
  this.addButton.className=this.stylePrefix+'_add';
  this.deskElement.className=this.stylePrefix;
  this.deskBackgroundElement.className=this.stylePrefix+'_background';
  for(var i in this.windows)
	  this.windows[i].setStyleClassPrefix(prefix);
}

Desktop.prototype.setDocletPosition=function(position){
  var docHeight=30;
  if(position=='bottom'){
    this.docElement.style.top='';
    this.docElement.style.bottom=toPx(0);
    this.docElement.style.height=toPx(docHeight);
    this.deskBackgroundElement.style.top=toPx(0);
    this.deskBackgroundElement.style.bottom=toPx(docHeight);
  }else if(position=='top'){
    this.docElement.style.top=toPx(0);
    this.docElement.style.bottom='';
    this.docElement.style.height=toPx(docHeight);
    this.deskBackgroundElement.style.top=toPx(docHeight);
    this.deskBackgroundElement.style.bottom=toPx(0);
  }else if(position=='none'){
    this.docElement.style.height=toPx(0);
    this.deskBackgroundElement.style.top=toPx(0);
    this.deskBackgroundElement.style.bottom=toPx(0);
  }
}

Desktop.prototype.removeChild=function(childId){
  var innerWindow=this.windows[childId];
  if(innerWindow == null)
	  return;
  delete this.windows[childId];
  this.deskContainerElement.removeChild(innerWindow.windowDiv);
}




Desktop.prototype.populatePortletMenu=function(menu){
  this.portlet.populatePortletMenu(menu);
  var that=this;
  menu.addItem('<img src="rsc/des.gif"> <f1:txt key="Add a Window"/>',null,function(e){that.showAdd(e);});//TODO
}

  
Desktop.prototype.addChild=function(childId,divElement,title){
  var dw=new DesktopWindow(this,childId,divElement,title);
  dw.setStyleClassPrefix(this.stylePrefix);
  dw.onOptionsChanged();
  var that=this;
  this.windows[childId]=dw;
  var thatChildId;
  dw.onUserButton=function(portletId,buttonId){that.onButton(portletId,buttonId);}
  dw.onUserMovedWindow=function(id,x,y,w,h){that.onLocationChanged(id,x,y,w,h);}
  dw.onUserRenamedWindow=function(id,text){that.onUserRename(id,text);}
  dw.onUserFocusedWindow=function(id){that.onUserFocus(id);}
  dw.onWindowLocation=function(left,top,width,height,isMax,zindex,active,fromBackend){if(that.onWindowLocation) that.onWindowLocation(left,top,width,height,isMax,zindex,active,fromBackend,thatChildId)};
  this.deskContainerElement.appendChild(dw.windowDiv);
}

Desktop.prototype.onButton=function(portletId,buttonIdj){
  if(this.onUserButton)
	  this.onUserButton(portletId,buttonIdj);
}

//Desktop.prototype.onClose=function(id){
//  if(this.onUserClosedWindow)
//	  this.onUserClosedWindow(id);
//}
//
//
//Desktop.prototype.onMax=function(id){
//  if(this.onUserMaximizedWindow)
//	  this.onUserMaximizedWindow(id);
//}
//
//Desktop.prototype.onMin=function(id){
//  if(this.onUserMinimizedWindow)
//	  this.onUserMinimizedWindow(id);
//}

Desktop.prototype.onLocationChanged=function(id,done,left,top,width,height){
  if(this.onUserMovedWindow)
	  this.onUserMovedWindow(id,done,left,top,width,height);
}

Desktop.prototype.onUserFocus=function(id){
	if(this.onUserFocusedWindow)
	  this.onUserFocusedWindow(id);
}

Desktop.prototype.setDoclets=function(list){
  removeAllChildren(this.docRow);
  this.doclets=[];
  this.docRow.appendChild(this.addButton);
	for(var i in list)
		this.addDocklet(list[i]);
}
Desktop.prototype.addDocklet=function(docletMap){
  var that=this;
  var doclet=nw('td',docletMap.active ? 'portal_doclet_active' : 'portal_doclet');
  doclet.docletid=docletMap.id;
  doclet.innerHTML=docletMap.title;
  //doclet.title=docletMap.title;
  doclet.portletId=docletMap.portletId;
  if(docletMap.active){
    makeEditable(doclet);
    doclet.onEdit=function(old,nuw){that.onUserRename(this.portletId,nuw);};
  }else{
    doclet.onclick=function(e){that.onDocSelected(e);};
  }
  
  this.docRow.appendChild(nw('td','portal_doclet_left'));
  this.docRow.appendChild(doclet);
  this.docRow.appendChild(nw('td','portal_doclet_right'));
  this.doclets[doclet.portletId]=doclet;//TODO
  var innerWindow=this.windows[doclet.portletId];
  if(innerWindow!=null){
	  try{
	  innerWindow.title.innerHTML=doclet.innerHTML;
	  }catch(e){}
  }
}
Desktop.prototype.onUserRename=function(id,text){
  if(this.onUserRenamedWindow)
	  this.onUserRenamedWindow(id,text);
}


Desktop.prototype.onDocSelected=function(event){
  this.onUserFocus(getMouseTarget(event).portletId);
}

Desktop.prototype.onUpdated=function(event){
}

Desktop.prototype.setWindowLocation=function(childId,left,top,width,height,isMax,zindex,active,headerSize,borderSize){
  this.windows[childId].setWindowLocation(left,top,width,height,isMax,zindex,active,true,headerSize,borderSize);
}

Desktop.prototype.getWindow=function(childId){
  return this.windows[childId];
}



DesktopWindow.prototype.setButtonsVisible=function(flags){
	this.buttonsVisible=flags;
	this.onOptionsChanged();
//	var pos=0;
	for(var i in this.buttons){
		var btn=this.buttons[i];
		var isVisible=flags[btn.name];
		if(btn.isVisible)
		  this.headerInnerDiv.removeChild(btn);
		btn.isVisible=isVisible;
		if(isVisible){
		  this.headerInnerDiv.appendChild(btn);
//          btn.style.top=toPx(fl((this.headerSize+this.borderSize-this.buttonHeight)/2-this.borderSize));
//          btn.style.right=toPx(pos*this.buttonWidth);
//          btn.style.height=toPx(this.buttonHeight);
//          btn.style.width=toPx(this.buttonWidth);
//          pos++;
		}
	}
}

DesktopWindow.prototype.setHasHeader=function(hasHeader){
	if(this.hasHeader==hasHeader)
		return;
	if(this.hasHeader)
		this.headerDiv.removeChild(this.headerInnerDiv);
	else
		this.headerDiv.appendChild(this.headerInnerDiv);
	this.hasHeader=hasHeader;
}
DesktopWindow.prototype.setAllowEditTitle=function(allowEditTitle){
	this.allowEditTitle=allowEditTitle;
    makeEditable(this.title,this.allowEditTitle);
    this.title.style.cursor=this.allowEditTitle ? 'text' : 'move';
    this.title.style.pointerEvents=this.allowEditTitle ? 'auto' : 'none';
}

DesktopWindow.prototype.setTitle=function(name){
  var that=this;
  this.headerInnerDiv.removeChild(this.title);
  this.title=nw('div');
  this.headerInnerDiv.appendChild(this.title);
  this.title.onEdit=function(old,nuw){that.onUserRename(nuw);};
  this.title.onmousedown=function(e){that.onUserFocus();};
  this.title.innerHTML=name;
  makeEditable(this.title,this.allowEditTitle);
  // DesktopWindow::setWindowLocation already sets this
//  this.title.style.display=this.location!=null && this.location.top!=0 ? 'block' : 'none';
  this.title.style.bottom="50%";
  this.title.style.marginBottom="1px";
  this.title.style.transform="translateY(50%)";
  this.title.style.cursor=this.allowEditTitle ? 'text' : 'move';
  this.title.style.pointerEvents=this.allowEditTitle ? 'auto' : 'none';
}

DesktopWindow.prototype.setOptions=function(options){
	this.options=options;
	this.buttonStyles={};
	if(options.windowStyleButtonMin!=null){ this.buttonStyles.min=options.windowStyleButtonMin;}
	if(options.windowStyleButtonMax!=null){ this.buttonStyles.max=options.windowStyleButtonMax;}
	if(options.windowStyleButtonPop!=null){ this.buttonStyles.pop=options.windowStyleButtonPop;}
	if(options.windowStyleButtonClose!=null){ this.buttonStyles.close=options.windowStyleButtonClose;}
	this.onOptionsChanged();
}

DesktopWindow.prototype.setWindowLocation=function(left,top,width,height,isMax,zindex,active,fromBackend,headerSizePx,borderSizePx){
	
  if(this.location!=null && this.location.left==left && this.location.top==top && this.location.width==width && this.location.height==height && this.headerSize==headerSizePx && this.borderSize==borderSizePx){
	  if(active==null || active==this.active)
		  if(zindex==null || this.windowDiv.style.zIndex==zindex)
	        return;
  }
  if(borderSizePx==null )
    borderSizePx=this.borderSize;
  if(headerSizePx==null )
    headerSizePx=this.headerSize;
  this.location=new Rect(left,top,width,height);
  if(zindex)
	  this.windowDiv.style.zIndex=zindex;
    var maxChanged=this.isMax!=isMax;
    if(maxChanged){
	  if(isMax){
          this.windowDiv.removeChild(this.left);
          this.windowDiv.removeChild(this.right);
          this.windowDiv.removeChild(this.bottom);
          this.windowDiv.removeChild(this.bottomLeft);
          this.windowDiv.removeChild(this.bottomRight);
          this.windowDiv.removeChild(this.topLeft);
          this.windowDiv.removeChild(this.topRight);
          this.windowDiv.removeChild(this.top);
	  }else{
        this.windowDiv.appendChild(this.left);
        this.windowDiv.appendChild(this.right);
        this.windowDiv.appendChild(this.bottom);
        this.windowDiv.appendChild(this.bottomLeft);
        this.windowDiv.appendChild(this.bottomRight);
        this.windowDiv.appendChild(this.topLeft);
        this.windowDiv.appendChild(this.topRight);
        this.windowDiv.appendChild(this.top);
	  }
    }
    if(maxChanged || this.borderSize!=borderSizePx || this.headerSize!=headerSizePx){
      this.headerSize=headerSizePx;
      this.borderSize=borderSizePx;
	  this.isMax=isMax;
      var headerSize=toPx(this.headerSize)
      var borderSize=toPx(this.borderSize);
      var bothSize=toPx(this.headerSize+this.borderSize)
	  if(isMax){
        this.contentDiv.style.left='0px';
        this.contentDiv.style.right='0px';
        this.contentDiv.style.bottom='0px';
        this.contentDiv.style.top=headerSize;
        this.headerDiv.style.left='0px';
        this.headerDiv.style.right='0px';
        this.headerDiv.style.top='0px';
        this.headerDiv.style.height=headerSize;
	  }else{
        this.contentDiv.style.left=borderSize;
        this.contentDiv.style.right=borderSize;
        this.contentDiv.style.bottom=borderSize;
        this.contentDiv.style.top=bothSize;
        
        this.headerDiv.style.left=borderSize;
        this.headerDiv.style.right=borderSize;
        this.headerDiv.style.top=borderSize;
        this.headerDiv.style.height=headerSize;
        
        this.left.style.top=bothSize;
        this.left.style.bottom=borderSize;
        this.left.style.width=borderSize;
        
        this.right.style.top=bothSize;
        this.right.style.bottom=borderSize;
        this.right.style.width=borderSize;
        
        this.top.style.height=borderSize;
        this.top.style.left=borderSize;
        this.top.style.right=borderSize;
        
        this.bottom.style.left=borderSize;
        this.bottom.style.right=borderSize;
        this.bottom.style.height=borderSize;
        
        this.topLeft.style.width=borderSize;
        this.topLeft.style.height=bothSize;
        
        this.topRight.style.width=borderSize;
        this.topRight.style.height=bothSize;
        
        this.bottomLeft.style.width=borderSize;
        this.bottomLeft.style.height=borderSize;
        
        this.bottomRight.style.width=borderSize;
        this.bottomRight.style.height=borderSize;
        this.onOptionsChanged();
	  }
  }
    this.title.style.display=top!=0 ? 'block' : 'none';
    var location2;
	if(isMax)
      location2=new Rect(left,top-this.headerSize,width,height+this.headerSize);
	else
      location2=new Rect(left-this.borderSize,top-(this.headerSize+this.borderSize),width+this.borderSize*2,height+(this.borderSize*2+this.headerSize));
    location2.writeToElement(this.windowDiv);
  if(active!=null && this.active!=active){
	  this.active=active;
      if(this.active)
        this.windowDiv.removeChild(this.disabledGlassDiv);
      else
        this.windowDiv.appendChild(this.disabledGlassDiv);
  }
  if(this.onWindowLocation)
	  this.onWindowLocation(left,top,width,height,isMax,zindex,active,fromBackend);
}

function DesktopWindow(desktop,childId,divElement,title){
  var that=this;
  this.desktop=desktop;
  this.options={};
  this.buttonStyles={};
  this.hasHeader=true;
  this.buttons=[];
  this.fontSize=14;
  
  
  this.isMax=null;
  this.active=false;
  this.portletId=childId;
  
  this.windowDiv=nw('div');
  this.contentDiv=nw('div');
  this.headerDiv=nw('div');
  this.headerInnerDiv=nw('div');
  this.title=nw('div');
  this.top=nw('div');
  this.bottom=nw('div');
  this.left=nw('div');
  this.right=nw('div');
  this.bottomRight=nw('div');
  this.bottomLeft=nw('div');
  this.topRight=nw('div');
  this.topLeft=nw('div');
  
  this.contentDiv.style.overflow="hidden";
  this.headerDiv.style.position='absolute';
  this.headerInnerDiv.style.cursor='move';
  this.headerDiv.portletSelectType='nav';
  this.headerInnerDiv.style.width="100%";
  this.headerInnerDiv.style.height="100%";
  
  this.top.style.position='absolute';
  this.top.style.top='0px';
  this.top.style.cursor='n-resize';
  
  this.bottom.style.position='absolute';
  this.bottom.style.bottom='0px';
  this.bottom.style.cursor='n-resize';
  
  this.left.style.position='absolute';
  this.left.style.left='0px';
  this.left.style.cursor='w-resize';
  
  this.right.style.position='absolute';
  this.right.style.right='0px';
  this.right.style.cursor='w-resize';
  
  this.bottomRight.style.position='absolute';
  this.bottomRight.style.right='0px';
  this.bottomRight.style.bottom='0px';
  this.bottomRight.style.cursor='nw-resize';
  
  this.bottomLeft.style.position='absolute';
  this.bottomLeft.style.left='0px';
  this.bottomLeft.style.bottom='0px';
  this.bottomLeft.style.cursor='ne-resize';
  
  this.topRight.style.position='absolute';
  this.topRight.style.right='0px';
  this.topRight.style.top='0px';
  this.topRight.style.cursor='ne-resize';
  
  this.topLeft.style.position='absolute';
  this.topLeft.style.left='0px';
  this.topLeft.style.top='0px';
  this.topLeft.style.cursor='nw-resize';
  
  this.title.innerHTML=title;
  
  this.headerDiv.ondraggingEnd=function(e,diffx,diffy){that.onHeaderDragging(e,diffx,diffy,true);};
  this.headerDiv.ondragging=function(e,diffx,diffy){that.onHeaderDragging(e,diffx,diffy,false);};
  this.headerDiv.ondraggingStart=function(e){that.onUserFocus();};
  this.headerDiv.onclick=function(e){that.onUserFocus();};
  
  this.headerInnerDiv.ondraggingEnd=function(e,diffx,diffy){that.onHeaderDragging(e,diffx,diffy,true);};
  this.headerInnerDiv.ondragging=function(e,diffx,diffy){that.onHeaderDragging(e,diffx,diffy,false);};
  this.headerInnerDiv.ondraggingStart=function(e){that.onUserFocus();};
  this.headerInnerDiv.onclick=function(e){that.onUserFocus();};
  
  this.title.onEdit=function(old,nuw){that.onUserRename(nuw);};
  
  this.top.ondraggingEnd=function(e,diffx,diffy){that.onTopDragging(e,diffx,diffy,true);};
  this.top.ondragging=function(e,diffx,diffy){that.onTopDragging(e,diffx,diffy,false);};
  this.top.ondraggingStart=function(e){that.onUserFocus();};
  
  this.bottom.ondraggingEnd=function(e,diffx,diffy){that.onBottomDragging(e,diffx,diffy,true);};
  this.bottom.ondragging=function(e,diffx,diffy){that.onBottomDragging(e,diffx,diffy,false);};
  this.bottom.ondraggingStart=function(e){that.onUserFocus();};
  
  this.left.ondraggingEnd=function(e,diffx,diffy){that.onLeftDragging(e,diffx,diffy,true);};
  this.left.ondragging=function(e,diffx,diffy){that.onLeftDragging(e,diffx,diffy,false);};
  this.left.ondraggingStart=function(e){that.onUserFocus();};
  
  this.right.ondraggingEnd=function(e,diffx,diffy){that.onRightDragging(e,diffx,diffy,true);};
  this.right.ondragging=function(e,diffx,diffy){that.onRightDragging(e,diffx,diffy,false);};
  this.right.ondraggingStart=function(e){that.onUserFocus();};
  
  this.bottomRight.ondraggingEnd=function(e,diffx,diffy){that.onBottomRightDragging(e,diffx,diffy,true);};
  this.bottomRight.ondragging=function(e,diffx,diffy){that.onBottomRightDragging(e,diffx,diffy,false);};
  this.bottomRight.ondraggingStart=function(e){that.onUserFocus();};
  
  this.bottomLeft.ondraggingEnd=function(e,diffx,diffy){that.onBottomLeftDragging(e,diffx,diffy,true);};
  this.bottomLeft.ondragging=function(e,diffx,diffy){that.onBottomLeftDragging(e,diffx,diffy,false);};
  this.bottomLeft.ondraggingStart=function(e){that.onUserFocus();};
  
  this.topRight.ondraggingEnd=function(e,diffx,diffy){that.onTopRightDragging(e,diffx,diffy,true);};
  this.topRight.ondragging=function(e,diffx,diffy){that.onTopRightDragging(e,diffx,diffy,false);};
  this.topRight.ondraggingStart=function(e){that.onUserFocus();};
  
  
  this.topLeft.ondraggingEnd=function(e,diffx,diffy){that.onTopLeftDragging(e,diffx,diffy,true);};
  this.topLeft.ondragging=function(e,diffx,diffy){that.onTopLeftDragging(e,diffx,diffy,false);};
  this.topLeft.ondraggingStart=function(e){that.onUserFocus();};
  
  makeDraggable(this.headerDiv,null,true,true);
  makeDraggable(this.headerInnerDiv,null,true,true);
  makeDraggable(this.top,null,true,true);
  makeDraggable(this.bottom,null,true,false);
  makeDraggable(this.left,null,false,true);
  makeDraggable(this.right,null,false,true);
  makeDraggable(this.bottomRight,null,false,false);
  makeDraggable(this.bottomLeft,null,false,false);
  makeDraggable(this.topRight,null,false,false);
  makeDraggable(this.topLeft,null,false,false);
  
  makeEditable(this.title);
  // combines with transform to make title appear vertically centered
  this.title.style.bottom="50%";
  this.title.style.marginBottom="1px";
  this.title.style.transform="translateY(50%)";
  this.headerInnerDiv.appendChild(this.title);
  this.headerDiv.appendChild(this.headerInnerDiv);
  this.windowDiv.appendChild(this.top);
  this.windowDiv.appendChild(this.headerDiv);
  this.windowDiv.appendChild(this.bottom);
  this.windowDiv.appendChild(this.left);
  this.windowDiv.appendChild(this.right);
  this.windowDiv.appendChild(this.bottomRight);
  this.windowDiv.appendChild(this.bottomLeft);
  this.windowDiv.appendChild(this.topRight);
  this.windowDiv.appendChild(this.topLeft);
  //end
  
  this.hasPopButton=false;
  this.popButton=nw('div');
  this.popButton.style.position='absolute';
  this.popButton.onlick=function(e){that.onPop(e);};
  this.popButton.style.right='64px';
  
  this.addButton('close');
  this.addButton('max');
  this.addButton('min');
  this.addButton('pop');
  
  
  //this.headerDiv.ondblclick=function(e){if(getMouseTarget(e)==that.headerDiv) that.onButton('max');};
  this.headerInnerDiv.ondblclick=function(e){if(getMouseTarget(e)==that.headerInnerDiv) that.onButton('max');};
  this.title.onmousedown=function(e){that.onUserFocus();};
  this.contentDiv.appendChild(divElement);
  this.windowDiv.appendChild(this.contentDiv);
 
  
  
  this.disabledGlassDiv=nw('div');
  this.disabledGlassDiv.style.background='black';
  this.disabledGlassDiv.style.opacity=0;
  this.disabledGlassDiv.style.zIndex=20;
  this.disabledGlassDiv.style.left='0px';
  this.disabledGlassDiv.style.top='20px';
  this.disabledGlassDiv.style.bottom='0px';
  this.disabledGlassDiv.style.right='0px';
  this.disabledGlassDiv.onmousedown=function(e){that.onUserFocus();};
  this.disabledGlassDiv.portletPeer=divElement;
  this.windowDiv.appendChild(this.disabledGlassDiv);
}

DesktopWindow.prototype.addButton=function(name){
  var that=this;
  var nm=name;
  var button=nw('div');
  button.name=name;
  button.isVisible=false;
  button.position='absolute';
  button.onclick=function(e){that.onButton(button.name);};
  button.style.right='64px';
  this.buttons[this.buttons.length]=button;
}

DesktopWindow.prototype.onButton=function(name){
  if(this.onUserButton)
	  this.onUserButton(this.portletId,name);
}

DesktopWindow.prototype.setStyleClassPrefix=function(prefix){
//  this.title.className=prefix+'_title';
  
//  this.maxButton.className=prefix+'_max_button';
//  this.closeButton.className=prefix+'_close_button';
//  this.minButton.className=prefix+'_min_button';
  
//  for(var i in this.buttons){
//	  var btn=this.buttons[i];
//	  btn.className=prefix+'_'+btn.name+'_button';
//	  var style=this.buttonStyles[btn.name];
//  }
  
//  this.topLeft.className=prefix+'_top_left';
//  
//  this.left.className=prefix+'_left';
//  this.bottomRight.className=prefix+'_bottom_right';
//  this.bottomLeft.className=prefix+'_bottom_left';
//  this.topRight.className=prefix+'_top_right';
//  this.contentDiv.className=prefix+'_content';
//  this.top.className=prefix+'_top';
//  this.headerDiv.className=prefix+'_header';
//  this.bottom.className=prefix+'_bottom';
//  this.right.className=prefix+'_right';
}

DesktopWindow.prototype.onOptionsChanged=function(){
  var desktopOptions=this.desktop==null ? {} : this.desktop.options;
  var windowColor=this.options.windowColor || desktopOptions.windowColor || '#007608';
  var windowColorUp=this.options.windowColorUp || desktopOptions.windowColorUp || '#FFFFFF';
  var windowColorDown=this.options.windowColorDown || desktopOptions.windowColorDown || '#FFFFFF';
  var windowColorText=this.options.windowColorText || desktopOptions.windowColorText || '#FFFFFF';
  var windowColorButton=this.options.windowColorButton || desktopOptions.windowColorButton || '#E2E2E2';
  var windowColorButtonUp=this.options.windowColorButtonUp || desktopOptions.windowColorButtonUp || '#E2E2E2';
  var windowColorButtonDown=this.options.windowColorButtonDown || desktopOptions.windowColorButtonDown || '#E2E2E2';
  var windowColorButtonIcon=this.options.windowColorButtonIcon || desktopOptions.windowColorButtonIcon || '#000000';
  var windowBorderInnerSize=(this.options.windowBorderInnerSize || desktopOptions.windowBorderInnerSize || '0');
  var windowBorderOuterSize=(this.options.windowBorderOuterSize || desktopOptions.windowBorderOuterSize || '1');
  var buttonWidth=this.options.windowButtonWidth || desktopOptions.windowButtonWidth || 32
  var buttonHeight=this.options.windowButtonHeight || desktopOptions.windowButtonHeight || 13;
  var iPx=toPx(windowBorderInnerSize);
  var oPx=toPx(windowBorderOuterSize);
  
  var windowFontStyle=this.options.windowFontStyle || desktopOptions.windowFontStyle || '';
  var borderColor=windowColorUp+' '+windowColorDown+' '+windowColorDown+' '+windowColorUp;
 
  this.title.style.color=windowColorText;
  applyStyle(this.title,windowFontStyle);
  
  this.top.style.background=windowColor;
  this.top.style.borderWidth=oPx+' 0px 0px 0px';
  this.top.style.borderColor=windowColorUp;
  this.top.style.borderStyle='solid';
  
  this.bottom.style.background=windowColor;
  this.bottom.style.borderWidth= iPx+' 0px '+oPx+' 0px';
  this.bottom.style.borderColor=borderColor;
  this.bottom.style.borderStyle='solid';
  
  this.left.style.background=windowColor;
  this.left.style.borderWidth='0px '+iPx+' 0px '+oPx;
  this.left.style.borderColor=borderColor;
  this.left.style.borderStyle='solid';
  
  this.right.style.background=windowColor;
  this.right.style.borderWidth='0px '+oPx+' 0px '+iPx;
  this.right.style.borderColor=borderColor;
  this.right.style.borderStyle='solid';
  
  if(this.location!=null && this.location.top>0){
    this.headerDiv.style.background=windowColor;
    this.headerDiv.style.borderWidth='0px 0px '+iPx+' 0px';
    this.headerDiv.style.borderColor=borderColor;
    this.headerDiv.style.borderStyle='solid';
  }else{
    this.headerDiv.style.background=null;
    this.headerDiv.style.borderWidth=null;
    this.headerDiv.style.borderColor=null;
    this.headerDiv.style.borderStyle=null;
  }
  
//  this.title.style.borderWidth='0px 0px '+iPx+' 0px';
//  this.title.style.borderColor=borderColor;
//  this.title.style.borderStyle='solid';
  
  this.topLeft.style.background=windowColor;
  this.topLeft.style.borderWidth=oPx+' 0px 0px '+oPx;
  this.topLeft.style.borderColor=borderColor;
  this.topLeft.style.borderStyle='solid';
  
  this.bottomRight.style.background=windowColor;
  this.bottomRight.style.borderWidth='0px '+oPx+' '+oPx+' 0px';
  this.bottomRight.style.borderColor=borderColor;
  this.bottomRight.style.borderStyle='solid';
  
  this.bottomLeft.style.background=windowColor;
  this.bottomLeft.style.borderWidth='0px 0px '+oPx+' '+oPx;
  this.bottomLeft.style.borderColor=borderColor;
  this.bottomLeft.style.borderStyle='solid';
  
  this.topRight.style.background=windowColor;
  this.topRight.style.borderWidth=oPx+' '+oPx+' 0px 0px';
  this.topRight.style.borderColor=borderColor;
  this.topRight.style.borderStyle='solid';
  
  var buttonBorder=windowColorButtonUp+' '+windowColorButtonDown+' '+windowColorButtonDown+' '+windowColorButtonUp;
  
  var btnColor="#000000";
  var pos=0; 
  svgStyle='fill="none" stroke="'+windowColorButtonIcon+'"';
  svgStyle=svgStyle.replace(/#/g, "%23");
  for(var i in this.buttons){
	  var btn=this.buttons[i];
		var isVisible=this.buttonsVisible==null || this.buttonsVisible[btn.name];
		if(!isVisible){
		  if(btn.isVisible)
		    this.headerInnerDiv.removeChild(btn);
		}else{
		  if(!btn.isVisible)
		    this.headerInnerDiv.appendChild(btn);
          btn.style.backgroundColor=windowColorButton;
          btn.style.borderWidth='1px';
          btn.style.borderColor=buttonBorder;
          btn.style.borderStyle='solid';
	      btn.className='portal_desktop_button';
	      btn.style.backgroundImage=(DESKTOP_BUTTON_IMAGE_PREFIX+svgStyle+DESKTOP_BUTTON_IMAGE[btn.name == 'max' && this.isMax ? 'maxAlt' : btn.name]);
	      var style=this.buttonStyles[btn.name];
	      if(this.isMax)
            btn.style.top=toPx(fl((this.headerSize-buttonHeight)/2));
	      else
            btn.style.top=toPx(max(fl((this.headerSize+this.borderSize-buttonHeight)/2-this.borderSize),0));
          btn.style.right=toPx(pos*buttonWidth);
          btn.style.height=toPx(buttonHeight);
          btn.style.width=toPx(buttonWidth);
	      if(style!=null)
		      applyStyle(btn,style);
	      pos++;
		}
		btn.isVisible=isVisible;
  }
  
  this.top.style.background=windowColor;
}

DesktopWindow.prototype.getHeaderDiv=function(){
	return this.headerDiv;
}



DesktopWindow.prototype.onTopDragging=function(element,x,y,done){
  if(this.isMax)return;
  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top+y,this.predragLocation.width,this.predragLocation.height-y,false,null,this.active,false );
  if(done)
    this.onLocationChanged();
}

DesktopWindow.prototype.onHeaderDragging=function(element,x,y,done){
  if(this.isMax)return;
  this.setWindowLocation(this.predragLocation.left+x,this.predragLocation.top+y,this.predragLocation.width,this.predragLocation.height,false,null,this.active,false );
  if(done)
    this.onLocationChanged();
}

DesktopWindow.prototype.onBottomDragging=function(element,x,y,done){
  if(this.isMax)return;
  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top,this.predragLocation.width,this.predragLocation.height+y,false,null,this.active,false );
  if(done)
    this.onLocationChanged();
}

DesktopWindow.prototype.onRightDragging=function(element,x,y,done){
  if(this.isMax)return;
  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top,this.predragLocation.width+x,this.predragLocation.height,false,null,this.active,false );
  if(done)
    this.onLocationChanged();
}

DesktopWindow.prototype.onLeftDragging=function(element,x,y,done){
  if(this.isMax)return;
  this.setWindowLocation(this.predragLocation.left+x,this.predragLocation.top,this.predragLocation.width-x,this.predragLocation.height,false,null,this.active,false );
  if(done)
    this.onLocationChanged();
}

DesktopWindow.prototype.onBottomLeftDragging=function(element,x,y,done){
  if(this.isMax)return;
  this.setWindowLocation(this.predragLocation.left+x,this.predragLocation.top,this.predragLocation.width-x,this.predragLocation.height+y,false,null,this.active,false );
  if(done)
    this.onLocationChanged();
}

DesktopWindow.prototype.onTopLeftDragging=function(element,x,y,done){
  if(this.isMax)return;
  this.setWindowLocation(this.predragLocation.left+x,this.predragLocation.top+y,this.predragLocation.width-x,this.predragLocation.height-y,false,null,this.active,false );
  if(done)
    this.onLocationChanged();
}

DesktopWindow.prototype.onTopRightDragging=function(element,x,y,done){
  if(this.isMax)return;
  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top+y,this.predragLocation.width+x,this.predragLocation.height-y,false,null,this.active,false );
  if(done)
    this.onLocationChanged();
}

DesktopWindow.prototype.onBottomRightDragging=function(element,x,y,done){
  if(this.isMax)return;
  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top,this.predragLocation.width+x,this.predragLocation.height+y,false ,null,this.active,false);
  if(done)
    this.onLocationChanged();
}
//DesktopWindow.prototype.onClose=function(){
//  if(this.onUserClosedWindow)
//	  this.onUserClosedWindow(this.portletId);
//}
//
//DesktopWindow.prototype.onMax=function(){
//  if(this.onUserMaximizedWindow)
//	  this.onUserMaximizedWindow(this.portletId);
//}
//
//DesktopWindow.prototype.onMin=function(){
//  if(this.onUserMinimizedWindow)
//	  this.onUserMinimizedWindow(this.portletId);
//}

DesktopWindow.prototype.onLocationChanged=function(){
	  this.onUserMovedWindow(this.portletId,this.location.left,this.location.top,this.location.width,this.location.height);
}

DesktopWindow.prototype.onUserFocus=function(){
	if(this.onUserFocusedWindow)
	  this.onUserFocusedWindow(this.portletId);
	this.predragLocation=this.location.clone();
}

DesktopWindow.prototype.onUserRename=function(text){
  if(this.onUserRenamedWindow)
	  this.onUserRenamedWindow(this.portletId,text);
}


function MenuBar(divElement){
  this.divElement=divElement;
  this.window=getWindow(divElement);
  this.innerDivElement=nw('div');
  this.divElement.className='portal_menubar_background';
  this.menuStyle='portal_menubar_menu';
  this.divElement.style.left='0px';
  this.divElement.style.top='0px';
  this.divElement.style.right='0px';
  this.divElement.style.height='25px';
  this.divElement.style.overflowX='hidden';
  this.divElement.style.overflowY='hidden';
  this.divElement.style.whiteSpace='nowrap';
  this.divElement.appendChild(this.innerDivElement);
  this.cssStyle = "";
}
  
MenuBar.prototype.setOptions=function(options){
    this.align=options.align;
	this.goUp=options.goUp;
}

MenuBar.prototype.showMenu=function(id,menuConfig){
	if(this.currentMenu!=null){
		this.currentMenu.hide();
		this.currentMenu=null;
	}
	if(this.currentMenuDiv!=null){
		this.currentMenuDiv.className=this.menuStyle;
    }
	for(var i in this.menuDivs){
		if(this.menuDivs[i].menu.id==id){
		  this.currentMenuDiv=this.menuDivs[i];
		  break;
		}
	}
  if(this.currentMenuDiv==null || this.currentMenuDiv.menu.id!=id)
	  return;
  var rect=new Rect();
  rect.readFromElement(this.currentMenuDiv);
  var point=new Point(rect.getLeft(),this.goUp=='true' ? rect.getTop()+1 : rect.getBottom());
  var menu=this.createMenu(menuConfig);
  if (menu.isEmpty()) 
  	return;
  menu.show(point,null,null,null,this.goUp=='true');
  var that=this;
  menu.onHide=function(){that.onMenuHidden()};
  rect.readFromElement(this.innerDivElement);
  if(this.innerDivElement.parentNode!=null)
      this.innerDivElement.parentNode.removeChild(this.innerDivElement);
  this.innerDivElement.oldWidth=this.innerDivElement.style.width;
  this.innerDivElement.oldLeft=this.innerDivElement.style.left;
  this.innerDivElement.oldTop=this.innerDivElement.style.top;
  if(this.goUp=='true')
    this.innerDivElement.style.top=(rect.getTop())+'px';
  else 
  this.innerDivElement.style.top=(rect.getTop()-1)+'px';
  //TODO: pay attention to this.align:
  this.innerDivElement.style.left=(rect.getLeft()-1)+'px';
  for(var i in this.menuDivs){
	  var md=this.menuDivs[i];
      md.onmouseover=function(e){that.onUserMenu(e,arguments.callee.div);};
	  md.onmouseover.div=md;
  }
  menu.glass.appendChild(this.innerDivElement);
  menu.glass.style.overflowX='hidden';
  menu.glass.style.overflowY='hidden';
  menu.glass.style.whiteSpace='nowrap';
  this.currentMenu=menu;
}

MenuBar.prototype.onMenuHidden=function(){
  this.currentMenuDiv.className=this.menuStyle;
  this.currentMenu.glass.removeChild(this.innerDivElement);
  this.innerDivElement.style.top=this.innerDivElement.oldTop;
  this.innerDivElement.style.left=this.innerDivElement.oldLeft;
  this.innerDivElement.style.width=this.innerDivElement.oldWidth;
  this.divElement.appendChild(this.innerDivElement);
  this.currentMenu=null;
  this.currentMenuDiv=null;
  for(var i in this.menuDivs){
	  var md=this.menuDivs[i];
      md.onmouseover=null;
  }
}


MenuBar.prototype.createMenu=function(menu){
   var that=this;
   var r=new Menu(this.window);
   if(menu==null)
     return r;
   r.createMenu(menu, function(e,id){that.onUserMenuItem(e,id);});
   return r;
}

MenuBar.prototype.onUserMenuItem=function(e,id){
	if(this.onUserClickedMenuItem)
	  this.onUserClickedMenuItem(id);
}
MenuBar.prototype.onUserMenu=function(e,div){
  if(this.currentMenuDiv==div)
	  return;
	if(this.onUserOverMenu){
//	  this.currentMenuDiv=div;
//	  this.currentMenuDiv.className=this.menuStyle+ ' '+this.menuStyle+'_hover';
	  this.onUserOverMenu(div.menu.id);
	}
}

MenuBar.prototype.setMenus=function(menus){
  this.menuDivs=[];
  var that=this;
  removeAllChildren(this.innerDivElement);
  for(var i in menus){
	  var menu=nw('div',this.menuStyle);
	  menu.style.display='inline-block';
	  menu.style.position='static';
	  menu.innerHTML=menus[i].text;
	  menu.menu=menus[i];
	  menu.onmousedown=function(e){that.onUserMenu(e,arguments.callee.div);};
	  menu.onmousedown.div=menu;
	  this.menuDivs[this.menuDivs.length]=menu;
	  this.innerDivElement.appendChild(menu);
  }
}
MenuBar.prototype.setCssStyle=function(cssStyle){
	if(this.cssStyle == cssStyle)
	   return;
	this.cssStyle = cssStyle;
    delete this.divElement.style;
    delete this.divElement.class;
    this.divElement.style.left='0px';
    this.divElement.style.top='0px';
    this.divElement.style.right='0px';
    this.divElement.style.height='25px';
    this.divElement.style.overflowX='hidden';
    this.divElement.style.overflowY='hidden';
    this.divElement.style.whiteSpace='nowrap';
	applyStyle(this.divElement,this.cssStyle);
    this.innerDivElement.style.color = this.divElement.style.color;
}
