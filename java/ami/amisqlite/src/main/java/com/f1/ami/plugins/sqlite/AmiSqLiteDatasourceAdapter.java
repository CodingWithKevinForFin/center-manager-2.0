package com.f1.ami.plugins.sqlite;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class AmiSqLiteDatasourceAdapter extends JdbcAdapter {
	private static final Logger log = LH.get();
	private static final int MAX_CELLS = 1000;

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		return r;
	}

	@Override
	protected String buildJdbcDriverClass() {
		return "org.sqlite.JDBC";
	}

	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return Collections.EMPTY_MAP;
	}

	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:sqlite:";
	}
	@Override
	protected String buildJdbcUrlPassword() {
		return null;
	}
	@Override
	protected String buildJdbcUrl() {
		return getUrl();
	}

	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		sb.append("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%'");
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

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		// TODO Auto-generated method stub -- needs fullname
		return getSchemaName('.', '`', sb, table.getName());
	}

	protected boolean supportsGeneratedKeys() {
		return false;
	}

}
