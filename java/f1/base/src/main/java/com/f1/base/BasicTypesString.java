/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * See {@link BasicTypes}. These are used when marshalling data to legible strings (instead of the preferred byte array).
 * 
 */
public interface BasicTypesString {
	String PRIMITIVE_BOOLEAN = "b";
	String PRIMITIVE_BYTE = "y";
	String PRIMITIVE_SHORT = "s";
	String PRIMITIVE_CHAR = "c";
	String PRIMITIVE_INT = "i";
	String PRIMITIVE_FLOAT = "f";
	String PRIMITIVE_LONG = "l";
	String PRIMITIVE_DOUBLE = "d";
	String PRIMITIVE_OBJECT = "O";

	String BOOLEAN = "B";
	String BYTE = "B";
	String SHORT = "S";
	String CHAR = "C";
	String INT = "I";
	String FLOAT = "F";
	String LONG = "L";
	String DOUBLE = "D";
	String OBJECT = "O";

	String STRING = "STR";
	String LIST = "LIST";
	String SET = "SET";
	String MAP = "MAP";

	String DATE = "DATE";
	String DAY = "DAY";
	String TIME = "TIME";
	String TIME_ZONE = "TZ";

	String MESSAGE = "MSG";
	String TABLE = "TABLE";
	String FIXPOINT = "FP";

	String ARRAY = "A";
	String CLASS = "CL";
	String NULL = "N";
	String THROWABLE = "THR";

	String UNDEFINED = "UND";
	String VALUED_ENUM = "VENUM";
	String REGEX = "REGEX";
	String TUPLE = "TUPLE";

}
