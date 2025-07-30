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
import java.util.regex.Matcher;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Column;
import com.f1.base.DateMillis;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiQuandlDatasourceAdapter implements AmiDatasourceAdapter {

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
		BasicTable ds;
		ds = AmiDatasourceUtils.readResource(AmiQuandlDatasourceAdapter.class.getPackage(), "quandl_datasources.txt");
		for (Row row : ds.getRows()) {
			AmiDatasourceTable t = tools.nw(AmiDatasourceTable.class);
			t.setCollectionName((String) row.get("Description") + " (" + row.get("Pricing") + ")");
			String databaseCode = row.get("DATABASE", Caster_String.INSTANCE);
			t.setName(databaseCode);
			t.setCreateTableClause(AmiUtils.toValidVarName(databaseCode));
			StringBuilder use = new StringBuilder();
			String type = (String) row.get("API");
			if ("table".equalsIgnoreCase(type))
				type = "table";
			else if ("time-series".equalsIgnoreCase(type))
				type = "timeseries";
			else
				throw new RuntimeException("bad API type in row: " + row);
			use.append("_type=\"").append(type).append('"');
			String datasetCode = (String) row.get("SAMPLE");
			use.append(" _database=\"").append(databaseCode).append('"');
			use.append(" _dataset=\"").append(datasetCode).append('"');

			t.setCustomUse(use.toString());
			t.setCustomQuery("select * from quandl");
			t.setColumns(Collections.EMPTY_LIST);
			r.add(t);
		}
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
			String type = useMap.get("_type");
			String datasetCode = useMap.get("_dataset");
			if (type != null && datasetCode != null) {
				type = SH.trim('"', type);
				datasetCode = SH.trim('"', datasetCode);
				AmiCenterQuery q = tools.nw(AmiCenterQuery.class);
				q.setQuery(table.getCustomQuery());
				Map<String, Object> d = CH.m(new LinkedHashMap<String, Object>(), "database", databaseCode, "type", type, "dataset", datasetCode);
				q.setDirectives(d);
				AmiCenterQueryResult rs = tools.nw(AmiCenterQueryResult.class);
				q.setLimit(previewCount);
				List<Table> results = AmiUtils.processQuery(tools, this, q, debugSink, tc);
				if (CH.isntEmpty(results)) {
					table.setPreviewData(results.get(0));
					table.getPreviewData().setTitle(databaseCode + "/" + datasetCode);
				}
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
		String type = AmiDatasourceUtils.getRequired(d, "type", "timeseries", "table");
		String databaseCode = AmiDatasourceUtils.getRequired(d, "database");
		String datasetCode = AmiDatasourceUtils.getRequired(d, "dataset");
		int limit = query.getLimit();
		Table r;
		if ("timeseries".equals(type)) {
			int columnIndex = AmiDatasourceUtils.getOptionalInt(d, "column_index");
			String startDate = AmiDatasourceUtils.getOptionalDate(d, "start_date");
			String endDate = AmiDatasourceUtils.getOptionalDate(d, "end_date");
			String order = AmiDatasourceUtils.getOptional(d, "order", "asc", "desc");
			String collapse = AmiDatasourceUtils.getOptional(d, "collapse", "none", "daily", "weekly", "monthly", "quarterly", "annual");
			String transform = AmiDatasourceUtils.getOptional(d, "transform", "none", "diff", "rdiff", "rdiff_from", "cumul", "normalize");
			AmiDatasourceUtils.checkForExtraDirectives(d);
			StringBuilder url = new StringBuilder("https://www.quandl.com/api/v3/datasets/").append(databaseCode).append('/').append(datasetCode).append('/')
					.append("data.csv?api_key").append('=').append(apiKey);
			if (limit != -1)
				append(url, "limit", limit);
			append(url, "column_index", columnIndex);
			append(url, "start_date", startDate);
			append(url, "end_date", endDate);
			append(url, "order", order);
			append(url, "collapse", collapse);
			append(url, "transform", transform);
			String csv;
			Map<String, String> options = SH.splitToMap(',', '=', '\\', serviceLocator.getOptions());
			boolean ignoreCerts = "true".equals(options.get("ignoreCerts"));
			try {
				csv = new String(IOH.doGet(new URL(url.toString()), null, null, ignoreCerts, 60000));
			} catch (Exception e) {
				if (SH.indexOf(e.getMessage(), " incorrect Quandl code", 0) != -1)
					throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Unsupported Quandl code");
				if (SH.indexOf(e.getMessage(), "permission", 0) != -1)
					throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Permission Denied by quandl.com, please subscribe to this service first");
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
			}
			String[] lines = SH.splitLines(csv);
			List<Column> columnNames;
			try {
				columnNames = AmiDatasourceUtils.parseCsv(lines[0] + '\n').getColumns();
			} catch (IOException e) {
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
			}
			r = new BasicTable();
			r.setTitle(databaseCode + "_" + datasetCode);
			for (Column s : columnNames) {
				Class<? extends Number> n = "Date".equals(s) ? DateMillis.class : Double.class;
				r.addColumn(n, (String) s.getId());
			}
			SimpleDateFormat p = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 1; i < lines.length; i++) {
				String[] row = SH.split(',', lines[i]);
				Object[] row2 = new Object[row.length];
				try {
					row2[0] = new DateMillis(p.parse((String) row[0]).getTime());
				} catch (ParseException e) {
				}
				for (int n = 1; n < row.length; n++) {
					String s = row[n];
					if (SH.is(s))
						row2[n] = SH.parseDouble(s);
				}
				r.getRows().addRow(row2);
			}
		} else if ("table".equals(type)) {
			String filter = AmiDatasourceUtils.getOptional(d, "filters");
			String columns = AmiDatasourceUtils.getOptional(d, "columns");
			AmiDatasourceUtils.checkForExtraDirectives(d);
			StringBuilder url = new StringBuilder("https://www.quandl.com/api/v3/datatables/").append(databaseCode).append('/').append(datasetCode).append(".csv?api_key")
					.append('=').append(apiKey);
			if (SH.is(columns))
				url.append("&qopts.columns=").append(columns);
			if (SH.is(filter)) {
				filter = SH.replaceAll(filter, ">", ".gt=");
				filter = SH.replaceAll(filter, "<", ".lt=");
				filter = SH.replaceAll(filter, ">=", ".gte=");
				filter = SH.replaceAll(filter, "<=", ".lte=");
				url.append('&').append(filter);
			}
			if (limit != -1)
				url.append("&qopts.per_page=").append(limit);
			String csv;
			Map<String, String> options = SH.splitToMap(',', '=', '\\', serviceLocator.getOptions());
			boolean ignoreCerts = "true".equals(options.get("ignoreCerts"));
			try {
				csv = new String(IOH.doGet(new URL(url.toString()), null, null, ignoreCerts, 60000));
				Table table = AmiDatasourceUtils.parseCsv(csv);
				table = convertTypes(table);
				r = table;
			} catch (Exception e) {
				if (SH.indexOf(e.getMessage(), " incorrect Quandl code", 0) != -1)
					throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Unsupported Quandl code");
				if (SH.indexOf(e.getMessage(), "permission", 0) != -1)
					throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Permission Denied by quandl.com, please subscribe to this service first");
				if (e.getCause() != null && SH.indexOf(e.getCause().getMessage(), "permission", 0) != -1)
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e.getCause().getMessage(), e);
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, e);
			}

		} else
			r = null;
		if (r != null) {

			r.setTitle("quandl");
			if (!query.getQuery().equals("select * from quandl"))
				r = AmiDatasourceUtils.processTable(query.getQuery(), query.getLimit(), r.getTitle(), r, debugSink, tc);
			resultSink.setTables(CH.l(r));
		}
	}

	private static final byte TYPE_STRING = 0;
	private static final byte TYPE_DOUBLE = 1;
	private static final byte TYPE_DATE = 2;

	private Table convertTypes(Table table) {
		Matcher matcherDate = AmiDatasourceUtils.DATE_PATTERN.matcher("");
		Matcher matcherNumber = AmiDatasourceUtils.NUMBER_PATTERN.matcher("");
		Table r = new BasicTable();
		byte[] types = new byte[table.getColumnsCount()];
		for (int i = 0; i < table.getColumnsCount(); i++) {
			byte type = TYPE_STRING;
			for (Row row : table.getRows()) {
				String value = (String) row.getAt(i);
				if (SH.isnt(value))
					continue;
				switch (type) {
					case TYPE_STRING:
						if (matcherDate.reset(value).matches())
							type = TYPE_DATE;
						else if (matcherNumber.reset(value).matches())
							type = TYPE_DOUBLE;
						break;
					case TYPE_DATE:
						if (!matcherDate.reset(value).matches())
							type = TYPE_STRING;
						break;
					case TYPE_DOUBLE:
						if (!matcherNumber.reset(value).matches())
							type = TYPE_STRING;
						break;
				}
				if (type == TYPE_STRING)
					break;
			}
			switch (type) {
				case TYPE_STRING:
					r.addColumn(String.class, table.getColumnAt(i).getId());
					break;
				case TYPE_DATE:
					r.addColumn(DateMillis.class, table.getColumnAt(i).getId());
					break;
				case TYPE_DOUBLE:
					r.addColumn(Double.class, table.getColumnAt(i).getId());
					break;
			}
			types[i] = type;
		}
		SimpleDateFormat p = new SimpleDateFormat("yyyy-MM-dd");
		for (Row row : table.getRows()) {
			final Row nr = r.newEmptyRow();
			for (int i = 0; i < table.getColumnsCount(); i++) {
				String value = (String) row.getAt(i);
				if (SH.isnt(value))
					continue;
				switch (types[i]) {
					case TYPE_STRING:
						nr.putAt(i, value);
						break;
					case TYPE_DOUBLE:
						nr.putAt(i, SH.parseDouble(value));
						break;
					case TYPE_DATE:
						try {
							nr.putAt(i, new DateMillis(p.parse((String) value).getTime()));
						} catch (ParseException e) {
						}
						break;
				}
			}
			r.getRows().add(nr);
		}
		return r;
	}

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

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}
}
