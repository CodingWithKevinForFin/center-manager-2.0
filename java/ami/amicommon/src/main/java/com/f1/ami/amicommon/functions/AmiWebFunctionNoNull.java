package com.f1.ami.amicommon.functions;

import org.python.bouncycastle.util.Arrays;

import com.f1.base.Caster;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionNoNull extends AbstractMethodDerivedCellCalculatorN {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("noNull", Object.class, "Object value1,Object value2, Object ... additionalValues");
	static {
		VERIFIER.addDesc(
				"Returns the first non-null value supplied (starting at left). If all values are null, then null is returned. Returns null if both supplied values are null.");
		VERIFIER.addParamDesc(0, "value to test for null and return, unless null");
		VERIFIER.addExample(32, 46);
		VERIFIER.addExample(null, 46);
		VERIFIER.addExample(null, null, 20, null);
		VERIFIER.addExample(null, null, null, null);
	}

	final private Caster<?> returnType;
	private Object constVal;

	public AmiWebFunctionNoNull(int position, DerivedCellCalculator[] params) {
		super(position, params);
		Class rt = null;
		for (int i = 0; i < params.length; i++)
			rt = OH.getWidestIgnoreNull(rt, params[i].getReturnType());
		this.returnType = OH.getCaster(rt);
		int[] consts = evalConsts();
		for (int i : consts) {
			Object val = getBuf()[i];
			if (val != null) {
				this.constVal = this.returnType.cast(val);
				//get rid of any non-const params after first const that is not null
				for (int j = 0; j < notConsts.length; j++) {
					if (notConsts[j] > i) {
						super.notConsts = Arrays.copyOf(super.notConsts, j);
						break;
					}
				}
				break;
			}
		}
		if (consts.length == 0 || consts[0] != 0)//if the first one isn't a const
			this.shortCircuitConstant = KEEP_GOING;
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	public Object eval(Object[] vals) {
		return constVal;
	}

	@Override
	protected Object shortCircuit(int i, Object val) {
		return val != null ? val : KEEP_GOING;
	}

	@Override
	public Class<?> getReturnType() {
		return returnType.getCastToClass();
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionNoNull(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionNoNull(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
