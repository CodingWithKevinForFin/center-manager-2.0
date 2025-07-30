AmiCodeField.prototype=new FormField();
function AmiCodeField(portlet,id,title){
	FormField.call(this,portlet,id,title);
	var win=portletManager.getWindow(portlet.portlet.owningWindowId);
	this.element=(win||window).document.createElement('pre');
	this.element.setAttribute("id", "ace-editor-id-" + id);
    this.element.style.border='1px solid #AAAAAA';
    this.element.style.textAlign='left';
    this.element.style.margin='0px';
	this.oldValue = "";
	this.cursorPosition = 0;
	this.scrollLineNum = null;
    
    this.editor = ace.edit(this.element);
    this.setMode("amiscript");
    this.editor.session.setUseWorker(false);
    this.editor.getSession().setUseWrapMode(true);
    this.editor.getSession().setUseSoftTabs(true);
    this.editor.getSession().setTabSize(2);
    this.editor.setShowPrintMargin(false);
    this.editor.setHighlightActiveLine(false);
    this.editor.$blockScrolling=Infinity;
    this.needsUpdate = true;
	this.range = ace.require("ace/range").Range;
	this.curMarkerId = null;
	this.flashMarkers = []; // this will work as a queue
    var that = this;
    //this.element.onchange=function(){that.portlet.onChange(that.id,{value:that.getValue()});};
    //this.element.onmousedown=function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,getCursorPosition(that.element));};
    //this.element.onkeydown=function(e){that.onKeyDown(e);};
    this.element.onblur=function(){that.onChangeDiff();};
    this.editor.getSession().on('change',function(){that.onChangeDiff();});
	this.editor.commands.on('afterExec', function(e) { that.onAfterExec(e); });
	this.editor.addEventListener("click", function(event) {
		that.handleClick(event.getDocumentPosition().row, event.domEvent.ctrlKey, event.domEvent.shiftKey, event.domEvent.altKey);
		that.onUpdateCursor(event);
	});
    // Two methods of handling keystrokes
    // This method sends the keystroke, then the on change fires for all keys. 
	
	// Changed from keydown to changeCursor event to ensure correct updates of cursor position by removing onUpdateCursor from onKey
    this.element.addEventListener('keydown', function(e){that.onKey(e);}, true);
	this.editor.getSelection().on("changeCursor", function(e){ that.onUpdateCursor(e); });
    // This method sends the keystroke then the on change fires for input keys, but does the reverse for removal keys like backspace. Thus the other method will be used.
//    this.editor.keyBinding.origOnCommandKey=this.editor.keyBinding.onCommandKey;
//    this.editor.keyBinding.onCommandKey=function(e,hashId,keyCode){that.editor.keyBinding.origOnCommandKey(e,hashId,keyCode);that.onKey(e)};
    this.editor.container.addEventListener('contextmenu',function(e){if(getMouseButton(e)==2)that.portlet.onUserContextMenu(e,that.id,that.getCursorPosition());});
   	this.editor.on("guttermousedown", function(e) {
   		var target = e.domEvent.target; 
	    if (target.className.indexOf("ace_gutter-cell") == -1)
	        return; 
	    if (!that.editor.isFocused()) 
	        return; 
	    if (e.clientX > 40 + target.getBoundingClientRect().left) 
	        return; 
	    var breakpoints = e.editor.session.getBreakpoints(row, 0);
		var row = e.getDocumentPosition().row;
		if(typeof breakpoints[row] === typeof undefined) {
		    e.editor.session.setBreakpoint(row);
		} else {
		    e.editor.session.clearBreakpoint(row);
		}
    	that.portlet.onCustom(that.id, "onGutterMousedown", {row: row});
		e.stop();
	});
	this.editor.getSession().on("changeScrollTop", function(event) {
		that.onUpdateScrollTop(event);
	});
	this.editor.getSession().on("changeScrollLeft", function(event) {
		that.onUpdateScrollLeft(event);
	});
	this.editor.renderer.on('afterRender', function(event) {
		if (that.scrollLineNum >= 0 && that.scrollLineNum < that.editor.session.getLength()) {
			that.editor.scrollToLine(that.scrollLineNum, true, true, function() {});
			that.scrollLineNum = -1;
		}
		if (that.annotate) {
			that.editor.getSession().setAnnotations([{row: that.annotationRow, column:0, type: that.annotationType, html: that.annotationMessage}]);
			that.annotate = false;
		}
	});
}
AmiCodeField.prototype.scrollToLine=function(row) {
	this.scrollLineNum = row;
	if (this.scrollLineNum >= 0 && this.scrollLineNum < this.editor.session.getLength()) {
    	this.editor.scrollToLine(this.scrollLineNum, true, true, function() {});
    }
}
AmiCodeField.FLASH_COLORS = new Set(["red", "yellow", "orange"]);
AmiCodeField.prototype.flashRows=function(startRow, endRow, flashColor) {
	if (!flashColor || !AmiCodeField.FLASH_COLORS.has(flashColor.toLowerCase()))
		return;
	var flashMarker = this.editor.session.addMarker(new this.range(startRow,0,endRow,200), "ace-flash-" + flashColor.toLowerCase(), "fullLine", false);
	this.flashMarkers.push(flashMarker); // enqueue
	var that = this;
	var removeFlashMarkerTimeoutId = setTimeout(function() {
		that.editor.session.removeMarker(that.flashMarkers.shift()); // dequeue
	}, 500);
}
AmiCodeField.prototype.setAnnotation=function(row, type, text) {
	this.annotationRow = row;
	this.annotationType = type;
	this.annotationMessage = text;
	this.annotate = true;
}
AmiCodeField.prototype.clearAnnotation=function() {
	this.editor.getSession().clearAnnotations();
	this.annotate = false;
	this.annotationRow = -1;
	this.annotationType = null;
	this.annotationMessage = null;
}
AmiCodeField.prototype.handleClick=function(clickedRowNum, ctrlKey, shiftKey, altKey){ // clickedRowNum: 0 index based
    this.portlet.onCustom(this.id, "click", {row:clickedRowNum, ctrlKey:ctrlKey, shiftKey:shiftKey, altKey:altKey});
}
AmiCodeField.prototype.setBreakpoints=function(breakpoints){
	for (var i = 0; i < breakpoints.length; i++)
		this.editor.session.setBreakpoint(breakpoints[i]);
}
AmiCodeField.prototype.updateHighlight=function(highlightRow){
	if (highlightRow != -1) {
		if (this.curMarkerId != null)
			this.editor.session.removeMarker(this.curMarkerId);
		this.curMarkerId = this.editor.session.addMarker(new this.range(highlightRow,0,highlightRow,200), "ace-highlight-row", "fullLine");
	} else {
		if (this.curMarkerId != null) {
			this.editor.session.removeMarker(this.curMarkerId);
			this.curMarkerId = null;
		}
	}
}
AmiCodeField.prototype.setDisabled=function(disabled){
	this.disabled=disabled == true;
	this.editor.setReadOnly(disabled);
//	this.element.style.pointerEvents= this.disabled?"none":null;
	if(this.input)
		this.input.disabled=this.disabled;
}
	
AmiCodeField.prototype.setKeyboardHandler=function(amiEditorKeyboard){
	if(amiEditorKeyboard == "vi"){
		this.editor.setKeyboardHandler("ace/keyboard/vim");
	}else if(amiEditorKeyboard == "emacs"){
		this.editor.setKeyboardHandler("ace/keyboard/emacs");
	}
	else{
		this.editor.setKeyboardHandler("");
	}
}
AmiCodeField.prototype.setMode=function(mode){
	if(this.mode==mode)
		return;
	this.mode=mode;
    this.editor.session.setMode("ace/mode/"+mode);
}
AmiCodeField.prototype.setValue=function(value){
	var c=this.getCursorPosition();
	this.needsUpdate = false;
	this.editor.getSession().setValue(value==null ? "" : this.cleanValue(value));
	this.needsUpdate = true;
	// TODO this doesn't make sense to me
	this.moveCursor(c);
};
AmiCodeField.prototype.onKey=function(e){
  if(e.key == "Alt" || e.key == "Control" || e.key == "Shift"){
  }else{
	  if(e.ctrlKey || e.altKey || currentContextMenu != null)
	    this.portlet.onKey(this.id,e,{pos:this.getCursorPosition()});
	  if(e.ctrlKey && e.key== " ")
		  e.stopPropagation();
  }
};
AmiCodeField.prototype.getCursorPosition=function(){
	return this.cursorPosition;
};
AmiCodeField.prototype.moveCursor=function(i){
	this.cursorPosition = i;
	this.editor.selection.moveCursorToPosition(this.editor.getSession().doc.indexToPosition(i));
	//this.editor.focus();
};
AmiCodeField.prototype.moveScrollTop=function(pageY){
    this.editor.renderer.scrollToY(pageY);
}
AmiCodeField.prototype.moveScrollLeft=function(pageX){
    this.editor.renderer.scrollToX(pageX);
}
AmiCodeField.prototype.onFieldSizeChanged=function(){
	this.element.style.height=toPx(this.height);
    this.element.style.width=toPx(this.width);
	this.editor.resize();
}
//AmiCodeField.prototype.setHeight=function(value){
//	FormField.prototype.setHeight(value);
//	this.element.style.height=toPx(value);
//	this.editor.resize();
//}
//AmiCodeField.prototype.setWidth=function(width){
//	FormField.prototype.setWidth(width);
//    this.element.style.width=toPx(width);
//	this.editor.resize();
//}
AmiCodeField.prototype.onUpdateScrollTop=function(e){
	var scrollTop = this.editor.renderer.getScrollTop();
    this.portlet.onCustom(this.id, "updateScrollTop", { scrollTop:scrollTop });
};
AmiCodeField.prototype.onUpdateScrollLeft=function(e){
	var scrollLeft = this.editor.renderer.getScrollLeft();
    this.portlet.onCustom(this.id, "updateScrollLeft", { scrollLeft:scrollLeft});
};
AmiCodeField.prototype.onUpdateCursor=function(e){
    var cords=this.editor.renderer.textToScreenCoordinates(this.editor.selection.getCursor());

	var editorCP = this.editor.getSession().doc.positionToIndex(this.editor.selection.getCursor());
    var position = null;

	if(e instanceof KeyboardEvent)
		position = e.key == "Backspace"? (editorCP == 0? 0 : editorCP - 1): (e.key == "Delete" || e.altKey || e.ctrlKey)?editorCP: editorCP + 1;
	else
		position = editorCP;
	this.cursorPosition=position;
	this.portlet.onCustom(this.id, "updateCursor", {pos:position, pageX:cords.pageX, pageY:cords.pageY});
}
AmiCodeField.prototype.cleanValue=function(value){
	return value.replace(/\r\n/g, "\n");
}
AmiCodeField.prototype.onChangeDiff=function(){
	if(this.needsUpdate){
		var newValue = this.cleanValue(this.getValue());
		var change = strDiff(this.oldValue, newValue);
		this.oldValue = newValue;
		this.portlet.onChange(this.id, {c:change.c,s:change.s,e:change.e,mid:this.getModificationNumber()});
	}else{
		this.oldValue = this.getValue();
	}
}
AmiCodeField.prototype.onAfterExec=function(e){
	var showAutocomplete = false;
    if (e.command.name === "backspace") {
		showAutocomplete = true;
    } else if (e.command.name === "insertstring") {
		showAutocomplete = true;
	}
	if(showAutocomplete){
		this.portlet.onCustom(this.id, "showAC", {});
	}
}
AmiCodeField.prototype.getValue=function(){
		return this.editor.getSession().getValue();
};
AmiCodeField.prototype.focusField=function(){
	this.editor.focus();
}
