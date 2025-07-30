package com.f1.fixclientsim;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.Dictionary;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.UnsupportedMessageType;
import quickfix.field.AvgPx;
import quickfix.field.BeginString;
import quickfix.field.MsgType;

public class TestServer implements SessionFactory, Application, MessageFactory {

	public static void main(String a[]) throws ConfigError {
		new TestServer();
	}

	public TestServer() throws ConfigError {
		SessionSettings settings = new SessionSettings();
		settings.setString("ConnectionType", "acceptor");
		settings.setString("SocketAcceptPort", "9876");
		settings.setString("StartTime", "00:00:00");
		settings.setString("EndTime", "00:00:00");
		settings.setString("HeartBtInt", "30");
		settings.setString("ValidOrderTypes", "1,2,F");
		settings.setString("SenderCompID", "*");
		settings.setString("TargetCompID", "*");
		settings.setString("UseDataDictionary", "Y");
		settings.setString("DefaultMarketPrice", "15");

		SessionID sessionId = new SessionID("FIX.4.2", "TargetComp", "SenderComp");
		settings.setString(sessionId, "AcceptorTemplate", "Y");
		settings.setString(sessionId, "DataDictionary", "FIX42.xml");
		settings.setString(sessionId, "BeginString", "FIX.4.2");
		settings.set(new Dictionary());
		LogFactory logFactory = new FileLogFactory(settings);
		SocketAcceptor acceptor = new SocketAcceptor(this, new FileStoreFactory(settings), settings, logFactory, this);
		acceptor.start();
		Session session = Session.lookupSession(sessionId);
		Message message = new Message();
		message.setString(35, "D");
		message.setString(AvgPx.FIELD, "1234.432");
		System.out.println("done init");
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

	@Override
	public Session create(SessionID sessionID, SessionSettings settings) throws ConfigError {
		return null;
	}

	@Override
	public Message create(String beginString, String msgType) {
		Message r = new Message();
		r.setString(BeginString.FIELD, "FIX.4.2");
		r.setString(MsgType.FIELD, msgType);
		return r;
	}

	@Override
	public Group create(String beginString, String msgType, int correspondingFieldID) {
		return null;
	}

}
