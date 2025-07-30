package com.f1.ami.web;

import java.util.Set;

import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.structs.table.derived.AggregateTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public interface AmiWebFormulas {

	public void addFormulasListener(AmiWebFormulasListener listener);
	public void removeFormulasListener(AmiWebFormulasListener listener);

	public AmiWebFormula getFormula(String s);

	public Set<String> getFormulaIds();

	public AmiWebDomObject getThis();

	void fireOnFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw);

	public void recompileAmiscript();

	public AggregateFactory getAggregateFactory();//returns null if not an aggregate factory

	public AggregateTable getAggregateTable();//returns null if not using a realtime aggregate table
	public DerivedCellCalculator parseFormula(AmiWebFormula f, String formula, Set<String> usedVars);

	public boolean onVarConstChanged(String var);
}
