package com.f1.ami.center.hdb.qry;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.utils.OH;
import com.f1.utils.string.node.OperationNode;

public class AmiHdbQuery_Le extends AmiHdbQuery_Compare {

	public AmiHdbQuery_Le(AmiHdbColumn column, Comparable value) {
		super(column, value);
	}

	@Override
	public int getScore() {
		return SCORE_GELE;
	}

	@Override
	public boolean matchesInner(Comparable val) {
		return OH.compare(val, this.value) <= 0;
	}

	@Override
	public byte getType() {
		return LE;
	}
	@Override
	protected byte getOperatorNodeCode() {
		return OperationNode.OP_LT_EQ;
	}
}
