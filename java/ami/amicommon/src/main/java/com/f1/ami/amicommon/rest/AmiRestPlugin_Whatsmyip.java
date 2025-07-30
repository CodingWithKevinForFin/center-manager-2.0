package com.f1.ami.amicommon.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.container.ContainerTools;
import com.f1.http.HttpRequestResponse;
import com.f1.utils.CH;
import com.f1.utils.ContentType;
import com.f1.utils.PropertyController;

public class AmiRestPlugin_Whatsmyip implements AmiRestPlugin {

	private ContainerTools tools;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;

	}

	@Override
	public String getPluginId() {
		return "REST_WHATSMYIP";
	}

	@Override
	public String getEndpoint() {
		return "whatsmyip";
	}

	@Override
	public void handler(AmiRestRequest rr, AmiAuthUser user) {
		HttpRequestResponse req = rr.getInnerRequest();
		req.getRemoteHost();
		req.getRemotePort();
		Map<String, String> header = new HashMap<String, String>(req.getHeader());
		boolean authRedacted = header.remove("Authorization") != null;

		if (rr.isDisplayText()) {
			rr.println("IP Address: " + req.getRemoteHost());
			rr.println("      Port: " + req.getRemotePort());
			rr.println("");
			rr.println(" Http Request Headers:");
			for (Entry<String, String> entry : req.getHeader().entrySet())
				rr.println("    " + entry.getKey() + "=" + entry.getValue());
			rr.setContentType(ContentType.TEXT);
			if (authRedacted) {
				rr.println("");
				rr.println(" Http Request Headers:");
				rr.println("    Authorization");
			}
		} else {
			Map<Object, Object> m = CH.m("ip", req.getRemoteHost(), "port", req.getPort(), "headers", header);
			if (authRedacted)
				m.put("redactedHeaders", CH.l("Authorization"));
			rr.printJson(m);
		}
	}

	@Override
	public boolean requiresAuth() {
		return false;
	}
}
