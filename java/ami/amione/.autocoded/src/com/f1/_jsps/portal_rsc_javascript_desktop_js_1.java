package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_desktop_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_desktop_js_1() {
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
            "//###########################\r\n"+
            "//##### Desktop Portlet #####\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "var DESKTOP_BUTTON_IMAGE_PREFIX=SVG_PREFIX+' x=\"0px\" y=\"0px\" viewBox=\"0 0 12 12\" style=\"enable-background:new 0 0 12 12;\" xml:space=\"preserve\"><g style=\"stroke-width:1;stroke-miterlimit:10;vector-effect:non-scaling-stroke;\" ';// <g fill=\"#FFFFFF\" stroke=\"#FFFFFF\"> ';\r\n"+
            "var DESKTOP_BUTTON_IMAGE=[];\r\n"+
            "DESKTOP_BUTTON_IMAGE['close']='><path d=\"M2 2L10 10M2 10L10 2\"/></g>'+SVG_SUFFIX;\r\n"+
            "DESKTOP_BUTTON_IMAGE['min']  ='><path d=\"M1 9h10\" /></g> '+SVG_SUFFIX;\r\n"+
            "DESKTOP_BUTTON_IMAGE['max']  ='><path d=\"M2 2h8v8h-8v-8\"/></g> '+SVG_SUFFIX;\r\n"+
            "DESKTOP_BUTTON_IMAGE['pop']  ='><path d=\"M4 8L10 2v4M10 2h-4\"/><path d=\"M4 2h-2v8h8v-3\"/></g> '+SVG_SUFFIX;\r\n"+
            "DESKTOP_BUTTON_IMAGE['maxAlt']  ='><path d=\"M2 4h6v6h-6v-6\"/><path d=\"M4 4v-2h6v6h-2\"/></g> '+SVG_SUFFIX;\r\n"+
            "\r\n"+
            "function Desktop(divElement){\r\n"+
            "  this.divElement=divElement;\r\n"+
            "  var that=this;\r\n"+
            "  this.deskElement=nw('div');\r\n"+
            "  this.deskBackgroundElement=nw('div');\r\n"+
            "  this.deskBackgroundElement.style.top='0px';\r\n"+
            "  this.deskBackgroundElement.style.left='0px';\r\n"+
            "  this.deskBackgroundElement.style.right='0px';\r\n"+
            "  this.deskBackgroundElement.style.bottom='0px';\r\n"+
            "  \r\n"+
            "  this.deskElement.style.position='absolute';\r\n"+
            "  this.deskElement.style.top='0px';\r\n"+
            "  this.deskElement.style.left='0px';\r\n"+
            "  this.deskElement.style.right='0px';\r\n"+
            "  this.deskElement.style.bottom='0px';\r\n"+
            "  this.deskElement.className='portal_desktop';\r\n"+
            "  this.deskElement.style.overflow='hidden';\r\n"+
            "  this.deskContainerElement=nw('div', \"ami_desktop_container\");\r\n"+
            "  var _doc = getDocument(this.divElement);\r\n"+
            "  _doc.deskContainerElement=this.deskContainerElement;\r\n"+
            "  this.deskBackgroundElement.appendChild(this.deskContainerElement);\r\n"+
            "  this.deskContainerElement.appendChild(this.deskElement);\r\n"+
            "  this.divElement.appendChild(this.deskBackgroundElement);\r\n"+
            "  \r\n"+
            "  this.divElement.classList.add(\"amidivElement\");\r\n"+
            "  this.deskContainerElement.scroll=\"no\";\r\n"+
            "  this.deskContainerElement.tabindex=\"-1\";\r\n"+
            "\r\n"+
            "  \r\n"+
            "  this.docElement=nw('div');\r\n"+
            "  this.docTable=nw('table');\r\n"+
            "  this.docRow=nw('tr');\r\n"+
            "  this.docElement.appendChild(this.docTable);\r\n"+
            "  this.docTable.appendChild(this.docRow);\r\n"+
            "  this.divElement.appendChild(this.docElement);\r\n"+
            "  \r\n"+
            "  this.nextZindex=0;\r\n"+
            "  this.windows=[];\r\n"+
            "  this.doclets=[];\r\n"+
            "  var bottom=true;\r\n"+
            "  this.setDocletPosition('bottom');\r\n"+
            "  this.isContainer=true;\r\n"+
            "  this.addButton=nw('td');\r\n"+
            "  this.addButton.onclick=function(e){if(that.onUserClickedButton) that.onUserClickedButton(e);};\r\n"+
            "  this.deskElement.ondblclick=function(e){if(getMouseTarget(e)==that.deskElement && that.onUserDoubleclickedBackground) that.onUserDoubleclickedBackground(e)};\r\n"+
            "  \r\n"+
            "  this.options={};\r\n"+
            "  this.setStyleClassPrefix('portal_desktop');\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.setOptions=function(options){\r\n"+
            "	if(options.docletPosition!=null)\r\n"+
            "		this.setDocletPosition(options.docletPosition);\r\n"+
            "	if(options.styleClassPrefix!=null)\r\n"+
            "		this.setStyleClassPrefix(options.styleClassPrefix);\r\n"+
            "	var colorChanged=false;\r\n"+
            "	this.options=options;\r\n"+
            "//	if(options.windowColor!=null){ this.options.windowColor=options.windowColor; colorChanged=true; }\r\n"+
            "//	if(options.windowColorUp!=null){ this.options.windowColorUp=options.windowColorUp; colorChanged=true; }\r\n"+
            "//	if(options.windowColorDown!=null){ this.options.windowColorDown=options.windowColorDown; colorChanged=true; }\r\n"+
            "//	if(options.windowColorText!=null){ this.options.windowColorText=options.windowColorText; colorChanged=true; }\r\n"+
            "//	if(options.windowColorButton!=null){ this.options.windowColorButton=options.windowColorButton; colorChanged=true; }\r\n"+
            "	if(options.windowColorButtonUp!=null){ this.options.windowColorButtonUp=options.windowColorButtonUp; colorChanged=true; }\r\n"+
            "//	if(options.windowColorButtonDown!=null){ this.options.windowColorButtonDown=options.windowColorButtonDown; colorChanged=true; }\r\n"+
            "//	if(options.windowFontStyle!=null){ this.options.windowFontStyle=options.windowFontStyle; colorChanged=true; }\r\n"+
            "	if(options.backgroundInnerHTML!=null){this.deskElement.innerHTML=options.backgroundInnerHTML;}\r\n"+
            "    for(var i in this.windows)\r\n"+
            "	  this.windows[i].onOptionsChanged();\r\n"+
            "	if(options.desktopStyle!=null)\r\n"+
            "		this.setDesktopStyle(options.desktopStyle);\r\n"+
            "	if (options.bgImage != null)\r\n"+
            "		this.deskContainerElement.style.backgroundImage = \"url('\" + options.bgImage + \"')\";\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.setDesktopStyle=function(style){\r\n"+
            "	this.deskStyle=style;\r\n"+
            "	applyStyle(this.deskBackgroundElement,this.deskStyle)\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.setStyleClassPrefix=function(prefix){\r\n"+
            "  this.stylePrefix=prefix;\r\n"+
            "  this.docElement.className=this.stylePrefix+'_dashboard';\r\n"+
            "  this.addButton.className=this.stylePrefix+'_add';\r\n"+
            "  this.deskElement.className=this.stylePrefix;\r\n"+
            "  this.deskBackgroundElement.className=this.stylePrefix+'_background';\r\n"+
            "  for(var i in this.windows)\r\n"+
            "	  this.windows[i].setStyleClassPrefix(prefix);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.setDocletPosition=function(position){\r\n"+
            "  var docHeight=30;\r\n"+
            "  if(position=='bottom'){\r\n"+
            "    this.docElement.style.top='';\r\n"+
            "    this.docElement.style.bottom=toPx(0);\r\n"+
            "    this.docElement.style.height=toPx(docHeight);\r\n"+
            "    this.deskBackgroundElement.style.top=toPx(0);\r\n"+
            "    this.deskBackgroundElement.style.bottom=toPx(docHeight);\r\n"+
            "  }else if(position=='top'){\r\n"+
            "    this.docElement.style.top=toPx(0);\r\n"+
            "    this.docElement.style.bottom='';\r\n"+
            "    this.docElement.style.height=toPx(docHeight);\r\n"+
            "    this.deskBackgroundElement.style.top=toPx(docHeight);\r\n"+
            "    this.deskBackgroundElement.style.bottom=toPx(0);\r\n"+
            "  }else if(position=='none'){\r\n"+
            "    this.docElement.style.height=toPx(0);\r\n"+
            "    this.deskBackgroundElement.style.top=toPx(0);\r\n"+
            "    this.deskBackgroundElement.style.bottom=toPx(0);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.removeChild=function(childId){\r\n"+
            "  var innerWindow=this.windows[childId];\r\n"+
            "  if(innerWindow == null)\r\n"+
            "	  return;\r\n"+
            "  delete this.windows[childId];\r\n"+
            "  this.deskContainerElement.removeChild(innerWindow.windowDiv);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "Desktop.prototype.populatePortletMenu=function(menu){\r\n"+
            "  this.portlet.populatePortletMenu(menu);\r\n"+
            "  var that=this;\r\n"+
            "  menu.addItem('<img src=\"rsc/des.gif\"> ");
          com.f1.http.tag.FormatTextTag.append(out,formatter,"Add a Window");
          out.print(
            "',null,function(e){that.showAdd(e);});//TODO\r\n"+
            "}\r\n"+
            "\r\n"+
            "  \r\n"+
            "Desktop.prototype.addChild=function(childId,divElement,title){\r\n"+
            "  var dw=new DesktopWindow(this,childId,divElement,title);\r\n"+
            "  dw.setStyleClassPrefix(this.stylePrefix);\r\n"+
            "  dw.onOptionsChanged();\r\n"+
            "  var that=this;\r\n"+
            "  this.windows[childId]=dw;\r\n"+
            "  var thatChildId;\r\n"+
            "  dw.onUserButton=function(portletId,buttonId){that.onButton(portletId,buttonId);}\r\n"+
            "  dw.onUserMovedWindow=function(id,x,y,w,h){that.onLocationChanged(id,x,y,w,h);}\r\n"+
            "  dw.onUserRenamedWindow=function(id,text){that.onUserRename(id,text);}\r\n"+
            "  dw.onUserFocusedWindow=function(id){that.onUserFocus(id);}\r\n"+
            "  dw.onWindowLocation=function(left,top,width,height,isMax,zindex,active,fromBackend){if(that.onWindowLocation) that.onWindowLocation(left,top,width,height,isMax,zindex,active,fromBackend,thatChildId)};\r\n"+
            "  this.deskContainerElement.appendChild(dw.windowDiv);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.onButton=function(portletId,buttonIdj){\r\n"+
            "  if(this.onUserButton)\r\n"+
            "	  this.onUserButton(portletId,buttonIdj);\r\n"+
            "}\r\n"+
            "\r\n"+
            "//Desktop.prototype.onClose=function(id){\r\n"+
            "//  if(this.onUserClosedWindow)\r\n"+
            "//	  this.onUserClosedWindow(id);\r\n"+
            "//}\r\n"+
            "//\r\n"+
            "//\r\n"+
            "//Desktop.prototype.onMax=function(id){\r\n"+
            "//  if(this.onUserMaximizedWindow)\r\n"+
            "//	  this.onUserMaximizedWindow(id);\r\n"+
            "//}\r\n"+
            "//\r\n"+
            "//Desktop.prototype.onMin=function(id){\r\n"+
            "//  if(this.onUserMinimizedWindow)\r\n"+
            "//	  this.onUserMinimizedWindow(id);\r\n"+
            "//}\r\n"+
            "\r\n"+
            "Desktop.prototype.onLocationChanged=function(id,done,left,top,width,height){\r\n"+
            "  if(this.onUserMovedWindow)\r\n"+
            "	  this.onUserMovedWindow(id,done,left,top,width,height);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.onUserFocus=function(id){\r\n"+
            "	if(this.onUserFocusedWindow)\r\n"+
            "	  this.onUserFocusedWindow(id);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.setDoclets=function(list){\r\n"+
            "  removeAllChildren(this.docRow);\r\n"+
            "  this.doclets=[];\r\n"+
            "  this.docRow.appendChild(this.addButton);\r\n"+
            "	for(var i in list)\r\n"+
            "		this.addDocklet(list[i]);\r\n"+
            "}\r\n"+
            "Desktop.prototype.addDocklet=function(docletMap){\r\n"+
            "  var that=this;\r\n"+
            "  var doclet=nw('td',docletMap.active ? 'portal_doclet_active' : 'portal_doclet');\r\n"+
            "  doclet.docletid=docletMap.id;\r\n"+
            "  doclet.innerHTML=docletMap.title;\r\n"+
            "  //doclet.title=docletMap.title;\r\n"+
            "  doclet.portletId=docletMap.portletId;\r\n"+
            "  if(docletMap.active){\r\n"+
            "    makeEditable(doclet);\r\n"+
            "    doclet.onEdit=function(old,nuw){that.onUserRename(this.portletId,nuw);};\r\n"+
            "  }else{\r\n"+
            "    doclet.onclick=function(e){that.onDocSelected(e);};\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  this.docRow.appendChild(nw('td','portal_doclet_left'));\r\n"+
            "  this.docRow.appendChild(doclet);\r\n"+
            "  this.docRow.appendChild(nw('td','portal_doclet_right'));\r\n"+
            "  this.doclets[doclet.portletId]=doclet;//TODO\r\n"+
            "  var innerWindow=this.windows[doclet.portletId];\r\n"+
            "  if(innerWindow!=null){\r\n"+
            "	  try{\r\n"+
            "	  innerWindow.title.innerHTML=doclet.innerHTML;\r\n"+
            "	  }catch(e){}\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "Desktop.prototype.onUserRename=function(id,text){\r\n"+
            "  if(this.onUserRenamedWindow)\r\n"+
            "	  this.onUserRenamedWindow(id,text);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "Desktop.prototype.onDocSelected=function(event){\r\n"+
            "  this.onUserFocus(getMouseTarget(event).portletId);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.onUpdated=function(event){\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.setWindowLocation=function(childId,left,top,width,height,isMax,zindex,active,headerSize,borderSize){\r\n"+
            "  this.windows[childId].setWindowLocation(left,top,width,height,isMax,zindex,active,true,headerSize,borderSize);\r\n"+
            "}\r\n"+
            "\r\n"+
            "Desktop.prototype.getWindow=function(childId){\r\n"+
            "  return this.windows[childId];\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.setButtonsVisible=function(flags){\r\n"+
            "	this.buttonsVisible=flags;\r\n"+
            "	this.onOptionsChanged();\r\n"+
            "//	var pos=0;\r\n"+
            "	for(var i in this.buttons){\r\n"+
            "		var btn=this.buttons[i];\r\n"+
            "		var isVisible=flags[btn.name];\r\n"+
            "		if(btn.isVisible)\r\n"+
            "		  this.headerInnerDiv.removeChild(btn);\r\n"+
            "		btn.isVisible=isVisible;\r\n"+
            "		if(isVisible){\r\n"+
            "		  this.headerInnerDiv.appendChild(btn);\r\n"+
            "//          btn.style.top=toPx(fl((this.headerSize+this.borderSize-this.buttonHeight)/2-this.borderSize));\r\n"+
            "//          btn.style.right=toPx(pos*this.buttonWidth);\r\n"+
            "//          btn.style.height=toPx(this.buttonHeight);\r\n"+
            "//          btn.style.width=toPx(this.buttonWidth);\r\n"+
            "//          pos++;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.setHasHeader=function(hasHeader){\r\n"+
            "	if(this.hasHeader==hasHeader)\r\n"+
            "		return;\r\n"+
            "	if(this.hasHeader)\r\n"+
            "		this.headerDiv.removeChild(this.headerInnerDiv);\r\n"+
            "	else\r\n"+
            "		this.headerDiv.appendChild(this.headerInnerDiv);\r\n"+
            "	this.hasHeader=hasHeader;\r\n"+
            "}\r\n"+
            "DesktopWindow.prototype.setAllowEditTitle=function(allowEditTitle){\r\n"+
            "	this.allowEditTitle=allowEditTitle;\r\n"+
            "    makeEditable(this.title,this.allowEditTitle);\r\n"+
            "    this.title.style.cursor=this.allowEditTitle ? 'text' : 'move';\r\n"+
            "    this.title.style.pointerEvents=this.allowEditTitle ? 'auto' : 'none';\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.setTitle=function(name){\r\n"+
            "  var that=this;\r\n"+
            "  this.headerInnerDiv.removeChild(this.title);\r\n"+
            "  this.title=nw('div');\r\n"+
            "  this.headerInnerDiv.appendChild(this.title);\r\n"+
            "  this.title.onEdit=function(old,nuw){that.onUserRename(nuw);};\r\n"+
            "  this.title.onmousedown=function(e){that.onUserFocus();};\r\n"+
            "  this.title.innerHTML=name;\r\n"+
            "  makeEditable(this.title,this.allowEditTitle);\r\n"+
            "  // DesktopWindow::setWindowLocation already sets this\r\n"+
            "//  this.title.style.display=this.location!=null && this.location.top!=0 ? 'block' : 'none';\r\n"+
            "  this.title.style.bottom=\"50%\";\r\n"+
            "  this.title.style.marginBottom=\"1px\";\r\n"+
            "  this.title.style.transform=\"translateY(50%)\";\r\n"+
            "  this.title.style.cursor=this.allowEditTitle ? 'text' : 'move';\r\n"+
            "  this.title.style.pointerEvents=this.allowEditTitle ? 'auto' : 'none';\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.setOptions=function(options){\r\n"+
            "	this.options=options;\r\n"+
            "	this.buttonStyles={};\r\n"+
            "	if(options.windowStyleButtonMin!=null){ this.buttonStyles.min=options.windowStyleButtonMin;}\r\n"+
            "	if(options.windowStyleButtonMax!=null){ this.buttonStyles.max=options.windowStyleButtonMax;}\r\n"+
            "	if(options.windowStyleButtonPop!=null){ this.buttonStyles.pop=options.windowStyleButtonPop;}\r\n"+
            "	if(options.windowStyleButtonClose!=null){ this.buttonStyles.close=options.windowStyleButtonClose;}\r\n"+
            "	this.onOptionsChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.setWindowLocation=function(left,top,width,height,isMax,zindex,active,fromBackend,headerSizePx,borderSizePx){\r\n"+
            "	\r\n"+
            "  if(this.location!=null && this.location.left==left && this.location.top==top && this.location.width==width && this.location.height==height && this.headerSize==headerSizePx && this.borderSize==borderSizePx){\r\n"+
            "	  if(active==null || active==this.active)\r\n"+
            "		  if(zindex==null || this.windowDiv.style.zIndex==zindex)\r\n"+
            "	        return;\r\n"+
            "  }\r\n"+
            "  if(borderSizePx==null )\r\n"+
            "    borderSizePx=this.borderSize;\r\n"+
            "  if(headerSizePx==null )\r\n"+
            "    headerSizePx=this.headerSize;\r\n"+
            "  this.location=new Rect(left,top,width,height);\r\n"+
            "  if(zindex)\r\n"+
            "	  this.windowDiv.style.zIndex=zindex;\r\n"+
            "    var maxChanged=this.isMax!=isMax;\r\n"+
            "    if(maxChanged){\r\n"+
            "	  if(isMax){\r\n"+
            "          this.windowDiv.removeChild(this.left);\r\n"+
            "          this.windowDiv.removeChild(this.right);\r\n"+
            "          this.windowDiv.removeChild(this.bottom);\r\n"+
            "          this.windowDiv.removeChild(this.bottomLeft);\r\n"+
            "          this.windowDiv.removeChild(this.bottomRight);\r\n"+
            "          this.windowDiv.removeChild(this.topLeft);\r\n"+
            "          this.windowDiv.removeChild(this.topRight);\r\n"+
            "          this.windowDiv.removeChild(this.top);\r\n"+
            "	  }else{\r\n"+
            "        this.windowDiv.appendChild(this.left);\r\n"+
            "        this.windowDiv.appendChild(this.right);\r\n"+
            "        this.windowDiv.appendChild(this.bottom);\r\n"+
            "        this.windowDiv.appendChild(this.bottomLeft);\r\n"+
            "        this.windowDiv.appendChild(this.bottomRight);\r\n"+
            "        this.windowDiv.appendChild(this.topLeft);\r\n"+
            "        this.windowDiv.appendChild(this.topRight);\r\n"+
            "        this.windowDiv.appendChild(this.top);\r\n"+
            "	  }\r\n"+
            "    }\r\n"+
            "    if(maxChanged || this.borderSize!=borderSizePx || this.headerSize!=headerSizePx){\r\n"+
            "      this.headerSize=headerSizePx;\r\n"+
            "      this.borderSize=borderSizePx;\r\n"+
            "	  this.isMax=isMax;\r\n"+
            "      var headerSize=toPx(this.headerSize)\r\n"+
            "      var borderSize=toPx(this.borderSize);\r\n"+
            "      var bothSize=toPx(this.headerSize+this.borderSize)\r\n"+
            "	  if(isMax){\r\n"+
            "        this.contentDiv.style.left='0px';\r\n"+
            "        this.contentDiv.style.right='0px';\r\n"+
            "        this.contentDiv.style.bottom='0px';\r\n"+
            "        this.contentDiv.style.top=headerSize;\r\n"+
            "        this.headerDiv.style.left='0px';\r\n"+
            "        this.headerDiv.style.right='0px';\r\n"+
            "        this.headerDiv.style.top='0px';\r\n"+
            "        this.headerDiv.style.height=headerSize;\r\n"+
            "	  }else{\r\n"+
            "        this.contentDiv.style.left=borderSize;\r\n"+
            "        this.contentDiv.style.right=borderSize;\r\n"+
            "        this.contentDiv.style.bottom=borderSize;\r\n"+
            "        this.contentDiv.style.top=bothSize;\r\n"+
            "        \r\n"+
            "        this.headerDiv.style.left=borderSize;\r\n"+
            "        this.headerDiv.style.right=borderSize;\r\n"+
            "        this.headerDiv.style.top=borderSize;\r\n"+
            "        this.headerDiv.style.height=headerSize;\r\n"+
            "        \r\n"+
            "        this.left.style.top=bothSize;\r\n"+
            "        this.left.style.bottom=borderSize;\r\n"+
            "        this.left.style.width=borderSize;\r\n"+
            "        \r\n"+
            "        this.right.style.top=bothSize;\r\n"+
            "        this.right.style.bottom=borderSize;\r\n"+
            "        this.right.style.width=borderSize;\r\n"+
            "        \r\n"+
            "        this.top.style.height=borderSize;\r\n"+
            "        this.top.style.left=borderSize;\r\n"+
            "        this.top.style.right=borderSize;\r\n"+
            "        \r\n"+
            "        this.bottom.style.left=borderSize;\r\n"+
            "        this.bottom.style.right=borderSize;\r\n"+
            "        this.bottom.style.height=borderSize;\r\n"+
            "        \r\n"+
            "        this.topLeft.style.width=borderSize;\r\n"+
            "        this.topLeft.style.height=bothSize;\r\n"+
            "        \r\n"+
            "        this.topRight.style.width=borderSize;\r\n"+
            "        this.topRight.style.height=bothSize;\r\n"+
            "        \r\n"+
            "        this.bottomLeft.style.width=borderSize;\r\n"+
            "        this.bottomLeft.style.height=borderSize;\r\n"+
            "        \r\n"+
            "        this.bottomRight.style.width=borderSize;\r\n"+
            "        this.bottomRight.style.height=borderSize;\r\n"+
            "        this.onOptionsChanged();\r\n"+
            "	  }\r\n"+
            "  }\r\n"+
            "    this.title.style.display=top!=0 ? 'block' : 'none';\r\n"+
            "    var location2;\r\n"+
            "	if(isMax)\r\n"+
            "      location2=new Rect(left,top-this.headerSize,width,height+this.headerSize);\r\n"+
            "	else\r\n"+
            "      location2=new Rect(left-this.borderSize,top-(this.headerSize+this.borderSize),width+this.borderSize*2,height+(this.b");
          out.print(
            "orderSize*2+this.headerSize));\r\n"+
            "    location2.writeToElement(this.windowDiv);\r\n"+
            "  if(active!=null && this.active!=active){\r\n"+
            "	  this.active=active;\r\n"+
            "      if(this.active)\r\n"+
            "        this.windowDiv.removeChild(this.disabledGlassDiv);\r\n"+
            "      else\r\n"+
            "        this.windowDiv.appendChild(this.disabledGlassDiv);\r\n"+
            "  }\r\n"+
            "  if(this.onWindowLocation)\r\n"+
            "	  this.onWindowLocation(left,top,width,height,isMax,zindex,active,fromBackend);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function DesktopWindow(desktop,childId,divElement,title){\r\n"+
            "  var that=this;\r\n"+
            "  this.desktop=desktop;\r\n"+
            "  this.options={};\r\n"+
            "  this.buttonStyles={};\r\n"+
            "  this.hasHeader=true;\r\n"+
            "  this.buttons=[];\r\n"+
            "  this.fontSize=14;\r\n"+
            "  \r\n"+
            "  \r\n"+
            "  this.isMax=null;\r\n"+
            "  this.active=false;\r\n"+
            "  this.portletId=childId;\r\n"+
            "  \r\n"+
            "  this.windowDiv=nw('div');\r\n"+
            "  this.contentDiv=nw('div');\r\n"+
            "  this.headerDiv=nw('div');\r\n"+
            "  this.headerInnerDiv=nw('div');\r\n"+
            "  this.title=nw('div');\r\n"+
            "  this.top=nw('div');\r\n"+
            "  this.bottom=nw('div');\r\n"+
            "  this.left=nw('div');\r\n"+
            "  this.right=nw('div');\r\n"+
            "  this.bottomRight=nw('div');\r\n"+
            "  this.bottomLeft=nw('div');\r\n"+
            "  this.topRight=nw('div');\r\n"+
            "  this.topLeft=nw('div');\r\n"+
            "  \r\n"+
            "  this.contentDiv.style.overflow=\"hidden\";\r\n"+
            "  this.headerDiv.style.position='absolute';\r\n"+
            "  this.headerInnerDiv.style.cursor='move';\r\n"+
            "  this.headerDiv.portletSelectType='nav';\r\n"+
            "  this.headerInnerDiv.style.width=\"100%\";\r\n"+
            "  this.headerInnerDiv.style.height=\"100%\";\r\n"+
            "  \r\n"+
            "  this.top.style.position='absolute';\r\n"+
            "  this.top.style.top='0px';\r\n"+
            "  this.top.style.cursor='n-resize';\r\n"+
            "  \r\n"+
            "  this.bottom.style.position='absolute';\r\n"+
            "  this.bottom.style.bottom='0px';\r\n"+
            "  this.bottom.style.cursor='n-resize';\r\n"+
            "  \r\n"+
            "  this.left.style.position='absolute';\r\n"+
            "  this.left.style.left='0px';\r\n"+
            "  this.left.style.cursor='w-resize';\r\n"+
            "  \r\n"+
            "  this.right.style.position='absolute';\r\n"+
            "  this.right.style.right='0px';\r\n"+
            "  this.right.style.cursor='w-resize';\r\n"+
            "  \r\n"+
            "  this.bottomRight.style.position='absolute';\r\n"+
            "  this.bottomRight.style.right='0px';\r\n"+
            "  this.bottomRight.style.bottom='0px';\r\n"+
            "  this.bottomRight.style.cursor='nw-resize';\r\n"+
            "  \r\n"+
            "  this.bottomLeft.style.position='absolute';\r\n"+
            "  this.bottomLeft.style.left='0px';\r\n"+
            "  this.bottomLeft.style.bottom='0px';\r\n"+
            "  this.bottomLeft.style.cursor='ne-resize';\r\n"+
            "  \r\n"+
            "  this.topRight.style.position='absolute';\r\n"+
            "  this.topRight.style.right='0px';\r\n"+
            "  this.topRight.style.top='0px';\r\n"+
            "  this.topRight.style.cursor='ne-resize';\r\n"+
            "  \r\n"+
            "  this.topLeft.style.position='absolute';\r\n"+
            "  this.topLeft.style.left='0px';\r\n"+
            "  this.topLeft.style.top='0px';\r\n"+
            "  this.topLeft.style.cursor='nw-resize';\r\n"+
            "  \r\n"+
            "  this.title.innerHTML=title;\r\n"+
            "  \r\n"+
            "  this.headerDiv.ondraggingEnd=function(e,diffx,diffy){that.onHeaderDragging(e,diffx,diffy,true);};\r\n"+
            "  this.headerDiv.ondragging=function(e,diffx,diffy){that.onHeaderDragging(e,diffx,diffy,false);};\r\n"+
            "  this.headerDiv.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  this.headerDiv.onclick=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  this.headerInnerDiv.ondraggingEnd=function(e,diffx,diffy){that.onHeaderDragging(e,diffx,diffy,true);};\r\n"+
            "  this.headerInnerDiv.ondragging=function(e,diffx,diffy){that.onHeaderDragging(e,diffx,diffy,false);};\r\n"+
            "  this.headerInnerDiv.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  this.headerInnerDiv.onclick=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  this.title.onEdit=function(old,nuw){that.onUserRename(nuw);};\r\n"+
            "  \r\n"+
            "  this.top.ondraggingEnd=function(e,diffx,diffy){that.onTopDragging(e,diffx,diffy,true);};\r\n"+
            "  this.top.ondragging=function(e,diffx,diffy){that.onTopDragging(e,diffx,diffy,false);};\r\n"+
            "  this.top.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  this.bottom.ondraggingEnd=function(e,diffx,diffy){that.onBottomDragging(e,diffx,diffy,true);};\r\n"+
            "  this.bottom.ondragging=function(e,diffx,diffy){that.onBottomDragging(e,diffx,diffy,false);};\r\n"+
            "  this.bottom.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  this.left.ondraggingEnd=function(e,diffx,diffy){that.onLeftDragging(e,diffx,diffy,true);};\r\n"+
            "  this.left.ondragging=function(e,diffx,diffy){that.onLeftDragging(e,diffx,diffy,false);};\r\n"+
            "  this.left.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  this.right.ondraggingEnd=function(e,diffx,diffy){that.onRightDragging(e,diffx,diffy,true);};\r\n"+
            "  this.right.ondragging=function(e,diffx,diffy){that.onRightDragging(e,diffx,diffy,false);};\r\n"+
            "  this.right.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  this.bottomRight.ondraggingEnd=function(e,diffx,diffy){that.onBottomRightDragging(e,diffx,diffy,true);};\r\n"+
            "  this.bottomRight.ondragging=function(e,diffx,diffy){that.onBottomRightDragging(e,diffx,diffy,false);};\r\n"+
            "  this.bottomRight.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  this.bottomLeft.ondraggingEnd=function(e,diffx,diffy){that.onBottomLeftDragging(e,diffx,diffy,true);};\r\n"+
            "  this.bottomLeft.ondragging=function(e,diffx,diffy){that.onBottomLeftDragging(e,diffx,diffy,false);};\r\n"+
            "  this.bottomLeft.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  this.topRight.ondraggingEnd=function(e,diffx,diffy){that.onTopRightDragging(e,diffx,diffy,true);};\r\n"+
            "  this.topRight.ondragging=function(e,diffx,diffy){that.onTopRightDragging(e,diffx,diffy,false);};\r\n"+
            "  this.topRight.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  \r\n"+
            "  this.topLeft.ondraggingEnd=function(e,diffx,diffy){that.onTopLeftDragging(e,diffx,diffy,true);};\r\n"+
            "  this.topLeft.ondragging=function(e,diffx,diffy){that.onTopLeftDragging(e,diffx,diffy,false);};\r\n"+
            "  this.topLeft.ondraggingStart=function(e){that.onUserFocus();};\r\n"+
            "  \r\n"+
            "  makeDraggable(this.headerDiv,null,true,true);\r\n"+
            "  makeDraggable(this.headerInnerDiv,null,true,true);\r\n"+
            "  makeDraggable(this.top,null,true,true);\r\n"+
            "  makeDraggable(this.bottom,null,true,false);\r\n"+
            "  makeDraggable(this.left,null,false,true);\r\n"+
            "  makeDraggable(this.right,null,false,true);\r\n"+
            "  makeDraggable(this.bottomRight,null,false,false);\r\n"+
            "  makeDraggable(this.bottomLeft,null,false,false);\r\n"+
            "  makeDraggable(this.topRight,null,false,false);\r\n"+
            "  makeDraggable(this.topLeft,null,false,false);\r\n"+
            "  \r\n"+
            "  makeEditable(this.title);\r\n"+
            "  // combines with transform to make title appear vertically centered\r\n"+
            "  this.title.style.bottom=\"50%\";\r\n"+
            "  this.title.style.marginBottom=\"1px\";\r\n"+
            "  this.title.style.transform=\"translateY(50%)\";\r\n"+
            "  this.headerInnerDiv.appendChild(this.title);\r\n"+
            "  this.headerDiv.appendChild(this.headerInnerDiv);\r\n"+
            "  this.windowDiv.appendChild(this.top);\r\n"+
            "  this.windowDiv.appendChild(this.headerDiv);\r\n"+
            "  this.windowDiv.appendChild(this.bottom);\r\n"+
            "  this.windowDiv.appendChild(this.left);\r\n"+
            "  this.windowDiv.appendChild(this.right);\r\n"+
            "  this.windowDiv.appendChild(this.bottomRight);\r\n"+
            "  this.windowDiv.appendChild(this.bottomLeft);\r\n"+
            "  this.windowDiv.appendChild(this.topRight);\r\n"+
            "  this.windowDiv.appendChild(this.topLeft);\r\n"+
            "  //end\r\n"+
            "  \r\n"+
            "  this.hasPopButton=false;\r\n"+
            "  this.popButton=nw('div');\r\n"+
            "  this.popButton.style.position='absolute';\r\n"+
            "  this.popButton.onlick=function(e){that.onPop(e);};\r\n"+
            "  this.popButton.style.right='64px';\r\n"+
            "  \r\n"+
            "  this.addButton('close');\r\n"+
            "  this.addButton('max');\r\n"+
            "  this.addButton('min');\r\n"+
            "  this.addButton('pop');\r\n"+
            "  \r\n"+
            "  \r\n"+
            "  //this.headerDiv.ondblclick=function(e){if(getMouseTarget(e)==that.headerDiv) that.onButton('max');};\r\n"+
            "  this.headerInnerDiv.ondblclick=function(e){if(getMouseTarget(e)==that.headerInnerDiv) that.onButton('max');};\r\n"+
            "  this.title.onmousedown=function(e){that.onUserFocus();};\r\n"+
            "  this.contentDiv.appendChild(divElement);\r\n"+
            "  this.windowDiv.appendChild(this.contentDiv);\r\n"+
            " \r\n"+
            "  \r\n"+
            "  \r\n"+
            "  this.disabledGlassDiv=nw('div');\r\n"+
            "  this.disabledGlassDiv.style.background='black';\r\n"+
            "  this.disabledGlassDiv.style.opacity=0;\r\n"+
            "  this.disabledGlassDiv.style.zIndex=20;\r\n"+
            "  this.disabledGlassDiv.style.left='0px';\r\n"+
            "  this.disabledGlassDiv.style.top='20px';\r\n"+
            "  this.disabledGlassDiv.style.bottom='0px';\r\n"+
            "  this.disabledGlassDiv.style.right='0px';\r\n"+
            "  this.disabledGlassDiv.onmousedown=function(e){that.onUserFocus();};\r\n"+
            "  this.disabledGlassDiv.portletPeer=divElement;\r\n"+
            "  this.windowDiv.appendChild(this.disabledGlassDiv);\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.addButton=function(name){\r\n"+
            "  var that=this;\r\n"+
            "  var nm=name;\r\n"+
            "  var button=nw('div');\r\n"+
            "  button.name=name;\r\n"+
            "  button.isVisible=false;\r\n"+
            "  button.position='absolute';\r\n"+
            "  button.onclick=function(e){that.onButton(button.name);};\r\n"+
            "  button.style.right='64px';\r\n"+
            "  this.buttons[this.buttons.length]=button;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onButton=function(name){\r\n"+
            "  if(this.onUserButton)\r\n"+
            "	  this.onUserButton(this.portletId,name);\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.setStyleClassPrefix=function(prefix){\r\n"+
            "//  this.title.className=prefix+'_title';\r\n"+
            "  \r\n"+
            "//  this.maxButton.className=prefix+'_max_button';\r\n"+
            "//  this.closeButton.className=prefix+'_close_button';\r\n"+
            "//  this.minButton.className=prefix+'_min_button';\r\n"+
            "  \r\n"+
            "//  for(var i in this.buttons){\r\n"+
            "//	  var btn=this.buttons[i];\r\n"+
            "//	  btn.className=prefix+'_'+btn.name+'_button';\r\n"+
            "//	  var style=this.buttonStyles[btn.name];\r\n"+
            "//  }\r\n"+
            "  \r\n"+
            "//  this.topLeft.className=prefix+'_top_left';\r\n"+
            "//  \r\n"+
            "//  this.left.className=prefix+'_left';\r\n"+
            "//  this.bottomRight.className=prefix+'_bottom_right';\r\n"+
            "//  this.bottomLeft.className=prefix+'_bottom_left';\r\n"+
            "//  this.topRight.className=prefix+'_top_right';\r\n"+
            "//  this.contentDiv.className=prefix+'_content';\r\n"+
            "//  this.top.className=prefix+'_top';\r\n"+
            "//  this.headerDiv.className=prefix+'_header';\r\n"+
            "//  this.bottom.className=prefix+'_bottom';\r\n"+
            "//  this.right.className=prefix+'_right';\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onOptionsChanged=function(){\r\n"+
            "  var desktopOptions=this.desktop==null ? {} : this.desktop.options;\r\n"+
            "  var windowColor=this.options.windowColor || desktopOptions.windowColor || '#007608';\r\n"+
            "  var windowColorUp=this.options.windowColorUp || desktopOptions.windowColorUp || '#FFFFFF';\r\n"+
            "  var windowColorDown=this.options.windowColorDown || desktopOptions.windowColorDown || '#FFFFFF';\r\n"+
            "  var windowColorText=this.options.windowColorText || desktopOptions.windowColorText || '#FFFFFF';\r\n"+
            "  var windowColorButton=this.options.windowColorButton || desktopOptions.windowColorButton || '#E2E2E2';\r\n"+
            "  var windowColorButtonUp=this.options.windowColorButtonUp || desktopOptions.windowColorButtonUp || '#E2E2E2';\r\n"+
            "  var windowColorButtonDown=this.options.win");
          out.print(
            "dowColorButtonDown || desktopOptions.windowColorButtonDown || '#E2E2E2';\r\n"+
            "  var windowColorButtonIcon=this.options.windowColorButtonIcon || desktopOptions.windowColorButtonIcon || '#000000';\r\n"+
            "  var windowBorderInnerSize=(this.options.windowBorderInnerSize || desktopOptions.windowBorderInnerSize || '0');\r\n"+
            "  var windowBorderOuterSize=(this.options.windowBorderOuterSize || desktopOptions.windowBorderOuterSize || '1');\r\n"+
            "  var buttonWidth=this.options.windowButtonWidth || desktopOptions.windowButtonWidth || 32\r\n"+
            "  var buttonHeight=this.options.windowButtonHeight || desktopOptions.windowButtonHeight || 13;\r\n"+
            "  var iPx=toPx(windowBorderInnerSize);\r\n"+
            "  var oPx=toPx(windowBorderOuterSize);\r\n"+
            "  \r\n"+
            "  var windowFontStyle=this.options.windowFontStyle || desktopOptions.windowFontStyle || '';\r\n"+
            "  var borderColor=windowColorUp+' '+windowColorDown+' '+windowColorDown+' '+windowColorUp;\r\n"+
            " \r\n"+
            "  this.title.style.color=windowColorText;\r\n"+
            "  applyStyle(this.title,windowFontStyle);\r\n"+
            "  \r\n"+
            "  this.top.style.background=windowColor;\r\n"+
            "  this.top.style.borderWidth=oPx+' 0px 0px 0px';\r\n"+
            "  this.top.style.borderColor=windowColorUp;\r\n"+
            "  this.top.style.borderStyle='solid';\r\n"+
            "  \r\n"+
            "  this.bottom.style.background=windowColor;\r\n"+
            "  this.bottom.style.borderWidth= iPx+' 0px '+oPx+' 0px';\r\n"+
            "  this.bottom.style.borderColor=borderColor;\r\n"+
            "  this.bottom.style.borderStyle='solid';\r\n"+
            "  \r\n"+
            "  this.left.style.background=windowColor;\r\n"+
            "  this.left.style.borderWidth='0px '+iPx+' 0px '+oPx;\r\n"+
            "  this.left.style.borderColor=borderColor;\r\n"+
            "  this.left.style.borderStyle='solid';\r\n"+
            "  \r\n"+
            "  this.right.style.background=windowColor;\r\n"+
            "  this.right.style.borderWidth='0px '+oPx+' 0px '+iPx;\r\n"+
            "  this.right.style.borderColor=borderColor;\r\n"+
            "  this.right.style.borderStyle='solid';\r\n"+
            "  \r\n"+
            "  if(this.location!=null && this.location.top>0){\r\n"+
            "    this.headerDiv.style.background=windowColor;\r\n"+
            "    this.headerDiv.style.borderWidth='0px 0px '+iPx+' 0px';\r\n"+
            "    this.headerDiv.style.borderColor=borderColor;\r\n"+
            "    this.headerDiv.style.borderStyle='solid';\r\n"+
            "  }else{\r\n"+
            "    this.headerDiv.style.background=null;\r\n"+
            "    this.headerDiv.style.borderWidth=null;\r\n"+
            "    this.headerDiv.style.borderColor=null;\r\n"+
            "    this.headerDiv.style.borderStyle=null;\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "//  this.title.style.borderWidth='0px 0px '+iPx+' 0px';\r\n"+
            "//  this.title.style.borderColor=borderColor;\r\n"+
            "//  this.title.style.borderStyle='solid';\r\n"+
            "  \r\n"+
            "  this.topLeft.style.background=windowColor;\r\n"+
            "  this.topLeft.style.borderWidth=oPx+' 0px 0px '+oPx;\r\n"+
            "  this.topLeft.style.borderColor=borderColor;\r\n"+
            "  this.topLeft.style.borderStyle='solid';\r\n"+
            "  \r\n"+
            "  this.bottomRight.style.background=windowColor;\r\n"+
            "  this.bottomRight.style.borderWidth='0px '+oPx+' '+oPx+' 0px';\r\n"+
            "  this.bottomRight.style.borderColor=borderColor;\r\n"+
            "  this.bottomRight.style.borderStyle='solid';\r\n"+
            "  \r\n"+
            "  this.bottomLeft.style.background=windowColor;\r\n"+
            "  this.bottomLeft.style.borderWidth='0px 0px '+oPx+' '+oPx;\r\n"+
            "  this.bottomLeft.style.borderColor=borderColor;\r\n"+
            "  this.bottomLeft.style.borderStyle='solid';\r\n"+
            "  \r\n"+
            "  this.topRight.style.background=windowColor;\r\n"+
            "  this.topRight.style.borderWidth=oPx+' '+oPx+' 0px 0px';\r\n"+
            "  this.topRight.style.borderColor=borderColor;\r\n"+
            "  this.topRight.style.borderStyle='solid';\r\n"+
            "  \r\n"+
            "  var buttonBorder=windowColorButtonUp+' '+windowColorButtonDown+' '+windowColorButtonDown+' '+windowColorButtonUp;\r\n"+
            "  \r\n"+
            "  var btnColor=\"#000000\";\r\n"+
            "  var pos=0; \r\n"+
            "  svgStyle='fill=\"none\" stroke=\"'+windowColorButtonIcon+'\"';\r\n"+
            "  svgStyle=svgStyle.replace(/#/g, \"%23\");\r\n"+
            "  for(var i in this.buttons){\r\n"+
            "	  var btn=this.buttons[i];\r\n"+
            "		var isVisible=this.buttonsVisible==null || this.buttonsVisible[btn.name];\r\n"+
            "		if(!isVisible){\r\n"+
            "		  if(btn.isVisible)\r\n"+
            "		    this.headerInnerDiv.removeChild(btn);\r\n"+
            "		}else{\r\n"+
            "		  if(!btn.isVisible)\r\n"+
            "		    this.headerInnerDiv.appendChild(btn);\r\n"+
            "          btn.style.backgroundColor=windowColorButton;\r\n"+
            "          btn.style.borderWidth='1px';\r\n"+
            "          btn.style.borderColor=buttonBorder;\r\n"+
            "          btn.style.borderStyle='solid';\r\n"+
            "	      btn.className='portal_desktop_button';\r\n"+
            "	      btn.style.backgroundImage=(DESKTOP_BUTTON_IMAGE_PREFIX+svgStyle+DESKTOP_BUTTON_IMAGE[btn.name == 'max' && this.isMax ? 'maxAlt' : btn.name]);\r\n"+
            "	      var style=this.buttonStyles[btn.name];\r\n"+
            "	      if(this.isMax)\r\n"+
            "            btn.style.top=toPx(fl((this.headerSize-buttonHeight)/2));\r\n"+
            "	      else\r\n"+
            "            btn.style.top=toPx(max(fl((this.headerSize+this.borderSize-buttonHeight)/2-this.borderSize),0));\r\n"+
            "          btn.style.right=toPx(pos*buttonWidth);\r\n"+
            "          btn.style.height=toPx(buttonHeight);\r\n"+
            "          btn.style.width=toPx(buttonWidth);\r\n"+
            "	      if(style!=null)\r\n"+
            "		      applyStyle(btn,style);\r\n"+
            "	      pos++;\r\n"+
            "		}\r\n"+
            "		btn.isVisible=isVisible;\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  this.top.style.background=windowColor;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.getHeaderDiv=function(){\r\n"+
            "	return this.headerDiv;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onTopDragging=function(element,x,y,done){\r\n"+
            "  if(this.isMax)return;\r\n"+
            "  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top+y,this.predragLocation.width,this.predragLocation.height-y,false,null,this.active,false );\r\n"+
            "  if(done)\r\n"+
            "    this.onLocationChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onHeaderDragging=function(element,x,y,done){\r\n"+
            "  if(this.isMax)return;\r\n"+
            "  this.setWindowLocation(this.predragLocation.left+x,this.predragLocation.top+y,this.predragLocation.width,this.predragLocation.height,false,null,this.active,false );\r\n"+
            "  if(done)\r\n"+
            "    this.onLocationChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onBottomDragging=function(element,x,y,done){\r\n"+
            "  if(this.isMax)return;\r\n"+
            "  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top,this.predragLocation.width,this.predragLocation.height+y,false,null,this.active,false );\r\n"+
            "  if(done)\r\n"+
            "    this.onLocationChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onRightDragging=function(element,x,y,done){\r\n"+
            "  if(this.isMax)return;\r\n"+
            "  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top,this.predragLocation.width+x,this.predragLocation.height,false,null,this.active,false );\r\n"+
            "  if(done)\r\n"+
            "    this.onLocationChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onLeftDragging=function(element,x,y,done){\r\n"+
            "  if(this.isMax)return;\r\n"+
            "  this.setWindowLocation(this.predragLocation.left+x,this.predragLocation.top,this.predragLocation.width-x,this.predragLocation.height,false,null,this.active,false );\r\n"+
            "  if(done)\r\n"+
            "    this.onLocationChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onBottomLeftDragging=function(element,x,y,done){\r\n"+
            "  if(this.isMax)return;\r\n"+
            "  this.setWindowLocation(this.predragLocation.left+x,this.predragLocation.top,this.predragLocation.width-x,this.predragLocation.height+y,false,null,this.active,false );\r\n"+
            "  if(done)\r\n"+
            "    this.onLocationChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onTopLeftDragging=function(element,x,y,done){\r\n"+
            "  if(this.isMax)return;\r\n"+
            "  this.setWindowLocation(this.predragLocation.left+x,this.predragLocation.top+y,this.predragLocation.width-x,this.predragLocation.height-y,false,null,this.active,false );\r\n"+
            "  if(done)\r\n"+
            "    this.onLocationChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onTopRightDragging=function(element,x,y,done){\r\n"+
            "  if(this.isMax)return;\r\n"+
            "  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top+y,this.predragLocation.width+x,this.predragLocation.height-y,false,null,this.active,false );\r\n"+
            "  if(done)\r\n"+
            "    this.onLocationChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onBottomRightDragging=function(element,x,y,done){\r\n"+
            "  if(this.isMax)return;\r\n"+
            "  this.setWindowLocation(this.predragLocation.left,this.predragLocation.top,this.predragLocation.width+x,this.predragLocation.height+y,false ,null,this.active,false);\r\n"+
            "  if(done)\r\n"+
            "    this.onLocationChanged();\r\n"+
            "}\r\n"+
            "//DesktopWindow.prototype.onClose=function(){\r\n"+
            "//  if(this.onUserClosedWindow)\r\n"+
            "//	  this.onUserClosedWindow(this.portletId);\r\n"+
            "//}\r\n"+
            "//\r\n"+
            "//DesktopWindow.prototype.onMax=function(){\r\n"+
            "//  if(this.onUserMaximizedWindow)\r\n"+
            "//	  this.onUserMaximizedWindow(this.portletId);\r\n"+
            "//}\r\n"+
            "//\r\n"+
            "//DesktopWindow.prototype.onMin=function(){\r\n"+
            "//  if(this.onUserMinimizedWindow)\r\n"+
            "//	  this.onUserMinimizedWindow(this.portletId);\r\n"+
            "//}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onLocationChanged=function(){\r\n"+
            "	  this.onUserMovedWindow(this.portletId,this.location.left,this.location.top,this.location.width,this.location.height);\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onUserFocus=function(){\r\n"+
            "	if(this.onUserFocusedWindow)\r\n"+
            "	  this.onUserFocusedWindow(this.portletId);\r\n"+
            "	this.predragLocation=this.location.clone();\r\n"+
            "}\r\n"+
            "\r\n"+
            "DesktopWindow.prototype.onUserRename=function(text){\r\n"+
            "  if(this.onUserRenamedWindow)\r\n"+
            "	  this.onUserRenamedWindow(this.portletId,text);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function MenuBar(divElement){\r\n"+
            "  this.divElement=divElement;\r\n"+
            "  this.window=getWindow(divElement);\r\n"+
            "  this.innerDivElement=nw('div');\r\n"+
            "  this.divElement.className='portal_menubar_background';\r\n"+
            "  this.menuStyle='portal_menubar_menu';\r\n"+
            "  this.divElement.style.left='0px';\r\n"+
            "  this.divElement.style.top='0px';\r\n"+
            "  this.divElement.style.right='0px';\r\n"+
            "  this.divElement.style.height='25px';\r\n"+
            "  this.divElement.style.overflowX='hidden';\r\n"+
            "  this.divElement.style.overflowY='hidden';\r\n"+
            "  this.divElement.style.whiteSpace='nowrap';\r\n"+
            "  this.divElement.appendChild(this.innerDivElement);\r\n"+
            "  this.cssStyle = \"\";\r\n"+
            "}\r\n"+
            "  \r\n"+
            "MenuBar.prototype.setOptions=function(options){\r\n"+
            "    this.align=options.align;\r\n"+
            "	this.goUp=options.goUp;\r\n"+
            "}\r\n"+
            "\r\n"+
            "MenuBar.prototype.showMenu=function(id,menuConfig){\r\n"+
            "	if(this.currentMenu!=null){\r\n"+
            "		this.currentMenu.hide();\r\n"+
            "		this.currentMenu=null;\r\n"+
            "	}\r\n"+
            "	if(this.currentMenuDiv!=null){\r\n"+
            "		this.currentMenuDiv.className=this.menuStyle;\r\n"+
            "    }\r\n"+
            "	for(var i in this.menuDivs){\r\n"+
            "		if(this.menuDivs[i].menu.id==id){\r\n"+
            "		  this.currentMenuDiv=this.menuDivs[i];\r\n"+
            "		  break;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "  if(this.currentMenuDiv==null || this.currentMenuDiv.menu.id!=id)\r\n"+
            "	  return;\r\n"+
            "  var rect=new Rect();\r\n"+
            "  rect.readFromElement(this.currentMenuDiv);\r\n"+
            "  var point=new Point(rect.getLeft(),this.goUp=='true' ? rect.getTop()+1 : rect.getBottom());\r\n"+
            "  var menu=this.createMenu(menuConfig);\r\n"+
            "  if (menu.isEmpty()) \r\n"+
            "  	return;\r\n"+
            "  menu.show(point,null,null,null,this.goUp=='true');\r\n"+
            "  var that=this;\r\n"+
            "  menu.onHide=function(){that.onMenuHidden()};\r\n"+
            " ");
          out.print(
            " rect.readFromElement(this.innerDivElement);\r\n"+
            "  if(this.innerDivElement.parentNode!=null)\r\n"+
            "      this.innerDivElement.parentNode.removeChild(this.innerDivElement);\r\n"+
            "  this.innerDivElement.oldWidth=this.innerDivElement.style.width;\r\n"+
            "  this.innerDivElement.oldLeft=this.innerDivElement.style.left;\r\n"+
            "  this.innerDivElement.oldTop=this.innerDivElement.style.top;\r\n"+
            "  if(this.goUp=='true')\r\n"+
            "    this.innerDivElement.style.top=(rect.getTop())+'px';\r\n"+
            "  else \r\n"+
            "  this.innerDivElement.style.top=(rect.getTop()-1)+'px';\r\n"+
            "  //TODO: pay attention to this.align:\r\n"+
            "  this.innerDivElement.style.left=(rect.getLeft()-1)+'px';\r\n"+
            "  for(var i in this.menuDivs){\r\n"+
            "	  var md=this.menuDivs[i];\r\n"+
            "      md.onmouseover=function(e){that.onUserMenu(e,arguments.callee.div);};\r\n"+
            "	  md.onmouseover.div=md;\r\n"+
            "  }\r\n"+
            "  menu.glass.appendChild(this.innerDivElement);\r\n"+
            "  menu.glass.style.overflowX='hidden';\r\n"+
            "  menu.glass.style.overflowY='hidden';\r\n"+
            "  menu.glass.style.whiteSpace='nowrap';\r\n"+
            "  this.currentMenu=menu;\r\n"+
            "}\r\n"+
            "\r\n"+
            "MenuBar.prototype.onMenuHidden=function(){\r\n"+
            "  this.currentMenuDiv.className=this.menuStyle;\r\n"+
            "  this.currentMenu.glass.removeChild(this.innerDivElement);\r\n"+
            "  this.innerDivElement.style.top=this.innerDivElement.oldTop;\r\n"+
            "  this.innerDivElement.style.left=this.innerDivElement.oldLeft;\r\n"+
            "  this.innerDivElement.style.width=this.innerDivElement.oldWidth;\r\n"+
            "  this.divElement.appendChild(this.innerDivElement);\r\n"+
            "  this.currentMenu=null;\r\n"+
            "  this.currentMenuDiv=null;\r\n"+
            "  for(var i in this.menuDivs){\r\n"+
            "	  var md=this.menuDivs[i];\r\n"+
            "      md.onmouseover=null;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "MenuBar.prototype.createMenu=function(menu){\r\n"+
            "   var that=this;\r\n"+
            "   var r=new Menu(this.window);\r\n"+
            "   if(menu==null)\r\n"+
            "     return r;\r\n"+
            "   r.createMenu(menu, function(e,id){that.onUserMenuItem(e,id);});\r\n"+
            "   return r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "MenuBar.prototype.onUserMenuItem=function(e,id){\r\n"+
            "	if(this.onUserClickedMenuItem)\r\n"+
            "	  this.onUserClickedMenuItem(id);\r\n"+
            "}\r\n"+
            "MenuBar.prototype.onUserMenu=function(e,div){\r\n"+
            "  if(this.currentMenuDiv==div)\r\n"+
            "	  return;\r\n"+
            "	if(this.onUserOverMenu){\r\n"+
            "//	  this.currentMenuDiv=div;\r\n"+
            "//	  this.currentMenuDiv.className=this.menuStyle+ ' '+this.menuStyle+'_hover';\r\n"+
            "	  this.onUserOverMenu(div.menu.id);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "MenuBar.prototype.setMenus=function(menus){\r\n"+
            "  this.menuDivs=[];\r\n"+
            "  var that=this;\r\n"+
            "  removeAllChildren(this.innerDivElement);\r\n"+
            "  for(var i in menus){\r\n"+
            "	  var menu=nw('div',this.menuStyle);\r\n"+
            "	  menu.style.display='inline-block';\r\n"+
            "	  menu.style.position='static';\r\n"+
            "	  menu.innerHTML=menus[i].text;\r\n"+
            "	  menu.menu=menus[i];\r\n"+
            "	  menu.onmousedown=function(e){that.onUserMenu(e,arguments.callee.div);};\r\n"+
            "	  menu.onmousedown.div=menu;\r\n"+
            "	  this.menuDivs[this.menuDivs.length]=menu;\r\n"+
            "	  this.innerDivElement.appendChild(menu);\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "MenuBar.prototype.setCssStyle=function(cssStyle){\r\n"+
            "	if(this.cssStyle == cssStyle)\r\n"+
            "	   return;\r\n"+
            "	this.cssStyle = cssStyle;\r\n"+
            "    delete this.divElement.style;\r\n"+
            "    delete this.divElement.class;\r\n"+
            "    this.divElement.style.left='0px';\r\n"+
            "    this.divElement.style.top='0px';\r\n"+
            "    this.divElement.style.right='0px';\r\n"+
            "    this.divElement.style.height='25px';\r\n"+
            "    this.divElement.style.overflowX='hidden';\r\n"+
            "    this.divElement.style.overflowY='hidden';\r\n"+
            "    this.divElement.style.whiteSpace='nowrap';\r\n"+
            "	applyStyle(this.divElement,this.cssStyle);\r\n"+
            "    this.innerDivElement.style.color = this.divElement.style.color;\r\n"+
            "}\r\n"+
            "");

	}
	
}