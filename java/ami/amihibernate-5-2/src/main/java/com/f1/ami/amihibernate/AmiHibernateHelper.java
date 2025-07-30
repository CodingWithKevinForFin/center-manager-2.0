package com.f1.ami.amihibernate;

import java.util.LinkedHashSet;

import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;

public class AmiHibernateHelper {
	private static final StringBuilder sb = new StringBuilder();
	private static final StringBuilder sbParsed = new StringBuilder();
	private static final StringCharReader scr = new StringCharReader();

	private static final String DROP = "drop";
	private static final String CREATE = "create";
	private static final String INSERT = "insert";
	private static final String SELECT = "select";
	private static final String UPDATE = "update";
	private static final String DELETE = "delete";
	private static final String ALTER = "alter";
	private static final String AS = "as";
	private static final String FROM = "from";
	private static final String WHERE = "where";
	private static final String GROUP = "group";
	private static final String HAVING = "having";
	private static final String JOIN = "join";
	private static final String OUTER = "outer";
	private static final String INNER = "inner";
	private static final String FULL = "full";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	private static final String REPLACE_EQ_REGEX = "(?<=[a-zA-Z0-9_'\"`\\s])=(?=[a-zA-Z0-9_'\"`\\s])";
	private static final LinkedHashSet<String> KEYWORDS_AFTER_FROM = new LinkedHashSet<String>();
	static {
		KEYWORDS_AFTER_FROM.add(WHERE);
		KEYWORDS_AFTER_FROM.add(GROUP);
		KEYWORDS_AFTER_FROM.add(HAVING);
		KEYWORDS_AFTER_FROM.add(JOIN);
		KEYWORDS_AFTER_FROM.add(FULL);
		KEYWORDS_AFTER_FROM.add(OUTER);
		KEYWORDS_AFTER_FROM.add(INNER);
		KEYWORDS_AFTER_FROM.add(LEFT);
		KEYWORDS_AFTER_FROM.add(RIGHT);
	}
	private static final char SPACE = ' ';

	public static String prepareFrom(String query) {
		SH.clear(sb);
		SH.clear(sbParsed);
		scr.reset(query);

		scr.readUntilSequence(FROM, sbParsed);
		// From
		scr.setCaseInsensitive(true);
		boolean hasFrom = scr.expectSequenceNoThrow(FROM);
		scr.setCaseInsensitive(false);
		if (hasFrom) {
			sbParsed.append(FROM);

			// Whitespace
			scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
			sbParsed.append(SH.toStringAndClear(sb));

			// TableName
			scr.readUntilAny(StringCharReader.WHITE_SPACE, true, '\\', sb);
			String tableName = SH.toStringAndClear(sb);
			sbParsed.append(tableName);

			// Whitespace
			scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
			sbParsed.append(SH.toStringAndClear(sb));

			scr.setCaseInsensitive(true);
			boolean hasAs = scr.peakSequence(AS);
			scr.setCaseInsensitive(false);
			if (hasAs) {
				scr.setCaseInsensitive(true);
				scr.expectSequence(AS);
				scr.setCaseInsensitive(false);
				sbParsed.append(AS);

				// Whitespace
				scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
				sbParsed.append(SH.toStringAndClear(sb));

				// Table Alias
				scr.readUntilAny(StringCharReader.WHITE_SPACE, true, sb);
				sbParsed.append(SH.toStringAndClear(sb));
			} else {
				scr.mark();
				scr.readUntilAny(StringCharReader.WHITE_SPACE, true, sb);
				String nextWord = SH.toStringAndClear(sb);
				if (SH.is(nextWord) && !KEYWORDS_AFTER_FROM.contains(SH.toLowerCase(nextWord))) {
					// Table Alias
					sbParsed.append(AS).append(SPACE).append(nextWord);
				} else {
					scr.returnToMark();
				}

			}
			return SH.toStringAndClear(sbParsed);
		} else
			return query;
	}
}
