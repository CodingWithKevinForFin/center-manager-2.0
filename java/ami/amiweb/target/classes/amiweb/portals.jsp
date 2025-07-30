<!DOCTYPE html>
<f1:ifNot test="com.f1.ami.web.auth.AmiWebLoginHttpHandler.isLoggedIn(request)">
  <f1:redirect page="${loggedOutUrl}"/>
<f1:else/>
<html lang="en" dir="ltr">
  <head>
    <base href='<f1:out value="${url_prepend}"/>'>
    <meta charset="utf-8">
    <link rel="icon" href="<f1:out value="${favIcon}"/>" />
    <style media="screen">
      body {
        margin: 0;
        padding: 0;
        background: linear-gradient(45deg, #2C1E4A 0%, #06002E 100%);
        font-family: Arial, sans-serif;
        font-size: 14px;
        height: 100vh;
      }
      .logo-container {
        width: 200px;
        position: absolute;
        z-index: 999;
        margin-top: 10px;
        margin-left: 10px;
      }
      button  {
      	cursor: pointer;
      	background-color: #515976;
      	border: none;
      	color: white;
      	padding-left: 5px;
      	padding: 2px 5px;
      	border-radius: 3px;
      	font-weight: bold;
      }
      table {
      	width: 100%;
      }
      table, td, th {
 	 	border: 1px solid #333639;
  		border-collapse: collapse;
  		color: white;
	  }
	  .align-left {
	  	text-align: left;
	  }
	  th {
	  	background-color: #330543;
	  }  	
	.main-container {
		display: flex;
    	justify-content: center;
    	align-items: center;
	}
	.inner-container {
		width: 600px;
	}
	.title {
		text-align: center;
    	color: white;
    	font-size: 20px;
	}
	tr {
		transition: 100ms ease-out;
	}
	tr:hover {
		transform: scale(1.009);
		background: #17011e;
	}
	td a {
		display: block;
		width: 100%;
		height: 100%;
  		padding: 2px;
	}
    </style>
  </head>
  <body>
    <title>Session Chooser</title>
	 <div class="logo-container"> <a href="https://3forge.com"> <img src="portal/rsc/ami/menubar_logo3_color2.svg"> </a> </div>
     <div class="main-container">
	     <div class="inner-container">
     		<f1:ifNot test="${canAddSession}">
	 		   <center><span style='color:#FFFFAA;font-size:18px'><f1:out value="${messageMaxSessions}"/></span>
	 		</f1:ifNot>
	     	<p class="title">3forge Sessions</p>
		     <table>
		     	 <tr style="pointer-events:none;">
		     	 	<th><img src="portal/rsc/ami/session.svg" style="height:18px;"> Sessions</th>
		     	 	<th>Layout</th>
		     	 	<th>Label</th>
		     	 	<th></th>
		     	 </tr>
		     			
		          <f1:forEach items="${activePages}" var="page">
						<tr>
							<td class="session-name align-left">
		                		<f1:embed java="com.f1.suite.web.portal.impl.BasicPortletManager.onPageLoad(request);"/>
		                		<a style='color:white;text-decoration:none' href="<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL"/>?F1PGID=<f1:out value="${page..pgId}"/>"> 
		                			<f1:out value="${page..name}"/>
		                		</a>
		                	</td>
							
							<td class="align-left">
								<a style='color:white;text-decoration:none' href="<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL"/>?F1PGID=<f1:out value="${page..pgId}"/>"> 
								 	<f1:out value="${page..layout}"/> 
								</a>
							</td>
							
							<td class="align-left"> 
								<a style='color:white;text-decoration:none' href="<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.URL_PORTAL"/>?F1PGID=<f1:out value="${page..pgId}"/>"> 
									<f1:out value="${page..label}"/>
								</a>
							</td>
		                	
		                	<td>
		                		<center>
		                			<a style='color:white;text-decoration:none' href="<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.URL_END"/>?F1PGID=<f1:out value="${page..pgId}"/>">
		                				<img src="portal/rsc/ami/close.svg" style="height:18px;">
		                			</a>
		                		</center>
		                	</td>
		                <tr>
		          </f1:forEach>
		            
		          <f1:if test="${hasHeadlessPages}">
		            <tr style="pointer-events:none;">
		            	<th>
		            		<img src="portal/rsc/ami/headless-session.svg" style="height:18px;"> Headless&nbsp;Sessions&nbsp;Not&nbsp;Owned&nbsp;By&nbsp;Me
		            	</th>
		            	<th></th>
		            	<th></th>
		            	<th></th>
		            </tr>
		              <f1:forEach items="${headlessPages}" var="page">
		                 <tr>
		                 	<td>
		                 		<a style='color:white;text-decoration:none' href="<f1:out value="com.f1.ami.web.pages.AmiWebPages.URL_OWN_HEADLESS"/>?F1PGID=<f1:out value="${page..pgId}"/>">
		                 			<f1:out value="${page..name}"/>
		                 		</a>
		            		</td>
		                 	
		                 	<td> 
		                 		<a style='color:white;text-decoration:none' href="<f1:out value="com.f1.ami.web.pages.AmiWebPages.URL_OWN_HEADLESS"/>?F1PGID=<f1:out value="${page..pgId}"/>">
		      	           			<f1:out value="${page..layout}"/>
		      	           		</a>
		                 	</td>
		                 	
		                 	<td>
		                 		<a style='color:white;text-decoration:none' href="<f1:out value="com.f1.ami.web.pages.AmiWebPages.URL_OWN_HEADLESS"/>?F1PGID=<f1:out value="${page..pgId}"/>">
		                 			<f1:out value="${page..label}"/>
		                 		</a>	
		                 	</td>
		            		
		            		<td>
		                 			<f1:out value="${page..comment}"/>
		            			<!-- headless sessions don't have close buttons, keep the cell empty -->
		            		</td>
		            	</tr>
		            </f1:forEach>
		          </f1:if>
		     </table>
     		<br><br>
	 		   <center>
     		<f1:if test="${canAddSession}">
	 		   <a style='color:white;text-decoration:none;' href="<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.URL_START"/>?<f1:out value="com.f1.suite.web.portal.impl.BasicPortletManager.KEEP_EXISTING_OPEN"/>=true&LAYOUT=">
	 		   	<button>Start a new session</button>
	 		   </a>
	 		</f1:if>
	 		   &nbsp;&nbsp;<a style='color:white;text-decoration:none;' href="logout">
	 		   		<button>Logout (Ends all sessions)</button>
	 		   </a>
	     </div>
  <script>
    var modCount='<f1:out value="${pagesModCount}"/>';
    function ajax(){
      var r=new XMLHttpRequest(); 
      r.open("GET","modcount",true);
      r.responseType="text";
      r.onreadystatechange=function(o){ 
       if (r.readyState!=4) return;
       var res=r.response;
       if (res=="none" || res!=modCount)
         window.location.reload();
       else
         window.setTimeout( function() { ajax(); }, 1000);
       };
      r.setRequestHeader("Content-type","text/html"); 
      r.send();
    };
    window.setTimeout( function() { ajax(); }, 1000);
  </script>
</f1:ifNot>
  </body>

</html>

