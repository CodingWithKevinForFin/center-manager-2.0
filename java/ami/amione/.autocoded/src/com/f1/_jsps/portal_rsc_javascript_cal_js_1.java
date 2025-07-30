package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_rsc_javascript_cal_js_1 extends AbstractHttpHandler{

	public portal_rsc_javascript_cal_js_1() {
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
            "function DateChooser(parentDiv,isRange){ \r\n"+
            "  var that=this; \r\n"+
            "  this.parentDiv=parentDiv; \r\n"+
            "  this.parentDiv.style.width=toPx(isRange ? 180 : 80);\r\n"+
            "  this.window = getWindow(this.parentDiv);\r\n"+
            "  this.today=new Date(); \r\n"+
            "  var thisYear=this.today.getFullYear();; \r\n"+
            "  var thisMonth=this.today.getMonth(); \r\n"+
            "  var thisDate=this.today.getDate(); \r\n"+
            "  this.monthContainer;\r\n"+
            "  this.curCalendar;\r\n"+
            "  this.year=this.today.getFullYear();\r\n"+
            "  this.thisYmd=this.toYmd(thisYear,thisMonth+1,thisDate); \r\n"+
            "  this.selYear=0; \r\n"+
            "  this.selMonth=0; \r\n"+
            "  this.selDate=0; \r\n"+
            "  this.selYmd=0; \r\n"+
            "  this.isRange=isRange;\r\n"+
            "  if(this.isRange){ \r\n"+
            "    this.selYear2=0; \r\n"+
            "    this.selMonth2=0; \r\n"+
            "    this.selDate2=0; \r\n"+
            "    this.selYmd2=0; \r\n"+
            "  } \r\n"+
            "  this.input=nw('input','cal_input'); \r\n"+
            "  this.input.readOnly = true;\r\n"+
            "  this.input.size=8; \r\n"+
            "  this.input.onclick=function(e){that.onClickInput(e,1);}; \r\n"+
            "  this.input.onkeyup=function(e){that.onKeyUpInput(e,1);}; \r\n"+
            "  this.input.onkeydown=function(e){that.onKeyDownInput(e,1);};\r\n"+
            "  this.input.onchange=function(e){that.onChange();};\r\n"+
            "  parentDiv.appendChild(this.input); \r\n"+
            "  if(this.isRange){ \r\n"+
            "    this.dash=nw('span');\r\n"+
            "    this.dash.innerHTML=' - '; \r\n"+
            "    this.dash.className='cal_dash';\r\n"+
            "    this.dash.onclick=function(e){that.setValue('');that.setValue2('');that.onClickInput(e,1);that.onDashClicked(e);};\r\n"+
            "    parentDiv.appendChild(this.dash); \r\n"+
            "    this.input2=nw('input','cal_input'); \r\n"+
            "  	this.input2.readOnly = true;\r\n"+
            "    this.input2.size=8; \r\n"+
            "    this.input2.onclick=function(e){that.onClickInput(e,2);}; \r\n"+
            "    this.input2.onkeyup=function(e){that.onKeyUpInput(e,2);}; \r\n"+
            "  	this.input2.onkeydown=function(e){that.onKeyDownInput(e,2);};\r\n"+
            "    this.input2.onchange=function(e){that.onChange();};\r\n"+
            "    parentDiv.appendChild(this.input2); \r\n"+
            "  }\r\n"+
            " 	\r\n"+
            "  this.activeInput = null;\r\n"+
            "  this.activeYear = null;\r\n"+
            "  this.activeMonth = null;\r\n"+
            "  this.activeDate = null;\r\n"+
            "  // onEdit\r\n"+
            "  this.isOnEdit = false; \r\n"+
            "  this.enableLastNDays=null;\r\n"+
            "  this.disableFutureDays;\r\n"+
            "  this.enableLastNDaysEnd=null;\r\n"+
            "  this.disableFutureDaysEnd;\r\n"+
            "  this.style=new Object();\r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.onDashClicked=function(e){ \r\n"+
            "	// override\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.close=function(){ \r\n"+
            "	this.hide();\r\n"+
            "	if(this.input!=null)\r\n"+
            "		parentDiv.removeChild(this.input);\r\n"+
            "	if(this.input2!=null)\r\n"+
            "		parentDiv.removeChild(this.input2);\r\n"+
            "}\r\n"+
            "DateChooser.prototype.setSelectedToday=function(){ \r\n"+
            "  this.setSelected(this.today.getFullYear() ,this.today.getMonth()+1 ,this.today.getDate()); \r\n"+
            "} \r\n"+
            "DateChooser.prototype.setSelected=function(year,month,day){ \r\n"+
            "  this.selYear=year; \r\n"+
            "  this.selMonth=month-1; \r\n"+
            "  this.selDate=day; \r\n"+
            "  this.selYmd=this.toYmd(year,month,day); \r\n"+
            "  if(this.rootDiv==null && this.selYmd>0) {\r\n"+
            "	var selDate=new Date(this.selYear,this.selMonth,this.selDate);\r\n"+
            "	var timeMs= selDate.getTime();\r\n"+
            "	var ret=formatDate(this.dateFormat,selDate,timeMs);\r\n"+
            "	var newText=ret?ret:this.selYmd;\r\n"+
            "	const oVal=this.input.value;\r\n"+
            "	this.input.value=newText; \r\n"+
            "//	if (oVal!=newText)\r\n"+
            "//		this.ensureFieldsVisible();\r\n"+
            "  }\r\n"+
            "  else if (this.selYmd==0) {\r\n"+
            "	  this.input.value='';\r\n"+
            "  }\r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.setDateDisplayFormat=function(format){ \r\n"+
            "	this.dateFormat=format;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setSelected2=function(year,month,day){ \r\n"+
            "  this.selYear2=year; \r\n"+
            "  this.selMonth2=month-1; \r\n"+
            "  this.selDate2=day; \r\n"+
            "  this.selYmd2=this.toYmd(year,month,day); \r\n"+
            "  if(this.rootDiv==null && this.selYmd2>0) {\r\n"+
            "	var selDate=new Date(this.selYear2,this.selMonth2,this.selDate2);\r\n"+
            "	var timeMs= selDate.getTime();\r\n"+
            "	var ret=formatDate(this.dateFormat,selDate,timeMs);\r\n"+
            "	const oVal=this.input2.value;\r\n"+
            "	var newText=ret?ret:this.selYmd2;\r\n"+
            "	this.input2.value=newText; \r\n"+
            "//	if (oVal!=newText)\r\n"+
            "//		this.ensureFieldsVisible();\r\n"+
            "  } \r\n"+
            "  else if (this.selYmd2==0) {\r\n"+
            "	  this.input2.value='';\r\n"+
            "  }\r\n"+
            "} \r\n"+
            "DateChooser.prototype.setColors=function(headerColor){ \r\n"+
            "	this.headerColor = headerColor;\r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.onGlass=function(){ \r\n"+
            "	// override\r\n"+
            "	// happens when user clicks outside the calendar\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.showCalendar=function(){ \r\n"+
            "  var that=this; \r\n"+
            "  this.dayDivs=[]; \r\n"+
            "  var yearHeight=32; \r\n"+
            "  var yearWidth=100; \r\n"+
            "  var yearButtonHeight=32; \r\n"+
            "  var yearButtonWidth=25; \r\n"+
            "  var yearTopOffset=5;\r\n"+
            "//  var dayHeight=17; \r\n"+
            "//  var dayWidth=18; \r\n"+
            "  var dayHeight=19; \r\n"+
            "  var dayWidth=25; \r\n"+
            "  var monthWidth=dayWidth*7; \r\n"+
            "  var monthHeight=dayHeight*8; \r\n"+
            "  var monthPadding=5; \r\n"+
            "  var otherYearWidth=18;\r\n"+
            "  var calPadding=5;\r\n"+
            "  var calWidth=(monthWidth+monthPadding)*2+ 2*calPadding;\r\n"+
            "  var calHeight=(monthHeight+monthPadding)*2;\r\n"+
            " // var yearsBarWidth = calWidth-2*calPadding-2*26;\r\n"+
            "  var yearsBarWidth = 295;\r\n"+
            "\r\n"+
            "  var thisYear=this.today.getFullYear();\r\n"+
            "  var curYear=this.activeYear > 0  ? this.activeYear:thisYear;\r\n"+
            "  var thisMonth=this.today.getMonth(); \r\n"+
            "  var thisDate=this.today.getDate(); \r\n"+
            "  if(this.rootDiv==null){ \r\n"+
            "	var rect=new Rect();\r\n"+
            "	rect.readFromElement(this.input);\r\n"+
            "    this.rootDiv=nwDiv('cal_calendar',rect.getLeft(),rect.getTop()+rect.height,calWidth,calHeight); \r\n"+
            "    this.glass = new Glass(this.window,this);\r\n"+
            "    this.glass.appendChild(this.rootDiv);\r\n"+
            "	this.glass.glassElement.onclick = function(e){\r\n"+
            "		// event hook\r\n"+
            "		// rootDiv should always exist when this fires\r\n"+
            "		that.onGlass(e);\r\n"+
            "	}\r\n"+
            "    this.glass.show();\r\n"+
            "  } \r\n"+
            "  var headerWidth = calWidth-2*calPadding;\r\n"+
            "  var yearBarLeft = (headerWidth-yearsBarWidth)/2;\r\n"+
            "  var headerDiv = nwDiv('cal_header', calPadding,calPadding, headerWidth, yearHeight);\r\n"+
            "  \r\n"+
            "  var yearsBarDiv = nwDiv('cal_yearsBar', yearBarLeft, 0, yearsBarWidth, yearHeight);\r\n"+
            "  this.monthsBarDiv = nwDiv('cal_monthsBar', 5, 40, calWidth-2*calPadding, 50);\r\n"+
            "  this.monthContainer = nwDiv('cal_monthContainer', 5, 100, calWidth-2*calPadding, 160); \r\n"+
            "  \r\n"+
            "  \r\n"+
            "  if (this.style.calendarBgColor) {\r\n"+
            "	  this.rootDiv.style.background= this.style.calendarBgColor;\r\n"+
            "	  headerDiv.style.background = this.style.calendarBgColor; \r\n"+
            "	  yearsBarDiv.style.background = this.style.calendarBgColor;\r\n"+
            "  } else\r\n"+
            "	  headerDiv.style.background = \"transparent\";\r\n"+
            "  this.rootDiv.appendChild(headerDiv);\r\n"+
            "  this.rootDiv.appendChild(this.monthsBarDiv);\r\n"+
            "  this.rootDiv.appendChild(this.monthContainer);\r\n"+
            "  var yearDiv=nwDiv('cal_year',(yearsBarWidth-yearWidth)/2,yearTopOffset,yearWidth,yearHeight); \r\n"+
            "  yearDiv.innerHTML=curYear;\r\n"+
            "  yearsBarDiv.appendChild(yearDiv);\r\n"+
            "  if(thisYear==this.activeYear) \r\n"+
            "    yearDiv.className+=' cal_today'; \r\n"+
            "  var yearBackButton=nwDiv('cal_yearButton cal_prev',0,0,yearButtonWidth,yearButtonHeight); \r\n"+
            "  var yearNextButton=nwDiv('cal_yearButton cal_next',(calWidth-2*calPadding) - yearButtonWidth - 0,0,yearButtonWidth,yearButtonHeight); \r\n"+
            "  if (this.style.selYearFgColor)\r\n"+
            "	  yearDiv.style.color=this.style.selYearFgColor;\r\n"+
            "  yearBackButton.innerHTML=''; \r\n"+
            "  yearNextButton.innerHTML=''; \r\n"+
            "  //yearBackButton.onclick=function(){that.setActiveYear(that.year-1)}; \r\n"+
            "  //yearNextButton.onclick=function(){that.setActiveYear(that.year+1)}; \r\n"+
            "  headerDiv.appendChild(yearsBarDiv);\r\n"+
            "  headerDiv.appendChild(yearBackButton); \r\n"+
            "  headerDiv.appendChild(yearNextButton); \r\n"+
            "  \r\n"+
            "  ensureInWindow(this.rootDiv);\r\n"+
            "	\r\n"+
            "  // years	\r\n"+
            "  var btns=Math.floor((calWidth-yearButtonWidth*2)/otherYearWidth/2)-1;\r\n"+
            "  for(var i=-btns;i<=btns;i++){\r\n"+
            "    if(i==0) continue;\r\n"+
            "    var btn=nwDiv('otherYear',yearsBarWidth/2+ (i*otherYearWidth)-yearHeight/2+(i<0 ? -10 : 10),yearTopOffset,yearHeight,yearHeight);\r\n"+
            "    if (this.style.yearFgColor) {\r\n"+
            "    	btn.style.color=this.style.yearFgColor;\r\n"+
            "    }\r\n"+
            "    var y=parseInt(curYear)+i;\r\n"+
            "    if(y==thisYear)\r\n"+
            "      btn.className+=' cal_today';\r\n"+
            "    btn.onclick=function(e){that.setActiveYear(getMouseTarget(e).year)};\r\n"+
            "    btn.year=y;\r\n"+
            "    y%=100;\r\n"+
            "    btn.innerHTML=(y<10 ? \"'0\" : \"'\") + y;\r\n"+
            "    yearsBarDiv.appendChild(btn);\r\n"+
            "  }\r\n"+
            "  this.populateMonthsBar();\r\n"+
            "  this.curCalendar = this.getCalendar(this.activeMonth, this.activeYear, this.activeDate);\r\n"+
            "  this.monthContainer.appendChild(this.curCalendar);\r\n"+
            "  \r\n"+
            "  // clear & close button\r\n"+
            "  //this.rootDiv.style.height=toPx(this.rootDiv.clientHeight + 20);\r\n"+
            "  this.clearBtn = nw(\"div\", \"cal_clear\");\r\n"+
            "  this.closeBtn = nw(\"div\", \"cal_close\");\r\n"+
            "  if (this.style.btnBgColor) {\r\n"+
            "	  this.closeBtn.style.background=this.style.btnBgColor;\r\n"+
            "	  this.clearBtn.style.background=this.style.btnBgColor;\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "  var btnHeight = 20;\r\n"+
            "  var btnWidth = 100;\r\n"+
            "  var bottomPadding = 3;\r\n"+
            "  \r\n"+
            "  this.clearBtn.style.width=toPx(btnWidth);\r\n"+
            "  this.clearBtn.style.height=toPx(btnHeight);\r\n"+
            "  this.clearBtn.style.left = toPx(this.rootDiv.clientWidth/2 - btnWidth - 2);\r\n"+
            "  this.clearBtn.style.top = toPx(this.monthContainer.offsetTop + this.monthContainer.clientHeight + 25);\r\n"+
            "  if (this.isRange) {\r\n"+
            "  	if (this.isOnEdit)\r\n"+
            "  		this.clearBtn.textContent = \"Clear Range\";\r\n"+
            "  	else if (this.activeInput == this.input)\r\n"+
            "  		this.clearBtn.textContent = \"Clear Start Date\";\r\n"+
            "  	else if (this.activeInput == this.input2)\r\n"+
            "  		this.clearBtn.textContent = \"Clear End Date\";\r\n"+
            "  } else {\r\n"+
            "  		this.clearBtn.textContent = \"Clear\";\r\n"+
            "  }\r\n"+
            "  if (this.style.btnFgColor)\r\n"+
            "	  this.clearBtn.style.color=this.style.btnFgColor;\r\n"+
            "  \r\n"+
            "  this.closeBtn.style.width=toPx(btnWidth);\r\n"+
            "  this.closeBtn.style.height=toPx(btnHeight);\r\n"+
            "  this.closeBtn.style.left = toPx(this.rootDiv.clientWidth/2 + 2);\r\n"+
            "  this.closeBtn.style.top = toPx(this.monthContainer.offsetTop + this.monthContainer.clientHeight + 25);\r\n"+
            "  this.closeBtn.textContent = \"Close\";\r\n"+
            "  if (this.style.btnFgColor)\r\n"+
            "	  this.closeBtn.style.color=this.style.btnFgColor;\r\n"+
            "  \r\n"+
            "  this.rootDiv.appendChild(this.clearBtn);\r\n"+
            "  this.rootDiv.appendChild(this.closeBtn);\r\n"+
            "  this.clearBtn.onclick=function(e){that.onClickClearBtn();};\r\n"+
            "  this.closeBtn.onclick=function(e){that.onClickCloseBtn();that.handleCloseButton();};\r\n"+
            "  this.repaintDays(); \r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.handleCloseButton=function() {\r\n"+
            "	// override\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.populateMonthsBar=function() {\r\n"+
            "	this.monthsBarDiv.innerHTML = \"\";\r\n"+
            "	var thisYear = this.today.getFullYear();\r\n"+
            "	var thisMonth = this.today.getMonth();\r\n"+
            "	for (var i = 0; i < monthNamesShortened.length; i++) {\r\n"+
            "	  	var that = this;");
          out.print(
            "\r\n"+
            "		var monthDiv = nwDiv('cal_monthDiv');\r\n"+
            "	 	monthDiv.innerHTML = monthNamesShortened[i];\r\n"+
            "	 	if (this.style.monthFgColor)\r\n"+
            "	 		monthDiv.style.color = this.style.monthFgColor;\r\n"+
            "	  	if(thisYear==this.year && i == thisMonth) {	  		\r\n"+
            "	    	monthDiv.classList.add(\"cal_today\");\r\n"+
            "	  	}\r\n"+
            "	    \r\n"+
            "	    if ((typeof this.activeMonth != \"undefined\" || this.activeMonth != null) && i == this.activeMonth && !monthDiv.classList.contains(\"cal_clicked_month\")) {\r\n"+
            "	    	monthDiv.classList.add(\"cal_clicked_month\");\r\n"+
            "	    	if (this.style.selMonthFgColor) {\r\n"+
            "	    		monthDiv.style.color=this.style.selMonthFgColor;\r\n"+
            "	    	}\r\n"+
            "	    	if (this.style.selMonthBgColor) {	    		\r\n"+
            "	    		monthDiv.style.background=this.style.selMonthBgColor;\r\n"+
            "	    	}\r\n"+
            "	    }	else if ((typeof this.activeMonth == \"undefined\" || this.activeMonth == null) && i == thisMonth) {\r\n"+
            "	    	this.activeMonth = thisMonth;\r\n"+
            "	    	monthDiv.classList.add(\"cal_clicked_month\");\r\n"+
            "	    	if (this.style.selMonthFgColor) {\r\n"+
            "	    		monthDiv.style.color=this.style.selMonthFgColor;\r\n"+
            "	    	}\r\n"+
            "	    	if (this.style.selMonthBgColor) {	    		\r\n"+
            "	    		monthDiv.style.background=this.style.selMonthBgColor;\r\n"+
            "	    	}\r\n"+
            "	    }\r\n"+
            "	  	monthDiv.onclick=function(e){\r\n"+
            "	  		that.onMonthClick(e);\r\n"+
            "	  	};\r\n"+
            "	  	this.monthsBarDiv.appendChild(monthDiv); \r\n"+
            "	}\r\n"+
            "}\r\n"+
            "DateChooser.prototype.onMonthClick=function(e) {\r\n"+
            "	var monthMap = {Jan: 0, Feb: 1, Mar: 2, Apr: 3, May: 4, Jun: 5, Jul:6, Aug:7, Sep: 8, Oct:9, Nov: 10, Dec: 11};\r\n"+
            "	var monthDiv = getMouseTarget(e);\r\n"+
            "	monthDiv.classList.add(\"cal_clicked_month\");\r\n"+
            "	var monthId = monthMap[monthDiv.textContent];\r\n"+
            "	this.activeMonth = monthId;\r\n"+
            "	// TODO we are repainting everything, which could be optimized...\r\n"+
            "	this.hideCalendar();\r\n"+
            "	this.showCalendar();\r\n"+
            "}\r\n"+
            "DateChooser.prototype.getCalendar=function(month, year, day) {\r\n"+
            "  var that = this;\r\n"+
            "  var dayHeight=22; \r\n"+
            "  var dayWidth=50; \r\n"+
            "  var monthWidth=dayWidth*7; \r\n"+
            "  var monthHeight=dayHeight*8;\r\n"+
            "  var monthPad = 5;\r\n"+
            "  var thisYear=this.today.getFullYear(); \r\n"+
            "  var thisMonth=this.today.getMonth(); \r\n"+
            "  var thisDate=this.today.getDate(); \r\n"+
            "	\r\n"+
            "  var onClickDayFunc=function(e){that.onClickDay(e)}; \r\n"+
            "  var onOverDayFunc=function(e){that.onOverDay(e)}; \r\n"+
            "  var onOutDayFunc=function(e){that.onOutDay(e)};\r\n"+
            "  var monthDivSize=this.monthContainer.clientWidth-2*monthPad;\r\n"+
            "  var monthDiv=nwDiv('cal_month',monthPad,0,monthDivSize,monthHeight); \r\n"+
            "\r\n"+
            "  for(var i in dayNames){ \r\n"+
            "    var dayLabelDiv=nwDiv('cal_dayLabel',i*dayWidth,0,dayWidth,dayHeight); \r\n"+
            "    if (this.style.weekBgColor)\r\n"+
            "    	dayLabelDiv.style.boxShadow= \"inset 0px 0px 28px 4px\" +this.style.weekBgColor;\r\n"+
            "	 dayLabelDiv.style.background= this.headerColor;\r\n"+
            "    if (this.style.weekFgColor)\r\n"+
            "    	dayLabelDiv.style.color=this.style.weekFgColor;\r\n"+
            "    dayLabelDiv.innerHTML=dayNames[i]; \r\n"+
            "    monthDiv.appendChild(dayLabelDiv); \r\n"+
            "  } \r\n"+
            "  var firstDay=new Date(this.year,month,1).getDay(); \r\n"+
            "  var daysInMonth=32-new Date(this.year,month,32).getDate(); \r\n"+
            "  var dayInMonth=0; \r\n"+
            "  outer:for(var week=0;week<6;week++){ \r\n"+
            "    for(var day=week==0 ? firstDay : 0;day<7;day++){ \r\n"+
            "      dayInMonth++; \r\n"+
            "      if(dayInMonth>daysInMonth) \r\n"+
            "        break; \r\n"+
            "      var dayDiv=nwDiv('cal_day',day*dayWidth,(1+week)*dayHeight,dayWidth,dayHeight); \r\n"+
            "      if (this.style.dayFgColor)\r\n"+
            "    	  dayDiv.style.color=this.style.dayFgColor;\r\n"+
            "      monthDiv.appendChild(dayDiv); \r\n"+
            "      dayDiv.innerHTML=dayInMonth; \r\n"+
            "      dayDiv.onclick=onClickDayFunc;\r\n"+
            "      dayDiv.onmouseover=onOverDayFunc;\r\n"+
            "      dayDiv.onmouseout=onOutDayFunc;		\r\n"+
            "      dayDiv.dayInMonth=dayInMonth; \r\n"+
            "      dayDiv.dayInWeek=day; \r\n"+
            "      dayDiv.month=month; \r\n"+
            "      dayDiv.year=year; \r\n"+
            "      dayDiv.ymd=this.toYmd(year,month+1,dayInMonth); \r\n"+
            "      this.dayDivs[this.dayDivs.length]=dayDiv; \r\n"+
            "    } \r\n"+
            "  }\r\n"+
            "  this.repaintDays();\r\n"+
            "  this.monthContainer.innerHTML = \"\";\r\n"+
            "  return monthDiv;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "DateChooser.prototype.onOutDay=function(e) {\r\n"+
            "	var el=getMouseTarget(e);\r\n"+
            "	// do not overwrite if the hovered day has been selected or is part of a range\r\n"+
            "	if (el.classList.contains(\"cal_selected\") || el.classList.contains(\"cal_inrange\"))\r\n"+
            "		return;\r\n"+
            "	if (this.style.calendarBgColor)\r\n"+
            "		el.style.background=this.style.calendarBgColor;\r\n"+
            "	else		\r\n"+
            "		el.style.background=\"transparent\";\r\n"+
            "}\r\n"+
            "	\r\n"+
            "DateChooser.prototype.setBtnBgColor=function(color) {\r\n"+
            "	this.style.btnBgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setBtnFgColor=function(color) {\r\n"+
            "	this.style.btnFgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setCalendarBgColor=function(color) {\r\n"+
            "	this.style.calendarBgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setYearFgColor=function(color) {\r\n"+
            "	this.style.yearFgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setSelYearFgColor=function(color) {\r\n"+
            "	this.style.selYearFgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setMonthFgColor=function(color) {\r\n"+
            "	this.style.monthFgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setSelMonthBgColor=function(color) {\r\n"+
            "	this.style.selMonthBgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setSelMonthFgColor=function(color) {\r\n"+
            "	this.style.selMonthFgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setWeekFgColor=function(color) {\r\n"+
            "	this.style.weekFgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setWeekBgColor=function(color) {\r\n"+
            "	this.style.weekBgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setDayFgColor=function(color) {\r\n"+
            "	this.style.dayFgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setXDayFgColor=function(color) {\r\n"+
            "	this.style.xDayFgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.setHoverBgColor=function(color) {\r\n"+
            "	this.style.hoverBgColor=color;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.checkRightSettings= function(delta) {\r\n"+
            "	if (this.enableLastNDaysEnd == null) \r\n"+
            "		return false;\r\n"+
            "	return delta > this.enableLastNDaysEnd;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.checkLeftSettings= function(delta) {\r\n"+
            "	if (this.enableLastNDays == null)\r\n"+
            "		return false;\r\n"+
            "	return delta > this.enableLastNDays;\r\n"+
            "}\r\n"+
            "\r\n"+
            "DateChooser.prototype.repaintDays=function(){ \r\n"+
            "  var selYmd=this.selYmd;\r\n"+
            "  var selYmd2=this.selYmd2; \r\n"+
            "//  if(this.isRange && this.hoverYmd>0){ \r\n"+
            "//    if(this.state==1) \r\n"+
            "//      selYmd=this.hoverYmd; \r\n"+
            "//    else \r\n"+
            "//      selYmd2=this.hoverYmd; \r\n"+
            "//  } \r\n"+
            "  var firstDate = new Date(this.dayDivs[0].year, this.dayDivs[0].month, this.dayDivs[0].dayInMonth);\r\n"+
            "  var today = new Date(this.today.getFullYear(), this.today.getMonth(), this.today.getDate());\r\n"+
            "  var delta = Math.floor((today - firstDate) / (1000 * 60 * 60 * 24));\r\n"+
            "  var left = this.activeInput == this.input? true:false;\r\n"+
            "  for(var i in this.dayDivs){ \r\n"+
            "	var dayDiv=this.dayDivs[i]; \r\n"+
            "    dayDiv.className='cal_day'; \r\n"+
            "    if (this.style.calendarBgColor) {\r\n"+
            "    	dayDiv.style.background=this.style.calendarBgColor;\r\n"+
            "    }\r\n"+
            "    var day=dayDiv.dayInWeek; \r\n"+
            "    var dayInMonth=dayDiv.dayInMonth; \r\n"+
            "    var month=dayDiv.month; \r\n"+
            "    var year=dayDiv.year; \r\n"+
            "    var ymd=dayDiv.ymd; \r\n"+
            "    if(day==0 || day==6) \r\n"+
            "      dayDiv.className+=' cal_weekend'; \r\n"+
            "    if(this.thisYmd==ymd) {\r\n"+
            "    	dayDiv.className+=' cal_today';\r\n"+
            "    }\r\n"+
            "    if(this.selYmd!=0 && this.selDate==dayInMonth && this.selYear==this.activeYear && this.selMonth==this.activeMonth){ \r\n"+
            "    // this cell is the left calendar's selected day\r\n"+
            "      dayDiv.className+=' cal_selected';\r\n"+
            "      if (this.style.selMonthBgColor) {\r\n"+
            "			dayDiv.style.background=this.style.selMonthBgColor;\r\n"+
            "			dayDiv.style.filter='saturate(4)';\r\n"+
            "		}\r\n"+
            "    }else if(this.isRange && selYmd2!=0 && this.selDate2==dayInMonth && this.selYear2==this.activeYear && this.selMonth2==this.activeMonth){\r\n"+
            "    	// this cell is the right calendar's selected day\r\n"+
            "        dayDiv.className+=' cal_selected';\r\n"+
            "        if (this.style.selMonthBgColor) {\r\n"+
            "			dayDiv.style.background=this.style.selMonthBgColor;\r\n"+
            "			dayDiv.style.filter='saturate(4)';\r\n"+
            "		}\r\n"+
            "    } else if (selYmd != 0) {\r\n"+
            "    	// 1. if current cell is between two dates\r\n"+
            "    	// 2. if user hovers over a date that is between the left calendar's selected date and the hovered date while the right calendar is empty\r\n"+
            "    	if ((ymd > selYmd && ymd < selYmd2) || (selYmd2 == 0 && this.hoverYmd != 0 && ymd > selYmd && ymd < this.hoverYmd)) {\r\n"+
            "			// also paint in-range if start is set but end is not set\r\n"+
            "			dayDiv.className+=' cal_inrange';\r\n"+
            "			if (this.style.selMonthBgColor) {\r\n"+
            "				dayDiv.style.background=this.style.selMonthBgColor;\r\n"+
            "			}\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "\r\n"+
            "    // for right calendar, disable all days less than start date\r\n"+
            "    if (left==false && this.isRange && this.selYmd != 0 && ymd < this.selYmd) {\r\n"+
            "    	dayDiv.classList.add(\"day_disabled\");\r\n"+
            "    	if (this.style.xDayFgColor)\r\n"+
            "    		dayDiv.style.color=this.style.xDayFgColor;\r\n"+
            "    }\r\n"+
            "    // for left calendar, disable all days greater then end date if end is selected\r\n"+
            " 	if (left==true && this.isRange && this.selYmd2 != 0 && ymd > this.selYmd2) {\r\n"+
            "    	dayDiv.classList.add(\"day_disabled\");\r\n"+
            "    	if (this.style.xDayFgColor)\r\n"+
            "    		dayDiv.style.color=this.style.xDayFgColor;\r\n"+
            " 	} \r\n"+
            "    // disable last n days\r\n"+
            " 	if (ymd <= this.thisYmd) {\r\n"+
            " 		if (left==true) {\r\n"+
            " 	 		if (this.checkLeftSettings(delta) == true) {\r\n"+
            " 	 			dayDiv.classList.add(\"day_disabled\");\r\n"+
            " 	 			if (this.style.xDayFgColor)\r\n"+
            " 	 	    		dayDiv.style.color=this.style.xDayFgColor;\r\n"+
            " 	 	    	delta--;\r\n"+
            " 	 		}\r\n"+
            " 	 	} else {\r\n"+
            " 	 		if (this.checkRightSettings(delta) == true) {\r\n"+
            " 	 			dayDiv.classList.add(\"day_disabled\");\r\n"+
            " 	 			if (this.style.xDayFgColor)\r\n"+
            " 	 	    		dayDiv.style.color=this.style.xDayFgColor;\r\n"+
            " 	 	    	delta--;\r\n"+
            " 	 		}\r\n"+
            " 	 	}\r\n"+
            " 	}\r\n"+
            "\r\n"+
            "    //disable future days\r\n"+
            "    if (left==true) {\r\n"+
            "    	if (this.disableFutureDays == true && ymd > this.thisYmd) {\r\n"+
            "    		dayDiv.classList.add(\"day_disabled\");\r\n"+
            "    		if (this.style.xDayFgColor)\r\n"+
            "        		dayDiv.style.color=this.style.xDayFgColor;\r\n"+
            "    	}\r\n"+
            "    		\r\n"+
            "    } else {\r\n"+
            "    	if (this.disableFutureDaysEnd == true && ymd > this.thisYmd) {\r\n"+
            "    		dayDiv.classList.add(\"day_disabled\");\r\n"+
            "    		if (this.style.xDayFgColor)\r\n"+
            "        		dayDiv.style.color=this.style.xDayFgColor;\r\n"+
            "    	}\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "  \r\n"+
            "\r\n"+
            "}\r\n"+
            "DateChooser.prototype.toYmd=function(y,m,d){ \r\n"+
            "  return y*10000+m*100+d*1; \r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.onClickInput=function(e,idx){ \r\n"+
            "  this.state=idx; \r\n"+
            "  var val=idx==1 ? this.input.value : this.input2.val");
          out.print(
            "ue;\r\n"+
            "  this.activeInput = idx==1 ? this.input : this.input2; \r\n"+
            "  this.hoverYmd=0; \r\n"+
            "  this.parseInput(val,idx);\r\n"+
            "  this.hideCalendar();  // why hide?\r\n"+
            "  this.showCalendar(); \r\n"+
            "} \r\n"+
            "DateChooser.prototype.onKeyUpInput=function(e,idx){ \r\n"+
            "  var val=idx==1 ? this.input.value : this.input2.value; \r\n"+
            "  this.state=idx; \r\n"+
            "  this.hoverYmd=0; \r\n"+
            "  if(e.keyCode==27 || e.keyCode==13){ \r\n"+
            "    this.hideCalendar(); \r\n"+
            "    return; \r\n"+
            "  } \r\n"+
            "  this.parseInput(val,idx); \r\n"+
            "} \r\n"+
            "DateChooser.prototype.onKeyDownInput=function(e,idx){\r\n"+
            "	if (e.keyCode == 13)\r\n"+
            "	  	this.hideCalendar();\r\n"+
            "}\r\n"+
            "DateChooser.prototype.parseInput=function(val,idx){ \r\n"+
            "  val=val.toString(); \r\n"+
            "  if(/^[0-9]*$/.test(val)){\r\n"+
            "    if(val.length>=4){ \r\n"+
            "      var year=val.substring(0,4); \r\n"+
            "      if(this.year!=year){ \r\n"+
            "        //this.setActiveYear(year); \r\n"+
            "        this.activeYear = year;\r\n"+
            "      } \r\n"+
            "      if (val.length == 8) {\r\n"+
            "        this.activeYear = year;\r\n"+
            "        this.activeMonth = val.substring(4,6) - 1;\r\n"+
            "      }\r\n"+
            "      if(val.length==8 && val!=(idx==1 ? this.selYmd : this.selYmd2).toString()){ \r\n"+
            "        if(idx==1)\r\n"+
            "          this.setSelected(year,val.substring(4,6),val.substring(6,8)); \r\n"+
            "        else \r\n"+
            "          this.setSelected2(year,val.substring(4,6),val.substring(6,8)); \r\n"+
            "        \r\n"+
            "        if(this.rootDiv!=null) \r\n"+
            "          this.repaintDays(); \r\n"+
            "      } \r\n"+
            "    } else if(val.length==0){\r\n"+
            "    	var changed=false;\r\n"+
            "        if(idx==1) {\r\n"+
            "          this.setSelected(0,0,0);\r\n"+
            "          // goes to end date\r\n"+
            "		  if (this.isRange && this.selYmd2 != 0) {\r\n"+
            "			this.activeMonth=this.selMonth2;\r\n"+
            "			this.activeDate = this.selDate2;\r\n"+
            "			this.setActiveYear(this.selYear2);\r\n"+
            "			changed=true;\r\n"+
            "		  }\r\n"+
            "        } else {\r\n"+
            "          this.setSelected2(0,0,0);\r\n"+
            "          // goes to start date\r\n"+
            "		  if (this.selYmd != 0) {\r\n"+
            "			  this.activeMonth=this.selMonth;\r\n"+
            "			  this.activeDate = this.selDate;\r\n"+
            "			  this.setActiveYear(this.selYear);\r\n"+
            "			  changed=true;\r\n"+
            "		  }\r\n"+
            "\r\n"+
            "        }\r\n"+
            "        if (!changed) {\r\n"+
            "        	// defaults to current date if both calendars are empty\r\n"+
            "      		this.activeMonth=this.today.getMonth();\r\n"+
            "      		// below is needed in cases where you refresh the browser\r\n"+
            "      		this.activeYear=this.today.getFullYear();\r\n"+
            "      		this.setActiveYear(this.today.getFullYear());\r\n"+
            "      		this.activeDate = this.today.getDate();\r\n"+
            "        }\r\n"+
            "    }\r\n"+
            "  }\r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.hideCalendar=function(e){ \r\n"+
            "	if(this.rootDiv!=null){ \r\n"+
            "		this.glass.hide();\r\n"+
            "		this.glass=null;\r\n"+
            "		this.rootDiv=null; \r\n"+
            "		this.input.focus();\r\n"+
            "	} \r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.onClickDay=function(e){ \r\n"+
            "	var el=getMouseTarget(e); \r\n"+
            "	var year=el.year; \r\n"+
            "	var month=el.month+1; \r\n"+
            "	var day=el.dayInMonth; \r\n"+
            "	var ymd=this.toYmd(year,month,day); \r\n"+
            "	this.hideCalendar(); \r\n"+
            "	if(!this.isRange){\r\n"+
            "		this.setSelected(year,month,day); \r\n"+
            "	}else{\r\n"+
            "		if(this.state==2){\r\n"+
            "			this.setSelected2(year,month,day); \r\n"+
            "		}else{\r\n"+
            "			this.setSelected(year,month,day); \r\n"+
            "			this.state=2; \r\n"+
            "		} \r\n"+
            "	} \r\n"+
            "	this.onChange(e);\r\n"+
            "} \r\n"+
            "DateChooser.prototype.setOnEdit=function(isEdit) {\r\n"+
            "	this.isOnEdit = isEdit;\r\n"+
            "}\r\n"+
            "DateChooser.prototype.handleClickFromEditCell=function(e) {\r\n"+
            " 	var el=getMouseTarget(e); \r\n"+
            "  	if (el.classList.contains(\"day_disabled\")) {\r\n"+
            "		this.hideCalendar(); // repaint everything\r\n"+
            "		return;\r\n"+
            "  	}\r\n"+
            " 	var year=el.year; \r\n"+
            "  	var month=el.month+1; \r\n"+
            "  	var day=el.dayInMonth; \r\n"+
            "  	var ymd=this.toYmd(year,month,day);\r\n"+
            "	this.hideCalendar();\r\n"+
            "  	if (this.isRange) {\r\n"+
            "	  	if (this.selYmd == 0) {\r\n"+
            "	    	this.setSelected(year,month,day); \r\n"+
            "			this.state = 2;\r\n"+
            "	  	} else if (this.selYmd2 == 0) {\r\n"+
            "	      	this.setSelected2(year,month,day); \r\n"+
            "	  	} \r\n"+
            "  	} else {\r\n"+
            "		this.setSelected(year,month,day); \r\n"+
            "  	}\r\n"+
            "	this.parentDiv.value = this.input.value;\r\n"+
            "	this.input.focus();\r\n"+
            "}\r\n"+
            "DateChooser.prototype.onClickClearBtn=function(e) {\r\n"+
            "	this.hoverYmd=0; \r\n"+
            "	if (this.isOnEdit) {\r\n"+
            "		this.setSelected(0,0,0);\r\n"+
            "		this.setValue('');\r\n"+
            "		if (this.isRange) {\r\n"+
            "			this.setValue2('');\r\n"+
            "		}\r\n"+
            "		this.hideCalendar(); // repaint everything\r\n"+
            "		this.showCalendar();\r\n"+
            "		return;\r\n"+
            "	}\r\n"+
            "	if (this.activeInput == this.input) {\r\n"+
            "		this.setValue('');\r\n"+
            "	} else {\r\n"+
            "		this.setValue2('');\r\n"+
            "	}\r\n"+
            "	this.onChange(e);\r\n"+
            "	this.hideCalendar(); // hide/show to repaint everything\r\n"+
            "	this.showCalendar();\r\n"+
            "} \r\n"+
            "DateChooser.prototype.onClickCloseBtn=function(e) {\r\n"+
            "	this.hideCalendar();\r\n"+
            "}\r\n"+
            "DateChooser.prototype.setDisableFutureDays=function(bools) {\r\n"+
            "	this.disableFutureDays = bools[0];\r\n"+
            "	this.disableFutureDaysEnd=bools[1];\r\n"+
            "}\r\n"+
            "DateChooser.prototype.setEnableLastNDays=function(days) {\r\n"+
            "	this.enableLastNDays=days[0];\r\n"+
            "	this.enableLastNDaysEnd=days[1];\r\n"+
            "}\r\n"+
            "\r\n"+
            "//DateChooser.prototype.setDisableFutureDaysEnd=function(booleanVal) {\r\n"+
            "//	this.disableFutureDaysEnd = booleanVal;\r\n"+
            "//}\r\n"+
            "//DateChooser.prototype.setEnableLastNDaysEnd=function(nDays) {\r\n"+
            "//	if (typeof nDays !== 'string' || nDays.length == 0)\r\n"+
            "//		this.enableLastNDaysEnd = -1;\r\n"+
            "//	else\r\n"+
            "//		this.enableLastNDaysEnd = nDays;\r\n"+
            "//}\r\n"+
            "\r\n"+
            "DateChooser.prototype.onOverDay=function(e){\r\n"+
            "	var el=getMouseTarget(e); \r\n"+
            "	var year=el.year; \r\n"+
            "	var month=el.month+1; \r\n"+
            "	var day=el.dayInMonth; \r\n"+
            "	var ymd=this.toYmd(year,month,day);\r\n"+
            "	this.hoverYmd=ymd;\r\n"+
            "	// no op if both calendars have been set AND current cell is either in range or selected\r\n"+
            "	if (this.selYmd > 0 && this.selYmd2 > 0 && (el.classList.contains(\"cal_selected\") || el.classList.contains(\"cal_inrange\")))\r\n"+
            "		return;\r\n"+
            "	// repaint days if end calendar is not filled, so we need to color all the days previous to the hovered day. Note that if start calendar is not filled but end calendar is filled, no color animation.\r\n"+
            "	if (this.selYmd2 == 0)\r\n"+
            "		this.repaintDays(); \r\n"+
            "	// enforce hover color on the hovered day\r\n"+
            "	if (ymd != this.selYmd && ymd != this.selYmd2 && this.style.hoverBgColor) {\r\n"+
            "		el.style.background=this.style.hoverBgColor;\r\n"+
            "	}\r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.setActiveYear=function(year){ \r\n"+
            "  this.year=1*year; \r\n"+
            "  if(this.rootDiv!=null){ \r\n"+
            "    var month = this.activeMonth != null ? this.activeMonth : this.today.getMonth();\r\n"+
            "    this.activeMonth = month;\r\n"+
            "    this.activeYear = year;\r\n"+
            "    this.hideCalendar(); // why repaint?\r\n"+
            "    this.showCalendar(); \r\n"+
            "  } \r\n"+
            "} \r\n"+
            "DateChooser.prototype.onChange=function(e){ \r\n"+
            "}\r\n"+
            "DateChooser.prototype.getValue=function(){ \r\n"+
            "	return this.input.value;\r\n"+
            "}\r\n"+
            "DateChooser.prototype.getValues=function(){ \r\n"+
            "	if(this.selYmd == 0) return null;\r\n"+
            "	var m = {};\r\n"+
            "	m.year = this.selYear;\r\n"+
            "	m.month = this.selMonth+1; // get actual month, not the index\r\n"+
            "	m.day = this.selDate;\r\n"+
            "	return m;\r\n"+
            "}\r\n"+
            "DateChooser.prototype.getValue2=function(){ \r\n"+
            "	return this.isRange ? this.input2.value : null;\r\n"+
            "}\r\n"+
            "DateChooser.prototype.getValues2=function(){ \r\n"+
            "	if(this.isRange){\r\n"+
            "		if(this.selYmd2 == 0)\r\n"+
            "			return null;\r\n"+
            "		var m = {};\r\n"+
            "		m.year = this.selYear2;\r\n"+
            "		m.month = this.selMonth2+1; // get actual month, not the index\r\n"+
            "		m.day = this.selDate2;\r\n"+
            "		return m;\r\n"+
            "	}\r\n"+
            "	return null;\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "DateChooser.prototype.setValue=function(yyyymmdd){ \r\n"+
            "  this.parseInput(yyyymmdd,1); // also sets input value\r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.setValue2=function(yyyymmdd){ \r\n"+
            "  this.parseInput(yyyymmdd,2); // also sets input value\r\n"+
            "} \r\n"+
            "\r\n"+
            "DateChooser.prototype.setContainerSize=function(w,h){\r\n"+
            "	// this.dash is the hyphen between the two fields in date range fields. It is affected by font size as well.\r\n"+
            "	if (this.dash != null && this.input2) {\r\n"+
            "		w  = w/2- this.dash.offsetWidth;\r\n"+
            "	} else if (this.input2) {\r\n"+
            "		w = w/2 - 6;\r\n"+
            "	}\r\n"+
            "	\r\n"+
            "	this.input.style.width=toPx(w);\r\n"+
            "	this.input.style.height=toPx(h);\r\n"+
            "	if(this.input2){\r\n"+
            "		this.input2.style.height=toPx(h);\r\n"+
            "		this.input2.style.width=toPx(w);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "DateChooser.prototype.getSelectedYmd=function(){\r\n"+
            "	return this.selYmd;\r\n"+
            "}\r\n"+
            "DateChooser.prototype.getSelectedYmd2=function(){\r\n"+
            "	return this.selYmd2;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function TimeChooser(t_input){\r\n"+
            "    this.window=getWindow(t_input.parentDiv);\r\n"+
            "    this.t_input = t_input;    \r\n"+
            "    \r\n"+
            "//    this.input2.onclick=function(e){that.onClickInput(e,2);}; \r\n"+
            "//    this.input2.onkeyup=function(e){that.onKeyUpInput(e,2);}; \r\n"+
            "}\r\n"+
            "TimeChooser.prototype.init=function(){\r\n"+
            "   	var input = this.t_input.input;\r\n"+
            "    if(this.rootDiv == null){\r\n"+
            "		var rect = new Rect();\r\n"+
            "		rect.readFromElement(input);\r\n"+
            "		this.rootDiv=nwDiv('time_clock', rect.getLeft(), rect.getTop()+rect.height, 184, 50);\r\n"+
            "		this.glass = new Glass(this.window,this);\r\n"+
            "		this.glass.appendChild(this.rootDiv);\r\n"+
            "		this.rootDiv.onclick=function(e){e.stopPropagation(); e.preventDefault();};\r\n"+
            "\r\n"+
            "		var that = this;\r\n"+
            "		this.glass.glassElement.onclick = function(e){\r\n"+
            "			that.t_input.setValues(that.getValues(), true);\r\n"+
            "			that.t_input.timeChooser.hide();\r\n"+
            "			that.onGlass(e);\r\n"+
            "		};\r\n"+
            "		//this.glass.glassElement.onclick=function(e) {that.hide();}\r\n"+
            "		\r\n"+
            "		this.input_hours = nw(\"select\",\"time_hours\");\r\n"+
            "		this.input_minutes = nw(\"select\",\"time_minutes\");\r\n"+
            "		this.input_seconds = nw(\"select\",\"time_seconds\");\r\n"+
            "		this.input_millis = nw(\"input\",\"time_millis\");\r\n"+
            "		this.input_meridiem = nw(\"select\",\"time_meridiem\");\r\n"+
            "		\r\n"+
            "		this.input_millis.type=\"numbers\";\r\n"+
            "		this.input_millis.min=0;\r\n"+
            "		this.input_millis.max=999;\r\n"+
            "	\r\n"+
            "		var option;\r\n"+
            "		//Hours\r\n"+
            "		for(var i = 1; i < 13; i++){\r\n"+
            "			option = document.createElement(\"option\");\r\n"+
            "			\r\n"+
            "			option.text=i<10?\"0\"+i:\"\"+i;\r\n"+
            "			this.input_hours.add(option);\r\n"+
            "		}\r\n"+
            "		//Minutes\r\n"+
            "		for(var i = 0; i < 60; i++){\r\n"+
            "			option = document.createElement(\"option\");\r\n"+
            "			option.text=i<10?\"0\"+i:\"\"+i;\r\n"+
            "			this.input_minutes.add(option);\r\n"+
            "		}\r\n"+
            "		//Seconds\r\n"+
            "		for(var i = 0; i < 60; i++){\r\n"+
            "			option = document.createElement(\"option\");\r\n"+
            "			option.text=i<10?\"0\"+i:\"\"+i;\r\n"+
            "			this.input_seconds.add(option);\r\n"+
            "		}\r\n"+
            "		//Meridiem\r\n"+
            "		option = document.createElement(\"option\");\r\n"+
            "		option.text=\"AM\";\r\n"+
            "		this.input_meridiem.add(option);\r\n"+
            "		option = document.createElement(\"option\");\r\n"+
            "		option.text=\"PM\";\r\n"+
            "		this.input_meridiem.add(option);\r\n"+
            "		\r\n"+
            "		\r\n"+
            "		this.rootDiv.appendChild(this.input_hours);\r\n"+
            "		this.rootDiv.appendChild(nw(\"span\",\"time_colon\"));\r\n"+
            "		this.rootDiv.appendChild(this.input_minutes);\r\n"+
            "		this.rootDiv.appendChild(nw(\"span\",\"time_colon\"));\r\n"+
            "		this.rootDiv.appendChild(this.input_seconds);\r\n"+
            "		this.rootDiv.appendChild(nw(\"span\",\"time_period\"));\r\n"+
            "		this.rootDiv.appendChild(t");
          out.print(
            "his.input_millis);\r\n"+
            "		this.rootDiv.appendChild(this.input_meridiem);\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeChooser.prototype.onGlass=function(){\r\n"+
            "	// override\r\n"+
            "}\r\n"+
            "TimeChooser.prototype.show=function(){\r\n"+
            "    this.init();\r\n"+
            "    \r\n"+
            "    var time = this.t_input.getValues();\r\n"+
            "    if(this.t_input.getValue() == null){\r\n"+
            "    	var dtime = new Date();\r\n"+
            "    	time.hours = dtime.getHours();\r\n"+
            "    	time.minutes = dtime.getMinutes();\r\n"+
            "    	time.seconds = dtime.getSeconds();\r\n"+
            "    	time.millis = dtime.getMilliseconds();\r\n"+
            "    }\r\n"+
            "    this.loadValues(time);\r\n"+
            "    this.glass.show();\r\n"+
            "}\r\n"+
            "TimeChooser.prototype.hide=function(){\r\n"+
            "	if(this.rootDiv!=null){ \r\n"+
            "		this.glass.hide();\r\n"+
            "		this.glass=null;\r\n"+
            "	    this.rootDiv=null; \r\n"+
            "	    // will send blur to backend later so we shouldn't focus here\r\n"+
            "//	    this.t_input.input.focus();\r\n"+
            "	} \r\n"+
            "}\r\n"+
            "TimeChooser.prototype.getValues=function(){\r\n"+
            "	var hours = parseInt(this.input_hours.value);\r\n"+
            "	var minutes = parseInt(this.input_minutes.value);\r\n"+
            "	var seconds = parseInt(this.input_seconds.value);\r\n"+
            "	var millis = parseInt(this.input_millis.value);\r\n"+
            "	var meridiem = this.input_meridiem.value;\r\n"+
            "	if(isNaN(millis))\r\n"+
            "		millis=0;\r\n"+
            "	millis = Math.min(Math.max(millis,0), 999);\r\n"+
            "	if(hours == 12)\r\n"+
            "		hours = 0;\r\n"+
            "	if(meridiem==\"PM\")\r\n"+
            "		hours+=12;\r\n"+
            "//	if(meridiem==\"AM\" && hours == 12)\r\n"+
            "//		hours = 0;\r\n"+
            "	\r\n"+
            "	return {hours:hours,minutes:minutes,seconds:seconds,millis:millis};\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeChooser.prototype.loadValues=function(time){\r\n"+
            "    var hours = time.hours;\r\n"+
            "    var minutes = time.minutes;\r\n"+
            "    var seconds = time.seconds;\r\n"+
            "    var millis = time.millis;\r\n"+
            "	var meridiem=hours >= 12?\"PM\":\"AM\";\r\n"+
            "	\r\n"+
            "	if(hours == 0) hours = 12;\r\n"+
            "	if(hours >12)hours = hours-12;\r\n"+
            "	\r\n"+
            "	//Pad 0's\r\n"+
            "	hours=hours<10?\"0\"+hours:hours;\r\n"+
            "	minutes=minutes<10?\"0\"+minutes:minutes;\r\n"+
            "	seconds=seconds<10?\"0\"+seconds:seconds;\r\n"+
            "	if(millis < 10)\r\n"+
            "		millis=\"00\" + millis;\r\n"+
            "	else if(millis < 100)\r\n"+
            "		millis=\"0\" + millis;\r\n"+
            "    \r\n"+
            "    this.input_hours.value=hours;\r\n"+
            "	this.input_minutes.value=minutes;\r\n"+
            "	this.input_seconds.value=seconds;\r\n"+
            "    this.input_millis.value=millis;\r\n"+
            "	this.input_meridiem.value=meridiem;\r\n"+
            "}\r\n"+
            "\r\n"+
            "function TimeInput(parentDiv){\r\n"+
            "	var that = this;\r\n"+
            "	this.parentDiv = parentDiv;\r\n"+
            "	this.window = getWindow(this.parentDiv);\r\n"+
            "	this.input = nw(\"input\", \"time_input\"); //<input>\r\n"+
            "	this.input.onchange=function(e){\r\n"+
            "		if (that.input.value === \"\") {\r\n"+
            "			that.clear();\r\n"+
            "			that.onChange();\r\n"+
            "			if(that.timeChooser)\r\n"+
            "				that.timeChooser.hide();\r\n"+
            "		} else\r\n"+
            "			that.setValueLong(that.parseText(that.input.value), true);\r\n"+
            "	}\r\n"+
            "	this.input.onclick=function(e){that.timeChooser.show();}; \r\n"+
            "	this.input.display=\"none\";\r\n"+
            "	this.input.onkeydown=function(e){that.onKeyDownInput(e);};\r\n"+
            "	this.values = {};\r\n"+
            "	this.values.hours=null;\r\n"+
            "	this.values.minutes=null;\r\n"+
            "	this.values.seconds=null;\r\n"+
            "	this.values.millis=null;\r\n"+
            "	\r\n"+
            "	this.value = null; //long\r\n"+
            "	this.parentDiv.appendChild(this.input);\r\n"+
            "	this.timeChooser = new TimeChooser(this);\r\n"+
            "	\r\n"+
            "}\r\n"+
            "TimeInput.prototype.getInput=function(){\r\n"+
            "	return this.input;\r\n"+
            "}\r\n"+
            "TimeInput.prototype.getValue=function(){\r\n"+
            "	return this.value;\r\n"+
            "}\r\n"+
            "TimeInput.prototype.getValues=function(){\r\n"+
            "	return this.values;\r\n"+
            "}\r\n"+
            "TimeInput.prototype.onChange=function(){\r\n"+
            "	//Must be overwritten\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeInput.prototype.clear=function(){\r\n"+
            "	this.values.hours=null;\r\n"+
            "	this.values.minutes=null;\r\n"+
            "	this.values.seconds=null;\r\n"+
            "	this.values.millis=null;\r\n"+
            "	this.value = null; //long\r\n"+
            "	this.input.value='';\r\n"+
            "}\r\n"+
            "TimeInput.prototype.setValueLong=function(v, fire){\r\n"+
            "	this.setValue(Math.floor(v/3600000), Math.floor(v/60000 % 60), Math.floor(v/1000 % 60), v%1000,fire);\r\n"+
            "}\r\n"+
            "TimeInput.prototype.setValues=function(v, fire){\r\n"+
            "	this.setValue(v.hours,v.minutes,v.seconds, v.millis,fire);\r\n"+
            "}\r\n"+
            "TimeInput.prototype.setValue=function(hours, minutes, seconds, millis, fire){\r\n"+
            "	var newValue = hours*1000*60*60+minutes*60*1000+seconds*1000+millis;\r\n"+
            "	if(this.value == newValue)\r\n"+
            "		return;\r\n"+
            "	this.value = newValue;\r\n"+
            "	this.values.hours = hours;\r\n"+
            "	this.values.minutes = minutes;\r\n"+
            "	this.values.seconds = seconds;\r\n"+
            "	this.values.millis = millis;\r\n"+
            "	const ret=formatTime(this.timeFormat,hours,minutes);\r\n"+
            "	const oVal=this.input.value;\r\n"+
            "	this.input.value=ret?ret:this.formatAsText(this.value);	\r\n"+
            "//	if (oVal != ret)\r\n"+
            "//		this.ensureFieldsVisible();\r\n"+
            "	if(fire==true)\r\n"+
            "		this.onChange(); \r\n"+
            "}\r\n"+
            "\r\n"+
            "\r\n"+
            "TimeInput.prototype.parseText=function(val){\r\n"+
            "	if(val == null)\r\n"+
            "		return null;\r\n"+
            "	val = val.trim();\r\n"+
            "	val = val.toUpperCase();\r\n"+
            "	if(val.length == 0){\r\n"+
            "		return null;\r\n"+
            "//		return (new Date()).getTime() % 86400000;\r\n"+
            "	}\r\n"+
            "	var len = val.length;\r\n"+
            "	\r\n"+
            "	var meridiem;\r\n"+
            "	var millis;\r\n"+
            "	var seconds;\r\n"+
            "	var minutes;\r\n"+
            "	var hours;\r\n"+
            "	\r\n"+
            "	var isAM = val.endsWith(\"AM\");\r\n"+
            "	var isPM = val.endsWith(\"PM\");\r\n"+
            "	var hasMeridiem = isPM || isAM;\r\n"+
            "	if(hasMeridiem){\r\n"+
            "		if(isAM){\r\n"+
            "			val = val.split(\"AM\")[0];\r\n"+
            "		}\r\n"+
            "		if(isPM){\r\n"+
            "			val = val.split(\"PM\")[0];\r\n"+
            "		}\r\n"+
            "	}\r\n"+
            "	var hasMillis = val.indexOf('.') != -1;\r\n"+
            "	var millis;\r\n"+
            "	\r\n"+
            "	if(hasMillis){\r\n"+
            "		var c = val.split('.');\r\n"+
            "		millis = parseInt(c[1]);\r\n"+
            "		val = c[0];\r\n"+
            "	}\r\n"+
            "	else millis = 0;\r\n"+
            "	\r\n"+
            "	var tc = val.split(\":\");\r\n"+
            "	if(tc.length >= 1)\r\n"+
            "		hours = parseInt(tc[0]);\r\n"+
            "	else\r\n"+
            "		hours = 0;\r\n"+
            "	if(tc.length >= 2)\r\n"+
            "		minutes = parseInt(tc[1]);\r\n"+
            "	else \r\n"+
            "		minutes = 0;\r\n"+
            "	if(tc.length >= 3)\r\n"+
            "		seconds = parseInt(tc[2]);\r\n"+
            "	else \r\n"+
            "		seconds = 0;\r\n"+
            "		\r\n"+
            "	\r\n"+
            "	if(hours == 12)\r\n"+
            "		hours = 0;\r\n"+
            "	if(isPM)\r\n"+
            "		hours+=12;\r\n"+
            "//	if(hasMeridiem)\r\n"+
            "//		if(isPM && hours < 12){\r\n"+
            "//			hours += 12;\r\n"+
            "//		}else if(isAM && hours == 12)\r\n"+
            "//			hours=0;\r\n"+
            "	\r\n"+
            "	return 60*60*1000*hours+60*1000*minutes+1000*seconds+millis;\r\n"+
            "}\r\n"+
            "TimeInput.prototype.formatAsText=function(time){\r\n"+
            "	if(isNaN(time))\r\n"+
            "		return this.input.value;\r\n"+
            "	if(time == null)\r\n"+
            "		return \"\";\r\n"+
            "	//time is a long milliseconds since midnight\r\n"+
            "	//Get time components\r\n"+
            "	var millis = time % 1000;\r\n"+
            "	time = parseInt(time / 1000);\r\n"+
            "	var seconds = time % 60;\r\n"+
            "	time = parseInt(time / 60);\r\n"+
            "	var minutes = time % 60;\r\n"+
            "	time = parseInt(time / 60);\r\n"+
            "	var hours = time % 24;\r\n"+
            "	var meridiem=hours >= 12?\"PM\":\"AM\";\r\n"+
            "	\r\n"+
            "	if(hours >=12)hours = hours-12;\r\n"+
            "	if(hours == 0) hours = 12;\r\n"+
            "	\r\n"+
            "	//Pad 0's\r\n"+
            "	hours=hours<10?\"0\"+hours:hours;\r\n"+
            "	minutes=minutes<10?\"0\"+minutes:minutes;\r\n"+
            "	seconds=seconds<10?\"0\"+seconds:seconds;\r\n"+
            "	if(millis < 10)\r\n"+
            "		millis=\"00\" + millis;\r\n"+
            "	else if(millis < 100)\r\n"+
            "		millis=\"0\" + millis;\r\n"+
            "	\r\n"+
            "	//Generate Text\r\n"+
            "	var out = hours + \":\" + minutes + \":\" + seconds + \".\" + millis + meridiem;\r\n"+
            "	return out;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeInput.prototype.setTimeDisplayFormat=function(format){\r\n"+
            "	this.timeFormat=format;\r\n"+
            "}\r\n"+
            "\r\n"+
            "TimeInput.prototype.onKeyDownInput=function(e,idx){\r\n"+
            "	if (e.keyCode == 13 || e.keyCode == 27) { // enter or esc\r\n"+
            "		// TODO enter should close and save the value...\r\n"+
            "	  this.timeChooser.hide();\r\n"+
            "	}\r\n"+
            "}\r\n"+
            "");

	}
	
}