package com.f1.ami.amicommon.functions;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Mapping;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionGetBestImageType extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("getBestImageType", String.class, "String hexColor");
	static {
		VERIFIER.addDesc(
				"This method helps to decide which type of image to use based on the background color on the method parameter. For example, if the background color is of a darker shade, it returns WHITE. Possible return values: BLACK, WHITE, COLOR, null (for invalid input).");
		VERIFIER.addParamDesc(0, "color in hex format (i.e #FF00FF)");
	}

	public AmiWebFunctionGetBestImageType(int position, DerivedCellCalculator param) {
		super(position, param);
	}

	@Override
	public Object eval(Object p1) {
		String hexColor = Caster_String.INSTANCE.cast(p1);
		byte imageType = AmiUtils.getBestImageType(hexColor);
		switch (imageType) {
			case AmiUtils.BLACK:
				return "BLACK";
			case AmiUtils.COLOR1:
			case AmiUtils.COLOR2:
				return "COLOR";
			case AmiUtils.WHITE:
				return "WHITE";
			default:
				return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params) {
		return new AmiWebFunctionGetBestImageType(getPosition(), params);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionGetBestImageType(position, calcs[0]);
		}

	}

}
