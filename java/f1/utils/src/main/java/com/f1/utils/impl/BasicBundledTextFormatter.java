package com.f1.utils.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.utils.AH;
import com.f1.utils.BundledTextFormatter;
import com.f1.utils.CH;
import com.f1.utils.CachedFile;
import com.f1.utils.CachedFile.Cache;
import com.f1.utils.CharReader;
import com.f1.utils.Formatter;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.formatter.AbstractFormatter;

public class BasicBundledTextFormatter extends AbstractFormatter implements BundledTextFormatter {

	private static final Logger log = Logger.getLogger(BasicBundledTextFormatter.class.getName());

	private static final char PART_DELIM = '|';

	final private LocaleFormatter formatter;
	final private CachedFile.Cache[] fileCaches;
	private Map<String, FormattedMessage> messages;
	private Map<String, FormattedMessage> messagesWithBang;
	final private long fileCheckFrequencyMs;
	final private File[] files;
	final private int defaultOptions;

	public BasicBundledTextFormatter(long fileCheckFrequencyMs, File[] files, LocaleFormatter formatter, int defaultOptions) {
		this.fileCheckFrequencyMs = fileCheckFrequencyMs;
		this.files = files;
		this.defaultOptions = defaultOptions;
		fileCaches = new CachedFile.Cache[files.length];
		for (int i = 0; i < files.length; i++)
			fileCaches[i] = new CachedFile(files[i], fileCheckFrequencyMs).getData();
		this.formatter = formatter;
		reload();
	}

	@Override
	public void checkForUpdate() {
		boolean needsReload = false;
		for (int i = 0; i < fileCaches.length; i++)
			if (fileCaches[i].isOld()) {
				fileCaches[i] = fileCaches[i].getUpdated();
				needsReload = true;
			}
		if (needsReload)
			reload();
	}

	public void reload() {
		final Map<String, FormattedMessage> m = new HashMap<String, FormattedMessage>();
		final Map<String, FormattedMessage> mwb = new HashMap<String, FormattedMessage>();
		for (int i = 0; i < fileCaches.length; i++) {
			final HashMap<String, String> properties = new HashMap<String, String>();
			if (fileCaches[i].exists()) {
				parseProperties(fileCaches[i], properties);
				applyLocale(properties);
			}
			for (Map.Entry<String, String> e : properties.entrySet()) {
				final FormattedMessage fm = new FormattedMessage(formatter, e.getKey(), e.getValue());
				m.put(e.getKey(), fm);
				mwb.put("!!" + e.getKey(), fm);
			}
		}
		messages = m;
		messagesWithBang = mwb;
	}

	private void applyLocale(HashMap<String, String> properties) {
		final String language = this.formatter.getLocale().getLanguage();
		final String isolanguage = this.formatter.getLocale().getISO3Language();
		for (final Map.Entry<String, String> e : new ArrayList<Map.Entry<String, String>>(properties.entrySet())) {
			final String key = e.getKey();
			final int delimLoc = key.indexOf(PART_DELIM);
			if (delimLoc == language.length() && SH.startsWithIgnoreCase(key, language, 0))
				properties.put(key.substring(delimLoc + 1), e.getValue());
			else if (delimLoc == isolanguage.length() && SH.startsWithIgnoreCase(key, isolanguage, 0))
				properties.put(key.substring(delimLoc + 1), e.getValue());
		}
	}

	static private void parseProperties(Cache file, HashMap<String, String> properties) {
		final String text = file.getText();
		final String[] lines = SH.splitLines(text);
		final StringCharReader scr = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		final StringBuilder sb = new StringBuilder();
		String key = null;
		int lineNum = 0;
		String line = null;
		try {
			for (String l : lines) {
				line = l;
				lineNum++;
				if ((SH.isnt(line) && key == null) || line.startsWith("#"))
					continue;
				scr.reset(line);
				if (key == null) {
					scr.readUntilSkipEscaped('=', '\\', SH.clear(sb));
					key = SH.toStringDecode(sb.toString());
					SH.clear(sb);
					scr.expect('=');
				}
				scr.readUntil(CharReader.EOF, sb);
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\\')
					sb.setLength(sb.length() - 1);
				else {
					final String value = SH.toStringDecode(sb.toString());
					properties.put(key, value);
					key = null;
				}

			}
			if (key != null) {
				line = sb.toString();
				final String value = SH.toStringDecode(sb.toString());
				properties.put(key, value);
				key = null;
			}
		} catch (Exception e) {
			LH.warning(log, "Error reading line ", lineNum, " of ", IOH.getFullPath(file.getFileCached().getFile()), ":", line);
		}
	}
	@Override
	public String formatBundledTextFromMap(String propertyKey, Map<String, Object> arguments, int options) {
		StringBuilder sb = new StringBuilder();
		return formatBundledTextFromMap(propertyKey, arguments, options, sb) ? sb.toString() : null;
	}

	@Override
	public boolean formatBundledTextFromMap(String propertyKey, Map<String, Object> arguments, int options, StringBuilder sb) {
		boolean kswb = MH.areAnyBitsSet(options, OPTION_KEYS_START_WITH_BANGBANG);
		if (kswb && (propertyKey == null || propertyKey.length() < 2 || propertyKey.charAt(0) != '!' || propertyKey.charAt(1) != '!')) {
			sb.append(propertyKey);
			return true;
		}

		checkForUpdate();
		FormattedMessage formattedMessage = (kswb ? messagesWithBang : messages).get(propertyKey);
		if (formattedMessage == null) {
			if (MH.areAnyBitsSet(options, OPTION_ON_KEY_MISSING_RETURN_NULL))
				return false;
			else if (MH.areAnyBitsSet(options, OPTION_ON_KEY_MISSING_DEBUG)) {
				sb.append("^>");
				if (kswb)
					sb.append(propertyKey, 2, propertyKey.length());
				else
					sb.append(propertyKey);
				sb.append("<^");
				return true;
			} else {
				if (kswb)
					sb.append(propertyKey, 2, propertyKey.length());
				else
					sb.append(propertyKey);
				if (MH.areAnyBitsSet(options, OPTION_ON_KEY_MISSING_PRINT_DETAILED) && CH.isntEmpty(arguments)) {
					sb.append(" [");
					SH.joinMap(',', '=', arguments, sb);
					sb.append(" ]");
				}
				return true;
			}
		}
		formattedMessage.format(formatter, arguments, sb, options);
		return true;
	}
	@Override
	public String formatBundledText(String propertyKey, Object... arguments) {
		if (AH.isEmpty(arguments))
			return formatBundledTextFromMap(propertyKey, Collections.EMPTY_MAP, defaultOptions);
		Map<String, Object> values = new HashMap<String, Object>(arguments.length);
		for (int i = 0; i < arguments.length; i++)
			values.put(SH.toString(i), arguments[i]);
		return formatBundledTextFromMap(propertyKey, values, defaultOptions);
	}

	private static class FormattedMessage {
		private static final int[] COMMA_SPACE_OR_CLOSEBRAKET = new int[] { ',', '}', ' ' };
		private final Part parts[];
		private String propertyKey;

		public FormattedMessage(LocaleFormatter formatter, String propertyKey, String expression) {
			this.propertyKey = propertyKey;
			CharReader reader = new StringCharReader(expression);
			final List<Part> parts = new ArrayList<Part>();
			while (reader.peakOrEof() != CharReader.EOF) {
				StringBuilder sb = new StringBuilder();
				reader.readUntil('{', '\\', sb);
				if (sb.length() > 0) {
					parts.add(new ConstPart(sb.toString()));
					sb.setLength(0);
				}
				if (reader.peakOrEof() == '{') {
					final String type, subPattern;
					String key;
					reader.expect('{');
					reader.skip(' ');
					if (' ' == reader.readUntilAny(COMMA_SPACE_OR_CLOSEBRAKET, sb))
						reader.skip(' ');
					key = sb.toString();
					sb.setLength(0);
					if (reader.peak() == ',') {
						reader.expect(',');
						reader.skip(' ');
						if (' ' == reader.readUntilAny(COMMA_SPACE_OR_CLOSEBRAKET, sb))
							reader.skip(' ');
						type = sb.toString();
						sb.setLength(0);
						if (reader.peak() == ',') {
							reader.expect(',');
							reader.skip(' ');
							if (' ' == reader.readUntilAny(COMMA_SPACE_OR_CLOSEBRAKET, sb))
								reader.skip(' ');
							subPattern = sb.toString();
							sb.setLength(0);
						} else
							subPattern = "0";
					} else {
						type = "text";
						subPattern = "0";
					}
					reader.expect('}');
					if ("text".equals(type)) {
						parts.add(new TextPart(key));
					} else if ("date".equals(type)) {
						parts.add(new FormatterPart(key, formatter.getDateFormatter(Integer.parseInt(subPattern))));
					} else if ("number".equals(type)) {
						parts.add(new FormatterPart(key, formatter.getNumberFormatter(Integer.parseInt(subPattern))));
					} else if ("price".equals(type)) {
						parts.add(new FormatterPart(key, formatter.getPriceFormatter(Integer.parseInt(subPattern))));
					} else if ("percent".equals(type)) {
						parts.add(new FormatterPart(key, formatter.getPercentFormatter(Integer.parseInt(subPattern))));
					} else if ("custom".equals(type)) {
						parts.add(new FormatterPart(key, formatter.getCustomFormatter(subPattern)));
					} else if ("nested".equals(type)) {
						parts.add(new NestedFormatterPart(key, subPattern));
					} else
						throw new RuntimeException("unknown formatter type: " + type);
				}
			}
			this.parts = parts.toArray(new Part[parts.size()]);
		}

		public void format(LocaleFormatter formatter, Map<String, Object> arguments, StringBuilder sb, int options) {
			for (int i = 0; i < parts.length; i++) {
				Part part = parts[i];
				try {
					part.format(formatter, arguments, sb, options);
				} catch (RuntimeException e) {
					if (log.isLoggable(Level.WARNING)) {
						if (MH.areAnyBitsSet(options, OPTION_LOG_EXCEPTIONS)) {
							LH.warning(log, "Error while formatting: ", propertyKey, " at part ", part, "  exception: ", e);
						} else if (MH.areAnyBitsSet(options, OPTION_LOG_STACKTRACE)) {
							LH.warning(log, "Error while formatting: ", propertyKey, e);
						}
					}
					if (MH.areAnyBitsSet(options, OPTION_ON_ARG_ERROR_EMBED_MESSAGE)) {
						sb.append(e.getClass()).append(':').append(e.getMessage());
					}
				}
			}
		}

	}

	private static interface Part {
		public void format(LocaleFormatter formatter, Map<String, Object> arguments, StringBuilder sb, int options);
	}

	private static class ConstPart implements Part {
		private final String s;

		public ConstPart(String s) {
			this.s = s;
		}

		@Override
		public void format(LocaleFormatter formatter, Map<String, Object> arguments, StringBuilder sb, int options) {
			sb.append(s);
		}

		@Override
		public String toString() {
			return s;
		}
	}

	private static class TextPart implements Part {
		private final String offset;

		public TextPart(String offset) {
			this.offset = offset;
		}

		@Override
		public void format(LocaleFormatter formatter, Map<String, Object> arguments, StringBuilder sb, int options) {
			final Object value = extract(arguments, offset);
			if (value != null)
				sb.append(value);
			else if (MH.areAnyBitsSet(options, OPTION_ON_ARG_MISSING_PRINT_KEY))
				sb.append(offset);
			else if (MH.areAnyBitsSet(options, OPTION_ON_ARG_MISSING_PRINT_NULL))
				sb.append(RH.NULL);
		}

		@Override
		public String toString() {
			return offset;
		}

	}

	private static class FormatterPart implements Part {
		private final String offset;
		private Formatter formatter;

		public FormatterPart(String offset, Formatter formatter) {
			this.offset = offset;
			this.formatter = formatter;

		}

		@Override
		public void format(LocaleFormatter formatter, Map<String, Object> arguments, StringBuilder sb, int options) {
			final Object value = extract(arguments, offset);
			if (value != null)
				this.formatter.format(value, sb);
			else if (MH.areAnyBitsSet(options, OPTION_ON_ARG_MISSING_PRINT_KEY))
				sb.append(offset);
			else if (MH.areAnyBitsSet(options, OPTION_ON_ARG_MISSING_PRINT_NULL))
				sb.append(RH.NULL);
		}

		@Override
		public String toString() {
			return offset;
		}
	}

	private static class NestedFormatterPart implements Part {
		private String key;
		private String subPattern;

		public NestedFormatterPart(String key, String subPattern) {
			this.key = key;
			this.subPattern = SH.is(subPattern) ? subPattern : null;

		}

		@Override
		public void format(LocaleFormatter formatter, Map<String, Object> arguments, StringBuilder sb, int options) {
			if (subPattern != null) {

				final Object value = extract(arguments, subPattern);
				if (value != null)
					sb.append(formatter.getBundledTextFormatter().formatBundledTextFromMap(key, arguments, options));
				else if (MH.areAnyBitsSet(options, OPTION_ON_ARG_MISSING_PRINT_KEY))
					sb.append(subPattern);
				else if (MH.areAnyBitsSet(options, OPTION_ON_ARG_MISSING_PRINT_NULL))
					sb.append(RH.NULL);
			} else
				sb.append(formatter.getBundledTextFormatter().formatBundledTextFromMap(key, arguments, options));
		}

		@Override
		public String toString() {
			return key;
		}
	}

	private static Object extract(Map<String, Object> arguments, String offset) {
		return RootAssister.INSTANCE.getNestedValue(arguments, offset, false);
	}

	@Override
	public boolean canParse(String text) {
		return false;
	}

	@Override
	public Object parse(String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void format(Object value, StringBuilder sb) {
		sb.append(this.formatBundledText(OH.toString(value), OH.EMPTY_OBJECT_ARRAY));
	}
	@Override
	public String format(Object value) {
		String t = this.formatBundledText(OH.toString(value), OH.EMPTY_OBJECT_ARRAY);
		return t;
	}

	@Override
	public Formatter clone() {
		return new BasicBundledTextFormatter(fileCheckFrequencyMs, files, formatter, defaultOptions);
	}
}
