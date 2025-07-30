package com.f1.ami.relay.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.AmiRelayProperties;
import com.f1.ami.relay.fh.AmiFH;
import com.f1.ami.relay.fh.AmiSocketFH;
import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.NullOutputStream;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;

public class AmiRelayInvokablePlugin_Replay implements AmiRelayInvokablePlugin {

	private static final Logger log = LH.get();

	private static final String TYPE = "Replay";

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
		final String files = CH.getOr(Caster_String.INSTANCE, params, "FILE", null);
		if (files == null) {
			messageSink.append("FILE required");
			return false;
		}
		String[] filesList = SH.splitWithEscape(',', '\\', files);
		final long maxSleep;
		if (params.containsKey("MAXDELAY"))//backwards compatibility
			maxSleep = CH.getOr(Caster_Long.INSTANCE, params, "MAXDELAY", 0L);
		else
			maxSleep = CH.getOr(Caster_Long.INSTANCE, params, "MAX_DELAY", 0L);
		final long limit = CH.getOr(Caster_Long.PRIMITIVE, params, "LIMIT", Long.MAX_VALUE);
		final long maxPerSecond = CH.getOr(Caster_Long.PRIMITIVE, params, "MAX_PER_SECOND", Long.MAX_VALUE);
		final String login = CH.getOr(Caster_String.INSTANCE, params, "LOGIN", null);
		PipedOutputStream inOut = null;
		long lastTime = -1;
		long lastPerSecond = System.currentTimeMillis();
		long countInLastSecond = 0;
		long offset = -1;
		try {
			inOut = new PipedOutputStream();
			InputStream inIn = new PipedInputStream(inOut, 1024 * 100);

			AmiSocketFH fh2 = new AmiSocketFH(server.getTools().getNow(), inIn, NullOutputStream.INSTANCE, "REPLAY", -1);
			server.initAndStartFH(fh2, "ReplaySocketFH");
			FastByteArrayDataOutputStream line = new FastByteArrayDataOutputStream();
			if (login != null)
				inOut.write(("L|I=\"" + login + "\"\n").getBytes());
			outer: for (String file : filesList) {
				FastBufferedInputStream reader;
				try {
					if (!IOH.isSecureChildPath(file)) {
						LH.info(log, "Access denied, Attempting to access parent file: " + file);
						if (messageSink.length() > 0)
							messageSink.append(", ");
						messageSink.append("Access denied, attempting to access parent file: ").append(file);
						continue;
					}
					File f = new File(IOH.join(rootFile, file));
					if (!f.exists()) {
						LH.info(log, "File not found: " + file);
						if (messageSink.length() > 0)
							messageSink.append(", ");
						messageSink.append("File not found: ").append(file);
						continue;
					}
					reader = new FastBufferedInputStream(new FileInputStream(f), 100000);
				} catch (FileNotFoundException e) {
					LH.info(log, "Could not open file: " + file, e);
					if (messageSink.length() > 0)
						messageSink.append(", ");
					messageSink.append("Could not open file: ").append(file);
					continue;
				}
				int count = 0;
				TimeParser tp = new TimeParser();
				try {
					final long startMs = System.currentTimeMillis();
					for (boolean eof = false; !eof;) {
						if (count >= limit) {
							messageSink.append("[LIMIT-REACHED] Replayed ").append(count).append(" lines from ").append(file);
							break outer;
						}
						line.reset(102400);
						int start = -1;
						line: for (;;) {
							int b = reader.read();

							switch (b) {
								case '\r':
									continue;
								case -1:
									eof = true;
								case '\n':
									line.writeByte(b);
									break line;
								case '>':
									if (start == -1)
										start = line.getCount();
									line.writeByte(b);
									break;
								default:
									line.writeByte(b);
							}
						}
						int lineCount = line.getCount();
						byte[] buf = line.getBuffer();
						if (start == -1 || start + 3 > lineCount || buf[start + 1] != '>' || buf[start + 2] != ' ')
							continue;
						boolean interrupted = false;
						if (maxSleep > 0) {
							long time;
							if (start == 23) {//legacy, no date or timestamp
								time = parseTime(buf, 0);
							} else {
								time = tp.parse(buf);
							}
							if (time == -1)
								continue;
							long now = System.currentTimeMillis();
							if (lastTime == -1) {
								offset = now - time;
							} else if (time < lastTime && start == 23)
								time += 86400000;
							long sleep = offset + time - now;
							if (sleep > 0) {
								if (sleep > maxSleep) {
									offset -= sleep - maxSleep;
									sleep = maxSleep;
								}
								interrupted = !OH.sleep(sleep);
							}
							lastTime = time;
						}
						inOut.write(buf, start + 3, lineCount - (start + 3));
						inOut.flush();
						count++;
						countInLastSecond++;
						if (countInLastSecond >= maxPerSecond) {
							long now = System.currentTimeMillis();
							long remaining = 1000 - (now - lastPerSecond);
							if (remaining > 0)
								interrupted = !OH.sleep(remaining);
							lastPerSecond = now;
							countInLastSecond = 0;
						}
						if (interrupted || Thread.interrupted()) {
							if (messageSink.length() > 0)
								messageSink.append(", ");
							messageSink.append("[USER-BREAK] Replayed ").append(count).append(" lines from ").append(file);
							break outer;
						}
					}
					LH.info(log, "Replayed ", count, " lines from ", file);
					if (messageSink.length() > 0)
						messageSink.append(", ");
					messageSink.append("Replayed ").append(count).append(" lines from ").append(file).append(" in ").append(System.currentTimeMillis() - startMs).append(" millis");
				} catch (Exception e) {
					LH.warning(log, "Error at line ", (count + 1), " with file: ", file, e);
				} finally {
					IOH.close(reader, false);
				}
			}
		} catch (IOException e) {
		} finally {
			IOH.close(inOut, false);
		}
		return false;
	}

	static private int parseTime(byte[] buf, int i) {
		return toNum(buf, i) * 3600000 + toNum(buf, i + 3) * 60000 + toNum(buf, i + 6) * 1000 + toNum(buf, i + 9) * 10 + (buf[i + 11] - '0');
	}
	static private int toNum(byte[] buf, int i) {
		return (buf[i] - '0') * 10 + buf[i + 1] - '0';
	}

	public static class TimeParser {
		final private Calendar calendar = Calendar.getInstance();
		private byte[] date = OH.EMPTY_BYTE_ARRAY;
		private byte[] timezone = OH.EMPTY_BYTE_ARRAY;
		private long startOfDay;

		public long parse(byte buf[]) {
			if (buf[8] != '-' && buf[21] != ' ')
				return -1;
			int timezoneLength = 0;
			for (int i = 22; i < buf.length; i++) {
				if (buf[i] == ' ')
					break;
				timezoneLength++;
			}
			if (!AH.eq(date, 0, buf, 0, 8) || !AH.eq(timezone, 0, buf, 22, timezoneLength)) {
				date = AH.subarray(buf, 0, 8);
				timezone = AH.subarray(buf, 22, timezoneLength);
				calendar.clear();
				calendar.setTimeZone(TimeZone.getTimeZone(new String(timezone)));
				calendar.set(Calendar.YEAR, toNum(date, 0) * 100 + toNum(date, 2));
				calendar.set(Calendar.MONTH, toNum(date, 4) - 1);
				calendar.set(Calendar.DAY_OF_MONTH, toNum(date, 6));
				this.startOfDay = calendar.getTimeInMillis();
			}
			long time = parseTime(buf, 9);
			return startOfDay + time;
		}
	}
}
