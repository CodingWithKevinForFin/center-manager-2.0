package com.f1.ami.relay.fh.iress;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.feedos.api.core.Credentials;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.core.ProxyFeedosTCP;
import com.feedos.api.core.Session;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.ListOfTagValue;
import com.feedos.api.requests.MarketBranchId;
import com.feedos.api.requests.RequestSender;

public class AmiIressFH extends AmiFHBase {
	private Handler messagehandler;
	private static final Logger Log = LH.get();

	//Corresponds to local properties settings
	private static final String PROP_HOST = "host";
	private static final String PROP_PORT = "port";
	private static final String PROP_USERNAME = "username";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_HEARTBEAT = "heartbeat";
	private static final String PROP_MARKET_INFO = "marketInfo";

	private static final String PROP_ENABLE_LEVEL1 = "level1.enable";
	private static final String PROP_LEVEL1_INSTRUMENTS = "level1.instruments";
	private static final String PROP_LEVEL1_TAGS = "level1.tags";

	private static final String PROP_ENABLE_MBL = "MBL.enable";
	private static final String PROP_MBL_INSTRUMENTS = "MBL.instruments";
	private static final String PROP_MBL_DEPTH = "MBL.depth";

	private String host;
	private int port;
	private String level1Instruments;
	private String MBLInstruments;
	private int MBLDepth;
	private String level1Tags;
	private String password;
	private String username;
	private Session session = new Session();
	private int subscriptionId;
	private boolean heartbeat;
	private String[] marketIds;

	private boolean enableLevel1;
	private boolean enableMBL;

	private IressSessionObserver sessionObserver;
	private RequestSender async_requester = new RequestSender(session, 0, 10000);

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		Session.init_api("sub_l1");

		if (this.props.getProperty(PROP_HOST) == null || this.props.getProperty(PROP_PORT) == null || this.props.getProperty(PROP_USERNAME) == null
				|| this.props.getProperty(PROP_PASSWORD) == null || this.props.getProperty(PROP_MARKET_INFO) == null) {
			LH.severe(Log, "Failed to start IRESS FH. Missing required property");
			return;
		}

		//Required 
		host = this.props.getRequired(PROP_HOST);
		port = Integer.valueOf(this.props.getRequired(PROP_PORT));
		password = this.props.getRequired(PROP_PASSWORD);
		username = this.props.getRequired(PROP_USERNAME);
		marketIds = this.props.getRequired(PROP_MARKET_INFO).split(",");

		//Optional
		heartbeat = Boolean.valueOf(this.props.getOptional(PROP_HEARTBEAT));

		enableLevel1 = Boolean.valueOf(this.props.getOptional(PROP_ENABLE_LEVEL1));
		level1Instruments = this.props.getOptional(PROP_LEVEL1_INSTRUMENTS);
		level1Tags = this.props.getOptional(PROP_LEVEL1_TAGS);

		enableMBL = Boolean.valueOf(this.props.getOptional(PROP_ENABLE_MBL));
		MBLInstruments = this.props.getOptional(PROP_MBL_INSTRUMENTS);
		MBLDepth = Integer.valueOf(this.props.getOptional(PROP_MBL_DEPTH));

		sessionObserver = new IressSessionObserver(heartbeat);

		//Connects to FeedOS Server
		int rc = session.open(sessionObserver, new ProxyFeedosTCP(host, port, new Credentials(username, password)), 0);
		if (rc != Constants.RC_OK) {
			LH.severe(Log, "Cannot connect: " + Constants.getErrorCodeName(rc));
			return;
		}

		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();

		startSubscriptions();
	}

	public AmiIressFH() {
		messagehandler = new Handler();
	}

	public static PolymorphicInstrumentCode parseInstrumentCode(String instCode) {
		PolymorphicInstrumentCode p = null;
		if (instCode.contains("@")) { // MIC@LocalCodeStr
			p = new PolymorphicInstrumentCode(instCode);
		} else if (instCode.contains("/")) { // MktId/InstrId
			int idx = instCode.indexOf('/');
			int marketId = Integer.parseInt(instCode.substring(0, idx));
			int localCode = Integer.parseInt(instCode.substring(idx + 1));
			int internalCode = PolymorphicInstrumentCode.build_internal_code(marketId, localCode, 0);
			p = new PolymorphicInstrumentCode(internalCode);
		}
		return p;
	}

	private PolymorphicInstrumentCode[] createInstrumentList(String instruments) {
		String instrumentListInput[] = instruments.split(",");
		PolymorphicInstrumentCode[] instrumentsList = new PolymorphicInstrumentCode[instrumentListInput.length];

		for (int i = 0; i < instrumentListInput.length; i++) {
			PolymorphicInstrumentCode p = parseInstrumentCode(instrumentListInput[i]);
			instrumentsList[i] = p;
		}
		return instrumentsList;
	}

	private void startSubscriptions() {

		PolymorphicInstrumentCode[] instrumentsLevel1 = createInstrumentList(level1Instruments);
		PolymorphicInstrumentCode[] instrumentsMBL = createInstrumentList(MBLInstruments);

		HashSet<Integer> trackedTags = new HashSet<>();
		for (String tag : level1Tags.split(",")) {
			trackedTags.add(Constants.getTagNum(tag));
		}

		ReceiverDownloadSubscribe receiver2 = new ReceiverDownloadSubscribe(instrumentsLevel1, instrumentsMBL, MBLDepth, messagehandler, trackedTags, async_requester, enableLevel1,
				enableMBL);
		MarketBranchId[] branch_ids = new MarketBranchId[marketIds.length];
		for (int i = 0; i < marketIds.length; i++) {
			int idx = marketIds[i].indexOf("|");
			String marketId = marketIds[i].substring(0, idx);
			String securityType = marketIds[i].substring(idx + 1, marketIds[i].length());
			branch_ids[i] = new MarketBranchId(Constants.getFOSMarketId(marketId), securityType, "");
		}
		ListOfTagValue filter = new ListOfTagValue();

		//Starts getting referential data (symbol, description, etc)
		async_requester.asyncRefDownloadAndSubscribe_start(receiver2, new String("user context to distinguish requests"), branch_ids, filter, null, 0, true, true, true, true);
	}

	public void closeSession() {
		try {
			async_requester.asyncQuotSubscribeInstrumentsL1_stop(subscriptionId);
			session.close();
		} catch (Throwable e) {
			LH.warning(Log, "Cannot stop session" + e);
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
