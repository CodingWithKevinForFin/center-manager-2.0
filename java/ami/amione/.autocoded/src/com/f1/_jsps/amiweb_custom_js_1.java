package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_custom_js_1 extends AbstractHttpHandler{

	public amiweb_custom_js_1() {
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
            "var PI2=Math.PI*2;\r\n"+
            "var PI_180=Math.PI/180;\r\n"+
            "\r\n"+
            "var ROTATE_INTERVAL=null;\r\n"+
            "function rotateDiv(a){\r\n"+
            "	var element=getElement(a);\r\n"+
            "	if(!element.deg)\r\n"+
            "	  element.deg=0;\r\n"+
            "	element.speed=1;\r\n"+
            "	ROTATE_INTERVAL=window.setInterval(function(){\r\n"+
            "		element.deg+=element.speed;\r\n"+
            "		if(element.speed<2) element.speed+=.025;\r\n"+
            "		else if(element.speed<4) element.speed+=.012;\r\n"+
            "		rotate(rd(element.deg),element);\r\n"+
            "	}, 25);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function stopRotateDiv(a){\r\n"+
            "	clearInterval(ROTATE_INTERVAL);\r\n"+
            "	ROTATE_INTERVAL=null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function rotate(deg, elmt){\r\n"+
            "	var s=\"rotate(\" + deg + \"deg)\";\r\n"+
            "	elmt.style.transform=s;\r\n"+
            "	elmt.style['-moz-transform']=s;\r\n"+
            "	elmt.style['-o-transform']=s;\r\n"+
            "	elmt.style['-webkit-transform']=s;\r\n"+
            "	elmt.style['-ms-transform']=s;\r\n"+
            "	var size=500+deg*1;\r\n"+
            "	if(size>2500)\r\n"+
            "		size=2500;\r\n"+
            "	elmt.style.backgroundSize=toPx(size)+\" \"+toPx(size);\r\n"+
            "	elmt.style.backgroundPosition=toPx((690-size)/2)+\" \"+toPx((690-size)/2);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "function radiate(cm,oldX,oldA,x,a){\r\n"+
            "  if(oldX==x){\r\n"+
            "	  //cm.moveTo(rd(radiansToX(oldX,oldA)),rd(radiansToY(oldX,oldA)));\r\n"+
            "	  if(a<oldA)\r\n"+
            "	    cm.arc(0,0,oldX,oldA,a,true);\r\n"+
            "	  else\r\n"+
            "	    cm.arc(0,0,oldX,oldA,a,false);\r\n"+
            "  }else{\r\n"+
            "    var parts=cl(Math.abs(a-oldA)/(Math.PI/8));\r\n"+
            "    if(parts==0){\r\n"+
            "      var x0=radiansToX(oldX,oldA);\r\n"+
            "      var y0=radiansToY(oldX,oldA);\r\n"+
            "      var x2=radiansToX(x,a);\r\n"+
            "      var y2=radiansToY(x,a);\r\n"+
            "      //cm.moveTo(x0,y0);\r\n"+
            "      cm.lineTo(x2,y2);\r\n"+
            "    } else if(parts==1){\r\n"+
            "      radiate2(cm,oldX,oldA,x,a);\r\n"+
            "    }else{\r\n"+
            "      var diffA=(a-oldA)/parts;\r\n"+
            "      var diffX=(x-oldX)/parts;\r\n"+
            "      for(var i=0;i<parts;i++){\r\n"+
            "        radiate2(cm,oldX+diffX*i,oldA+diffA*i,oldX+diffX*(i+1),oldA+diffA*(i+1));\r\n"+
            "      }\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "}\r\n"+
            "\r\n"+
            "function radiate2(cm,oldX,oldA,x,a){\r\n"+
            "  var x0=radiansToX(oldX,oldA);\r\n"+
            "  var y0=radiansToY(oldX,oldA);\r\n"+
            "  var x2=radiansToX(x,a);\r\n"+
            "  var y2=radiansToY(x,a);\r\n"+
            "  var angle=(oldA+a)/2;\r\n"+
            "  var ax=(oldX+x)/2;\r\n"+
            "  var x1=radiansToX(ax,angle);\r\n"+
            "  var y1=radiansToY(ax,angle);\r\n"+
            "  var cpX=2*x1-(x0+x2)/2;\r\n"+
            "  var cpY=2*y1-(y0+y2)/2;\r\n"+
            "  //cm.moveTo(x0,y0);\r\n"+
            "  cm.quadTo(cpX,cpY,x2,y2);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function radiansToX(r,a){\r\n"+
            "  return r*Math.cos(a);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function radiansToY(r,a){\r\n"+
            "  return r*Math.sin(a);\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "var AMIGUISERVICES={};\r\n"+
            "function registerGuiService(id,service){\r\n"+
            "//	var thatId=id;\r\n"+
            "	AMIGUISERVICES[id]=service;\r\n"+
            "	if(service.registerAmiGuiServicePeer){\r\n"+
            "		service.registerAmiGuiServicePeer(new AmiGuiServicePeer(id));\r\n"+
            "	}else{\r\n"+
            "		err(\"AMI Gui Service \"+id+\" does not implement member function: registerAmiGuiServicePeer(peer)\");\r\n"+
            "	}\r\n"+
            "		\r\n"+
            "//	service.sendToServer=function(method,data){\r\n"+
            "//		var method=arguments[0];\r\n"+
            "//		var args = Array.prototype.slice.call(arguments);\r\n"+
            "//	}\r\n"+
            "}\r\n"+
            "function callAmiGuiServiceJavascript(id,method,args){\r\n"+
            "	var service=AMIGUISERVICES[id];\r\n"+
            "	if(service){\r\n"+
            "		var fn=service[method];\r\n"+
            "		if(typeof fn ===\"function\"){\r\n"+
            "			fn.apply(service,JSON.parse(args));\r\n"+
            "		}else{\r\n"+
            "		  err(\"AMI Gui Service \"+id+\" does not have function: \"+method);\r\n"+
            "		}\r\n"+
            "	}else{\r\n"+
            "		err(\"AMI Gui Service not found: \"+id+\" (call registerGuiService(...) first\");\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "function callAmiGuiService(id,method,dataArray){\r\n"+
            "	var params={};\r\n"+
            "	params.type=\"service\";\r\n"+
            "	params.subtype=\"guiservice\";\r\n"+
            "	params.serviceId=\"AMI_SERVICE\";\r\n"+
            "	params.guiserviceid=id\r\n"+
            "	params.gsmethod=method;\r\n"+
            "	params.gsdata=JSON.stringify(dataArray);\r\n"+
            "	portletManager.portletAjax(params);\r\n"+
            "}\r\n"+
            "\r\n"+
            "function AmiGuiServicePeer(id){\r\n"+
            "	this.id=id;\r\n"+
            "}\r\n"+
            "\r\n"+
            "AmiGuiServicePeer.prototype.sendToServer=function(method,data){\r\n"+
            "  var args = Array.prototype.slice.call(arguments);\r\n"+
            "  callAmiGuiService(this.id,method,args.splice(1));\r\n"+
            "}\r\n"+
            "\r\n"+
            "function amiJsCallback(target,action,params){\r\n"+
            "  var args = Array.prototype.slice.call(arguments);\r\n"+
            "  if(args.length<2){\r\n"+
            "    alert(\"usage:   aimJsCallback(domObject, customString, ...addtional_args...)\");\r\n"+
            "    return;\r\n"+
            "  }\r\n"+
            "  args=args.splice(1);\r\n"+
            "  while(target.parentNode!=null){\r\n"+
            "    if(target.customCb!=null){\r\n"+
            "      target.customCb('amiCustomCallback',args);\r\n"+
            "      return;\r\n"+
            "    }\r\n"+
            "    target=target.parentNode;\r\n"+
            "  }\r\n"+
            "  alert(\"Not a valid target\");\r\n"+
            "}\r\n"+
            "\r\n"+
            "");

	}
	
}