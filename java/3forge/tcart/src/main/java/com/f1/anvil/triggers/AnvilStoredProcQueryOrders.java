package com.f1.anvil.triggers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiPreparedQueryInClause;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiStoredProcedure;
import com.f1.ami.center.table.AmiStoredProcedureResponse;
import com.f1.ami.center.table.AmiTable;
import com.f1.base.Column;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.structs.table.columnar.FastColumnarTable;

public class AnvilStoredProcQueryOrders implements AmiStoredProcedure {

	private static final Logger log = LH.get(AnvilStoredProcQueryOrders.class);
	private AmiImdb imdb;
	private AmiTable table;
	private FastColumnarTable requestSchema;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public void startup(AmiImdb imdb) {
		this.imdb = imdb;
		this.table = imdb.getAmiTable("ParentOrder");
		this.requestSchema = new FastColumnarTable(String.class, "symbol", String.class, "system", String.class, "orderID", String.class, "industry", String.class, "sector");
	}

	@Override
	public String getPluginId() {
		return "spOrders";
	}

	@Override
	public AmiStoredProcedureResponse execute(Map<String, FastColumnarTable> request, String arguments, int limit, AmiDatasourceTracker debugSink) throws Exception {
		List<AmiColumn> cols = new ArrayList<AmiColumn>();
		if (SH.isnt(arguments))
			for (int i = 0; i < table.getColumnsCount(); i++)
				cols.add(table.getColumnAt(i));
		else
			for (String column : arguments.split(","))
				cols.add(table.getColumn(column));

		FastColumnarTable queryFields = request.get("FIELDS");
		int size = queryFields.getSize();
		if (size == 0) {
			return new AmiStoredProcedureResponse(toTable(table, Collections.EMPTY_LIST, cols));
		}
		AmiPreparedQuery query = table.createAmiPreparedQuery();

		for (Column i : queryFields.getColumns()) {
			Object o = queryFields.getAt(0, i.getLocation());
			if (o != null) {
				if (size == 1) {
					AmiPreparedQueryCompareClause eq = query.addEq(table.getColumn((String) i.getId()));
					eq.setValue((Comparable) o);
				} else {
					AmiPreparedQueryInClause in = query.addIn(table.getColumn((String) i.getId()));
					Set<Comparable> set = new HashSet<Comparable>();
					for (int row = 0; row < size; row++) {
						o = queryFields.getAt(row, i.getLocation());
						if (o != null)
							set.add((Comparable) o);
					}
					in.setValues(set);
				}
			}
		}

		LH.info(log, "Executing query: " + query);
		long start = System.currentTimeMillis();
		List<AmiRow> sink = new ArrayList<AmiRow>();
		table.query(query, limit, sink);
		AmiStoredProcedureResponse r = new AmiStoredProcedureResponse(AnvilStoredProcQueryOrders.toTable(table, sink, cols));
		long end = System.currentTimeMillis();
		LH.info(log, "Query returned: " + sink.size(), " row(s) in ", (end - start), "ms");
		return r;
	}

	public static FastColumnarTable toTable(AmiTable orders, List<AmiRow> sink, List<AmiColumn> columns) {

		int size = sink.size();
		FastColumnarTable r = new FastColumnarTable(Collections.EMPTY_LIST, size);
		for (int i = 0; i < columns.size(); i++) {
			AmiColumn col = columns.get(i);
			switch (col.getAmiType()) {
				case AmiTable.TYPE_ENUM:
				case AmiTable.TYPE_STRING: {
					String[] values = new String[size];
					for (int j = 0; j < size; j++)
						values[j] = col.getString(sink.get(j));
					r.addColumnWithObjects(col.getName(), String.class, values);
					break;
				}
				case AmiTable.TYPE_LONG: {
					long[] values = new long[size];
					for (int j = 0; j < size; j++) {
						AmiRow row = sink.get(j);
						values[j] = col.getIsNull(row) ? Long.MIN_VALUE : col.getLong(row);
					}
					r.addColumnWithLongs(col.getName(), values);
					break;
				}
				case AmiTable.TYPE_DOUBLE: {
					double[] values = new double[size];
					for (int j = 0; j < size; j++) {
						AmiRow row = sink.get(j);
						values[j] = col.getIsNull(row) ? Double.NaN : col.getDouble(row);
					}
					r.addColumnWithDoubles(col.getName(), values);
					break;
				}
				case AmiTable.TYPE_INT: {
					int[] values = new int[size];
					for (int j = 0; j < size; j++) {
						AmiRow row = sink.get(j);
						values[j] = col.getIsNull(row) ? Integer.MIN_VALUE : (int) col.getLong(row);
					}
					r.addColumnWithInts(col.getName(), values);
					break;
				}
				default:
					throw new RuntimeException("Unknown type: " + col.getAmiType());
			}
		}
		r.setTitle(orders.getName());
		return r;
	}

	@Override
	public FastColumnarTable getSchemaForWizard() {
		return requestSchema;
	}

}
