package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Pointer;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.BasicPointer;
import com.f1.utils.OH;
import com.f1.utils.sql.SqlProcessorUtils.Limits;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.ArrayNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.AdminNode;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.WhereNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.TablesCalcFrame;

public class SqlProcessor_Delete {

	final private SqlProcessor owner;

	public SqlProcessor_Delete(SqlProcessor sqlProcessor) {
		this.owner = sqlProcessor;
	}
	public TableReturn processTruncate(CalcFrameStack sf, AdminNode node) {
		final int scope;
		if (node.getOptions() == null || !SqlExpressionParser.isScopeKeyword(node.getOptions().getOperation()))
			scope = SqlExpressionParser.ID_INVALID;
		else
			scope = node.getOptions().getOperation();
		final ArrayNode names = JavaExpressionParser.castNode(node.getNext(), ArrayNode.class);
		final int size = names.getParamsCount();
		if (size > 1) {
			Set<String> visited = new HashSet<String>();
			for (int i = 0; i < size; i++) {
				Node nameNode = names.getParamAt(i);
				String name = nameNode.toString();
				this.owner.getMutator().getTable(sf, node.getPosition(), name, scope);
				if (!visited.add(name))
					throw new ExpressionParserException(nameNode.getPosition(), "Duplicate Table: " + name);
			}
		}
		long rows = 0;
		for (int i = 0; i < size; i++) {
			Node nameNode = names.getParamAt(i);
			String name = nameNode.toString();
			Table r = this.owner.getMutator().getTable(sf, node.getPosition(), name, scope);
			rows += r.getSize();
			this.owner.getMutator().processRowRemoveAll(sf, r);
		}
		return new TableReturn(rows);

	}
	public FlowControl processDelete(CalcFrameStack sf, DerivedCellCalculatorSql query, SqlColumnsNode fromClause, Table table2) {
		if (fromClause.getOperation() != SqlExpressionParser.ID_DELETE)
			throw new ExpressionParserException(fromClause.getPosition(), "Expecting DELETE");
		SqlProcessorTableMutator mutator = this.owner.getMutator();
		MethodFactoryManager methodFactory = sf.getFactory();
		SqlDerivedCellParser derivedCellParser = this.owner.getParser();

		final AsNode deleteTable = SqlProcessorUtils.toAsNode(fromClause.getColumnAt(0));
		final String deleteTableName;
		final String deleteTableNameAs;
		final Table table;
		{
			final Node val = deleteTable.getValue();
			if (val instanceof VariableNode) {
				deleteTableName = ((VariableNode) val).getVarname();
				table = owner.getMutator().getTable(sf, val.getPosition(), deleteTableName, SqlExpressionParser.ID_INVALID);
				deleteTableNameAs = deleteTable.getAs().toString();
			} else if (val instanceof SqlNode && SqlExpressionParser.isScopeKeyword(((SqlNode) val).getOperation())) {
				SqlNode t = (SqlNode) val;
				deleteTableName = ((VariableNode) t.getNext()).getVarname();
				table = owner.getMutator().getTable(sf, val.getPosition(), deleteTableName, t.getOperation());
				deleteTableNameAs = deleteTable.getAs().toString();
			} else {
				deleteTableName = val.toString();
				table = owner.getMutator().getTable(sf, val.getPosition(), deleteTableName, SqlExpressionParser.ID_INVALID);
				deleteTableNameAs = deleteTableName;
			}
		}

		WhereNode where = null;

		final String joinTable;
		final OperationNode joinOn;

		int joinType;
		SqlNode next = JavaExpressionParser.castNodeNotRequired(fromClause.getNext(), SqlNode.class);
		if (next != null) {
			switch (next.getOperation()) {
				case SqlExpressionParser.ID_RIGHT_JOIN:
				case SqlExpressionParser.ID_RIGHT_ONLY_JOIN:
				case SqlExpressionParser.ID_OUTER_JOIN:
				case SqlExpressionParser.ID_OUTER_ONLY_JOIN:
					throw new ExpressionParserException(next.getPosition(),
							"UPDATE only supports JOIN and LEFT JOIN. not: " + SqlExpressionParser.toOperationString(next.getOperation()));
				case SqlExpressionParser.ID_LEFT_ONLY_JOIN:
				case SqlExpressionParser.ID_LEFT_JOIN:
				case SqlExpressionParser.ID_JOIN:
					joinType = next.getOperation();
					joinTable = SqlProcessorUtils.toAsNode(((SqlColumnsNode) next).getColumnAt(0)).getAs().toString();
					SqlColumnsNode on = JavaExpressionParser.castNode(next.getNext(), SqlColumnsNode.class);
					if (on.getOperation() != SqlExpressionParser.ID_ON)
						throw new ExpressionParserException(on.getPosition(), "after JOIN expresion, Expecting ON");
					joinOn = (OperationNode) on.getColumnAt(0);
					next = JavaExpressionParser.castNodeNotRequired(on.getNext(), SqlNode.class);
					break;
				default:
					joinType = -1;
					joinOn = null;
					joinTable = null;
			}
		} else {
			joinType = -1;
			joinOn = null;
			joinTable = null;
		}

		if (next != null && next.getOperation() == SqlExpressionParser.ID_WHERE) {
			where = JavaExpressionParser.castNode(next, WhereNode.class);
			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlNode.class);
		}
		int offset;
		int limit;
		if (next != null && next.getOperation() == SqlExpressionParser.ID_LIMIT) {
			Limits limits = new Limits(JavaExpressionParser.castNode(next, SqlColumnsNode.class));
			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlNode.class);
			offset = limits.getLimitOffset(derivedCellParser, sf);
			limit = limits.getLimit(derivedCellParser, sf);
		} else {
			offset = 0;
			limit = -1;
		}
		if (next != null)
			throw new ExpressionParserException(next.getPosition(), "Unexpected Operation in DELETE clause: " + SqlExpressionParser.toOperationString(next.getOperation()));

		TablesCalcFrame tf;

		if (joinTable != null)
			tf = new TablesCalcFrame(new String[] { deleteTableNameAs, joinTable }, new Table[] { table, table2 });
		else
			tf = new TablesCalcFrame(new String[] { deleteTableNameAs }, new Table[] { table });
		CalcTypesStack context2 = new ChildCalcTypesStack(sf, true, tf);

		final DerivedCellCalculator whereClause;
		if (where != null) {
			whereClause = derivedCellParser.toCalc(where.getCondition(), context2);
			if (whereClause.getReturnType() != Boolean.class)
				throw new ExpressionParserException(where.getPosition(), "WHERE clause must evaluate to boolean not: " + whereClause.getReturnType());
			DerivedHelper.replaceVarsWithConsts(whereClause, sf, tf);
		} else {
			whereClause = null;
		}
		if (joinTable != null) {
			DerivedCellCalculator joinTo = derivedCellParser.toCalc(joinOn, context2);
			if (joinTo.getReturnType() != Boolean.class)
				throw new ExpressionParserException(where.getPosition(), "JOIN ON clause must evaluate to boolean not: " + joinTo.getReturnType());
			Map<String, Table> asTables = new LinkedHashMap<String, Table>();
			asTables.put(deleteTableNameAs, table);
			asTables.put(joinTable, table2);
			DeleteProjectionVisitor visitor = new DeleteProjectionVisitor();
			SqlProjector.project(query, asTables, OH.EMPTY_STRING_ARRAY, DerivedHelper.EMPTY_ARRAY, joinType, joinTo, null, whereClause, null, null, null, offset, limit,
					this.owner, visitor, sf);
			if (visitor.getDeleteCount() == 0)
				return TableReturn.EMPTY;
			else if (visitor.getDeleteCount() == table.getSize())
				return new TableReturn(mutator.processRowRemoveAll(sf, table));
			else
				return new TableReturn(mutator.processRowRemoves(sf, table, visitor.getRows(table)));
		}

		return doDelete(query, mutator, table, offset, limit, whereClause, sf);
	}
	protected FlowControl doDelete(DerivedCellCalculatorSql query, SqlProcessorTableMutator mutator, final Table table, int offset, int limit, DerivedCellCalculator whereClause,
			CalcFrameStack sf) {
		if (whereClause == null) {
			if (offset == 0 && limit == -1) {
				int size = table.getSize();
				mutator.processRowRemoveAll(sf, table);
				return new TableReturn(size);
			} else {
				int size = table.getSize();
				if (offset >= size)
					return TableReturn.EMPTY;
				if (limit > size - offset)
					limit = size - offset;
				if (offset == 0 && limit == size) {
					mutator.processRowRemoveAll(sf, table);
					return new TableReturn(size);
				} else if (limit > 0) {
					List<Row> toDelete = new ArrayList<Row>(limit);
					TableList rows = table.getRows();
					for (int n = offset + limit - 1; n >= offset; n--)
						toDelete.add(rows.get(n));
					mutator.processRowRemoves(sf, table, toDelete);
					return new TableReturn(toDelete.size());
				} else
					return TableReturn.EMPTY;
			}
		} else {
			if (whereClause.getReturnType() != Boolean.class)
				throw new ExpressionParserException(whereClause.getPosition(), "Where clause must evaluate to boolean not: " + whereClause.getReturnType());
			if (offset >= table.getSize() && limit == 0)
				return TableReturn.EMPTY;
			List<Row> toDelete = null;
			//			RowGetter rg = new RowGetter(table);
			ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
			Pointer<DerivedCellCalculator> pWhereClause = new BasicPointer<DerivedCellCalculator>(whereClause);
			Iterable<Row> rows = mutator.applyIndexes(sf, null, table, pWhereClause, limit == -1 ? Integer.MAX_VALUE : limit);
			whereClause = pWhereClause.get();
			for (Row row : rows) {
				rsf.reset(row);
				if (Boolean.TRUE.equals(whereClause.get(rsf))) {
					if (offset > 0) {
						offset--;
						continue;
					}
					if (toDelete == null)
						toDelete = new ArrayList<Row>();
					toDelete.add(row);
					if (limit != -1 && toDelete.size() == limit)
						break;
				}
			}
			if (toDelete != null) {
				if (toDelete.size() == table.getSize()) {
					mutator.processRowRemoveAll(sf, table);
				} else {
					Collections.reverse(toDelete);
					mutator.processRowRemoves(sf, table, toDelete);
				}
			}
			return toDelete == null ? TableReturn.EMPTY : new TableReturn(toDelete.size());
		}
	}
	//	public void getDependencies(DerivedCellParserContext context, SqlColumnsNode delete, Set<Object> sink2) {
	//		Set<Object> sink = new HashSet<Object>();
	//		SqlColumnsNode fromClause = JavaExpressionParser.castNode(delete.getColumnAt(0), SqlColumnsNode.class, "FROM");
	//		if (fromClause.getOperation() != SqlExpressionParser.ID_FROM)
	//			throw new ExpressionParserException(fromClause.getPosition(), "Expecting FROM");
	//		MethodFactoryManager methodFactory = context.getFactory();
	//		SqlDerivedCellParser derivedCellParser = this.owner.getParser();
	//
	//		final AsNode deleteTable = SqlProcessorUtils.toAsNode(fromClause.getColumnAt(0));
	//		final String deleteTableName;
	//		final String deleteTableNameAs;
	//		final Table table;
	//		{
	//			final Node val = deleteTable.getValue();
	//			if (val instanceof VariableNode) {
	//				deleteTableName = ((VariableNode) val).getVarname();
	//				table = owner.getMutator().getTable(context, val.getPosition(), deleteTableName, SqlExpressionParser.ID_INVALID);
	//				deleteTableNameAs = deleteTable.getAs().toString();
	//			} else if (val instanceof SqlNode && SqlExpressionParser.isScopeKeyword(((SqlNode) val).getOperation())) {
	//				SqlNode t = (SqlNode) val;
	//				deleteTableName = ((VariableNode) t.getNext()).getVarname();
	//				table = owner.getMutator().getTable(context, val.getPosition(), deleteTableName, t.getOperation());
	//				deleteTableNameAs = deleteTable.getAs().toString();
	//			} else {
	//				deleteTableName = val.toString();
	//				table = owner.getMutator().getTable(context, val.getPosition(), deleteTableName, SqlExpressionParser.ID_INVALID);
	//				deleteTableNameAs = deleteTableName;
	//			}
	//		}
	//
	//		WhereNode where = null;
	//
	//		final AsNode joinTable;
	//		final OperationNode joinOn;
	//
	//		SqlNode next = JavaExpressionParser.castNodeNotRequired(fromClause.getNext(), SqlNode.class);
	//		if (next != null) {
	//			switch (next.getOperation()) {
	//				case SqlExpressionParser.ID_RIGHT_JOIN:
	//				case SqlExpressionParser.ID_RIGHT_ONLY_JOIN:
	//				case SqlExpressionParser.ID_OUTER_JOIN:
	//				case SqlExpressionParser.ID_OUTER_ONLY_JOIN:
	//					throw new ExpressionParserException(next.getPosition(),
	//							"UPDATE only supports JOIN and LEFT JOIN. not: " + SqlExpressionParser.toOperationString(next.getOperation()));
	//				case SqlExpressionParser.ID_LEFT_ONLY_JOIN:
	//				case SqlExpressionParser.ID_LEFT_JOIN:
	//				case SqlExpressionParser.ID_JOIN:
	//					joinTable = SqlProcessorUtils.toAsNode(((SqlColumnsNode) next).getColumnAt(0));
	//					SqlColumnsNode on = JavaExpressionParser.castNode(next.getNext(), SqlColumnsNode.class);
	//					if (on.getOperation() != SqlExpressionParser.ID_ON)
	//						throw new ExpressionParserException(on.getPosition(), "after JOIN expresion, Expecting ON");
	//					joinOn = (OperationNode) on.getColumnAt(0);
	//					next = JavaExpressionParser.castNodeNotRequired(on.getNext(), SqlNode.class);
	//					break;
	//				default:
	//					joinOn = null;
	//					joinTable = null;
	//			}
	//		} else {
	//			joinOn = null;
	//			joinTable = null;
	//		}
	//
	//		if (next != null && next.getOperation() == SqlExpressionParser.ID_WHERE) {
	//			where = JavaExpressionParser.castNode(next, WhereNode.class);
	//			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlNode.class);
	//		}
	//		if (next != null && next.getOperation() == SqlExpressionParser.ID_LIMIT) {
	//			Limits limits = new Limits(JavaExpressionParser.castNode(next, SqlColumnsNode.class));
	//			limits.getDependencyIds(context, this.owner.getParser(), sink);
	//			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlNode.class);
	//		}
	//		if (next != null)
	//			throw new ExpressionParserException(next.getPosition(), "Unexpected Operation in DELETE clause: " + SqlExpressionParser.toOperationString(next.getOperation()));
	//
	//		TypesGetter schema = new SqlProcessorUtils.TypesGetter(deleteTableNameAs, table, context.getGlobalVarTypes());
	//
	//		String joinTableNameAs;
	//		String joinTableName;
	//		Table table2;
	//		if (joinTable != null) {
	//			final Node val = joinTable.getValue();
	//			if (val instanceof VariableNode) {
	//				joinTableName = ((VariableNode) val).getVarname();
	//				table2 = owner.getMutator().getTable(context, val.getPosition(), joinTableName, SqlExpressionParser.ID_INVALID);
	//				joinTableNameAs = joinTable.getAs().toString();
	//			} else if (val instanceof SqlNode && SqlExpressionParser.isScopeKeyword(((SqlNode) val).getOperation())) {
	//				SqlNode t = (SqlNode) val;
	//				joinTableName = ((VariableNode) t.getNext()).getVarname();
	//				table2 = owner.getMutator().getTable(context, val.getPosition(), joinTableName, t.getOperation());
	//				joinTableNameAs = joinTable.getAs().toString();
	//			} else {
	//				joinTableName = val.toString();
	//				table2 = owner.getMutator().getTable(context, val.getPosition(), joinTableName, SqlExpressionParser.ID_INVALID);
	//				joinTableNameAs = joinTableName;
	//			}
	//			schema.setOtherSchema(joinTableNameAs, table2);
	//		} else {
	//			joinTableNameAs = null;
	//			joinTableName = null;
	//			table2 = null;
	//		}
	//		//		com.f1.utils.TypesTuple schema2 = new com.f1.utils.TypesTuple(false, schema, globalVarTypes);
	//		DerivedCellParserContextWrapper context2 = new ChildCalcTypesStack(schema, context);
	//		if (where != null)
	//			DerivedHelper.getDependencyIds(derivedCellParser.toCalc(where.getCondition(), context2), sink);
	//		if (joinTable != null)
	//			DerivedHelper.getDependencyIds(derivedCellParser.toCalc(joinOn, context2), sink);
	//
	//		schema.removeTableKeys(sink);
	//		sink2.addAll(sink);
	//	}
}
