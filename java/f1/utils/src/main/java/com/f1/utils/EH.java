/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.SimpleExecutor;
import com.f1.utils.concurrent.UnsafeHelper;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.structs.MapInMap;

/**
 * Environment Helper
 * 
 * @author rcooke
 * 
 */
public class EH {

	private EH() {
	}

	private static final Runtime RT = Runtime.getRuntime();

	private static final Logger log = Logger.getLogger(EH.class.getName());
	public static final String PROPERTY_JAVA_HOME = "java.home";
	public static final String PROPERTY_JAVA_VERSION = "java.version";
	public static final String PROPERTY_JAVA_VENDOR = "java.vendor";
	public static final String PROPERTY_JAVA_EXT_DIRS = "java.ext.dirs";
	public static final String PROPERTY_JAVA_CLASS_PATH = "java.class.path";
	public static final String PROPERTY_JAVA_COMPILER = "java.compiler";
	public static final String PROPERTY_OS_NAME = "os.name";
	public static final String PROPERTY_OS_ARCH = "os.arch";
	public static final String PROPERTY_OS_VERSION = "os.version";
	public static final String PROPERTY_USER_NAME = "user.name";
	public static final String PROPERTY_USER_HOME = "user.home";
	public static final String THREAD_MAIN = "main";

	private static String hostName;
	private static InetAddress localhost;
	private static List<InetAddress> allAddresses;
	private static PrintStream stdout;
	private static PrintStream stderr;
	private static String hostIp;
	private static boolean isWindows;
	private static final String[] ENV;
	private static final TimeZone GMT;
	final private static MapInMap<String, String, TimeZone> timezonesByRegion;
	final private static Map<String, TimeZone> timezonesByI;
	final private static Map<String, Locale> localesByLanguage;
	final private static Map<String, Locale> localesByLanguageISO3;
	final private static Map<String, Locale> localesByLanguageDisplay;
	public static final int ESTIMATED_GC_OVERHEAD = 8;//overhead memory cost in bytes for each object
	public static final int ADDRESS_SIZE;

	static {
		{
			int t;
			try {
				t = UnsafeHelper.unsafe.addressSize();
			} catch (Throwable e) {
				System.err.println("Could not determing address size, assuming 8 bytes");
				t = 8;
			}
			ADDRESS_SIZE = t;
		}
		if (getOsName().startsWith("Windows"))
			setWindows(true);
		{
			Map<String, String> envMap = System.getenv();
			ENV = new String[envMap.size()];
			int i = 0;
			for (Map.Entry<String, String> e : envMap.entrySet())
				ENV[i++] = e.getKey() + "=" + e.getValue();
		}
		stdout = System.out;
		stderr = System.err;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (Exception e) {
			localhost = null;
		}
		try {
			if (localhost != null) {
				hostName = localhost.getHostName();
				hostIp = localhost.getHostAddress();
			} else {
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				if (interfaces != null) {
					for (NetworkInterface ni : Iterator2Iterable.toIterable(interfaces)) {
						for (InetAddress a : Iterator2Iterable.toIterable(ni.getInetAddresses())) {
							if (a.isSiteLocalAddress()) {
								hostIp = hostName = a.getHostName();
								if (!isIpAddress(hostName))
									break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			hostName = "127.0.0.1";
		}
		if (isIpAddress(hostName) || getJavaVersion().startsWith("1.8")) {
			try {
				ProcessResult result = exec(SimpleExecutor.DEFAULT_DEAMON, "hostname");
				if (result.getA().exitValue() == 0) {
					hostName = SH.trim(new String(result.getB()));
				}
			} finally {
			}
		}
		try {
			if (localhost != null)
				allAddresses = CH.l(InetAddress.getAllByName(localhost.getCanonicalHostName()));
		} catch (Exception e) {
			allAddresses = Collections.EMPTY_LIST;
		}
		timezonesByRegion = new MapInMap<String, String, TimeZone>();
		localesByLanguage = new HasherMap<String, Locale>(CaseInsensitiveHasher.INSTANCE);
		localesByLanguageISO3 = new HasherMap<String, Locale>(CaseInsensitiveHasher.INSTANCE);
		localesByLanguageDisplay = new HasherMap<String, Locale>(CaseInsensitiveHasher.INSTANCE);
		GMT = TimeZone.getTimeZone("GMT");
		timezonesByI = new HashMap<String, TimeZone>();
		for (Locale t : Locale.getAvailableLocales()) {
			if (SH.isnt(t.getCountry())) {
				localesByLanguageISO3.put(t.getISO3Language(), t);
				localesByLanguage.put(t.getISO3Language(), t);
				localesByLanguageDisplay.put(t.getDisplayLanguage(), t);
				localesByLanguage.put(t.getDisplayLanguage(), t);
			}
		}
		for (String id : TimeZone.getAvailableIDs()) {
			TimeZone tz = TimeZone.getTimeZone(id);
			int i = id.indexOf('/');
			if (i == -1)
				timezonesByRegion.putMulti("", id, tz);
			else
				timezonesByRegion.putMulti(id.substring(0, i), id.substring(i + 1), tz);
			timezonesByI.put(id, tz);
		}
		timezonesByRegion.lock();
	}
	private static String processUid;

	public static void redirectStdout(PrintStream stdout) {
		EH.stdout = stdout;
	}
	private static boolean isIpAddress(String hostName) {
		String[] parts = SH.split('.', hostName);
		if (parts.length != 4)
			return false;
		for (String i : parts) {
			if (!SH.areBetween(i, '0', '9'))
				return false;
			if (!OH.isBetween(SH.parseInt(i), 0, 255))
				return false;
		}
		return true;
	}
	public static void redirectStderr(PrintStream stderr) {
		EH.stderr = stderr;
	}

	public static String getPwd() {
		try {
			return new File(".").getCanonicalPath().replaceAll("\\\\", "/");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getLocalHost() {
		return hostName;
	}

	public static String getLocalHostIp() {
		return hostIp;
	}

	public static InetAddress getLocalHostAddress() {
		return localhost;
	}

	public static Iterable<InetAddress> getAllLocalAddresses() {
		return CH.l(allAddresses);
	}

	public static String getTimeZone() {
		TimeZone tz = TimeZone.getDefault();
		return tz.getID();
	}

	public static Date now() {
		return new Date(currentTimeMillis());
	}

	public static String getJavaHome() {
		return System.getProperty(PROPERTY_JAVA_HOME);
	}

	public static String getJavaVersion() {
		return System.getProperty(PROPERTY_JAVA_VERSION);
	}

	public static String getJavaVendor() {
		return System.getProperty(PROPERTY_JAVA_VENDOR);
	}

	public static String[] getJavaExtDirs() {
		String dirs = System.getProperty(PROPERTY_JAVA_EXT_DIRS);
		if (dirs == null)
			return OH.EMPTY_STRING_ARRAY;
		return SH.split(File.pathSeparator, dirs);
	}

	public static String[] getJavaClassPath() {
		String dirs = System.getProperty(PROPERTY_JAVA_CLASS_PATH);
		if (dirs == null)
			return OH.EMPTY_STRING_ARRAY;
		return SH.split(File.pathSeparator, dirs);
	}
	public static String[] getJavaClassPathAbsolute() {
		String dirs = System.getProperty(PROPERTY_JAVA_CLASS_PATH);
		if (dirs == null)
			return OH.EMPTY_STRING_ARRAY;
		String[] names = SH.split(File.pathSeparator, dirs);
		for (int i = 0; i < names.length; i++)
			names[i] = IOH.getFullPath(new File(names[i]));
		return names;
	}

	public static String getJavaCompiler() {
		return System.getProperty(PROPERTY_JAVA_COMPILER);
	}

	public static String getOsName() {
		return System.getProperty("os.name");
	}

	public static String getOsArchitecture() {
		return System.getProperty(PROPERTY_OS_ARCH);
	}

	public static String getOsVersion() {
		return System.getProperty(PROPERTY_OS_VERSION);
	}

	public static String getUserName() {
		return System.getProperty(PROPERTY_USER_NAME);
	}

	public static String getUserHome() {
		return System.getProperty(PROPERTY_USER_HOME);
	}

	public static String getStartupClassName() {
		for (Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet()) {
			if (THREAD_MAIN.equals(e.getKey().getName())) {
				StackTraceElement ste = e.getValue()[e.getValue().length - 1];
				if (ste.getMethodName().equals(THREAD_MAIN))
					return ste.getClassName();
			}
		}
		return null;
	}

	public static String getStartupSimpleClassName() {
		return SH.afterLast(getStartupClassName(), ".");
	}

	static private long startupNanoTime;

	static {
		initNanoTime();
	}

	static private int initCount = 0;

	static private void initNanoTime() {
		int count = ++initCount;
		if (count % 10 == 0)
			System.err.println(EH.class.getName() + ": Recalculating nanos due to drift correction too often. Clock may have issues. Count: " + count);
		long nowMillis, nowNanos;
		long start = 0;
		// spin until millis Roles, at that point we can grab the nano time and derive an offset between nano & milli time
		start = nowMillis = currentTimeMillis();
		do {
			nowMillis = currentTimeMillis();
			nowNanos = System.nanoTime();
		} while (nowMillis == start);

		startupNanoTime = nowMillis * 1000L * 1000L - nowNanos;
	}

	private static final long NANOS_PER_MILLI = 1000 * 1000L;
	private static final long CHECK_PERIOD_NANOS = 1000 * 1000 * 1000L;
	private static final long ACCEPTABLE_VARIATION = 10 * 1000L;

	private static long nextCheckPoint = 0;

	static public long currentTimeNanos() {
		long nowNs = System.nanoTime() + startupNanoTime;

		if (nextCheckPoint < nowNs) {// Periodically check to see if the system
										// clock has been corrected for drift.
			long nowMsInNs = currentTimeMillis() * NANOS_PER_MILLI;
			if (!OH.isBetween(nowNs, nowMsInNs - ACCEPTABLE_VARIATION, nowMsInNs + NANOS_PER_MILLI + ACCEPTABLE_VARIATION)) {
				initNanoTime();
				nowNs = System.nanoTime() + startupNanoTime;
			}
			nextCheckPoint = nowNs + CHECK_PERIOD_NANOS;
		}
		return nowNs;
	}

	public static String getPid() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return SH.beforeFirst(name, "@", name);
	}

	//	public static void main(String a[]) {
	//		while (true) {
	//			for (int i = 0; i < 100; i++) {
	//				long ns = currentTimeNanos();
	//				long ms = System.currentTimeMillis();
	//				if (ms * 1000 * 1000 - 10000 > ns) {
	//					System.out.println("------");
	//					System.out.println(":" + ns);
	//					System.out.println(":" + ms);
	//				}
	//			}
	//			OH.sleep(1);
	//		}
	//	}

	public static Thread[] getAllThreads() {
		ThreadGroup rootGroup = getRootThreadGroup(null);
		Thread[] r;
		int count = rootGroup.activeCount();
		do {
			r = new Thread[count * 2];
			count = rootGroup.enumerate(r, true);
		} while (count == r.length);

		System.arraycopy(r, 0, r = new Thread[count], 0, count);
		return r;
	}

	public static ThreadGroup getRootThreadGroup(ThreadGroup tg) {
		if (tg == null)
			tg = Thread.currentThread().getThreadGroup();
		for (;;) {
			ThreadGroup parent = tg.getParent();
			if (parent == null)
				return tg;
			tg = parent;
		}

	}

	public static String[] getBootClassPath() {
		RuntimeMXBean runtimeInfo = ManagementFactory.getRuntimeMXBean();
		try {
			return SH.split(File.pathSeparator, runtimeInfo.getBootClassPath());
		} catch (Exception e) {
			return new String[] { e.getMessage() };
		}
	}

	public static String[] getJvmArguments() {
		RuntimeMXBean runtimeInfo = ManagementFactory.getRuntimeMXBean();
		List<String> r = runtimeInfo.getInputArguments();
		return r.toArray(new String[r.size()]);
	}

	/**
	 * @see System#currentTimeMillis()
	 */
	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * calls System.exit(exitCode). see {@link System#exit(int)}
	 * 
	 * @param exitCode
	 *            exit code for jvm
	 */
	public static void systemExit(int exitCode) {
		if (!F1GlobalProperties.getDisableSytemExit())
			System.exit(exitCode);
		else {
			System.err.println("WOULD EXIT WITH " + exitCode + " BUT disableSystemExit() HAS BEEN CALLED SO SLEEPING INSTEAD");
			while (true)
				OH.sleep(10000);
		}
	}

	public static Process exec(Executor exec, String command[], String env[], File pwd, byte[] stdin, boolean blocking, OutputStream stdOut, OutputStream stdErr) {
		try {
			final Process process = RT.exec(command, env, pwd);
			final StreamPiper p1 = new StreamPiper(process.getErrorStream(), OH.noNull(stdErr, System.err), 4096);
			final StreamPiper p2 = new StreamPiper(process.getInputStream(), OH.noNull(stdOut, System.out), 4096);
			final MonitoredRunnable runner1 = new MonitoredRunnable(p1);
			final MonitoredRunnable runner2 = new MonitoredRunnable(p2);
			exec.execute(runner1);
			exec.execute(runner2);
			if (stdin != null) {
				OutputStream out = process.getOutputStream();
				out.write(stdin);
				out.flush();
				out.close();
			}
			if (blocking) {
				process.waitFor();
				runner1.waitUntilComplete();
				runner2.waitUntilComplete();
				IOH.close(process.getInputStream());
				IOH.close(process.getOutputStream());
				IOH.close(process.getErrorStream());
			}
			return process;
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}

	}

	public static Locale getLocale() {
		return Locale.getDefault();
	}

	public static void toStdout(String string, boolean includeNewline) {
		if (includeNewline)
			stdout.println(string);
		else
			stdout.print(string);
		stdout.flush();
	}

	public static void toStderr(String string, boolean includeNewline) {
		if (includeNewline)
			stderr.println(string);
		else
			stderr.print(string);
		stderr.flush();

	}

	public static long getFreeMemory() {
		return RT.freeMemory();
	}

	public static long getMaxMemory() {
		return RT.maxMemory();
	}
	public static long getTotalMemory() {
		return RT.totalMemory();
	}
	public static int getAvailableProcessors() {
		return RT.availableProcessors();
	}

	/**
	 * 
	 * @param command
	 * @param pwd
	 * @param arguments
	 * @return tuple contain process, stdout, stderr
	 */
	public static ProcessResult exec(Executor executor, String command) {
		return exec(executor, new String[] { command }, null);
	}
	public static ProcessResult exec(Executor executor, String command[]) {
		return exec(executor, command, null);
	}
	public static byte[] execToStdout(Executor executor, String command[], byte[] stdin, int requiredExitCode) {
		ProcessResult r = exec(executor, command, stdin);
		int exitCode = r.getA().exitValue();
		if (exitCode != requiredExitCode) {
			if (AH.isntEmpty(r.getC()))
				throw new RuntimeException("Expected exit code " + requiredExitCode + ": " + exitCode + " stderr: " + new String(r.getC()));
			else if (AH.isntEmpty(r.getB()))
				throw new RuntimeException("Expected exit code " + requiredExitCode + ": " + exitCode + " stdout: " + new String(r.getB()));
			else
				throw new RuntimeException("Expected exit code " + requiredExitCode + ": " + exitCode);
		}
		return r.getB();
	}
	public static ProcessResult exec(Executor executor, String command[], byte[] stdin) {
		final FastByteArrayOutputStream outData = new FastByteArrayOutputStream();
		final FastByteArrayOutputStream errData = new FastByteArrayOutputStream();
		final Process process = exec(executor, command, null, null, stdin, true, outData, errData);
		outData.flush();
		errData.flush();
		return new ProcessResult(process, outData.toByteArray(), errData.toByteArray());
	}
	public static String getProcessUid() {
		if (processUid == null) {
			synchronized (EH.class) {
				if (processUid == null)
					processUid = "F1-" + GuidHelper.getGuid(62);
			}
		}
		return processUid;
	}

	private static RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
	private static final List<String> arguments = Collections.unmodifiableList(runtime.getInputArguments());
	private static final long startTime = runtime.getStartTime();

	private static boolean isDebug = getStartupArguments().toString().indexOf("-agentlib:jdwp") != -1;

	private static TimeZone ti;

	public static List<String> getStartupArguments() {
		return arguments;
	}

	public static long getStartTime() {
		return startTime;
	}

	public static boolean getIsDebug() {
		return isDebug;
	}
	public static String getJvmDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getJavaExecutableCommand(String options[], String className, String[] args) {

		StringBuilder sb = new StringBuilder();
		if (isWindows() == true)
			sb.append(getJavaHome()).append("/bin/java -classpath \"");
		else
			sb.append(getJavaHome()).append("/bin/java -classpath ");
		SH.join(File.pathSeparator, getJavaClassPathAbsolute(), sb);
		if (isWindows() == true)
			sb.append("\" ");
		else
			sb.append(" ");
		SH.join(' ', options, sb);
		sb.append(' ');
		sb.append(className);
		sb.append(' ');
		SH.join(' ', args, sb);
		return sb.toString();
	}
	public static boolean isWindows() {
		return isWindows;
	}
	private static void setWindows(boolean isW) {
		isWindows = isW;
	}

	public static Set<String> getTimeZoneRegions() {
		return timezonesByRegion.keySet();
	}

	public static Map<String, TimeZone> getTimeZonesByRegion(String region) {
		return timezonesByRegion.get(region);
	}

	public static Iterable<TimeZone> getTimeZones() {
		return timezonesByRegion.valuesMulti();
	}
	public static String getTimeZoneRegion(TimeZone tz) {
		return SH.beforeFirst(tz.getID(), '/', "");
	}

	public static void destroyOnShutdown(Process p) {
		ProcessShutdownManager.INSTANCE.addProcessForDestroyOnShutdown(p);
	}

	//Same as Timezone.getTimeZone except returns null if invalid
	public static TimeZone getTimeZone(String tz) {
		if (tz == null)
			throw new NullPointerException("Timezone");
		TimeZone r = timezonesByI.get(tz);
		if (r == null)
			r = timezonesByI.get(tz.trim());
		if (r == null)
			throw new NoSuchElementException("Timezone not found: " + tz);
		return r;
	}
	public static TimeZone getTimeZoneNoThrow(String tz) {
		if (tz == null)
			return null;
		TimeZone r = timezonesByI.get(tz);
		if (r == null)
			r = timezonesByI.get(tz.trim());
		return r;
	}
	public static TimeZone getTimeZoneOrGMT(String tz) {
		if (tz == null)
			return null;
		TimeZone r = timezonesByI.get(tz);
		if (r == null)
			r = timezonesByI.get(tz.trim());
		if (r == null)
			return GMT;
		return r;
	}

	static public TimeZone getGMT() {
		return GMT;
	}

	static public Set<String> getLocaleLanguagesDisplay() {
		return localesByLanguageDisplay.keySet();
	}
	static public Set<String> getLocaleLanguagesISO3() {
		return localesByLanguageISO3.keySet();
	}
	static public Locale getLocaleByLanguage(String name) {
		return CH.getOrThrow(localesByLanguage, name);
	}
	static public Locale getLocaleByLanguageNoThrow(String name) {
		return localesByLanguage.get(name);
	}

	public static void main(String a[]) {
		for (Locale i : Locale.getAvailableLocales()) {
			System.out.println(i.getISO3Country() + " - " + i.getISO3Language() + "    " + i.getDisplayName() + "    " + i.getDisplayLanguage());
		}
		for (String id : TimeZone.getAvailableIDs()) {
			System.out.println(id);
		}
	}

	public static String[] getEnv() {
		return ENV;
	}

	public static int checkRc5Strength() {
		try {
			return Cipher.getMaxAllowedKeyLength("RC5");
		} catch (NoSuchAlgorithmException e) {
			return -1;
		}
	}

}
