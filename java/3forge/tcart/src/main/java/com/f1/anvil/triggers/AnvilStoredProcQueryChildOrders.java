package com.f1.anvil.triggers;

import java.util.logging.Logger;

import com.f1.ami.center.procs.AmiAbstractStoredProc;
import com.f1.ami.center.procs.AmiStoredProcRequest;
import com.f1.ami.center.procs.AmiStoredProcResult;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.LH;
import com.f1.utils.structs.table.columnar.FastColumnarTable;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilStoredProcQueryChildOrders extends AmiAbstractStoredProc {

	private static final Logger log = LH.get(AnvilStoredProcQueryChildOrders.class);
	private AmiTable table;
	private FastColumnarTable requestSchema;

	@Override
	protected void onStartup(AmiImdbSession session) {
		this.table = this.getImdb().getAmiTable("childOrders");
		this.requestSchema = new FastColumnarTable(String.class, "symbol", String.class, "clOrderId", String.class, "parentId");

	}

	@Override
	public void execute(AmiStoredProcRequest arguments, AmiStoredProcResult resultSink, AmiImdbSession session, StackFrame sf) throws Exception {

	}

	//	@Override
	//	public String getPluginId() {
	//		return "spChildOrders";
	//	}
	//	@Override
	//	public void init(ContainerTools tools, PropertyController props) {
	//	}
	//
	//	@Override
	//	public void startup(AmiImdb imdb) {
	//		this.imdb = imdb;
	//		this.table = imdb.getAmiTable("childOrders");
	//		this.requestSchema = new FastColumnarTable(String.class, "symbol", String.class, "clOrderId", String.class, "parentId");
	//	}
	//
	//	@Override
	//	public String getPluginId() {
	//		return "spChildOrders";
	//	}
	//
	//	@Override
	//	public AmiStoredProcedureResponse execute(Map<String, FastColumnarTable> request, String arguments, int limit, AmiDatasourceTracker debugSink) throws Exception {
	//		List<AmiColumn> cols = new ArrayList<AmiColumn>();
	//		if (SH.isnt(arguments))
	//			for (int i = 0; i < table.getColumnsCount(); i++)
	//				cols.add(table.getColumnAt(i));
	//		else
	//			for (String column : arguments.split(","))
	//				cols.add(table.getColumn(column));
	//
	//		FastColumnarTable queryFields = request.get("FIELDS");
	//		int size = queryFields.getSize();
	//		if (size == 0) {
	//			return new AmiStoredProcedureResponse(toTable(table, Collections.EMPTY_LIST, cols));
	//		}
	//		AmiPreparedQuery query = table.createAmiPreparedQuery();
	//
	//		for (Column i : queryFields.getColumns()) {
	//			Object o = queryFields.getAt(0, i.getLocation());
	//			if (o != null) {
	//				if (size == 1) {
	//					AmiPreparedQueryCompareClause eq = query.addEq(table.getColumn((String) i.getId()));
	//					eq.setValue((Comparable) o);
	//				} else {
	//					AmiPreparedQueryInClause in = query.addIn(table.getColumn((String) i.getId()));
	//					Set<Comparable> set = new HashSet<Comparable>();
	//					for (int row = 0; row < size; row++) {
	//						o = queryFields.getAt(row, i.getLocation());
	//						if (o != null)
	//							set.add((Comparable) o);
	//					}
	//					in.setValues(set);
	//				}
	//			}
	//		}
	//
	//		LH.info(log, "Executing query: " + query);
	//		long start = System.currentTimeMillis();
	//		List<AmiRow> sink = new ArrayList<AmiRow>();
	//		table.query(query, limit, sink);
	//		AmiStoredProcedureResponse r = new AmiStoredProcedureResponse(AnvilStoredProcQueryChildOrders.toTable(table, sink, cols));
	//		long end = System.currentTimeMillis();
	//		LH.info(log, "Query returned: " + sink.size(), " row(s) in ", (end - start), "ms");
	//		return r;
	//	}
	//
	//	public static FastColumnarTable toTable(AmiTable orders, List<AmiRow> sink, List<AmiColumn> columns) {
	//
	//		int size = sink.size();
	//		FastColumnarTable r = new FastColumnarTable(Collections.EMPTY_LIST, size);
	//		for (int i = 0; i < columns.size(); i++) {
	//			AmiColumn col = columns.get(i);
	//			switch (col.getAmiType()) {
	//				case AmiTable.TYPE_ENUM:
	//				case AmiTable.TYPE_STRING: {
	//					String[] values = new String[size];
	//					for (int j = 0; j < size; j++)
	//						values[j] = col.getString(sink.get(j));
	//					r.addColumnWithObjects(col.getName(), String.class, values);
	//					break;
	//				}
	//				case AmiTable.TYPE_LONG: {
	//					long[] values = new long[size];
	//					for (int j = 0; j < size; j++) {
	//						AmiRow row = sink.get(j);
	//						values[j] = col.getIsNull(row) ? Long.MIN_VALUE : col.getLong(row);
	//					}
	//					r.addColumnWithLongs(col.getName(), values);
	//					break;
	//				}
	//				case AmiTable.TYPE_DOUBLE: {
	//					double[] values = new double[size];
	//					for (int j = 0; j < size; j++) {
	//						AmiRow row = sink.get(j);
	//						values[j] = col.getIsNull(row) ? Double.NaN : col.getDouble(row);
	//					}
	//					r.addColumnWithDoubles(col.getName(), values);
	//					break;
	//				}
	//				case AmiTable.TYPE_INT: {
	//					int[] values = new int[size];
	//					for (int j = 0; j < size; j++) {
	//						AmiRow row = sink.get(j);
	//						values[j] = col.getIsNull(row) ? Integer.MIN_VALUE : (int) col.getLong(row);
	//					}
	//					r.addColumnWithInts(col.getName(), values);
	//					break;
	//				}
	//				case AmiTable.TYPE_BOOLEAN: {
	//					boolean[] values = new boolean[size];
	//					for (int j = 0; j < size; j++) {
	//						AmiRow row = sink.get(j);
	//						values[j] = (!col.getIsNull(row)) && col.getLong(row) != 0;
	//					}
	//					break;
	//				}
	//				default:
	//					throw new RuntimeException("Unknown type: " + col.getAmiType());
	//			}
	//		}
	//		r.setTitle(orders.getName());
	//		return r;
	//	}
	//
	//	@Override
	//	public FastColumnarTable getSchemaForWizard() {
	//		return requestSchema;
	//	}

}
