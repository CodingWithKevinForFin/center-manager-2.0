package com.f1.ami.center.hdb.qry;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.utils.OH;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;

public class AmiHdbQuery_Between implements AmiHdbQueryPart {

	final private AmiHdbColumn column;
	final private Comparable min;
	final private boolean minInc;
	final private Comparable max;
	final private boolean maxInc;

	public AmiHdbQuery_Between(AmiHdbColumn column, Comparable min, boolean minInc, Comparable max, boolean maxInc) {
		this.column = column;
		this.min = column.getTypeCaster().castNoThrow(min);
		this.minInc = minInc;
		this.max = column.getTypeCaster().castNoThrow(max);
		this.maxInc = maxInc;
	}

	@Override
	public AmiHdbColumn getColumn() {
		return column;
	}

	public Comparable getMin() {
		return min;
	}

	public boolean getMinInc() {
		return minInc;
	}

	public Comparable getMax() {
		return max;
	}

	public boolean getMaxInc() {
		return maxInc;
	}

	@Override
	public int getScore() {
		return SCORE_BETWEEN;
	}

	@Override
	public boolean matches(Comparable val) {
		val = this.column.getTypeCaster().castNoThrow(val);
		return OH.compare(val, this.min) >= (minInc ? 0 : 1) && OH.compare(val, this.max) <= (maxInc ? 0 : -1);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		StringBuilder sb = new StringBuilder("BETWEEN(");
		sb.append(min).append(minInc ? " <= " : " < ");
		sb.append(column);
		sb.append(maxInc ? " <= " : " < ").append(max);
		return sb.append(")");
	}

	@Override
	public DerivedCellCalculator toDcc() {
		final DerivedCellCalculator minDcc = DerivedCellCalculatorMath.valueOf(0, minInc ? OperationNode.OP_GT_EQ : OperationNode.OP_GT,
				new DerivedCellCalculatorRef(0, column.getType(), column.getId()), new DerivedCellCalculatorConst(0, min));
		final DerivedCellCalculator maxDcc = DerivedCellCalculatorMath.valueOf(0, maxInc ? OperationNode.OP_LT_EQ : OperationNode.OP_LT,
				new DerivedCellCalculatorRef(0, column.getType(), column.getId()), new DerivedCellCalculatorConst(0, min));
		return DerivedCellCalculatorMath.valueOf(0, OperationNode.OP_AMP_AMP, minDcc, maxDcc);
	}

}
