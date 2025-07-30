package com.f1.ami.web.rt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.bloomberglp.blpapi.AuthOptions;
import com.bloomberglp.blpapi.CorrelationID;
import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Identity;
import com.bloomberglp.blpapi.Schema.Datatype;
import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.Subscription;
import com.bloomberglp.blpapi.SubscriptionList;
import com.f1.ami.web.AmiWebAbstractRealtimeProcessor;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebObjectFields;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.bpipe.plugin.BPIPEPlugin;
import com.f1.base.CalcFrame;
import com.f1.base.IterableAndSize;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

public class AmiWebRealtimeProcessor_BPIPE extends AmiWebAbstractRealtimeProcessor {
	BasicCalcTypes paramToSchema = new BasicCalcTypes();
	BPIPEPlugin b;
	Session session;
	AuthOptions authOptions;
	CorrelationID authCorrelationId;

	private String options = "";
	private Boolean keepOutput = true;

	private final static Logger log = LH.get();
	private HasherMap<AmiWebObject, Output> mapOutput = new HasherMap<AmiWebObject, Output>();

	public AmiWebRealtimeProcessor_BPIPE(AmiWebService service) {
		super(service);
	}
	public AmiWebRealtimeProcessor_BPIPE(AmiWebService service, String alias) {
		super(service, alias);
	}
	public AmiWebRealtimeProcessor_BPIPE(AmiWebService service, String alias, String setOptions, Boolean setKeepOutput) {
		super(service, alias);
		this.options = setOptions;
		this.keepOutput = setKeepOutput;
		b = this.getService().getBPIPE();
		if (b != null)
			this.session = b.getSession();
		else if (!SH.equals(this.getService().getPortletManager().getTools().getOptional("bpipe_plugin_enabled"), "true")) {
			LH.severe(log, "bpipe_plugin_enabled not set to true in properties");
		}
	}
	@Override
	public String getType() {
		return AmiWebRealtimeProcessorPlugin_BPIPE.PLUGIN_ID;
	}

	@Override
	public IterableAndSize<AmiWebObject> getAmiObjects() {
		return (IterableAndSize) mapOutput.values();
	}

	@Override
	public com.f1.utils.structs.table.stack.BasicCalcTypes getRealtimeObjectschema() {
		return this.paramToSchema;
	}

	@Override
	public com.f1.utils.structs.table.stack.BasicCalcTypes getRealtimeObjectsOutputSchema() {
		return this.paramToSchema;
	}

	public void addToSchema(String varName, Class<Map> type) {
		this.paramToSchema.putIfAbsent(varName, type);
	}

	@Override
	public void rebuild() {
		fireOnAmiEntitiesCleared();
	}

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
		rebuild();
	}

	@Override
	public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		Output o = new Output(entity, getService().getNextAmiObjectUId());
		if (keepOutput) {
			Entry<AmiWebObject, Output> node = mapOutput.getOrCreateEntry(entity);
			node.setValue(o);
		}
		fireAmiEntityAdded(o);
	}

	@Override
	public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {

	}

	@Override
	public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		mapOutput.remove(entity);
		fireAmiEntityRemoved(entity);
	}

	private Object noNull(Object val, Object _default) {
		return val == null ? _default : val;
	}

	@Override
	public void init(String alias, Map<String, Object> configuration) {
		super.init(alias, configuration);
		this.options = (String) noNull(configuration.get("options"), "");
		this.keepOutput = (Boolean) noNull(configuration.get("keepOutput"), true);
		b = this.getService().getBPIPE();
		if (b != null)
			this.session = b.getSession();
		else if (!SH.equals(this.getService().getPortletManager().getTools().getOptional("bpipe_plugin_enabled"), "true")) {
			LH.severe(log, "bpipe_plugin_enabled not set to true in properties");
		}
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("options", options);
		r.put("keepOutput", keepOutput);
		return r;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.getRealtimeObjectsOutputSchema();
	}
	public String getAppName(boolean override) {
		return b.getAppName(override);
	}
	public String getAuthCorrelationId(boolean override) {
		return b.getAuthCorrelationId(override);
	}
	public String getHostPrimary(boolean override) {
		return b.getHostPrimary(override);
	}
	public int getPortPrimary(boolean override) {
		return b.getPortPrimary(override);
	}
	public String getHostSecondary(boolean override) {
		return b.getHostSecondary(override);
	}
	public int getPortSecondary(boolean override) {
		return b.getPortSecondary(override);
	}
	public int getReconnect(boolean override) {
		return b.getReconnect(override);
	}
	public boolean getKeepOutput() {
		return this.keepOutput;
	}
	public String getOptions() {
		return this.options;
	}
	public void setKeepOutput(boolean setKeepOutput) {
		this.keepOutput = setKeepOutput;
	}
	public void setOptions(String setOptions) {
		this.options = setOptions;
	}

	public void startSubscription(String ticker, String fields) {
		Identity identity = null;
		CorrelationID cID = new CorrelationID(this.getAuthCorrelationId(true));
		try {
			identity = session.getAuthorizedIdentity(cID);
		} catch (Throwable e) {
			LH.severe(log, "IDENTITY NOT FOUND", e);
		}
		CorrelationID correlationID = new CorrelationID(this.getName() + "_" + getService().getUserName() + "_ticker_" + ticker);
		SubscriptionList subscriptions = new SubscriptionList();
		Subscription subscription = new Subscription(ticker, fields, this.options, correlationID);
		subscriptions.add(subscription);
		try {
			session.subscribe(subscriptions, identity);
			b.addProcessorSubscription(correlationID, this);
		} catch (Throwable e) {
			if (b.getActiveSubcriptions().containsKey(correlationID)) {
				LH.warning(log, "Already subscribed to " + ticker + ". Please unsubscribe first.");
			}
			LH.severe(log, "SUBSCRIPTION FAILED", e);
		}
	}

	public void startSubscriptionLevel2(String ticker, String type) {
		Identity identity = null;
		CorrelationID cID = new CorrelationID(this.getAuthCorrelationId(true));
		try {
			identity = session.getAuthorizedIdentity(cID);
		} catch (Throwable e) {
			LH.severe(log, "IDENTITY NOT FOUND", e);
		}
		CorrelationID correlationID = new CorrelationID(this.getName() + "_" + getService().getUserName() + "_type_" + type + "_ticker_" + ticker);
		SubscriptionList subscriptions = new SubscriptionList();
		String subString = "//blp/mktdepthdata/ticker/" + ticker + "?type=" + type;
		Subscription subscription = new Subscription(subString, correlationID);
		subscriptions.add(subscription);
		try {
			session.subscribe(subscriptions, identity);
			b.addProcessorSubscription(correlationID, this);
		} catch (Throwable e) {
			if (b.getActiveSubcriptions().containsKey(correlationID)) {
				LH.warning(log, "Already subscribed to " + ticker + ". Please unsubscribe first.");
			}
			LH.severe(log, "SUBSCRIPTION FAILED", e);
		}
	}

	@SuppressWarnings("deprecation")
	public void unsubscribe(String ticker) {
		CorrelationID cID = new CorrelationID(this.getName() + "_" + getService().getUserName() + "_ticker_" + ticker);
		if (!b.getActiveSubcriptions().containsKey(cID)) {
			LH.warning(log, "Cannot find subscription to unsubscribe from");
			return;
		}
		try {
			this.session.unsubscribe(cID);
			b.removeProcessorSubscription(cID);
		} catch (Throwable e) {
			LH.severe(log, "UNSUBCRIBING FAILED", e);
		}
	}

	public void unsubscribeAll() {
		HashSet<CorrelationID> s = new HashSet<CorrelationID>();
		for (Entry<CorrelationID, String> e : b.getActiveSubcriptions().entrySet()) {
			if (SH.equals(e.getValue(), this.getName())) {
				try {
					this.session.unsubscribe(e.getKey());
					s.add(e.getKey());
				} catch (Throwable e1) {
					LH.severe(log, e1);
				}
			}
		}
		b.getActiveSubcriptions().keySet().removeAll(s);
	}

	public List<String> getSubscriptions() {
		ArrayList<String> subscriptions = new ArrayList<String>();
		for (Entry<CorrelationID, String> e : b.getActiveSubcriptions().entrySet()) {
			if (SH.equals(e.getValue(), this.getName())) {
				int index = e.getKey().toString().indexOf("ticker_");
				subscriptions.add(e.getKey().toString().substring(index + 7));
			}
		}
		return subscriptions;
	}

	public Map toMapView(Element e) {
		Map<String, Object> m = new HashMap<String, Object>();
		for (int i = 0; i < e.numElements(); i++) {
			String fieldName = e.getElement(i).name().toString();
			Element element = e.getElement(i);
			Object value = processType(element);
			addToSchema(fieldName, (Class<Map>) value.getClass());
			m.put(fieldName, value);
		}
		return m;
	}

	public Map toMapViewLevel2(Element e) {
		Map<String, Object> m = new HashMap<String, Object>();
		for (int i = 0; i < e.numElements(); i++) {
			String fieldName = e.getElement(i).name().toString();
			Element element = e.getElement(i);
			if (fieldName.startsWith("MBL_TABLE") || fieldName.startsWith("MBO_TABLE")) {
				toMapViewLevel2Helper(element, m);
			}
			if (!fieldName.startsWith("MBL_TABLE") && !fieldName.startsWith("MBO_TABLE")) {
				Object value = processType(element);
				addToSchema(fieldName, (Class<Map>) value.getClass());
				m.put(fieldName, value);
			}
		}
		return m;
	}

	public void toMapViewLevel2Helper(Element e, Map m) {
		for (int i = 0; i < e.numElements(); i++) {
			String fieldName = e.getElement(i).name().toString();
			Element element = e.getElement(i);
			Object value = processType(element);
			addToSchema(fieldName, (Class<Map>) value.getClass());
			m.put(fieldName, value);
		}
	}

	public void sendMessage(Object value, long timestamp) {
		Map<String, Object> msg = new HashMap<String, Object>();
		StringBuilder error = new StringBuilder();
		try {
			msg.clear();
			error.setLength(0);
			if (!parseMessage(value, msg, error)) {
				LH.warning(log, "Error processing data: ", value, " ==> ", error);
				return;
			}
			Map<String, Object> map = (Map<String, Object>) value;
			Output m = new Output();
			m.fill(map);
			onAmiEntityAdded(null, m);
		} catch (Exception e) {
			LH.warning(log, "Error for ", value, " ==> ", e);
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

	public class Output extends HashMap<String, Object> implements AmiWebObject {

		private long uniqueId;
		private AmiWebObject entity;

		public Output() {

		}
		public Output(AmiWebObject entity, long uniqueId) {
			this.uniqueId = uniqueId;
			this.entity = entity;
			entity.fill((CalcFrame) this);
		}
		@Override
		public Object getParam(String param) {
			return this.get(param);
		}

		@Override
		public void fill(Map<String, Object> o) {
			this.putAll(o);
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			return null;
		}

		@Override
		public Object getValue(String key) {
			return get(key);
		}

		@Override
		public Object putValue(String key, Object value) {
			return super.put(key, value);
		}

		@Override
		public Class<?> getType(String var) {
			return getRealtimeObjectschema().getType(var);
		}

		@Override
		public boolean isVarsEmpty() {
			return false;
		}

		@Override
		public Iterable<String> getVarKeys() {
			return getRealtimeObjectschema().getVarKeys();
		}

		@Override
		public int getVarsCount() {
			return getRealtimeObjectschema().getVarsCount();
		}

		@Override
		public long getUniqueId() {
			return uniqueId;
		}

		@Override
		public String getObjectId() {
			return null;
		}

		@Override
		public long getId() {
			return uniqueId;
		}

		@Override
		public String getTypeName() {
			return getName();
		}

		@Override
		public void fill(CalcFrame sink) {
			for (String s : keySet())
				sink.putValue(s, get(s));
		}
	}

	@Override
	public Set<String> getLowerRealtimeIds() {
		return null;
	}
	@Override
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		return;
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
