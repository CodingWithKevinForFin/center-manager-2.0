package com.f1.ami.web;

import java.io.IOException;

import com.f1.ami.web.auth.AmiWebLoginHttpHandler;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.WebStatesManager;
import com.f1.utils.FastPrintStream;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiWebSSORequestHandler extends AbstractHttpHandler {
	private static final java.util.logging.Logger log = LH.get();
	private AmiWebSSOPlugin plugin;
	private String pluginDesc;
	private long retryMs;

	public AmiWebSSORequestHandler(AmiWebSSOPlugin plugin, long retryMs) {
		this.plugin = plugin;
		this.pluginDesc = plugin.getPluginId();
		this.retryMs = retryMs;
	}
	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		HttpSession session = req.getSession(true);
		WebStatesManager wsm = WebStatesManager.get(session);
		if (wsm == null || !wsm.isLoggedIn()) {
			String url;
			try {
				if (OH.ne("true", req.getParams().get("force"))) {
					if (session.getAttributes().putIfAbsent("AUTHSEMAPHORE", Boolean.TRUE) != null) {
						String contextPath = req.getContextPath();
						String contextPathForce = contextPath + (contextPath.indexOf('?') == -1 ? '?' : '&') + "force=true";
						FastPrintStream out = req.getOutputStream();
						out.println("<script>");
						out.println("  function retry(){window.location.href='" + contextPath + "';}");
						out.println("  var retryTimeout=setTimeout(retry," + retryMs + ");");
						out.println("  function force(){clearTimeout(retryTimeout);window.location.href='" + contextPathForce + "';}");
						out.println("</script>");
						out.println("<body style='font-family: arial, sans-serif;font-size: 25px; color: white;background:linear-gradient(45deg, #2C1E4A 0%, #06002E 100%)' >");
						out.println("<img src='/portal/rsc/ami/menubar_logo3_color2.svg' width=200>");
						out.println("&nbsp;<P>&nbsp;<P><Center>Waiting for you to authenticate from another open window<BR></span><P>");
						out.println("<span style='font-size:20;color: #b7b7b7'>Can't find the other authentication page?&nbsp;&nbsp;</span><a href='javascript:force()'>");
						out.println(
								"<button style='cursor:pointer;background:#5C6379;border:none;color:white;padding:5px 10px;border-radius:3px;font-weight: bold;'>Authenticate Here</button></a><BR>");
						return;
					}
				}
				url = plugin.buildAuthRequest(req);
			} catch (Exception e) {
				LH.warning(log, "Unexpected error building " + this.pluginDesc + " request", e);
				req.getOutputStream().print("Critical Error Building " + this.pluginDesc + " Request.");
				return;
			}
			req.sendRedirect(url);
		} else

		{
			AmiWebLoginHttpHandler.sendToDashboard(req);
		}
	}
}