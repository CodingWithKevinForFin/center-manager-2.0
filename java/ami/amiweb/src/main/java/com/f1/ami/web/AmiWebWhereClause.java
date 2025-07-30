package com.f1.ami.web;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.tree.AmiWebTreePortlet;
import com.f1.base.CalcFrame;
import com.f1.base.Caster;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebWhereClause implements AmiWebFormulasListener {

	private AmiWebFormula formula;
	//	private String defaultWhereFilter;
	//	private String currentWhereFilter;
	private WhereClause currentWhereFilterCalc;
	private AmiWebAbstractPortlet target;
	private static final Logger log = LH.get();

	public AmiWebWhereClause(AmiWebAbstractPortlet target) {
		this.target = target;
		this.formula = this.target.formulas.addFormula("whereFilter", Boolean.class);
		this.target.formulas.addFormulasListener(this);
	}

	public WhereClause compileWhereFilter(String v, StringBuilder errorSink, com.f1.base.CalcTypes underlyingVarTypes) {
		if (SH.isnt(v))
			return null;
		try {
			AmiWebScriptManagerForLayout sm = target.getScriptManager();
			Set<String> usedConstVars = new HashSet<String>();
			DerivedCellCalculator r = sm.toCalc(v, underlyingVarTypes, target, usedConstVars);
			if (r != null && r.getReturnType() != Boolean.class) {
				errorSink.append("Where Filter must return Boolean not: " + sm.forType(r.getReturnType()));
				return null;
			}
			WhereClause r2 = new WhereClause(r, underlyingVarTypes, usedConstVars);
			return r2;
		} catch (Exception e) {
			errorSink.append("Could not compile where Filter: " + e.getMessage());
			LH.info(log, this.target.logMe(), e);
			return null;
		}
	}
	public void setDefaultWhereFilter(String v) {
		this.formula.setFormula(v, false);
	}
	public void setCurrentRuntimeFilter(String v, boolean override) {
		if (SH.isnt(v) && override)
			this.clearOverride();
		else
			this.formula.setFormula(v, override);
	}

	private void clearOverride() {
		this.formula.clearFormulaOverride();
	}

	public static class WhereClause implements CalcFrame {

		final private DerivedCellCalculator calc;
		final private BasicCalcTypes types;
		final private Map<String, Caster<?>> casters;
		final private Set<String> usedConstVars;
		private CalcFrame inner;

		public WhereClause(DerivedCellCalculator calc, com.f1.base.CalcTypes types, Set<String> usedConstVars) {
			if (types == null)
				throw new NullPointerException();
			this.calc = calc;
			this.types = new BasicCalcTypes(types);
			this.casters = new HasherMap<String, Caster<?>>();
			for (String i : types.getVarKeys()) {
				this.casters.put(i, OH.getCaster(types.getType(i)));
			}
			this.usedConstVars = usedConstVars;
		}

		@Override
		public Object getValue(String key) {
			Caster<?> type = casters.get(key);
			if (type == null) {
				LH.warning(log, "WHERE clause references missing var: " + key);
				return null;
			}
			Object o = inner.getValue(key);
			return type.castNoThrow(o);
		}

		public boolean eval(CalcFrame values, ReusableCalcFrameStack sf) {
			this.inner = values;
			return Boolean.TRUE.equals(this.calc.get(sf.reset(this)));
		}

		public Class<?> getReturnType() {
			return calc.getReturnType();
		}

		@Override
		public Object putValue(String key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?> getType(String key) {
			return inner.getType(key);
		}

		@Override
		public Iterable<String> getVarKeys() {
			return inner.getVarKeys();
		}

		@Override
		public boolean isVarsEmpty() {
			return inner.isVarsEmpty();
		}

		@Override
		public int getVarsCount() {
			return inner.getVarsCount();
		}

	}

	public String getDefaultWhereFilter() {
		return this.formula.getFormula(false);
	}
	public String getCurrentRuntimeFilter() {
		return this.formula.getFormula(true);
	}
	public boolean hasWhereFilter() {
		return this.formula.getFormulaCalc() != null;
	}
	public boolean meetsWhereFilter(CalcFrame values, ReusableCalcFrameStack sf) {
		if (currentWhereFilterCalc == null)
			return true;
		return this.currentWhereFilterCalc.eval(values, sf);
	}

	//returns true if changed
	public boolean resetWhere() {
		DerivedCellCalculator old = this.formula.getFormulaCalc();
		this.formula.clearFormulaOverride();
		this.formula.recompileFormula();
		DerivedCellCalculator nuw = this.formula.getFormulaCalc();
		return OH.ne(old, nuw) && this.formula.getFormulaError(true) == null;
	}

	public void init(Map<String, Object> configuration) {
		this.formula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "whereFilter", null));
	}

	public void getConfiguration(Map<String, Object> r) {
		AmiWebUtils.putSkipEmpty(r, "whereFilter", this.formula.getFormulaConfig());
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		if (formula == this.formula) {
			WhereClause t = compileWhereFilter(formula.getFormula(true), new StringBuilder(), target.getFormulaVarTypes(formula));
			//			if (OH.ne(t, this.currentWhereFilterCalc)) {
			if (t != null && t.calc.isConst() && Boolean.TRUE.equals(t.getValue(null)))
				this.currentWhereFilterCalc = null;
			else
				this.currentWhereFilterCalc = t;
			if (target instanceof AmiWebAbstractTablePortlet)
				((AmiWebAbstractTablePortlet) this.target).onWhereFormulaChanged();
			else if (target instanceof AmiWebTreePortlet)
				((AmiWebTreePortlet) this.target).onWhereFormulaChanged();

		}
	}
}
