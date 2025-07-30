package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.ExpressionNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculator_SqlInSingle implements DerivedCellCalculator {

	final private DerivedCellCalculator left;
	final private int position;
	final private DerivedCellCalculator values[];
	final private DerivedCellCalculator varValues[];
	private Set<Object> rowsSet = new HasherSet<Object>();
	final private Class<?> type;
	final private Caster<?> caster;

	public DerivedCellCalculator_SqlInSingle(int position, DerivedCellCalculator left, DerivedCellCalculator[] values, Class types) {
		this.position = position;
		this.left = left;

		this.values = values;
		this.type = types;
		this.caster = OH.getCaster(types);
		List<DerivedCellCalculator> varValues = null;
		for (DerivedCellCalculator d : values) {
			if (!d.isConst()) {
				if (varValues == null)
					varValues = new ArrayList<DerivedCellCalculator>();
				varValues.add(d);
			} else
				rowsSet.add(this.caster.cast(d.get(null)));
		}
		if (varValues != null)
			this.varValues = AH.toArray(varValues, DerivedCellCalculator.class);
		else
			this.varValues = null;

	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		Object tmp = this.caster.cast(this.left.get(lcvs));
		if (rowsSet.contains(tmp))
			return true;
		if (varValues != null)
			for (DerivedCellCalculator d : varValues)
				if (OH.eq(this.caster.cast(d.get(lcvs)), tmp))
					return true;
		return false;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('(');
		boolean first = true;
		left.toString(sink);
		sink.append(") in (");
		first = true;
		for (DerivedCellCalculator i : values) {
			if (first)
				first = false;
			else
				sink.append(',');
			sink.append("(");
			sink.append(i);
			sink.append(")");
		}
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
		return new DerivedCellCalculator_SqlInSingle(position, left.copy(), values, type);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	static private Node reduce(Node n) {
		while (n instanceof ExpressionNode)
			n = ((ExpressionNode) n).getValue();
		return n;
	}

	public DerivedCellCalculator getLeft() {
		return this.left;
	}

	public DerivedCellCalculator[] getInValues() {
		return this.values;
	}

	@Override
	public int getInnerCalcsCount() {
		return 1 + this.values.length;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return n == 0 ? this.left : this.values[n - 1];
	}
	@Override
	public Object resume(PauseStack paused) {
		throw new IllegalStateException();
	}
	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculator_SqlInSingle o = (DerivedCellCalculator_SqlInSingle) other;
		if (!OH.eq(type, o.type))
			return false;
		if (!DerivedHelper.areSame(left, o.left))
			return false;
		if (!DerivedHelper.areSame(values, o.values))
			return false;
		if (!DerivedHelper.areSame(varValues, o.varValues))
			return false;
		return true;
	}
}
