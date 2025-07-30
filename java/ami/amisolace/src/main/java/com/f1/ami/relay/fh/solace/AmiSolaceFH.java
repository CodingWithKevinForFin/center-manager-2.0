package com.f1.ami.relay.fh.solace;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.solace.messaging.MessagingService;
import com.solace.messaging.MessagingServiceClientBuilder;
import com.solace.messaging.config.AuthenticationStrategy.ClientCertificateAuthentication;
import com.solace.messaging.config.AuthenticationStrategy.Kerberos;
import com.solace.messaging.config.AuthenticationStrategy.OAuth2;
import com.solace.messaging.config.SolaceProperties.AuthenticationProperties;
import com.solace.messaging.config.SolaceProperties.ServiceProperties;
import com.solace.messaging.config.SolaceProperties.TransportLayerProperties;
import com.solace.messaging.config.TransportSecurityStrategy.TLS;
import com.solace.messaging.config.TransportSecurityStrategy.TLS.SecureProtocols;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.receiver.DirectMessageReceiver;
import com.solace.messaging.receiver.InboundMessage;
import com.solace.messaging.receiver.PersistentMessageReceiver;
import com.solace.messaging.resources.Queue;
import com.solace.messaging.resources.TopicSubscription;;

public class AmiSolaceFH extends AmiFHBase {
	private static final Logger log = LH.get();

	public static final String PROP_TABLE_NAME = "tableName";
	public static final String PROP_TABLE_MAPPING = "tableMapping";
	public static final String PROP_FILEPATH = "propertyFilepath";
	public static final String PROP_SOLACE_TOPICS = "topics";
	public static final String PROP_SOLACE_QUEUE = "queue";
	public static final String PROP_SOLACE_QUEUE_TYPE = "queueType";
	public static final String PROP_SOLACE_PARSE_MODE = "parseMode";
	public static final String PROP_SOLACE_HOST = "host";
	public static final String PROP_SOLACE_VPN_NAME = "vpnName";
	public static final String PROP_SOLACE_USERNAME = "username";
	public static final String PROP_SOLACE_PASSWORD = "password";
	public static final String PROP_SOLACE_PROTOBUF_PARSER = "protobufParserClass";
	public static final String PROP_SOLACE_PROTOBUF = "protobufClass";
	public static final String PROP_SOLACE_PLAINTEXT_COLUMN_DELIMITER = "plaintext.column.delimiter";
	public static final String PROP_SOLACE_PLAINTEXT_KEYVAL_DELIMITER = "plaintext.keyval.delimiter";
	public static final String PROP_SOLACE_USE_TLS = "useTLS";
	public static final String PROP_SOLACE_TLS_TRUSTSTORE_PASSWORD = "tlsTruststorePassword";
	public static final String PROP_SOLACE_TLS_IGNORE_EXPIRATION = "tlsIgnoreExpiration";
	public static final String PROP_SOLACE_KEYSTORE_URL = "keystoreUrl";
	public static final String PROP_SOLACE_KEYSTORE_PASSWORD = "keystorePassword";
	public static final String PROP_SOLACE_KERBEROS_INSTANCE_NAME = "kerberosInstanceName";
	public static final String PROP_SOLACE_KERBEROS_JAAS_CONTEXT_NAME = "kerberosJaasContextName";
	public static final String PROP_SOLACE_KERBEROS_USERNAME_ON_BROKER = "kerberosUsernameOnBroker";
	public static final String PROP_SOLACE_OAUTH_ACCESS_TOKEN = "oauthAccessToken";
	public static final String PROP_SOLACE_OAUTH_ISSUER_IDENTIFIER = "oauthIssuerIdentifier";

	private Thread t = null, t2 = null;
	private MessagingService service = null;
	private Parser<?> protobufParser;
	private PlainTextParser plaintextParser = null;

	private String topic;
	private String queueName;
	private String table;

	private static final char MODE_JSON = 1;
	private static final char MODE_PROTOBUF = 1 << 1;
	private static final char MODE_PLAINTEXT = 3;
	private char parsingMode;

	private Queue queue;

	Map<String, FHSetter> columnMapping;

	public AmiSolaceFH() {
	}

	@Override
	public void start() {
		super.start();
		try {
			startSolace();
			onStartFinish(true);
		} catch (Exception e) {
			log.severe("Failed to start up the FH: " + e.getMessage());
			onStartFinish(false);
		}
	}

	private void parseMessage(final AmiRelayMapToBytesConverter converter, final ObjectToJsonConverter jsonConverter, final InboundMessage message) throws Exception {
		converter.clear();
		if (log.isLoggable(Level.FINE))
			log.fine("Solace message received : " + message);

		if (parsingMode == MODE_PROTOBUF) {
			try {
				AbstractMessage p = (AbstractMessage) protobufParser.parseFrom(message.getPayloadAsBytes());
				for (Entry<FieldDescriptor, Object> e : p.getAllFields().entrySet()) {
					final FHSetter setter = columnMapping != null ? columnMapping.get(e.getKey().getName()) : null;
					Object v = e.getValue();
					//If object is null, ignore (FH does not support writing/reading nulls for types)
					if (v == null)
						continue;
					if (setter != null)
						setter.set(converter, v);
					else {
						if (v instanceof Number) {
							if (OH.isFloat(v.getClass()))
								converter.appendDouble(e.getKey().getName(), Caster_Double.INSTANCE.cast(v));
							else if (v instanceof Integer || v instanceof Long || v instanceof Short || v instanceof Byte)
								converter.appendLong(e.getKey().getName(), Caster_Long.INSTANCE.cast(v));
						} else {
							converter.appendString(e.getKey().getName(), SH.toString(v));
						}

					}
				}
			} catch (InvalidProtocolBufferException e1) {
				log.warning("protobufs error: " + e1.getMessage());
			}
		} else if (parsingMode == MODE_JSON) {
			final String messagePayload = message.getPayloadAsString();
			@SuppressWarnings("unchecked")
			Map<String, Object> jsonMap = (Map<String, Object>) jsonConverter.stringToObject(messagePayload);
			for (final Entry<String, Object> entry : jsonMap.entrySet()) {
				final Object o = entry.getValue();
				//If object is null, ignore (FH does not support writing/reading nulls for types)
				if (o == null)
					continue;
				final FHSetter setter = columnMapping != null ? columnMapping.get(entry.getKey()) : null;
				if (setter != null)
					setter.set(converter, o);
				else {
					if (o instanceof Number) {
						if (OH.isFloat(o.getClass()))
							converter.appendDouble(entry.getKey(), Caster_Double.INSTANCE.cast(o));
						else if (o instanceof Integer || o instanceof Long || o instanceof Short || o instanceof Byte)
							converter.appendLong(entry.getKey(), Caster_Long.INSTANCE.cast(o));
					} else {
						converter.appendString(entry.getKey(), SH.toString(o));
					}
				}
			}
		} else if (parsingMode == MODE_PLAINTEXT) {
			final String messagePayload = message.getPayloadAsString();
			Map<String, String> output = this.plaintextParser.parse(messagePayload);
			for (final Entry<String, String> entry : output.entrySet()) {
				final String o = entry.getValue();
				//If object is null, ignore (FH does not support writing/reading nulls for types)
				if (o == null)
					continue;
				if (SH.isDouble(o)) { // instance of Number
					if (SH.isWholeNumber(o))
						converter.appendLong(entry.getKey(), Caster_Long.INSTANCE.cast(o));
					else
						converter.appendDouble(entry.getKey(), Caster_Double.INSTANCE.cast(o));
				} else {
					converter.appendString(entry.getKey(), o);
				}
			}
		} else {
			throw new Exception("Invalid parsing mode");
		}

		publishObjectToAmi(-1, message.getApplicationMessageId(), table, 0, converter.toBytes());
	}

	private void startSolace() {
		if (!SH.isEmpty(this.topic)) {
			List<String> topics = SH.splitToList(",", topic);
			int topic_count = topics.size();

			//Direct Message Receiver
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
						ObjectToJsonConverter jsonConverter = new ObjectToJsonConverter();
						final DirectMessageReceiver receiver;
						log.info("Building Direct Message Receiver");
						if (topic_count > 1) {
							log.info("Multiple Topics Found: " + topics.toString());
							List<TopicSubscription> subscriptions = new ArrayList<>();
							for (String topic : topics) {
								subscriptions.add(TopicSubscription.of(topic));
							}
							receiver = service.createDirectMessageReceiverBuilder().withSubscriptions(subscriptions.toArray(new TopicSubscription[0])).build().start();
						} else {
							log.info("Single Topic Found: " + topic);
							receiver = service.createDirectMessageReceiverBuilder().withSubscriptions(TopicSubscription.of(topic)).build().start();
						}
						log.info("DirectMessageReceiver created with Topic Subscriptions");
						while (true) {
							parseMessage(converter, jsonConverter, receiver.receiveMessage());
						}
					} catch (Exception e) {
						log.severe(e.getMessage());
						onFailed("Connection to Solace has been closed");
					} finally {
						if (service != null)
							service.disconnect();
						;
					}
				}
			};

			t = getManager().getThreadFactory().newThread(r);
			t.setDaemon(true);
			t.setName("SolaceFH:Direct-" + this.getId());
			t.start();
		}

		if (!SH.isEmpty(this.queueName)) {
			//Persistent Message Receiver
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
						ObjectToJsonConverter jsonConverter = new ObjectToJsonConverter();
						final PersistentMessageReceiver receiver = service.createPersistentMessageReceiverBuilder().build(queue).start();

						while (true) {
							final InboundMessage message = receiver.receiveMessage();
							receiver.ack(message);
							parseMessage(converter, jsonConverter, message);
						}
					} catch (Exception e) {
						log.severe(e.getMessage());
						onFailed("Connection to Solace has been closed");
					} finally {
						if (service != null)
							service.disconnect();
						;
					}
				}
			};

			t2 = getManager().getThreadFactory().newThread(r);
			t2.setDaemon(true);
			t2.setName("SolaceFH:Persist-" + this.getId());
			t2.start();
		}
	}

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);

		this.table = props.getRequired(PROP_TABLE_NAME);

		this.topic = props.getOptional(PROP_SOLACE_TOPICS, "");
		this.queueName = props.getOptional(PROP_SOLACE_QUEUE, "");

		if (SH.isEmpty(this.topic) && SH.isEmpty(this.queueName))
			throw new RuntimeException("Either a topic or queue name must be specified");

		if (!SH.isEmpty(queueName)) {
			//Determine queue type - defaults to nondurable exclusive
			String queueType = SH.toLowerCase(props.getOptional(PROP_SOLACE_QUEUE_TYPE, ""));
			if ("durableexclusive".equals(queueType))
				this.queue = Queue.durableExclusiveQueue(this.queueName);
			else if ("durablenonexclusive".equals(queueType))
				this.queue = Queue.durableNonExclusiveQueue(this.queueName);
			else
				this.queue = Queue.nonDurableExclusiveQueue(this.queueName);
		}

		String tableMapping = props.getOptional(PROP_TABLE_MAPPING, "");
		if (!SH.isEmpty(tableMapping))
			buildTableSchema(tableMapping);

		//Get parsing mode - defaults to json
		String parseModeStr = SH.toLowerCase(props.getOptional(PROP_SOLACE_PARSE_MODE, "json"));
		if ("protobuf".equals(parseModeStr)) {
			this.parsingMode = MODE_PROTOBUF;
			String protobufParser = props.getRequired(PROP_SOLACE_PROTOBUF_PARSER);
			String protobufClass = props.getOptional(PROP_SOLACE_PROTOBUF, "");
			this.protobufParser = getProtobufParser(protobufParser, protobufClass);
			if (this.protobufParser == null)
				throw new RuntimeException("Failed to initialize protobuf parser of class: " + protobufClass);
		} else if ("plaintext".equals(parseModeStr)) {
			this.parsingMode = MODE_PLAINTEXT;
			String keyValDelimiter = props.getOptional(PROP_SOLACE_PLAINTEXT_KEYVAL_DELIMITER, "=");
			String colDelimiter = props.getRequired(PROP_SOLACE_PLAINTEXT_COLUMN_DELIMITER);
			this.plaintextParser = new PlainTextParser(colDelimiter, keyValDelimiter);
		} else {
			this.parsingMode = MODE_JSON;
		}

		final Properties properties = new Properties();
		//Attempt to load properties from a file
		String propertyFilepath = props.getOptional(PROP_FILEPATH, "");
		if (!SH.isEmpty(propertyFilepath)) {
			try (InputStream input = new FileInputStream(propertyFilepath)) {
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		trySetProperty(properties, TransportLayerProperties.HOST, PROP_SOLACE_HOST);
		trySetProperty(properties, ServiceProperties.VPN_NAME, PROP_SOLACE_VPN_NAME);

		MessagingServiceClientBuilder builder = MessagingService.builder(ConfigurationProfile.V1);

		boolean useTLS = props.getOptional(PROP_SOLACE_USE_TLS, false);
		if (useTLS) {
			log.fine("Using TLS for connection");
			final String truststorePassword = props.getOptional(PROP_SOLACE_TLS_TRUSTSTORE_PASSWORD, "");
			final boolean ignoreExpiration = props.getOptional(PROP_SOLACE_TLS_IGNORE_EXPIRATION, false);

			final TLS tls = SH.isEmpty(truststorePassword) ?
			//No validation
					TLS.create().withoutCertificateValidation() :
					//Validate certificate
					TLS.create().withCertificateHostValidation().withCertificateValidation(truststorePassword, ignoreExpiration).withExcludedProtocols(SecureProtocols.SSLv3);

			builder = builder.withTransportSecurityStrategy(tls);
		}

		//Handle authentication mode
		final String keystoreURL = props.getOptional(PROP_SOLACE_KEYSTORE_URL, "");
		final String keystorePassword = props.getOptional(PROP_SOLACE_KEYSTORE_PASSWORD, "");
		final String kerberosPrincipalInstanceName = props.getOptional(PROP_SOLACE_KERBEROS_INSTANCE_NAME, "");
		final String kerberosJaasLoginContextName = props.getOptional(PROP_SOLACE_KERBEROS_JAAS_CONTEXT_NAME, "");
		final String kerberosUserNameOnBroker = props.getOptional(PROP_SOLACE_KERBEROS_USERNAME_ON_BROKER, "");
		final String oauthAccessToken = props.getOptional(PROP_SOLACE_OAUTH_ACCESS_TOKEN, "");
		final String oauthIssuerIdentifier = props.getOptional(PROP_SOLACE_OAUTH_ISSUER_IDENTIFIER, "");

		if (useTLS || !SH.isEmpty(keystoreURL) || !SH.isEmpty(keystorePassword)) {
			//Client Certificate Authentication
			log.info("Authenticating Solace with Client Certificate");
			builder = builder.withAuthenticationStrategy(ClientCertificateAuthentication.of(keystoreURL, keystorePassword));
		} else if (!SH.isEmpty(kerberosPrincipalInstanceName) && !SH.isEmpty(kerberosJaasLoginContextName) && !SH.isEmpty(kerberosUserNameOnBroker)) {
			//Kerberos Authentication
			log.info("Authenticating Solace with Kerberos");
			builder = builder.withAuthenticationStrategy(Kerberos.of(kerberosPrincipalInstanceName, kerberosJaasLoginContextName).withUserName(kerberosUserNameOnBroker)
					.withMutualAuthentication().withReloadableJaasConfiguration());
		} else if (!SH.isEmpty(oauthAccessToken)) {
			//OAuth 2.0 Authentication
			log.info("Authenticating Solace with OAuth 2.0");
			builder = SH.isEmpty(oauthIssuerIdentifier) ? builder.withAuthenticationStrategy(OAuth2.of(oauthAccessToken))
					: builder.withAuthenticationStrategy(OAuth2.of(oauthAccessToken).withIssuerIdentifier(oauthIssuerIdentifier));
		} else {
			log.info("Authenticating Solace with Username and Password (Basic Authentication)");
			//Use basic authentication
			trySetProperty(properties, AuthenticationProperties.SCHEME_BASIC_USER_NAME, PROP_SOLACE_USERNAME);
			trySetProperty(properties, AuthenticationProperties.SCHEME_BASIC_PASSWORD, PROP_SOLACE_PASSWORD);
		}

		service = builder.fromProperties(properties).build().connect();
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
	}

	private void trySetProperty(final Properties properties, final String solacePropertyKey, final String propertyKey) {
		String val = props.getOptional(propertyKey, "");
		if (!SH.isEmpty(val))
			properties.setProperty(solacePropertyKey, val);
	}

	@SuppressWarnings("rawtypes")
	private Parser<?> getProtobufParser(final String parserName, final String className) {
		try {
			Class<?> c = Class.forName(parserName);
			if (!SH.isEmpty(className))
				c = RH.getDeclaredClass(c, className);
			return (Parser) RH.getStaticField(c, "PARSER");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not get Google Protobuf PARSER for " + className, e);
		}
	}

	//Expected format: col1=int,col2=string,col3=double,...
	private void buildTableSchema(final String mapping) {
		columnMapping = new HashMap<String, FHSetter>();
		List<String> colMaps = SH.splitToList(",", mapping);
		for (String colMap : colMaps) {
			//Extract column name and column type
			List<String> colType = SH.splitToList("=", colMap);
			if (colType.size() != 2)
				throw new RuntimeException("Failed to parse column name and column type: " + colMap);
			final String columnName = SH.trim(colType.get(0));
			final String columnType = SH.toLowerCase(SH.trim(colType.get(1)));
			switch (columnType) {
				case "string":
				case "str":
					columnMapping.put(columnName, new String_FHSetter(columnName));
					break;
				case "integer":
				case "int":
					columnMapping.put(columnName, new Int_FHSetter(columnName));
					break;
				case "short":
					columnMapping.put(columnName, new Short_FHSetter(columnName));
					break;
				case "long":
					columnMapping.put(columnName, new Long_FHSetter(columnName));
					break;
				case "float":
					columnMapping.put(columnName, new Float_FHSetter(columnName));
					break;
				case "double":
					columnMapping.put(columnName, new Double_FHSetter(columnName));
					break;
				case "char":
				case "character":
					columnMapping.put(columnName, new Char_FHSetter(columnName));
					break;
				case "bool":
				case "boolean":
					columnMapping.put(columnName, new Bool_FHSetter(columnName));
					break;
				default:
					throw new UnsupportedOperationException("Unsupported column type: " + columnType);
			}
		}
	}

	//Setter classes
	private static class Double_FHSetter extends FHSetter {

		public Double_FHSetter(final String key) {
			super(key);
		}
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendDouble(key, Caster_Double.INSTANCE.cast(val));
		}
	}

	private static class Float_FHSetter extends FHSetter {

		public Float_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendFloat(key, Caster_Float.INSTANCE.cast(val));
		}
	}

	private static class Long_FHSetter extends FHSetter {

		public Long_FHSetter(final String key) {
			super(key);
		}
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendLong(key, Caster_Long.INSTANCE.cast(val));
		}
	}

	private static class Short_FHSetter extends FHSetter {

		public Short_FHSetter(final String key) {
			super(key);
		}
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendShort(key, Caster_Short.INSTANCE.cast(val));
		}
	}

	private static class Bool_FHSetter extends FHSetter {

		public Bool_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendBoolean(key, Caster_Boolean.INSTANCE.cast(val));
		}
	}

	private static class Char_FHSetter extends FHSetter {

		public Char_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendChar(key, Caster_Character.INSTANCE.cast(val));
		}
	}

	private static class Int_FHSetter extends FHSetter {

		public Int_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendInt(key, Caster_Integer.INSTANCE.cast(val));
		}
	}

	private static class String_FHSetter extends FHSetter {

		public String_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendString(key, val.toString());
		}
	}

	private static abstract class FHSetter {

		final String key;

		public FHSetter(final String key) {
			this.key = key;
		}
		public abstract void set(final AmiRelayMapToBytesConverter c, final Object val);
	}

	private class PlainTextParser {
		private String colDelimiter;
		private String keyValDelimiter;

		public PlainTextParser(String _colDelimiter, String _keyValDelimiter) {
			this.colDelimiter = _colDelimiter;
			this.keyValDelimiter = _keyValDelimiter;
		}

		public Map<String, String> parse(String message) {
			Map<String, String> output = new HashMap<String, String>();
			String[] cols = SH.split(this.colDelimiter, message);
			for (String col : cols) {
				if (SH.isnt(col)) {
					continue;
				}
				String[] keyVal = SH.split(this.keyValDelimiter, col);
				if (keyVal.length != 2) {
					LH.warning(log, "Invalid col input at: " + col);
					continue;
				}
				output.put(keyVal[0], keyVal[1]);
			}

			return output;
		}
	}

}