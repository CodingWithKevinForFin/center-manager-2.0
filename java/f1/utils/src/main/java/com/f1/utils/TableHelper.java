/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Getter;
import com.f1.base.Message;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.structs.table.BasicRowComparator;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.utils.structs.table.derived.AggregateColumn;
import com.f1.utils.structs.table.derived.AggregateGroupByColumn;
import com.f1.utils.structs.table.derived.DerivedColumn;
import com.f1.utils.structs.table.derived.DerivedTable;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

public class TableHelper {

	public static final int SHOW_NAMES = 1;
	public static final int SHOW_TITLE = 128;
	public static final int SHOW_TYPES = 2;
	public static final int SHOW_HEADER_BREAK = 4;

	public static final int SHOW_BORDER_TOP = 8;
	public static final int SHOW_BORDER_BOTTOM = 16;
	public static final int SHOW_BORDER_LEFT = 32;
	public static final int SHOW_BORDER_RIGHT = 64;
	public static final int SHOW_FORMULAS = 128;
	public static final int DISALBE_MULTILINE_CELLS = 256;

	public static final int SHOW_BORDERS = SHOW_BORDER_LEFT | SHOW_BORDER_RIGHT | SHOW_BORDER_TOP | SHOW_BORDER_BOTTOM;
	public static final int SHOW_HEADER = SHOW_HEADER_BREAK | SHOW_NAMES | SHOW_TYPES | SHOW_TITLE | SHOW_FORMULAS;
	public static final int SHOW_ALL = SHOW_BORDERS | SHOW_HEADER;
	public static final char DEFAULT_CROSS = '+';
	public static final char DEFAULT_HORIZONTAL = '-';
	public static final char DEFAULT_VERTICLE = '|';
	public static final int SHOW_ALL_BUT_TYPES = SHOW_ALL ^ SHOW_TYPES;
	public static final int DEFAULT_MAX_LENGTH = 1024 * 1024 * 100;

	static public String toString(Table table) {
		return toString(table, "", SHOW_ALL, new StringBuilder()).toString();
	}

	static public String toString(Table table, String prefix, int options) {
		return toString(table, prefix, options, new StringBuilder()).toString();
	}

	static public StringBuilder toString(Table table, String prefix, int options, StringBuilder sb) {
		return toString(table, prefix, options, sb, SH.NEWLINE);
	}
	static public StringBuilder toString(Table table, String prefix, int options, StringBuilder sb, int maxlength) {
		return toString(table, prefix, options, sb, SH.NEWLINE, DEFAULT_VERTICLE, DEFAULT_HORIZONTAL, DEFAULT_CROSS, maxlength, null);
	}

	static public StringBuilder toString(Table table, String prefix, int options, StringBuilder sb, String newLine) {
		return toString(table, prefix, options, sb, newLine, DEFAULT_VERTICLE, DEFAULT_HORIZONTAL, DEFAULT_CROSS, DEFAULT_MAX_LENGTH, null);
	}
	static public StringBuilder toString(Table table, String prefix, int options, StringBuilder sb, String newLine, int maxlength) {
		return toString(table, prefix, options, sb, newLine, DEFAULT_VERTICLE, DEFAULT_HORIZONTAL, DEFAULT_CROSS, maxlength, null);
	}

	static public StringBuilder toString(Table table, String prefix, int options, StringBuilder sb, String newLine, char ver, char hor, char cro) {
		return toString(table, prefix, options, sb, newLine, ver, hor, cro, Integer.MAX_VALUE, null);
	}
	static public StringBuilder toString(Table table, String prefix, int options, StringBuilder sb, String newLine, char ver, char hor, char cro, int maxlength,
			Map<Class, String> typeFormats) {
		return toString(table, prefix, options, sb, newLine, ver, hor, cro, Integer.MAX_VALUE, typeFormats, null);
	}
	static public StringBuilder toString(Table table, String prefix, int options, StringBuilder sb, String newLine, char ver, char hor, char cro, int maxlength,
			Map<Class, String> typeFormats, Formatter[] columnFormatters) {
		List<Column> columns = table.getColumns();
		int cols = columns.size();
		if (cols == 0) {
			if (table.getSize() > 0)
				sb.append("<columnless-table with ").append(table.getSize()).append(" rows>").append(newLine);
			else
				sb.append("<empty-table>").append(newLine);
			return sb;
		}

		boolean incTopBorder = MH.anyBits(options, SHOW_BORDER_TOP);
		boolean incRightBorder = MH.anyBits(options, SHOW_BORDER_RIGHT);
		boolean incBottomBorder = MH.anyBits(options, SHOW_BORDER_BOTTOM);
		boolean incLeftBorder = MH.anyBits(options, SHOW_BORDER_LEFT);
		boolean incNames = MH.anyBits(options, SHOW_NAMES);
		boolean incTitle = MH.anyBits(options, SHOW_TITLE) && SH.is(table.getTitle());
		boolean incTypes = MH.anyBits(options, SHOW_TYPES);
		boolean incHeaderBreak = MH.anyBits(options, SHOW_HEADER_BREAK);
		boolean disableMultilineCells = MH.anyBits(options, DISALBE_MULTILINE_CELLS);
		boolean incFormula = MH.anyBits(options, SHOW_FORMULAS) && table instanceof DerivedTable;

		int[] widths = new int[cols];
		boolean[] hasMultiLine = new boolean[cols];
		boolean hasMultLines = false;
		StringBuilder tmp = new StringBuilder();

		int totalWidth = table.getColumnsCount() - 1;
		String[] typeNames = incTypes ? new String[cols] : null;
		for (int i = 0; i < cols; i++) {
			Column c = columns.get(i);
			int max = 0;
			if (incNames)
				max = Math.max(max, c.getId().toString().length());
			if (incTypes) {
				if (typeFormats != null) {
					String name = typeFormats.get(c.getType());
					typeNames[i] = name != null ? name : c.getType().getSimpleName();
				} else
					typeNames[i] = c.getType().getSimpleName();
				max = Math.max(max, typeNames[i].length());
			}
			if (incFormula) {
				if (c instanceof DerivedColumn)
					max = Math.max(max, ((DerivedColumn) c).getCalculator().toString(SH.clear(tmp)).length());
				else if (c instanceof AggregateColumn)
					max = Math.max(max, ((AggregateColumn) c).toCalcString(SH.clear(tmp)).length());
				else if (c instanceof AggregateGroupByColumn)
					max = Math.max(max, ((AggregateGroupByColumn) c).toCalcString(SH.clear(tmp)).length());
			}
			for (int row = 0; row < table.getSize(); row++) {
				formatCell(table.getAt(row, c.getLocation()), SH.clear(tmp), columnFormatters == null ? null : columnFormatters[i]);
				if (!disableMultilineCells && tmp.indexOf("\n") != -1) {
					hasMultiLine[i] = true;
					hasMultLines = true;
					for (String s : SH.splitLines(tmp.toString(), false))
						max = Math.max(max, s.length());
				} else
					max = Math.max(max, tmp.length());
			}
			widths[i] = max;
			totalWidth += max;
		}
		if (incTitle && totalWidth < table.getTitle().length()) {
			int remaining = table.getTitle().length() - totalWidth;
			int perColumnAdd = remaining / table.getColumnsCount();
			for (int i = 0; i < cols; i++) {
				widths[i] += perColumnAdd;
				remaining -= perColumnAdd;
			}
			widths[0] += remaining;
			totalWidth = table.getTitle().length();
		}
		int maxrows = (int) (((long) maxlength + totalWidth - 1) / Math.max(totalWidth, 1));
		if (maxrows == 0)
			return sb;

		StringBuilder divider = null;
		if (incTopBorder || incHeaderBreak || incBottomBorder) {
			divider = new StringBuilder();
			divider.append(prefix);
			if (incLeftBorder)
				divider.append(cro);
			for (int i = 0; i < cols; i++) {
				if (i > 0)
					divider.append(cro);
				SH.repeat(hor, widths[i], divider);
			}
			if (incRightBorder)
				divider.append(cro);
			divider.append(newLine);
			if (--maxrows <= 0)
				return sb;
		}

		if (incTitle) {
			sb.append(prefix);
			if (incLeftBorder)
				sb.append(cro);
			if (incTopBorder)
				SH.repeat(hor, totalWidth, sb);
			if (incRightBorder)
				sb.append(cro);
			sb.append(newLine);
			sb.append(prefix);
			maxrows--;
			if (incLeftBorder)
				sb.append(ver);
			SH.centerAlign(' ', table.getTitle(), totalWidth, true, sb);
			if (incRightBorder)
				sb.append(ver);
			sb.append(newLine);
			if (--maxrows <= 0)
				return sb;
		}
		if (incTopBorder)
			sb.append(divider);

		if (incNames) {
			sb.append(prefix);
			if (incLeftBorder)
				sb.append(ver);
			for (int i = 0; i < cols; i++) {
				if (i > 0)
					sb.append(ver);
				SH.leftAlign(' ', columns.get(i).getId().toString(), widths[i], false, sb);
			}
			if (incRightBorder)
				sb.append(ver);
			sb.append(newLine);
			if (--maxrows <= 0)
				return sb;
		}

		if (incTypes) {
			sb.append(prefix);
			if (incLeftBorder)
				sb.append(ver);
			for (int i = 0; i < cols; i++) {
				if (i > 0)
					sb.append(ver);
				SH.leftAlign(' ', typeNames[i], widths[i], false, sb);
			}
			if (incRightBorder)
				sb.append(ver);
			sb.append(newLine);
			if (--maxrows <= 0)
				return sb;
		}

		if (incFormula) {
			sb.append(prefix);
			if (incLeftBorder)
				sb.append(ver);
			for (int i = 0; i < cols; i++) {
				if (i > 0)
					sb.append(ver);
				Column c = columns.get(i);
				if (c instanceof DerivedColumn) {
					int len = sb.length();
					((DerivedColumn) c).getCalculator().toString(sb);
					SH.repeat(' ', widths[i] - (sb.length() - len), sb);
				} else if (c instanceof AggregateColumn) {
					int len = sb.length();
					((AggregateColumn) c).toCalcString(sb);
					SH.repeat(' ', widths[i] - (sb.length() - len), sb);
				} else if (c instanceof AggregateGroupByColumn) {
					int len = sb.length();
					((AggregateGroupByColumn) c).toCalcString(sb);
					SH.repeat(' ', widths[i] - (sb.length() - len), sb);
				} else
					SH.repeat(' ', widths[i], sb);
			}
			if (incRightBorder)
				sb.append(ver);
			sb.append(newLine);
			if (--maxrows <= 0)
				return sb;
		}

		if ((incNames || incTypes || incFormula) && incHeaderBreak)
			sb.append(divider);

		for (int row = 0; row < table.getSize(); row++) {
			int lineCount = 1;//this is 
			if (hasMultLines) {
				for (int i = 0; i < cols; i++) {
					if (hasMultiLine[i]) {
						Object at = table.getAt(row, i);
						formatCell(at, SH.clear(tmp), columnFormatters == null ? null : columnFormatters[i]);
						if (tmp.indexOf("\n") != -1)
							lineCount = Math.max(SH.splitLines(tmp.toString(), false).length, lineCount);
					}
				}
			}
			sb.append(prefix);
			if (lineCount == 1) {
				for (int i = 0; i < cols; i++) {
					Object at = table.getAt(row, i);
					if (at == null && ver == '|') {
						if (i > 0)
							sb.append('!');
						else if (incLeftBorder)
							sb.append('!');
					} else {
						if (i > 0)
							sb.append(ver);
						else if (incLeftBorder)
							sb.append(ver);
					}
					int pos = sb.length();
					formatCell(at, sb, columnFormatters == null ? null : columnFormatters[i]);
					int remaining = pos + widths[i] - sb.length();
					while (remaining-- > 0)
						sb.append(' ');
				}
				if (incRightBorder)
					sb.append(ver);
				sb.append(newLine);
			} else {
				String cells[][] = new String[cols][];
				for (int i = 0; i < cols; i++) {
					Object at = table.getAt(row, i);
					if (at != null) {
						formatCell(at, SH.clear(tmp), columnFormatters == null ? null : columnFormatters[i]);
						cells[i] = SH.splitLines(tmp.toString(), false);
					}
				}

				for (int y = 0; y < lineCount; y++) {
					for (int i = 0; i < cols; i++) {
						Object at;
						if (cells[i] == null)
							at = null;
						else if (cells[i].length <= y)
							at = "";
						else
							at = cells[i][y];
						if (at == null && ver == '|') {
							if (i > 0)
								sb.append('!');
							else if (incLeftBorder)
								sb.append('!');
						} else {
							if (i > 0)
								sb.append(y == 0 || at == null || cells[i].length <= y ? ver : ':');
							else if (incLeftBorder)
								sb.append(y == 0 || at == null || cells[i].length <= y ? ver : ':');
						}
						int pos = sb.length();
						sb.append(at);
						int remaining = pos + widths[i] - sb.length();
						while (remaining-- > 0)
							sb.append(' ');
					}
					if (incRightBorder)
						sb.append(ver);
					sb.append(newLine);
				}
			}
			if (--maxrows <= 0)
				return sb;
		}

		if (incBottomBorder)
			sb.append(divider);
		return sb;
	}

	private static StringBuilder formatCell(Object value, StringBuilder sink, Formatter f) {
		if (f != null)
			f.format(value, sink);
		else
			SH.s(value, sink);
		return sink;
	}

	public static Table toTable(Iterable<? extends Message> list) {
		if (CH.isEmpty(list))
			return new BasicTable(new Class[0], new String[0]);
		Iterator<? extends Message> it = list.iterator();
		ValuedSchema<? extends Valued> schema = it.next().askSchema();
		while (it.hasNext()) {
			if (it.next().askSchema().askOriginalType() != schema.askOriginalType())
				return toTableMultiTypes(list);
		}
		it = null;
		ValuedParam[] vps = schema.askValuedParams();
		Class<?>[] types = new Class<?>[vps.length];
		String[] names = new String[vps.length];
		for (int i = 0; i < vps.length; i++) {
			types[i] = OH.getBoxed(vps[i].getReturnType());
			names[i] = vps[i].getName();
		}
		BasicTable table = new BasicTable(types, names);
		for (Message m : list) {
			Object[] row = new Object[vps.length];
			for (int i = 0; i < vps.length; i++)
				row[i] = vps[i].getValue(m);
			table.getRows().addRow(row);
		}

		return table;
	}
	private static Table toTableMultiTypes(Iterable<? extends Message> list) {
		if (CH.isEmpty(list))
			return new BasicTable(new Class[0], new String[0]);
		Map<Class<?>, ValuedSchema<?>> schemas = new HashMap<Class<?>, ValuedSchema<?>>();
		for (Message m : list)
			schemas.put(m.askSchema().askOriginalType(), m.askSchema());
		BasicCalcTypes typesMap = new BasicCalcTypes();
		for (ValuedSchema<?> vs : schemas.values()) {
			for (ValuedParam<?> vp : vs.askValuedParams()) {
				Class<?> existing = typesMap.getType(vp.getName());
				typesMap.putType(vp.getName(), existing == null ? vp.getReturnType() : OH.getWidest(existing, vp.getReturnType()));
			}
		}
		List<String> nameList = CH.sort(typesMap.getVarKeys());
		int len = nameList.size();

		Class<?>[] types = new Class<?>[len];
		String[] names = new String[len];
		for (int i = 0; i < len; i++) {
			types[i] = typesMap.getType(nameList.get(i));
			names[i] = nameList.get(i);
		}
		BasicTable table = new BasicTable(types, names);
		for (Message m : list) {
			ValuedSchema<Valued> schema = m.askSchema();
			Object[] row = new Object[len];
			for (int i = 0; i < len; i++)
				if (schema.askParamValid(names[i]))
					row[i] = m.ask(names[i]);
			table.getRows().addRow(row);
		}
		return table;
	}
	public static void sort(Table t, String... cols) {
		final boolean[] ascending = new boolean[cols.length];
		final int[] columns = new int[cols.length];
		for (int i = 0; i < cols.length; i++) {
			ascending[i] = true;
			columns[i] = t.getColumn(cols[i]).getLocation();
		}
		BasicRowComparator c = new BasicRowComparator(columns, ascending);
		sort(t.getRows(), c);
	}
	public static void sortDesc(Table t, String... cols) {
		final boolean[] ascending = new boolean[cols.length];
		final int[] columns = new int[cols.length];
		for (int i = 0; i < cols.length; i++) {
			ascending[i] = false;
			columns[i] = t.getColumn(cols[i]).getLocation();
		}
		BasicRowComparator c = new BasicRowComparator(columns, ascending);
		sort(t.getRows(), c);
	}

	public static void sort(List<Row> rows, Comparator<Row> c) {
		if (rows.size() < 2)
			return;
		Row[] objects = rows.toArray(new Row[rows.size()]);
		Arrays.sort(objects, c);
		rows.clear();
		for (Row i : objects)
			rows.add(i);

	}
	public static void shuffle(TableList rows, Random random) {
		Row[] objects = rows.toArray(new Row[rows.size()]);
		AH.shuffle(objects, random);
		rows.clear();
		for (Row i : objects)
			rows.add(i);

	}

	public static List<Map<Object, Object>> toListOfMaps(Table table) {
		List<Map<Object, Object>> r = new ArrayList<Map<Object, Object>>(table.getRows().size());
		for (Row row : table.getRows()) {
			HashMap<Object, Object> map = new HashMap<Object, Object>();
			for (Object id : table.getColumnIds()) {
				Object val = row.get(id);
				if (val != null)
					map.put(id, row.get(id));
			}
			r.add(map);
		}
		return r;
	}
	public static List<Map<String, Object>> toListOfMapsSelectedCols(Table table, String[] columns) {
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>(table.getRows().size());
		for (Row row : table.getRows()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < columns.length; i++) {
				String id = columns[i];
				if (row.getType(id) != null) {
					Object val = row.get(id);
					map.put((String) id, row.get(id));
				}
			}
			r.add(map);
		}
		return r;
	}

	public static Map<Object, List<Object>> toMapOfLists(Table table) {
		Map<Object, List<Object>> r = new LinkedHashMap<Object, List<Object>>();
		int size = table.getRows().size();
		for (Column i : table.getColumns()) {
			List<Object> list = new ArrayList<Object>(size);
			for (int j = 0; j < size; j++)
				list.add(i.getValue(j));
			r.put(i.getId(), list);
		}
		return r;
	}

	public static void fromListOfMaps(List<Map<?, ?>> rows, Table sink) {
		for (Map<?, ?> row : rows) {
			Row nrow = sink.newEmptyRow();
			for (String id : sink.getColumnIds()) {
				nrow.put(id, row.get(id));
			}
			sink.getRows().add(nrow);
		}
	}

	public static Set<Object> getDistinctValues(SmartTable table, String columnId) {
		int loc = table.getColumn(columnId).getLocation();
		Set<Object> r = new HashSet<Object>();
		for (Row row : table.getRows())
			r.add(row.getAt(loc));
		return r;
	}

	public static com.f1.utils.structs.table.stack.BasicCalcTypes getColumnTypes(Table table) {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes(table.getColumnsCount());
		for (Column t : table.getColumns()) {
			r.putType((String) t.getId(), t.getType());
		}
		return r;
	}
	public static Class[] getColumnTypesArray(Table table) {
		Class[] r = new Class[table.getColumnsCount()];
		for (int i = 0; i < r.length; i++)
			r[i] = table.getColumnAt(i).getType();
		return r;
	}
	public static Object[] getColumnIdsArray(Table table) {
		Object[] r = new Object[table.getColumnsCount()];
		for (int i = 0; i < r.length; i++)
			r[i] = table.getColumnAt(i).getId();
		return r;
	}

	public static String generateId(Set<?> existing, String prefix) {
		StringBuilder sb = new StringBuilder(prefix);
		int len = sb.length();
		String r;
		int i = 0;
		while (existing.contains(r = sb.append(i++).toString()))
			sb.setLength(len);
		return r;
	}

	public static Table selectColumns(Table table, String... columns) {
		BasicTable r = new BasicTable();
		int count = columns.length;
		int pos[] = new int[count];
		for (int i = 0; i < count; i++) {
			Column col = table.getColumn(columns[i]);
			r.addColumn(col.getType(), col.getId());
			pos[i] = col.getLocation();
		}
		TableList rows = r.getRows();
		for (Row row : table.getRows()) {
			Object[] values = new Object[count];
			for (int i = 0; i < count; i++)
				values[i] = row.getAt(pos[i]);
			rows.addRow(values);
		}
		return r;
	}

	public static void fill(Column target, int targetStart, long data[], int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i]);
	}
	public static void fill(Column target, int targetStart, long data[], int start, int length, long nv) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i] == nv ? null : data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i] == nv ? null : data[start + i]);
	}

	public static void fill(Column target, int targetStart, int data[], int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i]);
	}
	public static void fill(Column target, int targetStart, int data[], int start, int length, int nv) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i] == nv ? null : data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i] == nv ? null : data[start + i]);
	}

	public static void fill(Column target, int targetStart, short data[], int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i]);
	}
	public static void fill(Column target, int targetStart, short data[], int start, int length, short nv) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i] == nv ? null : data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i] == nv ? null : data[start + i]);
	}

	public static void fill(Column target, int targetStart, byte data[], int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i]);
	}

	public static void fill(Column target, int targetStart, boolean data[], int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i]);
	}
	public static void fill(Column target, int targetStart, float data[], int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i]);
	}
	public static void fill(Column target, int targetStart, float data[], int start, int length, float nv) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, (data[i] == nv || (Float.isNaN(nv) && Float.isNaN(data[i]))) ? null : data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, (data[start + i] == nv || (Float.isNaN(nv) && Float.isNaN(data[start + i]))) ? null : data[start + i]);
	}

	public static void fill(Column target, int targetStart, double data[], int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i]);
	}
	public static void fill(Column target, int targetStart, double data[], int start, int length, double nv) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, (data[i] == nv || (Double.isNaN(nv) && Double.isNaN(data[i]))) ? null : data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, (data[start + i] == nv || (Double.isNaN(nv) && Double.isNaN(data[start + i]))) ? null : data[start + i]);
	}

	public static void fill(Column target, int targetStart, char data[], int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i]);
	}
	public static void fill(Column target, int targetStart, Object data[], int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data[i]);
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data[start + i]);
	}
	public static void fill(Column target, int targetStart, List<?> data, int start, int length) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, data.get(i));
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, data.get(start + i));
	}
	public static <T, V> void fill(Column target, int targetStart, T data[], int start, int length, Getter<T, V> getter) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, getter.get(data[i]));
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, getter.get(data[start + i]));
	}
	public static <T, V> void fill(Column target, int targetStart, T data[], int start, int length, Getter<T, V> getter, T nv) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, nv.equals(data[i]) ? null : getter.get(data[i]));
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, nv.equals(data[start + i]) ? null : getter.get(data[start + i]));
	}

	public static <T> void fill(Column target, int targetStart, List<? extends T> data, int start, int length, Getter<T, Object> getter) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		if (targetStart == 0 && start == 0)
			for (int i = 0; i < length; i++)
				table.setAt(i, pos, getter.get(data.get(i)));
		else
			for (int i = 0; i < length; i++)
				table.setAt(targetStart + i, pos, getter.get(data.get(start + i)));
	}

	public static long[] getLongs(Column target, long nv) {
		long[] r = new long[target.getTable().getSize()];
		getLongs(target, 0, r, 0, r.length, nv);
		return r;
	}
	public static void getLongs(Column target, int targetStart, long sink[], int start, int length, long nv) {
		Long nv2 = nv;
		final int pos = target.getLocation();
		Table table = target.getTable();
		for (int i = 0; i < length; i++)
			sink[start + i] = OH.noNull(table.getAt(targetStart + i, pos, Caster_Long.INSTANCE), nv2);
	}
	public static int[] getInts(Column target, int nv) {
		int[] r = new int[target.getTable().getSize()];
		getInts(target, 0, r, 0, r.length, nv);
		return r;
	}
	public static void getInts(Column target, int targetStart, int sink[], int start, int length, int nv) {
		Integer nv2 = nv;
		final int pos = target.getLocation();
		Table table = target.getTable();
		for (int i = 0; i < length; i++)
			sink[start + i] = OH.noNull(table.getAt(targetStart + i, pos, Caster_Integer.INSTANCE), nv2);
	}
	public static short[] getShorts(Column target, short nv) {
		short[] r = new short[target.getTable().getSize()];
		getShorts(target, 0, r, 0, r.length, nv);
		return r;
	}
	public static void getShorts(Column target, int targetStart, short sink[], int start, int length, short nv) {
		Short nv2 = nv;
		final int pos = target.getLocation();
		Table table = target.getTable();
		for (int i = 0; i < length; i++)
			sink[start + i] = OH.noNull(table.getAt(targetStart + i, pos, Caster_Short.INSTANCE), nv2);
	}
	public static byte[] getBytes(Column target, byte nv) {
		byte[] r = new byte[target.getTable().getSize()];
		getBytes(target, 0, r, 0, r.length, nv);
		return r;
	}
	public static void getBytes(Column target, int targetStart, byte sink[], int start, int length, byte nv) {
		Byte nv2 = nv;
		final int pos = target.getLocation();
		Table table = target.getTable();
		for (int i = 0; i < length; i++)
			sink[start + i] = OH.noNull(table.getAt(targetStart + i, pos, Caster_Byte.INSTANCE), nv2);
	}
	public static double[] getDoubles(Column target, double nv) {
		double[] r = new double[target.getTable().getSize()];
		getDoubles(target, 0, r, 0, r.length, nv);
		return r;
	}
	public static void getDoubles(Column target, int targetStart, double sink[], int start, int length, double nv) {
		Double nv2 = nv;
		final int pos = target.getLocation();
		Table table = target.getTable();
		for (int i = 0; i < length; i++)
			sink[start + i] = OH.noNull(table.getAt(targetStart + i, pos, Caster_Double.INSTANCE), nv2);
	}
	public static float[] getFloats(Column target, float nv) {
		float[] r = new float[target.getTable().getSize()];
		getFloats(target, 0, r, 0, r.length, nv);
		return r;
	}
	public static void getFloats(Column target, int targetStart, float sink[], int start, int length, float nv) {
		Float nv2 = nv;
		final int pos = target.getLocation();
		Table table = target.getTable();
		for (int i = 0; i < length; i++)
			sink[start + i] = OH.noNull(table.getAt(targetStart + i, pos, Caster_Float.INSTANCE), nv2);
	}
	public static boolean[] getBooleans(Column target, boolean nv) {
		boolean[] r = new boolean[target.getTable().getSize()];
		getBooleans(target, 0, r, 0, r.length, nv);
		return r;
	}
	public static void getBooleans(Column target, int targetStart, boolean sink[], int start, int length, boolean nv) {
		Boolean nv2 = nv;
		final int pos = target.getLocation();
		Table table = target.getTable();
		for (int i = 0; i < length; i++)
			sink[start + i] = OH.noNull(table.getAt(targetStart + i, pos, Caster_Boolean.INSTANCE), nv2);
	}
	public static char[] getChars(Column target, char nv) {
		char[] r = new char[target.getTable().getSize()];
		getChars(target, 0, r, 0, r.length, nv);
		return r;
	}
	public static void getChars(Column target, int targetStart, char sink[], int start, int length, char nv) {
		Character nv2 = nv;
		final int pos = target.getLocation();
		Table table = target.getTable();
		for (int i = 0; i < length; i++)
			sink[start + i] = OH.noNull(table.getAt(targetStart + i, pos, Caster_Character.INSTANCE), nv2);
	}
	public static <T> void getValues(Column target, int targetStart, T data[], int start, int length, Caster<T> caster) {
		final int pos = target.getLocation();
		Table table = target.getTable();
		for (int i = 0; i < length; i++)
			data[start + i] = table.getAt(targetStart + i, pos, caster);
	}
	public static <T> List<T> getValues(Column target, Caster<T> caster) {
		final int pos = target.getLocation();
		final Table table = target.getTable();
		int len = target.getTable().getSize();
		List<T> r = new ArrayList<T>(len);
		for (int i = 0; i < len; i++)
			r.add(table.getAt(i, pos, caster));
		return r;
	}

	public static String[] getColumnNamesArray(Table table) {
		String[] r = new String[table.getColumnsCount()];
		for (int i = 0; i < r.length; i++)
			r[i] = table.getColumnAt(i).getId();
		return r;
	}

	public static void addRowWithCast(Table table, Object[] elements) {
		int len = elements.length;
		Object[] casted = new Object[len];
		Caster<?> caster;
		for (int i = 0; i < len; i++) {
			caster = table.getColumnAt(i).getTypeCaster();
			casted[i] = caster.cast(elements[i]);
		}
		table.getRows().addRow(casted);
	}
}
