package com.f1.bootstrap;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.f1.base.Clock;
import com.f1.base.Ideable;
import com.f1.base.IdeableGenerator;
import com.f1.base.Message;
import com.f1.codegen.CodeGenerator;
import com.f1.console.impl.ConsoleManagerImpl;
import com.f1.container.ContainerConstants;
import com.f1.container.wrapper.InspectingDispatchController;
import com.f1.utils.BundledTextFormatter;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OfflineConverter;

//import com.f1.tester.AppTester;

/**
 * Conatains well know property names used by the {@link Bootstrap} and {@link ContainerBootstrap}. In addition, several default values are defined here as well. Note: this
 * interface is not intended to ever be implemented
 */
public interface F1Constants {

	String PROPERTY_PLUGINS_DIR = "f1.plugins.dir";

	/**
	 * deployment identifier
	 */
	String PROPERTY_DEPLOYMENT_ID = "f1.deployment.id";
	String PROPERTY_DEPLOYMENT_SETID = "f1.deployment.setid";
	String PROPERTY_DEPLOYMENT_INVOKED_BY = "f1.deployment.invokedby";
	String PROPERTY_DEPLOYMENT_INSTANCE_ID = "f1.deployment.instanceid";
	String PROPERTY_DEPLOYMENT_TIMESTAMP = "f1.deployment.timestamp";

	/**
	 * if supplied, then a file at specified location will be created. If an external process moves / copies to a file of the same name, but with a ".kill" suffix the process will
	 * exit
	 */
	String PROPERTY_TERMINATE_FILE = "f1.terminate.file";

	/**
	 * deployment identifier
	 */
	String PROPERTY_DEPLOYMENT_DESCRIPTION = "f1.deployment.description";

	/**
	 * version of this deployment
	 */
	String PROPERTY_DEPLOYMENT_VERSION = "f1.deployment.version";

	/**
	 * name of this deployment
	 */
	String PROPERTY_DEPLOYMENT_NAME = "f1.deployment.name";

	/**
	 * the name of the class used to start the application
	 */
	String PROPERTY_MAIN_CLASSNAME = "f1.main.classname";

	/**
	 * the 'simple' name of the class used to start the application
	 */
	String PROPERTY_MAIN_SIMPLECLASSNAME = "f1.main.simpleclassname";

	/**
	 * the host that the test track agent is running on. Typically the agent should be on localhost to minimize bandwidth to external machines.
	 */
	String PROPERTY_AGENT_HOST = "f1.agent.host";

	/**
	 * the server port that the test track agent is running on.
	 */
	String PROPERTY_AGENT_PORT = "f1.agent.port";

	/**
	 * The minimum period (in milliseconds) between updates.
	 */
	String PROPERTY_AGENT_UPDATE_PERIOD = "f1.agent.update.periodms";

	/**
	 * comma delimited list of the following options then the following options will be applied to the default {@link LocaleFormatter}'s {@link BundledTextFormatter}. See the
	 * BundleTextFormatter javadoc for option details details
	 * 
	 * <pre>
	 * KEY_MISSING_RETURN_NULL 
	 * KEY_MISSING_PRINT_DETAILED
	 * KEY_MISSING_DEBUG
	 * ON_ARG_MISSING_PRINT_KEY
	 * ON_ARG_MISSING_PRINT_NULL
	 * ON_ARG_ERROR_EMBED_MESSAGE
	 * LOG_EXCEPTIONS
	 * LOG_STACKTRACE
	 * </pre>
	 */
	String PROPERTY_TEXT_BUNDLE_OPTIONS = "f1.textbundle.options";

	/**
	 * If supplied, then the clock's time will be shifted forward if positive or backwards if negative. Format: 'nnn unit'. For example, '-7 MINUTES' will mean that at 4:30 PM the
	 * clock will read 04:23 PM. See {@link TimeUnit} for valid units
	 */
	String PROPERTY_TIME_SHIFT = "f1.time.shift";

	/**
	 * If supplied, then the clock's time will be synchronized with the supplied time at startup. Format: 'YYYYMMDD-hh:mm:ss'. For example, '20001213- 14:30:00' will result in the
	 * clock producing a time of approximately 2:30 pm on Dec 13, 2000. Please note that the 'start up' time is not exact so this is an approximation.
	 */
	String PROPERTY_TIME_START = "f1.time.start";

	/**
	 * If supplied, the time will be dilated accordingly. Format: 'nnn unit:mmm unit'. For example '2 HOURS:1 MINUTES' means that 2 hours will pass every one minute. Another
	 * example: '-2 MINUTES:1 HOURS' means that time will go backwards by 2 minutes every 1 hour. See {@link TimeUnit} for valid units
	 */
	String PROPERTY_TIME_DILATE = "f1.time.dilate";

	/**
	 * property name associated with the list of files for use in the {@link LocaleFormatter}'s. See {@link #PROPERTY_TEXT_BUNDLE_DIR} {@link BundledTextFormatter}
	 */
	String PROPERTY_TEXT_BUNDLE_FILES = "f1.textbundle.files";

	/**
	 * identifies where the f1 license file is located (generated at <A href="http://3forge.com">3forge.com</A>)
	 */
	String PROPERTY_LICENSE_FILE = "f1.license.file";

	/**
	 * Optional log level that the dispatcher will use to log a brief message during event processing. Must be a valid log level as specified by java's {@link Level#parse(String)}
	 */
	String PROPERTY_F1_LOG_PERFORMANCE_LEVEL = "f1.log.performance.level";

	/**
	 * property name assoicated with the location of files for use in the {@link LocaleFormatter}'s {@link BundledTextFormatter}
	 */
	String PROPERTY_TEXT_BUNDLE_DIR = "f1.textbundle.dir";

	/**
	 * property name associated with base directory for runtime. Typically, all files used would be relative to this directory
	 */
	String PROPERTY_RUNTIME_DIR = "f1.runtime.dir";

	/**
	 * property name associated with where autocoded source and class files will be depositied
	 */
	String PROPERTY_AUTOCODED_DIR = "f1.autocoded.dir";

	/**
	 * property name associated with the number of threads in the default thread pools
	 */
	String PROPERTY_THREADPOOL_DEFAULT_SIZE = "f1.threadpool.default.size";

	/**
	 * if true, then the thread pool will be the F1 FastThreadPool, otherwise the default java ThreadPool
	 */
	String PROPERTY_THREADPOOL_AGGRESSIVE = "f1.threadpool.aggressive";

	/**
	 * Threads sending in events will not directly unpark (wake up) threads from the thread pool lowering cost of calling execute(...). Instead, a dedicated dispatch thread handles
	 * the wake ups which results in higher latency. <BR>
	 * default is true.<BR>
	 * Note: Only applicable when f1.threadpool.aggresive=true
	 */
	String PROPERTY_THREADPOOL_FAST_EXECUTE = "f1.threadpool.fast.execute";

	/**
	 * property name indicating if the autocoded directory should be cleaned on startup (all files and the directory deleted). see {@link #PROPERTY_AUTOCODED_DIR}
	 */
	String PROPERTY_AUTOCODED_DIR_CLEAN = "f1.autocoded.dir.clean";
	String PROPERTY_AUTOCODED_DISABLED = "f1.autocoded.disabled";

	/**
	 * property name associated with the directory where the configuration file is located
	 */
	String PROPERTY_CONF_DIR = "f1.conf.dir";

	/**
	 * property name associated with the directory where the configuration file is located
	 */
	String PROPERTY_SCRIPTS_DIR = "f1.scripts.dir";

	/** property name associated with the file name of the configuration file */
	String PROPERTY_CONF_FILENAME = "f1.conf.filename";

	/** property name associated with the service's time zone */
	String PROPERTY_TIMEZONE = "f1.timezone";

	/** property name associated with the service's locale. Should be in the format 'Language-Country' or just 'Language' */
	String PROPERTY_LOCALE = "f1.locale";

	/**
	 * property name associated with the name of the application(as would be represented in monitoring tools)
	 */
	String PROPERTY_APPNAME = "f1.appname";

	/**
	 * property name associated with the enabbling of idemode. true=ide mode on, false=ide mode off.
	 */
	String PROPERTY_LOGGING_OVERRIDE = "f1.logging.mode";

	/**
	 * property name associated with the filename for log files
	 */
	String PROPERTY_LOGFILENAME = "f1.logfilename";

	/**
	 * property name associated with the enabbling of inspecting mode. true=enabling mode on, false=enabling mode off. see also {@link AppTester} and
	 * {@link InspectingDispatchController}
	 */
	String PROPERTY_INSPECTING_MODE = "f1.inspecting.mode";

	String PROPERTY_F1_MONITORING_ENABLED = "f1.monitoring.enabled";

	/** property name associated with the user id owning the JVM process */
	String PROPERTY_USERNAME = "f1.username";

	/** property name associated with the user home owning the JVM process */
	String PROPERTY_USERHOME = "f1.userhome";

	/** property name associated with the local hostname */
	String PROPERTY_LOCALHOST = "f1.localhost";

	/**
	 * property name associated with a list of comma-delimited packages and classes to search for {@link Message}s in. Any classes within said packages that implements the
	 * {@link Message} interface will automatically be registered. See {@link IdeableGenerator} and {@link Ideable} for details regarding object registration.
	 */
	String PROPERTY_MESSAGE_PACKAGES = "f1.messages.packages";

	/**
	 * property name associated with the fully qualified class name of the {@link OfflineConverter}
	 */
	String PROPERTY_CONVERTER = "f1.converter.class";

	/**
	 * property name associated with the root directory for where log files will be deposited
	 */
	String PROPERTY_LOGS_DIR = "f1.logs.dir";

	/**
	 * property name associated with the port where the the console telnet server will run
	 */
	String PROPERTY_CONSOLE_PORT = "f1.console.port";

	/** property name associated with the prompt displayed on the console */
	String PROPERTY_CONSOLE_PROMPT = "f1.console.prompt";

	/**
	 * property name associated with the location of where the console history file is placed see {@link ConsoleManagerImpl}
	 */
	String PROPERTY_CONSOLE_HISTORY_FILE = "f1.console.history.file";

	/**
	 * property name associated with the os delimter for seperating paths (unix=: , windows=;)
	 */
	String PROPERTY_PATH_SEPERATOR = "f1.path.seperator";

	/**
	 * property name associated with the os delimter for seperating directories (unix=/ , windows=\)
	 */
	String PROPERTY_DIR_SEPERATOR = "f1.dir.seperator";

	/**
	 * property name associated with the path of the working directory for the JVM
	 */
	String PROPERTY_PWD = "f1.pwd";

	/**
	 * property name to indicate if calling {@link ContainerBootstrap#suspend(long, edu.emory.mathcs.backport.java.util.concurrent.TimeUnit)} {@link ContainerBootstrap#unsuspend()}
	 * is called). true = suspend, false = dont suspend ( default)
	 */
	String PROPERTY_SUSPEND = "f1.suspend";

	/** timezone to use in the {@link Clock},supply 'default' to use TimeZone.getDefault() */
	String DEFAULT_TIMEZONE = "UTC";

	/**
	 * locale to use in the {@link Clock}, supply 'default' to use Locale.getDefault() otherwise should be in format: 'language_iso_code-country_iso_code', for example 'EN-US'
	 */
	String DEFAULT_LOCALE = "default";

	/**
	 * set logging to a verbose mode and ignore the properties file. see {@link Bootstrap#getLoggingMode()} for details on logging modes
	 */
	String LOGGING_OVERRIDE_VERBOSE = "verbose";

	/**
	 * default name for the properties file (see also {@link #DEFAULT_CONFIG_DIR})
	 */
	String DEFAULT_CONFIG_FILE = "root.properties";

	/** the default runtime directory (see {@link #PROPERTY_RUNTIME_DIR}) */
	String DEFAULT_RUNTIME_DIR = "${f1.pwd}";

	/** default location of the properties file */
	String DEFAULT_CONFIG_DIR = "${f1.runtime.dir}/config";

	/** the default log directory (see {@link #PROPERTY_RUNTIME_DIR}) */
	String DEFAULT_LOGS_DIR = "${f1.runtime.dir}/log";

	/** the default log directory (see {@link #PROPERTY_RUNTIME_DIR}) */
	String DEFAULT_SCRIPTS_DIR = "${f1.runtime.dir}/scripts";

	/**
	 * the location of where the {@link CodeGenerator} should deposite java and class files (see {@link #PROPERTY_AUTOCODED_DIR})
	 */
	String DEFAULT_AUTOCODED_DIR = "${f1.runtime.dir}/.autocoded";

	/**
	 * indicates wheather the autocoder directory should be cleaner on startup by default. see {@link #PROPERTY_AUTOCODED_DIR})
	 */
	boolean DEFAULT_AUTOCODED_DIR_CLEAN = false;
	boolean DEFAULT_AUTOCODED_DISABLED = false;

	/**
	 * the default value for wheather testing should should be enabled by default
	 */
	boolean DEFAULT_INSPECTING_MODE = true;
	boolean DEFAULT_F1_MONITORING_ENABLED = false;

	/**
	 * default location of where the history file should be located (see {@link #PROPERTY_CONSOLE_HISTORY_FILE})
	 */
	String DEFAULT_CONSOLE_HISTORY_FILE = "${f1.runtime.dir}/.console_history.txt";
	String DEFAULT_TEXT_BUNDLE_FILES = "bundle.txt";
	String DEFAULT_TEXT_BUNDLE_DIR = "${f1.runtime.dir}";
	String DEFAULT_CONSOLE_PROMPT = "${f1.appname}>";

	boolean DEFAULT_SUSPEND = false;

	String DEFAULT_TESTER_NAME = "tester";
	String DEFAULT_CONTAINER_NAME = "container";
	String DEFAULT_BOOTSTRAP_NAME = "bs";

	/** exit code if there was a faital error with the logger */
	int EXITCODE_LOGGER = 21;

	/** exit code if the appname was not configured */
	int EXITCODE_APPNAME = 22;

	/** exit code indicating the application should be restarted */
	int EXITCODE_RESTART = 25;

	/** exit code indicating their was an issue with the f1 license file */
	int EXITCODE_LICENSE = 23;

	/** exit code indicating their was an issue with writing to the process info */
	int EXITCODE_PROCINFOSINKFILE = 27;

	/**
	 * exit code indicating when the main thread throws an exception which is not caught
	 */
	int EXITCODE_MAINTHREAD_UNHANDLED_EXCEPTION = 2;

	/** exit code indicating their a terminate file was created (see terminate file property) */
	int EXITCODE_TERMINATE_FILE_FOUND = 28;

	/** default number of threads in the pool */
	int DEFAULT_THREADPOOL_SIZE = ContainerConstants.DEFAULT_THREAD_POOL_SIZE;

	String DEFAULT_PLUGINS_DIR = "${f1.runtime.dir}/plugins";
	boolean DEFAULT_THREADPOOL_AGGRESIVE = true;
	boolean DEFAULT_THREADPOOL_FAST_EXECUTE = true;

	String DEFAULT_AGENT_HOST = "localhost";
	int DEFAULT_AGENT_PORT = 3406;
	int DEFAULT_AGENT_UPDATE_PERIOD = 1000;

	String DEFAULT_TEXT_BUNDLE_OPTIONS = "KEY_MISSING_DEBUG,LOG_STACKTRACE";

	String TBO_KEY_MISSING_RETURN_NULL = "KEY_MISSING_RETURN_NULL";
	String TBO_KEY_MISSING_PRINT_DETAILED = "KEY_MISSING_PRINT_DETAILED";
	String TBO_KEY_MISSING_DEBUG = "KEY_MISSING_DEBUG";
	String TBO_ON_ARG_MISSING_PRINT_KEY = "ON_ARG_MISSING_PRINT_KEY";
	String TBO_ON_ARG_MISSING_PRINT_NULL = "ON_ARG_MISSING_PRINT_NULL";
	String TBO_ON_ARG_ERROR_EMBED_MESSAGE = "ON_ARG_ERROR_EMBED_MESSAGE";
	String TBO_LOG_EXCEPTIONS = "LOG_EXCEPTIONS";
	String TBO_LOG_STACKTRACE = "LOG_STACKTRACE";
	String TBO_KEYS_START_WITH_BANGBANG = "KEYS_START_WITH_BANGBANG";

	String PROPERTY_BUILD_VERSION = "build.version";

	String PROPERTY_SECRET_KEY_FILES = "f1.properties.secret.key.files";
	String PROPERTY_SECRET_KEY_DECRYPTERS = "f1.properties.decrypters";
}
