package com.f1.utils.structs.table.derived;

public class FlowControlContinue implements FlowControl {

	final private DerivedCellCalculator position;
	final private Object name;

	public FlowControlContinue(DerivedCellCalculator position, String name) {
		this.position = position;
		this.name = name;
	}

	public Object getBlockName() {
		return this.name;
	}

	@Override
	public byte getType() {
		return STATEMENT_CONTINUE;
	}

	@Override
	public DerivedCellCalculator getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return name == null ? "continue" : ("continue " + name);
	}
}
