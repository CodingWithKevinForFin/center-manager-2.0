package com.f1.ami.center.hdb;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.SH;

public class AmiHdbSchemaUtils {

	private static void generateCreateSql(AmiHdbTable table, StringBuilder sql) {
		int indent = sql.length();
		sql.append("CREATE PUBLIC TABLE ");
		AmiUtils.escapeVarName(table.getName(), sql);
		sql.append("(");
		indent = sql.length() - indent;
		for (int i = 0; i < table.getColumnsCount(); i++) {
			AmiHdbColumn col = table.getColumnAt(i);
			if (i > 0)
				sql.append(',');
			if (i % 5 == 0 && i != 0) {
				sql.append(SH.NEWLINE);
				SH.repeat(' ', indent, sql);
			}
			AmiUtils.escapeVarName(col.getName(), sql);
			sql.append(' ').append(AmiTableUtils.toStringForDataType(col.getAmiType()));
			sql.append(' ').append(AmiHdbUtils.toStringForMode(col.getMode()));
			getColumnOptions(col, sql);
		}
		sql.append(") USE PersistEngine=\"HISTORICAL\"");
		int mos = table.getMaxOptimizeSeconds();
		if (mos >= 0)
			sql.append(" " + AmiHdbUtils.OPTION_MAX_OPTIMIZE_SECONDS + "=\"").append(mos).append("\"");
		double mop = table.getOptimizePctCutoff();
		if (mop < 1d)
			sql.append(" " + AmiHdbUtils.OPTION_MIN_OPTIMIZE_PCT + "=\"").append(mop).append("\"");
		sql.append(';').append(SH.NEWLINE);
	}

	private static void getColumnOptions(AmiHdbColumn col, StringBuilder sql) {
	}
	private static void generateCreateSql(AmiHdbIndex index, StringBuilder sql) {
		sql.append("CREATE INDEX ");
		AmiUtils.escapeVarName(index.getName(), sql);
		sql.append(" ON ");
		AmiUtils.escapeVarName(index.getTable().getName(), sql);
		sql.append("(");
		//		for (int i = 0; i < index.getColumnsCount(); i++) {
		AmiHdbColumn col = index.getColumn();
		AmiUtils.escapeVarName(col.getName(), sql);

		sql.append(" SORT)");
		sql.append(';').append(SH.NEWLINE);
	}
	public static void generateHdbSchema(StringBuilder sql, AmiHdbTable table) {
		if (table.getDefType() == AmiTableUtils.DEFTYPE_USER) {
			generateCreateSql(table, sql);
			for (AmiHdbIndex i : table.getIndexes().values())
				generateCreateSql(i, sql);
			sql.append(SH.NEWLINE);
			sql.append(SH.NEWLINE);
		}
	}
}
