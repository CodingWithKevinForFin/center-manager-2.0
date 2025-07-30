/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.f1.base.F1LicenseInfo;
import com.f1.utils.impl.BasicLabeler;
import com.f1.utils.structs.table.BasicTable;

public class EnvironmentDump {

	public static interface EnvLogger {
		public void log(String label, Object value);
		public void logLabeler(String label, Labeler value);
		public void log(String label, Object[] value);
		public void log(String label, Collection value);
		public void log(String value);
		public void logHeader();
	}

	public static class SbLogger implements EnvLogger {

		private final StringBuilder sb;

		public SbLogger(StringBuilder sb) {
			this.sb = sb;
		}

		@Override
		public void log(String label, Object value) {
			sb.append(PRE).append(SH.rightAlign(' ', label, 22, false)).append(": ");
			String str = SH.toString(value);
			sb.append(SH.prefixLines(str, PRE + SH.repeat(' ', 24), false));
			if (!str.endsWith("\n") && !str.endsWith("\r"))
				sb.append(SH.NEWLINE);
		}

		@Override
		public void log(String label, Object[] values) {
			log(label, values.length > 0 ? OH.toString(values[0]) : "");
			for (int i = 1; i < values.length; i++) {
				sb.append(SH.prefixLines(SH.toString(values[i]), PRE + SH.repeat(' ', 24), true)).append(SH.NEWLINE);
			}
		}

		@Override
		public void log(String label, Collection values) {
			Iterator i = values.iterator();
			log(label, i.hasNext() ? OH.toString(i.next()) : "");
			while (i.hasNext()) {
				sb.append(SH.prefixLines(SH.toString(i.next()), PRE + SH.repeat(' ', 24), true)).append(SH.NEWLINE);
			}
		}

		@Override
		public void log(String str) {
			sb.append(PRE).append(str).append(SH.NEWLINE);
		}

		@Override
		public void logHeader() {
			sb.append(HEADER).append(SH.NEWLINE);
		}

		@Override
		public void logLabeler(String label, Labeler labeler) {
			log(label, labeler.toString(PRE + SH.repeat(' ', 20), "=", Labeler.OPTION_SORT_LABELS | Labeler.OPTION_LEFT_ALIGN | Labeler.OPTION_HIDE_PASSWORDS));

		}

	}

	public static final Logger log = Logger.getLogger(EnvironmentDump.class.getName());
	public static final String HEADER = "+----------------------------------";
	public static final String PRE = "| ";
	final public static Pattern PASSWORD_MASK;
	static {
		try {
			PASSWORD_MASK = Pattern.compile(F1GlobalProperties.getPasswordMask());
		} catch (Exception e) {
			throw new RuntimeException("value for property " + F1GlobalProperties.PROPERTY_PASSWORD_MASK + " is not a valid regular expression");
		}
	}

	public static boolean isPasswordKey(String key) {
		return PASSWORD_MASK.matcher(key).matches();
	}

	public static StringBuilder dump(Class startupClass, String args[], PropertyController pc, StringBuilder sb) {
		dump(startupClass, args, pc, new SbLogger(sb));
		return sb;
	}
	public static void dump(Class startupClass, String args[], PropertyController pc, EnvLogger sb) {
		Set<Entry<Object, Object>> properties = pc == null ? null : pc.getProperties().entrySet();
		List<String> propertySources = pc == null ? null : pc.getAllSources();
		Map<String, Object> optionallyDeclaredProperties = pc == null ? null : pc.getDefaultDeclaredProperties();
		int strength = EH.checkRc5Strength();
		logHeader(sb);
		log(sb, "Visit http://3forge.com");
		log(sb, F1GlobalProperties.getTitle());
		logHeader(sb);
		if (startupClass != null)
			log(sb, "Startup Class", startupClass.getName());
		log(sb, "Startup Time", new Date(ManagementFactory.getRuntimeMXBean().getStartTime()).toString());
		log(sb, "Working Dir", EH.getPwd());
		log(sb, "Local Host", EH.getLocalHost());
		log(sb, "Pid", EH.getPid());
		log(sb, "F1 ProcessUid", EH.getProcessUid());
		log(sb, "CPU Count", SH.toString(Runtime.getRuntime().availableProcessors()));
		log(sb, "Avail.Memory", formatMemory(Runtime.getRuntime().maxMemory()));
		log(sb, "Line Seperator", toHex(SH.NEWLINE));
		log(sb, "Java Home", EH.getJavaHome());
		log(sb, "Java Version", EH.getJavaVersion());
		log(sb, "Java Vendor", EH.getJavaVendor());
		log(sb, "Java Compiler", EH.getJavaCompiler());
		log(sb, "RC5 Strength", strength == Integer.MAX_VALUE ? "unlimited" : strength);
		log(sb, "OS - Version", EH.getOsName() + " - " + EH.getOsVersion());
		log(sb, "OS Arch.", EH.getOsArchitecture());
		log(sb, "Current Time", EH.now().toString());
		log(sb, "Default TimeZone", EH.getTimeZone());
		log(sb, "Default Charset", Charset.defaultCharset().name());
		if (EH.getLocale() != null) {
			log(sb, "Default Country", EH.getLocale().getCountry());
			log(sb, "Default Language", EH.getLocale().getLanguage());
		}
		log(sb, "User", EH.getUserName());
		log(sb, "User Home", EH.getUserHome());
		logHeader(sb);
		log(sb, "License App", F1LicenseInfo.getLicenseApp());
		log(sb, "License Instance", F1LicenseInfo.getLicenseInstance());
		log(sb, "License Host", F1LicenseInfo.getLicenseHost());
		log(sb, "License Start Date", F1LicenseInfo.getLicenseStartDate());
		log(sb, "License End Date", F1LicenseInfo.getLicenseEndDate());
		log(sb, "License Text", F1LicenseInfo.getLicenseText());
		logHeader(sb);
		dumpMemory(sb);
		logHeader(sb);
		log(sb, "Java Ext. Dirs.", EH.getJavaExtDirs());
		logHeader(sb);
		log(sb, "Java Classpath", appendFileInfo(EH.getJavaClassPath(), false));
		logHeader(sb);
		log(sb, "Boot Classpath", appendFileInfo(EH.getBootClassPath(), false));
		logHeader(sb);
		log(sb, "Jvm arguments", EH.getJvmArguments());
		logHeader(sb);
		if (args != null)
			log(sb, "Arguments", args);

		if (CH.isntEmpty(propertySources)) {
			logHeader(sb);
			log(sb, "Property Sources", appendFileInfo(propertySources.toArray(new String[propertySources.size()]), true));
		}
		if (CH.isntEmpty(System.getenv())) {
			logHeader(sb);
			BasicLabeler label = new BasicLabeler();
			for (Map.Entry m : System.getenv().entrySet())
				label.addItem(SH.toString(m.getKey()), SH.toString(m.getValue()), true);
			logLabeler(sb, "Env. Vars", label);
		}
		if (properties != null) {
			logHeader(sb);
			BasicLabeler label = new BasicLabeler();
			for (Map.Entry m : properties) {
				String key = SH.toString(m.getKey());
				Property p = pc.getProperty(key);
				if (p != null && p.getIsSecure())
					label.addItem(key, F1GlobalProperties.getPasswordSubstitute(), true);
				else
					label.addItem(key, SH.toString(m.getValue()), true);
			}
			logLabeler(sb, "Properties", label);
		}
		if (optionallyDeclaredProperties != null) {
			logHeader(sb);
			BasicLabeler label = new BasicLabeler();
			for (Map.Entry m : optionallyDeclaredProperties.entrySet())
				label.addItem(SH.toString(m.getKey()), SH.toString(m.getValue()), true);
			logLabeler(sb, "Default Properties", label);
		}
		{
			logHeader(sb);
			BasicLabeler label = new BasicLabeler();
			for (Entry<String, Object> m : F1GlobalProperties.getProps().entrySet())
				label.addItem(SH.toString(m.getKey()), SH.toString(m.getValue()), true);
			logLabeler(sb, "F1 Global properties", label);
		}
		logHeader(sb);

	}

	public static String[] appendFileInfo(String[] files, boolean isPropertySources) {
		if (!AH.isEmpty(files)) {
			StringBuilder tmp = new StringBuilder();
			int max = 0;
			for (int i = 0; i < files.length; i++)
				max = Math.max(max, files[i].length());

			for (int i = 0; i < files.length; i++) {
				files[i] = appendFileInfo(files[i], tmp, max, isPropertySources);
			}
		}
		return files;
	}

	public static String appendFileInfo(String fileName, StringBuilder tmp, int max, boolean isPropertySources) {
		if (SH.isnt(fileName) || (isPropertySources && !SH.startsWith(fileName, "file:")))
			return fileName;
		SH.clear(tmp).append(fileName);
		SH.repeat(' ', max - fileName.length() + 2, tmp);
		try {
			File f = new File(isPropertySources ? fileName.substring(5) : fileName);
			if (!f.exists())
				return tmp.append("(missing)").toString();
			else if (!f.canRead())
				return tmp.append("(unreadable)").toString();
			else if (f.isDirectory())
				return fileName;
			byte[] data = IOH.readData(f);
			return tmp.append("(cksum  ").append(IOH.cksum(data)).append(' ').append(data.length).append(')').toString();
		} catch (Exception e) {
			return tmp.append('(').append(OH.getSimpleClassName(e)).append(')').toString();
		}
	}
	public static StringBuilder dumpStartupComplete(Map<String, Map<String, Object>> extras) {
		StringBuilder sb = new StringBuilder();
		dumpStartupComplete(extras, new SbLogger(sb));
		return sb;
	}
	public static void dumpStartupComplete(Map<String, Map<String, Object>> extras, EnvLogger sb) {
		long dur = EH.currentTimeMillis() - EH.getStartTime();
		logHeader(sb);
		log(sb, "Startup duration(millis)", SH.toStringWithCommas(',', dur));
		if (extras != null) {
			for (Map.Entry<String, Map<String, Object>> e : extras.entrySet()) {
				logHeader(sb);
				if (e.getValue() == null)
					continue;
				BasicLabeler label = new BasicLabeler();
				for (Map.Entry m : e.getValue().entrySet())
					label.addItem(SH.toString(m.getKey()), SH.toString(m.getValue()), true);
				logLabeler(sb, e.getKey(), label);
			}
		}
		{
			logHeader(sb);
			BasicLabeler label = new BasicLabeler();
			for (Entry<String, Object> m : F1GlobalProperties.getProps().entrySet())
				label.addItem(SH.toString(m.getKey()), SH.toString(m.getValue()), true);
			logLabeler(sb, "F1 Global properties", label);
		}
		logHeader(sb);
		System.out.println("Startup complete in " + SH.toStringWithCommas(',', dur) + " milliseconds");
	}

	private static void logHeader(EnvLogger sb) {
		sb.logHeader();
	}

	private static String formatMemory(long bytes) {
		SH.toString(bytes);
		return SH.formatMemory(bytes) + " (" + SH.toStringWithCommas(',', bytes) + " B)";
	}

	private static String getLocales() {
		BasicTable table = new BasicTable(
				new String[] { "ISO3-Language", "Language", "Display Language", "ISO3-Country", "Country", "Display Country", "Name", "Variant", "Display Variant" });
		for (Locale l : Locale.getAvailableLocales()) {
			String isoCountry;
			String isoLanguage;
			try {
				isoCountry = l.getISO3Country();
			} catch (MissingResourceException e) {
				isoCountry = "<WARNING: F1 DETECTED MISSING RESOURCE>";
			}
			try {
				isoLanguage = l.getISO3Language();
			} catch (MissingResourceException e) {
				isoLanguage = "<WARNING: F1 DETECTED MISSING RESOURCE>";
			}
			table.getRows().addRow(isoLanguage, l.getLanguage(), l.getDisplayLanguage(), isoCountry, l.getCountry(), l.getDisplayCountry(), l.getDisplayName(), l.getVariant(),
					l.getDisplayVariant());
		}
		TableHelper.sort(table, "ISO3-Language", "ISO3-Country");
		return TableHelper.toString(table, "", TableHelper.SHOW_ALL_BUT_TYPES ^ TableHelper.SHOW_BORDERS);
	}
	private static String getTimezones() {
		Date now = new Date();
		long nowMs = now.getTime();
		long nextWeek = now.getTime() + TimeUnit.DAYS.toMillis(7);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
		final BasicTable table = new BasicTable(new String[] { "ID", "Display Name", "DST Saving", "DST Offset", "In DST Now", "Next DST", "Notes" });
		table.setTitle("TimeZone Offsets based on current time (" + now + ")");
		for (final String id : TimeZone.getAvailableIDs()) {
			final TimeZone tz = TimeZone.getTimeZone(id);
			Date change = getNextDstChange(now.getTime(), tz);
			int daysTilChange = change == null ? -1 : (int) TimeUnit.MILLISECONDS.toDays(change.getTime() - now.getTime());
			if (daysTilChange > 14)
				daysTilChange = -1;
			boolean changing = (tz.getOffset(nowMs) != tz.getOffset(nextWeek));
			table.getRows().addRow(id, tz.getDisplayName(), tz.getDSTSavings() == 0 ? "" : SH.formatDuration(tz.getDSTSavings()), SH.formatDuration(tz.getRawOffset()),
					tz.getOffset(nowMs) != tz.getRawOffset() ? "IN_DST" : "", change == null ? "" : sdf.format(change),
					daysTilChange == -1 ? "" : ("WARNING: Change in " + daysTilChange + " day(s)"));
		}
		TableHelper.sort(table, "ID");
		return TableHelper.toString(table, "", TableHelper.SHOW_ALL_BUT_TYPES ^ TableHelper.SHOW_BORDERS);
	}
	private static Date getNextDstChange(long now, TimeZone tz) {
		if (tz.getDSTSavings() == 0)
			return null;
		int offset = tz.getOffset(now);
		boolean forward = true;
		long guess = now;
		long max = guess + TimeUnit.DAYS.toMillis(365);
		for (long jumpSize = TimeUnit.DAYS.toMillis(1); jumpSize > 0;) {
			if (forward) {
				if ((guess += jumpSize) > max)
					return null;
			} else
				guess -= jumpSize;
			if (forward != (tz.getOffset(guess) == offset)) {
				forward = !forward;
				jumpSize /= 2;
			}
		}
		return new Date(guess);
	}
	private static String toHex(String text) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n')
				sb.append("\\n");
			else if (c == '\r')
				sb.append("\\r");
			else
				sb.append("0x").append(Integer.toHexString(c));
		}
		return sb.toString();
	}

	public void testLoggers() {
		System.err.println("#TEST STDERR#");
		System.out.println("#TEST STDOUT#");
		LH.severe(log, "#TEST SUN LOGGER SEVERE#");
		LH.warning(log, "#TEST SUN LOGGER WARNING#");
		LH.info(log, "#TEST SUN LOGGER INFO#");
		LH.fine(log, "#TEST SUN LOGGER FINE#");
		LH.finer(log, "#TEST SUN LOGGER FINER#");
		LH.finest(log, "#TEST SUN LOGGER FINEST#");
	}

	private static void dumpMemory(EnvLogger sb) {
		for (MemoryPoolMXBean pool : java.lang.management.ManagementFactory.getMemoryPoolMXBeans()) {
			log(sb, "Max " + pool.getName(), formatMemory(pool.getUsage().getMax()));
		}
	}

	private static void dumpNics(EnvLogger sb) {
		try {
			int j = 0;
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				List<String> lines = new ArrayList<String>();
				lines.add(intf.getName() + " " + intf.getDisplayName());
				for (InetAddress i : CH.l(intf.getInetAddresses())) {
					lines.add(" +--> " + i.getHostAddress() + " " + i.getHostName() + "  [" + describeFlags(i) + "]");
				}
				log(sb, "Network Interface " + j, lines.toArray());
				j++;
			}
		} catch (Exception e) {

		}
	}

	private static String describeFlags(InetAddress i) {
		StringBuilder sb = new StringBuilder();
		if (i.isAnyLocalAddress())
			sb.append(",any-local");
		if (i.isLinkLocalAddress())
			sb.append(",link-local");
		if (i.isLoopbackAddress())
			sb.append(",loop-back");
		if (i.isMCGlobal())
			sb.append(",mc-global");
		if (i.isMCLinkLocal())
			sb.append(",mc-link-local");
		if (i.isMCNodeLocal())
			sb.append(",mc-node-local");
		if (i.isMCOrgLocal())
			sb.append(",mc-org-local");
		if (i.isMCSiteLocal())
			sb.append(",mc-site-local");
		if (i.isMulticastAddress())
			sb.append(",multicast");
		if (i.isSiteLocalAddress())
			sb.append(",site-local");
		if (sb.length() > 2)
			return sb.substring(1);
		return "";
	}

	public static void log(EnvLogger sb, String label, Object value) {
		sb.log(label, value);
	}
	public static void logLabeler(EnvLogger sb, String label, Labeler value) {
		sb.logLabeler(label, value);
	}

	public static void log(EnvLogger sb, String label, Object[] values) {
		sb.log(label, values);
	}

	public static void log(EnvLogger sb, String label, Collection values) {
		sb.log(label, values);
	}

	private static void log(EnvLogger sb, String str) {
		sb.log(str);
	}

	public static String dump(Class<?> clazz, String[] args, PropertyController properties) {
		StringBuilder sb = new StringBuilder();
		if (properties != null)
			dump(clazz, args, properties, sb);
		else
			dump(clazz, args, null, sb);
		return sb.toString();
	}

}
