package com.f1.ami.center.ds;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import kx.c;
import kx.c.Dict;
import kx.c.Flip;
import kx.c.Minute;
import kx.c.Month;
import kx.c.Second;
import kx.c.Timespan;

import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.base.Getter;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.table.BasicTable;

public class AmiKxHelper {

	private static final Logger log = LH.get();

	public static Table toTable(String name, Object o, int limit) {

		if (o != null && o.getClass().isArray()) { // convert array into a table of one col
			o = new Flip(new Dict(new String[] { "values" }, new Object[] { o }));
		} else if (o instanceof Dict) { // map to flip...assuming an aggregate t
			Dict d = (Dict) o;

			if (d.x.getClass() == String[].class) { // sym -> flip ... convert to table with one row of flips
				Object[] vals = new Object[Array.getLength(d.y)];
				for (int i = 0; i < vals.length; i++)
					vals[i] = new Object[] { Array.get(d.y, i) };
				o = new Flip(new Dict(d.x, vals));
			} else if (d.x.getClass() == Flip.class) { // keyed table or a key value dict
				Flip f1 = (Flip) d.x;
				Flip f2 = (Flip) d.y;

				String[] cols = AH.cat(f1.x, f2.x);
				Object[] data = AH.cat(f1.y, f2.y);

				o = new Flip(new Dict(cols, data));
			} else {
				o = new Flip(new Dict(new String[] { "key", "value" }, new Object[] { d.x, d.y }));
			}
		} else if (!(o instanceof Flip)) {
			o = new Flip(new Dict(new String[] { "value" }, new Object[] { o }));
		}

		if (o instanceof Flip) {
			Flip f = (Flip) o;
			final int rows;
			Object fy = f.y[0];
			if (!fy.getClass().isArray()) {
				Class<?> clazz = fy.getClass();
				if (clazz == Timespan.class) {
					fy = ((Timespan) fy).j;
					clazz = long.class;
				} else if (clazz == Minute.class) {
					fy = ((Minute) fy).i;
					clazz = int.class;
				} else if (clazz == Second.class) {
					fy = ((Second) fy).i;
					clazz = int.class;
				} else if (clazz == Month.class) {
					fy = ((Month) fy).i;
					clazz = int.class;
				} else if (clazz == UUID.class) {
					fy = ((UUID) fy).toString();
					clazz = String.class;
				} else if (Date.class.isAssignableFrom(clazz)) {
					fy = ((Date) fy).getTime();
					clazz = long.class;
				} else if (clazz == short.class) {
					fy = ((Short) fy).intValue();
					clazz = int.class;
				} else if (clazz == byte.class) {
					fy = ((Byte) fy).intValue();
					clazz = int.class;
				} else if (clazz == Object.class) {
					clazz = String.class;
					fy = fy.toString();
				}
				BasicTable r = new BasicTable(clazz, "value");
				r.setTitle("value");
				r.getRows().addRow(fy);
				return r;
			}
			int length = Array.getLength(fy);
			rows = limit == -1 ? length : Math.min(length, limit);
			int cols = f.x.length;
			BasicTable r;
			// get the types
			{
				Class<?>[] types = new Class[cols];
				for (int i = 0; i < f.y.length; i++) {
					final Object a = f.y[i];
					Class clazz = a.getClass().getComponentType();
					types[i] = clazz;

					if (clazz == Timespan.class)
						types[i] = long.class;
					else if (clazz == Minute.class)
						types[i] = int.class;
					else if (clazz == Second.class)
						types[i] = int.class;
					else if (clazz == Month.class)
						types[i] = int.class;
					else if (clazz == UUID.class)
						types[i] = String.class;
					else if (Date.class.isAssignableFrom(clazz))
						types[i] = long.class;
					else if (types[i] == short.class)
						types[i] = int.class;
					else if (types[i] == byte.class)
						types[i] = int.class;
					else if (clazz == Object.class) {
						if (rows > 0 && Array.get(a, 0).getClass() == char[].class)
							types[i] = String.class;
						else if (rows > 0 && (Array.get(a, 0).getClass() == Flip.class || Array.get(a, 0).getClass() == Dict.class))
							types[i] = Table.class;
						else
							types[i] = String.class;
					}
				}
				r = new BasicTable(types, f.x, rows);
			}

			// create tables with all rows
			r.setTitle(name);

			for (int i = 0; i < rows; i++) {
				final Object[] row = new Object[cols];
				r.getRows().addRow(row);
			}

			// use fill funcs to populate columns
			for (int i = 0; i < f.y.length; i++) {
				final Object a = f.y[i];
				Class clazz = a.getClass().getComponentType();

				if (clazz == int.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (int[]) a, 0, rows, (Integer) c.NULL('i'));
				} else if (clazz == double.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (double[]) a, 0, rows, (Double) c.NULL('f'));
				} else if (clazz == long.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (long[]) a, 0, rows, (Long) c.NULL('j'));
				} else if (clazz == float.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (float[]) a, 0, rows, (Float) c.NULL('e'));
				} else if (clazz == boolean.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (boolean[]) a, 0, rows);
				} else if (clazz == char.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (char[]) a, 0, rows);
				} else if (clazz == byte.class) {
					TableHelper.fill(r.getColumnAt(i), 0, AH.castToInts((byte[]) a), 0, rows, (Integer) c.NULL('i'));
				} else if (clazz == short.class) {
					TableHelper.fill(r.getColumnAt(i), 0, AH.castToInts((short[]) a), 0, rows, (Integer) c.NULL('i'));
				} else if (clazz == Timespan.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (Timespan[]) a, 0, rows, new Getter<Timespan, Long>() {
						@Override
						public Long get(Timespan key) {
							return key.j;
						}

					}, (Timespan) c.NULL('n'));
				} else if (clazz == Minute.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (Minute[]) a, 0, rows, new Getter<Minute, Integer>() {
						@Override
						public Integer get(Minute key) {
							return key.i;
						}

					}, (Minute) c.NULL('u'));
				} else if (clazz == Second.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (Second[]) a, 0, rows, new Getter<Second, Integer>() {
						@Override
						public Integer get(Second key) {
							return key.i;
						}

					}, (Second) c.NULL('v'));
				} else if (clazz == Month.class) {
					TableHelper.fill(r.getColumnAt(i), 0, (Month[]) a, 0, rows, new Getter<Month, Integer>() {
						@Override
						public Integer get(Month key) {
							return key.i;
						}

					}, (Month) c.NULL('m'));

				} else if (clazz == UUID.class) {
					final UUID nv = (UUID) c.NULL('g');
					TableHelper.fill(r.getColumnAt(i), 0, (UUID[]) a, 0, rows, new Getter<UUID, String>() {
						@Override
						public String get(UUID key) {
							return nv.equals(key) ? null : key.toString();
						}

					});
				}

				// covers Date/Time/Datetime/
				else if (Date.class.isAssignableFrom(clazz)) {
					TableHelper.fill(r.getColumnAt(i), 0, (Date[]) a, 0, rows, new Getter<Date, Long>() {
						@Override
						public Long get(Date key) {
							return key.getTime();
						}

					}, (Date) c.NULL('d'));
				} else if (clazz == Object.class && rows > 0 && Array.get(a, 0).getClass() == char[].class) {
					TableHelper.fill(r.getColumnAt(i), 0, (Object[]) a, 0, rows, new Getter<Object, String>() {
						@Override
						public String get(Object key) {
							return key.getClass() == char[].class ? new String((char[]) key) : deepToString(key);
						}

					});
				} else if (clazz == Object.class && rows > 0 && (Array.get(a, 0).getClass() == Flip.class || Array.get(a, 0).getClass() == Dict.class)) {
					TableHelper.fill(r.getColumnAt(i), 0, (Object[]) a, 0, rows, new Getter<Object, Table>() {
						@Override
						public Table get(Object key) {
							return toTable("table", key, Integer.MAX_VALUE);
						}
					});
				} else {
					//string it
					TableHelper.fill(r.getColumnAt(i), 0, (Object[]) a, 0, rows, new Getter<Object, String>() {
						@Override
						public String get(Object key) {
							return deepToString(key);
						}

					});
				}

			}

			return r;
		}

		throw new IllegalArgumentException("Only flips are supported [" + o + "]");
	}
	public static String deepToString(Object a) {
		if (a != null && a.getClass().isArray())
			return Arrays.deepToString(a.getClass().getComponentType().isPrimitive() ? new Object[] { a } : (Object[]) a);
		else
			return SH.toString(a);
	}
	public static byte getAmiTypeForKxType(char type) {
		switch (type) {
			case ' ': // mixed type convert to string
				return (AmiDatasourceColumn.TYPE_STRING);
			case 'b':
				return (AmiDatasourceColumn.TYPE_BOOLEAN);

			case 'x':
				return (AmiDatasourceColumn.TYPE_BINARY);

			case 'i':
			case 'j':
				return (AmiDatasourceColumn.TYPE_LONG);

			case 'e':
				return (AmiDatasourceColumn.TYPE_FLOAT);

			case 'f':
				return (AmiDatasourceColumn.TYPE_DOUBLE);

			case 'c':
				return (AmiDatasourceColumn.TYPE_CHAR);

			case 'g':
			case 'C':
			case 's':
				return (AmiDatasourceColumn.TYPE_STRING);

				// dates...etc
			case 'p':
			case 'd':
			case 'z':
			case 'n':
			case 't':
				return (AmiDatasourceColumn.TYPE_LONG);
				// month/minute/second
			case 'h':
			case 'm':
			case 'u':
			case 'v':
				return (AmiDatasourceColumn.TYPE_INT);

			default:
				LH.warning(log, "Unhandled column type " + type);
				return (AmiDatasourceColumn.TYPE_STRING);
		}
	}
}
