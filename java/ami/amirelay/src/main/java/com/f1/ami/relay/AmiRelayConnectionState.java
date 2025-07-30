package com.f1.ami.relay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.ami.amicommon.msg.AmiRelayConnectionMessage;
import com.f1.ami.amicommon.msg.AmiRelayLoginMessage;
import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.ami.amicommon.msg.AmiRelayStatusMessage;
import com.f1.ami.relay.fh.AmiFH;

public class AmiRelayConnectionState {

	private AmiRelayLoginMessage login;
	private AmiRelayConnectionMessage connectionMessage;

	private Map<String, AmiRelayCommandDefMessage> commands = new HashMap<String, AmiRelayCommandDefMessage>();
	private AmiRelayState state;
	final private AmiFH connection;

	public AmiRelayConnectionState(AmiRelayState state, AmiRelayConnectionMessage connectionMessage) {
		this.state = state;
		this.connectionMessage = connectionMessage;
		this.connection = state.getAmiServer().getSession(getConnectionId());
	}

	public void addStatus(AmiRelayStatusMessage status) {
	}

	public void addCommand(AmiRelayCommandDefMessage command) {
		commands.put(command.getCommandId(), command);
	}

	public AmiRelayLoginMessage getLogin() {
		return login;
	}

	public void setLogin(AmiRelayLoginMessage login) {
		this.login = login;
	}

	public AmiRelayConnectionMessage getConnects() {
		return connectionMessage;
	}

	public int getConnectionId() {
		return connectionMessage.getConnectionId();
	}

	public void drainEvent(List<AmiRelayMessage> amiEvents) {
		amiEvents.add(connectionMessage);
		if (login != null)
			amiEvents.add(login);
		for (AmiRelayCommandDefMessage i : commands.values())
			amiEvents.add(i);
	}

	public AmiFH getConnection() {
		return connection;
	}
}
