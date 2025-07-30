package com.f1.ami.plugins.mongo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.f1.ami.plugins.mongo.AmiMongoDatasourceAdapter.MongoGetter;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoCmd {
	final private List<String> select;
	final private Map<String, String> asMap; // Original Column Name to Renamed Column Name
	final private Map<String, String> typesMap; // Original Column Name to String Class
	final private String unpack;
	final private String collectionName;
	final private String eval;
	final private String where;
	final private boolean selectAll;
	final private int limit;
	final private String d_find;
	final private String d_project;
	final private int d_skip;
	final private String d_sort;

	public MongoCmd(List<String> select, Map<String, String> asMap, Map<String, String> typesMap, String unpack, String collectionName, String eval, String where,
			boolean selectAll, String d_find, String d_project, String d_sort, int d_skip, int limit) {
		this.d_find = d_find;
		this.d_project = d_project;
		this.d_skip = d_skip;
		this.d_sort = d_sort;
		this.select = select;
		this.asMap = asMap;
		this.typesMap = typesMap;
		this.unpack = unpack;
		this.collectionName = collectionName;
		this.eval = eval;
		this.where = where;
		this.selectAll = selectAll;
		this.limit = limit;
	}

	public List<String> getSelect() {
		return select;
	}

	public Map<String, String> getAsMap() {
		return asMap;
	}

	public Map<String, String> getTypesMap() {
		return typesMap;
	}

	public String getUnpack() {
		return unpack;
	}

	public String getEval() {
		return eval;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getWhere() {
		return where;
	}

	public boolean getIsSelectAll() {
		return selectAll;
	}
	public int getLimit() {
		return limit;
	}

	public String getD_find() {
		return d_find;
	}

	public String getD_project() {
		return d_project;
	}

	public int getD_skip() {
		return d_skip;
	}

	public String getD_sort() {
		return d_sort;
	}

	public static Table runCommand(MongoCmd sql, MongoDatabase mongoDb, MongoQueryParser parser, ContainerTools tools) {
		int limit = sql.getLimit();
		// Create cursor for query
		MongoCursor<Document> cursor = null;
		if (sql.getEval() != null) {
			cursor = MongoEvalCmd.evalCommandCursor(sql, mongoDb, parser, tools, limit);
		} else {
			cursor = MongoEvalCmd.fromCollectionCursor(sql, mongoDb, tools, limit);
		}

		List<String> addedColumns = sql.getSelect();
		Map<String, String> asMap = sql.getAsMap();
		Map<String, String> typesMap = sql.getTypesMap();
		LinkedHashMap<String, MongoGetter> getters = null;
		com.f1.utils.structs.table.stack.BasicCalcTypes classMapping = null;
		ColumnarTable table = new ColumnarTable();

		if (cursor != null) {
			List<Document> rows = MongoEvalCmd.getRowsAndFindColumns(sql, cursor, addedColumns, typesMap, asMap);
			cursor.close();

			getters = new LinkedHashMap<String, MongoGetter>(addedColumns.size());
			MongoEvalCmd.populateGetters(asMap, getters);

			classMapping = new com.f1.utils.structs.table.stack.BasicCalcTypes();
			MongoEvalCmd.populateClassTypesMapping(addedColumns, typesMap, asMap, rows, getters, classMapping);

			ColumnarColumn<?>[] rColumns = MongoEvalCmd.createColumns(table, classMapping);
			MongoEvalCmd.addRowsAndCastData(table, rColumns, rows, getters);
		}

		return table;
	}

}
