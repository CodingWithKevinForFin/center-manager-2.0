package com.f1.ami.amicommon.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.container.ContainerTools;
import com.f1.utils.ContentType;
import com.f1.utils.PropertyController;

public class AmiRestPlugin_Whoami implements AmiRestPlugin {

	private ContainerTools tools;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;

	}

	@Override
	public String getPluginId() {
		return "REST_WHOAMI";
	}

	@Override
	public String getEndpoint() {
		return "whoami";
	}

	@Override
	public void handler(AmiRestRequest rr, AmiAuthUser user) {
		if (rr.isDisplayText()) {
			rr.println("Username: " + user.getUserName());
			rr.println("|");
			for (Entry<String, Object> i : user.getAuthAttributes().entrySet()) {
				rr.println("+--> " + i.getKey() + "=" + i.getValue());
			}
			rr.setContentType(ContentType.TEXT);
		} else {
			Map m = new HashMap<String, Object>();
			m.put("username", user.getUserName());
			m.put("attributes", user.getAuthAttributes());
			rr.printJson(m);
		}
	}

	@Override
	public boolean requiresAuth() {
		return true;
	}

}
