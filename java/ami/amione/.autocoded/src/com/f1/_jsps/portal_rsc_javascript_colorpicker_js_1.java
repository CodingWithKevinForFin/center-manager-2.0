package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_colorpicker_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_colorpicker_js_1() {
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
            "function ColorPicker(standalone,origColor,element,allowNoColor){ \r\n"+
            "	\r\n"+
            "  var that=this; \r\n"+
            "  this.allowNoColor=allowNoColor;\r\n"+
            "  this.inputs={};\r\n"+
            "  this.radios={};\r\n"+
            "  this.values={};\r\n"+
            "  this.customColors=[];\r\n"+
            "  this.origColor=origColor;\r\n"+
            "  this.alphaEnabled=false;\r\n"+
            "  if(element==null)\r\n"+
            "	  alert(\"CAN NOT BE NULL!\");\r\n"+
            "  this.standalone=standalone;\r\n"+
            "  if(this.standalone){\r\n"+
            "    this.document=element.document;\r\n"+
            "    this.element=nw('div');\r\n"+
            "    this.element.style.zIndex='9999';\r\n"+
            "    this.glass=nw('div','disable_glass_clear');\r\n"+
            "    this.glass.onclick=function(e){that.onGlassClicked(e);}\r\n"+
            "    this.glass.ondblclick=function(e){that.onOkPressed(e);}\r\n"+
            "	this.glass.style.cursor='url(./rsc/eyedropper.png) 1 24,auto';\r\n"+
            "  }else{\r\n"+
            "    this.document=getDocument(element);\r\n"+
            "    this.element=element;\r\n"+
            "  }    this.shadow=nw('div','shadow');\r\n"+
            "  \r\n"+
            "  this.canvas=nw('canvas');\r\n"+
            "  this.canvas.style.position='absolute';\r\n"+
            "  this.canvas.style.left='10px';\r\n"+
            "  this.canvas.style.top='10px';\r\n"+
            "  this.canvas.width=256;\r\n"+
            "  this.canvas.height=256;\r\n"+
            "  this.canvas.style.border='1px solid #666666';\r\n"+
            "  this.canvas.style.cursor='pointer';\r\n"+
            "  this.context = this.canvas.getContext('2d',{willReadFrequently: true});\r\n"+
            "  this.element.appendChild(this.canvas);\r\n"+
            "  this.element.style.border='1px solid black';\r\n"+
            "  this.element.style.background='white';\r\n"+
            "  this.element.style.width='460px';\r\n"+
            "  this.shadow.style.width='460px';\r\n"+
            "  var height=360;\r\n"+
            "  if(this.allowNoColor){\r\n"+
            "      this.noColorButton=nw('button');\r\n"+
            "      this.element.appendChild(this.noColorButton);\r\n"+
            "      //this.noColorButton.style.width='300px';\r\n"+
            "      this.noColorButton.style.position='absolute';\r\n"+
            "      this.noColorButton.innerHTML='No&nbsp;color';\r\n"+
            "      this.noColorButton.style.left='387px';\r\n"+
            "      this.noColorButton.style.width='60px';\r\n"+
            "      this.noColorButton.style.top='215px';\r\n"+
            "      this.noColorButton.style.margin='0px 5px 0px 0px';\r\n"+
            "      this.noColorButton.style.padding='0px 1px';\r\n"+
            "      this.noColorButton.onclick=function(){that.onNoColor();}\r\n"+
            "  }\r\n"+
            "  this.element.style.height=height+'px';\r\n"+
            "  this.shadow.style.height=height+'px';\r\n"+
            "  \r\n"+
            "  this.pixelBuf=this.context.createImageData(this.canvas.width,this.canvas.height);  this.pixelBufData=this.pixelBuf.data;\r\n"+
            "  \r\n"+
            "  this.sliderGrabber=nw('canvas');\r\n"+
            "  this.sliderGrabber.style.position='absolute';\r\n"+
            "  this.sliderGrabber.style.left='269px';\r\n"+
            "  this.sliderGrabber.style.top='10px';\r\n"+
            "  this.sliderGrabber.width=43;\r\n"+
            "  this.sliderGrabber.height=12;\r\n"+
            "  this.sliderGrabberContext=this.sliderGrabber.getContext('2d',{willReadFrequently: true});\r\n"+
            "  this.sliderGrabber.style.cursor='pointer';\r\n"+
            "  this.paintSliderGrabberContext();\r\n"+
            "  \r\n"+
            "  \r\n"+
            "  this.element.appendChild(this.sliderGrabber);\r\n"+
            "  \r\n"+
            "  this.sliderCanvas=nw('canvas');\r\n"+
            "  this.sliderCanvas.style.position='absolute';\r\n"+
            "  this.sliderCanvas.style.left='280px';\r\n"+
            "  this.sliderCanvas.style.top='10px';\r\n"+
            "  this.sliderCanvas.width=20;\r\n"+
            "  this.sliderCanvas.height=256;\r\n"+
            "  this.sliderCanvas.style.border='1px solid #666666';\r\n"+
            "  this.sliderCanvas.style.cursor='pointer';\r\n"+
            "  this.sliderCanvas.style.background='white';\r\n"+
            "  //this.sliderCanvas.onmouseup=function(e){that.onSliderClick(e);}\r\n"+
            "  this.sliderContext = this.sliderCanvas.getContext('2d',{willReadFrequently: true});\r\n"+
            "  this.element.appendChild(this.sliderCanvas);\r\n"+
            "  \r\n"+
            "  makeDraggable(this.sliderGrabber,this.sliderGrabber,true,false);\r\n"+
            "  this.sliderGrabber.clipDragging=function(e,rect){that.clipSlider(e,rect);};\r\n"+
            "  this.sliderGrabber.ondragging=function(tar,x,y,e){that.onSliderDragging(e,x,y);};\r\n"+
            "  \r\n"+
            "  makeDraggable(this.sliderCanvas,this.sliderGrabber,true,false);\r\n"+
            "  this.sliderCanvas.clipDragging=function(e,rect){that.clipSlider(e,rect);};\r\n"+
            "  this.sliderCanvas.ondragging=function(tar,x,y,e){that.onSliderDragging(e,x,y);};\r\n"+
            "  this.sliderCanvas.ondraggingStart=function(target,e){that.onSliderDragging(e);}\r\n"+
            "  this.sliderPosition=0;\r\n"+
            "  \r\n"+
            "  this.newRadio('hue','Hue',315,65,360, true);\r\n"+
            "  this.newRadio('sat','Sat.',315, 85,100,true);\r\n"+
            "  this.newRadio('bri','Bright',315,105,100,true);\r\n"+
            "  \r\n"+
            "  this.newRadio('red','Red',315,130,255,true);\r\n"+
            "  this.newRadio('green','Green',315,150,255,true);\r\n"+
            "  this.newRadio('blue','Blue',315,170,255,true);\r\n"+
            "  if(this.origColor!=null && this.origColor.length==9){\r\n"+
            "	    this.setAlphaEnabled();\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  this.backgroundDiv=nwDiv('',325,10,118,50);\r\n"+
            "  this.backgroundDiv.className=\"colorpicker_samplebackground\";\r\n"+
            "  this.colorSampleDiv=nwDiv('',320,10,64,50);\r\n"+
            "  this.oldColorSampleDiv=nwDiv('',384,10,64,50);\r\n"+
            "  this.oldColorSampleDiv.style.cursor='pointer';\r\n"+
            "  this.element.appendChild(this.backgroundDiv);\r\n"+
            "  this.element.appendChild(this.colorSampleDiv);\r\n"+
            "  this.element.appendChild(this.oldColorSampleDiv);\r\n"+
            "  this.oldColorSampleDiv.style.border='1px solid #666666';\r\n"+
            "  this.colorSampleDiv.style.border='1px solid #666666';\r\n"+
            "  this.colorSampleDiv.style.borderWidth='1px 0px 1px 1px';\r\n"+
            "  this.oldColorSampleDiv.style.borderWidth='1px 1px 1px 0px';\r\n"+
            "  this.oldColorSampleDiv.onclick=function(){\r\n"+
            "	  if(that.origColor){\r\n"+
            "	    that.color=that.origColor;that.onValueChanged('O',true);that.repaint();\r\n"+
            "	  }else{\r\n"+
            "	      that.color=that.origColor;\r\n"+
            "	       that.onValueChanged('NONE',true);that.repaint();\r\n"+
            "	      that.values.color=null;\r\n"+
            "		  that.hexColor.value='no color';\r\n"+
            "	  }\r\n"+
            "  };\r\n"+
            "  \r\n"+
            "  this.hexColor=nw('input','slider_input');\r\n"+
            "  this.hexColor.style.width=toPx(70);\r\n"+
            "  this.hexColor.style.height=toPx(19);\r\n"+
            "  //this.canvas.onmouseup=function(e){that.onColorClick(e);};\r\n"+
            "  makeDraggable(this.canvas,null,true,false);\r\n"+
            "  this.canvas.ondragging=function(target,x,y,e){that.onColorClick(e);};\r\n"+
            "  this.canvas.ondraggingStart=function(target,e){that.onColorClick(e);}\r\n"+
            "  \r\n"+
            "  this.sliderCanvas.clipDragging=function(e,rect){that.clipSlider(e,rect);};\r\n"+
            "  var hexColorDiv=nwDiv('',314,215,62,20);\r\n"+
            "  this.hexColor.onchange=function(){that.onValueChanged('HEX',true);if(that.hexColor.value!='no color')that.repaint();};\r\n"+
            "  this.paletteDiv=nwDiv('colorchooser_pallete',10,273,48*9+6,4*9+3);\r\n"+
            "  var t={};\r\n"+
            "  for(var i=0;i<13;i++){\r\n"+
            "    for(var j=0;j<3;j++){\r\n"+
            "      for(var k=0;k<3;k++){\r\n"+
            "	    var p=nwDiv('colorchooser_pallete_item',5+i*33+k*10,4+j*10,9,9);\r\n"+
            "	    if(i==12){\r\n"+
            "  	      t.hue=0;\r\n"+
            "	      t.sat=0;\r\n"+
            "	      t.bri=100-(j*3+k)*100/8;\r\n"+
            "	    }else{\r\n"+
            "  	      t.hue=360/12 * i;\r\n"+
            "	      t.bri=100-j*25;\r\n"+
            "	      t.sat=100-k*25;\r\n"+
            "	    }\r\n"+
            "	    this.hsb2rgb(t,t);\r\n"+
            "	    var c=toColor(t.red,t.green,t.blue);\r\n"+
            "	    var c2=toColor(Math.max(50,t.red*.8),Math.max(50,t.green*.8),Math.max(50,t.blue*.8));\r\n"+
            "	    p.style.background=c;\r\n"+
            "	    p.style.borderColor=c2;\r\n"+
            "	    p.color=c;\r\n"+
            "	    p.onclick=function(e){that.onColorPaletteClicked(e)};\r\n"+
            "        p.ondblclick=function(){that.onOkPressed();};\r\n"+
            "	    this.paletteDiv.appendChild(p);\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  this.customColorDiv=nwDiv('colorchooser_pallete',10,315,48*9+6,4*9+2);\r\n"+
            "  hexColorDiv.appendChild(this.hexColor);\r\n"+
            "  this.element.appendChild(hexColorDiv);\r\n"+
            "  var buttonDiv=nwDiv('',314,238,150,30);\r\n"+
            "  this.okButton=nw('button');\r\n"+
            "  this.okButton.style.width='70px';\r\n"+
            "  this.okButton.style.fontWeight='bold';\r\n"+
            "  this.okButton.innerHTML='OK';\r\n"+
            "  this.okButton.style.margin='0px 3px 0px 0px';\r\n"+
            "  buttonDiv.appendChild(this.okButton);\r\n"+
            "  this.clButton=nw('button');\r\n"+
            "  this.clButton.innerHTML='Cancel';\r\n"+
            "  this.clButton.style.width='60px';\r\n"+
            "  buttonDiv.appendChild(this.clButton);\r\n"+
            "  this.element.appendChild(buttonDiv);\r\n"+
            "  this.element.appendChild(this.paletteDiv);\r\n"+
            "  this.element.appendChild(this.customColorDiv);\r\n"+
            "  \r\n"+
            "  this.okButton.onclick=function(){that.onOkPressed();}\r\n"+
            "  this.canvas.ondblclick=function(){that.onOkPressed();};\r\n"+
            "  this.sliderCanvas.ondblclick=function(){that.onOkPressed();};\r\n"+
            "  this.clButton.onclick=function(){that.onCancel();}\r\n"+
            "  \r\n"+
            "  this.onValueChanged('O',false);\r\n"+
            "  this.onSliderModeChanged('hue');\r\n"+
            "  this.repaint();\r\n"+
            "  //log(this.origColor);\r\n"+
            "  //log(!this.origColor);\r\n"+
            "  this.lastClickTime=0;\r\n"+
            "  if(this.allowNoColor && !this.origColor)\r\n"+
            "    this.hexColor.value='no color';\r\n"+
            "} \r\n"+
            "\r\n"+
            "ColorPicker.prototype.onGlassClicked=function(e){\r\n"+
            "	var now=Date.now();\r\n"+
            "	var dur=now-this.lastClickTime;\r\n"+
            "	this.lastClickTime=now;\r\n"+
            "	if(dur<200){\r\n"+
            "		this.onOkPressed();\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	var point=getMousePoint(e);\r\n"+
            "	var that=this;\r\n"+
            "	if(dur<2000){\r\n"+
            "        var c=this.imageContext.getImageData(point.x,point.y,1,1).data;\r\n"+
            "        var r=c[0],g=c[1],b=c[2];\r\n"+
            "        var hex = \"#\" + (0x1000000 + (r << 16) + (g << 8) + b).toString(16).slice(1);\r\n"+
            "        this.setHex(hex);\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	html2canvas(document.body, {\r\n"+
            "        onrendered: function(canvas) {\r\n"+
            "            that.imageContext=canvas.getContext('2d',{willReadFrequently: true});\r\n"+
            "            var c=that.imageContext.getImageData(point.x,point.y,1,1).data;\r\n"+
            "            var r=c[0],g=c[1],b=c[2];\r\n"+
            "            var hex = \"#\" + (0x1000000 + (r << 16) + (g << 8) + b).toString(16).slice(1);\r\n"+
            "            that.setHex(hex);\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.onOkPressed=function(){\r\n"+
            "	if(this.onOk){\r\n"+
            "	  this.onOk();\r\n"+
            "	  this.onOk=null;\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.setHex=function(color){\r\n"+
            "	if(color!=null){\r\n"+
            "        this.hexColor.value=color;\r\n"+
            "        this.onValueChanged('HEX',true);\r\n"+
            "        this.repaint();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.handleKeydown=function(e){\r\n"+
            "	if(e.key === \"Escape\" && e.shiftKey == false && e.ctrlKey == false && e.altKey == false){\r\n"+
            "		this.onCancel();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.addColorChoice=function(color){\r\n"+
            "	var i=this.customColors.length;\r\n"+
            "	var x=i%39;\r\n"+
            "	var y=(i-x)/39;\r\n"+
            "	if(y>=3)\r\n"+
            "		return;\r\n"+
            "	this.customColors[i]=color;\r\n"+
            "	var p=nwDiv('colorchooser_pallete_item',4+x*11,2+y*11,10,10);\r\n"+
            "	p.style.background=color;\r\n"+
            "	var that=this;\r\n"+
            "	p.color=color;\r\n"+
            "	p.onclick=function(e){that.onColorPaletteClicked(e)};\r\n"+
            "    p.ondblclick=function(){that.onOkPressed();};\r\n"+
            "	this.customColorDiv.appendChild(p);\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.show=function(x,y){\r\n"+
            "	this.element.style.top=toPx(y);\r\n"+
            "	this.element.style.left=toPx(x);\r\n"+
            "	if(this.standalone){\r\n"+
            "	  this.document.body.appendChild(this.glass);\r\n"+
            "	}\r\n"+
            "	this.document.body.appendChild(this.element);\r\n"+
            "	ensureInWindow(this.element);\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.");
          out.print(
            "hide=function(){\r\n"+
            "	if(this.standalone){\r\n"+
            "	  this.document.body.removeChild(this.glass);\r\n"+
            "	}\r\n"+
            "	this.document.body.removeChild(this.element);\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.onColorPaletteClicked=function(e){\r\n"+
            "  var target=getMouseTarget(e);\r\n"+
            "  this.hexColor.value=target.color;\r\n"+
            "  this.onValueChanged('HEX',true);\r\n"+
            "  this.repaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.clipSlider=function(e,rect){\r\n"+
            "	rect.setTop(between(rect.getTop(),-4,253));\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.paintSliderGrabberContext=function(){\r\n"+
            "	var c=this.sliderGrabberContext;\r\n"+
            "	c.beginPath();\r\n"+
            "	c.translate(.5,.5);\r\n"+
            "	c.moveTo(2,0);\r\n"+
            "	c.lineTo(10,5);\r\n"+
            "	c.lineTo(2,10);\r\n"+
            "	c.lineTo(2,0);\r\n"+
            "	c.strokeStyle='#666666';\r\n"+
            "	if(this.sliderMode=='hue'){\r\n"+
            "	  var t={};\r\n"+
            "	  t.hue=this.values.hue;\r\n"+
            "	  t.sat=100;\r\n"+
            "	  t.bri=100;\r\n"+
            "	  this.hsb2rgb(t,t);\r\n"+
            "	  c.fillStyle=toColor(t.red,t.green,t.blue);\r\n"+
            "	}else\r\n"+
            "	  c.fillStyle=this.values.color;\r\n"+
            "	c.fill();\r\n"+
            "	c.stroke();\r\n"+
            "	c.moveTo(41,0);\r\n"+
            "	c.lineTo(33,5);\r\n"+
            "	c.lineTo(41,10);\r\n"+
            "	c.lineTo(41,0);\r\n"+
            "	c.fill();\r\n"+
            "	c.stroke();\r\n"+
            "	c.translate(-.5,-.5);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.newRadio=function(id,label,x,y,size,includeRadio){\r\n"+
            "	var that=this;\r\n"+
            "	if(includeRadio){\r\n"+
            "	  var radio=nw('input',0,0,13,13);\r\n"+
            "	  radio.type='radio';\r\n"+
            "	  radio.name='colorchooser_radio';\r\n"+
            "	  radio.onclick=function(){that.onSliderModeChanged(id)};\r\n"+
            "	  radio.style.margin=\"2px 0px\";\r\n"+
            "	}\r\n"+
            "	var input=nwDiv('',includeRadio ? 15 : 30,0,90,20);\r\n"+
            "	var slider=new Slider(input,0,0,size,90,0,true);\r\n"+
            "	slider.onValueChanged=function(){that.onValueChanged(id,true);that.repaint()};\r\n"+
            "	this.inputs[id]=slider;\r\n"+
            "	if(includeRadio){\r\n"+
            "	  this.radios[id]=radio;\r\n"+
            "	}\r\n"+
            "	var lab=nwDiv('colorchooser_label',includeRadio ? 106 : 121,0,0,0);\r\n"+
            "	lab.innerHTML=label;\r\n"+
            "	\r\n"+
            "	var div=nwDiv('',x-(includeRadio ? 3 : 18),y,100,15);\r\n"+
            "	if(includeRadio)\r\n"+
            "	  div.appendChild(radio);\r\n"+
            "	div.appendChild(input);\r\n"+
            "	div.appendChild(lab);\r\n"+
            "	this.element.appendChild(div);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.getColor=function(){\r\n"+
            "	if(this.values.color=='no color')\r\n"+
            "		return null ;\r\n"+
            "	return this.values.color;\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.getOrigColor=function(){\r\n"+
            "	return this.origColor;\r\n"+
            "}\r\n"+
            "	\r\n"+
            "ColorPicker.prototype.onSliderModeChanged=function(sm){\r\n"+
            "	this.sliderMode=sm;\r\n"+
            "	if(this.radios[sm]!=null)\r\n"+
            "	  this.radios[sm].checked='on';\r\n"+
            "	if(sm=='red'){this.xdim='blue',this.ydim='green';this.xdimScale=256;this.ydimScale=256;this.sliderScale=256; };\r\n"+
            "	if(sm=='green'){this.xdim='blue',this.ydim='red';this.xdimScale=256;this.ydimScale=256;this.sliderScale=256; };\r\n"+
            "	if(sm=='blue'){this.xdim='red',this.ydim='green';this.xdimScale=256;this.ydimScale=256;this.sliderScale=256; };\r\n"+
            "	\r\n"+
            "	if(sm=='hue'){this.xdim='sat',this.ydim='bri';this.xdimScale=100;this.ydimScale=100;this.sliderScale=360; };\r\n"+
            "	if(sm=='sat'){this.xdim='hue',this.ydim='bri';this.xdimScale=360;this.ydimScale=100;this.sliderScale=100; };\r\n"+
            "	if(sm=='bri'){this.xdim='hue',this.ydim='sat';this.xdimScale=360;this.ydimScale=100;this.sliderScale=100; };\r\n"+
            "	this.setSliderValue(this.values[sm]*255/this.sliderScale,false);\r\n"+
            "	this.repaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.onValueChanged=function(id,fire){\r\n"+
            "	var vals=this.values;\r\n"+
            "	var updateHSB=true;\r\n"+
            "	if(id=='HEX'){\r\n"+
            "		if(this.hexColor.value && this.hexColor.value!='no color'){\r\n"+
            "		  var colors=parseColor(this.hexColor.value);\r\n"+
            "		  vals.red=colors[0];\r\n"+
            "		  vals.green=colors[1];\r\n"+
            "		  vals.blue=colors[2];\r\n"+
            "	      if(this.getAlphaEnabled())\r\n"+
            "		   vals.alpha=colors[3];\r\n"+
            "		  if(vals.alpha==null)\r\n"+
            "			  vals.alpha=255;\r\n"+
            "		}else{\r\n"+
            "			this.hexColor.value='no color';\r\n"+
            "			vals.color=null;\r\n"+
            "	        if(this.onColorChanged!=null && fire===true)\r\n"+
            "		      this.onColorChanged(this,null);\r\n"+
            "	        return;\r\n"+
            "		}\r\n"+
            "	} else if(id=='O'){\r\n"+
            "		if(this.origColor){\r\n"+
            "		  var colors=parseColor(this.origColor);\r\n"+
            "		  vals.red=colors[0];\r\n"+
            "		  vals.green=colors[1];\r\n"+
            "		  vals.blue=colors[2];\r\n"+
            "		  vals.alpha=colors[3];\r\n"+
            "		  if(vals.alpha==null)\r\n"+
            "			  vals.alpha=255;\r\n"+
            "		}else{\r\n"+
            "		  vals.red=0;\r\n"+
            "		  vals.green=0;\r\n"+
            "		  vals.blue=0;\r\n"+
            "		  vals.alpha=255;\r\n"+
            "		}\r\n"+
            "	}else if(id=='red'){\r\n"+
            "	  vals.red=this.inputs.red.getValue();\r\n"+
            "	}else if(id=='green'){\r\n"+
            "	  vals.green=this.inputs.green.getValue();\r\n"+
            "	}else if(id=='blue'){\r\n"+
            "	  vals.blue=this.inputs.blue.getValue();\r\n"+
            "	}else if(id=='bri'){\r\n"+
            "	  vals.bri=this.inputs.bri.getValue();\r\n"+
            "	  updateHSB=false;\r\n"+
            "	}else if(id=='sat'){\r\n"+
            "	  vals.sat=this.inputs.sat.getValue();\r\n"+
            "	  updateHSB=false;\r\n"+
            "	}else if(id=='hue'){\r\n"+
            "	  vals.hue=this.inputs.hue.getValue();\r\n"+
            "	  updateHSB=false;\r\n"+
            "	}else if(id=='alpha'){\r\n"+
            "	  vals.alpha=this.inputs.alpha.getValue();\r\n"+
            "	}else if(id=='NONE'){\r\n"+
            "	  this.values.color=null;\r\n"+
            "	  if(this.onColorChanged!=null && fire===true)\r\n"+
            "		  this.onColorChanged(this,null);\r\n"+
            "	  return;\r\n"+
            "	}\r\n"+
            "	if(updateHSB){\r\n"+
            "      var max=Math.max(vals.red,vals.green,vals.blue);\r\n"+
            "      var min=Math.min(vals.red,vals.green,vals.blue);\r\n"+
            "      var c=max-min;\r\n"+
            "      vals.sat=max==0 ? 100 : 100*c/max;\r\n"+
            "      vals.bri=max/2.55;\r\n"+
            "      if(max==min){\r\n"+
            "    	  vals.hue=0;\r\n"+
            "      }else if(max==vals.red){\r\n"+
            "    	  vals.hue=60*((360+(vals.green-vals.blue)/c)%6);\r\n"+
            "      }else if(max==vals.green){\r\n"+
            "    	  vals.hue=60*((vals.blue-vals.red)/c+2);\r\n"+
            "      }else{\r\n"+
            "    	  vals.hue=60*((vals.red-vals.green)/c+4);\r\n"+
            "      }\r\n"+
            "	}else{\r\n"+
            "	  this.hsb2rgb(vals,vals);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	vals.color=toColor(vals.red,vals.green,vals.blue);\r\n"+
            "	if(this.getAlphaEnabled() && vals.alpha!=255)\r\n"+
            "	  vals.color+=toColorPart(vals.alpha);\r\n"+
            "	if(this.onColorChanged!=null && fire===true){\r\n"+
            "		   this.onColorChanged(this,vals.color);\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.inputs.red.setValue(vals.red,true,false);\r\n"+
            "	this.inputs.green.setValue(vals.green,true,false);\r\n"+
            "	this.inputs.blue.setValue(vals.blue,true,false);\r\n"+
            "    if(this.alphaEnabled)\r\n"+
            "	  this.inputs.alpha.setValue(vals.alpha,true,false);\r\n"+
            "	\r\n"+
            "	this.inputs.sat.setValue(vals.sat,true,false);\r\n"+
            "	this.inputs.bri.setValue(vals.bri,true,false);\r\n"+
            "	this.inputs.hue.setValue(vals.hue,true,false);\r\n"+
            "	if(fire){\r\n"+
            "	  var loc=between(255-this.values[this.sliderMode]*255/this.sliderScale,0,255);\r\n"+
            "	  this.sliderPosition=loc;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	\r\n"+
            "	\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.hsb2rgb=function(hsb,rgb,logg){\r\n"+
            "      var bri=hsb.bri/100,hue=(360-hsb.hue)/360*6,sat=hsb.sat/100;\r\n"+
            "      if(hue==0)\r\n"+
            "    	  hue=.01;\r\n"+
            "      var i=fl(hue);\r\n"+
            "      var f=hue-i;\r\n"+
            "      if((i & 0x01)==0)\r\n"+
            "    	  f=1-f;\r\n"+
            "      var m=between(fl(255*(bri*(1-sat))),0,255);\r\n"+
            "      var n=between(fl(255*(bri*(1-sat*f))),0,255);\r\n"+
            "      var v=between(fl(255*(bri)),0,255);\r\n"+
            "      switch(i){\r\n"+
            "        case 6:\r\n"+
            "        case 0: rgb.red=v; rgb.blue=n; rgb.green=m;  break;\r\n"+
            "        case 1: rgb.red=n; rgb.blue=v; rgb.green=m;  break;\r\n"+
            "        case 2: rgb.red=m; rgb.blue=v; rgb.green=n;  break;\r\n"+
            "        case 3: rgb.red=m; rgb.blue=n; rgb.green=v;  break;\r\n"+
            "        case 4: rgb.red=n; rgb.blue=m; rgb.green=v;  break;\r\n"+
            "        case 5: rgb.red=v; rgb.blue=m; rgb.green=n;  break;\r\n"+
            "      }\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.onSliderDragging=function(point,x,y){\r\n"+
            "	var loc=255-(point.y-new Rect().readFromElement(this.sliderCanvas).top);\r\n"+
            "	this.setSliderValue(loc,true);\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.setSliderValue=function(loc,fire){\r\n"+
            "	loc=between(255-loc,0,255);\r\n"+
            "	this.sliderPosition=loc;\r\n"+
            "	if(fire){\r\n"+
            "	  this.inputs[this.sliderMode].setValue((255-loc)*this.sliderScale/256);\r\n"+
            "	  this.onValueChanged(this.sliderMode,true);\r\n"+
            "	  this.repaint();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.onSliderClick=function(point){\r\n"+
            "	var loc=255-fl(getMouseLayerPoint(point).y-1);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ColorPicker.prototype.onColorClick=function(e){\r\n"+
            "    //log('-----');\r\n"+
            "    //log(this.values);\r\n"+
            "    var point=getMousePoint(e);\r\n"+
            "    var rect=new Rect();\r\n"+
            "    rect.readFromElement(this.canvas);\r\n"+
            "	var x=between(fl(point.x-rect.left),0,255);\r\n"+
            "	var y=255-between(fl(point.y-rect.top),0,255);\r\n"+
            "	var sm=this.sliderMode,vx=this.xdim,vy=this.ydim,sx=this.xdimScale,sy=this.ydimScale;\r\n"+
            "	this.inputs[vx].setValue(x*sx/256);\r\n"+
            "	this.onValueChanged(vx,false);\r\n"+
            "	this.inputs[vy].setValue(y*sy/256);\r\n"+
            "	this.onValueChanged(vy,true);\r\n"+
            "    //log(this.values);\r\n"+
            "	this.repaint();\r\n"+
            "    //log(this.values);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.onMouseMove=function(point){\r\n"+
            "}\r\n"+
            "ColorPicker.prototype.findNodeAt=function(x,y,data){\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ColorPicker.prototype.drawSlider=function(y,r,g,b){\r\n"+
            "  this.sliderContext.strokeStyle=toColor(r,g,b);\r\n"+
            "  this.sliderContext.beginPath();\r\n"+
            "  this.sliderContext.moveTo(0,255-y+.5);\r\n"+
            "  this.sliderContext.lineTo(20,255-y+.5);\r\n"+
            "  this.sliderContext.stroke();\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.setAlphaEnabled=function(){\r\n"+
            "    if(this.alphaEnabled)\r\n"+
            "      return;\r\n"+
            "    this.alphaEnabled=true;\r\n"+
            "	if(this.alphaEnabled){\r\n"+
            "      this.newRadio('alpha','Opacity',315,190,255,false);\r\n"+
            "      this.inputs['alpha'].setValue(255,true,false);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.getAlphaEnabled=function(){\r\n"+
            "    return (this.alphaEnabled);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "ColorPicker.prototype.repaint=function(){\r\n"+
            "	var sm=this.sliderMode;\r\n"+
            "	this.sliderContext.clearRect(0,0,this.sliderContext.width,this.sliderContext.height);\r\n"+
            "    this.context.getImageData(0,0,1,1);\r\n"+
            "	if(this.hexColor.value!='no color'){\r\n"+
            "	if(sm=='red'){\r\n"+
            "		for(var i=0;i<256;i++)\r\n"+
            "			this.drawSlider(i,i,this.values.green,this.values.blue);\r\n"+
            "		for(var x=0;x<256;x++)\r\n"+
            "		  for(var y=0;y<256;y++)\r\n"+
            "			this.setPixel(x,y,this.values.red,255-y,x);\r\n"+
            "	}else if(sm=='green'){\r\n"+
            "		for(var i=0;i<256;i++)\r\n"+
            "			this.drawSlider(i,this.values.red,i,this.values.blue);\r\n"+
            "		for(var x=0;x<256;x++)\r\n"+
            "		  for(var y=0;y<256;y++)\r\n"+
            "			this.setPixel(x,y,255-y,this.values.green,x);\r\n"+
            "	}else if(sm=='blue'){\r\n"+
            "		for(var i=0;i<256;i++)\r\n"+
            "			this.drawSlider(i,this.values.red,this.values.green,i);\r\n"+
            "		for(var x=0;x<256;x++)\r\n"+
            "		  for(var y=0;y<256;y++)\r\n"+
            "			this.setPixel(x,y,x,255-y,this.values.blue);\r\n"+
            "	}else if(sm=='hue'){\r\n"+
            "		var rgb={};\r\n"+
            "		var hsb={hue:this.values.hue, sat:this.values.sat, bri:this.values.bri};\r\n"+
            "		for(var i=0;i<256;i++){\r\n"+
            "			hsb.hue=i*360/256;\r\n"+
            "			hsb.sat=100;\r\n"+
            "			hsb.bri=100;\r\n"+
            "			this.hsb2rgb(hsb,rgb);\r\n"+
            "			this.drawSlider(i,rgb.red,rgb.green,rgb.blue);\r\n"+
            "		}\r\n"+
            "		for(var x=0;x<256;x++)\r\n"+
            "		  for(var y=0;y<256;y++){\r\n"+
            "			hsb.hue=this.values.hue;\r\n"+
            "			hsb.sat=x*100/256;\r\n"+
            "			hsb.bri=(255-y)*100/256;\r\n"+
            "			this.hsb2rgb(hsb,rgb);\r\n"+
            "			this.setPixel(x,y,rgb.red,rgb.green,rgb.blue);\r\n"+
            "		  }\r\n"+
            "");
          out.print(
            "	}else if(sm=='sat'){\r\n"+
            "		var rgb={};\r\n"+
            "		var hsb={hue:this.values.hue, sat:this.values.sat, bri:this.values.bri};\r\n"+
            "		for(var i=0;i<256;i++){\r\n"+
            "			hsb.sat=i*100/256;\r\n"+
            "			this.hsb2rgb(hsb,rgb);\r\n"+
            "			this.drawSlider(i,rgb.red,rgb.green,rgb.blue);\r\n"+
            "		}\r\n"+
            "		for(var x=0;x<256;x++)\r\n"+
            "		  for(var y=0;y<256;y++){\r\n"+
            "			hsb.sat=this.values.sat;\r\n"+
            "			hsb.hue=x*360/256;\r\n"+
            "			hsb.bri=(255-y)*100/256;\r\n"+
            "			this.hsb2rgb(hsb,rgb);\r\n"+
            "			this.setPixel(x,y,rgb.red,rgb.green,rgb.blue);\r\n"+
            "		  }\r\n"+
            "	}else if(sm=='bri'){\r\n"+
            "		var rgb={};\r\n"+
            "		var hsb={hue:this.values.hue, sat:this.values.sat, bri:this.values.bri};\r\n"+
            "		for(var i=0;i<256;i++){\r\n"+
            "			hsb.bri=i*100/256;\r\n"+
            "			this.hsb2rgb(hsb,rgb);\r\n"+
            "			this.drawSlider(i,rgb.red,rgb.green,rgb.blue);\r\n"+
            "		}\r\n"+
            "		for(var x=0;x<256;x++)\r\n"+
            "		  for(var y=0;y<256;y++){\r\n"+
            "			hsb.bri=this.values.bri;\r\n"+
            "			hsb.sat=(255-y)*100/256;\r\n"+
            "			hsb.hue=x*360/256;\r\n"+
            "			this.hsb2rgb(hsb,rgb);\r\n"+
            "			this.setPixel(x,y,rgb.red,rgb.green,rgb.blue);\r\n"+
            "		  }\r\n"+
            "	}\r\n"+
            "	}\r\n"+
            "	this.sliderGrabber.style.top=toPx(this.sliderPosition-4+10);\r\n"+
            "	this.hexColor.value=this.values.color;\r\n"+
            "	this.colorSampleDiv.style.background=this.values.color;\r\n"+
            "	if(!this.origColor){\r\n"+
            "	  this.oldColorSampleDiv.style.background='white';\r\n"+
            "	  this.oldColorSampleDiv.style.color='#AAAAAA';\r\n"+
            "	  this.oldColorSampleDiv.style.textAlign='center';\r\n"+
            "	  this.oldColorSampleDiv.innerHTML=\"<P>no color\";\r\n"+
            "	}else\r\n"+
            "	  this.oldColorSampleDiv.style.background=this.origColor;\r\n"+
            "    this.context.putImageData(this.pixelBuf,0,0);\r\n"+
            "    this.context.beginPath();\r\n"+
            "    this.context.fillStyle=this.values.color;\r\n"+
            "    if((this.values.red+this.values.blue+this.values.green)/3<120 ){\r\n"+
            "    	this.context.strokeStyle='white';\r\n"+
            "    }else{\r\n"+
            "    	this.context.strokeStyle='black';\r\n"+
            "    }\r\n"+
            "    this.context.arc(this.values[this.xdim]*256/this.xdimScale,255-this.values[this.ydim]*256/this.ydimScale,5,0,2*Math.PI,false);\r\n"+
            "    this.context.fill();\r\n"+
            "    this.context.stroke();\r\n"+
            "    this.paintSliderGrabberContext();\r\n"+
            "    //log(this.values);\r\n"+
            "    //log(this.values);\r\n"+
            "}\r\n"+
            "\r\n"+
            "ColorPicker.prototype.setPixel=function(x, y, r, g, b, a) {\r\n"+
            "    var idx = (x + y * 256) * 4;\r\n"+
            "    var buf=this.pixelBufData;    buf[idx+0] = r;    buf[idx+1] = g;    buf[idx+2] = b;    buf[idx+3] = 255;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function GradientPicker(gradient,minValue,maxValue){\r\n"+
            "  var that=this; \r\n"+
            "  this.handleSize=6;\r\n"+
            "  if(gradient==null){\r\n"+
            "    this.minValue=0;\r\n"+
            "    this.maxValue=1;\r\n"+
            "    this.gradient=new ColorGradient();\r\n"+
            "    this.origGradient=null;\r\n"+
            "  }else{\r\n"+
            "    this.minValue=min(this.minValue,gradient.getMinValue());\r\n"+
            "    this.maxValue=max(this.maxValue,gradient.getMaxValue());\r\n"+
            "    this.gradient=gradient.clone();\r\n"+
            "    this.origGradient=this.gradient.clone();\r\n"+
            "  }\r\n"+
            "  this.element=nw('div');\r\n"+
            "  this.element.style.background='white';  this.element.className='gradientpicker';  this.canvas=nw('canvas');\r\n"+
            "  this.canvas.style.position='absolute';\r\n"+
            "  this.canvas.style.cursor='pointer';\r\n"+
            "  this.canvas.onclick=function(e){that.onAddGradient(e);};\r\n"+
            "  this.element.tabIndex=1;\r\n"+
            "  this.context = this.canvas.getContext('2d',{willReadFrequently: true});\r\n"+
            "  this.element.appendChild(this.canvas);\r\n"+
            "  this.setSize(200,50);\r\n"+
            "  \r\n"+
            "  this.handles=[];\r\n"+
            "  \r\n"+
            "  //this.buildHandles();\r\n"+
            "  //this.repaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "GradientPicker.prototype.onAddGradient=function(e) {\r\n"+
            "    var point=getMouseLayerPoint(e);\r\n"+
            "	var n=between((this.isHorizontal ? point.x : point.y)-2,0,this.size);\r\n"+
            "	var val=this.minValue+ (this.isHorizontal ? n:(this.size-n))/this.size * (this.maxValue-this.minValue);\r\n"+
            "	var color=this.gradient.toColor(val);\r\n"+
            "	  this.gradient.addStepRgb(val,color[0],color[1],color[2],color[3]);\r\n"+
            "	this.buildHandles();\r\n"+
            "	this.repaint();\r\n"+
            "	this.fireGradientChanged();\r\n"+
            "	var that=this;\r\n"+
            "	var target=null;\r\n"+
            "	for(var i=0;i<this.handles.length;i++){\r\n"+
            "	  if(this.handles[i].value==val){\r\n"+
            "	      target=this.handles[i].dragger;\r\n"+
            "	      break;\r\n"+
            "	    }\r\n"+
            "	}\r\n"+
            "	if(target!=null){\r\n"+
            "	  rect=new Rect();\r\n"+
            "	  rect.readFromElement(target);\r\n"+
            "	  this.activeHandle=target.parentNode;\r\n"+
            "	  this.colorChooser=new ColorPicker(true,toColor(color[0],color[1],color[2],color[3]),getWindow(this.element),true);\r\n"+
            "	  if(this.isAlpha)\r\n"+
            "		  this.colorChooser.setAlphaEnabled();\r\n"+
            "	  this.colorChooser.onCancel=function(e){that.onNoColor(e)};\r\n"+
            "	  this.colorChooser.onColorChanged=function(){that.onChooserChanged()};\r\n"+
            "	  this.colorChooser.onOk=function(e){that.onChooserOk(e)};\r\n"+
            "	  this.colorChooser.onNoColor=function(e){that.onNoColor(e)};\r\n"+
            "	  this.colorChooser.show(rect.getRight(),rect.getBottom());\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "GradientPicker.prototype.buildHandles=function() {\r\n"+
            "	for(var i in this.handles){\r\n"+
            "		this.element.removeChild(this.handles[i]);\r\n"+
            "	}\r\n"+
            "	this.handles=[];\r\n"+
            "	var that=this;\r\n"+
            "	var g=this.gradient;\r\n"+
            "	var len=g.length();\r\n"+
            "	for(var i=0;i<len;i++){\r\n"+
            "		var d=nwDiv('');\r\n"+
            "		if(this.isHorizontal){\r\n"+
            "		  d.style.width=toPx(this.handleSize-2);\r\n"+
            "		  d.style.top='-1px';\r\n"+
            "		  d.style.bottom='-1px';\r\n"+
            "		}else{\r\n"+
            "		  d.style.left='-1px';\r\n"+
            "		  d.style.right='-1px';\r\n"+
            "		  d.style.height=toPx(this.handleSize-2);\r\n"+
            "		}\r\n"+
            "		var c=g.getColorAtStep(i);\r\n"+
            "		d.value=g.getValueAtStep(i);\r\n"+
            "		var dragger=nwDiv('gradientpicker_dragger');\r\n"+
            "		dragger.style.width='100%';\r\n"+
            "		dragger.style.height='100%';\r\n"+
            "        dragger.style.cursor=this.isHorizontal ? \"ew-resize\" : \"ns-resize\";\r\n"+
            "		d.appendChild(dragger);\r\n"+
            "		makeDraggable(dragger,d,!this.isHorizontal,this.isHorizontal);\r\n"+
            "        dragger.ondragging=function(target,x,y,e){that.onDragging(target,x,y,e);};\r\n"+
            "        dragger.ondraggingStart=function(target,x,y,e){that.onDraggingStart(target,e);};\r\n"+
            "        dragger.ondraggingEnd=function(target,x,y,e){that.onDraggingEnd(target,x,y,e);};\r\n"+
            "        dragger.clipDragging=function(e,rect){that.clipDragger(e,rect);};\r\n"+
            "		d.dragger=dragger;\r\n"+
            "		d.color=c;\r\n"+
            "		this.updateHandle(d);\r\n"+
            "		this.handles[i]=d;\r\n"+
            "		this.element.appendChild(d);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "GradientPicker.prototype.updateHandle=function(d) {\r\n"+
            "	d.dragger.style.background=d.color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "GradientPicker.prototype.onDragging=function(target,x,y,e) {\r\n"+
            "	var h=target.parentNode;\r\n"+
            "	var n=this.isHorizontal ? x : y;\r\n"+
            "	var value=between(this.draggingStartValue+((this.isHorizontal ? n : -n)/this.size)*(this.maxValue-this.minValue),this.minValue,this.maxValue);\r\n"+
            "	h.value=value;\r\n"+
            "	this.handles.sort(function(a,b){return a.value-b.value});\r\n"+
            "	this.gradient=this.draggingGradient.clone();\r\n"+
            "	this.gradient.addStep(h.value,h.color);\r\n"+
            "	this.repaint();\r\n"+
            "	this.fireGradientChanged();\r\n"+
            "}\r\n"+
            "GradientPicker.prototype.onDraggingEnd=function(target,x,y,e) {\r\n"+
            "    if(x==0 && y==0){\r\n"+
            "      this.onEdit(e);\r\n"+
            "    }else{\r\n"+
            "	  this.buildHandles();\r\n"+
            "	  this.repaint();\r\n"+
            "	  this.fireGradientChanged();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "GradientPicker.prototype.onDraggingStart=function(target,x,y,e) {\r\n"+
            "	var p=target.parentNode;\r\n"+
            "	this.draggingStartValue=1*target.parentNode.value;\r\n"+
            "	this.draggingGradient=this.gradient.clone();\r\n"+
            "	this.draggingGradient.removeStepByValue(this.draggingStartValue);\r\n"+
            "	this.element.removeChild(p);\r\n"+
            "	this.element.appendChild(p);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GradientPicker.prototype.clipDragger=function(e,rect) {\r\n"+
            "	//rect.setTop(between(rect.getTop(),40-7,340-7));\r\n"+
            "}\r\n"+
            "GradientPicker.prototype.onEdit=function(e) {\r\n"+
            "	var that=this;\r\n"+
            "	var target=getMouseTarget(e);\r\n"+
            "	rect=new Rect();\r\n"+
            "	rect.readFromElement(target);\r\n"+
            "	this.activeHandle=target.parentNode;\r\n"+
            "	this.colorChooser=new ColorPicker(true,target.parentNode.color,getWindow(this.element),true);\r\n"+
            "	  if(this.isAlpha)\r\n"+
            "		  this.colorChooser.setAlphaEnabled();\r\n"+
            "	this.colorChooser.onCancel=function(e){that.onChooserCancel(e)};\r\n"+
            "	this.colorChooser.onColorChanged=function(){that.onChooserChanged()};\r\n"+
            "	this.colorChooser.onOk=function(e){that.onChooserOk(e)};\r\n"+
            "	this.colorChooser.onNoColor=function(e){that.onNoColor(e)};\r\n"+
            "	this.colorChooser.show(rect.getRight(),rect.top);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "GradientPicker.prototype.onChooserCancel=function() {\r\n"+
            "	var color=this.colorChooser.getOrigColor();\r\n"+
            "	this.gradient.addStep(this.activeHandle.value,color);\r\n"+
            "	this.colorChooser.hide();\r\n"+
            "	this.colorChooser=null;\r\n"+
            "	this.activeHandle.color=color;\r\n"+
            "	this.updateHandle(this.activeHandle);\r\n"+
            "	this.repaint();\r\n"+
            "	this.fireGradientChanged();\r\n"+
            "	this.element.focus();\r\n"+
            "}\r\n"+
            "GradientPicker.prototype.onNoColor=function(e) {\r\n"+
            "    this.colorChooser.hide();\r\n"+
            "	this.colorChooser=null;\r\n"+
            "	this.gradient.removeStepByValue(this.activeHandle.value);\r\n"+
            "	this.buildHandles();\r\n"+
            "	this.repaint();\r\n"+
            "	this.fireGradientChanged();\r\n"+
            "	this.element.focus();\r\n"+
            "}\r\n"+
            "GradientPicker.prototype.onChooserOk=function() {\r\n"+
            "	var color=this.colorChooser.getColor();\r\n"+
            "	this.gradient.addStep(this.activeHandle.value,color);\r\n"+
            "	this.colorChooser.hide();\r\n"+
            "	this.colorChooser=null;\r\n"+
            "	this.activeHandle.color=color;\r\n"+
            "	this.updateHandle(this.activeHandle);\r\n"+
            "	this.repaint();\r\n"+
            "	this.fireGradientChanged();\r\n"+
            "	this.element.focus();\r\n"+
            "}\r\n"+
            "\r\n"+
            "GradientPicker.prototype.onChooserChanged=function() {\r\n"+
            "	var color=this.colorChooser.getColor();\r\n"+
            "	this.gradient.addStep(this.activeHandle.value,color);\r\n"+
            "	this.activeHandle.color=color;\r\n"+
            "	this.updateHandle(this.activeHandle);\r\n"+
            "	this.repaint();\r\n"+
            "	this.fireGradientChanged();\r\n"+
            "}\r\n"+
            "\r\n"+
            "GradientPicker.prototype.repaint=function() {\r\n"+
            "	var w=this.canvas.width;\r\n"+
            "	var h=this.height;\r\n"+
            "	var t=this.size-this.handleSize+2;\r\n"+
            "	var c=this.context;\r\n"+
            "	var g=this.gradient;\r\n"+
            "	var len=g.length();\r\n"+
            "	c.clearRect(0,0,w,h);\r\n"+
            "	if(len>0){\r\n"+
            "      var grad = this.context.createLinearGradient(this.isHorizontal ? this.size : 0, this.isHorizontal ? 0 : this.size, 0, 0);\r\n"+
            "      grad.addColorStop(this.isHorizontal ? 1 : 0,g.getColorAtStep(0));\r\n"+
            "      var min=this.minValue;\r\n"+
            "      var max=this.maxValue;\r\n"+
            "      if(min<max){\r\n"+
            "        for(var i=0;i<len;i++){\r\n"+
            "          var v=g.getValueAtStep(i);\r\n"+
            "          if(this.isHorizontal){\r\n"+
            "            grad.addColorStop(1-(v-min) / (max-min) ,g.getColorAtStep(i));\r\n"+
            "            var n=toPx(t*(v-min) / (max-min)-1);\r\n"+
            "            this.handles[i].style.left=n;\r\n"+
            "          } else{\r\n"+
            "            grad.addColorStop((v-min) / (max-min) ,g.getColorAtStep(i));\r\n"+
            "            var n=toPx(t-t*(v-min) / (max-min)-1);\r\n"+
            "            this.handles[i].style.top=n;\r\n"+
            "          }\r\n"+
            "        }  \r\n"+
            "      }\r\n"+
            "      grad.addColorStop(this.isHorizontal ? 0 : 1,g.getColorAtStep(len-1));\r\n"+
            "      c");
          out.print(
            ".fillStyle=grad;\r\n"+
            "      c.fillRect(0,0,w,h);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "GradientPicker.prototype.fireGradientChanged=function() {\r\n"+
            "	if(this.onGradientChanged)\r\n"+
            "		this.onGradientChanged(this);\r\n"+
            "}\r\n"+
            "\r\n"+
            "GradientPicker.prototype.getGradient=function() {\r\n"+
            "	return this.gradient;\r\n"+
            "}\r\n"+
            "GradientPicker.prototype.setGradient=function(g) {\r\n"+
            "	this.gradient=g;\r\n"+
            "	this.buildHandles();\r\n"+
            "	this.repaint();\r\n"+
            "}\r\n"+
            "GradientPicker.prototype.getOrigGradient=function() {\r\n"+
            "	return this.origGradient;\r\n"+
            "}\r\n"+
            "\r\n"+
            "GradientPicker.prototype.setSize=function(width,height) {\r\n"+
            "  this.width=width;\r\n"+
            "  this.height=height;\r\n"+
            "  this.isHorizontal=this.width>=this.height;\r\n"+
            "  this.size=this.isHorizontal ? this.width : this.height;\r\n"+
            "  this.element.style.width=toPx(this.width);\r\n"+
            "  this.element.style.height=toPx(this.height);\r\n"+
            "  this.canvas.width=width-2;\r\n"+
            "  this.canvas.height=height-2;\r\n"+
            "	this.buildHandles();\r\n"+
            "	this.repaint();\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "GradientPicker.prototype.setAlphaEnabled=function(b) {\r\n"+
            "	this.isAlpha=b;\r\n"+
            "}");

	}
	
}