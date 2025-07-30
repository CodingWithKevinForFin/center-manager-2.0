package com.f1.ami.amicommon;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.base.Bytes;
import com.f1.base.Column;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.base.Valued;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.DBH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.db.ResultSetGetter;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public abstract class JdbcAdapter extends AmiDatasourceAbstractAdapter {
	private static final Pattern DIRECTIVE_PARAMS_MATCHER = Pattern.compile("^p[0-9]+$");
	private static final char DIRECTIVE_PARAMS_START = 'p';
	private static final String DIRECTIVE_PARAMS_LIST = "paramsList";
	public static final String QUERY_TYPE_SELECT = "SELECT";
	public static final String QUERY_TYPE_INSERT = "INSERT";
	public static final String QUERY_TYPE_UPDATE = "UPDATE";
	public static final String QUERY_TYPE_DELETE = "DELETE";
	public static final String QUERY_TYPE_TRUNCATE = "TRUNCATE";
	public static final String QUERY_TYPE_CREATE = "CREATE";
	public static final String QUERY_TYPE_ALTER = "ALTER";
	public static final String QUERY_TYPE_DROP = "DROP";

	protected static final Logger log = LH.get();

	public static final String OPTION_URL_SUFFIX = "URL_SUFFIX";
	public static final String OPTION_URL_OVERRIDE = "URL_OVERRIDE";//can supply ${USERNAME}  and ${PASSWORD} which get substituted
	public static final String OPTION_PREPEND_SCHEMA = "PREPEND_SCHEMA";
	public static final String OPTION_SCHEMA_COLUMN_LIMIT = "SCHEMA_COLUMN_LIMIT";
	public static final String OPTION_SCHEMA_TABLE_LIMIT = "SCHEMA_TABLE_LIMIT";
	public static final String OPTION_DISABLE_UTCN = "DISABLE_UTCN";
	public static final String OPTION_DISABLE_BIGINT = "DISABLE_BIGINT";
	public static final String OPTION_DISABLE_BIGDEC = "DISABLE_BIGDEC";
	public static final String OPTION_DRIVER_CLASS = "DRIVER_CLASS";
	public static final String OPTION_DRIVER_ARGUMENTS = "URL_ARGUMENTS";
	public static final String OPTION_DRIVER_SUBPROTOCOL = "URL_SUBPROTOCOL";
	public static final String OPTION_DISABLE_URLENCODE = "DISABLE_URLENCODE";

	public static final String SELECT_ALL_FROM_CLAUSE = "SELECT * FROM ";
	public static final String WHERE_CLAUSE = " WHERE ${WHERE}";
	public static final String SELECT_COUNT_ALL_FROM_CLAUSE = "SELECT count(*) FROM ";

	public static Map<String, String> buildOptions() {
		HashMap<String, String> r = new LinkedHashMap<String, String>();
		r.put(OPTION_URL_SUFFIX, "Extra Parameters");
		r.put(OPTION_URL_OVERRIDE,
				"Hard code URL, passed into first argument of java.sql.Driver::Connect(...) requirements. ${USERNAME} and ${PASSWORD} will be substituted accordingly(Url SubProtocol,Url and Url Suffix are ignored)");
		r.put(OPTION_PREPEND_SCHEMA, "If true, Include schema name in the table listing wizard (default is true)");
		r.put(OPTION_SCHEMA_COLUMN_LIMIT, "Limit columns returent in wizard");
		r.put(OPTION_SCHEMA_TABLE_LIMIT, "Limit tables returent in wizard");
		r.put(OPTION_DISABLE_UTCN, "If true, convert timestamps with precision greater than milliseconds into milliseconds(UTC type)");
		r.put(OPTION_DISABLE_BIGINT, "If true, convert bigintegers to longs");
		r.put(OPTION_DISABLE_BIGDEC, "If true, convert bigdecimals to doubles");
		r.put(OPTION_DRIVER_CLASS, "class implementing java.sql.Driver interface");
		r.put(OPTION_DRIVER_ARGUMENTS, "comma delimited key=value pair of properties to passed into second argument of java.sql.Driver::Connect(...)");
		r.put(OPTION_DRIVER_SUBPROTOCOL, "The subprotocol used to indicate the jdbc driver");
		r.put(OPTION_DISABLE_URLENCODE, "If true, disables url encoding username and password in jdbc url");
		return r;
	}

	/*
	 * getSchemaName will automatically pull if prependSchema has been set
	 * Arguments:
	 * delim: delim to use when joining the names
	 * quote: default quote to use
	 * sb : stringbuilder sink, will append to existing sink clear beforehand if needed
	 * names: table names in reverse order ex:
	 * 		Table, Schema, Catalog, Cluster 
	 */
	protected final String getSchemaName(char delim, Character quote, StringBuilder sb, String... names) {
		// If prependSchema isn't set default is false, but also don't throw an error
		boolean prependSchema = getOptionNoThrow(OPTION_PREPEND_SCHEMA, Boolean.FALSE);
		return getSchemaNameH(delim, quote, prependSchema, new StringBuilder(), names).toString();

	}
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		return getSchemaName('.', '"', sb, table.getName());
	}
	/*
	 * Arguments:
	 * delim: delim to use when joining the names
	 * quote: default quote to use
	 * prependSchema : whether to create a fully qualitifed name
	 * sb : stringbuilder sink, will append to existing sink clear beforehand if needed
	 * names: table names in reverse order ex:
	 * 		Table, Schema, Catalog, Cluster 
	 */
	protected static StringBuilder getSchemaNameH(char delim, Character quote, boolean prependSchema, StringBuilder sb, String... names) {
		if (names == null || names.length == 0)
			return null;

		if (names.length == 1 || !prependSchema)
			if (quote == null)
				return sb.append(names[0]);
			else
				return SH.quote(quote, names[0], sb);
		else {
			boolean needsDelim = false;
			for (int i = names.length - 1; i > -1; i--) {
				if (names[i] != null) {
					if (needsDelim == true)
						sb.append(delim);
					else
						needsDelim = true;
					if (quote == null)
						sb.append(names[i]);
					else
						SH.quote(quote, names[i], sb);
				}
			}

			return sb;
		}
	}

	//raw parameters from service locator
	private String password;
	private String username;

	//These are constructed during initialization 
	private String jdbcDriver;
	private String jdbcUrl;
	private String jdbcPassword;
	private Map<String, Object> jdbcArguments;

	private AtomicReference<Statement> statement = new AtomicReference<Statement>();

	private boolean disableBigInt;
	private boolean disableBigDec;
	private boolean disableUTCN;
	private boolean disableUrlEncode;

	protected String getPassword() {
		return password;
	}
	protected String getUsername() {
		return this.username;
	}
	protected String getPasswordEncoded() {
		return !disableUrlEncode ? SH.encodeUrl(password) : password;
	}
	protected String getUsernameEncoded() {
		return !disableUrlEncode ? SH.encodeUrl(this.username) : this.username;
	}

	protected String getUrl() {
		return getServiceLocator().getUrl();
	}

	protected boolean isDisableUrlEncode() {
		return this.disableUrlEncode;
	}

	@Override
	public void init(ContainerTools tools, AmiServiceLocator locator) throws AmiDatasourceException {
		super.init(tools, locator);
		this.password = locator.getPassword() == null ? null : new String(locator.getPassword());
		this.username = locator.getUsername();

		//driver
		this.jdbcDriver = this.getOption(OPTION_DRIVER_CLASS, "");
		if (SH.isnt(jdbcDriver))
			jdbcDriver = buildJdbcDriverClass();

		this.disableUrlEncode = getOption(OPTION_DISABLE_URLENCODE, Boolean.FALSE);
		//properties
		String propsOverride = this.getOption(OPTION_DRIVER_ARGUMENTS, "");
		if (SH.is(propsOverride)) {
			jdbcArguments = new HashMap<String, Object>();
			jdbcArguments.putAll(SH.splitToMap(',', '=', '\\', propsOverride));
		} else
			jdbcArguments = buildJdbcArguments();

		//url and password
		StringBuilder sb = new StringBuilder();
		this.jdbcUrl = getOption(OPTION_URL_OVERRIDE, "");
		if (SH.is(jdbcUrl)) {
			jdbcUrl = SH.replaceAll(jdbcUrl, "${USERNAME}", getUsername());
			jdbcUrl = SH.replaceAll(jdbcUrl, "${PASSWORD}", "****");
			this.jdbcPassword = getPassword();
		} else {
			String jdbcUrlPrefix = this.getOption(OPTION_DRIVER_SUBPROTOCOL, "");
			if (SH.isnt(jdbcUrlPrefix))
				jdbcUrlPrefix = buildJdbcUrlSubprotocol();
			sb.append(jdbcUrlPrefix);
			sb.append(buildJdbcUrl());
			sb.append(getOption(OPTION_URL_SUFFIX, ""));
			this.jdbcUrl = sb.toString();
			this.jdbcPassword = buildJdbcUrlPassword();
		}

		if (log.isLoggable(Level.FINE)) {
			LH.fine(log, getClass().getSimpleName(), " ", locator.getTargetName());
		}
		this.disableBigInt = getOption(OPTION_DISABLE_BIGINT, Boolean.FALSE);
		this.disableBigDec = getOption(OPTION_DISABLE_BIGDEC, Boolean.FALSE);
		this.disableUTCN = getOption(OPTION_DISABLE_UTCN, Boolean.FALSE);
	}

	protected Connection getConnection() throws AmiDatasourceException {
		try {
			Properties p = new Properties();
			p.putAll(this.jdbcArguments);
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Getting connection: ", describeConnnection());

			final String urlWithPassword = this.jdbcPassword != null ? DBH.replaceMaskWithPassword(this.jdbcUrl, this.jdbcPassword) : this.jdbcUrl;
			final Connection r = DBH.createDatasource(this.jdbcDriver, urlWithPassword, p).getConnection();
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Got connection: ", describeConnnection());
			return r;
		} catch (SQLException e) {
			DBH.prepareException(e);
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Connection Failed: " + e.getMessage() + " for " + describeConnnection(), e);
		} catch (Throwable e) {
			throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, "For " + describeConnnection(), e);
		}
	}

	public String describeConnnection() {
		StringBuilder sb = new StringBuilder();
		sb.append("Driver=").append(this.jdbcDriver);
		sb.append(", URL=").append(this.jdbcUrl);
		sb.append(", Args={");
		boolean first = true;
		for (String key : CH.sort(this.jdbcArguments.keySet())) {
			if (first)
				first = false;
			sb.append(", ");
			Object value = this.jdbcArguments.get(key);
			if (SH.startsWithIgnoreCase(key, "pass"))
				value = DBH.PASSWORD_MASK;
			sb.append(key).append('=').append(value);
		}
		sb.append('}');
		return sb.toString();
	}

	private static final byte TYPE_SELECT = 1;
	private static final byte TYPE_INSERT = 2;
	private static final byte TYPE_UPDATE = 3;
	private static final byte TYPE_DELETE = 4;
	private static final byte TYPE_ALTER = 5;

	final protected <T extends Valued> void exec(Connection conn, AmiCenterQuery query, String sql, int limit, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink,
			TimeoutController timeout) throws AmiDatasourceException {
		if (limit == NO_LIMIT)
			limit = Integer.MAX_VALUE;
		ResultSet resultSet = null;
		final byte type = getType(sql);
		try {
			Table r = null;
			Map<Integer, Object> paramsMap = null;
			if (query != null) {
				Map<String, Object> directives = query.getDirectives();
				if (directives != null) {
					// If it contains the params list ignore individual params
					if (directives.containsKey(DIRECTIVE_PARAMS_LIST)) {
						List l = (List) directives.get(DIRECTIVE_PARAMS_LIST);
						if (paramsMap == null)
							paramsMap = new TreeMap<Integer, Object>();
						for (int i = 0; i < l.size(); i++) {
							paramsMap.put(i + 1, l.get(i));
						}
					} else
						for (Entry<String, Object> e : directives.entrySet()) {
							String k = SH.trim(e.getKey());
							if (DIRECTIVE_PARAMS_MATCHER.matcher(k).matches()) {

								if (paramsMap == null)
									paramsMap = new TreeMap<Integer, Object>();
								Integer paramN = SH.parseInt(SH.afterFirst(k, DIRECTIVE_PARAMS_START));
								paramsMap.put(paramN, e.getValue());
								//								LH.info(log, "PrepareStatement param `", e.getKey(), "` value: ", Caster_String.INSTANCE.cast(e));
							}
						}
				}
			}
			boolean isPrepared = paramsMap != null;
			final Statement ps = !isPrepared ? conn.createStatement() : conn.prepareStatement(sql);
			PreparedStatement pps = null;

			if (isPrepared) {
				pps = (PreparedStatement) ps;
				final byte defaultType = AmiDatasourceColumn.TYPE_INT;
				for (Entry<Integer, Object> e : paramsMap.entrySet()) {
					byte ptype = prepareStatementAddParam(pps, e.getKey(), e.getValue(), defaultType);
					//					LH.info(log, "PrepareStatement added param `", e.getKey(), "` value: ", Caster_String.INSTANCE.cast(e.getValue()));
				}
			}

			applyTimeout(ps, timeout.getTimeoutMillisRemaining());
			if (!this.statement.compareAndSet(null, ps))
				throw new IllegalStateException("Statement already set");
			try {
				try {
					switch (type) {
						case TYPE_ALTER:
							if (isPrepared) {
								if (pps.execute())
									r = toTable(pps.getResultSet(), '_', limit);
							} else if (ps.execute(sql))
								r = toTable(ps.getResultSet(), '_', limit);
							else
								r = null;
							break;
						case TYPE_INSERT:
							if (supportsGeneratedKeys()) {
								if (isPrepared) {
									if (pps.execute())
										r = toTable(pps.getGeneratedKeys(), '_', limit);
								} else if (ps.execute(sql, Statement.RETURN_GENERATED_KEYS)) {
									r = toTable(ps.getGeneratedKeys(), '_', limit);
								} else
									r = null;
							} else {
								if (isPrepared) {
									if (pps.execute()) {
										resultSet = pps.getResultSet();
										r = toTable(resultSet, '_', limit);
									}
								} else if (ps.execute(sql)) {
									resultSet = ps.getResultSet();
									r = toTable(resultSet, '_', limit);
								} else
									r = new BasicTable();
							}
							resultSink.setRowsEffected(ps.getUpdateCount());
							break;
						case TYPE_UPDATE:
							int changed = 0;
							if (isPrepared)
								changed = pps.executeUpdate();
							else
								changed = ps.executeUpdate(sql);
							r = new BasicTable(new Class[] { Integer.class }, new String[] { "UPDATED_COUNT" });
							r.getRows().addRow(changed);
							resultSink.setRowsEffected(changed);
							break;
						case TYPE_DELETE:
							int delete = 0;
							if (isPrepared)
								delete = pps.executeUpdate();
							else
								delete = ps.executeUpdate(sql);
							r = new BasicTable(new Class[] { Integer.class }, new String[] { "DELETED_COUNT" });
							r.getRows().addRow(delete);
							resultSink.setRowsEffected(delete);
							break;
						case TYPE_SELECT:
							if (isPrepared)
								resultSet = pps.executeQuery();
							else
								resultSet = ps.executeQuery(sql);
							r = toTable(resultSet, '_', limit);
							while (ps.getMoreResults()) {
								timeout.throwIfTimedout();
								if (resultSink.getTables() == null)
									resultSink.setTables(new ArrayList<Table>());
								resultSink.getTables().add(r);
								resultSet = ps.getResultSet();
								r = toTable(resultSet, '_', limit);
							}
							break;
						default:
							throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Bad type: " + type);
					}
				} catch (Exception e) {
					throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR,
							"Error From Datasource " + getServiceLocator().getTargetName() + ": " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
				}
			} finally {
				if (statement.getAndSet(null) == null)
					LH.info(log, "Execution sucessfully cancelled");
			}
			if (r != null) {
				if (resultSink.getTables() == null)
					resultSink.setTables(new ArrayList<Table>());
				resultSink.getTables().add(r);
			}
		} catch (SQLException e) {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Exception for SQL: ", sql, e);
			throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, SH.suppress(sql, 1000), e);
		} finally {
			IOH.close(resultSet);
		}
	}

	protected byte prepareStatementAddParam(PreparedStatement pps, int i, Object p, final byte defaultType) throws AmiDatasourceException {
		byte typeForClass = AmiDatasourceColumn.TYPE_UNKNOWN;
		try {
			if (p == null) {
				pps.setNull(i, java.sql.Types.NULL);
				typeForClass = AmiDatasourceColumn.TYPE_NONE;
				return typeForClass;
			}
			typeForClass = AmiUtils.getTypeForClass(p.getClass(), defaultType);
			switch (typeForClass) {

				case AmiDatasourceColumn.TYPE_CHAR:
					pps.setString(i, String.valueOf((char) p));
					break;

				case AmiDatasourceColumn.TYPE_LONG:
					pps.setLong(i, (long) p);
					break;

				case AmiDatasourceColumn.TYPE_UTC: {
					DateMillis u = (DateMillis) p;
					pps.setLong(i, (long) u.longValue());
					break;
				}

				case AmiDatasourceColumn.TYPE_UTCN: {
					DateNanos u = (DateNanos) p;
					pps.setLong(i, (long) u.longValue());
					break;
				}

				case AmiDatasourceColumn.TYPE_DOUBLE:
					pps.setDouble(i, (double) p);
					break;

				case AmiDatasourceColumn.TYPE_FLOAT:
					pps.setFloat(i, (float) p);
					break;

				case AmiDatasourceColumn.TYPE_INT:
					pps.setInt(i, (int) p);
					break;

				case AmiDatasourceColumn.TYPE_SHORT:
					pps.setShort(i, (short) p);
					break;

				case AmiDatasourceColumn.TYPE_BIGDEC:
					pps.setBigDecimal(i, (BigDecimal) p);
					break;

				case AmiDatasourceColumn.TYPE_BIGINT: {
					BigInteger bi = (BigInteger) p;
					pps.setBigDecimal(i, new BigDecimal(bi));
					break;
				}

				case AmiDatasourceColumn.TYPE_BOOLEAN:
					pps.setBoolean(i, (boolean) p);
					break;

				case AmiDatasourceColumn.TYPE_BYTE:
					pps.setByte(i, (byte) p);
					break;

				case AmiDatasourceColumn.TYPE_BINARY: {
					Bytes b = (Bytes) p;
					pps.setBytes(i, b.getBytes());

					break;
				}
				case AmiDatasourceColumn.TYPE_COMPLEX: {
					Complex cc = (Complex) p;
					pps.setString(i, cc.toString()); //TODO: not sure how to handle
					break;
				}
				case AmiDatasourceColumn.TYPE_STRING:
				case AmiDatasourceColumn.TYPE_UUID:
				case AmiDatasourceColumn.TYPE_UNKNOWN:
				case AmiDatasourceColumn.TYPE_NONE:
				default:
					pps.setString(i, Caster_String.INSTANCE.cast(p));
					break;
			}
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR,
					SH.jn("Param ", i, " could not be parameterized, inferred type: ", typeForClass, " value: ", p), e);
		}
		return typeForClass;
	}
	protected void applyTimeout(Statement ps, int timeoutMillisRemaining) {
		int timeout = (int) Math.max(1, (timeoutMillisRemaining + 999) / 1000);
		try {
			ps.setQueryTimeout(timeout);
		} catch (SQLException e) {
			LH.w(log, "Could not set timeout: ", timeout, e);
		}
	}

	protected boolean supportsGeneratedKeys() {
		return true;
	}

	protected <T extends Valued> Table exec(Connection conn, String sql, int limit, AmiDatasourceTracker debugSink, TimeoutController timeout) throws AmiDatasourceException {
		AmiCenterQueryResult rs = tools.nw(AmiCenterQueryResult.class);
		exec(conn, null, sql, limit, rs, debugSink, timeout);
		List<Table> tables = rs.getTables();
		if (tables == null || tables.size() == 0)
			return null;
		return (Table) tables.get(0);
	}
	protected byte getType(String sql) {
		int n = SH.indexOfNot(sql, 0, ' ');
		if (n != -1) {
			if (SH.startsWithIgnoreCase(sql, QUERY_TYPE_SELECT, n))
				return TYPE_SELECT;
			if (SH.startsWithIgnoreCase(sql, QUERY_TYPE_INSERT, n))
				return TYPE_INSERT;
			else if (SH.startsWithIgnoreCase(sql, QUERY_TYPE_UPDATE, n))
				return TYPE_UPDATE;
			else if (SH.startsWithIgnoreCase(sql, QUERY_TYPE_DELETE, n))
				return TYPE_DELETE;
			else if (SH.startsWithIgnoreCase(sql, QUERY_TYPE_TRUNCATE, n))
				return TYPE_DELETE;
			else if (SH.startsWithIgnoreCase(sql, QUERY_TYPE_CREATE, n))
				return TYPE_ALTER;
			else if (SH.startsWithIgnoreCase(sql, QUERY_TYPE_ALTER, n))
				return TYPE_ALTER;
			else if (SH.startsWithIgnoreCase(sql, QUERY_TYPE_DROP, n))
				return TYPE_ALTER;
		}

		return TYPE_SELECT;
	}

	private static ConcurrentMap<Class<?>, ResultSetGetter<?>> GETTERS = new CopyOnWriteHashMap<Class<?>, ResultSetGetter<?>>();
	static {
		GETTERS.putAll(DBH.getGetters());
		GETTERS.put(java.time.LocalDateTime.class, ResultSetGetter.TemporalToDateMillisResultSetGetter.INSTANCE);
		GETTERS.put(java.time.LocalTime.class, ResultSetGetter.TemporalToDateMillisResultSetGetter.INSTANCE);
		GETTERS.put(java.time.LocalDate.class, ResultSetGetter.TemporalToDateMillisResultSetGetter.INSTANCE);
		GETTERS.put(java.sql.Date.class, ResultSetGetter.DateMillisResultSetGetter.INSTANCE);
		GETTERS.put(java.sql.Time.class, ResultSetGetter.DateMillisResultSetGetter.INSTANCE);
		GETTERS.put(java.sql.Timestamp.class, ResultSetGetter.DateNanosResultSetGetter.INSTANCE);
		GETTERS.put(DateNanos.class, ResultSetGetter.DateNanosResultSetGetter.INSTANCE);
		GETTERS.put(DateMillis.class, ResultSetGetter.DateMillisResultSetGetter.INSTANCE);
	}

	public Table toTable(ResultSet result, char tableNameColumnDelim, int limit) throws SQLException {
		String names[] = DBH.getUniqueColumnNames(result.getMetaData(), tableNameColumnDelim);
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
		ResultSetGetter<?>[] getters = new ResultSetGetter[types.length];
		for (int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			ResultSetGetter<?> getter = resolveSpecialGetter(type);
			if (getter == null) {
				getter = GETTERS.get(type);
				if (getter == null)
					GETTERS.put(type, getter = DBH.getGetter(type));
			}
			getters[i] = getter;
			types[i] = getter.getReturnType();
		}
		return DBH.toTable(result, types, getters, names, limit, DBH.TABLE_OPTIONS_NONE);
	}

	protected static String toValidVarName(String name) {
		String r = AmiUtils.toValidVarName(name);
		if (r.length() == 1 && Character.isUpperCase(r.charAt(0)))
			return SH.toLowerCase(r);
		return r;
	}
	protected ResultSetGetter resolveSpecialGetter(Class<?> type) {
		if (type == java.sql.Timestamp.class) {
			if (disableUTCN)
				return ResultSetGetter.DateMillisResultSetGetter.INSTANCE;
		} else if (type == BigDecimal.class) {
			if (disableBigDec)
				return ResultSetGetter.DoubleResultSetGetter.INSTANCE;
		} else if (type == BigInteger.class) {
			if (disableBigInt)
				return ResultSetGetter.LongResultSetGetter.INSTANCE;
		}
		return null;
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<Table> r = new ArrayList<Table>();
		Connection conn = null;
		try {
			conn = getConnection();
			int rowPos = 0;
			String select = query.getQuery();
			int limit = query.getLimit();
			rowPos++;
			select = SH.trim(select);
			StringBuilder w = new StringBuilder();

			if (SH.startsWithIgnoreCase(select, QUERY_TYPE_SELECT, 0)) {
				if (limit != NO_LIMIT)
					select = SH.toStringAndClear(createLimitClauseH(select, limit, w));
				if (debugSink != null)
					debugSink.onQuery(select);
				exec(conn, query, select, limit, resultSink, debugSink, tc);
				Table rs = CH.getOr(resultSink.getTables(), 0, null);
				if (debugSink != null)
					debugSink.onQueryResult(select, (Table) rs);
			} else {
				exec(conn, query, select, limit, resultSink, debugSink, tc);
			}
		} catch (AmiDatasourceException e) {
			throw e;
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, e);
		} finally {
			IOH.close(conn);
		}
	}

	protected StringBuilder createSelectQuery(StringBuilder sb, String fullname) {
		sb.append(SELECT_ALL_FROM_CLAUSE);
		sb.append(fullname);
		sb.append(WHERE_CLAUSE);
		return sb;
	}

	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		sb.append("SHOW TABLES");
		//		createLimitClause2(sb, limit, null);
		return sb;
	}

	protected StringBuilder createPreviewQuery(StringBuilder sb, String fullname, int limit) {
		sb.append(SELECT_ALL_FROM_CLAUSE);
		sb.append(fullname);
		createLimitClause2(sb, limit, null);
		return sb;
	}
	protected StringBuilder createCountQuery(StringBuilder sb, String fullname) {
		sb.append("SELECT COUNT(*) AS CNT FROM ");
		sb.append(fullname);
		return sb;
	}
	/*
	 * Temporary measure goal is to make this the main create limit clause method
	 */
	protected StringBuilder createLimitClause2(StringBuilder query, int limit, Integer offset) {
		return createLimitClauseH("", limit, query);
	}
	/*
	 * Temporary measure - a wrapper goal to remove the other createLimitClause that returns a string
	 */
	protected final StringBuilder createLimitClauseH(String select, int limit, StringBuilder sb) {
		return sb.append(createLimitClause(select, limit));

	}
	protected String createLimitClause(String select, int limit) {
		return select + " LIMIT " + limit;
	}

	@Override
	public boolean cancelQuery() {
		Statement existing = statement.getAndSet(null);
		if (existing == null)
			return false;
		try {
			existing.cancel();
		} catch (SQLException e) {
			LH.info(log, "Error cancelling statement: ", e);
		}
		return true;
	}
	public long getTableSize(Connection conn, String sql, AmiDatasourceTracker debugSink, TimeoutController timeout) throws AmiDatasourceException {
		Table r = exec(conn, sql, 1, debugSink, timeout);
		if (r.getSize() == 0)
			return 0;
		if (r == null || r.getColumnsCount() != 1)
			return -1L;
		return r.getAt(0, 0, Caster_Long.INSTANCE);
	}

	private static final int MAX_CELLS = 100000;

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
				List<List<Row>> batches = CH.batchSublists(table.getRows(), batchSize, false);
				String targetTable = formatTargetTableName(ul);
				LH.info(log, "Inserting ", table.getRows().size(), " row(s) x ", columnsCount, " columns(s) into ", targetTable, " using ", batches.size(), " batch(es)");
				int n = 0;
				for (List<Row> rows : batches) {
					int rowsCount = rows.size();
					SH.clear(sql);
					sql.append('(');
					SH.repeat("?,", columnsCount - 1, sql);
					sql.append("?)");
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
							formatTargetColumnName(c, sql);
						}
						sql.append(")");
					}

					sql.append(" values ");
					for (int i = 0; i < rowsCount; i++) {
						if (i > 0)
							sql.append(',');
						sql.append(values);
					}
					PreparedStatement ps;
					ps = conn.prepareStatement(sql.toString());
					int j = 1;
					for (Row r : rows)
						for (int c = 0; c < columnsCount; c++)
							ps.setObject(j++, table.getAt(r.getLocation(), c));
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

	protected void formatTargetColumnName(String c, StringBuilder sql) {
		sql.append('`').append(c).append('`');
	}

	protected String formatTargetTableName(AmiCenterUploadTable ul) {
		return "`" + ul.getTargetTable() + "`";
	}
	//should be overridden
	abstract protected String buildJdbcDriverClass();
	abstract protected Map<String, Object> buildJdbcArguments();
	abstract protected String buildJdbcUrlSubprotocol();
	abstract protected String buildJdbcUrl();
	abstract protected String buildJdbcUrlPassword();

	/*
	 * Argument: row containing the results of the createShowTablesQuery
	 */
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		//		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		//		String schem = SH.trim(row.getAt(1, Caster_String.INSTANCE));
		//		String name = SH.trim(row.getAt(2, Caster_String.INSTANCE));
		//		table.setCollectionName(schem);
		//		table.setName(name);
		//		String fullname = getSchemaName(SH.clear(sb), table);
		//		createSelectQuery(sb, fullname);
		//		table.setCustomQuery(SH.toStringAndClear(sb));
		//		return table;
		throw new UnsupportedOperationException("Required to implement");
	}

	protected Table execShowTablesQuery(StringBuilder sb, Connection conn, int limit, AmiDatasourceTracker debugSink, TimeoutController tc) throws Exception {
		createShowTablesQuery(SH.clear(sb), limit);
		Table rs = exec(conn, SH.toStringAndClear(sb), limit, debugSink, tc);
		return rs;
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		//		boolean prependSchema = getOption(OPTION_PREPEND_SCHEMA, Boolean.FALSE);
		int tlimit = getOption(OPTION_SCHEMA_TABLE_LIMIT, NO_LIMIT);
		Connection conn = null;
		try {
			List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>();
			if (tlimit > 0 || tlimit == NO_LIMIT) {
				//				int rlimit = tlimit == NO_LIMIT ? Integer.MAX_VALUE : tlimit;
				conn = getConnection();

				StringBuilder sb = new StringBuilder();
				//Query for getting tables
				Table tables = execShowTablesQuery(sb, conn, tlimit, debugSink, tc);
				//				Table tables = execShowTablesQuery(sb, conn, rlimit, debugSink, tc);

				TableList rows = tables.getRows();
				for (int i = 0; i < rows.size(); i++) {
					Row row = rows.get(i);

					AmiDatasourceTable table = this.createAmiDatasourceTable(row, sb);
					if (table == null)
						continue;

					r.add(table);
				}
			}
			return r;
		} catch (SQLException e) {
			throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Could not get table list", e);
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, e.getMessage());
		} finally {
			IOH.close(conn);
		}
	}

	protected Table execPreviewQuery(StringBuilder sb, Connection conn, String fullname, int limit, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		createPreviewQuery(SH.clear(sb), fullname, limit);
		Table rs = exec(conn, SH.toStringAndClear(sb), limit, debugSink, tc);
		return rs;
	}
	protected long execCountQuery(StringBuilder sb, Connection conn, String fullname, int limit, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		createCountQuery(SH.clear(sb), fullname);
		long size = SH.isnt(sb) ? -1 : getTableSize(conn, SH.toStringAndClear(sb), debugSink, tc); // -1 is UNDEFINED/UNKNOWN SIZE
		return size;
	}
	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		int climit = getOptionNoThrow(OPTION_SCHEMA_COLUMN_LIMIT, NO_LIMIT);
		Connection conn = null;
		try {
			if (climit > 0 || climit == NO_LIMIT) {
				conn = getConnection();
				int tablesSize = tables.size();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < tablesSize; i++) {
					AmiDatasourceTable table = tables.get(i);
					String fullname = getSchemaName(SH.clear(sb), table);

					// SELECT QUERY
					Table rs = execPreviewQuery(sb, conn, fullname, previewCount, debugSink, tc);

					// COUNT QUERY
					long size = execCountQuery(sb, conn, fullname, previewCount, debugSink, tc);

					List<Column> rscols = rs.getColumns();
					table.setColumns(new ArrayList<AmiDatasourceColumn>(rscols.size()));
					//					int rscolsSize = rscols.size();
					int rscolsSize = climit != NO_LIMIT ? climit < rscols.size() ? climit : rscols.size() : rscols.size();
					for (int j = 0; j < rscolsSize; j++) {
						Column rscol = rscols.get(j);
						AmiDatasourceColumn col = tools.nw(AmiDatasourceColumn.class);
						col.setName(SH.trim(rscol.getId().toString()));
						col.setType(AmiUtils.getTypeForClass(rscol.getType(), AmiDatasourceColumn.TYPE_UNKNOWN));
						table.getColumns().add(col);
					}
					table.setPreviewData(rs);
					if (size != -1)
						table.setPreviewTableSize(size);
				}
			}
			return tables;
		} finally {
			IOH.close(conn);
		}
	}
}
