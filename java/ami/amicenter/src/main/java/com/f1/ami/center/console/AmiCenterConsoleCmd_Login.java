package com.f1.ami.center.console;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.web.auth.AmiAuthManager;
import com.f1.ami.web.auth.AmiAuthResponse;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.ami.web.auth.BasicAmiAuthResponse;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class AmiCenterConsoleCmd_Login extends AmiCenterConsoleCmd {

	public AmiCenterConsoleCmd_Login() {
		super("login <username> *", "login with username which is used for authentication against AMIDB. ");
	}

	@Override
	public void process(AmiCenterConsoleClient client, String cmd, String[] cmdParts) {
		final String username = cmdParts[1];
		String password;
		if (cmdParts.length == 3)
			password = cmdParts[2];
		else
			password = client.promptForPassword("password: ");
		if ("on".equals(client.getLocalSetting("password_encrypt")))
			try {
				password = client.getManager().getState().decrypt(password);
			} catch (Exception e) {
				client.getOutputStream().append("Password is not properly encrypted (setlocal password_encrypt=off for plain text)\n");
				return;
			}
		AmiAuthenticatorPlugin auth = client.getManager().getAuthenticator();
		AmiAuthResponse res = auth.authenticate(AmiAuthenticatorPlugin.NAMESPACE_AMIDB_CLI, client.getRemoteLocation(), username, password);
		if (res.getStatus() == AmiAuthResponse.STATUS_OKAY) {
			if (!AmiAuthManager.INSTANCE.addUser(client.getRemoteLocation(), res.getUser().getUserName())) {
				client.getOutputStream().append("Authentication failed: " + BasicAmiAuthResponse.toStringForStatus(AmiAuthResponse.STATUS_USER_COUNT_EXCEEDED))
						.append(": " + AmiAuthManager.INSTANCE.getMaxUsers()).append(" max user(s)").append('\n');
				return;
			}
			Map<String, Object> sessionVariables = new HashMap<String, Object>();
			Map<String, Class> sessionVariableTypes = new HashMap<String, Class>();
			for (Entry<String, Object> e : res.getUser().getAuthAttributes().entrySet()) {
				if (e.getKey().startsWith("amiscript.db.variable.")) {
					final String name = SH.stripPrefix(e.getKey(), "amiscript.db.variable.", true);
					Tuple2<Class<?>, Object> val = AmiUtils.toAmiscriptVariable(e.getValue(), "User Attribute amiscript.db.variable.", name);
					sessionVariableTypes.put(name, val.getA());
					sessionVariables.put(name, val.getB());
				}
			}
			if (client.hasSession())
				client.getOutputStream().append("Session Reset. ");
			client.terminateSession();
			byte permissions = AmiCenterUtils.getPermissions(client.getTools(), res.getUser());
			String username2 = res.getUser().getUserName();
			client.setUsernamePassword(username2, password, permissions, sessionVariables, sessionVariableTypes);
			AmiCenterQueryDsRequest request = client.getTools().nw(AmiCenterQueryDsRequest.class);
			int timeout = client.getLocalSetting("timeout", Integer.class);
			int limit = client.getLocalSetting("limit", Integer.class);
			request.setLimit(limit);
			request.setTimeoutMs(timeout);
			request.setQuerySessionKeepAlive(true);
			request.setInvokedBy(client.getUsername());
			request.setSessionVariableTypes(client.getSessionVariableTypes());
			request.setSessionVariables(client.getSessionVariables());
			request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_CMDLINE);
			request.setPermissions(client.getPermissions());
			AmiCenterQueryDsResponse action = client.sendToAmiState(request, AmiCenterQueryDsResponse.class);
			if (action == null || !action.getOk()) {
				client.getOutputStream().append("Logged in failed for '").append(username2).append("'\n");
				return;
			}
			client.setQuerySessionId(action.getQuerySessionId());
			client.getOutputStream().append("Logged in as '").append(username2).append("' to SESSION-").append(SH.toString(action.getQuerySessionId())).append(" For ")
					.append(AmiCenterUtils.toStringForPermissions(permissions)).append('\n');
		} else {
			client.getOutputStream().append("Authentication failed: " + BasicAmiAuthResponse.toStringForStatus(res.getStatus())).append('\n');
		}
	}
	@Override
	public boolean verifyLocalSetting(String key, Object value, StringBuilder sink) {
		if ("password_encrypt".equals(key)) {
			if ("off".equalsIgnoreCase((String) value) || "on".equalsIgnoreCase((String) value))
				return true;
			sink.append("password_encrypt must by 'on' or 'off'");
			return false;
		}
		return super.verifyLocalSetting(key, value, sink);
	}

	@Override
	public void init(AmiCenterConsoleClient client) {
		client.addLocalSetting("password_encrypt", "off", String.class);
	}
}
