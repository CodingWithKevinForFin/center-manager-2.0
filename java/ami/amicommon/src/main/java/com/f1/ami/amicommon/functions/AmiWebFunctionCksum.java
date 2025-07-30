package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.Cksum;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionCksum extends AbstractMethodDerivedCellCalculatorN {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("cksum", Long.class, "Object first, Object ... additional");
	static {
		VERIFIER.addDesc("Returns a Long that is the checksum of bytes in supplied binary data. Returns null if data is null.");
		VERIFIER.addParamDesc(0, "data to get length for");
	}

	public AmiWebFunctionCksum(int position, DerivedCellCalculator[] params) {
		super(position, params);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object[] o) {
		if (o.length == 0)
			return Cksum.cksum(OH.EMPTY_BYTE_ARRAY, 0, 0);
		else if (o.length == 1) {
			byte[] bytes = OH.toBytes(o[0]);
			return Cksum.cksum(bytes);
		} else {
			FastByteArrayDataOutputStream out = new FastByteArrayDataOutputStream();
			for (Object i : o)
				out.write(OH.toBytes(i));
			return Cksum.cksum(out.getBuffer(), 0, out.getCount());
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionCksum(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionCksum(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
