/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import static com.f1.base.BasicTypes.ARRAY;
import static com.f1.base.BasicTypes.BIGDECIMAL;
import static com.f1.base.BasicTypes.BIGINTEGER;
import static com.f1.base.BasicTypes.BOOLEAN;
import static com.f1.base.BasicTypes.BYTE;
import static com.f1.base.BasicTypes.BYTES;
import static com.f1.base.BasicTypes.BYTE_ARRAY_ARRAY;
import static com.f1.base.BasicTypes.CHAR;
import static com.f1.base.BasicTypes.CLASS;
import static com.f1.base.BasicTypes.COLOR_GRADIENT;
import static com.f1.base.BasicTypes.COMPLEX;
import static com.f1.base.BasicTypes.CUSTOM;
import static com.f1.base.BasicTypes.DATE;
import static com.f1.base.BasicTypes.DATE_MILLIS;
import static com.f1.base.BasicTypes.DATE_NANOS;
import static com.f1.base.BasicTypes.DAY;
import static com.f1.base.BasicTypes.DAYTIME;
import static com.f1.base.BasicTypes.DOUBLE;
import static com.f1.base.BasicTypes.ENUM;
import static com.f1.base.BasicTypes.FIXPOINT;
import static com.f1.base.BasicTypes.FLOAT;
import static com.f1.base.BasicTypes.IDEABLE;
import static com.f1.base.BasicTypes.INT;
import static com.f1.base.BasicTypes.LIST;
import static com.f1.base.BasicTypes.LONG;
import static com.f1.base.BasicTypes.LONG_KEY_MAP;
import static com.f1.base.BasicTypes.MAP;
import static com.f1.base.BasicTypes.MAPMESSAGE;
import static com.f1.base.BasicTypes.MESSAGE;
import static com.f1.base.BasicTypes.NULL;
import static com.f1.base.BasicTypes.OBJECT;
import static com.f1.base.BasicTypes.PASSWORD;
import static com.f1.base.BasicTypes.PERSISTABLE_LIST;
import static com.f1.base.BasicTypes.PERSISTABLE_MAP;
import static com.f1.base.BasicTypes.PERSISTABLE_SET;
import static com.f1.base.BasicTypes.PRIMITIVE_BOOLEAN;
import static com.f1.base.BasicTypes.PRIMITIVE_BYTE;
import static com.f1.base.BasicTypes.PRIMITIVE_BYTE_ARRAY;
import static com.f1.base.BasicTypes.PRIMITIVE_CHAR;
import static com.f1.base.BasicTypes.PRIMITIVE_CHAR_ARRAY;
import static com.f1.base.BasicTypes.PRIMITIVE_DOUBLE;
import static com.f1.base.BasicTypes.PRIMITIVE_DOUBLE_ARRAY;
import static com.f1.base.BasicTypes.PRIMITIVE_FLOAT;
import static com.f1.base.BasicTypes.PRIMITIVE_FLOAT_ARRAY;
import static com.f1.base.BasicTypes.PRIMITIVE_INT;
import static com.f1.base.BasicTypes.PRIMITIVE_INT_ARRAY;
import static com.f1.base.BasicTypes.PRIMITIVE_LONG;
import static com.f1.base.BasicTypes.PRIMITIVE_LONG_ARRAY;
import static com.f1.base.BasicTypes.PRIMITIVE_OBJECT;
import static com.f1.base.BasicTypes.PRIMITIVE_SHORT;
import static com.f1.base.BasicTypes.PRIMITIVE_VOID;
import static com.f1.base.BasicTypes.SET;
import static com.f1.base.BasicTypes.SHORT;
import static com.f1.base.BasicTypes.STRING;
import static com.f1.base.BasicTypes.STRING_ARRAY;
import static com.f1.base.BasicTypes.TABLE;
import static com.f1.base.BasicTypes.TABLE_COLUMNAR;
import static com.f1.base.BasicTypes.THROWABLE;
import static com.f1.base.BasicTypes.TIME_ZONE;
import static com.f1.base.BasicTypes.TUPLE;
import static com.f1.base.BasicTypes.UNDEFINED;
import static com.f1.base.BasicTypes.UUID;
import static com.f1.base.BasicTypes.VALUED_ENUM;
import static com.f1.base.BasicTypes.VOID;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import com.f1.base.BasicTypes;
import com.f1.base.Bytes;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Day;
import com.f1.base.DayTime;
import com.f1.base.Ideable;
import com.f1.base.Message;
import com.f1.base.Password;
import com.f1.base.Table;
import com.f1.base.UUID;
import com.f1.base.ValuedEnum;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.Tuple;

public class BasicTypeHelper {

	private static final Map<Class, Byte> ORIG_TYPES = new HashMap<Class, Byte>();
	private static final CopyOnWriteHashMap<Class, Byte> TYPES = new CopyOnWriteHashMap<Class, Byte>();
	private static final byte ALL_BASIC_TYPES[] = new byte[] { PRIMITIVE_BOOLEAN, PRIMITIVE_BYTE, PRIMITIVE_SHORT, PRIMITIVE_CHAR, PRIMITIVE_INT, PRIMITIVE_FLOAT, PRIMITIVE_LONG,
			PRIMITIVE_DOUBLE, PRIMITIVE_OBJECT, PRIMITIVE_VOID, BOOLEAN, BYTE, SHORT, CHAR, INT, FLOAT, LONG, DOUBLE, OBJECT, VOID, STRING, LIST, SET, MAP, PASSWORD, DATE, DAY,
			DAYTIME, TIME_ZONE, DATE_NANOS, IDEABLE, MESSAGE, MAPMESSAGE, TABLE_COLUMNAR, TABLE, FIXPOINT, BIGDECIMAL, BIGINTEGER, VALUED_ENUM, PERSISTABLE_MAP, PERSISTABLE_LIST,
			PERSISTABLE_SET, ARRAY, LONG_KEY_MAP, CLASS, NULL, THROWABLE, ENUM, TUPLE, PRIMITIVE_BYTE_ARRAY, PRIMITIVE_INT_ARRAY, PRIMITIVE_LONG_ARRAY, PRIMITIVE_CHAR_ARRAY,
			UNDEFINED, CUSTOM, DATE_MILLIS, BYTES, COMPLEX, UUID, COLOR_GRADIENT };
	static {
		for (byte b : ALL_BASIC_TYPES) {
			Class clzz = toClass(b);
			if (clzz != null)
				ORIG_TYPES.put(clzz, b);
		}
		//some common ones.
		ORIG_TYPES.put(ArrayList.class, LIST);
		ORIG_TYPES.put(HashMap.class, MAP);
		ORIG_TYPES.put(TreeMap.class, MAP);
		ORIG_TYPES.put(HashSet.class, SET);
		ORIG_TYPES.put(TreeSet.class, SET);
		TYPES.putAll(ORIG_TYPES);
	}

	public static byte toTypeNoInheritance(Class type) {
		Byte r = ORIG_TYPES.get(type);
		return r == null ? UNDEFINED : r.byteValue();
	}

	public static byte toType(Class type) {
		Byte r = TYPES.get(type);
		if (r == null) {
			//special case to ensure ValuedEnums don't get mapped to enums
			if (ValuedEnum.class.isAssignableFrom(type)) {
				TYPES.put(type, BasicTypes.VALUED_ENUM);
				return BasicTypes.VALUED_ENUM;
			}
			Entry<Class, Byte> candidate = null;
			for (Entry<Class, Byte> e : TYPES.entrySet()) {
				if (e.getKey().isAssignableFrom(type)) {
					if (candidate == null)
						candidate = e;
					else if (candidate.getKey().isAssignableFrom(e.getKey()))
						candidate = e;
					else if (!e.getKey().isAssignableFrom(candidate.getKey()))
						throw new DetailedException("Ambiguous class for basic type: " + type).set("supplied Type", type).set("candidate 1", candidate.getKey()).set("candidate 2",
								e.getKey());
				}
			}
			if (candidate != null && candidate.getValue().byteValue() != OBJECT) {
				TYPES.put(type, candidate.getValue());
				return candidate.getValue().byteValue();
			} else
				return UNDEFINED;

		} else
			return r.byteValue();
	}
	public static byte toTypeForObject(Object type) {
		return type == null ? NULL : toType(type.getClass());

	}

	public static Class toClass(byte basicType) {
		switch (basicType) {
			case PRIMITIVE_BOOLEAN:
				return boolean.class;
			case PRIMITIVE_BYTE:
				return byte.class;
			case PRIMITIVE_SHORT:
				return short.class;
			case PRIMITIVE_CHAR:
				return char.class;
			case PRIMITIVE_INT:
				return int.class;
			case PRIMITIVE_FLOAT:
				return float.class;
			case PRIMITIVE_LONG:
				return long.class;
			case PRIMITIVE_DOUBLE:
				return double.class;
			case PRIMITIVE_OBJECT:
				return Object.class;
			case PRIMITIVE_VOID:
				return void.class;
			case BOOLEAN:
				return Boolean.class;
			case SHORT:
				return Short.class;
			case CHAR:
				return Character.class;
			case INT:
				return Integer.class;
			case FLOAT:
				return Float.class;
			case LONG:
				return Long.class;
			case DOUBLE:
				return Double.class;
			case OBJECT:
				return Object.class;
			case VOID:
				return Void.class;
			case STRING:
				return String.class;
			case LIST:
				return List.class;
			case SET:
				return Set.class;
			case MAP:
				return Map.class;
			case PASSWORD:
				return Password.class;
			case DATE:
				return Date.class;
			case DATE_MILLIS:
				return DateMillis.class;
			case COMPLEX:
				return Complex.class;
			case UUID:
				return UUID.class;
			case BYTES:
				return Bytes.class;
			case DAY:
				return Day.class;
			case DAYTIME:
				return DayTime.class;
			case TIME_ZONE:
				return TimeZone.class;
			case DATE_NANOS:
				return DateNanos.class;
			case IDEABLE:
				return Ideable.class;
			case MESSAGE:
				return Message.class;
			case TABLE:
				return Table.class;
			case FIXPOINT:
				return FixPoint.class;
			case BIGDECIMAL:
				return BigDecimal.class;
			case BIGINTEGER:
				return BigInteger.class;
			case VALUED_ENUM:
				return ValuedEnum.class;

			case ARRAY:
				return Object[].class;

			case STRING_ARRAY:
				return String[].class;

			case BYTE_ARRAY_ARRAY:
				return byte[][].class;
			case LONG_KEY_MAP:
				return LongKeyMap.class;
			case CLASS:
				return Class.class;
			case NULL:
				return Short.class;
			case THROWABLE:
				return Throwable.class;
			case ENUM:
				return Enum.class;
			case TUPLE:
				return Tuple.class;

			case PRIMITIVE_BYTE_ARRAY:
				return byte[].class;
			case PRIMITIVE_INT_ARRAY:
				return int[].class;
			case PRIMITIVE_LONG_ARRAY:
				return long[].class;
			case PRIMITIVE_CHAR_ARRAY:
				return char[].class;
			case PRIMITIVE_DOUBLE_ARRAY:
				return double[].class;
			case PRIMITIVE_FLOAT_ARRAY:
				return float[].class;
			case COLOR_GRADIENT:
				return ColorGradient.class;

			case MAPMESSAGE:
			case PERSISTABLE_MAP:
			case PERSISTABLE_LIST:
			case PERSISTABLE_SET:
			case UNDEFINED:
			case CUSTOM:
			default:
				return null;
		}
	}
	public static String toString(byte basicType) {
		switch (basicType) {
			case PRIMITIVE_BOOLEAN:
				return "PRIMITIVE_BOOLEAN";
			case PRIMITIVE_BYTE:
				return "PRIMITIVE_BYTE";
			case PRIMITIVE_SHORT:
				return "PRIMITIVE_SHORT";
			case PRIMITIVE_CHAR:
				return "PRIMITIVE_CHAR";
			case PRIMITIVE_INT:
				return "PRIMITIVE_INT";
			case PRIMITIVE_FLOAT:
				return "PRIMITIVE_FLOAT";
			case PRIMITIVE_LONG:
				return "PRIMITIVE_LONG";
			case PRIMITIVE_DOUBLE:
				return "PRIMITIVE_DOUBLE";
			case PRIMITIVE_OBJECT:
				return "PRIMITIVE_OBJECT";
			case PRIMITIVE_VOID:
				return "PRIMITIVE_VOID";
			case BOOLEAN:
				return "BOOLEAN";
			case SHORT:
				return "SHORT";
			case CHAR:
				return "CHAR";
			case INT:
				return "INT";
			case FLOAT:
				return "FLOAT";
			case LONG:
				return "LONG";
			case DOUBLE:
				return "DOUBLE";
			case OBJECT:
				return "OBJECT";
			case VOID:
				return "VOID";
			case STRING:
				return "STRING";
			case LIST:
				return "LIST";
			case SET:
				return "SET";
			case MAP:
				return "MAP";
			case PASSWORD:
				return "PASSWORD";
			case DATE:
				return "DATE";
			case DATE_MILLIS:
				return "DATE_MILLIS";
			case COMPLEX:
				return "COMPLEX";
			case UUID:
				return "UUID";
			case BYTES:
				return "BYTES";
			case DAY:
				return "DAY";
			case DAYTIME:
				return "DAYTIME";
			case TIME_ZONE:
				return "TIME_ZONE";
			case DATE_NANOS:
				return "NANODATE";
			case IDEABLE:
				return "IDEABLE";
			case MESSAGE:
				return "MESSAGE";
			case TABLE:
				return "TABLE";
			case TABLE_COLUMNAR:
				return "TABLE_COLUMNAR";
			case FIXPOINT:
				return "FIXPOINT";
			case BIGDECIMAL:
				return "BIGDECIMAL";
			case BIGINTEGER:
				return "BIGINTEGER";
			case VALUED_ENUM:
				return "VALUED_ENUM";
			case ARRAY:
				return "ARRAY";
			case STRING_ARRAY:
				return "STRING_ARRAY";
			case BYTE_ARRAY_ARRAY:
				return "BYTE_ARRAY_ARRAY";
			case LONG_KEY_MAP:
				return "LONG_KEY_MAP";
			case CLASS:
				return "CLASS";
			case NULL:
				return "NULL";
			case THROWABLE:
				return "THROWABLE";
			case ENUM:
				return "ENUM";
			case TUPLE:
				return "TUPLE";
			case PRIMITIVE_BYTE_ARRAY:
				return "PRIMITIVE_BYTE_ARRAY";
			case PRIMITIVE_INT_ARRAY:
				return "PRIMITIVE_INT_ARRAY";
			case PRIMITIVE_LONG_ARRAY:
				return "PRIMITIVE_LONG_ARRAY";
			case PRIMITIVE_CHAR_ARRAY:
				return "PRIMITIVE_CHAR_ARRAY";
			case PRIMITIVE_DOUBLE_ARRAY:
				return "PRIMITIVE_DOUBLE_ARRAY";
			case PRIMITIVE_FLOAT_ARRAY:
				return "PRIMITIVE_FLOAT_ARRAY";
			case MAPMESSAGE:
				return "MAPMESSAGE";
			case PERSISTABLE_MAP:
				return "PERSISTABLE_MAP";
			case PERSISTABLE_LIST:
				return "PERSISTABLE_LIST";
			case PERSISTABLE_SET:
				return "PERSISTABLE_SET";
			case UNDEFINED:
				return "UNDEFINED";
			case CUSTOM:
				return "CUSTOM";
			default:
				throw new RuntimeException("Unknown basic type: " + basicType);
		}
	}

}
