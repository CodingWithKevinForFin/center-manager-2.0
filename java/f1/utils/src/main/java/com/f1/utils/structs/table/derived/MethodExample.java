package com.f1.utils.structs.table.derived;

import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public class MethodExample {
	final private String script;
	final private String[] returns;
	final private String description;
	final private String owningClass;

	public MethodExample(String script) {
		this.script = script;
		this.returns = new String[0];
		this.description = SH.EMPTY_STRING;
		this.owningClass = SH.EMPTY_STRING;
	}
	public MethodExample(String script, String[] returns) {
		this.script = script;
		this.returns = returns;
		this.description = SH.EMPTY_STRING;
		this.owningClass = SH.EMPTY_STRING;
	}
	public MethodExample(String script, String[] returns, String description) {
		this.script = script;
		this.returns = returns;
		this.description = description;
		this.owningClass = SH.EMPTY_STRING;
	}
	public MethodExample(String script, String[] returns, String description, String owningClass) {
		this.script = script;
		this.returns = returns;
		this.description = description;
		this.owningClass = owningClass;
	}

	public String getScript() {
		return this.script;
	}

	public String[] getReturns() {
		return this.returns;
	}

	public String getDescription() {
		return this.description;
	}

	public String getOwningClass() {
		return this.owningClass;
	}

	public String[] getEvaluableScripts() {
		String[] evaluations = new String[this.returns.length];
		for (int i = 0; i < evaluations.length; i++) {
			StringBuilder test = new StringBuilder();
			test.append("{").append('\n');
			test.append(this.script).append('\n');
			test.append("return ").append(returns[i]).append(";\n");
			test.append("}");
			evaluations[i] = test.toString();
		}
		return evaluations;
	}

	public String[] evaluateReturns(DerivedCellParser parser) {
		String[] evaluations = new String[this.returns.length];
		for (int i = 0; i < evaluations.length; i++) {
			StringBuilder test = new StringBuilder();
			test.append("{").append('\n');
			test.append(this.script).append('\n');
			test.append("return ").append(returns[i]).append(";\n");
			test.append("}");

			//			BasicDerivedCellParser dcp = new BasicDerivedCellParser(parser);
			DerivedCellCalculator c = parser.toCalc(test, EmptyCalcFrameStack.INSTANCE);
			evaluations[i] = c.get(null).toString();
		}
		return evaluations;
	}
}
