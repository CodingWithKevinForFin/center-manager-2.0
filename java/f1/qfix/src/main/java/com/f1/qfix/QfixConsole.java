package com.f1.qfix;

import java.io.IOException;
import java.util.Map;

import quickfix.Session;
import quickfix.SessionID;

import com.f1.base.Console;
import com.f1.base.Table;
import com.f1.msg.impl.MsgConsole;
import com.f1.qfix.msg.FixMsgConnection;
import com.f1.utils.structs.table.BasicTable;

@Console(help = "FIX Status and Session Administration")
public class QfixConsole extends MsgConsole {
	private FixMsgConnection connection;

	public QfixConsole(FixMsgConnection connection) {
		super(connection);
		this.connection = connection;
	}

	@Console(help = "Show a summary of all FIX sessions in the system")
	public Table showSessions() {
		final Table t = new BasicTable(String.class, "SessionName", Object.class, "SessionID", int.class, "NextIncomingSeqNo", int.class, "NextOutgoingSeqNo", boolean.class,
				"Connected");
		Map<String, SessionID> sessions = connection.getSessions();
		for (Map.Entry<String, SessionID> sessionEntry : sessions.entrySet()) {
			Session mySession = Session.lookupSession(sessionEntry.getValue());
			t.getRows().addRow(sessionEntry.getKey(), sessionEntry.getValue(), mySession.getExpectedTargetNum(), mySession.getExpectedSenderNum(), mySession.isLoggedOn());
		}
		return t;
	}

	@Console(help = "Set next outgoing seq number for session", params = { "sessionName", "seqNum" })
	public String setNextOutgoing(String sessionName, int seqNum) throws IOException {
		Map<String, SessionID> sessions = connection.getSessions();
		SessionID sessionID = sessions.get(sessionName);
		if (sessionID == null)
			return "Session not found : " + sessionName;
		Session session = Session.lookupSession(sessionID);
		if (session == null)
			return "Error lookup up session : " + sessionID;
		session.setNextSenderMsgSeqNum(seqNum);
		return "Session " + sessionName + " Sequence Outgoing Number Set to " + seqNum;

	}

	@Console(help = "Set next incoming seq number for session", params = { "sessionName", "seqNum" })
	public String setNextIncoming(String sessionName, int seqNum) throws IOException {
		Map<String, SessionID> sessions = connection.getSessions();
		SessionID sessionID = sessions.get(sessionName);
		if (sessionID == null)
			return "Session not found : " + sessionName;
		Session session = Session.lookupSession(sessionID);
		if (session == null)
			return "Error lookup up session : " + sessionID;
		session.setNextTargetMsgSeqNum(seqNum);
		return "Session " + sessionName + " Sequence Incoming Number Set to " + seqNum;

	}

	@Console(help = "Disconnect and Reset Session", params = { "sessionName" })
	public String resetSession(String sessionName) throws IOException {
		Map<String, SessionID> sessions = connection.getSessions();
		SessionID sessionID = sessions.get(sessionName);
		if (sessionID == null)
			return "Session not found : " + sessionName;
		Session session = Session.lookupSession(sessionID);
		if (session == null)
			return "Error lookup up session : " + sessionID;
		if (session.isLoggedOn())
			session.disconnect(sessionName, false);
		session.setNextSenderMsgSeqNum(1);
		session.setNextTargetMsgSeqNum(1);
		return "Session " + sessionName + " Reset to 1,1";

	}

	@Console(help = "Connect Session", params = { "sessionName" })
	public String connect(String sessionName) {
		Map<String, SessionID> sessions = connection.getSessions();
		SessionID sessionID = sessions.get(sessionName);
		if (sessionID == null)
			return "Session not found : " + sessionName;
		Session session = Session.lookupSession(sessionID);
		if (session == null)
			return "Error lookup up session : " + sessionID;
		session.logon();
		return "Session " + sessionName + " Logon Initiated";
	}

	@Console(help = "Logout Session", params = { "sessionName" })
	public String logout(String sessionName) {
		Map<String, SessionID> sessions = connection.getSessions();
		SessionID sessionID = sessions.get(sessionName);
		if (sessionID == null)
			return "Session not found : " + sessionName;
		Session session = Session.lookupSession(sessionID);
		if (session == null)
			return "Error lookup up session : " + sessionID;
		session.logout();
		return "Session " + sessionName + " Logout Initiated";
	}

	@Console(help = "Force Disconnect Session", params = { "sessionName" })
	public String disconnect(String sessionName) throws IOException {
		Map<String, SessionID> sessions = connection.getSessions();
		SessionID sessionID = sessions.get(sessionName);
		if (sessionID == null)
			return "Session not found : " + sessionName;
		Session session = Session.lookupSession(sessionID);
		if (session == null)
			return "Error lookup up session : " + sessionID;
		session.disconnect(sessionName, false);
		return "Session " + sessionName + " Disconnect Initiated";
	}

}
