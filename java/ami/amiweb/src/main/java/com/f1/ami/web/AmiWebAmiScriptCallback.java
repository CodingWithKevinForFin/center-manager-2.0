package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiWebChildCalcFrameStack;
import com.f1.ami.web.amiscript.AmiWebScriptRunner;
import com.f1.ami.web.amiscript.AmiWebTopCalcFrameStack;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTablesetSchema;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.dm.AmiWebFormFieldVarLink;
import com.f1.base.BaseHelper;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Table;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.Jsonable;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.columnar.CopyOnWriteTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.derived.ToDerivedString;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;

public class AmiWebAmiScriptCallback implements AmiWebDomObjectDependency, ToDerivedString, Jsonable {
	public static final byte COMPILE_NOCODE = 0;
	public static final byte COMPILE_ERROR = 1;
	public static final byte COMPILE_OKAY = 2;
	public final static byte TEST_INPUT_VARS_CUST = 1;
	public final static byte TEST_INPUT_VARS_DFLT = 2;
	public final static byte TEST_INPUT_VARS_LINK = 3;

	private static final Logger log = LH.get();
	final private String callbackType;//method name
	final private AmiWebOverrideValue<String> amiscript = new AmiWebOverrideValue<String>(null);
	final private ParamsDefinition paramsDef;
	private DerivedCellCalculator currentCalc;
	private DerivedCellCalculator calc;

	private String defaultDatasource;
	private int limit = AmiConsts.DEFAULT;//
	private int timeoutMs = AmiConsts.DEFAULT; //
	private byte testInputType = TEST_INPUT_VARS_DFLT; //
	private String testInputRef = null;
	private Map<String, String> testInputValues = new HashMap<String, String>(); //

	final private Map<String, AmiWebDomObject> linkedVariables = new HashMap<String, AmiWebDomObject>();
	final private OneToOne<String, String> varNameToAri = new OneToOne<String, String>();
	final private HashMap<String, String> removedAriToVarName = new HashMap<String, String>();
	final private com.f1.utils.structs.table.stack.BasicCalcTypes linkedVariablesTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	final private com.f1.utils.structs.table.stack.BasicCalcTypes compiledClassTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	final private HashMap<String, Byte> ariToDomEvent = new HashMap<String, Byte>();
	final private HashMap<String, Integer> ariToCustomFormat = new HashMap<String, Integer>();
	final private HashSet<String> usedVariableNames = new HashSet<String>();

	final private Map<String, String> paramDefaults = new HashMap<String, String>();
	private AmiWebDmTablesetSchema returnSchema;
	final private AmiWebService service;
	final private Set<String> inputDatamodels = new HashSet<String>(); //
	private List<Map<String, Object>> onLoadLinkedVariables;
	private String amiLayoutAlias;
	final private AmiWebAmiScriptCallbacks parent;
	private Map<String, Object> m;
	private boolean needsInitVariables = false;
	private boolean keepTablesetOnRerun;
	private AmiWebDmsImpl datamodel;
	private Exception currentException;
	private Exception exception;
	private Byte compiledResult = COMPILE_NOCODE;
	private boolean isDynamicDatamodel;
	private Set<String> constVarsUsed = new HashSet<String>();
	private Set<String> constVarsTmp = new HashSet<String>();
	private TablesetImpl tableset;
	private final boolean isDmOnProcess;//This indicates a special case where this script is 'listening' to underlying dms for reprocessing.

	//For Copying Callbacks
	public void copyFrom(AmiWebAmiScriptCallback callback) {
		//CLOSE EXISTING
		this.close();
		this.keepTablesetOnRerun = callback.keepTablesetOnRerun;
		// transfer tableset
		this.tableset = keepTablesetOnRerun ? new TablesetImpl(callback.tableset) : new TablesetImpl();
		this.defaultDatasource = callback.defaultDatasource;
		this.isDynamicDatamodel = callback.isDynamicDatamodel;
		this.amiscript.setValue(callback.amiscript.getValue(false), false);
		this.currentCalc = null;
		this.limit = callback.limit;
		this.timeoutMs = callback.timeoutMs;
		this.testInputType = callback.testInputType;
		this.testInputRef = callback.testInputRef;
		this.testInputValues.putAll(callback.testInputValues);
		this.paramDefaults.putAll(callback.paramDefaults);
		this.linkedVariables.putAll(callback.linkedVariables);
		this.linkedVariablesTypes.putAll(callback.linkedVariablesTypes);
		this.varNameToAri.putAll(callback.varNameToAri.toKeyValueMap());
		this.removedAriToVarName.putAll(callback.removedAriToVarName);
		this.ariToDomEvent.putAll(callback.ariToDomEvent);
		this.ariToCustomFormat.putAll(callback.ariToCustomFormat);
		this.setInputDatamodels(callback.getInputDatamodels());
		this.returnSchema = new AmiWebDmTablesetSchema(callback.returnSchema, this);
		this.updateUsedVariableNames();
		this.open();
		this.needsInitVariables = false;
		this.datamodel = callback.datamodel;
		this.compiledResult = this.ensureCompiledInner(service.getDebugManager());
	}
	//For Creating new Callbacks
	public AmiWebAmiScriptCallback(AmiWebAmiScriptCallbacks parent, String alias, AmiWebService service, AmiWebDmsImpl dm, ParamsDefinition def, Map<String, String> dflts,
			String methodName) {
		this.isDmOnProcess = parent.getThis() instanceof AmiWebDmsImpl && AmiWebDmsImpl.CALLBACK_DEF_ONPROCESS.getMethodName().equals(methodName);
		this.paramsDef = def;
		this.paramDefaults.putAll(dflts);
		this.callbackType = methodName;
		this.datamodel = dm;
		this.parent = parent;
		this.setAmiLayoutAlias(OH.noNull(alias, AmiWebLayoutFile.DEFAULT_ROOT_ALIAS));
		this.service = service;
		this.returnSchema = new AmiWebDmTablesetSchema(this.service, this);
		this.tableset = new TablesetImpl();
	}

	//For Editing a Callback
	public AmiWebAmiScriptCallback(String alias, AmiWebService service, AmiWebAmiScriptCallback callback) {
		this.isDmOnProcess = callback.isDmOnProcess;
		this.closed = true;
		this.parent = callback.parent;
		this.setAmiLayoutAlias(OH.noNull(alias, AmiWebLayoutFile.DEFAULT_ROOT_ALIAS));
		this.service = service;
		this.paramsDef = callback.paramsDef;
		this.paramDefaults.putAll(callback.paramDefaults);
		this.callbackType = callback.callbackType;
		this.copyFrom(callback);
	}
	//For initializing	
	public void init(AmiWebDmsImpl dms, String alias, Map<String, Object> m, StringBuilder warningsSink) {
		this.datamodel = dms;
		this.amiscript.setValue(AmiWebUtils.getAmiScript(m, "amiscript", null), false);
		this.limit = CH.getOr(Caster_Integer.PRIMITIVE, m, "limit", AmiConsts.DEFAULT);
		this.timeoutMs = CH.getOr(Caster_Integer.PRIMITIVE, m, "timeout", AmiConsts.DEFAULT);
		this.defaultDatasource = CH.getOr(Caster_String.INSTANCE, m, "defaultDs", null);
		this.keepTablesetOnRerun = CH.getOr(Caster_Boolean.INSTANCE, m, "keepTableset", Boolean.FALSE);
		this.isDynamicDatamodel = CH.getOr(Caster_Boolean.INSTANCE, m, "isDynamicDatamodel", Boolean.FALSE);
		this.testInputType = parseTestInputType(CH.getOr(Caster_String.INSTANCE, m, "testInputType", "dflt"));
		if (this.returnSchema.isLocked())
			this.returnSchema = new AmiWebDmTablesetSchema(this.service, this);
		this.returnSchema.init((Map) m.get("schema"), alias);
		this.testInputValues.putAll(CH.getOr(Map.class, m, "testInputs", Collections.EMPTY_MAP));
		this.inputDatamodels.clear();
		this.setAmiLayoutAlias(OH.noNull(alias, AmiWebLayoutFile.DEFAULT_ROOT_ALIAS));
		Object dm = m.get("inputDm");
		if (dm != null)
			for (String s : (Collection<String>) dm)
				this.inputDatamodels.add(AmiWebUtils.getFullAlias(this.getAmiLayoutAlias(), s));
		this.onLoadLinkedVariables = (List<Map<String, Object>>) CH.getOr(m, "linkedVariables", null);
		this.m = m;
		this.needsInitVariables = true;
	}
	@Override
	public void initLinkedVariables() {
		if (this.needsInitVariables == false)
			return;
		this.initLinkedVariablesAlt();
		if (CH.isEmpty(this.m))
			return;
		boolean hasDatamodel = CH.getOr(Caster_Boolean.INSTANCE, m, "hasDatamodel", false);
		if (hasDatamodel) {
			List<Map<String, Object>> domEvents = (List<Map<String, Object>>) CH.getOr(m, "domEvents", null);
			if (domEvents != null)
				for (int i = 0; i < domEvents.size(); i++) {
					Map<String, Object> entry = domEvents.get(i);
					String relativeAri = CH.getOrThrow(Caster_String.INSTANCE, entry, "ari");
					Byte domEvent = CH.getOrThrow(Caster_Byte.INSTANCE, entry, "domEvent");
					String fullAri = AmiWebAmiObjectsVariablesHelper.getFullAri(this.getAmiLayoutAlias(), relativeAri);
					fullAri = AmiWebLayoutVersionHelper.updateFullAri(fullAri, this.service);
					this.ariToDomEvent.put(fullAri, domEvent);
				}

			List<Map<String, Object>> customFormats = (List<Map<String, Object>>) CH.getOr(m, "customFormats", null);
			if (customFormats != null)
				for (int i = 0; i < customFormats.size(); i++) {
					Map<String, Object> entry = customFormats.get(i);
					String relativeAri = CH.getOrThrow(Caster_String.INSTANCE, entry, "ari");
					Integer customFormat = CH.getOrThrow(Caster_Integer.INSTANCE, entry, "customFormat");
					String fullAri = AmiWebAmiObjectsVariablesHelper.getFullAri(this.getAmiLayoutAlias(), relativeAri);
					fullAri = AmiWebLayoutVersionHelper.updateFullAri(fullAri, this.service);
					this.ariToCustomFormat.put(fullAri, customFormat);
				}
		}
		this.m = null;

	}
	public Map<String, Object> getConfiguration() {
		if (SH.isnt(this.amiscript.getValue(false)))
			return Collections.EMPTY_MAP;
		HashMap<String, Object> r = new HashMap<String, Object>();
		r.put("name", getName());
		AmiWebUtils.putAmiScript(r, "amiscript", this.amiscript.getValue(false));
		CH.putExcept(r, "limit", this.limit, AmiConsts.DEFAULT);
		CH.putExcept(r, "timeout", this.timeoutMs, AmiConsts.DEFAULT);
		CH.putExcept(r, "defaultDs", this.defaultDatasource, null);
		CH.putExcept(r, "keepTableset", this.keepTablesetOnRerun, Boolean.FALSE);
		CH.putExcept(r, "isDynamicDatamodel", this.isDynamicDatamodel, Boolean.FALSE);
		if (this.testInputType != TEST_INPUT_VARS_DFLT)
			r.put("testInputType", testInputTypeToString(this.testInputType));
		if (!this.returnSchema.isEmpty())
			r.put("schema", this.returnSchema.getConfiguration());
		if (!this.inputDatamodels.isEmpty()) {
			List<String> t = new ArrayList<String>();
			for (String s : this.inputDatamodels)
				t.add(AmiWebUtils.getRelativeAlias(this.getAmiLayoutAlias(), s));
			r.put("inputDm", t);
		}

		//TODO: only put values that are in params
		if (!this.testInputValues.isEmpty())
			r.put("testInputs", new HashMap<String, String>(this.testInputValues));

		List<Object> linkedVariables = new ArrayList<Object>();
		for (String varName : CH.sort(this.varNameToAri.getKeys())) {
			String val = this.varNameToAri.getValue(varName);
			String fullAri = null;
			AmiWebDomObject domObj = this.linkedVariables.get(varName);
			if (domObj != null)
				fullAri = domObj.getAri();
			else
				fullAri = val;

			String relativeAri = AmiWebAmiObjectsVariablesHelper.getRelativeAri(this.getAmiLayoutAlias(), fullAri);

			Map<String, Object> var = new LinkedHashMap<String, Object>();
			var.put("ari", relativeAri);
			var.put("varName", varName);

			linkedVariables.add(var);
		}

		r.put("linkedVariables", linkedVariables);

		if (this.hasDatamodel()) {
			r.put("hasDatamodel", this.hasDatamodel());

			if (this.ariToDomEvent.size() > 0) {
				List<Object> domEvents = new ArrayList<Object>();
				for (Map.Entry<String, Byte> entry : this.ariToDomEvent.entrySet()) {
					Map<String, Object> config = new LinkedHashMap<String, Object>();
					String key = entry.getKey();
					if (!this.varNameToAri.containsValue(key))
						continue;
					String varname = this.varNameToAri.getKey(key);
					if (!this.linkedVariables.containsKey(varname))
						continue;
					String relativeAri = AmiWebAmiObjectsVariablesHelper.getRelativeAri(this.getAmiLayoutAlias(), key);
					config.put("ari", relativeAri);
					config.put("domEvent", entry.getValue());
					domEvents.add(config);
				}
				r.put("domEvents", domEvents);
			}
			if (this.ariToCustomFormat.size() > 0) {
				List<Object> customFormats = new ArrayList<Object>();
				for (Map.Entry<String, Integer> entry : this.ariToCustomFormat.entrySet()) {
					Map<String, Object> config = new LinkedHashMap<String, Object>();
					String key = entry.getKey();
					if (!this.varNameToAri.containsValue(key))
						continue;
					String varname = this.varNameToAri.getKey(key);
					if (!this.linkedVariables.containsKey(varname))
						continue;
					config.put("ari", AmiWebAmiObjectsVariablesHelper.getRelativeAri(this.getAmiLayoutAlias(), key));
					config.put("customFormat", entry.getValue());
					customFormats.add(config);
				}
				r.put("customFormats", customFormats);
			}
		}
		return r;
	}

	public boolean hasDatamodel() {
		return this.datamodel != null;
	}

	public static byte parseTestInputType(String s) {
		if ("cust".equals(s))
			return TEST_INPUT_VARS_CUST;
		if ("relationship".equals(s))
			return TEST_INPUT_VARS_LINK;
		return TEST_INPUT_VARS_DFLT;
	}
	public static String testInputTypeToString(byte t) {
		switch (t) {
			case TEST_INPUT_VARS_CUST:
				return "cust";
			case TEST_INPUT_VARS_DFLT:
				return "dflt";
			case TEST_INPUT_VARS_LINK:
				return "relationship";
		}
		return null;
	}
	public String getCallbackType() {
		return callbackType;
	}
	public String getAmiscript(boolean override) {
		return amiscript.getValue(override);
	}
	public void setAmiscript(String amiscript, boolean override) {
		if (this.amiscript.setValue(amiscript, override)) {
			ensureCompiled(service.getDebugManager());
		}
	}
	public ParamsDefinition getParamsDef() {
		return paramsDef;
	}
	public String getDefaultDatasource() {
		return defaultDatasource;
	}
	public void setDefaultDatasource(String defaultDatasource) {
		this.defaultDatasource = defaultDatasource;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getTimeoutMs() {
		return this.timeoutMs;
	}
	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}
	public Map<String, String> getVarNameToAriMap() {
		return varNameToAri.getInnerKeyValueMap();
	}
	public Map<String, AmiWebDomObject> getVariables() {
		return linkedVariables;
	}
	public byte getTestInputType() {
		return testInputType;
	}
	public void setTestInputType(byte testInputType) {
		this.testInputType = testInputType;
	}
	public String getTestInputRef() {
		return testInputRef;
	}
	public void setTestInputRef(String s) {
		testInputRef = s;
	}
	public Map<String, String> getTestInputValues() {
		return testInputValues;
	}
	public void setTestInputValues(Map<String, String> testInputValues) {
		this.testInputValues = testInputValues;
	}
	public Map<String, String> getParamDefaults() {
		return paramDefaults;
	}
	public AmiWebDmTablesetSchema getReturnSchema() {
		return this.returnSchema;
	}
	public void setReturnSchema(AmiWebDmTablesetSchema schema) {
		OH.assertEqIdentity(schema.getCallback(), this);
		this.returnSchema = schema;
	}
	public String getName() {
		return this.paramsDef.getMethodName();
	}
	public String getAri() {
		return this.parent.getThis().getAri() + AmiWebConsts.CALLBACK_PREFIX_DELIM + this.paramsDef.getMethodName();
	}
	public byte ensureCompiled(AmiDebugManager dm) {
		OH.assertNotNull(dm);
		if (!this.service.getDesktop().getIsLocked()) {
			AmiWebDomObject thiz = this.parent.getThis();
			AmiWebEditAmiScriptCallbackPortlet editor = this.service.findEditor(thiz, this.getName());
			if (editor != null && editor.getCallback() != this) {
				byte r = editor.ensureCompiled();
				if (r == COMPILE_ERROR)
					PortletHelper.ensureVisible(editor);
				return r;
			}
		}
		return ensureCompiledInner(dm);

	}
	private byte ensureCompiledInner(AmiDebugManager dm) {
		DerivedCellCalculator oldCalc = this.calc;
		this.exception = null;
		this.currentException = null;
		if (SH.isnt(amiscript.getValue(true))) {
			this.calc = null;
			if (oldCalc != null)
				fireUpdate(oldCalc, this.calc);
			this.currentCalc = null;
			return COMPILE_NOCODE;
		}
		this.compiledClassTypes.clear();
		this.compiledClassTypes.putAll(getParamsDef().getParamTypesMapping());
		this.compiledClassTypes.putAll(this.linkedVariablesTypes);
		//		for (Map.Entry<String, AmiWebDomObject> entry : this.linkedVariables.entrySet()) {
		//			final AmiWebDomObject var = entry.getValue();
		//			final String k = entry.getKey();
		//			if (this.ariToCustomFormat.containsKey(var.getAri()))
		//				this.compiledClassTypes.putType(k, String.class);
		//			else
		//				this.compiledClassTypes.putType(k, var.getClass());
		//		}
		this.compiledClassTypes.putType("tableset", Tableset.class);
		final String layoutAlias = parent.getAmiLayoutAlias();
		final AmiWebDomObject thiz = this.parent.getThis();
		StringBuilder sb = new StringBuilder();
		try {
			this.constVarsTmp.clear();
			this.calc = service.getScriptManager(layoutAlias).parseAmiScript(amiscript.getValue(false), compiledClassTypes, sb, dm, AmiDebugMessage.TYPE_CALLBACK, thiz,
					this.getName(), true, this.constVarsTmp);
			this.constVarsUsed.clear();
			this.constVarsUsed.addAll(this.constVarsTmp);
			fireUpdate(oldCalc, this.calc);
		} catch (Exception e) {
			this.calc = null;
			if (oldCalc != null)
				fireUpdate(oldCalc, this.calc);
			this.exception = e;
		}
		if (amiscript.isOverride()) {
			String script = this.amiscript.getValue(true);
			try {
				this.constVarsTmp.clear();
				this.currentCalc = service.getScriptManager(layoutAlias).parseAmiScript(script, compiledClassTypes, sb, dm, AmiDebugMessage.TYPE_CALLBACK, thiz, this.getName(),
						true, this.constVarsTmp);
				this.constVarsUsed.clear();
				this.constVarsUsed.addAll(this.constVarsTmp);
			} catch (Exception e) {
				this.currentException = e;
			}
		} else {
			this.currentCalc = calc;
			this.currentException = exception;
		}
		return currentException == null ? COMPILE_OKAY : COMPILE_ERROR;
	}

	private CalcFrame getCalcValues(CalcFrame vals) {
		BasicCalcFrame values = new BasicCalcFrame(this.compiledClassTypes);
		values.putAll(vals);
		values.putValue("tableset", this.buildTablesetFromLowerDms());
		for (Map.Entry<String, AmiWebDomObject> entry : this.linkedVariables.entrySet()) {
			AmiWebDomObject var = entry.getValue();
			String k = entry.getKey();
			Integer formatId = this.ariToCustomFormat.get(var.getAri());
			if (formatId != null)
				values.putValue(k, AmiWebFormFieldVarLink.format(formatId, var.getDomValue(), var.getClass(), service.getFormatterManager(), service));
			else
				values.putValue(k, var.getDomValue());
		}
		return values;
	}

	//execution invoked from currently running amiscript
	public AmiWebChildCalcFrameStack prepareExecuteInstance(CalcFrameStack ei, CalcFrame values) {
		OH.assertNotNull(ei);
		CalcFrame calcValues = getCalcValues(values);
		AmiWebDomObject thiz = this.parent.getThis();
		if (!this.service.getDesktop().getIsLocked()) {
			AmiWebEditAmiScriptCallbackPortlet editor = this.service.findEditor(thiz, this.getName());
			if (editor != null && editor.getCallback() != this) {
				byte result = editor.ensureCompiled();
				if (result == COMPILE_ERROR) {
					PortletHelper.ensureVisible(editor);
					return null;
				}
			}
		}
		int toTimeoutMs = AmiUtils.toTimeout((long) this.timeoutMs, service.getDefaultTimeoutMs());
		TimeoutController tc = AmiUtils.getExecuteInstance2(ei).getTimeoutController();
		if (toTimeoutMs < tc.getTimeoutMillisRemaining())
			tc = new DerivedCellTimeoutController(toTimeoutMs);
		int limit = this.limit == AmiConsts.DEFAULT ? ei.getLimit() : this.limit;
		return new AmiWebChildCalcFrameStack(this.calc, ei, calcValues, new SingletonCalcFrame("this", this.parent.getThis().getClass(), this.parent.getThis()), this.tableset,
				thiz, this.getName(), limit, tc, this.defaultDatasource);
	}

	public Object executeInstance(CalcFrameStack ei, CalcFrame frame) {
		return this.currentCalc.get(new ReusableCalcFrameStack(ei, frame));
	}

	public Object executeInBlock(CalcFrameStack sf, CalcFrame frame) {
		sf = prepareExecuteInstance(sf, frame);
		if (sf == null)
			return null;
		return executeInstance(sf, frame);
	}

	//kicking off a new amiscript run
	public Object execute(AmiDebugManager dm, CalcFrame values) {
		OH.assertNotNull(dm);
		final CalcFrame calcValues = getCalcValues(values);
		final AmiWebDomObject thiz = this.parent.getThis();
		byte activityLogLevel = this.parent.getService().getActivityLogLevel();
		if (activityLogLevel > 0 && !this.parent.getService().getDesktop().getInEditMode()) {
			String ari = this.parent.getThis().getAri();
			String cbName = this.getCallbackType();
			String userName = this.parent.getService().getUserName();
			String sessionID = this.service.getWebState().getSessionId();
			StringBuilder vals = new StringBuilder();
			boolean verbose = activityLogLevel == 2 ? true : false;
			if (values.getVarsCount() == 0)
				vals.append("<no arg>");
			else {
				for (String string : values.getVarKeys()) {
					vals.append(string).append(" ").append(values.getValue(string)).append(';');
				}
			}
			AmiWebActivityLogger.logScript(verbose, userName, sessionID, cbName, ari, vals.toString());
		}
		final String layoutAlias = this.parent.getAmiLayoutAlias();
		if (!this.service.getDesktop().getIsLocked()) {
			AmiWebEditAmiScriptCallbackPortlet editor = this.service.findEditor(thiz, this.getName());
			if (editor != null && !editor.hasError()) {
				byte result = editor.ensureCompiled();
				if (result == COMPILE_ERROR) {
					PortletHelper.ensureVisible(editor);
					return null;
				} else if (result == COMPILE_OKAY) {
					return editor.test(values);
				} else
					return null;
			}
		}
		int toTimeoutMs = AmiUtils.toTimeout((long) this.timeoutMs, service.getDefaultTimeoutMs());
		int limit = this.limit == AmiConsts.DEFAULT ? service.getDefaultLimit() : this.limit;
		return service.getScriptManager(layoutAlias).executeAmiScript(amiscript.getValue(true), null, currentCalc, calcValues, dm, AmiDebugMessage.TYPE_CALLBACK, thiz,
				this.getName(), this.tableset, toTimeoutMs, limit, this.defaultDatasource);
	}
	public AmiWebScriptRunner executeReturnRunner(AmiDebugManager dm, CalcFrame values) {
		OH.assertNotNull(dm);
		if (currentCalc == null)
			return null;
		byte activityLogLevel = this.parent.getService().getActivityLogLevel();
		if (activityLogLevel > 0 && !this.parent.getService().getDesktop().getInEditMode()) {
			String cbName = this.getCallbackType();
			String userName = this.parent.getService().getUserName();
			String sessionID = this.service.getWebState().getSessionId();
			String ari = this.getAri();
			boolean verbose = activityLogLevel == 2 ? true : false;
			StringBuilder vals = new StringBuilder();
			if (values.getVarsCount() == 0)
				vals.append("<no arg>");
			else {
				for (String string : values.getVarKeys()) {
					vals.append(string).append(" ").append(values.getValue(string)).append(';');
				}
			}
			AmiWebActivityLogger.logScript(verbose, userName, sessionID, cbName, ari, vals.toString());
		}
		final CalcFrame calcValues = getCalcValues(values);
		final AmiWebDomObject thiz = this.parent.getThis();
		final String layoutAlias = parent.getAmiLayoutAlias();
		int toTimeoutMs = AmiUtils.toTimeout((long) this.timeoutMs, service.getDefaultTimeoutMs());
		int limit = this.limit == AmiConsts.DEFAULT ? service.getDefaultLimit() : this.limit;
		AmiWebTopCalcFrameStack runner = service.getScriptManager(layoutAlias).createExecuteInstance(dm, AmiDebugMessage.TYPE_CALLBACK, thiz, getName(), this.tableset, toTimeoutMs,
				limit, defaultDatasource, calcValues);
		return new AmiWebScriptRunner(amiscript.getValue(true), this.currentCalc, runner);
	}
	public void setInputDatamodels(Set<String> namesOrig) {
		HashSet<String> names = new HashSet<String>();
		for (String s : namesOrig)
			names.add(AmiWebUtils.getFullAlias(this.getAmiLayoutAlias(), s));
		if (CH.areSame(this.inputDatamodels, names))
			return;
		Set<String> added = CH.comm(this.inputDatamodels, names, false, true, false);
		Set<String> removed = CH.comm(this.inputDatamodels, names, true, false, false);
		this.inputDatamodels.clear();
		this.inputDatamodels.addAll(names);
		if (isDmOnProcess) {
			AmiWebDmsImpl a = (AmiWebDmsImpl) this.parent.getThis();
			if (a.getCallback_OnProcess() == this) {
				if (!added.isEmpty())
					a.onInputDatamodelsAdded(added);
				if (!removed.isEmpty())
					a.onInputDatamodelsRemoved(removed);
			}
		}
	}
	public Set<String> getInputDatamodels() {
		return this.inputDatamodels;
	}

	public boolean getKeepTablesetOnRerun() {
		return this.keepTablesetOnRerun;
	}
	public void setKeepTablesetOnRerun(boolean b) {
		this.keepTablesetOnRerun = b;
	}

	public boolean getIsDynamicDatamodel() {
		return this.isDynamicDatamodel;
	}

	public void setIsDynamicDatamodel(boolean b) {
		this.isDynamicDatamodel = b;
	}

	public TablesetImpl getTableset() {
		return this.tableset;
	}

	/**
	 * builds the context to be used in the upcoming amiscript execution
	 * 
	 * @return the tableset that contains all the tables this callback knows of
	 */
	public TablesetImpl buildTablesetFromLowerDms() {
		if (!keepTablesetOnRerun)
			tableset = new TablesetImpl();
		if (!this.inputDatamodels.isEmpty())
			for (String d : CH.sort(this.inputDatamodels)) {
				AmiWebDm dm = service.getDmManager().getDmByAliasDotName(d);
				Tableset lts = dm.getResponseTableset();
				// note: We are not handling duplicate table, we will replace the the table with the same name if we find it twice. Backwards INCOMPATIBLE change
				// each dm has two resources: tables and their schemas
				// if the amiscript causes schema to change, you will see a dialog to ask you if you want to update or do nothing. If you update, schema will match table, otherwise, the output schema will be different from the output table. Here is where we test the match.
				if (this.isDynamicDatamodel == true) {
					// skip schema check, use the output table as the context
					for (String name : lts.getTableNames()) {
						Table i = lts.getTable(name);
						if (!keepTablesetOnRerun || tableset.getTableNoThrow(name) == null) {
							tableset.putTable(name, new CopyOnWriteTable(i));
						}
					}
				} else {
					// see if each output table conforms to its schema
					final AmiWebDmTablesetSchema out = dm.getResponseOutSchema();
					for (String name : out.getTableNamesSorted()) {
						final Table i = lts.getTableNoThrow(name);
						// if it doesn't conform, then there is schema altering script AND (user didn't apply the schema changes in upstream OR use dynamic dm)
						// in this case we force the output table to conform to its pre-changed schema -> deep copy -> no performance boost
						final Table mapToSchema = out.getTable(name).mapToSchema(i, false);
						if (!keepTablesetOnRerun || tableset.getTableNoThrow(name) == null) {
							tableset.putTable(name, new CopyOnWriteTable(mapToSchema));
						}
					}
				}
			}
		if (isDmOnProcess) {
			AmiWebDmsImpl dm = (AmiWebDmsImpl) this.parent.getThis();
			dm.fillRealTimeTable(tableset);
		}
		return tableset;
	}
	public void onDmNameChanged(String oldAliasDotName, String aliasDotName) {
		if (this.inputDatamodels.contains(oldAliasDotName)) {
			this.inputDatamodels.remove(oldAliasDotName);
			this.inputDatamodels.add(aliasDotName);
			if (isDmOnProcess) {
				AmiWebDmsImpl a = (AmiWebDmsImpl) this.parent.getThis();
				if (a.getCallback_OnProcess() == this) {
					a.onInputDatamodelNameChanged(oldAliasDotName, aliasDotName);
				}
			}

		}
	}
	public void onDmRemoved(String aliasDotName) {
		if (this.inputDatamodels.remove(aliasDotName)) {
			if (isDmOnProcess) {
				AmiWebDmsImpl a = (AmiWebDmsImpl) this.parent.getThis();
				if (a.getCallback_OnProcess() == this) {
					a.onInputDatamodelsRemoved(CH.s(aliasDotName));
				}
			}
		}
	}
	public CalcFrame mapInputs(Map<String, Object> vars) {
		return this.paramsDef.mapToInputs(vars, false);
	}
	public void recompileAmiscript() {
		ensureCompiled(service.getDebugManager());
	}
	public void clearVariables() {
		this.linkedVariables.clear();
		this.linkedVariablesTypes.clear();
		this.varNameToAri.clear();
		this.removedAriToVarName.clear();
		this.ariToDomEvent.clear();
		this.ariToCustomFormat.clear();
		this.updateUsedVariableNames();
	}
	private void initLinkedVariablesAlt() {//private
		this.closed = true;
		this.service.getDomObjectsManager().removeCallback(this);
		for (String ari : this.varNameToAri.getValues())
			this.service.getDomObjectsManager().removeDomObjectDependency(ari, this);

		this.setAmiLayoutAlias(OH.noNull(this.getAmiLayoutAlias(), AmiWebLayoutFile.DEFAULT_ROOT_ALIAS));
		List<Map<String, Object>> linkedVariables = this.onLoadLinkedVariables;
		if (linkedVariables != null) {
			//Have to load the linkedVariables in reverse order in case of duplicate variables, last variable is default variable
			for (int i = linkedVariables.size() - 1; i >= 0; i--) {
				Map<String, Object> var = linkedVariables.get(i);
				String relativeAri = CH.getOrThrow(Caster_String.INSTANCE, var, "ari");
				String varName = CH.getOrThrow(Caster_String.INSTANCE, var, "varName");
				String fullAri = AmiWebAmiObjectsVariablesHelper.getFullAri(this.getAmiLayoutAlias(), relativeAri);
				fullAri = AmiWebLayoutVersionHelper.updateFullAri(fullAri, this.service);
				//				LH.info(log, "Relative ARI: " + relativeAri + " Full ARI: " + fullAri + " Name: " + varName);

				if (this.usedVariableNames.contains(varName)) {
					String newVarName = this.getNextVariableName(varName);
					//					this.amiscript += "\n// Duplicate variable: " + varName + " added object: " + fullAri + " as: " + newVarName;
					varName = newVarName;
				}
				this.addVariable(varName, fullAri);

				AmiWebDomObject object = AmiWebAmiObjectsVariablesHelper.getAmiWebDomObjectFromFullAri(fullAri, this.service);
				if (object != null)
					this.linkVariable(varName, object);
			}
		}
		this.needsInitVariables = false;
	}

	public void addVariable(String variableName, String ari) {
		this.varNameToAri.put(variableName, ari);
		this.usedVariableNames.add(variableName);
		this.service.getDomObjectsManager().addDomObjectDependency(ari, this);
	}
	public void linkVariable(String variableName, AmiWebDomObject object) {
		this.linkedVariables.put(variableName, object);
		this.linkedVariablesTypes.putType(variableName, object.getDomClassType());
	}
	public void unlinkVariable(String variableName) {
		this.linkedVariables.remove(variableName);
		this.linkedVariablesTypes.removeType(variableName);

	}
	public void removeVariable(String variableName) {
		String ari = this.varNameToAri.removeByKey(variableName);
		this.usedVariableNames.remove(variableName);
		this.service.getDomObjectsManager().removeDomObjectDependency(ari, this);

	}

	public void setDomEvent(String variableName, Byte triggerEvent) {
		if (triggerEvent == null) {
			AmiWebDomObject var = this.linkedVariables.get(variableName);
			if (var != null)
				this.removeDomEvent(var.getAri());
			return;
		}
		AmiWebDomObject variable = this.linkedVariables.get(variableName);
		if (this.varNameToAri.containsKey(variableName)) {
			this.ariToDomEvent.put(variable.getAri(), triggerEvent);
		}
	}
	public Byte removeDomEvent(String ari) {
		if (this.ariToDomEvent.containsKey(ari))
			return this.ariToDomEvent.remove(ari);
		else
			return null;
	}
	public void setCustomFormat(String variableName, Integer formatType) {
		if (formatType == null) {
			AmiWebDomObject var = this.linkedVariables.get(variableName);
			if (var != null)
				this.removeCustomFormat(var.getAri());
			return;
		}
		AmiWebDomObject variable = this.linkedVariables.get(variableName);
		if (this.varNameToAri.containsKey(variableName)) {
			this.ariToCustomFormat.put(variable.getAri(), formatType);
		}
	}
	public Integer removeCustomFormat(String ari) {
		if (this.ariToCustomFormat.containsKey(ari))
			return this.ariToCustomFormat.remove(ari);
		else
			return null;
	}

	private void updateUsedVariableNames() {
		this.usedVariableNames.clear();
		if (this.paramsDef != null) {
			String[] paramNames = this.paramsDef.getParamNames();
			if (paramNames != null)
				for (int i = 0; i < paramNames.length; i++) {
					this.usedVariableNames.add(paramNames[0]);
				}
		}
		this.usedVariableNames.addAll(this.varNameToAri.getKeys());
	}
	public Set<String> getUsedVariableNames(Set<String> sink) {
		return sink;
	}
	public String getNextVariableName(String suggestedName) {
		return SH.getNextId(AmiUtils.toValidVarName(suggestedName), this.usedVariableNames);
	}

	@Override
	public void onDomObjectAriChanged(AmiWebDomObject object, String oldAri) {
		String newAri = object.getAri();
		if (this.varNameToAri.containsValue(oldAri) && this.varNameToAri.containsValue(newAri)) {

			String varName = varNameToAri.getKey(oldAri);
			String currentVarName = varNameToAri.getKey(newAri);
			if (this.removedAriToVarName.containsKey(newAri)) {
				this.removedAriToVarName.remove(newAri);
				this.removeVariable(currentVarName);
			} else {
				this.unlinkVariable(newAri);
				this.removeVariable(currentVarName);
			}

			if (this.removedAriToVarName.containsKey(oldAri))
				this.removedAriToVarName.remove(oldAri);
			else
				this.unlinkVariable(varName); //if varname changes or domobj changes
			this.removeVariable(varName); //if ari changes
			this.addVariable(varName, newAri);
			this.linkVariable(varName, object);
			this.recompileAmiscript();
			if (this.ariToDomEvent.containsKey(oldAri)) {
				Byte domEvent = this.removeDomEvent(oldAri);
				this.setDomEvent(varName, domEvent);
			}
			if (this.ariToCustomFormat.containsKey(oldAri)) {
				Integer customFormat = this.removeCustomFormat(oldAri);
				this.setCustomFormat(varName, customFormat);
			}
		} else if (this.varNameToAri.containsValue(oldAri)) {
			//Object changed varname stays the same, ari changed
			String varName = varNameToAri.getKey(oldAri);
			if (this.removedAriToVarName.containsKey(oldAri))
				this.removedAriToVarName.remove(oldAri);
			else
				this.unlinkVariable(varName); //if varname changes or domobj changes
			this.removeVariable(varName); //if ari changes
			this.addVariable(varName, newAri);
			this.linkVariable(varName, object);
			this.recompileAmiscript();
			if (this.ariToDomEvent.containsKey(oldAri)) {
				Byte domEvent = this.removeDomEvent(oldAri);
				this.setDomEvent(varName, domEvent);
			}
			if (this.ariToCustomFormat.containsKey(oldAri)) {
				Integer customFormat = this.removeCustomFormat(oldAri);
				this.setCustomFormat(varName, customFormat);
			}
		} else if (this.varNameToAri.containsValue(newAri)) {
			//Object might have changed but varName and ari is the same
			//Link and update the removedMap
			String varName = this.varNameToAri.getKey(newAri);
			if (this.removedAriToVarName.containsKey(newAri))
				this.removedAriToVarName.remove(newAri);
			else
				this.unlinkVariable(varName);
			this.linkVariable(varName, object);
			this.recompileAmiscript();
		}

	}

	@Override
	public void onDomObjectEvent(AmiWebDomObject object, byte eventType) {
		if (this.ariToDomEvent.containsKey(object.getAri())) {
			byte triggerEvent = this.ariToDomEvent.get(object.getAri());
			if (eventType != triggerEvent)
				return;
			if (eventType == AmiWebDomObject.DOM_EVENT_CODE_NONE)
				return;
			AmiWebDmTablesetSchema returnSchema = this.getReturnSchema();
			if (returnSchema == null)
				return;
			AmiWebDm datamodel = returnSchema.getDatamodel();
			if (datamodel == null)
				return;
			datamodel.processRequest(datamodel.getRequestTableset(), datamodel.getDmManager().getService().getDebugManager());
		}
	}
	@Override
	public void onDomObjectAdded(AmiWebDomObject object) {

		String ari = object.getAri();
		//If it contains the ari
		if (this.varNameToAri.containsValue(ari)) {
			String varName = this.varNameToAri.getKey(ari);
			// If the object was removed previously without removing the variable
			if (this.removedAriToVarName.containsKey(ari)) {
				//Link and update the removedMap
				this.linkVariable(varName, object);
				this.removedAriToVarName.remove(ari);
				this.recompileAmiscript();
			} else {
				//Check if we have the domobj
				if (!this.linkedVariables.containsKey(varName)) {
					this.linkVariable(varName, object);
					this.recompileAmiscript();
				}
			}
		}
	}
	@Override
	public void onDomObjectRemoved(AmiWebDomObject object) {
		String ari = object.getAri();
		//If it contains the ari
		if (this.varNameToAri.containsValue(ari)) {
			String varName = this.varNameToAri.getKey(ari);
			//Check if we have the domobj
			if (this.linkedVariables.containsKey(varName)) {
				this.unlinkVariable(varName);
				this.removedAriToVarName.put(ari, varName);
				this.recompileAmiscript();
			}

		}
	}

	private boolean closed = false;

	public void close() {
		if (this.closed == true)
			return;
		this.closed = true;
		this.service.getDomObjectsManager().removeCallback(this);
		for (String ari : this.varNameToAri.getValues())
			this.service.getDomObjectsManager().removeDomObjectDependency(ari, this);
		this.linkedVariables.clear();
		this.linkedVariablesTypes.clear();
		this.varNameToAri.clear();
		this.removedAriToVarName.clear();
		this.ariToDomEvent.clear();
		this.ariToCustomFormat.clear();
		this.testInputValues.clear();
		//		this.inputDatamodels.clear();
		this.compiledClassTypes.clear();
	}
	public void open() {
		if (this.closed == false)
			return;
		this.closed = false;
		this.service.getDomObjectsManager().addCallback(this);
		for (String ari : this.varNameToAri.getValues())
			this.service.getDomObjectsManager().addDomObjectDependency(ari, this);
	}
	public Map<String, Byte> getAriToDomEvent() {
		return this.ariToDomEvent;
	}

	public Map<String, Integer> getAriToCustomFormat() {
		return this.ariToCustomFormat;
	}
	public String getAmiLayoutAlias() {
		return amiLayoutAlias;
	}
	public void setAmiLayoutAlias(String amiLayoutAlias) {
		this.amiLayoutAlias = amiLayoutAlias;
	}

	public CalcTypes getTypes() {
		ensureCompiled(service.getDebugManager());
		return this.compiledClassTypes;
	}

	public AmiWebAmiScriptCallbacks getParent() {
		return this.parent;
	}
	@Override
	public String toDerivedString() {
		return toDerivedString(new StringBuilder()).toString();
	}
	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		this.getParent().getThis().toDerivedString(sb).append(AmiWebConsts.CALLBACK_PREFIX_DELIM);
		sb.append(this.getParamsDef().getMethodName());
		return sb;
	}
	public AmiWebDmsImpl getDatamodel() {
		return this.datamodel;
	}

	public boolean hasCode() {
		return currentCalc != null;
	}
	public boolean hasError(boolean override) {
		return getError(override) != null;
	}
	public Exception getError(boolean override) {
		return override ? this.currentException : this.exception;
	}
	public void clearAmiscriptOverride() {
		if (this.amiscript.clearOverride())
			recompileAmiscript();
	}
	public Exception testAmiscript(String value) {
		if (SH.isnt(value)) {
			return null;
		}
		final String layoutAlias = parent.getAmiLayoutAlias();
		final AmiWebDomObject thiz = this.parent.getThis();
		StringBuilder sb = new StringBuilder();
		try {
			service.getScriptManager(layoutAlias).parseAmiScript(value, this.compiledClassTypes, sb, new AmiWebDebugManagerImpl(service), AmiDebugMessage.TYPE_CALLBACK, thiz,
					this.getName(), true, null);
		} catch (Exception e) {
			return e;
		}
		return null;
	}

	private void fireUpdate(DerivedCellCalculator oldCalc, DerivedCellCalculator nuwCalc) {
		this.service.fireOnCallbackChanged(this, oldCalc, nuwCalc);
	}

	protected byte getLastCompiledResult() {
		return this.compiledResult;
	}
	public void onVarConstChanged(String var) {
		if (SH.is(this.amiscript.getValue(false)) && this.getUsedConstVars().contains(var))
			this.recompileAmiscript();
	}

	public Set<String> getUsedConstVars() {
		return this.constVarsUsed;
	}
	@Override
	public String objectToJson() {
		return BaseHelper.toSimpleString(this);
	}
	public DerivedCellCalculator getCalc(boolean override) {
		return override ? this.currentCalc : this.calc;
	}
}
