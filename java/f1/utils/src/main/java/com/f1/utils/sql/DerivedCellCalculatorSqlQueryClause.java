package com.f1.utils.sql;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.base.Table;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSqlQueryClause extends DerivedCellCalculatorSql {

	final private SqlNode node;
	final private SqlDerivedCellParser dcp;
	private DerivedCellCalculatorSql[] tables;
	private String[] asNames;
	private QueryClause selectClause;
	final private boolean isInnerUnion;

	public DerivedCellCalculatorSqlQueryClause(SqlNode node, SqlDerivedCellParser dcp, DerivedCellCalculatorSql[] tables, String[] asNames, QueryClause sc, boolean isInnerUnion) {
		super(node, dcp);
		this.node = node;
		this.dcp = dcp;
		this.tables = tables;
		this.asNames = asNames;
		this.selectClause = sc;
		this.isInnerUnion = isInnerUnion;
	}

	@Override
	public FlowControl get(CalcFrameStack lcvs) {
		Map<String, Table> tables = new LinkedHashMap<String, Table>(this.tables.length);
		if (!isInnerUnion) {
			SqlPlanListener listener = lcvs.getSqlPlanListener();
			if (listener != null)
				listener.onStart(node.toString());
			if (listener != null)
				listener.onStep("PROJECTION", node.toString());
		}
		return get(lcvs, tables, 0);
	}

	private FlowControl get(CalcFrameStack lcvs, Map<String, Table> tables, int i) {
		while (i < this.tables.length) {
			DerivedCellCalculatorSql calc = this.tables[i];
			FlowControl o = calc.get(lcvs);
			FlowControlPause fcp = processTable(lcvs, o, tables, i);
			if (fcp != null)
				return fcp;
			i++;
		}
		SqlPlanListener listener = lcvs.getSqlPlanListener();

		try {
			FlowControl r = processQueryClause(this, this.selectClause, lcvs, tables);
			if (r instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) r, this, lcvs, i, tables);
			//			if (!getIsInnerQuery())
			//				r = (Table) this.dcp.getSqlProcessor().getMutator().processReturningTable(lcvs, (Table) r);
			if (listener != null)
				listener.onEnd(((TableReturn) r).getTables().get(0));
			return r;
		} catch (ExpressionParserException e) {
			e.setIsRuntime();
			if (listener != null)
				listener.onEndWithError(e);
			throw new FlowControlThrow(this, "Runtime Error: " + e.getMessage(), e);
		} catch (RuntimeException e) {
			if (listener != null)
				listener.onEndWithError(e);
			throw new FlowControlThrow(this, null, e);
		}

	}

	private FlowControlPause processTable(CalcFrameStack lcvs, FlowControl o, Map<String, Table> tables, int i) {
		Table table;
		if (o instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) o, this, lcvs, i, tables);
		table = DerivedHelper.toTableOrThrow(this.tables[i].getPosition(), o);
		String asName = this.asNames[i];
		if (asName != null) {
			if (tables.containsKey(asName))
				throw new ExpressionParserException(this.tables[i].getPosition(), "Duplicate table name: " + table.getTitle());
			tables.put(asName, table);
		} else {
			String name = SH.getNextId(table.getTitle(), tables.keySet(), 2);
			tables.put(name, table);
		}
		return null;
	}

	@Override
	public FlowControl resume(PauseStack paused) {
		final int i = paused.getState();
		if (paused.getNext() == null) {
			FlowControlPauseTableReturn fcp = (FlowControlPauseTableReturn) paused.getFlowControlPause();
			fcp.throwIfError(this);
			return fcp.getTableReturn();
		}
		final FlowControl o = (FlowControl) paused.getNext().resume();
		final Map<String, Table> tables = (Map<String, Table>) paused.getAttachment();
		final CalcFrameStack lcvs = paused.getLcvs();
		FlowControlPause fcp = processTable(lcvs, o, tables, i);
		if (fcp != null)
			return fcp;
		return get(lcvs, tables, i + 1);
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return node.toString(sink);
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	};

	@Override
	public int getPosition() {
		return selectClause.getPosition();
	}

	public SqlNode getNode() {
		return node;
	}

	public SqlDerivedCellParser getProcessor() {
		return dcp;
	}

	@Override
	public DerivedCellCalculator copy() {
		return null;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return tables[n];
	}

	@Override
	public int getInnerCalcsCount() {
		return tables.length;
	}

	//	public void getDependencyIds(Tableset tableset, Set<Object> sink, Map<String, Object> globalVars) {
	//		DerivedCellParserContextWrapper context2 = new ChildCalcTypesStack(tableset, this.context);
	//		context2.setGlobalVars(globalVars);
	//		this.dcp.getSqlProcessor().getDependencyIds(context2, this.node, sink);
	//	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorSqlQueryClause o = (DerivedCellCalculatorSqlQueryClause) other;
		if (!DerivedHelper.areSame(this.node, o.node))
			return false;
		return true;
	}
	final public FlowControl processQueryClause(DerivedCellCalculatorSqlQueryClause query, QueryClause selectClause, CalcFrameStack sf, Map<String, Table> tables) {
		FlowControl r;
		SqlProcessor sp = dcp.getSqlProcessor();
		switch (selectClause.getOperation()) {
			case SqlExpressionParser.ID_SELECT:
				r = sp.getSelectProcessor().processSelect(query, (SelectClause) selectClause, sf, tables);
				break;
			case SqlExpressionParser.ID_PREPARE:
				r = sp.getPrepareProcessor().processPrepare((PrepareClause) selectClause, sf, tables);
				break;
			case SqlExpressionParser.ID_ANALYZE:
				r = sp.getAnalyzeProcessor().processAnalyze((AnalyzeClause) selectClause, sf, tables);
				break;
			default:
				throw new ExpressionParserException(selectClause.getPosition(),
						"Expecting SELECT, ANALYZE, PREPARE, not: " + SqlExpressionParser.toOperationString(selectClause.getOperation()));
		}
		return r;
	}
}
