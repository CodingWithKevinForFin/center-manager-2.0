package com.f1.ami.plugins.apache.spark;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.DateMillis;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.DBH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.db.ResultSetGetter;
import com.f1.utils.db.ResultSetGetter.ObjectResultSetGetter;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiSparkDatasourceAdapter extends JdbcAdapter implements AmiDatasourceAdapter {
	public static final String OPTION_FULL_COLUMN_NAMES = "FULL_COLUMN_NAMES";

	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		sb.append("show tables");
		return sb;
	}
	@Override
	protected Table execShowTablesQuery(StringBuilder sb, Connection conn, int limit, AmiDatasourceTracker debugSink, TimeoutController tc) throws Exception {
		//		createShowTablesQuery(SH.clear(sb), limit);
		//		Table rs = exec(conn, SH.toStringAndClear(sb), limit, debugSink, tc);
		//		return rs;
		int rlimit = limit == NO_LIMIT ? Integer.MAX_VALUE : limit;
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("show tables");
		Table tables = toTable(rs, '_', rlimit);
		return tables;
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		//		return getSchemaName('.', '`', sb, table.getName(), table.getCollectionName());
		//TODO: Get full schema name
		return getSchemaName('.', null, sb, table.getName());
	}

	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);

		String schem = null;
		String name = "";
		name = SH.trim(row.getAt(0, Caster_String.INSTANCE));
		table.setCollectionName(schem);
		table.setName(name);

		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));
		return table;
	}

	/**
	 * HiveResultSetMetaData doesn't support getTableName
	 */
	@Override
	public Table toTable(ResultSet result, char tableNameColumnDelim, int limit) throws SQLException {
		boolean fullColumnNames = getOption(AmiSparkDatasourceAdapter.OPTION_FULL_COLUMN_NAMES, false);
		String names[] = DBH.getUniqueColumnNames(result.getMetaData(), tableNameColumnDelim);

		if (fullColumnNames == false) {
			boolean noTablePrefix = false;
			// Get Table Names
			Set<String> tableNames = new TreeSet<String>();
			for (int i = 0; i < names.length; i++) {
				String name = (String) names[i];
				int found = SH.indexOf(name, '.', 0);
				if (found != -1) {
					tableNames.add(SH.beforeFirst(name, '.'));
				} else
					noTablePrefix = true;
			}
			// Update column names
			if (tableNames.size() == 1 && noTablePrefix == false) {
				String tableName = tableNames.iterator().next();
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					// Ensure the column name is tableName.columnName
					if (SH.startsWith(name, tableName))
						// Update the name to be columnName
						names[i] = SH.afterFirst(name, '.', name);
				}
			}
		}

		Set<String> namesSet = null;
		for (int i = 0; i < names.length; i++) {
			String name = (String) names[i];
			String name2 = toValidVarName(name);
			if (OH.ne(name, name2)) {
				names[i] = name;
				if (namesSet == null)
					namesSet = CH.s(names);
				name2 = SH.getNextId(name2, namesSet);
				namesSet.remove(name);
				namesSet.add(name2);
				names[i] = name2;
			}
		}
		Class<?>[] types = DBH.getColumnTypes(result.getMetaData());
		ResultSetGetter[] getters = DBH.getColumnGetters(types);
		for (int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			if (type == java.sql.Date.class) {
				types[i] = DateMillis.class;
				getters[i] = new SparkDateMillisResultSetGetter();
				getters[i].setField(i + 1);
			} else if (type == java.sql.Timestamp.class || type == java.sql.Time.class) {
				types[i] = DateMillis.class;
				getters[i] = new ResultSetGetter.DateMillisResultSetGetter();
				getters[i].setField(i + 1);
			} else if (type == BigDecimal.class) {
				types[i] = Double.class;
				getters[i] = new ResultSetGetter.DoubleResultSetGetter();
				getters[i].setField(i + 1);
			} else if (type == BigInteger.class) {
				types[i] = Long.class;
				getters[i] = new ResultSetGetter.LongResultSetGetter();
				getters[i].setField(i + 1);
			} else if (Clob.class.isAssignableFrom(type)) {
				types[i] = String.class;
				getters[i] = new ResultSetGetter.StringResultSetGetter();
				getters[i].setField(i + 1);
			} else if (Blob.class.isAssignableFrom(type)) {
				types[i] = byte[].class;
				getters[i] = new ResultSetGetter.ByteArrayResultSetGetter();
				getters[i].setField(i + 1);
			} else if (getters[i] instanceof ObjectResultSetGetter) {
				Tuple2<Class, ResultSetGetter> t = resolveSpecialGetter((ObjectResultSetGetter) getters[i]);
				if (t == null) {
					types[i] = String.class;
					getters[i] = new ResultSetGetter.StringResultSetGetter();
					getters[i].setField(i + 1);
				} else {
					types[i] = t.getA();
					getters[i] = t.getB();
					getters[i].setField(i + 1);
				}

			}

		}
		return DBH.toTable(result, types, getters, names, limit, DBH.TABLE_OPTIONS_SKIP_TITLE);
	}
	@Override
	protected StringBuilder createPreviewQuery(StringBuilder sb, String fullname, int limit) {
		sb.append("SELECT * FROM ").append(fullname).append(" limit ").append(limit);
		return sb;
	}

	public static class SparkDateMillisResultSetGetter extends ResultSetGetter<DateMillis> {

		@Override
		protected DateMillis getInner(ResultSet rs) throws SQLException {
			Date date = rs.getDate(field);
			if (date == null)
				return null;
			return new DateMillis(date.getTime());
		}

	}

	@Override
	protected String toJdbcUrl(ContainerTools tools, String name, String url, String username, String password, String options) {
		String passthrough = getOption(OPTION_URL_SUFFIX, "");
		if (SH.equals("", username))
			return "org.apache.hive.jdbc.HiveDriver:{}:jdbc:hive2://" + url + "****" + OH.noNull(passthrough, "");
		else
			return "org.apache.hive.jdbc.HiveDriver:{}:jdbc:hive2://" + url + "user=" + username + ";password=" + "****" + OH.noNull(passthrough, "");
	}
}
