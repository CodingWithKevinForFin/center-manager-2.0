/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.f1.speedlogger.SpeedLoggerEventListener;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public class SpeedLoggerUtils {
	public static final SpeedLoggerEventListener[] EMPTY_LISTENER_ARRAY = new SpeedLoggerEventListener[0];

	public static String getLevelAsString(int level) {
		switch (level) {
			case SpeedLoggerLevels.ALL:
				return SpeedLoggerLevels.LABEL_ALL;
			case SpeedLoggerLevels.TRACE:
				return SpeedLoggerLevels.LABEL_TRACE;
			case SpeedLoggerLevels.FINEST:
				return SpeedLoggerLevels.LABEL_FINEST;
			case SpeedLoggerLevels.FINER:
				return SpeedLoggerLevels.LABEL_FINER;
			case SpeedLoggerLevels.FINE:
				return SpeedLoggerLevels.LABEL_FINE;
			case SpeedLoggerLevels.DEBUG:
				return SpeedLoggerLevels.LABEL_DEBUG;
			case SpeedLoggerLevels.CONFIG:
				return SpeedLoggerLevels.LABEL_CONFIG;
			case SpeedLoggerLevels.INFO:
				return SpeedLoggerLevels.LABEL_INFO;
			case SpeedLoggerLevels.WARNING:
				return SpeedLoggerLevels.LABEL_WARNING;
			case SpeedLoggerLevels.ERROR:
				return SpeedLoggerLevels.LABEL_ERROR;
			case SpeedLoggerLevels.SEVERE:
				return SpeedLoggerLevels.LABEL_SEVERE;
			case SpeedLoggerLevels.FATAL:
				return SpeedLoggerLevels.LABEL_FATAL;
			case SpeedLoggerLevels.OFF:
				return SpeedLoggerLevels.LABEL_OFF;
			default:
				return "Level-" + SH.toString(level);
		}
	}

	public static String getFullLevelAsString(int level) {
		switch (level) {
			case SpeedLoggerLevels.ALL:
				return SpeedLoggerLevels.FULL_LABEL_ALL;
			case SpeedLoggerLevels.TRACE:
				return SpeedLoggerLevels.FULL_LABEL_TRACE;
			case SpeedLoggerLevels.FINEST:
				return SpeedLoggerLevels.FULL_LABEL_FINEST;
			case SpeedLoggerLevels.FINER:
				return SpeedLoggerLevels.FULL_LABEL_FINER;
			case SpeedLoggerLevels.FINE:
				return SpeedLoggerLevels.FULL_LABEL_FINE;
			case SpeedLoggerLevels.DEBUG:
				return SpeedLoggerLevels.FULL_LABEL_DEBUG;
			case SpeedLoggerLevels.CONFIG:
				return SpeedLoggerLevels.FULL_LABEL_CONFIG;
			case SpeedLoggerLevels.INFO:
				return SpeedLoggerLevels.FULL_LABEL_INFO;
			case SpeedLoggerLevels.WARNING:
				return SpeedLoggerLevels.FULL_LABEL_WARNING;
			case SpeedLoggerLevels.ERROR:
				return SpeedLoggerLevels.FULL_LABEL_ERROR;
			case SpeedLoggerLevels.SEVERE:
				return SpeedLoggerLevels.FULL_LABEL_SEVERE;
			case SpeedLoggerLevels.FATAL:
				return SpeedLoggerLevels.FULL_LABEL_FATAL;
			case SpeedLoggerLevels.OFF:
				return SpeedLoggerLevels.FULL_LABEL_OFF;
			default:
				return "Level-" + SH.toString(level);
		}
	}

	public static int parseLevel(String level) {
		return getOrThrow(SpeedLoggerLevels.LABEL_2_LEVELS, level, "log level");
	}

	public static <K, V> V getOrThrow(Map<K, V> m, K key) {
		return getOrThrow(m, key, "key");
	}

	public static <K, V> V getOrThrow(Map<K, V> m, K key, String description) {
		return CH.getOrThrow(m, key, description);
	}

	public static File getFile(String fileName, int i) {
		if (i == 0)
			return new File(fileName);
		StringBuilder sb = new StringBuilder(fileName).append('.');
		String sI = SH.toString(i);
		if (i < 10)
			sb.append('0');
		if (i < 100)
			sb.append('0');
		if (i < 1000)
			sb.append('0');
		sb.append(sI);
		return new File(sb.toString());
	}

	public static File roleFile(String fileName, int maxFilesCount) throws IOException {
		int dstFileId = 0;
		File dstFile = null;
		for (;;) {
			dstFile = SpeedLoggerUtils.getFile(fileName, dstFileId);
			if (!dstFile.exists())
				break;
			if (dstFileId == maxFilesCount) {
				if (!dstFile.delete())
					throw new RollFileException("could not delete during roll: " + IOH.getFullPath(dstFile));
				break;
			}
			dstFileId++;
		}

		for (int i = dstFileId; i > 0; i--) {
			File srcFile = SpeedLoggerUtils.getFile(fileName, i - 1);
			if (!srcFile.renameTo(dstFile))
				throw new RollFileException("could not roll: " + IOH.getFullPath(srcFile) + " ==> " + IOH.getFullPath(dstFile)
						+ " (is another active application already reading from / writing to this log file?)");
			dstFile = srcFile;
		}
		return dstFile;

	}

	private static final AtomicLong uid = new AtomicLong(0);

	static public long generateUid() {
		return uid.incrementAndGet();
	}
}
