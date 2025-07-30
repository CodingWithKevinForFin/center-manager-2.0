package com.f1.ami.center.triggers.agg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiPreparedRowImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.triggers.AmiAbstractTrigger;
import com.f1.ami.center.triggers.AmiTriggerBinding;
import com.f1.ami.center.triggers.AmiTrigger_Join;
import com.f1.base.MappingEntry;
import com.f1.base.Row;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorWithDependencies;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiTrigger_Aggregate extends AmiAbstractTrigger {

	public static final byte INSERT = 1;
	public static final byte UPDATE = 2;
	public static final byte DELETE = 3;
	private static final Logger log = LH.get();
	private DerivedCellCalculator[] groupBySources;
	private DerivedCellCalculator[] aggregateSources;
	private String[] aggregateTargets;
	private String[] groupByTargets;
	private int[] aggregateTargetPos;
	private int[] groupByTargetPos;
	private int groupByCount;
	private int aggregateCount;

	private HasherMap<Object, RowAndCount> targetIndex = new HasherMap<Object, RowAndCount>(ArrayHasher.INSTANCE);
	private AmiTableImpl targetTable;
	private AmiTableImpl sourceTable;
	private Object[] tmpKey;
	private Object[] tmpOnUpdatingKey;

	private AmiTriggerAgg[] aggregateCalcs;
	private int aggregateCalcsCount;

	private AmiTriggerAgg2[] aggregateCalcs2;
	private int aggregateCalcsCount2;
	private int aggregateCalcsCountBoth;

	private Object[] tmpUnderlying;
	private Object[] tmpOnUpdatingUnderlying;
	private int aggregateCalcsUnderlyingCount;
	private DerivedCellCalculator[] aggregateCalcsUnderlying;

	private boolean inTrigger;
	private String dependenciesDef;
	private boolean aggregatesHaveState;
	private Set<String> lockedTables;
	private boolean targetTableNeedsRebuild;
	private Boolean allowExternalUpdates;
	private ReusableStackFramePool stackFramePool;

	@Override
	protected void onStartup(CalcFrameStack sf) {
		this.stackFramePool = getImdb().getState().getStackFramePool();
		if (this.getBinding().getTableNamesCount() != 2)
			throw new RuntimeException("AGGREGATE trigger must be on exactly two tables (source table, target table)");
		build(sf);
	}

	@Override
	public void onInitialized(CalcFrameStack sf) {
		rebuildTargetTable(sf);
	}

	private void build(CalcFrameStack sf) {
		final AmiImdbImpl db = (AmiImdbImpl) this.getImdb();
		final AmiTriggerBinding binding = this.getBinding();
		final AmiTableImpl sourceTable = db.getAmiTable(binding.getTableNameAt(0));
		final AmiTableImpl targetTable = db.getAmiTable(binding.getTableNameAt(1));
		final com.f1.base.CalcTypes sourceVariables = sourceTable.getTable().getColumnTypesMapping();

		final AmiImdbScriptManager sm = db.getScriptManager();
		final SqlExpressionParser ep = sm.getSqlProcessor().getExpressionParser();

		final Set<String> targetGroupBysVisited = new HashSet<String>();
		final DerivedCellCalculator[] groupBys;
		final String[] groupByTargets;
		{ //groupBy
			ChildCalcTypesStack context = new ChildCalcTypesStack(sf, sourceVariables, sm.getMethodFactory());
			String groupBy = binding.getOption(Caster_String.INSTANCE, "groupBys", null);
			SqlColumnsNode groupByNodes = ep.parseSqlColumnsNdoe(SqlExpressionParser.ID_GROUPBY, groupBy);
			//			Node[] cols = groupByNodes.columns;
			groupBys = new DerivedCellCalculator[groupByNodes.getColumnsCount()];
			groupByTargets = new String[groupByNodes.getColumnsCount()];
			if (groupBys.length == 0)
				throw new RuntimeException("GROUPBYS options must have atleast one group by clause");

			for (int i = 0; i < groupByNodes.getColumnsCount(); i++) {
				Node col = groupByNodes.getColumnAt(i);
				String targetName;
				OperationNode op;
				try {
					op = (OperationNode) col;
					VariableNode vn = (VariableNode) op.getLeft();
					targetName = vn.getVarname();
				} catch (Exception e) {
					throw new RuntimeException("GROUPBYS option should be in the form: targetColumn=aggFormulaOnSourceColumn", e);
				}
				if (targetTable.getColumnNoThrow(targetName) == null)
					throw new RuntimeException("GROUPBYS option has unknown assignment column: " + targetName);
				groupBys[i] = sm.getSqlProcessor().getParser().toCalc(op.getRight(), context);
				groupByTargets[i] = targetName;
				if (!targetGroupBysVisited.add(targetName))
					throw new RuntimeException("GROUPBYS option has duplicate target column definition: " + targetName);
			}
		}

		final DerivedCellCalculator[] aggregates;
		final String[] aggregateTargets;
		final AmiTriggerAggFactory af = new AmiTriggerAggFactory(sm.getMethodFactory(), this);
		final Set<Object> tmpSet = new HashSet<Object>();
		{ //aggregates
			ChildCalcTypesStack context = new ChildCalcTypesStack(sf, sourceVariables, af);
			String aggregatesString = binding.getOption(Caster_String.INSTANCE, "selects", null);
			SqlColumnsNode node1 = ep.parseSqlColumnsNdoe(SqlExpressionParser.ID_GROUPBY, aggregatesString);
			//			Node[] cols = node1.columns;
			aggregates = new DerivedCellCalculator[node1.getColumnsCount()];
			aggregateTargets = new String[node1.getColumnsCount()];

			Set<String> targetsVisited = new HashSet<String>();
			for (int i = 0; i < node1.getColumnsCount(); i++) {
				Node col = node1.getColumnAt(i);
				String targetName;
				OperationNode op;
				try {
					op = (OperationNode) col;
					VariableNode vn = (VariableNode) op.getLeft();
					targetName = vn.getVarname();
				} catch (Exception e) {
					throw new RuntimeException("SELECTS option should be in the form: targetColumn=aggFormulaOnSourceColumn", e);
				}
				if (targetTable.getColumnNoThrow(targetName) == null)
					throw new RuntimeException("SELECTS option has unknown assignment column: " + targetName);
				aggregates[i] = sm.getSqlProcessor().getParser().toCalc(op.getRight(), context);
				tmpSet.clear();
				getDependencyIdsIgnoreAgg(aggregates[i], tmpSet);

				if (!tmpSet.isEmpty())
					throw new RuntimeException("Source variable '" + CH.first(tmpSet) + "' must be inside an aggregate for SELECTS option");
				aggregateTargets[i] = targetName;

				if (!targetGroupBysVisited.add(targetName))
					throw new RuntimeException("SELECTS option and groupBy option share duplicate target column definition: " + targetName);
				if (!targetsVisited.add(targetName))
					throw new RuntimeException("SELECTS option has duplicate target column definition: " + targetName);
			}
		}
		this.targetTable = targetTable;
		db.assertNotLockedByTrigger(this, targetTable.getName());
		this.allowExternalUpdates = binding.getOption(Caster_Boolean.INSTANCE, "allowExternalUpdates", Boolean.FALSE);
		this.lockedTables = allowExternalUpdates ? Collections.EMPTY_SET : Collections.singleton(this.targetTable.getName());
		this.sourceTable = sourceTable;

		this.aggregateCount = aggregates.length;
		this.aggregateSources = aggregates;
		this.aggregateTargets = aggregateTargets;
		this.aggregateTargetPos = getPositions(targetTable, this.aggregateTargets);

		this.groupByCount = groupBys.length;
		this.groupBySources = groupBys;
		this.groupByTargets = groupByTargets;
		this.groupByTargetPos = getPositions(targetTable, this.groupByTargets);
		this.tmpKey = new Object[this.groupByCount];
		this.tmpOnUpdatingKey = new Object[this.groupByCount];

		this.aggregateCalcs = AH.toArray(af.getAggregates(), AmiTriggerAgg.class);
		this.aggregateCalcsCount = aggregateCalcs.length;

		this.aggregateCalcs2 = AH.toArray(af.getAggregates2(), AmiTriggerAgg2.class);
		this.aggregateCalcsCount2 = aggregateCalcs2.length;
		this.aggregateCalcsCountBoth = this.aggregateCalcsCount + this.aggregateCalcsCount2;

		this.aggregateCalcsUnderlyingCount = this.aggregateCalcsCount + this.aggregateCalcsCount2 * 2;
		this.aggregateCalcsUnderlying = new DerivedCellCalculator[this.aggregateCalcsUnderlyingCount];
		this.tmpUnderlying = new Object[this.aggregateCalcsUnderlyingCount];
		this.tmpOnUpdatingUnderlying = new Object[this.aggregateCalcsUnderlyingCount];

		this.dependenciesDef = AmiTrigger_Join.getDependenciesDef(this.getImdb(), sourceTable, targetTable);
		boolean aggregatesHaveState = false;
		for (int i = 0; i < aggregateCalcsCount; i++) {
			AmiTriggerAgg amiTriggerAgg = this.aggregateCalcs[i];
			this.aggregateCalcsUnderlying[i] = amiTriggerAgg.getInner();
			aggregatesHaveState = aggregatesHaveState || amiTriggerAgg.needsHelper();
		}

		for (int i = 0, n = this.aggregateCalcsCount; i < aggregateCalcsCount2; i++, n += 2) {
			AmiTriggerAgg2 amiTriggerAgg = this.aggregateCalcs2[i];
			this.aggregateCalcsUnderlying[n] = amiTriggerAgg.getInner1();
			this.aggregateCalcsUnderlying[n + 1] = amiTriggerAgg.getInner2();
			aggregatesHaveState = aggregatesHaveState || amiTriggerAgg.needsHelper();
		}
		this.aggregatesHaveState = aggregatesHaveState;
		this.targetTableNeedsRebuild = true;
	}
	private Set<Object> getDependencyIdsIgnoreAgg(DerivedCellCalculator calc, Set<Object> sink) {
		if (calc != null) {
			if (calc instanceof AmiTriggerAggCalc)
				return sink;
			if (calc instanceof DerivedCellCalculatorWithDependencies)
				((DerivedCellCalculatorWithDependencies) calc).getDependencyIds(sink);
			for (int i = 0, l = calc.getInnerCalcsCount(); i < l; i++)
				getDependencyIdsIgnoreAgg(calc.getInnerCalcAt(i), sink);
		}
		return sink;

	}

	private static int[] getPositions(AmiTableImpl table, String[] names) {
		int[] pos = new int[names.length];
		for (int i = 0; i < names.length; i++)
			pos[i] = table.getColumn(names[i]).getLocation();
		return pos;
	}
	private void rebuildTargetTable(CalcFrameStack sf) {
		if (!targetTableNeedsRebuild)
			return;
		this.targetTableNeedsRebuild = false;
		final long startNanos = System.nanoTime();
		this.targetIndex.clear();
		if (this.groupByCount == 1)
			this.targetIndex = new HasherMap<Object, AmiTrigger_Aggregate.RowAndCount>();
		else
			this.targetIndex = new HasherMap<Object, AmiTrigger_Aggregate.RowAndCount>(ArrayHasher.INSTANCE);
		try {
			inTrigger = true;
			this.targetTable.clearRows(sf);
		} finally {
			inTrigger = false;
		}
		for (Row row : this.sourceTable.getTable().getRows()) {
			onInserted(this.sourceTable, (AmiRow) row, sf);
		}
		LH.info(log, "Rebuilt AGGREGATE trigger '", this.getBinding().getTriggerName(), "' in ", (System.nanoTime() - startNanos) / 1000, " micros");
	}

	@Override
	public boolean onInserting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable && !inTrigger)
			return false;
		return super.onInserting(table, row, sf);
	}
	@Override
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable)
			return;

		try {
			this.inTrigger = true;
			getFromSourceRow((AmiRowImpl) row, tmpKey, sf);
			if (processAdd(tmpKey, (AmiRowImpl) row, sf))
				replaceTmpKey();
		} finally {
			this.inTrigger = false;
		}
	}
	private void replaceTmpKey() {
		if (groupByCount != 1)
			tmpKey = new Object[groupByCount];
	}

	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable)
			return inTrigger;
		try {
			this.inTrigger = true;
			getFromSourceRow((AmiRowImpl) row, tmpKey, sf);
			processDelete(tmpKey, (AmiRowImpl) row, sf);
			return true;
		} finally {
			this.inTrigger = false;
		}
	}
	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable)
			return inTrigger || this.allowExternalUpdates;
		try {
			this.inTrigger = true;
			getFromSourceRow((AmiRowImpl) row, tmpOnUpdatingKey, sf);
			getUnderlyingsFromSourceRow((AmiRowImpl) row, tmpOnUpdatingUnderlying, sf);
			return true;
		} finally {
			this.inTrigger = false;
		}
	}
	@Override
	public void onUpdated(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable)
			return;
		try {
			AmiRowImpl arow = (AmiRowImpl) row;
			this.inTrigger = true;
			getFromSourceRow(arow, tmpKey, sf);
			for (int n = 0; n < groupByCount; n++)
				if (OH.ne(tmpOnUpdatingKey[n], tmpKey[n])) {//key changed, process delete and add
					processDelete(tmpOnUpdatingKey, arow, tmpOnUpdatingUnderlying, sf);
					if (processAdd(tmpKey, arow, sf))
						replaceTmpKey();
					return;
				}
			RowAndCount rac = getFromTargetIndexOrThrow(toKey(tmpKey), arow);
			getUnderlyingsFromSourceRow(arow, tmpUnderlying, sf);
			processAggregatesForUpdates(tmpOnUpdatingUnderlying, tmpUnderlying, arow, rac, sf);
		} finally {
			this.inTrigger = false;
		}
	}

	private void processDelete(Object[] key, AmiRowImpl row, Object[] tmp, CalcFrameStack sf) {
		Object k = toKey(key);
		RowAndCount rac = getFromTargetIndexOrThrow(k, row);
		if (rac != null) {
			if (--rac.count == 0) {
				this.targetIndex.remove(k);
				this.targetTable.removeAmiRow(rac.row, sf);
				if (this.aggregatesHaveState)
					rac.sourceRows.remove(row);
			} else {
				processAggregatesForDelete(tmp, row, rac, sf);
				if (this.aggregatesHaveState)
					rac.sourceRows.remove(row);
			}
		}
	}

	private void processDelete(Object[] key, AmiRowImpl row, CalcFrameStack sf) {
		Object k = toKey(key);
		RowAndCount rac = getFromTargetIndexOrThrow(k, row);
		if (rac != null) {
			if (--rac.count == 0) {
				this.targetIndex.remove(k);
				this.targetTable.removeAmiRow(rac.row, sf);
				if (this.aggregatesHaveState)
					rac.sourceRows.remove(row);
			} else {
				getUnderlyingsFromSourceRow(row, tmpUnderlying, sf);
				processAggregatesForDelete(tmpUnderlying, row, rac, sf);
				if (this.aggregatesHaveState)
					rac.sourceRows.remove(row);
			}
		}
	}

	private RowAndCount getFromTargetIndexOrThrow(Object k, AmiRowImpl row) {
		RowAndCount r = this.targetIndex.get(k);
		if (r == null) {
			r = this.targetIndex.get(k);
			if (this.aggregatesHaveState)
				for (MappingEntry<Object, RowAndCount> i : this.targetIndex.entries())
					if (i.getValue().sourceRows.contains(row))
						throw new RuntimeException("Key mismatch: " + toKeyString(k) + " vs " + toKeyString(i.getKey()) + " for row " + row);
			throw new RuntimeException("Row not found: " + row + " key=" + toKeyString(k));
		}
		return r;
	}

	private String toKeyString(Object k) {
		return k instanceof Object[] ? SH.join(',', (Object[]) k) : SH.toString(k);
	}

	private void processAggregatesForInsert(Object[] nuw, AmiRowImpl causingSourceRow, RowAndCount sink, CalcFrameStack sf) {
		Object[] uv = sink.underlyingValues;
		if (this.aggregatesHaveState) {
			int i = 0;
			for (; i < this.aggregateCalcsCount; i++)
				uv[i] = this.aggregateCalcs[i].visitInsert(nuw[i], uv[i], causingSourceRow, sink.sourceRows, sink.aggregateHelpers[i], sf);
			for (int n = i; i < this.aggregateCalcsCountBoth; i++, n += 2)
				uv[i] = this.aggregateCalcs2[i - this.aggregateCalcsCount].visitInsert(nuw[n], nuw[n + 1], uv[i], causingSourceRow, sink.sourceRows, sink.aggregateHelpers[i]);
		} else {
			int i = 0;
			for (; i < this.aggregateCalcsCount; i++)
				uv[i] = this.aggregateCalcs[i].visitInsert(nuw[i], uv[i], causingSourceRow, null, null, sf);
			for (int n = i; i < this.aggregateCalcsCountBoth; i++, n += 2)
				uv[i] = this.aggregateCalcs2[i - this.aggregateCalcsCount].visitInsert(nuw[n], nuw[n + 1], uv[i], causingSourceRow, null, null);
		}
		applyAggregatesToRow(sink, sf);
	}
	private void processAggregatesForDelete(Object[] old, AmiRowImpl causingSourceRow, RowAndCount sink, CalcFrameStack sf) {
		Object[] uv = sink.underlyingValues;
		if (this.aggregatesHaveState) {
			int i = 0;
			for (; i < this.aggregateCalcsCount; i++)
				uv[i] = this.aggregateCalcs[i].visitDelete(old[i], uv[i], causingSourceRow, sink.sourceRows, sink.aggregateHelpers[i], sf);
			for (int n = i; i < this.aggregateCalcsCountBoth; i++, n += 2)
				uv[i] = this.aggregateCalcs2[i - this.aggregateCalcsCount].visitDelete(old[n], old[n + 1], uv[i], causingSourceRow, sink.sourceRows, sink.aggregateHelpers[i]);
		} else {
			int i = 0;
			for (; i < this.aggregateCalcsCount; i++)
				uv[i] = this.aggregateCalcs[i].visitDelete(old[i], uv[i], causingSourceRow, null, null, sf);
			for (int n = i; i < this.aggregateCalcsCountBoth; i++, n += 2)
				uv[i] = this.aggregateCalcs2[i - this.aggregateCalcsCount].visitDelete(old[n], old[n + 1], uv[i], causingSourceRow, null, null);
		}
		applyAggregatesToRow(sink, sf);
	}
	private void processAggregatesForUpdates(Object[] old, Object[] nuw, AmiRowImpl causingSourceRow, RowAndCount sink, CalcFrameStack sf) {
		Object[] uv = sink.underlyingValues;
		if (this.aggregatesHaveState) {
			int i = 0;
			for (; i < this.aggregateCalcsCount; i++)
				uv[i] = this.aggregateCalcs[i].visitUpdate(old[i], nuw[i], uv[i], causingSourceRow, sink.sourceRows, sink.aggregateHelpers[i], sf);
			for (int n = i; i < this.aggregateCalcsCountBoth; i++, n += 2)
				uv[i] = this.aggregateCalcs2[i - this.aggregateCalcsCount].visitUpdate(old[n], old[n + 1], nuw[n], nuw[n + 1], uv[i], causingSourceRow, sink.sourceRows,
						sink.aggregateHelpers[i]);
		} else {
			int i = 0;
			for (; i < this.aggregateCalcsCount; i++)
				uv[i] = this.aggregateCalcs[i].visitUpdate(old[i], nuw[i], uv[i], causingSourceRow, null, null, sf);
			for (int n = i; i < this.aggregateCalcsCountBoth; i++, n += 2)
				uv[i] = this.aggregateCalcs2[i - this.aggregateCalcsCount].visitUpdate(old[n], old[n + 1], nuw[n], nuw[n + 1], uv[i], causingSourceRow, null, null);
		}
		applyAggregatesToRow(sink, sf);
	}

	private void applyAggregatesToRow(RowAndCount sink, CalcFrameStack sf) {
		AmiPreparedRowImpl pr = this.targetTable.borrowPreparedRow();
		pr.reset();
		ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, sink.row);
		for (int i = 0; i < this.aggregateCount; i++)
			pr.putAt(this.aggregateTargetPos[i], this.aggregateSources[i].get(rsf));
		this.stackFramePool.release(rsf);
		this.targetTable.updateAmiRow(sink.row.getAmiId(), pr, sf);
	}
	private void processAggregatesForNewGrouping(Object[] nuw, AmiPreparedRow pr, Object[] uv, Object ah[], AmiRowImpl causingSourceRow, LinkedHasherSet emptyInstance,
			CalcFrameStack sf) {
		if (this.aggregatesHaveState) {
			int i = 0;
			for (; i < this.aggregateCalcsCount; i++)
				uv[i] = this.aggregateCalcs[i].visitInsert(nuw[i], null, causingSourceRow, LinkedHasherSet.EMPTY_INSTANCE, ah[i], sf);
			for (int n = i; i < this.aggregateCalcsCountBoth; i++, n += 2)
				uv[i] = this.aggregateCalcs2[i - this.aggregateCalcsCount].visitInsert(nuw[n], nuw[n + 1], null, causingSourceRow, LinkedHasherSet.EMPTY_INSTANCE, ah[i]);
		} else {
			int i = 0;
			for (; i < this.aggregateCalcsCount; i++)
				uv[i] = this.aggregateCalcs[i].visitInsert(nuw[i], null, causingSourceRow, null, null, sf);
			for (int n = i; i < this.aggregateCalcsCountBoth; i++, n += 2)
				uv[i] = this.aggregateCalcs2[i - this.aggregateCalcsCount].visitInsert(nuw[n], nuw[n + 1], null, causingSourceRow, null, null);
		}
		ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, pr);
		for (int i = 0; i < this.aggregateCount; i++)
			pr.putAt(this.aggregateTargetPos[i], this.aggregateSources[i].get(rsf));
		this.stackFramePool.release(rsf);
	}

	private boolean processAdd(Object[] key, AmiRowImpl row, CalcFrameStack sf) {
		Entry<Object, RowAndCount> entry = this.targetIndex.getOrCreateEntry(toKey(key));
		RowAndCount rac = entry.getValue();
		getUnderlyingsFromSourceRow(row, tmpUnderlying, sf);
		if (rac == null) {
			AmiPreparedRow prep = targetTable.borrowPreparedRow();
			prep.reset();
			for (int n = 0; n < groupByCount; n++)
				prep.putAt(this.groupByTargetPos[n], key[n]);
			Object[] uv = new Object[aggregateCalcsUnderlyingCount];
			AmiRowImpl targetRow;
			if (this.aggregatesHaveState) {
				Object[] ah = new Object[aggregateCalcsCount + aggregateCalcsCount2];
				int i = 0;
				for (; i < this.aggregateCalcsCount; i++)
					ah[i] = this.aggregateCalcs[i].initHelper();
				for (; i < this.aggregateCalcsCountBoth; i++)
					ah[i] = this.aggregateCalcs2[i - this.aggregateCalcsCount].initHelper();
				processAggregatesForNewGrouping(tmpUnderlying, prep, uv, ah, row, LinkedHasherSet.EMPTY_INSTANCE, sf);
				targetRow = this.targetTable.insertAmiRow(prep, sf);
				entry.setValue(rac = new RowAndCount(targetRow, ah, uv));
				rac.sourceRows.add(row);
			} else {
				processAggregatesForNewGrouping(tmpUnderlying, prep, uv, null, row, LinkedHasherSet.EMPTY_INSTANCE, sf);
				targetRow = this.targetTable.insertAmiRow(prep, sf);
				entry.setValue(rac = new RowAndCount(targetRow, null, uv));
			}
			return true;
		} else {
			rac.count++;
			processAggregatesForInsert(tmpUnderlying, row, rac, sf);
			if (this.aggregatesHaveState)
				rac.sourceRows.add(row);
			return false;
		}
	}
	private Object toKey(Object[] key) {
		return this.groupByCount == 1 ? key[0] : key;
	}

	private void getFromSourceRow(AmiRowImpl arow, Object[] keySink, CalcFrameStack sf) {
		ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, arow);
		for (int n = 0; n < groupByCount; n++)
			keySink[n] = groupBySources[n].get(rsf);
		this.stackFramePool.release(rsf);
	}
	private void getUnderlyingsFromSourceRow(AmiRowImpl row, Object[] sink, CalcFrameStack sf) {
		ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, row);
		for (int i = 0; i < aggregateCalcsUnderlyingCount; i++)
			sink[i] = this.aggregateCalcsUnderlying[i].get(rsf);
		this.stackFramePool.release(rsf);
	}
	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
		String t = AmiTrigger_Join.getDependenciesDef(imdb, sourceTable, targetTable);
		if (OH.ne(this.dependenciesDef, t))
			build(sf);
		rebuildTargetTable(sf);
	}

	private class RowAndCount {
		final public Object[] aggregateHelpers;
		final AmiRowImpl row;
		int count;
		final Object[] underlyingValues;
		final LinkedHasherSet<AmiRowImpl> sourceRows;

		RowAndCount(AmiRowImpl row, Object[] aggregateHelpers, Object[] underlyingValues) {
			if (aggregateHelpers != null) {
				this.aggregateHelpers = aggregateHelpers;
				sourceRows = new LinkedHasherSet<AmiRowImpl>();
			} else {
				this.aggregateHelpers = null;
				sourceRows = null;
			}
			this.row = row;
			this.count = 1;
			this.underlyingValues = underlyingValues;
		}
	}

	@Override
	public Set<String> getLockedTables() {
		return lockedTables;
	}

	public ReusableStackFramePool getPool() {
		return this.stackFramePool;
	}
}
