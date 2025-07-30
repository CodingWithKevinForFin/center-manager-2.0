package com.f1.ami.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableListenable;
import com.f1.base.TableListener;
import com.f1.base.CalcTypes;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.derived.AggregateColumn;
import com.f1.utils.structs.table.derived.AggregateGroupByColumn;
import com.f1.utils.structs.table.derived.AggregateRow;
import com.f1.utils.structs.table.derived.AggregateTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedColumn;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.DerivedTable;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebAggregator implements TableListener {

	private static final int POS_TYP = 0;
	private static final int POS_APP = 1;
	private static final int POS_EXP = 2;
	private static final int POS_OID = 3;
	private static final int POS_NOW = 4;
	private static final int POS_AID = 5;
	private static final int POS_DAT = 6;
	private static final int POS_CEN = 7;
	private static final com.f1.utils.structs.table.stack.BasicCalcTypes SPECIAL_VARIABLES = new BasicCalcTypes();
	static {
		SPECIAL_VARIABLES.putType(AmiConsts.TABLE_PARAM_T, String.class);
		SPECIAL_VARIABLES.putType(AmiConsts.TABLE_PARAM_P, String.class);
		SPECIAL_VARIABLES.putType(AmiConsts.TABLE_PARAM_E, Long.class);
		SPECIAL_VARIABLES.putType(AmiConsts.TABLE_PARAM_I, String.class);
		SPECIAL_VARIABLES.putType(AmiConsts.TABLE_PARAM_M, Long.class);
		SPECIAL_VARIABLES.putType(AmiConsts.TABLE_PARAM_CENTER, String.class);
		SPECIAL_VARIABLES.putType(AmiConsts.TABLE_PARAM_ID, Long.class);
	}
	private static final Map<String, String> SPECIAL_VARIABLE_NAMES = CH.m(AmiConsts.TABLE_PARAM_T, "Type", AmiConsts.TABLE_PARAM_P, "Application", AmiConsts.TABLE_PARAM_E,
			"Expires", AmiConsts.TABLE_PARAM_I, "Object", AmiConsts.TABLE_PARAM_M, "Modified Time", AmiConsts.TABLE_PARAM_CENTER, "AMI-Center", AmiConsts.TABLE_PARAM_ID, "AMI-ID");
	private final TableListener ROW_AMIOBJECT_BINDER = new TableListener() {

		@Override
		public void onRowRemoved(Row removed, int index) {
		}

		@Override
		public void onRowAdded(Row add) {
			AmiWebObject_AggregateWrapper w = (AmiWebObject_AggregateWrapper) add.getAt(amiWebObjectColumn.getLocation());
			w.setRow(add);
		}

		@Override
		public void onColumnRemoved(Column old) {
		}

		@Override
		public void onColumnChanged(Column old, Column nuw) {
		}

		@Override
		public void onColumnAdded(Column nuw) {
		}

		@Override
		public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		}
	};

	private LongKeyMap<Row> innerTableRows = new LongKeyMap<Row>();
	private DerivedTable innerTable;
	private AggregateTable table;
	private IntKeyMap<Row> rowToSink = new IntKeyMap<Row>();
	private IntKeyMap<Row> sinkToRow = new IntKeyMap<Row>();
	private Map<String, Column> paramReferences = new HashMap<String, Column>();
	private Column[] paramColumns = null;
	private int sinkColPosToThisColPos[] = OH.EMPTY_INT_ARRAY;
	private int thisColPosToSinkColPos[] = OH.EMPTY_INT_ARRAY;
	private Table sink;
	private boolean notifySink = true;
	private FastTablePortlet tableSink;
	private AmiWebService service;
	private String layoutAlias;
	private Column amiWebObjectColumn;

	public AmiWebAggregator(AmiWebService service, FastTablePortlet tablePortlet, TableListenable sink, String layoutAlias, CalcFrameStack sf) {
		this.sink = sink;
		sink.addTableListener(ROW_AMIOBJECT_BINDER);
		this.amiWebObjectColumn = this.sink.addColumn(AmiWebObject.class, "#params");
		this.layoutAlias = layoutAlias;
		this.tableSink = tablePortlet;
		this.service = service;
		this.innerTable = new DerivedTable(sf);
		this.innerTable.addColumn(POS_TYP, String.class, AmiConsts.TABLE_PARAM_T, null);//Table Name
		this.innerTable.addColumn(POS_APP, String.class, AmiConsts.TABLE_PARAM_P, null);//Application
		this.innerTable.addColumn(POS_EXP, Long.class, AmiConsts.TABLE_PARAM_E, null);//Expires Time
		this.innerTable.addColumn(POS_OID, String.class, AmiConsts.TABLE_PARAM_I, null);//Object ID
		this.innerTable.addColumn(POS_NOW, Long.class, AmiConsts.TABLE_PARAM_M, null);//Now
		this.innerTable.addColumn(POS_AID, Long.class, AmiConsts.TABLE_PARAM_ID, null);//AMI ID
		this.innerTable.addColumn(POS_DAT, AmiWebObject.class, AmiConsts.TABLE_PARAM_DATA, null);//params
		this.innerTable.addColumn(POS_CEN, String.class, AmiConsts.TABLE_PARAM_CENTER, null);//center name
		this.innerTable.setTitle("Underlying Table");
		this.table = new AggregateTable(innerTable);
		this.table.setTitle("Aggregate Table");
		this.table.addTableListener(this);
	}
	public Row addAmiObject(AmiWebObject cObject, AmiWebObjectFields changes) {
		Row existing = innerTableRows.get(cObject.getUniqueId());
		if (paramColumns == null) {
			paramColumns = this.paramReferences.values().toArray(new Column[this.paramReferences.size()]);
		}
		if (existing == null) {
			try {
				Object[] data = new Object[innerTable.getColumnsCount()];
				data[POS_DAT] = cObject;
				if (cObject instanceof AmiWebObject_Feed) {
					AmiWebObject_Feed ef = (AmiWebObject_Feed) cObject;
					data[POS_TYP] = ef.getTypeName();
					data[POS_APP] = ef.getAmiApplicationIdName();
					data[POS_EXP] = ef.getExpiresInMillis();
					data[POS_OID] = ef.getObjectId();
					data[POS_NOW] = ef.getModifiedOn();
					data[POS_CEN] = ef.getCenterName();
					data[POS_AID] = ef.getIdBoxed();
				} else
					data[POS_AID] = cObject.getId();
				for (Column c : paramColumns)
					data[c.getLocation()] = c.getTypeCaster().cast(cObject.getParam((String) c.getId()), false, false);
				Row row = innerTable.getRows().addRow(data);
				innerTableRows.put(cObject.getUniqueId(), row);
				return row;
			} catch (Exception e) {
				throw new RuntimeException("For object: " + cObject, e);
			}
		} else {
			try {
				if (cObject instanceof AmiWebObject_Feed) {
					AmiWebObject_Feed ef = (AmiWebObject_Feed) cObject;
					existing.putAt(POS_EXP, ef.getExpiresInMillis());
					existing.putAt(POS_NOW, ef.getModifiedOn());
				}
				if (changes != null) {
					for (int i = 0; i < changes.getChangesCount(); i++) {
						String name = changes.getChangeField(i);
						Column c = paramReferences.get(name);
						if (c != null)
							existing.putAt(c.getLocation(), c.getTypeCaster().cast(cObject.getParam((String) c.getId()), false));
					}
					return existing;
				} else {
					for (Column c : paramColumns)
						existing.putAt(c.getLocation(), c.getTypeCaster().cast(cObject.getParam((String) c.getId()), false));
					return existing;
				}
			} catch (Exception e) {
				throw new RuntimeException("For object: " + cObject, e);
			}
		}
	}

	public void removeAmiObject(AmiWebObject object) {
		Row row = innerTableRows.remove(object.getUniqueId());
		if (row != null)
			innerTable.getRows().remove(row.getLocation());
	}

	public void addParamReference(String id, Class<?> type) {
		Column column = this.innerTable.addColumn(type, id);
		Caster<?> caster = column.getTypeCaster();
		for (Row row : this.innerTable.getRows()) {
			AmiWebObject params = (AmiWebObject) row.getAt(POS_DAT);
			if (params != null)
				row.putAt(column.getLocation(), caster.cast(params.getParam((String) column.getId()), false));
		}
		this.paramReferences.put(id, column);
		this.paramColumns = null;
	}

	public void removeParamReference(String id) {
		this.innerTable.removeColumn(id);
		this.paramReferences.remove(id);
		this.paramColumns = null;
	}

	//do not modify
	public Map<String, Column> getParamReferences() {
		return paramReferences;
	}

	public void removeAggregateColumn(String id) {
		this.table.removeColumn(id);
		this.sink.removeColumn(id);

		HashSet<Object> innerDeps = new HashSet<Object>();
		for (Column i : this.table.getColumns()) {
			if (i instanceof AggregateColumn) {
				innerDeps.add(((AggregateColumn) i).getInnerColumnId());
			} else if (i instanceof AggregateGroupByColumn) {
				innerDeps.add(((AggregateGroupByColumn) i).getInnerColumnId());
			}
		}
		for (String s : new HashSet<String>(this.innerTable.getColumnIds())) {
			if (innerDeps.contains(s))
				continue;
			if (s.startsWith("!") && OH.ne("!params", s))
				this.innerTable.removeColumn(s);
		}
		redoSinkColPosToTableColPos();
	}

	public AggregateGroupByColumn addGroupBy(DerivedCellCalculator expression, CalcTypes varTypes) {
		String id = TableHelper.generateId(getAggregateTable().getColumnIds(), "@");
		return addGroupBy(id, expression, varTypes);
	}
	public AggregateGroupByColumn addGroupBy(String id, DerivedCellCalculator expression, CalcTypes varTypes) {
		if (SH.startsWith(id, '!'))
			throw new RuntimeException("invalid column id: " + id);

		for (Object var : DerivedHelper.getDependencyIds(expression)) {
			if (!this.innerTable.getColumnIds().contains(var))
				addParamReference((String) var, varTypes.getType((String) var));
		}
		String innerId = TableHelper.generateId(innerTable.getColumnIds(), "!");
		innerTable.addDerivedColumn(innerId, expression);
		AggregateGroupByColumn r = this.table.addGroupingColumn(id, innerId);
		sink.addColumn(r.getType(), r.getId());
		redoSinkColPosToTableColPos();
		int cell = r.getLocation();
		for (Row row : table.getRows()) {
			onCell(row, cell, null, row.getAt(cell));
		}
		return r;
	}
	public boolean setNotifySink(boolean b) {
		if (this.notifySink == b)
			return false;
		this.notifySink = b;
		if (!b) {
			if (this.tableSink != null)
				this.tableSink.clearRows();
			else
				this.sink.clear();
			this.rowToSink.clear();
			this.sinkToRow.clear();
		} else {
			redoSinkColPosToTableColPos();
			for (Row row : this.getAggregateTable().getRows())
				onRowAdded(row);
		}
		return true;
	}

	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		if (!notifySink)
			return;
		row.getAt(cell);
		Row sinkRow = rowToSink.get(row.getUid());
		if (sinkRow == null)
			return;
		int pos = this.thisColPosToSinkColPos[cell];
		if (pos == -1)
			return;
		sinkRow.putAt(pos, row.getAt(cell));
	}

	@Override
	public void onColumnAdded(Column nuw) {
		redoSinkColPosToTableColPos();
	}

	@Override
	public void onColumnRemoved(Column old) {
		redoSinkColPosToTableColPos();
	}
	@Override
	public void onColumnChanged(Column old, Column nuw) {
		redoSinkColPosToTableColPos();
	}

	@Override
	public void onRowAdded(Row add) {
		if (!notifySink)
			return;
		Object[] data = new Object[this.sinkColPosToThisColPos.length];
		for (int i = 0; i < data.length; i++) {
			int pos = this.sinkColPosToThisColPos[i];
			if (pos != -1)
				data[i] = add.getAt(pos);
		}
		AmiWebObject_AggregateWrapper amiWebObject = new AmiWebObject_AggregateWrapper(service.getNextAmiObjectUId(), this.downstreamPosition);
		data[this.amiWebObjectColumn.getLocation()] = amiWebObject;
		Row row = sink.getRows().addRow(data);
		amiWebObject.setRow(row);
		this.rowToSink.put(add.getUid(), row);
		this.sinkToRow.put(row.getUid(), add);
	}
	@Override
	public void onRowRemoved(Row removed, int location) {
		if (!notifySink)
			return;
		removed = this.rowToSink.remove(removed.getUid());
		this.sinkToRow.remove(removed.getUid());
		sink.removeRow(removed);
	}

	public Iterable<Row> getUnderlyingRows(Row row) {
		AggregateRow row2 = (AggregateRow) this.sinkToRow.get(row.getUid());
		return row2.getConstituents().values();
	}

	private OneToOne<String, Integer> downstreamPosition = new OneToOne<String, Integer>();
	private BasicCalcTypes downstreamTypes = new BasicCalcTypes();

	private void redoSinkColPosToTableColPos() {
		this.thisColPosToSinkColPos = new int[this.table.getColumnsCount()];
		this.sinkColPosToThisColPos = new int[this.sink.getColumnsCount()];
		AH.fill(thisColPosToSinkColPos, -1);
		AH.fill(sinkColPosToThisColPos, -1);
		for (int i = 0; i < sink.getColumnsCount(); i++) {
			Column col = this.table.getColumnsMap().get(sink.getColumnAt(i).getId());
			if (col == null)
				continue;
			int thisPos = col.getLocation();
			this.sinkColPosToThisColPos[i] = thisPos;
			this.thisColPosToSinkColPos[thisPos] = i;
		}
		downstreamPosition.clear();
		if (this.tableSink != null) {
			for (int i = 0; i < this.tableSink.getTable().getVisibleColumnsCount(); i++) {
				WebColumn col = this.tableSink.getTable().getVisibleColumn(i);
				int j = col.getTableColumnLocations()[0];
				downstreamPosition.put(col.getColumnName(), j);
				downstreamTypes.putType(col.getColumnName(), sink.getColumnAt(j).getType());
			}
			for (int i = 0; i < this.tableSink.getTable().getHiddenColumnsCount(); i++) {
				WebColumn col = this.tableSink.getTable().getHiddenColumn(i);
				int j = col.getTableColumnLocations()[0];
				downstreamPosition.put(col.getColumnName(), j);
				downstreamTypes.putType(col.getColumnName(), sink.getColumnAt(j).getType());
			}
		} else {
			for (int i = 0; i < this.sink.getColumnsCount(); i++) {
				Column col = sink.getColumnAt(i);
				downstreamPosition.put((String) col.getId(), i);
				downstreamTypes.putType((String) col.getId(), col.getType());
			}
		}
	}

	public void debug(StringBuilder sb) {
		SH.join(',', sinkColPosToThisColPos, sb.append("visible -> agg: ")).append(SH.NEWLINE);
		TableHelper.toString(getAggregateTable(), "", TableHelper.SHOW_ALL, sb);
		TableHelper.toString(getInnerTable(), "", TableHelper.SHOW_ALL, sb);
	}

	public AggregateTable getAggregateTable() {
		return this.table;
	}

	//Legacy, only used for the hardcoded count(1) column
	public DerivedColumn addAggregateColumn(String expression) {
		AmiWebScriptManagerForLayout sm = service.getScriptManager(layoutAlias);
		DerivedCellCalculator node = sm.toAggCalc(expression, innerTable.getColumnTypesMapping(), this.getAggregateTable(), null, null);
		return addAggregateColumn(null, node, innerTable.getColumnTypesMapping());
	}
	public DerivedColumn addAggregateColumn(String id, DerivedCellCalculator formula, CalcTypes varTypes) {
		Set<String> vars = new HashSet<String>();
		DerivedHelper.getDependencyIds(formula, (Set) vars);
		for (String var : vars) {
			if (this.table.getColumnIds().contains(var))
				continue;
			if (!this.innerTable.getColumnIds().contains(var))
				addParamReference((String) var, varTypes.getType(var));
		}

		String idString = id != null ? id : TableHelper.generateId(getAggregateTable().getColumnIds(), "@");
		DerivedColumn nuw = table.addDerivedColumn(idString, formula);
		sink.addColumn(nuw.getType(), nuw.getId());
		redoSinkColPosToTableColPos();
		int cell = nuw.getLocation();
		for (Row row : table.getRows()) {
			onCell(row, cell, null, row.getAt(cell));
		}
		return nuw;
	}

	public DerivedTable getInnerTable() {
		return innerTable;
	}

	public void removeUnusedVariableColumns() {
		redoSinkColPosToTableColPos();
		Set<Object> sink = new HashSet<Object>();
		for (int i : sinkColPosToThisColPos) {
			if (i == -1)
				continue;
			Column col = table.getColumnAt(i);
			if (col instanceof DerivedColumn) {
				DerivedHelper.getDependencyIds(((DerivedColumn) col).getCalculator(), sink);
			}
		}
		Set<String> toRemove = new HashSet<String>();
		for (Column col : table.getColumns()) {
			if (thisColPosToSinkColPos[col.getLocation()] == -1 && !sink.contains(col.getId()))
				toRemove.add(col.getId());
		}
		//remove lower first, then upper.
		for (String o : toRemove)
			if (!o.startsWith("!")) {
				if (!table.getColumnIds().contains(o))
					continue;
				while (table.getDependentColumnsCount(o) > 0)
					table.removeColumn(table.getDependentColumns(table.getColumn(o).getLocation())[0]);
				table.removeColumn(o);
			}
		for (String o : toRemove)
			if (o.startsWith("!"))
				table.removeColumn(o);

		sink.clear();
		toRemove.clear();
		for (Column col : table.getColumns()) {
			String colid;
			if (col instanceof AggregateColumn) {
				colid = ((AggregateColumn) col).getInnerColumnId();
			} else if (col instanceof AggregateGroupByColumn) {
				colid = ((AggregateGroupByColumn) col).getInnerColumnId();
			} else
				continue;
			sink.add(colid);
			Column col2 = innerTable.getColumn(colid);
			if (col2 instanceof DerivedColumn)
				DerivedHelper.getDependencyIds(((DerivedColumn) col2).getCalculator(), sink);
		}
		toRemove = CH.comm(this.paramReferences.keySet(), (Set) sink, true, false, false);
		for (String o : toRemove) {
			for (;;) {
				int[] deps = innerTable.getDependentColumns(innerTable.getColumn(o).getLocation());
				if (AH.isEmpty(deps))
					break;
				innerTable.removeColumn(deps[0]);
			}
			removeParamReference((String) o);
		}
		redoSinkColPosToTableColPos();
	}

	public com.f1.base.CalcTypes getSpecialVariables() {
		return SPECIAL_VARIABLES;
	}

	public String getUnderlyingColumnTitleFor(String name) {
		return CH.getOr(SPECIAL_VARIABLE_NAMES, name, name);
	}

	public void clear() {
		boolean needsDisabled = notifySink;
		try {
			if (needsDisabled)
				setNotifySink(false);
			this.table.clearUnderlyings();
			this.rowToSink.clear();
			this.sinkToRow.clear();
			this.innerTableRows.clear();
		} finally {
			if (needsDisabled)
				setNotifySink(true);
		}
	}
	public void getInnerDependencies(String columnId, Set<String> sink) {
		Column col = this.table.getColumn(columnId);
		if (col instanceof AggregateGroupByColumn) {
			walkDependencies((String) ((AggregateGroupByColumn) col).getInnerColumnId(), sink);
		} else if (col instanceof AggregateColumn) {
			walkDependencies((String) ((AggregateColumn) col).getInnerColumnId(), sink);
		} else if (col instanceof DerivedColumn) {
			DerivedCellCalculator calc = ((DerivedColumn) col).getCalculator();
			Set<Object> t = DerivedHelper.getDependencyIds(calc);
			for (Object s : t)
				getInnerDependencies((String) s, sink);
		}
	}
	private void walkDependencies(String s, Set<String> sink2) {
		if (!s.startsWith("!")) {
			sink2.add(s);
		} else {
			Column col = this.innerTable.getColumn(s);
			if (col instanceof DerivedColumn) {
				DerivedCellCalculator calc = ((DerivedColumn) col).getCalculator();
				Set<Object> t = DerivedHelper.getDependencyIds(calc);
				for (Object s2 : t)
					walkDependencies((String) s2, sink2);
			}

		}
	}
	public void startBulkAdd() {
		this.innerTable.removeTableListener(this.table);
	}
	public void finishBulkAdd() {
		this.innerTable.addTableListener(this.table);
		this.table.repopulateRowsFromInner();
	}
}
