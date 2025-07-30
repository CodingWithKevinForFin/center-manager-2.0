package com.f1.anvil.triggers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiStoredProcedure;
import com.f1.ami.center.table.AmiStoredProcedureResponse;
import com.f1.ami.center.table.AmiTable;
import com.f1.base.Column;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.table.columnar.FastColumnarTable;

public class AnvilStoredProcQuery implements AmiStoredProcedure {

	private AmiImdb imdb;
	private AmiTable table;
	private FastColumnarTable requestSchema;
	private String tableName;
	private Boolean returnAllOnEmptyQuery;
	private String[] columns;
	private String name;
	private static final Logger log = LH.get(AnvilStoredProcQuery.class);

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		tableName = props.getRequired("table");
		name = props.getRequired("name");
		columns = SH.split(",", props.getRequired("query.columns"));
		returnAllOnEmptyQuery = props.getRequired("all.on.empty", Boolean.class);
	}
	@Override
	public void startup(AmiImdb imdb) {
		this.imdb = imdb;
		this.table = imdb.getAmiTable(tableName);
		this.requestSchema = new FastColumnarTable();
		for (String s : this.columns)
			this.requestSchema.addColumn(String.class, s);
	}

	@Override
	public AmiStoredProcedureResponse execute(Map<String, FastColumnarTable> request, String arguments, int limit, AmiDatasourceTracker debugSink) throws Exception {
		List<AmiColumn> cols = new ArrayList<AmiColumn>();
		String fieldsTableName = "FIELDS";
		if (SH.is(arguments)) {
			Map<String, String> args = SH.splitToMap(';', '=', '\\', arguments);
			fieldsTableName = CH.getOr(args, "FIELDS_TABLE", fieldsTableName);
			String colsText = CH.getOr(args, "COLUMNS", "");
			for (String column : colsText.split(","))
				cols.add(table.getColumn(column));
		}

		if (cols.isEmpty())
			for (int i = 0; i < table.getColumnsCount(); i++)
				cols.add(table.getColumnAt(i));

		FastColumnarTable queryFields = request.get(fieldsTableName);
		AmiPreparedQuery query;
		final int queryRowsCount = queryFields.getSize();
		final long start = System.currentTimeMillis();
		final List<AmiRow> sink;
		if (queryRowsCount == 0) {
			if (!returnAllOnEmptyQuery)
				return new AmiStoredProcedureResponse(AnvilStoredProcQueryOrders.toTable(table, Collections.EMPTY_LIST, cols));
			query = null;
			int cnt = table.getRowsCount();
			sink = new ArrayList<AmiRow>(cnt);
			for (int i = 0; i < cnt; i++)
				sink.add(table.getAmiRowAt(i));
		} else {
			query = table.createAmiPreparedQuery();
			Map<String, Column> cols2 = queryFields.getColumnsMap();
			Column minTimeCol = cols2.get("minTime");
			Column maxTimeCol = cols2.get("maxTime");
			if (minTimeCol != null && maxTimeCol != null && queryFields.getSize() >= 1) {
				Long minTime = queryFields.getAt(0, minTimeCol.getLocation(), Caster_Long.INSTANCE);
				Long maxTime = queryFields.getAt(0, maxTimeCol.getLocation(), Caster_Long.INSTANCE);
				if (minTime != null && maxTime != null)
					query.addBetween(table.getColumn("time"), true, true).setMinMax(minTime, maxTime);
			}
			//			for (Column i : queryFields.getColumns()) {
			//				Object o = queryFields.getAt(0, i.getLocation());
			//				if (o != null) {
			//					AmiColumn column = table.getColumnNoThrow((String) i.getId());
			//					if (column == null)
			//						continue;
			//					if (queryRowsCount == 1) {
			//						AmiPreparedQueryCompareClause eq = query.addEq(column);
			//						eq.setValue((Comparable) o);
			//					} else {
			//						AmiPreparedQueryInClause in = query.addIn(column);
			//						Set<Comparable> set = new HashSet<Comparable>();
			//						for (int row = 0; row < queryRowsCount; row++) {
			//							o = queryFields.getAt(row, i.getLocation());
			//							if (o != null)
			//								switch (in.getAmiColumn().getAmiType()) {
			//									case AmiTable.TYPE_LONG:
			//										set.add(((Number) o).longValue());
			//										break;
			//									default:
			//										set.add((Comparable) o);
			//										break;
			//								}
			//						}
			//						in.setValues(set);
			//					}
			//				}
			//			}

			List<Column> cols3 = queryFields.getColumns();
			AmiPreparedQueryCompareClause eq[] = new AmiPreparedQueryCompareClause[cols.size()];
			int cloc[] = new int[cols.size()];
			int colsCount = 0;
			for (Column i : cols3) {
				//if (queryFields.getAt(0, i.getLocation()) == null)
				//continue;
				AmiColumn column = table.getColumnNoThrow((String) i.getId());
				if (column != null) {
					cloc[colsCount] = i.getLocation();
					eq[colsCount++] = query.addEq(column);
				}
			}
			final List<AmiRow> tmp = new ArrayList<AmiRow>();
			sink = new ArrayList<AmiRow>();
			IdentityHashSet<AmiRow> existing = new IdentityHashSet<AmiRow>();
			outer: for (int row = 0; row < queryRowsCount; row++) {
				for (int i = 0; i < colsCount; i++) {
					Object o = queryFields.getAt(row, cloc[i]);
					//if (o == null)//skipping any null values for now?
					//continue;
					switch (eq[i].getColumn().getAmiType()) {
						case AmiTable.TYPE_LONG:
							eq[i].setValue(((Number) o).longValue());
							break;
						default:
							eq[i].setValue((Comparable) o);
							break;
					}
				}

				table.query(query, limit, tmp);
				for (int i = 0, size = tmp.size(); i < size; i++) {
					AmiRow rw = tmp.get(i);
					if (existing.add(rw))
						sink.add(rw);
					if (sink.size() >= limit)
						break outer;
				}
				tmp.clear();
			}
			//TODO: Check for the same row duplicated in the result

			//			for (Column i : queryFields.getColumns()) {
			//				Object o = queryFields.getAt(0, i.getLocation());
			//				AmiColumn column = table.getColumnNoThrow((String) i.getId());
			//				if (column == null)
			//					continue;
			//				if (o == null)
			//					continue;
			//
			//				if (queryRowsCount == 1) {
			//					AmiPreparedQueryCompareClause eq = query.addEq(column);
			//					eq.setValue((Comparable) o);
			//				} else {
			//					AmiPreparedQueryInClause in = query.addIn(column);
			//					Set<Comparable> set = new HashSet<Comparable>();
			//					in.setValues(set);
			//				}
			//			}
			//LH.info(log, "Executing query: " + query);
		}

		final AmiStoredProcedureResponse r = new AmiStoredProcedureResponse(AnvilStoredProcQueryOrders.toTable(table, sink, cols));
		final long end = System.currentTimeMillis();
		LH.info(log, "Query returned: " + sink.size(), " row(s) in ", (end - start), "ms");
		return r;
	}
	@Override
	public FastColumnarTable getSchemaForWizard() {
		return requestSchema;
	}
	@Override
	public String getPluginId() {
		return this.name;
	}

}
