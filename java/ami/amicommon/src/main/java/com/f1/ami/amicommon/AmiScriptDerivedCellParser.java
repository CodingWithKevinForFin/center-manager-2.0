package com.f1.ami.amicommon;

import java.util.Map;
import java.util.Map.Entry;

import com.f1.container.ContainerTools;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.SqlProcessorUtils.Limits;
import com.f1.utils.string.ExpressionParser;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.ExecuteNode;
import com.f1.utils.string.sqlnode.InsertNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.SqlShowNode;
import com.f1.utils.string.sqlnode.UseNode;
import com.f1.utils.string.sqlnode.ValuesNode;
import com.f1.utils.structs.table.derived.BasicExternFactoryManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class AmiScriptDerivedCellParser extends SqlDerivedCellParser {

	private ContainerTools tools;

	public AmiScriptDerivedCellParser(ExpressionParser parser, SqlProcessor sqlProcessor, ContainerTools tools, BasicExternFactoryManager externFactory, boolean optimize) {
		super(parser, sqlProcessor, externFactory, optimize);
		this.tools = tools;
	}

	protected DerivedCellCalculator onSqlExecuteNode(SqlNode asNext, CalcTypesStack context) {
		SqlDerivedCellParser dcp = this.getSqlProcessor().getParser();
		switch (asNext.getOperation()) {
			case SqlExpressionParser.ID_USE: {
				SqlNode node = asNext;
				UseNode use = JavaExpressionParser.castNode(asNext, UseNode.class);
				if (use.getNext() instanceof ExecuteNode) {
					ExecuteNode execute = JavaExpressionParser.castNode(use.getNext(), ExecuteNode.class);
					AmiDerivedCellCalculatorSql_UseExecute t = new AmiDerivedCellCalculatorSql_UseExecute(tools, dcp, node, toUseCalcs(use, context), execute);
					return t;
				} else if (use.getNext() instanceof SqlShowNode) {
					SqlShowNode show = JavaExpressionParser.castNode(use.getNext(), SqlShowNode.class);
					AmiDerivedCellCalculatorSql_UseShow t = new AmiDerivedCellCalculatorSql_UseShow(tools, dcp, node, toUseCalcs(use, context), show);
					return t;
				} else if (use.getNext() instanceof InsertNode) {
					//					throw new ToDoException();
					SqlNode nxt = (SqlNode) use.getNext();
					if (nxt.getOperation() != SqlExpressionParser.ID_INSERT)
						throw new ExpressionParserException(nxt.getPosition(),
								"Unsupported Operation " + SqlExpressionParser.toOperationString(nxt.getOperation()) + ". Expecting INSERT");
					InsertNode insertNode = JavaExpressionParser.castNode(use.getNext(), InsertNode.class);
					String[] columnNames;
					if (insertNode.getColumnsCount() > 0) {
						columnNames = new String[insertNode.getColumnsCount()];
						for (int i = 0; i < columnNames.length; i++)
							columnNames[i] = insertNode.getColumnAt(i).toString();
					} else
						columnNames = null;
					String targetTableName = insertNode.getTablename();
					SqlColumnsNode next = JavaExpressionParser.castNode(use.getNext(), SqlColumnsNode.class);
					if (next.getNext() instanceof ValuesNode) {
						ValuesNode vn = (ValuesNode) next.getNext();

						Limits limit = new Limits(insertNode.getLimit());
						//						Object[][] rows = super.getSqlProcessor().getInsertProcessor().processValues(null, vn, vn.getColCount(), null, null, limit.getLimit(null, null));
						//						//						Object[][] rows = super.getInsertProcessor().processValues(vn, vn.getColCount(), null, tablesMap, globalVarTypes, globalVars, null,
						//						//								limit.getLimit(this.getParser(), globalVarTypes, this.getMethodFactory(), globalVars));
						//						BasicTable rowsAsTable = new BasicTable();
						//						for (int i = 0; i < vn.getColCount(); i++)
						//							rowsAsTable.addColumn(Object.class, "_" + SH.toString(i));
						//						for (Object[] row : rows)
						//							rowsAsTable.getRows().addRow(row);
						//
						AmiDerivedCellCalculatorSql_UseInsertValues t = new AmiDerivedCellCalculatorSql_UseInsertValues(tools, dcp, node, toUseCalcs(use, context), columnNames,
								targetTableName, vn, limit);
						return t;
					} else {
						SqlNode nextNext = JavaExpressionParser.castNode(next.getNext(), SqlNode.class);
						DerivedCellCalculatorSql toInsert = (DerivedCellCalculatorSql) toCalc(nextNext, context);
						AmiDerivedCellCalculatorSql_UseInsert t = new AmiDerivedCellCalculatorSql_UseInsert(tools, dcp, node, toUseCalcs(use, context), columnNames,
								targetTableName, toInsert);
						return t;
						//							return (List) Collections.singletonList(t);
						//						}
					}
				} else
					throw new ExpressionParserException(use.getNext() == null ? use.getPosition() : use.getNext().getPosition(),
							"expecting EXECUTE, INSERT, SHOW TABLE or SHOW TABLES clause");
			}
			case SqlExpressionParser.ID_EXECUTE:
				ExecuteNode execute = JavaExpressionParser.castNode(asNext, ExecuteNode.class);
				AmiDerivedCellCalculatorSql_UseExecute t = new AmiDerivedCellCalculatorSql_UseExecute(tools, dcp, asNext, null, execute);
				return t;
			default:
				throw new ExpressionParserException(asNext.getPosition(), "Expecting SELECT, ANALYZE, SHOW, DESCRIBE, EXECUTE or USE ... EXECUTE, USE ... SHOW TABLES not: "
						+ SqlExpressionParser.toOperationString(asNext.getOperation()));
		}
	}

	private Map<String, DerivedCellCalculator> toUseCalcs(UseNode use, CalcTypesStack context) {
		HasherMap<String, DerivedCellCalculator> r = new HasherMap<String, DerivedCellCalculator>(CaseInsensitiveHasher.INSTANCE);
		for (Entry<String, Node> i : use.getOptionsMap().entrySet())
			r.put(i.getKey(), this.getSqlProcessor().getParser().toCalc(i.getValue(), context));
		return r;
	}
}
