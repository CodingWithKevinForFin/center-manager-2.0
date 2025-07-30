package com.f1.qfix.msg;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.msg.MsgConnectionExternalInterfaces;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopicConfiguration;
import com.f1.msg.impl.AbstractMsgConnection;
import com.f1.msg.impl.BasicMsgTopicConfiguration;
import com.f1.qfix.FixEvent;
import com.f1.qfix.QuickFixSpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.impl.SpeedLoggerInstance;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.Dictionary;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.RuntimeError;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

public class FixMsgConnection extends AbstractMsgConnection implements Application {
	private Logger log = LH.get();
	SessionSettings settings;
	MessageStoreFactory storeFactory;
	LogFactory logFactory;
	MessageFactory messageFactory;
	Acceptor acceptor;
	Initiator connector;

	public FixMsgConnection(FixMsgConnectionConfiguration config) throws FileNotFoundException, ConfigError, FieldConvertError {
		super(config);
		//Our new Settings List
		settings = new SessionSettings();

		//sessionList will be an array of strings from qfix.sessions field in the properties files
		//the purpose of this next stanza is to effectively mimic the processing of the qfix.config file using the f1.properties files.
		String[] sessionList = SH.split(',', config.getConfig().getRequired("sessions"));
		//For each session process their properties....feel free to add more just be careful to make sure you have the correct type
		for (String sessionName : sessionList) {
			Dictionary dict = new Dictionary();
			PropertyController props = config.getConfig().getSubPropertyController(sessionName + ".");
			for (String key : props.getKeys()) {
				if ("SocketConnectPort".equals(key) || "SocketAcceptPort".equals(key))
					continue;
				dict.setString(key, props.getRequired(key));
			}
			//Create the new sessionID...don't send in a session name unless the session is an initiator.
			SessionID sessID = new SessionID(new BeginString(props.getRequired("BeginString")), new SenderCompID(props.getRequired("SenderCompID")),
					new TargetCompID(props.getRequired("TargetCompID")));

			for (String key : props.getKeys())
				settings.setString(sessID, key, props.getRequired(key));

		}

		for (final Iterator<SessionID> i = settings.sectionIterator(); i.hasNext();) {
			final String name = i.next().getTargetCompID();
			addTopic(new BasicMsgTopicConfiguration(name, name));

		}
		storeFactory = new FileStoreFactory(settings);
		logFactory = new QuickFixSpeedLoggerManager(SpeedLoggerInstance.getInstance(), SpeedLoggerLevels.INFO, SpeedLoggerLevels.INFO, SpeedLoggerLevels.INFO);
		messageFactory = new DefaultMessageFactory();
	}
	@Override
	public Iterable<MsgConnectionExternalInterfaces> getExternalInterfaces() {
		return null;
	}

	@Override
	protected MsgInputTopic newInputTopic(MsgTopicConfiguration config, String topicSuffix) {
		if (topicSuffix != null)
			throw new UnsupportedOperationException("topic suffix not supported for fix");
		return new FixMsgInputTopic(this, config);
	}

	@Override
	protected MsgOutputTopic newOutputTopic(MsgTopicConfiguration config, String topicSuffix) {
		if (topicSuffix != null)
			throw new UnsupportedOperationException("topic suffix not supported for fix");
		return new FixMsgOutputTopic(this, config);
	}

	private ObjectGeneratorForClass<FixEvent> generator;

	public static final Map<String, SessionID> sessions = new ConcurrentHashMap<String, SessionID>();

	@Override
	public void init() {
		super.init();
		try {
			try {
				acceptor = new SocketAcceptor(this, storeFactory, settings, logFactory, messageFactory);
			} catch (ConfigError e) {
			}
			try {
				connector = new SocketInitiator(this, storeFactory, settings, logFactory, messageFactory);
			} catch (ConfigError e) {

			}
			if (acceptor == null && connector == null) {
				throw new RuntimeException("No sessions configured for QuickFix...");
			}
			if (acceptor != null)
				acceptor.start();
			if (connector != null)
				connector.start();
		} catch (RuntimeError e) {
			log.log(Level.WARNING, "Error with quick fix connector", e);
		} catch (ConfigError e) {
			log.log(Level.WARNING, "Config Error with quick fix connector", e);
		}
	}
	//TODO: needs to be called
	public void stop() {
		try {
			acceptor.stop();
		} catch (RuntimeError e) {
			log.log(Level.WARNING, "Config Error with quick fix connector shutdown", e);
		}
	}

	@Override
	public void fromAdmin(Message arg0, SessionID arg1) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
	}

	@Override
	public void fromApp(Message arg0, SessionID arg1) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		FixMsgInputTopic input = (FixMsgInputTopic) getInputTopic(arg1.getTargetCompID());
		input.fire(new FixMsgEvent(arg0, arg1.getTargetCompID()));
	}
	@Override
	public void onCreate(SessionID arg0) {
		sessions.put(arg0.getTargetCompID(), arg0);
	}

	@Override
	public void onLogon(SessionID arg0) {
	}

	@Override
	public void onLogout(SessionID arg0) {
	}

	@Override
	public void toAdmin(Message arg0, SessionID arg1) {
	}

	@Override
	public void toApp(Message arg0, SessionID arg1) throws DoNotSend {
	}

	public static Map<String, SessionID> getSessions() {
		return sessions;
	}

	@Override
	public FixMsgOutputTopic getOutputTopic(String topicName) {
		return (FixMsgOutputTopic) getOutputTopic(topicName, null);
	}

	@Override
	public FixMsgInputTopic getInputTopic(String topicName) {
		return (FixMsgInputTopic) getInputTopic(topicName, null);
	}
}
