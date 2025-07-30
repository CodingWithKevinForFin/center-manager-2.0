package com.f1.ami.center.replication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.center.sysschema.AmiSchema;
import com.f1.ami.center.table.AmiCenterSqlProcessorMutator;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.table.BasicRowComparator;

public class AmiCenterReplicationHelper {

	public static boolean describeIndex(String table, String index, Table indexSchema, StringBuilder sql) {
		List<Row> rows = new ArrayList<Row>();
		for (Row row : indexSchema.getRows()) {
			if (OH.eq(table, row.get("TableName")) && OH.eq(index, row.get("IndexName"))) {
				rows.add(row);
			}
		}
		if (rows.size() == 0)
			return false;
		sql.append("CREATE INDEX ");
		AmiUtils.escapeVarName(index, sql);
		sql.append(" ON ");
		AmiUtils.escapeVarName(table, sql);
		sql.append("(");
		String constraint = rows.get(0).get("Constraint", String.class);
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			String type = row.get("IndexType", String.class);
			String name = row.get("ColumnName", String.class);
			if (i > 0)
				sql.append(',');
			AmiUtils.escapeVarName(name, sql);
			sql.append(' ').append(type);
		}

		sql.append(") USE ");
		AmiSchema.generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_CONSTRAINT, constraint, sql);
		sql.append(';').append(SH.NEWLINE);
		return true;
	}

	public static boolean describeTable(String table, Table tableSchema, Table columnSchema, Table indexSchema, StringBuilder sql) {
		Row tableRow = null;
		List<Row> rows = new ArrayList<Row>();
		for (Row row : tableSchema.getRows())
			if (OH.eq(table, row.get("TableName"))) {
				tableRow = row;
				break;
			}
		if (tableRow == null)
			return false;
		for (Row row : columnSchema.getRows())
			if (OH.eq(table, row.get("TableName")))
				rows.add(row);
		TableHelper.sort(rows, new BasicRowComparator(new int[] { rows.get(0).getTable().getColumn("Position").getLocation() }, new boolean[] { true }));
		sql.append("CREATE PUBLIC TABLE ");
		AmiUtils.escapeVarName(table, sql);
		sql.append("(");
		for (int i = 0; i < rows.size(); i++) {
			Row col = rows.get(i);
			if (i > 0)
				sql.append(',');
			if (i % 5 == 0 && i != 0) {
				sql.append(SH.NEWLINE);
			}
			AmiUtils.escapeVarName(col.get("ColumnName", String.class), sql);
			sql.append(' ').append(col.get("DataType", String.class));
			String options = col.get("Options", String.class);
			if (options != null)
				sql.append(' ').append(options);
			boolean noNull = col.get("NoNull", Boolean.class);
			if (noNull)
				sql.append(" NoNull");
		}
		sql.append(") USE ");
		//		AmiTablePersisterBinding persister = table.getPersister();
		//		if (persister != null) {
		//			generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_PERSIST_ENGINE, persister.getPersisterType(), sql);
		//			for (Entry<String, String> i : persister.getOptionsStrings().entrySet())
		//				generateUseOptionSql(i.getKey(), SH.toString(i.getValue()), sql);
		//		}
		AmiSchema.generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_PERSIST_ENGINE, SH.toString(tableRow.get("PersistEngine", String.class)), sql);
		AmiSchema.generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_NOBROADCAST, SH.toString(!tableRow.get("Broadcast", boolean.class)), sql);
		AmiSchema.generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_REFRESH_PERIOD_MS, SH.toString(tableRow.get("RefreshPeriodMs", Long.class)), sql);
		AmiSchema.generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_ON_UNDEF_COLUMN, tableRow.get("OnUndefColumn", String.class), sql);
		//		AmiSchema.generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_INITIAL_CAPACITY, SH.toString(tableRow.get("InitialCapacity", Long.class)), sql);
		sql.append(';').append(SH.NEWLINE);
		Set<String> indexes = new TreeSet<String>();
		for (Row row : indexSchema.getRows())
			if (OH.eq(table, row.get("TableName")))
				indexes.add(row.get("IndexName", String.class));
		for (String s : indexes)
			describeIndex(table, s, indexSchema, sql);
		return true;
	}

}
