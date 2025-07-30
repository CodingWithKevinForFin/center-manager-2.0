package com.f1.utils.structs.table.derived;

public class DeclaredMethodFactory implements MethodFactory {

	final private Class[] argumentTypes;
	DerivedCellCalculator inner;
	final private String[] argumentNames;
	final private Class<?> returnType;
	final private ParamsDefinition definition;
	private String innerText;
	private String innerLabel;
	private int bodyStart;
	private int bodyEnd;

	public DeclaredMethodFactory(Class<?> returnType, String methodName, String[] argumentNames, Class[] argumentTypes, byte modifiers) {
		this.argumentTypes = argumentTypes;
		this.argumentNames = argumentNames;
		this.returnType = returnType;
		this.definition = new ParamsDefinition(methodName, returnType, argumentNames, argumentTypes, false, modifiers);
	}

	public void setInner(String innerText, int bodyStart, int bodyEnd, DerivedCellCalculator inner) {
		this.innerText = innerText;
		this.inner = inner;
		this.bodyStart = bodyStart;
		this.bodyEnd = bodyEnd;
	}

	public void setInnerLabel(String innerTextLabel) {
		this.innerLabel = innerTextLabel;
	}

	public DerivedCellCalculator getInner() {
		return this.inner;
	}
	public StringBuilder getText(MethodFactoryManager mfm, StringBuilder sb) {
		sb.append("  ").append(getDefinition().toString(mfm));
		sb.append(this.innerText, this.bodyStart, this.bodyEnd);
		return sb.append(";");
	}
	public String getText(MethodFactoryManager mfm) {
		return getText(mfm, new StringBuilder()).toString();
	}

	public int getBodyStart() {
		return this.bodyStart;
	}
	public int getBodyEnd() {
		return this.bodyEnd;
	}
	public String getBodyText() {
		return this.innerText;
	}
	public String getBodyTextLabel() {
		return this.innerLabel;
	}
	@Override
	public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
		return new DerivedCellCalculatorMethod(position, returnType, methodName, argumentNames, argumentTypes, DeclaredMethodFactory.this, calcs);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return this.definition;
	}

}