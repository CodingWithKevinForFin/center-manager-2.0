package com.f1.utils.sql;

import java.util.logging.Logger;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.Table;
import com.f1.utils.LH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.AdminNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class SqlProcessor implements Lockable {
	public static final int NO_LIMIT = -1;

	private static final Logger log = LH.get();
	final private SqlExpressionParser sqlExpressionParser;
	private SqlDerivedCellParser derivedCellParser;
	private SqlProcessor_Insert insertProcessor = new SqlProcessor_Insert(this);
	private SqlProcessor_Update updateProcessor = new SqlProcessor_Update(this);
	private SqlProcessor_Delete deleteProcessor = new SqlProcessor_Delete(this);
	private SqlProcessor_Select selectProcessor = new SqlProcessor_Select(this);
	private SqlProcessor_Tools toolsProcessor = new SqlProcessor_Tools(this);
	private SqlProcessor_Admin adminProcessor = new SqlProcessor_Admin(this);
	private SqlProcessor_Analyze analyzeProcessor = new SqlProcessor_Analyze(this);
	private SqlProcessor_Prepare prepareProcessor = new SqlProcessor_Prepare(this);
	private SqlProcessorTableMutator mutator = new SqlProcessorTableMutatorImpl(this);

	private boolean isLocked;

	public SqlProcessor() {
		this(new SqlExpressionParser());
	}

	public SqlProcessor(SqlExpressionParser parser) {
		this.derivedCellParser = new SqlDerivedCellParser(parser, this);
		if (parser == null)
			this.sqlExpressionParser = new SqlExpressionParser();
		else
			this.sqlExpressionParser = (SqlExpressionParser) parser;

	}

	final public Object processSql(String sql, CalcFrameStack sf) {
		DerivedCellCalculatorExpression calc = toCalc(sql, sf);
		if (calc == null)
			return null;
		return calc.get(sf);
	}
	final public Table process(String sql, CalcFrameStack sf) {
		Object o = processSql(sql, sf);
		if (o instanceof TableReturn) {
			TableReturn tr = (TableReturn) o;
			if (tr.getTables().size() > 0)
				return tr.getTables().get(0);
		}
		return null;
	}

	public DerivedCellCalculatorExpression toCalc(String sql, CalcTypesStack context) {
		Node node = sqlExpressionParser.parse(sql);
		return toCalc(sql, node, context);
	}
	public DerivedCellCalculatorExpression toCalc(String sql, Node node, CalcTypesStack context) {
		DerivedCellCalculator calc;
		try {
			calc = derivedCellParser.toCalc(node, context);
		} catch (ExpressionParserException e) {
			if (e.getExpression() == null)
				e.setExpression(sql);
			throw e;
		}
		if (calc == null)
			return null;
		return new DerivedCellCalculatorExpression(sql, calc);
	}

	public SelectClause toSelectClause(String query) {
		Node selectNode = this.sqlExpressionParser.parse(query);
		SqlColumnsNode scn = JavaExpressionParser.castNode(selectNode, SqlColumnsNode.class, "SELECT CLAUSE");
		return SqlProcessor_Select.buildSelectClause(scn);
	}

	protected FlowControl process(DerivedCellCalculatorSql query, Node node, SqlDerivedCellParser dcp, CalcFrameStack sf) {
		if (node == null)
			throw new ExpressionParserException(0, "Unknown statement");
		if (node instanceof AdminNode) {
			AdminNode an = (AdminNode) node;
			switch (an.getOperation()) {
				case SqlExpressionParser.ID_CREATE:
					switch (an.getTargetType()) {
						case SqlExpressionParser.ID_INDEX:
							adminProcessor.processCreateIndex(sf, an, dcp);
							return null;
						case SqlExpressionParser.ID_TRIGGER:
							adminProcessor.processCreateTrigger(sf, an, dcp);
							return null;
						case SqlExpressionParser.ID_TIMER:
							adminProcessor.processCreateTimer(sf, an, dcp);
							return null;
						case SqlExpressionParser.ID_PROCEDURE:
							adminProcessor.processCreateProcedure(sf, an, dcp);
							return null;
						case SqlExpressionParser.ID_METHOD:
							adminProcessor.processCreateMethod(sf, an, dcp);
							return null;
						case SqlExpressionParser.ID_DBO:
							adminProcessor.processCreateDbo(sf, an, dcp);
							return null;
						default:
							throw new ExpressionParserException(node.getPosition(), "Operation not supported: CREATE " + SqlExpressionParser.toOperationString(an.getTargetType()));
					}
				case SqlExpressionParser.ID_DROP:
					switch (an.getTargetType()) {
						case SqlExpressionParser.ID_TABLE:
							adminProcessor.processDropTable(sf, an);
							return null;
						case SqlExpressionParser.ID_INDEX:
							adminProcessor.processDropIndex(sf, an);
							return null;
						case SqlExpressionParser.ID_TRIGGER:
							adminProcessor.processDropTrigger(sf, an);
							return null;
						case SqlExpressionParser.ID_TIMER:
							adminProcessor.processDropTimer(sf, an);
							return null;
						case SqlExpressionParser.ID_PROCEDURE:
							adminProcessor.processDropProcedure(sf, an);
							return null;
						case SqlExpressionParser.ID_METHOD:
							adminProcessor.processDropMethod(sf, an, dcp);
							return null;
						case SqlExpressionParser.ID_DBO:
							adminProcessor.processDropDbo(sf, an);
							return null;
						default:
							throw new ExpressionParserException(node.getPosition(), "Operation not supported: DROP " + SqlExpressionParser.toOperationString(an.getTargetType()));
					}
				case SqlExpressionParser.ID_ALTER:
					if (an.getTargetType() == SqlExpressionParser.ID_TABLE)
						adminProcessor.processAlterTable(sf, an);
					else
						adminProcessor.processAlterUse(sf, an);
					return null;
				case SqlExpressionParser.ID_ENABLE:
				case SqlExpressionParser.ID_DISABLE:
					adminProcessor.processEnable(sf, an);
					return null;
				case SqlExpressionParser.ID_RENAME: {
					adminProcessor.processRename(sf, an);
					return null;
				}
				case SqlExpressionParser.ID_TRUNCATE: {
					return deleteProcessor.processTruncate(sf, an);
				}
			}
		}
		if (node instanceof SqlNode) {
			SqlNode an = (SqlNode) node;
			int op = an.getOperation();
			switch (op) {
				case SqlExpressionParser.ID_DESCRIBE:
					return toolsProcessor.processDescribe(sf, query, an);
				case SqlExpressionParser.ID_DIAGNOSE:
					return toolsProcessor.processDiagnose(sf, query, an);
				case SqlExpressionParser.ID_CALL:
					return toolsProcessor.processCall(sf, query, an);
				case SqlExpressionParser.ID_SHOW:
					return toolsProcessor.processShow(sf, query, an);
				default:
					throw new ExpressionParserException(node.getPosition(), "Operation not supported: " + SqlExpressionParser.toOperationString(an.getOperation()));
			}
		}
		throw new ExpressionParserException(node.getPosition(), "Operation not supported: " + node);
	}

	public SqlDerivedCellParser getParser() {
		return this.derivedCellParser;
	}

	public void setParser(SqlDerivedCellParser derivedCellParser) {
		LockedException.assertNotLocked(this);
		this.derivedCellParser = derivedCellParser;
	}

	final public SqlExpressionParser getExpressionParser() {
		return this.sqlExpressionParser;
	}

	public void setMutator(SqlProcessorTableMutator mutator) {
		LockedException.assertNotLocked(this);
		this.mutator = mutator;
	}
	public SqlProcessorTableMutator getMutator() {
		return this.mutator;
	}
	public SqlProcessor_Insert getInsertProcessor() {
		return insertProcessor;
	}

	public void setInsertProcessor(SqlProcessor_Insert insertProcessor) {
		LockedException.assertNotLocked(this);
		this.insertProcessor = insertProcessor;
	}

	public SqlProcessor_Update getUpdateProcessor() {
		return updateProcessor;
	}

	public void setUpdateProcessor(SqlProcessor_Update updateProcessor) {
		LockedException.assertNotLocked(this);
		this.updateProcessor = updateProcessor;
	}

	public SqlProcessor_Delete getDeleteProcessor() {
		return deleteProcessor;
	}

	public void setDeleteProcessor(SqlProcessor_Delete deleteProcessor) {
		LockedException.assertNotLocked(this);
		this.deleteProcessor = deleteProcessor;
	}

	public SqlProcessor_Select getSelectProcessor() {
		return selectProcessor;
	}

	public void setSelectProcessor(SqlProcessor_Select selectProcessor) {
		LockedException.assertNotLocked(this);
		this.selectProcessor = selectProcessor;
	}

	public SqlProcessor_Admin getAdminProcessor() {
		return adminProcessor;
	}

	public void setAdminProcessor(SqlProcessor_Admin adminProcessor) {
		LockedException.assertNotLocked(this);
		this.adminProcessor = adminProcessor;
	}

	public SqlProcessor_Analyze getAnalyzeProcessor() {
		return analyzeProcessor;
	}

	public void setAnalyzeProcessor(SqlProcessor_Analyze analyzeProcessor) {
		LockedException.assertNotLocked(this);
		this.analyzeProcessor = analyzeProcessor;
	}

	public SqlProcessor_Prepare getPrepareProcessor() {
		return prepareProcessor;
	}

	public void setPrepareProcessor(SqlProcessor_Prepare prepareProcessor) {
		LockedException.assertNotLocked(this);
		this.prepareProcessor = prepareProcessor;
	}

	public SqlProcessor_Tools getToolsProcessor() {
		return toolsProcessor;
	}

	@Override
	public void lock() {
		this.isLocked = true;
	}

	@Override
	public boolean isLocked() {
		return isLocked;
	}

	public static QueryClause buildQueryClause(Node node) {
		SqlColumnsNode sn = JavaExpressionParser.castNode(node, SqlColumnsNode.class, "SELECT,PREPARE or ANALYZE clause after UNION");
		switch (sn.getOperation()) {
			case SqlExpressionParser.ID_SELECT:
				return SqlProcessor_Select.buildSelectClause(sn);
			case SqlExpressionParser.ID_PREPARE:
				return SqlProcessor_Prepare.buildPrepareClause(sn);
			case SqlExpressionParser.ID_ANALYZE:
				return SqlProcessor_Analyze.buildAnalyzeClause(sn);
			default:
				throw new ExpressionParserException(sn.getPosition(), "Unknown operation: " + SqlExpressionParser.toOperationString(sn.getOperation()));
		}
	}

}
