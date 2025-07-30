package com.f1.transportManagement;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.container.Container;
import com.f1.pofo.fix.MsgType;
import com.f1.qfix.QuickFixSpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.impl.SpeedLoggerInstance;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.Dictionary;
import quickfix.FieldConvertError;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

public class SessionManager {
	private Logger log = LH.get();

	public static final String ATTR_SESSION_MANAGER_WAIT_INTERVAL = "doneWaitInterval";
	public static final String ATTR_QFIX_PREFIX = "qfix.";
	public static final String ATTR_SESSION_PREFIX = "session.";
	public static final String ATTR_BEGIN_STRING = "BeginString";
	public static final String ATTR_SENDER_COMP_ID = "SenderCompID";
	public static final String ATTR_TARGET_COMP_ID = "TargetCompID";
	public static final String ATTR_SOCKET_CONNECT_PORT = "SocketConnectPort";
	public static final String ATTR_SOCKET_CONNECT_HOST = "SocketConnectHost";
	public static final String ATTR_SOCKET_ACEPT_PORT = "SocketAcceptPort";
	public static final String ATTR_CONNECT_TYPE = "ConnectionType";
	public static final String ATTR_TARGET_SESSION_NAME = "TargetSessionName";
	public static final String ATTR_DROP_COPY_SESSION_NAME = "DropCopySessionName";
	public static final String ATTR_STRAIGHT_THROUGH = "StraightThrough";
	public static final String ATTR_TRACK_LAST_MESSAGE = "TrackLastMessage";
	public static final String ATTR_SHOW_HEARTBEAT_MSG = "ShowHeartbeatMessage";

	public static final String ATTR_PARTITION_BY_FIX_TAG = "PartitionByFixTag";
	public static final String ATTR_PARTITION_ID_LENGTH = "PartitionIdLength";
	public static final String ATTR_PARTITION_DEFAULT_VALUE = "PartitionDefaultValue";
	public static final String ATTR_MSG_TYPE_FOR_PARTITION_ID_CACHING = "MsgTypeForPartitionIdCaching";

	final private LogFactory logFactory;
	final private PropertyController rootProps;
	final private int donePollInterval;

	enum CONNECTION_TYPE {
							initiator,
							acceptor
	};

	public static class FixSessionContext {
		final String sessionName;
		final SessionID sessionID;
		final SessionSettings settings;
		final MessageStoreFactory storeFactory;
		final LogFactory logFactory;
		final MessageFactory messageFactory;
		final CONNECTION_TYPE connectionType;
		final String targetCompID;
		final String senderCompID;
		final String targetSessionName;
		final String dropCopySessionName;
		final String host;
		final int port;
		final boolean straightThrough;
		final boolean trackLastMessage;
		final Container container;
		final int partitionByFixTag;
		final int partitionIdLength;
		final String partitionDefaultValue;
		volatile FixSession senderFixSession = null;
		volatile FixSession targetFixSession = null;
		final Set<MsgType> msgTypeCacheForPartitionId;
		final boolean showHeartbeatMsg;
		final MessageDispatcher messageDispatcher;

		FixSessionContext(final String sessionName, final SessionSettings settings, final SessionID sessionID, final FileStoreFactory storeFactory,
				final MessageFactory messageFactory, final LogFactory logFactory, CONNECTION_TYPE connectionType, final String senderCompID, final String targetCompID,
				final String targetSessionName, final String dropCopySessionName, final String host, int port, boolean straightThrough, boolean trackLastMessage,
				final Container container, int partitionByFixTag, int partitionIdLength, final String partitionDefaultValue, final Set<MsgType> msgTypeCacheForPartitionId,
				boolean showHeartbeatMsg, final MessageDispatcher messageDispatcher) {
			this.sessionName = sessionName;
			this.settings = settings;
			this.sessionID = sessionID;
			this.storeFactory = storeFactory;
			this.messageFactory = messageFactory;
			this.logFactory = logFactory;
			this.connectionType = connectionType;
			this.senderCompID = senderCompID;
			this.targetCompID = targetCompID;
			this.targetSessionName = targetSessionName;
			this.dropCopySessionName = dropCopySessionName;
			this.host = host;
			this.port = port;
			this.straightThrough = straightThrough;
			this.trackLastMessage = trackLastMessage;
			this.container = container;
			this.partitionByFixTag = partitionByFixTag;
			this.partitionIdLength = partitionIdLength;
			this.partitionDefaultValue = partitionDefaultValue;
			this.msgTypeCacheForPartitionId = msgTypeCacheForPartitionId;
			this.showHeartbeatMsg = showHeartbeatMsg;
			this.messageDispatcher = messageDispatcher;
		}

		public FixSession getSenderFixSession() {
			return senderFixSession;
		}

		public FixSession getTargetFixSession() {
			return targetFixSession;
		}
	}

	final Map<String, FixSessionContext> streamName2fixSessionContext = new HashMap<>();

	public FixSessionContext getFixSessionContext(final String sessionName) {
		return streamName2fixSessionContext.get(sessionName);
	}

	public SessionManager(PropertyController rootProps) throws ConfigError, FieldConvertError {
		this(null, null, rootProps);
	}

	public SessionManager(final Container container, final MessageDispatcher messageDispatcher, PropertyController rootProps) throws ConfigError, FieldConvertError {
		this.rootProps = rootProps;

		this.donePollInterval = rootProps.getOptional(ATTR_SESSION_MANAGER_WAIT_INTERVAL, 120000);

		logFactory = new QuickFixSpeedLoggerManager(SpeedLoggerInstance.getInstance(), SpeedLoggerLevels.INFO, SpeedLoggerLevels.INFO, SpeedLoggerLevels.INFO);
		PropertyController qFixProps = rootProps.getSubPropertyController(ATTR_QFIX_PREFIX);

		String[] sessionList = SH.split(',', qFixProps.getRequired("sessions"));
		for (String sessionName : sessionList) {
			SessionSettings aSettings = new SessionSettings();
			String host = null;
			CONNECTION_TYPE connectionType = null;
			String targetCompID = null;
			String senderCompID = null;
			String dropCopySessionName = null;
			String targetSessionName = null;
			boolean straightThrough = true;
			boolean trackLastMessage = false;
			int port = -1;
			int partitionByFixTag = -1;
			int partitionIdLength = -1;
			String partitionDefaultValue = "AAA";
			Set<MsgType> msgTypeCacheForPartitionId = EnumSet.noneOf(MsgType.class);
			boolean showHeartbeatMsg = false;

			Dictionary dict = new Dictionary();
			PropertyController props = qFixProps.getSubPropertyController(sessionName + '.');
			for (String key : props.getKeys()) {
				switch (key) {
					case ATTR_SOCKET_CONNECT_PORT:
						port = props.getRequired(key, Integer.class);
						continue;
					case ATTR_SOCKET_CONNECT_HOST:
						host = props.getRequired(key);
						break;
					case ATTR_CONNECT_TYPE:
						connectionType = CONNECTION_TYPE.valueOf(props.getRequired(key));
						break;
					case ATTR_SENDER_COMP_ID:
						senderCompID = props.getRequired(key);
						break;
					case ATTR_TARGET_COMP_ID:
						targetCompID = props.getRequired(key);
						break;
					case ATTR_TARGET_SESSION_NAME:
						targetSessionName = props.getRequired(key);
						continue;
					case ATTR_DROP_COPY_SESSION_NAME:
						dropCopySessionName = props.getRequired(key);
						continue;
					case ATTR_STRAIGHT_THROUGH:
						straightThrough = props.getRequired(key, Boolean.class);
						continue;
					case ATTR_TRACK_LAST_MESSAGE:
						trackLastMessage = props.getRequired(key, Boolean.class);
						continue;
					case ATTR_PARTITION_BY_FIX_TAG:
						partitionByFixTag = props.getRequired(key, Integer.class);
						continue;
					case ATTR_PARTITION_ID_LENGTH:
						partitionIdLength = props.getRequired(key, Integer.class);
						continue;
					case ATTR_PARTITION_DEFAULT_VALUE:
						partitionDefaultValue = props.getRequired(key);
						continue;
					case ATTR_MSG_TYPE_FOR_PARTITION_ID_CACHING:
						for (String string : props.getRequired(key).split(",")) {
							try {
								msgTypeCacheForPartitionId.add(MsgType.get(string.charAt(0)));
							} catch (Exception e) {
								LH.info(log, "Invalid message type for ", sessionName, '.', ATTR_MSG_TYPE_FOR_PARTITION_ID_CACHING, " attribute - message type: ", key);
							}
						}
						continue;
					case ATTR_SHOW_HEARTBEAT_MSG:
						showHeartbeatMsg = props.getRequired(key, Boolean.class);
						continue;
					default:
						break;
				}
				dict.setString(key, props.getRequired(key));
			}

			SessionID sessID = new SessionID(new BeginString(props.getRequired(ATTR_BEGIN_STRING)), new SenderCompID(props.getRequired(ATTR_SENDER_COMP_ID)),
					new TargetCompID(props.getRequired(ATTR_TARGET_COMP_ID)));

			for (String key : props.getKeys())
				aSettings.setString(sessID, key, props.getRequired(key));

			FixSessionContext fixSessionContext = new FixSessionContext(sessionName, aSettings, sessID, new FileStoreFactory(aSettings), new DefaultMessageFactory(), logFactory,
					connectionType, senderCompID, targetCompID, targetSessionName, dropCopySessionName, host, port, straightThrough, trackLastMessage, container, partitionByFixTag,
					partitionIdLength, partitionDefaultValue, msgTypeCacheForPartitionId, showHeartbeatMsg, messageDispatcher);
			fixSessionContext.senderFixSession = new FixSession(fixSessionContext);
			fixSessionContext.senderFixSession.init();

			streamName2fixSessionContext.put(sessionName, fixSessionContext);
		}

	}

	private Session getSessionByStreamName(final PropertyController props, final String sessionName) {
		Session session = null;
		FixSessionContext fixSessionContext = streamName2fixSessionContext.get(sessionName);
		if (null != fixSessionContext && null != fixSessionContext.sessionID) {
			session = Session.lookupSession(fixSessionContext.sessionID);
		}
		return session;
	}

	public void start() throws ConfigError {
		final PropertyController sessionMappingProps = rootProps.getSubPropertyController(ATTR_SESSION_PREFIX);

		//start all sessions
		for (Map.Entry<String, FixSessionContext> entry : streamName2fixSessionContext.entrySet()) {
			entry.getValue().senderFixSession.start();
		}

		for (Map.Entry<String, FixSessionContext> entry : streamName2fixSessionContext.entrySet()) {
			PropertyController props = sessionMappingProps.getSubPropertyController(entry.getKey() + '.');

			Session targetSession = getSessionByStreamName(props, entry.getValue().targetSessionName);
			if (null != targetSession) {
				entry.getValue().senderFixSession.setTargetSession(targetSession);
				FixSessionContext targetFixSessionContext = streamName2fixSessionContext.get(entry.getValue().targetSessionName);
				if (null != targetFixSessionContext) {
					entry.getValue().targetFixSession = targetFixSessionContext.senderFixSession;
				} else {
					LH.warning(log, "missing target session context(", entry.getValue().targetSessionName, ").  Check qfix configuration.");
					throw new ConfigError("missing target session context");
				}
			}

			Session dropCopySession = getSessionByStreamName(props, entry.getValue().dropCopySessionName);
			if (null != dropCopySession) {
				entry.getValue().senderFixSession.setDropCopySession(dropCopySession);
			}

			//entry.getValue().senderFixSession.start();
		}
	}

	public void shutdown() throws Exception {
		for (Map.Entry<String, FixSessionContext> entry : streamName2fixSessionContext.entrySet()) {
			entry.getValue().senderFixSession.shutdown();
		}
	}

	public void waitForComplete() throws InterruptedException {
		LH.info(log, "entered waiting loop.");
		while (!Thread.currentThread().isInterrupted()) {
			boolean allDone = true;
			for (Map.Entry<String, FixSessionContext> entry : streamName2fixSessionContext.entrySet()) {
				if (!entry.getValue().senderFixSession.isDone()) {
					allDone = false;
				}
			}

			if (allDone) {
				LH.info(log, "All session is down.  Exiting...");
				break;
			}
			Thread.sleep(donePollInterval);
		}
	}
}
