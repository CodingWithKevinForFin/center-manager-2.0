/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.db;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MultiMap;

/**
 * 
 * Advanced statement executor. The following syntax can be parsed out of the sql:
 * 
 * <pre>
 *  ?                A standard parameter (identified by numerical index)
 *  ?{somename}      A standard named parameter (identified by numerical index or by name)
 *  ??               An inline parameter (identified by numerical index)
 *  ??{somename}     An inline named parameter (identified by numerical index or by name)
 *  ???              Will be replaced with a single ? (used for escaping)
 * </pre>
 * 
 * Inline parameters, unlike conventional sql parameters will use "inline" substitution, meaning that the ??{...} or ?? will be <i>physically replaced</i> by the supplied
 * parameter's value.<BR>
 * Also, if the value of a parameter is a collection, then the ? will be expanded by the number of elements in the array, then the array will be expanded so that each element
 * matches an element. This is useful for IN() clauses.
 * 
 * 
 * @author rcooke
 * 
 */
public class NamedParamDbStatementPreparer implements DbStatementPreparer {

	private static final Object NULL = new Object();
	final private MultiMap<String, Integer, List<Integer>> namedPositions = new BasicMultiMap.List<String, Integer>();
	final private int paramsCount;

	@Override
	public List<PreparedStatement> prepareStatement(Map<Object, Object> params, Connection connection, DbStatementFactory psFactory) throws Exception {
		final Object[] paramList = new Object[paramsCount];
		if (paramsCount > 0 || CH.isntEmpty(params)) {
			int remaining = paramsCount;
			for (Map.Entry<Object, Object> e : params.entrySet()) {
				final Object k = e.getKey();
				final Object v = OH.noNull(e.getValue(), NULL);
				if (k instanceof String) {
					for (int location : CH.getOrThrow(namedPositions, (String) k, "invalid named param(does not exist in sql template)")) {
						if (paramList[location] != null)
							throw new RuntimeException("multiple values at the same location: " + location);
						paramList[location] = v;
						remaining--;
					}
				} else if (k instanceof Integer) {
					int location = (Integer) k;
					if (paramList[location] != null)
						throw new RuntimeException("multiple values at the same location: " + location);
					paramList[location] = v;
					remaining--;
				} else
					throw new RuntimeException("keys must either be integers (positioning) or strings (by name): " + k);
			}
			if (remaining != 0) {
				for (Entry<String, List<Integer>> e : namedPositions.entrySet())
					for (int i : e.getValue())
						if (paramList[i] == null)
							throw new RuntimeException("param referenced in sql not in map: " + e.getKey() + "  (at " + i + ")");
				for (int i = 0; i < paramList.length; i++)
					if (paramList[i] == null)
						throw new RuntimeException("param referenced in sql not at index: " + i);
				throw new IllegalStateException();
			}
		}
		int position = 0;
		final List<Object> objects = new ArrayList<Object>(paramsCount);
		final StringBuilder sb = new StringBuilder();
		for (int i = 0, l = parts.size(); i < l;) {
			sb.append(parts.get(i++));
			if (i < parts.size()) {
				Object value = paramList[position++];
				if (value == NULL)
					value = null;
				String s = parts.get(i++);
				if (s.equals("?")) {
					if (value instanceof Collection) {
						Collection<Object> c = (Collection) value;
						if (c.size() > 0) {
							SH.repeat("?,", c.size() - 1, sb);
							sb.append('?');
							objects.addAll(c);
						} else {
							sb.append('?');
							objects.add(null);
						}
					} else if (value != null && value.getClass().isArray() && value.getClass().getComponentType() != byte.class) {
						int size = Array.getLength(value);
						if (size > 0) {
							SH.repeat("?,", size - 1, sb);
							sb.append('?');
							if (value instanceof Object[]) {
								Object[] array = (Object[]) value;
								for (Object o : array)
									objects.add(o);
							} else {
								for (int j = 0; j < size; j++)
									objects.add(Array.get(value, j));
							}
						} else {
							sb.append('?');
							objects.add(null);
						}

					} else {
						objects.add(value);
						sb.append('?');
					}
				} else if (s.equals("??")) {
					sb.append(value);
				} else
					throw new IllegalStateException(s);
			}
		}

		PreparedStatement ps = psFactory == null ? connection.prepareStatement(sb.toString()) : psFactory.createPreparedStatement(connection, sb.toString());
		for (int i = 0, l = objects.size(); i < l; i++) {
			final Object data = objects.get(i);
			try {
				ps.setObject(1 + i, data);
			} catch (Exception e) {
				try {
					if (ps != null)
						ps.close();
				} catch (Exception e2) {
					throw new Exception("Error closing prepared statement ", e2);
				}
				throw new Exception("Error setting param " + (1 + i) + " with value '" + data + "' of type " + OH.getClassName(data) + " for expanded sql " + sb.toString(), e);
			}
		}
		List<PreparedStatement> res = CH.l(ps);
		return res;
	}

	private int[] QUESTION_OR_EOF = new int[] { CharReader.EOF, '?' };
	private List<String> parts;

	public NamedParamDbStatementPreparer(String sql) throws SQLException {
		StringCharReader reader = new StringCharReader(sql);
		StringBuilder sb = new StringBuilder();

		List<String> parts = new ArrayList<String>();
		outer: for (;;) {
			if (reader.readUntilAny(QUESTION_OR_EOF, sb) == CharReader.EOF) {
				parts.add(sb.toString());
				break outer;
			}
			reader.expect('?');
			int nextChar = reader.readCharOrEof();
			switch (nextChar) {
				case CharReader.EOF:
					parts.add(sb.toString());
					parts.add("?");
					SH.clear(sb);
					break;
				case '{':
					parts.add(sb.toString());
					parts.add("?");
					reader.readUntil('}', SH.clear(sb));
					reader.expect('}');
					namedPositions.putMulti(sb.toString(), parts.size() / 2 - 1);
					SH.clear(sb);
					break;
				case '?':
					nextChar = reader.readCharOrEof();
					switch (nextChar) {
						case CharReader.EOF:
							parts.add(sb.toString());
							parts.add("??");
							SH.clear(sb);
							break;
						case '?':
							sb.append('?');
							break;
						case '{':
							parts.add(sb.toString());
							parts.add("??");
							reader.readUntil('}', SH.clear(sb));
							reader.expect('}');
							namedPositions.putMulti(sb.toString(), parts.size() / 2 - 1);
							SH.clear(sb);
							break;
						default:
							parts.add(sb.toString());
							parts.add("??");
							SH.clear(sb).append((char) nextChar);
							break;
					}
					break;
				default:
					parts.add(sb.toString());
					parts.add("?");
					SH.clear(sb).append((char) nextChar);
			}
		}
		this.parts = parts;
		this.paramsCount = (parts.size() - 1) / 2;
	}

	public Set<String> getNamedParams() {
		return namedPositions.keySet();
	}

	public int getParamsCount() {
		return paramsCount;
	}
}
