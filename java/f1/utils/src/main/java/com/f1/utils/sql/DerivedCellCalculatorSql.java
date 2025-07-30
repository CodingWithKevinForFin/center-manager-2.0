package com.f1.utils.sql;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSql implements DerivedCellCalculator {

	final private Node node;
	final private SqlDerivedCellParser dcp;
	//	private boolean isInnerQuery;

	public DerivedCellCalculatorSql(Node node, SqlDerivedCellParser dcp) {
		this.node = node;
		this.dcp = dcp;
		//		this.isInnerQuery = false;
	}

	@Override
	public FlowControl get(CalcFrameStack lcvs) {
		SqlPlanListener listener = lcvs.getSqlPlanListener();

		SqlProcessor sp = dcp.getSqlProcessor();
		if (listener != null)
			listener.onStart(node.toString());
		try {
			FlowControl r = sp.process(this, node, dcp, lcvs);
			if (listener != null)
				listener.onEnd(r);
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

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return node.toString(sink);
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	};

	@Override
	final public Class<?> getReturnType() {
		return TableReturn.class;
	}

	@Override
	public int getPosition() {
		return node.getPosition();
	}

	public Node getNode() {
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
		throw new IndexOutOfBoundsException("" + n);
	}

	@Override
	public int getInnerCalcsCount() {
		return 0;
	}

	//	public void getDependencyIds(Tableset tableset, Set<Object> sink, Map<String, Object> globalVars) {
	//		DerivedCellParserContextWrapper context2 = new ChildCalcTypesStack(tableset, this.context);
	//		context2.setGlobalVars(globalVars);
	//		this.dcp.getSqlProcessor().getDependencyIds(context2, this.node, sink);
	//	}

	//	public void setIsInnerQuery(boolean b) {
	//		this.isInnerQuery = b;
	//	}
	//
	@Override
	public FlowControl resume(PauseStack paused) {
		throw new IllegalStateException();
	}
	//	@Override
	//	public boolean isPausable() {
	//		if (node instanceof ExecuteNode || node.getNext() instanceof ExecuteNode)
	//			return true;
	//		return false;
	//	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorSql o = (DerivedCellCalculatorSql) other;
		if (!DerivedHelper.areSame(this.node, o.node))
			return false;
		return true;
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	//	public boolean getIsInnerQuery() {
	//		return this.isInnerQuery;
	//	}
	//
}
