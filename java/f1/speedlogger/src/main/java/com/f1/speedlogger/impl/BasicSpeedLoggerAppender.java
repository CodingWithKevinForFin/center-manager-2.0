/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerAppender;
import com.f1.speedlogger.SpeedLoggerException;
import com.f1.speedlogger.StackTraceFormatter;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class BasicSpeedLoggerAppender implements SpeedLoggerAppender {

	final private long uid = SpeedLoggerUtils.generateUid();
	static final private char CODE_TEXT = 'T';
	static final private char CODE_CHARACTER = 'A';

	static final private char CODE_MSG = 'm';
	static final private char CODE_MSG_ONELINE = 's';
	static final private char CODE_DATETIME = 'd';
	static final private char CODE_THREAD = 't';
	static final private char CODE_ID = 'c';
	static final private char CODE_LEVEL = 'P';
	static final private char CODE_LEVEL_FULL = 'E';
	static final private char CODE_EXTRAS = 'D';
	static final private char CODE_EXTRAS_ONELINE = 'g';
	static final private char CODE_CLASSNAME = 'C';
	static final private char CODE_LINE = 'L';
	static final private char CODE_METHOD = 'M';
	static final private char CODE_NEWLINE = 'n';
	static final private char CODE_FILENAME = 'F';
	static final private char CODE_PERCENT = '%';

	static final Set<Character> STANDARD_CODES = new HashSet<Character>();
	private static final char[] CHAR_CR_LN = new char[] { SH.CHAR_CR, SH.CHAR_NEWLINE };
	public static final int DEFAULT_MAXLENGTH = 1024 * 1024;
	static {
		CH.addOrThrow(STANDARD_CODES, CODE_MSG);
		CH.addOrThrow(STANDARD_CODES, CODE_MSG_ONELINE);
		CH.addOrThrow(STANDARD_CODES, CODE_DATETIME);
		CH.addOrThrow(STANDARD_CODES, CODE_THREAD);
		CH.addOrThrow(STANDARD_CODES, CODE_ID);
		CH.addOrThrow(STANDARD_CODES, CODE_LEVEL);
		CH.addOrThrow(STANDARD_CODES, CODE_LEVEL_FULL);
		CH.addOrThrow(STANDARD_CODES, CODE_EXTRAS);
		CH.addOrThrow(STANDARD_CODES, CODE_EXTRAS_ONELINE);
		CH.addOrThrow(STANDARD_CODES, CODE_CLASSNAME);
		CH.addOrThrow(STANDARD_CODES, CODE_LINE);
		CH.addOrThrow(STANDARD_CODES, CODE_METHOD);
		CH.addOrThrow(STANDARD_CODES, CODE_NEWLINE);
		CH.addOrThrow(STANDARD_CODES, CODE_FILENAME);

	}

	final private String id;
	final private boolean requiresStackTrace;
	final private boolean requiresTimeMs;
	final private TimeZone timeZone;
	final private String format;
	final private char[] codes;
	final private Object[] options;
	final private StackTraceFormatter stackTraceFormatter;
	final private int maxToStringBytes;
	final private SpeedLoggerRedactor redacter;

	public BasicSpeedLoggerAppender(String id, String format, TimeZone timeZone, StackTraceFormatter stackTraceFormatter, int maxToStringBytes, String redactFind,
			String redactReplace) {
		this.maxToStringBytes = maxToStringBytes;
		this.id = id;
		this.format = format;
		this.timeZone = timeZone;
		this.stackTraceFormatter = stackTraceFormatter;
		SpeedLoggerRedactor t = null;
		if (SH.is(redactFind)) {
			try {
				t = new SpeedLoggerRedactor(redactFind, redactReplace);
			} catch (SpeedLoggerException e) {
				e.printStackTrace();
			}
		}
		this.redacter = t;

		boolean requiresStackTrace = false;
		boolean requiresTimeMs = false;

		List<Character> codes = new ArrayList<Character>();
		List<Object> options = new ArrayList<Object>();
		while (format.length() > 0) {
			int i = format.indexOf(CODE_PERCENT);
			if (i == -1)
				i = format.length();
			if (i > 0) {
				codes.add(i == 1 ? CODE_CHARACTER : CODE_TEXT);
				options.add(i == 1 ? format.charAt(0) : format.substring(0, i));
				format = format.substring(i);
			} else {
				String extras = null;
				char c = format.charAt(1);
				int end = 1;
				if (format.length() > 2 && format.charAt(2) == '{') {
					end = format.indexOf('}');
					if (end == -1)
						throw new RuntimeException("Missing closing } near " + format);
					extras = format.substring(3, end);
				}
				format = format.substring(end + 1);

				if (!STANDARD_CODES.contains(c))
					throw new RuntimeException("Invalid code: " + c);
				codes.add(c);
				switch (c) {
					case CODE_DATETIME:
						requiresTimeMs = true;
						options.add(new TimeFormatter(extras == null ? TimeFormatter.DEFAULT : extras, timeZone));
						break;
					case CODE_CLASSNAME:
					case CODE_FILENAME:
					case CODE_LINE:
					case CODE_METHOD:
						requiresStackTrace = true;
						options.add(null);
						break;
					default:
						options.add(null);
				}
			}
		}

		this.codes = new char[codes.size()];
		for (int i = 0; i < codes.size(); i++)
			this.codes[i] = codes.get(i);
		this.options = options.toArray(new Object[options.size()]);

		this.requiresStackTrace = requiresStackTrace;
		this.requiresTimeMs = requiresTimeMs;
	}

	@Override
	public void append(AppendableBuffer sink, Object message, int level, SpeedLogger logger, long timeMs, StackTraceElement ste) throws IOException {
		int start = sink.length();

		for (int i = 0; i < codes.length; i++) {
			switch (codes[i]) {
				case CODE_TEXT:
					sink.append((String) options[i]);
					break;
				case CODE_CHARACTER:
					sink.append((Character) options[i]);
					break;
				case CODE_MSG_ONELINE:
					if (message == null)
						sink.append("null");
					else
						toStringOneLine(message, sink);//TODO: remove lfcr
					break;
				case CODE_MSG:
					if (message == null)
						sink.append("null");
					else
						toString(message, sink);
					break;
				case CODE_DATETIME:
					((TimeFormatter) options[i]).append(sink, timeMs);
					break;
				case CODE_THREAD:
					sink.append(Thread.currentThread().getName());
					break;
				case CODE_ID:
					if (logger == null)
						sink.append("null");
					else
						sink.append(logger.getId());
					break;
				case CODE_LEVEL:
					sink.append(SpeedLoggerUtils.getLevelAsString(level));
					break;
				case CODE_LEVEL_FULL:
					sink.append(SpeedLoggerUtils.getFullLevelAsString(level));
					break;
				case CODE_EXTRAS:
					break;
				case CODE_EXTRAS_ONELINE:
					break;
				case CODE_LINE:
					sink.append(SH.toString(ste.getLineNumber()));
					break;
				case CODE_METHOD:
					sink.append(ste.getMethodName());
					break;
				case CODE_FILENAME:
					sink.append(ste.getFileName());
					break;
				case CODE_CLASSNAME:
					sink.append(ste.getClassName());
					break;
				case CODE_NEWLINE:
					sink.append(BasicStackTraceFormatter.NEWLINE);
					break;
				case CODE_PERCENT:
					sink.append(CODE_PERCENT);
					break;
				default:
					throw new RuntimeException("Unknown code: " + codes[i]);
			}
		}
		if (this.redacter != null)
			redacter.redact(start, sink);
	}

	private void toString(Object message, AppendableBuffer sink) throws IOException {
		if (message instanceof Object[]) {
			for (Object o : (Object[]) message)
				toString2(o, sink);
		} else
			toString2(message, sink);
	}
	private void toStringOneLine(Object message, AppendableBuffer sink) throws IOException {
		if (message instanceof Object[]) {
			for (Object o : (Object[]) message)
				toStringOneLine2(o, sink);
		} else
			toStringOneLine2(message, sink);
	}

	private void toString2(Object message, AppendableBuffer sink) throws IOException {
		if (message instanceof Throwable) {
			sink.append(BasicStackTraceFormatter.NEWLINE);
			stackTraceFormatter.printStackTrace(" ", "> ", (Throwable) message, sink);
		} else if (message instanceof CharSequence) {
			CharSequence r = (CharSequence) message;
			if (r != null && r.length() > maxToStringBytes) {
				sink.append(r, 0, maxToStringBytes);
				sink.append("< Speed Logger suppressing last ");
				sink.append(SH.toString(r.length() - maxToStringBytes));
				sink.append(" bytes of ");
				sink.append(SH.toString(r.length()));
				sink.append(" bytes message >");
			} else
				sink.append(r);
		} else {
			String r;
			try {
				r = OH.toString(message);
			} catch (Throwable t) {
				r = "<SPEEDLOGGINER REPORTING " + message.getClass().getName() + "::toString() threw " + t.getClass().getName() + ", see stderr for stacktrace>";
				System.err.println(r);
				t.printStackTrace();
			}
			if (r != null && r.length() > maxToStringBytes) {
				sink.append(r, 0, maxToStringBytes);
				sink.append("< Speed Logger suppressing last ");
				sink.append(SH.toString(r.length() - maxToStringBytes));
				sink.append(" bytes of ");
				sink.append(SH.toString(r.length()));
				sink.append(" bytes message >");
			} else
				sink.append(r);
		}
	}
	private void toStringOneLine2(Object message, AppendableBuffer sink) throws IOException {
		if (message instanceof Throwable) {
			StringBuilder sink2 = new StringBuilder();
			stackTraceFormatter.printStackTrace(" ", "> ", (Throwable) message, sink2);
			removeLfcr(sink2, sink);
		} else if (message instanceof CharSequence) {
			CharSequence r = (CharSequence) message;
			if (r != null && r.length() > maxToStringBytes) {
				sink.append(r, 0, maxToStringBytes);
				sink.append("<SpeedLogger suppressing last ");
				sink.append(SH.toString(r.length() - maxToStringBytes));
				sink.append(" bytes of ");
				sink.append(SH.toString(r.length()));
				sink.append(" bytes message>");
			} else
				removeLfcr(r, sink);
		} else {
			String r = OH.toString(message);
			if (r != null && r.length() > maxToStringBytes) {
				sink.append(r, 0, maxToStringBytes);
				sink.append("<SpeedLogger suppressing last ");
				sink.append(SH.toString(r.length() - maxToStringBytes));
				sink.append(" bytes of ");
				sink.append(SH.toString(r.length()));
				sink.append(" bytes message>");
			} else
				removeLfcr(r, sink);
		}
	}

	public static void removeLfcr(CharSequence text, AppendableBuffer sink) throws IOException {
		for (int i = 0, l = text.length(); i < l; i++) {
			char c = text.charAt(i);
			switch (c) {
				case '\n':
					sink.append("<SpeedLogger Suppressing \\n >");
					break;
				case '\r':
					if (i + 1 < l && text.charAt(i + 1) == '\n') {
						i++;
						sink.append("<\\r\\n>");
					} else
						sink.append("<\\r>");
					break;
				default:
					sink.append(c);
					break;

			}
		}
	}

	public static String replaceLfcr(String text) {
		// TODO: check for excaped \\r and \\n
		text = SH.replaceAll(text, "\\r", "\r");
		text = SH.replaceAll(text, "\\n", "\n");
		return text;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean getRequiresStackTrace() {
		return requiresStackTrace;
	}

	@Override
	public boolean getRequiresTimeMs() {
		return requiresTimeMs;
	}

	@Override
	public String toString() {
		return "SpeedLoggerAppender: " + id + " (format=" + format + ")";
	}

	@Override
	public long getUid() {
		return uid;
	}

	@Override
	public String describe() {
		return format;
	}

}
