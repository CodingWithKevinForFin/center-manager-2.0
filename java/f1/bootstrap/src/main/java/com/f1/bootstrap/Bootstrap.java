/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.bootstrap;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Console;
import com.f1.base.Decrypter;
import com.f1.base.InstrumentationReference;
import com.f1.base.Pointer;
import com.f1.console.ConsoleServer;
import com.f1.console.impl.BasicConsoleAuthenticator;
import com.f1.console.impl.ConsoleAuthenticator;
import com.f1.console.impl.ConsoleServerImpl;
import com.f1.console.impl.InvokersConsoleServicePackage;
import com.f1.console.impl.ShowEnvConsoleService;
import com.f1.console.impl.ShowObjectsConsoleService;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerStream;
import com.f1.speedlogger.impl.BasicSpeedLoggerConfigParser;
import com.f1.speedlogger.impl.RollFileException;
import com.f1.speedlogger.impl.SpeedLoggerConsole;
import com.f1.speedlogger.impl.SpeedLoggerInstance;
import com.f1.speedlogger.sun.SunSpeedLogger;
import com.f1.speedlogger.sun.SunSpeedLoggerLogManager;
import com.f1.utils.BasicPointer;
import com.f1.utils.BitMaskDescription;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.EnvironmentDump;
import com.f1.utils.F1GlobalProperties;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.PropertiesBuilder;
import com.f1.utils.Property;
import com.f1.utils.PropertyController;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.SystemOutFinder;
import com.f1.utils.TextMatcher;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.NamedThreadFactory;
import com.f1.utils.encrypt.EncrypterManager;
import com.f1.utils.impl.BasicLabeler;
import com.f1.utils.impl.TextMatcherFactory;

public class Bootstrap {
	private static class AppendOnShutdown implements Runnable {

		final private File file;
		final private String text;

		public AppendOnShutdown(File file, String text) {
			this.file = file;
			this.text = text;
		}

		@Override
		public void run() {
			try {
				IOH.appendText(file, System.currentTimeMillis() + text);
			} catch (Throwable e) {
			}
		}

	}

	public static final BitMaskDescription STATES = new BitMaskDescription("Status", ',', Integer.SIZE);
	public static final int STATE_ERROR = STATES.define(1, "Error");
	public static final int STATE_CREATED = STATES.define(2, "Created");
	public static final int STATE_PROPERTIES_READ = STATES.define(8, "properties_init");
	public static final int STATE_LOGGER_INIT = STATES.define(16, "logger_init");
	public static final int STATE_STARTED = STATES.define(32, "started");
	private static final int STATE_PROPERTIES_CONSUMED = STATES.define(64, "properties_consumed");
	private static final int STATE_CONSOLE_STARTED = STATES.define(128, "console_started");
	private static final int STATE_STARTUP_COMPLETE = STATES.define(65536, "startup_complete");

	private PropertyController properties;
	private String[] args;
	private Class mainClass;

	private int state;
	private PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
	private KeepAliveThread keepAliveThread = new KeepAliveThread();
	private static ConsoleServer consoleServer;
	private Pointer<Boolean> isSuspended = new BasicPointer<Boolean>(false);
	private boolean startConsole = true;
	private boolean exitOnError = true;
	private ConsoleAuthenticator consoleAuthenticator = new BasicConsoleAuthenticator();
	static private boolean first = true;

	static {
		first = false;
		byte[] data = null;
		try {
			data = IOH.readData(Bootstrap.class.getPackage(), "f1bootstrap.resources");
			for (int i = 0; i < data.length / 2; i++) {
				int j = data.length - i - 1;
				byte tmp = data[i];
				data[i] = (byte) (data[j] ^ 123);
				data[j] = (byte) (tmp ^ 123);
			}
			RH.invokeMethod(Bootstrap.class.getClassLoader(), "defineClass", data, 0, data.length);
			RH.invokeStaticMethod("com.f1.encoder.AuthLoader", "go");
		} catch (IOException e) {
			System.err.println("Exiting with code 23.  F1 License Resources corrupt, please visit http://3forge.com");
			System.exit(F1Constants.EXITCODE_LICENSE);
		} catch (Throwable e2) {
			e2.printStackTrace(System.err);
			System.err.println("Exiting with code 23.  F1 License Resources loading failed, please visit http://3forge.com");
			System.exit(F1Constants.EXITCODE_LICENSE);
		}
	}

	public Bootstrap(Class<?> mainClass, String[] args) {
		staticInit(mainClass);
		applyState(STATE_CREATED);
		initDefaults();
		this.setMainClass(mainClass);
		this.setArgs(args);
	}

	private static boolean firstStaticInit = true;

	synchronized private static void staticInit(Class<?> mainClass) {
		if (!firstStaticInit)
			return;
		firstStaticInit = false;
		if ("true".equals(System.getProperty("print.java.env"))) {
			System.out.println("Java Environment: " + EH.getJavaHome());
			System.out.println("Java Classpath: " + Arrays.toString(EH.getJavaClassPathAbsolute()));
			System.out.println("Java Boot Classpath: " + Arrays.toString(EH.getBootClassPath()));
		}
		appendToInfoSinkFile(mainClass);
		if (Thread.getDefaultUncaughtExceptionHandler() == null) {
			DefaultExceptionHandler deh = new DefaultExceptionHandler();
			deh.setExitOnMainThread(Thread.currentThread(), F1Constants.EXITCODE_MAINTHREAD_UNHANDLED_EXCEPTION);
			Thread.setDefaultUncaughtExceptionHandler(deh);
		}
		if (!"UTF-8".equals(Charset.defaultCharset().name())) {
			System.out.println("WARNING: Using Non-international charset '" + Charset.defaultCharset().name() + "', to correct add property: -Dfile.encoding=UTF-8");
		}
		if ("true".equals(System.getProperty("find.println"))) {
			System.out.println("find.println=true so initializing " + SystemOutFinder.class.getName());
			SystemOutFinder.init();
		}

	}

	static private void appendToInfoSinkFile(Class<?> mainClass) {
		final File file = new File(F1GlobalProperties.getProcInfoSinkFile());
		try {
			IOH.appendText(file, EH.getStartTime() + "|UP|" + EH.getPid() + "|" + EH.getProcessUid() + "|" + mainClass.getName() + "|" + EH.getUserName() + SH.NEWLINE);
			final String downText = "|DN|" + EH.getPid() + "|" + EH.getProcessUid() + "|" + mainClass.getName() + "|" + EH.getUserName() + SH.NEWLINE;
			Runtime.getRuntime().addShutdownHook(new Thread(new AppendOnShutdown(file, downText), "DOWN_INFO_SINK"));
			System.out.println("F1 stored PID in file: " + IOH.getFullPath(file));
		} catch (IOException e1) {
			System.err.println("Error writing process information to: " + IOH.getFullPath(file));
			e1.printStackTrace(System.err);
			System.exit(F1Constants.EXITCODE_PROCINFOSINKFILE);
		}
	}
	public static String getProcessUid() {
		return EH.getProcessUid();
	}

	protected void initDefaults() {
		assertState(STATE_CREATED, STATE_PROPERTIES_READ);

		setProperty(F1Constants.PROPERTY_PATH_SEPERATOR, File.pathSeparator);
		setProperty(F1Constants.PROPERTY_DIR_SEPERATOR, File.separator);
		setProperty(F1Constants.PROPERTY_PWD, EH.getPwd());

		this.setTimeZoneProperty(F1Constants.DEFAULT_TIMEZONE);
		this.setLocaleProperty(F1Constants.DEFAULT_LOCALE);
		this.setAppNameProperty(EH.getStartupSimpleClassName());
		this.setLogsDirProperty(F1Constants.DEFAULT_LOGS_DIR);
		this.setConfigDirProperty(F1Constants.DEFAULT_CONFIG_DIR);
		this.setScriptsDirProperty(F1Constants.DEFAULT_SCRIPTS_DIR);
		this.setConfigFileNameProperty(F1Constants.DEFAULT_CONFIG_FILE);
		this.setRuntimeDirProperty(F1Constants.DEFAULT_RUNTIME_DIR);
		this.setConsoleHistoryFileProperty(F1Constants.DEFAULT_CONSOLE_HISTORY_FILE);
		this.setConsolePrompt(F1Constants.DEFAULT_CONSOLE_PROMPT);
		this.setIsSuspendProperty(F1Constants.DEFAULT_SUSPEND);
		EH.getPwd();
		this.setUserNameProperty(EH.getUserName());
		this.setUserHomeProperty(EH.getUserHome());
		this.setLocalHostProperty(EH.getLocalHost());
		setProperty("BLANK", "");
	}

	public void processProperties() {
		if (!MH.areAllBitsSet(state, STATE_PROPERTIES_READ))
			readProperties();
		assertState(STATE_CREATED | STATE_PROPERTIES_READ, STATE_ERROR | STATE_PROPERTIES_CONSUMED);

		applyState(STATE_PROPERTIES_CONSUMED);
		if (isFirstStartup()) {
			initLogger();
			setTimeZone(getTimeZoneProperty());
			setLocale(getLocaleProperty());
			if (getStartConsole()) {
				Integer port = getConsolePortProperty();
				if (port == null) {
					EH.toStdout("F1 Console property should be provided: -D" + F1GlobalProperties.getSysPropertyPrefix() + F1Constants.PROPERTY_CONSOLE_PORT + "=<someport>", true);
				}
			}
		} else
			applyState(STATE_LOGGER_INIT);

	}

	protected void initLogger() {
		assertState(STATE_CREATED | STATE_PROPERTIES_READ, STATE_ERROR);
		SpeedLoggerInstance.getInstance().setDefaultStackTraceFormatter(ConvertedStackTraceFormatter.INSTANCE);
		String loggingOverrideProperty = getLoggingOverrideProperty();
		BasicSpeedLoggerConfigParser configParser = new BasicSpeedLoggerConfigParser(SpeedLoggerInstance.getInstance());
		if (SH.isnt(loggingOverrideProperty)) {
			configParser.process(properties.getProperties());
		} else {
			try {
				String fileName = "logging." + loggingOverrideProperty + ".properties";
				String text = IOH.readText(Bootstrap.class.getPackage(), fileName);
				PropertiesBuilder pb = new PropertiesBuilder();
				pb.readProperties(properties.getProperties());
				pb.readProperties(text);
				configParser.process(pb.resolveProperties(true).getProperties());
				EH.toStdout("F1 Logging properties overridden, read from resource: " + fileName, true);
			} catch (Exception e) {
				for (Throwable e2 = e; e2 != null; e2 = e2.getCause())
					if (e2 instanceof RollFileException) {
						throw new RuntimeException("CAN NOT MOVE/WRITE LOG FILE, MOST LIKELY ANOTHER INSTANCE OF THIS PROGRAM IS ALREADY RUNNING: " + e2.getMessage(), e);
					}
				throw new RuntimeException("invalid value for property " + F1Constants.PROPERTY_LOGGING_OVERRIDE + ": " + loggingOverrideProperty, e);
			}
		}
		Logger l = Logger.getLogger(Bootstrap.class.getName());
		if (!(l instanceof SunSpeedLogger) && !"true".equals(System.getProperty("f1.skip.logger.check"))) {
			printError("\n\n##### ERROR:add option:\n\n  -Djava.util.logging.manager=" + SunSpeedLoggerLogManager.class.getName()
					+ "\n\n -- or --\n\n -Df1.skip.logger.check=true\n\n#####");
			exitOnError(F1Constants.EXITCODE_LOGGER);
		}
		applyState(STATE_LOGGER_INIT);
	}

	private void exitOnError(int exitCode) {
		if (exitOnError)
			EH.systemExit(exitCode);
		else
			printError("strict mode disabled, would normally exit with code: " + exitCode);
	}

	public void setLogLevel(Level fileLevel, Level stdoutLevel, Class... clazz) {
		String s[] = new String[clazz.length];
		for (int i = 0; i < clazz.length; i++)
			s[i] = clazz[i].getName();
		setLogLevel(fileLevel, stdoutLevel, s);
	}

	public void setLogLevel(Level fileLevel, Level stdoutLevel, String... clazz) {
		assertState(STATE_CREATED, STATE_LOGGER_INIT | STATE_ERROR);
		String fileLabel = SpeedLoggerLevels.LEVELS_2_LABEL.get(SunSpeedLogger.toSpeedLoggerLevel(fileLevel));
		String stdoutLabel = SpeedLoggerLevels.LEVELS_2_LABEL.get(SunSpeedLogger.toSpeedLoggerLevel(stdoutLevel));
		for (String c : clazz) {
			String key = "speedlogger.stream." + c + "^";
			int i = 0;
			while (getProperty(key + i) != null || getProperty(key + (i + 1)) != null)
				i += 2;
			setProperty(key + (i), "BASIC_APPENDER;STDOUT_SINK;" + stdoutLabel);
			setProperty(key + (i + 1), "BASIC_APPENDER;FILE_SINK;" + fileLabel);
		}
	}

	public void setProperty(String key, String value) {
		assertState(STATE_CREATED, STATE_ERROR);
		if (properties != null)
			properties.getProperties().setProperty(key, value);
		propertiesBuilder.setProperty(key, value);
	}

	public void setPropertyIfValue(String key, String value) {
		if (SH.is(value))
			setProperty(key, value);
	}

	public String getProperty(String key) {
		assertState(STATE_CREATED, STATE_ERROR);
		if (properties != null)
			return properties.getOptional(key, Caster_String.INSTANCE);
		return propertiesBuilder.getProperty(key);
	}

	public void readProperties() {
		assertState(STATE_CREATED, STATE_ERROR | STATE_PROPERTIES_READ);

		String secretFiles = this.propertiesBuilder.getEnvProperty(F1Constants.PROPERTY_SECRET_KEY_FILES);
		if (SH.is(secretFiles)) {
			String[] files = SH.split(',', secretFiles);
			for (String file : files) {
				String key = SH.beforeFirst(file, "=", "");
				String val = SH.afterFirst(file, "=", file);
				File f = new File(val);
				try {
					this.propertiesBuilder.addDecrypter(key, EncrypterManager.loadEncrypter(IOH.readText(f, true)));
				} catch (Exception e) {
					throw new RuntimeException("Failed to read Aes Secret Key (see env property " + F1Constants.PROPERTY_SECRET_KEY_FILES + "): " + IOH.getFullPath(f), e);
				}
			}
		}
		String decrypters = this.propertiesBuilder.getEnvProperty(F1Constants.PROPERTY_SECRET_KEY_DECRYPTERS);
		if (SH.is(decrypters)) {
			String[] t = SH.split(',', decrypters);
			for (String decrypter : t) {
				try {
					String key = SH.beforeFirst(decrypter, "=", "");
					String val = SH.afterFirst(decrypter, "=", decrypter);
					Decrypter f = RH.newInstance(val, Decrypter.class);
					this.propertiesBuilder.addDecrypter(key, f);
				} catch (Exception e) {
					throw new RuntimeException("Failed to instantiate decrypter (see env property " + F1Constants.PROPERTY_SECRET_KEY_DECRYPTERS + "): " + decrypter, e);
				}
			}
		}
		properties = propertiesBuilder.resolveProperties(true);

		if (!SH.is(getAppNameProperty())) {
			printError("\n\n##### ERROR:add option:\n\n  -Dproperty.f1.appname=<your application name>\n\n#####");
			exitOnError(F1Constants.EXITCODE_APPNAME);
		}
		System.out.println("F1 Root Properties: " + IOH.getFullPath(new File(getConfigDirProperty() + "/" + getConfigFileNameProperty())));
		propertiesBuilder.setRootDirectorySearchPath(getConfigDirProperty());
		propertiesBuilder.readFile(getConfigFileNameProperty());
		properties = propertiesBuilder.resolveProperties(false);
		applyState(STATE_PROPERTIES_READ);
	}

	public PropertyController getProperties() {
		if (!MH.areAllBitsSet(state, STATE_PROPERTIES_READ))
			readProperties();
		assertState(STATE_PROPERTIES_READ, 0);
		return properties;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		assertState(STATE_CREATED, STATE_PROPERTIES_CONSUMED);
		this.args = args;
	}

	public Class getMainClass() {
		return mainClass;
	}

	public void setMainClass(Class mainClass) {
		assertState(STATE_CREATED, STATE_PROPERTIES_CONSUMED);
		this.mainClass = mainClass;
		if (this.mainClass != null) {
			setMainSimpleClassNameProperty(mainClass.getSimpleName());
			setMainClassNameProperty(mainClass.getName());
		}
	}

	private static boolean firstStartup = true;

	protected static boolean isFirstStartup() {
		return firstStartup;
	}

	public void startup() {
		if (!MH.areAllBitsSet(state, STATE_PROPERTIES_CONSUMED))
			processProperties();
		assertState(STATE_CREATED | STATE_LOGGER_INIT | STATE_PROPERTIES_READ, STATE_ERROR | STATE_STARTED);
		try {
			StringBuilder sb = new StringBuilder();
			EnvironmentDump.dump(mainClass, args, properties, sb);
			LH.info(EnvironmentDump.log, "EnvironmentDump: " + SH.NEWLINE, sb);
			applyState(STATE_STARTED);
			if (!MH.areAllBitsSet(state, STATE_CONSOLE_STARTED))
				startupConsole();
			ShowEnvConsoleService.init(consoleServer.getManager(), getMainClass(), getArgs(), getProperties());
		} catch (Exception e) {
			applyState(STATE_ERROR);
			throw OH.toRuntime(e);
		}
		verifyEnvironment();
		if (getTerminateFileProperty() != null) {
			new TerminateFileWatcherThread(getTerminateFileProperty());
		}
		firstStartup = false;
	}

	@Console(help = "show all environment variables")
	public String showEnv() {
		StringBuilder sb = new StringBuilder();
		EnvironmentDump.dump(mainClass, args, properties, sb);
		return sb.toString();
	}

	@Console(help = "run System.gc()")
	public String runGc() {
		System.gc();
		return "Executed System.gc()";
	}

	public void setConsoleAuthenticator(ConsoleAuthenticator authenticator) {
		assertState(0, STATE_CONSOLE_STARTED);
		this.consoleAuthenticator = authenticator;
	}

	public ConsoleAuthenticator getConsoleAuthenticator() {
		return this.consoleAuthenticator;
	}

	public void startupConsole() throws IOException {
		assertState(STATE_CREATED, STATE_ERROR | STATE_CONSOLE_STARTED);
		if (getStartConsole()) {
			ScheduledThreadPoolExecutor consoleExecutor = new ScheduledThreadPoolExecutor(16, new NamedThreadFactory("consoleServer", true));
			Integer port = getConsolePortProperty();
			String fileName = getConsoleHistoryFileProperty();
			File file = null;
			if (fileName != null) {
				file = new File(fileName);
				IOH.ensureDir(file.getParentFile());
			}
			if (consoleServer == null) {
				if (port != null && OH.ne(new Integer(-1), port)) {
					consoleServer = new ConsoleServerImpl(consoleExecutor, port, file, consoleAuthenticator);
				} else
					consoleServer = new ConsoleServerImpl();
				consoleServer.getManager().setPrompt(getConsolePrompt());
				applyState(STATE_CONSOLE_STARTED);
				registerConsoleObject("log", new SpeedLoggerConsole(SpeedLoggerInstance.getInstance()));
				registerConsoleObject(F1Constants.DEFAULT_BOOTSTRAP_NAME, this);
			} else
				applyState(STATE_CONSOLE_STARTED);
		}
	}

	public void registerConsoleObject(String name, Object value) {
		InvokersConsoleServicePackage.registerObject(getConsoleServer(), name, value);
	}
	public Object unregisterConsoleObject(String name) {
		return InvokersConsoleServicePackage.unregisterObject(getConsoleServer(), name);
	}

	public Object getRegisteredConsoleObject(String name) {
		return InvokersConsoleServicePackage.getRegisteredObject(getConsoleServer(), name);
	}

	public Set<String> getRegisteredConsoleObjects() {
		return ShowObjectsConsoleService.getGlobalInvokables(getConsoleServer().getManager()).keySet();
	}

	public void registerConsoleImport(String name) {
		ShowObjectsConsoleService.getGlobalImports(getConsoleServer().getManager()).add(name);
	}

	public ConsoleServer getConsoleServer() {
		assertState(STATE_CREATED | STATE_CONSOLE_STARTED, STATE_ERROR);
		return consoleServer;
	}

	final public int getState() {
		return state;
	}

	final public void assertState(int mustHave, int mustNotHave) {
		if (!MH.areAllBitsSet(state, mustHave))
			throw new AppException("in state " + STATES.toString(state) + ", should be in all of these states: " + STATES.toString(mustHave));
		if (MH.areAnyBitsSet(state, mustNotHave))
			throw new AppException("invalid state " + STATES.toString(state) + " should NOT be in any these states: " + STATES.toString(mustNotHave));
	}

	final protected void applyState(int state) {
		this.state |= state;
	}

	public void keepAlive() {
		keepAlive(false);
	}

	public void keepAlive(boolean blockInCallingThread) {
		this.keepAliveThread.startRunning(blockInCallingThread);
	}

	public void stopKeepAlive() {
		this.keepAliveThread.stopRunning();
	}
	public void setMainClassNameProperty(String startupClassName) {
		setProperty(F1Constants.PROPERTY_MAIN_CLASSNAME, startupClassName);
	}

	public String getMainClassNameProperty() {
		return getProperty(F1Constants.PROPERTY_MAIN_CLASSNAME);
	}

	public void setTerminateFileProperty(String terminateFile) {
		setProperty(F1Constants.PROPERTY_TERMINATE_FILE, terminateFile);
	}

	public String getTerminateFileProperty() {
		return getProperty(F1Constants.PROPERTY_TERMINATE_FILE);
	}

	public String getDeploymentId() {
		return getProperty(F1Constants.PROPERTY_DEPLOYMENT_ID);
	}

	public void setMainSimpleClassNameProperty(String startupSimpleClassName) {
		setProperty(F1Constants.PROPERTY_MAIN_SIMPLECLASSNAME, startupSimpleClassName);
	}

	public String getMainSimpleClassNameProperty() {
		return getProperty(F1Constants.PROPERTY_MAIN_SIMPLECLASSNAME);
	}

	public void setTimeZoneProperty(String timeZone) {
		setProperty(F1Constants.PROPERTY_TIMEZONE, timeZone);
	}

	public String getTimeZoneProperty() {
		return getProperty(F1Constants.PROPERTY_TIMEZONE);
	}

	public void setLocaleProperty(String locale) {
		setProperty(F1Constants.PROPERTY_LOCALE, locale);
	}

	public String getLocaleProperty() {
		return getProperty(F1Constants.PROPERTY_LOCALE);
	}

	public void setAppNameProperty(String appName) {
		setProperty(F1Constants.PROPERTY_APPNAME, appName);
	}

	public String getAppNameProperty() {
		return getProperty(F1Constants.PROPERTY_APPNAME);
	}

	public void setLoggingOverrideProperty(String loggingOverride) {
		setProperty(F1Constants.PROPERTY_LOGGING_OVERRIDE, loggingOverride);
	}

	public String getLoggingOverrideProperty() {
		return getProperty(F1Constants.PROPERTY_LOGGING_OVERRIDE);
	}

	public void setConfigDirProperty(String configDir) {
		assertState(STATE_CREATED, STATE_PROPERTIES_READ);
		setProperty(F1Constants.PROPERTY_CONF_DIR, configDir);
	}

	public String getConfigDirProperty() {
		return getProperty(F1Constants.PROPERTY_CONF_DIR);
	}

	public void setScriptsDirProperty(String scriptsDir) {
		assertState(STATE_CREATED, STATE_PROPERTIES_READ);
		setProperty(F1Constants.PROPERTY_SCRIPTS_DIR, scriptsDir);
	}

	public String getScriptsDirProperty() {
		return getProperty(F1Constants.PROPERTY_SCRIPTS_DIR);
	}

	public void setLogsDirProperty(String logsDir) {
		assertState(STATE_CREATED, STATE_PROPERTIES_READ);
		setProperty(F1Constants.PROPERTY_LOGS_DIR, logsDir);
	}

	public String getLogsDirProperty() {
		return getProperty(F1Constants.PROPERTY_LOGS_DIR);
	}

	public void setConfigFileNameProperty(String configFile) {
		assertState(STATE_CREATED, STATE_PROPERTIES_READ);
		setProperty(F1Constants.PROPERTY_CONF_FILENAME, configFile);
	}

	public String getConfigFileNameProperty() {
		return getProperty(F1Constants.PROPERTY_CONF_FILENAME);
	}

	public void setRuntimeDirProperty(String startupDir) {
		assertState(STATE_CREATED, STATE_PROPERTIES_READ);
		setProperty(F1Constants.PROPERTY_RUNTIME_DIR, startupDir);
	}
	public String getRuntimeDirProperty() {
		return getProperty(F1Constants.PROPERTY_RUNTIME_DIR);
	}

	public void setUserNameProperty(String userName) {
		setProperty(F1Constants.PROPERTY_USERNAME, userName);
	}

	public String getUserNameProperty() {
		return getProperty(F1Constants.PROPERTY_USERNAME);
	}

	public void setUserHomeProperty(String userName) {
		setProperty(F1Constants.PROPERTY_USERHOME, userName);
	}

	public String getUserHomeProperty() {
		return getProperty(F1Constants.PROPERTY_USERHOME);
	}

	public void setLocalHostProperty(String localHost) {
		setProperty(F1Constants.PROPERTY_LOCALHOST, localHost);
	}

	public String getLocalHostProperty() {
		return getProperty(F1Constants.PROPERTY_LOCALHOST);
	}

	public Integer getConsolePortProperty() {
		return Caster_Integer.INSTANCE.cast(getProperty(F1Constants.PROPERTY_CONSOLE_PORT), false);
	}

	public void setConsolePortProperty(Integer port) {
		setProperty(F1Constants.PROPERTY_CONSOLE_PORT, Caster_String.INSTANCE.cast(port, false));
	}

	public String getConsoleHistoryFileProperty() {
		return getProperty(F1Constants.PROPERTY_CONSOLE_HISTORY_FILE);
	}

	public String getConsolePrompt() {
		return getProperty(F1Constants.PROPERTY_CONSOLE_PROMPT);
	}

	public void setConsoleHistoryFileProperty(String fileName) {
		setProperty(F1Constants.PROPERTY_CONSOLE_HISTORY_FILE, fileName);
	}

	public void setConsolePrompt(String prompt) {
		setProperty(F1Constants.PROPERTY_CONSOLE_PROMPT, prompt);
	}

	static public class TerminateFileWatcherThread extends Thread {

		private boolean isRunning;
		private File terminateFile;
		private String terminateFileGuid;
		private long lastModified;

		public TerminateFileWatcherThread(String terminateFile) {
			super("TerminateFileThread");
			File t = new File(terminateFile);
			setDaemon(true);
			this.terminateFile = new File(terminateFile + ".kill");
			System.out.println("F1 Terminate process: mv " + IOH.getFullPath(t) + " " + IOH.getFullPath(this.terminateFile));
			if (this.terminateFile != null) {
				this.terminateFileGuid = '!' + GuidHelper.getGuid();
				try {
					IOH.appendText(t, terminateFileGuid);
				} catch (Exception e) {
					throw new RuntimeException("Could not create terminate file: " + terminateFile, e);
				}
			}
			start();
			while (!isRunning)
				OH.sleep(TimeUnit.MILLISECONDS, 10);
		}

		@Override
		public void run() {
			isRunning = true;
			for (;;) {
				OH.sleep(TimeUnit.SECONDS, 1);
				if (this.terminateFile.isFile()) {
					if (this.terminateFile.lastModified() != this.lastModified) {
						this.lastModified = this.terminateFile.lastModified();
						try {
							if (IOH.readText(terminateFile).trim().contains(terminateFileGuid)) {
								EH.toStdout("Terminate file found, exiting: " + IOH.getFullPath(terminateFile), true);
								IOH.delete(terminateFile);
								EH.systemExit(F1Constants.EXITCODE_TERMINATE_FILE_FOUND);
							}
						} catch (IOException e) {
						}
					}
				} else
					this.lastModified = 0;
			}
		}

	}

	static public class KeepAliveThread extends Thread {

		private boolean running;
		private boolean isRunning;

		public KeepAliveThread() {
			super("KeepAliveThread");
		}

		synchronized public void startRunning(boolean block) {
			if (isRunning)
				throw new IllegalStateException();
			running = true;
			if (block)
				go();
			else {
				start();
				while (!isRunning)
					OH.sleep(TimeUnit.MILLISECONDS, 10);
			}
		}

		@Override
		public void run() {
			go();
		}

		private void go() {
			isRunning = true;
			while (running)
				OH.sleep(TimeUnit.SECONDS, 1);
			isRunning = false;
		}

		synchronized public void stopRunning() {
			if (!isRunning)
				throw new IllegalStateException();
			running = false;
			interrupt();
			while (isRunning)
				OH.sleep(TimeUnit.MILLISECONDS, 10);
		}
	}

	public void readSystemProperty(String systemPropertyName) {
		setPropertyIfValue(systemPropertyName, System.getProperty(systemPropertyName));
	}

	public void suspend() {
		suspend(-1, null);

	}

	public void suspend(long timeoutMs, TimeUnit unit) {
		if (!getIsSuspendProperty()) {
			printInfo("**** NOT SUSPENDING. To honor suspension please set property " + F1Constants.PROPERTY_SUSPEND + "=true");
			return;
		}
		synchronized (isSuspended) {
			if (isSuspended.get())
				throw new IllegalStateException("already suspended");
			isSuspended.put(true);
			printInfo("**** SUSPENDED -- Please call bs.unsuspend() to continue **** ");
			if (!MH.areAllBitsSet(state, STATE_CONSOLE_STARTED))
				printInfo("**** Please note, console has not been started yet, you may wish to call startConsole() prior to suspend() ****");
			if (timeoutMs < 0L)
				OH.wait(isSuspended);
			else
				OH.wait(isSuspended, TimeUnit.MILLISECONDS.convert(timeoutMs, unit));
			isSuspended.put(false);
		}
	}

	@Console(help = "unsuspend an application started in suspend mode by setting property " + F1Constants.PROPERTY_SUSPEND + "=true")
	public String unsuspend() {
		if (!getIsSuspendProperty())
			return "ingoring suspend because property " + F1Constants.PROPERTY_SUSPEND + " is not set to true";
		synchronized (isSuspended) {
			if (!isSuspended.get())
				return "nothing done, not in a suspended state";
			isSuspended.notify();
			isSuspended.put(false);
			printInfo("**** UNSUSPEND called ****");
			return "ok";
		}
	}

	public boolean getIsSuspendProperty() {
		return Boolean.parseBoolean(getProperty(F1Constants.PROPERTY_SUSPEND));
	}

	public void setIsSuspendProperty(boolean value) {
		setProperty(F1Constants.PROPERTY_SUSPEND, Boolean.toString(value));
	}

	public void setStartConsole(boolean startConsole) {
		assertState(STATE_CREATED, STATE_ERROR | STATE_STARTED);
		this.startConsole = startConsole;
	}

	public boolean getStartConsole() {
		return startConsole;
	}

	public void verifyEnvironment() {
		int i = 0;
		for (String classPath : EH.getJavaClassPath()) {
			File f = new File(classPath);
			if (!f.exists())
				printError("CLASSPATH ENTRY AT POSITION " + (i + 1) + " HAS INVALID LOCATION: " + f);
			i++;
		}
		if (SpeedLoggerInstance.getInstance().getSinkIds().size() == 0)

			printError("\n\n##### SpeedLogger has no Sinks. Nothing will be logged!\n\n if you are running in dev mode, add property: -D"
					+ F1GlobalProperties.getSysPropertyPrefix() + F1Constants.PROPERTY_LOGGING_OVERRIDE + "=" + F1Constants.LOGGING_OVERRIDE_VERBOSE + "\n\n#####");
	}

	@Console(help = "show all properties and their sources'")
	public String showProperties() {
		return showProperties(null);
	}

	@Console(help = "show property values and their sources' for all properties whose keys match the supplied expression", params = { "keyExpression" })
	public String showProperties(String keyExpression) {
		TextMatcher matcher = TextMatcherFactory.DEFAULT.toMatcher(keyExpression);
		PropertyController pc = getProperties();
		BasicLabeler bl = new BasicLabeler();
		for (String key : CH.sort(pc.getKeys())) {
			if (!matcher.matches(key))
				continue;
			bl.addItem(key, pc.getOptional(key));
			List<Property> props = pc.getPropertySources(key);
			for (int i = 0; i < props.size(); i++) {
				Property source = props.get(i);
				if (i == props.size() - 1)
					bl.addItem(key, "effective => " + source);
				else
					bl.addItem(key, "overidden => " + source);
			}
		}
		return bl.toString("", "=", 0);
	}
	@Console(help = "lists all sources (typically file names) that were used to determine properties")
	public String showPropertySources() {
		Collection<String> sources = properties.getAllSources();
		if (CH.isEmpty(sources))
			return "<sources not available>";
		return SH.join(SH.NEWLINE, sources);
	}

	public void printError(String text) {
		EH.toStderr("ERROR: " + text, true);
		Logger log = Logger.getLogger(getClass().getName());
		LH.severe(log, text);
	}

	public void printInfo(String text) {
		EH.toStdout("INFO: " + text, true);
		Logger log = Logger.getLogger(getClass().getName());
		LH.info(log, text);
	}

	public void setShouldExitOnError(boolean exitOnError) {
		this.exitOnError = exitOnError;
	}

	public boolean getShouldExitOnError() {
		return exitOnError;
	}

	private Locale locale = Locale.getDefault();
	private TimeZone timeZone = TimeZone.getDefault();

	public Locale getLocale() {
		return locale;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @param locale
	 *            format should either be COUNTRY or
	 */
	private void setLocale(String locale) {
		locale = locale.toLowerCase();
		if ("default".equals(locale) || SH.isnt(locale)) {
			setLocale(Locale.getDefault());
			return;
		}
		locale = locale.trim();
		if (locale.indexOf('_') == -1) {
			final String language = locale;
			for (Locale l : Locale.getAvailableLocales()) {
				if (locale.equalsIgnoreCase(l.getDisplayName())) {
					setLocale(l);
					return;
				}
				if (SH.isnt(l.getCountry())
						&& (language.equalsIgnoreCase(l.getISO3Language()) || language.equalsIgnoreCase(l.getLanguage()) || language.equalsIgnoreCase(l.getDisplayLanguage()))) {
					setLocale(l);
					return;
				}
			}
		} else {
			final String language = SH.toLowerCase(SH.beforeFirst(locale, "_"));
			final String country = SH.toLowerCase(SH.afterFirst(locale, "_"));
			for (Locale l : Locale.getAvailableLocales()) {
				if ((country.equalsIgnoreCase(l.getISO3Country()) || country.equalsIgnoreCase(l.getCountry()) || country.equalsIgnoreCase(l.getDisplayCountry()))
						&& (language.equalsIgnoreCase(l.getISO3Language()) || language.equalsIgnoreCase(l.getLanguage()) || language.equalsIgnoreCase(l.getDisplayLanguage()))) {
					setLocale(l);
					return;
				}
			}
		}
		throw new NoSuchElementException("locale: " + locale);

	}
	private void setTimeZone(String id) {
		if ("default".equals(id) || SH.isnt(id))
			setTimeZone(TimeZone.getDefault());
		TimeZone tz = TimeZone.getTimeZone(id);
		if (!id.equals(tz.getID()))
			throw new NoSuchElementException("timezone id: " + id + ", available ids: " + SH.join(",", TimeZone.getAvailableIDs()));
		setTimeZone(tz);
	}

	public void startupComplete() {
		applyState(STATE_STARTUP_COMPLETE);

		TreeMap<String, Object> streams = new TreeMap<String, Object>();
		SpeedLoggerManager slm = SpeedLoggerInstance.getInstance();
		List<String> t2 = new ArrayList<String>();
		for (String i : slm.getStreamIds()) {
			for (SpeedLoggerStream s : slm.getStream(i))
				t2.add(s.describe());
			streams.put(i, SH.join(",", t2));
			t2.clear();
		}

		TreeMap<String, Object> sinks = new TreeMap<String, Object>();
		for (String i : slm.getSinkIds())
			sinks.put(i, slm.getSink(i).describe());

		TreeMap<String, Object> appenders = new TreeMap<String, Object>();
		for (String i : slm.getAppenderIds())
			appenders.put(i, slm.getAppender(i).describe());

		Map<String, Map<String, Object>> m = new LinkedHashMap<String, Map<String, Object>>();

		m.put("SpeedLogger Sinks", sinks);
		m.put("SpeedLogger Appenders", appenders);
		m.put("SpeedLogger Streams", streams);
		m.put("Default Properties", this.getProperties().getDefaultDeclaredProperties());
		EnvironmentDump.log.info("Additionally declared properties: " + SH.NEWLINE + EnvironmentDump.dumpStartupComplete(m).toString());
	}

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		InstrumentationReference.setInstrumentation(instrumentation);
	}

}
