package com.f1.utils.sql;

import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculator_SqlInnerSelectSingle implements DerivedCellCalculator {

	private DerivedCellCalculator left;
	private int position;
	private Set<Object> rowsSet = null;
	private Class<?> type;
	private Caster<?> caster;
	private DerivedCellCalculatorSql inner;

	public DerivedCellCalculator_SqlInnerSelectSingle(int position, DerivedCellCalculator left, DerivedCellCalculatorSql inner) {
		this.position = position;
		this.inner = inner;
		this.left = left;
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		if (rowsSet == null) {
			Object obj = inner.get(lcvs);
			FlowControlPause fcp = get(lcvs, obj);
			if (fcp != null)
				return fcp;
		}
		Object tmp = this.caster.cast(this.left.get(lcvs));
		return rowsSet.contains(tmp);
	}

	public FlowControlPause evaluateInner(CalcFrameStack lcvs) {
		if (rowsSet == null) {
			Object obj = inner.get(lcvs);
			FlowControlPause fcp = get(lcvs, obj);
			if (fcp != null)
				return fcp;
		}
		return null;
	}
	private FlowControlPause get(CalcFrameStack lcvs, Object obj) {
		SqlPlanListener listener = lcvs.getSqlPlanListener();
		if (obj instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) obj, this, lcvs, 0, null);
		else if (!(obj instanceof TableReturn))
			throw new ExpressionParserException(inner.getPosition(), "Must evaluate to a table");
		TableReturn tr = (TableReturn) obj;
		Table values = tr.getTables().get(0);
		if (values.getColumnsCount() != 1)
			throw new ExpressionParserException(inner.getPosition(),
					"Column count mismatch: Expecting 1 column(s) but inner query returned " + values.getColumnsCount() + " column(s)");
		rowsSet = new HasherSet<Object>();
		this.type = SqlProcessorUtils.getWidest(values.getColumnAt(0).getType(), this.left.getReturnType());
		this.caster = OH.getCaster(this.type);
		for (Row row : values.getRows()) {
			Object o = row.getAt(0);
			o = this.caster.cast(o, false, false);
			rowsSet.add(o);
		}
		if (listener != null)
			listener.onStep("TMP_INDEX_ON_IN_CLAUSE", values.getSize() + " row(s) reduced to " + rowsSet.size());
		return null;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('(');
		boolean first = true;
		left.toString(sink);
		sink.append(") in (");
		inner.toString(sink);
		sink.append(")");
		return sink;
	}
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public Class<?> getReturnType() {
		return Boolean.class;
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculator_SqlInnerSelectSingle(this.position, left.copy(), (DerivedCellCalculatorSql) this.inner.copy());
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public int getInnerCalcsCount() {
		return 2;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return n == 0 ? this.inner : this.left;
	}

	public DerivedCellCalculator getLeft() {
		return this.left;
	}

	public Set<Object> getInValues() {
		return this.rowsSet;
	}
	@Override
	public Object resume(PauseStack paused) {
		Object l = paused.getNext().resume();
		FlowControlPause fcp = get(paused.getLcvs(), l);
		if (fcp != null)
			return fcp;
		Object tmp = this.caster.cast(this.left.get(paused.getLcvs()));
		return rowsSet.contains(tmp);
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculator_SqlInnerSelectSingle o = (DerivedCellCalculator_SqlInnerSelectSingle) other;
		return DerivedHelper.areSame(this.inner, o.inner) && DerivedHelper.areSame(left, o.left);
	}

}
