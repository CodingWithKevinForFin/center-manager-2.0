package com.f1.ami.web;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public interface AmiWebCompilerListener {

	void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw);
	void onCallbackChanged(AmiWebAmiScriptCallback callback, DerivedCellCalculator old, DerivedCellCalculator nuw);

	void onRecompiled();

}
