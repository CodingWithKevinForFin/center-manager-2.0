package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionGradient extends AbstractMethodDerivedCellCalculatorN {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("gradient", String.class,
			"Number value,Number value1,String color1,Number value2,String color2,Object ... moreNumbersAndColors");
	static {
		VERIFIER.addDesc("Requires a value and a list of number+color pairs. It will return a color that");
		VERIFIER.addDesc(" properly represents the supplied value. For example, given the pairs 0=black and 2=white,");
		VERIFIER.addDesc("1 will return grey, any value <= 0 will return black and any value >=2 will return white. ");
		VERIFIER.addDesc("Colors must be in standard html notation (#RRGGBB), ex: #AABB88. At least 2 number+color");
		VERIFIER.addDesc("pairs are required but additional pairs can be provided in the var args parameter");
		VERIFIER.addParamDesc(0, "Value to return color for");
		VERIFIER.addParamDesc(1, "Value associated with color1", "color associated with value1(must be in #RRGGBB format)");
		VERIFIER.addParamDesc(3, "Value associated with color2", "color associated with value2(must be in #RRGGBB format)");
		VERIFIER.addParamDesc(5, "Additional value/number pairs, must be even number of elements, where even elements are a number and odd elements are a color");
		VERIFIER.addExample(.5, 0, "#000000", 1, "#FFFFFF");
		VERIFIER.addExample(1.5, 0, "#000000", 1, "#FFFFFF", 2, "#FF0000");
		VERIFIER.addExample(3, 0, "#000000", 1, "#FFFFFF", 2, "#FF0000");
		VERIFIER.addExample(-1, 0, "#000000", 1, "#FFFFFF", 2, "#FF0000");
	}

	final private ColorGradient gradient;

	public AmiWebFunctionGradient(int position, String methodName, DerivedCellCalculator[] params) {
		super(position, params);
		if (params.length % 2 != 1)
			throw new ExpressionParserException(position, "sources and targets must be in pairs.");
		for (int i = 5; i < params.length; i++) {
			if (!Number.class.isAssignableFrom(params[i].getReturnType()))
				throw new ExpressionParserException(params[i].getPosition(), "Argument " + (i + 1) + " must be a number");
			i++;
			if (!String.class.isAssignableFrom(params[i].getReturnType()))
				throw new ExpressionParserException(params[i].getPosition(), "Argument " + (i + 1) + " must be a string");
		}
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
	private ColorGradient buildGradient(Object[] values) {
		ColorGradient r = new ColorGradient();
		DerivedCellCalculator[] p = getParams();
		for (int i = 1; i < p.length;) {
			Number n = (Number) values[i++];
			String color = (String) values[i++];
			if (n == null || color == null)
				continue;
			try {
				long c = ColorHelper.parseRgbNoThrow(color);
				if (c != ColorHelper.NO_COLOR)
					r.addStop(n.doubleValue(), (int) c);
			} catch (Exception e) {
			}
		}
		return r.getStopsCount() < 1 ? null : r;
	}

	@Override
	public Object eval(Object[] values) {
		ColorGradient g = gradient != null ? gradient : buildGradient(values);
		return g == null ? null : g.toColorRgb(((Number) values[0]).doubleValue());
	}

	@Override
	protected Object shortCircuit(int i, Object val) {
		return val == null && i == 0 ? null : KEEP_GOING;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionGradient(getPosition(), getMethodName(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionGradient(position, methodName, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
