package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_index2_htm_1 extends AbstractHttpHandler{

	public amiweb_index2_htm_1() {
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
              "<html lang=\"en\" dir=\"ltr\">\r\n"+
              "  <head>\r\n"+
              "    <meta charset=\"utf-8\">\r\n"+
              "    \r\n"+
              "  <link rel=\"icon\" href=\"portal/");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("favIcon"));
            out.print(
              "\" />\r\n"+
              "  <title>3forge");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("webLoginTitleSuffix"));
            out.print(
              "</title>\r\n"+
              "  <link rel=\"stylesheet\" type=\"text/css\" href=\"rsc/ami/index2.css\">\r\n"+
              "  </head>\r\n"+
              "  <body onresize=\"updateBounds()\">\r\n"+
              "  \r\n"+
              "  	<div id=\"browsernotice\"></div>\r\n"+
              "	\r\n"+
              "    <div class=\"container\">\r\n"+
              "    \r\n"+
              "      <canvas width=100% height=100% id=\"canvas\">\r\n"+
              "      </canvas>\r\n"+
              "      \r\n"+
              "      <div class=\"content\">\r\n"+
              "\r\n"+
              "		<!-- code for inserting custom images -->\r\n"+
              "        ");
            if(com.f1.http.HttpUtils.toBoolean(SH.is(request.findAttribute("loginPageLogo")))){
              out.print(
                "\r\n"+
                "        	<div align=\"center\">\r\n"+
                "				<img id=\"login-custom-logo\" src=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.ami.web.pages.AmiWebPages.URL_CUSTOM_LOGO);
              out.print(
                "\" width=\"350px\" height=\"200px\">\r\n"+
                "			</div>\r\n"+
                "       		<h3 class=\"text-white\" align=\"center\">Powered By</h3> \r\n"+
                "		");
            }
            out.print(
              "\r\n"+
              "       	\r\n"+
              "		<div class=\"logo-container\">\r\n"+
              "	      <a href=\"https://3forge.com\" target=\"_blank\">\r\n"+
              "	       <img src=\"rsc/ami/menubar_logo3_color2.svg\">\r\n"+
              "	      </a>\r\n"+
              "	    </div>\r\n"+
              "		\r\n"+
              "        <form id=\"login-form\" class=\"login-form\" method=\"post\" action=\"login\" accept-charset=\"UTF-8\" role=\"form\" >\r\n"+
              "            <div class='error'><label for='' id=\"error-message\">");
com.f1.http.tag.OutTag.escapeHtml_Full(out,request.findAttribute("error"));
            out.print(
              "&nbsp;</label></div>\r\n"+
              "            \r\n"+
              "            <label class=\"field-label\">Username</label>\r\n"+
              "\r\n"+
              "            <div><input id=\"username\" name=\"username\" style=\"margin-top:0px;\" type=\"text\" value=\"");
            if(com.f1.http.HttpUtils.toBoolean(SH.isnt(request.findAttribute("username")))){
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("webLoginDefaultUser"));
            }
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("username"));
            out.print(
              "\"/>\r\n"+
              "			  ");
            if(com.f1.http.HttpUtils.toBoolean(SH.is(request.findAttribute("webLoginDefaultUser")))){
              out.print(
                "<BR><span>(Default: <span style='background:yellow'>");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("webLoginDefaultUser"));
              out.print(
                "</span>)</span><br><br>");
            }
            out.print(
              "\r\n"+
              "			</div>\r\n"+
              "			\r\n"+
              "			<label class=\"field-label\">Password</label>\r\n"+
              "\r\n"+
              "            <div><input type='password' autocomplete='current-password' id=\"password\" name=\"password\" type=\"text\" value=\"\"/>\r\n"+
              "			  ");
            if(com.f1.http.HttpUtils.toBoolean(SH.is(request.findAttribute("webLoginDefaultPass")))){
              out.print(
                "<BR><span>(Default: <span style='background:yellow'>");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("webLoginDefaultPass"));
              out.print(
                "</span>)</span><br><br>");
            }
            out.print(
              "\r\n"+
              "			 </div>\r\n"+
              "			\r\n"+
              "			<center>\r\n"+
              "				<div><button type=\"submit\" id=\"login\" onclick='onLoginBtnPress()' title=\"Login\"><span id=\"main-text\">Login</span><span id=\"waiting\"></span></button></div>\r\n"+
              "				<!-- \r\n"+
              "				<div><input type=\"submit\" id=\"login\" value=\"&nbsp;Login&nbsp;\" onclick='onLoginBtnPress()' title=\"Login\" /></div>\r\n"+
              " 				-->\r\n"+
              "			</center>\r\n"+
              "			\r\n"+
              "			<div class=\"error\" style='color:#FF0000;padding-bottom:10px;'>\r\n"+
              "			  ");
            if(!com.f1.http.HttpUtils.toBoolean(request.findAttribute("f1license_status"))){
              out.print(
                "\r\n"+
                "			    Due to Licensing, process will shutdown at:<BR><B>");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("f1license_termtime"));
              out.print(
                "</B><BR>Please visit 3forge.com and generate a license file for application '<b>");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("f1license_app"));
              out.print(
                "</b>' on host '<b>");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("f1license_host"));
              out.print(
                "</b>'\r\n"+
                "			  ");
              } else {              if(com.f1.http.HttpUtils.toBoolean(System.currentTimeMillis() > (Long)request.findAttribute("f1license_warnms"))){
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("messageLicenseExpires"));
              }
            }
            out.print(
              "\r\n"+
              "			</div>\r\n"+
              "          \r\n"+
              "          ");
            if(com.f1.http.HttpUtils.toBoolean(OH.ne(request.findAttribute("termsText"),null))){
              out.print(
                "\r\n"+
                "				<div> <input type=\"checkbox\" id=\"accept_agreement\" style=\"width:auto;\" name='accept_agreement' title=\"TermsAndConditions\" value='");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("termsSignature"));
              out.print(
                "'/>I accept the agreement below</div>\r\n"+
                "			");
            }
            if(com.f1.http.HttpUtils.toBoolean(OH.ne(request.findAttribute("termsText"),null))){
              out.print(
                "\r\n"+
                "			  <div id='terms'>");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("termsText"));
              out.print(
                "</div>\r\n"+
                "			");
            }
            out.print(
              "\r\n"+
              "\r\n"+
              "        </form>\r\n"+
              "        \r\n"+
              "        \r\n"+
              "\r\n"+
              "      </div>\r\n"+
              "    \r\n"+
              "    </div>\r\n"+
              "\r\n"+
              "  </body>\r\n"+
              "  \r\n"+
              "  <script src=\"rsc/ami/index2.js\" type=\"text/javascript\"></script>\r\n"+
              "  \r\n"+
              "\r\n"+
              "</html>\r\n"+
              "\r\n"+
              "");
          }

	}
	
}