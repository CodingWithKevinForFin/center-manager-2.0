package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.string.ExpressionParser;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.ExpressionNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.CreateTableNode;
import com.f1.utils.string.sqlnode.InsertNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.ValuesNode;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ExternFactoryManager;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class SqlDerivedCellParser extends BasicDerivedCellParser {

	final private SqlProcessor sqlProcessor;

	public SqlDerivedCellParser(ExpressionParser parser, SqlProcessor sqlProcessor) {
		super(parser);
		this.sqlProcessor = sqlProcessor;
	}
	public SqlDerivedCellParser(ExpressionParser parser, SqlProcessor sqlProcessor, ExternFactoryManager efm, boolean optimize) {
		super(parser, efm, optimize);
		this.sqlProcessor = sqlProcessor;
	}

	public SqlProcessor getSqlProcessor() {
		return this.sqlProcessor;
	}

	protected DerivedCellCalculator onSqlExecuteNode(SqlNode sn, CalcTypesStack context) {
		throw new ExpressionParserException(sn.getPosition(), "USE / EXECUTE NOT SUPPORTED");
	}

	protected DerivedCellCalculator onSqlCreateNode(SqlNode an, CalcTypesStack context) {
		if (an instanceof CreateTableNode) {

			CreateTableNode ctn = (CreateTableNode) an;
			DerivedCellCalculator query;
			if (ctn.getNext() != null) {
				query = (DerivedCellCalculator) toCalc(ctn.getNext(), context);
			} else
				query = null;
			return new DerivedCellCalculatorSqlCreateTable(ctn, (SqlDerivedCellParser) this, query);
		}
		return new DerivedCellCalculatorSql(an, (SqlDerivedCellParser) this);
	}
	protected DerivedCellCalculator onSqlInsertNode(InsertNode sn, CalcTypesStack context) {
		Node values = sn.getNext();
		DerivedCellCalculatorSql toInsert;
		if (!(values instanceof ValuesNode)) {
			toInsert = (DerivedCellCalculatorSql) toCalc(values, context);
		} else
			toInsert = null;
		return new DerivedCellCalculatorSqlInsert(sn, (SqlDerivedCellParser) this, toInsert);
	}
	private DerivedCellCalculator onSqlDeleteNode(SqlColumnsNode sn, CalcTypesStack context) {
		Node values = sn.getNext();
		DerivedCellCalculatorSql toJoin = null;
		if (values instanceof SqlColumnsNode) {
			SqlColumnsNode cn = (SqlColumnsNode) values;
			if (SqlExpressionParser.isJoin(cn.getOperation()))
				toJoin = toJoinRef(cn.getColumnAt(0), context);
		}
		return new DerivedCellCalculatorSqlDelete(sn, (SqlDerivedCellParser) this, toJoin);
	}
	private DerivedCellCalculator onSqlUpdateNode(SqlColumnsNode sn, CalcTypesStack context) {
		Node values = sn.getNext();
		DerivedCellCalculatorSql toJoin = null;
		if (values instanceof SqlColumnsNode) {
			SqlColumnsNode cn = (SqlColumnsNode) values;
			if (SqlExpressionParser.isJoin(cn.getOperation()))
				toJoin = toJoinRef(cn.getColumnAt(0), context);
		}
		return new DerivedCellCalculatorSqlUpdate(sn, (SqlDerivedCellParser) this, toJoin);
	}

	private DerivedCellCalculatorSql onSqlQueryNode(SqlColumnsNode sn, CalcTypesStack context) {
		QueryClause sc = SqlProcessor.buildQueryClause(sn);
		final List<DerivedCellCalculatorSql> selects;
		final List<Boolean> bynames;
		if (sc.getUnion() != null) {
			selects = new ArrayList<DerivedCellCalculatorSql>();
			bynames = new ArrayList<Boolean>();
			bynames.add(Boolean.FALSE);
		} else {
			selects = null;
			bynames = null;
		}
		boolean hasUnion = sc.getUnion() != null;
		for (;;) {
			AsNode[] rawTables = sc.getTables();
			DerivedCellCalculatorSql[] tables = new DerivedCellCalculatorSql[rawTables.length];
			String[] asNames = new String[rawTables.length];
			for (int i = 0; i < rawTables.length; i++) {
				AsNode an = rawTables[i];
				asNames[i] = an.getAs().toString();
				Node value = an.getValue();
				tables[i] = toJoinRef(value, context);
			}
			DerivedCellCalculatorSqlQueryClause r = new DerivedCellCalculatorSqlQueryClause(sn, (SqlDerivedCellParser) this, tables, asNames, sc, bynames != null);
			if (selects == null)
				return r;
			selects.add(r);
			if (sc.getUnion() == null)
				break;
			bynames.add(sc.isUnionByName());
			sc = sc.getUnion();
		}

		return new DerivedCellCalculatorSqlUnion(sn, (SqlDerivedCellParser) this, selects, bynames);
	}

	private DerivedCellCalculatorSql toJoinRef(Node value, CalcTypesStack context) {
		DerivedCellCalculatorSql table;
		while (value instanceof ExpressionNode)
			value = ((ExpressionNode) value).getValue();
		if (value.getNodeCode() == AsNode.CODE)
			value = ((AsNode) value).getValue();
		switch (value.getNodeCode()) {
			case SqlNode.CODE: {
				SqlNode sql = (SqlNode) value;
				if (SqlExpressionParser.isScopeKeyword(sql.getOperation())) {//SCOPE tablename
					Node next = sql.getNext();
					table = new DerivedCellCalculatorSqlTableRef(sql, (SqlDerivedCellParser) this, next.toString(), sql.getOperation());
				} else {
					table = (DerivedCellCalculatorSql) toCalc(value, context);
				}
				break;
			}
			case VariableNode.CODE: {//tablename
				String varname = ((VariableNode) value).getVarname();
				table = new DerivedCellCalculatorSqlTableRef(value, (SqlDerivedCellParser) this, varname, SqlExpressionParser.ID_INVALID);
				break;
			}
			default:
				throw new ExpressionParserException(value.getPosition(), "Expecting Table name or inner query");
		}
		return table;
	}

	protected DerivedCellCalculator onSqlNode(SqlNode sn, CalcTypesStack context) {
		switch (sn.getOperation()) {
			case SqlExpressionParser.ID_SELECT:
			case SqlExpressionParser.ID_PREPARE:
			case SqlExpressionParser.ID_ANALYZE:
				return onSqlQueryNode((SqlColumnsNode) sn, context);
			case SqlExpressionParser.ID_CREATE:
				return onSqlCreateNode(sn, context);
			case SqlExpressionParser.ID_INSERT:
			case SqlExpressionParser.ID_SYNC:
				return onSqlInsertNode((InsertNode) sn, context);
			case SqlExpressionParser.ID_DELETE:
				return onSqlDeleteNode((SqlColumnsNode) sn, context);
			case SqlExpressionParser.ID_UPDATE:
				return onSqlUpdateNode((SqlColumnsNode) sn, context);
			case SqlExpressionParser.ID_USE:
			case SqlExpressionParser.ID_EXECUTE:
				return onSqlExecuteNode((SqlNode) sn, context);
			default:
				return new DerivedCellCalculatorSql(sn, (SqlDerivedCellParser) this);
		}
	}
}
