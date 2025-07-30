package com.f1.ami.relay.fh.amps;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.crankuptheamps.authentication.kerberos.AMPSKerberosGSSAPIAuthenticator;
import com.crankuptheamps.client.Authenticator;
import com.crankuptheamps.client.Client;
import com.crankuptheamps.client.ClientDisconnectHandler;
import com.crankuptheamps.client.Command;
import com.crankuptheamps.client.ConnectionStateListener;
import com.crankuptheamps.client.Message;
import com.crankuptheamps.client.MessageHandler;
import com.crankuptheamps.client.exception.AuthenticationException;
import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.BasicFromJsonConverterSession;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.StringCharReader;
import com.google.protobuf.Parser;

public class AmiAmpsFH extends AmiFHBase {
	private static final Logger log = LH.get();

	public AmiAmpsFH() {
	}

	private static final String PROP_URL = "url";//url to connect to, ex: tcp://localhost:9007/amps/json
	private static final String PROP_TOPICS = "topics";//comma delimited list of topics, ex: ORDERS,EXECUTIONS
	private static final String PROP_FILTERS = "filters";//comma delimited list of filters (same count as topics or empty for no filter), ex: /status='open',1=1
	private static final String PROP_COMMANDS = "commands";//comma delimited list of commands (same count as topics or empty for sow_delta_subscribe)
	private static final String PROP_OPTIONS = "options";//comma delimited list of options (same count as topics or empty for oof)
	private static final String PROP_TIMEOUT = "timeout";//milliseconds for command timeout, default is 60000
	private static final String PROP_BATCHSIZE = "batchsize";//batch size for commands, defaut is 10000
	private static final String PROP_AMPS_CLIENT_NAME = "clientname";//name of client to pass into amps' client constructor
	private static final String PROP_USE_SOW_KEY = "sow_key_mappings";//should the AMPS sow key be mapped to AMI's I value? default is true
	private static final String PROP_PROTOBUF_CLASS = "protobuf_class";//should the AMPS sow key be mapped to AMI's I value? default is true
	private static final String PROP_RECONNECT_TIMEOUT_MS = "reconnect_interval_ms";//should the AMPS sow key be mapped to AMI's I value? default is true

	private static final String SSL_AUTH = "ssl";
	private static final String KERBEROS_AUTH = "kerberos";

	//SSL
	private static final String AUTH_PROTOCOL = "authentication_protocol";//protocol to use for authentication for connection to AMPS. [SSL, kerberos (soon)]
	private static final String KEYSTORE_FILE = "ssl_keystore_file";//path to the keystore file with client key and certificate
	private static final String KEYSTORE_PASSWORD = "ssl_keystore_password";//password to the keystore file 
	private static final String STORE_TYPE = "ssl_store_type";//type of keystore/truststore (JKS is default)
	private static final String SSL_PROTOCOL = "ssl_protocol";//protocol used for the connection (TLSv1.2 is default)

	//kerberos
	private static final String PROP_KERBEROS_HOSTNAME = "kerberos_hostname";// AMPS hostname for kerberos authentication
	private static final String PROP_KERBEROS_USERNAME = "kerberos_username";// username for kerberos authentication
	private static final String PROP_KERBEROS_TIMEOUT = "kerberos_timeout";//milliseconds for kerberos authentication timeout
	private static final String PROP_KERBEROS_JAAS_MODULE = "kerberos_jaas_module";//name of the module for the kerberos connection in the jaas config

	private String url;
	private String[] topics;
	private String[] commands;
	private String[] filters;
	private String[] options;
	private String[] sowMappings;
	private Client client;
	private String description;
	private byte mtype;
	private int timeout;
	private int batchSize;
	private String ampsId;
	private Parser<?> protobufParser;
	private int reconnectIntervalMs;
	private int ampsState;

	private Authenticator kerberosAuthenticator;
	private boolean useKerberos;
	private long kerberosTimeout;

	@Override
	public void start() {
		super.start();
		new Thread(new Connector(), "amps connector").start();
	}

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		url = this.props.getRequired(PROP_URL);
		SH.afterLast(url, '/');
		topics = SH.splitWithEscape(',', '\\', this.props.getRequired(PROP_TOPICS));
		filters = SH.splitWithEscape(',', '\\', this.props.getOptional(PROP_FILTERS, ""));
		commands = SH.splitWithEscape(',', '\\', this.props.getOptional(PROP_COMMANDS, "sow_and_delta_subscribe"));
		options = SH.splitWithEscape(',', '\\', this.props.getOptional(PROP_OPTIONS, "oof"));
		sowMappings = SH.splitWithEscape(',', '\\', this.props.getOptional(PROP_USE_SOW_KEY, "I"));
		String protobufClass = this.props.getOptional(PROP_PROTOBUF_CLASS);
		if (protobufClass != null) {
			this.protobufParser = AmiAmpsHelper.getProtobufParser(protobufClass);
			this.mtype = AmiAmpsHelper.MTYPE_PROTOBUF;
		} else
			this.mtype = AmiAmpsHelper.parseType(url);
		assertSize(filters.length, "filter");
		assertSize(commands.length, "command");
		assertSize(options.length, "option");
		timeout = this.props.getOptional(PROP_TIMEOUT, 60000);
		batchSize = this.props.getOptional(PROP_BATCHSIZE, 10000);
		ampsId = this.props.getOptional(PROP_AMPS_CLIENT_NAME, "AMPS2AMIRELAY");
		this.reconnectIntervalMs = this.props.getOptional(PROP_RECONNECT_TIMEOUT_MS, 5000);
		this.description = (url + " ==> " + SH.join(',', topics));

		setupAuth();
		this.client = new Client(ampsId);
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		this.ampsState = ConnectionStateListener.Disconnected;

		login();
	}

	private void setupAuth() {
		String[] types = SH.splitWithEscape(',', '\\', this.props.getOptional(AUTH_PROTOCOL, ""));
		for (String type : types) {
			if (SH.is(type)) {
				if (type.equals(SSL_AUTH)) {
					setupSSLAuth();
				} else if (type.equals(KERBEROS_AUTH)) {
					setupKerberosAuth();
				} else {
					LH.warning(log, "Unknown authentication protocol: ", type, ". Use one of ", SSL_AUTH, ", ", KERBEROS_AUTH);
				}
			}
		}
	}

	private void setupSSLAuth() {
		String keystoreFile = this.props.getRequired(KEYSTORE_FILE);

		String keystorePassword = this.props.getOptional(KEYSTORE_PASSWORD, "");

		String storeType = this.props.getOptional(STORE_TYPE, "JKS");
		String protocol = this.props.getOptional(SSL_PROTOCOL, "TLSv1.2");

		try {
			AmiAmpsDatasourceAdapter.setupSSLContext(keystoreFile, keystorePassword, storeType, protocol);
		} catch (Exception e) {
			LH.warning(log, "Error while setting up SSL connection for AMPS: ", e);
		}
	}

	private void setupKerberosAuth() {
		String hostname = this.props.getRequired(PROP_KERBEROS_HOSTNAME);
		this.kerberosTimeout = this.props.getOptional(PROP_KERBEROS_TIMEOUT, 5000);
		String ampsSPN = "AMPS/" + hostname;
		String jaasConfigName = this.props.getRequired(PROP_KERBEROS_JAAS_MODULE);

		this.useKerberos = true;
		try {
			this.kerberosAuthenticator = new AMPSKerberosGSSAPIAuthenticator(ampsSPN, jaasConfigName);
		} catch (AuthenticationException e) {
			LH.warning(log, "Exception while connecting to kerberos:", e);
		}
	}

	private void assertSize(int length, String string) {
		if (length != 0 && length != topics.length && length != 1)
			throw new RuntimeException(string + " count must match topic count");
	}

	public void connect() {
		try {
			client.connect(url);
			if (this.useKerberos) {
				client.logon(this.kerberosTimeout, this.kerberosAuthenticator);
			} else {
				client.logon();
			}

			for (int n = 0; n < topics.length; n++) {
				String topic = topics[n];
				String cmd = get(commands, n);
				String option = get(options, n);
				String filter = get(filters, n);
				String sowKey = get(sowMappings, n);
				try {
					for (String s : SH.split('+', cmd)) {
						Command command = new Command(s).setTopic(topic);
						command.setFilter(filter);
						command.setOptions(option);
						command.setTimeout(timeout);
						command.setBatchSize(batchSize);
						LH.info(log, "executed command=", s, ", topic=", topic, ", filter=", filter, ", options=", option, ", timeout=", timeout, ", batchSize=", batchSize,
								", sowkeymap=", sowKey);
						client.executeAsync(command, new Handler(sowKey, topic));
					}
				} catch (Exception e) {
					if (log.isLoggable(Level.FINE))
						LH.warning(log, "Error for AMPS URL with AMPS topic: ", url, ", ", topic, e);
				}
			}

		} catch (Exception e) {
			if (log.isLoggable(Level.FINE))
				LH.warning(log, "Error for reconnecting to AMPS URL: ", url, " for AmpsId: ", ampsId, e);
			return;
		}
	}

	public class Connector implements Runnable, ConnectionStateListener {

		@Override
		public void run() {
			client.addConnectionStateListener(this);
			client.setDisconnectHandler(new DisconnectHandler());
			while (ampsState == ConnectionStateListener.Disconnected || ampsState == ConnectionStateListener.Shutdown) {
				AmiAmpsFH.this.connect();
				if (ampsState == ConnectionStateListener.Disconnected || ampsState == ConnectionStateListener.Shutdown)
					try {
						Thread.sleep(reconnectIntervalMs);
					} catch (InterruptedException e) {
						LH.warning(log, "Waiting for reconnect error for amps ", ampsId, " timeout: ", reconnectIntervalMs, e);
					}
			}
		}

		@Override
		public void connectionStateChanged(int newState) {
			LH.info(log, "Connection state changed for ", ampsId, " to: ", formatState(newState), " (" + newState, ")");
			ampsState = newState;
		}

	}

	static private String formatState(int newState) {
		switch (newState) {
			case ConnectionStateListener.Disconnected:
				return "Disconnected";
			case ConnectionStateListener.Shutdown:
				return "Shutdown";
			case ConnectionStateListener.Connected:
				return "Connected";
			case ConnectionStateListener.LoggedOn:
				return "LoggedOn";
			case ConnectionStateListener.PublishReplayed:
				return "PublishReplayed";
			case ConnectionStateListener.HeartbeatInitiated:
				return "HeartbeatInitiated";
			case ConnectionStateListener.Resubscribed:
				return "Resubscribed";
			default:
				return "Unknown (" + newState + ")";
		}
	}
	private String get(String[] values, int n) {
		switch (values.length) {
			case 0:
				return null;
			case 1:
				return values[0];
			default:
				return values[n];
		}
	}
	@Override
	public void stop() {
		IOH.close(client);
		super.stop();
	}

	private class DisconnectHandler implements ClientDisconnectHandler {

		@Override
		public void invoke(Client client) {
			while (ampsState == ConnectionStateListener.Disconnected || ampsState == ConnectionStateListener.Shutdown) {
				AmiAmpsFH.this.connect();
				if (ampsState == ConnectionStateListener.Disconnected || ampsState == ConnectionStateListener.Shutdown)
					try {
						Thread.sleep(reconnectIntervalMs);
					} catch (InterruptedException e) {
						LH.warning(log, "Waiting for reconnect error for amps ", ampsId, " timeout: ", reconnectIntervalMs, e);
					}
			}
		}
	}

	private class Handler implements MessageHandler {

		private static final int LOG_BATCH_SIZE = 10000;
		private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
		private StringBuilder error = new StringBuilder();
		private String sowKey;
		private String topic;
		private String amiTableName;
		private long totalTime;
		private long totalCount;
		private BasicFromJsonConverterSession jsonConverter;
		private StringBuilder keyBuf = new StringBuilder();

		public Handler(String sowKey, String topic) {
			this.jsonConverter = new BasicFromJsonConverterSession(ObjectToJsonConverter.INSTANCE_CLEAN, new StringCharReader());
			this.topic = topic;
			this.amiTableName = AmiAmpsFH.this.getAmiTableName(this.topic);
			// Note if topic is not equal to message.getTopic() publish log is incorrect
			LH.info(log, "Initializing Amps Handler for topic:", this.topic, " mapping to AMI table: ", this.amiTableName);
			if (SH.isnt(sowKey))
				this.sowKey = null;
			else
				this.sowKey = sowKey;
		}

		@Override
		public void invoke(Message message) {
			String data = message.getData();
			long start = System.nanoTime();
			try {
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "AMPS Message received on Topic '", message.getTopic(), "' : ", message);
				switch (message.getCommand()) {
					case Message.Command.SOW:
					case Message.Command.DeltaPublish:
					case Message.Command.Publish:
					case Message.Command.OOF:
						error.setLength(0);
						if (!AmiAmpsHelper.parseMessage(mtype, data, jsonConverter, converter, error, protobufParser, keyBuf)) {
							LH.warning(log, "Error processing data: ", data, " ==> ", error);
							return;
						}
						String id;
						if (sowKey == null)
							id = null;
						else if ("I".equals(sowKey))
							id = message.getSowKey();
						else {
							id = null;
							converter.appendString(sowKey, message.getSowKey());
						}
						byte[] b = converter.toBytes();

						String type = topic;
						if (message.getCommand() == Message.Command.OOF)
							publishObjectDeleteToAmi(-1, id, type, b);
						else
							publishObjectToAmi(-1, id, type, 0, b);
				}
			} catch (Exception e) {
				LH.warning(log, "Error for ", message, " ==> ", e);
			} finally {
				this.totalTime += System.nanoTime() - start;
				this.totalCount++;
				if (this.totalCount % LOG_BATCH_SIZE == 0) {
					LH.info(log, "AMPS Message count on Topic '", message.getTopic(), "' : ", totalCount, " (", (totalTime / LOG_BATCH_SIZE), " avg nanos/per last ",
							LOG_BATCH_SIZE, " messages) ");
					this.totalTime = 0;
				}
			}
		}

	}

	@Override
	public String getDescription() {
		return this.description;
	}

}
