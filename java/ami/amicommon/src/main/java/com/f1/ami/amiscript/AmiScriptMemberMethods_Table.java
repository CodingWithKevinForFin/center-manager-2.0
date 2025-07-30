package com.f1.ami.amiscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CsvHelper;
import com.f1.utils.Formatter;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;

public class AmiScriptMemberMethods_Table extends AmiScriptBaseMemberMethods<Table> {

	private AmiScriptMemberMethods_Table() {
		super();

		addMethod(INIT);
		addMethod(INIT2);
		addMethod(INIT3);
		addMethod(INIT4);
		addMethod(ADD_ROW);
		addMethod(ADD_ROW2);
		addMethod(TO_CSV);
		addMethod(INSERT_ROW);
		addMethod(INSERT_ROW2);
		addMethod(GET_ROW);
		addMethod(GET_ROWS_COUNT);
		addMethod(GET_COLUMNS_COUNT);
		addMethod(TO_STRING);
		addMethod(TO_LIST);
		addMethod(TO_JSON);
		addMethod(TO_SET);
		addMethod(GET_VALUE);
		addMethod(SET_TITLE);
		addMethod(GET_TITLE, "title");
		addMethod(QUERY);
		addMethod(REMOVE_ROW);
		addMethod(ADD_COLUMN);
		addMethod(ALTER_COLUMN);
		addMethod(REMOVE_COLUMN);
		addMethod(GET_COLUMN_LOCATION);
		addMethod(GET_COLUMN_NAME_AT);
		addMethod(GET_COLUMN_TYPE);
		addMethod(GET_ROWS, "rows");
		addMethod(GET_COLUMN_NAMES, "columnNames");
		addMethod(GET_COLUMN_TYPES, "columnTypes");
		addMethod(ITERATOR);
		// addMethod(SHUFFLE);
		// addMethod(SHUFFLE2);
		addMethod(ISEQUAL);
	}

	private static final AmiAbstractMemberMethod<Table> GET_ROWS_COUNT = new AmiAbstractMemberMethod<Table>(Table.class, "getRowsCount", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRows().size();
		}
		@Override
		protected String getHelp() {
			return "Returns the number of rows in this table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> TO_CSV = new AmiAbstractMemberMethod<Table>(Table.class, "toCsv", String.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean incNames = Boolean.TRUE.equals(params[0]);
			Formatter[] columnFormatters = new Formatter[targetObject.getColumnsCount()];
			AH.fill(columnFormatters, AmiUtils.FORMATTER);
			return CsvHelper.toCsv(targetObject, incNames, new StringBuilder(), columnFormatters).toString();
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "includeColumnNames" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Should the csv's first row be the column names" };
		}
		@Override
		protected String getHelp() {
			return "Returns a comma seperated values formatter string of this table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> GET_COLUMNS_COUNT = new AmiAbstractMemberMethod<Table>(Table.class, "getColumnsCount", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColumnsCount();
		}
		@Override
		protected String getHelp() {
			return "Returns the number of columns in this table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> GET_ROW = new AmiAbstractMemberMethod<Table>(Table.class, "getRow", Row.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			// TODO ideally we should create a CopyOnWriteRow, which points to COWT
			targetObject.onModify();
			int i = (Integer) params[0];
			TableList rows = targetObject.getRows();
			if (i < 0)
				i = rows.size() + i;
			return OH.isBetween(i, 0, rows.size() - 1) ? rows.get(i) : null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "rowNum" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero-indexed row number, negative to start at end" };
		}
		@Override
		protected String getHelp() {
			return "Returns the row at the zero indexed location, or null.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> TO_STRING = new AmiAbstractMemberMethod<Table>(Table.class, "toString", String.class, true, String.class, String.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			String rowDelim = "\n";
			String colDelim = ",";
			if (params.length > 0)
				rowDelim = OH.noNull((String) params[0], rowDelim);
			if (params.length > 1)
				colDelim = OH.noNull((String) params[1], colDelim);
			boolean includeHeader = AH.contains("INCLUDE_HEADER", params, 2, params.length);
			boolean includeTypes = AH.contains("INCLUDE_TYPES", params, 2, params.length);
			boolean includeTitle = AH.contains("INCLUDE_TITLE", params, 2, params.length);
			boolean includeRows = !AH.contains("SKIP_ROWS", params, 2, params.length);

			StringBuilder sb = new StringBuilder();
			boolean first = true;
			if (includeTitle) {
				sb.append(targetObject.getTitle());
				sb.append(rowDelim);
			}
			if (includeHeader) {
				join(colDelim, '\\', TableHelper.getColumnIdsArray(targetObject), sb);
				sb.append(rowDelim);
			}
			if (includeTypes) {
				Class[] types = TableHelper.getColumnTypesArray(targetObject);
				Object[] typeNames = new Object[types.length];
				for (int i = 0; i < types.length; i++)
					typeNames[i] = types[i].getSimpleName();
				join(colDelim, '\\', typeNames, sb);
				sb.append(rowDelim);
			}
			if (includeRows) {
				for (Row r : targetObject.getRows()) {
					if (first)
						first = false;
					else
						sb.append(rowDelim);
					Object[] values = r.getValuesCloned();
					join(colDelim, '\\', values, sb);
				}
			}
			return sb.toString();
		}

		private void join(String colDelim, char c, Object[] values, StringBuilder sb) {
			if (colDelim.length() == 1)
				SH.joinWithEscape(colDelim.charAt(0), '\\', values, sb);
			else
				SH.join(colDelim, values, sb);
		}

		protected String[] buildParamNames() {
			return new String[] { "rowDelim", "colDelim", "options" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "delimiter to seperate rows, default is line return", "delimiter to seperate values, default is comma",
					"any combination of: INCLUDE_HEADER, INCLUDE_TYPES, INCLUDE_TITLE, SKIP_ROWS" };
		}
		@Override
		protected String getHelp() {
			return "Return a string representing this table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> TO_SET = new AmiAbstractMemberMethod<Table>(Table.class, "toSet", Set.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			Column column = targetObject.getColumnsMap().get(name);
			if (column == null)
				return null;
			Set<Object> values = new HashSet<Object>(targetObject.getSize());
			for (int i = 0; i < targetObject.getSize(); i++)
				values.add(column.getValue(i));
			return values;
		}
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of column to get values for" };
		}
		@Override
		protected String getHelp() {
			return "Returns a set of values for a particular column, or null if the column does not exist.  Empty tables will return an empty set.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> TO_LIST = new AmiAbstractMemberMethod<Table>(Table.class, "toList", List.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			Column column = targetObject.getColumnsMap().get(name);
			if (column == null)
				return null;
			List<Object> values = new ArrayList<Object>(targetObject.getSize());
			for (int i = 0; i < targetObject.getSize(); i++)
				values.add(column.getValue(i));
			return values;
		}
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of column to get values for" };
		}
		@Override
		protected String getHelp() {
			return "Return a list of values for a particular column, or null if the column does not exist.  Empty tables will return an empty list.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> TO_JSON = new AmiAbstractMemberMethod<Table>(Table.class, "toJson", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			return AmiScriptMemberMethods_Object.JSON_CONVERTER.objectToString(targetObject.getRows());
		}
		@Override
		protected String getHelp() {
			return "Returns a json of this table as a list of maps, where each map represents a row in the table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> GET_ROWS = new AmiAbstractMemberMethod<Table>(Table.class, "getRows", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			// TODO ideally we should create a CopyOnWriteRow, which points to COWT
			targetObject.onModify();
			return new ArrayList<Row>(targetObject.getRows());
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "Return a list of rows in this table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> GET_VALUE = new AmiAbstractMemberMethod<Table>(Table.class, "getValue", Object.class, Integer.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer pos = (Integer) params[0];
			String name = (String) params[1];
			int size = targetObject.getRows().size();
			if (pos < 0 || pos >= size)
				return null;
			if (!targetObject.getColumnIds().contains(name))
				return null;
			return targetObject.get(pos, name);
		}
		protected String[] buildParamNames() {
			return new String[] { "rowNum", "columnName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero indexed row number", "name of column" };
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with the given key and value. Returns null if column does not exist or row number is out of bounds.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> GET_TITLE = new AmiAbstractMemberMethod<Table>(Table.class, "getTitle", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTitle();
		}
		@Override
		protected String getHelp() {
			return "Returns the title of this table, or null if no title.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> SHUFFLE = new AmiAbstractMemberMethod<Table>(Table.class, "shuffle", Table.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			TableHelper.shuffle(targetObject.getRows(), MH.RANDOM);
			return targetObject;
		}
		@Override
		protected String getHelp() {
			return "Shuffles the rows randomly.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<Table> SHUFFLE2 = new AmiAbstractMemberMethod<Table>(Table.class, "shuffle", Table.class, Random.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			TableHelper.shuffle(targetObject.getRows(), (Random) params[0]);
			return targetObject;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "random" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "random" };
		}
		@Override
		protected String getHelp() {
			return "Shuffles the rows randonly given the provided Random";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Table> SET_TITLE = new AmiAbstractMemberMethod<Table>(Table.class, "setTitle", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			String r = targetObject.getTitle();
			targetObject.setTitle((String) params[0]);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "titleName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "new title name" };
		}
		@Override
		protected String getHelp() {
			return "Sets the title of this table with the given string.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Table> QUERY = new AmiAbstractMemberMethod<Table>(Table.class, "query", Table.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(sf);
			String sql = (String) params[0];
			Tableset tableset = new TablesetImpl();
			tableset.putTable("this", targetObject);
			AmiWebChildCalcFrameStack t;
			t = new AmiWebChildCalcFrameStack(caller, sf, EmptyCalcFrame.INSTANCE, EmptyCalcFrame.INSTANCE, tableset, targetObject, null, sf.getLimit(), null,
					ei == null ? null : ei.getDefaultDatasource());
			//			ChildCalcFrameStack t = new ChildCalcFrameStack(caller, true, sf, EmptyCalcFrame.INSTANCE);
			DerivedCellCalculatorExpression c;

			AmiService service = getService(sf);
			try {
				c = service.getSqlProcessor().toCalc(sql, t);
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode(null, sql);
				e.addFrame(null);
				throw e;
			} catch (ExpressionParserException e) {
				throw new FlowControlThrow(null, "Compiler Error: " + e.getMessage(), e);
			} catch (Exception e) {
				throw new FlowControlThrow(null, "Internal Error", e);
			}
			try {
				final Object o;
				o = c.get(t);
				if (o instanceof TableReturn) {
					TableReturn tr = (TableReturn) o;
					List<Table> tables = tr.getTables();
					if (tables.size() == 1)
						return tables.get(0);
				}
				return null;
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode(null, sql);
				e.addFrame(null);
				throw e;
			} catch (ExpressionParserException e) {
				throw new FlowControlThrow(null, "Runtime Error: " + e.getMessage(), e);
			} catch (Exception e) {
				throw new FlowControlThrow(null, "Internal Error", e);
			}
		}
		private long getNow() {
			return 0;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "amiSql" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "amiSql to run" };
		}
		@Override
		protected String getHelp() {
			return "Runs the given amisql on this table. Use 'this' to reference this table in the amisql. Returns the resulting table.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		//		@Override
		//		public Object resumeMethod(Map lcvs, Table target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
		//			try {
		//				Object o = paused.getNext().resume();
		//				return o instanceof Table ? o : null;
		//			} catch (FlowControlThrow e) {
		//				e.getTailFrame().setOriginalSourceCode(null, (String) params[0]);
		//				e.addFrame(caller);
		//				throw e;
		//			} catch (ExpressionParserException e) {
		//				throw new FlowControlThrow(caller, "Runtime Error: " + e.getMessage(), e);
		//			} catch (Exception e) {
		//				throw new FlowControlThrow(caller, "Internal Error", e);
		//			}
		//		}
	};
	private static final AmiAbstractMemberMethod<Table> INIT = new AmiAbstractMemberMethod<Table>(Table.class, null, Object.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			ColumnarTable r = new ColumnarTable();
			r.setTitle((String) params[0]);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "title" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "title name" };
		}
		@Override
		protected String getHelp() {
			return "Creates a new table given the title.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> INIT2 = new AmiAbstractMemberMethod<Table>(Table.class, null, Object.class, false, String.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			ColumnarTable r = new ColumnarTable();
			r.setTitle((String) params[0]);
			Map<Object, Object> values = (Map) params[1];
			if (CH.isntEmpty(values)) {
				int len = -1;
				for (Entry<Object, Object> i : values.entrySet()) {
					if (!(i.getKey() instanceof String))
						throw new RuntimeException("All keys must be strings");
					if (!(i.getValue() instanceof Collection))
						throw new RuntimeException("All values must be Lists");
					int len2 = ((Collection) i.getValue()).size();
					if (len == -1)
						len = len2;
					else if (len != len2)
						throw new RuntimeException("List size mismatch: " + len + " != " + len2);
				}
				Iterator[] iterators = new Iterator[values.size()];
				int pos = 0;
				for (Entry<Object, Object> i : values.entrySet()) {
					Iterable list = (Iterable) i.getValue();
					Class type = OH.getWidestIgnoreNull(list);
					if (type == null)
						type = Object.class;
					iterators[pos++] = list.iterator();
					r.addColumn(type, (String) i.getKey());
				}
				for (int i = 0; i < len; i++) {
					Row row = r.newEmptyRow();
					for (int j = 0; j < iterators.length; j++)
						row.putAt(j, iterators[j].next());
					r.getRows().add(row);
				}
			}
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "title", "map_of_lists" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "title name", "Map of lists where key will be the column name and value is a list of values, all lists must be same length" };
		}
		@Override
		protected String getHelp() {
			return "Creates a new table given the title and a map of lists.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> INIT3 = new AmiAbstractMemberMethod<Table>(Table.class, null, Object.class, false, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			String title = (String) params[0];
			String columns = (String) params[1];
			if (title == null || columns == null)
				return null;
			StringCharReader cr = new StringCharReader(columns);
			cr.setToStringIncludesLocation(true);
			StringBuilder sink = new StringBuilder();
			List<String> names = new ArrayList<String>();
			List<Class> types = new ArrayList<Class>();
			for (;;) {
				if (cr.isEof())
					break;
				cr.skip(StringCharReader.WHITE_SPACE);
				if (cr.expectNoThrow('`')) {
					cr.readUntilSkipEscaped('`', '\\', sink);
					cr.expect('`');
				} else
					cr.readUntilAny(StringCharReader.WHITE_SPACE, true, sink);
				names.add(SH.toStringAndClear(sink));
				cr.skip(StringCharReader.WHITE_SPACE);
				cr.readUntilAny(StringCharReader.WHITE_SPACE_COMMA, true, sink);
				String type = SH.toStringAndClear(sink);
				if (SH.isnt(type))
					throw new RuntimeException("Column type missing at position " + cr.getCountRead());
				try {
					Class<?> forName = sf.getFactory().forName(type);
					types.add(forName);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Column type invalid: '" + type + "' at position " + cr.getCountRead(), e);
				}
				cr.skip(StringCharReader.WHITE_SPACE);
				if (cr.isEof())
					break;
				cr.expect(',');
			}
			ColumnarTable r = new ColumnarTable(AH.toArray(types, Class.class), AH.toArray(names, String.class));
			r.setTitle(title);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "title", "columnsDefition" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "title name", "name type,name type,name type..." };
		}
		@Override
		protected String getHelp() {
			return "Creates a new table given the title and the column definitions.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> INIT4 = new AmiAbstractMemberMethod<Table>(Table.class, null, Object.class, false, Table.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			Table t = (Table) params[0];
			if (t != null)
				return new ColumnarTable(t);
			return new ColumnarTable();
		}
		protected String[] buildParamNames() {
			return new String[] { "table" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "table to copy" };
		}
		@Override
		protected String getHelp() {
			return "Copy constructor for creating a new table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> GET_COLUMN_LOCATION = new AmiAbstractMemberMethod<Table>(Table.class, "getColumnLocation", Integer.class, false,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			Column col = targetObject.getColumnsMap().get(params[0]);
			return col == null ? -1 : col.getLocation();
		}
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "column name" };
		}
		@Override
		protected String getHelp() {
			return "Returns 0-indexed column position, or -1 if column does not exist in the table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> REMOVE_COLUMN = new AmiAbstractMemberMethod<Table>(Table.class, "removeColumn", Boolean.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			Column col = targetObject.getColumnsMap().get(params[0]);
			if (col == null)
				return false;
			targetObject.removeColumn(col.getLocation());
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of column to remove" };
		}
		@Override
		protected String getHelp() {
			return "Removes a column by specifying the its name. Returns true if the table changed as a result.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Table> ADD_COLUMN = new AmiAbstractMemberMethod<Table>(Table.class, "addColumn", Object.class, false, String.class, String.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			String id = (String) params[0];
			Class clazz;
			clazz = sf.getFactory().forNameNoThrow((String) params[1]);
			if (clazz == null)
				clazz = String.class;
			Object dflt = OH.getCaster(clazz).cast(params[2], false, false);
			id = SH.getNextId(id, (Set) targetObject.getColumnIds(), 2);
			targetObject.addColumn(clazz, id, dflt);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "columnName", "columnType", "DefaultValue" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "columnName", "columnType", "default value for existing columns or null" };
		}
		@Override
		protected String getHelp() {
			return "Adds a new columns with the specified name, type and default value.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Table> ALTER_COLUMN = new AmiAbstractMemberMethod<Table>(Table.class, "alterColumn", String.class, false, String.class,
			String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			String currentColumn = (String) params[0];
			String newName = (String) params[1];
			String type = (String) params[2];
			Map<String, Column> columnsMap = targetObject.getColumnsMap();
			Column existing = columnsMap.get(currentColumn);
			if (existing == null)
				return "current Column not found";
			Class clazz = sf.getFactory().forNameNoThrow(type);
			if (clazz == null)
				return "Type invalid";
			if (OH.eq(currentColumn, newName)) {
				if (existing.getType() == clazz)
					return null;
			} else if (columnsMap.containsKey(newName))
				return "New Column name already exists";
			// need to make sure getRows return the copied rows
			targetObject.onModify();
			final int location = existing.getLocation();
			final Object[] vals = new Object[targetObject.getSize()];
			TableList rows = targetObject.getRows();
			for (int i = 0; i < vals.length; i++)
				vals[i] = rows.get(i).getAt(location);

			targetObject.removeColumn(existing.getLocation());
			Column col2 = targetObject.addColumn(location, clazz, newName, null);
			Caster<?> caster = col2.getTypeCaster();
			for (int i = 0; i < vals.length; i++)
				rows.get(i).putAt(location, caster.castNoThrow(vals[i]));
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "currentColumnName", "newColumnName", "newTypeOrNull" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of existing column to alter", "the new name of the column", "The type of the column, if null then the type is not changed" };
		}
		@Override
		protected String getHelp() {
			return "Changes the column's name and/or type. Returns null on success or error message on failure. When changing the type, auto-casting will be used.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Table> GET_COLUMN_NAME_AT = new AmiAbstractMemberMethod<Table>(Table.class, "getColumnNameAt", String.class, false,
			Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer pos = (Integer) params[0];
			if (pos == null || pos < 0 || pos >= targetObject.getColumnsCount())
				return null;
			return (String) targetObject.getColumnAt(pos.intValue()).getId();
		}
		protected String[] buildParamNames() {
			return new String[] { "columnPos" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero column position" };
		}
		@Override
		protected String getHelp() {
			return "Returns name of column, or null if the supplied index is outside of the column count.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> GET_COLUMN_TYPE = new AmiAbstractMemberMethod<Table>(Table.class, "getColumnType", String.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			Column col = targetObject.getColumnsMap().get(params[0]);
			MethodFactoryManager methodFactory = sf.getFactory();
			return col == null ? null : methodFactory.forType(col.getType());
		}
		protected String[] buildParamNames() {
			return new String[] { "columnName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the column to get the type for" };
		}
		@Override
		protected String getHelp() {
			return "Returns the type of a column, or null if column doesn't exist.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> ITERATOR = new AmiAbstractMemberMethod<Table>(Table.class, "iterator", Iterator.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRows().iterator();
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "Returns an iterator of Row objects.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> ADD_ROW = new AmiAbstractMemberMethod<Table>(Table.class, "addRow", Row.class, true, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.onModify();
			params = toArraySafe(targetObject, params);
			Row row = targetObject.getRows().addRow(params);
			return row;
		}
		protected String[] buildParamNames() {
			return new String[] { "params" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "object that contains the data" };
		}
		@Override
		protected String getHelp() {
			return "Adds and returns the new row given an object that contains the row data.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Table> ADD_ROW2 = new AmiAbstractMemberMethod<Table>(Table.class, "addRow", Row.class, false, List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			List l = (List) params[0];
			if (l == null)
				return null;
			targetObject.onModify();
			Object[] values = toArraySafe(targetObject, l.toArray());
			Row row = targetObject.getRows().addRow(values);
			return row;
		}
		protected String[] buildParamNames() {
			return new String[] { "list" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "list of params" };
		}
		@Override
		protected String getHelp() {
			return "Adds and returns the new row given a list of row data.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Table> REMOVE_ROW = new AmiAbstractMemberMethod<Table>(Table.class, "removeRow", Object.class, false, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			int rowPos = (Integer) params[0];
			targetObject.onModify();
			Row row = targetObject.getRows().remove(rowPos);
			return row;
		}
		protected String[] buildParamNames() {
			return new String[] { "rowPosition" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "0-index row position" };
		}
		@Override
		protected String getHelp() {
			return "Removes and returns the row given its index position.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Table> INSERT_ROW = new AmiAbstractMemberMethod<Table>(Table.class, "insertRow", Object.class, true, Integer.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			int rowPos = (Integer) params[0];
			params = AH.remove(params, 0);
			targetObject.onModify();
			params = toArraySafe(targetObject, params);
			Row row = targetObject.getRows().insertRow(rowPos, params);
			return row;
		}
		protected String[] buildParamNames() {
			return new String[] { "rowPosition", "rowValueList" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "0-indexed row position", "row values, each as a parameter" };
		}
		@Override
		protected String getHelp() {
			return "Inserts and returns a new row given its position and its row values, each as a parameter.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Table> INSERT_ROW2 = new AmiAbstractMemberMethod<Table>(Table.class, "insertRow", Object.class, false, Integer.class, List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			int rowPos = (Integer) params[0];
			List l = (List) params[1];
			if (l == null)
				return null;
			Object[] values = l.toArray();
			targetObject.onModify();
			params = toArraySafe(targetObject, values);
			Row row = targetObject.getRows().insertRow(rowPos, params);
			return row;
		}
		protected String[] buildParamNames() {
			return new String[] { "rowPosition", "values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "0-indexed row position", "a list that contains the values" };
		}
		@Override
		protected String getHelp() {
			return "Inserts and returns the new row given its position and a list of values.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Table";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents a 2-dimensional data structure with a predefined set of uniquely named, ordered columns.  It also has a variant number of rows (see Row).";
	}

	private static final AmiAbstractMemberMethod<Table> GET_COLUMN_NAMES = new AmiAbstractMemberMethod<Table>(Table.class, "getColumnNames", List.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			int n = targetObject.getColumnsCount();
			ArrayList<String> r = new ArrayList<String>(n);
			for (int i = 0; i < n; i++)
				r.add((String) targetObject.getColumnAt(i).getId());
			return r;
		}
		@Override
		protected String getHelp() {
			return "Return a list of the names of the columns, ordered from left to right.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Table> GET_COLUMN_TYPES = new AmiAbstractMemberMethod<Table>(Table.class, "getColumnTypes", Map.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			int n = targetObject.getColumnsCount();
			HashMap<String, String> r = new LinkedHashMap<String, String>(n);
			for (int i = 0; i < n; i++) {
				Column col = targetObject.getColumnAt(i);
				String type = sf.getFactory().forType(col.getType());
				r.put((String) col.getId(), type);
			}
			return r;
		}
		@Override
		protected String getHelp() {
			return "Returns a map of the name of the columns, ordered from left to right.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<Table> ISEQUAL = new AmiAbstractMemberMethod<Table>(Table.class, "isEqual", Boolean.class, false, Table.class, Boolean.class,
			Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Table targetObject, Object[] params, DerivedCellCalculator caller) {
			Table tableToCompare = (Table) params[0];
			boolean ignoreRowOrdering = Boolean.TRUE.equals(params[1]);
			boolean ignoreColumnOrdering = Boolean.TRUE.equals(params[2]);
			// step1: EARLY TERIMINATING CHECK::check for rowCnt and ColCnt, if any not matching, return false
			if (!OH.eq(targetObject.getColumnsCount(), tableToCompare.getColumnsCount()) || !OH.eq(targetObject.getRows().size(), tableToCompare.getRows().size()))
				return false;
			// step2: check schema
			// (1).EARLY TERIMINATING CHECK::first check the schema set is the same(ignore ordering)
			Map sourceSchema = TableHelper.getColumnTypes(targetObject).getTypes();
			Map targetSchema = TableHelper.getColumnTypes(tableToCompare).getTypes();
			boolean isSchemaSameIgnoreOrdering = sourceSchema.equals(targetSchema);
			if (!isSchemaSameIgnoreOrdering)
				return false;
			// (2).cases where ignoreColOrdering is true
			if (ignoreColumnOrdering) {
				if (ignoreRowOrdering) {
					List<Map<Object, Object>> l_src = TableHelper.toListOfMaps(targetObject);
					List<Map<Object, Object>> l_tgt = TableHelper.toListOfMaps(tableToCompare);
					return new HashSet<Map<?, ?>>(l_src).equals(new HashSet<Map<?, ?>>(l_tgt));// turn ArrayList into Hashset to disregard row ordering
				} else {// row ordering matters, use arrayList to enforce rowOrdering  for src table
					Set colDataSet_src = new HashSet<List<String>>();
					for (Column col : targetObject.getColumns()) {
						List<String> valList = TableHelper.getValues(col, Caster_String.INSTANCE);
						colDataSet_src.add(valList);
					}
					// for tgt table
					Set colDataSet_tgt = new HashSet<List<String>>();
					for (Column col : tableToCompare.getColumns()) {
						List<String> valList = TableHelper.getValues(col, Caster_String.INSTANCE);
						colDataSet_tgt.add(valList);
					}
					return colDataSet_src.equals(colDataSet_tgt);
				}
			} else {// col ordering matters
				if (ignoreRowOrdering) {// col ordering matters, row ordering does not.For instance: m1 = {[europe,uk,20]:2,[europe,germany,30:1]]}, m2 = {[uk,europe,20]:2,[germany,europe,30:1]]}for src table
					Map rowToRowCntMapping_src = new HashMap<String, Integer>();
					for (Row row : targetObject.getRows()) {
						String rowStr = row.toString();
						int cntForTheRow = (int) (rowToRowCntMapping_src.containsKey(rowStr) ? rowToRowCntMapping_src.get(rowStr) : 0);
						rowToRowCntMapping_src.put(rowStr, cntForTheRow + 1);
						;
					}
					// for tgt table
					Map rowToRowCntMapping_tgt = new HashMap<String, Integer>();
					for (Row row : tableToCompare.getRows()) {
						String rowStr = row.toString();
						int cntForTheRow = (int) (rowToRowCntMapping_tgt.containsKey(rowStr) ? rowToRowCntMapping_tgt.get(rowStr) : 0);
						rowToRowCntMapping_tgt.put(rowStr, cntForTheRow + 1);
						;
					}
					if (rowToRowCntMapping_src.equals(rowToRowCntMapping_tgt))
						return true;
					return false;
				} else {// both col,row ordering matters
					String targetObj_tableStrExcludingTitle = TableHelper.toString(targetObject, "", TableHelper.SHOW_ALL ^ TableHelper.SHOW_TITLE);
					String tableToCompare_tableStrExcludingTitle = TableHelper.toString(tableToCompare, "", TableHelper.SHOW_ALL ^ TableHelper.SHOW_TITLE);
					boolean isTableStrictlyEquivalentExcludingTitle = OH.eq(targetObj_tableStrExcludingTitle, tableToCompare_tableStrExcludingTitle);
					return isTableStrictlyEquivalentExcludingTitle;
				}
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "tableToCompare", "ignoreRowOrdering", "ignoreColumnOrdering" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "table to compare with", "whether to ignore row ordering", "whether to ignore column ordering" };
		}
		@Override
		protected String getHelp() {
			return "Returns the boolean flag whether two tables are considered equivalent based on the method aruments. "
					+ "Note that if two tables have different row counts or different schemas (column name + column type), this method will return false immediately";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public Class<Table> getVarType() {
		return Table.class;
	}
	@Override
	public Class<? extends Table> getVarDefaultImpl() {
		return ColumnarTable.class;
	}
	static private Object[] toArraySafe(Table targetObject, Object[] params) {
		params = Arrays.copyOf(params, targetObject.getColumnsCount());
		for (int i = 0; i < params.length; i++)
			params[i] = targetObject.getColumnAt(i).getTypeCaster().cast(params[i]);
		return params;
	}

	public static AmiScriptMemberMethods_Table INSTANCE = new AmiScriptMemberMethods_Table();
}
