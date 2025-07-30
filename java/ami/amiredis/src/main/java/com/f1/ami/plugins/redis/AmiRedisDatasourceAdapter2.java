package com.f1.ami.plugins.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class AmiRedisDatasourceAdapter2 extends JdbcAdapter implements AmiDatasourceAdapter {
	private static final String DB_INFOTABLE = "sys_tables";
	private static final String DB_INFOCOLUMNS = "sys_tablecolumns";
	private static final String DB_TABLESCHEMA = "SchemaName";
	private static final String DB_TABLENAME = "TableName";
	private static final String DB_COLNAME = "ColumnName";
	private static final String DB_COLTYPE = "DataTypeName";

	private static final String DB_TABLESCHEMA_QUERY = "SELECT CatalogName, " + DB_TABLESCHEMA + ", " + DB_TABLENAME + ", TableType FROM " + DB_INFOTABLE;
	private static final String DB_COLSCHEMA_QUERY = "SELECT " + DB_COLNAME + ", " + DB_COLTYPE + " FROM " + DB_INFOCOLUMNS + " WHERE ";

	private static final Logger log = LH.get(AmiRedisDatasourceAdapter.class);

	public static Map<String, String> buildOptions() {
		//		Map<String, String> r = JdbcAdapter.buildOptions();
		HashMap<String, String> r = new HashMap<String, String>();
		r.put(OPTION_URL_SUFFIX, "Extra Parameters");
		r.put(OPTION_URL_OVERRIDE, "Hard code URL, ${USERNAME} and ${PASSWORD} will be substituted accordningly");
		r.put(OPTION_PREPEND_SCHEMA, "Include schema name in the table listing wizard");
		r.put(OPTION_SCHEMA_COLUMN_LIMIT, "Limit columns returent in wizard");
		r.put(OPTION_SCHEMA_TABLE_LIMIT, "Limit tables returent in wizard");
		r.put(OPTION_DISABLE_UTCN, "If true, convert timestamps with precision greater than milliseconds into milliseconds(UTC type)");
		r.put(OPTION_DISABLE_BIGINT, "If true, convert bigintegers to longs");
		r.put(OPTION_DISABLE_BIGDEC, "If true, convert bigdecimals to doubles");
		return r;
	}

	//	private static final Map<String, String> OPERATIONS = CH.m("=", "AND", "OR");
	private final StringBuilder sb = new StringBuilder();

	@Override
	protected String buildJdbcDriverClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> buildJdbcArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String buildJdbcUrlSubprotocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String buildJdbcUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String buildJdbcUrlPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String toJdbcUrl(ContainerTools tools, String name, String url, String username, String password, String options) {
		SH.clear(sb);
		sb.append("cdata.jdbc.redis.RedisDriver:{").append(SH.noNull("")).append("}:jdbc:redis://").append(url);
		if (!SH.endsWith(sb, ';'))
			sb.append(';');
		if (SH.isnt(username) && SH.isnt(password))
			sb.append("AuthScheme=None");
		if (SH.is(username))
			sb.append("UserId=").append(username).append(';');
		if (SH.is(password))
			sb.append("Password=").append(password).append(';');
		return SH.toStringAndClear(sb);
	}

	@Override
	protected String createLimitClause(String select, int limit) {
		if (limit >= 0) {
			SH.clear(sb);
			sb.append(select).append(" LIMIT ").append(limit);
			return SH.toStringAndClear(sb);
		} else
			return select;
	}

	protected String createLimitClause(String select, int skip, int limit) {
		if (limit >= 0 || skip >= 0) {
			SH.clear(sb);
			sb.append(select).append(" LIMIT ").append(skip).append(',').append(limit);
			return SH.toStringAndClear(sb);
		} else
			return select;
	}

	@Override
	protected StringBuilder createLimitClause2(StringBuilder query, int limit, Integer skip) {
		if (limit >= 0 || (skip != null && skip >= 0)) { // I think it should be skip > 0 instead
			if (skip == null || skip <= 0)
				sb.append(" LIMIT ").append(limit);
			else
				sb.append(" LIMIT ").append(skip).append(',').append(limit);
		}
		return sb;
	}
	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		sb.append(DB_TABLESCHEMA_QUERY);
		createLimitClause2(sb, limit, null);
		return sb;
	}
	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);

		//Get name of Table and the Collection(Schema,Owner) it's in
		String name = SH.trim(row.get(DB_TABLENAME, Caster_String.INSTANCE));
		String collectionName = SH.trim(row.get(DB_TABLESCHEMA, Caster_String.INSTANCE));

		table.setName(name);
		table.setCollectionName(collectionName);

		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));
		return table;
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		return getSchemaName('.', '"', sb, table.getName(), table.getCollectionName());
	}

}
