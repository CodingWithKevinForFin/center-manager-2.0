package com.f1.utils.structs.table.derived;

public class DerivedCellCalculatorNoConst extends DerivedCellCalculatorWrapper {

	public DerivedCellCalculatorNoConst(DerivedCellCalculator inner) {
		super(inner);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	public static DerivedCellCalculator valueOf(DerivedCellCalculator p1) {
		return p1.isConst() ? new DerivedCellCalculatorNoConst(p1) : p1;
	}

}
