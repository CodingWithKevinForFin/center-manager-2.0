package com.f1.ami.amidb.jdbc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.f1.base.Caster;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Array;
import com.f1.utils.casters.Caster_BigDecimal;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;

public class SqlTypesMap {

	/**
	 * Translates a data type from an integer (java.sql.Types value) to a string that represents the corresponding class.
	 * 
	 * @param type
	 *            The java.sql.Types value to convert to its corresponding class.
	 * @return The class that corresponds to the given java.sql.Types value, or Object.class if the type has no known mapping.
	 */
	public static Class<?> toClass(int type) {
		Class<?> result = Object.class;

		switch (type) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				result = String.class;
				break;

			case Types.NUMERIC:
			case Types.DECIMAL:
				result = java.math.BigDecimal.class;
				break;

			case Types.BIT:
				result = Boolean.class;
				break;

			case Types.TINYINT:
				result = Byte.class;
				break;

			case Types.SMALLINT:
				result = Short.class;
				break;

			case Types.INTEGER:
				result = Integer.class;
				break;

			case Types.BIGINT:
				result = Long.class;
				break;

			case Types.REAL:
			case Types.FLOAT:
				result = Float.class;
				break;

			case Types.DOUBLE:
				result = Double.class;
				break;

			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				result = Byte[].class;
				break;

			case Types.DATE:
				result = java.sql.Date.class;
				break;

			case Types.TIME:
				result = java.sql.Time.class;
				break;

			case Types.TIMESTAMP:
				result = java.sql.Timestamp.class;
				break;
		}

		return result;
	}
	public static Caster<?> toCaster(int type) {
		Caster<?> result = Caster_Simple.OBJECT;

		switch (type) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				result = Caster_String.INSTANCE;
				break;

			case Types.NUMERIC:
			case Types.DECIMAL:
				result = Caster_BigDecimal.INSTANCE;
				break;

			case Types.BIT:
				result = Caster_Boolean.INSTANCE;
				break;

			case Types.TINYINT:
				result = Caster_Byte.INSTANCE;
				break;

			case Types.SMALLINT:
				result = Caster_Short.INSTANCE;
				break;

			case Types.INTEGER:
				result = Caster_Integer.INSTANCE;
				break;

			case Types.BIGINT:
				result = Caster_Long.INSTANCE;
				break;

			case Types.REAL:
			case Types.FLOAT:
				result = Caster_Float.INSTANCE;
				break;

			case Types.DOUBLE:
				result = Caster_Double.INSTANCE;
				break;

			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				result = Caster_Array.BYTE;
				break;
		}
		return result;
	}

	private static final Map<Class, Integer> TYPES = new HashMap<Class, Integer>();
	static {
		TYPES.put(String.class, Types.VARCHAR);
		TYPES.put(Integer.class, Types.INTEGER);
		TYPES.put(int.class, Types.INTEGER);
		TYPES.put(Long.class, Types.BIGINT);
		TYPES.put(long.class, Types.BIGINT);
		TYPES.put(Short.class, Types.SMALLINT);
		TYPES.put(short.class, Types.SMALLINT);
		TYPES.put(Double.class, Types.DOUBLE);
		TYPES.put(double.class, Types.DOUBLE);
		TYPES.put(Float.class, Types.FLOAT);
		TYPES.put(float.class, Types.FLOAT);
		TYPES.put(Byte.class, Types.TINYINT);
		TYPES.put(byte.class, Types.TINYINT);
		TYPES.put(Boolean.class, Types.BOOLEAN);
		TYPES.put(boolean.class, Types.BOOLEAN);
		TYPES.put(BigDecimal.class, Types.DECIMAL);
		TYPES.put(char.class, Types.CHAR);
		TYPES.put(Character.class, Types.CHAR);
		TYPES.put(byte[].class, Types.BINARY);
		TYPES.put(Date.class, Types.DATE);
		TYPES.put(Time.class, Types.TIME);
		TYPES.put(Timestamp.class, Types.TIMESTAMP);
		TYPES.put(java.util.Date.class, Types.TIMESTAMP);
		TYPES.put(DateNanos.class, Types.TIMESTAMP);
		TYPES.put(DateMillis.class, Types.TIMESTAMP);
	}

	public static int toType(Class t) {
		Integer r = TYPES.get(t);
		return r == null ? Integer.MIN_VALUE : r.intValue();
	}
	public static String toName(Class type) {
		int r = toType(type);
		if (r == Integer.MIN_VALUE)
			return type.getName();
		return toName(r);
	}
	public static String toName(int type) {
		Class<?> result = Object.class;

		switch (type) {
			case Types.CHAR:
				return "CHAR";
			case Types.VARCHAR:
				return "VARCHAR";
			case Types.LONGVARCHAR:
				return "LONGVARCHAR";
			case Types.NUMERIC:
				return "NUMERIC";
			case Types.DECIMAL:
				return "DECIMAL";
			case Types.BIT:
				return "BIT";
			case Types.TINYINT:
				return "TINYINT";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.REAL:
				return "REAL";
			case Types.FLOAT:
				return "FLOAT";
			case Types.DOUBLE:
				return "DOUBLE";
			case Types.BINARY:
				return "BINARY";
			case Types.VARBINARY:
				return "VARBINARY";
			case Types.LONGVARBINARY:
				return "LONGVARBINARY";
			case Types.DATE:
				return "DATE";
			case Types.TIME:
				return "TIME";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
		}
		return SH.toString(result);
	}
}
