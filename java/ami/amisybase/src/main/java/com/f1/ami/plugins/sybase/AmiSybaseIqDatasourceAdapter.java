package com.f1.ami.plugins.sybase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.base.Valued;
import com.f1.utils.CH;
import com.f1.utils.DBH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_String;

public class AmiSybaseIqDatasourceAdapter extends JdbcAdapter {
	private static final Logger log = LH.get();

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		return r;
	}

	@Override
	protected String buildJdbcDriverClass() {
		return "com.sybase.jdbc4.jdbc.SybDriver";
	}
	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return CH.m("User", getUsername(), "Password", getPassword());
	}
	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:sybase:Tds";
	}
	@Override
	protected String buildJdbcUrlPassword() {
		return null;
	}

	@Override
	protected String buildJdbcUrl() {
		return super.getUrl();
	}

	static final Map<String, Byte> DATA_TYPES = new HashMap<String, Byte>();
	static {
		DATA_TYPES.put("tinyint", AmiDatasourceColumn.TYPE_INT);
		DATA_TYPES.put("smallint", AmiDatasourceColumn.TYPE_INT);
		DATA_TYPES.put("mediumint", AmiDatasourceColumn.TYPE_INT);
		DATA_TYPES.put("int", AmiDatasourceColumn.TYPE_INT);

		DATA_TYPES.put("integer", AmiDatasourceColumn.TYPE_INT);
		DATA_TYPES.put("bigint", AmiDatasourceColumn.TYPE_INT);
		DATA_TYPES.put("float", AmiDatasourceColumn.TYPE_DOUBLE);

		DATA_TYPES.put("double", AmiDatasourceColumn.TYPE_DOUBLE);
		DATA_TYPES.put("double precision", AmiDatasourceColumn.TYPE_DOUBLE);
		DATA_TYPES.put("real", AmiDatasourceColumn.TYPE_FLOAT);
		DATA_TYPES.put("decimal", AmiDatasourceColumn.TYPE_DOUBLE);
		DATA_TYPES.put("numeric", AmiDatasourceColumn.TYPE_DOUBLE);
		DATA_TYPES.put("smallmoney", AmiDatasourceColumn.TYPE_DOUBLE);
		DATA_TYPES.put("money", AmiDatasourceColumn.TYPE_DOUBLE);

		DATA_TYPES.put("date", AmiDatasourceColumn.TYPE_LONG);

		DATA_TYPES.put("datetime", AmiDatasourceColumn.TYPE_LONG);
		DATA_TYPES.put("bigdatetime", AmiDatasourceColumn.TYPE_LONG);
		DATA_TYPES.put("smalldatetime", AmiDatasourceColumn.TYPE_LONG);
		DATA_TYPES.put("timestamp", AmiDatasourceColumn.TYPE_LONG);
		DATA_TYPES.put("time", AmiDatasourceColumn.TYPE_LONG);
		DATA_TYPES.put("bigtime", AmiDatasourceColumn.TYPE_LONG);
		DATA_TYPES.put("year", AmiDatasourceColumn.TYPE_LONG);

		DATA_TYPES.put("char", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("varchar", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("tinytext", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("text", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("mediumtext", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("longtext", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("enum", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("set", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("bit", AmiDatasourceColumn.TYPE_INT);
		DATA_TYPES.put("nchar", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("nvarchar", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("ntext", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("unichar", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("unitext", AmiDatasourceColumn.TYPE_STRING);

		DATA_TYPES.put("tinyblock", AmiDatasourceColumn.TYPE_BINARY);
		DATA_TYPES.put("blob", AmiDatasourceColumn.TYPE_BINARY);
		DATA_TYPES.put("varbinary", AmiDatasourceColumn.TYPE_BINARY);
		DATA_TYPES.put("binary", AmiDatasourceColumn.TYPE_BINARY);
		DATA_TYPES.put("mediumblob", AmiDatasourceColumn.TYPE_BINARY);
		DATA_TYPES.put("longblob", AmiDatasourceColumn.TYPE_BINARY);
		DATA_TYPES.put("image", AmiDatasourceColumn.TYPE_BINARY);

		DATA_TYPES.put("OTHER", AmiDatasourceColumn.TYPE_STRING);

	}

	private byte toColumnType(String type, int width) {
		byte r = CH.getOr(DATA_TYPES, type, AmiDatasourceColumn.TYPE_NONE).byteValue();
		return r;
	}
	private <T extends Valued> List<T> exec(Connection conn, String sql, Class<T> clazz) throws Exception {
		ResultSet resultSet = conn.createStatement().executeQuery(sql);
		List<T> r = DBH.toValuedList(resultSet, tools.getServices().getGenerator(clazz));
		IOH.close(resultSet);
		return r;
	}
	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		String limitString = limit > 0 ? "SET ROWCOUNT " + limit + " " : "";
		sb.append(limitString);
		sb.append("SELECT a.name as table_name, a.type as table_type, b.name as table_owner from dbo.sysobjects a join sysusers b on a.uid = b.uid ");
		return sb;
	}
	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);

		//Get name of Table and the Collection(Schema,Owner) it's in
		String name = SH.trim(row.get("table_name", Caster_String.INSTANCE));
		String owner = SH.trim(row.get("table_owner", Caster_String.INSTANCE));
		char type = row.get("table_type", Caster_Character.INSTANCE);

		if ('S' == type)
			return null;
		if ('V' == type)
			return null;
		//End

		table.setName(name);
		table.setCollectionName(owner);

		//Set Custom Query 
		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));
		return table;
	}
	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		//CollectionName is owner
		return getSchemaName('.', '"', sb, table.getName(), table.getCollectionName());
	}

	@Override
	protected StringBuilder createPreviewQuery(StringBuilder sb, String fullname, int limit) {
		sb.append("SET ROWCOUNT ").append(limit);
		sb.append(" SELECT * FROM ").append(fullname);

		return sb;
	}

}
