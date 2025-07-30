package com.f1.ami.center.ds;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.DateMillis;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiFredDatasourceAdapter implements AmiDatasourceAdapter {

	private AmiServiceLocator serviceLocator;
	private ContainerTools tools;
	private String apiKey;

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceLocator = serviceLocator;
		this.apiKey = new String(this.serviceLocator.getPassword());
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>();
		BasicTable samples = AmiDatasourceUtils.readResource(AmiQuandlDatasourceAdapter.class.getPackage(), "fred_datasources.txt");
		for (Row s : samples.getRows()) {
			String id = s.get("ID", Caster_String.INSTANCE);
			String desc = s.get("Description", Caster_String.INSTANCE);
			AmiDatasourceTable t = tools.nw(AmiDatasourceTable.class);
			t.setCollectionName(desc);
			t.setName(id);
			t.setCustomUse("_type=\"series_observation\" _series_id=\"" + id + "\"");
			t.setCreateTableClause("fred,readme");
			t.setColumns(Collections.EMPTY_LIST);
			t.setCustomQuery("select * from fred");
			r.add(t);
		}
		return r;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController timeout)
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
			List<Table> results = AmiUtils.processQuery(tools, this, q, debugSink, timeout);
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
		String type = AmiDatasourceUtils.getRequired(d, "type", "series_observation");
		String seriesId = AmiDatasourceUtils.getRequired(d, "series_id");
		String realtimeStart = AmiDatasourceUtils.getOptionalDate(d, "realtime_start");
		String realtimeEnd = AmiDatasourceUtils.getOptionalDate(d, "realtime_end");
		int offset = AmiDatasourceUtils.getOptionalInt(d, "offset");
		String sortOrder = AmiDatasourceUtils.getOptional(d, "sort_order", "asc", "desc");
		String observationStart = AmiDatasourceUtils.getOptionalDate(d, "observation_start");
		String observationEnd = AmiDatasourceUtils.getOptionalDate(d, "observation_end");
		String units = AmiDatasourceUtils.getOptional(d, "units", "chg", "ch1", "pch", "pc1", "pca", "cch", "cca", "log");
		String frequency = AmiDatasourceUtils.getOptional(d, "d", "w", "bw", "m", "q", "sa", "a", "wef", "weth", "wew", "wetu", "wem", "wesu", "wesa", "bwew", "bwem");
		String aggregationmethod = AmiDatasourceUtils.getOptional(d, "aggregation_method", "avg", "sum", "eop");

		int limit = query.getLimit();
		if ("series_observation".equals(type)) {
			StringBuilder url = new StringBuilder("https://api.stlouisfed.org/fred/series/observations?series_id=").append(seriesId).append("&api_key").append('=').append(apiKey)
					.append("&file_type=txt");
			append(url, "limit", limit);
			append(url, "realtime_start", realtimeStart);
			append(url, "realtime_end", realtimeEnd);
			append(url, "sort_order", sortOrder);
			append(url, "observation_start", observationStart);
			append(url, "observation_end", observationEnd);
			append(url, "units", units);
			append(url, "frequency", frequency);
			append(url, "aggregation_method", aggregationmethod);
			byte zip[];
			Map<String, String> options = SH.splitToMap(',', '=', '\\', serviceLocator.getOptions());
			boolean ignoreCerts = "true".equals(options.get("ignoreCerts"));
			try {
				zip = IOH.doGet(new URL(url.toString()), null, null, ignoreCerts, 60000);
			} catch (Exception e) {
				if (SH.indexOf(e.getMessage(), " incorrect Quandl code", 0) != -1)
					throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Unsupported Quandl code");
				if (SH.indexOf(e.getMessage(), "permission", 0) != -1)
					throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Permission Denied by quandl.com, please subscribe to this service first");
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
			}
			resultSink.setTables(new ArrayList<Table>());
			Map<String, Tuple2<ZipEntry, byte[]>> entries;
			try {
				entries = IOH.unzip(zip);
			} catch (IOException e) {
				throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Unzip failed", e);
			}
			Tuple2<ZipEntry, byte[]> dataEntry = entries.get(seriesId + "_1.txt");
			if (dataEntry != null) {
				Table r = new BasicTable();
				try {
					String raw = new String(dataEntry.getB());
					String lines[] = SH.splitLines(raw);
					String[] cols = SH.split('\t', lines[0]);
					byte[] types = new byte[cols.length];
					for (int n = 0; n < cols.length; n++) {
						String col = cols[n];
						if (SH.indexOf(col, "date", 0) != -1) {
							types[n] = TYPE_DATE;
							r.addColumn(DateMillis.class, col);
						} else if (OH.eq(seriesId, col)) {
							types[n] = TYPE_DOUBLE;
							r.addColumn(Double.class, "value");
						} else if (SH.indexOf(col, "value", 0) != -1) {
							types[n] = TYPE_DOUBLE;
							r.addColumn(Double.class, col);
						} else {
							types[n] = TYPE_STRING;
							r.addColumn(String.class, col);
						}
					}
					SimpleDateFormat p = new SimpleDateFormat("yyyy-MM-dd");
					for (int i = 1; i < lines.length; i++) {
						String[] parts = SH.split('\t', lines[i]);
						Row row = r.newEmptyRow();
						for (int n = 0; n < parts.length; n++) {
							String v = parts[n];
							if (".".equals(v) || SH.isnt(v))
								continue;
							switch (types[n]) {
								case TYPE_DOUBLE:
									row.putAt(n, SH.parseDouble(v));
									break;
								case TYPE_DATE:
									try {
										row.putAt(n, new DateMillis(p.parse(v).getTime()));
									} catch (ParseException e) {
									}
									break;
								default:
									row.putAt(n, v);
							}
						}
						r.getRows().add(row);
					}
					r.setTitle("fred");
				} catch (Exception e) {
					throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Parse " + dataEntry.getA().getName(), e);
				}
				if (!query.getQuery().equals("select * from fred"))
					r = AmiDatasourceUtils.processTable(query.getQuery(), query.getLimit(), r.getTitle(), r, debugSink, tc);
				resultSink.getTables().add(r);
				Tuple2<ZipEntry, byte[]> readmeEntry = entries.get(seriesId + "_README.txt");
				if (readmeEntry != null) {
					try {
						Table table = parseReadme(new String(readmeEntry.getB()));
						table.setTitle("readme");
						resultSink.getTables().add(table);
					} catch (Exception e) {
						throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Parse " + readmeEntry.getA().getName(), e);
					}
				}
			}
		}
	}

	private static final byte TYPE_STRING = 0;
	private static final byte TYPE_DOUBLE = 1;
	private static final byte TYPE_DATE = 2;

	private void append(StringBuilder sink, String key, int value) {
		if (value != -1)
			sink.append('&').append(key).append('=').append(value);
	}
	private void append(StringBuilder sink, String key, String value) {
		if (value != null)
			sink.append('&').append(key).append('=').append(value);
	}

	@Override
	public boolean cancelQuery() {
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

	private static Table parseCsv(String csv) throws IOException {
		StringCharReader scr = new StringCharReader(csv);
		StringBuilder sink = new StringBuilder();
		Table r = new BasicTable();
		for (;;) {
			if (scr.expectNoThrow('"')) {
				SH.clear(sink);
				for (;;) {
					scr.readUntil('"', sink);
					scr.expect('"');
					if (scr.expectNoThrow('"')) {
						sink.append('"');
					} else
						break;
				}

				String id = AmiUtils.toValidVarName(SH.toStringAndClear(sink));
				id = SH.getNextId(id, (Set) r.getColumnsMap().keySet());
				r.addColumn(String.class, id);
			} else {
				scr.readUntilAny(",\n", false, SH.clear(sink));
				String id = AmiUtils.toValidVarName(SH.toStringAndClear(sink));
				id = SH.getNextId(id, (Set) r.getColumnsMap().keySet());
				r.addColumn(String.class, id);
			}
			if (scr.expectNoThrow(','))
				continue;
			scr.expect('\n');
			break;
		}
		for (;;) {
			if (scr.isEof())
				break;
			Row row = r.newEmptyRow();
			for (int pos = 0;; pos++) {
				if (scr.expectNoThrow('"')) {
					SH.clear(sink);
					for (;;) {
						scr.readUntil('"', sink);
						scr.expect('"');
						if (scr.expectNoThrow('"')) {
							sink.append('"');
						} else
							break;
					}

					row.putAt(pos, SH.toStringAndClear(sink));
				} else {
					scr.readUntilAny(",\n", false, SH.clear(sink));
					row.putAt(pos, SH.toStringAndClear(sink));
				}
				if (scr.expectNoThrow(','))
					continue;
				scr.expect('\n');
				break;
			}
			r.getRows().add(row);
		}
		return r;
	}

	public static Table parseReadme(String text) {
		Table t = new BasicTable(String.class, "key", String.class, "value");
		String[] lines = SH.splitLines(text);
		String key = null;
		StringBuilder value = new StringBuilder();
		for (int linenum = 1; linenum < lines.length - 1; linenum++) {
			String line = lines[linenum];
			if (line.startsWith("----------")) {
				key = lines[linenum - 1];
				key = SH.beforeFirst(key, "    ", key);
				line = SH.beforeFirst(lines[++linenum], "   ");
				SH.trim(line, value);
				while (++linenum < lines.length) {
					line = lines[linenum];
					if (SH.isnt(line)) {
						t.getRows().addRow(key, value.toString());
						SH.clear(value);
						key = null;
						break;
					}
					SH.trim(line, value.append(' '));
				}
			}
		}
		return t;
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}
}
