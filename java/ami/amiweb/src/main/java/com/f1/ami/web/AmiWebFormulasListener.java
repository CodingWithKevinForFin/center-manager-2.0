package com.f1.ami.web;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public interface AmiWebFormulasListener {

	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw);
}
