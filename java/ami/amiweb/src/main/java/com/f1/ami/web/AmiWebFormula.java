package com.f1.ami.web;

import java.util.Set;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public interface AmiWebFormula {

	static byte FORMULA_TYPE_NORMAL = 1;
	static byte FORMULA_TYPE_AGG = 2;
	static byte FORMULA_TYPE_AGG_RT = 3;
	static byte FORMULA_TYPE_TEMPLATE = 4;

	public byte getFormulaType();
	public String getFormula(boolean override);//return original if no override
	public void setFormula(String amiscript, boolean override);//if override is false, then any override is cleared out
	public DerivedCellCalculator getFormulaCalc();//returns current calc (if overridden, returns override)
	public String getFormulaId();
	public AmiWebFormulas getOwnerFormulas();
	public Class<?> getFormulaReturnType();

	public String getFormulaConfig();
	public void initFormula(String value);

	public void recompileFormula();//recompiles both original and override(if has override)
	public Exception getFormulaError(boolean override);//return original if no override

	public boolean hasFormulaOverride();
	public void clearFormulaOverride();
	public String getAri();

	public Exception testFormula(String str);
	public DerivedCellCalculator toCalc(String str, Set<String> usedVarsSink);
	public Set<String> getUsedConstVars();

}
