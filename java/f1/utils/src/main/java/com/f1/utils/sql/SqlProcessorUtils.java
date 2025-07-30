package com.f1.utils.sql;

import com.f1.base.CalcTypes;
import com.f1.base.Table;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParser;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.CastNode;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.ExpressionNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.CalcTypesTuple2;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class SqlProcessorUtils {

	static public Class getWidest(Class c1, Class c2) {
		if (c1 == c2)
			return c1;
		if (c1 == String.class || c2 == String.class)
			return String.class;
		return OH.getWidest(c1, c2);
	}

	//	public static void addToSchema(String tableName, final Table table, com.f1.utils.BasicTypes schema) {
	//		for (MappingEntry<String, Class<?>> var : TableHelper.getColumnTypes(table).entries()) {
	//			schema.put(var.getKey(), var.getValue());
	//			schema.put(tableName + "." + var.getKey(), var.getValue());
	//		}
	//	}
	public static CalcTypes toTypes(CalcFrameStack context, Table table) {
		if (context.getGlobal().isVarsEmpty()) {
			if (table == null)
				return EmptyCalcTypes.INSTANCE;
			return table.getColumnTypesMapping();
		}
		if (table == null)
			return toTypes(context.getGlobal());
		else
			return new CalcTypesTuple2(table.getColumnTypesMapping(), context.getGlobal());
	}

	public static CalcTypes toTypes(Table table, CalcTypes globalVars) {
		if (globalVars == null || globalVars.isVarsEmpty()) {
			if (table == null)
				return EmptyCalcTypes.INSTANCE;
			return table.getColumnTypesMapping();
		}
		if (table == null)
			return globalVars;
		else
			return new CalcTypesTuple2(table.getColumnTypesMapping(), globalVars);
	}
	public static CalcTypes toTypes(CalcTypes globalVars) {
		if (globalVars == null || globalVars.isVarsEmpty())
			return EmptyCalcTypes.INSTANCE;
		return globalVars;
	}
	public Class<?> forName(MethodFactoryManager mf, int typePos, String type) {
		try {
			return mf.forName(type);
		} catch (ClassNotFoundException e) {
			throw new ExpressionParserException(typePos, "Invalid type: " + type);
		}
	}

	//	public static class RowGetter implements NameSpaceFrame, PositionalFrame {
	//		public Row current;
	//
	//		final private NameSpaceTypes columnTypes;
	//		final private String tableName;
	//
	//		public RowGetter(Table table) {
	//			this(table.getTitle(), table.getColumnTypesMapping());
	//		}
	//		public RowGetter(String tableName, NameSpaceTypes columnTypes) {
	//			this.tableName = tableName;
	//			this.columnTypes = columnTypes;
	//		}
	//
	//		public RowGetter reset(Row row) {
	//			this.current = row;
	//			return this;
	//		}
	//
	//		@Override
	//		public Object getValue(String key) {
	//			if (columnTypes.containsKey(key))
	//				return current.get(key);
	//			throw new IllegalStateException("unknown key: " + key);
	//		}
	//
	//		@Override
	//		public Object putValue(String key, Object value) {
	//			return this.current.putValue(key, value);
	//		}
	//		@Override
	//		public NameSpaceTypes getTypes() {
	//			return this.columnTypes;
	//		}
	//		@Override
	//		public boolean containsValue(String key) {
	//			// TODO Auto-generated method stub
	//			return false;
	//		}
	//		@Override
	//		public Object getValue(NameSpaceIdentifier id) {
	//			if (OH.eq(id.getNamespace(), tableName))
	//				return current.get(id.getVarName());
	//			throw new IllegalStateException("unknown key: " + id);
	//		}
	//		@Override
	//		public Object getAt(int n) {
	//			return current.getAt(n);
	//		}
	//	}
	//
	//	public static class RowsGetter extends ArrayList<Row> {
	//
	//		private RowGetter inner;
	//
	//		public RowsGetter(RowGetter inner) {
	//			this.inner = inner;
	//		}
	//		public RowsGetter(RowGetter inner, List<Row> values) {
	//			super(values);
	//			this.inner = inner;
	//		}
	//		public Row get(int a) {
	//			return inner.reset((Row) super.get(a));
	//		}
	//		@Override
	//		public Iterator<Row> iterator() {
	//			return new RowsGetterIterator(this);
	//		}
	//
	//	}
	//
	//	public static class RowsGetterIterator implements Iterator<Row> {
	//
	//		private RowsGetter rowsGetter;
	//		private int nextPos;
	//
	//		public RowsGetterIterator(RowsGetter rowsGetter) {
	//			this.rowsGetter = rowsGetter;
	//			this.nextPos = 0;
	//		}
	//
	//		@Override
	//		public boolean hasNext() {
	//			return nextPos < this.rowsGetter.size();
	//		}
	//
	//		@Override
	//		public Row next() {
	//			return this.rowsGetter.get(nextPos++);
	//		}
	//
	//		@Override
	//		public void remove() {
	//			throw new UnsupportedOperationException();
	//		}
	//	}

	public static class Limits {
		final private Node limitOffset;
		final private Node limit;

		public Limits(SqlColumnsNode limit) {
			if (limit != null) {
				//				Node[] cols = limit.columns;
				if (limit.getColumnsCount() == 1) {
					this.limit = limit.getColumnAt(0);
					this.limitOffset = null;
				} else if (limit.getColumnsCount() == 2) {
					this.limitOffset = limit.getColumnAt(0);
					this.limit = limit.getColumnAt(1);
				} else
					throw new ExpressionParserException(limit.getPosition(), "LIMIT must have 1 or 2 arguments, not: " + limit.getColumnsCount());
			} else {
				this.limitOffset = null;
				this.limit = null;
			}
		}

		public Node getLimitNode() {
			return limit;
		}
		public Node getLimitOffsetNode() {
			return limitOffset;
		}
		public int getLimit(BasicDerivedCellParser dcp, CalcFrameStack sf) {
			if (limit == null)
				return -1;
			try {
				return Math.max(0, ((Number) dcp.toCalc(this.limit, sf).get(sf)).intValue());
			} catch (ExpressionParserException e) {
				throw e;
			} catch (Exception e) {
				throw new ExpressionParserException(this.limit.getPosition(), "LIMIT must evaluate to number", e);
			}
		}
		public int getLimitOffset(BasicDerivedCellParser dcp, CalcFrameStack sf) {
			if (limitOffset == null)
				return 0;
			final int limitOffset;
			try {
				limitOffset = ((Number) dcp.toCalc(this.limitOffset, sf).get(sf)).intValue();
			} catch (ExpressionParserException e) {
				throw e;
			} catch (Exception e) {
				throw new ExpressionParserException(this.limitOffset.getPosition(), "LIMIT offset must evaluate to number", e);
			}
			if (limitOffset < 0)
				throw new ExpressionParserException(this.limitOffset.getPosition(), "LIMIT offset can not be negative");
			return limitOffset;
		}

	}

	static public AsNode[] toAsNode(Node[] nodes) {
		AsNode[] r = new AsNode[nodes.length];
		for (int i = 0; i < nodes.length; i++)
			r[i] = toAsNode(nodes[i]);
		return r;
	}

	static public AsNode toAsNode(Node col) {
		if (col instanceof AsNode) {
			AsNode r = (AsNode) col;
			if (r.getValue() == null)
				throw new ExpressionParserException(r.getPosition(), "AS missing expression");
			return r;
		} else {
			String name = toName(col);
			if (name.length() == 0)
				name = "_";
			return new AsNode(col.getPosition(), col, new VariableNode(col.getPosition(), name), false);

		}
	}
	private static String toName(Node col) {
		while (col instanceof ExpressionNode)
			col = ((ExpressionNode) col).getValue();
		if (col instanceof MethodNode || col instanceof OperationNode || col instanceof CastNode) {
			StringBuilder sb = new StringBuilder();
			String name = SH.toStringAndClear(toString(col, sb));
			for (int i = 0; i < name.length(); i++) {
				char c = name.charAt(i);
				if (OH.isBetween(c, 'a', 'z') || OH.isBetween(c, 'A', 'Z') || c == '_')
					sb.append(c);
				else if (c == '`')
					continue;
				else if (OH.isBetween(c, '0', '9')) {
					if (sb.length() == 0)
						sb.append('_');
					sb.append(c);
				} else if (c == '(' && i == 0) {
					int i2 = name.indexOf(')');
					if (i2 != name.length() - 1)
						i = i2;
				} else if (sb.length() > 0 && !SH.endsWith(sb, '_') && c != ')') {
					sb.append('_');
				}
			}
			name = sb.toString();
			if (name.length() == 0)
				name = "_";
			return name;
		}

		if (col instanceof VariableNode)
			return ((VariableNode) col).getVarname();
		if (col instanceof SqlNode) {
			SqlNode sn = (SqlNode) col;
			StringBuilder sb = new StringBuilder();
			if (SqlExpressionParser.isScopeKeyword(sn.getOperation()))
				return SH.toStringAndClear(toString(sn.getNext(), sb));
			else
				return SH.toStringAndClear(toString(col, sb));
		}
		if (col instanceof OperationNode) {
			OperationNode op = (OperationNode) col;
			return toName(op.getLeft()) + op.getOpString() + toName(op.getRight());
		}
		StringBuilder sb = new StringBuilder();
		return SH.toStringAndClear(toString(col, sb));
	}

	private static StringBuilder toString(Node node, StringBuilder buf) {
		if (node instanceof ConstNode) {
			return buf.append(((ConstNode) node).getValue());
		} else if (node instanceof OperationNode) {
			OperationNode op = (OperationNode) node;
			toString(op.getLeft(), buf).append(op.getOpString());
			return toString(op.getRight(), buf);
		} else if (node != null)
			return node.toString(buf);
		else
			return buf;
	}

	static public class GroupByDerivedCellParser extends SqlDerivedCellParser {

		private SelectClause sc;
		private boolean isRecursed = false;

		public GroupByDerivedCellParser(ExpressionParser parser, SqlProcessor sqlProcessor, SelectClause sc) {
			super(parser, sqlProcessor);
			this.sc = sc;
		}

		@Override
		protected DerivedCellCalculator determineVariableType(int position, String varname, CalcTypesStack context) {
			if (!isRecursed) {
				isRecursed = true;
				try {
					for (AsNode i : sc.getSelects())
						if (OH.eq(varname, i.getAs().toString()))
							return super.toCalc(i.getValue(), context);
				} finally {
					isRecursed = false;
				}
			}
			return super.determineVariableType(position, varname, context);
		}

	}

}
