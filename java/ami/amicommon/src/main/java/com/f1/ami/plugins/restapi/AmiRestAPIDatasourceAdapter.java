package com.f1.ami.plugins.restapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Getter;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.CasterManager;
import com.f1.utils.ConnectionIOException;
import com.f1.utils.DateFormatNano;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FileMagic;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_DateMillis;
import com.f1.utils.casters.Caster_DateNanos;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.rest.RestHelper;
import com.f1.utils.structs.table.BasicColumn;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiRestAPIDatasourceAdapter implements AmiDatasourceAdapter {
	private static final String HTTP_HEADER_USER_AGENT = "User-Agent";
	private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	private static final String HTTP_HEADER_AUTH = "Authorization";

	protected static final Logger log = LH.get();
	private ContainerTools tools;
	private AmiServiceLocator serviceLocator;
	private Map<String, String> options = new LinkedHashMap<String, String>();
	private String url;
	private Thread thread;
	private String username;
	private String auth;
	private char[] password;
	private static TimeZone GMT = TimeZone.getTimeZone("GMT");
	private static DateFormatNano TIMESTAMP_FORMATTER = new DateFormatNano("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
	static {
		TIMESTAMP_FORMATTER.setTimeZone(GMT);
	}

	public static Map<String, String> buildOptions() {
		return new HashMap<String, String>();
	}

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceLocator = serviceLocator;
		if (serviceLocator.getOptions() != null)
			SH.splitToMap(this.options, ',', '=', '\\', serviceLocator.getOptions());
		this.url = serviceLocator.getUrl();
		this.username = serviceLocator.getUsername();
		this.password = serviceLocator.getPassword();
		if (SH.is(this.username)) {
			String auth = this.username + ":" + new String(password);
			this.auth = "Basic " + EncoderUtils.encode64(auth.getBytes());
		} else {
			this.auth = null;
		}
		try {
			URL connection = new URL(this.url);
			connection.openConnection();
			//			IOH.doGet(connection, null, null);
		} catch (MalformedURLException e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
		} catch (UnknownHostException e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
		} catch (IOException e) {
			LH.fine(log, e);
		}
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<AmiDatasourceTable> sink = new ArrayList<AmiDatasourceTable>();
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		String suggestedName = SH.beforeFirst(SH.afterLast(this.url, "/"), "?");
		table.setName(AmiUtils.toValidVarName(SH.getNextId(suggestedName, new HashSet<String>())));
		table.setCustomQuery("SELECT * FROM " + table.getName());
		table.setCustomUse("_method=\"GET\" _validateCerts=\"true\"");
		//		table.setCollectionName(null);
		sink.add(table);
		return sink;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		for (int i = 0; i < tables.size(); i++) {
			AmiDatasourceTable table = tables.get(i);
			// Set up Query
			AmiCenterQuery q = tools.nw(AmiCenterQuery.class);
			Map<String, Object> directives = new LinkedHashMap<String, Object>();
			directives.put("method", "GET");
			directives.put("validateCerts", "true");
			directives.put("delim", "_");
			directives.put("urlExtension", "");
			directives.put("path", "");

			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, Object> e : directives.entrySet()) {
				sb.append('_').append(e.getKey()).append('=').append(SH.doubleQuote((String) e.getValue())).append(' ');
			}

			q.setQuery("SELECT * FROM " + table.getName());
			q.setLimit(previewCount);
			q.setDirectives(directives);

			// Get Preview Data
			List<Table> previewData = null;
			try {
				previewData = AmiUtils.processQuery(tools, this, q, debugSink, tc);
			} catch (AmiDatasourceException e) {
				if (e.getCause() instanceof ConnectionIOException) {

				} else
					throw e;
			}

			// Get Columns
			ArrayList<AmiDatasourceColumn> amiColumns = new ArrayList<AmiDatasourceColumn>();
			List<Column> cols = null;
			if (previewData != null) {
				previewData.get(0).getColumns();
				cols = new ArrayList<Column>();
			}
			sb.append("_fields=");
			String custUse = sb.toString();
			sb.setLength(0);

			if (previewData != null)
				for (int j = 0; j < cols.size(); j++) {
					if (j != 0)
						sb.append(", ");
					// For fields directive
					Column column = cols.get(j);
					sb.append(AmiUtils.toTypeName(column.getType())).append(' ');
					sb.append(column.getId());

					// Create AmiDatasource Column
					AmiDatasourceColumn amiCol = tools.nw(AmiDatasourceColumn.class);
					byte type = AmiUtils.getTypeForClass(column.getType(), AmiDatasourceColumn.TYPE_STRING);
					amiCol.setName(AmiUtils.toValidVarName(SH.s(column.getId())));
					amiCol.setType(type);
					amiColumns.add(amiCol);
				}

			table.setCustomUse(custUse + SH.doubleQuote(sb.toString()));
			table.setColumns(amiColumns);
			if (previewData != null)
				table.setPreviewData(previewData.get(0));
		}
		return tables;
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<Table> r = new ArrayList<Table>();
		this.processQuery(query, debugSink, tc, r);
		resultSink.setTables(r);
	}

	private void processQuery(AmiCenterQuery query, AmiDatasourceTracker debugSink, TimeoutController tc, List<Table> sink) throws AmiDatasourceException {
		Map<String, Object> d = query.getDirectives();
		String HTTPMethod = AmiDatasourceUtils.getRequired(d, "method", "connect", "delete", "get", "head", "options", "patch", "post", "put", "trace");
		byte httpMethodType = RestHelper.toHttpMethodType(HTTPMethod);
		String urlExtension = AmiDatasourceUtils.getOptional(d, "urlExtension");
		if (SH.isnt(urlExtension))
			urlExtension = "";
		String urlBaseOverride = AmiDatasourceUtils.getOptional(d, "urlBaseOverride");
		String path = AmiDatasourceUtils.getOptional(d, "path");
		String fields = AmiDatasourceUtils.getOptional(d, "fields");
		String headers = AmiDatasourceUtils.getOptional(d, "headers");
		String params = AmiDatasourceUtils.getOptional(d, "params");
		String delim = AmiDatasourceUtils.getOptional(d, "delim");
		String validateCerts = AmiDatasourceUtils.getOptional(d, "validateCerts", "true", "false");
		String returnHeaders = AmiDatasourceUtils.getOptional(d, "returnHeaders", "true", "false");
		String dataType = AmiDatasourceUtils.getOptional(d, "dataType", "raw", "json", "text");
		boolean debug = SH.equals("true", SH.toLowerCase(AmiDatasourceUtils.getOptional(d, "debug", "true", "false")));
		final boolean redirectFollowHttpMethod = SH.equals("true", SH.toLowerCase(AmiDatasourceUtils.getOptional(d, "redirectFollowHttpMethod", "true", "false")));
		final boolean redirectFollowAuthHeader = SH.equals("true", SH.toLowerCase(AmiDatasourceUtils.getOptional(d, "redirectFollowAuthHeader", "true", "false")));
		final boolean redirectPersistCookies = SH.equals("true", SH.toLowerCase(AmiDatasourceUtils.getOptional(d, "redirectPersistCookies", "true", "false")));
		//		String handleError = AmiDatasourceUtils.getOptional(d, "handleError", "none", "http");
		Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		Map<String, String> headersMap = new LinkedHashMap<String, String>();
		Map<String, Column> columns = new LinkedHashMap<String, Column>();

		boolean returnHeadersFlag = SH.equals(returnHeaders, "true");
		boolean ignoreCerts = false;

		if (SH.is(validateCerts)) {
			if (SH.equals("false", validateCerts))
				ignoreCerts = true;
			else if (SH.equals("true", validateCerts))
				ignoreCerts = false;
		}

		if (SH.isnt(delim)) {
			delim = "_";
		}
		// GET HTTP HEADERS
		headersMap.put(HTTP_HEADER_CONTENT_TYPE, "application/json");
		headersMap.put(HTTP_HEADER_USER_AGENT, "3Forge-AMI-REST_Adapter");
		//Do Auth
		if (this.auth != null) {
			headersMap.put(HTTP_HEADER_AUTH, this.auth);
		}
		//		headersMap.put("Connection", "keep-alive");
		//		headersMap.put("Accept-Encoding", "gzip, deflate");
		//		headersMap.put("Accept", "*/*");
		if (SH.is(headers)) {
			Map<String, String> headersMapAdd = (Map<String, String>) ObjectToJsonConverter.INSTANCE_COMPACT.stringToObject(headers);
			headersMap.putAll(headersMapAdd);
		}
		for (Map.Entry<String, Object> t : d.entrySet()) {
			if (t.getKey().startsWith("header_"))
				headersMap.put(SH.stripPrefix(t.getKey(), "header_", true), (String) t.getValue());
		}

		// GET Rest API Params
		for (Map.Entry<String, Object> t : d.entrySet()) {
			if (t.getKey().startsWith("param_"))
				paramsMap.put(SH.stripPrefix(t.getKey(), "param_", true), (String) t.getValue());
		}

		// GET Fields
		if (SH.is(fields))
			parseColumns("_fields", fields, columns);

		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();

		// Build new URL 
		StringBuilder urlBuilder = new StringBuilder();
		String postParamsStr = null;
		final String urlBase = SH.isnt(urlBaseOverride) ? this.url : urlBaseOverride;

		if (httpMethodType == RestHelper.HTTP_GET) {
			if (SH.is(params) || paramsMap.size() > 0)
				urlBuilder.append('?');
			if (SH.is(params))
				urlBuilder.append(params);
			if (paramsMap.size() > 0)
				urlBuilder.append(SH.joinMap("&", "=", paramsMap));
		} else if (SH.is(params) || !paramsMap.isEmpty()) {
			String tparam = "";
			if (SH.is(params))
				tparam = params;
			if (paramsMap.size() > 0)
				tparam = tparam + SH.joinMap("&", "=", paramsMap);
			postParamsStr = tparam;
		}

		this.thread = Thread.currentThread();
		Object restResult = null;

		boolean isError = false;
		try {
			//			URL sourceUrl = new URL(urlBuilder.toString());
			byte[] data = null;

			// Do Post or Get Request
			data = doHttp(username, password, HTTPMethod, urlBase, urlExtension, urlBuilder.toString(), headersMap, postParamsStr, ignoreCerts, tc.getTimeoutMillisRemaining(),
					returnHeadersSink, debug, redirectFollowHttpMethod, redirectFollowAuthHeader, redirectPersistCookies);

			if (dataType == null) {
				String ct = CH.first(returnHeadersSink.get("Content-Type"));
				if (ct != null) {
					if (ct.indexOf("json") != -1)
						dataType = "json";
					else if (ct.indexOf("text") != -1)
						dataType = "text";
				}
			}
			if (data == null) {
				String errorCode = CH.first(returnHeadersSink.get(null));
				if (errorCode != null && errorCode.indexOf("200") == -1)
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, errorCode);
			}

			restResult = IOH.parseHttpResponseData(data, IOH.getHttpDataType(dataType));
		} catch (ConnectionIOException e) {
			if (returnHeadersFlag == false)
				if (SH.indexOf(e.getMessage(), "response code: 401 ", 0) != -1)
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Bad username/password at " + urlBuilder.toString(), e);
				else
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Connection failed at " + urlBuilder.toString(), e);
			else {
				isError = true;
				restResult = e.getMessage();
			}

		} catch (AmiDatasourceException e) {
			throw e;
		} catch (Exception e) {
			if (SH.indexOf(e.getMessage(), "response code: 401 ", 0) != -1)
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Bad username/password at " + urlBuilder.toString(), e);
			else
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Connection failed at " + urlBuilder.toString(), e);
		} finally {
			this.thread = null;
		}

		if (SH.is(path)) {
			NestedMapGetter mapGetter = new NestedMapGetter(path, delim);
			if (restResult instanceof Map) {
				restResult = mapGetter.get((Map) restResult);
			} else if (restResult instanceof List) {
				throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Invalid Path Directive for List: " + path);
			}
		}

		if (isError == true)
			columns.put("value", new BasicColumn(String.class, "_Error"));
		else {
			if (processAmiRestQuery(restResult, sink))
				return;
			if (columns.size() == 0) {
				// Gets default columns if no columns are defined
				parseColumns(restResult, columns);
			}
		}

		//Get Nested Getters
		Map<String, Getter> getters = new LinkedHashMap<String, Getter>();
		for (Map.Entry<String, Column> r : columns.entrySet()) {
			Getter<Map, Object> get = toGetter(r.getKey(), delim);

			if (get != null)
				getters.put(r.getKey(), get);
		}

		sink.add(createTable(restResult, columns, getters));
		if (returnHeadersFlag)
			sink.add(createReturnHeadersTable(returnHeadersSink));

	}

	private boolean processAmiRestQuery(Object restResult, List<Table> sink) {
		if (restResult instanceof Map) {
			Map mresult = (Map) restResult;
			if ("ok".equals(mresult.get("status"))) {
				Object oTables = mresult.get("tables");
				if (oTables instanceof List) {
					List tables = (List) oTables;
					for (Object table : tables) {
						if (table instanceof Map) {
							Map mtable = (Map) table;
							String title = (String) mtable.get("title");
							List<Map> cols = (List) mtable.get("columns");
							ColumnarTable ct = new ColumnarTable();
							if (title != null)
								ct.setTitle(title);
							boolean first = false;
							int colsCount = cols.size();
							List[] values = new List[colsCount];
							Caster[] types = new Caster[colsCount];
							for (int x = 0; x < colsCount; x++) {
								Map col = cols.get(x);
								String name = (String) col.get("name");
								String type = (String) col.get("type");
								values[x] = (List) col.get("values");
								Class<?> clazz = AmiUtils.METHOD_FACTORY.forNameNoThrow(type);
								if (clazz == null)
									clazz = String.class;
								types[x] = CasterManager.getCaster(clazz);
								ct.addColumn(clazz, name);
							}
							if (values.length > 0) {
								if (values[0] != null) {
									int rowsCount = values[0].size();
									for (int y = 0; y < rowsCount; y++) {
										Object[] rowValues = new Object[colsCount];
										for (int x = 0; x < colsCount; x++)
											rowValues[x] = types[x].cast(values[x].get(y));
										ct.getRows().addRow(rowValues);
									}
								} else {
									List<List> rows = (List) mtable.get("rows");
									for (List row : rows) {
										Object[] rowValues = new Object[colsCount];
										for (int x = 0; x < colsCount; x++)
											rowValues[x] = types[x].cast(row.get(x));
										ct.getRows().addRow(rowValues);
									}
								}
							}
							sink.add(ct);
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	private static Table createReturnHeadersTable(Map<String, List<String>> returnHeaders) {
		ArrayList<Column> colsDef = new ArrayList<Column>();
		colsDef.add(new BasicColumn(String.class, "Key"));
		colsDef.add(new BasicColumn(String.class, "Value"));
		ColumnarTable out = new ColumnarTable(colsDef);
		if (returnHeaders != null)
			for (Entry<String, List<String>> e : returnHeaders.entrySet()) {
				String key = e.getKey();
				List<String> vals = e.getValue();
				for (String val : vals) {
					ColumnarRow crow = out.newEmptyRow();
					out.getRows().add(crow);
					crow.put("Key", key);
					crow.put("Value", val);
				}
			}
		out.setTitle("ResponseHeaders");
		return out;
	}

	static private Table createTable(Object o, Map<String, Column> columns, Map<String, Getter> getters) throws AmiDatasourceException {
		ColumnarTable out = new ColumnarTable(new ArrayList(columns.values()));
		out.setTitle("ResponseData");

		//Get first field and column
		String field0 = null;
		Column col0 = null;
		if (columns.size() == 1) {
			for (Map.Entry<String, Column> k : columns.entrySet()) {
				field0 = k.getKey();
				col0 = k.getValue();
				break;
			}
		}

		// Convert data to table based on columns
		if (o instanceof List) {
			List l = (List) o;
			for (int i = 0; i < l.size(); i++) {
				Object ro = l.get(i);
				addRowToTable(out, ro, columns, getters, col0, field0);
			}
		} else {
			addRowToTable(out, o, columns, getters, col0, field0);
		}
		return out;
	}

	private static void addRowToTable(ColumnarTable out, Object ro, Map<String, Column> columns, Map<String, Getter> getters, Column col0, String field0)
			throws AmiDatasourceException {
		ColumnarRow crow = out.newEmptyRow();
		out.getRows().add(crow);
		if (ro instanceof Map) {
			Map<String, Object> m = (Map<String, Object>) ro;
			for (Map.Entry<String, Column> r : columns.entrySet()) {
				String field = r.getKey();
				Column c = r.getValue();

				Getter get = getters.get(field);
				Object val = null;
				Caster<?> typeCaster = c.getTypeCaster();

				Object uncasted = null;
				if (get != null) {
					uncasted = get.get(m);
				} else
					uncasted = m.get(c.getId());

				if (typeCaster instanceof Caster_DateMillis && uncasted instanceof String) {
					if (TIMESTAMP_FORMATTER.canParse((String) uncasted)) {
						val = TIMESTAMP_FORMATTER.parse((String) uncasted);
					} else
						val = typeCaster.cast(uncasted);

				} else if (typeCaster instanceof Caster_DateNanos && uncasted instanceof String) {
					if (TIMESTAMP_FORMATTER.canParse((String) uncasted)) {
						val = TIMESTAMP_FORMATTER.parseToNanos((String) uncasted);
					} else
						val = typeCaster.cast(uncasted);
				} else if (typeCaster instanceof Caster_String && (uncasted instanceof Map || uncasted instanceof List)) {
					val = ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(uncasted);
				} else
					val = typeCaster.cast(uncasted);
				crow.put(c.getId(), val);
			}
		} else {
			if (columns.size() == 1) {
				if (col0 != null && field0 != null) {
					Getter get = getters.get(field0);

					Object val = null;
					Caster<?> typeCaster = col0.getTypeCaster();
					Object uncasted = null;
					if (get != null) {
						uncasted = get.get(ro);
					} else
						uncasted = ro;

					if (typeCaster instanceof Caster_DateMillis && uncasted instanceof String) {
						if (TIMESTAMP_FORMATTER.canParse((String) uncasted)) {
							val = TIMESTAMP_FORMATTER.parse((String) uncasted);
						} else
							val = typeCaster.cast(uncasted);

					} else if (typeCaster instanceof Caster_DateNanos && uncasted instanceof String) {
						if (TIMESTAMP_FORMATTER.canParse((String) uncasted)) {
							val = TIMESTAMP_FORMATTER.parseToNanos((String) uncasted);
						} else
							val = typeCaster.cast(uncasted);
					} else if (typeCaster instanceof Caster_String && (uncasted instanceof Map || uncasted instanceof List)) {
						val = ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(uncasted);
					} else
						val = typeCaster.cast(uncasted);
					crow.put(col0.getId(), val);
				}
			} else {
				throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Inconsistent schema expecting more than one value");
			}
		}
	}

	private static Getter<Map, Object> toGetter(String path, String delim) {
		if (delim.equals(path))
			return null;
		//		return ALL;
		if (path.indexOf(delim) == -1)
			return null;
		else
			return new NestedMapGetter(SH.afterFirst(path, delim), delim);

	}

	private static class NestedMapGetter implements Getter<Map, Object> {
		private String name;
		private String path[];

		public NestedMapGetter(String path, String delim) {
			this.name = path;
			if (path.startsWith(delim))
				this.path = SH.split(delim, path.substring(2));
			else
				this.path = SH.split(delim, path);
		}

		@Override
		public Object get(Map document) {
			Object r = document.get(path[0]);
			for (int i = 1; i < path.length; i++)
				if (r instanceof Map)
					r = ((Map) r).get(path[i]);
				else if (r instanceof List) {
					Integer idx = Caster_Integer.INSTANCE.castNoThrow(path[i]);
					if (idx == null)
						return null;
					List l = (List) r;
					if (idx >= l.size() || idx < 0)
						return null;

					r = l.get(idx);
				} else
					return null;
			return r;
		}

		public String getName() {
			return name;
		}

	}

	static private void parseColumns(Object o, Map<String, Column> columns) throws AmiDatasourceException {
		HashSet<String> usedColumnNames = new HashSet<String>();

		Object t = o;
		if (o instanceof List) {
			List l = (List) o;
			if (l.size() == 0) {
				throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Could not figure out schema of empty list");
			} else {
				t = l.get(0);
			}
		}
		if (t instanceof Map) {
			Map<String, Object> m = (Map<String, Object>) t;
			for (Map.Entry<String, Object> e : m.entrySet()) {
				String name = e.getKey();
				Class<?> classForValueType = OH.getClass(e.getValue());
				if (classForValueType == null)
					classForValueType = String.class;
				byte type = AmiUtils.getTypeForClass(classForValueType, AmiDatasourceColumn.TYPE_STRING);
				classForValueType = AmiUtils.getClassForValueType(type);
				Column column = columns.get(name);

				if (column == null) {
					columns.put(name, new BasicColumn(classForValueType, name));
				} else if (column.getType() != classForValueType)
					throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Column `" + name + "` has inconsistent type '");
			}

		} else {
			//Type object

			String name = SH.getNextId("value", usedColumnNames);
			Class<?> classForValueType = OH.getClass(t);
			if (classForValueType == null)
				classForValueType = String.class;
			byte type = AmiUtils.getTypeForClass(classForValueType, AmiDatasourceColumn.TYPE_STRING);
			classForValueType = AmiUtils.getClassForValueType(type);
			Column column = columns.get(name);

			if (column == null) {
				columns.put(name, new BasicColumn(classForValueType, name));
			} else if (column.getType() != classForValueType)
				throw new AmiDatasourceException(AmiDatasourceException.SCHEMA_ERROR, "Column `" + name + "` has inconsistent type '");
		}

	}

	public static String parseSpecialKey(String key) {
		if (SH.isnt(key))
			return "_";
		StringBuilder r = new StringBuilder();
		StringCharReader c = new StringCharReader(key);
		SH.clear(r);
		c.expect('`');
		c.readUntilSkipEscaped('`', '\\', r);
		c.expect('`');
		return SH.toStringAndClear(r);
	}

	static private void parseColumns(String directiveName, String columnsString, Map<String, Column> columns) throws AmiDatasourceException {
		String[] parts = SH.trimArray(SH.split(',', columnsString));
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			String type = SH.beforeFirst(part, ' ', "String").trim();
			String name = SH.afterFirst(part, ' ', part).trim();
			String validName = name.charAt(0) == '`' ? parseSpecialKey(name) : AmiUtils.toValidVarName(name);
			//			if (!AmiUtils.isValidVariableName(name, false, false))
			//				throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, directiveName + " directive defines invalid column name: '" + name + "'");
			byte typeClass = AmiUtils.parseTypeName(type);
			Column column = columns.get(name);
			Class<?> classForValueType = AmiUtils.getClassForValueType(typeClass);
			if (column == null) {
				columns.put(name, new BasicColumn(classForValueType, validName));
			} else if (column.getType() != classForValueType)
				throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, directiveName + " has inconsistent type '" + type + "' for column: '" + name + "'");
		}
	}

	@Override
	public boolean cancelQuery() {
		Thread t = this.thread;
		if (t != null) {
			t.interrupt();
			return true;
		}
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		// TODO Auto-generated method stub
	}

	public byte[] doHttp(String username, char[] password, String httpMethod, String baseUrl, String urlExtension, String urlParams, Map<String, String> headers, String params,
			boolean ignoreCerts, int timeout, Map<String, List<String>> returnHeadersSink, boolean debug, boolean redirectFollowHttpMethod, boolean redirectFollowAuthHeader,
			boolean redirectPersistCookies) throws IOException {

		if (redirectFollowHttpMethod || redirectFollowAuthHeader)
			return RestHelper.sendRestRequestHandleRedirect(httpMethod, baseUrl, urlExtension, urlParams, headers, params, ignoreCerts, timeout, returnHeadersSink, debug,
					redirectFollowHttpMethod, redirectFollowAuthHeader, redirectPersistCookies);
		else
			return RestHelper.sendRestRequest(httpMethod, baseUrl + urlExtension + urlParams, headers, params, ignoreCerts, timeout, returnHeadersSink, debug);
	}
	//	public byte[] doGet(String username, char[] password, URL sourceUrl, Map<String, String> headers, boolean ignoreCerts, int timeout, Map<String, List<String>> returnHeadersSink)
	//			throws IOException {
	//		return IOH.doGet(sourceUrl, headers, returnHeadersSink, ignoreCerts, timeout);
	//	}
	public static void main(String[] args) throws MalformedURLException, IOException {
		String srcUrl = "https://gorest.co.in/public/v2/users";
		Map<String, List<String>> returnHeadersSink = new HashMap<String, List<String>>();
		Map<String, String> headers = new HashMap<String, String>();
		//		byte[] data = IOH.doGet(new URL(srcUrl), headers, returnHeadersSink);
		byte[] data = IOH.doPost(new URL(srcUrl), headers, "".getBytes(), returnHeadersSink);
		byte[] readData = null;
		if (data != null) {
			int type = FileMagic.getType(data);
			switch (type) {
				case FileMagic.FILE_TYPE_GZIP_COMPRESSED_DATA: {
					GZIPInputStream in = new GZIPInputStream(new FastByteArrayDataInputStream(data));
					readData = IOH.readData(in);
					break;
				}
				case FileMagic.FILE_TYPE_DEFLATE_COMPRESSED_DATA: {
					InflaterInputStream in = new InflaterInputStream(new FastByteArrayDataInputStream(data));
					readData = IOH.readData(in);
					break;
				}
				default:
					readData = data;
			}

		}

		Object restResult = null;
		if (readData != null) {
			restResult = ObjectToJsonConverter.INSTANCE_COMPACT.bytes2Object(readData);
		}
		System.out.println(restResult);
	}

}
