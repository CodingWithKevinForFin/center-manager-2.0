package com.f1.ami.amidb.jdbc;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiJdbcConsts;
import com.f1.ami.amicommon.messaging.SimpleMessagingClientConnection;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.converter.bytes.ClassToByteArrayConverter_Legacy;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class AmiDbJdbcClient implements Closeable {
	private static final ObjectToByteArrayConverter CONVERTER = new ObjectToByteArrayConverter(true);
	private static final ObjectToByteArrayConverter CONVERTER_LEGACY = new ObjectToByteArrayConverter(true);
	static {
		CONVERTER_LEGACY.replaceConverter(new ClassToByteArrayConverter_Legacy());
	}

	final static private Logger log = LH.get();
	public static final String JDBC_AMISQL = "jdbc:amisql:";

	final private Map<String, String> optionsMap;
	final private String hostname;
	final private int port;
	final private ObjectToByteArrayConverter converter;
	final private List<Object> buf;
	final private SimpleMessagingClientConnection sms;
	final private String url;
	final private int legacyMode;

	private int serverVersion;
	private int negotiatedVersion;
	private boolean ignoreRollback;
	public static final int CLIENT_VERSION = AmiJdbcConsts.VERSION_2;
	private boolean isSecure;
	private String sslKeystore;
	private String sslKeystorePass;

	public AmiDbJdbcClient(String url, String password) throws IOException {
		this(url, password, null);
	}

	public AmiDbJdbcClient(String url, String password, Properties properties) throws IOException {
		LH.info(log, "LOG:AmiDBJdbcClientLog connecting to: " + suppress(suppress(url, "password="), "pass="));
		buf = new ArrayList<Object>(3);
		try {
			url = SH.stripPrefix(url, JDBC_AMISQL, true);
			this.url = url = SH.stripPrefix(url, "//", false);
			String urlPort = SH.beforeFirst(url, '?', url);
			String options = SH.afterFirst(url, '?', "");
			this.port = SH.parseInt(SH.afterLast(urlPort, ':', null));
			this.hostname = SH.beforeLast(urlPort, ':', null);
			this.optionsMap = SH.splitToMap('&', '=', '\\', options);
		} catch (Exception e) {
			throw new RuntimeException("Expecting URL format: " + "jdbc:amisql:hostname:port?option=value&option=value (options are: username, password)", e);
		}
		if (properties != null) {
			for (Entry<Object, Object> i : properties.entrySet()) {
				String key = OH.toString(i.getKey());
				String val = OH.toString(i.getValue());
				if (key == null | val == null)
					continue;
				if ("user".equals(key))
					key = "username";
				else if ("pass".equals(key))
					key = "password";
				else if ("timeoutMs".equals(key))
					key = "timeout";
				this.optionsMap.put(key, val);
			}
		}
		if (password != null)
			this.optionsMap.put("password", password);
		int timeoutMs = CH.getOr(Integer.class, this.optionsMap, "timeout", 60000);
		this.legacyMode = CH.getOr(Caster_Integer.INSTANCE, this.optionsMap, "legacyVersion", Integer.MAX_VALUE);
		this.ignoreRollback = CH.getOr(Caster_Boolean.INSTANCE, this.optionsMap, "ignoreRollback", Boolean.FALSE);
		this.converter = this.legacyMode < 2 ? CONVERTER_LEGACY : CONVERTER;
		this.isSecure = CH.getOr(Caster_Boolean.INSTANCE, this.optionsMap, "isSecure", Boolean.FALSE);
		this.sslKeystore = isSecure ? CH.getOr(String.class, this.optionsMap, "sslKeystore", null) : null;
		this.sslKeystorePass = isSecure ? CH.getOr(String.class, this.optionsMap, "sslKeystorePass", null) : null;
		try {
			this.sms = new SimpleMessagingClientConnection(converter, hostname, port, isSecure, timeoutMs, sslKeystore, sslKeystorePass);
		} catch (Exception e) {
			throw new RuntimeException("Could not connect to '" + this.hostname + "' at port " + this.port + " ==>  " + e.getMessage(), e);
		}

		String msg;
		if (legacyMode < 3) {
			Object loginResult = send(AmiJdbcConsts.INSTRUCTION_LOGIN, this.optionsMap);
			this.serverVersion = AmiJdbcConsts.VERSION_1;
			msg = (String) loginResult;
		} else {
			Object loginResult = send(AmiJdbcConsts.INSTRUCTION_LOGIN, this.optionsMap, CLIENT_VERSION);
			List vals = (List) loginResult;
			msg = (String) vals.get(0);
			this.serverVersion = (int) vals.get(1);
		}
		this.negotiatedVersion = Math.min(CLIENT_VERSION, serverVersion);
		if (!sms.isOpen())
			throw new RuntimeException("LOGIN FAILED: " + msg);
	}

	static private String suppress(String url, String param) {
		if (SH.isnt(url))
			return url;
		int start = url.indexOf(param);
		if (start != -1) {
			start += param.length();
			int end = url.indexOf('&', start);
			if (end == -1)
				end = url.length();
			StringBuilder sb = new StringBuilder(url);
			for (int i = start; i < end; i++)
				sb.setCharAt(i, '*');
			return sb.toString();
		}

		return url;
	}

	private synchronized Object send(byte instruction, Object payload) throws IOException {
		buf.clear();
		buf.add(instruction);
		buf.add(payload);
		return sms.sendObject(buf);
	}
	private synchronized Object send(byte instruction, Object payload, Object payload2) throws IOException {
		buf.clear();
		buf.add(instruction);
		buf.add(payload);
		buf.add(payload2);
		return sms.sendObject(buf);
	}
	private synchronized Object send(byte instruction, Object payload, Object payload2, Object payload3) throws IOException {
		buf.clear();
		buf.add(instruction);
		buf.add(payload);
		buf.add(payload2);
		buf.add(payload3);
		return sms.sendObject(buf);
	}
	private synchronized Object send(byte instruction, Object payload, Object payload2, Object payload3, Object payload4) throws IOException {
		buf.clear();
		buf.add(instruction);
		buf.add(payload);
		buf.add(payload2);
		buf.add(payload3);
		buf.add(payload4);
		return sms.sendObject(buf);
	}
	public Tuple2<List<Table>, List<Object>> query(String string, long timeoutMillis) throws Exception {
		final List values;
		if (legacyMode < 2)
			values = (List) send(AmiJdbcConsts.INSTRUCTION_QUERY, string);
		else
			values = (List) send(AmiJdbcConsts.INSTRUCTION_QUERY, string, timeoutMillis);
		if (CH.isntEmpty(values)) {
			Object value = values.get(0);
			if (value instanceof Byte) {
				List<Table> tables = null;
				List<Object> generatedKeys = null;
				for (int i = 0; i < values.size();) {
					final byte key = Caster_Byte.PRIMITIVE.cast(values.get(i++));
					final Object val = values.get(i++);
					switch (key) {
						case AmiJdbcConsts.RET_CODE_TABLES:
							tables = (List) val;
							break;
						case AmiJdbcConsts.RET_CODE_GENERATED_KEYS:
							generatedKeys = (List) val;
							break;
						case AmiJdbcConsts.RET_CODE_ERROR:
							throw new SQLException((String) val);
						case AmiJdbcConsts.RET_CODE_ROWS_EFFECTED:
						case AmiJdbcConsts.RET_CODE_RETURN_VALUE:
							break;//nothing to do;
					}
				}
				return new Tuple2<List<Table>, List<Object>>(tables, generatedKeys);
			} else {//backwards compatibility
				if (value instanceof Table)
					return new Tuple2(values, null);
				else if (value instanceof Long)
					return new Tuple2(values, null);
				else if (value instanceof Exception)
					throw (Exception) value;
				else if (value instanceof CharSequence)
					throw new SQLException(value.toString());
				else
					throw new SQLException("Unknown response: " + OH.getClassName(value));
			}
		}
		return null;
	}
	public int queryExecuteUpdate(String string, long timeoutMillis) throws Exception {
		final List values;
		if (legacyMode < 2)
			values = (List) send(AmiJdbcConsts.INSTRUCTION_QUERY, string);
		else
			values = (List) send(AmiJdbcConsts.INSTRUCTION_QUERY, string, timeoutMillis);
		if (CH.isntEmpty(values)) {
			Object value = values.get(0);
			if (value instanceof Byte) {
				for (int i = 0; i < values.size();) {
					final byte key = Caster_Byte.PRIMITIVE.cast(values.get(i++));
					final Object val = values.get(i++);
					switch (key) {
						case AmiJdbcConsts.RET_CODE_ROWS_EFFECTED:
							return Caster_Integer.PRIMITIVE.cast(val);
						case AmiJdbcConsts.RET_CODE_ERROR:
							throw new SQLException((String) val);
						case AmiJdbcConsts.RET_CODE_TABLES:
						case AmiJdbcConsts.RET_CODE_RETURN_VALUE:
						case AmiJdbcConsts.RET_CODE_GENERATED_KEYS:
							break;//nothing to do;
					}
				}
			} else {//backwards compatibility
				return -1;
			}
		}
		return -1;
	}
	private Map<String, Object> getQueryResults(ColumnarTable result) {
		HashMap<String, Object> r = new HashMap<String, Object>();
		Set<String> columnIds = result.getColumnIds();
		OH.assertTrue(columnIds.contains("name")); // See AmiCenterJdbcServerConnectionHandler
		OH.assertTrue(columnIds.contains("value"));
		for (Row row : result.getRows()) {
			r.put((String) row.get("name"), row.get("value"));
		}

		return r;
	}

	public boolean isClosed() {
		return !this.sms.isOpen();
	}

	@Override
	public void close() {
		this.sms.close();
	}

	public String getUrl() {
		return this.hostname;
	}

	public String getUsername() {
		return this.optionsMap.get("username");
	}

	public void insert(String tablename, List<String> columns, Table data, long timeoutMillis) throws Exception {
		List values = (List) send(AmiJdbcConsts.INSTRUCTION_INSERT, tablename, columns, data, timeoutMillis);
		List<Table> tables = null;
		List<Object> generatedKeys = null;
		if (values != null)
			for (int i = 0; i < values.size();) {
				final byte key = Caster_Byte.PRIMITIVE.cast(values.get(i++));
				final Object val = values.get(i++);
				switch (key) {
					case AmiJdbcConsts.RET_CODE_TABLES:
						tables = (List) val;
						break;
					case AmiJdbcConsts.RET_CODE_GENERATED_KEYS:
						generatedKeys = (List) val;
						break;
					case AmiJdbcConsts.RET_CODE_ERROR:
						throw new SQLException((String) val);
					case AmiJdbcConsts.RET_CODE_ROWS_EFFECTED:
					case AmiJdbcConsts.RET_CODE_RETURN_VALUE:
						break;//nothing to do;
				}
			}
	}

	public boolean supportFastInsert() {
		return negotiatedVersion >= 2;
	}

	public boolean ignoreRollback() {
		return this.ignoreRollback;
	}
}
