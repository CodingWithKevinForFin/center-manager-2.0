package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_portals_jsp_1 extends AbstractHttpHandler{

	public amiweb_portals_jsp_1() {
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
          if(!com.f1.http.HttpUtils.toBoolean(com.f1.ami.web.auth.AmiWebLoginHttpHandler.isLoggedIn(request))){
            request.sendRedirect((String)request.findAttribute("loggedOutUrl"));
if(true) return;
            } else {            out.print(
              "\r\n"+
              "<html lang=\"en\" dir=\"ltr\">\r\n"+
              "  <head>\r\n"+
              "    <base href='");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("url_prepend"));
            out.print(
              "'>\r\n"+
              "    <meta charset=\"utf-8\">\r\n"+
              "    <link rel=\"icon\" href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("favIcon"));
            out.print(
              "\" />\r\n"+
              "    <style media=\"screen\">\r\n"+
              "      body {\r\n"+
              "        margin: 0;\r\n"+
              "        padding: 0;\r\n"+
              "        background: linear-gradient(45deg, #2C1E4A 0%, #06002E 100%);\r\n"+
              "        font-family: Arial, sans-serif;\r\n"+
              "        font-size: 14px;\r\n"+
              "        height: 100vh;\r\n"+
              "      }\r\n"+
              "      .logo-container {\r\n"+
              "        width: 200px;\r\n"+
              "        position: absolute;\r\n"+
              "        z-index: 999;\r\n"+
              "        margin-top: 10px;\r\n"+
              "        margin-left: 10px;\r\n"+
              "      }\r\n"+
              "      button  {\r\n"+
              "      	cursor: pointer;\r\n"+
              "      	background-color: #515976;\r\n"+
              "      	border: none;\r\n"+
              "      	color: white;\r\n"+
              "      	padding-left: 5px;\r\n"+
              "      	padding: 2px 5px;\r\n"+
              "      	border-radius: 3px;\r\n"+
              "      	font-weight: bold;\r\n"+
              "      }\r\n"+
              "      table {\r\n"+
              "      	width: 100%;\r\n"+
              "      }\r\n"+
              "      table, td, th {\r\n"+
              " 	 	border: 1px solid #333639;\r\n"+
              "  		border-collapse: collapse;\r\n"+
              "  		color: white;\r\n"+
              "	  }\r\n"+
              "	  .align-left {\r\n"+
              "	  	text-align: left;\r\n"+
              "	  }\r\n"+
              "	  th {\r\n"+
              "	  	background-color: #330543;\r\n"+
              "	  }  	\r\n"+
              "	.main-container {\r\n"+
              "		display: flex;\r\n"+
              "    	justify-content: center;\r\n"+
              "    	align-items: center;\r\n"+
              "	}\r\n"+
              "	.inner-container {\r\n"+
              "		width: 600px;\r\n"+
              "	}\r\n"+
              "	.title {\r\n"+
              "		text-align: center;\r\n"+
              "    	color: white;\r\n"+
              "    	font-size: 20px;\r\n"+
              "	}\r\n"+
              "	tr {\r\n"+
              "		transition: 100ms ease-out;\r\n"+
              "	}\r\n"+
              "	tr:hover {\r\n"+
              "		transform: scale(1.009);\r\n"+
              "		background: #17011e;\r\n"+
              "	}\r\n"+
              "	td a {\r\n"+
              "		display: block;\r\n"+
              "		width: 100%;\r\n"+
              "		height: 100%;\r\n"+
              "  		padding: 2px;\r\n"+
              "	}\r\n"+
              "    </style>\r\n"+
              "  </head>\r\n"+
              "  <body>\r\n"+
              "    <title>Session Chooser</title>\r\n"+
              "	 <div class=\"logo-container\"> <a href=\"https://3forge.com\"> <img src=\"portal/rsc/ami/menubar_logo3_color2.svg\"> </a> </div>\r\n"+
              "     <div class=\"main-container\">\r\n"+
              "	     <div class=\"inner-container\">\r\n"+
              "     		");
            if(!com.f1.http.HttpUtils.toBoolean(request.findAttribute("canAddSession"))){
              out.print(
                "\r\n"+
                "	 		   <center><span style='color:#FFFFAA;font-size:18px'>");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("messageMaxSessions"));
              out.print(
                "</span>\r\n"+
                "	 		");
            }
            out.print(
              "\r\n"+
              "	     	<p class=\"title\">3forge Sessions</p>\r\n"+
              "		     <table>\r\n"+
              "		     	 <tr style=\"pointer-events:none;\">\r\n"+
              "		     	 	<th><img src=\"portal/rsc/ami/session.svg\" style=\"height:18px;\"> Sessions</th>\r\n"+
              "		     	 	<th>Layout</th>\r\n"+
              "		     	 	<th>Label</th>\r\n"+
              "		     	 	<th></th>\r\n"+
              "		     	 </tr>\r\n"+
              "		     			\r\n"+
              "		          ");
            for(Object page: com.f1.http.HttpUtils.toIterable(request.findAttribute("activePages"))){
              out.print(
                "\r\n"+
                "						<tr>\r\n"+
                "							<td class=\"session-name align-left\">\r\n"+
                "		                		");
              com.f1.suite.web.portal.impl.BasicPortletManager.onPageLoad(request);
              out.print(
                "\r\n"+
                "		                		<a style='color:white;text-decoration:none' href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL);
              out.print(
                "?F1PGID=");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"pgId",false));
              out.print(
                "\"> \r\n"+
                "		                			");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"name",false));
              out.print(
                "\r\n"+
                "		                		</a>\r\n"+
                "		                	</td>\r\n"+
                "							\r\n"+
                "							<td class=\"align-left\">\r\n"+
                "								<a style='color:white;text-decoration:none' href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL);
              out.print(
                "?F1PGID=");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"pgId",false));
              out.print(
                "\"> \r\n"+
                "								 	");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"layout",false));
              out.print(
                " \r\n"+
                "								</a>\r\n"+
                "							</td>\r\n"+
                "							\r\n"+
                "							<td class=\"align-left\"> \r\n"+
                "								<a style='color:white;text-decoration:none' href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL);
              out.print(
                "?F1PGID=");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"pgId",false));
              out.print(
                "\"> \r\n"+
                "									");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"label",false));
              out.print(
                "\r\n"+
                "								</a>\r\n"+
                "							</td>\r\n"+
                "		                	\r\n"+
                "		                	<td>\r\n"+
                "		                		<center>\r\n"+
                "		                			<a style='color:white;text-decoration:none' href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.URL_END);
              out.print(
                "?F1PGID=");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"pgId",false));
              out.print(
                "\">\r\n"+
                "		                				<img src=\"portal/rsc/ami/close.svg\" style=\"height:18px;\">\r\n"+
                "		                			</a>\r\n"+
                "		                		</center>\r\n"+
                "		                	</td>\r\n"+
                "		                <tr>\r\n"+
                "		          ");
            }
            if(com.f1.http.HttpUtils.toBoolean(request.findAttribute("hasHeadlessPages"))){
              out.print(
                "\r\n"+
                "		            <tr style=\"pointer-events:none;\">\r\n"+
                "		            	<th>\r\n"+
                "		            		<img src=\"portal/rsc/ami/headless-session.svg\" style=\"height:18px;\"> Headless&nbsp;Sessions&nbsp;Not&nbsp;Owned&nbsp;By&nbsp;Me\r\n"+
                "		            	</th>\r\n"+
                "		            	<th></th>\r\n"+
                "		            	<th></th>\r\n"+
                "		            	<th></th>\r\n"+
                "		            </tr>\r\n"+
                "		              ");
              for(Object page: com.f1.http.HttpUtils.toIterable(request.findAttribute("headlessPages"))){
                out.print(
                  "\r\n"+
                  "		                 <tr>\r\n"+
                  "		                 	<td>\r\n"+
                  "		                 		<a style='color:white;text-decoration:none' href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.ami.web.pages.AmiWebPages.URL_OWN_HEADLESS);
                out.print(
                  "?F1PGID=");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"pgId",false));
                out.print(
                  "\">\r\n"+
                  "		                 			");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"name",false));
                out.print(
                  "\r\n"+
                  "		                 		</a>\r\n"+
                  "		            		</td>\r\n"+
                  "		                 	\r\n"+
                  "		                 	<td> \r\n"+
                  "		                 		<a style='color:white;text-decoration:none' href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.ami.web.pages.AmiWebPages.URL_OWN_HEADLESS);
                out.print(
                  "?F1PGID=");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"pgId",false));
                out.print(
                  "\">\r\n"+
                  "		      	           			");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"layout",false));
                out.print(
                  "\r\n"+
                  "		      	           		</a>\r\n"+
                  "		                 	</td>\r\n"+
                  "		                 	\r\n"+
                  "		                 	<td>\r\n"+
                  "		                 		<a style='color:white;text-decoration:none' href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.ami.web.pages.AmiWebPages.URL_OWN_HEADLESS);
                out.print(
                  "?F1PGID=");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"pgId",false));
                out.print(
                  "\">\r\n"+
                  "		                 			");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"label",false));
                out.print(
                  "\r\n"+
                  "		                 		</a>	\r\n"+
                  "		                 	</td>\r\n"+
                  "		            		\r\n"+
                  "		            		<td>\r\n"+
                  "		                 			");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,VH.getNestedValue(page,"comment",false));
                out.print(
                  "\r\n"+
                  "		            			<!-- headless sessions don't have close buttons, keep the cell empty -->\r\n"+
                  "		            		</td>\r\n"+
                  "		            	</tr>\r\n"+
                  "		            ");
              }
            }
            out.print(
              "\r\n"+
              "		     </table>\r\n"+
              "     		<br><br>\r\n"+
              "	 		   <center>\r\n"+
              "     		");
            if(com.f1.http.HttpUtils.toBoolean(request.findAttribute("canAddSession"))){
              out.print(
                "\r\n"+
                "	 		   <a style='color:white;text-decoration:none;' href=\"");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.URL_START);
              out.print(
                "?");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,com.f1.suite.web.portal.impl.BasicPortletManager.KEEP_EXISTING_OPEN);
              out.print(
                "=true&LAYOUT=\">\r\n"+
                "	 		   	<button>Start a new session</button>\r\n"+
                "	 		   </a>\r\n"+
                "	 		");
            }
            out.print(
              "\r\n"+
              "	 		   &nbsp;&nbsp;<a style='color:white;text-decoration:none;' href=\"logout\">\r\n"+
              "	 		   		<button>Logout (Ends all sessions)</button>\r\n"+
              "	 		   </a>\r\n"+
              "	     </div>\r\n"+
              "  <script>\r\n"+
              "    var modCount='");
com.f1.http.tag.OutTag.escapeHtml_Quotes(out,request.findAttribute("pagesModCount"));
            out.print(
              "';\r\n"+
              "    function ajax(){\r\n"+
              "      var r=new XMLHttpRequest(); \r\n"+
              "      r.open(\"GET\",\"modcount\",true);\r\n"+
              "      r.responseType=\"text\";\r\n"+
              "      r.onreadystatechange=function(o){ \r\n"+
              "       if (r.readyState!=4) return;\r\n"+
              "       var res=r.response;\r\n"+
              "       if (res==\"none\" || res!=modCount)\r\n"+
              "         window.location.reload();\r\n"+
              "       else\r\n"+
              "         window.setTimeout( function() { ajax(); }, 1000);\r\n"+
              "       };\r\n"+
              "      r.setRequestHeader(\"Content-type\",\"text/html\"); \r\n"+
              "      r.send();\r\n"+
              "    };\r\n"+
              "    window.setTimeout( function() { ajax(); }, 1000);\r\n"+
              "  </script>\r\n"+
              "");
          }
          out.print(
            "\r\n"+
            "  </body>\r\n"+
            "\r\n"+
            "</html>\r\n"+
            "\r\n"+
            "");

	}
	
}