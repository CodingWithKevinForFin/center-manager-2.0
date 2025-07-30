function DateChooser(parentDiv,isRange){ 
  var that=this; 
  this.parentDiv=parentDiv; 
  this.parentDiv.style.width=toPx(isRange ? 180 : 80);
  this.window = getWindow(this.parentDiv);
  this.today=new Date(); 
  var thisYear=this.today.getFullYear();; 
  var thisMonth=this.today.getMonth(); 
  var thisDate=this.today.getDate(); 
  this.monthContainer;
  this.curCalendar;
  this.year=this.today.getFullYear();
  this.thisYmd=this.toYmd(thisYear,thisMonth+1,thisDate); 
  this.selYear=0; 
  this.selMonth=0; 
  this.selDate=0; 
  this.selYmd=0; 
  this.isRange=isRange;
  if(this.isRange){ 
    this.selYear2=0; 
    this.selMonth2=0; 
    this.selDate2=0; 
    this.selYmd2=0; 
  } 
  this.input=nw('input','cal_input'); 
  this.input.readOnly = true;
  this.input.size=8; 
  this.input.onclick=function(e){that.onClickInput(e,1);}; 
  this.input.onkeyup=function(e){that.onKeyUpInput(e,1);}; 
  this.input.onkeydown=function(e){that.onKeyDownInput(e,1);};
  this.input.onchange=function(e){that.onChange();};
  parentDiv.appendChild(this.input); 
  if(this.isRange){ 
    this.dash=nw('span');
    this.dash.innerHTML=' - '; 
    this.dash.className='cal_dash';
    this.dash.onclick=function(e){that.setValue('');that.setValue2('');that.onClickInput(e,1);that.onDashClicked(e);};
    parentDiv.appendChild(this.dash); 
    this.input2=nw('input','cal_input'); 
  	this.input2.readOnly = true;
    this.input2.size=8; 
    this.input2.onclick=function(e){that.onClickInput(e,2);}; 
    this.input2.onkeyup=function(e){that.onKeyUpInput(e,2);}; 
  	this.input2.onkeydown=function(e){that.onKeyDownInput(e,2);};
    this.input2.onchange=function(e){that.onChange();};
    parentDiv.appendChild(this.input2); 
  }
 	
  this.activeInput = null;
  this.activeYear = null;
  this.activeMonth = null;
  this.activeDate = null;
  // onEdit
  this.isOnEdit = false; 
  this.enableLastNDays=null;
  this.disableFutureDays;
  this.enableLastNDaysEnd=null;
  this.disableFutureDaysEnd;
  this.style=new Object();
} 

DateChooser.prototype.onDashClicked=function(e){ 
	// override
}

DateChooser.prototype.close=function(){ 
	this.hide();
	if(this.input!=null)
		parentDiv.removeChild(this.input);
	if(this.input2!=null)
		parentDiv.removeChild(this.input2);
}
DateChooser.prototype.setSelectedToday=function(){ 
  this.setSelected(this.today.getFullYear() ,this.today.getMonth()+1 ,this.today.getDate()); 
} 
DateChooser.prototype.setSelected=function(year,month,day){ 
  this.selYear=year; 
  this.selMonth=month-1; 
  this.selDate=day; 
  this.selYmd=this.toYmd(year,month,day); 
  if(this.rootDiv==null && this.selYmd>0) {
	var selDate=new Date(this.selYear,this.selMonth,this.selDate);
	var timeMs= selDate.getTime();
	var ret=formatDate(this.dateFormat,selDate,timeMs);
	var newText=ret?ret:this.selYmd;
	const oVal=this.input.value;
	this.input.value=newText; 
//	if (oVal!=newText)
//		this.ensureFieldsVisible();
  }
  else if (this.selYmd==0) {
	  this.input.value='';
  }
} 

DateChooser.prototype.setDateDisplayFormat=function(format){ 
	this.dateFormat=format;
}

DateChooser.prototype.setSelected2=function(year,month,day){ 
  this.selYear2=year; 
  this.selMonth2=month-1; 
  this.selDate2=day; 
  this.selYmd2=this.toYmd(year,month,day); 
  if(this.rootDiv==null && this.selYmd2>0) {
	var selDate=new Date(this.selYear2,this.selMonth2,this.selDate2);
	var timeMs= selDate.getTime();
	var ret=formatDate(this.dateFormat,selDate,timeMs);
	const oVal=this.input2.value;
	var newText=ret?ret:this.selYmd2;
	this.input2.value=newText; 
//	if (oVal!=newText)
//		this.ensureFieldsVisible();
  } 
  else if (this.selYmd2==0) {
	  this.input2.value='';
  }
} 
DateChooser.prototype.setColors=function(headerColor){ 
	this.headerColor = headerColor;
} 

DateChooser.prototype.onGlass=function(){ 
	// override
	// happens when user clicks outside the calendar
}

DateChooser.prototype.showCalendar=function(){ 
  var that=this; 
  this.dayDivs=[]; 
  var yearHeight=32; 
  var yearWidth=100; 
  var yearButtonHeight=32; 
  var yearButtonWidth=25; 
  var yearTopOffset=5;
//  var dayHeight=17; 
//  var dayWidth=18; 
  var dayHeight=19; 
  var dayWidth=25; 
  var monthWidth=dayWidth*7; 
  var monthHeight=dayHeight*8; 
  var monthPadding=5; 
  var otherYearWidth=18;
  var calPadding=5;
  var calWidth=(monthWidth+monthPadding)*2+ 2*calPadding;
  var calHeight=(monthHeight+monthPadding)*2;
 // var yearsBarWidth = calWidth-2*calPadding-2*26;
  var yearsBarWidth = 295;

  var thisYear=this.today.getFullYear();
  var curYear=this.activeYear > 0  ? this.activeYear:thisYear;
  var thisMonth=this.today.getMonth(); 
  var thisDate=this.today.getDate(); 
  if(this.rootDiv==null){ 
	var rect=new Rect();
	rect.readFromElement(this.input);
    this.rootDiv=nwDiv('cal_calendar',rect.getLeft(),rect.getTop()+rect.height,calWidth,calHeight); 
    this.glass = new Glass(this.window,this);
    this.glass.appendChild(this.rootDiv);
	this.glass.glassElement.onclick = function(e){
		// event hook
		// rootDiv should always exist when this fires
		that.onGlass(e);
	}
    this.glass.show();
  } 
  var headerWidth = calWidth-2*calPadding;
  var yearBarLeft = (headerWidth-yearsBarWidth)/2;
  var headerDiv = nwDiv('cal_header', calPadding,calPadding, headerWidth, yearHeight);
  
  var yearsBarDiv = nwDiv('cal_yearsBar', yearBarLeft, 0, yearsBarWidth, yearHeight);
  this.monthsBarDiv = nwDiv('cal_monthsBar', 5, 40, calWidth-2*calPadding, 50);
  this.monthContainer = nwDiv('cal_monthContainer', 5, 100, calWidth-2*calPadding, 160); 
  
  
  if (this.style.calendarBgColor) {
	  this.rootDiv.style.background= this.style.calendarBgColor;
	  headerDiv.style.background = this.style.calendarBgColor; 
	  yearsBarDiv.style.background = this.style.calendarBgColor;
  } else
	  headerDiv.style.background = "transparent";
  this.rootDiv.appendChild(headerDiv);
  this.rootDiv.appendChild(this.monthsBarDiv);
  this.rootDiv.appendChild(this.monthContainer);
  var yearDiv=nwDiv('cal_year',(yearsBarWidth-yearWidth)/2,yearTopOffset,yearWidth,yearHeight); 
  yearDiv.innerHTML=curYear;
  yearsBarDiv.appendChild(yearDiv);
  if(thisYear==this.activeYear) 
    yearDiv.className+=' cal_today'; 
  var yearBackButton=nwDiv('cal_yearButton cal_prev',0,0,yearButtonWidth,yearButtonHeight); 
  var yearNextButton=nwDiv('cal_yearButton cal_next',(calWidth-2*calPadding) - yearButtonWidth - 0,0,yearButtonWidth,yearButtonHeight); 
  if (this.style.selYearFgColor)
	  yearDiv.style.color=this.style.selYearFgColor;
  yearBackButton.innerHTML=''; 
  yearNextButton.innerHTML=''; 
  //yearBackButton.onclick=function(){that.setActiveYear(that.year-1)}; 
  //yearNextButton.onclick=function(){that.setActiveYear(that.year+1)}; 
  headerDiv.appendChild(yearsBarDiv);
  headerDiv.appendChild(yearBackButton); 
  headerDiv.appendChild(yearNextButton); 
  
  ensureInWindow(this.rootDiv);
	
  // years	
  var btns=Math.floor((calWidth-yearButtonWidth*2)/otherYearWidth/2)-1;
  for(var i=-btns;i<=btns;i++){
    if(i==0) continue;
    var btn=nwDiv('otherYear',yearsBarWidth/2+ (i*otherYearWidth)-yearHeight/2+(i<0 ? -10 : 10),yearTopOffset,yearHeight,yearHeight);
    if (this.style.yearFgColor) {
    	btn.style.color=this.style.yearFgColor;
    }
    var y=parseInt(curYear)+i;
    if(y==thisYear)
      btn.className+=' cal_today';
    btn.onclick=function(e){that.setActiveYear(getMouseTarget(e).year)};
    btn.year=y;
    y%=100;
    btn.innerHTML=(y<10 ? "'0" : "'") + y;
    yearsBarDiv.appendChild(btn);
  }
  this.populateMonthsBar();
  this.curCalendar = this.getCalendar(this.activeMonth, this.activeYear, this.activeDate);
  this.monthContainer.appendChild(this.curCalendar);
  
  // clear & close button
  //this.rootDiv.style.height=toPx(this.rootDiv.clientHeight + 20);
  this.clearBtn = nw("div", "cal_clear");
  this.closeBtn = nw("div", "cal_close");
  if (this.style.btnBgColor) {
	  this.closeBtn.style.background=this.style.btnBgColor;
	  this.clearBtn.style.background=this.style.btnBgColor;
  }
  
  var btnHeight = 20;
  var btnWidth = 100;
  var bottomPadding = 3;
  
  this.clearBtn.style.width=toPx(btnWidth);
  this.clearBtn.style.height=toPx(btnHeight);
  this.clearBtn.style.left = toPx(this.rootDiv.clientWidth/2 - btnWidth - 2);
  this.clearBtn.style.top = toPx(this.monthContainer.offsetTop + this.monthContainer.clientHeight + 25);
  if (this.isRange) {
  	if (this.isOnEdit)
  		this.clearBtn.textContent = "Clear Range";
  	else if (this.activeInput == this.input)
  		this.clearBtn.textContent = "Clear Start Date";
  	else if (this.activeInput == this.input2)
  		this.clearBtn.textContent = "Clear End Date";
  } else {
  		this.clearBtn.textContent = "Clear";
  }
  if (this.style.btnFgColor)
	  this.clearBtn.style.color=this.style.btnFgColor;
  
  this.closeBtn.style.width=toPx(btnWidth);
  this.closeBtn.style.height=toPx(btnHeight);
  this.closeBtn.style.left = toPx(this.rootDiv.clientWidth/2 + 2);
  this.closeBtn.style.top = toPx(this.monthContainer.offsetTop + this.monthContainer.clientHeight + 25);
  this.closeBtn.textContent = "Close";
  if (this.style.btnFgColor)
	  this.closeBtn.style.color=this.style.btnFgColor;
  
  this.rootDiv.appendChild(this.clearBtn);
  this.rootDiv.appendChild(this.closeBtn);
  this.clearBtn.onclick=function(e){that.onClickClearBtn();};
  this.closeBtn.onclick=function(e){that.onClickCloseBtn();that.handleCloseButton();};
  this.repaintDays(); 
} 

DateChooser.prototype.handleCloseButton=function() {
	// override
}

DateChooser.prototype.populateMonthsBar=function() {
	this.monthsBarDiv.innerHTML = "";
	var thisYear = this.today.getFullYear();
	var thisMonth = this.today.getMonth();
	for (var i = 0; i < monthNamesShortened.length; i++) {
	  	var that = this;
		var monthDiv = nwDiv('cal_monthDiv');
	 	monthDiv.innerHTML = monthNamesShortened[i];
	 	if (this.style.monthFgColor)
	 		monthDiv.style.color = this.style.monthFgColor;
	  	if(thisYear==this.year && i == thisMonth) {	  		
	    	monthDiv.classList.add("cal_today");
	  	}
	    
	    if ((typeof this.activeMonth != "undefined" || this.activeMonth != null) && i == this.activeMonth && !monthDiv.classList.contains("cal_clicked_month")) {
	    	monthDiv.classList.add("cal_clicked_month");
	    	if (this.style.selMonthFgColor) {
	    		monthDiv.style.color=this.style.selMonthFgColor;
	    	}
	    	if (this.style.selMonthBgColor) {	    		
	    		monthDiv.style.background=this.style.selMonthBgColor;
	    	}
	    }	else if ((typeof this.activeMonth == "undefined" || this.activeMonth == null) && i == thisMonth) {
	    	this.activeMonth = thisMonth;
	    	monthDiv.classList.add("cal_clicked_month");
	    	if (this.style.selMonthFgColor) {
	    		monthDiv.style.color=this.style.selMonthFgColor;
	    	}
	    	if (this.style.selMonthBgColor) {	    		
	    		monthDiv.style.background=this.style.selMonthBgColor;
	    	}
	    }
	  	monthDiv.onclick=function(e){
	  		that.onMonthClick(e);
	  	};
	  	this.monthsBarDiv.appendChild(monthDiv); 
	}
}
DateChooser.prototype.onMonthClick=function(e) {
	var monthMap = {Jan: 0, Feb: 1, Mar: 2, Apr: 3, May: 4, Jun: 5, Jul:6, Aug:7, Sep: 8, Oct:9, Nov: 10, Dec: 11};
	var monthDiv = getMouseTarget(e);
	monthDiv.classList.add("cal_clicked_month");
	var monthId = monthMap[monthDiv.textContent];
	this.activeMonth = monthId;
	// TODO we are repainting everything, which could be optimized...
	this.hideCalendar();
	this.showCalendar();
}
DateChooser.prototype.getCalendar=function(month, year, day) {
  var that = this;
  var dayHeight=22; 
  var dayWidth=50; 
  var monthWidth=dayWidth*7; 
  var monthHeight=dayHeight*8;
  var monthPad = 5;
  var thisYear=this.today.getFullYear(); 
  var thisMonth=this.today.getMonth(); 
  var thisDate=this.today.getDate(); 
	
  var onClickDayFunc=function(e){that.onClickDay(e)}; 
  var onOverDayFunc=function(e){that.onOverDay(e)}; 
  var onOutDayFunc=function(e){that.onOutDay(e)};
  var monthDivSize=this.monthContainer.clientWidth-2*monthPad;
  var monthDiv=nwDiv('cal_month',monthPad,0,monthDivSize,monthHeight); 

  for(var i in dayNames){ 
    var dayLabelDiv=nwDiv('cal_dayLabel',i*dayWidth,0,dayWidth,dayHeight); 
    if (this.style.weekBgColor)
    	dayLabelDiv.style.boxShadow= "inset 0px 0px 28px 4px" +this.style.weekBgColor;
	 dayLabelDiv.style.background= this.headerColor;
    if (this.style.weekFgColor)
    	dayLabelDiv.style.color=this.style.weekFgColor;
    dayLabelDiv.innerHTML=dayNames[i]; 
    monthDiv.appendChild(dayLabelDiv); 
  } 
  var firstDay=new Date(this.year,month,1).getDay(); 
  var daysInMonth=32-new Date(this.year,month,32).getDate(); 
  var dayInMonth=0; 
  outer:for(var week=0;week<6;week++){ 
    for(var day=week==0 ? firstDay : 0;day<7;day++){ 
      dayInMonth++; 
      if(dayInMonth>daysInMonth) 
        break; 
      var dayDiv=nwDiv('cal_day',day*dayWidth,(1+week)*dayHeight,dayWidth,dayHeight); 
      if (this.style.dayFgColor)
    	  dayDiv.style.color=this.style.dayFgColor;
      monthDiv.appendChild(dayDiv); 
      dayDiv.innerHTML=dayInMonth; 
      dayDiv.onclick=onClickDayFunc;
      dayDiv.onmouseover=onOverDayFunc;
      dayDiv.onmouseout=onOutDayFunc;		
      dayDiv.dayInMonth=dayInMonth; 
      dayDiv.dayInWeek=day; 
      dayDiv.month=month; 
      dayDiv.year=year; 
      dayDiv.ymd=this.toYmd(year,month+1,dayInMonth); 
      this.dayDivs[this.dayDivs.length]=dayDiv; 
    } 
  }
  this.repaintDays();
  this.monthContainer.innerHTML = "";
  return monthDiv;
}


DateChooser.prototype.onOutDay=function(e) {
	var el=getMouseTarget(e);
	// do not overwrite if the hovered day has been selected or is part of a range
	if (el.classList.contains("cal_selected") || el.classList.contains("cal_inrange"))
		return;
	if (this.style.calendarBgColor)
		el.style.background=this.style.calendarBgColor;
	else		
		el.style.background="transparent";
}
	
DateChooser.prototype.setBtnBgColor=function(color) {
	this.style.btnBgColor=color;
}

DateChooser.prototype.setBtnFgColor=function(color) {
	this.style.btnFgColor=color;
}

DateChooser.prototype.setCalendarBgColor=function(color) {
	this.style.calendarBgColor=color;
}

DateChooser.prototype.setYearFgColor=function(color) {
	this.style.yearFgColor=color;
}

DateChooser.prototype.setSelYearFgColor=function(color) {
	this.style.selYearFgColor=color;
}

DateChooser.prototype.setMonthFgColor=function(color) {
	this.style.monthFgColor=color;
}

DateChooser.prototype.setSelMonthBgColor=function(color) {
	this.style.selMonthBgColor=color;
}

DateChooser.prototype.setSelMonthFgColor=function(color) {
	this.style.selMonthFgColor=color;
}

DateChooser.prototype.setWeekFgColor=function(color) {
	this.style.weekFgColor=color;
}

DateChooser.prototype.setWeekBgColor=function(color) {
	this.style.weekBgColor=color;
}

DateChooser.prototype.setDayFgColor=function(color) {
	this.style.dayFgColor=color;
}

DateChooser.prototype.setXDayFgColor=function(color) {
	this.style.xDayFgColor=color;
}

DateChooser.prototype.setHoverBgColor=function(color) {
	this.style.hoverBgColor=color;
}

DateChooser.prototype.checkRightSettings= function(delta) {
	if (this.enableLastNDaysEnd == null) 
		return false;
	return delta > this.enableLastNDaysEnd;
}

DateChooser.prototype.checkLeftSettings= function(delta) {
	if (this.enableLastNDays == null)
		return false;
	return delta > this.enableLastNDays;
}

DateChooser.prototype.repaintDays=function(){ 
  var selYmd=this.selYmd;
  var selYmd2=this.selYmd2; 
//  if(this.isRange && this.hoverYmd>0){ 
//    if(this.state==1) 
//      selYmd=this.hoverYmd; 
//    else 
//      selYmd2=this.hoverYmd; 
//  } 
  var firstDate = new Date(this.dayDivs[0].year, this.dayDivs[0].month, this.dayDivs[0].dayInMonth);
  var today = new Date(this.today.getFullYear(), this.today.getMonth(), this.today.getDate());
  var delta = Math.floor((today - firstDate) / (1000 * 60 * 60 * 24));
  var left = this.activeInput == this.input? true:false;
  for(var i in this.dayDivs){ 
	var dayDiv=this.dayDivs[i]; 
    dayDiv.className='cal_day'; 
    if (this.style.calendarBgColor) {
    	dayDiv.style.background=this.style.calendarBgColor;
    }
    var day=dayDiv.dayInWeek; 
    var dayInMonth=dayDiv.dayInMonth; 
    var month=dayDiv.month; 
    var year=dayDiv.year; 
    var ymd=dayDiv.ymd; 
    if(day==0 || day==6) 
      dayDiv.className+=' cal_weekend'; 
    if(this.thisYmd==ymd) {
    	dayDiv.className+=' cal_today';
    }
    if(this.selYmd!=0 && this.selDate==dayInMonth && this.selYear==this.activeYear && this.selMonth==this.activeMonth){ 
    // this cell is the left calendar's selected day
      dayDiv.className+=' cal_selected';
      if (this.style.selMonthBgColor) {
			dayDiv.style.background=this.style.selMonthBgColor;
			dayDiv.style.filter='saturate(4)';
		}
    }else if(this.isRange && selYmd2!=0 && this.selDate2==dayInMonth && this.selYear2==this.activeYear && this.selMonth2==this.activeMonth){
    	// this cell is the right calendar's selected day
        dayDiv.className+=' cal_selected';
        if (this.style.selMonthBgColor) {
			dayDiv.style.background=this.style.selMonthBgColor;
			dayDiv.style.filter='saturate(4)';
		}
    } else if (selYmd != 0) {
    	// 1. if current cell is between two dates
    	// 2. if user hovers over a date that is between the left calendar's selected date and the hovered date while the right calendar is empty
    	if ((ymd > selYmd && ymd < selYmd2) || (selYmd2 == 0 && this.hoverYmd != 0 && ymd > selYmd && ymd < this.hoverYmd)) {
			// also paint in-range if start is set but end is not set
			dayDiv.className+=' cal_inrange';
			if (this.style.selMonthBgColor) {
				dayDiv.style.background=this.style.selMonthBgColor;
			}
    	}
    }

    // for right calendar, disable all days less than start date
    if (left==false && this.isRange && this.selYmd != 0 && ymd < this.selYmd) {
    	dayDiv.classList.add("day_disabled");
    	if (this.style.xDayFgColor)
    		dayDiv.style.color=this.style.xDayFgColor;
    }
    // for left calendar, disable all days greater then end date if end is selected
 	if (left==true && this.isRange && this.selYmd2 != 0 && ymd > this.selYmd2) {
    	dayDiv.classList.add("day_disabled");
    	if (this.style.xDayFgColor)
    		dayDiv.style.color=this.style.xDayFgColor;
 	} 
    // disable last n days
 	if (ymd <= this.thisYmd) {
 		if (left==true) {
 	 		if (this.checkLeftSettings(delta) == true) {
 	 			dayDiv.classList.add("day_disabled");
 	 			if (this.style.xDayFgColor)
 	 	    		dayDiv.style.color=this.style.xDayFgColor;
 	 	    	delta--;
 	 		}
 	 	} else {
 	 		if (this.checkRightSettings(delta) == true) {
 	 			dayDiv.classList.add("day_disabled");
 	 			if (this.style.xDayFgColor)
 	 	    		dayDiv.style.color=this.style.xDayFgColor;
 	 	    	delta--;
 	 		}
 	 	}
 	}

    //disable future days
    if (left==true) {
    	if (this.disableFutureDays == true && ymd > this.thisYmd) {
    		dayDiv.classList.add("day_disabled");
    		if (this.style.xDayFgColor)
        		dayDiv.style.color=this.style.xDayFgColor;
    	}
    		
    } else {
    	if (this.disableFutureDaysEnd == true && ymd > this.thisYmd) {
    		dayDiv.classList.add("day_disabled");
    		if (this.style.xDayFgColor)
        		dayDiv.style.color=this.style.xDayFgColor;
    	}
    }
  }
  

}
DateChooser.prototype.toYmd=function(y,m,d){ 
  return y*10000+m*100+d*1; 
} 

DateChooser.prototype.onClickInput=function(e,idx){ 
  this.state=idx; 
  var val=idx==1 ? this.input.value : this.input2.value;
  this.activeInput = idx==1 ? this.input : this.input2; 
  this.hoverYmd=0; 
  this.parseInput(val,idx);
  this.hideCalendar();  // why hide?
  this.showCalendar(); 
} 
DateChooser.prototype.onKeyUpInput=function(e,idx){ 
  var val=idx==1 ? this.input.value : this.input2.value; 
  this.state=idx; 
  this.hoverYmd=0; 
  if(e.keyCode==27 || e.keyCode==13){ 
    this.hideCalendar(); 
    return; 
  } 
  this.parseInput(val,idx); 
} 
DateChooser.prototype.onKeyDownInput=function(e,idx){
	if (e.keyCode == 13)
	  	this.hideCalendar();
}
DateChooser.prototype.parseInput=function(val,idx){ 
  val=val.toString(); 
  if(/^[0-9]*$/.test(val)){
    if(val.length>=4){ 
      var year=val.substring(0,4); 
      if(this.year!=year){ 
        //this.setActiveYear(year); 
        this.activeYear = year;
      } 
      if (val.length == 8) {
        this.activeYear = year;
        this.activeMonth = val.substring(4,6) - 1;
      }
      if(val.length==8 && val!=(idx==1 ? this.selYmd : this.selYmd2).toString()){ 
        if(idx==1)
          this.setSelected(year,val.substring(4,6),val.substring(6,8)); 
        else 
          this.setSelected2(year,val.substring(4,6),val.substring(6,8)); 
        
        if(this.rootDiv!=null) 
          this.repaintDays(); 
      } 
    } else if(val.length==0){
    	var changed=false;
        if(idx==1) {
          this.setSelected(0,0,0);
          // goes to end date
		  if (this.isRange && this.selYmd2 != 0) {
			this.activeMonth=this.selMonth2;
			this.activeDate = this.selDate2;
			this.setActiveYear(this.selYear2);
			changed=true;
		  }
        } else {
          this.setSelected2(0,0,0);
          // goes to start date
		  if (this.selYmd != 0) {
			  this.activeMonth=this.selMonth;
			  this.activeDate = this.selDate;
			  this.setActiveYear(this.selYear);
			  changed=true;
		  }

        }
        if (!changed) {
        	// defaults to current date if both calendars are empty
      		this.activeMonth=this.today.getMonth();
      		// below is needed in cases where you refresh the browser
      		this.activeYear=this.today.getFullYear();
      		this.setActiveYear(this.today.getFullYear());
      		this.activeDate = this.today.getDate();
        }
    }
  }
} 

DateChooser.prototype.hideCalendar=function(e){ 
	if(this.rootDiv!=null){ 
		this.glass.hide();
		this.glass=null;
		this.rootDiv=null; 
		this.input.focus();
	} 
} 

DateChooser.prototype.onClickDay=function(e){ 
	var el=getMouseTarget(e); 
	var year=el.year; 
	var month=el.month+1; 
	var day=el.dayInMonth; 
	var ymd=this.toYmd(year,month,day); 
	this.hideCalendar(); 
	if(!this.isRange){
		this.setSelected(year,month,day); 
	}else{
		if(this.state==2){
			this.setSelected2(year,month,day); 
		}else{
			this.setSelected(year,month,day); 
			this.state=2; 
		} 
	} 
	this.onChange(e);
} 
DateChooser.prototype.setOnEdit=function(isEdit) {
	this.isOnEdit = isEdit;
}
DateChooser.prototype.handleClickFromEditCell=function(e) {
 	var el=getMouseTarget(e); 
  	if (el.classList.contains("day_disabled")) {
		this.hideCalendar(); // repaint everything
		return;
  	}
 	var year=el.year; 
  	var month=el.month+1; 
  	var day=el.dayInMonth; 
  	var ymd=this.toYmd(year,month,day);
	this.hideCalendar();
  	if (this.isRange) {
	  	if (this.selYmd == 0) {
	    	this.setSelected(year,month,day); 
			this.state = 2;
	  	} else if (this.selYmd2 == 0) {
	      	this.setSelected2(year,month,day); 
	  	} 
  	} else {
		this.setSelected(year,month,day); 
  	}
	this.parentDiv.value = this.input.value;
	this.input.focus();
}
DateChooser.prototype.onClickClearBtn=function(e) {
	this.hoverYmd=0; 
	if (this.isOnEdit) {
		this.setSelected(0,0,0);
		this.setValue('');
		if (this.isRange) {
			this.setValue2('');
		}
		this.hideCalendar(); // repaint everything
		this.showCalendar();
		return;
	}
	if (this.activeInput == this.input) {
		this.setValue('');
	} else {
		this.setValue2('');
	}
	this.onChange(e);
	this.hideCalendar(); // hide/show to repaint everything
	this.showCalendar();
} 
DateChooser.prototype.onClickCloseBtn=function(e) {
	this.hideCalendar();
}
DateChooser.prototype.setDisableFutureDays=function(bools) {
	this.disableFutureDays = bools[0];
	this.disableFutureDaysEnd=bools[1];
}
DateChooser.prototype.setEnableLastNDays=function(days) {
	this.enableLastNDays=days[0];
	this.enableLastNDaysEnd=days[1];
}

//DateChooser.prototype.setDisableFutureDaysEnd=function(booleanVal) {
//	this.disableFutureDaysEnd = booleanVal;
//}
//DateChooser.prototype.setEnableLastNDaysEnd=function(nDays) {
//	if (typeof nDays !== 'string' || nDays.length == 0)
//		this.enableLastNDaysEnd = -1;
//	else
//		this.enableLastNDaysEnd = nDays;
//}

DateChooser.prototype.onOverDay=function(e){
	var el=getMouseTarget(e); 
	var year=el.year; 
	var month=el.month+1; 
	var day=el.dayInMonth; 
	var ymd=this.toYmd(year,month,day);
	this.hoverYmd=ymd;
	// no op if both calendars have been set AND current cell is either in range or selected
	if (this.selYmd > 0 && this.selYmd2 > 0 && (el.classList.contains("cal_selected") || el.classList.contains("cal_inrange")))
		return;
	// repaint days if end calendar is not filled, so we need to color all the days previous to the hovered day. Note that if start calendar is not filled but end calendar is filled, no color animation.
	if (this.selYmd2 == 0)
		this.repaintDays(); 
	// enforce hover color on the hovered day
	if (ymd != this.selYmd && ymd != this.selYmd2 && this.style.hoverBgColor) {
		el.style.background=this.style.hoverBgColor;
	}
} 

DateChooser.prototype.setActiveYear=function(year){ 
  this.year=1*year; 
  if(this.rootDiv!=null){ 
    var month = this.activeMonth != null ? this.activeMonth : this.today.getMonth();
    this.activeMonth = month;
    this.activeYear = year;
    this.hideCalendar(); // why repaint?
    this.showCalendar(); 
  } 
} 
DateChooser.prototype.onChange=function(e){ 
}
DateChooser.prototype.getValue=function(){ 
	return this.input.value;
}
DateChooser.prototype.getValues=function(){ 
	if(this.selYmd == 0) return null;
	var m = {};
	m.year = this.selYear;
	m.month = this.selMonth+1; // get actual month, not the index
	m.day = this.selDate;
	return m;
}
DateChooser.prototype.getValue2=function(){ 
	return this.isRange ? this.input2.value : null;
}
DateChooser.prototype.getValues2=function(){ 
	if(this.isRange){
		if(this.selYmd2 == 0)
			return null;
		var m = {};
		m.year = this.selYear2;
		m.month = this.selMonth2+1; // get actual month, not the index
		m.day = this.selDate2;
		return m;
	}
	return null;
}


DateChooser.prototype.setValue=function(yyyymmdd){ 
  this.parseInput(yyyymmdd,1); // also sets input value
} 

DateChooser.prototype.setValue2=function(yyyymmdd){ 
  this.parseInput(yyyymmdd,2); // also sets input value
} 

DateChooser.prototype.setContainerSize=function(w,h){
	// this.dash is the hyphen between the two fields in date range fields. It is affected by font size as well.
	if (this.dash != null && this.input2) {
		w  = w/2- this.dash.offsetWidth;
	} else if (this.input2) {
		w = w/2 - 6;
	}
	
	this.input.style.width=toPx(w);
	this.input.style.height=toPx(h);
	if(this.input2){
		this.input2.style.height=toPx(h);
		this.input2.style.width=toPx(w);
	}
}


DateChooser.prototype.getSelectedYmd=function(){
	return this.selYmd;
}
DateChooser.prototype.getSelectedYmd2=function(){
	return this.selYmd2;
}

function TimeChooser(t_input){
    this.window=getWindow(t_input.parentDiv);
    this.t_input = t_input;    
    
//    this.input2.onclick=function(e){that.onClickInput(e,2);}; 
//    this.input2.onkeyup=function(e){that.onKeyUpInput(e,2);}; 
}
TimeChooser.prototype.init=function(){
   	var input = this.t_input.input;
    if(this.rootDiv == null){
		var rect = new Rect();
		rect.readFromElement(input);
		this.rootDiv=nwDiv('time_clock', rect.getLeft(), rect.getTop()+rect.height, 184, 50);
		this.glass = new Glass(this.window,this);
		this.glass.appendChild(this.rootDiv);
		this.rootDiv.onclick=function(e){e.stopPropagation(); e.preventDefault();};

		var that = this;
		this.glass.glassElement.onclick = function(e){
			that.t_input.setValues(that.getValues(), true);
			that.t_input.timeChooser.hide();
			that.onGlass(e);
		};
		//this.glass.glassElement.onclick=function(e) {that.hide();}
		
		this.input_hours = nw("select","time_hours");
		this.input_minutes = nw("select","time_minutes");
		this.input_seconds = nw("select","time_seconds");
		this.input_millis = nw("input","time_millis");
		this.input_meridiem = nw("select","time_meridiem");
		
		this.input_millis.type="numbers";
		this.input_millis.min=0;
		this.input_millis.max=999;
	
		var option;
		//Hours
		for(var i = 1; i < 13; i++){
			option = document.createElement("option");
			
			option.text=i<10?"0"+i:""+i;
			this.input_hours.add(option);
		}
		//Minutes
		for(var i = 0; i < 60; i++){
			option = document.createElement("option");
			option.text=i<10?"0"+i:""+i;
			this.input_minutes.add(option);
		}
		//Seconds
		for(var i = 0; i < 60; i++){
			option = document.createElement("option");
			option.text=i<10?"0"+i:""+i;
			this.input_seconds.add(option);
		}
		//Meridiem
		option = document.createElement("option");
		option.text="AM";
		this.input_meridiem.add(option);
		option = document.createElement("option");
		option.text="PM";
		this.input_meridiem.add(option);
		
		
		this.rootDiv.appendChild(this.input_hours);
		this.rootDiv.appendChild(nw("span","time_colon"));
		this.rootDiv.appendChild(this.input_minutes);
		this.rootDiv.appendChild(nw("span","time_colon"));
		this.rootDiv.appendChild(this.input_seconds);
		this.rootDiv.appendChild(nw("span","time_period"));
		this.rootDiv.appendChild(this.input_millis);
		this.rootDiv.appendChild(this.input_meridiem);
	}
}

TimeChooser.prototype.onGlass=function(){
	// override
}
TimeChooser.prototype.show=function(){
    this.init();
    
    var time = this.t_input.getValues();
    if(this.t_input.getValue() == null){
    	var dtime = new Date();
    	time.hours = dtime.getHours();
    	time.minutes = dtime.getMinutes();
    	time.seconds = dtime.getSeconds();
    	time.millis = dtime.getMilliseconds();
    }
    this.loadValues(time);
    this.glass.show();
}
TimeChooser.prototype.hide=function(){
	if(this.rootDiv!=null){ 
		this.glass.hide();
		this.glass=null;
	    this.rootDiv=null; 
	    // will send blur to backend later so we shouldn't focus here
//	    this.t_input.input.focus();
	} 
}
TimeChooser.prototype.getValues=function(){
	var hours = parseInt(this.input_hours.value);
	var minutes = parseInt(this.input_minutes.value);
	var seconds = parseInt(this.input_seconds.value);
	var millis = parseInt(this.input_millis.value);
	var meridiem = this.input_meridiem.value;
	if(isNaN(millis))
		millis=0;
	millis = Math.min(Math.max(millis,0), 999);
	if(hours == 12)
		hours = 0;
	if(meridiem=="PM")
		hours+=12;
//	if(meridiem=="AM" && hours == 12)
//		hours = 0;
	
	return {hours:hours,minutes:minutes,seconds:seconds,millis:millis};
}

TimeChooser.prototype.loadValues=function(time){
    var hours = time.hours;
    var minutes = time.minutes;
    var seconds = time.seconds;
    var millis = time.millis;
	var meridiem=hours >= 12?"PM":"AM";
	
	if(hours == 0) hours = 12;
	if(hours >12)hours = hours-12;
	
	//Pad 0's
	hours=hours<10?"0"+hours:hours;
	minutes=minutes<10?"0"+minutes:minutes;
	seconds=seconds<10?"0"+seconds:seconds;
	if(millis < 10)
		millis="00" + millis;
	else if(millis < 100)
		millis="0" + millis;
    
    this.input_hours.value=hours;
	this.input_minutes.value=minutes;
	this.input_seconds.value=seconds;
    this.input_millis.value=millis;
	this.input_meridiem.value=meridiem;
}

function TimeInput(parentDiv){
	var that = this;
	this.parentDiv = parentDiv;
	this.window = getWindow(this.parentDiv);
	this.input = nw("input", "time_input"); //<input>
	this.input.onchange=function(e){
		if (that.input.value === "") {
			that.clear();
			that.onChange();
			if(that.timeChooser)
				that.timeChooser.hide();
		} else
			that.setValueLong(that.parseText(that.input.value), true);
	}
	this.input.onclick=function(e){that.timeChooser.show();}; 
	this.input.display="none";
	this.input.onkeydown=function(e){that.onKeyDownInput(e);};
	this.values = {};
	this.values.hours=null;
	this.values.minutes=null;
	this.values.seconds=null;
	this.values.millis=null;
	
	this.value = null; //long
	this.parentDiv.appendChild(this.input);
	this.timeChooser = new TimeChooser(this);
	
}
TimeInput.prototype.getInput=function(){
	return this.input;
}
TimeInput.prototype.getValue=function(){
	return this.value;
}
TimeInput.prototype.getValues=function(){
	return this.values;
}
TimeInput.prototype.onChange=function(){
	//Must be overwritten
}

TimeInput.prototype.clear=function(){
	this.values.hours=null;
	this.values.minutes=null;
	this.values.seconds=null;
	this.values.millis=null;
	this.value = null; //long
	this.input.value='';
}
TimeInput.prototype.setValueLong=function(v, fire){
	this.setValue(Math.floor(v/3600000), Math.floor(v/60000 % 60), Math.floor(v/1000 % 60), v%1000,fire);
}
TimeInput.prototype.setValues=function(v, fire){
	this.setValue(v.hours,v.minutes,v.seconds, v.millis,fire);
}
TimeInput.prototype.setValue=function(hours, minutes, seconds, millis, fire){
	var newValue = hours*1000*60*60+minutes*60*1000+seconds*1000+millis;
	if(this.value == newValue)
		return;
	this.value = newValue;
	this.values.hours = hours;
	this.values.minutes = minutes;
	this.values.seconds = seconds;
	this.values.millis = millis;
	const ret=formatTime(this.timeFormat,hours,minutes);
	const oVal=this.input.value;
	this.input.value=ret?ret:this.formatAsText(this.value);	
//	if (oVal != ret)
//		this.ensureFieldsVisible();
	if(fire==true)
		this.onChange(); 
}


TimeInput.prototype.parseText=function(val){
	if(val == null)
		return null;
	val = val.trim();
	val = val.toUpperCase();
	if(val.length == 0){
		return null;
//		return (new Date()).getTime() % 86400000;
	}
	var len = val.length;
	
	var meridiem;
	var millis;
	var seconds;
	var minutes;
	var hours;
	
	var isAM = val.endsWith("AM");
	var isPM = val.endsWith("PM");
	var hasMeridiem = isPM || isAM;
	if(hasMeridiem){
		if(isAM){
			val = val.split("AM")[0];
		}
		if(isPM){
			val = val.split("PM")[0];
		}
	}
	var hasMillis = val.indexOf('.') != -1;
	var millis;
	
	if(hasMillis){
		var c = val.split('.');
		millis = parseInt(c[1]);
		val = c[0];
	}
	else millis = 0;
	
	var tc = val.split(":");
	if(tc.length >= 1)
		hours = parseInt(tc[0]);
	else
		hours = 0;
	if(tc.length >= 2)
		minutes = parseInt(tc[1]);
	else 
		minutes = 0;
	if(tc.length >= 3)
		seconds = parseInt(tc[2]);
	else 
		seconds = 0;
		
	
	if(hours == 12)
		hours = 0;
	if(isPM)
		hours+=12;
//	if(hasMeridiem)
//		if(isPM && hours < 12){
//			hours += 12;
//		}else if(isAM && hours == 12)
//			hours=0;
	
	return 60*60*1000*hours+60*1000*minutes+1000*seconds+millis;
}
TimeInput.prototype.formatAsText=function(time){
	if(isNaN(time))
		return this.input.value;
	if(time == null)
		return "";
	//time is a long milliseconds since midnight
	//Get time components
	var millis = time % 1000;
	time = parseInt(time / 1000);
	var seconds = time % 60;
	time = parseInt(time / 60);
	var minutes = time % 60;
	time = parseInt(time / 60);
	var hours = time % 24;
	var meridiem=hours >= 12?"PM":"AM";
	
	if(hours >=12)hours = hours-12;
	if(hours == 0) hours = 12;
	
	//Pad 0's
	hours=hours<10?"0"+hours:hours;
	minutes=minutes<10?"0"+minutes:minutes;
	seconds=seconds<10?"0"+seconds:seconds;
	if(millis < 10)
		millis="00" + millis;
	else if(millis < 100)
		millis="0" + millis;
	
	//Generate Text
	var out = hours + ":" + minutes + ":" + seconds + "." + millis + meridiem;
	return out;
}

TimeInput.prototype.setTimeDisplayFormat=function(format){
	this.timeFormat=format;
}

TimeInput.prototype.onKeyDownInput=function(e,idx){
	if (e.keyCode == 13 || e.keyCode == 27) { // enter or esc
		// TODO enter should close and save the value...
	  this.timeChooser.hide();
	}
}
