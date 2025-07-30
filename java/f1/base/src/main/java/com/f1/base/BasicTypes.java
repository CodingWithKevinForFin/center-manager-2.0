/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * All the types of objects that can be [de]marshalled at the f1 framework level. These byte values are a way of compactly storing/communicating a type. Note that more highlevel
 * objects, such as mesages are indicated by the MESSAGE (and other) enum. Along with primitives and autoboxed versions, common arrays and colllections are also available. To move
 * custom types use the CUSTOM field and provide your own protocol
 */
public interface BasicTypes {
	byte PRIMITIVE_BOOLEAN = 0;
	byte PRIMITIVE_BYTE = 1;
	byte PRIMITIVE_SHORT = 2;
	byte PRIMITIVE_CHAR = 3;
	byte PRIMITIVE_INT = 4;
	byte PRIMITIVE_FLOAT = 5;
	byte PRIMITIVE_LONG = 6;
	byte PRIMITIVE_DOUBLE = 7;
	byte PRIMITIVE_OBJECT = 8;
	byte PRIMITIVE_VOID = 9;

	byte BOOLEAN = 10;
	byte BYTE = 11;
	byte SHORT = 12;
	byte CHAR = 13;
	byte INT = 14;
	byte FLOAT = 15;
	byte LONG = 16;
	byte DOUBLE = 17;
	byte OBJECT = 18;
	byte VOID = 19;

	byte STRING = 20;
	byte LIST = 21;
	byte SET = 22;
	byte MAP = 23;
	byte PASSWORD = 24;

	byte DATE = 30;
	byte DAY = 31;
	byte DAYTIME = 32;
	byte TIME_ZONE = 33;
	byte DATE_NANOS = 34;
	byte DATE_MILLIS = 35;
	byte BYTES = 36;
	byte COMPLEX = 37;
	byte UUID = 38;

	byte TABLE_COLUMNAR = 39;
	byte IDEABLE = 40;
	byte MESSAGE = 41;
	byte MAPMESSAGE = 42;
	byte TABLE = 43;
	byte FIXPOINT = 44;
	byte BIGDECIMAL = 45;
	byte BIGINTEGER = 46;
	byte VALUED_ENUM = 47;
	byte PERSISTABLE_MAP = 48;
	byte PERSISTABLE_LIST = 49;
	byte PERSISTABLE_SET = 50;

	byte CLASS = 51;
	byte NULL = 52;
	byte THROWABLE = 53;
	byte ENUM = 54;
	byte TUPLE = 55;
	byte ARRAY = 56;

	byte STRING_ARRAY = 57;
	byte BYTE_ARRAY_ARRAY = 58;
	byte STRING_BUILDER = 59;
	byte COLOR_GRADIENT = 62;

	byte LONG_KEY_MAP = 70;

	byte PRIMITIVE_BYTE_ARRAY = 101;
	byte PRIMITIVE_INT_ARRAY = 104;
	byte PRIMITIVE_LONG_ARRAY = 106;
	byte PRIMITIVE_CHAR_ARRAY = 107;
	byte PRIMITIVE_DOUBLE_ARRAY = 108;
	byte PRIMITIVE_FLOAT_ARRAY = 109;

	byte UNDEFINED = 60;
	byte CUSTOM = 61;

}
