package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.DoubleGradient;
import com.f1.utils.MH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionScale extends AbstractMethodDerivedCellCalculatorN {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("scale", Double.class,
			"Number value,Number source1,Number target1,Number source2,Number target2,Number ... moreSourcesAndTargets");
	static {
		VERIFIER.addDesc("Requires a value and a list of number_from=number_to pairs. It will return a Double that");
		VERIFIER.addDesc(" properly represents the supplied value. For example, given the pairs 1=10 and 2=20,");
		VERIFIER.addDesc(
				" 1 will return 10, 1.5 returns 15 and 2 returns 20. Values greater than the max number_from will return the corresponding number_to (same for minimum). Ex 3 will return 20. ");
		VERIFIER.addDesc("At least 2 pairs are required but additional pairs can be provided in the var args parameter.");
		VERIFIER.addParamDesc(0, "Value to return number for");
		VERIFIER.addParamDesc(1, "Input Value 1 associated with return value 1", "return value 1 associated with value 1");
		VERIFIER.addParamDesc(3, "Input Value 2 associated with return value 2", "return value 2 associated with Input value 2");
		VERIFIER.addParamDesc(5, "Additional number/number pairs, must be even number of elements, where even elements are a number and odd elements are a return number");
		VERIFIER.addExample(.5, 0, 10, 1, 20, 2, 100);
		VERIFIER.addExample(1.5, 0, 10, 1, 20, 2, 100);
		VERIFIER.addExample(2.5, 0, 10, 1, 20, 2, 100);
		VERIFIER.addExample(-0.5, 0, 10, 1, 20, 2, 100);
	}

	final private DoubleGradient gradient;

	public AmiWebFunctionScale(int position, DerivedCellCalculator[] params) {
		super(position, params);
		if (params.length % 2 != 1)
			throw new ExpressionParserException(position, "sources and targets must be in pairs.");
		int[] consts = evalConsts();
		if (consts.length == params.length || (consts.length == params.length - 1 && consts[0] == 1))//either all consts, or first is var and rest are consts
			this.gradient = buildGradient(getBuf());
		else
			this.gradient = null;
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	private DoubleGradient buildGradient(Object[] values) {
		DoubleGradient r = new DoubleGradient();
		DerivedCellCalculator[] p = getParams();
		for (int i = 1; i < p.length;) {
			Number source = (Number) values[i++];
			Number target = (Number) values[i++];
			if (source == null || target == null)
				continue;
			double d1 = source.doubleValue();
			double d2 = target.doubleValue();
			if (MH.isntNumber(d1) || MH.isntNumber(d2))
				continue;
			try {
				r.addStop(d1, d2);
			} catch (Exception e) {
			}
		}
		return r.getStopsCount() < 1 ? null : r;
	}

	@Override
	public Object eval(Object[] values) {
		DoubleGradient g = gradient != null ? gradient : buildGradient(values);
		return g == null ? null : g.toGradient(((Number) values[0]).doubleValue());
	}

	@Override
	protected Object shortCircuit(int i, Object val) {
		return val == null && i == 0 ? null : KEEP_GOING;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionScale(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionScale(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
