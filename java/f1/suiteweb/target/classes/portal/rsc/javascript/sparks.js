function InlineLineChart(element, text, delim){
	this.containerElement = element;
	this.containerElement.style.padding="0px";
	this.ns = "http://www.w3.org/2000/svg";
	this.width = 100;
	this.height = 100;
	this.setText(text, delim);
	this.setPadding(2, 2);
}

InlineLineChart.prototype.svg;
InlineLineChart.prototype.path;
InlineLineChart.prototype.dotsGroup;
InlineLineChart.prototype.dots;
InlineLineChart.prototype.width;
InlineLineChart.prototype.height;
InlineLineChart.prototype.paddingX;
InlineLineChart.prototype.paddingY;
InlineLineChart.prototype.graphData;
InlineLineChart.prototype.originalText;
InlineLineChart.prototype.delim;

InlineLineChart.prototype.setText=function(text, delim){
	this.originalText = text == null? "" : text;
	this.delim = delim;
}

InlineLineChart.prototype.drawSvg=function(width, height){
	if(this.svg && this.parentNode==this.containerElement){
		this.containerElement.removeChild(this.svg);
	}
	this.width = width;
	this.height = height;
	this.graphData = this.prepareData(this.originalText, this.delim);
	if(this.graphData.length == 0){
		return;
	}
	this.containerElement.textContent= "";
	this.svg = document.createElementNS(this.ns, "svg");
	this.svg.setAttribute("width", width);
	this.svg.setAttribute("height", height);
	this.svg.setAttribute("preserveAspectRatio", "none");
	this.svg.setAttribute("xmlns", this.ns);
	this.svg.setAttribute("fill", "none");
	this.color = this.containerElement.style.color;
	this.color = this.color === ""? "black" : this.color;
	this.path = this.createSvgPath(this.ns, this.graphData, this.svg);
	this.dotsGroup = this.createSvgGroup(this.ns, this.svg);
	this.dotsGroup.setAttribute("fill", this.color);
	this.dots = this.createSvgDots(this.ns, this.graphData, this.dotsGroup);
	
	this.containerElement.appendChild(this.svg);
}
InlineLineChart.prototype.createSvgPath=function(ns, data, parent) {
	var path = document.createElementNS(ns, 'path');

	var p = "";
	for (var i = 0; i < data.length; i++) {
		if (i == 0) {
			p += "M" + (fl(data[i].x)+.5) + " " + (fl(data[i].y)+.5);
		} else {
			p += " L" + (fl(data[i].x)+.5) + " " + (fl(data[i].y)+.5);
		}
	}
	path.setAttribute("d", p);
	path.setAttribute("stroke", this.color);
	path.setAttribute("stroke-width", "1");
	parent.appendChild(path);
	return path;
}

InlineLineChart.prototype.createSvgGroup=function(ns, parent) {
	var group = document.createElementNS(ns, 'g');
	parent.appendChild(group);
	return group;
}

InlineLineChart.prototype.createSvgDots=function(ns, data, parent) {
	var dots = [];
	for (var i = 0; i < data.length; i++) {
		var dot = document.createElementNS(ns, 'circle');
		var title = document.createElementNS(ns, "title");
		title.textContent = data[i].d;
		dot.setAttribute("cx", fl(data[i].x) + 0.5);
		dot.setAttribute("cy", fl(data[i].y) + 0.5);
		dot.setAttribute("r", "1.5");
		
		dot.appendChild(title);
		parent.appendChild(dot);
		dots.push(dot);
	}
	return dots;
}

InlineLineChart.prototype.setPadding=function(paddingX, paddingY){
	this.paddingX = paddingX;
	this.paddingY = paddingY;
}

InlineLineChart.prototype.prepareData=function(text, delimiter){
	var arr = text.split(delimiter);
	var width = Math.max(this.width - 2* this.paddingX, 0);
	var height = Math.max(this.height - 2* this.paddingY, 0);
	
	// Convert to array of numbers
	var nums = [];
	for(var i = 0; i < arr.length; i++){
		var tr = arr[i].trim();
		var n = Number(tr);
		if(tr === "" || isNaN(n)){
			return [];
		}else{
			nums.push(Number(arr[i]));
		}
	}
	
	//Min Max
	var max = Math.max.apply(Math, nums);
	var min = Math.min.apply(Math, nums);
	var dif = max - min;
	
	//Generate Line
	var data = [];
	var x = this.paddingX;
	for (var i = 0; i < nums.length; i++){
		var invertedRatio = dif == 0? 0.5 : (max - nums[i])/dif;
		var y = height * invertedRatio + this.paddingY;
		var point = {};
		point.x = x;
		point.y = y;
		point.d = nums[i];
		x += width / (nums.length -1);
		data.push(point);
	}
	return data;
}

function InlineCandleStickChart(element, text, delim){
	this.containerElement = element;
	
	this.ns = "http://www.w3.org/2000/svg";
	
	this.svg = document.createElementNS(this.ns, "svg");
	this.setPadding(6,4);
	this.candleWidth = 8;
	this.svg.setAttribute("fill", "none");
	this.graphData = this.prepareData(text, delim, 4);
	this.originalText = element.textContent;
	element.textContent= "";
	this.createCandleSticks(this.ns, this.graphData, this.svg);
	
	this.containerElement.appendChild(this.svg);
}

InlineCandleStickChart.prototype.svg;
InlineCandleStickChart.prototype.width;
InlineCandleStickChart.prototype.height;
InlineCandleStickChart.prototype.candleWidth;
InlineCandleStickChart.prototype.paddingX;
InlineCandleStickChart.prototype.paddingY;
InlineCandleStickChart.prototype.graphData;
InlineCandleStickChart.prototype.originalText;

InlineCandleStickChart.prototype.createCandleSticks=function(ns, data, parent){
	var candleSticks = [];
	for(var i = 0; i < data.length; i++){
		candleSticks.push(this.createCandleStick(ns, data[i], parent));
	}
	
	return candleSticks;
}

InlineCandleStickChart.prototype.createCandleStick=function(ns, data, parent){
	var candleStick = document.createElementNS(ns, 'g');
	var candle = document.createElementNS(ns, 'rect');
	var stick = document.createElementNS(ns, "path");
	
	// Candle
		candle.setAttribute("x", data.left);
		candle.setAttribute("y", data.topY);
		candle.setAttribute("width", this.candleWidth);
		candle.setAttribute("height", data.candleHeight);
		candle.setAttribute("stroke-width", "0.5");
		candle.setAttribute("stroke", "black");
	
	var color = "";
	if(data.close > data.open)
		color = "#b1d68f";
	else
		color = "#ff6666";
	//	color = "rgba(0,0,0,0)";
	candle.setAttribute("fill", color);
	
	
	// Stick
	var d = "";
	d+= "M" + data.x + " " + data.minY + " L" + data.x + " " + data.botY;
	d+= " M" + data.x + " " + data.maxY + " L" + data.x + " " + data.topY;
	d+= " M" + data.left + " " + data.maxY + " L" + data.right + " " + data.maxY;
	d+= " M" + data.left + " " + data.minY + " L" + data.right + " " + data.minY;
	
	
	stick.setAttribute("d", d);
	stick.setAttribute("stroke", "black");
	stick.setAttribute("stroke-width", "1");
	
	candleStick.appendChild(stick);
	candleStick.appendChild(candle);
	parent.appendChild(candleStick);
	
	var ret = {};
	ret.candleStick = candleStick;
	ret.candle = candle;
	ret.stick = stick;
	
	return ret;
}

InlineCandleStickChart.prototype.prepareData=function(text, delimiter, groupSize){
	var arr = text.split(delimiter);
	// Convert to array of numbers
	
	var nums = [];
	for(var i = 0; i < arr.length; i++){
		nums.push(Number(arr[i]));
	}

	//Min Max
	var max = Math.max.apply(Math, nums);
	var min = Math.min.apply(Math, nums);
	var dif = max - min;
	
	//Generate Data
	var data = [];
	var count = Math.ceil(nums.length/groupSize);
	var x = this.paddingX;
	for(var i = 0; i < count; i++){
		var begin = i * groupSize;
		var end = (i+1) * groupSize;
		if(end > nums.length)
			end = nums.length;
		
		var result = {};
		result.count = end - begin;
		result.open = nums[begin];
		result.close = nums[end-1];
		result.high = Math.max.apply(Math, nums.slice(begin, end));
		result.low = Math.min.apply(Math, nums.slice(begin, end));
		result.x = x;
		result.candleHeight = Math.abs(result.open - result.close);
		result.left = x - this.candleWidth/2;
		result.right = x + this.candleWidth/2;
		if(dif == 0){
			result.maxY = 0.5 * this.height;
			result.minY = 0.5 * this.height;
			result.topY = 0.5 * this.height;
			result.botY = 0.5 * this.height;
			result.candleHeight = 0;
		}else{
			result.maxY = this.height * (max - result.high)/dif + this.paddingY;
			result.minY = this.height * (max - result.low)/dif + this.paddingY;
			
			var openY = this.height * (max - result.open)/dif + this.paddingY;
			var closeY = this.height * (max - result.close)/dif + this.paddingY;
			
			result.topY = openY < closeY? openY : closeY;
			result.botY = openY > closeY? openY : closeY;
			result.candleHeight = this.height * result.candleHeight/dif;
		}
		x+= this.width/(count);
		data.push(result);
	}
	return data;
}

InlineCandleStickChart.prototype.setPadding=function(paddingX, paddingY){
	this.paddingX = paddingX;
	this.paddingY = paddingY;
	
	var r = this.containerElement.getBoundingClientRect();
	this.svg.setAttribute("width", r.width);
	this.svg.setAttribute("height", r.height);
	
	this.width = r.width- 2*paddingX;
	this.height = r.height - 2*paddingY;
	this.width = this.width < 0 ? 0: this.width;
	this.height = this.height < 0 ? 0: this.height;
}
