
function ColorPicker(standalone,origColor,element,allowNoColor){ 
	
  var that=this; 
  this.allowNoColor=allowNoColor;
  this.inputs={};
  this.radios={};
  this.values={};
  this.customColors=[];
  this.origColor=origColor;
  this.alphaEnabled=false;
  if(element==null)
	  alert("CAN NOT BE NULL!");
  this.standalone=standalone;
  if(this.standalone){
    this.document=element.document;
    this.element=nw('div');
    this.element.style.zIndex='9999';
    this.glass=nw('div','disable_glass_clear');
    this.glass.onclick=function(e){that.onGlassClicked(e);}
    this.glass.ondblclick=function(e){that.onOkPressed(e);}
	this.glass.style.cursor='url(./rsc/eyedropper.png) 1 24,auto';
  }else{
    this.document=getDocument(element);
    this.element=element;
  }    this.shadow=nw('div','shadow');
  
  this.canvas=nw('canvas');
  this.canvas.style.position='absolute';
  this.canvas.style.left='10px';
  this.canvas.style.top='10px';
  this.canvas.width=256;
  this.canvas.height=256;
  this.canvas.style.border='1px solid #666666';
  this.canvas.style.cursor='pointer';
  this.context = this.canvas.getContext('2d',{willReadFrequently: true});
  this.element.appendChild(this.canvas);
  this.element.style.border='1px solid black';
  this.element.style.background='white';
  this.element.style.width='460px';
  this.shadow.style.width='460px';
  var height=360;
  if(this.allowNoColor){
      this.noColorButton=nw('button');
      this.element.appendChild(this.noColorButton);
      //this.noColorButton.style.width='300px';
      this.noColorButton.style.position='absolute';
      this.noColorButton.innerHTML='No&nbsp;color';
      this.noColorButton.style.left='387px';
      this.noColorButton.style.width='60px';
      this.noColorButton.style.top='215px';
      this.noColorButton.style.margin='0px 5px 0px 0px';
      this.noColorButton.style.padding='0px 1px';
      this.noColorButton.onclick=function(){that.onNoColor();}
  }
  this.element.style.height=height+'px';
  this.shadow.style.height=height+'px';
  
  this.pixelBuf=this.context.createImageData(this.canvas.width,this.canvas.height);  this.pixelBufData=this.pixelBuf.data;
  
  this.sliderGrabber=nw('canvas');
  this.sliderGrabber.style.position='absolute';
  this.sliderGrabber.style.left='269px';
  this.sliderGrabber.style.top='10px';
  this.sliderGrabber.width=43;
  this.sliderGrabber.height=12;
  this.sliderGrabberContext=this.sliderGrabber.getContext('2d',{willReadFrequently: true});
  this.sliderGrabber.style.cursor='pointer';
  this.paintSliderGrabberContext();
  
  
  this.element.appendChild(this.sliderGrabber);
  
  this.sliderCanvas=nw('canvas');
  this.sliderCanvas.style.position='absolute';
  this.sliderCanvas.style.left='280px';
  this.sliderCanvas.style.top='10px';
  this.sliderCanvas.width=20;
  this.sliderCanvas.height=256;
  this.sliderCanvas.style.border='1px solid #666666';
  this.sliderCanvas.style.cursor='pointer';
  this.sliderCanvas.style.background='white';
  //this.sliderCanvas.onmouseup=function(e){that.onSliderClick(e);}
  this.sliderContext = this.sliderCanvas.getContext('2d',{willReadFrequently: true});
  this.element.appendChild(this.sliderCanvas);
  
  makeDraggable(this.sliderGrabber,this.sliderGrabber,true,false);
  this.sliderGrabber.clipDragging=function(e,rect){that.clipSlider(e,rect);};
  this.sliderGrabber.ondragging=function(tar,x,y,e){that.onSliderDragging(e,x,y);};
  
  makeDraggable(this.sliderCanvas,this.sliderGrabber,true,false);
  this.sliderCanvas.clipDragging=function(e,rect){that.clipSlider(e,rect);};
  this.sliderCanvas.ondragging=function(tar,x,y,e){that.onSliderDragging(e,x,y);};
  this.sliderCanvas.ondraggingStart=function(target,e){that.onSliderDragging(e);}
  this.sliderPosition=0;
  
  this.newRadio('hue','Hue',315,65,360, true);
  this.newRadio('sat','Sat.',315, 85,100,true);
  this.newRadio('bri','Bright',315,105,100,true);
  
  this.newRadio('red','Red',315,130,255,true);
  this.newRadio('green','Green',315,150,255,true);
  this.newRadio('blue','Blue',315,170,255,true);
  if(this.origColor!=null && this.origColor.length==9){
	    this.setAlphaEnabled();
  }
  
  this.backgroundDiv=nwDiv('',325,10,118,50);
  this.backgroundDiv.className="colorpicker_samplebackground";
  this.colorSampleDiv=nwDiv('',320,10,64,50);
  this.oldColorSampleDiv=nwDiv('',384,10,64,50);
  this.oldColorSampleDiv.style.cursor='pointer';
  this.element.appendChild(this.backgroundDiv);
  this.element.appendChild(this.colorSampleDiv);
  this.element.appendChild(this.oldColorSampleDiv);
  this.oldColorSampleDiv.style.border='1px solid #666666';
  this.colorSampleDiv.style.border='1px solid #666666';
  this.colorSampleDiv.style.borderWidth='1px 0px 1px 1px';
  this.oldColorSampleDiv.style.borderWidth='1px 1px 1px 0px';
  this.oldColorSampleDiv.onclick=function(){
	  if(that.origColor){
	    that.color=that.origColor;that.onValueChanged('O',true);that.repaint();
	  }else{
	      that.color=that.origColor;
	       that.onValueChanged('NONE',true);that.repaint();
	      that.values.color=null;
		  that.hexColor.value='no color';
	  }
  };
  
  this.hexColor=nw('input','slider_input');
  this.hexColor.style.width=toPx(70);
  this.hexColor.style.height=toPx(19);
  //this.canvas.onmouseup=function(e){that.onColorClick(e);};
  makeDraggable(this.canvas,null,true,false);
  this.canvas.ondragging=function(target,x,y,e){that.onColorClick(e);};
  this.canvas.ondraggingStart=function(target,e){that.onColorClick(e);}
  
  this.sliderCanvas.clipDragging=function(e,rect){that.clipSlider(e,rect);};
  var hexColorDiv=nwDiv('',314,215,62,20);
  this.hexColor.onchange=function(){that.onValueChanged('HEX',true);if(that.hexColor.value!='no color')that.repaint();};
  this.paletteDiv=nwDiv('colorchooser_pallete',10,273,48*9+6,4*9+3);
  var t={};
  for(var i=0;i<13;i++){
    for(var j=0;j<3;j++){
      for(var k=0;k<3;k++){
	    var p=nwDiv('colorchooser_pallete_item',5+i*33+k*10,4+j*10,9,9);
	    if(i==12){
  	      t.hue=0;
	      t.sat=0;
	      t.bri=100-(j*3+k)*100/8;
	    }else{
  	      t.hue=360/12 * i;
	      t.bri=100-j*25;
	      t.sat=100-k*25;
	    }
	    this.hsb2rgb(t,t);
	    var c=toColor(t.red,t.green,t.blue);
	    var c2=toColor(Math.max(50,t.red*.8),Math.max(50,t.green*.8),Math.max(50,t.blue*.8));
	    p.style.background=c;
	    p.style.borderColor=c2;
	    p.color=c;
	    p.onclick=function(e){that.onColorPaletteClicked(e)};
        p.ondblclick=function(){that.onOkPressed();};
	    this.paletteDiv.appendChild(p);
      }
    }
  }
  this.customColorDiv=nwDiv('colorchooser_pallete',10,315,48*9+6,4*9+2);
  hexColorDiv.appendChild(this.hexColor);
  this.element.appendChild(hexColorDiv);
  var buttonDiv=nwDiv('',314,238,150,30);
  this.okButton=nw('button');
  this.okButton.style.width='70px';
  this.okButton.style.fontWeight='bold';
  this.okButton.innerHTML='OK';
  this.okButton.style.margin='0px 3px 0px 0px';
  buttonDiv.appendChild(this.okButton);
  this.clButton=nw('button');
  this.clButton.innerHTML='Cancel';
  this.clButton.style.width='60px';
  buttonDiv.appendChild(this.clButton);
  this.element.appendChild(buttonDiv);
  this.element.appendChild(this.paletteDiv);
  this.element.appendChild(this.customColorDiv);
  
  this.okButton.onclick=function(){that.onOkPressed();}
  this.canvas.ondblclick=function(){that.onOkPressed();};
  this.sliderCanvas.ondblclick=function(){that.onOkPressed();};
  this.clButton.onclick=function(){that.onCancel();}
  
  this.onValueChanged('O',false);
  this.onSliderModeChanged('hue');
  this.repaint();
  //log(this.origColor);
  //log(!this.origColor);
  this.lastClickTime=0;
  if(this.allowNoColor && !this.origColor)
    this.hexColor.value='no color';
} 

ColorPicker.prototype.onGlassClicked=function(e){
	var now=Date.now();
	var dur=now-this.lastClickTime;
	this.lastClickTime=now;
	if(dur<200){
		this.onOkPressed();
		return;
	}
	var point=getMousePoint(e);
	var that=this;
	if(dur<2000){
        var c=this.imageContext.getImageData(point.x,point.y,1,1).data;
        var r=c[0],g=c[1],b=c[2];
        var hex = "#" + (0x1000000 + (r << 16) + (g << 8) + b).toString(16).slice(1);
        this.setHex(hex);
		return;
	}
	html2canvas(document.body, {
        onrendered: function(canvas) {
            that.imageContext=canvas.getContext('2d',{willReadFrequently: true});
            var c=that.imageContext.getImageData(point.x,point.y,1,1).data;
            var r=c[0],g=c[1],b=c[2];
            var hex = "#" + (0x1000000 + (r << 16) + (g << 8) + b).toString(16).slice(1);
            that.setHex(hex);
        }
    });
}
ColorPicker.prototype.onOkPressed=function(){
	if(this.onOk){
	  this.onOk();
	  this.onOk=null;
	}
}
ColorPicker.prototype.setHex=function(color){
	if(color!=null){
        this.hexColor.value=color;
        this.onValueChanged('HEX',true);
        this.repaint();
	}
}
ColorPicker.prototype.handleKeydown=function(e){
	if(e.key === "Escape" && e.shiftKey == false && e.ctrlKey == false && e.altKey == false){
		this.onCancel();
	}
}

ColorPicker.prototype.addColorChoice=function(color){
	var i=this.customColors.length;
	var x=i%39;
	var y=(i-x)/39;
	if(y>=3)
		return;
	this.customColors[i]=color;
	var p=nwDiv('colorchooser_pallete_item',4+x*11,2+y*11,10,10);
	p.style.background=color;
	var that=this;
	p.color=color;
	p.onclick=function(e){that.onColorPaletteClicked(e)};
    p.ondblclick=function(){that.onOkPressed();};
	this.customColorDiv.appendChild(p);
}
ColorPicker.prototype.show=function(x,y){
	this.element.style.top=toPx(y);
	this.element.style.left=toPx(x);
	if(this.standalone){
	  this.document.body.appendChild(this.glass);
	}
	this.document.body.appendChild(this.element);
	ensureInWindow(this.element);
}
ColorPicker.prototype.hide=function(){
	if(this.standalone){
	  this.document.body.removeChild(this.glass);
	}
	this.document.body.removeChild(this.element);
}
ColorPicker.prototype.onColorPaletteClicked=function(e){
  var target=getMouseTarget(e);
  this.hexColor.value=target.color;
  this.onValueChanged('HEX',true);
  this.repaint();
}

ColorPicker.prototype.clipSlider=function(e,rect){
	rect.setTop(between(rect.getTop(),-4,253));
}

ColorPicker.prototype.paintSliderGrabberContext=function(){
	var c=this.sliderGrabberContext;
	c.beginPath();
	c.translate(.5,.5);
	c.moveTo(2,0);
	c.lineTo(10,5);
	c.lineTo(2,10);
	c.lineTo(2,0);
	c.strokeStyle='#666666';
	if(this.sliderMode=='hue'){
	  var t={};
	  t.hue=this.values.hue;
	  t.sat=100;
	  t.bri=100;
	  this.hsb2rgb(t,t);
	  c.fillStyle=toColor(t.red,t.green,t.blue);
	}else
	  c.fillStyle=this.values.color;
	c.fill();
	c.stroke();
	c.moveTo(41,0);
	c.lineTo(33,5);
	c.lineTo(41,10);
	c.lineTo(41,0);
	c.fill();
	c.stroke();
	c.translate(-.5,-.5);
}

ColorPicker.prototype.newRadio=function(id,label,x,y,size,includeRadio){
	var that=this;
	if(includeRadio){
	  var radio=nw('input',0,0,13,13);
	  radio.type='radio';
	  radio.name='colorchooser_radio';
	  radio.onclick=function(){that.onSliderModeChanged(id)};
	  radio.style.margin="2px 0px";
	}
	var input=nwDiv('',includeRadio ? 15 : 30,0,90,20);
	var slider=new Slider(input,0,0,size,90,0,true);
	slider.onValueChanged=function(){that.onValueChanged(id,true);that.repaint()};
	this.inputs[id]=slider;
	if(includeRadio){
	  this.radios[id]=radio;
	}
	var lab=nwDiv('colorchooser_label',includeRadio ? 106 : 121,0,0,0);
	lab.innerHTML=label;
	
	var div=nwDiv('',x-(includeRadio ? 3 : 18),y,100,15);
	if(includeRadio)
	  div.appendChild(radio);
	div.appendChild(input);
	div.appendChild(lab);
	this.element.appendChild(div);
}

ColorPicker.prototype.getColor=function(){
	if(this.values.color=='no color')
		return null ;
	return this.values.color;
}
ColorPicker.prototype.getOrigColor=function(){
	return this.origColor;
}
	
ColorPicker.prototype.onSliderModeChanged=function(sm){
	this.sliderMode=sm;
	if(this.radios[sm]!=null)
	  this.radios[sm].checked='on';
	if(sm=='red'){this.xdim='blue',this.ydim='green';this.xdimScale=256;this.ydimScale=256;this.sliderScale=256; };
	if(sm=='green'){this.xdim='blue',this.ydim='red';this.xdimScale=256;this.ydimScale=256;this.sliderScale=256; };
	if(sm=='blue'){this.xdim='red',this.ydim='green';this.xdimScale=256;this.ydimScale=256;this.sliderScale=256; };
	
	if(sm=='hue'){this.xdim='sat',this.ydim='bri';this.xdimScale=100;this.ydimScale=100;this.sliderScale=360; };
	if(sm=='sat'){this.xdim='hue',this.ydim='bri';this.xdimScale=360;this.ydimScale=100;this.sliderScale=100; };
	if(sm=='bri'){this.xdim='hue',this.ydim='sat';this.xdimScale=360;this.ydimScale=100;this.sliderScale=100; };
	this.setSliderValue(this.values[sm]*255/this.sliderScale,false);
	this.repaint();
}

ColorPicker.prototype.onValueChanged=function(id,fire){
	var vals=this.values;
	var updateHSB=true;
	if(id=='HEX'){
		if(this.hexColor.value && this.hexColor.value!='no color'){
		  var colors=parseColor(this.hexColor.value);
		  vals.red=colors[0];
		  vals.green=colors[1];
		  vals.blue=colors[2];
	      if(this.getAlphaEnabled())
		   vals.alpha=colors[3];
		  if(vals.alpha==null)
			  vals.alpha=255;
		}else{
			this.hexColor.value='no color';
			vals.color=null;
	        if(this.onColorChanged!=null && fire===true)
		      this.onColorChanged(this,null);
	        return;
		}
	} else if(id=='O'){
		if(this.origColor){
		  var colors=parseColor(this.origColor);
		  vals.red=colors[0];
		  vals.green=colors[1];
		  vals.blue=colors[2];
		  vals.alpha=colors[3];
		  if(vals.alpha==null)
			  vals.alpha=255;
		}else{
		  vals.red=0;
		  vals.green=0;
		  vals.blue=0;
		  vals.alpha=255;
		}
	}else if(id=='red'){
	  vals.red=this.inputs.red.getValue();
	}else if(id=='green'){
	  vals.green=this.inputs.green.getValue();
	}else if(id=='blue'){
	  vals.blue=this.inputs.blue.getValue();
	}else if(id=='bri'){
	  vals.bri=this.inputs.bri.getValue();
	  updateHSB=false;
	}else if(id=='sat'){
	  vals.sat=this.inputs.sat.getValue();
	  updateHSB=false;
	}else if(id=='hue'){
	  vals.hue=this.inputs.hue.getValue();
	  updateHSB=false;
	}else if(id=='alpha'){
	  vals.alpha=this.inputs.alpha.getValue();
	}else if(id=='NONE'){
	  this.values.color=null;
	  if(this.onColorChanged!=null && fire===true)
		  this.onColorChanged(this,null);
	  return;
	}
	if(updateHSB){
      var max=Math.max(vals.red,vals.green,vals.blue);
      var min=Math.min(vals.red,vals.green,vals.blue);
      var c=max-min;
      vals.sat=max==0 ? 100 : 100*c/max;
      vals.bri=max/2.55;
      if(max==min){
    	  vals.hue=0;
      }else if(max==vals.red){
    	  vals.hue=60*((360+(vals.green-vals.blue)/c)%6);
      }else if(max==vals.green){
    	  vals.hue=60*((vals.blue-vals.red)/c+2);
      }else{
    	  vals.hue=60*((vals.red-vals.green)/c+4);
      }
	}else{
	  this.hsb2rgb(vals,vals);
	}
	
	vals.color=toColor(vals.red,vals.green,vals.blue);
	if(this.getAlphaEnabled() && vals.alpha!=255)
	  vals.color+=toColorPart(vals.alpha);
	if(this.onColorChanged!=null && fire===true){
		   this.onColorChanged(this,vals.color);
	}
	
	this.inputs.red.setValue(vals.red,true,false);
	this.inputs.green.setValue(vals.green,true,false);
	this.inputs.blue.setValue(vals.blue,true,false);
    if(this.alphaEnabled)
	  this.inputs.alpha.setValue(vals.alpha,true,false);
	
	this.inputs.sat.setValue(vals.sat,true,false);
	this.inputs.bri.setValue(vals.bri,true,false);
	this.inputs.hue.setValue(vals.hue,true,false);
	if(fire){
	  var loc=between(255-this.values[this.sliderMode]*255/this.sliderScale,0,255);
	  this.sliderPosition=loc;
	}
	
	
	
}

ColorPicker.prototype.hsb2rgb=function(hsb,rgb,logg){
      var bri=hsb.bri/100,hue=(360-hsb.hue)/360*6,sat=hsb.sat/100;
      if(hue==0)
    	  hue=.01;
      var i=fl(hue);
      var f=hue-i;
      if((i & 0x01)==0)
    	  f=1-f;
      var m=between(fl(255*(bri*(1-sat))),0,255);
      var n=between(fl(255*(bri*(1-sat*f))),0,255);
      var v=between(fl(255*(bri)),0,255);
      switch(i){
        case 6:
        case 0: rgb.red=v; rgb.blue=n; rgb.green=m;  break;
        case 1: rgb.red=n; rgb.blue=v; rgb.green=m;  break;
        case 2: rgb.red=m; rgb.blue=v; rgb.green=n;  break;
        case 3: rgb.red=m; rgb.blue=n; rgb.green=v;  break;
        case 4: rgb.red=n; rgb.blue=m; rgb.green=v;  break;
        case 5: rgb.red=v; rgb.blue=m; rgb.green=n;  break;
      }
}

ColorPicker.prototype.onSliderDragging=function(point,x,y){
	var loc=255-(point.y-new Rect().readFromElement(this.sliderCanvas).top);
	this.setSliderValue(loc,true);
}
ColorPicker.prototype.setSliderValue=function(loc,fire){
	loc=between(255-loc,0,255);
	this.sliderPosition=loc;
	if(fire){
	  this.inputs[this.sliderMode].setValue((255-loc)*this.sliderScale/256);
	  this.onValueChanged(this.sliderMode,true);
	  this.repaint();
	}
}
ColorPicker.prototype.onSliderClick=function(point){
	var loc=255-fl(getMouseLayerPoint(point).y-1);
}


ColorPicker.prototype.onColorClick=function(e){
    //log('-----');
    //log(this.values);
    var point=getMousePoint(e);
    var rect=new Rect();
    rect.readFromElement(this.canvas);
	var x=between(fl(point.x-rect.left),0,255);
	var y=255-between(fl(point.y-rect.top),0,255);
	var sm=this.sliderMode,vx=this.xdim,vy=this.ydim,sx=this.xdimScale,sy=this.ydimScale;
	this.inputs[vx].setValue(x*sx/256);
	this.onValueChanged(vx,false);
	this.inputs[vy].setValue(y*sy/256);
	this.onValueChanged(vy,true);
    //log(this.values);
	this.repaint();
    //log(this.values);
}

ColorPicker.prototype.onMouseMove=function(point){
}
ColorPicker.prototype.findNodeAt=function(x,y,data){
}


ColorPicker.prototype.drawSlider=function(y,r,g,b){
  this.sliderContext.strokeStyle=toColor(r,g,b);
  this.sliderContext.beginPath();
  this.sliderContext.moveTo(0,255-y+.5);
  this.sliderContext.lineTo(20,255-y+.5);
  this.sliderContext.stroke();
}

ColorPicker.prototype.setAlphaEnabled=function(){
    if(this.alphaEnabled)
      return;
    this.alphaEnabled=true;
	if(this.alphaEnabled){
      this.newRadio('alpha','Opacity',315,190,255,false);
      this.inputs['alpha'].setValue(255,true,false);
	}
}

ColorPicker.prototype.getAlphaEnabled=function(){
    return (this.alphaEnabled);
}


ColorPicker.prototype.repaint=function(){
	var sm=this.sliderMode;
	this.sliderContext.clearRect(0,0,this.sliderContext.width,this.sliderContext.height);
    this.context.getImageData(0,0,1,1);
	if(this.hexColor.value!='no color'){
	if(sm=='red'){
		for(var i=0;i<256;i++)
			this.drawSlider(i,i,this.values.green,this.values.blue);
		for(var x=0;x<256;x++)
		  for(var y=0;y<256;y++)
			this.setPixel(x,y,this.values.red,255-y,x);
	}else if(sm=='green'){
		for(var i=0;i<256;i++)
			this.drawSlider(i,this.values.red,i,this.values.blue);
		for(var x=0;x<256;x++)
		  for(var y=0;y<256;y++)
			this.setPixel(x,y,255-y,this.values.green,x);
	}else if(sm=='blue'){
		for(var i=0;i<256;i++)
			this.drawSlider(i,this.values.red,this.values.green,i);
		for(var x=0;x<256;x++)
		  for(var y=0;y<256;y++)
			this.setPixel(x,y,x,255-y,this.values.blue);
	}else if(sm=='hue'){
		var rgb={};
		var hsb={hue:this.values.hue, sat:this.values.sat, bri:this.values.bri};
		for(var i=0;i<256;i++){
			hsb.hue=i*360/256;
			hsb.sat=100;
			hsb.bri=100;
			this.hsb2rgb(hsb,rgb);
			this.drawSlider(i,rgb.red,rgb.green,rgb.blue);
		}
		for(var x=0;x<256;x++)
		  for(var y=0;y<256;y++){
			hsb.hue=this.values.hue;
			hsb.sat=x*100/256;
			hsb.bri=(255-y)*100/256;
			this.hsb2rgb(hsb,rgb);
			this.setPixel(x,y,rgb.red,rgb.green,rgb.blue);
		  }
	}else if(sm=='sat'){
		var rgb={};
		var hsb={hue:this.values.hue, sat:this.values.sat, bri:this.values.bri};
		for(var i=0;i<256;i++){
			hsb.sat=i*100/256;
			this.hsb2rgb(hsb,rgb);
			this.drawSlider(i,rgb.red,rgb.green,rgb.blue);
		}
		for(var x=0;x<256;x++)
		  for(var y=0;y<256;y++){
			hsb.sat=this.values.sat;
			hsb.hue=x*360/256;
			hsb.bri=(255-y)*100/256;
			this.hsb2rgb(hsb,rgb);
			this.setPixel(x,y,rgb.red,rgb.green,rgb.blue);
		  }
	}else if(sm=='bri'){
		var rgb={};
		var hsb={hue:this.values.hue, sat:this.values.sat, bri:this.values.bri};
		for(var i=0;i<256;i++){
			hsb.bri=i*100/256;
			this.hsb2rgb(hsb,rgb);
			this.drawSlider(i,rgb.red,rgb.green,rgb.blue);
		}
		for(var x=0;x<256;x++)
		  for(var y=0;y<256;y++){
			hsb.bri=this.values.bri;
			hsb.sat=(255-y)*100/256;
			hsb.hue=x*360/256;
			this.hsb2rgb(hsb,rgb);
			this.setPixel(x,y,rgb.red,rgb.green,rgb.blue);
		  }
	}
	}
	this.sliderGrabber.style.top=toPx(this.sliderPosition-4+10);
	this.hexColor.value=this.values.color;
	this.colorSampleDiv.style.background=this.values.color;
	if(!this.origColor){
	  this.oldColorSampleDiv.style.background='white';
	  this.oldColorSampleDiv.style.color='#AAAAAA';
	  this.oldColorSampleDiv.style.textAlign='center';
	  this.oldColorSampleDiv.innerHTML="<P>no color";
	}else
	  this.oldColorSampleDiv.style.background=this.origColor;
    this.context.putImageData(this.pixelBuf,0,0);
    this.context.beginPath();
    this.context.fillStyle=this.values.color;
    if((this.values.red+this.values.blue+this.values.green)/3<120 ){
    	this.context.strokeStyle='white';
    }else{
    	this.context.strokeStyle='black';
    }
    this.context.arc(this.values[this.xdim]*256/this.xdimScale,255-this.values[this.ydim]*256/this.ydimScale,5,0,2*Math.PI,false);
    this.context.fill();
    this.context.stroke();
    this.paintSliderGrabberContext();
    //log(this.values);
    //log(this.values);
}

ColorPicker.prototype.setPixel=function(x, y, r, g, b, a) {
    var idx = (x + y * 256) * 4;
    var buf=this.pixelBufData;    buf[idx+0] = r;    buf[idx+1] = g;    buf[idx+2] = b;    buf[idx+3] = 255;
}


function GradientPicker(gradient,minValue,maxValue){
  var that=this; 
  this.handleSize=6;
  if(gradient==null){
    this.minValue=0;
    this.maxValue=1;
    this.gradient=new ColorGradient();
    this.origGradient=null;
  }else{
    this.minValue=min(this.minValue,gradient.getMinValue());
    this.maxValue=max(this.maxValue,gradient.getMaxValue());
    this.gradient=gradient.clone();
    this.origGradient=this.gradient.clone();
  }
  this.element=nw('div');
  this.element.style.background='white';  this.element.className='gradientpicker';  this.canvas=nw('canvas');
  this.canvas.style.position='absolute';
  this.canvas.style.cursor='pointer';
  this.canvas.onclick=function(e){that.onAddGradient(e);};
  this.element.tabIndex=1;
  this.context = this.canvas.getContext('2d',{willReadFrequently: true});
  this.element.appendChild(this.canvas);
  this.setSize(200,50);
  
  this.handles=[];
  
  //this.buildHandles();
  //this.repaint();
}


GradientPicker.prototype.onAddGradient=function(e) {
    var point=getMouseLayerPoint(e);
	var n=between((this.isHorizontal ? point.x : point.y)-2,0,this.size);
	var val=this.minValue+ (this.isHorizontal ? n:(this.size-n))/this.size * (this.maxValue-this.minValue);
	var color=this.gradient.toColor(val);
	  this.gradient.addStepRgb(val,color[0],color[1],color[2],color[3]);
	this.buildHandles();
	this.repaint();
	this.fireGradientChanged();
	var that=this;
	var target=null;
	for(var i=0;i<this.handles.length;i++){
	  if(this.handles[i].value==val){
	      target=this.handles[i].dragger;
	      break;
	    }
	}
	if(target!=null){
	  rect=new Rect();
	  rect.readFromElement(target);
	  this.activeHandle=target.parentNode;
	  this.colorChooser=new ColorPicker(true,toColor(color[0],color[1],color[2],color[3]),getWindow(this.element),true);
	  if(this.isAlpha)
		  this.colorChooser.setAlphaEnabled();
	  this.colorChooser.onCancel=function(e){that.onNoColor(e)};
	  this.colorChooser.onColorChanged=function(){that.onChooserChanged()};
	  this.colorChooser.onOk=function(e){that.onChooserOk(e)};
	  this.colorChooser.onNoColor=function(e){that.onNoColor(e)};
	  this.colorChooser.show(rect.getRight(),rect.getBottom());
	}
}

GradientPicker.prototype.buildHandles=function() {
	for(var i in this.handles){
		this.element.removeChild(this.handles[i]);
	}
	this.handles=[];
	var that=this;
	var g=this.gradient;
	var len=g.length();
	for(var i=0;i<len;i++){
		var d=nwDiv('');
		if(this.isHorizontal){
		  d.style.width=toPx(this.handleSize-2);
		  d.style.top='-1px';
		  d.style.bottom='-1px';
		}else{
		  d.style.left='-1px';
		  d.style.right='-1px';
		  d.style.height=toPx(this.handleSize-2);
		}
		var c=g.getColorAtStep(i);
		d.value=g.getValueAtStep(i);
		var dragger=nwDiv('gradientpicker_dragger');
		dragger.style.width='100%';
		dragger.style.height='100%';
        dragger.style.cursor=this.isHorizontal ? "ew-resize" : "ns-resize";
		d.appendChild(dragger);
		makeDraggable(dragger,d,!this.isHorizontal,this.isHorizontal);
        dragger.ondragging=function(target,x,y,e){that.onDragging(target,x,y,e);};
        dragger.ondraggingStart=function(target,x,y,e){that.onDraggingStart(target,e);};
        dragger.ondraggingEnd=function(target,x,y,e){that.onDraggingEnd(target,x,y,e);};
        dragger.clipDragging=function(e,rect){that.clipDragger(e,rect);};
		d.dragger=dragger;
		d.color=c;
		this.updateHandle(d);
		this.handles[i]=d;
		this.element.appendChild(d);
	}
}
GradientPicker.prototype.updateHandle=function(d) {
	d.dragger.style.background=d.color;
}

GradientPicker.prototype.onDragging=function(target,x,y,e) {
	var h=target.parentNode;
	var n=this.isHorizontal ? x : y;
	var value=between(this.draggingStartValue+((this.isHorizontal ? n : -n)/this.size)*(this.maxValue-this.minValue),this.minValue,this.maxValue);
	h.value=value;
	this.handles.sort(function(a,b){return a.value-b.value});
	this.gradient=this.draggingGradient.clone();
	this.gradient.addStep(h.value,h.color);
	this.repaint();
	this.fireGradientChanged();
}
GradientPicker.prototype.onDraggingEnd=function(target,x,y,e) {
    if(x==0 && y==0){
      this.onEdit(e);
    }else{
	  this.buildHandles();
	  this.repaint();
	  this.fireGradientChanged();
	}
}
GradientPicker.prototype.onDraggingStart=function(target,x,y,e) {
	var p=target.parentNode;
	this.draggingStartValue=1*target.parentNode.value;
	this.draggingGradient=this.gradient.clone();
	this.draggingGradient.removeStepByValue(this.draggingStartValue);
	this.element.removeChild(p);
	this.element.appendChild(p);
}

GradientPicker.prototype.clipDragger=function(e,rect) {
	//rect.setTop(between(rect.getTop(),40-7,340-7));
}
GradientPicker.prototype.onEdit=function(e) {
	var that=this;
	var target=getMouseTarget(e);
	rect=new Rect();
	rect.readFromElement(target);
	this.activeHandle=target.parentNode;
	this.colorChooser=new ColorPicker(true,target.parentNode.color,getWindow(this.element),true);
	  if(this.isAlpha)
		  this.colorChooser.setAlphaEnabled();
	this.colorChooser.onCancel=function(e){that.onChooserCancel(e)};
	this.colorChooser.onColorChanged=function(){that.onChooserChanged()};
	this.colorChooser.onOk=function(e){that.onChooserOk(e)};
	this.colorChooser.onNoColor=function(e){that.onNoColor(e)};
	this.colorChooser.show(rect.getRight(),rect.top);
}


GradientPicker.prototype.onChooserCancel=function() {
	var color=this.colorChooser.getOrigColor();
	this.gradient.addStep(this.activeHandle.value,color);
	this.colorChooser.hide();
	this.colorChooser=null;
	this.activeHandle.color=color;
	this.updateHandle(this.activeHandle);
	this.repaint();
	this.fireGradientChanged();
	this.element.focus();
}
GradientPicker.prototype.onNoColor=function(e) {
    this.colorChooser.hide();
	this.colorChooser=null;
	this.gradient.removeStepByValue(this.activeHandle.value);
	this.buildHandles();
	this.repaint();
	this.fireGradientChanged();
	this.element.focus();
}
GradientPicker.prototype.onChooserOk=function() {
	var color=this.colorChooser.getColor();
	this.gradient.addStep(this.activeHandle.value,color);
	this.colorChooser.hide();
	this.colorChooser=null;
	this.activeHandle.color=color;
	this.updateHandle(this.activeHandle);
	this.repaint();
	this.fireGradientChanged();
	this.element.focus();
}

GradientPicker.prototype.onChooserChanged=function() {
	var color=this.colorChooser.getColor();
	this.gradient.addStep(this.activeHandle.value,color);
	this.activeHandle.color=color;
	this.updateHandle(this.activeHandle);
	this.repaint();
	this.fireGradientChanged();
}

GradientPicker.prototype.repaint=function() {
	var w=this.canvas.width;
	var h=this.height;
	var t=this.size-this.handleSize+2;
	var c=this.context;
	var g=this.gradient;
	var len=g.length();
	c.clearRect(0,0,w,h);
	if(len>0){
      var grad = this.context.createLinearGradient(this.isHorizontal ? this.size : 0, this.isHorizontal ? 0 : this.size, 0, 0);
      grad.addColorStop(this.isHorizontal ? 1 : 0,g.getColorAtStep(0));
      var min=this.minValue;
      var max=this.maxValue;
      if(min<max){
        for(var i=0;i<len;i++){
          var v=g.getValueAtStep(i);
          if(this.isHorizontal){
            grad.addColorStop(1-(v-min) / (max-min) ,g.getColorAtStep(i));
            var n=toPx(t*(v-min) / (max-min)-1);
            this.handles[i].style.left=n;
          } else{
            grad.addColorStop((v-min) / (max-min) ,g.getColorAtStep(i));
            var n=toPx(t-t*(v-min) / (max-min)-1);
            this.handles[i].style.top=n;
          }
        }  
      }
      grad.addColorStop(this.isHorizontal ? 0 : 1,g.getColorAtStep(len-1));
      c.fillStyle=grad;
      c.fillRect(0,0,w,h);
	}
}


GradientPicker.prototype.fireGradientChanged=function() {
	if(this.onGradientChanged)
		this.onGradientChanged(this);
}

GradientPicker.prototype.getGradient=function() {
	return this.gradient;
}
GradientPicker.prototype.setGradient=function(g) {
	this.gradient=g;
	this.buildHandles();
	this.repaint();
}
GradientPicker.prototype.getOrigGradient=function() {
	return this.origGradient;
}

GradientPicker.prototype.setSize=function(width,height) {
  this.width=width;
  this.height=height;
  this.isHorizontal=this.width>=this.height;
  this.size=this.isHorizontal ? this.width : this.height;
  this.element.style.width=toPx(this.width);
  this.element.style.height=toPx(this.height);
  this.canvas.width=width-2;
  this.canvas.height=height-2;
	this.buildHandles();
	this.repaint();
}


GradientPicker.prototype.setAlphaEnabled=function(b) {
	this.isAlpha=b;
}