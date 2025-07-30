package com.f1.vortexglass;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Action;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.MapInMap;
import com.sso.messages.LoginSsoUserRequest;
import com.sso.messages.LoginSsoUserResponse;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUser;

public class DemoSsoProcessor extends BasicProcessor<Action, State> {

	public DemoSsoProcessor() {
		super(Action.class, State.class);
		bindToPartition("DEMOSSO");
	}

	private Map<String, String> userPasswords = new HashMap<String, String>();
	private MapInMap<String, String, String> userSettings = new MapInMap<String, String, String>();
	public void init() {
		File path = getTools().getOptional("users.access.file", new File("data/access.txt"));
		if (!path.isFile()) {
			System.err.println("You are running in demo mode (without an sso database). ");
			System.err.println("1) Create a file at: " + IOH.getFullPath(path));
			System.err.println("2) populate the file with one line per user with the syntax: user|password");
			System.err.println("Exiting...");
			EH.systemExit(1);
		} else {
			try {
				for (String line : SH.splitLines(IOH.readText(path))) {
					if (SH.isnt(line))
						continue;
					String[] parts = SH.split('|', line);
					final String userName = parts[0];
					final String password = parts[1];
					if (SH.isnt(userName) || SH.isnt(password))
						throw new RuntimeException("invalid syntax: " + line);
					CH.putOrThrow(userPasswords, userName, password);
					for (int i = 2; i < parts.length; i++) {
						if (SH.isnt(parts[i]))
							continue;
						String key = SH.beforeFirst(parts[i], '=');
						String val = SH.afterFirst(parts[i], '=');
						if (SH.isnt(val) || SH.isnt(key))
							throw new RuntimeException("invalid syntax: " + line + "   (bad key value pair: " + parts[i] + ")");
						this.userSettings.putMulti(userName, key, val);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException("Error processing file: " + IOH.getFullPath(path), e);
			}
		}
		super.init();
	}

	@Override
	public void processAction(Action action, State state, ThreadScope threadScope) throws Exception {
		RequestMessage req = (RequestMessage) action;
		Action action2 = req.getAction();
		if (action2 instanceof LoginSsoUserRequest) {
			LoginSsoUserRequest action3 = (LoginSsoUserRequest) action2;
			LoginSsoUserResponse res = nw(LoginSsoUserResponse.class);
			//res.setMessage("no luck chuck");
			SsoUser user = nw(SsoUser.class);
			String userName = action3.getUserName();
			String expectedPassword = userPasswords.get(userName);
			user.setUserName(userName);
			String password = action3.getPassword();
			if (expectedPassword == null) {
				res.setStatus(LoginSsoUserResponse.STATUS_USER_NOT_FOUND);
			} else if (OH.ne(expectedPassword, password)) {
				res.setStatus(LoginSsoUserResponse.STATUS_PASSWORD_INVALID);
			} else {
				res.setStatus(LoginSsoUserResponse.STATUS_OK);
				res.setUser(user);
				res.setOk(true);
				Map<String, SsoGroupAttribute> attributes = new HashMap<String, SsoGroupAttribute>();
				Map<String, String> settings = this.userSettings.get(userName);
				if (settings != null) {
					for (Entry<String, String> e : settings.entrySet()) {
						SsoGroupAttribute att = nw(SsoGroupAttribute.class);
						att.setKey(e.getKey());
						att.setValue(e.getValue());
						att.setType(SsoGroupAttribute.TYPE_TEXT);
						attributes.put(e.getKey(), att);
					}
				}
				res.setGroupAttributes(attributes);
			}
			ResultMessage<Action> result = nw(ResultMessage.class);
			result.setAction(res);
			reply(req, result, threadScope);
		}
	}

}
