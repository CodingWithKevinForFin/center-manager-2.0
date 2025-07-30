/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import com.f1.base.Row;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.Hasher;
import com.f1.utils.LocalToolkit;
import com.f1.utils.LongArrayList;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.impl.SimpleTextMatcher;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.RowFilter;

public class WebTableFilteredInFilter implements RowFilter {
	public static final TextMatcherFactory MATCHER_FACTORY = new TextMatcherFactory(true, false, false);

	private static final TextMatcher[] EMPTY = new TextMatcher[0];
	private WebColumn column;
	final private HasherSet<String> values = new HasherSet<String>((Hasher) CaseInsensitiveHasher.INSTANCE);
	private double[] numberValues = null;
	private long[] longValues;
	private TextMatcher[] matchers = EMPTY;
	private boolean isPattern = false;
	final private WebCellFormatter formatter;
	private String min = null;
	private String max = null;
	private boolean includeNull = false;
	private boolean patternIncludesNull = false;
	private boolean keep = true;
	private Double minNum;
	private Double maxNum;
	private boolean minInclusive;
	private boolean maxInclusive;
	private String singleValue;
	private StringBuilder buf = new StringBuilder();

	public WebTableFilteredInFilter(WebColumn column) {
		this.column = column;
		this.formatter = column.getCellFormatter();
	}

	@Override
	public boolean shouldKeep(Row row, LocalToolkit tk) {
		Object data = column.getData(row);
		if (data == null)
			return (includeNull || patternIncludesNull) == this.keep;
		if (this.singleValue != null)
			return CaseInsensitiveHasher.INSTANCE.areEqual(this.singleValue, formatData(data)) == keep;
		CharSequence svalue = null;
		if (!values.isEmpty()) {
			svalue = formatData(data);
			if (svalue == null)
				return (includeNull || patternIncludesNull) == this.keep;
			if (matchesValues(svalue))
				return keep;
		}
		if (minNum != null || maxNum != null || numberValues != null) {
			Comparable value = formatter.getOrdinalValue(data);
			if (value instanceof Number) {
				if (longValues != null && !OH.isFloat(value.getClass())) {
					long val = ((Number) value).longValue();
					for (long d : longValues)
						if (val == d)
							return keep;
					if (minNum != null || maxNum != null)
						if (minNum == null || (minInclusive ? val >= minNum : val > minNum))
							if (maxNum == null || (maxInclusive ? val <= maxNum : val < maxNum))
								return keep;
				} else {
					double val = ((Number) value).doubleValue();
					if (numberValues != null)
						for (Double d : numberValues)
							if (OH.eq(val, d, .00000000001))
								return keep;
					if (minNum != null || maxNum != null)
						if (minNum == null || (minInclusive ? val >= minNum : val > minNum))
							if (maxNum == null || (maxInclusive ? val <= maxNum : val < maxNum))
								return keep;
				}
				return !keep;
			}
		}
		if (min != null || max != null) {
			if (values.isEmpty()) {//we skipped last time, need to generate text value now
				svalue = formatData(data);
				if (svalue == null)
					return (includeNull || patternIncludesNull) == this.keep;
			}
			if (min == null || gt(svalue, min, minInclusive))
				if (max == null || lt(svalue, max, maxInclusive))
					return keep;
		}
		return !keep;

	}

	private CharSequence formatData(Object data) {
		if (data instanceof String) {
			return formatter.formatCellToText(data);
		} else {
			return formatter.formatCellToText(data, SH.clear(buf));
		}
	}

	private boolean gt(CharSequence value, String limit, boolean inclusive) {
		int len = Math.min(value.length(), limit.length());
		for (int i = 0; i < len; i++) {
			int n = compareChar(value.charAt(i), limit.charAt(i));
			if (n == -1 || n == -2)
				return false;
			else if (n == 1)
				return true;
		}
		return value.length() >= limit.length() && inclusive;
	}

	private boolean lt(CharSequence value, String limit, boolean inclusive) {
		int len = Math.min(value.length(), limit.length());
		for (int i = 0; i < len; i++) {
			int n = compareChar(value.charAt(i), limit.charAt(i));
			if (n == 1 || n == -2)
				return false;
			else if (n == -1)
				return true;
		}
		return value.length() >= limit.length() && inclusive;
	}

	private int compareChar(char l, char r) {
		if (l == r)
			return 0;
		if ((l >= '0' && l <= '9') != (r >= '0' && r <= '9'))
			return -2;

		l = Character.toUpperCase(l);
		r = Character.toUpperCase(r);
		if ((l >= 'A' && l <= 'Z') != (r >= 'A' && r <= 'Z'))
			return -2;
		return l == r ? 0 : (l < r ? -1 : 1);

	}

	private boolean matchesValues(CharSequence sb) {
		if (isPattern) {
			for (TextMatcher m : matchers)
				if (m.matches(sb))
					return true;
			return false;
		} else {
			return values.contains(sb);
		}
	}

	public Set<String> getValues() {
		return this.values;
	}

	public WebTableFilteredInFilter setValues(Set<String> filterIn, boolean isPattern) {
		this.values.clear();
		this.numberValues = null;
		this.longValues = null;
		this.isPattern = isPattern;
		if (filterIn != null)
			this.values.addAll(filterIn);
		if (isPattern) {
			if (this.matchers.length != this.values.size())
				this.matchers = new TextMatcher[this.values.size()];
			int n = 0;
			StringCharReader reader = new StringCharReader("");
			reader.setToStringIncludesLocation(true);
			StringBuilder sb = new StringBuilder();
			this.patternIncludesNull = false;
			for (String i : this.values) {
				reader.reset(i);
				this.matchers[n] = MATCHER_FACTORY.toMatcherNoThrow(reader, SH.clear(sb));
				if (!this.patternIncludesNull && this.matchers[n].matches(null))
					this.patternIncludesNull = true;
				n++;
			}
		} else {
			this.patternIncludesNull = false;
			this.matchers = EMPTY;
		}
		DoubleArrayList t = null;
		LongArrayList t2 = null;
		for (String i : this.values) {
			double n = toNumber(i);
			if (n == n) {
				if (t == null)
					t = new DoubleArrayList();
				t.add(n);
				Long n2 = toLong(i);
				if (n2 != null) {
					if (t2 == null)
						t2 = new LongArrayList();
					t2.add(n2);
				}
			}
		}
		if (t != null) {
			this.numberValues = t.toDoubleArray();
			if (t2 != null)
				this.longValues = t2.toLongArray();
		}
		updateSingleValue();
		return this;
	}

	private void updateSingleValue() {
		if (!isPattern && min == null && max == null && minNum == null && maxNum == null && numberValues == null && values.size() == 1)
			this.singleValue = CH.first(values);
		else
			this.singleValue = null;
	}

	public boolean isEmpty() {
		return this.values.isEmpty() && min == null && max == null && !includeNull;
	}

	public boolean getIncludeNull() {
		return includeNull;
	}

	public WebTableFilteredInFilter setIncludeNull(boolean includeNull) {
		this.includeNull = includeNull;
		return this;
	}

	public String getMax() {
		return max;
	}
	public WebTableFilteredInFilter setMax(boolean inclusive, String max) {
		this.maxInclusive = inclusive;
		if ("".equals(max)) {
			this.max = null;
			this.maxNum = null;
		} else if (hasPercent(max)) {
			this.max = max;
			this.maxNum = percentToDouble(max);
		} else {
			this.max = max;
			try {
				this.maxNum = max == null ? null : SH.parseDouble(SH.replaceAll(max, ',', ""));
			} catch (Exception e) {
				this.maxNum = null;
			}
		}
		updateSingleValue();
		return this;
	}
	private boolean hasPercent(String val) {
		String trimmed = SH.trim(val);
		return SH.endsWith(trimmed, '%') ? true : false;
	}
	private double percentToDouble(String val) {
		return SH.parseDouble(val, 0, SH.length(val) - 1) / 100;
	}
	public String getMin() {
		return min;
	}
	public WebTableFilteredInFilter setMin(boolean inclusive, String min) {
		this.minInclusive = inclusive;
		if ("".equals(min)) {
			this.min = null;
			this.minNum = null;
		} else if (hasPercent(min)) {
			this.min = min;
			this.minNum = percentToDouble(min);
		} else {
			this.min = min;
			try {
				this.minNum = min == null ? null : SH.parseDouble(SH.replaceAll(min, ',', ""));
			} catch (Exception e) {
				this.minNum = null;
			}
		}
		updateSingleValue();
		return this;
	}

	public WebTableFilteredInFilter setKeep(boolean keep) {
		this.keep = keep;
		return this;
	}

	public boolean getKeep() {
		return this.keep;
	}

	public boolean getIsPattern() {
		return this.isPattern;
	}

	public boolean isSimple() {
		return getSimpleValue() != null;
	}

	public String getSimpleValue() {
		if (getKeep()) {
			if (!getIsPattern()) {
				int size = CH.size(getValues());
				if (getMax() == null && getMin() == null && size > 0 && size < 5) {
					ArrayList<String> t = new ArrayList<String>(size);
					for (String s : getValues())
						t.add(TextMatcherFactory.escapeToPattern(s, false));
					return SH.join('|', t);
				} else if (size == 0) {
					if (getMax() != null && getMin() != null && maxInclusive && minInclusive) {
						return TextMatcherFactory.escapeToPattern(min, false) + " - " + TextMatcherFactory.escapeToPattern(max, false);

					} else if (getMax() != null && getMin() == null)
						return (maxInclusive ? "<= " : "< ") + TextMatcherFactory.escapeToPattern(max, false);
					if (getMin() != null && getMax() == null)
						return (minInclusive ? ">= " : "> ") + TextMatcherFactory.escapeToPattern(min, false);
				}
			} else if (getValues().size() == 1) {
				return CH.first(getValues());
			}
		}
		return null;
	}

	public void setFilteredExpression(String val) {
		final boolean isPattern;
		boolean minInclusive = false;
		boolean maxInclusive = false;
		String max = null;
		String min = null;
		String filter = null;
		if (SH.startsWith(val, ">=")) {
			min = TextMatcherFactory.unescapeFromPattern(SH.trim(SH.stripPrefix(val, ">=", true)));
			minInclusive = true;
			isPattern = false;
		} else if (SH.startsWith(val, ">")) {
			min = TextMatcherFactory.unescapeFromPattern(SH.trim(SH.stripPrefix(val, ">", true)));
			minInclusive = false;
			isPattern = false;
		} else if (SH.startsWith(val, "<=")) {
			max = TextMatcherFactory.unescapeFromPattern(SH.trim(SH.stripPrefix(val, "<=", true)));
			maxInclusive = true;
			isPattern = false;
		} else if (SH.startsWith(val, "<")) {
			max = TextMatcherFactory.unescapeFromPattern(SH.trim(SH.stripPrefix(val, "<", true)));
			maxInclusive = false;
			isPattern = false;
		} else {
			Tuple2<String, String> range = parseRangeExpression(val);
			if (range != null) {
				min = TextMatcherFactory.unescapeFromPattern(SH.trim(range.getA()));
				max = TextMatcherFactory.unescapeFromPattern(SH.trim(range.getB()));
				minInclusive = true;
				maxInclusive = true;
				isPattern = false;
			} else {
				if (val.startsWith("^") && !val.endsWith("*") && !val.endsWith("$"))
					val = val + '*';
				if (val.endsWith("$") && !val.startsWith("*") && !val.startsWith("^"))
					val = "*" + val;
				TextMatcher matcher = MATCHER_FACTORY.toMatcherNoThrow(new StringCharReader(val), new StringBuilder());
				if (matcher instanceof SimpleTextMatcher) {
					SimpleTextMatcher m = (SimpleTextMatcher) matcher;
					if (m.ignoreCase()) {
						filter = m.getText();
						this.setValues(CH.s(new LinkedHashSet<String>(), filter), false);
						this.setIncludeNull(false);
						return;
					}
				}
				filter = val;
				isPattern = true;
			}
		}
		this.setMin(minInclusive, min);
		this.setMax(maxInclusive, max);
		if (isPattern) {
			this.setValues(filter == null ? null : CH.s(new LinkedHashSet<String>(), filter), true);
			this.setIncludeNull(false);
		} else {
			this.setValues(null, false);
			this.setIncludeNull(false);
		}
	}

	private static Tuple2<String, String> parseRangeExpression(String val) {
		int i = val.indexOf('-', 1);
		if (i == -1 || i == val.length() - 1)
			return null;
		StringCharReader scr = new StringCharReader(val);
		scr.skip(StringCharReader.WHITE_SPACE);
		StringBuilder buf = new StringBuilder();
		final String min;
		if (scr.expectNoThrow('\'')) {
			scr.readUntil('\'', '\\', buf.append('\''));
			if (!scr.expectNoThrow('\''))
				return null;
			scr.skip(StringCharReader.WHITE_SPACE);
			if (!scr.expectNoThrow('-'))
				return null;
			min = SH.toStringAndClear(buf.append('\''));
		} else {
			buf.append(scr.readChar());
			scr.readUntil('-', buf);
			if (!scr.expectNoThrow('-'))
				return null;
			min = SH.trim(SH.toStringAndClear(buf));
		}
		scr.skip(StringCharReader.WHITE_SPACE);
		final String max;
		if (scr.expectNoThrow('\'')) {
			scr.readUntil('\'', '\\', buf.append('\''));
			if (!scr.expectNoThrow('\''))
				return null;
			scr.skip(StringCharReader.WHITE_SPACE);
			if (!scr.isEof())
				return null;
			max = SH.toStringAndClear(buf.append('\''));
		} else {
			scr.readUntil(CharReader.EOF, buf);
			max = SH.trim(SH.toStringAndClear(buf));
		}
		return new Tuple2<String, String>(min, max);
	}

	public boolean getMinInclusive() {
		return this.minInclusive;
	}
	public boolean getMaxInclusive() {
		return this.maxInclusive;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != WebTableFilteredInFilter.class)
			return false;
		WebTableFilteredInFilter other = (WebTableFilteredInFilter) obj;
		return isPattern == other.isPattern && //
				includeNull == other.includeNull && //
				patternIncludesNull == other.patternIncludesNull && //
				keep == other.keep && //
				minInclusive == other.minInclusive && //
				maxInclusive == other.maxInclusive && //
				OH.eq(min, other.min) && //
				OH.eq(max, other.max) && //
				OH.eq(minNum, other.minNum) && //
				OH.eq(maxNum, other.maxNum) && //
				AH.eq(matchers, other.matchers) && //
				OH.eq(column, other.column) && //
				OH.eq(formatter, other.formatter) && //
				OH.eq(values, other.values);
	}

	public static Long toLong(String cs) {
		int len = cs.length();
		if (len < 1)
			return null;
		char c = cs.charAt(0);
		boolean hasCommas = false;
		int hasDecimal = -1;
		if (c == '-' || c == '+') {
			if (len == 1)
				return null;
		} else if (c < '0' || c > '9')
			return null;
		for (int i = 1; i < len; i++) {
			c = cs.charAt(i);
			if (c == '.') {
				if (hasDecimal != -1)
					return null;
				hasDecimal = i;
			} else if (c == ',')
				hasCommas = true;
			else if (c < '0' || c > '9')
				return null;
			else if (hasDecimal != -1 && c != '0')
				return null;
		}

		if (hasDecimal != -1)
			cs = cs.substring(0, hasDecimal);
		if (hasCommas)
			cs = SH.replaceAll(cs, ',', "");
		try {
			return Long.parseLong(cs);
		} catch (Exception e) {
			return null;
		}
	}
	public static double toNumber(String cs) {
		int len = cs.length();
		if (len < 1)
			return Double.NaN;
		char c = cs.charAt(0);
		boolean hasDecimal = false;
		boolean hasCommas = false;
		if (c == '-' || c == '+') {
			if (len == 1)
				return Double.NaN;
		} else if (c < '0' || c > '9')
			return Double.NaN;
		for (int i = 1; i < len; i++) {
			c = cs.charAt(i);
			if (c == '.') {
				if (hasDecimal)
					return Double.NaN;
				hasDecimal = true;
			} else if (c == ',')
				hasCommas = true;
			else if (c < '0' || c > '9')
				return Double.NaN;
		}

		if (hasCommas)
			cs = SH.replaceAll(cs, ',', "");
		try {
			return Double.parseDouble(cs);
		} catch (Exception e) {
			return Double.NaN;
		}
	}

	public void setColumn(WebColumn column) {
		this.column = column;
	}
}
