package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ami_scripteditor_js_1 extends AbstractHttpHandler{

	public amiweb_ami_scripteditor_js_1() {
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
            "AmiCodeField.prototype=new FormField();\r\n"+
            "function AmiCodeField(portlet,id,title){\r\n"+
            "	FormField.call(this,portlet,id,title);\r\n"+
            "	var win=portletManager.getWindow(portlet.portlet.owningWindowId);\r\n"+
            "	this.element=(win||window).document.createElement('pre');\r\n"+
            "	this.element.setAttribute(\"id\", \"ace-editor-id-\" + id);\r\n"+
            "    this.element.style.border='1px solid #AAAAAA';\r\n"+
            "    this.element.style.textAlign='left';\r\n"+
            "    this.element.style.margin='0px';\r\n"+
            "	this.oldValue = \"\";\r\n"+
            "	this.cursorPosition = 0;\r\n"+
            "	this.scrollLineNum = null;\r\n"+
            "    \r\n"+
            "    this.editor = ace.edit(this.element);\r\n"+
            "    this.setMode(\"amiscript\");\r\n"+
            "    this.editor.session.setUseWorker(false);\r\n"+
            "    this.editor.getSession().setUseWrapMode(true);\r\n"+
            "    this.editor.getSession().setUseSoftTabs(true);\r\n"+
            "    this.editor.getSession().setTabSize(2);\r\n"+
            "    this.editor.setShowPrintMargin(false);\r\n"+
            "    this.editor.setHighlightActiveLine(false);\r\n"+
            "    this.editor.$blockScrolling=Infinity;\r\n"+
            "    this.needsUpdate = true;\r\n"+
            "	this.range = ace.require(\"ace/range\").Range;\r\n"+
            "	this.curMarkerId = null;\r\n"+
            "	this.flashMarkers = []; // this will work as a queue\r\n"+
            "    var that = this;\r\n"+
            "    //this.element.onchange=function(){that.portlet.onChange(that.id,{value:that.getValue()});};\r\n"+
            "    //this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.element));};\r\n"+
            "    //this.element.onkeydown=function(e){that.onKeyDown(e);};\r\n"+
            "    this.element.onblur=function(){that.onChangeDiff();};\r\n"+
            "    this.editor.getSession().on('change',function(){that.onChangeDiff();});\r\n"+
            "	this.editor.commands.on('afterExec', function(e) { that.onAfterExec(e); });\r\n"+
            "	this.editor.addEventListener(\"click\", function(event) {\r\n"+
            "		that.handleClick(event.getDocumentPosition().row, event.domEvent.ctrlKey, event.domEvent.shiftKey, event.domEvent.altKey);\r\n"+
            "		that.onUpdateCursor(event);\r\n"+
            "	});\r\n"+
            "    // Two methods of handling keystrokes\r\n"+
            "    // This method sends the keystroke, then the on change fires for all keys. \r\n"+
            "	\r\n"+
            "	// Changed from keydown to changeCursor event to ensure correct updates of cursor position by removing onUpdateCursor from onKey\r\n"+
            "    this.element.addEventListener('keydown', function(e){that.onKey(e);}, true);\r\n"+
            "	this.editor.getSelection().on(\"changeCursor\", function(e){ that.onUpdateCursor(e); });\r\n"+
            "    // This method sends the keystroke then the on change fires for input keys, but does the reverse for removal keys like backspace. Thus the other method will be used.\r\n"+
            "//    this.editor.keyBinding.origOnCommandKey=this.editor.keyBinding.onCommandKey;\r\n"+
            "//    this.editor.keyBinding.onCommandKey=function(e,hashId,keyCode){that.editor.keyBinding.origOnCommandKey(e,hashId,keyCode);that.onKey(e)};\r\n"+
            "    this.editor.container.addEventListener('contextmenu',function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,that.getCursorPosition());});\r\n"+
            "   	this.editor.on(\"guttermousedown\", function(e) {\r\n"+
            "   		var target = e.domEvent.target; \r\n"+
            "	    if (target.className.indexOf(\"ace_gutter-cell\") == -1)\r\n"+
            "	        return; \r\n"+
            "	    if (!that.editor.isFocused()) \r\n"+
            "	        return; \r\n"+
            "	    if (e.clientX > 40 + target.getBoundingClientRect().left) \r\n"+
            "	        return; \r\n"+
            "	    var breakpoints = e.editor.session.getBreakpoints(row, 0);\r\n"+
            "		var row = e.getDocumentPosition().row;\r\n"+
            "		if(typeof breakpoints[row] === typeof undefined) {\r\n"+
            "		    e.editor.session.setBreakpoint(row);\r\n"+
            "		} else {\r\n"+
            "		    e.editor.session.clearBreakpoint(row);\r\n"+
            "		}\r\n"+
            "    	that.portlet.onCustom(that.id, \"onGutterMousedown\", {row: row});\r\n"+
            "		e.stop();\r\n"+
            "	});\r\n"+
            "	this.editor.getSession().on(\"changeScrollTop\", function(event) {\r\n"+
            "		that.onUpdateScrollTop(event);\r\n"+
            "	});\r\n"+
            "	this.editor.getSession().on(\"changeScrollLeft\", function(event) {\r\n"+
            "		that.onUpdateScrollLeft(event);\r\n"+
            "	});\r\n"+
            "	this.editor.renderer.on('afterRender', function(event) {\r\n"+
            "		if (that.scrollLineNum >= 0 && that.scrollLineNum < that.editor.session.getLength()) {\r\n"+
            "			that.editor.scrollToLine(that.scrollLineNum, true, true, function() {});\r\n"+
            "			that.scrollLineNum = -1;\r\n"+
            "		}\r\n"+
            "		if (that.annotate) {\r\n"+
            "			that.editor.getSession().setAnnotations([{row: that.annotationRow, column:0, type: that.annotationType, html: that.annotationMessage}]);\r\n"+
            "			that.annotate = false;\r\n"+
            "		}\r\n"+
            "	});\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.scrollToLine=function(row) {\r\n"+
            "	this.scrollLineNum = row;\r\n"+
            "	if (this.scrollLineNum >= 0 && this.scrollLineNum < this.editor.session.getLength()) {\r\n"+
            "    	this.editor.scrollToLine(this.scrollLineNum, true, true, function() {});\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "AmiCodeField.FLASH_COLORS = new Set([\"red\", \"yellow\", \"orange\"]);\r\n"+
            "AmiCodeField.prototype.flashRows=function(startRow, endRow, flashColor) {\r\n"+
            "	if (!flashColor || !AmiCodeField.FLASH_COLORS.has(flashColor.toLowerCase()))\r\n"+
            "		return;\r\n"+
            "	var flashMarker = this.editor.session.addMarker(new this.range(startRow,0,endRow,200), \"ace-flash-\" + flashColor.toLowerCase(), \"fullLine\", false);\r\n"+
            "	this.flashMarkers.push(flashMarker); // enqueue\r\n"+
            "	var that = this;\r\n"+
            "	var removeFlashMarkerTimeoutId = setTimeout(function() {\r\n"+
            "		that.editor.session.removeMarker(that.flashMarkers.shift()); // dequeue\r\n"+
            "	}, 500);\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.setAnnotation=function(row, type, text) {\r\n"+
            "	this.annotationRow = row;\r\n"+
            "	this.annotationType = type;\r\n"+
            "	this.annotationMessage = text;\r\n"+
            "	this.annotate = true;\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.clearAnnotation=function() {\r\n"+
            "	this.editor.getSession().clearAnnotations();\r\n"+
            "	this.annotate = false;\r\n"+
            "	this.annotationRow = -1;\r\n"+
            "	this.annotationType = null;\r\n"+
            "	this.annotationMessage = null;\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.handleClick=function(clickedRowNum, ctrlKey, shiftKey, altKey){ // clickedRowNum: 0 index based\r\n"+
            "    this.portlet.onCustom(this.id, \"click\", {row:clickedRowNum, ctrlKey:ctrlKey, shiftKey:shiftKey, altKey:altKey});\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.setBreakpoints=function(breakpoints){\r\n"+
            "	for (var i = 0; i < breakpoints.length; i++)\r\n"+
            "		this.editor.session.setBreakpoint(breakpoints[i]);\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.updateHighlight=function(highlightRow){\r\n"+
            "	if (highlightRow != -1) {\r\n"+
            "		if (this.curMarkerId != null)\r\n"+
            "			this.editor.session.removeMarker(this.curMarkerId);\r\n"+
            "		this.curMarkerId = this.editor.session.addMarker(new this.range(highlightRow,0,highlightRow,200), \"ace-highlight-row\", \"fullLine\");\r\n"+
            "	} else {\r\n"+
            "		if (this.curMarkerId != null) {\r\n"+
            "			this.editor.session.removeMarker(this.curMarkerId);\r\n"+
            "			this.curMarkerId = null;\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.setDisabled=function(disabled){\r\n"+
            "	this.disabled=disabled == true;\r\n"+
            "	this.editor.setReadOnly(disabled);\r\n"+
            "//	this.element.style.pointerEvents= this.disabled?\"none\":null;\r\n"+
            "	if(this.input)\r\n"+
            "		this.input.disabled=this.disabled;\r\n"+
            "}\r\n"+
            "	\r\n"+
            "AmiCodeField.prototype.setKeyboardHandler=function(amiEditorKeyboard){\r\n"+
            "	if(amiEditorKeyboard == \"vi\"){\r\n"+
            "		this.editor.setKeyboardHandler(\"ace/keyboard/vim\");\r\n"+
            "	}else if(amiEditorKeyboard == \"emacs\"){\r\n"+
            "		this.editor.setKeyboardHandler(\"ace/keyboard/emacs\");\r\n"+
            "	}\r\n"+
            "	else{\r\n"+
            "		this.editor.setKeyboardHandler(\"\");\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.setMode=function(mode){\r\n"+
            "	if(this.mode==mode)\r\n"+
            "		return;\r\n"+
            "	this.mode=mode;\r\n"+
            "    this.editor.session.setMode(\"ace/mode/\"+mode);\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.setValue=function(value){\r\n"+
            "	var c=this.getCursorPosition();\r\n"+
            "	this.needsUpdate = false;\r\n"+
            "	this.editor.getSession().setValue(value==null ? \"\" : this.cleanValue(value));\r\n"+
            "	this.needsUpdate = true;\r\n"+
            "	// TODO this doesn't make sense to me\r\n"+
            "	this.moveCursor(c);\r\n"+
            "};\r\n"+
            "AmiCodeField.prototype.onKey=function(e){\r\n"+
            "  if(e.key == \"Alt\" || e.key == \"Control\" || e.key == \"Shift\"){\r\n"+
            "  }else{\r\n"+
            "	  if(e.ctrlKey || e.altKey || currentContextMenu != null)\r\n"+
            "	    this.portlet.onKey(this.id,e,{pos:this.getCursorPosition()});\r\n"+
            "	  if(e.ctrlKey && e.key== \" \")\r\n"+
            "		  e.stopPropagation();\r\n"+
            "  }\r\n"+
            "};\r\n"+
            "AmiCodeField.prototype.getCursorPosition=function(){\r\n"+
            "	return this.cursorPosition;\r\n"+
            "};\r\n"+
            "AmiCodeField.prototype.moveCursor=function(i){\r\n"+
            "	this.cursorPosition = i;\r\n"+
            "	this.editor.selection.moveCursorToPosition(this.editor.getSession().doc.indexToPosition(i));\r\n"+
            "	//this.editor.focus();\r\n"+
            "};\r\n"+
            "AmiCodeField.prototype.moveScrollTop=function(pageY){\r\n"+
            "    this.editor.renderer.scrollToY(pageY);\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.moveScrollLeft=function(pageX){\r\n"+
            "    this.editor.renderer.scrollToX(pageX);\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.onFieldSizeChanged=function(){\r\n"+
            "	this.element.style.height=toPx(this.height);\r\n"+
            "    this.element.style.width=toPx(this.width);\r\n"+
            "	this.editor.resize();\r\n"+
            "}\r\n"+
            "//AmiCodeField.prototype.setHeight=function(value){\r\n"+
            "//	FormField.prototype.setHeight(value);\r\n"+
            "//	this.element.style.height=toPx(value);\r\n"+
            "//	this.editor.resize();\r\n"+
            "//}\r\n"+
            "//AmiCodeField.prototype.setWidth=function(width){\r\n"+
            "//	FormField.prototype.setWidth(width);\r\n"+
            "//    this.element.style.width=toPx(width);\r\n"+
            "//	this.editor.resize();\r\n"+
            "//}\r\n"+
            "AmiCodeField.prototype.onUpdateScrollTop=function(e){\r\n"+
            "	var scrollTop = this.editor.renderer.getScrollTop();\r\n"+
            "    this.portlet.onCustom(this.id, \"updateScrollTop\", { scrollTop:scrollTop });\r\n"+
            "};\r\n"+
            "AmiCodeField.prototype.onUpdateScrollLeft=function(e){\r\n"+
            "	var scrollLeft = this.editor.renderer.getScrollLeft();\r\n"+
            "    this.portlet.onCustom(this.id, \"updateScrollLeft\", { scrollLeft:scrollLeft});\r\n"+
            "};\r\n"+
            "AmiCodeField.prototype.onUpdateCursor=function(e){\r\n"+
            "    var cords=this.editor.renderer.textToScreenCoordinates(this.editor.selection.getCursor());\r\n"+
            "\r\n"+
            "	var editorCP = this.editor.getSession().doc.positionToIndex(this.editor.selection.getCursor());\r\n"+
            "    var position = null;\r\n"+
            "\r\n"+
            "	if(e instanceof KeyboardEvent)\r\n"+
            "		position = e.key == \"Backspace\"? (editorCP == 0? 0 : editorCP - 1): (e.key == \"Delete\" || e.altKey || e.ctrlKey)?editorCP: editorCP + 1;\r\n"+
            "	else\r\n"+
            "		position = editorCP;\r\n"+
            "	this.cursorPosition=position;\r\n"+
            "	this.portlet.onCustom(this.id, \"updateCursor\", {pos:position, pageX:cords.pageX, pageY:cords.pageY});\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.cleanValue=function(value){\r\n"+
            "	return value.replace(/\\r\\n/g, \"\\n\");\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.onChangeDiff=function(){\r\n"+
            "	if(this.needsUpdate){\r\n"+
            "		var newValue = this.cleanValue(this.getValue());\r\n"+
            "		var change = strDiff(this.oldValue, newValue);\r\n"+
            "		this.oldValue = newValue;\r\n"+
            "		this.portlet.onChange(this.id, {");
          out.print(
            "c:change.c,s:change.s,e:change.e,mid:this.getModificationNumber()});\r\n"+
            "	}else{\r\n"+
            "		this.oldValue = this.getValue();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.onAfterExec=function(e){\r\n"+
            "	var showAutocomplete = false;\r\n"+
            "    if (e.command.name === \"backspace\") {\r\n"+
            "		showAutocomplete = true;\r\n"+
            "    } else if (e.command.name === \"insertstring\") {\r\n"+
            "		showAutocomplete = true;\r\n"+
            "	}\r\n"+
            "	if(showAutocomplete){\r\n"+
            "		this.portlet.onCustom(this.id, \"showAC\", {});\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "AmiCodeField.prototype.getValue=function(){\r\n"+
            "		return this.editor.getSession().getValue();\r\n"+
            "};\r\n"+
            "AmiCodeField.prototype.focusField=function(){\r\n"+
            "	this.editor.focus();\r\n"+
            "}\r\n"+
            "");

	}
	
}