package com.f1.ami.relay.fh.ibmmq;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueueConnectionFactory;

public class AmiIBMMQFH extends AmiFHBase {
	private static final Logger log = LH.get();

	private static final String PROP_HOST = "host";
	private static final String PROP_PORT = "port";
	private static final String PROP_USERNAME = "username";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_QUEUEMANAGER = "queuemanager";
	private static final String PROP_QUEUENAME = "queuename";
	private static final String PROP_CHANNEL = "channel";
	private static final String PROP_FORMAT = "format"; // only accepts FIX,XML,JSON,COBOL
	private static final String PROP_READANDDELETE = "readanddelete";
	private static final String PROP_DEBUG = "debug";
	private String queuemanager;
	private String queuename;
	private String host;
	private String port;
	private String format;
	private String channelName;
	private boolean readAndDelete = false;
	private boolean debug = false;
	private MQConnectionFactory factory = null;
	private Queue queue = null;
	private QueueBrowser browser = null;
	private Connection connection = null;
	private String username;
	private String password;
	private boolean useTLS;
	private AmiIbmMqHelper parserHelper;

	// Set to track processed message IDs
	Set<String> processedMessageIDs = new HashSet<>();

	public void start() {
		super.start();
		try {
			(new Thread(new Connector(), "IBM MQ Browser connector")).start();
		} catch (Exception e) {
			log.severe("Failed to start up the FH: " + e.getMessage());
		}
	}

	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		this.host = this.props.getRequired(PROP_HOST);
		this.port = this.props.getRequired(PROP_PORT);
		this.username = this.props.getOptional(PROP_USERNAME);
		this.password = this.props.getOptional(PROP_PASSWORD);
		this.format = this.props.getRequired(PROP_FORMAT);
		this.queuemanager = this.props.getRequired(PROP_QUEUEMANAGER);
		this.queuename = this.props.getRequired(PROP_QUEUENAME);
		this.channelName = this.props.getOptional(PROP_CHANNEL);
		this.readAndDelete = this.props.getOptional(PROP_READANDDELETE, false);
		this.debug = this.props.getOptional(PROP_DEBUG, false);

		getAmiRelayIn().onConnection(EMPTY_PARAMS);

		try {
			Class<AmiIbmMqHelperFactory> factoryClass = (Class<AmiIbmMqHelperFactory>) Class.forName("com.f1.ami.relay.fh.ibmmq.AmiIbmMqHelperFactory");
			parserHelper = factoryClass.newInstance().getIBMHelper(this.format);
			if (parserHelper == null) {
				LH.warning(log, "No parser found for " + this.format + "! Records may have parsing errors.");
				parserHelper = new AmiIbmMqHelper();
			}
		} catch (Exception e) {
			LH.warning(log, e.getMessage());
		}

		login();
	}

	public void connect() {
		try {
			this.factory = (MQConnectionFactory) new MQQueueConnectionFactory();
			this.factory.setQueueManager(this.queuemanager);
			this.factory.setHostName(this.host);
			this.factory.setPort(Integer.parseInt(this.port));
			if (this.channelName != null)
				this.factory.setChannel(this.channelName);
			this.connection = this.factory.createConnection(this.username, this.password);
			this.connection.start();
		} catch (JMSException e) {
			LH.warning(log, "Failed to connect: " + e.getMessage());
			e.printStackTrace();
		}
		while (true) {
			Session session;
			try {
				session = this.connection.createSession(false, 1);
				this.queue = session.createQueue(this.queuename);
				QueueBrowser queueBrowser = session.createBrowser(this.queue);
				Enumeration<?> messages = queueBrowser.getEnumeration();
				if (this.debug) {
					LH.warning(log, "Browsing messages from queue: " + this.queuename);
				}
				if (!messages.hasMoreElements()) {
					if (this.debug) {
						LH.warning(log, "No messages in the queue.");
					}
					Thread.sleep(5000L);
				} else {
					while (messages.hasMoreElements()) {
						TextMessage message = (TextMessage) messages.nextElement();

						String messageID = message.getJMSMessageID();
						if (processedMessageIDs.contains(messageID)) {
							continue;
						}

						// Process the message and mark it as processed
						processedMessageIDs.add(messageID);
						String toParse = message.getText();
						MessageHandler messagehandler = new Handler();
						((Handler) messagehandler).sendMessage(this.queuename, message.getJMSMessageID(), toParse, message.getJMSTimestamp());

						//Delete after read
						if (this.readAndDelete) {
							MessageConsumer consumer = session.createConsumer(this.queue);
							consumer.receive(1000);
						}
					}
					queueBrowser.close();
					Thread.sleep(5000L);
				}
			} catch (JMSException e) {
				LH.warning(log, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LH.warning(log, e.getMessage());
			}
		}
	}

	public class Connector implements Runnable {
		public void run() {
			AmiIBMMQFH.this.connect();
		}
	}

	private class Handler implements MessageHandler {
		private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
		private Map<String, Object> msg = new HashMap<>();
		private StringBuilder error = new StringBuilder();

		public void sendMessage(String stream, String key, Object value, long timestamp) {
			try {
				this.msg.clear();
				this.error.setLength(0);
				if (!parserHelper.parseMessage(value, this.msg, this.error)) {
					LH.warning(AmiIBMMQFH.log, new Object[] { "Error processing data: ", value, " ==> ", this.error });
					return;
				}
				String id = null;
				byte[] b = this.converter.toBytes(this.msg);
				String type = AmiIBMMQFH.this.getAmiTableName(stream);
				AmiIBMMQFH.this.getAmiRelayIn().onObject(-1L, id, type, 0L, b);
			} catch (Exception e) {
				LH.warning(AmiIBMMQFH.log, new Object[] { "Error for ", value, " ==> ", e });
			}
		}
	}

	protected Map<String, Object> processMessage(Map<String, Object> parts, String topic2) {
		return parts;
	}
}
