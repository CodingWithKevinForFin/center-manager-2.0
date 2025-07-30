package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.CalcFrame;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Pointer;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.BasicPointer;
import com.f1.utils.sql.SqlProcessorUtils.Limits;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
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

public class SqlProcessor_Update {

	final private SqlProcessor owner;

	public SqlProcessor_Update(SqlProcessor sqlProcessor) {
		this.owner = sqlProcessor;
	}

	public FlowControl processUpdate(CalcFrameStack sf, DerivedCellCalculatorSql query, SqlColumnsNode update, Table table2) {
		final SqlProcessorTableMutator mutator = owner.getMutator();

		final AsNode updateTable = SqlProcessorUtils.toAsNode(update.getColumnAt(0));
		final String updateTableName;
		final String updateTableNameAs;
		MethodFactoryManager mf = sf.getFactory();
		final Table table;
		{
			final Node val = updateTable.getValue();
			if (val instanceof VariableNode) {
				updateTableName = ((VariableNode) val).getVarname();
				table = owner.getMutator().getTable(sf, val.getPosition(), updateTableName, SqlExpressionParser.ID_INVALID);
				updateTableNameAs = updateTable.getAs().toString();
			} else if (val instanceof SqlNode && SqlExpressionParser.isScopeKeyword(((SqlNode) val).getOperation())) {
				SqlNode t = (SqlNode) val;
				updateTableName = ((VariableNode) t.getNext()).getVarname();
				table = owner.getMutator().getTable(sf, val.getPosition(), updateTableName, t.getOperation());
				updateTableNameAs = updateTable.getAs().toString();
			} else {
				throw new ExpressionParserException(updateTable.getPosition(), "Expecting tablename after UPDATE keyword");
			}
		}
		final String joinTable;
		final OperationNode joinOn;
		if (update.getNext() == null)
			throw new ExpressionParserException(update.getPosition(), "Expecting SET clause");
		SqlColumnsNode set = SqlExpressionParser.castNode(update.getNext(), SqlColumnsNode.class);

		int joinType;
		switch (set.getOperation()) {
			case SqlExpressionParser.ID_RIGHT_JOIN:
			case SqlExpressionParser.ID_RIGHT_ONLY_JOIN:
			case SqlExpressionParser.ID_OUTER_JOIN:
			case SqlExpressionParser.ID_OUTER_ONLY_JOIN:
				throw new ExpressionParserException(set.getPosition(),
						"UPDATE only supports JOIN and LEFT JOIN. not: " + SqlExpressionParser.toOperationString(set.getOperation()));
			case SqlExpressionParser.ID_LEFT_ONLY_JOIN:
			case SqlExpressionParser.ID_LEFT_JOIN:
			case SqlExpressionParser.ID_JOIN:
				joinType = set.getOperation();
				joinTable = SqlProcessorUtils.toAsNode(set.getColumnAt(0)).getAs().toString();
				SqlColumnsNode on = JavaExpressionParser.castNode(set.getNext(), SqlColumnsNode.class);
				if (on.getOperation() != SqlExpressionParser.ID_ON)
					throw new ExpressionParserException(on.getPosition(), "after JOIN expresion, Expecting ON");
				joinOn = (OperationNode) on.getColumnAt(0);
				if (on.getNext() == null)
					throw new ExpressionParserException(on.getEndPosition(), "Missing SET after ON expression");
				set = JavaExpressionParser.castNode(on.getNext(), SqlColumnsNode.class);
				break;
			default:
				joinType = -1;
				joinOn = null;
				joinTable = null;
		}
		if (set.getOperation() != SqlExpressionParser.ID_SET)
			throw new ExpressionParserException(set.getPosition(), "Expecting SET not " + SqlExpressionParser.toOperationString(set.getOperation()));

		WhereNode where;
		SqlNode next = JavaExpressionParser.castNodeNotRequired(set.getNext(), SqlNode.class);
		if (next != null && next.getOperation() == SqlExpressionParser.ID_WHERE) {
			where = JavaExpressionParser.castNode(next, WhereNode.class);
			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlNode.class);
		} else
			where = null;

		int offset;
		int limit;
		if (next != null && next.getOperation() == SqlExpressionParser.ID_LIMIT) {
			Limits limits = new Limits(JavaExpressionParser.castNode(next, SqlColumnsNode.class));
			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlNode.class);
			offset = limits.getLimitOffset(this.owner.getParser(), sf);
			limit = limits.getLimit(this.owner.getParser(), sf);
		} else {
			offset = 0;
			limit = -1;
		}
		if (next != null)
			throw new ExpressionParserException(next.getPosition(), "Unexpected Operation in UPDATE clause: " + SqlExpressionParser.toOperationString(next.getOperation()));

		int tableNamePos = updateTable.getPosition();

		TablesCalcFrame tf;

		if (joinTable != null)
			tf = new TablesCalcFrame(new String[] { updateTableNameAs, joinTable }, new Table[] { table, table2 });
		else
			tf = new TablesCalcFrame(new String[] { updateTableNameAs }, new Table[] { table });

		if (set.getColumnsCount() == 0)
			throw new ExpressionParserException(set.getPosition(), "At least one assigment in SET clause required in update cloase ");

		int setsCount = set.getColumnsCount();
		int[] targetColumnPositions = new int[setsCount];
		Caster<?>[] targetColumnCasters = new Caster<?>[setsCount];
		DerivedCellCalculator[] sourceExpressions = new DerivedCellCalculator[setsCount];
		String[] names = new String[setsCount];
		int pos = 0;
		Set<String> visisted = new HashSet<String>();
		CalcTypesStack context2 = new ChildCalcTypesStack(sf, true, tf);
		for (int i = 0; i < set.getColumnsCount(); i++) {
			Node col = set.getColumnAt(i);
			OperationNode op = JavaExpressionParser.castNode(col, OperationNode.class);
			if (op.getOp() != OperationNode.OP_EQ)
				throw new ExpressionParserException(op.getPosition(), "SET expecting assignment operation (=) not: " + op.getOpString());
			VariableNode target = JavaExpressionParser.castNode(op.getLeft(), VariableNode.class);
			names[pos] = target.getVarname();
			Column column = table.getColumnsMap().get(target.getVarname());
			if (column == null)
				throw new ExpressionParserException(target.getPosition(), "Column not found: " + target.getVarname());
			if (!visisted.add(target.getVarname()))
				throw new ExpressionParserException(target.getPosition(), "Duplicate Column: " + target.getVarname());
			Node expression = op.getRight();
			DerivedCellCalculator expressionClause = owner.getParser().toCalc(expression, context2);
			targetColumnPositions[pos] = column.getLocation();
			targetColumnCasters[pos] = column.getTypeCaster();
			sourceExpressions[pos] = expressionClause;
			pos++;
		}

		final DerivedCellCalculator whereClause;
		if (where != null) {
			whereClause = owner.getParser().toCalc(where.getCondition(), context2);
			if (whereClause.getReturnType() != Boolean.class)
				throw new ExpressionParserException(where.getPosition(), "WHERE clause must evaluate to boolean not: " + whereClause.getReturnType());
			DerivedHelper.replaceVarsWithConsts(whereClause, sf, tf);
		} else {
			whereClause = null;
		}

		if (joinTable != null) {
			DerivedCellCalculator joinTo = owner.getParser().toCalc(joinOn, context2);
			if (joinTo.getReturnType() != Boolean.class)
				throw new ExpressionParserException(where.getPosition(), "JOIN ON clause must evaluate to boolean not: " + joinTo.getReturnType());
			Map<String, Table> asTables = new LinkedHashMap<String, Table>();
			asTables.put(updateTableNameAs, table);
			asTables.put(joinTable, table2);
			UpdateProjectionVisitor visitor = new UpdateProjectionVisitor(sourceExpressions, targetColumnCasters);
			SqlProjector.project(query, asTables, names, sourceExpressions, joinType, joinTo, null, whereClause, null, null, null, offset, limit, this.owner, visitor, sf);
			return new TableReturn(mutator.processRowUpdate(sf, table, tableNamePos, visitor.getRows(table), targetColumnPositions, visitor.getValues()));
		}

		return doUpdate(query, mutator, table, offset, limit, tableNamePos, targetColumnPositions, targetColumnCasters, sourceExpressions, whereClause, sf);
	}

	protected FlowControl doUpdate(DerivedCellCalculatorSql query, final SqlProcessorTableMutator mutator, final Table table, int offset, int limit, int tableNamePos,
			int[] targetColumnPositions, Caster<?>[] targetColumnCasters, DerivedCellCalculator[] sourceExpressions, DerivedCellCalculator whereClause, CalcFrameStack sf) {
		CalcFrame globalVars = sf.getGlobal();
		List<Row> toUpdate = null;
		if (whereClause == null) {
			if (offset == 0 && limit == -1)
				toUpdate = table.getRows();
			else {
				int size = table.getSize();
				if (offset < size) {
					if (limit > size - offset)
						limit = size - offset;
					if (offset == 0 && limit == size) {
						toUpdate = table.getRows();
					} else if (limit > 0) {
						toUpdate = new ArrayList<Row>(limit);
						TableList rows = table.getRows();
						for (int n = offset, end = offset + limit; n < end; n++)
							toUpdate.add(rows.get(n));
					}
				}
			}

		} else {
			if (offset < table.getSize() && limit != 0) {
				//				RowGetter rg = new RowGetter(table, globalVars);
				Pointer<DerivedCellCalculator> pWhereClause = new BasicPointer<DerivedCellCalculator>(whereClause);
				Iterable<Row> rows = mutator.applyIndexes(sf, null, table, pWhereClause, limit == -1 ? Integer.MAX_VALUE : limit);
				whereClause = pWhereClause.get();
				ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
				for (Row row : rows) {
					rsf.reset(row);
					if (Boolean.TRUE.equals(whereClause.get(rsf))) {
						if (offset > 0) {
							offset--;
							continue;
						}
						if (toUpdate == null)
							toUpdate = new ArrayList<Row>();
						toUpdate.add(row);
						if (limit != -1 && toUpdate.size() == limit)
							break;
					}
				}
			}
		}
		int setsCount = targetColumnCasters.length;
		if (toUpdate != null) {
			Object tmp[][] = new Object[toUpdate.size()][setsCount];

			//			if (!globalVars.getTypes().isEmpty()) {
			ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
			//				RowGetter row = new RowGetter(table, globalVars);
			for (int n = 0, l = toUpdate.size(); n < l; n++) {
				rsf.reset(toUpdate.get(n));
				for (int i = 0; i < setsCount; i++)
					tmp[n][i] = targetColumnCasters[i].cast(sourceExpressions[i].get(rsf), false, false);
			}
			//			} else {
			//				for (int n = 0, l = toUpdate.size(); n < l; n++) {
			//					Row row = toUpdate.get(n);
			//					for (int i = 0; i < setsCount; i++)
			//						tmp[n][i] = targetColumnCasters[i].cast(sourceExpressions[i].get(sf, row), false, false);
			//				}
			//			}
			return new TableReturn(mutator.processRowUpdate(sf, table, tableNamePos, toUpdate, targetColumnPositions, tmp));
		} else {
			Object tmp[][] = new Object[0][0];
			mutator.processRowUpdate(sf, table, tableNamePos, Collections.EMPTY_LIST, targetColumnPositions, tmp);
			return TableReturn.EMPTY;
		}
	}

	//	public void getDependencies(DerivedCellParserContext context, SqlColumnsNode update, Set<Object> sink2) {
	//		HashSet sink = new HashSet();
	//
	//		final AsNode updateTable = SqlProcessorUtils.toAsNode(update.getColumnAt(0));
	//		final String updateTableName;
	//		final Table table;
	//		{
	//			final Node val = updateTable.getValue();
	//			if (val instanceof VariableNode) {
	//				updateTableName = ((VariableNode) val).getVarname();
	//				table = owner.getMutator().getTable(context, val.getPosition(), updateTableName, SqlExpressionParser.ID_INVALID);
	//			} else if (val instanceof SqlNode && SqlExpressionParser.isScopeKeyword(((SqlNode) val).getOperation())) {
	//				SqlNode t = (SqlNode) val;
	//				updateTableName = ((VariableNode) t.getNext()).getVarname();
	//				table = owner.getMutator().getTable(context, val.getPosition(), updateTableName, t.getOperation());
	//			} else {
	//				updateTableName = val.toString();
	//				table = owner.getMutator().getTable(context, val.getPosition(), updateTableName, SqlExpressionParser.ID_INVALID);
	//			}
	//		}
	//		final AsNode joinTable;
	//		final OperationNode joinOn;
	//		if (update.getNext() == null)
	//			throw new ExpressionParserException(update.getPosition(), "Expecting SET clause");
	//		SqlColumnsNode set = SqlExpressionParser.castNode(update.getNext(), SqlColumnsNode.class);
	//
	//		switch (set.getOperation()) {
	//			case SqlExpressionParser.ID_RIGHT_JOIN:
	//			case SqlExpressionParser.ID_RIGHT_ONLY_JOIN:
	//			case SqlExpressionParser.ID_OUTER_JOIN:
	//			case SqlExpressionParser.ID_OUTER_ONLY_JOIN:
	//				throw new ExpressionParserException(set.getPosition(),
	//						"UPDATE only supports JOIN and LEFT JOIN. not: " + SqlExpressionParser.toOperationString(set.getOperation()));
	//			case SqlExpressionParser.ID_LEFT_ONLY_JOIN:
	//			case SqlExpressionParser.ID_LEFT_JOIN:
	//			case SqlExpressionParser.ID_JOIN:
	//				joinTable = SqlProcessorUtils.toAsNode(set.getColumnAt(0));
	//				SqlColumnsNode on = JavaExpressionParser.castNode(set.getNext(), SqlColumnsNode.class);
	//				if (on.getOperation() != SqlExpressionParser.ID_ON)
	//					throw new ExpressionParserException(on.getPosition(), "after JOIN expresion, Expecting ON");
	//				joinOn = (OperationNode) on.getColumnAt(0);
	//				if (on.getNext() == null)
	//					throw new ExpressionParserException(on.getEndPosition(), "Missing SET after ON expression");
	//				set = JavaExpressionParser.castNode(on.getNext(), SqlColumnsNode.class);
	//				break;
	//			default:
	//				joinOn = null;
	//				joinTable = null;
	//		}
	//		if (set.getOperation() != SqlExpressionParser.ID_SET)
	//			throw new ExpressionParserException(set.getPosition(), "Expecting SET not " + SqlExpressionParser.toOperationString(set.getOperation()));
	//
	//		WhereNode where;
	//		SqlNode next = JavaExpressionParser.castNodeNotRequired(set.getNext(), SqlNode.class);
	//		if (next != null && next.getOperation() == SqlExpressionParser.ID_WHERE) {
	//			where = JavaExpressionParser.castNode(next, WhereNode.class);
	//			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlNode.class);
	//		} else
	//			where = null;
	//
	//		if (next != null && next.getOperation() == SqlExpressionParser.ID_LIMIT) {
	//			Limits limits = new Limits(JavaExpressionParser.castNode(next, SqlColumnsNode.class));
	//			limits.getDependencyIds(context, this.owner.getParser(), sink);
	//			next = JavaExpressionParser.castNodeNotRequired(next.getNext(), SqlNode.class);
	//		}
	//		if (next != null)
	//			throw new ExpressionParserException(next.getPosition(), "Unexpected Operation in UPDATE clause: " + SqlExpressionParser.toOperationString(next.getOperation()));
	//
	//		TypesGetter schema = new SqlProcessorUtils.TypesGetter(updateTableName, table, context.getGlobalVarTypes());
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
	//
	//		DerivedCellParserContextWrapper context2 = new ChildCalcTypesStack(schema, context);
	//		for (Node col : set.getColumns()) {
	//			OperationNode op = JavaExpressionParser.castNode(col, OperationNode.class);
	//			if (OH.ne("=", op.getOperation()))
	//				throw new ExpressionParserException(op.getPosition(), "SET expecting assignment operation (=) not: " + op.getOperation());
	//			Node expression = op.getRight();
	//			DerivedHelper.getDependencyIds(owner.getParser().toCalc(expression, context2), sink);
	//		}
	//
	//		if (where != null)
	//			DerivedHelper.getDependencyIds(owner.getParser().toCalc(where.getCondition(), context2), sink);
	//
	//		if (joinTable != null)
	//			DerivedHelper.getDependencyIds(owner.getParser().toCalc(joinOn, context2), sink);
	//		schema.removeTableKeys(sink);
	//		sink2.addAll(sink);
	//	}
}
