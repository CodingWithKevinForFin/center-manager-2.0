package com.f1.ami.amihibernate;

import java.util.Iterator;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.unique.MySQLUniqueDelegate;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;

import com.f1.utils.SH;

public class AmiUniqueDelegate extends MySQLUniqueDelegate {
	private Dialect dialect;
	private StringBuilder sb;

	public AmiUniqueDelegate(Dialect dialect) {
		super(dialect);
		this.dialect = dialect;
		this.sb = new StringBuilder();
	}
	@Override
	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
		SH.clear(sb);
		final JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();

		final String tableName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(uniqueKey.getTable().getQualifiedTableName(), dialect);

		final String constraintName = dialect.quote(uniqueKey.getName());

		sb.append("create index ");
		if (dialect.supportsIfExistsBeforeConstraintName()) {
			sb.append("if exists ");
		}
		sb.append(constraintName).append(" on ").append(tableName);

		String createIndexSql = SH.toStringAndClear(sb);
		String uniqueConstraintSql = uniqueConstraintSql(uniqueKey);

		//		return super.getAlterTableToAddUniqueKeyCommand(uniqueKey, metadata);
		return createIndexSql + uniqueConstraintSql;
	}

	@Override
	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
		SH.clear(sb);
		final JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();

		final String tableName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(uniqueKey.getTable().getQualifiedTableName(), dialect);

		//		final StringBuilder buf = new StringBuilder(dialect.getAlterTableString(tableName));
		sb.append(getDropUnique());
		if (dialect.supportsIfExistsBeforeConstraintName()) {
			sb.append("if exists ");
		}
		sb.append(dialect.quote(uniqueKey.getName()));
		sb.append(" on ").append(tableName);
		//		return buf.toString();
		// TODO Auto-generated method stub
		return SH.toStringAndClear(sb);
	}

	@Override
	protected String uniqueConstraintSql(UniqueKey uniqueKey) {
		// TODO: Hard coded to HASH
		SH.clear(sb);
		sb.append("(");
		final Iterator<org.hibernate.mapping.Column> columnIterator = uniqueKey.columnIterator();
		while (columnIterator.hasNext()) {
			final org.hibernate.mapping.Column column = columnIterator.next();
			sb.append(column.getQuotedName(dialect));
			sb.append(" ").append("HASH");
			if (uniqueKey.getColumnOrderMap().containsKey(column)) {
				sb.append(" ").append(uniqueKey.getColumnOrderMap().get(column));
			}
			if (columnIterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append(") use constraint=\"UNIQUE\"");
		return sb.toString();
	}
	@Override
	protected String getDropUnique() {
		// TODO Auto-generated method stub
		return "drop index ";
	}
	@Override
	public String getColumnDefinitionUniquenessFragment(Column column) {
		return super.getColumnDefinitionUniquenessFragment(column);
	}
	@Override
	public String getTableCreationUniqueConstraintsFragment(Table table) {
		return super.getTableCreationUniqueConstraintsFragment(table);
	}

}
