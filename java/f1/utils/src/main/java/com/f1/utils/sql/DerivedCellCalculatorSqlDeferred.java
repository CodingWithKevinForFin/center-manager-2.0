package com.f1.utils.sql;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorStringTemplate;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSqlDeferred implements DerivedCellCalculator {

	private SqlDerivedCellParser dcp;
	private DerivedCellCalculatorStringTemplate stringTemplate;
	private com.f1.base.CalcTypes globalVarTypes;
	//	private boolean isInnerQuery = false;

	public DerivedCellCalculatorSqlDeferred(DerivedCellCalculatorStringTemplate stringTemplate, SqlDerivedCellParser dcp) {
		this.stringTemplate = stringTemplate;
		this.dcp = dcp;
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		Object obj = this.stringTemplate.get(lcvs, true);
		if (obj instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) obj, this, lcvs, 0, null);
		StringBuilder sb = new StringBuilder();
		SH.repeat(' ', this.stringTemplate.getPosition(), sb);//this padding is a hack but it gets the position of the nodes to the right spot
		sb.append(obj);
		sb.append(';');
		DerivedCellCalculatorExpression calc;
		try {
			final SqlExpressionParser sep = new SqlExpressionParser();
			sep.setProcessingDeferredSql(true);
			Node node = sep.parse(sb.toString());
			calc = dcp.getSqlProcessor().toCalc(sb.toString(), node, lcvs);
		} catch (ExpressionParserException e) {
			throw new ExpressionParserException(e.getPosition(), "Syntax error for: " + sb.substring(this.stringTemplate.getPosition()) + '\n' + e.getMessage(), e);
		} //		if (isInnerQuery) {
			//			DerivedCellCalculatorSql sqlcalc = DerivedHelper.findFirst(calc, DerivedCellCalculatorSql.class);
			//			sqlcalc.setIsInnerQuery(true);
			//		}
		return calc.get(lcvs);
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		return stringTemplate.toString(sink);
	}

	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}

	@Override
	public int getPosition() {
		return stringTemplate.getPosition();
	}

	@Override
	public DerivedCellCalculator copy() {
		return null;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public int getInnerCalcsCount() {
		return 0;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		throw new IndexOutOfBoundsException("" + n);
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public Object resume(PauseStack paused) {
		CalcFrameStack lcvs = paused.getLcvs();
		OH.assertEq(paused.getState(), 0);
		Object obj = paused.getNext().resume();
		if (obj instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) obj, this, paused.getLcvs(), 0, null);

		StringBuilder sb = new StringBuilder();
		SH.repeat(' ', this.stringTemplate.getPosition(), sb);//this padding is a hack but it gets the position of the nodes to the right spot
		sb.append(obj);
		sb.append(';');
		boolean t = dcp.getSqlProcessor().getExpressionParser().getProcessingDeferredSql();
		DerivedCellCalculatorExpression calc;
		if (t) {
			try {
				calc = dcp.getSqlProcessor().toCalc(sb.toString(), lcvs);
			} catch (ExpressionParserException e) {
				throw new ExpressionParserException(e.getPosition(), "Syntax error for: " + sb.substring(this.stringTemplate.getPosition()) + '\n' + e.getMessage(), e);
			}
		} else {
			try {
				dcp.getSqlProcessor().getExpressionParser().setProcessingDeferredSql(true);
				calc = dcp.getSqlProcessor().toCalc(sb.toString(), lcvs);
			} catch (ExpressionParserException e) {
				throw new ExpressionParserException(e.getPosition(), "Syntax error for: " + sb.substring(this.stringTemplate.getPosition()) + '\n' + e.getMessage(), e);
			} finally {
				dcp.getSqlProcessor().getExpressionParser().setProcessingDeferredSql(false);
			}
		}
		//		if (isInnerQuery) {
		//			DerivedCellCalculatorSql sqlcalc = DerivedHelper.findFirst(calc, DerivedCellCalculatorSql.class);
		//			sqlcalc.setIsInnerQuery(true);
		//		}
		return calc.get(lcvs);

	}
	@Override
	public boolean isPausable() {
		return false;
	}
	//	public void setIsInnerQuery(boolean b) {
	//		this.isInnerQuery = b;
	//	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorSqlDeferred o = (DerivedCellCalculatorSqlDeferred) other;
		return ///isInnerQuery == o.isInnerQuery && 
		DerivedHelper.areSame(this.stringTemplate, o.stringTemplate);
	}
}
