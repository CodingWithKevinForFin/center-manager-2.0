package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class portal_portal_jsp_1 extends AbstractHttpHandler{

	public portal_portal_jsp_1() {
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
          if(com.f1.http.HttpUtils.toBoolean(com.f1.suite.web.portal.impl.BasicPortletManager.processPgid(request))){
            out.print(
              "\r\n"+
              "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\"> \r\n"+
              "<meta name=\"viewport\" content=\"width=100 , initial-scale=1, maximum-scale=1, user-scalable=0\"/> <!--320-->\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/gzip.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/textencoding-minified.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/utils.js?v=1\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/scrollbar.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/menu.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/math.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/portal.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/form.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/tabs.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/sparks.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/fasttable.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/fasttree.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/fastchart.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/fasttext.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/cal.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/slider.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/webgl-minified.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/3d.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/colorpicker.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/treemap.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/tiles.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/graph.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/helpbox.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/keymousemanager.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/desktop.js\"></script>\r\n"+
              "<script type=\"text/javascript\" src=\"rsc/javascript/html2canvas.js\"></script>\r\n"+
              "\r\n"+
              "\r\n"+
              "\r\n"+
              "");
            com.f1.suite.web.portal.impl.BasicPortletManager.onPageLoad(request);
            out.print(
              "\r\n"+
              "\r\n"+
              "<script>\r\n"+
              "\r\n"+
              "  if(window.titleOverride)\r\n"+
              "    window.document.title=titleOverride;\r\n"+
              "  else \r\n"+
              "    window.document.title='");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("webTitle"));
            out.print(
              "';\r\n"+
              "</script>\r\n"+
              "<HEAD>\r\n"+
              "  <link rel=\"icon\" href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("favIcon"));
            out.print(
              "\" />\r\n"+
              "</HEAD>\r\n"+
              "<body oncontextmenu='return false;' style='position:fixed;top:0px;left:0px;right:0px;bottom:0px'>\r\n"+
              "<HTML>\r\n"+
              "<style>\r\n"+
              "html {\r\n"+
              "  -webkit-box-sizing: border-box;\r\n"+
              "  -moz-box-sizing: border-box;\r\n"+
              "  box-sizing: border-box;\r\n"+
              "}\r\n"+
              "*, *:before, *:after {\r\n"+
              "  -webkit-box-sizing: inherit;\r\n"+
              "  -moz-box-sizing: inherit;\r\n"+
              "  box-sizing: inherit;\r\n"+
              "  outline: none;\r\n"+
              "  /*IOS SPECIFIC FOR MOBILE SCROLLING*/\r\n"+
              "  -webkit-touch-callout: none;\r\n"+
              "  touch-action: manipulation;\r\n"+
              "  }\r\n"+
              "button:active {\r\n"+
              "	transform: scale(0.97);\r\n"+
              "	filter: saturate(2) contrast(1.5);\r\n"+
              "}\r\n"+
              "button{\r\n"+
              "	cursor:pointer;\r\n"+
              "}\r\n"+
              "  .rotate90{\r\n"+
              "    transform: rotate(90deg);\r\n"+
              "	transform-origin: left top 0;\r\n"+
              "  }\r\n"+
              "  .dialog_alert{\r\n"+
              "    text-align:center;\r\n"+
              "    width:100%;\r\n"+
              "    padding:10px;\r\n"+
              "    font-size:18px;\r\n"+
              "    color: #000000;\r\n"+
              "  }\r\n"+
              "  .gradientpicker_dragger{\r\n"+
              "    border:1px outset #666666;\r\n"+
              "    border-width:1px 1px;\r\n"+
              "  }\r\n"+
              "  .shadow{\r\n"+
              "        background-color:black;\r\n"+
              "        opacity:.3;\r\n"+
              "  }\r\n"+
              "  .gradientpicker_rangeinput{\r\n"+
              "     color:#0000AA;\r\n"+
              "     border:1px solid #0000AA;\r\n"+
              "  }\r\n"+
              "  .gradientpicker_range{\r\n"+
              "     color:#0000AA;\r\n"+
              "  }\r\n"+
              "  .gradientpicker_autorange{\r\n"+
              "     text-align:center;\r\n"+
              "     color:#0000AA;\r\n"+
              "  }\r\n"+
              "  .gradientpicker{\r\n"+
              "    font-family:arial;\r\n"+
              "    font-size:12px;\r\n"+
              "  }\r\n"+
              "  .gradientpicker_label{\r\n"+
              "    border:1px outset #666666;\r\n"+
              "    font-size:12px;\r\n"+
              "    margin:0px;\r\n"+
              "  }\r\n"+
              "  .gradientpicker_edit{\r\n"+
              "    border:1px outset #aaaaaa;\r\n"+
              "    cursor:pointer;\r\n"+
              "  }\r\n"+
              "  .gradientpicker_edit:hover{\r\n"+
              "    border-color:black !important;\r\n"+
              "  }\r\n"+
              "  .gradientpicker_delete{\r\n"+
              "    border:1px outset #666666;\r\n"+
              "    background:#666666;\r\n"+
              "    color:white;\r\n"+
              "    margin:0px;\r\n"+
              "    font-size:11px;\r\n"+
              "    font-family:arial;\r\n"+
              "    vertical-align:top;\r\n"+
              "    cursor:pointer;\r\n"+
              "  }\r\n"+
              "  .gradientpicker_delete:hover{\r\n"+
              "    border-color:black !important;\r\n"+
              "    background:black;\r\n"+
              "    color:yellow;\r\n"+
              "  }\r\n"+
              "  .colorchooser_label{\r\n"+
              "    font-family:arial;\r\n"+
              "    font-size:12px;\r\n"+
              "  }\r\n"+
              "  .colorchooser_pallete{\r\n"+
              "    background:#EEEEEE;\r\n"+
              "    border:1px solid #666666;\r\n"+
              "  }\r\n"+
              "  .colorchooser_pallete_item{\r\n"+
              "    border:1px solid #CCCCCC;\r\n"+
              "    cursor:pointer;\r\n"+
              "  }\r\n"+
              "  .slider{\r\n"+
              "    border:1px solid #aaaaaa;\r\n"+
              "    background:white;\r\n"+
              "    z-index:1;\r\n"+
              "    display:inline-flex !important;\r\n"+
              "    align-items:center;\r\n"+
              "  }\r\n"+
              "  .slider_guide{\r\n"+
              "     background:#AAAAAA;\r\n"+
              "     cursor:pointer;\r\n"+
              "     /*box-shadow:0 0 2px 0px grey inset;  */\r\n"+
              "  }\r\n"+
              "  .slider_grabber{\r\n"+
              "     background:#888888;\r\n"+
              "/*      border:1px outset grey; */\r\n"+
              "     /*box-shadow: 0 0 4px -1px grey, 0 0 1px 0px rgba(0,0,0,0.5);  */\r\n"+
              "     cursor:pointer;\r\n"+
              "  }\r\n"+
              "  .slider_min{\r\n"+
              "     font-family:arial;\r\n"+
              "     font-size:12px;\r\n"+
              "     text-align:left;\r\n"+
              "     cursor:pointer;\r\n"+
              "  }\r\n"+
              "  .slider_max{\r\n"+
              "     font-family:arial;\r\n"+
              "     font-size:12px;\r\n"+
              "     text-align:right;\r\n"+
              "     cursor:pointer;\r\n"+
              "  }\r\n"+
              "  .slider_val:hover{\r\n"+
              "    border: 1px inset gray !important;\r\n"+
              "    }\r\n"+
              "  .slider_val {\r\n"+
              "    /*border: 1px solid transparent;*/\r\n"+
              "    border-width: 1px;\r\n"+
              "    border-style: solid;\r\n"+
              "    background:none;\r\n"+
              "    }\r\n"+
              "  .text{\r\n"+
              "     background:white;\r\n"+
              "  }\r\n"+
              "  .text_label{\r\n"+
              "     font-family:courier;\r\n"+
              "     font-size:11px;\r\n"+
              "     text-align:right;\r\n"+
              "     padding:0px 5px 0px 0px;\r\n"+
              "     color:#777777;\r\n"+
              "     background:#eeeeee;\r\n"+
              "  }\r\n"+
              "  .text_contents{\r\n"+
              "     font-family:courier;\r\n"+
              "     font-size:11px;\r\n"+
              "     cursor: text;\r\n"+
              "  }\r\n"+
              "  .text_selected{\r\n"+
              "    /*background:#dddddd !important;*/\r\n"+
              "    background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQYV2PYeeZXHgAHrwLunYbV6QAAAABJRU5ErkJggg==);\r\n"+
              "    \r\n"+
              "    color:black !important;\r\n"+
              "  }\r\n"+
              "  .text_search{\r\n"+
              "     background:#eeeeee;\r\n"+
              "     text-align:right;\r\n"+
              "  }\r\n"+
              "  .progress_bar{\r\n"+
              "     width:0%;\r\n"+
              "     height:100%;\r\n"+
              "     border-width:0px 1px 0px 0px;\r\n"+
              "     border-style:solid;\r\n"+
              "     border-color:rgb(180,235,180);\r\n"+
              "    background-image: -webkit-gradient( linear, left bottom, left top,\r\n"+
              "	    color-stop(1.00, rgb(160,245,190)),\r\n"+
              "	    color-stop(0.75, rgb(205,255,235)),\r\n"+
              "	    color-stop(0.6, rgb(205,255,235)),\r\n"+
              "	    color-stop(0.0, rgb(150,235,180))\r\n"+
              "	    );\r\n"+
              "  }\r\n"+
              "  .progress_bar_message{\r\n"+
              "     text-align:center;\r\n"+
              "     width:100%;\r\n"+
              "     height:100%;\r\n"+
              "  }\r\n"+
              "  .progress_bar_outer{\r\n"+
              "     background:#FFFFFF;\r\n"+
              "     border:1px solid black;\r\n"+
              "  }\r\n"+
              "  .progress_bar_back{\r\n"+
              "     background:#FFFFFF;\r\n"+
              "     width:100%;\r\n"+
              "     height:100%;\r\n"+
              "  }\r\n"+
              "  .cal_year, .cal_yearButton, .cal_calendar, .cal_month, .cal_monthLabel, .cal_dayLabel, .cal_day { \r\n"+
              "    font-family:arial; \r\n"+
              "    font-size:12px; \r\n"+
              "    text-align:center; \r\n"+
              "    font-weight:normal; \r\n"+
              "  } \r\n"+
              "  .cal_input{ \r\n"+
              "  width:70px; \r\n"+
              "  	display:inline-block;\r\n"+
              "  	position:relative;\r\n"+
              "  } \r\n"+
              "  .cal_dash{ \r\n"+
              "     cursor:crosshair; \r\n"+
              "  } \r\n"+
              "  .cal_calendar{ \r\n"+
              "    background:white; \r\n"+
              "/*     border:1px solid #777777;  */\r\n"+
              "    box-shadow: 0 0 6px -1px #777777;\r\n"+
              "    z-index:9999;\r\n"+
              "  } \r\n"+
              "  .cal_month{ \r\n"+
              "    color:#000033;\r\n"+
              "    width: 100%; \r\n"+
              "  } \r\n"+
              "  .cal_month_click_year {\r\n"+
              "  	display: none;\r\n"+
              "  }\r\n"+
              "  .cal_monthLabel{ \r\n"+
              "  	padding-top:2px;\r\n"+
              "  	width: 100% !important;\r\n"+
              "  } \r\n"+
              "  .cal_year{ \r\n"+
              "    font-weight:bold; \r\n"+
              "    font-size:14px; \r\n"+
              "    color:#000000; \r\n"+
              "  } \r\n"+
              "  .cal_yearsBar{\r\n"+
              "  	background: white; \r\n"+
              "  }\r\n"+
              "  .cal_monthsBar {\r\n"+
              "  	display: flex;\r\n"+
              "  	justify-content: center;\r\n"+
              "  	align-items: center;\r\n"+
              "  	flex-wrap: wrap;\r\n"+
              "  }\r\n"+
              "  .cal_monthDiv {\r\n"+
              "  	width: 60px;\r\n"+
              "    height: 20px;\r\n"+
              "    font-size: 13px;\r\n"+
              "    position: relative;\r\n"+
              "    font-weight: bold;\r\n"+
              "    cursor: pointer;\r\n"+
              "    display: flex;\r\n"+
              "    justify-content: center;\r\n"+
              "    align-items: center;\r\n"+
              "    color: #0e91bb;\r\n"+
              "  }\r\n"+
              "  .cal_clicked_month {\r\n"+
              "  	/*box-shadow: inset 0px 0px 9px 3px #77cefa;*/\r\n"+
              "  	background: #0e91bb;\r\n"+
              "  	color: white;\r\n"+
              "  }\r\n"+
              "  .otherYear{ \r\n"+
              "    font-weight:normal; \r\n"+
              "    font-size:11px; \r\n"+
              "    color:#f16900; \r\n"+
              "/*     color:white; */\r\n"+
              "    cursor:pointer; \r\n"+
              "/*   	text-shadow:1px 1px 1px #4d4d4d, 1px 1px 0px #b8b8b8; */\r\n"+
              "  } \r\n"+
              "  .otherYear:hover{ \r\n"+
              "    text-decoration:underline; \r\n"+
              "  }\r\n"+
              "  .cal_header > .cal_year{\r\n"+
              "  	text-shadow:-1px -1px 0px #cccccc, -1px -1px 1px rgba(255,255,255,0.5);\r\n"+
              "  }\r\n"+
              "  .cal_header > .cal_today{\r\n"+
              "  	text-shadow:1px 1px 1px #4d4d4d, 1px 1px 0px black;\r\n"+
              "    color:#ccc366 !important; \r\n"+
              "  }\r\n"+
              "  .cal_today{ \r\n"+
              "    color:#000000; \r\n"+
              "    font-weight: bold;\r\n"+
              "    text-decoration: underline;\r\n"+
              "    font-size: 15px;\r\n"+
              "  } \r\n"+
              "  .cal_yearButton.cal_prev{\r\n"+
              "  	background-image:url(\"data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' version='1.1' height='32px' width='25px'><text class='cal_year_svg' x='4' y='21' fill='white' font-size='16'>&#9668;</text></svg>\");\r\n"+
              "  }\r\n"+
              "  .cal_yearButton.cal_next{\r\n"+
              "  	background-image:url(\"data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' version='1.1' height='32px' width='25px'><text class='cal_year_svg' x='4' y='21' fill='white' font-size='16'>&#9658;</text></svg>\");\r\n"+
              "  }\r\n"+
              "  .cal_yearButton{ \r\n"+
              "    cursor:pointer; \r\n"+
              "	color:white;\r\n"+
              "	border-radius:3px;\r\n"+
              "	background-repeat:no-repeat;\r\n"+
              "	display: none;\r\n"+
              "  } \r\n"+
              "  \r\n"+
              "  .cal_yearButton:active{\r\n"+
              "  	opacity:1 !important;\r\n"+
              "  	box-shadow: 0px 1px 4px -1px #333333 inset;\r\n"+
              "  }\r\n"+
              "  .cal_yearButton:hover{\r\n"+
              "  	opacity:0.75;\r\n"+
              "  	transform:translateY(1px);\r\n"+
              "  	-webkit-transform:translateY(1px);\r\n"+
              "  	transition:0.1s\r\n"+
              "  	-webkit-transition:0.1s;\r\n"+
              "  }\r\n"+
              "  .cal_dayLabel{\r\n"+
              "  	color: #ffffff;\r\n"+
              "    border-width: 0px 0px 0px 1px;\r\n"+
              "    /* text-shadow: 1px 1px 1px #4d4d4d; */\r\n"+
              "    padding-top: 2px;\r\n"+
              "    box-shadow: inset 0px 0px 28px 4px #0e91bb;\r\n"+
              "    font-weight: bold;\r\n"+
              "    display: flex;\r\n"+
              "    justify-content: center;\r\n"+
              "    align-items: center;\r\n"+
              "  } \r\n"+
              "\r\n"+
              "  .cal_day{ \r\n"+
              "    cursor:pointer; \r\n"+
              "    border-width:1px 0px 0px 1px; \r\n"+
              "    color:#000099; \r\n"+
              "    background:#ffffff; \r\n"+
              "  	padding-top:2px;\r\n"+
              "  	display: flex;\r\n"+
              "    justify-content: center;\r\n"+
              "    align-items: center;\r\n"+
              "  } \r\n"+
              "  .cal_weekend{ \r\n"+
              "    color:#5555FF; \r\n"+
              "  } \r\n"+
              "  .cal_selected{ \r\n"+
              "  	background-color:#0E91BB;\r\n"+
              "    color:white; \r\n"+
              "  } \r\n"+
              "  .cal_inrange{ \r\n"+
              "    color:white; \r\n"+
              "  } \r\n"+
              "  .graph_panel_selectbox{ \r\n"+
              "    background:none;\r\n"+
              "    border:1px dashed black;\r\n"+
              "  }\r\n"+
              "  .graph_panel{ \r\n"+
              "    background-color:white; \r\n"+
              "    left:0px;\r\n"+
              "    right:0px;\r\n"+
              "    top:0px;\r\n"+
              "    bottom:0px;\r\n"+
              "  } \r\n"+
              "  .graph_node{ \r\n"+
              "  	color:black;\r\n"+
              "    background-color:#DDDEEE; \r\n"+
              "    text-align:center;\r\n"+
              "    overflow:hidden;\r\n"+
              "    border:2px outset #aaaaaa;\r\n"+
              "    border-radius: 4px;\r\n"+
              "    -moz-border-radius: 4px;\r\n"+
              "    cursor:move;\r\n"+
              "  } \r\n"+
              "  .graph_node.graph_node_selected{ \r\n"+
              "/*     border:3px solid #6688FF !important; */\r\n"+
              "/*     border: 2px solid #e57930 !important; */\r\n"+
              "	box-shadow: 0px 0px 1px 2px #e57930;\r\n"+
              "  }\r\n"+
              "  .tiles_panel{ \r\n"+
              "    background-color:white; \r\n"+
              "  } \r\n"+
              "  .shortcut{ \r\n"+
              "    background-repeat:no-repeat !important;\r\n"+
              "    background-position:center  bottom!important;\r\n"+
              "    font-weight:bold;\r\n"+
              "  }\r\n"+
              "  .tiles_tile{\r\n"+
              "  	padding:1px; \r\n"+
              "	outline:none;\r\n"+
              "    text-align:center;\r\n"+
              "    border:1px outset #eeeeee;\r\n"+
              "    cursor:pointer;\r\n"+
              "    color:black;\r\n"+
              "    border-radius: 10px;\r\n"+
              "    -moz-border-radius: 10px;\r\n"+
              "    overflow:hidden;\r\n"+
              "  } \r\n"+
              "  .tiles_tile.tiles_selected{ \r\n"+
              "    border:3px solid #6688FF;\r\n"+
              "  }\r\n"+
              "  .tiles_tile.tiles_active{ \r\n"+
              "    border:3px solid #0000FF;\r\n"+
              "  }\r\n"+
              "  .portlet_select_value{\r\n"+
              "    border:1px outset grey;\r\n"+
              "    padding:1px;\r\n"+
              "    margin:1px;\r\n"+
              "    height:17px;\r\n"+
              "    background:#cccccc;\r\n"+
              "    cursor:pointer;\r\n"+
              "  }\r\n"+
              "  /*\r\n"+
              "  div.checkboxField{\r\n"+
              "  	-webkit-appearance:none;\r\n"+
              "  	-moz-appearance:none;\r\n"+
              "  	border:none;\r\n"+
              "	outline:none;\r\n"+
              "	background-none;\r\n"+
              "	background-image: url(\"data:image/svg+xml;utf8,<svg preserveAspectRatio='xMinYMin' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'>\\\r\n"+
              "							<defs>\\\r\n"+
              "									<linearGradient id='fillcolor' x1='0%' y1='0%' x2='0%' y2='100%'>\\\r\n"+
              "										<stop offset='0%' style='stop-color:rgb(240,240,240);stop-opacity:1' />\\\r\n"+
              "   										<stop offset='100%' style='stop-color:rgb(222,222,222);stop-opacity:1' />\\\r\n"+
              "									</linearGradient>\\\r\n"+
              "							</defs>\\\r\n"+
              "							<rect width='96' height='96' x='2' y='2' fill='url(%23fillcolor)' stroke='%23aeaeae' stroke-width='2' vector-effect='non-scaling-stroke' stroke-linecap='round' \\\r\n"+
              "						/></svg>\");\r\n"+
              "	background-repeat: no-repeat;\r\n"+
              "	background-position: center;\r\n"+
              "	filter:drop-shadow(0px 1px 0.5px rgba(206,206,206,1));\r\n"+
              "	dis");
            out.print(
              "play:inline-block;\r\n"+
              "  }\r\n"+
              "  div.checkboxField.checked{\r\n"+
              "  	background-image: url(\"data:image/svg+xml;utf8,<svg preserveAspectRatio='xMinYMin' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'>\\\r\n"+
              "							<defs>\\\r\n"+
              "									<linearGradient id='fillcolor' x1='0%' y1='0%' x2='0%' y2='100%'>\\\r\n"+
              "										<stop offset='0%' style='stop-color:rgb(240,240,240);stop-opacity:1' />\\\r\n"+
              "   										<stop offset='100%' style='stop-color:rgb(222,222,222);stop-opacity:1' />\\\r\n"+
              "									</linearGradient>\\\r\n"+
              "							</defs>\\\r\n"+
              "							<rect width='96' height='96' x='2' y='2' fill='url(%23fillcolor)' stroke='%23aeaeae' stroke-width='2' vector-effect='non-scaling-stroke' stroke-linecap='round' />\\\r\n"+
              "							<path fill='%23424242' width='96' height='96' d='M29 42 L14 56 L41 83 L85 28 L70 15 L85 28 L69 15 L39 52 Z'/>\\\r\n"+
              "						</svg>\");\r\n"+
              "  }\r\n"+
              "  div.checkboxField:hover{\r\n"+
              "  	filter:drop-shadow(0px 1px 1px rgba(206,206,206,1)) brightness(103%);\r\n"+
              "  }\r\n"+
              "  div.checkboxField:active{\r\n"+
              "  	filter:brightness(97%);\r\n"+
              "  }\r\n"+
              "  div.checkboxField.switch{\r\n"+
              "	background-image: url(\"data:image/svg+xml;utf8,<svg preserveAspectRatio='xMinYMin' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 100'>\\\r\n"+
              "							<defs>\\\r\n"+
              "									<linearGradient id='fillcolor' x1='0%' y1='0%' x2='0%' y2='100%'>\\\r\n"+
              "										<stop offset='0%' style='stop-color:rgb(155,39,23);stop-opacity:1' />\\\r\n"+
              "   										<stop offset='100%' style='stop-color:rgb(135,19,3);stop-opacity:1' />\\\r\n"+
              "									</linearGradient>\\\r\n"+
              "									<linearGradient id='fillcolor2' x1='0%' y1='0%' x2='0%' y2='100%'>\\\r\n"+
              "										<stop offset='0%' style='stop-color:rgb(115,33,22);stop-opacity:1' />\\\r\n"+
              "   										<stop offset='100%' style='stop-color:rgb(95,13,2);stop-opacity:1' />\\\r\n"+
              "									</linearGradient>\\\r\n"+
              "									<filter id='ds' width='120%' height='120%' color-interpolation-filters='sRGB'>\\\r\n"+
              "										<feOffset result='o1' in='SourceAlpha' dx='1' dy='2'/>\\\r\n"+
              "										<feColorMatrix result='o2' in='o1' type='matrix' values='0 0 0 0 0.4 0 0 0 0 0.4 0 0 0 0 0.4 0 0 0 1 0'/>\\\r\n"+
              "										<feGaussianBlur result='o3' in='o2' stdDeviation='2'/>\\\r\n"+
              "										<feBlend in='SourceGraphic' in2='o3' mode='normal'/>\\\r\n"+
              "									</filter>\\\r\n"+
              "							</defs>\\\r\n"+
              "							<rect width='196' height='96' x='2' y='2' rx='8' ry='8' fill='url(%23fillcolor)' stroke-width='0'/>\\\r\n"+
              "							<rect width='50' height='90' x='5' y='5' rx='8' ry='8' fill='url(%23fillcolor2)' stroke-width='0'/>\\\r\n"+
              "							<rect width='20' height='92' x='45' y='4' rx='16' ry='2' fill='white' stroke-width='0' filter='url(%23ds)'/>\\\r\n"+
              "							<text x='85' y='65' font-size='40' font-family='Arial' stroke='white' fill='white'>OFF</text>\\\r\n"+
              "						</svg>\");\r\n"+
              "\r\n"+
              "  }\r\n"+
              "  div.checkboxField.switch.checked{\r\n"+
              "	  background-image: url(\"data:image/svg+xml;utf8,<svg preserveAspectRatio='xMinYMin' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 100'>\\\r\n"+
              "								<defs>\\\r\n"+
              "										<linearGradient id='fillcolor' x1='0%' y1='0%' x2='0%' y2='100%'>\\\r\n"+
              "											<stop offset='0%' style='stop-color:rgb(26,157,91);stop-opacity:1' />\\\r\n"+
              "	   										<stop offset='100%' style='stop-color:rgb(6,137,71);stop-opacity:1' />\\\r\n"+
              "										</linearGradient>\\\r\n"+
              "										<linearGradient id='fillcolor2' x1='0%' y1='0%' x2='0%' y2='100%'>\\\r\n"+
              "											<stop offset='0%' style='stop-color:rgb(24,118,71);stop-opacity:1' />\\\r\n"+
              "	   										<stop offset='100%' style='stop-color:rgb(4,98,51);stop-opacity:1' />\\\r\n"+
              "										</linearGradient>\\\r\n"+
              "										<filter id='ds' width='120%' height='120%' color-interpolation-filters='sRGB'>\\\r\n"+
              "											<feOffset result='o1' in='SourceAlpha' dx='1' dy='2'/>\\\r\n"+
              "											<feColorMatrix result='o2' in='o1' type='matrix' values='0 0 0 0 0.4 0 0 0 0 0.4 0 0 0 0 0.4 0 0 0 1 0'/>\\\r\n"+
              "											<feGaussianBlur result='o3' in='o2' stdDeviation='2'/>\\\r\n"+
              "											<feBlend in='SourceGraphic' in2='o3' mode='normal'/>\\\r\n"+
              "										</filter>\\\r\n"+
              "								</defs>\\\r\n"+
              "								<rect width='196' height='96' x='2' y='2' rx='8' ry='8' fill='url(%23fillcolor)' stroke-width='0'/>\\\r\n"+
              "								<rect width='50' height='90' x='145' y='5' rx='8' ry='8' fill='url(%23fillcolor2)' stroke-width='0'/>\\\r\n"+
              "								<rect width='20' height='92' x='135' y='4' rx='16' ry='2' fill='white' stroke-width='0' filter='url(%23ds)'/>\\\r\n"+
              "								<text x='35' y='65' font-size='40' font-family='Arial' stroke='white' fill='white'>ON</text>\\\r\n"+
              "							</svg>\");\r\n"+
              "  }\r\n"+
              "  */\r\n"+
              "  .ckMark {\r\n"+
              "  	color: #5b5959;\r\n"+
              "  	font-weight: bold;\r\n"+
              "  }\r\n"+
              "  .ckFieldWrapper {\r\n"+
              "	outline:none;\r\n"+
              "  	background: #ffffff;\r\n"+
              "  	border: 1px solid #999999;\r\n"+
              "  	border-radius: 3px;\r\n"+
              "  	display: flex;\r\n"+
              "  	justify-content: center;\r\n"+
              "  	align-items: center;\r\n"+
              "  }\r\n"+
              "  .ckFieldWrapper:hover{\r\n"+
              "  	filter:brightness(120%);\r\n"+
              "  }\r\n"+
              " .radioButtonField{\r\n"+
              "  	-webkit-appearance:none;\r\n"+
              "  	-moz-appearance:none;\r\n"+
              "  	border:none;\r\n"+
              "	outline:none;\r\n"+
              "	background-none;\r\n"+
              "	background-repeat: no-repeat;\r\n"+
              "	background-position: center;\r\n"+
              "	filter:drop-shadow(0px 1px 0.5px rgba(206,206,206,1));\r\n"+
              "	display:inline-block;\r\n"+
              "  }\r\n"+
              "  .radioButtonField:checked{\r\n"+
              "	background-image: url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' viewBox='0 0 89 89'%3E%3Cdefs%3E%3Cstyle%3E.cls-1%7Bisolation:isolate;%7D.cls-2%7Bfill:%23f2f2f2;%7D.cls-3%7Bmask:url(%23mask);%7D.cls-4%7Bopacity:0.5;mix-blend-mode:darken;%7D.cls-5%7Bfill:%23666262;%7D.cls-6%7Bfill:%233a3838;%7D.cls-7%7Bfilter:url(%23luminosity-invert);%7D%3C/style%3E%3Cfilter id='luminosity-invert' filterUnits='userSpaceOnUse' color-interpolation-filters='sRGB'%3E%3CfeColorMatrix values='-1 0 0 0 1 0 -1 0 0 1 0 0 -1 0 1 0 0 0 1 0'/%3E%3C/filter%3E%3Cmask id='mask' x='-2.25' y='-2.25' width='94' height='94' maskUnits='userSpaceOnUse'%3E%3Cg class='cls-7'%3E%3Cimage width='94' height='94' transform='translate(-2.25 -2.25)' xlink:href='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAF4AAABeCAYAAACq0qNuAAAACXBIWXMAAAsSAAALEgHS3X78AAANz0lEQVR4Xu1dWVfbzBIsb/Im75skbAyJH5J/ef/llwdDjG3wAnjf1/vAqclI3tlDVOfoBDgjQ0qtmp6e7h4HgDVsvDuchwbYeBu4Dw34KOi6jnA4DK/XC5fLBafTCYfDsXXser3Ger3GarXCcrnEZDJBv99HvV7fOv4z4NMQbxgGotEofD4fVFVFNBpFKBSCz+eDx+OBy+WCw+EQF/BEOACsViusVissFgssFgtMJhMMh0N0Oh2Mx2PMZjNMJhP0ej3UarV9f8a7wYEP1HjDMBCPx6GqKlKpFKLRKFRVRTAYhKqqCAQC8Pl8cLvdJquXiee1XC6xXC4xm80wnU4xnU4xGAwwmUwwHo8xHo/R6XTw8PCAXq+HTqfzoW/Eu1u8ruuIxWIIh8PIZDKIRqOIRqOIRCIIhUKCcL/fD5/PB6/Xu0G8DEoMLX4+n2M+n2M2m2E8HmM6nYo3oNfrodfrod1uo91uo9FooNFo4Pb2dsdf+3Z4N+INw0A6nYamaUgmk4jFYojFYohEIgiHwwiFQggEAggGg/D5fPD5fFAUBW63G263G06nU1zEarXa0HfKDcnnAxiPxxgOh+j3+8Li7+/vcX9/j3Q6jWaz+a4P4M2J1zQNmqbBMAxkMhmkUinE43Fh5aqqCisPBALwer3wer3weDxC20k4rX2b1MiWLz8Akj+dTjEajTAajQT56XQarVZLEK9pGhqNBqrV6r7/0qvgTYm/uLhAoVCAYRjQNG2D9GAwiGAwaJIVj8cDt9sNj8cjyKaVH/JqgD9vAaVnuVwK+ZlMJkLz+/0+EokE2u02EokENE1DOp1Go9FAIpFAo9F40zngTYjXdR2apuHbt284OzuDrutIpVJIJBKIRqMIh8MIBoMmCyfZh1zHbZDHulwu8bX1LZjNZuIBhMNhMb/EYjEkEgnE43FhHLVaDYFAANfX19t+5Yvx6sTTyrPZLLLZ7Abp9Fr8fj8URYGiKHC5XCbCXgsOh0N8tsfjgaIoWC6X8Pl8Yj5RVdX0EHjF43Hx81qt9upu6KsRr2kadF1HPp9HPp8X8pJMJpFIJITX4vf74fV6xcR5imW/FJwr3G43vF4vfD4f/H7/xkOIRCLi7w2Hw4jFYlAUBTc3N4d+xdF4FeJzuRy+f/+OXC6HbDaLs7MzMZHSdVRV1eQevifhVjgcDuGiejwe8RA4wfPy+/1CEhVFgcPhQKlUOvTxR+HFxJ+fn+PHjx+4vLxELpeDruvIZDJCM0OhkHARPR7PhxJuhfUBKIoCr9cLv99vuhRFEZO90+l8Fd1/EfH5fB4/fvxAoVDA+fm5sHTqOUmnjn9WWB/ArouT/nq9xu/fvw997F48m/h8Po+fP3+iUCjg4uIC2WwWmUwGyWRSTKKBQODTWfk+OBwOKIoCp9MpJmU+EDlWxLEvsfxnEU95Iem5XE746dFoVOii2/2sj/9wcKVMwuUVs0z8Syz/ZGZyudwG6YZhiDAAXcXPLC3HwOl0wuv1miKissUDT+uE51r+ScRrmobv37/j8vIS5+fnyGazptiLqqrw+/2meMrfDErPtsCcNVRxqrdzEvG6riOXyyGXy224jF+NdMLhcMDj8UBVVfGzbbGh9Xp9kp9/NPEXFxfI5/NiNSpPpJSXr0Y6YSXfGg2VQ9HHrnCPIl7XdRQKBeTz+Q2X8auTTpD8YDC4EQVl5LPT6bwu8ZqmIZvNitCuHHcJBAJ//UR6LKj5qqqaNl7G4zFGoxHa7TYeHx+P0vuDxF9cXODbt29iImX0jhsXf6vL+FyQ/GAwKOL9jPN3u10MBgPMZjPc3d3t/Zy9rGmahkKhIEK79F64IvV4PPtu/7KgqxkMBjGbzRCPxzEcDqHrOrrdLh4eHl5OvGEYptAuY+nb3Kx/CW6325QRMRqNxJbiw8MDms3mXvJ3zoiGYZh2jhjaZZTxX9H1ffB4PCbyE4kEUqkUUqkU0un03nt3Ep9Op4WfLge9GGW08Udy/H6/IJ+7WDTcnfdu+yG37uQ9Ui6QuIy28QRKTjAYFDtWyWQSyWQS8Xh8531biY/FYuJGeY+Umxg2/oBeDjdNIpEIYrEY4vE4IpHIzvu2Es/tLjkbgBsCtrVvwuVyCRczFAoJvU+n08hms1vv2SCeiyQmG8lbX7a1b4fT6YSiKEJySH4ymUQmk9l+j/UH1mQjSoxt7fvhdruF5HCTPJFIIJPJQNO0jfEbxHN25gY1J1TbfdwPWj21nhMtJXtjvPyNYRhIpVIitcGarWtjP2j1suSEw2EEAoGNsSbimcxD0pnpZfvtx8HpdIp0ETlf5yDxXIXJ2s60OhuHIRNPq1dVFbFYDLqum8fK35BwSgz99q8ea39NyOR7vV4EAgEhOaZx/ELXddMKlaGBvyk94zOA2QlMkGKuv9frNY/jF3JxAIsCmMRj43iQeOZnMjvNugYSrFKXrOUvtrWfBofDIRJjmXrOnH8Zgni+HrxBLn+xcTxk4q1aL0OwKmdOWbOnbJwGckiZ4aJUDhMLZpklRcKZoGlLzemwWjzj9bJnYzJpOU3NtvbnY5fcKIoixmwlXv7exumQlUOeZOWFqG3Wb4BjZNtE/LayRRtvgw2Lt2bC2ng+5ORWK5+CeHmAPNAm/3RsqzKfz+dYLBZizAbxy+VS3GQT/zyQS6b4sZvIbDYTYwTxcgk6/12tVls/2MZ+0IDn87noHsL+CYQgnoSz7HyxWAjLt3EarNY+GAzQbrdNKdyCeDZYmE6ngnhWOtg4Huv12kT6eDwW3MoQxPf7fQyHQ6FFstXbOB5y3rys7/LECkjE1+t1dDodDAYD0ceLPV9sqz8esmTT0ieTCebzuWmcyY/na0Grn81m9iR7Ijipyr1xyKkME/FsI8UbKDk28cdhtVoJT4Z1UYPBAL1eD/1+3zTWRDwJHw6HopHafD7HcrmEjcOQi9HkHmitVmujKM1EPCsaer0ehsMhRqMRJpMJZrOZbfVHgF2g2PWv3++j2+2i2+1ujDURX6vVRF/Gfr8viLet/jBWq5WJ9MFggE6ng1arZVo4ERtBMvZlJPnU/NlsZns3e0Brp8R0u110Oh08Pj5urYXaIL7T6aDdbgvJIfn0cGxsYr1eC5/dKjHWSZXYIL5er6PRaOD+/l68JsPhUEiObfWboLVTYrrdLtrtNlqtFtrt9tZ7tu5AycRTdkajEabTqW31FqzXa+G3s+SSFd6NRmNnif1W4m9vb1GtVlGv1/H4+Gha0dpWb4bcvVsmvdlsotls7rxv554rb3x8fBRWT8mxrf4J9GTG47HJi2Fx8b5exTuLmm5vb0XbV7nkkvVQdnrfH2uXSb+/v0e9Xt9r7cCBknq2+WavMVY3MG9ezhP518CTGdg8ot1u4+HhQej6ocbQe4mvVquifyQL0tgIk5nE/2IlIN3H8Xgs/HWSTq/wEA6yxifIglmZ+H81x3I+n5uaRtzf35ss/Zg+9AeJr9frCAQCIn+e3UdJvMvl+qfK7BeLhehB3263haZXq1VUKpWD2k4cJB4Arq+vTaX1JF7Oof8X6mCXy6UpJPDw8IB6vY5KpYJSqYSbm5uje84fRTzwFECLxWLCq5GlhoR/ZfJJOj0Yavrd3R1KpRKKxeLbdOGr1WpQFEWQvo14h8PxJWum6MHIlt5sNlGr1VCpVFAul3F1dXXoY0w4mngAuLm5MSVjyoQTX617kywv1HSepENdP7bznoyTiAeAUqlkyn7dVbzwFQrXFouFkJdutytIr1arKJfLuL6+RrFYfB/igafJ1hqvkRM0V6vVX9G+fBcY+BqNRuIENWp6tVrFzc0NisUifv36hXK5fOjjtuJZxAPA79+/TdZuJX61Wpl6IfwtYIiXLqOs6be3tyiXyygWi/jvv/9OmkyteBEjtPxt2bFyhizrZj+z9DARiRv+jDRawwDX19f49evXi0gHXkg88MfySb7cZ5e7Mmwix+59n+kByCl3cuyFK9J6vY67uztUKhVUKhUUi8Vny4uMFxMPPFm+NUOW3Ufj8Tgmk4lY9cpHyX3kA1iv16bkI3kSlUnn4qhcLr/qsUSvQjzw5O0wPs19R17D4VAc8cOVL6vg3vsB0ED4RlqzAmR5qVarYnF0qp9+CC4A/zs06Fh0u13R4JgHG8paTwliJjLTA2UP6S38fzmRlH8fvRXZY6nV");
            out.print(
              "amICLZVKuLq6wtXV1bOPo9gHB97oPNezszMYhoGLC/PBLWwyxyNErUf+0P9/SZGzXN1COZF1nAsiptdxY5p+OoNezWbz6NjLqXgz4onLy0sUCgWxm8UTFhh0k8+JYiGuHPnkA5DDE9aHYfWs6FXRypnLSFmRUzDkPdJ6vY5arfYuZ7y+OfEABOk80zUejyMej4tT0eSop/WUS15sRWINVwB/jo62VrXQwuVcRk6gVtLv7u7QbDYP7hy9Ft6FeMIwDCE1PACAx13IB+ly4pXlh19bu4rIhV5yMYCcn05N7/f7Qte73S5arRYajca7H6ILvDPxMqj7bC7K/dxtzYooQfKbIBNPL4WSIh8ZTTeR0tJqtfD4+Cg2Ml7LPTwVH0Y8oWkaotGo6PrHgwFkyWHrEbqhMvG0dhLNA9L5/WTydDp9q9VCt9tFr9c72FT/PfDhxFuh6zrC4bAg2O12IxAIiPQSPgw5RsQJlFY8mUxME2u/3/8wy96FT0f8LhiGgXA4vJHTIy/5e73epyN4F/4a4r8a3m+tbsOE/wPC00ouMEARagAAAABJRU5ErkJggg=='/%3E%3C/g%3E%3C/mask%3E%3C/defs%3E%3Cg class='cls-1'%3E%3Cg id='Layer_2' data-name='Layer 2'%3E%3Cg id='Layer_1-2' data-name='Layer 1'%3E%3Ccircle class='cls-2' cx='44.5' cy='44.5' r='44.5'/%3E%3Cg class='cls-3'%3E%3Cg class='cls-4'%3E%3Ccircle class='cls-5' cx='44.5' cy='44.5' r='44.5'/%3E%3C/g%3E%3C/g%3E%3Ccircle class='cls-6' cx='44.5' cy='44.5' r='22.25'/%3E%3C/g%3E%3C/g%3E%3C/g%3E%3C/svg%3E\");\r\n"+
              "  }\r\n"+
              "  .radioButtonField:hover{\r\n"+
              "  	filter:drop-shadow(0px 1px 1px rgba(206,206,206,1)) brightness(103%);\r\n"+
              "  }\r\n"+
              "  .radioButtonField:active{\r\n"+
              "  	filter:brightness(97%);\r\n"+
              "  }\r\n"+
              "  .multicheckbox_menulist.maxWidth {\r\n"+
              "  	width:auto !important;\r\n"+
              "  }\r\n"+
              "  .multicheckbox{\r\n"+
              "  	position:relative;\r\n"+
              "  	width:100%;\r\n"+
              "  	height:100%;\r\n"+
              "  	/*box-shadow:1px 1px 2px 1px #cccccc;*/\r\n"+
              "  	border: solid 1px #888888;\r\n"+
              "  	padding:1px 4px 0px 4px;\r\n"+
              "  	display:flex;\r\n"+
              "  	flex-direction:row;\r\n"+
              "  	flex-wrap:wrap;\r\n"+
              "  	justify-content:flex-start;\r\n"+
              "  	align-content: flex-start;\r\n"+
              "  	overflow-y:hidden;\r\n"+
              "  }\r\n"+
              "  .multicheckbox:hover{\r\n"+
              "  	overflow-y:visible;\r\n"+
              "  }\r\n"+
              "  .multicheckbox_clear{\r\n"+
              "  	background:rgba(0,0,0,0)!important;\r\n"+
              "  	z-index:1;\r\n"+
              "  	position:absolute;\r\n"+
              "  	transform-origin:top right;\r\n"+
              "  	transform: scale(1.5);\r\n"+
              "  	border-radius:3px;\r\n"+
              "  	top:0px;\r\n"+
              "  	right:0px;\r\n"+
              "  	line-height:80%;\r\n"+
              "  	opacity: 0.65;\r\n"+
              "  }\r\n"+
              "  .multicheckbox_clear:hover{\r\n"+
              "  	opacity: 1;\r\n"+
              "  }\r\n"+
              "  .multicheckeditem{\r\n"+
              "  	position:relative;\r\n"+
              "  	display:inline-block;\r\n"+
              "  	padding:0px 4px;\r\n"+
              "  	box-shadow:1px 1px 2px 1px #cccccc;\r\n"+
              "  	border-radius:3px;\r\n"+
              "  	background:#eeeeee;\r\n"+
              "  	\r\n"+
              "  	\r\n"+
              "  	margin-right:5px;\r\n"+
              "  	margin-bottom:2px;\r\n"+
              "  }\r\n"+
              "  .multicheckbox_menulist{\r\n"+
              "  	pointer-events: all;\r\n"+
              "    list-style-position: inside;\r\n"+
              "    padding: 3px 4px;\r\n"+
              "    margin: 0px;\r\n"+
              "    background: white !important;\r\n"+
              "  	box-shadow:1px 1px 2px 1px #cccccc;\r\n"+
              "  	border:none !important;\r\n"+
              "  }\r\n"+
              "  .multicheckbox_menuitem{\r\n"+
              "  	box-shadow:1px 1px 1px 0px #eeeeee;\r\n"+
              "   }\r\n"+
              "\r\n"+
              "  .multicheckbox_menuitem:hover{\r\n"+
              "  	overflow-y:visible;\r\n"+
              "  }\r\n"+
              "  .multicheckbox_menuitem > td {\r\n"+
              "  	position:relative;\r\n"+
              "  	top:3px;\r\n"+
              "  	height:28px;\r\n"+
              "  }\r\n"+
              "  .multicheckbox_menuitem > td > input{\r\n"+
              "  	margin-right:2px;\r\n"+
              "  }\r\n"+
              "  .textfield_plusbutton{\r\n"+
              "    cursor:pointer;\r\n"+
              "    background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"-3 -3 21 21\" style=\"enable-background:new 0 0 15 15;\" xml:space=\"preserve\"> <g id=\"plus\"> <line style=\"fill:none;stroke:%23000000;stroke-width:3;stroke-miterlimit:10;\" x1=\"7.5\" y1=\"0.5\" x2=\"7.5\" y2=\"14.4\"/> <line style=\"fill:none;stroke:%23000000;stroke-width:3;stroke-miterlimit:10;\" x1=\"14.4\" y1=\"7.5\" x2=\"0.6\" y2=\"7.5\"/> </g> </svg>');\r\n"+
              "    border:1px solid white;\r\n"+
              "    background-color:#EEEEEE;\r\n"+
              "    background-position:center;\r\n"+
              "    background-repeat:no-repeat;\r\n"+
              "    cursor:pointer;\r\n"+
              "  }\r\n"+
              "  .textfield_plusbutton:hover{\r\n"+
              "  	box-shadow:0px 1px 5px 1px #AAAAAA inset;\r\n"+
              "  }\r\n"+
              "  .password-icon-div {\r\n"+
              "  	background-image:url('rsc/pass-show-black.svg');\r\n"+
              "    background-position:center;\r\n"+
              "    background-repeat:no-repeat;\r\n"+
              "  	cursor: pointer;\r\n"+
              "  	margin-left: 3px;\r\n"+
              "  }\r\n"+
              "  .password-field-container {\r\n"+
              "  	display: flex !important;\r\n"+
              "  }\r\n"+
              "  .selectfield_plusbutton{\r\n"+
              "    cursor:pointer;\r\n"+
              "    background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"-3 -3 21 21\" style=\"enable-background:new 0 0 15 15;\" xml:space=\"preserve\"> <g id=\"plus\"> <line style=\"fill:none;stroke:%23000000;stroke-width:3;stroke-miterlimit:10;\" x1=\"7.5\" y1=\"0.5\" x2=\"7.5\" y2=\"14.4\"/> <line style=\"fill:none;stroke:%23000000;stroke-width:3;stroke-miterlimit:10;\" x1=\"14.4\" y1=\"7.5\" x2=\"0.6\" y2=\"7.5\"/> </g> </svg>');\r\n"+
              "    border:1px solid white;\r\n"+
              "    background-color:#EEEEEE;\r\n"+
              "    background-position:center;\r\n"+
              "    background-repeat:no-repeat;\r\n"+
              "    cursor:pointer;\r\n"+
              "    top:0px;\r\n"+
              "    width:25px;\r\n"+
              "    height:25px;\r\n"+
              "    position:relative;\r\n"+
              "    display:inline-flex;\r\n"+
              "    vertical-align:middle;\r\n"+
              "  }\r\n"+
              "  .selectfield_plusbutton:hover{\r\n"+
              "  	box-shadow:0px 1px 5px 1px #AAAAAA inset;\r\n"+
              "  }\r\n"+
              "  .fixedfont{\r\n"+
              "    font-family: courier;\r\n"+
              "    font-size:11px;\r\n"+
              "  }\r\n"+
              "  .colorpicker_samplebackground{\r\n"+
              "	background: url('rsc/ami/menubar_logo3_color1.svg') !important;\r\n"+
              "	background-repeat: no-repeat !important;\r\n"+
              "	background-position: center !important;\r\n"+
              "  }\r\n"+
              "  .image_cancel_up{\r\n"+
              "	background: url('rsc/btn_cancel_up.gif') !important;\r\n"+
              "  }\r\n"+
              "  .image_cancel_dn{\r\n"+
              "	background: url('rsc/btn_cancel_dn.gif') !important;\r\n"+
              "  }\r\n"+
              "  .image_pause_up{\r\n"+
              "	background: url('rsc/btn_pause_up.gif') !important;\r\n"+
              "  }\r\n"+
              "  .image_pause_dn{\r\n"+
              "	background: url('rsc/btn_pause_dn.gif') !important;\r\n"+
              "  }\r\n"+
              "  .image_show_up{\r\n"+
              "	background-image: url('rsc/btn_show_up.gif') !important;\r\n"+
              "  }\r\n"+
              "  .image_show_dn{\r\n"+
              "	background-image: url('rsc/btn_show_dn.gif') !important;\r\n"+
              "  }\r\n"+
              "  .image_checkbox{\r\n"+
              "	background-image: url('rsc/checkbox.gif') !important;\r\n"+
              "  }\r\n"+
              "  .image_checkbox_checked{\r\n"+
              "	background-image: url('rsc/checkbox_checked.gif') !important;\r\n"+
              "  }\r\n"+
              "  .linechart_line{\r\n"+
              "  stroke: #eee;\r\n"+
              " }\r\n"+
              " .help_container{\r\n"+
              "  position: relative;\r\n"+
              " }\r\n"+
              "  table{\r\n"+
              "    border-collapse:collapse;\r\n"+
              "    border-color:transparent;\r\n"+
              "  }  \r\n"+
              " .help_title{\r\n"+
              " clear:left;\r\n"+
              "   color:#222222;\r\n"+
              "  font-weight:bold;\r\n"+
              " }\r\n"+
              " .help_text{\r\n"+
              "   color:#222222;\r\n"+
              " }\r\n"+
              " .help_entry{\r\n"+
              "   display:block;\r\n"+
              "   padding:3px 5px 10px 15px;\r\n"+
              " }\r\n"+
              " .help_group{\r\n"+
              "   font-size:14px;\r\n"+
              "   display:block;\r\n"+
              "   font-weight:bold;\r\n"+
              "   width:100%;\r\n"+
              "   padding: 8px;\r\n"+
              "   border-style:solid;\r\n"+
              "   border-width:1px 0px 0px 0px;\r\n"+
              "   border-color:black;\r\n"+
              "   background-image:url('rsc/help_back.gif');\r\n"+
              "   background-position:top;\r\n"+
              "   background-repeat:repeat-x;\r\n"+
              "   background-color:white;\r\n"+
              " }\r\n"+
              ".treemap_cell {\r\n"+
              "  border: solid 1px white;\r\n"+
              "  font: 10px sans-serif;\r\n"+
              "  line-height: 12px;\r\n"+
              "  overflow: hidden;\r\n"+
              "  position: absolute;\r\n"+
              "  color:white;\r\n"+
              "  font-size:12px;\r\n"+
              "  text-indent: 2px;\r\n"+
              "}\r\n"+
              ".treemap_tooltip{\r\n"+
              "	 background:white;\r\n"+
              "	 padding:2px;\r\n"+
              "	 border:1px solid black;\r\n"+
              "	 pointer-events:none;\r\n"+
              "     border-radius: 4px;\r\n"+
              "     white-space: pre-wrap;\r\n"+
              "}\r\n"+
              ".bold{\r\n"+
              "  font-weight:bold;\r\n"+
              "}\r\n"+
              ".darkgray{\r\n"+
              "  color:#555555;\r\n"+
              "}\r\n"+
              ".gray{\r\n"+
              "  color:#888888;\r\n"+
              "}\r\n"+
              ".cell_greybar{\r\n"+
              "  background:#EEEEEE;\r\n"+
              "}\r\n"+
              ".cell_whitebar{\r\n"+
              "  background:#FFFFFF;\r\n"+
              "}\r\n"+
              ".cell_active_default{\r\n"+
              "  background-image:url('rsc/cell_active_default.png');\r\n"+
              "}\r\n"+
              ".cell_selected_default{\r\n"+
              "  background-image:url('rsc/cell_selected_default.png');\r\n"+
              "}\r\n"+
              ".cell_whitebar{\r\n"+
              "}\r\n"+
              ".html_portlet{\r\n"+
              "  background:white;\r\n"+
              "  overflow:auto !important;\r\n"+
              "  -webkit-user-select: text;\r\n"+
              "}\r\n"+
              ".cell_percent{\r\n"+
              "  background-image:url('rsc/percent_bar.gif');\r\n"+
              "  background-size:0% 100%;\r\n"+
              "  text-align:center;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "}\r\n"+
              ".white{\r\n"+
              "  background-image:url('rsc/white.gif');\r\n"+
              "  background-size:0% 100%;\r\n"+
              "  text-align:center;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "}\r\n"+
              ".center{\r\n"+
              "  text-align:center;\r\n"+
              "}\r\n"+
              ".portlet_custom_icon{\r\n"+
              "  float:left;\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_pause{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_pause.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_backup{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_backup.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_backup_dest{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_backup_dest.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_treemap{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_treemap.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_folder{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_folder.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_folder_managed{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_folder_managed.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_file_managed{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_file_managed.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_file{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_file.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_deployment_found{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_deployment_found.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_deployment_stopped{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_deployment_stopped.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_deployment_started{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_deployment_started.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_portlet_rconnection{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_portlet_rconnection.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;");
            out.print(
              "\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_portlet_connection{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_portlet_connection.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_clock{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_clock.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_field{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_field.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_map{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_map.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_diff_left{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_diff_left.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_diff_right{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_diff_right.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_list{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_list.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_stack{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_stack.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_exception{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_exception.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_error{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_error.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "  color:#AA0000;\r\n"+
              "}\r\n"+
              ".portlet_icon_warning{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_warning.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "  color:#888102;\r\n"+
              "}\r\n"+
              ".portlet_icon_debug{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_debug.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "  color:#742d00;\r\n"+
              "}\r\n"+
              ".portlet_field_icon_error{\r\n"+
              "  position: relative;\r\n"+
              "  float: right;\r\n"+
              "  background-image:url('rsc/portlet_icon_error.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "  color:#888102;\r\n"+
              "}\r\n"+
              ".portlet_icon_eye{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_eye.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_deployment{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_deployment.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_okay{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_okay.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "  color: #355234;\r\n"+
              "}\r\n"+
              ".portlet_icon_wait{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/wait.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "  color: #355234;\r\n"+
              "}\r\n"+
              ".portlet_icon_okay_bold{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_okay_bold.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "  font-weight:bold;\r\n"+
              "  color: #355234;\r\n"+
              "}\r\n"+
              ".portlet_icon_okay_lock{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_okay_lock.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "  font-weight:bold;\r\n"+
              "  color: #355234;\r\n"+
              "}\r\n"+
              ".portlet_icon_info{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_info.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_db_database{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_db_database.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_db_database_info{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_db_database_info.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_db_table{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_db_table.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_db_column{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_db_column.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_db_object{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_db_object.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_db_privilege{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_db_privilege.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_connection{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_connection.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_socket{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_socket.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_user{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_user.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_window{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_window.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_tabs{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_tabs.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_chart{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_chart.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".legend {\r\n"+
              "	z-index: 9;\r\n"+
              "	position: absolute;\r\n"+
              "	cursor: move;\r\n"+
              "	min-width: 50px;\r\n"+
              "	min-height: 50px;\r\n"+
              "	padding: 2px;\r\n"+
              "	textAlign: left;\r\n"+
              "	overflow: hidden;\r\n"+
              "}\r\n"+
              ".legend_series {\r\n"+
              "	width: 100%;\r\n"+
              "	height: inherit;\r\n"+
              "	overflow-y: scroll;\r\n"+
              "  	scrollbar-width: none; /* Firefox */\r\n"+
              "  	-ms-overflow-style: none;  /* IE 10+ */\r\n"+
              "}\r\n"+
              ".legend_series::-webkit-scrollbar {\r\n"+
              "	width: 0px;\r\n"+
              "}\r\n"+
              ".legend_resizer {\r\n"+
              "	z-index: 100;\r\n"+
              "	cursor: nw-resize;\r\n"+
              "	background-repeat:no-repeat;\r\n"+
              "	display: none;\r\n"+
              "}\r\n"+
              ".legend_title {\r\n"+
              "	position: relative;\r\n"+
              "	width: 100%;\r\n"+
              "}\r\n"+
              ".series {\r\n"+
              "	position: relative;\r\n"+
              "	width: 100%;\r\n"+
              "	margin: 2px 0 0 0;\r\n"+
              "	padding: 0px;\r\n"+
              "	list-style-type: none;\r\n"+
              "	display: flex;\r\n"+
              "    justify-content: left;\r\n"+
              "    align-items: flex-start;\r\n"+
              "}\r\n"+
              ".legend_series ul {\r\n"+
              "	margin: 0px;\r\n"+
              "	padding: 0px;\r\n"+
              "}\r\n"+
              ".series_name {\r\n"+
              "	margin-left: 5px;\r\n"+
              "}\r\n"+
              ".series_checkbox {\r\n"+
              "	margin-right: 5px;\r\n"+
              "	opacity: 0;\r\n"+
              "	position: absolute;\r\n"+
              "}\r\n"+
              ".series_shape {\r\n"+
              "	margin-left: 15px;\r\n"+
              "}\r\n"+
              ".series_checkbox_wrapper {\r\n"+
              "    background: red;\r\n"+
              "    width: 12px;\r\n"+
              "    height: 12px;\r\n"+
              "    margin-right: 5px;\r\n"+
              "    border-radius: 3px;\r\n"+
              "    z-index: 2;\r\n"+
              "}\r\n"+
              ".checkMark {\r\n"+
              "	transform: rotate(28deg);\r\n"+
              "	position: absolute;\r\n"+
              "	height: 8px;\r\n"+
              "	width: 5px;\r\n"+
              "	border-bottom: 2px solid #ffffff;\r\n"+
              "	border-right: 2px solid #ffffff;\r\n"+
              "	z-index: 100;\r\n"+
              "	margin-left: 3px;\r\n"+
              "	cursor: pointer;\r\n"+
              "}\r\n"+
              ".portlet_icon_desktop{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_desktop.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_hdivider{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_hdivider.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_vdivider{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_vdivider.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_tab{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_tab.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_table{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_table.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_graph{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_graph.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_tree{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_tree.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_group{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_group.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_host{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_host.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_region{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_region.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_process{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_process.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;");
            out.print(
              "\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_account{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_account.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_environment{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_environment.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_blank{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_blank.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".portlet_icon_form{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_form.gif');\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "}\r\n"+
              ".overlay_div{\r\n"+
              "  pointer-events:none;\r\n"+
              "  width:100%;\r\n"+
              "  height:100%;\r\n"+
              "  opacity:.5;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "}\r\n"+
              "    .wait_pane{\r\n"+
              "        top:0px;\r\n"+
              "        bottom:0px;\r\n"+
              "        left:0px;\r\n"+
              "        right:0px;\r\n"+
              "        z-index:9999;\r\n"+
              "        background-color:black;\r\n"+
              "        opacity:.5;\r\n"+
              "    }\r\n"+
              ".wait_plot{\r\n"+
              "   top:0px;\r\n"+
              "   bottom:0px;\r\n"+
              "   left:0px;\r\n"+
              "   right:0px;\r\n"+
              "   z-index:9999;\r\n"+
              "   \r\n"+
              "	background-image:url('rsc/wait2.gif');\r\n"+
              "	background-repeat:no-repeat;\r\n"+
              "	background-position:top left;\r\n"+
              "	background-size:55px 40px;\r\n"+
              "	pointer-events:none;\r\n"+
              "}\r\n"+
              "    .dialog_pane{\r\n"+
              "        top:0px;\r\n"+
              "/*         bottom:0px; */\r\n"+
              "        left:0px;\r\n"+
              "/*         right:0px; */\r\n"+
              "		width:100%;\r\n"+
              "		height:100%;\r\n"+
              "        z-index:9999;\r\n"+
              "        background-color:black;\r\n"+
              "        cursor:not-allowed;\r\n"+
              "        opacity:.5;\r\n"+
              "        position:fixed;\r\n"+
              "    }\r\n"+
              "    .div_fill{\r\n"+
              "        top:0px;\r\n"+
              "        bottom:0px;\r\n"+
              "        left:0px;\r\n"+
              "        right:0px;\r\n"+
              "    }\r\n"+
              "    .disable_glass_clear{\r\n"+
              "        top:1px;\r\n"+
              "        bottom:1px;\r\n"+
              "        left:1px;\r\n"+
              "        right:1px;\r\n"+
              "        position:fixed;\r\n"+
              "        z-index:9998;\r\n"+
              "    }\r\n"+
              "    .disable_glass{\r\n"+
              "        top:1px;\r\n"+
              "        bottom:1px;\r\n"+
              "        left:1px;\r\n"+
              "        right:1px;\r\n"+
              "        background-color:black;\r\n"+
              "        font-weight:bold;\r\n"+
              "        z-index:9998;\r\n"+
              "        opacity:.3;\r\n"+
              "        position:fixed;\r\n"+
              "    }\r\n"+
              "    .wait{\r\n"+
              "        left:1px;\r\n"+
              "        right:1px;\r\n"+
              "        bottom:50%;\r\n"+
              "        padding:200px 10px 0px 0px;\r\n"+
              "        color:white;\r\n"+
              "        font-weight:bold;\r\n"+
              "        z-index:9999;\r\n"+
              "        text-align:center;\r\n"+
              "        background-image:url('rsc/wait2.gif');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "    }\r\n"+
              "    .scrollbar_container{\r\n"+
              "    	z-index:9997;\r\n"+
              "	}\r\n"+
              "    .scrollbar_tick {\r\n"+
              "	    cursor:pointer;\r\n"+
              "	}\r\n"+
              "    .scrollbar_tick:hover {\r\n"+
              "        border:1px solid #333333;\r\n"+
              "	}\r\n"+
              "	.scrollpane_container {\r\n"+
              "	}\r\n"+
              "	.scrollpane {\r\n"+
              "	    background:white;\r\n"+
              "	    overflow: hidden;\r\n"+
              "	}\r\n"+
              "	.scrollpane_inner {\r\n"+
              "	}\r\n"+
              "	.scrollbar_track_v {\r\n"+
              "	    background-color:#d6d4d2;\r\n"+
              "	    border: 1px solid #a19d9a;\r\n"+
              "	}\r\n"+
              "	.scrollbar_handle_v {\r\n"+
              "	    background-color:#b0cbec;\r\n"+
              "	    border: 1px solid #868482;\r\n"+
              "	    overflow-y: auto;\r\n"+
              "	    background-image:url('rsc/scroll_v_back.gif');\r\n"+
              "	    cursor:pointer;\r\n"+
              "	}\r\n"+
              "	.scrollbar_grip_v {\r\n"+
              "	    width:100%;\r\n"+
              "	    height:100%;\r\n"+
              "	    /*background-image:url('rsc/scroll_v.gif');*/\r\n"+
              "	    background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> <g> <line style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" x1=\"12\" y1=\"24.1\" x2=\"88\" y2=\"24.1\"/> <line style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" x1=\"12\" y1=\"50\" x2=\"88\" y2=\"50\"/> <line style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" x1=\"12\" y1=\"76.3\" x2=\"88\" y2=\"76.3\"/> </g> </g> <g id=\"horizontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> </svg>');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "	    cursor:pointer;\r\n"+
              "	}\r\n"+
              "	.scrollbar_grip_h {\r\n"+
              "	    width:100%;\r\n"+
              "	    height:100%;\r\n"+
              "	    /*background-image:url('rsc/scroll_h.gif');*/\r\n"+
              "	    background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"horizontal_x5F_scrollbar\"> <g> <line style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" x1=\"76.1\" y1=\"13.5\" x2=\"76.1\" y2=\"89.5\"/> <line style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" x1=\"50.2\" y1=\"13.5\" x2=\"50.2\" y2=\"89.5\"/> <line style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" x1=\"23.9\" y1=\"13.5\" x2=\"23.9\" y2=\"89.5\"/> </g> </g> <g id=\"save\"> </g> </svg>');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "	    cursor:pointer;\r\n"+
              "	}\r\n"+
              "	.scrollbar_up {\r\n"+
              "	    /*background-image:url('rsc/scroll_up.gif');*/\r\n"+
              "	    background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> <polyline style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" points=\"11.9,69 50,31 88.1,69 	\"/> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"horizontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> </svg>');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "	    background-color:#fafafa;\r\n"+
              "	    border:1px solid #868482;\r\n"+
              "	}\r\n"+
              "	.scrollbar_down {\r\n"+
              "	    /*background-image:url('rsc/scroll_down.gif');*/\r\n"+
              "	    background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> <polyline style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" points=\"88.1,31 50,69 11.9,31 	\"/> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"horizontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> </svg>');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "	    background-color:#fafafa;\r\n"+
              "	    border:1px solid #868482;\r\n"+
              "	}\r\n"+
              "	.scrollbar_track_h {\r\n"+
              "	    background-color:#d6d4d2;\r\n"+
              "	    border: 1px solid #a19d9a;\r\n"+
              "	}\r\n"+
              "	.scrollbar_handle_h {\r\n"+
              "	    overflow-x: auto;\r\n"+
              "	    background-color:#b0cbec;\r\n"+
              "	    border:1px solid #868482;\r\n"+
              "	    background-image:url('rsc/scroll_h_back.gif');\r\n"+
              "	}\r\n"+
              "	.scrollbar_left {\r\n"+
              "	    background-color:#fafafa;\r\n"+
              "	    border:1px solid #868482;\r\n"+
              "	    /*background-image:url('rsc/scroll_left.gif');*/\r\n"+
              "	    background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> <polyline style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" points=\"69,88.1 31,50 69,11.9 	\"/> </g> <g id=\"scrollright\"> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"horizontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> </svg>');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "	}\r\n"+
              "	.scrollbar_right {\r\n"+
              "	    background-color:#fafafa;\r\n"+
              "	    border:1px solid #868482;\r\n"+
              "	    /*background-image:url('rsc/scroll_right.gif');*/\r\n"+
              "	    background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"Layer_1\"> </g> <g id=\"plus\"> </g> <g id=\"check\"> </g> <g id=\"popout\"> </g> <g id=\"popin\"> </g> <g id=\"minimize\"> </g> <g id=\"maximize\"> </g> <g id=\"xclose\"> </g> <g id=\"dropdown\"> </g> <g id=\"hidden\"> </g> <g id=\"visible\"> </g> <g id=\"search\"> </g> <g id=\"scrolldown\"> </g> <g id=\"scrollup\"> </g> <g id=\"scrollleft\"> </g> <g id=\"scrollright\"> <polyline style=\"fill:none;stroke:%23000000;stroke-width:14;stroke-miterlimit:10;\" points=\"32.3,11.9 70.3,50 32.3,88.1 	\"/> </g> <g id=\"vertical_x5F_scrollbar\"> </g> <g id=\"hori");
            out.print(
              "zontal_x5F_scrollbar\"> </g> <g id=\"save\"> </g> </svg>');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "	}\r\n"+
              "	.tiny_square {\r\n"+
              "		display: none;\r\n"+
              "	    background-color:#fafafa;\r\n"+
              "	    width:15px;\r\n"+
              "	    height:15px;\r\n"+
              "	}\r\n"+
              "	::-moz-selection{\r\n"+
              "	    background-color:#008800;\r\n"+
              "	    color:#EEEEEE;\r\n"+
              "	}\r\n"+
              "	::selection {\r\n"+
              "	    background-color:#008800;\r\n"+
              "	    color:#EEEEEE;\r\n"+
              "    }\r\n"+
              "	.table {\r\n"+
              "		position: absolute;\r\n"+
              "		top:0px;\r\n"+
              "		left:0px;\r\n"+
              "		overflow: hidden;\r\n"+
              "		white-space: nowrap;\r\n"+
              "		background:#FCFCFC;\r\n"+
              "        outline:none;\r\n"+
              "		}\r\n"+
              "	.table_corner {\r\n"+
              "		background:#EEEEEE;\r\n"+
              "		background-image:url('rsc/help_icon.gif');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "	    cursor: pointer;\r\n"+
              "		}\r\n"+
              "	.header_filtered {\r\n"+
              "		background-image:no-image;\r\n"+
              "		white-space: nowrap;\r\n"+
              "		border-width:1px 1px 0px 0px;\r\n"+
              "		padding:0px 0px 0px 0px;\r\n"+
              "		cursor:pointer;\r\n"+
              "	}\r\n"+
              "	.header > .header{\r\n"+
              "		top:0px;\r\n"+
              "		height:100%;\r\n"+
              " 		border-width:0px 1px 0px 0px; \r\n"+
              "	    border-style: solid; \r\n"+
              "    	border-color: #AAAAAA; \r\n"+
              "    	padding-left:2px;\r\n"+
              "    	padding-right:2px;\r\n"+
              "    	padding-top:2px;\r\n"+
              "	}\r\n"+
              "	.header {\r\n"+
              "		overflow: hidden;\r\n"+
              "		white-space: nowrap;\r\n"+
              "		border-width:1px 1px 0px 0px;\r\n"+
              "		/*background-image:url('rsc/column_header_back.gif');*/\r\n"+
              "		/*background-position:bottom;*?\r\n"+
              "		/*background-repeat:repeat-x;*/\r\n"+
              "		/*padding-top:0px !important; */\r\n"+
              "		cursor:pointer;\r\n"+
              "		box-shadow:inset 0px -8px 5px -5px rgba(255,255,255,0.5);\r\n"+
              "		/*background-blend-mode: screen;*/\r\n"+
              "		/*background-blend-mode: luminosity;*/\r\n"+
              "		/*background-blend-mode: lighten;*/\r\n"+
              "		}\r\n"+
              "	.asc{\r\n"+
              "		background-image:url('rsc/asc.gif') !important;\r\n"+
              "	    background-repeat:no-repeat !important;\r\n"+
              "	    background-position:left !important;\r\n"+
              "		padding-left:12px !important;\r\n"+
              "	    font-weight:bold;\r\n"+
              "	}\r\n"+
              "	.des{\r\n"+
              "		background-image:url('rsc/des.gif') !important;\r\n"+
              "	    background-repeat:no-repeat !important;\r\n"+
              "	    background-position:left !important;\r\n"+
              "		padding-left:12px !important;\r\n"+
              "	    font-weight:bold;\r\n"+
              "	}\r\n"+
              "	.autoasc{\r\n"+
              "		background-image:url('rsc/autoasc.gif');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:left;\r\n"+
              "		padding-left:12px !important;\r\n"+
              "	    font-weight:bold;\r\n"+
              "	}\r\n"+
              "	.autodes{\r\n"+
              "		background-image:url('rsc/autodes.gif');\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:left;\r\n"+
              "		padding-left:12px !important;\r\n"+
              "	    font-weight:bold;\r\n"+
              "	}\r\n"+
              "	.header_invisible_layer{\r\n"+
              "		width:calc(100% - 5px);\r\n"+
              "		height:100%;\r\n"+
              "		opacity:0;\r\n"+
              "		top:0;\r\n"+
              "		left:0;\r\n"+
              "		margin:0px 2.5px;\r\n"+
              "	}\r\n"+
              "	.header_invisible_layer:hover{\r\n"+
              "		opacity:1; \r\n"+
              "	}\r\n"+
              "	.header_buttons_up {\r\n"+
              "	    top:0px;\r\n"+
              "	    right:0px;\r\n"+
              "	    width:11px;\r\n"+
              "	    height:11px;\r\n"+
              "	    background-image:url('rsc/header_buttons_up.gif');\r\n"+
              "	    background-color:#444444;\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "	    border:1px solid #666666;\r\n"+
              "	    border-width:0px 0px 0px 1px;\r\n"+
              "	}\r\n"+
              "	.header_buttons_up:hover {\r\n"+
              "	    background-image:url('rsc/header_buttons_up_hover.gif');\r\n"+
              "	}\r\n"+
              "	.header_buttons_dn {\r\n"+
              "	    top:11px;\r\n"+
              "	    right:0px;\r\n"+
              "	    width:11px;\r\n"+
              "	    height:11px;\r\n"+
              "	    background-image: url('rsc/header_buttons_dn.gif');\r\n"+
              "	    background-color:#444444;\r\n"+
              "	    background-repeat:no-repeat;\r\n"+
              "	    background-position:center;\r\n"+
              "	    border:1px solid #666666;\r\n"+
              "	    border-width:0px 0px 0px 1px;\r\n"+
              "	}\r\n"+
              "	.header_buttons_dn:hover {\r\n"+
              "	    background-image:url('rsc/header_buttons_dn_hover.gif');\r\n"+
              "	}\r\n"+
              "	.header_grab {\r\n"+
              "		position: absolute;\r\n"+
              "		top:0px;\r\n"+
              "		width:7px;\r\n"+
              "		height:100%;\r\n"+
              "		overflow: hidden;\r\n"+
              "		white-space: nowrap;\r\n"+
              "		border-width:0px 1px;\r\n"+
              "		background:#949494;\r\n"+
              "/* 		background:#535353; */\r\n"+
              "		border-style:solid;\r\n"+
              "		border-color: black #333333 black #888888;\r\n"+
              "        cursor:e-resize;\r\n"+
              "		}\r\n"+
              ".tableEditor{\r\n"+
              "  /*background:green!important;*/\r\n"+
              "  width:100%;\r\n"+
              "  height:100%;\r\n"+
              "  left:0px;\r\n"+
              "  top:0px;\r\n"+
              "\r\n"+
              "  /*opacity:.3;*/\r\n"+
              "}\r\n"+
              ".tableEditorMoving{\r\n"+
              "  /*background:green!important;*/\r\n"+
              "  height:100%;\r\n"+
              "  z-index:1;\r\n"+
              "  overflow:hidden;\r\n"+
              "\r\n"+
              "  /*opacity:.3;*/\r\n"+
              "}\r\n"+
              ".tableEditorPinned{\r\n"+
              "  /*background:green!important;*/\r\n"+
              "  height:100%;\r\n"+
              "  z-index:2;\r\n"+
              "  overflow:hidden;\r\n"+
              "  margin-right:2px;\r\n"+
              "\r\n"+
              "  /*opacity:.3;*/\r\n"+
              "}\r\n"+
              ".editCell{\r\n"+
              "  background:white!important;\r\n"+
              "  position:absolute; \r\n"+
              "  padding-left:1px;\r\n"+
              "  padding-right:1px;\r\n"+
              "  border:#d0d0d0 solid 1px;    \r\n"+
              "}	\r\n"+
              ".editCell:focus{\r\n"+
              "  -webkit-box-shadow: 0px 1px 2px 0px #000000; \r\n"+
              "  box-shadow: 0px 1px 2px 0px #000000;\r\n"+
              "  color:black;\r\n"+
              "  \r\n"+
              "  z-index:2;\r\n"+
              "}\r\n"+
              "\r\n"+
              "\r\n"+
              ".editComboBox{\r\n"+
              "  position:absolute;\r\n"+
              "  padding:0px !important;\r\n"+
              "  border:#d0d0d0 solid 1px;    \r\n"+
              "}\r\n"+
              "\r\n"+
              "\r\n"+
              "\r\n"+
              ".editComboBox:focus{\r\n"+
              "  -webkit-box-shadow: 0px 1px 2px 0px #000000; \r\n"+
              "  box-shadow: 0px 1px 2px 0px #000000;\r\n"+
              "  color:black;\r\n"+
              "  \r\n"+
              "  z-index:2;\r\n"+
              "}\r\n"+
              "	.table_cell > div{\r\n"+
              "		box-sizing:border-box;\r\n"+
              "		display:inline-block;\r\n"+
              "		\r\n"+
              "		/*vertical-align:bottom;\r\n"+
              "		margin-top: 0em;*/\r\n"+
              "	}\r\n"+
              "	.table_cell p{\r\n"+
              "		vertical-align:bottom;\r\n"+
              "		margin-top: 0em;\r\n"+
              "	}\r\n"+
              "	.table_cell > *{\r\n"+
              "		position:absolute;\r\n"+
              "	}\r\n"+
              "	.table_cell {\r\n"+
              "		vertical-align:middle;\r\n"+
              "		position: absolute;\r\n"+
              "		width: 100px;\r\n"+
              "		height:20px;\r\n"+
              "		overflow: hidden;\r\n"+
              "		padding:0px;\r\n"+
              "		border-style:solid;\r\n"+
              "		border-color:#AAAAAA;\r\n"+
              "		border-width:0px 1px 0px 0px;\r\n"+
              "		white-space: nowrap;\r\n"+
              "		outline:none;\r\n"+
              "		flex-direction:column;\r\n"+
              "		\r\n"+
              "		}\r\n"+
              "     .cell_number > div{\r\n"+
              "		align-items:flex-end;\r\n"+
              "     }\r\n"+
              "     .cell_number{\r\n"+
              "        text-align:right;\r\n"+
              "       padding: 0px 2px 0px 0px;\r\n"+
              "     }\r\n"+
              "     .cell_text{\r\n"+
              "		vertical-align:middle;\r\n"+
              "       padding: 0px 0px 0px 2px;\r\n"+
              "     }\r\n"+
              "     .cell_button{\r\n"+
              "        background-repeat:no-repeat;\r\n"+
              "        padding:1px;\r\n"+
              "        cursor:pointer;\r\n"+
              "     }\r\n"+
              "     .table_cell_button{\r\n"+
              "        cursor:pointer;\r\n"+
              "        text-align:center;\r\n"+
              "        background:#DDDDDD;\r\n"+
              "        color:black;\r\n"+
              "        border-radius: 4px;\r\n"+
              "        border:1px outset grey !important;\r\n"+
              "     }\r\n"+
              "     .table_cell_button:hover{\r\n"+
              "        background:#EEEEEE;\r\n"+
              "        border:1px outset black !important;\r\n"+
              "     }\r\n"+
              "     .field_disabled, .field_disabled > *{\r\n"+
              "       border-color:#C0C0C0;\r\n"+
              "       background:#F0F0F0;\r\n"+
              "       color:#555555;\r\n"+
              "       cursor:default;\r\n"+
              "     }\r\n"+
              "body{\r\n"+
              "  margin:0px;\r\n"+
              "  font-family:arial;\r\n"+
              "  font-size:10pt;\r\n"+
              "  font-variant-numeric: tabular-nums;\r\n"+
              "  overflow:hidden;\r\n"+
              "  color:#444444;\r\n"+
              "}\r\n"+
              ".normal{\r\n"+
              "  color:#444444;\r\n"+
              "}\r\n"+
              "/*\r\n"+
              ".table_icon.cell_selected p{\r\n"+
              "  background:#99ff99;\r\n"+
              "  color:#000000;\r\n"+
              "}\r\n"+
              ".table_icon.cell_active p{\r\n"+
              "  background:#44ff44;\r\n"+
              "  color:#000000;\r\n"+
              "}\r\n"+
              ".table_icon p{\r\n"+
              "  border:1px solid black;\r\n"+
              "  margin:4px;\r\n"+
              "  padding:4px;\r\n"+
              "  font-size:14px;\r\n"+
              "  background:#eeeeee;\r\n"+
              "  color:#444444;\r\n"+
              "  overflow:hidden;\r\n"+
              "  border-radius: 10px;\r\n"+
              "  -moz-border-radius: 10px;\r\n"+
              "  cursor: pointer;\r\n"+
              "  text-align:center;\r\n"+
              "}\r\n"+
              "*/\r\n"+
              ".bold{\r\n"+
              "  font-weight:bold;\r\n"+
              "}\r\n"+
              ".col_location{\r\n"+
              "  background-image:url('rsc/yellow.gif');\r\n"+
              "}\r\n"+
              ".strike{\r\n"+
              "  text-decoration:line-through;\r\n"+
              "}\r\n"+
              ".blue{\r\n"+
              "  color:#0000DD;\r\n"+
              "}\r\n"+
              ".pink{\r\n"+
              "  color:#DD00DD;\r\n"+
              "}\r\n"+
              ".yellow{\r\n"+
              "  color:#AA8800;\r\n"+
              "}\r\n"+
              ".black{\r\n"+
              "  background:#000000;\r\n"+
              "}\r\n"+
              ".green{\r\n"+
              "  color:#006600;\r\n"+
              "}\r\n"+
              ".purple{\r\n"+
              "  color:#880088;\r\n"+
              "}\r\n"+
              ".italic{\r\n"+
              "  font-style:italic;\r\n"+
              "}\r\n"+
              ".red{\r\n"+
              "  color:#DD0000;\r\n"+
              "}\r\n"+
              ".clipboarder{\r\n"+
              "   border:1px dashed black;\r\n"+
              "}\r\n"+
              ".clickable{\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".pointer{\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".clickable:hover{\r\n"+
              "  text-decoration:underline\r\n"+
              "}\r\n"+
              "div{\r\n"+
              "  position: absolute;\r\n"+
              "  -webkit-user-select:none;\r\n"+
              "  -khtml-user-select:none;\r\n"+
              "  -moz-user-select:none;\r\n"+
              "  -o-user-select:none;\r\n"+
              "  user-select:none;\r\n"+
              "  -moz-box-sizing: border-box;\r\n"+
              "  -ms-user-select: none;\r\n"+
              "  box-sizing: border-box;\r\n"+
              "}\r\n"+
              "pre{\r\n"+
              "  -moz-box-sizing: border-box;\r\n"+
              "  box-sizing: border-box;\r\n"+
              "}\r\n"+
              "ul, ol, li\r\n"+
              "{\r\n"+
              "	list-style-type: none;\r\n"+
              "}\r\n"+
              "li\r\n"+
              "{\r\n"+
              "	margin: .75em;\r\n"+
              "	padding-left: 20px;\r\n"+
              "}\r\n"+
              "li strong\r\n"+
              "{\r\n"+
              "	font-weight: normal;\r\n"+
              "	text-transform: uppercase;\r\n"+
              "	color: #e36f1e;\r\n"+
              "}\r\n"+
              "label\r\n"+
              "{\r\n"+
              "	position: relative;\r\n"+
              "	float: left;\r\n"+
              "	width: 10em;\r\n"+
              "	margin-right: 1em;\r\n"+
              "}\r\n"+
              "label em\r\n"+
              "{\r\n"+
              "	position: absolute;\r\n"+
              "	left: 7.5em;\r\n"+
              "	top: 0;\r\n"+
              "}\r\n"+
              "textarea\r\n"+
              "{\r\n"+
              "  font-family:courier;\r\n"+
              "  font-size:10pt;\r\n"+
              "}\r\n"+
              "input[type=\"submit\"], button\r\n"+
              "{\r\n"+
              "	min-width: 25px;\r\n"+
              "}\r\n"+
              "input, select\r\n"+
              "{\r\n"+
              "  box-sizing: border-box;\r\n"+
              "  border:1px solid #888888;\r\n"+
              "  color:#4444AA;\r\n"+
              "  font-size:12px;\r\n"+
              "  margin: 0;\r\n"+
              "  padding: 0;\r\n"+
              "}\r\n"+
              ".portlet_divider_v_dragger:hover{\r\n"+
              "  background:#AAAAAA;\r\n"+
              "  background-image:url('rsc/vdiv.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  border-width:0px 1px 0px 1px;\r\n"+
              "  border-style:solid;\r\n"+
              "  border-color:#999999 #444444 #444444 #999999;\r\n"+
              "  cursor:e-resize;\r\n"+
              "}\r\n"+
              ".portlet_divider_h_dragger:hover{\r\n"+
              "  background:#AAAAAA;\r\n"+
              "  background-image:url('rsc/hdiv.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  border-width:1px 0px 1px 0px;\r\n"+
              "  border-style:solid;\r\n"+
              "  border-color:#999999 #444444 #444444 #999999;\r\n"+
              "  cursor:n-resize;\r\n"+
              "}\r\n"+
              "/*\r\n"+
              ".portlet_divider_v:hover{\r\n"+
              "  background:#AAAAAA;\r\n"+
              "  background-image:url('rsc/vdiv.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  border-width:0px 1px 0px 1px;\r\n"+
              "  border-style:solid;\r\n"+
              "  border-color:#999999 #444444 #444444 #999999;\r\n"+
              "  cursor:e-resize;\r\n"+
              "}\r\n"+
              "*/\r\n"+
              ".portlet_divider_v, .portlet_divider_h, .portlet_divider_v_locked, .portlet_divider_h_locked, .portlet_divider_vswap, .portlet_divider_hswap\r\n"+
              "{\r\n"+
              "  background:#dddddd;\r\n"+
              "}\r\n"+
              "/*\r\n"+
              ".portlet_divider_h:hover{\r\n"+
              "  background:#AAAAAA;\r\n"+
              "  background-image:url('rsc/hdiv.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  border-width:1px 0px 1px 0px;\r\n"+
              "  border-style:solid;\r\n"+
              "  border-color:#999999 #444444 #444444 #999999;\r\n"+
              "  cursor:n-resize;\r\n"+
              "}\r\n"+
              "*/\r\n"+
              ".portlet_divider_vswap:hover{\r\n"+
              "  background-image:url('rsc/vswap.gif');\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portlet_divider_hswap:hover{\r\n"+
              "  background-image:url('rsc/hswap.gif');\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portlet_html{\r\n"+
              "  bottom:0px;\r\n"+
              "  top:0px;\r\n"+
              "  left:0px;\r\n"+
              "  right:0px;\r\n"+
              "}\r\n"+
              ".portlet_blank{\r\n"+
              "  background-image:url('rsc/blank.gif');\r\n"+
              "  cursor:pointer;\r\n"+
              "  position:absolute;\r\n"+
              "  bottom:0px;\r\n"+
              "  top:0px;\r\n"+
              "  left:0px;\r\n"+
              "  right:0px;\r\n"+
              "}\r\n"+
              ".portlet_blank_add{\r\n"+
              "  background-image:url('rsc/blank_add.gif');\r\n"+
              "  cursor:pointer;\r\n"+
              "  position:absolute;\r\n"+
              "  width:100px;\r\n"+
              "  height:100px;\r\n"+
              "}\r\n"+
              ".portal_doclet_right{\r\n"+
              "  background-image:url('rsc/docklet_right.gif');\r\n"+
              "");
            out.print(
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left center;\r\n"+
              "  color:white;\r\n"+
              "  cursor:pointer;\r\n"+
              "  text-align:center;\r\n"+
              "  height:26px;\r\n"+
              "  width:5px;\r\n"+
              "  padding:0px 1px;\r\n"+
              "}\r\n"+
              ".portal_doclet{\r\n"+
              "  background-image:url('rsc/docklet_center.gif');\r\n"+
              "  background-repeat:repeat-x;\r\n"+
              "  background-position:center;\r\n"+
              "  color:#BBBBBB;\r\n"+
              "  cursor:pointer;\r\n"+
              "  font-weight:normal;\r\n"+
              "  text-align:center;\r\n"+
              "  height:26px;\r\n"+
              "}\r\n"+
              ".portal_doclet_left{\r\n"+
              "  background-image:url('rsc/docklet_left.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:right center;\r\n"+
              "  cursor:pointer;\r\n"+
              "  height:26px;\r\n"+
              "  width:5px;\r\n"+
              "  padding:0px 1px;\r\n"+
              "}\r\n"+
              ".portal_doclet_active{\r\n"+
              "  background-image:url('rsc/docklet_center.gif');\r\n"+
              "  background-repeat:repeat-x;\r\n"+
              "  background-position:center;\r\n"+
              "  color:#FFFFFF;\r\n"+
              "  cursor:pointer;\r\n"+
              "  font-weight:normal;\r\n"+
              "  text-align:center;\r\n"+
              "  height:26px;\r\n"+
              "}\r\n"+
              ".portal_desktop{\r\n"+
              "  background-color:#acbdf5;\r\n"+
              "  background-image:url('rsc/bluebg.jpg');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  overflow:hidden;\r\n"+
              "}\r\n"+
              ".portal_desktop_dashboard{\r\n"+
              "  background:url('rsc/dashboard.gif');\r\n"+
              "  position:absolute;\r\n"+
              "  bottom:0px;\r\n"+
              "  left:0px;\r\n"+
              "  right:0px;\r\n"+
              "  padding:0px\r\n"+
              "}\r\n"+
              ".portal_desktop_add{\r\n"+
              "  background:url('rsc/dashboard_add.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  position:relative;\r\n"+
              "  bottom:0px;\r\n"+
              "  left:4px;\r\n"+
              "  width:30px;\r\n"+
              "  height:30px;\r\n"+
              "  cursor: pointer;\r\n"+
              "}\r\n"+
              ".portal_tab_inner{\r\n"+
              "  cursor:pointer;\r\n"+
              "  padding:0px 6px;\r\n"+
              "  white-space:nowrap;\r\n"+
              "  display:block;\r\n"+
              "}\r\n"+
              ".portal_tab{\r\n"+
              "  cursor:pointer;\r\n"+
              "  vertical-align:bottom;\r\n"+
              "  display:flex;\r\n"+
              "  position:absolute;\r\n"+
              "}\r\n"+
              ".portal_tab:active {\r\n"+
              "	transform: scale(0.97);\r\n"+
              "	filter: saturate(2);\r\n"+
              "}\r\n"+
              ".portal_tabsBar{\r\n"+
              "	display:inline-block;\r\n"+
              "	position:relative;\r\n"+
              "	z-index:2;\r\n"+
              "}\r\n"+
              ".tab_blink {\r\n"+
              "	transition: all 200ms ease-in-out;\r\n"+
              "}\r\n"+
              ".portal_tab_add_button{\r\n"+
              "  background-image:url('rsc/tab_add.gif');\r\n"+
              "  width:22px;\r\n"+
              "  top:1px;\r\n"+
              "  bottom:0px;\r\n"+
              "  padding:0px;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:top left;\r\n"+
              "}\r\n"+
              ".portal_tab_add{\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_tab_newtab{\r\n"+
              "  cursor:pointer;\r\n"+
              "} \r\n"+
              ".svgAddTabIcon{\r\n"+
              "	fill:blue;\r\n"+
              "}\r\n"+
              ".portal_tab_newtabicon{\r\n"+
              "	background-image: url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' version='1.1' viewBox='0 0 1000 1000' %3E%3Cpath style='fill:%2300dd00;' d='M729.471,457.513h-186.991V270.519c0-23.473-19.006-42.498-42.479-42.498 c-23.473,0-42.498,19.006-42.498,42.498v186.991H270.512c-23.473,0-42.479,19.006-42.479,42.498c0,23.473,19.006,42.479,42.479,42.479 h186.975v187.011c0,23.476,19.022,42.479,42.498,42.479c23.473,0,42.498-19.006,42.498-42.479v-187.011h186.975 c23.476,0,42.498-19.006,42.498-42.479C771.97,476.516,752.944,457.513,729.471,457.513z'/%3E%3C/svg%3E%0A\");\r\n"+
              "	border-width:2px;\r\n"+
              "	border-style:solid;\r\n"+
              "\r\n"+
              "	border-radius:50%;\r\n"+
              "	background-repeat:no-repeat;\r\n"+
              "	background-position:center;\r\n"+
              "\r\n"+
              "}\r\n"+
              ".portal_tab_delete{\r\n"+
              "  background-image:url('rsc/tab_del.png');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:top right;\r\n"+
              "  opacity:.5;\r\n"+
              "  cursor:pointer;\r\n"+
              "  padding:2px 0px 8px 7px;\r\n"+
              "}\r\n"+
              ".portal_tab_delete:hover{\r\n"+
              "  background-image:url('rsc/tab_del_hover.png');\r\n"+
              "  opacity:1;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "}\r\n"+
              ".portal_tab_menu{\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:top center;\r\n"+
              "  opacity:.8;\r\n"+
              "  cursor:pointer;\r\n"+
              "  padding:0px 0px 5px 9px;\r\n"+
              "}\r\n"+
              ".portal_tab_menu:hover{\r\n"+
              "  opacity:1;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "}\r\n"+
              ".portal_tabpane{\r\n"+
              "  overflow:visible;\r\n"+
              "  display:flex;\r\n"+
              "  position:relative;\r\n"+
              "  z-index:1;\r\n"+
              "  width:100%;\r\n"+
              "}\r\n"+
              ".portal_tabScrollPane{\r\n"+
              "	border-style:solid;\r\n"+
              "	border-left-width:0px;\r\n"+
              "	border-right-width:0px;\r\n"+
              "	box-sizing:content-box;\r\n"+
              "	overflow:hidden;\r\n"+
              "}\r\n"+
              ".portal_tabArrow{\r\n"+
              "	text-align:center;\r\n"+
              "	display:inline-flex;\r\n"+
              "	position:relative;\r\n"+
              "}\r\n"+
              ".portal_tabArrow > abbr{\r\n"+
              "	width:100%;\r\n"+
              "}\r\n"+
              "\r\n"+
              ".portal_tab> abbr{\r\n"+
              "	text-decoration:none;\r\n"+
              "	display:inline-block;\r\n"+
              "	overflow:hidden;\r\n"+
              "}\r\n"+
              ".portal_menubar_background{\r\n"+
              "  background:#d4d4d4;\r\n"+
              "  border-color:#d4d4d4;\r\n"+
              "  border:0px;\r\n"+
              "  color: black;\r\n"+
              "}\r\n"+
              ".portal_menubar_menu:hover{\r\n"+
              "  border:1px outset #aaaaaa;\r\n"+
              "}\r\n"+
              ".portal_menubar_menu:active{\r\n"+
              "	transform: scale(.97);\r\n"+
              "}\r\n"+
              ".portal_menubar_menu{\r\n"+
              "  top:0px;\r\n"+
              "  bottom:0px;\r\n"+
              "  margin:1px 0px;\r\n"+
              "  padding:1px 10px;\r\n"+
              "  border-width:1px;\r\n"+
              "  border-style:solid;\r\n"+
              "  border-color:transparent;\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_menubar_menu_hover{\r\n"+
              "  border:1px outset #aaaaaa;\r\n"+
              "  color:#FFFFFF;\r\n"+
              "  background:#777777;\r\n"+
              "}\r\n"+
              ".portal_desktop_top{\r\n"+
              "  cursor:ns-resize;\r\n"+
              "}\r\n"+
              ".portal_desktop_header{\r\n"+
              "  cursor:move;\r\n"+
              "}\r\n"+
              ".portal_desktop_title{\r\n"+
              "  cursor:text;\r\n"+
              "  padding:0px;\r\n"+
              "}\r\n"+
              ".portal_desktop_button{\r\n"+
              "    cursor:pointer;\r\n"+
              "    background-position:center;\r\n"+
              "    background-repeat:no-repeat;\r\n"+
              "	background-size: 100% 100%;\r\n"+
              "}\r\n"+
              ".portal_desktop_button:active {\r\n"+
              "   	background-position:center;\r\n"+
              "   	background-repeat:no-repeat;\r\n"+
              "	filter: contrast(2);\r\n"+
              "	background-size: 100% 100%;\r\n"+
              "}\r\n"+
              ".portal_desktop_close_button{\r\n"+
              "   cursor:pointer;\r\n"+
              "   background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 -2 15 13\" style=\"enable-background:new 0 0 15 10;\" xml:space=\"preserve\"> <g> <path d=\"M15,10h-3.3L7.4,6.2L3,10H0l6-5L0.1,0h3.3l4.3,3.8L12,0h3L9,4.9L15,10z\"/> </g> </svg>');\r\n"+
              "   background-position:center;\r\n"+
              "   background-repeat:no-repeat;\r\n"+
              "}\r\n"+
              ".portal_desktop_min_button{\r\n"+
              " background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 -1 15 12\" style=\"enable-background:new 0 0 15 10;\" xml:space=\"preserve\"> <g id=\"minimize\"> <rect x=\"1.1\" y=\"6.2\" width=\"12.7\" height=\"2.9\"/> </g> </svg>');\r\n"+
              " background-position:center;\r\n"+
              " background-repeat:no-repeat;\r\n"+
              " cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_desktop_max_button{\r\n"+
              " background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 -1 15 12\" style=\"enable-background:new 0 0 15 10;\" xml:space=\"preserve\"> <g id=\"maximize\"> <rect x=\"0.8\" y=\"1.1\" style=\"fill:none;stroke:%23000000;stroke-width:0.5;stroke-miterlimit:10;\" width=\"13.4\" height=\"8\"/> <rect x=\"0.8\" y=\"1.1\" width=\"13.4\" height=\"2.1\"/> </g> </svg> ');\r\n"+
              " background-position:center;\r\n"+
              " background-repeat:no-repeat;\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_desktop_pop_button{\r\n"+
              " background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 -1 15 12\" style=\"enable-background:new 0 0 15 10;\" xml:space=\"preserve\"> <g id=\"popout\"> <polygon points=\"13.6,0.5 6.2,0.5 9,3.3 4.6,7.8 6.3,9.5 10.7,5 13.6,7.9 	\"/> </g> </svg>');\r\n"+
              " background-position:center;\r\n"+
              " background-repeat:no-repeat;\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_form_hidden{\r\n"+
              "	z-index:0;\r\n"+
              "	padding:0px;\r\n"+
              "	background:white;\r\n"+
              "/*#####################*/\r\n"+
              "/* 	display:none; */\r\n"+
              "	width:100%;\r\n"+
              "	height:100%;\r\n"+
              "/*#####################*/\r\n"+
              "}\r\n"+
              ".portal_form_container{\r\n"+
              "	overflow:visible;\r\n"+
              "/*	z-index:0;*/\r\n"+
              "}\r\n"+
              ".portal_form{\r\n"+
              "  padding:0px;\r\n"+
              "  text-align:center;\r\n"+
              "  background:#eeeeee;\r\n"+
              "  background:rgba(0,0,0,0);\r\n"+
              "  background-color:rgba(0,0,0,0);\r\n"+
              "  z-index: 1;\r\n"+
              "  pointer-events:none;\r\n"+
              "/*#####################*/\r\n"+
              "/* 	display:none; */\r\n"+
              "	width:100%;\r\n"+
              "	height:100%;\r\n"+
              "/*#####################*/\r\n"+
              "}\r\n"+
              ".portal_form_canvas{\r\n"+
              "  z-index:2;\r\n"+
              "/*#####################*/\r\n"+
              "/*  	display:none;  */\r\n"+
              "	width:100%;\r\n"+
              "	height:100%;\r\n"+
              "/*#####################*/\r\n"+
              "}\r\n"+
              ".portal_form > *{\r\n"+
              "  pointer-events:all;\r\n"+
              "}\r\n"+
              ".portal_form_table{\r\n"+
              "  width:100%;\r\n"+
              "}\r\n"+
              ".portal_form_input{\r\n"+
              "  text-align:left;\r\n"+
              "}\r\n"+
              ".portal_form_table td{\r\n"+
              "  padding:0px 0px 0px 0px;\r\n"+
              "}\r\n"+
              ".portal_form_buttons{\r\n"+
              "/*#####################*/\r\n"+
              "/* 	display:none; */\r\n"+
              "/*#####################*/\r\n"+
              "  text-align:center;\r\n"+
              "/*   left:0px; */\r\n"+
              "/*   right:0px; */\r\n"+
              "  width:100%;\r\n"+
              "  bottom:0px;\r\n"+
              "  background:#eeeeee;\r\n"+
              "  z-index:1;\r\n"+
              "}\r\n"+
              ".portal_form_label{\r\n"+
              "  text-align:right;\r\n"+
              "  display:flex;\r\n"+
              "  justify-content:flex-end;\r\n"+
              "  align-items: center;\r\n"+
              "  pointer-events:none;\r\n"+
              "}\r\n"+
              ".portal_form_label_help{\r\n"+
              "  cursor:help;\r\n"+
              "  pointer-events:all;\r\n"+
              "}\r\n"+
              ".portal_form_label_help:hover{\r\n"+
              "  text-decoration:underline;\r\n"+
              "}\r\n"+
              ".portal_form_title{\r\n"+
              "   font-weight:bold;\r\n"+
              "   font-size:13px;\r\n"+
              "}\r\n"+
              ".dateformfield{\r\n"+
              "	outline:none !important;\r\n"+
              "/*	border:solid 1px #888888; */\r\n"+
              "	overflow:hidden;\r\n"+
              "}\r\n"+
              ".dateformfield .cal_input{\r\n"+
              "	display:inline-block !important;\r\n"+
              "/*	border-bottom:solid 1px #888888; */\r\n"+
              "	border-width:1px;\r\n"+
              "	border-style:solid;\r\n"+
              "	padding:0px 2px 0px 2px;\r\n"+
              "	margin:0px 2px 0px 0px;\r\n"+
              "	width:5em;\r\n"+
              "}\r\n"+
              ".dateformfield .time_input{\r\n"+
              "	display:inline-block;\r\n"+
              "/*	border-bottom:solid 1px #888888; */\r\n"+
              "	padding:0px 0px 0px 2px;\r\n"+
              "	margin:0px 0px 0px 2px;\r\n"+
              "	width:calc(100% - 5em - 4px);\r\n"+
              "	border-width:1px;\r\n"+
              "	border-style:solid;\r\n"+
              "}\r\n"+
              "\r\n"+
              ".dateformfield:hover{\r\n"+
              "	border:none;\r\n"+
              "}\r\n"+
              ".dateformfield .cal_input:hover{\r\n"+
              "	border:solid 1px #888888; \r\n"+
              "}\r\n"+
              ".dateformfield .time_input:hover{\r\n"+
              "	border:solid 1px #888888; \r\n"+
              "}\r\n"+
              "/*\r\n"+
              ".dateformfield:hover .cal_input{\r\n"+
              "	border:solid 1px rgba(136,136,136,0.4); \r\n"+
              "}\r\n"+
              ".dateformfield:hover .time_input{\r\n"+
              "	border:solid 1px rgba(136,136,136,0.4); \r\n"+
              "}\r\n"+
              "*/\r\n"+
              ".divFieldElement {\r\n"+
              "	border:1px solid transparent;\r\n"+
              "}\r\n"+
              ".time_clock{\r\n"+
              "    background:white; \r\n"+
              "    box-shadow: 0 0 16px -1px #777777;\r\n"+
              "    z-index:9999;\r\n"+
              "    padding:7px;\r\n"+
              "}\r\n"+
              ".time_hours{\r\n"+
              "	display:inline-block;\r\n"+
              "	border:none;\r\n"+
              "	border:solid 1px #cccccc;\r\n"+
              "	width:2em;\r\n"+
              "	margin-right:2px;\r\n"+
              "	height:100%;\r\n"+
              "	-webkit-appearance:none;\r\n"+
              "}\r\n"+
              "\r\n"+
              ".time_minutes{\r\n"+
              "	display:inline-block;\r\n"+
              "	border:none;\r\n"+
              "	border:solid 1px #cccccc;\r\n"+
              "	width:2em;\r\n"+
              "	margin-right:2px;\r\n"+
              "	height:100%;\r\n"+
              "	-webkit-appearance:none;\r\n"+
              "}\r\n"+
              ".time_seconds{\r\n"+
              "	display:inline-block;\r\n"+
              "	border:none;\r\n"+
              "	border:solid 1px #cccccc;\r\n"+
              "	width:2em;\r\n"+
              "	margin-right:2px;\r\n"+
              "	height:100%;\r\n"+
              "	-webkit-appearance:none;\r\n"+
              "}\r\n"+
              ".time_millis{\r\n"+
              "	display:inline-block;\r\n"+
              "	border:none;\r\n"+
              "	border:solid 1px #cccccc;\r\n"+
              "	width:3em; \r\n"+
              "	margin-right:2px;\r\n"+
              "	height:100%;\r\n"+
              "}\r\n"+
              "	\r\n"+
              ".time_meridiem{\r\n"+
              "	display:inline-block;\r\n"+
              "	border:none;\r\n"+
              "	border:solid 1px #cccccc;\r\n"+
              "	width:3em; \r\n"+
              "	height:100%;\r\n"+
              "	-webkit-a");
            out.print(
              "ppearance:none;\r\n"+
              "}\r\n"+
              ".time_colon::before{\r\n"+
              "	content:\":\";\r\n"+
              "	margin-right:2px;\r\n"+
              "}\r\n"+
              ".time_period::before{\r\n"+
              "	content:\".\";\r\n"+
              "	margin-right:2px;\r\n"+
              "}\r\n"+
              ".time_submit{\r\n"+
              "	width:100%;\r\n"+
              "	height:12px;\r\n"+
              "	border:solid 1px #cccccc;\r\n"+
              "}\r\n"+
              ".dayformfield input{\r\n"+
              "	display:inline-block;\r\n"+
              "	border:none;\r\n"+
              "	\r\n"+
              "}\r\n"+
              "\r\n"+
              ".form_field_toggle_disabled{\r\n"+
              "	opacity:0.80 !important;\r\n"+
              "	box-shadow:none !important;\r\n"+
              "}\r\n"+
              ".form_field_toggle_buttons{\r\n"+
              "	position:inherit;\r\n"+
              "	text-align:left;\r\n"+
              "}\r\n"+
              ".form_field_toggle_button{\r\n"+
              "	background: linear-gradient(#41C850, #007700);\r\n"+
              "	color: white;\r\n"+
              "	border: 0px solid #202020;\r\n"+
              "	height: 26px;\r\n"+
              "	min-width: 64px;\r\n"+
              "}\r\n"+
              ".form_field_toggle_button + .form_field_toggle_button{\r\n"+
              "	margin-left:8px;\r\n"+
              "}\r\n"+
              ".form_field_toggle_button_on{\r\n"+
              "	background: #e27027;\r\n"+
              "	box-shadow: 0px 2px 6px 0px #333333 inset;\r\n"+
              "}\r\n"+
              ".form_field_button{\r\n"+
              "  height:17px;\r\n"+
              "  -moz-border-radius:4px;\r\n"+
              "  border-radius: 4px;\r\n"+
              "  font-size: 12px;\r\n"+
              "  font-family:arial;\r\n"+
              "  text-decoration:none;\r\n"+
              "  padding:0px 5px;\r\n"+
              "  border:1px solid black;\r\n"+
              "  cursor:pointer;\r\n"+
              "  white-space:nowrap;\r\n"+
              "  background-color: rgb(210,210,210);\r\n"+
              "	\r\n"+
              "  xbackground: -webkit-gradient( linear, left bottom, left top,\r\n"+
              "	    color-stop(1.00, rgb(210,210,215)),\r\n"+
              "	    color-stop(0.75, rgb(235,235,235)),\r\n"+
              "	    color-stop(0.0, rgb(200,200,200))\r\n"+
              "	    );\r\n"+
              "}\r\n"+
              ".form_field_button:hover{\r\n"+
              "	filter: sepia(0.2) contrast(1.5);\r\n"+
              "	  xbackground-image: -webkit-gradient( linear, left bottom, left top,\r\n"+
              "	    color-stop(1.00, rgb(180,210,255)),\r\n"+
              "	    color-stop(0.75, rgb(225,235,255)),\r\n"+
              "	    color-stop(0.0, rgb(170,200,255))\r\n"+
              "	    );\r\n"+
              "  border:1px solid #4444ff;\r\n"+
              "}\r\n"+
              ".form_field_button:active{\r\n"+
              "	transform: none;\r\n"+
              "	filter: contrast(0.8);\r\n"+
              "}\r\n"+
              ".selectfield_ac{\r\n"+
              "	width:100%;\r\n"+
              "	height:100%;\r\n"+
              "}\r\n"+
              ".selectfield_ac_input{\r\n"+
              "	width:100%;\r\n"+
              "	height:100%;\r\n"+
              "	color:black;\r\n"+
              "	text-indent:4px;\r\n"+
              "	background-image: url(\"data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='13.5' height='10' version='1.1' ><text x='0' y='8' fill='black' font-size='9'>&#9660;</text></svg>\");\r\n"+
              "  	background-position:right center;   \r\n"+
              "	background-repeat:no-repeat;  \r\n"+
              "	padding-right:16px;\r\n"+
              "}\r\n"+
              ".selectfield_ac_list{\r\n"+
              " 	pointer-events:all; \r\n"+
              "	/*overflow-y:scroll;*/\r\n"+
              "	\r\n"+
              "	list-style-position:inside;\r\n"+
              "	padding:0px;\r\n"+
              "	margin:0px;\r\n"+
              "	border:1px solid #407fbf !important;\r\n"+
              "	background:white !important;\r\n"+
              "/*	z-index:1; */\r\n"+
              "}\r\n"+
              ".selectfield_ac_item{\r\n"+
              "	position:static;\r\n"+
              " 	/*pointer-events:none;*/\r\n"+
              "	list-style-type:none;\r\n"+
              "	padding:0px;\r\n"+
              "	margin:0px;\r\n"+
              "	text-indent:4px;\r\n"+
              "	width:100%;\r\n"+
              "}\r\n"+
              ".selectfield_ac_item_active{\r\n"+
              " 	background-color:rgb(30,144,255); \r\n"+
              " 	color:white; \r\n"+
              " 	text-shadow:0px 1px #000000; \r\n"+
              "}\r\n"+
              ".help_label{\r\n"+
              "  text-align:right;\r\n"+
              "  cursor:help;\r\n"+
              "}\r\n"+
              ".help_label:hover{\r\n"+
              "  text-decoration:underline;\r\n"+
              "}\r\n"+
              ".help_popup{\r\n"+
              "  background: #fff196;\r\n"+
              "  color:black;\r\n"+
              "  font-size:12px;\r\n"+
              "  width:180px;\r\n"+
              "  height:60px;\r\n"+
              "  overflow:auto;\r\n"+
              "  border:1px solid #89814d;\r\n"+
              "  padding:2px;\r\n"+
              "  z-index:9999;\r\n"+
              "}\r\n"+
              ".error_popup{\r\n"+
              "  background: #ffffff;\r\n"+
              "  color:black;\r\n"+
              "  font-size:12px;\r\n"+
              "  width:300px;\r\n"+
              "  height:100px;\r\n"+
              "  overflow:auto;\r\n"+
              "  border:1px solid red;\r\n"+
              "  padding:2px;\r\n"+
              "  z-index:9999;\r\n"+
              "}\r\n"+
              ".ul_disc{\r\n"+
              "  list-style-type:disc;	\r\n"+
              "}\r\n"+
              ".headerdiv{\r\n"+
              "  left:0px;\r\n"+
              "  right:16px;\r\n"+
              "  top:40px;\r\n"+
              "  overflow:hidden;\r\n"+
              "}\r\n"+
              "td{\r\n"+
              "  font-size:12px;\r\n"+
              "  padding:0px 0px;\r\n"+
              "  border:0px;\r\n"+
              "}\r\n"+
              "th{\r\n"+
              "  font-size:12px;\r\n"+
              "  background:#000080;\r\n"+
              "  color:white;\r\n"+
              "  cursor:pointer;\r\n"+
              "  font-weight:normal;\r\n"+
              "}\r\n"+
              "th:hover{\r\n"+
              "  font-size:12px;\r\n"+
              "  background:#4040C0;\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".greybar{\r\n"+
              "  background:#DDDDDD;\r\n"+
              "}\r\n"+
              ".greybar:hover{\r\n"+
              "  background:#AAAAFF;\r\n"+
              "}\r\n"+
              ".whitebar:hover{\r\n"+
              "  background:#AAAAFF;\r\n"+
              "}\r\n"+
              ".selected{\r\n"+
              "  background:#8888FF;\r\n"+
              "}\r\n"+
              ".selected:hover{\r\n"+
              "  background:#AAAAFF;\r\n"+
              "}\r\n"+
              ".tablescroll{\r\n"+
              "  top:40px;\r\n"+
              "  bottom:0px;\r\n"+
              "  left:0px;\r\n"+
              "  right:0px;\r\n"+
              "  overflow:scroll;\r\n"+
              "}\r\n"+
              ".table_search{\r\n"+
              "  background-image:url('rsc/table_title_back.gif');\r\n"+
              "  width:100%;\r\n"+
              "  height:20px;\r\n"+
              "  overflow:hidden;\r\n"+
              "  border-width:0px 0px 1px 0px;\r\n"+
              "  border-style:solid;\r\n"+
              "  border-color:black;\r\n"+
              "  padding:0px 20px;\r\n"+
              "}\r\n"+
              ".table_title{\r\n"+
              "  width:100%;\r\n"+
              "  height:100%;\r\n"+
              "  font-size:12px;\r\n"+
              "  left:0px;\r\n"+
              "  color:white;\r\n"+
              "  padding:2px;\r\n"+
              "}\r\n"+
              ".table_search_input{\r\n"+
              "  top:1px;\r\n"+
              "  position:absolute;\r\n"+
              "  right:42px;\r\n"+
              "}\r\n"+
              ".table_search_input_active{\r\n"+
              "  top:1px;\r\n"+
              "  color:black;\r\n"+
              "  position:absolute;\r\n"+
              "  right:42px;\r\n"+
              "  background-color:#ff774d;\r\n"+
              "}\r\n"+
              ".table_search_button{\r\n"+
              "  top:1px;\r\n"+
              "  right:25px;\r\n"+
              "  width:16px;\r\n"+
              "  height:16px;\r\n"+
              "  background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"search\"> <g> <circle style=\"fill:none;stroke:%23ffffff;stroke-width:14;stroke-miterlimit:10;\" cx=\"41.3\" cy=\"41.2\" r=\"24.7\"/> <line style=\"fill:none;stroke:%23ffffff;stroke-width:14;stroke-miterlimit:10;\" x1=\"58.6\" y1=\"58.9\" x2=\"87.7\" y2=\"88\"/> </g> </g> </svg>');\r\n"+
              "  cursor: pointer;\r\n"+
              "}\r\n"+
              ".expand_all_button{\r\n"+
              "  top:1px;\r\n"+
              "  right:25px;\r\n"+
              "  width:16px;\r\n"+
              "  height:16px;\r\n"+
              "  background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"plus_1_\"> <g> <line style=\"fill:none;stroke:%23ffffff;stroke-width:18;stroke-miterlimit:10;\" x1=\"12\" y1=\"50\" x2=\"88\" y2=\"50\"/> <line style=\"fill:none;stroke:%23ffffff;stroke-width:18;stroke-miterlimit:10;\" x1=\"50\" y1=\"12\" x2=\"50\" y2=\"88\"/> </g> </g> </svg>');\r\n"+
              "  cursor: pointer;\r\n"+
              "}\r\n"+
              ".contract_all_button{\r\n"+
              "  top:1px;\r\n"+
              "  right:25px;\r\n"+
              "  width:16px;\r\n"+
              "  height:16px;\r\n"+
              "  background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"minus\"> <line style=\"fill:none;stroke:%23ffffff;stroke-width:18;stroke-miterlimit:10;\" x1=\"12\" y1=\"50\" x2=\"88\" y2=\"50\"/> </g> </svg>');\r\n"+
              "  cursor: pointer;\r\n"+
              "}\r\n"+
              ".table_download_button{\r\n"+
              "  top:1px; \r\n"+
              "  right:1px;\r\n"+
              "  width:16px;\r\n"+
              "  height:16px;\r\n"+
              "  background-image: url('data:image/svg+xml;utf8,<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" style=\"enable-background:new 0 0 100 100;\" xml:space=\"preserve\"> <g id=\"save\"> <g> <line style=\"fill:none;stroke:%23ffffff;stroke-width:10;stroke-miterlimit:10;\" x1=\"8\" y1=\"91\" x2=\"92\" y2=\"91\"/> <g> <line style=\"fill:none;stroke:%23ffffff;stroke-width:12;stroke-miterlimit:10;\" x1=\"50\" y1=\"8\" x2=\"50\" y2=\"71\"/> <polyline style=\"fill:none;stroke:%23ffffff;stroke-width:12;stroke-miterlimit:10;\" points=\"77.3,44.3 50,71.6 22.7,44.3\"/> </g> </g> </g> </svg>');\r\n"+
              "  cursor: pointer;\r\n"+
              "}\r\n"+
              ".menu_hover:hover{\r\n"+
              "   background:#0000CC !important;\r\n"+
              "   color:#FFFFFF !important;\r\n"+
              "}\r\n"+
              "/* .menu td:hover{ */\r\n"+
              "/*    background:#0000CC; */\r\n"+
              "/*    color:#FFFFFF; */\r\n"+
              "/* } */\r\n"+
              ".menu_item {\r\n"+
              "   color:black;\r\n"+
              "   transition: color 25ms ease-in;\r\n"+
              "}\r\n"+
              ".menu_item > td:nth-child(1){\r\n"+
              "	min-width:24px;\r\n"+
              "	width:24px;\r\n"+
              "	background-position:center center;\r\n"+
              "	background-repeat:no-repeat;\r\n"+
              "}\r\n"+
              ".menu_item > td {\r\n"+
              "	pointer-events:all;\r\n"+
              "}\r\n"+
              ".menu_item > td * {\r\n"+
              "	pointer-events:none;\r\n"+
              "}\r\n"+
              ".menu_item > td:nth-child(2){\r\n"+
              "   padding:0px 20px 0px 0px;\r\n"+
              "   height:20px;\r\n"+
              "   border-width:0px 0px 0px 0px;\r\n"+
              "   white-space:nowrap;\r\n"+
              "}\r\n"+
              ".menu_item_help{\r\n"+
              "   background:#fff7b0;\r\n"+
              "   color:#008800 !important;\r\n"+
              "}\r\n"+
              ".menu{\r\n"+
              "   background:#F0F0F0;\r\n"+
              "   border:1px solid grey;\r\n"+
              "   position:absolute;\r\n"+
              "   cursor:pointer;\r\n"+
              "   z-index:9999;\r\n"+
              "   max-width:600px;\r\n"+
              "   overflow-x:auto !important;\r\n"+
              "   border-radius: 5px;\r\n"+
              "   padding: 5px;\r\n"+
              "}\r\n"+
              ".menu_divider{\r\n"+
              "   background:#898989;\r\n"+
              "   height:1px !important;\r\n"+
              "}\r\n"+
              ".menu_divider:hover{\r\n"+
              "   background:#898989 !important;\r\n"+
              "}\r\n"+
              ".menu_divider > td{\r\n"+
              "	padding: 0px;\r\n"+
              "}\r\n"+
              ".menu_item.disabled{\r\n"+
              "   background:#F0F0F0;\r\n"+
              "/*    color:#AAAAAA; */\r\n"+
              "   cursor:default;\r\n"+
              "}\r\n"+
              ".menu_item.disabled > td{\r\n"+
              "	opacity:0.40;\r\n"+
              "}\r\n"+
              ".menu_right_arrow {\r\n"+
              "	position: relative;\r\n"+
              "	display: block;\r\n"+
              "}\r\n"+
              "/* .parent:hover{ */\r\n"+
              "/*   background-image:url('rsc/menu_parent_hover.png') !important; */\r\n"+
              "/*   background:yellow; */\r\n"+
              "/* } */\r\n"+
              ".parent.menu_item_highlighted > td:nth-child(2){\r\n"+
              "   background-image:url('rsc/menu_parent_hover.png') !important; \r\n"+
              "}\r\n"+
              ".ami_help_box_nostyle{\r\n"+
              "	position:absolute;\r\n"+
              "	z-index:9999;\r\n"+
              "	word-break:break-word;\r\n"+
              "	text-align:left;\r\n"+
              "	box-shadow: 0px 0px 1px 1px #e0e0e0;\r\n"+
              "	background: rgba(255,255,255,1.0);\r\n"+
              "	border-radius:7px;\r\n"+
              "	z-index:9999;\r\n"+
              " 	padding: 8px 8px 8px 8px; \r\n"+
              "	word-break:break-word;\r\n"+
              "	user-select:inherit;\r\n"+
              "}\r\n"+
              ".ami_help_box{\r\n"+
              "	position:absolute;\r\n"+
              "	text-align:left;\r\n"+
              "	box-shadow: 0px 0px 1px 1px #e0e0e0;\r\n"+
              "	background: rgba(255,255,255,1.0);\r\n"+
              "	border-radius:7px;\r\n"+
              "	z-index:9999;\r\n"+
              " 	padding: 8px 8px 8px 8px; \r\n"+
              "  	border-left:12px solid #d0d0d0; \r\n"+
              "	word-break:break-word;\r\n"+
              "}\r\n"+
              ".treenode_checkbox{\r\n"+
              "	background-image: url('rsc/checkbox.gif') !important;\r\n"+
              "	background-repeat:no-repeat;\r\n"+
              "	background-position:left;\r\n"+
              "	padding:0px 0px 0px 18px;\r\n"+
              "	cursor:pointer;\r\n"+
              "	pointer-events:inherit !important;\r\n"+
              "}\r\n"+
              ".treenode_checkbox_disabled{\r\n"+
              "	background-image: url('rsc/checkbox_disabled.gif') !important;\r\n"+
              "	background-repeat:no-repeat;\r\n"+
              "    color:#AAAAAA;\r\n"+
              "	background-position:left;\r\n"+
              "	padding:0px 0px 0px 18px;\r\n"+
              "	cursor:pointer;\r\n"+
              "}\r\n"+
              ".treenode_disabled{\r\n"+
              "    color:#AAAAAA;\r\n"+
              "}\r\n"+
              ".treenode_checkbox_checked{\r\n"+
              "	background-image: url('rsc/checkbox_checked.gif') !important;\r\n"+
              "	background-repeat:no-repeat;\r\n"+
              "	background-position:left;\r\n"+
              "	padding:0px 0px 0px 18px;\r\n"+
              "	cursor:pointer;\r\n"+
              "	pointer-events:inherit !important;\r\n"+
              "}\r\n"+
              ".portal_tree_leaf{\r\n"+
              "  float:left;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_tree_expanded{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('rsc/portlet_icon_expanded.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_tree_contracted{\r\n"+
              "  float:left;\r\n"+
              "  background-image:url('r");
            out.print(
              "sc/portlet_icon_contracted.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  width:18px;\r\n"+
              "  height:18px;\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_tree_node_icon_checkbox{\r\n"+
              "	font-size:19px;\r\n"+
              "	bottom:0px;\r\n"+
              "	cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_tree_node_icon_expand{\r\n"+
              "	font-size:10px;\r\n"+
              "	cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_tree_node_icon_collapse{\r\n"+
              "	font-size:10px;\r\n"+
              "	cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_tree_row_selected{\r\n"+
              "  background:#CCCCFF;\r\n"+
              "}\r\n"+
              ".portal_tree_row{\r\n"+
              "  background:none;\r\n"+
              "}\r\n"+
              ".portal_tree_node{\r\n"+
              "  overflow: hidden;\r\n"+
              "  white-space: nowrap;\r\n"+
              "}\r\n"+
              "	.tree_cell {\r\n"+
              "		display:inline-flex;\r\n"+
              "		flex-direction:column;\r\n"+
              "		vertical-align:middle;\r\n"+
              "		position: absolute;\r\n"+
              "		width: 100%;\r\n"+
              "		height:100%;\r\n"+
              "		overflow: hidden;\r\n"+
              "		padding:0px;\r\n"+
              "		border-style:solid;\r\n"+
              "		border-color:#AAAAAA;\r\n"+
              "		border-width:0px 1px 0px 0px;\r\n"+
              "		white-space: nowrap;\r\n"+
              "		outline:none;\r\n"+
              "		}\r\n"+
              ".portal_tree_node:hover{\r\n"+
              "}\r\n"+
              ".portal_tree_text{\r\n"+
              " height:18px;\r\n"+
              " white-space:nowrap;\r\n"+
              "/*  pointer-events:none; */\r\n"+
              "}\r\n"+
              ".portal_addComponents_Dialog{\r\n"+
              "  background-image:url('rsc/add_component_dialog.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  position:absolute;\r\n"+
              "  display:none;\r\n"+
              "  z-index:100;\r\n"+
              "}\r\n"+
              ".portal_addComponents_Button{\r\n"+
              "  background-image:url('rsc/add_component_button_hover.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:left;\r\n"+
              "  position:absolute;\r\n"+
              "  height:38px;\r\n"+
              "  color:Black; \r\n"+
              "  padding:12px 0px 0px 22px;\r\n"+
              "  margin:0px 0px 0px 30px;\r\n"+
              "  font-size:14px;\r\n"+
              "  cursor:pointer;\r\n"+
              "}\r\n"+
              ".portal_addComponents_Button:hover{\r\n"+
              "  background-image:url('rsc/add_component_button.gif');\r\n"+
              "  color:blue;\r\n"+
              "}\r\n"+
              ".nooverflow{\r\n"+
              "  overflow:hidden;\r\n"+
              "}\r\n"+
              ".configDoc{\r\n"+
              "  z-index:9999;\r\n"+
              "  position:absolute;\r\n"+
              "  height:20px;\r\n"+
              "  bottom:0px;\r\n"+
              "  overflow:hidden;\r\n"+
              "  padding:4px;\r\n"+
              "  width:300px;\r\n"+
              "  right:100px;\r\n"+
              "  background-image:url('rsc/config.gif');\r\n"+
              "}\r\n"+
              ".highlight {\r\n"+
              "  z-index:9996;\r\n"+
              "  position:absolute;\r\n"+
              "  border:1px solid black;\r\n"+
              "  opacity:.3;\r\n"+
              "  background:red;\r\n"+
              "}\r\n"+
              ".disableScreen {\r\n"+
              "  z-index:9997;\r\n"+
              "  position:absolute;\r\n"+
              "  display:none;\r\n"+
              "  background:none;\r\n"+
              "  top:0px;\r\n"+
              "  left:0px;\r\n"+
              "  right:0px;\r\n"+
              "  bottom:0px;\r\n"+
              "}\r\n"+
              ".btn_show_up{\r\n"+
              "  background-image:url('rsc/btn_show_up.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  width:30px;\r\n"+
              "  height:16px;\r\n"+
              "}\r\n"+
              ".btn_show_dn{\r\n"+
              "  background-image:url('rsc/btn_show_dn.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  width:30px;\r\n"+
              "  height:16px;\r\n"+
              "}\r\n"+
              ".confirm_dialog{\r\n"+
              "  background-image:url('rsc/headers/chrome.jpg');\r\n"+
              "}\r\n"+
              ".confirm_dialog_icon_important{\r\n"+
              "  background-image:url('rsc/headers/dialog_important.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "}\r\n"+
              ".confirm_dialog_icon_question{\r\n"+
              "  background-image:url('rsc/headers/dialog_question.gif');\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "}\r\n"+
              ".confirm_dialog_text{\r\n"+
              "  text-align:left;\r\n"+
              "  padding:20px;\r\n"+
              "  background:none;\r\n"+
              "  font-size:13px;\r\n"+
              "  text-align:center;\r\n"+
              "  height:100%;\r\n"+
              "  width:100%;\r\n"+
              "}\r\n"+
              ".confirm_dialog_text p{\r\n"+
              "  display:table-cell;\r\n"+
              "  vertical-align:middle;\r\n"+
              "}\r\n"+
              ".confirm_dialog_button_row{\r\n"+
              "  width:100%;\r\n"+
              "  height:100%;\r\n"+
              "  text-align:center;\r\n"+
              "}\r\n"+
              ".confirm_dialog_button:hover{\r\n"+
              "  background-image:url('rsc/headers/dialog_button.gif');\r\n"+
              "  color:#333333;\r\n"+
              "}\r\n"+
              ".confirm_dialog_button{\r\n"+
              "  display:inline-block;\r\n"+
              "  background-repeat:no-repeat;\r\n"+
              "  background-position:center;\r\n"+
              "  background-image:url('rsc/headers/dialog_button_dark.gif');\r\n"+
              "  color:#000000;\r\n"+
              "  padding:15px 0px;\r\n"+
              "  width:140px;\r\n"+
              "  font-weight:bold;\r\n"+
              "  cursor:pointer;\r\n"+
              "  text-align:center;\r\n"+
              "  font-size:14px;\r\n"+
              "}\r\n"+
              "#blankpage-loading-animation-container {\r\n"+
              "	display:flex;\r\n"+
              "	justify-content:center;\r\n"+
              "	align-items:center;\r\n"+
              "	width:100vw;\r\n"+
              "	height:100vh;\r\n"+
              "	flex-direction:column;\r\n"+
              "	background:#FAFFFA;\r\n"+
              "	position: relative;\r\n"+
              "}\r\n"+
              ".bp-anim-wrapper {\r\n"+
              "	border-radius:35px;\r\n"+
              "    background-color:#FAFFFA;\r\n"+
              "    width:250px;\r\n"+
              "    height: 250px;\r\n"+
              "    display:flex;\r\n"+
              "    justify-content:center;\r\n"+
              "    align-items:center;\r\n"+
              "    flex-direction:column;\r\n"+
              "    position: relative;\r\n"+
              "}\r\n"+
              "#blankpage-loading-animation-container img {\r\n"+
              "	width:120px;\r\n"+
              "	height:120px;\r\n"+
              "	margin-bottom: 10px;\r\n"+
              "}\r\n"+
              "#blankpage-loading-animation-container .bp-animation-text {\r\n"+
              "	color:#8f847c;\r\n"+
              "	font-weight: bold;\r\n"+
              "	position: relative;\r\n"+
              "	font-size: 16px;\r\n"+
              "	animation: fade-in-out 5s linear infinite;\r\n"+
              "}\r\n"+
              "#blankpage-info-container {\r\n"+
              "	position: relative;\r\n"+
              "}\r\n"+
              "@keyframes fade-in-out {\r\n"+
              "  0%,100% { opacity: 0; }\r\n"+
              "  90% { opacity: 1; }\r\n"+
              "}\r\n"+
              "#customScroll {\r\n"+
              "	--scrollbar-gripcolor: #657080;\r\n"+
              "	--scrollbar-trackcolor: #e2e2e2;\r\n"+
              "	--scrollbar-radius: 0px;\r\n"+
              "	<%-- below is for firefox --%>\r\n"+
              "	scrollbar-color: var(--scrollbar-gripcolor) var(--scrollbar-trackcolor);\r\n"+
              "}\r\n"+
              "\r\n"+
              "#customScroll::-webkit-scrollbar {\r\n"+
              "  width: 13px;\r\n"+
              "  \r\n"+
              "}\r\n"+
              "\r\n"+
              "#customScroll::-webkit-scrollbar-track {\r\n"+
              "background-color:#f1f1f1;\r\n"+
              "  background-color: var(--scrollbar-trackcolor);\r\n"+
              "}\r\n"+
              "\r\n"+
              "#customScroll::-webkit-scrollbar-thumb {\r\n"+
              "background-color:#c1c1c1;\r\n"+
              "border-radius: var(--scrollbar-radius);\r\n"+
              "  background-color: var(--scrollbar-gripcolor);\r\n"+
              "}\r\n"+
              "\r\n"+
              "</style>\r\n"+
              "<link rel=\"stylesheet\" type=\"text/css\" href=\"custom.css?\" />\r\n"+
              "<BODY>\r\n"+
              "<pre style=\"display:inline;z-index:9999\" id=\"copybuf\"></pre>\r\n"+
              "<div id=\"blankpage-loading-animation-container\">\r\n"+
              "	<div class=\"bp-anim-wrapper\">\r\n"+
              "		<img src=\"rsc/blank-loading-animation.gif\">\r\n"+
              "		<div id=\"waitingMessage\" class=\"bp-animation-text\">Loading Dashboard</div>\r\n"+
              "	</div>\r\n"+
              "	<div id=\"blankpage-info-container\">\r\n"+
              "	</div>\r\n"+
              "</div>\r\n"+
              "</BODY>\r\n"+
              "<script>\r\n"+
              "\r\n"+
              "var pgid=__PGID;\r\n"+
              "ajaxAndEval('portal.ajax',true,true,{type:'init',webWindowId:window.windowId,width:window.innerWidth,height:window.innerHeight,");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.PAGEID);
            out.print(
              ":pgid});\r\n"+
              "var waitingMessageDiv = document.getElementById(\"waitingMessage\");\r\n"+
              "var waitingMessages = [\"Loading Dashboard\", \"Hang Tight\", \"Crunching Data\"];\r\n"+
              "var idx = 1;\r\n"+
              "var blankPageAnimationInterval = setInterval(function() {\r\n"+
              "    if (idx == waitingMessages.length)\r\n"+
              "        idx = 0;\r\n"+
              "    waitingMessageDiv.innerHTML=waitingMessages[idx];\r\n"+
              "    idx++;\r\n"+
              "}, 5000);\r\n"+
              "\r\n"+
              "var splashScreenInfoContainer = document.getElementById(\"blankpage-info-container\");\r\n"+
              "if (splashScreenInfoContainer != null)\r\n"+
              "	splashScreenInfoContainer.innerHTML = '");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("splashScreenInfoHtml"));
            out.print(
              "';\r\n"+
              "\r\n"+
              "</script>\r\n"+
              "</HTML>\r\n"+
              "</BODY>\r\n"+
              "<script>\r\n"+
              "initUtils();\r\n"+
              "</script>\r\n"+
              "\r\n"+
              "\r\n"+
              "");
          }

	}
	
}