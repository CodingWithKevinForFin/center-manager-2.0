package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_sparks_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_sparks_js_1() {
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
            "function InlineLineChart(element, text, delim){\r\n"+
            "	this.containerElement = element;\r\n"+
            "	this.containerElement.style.padding=\"0px\";\r\n"+
            "	this.ns = \"http://www.w3.org/2000/svg\";\r\n"+
            "	this.width = 100;\r\n"+
            "	this.height = 100;\r\n"+
            "	this.setText(text, delim);\r\n"+
            "	this.setPadding(2, 2);\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineLineChart.prototype.svg;\r\n"+
            "InlineLineChart.prototype.path;\r\n"+
            "InlineLineChart.prototype.dotsGroup;\r\n"+
            "InlineLineChart.prototype.dots;\r\n"+
            "InlineLineChart.prototype.width;\r\n"+
            "InlineLineChart.prototype.height;\r\n"+
            "InlineLineChart.prototype.paddingX;\r\n"+
            "InlineLineChart.prototype.paddingY;\r\n"+
            "InlineLineChart.prototype.graphData;\r\n"+
            "InlineLineChart.prototype.originalText;\r\n"+
            "InlineLineChart.prototype.delim;\r\n"+
            "\r\n"+
            "InlineLineChart.prototype.setText=function(text, delim){\r\n"+
            "	this.originalText = text == null? \"\" : text;\r\n"+
            "	this.delim = delim;\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineLineChart.prototype.drawSvg=function(width, height){\r\n"+
            "	if(this.svg && this.parentNode==this.containerElement){\r\n"+
            "		this.containerElement.removeChild(this.svg);\r\n"+
            "	}\r\n"+
            "	this.width = width;\r\n"+
            "	this.height = height;\r\n"+
            "	this.graphData = this.prepareData(this.originalText, this.delim);\r\n"+
            "	if(this.graphData.length == 0){\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	this.containerElement.textContent= \"\";\r\n"+
            "	this.svg = document.createElementNS(this.ns, \"svg\");\r\n"+
            "	this.svg.setAttribute(\"width\", width);\r\n"+
            "	this.svg.setAttribute(\"height\", height);\r\n"+
            "	this.svg.setAttribute(\"preserveAspectRatio\", \"none\");\r\n"+
            "	this.svg.setAttribute(\"xmlns\", this.ns);\r\n"+
            "	this.svg.setAttribute(\"fill\", \"none\");\r\n"+
            "	this.color = this.containerElement.style.color;\r\n"+
            "	this.color = this.color === \"\"? \"black\" : this.color;\r\n"+
            "	this.path = this.createSvgPath(this.ns, this.graphData, this.svg);\r\n"+
            "	this.dotsGroup = this.createSvgGroup(this.ns, this.svg);\r\n"+
            "	this.dotsGroup.setAttribute(\"fill\", this.color);\r\n"+
            "	this.dots = this.createSvgDots(this.ns, this.graphData, this.dotsGroup);\r\n"+
            "	\r\n"+
            "	this.containerElement.appendChild(this.svg);\r\n"+
            "}\r\n"+
            "InlineLineChart.prototype.createSvgPath=function(ns, data, parent) {\r\n"+
            "	var path = document.createElementNS(ns, 'path');\r\n"+
            "\r\n"+
            "	var p = \"\";\r\n"+
            "	for (var i = 0; i < data.length; i++) {\r\n"+
            "		if (i == 0) {\r\n"+
            "			p += \"M\" + (fl(data[i].x)+.5) + \" \" + (fl(data[i].y)+.5);\r\n"+
            "		} else {\r\n"+
            "			p += \" L\" + (fl(data[i].x)+.5) + \" \" + (fl(data[i].y)+.5);\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	path.setAttribute(\"d\", p);\r\n"+
            "	path.setAttribute(\"stroke\", this.color);\r\n"+
            "	path.setAttribute(\"stroke-width\", \"1\");\r\n"+
            "	parent.appendChild(path);\r\n"+
            "	return path;\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineLineChart.prototype.createSvgGroup=function(ns, parent) {\r\n"+
            "	var group = document.createElementNS(ns, 'g');\r\n"+
            "	parent.appendChild(group);\r\n"+
            "	return group;\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineLineChart.prototype.createSvgDots=function(ns, data, parent) {\r\n"+
            "	var dots = [];\r\n"+
            "	for (var i = 0; i < data.length; i++) {\r\n"+
            "		var dot = document.createElementNS(ns, 'circle');\r\n"+
            "		var title = document.createElementNS(ns, \"title\");\r\n"+
            "		title.textContent = data[i].d;\r\n"+
            "		dot.setAttribute(\"cx\", fl(data[i].x) + 0.5);\r\n"+
            "		dot.setAttribute(\"cy\", fl(data[i].y) + 0.5);\r\n"+
            "		dot.setAttribute(\"r\", \"1.5\");\r\n"+
            "		\r\n"+
            "		dot.appendChild(title);\r\n"+
            "		parent.appendChild(dot);\r\n"+
            "		dots.push(dot);\r\n"+
            "	}\r\n"+
            "	return dots;\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineLineChart.prototype.setPadding=function(paddingX, paddingY){\r\n"+
            "	this.paddingX = paddingX;\r\n"+
            "	this.paddingY = paddingY;\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineLineChart.prototype.prepareData=function(text, delimiter){\r\n"+
            "	var arr = text.split(delimiter);\r\n"+
            "	var width = Math.max(this.width - 2* this.paddingX, 0);\r\n"+
            "	var height = Math.max(this.height - 2* this.paddingY, 0);\r\n"+
            "	\r\n"+
            "	// Convert to array of numbers\r\n"+
            "	var nums = [];\r\n"+
            "	for(var i = 0; i < arr.length; i++){\r\n"+
            "		var tr = arr[i].trim();\r\n"+
            "		var n = Number(tr);\r\n"+
            "		if(tr === \"\" || isNaN(n)){\r\n"+
            "			return [];\r\n"+
            "		}else{\r\n"+
            "			nums.push(Number(arr[i]));\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	//Min Max\r\n"+
            "	var max = Math.max.apply(Math, nums);\r\n"+
            "	var min = Math.min.apply(Math, nums);\r\n"+
            "	var dif = max - min;\r\n"+
            "	\r\n"+
            "	//Generate Line\r\n"+
            "	var data = [];\r\n"+
            "	var x = this.paddingX;\r\n"+
            "	for (var i = 0; i < nums.length; i++){\r\n"+
            "		var invertedRatio = dif == 0? 0.5 : (max - nums[i])/dif;\r\n"+
            "		var y = height * invertedRatio + this.paddingY;\r\n"+
            "		var point = {};\r\n"+
            "		point.x = x;\r\n"+
            "		point.y = y;\r\n"+
            "		point.d = nums[i];\r\n"+
            "		x += width / (nums.length -1);\r\n"+
            "		data.push(point);\r\n"+
            "	}\r\n"+
            "	return data;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function InlineCandleStickChart(element, text, delim){\r\n"+
            "	this.containerElement = element;\r\n"+
            "	\r\n"+
            "	this.ns = \"http://www.w3.org/2000/svg\";\r\n"+
            "	\r\n"+
            "	this.svg = document.createElementNS(this.ns, \"svg\");\r\n"+
            "	this.setPadding(6,4);\r\n"+
            "	this.candleWidth = 8;\r\n"+
            "	this.svg.setAttribute(\"fill\", \"none\");\r\n"+
            "	this.graphData = this.prepareData(text, delim, 4);\r\n"+
            "	this.originalText = element.textContent;\r\n"+
            "	element.textContent= \"\";\r\n"+
            "	this.createCandleSticks(this.ns, this.graphData, this.svg);\r\n"+
            "	\r\n"+
            "	this.containerElement.appendChild(this.svg);\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineCandleStickChart.prototype.svg;\r\n"+
            "InlineCandleStickChart.prototype.width;\r\n"+
            "InlineCandleStickChart.prototype.height;\r\n"+
            "InlineCandleStickChart.prototype.candleWidth;\r\n"+
            "InlineCandleStickChart.prototype.paddingX;\r\n"+
            "InlineCandleStickChart.prototype.paddingY;\r\n"+
            "InlineCandleStickChart.prototype.graphData;\r\n"+
            "InlineCandleStickChart.prototype.originalText;\r\n"+
            "\r\n"+
            "InlineCandleStickChart.prototype.createCandleSticks=function(ns, data, parent){\r\n"+
            "	var candleSticks = [];\r\n"+
            "	for(var i = 0; i < data.length; i++){\r\n"+
            "		candleSticks.push(this.createCandleStick(ns, data[i], parent));\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	return candleSticks;\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineCandleStickChart.prototype.createCandleStick=function(ns, data, parent){\r\n"+
            "	var candleStick = document.createElementNS(ns, 'g');\r\n"+
            "	var candle = document.createElementNS(ns, 'rect');\r\n"+
            "	var stick = document.createElementNS(ns, \"path\");\r\n"+
            "	\r\n"+
            "	// Candle\r\n"+
            "		candle.setAttribute(\"x\", data.left);\r\n"+
            "		candle.setAttribute(\"y\", data.topY);\r\n"+
            "		candle.setAttribute(\"width\", this.candleWidth);\r\n"+
            "		candle.setAttribute(\"height\", data.candleHeight);\r\n"+
            "		candle.setAttribute(\"stroke-width\", \"0.5\");\r\n"+
            "		candle.setAttribute(\"stroke\", \"black\");\r\n"+
            "	\r\n"+
            "	var color = \"\";\r\n"+
            "	if(data.close > data.open)\r\n"+
            "		color = \"#b1d68f\";\r\n"+
            "	else\r\n"+
            "		color = \"#ff6666\";\r\n"+
            "	//	color = \"rgba(0,0,0,0)\";\r\n"+
            "	candle.setAttribute(\"fill\", color);\r\n"+
            "	\r\n"+
            "	\r\n"+
            "	// Stick\r\n"+
            "	var d = \"\";\r\n"+
            "	d+= \"M\" + data.x + \" \" + data.minY + \" L\" + data.x + \" \" + data.botY;\r\n"+
            "	d+= \" M\" + data.x + \" \" + data.maxY + \" L\" + data.x + \" \" + data.topY;\r\n"+
            "	d+= \" M\" + data.left + \" \" + data.maxY + \" L\" + data.right + \" \" + data.maxY;\r\n"+
            "	d+= \" M\" + data.left + \" \" + data.minY + \" L\" + data.right + \" \" + data.minY;\r\n"+
            "	\r\n"+
            "	\r\n"+
            "	stick.setAttribute(\"d\", d);\r\n"+
            "	stick.setAttribute(\"stroke\", \"black\");\r\n"+
            "	stick.setAttribute(\"stroke-width\", \"1\");\r\n"+
            "	\r\n"+
            "	candleStick.appendChild(stick);\r\n"+
            "	candleStick.appendChild(candle);\r\n"+
            "	parent.appendChild(candleStick);\r\n"+
            "	\r\n"+
            "	var ret = {};\r\n"+
            "	ret.candleStick = candleStick;\r\n"+
            "	ret.candle = candle;\r\n"+
            "	ret.stick = stick;\r\n"+
            "	\r\n"+
            "	return ret;\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineCandleStickChart.prototype.prepareData=function(text, delimiter, groupSize){\r\n"+
            "	var arr = text.split(delimiter);\r\n"+
            "	// Convert to array of numbers\r\n"+
            "	\r\n"+
            "	var nums = [];\r\n"+
            "	for(var i = 0; i < arr.length; i++){\r\n"+
            "		nums.push(Number(arr[i]));\r\n"+
            "	}\r\n"+
            "\r\n"+
            "	//Min Max\r\n"+
            "	var max = Math.max.apply(Math, nums);\r\n"+
            "	var min = Math.min.apply(Math, nums);\r\n"+
            "	var dif = max - min;\r\n"+
            "	\r\n"+
            "	//Generate Data\r\n"+
            "	var data = [];\r\n"+
            "	var count = Math.ceil(nums.length/groupSize);\r\n"+
            "	var x = this.paddingX;\r\n"+
            "	for(var i = 0; i < count; i++){\r\n"+
            "		var begin = i * groupSize;\r\n"+
            "		var end = (i+1) * groupSize;\r\n"+
            "		if(end > nums.length)\r\n"+
            "			end = nums.length;\r\n"+
            "		\r\n"+
            "		var result = {};\r\n"+
            "		result.count = end - begin;\r\n"+
            "		result.open = nums[begin];\r\n"+
            "		result.close = nums[end-1];\r\n"+
            "		result.high = Math.max.apply(Math, nums.slice(begin, end));\r\n"+
            "		result.low = Math.min.apply(Math, nums.slice(begin, end));\r\n"+
            "		result.x = x;\r\n"+
            "		result.candleHeight = Math.abs(result.open - result.close);\r\n"+
            "		result.left = x - this.candleWidth/2;\r\n"+
            "		result.right = x + this.candleWidth/2;\r\n"+
            "		if(dif == 0){\r\n"+
            "			result.maxY = 0.5 * this.height;\r\n"+
            "			result.minY = 0.5 * this.height;\r\n"+
            "			result.topY = 0.5 * this.height;\r\n"+
            "			result.botY = 0.5 * this.height;\r\n"+
            "			result.candleHeight = 0;\r\n"+
            "		}else{\r\n"+
            "			result.maxY = this.height * (max - result.high)/dif + this.paddingY;\r\n"+
            "			result.minY = this.height * (max - result.low)/dif + this.paddingY;\r\n"+
            "			\r\n"+
            "			var openY = this.height * (max - result.open)/dif + this.paddingY;\r\n"+
            "			var closeY = this.height * (max - result.close)/dif + this.paddingY;\r\n"+
            "			\r\n"+
            "			result.topY = openY < closeY? openY : closeY;\r\n"+
            "			result.botY = openY > closeY? openY : closeY;\r\n"+
            "			result.candleHeight = this.height * result.candleHeight/dif;\r\n"+
            "		}\r\n"+
            "		x+= this.width/(count);\r\n"+
            "		data.push(result);\r\n"+
            "	}\r\n"+
            "	return data;\r\n"+
            "}\r\n"+
            "\r\n"+
            "InlineCandleStickChart.prototype.setPadding=function(paddingX, paddingY){\r\n"+
            "	this.paddingX = paddingX;\r\n"+
            "	this.paddingY = paddingY;\r\n"+
            "	\r\n"+
            "	var r = this.containerElement.getBoundingClientRect();\r\n"+
            "	this.svg.setAttribute(\"width\", r.width);\r\n"+
            "	this.svg.setAttribute(\"height\", r.height);\r\n"+
            "	\r\n"+
            "	this.width = r.width- 2*paddingX;\r\n"+
            "	this.height = r.height - 2*paddingY;\r\n"+
            "	this.width = this.width < 0 ? 0: this.width;\r\n"+
            "	this.height = this.height < 0 ? 0: this.height;\r\n"+
            "}\r\n"+
            "");

	}
	
}