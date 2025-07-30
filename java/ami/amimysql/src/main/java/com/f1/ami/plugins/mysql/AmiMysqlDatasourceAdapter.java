package com.f1.ami.plugins.mysql;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class AmiMysqlDatasourceAdapter extends JdbcAdapter {
	private static final Logger log = LH.get();
	private static final int MAX_CELLS = 1000;

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		return r;
	}

	@Override
	protected String buildJdbcDriverClass() {
		return "com.mysql.jdbc.Driver";
	}

	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return CH.m("user", getUsernameEncoded(), "password", getPassword(), "zeroDateTimeBehavior", "convertToNull");
	}

	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:mysql://";
	}

	@Override
	protected String buildJdbcUrl() {
		return getUrl() + "?";
	}
	@Override
	protected String buildJdbcUrlPassword() {
		return null;
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		//TODO: see if mysql adapter has a collection name
		return getSchemaName('.', '`', sb, table.getName());
	}

	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		String limitString = limit > 0 ? " limit " + limit : "";
		sb.append("show tables");
		sb.append(limitString);
		return sb;
	}

	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		String name = SH.trim(row.getAt(0, Caster_String.INSTANCE));
		table.setName(name);

		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));
		return table;
	}

}
