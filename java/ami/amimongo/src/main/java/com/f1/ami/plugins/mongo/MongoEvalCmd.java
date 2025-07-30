package com.f1.ami.plugins.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amiscript.AmiScriptMemberMethods_Object;
import com.f1.ami.plugins.mongo.AmiMongoDatasourceAdapter.MongoGetter;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoEvalCmd {

	private static final int NO_LIMIT = -1;
	private static final char NESTED_DELIM = '.';
	private static final char REPLACEMENT_DELIM = '_';
	final private String collectionName;
	final private String methodName;
	final private String args;
	final private String postfix;

	public static Block<Document> printBlock = new Block<Document>() {
		@Override
		public void apply(final Document document) {
			System.out.println(document.toJson());
		}
	};

	public MongoEvalCmd(String collName, String funcName, String argString, String postCommand) {
		this.collectionName = collName;
		this.methodName = funcName;
		this.args = argString;
		this.postfix = postCommand;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getArgs() {
		return args;
	}
	public String parseArgJSON() {
		String json = SH.equals(args, "") ? "{}" : args;
		return json;
	}
	public String getPostfix() {
		return postfix;
	}
	public static int countCursor(MongoCursor<Document> cursor) {
		int i = 0;
		while (cursor.hasNext()) {
			cursor.next();
			i++;
		}
		return i;
	}
	public static FindIterable<Document> mongoFind(MongoCmd sql, MongoEvalCmd cmd, MongoCollection<Document> col, int limit) {
		String parseArgJSON = cmd.parseArgJSON();
		FindIterable<Document> result = col.find(Document.parse(parseArgJSON)).projection(getProjectionDocument(sql));
		if (limit != NO_LIMIT)
			result = result.limit(limit);
		//		result.forEach(printBlock);
		return result;
	}

	public static List<Document> getRowsAndFindColumns(final MongoCmd sql, MongoCursor<Document> itr, List<String> addedColumns, Map<String, String> typesMap,
			Map<String, String> asMap) {
		ArrayList<Document> rows = new ArrayList<Document>();
		// Get all rows and store it into an list
		// Also get all column names

		if (sql.getIsSelectAll()) {
			while (itr.hasNext()) {
				Document next = itr.next();
				rows.add(next);

				HashSet<String> missingColumns = new HashSet<String>(next.keySet());
				missingColumns.removeAll(addedColumns);
				if (!CH.isEmpty(missingColumns)) {
					for (String nwCol : missingColumns) {
						addedColumns.add(nwCol);
						typesMap.put(nwCol, null);
						asMap.put(nwCol, nwCol);
					}
				}
			}

		} else {
			while (itr.hasNext()) {
				Document next = itr.next();
				rows.add(next);
			}

		}
		// Ensure valid column names replace periods with underbars
		for (Entry<String, String> e : asMap.entrySet()) {
			String asColumnId = e.getValue();
			String newAsColumnId = SH.replaceAll(asColumnId, NESTED_DELIM, REPLACEMENT_DELIM);
			if (!SH.equals(asColumnId, newAsColumnId))
				asMap.put(e.getKey(), newAsColumnId);
		}
		return rows;
	}

	public static void populateGetters(final Map<String, String> asMap, LinkedHashMap<String, MongoGetter> getters) {
		// Initialize Getters 
		for (Entry<String, String> entry : asMap.entrySet()) {
			getters.put(entry.getValue(), AmiMongoDatasourceAdapter.toGetter(entry.getKey()));
		}
	}
	private static Class<?> toClassFromValue(Object val) {
		Class<?> clazz = val.getClass();
		byte typeForClass = AmiUtils.getTypeForClass(clazz, AmiDatasourceColumn.TYPE_UNKNOWN);
		if (typeForClass == AmiDatasourceColumn.TYPE_UNKNOWN)
			return String.class;
		else
			return clazz;
	}
	private static String toTypeNameFromClass(Class<?> clazz) {
		byte typeForClass = AmiUtils.getTypeForClass(clazz, AmiDatasourceColumn.TYPE_UNKNOWN);
		if (typeForClass == AmiDatasourceColumn.TYPE_UNKNOWN)
			return AmiConsts.TYPE_NAME_STRING;
		String typeName = AmiUtils.toTypeName(typeForClass);
		if (typeName == null)
			return AmiConsts.TYPE_NAME_STRING;
		return typeName;
	}
	public static void populateClassTypesMapping(final List<String> addedColumns, final Map<String, String> typesMap, final Map<String, String> asMap, final List<Document> rows,
			final LinkedHashMap<String, MongoGetter> getters, com.f1.utils.structs.table.stack.BasicCalcTypes typesMapping) {
		// Add known types to the mapping 
		ArrayList<String> unknownTypesColumns = new ArrayList<String>();
		for (int i = 0; i < addedColumns.size(); i++) {
			String columnId = addedColumns.get(i);
			String asColumnId = asMap.get(columnId);
			String type = typesMap.get(columnId);

			if (type != null) {
				Class<?> clazz = AmiUtils.METHOD_FACTORY.forNameNoThrow(type);
				if (clazz == null)
					throw new RuntimeException("type not found: " + type);
				typesMapping.putType(asColumnId, clazz);
			} else {
				unknownTypesColumns.add(columnId);
			}

		}

		// Infer unknown types based on data
		for (int j = 0; j < unknownTypesColumns.size(); j++) {
			String columnId = unknownTypesColumns.get(j);
			String asColumnId = asMap.get(columnId);
			MongoGetter getter = getters.get(asColumnId);
			Class<?> existingType = null;
			for (int i = 0; i < rows.size(); i++) {
				Document document = rows.get(i);
				Object value = getter.get(document);
				if (value == null)
					continue;

				Class<?> clazz = value.getClass();
				if (existingType == null) {
					existingType = clazz;
				} else if (existingType != clazz) {
					existingType = OH.getWidest(existingType, clazz);
				}
			}

			// After getting the widest type for the dataset, get the Ami Column Type and convert it to the class 
			// Ie java.utils.Date will become DateMillis
			String type = toTypeNameFromClass(existingType);
			Class<?> clazz = AmiUtils.METHOD_FACTORY.forNameNoThrow(type);
			if (clazz == null)
				throw new RuntimeException("type not found: " + type);

			typesMapping.putType(asColumnId, clazz);
		}

	}
	public static ColumnarColumn<?>[] createColumns(ColumnarTable r, final com.f1.utils.structs.table.stack.BasicCalcTypes types) {
		// Create the table's columns using the as column names and types;
		int n = 0;
		ColumnarColumn<?>[] cols = new ColumnarColumn[types.getVarsCount()];
		for (String e : types.getVarKeys()) {
			cols[n++] = r.addColumn(types.getType(e), e);
		}
		return cols;
	}

	public static void addRowsAndCastData(ColumnarTable table, final ColumnarColumn<?>[] rColumns, final List<Document> rows, final LinkedHashMap<String, MongoGetter> getters) {
		// Iterate through the rows and add them to the table;
		for (int i = 0; i < rows.size(); i++) {
			Document json = rows.get(i);
			ColumnarRow row = table.newEmptyRow();
			for (ColumnarColumn<?> c : rColumns) {
				Object value = getters.get(c.getId()).get(json);
				if (c.getType() == String.class && (value instanceof Map || value instanceof Collection))
					row.putAt(c.getLocation(), AmiScriptMemberMethods_Object.JSON_CONVERTER.objectToString(value));
				else
					row.putAt(c.getLocation(), c.getTypeCaster().cast(value, false, false));
			}
			table.getRows().add(row);
		}
	}

	private static Document getProjectionDocument(MongoCmd sql) {
		if (sql.getD_project() != null) {
			return Document.parse(sql.getD_project());
		}
		//Get columns
		Document d = new Document();
		if (!sql.getIsSelectAll()) {
			List<String> l = sql.getSelect();
			for (int i = 0; i < l.size(); i++) {
				String columnId = l.get(i);
				columnId = SH.beforeFirst(columnId, NESTED_DELIM);
				d.put(columnId, 1);
			}
		}
		return d;
	}
	public static MongoCursor<Document> fromCollectionCursor(MongoCmd sql, MongoDatabase db, ContainerTools tools, int limit) {
		MongoCollection<Document> col = db.getCollection(sql.getCollectionName());
		FindIterable<Document> result = null;
		if (sql.getD_find() != null)
			result = col.find(Document.parse(sql.getD_find()));
		else
			result = col.find(Document.parse("{}"));
		//		else findItr = col.find(); // Same as above

		result = result.projection(getProjectionDocument(sql));
		if (sql.getD_sort() != null)
			result = result.sort(Document.parse(sql.getD_sort()));
		if ((sql.getD_skip() > 0)) {
			result = result.skip(sql.getD_skip());
		}
		if (limit != NO_LIMIT)
			result = result.limit(limit);

		//		MongoEvalCmd.convertFindIterableToTable(sql, table, result);
		return result.iterator();
	}

	public static MongoCursor<Document> evalCommandCursor(MongoCmd sql, MongoDatabase db, MongoQueryParser parser, ContainerTools tools, int limit) {
		MongoEvalCmd cmd = parser.parseEval(sql.getEval());
		String command = cmd.getMethodName().toLowerCase();
		MongoCollection<Document> col = db.getCollection(cmd.getCollectionName());
		if ("find".equals(command)) {
			FindIterable<Document> result = mongoFind(sql, cmd, col, limit);
			FindIterable<Document> postResult = evalCommand(cmd.getPostfix(), result, parser);

			return postResult.iterator();
			//			MongoEvalCmd.convertFindIterableToTable(sql, table, postResult);
		} else if ("aggregate".equals(command)) {
			return null;
		} else {
			return null;
		}

	}

	//runs FindIterable commands
	private static FindIterable<Document> evalCommand(String postfix, FindIterable<Document> findResult, MongoQueryParser parser) {
		if ("".equals(SH.trim(postfix))) {
			return findResult;
		}
		MongoEvalCmd cmd = parser.parseNextCommand(postfix);
		String command = cmd.getMethodName().toLowerCase();

		FindIterable<Document> result;
		if ("batchSize".equals(command)) {
			result = findResult.batchSize(SH.parseIntSafe(cmd.getArgs(), true, false));
		} else if ("filter".equals(command)) {
			result = findResult.filter(Document.parse(cmd.parseArgJSON()));
		} else if ("limit".equals(command)) {
			result = findResult.limit(SH.parseIntSafe(cmd.getArgs(), true, false));
		} else if ("skip".equals(command)) {
			result = findResult.skip(SH.parseIntSafe(cmd.getArgs(), true, false));
		} else if ("sort".equals(command)) {
			result = findResult.sort(Document.parse(cmd.parseArgJSON()));
		} else if ("projection".equals(command)) {
			result = findResult.projection(Document.parse(cmd.parseArgJSON()));
		} else {
			//throw exception
			return findResult;
		}
		FindIterable<Document> postResult = evalCommand(cmd.getPostfix(), result, parser);
		return postResult;
	}
}
