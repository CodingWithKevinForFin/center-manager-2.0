package com.f1.utils.structs.table.stack;

import com.f1.utils.structs.table.derived.DerivedCellCalculatorStringTemplate;

public class StringTemplateCalcFrameStack extends ChildCalcFrameStack {

	public StringTemplateCalcFrameStack(DerivedCellCalculatorStringTemplate calc, CalcFrameStack parent, StringBuilder sink) {
		super(calc, true, parent, EmptyCalcFrame.INSTANCE);
		this.sink = sink;
	}

	final private StringBuilder sink;

	public StringBuilder getStringBuilder() {
		return sink;
	}

}
