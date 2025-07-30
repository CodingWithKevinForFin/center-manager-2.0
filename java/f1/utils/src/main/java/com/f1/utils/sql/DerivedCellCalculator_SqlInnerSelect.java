package com.f1.utils.sql;

import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculator_SqlInnerSelect implements DerivedCellCalculator {

	private DerivedCellCalculator[] left;
	private int position;
	private Set<Object[]> rowsSet = null;
	private Object[] tmp;
	private Class<?>[] types;
	private Caster<?>[] casters;
	private DerivedCellCalculatorSql inner;

	public DerivedCellCalculator_SqlInnerSelect(int position, DerivedCellCalculator[] left, DerivedCellCalculatorSql inner) {
		this.position = position;
		this.inner = inner;
		this.left = left;
		tmp = new Object[left.length];
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		if (rowsSet == null) {
			Object obj = inner.get(lcvs);
			FlowControlPause fcp = get(lcvs, obj);
			if (fcp != null)
				return fcp;
		}
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = this.casters[i].cast(this.left[i].get(lcvs));
		}
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
		if (!(obj instanceof TableReturn))
			throw new ExpressionParserException(inner.getPosition(), "Must evaluate to a table");
		TableReturn tr = (TableReturn) obj;
		Table values = tr.getTables().get(0);
		if (values.getColumnsCount() != left.length)
			throw new ExpressionParserException(inner.getPosition(),
					"Column count mismatch: Expecting " + this.tmp.length + " column(s) but inner query returned " + values.getColumnsCount() + " column(s)");
		rowsSet = new HasherSet<Object[]>(ArrayHasher.INSTANCE);
		this.types = new Class[tmp.length];
		for (int i = 0; i < tmp.length; i++)
			this.types[i] = SqlProcessorUtils.getWidest(values.getColumnAt(i).getType(), this.left[i].getReturnType());
		this.casters = OH.getAllCasters(this.types);
		for (Row row : values.getRows()) {
			Object o[] = row.getValuesCloned();
			for (int i = 0; i < types.length; i++)
				o[i] = this.casters[i].cast(o[i], false, false);
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
		for (DerivedCellCalculator i : left) {
			i.toString(sink);
			if (first)
				first = false;
			else
				sink.append(',');
		}
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
		DerivedCellCalculator[] leftCopies = left.clone();
		for (int i = 0; i < leftCopies.length; i++)
			leftCopies[i] = leftCopies[i].copy();
		return new DerivedCellCalculator_SqlInnerSelect(this.position, left, (DerivedCellCalculatorSql) this.inner.copy());
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	public DerivedCellCalculator[] getLeft() {
		return this.left;
	}

	public Set<Object[]> getInValues() {
		return this.rowsSet;
	}

	@Override
	public int getInnerCalcsCount() {
		return this.left.length + 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return n == 0 ? this.inner : this.left[n - 1];
	}
	@Override
	public Object resume(PauseStack paused) {
		Object l = paused.getNext().resume();
		CalcFrameStack lcvs = paused.getLcvs();
		FlowControlPause fcp = get(lcvs, l);
		if (fcp != null)
			return fcp;
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = this.casters[i].cast(this.left[i].get(lcvs));
		}
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
		DerivedCellCalculator_SqlInnerSelect o = (DerivedCellCalculator_SqlInnerSelect) other;
		return DerivedHelper.areSame(this.inner, o.inner) && DerivedHelper.areSame(left, o.left);
	}

}
