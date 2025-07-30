package com.f1.ami.web.dm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.amiscript.AmiWebScriptRunner;
import com.f1.ami.web.amiscript.AmiWebScriptRunnerListener;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.base.CalcFrame;
import com.f1.base.IterableAndSize;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.CronTab;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.Timer;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.ToDerivedString;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebDmsImpl implements AmiWebScriptRunnerListener, AmiWebDmListener, AmiWebDm, ToDerivedString {

	public static final String WHERES = "wheres";
	public static final String WHERE = "WHERE";
	public static final String RTEVENTS = "rtevents";
	public static final String CALLBACK_ONPROCESS = "onProcess";
	public static final String CALLBACK_ONCOMPLETE = "onComplete";
	public static final ParamsDefinition CALLBACK_DEF_ONPROCESS = new ParamsDefinition(CALLBACK_ONPROCESS, Object.class,
			"String WHERE,java.util.Map wheres,com.f1.ami.web.dm.AmiWebDmRealtimeEvent rtevents");
	public static final ParamsDefinition CALLBACK_DEF_ONCOMPLETE = new ParamsDefinition(CALLBACK_ONCOMPLETE, Object.class, "String WHERE,java.util.Map wheres");
	static {
		CALLBACK_DEF_ONCOMPLETE.addDesc("Called after onProcess executes successfully");
		CALLBACK_DEF_ONPROCESS.addDesc("Called when the datamodel is run. Note: The arguments passed in are dynamic based on the relationship that is executing the datamodel");
		CALLBACK_DEF_ONPROCESS.addParamDesc(0, "the WHERE variable as passed in from relationships. Note, this is equivilant to calling wheres.get(\"WHERE\")");
		CALLBACK_DEF_ONPROCESS.addParamDesc(0, "the variables as passed in from relationships");
		CALLBACK_DEF_ONPROCESS.addParamDesc(0, "Events for the subscription(s) (See Subscribe option)... Note, you should also check rtevents.getNext() in case of batch events");
	}
	private final static String CONFIG_QUERY_MODE_NONE = "none";
	private final static String CONFIG_QUERY_MODE_STARTUP = "startup";
	private final static String CONFIG_QUERY_MODE_STARTUP_VISIBLE = "startup,visible";
	private final static String CONFIG_QUERY_MODE_VISIBLE = "visible";
	private final static String CONFIG_QUERY_MODE_VISIBLEONCE = "visibleOnce";
	public final static String TEST_INPUT_VARS_CUST = "CUST";
	public final static String TEST_INPUT_VARS_OPEN = "OPEN";
	public final static String TEST_INPUT_VARS_PREFIX_LINK = "LINK:";
	public final static String TEST_INPUT_CUSTOM_VARS_DEFAULT = "String WHERE=\"true\";";
	public static final byte QUERY_ON_NONE = 0;
	public static final byte QUERY_ON_VISIBLE = 1;
	public static final byte QUERY_ON_VISIBLE_ONCE = 2;
	public static final byte QUERY_ON_STARTUP = 3;
	public static final byte QUERY_ON_STARTUP_AND_VISIBLE = 4;

	public static final Logger log = LH.get();

	final private Set<String> usedDatasources = new HashSet<String>();
	final private String dmId;
	final private List<AmiWebDmListener> listeners = new ArrayList<AmiWebDmListener>();
	final private List<AmiWebDmFilter> filters = new ArrayList<AmiWebDmFilter>();
	final private Set<String> upperDmNames = new HashSet<String>();
	final private Set<String> rtSources = new HashSet<String>();
	private int minRequeryMs;
	private int maxRequeryMs;
	private boolean isPlaying = true;
	private byte queryOnMode = QUERY_ON_NONE;
	private String aliasDotName;
	private String dmName;
	private AmiWebDmManagerImpl dmManager;
	private AmiWebAmiScriptCallbacks callbacks;

	private String testInputType = TEST_INPUT_VARS_OPEN;
	private String testInputCustomVars = TEST_INPUT_CUSTOM_VARS_DEFAULT;
	private long lastQueryStartTimeMillis;
	private long lastQueryEndTimeMillis;
	private long lastQueryStartTimeNanos;
	private AmiWebScriptRunner scriptRunner;
	private AmiWebDmRequest requestTableset = new AmiWebDmRequest();
	private Tableset responseTablesetBeforeFilter = new TablesetImpl();
	private Tableset responseTableset = new TablesetImpl();
	private boolean isRegisteredWithDmManager;
	private AmiWebDmRealtimeQueue realtimeQueue = new AmiWebDmRealtimeQueue(this);
	private boolean isQueuedForProcessing;
	private boolean isEdit = false;
	private long statsEvalsCount = 0;
	private long statsErrorsCount = 0;
	private long statsEvalTimeNanos = 0;
	private long statsEvalTimeMinNanos = -1;
	private long statsEvalTimeMaxNanos = -1;
	private long statsConsecutiveAutoRequeriesCount = 0;
	private Map<String, Object> vars = new LinkedHashMap<String, Object>();
	private String alias = "";

	public AmiWebDmsImpl(AmiWebDmManager dmManager, String alias, String dmName) {
		this.dmId = dmManager.getService().getPortletManager().generateId();

		this.dmManager = (AmiWebDmManagerImpl) dmManager;
		this.callbacks = new AmiWebAmiScriptCallbacks(dmManager.getService(), this);
		this.callbacks.registerCallbackDefinition(CALLBACK_DEF_ONPROCESS, (Map) CH.m(WHERE, "true"), this);
		this.callbacks.registerCallbackDefinition(CALLBACK_DEF_ONCOMPLETE);
		this.callbacks.setAmiLayoutAlias(alias);
		this.alias = alias;
		this.dmName = dmName;
		this.aliasDotName = dmName == null ? null : AmiWebUtils.getFullAlias(alias, dmName);
	}

	@Override
	public String getDefaultDatasource() {
		return getCallback_OnProcess().getDefaultDatasource();
	}

	@Override
	public void processRequest(AmiWebDmRequest request, AmiDebugManager debugManager) {
		processRequest(request, debugManager, false, false);
	}
	public void processRequest(AmiWebDmRequest request, AmiDebugManager debugManager, boolean isRequery, boolean forceQueue) {
		if (maxRequeryMs > 0)
			setIsPlay(true);
		for (String d : CH.sort(getLowerDmAliasDotNames())) {
			AmiWebDm dm = getDmManager().getDmByAliasDotName(d);
			if (dm.isCurrentlyRunning())
				return;
		}
		final long now = getDmManager().getService().getPortletManager().getNow();
		setRequestTableset(request);
		if (forceQueue || !canQueryAtThisTime(now) || !getDmManager().getService().getPrimaryWebManager().getIsEyeConnected()) {
			this.isQueuedForProcessing = true;
			getDmManager().queueRequest(this, new Tuple3<AmiWebDmRequest, AmiDebugManager, Boolean>(request, debugManager, isRequery));
			return;
		}
		processRequestNow(request, debugManager, isRequery);
	}
	public void onRtEvent() {
		if (isQueuedForProcessing || isEdit)
			return;
		for (String d : CH.sort(getLowerDmAliasDotNames())) {
			AmiWebDm dm = getDmManager().getDmByAliasDotName(d);
			if (dm.isCurrentlyRunning())
				return;
		}
		//		AmiWebDmRequest request = this.getRequestTableset()
		//		setRequestTableset(request);
		this.isQueuedForProcessing = true;
		getDmManager().queueRequest(this, new Tuple3<AmiWebDmRequest, AmiDebugManager, Boolean>(this.getRequestTableset(), dmManager.getService().getDebugManager(), true));
		return;
	}

	private Map<String, AmiWebRtTable> rtTables = new HashMap<String, AmiWebRtTable>();
	//	final private LongKeyMap<ColumnarRow> rows = new LongKeyMap<ColumnarRow>();
	private boolean isInRequery;

	public void processRequestNow(AmiWebDmRequest request, AmiDebugManager debugManager, boolean isRequery) {
		this.isQueuedForProcessing = false;
		final Map<String, Object> inputs = request.getVariablesForOnProcess(getDefaultTrue());
		AmiWebDmRealtimeEvent events = this.realtimeQueue.drainRealtimeEventsBuffer();
		inputs.put(RTEVENTS, events);
		CalcFrame v = this.getCallback_OnProcess().mapInputs(inputs);
		if (this.getCallback_OnProcess().hasError(true)) {
			this.fireDmError(new AmiWebDmError(this, null));
			return;
		}
		AmiWebScriptRunner runner = this.getCallback_OnProcess().executeReturnRunner(debugManager, v);
		if (runner == null)
			return;
		if (!this.rtSources.isEmpty()) {
			//			Tableset tableset = runner.getVars().getTableset();
			//			fillRealTimeTable(tableset);
			for (AmiWebDmRealtimeEvent event = events; event != null; event = event.next) {
				AmiWebRtTable rtTable = rtTables.get(event.getRealtimeId());
				if (rtTable != null) {
					switch (event.status) {
						case AmiWebDmRealtimeEvent.TRUNCATE:
							rtTable.clear();
							break;
						case AmiWebDmRealtimeEvent.SNAPSHOT:
						case AmiWebDmRealtimeEvent.INSERT:
							rtTable.addRow(event.getData());
							break;
						case AmiWebDmRealtimeEvent.UPDATE:
							rtTable.updateRow(event.getData());
							break;
						case AmiWebDmRealtimeEvent.DELETE:
							rtTable.removeRow(event.getData());
							break;
					}
				}
			}
		}

		runner.addListener(this);

		this.scriptRunner = runner;
		this.isInRequery = isRequery;
		if (!isRequery)
			this.statsConsecutiveAutoRequeriesCount = 0;
		else
			this.statsConsecutiveAutoRequeriesCount++;

		this.getDmManager().onDmsRunning(this);
		fireDmRunningQuery(isRequery);
		runner.runStep();
	}

	@Override
	public boolean isInRequery() {
		return this.isInRequery;
	}

	protected void processAutoRequery() {
		processRequest(getRequestTableset(), getDmManager().getService().getDebugManager(), true, false);
	}

	@Override
	public void close() {
		this.getCallbacks().close();
		if (this.isRegisteredWithDmManager) {
			this.isRegisteredWithDmManager = false;
			for (String s : CH.l(getLowerDmAliasDotNames())) {
				AmiWebDmsImpl dm = (AmiWebDmsImpl) this.dmManager.getDmByAliasDotName(s);
				dm.removeDmListener(this);
				dm.removeUpperDm(this.aliasDotName);
			}
			if (!this.listeners.isEmpty())
				LH.warning(log, "Removed DM '", this.getAmiLayoutFullAliasDotId(), "' that still has dependencies: " + this.listeners);
		}
		if (CH.isntEmpty(this.rtSources)) {
			AmiWebManagers webManagers = this.getDmManager().getService().getWebManagers();
			for (String rtid : this.rtSources)
				webManagers.getAmiObjectsByType(rtid).removeAmiListener(this.realtimeQueue);
			this.rtSources.clear();
		}
		this.upperDmNames.clear();
		this.usedDatasources.clear();
		this.listeners.clear();
		this.filters.clear();
		this.vars.clear();
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = new HashMap<String, Object>();
		r.put("lbl", dmName);
		r.put("callbacks", callbacks.getConfiguration());
		AmiWebUtils.putSkipEmpty(r, "crontab", this.crontabText);
		AmiWebUtils.putSkipEmpty(r, "crontabTz", this.crontabTimezoneText);
		r.put("test_input_type", this.testInputType);
		r.put("test_input_vars", this.testInputCustomVars);
		CH.putExcept(r, "minRequery", this.minRequeryMs, 0);
		CH.putExcept(r, "maxRequery", this.maxRequeryMs, 0);
		CH.putExcept(r, "queryMode", toQueryModeString(this.queryOnMode), "none");
		if (!this.usedDatasources.isEmpty())
			r.put("datasources", this.usedDatasources);
		ArrayList<String> t = new ArrayList<String>();
		for (String s : getLowerDmAliasDotNames())
			t.add(AmiWebUtils.getRelativeAlias(this.getAmiLayoutFullAlias(), s));
		//		r.put("lower", t);
		if (!this.rtSources.isEmpty()) {
			HashSet<String> rts = new HashSet<String>(this.rtSources.size());
			for (String rtSource : this.rtSources) {
				rts.add(AmiWebUtils.getRelativeRealtimeId(alias, rtSource));
			}
			r.put("subscribe", rts);
		}
		return r;
	}

	static public String toQueryModeString(byte mode) {
		switch (mode) {
			case QUERY_ON_NONE:
				return CONFIG_QUERY_MODE_NONE;
			case QUERY_ON_STARTUP:
				return CONFIG_QUERY_MODE_STARTUP;
			case QUERY_ON_STARTUP_AND_VISIBLE:
				return CONFIG_QUERY_MODE_STARTUP_VISIBLE;
			case QUERY_ON_VISIBLE:
				return CONFIG_QUERY_MODE_VISIBLE;
			case QUERY_ON_VISIBLE_ONCE:
				return CONFIG_QUERY_MODE_VISIBLEONCE;
		}
		return null;
	}
	static public byte parseQueryModeString(String s) {
		if (CONFIG_QUERY_MODE_NONE.equals(s))
			return QUERY_ON_NONE;
		if (CONFIG_QUERY_MODE_STARTUP.equals(s))
			return QUERY_ON_STARTUP;
		if (CONFIG_QUERY_MODE_STARTUP_VISIBLE.equals(s))
			return QUERY_ON_STARTUP_AND_VISIBLE;
		if (CONFIG_QUERY_MODE_VISIBLE.equals(s))
			return QUERY_ON_VISIBLE;
		if (CONFIG_QUERY_MODE_VISIBLEONCE.equals(s))
			return QUERY_ON_VISIBLE_ONCE;
		return QUERY_ON_NONE;
	}

	private void initV2(String alias, Map<String, Object> val, Map<String, String> origToNewPortletIdMapping, StringBuilder warningsSink) {
		Map<String, Object> config = new HashMap<String, Object>();
		config.put("test_input_type", val.get("test_input_type"));
		config.put("test_input_vars", val.get("test_input_vars"));
		config.put("test_row_lim", val.get("test_row_lim")); // new version doesn't have this can remove
		config.put("test_timeout", val.get("test_timeout")); // new version doesn't have this can remove
		CH.putNoNull(config, "to", val.get("to"));
		List<Object> lower = (List<Object>) CH.getOr(val, "lower", new ArrayList<Object>());
		config.put("lowerV2", lower);
		//		config.put("upper", val.get("upper"));
		config.put("vcfg", val.get("vcfg")); //variables config
		config.put("dmPos", val.get("dmPos"));
		config.put("lbl", val.get("lbl"));
		CH.putNoNull(config, "lm", val.get("lm"));
		if (val.containsKey("maxRequery"))
			config.put("maxRequery", val.get("maxRequery"));
		else
			config.put("maxRequery", val.get("qmax"));
		if (val.containsKey("minRequery"))
			config.put("minRequery", val.get("minRequery"));
		else
			config.put("minRequery", val.get("qmin"));
		String ds = CH.getOr(Caster_String.INSTANCE, val, "ds", null);
		if (ds != null) {
			ArrayList<Object> datasources = new ArrayList<Object>();
			datasources.add(ds);
			config.put("datasources", datasources);
		}

		String queryMode = CONFIG_QUERY_MODE_NONE;
		boolean queryOnStartup = CH.getOr(Caster_Boolean.PRIMITIVE, val, "qsup", true); // True False
		short requeryVisibleSetting = CH.getOr(Caster_Short.INSTANCE, val, "rqvis", (short) 0); //Always=1, Once=2, Off=0
		if (queryOnStartup == false) {
			if (requeryVisibleSetting == 0)
				queryMode = CONFIG_QUERY_MODE_NONE;
			else if (requeryVisibleSetting == 1)
				queryMode = CONFIG_QUERY_MODE_VISIBLE;
			else if (requeryVisibleSetting == 2)
				queryMode = CONFIG_QUERY_MODE_VISIBLEONCE;
		} else {
			if (requeryVisibleSetting == 0)
				queryMode = CONFIG_QUERY_MODE_STARTUP;
			else if (requeryVisibleSetting == 1)
				queryMode = CONFIG_QUERY_MODE_STARTUP_VISIBLE;
			else if (requeryVisibleSetting == 2)
				queryMode = CONFIG_QUERY_MODE_STARTUP;
		}

		config.put("queryMode", queryMode);

		Map<String, Object> callbacks = new LinkedHashMap<String, Object>();
		ArrayList<Object> entries = new ArrayList<Object>();
		Map<String, Object> onProcess = new LinkedHashMap<String, Object>();

		CH.putNoNull(onProcess, "name", "onProcess");
		CH.putNoNull(onProcess, "amiscript", val.get("amisc"));
		CH.putNoNull(onProcess, "defaultDs", ds);
		CH.putNoNull(onProcess, "screq", val.get("screq"));
		CH.putNoNull(onProcess, "schema", val.get("scres"));
		CH.putNoNull(onProcess, "limit", val.get("lm"));
		CH.putNoNull(onProcess, "timeout", val.get("to"));
		CH.putNoNull(onProcess, "hasDatamodel", true);

		//		 {"ari":"FIELDVALUE:PNL1?text?value","domEvent":1}
		//		 {"ari":"FIELDVALUE:PNL1?text?value","customFormat":1}
		//{"ffadn":"PNL1","fid":0,"fn":"test123","portletId":"GGRi6ZPXdjC3","rdoc":true,"vn":"test123"}

		List<Object> vcfg = (List<Object>) CH.getOr(val, "vcfg", null);
		if (vcfg != null && vcfg.size() > 0) {
			List<Object> domEvents = new ArrayList<Object>();
			List<Object> customFormats = new ArrayList<Object>();
			List<Object> linkedVariables = new ArrayList<Object>();
			// For backwards compatibility to count if the field has multiple variables or only one
			// For situation when fields have _ in the varname

			HashMap<String, Integer> fieldArisCount = new HashMap<String, Integer>();
			// Loop through the vcfg 
			for (int i = 0; i < vcfg.size(); i++) {
				HashMap<String, Object> obj = (HashMap<String, Object>) CH.getOr(vcfg, i, null);
				if (obj == null)
					continue;
				String formfieldAliasDotName = CH.getOrNoThrow(Caster_String.INSTANCE, obj, "ffadn", null);
				//Backwards compatibility
				if (formfieldAliasDotName == null) {
					String panelId = CH.getOrNoThrow(Caster_String.INSTANCE, obj, "pnlId", null);
					if (panelId != null)
						formfieldAliasDotName = AmiWebUtils.getFullAlias(AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS, panelId);
				}

				String fn = CH.getOrThrow(Caster_String.INSTANCE, obj, "fn");
				String fieldName = SH.beforeFirst(fn, '_');
				String ariWithoutFieldValueKey = "FIELDVALUE:" + formfieldAliasDotName + "?" + fieldName;

				int existingFieldVariables = CH.getOr(Caster_Integer.PRIMITIVE, fieldArisCount, ari, 0) + 1;
				fieldArisCount.put(ariWithoutFieldValueKey, existingFieldVariables);
			}
			// Loop through the variables once more
			for (int i = 0; i < vcfg.size(); i++) {
				HashMap<String, Object> obj = (HashMap<String, Object>) CH.getOr(vcfg, i, null);
				if (obj == null)
					continue;

				String formfieldAliasDotName = CH.getOrNoThrow(Caster_String.INSTANCE, obj, "ffadn", null);
				String fn = CH.getOrThrow(Caster_String.INSTANCE, obj, "fn");
				boolean rerunDmOnChange = CH.getOrThrow(Caster_Boolean.PRIMITIVE, obj, "rdoc");
				String varname = CH.getOrThrow(Caster_String.INSTANCE, obj, "vn");
				int formatterId = CH.getOrThrow(Caster_Integer.PRIMITIVE, obj, "fid");

				//Backwards compatibility
				if (formfieldAliasDotName == null) {
					String panelId = CH.getOrNoThrow(Caster_String.INSTANCE, obj, "pnlId", null);
					if (panelId != null)
						formfieldAliasDotName = AmiWebUtils.getFullAlias(AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS, panelId);
				}

				String fieldName = SH.beforeFirst(fn, '_');

				String ariWithoutFieldValueKey = "FIELDVALUE:" + formfieldAliasDotName + "?" + fieldName;
				String fieldValueKey = "";
				if (fieldArisCount.get(ariWithoutFieldValueKey) > 1)
					fieldValueKey = fn.indexOf('_') == -1 ? "" : '_' + SH.afterFirst(fn, '_');
				else
					fieldName = fn;

				String ari = "FIELDVALUE:" + formfieldAliasDotName + "?" + fieldName + "?" + fieldValueKey;
				if (rerunDmOnChange == true) {
					HashMap<String, Object> domEvent = new HashMap<String, Object>();
					domEvent.put("ari", ari);
					domEvent.put("domEvent", AmiWebDomObject.DOM_EVENT_CODE_ONCHANGE);
					domEvents.add(domEvent);
				}
				{
					HashMap<String, Object> customFormat = new HashMap<String, Object>();
					customFormat.put("ari", ari);
					customFormat.put("customFormat", formatterId);
					customFormats.add(customFormat);
				}
				{
					HashMap<String, Object> linkedVariable = new HashMap<String, Object>();
					linkedVariable.put("ari", ari);
					linkedVariable.put("varName", varname);
					linkedVariables.add(linkedVariable);
				}
			}
			onProcess.put("domEvents", domEvents);
			onProcess.put("customFormats", customFormats);
			onProcess.put("linkedVariables", linkedVariables);
		}

		entries.add(onProcess);
		callbacks.put("entries", entries);
		config.put("callbacks", callbacks);

		this.initV3(alias, config, origToNewPortletIdMapping, warningsSink);

	}
	private void initV3(String alias, Map<String, Object> val, Map<String, String> origToNewPortletIdMapping, StringBuilder warningsSink) {
		if (SH.isnt(dmName))
			dmName = CH.getOrThrow(Caster_String.INSTANCE, val, "lbl");
		this.callbacks.init(this, this.getAmiLayoutFullAlias(), (Map) val.get("callbacks"), warningsSink);

		this.responseTablesetBeforeFilter = getResponseOutSchema().newEmptyTables();
		fireDmDataBeforeFilterChanged();
		rebuildResponseTablesetAfterFilter();
		this.testInputType = CH.getOr(Caster_String.INSTANCE, val, "test_input_type", TEST_INPUT_VARS_OPEN);
		this.testInputCustomVars = CH.getOr(Caster_String.INSTANCE, val, "test_input_vars", TEST_INPUT_CUSTOM_VARS_DEFAULT);
		List<Map<String, String>> t = (List) val.get("onProcessParams");
		setAliasDotName(AmiWebUtils.getFullAlias(this.getAmiLayoutFullAlias(), this.dmName));
		this.minRequeryMs = OH.noNull(CH.getOr(Caster_Integer.INSTANCE, val, "minRequery", 0), 0);
		this.maxRequeryMs = OH.noNull(CH.getOr(Caster_Integer.INSTANCE, val, "maxRequery", 0), 0);
		this.queryOnMode = parseQueryModeString(CH.getOrNoThrow(Caster_String.INSTANCE, val, "queryMode", "none"));
		this.usedDatasources.addAll(CH.getOrNoThrow(Collection.class, val, "datasources", Collections.EMPTY_SET));
		isPlaying = getQueryOnStartup() && getMaxRequeryMs() > 0;

		for (String i : (Collection<String>) CH.getOr(Caster_Simple.OBJECT, val, "lowerV2", Collections.EMPTY_LIST)) {
			getLowerDmAliasDotNames().add(AmiWebUtils.getFullAlias(this.getAmiLayoutFullAlias(), i));
		}
		this.updateAri();
	}
	@Override
	public void init(String alias, Map<String, Object> val, Map<String, String> origToNewPortletIdMapping, StringBuilder warningsSink) {
		Map<String, Object> callbacks = (Map<String, Object>) CH.getOr(val, "callbacks", null);
		Collection<String> rtSources = (Collection<String>) CH.getOr(val, "subscribe", null);
		String ct = CH.getOr(Caster_String.INSTANCE, val, "crontab", null);
		String cttz = CH.getOr(Caster_String.INSTANCE, val, "crontabTz", null);
		setCrontab(ct, cttz);

		if (callbacks != null)
			this.initV3(alias, val, origToNewPortletIdMapping, warningsSink);
		else
			this.initV2(alias, val, origToNewPortletIdMapping, warningsSink);
		if (rtSources != null) {
			HashSet<String> aliasRts = new HashSet<String>(rtSources.size());
			// handle subscription alias: ensure dm subscribes to the correct rt resource when opening a child layout dm in root layout
			for (String s : rtSources)
				aliasRts.add(AmiWebUtils.getFullRealtimeId(alias, s));
			setRtSources(aliasRts);
		}
	}

	@Override
	public boolean getQueryOnStartup() {
		return this.queryOnMode == QUERY_ON_STARTUP || this.queryOnMode == QUERY_ON_STARTUP_AND_VISIBLE;
	}

	@Override
	public int getMinRequeryMs() {
		return minRequeryMs;
	}

	@Override
	public int getMaxRequeryMs() {
		return maxRequeryMs;
	}

	public void setMinRequeryMs(int minRequeryMs) {
		if (this.minRequeryMs == minRequeryMs)
			return;
		this.minRequeryMs = minRequeryMs;
		onChanged();

	}
	public void setMaxRequeryMs(int maxRequeryMs) {
		if (this.maxRequeryMs == maxRequeryMs)
			return;
		this.maxRequeryMs = maxRequeryMs;
		onChanged();
	}

	@Override
	public boolean canQueryAtThisTime(long now) {
		return this.scriptRunner == null && (this.minRequeryMs == 0 || this.getLastQueryStartTimeMillis() == 0 || this.getLastQueryEndTimeMillis() + this.minRequeryMs <= now);
	}

	@Override
	public void setIsPlay(boolean b) {
		if (b == isPlaying)
			return;
		isPlaying = b;
		if (!isPlaying)
			this.nextRunTime = -1l;
		onChanged();
	}

	@Override
	public boolean isPlaying() {
		return isPlaying;
	}

	@Override
	public Set<String> getLowerDmAliasDotNames() {
		return this.getCallback_OnProcess().getInputDatamodels();
	}

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		processRequest(getRequestTableset(), getDmManager().getService().getDebugManager(), datamodel.isInRequery(), false);
	}
	public void processResponseDebug(AmiDebugManager ds) {
	}

	public void onInitDone() {
		//Initialize callbacks linkedVariables
		this.callbacks.initCallbacksLinkedVariables();
	}

	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
	}

	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {
	}

	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
	}

	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		this.getCallback_OnProcess().onDmNameChanged(oldAliasDotName, dm.getAmiLayoutFullAliasDotId());
	}

	@Override
	public Set<String> getUsedDatasources() {
		return this.usedDatasources;
	}

	public void setUsedDatasources(Set<String> t) {
		if (CH.areSame(this.usedDatasources, t))
			return;
		this.usedDatasources.clear();
		this.usedDatasources.addAll(t);
		onChanged();
	}

	@Override
	public void setQueryOnMode(byte value) {
		if (this.queryOnMode == value)
			return;
		this.queryOnMode = value;
		onChanged();
	}
	@Override
	public byte getQueryOnMode() {
		return this.queryOnMode;
	}

	public void doStartup() {
		this.getResponseTableset().clearTables();
		for (String s : this.getLowerDmAliasDotNames()) {
			AmiWebDmsImpl dm = this.dmManager.getDmByAliasDotName(s);
			if (dm.getStatisticEvals() > 0) {
				processRequest(getRequestTableset(), this.getDmManager().getService().getDebugManager());
				return;
			}
		}
		switch (getQueryOnMode()) {
			case AmiWebDmsImpl.QUERY_ON_STARTUP:
			case AmiWebDmsImpl.QUERY_ON_STARTUP_AND_VISIBLE:
				processRequest(getRequestTableset(), this.getDmManager().getService().getDebugManager());
				break;
			case AmiWebDmsImpl.QUERY_ON_VISIBLE_ONCE:
			case AmiWebDmsImpl.QUERY_ON_VISIBLE:
				if (getStatisticEvals() > 0)
					processRequest(getRequestTableset(), this.getDmManager().getService().getDebugManager());
				break;
		}
	}

	@Override
	public void verify() {
		AmiWebDmManagerImpl mgr = this.getDmManager();
		String user = dmManager.getService().getPortletManager().getUserName();
		for (String i : this.getUpperDmAliasDotNames()) {
			AmiWebDm t = mgr.getDmByAliasDotName(i);
			if (t == null)
				LH.warning(log, user, ": For " + getAmiLayoutFullAliasDotId() + ": ", new Exception("Upper dm missing: " + i));
			else if (!t.getLowerDmAliasDotNames().contains(this.getAmiLayoutFullAliasDotId()))
				LH.warning(log, user, ": For " + getAmiLayoutFullAliasDotId() + ": ", new Exception("Upper dm missing link to me: " + i));
		}
		for (String i : this.getLowerDmAliasDotNames()) {
			AmiWebDm t = mgr.getDmByAliasDotName(i);
			if (t == null)
				LH.warning(log, user, ": For " + getAmiLayoutFullAliasDotId() + ": ", new Exception("Lower dm missing: " + i));
			else if (!t.getUpperDmAliasDotNames().contains(this.getAmiLayoutFullAliasDotId()))
				LH.warning(log, user, ": For " + getAmiLayoutFullAliasDotId() + ": ", new Exception("Lower dm missing link to me: " + i));
		}
		AmiWebDmTablesetSchema schema = this.getCallback_OnProcess().getReturnSchema();
		if (schema != null && schema.getDatamodel() != this)
			LH.warning(log, user, ": For " + getAmiLayoutFullAliasDotId() + ": ", new Exception("Callback schema points to wrong datamodel: " + schema.getDatamodel()));
	}

	@Override
	public AmiWebDmRequest getRequestTableset() {
		return requestTableset;
	}

	public void setRequestTableset(AmiWebDmRequest request) {
		this.requestTableset = request;
	}
	public void setResponseTableset(Tableset tableset) {
		this.responseTablesetBeforeFilter = tableset;
		fireDmDataBeforeFilterChanged();
		rebuildResponseTablesetAfterFilter();
	}

	@Override
	public Tableset getResponseTableset() {
		return responseTableset;
	}

	@Override
	public String getDmUid() {
		return dmId;
	}

	public void onChanged() {
		if (isRegisteredWithDmManager())
			this.dmManager.fireDmUpdated(this);
	}
	@Override
	public String getDmName() {
		return dmName;
	}

	@Override
	public AmiWebDmTablesetSchema getRequestInSchema() {
		return this.getCallback_OnProcess().getReturnSchema();
	}

	@Override
	public AmiWebDmTablesetSchema getResponseOutSchema() {
		return this.getCallback_OnProcess().getReturnSchema();
	}

	@Override
	public void fireDmDataChanged() {
		if (LH.isFine(log))
			LH.fine(log, this.getService().getUserName(), ": Datamodel '" + getAmiLayoutFullAliasDotId() + "' COMPLETED");
		for (int i = 0, l = listeners.size(); i < l; i++)
			listeners.get(i).onDmDataChanged(this);
	}
	@Override
	public void fireDmDataBeforeFilterChanged() {
		for (int i = 0, l = listeners.size(); i < l; i++)
			try {
				listeners.get(i).onDmDataBeforeFilterChanged(this);
			} catch (Exception e) {
				LH.warning(log, "Critical error in listener of dm: ", this.toDerivedString(), e);
			}
	}
	private void fireDmNameChanged(String oldAliasDotName) {
		if (isRegisteredWithDmManager())
			this.dmManager.onDmNameChanged(oldAliasDotName, this);
		for (int i = 0, l = listeners.size(); i < l; i++)
			try {
				listeners.get(i).onDmNameChanged(oldAliasDotName, this);
			} catch (Exception e) {
				LH.warning(log, "Critical error in listener of dm: ", this.toDerivedString(), e);
			}
	}
	protected void fireDmRunningQuery(boolean isRequery) {
		if (LH.isFine(log))
			LH.fine(log, this.getService().getUserName(), ": Datamodel '" + getAmiLayoutFullAliasDotId() + "' RUNNING", (isRequery ? "(REQUERY)" : ""));
		for (int i = 0, l = listeners.size(); i < l; i++)
			try {
				listeners.get(i).onDmRunningQuery(this, isRequery);
			} catch (Exception e) {
				LH.warning(log, "Critical error in listener of dm: ", this.toDerivedString(), e);
			}
	}
	protected void fireDmError(AmiWebDmError error) {
		if (LH.isFine(log))
			LH.fine(log, this.getService().getUserName(), ": Datamodel '" + getAmiLayoutFullAliasDotId() + "' ERROR: ", error.getException());
		for (int i = 0, l = listeners.size(); i < l; i++)
			try {
				listeners.get(i).onDmError(this, error);
			} catch (Exception e) {
				LH.warning(log, "Critical error in listener of dm: ", this.toDerivedString(), e);
			}
	}

	@Override
	public void addDmListener(AmiWebDmListener listener) {
		if (listener == this)
			throw new IllegalArgumentException();
		if (this.listeners.contains(listener))
			throw new IllegalArgumentException("already added: " + listener);
		this.listeners.add(listener);
	}

	@Override
	public void removeDmListener(AmiWebDmListener listener) {
		if (!this.listeners.contains(listener))
			throw new IllegalArgumentException("not found: " + listener);
		this.listeners.remove(listener);
	}

	@Override
	public Iterable<AmiWebDmListener> getDmListeners() {
		return this.listeners;
	}

	@Override
	public AmiWebDmManagerImpl getDmManager() {
		return dmManager;
	}

	@Override
	public Set<String> getUpperDmAliasDotNames() {
		return upperDmNames;
	}

	public void addUpperDm(String aliasDotName) {
		this.upperDmNames.add(aliasDotName);
	}

	public boolean removeUpperDm(String aliasDotName) {
		return this.upperDmNames.remove(aliasDotName);
	}

	//	public void setAmiScriptForRequest(String value) {
	//		this.amiScriptForRequest = value;
	//	}
	@Override
	public AmiWebAmiScriptCallbacks getCallbacks() {
		return callbacks;
	}

	@Override
	public boolean hasVisiblePortlet() {
		if (this.listeners.size() > 0)
			for (AmiWebDmListener i : this.listeners)
				if (i.hasVisiblePortletForDm(this))
					return true;
		return false;
	}

	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return hasVisiblePortlet();
	}

	public void incStatsEvalsCount() {
		this.statsEvalsCount++;
	}

	public void incStatsErrorsCount() {
		this.statsErrorsCount++;
	}

	public void incStatsEvalTimeNanos(long nanos) {
		this.statsEvalTimeNanos += nanos;
		if (this.statsEvalTimeMinNanos == -1)
			this.statsEvalTimeMinNanos = nanos;
		else
			this.statsEvalTimeMinNanos = MH.minl(this.statsEvalTimeMinNanos, nanos);
		if (this.statsEvalTimeMaxNanos == -1)
			this.statsEvalTimeMaxNanos = nanos;
		else
			this.statsEvalTimeMaxNanos = MH.maxl(this.statsEvalTimeMaxNanos, nanos);
	}

	@Override
	public void resetStatistics() {
		this.statsErrorsCount = 0;
		this.statsEvalsCount = 0;
		this.statsConsecutiveAutoRequeriesCount = 0;
		this.statsEvalTimeNanos = 0;
		this.statsEvalTimeMinNanos = -1;
		this.statsEvalTimeMaxNanos = -1;
	}

	@Override
	public long getStatisticErrors() {
		return this.statsErrorsCount;
	}

	@Override
	public long getStatisticEvals() {
		return this.statsEvalsCount;
	}
	@Override
	public long getStatisticConsecutiveRequeriesCount() {
		return this.statsConsecutiveAutoRequeriesCount;
	}
	@Override
	public long getStatisticEvalsAvgTimeMillis() {
		return this.statsEvalsCount != 0 ? this.statsEvalTimeNanos / this.statsEvalsCount / 1000000L : 0;
	}

	@Override
	public long getStatisticEvalsTimeMillis() {
		return this.statsEvalTimeNanos / 1000000L;
	}

	@Override
	public long getStatisticEvalsMinTimeMillis() {
		return this.statsEvalTimeMinNanos / 1000000L;
	}

	@Override
	public long getStatisticEvalsMaxTimeMillis() {
		return this.statsEvalTimeMaxNanos / 1000000L;
	}

	@Override
	public long getStatisticLastEvalTimeMillis() {
		return this.getLastQueryEndTimeMillis();
	}

	@Override
	public long getStatisticNextEvalTimeMillis() {
		if (this.nextRunTime == -1) {
			if (this.nextCrontabTime == -1)
				return 0;
			else
				return this.nextCrontabTime;
		} else {
			if (this.nextCrontabTime == -1)
				return this.nextRunTime;
			else
				return Math.min(this.nextRunTime, this.nextCrontabTime);
		}
	}

	@Override
	public Map<String, Object> getVars() {
		return vars;
	}

	@Override
	public boolean isCurrentlyRunning() {
		return this.getDmManager().getCurrentlyRunningDms().contains(this);
	}
	@Override
	public Tableset getResponseTablesetBeforeFilter() {
		return this.responseTablesetBeforeFilter;
	}

	@Override
	public void reprocessFilters(String tableName) {
		if (this.responseTablesetBeforeFilter == this.responseTableset)
			this.responseTableset = new TablesetImpl(responseTablesetBeforeFilter);
		Table tbl = responseTablesetBeforeFilter.getTableNoThrow(tableName);
		if (tbl != null) {
			tbl = new BasicTable(tbl);
			for (AmiWebDmFilter f : filters)
				if (OH.eq(f.getTargetDmAliasDotName(), this.getAmiLayoutFullAliasDotId()))
					if (OH.eq(f.getTargetTableName(), tableName))
						tbl = f.filter(tbl);
			this.responseTableset.putTable(tbl);
		}
		fireDmDataChanged();
	}
	@Override
	public void addFilter(AmiWebDmFilter filter) {
		CH.addIdentityOrThrow(this.filters, filter);
	}

	@Override
	public void removeFilter(AmiWebDmFilter filter) {
		CH.removeOrThrow(this.filters, filter);
	}

	@Override
	public List<AmiWebDmFilter> getFilters() {
		return this.filters;
	}
	private void rebuildResponseTablesetAfterFilter() {
		List<AmiWebDmFilter> filters = this.getFilters();
		if (filters.isEmpty())
			this.responseTableset = responseTablesetBeforeFilter;
		else {
			this.responseTableset = new TablesetImpl(responseTablesetBeforeFilter);
			for (AmiWebDmFilter f : filters)
				if (OH.eq(f.getTargetDmAliasDotName(), this.getAmiLayoutFullAliasDotId())) {
					Table tbl = this.responseTableset.getTableNoThrow(f.getTargetTableName());
					if (tbl != null) {
						tbl = f.filter(tbl);
						this.responseTableset.putTable(tbl);
					}
				}
		}
		fireDmDataChanged();
	}

	public String getTestInputType() {
		return testInputType;
	}
	public void setTestInputType(String testInputType) {
		this.testInputType = testInputType;
	}
	public String getTestInputCustomVars() {
		return testInputCustomVars;
	}
	public void setTestInputCustomVars(String testInputCustomVars) {
		this.testInputCustomVars = testInputCustomVars;
	}

	@Override
	public AmiWebDmRequest getInputDefaults() {
		AmiWebDmRequest r = new AmiWebDmRequest();
		//		for (AmiWebDmLink link : this.getDmManager().getDmLinksToDmAliasDotName(getAliasDotName()))
		//			for (String name : link.getWhereClauseVarNames())
		//				r.putVariable(name, null);
		String trueConst = getDefaultTrue();
		r.putVariable(WHERE, trueConst);
		r.putVariable(WHERES, null);
		r.putVariable(RTEVENTS, null);
		//		for (Entry<String, Class<?>> i : this.onProcessParamTypes.entrySet())
		//			r.putVariable(i.getKey(), null);

		return r;
	}

	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}
	@Override
	public String getAmiLayoutFullAlias() {
		return this.alias;
	}

	@Override
	public void setAliasDotName(String adn) {
		if (OH.eq(adn, this.aliasDotName))
			return;
		String oldAliasDotName = this.aliasDotName;
		this.alias = AmiWebUtils.getAliasFromAdn(adn);
		this.dmName = AmiWebUtils.getNameFromAdn(adn);
		this.aliasDotName = adn;
		if (oldAliasDotName != null) {
			this.fireDmNameChanged(oldAliasDotName);
			for (String i : this.getLowerDmAliasDotNames()) {
				this.dmManager.getDmByAliasDotName(i).removeUpperDm(oldAliasDotName);
				this.dmManager.getDmByAliasDotName(i).addUpperDm(this.aliasDotName);
			}
		}
		this.callbacks.setAmiLayoutAlias(this.alias);
		updateAri();
	}

	private List<AmiWebDmLink> dmLinksFromThisDm = new ArrayList<AmiWebDmLink>();
	private List<AmiWebDmLink> dmLinksToThisDm = new ArrayList<AmiWebDmLink>();
	private String ari;
	private boolean isTransient;

	@Override
	public Collection<AmiWebDmLink> getDmLinksFromThisDm() {
		return this.dmLinksFromThisDm;
	}
	@Override
	public Collection<AmiWebDmLink> getDmLinksToThisDm() {
		return this.dmLinksToThisDm;
	}
	@Override
	public void addDmLinkFromThisDm(AmiWebDmLink link) {
		this.dmLinksFromThisDm = new ArrayList(this.dmLinksFromThisDm);
		CH.addIdentityOrThrow(this.dmLinksFromThisDm, link);
	}
	@Override
	public void removeDmLinkFromThisDm(AmiWebDmLink link) {
		this.dmLinksFromThisDm = new ArrayList(this.dmLinksFromThisDm);
		CH.removeOrThrow(this.dmLinksFromThisDm, link);
	}
	@Override
	public void addDmLinkToThisDm(AmiWebDmLink link) {
		this.dmLinksToThisDm = new ArrayList(this.dmLinksToThisDm);
		CH.addIdentityOrThrow(this.dmLinksToThisDm, link);
	}
	@Override
	public void removeDmLinkToThisDm(AmiWebDmLink link) {
		this.dmLinksToThisDm = new ArrayList(this.dmLinksToThisDm);
		CH.removeOrThrow(this.dmLinksToThisDm, link);
	}

	public boolean isRegisteredWithDmManager() {
		return isRegisteredWithDmManager;
	}

	public void setRegisteredWithDmManager() {
		if (this.isRegisteredWithDmManager)
			throw new IllegalStateException("DM already added: " + this.aliasDotName);
		this.isRegisteredWithDmManager = true;
		for (String dm : this.getLowerDmAliasDotNames()) {
			AmiWebDmsImpl t = (AmiWebDmsImpl) getDmManager().getDmByAliasDotName(dm);
			if (t == this)
				throw new IllegalArgumentException("Can not assign datamodel to itself");
			t.addDmListener(this);
			t.addUpperDm(this.getAmiLayoutFullAliasDotId());
		}
	}

	@Override
	public boolean isReadonlyLayout() {
		AmiWebLayoutFile layout = this.getDmManager().getService().getLayoutFilesManager().getLayoutByFullAlias(this.getAmiLayoutFullAlias());
		return layout.isReadonly();
	}
	public void setLastQueryEndTimeMillis(long now) {
		if (now < this.lastQueryStartTimeMillis) {
			LH.warning(log, "Drift detected: resetting end time to start time: ", now, " vs. ", this.lastQueryStartTimeMillis);
			now = this.lastQueryStartTimeMillis;
		}
		this.lastQueryEndTimeMillis = now;
		this.nextCrontabTime = crontab == null ? -1L : crontab.calculateNextOccurance(now + 1);
		this.nextRunTime = !isPlaying || maxRequeryMs == 0 ? -1L : maxRequeryMs + now;
	}
	protected void setLastQueryStartTimeMillis(long now) {
		this.lastQueryStartTimeMillis = now;
	}
	public long getLastQueryEndTimeMillis() {
		return this.lastQueryEndTimeMillis;
	}
	protected long getLastQueryStartTimeMillis() {
		return this.lastQueryStartTimeMillis;
	}
	// higher precision but not since epoch
	public void setLastQueryStartTimeNanos(long nanos) {
		this.lastQueryStartTimeNanos = nanos;
	}
	public long getLastQueryStartTimeNanos() {
		return this.lastQueryStartTimeNanos;
	}

	@Override
	public void onScriptRunStateChanged(AmiWebScriptRunner runner, byte oldState, byte state) {
		if (this.scriptRunner != runner)
			return;
		final AmiDebugManager debug = runner.getVars().getDebugManager();
		final Tableset tableset = runner.getVars().getTableset();
		onScriptRunStateChanged(state, debug, runner.getException(), tableset);
	}
	public void onScriptRunStateChanged(byte state, AmiDebugManager debug, Exception exception, Tableset tableset) {
		final long now = getDmManager().getService().getPortletManager().getNow();
		final long nowNanos = System.nanoTime();
		switch (state) {
			case AmiWebScriptRunner.STATE_DONE:
				incStatsEvalsCount();
				incStatsEvalTimeNanos(nowNanos - this.getLastQueryStartTimeNanos());

				CalcFrame onProccessVars = this.scriptRunner.getWriteableVars();
				final Object where = onProccessVars.getValue(AmiWebDmsImpl.WHERE);
				final Map<String, Object> wheres = (Map<String, Object>) onProccessVars.getValue(WHERES);
				this.getDmManager().onDmsFinished(this);
				this.scriptRunner = null;
				if (debug.shouldDebug(AmiDebugMessage.SEVERITY_INFO))
					debug.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_DATASOURCE_QUERY, this.getAri(), CALLBACK_ONPROCESS,
							"Datamodel Script Complete", null, exception));
				// only onProcess gets to set tableset
				setResponseTableset(tableset);
				this.callbacks.execute(CALLBACK_ONCOMPLETE, where, wheres);
				setLastQueryEndTimeMillis(System.currentTimeMillis());
				this.isInRequery = false;
				break;
			case AmiWebScriptRunner.STATE_ERROR:
				incStatsErrorsCount();
				setLastQueryEndTimeMillis(now);
				this.scriptRunner = null;
				fireDmError(new AmiWebDmError(this, exception));
				this.getDmManager().onDmsFinished(this);
				this.isInRequery = false;
				break;
			case AmiWebScriptRunner.STATE_INIT:
				setLastQueryStartTimeMillis(now);
				setLastQueryStartTimeNanos(nowNanos);
				break;
		}
		if (state == AmiWebScriptRunner.STATE_ERROR && isPlaying()) {
			setIsPlay(false);
		}
	}

	public String getDefaultTrue() {
		String trueConst;
		if (!this.getLowerDmAliasDotNames().isEmpty()) {
			trueConst = "true";
		} else
			trueConst = AmiWebDmUtils.getDefaultTrueConst(getDmManager().getService().getPortletManager(), this);
		return trueConst;
	}

	@Override
	public AmiWebAmiScriptCallback getCallback_OnProcess() {
		return this.getCallbacks().getCallback(CALLBACK_ONPROCESS);
	}

	public void onInputDatamodelsAdded(Set<String> added) {
		if (isRegisteredWithDmManager) {
			for (String dm : added) {
				AmiWebDmsImpl t = (AmiWebDmsImpl) getDmManager().getDmByAliasDotName(dm);
				t.addDmListener(this);
				t.addUpperDm(this.getAmiLayoutFullAliasDotId());
			}
			onChanged();
		}
	}

	public void onInputDatamodelsRemoved(Set<String> removed) {
		if (isRegisteredWithDmManager) {
			for (String dm : removed) {
				AmiWebDmsImpl t = (AmiWebDmsImpl) getDmManager().getDmByAliasDotName(dm);
				if (t != null) {
					t.removeDmListener(this);
					t.removeUpperDm(this.getAmiLayoutFullAliasDotId());
				}
			}
			onChanged();
		}
	}

	public void onInputDatamodelNameChanged(String old, String nuw) {
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_DATAMODEL;
	}
	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public void updateAri() {
		String oldAri = ari;
		this.ari = getDmManager().getDmAri(this.dmName, this.alias);
		if (isManagedByDomManager)
			getDmManager().getService().getDomObjectsManager().fireAriChanged(this, oldAri);
	}

	@Override
	public String getDomLabel() {
		return this.getDmName();
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		//TODO: Make AmiWebLayoutFile implement AmiWebDomObject
		return (AmiWebDomObject) this.dmManager.getService().getLayoutFilesManager().getLayoutByFullAlias(this.getAmiLayoutFullAlias());
	}

	@Override
	public Class<?> getDomClassType() {
		return AmiWebDm.class;
	}

	@Override
	public Object getDomValue() {
		return this;
	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return this.callbacks;
	}

	@Override
	public boolean isTransient() {
		return this.isTransient;
	}
	@Override
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	private boolean isManagedByDomManager = false;
	private String crontabTimezoneText;
	private String crontabText;
	private TimeZone crontabTimezone;
	private Timer crontab;
	private long nextCrontabTime = -1L;
	private long nextRunTime = -1L;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.dmManager.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		this.getDmManager().getService().getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			AmiWebService service = this.dmManager.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}

	public long getNextCrontabTime() {
		return nextCrontabTime;
	}
	public long getNextRunTime() {
		return nextRunTime;
	}
	public Set<String> getRtSources() {
		return this.rtSources;
	}

	public void setRtSources(Set<String> s) {
		AmiWebManagers webManagers = this.getDmManager().getService().getWebManagers();
		for (String rtid : CH.comm(this.rtSources, s, true, false, false)) {
			webManagers.getAmiObjectsByType(rtid).removeAmiListener(this.realtimeQueue);
			rtTables.remove(rtid);
		}
		for (String rtid : CH.comm(this.rtSources, s, false, true, false))
			webManagers.getAmiObjectsByType(rtid).addAmiListener(this.realtimeQueue);
		this.rtSources.clear();
		this.rtSources.addAll(s);
	}

	public AmiWebDmRealtimeEvent drainRealtimeEvent() {
		return this.realtimeQueue.drainRealtimeEventsBuffer();
	}

	public boolean getIsInEdit() {
		return isEdit;
	}

	public void setIsInEdit(boolean isEdit) {
		this.isEdit = isEdit;
		for (String i : this.rtSources)
			this.getCallback_OnProcess().getTableset().removeTable(i);
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.aliasDotName;
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return null;
	}

	@Override
	public AmiWebService getService() {
		return this.dmManager.getService();
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula formula) {
		return EmptyCalcTypes.INSTANCE;
	}

	@Override
	public String getCrontabTimezone() {
		return this.crontabTimezoneText;
	}

	@Override
	public String getCrontab() {
		return this.crontabText;
	}

	@Override
	public void setCrontab(String crontab, String tz) {
		this.crontabText = crontab;
		this.crontabTimezoneText = tz;
		this.crontabTimezone = SH.isnt(tz) ? getService().getVarsManager().getTimeZone() : EH.getTimeZone(tz);
		if (SH.is(crontab)) {
			this.crontab = CronTab.parse(this.crontabText, this.crontabTimezone);
			final long now = getDmManager().getService().getPortletManager().getNow();
			this.nextCrontabTime = this.crontab.calculateNextOccurance(now + 1);
		} else {
			this.nextCrontabTime = -1L;
			this.crontab = null;
		}
	}

	public boolean hasCrontab() {
		return this.crontab != null;
	}

	@Override
	public boolean hasRanOnStartUp() {
		return getLastQueryStartTimeMillis() != 0;
	}

	public void onRealtimeLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		CH.removeOrThrow(this.rtSources, oldAri);
		CH.addOrThrow(this.rtSources, newAri);
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

	public void fillRealTimeTable(Tableset sink) {
		if (sink == null)
			return;
		for (String s : this.rtSources) {
			if (sink.getTableNoThrow(s) == null) {
				AmiWebRtTable rtTable = this.rtTables.get(s);
				if (rtTable == null) {
					com.f1.base.CalcTypes cols;
					AmiWebRealtimeObjectManager rtTypes = this.dmManager.getService().getWebManagers().getAmiObjectsByType(s);
					if (rtTypes.getRealtimeObjectsOutputSchema().isVarsEmpty())
						continue;
					cols = rtTypes.getRealtimeObjectsOutputSchema();
					rtTable = new AmiWebRtTable(s, cols);
					IterableAndSize<AmiWebObject> objects = rtTypes.getAmiObjects();
					this.rtTables.put(rtTable.getName(), rtTable);
					if (objects != null)
						for (AmiWebObject row : objects)
							rtTable.addRow(row);
				}
				sink.putTable(rtTable.getReadonlyTable());
			}
		}
	}
}
