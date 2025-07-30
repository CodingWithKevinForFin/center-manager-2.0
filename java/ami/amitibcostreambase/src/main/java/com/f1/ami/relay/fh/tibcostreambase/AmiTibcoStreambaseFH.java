package com.f1.ami.relay.fh.tibcostreambase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.streambase.sb.StreamBaseException;
import com.streambase.sb.client.ConnectionStatus;
import com.streambase.sb.client.DequeueResult;
import com.streambase.sb.client.StreamBaseClient;
import com.streambase.sb.client.StreamBaseURI;

public class AmiTibcoStreambaseFH extends AmiFHBase {
	public AmiTibcoStreambaseFH() {
	}

	private static final Logger log = LH.get();
	private static final String PROP_STREAMS = "streams";
	private static final String PROP_SERVER_URI = "server.uri";
	private static final String PROP_USERNAME = "username";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_USE_TLS = "useTLS";
	private static final String PROP_RECONNECT_TIMEOUT_MS = "reconnect.interval.ms";

	private String serverURI;
	private String[] streams;
	private static StreamBaseClient client = null;
	private String username;
	private String password;
	private boolean useTLS;
	private int reconnectIntervalMs;
	private ConnectionStatus streambaseState;
	private int lastEntry;

	@Override
	public void start() {
		super.start();
		try {
			new Thread(new Connector(), "Tibco Streambase connector").start();
		} catch (Exception e) {
			log.severe("Failed to start up the FH: " + e.getMessage());
		}
	}

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		this.serverURI = this.props.getRequired(PROP_SERVER_URI);
		this.streams = SH.split(',', this.props.getOptional(PROP_STREAMS));
		this.username = this.props.getOptional(PROP_USERNAME, "");
		this.password = this.props.getOptional(PROP_PASSWORD, "");
		this.useTLS = this.props.getOptional(PROP_USE_TLS, false);
		this.reconnectIntervalMs = this.props.getOptional(PROP_RECONNECT_TIMEOUT_MS, 5000);
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		streambaseState = com.streambase.sb.client.ConnectionStatus.DISCONNECTED;
		login();
	}

	public AmiTibcoStreambaseFH(StreamBaseURI uri) throws StreamBaseException {
		log.info("Connecting to " + uri.toString() + "...");
		String connectionURI = "tcp://" + username + ":" + password + "@" + serverURI;
		if (this.useTLS)
			client = new StreamBaseClient(connectionURI);
		else
			client = new StreamBaseClient(uri);
		log.info("Connected to " + uri.toString());
	}

	public void connect() {
		try {
			StreamBaseURI uri = new StreamBaseURI(serverURI);
			new AmiTibcoStreambaseFH(uri);
		} catch (StreamBaseException e) {
			log.severe(e.getMessage());
		}

		try {
			// Subscribe to streams
			for (int i = 0; i < streams.length; i++) {
				client.subscribe(streams[i]);
			}
			startDequeuing();
		} catch (StreamBaseException e) {
			log.severe("StreamBaseException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (StreamBaseException e) {
					log.severe("StreamBaseException while closing client: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public class Connector implements Runnable {
		@Override
		public void run() {
			while (streambaseState == ConnectionStatus.DISCONNECTED) {
				AmiTibcoStreambaseFH.this.connect();
				if (streambaseState == ConnectionStatus.DISCONNECTED)
					try {
						Thread.sleep(reconnectIntervalMs);
					} catch (InterruptedException e) {
						LH.warning(log, "Waiting for reconnect error for streambse, timeout: ", reconnectIntervalMs, e);
					}
			}
		}

	}

	private void startDequeuing() throws StreamBaseException {
		MessageHandler messagehandler = new Handler();
		DequeueResult dr = null;
		try {
			while ((dr = client.dequeue()) != null) {
				Iterator<com.streambase.sb.Tuple> tuples = dr.iterator();
				while (tuples.hasNext()) {
					lastEntry = lastEntry + 1;
					com.streambase.sb.Tuple t = tuples.next();
					long timestamp = this.getConnectionTime();
					Object value = t.toMapView();
					String key = String.valueOf(lastEntry);
					String name = dr.getStreamName();
					((Handler) messagehandler).sendMessage(name, key, value, timestamp);
				}
			}
		} catch (StreamBaseException e) {
			log.severe("StreamBaseException during dequeue:  " + e.getMessage());
			e.printStackTrace();
		}
		log.info("Client connection ended: dequeuing thread exiting.");
		return;
	}

	private class Handler implements MessageHandler {
		private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
		private Map<String, Object> msg = new HashMap<String, Object>();
		private StringBuilder error = new StringBuilder();

		public Handler() {
		}
		// sends the content of the message to AMI
		public void sendMessage(String stream, String key, Object value, long timestamp) {
			try {
				msg.clear();
				error.setLength(0);
				if (!parseMessage(value, msg, error)) {
					LH.warning(log, "Error processing data: ", value, " ==> ", error);
					return;
				}
				String id = null;
				byte[] b = converter.toBytes(msg);
				String type = getAmiTableName(stream);
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

	// converts any object to its particular type for AMI.
	// If it is not one of the primitive types, it returns a readable string representation
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