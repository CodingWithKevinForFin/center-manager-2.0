package com.f1.ami.amihibernate;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.dialect.MySQLSqlAstTranslator;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.sqm.ComparisonOperator;
import org.hibernate.sql.ast.Clause;
import org.hibernate.sql.ast.spi.SqlSelection;
import org.hibernate.sql.ast.tree.Statement;
import org.hibernate.sql.ast.tree.delete.DeleteStatement;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.sql.ast.tree.expression.SqlTuple;
import org.hibernate.sql.ast.tree.from.NamedTableReference;
import org.hibernate.sql.ast.tree.from.TableReference;
import org.hibernate.sql.exec.spi.JdbcOperation;

public class AmiSqlAstTranslator<T extends JdbcOperation> extends MySQLSqlAstTranslator<T> {
	//	private static final Map<ComparisonOperator, AmiHibernateComparisonOperator> OPERATION_MAP;
	//
	//	static {
	//		OPERATION_MAP = new EnumMap<>(ComparisonOperator.class);
	//		OPERATION_MAP.put(ComparisonOperator.EQUAL, AmiComparisonOperator.EQUAL);
	//		OPERATION_MAP.put(ComparisonOperator.GREATER_THAN, AmiComparisonOperator.GREATER_THAN);
	//		OPERATION_MAP.put(ComparisonOperator.GREATER_THAN_OR_EQUAL, AmiComparisonOperator.GREATER_THAN_OR_EQUAL);
	//		OPERATION_MAP.put(ComparisonOperator.LESS_THAN, AmiComparisonOperator.LESS_THAN);
	//		OPERATION_MAP.put(ComparisonOperator.LESS_THAN_OR_EQUAL, AmiComparisonOperator.LESS_THAN_OR_EQUAL);
	//		OPERATION_MAP.put(ComparisonOperator.NOT_EQUAL, AmiComparisonOperator.NOT_EQUAL);
	//		OPERATION_MAP.put(ComparisonOperator.DISTINCT_FROM, AmiComparisonOperator.DISTINCT_FROM);
	//		OPERATION_MAP.put(ComparisonOperator.NOT_DISTINCT_FROM, AmiComparisonOperator.NOT_DISTINCT_FROM);
	//	}

	public AmiSqlAstTranslator(SessionFactoryImplementor sessionFactory, Statement statement) {
		super(sessionFactory, statement);
	}

	private final void renderAmiTableReferenceIdentificationVariable(TableReference tableReference) {
		final String identificationVariable = tableReference.getIdentificationVariable();
		if (identificationVariable != null) {
			append(WHITESPACE);
			append("AS");
			append(WHITESPACE);
			append(tableReference.getIdentificationVariable());
		}

	}
	@Override
	protected boolean renderNamedTableReference(NamedTableReference tableReference, LockMode lockMode) {
		appendSql(tableReference.getTableExpression());
		registerAffectedTable(tableReference);
		final Clause currentClause = this.getClauseStack().getCurrent();
		if (rendersTableReferenceAlias(currentClause)) {
			renderAmiTableReferenceIdentificationVariable(tableReference);
		}
		return false;
	}
	@Override
	protected void renderComparison(Expression lhs, ComparisonOperator operator, Expression rhs) {
		super.renderComparison(lhs, operator, rhs);
	}
	@Override
	protected void renderComparisonDistinctOperator(Expression lhs, ComparisonOperator operator, Expression rhs) {
		final boolean notWrapper;
		final String operatorText;
		final String amiOperator = AmiComparisonOperator.COMPARISON_OPERATOR_MAP.get(operator);
		switch (operator) {
			case DISTINCT_FROM:
				notWrapper = true;
				operatorText = "<=>";
				break;
			case NOT_DISTINCT_FROM:
				notWrapper = false;
				operatorText = "<=>";
				break;
			default:
				notWrapper = false;
				operatorText = amiOperator;
				break;
		}
		if (notWrapper) {
			appendSql("not(");
		}
		lhs.accept(this);
		appendSql(operatorText);
		rhs.accept(this);
		if (notWrapper) {
			appendSql(CLOSE_PARENTHESIS);
		}
	}
	@Override
	protected void renderComparisonEmulateCase(Expression lhs, ComparisonOperator operator, Expression rhs) {
		final String amiOperator = AmiComparisonOperator.COMPARISON_OPERATOR_MAP.get(operator);
		switch (operator) {
			case DISTINCT_FROM:
				appendSql("case when ");
				lhs.accept(this);
				appendSql('=');
				rhs.accept(this);
				appendSql(" or ");
				lhs.accept(this);
				appendSql(" is null and ");
				rhs.accept(this);
				appendSql(" is null then 0 else 1 end=1");
				break;
			case NOT_DISTINCT_FROM:
				appendSql("case when ");
				lhs.accept(this);
				appendSql('=');
				rhs.accept(this);
				appendSql(" or ");
				lhs.accept(this);
				appendSql(" is null and ");
				rhs.accept(this);
				appendSql(" is null then 0 else 1 end=0");
				break;
			default:
				lhs.accept(this);
				appendSql(amiOperator);
				rhs.accept(this);
				break;
		}
	}
	@Override
	protected void renderComparisonEmulateDecode(Expression lhs, ComparisonOperator operator, Expression rhs) {
		final String amiOperator = AmiComparisonOperator.COMPARISON_OPERATOR_MAP.get(operator);
		switch (operator) {
			case DISTINCT_FROM:
				appendSql("decode(");
				lhs.accept(this);
				appendSql(',');
				rhs.accept(this);
				appendSql(",0,1)=1");
				break;
			case NOT_DISTINCT_FROM:
				appendSql("decode(");
				lhs.accept(this);
				appendSql(',');
				rhs.accept(this);
				appendSql(",0,1)=0");
				break;
			default:
				lhs.accept(this);
				appendSql(amiOperator);
				rhs.accept(this);
				break;
		}
	}
	@Override
	protected void renderComparisonEmulateIntersect(Expression lhs, ComparisonOperator operator, Expression rhs) {
		final String amiOperator = AmiComparisonOperator.COMPARISON_OPERATOR_MAP.get(operator);
		switch (operator) {
			case DISTINCT_FROM:
				appendSql("not ");
			case NOT_DISTINCT_FROM: {
				appendSql("exists (select ");
				getClauseStack().push(Clause.SELECT);
				visitSqlSelectExpression(lhs);
				appendSql(getFromDualForSelectOnly());
				appendSql(" intersect select ");
				visitSqlSelectExpression(rhs);
				appendSql(getFromDualForSelectOnly());
				getClauseStack().pop();
				appendSql(CLOSE_PARENTHESIS);
				return;
			}
		}
		lhs.accept(this);
		appendSql(amiOperator);
		rhs.accept(this);
	}
	@Override
	protected void renderComparisonStandard(Expression lhs, ComparisonOperator operator, Expression rhs) {
		final String amiOperator = AmiComparisonOperator.COMPARISON_OPERATOR_MAP.get(operator);
		lhs.accept(this);
		appendSql(amiOperator);
		rhs.accept(this);
	}
	@Override
	protected void renderTupleComparisonStandard(List<SqlSelection> lhsExpressions, SqlTuple tuple, ComparisonOperator operator) {
		final String amiOperator = AmiComparisonOperator.COMPARISON_OPERATOR_MAP.get(operator);
		appendSql(OPEN_PARENTHESIS);
		String separator = NO_SEPARATOR;
		for (SqlSelection lhsExpression : lhsExpressions) {
			appendSql(separator);
			lhsExpression.getExpression().accept(this);
			separator = COMA_SEPARATOR;
		}
		appendSql(CLOSE_PARENTHESIS);
		appendSql(amiOperator);
		tuple.accept(this);
	}
	@Override
	public void visitDeleteStatement(DeleteStatement statement) {
		// TODO Auto-generated method stub
		super.visitDeleteStatement(statement);
	}

}
