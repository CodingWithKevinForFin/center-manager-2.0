package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionIsInstanceOf extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("isInstanceOf", Boolean.class, "Object value,String className");

	static {
		VERIFIER.addDesc("Returns true if the value is an instance of the named class, false otherwise.");
		VERIFIER.addParamDesc(0, "any value", "A valid class name, such as String, Long, TableSet, ....");
		VERIFIER.addExample(1234L, "Long");
		VERIFIER.addExample(1234L, "Integer");
		VERIFIER.addExample("test", "String");
		VERIFIER.addExample(null, "String");
	}

	final private MethodFactoryManager methodFactory;

	public AmiWebFunctionIsInstanceOf(int position, DerivedCellCalculator param0, DerivedCellCalculator param1, MethodFactoryManager methodFactory) {
		super(position, param0, param1);
		this.methodFactory = methodFactory;
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected boolean shortCircuitNull() {
		return false;
	}

	@Override
	protected Class get1(Object o) {
		return methodFactory.forNameNoThrow((String) o);
	}

	@Override
	public Object eval(Object p0, Object p1) {
		if (p0 == null || p1 == null)
			return Boolean.FALSE;
		Class<? extends Object> t = (Class<? extends Object>) p1;
		return t.isAssignableFrom(p0.getClass());
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionIsInstanceOf(getPosition(), p0, p1, this.methodFactory);
	}

	public static class Factory implements AmiWebFunctionFactory {

		private MethodFactoryManager methodFactory;

		public Factory(MethodFactoryManager methodFactory) {
			OH.assertNotNull(methodFactory);
			this.methodFactory = methodFactory;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionIsInstanceOf(position, calcs[0], calcs[1], methodFactory);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
