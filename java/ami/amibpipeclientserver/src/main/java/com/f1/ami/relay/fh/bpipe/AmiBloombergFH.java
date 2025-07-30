package com.f1.ami.relay.fh.bpipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.bloomberglp.blpapi.AuthApplication;
import com.bloomberglp.blpapi.AuthOptions;
import com.bloomberglp.blpapi.CorrelationID;
import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.EventHandler;
import com.bloomberglp.blpapi.Identity;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;
import com.bloomberglp.blpapi.Names;
import com.bloomberglp.blpapi.Schema.Datatype;
import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.SessionOptions;
import com.bloomberglp.blpapi.Subscription;
import com.bloomberglp.blpapi.SubscriptionList;
import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;

public class AmiBloombergFH extends AmiFHBase {
	static Handler messagehandler;
	private static final Logger Log = LH.get();

	//Corresponds to local properties settings
	private static final String PROP_HOST = "host";
	private static final String PROP_PORT = "port";
	private static final String PROP_SECONDARY_HOST = "secondary_host";
	private static final String PROP_SECONDARY_PORT = "secondary_port";
	private static final String PROP_RECONNECT = "reconnect";
	private static final String PROP_APP_NAME = "application.name";
	private static final String PROP_TICKERS = "level1.tickers";
	private static final String PROP_FIELDS = "level1.fields";

	private static final String PROP_TICKERS_LEVEL2 = "level2.tickers";
	private static final String PROP_FIELDS_LEVEL2 = "level2.type";

	private String host;
	private String host2;
	private String appName;
	private int port;
	private int port2;
	private int reconnect;
	private Session session;
	private String tickers;
	private String fields;
	private String tickersLevel2;
	private String fieldsLevel2;

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		try {
			session.stop();
		} catch (InterruptedException e) {
			LH.warning(Log, "CANNOT STOP SESSION");
		}
	}

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);

		//Required Properties 
		if (this.props.getProperty(PROP_HOST) == null)
			LH.warning(Log, "Host Property missing");
		if (this.props.getProperty(PROP_PORT) == null)
			LH.warning(Log, "Port Property Missing");
		if (this.props.getProperty(PROP_APP_NAME) == null)
			LH.warning(Log, "Bloomberg App Name Property Missing");

		this.host = this.props.getRequired(PROP_HOST);
		this.port = Integer.valueOf(this.props.getRequired(PROP_PORT));
		this.appName = this.props.getRequired(PROP_APP_NAME);

		//Optional Properties
		this.host2 = this.props.getOptional(PROP_SECONDARY_HOST);
		//Catches null error if secondary port and reconnect are not set 
		if (this.props.getOptional(PROP_SECONDARY_PORT) != null)
			this.port2 = Integer.valueOf(this.props.getRequired(PROP_PORT));
		if (this.props.getOptional(PROP_RECONNECT) != null)
			this.reconnect = Integer.valueOf(this.props.getOptional(PROP_RECONNECT));
		this.tickers = this.props.getOptional(PROP_TICKERS);
		this.fields = this.props.getOptional(PROP_FIELDS);
		this.tickersLevel2 = this.props.getOptional(PROP_TICKERS_LEVEL2);
		this.fieldsLevel2 = this.props.getOptional(PROP_FIELDS_LEVEL2);

		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
		createSession();
		startSubscription(tickers, fields);
		startSubscriptionLevel2(tickersLevel2, fieldsLevel2);
	}

	public AmiBloombergFH() {
		messagehandler = new Handler();
	}

	private void createSession() {
		if (this.host == null || this.appName == null) {
			return;
		}
		SessionOptions sessionOptions = new SessionOptions();
		SessionOptions.ServerAddress[] servers = new SessionOptions.ServerAddress[2];
		servers[0] = new SessionOptions.ServerAddress(host, port);
		if (host2 != null && this.props.getOptional(PROP_SECONDARY_PORT) != null)
			servers[1] = new SessionOptions.ServerAddress(host2, port2);
		else
			servers[1] = new SessionOptions.ServerAddress(host, port);
		sessionOptions.setServerAddresses(servers);
		sessionOptions.setAutoRestartOnDisconnection(true);
		sessionOptions.setNumStartAttempts(reconnect);
		CorrelationID authCorrelationId = new CorrelationID(appName);
		AuthOptions authOptions = new AuthOptions(new AuthApplication(appName));

		sessionOptions.setSessionIdentityOptions(authOptions, authCorrelationId);

		Session session = new Session(sessionOptions, new SubscriptionEventHandler());
		this.session = session;

		try {
			session.start();
			session.generateAuthorizedIdentity(authOptions);
		} catch (Throwable e) {
			LH.warning(Log, "CANNOT START SESSION", e);
			return;
		}

		try {
			session.openService("//blp/mktdata");
		} catch (Throwable e) {
			LH.warning(Log, "FAILED TO OPEN SERVICE MARKET DATA", e);
			return;
		}
	}

	private void startSubscription(String tickers, String fields) {
		if (tickers == null || fields == null) {
			return;
		}

		CorrelationID cID = new CorrelationID(this.appName);
		Identity identity = null;
		try {
			identity = session.getAuthorizedIdentity(cID);
		} catch (Throwable e) {
			LH.warning(Log, "IDENTITY NOT FOUND", e);
		}
		SubscriptionList subscriptions = new SubscriptionList();

		String[] tickersList = tickers.split(",");
		for (String ticker : tickersList) {
			subscriptions.add(new Subscription("//blp/mktdata/ticker/" + ticker + "?fields=" + fields, new CorrelationID(appName + "_fields_" + fields + "_ticker_" + ticker)));
		}
		try {
			session.subscribe(subscriptions, identity);
		} catch (Throwable e) {
			LH.warning(Log, "Failed to subscribe", e);
		}
	}

	public void startSubscriptionLevel2(String ticker, String type) {
		if (ticker == null || type == null) {
			return;
		}

		Identity identity = null;
		CorrelationID cID = new CorrelationID(appName);
		try {
			identity = session.getAuthorizedIdentity(cID);
		} catch (Throwable e) {
			LH.warning(Log, "IDENTITY NOT FOUND", e);
		}
		CorrelationID correlationID = new CorrelationID(this.getName() + "_type_" + type + "_ticker_" + ticker);
		SubscriptionList subscriptions = new SubscriptionList();
		String subString = "//blp/mktdepthdata/ticker/" + ticker + "?type=" + type;
		Subscription subscription = new Subscription(subString, correlationID);
		subscriptions.add(subscription);
		try {
			session.subscribe(subscriptions, identity);
		} catch (Throwable e) {
			LH.warning(Log, "SUBSCRIPTION FAILED", e);
		}
	}

	static class SubscriptionEventHandler implements EventHandler {
		@Override
		public void processEvent(Event event, Session session) {
			MessageIterator msgIter = event.messageIterator();
			while (msgIter.hasNext()) {
				Message msg = msgIter.next();
				if (msg.messageType().equals("MarketDepthUpdates")) { //FOR LEVEL 2 STREAMING
					Element e = msg.asElement();
					Map<String, Object> v = toMapViewLevel2(e);
					v.put("ticker", msg.correlationID().toString().substring(msg.correlationID().toString().lastIndexOf("ticker_") + 7));
					AmiBloombergFH.messagehandler.sendMessage("BpipeLevel2", null, v, msg.timeReceivedMillis());

				} else if (event.eventType() == Event.EventType.SUBSCRIPTION_DATA) { //FOR LEVEL 1 STREAMING
					Element e = msg.asElement();
					Map<String, Object> v = toMapView(e);
					v.put("ticker", msg.correlationID().toString().substring(msg.correlationID().toString().lastIndexOf("ticker_") + 7));
					AmiBloombergFH.messagehandler.sendMessage("BpipeLevel1", null, v, msg.timeReceivedMillis());
				}

				checkStatus(event, msg);

			}
		}

		private void checkStatus(Event event, Message msg) {
			if (event.eventType() == Event.EventType.SUBSCRIPTION_STATUS) {
				if (msg.messageType() == Names.SUBSCRIPTION_FAILURE)
					LH.warning(Log, "SUBSCRIPTION FAILED: " + msg.asElement());
			} else if (event.eventType() == Event.EventType.SESSION_STATUS) {
				if (msg.messageType() == Names.SESSION_STARTUP_FAILURE)
					LH.warning(Log, "SESSION STATUS: " + msg.asElement());
				else if (msg.messageType() == Names.SESSION_CONNECTION_DOWN || msg.messageType() == Names.SESSION_TERMINATED)
					LH.warning(Log, "SESSION STATUS: " + msg.asElement());
			} else if (event.eventType() == Event.EventType.SERVICE_STATUS) {
				if (msg.messageType() == Names.SERVICE_OPEN_FAILURE)
					LH.warning(Log, "SERVICE STATUS: " + msg.asElement());
			} else if (event.eventType() == Event.EventType.AUTHORIZATION_STATUS) {
				if (msg.messageType() == Names.AUTHORIZATION_FAILURE || msg.messageType() == Names.AUTHORIZATION_REVOKED)
					LH.warning(Log, "AUTHORIZATION STATUS: " + msg.asElement());
			} else {
				return;
			}
		}

		public Map<String, Object> toMapView(Element e) {
			Map<String, Object> m = new HashMap<String, Object>();
			for (int i = 0; i < e.numElements(); i++) {
				String fieldName = e.getElement(i).name().toString();
				Element element = e.getElement(i);
				Object value = processType(element);
				m.put(fieldName, value);
			}
			return m;
		}

		public Map<String, Object> toMapViewLevel2(Element e) {
			Map<String, Object> m = new HashMap<String, Object>();
			for (int i = 0; i < e.numElements(); i++) {
				String fieldName = e.getElement(i).name().toString();
				Element element = e.getElement(i);
				if (fieldName.startsWith("MBL_TABLE") || fieldName.startsWith("MBO_TABLE")) {
					toMapViewLevel2Helper(element, m);
				}
				if (!fieldName.startsWith("MBL_TABLE") && !fieldName.startsWith("MBO_TABLE")) {
					Object value = processType(element);
					m.put(fieldName, value);
				}
			}
			return m;
		}

		public void toMapViewLevel2Helper(Element e, Map<String, Object> m) {
			for (int i = 0; i < e.numElements(); i++) {
				String fieldName = e.getElement(i).name().toString();
				Element element = e.getElement(i);
				Object value = processType(element);
				m.put(fieldName, value);
			}
		}

		public Object processType(Element e) {
			if (e.datatype() == Datatype.STRING || e.datatype() == Datatype.ENUMERATION)
				return e.getValueAsString();
			else if (e.datatype() == Datatype.INT32)
				return e.getValueAsInt32();
			else if (e.datatype() == Datatype.INT64)
				return e.getValueAsInt64();
			else if (e.datatype() == Datatype.FLOAT32)
				return e.getValueAsFloat32();
			else if (e.datatype() == Datatype.FLOAT64)
				return e.getValueAsFloat64();
			else if (e.datatype() == Datatype.BOOL)
				return e.getValueAsBool();
			else if (e.datatype() == Datatype.CHAR)
				return e.getValueAsChar();
			else if (e.datatype() == Datatype.DATE)
				return (long) e.getValueAsDate().calendar().getTimeInMillis();
			else if (e.datatype() == Datatype.DATETIME)
				return (long) e.getValueAsDatetime().calendar().getTimeInMillis();
			else if (e.datatype() == Datatype.TIME)
				return (long) e.getValueAsTime().calendar().getTimeInMillis();
			else
				return e.getValueAsString();
		}

	}

	class Handler implements MessageHandler {
		private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
		private Map<String, Object> msg = new HashMap<String, Object>();
		private StringBuilder error = new StringBuilder();
		private final Logger log = LH.get();

		public Handler() {
		}
		public void sendMessage(String subscription, String key, Object value, long timestamp) {
			try {
				msg.clear();
				error.setLength(0);
				if (!parseMessage(value, msg, error)) {
					LH.warning(log, "Error processing data: ", value, " ==> ", error);
					return;
				}
				String id = null;
				byte[] b = converter.toBytes(msg);
				String type = subscription;
				getAmiRelayIn().onObject(-1, id, type, 0, b);
			} catch (Exception e) {
				LH.warning(log, "Error for ", value, " ==> ", e);
			}
		}
	}

	public static boolean parseMessage(Object value, Map<String, Object> parts, StringBuilder errorSink) {
		Map<String, Object> map = (Map<String, Object>) value;
		try {
			for (Entry<String, Object> field : map.entrySet()) {
				Object fieldValue = field.getValue();
				parts.put(field.getKey(), toReadable(fieldValue));
			}
		} catch (Exception e) {
			errorSink.append(e.getMessage());
			return false;
		}
		return true;
	}

	private static Object toReadable(Object v) {
		if (v instanceof Number) {
			if (OH.isFloat(v.getClass()))
				v = ((Number) v).doubleValue();
			else if (!(v instanceof Integer || v instanceof Long || v instanceof Short || v instanceof Byte))
				v = ((Number) v).longValue();
		} else if (v != null && !(v instanceof Boolean)) {
			v = v.toString();
		}
		return v;
	}

	protected Map<String, Object> processMessage(Map<String, Object> parts, String topic2) {
		return parts;
	}

}
