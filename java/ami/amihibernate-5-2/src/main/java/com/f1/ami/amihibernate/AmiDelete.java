package com.f1.ami.amihibernate;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.sql.Delete;

public class AmiDelete extends Delete {
	protected String tableName;
	protected String comment;
	protected String where;
	protected String versionColumnName;
	protected Map primaryKeyColumns = new LinkedHashMap();

	@Override
	public Delete setTableName(String tableName) {
		this.tableName = tableName;
		super.setTableName(tableName);
		return this;
	}
	@Override
	public Delete setComment(String comment) {
		this.comment = comment;
		super.setComment(comment);
		return this;
	}
	@Override
	public Delete setWhere(String where) {
		this.where = where;
		super.setWhere(where);
		return this;
	}
	public Delete addWhereFragment(String fragment) {
		if (where == null) {
			where = fragment;
		} else {
			where += (" and " + fragment);
		}
		super.addWhereFragment(fragment);
		return this;
	}

	public Delete setPrimaryKeyColumnNames(String[] columnNames) {
		this.primaryKeyColumns.clear();
		super.setPrimaryKeyColumnNames(columnNames);
		return this;
	}

	public Delete addPrimaryKeyColumns(String[] columnNames) {
		for (String columnName : columnNames) {
			this.addPrimaryKeyColumn(columnName, "?");
		}
		return this;
	}

	public Delete addPrimaryKeyColumns(String[] columnNames, boolean[] includeColumns, String[] valueExpressions) {
		for (int i = 0; i < columnNames.length; i++) {
			if (includeColumns[i]) {
				this.addPrimaryKeyColumn(columnNames[i], valueExpressions[i]);
			}
		}
		return this;
	}

	public Delete addPrimaryKeyColumns(String[] columnNames, String[] valueExpressions) {
		for (int i = 0; i < columnNames.length; i++) {
			this.addPrimaryKeyColumn(columnNames[i], valueExpressions[i]);
		}
		return this;
	}

	public Delete addPrimaryKeyColumn(String columnName, String valueExpression) {
		this.primaryKeyColumns.put(columnName, valueExpression);
		super.addPrimaryKeyColumn(columnName, valueExpression);
		return this;
	}

	public Delete setVersionColumnName(String versionColumnName) {
		this.versionColumnName = versionColumnName;
		super.setVersionColumnName(versionColumnName);
		return this;
	}

	@Override
	public String toStatementString() {
		StringBuilder buf = new StringBuilder(tableName.length() + 10);
		if (comment != null) {
			buf.append("/* ").append(comment).append(" */ ");
		}
		buf.append("delete from ").append(tableName);
		if (where != null || !primaryKeyColumns.isEmpty() || versionColumnName != null) {
			buf.append(" where ");
		}
		boolean conditionsAppended = false;
		Iterator iter = primaryKeyColumns.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry e = (Map.Entry) iter.next();
			Object key = e.getKey();
			buf.append(e.getKey()).append(AmiDialect.OPERATOR_EQUALS).append(e.getValue());
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
			buf.append(versionColumnName).append(AmiDialect.OPERATOR_EQUALS).append('?');
		}
		return buf.toString();
	}

}
