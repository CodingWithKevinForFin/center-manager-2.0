package com.f1.transportManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.fix2ami.processor.AbstractFix2AmiProcessor;
import com.f1.pofo.fix.MsgType;
import com.f1.transportManagement.SessionManager.CONNECTION_TYPE;
import com.f1.transportManagement.SessionManager.FixSessionContext;
import com.f1.utils.LH;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.Connector;
import quickfix.DataDictionary;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageStore;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.ThreadedSocketAcceptor;
import quickfix.ThreadedSocketInitiator;
import quickfix.UnsupportedMessageType;

public class FixSession extends AbstractSession implements Application {
	private static final Logger log = Logger.getLogger(FixSession.class.getName());

	static enum STATE {
						NEW,
						INITIALIZED,
						STARTED,
						SHUTDOWN
	}

	volatile private Session senderSession = null;
	volatile private Session targetSession = null;
	volatile private Session dropCopySession = null;
	volatile private Connector connector = null;

	final private MessageStore messageStore;
	final FixSessionContext fixSessionContext;

	volatile DataDictionary dataDictionary = null;

	volatile STATE state = STATE.NEW;
	volatile boolean done = false;
	volatile private String lastIncomingDataMessage = null;
	volatile private String lastOutgoingDataMessage = null;
	private final Map<String, String> partitionIdValueCache = new HashMap<>();

	public FixSession(final FixSessionContext fixSessionContext) throws ConfigError, FieldConvertError {
		this.fixSessionContext = fixSessionContext;
		this.messageStore = fixSessionContext.storeFactory.create(fixSessionContext.sessionID);
	}

	public synchronized void init() throws ConfigError {
		if (state.ordinal() > STATE.NEW.ordinal() && state != STATE.SHUTDOWN) {
			LH.info(log, fixSessionContext.sessionName, " - session has already been initialized and return immediately.");
			return;
		}

		if (null == fixSessionContext.connectionType) {
			LH.warning(log, "missing connectionType");
			throw new ConfigError("missing connectionType");
		} else if (CONNECTION_TYPE.acceptor == fixSessionContext.connectionType) {
			connector = new ThreadedSocketAcceptor(this, fixSessionContext.storeFactory, fixSessionContext.settings, fixSessionContext.logFactory,
					fixSessionContext.messageFactory);
		} else if (CONNECTION_TYPE.initiator == fixSessionContext.connectionType) {
			ThreadedSocketInitiator initiator = new ThreadedSocketInitiator(this, fixSessionContext.storeFactory, fixSessionContext.settings, fixSessionContext.logFactory,
					fixSessionContext.messageFactory);
			this.connector = initiator;
			initiator.getSettings().setString(fixSessionContext.sessionID, Initiator.SETTING_SOCKET_CONNECT_HOST, fixSessionContext.host);
			initiator.getSettings().setString(fixSessionContext.sessionID, Initiator.SETTING_SOCKET_CONNECT_PORT, Integer.toString(fixSessionContext.port));
		}
		state = STATE.INITIALIZED;
		LH.info(log, fixSessionContext.sessionName, " - session has been initialized.");
	}

	public synchronized void start() throws ConfigError {
		if (state.ordinal() < STATE.INITIALIZED.ordinal() || state == STATE.SHUTDOWN) {
			LH.info(log, fixSessionContext.sessionName, " - session has not been initialized yet, proceed to initialize session.");
			init();
		}

		if (null != connector) {
			connector.start();
			state = STATE.STARTED;
		} else {
			LH.warning(log, fixSessionContext.sessionName, fixSessionContext.connectionType == CONNECTION_TYPE.initiator ? " - initiator is not set." : " - acceptor is not set.");
		}

		this.senderSession = Session.lookupSession(fixSessionContext.sessionID);
		this.dataDictionary = senderSession.getDataDictionary();
		LH.info(log, fixSessionContext.sessionName, " - session has been started.");
	}

	public synchronized void shutdown() {
		if (state.ordinal() < STATE.STARTED.ordinal()) {
			LH.info(log, fixSessionContext.sessionName, " - system has been started yet and nothing to be shut down.");
			return;
		}

		if (null != connector) {
			connector.stop();
			state = STATE.SHUTDOWN;
		} else {
			LH.warning(log, fixSessionContext.sessionName, fixSessionContext.connectionType == CONNECTION_TYPE.initiator ? " - initiator is not set." : " - acceptor is not set.");
		}
		LH.info(log, fixSessionContext.sessionName, " - session has been shut down.");
	}

	public Session getSenderSession() {
		return senderSession;
	}

	public void setTargetSession(Session targetSession) {
		this.targetSession = targetSession;
	}

	public void setDropCopySession(Session dropCopySession) {
		this.dropCopySession = dropCopySession;
	}

	public boolean isDone() {
		return done;
	}

	public boolean isActive() {
		return state == STATE.STARTED;
	}

	public Message getLastIncomingDataMessage() throws InvalidMessage {
		return null == lastIncomingDataMessage ? null : new Message(lastIncomingDataMessage);
	}

	public Message getLastOutgoingDataMessage() throws InvalidMessage {
		return null == lastOutgoingDataMessage ? null : new Message(lastOutgoingDataMessage);
	}

	@Override
	public void onCreate(SessionID sessionId) {
		LH.info(log, fixSessionContext.sessionName, " - on create: ", sessionId);
	}

	@Override
	public void onLogon(SessionID sessionId) {
		LH.info(log, fixSessionContext.sessionName, " - onLogon: ", sessionId);
		done = false;
	}

	@Override
	public void onLogout(SessionID sessionId) {
		LH.info(log, fixSessionContext.sessionName, " - onLogout: ", sessionId);
		done = true;
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId) {
		if (!fixSessionContext.showHeartbeatMsg) {
			String msgTypeString = AbstractFix2AmiProcessor.getTagValue(dataDictionary, message, AbstractFix2AmiProcessor.TAG_MsgType);
			if ("0".equals(msgTypeString)) {
				return;
			}
		}
		LH.info(log, fixSessionContext.sessionName, "(Admin) ->   ", message);
	}

	@Override
	public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		if (!fixSessionContext.showHeartbeatMsg) {
			String msgTypeString = AbstractFix2AmiProcessor.getTagValue(dataDictionary, message, AbstractFix2AmiProcessor.TAG_MsgType);
			if ("0".equals(msgTypeString)) {
				return;
			}
		}
		LH.info(log, fixSessionContext.sessionName, "(Admin) <-   ", message);
	}

	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		LH.info(log, fixSessionContext.sessionName, "(data) ->   ", message);
		if (fixSessionContext.trackLastMessage) {
			lastOutgoingDataMessage = message.toString();
		}
	}

	@Override
	public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		LH.info(log, fixSessionContext.sessionName, "(data) <-   ", message);
		if (fixSessionContext.trackLastMessage) {
			lastIncomingDataMessage = message.toString();
		}

		if (!fixSessionContext.straightThrough || (null != targetSession && targetSession.isLoggedOn())) {
			if (null != dropCopySession) {
				LH.fine(log, "try to write to drop copy session");
				dropCopySession.send(message);
			}

			if (null != targetSession) {
				LH.fine(log, "try to write to target session");
				targetSession.send(message);
			}
		} else {
			LH.warning(log, "target session (", fixSessionContext.targetSessionName, ") is not running yet.  Throw UnsupportedMessageType to the sender.");
			throw new UnsupportedMessageType();
		}

		// sends message to container partition callback framework.
		if (null != fixSessionContext.container && null != fixSessionContext.messageDispatcher) {

			final String clOrdID = AbstractFix2AmiProcessor.getTagValue(dataDictionary, message, AbstractFix2AmiProcessor.TAG_ClOrdID);

			String msgTypeString = AbstractFix2AmiProcessor.getTagValue(dataDictionary, message, AbstractFix2AmiProcessor.TAG_MsgType);
			MsgType msgType = null;
			try {
				if (null != msgTypeString) {
					msgType = MsgType.get(msgTypeString.charAt(0));
				}
			} catch (RuntimeException re) {
				LH.info(log, "unsupport message type: ", msgTypeString);
			}

			String partitionId = fixSessionContext.partitionDefaultValue;
			if (fixSessionContext.partitionByFixTag != -1) {
				partitionId = AbstractFix2AmiProcessor.getTagValue(dataDictionary, message, fixSessionContext.partitionByFixTag);

				if (null == partitionId || partitionId.isEmpty()) {
					partitionId = partitionIdValueCache.get(clOrdID);
				}
				if (null == partitionId || partitionId.isEmpty()) {
					partitionId = fixSessionContext.partitionDefaultValue;
				}
				if (fixSessionContext.partitionIdLength != -1 && partitionId.length() >= fixSessionContext.partitionIdLength) {
					partitionId = partitionId.substring(0, fixSessionContext.partitionIdLength);
				}
			}

			if (fixSessionContext.msgTypeCacheForPartitionId.contains(msgType) && !partitionIdValueCache.containsKey(clOrdID)) {
				partitionIdValueCache.put(clOrdID, partitionId);
			}

			fixSessionContext.messageDispatcher.sendRequest(fixSessionContext.container, clOrdID, msgType, partitionId, message);
		}
	}

}
