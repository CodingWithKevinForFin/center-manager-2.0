package com.f1.ami.center.triggers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiPreparedRowImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.triggers.AmiTrigger_Join.CellParser;
import com.f1.base.Row;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorCast;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.NamespaceCalcTypesImpl;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiTrigger_Decorate extends AmiAbstractTrigger {

	private static final Logger log = LH.get();
	private HasherMap<Object[], Set<AmiRowImpl>> tgtIndex = new HasherMap<Object[], Set<AmiRowImpl>>(ArrayHasher.INSTANCE);
	private HasherMap<Object[], AmiRowImpl> srcIndex = new HasherMap<Object[], AmiRowImpl>(ArrayHasher.INSTANCE);
	private AmiTableImpl srcTable;
	private AmiTableImpl tgtTable;
	private DerivedCellCalculator[] tgtIndexKeys;
	private DerivedCellCalculator[] srcIndexKeys;
	private Object[] tmpKey;
	private int[] tmpChanges;
	private Object[] updKey;
	private Object[] tmpVal;
	private String[] tgtColumns;
	private int[] tgtColumnPos;
	private DerivedCellCalculator[] tgtCalcs;
	private Boolean keysChange;
	private boolean targetTableNeedsRebuild;
	private ReusableStackFramePool stackFramePool;

	@Override
	protected void onStartup(CalcFrameStack sf) {
		this.stackFramePool = getImdb().getState().getStackFramePool();
		build(sf);
	}

	@Override
	public void onInitialized(CalcFrameStack sf) {
		rebuildTargetTable(sf);
	}
	private void build(CalcFrameStack sf) {
		final AmiImdbImpl db = (AmiImdbImpl) this.getImdb();
		final AmiTriggerBinding binding = this.getBinding();
		final AmiImdbScriptManager sm = db.getScriptManager();
		final SqlProcessor sqlProcessor = sm.getSqlProcessor();
		final SqlExpressionParser ep = sqlProcessor.getExpressionParser();
		this.srcTable = db.getAmiTable(binding.getTableNameAt(0));
		this.tgtTable = db.getAmiTable(binding.getTableNameAt(1));
		db.assertNotLockedByTrigger(this, tgtTable.getName());
		NamespaceCalcTypesImpl variables = new NamespaceCalcTypesImpl();
		com.f1.utils.structs.table.stack.BasicCalcTypes tgtVars = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		com.f1.utils.structs.table.stack.BasicCalcTypes srcVars = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		variables.addNamespace(tgtTable.getName(), tgtTable.getTable().getColumnTypesMapping());
		variables.addNamespace(srcTable.getName(), srcTable.getTable().getColumnTypesMapping());
		variables.putAll(tgtTable.getTable().getColumnTypesMapping());
		tgtVars.putAll(tgtTable.getTable().getColumnTypesMapping());
		variables.putAll(srcTable.getTable().getColumnTypesMapping());
		srcVars.putAll(srcTable.getTable().getColumnTypesMapping());
		final CellParser cp = new AmiTrigger_Join.CellParser(sqlProcessor, tgtTable, srcTable, tgtVars, srcVars);
		final DerivedCellCalculator tgtIndexKeys[];
		final DerivedCellCalculator srcIndexKeys[];
		{//ON
			String on = binding.getOption(Caster_String.INSTANCE, "on", null);

			ChildCalcTypesStack context = new ChildCalcTypesStack(sf, variables);
			DerivedCellCalculator onCalc = cp.toCalc(on, context);
			List<Tuple2<DerivedCellCalculator, DerivedCellCalculator>> sink = new ArrayList<Tuple2<DerivedCellCalculator, DerivedCellCalculator>>();
			DerivedCellCalculator extra = AmiTrigger_Join.toAndsForIndex(onCalc, sink, tgtVars, srcVars, tgtTable.getName(), srcTable.getName());
			if (extra != null || sink.isEmpty())
				throw new RuntimeException("ON option must be of the form: leftColumn==rightColumn [&& leftColumn==rightColumn ...]");
			int pos = 0;
			tgtIndexKeys = new DerivedCellCalculator[sink.size()];
			srcIndexKeys = new DerivedCellCalculator[sink.size()];
			for (Tuple2<DerivedCellCalculator, DerivedCellCalculator> i : sink) {
				DerivedCellCalculatorRef l = (DerivedCellCalculatorRef) i.getA();
				DerivedCellCalculatorRef r = (DerivedCellCalculatorRef) i.getB();
				tgtIndexKeys[pos] = l;
				srcIndexKeys[pos] = r;
				pos++;
			}
		}
		this.keysChange = binding.getOption(Caster_Boolean.INSTANCE, "keysChange", false);
		this.tgtIndexKeys = tgtIndexKeys;
		this.srcIndexKeys = srcIndexKeys;
		this.tmpKey = new Object[tgtIndexKeys.length];
		this.updKey = new Object[tgtIndexKeys.length];

		{//SELECTS
			String assignments = binding.getOption(Caster_String.INSTANCE, "selects", null);
			SqlColumnsNode node1 = ep.parseSqlColumnsNdoe(SqlExpressionParser.ID_SELECT, assignments);
			//			Node[] cols = node1.columns;
			int colsCount = node1.getColumnsCount();
			tgtColumns = new String[colsCount];
			tgtColumnPos = new int[colsCount];
			tgtCalcs = new DerivedCellCalculator[colsCount];
			ChildCalcTypesStack context = new ChildCalcTypesStack(sf, srcVars);
			for (int i = 0; i < colsCount; i++) {
				Node col = node1.getColumnAt(i);
				String targetName;
				OperationNode op;
				try {
					op = (OperationNode) col;
					VariableNode vn = (VariableNode) op.getLeft();
					targetName = vn.getVarname();
				} catch (Exception e) {
					throw new RuntimeException("selects option should be in the form: targetColumn=sourceColumnsExpression", e);
				}
				AmiColumnImpl<?> colm = tgtTable.getColumnNoThrow(targetName);
				if (colm == null)
					throw new RuntimeException("selects option has unknown assignment column: " + targetName);
				DerivedCellCalculator calc = sqlProcessor.getParser().toCalc(op.getRight(), context);
				tgtColumns[i] = colm.getName();
				tgtColumnPos[i] = colm.getLocation();
				if (colm.getType() != calc.getReturnType())
					calc = new DerivedCellCalculatorCast(0, colm.getType(), calc, sm.getMethodFactory().getCaster(colm.getType()));
				tgtCalcs[i] = calc;
			}
		}
		this.tmpVal = new Object[tgtCalcs.length];
		this.tmpChanges = new int[tgtCalcs.length];

		this.dependenciesDef = AmiTrigger_Join.getDependenciesDef(this.getImdb(), this.srcTable, this.tgtTable);
		this.targetTableNeedsRebuild = true;
	}

	private void rebuildTargetTable(CalcFrameStack sf) {
		if (!targetTableNeedsRebuild)
			return;
		this.targetTableNeedsRebuild = false;
		final long startNanos = System.nanoTime();
		this.tgtIndex.clear();
		this.srcIndex.clear();
		for (Row row : this.tgtTable.getTable().getRows())
			onInserted(this.tgtTable, (AmiRow) row, sf);
		for (Row row : this.srcTable.getTable().getRows())
			onInserted(this.srcTable, (AmiRow) row, sf);
		LH.info(log, "Rebuilt DECORATE trigger '", this.getBinding().getTriggerName(), "' in ", (System.nanoTime() - startNanos) / 1000, " micros");
	}

	private int updateStackSize = 0;
	private String dependenciesDef;

	@Override
	public boolean onInserting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (updateStackSize > 0 || table == this.srcTable)
			return true;
		getTgtKey(row, this.tmpKey, sf);
		AmiRowImpl src = this.srcIndex.get(tmpKey);
		if (src != null) {
			ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, src);
			for (int i = 0; i < tgtCalcs.length; i++)
				row.putAt(tgtColumnPos[i], tgtCalcs[i].get(rsf), sf);
			this.stackFramePool.release(rsf);
		}
		return true;

	}
	@Override
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (updateStackSize > 0)
			return;
		if (table == this.srcTable) {
			getSrcKey(row, this.tmpKey, sf);
			onSrcUpdated(row, sf);
		} else if (table == this.tgtTable) {
			getTgtKey(row, this.tmpKey, sf);
			Entry<Object[], Set<AmiRowImpl>> entry = this.tgtIndex.getOrCreateEntry(tmpKey);
			Set<AmiRowImpl> s = entry.getValue();
			if (s == null) {
				entry.setValue(s = new LinkedHashSet<AmiRowImpl>());
				tmpKey = tmpKey.clone();
			}
			s.add((AmiRowImpl) row);
		}

	}

	@Override
	public void onUpdated(AmiTable table, AmiRow row, CalcFrameStack sf) {
		try {
			if (updateStackSize == 1) {
				if (table == this.srcTable) {
					getSrcKey(row, this.tmpKey, sf);
					if (keysChange) {
						if (!AH.eq(this.updKey, this.tmpKey)) {
							AmiRowImpl oldRow = this.srcIndex.get(this.updKey);
							if (oldRow == row) {
								this.srcIndex.remove(this.updKey);
							}
							getSrcKey(row, this.tmpKey, sf);
						}
					}
					onSrcUpdated(row, sf);
				} else if (table == this.tgtTable) {
					if (keysChange) {
						getTgtKey(row, this.tmpKey, sf);
						if (!AH.eq(this.updKey, this.tmpKey)) {
							Set<AmiRowImpl> oldRows = this.tgtIndex.get(this.updKey);
							if (oldRows != null) {
								oldRows.remove(row);
								if (oldRows.isEmpty())
									this.tgtIndex.remove(this.updKey);
							}
							Entry<Object[], Set<AmiRowImpl>> entry = this.tgtIndex.getOrCreateEntry(tmpKey);
							Set<AmiRowImpl> s = entry.getValue();
							if (s == null) {
								entry.setValue(s = new LinkedHashSet<AmiRowImpl>());
								tmpKey = tmpKey.clone();
							}
							s.add((AmiRowImpl) row);
						}
						AmiRowImpl row2 = srcIndex.get(tmpKey);
						if (row2 != null)
							onSrcUpdated(row2, sf);
					}
				}
			}
		} finally {
			updateStackSize--;
		}
	}
	@Override
	public void onUpdatingRejected(AmiTable table, AmiRow row) {
		updateStackSize--;
	}

	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, AmiPreparedRow updatingTo, CalcFrameStack sf) {
		if (updateStackSize++ > 0)
			return true;
		if (keysChange) {
			if (table == this.tgtTable)
				getTgtKey(row, updKey, sf);
			else if (table == this.srcTable)
				getSrcKey(row, updKey, sf);
		}
		return super.onUpdating(table, row, sf);
	}

	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == this.tgtTable) {
			getTgtKey(row, this.tmpKey, sf);
			Set<AmiRowImpl> rows = this.tgtIndex.get(this.tmpKey);
			if (CH.isntEmpty(rows))
				rows.remove(row);
			if (rows != null && rows.isEmpty())
				this.tgtIndex.remove(this.tmpKey);
		} else if (table == this.srcTable) {
			getSrcKey(row, this.tmpKey, sf);
			AmiRowImpl row2 = this.srcIndex.get(this.tmpKey);
			if (row2 == row)
				this.srcIndex.remove(this.tmpKey);
		}

		return super.onDeleting(table, row, sf);
	}

	private void onSrcUpdated(AmiRow row, CalcFrameStack sf) {
		Entry<Object[], AmiRowImpl> entry = this.srcIndex.getOrCreateEntry(this.tmpKey);
		if (entry.getValue() == null) {
			this.tmpKey = tmpKey.clone();
		}
		entry.setValue((AmiRowImpl) row);
		Set<AmiRowImpl> rows = this.tgtIndex.get(this.tmpKey);
		if (CH.isntEmpty(rows)) {
			ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, row);
			for (int i = 0; i < tgtCalcs.length; i++)
				this.tmpVal[i] = tgtCalcs[i].get(rsf);
			for (AmiRowImpl tgtRow : rows) {
				AmiPreparedRowImpl pr = this.tgtTable.borrowPreparedRow();
				pr.reset();
				for (int i = 0; i < tgtCalcs.length; i++)
					pr.setComparable(tgtColumnPos[i], (Comparable) tmpVal[i]);
				tgtTable.updateAmiRow(tgtRow, pr, sf);
			}
			this.stackFramePool.release(rsf);
		}
	}
	private void getTgtKey(AmiRow row, Object[] sink, CalcFrameStack sf) {
		ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, row);
		for (int i = 0; i < sink.length; i++)
			sink[i] = this.tgtIndexKeys[i].get(rsf);
		this.stackFramePool.release(rsf);
	}
	private void getSrcKey(AmiRow row, Object[] sink, CalcFrameStack sf) {
		ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, row);
		for (int i = 0; i < sink.length; i++)
			sink[i] = this.srcIndexKeys[i].get(rsf);
		this.stackFramePool.release(rsf);
	}
	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
		String t = AmiTrigger_Join.getDependenciesDef(imdb, this.srcTable, this.tgtTable);
		if (OH.ne(this.dependenciesDef, t))
			build(sf);
		rebuildTargetTable(sf);
	}
	@Override
	public void onEnabled(boolean enable, CalcFrameStack sf) {
		if (enable)
			build(sf);
		rebuildTargetTable(sf);
		super.onEnabled(enable, sf);
	}
}
