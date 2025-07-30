package com.f1.ami.web;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ToDerivedString;

public class AmiWebFormulaImpl implements AmiWebFormula, ToDerivedString {
	private static final Logger log = LH.get();

	final private String formulaId;
	final private AmiWebFormulas owner;
	final private AmiWebOverrideValue<String> formula = new AmiWebOverrideValue<String>(null);
	final private byte type;
	final private Class<?> returnType;
	private DerivedCellCalculator calc;
	private DerivedCellCalculator currentCalc;
	private Exception exception;
	private Exception currentException;
	private Set<String> usedConstVars = new HashSet<String>();

	public AmiWebFormulaImpl(String formulaId, AmiWebFormulas owner, byte type, Class<?> returnType) {
		this.formulaId = formulaId;
		this.returnType = returnType;
		this.owner = owner;
		this.type = type;
		switch (type) {
			case FORMULA_TYPE_AGG:
				OH.assertNotNull(owner.getAggregateFactory());
				break;
			case FORMULA_TYPE_AGG_RT:
				OH.assertNotNull(owner.getAggregateTable());
				break;
		}
		OH.assertNotNull(this.owner);
	}

	@Override
	public DerivedCellCalculator getFormulaCalc() {
		return currentCalc;
	}

	@Override
	public String getFormulaId() {
		return this.formulaId;
	}

	@Override
	public AmiWebFormulas getOwnerFormulas() {
		return owner;
	}

	@Override
	public String getFormula(boolean override) {
		return this.formula.getValue(override);
	}

	@Override
	public void setFormula(String amiscript, boolean override) {
		if (this.formula.setValue(amiscript, override) || this.currentException != null) {
			recompileFormula();
		}
	}

	@Override
	public String getFormulaConfig() {
		return getFormula(false);
	}

	@Override
	public void initFormula(String value) {
		this.formula.setValue(value, false);
		this.exception = null;
		this.currentException = null;
		this.currentCalc = null;
		this.calc = null;
		this.usedConstVars.clear();
	}

	@Override
	public void recompileFormula() {
		Exception oldException = this.currentException;
		this.exception = null;
		this.currentException = null;
		if (this.formula.get() == null && !this.hasFormulaOverride() && this.currentCalc == null)
			return;
		DerivedCellCalculator old = this.currentCalc;
		this.usedConstVars.clear();
		try {
			this.calc = toCalc(this.getFormula(false), this.usedConstVars);
		} catch (Exception e) {
			this.calc = null;
			this.exception = e;
			this.usedConstVars.clear();
			if (!(e instanceof ExpressionParserException))
				LH.warning(log, "For " + this.getAri(), e);
		}
		if (formula.isOverride()) {
			try {
				this.currentCalc = toCalc(this.getFormula(true), this.usedConstVars);
			} catch (Exception e) {
				this.currentException = e;
				this.currentCalc = null;
				this.usedConstVars.clear();
			}
		} else {
			this.currentCalc = calc;
			this.currentException = exception;
		}
		if (!DerivedHelper.areSame(old, currentCalc) || (currentException == null) != (oldException == null))
			this.getOwnerFormulas().fireOnFormulaChanged(this, old, currentCalc);
	}

	@Override
	public Exception getFormulaError(boolean override) {
		return override ? this.currentException : this.exception;
	}

	@Override
	public void clearFormulaOverride() {
		if (this.formula.clearOverride()) {
			recompileFormula();
		}
	}

	@Override
	public boolean hasFormulaOverride() {
		return this.formula.isOverride();
	}
	@Override
	public String getAri() {
		return this.getOwnerFormulas().getThis().getAri() + AmiWebConsts.FORMULA_PREFIX_DELIM + this.getFormulaId();
	}

	@Override
	public byte getFormulaType() {
		return this.type;
	}

	@Override
	public Class<?> getFormulaReturnType() {
		return returnType;
	}

	@Override
	public Exception testFormula(String str) {
		try {
			owner.parseFormula(this, str, null);
		} catch (Exception e) {
			return e;
		}
		return null;
	}

	@Override
	public DerivedCellCalculator toCalc(String str, Set<String> usedConstVarsSink) {
		if (SH.isnt(str))
			return null;
		return owner.parseFormula(this, str, usedConstVarsSink);
	}

	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}

	@Override
	public Set<String> getUsedConstVars() {
		return this.usedConstVars;
	}

}
