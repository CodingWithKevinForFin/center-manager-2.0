package com.f1.ami.center.ds;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.ds.AmiTableResultSet;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiAmiDbDatasourceAdapter extends JdbcAdapter {

	public static final String OPTION_URL_TIMEOUT = "TIMEOUT";
	public static final String OPTION_LEGACY_VERSION = "legacyVersion";
	public static final String OPTION_IS_SECURE = "SSL Connection";
	public static final String OPTION_SSL_KEYSTORE = "SSL Keystore";
	public static final String OPTION_SSL_KEYSTORE_PASS = "SSL Keystore Password";

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		r.put(OPTION_URL_TIMEOUT, "timeout in milliseconds");
		r.put(OPTION_LEGACY_VERSION, "legacy version for backwards compatibility with older amidb versions (starting at 1)");
		r.put(OPTION_SSL_KEYSTORE, "File path to jks keystore file");
		r.put(OPTION_IS_SECURE, "Determines if JDBC connection is secure");
		r.put(OPTION_SSL_KEYSTORE_PASS, "Password for jks keystore file");
		return r;
	}

	@Override
	protected StringBuilder createLimitClause2(StringBuilder query, int limit, Integer offset) {
		query.append(" LIMIT ");
		if (offset != null)
			query.append(offset).append(',');
		query.append(limit);

		return query;
	}
	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		sb.append("SELECT * FROM __COLUMN");
		return sb;
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		return getSchemaName('.', '`', sb, table.getName());
	}
	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		Connection con = getConnection();
		try {
			StringBuilder sb = new StringBuilder();
			Table t = this.execShowTablesQuery(sb, con, NO_LIMIT, debugSink, tc);
			List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>();
			Map<String, AmiDatasourceTable> m = new HashMap<String, AmiDatasourceTable>();
			for (Row row : t.getRows()) {
				String tableName = row.get("TableName", Caster_String.INSTANCE);
				String columnName = row.get("ColumnName", Caster_String.INSTANCE);
				String definedBy = row.get("DefinedBy", Caster_String.INSTANCE);
				String dataType = row.get("DataType", Caster_String.INSTANCE);
				AmiDatasourceTable dt = m.get(tableName);
				if (dt == null) {
					dt = tools.nw(AmiDatasourceTable.class);
					dt.setCollectionName(definedBy);
					dt.setName(tableName);

					String fullname = getSchemaName(SH.clear(sb), dt);
					createSelectQuery(sb, fullname);

					dt.setCustomQuery(SH.toStringAndClear(sb));

					m.put(tableName, dt);
					r.add(dt);
					dt.setColumns(new ArrayList<AmiDatasourceColumn>());
				}
				AmiDatasourceColumn col = tools.nw(AmiDatasourceColumn.class);
				col.setName(columnName);
				col.setType(AmiUtils.parseTypeName(dataType));
				dt.getColumns().add(col);
			}
			return r;
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, e.getMessage());
		} finally {
			IOH.close(con);
		}

	}

	@Override
	protected void applyTimeout(Statement ps, int timeoutMillisRemaining) {
		if (ps instanceof AmiStatement) {
			final AmiStatement ps2 = (AmiStatement) ps;
			ps2.setQueryTimeoutMillis(Math.max(1, timeoutMillisRemaining));
		} else {
			super.applyTimeout(ps, timeoutMillisRemaining);
		}
	}

	@Override
	protected StringBuilder createCountQuery(StringBuilder sb, String fullname) {
		sb.append("SELECT count(*) FROM ");
		sb.append(fullname);
		return sb;
	}
	//	@Override
	//	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
	//			throws AmiDatasourceException {
	//		Connection conn = null;
	//		try {
	//			conn = getConnection();
	//			int tablesSize = tables.size();
	//			StringBuilder sb = new StringBuilder();
	//			for (int i = 0; i < tablesSize; i++) {
	//				AmiDatasourceTable table = tables.get(i);
	//				String fullname = getSchemaName(SH.clear(sb), table);
	//
	//				// SELECT QUERY
	//				createPreviewQuery(SH.clear(sb), fullname, previewCount);
	//				Table rs = exec(conn, SH.toStringAndClear(sb), previewCount, debugSink, tc);
	//
	//				// COUNT QUERY
	//				createCountQuery(SH.clear(sb), fullname);
	//				long size = getTableSize(conn, SH.toStringAndClear(sb), debugSink, tc);
	//				List<Column> rscols = rs.getColumns();
	//
	//				table.setColumns(new ArrayList<AmiDatasourceColumn>(rscols.size()));
	//				int rscolsSize = rscols.size();
	//				for (int j = 0; j < rscolsSize; j++) {
	//					Column rscol = rscols.get(j);
	//					AmiDatasourceColumn col = tools.nw(AmiDatasourceColumn.class);
	//					col.setName(SH.trim(rscol.getId().toString()));
	//					col.setType(AmiUtils.getTypeForClass(rscol.getType(), AmiDatasourceColumn.TYPE_UNKNOWN));
	//					table.getColumns().add(col);
	//				}
	//				table.setPreviewData(rs);
	//				table.setPreviewTableSize(size);
	//			}
	//			return tables;
	//		} finally {
	//			IOH.close(conn);
	//		}
	//	}

	@Override
	public Table toTable(ResultSet result, char tableNameColumnDelim, int limit) throws SQLException {
		return result == null ? null : ((AmiTableResultSet) result).getUnderlyingTable();
	}

	@Override
	protected String buildJdbcDriverClass() {
		return "com.f1.ami.amidb.jdbc.AmiDbJdbcDriver";
	}

	@Override
	protected Map<String, Object> buildJdbcArguments() {
		Map<String, Object> r = CH.m("username", getUsername(), "password", getPassword());
		Integer timeout = getOption(OPTION_URL_TIMEOUT, AmiConsts.DEFAULT);
		String legacyVersion = getOption(OPTION_LEGACY_VERSION, "");
		Boolean isSecure = "true".equalsIgnoreCase(getOption(OPTION_IS_SECURE, "false"));
		if (timeout != AmiConsts.DEFAULT)
			r.put("timeout", SH.toString(timeout));
		if (SH.is(legacyVersion))
			r.put("legacyVersion", legacyVersion);
		r.put("isSecure", isSecure);
		if (isSecure) {
			String sslKeystore = getOption(OPTION_SSL_KEYSTORE, "");
			String sslKeystorePass = getOption(OPTION_SSL_KEYSTORE_PASS, "");
			if (SH.is(sslKeystore))
				r.put("sslKeystore", sslKeystore);
			if (SH.is(sslKeystorePass))
				r.put("sslKeystorePass", sslKeystorePass);
		}
		return r;
	}

	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:amisql:";
	}

	@Override
	protected String buildJdbcUrl() {
		return super.getUrl();
	}

	@Override
	protected String buildJdbcUrlPassword() {
		return null;
	}

	private static final int MAX_CELLS = 1000 * 1000 * 10;

	@Override
	public void processUpload(AmiCenterUpload request, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		FastJdbcConnection conn = (FastJdbcConnection) getConnection();
		if (conn.supportFastInsert()) {
			try {
				for (AmiCenterUploadTable i : request.getData()) {
					Table table = i.getData();
					int columnsCount = table.getColumnsCount();
					int batchSize = MAX_CELLS / columnsCount;
					if (request.getDirectives().containsKey("batchsize")) {
						batchSize = AmiDatasourceUtils.getOptionalInt(request.getDirectives(), "batchsize");
						if (batchSize < 1)
							throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_batchsize shoud be positive number: " + batchSize);
					}
					if (batchSize < 1)
						batchSize = 1;
					int rowsCount = table.getSize();
					if (rowsCount <= batchSize)
						conn.fastInsert(i.getTargetTable(), i.getTargetColumns(), table, request.getTimeout().getTimeoutMillisRemaining());
					else {
						for (int start = 0; start < rowsCount; start += batchSize) {
							int end = Math.min(start + batchSize, rowsCount);
							ColumnarTable table2 = new ColumnarTable((Table) table, start, end);
							conn.fastInsert(i.getTargetTable(), i.getTargetColumns(), table2, request.getTimeout().getTimeoutMillisRemaining());
						}
					}
				}
			} catch (SQLException e) {
				throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Remote INSERT Error: " + e.getMessage(), e);
			} finally {
				IOH.close(conn);
			}
		} else {
			LH.info(log, "Target AMIDB '" + this.getServiceLocator().getTargetName(), "' does not support fastInsert. Please upgrade the target ami version!");
			super.processUpload(request, results, tracker);
		}
	}

}
