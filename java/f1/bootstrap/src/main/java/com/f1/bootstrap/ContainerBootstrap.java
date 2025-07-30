/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.f1.base.Clock;
import com.f1.base.CodeGenerated;
import com.f1.base.Console;
import com.f1.base.IdeableGenerator;
import com.f1.base.Message;
import com.f1.base.ValuedEnumCache;
import com.f1.bootstrap.appmonitor.AppMonitorContainer;
import com.f1.codegen.ClassPathModifier;
import com.f1.codegen.CodeCompiler;
import com.f1.codegen.CodeGenerator;
import com.f1.codegen.impl.BasicCodeCompiler;
import com.f1.codegen.impl.BasicCodeGenerator;
import com.f1.codegen.impl.DummyCodeCompiler;
import com.f1.container.Container;
import com.f1.container.ContainerServices;
import com.f1.container.DispatchController;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicDispatcherController;
import com.f1.container.impl.ContainerConsole;
import com.f1.container.impl.ContainerHelper;
import com.f1.container.impl.PersistenceRoot;
import com.f1.container.wrapper.InspectingDispatchController;
import com.f1.povo.f1app.F1AppClass;
import com.f1.povo.f1app.F1AppContainerScope;
import com.f1.povo.f1app.F1AppDatabase;
import com.f1.povo.f1app.F1AppDispatcher;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppLogger;
import com.f1.povo.f1app.F1AppLoggerSink;
import com.f1.povo.f1app.F1AppMsgTopic;
import com.f1.povo.f1app.F1AppPartition;
import com.f1.povo.f1app.F1AppPort;
import com.f1.povo.f1app.F1AppProcessor;
import com.f1.povo.f1app.F1AppProperty;
import com.f1.povo.f1app.F1AppState;
import com.f1.povo.f1app.F1AppThreadScope;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailEventList;
import com.f1.povo.f1app.audit.F1AppAuditTrailF1Event;
import com.f1.povo.f1app.audit.F1AppAuditTrailLoggerEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailMsgEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.povo.f1app.audit.F1AppAuditTrailRuleSet;
import com.f1.povo.f1app.audit.F1AppAuditTrailSqlEvent;
import com.f1.povo.f1app.reqres.F1AppChangeLogLevelRequest;
import com.f1.povo.f1app.reqres.F1AppChangeLogLevelResponse;
import com.f1.povo.f1app.reqres.F1AppChangesRequest;
import com.f1.povo.f1app.reqres.F1AppChangesResponse;
import com.f1.povo.f1app.reqres.F1AppInterruptThreadRequest;
import com.f1.povo.f1app.reqres.F1AppRequest;
import com.f1.povo.f1app.reqres.F1AppResponse;
import com.f1.povo.f1app.reqres.F1AppSnapshotRequest;
import com.f1.povo.f1app.reqres.F1AppSnapshotResponse;
import com.f1.povo.msg.MsgMessage;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.povo.standard.BatchMessage;
import com.f1.povo.standard.MapMessage;
import com.f1.povo.standard.ObjectMessage;
import com.f1.povo.standard.TextMessage;
import com.f1.utils.AH;
import com.f1.utils.BasicIdeableGenerator;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.BundledTextFormatter;
import com.f1.utils.CH;
import com.f1.utils.ClassFinder;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.SearchPath;
import com.f1.utils.TableHelper;
import com.f1.utils.VidParser;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.ids.BasicIdGenerator;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.NamespaceIdGenerator;
import com.f1.utils.impl.BasicClock;
import com.f1.utils.impl.SimulatedClock;
import com.f1.utils.mirror.reflect.ReflectedClassMirror;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;

@Console(name = "Bootstrap", help = "Provides access to various tools and services used for bootstapping the application")
public class ContainerBootstrap extends Bootstrap {
	private IdeableGenerator generator;
	private static AppMonitorContainer appMonitor;
	public static final int STATE_CONTAINER_DEPENDENCIES_INIT = STATES.define(1024, "container_dependencies_init");
	public static final int STATE_CONTAINER_PREPARED = STATES.define(2048, "container_prepared");
	private static final int STATE_APPMONITOR_STARTED = STATES.define(4096, "monitor_started");

	private static final char DELIM = ',';

	static {
		EH.getStartTime();
	}

	private CodeCompiler compiler;
	private HashMap<String, Container> containers = new LinkedHashMap<String, Container>();
	private NamespaceIdGenerator<Long> idGenerator;
	private OfflineConverter converter;
	private Object waitForContainersSemephore = new Object();
	private Class[] messagesToRegister = OH.EMPTY_CLASS_ARRAY;
	private Class[] DEFAULT_MESSAGES = new Class[] { MapMessage.class, TextMessage.class, ObjectMessage.class, BatchMessage.class, RequestMessage.class, ResultMessage.class,
			MsgMessage.class, PersistenceRoot.class, MsgStatusMessage.class };
	private Class[] DEFAULT_F1APP_MESSAGES = new Class[] { F1AppInstance.class, F1AppContainerScope.class, F1AppEntity.class, F1AppPartition.class, F1AppPort.class,
			F1AppProcessor.class, F1AppState.class, F1AppClass.class, F1AppThreadScope.class, F1AppLogger.class, F1AppLoggerSink.class, F1AppDispatcher.class, F1AppDatabase.class,
			F1AppMsgTopic.class, F1AppProperty.class, F1AppAuditTrailRuleSet.class, F1AppAuditTrailRule.class, F1AppAuditTrailEvent.class, F1AppAuditTrailEventList.class,
			F1AppAuditTrailLoggerEvent.class, F1AppAuditTrailSqlEvent.class, F1AppAuditTrailMsgEvent.class, F1AppAuditTrailF1Event.class, F1AppInterruptThreadRequest.class,
			F1AppResponse.class, F1AppRequest.class, F1AppSnapshotRequest.class, F1AppSnapshotResponse.class, F1AppChangesRequest.class, F1AppChangesResponse.class,
			F1AppChangeLogLevelRequest.class, F1AppChangeLogLevelResponse.class };
	private CodeGenerator codeGenerator;
	final private Map<String, Object> customerContainerServices = new LinkedHashMap<String, Object>();

	@Override
	protected void initDefaults() {
		super.initDefaults();
		setAutocodedDirectoryProperty(F1Constants.DEFAULT_AUTOCODED_DIR);
		setAutocodedDirectoryCleanProperty(F1Constants.DEFAULT_AUTOCODED_DIR_CLEAN);
		setAutocodedDisabledProperty(F1Constants.DEFAULT_AUTOCODED_DISABLED);
		setInspectingModeProperty(F1Constants.DEFAULT_INSPECTING_MODE);
		setThreadPoolDefaultSizeProperty(F1Constants.DEFAULT_THREADPOOL_SIZE);
		setUseAggressiveThreadPool(F1Constants.DEFAULT_THREADPOOL_AGGRESIVE);
		setUseThreadPoolFastExecute(F1Constants.DEFAULT_THREADPOOL_FAST_EXECUTE);
		setTextBundleFiles(F1Constants.DEFAULT_TEXT_BUNDLE_FILES);
		setTextBundleOptions(F1Constants.DEFAULT_TEXT_BUNDLE_OPTIONS);
		setTextBundleDir(F1Constants.DEFAULT_TEXT_BUNDLE_DIR);
		setF1MonitoringEnabledProperty(F1Constants.DEFAULT_F1_MONITORING_ENABLED);
		setPluginsDir(F1Constants.DEFAULT_PLUGINS_DIR);
		setLogPerformanceLevel(null);
		setMessagePackagesProperty("");
		setConverterProperty("");
	}

	public ContainerBootstrap(Class mainClass, String[] args) {
		super(mainClass, args);
	}

	public String getTextBundleOptions() {
		return getProperty(F1Constants.PROPERTY_TEXT_BUNDLE_OPTIONS);
	}

	public void setTextBundleOptions(String value) {
		setProperty(F1Constants.PROPERTY_TEXT_BUNDLE_OPTIONS, value);
	}

	public String getTextBundleFiles() {
		return getProperty(F1Constants.PROPERTY_TEXT_BUNDLE_FILES);
	}

	public void setTextBundleFiles(String textBundleFiles) {
		setProperty(F1Constants.PROPERTY_TEXT_BUNDLE_FILES, textBundleFiles);
	}
	public Level getLogPerformanceLevel() {
		String r = getProperty(F1Constants.PROPERTY_F1_LOG_PERFORMANCE_LEVEL);
		return SH.is(r) ? Level.parse(r.trim()) : null;
	}

	public void setLogPerformanceLevel(Level level) {
		setProperty(F1Constants.PROPERTY_F1_LOG_PERFORMANCE_LEVEL, level == null ? "" : level.getName());
	}
	public void setPluginsDir(String dir) {
		assertState(STATE_CREATED, STATE_PROPERTIES_READ);
		setProperty(F1Constants.PROPERTY_PLUGINS_DIR, dir);
	}
	public String getPluginsDir() {
		return getProperty(F1Constants.PROPERTY_PLUGINS_DIR);
	}

	public String getTextBundleDir() {
		return getProperty(F1Constants.PROPERTY_TEXT_BUNDLE_DIR);
	}

	public void setTextBundleDir(String textBundleDir) {
		setProperty(F1Constants.PROPERTY_TEXT_BUNDLE_DIR, textBundleDir);
	}

	// #### OPTIONS ####
	public String getMessagePackagesProperty() {
		return getProperty(F1Constants.PROPERTY_MESSAGE_PACKAGES);
	}

	public String getConverterProperty() {
		return getProperty(F1Constants.PROPERTY_CONVERTER);
	}

	public void setMessagePackagesProperty(String value) {
		setProperty(F1Constants.PROPERTY_MESSAGE_PACKAGES, value);
	}

	public void setConverterProperty(String value) {
		setProperty(F1Constants.PROPERTY_CONVERTER, value);
	}

	private boolean getF1MonitoringEnabledProperty() {
		return "true".equals(getProperty(F1Constants.PROPERTY_F1_MONITORING_ENABLED));
	}
	public void setF1MonitoringEnabledProperty(boolean enabled) {
		setProperty(F1Constants.PROPERTY_F1_MONITORING_ENABLED, Boolean.toString(enabled));
	}

	@Override
	public void processProperties() {
		super.processProperties();
		try {
			assertState(STATE_CREATED | STATE_PROPERTIES_READ, STATE_CONTAINER_DEPENDENCIES_INIT | STATE_ERROR);
			if (getCompiler() == null)
				initCompiler();
			if (getCodeGenerator() == null)
				initCodeGenerator();
			if (getGenerator() == null)
				initGenerator();
			if (getIdGenerator() == null)
				initIdGenerator();
			if (getConverter() == null)
				initConverter();
			processPlugins();
			if (appMonitor == null && getF1MonitoringEnabledProperty()) {
				synchronized (ContainerBootstrap.class) {
					if (appMonitor == null) {
						appMonitor = AppMonitorContainer.init(this);
						this.appMonitor.start();
					}
				}
			}
			applyState(STATE_APPMONITOR_STARTED);
			initRegisteredClasses();
			applyState(STATE_CONTAINER_DEPENDENCIES_INIT);
		} catch (Exception e) {
			applyState(STATE_ERROR);
			throw OH.toRuntime(e);
		}
	}

	private void initIdGenerator() {
		setIdGenerator(new BasicNamespaceIdGenerator<Long>(new BasicIdGenerator.Factory(0)));
	}

	protected void initCodeGenerator() throws IOException {
		setCodeGenerator(new BasicCodeGenerator(getCompiler(), true));
	}

	protected void initGenerator() {
		setGenerator(new BasicIdeableGenerator(getCodeGenerator()));
	}

	protected void initCompiler() throws IOException {
		if (getAutocodedDisabledProperty()) {
			setCompiler(new DummyCodeCompiler(getAutocodedDirectoryProperty()));
			return;
		}
		if (getAutocodedDirectoryCleanProperty()) {
			printInfo("CLEANING AUTOCODED DIRECTORY: " + getAutocodedDirectoryProperty());
			IOH.deleteForce(new File(getAutocodedDirectoryProperty()));
		}

		BasicCodeCompiler coderCompiler = new BasicCodeCompiler(getAutocodedDirectoryProperty());
		setCompiler(coderCompiler);
	}
	public void setTimeDilateProperty(String value) {
		setProperty(F1Constants.PROPERTY_TIME_DILATE, value);
	}

	public String getTimeDilateProperty() {
		return getProperty(F1Constants.PROPERTY_TIME_DILATE);
	}

	public void setTimeShiftProperty(String value) {
		setProperty(F1Constants.PROPERTY_TIME_DILATE, value);
	}

	public String getTimeShiftProperty() {
		return getProperty(F1Constants.PROPERTY_TIME_SHIFT);
	}

	public void setTimeStartProperty(String value) {
		setProperty(F1Constants.PROPERTY_TIME_START, value);
	}

	public String getTimeStartProperty() {
		return getProperty(F1Constants.PROPERTY_TIME_START);
	}

	public String getAutocodedDirectoryProperty() {
		return getProperty(F1Constants.PROPERTY_AUTOCODED_DIR);
	}

	public void setAutocodedDirectoryProperty(String value) {
		setProperty(F1Constants.PROPERTY_AUTOCODED_DIR, value);
	}

	public boolean getAutocodedDirectoryCleanProperty() {
		return Boolean.parseBoolean(getProperty(F1Constants.PROPERTY_AUTOCODED_DIR_CLEAN));
	}

	public void setAutocodedDirectoryCleanProperty(boolean value) {
		setProperty(F1Constants.PROPERTY_AUTOCODED_DIR_CLEAN, Boolean.toString(value));
	}
	public boolean getAutocodedDisabledProperty() {
		return Boolean.parseBoolean(getProperty(F1Constants.PROPERTY_AUTOCODED_DISABLED));
	}

	public void setAutocodedDisabledProperty(boolean value) {
		setProperty(F1Constants.PROPERTY_AUTOCODED_DISABLED, Boolean.toString(value));
	}

	public boolean getInspectingModeProperty() {
		return Boolean.parseBoolean(getProperty(F1Constants.PROPERTY_INSPECTING_MODE));
	}

	public void setInspectingModeProperty(boolean value) {
		setProperty(F1Constants.PROPERTY_INSPECTING_MODE, Boolean.toString(value));
	}

	public int getThreadPoolDefaultSizeProperty() {
		return Integer.parseInt(getProperty(F1Constants.PROPERTY_THREADPOOL_DEFAULT_SIZE));
	}

	public void setThreadPoolDefaultSizeProperty(int value) {
		setProperty(F1Constants.PROPERTY_THREADPOOL_DEFAULT_SIZE, Integer.toString(value));
	}

	protected void initConverter() {
		String converterClass = getConverterProperty();
		if (SH.isnt(converterClass))
			setConverter(new ObjectToByteArrayConverter());
		else
			setConverter((OfflineConverter) RH.invokeConstructor(converterClass));
		getConverter().setIdeableGenerator(getGenerator());
	}

	public void initRegisteredClasses() {

		String packages = getMessagePackagesProperty();
		if (SH.is(packages)) {
			registerMessagesInPackages(packages);
		}
		getGenerator().register(DEFAULT_MESSAGES);
		if (getF1MonitoringEnabledProperty())
			getGenerator().register(DEFAULT_F1APP_MESSAGES);
		getGenerator().register(messagesToRegister);
		messagesToRegister = OH.EMPTY_CLASS_ARRAY;
	}

	// Startup routines
	@Override
	public void startup() {
		super.startup();
		assertState(STATE_CONTAINER_DEPENDENCIES_INIT, STATE_ERROR);
	}

	public void prepareContainer(Container c) {
		if (!MH.areAllBitsSet(getState(), STATE_STARTED))
			startup();
		assertState(STATE_STARTED, STATE_ERROR);
		File[] files = new SearchPath(getTextBundleDir())
				.search(getTextBundleFiles(), SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_RECURSE | SearchPath.OPTION_IS_PATTERN).toArray(new File[0]);
		final ContainerServices services = c.getServices();
		for (File file : files)
			services.getLocaleFormatterManager().addMessageFile(file);
		services.getLocaleFormatterManager().setBundleTextOptions(parseTextBundlesOptions(getTextBundleOptions()));
		c.getThreadPoolController().setDefaultThreadPoolSize(getThreadPoolDefaultSizeProperty());
		c.getThreadPoolController().setUseAggressiveThreadPool(getUseAggressiveThreadPool());
		c.getThreadPoolController().setUseThreadPoolFastExecute(getUseThreadPoolFastExecute());

		services.setClock(createClock());
		services.setGenerator(getGenerator());
		services.setPropertyController(getProperties());
		if (services.getUidGenerator() == null)
			services.setUidGenerator(getIdGenerator());
		services.setConverter(getConverter());
		for (Entry<String, Object> e : this.customerContainerServices.entrySet())
			services.putService(e.getKey(), e.getValue());
		if (c.getClass().getSimpleName().equals(c.getName()))
			c.setName(getAppNameProperty());
		CH.putOrThrow(containers, c.getName(), c);
		if (getInspectingModeProperty()) {
			final BasicContainer basicContainer = (BasicContainer) c;
			final DispatchController dc = basicContainer.getDispatchController();
			final InspectingDispatchController idc = new InspectingDispatchController(dc);
			basicContainer.setDispatchController(idc);
		}
		if (getLogPerformanceLevel() != null) {
			BasicDispatcherController controller = ContainerHelper.getBasicDispatchController(c);
			controller.setPerformanceLoggingLevel(getLogPerformanceLevel());
		}
		final String containerName;
		final boolean nameIsDefault = SH.equals(c.getName(), c.getClass().getSimpleName());
		if (nameIsDefault)
			containerName = F1Constants.DEFAULT_CONTAINER_NAME;
		else
			containerName = SH.lowercaseFirstChar(OH.noNull(c.getName(), getMainClass().getSimpleName()));
		Set<String> existingNames = getRegisteredConsoleObjects();
		String uniqueName = SH.getNextId("container_" + containerName, existingNames);
		registerConsoleObject(uniqueName, new ContainerConsole(c));
		if (appMonitor != null)
			appMonitor.onNewContainer(c);
		if (!c.isInit())
			c.init();
		applyState(STATE_CONTAINER_PREPARED);
	}
	public boolean getUseAggressiveThreadPool() {
		return Boolean.parseBoolean(getProperty(F1Constants.PROPERTY_THREADPOOL_AGGRESSIVE));
	}
	public void setUseAggressiveThreadPool(boolean useAggressiveThreadPool) {
		setProperty(F1Constants.PROPERTY_THREADPOOL_AGGRESSIVE, Boolean.toString(useAggressiveThreadPool));
	}

	public boolean getUseThreadPoolFastExecute() {
		return Boolean.parseBoolean(getProperty(F1Constants.PROPERTY_THREADPOOL_FAST_EXECUTE));
	}
	public void setUseThreadPoolFastExecute(boolean fastExecute) {
		setProperty(F1Constants.PROPERTY_THREADPOOL_FAST_EXECUTE, Boolean.toString(fastExecute));
	}

	public Clock createClock() {
		String dilate = getTimeDilateProperty();
		String shift = getTimeShiftProperty();
		String start = SH.trim(getTimeStartProperty());
		long shiftValue = shift == null ? 0 : parseToNano(shift);
		double dilateValue = 1d;
		if (dilate != null) {
			long from = parseToNano(SH.beforeFirst(dilate, ':'));
			long to = parseToNano(SH.afterFirst(dilate, ':'));
			dilateValue = (double) to / from;
			if (Double.isInfinite(dilateValue) || Double.isNaN(dilateValue))
				throw new IllegalArgumentException("invalid time.dilate: " + dilate);
		}
		if (start != null) {
			try {
				long startValue;
				LocaleFormatter f = new BasicLocaleFormatter(getLocale(), getTimeZone(), false, null, null, parseTextBundlesOptions(getTextBundleOptions()));
				if (start.length() == 17) {
					startValue = ((Date) f.getDateFormatter(LocaleFormatter.DATETIME).parse(start)).getTime();
				} else if (start.length() == 8) {
					startValue = ((Date) f.getDateFormatter(LocaleFormatter.DATETIME)
							.parse(f.getDateFormatter(LocaleFormatter.DATE).format(new Date(EH.currentTimeMillis())) + "-" + start)).getTime();
				} else
					throw new IllegalArgumentException("invalid format");
				shiftValue += (startValue - new Date().getTime()) * 1000000L;
			} catch (Exception e) {
				throw new IllegalArgumentException("invalid time.start (should be in format yyyymmdd-hh:mm:ss or hh:mm:ss: " + start, e);
			}
		}
		if (shiftValue == 0 && OH.eq(dilateValue, 1d, .0001)) {
			return new BasicClock(getTimeZone(), getLocale());
		}
		return new SimulatedClock(getTimeZone(), getLocale(), shiftValue, dilateValue);
	}

	private static Map<String, Integer> TEXT_BUNDLER_OPTIONS = new HashMap<String, Integer>();
	static {
		TEXT_BUNDLER_OPTIONS.put(F1Constants.TBO_KEY_MISSING_RETURN_NULL, BundledTextFormatter.OPTION_ON_KEY_MISSING_RETURN_NULL);
		TEXT_BUNDLER_OPTIONS.put(F1Constants.TBO_KEY_MISSING_PRINT_DETAILED, BundledTextFormatter.OPTION_ON_KEY_MISSING_PRINT_DETAILED);
		TEXT_BUNDLER_OPTIONS.put(F1Constants.TBO_KEY_MISSING_DEBUG, BundledTextFormatter.OPTION_ON_KEY_MISSING_DEBUG);
		TEXT_BUNDLER_OPTIONS.put(F1Constants.TBO_ON_ARG_MISSING_PRINT_KEY, BundledTextFormatter.OPTION_ON_ARG_MISSING_PRINT_KEY);
		TEXT_BUNDLER_OPTIONS.put(F1Constants.TBO_ON_ARG_MISSING_PRINT_NULL, BundledTextFormatter.OPTION_ON_ARG_MISSING_PRINT_NULL);
		TEXT_BUNDLER_OPTIONS.put(F1Constants.TBO_ON_ARG_ERROR_EMBED_MESSAGE, BundledTextFormatter.OPTION_ON_ARG_ERROR_EMBED_MESSAGE);
		TEXT_BUNDLER_OPTIONS.put(F1Constants.TBO_LOG_EXCEPTIONS, BundledTextFormatter.OPTION_LOG_EXCEPTIONS);
		TEXT_BUNDLER_OPTIONS.put(F1Constants.TBO_LOG_STACKTRACE, BundledTextFormatter.OPTION_LOG_STACKTRACE);
		TEXT_BUNDLER_OPTIONS.put(F1Constants.TBO_KEYS_START_WITH_BANGBANG, BundledTextFormatter.OPTION_KEYS_START_WITH_BANGBANG);
	}

	private int parseTextBundlesOptions(String textBundleOptions) {
		int r = 0;
		for (String option : SH.trimArray(SH.splitContinous(',', textBundleOptions)))
			r |= CH.getOrThrow(TEXT_BUNDLER_OPTIONS, option, "invalid option for " + F1Constants.PROPERTY_TEXT_BUNDLE_OPTIONS);
		return r;
	}
	public void startupContainer(Container c) {
		if (!MH.areAllBitsSet(getState(), STATE_STARTED))
			startup();
		assertState(STATE_STARTED, STATE_ERROR);
		Container existing = containers.get(c.getName());
		if (existing == null)
			prepareContainer(c);
		else if (existing != c)
			throw new RuntimeException("Container already registered with same name: " + c.getName());
		c.start();
		synchronized (waitForContainersSemephore) {
			CH.putOrThrow(containers, c.getName(), c);
			waitForContainersSemephore.notify();
		}
	}

	public void blockUntilContainersStarted(int numberOfContainers) {
		synchronized (waitForContainersSemephore) {
			while (containers.size() < numberOfContainers)
				OH.wait(waitForContainersSemephore);
		}
	}

	public void setCompiler(CodeCompiler compiler) {
		assertState(STATE_CREATED, STATE_ERROR | STATE_CONTAINER_PREPARED);
		this.compiler = compiler;
	}

	public CodeCompiler getCompiler() {
		return compiler;
	}

	public void setCodeGenerator(CodeGenerator codeGenerator) {
		assertState(STATE_CREATED, STATE_ERROR | STATE_CONTAINER_PREPARED);
		this.codeGenerator = codeGenerator;
	}

	public CodeGenerator getCodeGenerator() {
		return codeGenerator;
	}

	public void setGenerator(IdeableGenerator generator) {
		assertState(STATE_CREATED, STATE_ERROR | STATE_CONTAINER_PREPARED | STATE_APPMONITOR_STARTED);
		this.generator = generator;
	}

	public void setIdGenerator(NamespaceIdGenerator<Long> generator) {
		assertState(STATE_CREATED, STATE_ERROR | STATE_CONTAINER_PREPARED);
		this.idGenerator = generator;
	}

	public NamespaceIdGenerator<Long> getIdGenerator() {
		return idGenerator;
	}

	public void setConverter(OfflineConverter converter) {
		assertState(STATE_CREATED, STATE_ERROR | STATE_CONTAINER_PREPARED);
		this.converter = converter;
	}

	public OfflineConverter getConverter() {
		return this.converter;
	}

	@Console(help = "Stops all running containers by calling container::stop() then does a system exit.")
	public void stopAndExit() {
		printInfo("stopAndExit called.");
		for (Container container : containers.values()) {
			if (!container.isStarted())
				continue;
			try {
				printInfo("Calling  stop() on container: " + container.getName());
				container.stop();
				printInfo("Called stop() on container: " + container.getName());
			} catch (Exception e) {
				printError("Error shutting down " + container.getName());
			}
		}
		printError("shutdown called.");
		final int exitCode = 0;
		printError("calling System.exit(" + exitCode + ");  //good bye");
		EH.systemExit(0);
	}

	public void registerMessages(Class<? extends Message>... clazz) {
		if (getGenerator() == null)
			messagesToRegister = AH.cat(messagesToRegister, clazz);
		else
			getGenerator().register(clazz);
	}

	public void registerMessagesInPackages(Package... packages) {
		registerMessagesInPackages(getGenerator(), packages);
	}
	public void registerMessagesInPackages(String... packages) {
		registerMessagesInPackages(getGenerator(), packages);
	}
	public static void registerMessagesInPackages(IdeableGenerator generator, Package... packages) {
		String packageStrings[] = new String[packages.length];
		for (int i = 0; i < packages.length; i++)
			packageStrings[i] = packages[i].getName();
		registerMessagesInPackages(generator, packageStrings);
	}
	public static void registerMessagesInPackages(IdeableGenerator generator, String... packages) {
		try {
			List<Class> found = new ArrayList<Class>();
			ClassFinder finder = new ClassFinder().searchClasspath(ClassFinder.TYPE_DIRECTORY | ClassFinder.TYPE_JAR);
			for (String pckg : packages) {
				for (String s : SH.trimArray(SH.split(DELIM, pckg))) {
					Collection<Class> classes = finder.filterByPackage(s).toReflected().filterByExtends(ReflectedClassMirror.valueOf(Message.class)).getClasses();
					for (Class clazz : classes)
						if (!CodeGenerated.class.isAssignableFrom(clazz))
							found.add(clazz);
				}
			}
			generator.register(found.toArray(new Class[found.size()]));
		} catch (IOException e) {
			throw new RuntimeException("error registering messages in " + packages, e);
		}
	}

	public String showRegisteredClasses() {
		IdeableGenerator g = getGenerator();
		if (g == null)
			return "<Generator Not Registered>";
		BasicTable table = new BasicTable(new Class[] { String.class, String.class, String.class, String.class }, new String[] { "VID", "VIN", "Class", "Type" });
		StringBuilder sb = new StringBuilder();
		table.setTitle("Registered Classes");
		for (Class o : g.getRegistered()) {
			long vid = VidParser.getVid(o);
			String vidText = VidParser.fromLong(vid);
			String vin = VidParser.getVin(o);
			table.getRows().addRow(vid == -1 ? "" : (" " + vidText + " (" + vid + ") "), vin == null ? "" : '"' + vin + '"', o.getName(),
					Message.class.isAssignableFrom(o) ? "Message" : "Other");
		}
		for (Class o : ValuedEnumCache.getCached()) {
			long vid = VidParser.getVid(o);
			String vidText = VidParser.fromLong(vid);
			String vin = VidParser.getVin(o);
			Class type = ValuedEnumCache.getCache(o).getReturnType();
			table.getRows().addRow(vid == -1 ? "" : (" " + vidText + " (" + vid + ") "), vin == null ? "" : '"' + vin + '"', o.getName(),
					"ValuedEnum<" + type.getSimpleName() + ">");
		}
		TableHelper.sort(table, "VID", "Type");
		TableHelper.toString(table, "", MH.clearBits(TableHelper.SHOW_ALL, TableHelper.SHOW_TYPES), sb);
		return sb.toString();
	}

	public static long parseToNano(String text) {
		text = text.trim();
		int index = text.indexOf(' ');
		if (index == -1)
			throw new IllegalArgumentException("must be in format 'nnn unit': " + text);
		long quantity = Long.parseLong(text.substring(0, index).trim());
		TimeUnit unit = (TimeUnit) Caster_Simple.OBJECT.cast(text.substring(index + 1).toUpperCase());
		return unit.toNanos(quantity);
	}

	public IdeableGenerator getGenerator() {
		return generator;
	}

	public static AppMonitorContainer getAppMonitor() {
		return appMonitor;
	}

	public Collection<Container> getContainers() {
		return containers.values();
	}

	private static boolean pluginsProcessed;

	private void processPlugins() {
		if (pluginsProcessed)
			return;
		pluginsProcessed = true;
		File root = new File(getPluginsDir());
		if (root.isDirectory()) {
			String rootPath = IOH.getFullPath(root);
			try {
				final List<File> javaFiles = new SearchPath(root).search("/\\.*\\.java$",
						SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);
				if (CH.isntEmpty(javaFiles)) {
					final List<Tuple2<String, String>> classes = new ArrayList<Tuple2<String, String>>();
					for (File file : javaFiles) {
						final String src = IOH.readText(file);
						final String className = SH.trim('.',
								SH.replaceAll(SH.stripPrefix(SH.stripSuffix(IOH.getFullPath(file), ".java", true), rootPath, true), File.separatorChar, '.'));
						classes.add(new Tuple2<String, String>(className, src));
					}
					List<Boolean> results = this.compiler.compile(classes);
					for (int i = 0; i < results.size(); i++) {
						if (!results.get(i).booleanValue())
							EH.toStdout("Failed to build plugin: " + classes.get(i).getA(), true);
					}
				}
				final List<File> classes = new SearchPath(root).search("/\\.*\\.class$",
						SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);
				for (File f : classes)
					ClassPathModifier.addFile(f);
				final List<File> jars = new SearchPath(root).search("/\\.*\\.jar$",
						SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);
				for (File f : jars)
					ClassPathModifier.addFile(f);
				int tot = javaFiles.size() + classes.size() + jars.size();
				if (tot > 0)
					EH.toStdout("F1 Loaded " + tot + " plugin(s) including " + javaFiles.size() + " .java, " + classes.size() + " .class and " + jars.size()
							+ " .jar file(s) &  from: " + rootPath, true);
			} catch (Exception e) {
				throw new RuntimeException("Error processing plugin directory: " + rootPath, e);
			}
		} else if (root.isFile())
			throw new RuntimeException(F1Constants.PROPERTY_PLUGINS_DIR + " is a file, should be directory: " + IOH.getFullPath(root));
	}

	public void addContainerService(String serviceName, Object service) {
		assertState(STATE_CREATED, STATE_ERROR | STATE_CONTAINER_PREPARED);
		this.customerContainerServices.put(serviceName, service);
	}

}
