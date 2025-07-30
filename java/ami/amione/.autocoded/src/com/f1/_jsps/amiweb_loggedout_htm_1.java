package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_loggedout_htm_1 extends AbstractHttpHandler{

	public amiweb_loggedout_htm_1() {
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
            "<!DOCTYPE html>\r\n"+
            "");
          if(com.f1.http.HttpUtils.toBoolean(com.f1.ami.web.auth.AmiWebLoginHttpHandler.isLoggedIn(request))){
            request.sendRedirect((String)com.f1.ami.web.pages.AmiWebPages.URL_PORTALS);
if(true) return;
            } else {            out.print(
              "\r\n"+
              "\r\n"+
              "<html lang=\"en\" dir=\"ltr\">\r\n"+
              "  <head>\r\n"+
              "    <meta charset=\"utf-8\">\r\n"+
              "    <base href='");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("url_prepend"));
            out.print(
              "'>\r\n"+
              "  	<link rel=\"icon\" href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("favIcon"));
            out.print(
              "\" />\r\n"+
              "  \r\n"+
              "    <style media=\"screen\">\r\n"+
              "      body {\r\n"+
              "        margin: 0;\r\n"+
              "        padding: 0;\r\n"+
              "        font-family: Arial, sans-serif;\r\n"+
              "        font-size: 14px;\r\n"+
              "        background: linear-gradient(45deg, #2C1E4A 0%, #06002E 100%);\r\n"+
              "      }\r\n"+
              "      .logo-container {\r\n"+
              "        width: 200px;\r\n"+
              "        position: absolute;\r\n"+
              "        z-index: 999;\r\n"+
              "        margin-top: 10px;\r\n"+
              "        margin-left: 10px;\r\n"+
              "      }\r\n"+
              "      .logo {\r\n"+
              "        padding: 15px;\r\n"+
              "        width: 200px;\r\n"+
              "      }\r\n"+
              "      .container {\r\n"+
              "        position: relative;\r\n"+
              "        display: flex;\r\n"+
              "        align-items: center;\r\n"+
              "        justify-content: center;\r\n"+
              "        flex-direction: column;\r\n"+
              "        height: 100vh;\r\n"+
              "      }\r\n"+
              "      .container2 {\r\n"+
              "        position: relative;\r\n"+
              "        align-items: center;\r\n"+
              "        justify-content: center;\r\n"+
              "        height: 100vh;\r\n"+
              "      }\r\n"+
              "      .container > div {\r\n"+
              "      	margin: 15px;\r\n"+
              "      }\r\n"+
              "      .logout-msg {\r\n"+
              "      	color: white;\r\n"+
              "      	font-size: 30px;\r\n"+
              "      }\r\n"+
              "      .logout-msg-btn {\r\n"+
              "      	display: flex;\r\n"+
              "      	justify-content: center;\r\n"+
              "      	align-items: center;\r\n"+
              "      }\r\n"+
              "      .logout-msg-btn > div {\r\n"+
              "      	margin-right: 10px;\r\n"+
              "      }\r\n"+
              "      .too-soon {\r\n"+
              "      	color: #b7b7b7;\r\n"+
              "      	font-size: 20px;\r\n"+
              "      }\r\n"+
              "      .logout-btn, button, .logout-btn a {\r\n"+
              "      	cursor: pointer;\r\n"+
              "      	background: #5C6379;\r\n"+
              "      	border: none;\r\n"+
              "      	color: white;\r\n"+
              "      	padding-left: 5px;\r\n"+
              "      	padding: 2px 5px;\r\n"+
              "      	border-radius: 3px;\r\n"+
              "      	font-weight: bold;\r\n"+
              "      }\r\n"+
              "      .content {\r\n"+
              "        position: absolute;\r\n"+
              "        width: 350px;\r\n"+
              "      }\r\n"+
              "      #browsernotice {\r\n"+
              "      	background-color: #F2DEDE;\r\n"+
              "	    text-align: center;\r\n"+
              "      }\r\n"+
              "      .browsernotice-warning-text {\r\n"+
              "      	padding: 10px;\r\n"+
              "      	color: #A94442;\r\n"+
              "      }\r\n"+
              "    </style>\r\n"+
              "  </head>\r\n"+
              "  <body>\r\n"+
              "  \r\n"+
              "  	<div id=\"browsernotice\"></div>\r\n"+
              "  \r\n"+
              "	<div class=\"logo-container\">\r\n"+
              "      <a href=\"https://3forge.com\">\r\n"+
              "       <img src=\"portal/rsc/ami/menubar_logo3_color2.svg\">\r\n"+
              "      </a>\r\n"+
              "    </div>\r\n"+
              "  <title>Logged Out</title>\r\n"+
              "    <div class=\"container\">\r\n"+
              "      <div class=\"logout-msg\">You have been logged out successfully!</div>\r\n"+
              "	  <div class=\"logout-msg-btn\">\r\n"+
              "	  	<div class=\"too-soon\">logged out too soon?</div>\r\n"+
              "	  	<div class=\"logout-btn\"><a href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("indexPage"));
            out.print(
              "\"><button>Login</button></a></div>\r\n"+
              "	  </div>    \r\n"+
              "    </div>\r\n"+
              "\r\n"+
              "");
          }
          out.print(
            "\r\n"+
            "</html>\r\n"+
            "\r\n"+
            "");

	}
	
}