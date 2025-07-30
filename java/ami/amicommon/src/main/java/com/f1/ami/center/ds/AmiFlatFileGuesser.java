package com.f1.ami.center.ds;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.CH;
import com.f1.utils.FastBufferedReader;
import com.f1.utils.IOH;
import com.f1.utils.IntValueMap;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiFlatFileGuesser {

	private static final char[] DELIMS = ";,|\t\u0001".toCharArray();
	private static final char[] EQUALS = "=".toCharArray();
	private static final char[] QUOTES = "'\"".toCharArray();
	private static final char[] ESCAPES = "'\"".toCharArray();

	final public char delim;
	final public char quote;
	final public char equals;
	final private char escape;
	final private boolean hasHeader;
	final public List<String> fields = new ArrayList<String>();
	final public com.f1.utils.structs.table.stack.BasicCalcTypes types = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	final private Map<String, String> mappings = new HashMap<String, String>();

	public static AmiFlatFileGuesser nw(File f, int numLines) throws IOException {
		List<String> lines = new ArrayList<String>();

		FastBufferedReader r = new FastBufferedReader(new FileReader(f));
		String line = r.readLine();
		int i = 0;
		while (i < numLines && line != null) {
			lines.add(line);
			line = r.readLine();
			i++;
		}
		if (r != null)
			r.close();
		return new AmiFlatFileGuesser(lines);
	}
	public AmiFlatFileGuesser(List<String> lines) {
		if (lines.size() >= 1) {
			IntValueMap<String> configOccurences = new IntValueMap<String>();
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				String key = evaluateLine(line);
				configOccurences.add(key, 1);
			}
			String most = configOccurences.getKeysSortedByCount(false).get(0);
			this.delim = most.charAt(0);
			this.equals = most.charAt(1);
			this.quote = most.charAt(2);
			this.escape = most.charAt(3);
			if (delim != 0) {
				if (equals == 0) {
					boolean allStrings = true;
					{//build header
						String header = lines.get(0);
						char delim = evaluateLine(header).charAt(0);
						String[] t = SH.split(delim, header);
						Set<String> visited = new HashSet<String>();
						for (int i = 0; i < t.length; i++) {
							String s = t[i];
							if (equals != 0 && s.indexOf(equals) > 0)
								s = SH.beforeFirst(s, equals);
							s = SH.replaceAll(s, '"', "");
							s = SH.replaceAll(s, '\'', "");
							s = s.trim();
							allStrings = allStrings && guessType(s) == String.class;
							if (allStrings) {
								try {
									s = SH.getNextId(AmiUtils.toValidVarName(s), visited, 2);
								} catch (Exception e) {
									s = SH.getNextId("col", visited);
								}
							}
							this.fields.add(s);
							visited.add(s);
						}
					} //read next lines to determine types
					this.hasHeader = allStrings;
					if (!allStrings) {
						for (int i = 0; i < this.fields.size(); i++)
							this.fields.set(i, "col" + (i + 1));
					}
					for (int ln = 1; ln < lines.size(); ln++) {
						String line = lines.get(ln);
						String[] t = SH.split(delim, line);
						for (int i = 0; i < t.length && i < this.fields.size(); i++) {
							String st = t[i].trim();
							if (st.length() == 0)
								continue;
							String key = this.fields.get(i);
							Class<?> type = guessType(st);
							this.types.putType(key, OH.getWidestIgnoreNull(type, this.types.getType(key)));
						}
					}
				} else {//key values
					this.hasHeader = false;
					Set<String> visited = new HashSet<String>();
					for (int ln = 0; ln < lines.size(); ln++) {
						String line = lines.get(ln);
						String[] t = SH.split(delim, line);
						for (int i = 0; i < t.length; i++) {
							String st = t[i].trim();
							int idx = st.indexOf(equals);
							if (idx == -1)
								continue;
							String origKey = st.substring(0, idx);
							origKey = origKey.trim();
							if (origKey.length() == 0)
								continue;
							String val = st.substring(idx + 1);
							String key = AmiUtils.toValidVarName(origKey);
							if (OH.ne(key, origKey))
								this.mappings.put(origKey, key);
							Class<?> type = guessType(val);
							if (visited.add(key))
								this.fields.add(key);
							this.types.putType(key, OH.getWidestIgnoreNull(type, this.types.getType(key)));
						}
					}
				}
			} else
				this.hasHeader = false;

		} else {
			this.hasHeader = false;
			this.delim = this.quote = this.equals = this.escape = 0;
		}
		for (String s : this.fields) {
			Class<?> existing = this.types.getType(s);
			if (existing == null || existing == Object.class)
				this.types.putType(s, String.class);
		}
	}
	private Class<?> guessType(String s) {
		if (SH.isnt(s))
			return null;
		s = s.trim();
		try {
			Object val = SH.parseConstant(s);
			if (val != null) {
				if (val instanceof Double || val instanceof Float)
					return Double.class;
				if (val instanceof Integer || val instanceof Long)
					return Long.class;
			}
		} catch (Exception e) {
		}
		return String.class;
	}
	private String evaluateLine(String line) {
		char delim = getMost(line, DELIMS);
		char equals = delim == 0 ? 0 : getMost(line, EQUALS);
		if (equals != 0 && SH.count(equals, line) <= SH.count(delim, line) - 1)
			equals = 0;
		char quote = getMost(line, QUOTES);
		char escape = getMost(line, ESCAPES);
		String key = "" + delim + equals + quote + escape;
		return key;
	}

	static private char getMost(String text, char[] options) {
		char bestChar = 0;
		int bestCount = 0;
		for (char c : options) {
			int cnt = SH.count(c, text);
			if (cnt > bestCount) {
				cnt = bestCount;
				bestChar = c;
			}
		}
		return bestChar;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("titles=").append(this.fields);
		sb.append("   types=").append(this.types);
		if (this.delim != 0)
			sb.append("   delim=").append(this.delim);
		if (this.quote != 0)
			sb.append("   quote=").append(this.quote);
		if (this.equals != 0)
			sb.append("   equals=").append(this.equals);
		return sb.toString();
	}

	public static void main(String a[]) throws IOException {
		List<String> lnes = CH.l(SH.splitLines(IOH.readText(new File("/tmp/pipedelim.csv"))));
		System.out.println(new AmiFlatFileGuesser(lnes));
	}
	public char getDelim() {
		return delim;
	}
	public char getQuote() {
		return quote;
	}
	public char getEquals() {
		return equals;
	}
	public char getEscape() {
		return escape;
	}
	public List<String> getFields() {
		return fields;
	}
	public int getLinesSkip() {
		return hasHeader ? 1 : 0;
	}
	public Class<?> getType(String field) {
		return this.types.getTypeCasterOrThrow(field);
	}
	public Map<String, String> getMappings() {
		return this.mappings;
	}
}
