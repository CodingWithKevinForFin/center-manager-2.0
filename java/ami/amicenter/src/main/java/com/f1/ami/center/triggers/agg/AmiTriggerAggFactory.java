package com.f1.ami.center.triggers.agg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.MethodFactoryManagerWrapper;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class AmiTriggerAggFactory extends MethodFactoryManagerWrapper {

	private static final String CAT = "cat";
	private static final String SUM = "sum";
	private static final String MIN = "min";
	private static final String MAX = "max";
	private static final String COUNT = "count";
	private static final String COUNTUNIQUE = "countUnique";
	private static final String FIRST = "first";
	private static final String LAST = "last";
	private static final String AVG = "avg";
	private static final String VAR = "var";
	private static final String VARS = "varS";
	private static final String STDEV = "stdev";
	private static final String STDEVS = "stdevS";
	private static final String COVAR = "covar";
	private static final String COVARS = "covarS";
	private static final Object COR = "cor";
	private static final Object BETA = "beta";
	final private Map<AmiTriggerAgg, AmiTriggerAgg> calculators = new HashMap<AmiTriggerAgg, AmiTriggerAgg>();
	final private Map<AmiTriggerAgg2, AmiTriggerAgg2> calculators2 = new HashMap<AmiTriggerAgg2, AmiTriggerAgg2>();
	final private AmiTrigger_Aggregate trigger;

	public Collection<AmiTriggerAgg> getAggregates() {
		return calculators.values();
	}
	public Collection<AmiTriggerAgg2> getAggregates2() {
		return calculators2.values();
	}
	public void clearAggregates() {
		calculators.clear();
		calculators2.clear();
	}

	public AmiTriggerAggFactory(MethodFactoryManager inner, AmiTrigger_Aggregate trigger) {
		super(inner);
		this.trigger = trigger;
	}
	public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack context) {
		if (SUM.equals(methodName) || COUNT.equals(methodName) || COUNTUNIQUE.equals(methodName) || AVG.equals(methodName) || VAR.equals(methodName) || VARS.equals(methodName)
				|| STDEV.equals(methodName) || STDEVS.equals(methodName) || FIRST.equals(methodName) || LAST.equals(methodName) || MIN.equals(methodName)
				|| MAX.equals(methodName)) {
			if (calcs.length != 1)
				throw new ExpressionParserException(position, methodName + "(...) takes one parameter, not " + calcs.length);
			AmiTriggerAgg r = null;
			if (SUM.equals(methodName))
				r = new AmiTriggerAgg_Sum(position, calcs[0]);
			else if (COUNT.equals(methodName))
				r = new AmiTriggerAgg_Count(position, calcs[0]);
			else if (COUNTUNIQUE.equals(methodName))
				r = new AmiTriggerAgg_CountUnique(position, calcs[0]);
			else if (AVG.equals(methodName))
				r = new AmiTriggerAgg_Avg(position, calcs[0]);
			else if (VAR.equals(methodName))
				r = new AmiTriggerAgg_Var(position, calcs[0]);
			else if (VARS.equals(methodName))
				r = new AmiTriggerAgg_Var(position, calcs[0], true);
			else if (STDEV.equals(methodName))
				r = new AmiTriggerAgg_Stdev(position, calcs[0]);
			else if (STDEVS.equals(methodName))
				r = new AmiTriggerAgg_Stdev(position, calcs[0], true);
			else if (FIRST.equals(methodName))
				r = new AmiTriggerAgg_First(position, calcs[0], trigger.getPool());
			else if (LAST.equals(methodName))
				r = new AmiTriggerAgg_Last(position, calcs[0], trigger.getPool());
			else if (MIN.equals(methodName))
				r = new AmiTriggerAgg_Min(position, calcs[0], trigger.getPool());
			else if (MAX.equals(methodName))
				r = new AmiTriggerAgg_Max(position, calcs[0], trigger.getPool());
			AmiTriggerAgg existing = calculators.get(r);
			if (existing == null)
				calculators.put(r, existing = r);
			return existing;
		} else if (COVAR.equals(methodName) || COVARS.equals(methodName) || COR.equals(methodName) || BETA.equals(methodName)) {
			if (calcs.length != 2)
				throw new ExpressionParserException(position, methodName + "(...) takes two parameters, not " + calcs.length);
			AmiTriggerAgg2 r = null;
			if (COVAR.equals(methodName))
				r = new AmiTriggerAgg_Cov(position, calcs[0], calcs[1]);
			else if (COVARS.equals(methodName))
				r = new AmiTriggerAgg_Cov(position, calcs[0], calcs[1], true);
			else if (COR.equals(methodName))
				r = new AmiTriggerAgg_Cor(position, calcs[0], calcs[1]);
			else if (BETA.equals(methodName))
				r = new AmiTriggerAgg_Beta(position, calcs[0], calcs[1]);
			AmiTriggerAgg2 existing = calculators2.get(r);
			if (existing == null)
				calculators2.put(r, existing = r);
			return existing;
		}
		return super.toMethod(position, methodName, calcs, context);
	}
}
