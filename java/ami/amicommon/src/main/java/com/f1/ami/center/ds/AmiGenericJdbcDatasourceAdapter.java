package com.f1.ami.center.ds;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiGenericJdbcDatasourceAdapter extends JdbcAdapter {

	private static final String OPTION_PREVIEW_TABLE_CLAUSE = "PREVIEW_TABLE_CLAUSE";
	private static final String OPTION_SUGGESTED_CLAUSE = "SUGGESTED_CLAUSE";
	private static final String OPTION_SHOW_TABLES_CLAUSE = "SHOW_TABLES_CLAUSE";

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		r.remove(OPTION_URL_SUFFIX);
		r.remove(OPTION_URL_OVERRIDE);
		r.put(OPTION_PREVIEW_TABLE_CLAUSE, "");
		r.put(OPTION_SUGGESTED_CLAUSE, "");
		r.put(OPTION_SHOW_TABLES_CLAUSE, "");
		return r;
	}

	@Override
	protected StringBuilder createSelectQuery(StringBuilder sb, String fullname) {
		String suggested = this.getOption(OPTION_SUGGESTED_CLAUSE, "");
		return sb.append(SH.replaceAll(suggested, "${TABLE}", fullname));
	}

	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		String showTables = this.getOption(OPTION_SHOW_TABLES_CLAUSE, "");
		sb.append(showTables);
		String limitString = limit > 0 ? " limit " + limit : "";
		sb.append(limitString);
		return sb;
	}

	@Override
	protected Table execShowTablesQuery(StringBuilder sb, Connection conn, int limit, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		createShowTablesQuery(SH.clear(sb), limit);
		Table rs = exec(conn, SH.toStringAndClear(sb), limit, debugSink, tc);
		return rs;
	}

	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		String name = SH.trim(row.getAt(0, Caster_String.INSTANCE));
		table.setName(name);

		return table;
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>();
		String showTables = this.getOption(OPTION_SHOW_TABLES_CLAUSE, "");
		if (SH.is(showTables)) {
			String suggested = this.getOption(OPTION_SUGGESTED_CLAUSE, "");
			int tlimit = getOption(OPTION_SCHEMA_TABLE_LIMIT, NO_LIMIT);
			Connection conn = null;
			try {
				if (tlimit > 0 || tlimit == NO_LIMIT) {
					conn = getConnection();
					StringBuilder sb = new StringBuilder();
					Table tables = execShowTablesQuery(sb, conn, tlimit, debugSink, tc);

					TableList rows = tables.getRows();
					int rowsSize = rows.size();
					for (int i = 0; i < rowsSize; i++) {
						Row row = rows.get(i);
						AmiDatasourceTable table = createAmiDatasourceTable(row, sb);
						if (table == null)
							continue;

						if (SH.is(suggested)) {
							String fullname = getSchemaName(SH.clear(sb), table);
							createSelectQuery(sb, fullname);
							table.setCustomQuery(SH.toStringAndClear(sb));
						}
						r.add(table);
					}
				}
				return r;
			} finally {
				IOH.close(conn);
			}
		}
		return r;
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		return table.getName();
	}
	@Override
	protected StringBuilder createPreviewQuery(StringBuilder sb, String fullname, int limit) {
		String showTables = this.getOption(OPTION_PREVIEW_TABLE_CLAUSE, "");
		showTables = SH.replaceAll(showTables, "${LIMIT}", SH.toString(limit));
		showTables = SH.replaceAll(showTables, "${TABLE}", fullname);
		return sb.append(showTables);
	}
	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		String showTables = this.getOption(OPTION_PREVIEW_TABLE_CLAUSE, "");
		if (SH.is(showTables)) {
			if (showTables.indexOf("${LIMIT}") == -1)
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Option PREVIEW_TABLE_CLAUSE missing substitution: ${LIMIT}");
			if (showTables.indexOf("${TABLE}") == -1)
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Option PREVIEW_TABLE_CLAUSE missing substitution: ${TABLE}");
			return super.getPreviewData(tables, previewCount, debugSink, tc);
		}
		return tables;
	}

	@Override
	protected String buildJdbcDriverClass() {
		String driver = this.getOption(OPTION_DRIVER_CLASS, "");
		return driver;
	}

	@Override
	protected Map<String, Object> buildJdbcArguments() {
		String props = this.getOption(OPTION_DRIVER_ARGUMENTS, "");
		Map<String, Object> r = new HashMap<String, Object>();
		r.putAll(SH.splitToMap(',', '=', '\\', props));
		return r;
	}

	@Override
	protected String buildJdbcUrlSubprotocol() {
		return this.getOption(OPTION_DRIVER_SUBPROTOCOL, "");
	}

	@Override
	protected String buildJdbcUrl() {
		String url = getUrl();
		url = SH.replaceAll(url, "${USERNAME}", getUsername());
		url = SH.replaceAll(url, "${PASSWORD}", "****");
		return url;
	}
	@Override
	protected String buildJdbcUrlPassword() {
		return super.getPasswordEncoded();
	}
	@Override
	protected boolean supportsGeneratedKeys() {
		return false;
	}

}
