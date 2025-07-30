package com.f1.suite.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.container.Partition;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.FastPrintStream;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class StartPortalHttpHandler extends AbstractHttpHandler {

	final private PortletManagerFactory portletManagerFactory;
	final private HttpStateCreator stateCreator;
	private static final Logger log = LH.get();
	private String logoutUrl;
	final private String maxSessionsReachedUrl;

	public StartPortalHttpHandler(HttpStateCreator stateCreator, PortletManagerFactory portletManagerFactory, String maxSessionsReachedUrl) {
		this.stateCreator = stateCreator;
		this.portletManagerFactory = portletManagerFactory;
		this.maxSessionsReachedUrl = maxSessionsReachedUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}
	public void setLogoutUrl(String url) {
		this.logoutUrl = url;
	}
	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		super.handle(request);
		HttpSession session = request.getSession(false);
		WebStatesManager wsm = WebStatesManager.get(session);
		if (wsm == null || !wsm.isLoggedIn()) {
			request.sendRedirect("/");
			return;
		}
		if (!wsm.canAddSession()) {
			request.sendRedirect(maxSessionsReachedUrl);
			return;
		}
		String pgid = this.stateCreator.nextPgId();
		Partition partition = portletManagerFactory.getTools().getContainer().getPartitionController().getOrCreatePartition(pgid);
		WebState state = this.stateCreator.createState(request, partition, wsm, pgid);
		BasicPortletManager bpm;
		if (partition.lockForWrite(stateCreator.getAcquireLockTimeoutSeconds(), TimeUnit.SECONDS))
			try {
				bpm = this.portletManagerFactory.createPortletManager(request, state);
			} catch (RuntimeException e) {
				if (getLogoutUrl() == null)
					throw e;
				request.sendRedirect(getLogoutUrl());
				LH.warning(log, "Redirecting to logout url ", getLogoutUrl(), " for user ", state.getUserName(), e);
				return;
			} finally {
				partition.unlockForWrite();
			}
		else {
			request.setResponseType(HttpRequestResponse.HTTP_500_SERVICE_ERROR);
			LH.info(log, "Session timeout (", stateCreator.getAcquireLockTimeoutSeconds(), " seconds): ", session.getSessionId());
			return;
		}
		FastPrintStream out = request.getOutputStream();
		HashMap<String, String> params = new HashMap<String, String>(request.getParams());
		String keepExistingOpen = params.remove(BasicPortletManager.KEEP_EXISTING_OPEN);
		params.remove(BasicPortletManager.PRELOGINID);
		out.println("<script>");
		if (OH.ne("true", keepExistingOpen)) {
			out.println("var oldpgid=sessionStorage.getItem('" + BasicPortletManager.PAGEID + "');");
			out.println("if(oldpgid!=null){");
			out.println("  var ajax=new XMLHttpRequest();");
			out.println("  ajax.open('GET','" + BasicPortletManager.URL_END + "?" + BasicPortletManager.PAGEID + "='+oldpgid,true);");
			out.println("  ajax.setRequestHeader('Content-type','text/html'); ");
			out.println("  ajax.send();");
			out.println("}");
		}
		out.println("sessionStorage.setItem('" + BasicPortletManager.PAGEID + "','" + pgid + "');");
		String paramsText = HttpUtils.getParamsAsString(params);
		if (SH.is(paramsText))
			out.println("window.location='" + BasicPortletManager.URL_PORTAL + "?" + BasicPortletManager.PAGEID + "=" + state.getPgId() + "&" + paramsText + "';");
		else {
			String urlParams = bpm.buildUrlParams(false);
			out.println("window.location='" + BasicPortletManager.URL_PORTAL + "?" + BasicPortletManager.PAGEID + "=" + state.getPgId() + urlParams + "';");
		}
		out.println("</script>");
	}

}
