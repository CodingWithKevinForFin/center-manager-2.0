package com.f1.ami.plugins.vertica;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class AmiVerticaDatasourceAdapter extends JdbcAdapter {
	private static final Logger log = LH.get();
	private static final int MAX_CELLS = 1000;

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		return r;
	}
	@Override
	public String buildJdbcUrl() {
		if (SH.is(getUsernameEncoded()))
			return getUrl() + "?user=" + getUsernameEncoded() + "&password=****";
		else
			return getUrl();
	}
	@Override
	protected String buildJdbcDriverClass() {
		return "com.vertica.jdbc.Driver";
	}
	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return Collections.EMPTY_MAP;
	}
	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:vertica://";
	}
	@Override
	protected String buildJdbcUrlPassword() {
		return getPasswordEncoded();
	}

	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		String limitString = limit > 0 ? " limit " + limit : "";
		sb.append("SELECT table_name,schema_name,table_type FROM all_tables where table_type !='SYSTEM TABLE'");
		sb.append(limitString);
		return sb;
	}

	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);

		String name = SH.trim(row.getAt(0, Caster_String.INSTANCE));
		String schema = SH.trim(row.getAt(1, Caster_String.INSTANCE));
		table.setName(name);
		table.setCollectionName(schema);

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
	protected StringBuilder createPreviewQuery(StringBuilder sb, String fullname, int limit) {
		sb.append(" SELECT * FROM ").append(fullname);
		createLimitClause2(sb, limit, null);
		return sb;
	}

	@Override
	protected void formatTargetColumnName(String c, StringBuilder sql) {
		sql.append('"').append(c).append('"');
	}

	@Override
	protected String formatTargetTableName(AmiCenterUploadTable ul) {
		StringBuilder sql = new StringBuilder();
		String targetTable = ul.getTargetTable();
		String[] split = SH.split('.', targetTable);
		for (int i = 0; i < split.length; i++) {
			if (i != 0)
				sql.append('.');
			SH.quote('"', split[i], sql);
		}
		targetTable = SH.toStringAndClear(sql);
		return targetTable;
	}

	@Override
	protected boolean supportsGeneratedKeys() {
		return false;
	}
}
