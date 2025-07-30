package com.f1.ami.center.hdb.qry;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;

public class AmiHdbQuery_All implements AmiHdbQueryPart {

	final private AmiHdbColumn column;

	public AmiHdbQuery_All(AmiHdbColumn column) {
		this.column = column;
	}

	@Override
	public AmiHdbColumn getColumn() {
		return column;
	}

	@Override
	public int getScore() {
		return SCORE_ALL;
	}

	@Override
	public boolean matches(Comparable val) {
		return true;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(true);
	}

	@Override
	public DerivedCellCalculator toDcc() {
		return new DerivedCellCalculatorConst(0, Boolean.TRUE);
	}

}
