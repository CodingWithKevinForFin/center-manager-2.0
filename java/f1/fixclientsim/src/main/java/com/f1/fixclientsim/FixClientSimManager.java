package com.f1.fixclientsim;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageStore;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;

import com.f1.qfix.QuickFixSpeedLoggerManager;
import com.f1.speedlogger.impl.SpeedLoggerInstance;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;

public class FixClientSimManager implements Application {

	private static final Logger log = Logger.getLogger(FixClientSimManager.class.getName());
	final private SessionSettings settings;
	final private LogFactory logFactory;
	final private Map<SessionID, FixClientSimSession> sessions = new HashMap<SessionID, FixClientSimSession>();
	private long uid = 0;

	public FixClientSimManager(File fileStorePath, int messageLevel, int eventLevel, int errorLevel) {
		settings = new SessionSettings();
		settings.setString("FileStorePath", IOH.getFullPath(fileStorePath));
		settings.setString("ConnectionType", "initiator");
		settings.setString("BeginString", "FIX.4.2");
		settings.setString("HeartBtInt", "30");
		settings.setString("StartTime", "00:00:00");
		settings.setString("EndTime", "00:00:00");
		settings.setString("ValidateUserDefinedFields", "N");
		settings.setString("UseDataDictionary", "N");
		logFactory = new QuickFixSpeedLoggerManager(SpeedLoggerInstance.getInstance(), messageLevel, eventLevel, errorLevel);
	}

	public FixClientSimSession createSession(String host, int port, String beginString, String senderCompId, String targetCompId, int senderSeqNum, int targetSeqNum)
			throws Exception {
		SessionID sessionId = new SessionID(beginString, senderCompId, targetCompId);
		if (sessions.containsKey(sessionId))
			throw new RuntimeException("Session already exists: " + sessionId);
		DefaultMessageFactory messageFactory = new DefaultMessageFactory();
		FileStoreFactory fsf = new FileStoreFactory(settings);
		MessageStore messageStore = fsf.create(sessionId);
		if (senderSeqNum != -1)
			messageStore.setNextSenderMsgSeqNum(senderSeqNum);
		if (targetSeqNum != -1)
			messageStore.setNextTargetMsgSeqNum(targetSeqNum);
		SocketInitiator initiator = new SocketInitiator(this, fsf, settings, logFactory, messageFactory);
		initiator.getSettings().setString(sessionId, Initiator.SETTING_SOCKET_CONNECT_HOST, host);
		initiator.getSettings().setString(sessionId, "SocketConnectHost", host);
		initiator.getSettings().setString(sessionId, Initiator.SETTING_SOCKET_CONNECT_PORT, Integer.toString(port));
		initiator.getSettings().setString(sessionId, Session.SETTING_HEARTBTINT, "30");
		initiator.getSettings().setString(sessionId, Session.SETTING_PERSIST_MESSAGES, "N");
		initiator.start();

		Session session = Session.lookupSession(sessionId);

		if (session == null)
			throw new RuntimeException("session could not be created at: " + host + ":" + port + " for sessson: " + sessionId);
		FixClientSimSession r = new FixClientSimSession(uid++, initiator, session);
		sessions.put(sessionId, r);
		return r;
	}

	@Override
	public void onCreate(SessionID sessionId) {
		log.info("on create: " + sessionId);
	}

	@Override
	public void onLogon(SessionID sessionId) {
		final FixClientSimSession session = sessions.get(sessionId);
		if (session == null)
			LH.info(log, "message for unknown session: ", sessionId);
		else
			session.fireOnLogon();
	}

	@Override
	public void onLogout(SessionID sessionId) {
		final FixClientSimSession session = sessions.get(sessionId);
		if (session == null)
			LH.info(log, "message for unknown session: ", sessionId);
		else
			session.fireOnLogout();
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId) {
	}

	@Override
	public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		final FixClientSimSession session = sessions.get(sessionId);
		if (session == null)
			LH.info(log, "message for unknown session: ", sessionId, "  message: ", message);
		else
			session.fireOnAdminMessage(message);
	}

	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
	}

	@Override
	public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		final FixClientSimSession session = sessions.get(sessionId);
		if (session == null)
			LH.info(log, "message for unknown session: ", sessionId, "  message: ", message);
		else
			session.fireOnMessage(message);
	}

	public void closeSession(FixClientSimSession session) throws Exception {
		CH.removeOrThrow(sessions, session.getSessionId());
		session.close();
	}

	public Collection<FixClientSimSession> getSessions() {
		return sessions.values();
	}

	public FixClientSimSession getSessionByUid(long uid) {
		for (FixClientSimSession session : sessions.values()) {
			if (session.getUid() == uid)
				return session;
		}
		return null;
	}

}

