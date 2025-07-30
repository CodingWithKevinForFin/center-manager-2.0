package com.f1.fixclientsim;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DefaultSessionFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;

import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.utils.CH;

public class TestClient implements Application {

	public static void main(String a[]) throws Exception {
		new TestClient();
	}

	private SessionSettings settings;
	private FileLogFactory logFactory;
	private FileStoreFactory messageStoreFactory;
	private DefaultSessionFactory sessionFactory;

	public TestClient() throws Exception {

		FixClientSimManager manager = new FixClientSimManager(new File("c:/test/qf/client/store"), SpeedLoggerLevels.INFO, SpeedLoggerLevels.INFO,
				SpeedLoggerLevels.INFO);
		FixClientSimSession session = manager.createSession("localhost", 9878, "FIX.4.2", "BANZAI", "EXEC", -1, -1);
		session.sendMessage42("D", (Map) CH.m(1, "rob", 55, "MSFT", 21, "1", 54, "1", 40, "1", 11, "Ord1", 60, "20111010-00:00:00.000", 38, "2000"));
		Thread.sleep(10000);
	}

	public Session startSession(String host, int port, String beginString, String senderCompId, String targetCompId, int senderSeqNum, int targetSeqNum)
			throws ConfigError, IOException {

		SessionID sessionId = new SessionID(beginString, senderCompId, targetCompId);
		SocketInitiator initiator = new SocketInitiator(this, new FileStoreFactory(settings), settings, logFactory, new DefaultMessageFactory());
		initiator.getSettings().setString(sessionId, "SocketConnectHost", host);
		initiator.getSettings().setString(sessionId, "SocketConnectPort", Integer.toString(port));
		initiator.getSettings().setString(sessionId, "StartTime", "00:00:00");
		initiator.getSettings().setString(sessionId, "EndTime", "00:00:00");
		initiator.getSettings().setString(sessionId, "HeartBtInt", "30");

		initiator.start();
		Session session = Session.lookupSession(sessionId);
		if (senderSeqNum != -1)
			session.getStore().setNextSenderMsgSeqNum(senderSeqNum);
		if (targetSeqNum != -1)
			session.getStore().setNextSenderMsgSeqNum(targetSeqNum);
		if (session == null)
			throw new RuntimeException("session could not be created at " + host + ":" + port + " for sessson: " + sessionId);
		return session;
	}

	@Override
	public void onCreate(SessionID sessionId) {
	}

	@Override
	public void onLogon(SessionID sessionId) {
	}

	@Override
	public void onLogout(SessionID sessionId) {
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId) {
	}

	@Override
	public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
	}

	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
	}

	@Override
	public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
	}

}
