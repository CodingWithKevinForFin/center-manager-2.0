package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrClassName extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strClassName", String.class, "Object value");

	static {
		VERIFIER.addDesc("Returns the class name of the selected value.");
		VERIFIER.addExample(1234L);
		VERIFIER.addExample(123);
		VERIFIER.addExample(null);
		VERIFIER.addExample(true);
		VERIFIER.addExample("test");
	}

	final private MethodFactoryManager methodFactory;

	public AmiWebFunctionStrClassName(int position, DerivedCellCalculator params, MethodFactoryManager methodFactory) {
		super(position, params);
		this.methodFactory = methodFactory;
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object value) {
		return value == null ? null : this.methodFactory.forType(value.getClass());
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionStrClassName(getPosition(), params2, this.methodFactory);
	}

	public static class Factory implements AmiWebFunctionFactory {

		private MethodFactoryManager methodFactory;

		public Factory(MethodFactoryManager methodFactory) {
			OH.assertNotNull(methodFactory);
			this.methodFactory = methodFactory;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrClassName(position, calcs[0], methodFactory);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
