package com.f1.ami.plugins.cloudera.impala;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiImpalaDatasourceAdapter extends JdbcAdapter {

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		return r;
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		return getSchemaName('.', null, sb, table.getName(), table.getCollectionName());
	}
	@Override
	protected Table execShowTablesQuery(StringBuilder sb, Connection conn, int limit, AmiDatasourceTracker debugSink, TimeoutController tc) throws Exception {
		int rlimit = limit == NO_LIMIT ? Integer.MAX_VALUE : limit;
		ResultSet rs = conn.getMetaData().getTables(null, null, null, new String[] { "TABLE", "VIEW", "SYSTEM TABLE" });
		Table tables = toTable(rs, '_', rlimit);
		return tables;
	}
	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		String schem = SH.trim(row.getAt(1, Caster_String.INSTANCE));
		String name = SH.trim(row.getAt(2, Caster_String.INSTANCE));
		table.setCollectionName(schem);
		table.setName(name);
		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));
		return table;
	}

	@Override
	protected String buildJdbcDriverClass() {
		return "com.cloudera.impala.jdbc41.Driver";
	}
	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return Collections.EMPTY_MAP;
	}
	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:impala://";
	}
	@Override
	protected String buildJdbcUrl() {
		return getUrl() + ";UID=" + getUsernameEncoded() + ";PWD=****;";
	}
	@Override
	protected String buildJdbcUrlPassword() {
		return getPasswordEncoded();
	}
	@Override
	protected boolean supportsGeneratedKeys() {
		return false;
	}
}
