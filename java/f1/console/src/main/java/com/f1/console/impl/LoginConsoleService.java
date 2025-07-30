package com.f1.console.impl;

import com.f1.base.Password;
import com.f1.console.ConsoleEvent;
import com.f1.console.ConsoleSession;
import com.f1.utils.EH;
import com.f1.utils.SH;

public class LoginConsoleService extends AbstractConsoleService {

	public LoginConsoleService() {
		super("LOGIN", "login +(.*)", "login with username, if you do not supply a password you will then be prompted for one. Usage: LOGIN username <password>");
	}

	@Override
	public void doRequest(ConsoleSession client, String[] cmdParts) {
		String cmd = cmdParts[1];
		String username = SH.trim(cmd);
		Password password;
		int split = cmd.indexOf(' ');
		if (split == -1)
			password = client.getConnection().promptForPassword("password: ");
		else {
			password = new Password(username.substring(split + 1));
			username = username.substring(0, split);
		}

		ConsoleAuthenticator auth = client.getManager().getAuthenticator();
		if (auth == null) {
			client.getConnection().comment("Authentication failed", ConsoleAuthenticatorResponse.toStringForStatus(ConsoleAuthenticatorResponse.STATUS_AUTH_SERVICE_NOT_STARTED));
		} else {
			ConsoleAuthenticatorResponse res = auth.authenticate("3FORGE_CLI", client.getConnection().getConnectionIdentifier(), username, password);
			if (res.getStatus() == ConsoleAuthenticatorResponse.STATUS_OKAY) {
				client.getConnection().comment("Logged in as", username);
				client.setUserLoggedin(res.getUsername(), res.getAttributes());
				BasicConsoleEvent event = new BasicConsoleEvent(ConsoleEvent.TYPE_LOGIN, EH.currentTimeMillis(), client.getConnection().getConnectionIdentifier(), null,
						client.getUsername());
				client.onEvent(event, true);
			} else {
				client.getConnection().comment("Authentication failed", ConsoleAuthenticatorResponse.toStringForStatus(res.getStatus()));
			}
		}
	}

	@Override
	public boolean availableWihtoutLogin() {
		return true;
	}

	@Override
	public boolean saveCommandToHistory() {
		return false;
	}

}
