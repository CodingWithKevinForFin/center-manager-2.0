package com.f1.ami.amicommon.functions;

import com.f1.base.Caster;
import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionMinimum extends AbstractMethodDerivedCellCalculatorN {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("minimum", Comparable.class, "Comparable ... o");
	static {
		VERIFIER.addDesc("Finds and returns the minimum value, skipping nulls. Returns null if all arguments are null. Note the return type will be the widest of input values.");
		VERIFIER.addParamDesc(0, "values to compare");
		VERIFIER.addExample("orange", "bananna", null, "apple");
		VERIFIER.addExample(null, null, null);
		VERIFIER.addExample("1", "1.7", "-1");
	}
	final private Caster<? extends Comparable> returnType;
	final private Comparable constVal;

	public AmiWebFunctionMinimum(int position, DerivedCellCalculator[] params) {
		super(position, params);
		this.returnType = DerivedHelper.getReturnType(params, Comparable.class);
		Comparable cv = null;
		for (int pos : evalConsts())
			cv = min(this.returnType.castNoThrow(getParamAt(pos).get(null)), cv);
		this.constVal = cv;
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	private Comparable min(Comparable a, Comparable b) {
		return b == null || (a != null && a.compareTo(b) <= 0) ? a : b;
	}

	@Override
	public Class<?> getReturnType() {
		return this.returnType.getCastToClass();
	}

	@Override
	public Object eval(Object[] o) {
		Comparable r = constVal;
		for (int i : this.notConsts)
			r = min(r, this.returnType.castNoThrow(o[i]));
		return r;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionMinimum(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionMinimum(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
