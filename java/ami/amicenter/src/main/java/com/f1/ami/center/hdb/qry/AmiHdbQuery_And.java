package com.f1.ami.center.hdb.qry;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.utils.OH;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;

public class AmiHdbQuery_And implements AmiHdbQueryPart {

	final private AmiHdbColumn column;
	private AmiHdbQueryPart left;
	private AmiHdbQueryPart right;

	public AmiHdbQuery_And(AmiHdbQueryPart left, AmiHdbQueryPart right) {
		OH.assertEq(left.getColumn(), right.getColumn());
		this.column = left.getColumn();
		this.left = left;
		this.right = right;
	}

	@Override
	public AmiHdbColumn getColumn() {
		return column;
	}

	@Override
	public int getScore() {
		return SCORE_NEPA;
	}

	@Override
	public boolean matches(Comparable val) {
		return left.matches(val) && right.matches(val);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		left.toString(sink);
		sink.append(" && ");
		right.toString(sink);
		return sink;
	}

	@Override
	public DerivedCellCalculator toDcc() {
		return DerivedCellCalculatorMath.valueOf(0, OperationNode.OP_AMP_AMP, left.toDcc(), right.toDcc());
	}

	public AmiHdbQueryPart getLeft() {
		return left;
	}

	public AmiHdbQueryPart getRight() {
		return right;
	}

}
