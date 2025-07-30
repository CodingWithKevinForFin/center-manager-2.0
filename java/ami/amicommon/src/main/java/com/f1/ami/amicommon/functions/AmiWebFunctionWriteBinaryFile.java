package com.f1.ami.amicommon.functions;

import java.io.File;
import java.io.IOException;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionWriteBinaryFile extends AbstractMethodDerivedCellCalculator3 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("writeBinaryFile", Boolean.class, "String filename,com.f1.base.Bytes data,Boolean append");
	static {
		VERIFIER.addDesc("Writes binary data to a file. Returns true if success, false otherwise.");
		VERIFIER.addParamDesc(0, "The string to convert");
		VERIFIER.addParamDesc(1, "binary data");
		VERIFIER.addParamDesc(2, "If file exists, should it append?");
	}

	public AmiWebFunctionWriteBinaryFile(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		String pathname = (String) o0;
		if (SH.isnt(pathname))
			return Boolean.FALSE;
		Bytes data = (Bytes) o1;
		if (data == null)
			return Boolean.FALSE;
		try {
			File file = new File(pathname);
			Boolean append = (Boolean) o2;
			if (Boolean.TRUE.equals(append))
				IOH.appendData(file, data.getBytes());
			else
				IOH.writeData(file, data.getBytes());
			return Boolean.TRUE;
		} catch (IOException e) {
			return Boolean.FALSE;
		}
	}
	@Override
	protected boolean shortCircuitNull() {
		return false;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionWriteBinaryFile(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionWriteBinaryFile(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
