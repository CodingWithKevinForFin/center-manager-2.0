package com.f1.ami.amicommon.functions;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionReadBinaryFile extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("readBinaryFile", Bytes.class, "String fileName");
	static {
		VERIFIER.addDesc("Returns a Bytes Object that stores the contents of the given binary file.");
		VERIFIER.addParamDesc(0, "The file name");
	}

	public AmiWebFunctionReadBinaryFile(int position, DerivedCellCalculator params) {
		super(position, params);
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
	public Object eval(Object o) {
		String pathname = (String) o;
		if (SH.isnt(pathname))
			return null;
		try {
			if (pathname.contains("://")) {
				byte[] r = IOH.doGet(new URL(pathname), null, null);
				return Bytes.valueOf(r);
			}
			if (pathname.startsWith("data:")) {
				String data = SH.afterFirst(pathname, ';');
				String type = SH.beforeFirst(data, ',');
				String payload = SH.afterFirst(data, ',');
				if ("base64".equalsIgnoreCase(type))
					return Bytes.valueOf(EncoderUtils.decode64(payload));
				else if ("base16".equalsIgnoreCase(type))
					return Bytes.valueOf(EncoderUtils.decode16(payload));
				else
					return null;
			}
			File file = new File(pathname);
			if (!file.isFile() || !file.canRead())
				return null;
			if (IOH.isSymlink(file))
				return Bytes.valueOf(IOH.readData(new File(IOH.getFullPath(file))));
			else
				return Bytes.valueOf(IOH.readData(file));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionReadBinaryFile(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionReadBinaryFile(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
