package com.f1.ami.amicommon.rest;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.web.auth.AmiAuthUser;

public interface AmiRestPlugin extends AmiPlugin {

	public String getEndpoint();
	public void handler(AmiRestRequest rr, AmiAuthUser user);
	public boolean requiresAuth();

}
