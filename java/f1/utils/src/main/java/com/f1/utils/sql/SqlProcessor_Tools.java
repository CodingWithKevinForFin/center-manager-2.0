package com.f1.utils.sql;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.sql.SqlProcessorUtils.Limits;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.ArrayNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.AdminNode;
import com.f1.utils.string.sqlnode.SqlCallNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.SqlOperationNode;
import com.f1.utils.string.sqlnode.SqlShowNode;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class SqlProcessor_Tools {

	final private SqlProcessor owner;

	public SqlProcessor_Tools(SqlProcessor sqlProcessor) {
		this.owner = sqlProcessor;
	}

	public FlowControl processCall(CalcFrameStack sf, DerivedCellCalculatorSql query, SqlNode an) {
		SqlCallNode mn = JavaExpressionParser.castNode(an, SqlCallNode.class);
		String name = mn.getMethodName();
		int namePos = mn.getPosition();
		int size = mn.getParamsCount();
		Object params[] = new Object[size];
		int paramsPos[] = new int[size];
		for (int i = 0; i < size; i++) {
			Node node = mn.getParamAt(i);
			params[i] = owner.getParser().toCalc(node, sf).get(sf);
			paramsPos[i] = node.getPosition();
		}
		final int limitOffset;
		final int limit;
		if (mn.getNext() != null) {
			final Limits limits = new Limits(JavaExpressionParser.castNode(mn.getNext(), SqlColumnsNode.class));
			limitOffset = limits.getLimitOffset(this.owner.getParser(), sf);
			limit = limits.getLimit(this.owner.getParser(), sf);
		} else {
			limitOffset = 0;
			limit = -1;
		}
		return this.owner.getMutator().processCallProcedure(sf, name, namePos, params, paramsPos, limitOffset, limit);
	}
	public TableReturn processDescribe(CalcFrameStack sf, DerivedCellCalculatorSql query, SqlNode an) {
		ColumnarTable r = new ColumnarTable(String.class, "SQL");
		r.setTitle("DESCRIBE");
		AdminNode admin = JavaExpressionParser.castNode(an, AdminNode.class);
		SqlNode options = admin.getOptions();
		int scope;
		if (options != null && SqlExpressionParser.isScopeKeyword(options.getOperation()))
			scope = options.getOperation();
		else
			scope = SqlExpressionParser.ID_INVALID;
		ArrayNode nodes = (ArrayNode) admin.getNext();
		for (int i = 0, l = nodes.getParamsCount(); i < l; i++) {
			Node n = nodes.getParamAt(i);
			SqlOperationNode nameNode = JavaExpressionParser.castNode(n, SqlOperationNode.class);
			SqlOperationNode optionsNode = nameNode.getNext() == null ? null : JavaExpressionParser.castNode(nameNode.getNext(), SqlOperationNode.class);
			String name = nameNode.getNameAsString();
			int namePos = nameNode.getPosition();
			//			String on = onNode == null ? null : onNode.getName();
			//			int onPos = onNode == null ? -1 : onNode.getPosition();
			String on = null, from = null;
			int onPos = -1, fromPos = -1;
			while (optionsNode != null) {
				if (optionsNode.getOperation() == SqlExpressionParser.ID_ON) {
					on = optionsNode.getNameAsString();
					onPos = optionsNode.getPosition();
				} else if (optionsNode.getOperation() == SqlExpressionParser.ID_FROM) {
					from = optionsNode.getNameAsString();
					fromPos = optionsNode.getPosition();
				}
				optionsNode = JavaExpressionParser.castNode(optionsNode.getNext(), SqlOperationNode.class);
			}
			int type = nameNode.getOperation();
			String s;
			if (type == SqlExpressionParser.ID_METHOD)
				s = this.owner.getMutator().processDescribe(sf, type, scope, name, namePos, on, onPos, from, fromPos,
						nameNode.getName() instanceof MethodNode ? (MethodNode) (nameNode.getName()) : null);
			else
				s = this.owner.getMutator().processDescribe(sf, type, scope, name, namePos, on, onPos, from, fromPos, null);
			//			s = SH.trim(s);
			//			StringBuilder sb = new StringBuilder();
			//			for (String s2 : SH.splitLines(s)) {
			//				s2 = s2.trim();
			//				if (SH.is(s2)) {
			//					sb.append(s2);
			//				}
			//			}
			r.getRows().addRow(s);
		}
		return new TableReturn(r);
	}
	public TableReturn processDiagnose(CalcFrameStack sf, DerivedCellCalculatorSql query, SqlNode node) {
		AdminNode admin = JavaExpressionParser.castNode(node, AdminNode.class);

		ColumnarTable r = new ColumnarTable(String.class, "TABLE", String.class, "TYPE", String.class, "NAME");
		r.setTitle("DIAGNOSE");
		ArrayNode nodes = (ArrayNode) admin.getNext();
		SqlNode options = admin.getOptions();
		int scope;
		if (options != null && SqlExpressionParser.isScopeKeyword(options.getOperation()))
			scope = options.getOperation();
		else
			scope = SqlExpressionParser.ID_INVALID;
		for (int k = 0, l = nodes.getParamsCount(); k < l; k++) {
			Node n = nodes.getParamAt(k);
			SqlOperationNode nameNode = JavaExpressionParser.castNode(n, SqlOperationNode.class);
			int type = nameNode.getOperation();
			SqlOperationNode onNode = nameNode.getNext() == null ? null : JavaExpressionParser.castNode(nameNode.getNext(), SqlOperationNode.class);
			String onName = onNode == null ? null : onNode.getNameAsString();
			int onPos = onNode == null ? -1 : onNode.getPosition();
			String name = nameNode.getNameAsString();
			int namePos = nameNode.getPosition();
			if (type == SqlExpressionParser.ID_TABLE) {
				Table table = this.owner.getMutator().getTable(sf, namePos, name, scope);
				this.owner.getMutator().processDiagnoseTable(sf, scope, table, r);
			} else if (type == SqlExpressionParser.ID_COLUMN) {
				Table table = this.owner.getMutator().getTable(sf, onPos, onName, scope);
				Column i = table.getColumnsMap().get(name);
				if (i == null)
					throw new ExpressionParserException(nameNode.getPosition(), "Unknown column name for table '" + onName + "': " + name);
				Map<String, Object> m = this.owner.getMutator().processDiagnoseColumn(sf, scope, table, i);
				for (Entry<String, Object> j : m.entrySet()) {
					if (j.getValue() != null && !r.getColumnsMap().containsKey(j.getKey())) {
						r.addColumn(j.getValue().getClass(), j.getKey(), null, true);
					}
				}
				ColumnarRow row = r.newEmptyRow();
				r.getRows().add(row);
				row.put("TABLE", table.getTitle());
				row.put("NAME", (String) i.getId());
				for (Entry<String, Object> j : m.entrySet()) {
					if (j.getValue() != null)
						row.put(j.getKey(), j.getValue());
				}
			} else if (type == SqlExpressionParser.ID_INDEX) {
				Table table = this.owner.getMutator().getTable(sf, onPos, onName, scope);
				Set<String> indexes = this.owner.getMutator().getIndexes(sf, table);
				if (!indexes.contains(name))
					throw new ExpressionParserException(nameNode.getPosition(), "Unknown Index name for table '" + onName + "': " + name);
				Map<String, Object> m = this.owner.getMutator().processDiagnoseIndex(sf, scope, table, namePos, name);
				for (Entry<String, Object> j : m.entrySet()) {
					if (j.getValue() != null && !r.getColumnsMap().containsKey(j.getKey())) {
						r.addColumn(j.getValue().getClass(), j.getKey(), null, true);
					}
				}
				ColumnarRow row = r.newEmptyRow();
				r.getRows().add(row);
				row.put("TABLE", table.getTitle());
				row.put("NAME", (String) name);
				for (Entry<String, Object> j : m.entrySet()) {
					if (j.getValue() != null)
						row.put(j.getKey(), j.getValue());
				}
			} else
				throw new ExpressionParserException(nameNode.getPosition(), "Expecting TABLE, COLUMN or INDEX");
		}
		return new TableReturn(r);

	}

	public FlowControl processShow(CalcFrameStack sf, DerivedCellCalculatorSql query, SqlNode node) {
		SqlShowNode show = JavaExpressionParser.castNode(node, SqlShowNode.class);

		Table table;
		if (show.getName() != null) {//for VariableNode
			table = owner.getMutator().processShow(sf, show.getTarget(), show.getPosition(), show.getScope(), show.isFull(),
					show.getName() == null ? null : show.getName().getVarname(), show.getName() == null ? -1 : show.getName().getPosition(),
					show.getFrom() == null ? null : show.getFrom().getVarname(), show.getFrom() == null ? -1 : show.getFrom().getPosition(), null);
		} else { //for MethodNode
			table = owner.getMutator().processShow(sf, show.getTarget(), show.getPosition(), show.getScope(), show.isFull(),
					show.getMethodSignature() == null ? null : show.getMethodSignature().toString(), show.getMethodSignature() == null ? -1 : show.getPosition(),
					show.getFrom() == null ? null : show.getFrom().getVarname(), show.getFrom() == null ? -1 : show.getFrom().getPosition(), show.getMethodSignature());
		}
		if (node.getNext() != null) {
			Tableset tmp = new TablesetImpl();
			tmp.putTable(table.getTitle(), table);
			Node[] columns = new Node[table.getColumnsCount()];
			for (int i = 0; i < columns.length; i++) {
				columns[i] = new VariableNode(0, (String) table.getColumnAt(i).getId());
			}
			SqlColumnsNode n = new SqlColumnsNode(0, new Node[] { new VariableNode(0, table.getTitle()) }, show.getNext(), SqlExpressionParser.ID_FROM);
			SqlColumnsNode select = new SqlColumnsNode(0, columns, n, SqlExpressionParser.ID_SELECT);
			SelectClause sc = SqlProcessor_Select.buildSelectClause(select);
			Map<String, Table> m = CH.m(table.getTitle(), table);
			return owner.getSelectProcessor().processSelect(query, sc, sf, m);
		}
		return new TableReturn(table);
	}

	//	public void getCallDependencies(DerivedCellParserContext context, SqlNode an, Set<Object> sink) {
	//		SqlCallNode mn = JavaExpressionParser.castNode(an, SqlCallNode.class);
	//		int size = mn.getParamsCount();
	//		for (int i = 0; i < size; i++) {
	//			Node node = mn.getParamAt(i);
	//			DerivedHelper.getDependencyIds(owner.getParser().toCalc(node, context), sink);
	//		}
	//		if (mn.getNext() != null) {
	//			final Limits limits = new Limits(JavaExpressionParser.castNode(mn.getNext(), SqlColumnsNode.class));
	//			limits.getDependencyIds(context, this.owner.getParser(), sink);
	//		}
	//	}

}
