/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.f1.speedlogger.SpeedLoggerAppender;
import com.f1.speedlogger.SpeedLoggerAppenderFactory;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.StackTraceFormatter;
import com.f1.utils.SH;

public class BasicSpeedLoggerAppenderFactory implements SpeedLoggerAppenderFactory {

	private static final String OPTION_REDACT_FIND = "redact.find";
	private static final String OPTION_REDACT_REPLACE = "redact.replace";
	private static final String OPTION_TIMEZONE = "timezone";
	private static final String OPTION_PATTERN = "pattern";
	private static final String OPTION_MAXBYTES = "maxbytes";
	private static final String OPTION_STACKTRACEFORMATTER = "stackTraceFormatter";
	public static final String ID = "BasicAppender";
	private static final Map<String, String> configuration = new HashMap<String, String>();
	private StackTraceFormatter defaultStackTraceFormatter;
	private SpeedLoggerManager manager;

	public BasicSpeedLoggerAppenderFactory(SpeedLoggerManager manager) {
		configuration.put(OPTION_TIMEZONE, TimeZone.getDefault().getID());
		configuration.put(OPTION_REDACT_FIND, "");
		configuration.put(OPTION_REDACT_REPLACE, "<REDACT>");
		configuration.put(OPTION_PATTERN, "%P %t %d{YMD h:m:s.S z} %c::%M:%m %D%n");
		configuration.put(OPTION_STACKTRACEFORMATTER, "default");
		configuration.put(OPTION_MAXBYTES, SH.toString(BasicSpeedLoggerAppender.DEFAULT_MAXLENGTH));
		this.manager = manager;
	}

	@Override
	public SpeedLoggerAppender createAppender(String id, Map<String, String> options) {
		StackTraceFormatter stackTraceFormatter;
		try {
			if ("default".equals(options.get(OPTION_STACKTRACEFORMATTER)))
				stackTraceFormatter = manager.getDefaultStackTraceFormatter();
			else
				stackTraceFormatter = (StackTraceFormatter) Class.forName(options.get(OPTION_STACKTRACEFORMATTER)).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("error getting stack trace formatter for appender: " + id, e);
		}
		final String pattern = options.get(OPTION_PATTERN);
		final String timezone = options.get(OPTION_TIMEZONE);
		final int maxBytes = Integer.parseInt(options.get(OPTION_MAXBYTES));
		final String redactFind = options.get(OPTION_REDACT_FIND);
		final String redactReplace = options.get(OPTION_REDACT_REPLACE);
		BasicSpeedLoggerAppender r = new BasicSpeedLoggerAppender(id, pattern, TimeZone.getTimeZone(timezone), stackTraceFormatter, maxBytes, redactFind, redactReplace);
		return r;
	}
	@Override
	public Map<String, String> getConfiguration() {
		return configuration;
	}

	@Override
	public String getId() {
		return ID;
	}

}
