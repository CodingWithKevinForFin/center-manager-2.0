/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.f1.speedlogger.SpeedLoggerSink;
import com.f1.speedlogger.SpeedLoggerSinkFactory;
import com.f1.utils.EH;
import com.f1.utils.SH;

/**
 * A factory for creating sinks that will log to a file system. In general, this is a asynch implementation meaning that writes will be buffered momentarily in memory and written
 * to disk either after a specified buffer size is reached or a certain amout of time has passed. Also, files can be rolled after a certain file size has been reached.
 */
public class FileSpeedLoggerSinkFactory implements SpeedLoggerSinkFactory {

	private static final String OPTION_RING_AGGRESSIVE_TIMEOUT_NANOS = "ringAggressiveTimeoutNanos";

	private static final String ID = "file";

	/** the name of the file to write to */
	public static String OPTION_FILENAME = "fileName";

	/**
	 * max buffer size in bytes before flushing buffer to disk. zero indicates no buffering
	 */
	public static String OPTION_BUFFERSIZE = "bufferSize";

	/**
	 * number of buffers in ring, if <=2 then double buffer is used, otherwise ring of specified size
	 */
	public static String OPTION_BUFFERCOUNT = "bufferCount";

	/**
	 * max time delay before flushing buffer to disk. zero indicates no buffering
	 */
	public static String OPTION_DELAYFLUSHMS = "delayFlushMs";

	/**
	 * action to take for existing files on startup, value should be roll append or overwrite
	 */
	public static String OPTION_STARTUP = "startup";

	/** maximum number of files, only appicable when startup=roll */
	public static String OPTION_MAX_FILES = "maxFiles";

	/**
	 * max file size in megabytes before rolling, if zero then file will never roll
	 */
	public static String OPTION_MAX_FILE_SIZE = "maxFileSizeMb";

	/** roll existing file on statup */
	public static String STARTUP_ROLL = "roll";

	/** append to existing file on statup */
	public static String STARTUP_APPEND = "append";

	/** overwrite existing file on startup */
	public static String STARTUP_OVERWRITE = "overwrite";

	public static String DEFAULT_FILENAME = "speedLogger.log";
	public static String DEFAULT_BUFFERSIZE = "500000";
	public static String DEFAULT_BUFFERCOUNT = "2";
	public static String DEFAULT_DELAYMS = "50";
	public static String DEFAULT_STARTUP = STARTUP_ROLL;
	public static String DEFAULT_MAX_FILES = "10";
	public static String DEFAULT_MAX_FILE_SIZE = "1000";

	private Map<String, String> configuration = new HashMap<String, String>();

	public FileSpeedLoggerSinkFactory() {
		configuration.put(OPTION_FILENAME, DEFAULT_FILENAME);
		configuration.put(OPTION_BUFFERSIZE, DEFAULT_BUFFERSIZE);
		configuration.put(OPTION_DELAYFLUSHMS, DEFAULT_DELAYMS);
		configuration.put(OPTION_STARTUP, DEFAULT_STARTUP);
		configuration.put(OPTION_MAX_FILES, DEFAULT_MAX_FILES);
		configuration.put(OPTION_MAX_FILE_SIZE, DEFAULT_MAX_FILE_SIZE);
		configuration.put(OPTION_BUFFERCOUNT, DEFAULT_BUFFERCOUNT);
		configuration.put(OPTION_RING_AGGRESSIVE_TIMEOUT_NANOS, SH.toString(AutoFlushWriterRing.DEFAULT_AGGRESIVE_TIMEOUT_NANOS));
	}

	@Override
	public Map<String, String> getConfiguration() {
		return configuration;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public SpeedLoggerSink createSink(String id, Map<String, String> configuration) {
		String fileName = configuration.get(OPTION_FILENAME);
		final int bufferSize = Integer.parseInt(configuration.get(OPTION_BUFFERSIZE));
		final int delay = Integer.parseInt(configuration.get(OPTION_DELAYFLUSHMS));
		final int maxFiles = Integer.parseInt(configuration.get(OPTION_MAX_FILES));
		final long maxFileSize = Long.parseLong(configuration.get(OPTION_MAX_FILE_SIZE));
		int buffersCount = Integer.parseInt(configuration.get(OPTION_BUFFERCOUNT));
		final String startup = configuration.get("startup");
		final boolean roll = STARTUP_ROLL.equals(startup);
		final boolean append = STARTUP_APPEND.equals(startup);
		final boolean overwrite = STARTUP_OVERWRITE.equals(startup);

		if ((!roll || append || overwrite))
			throw new RuntimeException("invalid value for " + OPTION_STARTUP + " (must be roll,append or overwrite): " + startup);
		try {
			if (roll)
				fileName = SpeedLoggerUtils.roleFile(fileName, maxFiles).getPath();
			Writer writer = maxFileSize > 0 ? new AutoFileRollingWriter(maxFileSize * 1024L * 1024L, maxFiles, fileName, append) : new FileWriter(fileName, append);
			if (bufferSize == 0 || delay == 0)
				return new BasicSpeedLoggerSink(configuration, id, writer, "File:" + fileName);
			Writer flusher;
			if (buffersCount == 1) {
				flusher = new AutoFlushWriter2(writer, bufferSize, delay).start();
			} else
				flusher = new AutoFlushWriter(writer, bufferSize, delay).start();
			EH.toStdout("F1 Speedlogger sink " + id + ", file at: " + new File(fileName).getCanonicalPath(), true);
			return new BasicSpeedLoggerSink(configuration, id, flusher, "File:" + fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
