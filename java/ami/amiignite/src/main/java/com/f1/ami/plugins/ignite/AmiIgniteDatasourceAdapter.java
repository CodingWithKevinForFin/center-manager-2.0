package com.f1.ami.plugins.ignite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.DBH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiIgniteDatasourceAdapter extends JdbcAdapter {
	private static final Logger log = LH.get();
	private static final int MAX_CELLS = 1000;

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		return r;
	}

	@Override
	protected String buildJdbcDriverClass() {
		return "org.apache.ignite.IgniteJdbcThinDriver";
	}
	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return Collections.EMPTY_MAP;
	}
	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:ignite:thin://";
	}
	@Override
	protected String buildJdbcUrl() {
		if (SH.is(getUsernameEncoded()))
			return getUrl() + ";user=" + getUsernameEncoded() + ";password=****";
		else
			return getUrl();
	}
	@Override
	protected String buildJdbcUrlPassword() {
		return getPasswordEncoded();
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		return getSchemaName('.', '`', sb, table.getName(), table.getCollectionName());
	}
	@Override
	protected Table execShowTablesQuery(StringBuilder sb, Connection conn, int limit, AmiDatasourceTracker debugSink, TimeoutController tc) throws Exception {
		ResultSet t = conn.getMetaData().getTables(null, null, null, null);
		Table tables = DBH.toTable(t);
		return tables;
	}
	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		String schema = SH.trim(row.get("TABLE_SCHEM", Caster_String.INSTANCE));
		if (SH.equals("SYS", schema))
			return null;
		String name = SH.trim(row.get("TABLE_NAME", Caster_String.INSTANCE));
		table.setName(name);
		table.setCollectionName(schema);

		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));
		return table;
	}

	@Override
	protected String formatTargetTableName(AmiCenterUploadTable ul) {
		StringBuilder sql = new StringBuilder();
		String targetTable = ul.getTargetTable();
		String[] split = SH.split('.', targetTable);
		for (int i = 0; i < split.length; i++) {
			if (i != 0)
				sql.append('.');
			SH.quote('`', split[i], sql);
		}
		targetTable = SH.toStringAndClear(sql);
		return targetTable;
	}
}
