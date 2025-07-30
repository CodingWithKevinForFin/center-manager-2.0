package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.AggregateTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;

public class AmiWebFormulasImpl implements AmiWebFormulas {

	private static final AmiWebFormulasListener[] EMPTY_LISTENERS = new AmiWebFormulasListener[0];
	private static final Logger log = LH.get();
	private Map<String, AmiWebFormula> formulas = new HashMap<String, AmiWebFormula>();
	private AmiWebDomObject thiz;
	private AmiWebFormulasListener[] listeners = EMPTY_LISTENERS;
	private AggregateFactory aggregateFactory;
	private AggregateTable aggregateTable;

	public AmiWebFormulasImpl(AmiWebDomObject thiz) {
		this.thiz = thiz;
	}

	@Override
	public AmiWebFormula getFormula(String s) {
		return this.formulas.get(s);
	}

	@Override
	public Set<String> getFormulaIds() {
		return this.formulas.keySet();
	}

	@Override
	public AmiWebDomObject getThis() {
		return thiz;
	}

	public AmiWebFormula addFormula(String key, byte type, Class<?> returnType) {
		final AmiWebFormula r = new AmiWebFormulaImpl(key, this, type, returnType);
		CH.putOrThrow(this.formulas, key, r);
		return r;
	}
	public AmiWebFormula addFormula(String key, Class<?> returnType) {
		return addFormula(key, AmiWebFormula.FORMULA_TYPE_NORMAL, returnType);
	}
	public AmiWebFormula addFormulaAgg(String key, Class<?> returnType) {
		return addFormula(key, AmiWebFormula.FORMULA_TYPE_AGG, returnType);
	}
	public AmiWebFormula addFormulaAggRt(String key, Class<?> returnType) {
		return addFormula(key, AmiWebFormula.FORMULA_TYPE_AGG_RT, returnType);
	}
	public AmiWebFormula addFormulaTemplate(String key) {
		return addFormula(key, AmiWebFormula.FORMULA_TYPE_TEMPLATE, String.class);
	}

	public void addFormula(AmiWebFormula formula) {
		CH.putOrThrow(this.formulas, formula.getFormulaId(), formula);
	}
	public void addOrReplaceFormula(AmiWebFormula formula) {
		//		System.out.println(formula.getAri() + ":: " + formula.getFormulaConfig());
		this.formulas.put(formula.getFormulaId(), formula);
	}

	@Override
	public void addFormulasListener(AmiWebFormulasListener listener) {
		this.listeners = AH.append(this.listeners, listener);
	}

	@Override
	public void removeFormulasListener(AmiWebFormulasListener listener) {
		this.listeners = AH.remove(this.listeners, listener);
	}

	@Override
	public void fireOnFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		for (AmiWebFormulasListener i : this.listeners) {
			try {
				i.onFormulaChanged(formula, old, nuw);
			} catch (Exception e) {
				LH.warning(log, thiz.getService().getUserName() + " ==> ", thiz.getAri(), ": Error updating formula " + formula.getFormulaId(), ": ", e);
			}
		}
		thiz.getService().fireOnFormulaChanged(formula, old, nuw);
	}

	@Override
	public void recompileAmiscript() {
		//		if (this.aggregateFactory != null)
		//			this.aggregateFactory.clearAggregates();
		for (AmiWebFormula i : this.formulas.values())
			i.recompileFormula();
	}

	@Override
	public AggregateFactory getAggregateFactory() {
		return this.aggregateFactory;
	}

	public void setAggregateFactory(AggregateFactory aggregateFactory) {
		OH.assertEmpty(this.formulas);
		OH.assertNull(this.aggregateFactory);
		this.aggregateFactory = aggregateFactory;
	}

	@Override
	public AggregateTable getAggregateTable() {
		return aggregateTable;
	}

	public void setAggregateTable(AggregateTable aggregateTable) {
		this.aggregateTable = aggregateTable;
	}

	public AmiWebFormula removeFormula(String name) {
		return this.formulas.remove(name);
	}

	@Override
	public DerivedCellCalculator parseFormula(AmiWebFormula f, String formula, Set<String> usedVars) {
		final AmiWebFormulas of = this;
		final AmiWebDomObject t = of.getThis();
		final AmiWebScriptManagerForLayout sm = t.getService().getScriptManager(t.getAmiLayoutFullAlias());
		com.f1.base.CalcTypes vars = t.getFormulaVarTypes(f);
		AmiWebDomObject this1 = getThis();
		sm.getParser();
		DerivedCellCalculator r;
		switch (f.getFormulaType()) {
			case AmiWebFormula.FORMULA_TYPE_NORMAL:
				r = sm.toCalc(formula, vars, this1, usedVars);
				break;
			case AmiWebFormula.FORMULA_TYPE_AGG:
				r = sm.toAggCalc(formula, vars, of.getAggregateFactory(), this1, usedVars);
				break;
			case AmiWebFormula.FORMULA_TYPE_AGG_RT:
				r = sm.toAggCalc(formula, vars, of.getAggregateTable(), this1, usedVars);
				break;
			case AmiWebFormula.FORMULA_TYPE_TEMPLATE:
				r = sm.toCalcTemplate(formula, vars, this1, usedVars);
				break;
			default:
				throw new IllegalStateException();
		}
		if (r == null)
			return null;
		DerivedCellCalculator pause = DerivedHelper.findFirstPausable(r);
		if (pause != null)
			throw new ExpressionParserException(0, "Formulas can not call deferred statements: " + pause);
		return r;
	}

	@Override
	public boolean onVarConstChanged(String var) {
		boolean r = false;
		for (AmiWebFormula i : formulas.values())
			if (i.getUsedConstVars().contains(var)) {
				i.recompileFormula();
				r = true;
			}
		return r;
	}

}
