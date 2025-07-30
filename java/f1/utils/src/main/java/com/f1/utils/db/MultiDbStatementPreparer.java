package com.f1.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class MultiDbStatementPreparer implements DbStatementPreparer {

	private List<NamedParamDbStatementPreparer> inner = new ArrayList<NamedParamDbStatementPreparer>();

	@Override
	public List<PreparedStatement> prepareStatement(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		if (connection == null)
			throw new NullPointerException("connection");
		if (inner.size() == 1)
			return inner.get(0).prepareStatement(params, connection, factory);
		int start = 0, end;
		final Set<Object> used = new HashSet<Object>();
		final List<PreparedStatement> r = new ArrayList<PreparedStatement>(inner.size());
		for (NamedParamDbStatementPreparer p : inner) {
			end = start + p.getParamsCount();
			final Map<Object, Object> m = new HashMap<Object, Object>();
			if (params != null) {
				for (Map.Entry<Object, Object> e : params.entrySet()) {
					final Object k = e.getKey();
					if (k instanceof Integer) {
						final int i = (Integer) k;
						if (OH.isBetween(i, start, end - 1))
							m.put(i - start, e.getValue());
						used.add(k);
					} else if (p.getNamedParams().contains(k)) {
						m.put(k, e.getValue());
						used.add(k);
					}
				}
			}
			r.addAll(p.prepareStatement(m, connection, factory));
			start = end;
		}
		return r;
	}

	public MultiDbStatementPreparer(String sql) throws SQLException {
		List<String> parts = new ArrayList<String>();
		String[] lines = SH.splitLines(sql);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			line = SH.trim('\n', line);
			line = SH.trim('\r', line);
			if (SH.isnt(line))
				continue;
			if (line.trim().endsWith(";")) {
				sb.append(SH.beforeLast(line.trim(), ';')).append(SH.NEWLINE);
				parts.add(sb.toString());
				SH.clear(sb);
			} else
				sb.append(line).append(SH.NEWLINE);
		}
		if (sb.length() > 0) {
			parts.add(sb.toString());
			SH.clear(sb);
		}
		for (String part : parts) {
			inner.add(new NamedParamDbStatementPreparer(part));
		}
	}
}