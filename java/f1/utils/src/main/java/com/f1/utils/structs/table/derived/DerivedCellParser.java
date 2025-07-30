package com.f1.utils.structs.table.derived;

import com.f1.utils.string.Node;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public interface DerivedCellParser {
	DerivedCellCalculator toCalc(CharSequence text, CalcTypesStack context);
	DerivedCellCalculator toCalcFromNode(Node node, CalcTypesStack context);
}
