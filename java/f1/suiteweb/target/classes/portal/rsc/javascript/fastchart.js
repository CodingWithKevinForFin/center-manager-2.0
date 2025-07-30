

FastChart.prototype.fill=false;
FastChart.prototype.textPaddingY=10;
FastChart.prototype.textPaddingX=10;
FastChart.prototype.tickSize=2;
FastChart.prototype.tickPadding=2;
FastChart.prototype.textSize=14;
FastChart.prototype.dataXs=[];
FastChart.prototype.dataYs=[];


function FastChart(element){
	this.element=element;
	this.element.style.background='white';
	this.canvas=nw('canvas');
	this.canvas.width=500;
	this.canvas.height=256;
    this.context = this.canvas.getContext('2d');
    var that=this;
    this.canvas.onclick=function(e){that.onCanvasClicked(e)};
	this.element.appendChild(this.canvas);
}


FastChart.prototype.onCanvasClicked=function(e){
	if(this.onclick){
		var p=getMousePoint(e);
		this.onclick(p.x,p.y);
	}
}

FastChart.prototype.setLocation=function(x,y,width,height){
    //this.graphLeft=x+40;
    this.width=width;
    this.height=height;
	this.element.style.width=toPx(width);
	this.element.style.height=toPx(height);
	this.canvas.style.width=toPx(width);
	this.canvas.style.height=toPx(height);
	this.canvas.width=width;
	this.canvas.height=height;
    this.context.translate(.5,.5);
	this.init();
}

FastChart.prototype.setData=function(type,json,options){
	this.type=type;
    this.colors=[];
    this.labels=[];
    //log(options);
    if(options==null)
    	options={};
	if(options.yLblSfx)
		this.yLblSfx=options.yLblSfx;
	else 
		this.yLblSfx='';
	if(options.yMin)
		this.yMinOverride=options.yMin;
	else 
		this.yMinOverride=null;
	
	if(options.yMax)
		this.yMaxOverride=options.yMax;
	else 
		this.yMaxOverride=null;
	
	if(options.xMax)
		this.xMaxOverride=options.xMax;
	else 
		this.xMaxOverride=null;
	
	if(options.xMin)
		this.xMinOverride=options.xMin;
	else 
		this.xMinOverride=null;
	
	if(options.title)
		this.title=options.title;
	else 
		this.title='';
	
	if(options.keyPos)
		this.keyPos=options.keyPos;
	else 
		this.keyPos='';
	
	if(options.chartText)
		this.chartText=options.chartText;
	else
		this.chartText='';
	
	if(options.chartTextFont)
		this.chartTextFont=options.chartTextFont;
	else
		this.chartTextFont='9px arial';
	
	if(options.chartTextStyle)
		this.chartTextStyle=options.chartTextStyle;
	else
		this.chartTextStyle='#000000';
	
	this.xGridShow=options.xGridHide!='true';
	this.yGridShow=options.yGridHide!='true';
	this.xLblShow=options.xLblHide!='true';
	this.yLblShow=options.yLblHide!='true';
	this.borderShow=options.borderHide!='true';
	
	
	if(type=='SCATTER'){
	  this.dataXs=[];
	  this.dataYs=[];
	  var sIdx=0;
	  this.domainLabels=null;
	  for(var key in json){
	    var xy=json[key].xy;
	    this.colors[sIdx]=json[key].color;
	    this.labels[sIdx]=json[key].label;
	    var pos=0;
	    this.dataXs[sIdx]=[];
	    this.dataYs[sIdx]=[];
	    for(var i=0,pos=0;i<xy.length;pos++){
		  this.dataXs[sIdx][pos]=xy[i++];
		  this.dataYs[sIdx][pos]=xy[i++];
	    }
	    sIdx++;
	  }
	}else{
	  this.dataXs=[];
	  this.dataYs=[];
	  var sIdx=0;
	  var t=[];
	  this.domainLabels=json.domains;
	  for(var i=0;i<json.domains.length;i++)
		  t[i]=i;
	  for(var series in json.series){
	      this.colors[sIdx]=json.series[series].color;
	      this.labels[sIdx]=json.series[series].label;
		  this.dataXs[sIdx]=t;
		  this.dataYs[sIdx]=json.series[series].values;
	    sIdx++;
	  }
	  
	}
	this.init();
}

FastChart.prototype.init=function(){
  this.context.clearRect(0,0,this.width,this.height);

  var dataXs=this.dataXs;
  var dataYs=this.dataYs;
  var paddingPct=0;


  
  if(dataYs.length==0)
	  return;

  //calc
  this.minValY=dataYs[0][0];
  this.maxValY=dataYs[0][0];
  this.minValX=dataXs[0][0];
  this.maxValX=dataXs[0][0];
  if(this.type=='BAR_STACKED' || this.type=='AREA_STACKED'){
    var dataX=dataXs[0];
    for(var i in dataX){
      var stackup=0;
      var stackdn=0;
      var valx=dataXs[0][i];
      this.minValX=Math.min(this.minValX,valx);
      this.maxValX=Math.max(this.maxValX,valx);
      for(j in dataYs){
        var yval=dataYs[j][i];
        if(yval>=0)
          stackup+=yval;
        else 
          stackdn+=yval;
        this.minValY=Math.min(this.minValY,stackdn);
        this.maxValY=Math.max(this.maxValY,stackup);
      }
    }
  }else{
    for(j in dataYs){
      var dataX=dataXs[j];
      var dataY=dataYs[j];
      for(i in dataY){
        this.minValY=Math.min(this.minValY,dataY[i]);
        this.maxValY=Math.max(this.maxValY,dataY[i]);
        this.minValX=Math.min(this.minValX,dataX[i]);
        this.maxValX=Math.max(this.maxValX,dataX[i]);
      }
    }
  }
  if(this.yMinOverride!=null)
	  this.minValY=this.yMinOverride;
  if(this.yMaxOverride!=null)
	  this.maxValY=this.yMaxOverride;
  if(this.xMinOverride!=null)
	  this.minValX=this.xMinOverride;
  if(this.xMaxOverride!=null)
	  this.maxValX=this.xMaxOverride;


  this.graphTop=(this.textPaddingY+this.textSize)/2;
  if(this.title!='')
    this.graphTop=this.textSize+this.textPaddingY;
  else
    this.graphTop=(this.textPaddingY+this.textSize)/2;
  
  this.graphHeight=this.height-this.graphTop;
  
  
  if(this.keyPos=='below'){
	  this.textLineCount(this.chartText,1480,fl(this.textSize+this.textPaddingY/2));
	  if (this.chartLineCount > this.dataXs.length ){
		  this.graphHeight-=this.chartLineCount*(this.textSize+this.textPaddingY/2)+this.textPaddingY;
	  } else {
		  this.graphHeight-=this.dataXs.length*(this.textSize+this.textPaddingY/2)+this.textPaddingY;
	  }
		 
	  
  }

    
  if(this.xLblShow)
    this.graphHeight-=this.textPaddingY+this.textSize+this.tickSize+this.tickPadding;
  
  this.vscale=this.graphHeight / (this.maxValY-this.minValY);
  

  //labels (left)
  this.context.font=this.textSize+'px arial';
  var ylabelsCount=fl(this.graphHeight/(this.textPaddingY+this.textSize))-1;
  var ylabelSpacing=this.spacing(ylabelsCount,this.minValY,this.maxValY);
  //var ystart=fl((this.minValY+ylabelSpacing-1)/ylabelSpacing)*ylabelSpacing;
  var ystart=fl(fl(this.minValY)/ylabelSpacing)*ylabelSpacing;
  
  var minGridSpacingX=this.textPaddingX+Math.max(this.context.measureText(this.formatDomain(this.minValX)).width,this.context.measureText(this.formatDomain(this.maxValX)).width);
  
  var labelPadding=0;
  if(this.xLblShow)
    minGridSpacingX/2;
  
  if(this.yLblShow){
    for(var i=ystart;i<=this.maxValY-ylabelSpacing;i+=ylabelSpacing){
	  labelPadding=Math.max(this.context.measureText(this.formatRange(i)).width,labelPadding);
    }
    labelPadding=Math.max(this.context.measureText(this.formatRange(this.maxValY)).width,labelPadding);
    labelPadding=Math.max(this.context.measureText(this.formatRange(this.minValY)).width,labelPadding);
  }
  
  this.graphLeft=labelPadding+this.tickPadding+this.tickSize;
  this.graphWidth=this.width-this.graphLeft-minGridSpacingX/2;
  
  for(var i=ystart;i<=this.maxValY-ylabelSpacing;i+=ylabelSpacing){
	  if(i<this.minValY)
		  continue;
    this.drawLabelY(i);
  }
  
  this.drawLabelY(this.maxValY);
  
  if(this.type=='BAR' || this.type=='BAR_STACKED')
    this.hscale=this.graphWidth / (this.maxValX-this.minValX+1);
  else
    this.hscale=this.graphWidth / (this.maxValX-this.minValX);
    


  //labels (bottom)
  var xlabelsCount=fl(this.graphWidth/minGridSpacingX)-1;
  var xlabelSpacing=this.spacing(xlabelsCount,this.minValX,this.maxValX);
  var xstart=fl((this.minValX+xlabelSpacing-1)/xlabelSpacing)*xlabelSpacing;
  for(var i=xstart;i<=this.maxValX-xlabelSpacing;i+=xlabelSpacing)
    this.drawLabelX(i);
  //this.drawLabelX(this.minValX);
  this.drawLabelX(this.maxValX);

  this.context.save();
  this.context.rect(this.graphLeft,this.graphTop,this.graphWidth,this.graphHeight);
  this.context.clip();

  
  //data fill

  var zero=fl(this.graphTop+this.maxValY * this.vscale);
  
  if(this.type=='BAR'){
    this.context.lineWidth = 1;
    this.context.lineJoin="round";
    var barWidth=Math.max(fl(this.hscale/dataYs.length)-1,1);
    for(j in dataYs){
      var dataX=dataXs[j];
      var dataY=dataYs[j];
      this.context.fillStyle = this.colors[j];
      for(var i in dataX){
    	var xval=dataX[i];
    	var yval=dataY[i];
        var x=fl(this.graphLeft + (xval-this.minValX)*this.hscale)+j*barWidth;
        var y=fl(this.graphTop + this.graphHeight - ((yval-this.minValY)*this.vscale));
        this.context.fillRect(x+.5,y+.5,barWidth,zero-y);
      }
    }
  } else if(this.type=='BAR_STACKED'){
    this.context.lineWidth = 1;
    this.context.lineJoin="round";
    var barWidth=Math.max(fl(this.hscale)-1,1);
    var bottom=fl(this.graphHeight+this.graphTop);
    var dataX=dataXs[0];
    for(var i in dataX){
      var stackup=0;
      var stackdn=0;
      var valx=dataXs[0][i];
      for(j in dataYs){
        var yval=dataYs[j][i];
        this.context.fillStyle = this.colors[j];
        var x=fl(this.graphLeft + (valx-this.minValX)*this.hscale);
        var y=fl(this.graphTop + this.graphHeight - ((yval-this.minValY)*this.vscale));
        var stackOffset=yval>0 ? fl(stackup*this.vscale) : cl(stackdn*this.vscale);
        this.context.fillRect(x+.5,y+.5-stackOffset,barWidth,zero-y);
        if(yval>0)
          stackup+=yval;
        else
          stackdn+=yval;
      }
    }
  } else if(this.type=='AREA'){
    this.context.lineWidth = 1.5;
    this.context.lineJoin="round";
    for(j in dataYs){
      var dataX=dataXs[j];
      var dataY=dataYs[j];
      this.context.beginPath();
      this.context.moveTo(this.graphLeft,zero);//this.graphHeight);
      for(var i in dataY){
        if(dataY[i]==null || dataX[i]==null)
    	  continue;
        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale), fl(this.graphTop + this.graphHeight - ((dataY[i]-this.minValY)*this.vscale)));
      }
      this.context.lineTo(fl(this.graphLeft + this.graphWidth),zero);
      this.context.closePath();
      var gradient = this.context.createLinearGradient(0, 0, 0, this.graphHeight);
      gradient.addColorStop(0,  this.colors[j]);
      this.context.fillStyle=gradient;
      this.context.globalAlpha=.6;
      this.context.fill();
      this.context.globalAlpha=1;
      this.context.beginPath();
      for(var i in dataY){
        if(dataY[i]==null || dataX[i]==null)
    	  continue;
        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale), fl(this.graphTop + this.graphHeight - ((dataY[i]-this.minValY)*this.vscale) ));
      }
      this.context.strokeStyle = this.colors[j];
      this.context.stroke();
    }
  } else if(this.type=='AREA_STACKED'){
    for(j in dataYs){
      var dataX=dataXs[j];
      var dataY=dataYs[j];
      this.context.beginPath();
      var startPoint;
      for(var i=0;i<dataY.length;i++){
    	var under=0;
    	for(var t=0;t<j;t++)
    		under=dataYs[t][i];
    	var ypos=fl(this.graphTop + this.graphHeight - ((under+dataY[i]-this.minValY)*this.vscale));
    	if(i==0)
    	  startPoint=ypos;
        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale),ypos );
      }
      
      for(var i=dataY.length-1;i>=0;i--){
    	var under=0;
    	for(var t=0;t<j;t++)
    		under=dataYs[t][i];
    	var ypos=fl(this.graphTop + this.graphHeight - ((under-this.minValY)*this.vscale));
    	if(i==0)
    	  startPoint=ypos;
        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale),ypos );
      }
      this.context.strokeStyle = this.colors[j];
      this.context.closePath();
      var gradient = this.context.createLinearGradient(0, 0, 0, this.graphHeight);
      gradient.addColorStop(0,  this.colors[j]);
      this.context.fillStyle=gradient;
      this.context.globalAlpha=.7;
      this.context.fill();
    }
    
    this.context.lineWidth = 1.5;
    this.context.lineJoin="round";
    for(j in dataYs){
      var dataX=dataXs[j];
      var dataY=dataYs[j];
      this.context.beginPath();
      for(var i in dataY){
    	var under=0;
    	for(var t=0;t<j;t++)
    		under=dataYs[t][i];
        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale), fl(this.graphTop + this.graphHeight - ((under+dataY[i]-this.minValY)*this.vscale) ));
      }
      this.context.strokeStyle = this.colors[j];
      this.context.stroke();
    }
  }else if(this.type=='LINE' || this.type=='SCATTER'|| this.type=='AREA'){
    this.context.lineWidth = 1.5;
    this.context.lineJoin="round";
    for(j in dataYs){
      var dataX=dataXs[j];
      var dataY=dataYs[j];
      this.context.beginPath();
      for(var i in dataY){
        if(dataY[i]==null || dataX[i]==null)
    	  continue;
        this.context.lineTo(fl(this.graphLeft + (dataX[i]-this.minValX)*this.hscale), fl(this.graphTop + this.graphHeight - ((dataY[i]-this.minValY)*this.vscale) ));
      }
      this.context.strokeStyle = this.colors[j];
      this.context.stroke();
    }
  }

  this.context.restore();
  //outer rectangle
  if(this.borderShow){
    this.context.strokeStyle='#444444';
    this.context.lineWidth=1;
    this.context.strokeRect(this.graphLeft,this.graphTop,this.graphWidth,this.graphHeight);
  }
  
  //title
  if(this.title!=''){
    this.context.textAlign='center';
    this.context.textBaseline='middle';
    this.context.font="bold "+this.textSize+'px arial';
    this.context.fillText(this.title,fl(this.graphLeft+this.graphWidth / 2),fl(this.graphTop/2));
    this.context.font=this.textSize+'px arial';
  }
  
  //key
  if(this.keyPos=='below'){
//	  this.drawKey(this.graphLeft+this.textPaddingX,this.height-this.dataXs.length*(this.textSize+this.textPaddingY/2)-this.textPaddingY/2);
	  this.drawKey(this.graphLeft+this.textPaddingX,this.graphTop +this.graphHeight+this.textPaddingY+this.textSize+this.tickSize+this.tickPadding);
  }

}


FastChart.prototype.drawLabelY=function(val){
  var y=this.graphTop+this.graphHeight-(val-this.minValY) * this.vscale;
  
  if(this.yGridShow){
    this.context.beginPath();
    this.context.lineWidth=1;
    this.context.strokeStyle='#AAAAAA';
    this.context.moveTo(fl(this.graphLeft+this.graphWidth),fl(y));
    this.context.lineTo(fl(this.graphLeft),fl(y));
    this.context.closePath();
    this.context.stroke();
  }
  
  if(this.yLblShow){
    this.context.beginPath();
    this.context.strokeStyle='#444444';
    this.context.moveTo(fl(this.graphLeft),fl(y));
    this.context.lineTo(fl(this.graphLeft-this.tickSize),fl(y));
    this.context.closePath();
    this.context.stroke();
    this.context.textAlign='right';
    this.context.textBaseline='middle';
    this.context.fillText(this.formatRange(val),fl(this.graphLeft-this.tickSize-this.tickPadding),fl(y));
  }
}

FastChart.prototype.drawKey=function(x,y){
    this.context.lineWidth=1;
    this.context.textAlign='left';
    this.context.textBaseline='top';
    var maxl=0;
    for(var i in this.colors){
      this.context.fillStyle='#000000';
      var yloc=fl(y+i*(this.textSize+this.textPaddingY/2));
      var metrics = this.context.measureText(this.labels[i]);
      this.context.fillText(this.labels[i],fl(x+this.textSize*2.5),yloc);
      if( metrics.width > maxl)
    	  maxl = metrics.width;
      this.context.fillStyle=this.colors[i];
      this.context.fillRect(fl(x)+.5,yloc+.5,fl(this.textSize*2),fl(this.textSize));
    }
    
    this.context.fillStyle=this.chartTextStyle;
    this.context.font=this.chartTextFont;
    var tempx=fl(x+this.textSize*4)+maxl;
   this.wrapText(this.chartText,tempx,y,this.graphLeft+this.graphWidth-tempx,fl(this.textSize+this.textPaddingY/2));
  //this.context.fillText(text,fl(x+maxl*this.textSize),y);
   
}
FastChart.prototype.wrapText=function(text, x, y, maxWidth, lineHeight) {
    var words = text.split(' ');
    var line = '';

    for(var n = 0; n < words.length; n++) {
      var testLine = line + words[n] + ' ';
      var metrics = this.context.measureText(testLine);
      var testWidth = metrics.width;
      if (testWidth > maxWidth && n > 0) {
        this.context.fillText(line, x, y);
        line = words[n] + ' ';
        y += lineHeight;
      }
      else {
        line = testLine;
      }
    }
    this.context.fillText(line, x, y);
  }
FastChart.prototype.textLineCount=function(text,  maxWidth, lineHeight) {
    var words = text.split(' ');
    var line = '';
    var y = 1;

    for(var n = 0; n < words.length; n++) {
      var testLine = line + words[n] + ' ';
      var metrics = this.context.measureText(testLine);
      var testWidth = metrics.width;
      if (testWidth > maxWidth && n > 0) {
        line = words[n] + ' ';
        y++;
      }
      else {
        line = testLine;
      }
    }
    this.chartLineCount = y;
  }
FastChart.prototype.drawLabelX=function(val){
  var label=this.formatDomain(val);
  var x=this.graphLeft+(val-this.minValX) * this.hscale;
  if(this.type=='BAR' || this.type=='BAR_STACKED')
	  x+=this.hscale/2;
  
  if(this.xGridShow){
    this.context.beginPath();
    this.context.lineWidth=1;
    this.context.strokeStyle='#AAAAAA';
    this.context.moveTo(fl(x),fl(this.graphTop));
    this.context.lineTo(fl(x),fl(this.graphTop+this.graphHeight));
    this.context.closePath();
    this.context.stroke();
  }
  
  if(this.xLblShow){
    this.context.beginPath();
    this.context.strokeStyle='#444444';
    this.context.moveTo(fl(x),fl(this.graphTop+this.graphHeight));
    this.context.lineTo(fl(x),fl(this.graphTop+this.graphHeight+this.tickSize));
    this.context.closePath();
    this.context.stroke();
    this.context.textAlign='center';
    this.context.textBaseline='top';
    this.context.fillText(label,fl(x),fl(this.graphTop+this.graphHeight+this.tickSize+this.tickPadding));
  }
}


FastChart.prototype.formatRange=function(val){
	var diff=Math.abs(this.minValY-this.maxValY),precision=1;
	if(diff<100){
      if(diff==0)
    	precision=1;
      else{
	    while(diff<100){
		  precision*=10;
		  diff*=10;
	    }
      }
	}
	return (fl(val*precision)/precision)+this.yLblSfx;
}

FastChart.prototype.formatDomain=function(val){
  if(this.domainLabels==null)
    return val;
  else
    return this.domainLabels[val];
}


FastChart.prototype.spacing=function(maxLabels,minv,maxv){
	if(maxLabels<1)
		return 10000;//theoretically, its infinite
    var humanFactors=[1,2,5,10,20,25,50,100];
	var delta=maxv-minv;
	var spacing=delta/maxLabels;
	var digits=0;
	while(spacing>=100){
		digits++;
		spacing/=10;
	}
	for(var i in humanFactors)
		if(humanFactors[i]>=spacing){
			spacing=humanFactors[i];
			break;
		}
	for(;digits>0;digits--)
		spacing*=10;
	return spacing;
}
