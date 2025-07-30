/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.flogger.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import com.f1.utils.FastTimeFormatter;
import com.f1.utils.SH;
import com.f1.utils.flogger.FloggerFormatter;

public class BasicFloggerFormatter implements FloggerFormatter {

	static final private char CODE_TEXT = 'T';
	static final private char CODE_CHARACTER = 'A';

	static final private char CODE_MSG = 'm';
	static final private char CODE_DATETIME = 'd';
	static final private char CODE_THREAD = 't';
	static final private char CODE_ID = 'c';
	static final private char CODE_LEVEL = 'P';
	static final private char CODE_EXTRAS = 'D';
	static final private char CODE_CLASSNAME = 'C';
	static final private char CODE_LINE = 'L';
	static final private char CODE_METHOD = 'M';
	static final private char CODE_NEWLINE = 'n';
	static final private char CODE_NEWLINE_CONDITIONAL = 'N';
	static final private char CODE_FILENAME = 'F';
	static final private char CODE_TRANSACTIONID = 'X';
	static final private char CODE_PERCENT = '%';

	static final public String DEFAULT_PREFIX = "## Flogger[%X] ";
	static final public String DEFAULT_FORMAT = "%P %d [%t] %C::%M:%m%D{ }%N";

	static final Set<Character> STANDARD_CODES = new HashSet<Character>();
	static {
		STANDARD_CODES.add(CODE_MSG);
		STANDARD_CODES.add(CODE_DATETIME);
		STANDARD_CODES.add(CODE_THREAD);
		STANDARD_CODES.add(CODE_ID);
		STANDARD_CODES.add(CODE_LEVEL);
		STANDARD_CODES.add(CODE_EXTRAS);
		STANDARD_CODES.add(CODE_CLASSNAME);
		STANDARD_CODES.add(CODE_LINE);
		STANDARD_CODES.add(CODE_METHOD);
		STANDARD_CODES.add(CODE_NEWLINE);
		STANDARD_CODES.add(CODE_NEWLINE_CONDITIONAL);
		STANDARD_CODES.add(CODE_FILENAME);
		STANDARD_CODES.add(CODE_NEWLINE);
		STANDARD_CODES.add(CODE_TRANSACTIONID);
	}

	final private TimeZone timeZone;
	final private String format;
	private String prefix;
	private CodesAndOptions formatCodes;
	private CodesAndOptions prefixCodes;

	public BasicFloggerFormatter() {
		this(DEFAULT_PREFIX, DEFAULT_FORMAT, TimeZone.getDefault());
	}

	public BasicFloggerFormatter(String prefix, String format, TimeZone timeZone) {
		this.format = format;
		this.prefix = prefix;
		this.timeZone = timeZone;
		formatCodes = parseCodes(this.format);
		prefixCodes = parseCodes(this.prefix);
	}

	private CodesAndOptions parseCodes(String format) {
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
					case CODE_DATETIME :
						options.add(new FastTimeFormatter(extras == null ? FastTimeFormatter.DEFAULT : extras, timeZone));
						break;
					case CODE_CLASSNAME :
					case CODE_FILENAME :
					case CODE_LINE :
					case CODE_METHOD :
						options.add(null);
						break;
					default :
						options.add(extras);
				}
			}
		}

		char[] codesArray = new char[codes.size()];
		for (int i = 0; i < codes.size(); i++)
			codesArray[i] = codes.get(i);
		Object[] optionsArray = options.toArray(new Object[options.size()]);
		return new CodesAndOptions(codesArray, optionsArray);
	}

	private static class CodesAndOptions {

		final public char[] codes;
		final public Object[] options;

		public CodesAndOptions(char[] codes, Object[] options) {
			this.codes = codes;
			this.options = options;
		}

	}

	@Override
	public void append(StringBuilder sink, String loggerId, String txnId, Object message, int level, Object extras, long timeMs, StackTraceElement ste,
			Thread thread) throws IOException {
		int start = sink.length();
		append("", prefixCodes.codes, prefixCodes.options, sink, loggerId, txnId, message, level, extras, timeMs, ste, thread);
		append(sink.substring(start), formatCodes.codes, formatCodes.options, sink, loggerId, txnId, message, level, extras, timeMs, ste, thread);
	}

	private void append(String prefix, char[] codes, Object[] options, StringBuilder sink, String loggerId, String txnId, Object message, int level,
			Object extras, long timeMs, StackTraceElement ste, Thread thread) throws IOException {

		for (int i = 0; i < codes.length; i++) {
			switch (codes[i]) {
				case CODE_TEXT :
					sink.append((String) options[i]);
					break;
				case CODE_CHARACTER :
					sink.append(options[i]);
					break;
				case CODE_MSG :
					sink.append(message != null ? SH.prefixLines(message.toString(), prefix, false) : "null");
					break;
				case CODE_DATETIME :
					((FastTimeFormatter) options[i]).append(sink, timeMs);
					break;
				case CODE_THREAD :
					sink.append(thread.getName());
					break;
				case CODE_ID :
					sink.append(loggerId);
					break;
				case CODE_LEVEL :
					sink.append(FloggerUtils.getLevelAsString(level));
					break;
				case CODE_EXTRAS :
					if (extras != null && options[i] != null)
						sink.append(options[i]);
					if (extras instanceof Throwable)
						SH.printStackTrace(prefix, "> ", (Throwable) extras, sink);
					else if (extras != null)
						sink.append(extras.toString());
					break;
				case CODE_LINE :
					sink.append(SH.toString(ste.getLineNumber()));
					break;
				case CODE_METHOD :
					sink.append(ste.getMethodName());
					break;
				case CODE_FILENAME :
					sink.append(ste.getFileName());
					break;
				case CODE_CLASSNAME :
					sink.append(ste.getClassName());
					break;
				case CODE_NEWLINE :
					sink.append(SH.NEWLINE);
					break;
				case CODE_NEWLINE_CONDITIONAL :
					if (sink.length() > 0) {
						switch (sink.charAt(sink.length() - 1)) {
							case '\n' :
							case '\r' :
								break;
							default :
								sink.append(SH.NEWLINE);
						}
					}
					break;
				case CODE_PERCENT :
					sink.append(CODE_PERCENT);
					break;
				case CODE_TRANSACTIONID :
					sink.append(txnId);
					break;
				default :
					throw new RuntimeException("Unknown code: " + codes[i]);
			}
		}
	}
}
