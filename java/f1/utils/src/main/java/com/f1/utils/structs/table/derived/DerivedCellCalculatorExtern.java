package com.f1.utils.structs.table.derived;

import java.util.Set;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorExtern implements DerivedCellCalculatorWithDependencies {

	private int codePosition;
	private String code;
	private ExternCompiled compiled;
	private String languageName;
	private int bracketsCount;

	public DerivedCellCalculatorExtern(int codePosition, String languageName, int bracketsCount, String code, ExternCompiled compiled) {
		this.codePosition = codePosition;
		this.code = code;
		this.languageName = languageName;
		this.bracketsCount = bracketsCount;
		this.compiled = compiled;
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		return compiled.execute(lcvs);
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("extern ").append(languageName).append(' ');
		SH.repeat('{', bracketsCount, sink);
		sink.append(code);
		SH.repeat('}', bracketsCount, sink);
		return sink;
	}
	@Override
	public Set<Object> getDependencyIds(Set<Object> sink) {
		this.compiled.getDependencies((Set) sink);
		return sink;
	}

	@Override
	public Class<?> getReturnType() {
		return this.compiled.getReturnType();
	}

	@Override
	public int getPosition() {
		return this.codePosition;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorExtern(codePosition, languageName, bracketsCount, code, compiled);
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
	public DerivedCellCalculator getInnerCalcAt(int n) {
		throw new IndexOutOfBoundsException("" + n);
	}

	@Override
	public int getInnerCalcsCount() {
		return 0;
	}
	@Override
	public Object resume(PauseStack paused) {
		throw new IllegalStateException();
	}
	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorExtern o = (DerivedCellCalculatorExtern) other;
		return OH.eq(this.languageName, o.languageName) && OH.eq(this.code, o.code) && OH.eq(this.bracketsCount, o.bracketsCount);
	}

}
