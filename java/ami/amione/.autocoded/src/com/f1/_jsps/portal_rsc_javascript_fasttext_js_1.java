package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_fasttext_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_fasttext_js_1() {
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
            "\r\n"+
            "function FastText(element){\r\n"+
            "  var that=this;\r\n"+
            "  this.ignoreOnScroll=true;\r\n"+
            "  this.containerElement=element;\r\n"+
            "  this.textElement=nw(\"div\");\r\n"+
            "  this.textElement.className='text';\r\n"+
            "  this.scrollPaneDiv=nw('div');\r\n"+
            "  this.scrollPane=new ScrollPane(this.scrollPaneDiv,15,this.textElement);\r\n"+
            "  this.scrollPane.onScroll=function(){that.onScroll()};\r\n"+
            "  this.rowHeight=15;\r\n"+
            "  this.upperRow=0;\r\n"+
            "  this.bottomRow=0;\r\n"+
            "  this.containerElement.appendChild(this.scrollPaneDiv);\r\n"+
            "  this.data={};\r\n"+
            "  this.clipPadding=100;\r\n"+
            "  this.cells=[];\r\n"+
            "  this.labls=[];\r\n"+
            "  this.labelWidthPx=toPx(75);\r\n"+
            "  this.isInit=false;\r\n"+
            "  this.selectedRows=new SelectedRows();\r\n"+
            "  this.selectedRows.add(4,9);\r\n"+
            "  this.selectedRows.add(15,22);\r\n"+
            "  this.onCellMouseDownFunc=function(e){e.stopPropagation();return that.onCellMouseDown(e);};\r\n"+
            "  this.onCellMouseUpFunc=function(e){return that.onCellMouseUp(e);};\r\n"+
            "  this.onLabelMouseDownFunc=function(e){e.stopPropagation();return that.onLabelMouseDown(e);};\r\n"+
            "  this.scrollPane.onTickClick=function(id,pos){return that.onTickClick(id,pos)};\r\n"+
            "}\r\n"+
            "FastText.prototype.getScrollPane=function(){\r\n"+
            "  return this.scrollPane;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.init=function(linesCount,rowHeight,maxChars,labelWidth,labelStyle,style){\r\n"+
            "	this.labelStyle=labelStyle;\r\n"+
            "	applyStyle(this.textElement,style);\r\n"+
            "    this.isInit=false;\r\n"+
            "    this.selectedRows.clear();\r\n"+
            "	this.linesCount=linesCount;\r\n"+
            "	this.rowHeight=rowHeight;\r\n"+
            "	this.rowHeightPx=toPx(rowHeight);\r\n"+
            "	this.maxChars=maxChars;\r\n"+
            "    this.labelWidthPx=toPx(labelWidth);\r\n"+
            "    this.labelWidth=labelWidth;\r\n"+
            "	this.scrollPane.setPaneSize(this.labelWidth+maxChars*7,linesCount*rowHeight);\r\n"+
            "	this.scrollPane.vscroll.setRange(0,this.linesCount-1);\r\n"+
            "	if(this.width)\r\n"+
            "		this.setLocation(0,0,this.width,this.height);\r\n"+
            "    for(var i in this.cells){\r\n"+
            "    	this.textElement.removeChild(this.cells[i]);\r\n"+
            "    	this.textElement.removeChild(this.labls[i]);\r\n"+
            "    }\r\n"+
            "    this.scrollPane.setClipTop(0);\r\n"+
            "    this.scrollPane.setClipLeft(0);\r\n"+
            "    this.data={};\r\n"+
            "    this.cells=[];\r\n"+
            "    this.labls=[];\r\n"+
            "	this.isInit=true;\r\n"+
            "    this.updateCells();\r\n"+
            "	this.updateColsVisible();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "FastText.prototype.setData=function(data){\r\n"+
            "	for(var i=0;i<data.length;){\r\n"+
            "		var dat=data[i++];\r\n"+
            "		var pos=dat.p;\r\n"+
            "		this.data[pos]=dat;\r\n"+
            "		if(pos>=this.upperRow && pos<this.bottomRow){\r\n"+
            "			var offset=pos-this.upperRow;\r\n"+
            "			var cell=this.cells[offset];\r\n"+
            "			if(cell!=null){\r\n"+
            "			  cell.innerHTML=dat.v;\r\n"+
            "    		  resetAppliedStyles(cell);\r\n"+
            "			  applyStyle(cell,dat.s);\r\n"+
            "			  this.labls[offset].innerHTML=dat.l;\r\n"+
            "			}\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.updateColsVisible=function(x,y,width,height){\r\n"+
            "  var colsVisible=fl((this.width-20-this.labelWidth)/7);\r\n"+
            "  if(isNaN(colsVisible))\r\n"+
            "	  return;\r\n"+
            "  if(colsVisible!=this.colsVisible){\r\n"+
            "    this.onColumnsVisible(colsVisible);\r\n"+
            "    this.colsVisible=colsVisible;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.setLocation=function(x,y,width,height){\r\n"+
            "    this.width=width;\r\n"+
            "	this.height=height;\r\n"+
            "	this.updateColsVisible();\r\n"+
            "	\r\n"+
            "    this.ignoreOnScroll=true;\r\n"+
            "	this.scrollPane.setLocation(x,y,width,height);\r\n"+
            "    this.ignoreOnScroll=false;\r\n"+
            "    new Rect(0,0,width,height).writeToElement(this.containerElement);\r\n"+
            "    new Rect(0,0,Math.max(width,this.maxChars*20),height+this.rowHeight).writeToElement(this.textElement);\r\n"+
            "    this.onScroll();\r\n"+
            "    this.updateCells();\r\n"+
            "    	\r\n"+
            "}\r\n"+
            "\r\n"+
            "//FastText.prototype.updateBounds=function(){\r\n"+
            "    //this.visibleRowsCount=Math.ceil((this.height-this.headerHeight-(this.hscrollVisible ? this.scrollSize : 0))/this.rowHeight);\r\n"+
            "    //new Rect(0,0,Math.max(this.totalWidth,this.width),this.headerHeight).writeToElement(this.headerElement);\r\n"+
            "    //new Rect(0,this.headerHeight,max(this.totalWidth,this.width),this.height-(this.hscrollVisible ? this.scrollSize : 0)-this.headerHeight+this.rowHeight*2).writeToElement(this.textElement);\r\n"+
            "    //this.updateCells();\r\n"+
            "//}\r\n"+
            "\r\n"+
            "FastText.prototype.updateCells=function(){\r\n"+
            "	if(!this.isInit)\r\n"+
            "		return false;\r\n"+
            "    for(i=0;i<=this.bottomRow-this.upperRow;i++){\r\n"+
            "    	var cell=this.cells[i];\r\n"+
            "    	var labl=this.labls[i];\r\n"+
            "    	var pos=i+this.upperRow;\r\n"+
            "    	if(cell==null){\r\n"+
            "    	  var top=toPx(i*this.rowHeight);\r\n"+
            "    	  cell=this.cells[i]=nw('div','text_contents');\r\n"+
            "    	  cell.style.left=this.labelWidthPx;\r\n"+
            "    	  cell.style.height=this.rowHeightPx;\r\n"+
            "    	  labl=this.labls[i]=nw('div','text_label');\r\n"+
            "    	  labl.onclick=this.onLabelMouseDownFunc;\r\n"+
            "    	  labl.style.left='0px';\r\n"+
            "    	  labl.style.width=this.labelWidthPx;\r\n"+
            "    	  labl.style.height=this.rowHeightPx;\r\n"+
            "    	  cell.style.top=top;\r\n"+
            "    	  cell.style.width='100%';\r\n"+
            "    	  cell.onmousedown=this.onCellMouseDownFunc;\r\n"+
            "    	  labl.style.top=top;\r\n"+
            "	      applyStyle(labl,this.labelStyle);\r\n"+
            "    	  this.textElement.appendChild(labl);\r\n"+
            "    	  this.textElement.appendChild(cell);\r\n"+
            "    	}\r\n"+
            "    	cell.pos=pos;\r\n"+
            "    	var dat=this.data[pos];\r\n"+
            "    	if(this.selectedRows.isSelected(pos))\r\n"+
            "    	  cell.className='text_contents text_selected';\r\n"+
            "    	else\r\n"+
            "    	  cell.className='text_contents';\r\n"+
            "    	if(dat!=null){\r\n"+
            "    		cell.innerHTML=dat.v;\r\n"+
            "    		resetAppliedStyles(cell);\r\n"+
            "			applyStyle(cell,dat.s);\r\n"+
            "    		labl.innerHTML=dat.l;\r\n"+
            "    	}else {\r\n"+
            "    		cell.innerHTML='';\r\n"+
            "    		resetAppliedStyles(cell);\r\n"+
            "			applyStyle(cell,'');\r\n"+
            "    		labl.innerHTML='';\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastText.prototype.onScroll=function(){\r\n"+
            "  if(this.ignoreOnScroll)\r\n"+
            "    return;\r\n"+
            "   var v=Math.round(this.scrollPane.getClipTop())/this.rowHeight;\r\n"+
            "   var rows=Math.round(this.height/this.rowHeight)+1;\r\n"+
            "   this.textElement.style.top=toPx((Math.floor(v)-v)*this.rowHeight);\r\n"+
            "   this.textElement.style.left=toPx(-1*(this.scrollPane.getClipLeft()));\r\n"+
            "   v=Math.floor(v);\r\n"+
            "   this.upperRow=v;\r\n"+
            "   this.bottomRow=v+rows;\r\n"+
            "   this.updateCells();\r\n"+
            "   this.onClip(v,this.bottomRow);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "\r\n"+
            "FastText.prototype.onCellMouseDown=function(e){\r\n"+
            "	var that=this;\r\n"+
            "    var shiftKey=e.shiftKey;\r\n"+
            "    var ctrlKey=e.ctrlKey;\r\n"+
            "	var cell=getMouseTarget(e);\r\n"+
            "	var row=cell.pos;\r\n"+
            "	while(row==null){\r\n"+
            "	  cell=cell.parentNode;\r\n"+
            "	  if(cell==null)\r\n"+
            "	  return;\r\n"+
            "	    row=cell.pos;\r\n"+
            "	}\r\n"+
            "	document.onmouseup=this.onCellMouseUpFunc;\r\n"+
            "	document.onmousemove=function(e){that.onOutsideMouseDragging(e);};\r\n"+
            "	var button=getMouseButton(e);\r\n"+
            "	if(button==2){\r\n"+
            "	  var cell=getMouseTarget(e);\r\n"+
            "	  var row=cell.pos;\r\n"+
            "	  while(row==null){\r\n"+
            "	    cell=cell.parentNode;\r\n"+
            "	    row=cell.pos;\r\n"+
            "	  }\r\n"+
            "	  if(!this.selectedRows.isSelected(row)){\r\n"+
            "		  this.selectedRows.clear();\r\n"+
            "		  this.selectRow(row,shiftKey,ctrlKey,false);\r\n"+
            "	  }\r\n"+
            "	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "	  if(this.onUserContextMenu!=null)\r\n"+
            "		  this.onUserContextMenu(e);\r\n"+
            "	var col=cell.colLocation;\r\n"+
            "	}else{\r\n"+
            "      if(!e.shiftKey  && !e.ctrlKey){//TODO: why does this block exist?\r\n"+
            "	    this.dragSelect=true;\r\n"+
            "      }\r\n"+
            "	  this.selectRow(row,shiftKey,ctrlKey,false);\r\n"+
            "      //this.dragOutsideTimer = window.setInterval(function(){that.onOutsideDragging();}, 100);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.selectRow=function(row,shiftKey,ctrlKey,isDragging){\r\n"+
            "	var before=this.selectedRows.toString();\r\n"+
            "	var beforeCurrentRow=this.currentRow;\r\n"+
            "	if(!isDragging && this.dragSelect){\r\n"+
            "		this.dragStart=row;\r\n"+
            "	}\r\n"+
            "    if(ctrlKey && shiftKey){\r\n"+
            "    	if(this.currentRow==-1)\r\n"+
            "    	  this.selectedRows.remove(row);\r\n"+
            "    	else if(this.currentRow<row)\r\n"+
            "    	  this.selectedRows.remove(this.currentRow,row);\r\n"+
            "    	else\r\n"+
            "    	  this.selectedRows.remove(row,this.currentRow);\r\n"+
            "    }else if(ctrlKey){\r\n"+
            "      if(this.selectedRows.isSelected(row))\r\n"+
            "  	    this.selectedRows.remove(row);\r\n"+
            "      else\r\n"+
            "  	    this.selectedRows.add(row);\r\n"+
            "    }else if(shiftKey){\r\n"+
            "    	if(this.currentRow==-1)\r\n"+
            "    	  this.selectedRows.add(row);\r\n"+
            "    	else if(this.currentRow<row)\r\n"+
            "    	  this.selectedRows.add(this.currentRow,row);\r\n"+
            "    	else\r\n"+
            "    	  this.selectedRows.add(row,this.currentRow);\r\n"+
            "    }else if(this.dragSelect){\r\n"+
            "    	this.selectedRows.clear();\r\n"+
            "    	if(row>this.dragStart)\r\n"+
            "    	  this.selectedRows.add(this.dragStart,row);\r\n"+
            "    	else\r\n"+
            "    	  this.selectedRows.add(row,this.dragStart);\r\n"+
            "    }else{\r\n"+
            "	  this.selectedRows.clear();\r\n"+
            "  	  this.selectedRows.add(row);\r\n"+
            "    }\r\n"+
            "    if(this.selectedRows.isSelected(row))\r\n"+
            "	  this.currentRow=row;\r\n"+
            "    else\r\n"+
            "	  this.currentRow=-1;\r\n"+
            "	this.updateCells();\r\n"+
            "	var after=this.selectedRows.toString();\r\n"+
            "	if(this.onUserSelected!=null && (after!=before || this.currentRow!=beforeCurrentRow))\r\n"+
            "		this.onUserSelected(this.selectedRows.toString());\r\n"+
            "	return false;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.onOutsideMouseDragging=function(e){\r\n"+
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
            "	    var row=cell.pos;\r\n"+
            "	    while(row==null){\r\n"+
            "	      cell=cell.parentNode;\r\n"+
            "	      if(cell==null)\r\n"+
            "	    	  break;\r\n"+
            "	      row=cell.pos;\r\n"+
            "	    }\r\n"+
            "	    if(row!=null)\r\n"+
            "	  	  this.selectRow(row,false,false,true);\r\n"+
            "	  }  \r\n"+
            "  }\r\n"+
            "	  \r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.onCellMouseUp=function(e){\r\n"+
            "	this.stopOutsideDragging();\r\n"+
            "	this.dragSelect=false;\r\n"+
            "	document.onmouseup=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.stopOutsideDragging=function(){\r\n"+
            "  if(this.dragOutsideTimer!=null){\r\n"+
            "    window.clearInterval(this.dragOutsideTimer);\r\n"+
            "    this.dragOutsideTimer=null;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.setTopLine=function(line){\r\n"+
            "  this.scrollPane.setClipTop(line*this.rowHeight);\r\n"+
            "}\r\n"+
            "FastText.prototype.onTickClick=function(pos){\r\n"+
            "	this.setTopLine(pos-fl((this.bottomRow-this.upperRow)/2));\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.setSelectedLines=function(text){\r\n"+
            "	this.selectedRows.parseString(text);\r\n"+
            "	this.updateCells();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastText.prototype.showContextMenu=function(menu){\r\n"+
            "  this.createMenu(menu).show(this.contextMenuPoint);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastText.prototype.createMenu=function(menu){\r\n"+
            "   var that=this;\r\n"+
            "   var r=new Menu(getWindow(this.containerElement));\r\n"+
            "   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e,id);} );\r\n"+
            "   return ");
          out.print(
            "r;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastText.prototype.setScrollTicks=function(data){\r\n"+
            "	this.scrollPane.clearTicks();\r\n"+
            "	var rheight=(this.height-45) / this.linesCount;\r\n"+
            "	var width=Math.floor(15 / data.length);\r\n"+
            "	for(var i=0;i<data.length;i++){\r\n"+
            "		var cat=data[i];\r\n"+
            "		var style=cat.style;\r\n"+
            "		var ticks=cat.ticks;\r\n"+
            "		var left=width*i;\r\n"+
            "		for(var j=0;j<ticks.length;){\r\n"+
            "			var pos=ticks[j++];\r\n"+
            "			var len=ticks[j++];\r\n"+
            "		    this.scrollPane.addTick(pos,left,pos*rheight-15,width,Math.max(2,len*rheight),style);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "");

	}
	
}