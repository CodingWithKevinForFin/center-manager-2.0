package com.f1.utils.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Bytes;
import com.f1.base.CalcFrame;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Pointer;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.CH;
import com.f1.utils.CastGetter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.concurrent.UnsafeHelper;
import com.f1.utils.sql.SqlProjector.TempIndex;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.ColumnPositionMapping;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarColumnObject;
import com.f1.utils.structs.table.columnar.ColumnarColumnObject_Cached;
import com.f1.utils.structs.table.columnar.ColumnarColumnPrimitive;
import com.f1.utils.structs.table.columnar.ColumnarColumnString_BitMap;
import com.f1.utils.structs.table.columnar.ColumnarColumnString_CompactAscii;
import com.f1.utils.structs.table.columnar.ColumnarColumnString_CompactChars;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class SqlProcessorTableMutatorImpl implements SqlProcessorTableMutator {

	private final SqlProcessor owner;

	public SqlProcessorTableMutatorImpl(SqlProcessor processor) {
		this.owner = processor;
	}

	//add a getter for owner
	public SqlProcessor getOwner() {
		return this.owner;
	}
	@Override
	public Table processTableRemove(CalcFrameStack sf, String name, int pos, int scope, boolean ifExists) {
		final Table r = sf.getTableset().removeTable(name);
		if (r == null && !ifExists)
			throw new ExpressionParserException(pos, "Table not found: " + name);
		return r;
	}

	@Override
	public Table processTableRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope) {
		Table r = sf.getTableset().removeTable(from);
		if (r == null)
			throw new ExpressionParserException(fromPos, "Table not found: " + from);
		r.onModify();
		r.setTitle(to);
		sf.getTableset().putTable(to, r);
		return r;
	}

	@Override
	public Table processColumnAdd(CalcFrameStack sf, int tableNamePos, String tableName, int typePosition, String type, String varname, int colPos, int scope,
			Map<String, Node> options, Object[] vals) {
		Table r = getTable(sf, tableNamePos, tableName, scope);
		r.onModify();
		Column col = r.addColumn(colPos, parseType(sf, typePosition, type), varname, null);
		if (vals != null) {
			final Caster<?> typeCaster = col.getTypeCaster();
			int y = 0;
			for (Row row : r.getRows()) {
				row.putAt(colPos, typeCaster.cast(vals[y++], false, false));
			}
		}
		return r;
	}
	@Override
	public Table processColumnRemove(CalcFrameStack sf, int tableNamePos, String tableName, String colname, int colnamePos, int scope) {
		Table r = getTable(sf, tableNamePos, tableName, scope);
		r.onModify();
		r.removeColumn(colname);
		return r;
	}

	@Override
	public Table processColumnChangeType(CalcFrameStack sf, int tableNamePos, String tableName, int location, Class<?> type, int typePosition, String newTypeName, String newName,
			Map<String, Node> options, int scope) {

		Table r = getTable(sf, tableNamePos, tableName, scope);
		r.onModify();
		if (newTypeName != null) {
			Class<?> newType = parseType(sf, typePosition, newTypeName);
			r.setColumnType(location, type, newType, new CastGetter(newType));
		}
		if (newName != null)
			r.renameColumn(location, newName);
		return r;
	}

	@Override
	public TableReturn processRowAdds(CalcFrameStack sf, Table table, int tableNamePos, int positions[], Object[][] rows, boolean returnGeneratedId) {
		table.onModify();
		TableList rows2 = table.getRows();
		for (Object[] row : rows) {
			rows2.addRow(row);
		}
		return new TableReturn(rows.length);
	}

	@Override
	public TableReturn processRowAdds(CalcFrameStack sf, Table table, int tableNamePos, ColumnPositionMapping pos, int startRow, int rowsCount, Table values,
			boolean returnGeneratedIds) {
		table.onModify();
		TableList rows2 = table.getRows();
		TableList rows = values.getRows();
		final int colsCount = pos.getPosCount();
		Caster[] casters = new Caster[colsCount];
		for (int i = 0; i < colsCount; i++)
			casters[i] = table.getColumnAt(i).getTypeCaster();
		for (int i = 0; i < rowsCount; i++) {
			Row row = rows.get(startRow + i);
			Row row2 = table.newEmptyRow();
			if (pos.isStraight())
				for (int j = 0; j < colsCount; j++)
					row2.putAt(j, casters[j].cast(row.getAt(j), false, false));
			else
				for (int j = 0; j < colsCount; j++)
					row2.putAt(pos.getTargetPosAt(j), casters[j].cast(row.getAt(pos.getSourcePosAt(j)), false, false));
			rows2.addRow(row2);
		}
		return new TableReturn(values.getSize());
	}

	@Override
	public int processRowRemoves(CalcFrameStack sf, Table table, List<Row> toDelete) {
		if (table.onModify()) //this means the passed in rows point to an old table and we need to grab the rows from the copied table
			toDelete = getRowsFromCopy(table, toDelete);
		table.getRows().removeAll(toDelete);
		return toDelete.size();
	}

	@Override
	public int processRowRemoveAll(CalcFrameStack sf, Table table) {
		table.onModify();
		int r = table.getSize();
		table.clear();
		return r;
	}

	@Override
	public int processRowUpdate(CalcFrameStack sf, Table table, int tableNamePos, List<Row> toUpdate, int[] positions, Object[][] values) {
		int r = 0;
		if (table.onModify()) //this means the passed in rows point to an old table and we need to grab the rows from the copied table
			toUpdate = getRowsFromCopy(table, toUpdate);
		for (int n = 0, l = toUpdate.size(); n < l; n++) {
			Row row = toUpdate.get(n);
			Object[] v = values[n];
			boolean change = false;
			for (int i = 0; i < v.length; i++) {
				if (OH.ne(row.getAt(positions[i]), v[i])) {
					change = true;
					row.putAt(positions[i], v[i]);
				}
			}
			if (change)
				r++;
		}
		return r;
	}

	static private List<Row> getRowsFromCopy(Table table, List<Row> rows) {
		rows = new ArrayList<Row>(rows);
		for (int i = 0, s = rows.size(); i < s; i++)
			rows.set(i, table.getRow(rows.get(i).getLocation()));
		return rows;
	}

	@Override
	public Table processTableAdd(CalcFrameStack sf, String name, int namePos, String[] types, String[] names, Map<String, Node>[] colOptions, int[] colDefPos,
			Map<String, Node> useOptions, int scope, boolean ifNotExists) {
		if (ifNotExists && sf.getTableset().getTableNames().contains(name))
			return null;
		if (CH.isntEmpty(useOptions)) {
			Node first = CH.first(useOptions.values());
			throw new ExpressionParserException(first.getPosition(), "USE options not supported: " + CH.first(useOptions.keySet()));
		}
		for (int i = 0; i < colOptions.length; i++) {
			if (CH.isntEmpty(colOptions[i]))
				throw new ExpressionParserException(colDefPos[i], "Option not supported: " + colOptions[i]);
		}
		Class<?>[] typesClass = new Class[types.length];
		for (int i = 0; i < types.length; i++)
			typesClass[i] = parseType(sf, colDefPos[i], types[i]);
		Table r = SqlProjector.newBasicTable(typesClass, names);
		r.setTitle(name);
		sf.getTableset().putTable(r.getTitle(), r);
		return r;
	}

	@Override
	public void processIndexRemove(CalcFrameStack sf, String tableName, int tableNamePos, String indexName, int indexNamePos, boolean ifExists, int scope) {
		throw new ExpressionParserException(tableNamePos, "DROP INDEX not supported");
	}

	@Override
	public void processIndexCreate(CalcFrameStack sf, String idxName, int idxNamePos, String tableName, int tableNamePos, String[] colName, String[] colType, int[] colPos,
			Map<String, Node> useOptions, boolean ifNotExists, int scope) {
		throw new ExpressionParserException(idxNamePos, "CREATE INDEX not supported");
	}

	@Override
	public List<Row> applyIndexes(CalcFrameStack sf, String astable, Table table, Pointer<DerivedCellCalculator> whereClause, int limit) {
		return table.getRows();
	}

	@Override
	public void processTriggerCreate(CalcFrameStack sf, String triggerName, int triggerNamePos, String typeName, int typeNamePos, String tableName[], int tableNamePos[],
			int priority, Map<String, Node> useOptions, boolean ifNotExists) {
		throw new ExpressionParserException(triggerNamePos, "CREATE TRIGGER not supported");
	}

	@Override
	public void processTriggerRemove(CalcFrameStack sf, String tableName, int tableNamePos, String triggerName, int triggerNamePos, boolean ifExists) {
		throw new ExpressionParserException(triggerNamePos, "DROP TRIGGER not supported");
	}

	@Override
	public void processTimerRemove(CalcFrameStack sf, String timerName, int timerNamePos, boolean ifExists) {
		throw new ExpressionParserException(timerNamePos, "DROP TIMER not supported");

	}

	@Override
	public void processTimerCreate(CalcFrameStack sf, String timerName, int timerNamePos, String typeName, int typeNamePos, int priority, String on, int onPos,
			Map<String, Node> useOptions, boolean ifNotExists) {
		throw new ExpressionParserException(timerNamePos, "CREATE TIMER not supported");

	}

	@Override
	public void processProcedureCreate(CalcFrameStack sf, String procedureName, int procedureNamePos, String typeName, int typeNamePos, Map<String, Node> useOptions,
			boolean ifNotExists) {
		throw new ExpressionParserException(procedureNamePos, "CREATE PROCEDURE not supported");
	}

	@Override
	public void processProcedureRemove(CalcFrameStack sf, String procedureName, int procedureNamePos, boolean ifExists) {
		throw new ExpressionParserException(procedureNamePos, "DROP PROCEDURE not supported");
	}

	@Override
	public TableReturn processCallProcedure(CalcFrameStack sf, String name, int namePos, Object[] params, int[] paramsPos, int limitOffset, int limit) {
		throw new ExpressionParserException(namePos, "CALL not supported");
	}

	@Override
	public String processDescribe(CalcFrameStack sf, int type, int scope, String name, int namePos, String on, int onPos, String from, int fromPos, MethodNode mn) {
		if (from != null)
			throw new ExpressionParserException(fromPos, "Unexepcted Token FROM: " + from);
		StringBuilder r = new StringBuilder();
		switch (type) {
			case SqlExpressionParser.ID_TABLE: {
				Table table = getTable(sf, namePos, name, scope);
				r.append("CREATE TABLE ");
				if (owner.getExpressionParser().isValidVarName(name))
					r.append(name);
				else
					SH.quote('`', name, r);
				r.append(" (");
				for (int i = 0; i < table.getColumnsCount(); i++) {
					Column col = table.getColumnAt(i);
					if (i != 0)
						r.append(',');
					if (owner.getExpressionParser().isValidVarName((String) col.getId()))
						r.append(col.getId());
					else
						SH.quote('`', (String) col.getId(), r);
					r.append(' ');
					r.append(sf.getFactory().forType(col.getType()));
				}
				r.append(")");
				break;
			}
			default:
				throw new ExpressionParserException(namePos, "DESCRIBE " + SqlExpressionParser.toOperationString(type) + " not defined");
		}
		return r.toString();
	}

	@Override
	public Table processShow(CalcFrameStack sf, String targetType, int targetTypePos, int scope, boolean full, String name, int namePos, String from, int fromPos, MethodNode mn) {
		CalcFrame globalVars = sf.getGlobal();
		Tableset tables = sf.getTableset();
		if (from != null)
			throw new ExpressionParserException(fromPos, "FROM not supported");
		if ("TABLES".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			BasicTable r = new BasicTable(String.class, "TableName", Integer.class, "RowCount", Integer.class, "ColumnsCount", String.class, "Scope");
			r.setTitle("TABLES");
			for (String s : tables.getTableNamesSorted()) {
				Table table = tables.getTable(s);
				r.getRows().addRow(table.getTitle(), table.getSize(), table.getColumnsCount(), "TEMPORARY");
			}
			for (String key : globalVars.getVarKeys()) {
				Object value = globalVars.getValue(key);
				if (value instanceof Table) {
					Table table = (Table) value;
					r.getRows().addRow(table.getTitle(), table.getSize(), table.getColumnsCount(), "VARIABLE");
				}
			}
			return r;
		} else {
			MethodFactoryManager mf = sf.getFactory();
			if ("COLUMNS".equalsIgnoreCase(targetType)) {
				if (name != null)
					throw new ExpressionParserException(namePos, "Unexpected token: " + name);
				BasicTable r = new BasicTable(String.class, "TableName", String.class, "ColumnName", String.class, "Type");
				r.setTitle("COLUMNS");
				for (String s : tables.getTableNamesSorted()) {
					Table table = tables.getTable(s);
					for (Column c : table.getColumns()) {
						String type = mf.forType(c.getType());
						r.getRows().addRow(table.getTitle(), (String) c.getId(), type);
					}
				}
				return r;
			} else if ("TABLE".equalsIgnoreCase(targetType)) {
				if (name == null)
					throw new ExpressionParserException(targetTypePos, "SHOW TABLE Expecting table name");
				Table table = getTable(sf, namePos, name, scope);
				BasicTable r = new BasicTable(String.class, "ColumnName", String.class, "Type", Integer.class, "Position");
				r.setTitle("COLUMNS");
				int n = 0;
				for (Column c : table.getColumns()) {
					String type = mf.forType(c.getType());
					r.getRows().addRow((String) c.getId(), type, n++);
				}
				return r;
			} else if ("METHODS".equalsIgnoreCase(targetType)) {
				List<MethodFactory> sink = new ArrayList<MethodFactory>();
				mf.getAllMethodFactories(sink);
				List<DerivedCellMemberMethod<Object>> sink2 = new ArrayList<DerivedCellMemberMethod<Object>>();
				mf.getMemberMethods(null, null, sink2);
				BasicTable r = new BasicTable(String.class, "TargetType", String.class, "MethodName", String.class, "ReturnType", String.class, "Arguments");
				r.setTitle("METHODS");
				StringBuilder sb = new StringBuilder();
				for (MethodFactory i : sink) {
					ParamsDefinition definition = i.getDefinition();
					for (int n = 0; n < definition.getParamsCount(); n++) {
						if (n > 0)
							sb.append(", ");
						sb.append(mf.forType(definition.getParamType(n))).append(' ').append(definition.getParamName(n));
					}
					r.getRows().addRow(null, definition.getMethodName(), definition.getReturnType() == null ? "Object" : mf.forType(definition.getReturnType()),
							SH.toStringAndClear(sb));
				}
				for (DerivedCellMemberMethod<Object> i : sink2) {
					for (int n = 0; n < i.getParamNames().length; n++) {
						if (n > 0)
							sb.append(", ");
						sb.append(mf.forType(n == i.getParamTypes().length ? i.getVarArgType() : i.getParamTypes()[n])).append(' ').append(i.getParamNames()[n]);

					}
					r.getRows().addRow(mf.forType(i.getTargetType()), i.getMethodName() == null ? "<constructor>" : i.getMethodName(),
							i.getReturnType() == null ? "Object" : mf.forType(i.getReturnType()), SH.toStringAndClear(sb));
				}
				TableHelper.sort(r, "TargetType", "MethodName");
				return r;
			} else if ("VARS".equalsIgnoreCase(targetType)) {
				if (name != null)
					throw new ExpressionParserException(namePos, "Unexpected token: " + name);
				if (from != null)
					throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
				if (scope != SqlExpressionParser.ID_INVALID)
					throw new ExpressionParserException(targetTypePos, "Not expecting: " + SqlExpressionParser.toOperationString(scope));
				BasicTable r = new BasicTable();
				r.setTitle("VARS");
				r.addColumn(String.class, "Name");
				r.addColumn(String.class, "DeclaredType");
				r.addColumn(String.class, "Type");
				r.addColumn(String.class, "Value");
				r.addColumn(Boolean.class, "Constant");
				{
					CalcFrame consts = sf.getGlobalConsts();
					for (String varname : consts.getVarKeys()) {
						Object rawvalue = consts.getValue(varname);
						String value = OH.toString(rawvalue);
						if (!full)
							value = SH.ddd(value, 80);
						r.getRows().addRow(varname, mf.forType(consts.getType(varname)), rawvalue == null ? null : mf.forType(rawvalue.getClass()), value, true);
					}
					CalcFrame vars = sf.getGlobal();
					for (String varname : vars.getVarKeys()) {
						Object rawvalue = vars.getValue(varname);
						String value = OH.toString(rawvalue);
						if (!full)
							value = SH.ddd(value, 80);
						r.getRows().addRow(varname, mf.forType(vars.getType(varname)), rawvalue == null ? null : mf.forType(rawvalue.getClass()), value, false);
					}
				}
				for (CalcFrameStack sf2 = sf; sf2 != null; sf2 = sf2.isParentVisible() ? sf2.getParent() : null) {
					CalcFrame consts = sf2.getFrameConsts();
					for (String varname : consts.getVarKeys()) {
						Object rawvalue = consts.getValue(varname);
						String value = OH.toString(rawvalue);
						if (!full)
							value = SH.ddd(value, 80);
						r.getRows().addRow(varname, mf.forType(consts.getType(varname)), rawvalue == null ? null : mf.forType(rawvalue.getClass()), value, true);
					}
					CalcFrame vars = sf2.getFrame();
					for (String varname : vars.getVarKeys()) {
						Object rawvalue = vars.getValue(varname);
						String value = OH.toString(rawvalue);
						if (!full)
							value = SH.ddd(value, 80);
						r.getRows().addRow(varname, mf.forType(vars.getType(varname)), rawvalue == null ? null : mf.forType(rawvalue.getClass()), value, false);
					}
				}
				return r;
			}
		}

		if (targetType == null)
			throw new ExpressionParserException(targetTypePos, "SHOW requires noun");
		else
			throw new ExpressionParserException(targetTypePos, "SHOW references unrecognized noun: " + SH.toUpperCase(targetType));
	}
	@Override
	public Table processTableAdd(CalcFrameStack sf, String name, int namePosition, Table r, Map<String, Node> useOptions, int scope, boolean ifNotExists) {
		if (CH.isntEmpty(useOptions)) {
			Node first = CH.first(useOptions.values());
			throw new ExpressionParserException(first.getPosition(), "USE options not supported: " + CH.first(useOptions.keySet()));
		}
		if (ifNotExists && sf.getTableset().getTableNames().contains(name))
			return null;
		r.setTitle(name);
		sf.getTableset().putTable(r);
		return r;
	}

	@Override
	public void processEnabled(CalcFrameStack sf, boolean enable, int position, String type, String[] name, int[] namePosition) {
		throw new ExpressionParserException(position, enable ? "Not supported: ENABLE" : "Not Supported: DISABLE");
	}

	//	@Override
	//	public Table processReturningTable(CalcFrameStack sf, Table r) {
	//		return r;
	//	}

	@Override
	public void processTriggerRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int isPublic) {
		throw new ExpressionParserException(fromPos, "Not supported: RENAME TRIGGER");
	}

	@Override
	public void processTimerRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int isPublic) {
		throw new ExpressionParserException(fromPos, "Not supported: RENAME TIMER");
	}

	@Override
	public void processProcedureRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int isPublic) {
		throw new ExpressionParserException(fromPos, "Not supported: RENAME PROCEDURE");
	}

	//	@Override
	//	public void processReturningRowsEffected(CalcFrameStack sf, long rowsEffected) {
	//	}

	@Override
	public TempIndex findIndex(CalcFrameStack sf, Table targetTable, String[] targetColumns, int targetTablePos, Table sourceTable, String[] sourceColumns, List<Row> targetRows) {
		return null;
	}

	@Override
	public Table getTableIfExists(CalcFrameStack sf, String tableName, int scope) {
		Table r = sf.getTableset().getTableNoThrow(tableName);
		if (r != null)
			return r;
		Class<?> type = DerivedHelper.getType(sf, tableName);
		if (type != null && Table.class.isAssignableFrom(type))
			return (Table) DerivedHelper.getValue(sf, tableName);
		return null;

	}

	@Override
	public Table getTable(CalcFrameStack sf, int position, String tableName, int scope) {
		Table r = getTableIfExists(sf, tableName, scope);
		if (r == null)
			throw new ExpressionParserException(position, "Unknown table: " + tableName);
		return r;
	}

	@Override
	public Map processDiagnoseColumn(CalcFrameStack sf, int scope, Table table, Column column) {
		int addressSize = UnsafeHelper.unsafe.addressSize();
		if (column == null) {
			if (table instanceof BasicTable) {
				long overhead = 1024 * 1024 + table.getRows().size() * (5 * addressSize + 8);
				overhead += table.getColumnsCount() * 32;
				return CH.m("TYPE", "TABLE_OVERHEAD", "EST_MEMORY", overhead, "COMMENT", "ROW STORAGE ENGINE", "COUNT", table.getSize());
			} else if (table instanceof ColumnarTable) {
				long overhead = 1024 * 1024 + table.getRows().size() * (3 * addressSize + 8);
				overhead += table.getColumnsCount() * 32;
				return CH.m("TYPE", "TABLE_OVERHEAD", "EST_MEMORY", overhead, "COMMENT", "COLUMNAR STORAGE ENGINE", "COUNT", table.getSize());
			} else {
				return CH.m("TYPE", "TABLE_OVERHEAD", "COMMENT", "Unknown storage engine", "COUNT", table.getSize());
			}
		}
		HashSet<Object> unique = new HashSet<Object>();
		int nonNullCount = 0;
		int loc = column.getLocation();
		long size = 0;
		double length = 0;

		TableList rows = table.getRows();
		if (table instanceof BasicTable) {
			for (Row row : rows) {
				Object o = row.getAt(loc);
				if (o != null) {
					size += 8;//assume GC overhead
					nonNullCount++;
					if (o instanceof String) {
						int j = o.toString().length() * 2;
						size += j + 24;
						length += j;
					} else if (o instanceof Bytes) {
						int j = ((Bytes) o).length();
						size += 16 + j;
						length += j;
					} else if (o instanceof BigInteger) {
						int j = 40 + ((BigInteger) o).bitLength() / 32;
						length += j;
					} else if (o instanceof BigDecimal) {
						int j = 74 + ((BigDecimal) o).toBigInteger().bitLength() / 32;
						length += j;
					} else {
						size += 8;//almost all boxed primitives just have one member var
					}
				}
				unique.add(o);
			}
			return CH.m("TYPE", "COLUMN", "COUNT", nonNullCount, "AVG_LENGTH", length == 0d ? null : (length / nonNullCount), "CARDINALITY", unique.size(), "EST_MEMORY", size,
					"COMMENT", column.getType().getSimpleName());
		} else if (table instanceof ColumnarTable) {
			ColumnarColumn ccolumn = (ColumnarColumn) column;
			size += ccolumn.getMemorySize();
			for (Row row : rows) {
				Object o = row.getAt(loc);
				if (o != null) {
					nonNullCount++;
					if (o instanceof String) {
						int j = o.toString().length();
						length += j;
					} else if (o instanceof Bytes) {
						int j = ((Bytes) o).length();
						length += j;
					} else if (o instanceof BigInteger) {
						int j = ((BigInteger) o).toString().length();
						length += j;
					} else if (o instanceof BigDecimal) {
						int j = ((BigDecimal) o).toString().length();
						length += j;
					}
				}
				unique.add(o);
			}
			String comment;
			if (ccolumn instanceof ColumnarColumnString_BitMap)
				comment = ((ColumnarColumnString_BitMap) ccolumn).getIndexSize() + "-BYTE INDEXED BITMAP";
			else if (ccolumn instanceof ColumnarColumnPrimitive) {
				comment = ((ColumnarColumnPrimitive) ccolumn).getPrimitiveMemorySize() + "-BYTE PRIMITIVE ARRAY (" + ccolumn.getType().getSimpleName().toUpperCase() + ")";
			} else if (ccolumn instanceof ColumnarColumnString_CompactAscii) {
				comment = "1-BYTE CHAR COMPACT ARRAY";
			} else if (ccolumn instanceof ColumnarColumnString_CompactChars) {
				comment = "2-BYTE CHAR COMPACT ARRAY";
			} else if (ccolumn instanceof ColumnarColumnObject) {
				comment = addressSize + "-BYTE ADDRESSED JAVA OBJECT (" + ccolumn.getType().getSimpleName() + ")";
			} else if (ccolumn instanceof ColumnarColumnObject_Cached) {
				ColumnarColumnObject_Cached cc = (ColumnarColumnObject_Cached) ccolumn;
				comment = "ONDISK CACHE (FRAGMENTATION=" + cc.getCache().getFragmentation() + ", FILE_SIZE=" + cc.getCache().getDiskSize() + ")";
			} else
				comment = SH.stripPrefix(column.getClass().getSimpleName(), "ColumnarColumn", false);
			return CH.m("TYPE", "COLUMN", "COUNT", nonNullCount, "AVG_LENGTH", length == 0d ? null : (length / nonNullCount), "CARDINALITY", unique.size(), "EST_MEMORY", size,
					"COMMENT", comment);

		} else
			return CH.m("TYPE", "COLUMN", "COMMENT", "Unknown storage engine");
	}

	@Override
	public Set<String> getIndexes(CalcFrameStack sf, Table targetTable) {
		return Collections.EMPTY_SET;
	}

	@Override
	public Map processDiagnoseIndex(CalcFrameStack sf, int scope, Table table, int indexNamePos, String indexName) {
		throw new ExpressionParserException(indexNamePos, "Unknown index: " + indexName);
	}

	@Override
	public void processMethodCreate(CalcFrameStack sf, int pos, List<MethodFactory> methodFactory, boolean ifNotExists) {
		throw new ExpressionParserException(pos, "Can not create methods");
	}
	@Override
	public void processMethodDrop(CalcFrameStack sf, int pos, String methodName, Class[] types, boolean ifExists) {
		throw new ExpressionParserException(pos, "Can not drop methods");
	}

	@Override
	public void processDiagnoseTable(CalcFrameStack sf, int scope, Table table, ColumnarTable r) {
		for (int x = 0; x <= table.getColumnsCount(); x++) {
			Column i = x == table.getColumnsCount() ? null : table.getColumnAt(x);
			Map<String, Object> m = owner.getMutator().processDiagnoseColumn(sf, scope, table, i);
			for (Entry<String, Object> j : m.entrySet()) {
				if (j.getValue() != null && !r.getColumnsMap().containsKey(j.getKey())) {
					r.addColumn(j.getValue().getClass(), j.getKey(), null, true);
				}
			}
			ColumnarRow row = r.newEmptyRow();
			r.getRows().add(row);
			row.put("TABLE", table.getTitle());
			if (i != null)
				row.put("NAME", (String) i.getId());
			for (Entry<String, Object> j : m.entrySet()) {
				if (j.getValue() != null)
					row.put(j.getKey(), j.getValue());
			}
		}
		for (String s : owner.getMutator().getIndexes(sf, table)) {
			Map<String, Object> m = owner.getMutator().processDiagnoseIndex(sf, scope, table, 0, s);
			for (Entry<String, Object> j : m.entrySet()) {
				if (j.getValue() != null && !r.getColumnsMap().containsKey(j.getKey())) {
					r.addColumn(j.getValue().getClass(), j.getKey(), null, true);
				}
			}
			ColumnarRow row = r.newEmptyRow();
			r.getRows().add(row);
			row.put("TABLE", table.getTitle());
			row.put("NAME", (String) s);
			for (Entry<String, Object> j : m.entrySet()) {
				if (j.getValue() != null)
					row.put(j.getKey(), j.getValue());
			}
		}
	}

	private Class parseType(CalcFrameStack sf, int typePos, String type) {
		try {
			return sf.getFactory().forName(type);
		} catch (ClassNotFoundException e) {
			throw new ExpressionParserException(typePos, "Invalid type: " + type);
		}
	}

	@Override
	public void processReturningTable(CalcFrameStack sf, TableReturn r) {
	}

	@Override
	public void processDboCreate(CalcFrameStack sf, String dboName, int dboNamePos, String typeName, int typeNamePos, int priority, Map<String, Node> useOptions,
			boolean ifNotExists) {
		throw new ExpressionParserException(dboNamePos, "CREATE DBO not supported");
	}

	@Override
	public void processDboRemove(CalcFrameStack sf, String dboName, int dboNamePos, boolean ifExists) {
		throw new ExpressionParserException(dboNamePos, "DROP DBO not supported");
	}

	@Override
	public void processDboRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope) {
		throw new ExpressionParserException(fromPos, "RENAME DBO not supported");
	}

	@Override
	public void processAlterUseOptions(CalcFrameStack sf, int targetType, String name, int position, Map<String, Node> useOptions) {
		throw new ExpressionParserException(position, "ALTER ... USE not supported");
	}

	@Override
	public Iterable<Row> findSortIndex(CalcFrameStack sf, Table next, String name, boolean asc) {
		return null;
	}

	@Override
	public boolean hasIndex(CalcFrameStack sf, Table table, String columnName) {
		return false;
	}

}
