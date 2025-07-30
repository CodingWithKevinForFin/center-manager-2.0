package com.f1.ami.center.hdb.qry;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.utils.OH;
import com.f1.utils.string.node.OperationNode;

public class AmiHdbQuery_Lt extends AmiHdbQuery_Compare {

	public AmiHdbQuery_Lt(AmiHdbColumn column, Comparable value) {
		super(column, value);
	}

	@Override
	public int getScore() {
		return SCORE_GTLT;
	}

	@Override
	public boolean matchesInner(Comparable val) {
		return OH.compare(val, this.value) < 0;
	}

	@Override
	protected byte getOperatorNodeCode() {
		return OperationNode.OP_LT;
	}

	@Override
	public byte getType() {
		return LT;
	}
}
