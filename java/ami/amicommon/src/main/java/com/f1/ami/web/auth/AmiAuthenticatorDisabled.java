package com.f1.ami.web.auth;

import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiAuthenticatorDisabled implements AmiAuthenticatorPlugin {

	public static final AmiAuthenticatorDisabled INSTANCE = new AmiAuthenticatorDisabled();

	private AmiAuthenticatorDisabled() {
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public String getPluginId() {
		return "AUTH_DISABLED";
	}

	@Override
	public AmiAuthResponse authenticate(String namespace, String location, String user, String password) {
		return new BasicAmiAuthResponse(AmiAuthResponse.STATUS_SERVICE_DISABLED, null, null);
	}

}
