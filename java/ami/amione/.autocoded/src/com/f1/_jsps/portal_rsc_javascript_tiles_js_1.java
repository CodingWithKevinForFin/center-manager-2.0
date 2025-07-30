package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_tiles_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_tiles_js_1() {
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
            "function TilesPanel(containerElement){\r\n"+
            "	var that=this;\r\n"+
            "	this.containerElement=containerElement;\r\n"+
            "	this.tilesElement=nw('div','tiles_panel');\r\n"+
            "	this.containerElement.appendChild(this.tilesElement);\r\n"+
            "    this.tilesElement.style.left='0px';\r\n"+
            "    this.vscrollElement=nw(\"div\");\r\n"+
            "	this.tileWidth=100;\r\n"+
            "	this.tilePadding=10;\r\n"+
            "	this.tileHeight= 50;\r\n"+
            "	this.tileDivsPool={};\r\n"+
            "	this.tilesOffset=0;\r\n"+
            "    this.clipTop=0;\r\n"+
            "    this.vscroll=new ScrollBar(this.vscrollElement,false);\r\n"+
            "    this.vscroll.onscroll=function(){that.onScroll()};\r\n"+
            "    this.vscrollVisible=false;\r\n"+
            "    this.scrollSize=15;\r\n"+
            "    this.tilesCount=0;\r\n"+
            "    this.tilesByPosition=[];\r\n"+
            "    this.tilesById={};\r\n"+
            "    this.clipTop=0;\r\n"+
            "    this.clipBottom=-1;\r\n"+
            "    this.selectedTiles=new SelectedRows();\r\n"+
            "    this.align='left';\r\n"+
            "//    this.containerElement.onMouseWheel=function(e,delta){that.onMouseWheel(e,delta);};\r\n"+
            "    this.onTileMouseClickFunc=function(e){e.stopPropagation();return that.onTileMouseClick(e);};\r\n"+
            "    this.onTileMouseDblClickFunc=function(e){e.stopPropagation();return that.onTileMouseDblClick(e);};\r\n"+
            "    this.onTileKeyDownFunc=function(e){e.stopPropagation();return that.onTileKeyDown(e);};\r\n"+
            "    this.tilesElement.onmousedown=function(e){return that.onBackgroundClick(e);};\r\n"+
            "    this.tilesElement.ondblclick=function(e){return that.onBackgroundDblClick(e);};\r\n"+
            "    this.activeTilePos=-1;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPanel.prototype.onBackgroundClick=function(e){\r\n"+
            "	if(this.tilesElement!=getMouseTarget(e))\r\n"+
            "		return;\r\n"+
            "	var button=getMouseButton(e);\r\n"+
            "	this.selectedTiles.parseString(\"\");\r\n"+
            "	this.activeTilePos=-1;\r\n"+
            "	if(button===2){\r\n"+
            "	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "	  if(this.onUserContextMenu!=null)\r\n"+
            "		  this.onUserContextMenu(e);\r\n"+
            "	}else{\r\n"+
            "	  if(this.onUserSelected!=null){\r\n"+
            "		this.onUserSelected(this.activeTilePos,this.selectedTiles.toString());\r\n"+
            "	  }\r\n"+
            "	  this.repaint();\r\n"+
            "	}\r\n"+
            "	return true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPanel.prototype.onBackgroundDblClick=function(e){\r\n"+
            "	if(this.onUserDblClick!=null)\r\n"+
            "	  this.onUserDblClick(e,-1);\r\n"+
            "	return true;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPanel.prototype.onTileKeyDown=function(e){\r\n"+
            "	var tileDiv=getMouseTarget(e);\r\n"+
            "	var tile=tileDiv.tile;\r\n"+
            "    var pos=this.activeTilePos;\r\n"+
            "    if(pos===-1)\r\n"+
            "    	return;\r\n"+
            "	var active=this.tilesByPosition[this.activeTolPos];\r\n"+
            "	var newPos=-1;\r\n"+
            "	if(e.keyCode===38){//up\r\n"+
            "	  newPos=pos-this.columnsCount;\r\n"+
            "	}else if(e.keyCode===40){//down\r\n"+
            "	  newPos=pos+this.columnsCount;\r\n"+
            "	}else if(e.keyCode===37){//left\r\n"+
            "	  newPos=pos-1;\r\n"+
            "	}else if(e.keyCode===39){//right\r\n"+
            "	  newPos=pos+1;\r\n"+
            "	}\r\n"+
            "	//var newActiveTile=this.tilesByPosition[newPos];\r\n"+
            "	//if(newActiveTile!=null){\r\n"+
            "	if(newPos>=0 && newPos<this.tilesCount)\r\n"+
            "		this.selectTile(newPos,e.ctrlKey,e.shiftKey);\r\n"+
            "	//}\r\n"+
            "	\r\n"+
            "	//else if(e.keyCode==33)//pgup\r\n"+
            "	//else if(e.keyCode==34)//pgdn\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.onTileMouseDblClick=function(e){\r\n"+
            "	var tileDiv=getMouseTarget(e);\r\n"+
            "	var button=getMouseButton(e);\r\n"+
            "	var tile=tileDiv.tile;\r\n"+
            "	if(tile===null)\r\n"+
            "		return;\r\n"+
            "	if(this.onUserDblClick!=null){\r\n"+
            "	  this.onUserDblClick(e,tile.pos);\r\n"+
            "	}\r\n"+
            "	return true;\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.onTileMouseClick=function(e){\r\n"+
            "	var tileDiv=getMouseTarget(e);\r\n"+
            "	var button=getMouseButton(e);\r\n"+
            "	var tile=tileDiv.tile;\r\n"+
            "	if(tile===null)\r\n"+
            "		return;\r\n"+
            "	var pos=tile.pos;\r\n"+
            "	if(button===2){\r\n"+
            "	  if(!this.selectedTiles.isSelected(pos)){\r\n"+
            "		  this.selectedTiles.clear();\r\n"+
            "		  this.selectTile(pos,e.ctrlKey,e.shiftKey);\r\n"+
            "		  this.repaint();\r\n"+
            "	  }\r\n"+
            "	  this.contextMenuPoint=getMousePoint(e).move(-4,-4);\r\n"+
            "	  if(this.onUserContextMenu!=null)\r\n"+
            "		  this.onUserContextMenu(e);\r\n"+
            "	}else{\r\n"+
            "	  this.selectTile(pos,e.ctrlKey,e.shiftKey)\r\n"+
            "	}\r\n"+
            "	return true;\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.selectTile=function(pos,ctrlKey,shiftKey){\r\n"+
            "	var before=this.selectedTiles.toString();\r\n"+
            "	var beforeActiveTilePos=this.activeTilePos;\r\n"+
            "	var repaint=false;\r\n"+
            "    if(shiftKey && this.multiselect){\r\n"+
            "      this.selectedTiles.add(this.activeTilePos===-1 ? pos : this.activeTilePos,pos);\r\n"+
            "      this.setActiveTilePos(pos);\r\n"+
            "      repaint=true;\r\n"+
            "    }else if(ctrlKey && this.multiselect){\r\n"+
            "      if(this.selectedTiles.isSelected(pos))\r\n"+
            "  	    this.selectedTiles.remove(pos);\r\n"+
            "      else{\r\n"+
            "  	    this.selectedTiles.add(pos);\r\n"+
            "        this.setActiveTilePos(pos);\r\n"+
            "      }\r\n"+
            "    }else {\r\n"+
            "    	if(!this.selectedTiles.isEmpty()){\r\n"+
            "  	       this.selectedTiles.clear();\r\n"+
            "  	       repaint=true;\r\n"+
            "    	}\r\n"+
            "  	    this.selectedTiles.add(pos);\r\n"+
            "        this.setActiveTilePos(pos);\r\n"+
            "    }\r\n"+
            "    	\r\n"+
            "    \r\n"+
            "    if(repaint){\r\n"+
            "	  this.repaint();\r\n"+
            "      var t=this.tileDivsPool[this.activeTilePos];\r\n"+
            "    }else{\r\n"+
            "      var tile=this.tilesByPosition[pos];\r\n"+
            "	  this.applyStyle(this.tileDivsPool[tile.pos],tile);\r\n"+
            "	}\r\n"+
            "    \r\n"+
            "	var after=this.selectedTiles.toString();\r\n"+
            "		this.onUserSelected(this.activeTilePos,this.selectedTiles.toString());\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.setSelectedTiles=function(sel){\r\n"+
            "	this.selectedTiles.parseString(sel);\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.setActiveTilePos=function(pos){\r\n"+
            "  var oldActive=this.tilesByPosition[this.activeTilePos];\r\n"+
            "  this.activeTilePos=pos;\r\n"+
            "  if(oldActive!=null){\r\n"+
            "    var oldDiv=this.tileDivsPool[oldActive.pos];\r\n"+
            "    if(oldDiv!=null)\r\n"+
            "      this.applyStyle(oldDiv,oldActive);\r\n"+
            "  }\r\n"+
            "  var active=this.tilesById[this.activeTileId];\r\n"+
            "  if(active!=null){\r\n"+
            "    var activeDiv=this.tileDivsPool[active.pos];\r\n"+
            "    if(activeDiv!=null)\r\n"+
            "      this.applyStyle(activeDiv,active);\r\n"+
            "    this.activeTilePos=this.activeTileId;\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPanel.prototype.addTile=function(id,pos,name,style,sel){\r\n"+
            "	var tile={\r\n"+
            "			id:id,\r\n"+
            "			name:name,\r\n"+
            "			style:style,\r\n"+
            "			pos:1*pos,\r\n"+
            "			sel:sel===true\r\n"+
            "			};\r\n"+
            "	this.tilesByPosition[pos]=tile;\r\n"+
            "	this.tilesById[tile.id]=tile;\r\n"+
            "	if(between(pos,this.clipTop,this.clipBottom-1)){\r\n"+
            "	  var tileDiv=this.tileDivsPool[pos];\r\n"+
            "	  if(tileDiv!=null){\r\n"+
            "		  this.applyStyle(tileDiv,tile);\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "TilesPanel.prototype.setLocation=function(x,y,width,height){\r\n"+
            "    this.width=width;\r\n"+
            "	this.height=height;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPanel.prototype.setTilesCount=function(count){\r\n"+
            "    this.tilesCount=count;\r\n"+
            "    this.updateBounds();\r\n"+
            "    this.repaint();\r\n"+
            "}\r\n"+
            "	\r\n"+
            "\r\n"+
            "TilesPanel.prototype.setLocation=function(x,y,width,height){\r\n"+
            "    this.width=width;\r\n"+
            "	this.height=height;\r\n"+
            "    new Rect(x,y,width,height).writeToElement(this.containerElement);\r\n"+
            "	this.vscroll.setLocation(this.width-this.scrollSize,0,this.scrollSize,this.height);\r\n"+
            "    this.updateBounds();\r\n"+
            "    this.repaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "TilesPanel.prototype.updateBounds=function(){\r\n"+
            "	this.tilesPerRow=Math.max(1,fl((this.width-this.scrollSize) / (this.tilePadding+this.tileWidth)));\r\n"+
            "	this.totalHeight=cl(this.tilesCount / this.tilesPerRow) * (this.tilePadding+this.tileHeight);\r\n"+
            "	var vscrollVisible=this.totalHeight>this.height;\r\n"+
            "	if(vscrollVisible!=this.vscrollVisible){\r\n"+
            "		this.vscrollVisible=vscrollVisible;\r\n"+
            "		if(this.vscrollVisible){\r\n"+
            "          this.vscroll.setClipTop(0);\r\n"+
            "          this.containerElement.appendChild(this.vscrollElement);\r\n"+
            "          this.onScroll();\r\n"+
            "		}else{\r\n"+
            "          this.containerElement.removeChild(this.vscrollElement);\r\n"+
            "          this.tilesElement.style.top='0px';\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	var width=this.width;\r\n"+
            "	if(this.vscrollVisible)\r\n"+
            "  	  width-=this.scrollSize;\r\n"+
            "	var xInc=this.tilePadding+this.tileWidth;\r\n"+
            "	var yInc=this.tilePadding+this.tileHeight;\r\n"+
            "	this.columnsCount=Math.max(1,fl(width / xInc));\r\n"+
            "	this.rowsVisible=Math.max(1,1+cl(this.height / yInc));\r\n"+
            "    this.tilesVisible=this.columnsCount *this.rowsVisible;\r\n"+
            "	if(this.vscrollVisible){\r\n"+
            "      this.vscroll.setPaneLength(this.totalHeight);\r\n"+
            "      this.vscroll.setClipLength(this.height);\r\n"+
            "  	  width-=this.scrollSize;\r\n"+
            "	}else{\r\n"+
            "	  this.setClipzone(0,this.tilesVisible);\r\n"+
            "	}\r\n"+
            "	if(this.cssStyle!=null)\r\n"+
            "	  applyStyle(this.tilesElement,this.cssStyle);\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPanel.prototype.clearData=function(){\r\n"+
            "	this.tilesByPosition=[];\r\n"+
            "	this.tilesById={};\r\n"+
            "	for(var i in this.tileDivsPool)\r\n"+
            "		this.tilesElement.removeChild(this.tileDivsPool[i]);\r\n"+
            "	this.tileDivsPool={};\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.initTiles=function(multiselect){\r\n"+
            "	this.multiselect=multiselect;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPanel.prototype.addChildren=function(data){\r\n"+
            "	for(var i in data){\r\n"+
            "		var child=data[i];\r\n"+
            "		this.addTile(child.i,child.p,child.n,child.s);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPanel.prototype.setOptions=function(options){\r\n"+
            "	if(options.tileWidth!=null)\r\n"+
            "		this.tileWidth=fl(options.tileWidth);\r\n"+
            "	if(options.align!=null)\r\n"+
            "		this.align=options.align;\r\n"+
            "	if(options.tileWidth!=null)\r\n"+
            "		this.tileHeight=fl(options.tileHeight);\r\n"+
            "	if(options.tilePadding!=null)\r\n"+
            "		this.tilePadding=fl(options.tilePadding);\r\n"+
            "	if(options.cssStyle!=null)\r\n"+
            "		this.cssStyle=options.cssStyle;\r\n"+
            "	this.updateBounds();\r\n"+
            "}\r\n"+
            "\r\n"+
            "TilesPanel.prototype.repaint=function(){\r\n"+
            "	var minPadding=fl(this.tilePadding/2);\r\n"+
            "	var width=this.width;\r\n"+
            "	var top=0;\r\n"+
            "	if(this.vscrollVisible){\r\n"+
            "		width-=this.scrollSize;\r\n"+
            "	}\r\n"+
            "    this.tilesElement.style.width=toPx(this.width);\r\n"+
            "    this.tilesElement.style.height=toPx(this.height+this.tileHeight+this.tilePadding);\r\n"+
            "	var xInc=this.tilePadding+this.tileWidth;\r\n"+
            "	var yInc=this.tilePadding+this.tileHeight;\r\n"+
            "	var extra=fl((width - this.columnsCount * xInc)/this.columnsCount);\r\n"+
            "	xInc+=extra;\r\n"+
            "	var xPadding=fl(Math.max(minPadding,(this.tilePadding+extra)/2));\r\n"+
            "	var yPadding=this.tilePadding;//Math.min(xPadding,minPadding*2);\r\n"+
            "	var i=this.clipTop;\r\n"+
            "	\r\n"+
            "	var pos=this.clipTop;\r\n"+
            "	outer:for(var r=0;r<this.rowsVisible;r++){\r\n"+
            "	  var tilesOnThisRow = Math.min(this.columnsCount,this.tilesCount-i);\r\n"+
            "	  var leftAdjust=0;\r\n"+
            "	  var spacing=0;\r\n"+
            "	  if(this.align==='center')\r\n"+
            "	      leftAdjust=(this.width-(xPadding+tilesOnThisRow*xInc))/2;\r\n"+
            "	  else if(this.align==='right')\r\n"+
            "	      leftAdjust=this.width-(xPadding+tilesOnThisRow*xInc);\r\n"+
            "	  else if(this.align==='justify'){\r\n"+
            "	      leftAdjust=this.width-(xPadding+tilesOnThisRow*xInc);\r\n"+
            "	      leftAdjust/=tilesOnThisRow+1;\r\n"+
            "	      xInc+=leftAdjust;\r\n"+
            "	  }\r\n"+
            "		  \r\n"+
            "	  if(tilesOnThisRow<1)\r\n"+
            "		  break;\r\n"+
            "		  \r\n"+
            "	  for(var c=0;c<this.columnsCount;c++){\r\n"+
            "		  var pos=i++;\r\n"+
            "		  var x=leftAdjust+xPadding+c*xInc;\r\n"+
            "		  var y=yPadding+r*yInc;\r\n"+
            "		  var tileDiv=this.tileDivsPool[pos];\r\n"+
            "		  var tile=this.tilesByPosition[pos];\r\n"+
            "		  if(tile===undefined && tileDiv===undefined){ //no data and no div means no work");
          out.print(
            "\r\n"+
            "			  continue;\r\n"+
            "		  }else if(tileDiv==null){\r\n"+
            "    	      var tileDiv=nw('div','tiles_tile');\r\n"+
            "	          tileDiv.tile=tile;\r\n"+
            "              tileDiv.tabIndex=0;\r\n"+
            "	          tileDiv.onmousedown=this.onTileMouseClickFunc;\r\n"+
            "	          tileDiv.ondblclick=this.onTileMouseDblClickFunc;\r\n"+
            "              tileDiv.onkeydown=this.onTileKeyDownFunc;\r\n"+
            "    	      this.tileDivsPool[pos]=tileDiv;\r\n"+
            "	          this.tilesElement.appendChild(tileDiv);\r\n"+
            "		  } else if(tile==null){\r\n"+
            "			  this.tilesElement.removeChild(tileDiv);\r\n"+
            "			  continue;\r\n"+
            "		  }\r\n"+
            "		  tileDiv.style.left=toPx(x);\r\n"+
            "		  tileDiv.style.top=toPx(y);\r\n"+
            "    	  tileDiv.style.width=toPx(this.tileWidth);\r\n"+
            "    	  tileDiv.style.height=toPx(this.tileHeight);\r\n"+
            "		  this.applyStyle(tileDiv,tile);\r\n"+
            "		  x+=xInc;\r\n"+
            "	  }\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "TilesPanel.prototype.applyStyle=function(tileDiv,tile){\r\n"+
            "	tileDiv.innerHTML=tile.name;\r\n"+
            "	if(tile.pos===this.activeTilePos){\r\n"+
            "	  if(this.selectedTiles.isSelected(tile.pos))\r\n"+
            "	    tileDiv.className='tiles_tile tiles_selected tiles_active';\r\n"+
            "	  else\r\n"+
            "	    tileDiv.className='tiles_tile tiles_active';\r\n"+
            "	}else if(this.selectedTiles.isSelected(tile.pos))\r\n"+
            "	  tileDiv.className='tiles_tile tiles_selected';\r\n"+
            "	else\r\n"+
            "	  tileDiv.className='tiles_tile';\r\n"+
            "	applyStyle(tileDiv,tile.style);\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.onMouseWheel=function(e,delta){\r\n"+
            "	var den=e.shiftKey ? 1 : 10;\r\n"+
            "	if(e.altKey){\r\n"+
            "      if(this.hscrollVisible){\r\n"+
            "	    this.hscroll.goPage(-delta/den);\r\n"+
            "      }\r\n"+
            "    }else\r\n"+
            "	  this.vscroll.goPage(-delta/den);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "TilesPanel.prototype.onScroll=function(){\r\n"+
            "  var v=Math.round(this.vscroll.getClipTop())/(this.tileHeight+this.tilePadding);\r\n"+
            "  var top=fl(v);\r\n"+
            "  this.tilesElement.style.top=toPx(fl((fl(v+1)-v-1)*(this.tileHeight+this.tilePadding)));\r\n"+
            "  var clipTop=top*this.columnsCount;\r\n"+
            "  var clipBottom=clipTop+this.tilesVisible;\r\n"+
            "  this.setClipzone(clipTop,clipBottom);\r\n"+
            "  this.repaint();\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.setClipzone=function(clipTop,clipBottom){\r\n"+
            "\r\n"+
            "  if(isNaN(clipTop) || (this.clipTop===clipTop && this.clipBottom===clipBottom))//no change?\r\n"+
            "	  return;\r\n"+
            "  else if(clipTop>this.clipBottom || clipBottom < this.clipTop){//clip zone completely new (no overlap w/ existing) ?\r\n"+
            "	  for(var i in this.tileDivsPool){\r\n"+
            "		var tile=this.tileDivsPool[i];\r\n"+
            "		if(tile!=null)\r\n"+
            "		  this.tilesElement.removeChild(tile);\r\n"+
            "	  }\r\n"+
            "	  this.tileDivsPool={};\r\n"+
            "  }else{ // clip zone moved, but there is some overlap\r\n"+
            "	  for(var i=this.clipTop;i<clipTop;i++){\r\n"+
            "		var tile=this.tileDivsPool[i];\r\n"+
            "		if(tile!=null){\r\n"+
            "		  delete this.tileDivsPool[i];\r\n"+
            "		  this.tilesElement.removeChild(tile);\r\n"+
            "		}\r\n"+
            "	  }\r\n"+
            "	  for(var i=clipBottom;i<this.clipBottom;i++){\r\n"+
            "		var tile=this.tileDivsPool[i];\r\n"+
            "		if(tile!=null){\r\n"+
            "		  delete this.tileDivsPool[i];\r\n"+
            "		  this.tilesElement.removeChild(tile);\r\n"+
            "		}\r\n"+
            "	  }\r\n"+
            "  }\r\n"+
            "  this.clipTop=clipTop;\r\n"+
            "  this.clipBottom=clipBottom;\r\n"+
            "  if(this.onClipzoneChanged)\r\n"+
            "    this.onClipzoneChanged(this.clipTop,this.clipBottom);\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.showContextMenu=function(menu){\r\n"+
            "  this.createMenu(menu).show(this.contextMenuPoint);\r\n"+
            "}\r\n"+
            "TilesPanel.prototype.createMenu=function(menu){\r\n"+
            "   var that=this;\r\n"+
            "   var r=new Menu(getWindow(this.containerElement));\r\n"+
            "   r.createMenu(menu, function(e,id){that.onUserContextMenuItem(e,id);} );\r\n"+
            "   return r;\r\n"+
            "}");

	}
	
}