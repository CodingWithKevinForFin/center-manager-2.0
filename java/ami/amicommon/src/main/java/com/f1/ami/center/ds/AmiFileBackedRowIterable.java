package com.f1.ami.center.ds;

import java.io.Reader;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Generator;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CharSubSequence;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CharSequenceHasher;
import com.f1.utils.structs.table.online.FileBackedRowIterable;
import com.f1.utils.structs.table.online.OnlineTable;

public class AmiFileBackedRowIterable extends FileBackedRowIterable {

	public static final char NO_CHAR = (char) -1;
	final private Matcher filterInMatcherMatcher;
	final private Matcher filterOutMatcherMatcher;
	final private int skipLines;
	final private CharSubSequence tmp = new CharSubSequence();
	final private StringBuilder tmp2 = new StringBuilder();
	final private String delim;
	final private char quote;
	final private char escape;
	final private char associator;
	final private OnlineTable table;
	final private HasherMap<CharSequence, Integer> columnsPos = new HasherMap<CharSequence, Integer>(CharSequenceHasher.INSTANCE);
	final private Extractor[] extractors;
	final private String linenumColumnName;
	final private Column linenumColumn;
	final private String filterInLiteral;
	final private String filterOutLiteral;
	final private boolean hasFilters;
	final private int colCount;
	private boolean conflateDelims;

	private int getColumnPosition(StringBuilder name) {
		SH.trimInplace(name);
		Integer r = columnsPos.get(name);
		return r == null ? -1 : r.intValue();
	}
	public AmiFileBackedRowIterable(OnlineTable table, Generator<Reader> input, int skipLinesCount, String filterInPattern, String filterOutPattern, String delim,
			boolean conflateDelims, char quote, char escape, char associator, String[] extractors, String linenumColumnName, Map<String, String> mapping) {
		super(table, input);
		this.filterInMatcherMatcher = toMatcher(filterInPattern);
		this.filterInLiteral = toLiteralOrNullIfRegex(filterInPattern);
		this.filterOutLiteral = toLiteralOrNullIfRegex(filterOutPattern);
		this.filterOutMatcherMatcher = toMatcher(filterOutPattern);
		this.hasFilters = this.filterInLiteral != null || this.filterInMatcherMatcher != null || this.filterOutLiteral != null || this.filterOutMatcherMatcher != null;
		this.skipLines = skipLinesCount;
		this.linenumColumnName = linenumColumnName;
		this.table = table;
		this.conflateDelims = conflateDelims;
		this.linenumColumn = this.linenumColumnName == null ? null : this.table.getColumn(this.linenumColumnName);
		for (int i = 0; i < table.getColumnsCount(); i++)
			columnsPos.put((String) table.getColumnAt(i).getId(), i);
		this.delim = delim;
		this.quote = quote;
		this.escape = escape;
		this.associator = associator;
		this.extractors = new Extractor[extractors.length];
		for (int i = 0; i < extractors.length; i++) {
			this.extractors[i] = toExtractor(extractors[i]);
		}
		if (mapping != null) {
			for (Map.Entry<String, String> e : mapping.entrySet()) {
				Integer pos = CH.getOrThrow(columnsPos, e.getValue());
				CH.putOrThrow(columnsPos, e.getKey(), pos);
			}
		}
		this.colCount = table.getColumnsCount();
	}

	private Extractor toExtractor(String string) {
		int split = string.indexOf('=');
		if (split == -1)
			throw new RuntimeException("pattern should be in format colname1,colname2,colname3=pattern");
		String[] names = SH.trimStrings(SH.split(',', string.substring(0, split)));
		for (int i = 0; i < names.length; i++)
			names[i] = SH.afterLast(names[i], ' ', names[i]);
		String pattern = string.substring(split + 1);
		if ("(.*)".equals(pattern) && names.length == 1)
			return new FullLineExtractor(names[0], table);
		else
			return new PatternExtractor(names, pattern, table);
	}
	@Override
	protected boolean resetRow(int linenum, Row row, CharSequence sb) {
		if (linenum != 0 && linenum % 1000000 == 0)
			System.out.println(new Date() + " large file being read, at line " + linenum);
		CharSequence line = sb;
		if (skipLines > linenum)
			return false;
		if (hasFilters) {
			if (filterOutLiteral != null) {
				if (((StringBuilder) sb).indexOf(filterOutLiteral) != -1)
					return false;
			} else if (filterOutMatcherMatcher != null) {
				filterOutMatcherMatcher.reset(line);
				if (filterOutMatcherMatcher.matches())
					return false;
			}
			if (filterInLiteral != null) {
				if (((StringBuilder) sb).indexOf(filterInLiteral) == -1)
					return false;
			} else if (filterInMatcherMatcher != null) {
				filterInMatcherMatcher.reset(line);
				if (!filterInMatcherMatcher.matches())
					return false;
				if (filterInMatcherMatcher.groupCount() > 0) {
					line = tmp.reset(line, filterInMatcherMatcher.start(1), filterInMatcherMatcher.end(1));
				}
			}
		}

		AH.fill(row.getValues(), null);
		if (this.linenumColumn != null)
			row.putAt(this.linenumColumn.getLocation(), this.linenumColumn.getTypeCaster().cast(linenum + 1, false, false));
		if (delim != null) {
			int col = associator == (char) -1 ? (this.linenumColumn == null ? 0 : 1) : -1;
			boolean inQuote = false;
			for (int i = 0, l = line.length(); i < l; i++) {
				char c = line.charAt(i);
				if (inQuote && c == quote) {
					col = populate(row, col, tmp2);
					if (col == colCount)
						return true;
					while (i + 1 < l && !SH.equalsAt(line, i, delim))
						i++;
					inQuote = false;
				} else if (!inQuote && SH.equalsAt(line, i, delim)) {
					if (!conflateDelims || tmp2.length() > 0) {
						col = populate(row, col, tmp2);
						if (col == colCount)
							return true;
					}
				} else if (c == quote && tmp2.length() == 0) {
					inQuote = true;
				} else if (c == escape) {
					i++;
					if (i < l) {
						char c2 = SH.toSpecial(line.charAt(i));
						if (c2 == SH.CHAR_NOT_SPECIAL)
							c2 = line.charAt(i);
						tmp2.append(c2);
					}
				} else if (c == associator) {
					col = getColumnPosition(tmp2);
					tmp2.setLength(0);
				} else
					tmp2.append(c);
			}
			populate(row, col, tmp2);
		}

		for (Extractor i : extractors)
			i.extract(line, row);
		return true;
	}
	private int populate(Row row, int col, StringBuilder tmp2) {
		if (col != -1) {
			Column column = table.getColumnAt(col);
			row.putAt(col, column.getTypeCaster().cast(tmp2, false, false));
			tmp2.setLength(0);
			if (associator == (char) -1)
				return col + 1;
			else
				return -1;
		}
		tmp2.setLength(0);
		return col;
	}

	public static interface Extractor {
		public void extract(CharSequence line, Row row);
	}

	public static class PatternExtractor implements Extractor {
		final public int[] positions;
		final public Matcher matcher;
		final private Table table;
		private CharSubSequence tmp = new CharSubSequence();

		public PatternExtractor(String[] names, String pattern, Table table) {
			this.table = table;
			positions = new int[names.length];
			for (int i = 0; i < names.length; i++)
				positions[i] = table.getColumn(names[i]).getLocation();
			this.matcher = toMatcher(pattern);
		}

		public void extract(CharSequence line, Row row) {
			matcher.reset(line);
			if (matcher.matches()) {
				Caster<?> caster;
				for (int i = 1, l = Math.min(positions.length + 1, matcher.groupCount() + 1); i < l; i++) {
					int pos = positions[i - 1];
					caster = table.getColumnAt(pos).getTypeCaster();
					tmp.reset(line, matcher.start(i), matcher.end(i));
					row.putAt(pos, caster.cast(tmp, false, false));
				}
			}
		}
	}

	public class FullLineExtractor implements Extractor {

		private int position;
		private OnlineTable table;

		public FullLineExtractor(String name, OnlineTable table) {
			this.position = table.getColumn(name).getLocation();
			this.table = table;
		}

		@Override
		public void extract(CharSequence line, Row row) {
			row.putAt(position, line.toString());
		}
	}

	public static Matcher toMatcher(String pattern) {
		if (pattern == null)
			return null;
		final boolean s = pattern.startsWith("^");
		if (s == pattern.endsWith("$"))
			pattern = s ? pattern : (".*?" + pattern + ".*");
		else
			pattern = s ? (pattern + ".*") : (".*?" + pattern);
		return Pattern.compile(pattern, Pattern.DOTALL | Pattern.UNICODE_CASE).matcher("");
	}

	public static String toLiteralOrNullIfRegex(String s) {
		if (s == null)
			return null;
		BasicCharMatcher cm = new BasicCharMatcher(".[]{}()*+\\-?^$|", false);
		if (s.indexOf('\\') == -1) {
			for (int i = 0; i < s.length(); i++) {
				if (cm.matches(s.charAt(i)))
					return null;
			}
			return s;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0, l = s.length(); i < l;) {
			if (cm.matches(s.charAt(i)))
				return null;
			i = SH.decodeChar(s, i, sb);
		}
		return sb.toString();

	}
}
