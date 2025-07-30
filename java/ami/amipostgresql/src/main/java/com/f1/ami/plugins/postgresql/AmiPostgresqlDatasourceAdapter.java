package com.f1.ami.plugins.postgresql;

import java.sql.Connection;
import java.util.Collections;
import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiPostgresqlDatasourceAdapter extends JdbcAdapter {
	private static final String DEFAULT_SELECT_CLAUSE = "SELECT table_schema,table_name,column_name,data_type FROM information_schema.columns where table_catalog=current_database() and table_schema!='information_schema' and table_schema!='pg_catalog' order by table_schema DESC,table_name DESC;";
	private com.f1.utils.structs.BasicMultiMap.List<String, String> tablesToColumns;

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		return r;
	}

	@Override
	protected String buildJdbcDriverClass() {
		return "org.postgresql.Driver";
	}

	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return Collections.EMPTY_MAP;
	}

	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:postgresql://";
	}

	@Override
	protected String buildJdbcUrl() {
		return getUrl() + "?user=" + getUsernameEncoded() + "&password=****";
	}

	@Override
	protected String buildJdbcUrlPassword() {
		return getPasswordEncoded();
	}

	private byte convertPostgresTypesToAmi(String colType) {
		if (colType.startsWith("smallint") || colType.startsWith("integer") || colType.startsWith("int") || colType.startsWith("bigint") || colType.startsWith("smallserial")
				|| colType.startsWith("serial") || colType.startsWith("bigserial"))
			return AmiDatasourceColumn.TYPE_LONG;
		else if (colType.startsWith("decimal") || colType.startsWith("numeric") || colType.startsWith("real") || colType.startsWith("double"))
			return AmiDatasourceColumn.TYPE_DOUBLE;
		else if (colType.startsWith("date") || colType.startsWith("time"))
			return AmiDatasourceColumn.TYPE_UTC;
		else if (colType.startsWith("boolean"))
			return AmiDatasourceColumn.TYPE_BOOLEAN;
		else
			return AmiDatasourceColumn.TYPE_STRING;
	}
	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		String limitString = limit > 0 ? " LIMIT " + limit : "";
		sb.append("SELECT table_schema, table_name, table_type FROM information_schema.tables");
		sb.append(limitString);
		return sb;
	}
	@Override
	protected Table execShowTablesQuery(StringBuilder sb, Connection conn, int limit, AmiDatasourceTracker debugSink, TimeoutController tc) throws Exception {
		createShowTablesQuery(SH.clear(sb), limit);
		Table tables = exec(conn, SH.toStringAndClear(sb), limit, debugSink, tc);
		for (int i = 0; i < tables.getColumnsCount(); i++) {//netezza database returns upper case names
			String name = tables.getColumnAt(i).getId().toString();
			if (!SH.isLowerCase(name))
				tables.renameColumn(i, name.toLowerCase());
		}
		return tables;
	}

	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);

		//Get name of Table and the Collection(Schema,Owner) it's in
		String name = SH.trim(row.get("table_name", Caster_String.INSTANCE));
		String collectionName = SH.trim(row.get("table_schema", Caster_String.INSTANCE));
		String type = SH.trim(row.get("table_type", Caster_String.INSTANCE));

		if ("VIEW".equalsIgnoreCase(type))
			return null;
		if ("pg_catalog".equalsIgnoreCase(collectionName))
			return null;
		//End

		table.setName(name);
		table.setCollectionName(collectionName);

		//Set Custom Query 
		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));
		return table;
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		return getSchemaName('.', '"', sb, table.getName(), table.getCollectionName());
	}

	@Override
	protected String formatTargetTableName(AmiCenterUploadTable ul) {
		return "\"" + ul.getTargetTable() + "\"";
	}

	@Override
	protected void formatTargetColumnName(String c, StringBuilder sql) {
		sql.append("\"").append(c).append("\"");
	}

}
