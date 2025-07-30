package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.customobjects.AmiReflectionMemberConstructor;
import com.f1.ami.amicommon.customobjects.AmiReflectionMemberMethod;
import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.ami.amicommon.functions.AmiWebFunctionEval;
import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.amicommon.functions.AmiWebFunctionIsInstanceOf;
import com.f1.ami.amicommon.functions.AmiWebFunctionStrClassName;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiScriptMemberMethods;
import com.f1.ami.extern.PythonExtern;
import com.f1.ami.web.amiscript.AmiWebAmiScriptDerivedCellParser;
import com.f1.ami.web.amiscript.AmiWebAmiScriptDerivedCellParserAgg;
import com.f1.ami.web.amiscript.AmiWebCalcTypesStack;
import com.f1.ami.web.amiscript.AmiWebScriptBaseMemberMethods;
import com.f1.ami.web.amiscript.AmiWebScriptMemberMethods;
import com.f1.ami.web.amiscript.AmiWebScriptRunner;
import com.f1.ami.web.amiscript.AmiWebTopCalcFrameStack;
import com.f1.ami.web.functions.AmiWebFunctionAccessToken;
import com.f1.ami.web.functions.AmiWebFunctionDatamodelEnum;
import com.f1.ami.web.functions.AmiWebFunctionDateTimeFormatters;
import com.f1.ami.web.functions.AmiWebFunctionFormatDate2;
import com.f1.ami.web.functions.AmiWebFunctionFormatDecimal;
import com.f1.ami.web.functions.AmiWebFunctionNumberFormatters;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.container.ContainerTools;
import com.f1.stringmaker.impl.BasicStringMakerFactory;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.string.ExpressionParser;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.derived.AggregateTable;
import com.f1.utils.structs.table.derived.BasicExternFactoryManager;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.ClassDebugInspector;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorBlock;
import com.f1.utils.structs.table.derived.DerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.MethodFactoryHasher;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;

public class AmiWebScriptManager {

	public static final ParamsDefinition CALLBACK_DEF_ONSTARTUP = new ParamsDefinition("onStartup", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONSTARTUP_COMPLETE = new ParamsDefinition("onStartupComplete", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONUSERPREFSLOADING = new ParamsDefinition("onUserPrefsLoading", Map.class, "java.util.List userPrefs");
	public static final ParamsDefinition CALLBACK_DEF_ONUSERPREFSLOADED = new ParamsDefinition("onUserPrefsLoaded", Map.class, "java.util.List userPrefs");
	public static final ParamsDefinition CALLBACK_DEF_ONUSERPREFSSAVING = new ParamsDefinition("onUserPrefsSaving", Map.class, "java.util.List userPrefs");
	public static final ParamsDefinition CALLBACK_DEF_ONUSERPREFSSAVED = new ParamsDefinition("onUserPrefsSaved", Map.class, "java.util.List userPrefs");
	public static final ParamsDefinition CALLBACK_DEF_ONNOTIFICATIONHANDLED = new ParamsDefinition("onNotificationHandled", Object.class,
			"String id,String status,java.util.Map attachment");
	public static final ParamsDefinition CALLBACK_DEF_ONASYNCCOMMANDRESPONSE = new ParamsDefinition("onAsyncCommandResponse", Object.class,
			"com.f1.ami.web.amiscript.AmiWebCommandResponse response");
	public static final ParamsDefinition CALLBACK_DEF_ONKEY = new ParamsDefinition("onKey", String.class, "com.f1.suite.web.peripheral.KeyEvent keyEvent");
	public static final ParamsDefinition CALLBACK_DEF_ONMOUSE = new ParamsDefinition("onMouse", String.class, "com.f1.suite.web.peripheral.MouseEvent event");
	public static final ParamsDefinition CALLBACK_DEF_ONURLPARAMS = new ParamsDefinition("onUrlParams", Map.class, "java.util.Map params");
	public static final ParamsDefinition CALLBACK_ONAMIJSCALLBACKINTITLEBAR = new ParamsDefinition("onAmiJsCallbackInTitlebar", Object.class,
			"String action,java.util.List params");

	static {

		CALLBACK_DEF_ONSTARTUP.addDesc("Called once, after the layout(s) have successfully loaded, but before the query-on-startup datamodels are executed");
		CALLBACK_DEF_ONSTARTUP_COMPLETE.addDesc("Called once, after the layout(s) have successfully loaded and preferences have been loaded or ignored");
		CALLBACK_DEF_ONUSERPREFSLOADING.addDesc(
				"Called before the user preferences are imported/applied to the layout. You can mutate the passed in parameters in order to change what preferences are actually applied.This includes when the layout is first loaded and the user has associated preferences, or then the preferences are imported/uploaded by the user");
		CALLBACK_DEF_ONUSERPREFSLOADING.addParamDesc(0,
				"The user preferences that will be applied. IMPORTANT NOTE: you can mutate the values in this map and the result will be loaded");
		CALLBACK_DEF_ONUSERPREFSLOADED.addDesc(
				"Called after the user preferences are applied to the layout. This includes when the layout is first loaded and the user has associated preferences, or then the preferences are imported/uploaded by the user");
		CALLBACK_DEF_ONUSERPREFSLOADED.addParamDesc(0, "The user preferences that were applied");
		CALLBACK_DEF_ONKEY.addDesc(
				"Called when a keyboard key is pressed and either no panel is focused or the focused panel does not return 'stop'. Note, event calls cascade up from child to parent panels, depending on what the child's onKey(...) callback returns");
		CALLBACK_DEF_ONURLPARAMS.addDesc("Called when with the page is refreshed and there are parameters present (ex host:33332/" + BasicPortletManager.URL_PORTAL + "?a=b&c=d");
		CALLBACK_DEF_ONUSERPREFSSAVING.addDesc(
				"Called before the user preferences are saved/exported from the layout. You can mutate the passed in parameters in order to change what preferences are actually exported/saved.This includes when the layout is first loaded and the user has associated preferences, or then the preferences are imported/uploaded by the user");
		CALLBACK_DEF_ONUSERPREFSSAVING.addParamDesc(0,
				"The user preferences that will be saved. IMPORTANT NOTE: you can mutate the values in this map and the result is what will actually be exported/saved");
		CALLBACK_DEF_ONUSERPREFSSAVED.addDesc("Called after the user preferences are saved/exported from the layout.");
		CALLBACK_DEF_ONUSERPREFSLOADED.addParamDesc(0, "The user preferences that were saved/exported");
		CALLBACK_DEF_ONASYNCCOMMANDRESPONSE.addDesc("Called when a response from Session::callCommand(...) is available");
		CALLBACK_DEF_ONASYNCCOMMANDRESPONSE.addParamDesc(0, "The details and payload of the response");
		CALLBACK_DEF_ONNOTIFICATIONHANDLED.addDesc("Called when a user takes action on a notification (See Session::notify(...) for how to display notifications to a user)");
		CALLBACK_DEF_ONNOTIFICATIONHANDLED.addParamDesc(0, "The id that uniquely idetifies the notification, as returned by Session::notify(...)");
		CALLBACK_DEF_ONNOTIFICATIONHANDLED.addParamDesc(1,
				"Status of the notification: CLICKED - user clicked on the notification, CLOSED - user closed the notification, DENIED - The notification was not displayed (perhaps the user had disabled notifications at the browser level), CLEARED - the user refreshed the browser, causing notifications to be cleared");
		CALLBACK_DEF_ONNOTIFICATIONHANDLED.addParamDesc(2, "The attachment which was passed into the Session:notify(...)");
		CALLBACK_DEF_ONKEY.addParamDesc(0, "The key event");
		CALLBACK_DEF_ONURLPARAMS.addParamDesc(0, "Key and value of parameters passed into the url (after the question mark)");
		CALLBACK_DEF_ONMOUSE.addDesc(
				"Called when a mouse key is pressed while this panel is focused. Note, event calls cascade up from child to parent panels, depending on what the child's onMouse(...) callback returns");
		CALLBACK_DEF_ONMOUSE.addParamDesc(0, "The key event");
		CALLBACK_DEF_ONMOUSE.addRetDesc("return \"stop\" to not have the parent panel receive the event. Otherwise, the event gets fired on the parent panel.");
		CALLBACK_ONAMIJSCALLBACKINTITLEBAR.addDesc(
				"Executed when the javascript function amiJsCallback(...) is executed within the browser.  See Green Button -> Edit HTML -> Right click Menu -> Embed javascript call to onAmiJsCallback");
		CALLBACK_ONAMIJSCALLBACKINTITLEBAR.addParamDesc(0,
				"The 2nd param passed into the javascript method amiJsCallback(...). For example amiJsCallback(this,\"sample\") would result in \"sample\" being passed in");
		CALLBACK_ONAMIJSCALLBACKINTITLEBAR.addParamDesc(1,
				"A list of all remaining params passed into the javascript, exlcuding the first tow. For example, amiJsCallback(this,\"sample\",2,5,\"hello\",{n:20}) would result in List(2,5,\"hello\",Map(n,20)");
	}
	private static final Logger log = LH.get();

	final private Map<String, Layout> layouts = new HashMap<String, Layout>();

	final private BasicMethodFactory predefinedMethodsFactory;//this stores prebuilt 'factory-set' methods
	final private Map<String, Object> amiScriptValues = new LinkedHashMap<String, Object>();
	private BasicStringMakerFactory stringMakerFactory;

	final private AmiWebService service;
	final private BasicExternFactoryManager externFactory;
	private boolean shouldDebugExecutedAmiScript;
	final private int warnSlowAmiScriptMs;
	final private int defalutTimeoutMs;
	final private int defaultLimit;

	public AmiWebScriptManager(AmiWebService service, Map<String, AmiScriptClassPluginWrapper> customClassPlugins) {
		this.service = service;
		ContainerTools tools = service.getPortletManager().getTools();
		this.warnSlowAmiScriptMs = tools.getOptional(AmiWebProperties.PROPERTY_AMI_SLOW_AQMISCRIPT_WARN_MS, 1000);
		this.defalutTimeoutMs = AmiUtils.getDefaultTimeout(tools);
		this.defaultLimit = AmiUtils.getDefaultLimit(tools);
		setShouldDebugExecutedAmiScript(false);//TODO: this should be a user preference
		this.externFactory = new BasicExternFactoryManager();
		this.externFactory.addLanguage("python", new PythonExtern());
		this.predefinedMethodsFactory = new BasicMethodFactory(null);
		for (AmiWebFunctionFactory f : AmiUtils.getFunctions())
			this.predefinedMethodsFactory.addFactory(f);
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDatamodelEnum.Factory(this.getService()));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.TimeFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.TimeSecsFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.TimeMillisFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.TimeMicrosFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.TimeNanosFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.DateFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.DateTimeFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.DateTimeSecsFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.DateTimeMillisFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.DateTimeMicrosFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionDateTimeFormatters.DateTimeNanosFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionNumberFormatters.DecimalFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionFormatDecimal.Factory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionNumberFormatters.IntegerFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionNumberFormatters.ScientificFactory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionFormatDate2.Factory(service));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionIsInstanceOf.Factory(this.predefinedMethodsFactory));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionStrClassName.Factory(this.predefinedMethodsFactory));
		this.predefinedMethodsFactory.addFactory(new AmiWebFunctionAccessToken.Factory(service));
		AmiScriptMemberMethods.registerMethods(service.getDebugManager(), predefinedMethodsFactory);
		AmiWebScriptMemberMethods.registerMethods(service, predefinedMethodsFactory);
		for (AmiScriptClassPluginWrapper i : customClassPlugins.values()) {
			predefinedMethodsFactory.addVarType(i.getName(), i.getClazz());
			for (AmiReflectionMemberMethod method : i.getMethods())
				predefinedMethodsFactory.addMemberMethod(method);
			for (AmiReflectionMemberConstructor method : i.getConstructors())
				predefinedMethodsFactory.addMemberMethod(method);
		}

		this.stringMakerFactory = new BasicStringMakerFactory();
		this.stringMakerFactory.setFormatterFactory(new AmiWebStringMakerFormatterFactory());
	}

	private Map<Class, List<ParamsDefinition>> callbackDefinitions = new HashMap<Class, List<ParamsDefinition>>();

	public List<ParamsDefinition> getCallbackDefinitions(Class<?> c) {
		List<ClassDebugInspector<?>> classDeclarations = this.predefinedMethodsFactory.getClassDebugInepectors(c);
		List<ParamsDefinition> r = callbackDefinitions.get(c);
		if (r != null)
			return r;
		r = new ArrayList<ParamsDefinition>();
		for (ClassDebugInspector<?> i : classDeclarations)
			if (i instanceof AmiWebScriptBaseMemberMethods)
				r.addAll(((AmiWebScriptBaseMemberMethods) i).getCallbackDefinitions());
		callbackDefinitions.put(c, r);
		return r;
	}
	public void removeConstValue(String key) {
		for (Layout layout : this.layouts.values())
			layout.removeConstValue(key);
	}

	public void addConstValue(String key, Object globalVarValue, Class<?> globalVarType) {
		for (Layout layout : this.layouts.values())
			layout.addConstValue(key, globalVarValue, globalVarType);
	}

	private String getAri(AmiWebDomObject o) {
		return o == null ? null : o.getAri();
	}

	public Object getAmiScriptValue(String name) {
		return this.amiScriptValues.get(name);
	}

	public void putAmiScriptValue(String name, Object value) {
		this.amiScriptValues.put(name, value);
	}
	public Set<String> getAmiScriptValues() {
		return this.amiScriptValues.keySet();
	}
	public Map<String, Object> getAmiScriptValuesMap() {
		return this.amiScriptValues;
	}

	public void clear() {
		for (Layout i : this.layouts.values())
			i.close();
		this.layouts.clear();
		this.amiScriptValues.clear();
	}

	public int getAmiScriptTimeout() {
		return this.defalutTimeoutMs;
	}

	public AmiWebService getService() {
		return this.service;
	}

	private Layout getLayoutInner(String layoutAlias) {
		Layout r = this.layouts.get(layoutAlias);
		if (r == null)
			this.layouts.put(layoutAlias, r = new Layout(layoutAlias));
		return r;
	}
	public AmiWebScriptManagerForLayout getLayout(String layoutAlias) {
		Layout r = this.layouts.get(layoutAlias);
		if (r == null)
			this.layouts.put(layoutAlias, r = new Layout(layoutAlias));
		return r;
	}

	private class Layout implements AmiWebScriptManagerForLayout {

		final private BasicMethodFactory methodFactory;//Methods visible considering overriding/visibility
		final private SqlProcessor sqlProcessor;
		final private SqlProcessor sqlProcessorNotOptimized;
		final private SqlDerivedCellParser amiScriptParser;
		final private DerivedCellParser amiScriptParserNotOptimized;
		final private String layouAlias;
		final private Map<String, String> layoutVariablesScript = new HashMap<String, String>();
		final private MutableCalcFrame layoutVariablesValue = new MutableCalcFrame();
		final private SqlExpressionParser expressionParser;
		final private AmiWebAmiScriptDerivedCellParserAgg aggParser;
		private BasicMethodFactory declaredMethods = null;//Raw Methods declared in this layout (not considering override/inheritance
		private String declaredMethodsScript;
		final private AmiWebAmiScriptCallbacks callbacks;
		final private AmiWebFunctionEval.Factory evalFactory;

		public Layout(String layoutAlias) {
			AmiWebLayoutFile layoutByFullAlias = service.getLayoutFilesManager().getLayoutByFullAlias(layoutAlias);
			OH.assertNotNull(layoutByFullAlias);
			this.callbacks = new AmiWebAmiScriptCallbacks(service, layoutByFullAlias);
			this.callbacks.setAmiLayoutAlias(layoutAlias);
			for (ParamsDefinition i : service.getScriptManager().getCallbackDefinitions(AmiWebLayoutFile.class))
				this.callbacks.registerCallbackDefinition(i);
			this.layouAlias = layoutAlias;
			this.methodFactory = new BasicMethodFactory(predefinedMethodsFactory);
			AmiUtils.addTypes(this.methodFactory);
			ContainerTools tools = service.getPortletManager().getTools();
			this.sqlProcessor = new SqlProcessor();
			this.expressionParser = sqlProcessor.getExpressionParser();
			this.amiScriptParser = new AmiWebAmiScriptDerivedCellParser(this.sqlProcessor.getExpressionParser(), this.sqlProcessor, tools, externFactory, layoutAlias, true);
			this.aggParser = new AmiWebAmiScriptDerivedCellParserAgg(this.sqlProcessor.getExpressionParser(), this.sqlProcessor, tools, externFactory, layoutAlias, true);
			this.sqlProcessor.setParser(this.amiScriptParser);
			this.sqlProcessor.lock();
			this.sqlProcessorNotOptimized = new SqlProcessor();
			this.sqlProcessorNotOptimized.setParser(new AmiWebAmiScriptDerivedCellParser(this.sqlProcessorNotOptimized.getExpressionParser(), this.sqlProcessorNotOptimized, tools,
					externFactory, layoutAlias, false));
			this.amiScriptParserNotOptimized = this.sqlProcessorNotOptimized.getParser();
			this.evalFactory = new AmiWebFunctionEval.Factory(null, this.methodFactory);
			this.methodFactory.addFactory(evalFactory);
			evalFactory.setParser(this.getParser());
			service.getDomObjectsManager().addManagedDomObject(layoutByFullAlias);
			this.addConstValue("layout", getFile(), AmiWebLayoutFile.class);
			AmiWebVarsManager varsManager = service.getVarsManager();
			for (String i : varsManager.getGlobalVarNames()) {
				this.addConstValue(i, varsManager.getGlobalVarValue(i), varsManager.getGlobalVarType(i));
			}
		}

		public void close() {
			this.callbacks.close();
		}

		@Override
		public String getDeclaredMethodsScript() {
			return this.declaredMethodsScript;
		}

		@Override
		public BasicMethodFactory getDeclaredMethods() {
			return this.declaredMethods;
		}
		public SqlProcessor getSqlProcessor() {
			return this.sqlProcessor;
		}

		@Override
		public String forType(Class<?> type) {
			return this.methodFactory.forType(type);
		}
		@Override
		public Class<?> forName(String name) {
			return this.forName(0, name);
		}
		@Override
		public Class<?> forNameNoThrow(String name) {
			return this.methodFactory.forNameNoThrow(name);
		}
		@Override
		public Class<?> forName(int pos, String typeName) {
			if (SH.isnt(typeName))
				throw new ExpressionParserException(pos, "Missing type");
			try {
				return this.methodFactory.forName(typeName);
			} catch (Exception e) {
				throw new ExpressionParserException(pos, "Invalid type: " + typeName);
			}
		}

		@Override
		public BasicMethodFactory getMethodFactory() {
			return this.methodFactory;
		}

		private MutableCalcFrame consts = new MutableCalcFrame();
		//		private Map<String, Tuple2<Class<?>, Object>> consts = new HashMap<String, Tuple2<Class<?>, Object>>();

		private void rebuildVariablesInner() {
			this.consts.clear();
			this.style2ConstsCache.clear();
			AmiWebVarsManager varsManager = service.getVarsManager();
			HasherSet<String> constVisible = new HasherSet<String>();
			for (String i : varsManager.getGlobalVarNames()) {
				constVisible.add(i);
				this.addConstValue(i, varsManager.getGlobalVarValue(i), varsManager.getGlobalVarType(i));
			}
			Map<String, String> constOverrides = new HashMap<String, String>();
			final AmiWebLayoutFile layoutFile = service.getLayoutFilesManager().getLayoutByFullAlias(this.layouAlias);
			for (AmiWebLayoutFile t = layoutFile.getParent(); t != null; t = t.getParent()) {
				for (String i : getLayoutInner(t.getFullAlias()).getVariables())
					constOverrides.put(i, t.getFullAlias());
			}
			for (AmiWebLayoutFile t : layoutFile.getChildrenRecursive(true)) {
				for (String varName : getLayoutInner(t.getFullAlias()).getVariables()) {
					if (constVisible.contains(varName))
						continue;
					String la = constOverrides.get(varName);
					if (la == null)
						la = t.getFullAlias();
					Object value = getLayoutInner(la).getVariableValue(varName);
					Class<?> type = getLayoutInner(la).getVariableType(varName);
					addConstValue(varName, value, type);
					constVisible.add(varName);

				}
			}
			AmiWebLayoutFile p = layoutFile.getParent();
			if (p != null)
				getLayoutInner(p.getFullAlias()).rebuildVariablesInner();

		}
		private Class<?> getVariableType(String varName) {
			return this.layoutVariablesValue.getType(varName);
		}
		private Object getVariableValue(String varName) {
			return this.layoutVariablesValue.getValue(varName);
		}
		private Iterable<String> getVariables() {
			return this.layoutVariablesValue.getVarKeys();
		}

		@Override
		public DerivedCellCalculator toCalc(String formula, com.f1.base.CalcTypes variables, AmiWebDomObject stylePortlet, Set<String> constVarsSink) {
			return this.amiScriptParser.toCalc(formula, newAmiWebDerivedCellParserContextImpl(constVarsSink, variables, methodFactory, stylePortlet));
		}
		@Override
		public DerivedCellCalculator toCalcTemplate(String formula, com.f1.base.CalcTypes variables, AmiWebDomObject stylePortlet, Set<String> constVarsSink) {
			Node node = this.expressionParser.parseTemplate(formula);
			return this.amiScriptParser.toCalc(node, newAmiWebDerivedCellParserContextImpl(constVarsSink, variables, methodFactory, stylePortlet));
		}
		public DerivedCellCalculator toCalc(Node formula, com.f1.base.CalcTypes variables, AmiWebDomObject stylePortlet, Set<String> constVarsSink) {
			return this.amiScriptParser.toCalcFromNode(formula, newAmiWebDerivedCellParserContextImpl(constVarsSink, variables, methodFactory, stylePortlet));
		}

		public void removeConstValue(String i) {
			this.consts.removeTypeValue(i);
			this.style2ConstsCache.clear();
		}

		public boolean addConstValue(String i, Object globalVarValue, Class<?> globalVarType) {
			if (globalVarType == null)
				throw new RuntimeException("invalid type for " + i);
			this.consts.putTypeValue(i, globalVarType, globalVarValue);
			this.style2ConstsCache.clear();
			return true;
		}
		@Override
		public DerivedCellParser getParser() {
			return amiScriptParser;
		}
		public DerivedCellParser getAmiScriptParserNotOptimized() {
			return amiScriptParserNotOptimized;
		}
		@Override
		public void removeLayoutVariable(String name) {
			this.layoutVariablesScript.remove(name);
			this.layoutVariablesValue.removeValue(name);
			this.layoutVariablesValue.removeTypeValue(name);
			//this.layoutVariablesOverride.remove(name);
			this.rebuildVariablesInner();

		}
		private DerivedCellCalculator parseAmiScript(AmiDebugManager debugManager, byte debugType, AmiWebDomObject thiz, String callback, String amiscript,
				com.f1.base.CalcTypes classTypes, StringBuilder errorSink, boolean isTemplate, boolean throwException, Set<String> constVarsSink) {
			if (SH.isnt(amiscript))
				return null;
			AmiWebMethodsPortlet mp = getService().getDesktop().getMethodPortlet();
			if (mp != null && mp.hasPendingChanges()) {
				if (!mp.apply(null, errorSink)) {
					return null;
				}
			}
			try {
				Node node;
				try {
					if (isTemplate)
						node = this.expressionParser.parseTemplate(amiscript);
					else
						node = this.expressionParser.parse(amiscript);
				} catch (StackOverflowError e) {
					throw new ExpressionParserException(amiscript, 0, "Expression has too many phrases");
				}

				return toCalc(node, classTypes, thiz, constVarsSink);
			} catch (ExpressionParserException e) {
				if (e.getExpression() == null)
					e.setExpression(amiscript);
				if (errorSink != null) {
					if (amiscript.indexOf('\n') != -1) {
						Tuple3<Integer, Integer, String> pos = SH.getLinePositionAndText(amiscript, e.getPosition());
						errorSink.append("Error at position ").append(pos.getB()).append(" on line ").append(pos.getA() + 1).append(" on datamodel ").append(": ")
								.append(e.getMessage());
					} else {
						errorSink.append("Error at position ").append(e.getPosition()).append(": ").append(e.getMessage());
					}
				}
				if (debugManager != null && debugManager.shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					debugManager.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, debugType, getAri(thiz), callback, "Compiler Error",
							CH.m(new LinkedHashMap(), "Message", e.toLegibleString()), e));
				if (throwException)
					throw (RuntimeException) e;
				return null;
			} catch (Throwable e) {
				if (errorSink != null)
					errorSink.append("Error parsing code: ").append(e.getMessage());
				if (debugManager != null && debugManager.shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					debugManager.addMessage(
							new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, debugType, getAri(thiz), callback, "Compiler Error", CH.m("AmiScript", amiscript), e));
				LH.info(log, service.getUserName(), ": Error parsing code on datamodel ", getAri(thiz), callback == null ? "" : ("@" + callback), ": ", amiscript, e);
				if (throwException && e instanceof RuntimeException)
					throw (RuntimeException) e;
				return null;
			}
		}

		@Override
		public AggregateFactory createAggregateFactory() {
			return new AggregateFactory(methodFactory);
		}
		@Override
		public ExpressionParser getExpressionParser() {
			return this.expressionParser;
		}
		@Override
		public DerivedCellCalculator toAggCalc(String formula, com.f1.base.CalcTypes variables, AggregateFactory methodFactory, AmiWebDomObject stylePortlet,
				Set<String> constVarsSink) {
			return this.amiScriptParser.toCalc(formula, newAmiWebDerivedCellParserContextImpl(constVarsSink, variables, methodFactory, stylePortlet));
		}

		private Map<AmiWebDomObject, CalcFrame> style2ConstsCache = new IdentityHashMap<AmiWebDomObject, CalcFrame>();

		@Override
		public CalcFrame getConstsMap(AmiWebDomObject obj) {
			if (obj == null)
				return this.consts;
			CalcFrame r2 = style2ConstsCache.get(obj);
			if (r2 != null && !(obj instanceof com.f1.ami.web.tree.AmiWebTreeColumn))//For trees, don't use the style cache
				return r2;
			MutableCalcFrame r = new MutableCalcFrame();
			//			t.putType("this", obj.getClass());
			//			r.putValue("this", obj);
			r.putAllTypeValues(this.consts);
			AmiWebStyledPortlet portlet = null;
			for (AmiWebDomObject i = obj; i != null; i = i.getParentDomObject()) {
				if (i instanceof AmiWebStyledPortlet) {
					portlet = (AmiWebStyledPortlet) i;
					break;
				}
			}
			if (portlet != null) {
				BasicIndexedList<String, String> vc = portlet.getStylePeer().getVarValues();
				if (vc.getSize() > 0) {
					for (Map.Entry<String, String> entry : vc.entrySet()) {
						r.putTypeValue(entry.getKey(), String.class, entry.getValue());
					}
				}
			}
			style2ConstsCache.put(obj, r);
			return r;
		}

		@Override
		public DerivedCellCalculator toAggCalc(String formula, com.f1.base.CalcTypes varTypes, AggregateTable table, AmiWebDomObject stylePortlet, Set<String> constVarsSink) {
			return aggParser.toCalc(formula, newAmiWebDerivedCellParserContextImpl(constVarsSink, varTypes, this.methodFactory, stylePortlet));
		}
		private CalcTypesStack newAmiWebDerivedCellParserContextImpl(Set<String> constVarsSink, CalcTypes varTypes, MethodFactoryManager methodFactory, AmiWebDomObject thiz) {
			return new AmiWebCalcTypesStack(constVarsSink, varTypes, thiz == null ? EmptyCalcFrame.INSTANCE : new SingletonCalcFrame("this", thiz.getClass(), thiz), methodFactory,
					getConstsMap(thiz));
		}

		@Override
		public Iterable<String> getConsts(AmiWebDomObject obj) {
			return getConstsMap(obj).getVarKeys();
		}
		@Override
		public Object getConstValue(AmiWebDomObject obj, String key) {
			return this.getConstsMap(obj).getValue(key);
		}
		@Override
		public Class<?> getConstType(AmiWebDomObject obj, String key) {
			return this.getConstsMap(obj).getType(key);
		}
		@Override
		public DerivedCellCalculator toCalcNotOptimized(String formula, com.f1.base.CalcTypes variables, AmiWebDomObject stylePortlet, Set<String> constVarsSink) {
			return this.amiScriptParserNotOptimized.toCalc(formula, newAmiWebDerivedCellParserContextImpl(constVarsSink, variables, this.methodFactory, stylePortlet));
		}
		@Override
		public String getLayoutAlias() {
			return this.layouAlias;
		}

		@Override
		public List<DeclaredMethodFactory> getDeclaredMethodFactories() {
			BasicMethodFactory mf = getDeclaredMethods();
			List<DeclaredMethodFactory> sink2 = new ArrayList<DeclaredMethodFactory>();
			if (mf != null) {
				List<MethodFactory> sink = new ArrayList<MethodFactory>();
				mf.getAllMethodFactories(sink);
				for (MethodFactory i : sink)
					if (i instanceof DeclaredMethodFactory)
						sink2.add((DeclaredMethodFactory) i);
			}
			return sink2;
		}

		@Override
		public AmiWebAmiScriptCallbacks getLayoutCallbacks() {
			return this.callbacks;
		}

		@Override
		public void setDeclaredMethodsNoCompile(String customMethods) {
			setDeclaredMethods(null, customMethods);
		}

		//This Will not replace virtual methods for override.  AmiWebScriptManager::bindVirtuals() must be called afterwards
		@Override
		public boolean setDeclaredMethods(String code, AmiDebugManager debugManager, StringBuilder errorSink) {
			AmiWebLayoutFile file = this.getFile();
			if (SH.is(code)) {
				BasicMethodFactory old = getDeclaredMethods();
				String old2 = getDeclaredMethodsScript();
				setDeclaredMethods(null, null);
				Set<String> constVarsSink = new HashSet<String>();//TODO: this needs to be stored
				DerivedCellCalculator t = parseAmiScript(debugManager, AmiDebugMessage.TYPE_CUSTOM_METHODS, file, null, code, null, errorSink, false, false, constVarsSink);
				if (errorSink.length() > 0) {
					if (old != null)
						setDeclaredMethods(old, old2);
					return false;
				}
				if (t instanceof DerivedCellCalculatorBlock) {
					DerivedCellCalculatorBlock block = (DerivedCellCalculatorBlock) t;
					if (block.getMethodFactory() != null) {
						List<MethodFactory> sink = new ArrayList<MethodFactory>();
						block.getMethodFactory().getMethodFactories(sink);
						BasicMethodFactory mf = new BasicMethodFactory();
						for (MethodFactory i : sink)
							mf.addFactory(i);
						setDeclaredMethods(mf, code);
					}
				}
			} else {
				setDeclaredMethods(null, null);
			}
			return true;
		}
		private void setDeclaredMethods(BasicMethodFactory factory, String amiScript) {
			this.declaredMethods = factory;
			this.declaredMethodsScript = amiScript;
			this.methodFactory.clearFactoryManagers();
			this.methodFactory.addFactoryManager(predefinedMethodsFactory);
			AmiWebLayoutFile layoutFile = this.getFile();
			HasherSet<MethodFactory> childMethods = new HasherSet<MethodFactory>(MethodFactoryHasher.INSTANCE);
			getChildMethods(layoutFile, childMethods);
			BasicMethodFactory childMethodsFactory = new BasicMethodFactory();
			for (MethodFactory i : childMethods)
				childMethodsFactory.addFactory(i);
			this.methodFactory.addFactoryManager(childMethodsFactory);
			this.methodFactory.setFactoryForVirtuals(null);
			if (this.methodFactory.findFactory(evalFactory.getDefinition()) == null) //only add evalFactory if it does not already exist in the methodFactory, this prevents adding duplicate evalFactory
				this.methodFactory.addFactory(evalFactory);
		}
		private void bindVirtuals() {
			BasicMethodFactory old = this.getDeclaredMethods();
			String old2 = getDeclaredMethodsScript();
			this.methodFactory.clearMethodFactories();
			setDeclaredMethods(null, null);
			HasherSet<MethodFactory> parentMethods = new HasherSet<MethodFactory>(MethodFactoryHasher.INSTANCE);
			getParentMethods(this.getFile(), parentMethods);
			BasicMethodFactory parentMethodsFactory = new BasicMethodFactory();
			for (MethodFactory i : parentMethods)
				parentMethodsFactory.addFactory(i);
			this.methodFactory.setFactoryForVirtuals(parentMethodsFactory);
			AmiWebLayoutFile file = this.getFile();
			StringBuilder errorSink = new StringBuilder();
			Set<String> constVarsSink = new HashSet<String>();//TODO: this needs to be stored
			DerivedCellCalculator t = parseAmiScript(null, AmiDebugMessage.TYPE_CUSTOM_METHODS, file, null, old2, null, errorSink, false, false, constVarsSink);
			if (t instanceof DerivedCellCalculatorBlock) {
				DerivedCellCalculatorBlock block = (DerivedCellCalculatorBlock) t;
				if (block.getMethodFactory() != null) {
					List<MethodFactory> sink = new ArrayList<MethodFactory>();
					block.getMethodFactory().getMethodFactories(sink);
					for (MethodFactory i : sink)
						this.methodFactory.addFactory(i);
				}
			}
			setDeclaredMethods(old, old2);
			for (int i = 0; i < file.getChildrenCount(); i++)
				getLayoutInner(file.getChildAt(i).getAmiLayoutFullAlias()).bindVirtuals();
			setDeclaredMethods(old, old2);
		}
		private void getChildMethods(AmiWebLayoutFile t, HasherSet<MethodFactory> sink) {
			for (int i = 0; i < t.getChildrenCount(); i++) {
				Layout layout = getLayoutInner(t.getChildAt(i).getFullAlias());
				BasicMethodFactory dm = layout.declaredMethods;
				if (dm != null) {
					List<MethodFactory> sink2 = new ArrayList<MethodFactory>();
					dm.getMethodFactories(sink2);
					for (MethodFactory j : sink2)
						if (!sink.contains(j))
							sink.add(j);
				}
				getChildMethods(t.getChildAt(i), sink);
			}

		}
		private void getParentMethods(AmiWebLayoutFile t, HasherSet<MethodFactory> sink) {
			if (t == null)
				return;
			Layout layout = getLayoutInner(t.getFullAlias());
			BasicMethodFactory dm = layout.methodFactory;
			if (dm != null) {
				List<MethodFactory> sink2 = new ArrayList<MethodFactory>();
				dm.getMethodFactories(sink2);
				for (MethodFactory i : sink2)
					if (!sink.contains(i))
						sink.add(i);
			}
			getParentMethods(t.getParent(), sink);
		}

		@Override
		public AmiWebLayoutFile getFile() {
			return service.getLayoutFilesManager().getLayoutByFullAlias(this.layouAlias);
		}
		@Override
		public Map<String, String> getLayoutVariableScripts() {
			return layoutVariablesScript;
		}
		@Override
		public CalcFrame getLayoutVariableValues() {
			return layoutVariablesValue;
		}
		@Override
		public com.f1.base.CalcTypes getLayourVariableTypes() {
			return layoutVariablesValue;
		}
		@Override
		public DerivedCellCalculator parseAmiScript(String script, com.f1.base.CalcTypes types, StringBuilder errorSink, AmiDebugManager debugManager, byte debugType,
				AmiWebDomObject thiz, String callback, boolean throwException, Set<String> constVarsSink) {
			return parseAmiScript(debugManager, debugType, thiz, callback, script, types, errorSink, false, throwException, constVarsSink);
		}
		@Override
		public DerivedCellCalculator parseAmiScriptTemplate(String script, com.f1.base.CalcTypes types, StringBuilder errorSink, AmiDebugManager debugManager, byte debugType,
				AmiWebDomObject thiz, String callback, Set<String> constVarsSink) {
			return parseAmiScript(debugManager, debugType, thiz, callback, script, types, errorSink, true, false, constVarsSink);
		}

		@Override
		public Object executeAmiScript(String amiscript, StringBuilder errorSink, DerivedCellCalculator calc, CalcFrame values, AmiDebugManager debugManager, byte debugType,
				AmiWebDomObject thiz, String callback, Tableset tableset, int timeoutMs, int limit, String defaultDatasource) {
			OH.assertNotNull(thiz);
			if (SH.isnt(amiscript))
				return null;
			try {
				AmiWebTopCalcFrameStack ei = createExecuteInstance(debugManager, debugType, thiz, callback, tableset, timeoutMs, limit, defaultDatasource, values);
				AmiWebScriptRunner runner = new AmiWebScriptRunner(amiscript, calc, ei);
				runner.runStep();
				Object r;
				if (runner.getState() == AmiWebScriptRunner.STATE_DONE) {
					r = runner.getReturnValue();
				} else {
					return null;
				}
				if (shouldDebugExecutedAmiScript && debugManager.shouldDebug(AmiDebugMessage.SEVERITY_INFO)) {
					String type = r == null ? "void" : forType(r.getClass());
					debugManager.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, debugType, getAri(thiz), callback, "Executed AmiScript",
							CH.m("AmiScript", amiscript, "Result (" + type + ")", SH.s(r)), null));
				}
				return r;
			} catch (Exception e) {
				if (debugManager.shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					debugManager
							.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, debugType, getAri(thiz), callback, "Runtime Error", CH.m("AmiScript", amiscript), e));
				if (errorSink != null)
					errorSink.append(", Error executing code: ").append(amiscript);
				LH.info(log, service.getUserName(), ": Error executing code: ", amiscript, e);
				return null;
			}
		}

		@Override
		public AmiWebTopCalcFrameStack createExecuteInstance(AmiDebugManager debugManager, byte debugType, AmiWebDomObject thiz, String callback, Tableset tableset, int timeoutMs,
				int limit, String defaultDatasource, CalcFrame vars) {
			CalcFrame rov = thiz == null ? EmptyCalcFrame.INSTANCE : new SingletonCalcFrame("this", thiz.getClass(), thiz);
			TimeoutController timeoutController;
			if (timeoutMs == AmiConsts.DEFAULT)
				timeoutController = new DerivedCellTimeoutController(service.getDefaultTimeoutMs());
			else
				timeoutController = new DerivedCellTimeoutController(timeoutMs);
			if (debugManager == null)
				debugManager = service.getDebugManager();
			AmiWebTopCalcFrameStack ei = new AmiWebTopCalcFrameStack(tableset, limit, timeoutController, defaultDatasource, service.getBreakpointManager(), getMethodFactory(),
					vars, getConstsMap(thiz), debugManager, service, callback, debugType, thiz, this.layouAlias, rov);

			//			AmiWebScriptRunner runner = new AmiWebScriptRunner(amiscript, calc, getService(), values, rov, thiz, debugManager, tableset, false, timeoutMs, limit, defaultDatasource,
			//					debugType, callback, this.layouAlias);

			return ei;
		}
		@Override
		public Object executeAmiScript(String amiscript, StringBuilder errorSink, DerivedCellCalculator calc, CalcFrame values, AmiDebugManager debugManager, byte debugType,
				AmiWebDomObject thiz, String callback) {
			return executeAmiScript(amiscript, errorSink, calc, values, debugManager, debugType, thiz, callback, new TablesetImpl(), service.getDefaultTimeoutMs(),
					service.getDefaultLimit(), null);

		}
		@Override
		public Object parseAndExecuteAmiScript(String amiscript, StringBuilder errorSink, CalcFrame values, AmiDebugManager debugManager, byte debugType, AmiWebDomObject thiz,
				String callback) {
			OH.assertNotNull(thiz);
			DerivedCellCalculator calc = parseAmiScript(amiscript, values, errorSink, debugManager, debugType, thiz, callback, false, null);
			if (calc == null)
				return null;
			return executeAmiScript(amiscript, errorSink, calc, values, debugManager, debugType, thiz, callback);
		}

		@Override
		public boolean putLayoutVariableScript(String name, String amiscript, StringBuilder errorSink) {
			DerivedCellCalculator calc = parseAmiScript(amiscript, null, errorSink, service.getDebugManager(), AmiDebugMessage.TYPE_VARIABLE, service, name, false, null);
			if (calc == null)
				return false;
			Class<?> type = calc.getReturnType();
			Object value;
			try {
				value = calc.get(null);
			} catch (Exception e) {
				errorSink.append("Error: " + e);
				return false;
			}
			this.layoutVariablesScript.put(name, amiscript);
			this.layoutVariablesValue.putTypeValue(name, type, value);
			this.rebuildVariablesInner();
			return true;
		}

	}

	public boolean getShouldDebugExecutedAmiScript() {
		return shouldDebugExecutedAmiScript;
	}
	public void setShouldDebugExecutedAmiScript(boolean shouldDebugExecutedAmiScript) {
		this.shouldDebugExecutedAmiScript = shouldDebugExecutedAmiScript;
	}
	public int getWarnSlowAmiScriptMs() {
		return warnSlowAmiScriptMs;
	}
	public void bindVirtuals() {
		this.getLayoutInner("").bindVirtuals();
	}
	public void onStyleVarsChanged(String id) {
		for (Layout i : this.layouts.values())
			i.style2ConstsCache.remove(id);
	}
	public int getDefaultLimit() {
		return defaultLimit;
	}

}
