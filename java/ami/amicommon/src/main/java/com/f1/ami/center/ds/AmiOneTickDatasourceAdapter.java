package com.f1.ami.center.ds;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.DateMillis;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_DateMillis;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.columnar.ColumnarTableList;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiOneTickDatasourceAdapter implements AmiDatasourceAdapter {

	private AmiServiceLocator serviceLocator;
	private ContainerTools tools;
	private Logger log = LH.get();

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceLocator = serviceLocator;
	}

	public static Map<String, String> buildOptions() {
		HashMap<String, String> r = new HashMap<String, String>();
		return r;
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>();
		AmiDatasourceTable t = tools.nw(AmiDatasourceTable.class);
		t.setName("Sample");
		t.setCustomUse("_s=\"20180201093000\" _e=\"20180201100000\" _symbol=\"MSFT\" _response=\"csv\" _timezone=\"America/New_York\"");
		t.setCreateTableClause("Sample");
		t.setColumns(Collections.EMPTY_LIST);
		t.setCustomQuery("38/151/otq/c4853462-32a1-474c-b6c6-ff3af6527202.otq");
		r.add(t);
		return r;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		for (AmiDatasourceTable table : tables) {
			String databaseCode = table.getName();
			String use = table.getCustomUse();
			if (use == null)
				continue;
			Map<String, String> useMap = SH.splitToMap(' ', '=', use);
			Map<String, Object> d = new LinkedHashMap();
			if (CH.isntEmpty(useMap))
				for (Entry<String, String> e : useMap.entrySet())
					if (e.getKey().startsWith("_"))
						d.put(e.getKey().substring(1), SH.trim('"', e.getValue()));
			AmiCenterQuery q = tools.nw(AmiCenterQuery.class);
			q.setQuery(table.getCustomQuery());
			q.setDirectives(d);
			AmiCenterQueryResult rs = tools.nw(AmiCenterQueryResult.class);
			q.setLimit(previewCount);
			List<Table> results = AmiUtils.processQuery(tools, this, q, debugSink, tc);
			if (CH.isntEmpty(results)) {
				table.setPreviewData(results.get(0));
				table.getPreviewData().setTitle(table.getName());
			}
		}
		return tables;
	}
	public String getName() {
		return this.serviceLocator.getTargetName();
	}

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		Map<String, Object> d = new HashMap<String, Object>(query.getDirectives());

		String compressionStr = AmiDatasourceUtils.getOptional(d, "compression", "off", "gzip");
		boolean compression = "gzip".equals(compressionStr);
		String srt = AmiDatasourceUtils.getOptional(d, "s");//"20180201093000";
		String end = AmiDatasourceUtils.getOptional(d, "e");//"20180201103000";
		String symbol = AmiDatasourceUtils.getOptional(d, "symbol");//"20180201103000";
		String fields = AmiDatasourceUtils.getOptional(d, "fields");//"20180201103000";
		String symbolDate = AmiDatasourceUtils.getOptional(d, "symbol_date");//"20180201103000";
		String timezone = AmiDatasourceUtils.getOptional(d, "timezone");//"20180201103000";
		String epse = AmiDatasourceUtils.getOptional(d, "enable_per_symbol_errors");//enable per symbol errors
		String db = AmiDatasourceUtils.getOptional(d, "db");//TAQ
		String url = getServiceLocator().getUrl();//"https://cldr.onetick.com:443/omdwebapi/rest/";
		String otq = query.getQuery();//"38/151/otq/c4853462-32a1-474c-b6c6-ff3af6527202.otq";
		String response = AmiDatasourceUtils.getOptional(d, "response", "csv", "json");
		if (SH.isnt(response))
			response = "csv";
		//		if (SH.isnt(srt) || SH.isnt(end))
		//			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR,
		//					"_s=<start datetime> and _e=<end datetime> required. Example: USE _s=20180201093000 _e=20180201103000 EXECUTE ....");

		URL sourceUrl;
		byte[] data;
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		Map<String, String> otqParams = new LinkedHashMap<String, String>();

		if (db != null)
			otqParams.put("db", db);
		if (symbol != null)
			otqParams.put("SYMBOL", "'" + symbol + "'");
		if (symbolDate != null)
			otqParams.put("SYMBOL_DATE", "'" + symbolDate + "'");
		for (Map.Entry<String, Object> t : d.entrySet())
			if (t.getKey().startsWith("otq_param_"))
				otqParams.put(SH.stripPrefix(t.getKey(), "otq_param_", true), t.getValue().toString());

		params.put("query_type", "otq");
		params.put("otq", otq);
		params.put("context", "DEFAULT");

		if (epse != null)
			params.put("enable_per_symbol_errors", epse);
		if (timezone != null)
			params.put("timezone", timezone);
		params.put("response", response);
		if (!otqParams.isEmpty())
			params.put("otq_params", SH.joinMap(',', '=', otqParams));

		if (compression)
			params.put("compression", "GZIP");
		if (srt != null)
			params.put("s", srt);
		if (end != null)
			params.put("e", end);
		//		String fields = "TIMESTAMP|EXCH_TIME|PRICE|SIZE|TRADE_ID";
		if (fields != null)
			params.put("format", CH.l("order=" + fields));
		long startTime, dataTime;
		try {
			StringBuilder sb = new StringBuilder(url);
			sb.append("?params=");
			sb.append(ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(params));
			String urlString = sb.toString();
			if (debugSink != null)
				debugSink.onQuery(urlString);

			sourceUrl = new URL(urlString);
			final Map<String, String> properties;
			final String name = this.getServiceLocator().getUsername();
			final String password = new String(this.getServiceLocator().getPassword());
			if (SH.is(name) || SH.is(password)) {
				String authString = name + ":" + password;
				String authEncBytes = EncoderUtils.encode64(authString.getBytes());
				properties = CH.m("Authorization", "Basic " + authEncBytes);
			} else
				properties = Collections.EMPTY_MAP;
			startTime = System.currentTimeMillis();
			LH.info(log, "OneTick query: ", urlString);
			data = IOH.doGet(sourceUrl, properties, null, true, tc.getTimeoutMillisRemaining());
			dataTime = System.currentTimeMillis();
			LH.info(log, "OneTick query returned ", AH.length(data), " byte(s) in ", dataTime - startTime, " milli(s)");
			if (compression) {
				try {
					data = IOH.readData(new GZIPInputStream(new FastByteArrayDataInputStream(data)));
				} catch (Exception e) {
					if (data.length < 10000)
						throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, new String(data));
				}
			}
		} catch (Exception e1) {
			if (SH.indexOf(e1.getMessage(), "response code: 401 ", 0) != -1)
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Bad username/password", e1);
			else
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e1);
		}
		String str = new String(data);
		if (SH.isnt(str)) {
			List<Table> tables = new ArrayList<Table>(1);
			tables.add(new BasicTable());
			resultSink.setTables(tables);
			return;
		}

		List<Table> tables = new ArrayList<Table>(1);
		resultSink.setTables(tables);
		int limit = query.getLimit();
		if ("csv".equals(response)) {
			String[] lines = SH.splitLines(str);
			for (int i = 0; i < lines.length; i++) {
				if (SH.startsWith(lines[i], "WARNING:")) {
					LH.warning(log, "From OneTick: ", lines[i]);
				} else {
					if (i > 0) {
						lines = AH.subarray(lines, i, lines.length - i);
					}
					break;

				}
			}
			if (lines.length == 0 || !lines[0].startsWith("#"))
				throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Response from OneTick: " + SH.ddd(str, 10000));
			try {
				String[] columns = SH.split(',', SH.stripPrefix(lines[0], "#", true));
				Class[] types = new Class[columns.length];
				if (lines.length == 1) {
					AH.fill(types, String.class);
				} else {
					for (int row = 1; row < 10 && row < lines.length; row++) {
						String[] values = SH.split(',', lines[row]);
						for (int i = 0; i < values.length; i++) {
							String s = values[i];
							Class type;
							switch (SH.evaluateNumber(s)) {
								case SH.NUMBER_EVALUATED_TO_FLOAT_NAN:
								case SH.NUMBER_EVALUATED_TO_FLOAT_POS_INF:
								case SH.NUMBER_EVALUATED_TO_FLOAT_NEG_INF:
								case SH.NUMBER_EVALUATED_TO_FLOAT_NO_DEC:
								default:
									type = Double.class;
									break;

								case SH.NUMBER_EVALUATED_TO_HEX_NEG:
								case SH.NUMBER_EVALUATED_TO_HEX_POS:
								case SH.NUMBER_EVALUATED_TO_HEX:
								case SH.NUMBER_EVALUATED_TO_STRING:
									type = String.class;
									break;
								case SH.NUMBER_EVALUATED_TO_NO_DEC:
									type = Long.class;
									break;
							}
							types[i] = OH.getWidestIgnoreNull(types[i], type);
						}
					}
				}
				Caster[] casters = new Caster[columns.length];
				for (int i = 0; i < columns.length; i++) {
					Class dstClass = types[i];
					if (dstClass == Long.class && columns[i].contains("TIME"))
						types[i] = DateMillis.class;

					casters[i] = OH.getCaster(dstClass);
				}
				ColumnarTable r = new ColumnarTable(types, columns);
				ColumnarTableList rows = r.getRows();
				for (int row = 0; row < lines.length; row++) {
					String text = lines[row];
					if (text.startsWith("#"))
						continue;
					String[] parts = SH.split(',', text);
					Object values[] = new Object[columns.length];
					for (int i = 0; i < columns.length; i++) {
						Object value = casters[i].castNoThrow(parts[i]);
						if (types[i] == DateMillis.class && value instanceof Long)
							value = new DateMillis((Long) value);
						values[i] = value;
					}
					rows.addRow(values);
					if (limit != -1 && rows.size() >= limit)
						break;
				}
				tables.add(r);

			} catch (Exception e1) {
				LH.warning(log, "Error parsing onetick csv results", e1);
				throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Could not parse csv results", e1);
			}
		} else {//json
			try {
				String[] lines = SH.splitLines(str);
				List<String> fieldNames = null;
				List<String> fieldTypes = null;
				Table table = null;
				outer: for (String line : lines) {
					if (line.startsWith("ERROR"))
						throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "From Onetick: " + line);
					Map<String, Object> map = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_COMPACT.stringToObject(line);
					String msgType = (String) map.get("MSG_TYPE");
					if ("ERROR".equals(msgType))
						LH.warning(log, "From Ontick: ", map);
					else if ("PROCESS_TICK_DESCRIPTOR".equals(msgType)) {
						List<String> fieldNames2 = (List<String>) map.get("FIELD_NAMES");
						List<String> fieldTypes2 = (List<String>) map.get("FIELD_TYPES");
						fieldNames2.add(0, "Symbol");
						fieldTypes2.add(0, "TYPE_STRING");
						fieldNames2.add(1, "Time");
						fieldTypes2.add(1, "TYPE_TIME_MSEC64");
						if (table == null) {
							fieldNames = fieldNames2;
							fieldTypes = fieldTypes2;
						} else {
							if (OH.ne(fieldNames, fieldNames2) || OH.ne(fieldTypes, fieldTypes2)) {
								fieldNames = fieldNames2;
								fieldTypes = fieldTypes2;
								tables.add(table);
								table = null;
							}
						}
						if (table == null) {
							if (fieldTypes.size() == 0)
								LH.warning(log, "FIELD_TYPES is empty");
							else if (fieldTypes.size() != fieldNames.size())
								LH.warning(log, "FIELD_TYPES length different from FIELD_NAMES");
							else {
								Class<?>[] types = new Class[fieldTypes.size()];
								for (int i = 0; i < types.length; i++)
									types[i] = parseType(fieldTypes.get(i));
								String[] names = AH.toArray(fieldNames, String.class);
								table = new BasicTable(types, names);
							}
						}
					} else if ("PROCESS_EVENT".equals(msgType)) {
						if (table == null) {
							LH.warning(log, "Missing PROCESS_TICK_DESCRIPTOR");
							continue;
						}
						int columnsCount = table.getColumnsCount();
						List<Object>[] columnsData = new List[columnsCount];
						Caster[] casters = new Caster[columnsCount];
						for (Entry<String, Object> e : map.entrySet()) {
							String key = e.getKey();
							if ("_SYMBOL_NAME".equals(key)) {
								columnsData[0] = CH.l((Object) e.getValue());
								casters[0] = Caster_String.INSTANCE;
							} else if ("TIMESTAMP".equals(key)) {
								columnsData[1] = (List<Object>) e.getValue();
								casters[1] = Caster_DateMillis.INSTANCE;
							} else {
								Column column = table.getColumnsMap().get(key);
								if (column != null) {
									columnsData[column.getLocation()] = (List<Object>) e.getValue();
									casters[column.getLocation()] = column.getTypeCaster();
								}
							}
						}
						int length = CH.size(columnsData[1]);//time column
						for (int i = 1; i < columnsData.length; i++) {
							List<Object> list = columnsData[i];
							if (list == null) {
								LH.warning(log, "Missing column: " + table.getColumnAt(i).getId());
								continue outer;
							} else if (list.size() != length) {
								LH.warning(log, "Column has wrong number of elements: " + table.getColumnAt(i).getId());
								continue outer;
							}
						}
						TableList rows = table.getRows();
						for (int y = 0; y < length; y++) {
							if (limit != -1 && rows.size() >= limit)
								break;
							Object[] values = new Object[columnsCount];
							values[0] = (String) columnsData[0].get(0);//special symbol column
							for (int x = 1; x < columnsCount; x++) {
								Caster caster = casters[x];
								if (caster == Caster_DateMillis.INSTANCE) {
									Long ms = Caster_Long.INSTANCE.cast(columnsData[x].get(y), false, false);
									values[x] = ms == null ? null : new DateMillis(ms);
								} else
									values[x] = caster.cast(columnsData[x].get(y), false, false);
							}
							rows.addRow(values);
						}
					} else if ("REPLICATE".equals(msgType) || "PROCESS_CALLBACK_LABEL".equals(msgType) || "PROCESS_SYMBOL_NAME".equals(msgType)
							|| "PROCESS_TICK_TYPE".equals(msgType) || "PROCESS_DATA_QUALITY_CHANGE".equals(msgType) || "PROCESS_SYMBOL_GROUP_NAME".equals(msgType)
							|| "QUERY_BEGIN".equals(msgType) || "DONE".equals(msgType) || "QUERY_END".equals(msgType)) {
						continue;
					} else {
						LH.warning(log, "Unknown msgtype From Ontick: ", SH.ddd(map.toString(), 10000));
					}

				}
				if (table != null)
					tables.add(table);
			} catch (Exception e1) {
				LH.warning(log, "Error parsing onetick json results", e1);
				throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, "Could not parse json results", e1);
			}

		}
		long cellsCount = 0;
		for (Table i : tables)
			cellsCount += (long) i.getSize() * i.getColumnsCount();
		LH.info(log, "AMI Processed ", cellsCount, " cell(s) in ", System.currentTimeMillis() - dataTime, " milli(s)");
	}

	private static final Map<String, Class> TYPES = CH.m("TYPE_STRING", String.class, "TYPE_TIME_MSEC64", DateMillis.class, "TYPE_DOUBLE", double.class, "TYPE_INT32", int.class,
			"TYPE_INT64", long.class, "TYPE_STRING", String.class);

	private Class<?> parseType(String type) {
		return CH.getOr(TYPES, type, String.class);
	}

	@Override
	public boolean cancelQuery() {
		return false;
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

}
