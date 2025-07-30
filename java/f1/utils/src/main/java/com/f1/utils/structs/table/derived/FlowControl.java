package com.f1.utils.structs.table.derived;

public interface FlowControl {
	//	public static final byte STATEMENT_RETURN = 1;
	public static final byte STATEMENT_BREAK = 2;
	public static final byte STATEMENT_CONTINUE = 3;
	public static final byte STATEMENT_PAUSE = 4;
	public static final byte STATEMENT_SQL = 5;
	public static final byte STATEMENT_RETURN = 6;
	//	public static final byte STATEMENT_THROW = 5;

	public byte getType();
	public DerivedCellCalculator getPosition();
}