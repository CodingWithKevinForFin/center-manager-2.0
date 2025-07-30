package com.f1.ami.plugins.sqlserver;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class AmiSqlServerDatasourceAdapter2 extends JdbcAdapter implements AmiDatasourceAdapter {
	public static final String OPTION_URL_SUFFIX = "URL_SUFFIX";
	private static final Logger log = LH.get(AmiSqlServerDatasourceAdapter2.class);

	@Override
	protected String createLimitClause(String select, int limit) {
		return "SET ROWCOUNT " + limit + "  " + select;
	}
	@Override
	protected StringBuilder createLimitClause2(StringBuilder query, int limit, Integer offset) {
		String q = SH.toStringAndClear(query);
		query.append("SET ROWCOUNT ").append(limit).append(" ").append(q);
		return query;
	}
	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		return sb.append("sp_tables");
	}
	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);

		//Get name of Table and the Collection(Schema,Owner) it's in
		String name = SH.trim(row.get("TABLE_NAME", Caster_String.INSTANCE));
		String collectionName = SH.trim(row.get("TABLE_OWNER", Caster_String.INSTANCE));
		String type = SH.trim(row.get("TABLE_TYPE", Caster_String.INSTANCE));

		if ("SYSTEM TABLE".equals(type))
			return null;
		if ("VIEW".equals(type))
			return null;

		//End
		table.setName(name);
		table.setCollectionName(collectionName);

		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));

		return table;
	}

	@Override
	protected StringBuilder createPreviewQuery(StringBuilder sb, String fullname, int limit) {
		sb.append("SELECT TOP ").append(limit).append(" * FROM ").append(fullname);
		return sb;
	}
	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		return getSchemaName('.', '"', sb, table.getName(), table.getCollectionName());
	}

	//	@Override
	//	protected String toJdbcUrl(ContainerTools tools, String name, String url, String username, String password, String options) {
	//		String passthrough = getOption(OPTION_URL_SUFFIX, "");
	//		return "com.microsoft.sqlserver.jdbc.SQLServerDriver:{" + SH.noNull("") + "}:jdbc:sqlserver://" + url + ";user=" + username + ";password=****" + OH.noNull(passthrough, "");
	//	}
	@Override
	protected String buildJdbcDriverClass() {
		return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	}
	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return CH.m("user", getUsernameEncoded(), "password", getPassword());
	}
	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:sqlserver://";
	}
	@Override
	protected String buildJdbcUrl() {
		return getUrl() + ";";
	}
	@Override
	protected String buildJdbcUrlPassword() {
		return null;
	}

}
