package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.CalcTypes;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.ArrayNode;
import com.f1.utils.string.node.BlockNode;
import com.f1.utils.string.node.DeclarationNode;
import com.f1.utils.string.node.ExpressionNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.AdminNode;
import com.f1.utils.string.sqlnode.AlterColumnNode;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.CreateTableNode;
import com.f1.utils.string.sqlnode.SqlColumnDefNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlOperationNode;
import com.f1.utils.string.sqlnode.UseNode;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorBlock;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class SqlProcessor_Admin {
	/*
	 * 
	 * Processes sql in datamodel amiscript, e.g. create table Sample ...
	 *
	 */

	final private SqlProcessor owner;

	public SqlProcessor_Admin(SqlProcessor sqlProcessor) {
		this.owner = sqlProcessor;
	}

	protected void processCreateTrigger(CalcFrameStack sf, AdminNode node, SqlDerivedCellParser dcp) {
		SqlOperationNode triggerNode = JavaExpressionParser.castNode(node.getNext(), SqlOperationNode.class);
		SqlOperationNode typeNode = JavaExpressionParser.castNode(triggerNode.getNext(), SqlOperationNode.class);
		SqlColumnsNode tableNameNode = JavaExpressionParser.castNode(typeNode.getNext(), SqlColumnsNode.class);
		SqlOperationNode priorityNode = tableNameNode.getNext() == null ? null : JavaExpressionParser.castNode(tableNameNode.getNext(), SqlOperationNode.class);
		int priority;
		try {
			priority = priorityNode == null ? 0 : ((Number) SH.parseConstant(priorityNode.getNameAsString())).intValue();
		} catch (Exception e) {
			throw new ExpressionParserException(priorityNode.getPosition(), "Not a valid number: " + priorityNode.getNameAsString(), e);
		}
		Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : Collections.EMPTY_MAP;
		String triggerName = triggerNode.getNameAsString();
		int triggerNamePos = triggerNode.getPosition();
		String typeName = typeNode.getNameAsString();
		int typeNamePos = typeNode.getPosition();
		//		Node[] columns = tableNameNode.columns;
		String tableNames[] = new String[tableNameNode.getColumnsCount()];
		int tableNamePos[] = new int[tableNameNode.getColumnsCount()];
		for (int i = 0; i < tableNames.length; i++) {
			tableNames[i] = JavaExpressionParser.castNode(tableNameNode.getColumnAt(i), VariableNode.class).getVarname();
			tableNamePos[i] = tableNameNode.getColumnAt(i).getPosition();
		}
		this.owner.getMutator().processTriggerCreate(sf, triggerName, triggerNamePos, typeName, typeNamePos, tableNames, tableNamePos, priority, useOptions,
				node.getIfNotExistsOrThrow());
	}
	protected void processCreateTimer(CalcFrameStack sf, AdminNode node, SqlDerivedCellParser dcp) {
		SqlOperationNode timerNode = JavaExpressionParser.castNode(node.getNext(), SqlOperationNode.class);
		SqlOperationNode typeNode = JavaExpressionParser.castNode(timerNode.getNext(), SqlOperationNode.class);
		SqlOperationNode onNode = JavaExpressionParser.castNode(typeNode.getNext(), SqlOperationNode.class);
		SqlOperationNode priorityNode = onNode.getNext() == null ? null : JavaExpressionParser.castNode(onNode.getNext(), SqlOperationNode.class);
		int priority;
		try {
			priority = priorityNode == null ? 0 : ((Number) SH.parseConstant(priorityNode.getNameAsString())).intValue();
		} catch (Exception e) {
			throw new ExpressionParserException(priorityNode.getPosition(), "Not a valid number: " + priorityNode.getNameAsString(), e);
		}
		Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : Collections.EMPTY_MAP;
		String timerName = timerNode.getNameAsString();
		int timerNamePos = timerNode.getPosition();
		String typeName = typeNode.getNameAsString();
		int typeNamePos = typeNode.getPosition();
		String on = onNode.getNameAsString();
		int onPos = onNode.getPosition();
		this.owner.getMutator().processTimerCreate(sf, timerName, timerNamePos, typeName, typeNamePos, priority, on, onPos, useOptions, node.getIfNotExistsOrThrow());
	}
	protected void processCreateDbo(CalcFrameStack sf, AdminNode node, SqlDerivedCellParser dcp) {
		SqlOperationNode dboNode = JavaExpressionParser.castNode(node.getNext(), SqlOperationNode.class);
		SqlOperationNode typeNode = JavaExpressionParser.castNode(dboNode.getNext(), SqlOperationNode.class);
		SqlOperationNode priorityNode = typeNode.getNext() == null ? null : JavaExpressionParser.castNode(typeNode.getNext(), SqlOperationNode.class);
		int priority;
		try {
			priority = priorityNode == null ? 0 : ((Number) SH.parseConstant(priorityNode.getNameAsString())).intValue();
		} catch (Exception e) {
			throw new ExpressionParserException(priorityNode.getPosition(), "Not a valid number: " + priorityNode.getNameAsString(), e);
		}
		Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : Collections.EMPTY_MAP;
		String dboName = dboNode.getNameAsString();
		int dboNamePos = dboNode.getPosition();
		String typeName = typeNode.getNameAsString();
		int typeNamePos = typeNode.getPosition();
		this.owner.getMutator().processDboCreate(sf, dboName, dboNamePos, typeName, typeNamePos, priority, useOptions, node.getIfNotExistsOrThrow());
	}
	protected void processCreateProcedure(CalcFrameStack sf, AdminNode node, SqlDerivedCellParser dcp) {
		SqlOperationNode triggerNode = JavaExpressionParser.castNode(node.getNext(), SqlOperationNode.class);
		SqlOperationNode typeNode = JavaExpressionParser.castNode(triggerNode.getNext(), SqlOperationNode.class);
		Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : Collections.EMPTY_MAP;
		String procedureName = triggerNode.getNameAsString();
		int procedureNamePos = triggerNode.getPosition();
		String typeName = typeNode.getNameAsString();
		int typeNamePos = typeNode.getPosition();
		this.owner.getMutator().processProcedureCreate(sf, procedureName, procedureNamePos, typeName, typeNamePos, useOptions, node.getIfNotExistsOrThrow());
	}
	protected void processCreateMethod(CalcFrameStack sf, AdminNode node, SqlDerivedCellParser dcp) {
		BlockNode bn = (BlockNode) node.getNext();
		DerivedCellCalculatorBlock calc = (DerivedCellCalculatorBlock) dcp.toCalc(bn, sf);
		List<MethodFactory> sink = new ArrayList<MethodFactory>();
		calc.getMethodFactory().getMethodFactories(sink);
		//		OH.assertEq(sink.size(), 1);
		boolean ifNotExists = node.getIfNotExistsOrThrow();
		this.owner.getMutator().processMethodCreate(sf, node.getNext().getPosition(), sink, ifNotExists);
	}
	public void processDropMethod(CalcFrameStack sf, AdminNode an, SqlDerivedCellParser dcp) {
		MethodNode mn = (MethodNode) an.getNext();
		boolean ifExists = an.getIfExistsOrThrow();
		Class[] types = new Class[mn.getParamsCount()];
		for (int i = 0; i < mn.getParamsCount(); i++) {
			Node node = mn.getParamAt(i);
			String varname;
			if (node instanceof DeclarationNode) {
				varname = ((DeclarationNode) node).getVartype();
			} else if (node instanceof VariableNode) {
				varname = ((VariableNode) node).getVarname();
			} else
				throw new ExpressionParserException(node.getPosition(), "Expecting argument type");
			Class<?> cl = sf.getFactory().forNameNoThrow(varname);
			if (cl == null)
				throw new ExpressionParserException(node.getPosition(), "Unknown type: " + varname);
			types[i] = cl;
		}
		this.owner.getMutator().processMethodDrop(sf, mn.getPosition(), mn.getMethodName(), types, ifExists);
	}
	protected Table processCreateIndex(CalcFrameStack sf, AdminNode node, SqlDerivedCellParser dcp) {
		final int scope = getScope(node);
		SqlOperationNode dn = JavaExpressionParser.castNode(node.getNext(), SqlOperationNode.class);
		MethodNode parts = JavaExpressionParser.castNode(dn.getNext(), MethodNode.class);
		final int paramsCount = parts.getParamsCount();
		String indexName = dn.getNameAsString();
		int indexNamePos = dn.getPosition();
		String tableName = parts.getMethodName();
		int tableNamePos = parts.getPosition();
		String colName[] = new String[paramsCount];
		String colType[] = new String[paramsCount];
		int colPos[] = new int[paramsCount];
		for (int i = 0; i < paramsCount; i++) {
			Node p = parts.getParamAt(i);
			while (p instanceof ExpressionNode)
				p = ((ExpressionNode) p).getValue();
			colPos[i] = p.getPosition();
			if (p instanceof DeclarationNode) {
				DeclarationNode param = JavaExpressionParser.castNode(p, DeclarationNode.class);
				colName[i] = param.getVartype();
				colType[i] = param.getVarname();
			} else if (p instanceof VariableNode) {
				VariableNode param = JavaExpressionParser.castNode(p, VariableNode.class);
				colName[i] = param.getVarname();
				colType[i] = null;
			} else
				throw new ExpressionParserException(p.getPosition(), "Invalid column reference");
		}
		Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : null;
		this.owner.getMutator().processIndexCreate(sf, indexName, indexNamePos, tableName, tableNamePos, colName, colType, colPos, useOptions, node.getIfNotExistsOrThrow(), scope);
		return null;
	}
	protected TableReturn processCreateTable(CalcFrameStack sf, DerivedCellCalculatorSql query, final CreateTableNode nodes, List<Table> tables, SqlDerivedCellParser dcp) {
		AdminNode[] tableDefs = nodes.getTableDefs();
		if (tables != null && tables.size() != tableDefs.length)
			throw new ExpressionParserException(nodes.getPosition(), "EXECUTE AS clause Expecting " + tables.size() + " table(s), not: " + tableDefs.length);
		int rowsInserted = 0;
		for (int n = 0; n < tableDefs.length; n++) {
			AdminNode node = tableDefs[n];
			final int scope = getScope(node);
			if (node.getTargetType() != SqlExpressionParser.ID_TABLE)
				throw new ExpressionParserException(node.getPosition(), "Operation not supported: CREATE " + SqlExpressionParser.toOperationString(node.getTargetType()));
			boolean ifNotExists = node.getIfNotExistsOrThrow();
			if (node.getNext() instanceof SqlOperationNode) {//CREATE TABLE ... AS <query>
				final SqlOperationNode as = (SqlOperationNode) node.getNext();
				Table t = (Table) tables.get(n);
				String name = as.getNameAsString();
				if (ifNotExists && this.owner.getMutator().getTableIfExists(sf, name, scope) != null)
					continue;
				t.setTitle(name);
				Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : null;
				this.owner.getMutator().processTableAdd(sf, name, as.getName().getPosition(), t, useOptions, scope, ifNotExists);
				rowsInserted += t.getSize();
			} else if (node.getNext() instanceof VariableNode) {
				VariableNode as = (VariableNode) node.getNext();
				String name = as.getVarname();
				if (ifNotExists && this.owner.getMutator().getTableIfExists(sf, name, scope) != null)
					continue;
				Table t = (Table) tables.get(n);
				t.setTitle(name);
				Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : null;
				this.owner.getMutator().processTableAdd(sf, name, as.getPosition(), t, useOptions, scope, ifNotExists);
				rowsInserted += t.getSize();
			} else if (node.getNext() instanceof OperationNode && ((OperationNode) (node.getNext())).getOp() == OperationNode.OP_EQ) {
				OperationNode on = (OperationNode) node.getNext();
				String tableName = on.getLeft().toString();
				if (ifNotExists && this.owner.getMutator().getTableIfExists(sf, tableName, scope) != null)
					return null;
				Node value = on.getRight();
				DerivedCellCalculator expression = this.owner.getParser().processNode(value, sf);
				if (!Table.class.isAssignableFrom(expression.getReturnType()))
					throw new ExpressionParserException(node.getNext().getPosition(), "Table assignment can not cast " + sf.getFactory().forType(expression.getReturnType()));

				Table table = (Table) expression.get(sf);
				if (table == null)
					return null;
				table = new ColumnarTable(table);
				Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : null;
				this.owner.getMutator().processTableAdd(sf, tableName, node.getPosition(), table, useOptions, scope, ifNotExists);
				return null;
			} else {
				//CREATE TABLE .... (.....)
				MethodNode def = (MethodNode) node.getNext();
				String name = def.getMethodName();
				if (ifNotExists && this.owner.getMutator().getTableIfExists(sf, name, scope) != null)
					continue;
				int size = def.getParamsCount();
				if (size == 0)
					throw new ExpressionParserException(def.getPosition(), "Empty column set");
				String[] types = new String[size];
				String[] names = new String[size];
				Map<String, Node>[] uses = new Map[size];
				int[] colDefPos = new int[size];
				for (int i = 0; i < size; i++) {
					SqlColumnDefNode param = JavaExpressionParser.castNode(def.getParamAt(i), SqlColumnDefNode.class);
					types[i] = param.getType().getVarname();
					names[i] = param.getName().getVarname();
					uses[i] = param.getUse() == null ? null : param.getUse().getOptionsMap();
					colDefPos[i] = param.getPosition();
				}
				Map<String, Node> useOptions = node.getUseNode() != null ? node.getUseNode().getOptionsMap() : null;
				Table r = this.owner.getMutator().processTableAdd(sf, name, def.getPosition(), types, names, uses, colDefPos, useOptions, scope, ifNotExists);
				if (tables != null) {
					Table t = tables.get(n);
					if (t.getColumnsCount() != names.length)
						throw new ExpressionParserException(node.getPosition(),
								"Wrong number of columns in table definition vs AS clause result: " + names.length + " vs " + t.getColumnsCount());
					int columnsCount = r.getColumnsCount();
					int[] positions = new int[columnsCount];
					for (int i = 0; i < positions.length; i++)
						positions[i] = i;
					Object[][] rows = new Object[t.getSize()][];
					TableList toInserts = t.getRows();
					for (int y = 0; y < rows.length; y++) {
						Row sourceRow = toInserts.get(y);
						Object[] row = rows[y] = new Object[columnsCount];
						for (int x = 0; x < columnsCount; x++) {
							Object value = sourceRow.getAt(x);
							row[x] = r.getColumnAt(x).getTypeCaster().cast(value, false, false);
						}
					}
					this.owner.getMutator().processRowAdds(sf, r, def.getPosition(), positions, rows, false);
					rowsInserted += rows.length;
				}
			}
		}

		return new TableReturn(rowsInserted);
	}
	static public int getScope(AdminNode node) {
		if (node.getOptions() != null && SqlExpressionParser.isScopeKeyword(node.getOptions().getOperation()))
			return node.getOptions().getOperation();
		else
			return SqlExpressionParser.ID_INVALID;
	}

	public Table processDropTable(CalcFrameStack sf, AdminNode node) {
		ArrayNode names = JavaExpressionParser.castNode(node.getNext(), ArrayNode.class);
		Table r = null;
		//		List<Node> params = names.params;
		if (!node.getIfExistsOrThrow() && names.getParamsCount() > 1) {
			Set<String> visited = new HashSet<String>();
			for (int i = 0, l = names.getParamsCount(); i < l; i++) {
				Node nameNode = names.getParamAt(i);
				String name = nameNode.toString();
				final int scope = getScope(node);
				if (r == this.owner.getMutator().getTableIfExists(sf, name, scope))
					throw new ExpressionParserException(nameNode.getPosition(), "Table not found: " + name);
				if (!visited.add(name))
					throw new ExpressionParserException(nameNode.getPosition(), "Duplicate Table: " + name);
			}
		}
		for (int i = 0, l = names.getParamsCount(); i < l; i++) {
			Node nameNode = names.getParamAt(i);
			String name = nameNode.toString();
			final int scope = getScope(node);
			r = this.owner.getMutator().processTableRemove(sf, name, node.getPosition(), scope, node.getIfExistsOrThrow());
		}
		return r;
	}
	public Table processDropIndex(CalcFrameStack sf, AdminNode node) {
		final int scope = getScope(node);
		ArrayNode indexNames = JavaExpressionParser.castNode(node.getNext(), ArrayNode.class);
		for (int i = 0, l = indexNames.getParamsCount(); i < l; i++) {
			Node indexNameNode = indexNames.getParamAt(i);
			SqlOperationNode in = JavaExpressionParser.castNode(indexNameNode, SqlOperationNode.class);
			String indexName = in.getNameAsString();
			int indexNamePos = in.getPosition();
			VariableNode vn = JavaExpressionParser.castNode(in.getNext(), VariableNode.class);
			String tableName = vn.getVarname();
			int tableNamePos = vn.getPosition();
			this.owner.getMutator().processIndexRemove(sf, tableName, tableNamePos, indexName, indexNamePos, node.getIfExistsOrThrow(), scope);
		}
		return null;
	}
	public Table processDropTrigger(CalcFrameStack sf, AdminNode node) {
		ArrayNode triggerNames = JavaExpressionParser.castNode(node.getNext(), ArrayNode.class);
		for (int i = 0, l = triggerNames.getParamsCount(); i < l; i++) {
			Node triggerNameNode = triggerNames.getParamAt(i);
			String triggerName = triggerNameNode.toString();
			int triggerNamePos = triggerNameNode.getPosition();
			String tableName = null;
			int tableNamePos = -1;
			this.owner.getMutator().processTriggerRemove(sf, tableName, tableNamePos, triggerName, triggerNamePos, node.getIfExistsOrThrow());
		}
		return null;
	}
	public Table processDropTimer(CalcFrameStack sf, AdminNode node) {
		ArrayNode timerNames = JavaExpressionParser.castNode(node.getNext(), ArrayNode.class);
		for (int i = 0, l = timerNames.getParamsCount(); i < l; i++) {
			Node timerNameNode = timerNames.getParamAt(i);
			String timerName = timerNameNode.toString();
			int timerNamePos = timerNameNode.getPosition();
			this.owner.getMutator().processTimerRemove(sf, timerName, timerNamePos, node.getIfExistsOrThrow());
		}
		return null;
	}
	public Table processDropDbo(CalcFrameStack sf, AdminNode node) {
		ArrayNode dboNames = JavaExpressionParser.castNode(node.getNext(), ArrayNode.class);
		for (int i = 0, l = dboNames.getParamsCount(); i < l; i++) {
			Node dboNameNode = dboNames.getParamAt(i);
			String dboName = dboNameNode.toString();
			int dboNamePos = dboNameNode.getPosition();
			this.owner.getMutator().processDboRemove(sf, dboName, dboNamePos, node.getIfExistsOrThrow());
		}
		return null;
	}
	public Table processDropProcedure(CalcFrameStack sf, AdminNode node) {
		ArrayNode procedureNames = JavaExpressionParser.castNode(node.getNext(), ArrayNode.class);
		for (int i = 0, l = procedureNames.getParamsCount(); i < l; i++) {
			Node procedureNameNode = procedureNames.getParamAt(i);
			String procedureName = procedureNameNode.toString();
			int procedureNamePos = procedureNameNode.getPosition();
			this.owner.getMutator().processProcedureRemove(sf, procedureName, procedureNamePos, node.getIfExistsOrThrow());
		}
		return null;
	}
	public void processEnable(CalcFrameStack sf, AdminNode an) {
		boolean enable;
		switch (an.getOperation()) {
			case SqlExpressionParser.ID_ENABLE:
				enable = true;
				break;
			case SqlExpressionParser.ID_DISABLE:
				enable = false;
				break;
			default:
				throw new ExpressionParserException(an.getOperation(), "Expecting ENABLED or DISABLED: " + SqlExpressionParser.toOperationString(an.getOperation()));
		}
		ArrayNode arrayNode = JavaExpressionParser.castNode(an.getNext(), ArrayNode.class);
		//		List<Node> nameNodes = JavaExpressionParser.castNode(an.next, ArrayNode.class).params;
		String[] names = new String[arrayNode.getParamsCount()];
		int[] namePositions = new int[arrayNode.getParamsCount()];
		for (int i = 0; i < names.length; i++) {
			VariableNode vn = JavaExpressionParser.castNode(arrayNode.getParamAt(i), VariableNode.class);
			names[i] = vn.getVarname();
			namePositions[i] = vn.getPosition();
		}
		this.owner.getMutator().processEnabled(sf, enable, an.getPosition(), SqlExpressionParser.toOperationString(an.getTargetType()), names, namePositions);
	}
	public Table processRename(CalcFrameStack sf, AdminNode node) {
		final SqlProcessorTableMutator mutator = owner.getMutator();
		AsNode as = JavaExpressionParser.castNode(node.getNext(), AsNode.class);
		String from = JavaExpressionParser.castNode(as.getValue(), VariableNode.class).getVarname();
		String to = JavaExpressionParser.castNode(as.getAs(), VariableNode.class).getVarname();
		if (OH.eq(from, to))
			throw new ExpressionParserException(node.getPosition(), "To name and From name are the same");
		final int scope = getScope(node);
		switch (node.getTargetType()) {
			case SqlExpressionParser.ID_TABLE:
				return mutator.processTableRename(sf, as.getPosition(), from, as.getAs().getPosition(), to, scope);
			case SqlExpressionParser.ID_TRIGGER:
				mutator.processTriggerRename(sf, as.getPosition(), from, as.getAs().getPosition(), to, scope);
				return null;
			case SqlExpressionParser.ID_TIMER:
				mutator.processTimerRename(sf, as.getPosition(), from, as.getAs().getPosition(), to, scope);
				return null;
			case SqlExpressionParser.ID_PROCEDURE:
				mutator.processProcedureRename(sf, as.getPosition(), from, as.getAs().getPosition(), to, scope);
				return null;
			case SqlExpressionParser.ID_DBO:
				mutator.processDboRename(sf, as.getPosition(), from, as.getAs().getPosition(), to, scope);
				return null;
			default:
				throw new ExpressionParserException(node.getPosition(), "Operation not supported: RENAME " + SqlExpressionParser.toOperationString(node.getTargetType()));
		}
	}
	public Table processAlterUse(CalcFrameStack sf, AdminNode node) {
		if (node.getTargetType() != SqlExpressionParser.ID_DBO)
			throw new ExpressionParserException(node.getPosition(), "Expecting: DBO");
		MethodNode alters = JavaExpressionParser.castNode(node.getNext(), MethodNode.class);
		String name = alters.getMethodName();
		int altersCount = alters.getParamsCount();
		for (int alterPos = 0; alterPos < altersCount; alterPos++) {
			AlterColumnNode alter = JavaExpressionParser.castNode(alters.getParamAt(alterPos), AlterColumnNode.class);
			switch (alter.getType()) {
				case SqlExpressionParser.ID_USE:
					UseNode useOptions = alter.getUseOptions();
					this.owner.getMutator().processAlterUseOptions(sf, node.getTargetType(), name, node.getPosition(), useOptions.getOptionsMap());
					break;
				default:
					throw new ExpressionParserException(alter.getPosition(), "Invalid ALTER column type: " + SqlExpressionParser.toOperationString(alter.getType()));
			}
		}
		return null;
	}
	public Table processAlterTable(CalcFrameStack sf, AdminNode node) {
		final SqlProcessorTableMutator mutator = owner.getMutator();
		if (node.getTargetType() != SqlExpressionParser.ID_TABLE)
			throw new ExpressionParserException(node.getPosition(), "Expecting: TABLE");
		final int scope = getScope(node);
		MethodNode alters = JavaExpressionParser.castNode(node.getNext(), MethodNode.class);
		String name = alters.getMethodName();
		int tableNamePos = alters.getPosition();
		Table r = this.owner.getMutator().getTable(sf, tableNamePos, name, scope);
		Set<Object> postColumnNames = new HashSet<Object>(r.getColumnsMap().keySet());

		int altersCount = alters.getParamsCount();
		DerivedCellCalculator addCalcs[] = new DerivedCellCalculator[altersCount];
		CalcTypes types2 = SqlProcessorUtils.toTypes(sf, r);
		CalcTypesStack context2 = new ChildCalcTypesStack(sf, true, types2);
		for (int alterPos = 0; alterPos < altersCount; alterPos++) {
			AlterColumnNode alter = JavaExpressionParser.castNode(alters.getParamAt(alterPos), AlterColumnNode.class);
			switch (alter.getType()) {
				case SqlExpressionParser.ID_RENAME:
				case SqlExpressionParser.ID_MODIFY:
					if (!postColumnNames.remove(alter.getColName().getVarname()))
						throw new ExpressionParserException(alter.getColName().getPosition(), "Column not found: " + alter.getColName().getVarname());
					if (!postColumnNames.add(alter.getNewName().getVarname()))
						throw new ExpressionParserException(alter.getNewName().getPosition(), "Duplicate Column Name: " + alter.getNewName().getVarname());
					break;
				case SqlExpressionParser.ID_DROP:
					if (!postColumnNames.remove(alter.getColName().getVarname()))
						throw new ExpressionParserException(alter.getColName().getPosition(), "Column not found: " + alter.getColName().getVarname());
					break;
				case SqlExpressionParser.ID_ADD:
					if (!postColumnNames.add(alter.getNewName().getVarname()))
						throw new ExpressionParserException(alter.getNewName().getPosition(), "Duplicate Column Name: " + alter.getNewName().getVarname());
					if (alter.getBefore() != null && !postColumnNames.contains(alter.getBefore().getVarname()))
						throw new ExpressionParserException(alter.getNewName().getPosition(), "Column not found: " + alter.getBefore().getVarname());
					if (alter.getNewType() != null)
						try {
							sf.getFactory().forName(alter.getNewType().getVarname());
						} catch (ClassNotFoundException e) {
							throw new ExpressionParserException(alter.getPosition(), "Invalid type: " + alter.getNewType().getVarname());
						}
					if (alter.getExpression() != null)
						addCalcs[alterPos] = this.owner.getParser().processNode(alter.getExpression(), context2);
					break;
				default:
					throw new ExpressionParserException(alter.getPosition(), "Invalid ALTER column type: " + SqlExpressionParser.toOperationString(alter.getType()));
			}
		}
		Object addVals[][] = new Object[altersCount][];
		for (int col = 0; col < altersCount; col++) {
			DerivedCellCalculator val = addCalcs[col];
			if (val != null) {
				ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
				//								RowGetter vars = new RowGetter(r, sf.getGlobalVars());
				Object vals[] = new Object[r.getSize()];
				for (Row row : r.getRows()) {
					vals[row.getLocation()] = val.get(rsf.reset(row));
				}
				addVals[col] = vals;
			}
		}
		for (int alterPos = 0; alterPos < altersCount; alterPos++) {
			AlterColumnNode alter = JavaExpressionParser.castNode(alters.getParamAt(alterPos), AlterColumnNode.class);
			switch (alter.getType()) {
				case SqlExpressionParser.ID_MODIFY:
				case SqlExpressionParser.ID_RENAME: {
					Column col = r.getColumn(alter.getColName().getVarname());
					String newName = OH.ne(alter.getColName().getVarname(), alter.getNewName().getVarname()) ? alter.getNewName().getVarname() : null;
					String newType = alter.getNewType() != null ? alter.getNewType().getVarname() : null;
					Map<String, Node> options = alter.getUseOptions() != null ? alter.getUseOptions().getOptionsMap() : null;
					int pos = alter.getNewName().getPosition();
					mutator.processColumnChangeType(sf, tableNamePos, name, col.getLocation(), col.getType(), pos, newType, newName, options, scope);
					break;
				}
				case SqlExpressionParser.ID_DROP:
					mutator.processColumnRemove(sf, tableNamePos, name, alter.getColName().getVarname(), alter.getColName().getPosition(), scope);
					break;
				case SqlExpressionParser.ID_ADD:
					int position = alter.getBefore() != null ? r.getColumn(alter.getBefore().getVarname()).getLocation() : r.getColumnsCount();
					int typePos;
					String type;
					if (alter.getNewType() != null) {
						typePos = alter.getNewType().getPosition();
						type = alter.getNewType().getVarname();
					} else {
						typePos = addCalcs[alterPos].getPosition();
						type = sf.getFactory().forType(addCalcs[alterPos].getReturnType());
					}
					mutator.processColumnAdd(sf, tableNamePos, name, typePos, type, alter.getNewName().getVarname(), position, scope,
							alter.getUseOptions() == null ? null : alter.getUseOptions().getOptionsMap(), addVals[alterPos]);
					break;
				default:
					throw new ExpressionParserException(alter.getPosition(), "Invalid ALTER column type: " + SqlExpressionParser.toOperationString(alter.getType()));
			}
		}

		return r;
	}

}
