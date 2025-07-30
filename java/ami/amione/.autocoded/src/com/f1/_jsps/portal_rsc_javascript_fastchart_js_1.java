package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_fastchart_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_fastchart_js_1() {
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
            "FastChart.prototype.fill=false;\r\n"+
            "FastChart.prototype.textPaddingY=10;\r\n"+
            "FastChart.prototype.textPaddingX=10;\r\n"+
            "FastChart.prototype.tickSize=2;\r\n"+
            "FastChart.prototype.tickPadding=2;\r\n"+
            "FastChart.prototype.textSize=14;\r\n"+
            "FastChart.prototype.dataXs=[];\r\n"+
            "FastChart.prototype.dataYs=[];\r\n"+
            "\r\n"+
            "\r\n"+
            "function FastChart(element){\r\n"+
            "	this.element=element;\r\n"+
            "	this.element.style.background='white';\r\n"+
            "	this.canvas=nw('canvas');\r\n"+
            "	this.canvas.width=500;\r\n"+
            "	this.canvas.height=256;\r\n"+
            "    this.context = this.canvas.getContext('2d');\r\n"+
            "    var that=this;\r\n"+
            "    this.canvas.onclick=function(e){that.onCanvasClicked(e)};\r\n"+
            "	this.element.appendChild(this.canvas);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastChart.prototype.onCanvasClicked=function(e){\r\n"+
            "	if(this.onclick){\r\n"+
            "		var p=getMousePoint(e);\r\n"+
            "		this.onclick(p.x,p.y);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastChart.prototype.setLocation=function(x,y,width,height){\r\n"+
            "    //this.graphLeft=x+40;\r\n"+
            "    this.width=width;\r\n"+
            "    this.height=height;\r\n"+
            "	this.element.style.width=toPx(width);\r\n"+
            "	this.element.style.height=toPx(height);\r\n"+
            "	this.canvas.style.width=toPx(width);\r\n"+
            "	this.canvas.style.height=toPx(height);\r\n"+
            "	this.canvas.width=width;\r\n"+
            "	this.canvas.height=height;\r\n"+
            "    this.context.translate(.5,.5);\r\n"+
            "	this.init();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastChart.prototype.setData=function(type,json,options){\r\n"+
            "	this.type=type;\r\n"+
            "    this.colors=[];\r\n"+
            "    this.labels=[];\r\n"+
            "    //log(options);\r\n"+
            "    if(options==null)\r\n"+
            "    	options={};\r\n"+
            "	if(options.yLblSfx)\r\n"+
            "		this.yLblSfx=options.yLblSfx;\r\n"+
            "	else \r\n"+
            "		this.yLblSfx='';\r\n"+
            "	if(options.yMin)\r\n"+
            "		this.yMinOverride=options.yMin;\r\n"+
            "	else \r\n"+
            "		this.yMinOverride=null;\r\n"+
            "	\r\n"+
            "	if(options.yMax)\r\n"+
            "		this.yMaxOverride=options.yMax;\r\n"+
            "	else \r\n"+
            "		this.yMaxOverride=null;\r\n"+
            "	\r\n"+
            "	if(options.xMax)\r\n"+
            "		this.xMaxOverride=options.xMax;\r\n"+
            "	else \r\n"+
            "		this.xMaxOverride=null;\r\n"+
            "	\r\n"+
            "	if(options.xMin)\r\n"+
            "		this.xMinOverride=options.xMin;\r\n"+
            "	else \r\n"+
            "		this.xMinOverride=null;\r\n"+
            "	\r\n"+
            "	if(options.title)\r\n"+
            "		this.title=options.title;\r\n"+
            "	else \r\n"+
            "		this.title='';\r\n"+
            "	\r\n"+
            "	if(options.keyPos)\r\n"+
            "		this.keyPos=options.keyPos;\r\n"+
            "	else \r\n"+
            "		this.keyPos='';\r\n"+
            "	\r\n"+
            "	if(options.chartText)\r\n"+
            "		this.chartText=options.chartText;\r\n"+
            "	else\r\n"+
            "		this.chartText='';\r\n"+
            "	\r\n"+
            "	if(options.chartTextFont)\r\n"+
            "		this.chartTextFont=options.chartTextFont;\r\n"+
            "	else\r\n"+
            "		this.chartTextFont='9px arial';\r\n"+
            "	\r\n"+
            "	if(options.chartTextStyle)\r\n"+
            "		this.chartTextStyle=options.chartTextStyle;\r\n"+
            "	else\r\n"+
            "		this.chartTextStyle='#000000';\r\n"+
            "	\r\n"+
            "	this.xGridShow=options.xGridHide!='true';\r\n"+
            "	this.yGridShow=options.yGridHide!='true';\r\n"+
            "	this.xLblShow=options.xLblHide!='true';\r\n"+
            "	this.yLblShow=options.yLblHide!='true';\r\n"+
            "	this.borderShow=options.borderHide!='true';\r\n"+
            "	\r\n"+
            "	\r\n"+
            "	if(type=='SCATTER'){\r\n"+
            "	  this.dataXs=[];\r\n"+
            "	  this.dataYs=[];\r\n"+
            "	  var sIdx=0;\r\n"+
            "	  this.domainLabels=null;\r\n"+
            "	  for(var key in json){\r\n"+
            "	    var xy=json[key].xy;\r\n"+
            "	    this.colors[sIdx]=json[key].color;\r\n"+
            "	    this.labels[sIdx]=json[key].label;\r\n"+
            "	    var pos=0;\r\n"+
            "	    this.dataXs[sIdx]=[];\r\n"+
            "	    this.dataYs[sIdx]=[];\r\n"+
            "	    for(var i=0,pos=0;i<xy.length;pos++){\r\n"+
            "		  this.dataXs[sIdx][pos]=xy[i++];\r\n"+
            "		  this.dataYs[sIdx][pos]=xy[i++];\r\n"+
            "	    }\r\n"+
            "	    sIdx++;\r\n"+
            "	  }\r\n"+
            "	}else{\r\n"+
            "	  this.dataXs=[];\r\n"+
            "	  this.dataYs=[];\r\n"+
            "	  var sIdx=0;\r\n"+
            "	  var t=[];\r\n"+
            "	  this.domainLabels=json.domains;\r\n"+
            "	  for(var i=0;i<json.domains.length;i++)\r\n"+
            "		  t[i]=i;\r\n"+
            "	  for(var series in json.series){\r\n"+
            "	      this.colors[sIdx]=json.series[series].color;\r\n"+
            "	      this.labels[sIdx]=json.series[series].label;\r\n"+
            "		  this.dataXs[sIdx]=t;\r\n"+
            "		  this.dataYs[sIdx]=json.series[series].values;\r\n"+
            "	    sIdx++;\r\n"+
            "	  }\r\n"+
            "	  \r\n"+
            "	}\r\n"+
            "	this.init();\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastChart.prototype.init=function(){\r\n"+
            "  this.context.clearRect(0,0,this.width,this.height);\r\n"+
            "\r\n"+
            "  var dataXs=this.dataXs;\r\n"+
            "  var dataYs=this.dataYs;\r\n"+
            "  var paddingPct=0;\r\n"+
            "\r\n"+
            "\r\n"+
            "  \r\n"+
            "  if(dataYs.length==0)\r\n"+
            "	  return;\r\n"+
            "\r\n"+
            "  //calc\r\n"+
            "  this.minValY=dataYs[0][0];\r\n"+
            "  this.maxValY=dataYs[0][0];\r\n"+
            "  this.minValX=dataXs[0][0];\r\n"+
            "  this.maxValX=dataXs[0][0];\r\n"+
            "  if(this.type=='BAR_STACKED' || this.type=='AREA_STACKED'){\r\n"+
            "    var dataX=dataXs[0];\r\n"+
            "    for(var i in dataX){\r\n"+
            "      var stackup=0;\r\n"+
            "      var stackdn=0;\r\n"+
            "      var valx=dataXs[0][i];\r\n"+
            "      this.minValX=Math.min(this.minValX,valx);\r\n"+
            "      this.maxValX=Math.max(this.maxValX,valx);\r\n"+
            "      for(j in dataYs){\r\n"+
            "        var yval=dataYs[j][i];\r\n"+
            "        if(yval>=0)\r\n"+
            "          stackup+=yval;\r\n"+
            "        else \r\n"+
            "          stackdn+=yval;\r\n"+
            "        this.minValY=Math.min(this.minValY,stackdn);\r\n"+
            "        this.maxValY=Math.max(this.maxValY,stackup);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  }else{\r\n"+
            "    for(j in dataYs){\r\n"+
            "      var dataX=dataXs[j];\r\n"+
            "      var dataY=dataYs[j];\r\n"+
            "      for(i in dataY){\r\n"+
            "        this.minValY=Math.min(this.minValY,dataY[i]);\r\n"+
            "        this.maxValY=Math.max(this.maxValY,dataY[i]);\r\n"+
            "        this.minValX=Math.min(this.minValX,dataX[i]);\r\n"+
            "        this.maxValX=Math.max(this.maxValX,dataX[i]);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  if(this.yMinOverride!=null)\r\n"+
            "	  this.minValY=this.yMinOverride;\r\n"+
            "  if(this.yMaxOverride!=null)\r\n"+
            "	  this.maxValY=this.yMaxOverride;\r\n"+
            "  if(this.xMinOverride!=null)\r\n"+
            "	  this.minValX=this.xMinOverride;\r\n"+
            "  if(this.xMaxOverride!=null)\r\n"+
            "	  this.maxValX=this.xMaxOverride;\r\n"+
            "\r\n"+
            "\r\n"+
            "  this.graphTop=(this.textPaddingY+this.textSize)/2;\r\n"+
            "  if(this.title!='')\r\n"+
            "    this.graphTop=this.textSize+this.textPaddingY;\r\n"+
            "  else\r\n"+
            "    this.graphTop=(this.textPaddingY+this.textSize)/2;\r\n"+
            "  \r\n"+
            "  this.graphHeight=this.height-this.graphTop;\r\n"+
            "  \r\n"+
            "  \r\n"+
            "  if(this.keyPos=='below'){\r\n"+
            "	  this.textLineCount(this.chartText,1480,fl(this.textSize+this.textPaddingY/2));\r\n"+
            "	  if (this.chartLineCount > this.dataXs.length ){\r\n"+
            "		  this.graphHeight-=this.chartLineCount*(this.textSize+this.textPaddingY/2)+this.textPaddingY;\r\n"+
            "	  } else {\r\n"+
            "		  this.graphHeight-=this.dataXs.length*(this.textSize+this.textPaddingY/2)+this.textPaddingY;\r\n"+
            "	  }\r\n"+
            "		 \r\n"+
            "	  \r\n"+
            "  }\r\n"+
            "\r\n"+
            "    \r\n"+
            "  if(this.xLblShow)\r\n"+
            "    this.graphHeight-=this.textPaddingY+this.textSize+this.tickSize+this.tickPadding;\r\n"+
            "  \r\n"+
            "  this.vscale=this.graphHeight / (this.maxValY-this.minValY);\r\n"+
            "  \r\n"+
            "\r\n"+
            "  //labels (left)\r\n"+
            "  this.context.font=this.textSize+'px arial';\r\n"+
            "  var ylabelsCount=fl(this.graphHeight/(this.textPaddingY+this.textSize))-1;\r\n"+
            "  var ylabelSpacing=this.spacing(ylabelsCount,this.minValY,this.maxValY);\r\n"+
            "  //var ystart=fl((this.minValY+ylabelSpacing-1)/ylabelSpacing)*ylabelSpacing;\r\n"+
            "  var ystart=fl(fl(this.minValY)/ylabelSpacing)*ylabelSpacing;\r\n"+
            "  \r\n"+
            "  var minGridSpacingX=this.textPaddingX+Math.max(this.context.measureText(this.formatDomain(this.minValX)).width,this.context.measureText(this.formatDomain(this.maxValX)).width);\r\n"+
            "  \r\n"+
            "  var labelPadding=0;\r\n"+
            "  if(this.xLblShow)\r\n"+
            "    minGridSpacingX/2;\r\n"+
            "  \r\n"+
            "  if(this.yLblShow){\r\n"+
            "    for(var i=ystart;i<=this.maxValY-ylabelSpacing;i+=ylabelSpacing){\r\n"+
            "	  labelPadding=Math.max(this.context.measureText(this.formatRange(i)).width,labelPadding);\r\n"+
            "    }\r\n"+
            "    labelPadding=Math.max(this.context.measureText(this.formatRange(this.maxValY)).width,labelPadding);\r\n"+
            "    labelPadding=Math.max(this.context.measureText(this.formatRange(this.minValY)).width,labelPadding);\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  this.graphLeft=labelPadding+this.tickPadding+this.tickSize;\r\n"+
            "  this.graphWidth=this.width-this.graphLeft-minGridSpacingX/2;\r\n"+
            "  \r\n"+
            "  for(var i=ystart;i<=this.maxValY-ylabelSpacing;i+=ylabelSpacing){\r\n"+
            "	  if(i<this.minValY)\r\n"+
            "		  continue;\r\n"+
            "    this.drawLabelY(i);\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  this.drawLabelY(this.maxValY);\r\n"+
            "  \r\n"+
            "  if(this.type=='BAR' || this.type=='BAR_STACKED')\r\n"+
            "    this.hscale=this.graphWidth / (this.maxValX-this.minValX+1);\r\n"+
            "  else\r\n"+
            "    this.hscale=this.graphWidth / (this.maxValX-this.minValX);\r\n"+
            "    \r\n"+
            "\r\n"+
            "\r\n"+
            "  //labels (bottom)\r\n"+
            "  var xlabelsCount=fl(this.graphWidth/minGridSpacingX)-1;\r\n"+
            "  var xlabelSpacing=this.spacing(xlabelsCount,this.minValX,this.maxValX);\r\n"+
            "  var xstart=fl((this.minValX+xlabelSpacing-1)/xlabelSpacing)*xlabelSpacing;\r\n"+
            "  for(var i=xstart;i<=this.maxValX-xlabelSpacing;i+=xlabelSpacing)\r\n"+
            "    this.drawLabelX(i);\r\n"+
            "  //this.drawLabelX(this.minValX);\r\n"+
            "  this.drawLabelX(this.maxValX);\r\n"+
            "\r\n"+
            "  this.context.save();\r\n"+
            "  this.context.rect(this.graphLeft,this.graphTop,this.graphWidth,this.graphHeight);\r\n"+
            "  this.context.clip();\r\n"+
            "\r\n"+
            "  \r\n"+
            "  //data fill\r\n"+
            "\r\n"+
            "  var zero=fl(this.graphTop+this.maxValY * this.vscale);\r\n"+
            "  \r\n"+
            "  if(this.type=='BAR'){\r\n"+
            "    this.context.lineWidth = 1;\r\n"+
            "    this.context.lineJoin=\"round\";\r\n"+
            "    var barWidth=Math.max(fl(this.hscale/dataYs.length)-1,1);\r\n"+
            "    for(j in dataYs){\r\n"+
            "      var dataX=dataXs[j];\r\n"+
            "      var dataY=dataYs[j];\r\n"+
            "      this.context.fillStyle = this.colors[j];\r\n"+
            "      for(var i in dataX){\r\n"+
            "    	var xval=dataX[i];\r\n"+
            "    	var yval=dataY[i];\r\n"+
            "        var x=fl(this.graphLeft + (xval-this.minValX)*this.hscale)+j*barWidth;\r\n"+
            "        var y=fl(this.graphTop + this.graphHeight - ((yval-this.minValY)*this.vscale));\r\n"+
            "        this.context.fillRect(x+.5,y+.5,barWidth,zero-y);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  } else if(this.type=='BAR_STACKED'){\r\n"+
            "    this.context.lineWidth = 1;\r\n"+
            "    this.context.lineJoin=\"round\";\r\n"+
            "    var barWidth=Math.max(fl(this.hscale)-1,1);\r\n"+
            "    var bottom=fl(this.graphHeight+this.graphTop);\r\n"+
            "    var dataX=dataXs[0];\r\n"+
            "    for(var i in dataX){\r\n"+
            "      var stackup=0;\r\n"+
            "      var stackdn=0;\r\n"+
            "      var valx=dataXs[0][i];\r\n"+
            "      for(j in dataYs){\r\n"+
            "        var yval=dataYs[j][i];\r\n"+
            "        this.context.fillStyle = this.colors[j];\r\n"+
            "        var x=fl(this.graphLeft + (valx-this.minValX)*this.hscale);\r\n"+
            "        var y=fl(this.graphTop + this.graphHeight - ((yval-this.minValY)*this.vscale));\r\n"+
            "        var stackOffset=yval>0 ? fl(stackup*this.vscale) : cl(stackdn*this.vscale);\r\n"+
            "        this.context.fillRect(x+.5,y+.5-stackOffset,barWidth,zero-y);\r\n"+
            "        if(yval>0)\r\n"+
            "          stackup+=yval;\r\n"+
            "        else\r\n"+
            "          stackdn+=yval;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  } else if(this.type=='AREA'){\r\n"+
            "    this.context.lineWidth = 1.5;\r\n"+
            "    this.context.lineJoin=\"round\";\r\n"+
            "    for(j in dataYs){\r\n"+
            "      var dataX=dataXs[j];\r\n"+
            "      var dataY=dataYs[j];\r\n"+
            "      this.context.beginPath();\r\n"+
            "      this.context.moveTo(this.graphLeft,zero);//this.graphHeight);\r\n"+
            "      for(var i in dataY){\r\n"+
            "        if(dataY[i]==null || dataX[i]==null)\r\n"+
            "    	  continue;\r\n"+
            "        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale), fl(this.graphTop + this.graphHeight - ((dataY[i]-this.minValY)*this.vscale)));\r\n"+
            "      }\r\n"+
            "      this.context.lineTo(fl(this.");
          out.print(
            "graphLeft + this.graphWidth),zero);\r\n"+
            "      this.context.closePath();\r\n"+
            "      var gradient = this.context.createLinearGradient(0, 0, 0, this.graphHeight);\r\n"+
            "      gradient.addColorStop(0,  this.colors[j]);\r\n"+
            "      this.context.fillStyle=gradient;\r\n"+
            "      this.context.globalAlpha=.6;\r\n"+
            "      this.context.fill();\r\n"+
            "      this.context.globalAlpha=1;\r\n"+
            "      this.context.beginPath();\r\n"+
            "      for(var i in dataY){\r\n"+
            "        if(dataY[i]==null || dataX[i]==null)\r\n"+
            "    	  continue;\r\n"+
            "        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale), fl(this.graphTop + this.graphHeight - ((dataY[i]-this.minValY)*this.vscale) ));\r\n"+
            "      }\r\n"+
            "      this.context.strokeStyle = this.colors[j];\r\n"+
            "      this.context.stroke();\r\n"+
            "    }\r\n"+
            "  } else if(this.type=='AREA_STACKED'){\r\n"+
            "    for(j in dataYs){\r\n"+
            "      var dataX=dataXs[j];\r\n"+
            "      var dataY=dataYs[j];\r\n"+
            "      this.context.beginPath();\r\n"+
            "      var startPoint;\r\n"+
            "      for(var i=0;i<dataY.length;i++){\r\n"+
            "    	var under=0;\r\n"+
            "    	for(var t=0;t<j;t++)\r\n"+
            "    		under=dataYs[t][i];\r\n"+
            "    	var ypos=fl(this.graphTop + this.graphHeight - ((under+dataY[i]-this.minValY)*this.vscale));\r\n"+
            "    	if(i==0)\r\n"+
            "    	  startPoint=ypos;\r\n"+
            "        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale),ypos );\r\n"+
            "      }\r\n"+
            "      \r\n"+
            "      for(var i=dataY.length-1;i>=0;i--){\r\n"+
            "    	var under=0;\r\n"+
            "    	for(var t=0;t<j;t++)\r\n"+
            "    		under=dataYs[t][i];\r\n"+
            "    	var ypos=fl(this.graphTop + this.graphHeight - ((under-this.minValY)*this.vscale));\r\n"+
            "    	if(i==0)\r\n"+
            "    	  startPoint=ypos;\r\n"+
            "        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale),ypos );\r\n"+
            "      }\r\n"+
            "      this.context.strokeStyle = this.colors[j];\r\n"+
            "      this.context.closePath();\r\n"+
            "      var gradient = this.context.createLinearGradient(0, 0, 0, this.graphHeight);\r\n"+
            "      gradient.addColorStop(0,  this.colors[j]);\r\n"+
            "      this.context.fillStyle=gradient;\r\n"+
            "      this.context.globalAlpha=.7;\r\n"+
            "      this.context.fill();\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "    this.context.lineWidth = 1.5;\r\n"+
            "    this.context.lineJoin=\"round\";\r\n"+
            "    for(j in dataYs){\r\n"+
            "      var dataX=dataXs[j];\r\n"+
            "      var dataY=dataYs[j];\r\n"+
            "      this.context.beginPath();\r\n"+
            "      for(var i in dataY){\r\n"+
            "    	var under=0;\r\n"+
            "    	for(var t=0;t<j;t++)\r\n"+
            "    		under=dataYs[t][i];\r\n"+
            "        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale), fl(this.graphTop + this.graphHeight - ((under+dataY[i]-this.minValY)*this.vscale) ));\r\n"+
            "      }\r\n"+
            "      this.context.strokeStyle = this.colors[j];\r\n"+
            "      this.context.stroke();\r\n"+
            "    }\r\n"+
            "  }else if(this.type=='LINE' || this.type=='SCATTER'|| this.type=='AREA'){\r\n"+
            "    this.context.lineWidth = 1.5;\r\n"+
            "    this.context.lineJoin=\"round\";\r\n"+
            "    for(j in dataYs){\r\n"+
            "      var dataX=dataXs[j];\r\n"+
            "      var dataY=dataYs[j];\r\n"+
            "      this.context.beginPath();\r\n"+
            "      for(var i in dataY){\r\n"+
            "        if(dataY[i]==null || dataX[i]==null)\r\n"+
            "    	  continue;\r\n"+
            "        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale), fl(this.graphTop + this.graphHeight - ((dataY[i]-this.minValY)*this.vscale) ));\r\n"+
            "      }\r\n"+
            "      this.context.strokeStyle = this.colors[j];\r\n"+
            "      this.context.stroke();\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "\r\n"+
            "  this.context.restore();\r\n"+
            "  //outer rectangle\r\n"+
            "  if(this.borderShow){\r\n"+
            "    this.context.strokeStyle='#444444';\r\n"+
            "    this.context.lineWidth=1;\r\n"+
            "    this.context.strokeRect(this.graphLeft,this.graphTop,this.graphWidth,this.graphHeight);\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  //title\r\n"+
            "  if(this.title!=''){\r\n"+
            "    this.context.textAlign='center';\r\n"+
            "    this.context.textBaseline='middle';\r\n"+
            "    this.context.font=\"bold \"+this.textSize+'px arial';\r\n"+
            "    this.context.fillText(this.title,fl(this.graphLeft+this.graphWidth / 2),fl(this.graphTop/2));\r\n"+
            "    this.context.font=this.textSize+'px arial';\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  //key\r\n"+
            "  if(this.keyPos=='below'){\r\n"+
            "//	  this.drawKey(this.graphLeft+this.textPaddingX,this.height-this.dataXs.length*(this.textSize+this.textPaddingY/2)-this.textPaddingY/2);\r\n"+
            "	  this.drawKey(this.graphLeft+this.textPaddingX,this.graphTop +this.graphHeight+this.textPaddingY+this.textSize+this.tickSize+this.tickPadding);\r\n"+
            "  }\r\n"+
            "\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastChart.prototype.drawLabelY=function(val){\r\n"+
            "  var y=this.graphTop+this.graphHeight-(val-this.minValY) * this.vscale;\r\n"+
            "  \r\n"+
            "  if(this.yGridShow){\r\n"+
            "    this.context.beginPath();\r\n"+
            "    this.context.lineWidth=1;\r\n"+
            "    this.context.strokeStyle='#AAAAAA';\r\n"+
            "    this.context.moveTo(fl(this.graphLeft+this.graphWidth),fl(y));\r\n"+
            "    this.context.lineTo(fl(this.graphLeft),fl(y));\r\n"+
            "    this.context.closePath();\r\n"+
            "    this.context.stroke();\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  if(this.yLblShow){\r\n"+
            "    this.context.beginPath();\r\n"+
            "    this.context.strokeStyle='#444444';\r\n"+
            "    this.context.moveTo(fl(this.graphLeft),fl(y));\r\n"+
            "    this.context.lineTo(fl(this.graphLeft-this.tickSize),fl(y));\r\n"+
            "    this.context.closePath();\r\n"+
            "    this.context.stroke();\r\n"+
            "    this.context.textAlign='right';\r\n"+
            "    this.context.textBaseline='middle';\r\n"+
            "    this.context.fillText(this.formatRange(val),fl(this.graphLeft-this.tickSize-this.tickPadding),fl(y));\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastChart.prototype.drawKey=function(x,y){\r\n"+
            "    this.context.lineWidth=1;\r\n"+
            "    this.context.textAlign='left';\r\n"+
            "    this.context.textBaseline='top';\r\n"+
            "    var maxl=0;\r\n"+
            "    for(var i in this.colors){\r\n"+
            "      this.context.fillStyle='#000000';\r\n"+
            "      var yloc=fl(y+i*(this.textSize+this.textPaddingY/2));\r\n"+
            "      var metrics = this.context.measureText(this.labels[i]);\r\n"+
            "      this.context.fillText(this.labels[i],fl(x+this.textSize*2.5),yloc);\r\n"+
            "      if( metrics.width > maxl)\r\n"+
            "    	  maxl = metrics.width;\r\n"+
            "      this.context.fillStyle=this.colors[i];\r\n"+
            "      this.context.fillRect(fl(x)+.5,yloc+.5,fl(this.textSize*2),fl(this.textSize));\r\n"+
            "    }\r\n"+
            "    \r\n"+
            "    this.context.fillStyle=this.chartTextStyle;\r\n"+
            "    this.context.font=this.chartTextFont;\r\n"+
            "    var tempx=fl(x+this.textSize*4)+maxl;\r\n"+
            "   this.wrapText(this.chartText,tempx,y,this.graphLeft+this.graphWidth-tempx,fl(this.textSize+this.textPaddingY/2));\r\n"+
            "  //this.context.fillText(text,fl(x+maxl*this.textSize),y);\r\n"+
            "   \r\n"+
            "}\r\n"+
            "FastChart.prototype.wrapText=function(text, x, y, maxWidth, lineHeight) {\r\n"+
            "    var words = text.split(' ');\r\n"+
            "    var line = '';\r\n"+
            "\r\n"+
            "    for(var n = 0; n < words.length; n++) {\r\n"+
            "      var testLine = line + words[n] + ' ';\r\n"+
            "      var metrics = this.context.measureText(testLine);\r\n"+
            "      var testWidth = metrics.width;\r\n"+
            "      if (testWidth > maxWidth && n > 0) {\r\n"+
            "        this.context.fillText(line, x, y);\r\n"+
            "        line = words[n] + ' ';\r\n"+
            "        y += lineHeight;\r\n"+
            "      }\r\n"+
            "      else {\r\n"+
            "        line = testLine;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "    this.context.fillText(line, x, y);\r\n"+
            "  }\r\n"+
            "FastChart.prototype.textLineCount=function(text,  maxWidth, lineHeight) {\r\n"+
            "    var words = text.split(' ');\r\n"+
            "    var line = '';\r\n"+
            "    var y = 1;\r\n"+
            "\r\n"+
            "    for(var n = 0; n < words.length; n++) {\r\n"+
            "      var testLine = line + words[n] + ' ';\r\n"+
            "      var metrics = this.context.measureText(testLine);\r\n"+
            "      var testWidth = metrics.width;\r\n"+
            "      if (testWidth > maxWidth && n > 0) {\r\n"+
            "        line = words[n] + ' ';\r\n"+
            "        y++;\r\n"+
            "      }\r\n"+
            "      else {\r\n"+
            "        line = testLine;\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "    this.chartLineCount = y;\r\n"+
            "  }\r\n"+
            "FastChart.prototype.drawLabelX=function(val){\r\n"+
            "  var label=this.formatDomain(val);\r\n"+
            "  var x=this.graphLeft+(val-this.minValX) * this.hscale;\r\n"+
            "  if(this.type=='BAR' || this.type=='BAR_STACKED')\r\n"+
            "	  x+=this.hscale/2;\r\n"+
            "  \r\n"+
            "  if(this.xGridShow){\r\n"+
            "    this.context.beginPath();\r\n"+
            "    this.context.lineWidth=1;\r\n"+
            "    this.context.strokeStyle='#AAAAAA';\r\n"+
            "    this.context.moveTo(fl(x),fl(this.graphTop));\r\n"+
            "    this.context.lineTo(fl(x),fl(this.graphTop+this.graphHeight));\r\n"+
            "    this.context.closePath();\r\n"+
            "    this.context.stroke();\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  if(this.xLblShow){\r\n"+
            "    this.context.beginPath();\r\n"+
            "    this.context.strokeStyle='#444444';\r\n"+
            "    this.context.moveTo(fl(x),fl(this.graphTop+this.graphHeight));\r\n"+
            "    this.context.lineTo(fl(x),fl(this.graphTop+this.graphHeight+this.tickSize));\r\n"+
            "    this.context.closePath();\r\n"+
            "    this.context.stroke();\r\n"+
            "    this.context.textAlign='center';\r\n"+
            "    this.context.textBaseline='top';\r\n"+
            "    this.context.fillText(label,fl(x),fl(this.graphTop+this.graphHeight+this.tickSize+this.tickPadding));\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastChart.prototype.formatRange=function(val){\r\n"+
            "	var diff=Math.abs(this.minValY-this.maxValY),precision=1;\r\n"+
            "	if(diff<100){\r\n"+
            "      if(diff==0)\r\n"+
            "    	precision=1;\r\n"+
            "      else{\r\n"+
            "	    while(diff<100){\r\n"+
            "		  precision*=10;\r\n"+
            "		  diff*=10;\r\n"+
            "	    }\r\n"+
            "      }\r\n"+
            "	}\r\n"+
            "	return (fl(val*precision)/precision)+this.yLblSfx;\r\n"+
            "}\r\n"+
            "\r\n"+
            "FastChart.prototype.formatDomain=function(val){\r\n"+
            "  if(this.domainLabels==null)\r\n"+
            "    return val;\r\n"+
            "  else\r\n"+
            "    return this.domainLabels[val];\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "FastChart.prototype.spacing=function(maxLabels,minv,maxv){\r\n"+
            "	if(maxLabels<1)\r\n"+
            "		return 10000;//theoretically, its infinite\r\n"+
            "    var humanFactors=[1,2,5,10,20,25,50,100];\r\n"+
            "	var delta=maxv-minv;\r\n"+
            "	var spacing=delta/maxLabels;\r\n"+
            "	var digits=0;\r\n"+
            "	while(spacing>=100){\r\n"+
            "		digits++;\r\n"+
            "		spacing/=10;\r\n"+
            "	}\r\n"+
            "	for(var i in humanFactors)\r\n"+
            "		if(humanFactors[i]>=spacing){\r\n"+
            "			spacing=humanFactors[i];\r\n"+
            "			break;\r\n"+
            "		}\r\n"+
            "	for(;digits>0;digits--)\r\n"+
            "		spacing*=10;\r\n"+
            "	return spacing;\r\n"+
            "}\r\n"+
            "");

	}
	
}