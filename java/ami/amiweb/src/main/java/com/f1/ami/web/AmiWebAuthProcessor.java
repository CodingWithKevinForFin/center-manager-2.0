package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiWebLoginRequest;
import com.f1.ami.amicommon.msg.AmiWebLoginResponse;
import com.f1.ami.web.auth.AmiAuthManager;
import com.f1.ami.web.auth.AmiAuthResponse;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.ami.web.auth.BasicAmiAuthResponse;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiWebAuthProcessor extends BasicRequestProcessor<AmiWebLoginRequest, State, AmiWebLoginResponse> {

	public AmiWebAuthProcessor() {
		super(AmiWebLoginRequest.class, State.class, AmiWebLoginResponse.class);
		bindToPartition("USERLOGIN");
	}

	private AmiAuthenticatorPlugin authenticator;
	//	private String namespace;

	public void init() {
		super.init();
		StringBuilder errorSink = new StringBuilder();
		this.authenticator = AmiUtils.loadAuthenticatorPlugin(getTools(), AmiWebProperties.PROPERTY_AMI_WEB_AUTH_PLUGIN_CLASS, "Ami Web Authenticator Plugin");
	}
	@Override
	protected AmiWebLoginResponse processRequest(RequestMessage<AmiWebLoginRequest> req, State state, ThreadScope threadScope) throws Exception {
		AmiWebLoginRequest action3 = req.getAction();
		AmiWebLoginResponse res = nw(AmiWebLoginResponse.class);
		String userName = action3.getUserName();
		String password = action3.getPassword() == null ? null : action3.getPassword().getPasswordString();
		String loc = action3.getClientLocation();
		String agent = action3.getClientAgent();
		String pwdObv = SH.password(password);

		AmiAuthResponse aar = null;
		if (SH.isnt(userName)) {
			res.setStatus(AmiAuthResponse.STATUS_BAD_USERNAME);
			res.setMessage("Username required");
			return res;
		}
		try {
			aar = authenticator.authenticate(AmiAuthenticatorPlugin.NAMESPACE_AMIWEB_GUI, loc, userName, password);
		} catch (Throwable t) {
			LH.severe(log, "Authenticator for (", userName, "@", loc, ",", agent, ",", pwdObv, "): AUTHENTICATOR_ERROR", t);
			res.setStatus(AmiAuthResponse.STATUS_GENERAL_ERROR);
			res.setMessage("Custom Authenticator threw error");
		}
		if (aar == null) {
			LH.severe(log, "Authenticator for (", userName, "@", loc, ",", pwdObv, "): AUTHENTICATOR_ERROR");
			res.setStatus(AmiAuthResponse.STATUS_GENERAL_ERROR);
			res.setMessage("Authenticator returned null");
			return res;
		}
		switch (aar.getStatus()) {
			case AmiAuthResponse.STATUS_GENERAL_ERROR:
			case AmiAuthResponse.STATUS_ACCOUNT_LOCKED:
			case AmiAuthResponse.STATUS_BAD_PASSWORD:
			case AmiAuthResponse.STATUS_BAD_USERNAME:
			case AmiAuthResponse.STATUS_USER_COUNT_EXCEEDED:
			case AmiAuthResponse.STATUS_BAD_CREDENTIALS:
			case AmiAuthResponse.STATUS_SERVICE_DISABLED:
				LH.info(log, "Authenticator for (", userName, "@", loc, ",", agent, ",", pwdObv, "): ", BasicAmiAuthResponse.toStringForStatus(aar.getStatus()));
				res.setMessage(aar.getMessage());
				res.setStatus(aar.getStatus());
				return res;
			case AmiAuthResponse.STATUS_OKAY: {
				if (!AmiAuthManager.INSTANCE.addUser(loc, userName)) {
					res.setStatus(AmiAuthResponse.STATUS_USER_COUNT_EXCEEDED);
					LH.info(log, "Authenticator for (", userName, "@", loc, ",", agent, ",", pwdObv, "): USER_COUNT_EXCEEDED");
				} else {
					LH.info(log, "Authenticator for (", userName, "@", loc, ",", agent, ",", pwdObv, "): OKAY");
					AmiAuthUser u = aar.getUser();
					res.setUsername(u.getUserName());
					Map<String, Object> attributes = new HashMap<String, Object>(u.getAuthAttributes());
					res.setAttributes(attributes);
					res.setStatus(AmiAuthResponse.STATUS_OKAY);
				}
				break;
			}
			default: {
				LH.severe(log, "Authenticator for (", userName, "@", loc, ",", pwdObv, "): AUTHENTICATOR_ERROR");
				res.setStatus(AmiAuthResponse.STATUS_GENERAL_ERROR);
				res.setMessage("Authenticator returned bad status");
			}
		}
		return res;
	}
}
