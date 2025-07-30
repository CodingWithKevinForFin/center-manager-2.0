package com.f1.ami.amihibernate;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.hibernate.sql.Update;

public class AmiUpdate extends Update {

	public AmiUpdate(Dialect dialect) {
		super(dialect);
	}

	@Override
	public Update addWhereColumn(String columnName) {
		return addWhereColumn(columnName, "==?");
	}
	public String toStatementString() {
		StringBuilder buf = new StringBuilder((columns.size() * 15) + tableName.length() + 10);
		if (comment != null) {
			buf.append("/* ").append(Dialect.escapeComment(comment)).append(" */ ");
		}
		buf.append("update ").append(tableName).append(" set ");
		boolean assignmentsAppended = false;
		Iterator<Map.Entry<String, String>> iter = columns.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> e = iter.next();
			buf.append(e.getKey()).append('=').append(e.getValue());
			if (iter.hasNext()) {
				buf.append(", ");
			}
			assignmentsAppended = true;
		}
		if (assignments != null) {
			if (assignmentsAppended) {
				buf.append(", ");
			}
			buf.append(assignments);
		}

		boolean conditionsAppended = false;
		if (!primaryKeyColumns.isEmpty() || where != null || !whereColumns.isEmpty() || versionColumnName != null) {
			buf.append(" where ");
		}
		iter = primaryKeyColumns.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> e = iter.next();
			buf.append(e.getKey()).append("==").append(e.getValue());
			if (iter.hasNext()) {
				buf.append(" and ");
			}
			conditionsAppended = true;
		}
		if (where != null) {
			if (conditionsAppended) {
				buf.append(" and ");
			}
			buf.append(where);
			conditionsAppended = true;
		}
		iter = whereColumns.entrySet().iterator();
		while (iter.hasNext()) {
			final Map.Entry<String, String> e = iter.next();
			if (conditionsAppended) {
				buf.append(" and ");
			}
			buf.append(e.getKey()).append(e.getValue());
			conditionsAppended = true;
		}
		if (versionColumnName != null) {
			if (conditionsAppended) {
				buf.append(" and ");
			}
			buf.append(versionColumnName).append("=?");
		}

		return buf.toString();
	}
}
