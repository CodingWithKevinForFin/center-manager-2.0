package com.f1.ami.amihibernate;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.hibernate.sql.Update;
import org.hibernate.type.LiteralType;

public class AmiUpdate extends Update {
	private Dialect dialect;
	private Map columns = new LinkedHashMap();
	private Map primaryKeyColumns = new LinkedHashMap();
	private Map whereColumns = new LinkedHashMap();

	private String tableName;
	private String versionColumnName;
	private String comment;
	private String assignments;
	private String where;

	public AmiUpdate(Dialect dialect) {
		super(dialect);
		this.dialect = dialect;
	}
	@Override
	public Update setTableName(String tableName) {
		this.tableName = tableName;
		super.setTableName(tableName);
		return this;
	}
	@Override
	public Update setComment(String comment) {
		this.comment = comment;
		super.setComment(comment);
		return this;
	}

	@Override
	public Update appendAssignmentFragment(String fragment) {
		if (assignments == null) {
			assignments = fragment;
		} else {
			assignments += ", " + fragment;
		}
		super.appendAssignmentFragment(fragment);
		return this;
	}

	@Override
	public Update addWhereColumns(String[] columnNames) {
		for (String columnName : columnNames) {
			this.addWhereColumn(columnName);
		}
		return this;
	}

	@Override
	public Update addWhereColumns(String[] columnNames, String valueExpression) {
		for (String columnName : columnNames) {
			this.addWhereColumn(columnName, valueExpression);
		}
		return this;
	}

	@Override
	public Update addWhereColumn(String columnName) {
		return this.addWhereColumn(columnName, "=?");
	}

	@Override
	public Update addWhereColumn(String columnName, String valueExpression) {
		whereColumns.put(columnName, valueExpression);
		super.addWhereColumn(columnName, valueExpression);
		return this;
	}

	@Override
	public Update setWhere(String where) {
		this.where = where;
		super.setWhere(where);
		return this;
	}

	@Override
	public Update setVersionColumnName(String versionColumnName) {
		this.versionColumnName = versionColumnName;
		super.setVersionColumnName(versionColumnName);
		return this;
	}
	@Override
	public Update addColumns(String[] columnNames) {
		for (String columnName : columnNames) {
			this.addColumn(columnName);
		}
		return this;
	}

	@Override
	public Update addColumns(String[] columnNames, boolean[] updateable, String[] valueExpressions) {
		for (int i = 0; i < columnNames.length; i++) {
			if (updateable[i]) {
				this.addColumn(columnNames[i], valueExpressions[i]);
			}
		}
		return this;
	}

	@Override
	public Update addColumns(String[] columnNames, String valueExpression) {
		for (String columnName : columnNames) {
			this.addColumn(columnName, valueExpression);
		}
		return this;
	}

	@Override
	public Update addColumn(String columnName) {
		return this.addColumn(columnName, "?");
	}

	@Override
	public Update addColumn(String columnName, String valueExpression) {
		columns.put(columnName, valueExpression);
		super.addColumn(columnName, valueExpression);
		return this;
	}

	@Override
	public Update addColumn(String columnName, Object value, LiteralType type) throws Exception {
		return addColumn(columnName, type.objectToSQLString(value, dialect));
	}
	@Override
	public Update setPrimaryKeyColumnNames(String[] columnNames) {
		this.primaryKeyColumns.clear();
		addPrimaryKeyColumns(columnNames);
		super.setPrimaryKeyColumnNames(columnNames);
		return this;
	}

	@Override
	public Update addPrimaryKeyColumns(String[] columnNames) {
		for (String columnName : columnNames) {
			this.addPrimaryKeyColumn(columnName, "?");
		}
		return this;
	}

	@Override
	public Update addPrimaryKeyColumns(String[] columnNames, boolean[] includeColumns, String[] valueExpressions) {
		for (int i = 0; i < columnNames.length; i++) {
			if (includeColumns[i]) {
				this.addPrimaryKeyColumn(columnNames[i], valueExpressions[i]);
			}
		}
		return this;
	}

	@Override
	public Update addPrimaryKeyColumns(String[] columnNames, String[] valueExpressions) {
		for (int i = 0; i < columnNames.length; i++) {
			this.addPrimaryKeyColumn(columnNames[i], valueExpressions[i]);
		}
		return this;
	}

	public Update addPrimaryKeyColumn(String columnName, String valueExpression) {
		this.primaryKeyColumns.put(columnName, valueExpression);
		super.addPrimaryKeyColumn(columnName, valueExpression);
		return this;
	}

	public String toStatementString() {
		StringBuilder buf = new StringBuilder((columns.size() * 15) + tableName.length() + 10);
		if (comment != null) {
			buf.append("/* ").append(comment).append(" */ ");
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
			buf.append(versionColumnName).append(AmiDialect.OPERATOR_EQUALS).append('?');
		}

		return buf.toString();
	}
}
