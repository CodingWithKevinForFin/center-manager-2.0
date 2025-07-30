package com.f1.ami.plugins.couchbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.cluster.BucketSettings;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
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
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiCouchbaseDatasourceAdapter implements AmiDatasourceAdapter {
	private static final Logger log = LH.get();
	private AmiServiceLocator locator;
	private ContainerTools tools;

	public static Map<String, String> buildOptions() {
		return new HashMap<String, String>();
	}
	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.locator = serviceLocator;
		this.tools = tools;
	}
	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		Cluster cluster = getConnection();
		try {
			List<BucketSettings> buckets = cluster.clusterManager().getBuckets();
			List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>(buckets.size());
			for (BucketSettings i : buckets)
				r.add(toTable(i));
			return r;
		} finally {
			cluster.disconnect();
		}
	}
	private AmiDatasourceTable toTable(BucketSettings i) {
		AmiDatasourceTable r = tools.nw(AmiDatasourceTable.class);
		r.setName(i.name());
		r.setCustomUse("_bucket=\"" + i.name() + "\"");
		r.setCreateTableClause(AmiUtils.toValidVarName(i.name()));
		r.setDatasourceName(this.locator.getTargetName());
		r.setCustomQuery("select * from `" + i.name() + "`");
		return r;
	}
	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		Cluster cluster = getConnection();
		try {
			for (AmiDatasourceTable i : tables) {
				toColumnsDef(i, 100, cluster);
				AmiCenterQuery q = tools.nw(AmiCenterQuery.class);
				q.setLimit(previewCount);
				q.setQuery(i.getCustomQuery());
				q.setDirectives((Map) CH.m(new LinkedHashMap<String, Object>(), "bucket", i.getName()));
				List<Table> previewData = AmiUtils.processQuery(this.tools, this, q, debugSink, tc);
				i.setPreviewData(previewData.get(0));
			}
		} finally {
			cluster.disconnect();
		}
		return tables;
	}
	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.locator;
	}
	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		Map<String, Object> d = query.getDirectives();
		String buckets = AmiDatasourceUtils.getOptional(d, "bucket");
		String columns = AmiDatasourceUtils.getOptional(d, "columns");
		if (SH.isnt(buckets))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_bucket directive required");
		Cluster cluster = getConnection();
		try {
			String[] bucketsStr = SH.splitWithEscape(',', '\\', buckets);
			for (String bucket : bucketsStr)
				cluster.openBucket(bucket);
			String q = query.getQuery();
			int limit = query.getLimit();
			if (limit > 0)
				q += " LIMIT " + limit;
			N1qlQueryResult result = cluster.query(N1qlQuery.simple(q), tc.getTimeoutMillisRemaining(), TimeUnit.MILLISECONDS);
			ColumnarTable t = new ColumnarTable();
			t.setTitle(bucketsStr[0]);
			if (SH.is(columns)) {
				for (String c : SH.split(',', columns)) {
					c = SH.trim(c);
					String type = SH.trim(SH.beforeFirst(c, ' '));
					String name = SH.trim(SH.afterFirst(c, ' '));
					t.addColumn(AmiUtils.getClassForValueType(AmiUtils.parseTypeName(type)), name);
				}
				Column[] cols = AH.toArray(t.getColumns(), Column.class);
				for (N1qlQueryRow i : result) {
					Object o[] = new Object[cols.length];
					final JsonObject object = i.value();
					for (String tn : object.getNames()) {
						Object o2 = object.get(tn);
						Column col = t.getColumnsMap().get(tn);
						if (col != null) {
							o[col.getLocation()] = col.getTypeCaster().cast(o2);
						} else if (o2 instanceof JsonObject) {//lets assume this is the result of a *
							JsonObject object2 = (JsonObject) o2;
							for (int n = 0; n < o.length; n++) {
								o[n] = cols[n].getTypeCaster().cast(object2.get((String) cols[n].getId()));
							}
						}
					}
					t.getRows().addRow(o);
				}
			} else {
				t.addColumn(String.class, "json");
				for (N1qlQueryRow i : result) {
					JsonObject object = i.value();
					t.getRows().addRow(object.toString());
				}
			}
			resultSink.setTables((List) CH.l(t));
		} finally {
			cluster.disconnect();
		}

	}
	@Override
	public boolean cancelQuery() {
		return false;
	}
	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult resultsSink, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

	protected Cluster getConnection() throws AmiDatasourceException {
		String url = this.getServiceLocator().getUrl();
		url = "localhost";
		try {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Getting connection: ", this.locator);
			CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder().connectTimeout(20000).build();
			Cluster cluster = CouchbaseCluster.create(env, url);
			cluster.authenticate(this.locator.getUsername(), new String(this.locator.getPassword()));

			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Got connection: ", url);
			return cluster;
		} catch (Throwable e) {
			throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, "For Url: " + url, e);
		}
	}
	private void toColumnsDef(AmiDatasourceTable table, int sampleSize, Cluster cluster) {
		String name = table.getName();
		N1qlQueryResult result = cluster.openBucket(name).query(N1qlQuery.simple(table.getCustomQuery() + " limit " + sampleSize), 10000, TimeUnit.MILLISECONDS);
		BasicMultiMap.Set<String, Byte> columnTypes = new BasicMultiMap.Set<String, Byte>();
		BasicMultiMap.Set<String, Byte> columnHints = new BasicMultiMap.Set<String, Byte>();
		columnTypes.setInnerMap(new java.util.LinkedHashMap<String, Set<Byte>>());
		int count = 0;
		if (sampleSize > 0) {
			for (N1qlQueryRow row : result) {
				JsonObject object = row.value();
				object = object.getObject(name);
				if (object == null)
					continue;
				for (String key : object.getNames()) {
					Object value = object.get(key);
					if (value == null)
						continue;
					Byte type = AmiUtils.getTypeForClass(value.getClass(), AmiDatasourceColumn.TYPE_STRING);
					if (value instanceof JsonObject)
						columnHints.putMulti(key, AmiDatasourceColumn.HINT_JSON);
					columnTypes.putMulti(key, type);
				}

				if (++count >= sampleSize)
					break;
			}
		}
		List<AmiDatasourceColumn> columns = new ArrayList<AmiDatasourceColumn>();
		for (Entry<String, Set<Byte>> entry : columnTypes.entrySet()) {
			Set<Byte> hint = columnHints.get(entry.getKey());
			AmiDatasourceColumn column = tools.nw(AmiDatasourceColumn.class);
			column.setName(entry.getKey());
			byte type = AmiDatasourceColumn.TYPE_UNKNOWN;
			for (Byte t : entry.getValue()) {
				if (type == AmiDatasourceColumn.TYPE_UNKNOWN)
					type = t.byteValue();
				else
					type = AmiUtils.getNarrowestType(type, t.byteValue());
			}
			column.setType(type);
			if (CH.isntEmpty(hint) && AmiDatasourceColumn.TYPE_STRING == type) {
				column.setHint(CH.first(hint));
			}
			columns.add(column);
		}
		String use = table.getCustomUse();
		StringBuilder sb = new StringBuilder(use);
		sb.append(" _columns=\"");
		boolean first = true;
		for (AmiDatasourceColumn i : columns) {
			if (first)
				first = false;
			else
				sb.append(",");
			sb.append(AmiUtils.toTypeName(i.getType())).append(' ').append(i.getName());
		}
		sb.append("\"");
		table.setCustomUse(sb.toString());

		table.setColumns(columns);
	}
}
