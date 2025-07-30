package com.f1.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LH {

	private static ConcurrentHashMap<String, AtomicInteger> loggers = new ConcurrentHashMap<String, AtomicInteger>();

	private static Logger getLogger(String name) {
		AtomicInteger count = loggers.get(name);
		if (count == null) {
			count = new AtomicInteger();
			AtomicInteger t = loggers.putIfAbsent(name, count);
			if (t != null)
				count = t;
		}
		int n = count.incrementAndGet();
		if (n > 1 && n < 10)
			System.err.println("com.ft.utils.LH: Redundant(" + n + ") Request for logger: " + name);
		return Logger.getLogger(name);
	}

	private LH() {
	}

	static int count = 0;

	//Stander Method Wrappers
	public static Logger get(Class<?> clazz) {
		return getLogger(clazz.getName());
	}
	public static Logger get() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return getLogger(stackTrace[2].getClassName());
	}

	public static void log(Logger logger, Level level, Object data1) {
		if (logger.isLoggable(level))
			rawlog(logger, level, "", data1);
	}

	public static void log(Logger logger, Level level, Object data1, Object data2) {
		if (logger.isLoggable(level))
			rawlogArray(logger, level, "", new Object[] { data1, data2 });
	}
	public static void log(Logger logger, Level level, Object data1, Object data2, Object data3) {
		if (logger.isLoggable(level))
			rawlogArray(logger, level, "", new Object[] { data1, data2, data3 });
	}

	public static void log(Logger logger, Level level, Object... data) {
		if (logger.isLoggable(level))
			rawlogArray(logger, level, "", data);
	}
	private static boolean isLoggable(Logger logger, Level level) {
		return logger.isLoggable(level);
	}

	private static void rawlogArray(Logger logger, Level level, String text, Object data[]) {
		if (logger.getClass() == Logger.class) {

			logger.log(level, text + join(data));
		} else
			logger.log(level, text, data);
	}
	private static String join(Object[] data) {
		if (data.length == 0)
			return "";
		if (data.length == 1 && !(data[0] instanceof Throwable))
			return data[0] == null ? "null" : data[0].toString();
		StringBuilder sb = new StringBuilder();
		for (Object o : data) {
			if (o instanceof Throwable) {
				SH.printStackTrace("", ">> ", (Throwable) o, sb);
			} else
				sb.append(o);
		}
		return sb.toString();
	}
	private static void rawlog(Logger logger, Level level, String text, Object data) {
		if (logger.getClass() == Logger.class)
			logger.log(level, text + data);
		else
			logger.log(level, text, data);
	}

	//convenience
	public static void w(Logger logger, Object... data) {
		log(logger, Level.WARNING, data);
	}
	public static void i(Logger logger, Object... data) {
		log(logger, Level.INFO, data);
	}
	public static void s(Logger logger, Object... data) {
		log(logger, Level.SEVERE, data);
	}
	public static void c(Logger logger, Object... data) {
		log(logger, Level.CONFIG, data);
	}
	public static void fn(Logger logger, Object... data) {
		log(logger, Level.FINE, data);
	}
	public static void fr(Logger logger, Object... data) {
		log(logger, Level.FINER, data);
	}
	public static void fs(Logger logger, Object... data) {
		log(logger, Level.FINEST, data);
	}

	public static void warning(Logger logger, Object... data) {
		log(logger, Level.WARNING, data);
	}
	public static void info(Logger logger, Object... data) {
		log(logger, Level.INFO, data);
	}
	public static void severe(Logger logger, Object... data) {
		log(logger, Level.SEVERE, data);
	}
	public static void config(Logger logger, Object... data) {
		log(logger, Level.CONFIG, data);
	}
	public static void fine(Logger logger, Object... data) {
		log(logger, Level.FINE, data);
	}
	public static void finer(Logger logger, Object... data) {
		log(logger, Level.FINER, data);
	}
	public static void finest(Logger logger, Object... data) {
		log(logger, Level.FINEST, data);
	}

	public static boolean isW(Logger logger) {
		return isLoggable(logger, Level.WARNING);
	}
	public static boolean isI(Logger logger) {
		return isLoggable(logger, Level.INFO);
	}
	public static boolean isS(Logger logger) {
		return isLoggable(logger, Level.SEVERE);
	}
	public static boolean isC(Logger logger) {
		return isLoggable(logger, Level.CONFIG);
	}
	public static boolean isFn(Logger logger) {
		return isLoggable(logger, Level.FINE);
	}
	public static boolean isFr(Logger logger) {
		return isLoggable(logger, Level.FINER);
	}
	public static boolean isFs(Logger logger) {
		return isLoggable(logger, Level.FINEST);
	}

	public static boolean isWarning(Logger logger) {
		return isLoggable(logger, Level.WARNING);
	}
	public static boolean isInfo(Logger logger) {
		return isLoggable(logger, Level.INFO);
	}
	public static boolean isSevere(Logger logger) {
		return isLoggable(logger, Level.SEVERE);
	}
	public static boolean isConfig(Logger logger) {
		return isLoggable(logger, Level.CONFIG);
	}
	public static boolean isFine(Logger logger) {
		return isLoggable(logger, Level.FINE);
	}
	public static boolean isFiner(Logger logger) {
		return isLoggable(logger, Level.FINER);
	}
	public static boolean isFinest(Logger logger) {
		return isLoggable(logger, Level.FINEST);
	}
}
