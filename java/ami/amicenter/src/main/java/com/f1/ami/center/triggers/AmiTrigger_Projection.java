package com.f1.ami.center.triggers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiPreparedRowImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.base.Column;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Row;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.NamespaceCalcTypesImpl;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiTrigger_Projection extends AmiAbstractTrigger {

	private static final Logger log = LH.get();
	private Map<String, Assignments> table2Assignments;
	private AmiTableImpl targetTable;
	private boolean inTrigger;
	private AmiTableImpl[] sourceTables;
	private String dependenciesDef;
	private Set<String> lockedTables;
	private boolean targetTableNeedsRebuild;
	private Boolean allowExternalUpdates;
	private ReusableStackFramePool stackFramePool;

	@Override
	protected void onStartup(CalcFrameStack sf) {
		this.stackFramePool = getImdb().getState().getStackFramePool();
		if (this.getBinding().getTableNamesCount() < 2)
			throw new RuntimeException("UNION trigger must have at least two tables (source table(s) followed by a target table)");
		build(sf);
	}
	@Override
	public void onInitialized(CalcFrameStack sf) {
		rebuildTargetTable(sf);
	}

	public class Assignments {
		private AmiTableImpl table;
		public NamespaceCalcTypesImpl colTypes = new NamespaceCalcTypesImpl();
		public Map<String, Node> targetToExpression = new HashMap<String, Node>();
		public List<Node> filters = new ArrayList<Node>();
		public DerivedCellCalculator[] filterCalcs;
		public DerivedCellCalculator[] assignmentCalcs;
		public String[] targets;
		public int[] targetPos;
		public LongKeyMap<AmiRowImpl> source2targetRows = new LongKeyMap<AmiRowImpl>();

		public Assignments(AmiTableImpl table) {
			this.table = table;
		}

		public void buildCalcs(DerivedCellParser cp, MethodFactoryManager methodFactory, AmiTableImpl target, CalcTypesStack cfs) {
			this.targets = new String[targetToExpression.size()];
			this.targetPos = new int[targetToExpression.size()];
			this.assignmentCalcs = new DerivedCellCalculator[targetToExpression.size()];
			int pos = 0;
			ChildCalcTypesStack context = new ChildCalcTypesStack(cfs, colTypes, methodFactory);
			for (Entry<String, Node> e : targetToExpression.entrySet()) {

				targetPos[pos] = target.getColumnLocation(e.getKey());
				targets[pos] = e.getKey();
				assignmentCalcs[pos] = cp.toCalcFromNode(e.getValue(), context);
				Class<?> sourceType = assignmentCalcs[pos].getReturnType();
				byte targetType = target.getColumnType(e.getKey());
				switch (targetType) {
					case AmiTable.TYPE_BOOLEAN:
						if (sourceType != Boolean.class)
							throw new RuntimeException("Can not cast " + OH.getSimpleName(sourceType) + " to boolean for column " + e.getKey());
						break;
					case AmiTable.TYPE_LONG:
					case AmiTable.TYPE_DOUBLE:
					case AmiTable.TYPE_FLOAT:
					case AmiTable.TYPE_INT:
					case AmiTable.TYPE_UTC:
					case AmiTable.TYPE_UTCN:
						if (!Number.class.isAssignableFrom(sourceType))
							throw new RuntimeException("Can not cast " + OH.getSimpleName(sourceType) + " to number for column " + e.getKey());
						break;
					case AmiTable.TYPE_ENUM:
					case AmiTable.TYPE_STRING:
						break;
				}
				pos++;
			}
			if (this.filters.isEmpty())
				this.filterCalcs = null;
			else {
				this.filterCalcs = new DerivedCellCalculator[this.filters.size()];
				for (int i = 0; i < this.filters.size(); i++) {
					this.filterCalcs[i] = cp.toCalcFromNode(this.filters.get(i), context);
					if (this.filterCalcs[i].getReturnType() != Boolean.class)
						throw new RuntimeException("wheres must evaluate to boolean");
				}
			}

		}

		public boolean passesFilter(ReusableCalcFrameStack rsf) {
			if (filterCalcs != null) {
				for (DerivedCellCalculator i : filterCalcs)
					if (!Boolean.TRUE.equals(i.get(rsf)))
						return false;
			}
			return true;
		}
	}

	private void build(CalcFrameStack sf) {
		final AmiImdbImpl db = (AmiImdbImpl) this.getImdb();
		final AmiTriggerBinding binding = this.getBinding();
		final AmiImdbScriptManager sm = db.getScriptManager();
		final SqlProcessor sqlProcessor = sm.getSqlProcessor();
		final SqlExpressionParser ep = sqlProcessor.getExpressionParser();

		AmiTableImpl[] sourceTables = new AmiTableImpl[binding.getTableNamesCount() - 1];
		Map<String, Assignments> table2Assignments = new HashMap<String, Assignments>();
		List<String> allSourceTableNames = new ArrayList<String>(sourceTables.length);
		for (int i = 0; i < sourceTables.length; i++) {
			String name = binding.getTableNameAt(i);
			sourceTables[i] = db.getAmiTable(name);
			allSourceTableNames.add(name);
		}
		AmiTableImpl targetTable = (AmiTableImpl) this.getImdb().getAmiTable(this.getBinding().getTableNameAt(sourceTables.length));
		db.assertNotLockedByTrigger(this, targetTable.getName());
		NamespaceCalcTypesImpl sourceTypes = new NamespaceCalcTypesImpl();
		BasicMultiMap.List<Object, String> cols2sourceTables = new BasicMultiMap.List<Object, String>();
		for (AmiTableImpl table : sourceTables) {
			sourceTypes.addNamespace(table.getName(), table.getTable().getColumnTypesMapping());
			String name = table.getName();
			Assignments a = new Assignments(table);
			a.colTypes.addNamespace(table.getName(), table.getTable().getColumnTypesMapping());
			table2Assignments.put(name, a);
			for (Column i : table.getTable().getColumns()) {
				Class<?> type = i.getType();
				String colName = (String) i.getId();
				//				String fullColName = name + "." + colName;
				//				sourceTypes.putType(fullColName, type);
				Class<?> widest = OH.getWidestIgnoreNull(sourceTypes.getType(colName), type);
				sourceTypes.putType(colName, widest);
				cols2sourceTables.putMulti(new NameSpaceIdentifier(name, colName), name);
				cols2sourceTables.putMulti(colName, name);
				a.colTypes.putType(colName, type);
			}
		}
		ChildCalcTypesStack context = new ChildCalcTypesStack(sf, sourceTypes, sm.getMethodFactory());
		{//assignments
			String assignments = binding.getOption(Caster_String.INSTANCE, "selects", null);
			SqlColumnsNode node1 = ep.parseSqlColumnsNdoe(SqlExpressionParser.ID_SELECT, assignments);
			//			Node[] cols = node1.columns;
			Set<Object> sink = new HasherSet<Object>();
			for (int i = 0; i < node1.getColumnsCount(); i++) {
				Node col = node1.getColumnAt(i);
				String targetName;
				OperationNode op;
				try {
					op = (OperationNode) col;
					VariableNode vn = (VariableNode) op.getLeft();
					targetName = vn.getVarname();
				} catch (Exception e) {
					throw new RuntimeException("selects option should be in the form: targetColumn=aggFormulaOnSourceColumn", e);
				}
				if (targetTable.getColumnNoThrow(targetName) == null)
					throw new RuntimeException("selects option has unknown assignment column: " + targetName);
				DerivedCellCalculator calc = sqlProcessor.getParser().toCalc(op.getRight(), context);
				sink.clear();
				DerivedHelper.getDependencyIds(calc, sink);
				List<String> st = null;
				if (sink.isEmpty()) {
					st = allSourceTableNames;
				} else {
					for (Object s : sink) {
						List<String> t = cols2sourceTables.get(s);
						if (st == null)
							st = t;
						else if (OH.ne(st, t))
							throw new RuntimeException(
									"selects option for '" + targetName + "' has inconsistent table reference mixing: " + SH.join(',', st) + " vs " + SH.join(',', t));
					}
				}
				for (String s : st) {
					Assignments a = table2Assignments.get(s);
					if (a.targetToExpression.containsKey(targetName))
						throw new RuntimeException("selects option for '" + targetName + "' has duplicate target column defition from table: " + s);
					a.targetToExpression.put(targetName, op.getRight());
				}
			}
		}

		String wheres = binding.getOption(Caster_String.INSTANCE, "wheres", null);
		if (wheres != null) {
			SqlColumnsNode node1 = ep.parseSqlColumnsNdoe(SqlExpressionParser.ID_WHERE, wheres);
			//			Node[] cols = node1.columns;
			Set<Object> sink = new HasherSet<Object>();
			for (int i = 0; i < node1.getColumnsCount(); i++) {
				Node col = node1.getColumnAt(i);
				DerivedCellCalculator calc = sqlProcessor.getParser().toCalc(col, context);
				sink.clear();
				DerivedHelper.getDependencyIds(calc, sink);
				List<String> st = null;
				if (sink.isEmpty()) {
					throw new RuntimeException("wheres option cluases must all reference at least one underlying column");
				} else {
					for (Object s : sink) {
						List<String> t = cols2sourceTables.get(s);
						if (st == null)
							st = t;
						else if (OH.ne(st, t))
							throw new RuntimeException("wheres option for has inconsistent table reference mixing: " + SH.join(',', st) + " vs " + SH.join(',', t));
					}
				}
				for (String s : st) {
					Assignments a = table2Assignments.get(s);
					a.filters.add(col);
				}
			}
		}
		//		CellParser cp = new CellParser(sqlProcessor.getExpressionParser());
		for (String tn : allSourceTableNames)
			table2Assignments.get(tn).buildCalcs(sqlProcessor.getParser(), sm.getMethodFactory(), targetTable, sf);
		//		debug(table2Assignments, allSourceTableNames, targetTable);
		this.targetTable = targetTable;
		this.allowExternalUpdates = binding.getOption(Caster_Boolean.INSTANCE, "allowExternalUpdates", Boolean.FALSE);
		this.lockedTables = allowExternalUpdates ? Collections.EMPTY_SET : Collections.singleton(this.targetTable.getName());
		this.sourceTables = sourceTables;
		this.table2Assignments = table2Assignments;
		try {
			inTrigger = true;
			this.targetTable.clearRows(sf);
		} finally {
			inTrigger = false;
		}
		this.dependenciesDef = AmiTrigger_Join.getDependenciesDef(this.getImdb(), AH.append(sourceTables, targetTable));
		this.targetTableNeedsRebuild = true;
	}

	private void rebuildTargetTable(CalcFrameStack sf) {
		if (!targetTableNeedsRebuild)
			return;
		this.targetTableNeedsRebuild = false;
		final long startNanos = System.nanoTime();
		try {
			inTrigger = true;
			for (Assignments i : this.table2Assignments.values()) {
				for (AmiRowImpl j : i.source2targetRows.values())
					this.targetTable.removeAmiRow(j, sf);
				i.source2targetRows.clear();
			}
		} finally {
			inTrigger = false;
		}
		this.target2sourceRows.clear();
		for (AmiTableImpl t : sourceTables)
			for (Row row : t.getTable().getRows())
				onInserted(t, (AmiRow) row, sf);
		LH.info(log, "Rebuilt PROJECTION trigger '", this.getBinding().getTriggerName(), "' in ", (System.nanoTime() - startNanos) / 1000, " micros");
	}

	private LongKeyMap<AmiRow> target2sourceRows = new LongKeyMap<AmiRow>();

	@Override
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf) {
		Assignments t = this.table2Assignments.get(table.getName());
		if (t == null)
			return;
		try {
			this.inTrigger = true;
			AmiRowImpl arow = (AmiRowImpl) row;
			ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, arow);
			if (!t.passesFilter(rsf))
				return;
			AmiPreparedRowImpl pr = targetTable.borrowPreparedRow();
			pr.reset();
			for (int i = 0; i < t.targetPos.length; i++)
				pr.putAt(t.targetPos[i], t.assignmentCalcs[i].get(rsf));
			this.stackFramePool.release(rsf);
			AmiRowImpl targetRow = targetTable.insertAmiRow(pr, false, false, sf);
			if (targetRow == null)
				return;
			t.source2targetRows.put(row.getAmiId(), targetRow);
			target2sourceRows.put(targetRow.getAmiId(), row);
		} finally {
			this.inTrigger = false;
		}
	}
	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable) {
			if (inTrigger)
				return true;
			if (!allowExternalUpdates)
				return false;
			AmiRow sourceRow = target2sourceRows.remove(row.getAmiId());
			if (sourceRow != null)
				this.table2Assignments.get(sourceRow.getAmiTable().getName()).source2targetRows.remove(sourceRow.getAmiId());
			return true;
		}
		Assignments t = this.table2Assignments.get(table.getName());
		try {
			this.inTrigger = true;
			AmiRowImpl arow = (AmiRowImpl) row;
			AmiRowImpl removed = t.source2targetRows.remove(row.getAmiId());
			if (removed != null) {
				this.targetTable.removeAmiRow(removed, sf);
				target2sourceRows.remove(removed.getAmiId());
			}
			return true;
		} finally {
			this.inTrigger = false;
		}
	}
	@Override
	public boolean onInserting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table != targetTable || inTrigger) {
			return true;
		}
		if (table == targetTable && !inTrigger)
			return this.allowExternalUpdates;
		return !target2sourceRows.containsKey(row.getAmiId());
	}
	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (this.allowExternalUpdates || table != targetTable || inTrigger)
			return true;
		return !target2sourceRows.containsKey(row.getAmiId());
	}
	@Override
	public void onUpdated(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable)
			return;
		Assignments t = this.table2Assignments.get(table.getName());
		try {
			this.inTrigger = true;
			AmiRowImpl arow = (AmiRowImpl) row;
			ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, arow);
			if (!t.passesFilter(rsf)) {
				AmiRowImpl removed = t.source2targetRows.remove(row.getAmiId());
				if (removed != null) {
					this.targetTable.removeAmiRow(removed, sf);
					target2sourceRows.remove(removed.getAmiId());
				}
			} else {
				AmiRowImpl existing = t.source2targetRows.get(row.getAmiId());
				if (existing == null) {
					AmiPreparedRowImpl pr = targetTable.borrowPreparedRow();
					pr.reset();
					for (int i = 0; i < t.targetPos.length; i++)
						pr.putAt(t.targetPos[i], t.assignmentCalcs[i].get(rsf));
					AmiRowImpl targetRow = targetTable.insertAmiRow(pr, sf);
					t.source2targetRows.put(row.getAmiId(), targetRow);
					target2sourceRows.put(targetRow.getAmiId(), row);
				} else {
					AmiPreparedRowImpl pr = targetTable.borrowPreparedRow();
					pr.reset();
					for (int i = 0; i < t.targetPos.length; i++)
						pr.putAt(t.targetPos[i], t.assignmentCalcs[i].get(rsf));
					targetTable.updateAmiRow(existing.getAmiId(), pr, sf);
				}
			}
			this.stackFramePool.release(rsf);
		} finally {
			this.inTrigger = false;
		}
	}

	@Override
	public void onEnabled(boolean enable, CalcFrameStack sf) {
		super.onEnabled(enable, sf);
		if (enable) {
			onSchemaChanged(this.getImdb(), sf);
			rebuildTargetTable(sf);
		}
	}
	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
		String t = AmiTrigger_Join.getDependenciesDef(this.getImdb(), AH.append(sourceTables, targetTable));
		if (OH.ne(this.dependenciesDef, t))
			build(sf);
	}

	public Set<String> getLockedTables() {
		return lockedTables;
	}
}
