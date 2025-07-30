package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrStrip extends AbstractMethodDerivedCellCalculator3 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strStrip", String.class, "String text,String prefix,String suffix");
	static {
		VERIFIER.addDesc(
				"Returns the substring of supplied text with the prefix and suffix removed. If the string doesn't start with the specified prefix, then the prefix is ignored. If the string doesn't end with suffix, then the suffix is ignored.");
		VERIFIER.addParamDesc(0, "The text to use as base for return value");
		VERIFIER.addParamDesc(1, "The prefix to strip from beginning of base text");
		VERIFIER.addParamDesc(2, "The suffix to strip from end of base text");
		VERIFIER.addExample("What is Going On", "What", "On");
		VERIFIER.addExample("WhatOn", "What", "On");
		VERIFIER.addExample("When are we on", "What", "On");
	}

	public AmiWebFunctionStrStrip(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		String textVal = (String) o0;
		String prefixVal = (String) o1;
		String suffixVal = (String) o2;
		return SH.strip(textVal, prefixVal, suffixVal, false);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionStrStrip(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrStrip(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
