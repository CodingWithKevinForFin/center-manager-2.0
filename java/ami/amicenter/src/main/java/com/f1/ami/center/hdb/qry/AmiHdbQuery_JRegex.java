package com.f1.ami.center.hdb.qry;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.PatternTextMatcher;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;

public class AmiHdbQuery_JRegex extends AmiHdbQuery_Compare {

	private TextMatcher regex;

	public AmiHdbQuery_JRegex(AmiHdbColumn column, Comparable value) {
		super(column, value);
		regex = new PatternTextMatcher(OH.toString(value), DerivedCellCalculatorMath.REGEX_OPTIONS, false);//borrowed from DerivedCellCalculator::toPatern(...)
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
	public byte getType() {
		return ET;
	}

	@Override
	protected byte getOperatorNodeCode() {
		return OperationNode.OP_EQ_TILDE;
	}
}
