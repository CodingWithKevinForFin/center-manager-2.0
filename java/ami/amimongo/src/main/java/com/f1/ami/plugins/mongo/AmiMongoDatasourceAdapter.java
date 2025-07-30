package com.f1.ami.plugins.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.f1.ami.amicommon.AmiDatasourceAbstractAdapter;
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
import com.f1.base.Getter;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConditional;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.connection.SslSettings;

public class AmiMongoDatasourceAdapter extends AmiDatasourceAbstractAdapter {
	private static final String DEFLT_DB = "";
	private static final Logger log = LH.get();
	private static final String OPTION_SSL_PROTOCOL = "SSL";
	private static final String OPTION_DB = "DB";
	protected static final String USE_DIRECTIVE_MONGO_FIND = "find";
	protected static final String USE_DIRECTIVE_MONGO_PROJECTION = "project";
	protected static final String USE_DIRECTIVE_MONGO_SKIP = "skip";
	protected static final String USE_DIRECTIVE_MONGO_SORT = "sort";

	private long startTimeNano;
	private long endTimeNano;
	private String name;
	private String url;
	private String username;
	private char[] password;
	private String dbname;
	private Map<String, String> options;
	private MongoDatabase mongoDb;
	private MongoClient mongoClient;
	private ContainerTools tools;
	private AmiServiceLocator locator;
	private MongoQueryParser parser;
	private StringBuilder uriBuilder = new StringBuilder();
	private boolean hasCustomOptions;

	public static Map<String, String> buildOptions() {
		HashMap<String, String> r = new HashMap<String, String>();
		r.put(OPTION_SSL_PROTOCOL, "true=use ssl protocol (default=false)");
		r.put(OPTION_DB, "force db to connect to instead of extracting from url");
		return r;
	}

	public String toConnectionString(ContainerTools tools, String name, String url, String username, String password, Map<String, String> options) {
		SH.clear(uriBuilder);

		if (SH.equals("", username))
			uriBuilder.append("mongodb://").append(url);
		else
			uriBuilder.append("mongodb://").append(SH.encodeUrl(username)).append(":").append(SH.encodeUrl(password)).append('@').append(url);
		boolean isFirst = true;
		if (!hasCustomOptions && options.size() != 0)
			uriBuilder.append('?');

		for (String key : options.keySet()) {
			if (!isFirst || hasCustomOptions)
				uriBuilder.append('&');
			uriBuilder.append(key).append('=').append(options.get(key));
			isFirst = false;
		}

		return uriBuilder.toString();
	}
	private void ensureConnected() throws AmiDatasourceException {
		this.startTimeNano = System.nanoTime();
		if (this.mongoClient == null) {
			String connectionString = toConnectionString(tools, name, url, username, new String(password), options);
			LH.fine(log, "Connecting to MongoDB using JDBC URL: ", connectionString);

			ConnectionString cs = new ConnectionString(connectionString);
			Builder builder = MongoClientSettings.builder();
			builder.applyConnectionString(cs);

			String optionSSL = SH.toLowerCase(this.getOption(OPTION_SSL_PROTOCOL, "false"));
			if (SH.equals(optionSSL, "true")) {
				//				builder.applyToSslSettings(builder -> builder.enabled(true)); //Java 1.8
				builder = builder.applyToSslSettings(new Block<SslSettings.Builder>() {
					@Override
					public void apply(com.mongodb.connection.SslSettings.Builder t) {
						t.enabled(true);
					}
				});
			}

			MongoClientSettings settings = builder.build();
			this.mongoClient = MongoClients.create(settings);
		}
		try {
			if (this.mongoDb == null)
				this.mongoDb = mongoClient.getDatabase(dbname); // this method does not check if this database exists in the client
		} catch (Exception e) {
			mongoClient.close();
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Path syntax is ==> host:port/dbname", e);
		}
		this.endTimeNano = System.nanoTime();
		if (log.isLoggable(Level.FINEST))
			LH.finest(log, this.getClass().getSimpleName(), " Check connection ran in ", (endTimeNano - startTimeNano) / 1000, " micros");
	}

	@Override
	public void init(ContainerTools tools, AmiServiceLocator locator) throws AmiDatasourceException {
		super.init(tools, locator);
		this.parser = new MongoQueryParser();
		this.locator = locator;
		this.name = locator.getTargetName();
		this.url = locator.getUrl();
		this.username = locator.getUsername();
		this.password = locator.getPassword();
		this.options = SH.splitToMap(',', '=', '\\', this.locator.getOptions());
		String dbAndOptions = SH.afterFirst(url, '/');
		this.dbname = SH.beforeFirst(dbAndOptions, '?');
		this.hasCustomOptions = dbAndOptions.contains("?");
		String useDb = SH.toLowerCase(this.getOption(OPTION_DB, ""));
		if (!SH.equals(DEFLT_DB, useDb))
			this.dbname = useDb;
		this.tools = tools;
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		ensureConnected();
		this.startTimeNano = System.nanoTime();
		try {
			MongoIterable<String> names = mongoDb.listCollectionNames();

			List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>();
			for (String name : names) {
				MongoCollection<Document> collection = mongoDb.getCollection(name);
				AmiDatasourceTable tableDef = toAmiDatasourceTable(collection);
				r.add(tableDef);
			}
			this.endTimeNano = System.nanoTime();
			if (log.isLoggable(Level.FINER))
				LH.finer(log, this.getClass().getSimpleName(), " Get tables in: ", (endTimeNano - startTimeNano) / 1000, " micros");
			return r;
		} finally {
			mongoClient.close();
		}
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		ensureConnected();
		try {
			for (int i = 0; i < tables.size(); i++) {
				AmiDatasourceTable table = tables.get(i);
				// Set up Query
				AmiCenterQuery q = tools.nw(AmiCenterQuery.class);

				// Build use directives
				Map<String, Object> directives = new LinkedHashMap<String, Object>();
				//	directives.put(USE_DIRECTIVE_EVAL_CMD, null);

				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, Object> e : directives.entrySet()) {
					sb.append('_').append(e.getKey()).append('=').append(SH.doubleQuote((String) e.getValue())).append(' ');
				}

				q.setQuery("SELECT * FROM " + table.getName() + " WHERE true");
				q.setLimit(previewCount);
				q.setDirectives(directives);

				// Get Preview data
				List<Table> previewData = AmiUtils.processQuery(this.tools, this, q, debugSink, tc);

				// Get Columns
				Table rs = previewData.get(0);

				List<AmiDatasourceColumn> columnsDef = toColumnsDef(rs, table);
				String customQuery = this.toQueryDef(rs, table, new StringBuilder()).toString();

				table.setCustomQuery(customQuery);
				table.setCustomUse(sb.toString());
				table.setColumns(columnsDef);
				table.setPreviewData(rs);
			}
			return tables;
		} finally {
			mongoClient.close();
		}
	}

	private List<AmiDatasourceColumn> toColumnsDef(Table previewTable, AmiDatasourceTable table) {
		List<AmiDatasourceColumn> amiCols = new ArrayList<AmiDatasourceColumn>();
		int columnsCount = previewTable.getColumnsCount();
		if (columnsCount == 0) {
			return null;
		} else {
			List<Column> cols = previewTable.getColumns();
			for (int i = 0; i < cols.size(); i++) {
				Column col = cols.get(i);
				AmiDatasourceColumn newAmiColumn = tools.nw(AmiDatasourceColumn.class);
				newAmiColumn.setName(Caster_String.INSTANCE.cast(col.getId()));
				newAmiColumn.setType(AmiUtils.getTypeForClass(col.getType(), AmiDatasourceColumn.TYPE_UNKNOWN));
				amiCols.add(newAmiColumn);

			}

		}

		return amiCols;
	}
	private StringBuilder toQueryDef(Table previewTable, AmiDatasourceTable table, StringBuilder sb) {
		int columnsCount = previewTable.getColumnsCount();
		if (columnsCount == 0) {
			//			sb.append("SELECT * FROM db.").append(table.getName()).append(".find() WHERE ${WHERE}");
			sb.append("SELECT * FROM ").append(table.getName()).append(" WHERE ${WHERE}");
		} else {
			List<Column> cols = previewTable.getColumns();
			sb.append("SELECT ");
			for (int i = 0; i < cols.size(); i++) {
				Column col = cols.get(i);
				sb.append('(');
				sb.append(AmiUtils.toTypeName(col.getType()));
				sb.append(')').append(' ');
				sb.append(col.getId());
				if (i != (cols.size() - 1))
					sb.append(',').append(' ');

			}
			sb.append(" FROM ").append(table.getName()).append(" WHERE ${WHERE}");
		}
		return sb;
	}

	private AmiDatasourceTable toAmiDatasourceTable(MongoCollection<Document> collection) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		table.setName(collection.getNamespace().getCollectionName());
		return table;
	}

	//	public Table query(String table, List<String> columns, DerivedCellCalculator where, int limit) throws AmiDatasourceException {
	//		ensureConnected();
	//		final String[] cols = columns.toArray(new String[columns.size()]);
	//		final BasicTable r = new BasicTable(cols);
	//		final Getter<Document, Object>[] gets = new Getter[cols.length];
	//		for (int i = 0; i < cols.length; i++)
	//			gets[i] = toGetter(cols[i]);
	//		final MongoCollection<Document> collection = this.mongoDb.getCollection(table);
	//		Iterable<Document> docs;
	//		if (limit < 1)
	//			return r;
	//		if (where instanceof DerivedCellCalculatorConst) {
	//			if (Boolean.TRUE.equals(where.get(null)))
	//				docs = collection.find();
	//			else
	//				docs = EmptyIterable.INSTANCE;
	//		} else {
	//			Document whereObject = (Document) toWhere(where);
	//			docs = collection.find(whereObject);
	//		}
	//		int cnt = 0;
	//		for (final Document doc : docs) {
	//			final Object[] row = new Object[columns.size()];
	//			for (int i = 0; i < cols.length; i++) {
	//				final Object val = gets[i].get((Document) doc);
	//				if (val instanceof DBObject || val instanceof ObjectId)
	//					row[i] = val.toString();
	//				else
	//					row[i] = val;
	//			}
	//			r.getRows().addRow(row);
	//			if (++cnt >= limit)
	//				break;
	//		}
	//		return r;
	//	}

	private static final Map<String, String> OPERATIONS = CH.m("==", "$eq", "&&", "$and", "||", "$or", "*=", "*match", "!", "$not", "!=", "$ne", ">", "$gt", "<", "$lt", "<=",
			"$lte", ">=", "$gte", "/", "$divide", "-", "$subtract", "*", "$multiply", "%", "$mod", "+", "$add");
	private static final Map<String, String> OPERATION_SWAPS = CH.m("<", ">", ">", "<", "<=", ">=", ">=", "<=");

	private Object toWhere(DerivedCellCalculator where) throws AmiDatasourceException {
		if (where instanceof DerivedCellCalculatorMath) {
			DerivedCellCalculatorMath m = (DerivedCellCalculatorMath) where;
			String operation = m.getOperationString();
			DerivedCellCalculator left = m.getLeft();
			DerivedCellCalculator right = m.getRight();
			if ("||".equals(operation)) {
				return new BasicDBObject("$or", toList(toWhere(left), toWhere(right)));
			} else if ("&&".equals(operation)) {
				return new BasicDBObject("$and", toList(toWhere(left), toWhere(right)));
			} else if ("!".equals(operation)) {
				return new BasicDBObject("$not", toWhere(left));
			}
			boolean swapped;
			if (right instanceof DerivedCellCalculatorRef) {
				left = right;
				right = m.getLeft();
				swapped = true;
			} else if (left instanceof DerivedCellCalculatorRef)
				swapped = false;
			else
				throw new RuntimeException("Could not convert to mongodb: " + where);

			if ("==".equals(operation)) {
				return new BasicDBObject((String) toWhere(left), toWhere(right));
			} else {
				if (swapped)
					operation = CH.getOr(OPERATION_SWAPS, operation, operation);
				operation = CH.getOrThrow(OPERATIONS, operation);
				return new BasicDBObject((String) toWhere(left), new BasicDBObject(operation, toWhere(right)));
			}
		} else if (where instanceof DerivedCellCalculatorConst) {
			DerivedCellCalculatorConst m = (DerivedCellCalculatorConst) where;
			return m.get(null);
		} else if (where instanceof DerivedCellCalculatorConditional) {
			DerivedCellCalculatorConditional m = (DerivedCellCalculatorConditional) where;
			return new BasicDBObject("$cond", toList(toWhere(m.getCondition()), toWhere(m.getTrue()), toWhere(m.getFalse())));
		} else if (where instanceof DerivedCellCalculatorRef) {
			DerivedCellCalculatorRef m = (DerivedCellCalculatorRef) where;
			return m.getId();
		} else
			throw new RuntimeException("Cant convert to mongo language: " + where);
	}
	private BasicDBList toList(Object a, Object b) {
		BasicDBList r = new BasicDBList();
		r.add(a);
		r.add(b);
		return r;
	}
	private BasicDBList toList(Object a, Object b, Object c) {
		BasicDBList r = new BasicDBList();
		r.add(a);
		r.add(b);
		r.add(c);
		return r;
	}
	public String getName() {
		return name;
	}

	public static MongoGetter toGetter(String path) {
		if ("_".equals(path))
			return ALL;
		if (path.indexOf('.') == -1)
			return new SimpleGetter(path);
		else
			return new NestedGetter(path);

	}

	public static final MongoGetter ALL = new SimpleGetter(null);

	public static abstract class MongoGetter implements Getter<Document, Object> {

	}

	public static class SimpleGetter extends MongoGetter implements Getter<Document, Object> {
		private String path;

		public SimpleGetter(String path) {
			super();
			this.path = path;
		}

		@Override
		public Object get(Document document) {
			Object r = path == null ? document : document.get(path);
			return r;
		}

	}

	public static class NestedGetter extends MongoGetter implements Getter<Document, Object> {
		private String path[];

		public NestedGetter(String path) {
			super();
			if (path.startsWith("_."))
				this.path = SH.split('.', path.substring(2));
			else
				this.path = SH.split('.', path);
		}

		@Override
		public Object get(Document document) {
			Object r = document.get(path[0]);
			for (int i = 1; i < path.length; i++)
				if (r instanceof List) {
					List l = (List) r;
					int index = Integer.parseInt(path[i]);
					if (index >= l.size())
						return null;
					else
						r = l.get(index);
				} else if (!(r instanceof Document))
					return null;
				else
					r = ((Document) r).get(path[i]);
			return r;
		}
	}

	@Override
	public void processQuery(AmiCenterQuery q, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		this.ensureConnected();
		this.startTimeNano = System.nanoTime();

		final List<Table> results = new ArrayList<Table>();
		Table table = null;
		try {
			MongoCmd sql = parser.parseSqlCmd(q);
			Table mTable = MongoCmd.runCommand(sql, mongoDb, parser, tools);

			String filterQuery = "SELECT * FROM this";
			if (SH.is(sql.getWhere()))
				filterQuery += " WHERE " + sql.getWhere();
			mTable = AmiDatasourceUtils.processTable(filterQuery, q.getLimit(), "this", mTable, debugSink, tc);
			mTable.setTitle(sql.getCollectionName());
			table = mTable;

			this.endTimeNano = System.nanoTime();
			if (log.isLoggable(Level.FINER))
				LH.finer(log, this.getClass().getSimpleName(), " Process Query for mongo collection: ", sql.getCollectionName(), " ran in: ", (endTimeNano - startTimeNano) / 1000,
						" micros");

		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "MongoDB Query compilation failed", e);
		} finally {
			mongoClient.close();

		}
		if (table != null)
			results.add(table);
		resultSink.setTables(results);

	}

	public FindIterable<Document> runQuery() {
		return null;

	}

	public boolean cancelQuery() {
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.locator;
	}

}
