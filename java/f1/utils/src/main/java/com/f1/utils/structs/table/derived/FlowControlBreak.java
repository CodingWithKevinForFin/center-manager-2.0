package com.f1.utils.structs.table.derived;

public class FlowControlBreak implements FlowControl {

	final private DerivedCellCalculator position;
	final private Object name;

	public FlowControlBreak(DerivedCellCalculator position, String name) {
		this.position = position;
		this.name = name;
	}

	public Object getBlockName() {
		return this.name;
	}

	@Override
	public byte getType() {
		return STATEMENT_BREAK;
	}

	@Override
	public DerivedCellCalculator getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return name == null ? "break" : ("break " + name);
	}

}
