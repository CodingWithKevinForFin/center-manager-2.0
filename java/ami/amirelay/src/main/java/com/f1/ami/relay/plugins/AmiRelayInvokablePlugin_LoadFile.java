package com.f1.ami.relay.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.AmiRelayProperties;
import com.f1.ami.relay.fh.AmiFH;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.base.Caster;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Array;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;

public class AmiRelayInvokablePlugin_LoadFile implements AmiRelayInvokablePlugin {

	private static final Logger log = LH.get();

	private static final String TYPE = "LoadFile";

	private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();

	private ContainerTools tools;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;
	}

	@Override
	public String getPluginId() {
		return TYPE;
	}

	@Override
	public boolean invoke(AmiRelayIn server, Map<String, Object> params, AmiFH session, StringBuilder messageSink) {
		String rootFile = tools.getOptional(AmiRelayProperties.OPTION_AMI_RELAY_PLUGIN_RESOURCE_ROOT_DIR);
		if (SH.isnt(rootFile)) {
			messageSink.append("Required option missing: " + AmiRelayProperties.OPTION_AMI_RELAY_PLUGIN_RESOURCE_ROOT_DIR);
			return false;
		}
		if (!new File(rootFile).isDirectory()) {
			messageSink.append("Invalid directory specified via " + AmiRelayProperties.OPTION_AMI_RELAY_PLUGIN_RESOURCE_ROOT_DIR + ": " + rootFile);
			return false;
		}
		final String file = CH.getOr(Caster_String.INSTANCE, params, "FILE", null);
		if (file == null) {
			messageSink.append("FILE required");
			return false;
		}
		if (!IOH.isSecureChildPath(file)) {
			messageSink.append("Access denied, attempting to access parent file: ").append(file);
			return false;
		}
		File f = new File(IOH.join(rootFile, file));
		final String columns = CH.getOr(Caster_String.INSTANCE, params, "COLUMNS", null);
		if (columns == null) {
			messageSink.append("COLUMNS required");
			return false;
		}
		final String classes = CH.getOr(Caster_String.INSTANCE, params, "CLASSES", null);
		if (classes == null) {
			messageSink.append("CLASSES required");
			return false;
		}
		int mps = CH.getOr(Caster_Integer.INSTANCE, params, "MPS", -1);
		final String nullValue = CH.getOr(Caster_String.INSTANCE, params, "NULL", null);
		final char quote = CH.getOr(Caster_Character.PRIMITIVE, params, "QUOTES", (char) 0);
		final long limit = CH.getOr(Caster_Long.PRIMITIVE, params, "LIMIT", Long.MAX_VALUE);
		final long skip = CH.getOr(Caster_Long.PRIMITIVE, params, "SKIP", 0L);

		final char delim = CH.getOr(Caster_Character.PRIMITIVE, params, "DELIM", ',');
		final String type = CH.getOr(Caster_String.INSTANCE, params, "TYPE", "FILE:" + file);
		if (SH.startsWith(type, "__")) {
			messageSink.append("Can not use reserved type: " + type);
			return false;
		}

		final String[] columnNames = SH.trimArray(SH.split(",", columns));
		int columnsLength = columns.length();
		Class<?>[] classez = new Class[columnsLength];
		Caster<?>[] casters = new Caster<?>[columnsLength];
		DateFormat[] formatters = new DateFormat[columnsLength];
		String[] t = SH.split(",", classes);
		if (t.length != columnNames.length) {
			messageSink.append("Columns count does not match classes count: ").append(columnNames.length).append(" != ").append(t.length);
			return false;
		}
		for (int i = 0; i < t.length; i++) {
			String cl = t[i];
			if ("Enum".equals(cl)) {
				classez[i] = char[].class;
				casters[i] = Caster_Array.CHARACTER_PRIMITIVE;
			} else if (cl.startsWith("Date")) {
				classez[i] = Date.class;
				casters[i] = OH.getCaster(Date.class);
				String pattern = SH.trim('(', ')', SH.stripPrefix(cl, "Date", true));
				formatters[i] = new SimpleDateFormat(pattern);
			} else if ("Skip".equals(cl)) {
				classez[i] = null;
				casters[i] = null;
			} else {
				try {
					classez[i] = OH.forName(cl);
					casters[i] = OH.getCaster(OH.forName(cl));
				} catch (ClassNotFoundException e) {
					messageSink.append("Column class not found: ").append(cl);
					return false;
				}
			}
		}

		final String id = null;
		final long expires = 0;
		final LineNumberReader reader;
		try {
			reader = new LineNumberReader(new FileReader(f), 100000);
		} catch (FileNotFoundException e) {
			LH.info(log, "Could not open file: " + f, e);
			messageSink.append("Could not open file: ").append(f);
			return false;
		}
		int count = 0;
		final Map<String, Object> values = new IdentityHashMap<String, Object>();
		int rowNum = 0;
		StringBuilder buf = new StringBuilder();
		int msgsThisSecond = 0;
		int duration = 1000;
		if (mps >= 10) {
			mps /= 10;
			duration /= 10;
		} else {
			duration /= mps;
			mps = 1;
		}
		final List<String> row = new ArrayList<String>();
		for (;;) {
			final String line;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				messageSink.append("error, read ").append(count).append(" record(s) from file: ").append(file);
				return false;
			}
			if (line == null)
				break;
			if (rowNum++ < skip)
				continue;
			row.clear();
			if (quote != 0) {
				int start = 0;
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					if (c == quote) {
						i = SH.indexOfNotEscaped(line, quote, i + 1, '\\');
						if (i == -1)
							break;
					} else if (c == delim) {
						row.add(line.substring(start, i));
						start = i + 1;
					}
				}
				if (start < line.length() - 1)
					row.add(line.substring(start));
			} else {
				SH.splitToList(row, delim, line);
			}
			values.clear();
			final int cnt = Math.min(columnNames.length, row.size());
			long startOfPeriod = server.getTools().getNow();
			for (int i = 0; i < cnt; i++) {
				Class<?> clazz = classez[i];
				if (clazz == null)
					continue;
				String str = row.get(i);
				if (str == null)
					continue;
				if (quote != 0) {
					str = str.trim();
					if (str.length() > 1 && str.charAt(0) == quote && str.charAt(str.length() - 1) == quote)
						str = str.substring(1, str.length() - 1);
				} else if (nullValue != null && nullValue.equals(str))
					continue;
				if (clazz == char[].class) {
					values.put(columnNames[i], str);
				} else if (clazz == Date.class) {
					try {
						long value = formatters[i].parse(str).getTime();
						values.put(columnNames[i], value);
					} catch (Exception e) {

					}
				} else if (clazz == String.class) {
					values.put(columnNames[i], str);
				} else {
					Object value = casters[i].cast(str, false, false);
					if (value != null)
						values.put(columnNames[i], value);
				}
			}
			server.onObject(-1, id, type, expires, converter.toBytes(values));
			if (++count >= limit)
				break;
			if (mps != -1 && ++msgsThisSecond >= mps) {
				OH.sleepFromStart(startOfPeriod, duration, server.getTools().getServices().getClock());
				startOfPeriod += duration;
				msgsThisSecond = 0;
			}
		}
		messageSink.append("Sent ").append(count).append(" record(s) from file: ").append(file);
		return false;
	}
}
