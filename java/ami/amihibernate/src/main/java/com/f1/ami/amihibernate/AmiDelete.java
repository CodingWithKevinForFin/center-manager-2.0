package com.f1.ami.amihibernate;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.hibernate.sql.Delete;

public class AmiDelete extends Delete {
	@Override
	public String toStatementString() {
		StringBuilder buf = new StringBuilder(tableName.length() + 10);
		if (comment != null) {
			buf.append("/* ").append(Dialect.escapeComment(comment)).append(" */ ");
		}
		buf.append("delete from ").append(tableName);
		if (where != null || !primaryKeyColumns.isEmpty() || versionColumnName != null) {
			buf.append(" where ");
		}
		boolean conditionsAppended = false;
		Iterator<Map.Entry<String, String>> iter = primaryKeyColumns.entrySet().iterator();
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
		if (versionColumnName != null) {
			if (conditionsAppended) {
				buf.append(" and ");
			}
			buf.append(versionColumnName).append("=?");
		}
		return buf.toString();
	}

}
