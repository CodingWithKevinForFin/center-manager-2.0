package com.f1.ami.center.hdb.qry;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.string.node.OperationNode;

public class AmiHdbQuery_Regex extends AmiHdbQuery_Compare {

	private TextMatcher regex;

	public AmiHdbQuery_Regex(AmiHdbColumn column, Comparable value) {
		super(column, value);
		regex = SH.m(OH.toString(value));//borrowed from DerivedCellCalculator::toPatern(...)
	}

	@Override
	public int getScore() {
		return SCORE_NEPA;
	}

	@Override
	public boolean matchesInner(Comparable val) {
		return val != null && regex.matches(SH.toCharSequence(val));
	}

	@Override
	protected byte getOperatorNodeCode() {
		return OperationNode.OP_TILDE_TILDE;
	}
	@Override
	public byte getType() {
		return TT;
	}
}
