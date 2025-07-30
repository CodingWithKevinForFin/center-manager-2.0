package com.f1.ami.plugins.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.DBH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.db.ResultSetGetter;

public class AmiOracleDatasourceAdapter extends JdbcAdapter {
	private static final int MAX_CELLS = 1000;

	private static final Logger log = LH.get();

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		return r;
	}
	@Override
	protected String buildJdbcDriverClass() {
		return "oracle.jdbc.driver.OracleDriver";
	}
	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return Collections.EMPTY_MAP;
	}
	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:oracle:thin:";
	}
	@Override
	protected String buildJdbcUrl() {
		return getUsernameEncoded() + "/****@" + getUrl();
	}
	@Override
	protected String buildJdbcUrlPassword() {
		return SH.doubleQuote(getPassword());
	}

	static final Map<String, Byte> DATA_TYPES = new HashMap<String, Byte>();
	static {
		DATA_TYPES.put("tinyint", AmiDatasourceColumn.TYPE_INT);
		DATA_TYPES.put("smallint", AmiDatasourceColumn.TYPE_INT);
		DATA_TYPES.put("mediumint", AmiDatasourceColumn.TYPE_INT);
		DATA_TYPES.put("int", AmiDatasourceColumn.TYPE_INT);

		DATA_TYPES.put("integer", AmiDatasourceColumn.TYPE_LONG);
		DATA_TYPES.put("bigint", AmiDatasourceColumn.TYPE_LONG);
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
		DATA_TYPES.put("varchar2", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("tinytext", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("text", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("mediumtext", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("longtext", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("enum", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("set", AmiDatasourceColumn.TYPE_STRING);
		DATA_TYPES.put("bit", AmiDatasourceColumn.TYPE_BOOLEAN);
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

	private byte toColumnType(String type) {
		if (type == null)
			return AmiDatasourceColumn.TYPE_NONE;
		if (type.contains("CHAR") || type.contains("ROWID"))
			return AmiDatasourceColumn.TYPE_STRING;
		if (type.contains("NUMBER") || type.contains("FLOAT") || type.contains("DOUBLE") || type.contains("REAL"))
			return AmiDatasourceColumn.TYPE_DOUBLE;
		if (type.contains("INTERVAL"))
			return AmiDatasourceColumn.TYPE_STRING;
		if (type.contains("INT"))
			return AmiDatasourceColumn.TYPE_INT;
		if (type.contains("LONG") || type.contains("BLOB") || type.contains("RAW"))
			return AmiDatasourceColumn.TYPE_BINARY;
		if (type.contains("CLOB"))
			return AmiDatasourceColumn.TYPE_STRING;
		if (type.contains("TIME") || type.contains("DATE"))
			return AmiDatasourceColumn.TYPE_LONG;
		return AmiDatasourceColumn.TYPE_STRING;

	}
	protected String getDefaultWhereStatement() {
		return "1=1";
	}
	protected String createLimitClause(String select, int limit) {
		StringBuilder sb = new StringBuilder();
		int index = SH.indexOfIgnoreCase(select, "where", 0);
		if (index == -1) {
			sb.append(select).append(" WHERE ROWNUM <= ").append(limit);
			return sb.toString();
		} else {
			sb.append(SH.substring(select, 0, index));
			sb.append("WHERE (ROWNUM <= ").append(limit).append(") AND");
			sb.append(SH.substring(select, index + 5, select.length()));
			return sb.toString();
		}
	}

	@Override
	protected StringBuilder createLimitClause2(StringBuilder sb, int limit, Integer offset) {
		int index = SH.indexOfIgnoreCase(sb, "where", 0);
		if (index == -1)
			sb.append(" WHERE ROWNUM <= ").append(limit);
		else
			sb.insert(index + 5, " (ROWNUM <= " + limit + ") AND");
		return sb;

	}
	@Override
	protected ResultSetGetter resolveSpecialGetter(Class clazz) {
		if (clazz == oracle.sql.INTERVALDS.class || clazz == oracle.sql.INTERVALYM.class)
			return ResultSetGetter.StringResultSetGetter.INSTANCE;
		if (clazz.getName().startsWith(oracle.sql.TIMESTAMP.class.getName()))
			return ResultSetGetter.DateMillisResultSetGetter.INSTANCE;
		return super.resolveSpecialGetter(clazz);
	}

	//	public static class IntervalResultSetGetter extends ResultSetGetter<Long> {
	//
	//		public static final IntervalResultSetGetter INSTANCE = new IntervalResultSetGetter();
	//
	//		private IntervalResultSetGetter() {
	//		}
	//
	//		@Override
	//		protected Long getInner(ResultSet rs, int field) throws SQLException {
	//			INTERVALDS t = (INTERVALDS) rs.getObject(field);
	//			if (t == null)
	//				return null;
	//			return t.dateValue().getTime();
	//		}
	//
	//		@Override
	//		public void set(PreparedStatement ps, int parameterIndex, Long value) throws SQLException {
	//			if (value == null)
	//				ps.setNull(parameterIndex, java.sql.Types.BIGINT);
	//			else
	//				ps.setLong(parameterIndex, value.longValue());
	//		}
	//
	//	}
	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		String limitString = limit > 0 ? " WHERE ROWNUM <= " + limit : "";
		sb.append("SELECT OWNER, TABLE_NAME, TABLESPACE_NAME from ALL_TABLES");
		sb.append(limitString);
		//		createLimitClause2(sb, limit, null);
		return sb;
	}

	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);

		//Get name of Table and the Collection(Schema,Owner) it's in
		String name = SH.trim(row.get("TABLE_NAME", Caster_String.INSTANCE));
		String collectionName = SH.trim(row.get("OWNER", Caster_String.INSTANCE));
		String tablespace = SH.trim(row.get("TABLESPACE_NAME", Caster_String.INSTANCE));
		if ("SYSTEM".equals(tablespace))
			return null;
		if ("SYSAUX".equals(tablespace))
			return null;
		//End
		table.setName(name);
		table.setCollectionName(collectionName);

		//Set Custom Query 
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
	public void processUpload(AmiCenterUpload request, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		Connection conn = getConnection();
		try {
			int dBatchSize = -1;
			if (request.getDirectives().containsKey("batchsize")) {
				dBatchSize = AmiDatasourceUtils.getOptionalInt(request.getDirectives(), "batchsize");
				if (dBatchSize < 1)
					throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_batchsize shoud be positive number: " + dBatchSize);
			}
			for (AmiCenterUploadTable ul : request.getData()) {
				StringBuilder sql = new StringBuilder();
				Table table = (Table) ul.getData();
				int columnsCount = table.getColumnsCount();
				int batchSize = dBatchSize != -1 ? dBatchSize : MAX_CELLS / columnsCount;
				if (batchSize < 1)
					batchSize = 1;
				ResultSetGetter<Object>[] rsg = DBH.getColumnGetters(TableHelper.getColumnTypesArray(table));
				List<List<Row>> batches = CH.batchSublists(table.getRows(), batchSize, false);
				String targetTable = ul.getTargetTable();
				LH.info(log, "Inserting ", table.getRows().size(), " row(s) x ", columnsCount, " columns(s) into ", targetTable, " using ", batches.size(), " batch(es)");
				int n = 0;
				for (List<Row> rows : batches) {
					int rowsCount = rows.size();
					SH.clear(sql);
					SH.repeat(",?", columnsCount - 1, sql.append('?'));
					String values = SH.toStringAndClear(sql);
					sql.append("INSERT INTO ").append(targetTable).append(" ");

					if (ul.getTargetColumns() != null) {
						boolean first = true;
						sql.append("(");
						for (String c : ul.getTargetColumns()) {
							if (first)
								first = false;
							else
								sql.append(',');
							sql.append("").append(c).append("");
						}
						sql.append(")");
					}

					for (int i = 0; i < rowsCount; i++) {
						sql.append("\n   ");
						if (i > 0)
							sql.append(" UNION ALL ");
						sql.append("SELECT ");
						sql.append(values);
						sql.append(" FROM DUAL  ");
					}
					PreparedStatement ps;
					ps = conn.prepareStatement(sql.toString());
					int j = 1;
					for (Row r : rows)
						for (int c = 0; c < columnsCount; c++)
							rsg[c].set(ps, j++, table.getAt(r.getLocation(), c));
					ps.execute();
					LH.info(log, "Finished batch ", n, ": ", rowsCount, " row(s)");
					n++;
				}
			}
		} catch (SQLException e) {
			throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Remote INSERT Error", e);
		} finally {
			IOH.close(conn);
		}
	}
}
