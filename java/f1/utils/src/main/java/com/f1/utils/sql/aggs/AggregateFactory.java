package com.f1.utils.sql.aggs;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.MethodFactoryManagerWrapper;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class AggregateFactory extends MethodFactoryManagerWrapper {
	static private void addAggMethodFactory(AggMethodFactory mf) {
		AGG_METHOD_FACTORIES.put(mf.getDefinition().getMethodName(), mf);
	}

	public final static Map<String, AggMethodFactory> AGG_METHOD_FACTORIES = new HasherMap<String, AggMethodFactory>(CaseInsensitiveHasher.INSTANCE);

	static {
		addAggMethodFactory(AvgAggCalculator.FACTORY);
		addAggMethodFactory(BetaAggCalculator.FACTORY);
		addAggMethodFactory(CatAggCalculator.FACTORY);
		addAggMethodFactory(CatUniqueAggCalculator.FACTORY);
		addAggMethodFactory(CatUniqueAggCalculator.FACTORY_ASC);
		addAggMethodFactory(CatUniqueAggCalculator.FACTORY_DES);
		addAggMethodFactory(CatUniqueLimitAggCalculator.FACTORY_LIM);
		addAggMethodFactory(CatUniqueLimitAggCalculator.FACTORY_LIM_ASC);
		addAggMethodFactory(CatUniqueLimitAggCalculator.FACTORY_LIM_DES);
		addAggMethodFactory(CksumAggCalculator.FACTORY);
		addAggMethodFactory(CorAggCalculator.FACTORY);
		addAggMethodFactory(CountAggCalculator.FACTORY);
		addAggMethodFactory(CountUniqueAggCalculator.FACTORY);
		addAggMethodFactory(CovAggCalculator.FACTORY);
		addAggMethodFactory(CovAggCalculatorSample.FACTORY);
		addAggMethodFactory(ExponentialAvgAggCalculator.FACTORY);
		addAggMethodFactory(FirstAggCalculator.FACTORY);
		addAggMethodFactory(GaussianAvgAggCalculator.FACTORY);
		addAggMethodFactory(LastAggCalculator.FACTORY);
		addAggMethodFactory(LinearRegressionAggCalculator.FACTORY);
		addAggMethodFactory(MaxAggCalculator.FACTORY);
		addAggMethodFactory(MedianAggCalculator.FACTORY);
		addAggMethodFactory(MinAggCalculator.FACTORY);
		addAggMethodFactory(PercentileContAggCalculator.FACTORY);
		addAggMethodFactory(PercentileDiscAggCalculator.FACTORY);
		addAggMethodFactory(PolynomialLinearRegressionAggCalculator.FACTORY);
		addAggMethodFactory(StdevAggCalculator.FACTORY);
		addAggMethodFactory(StdevAggCalculatorSample.FACTORY);
		addAggMethodFactory(SumAggCalculator.FACTORY);
		addAggMethodFactory(VarAggCalculator.FACTORY);
		addAggMethodFactory(VarAggCalculatorSample.FACTORY);
	}

	final private Map<AggCalculator, AggCalculator> calculators = new LinkedHashMap<AggCalculator, AggCalculator>();

	public AggregateFactory(MethodFactoryManager inner) {
		super(inner);
	}

	public Collection<AggCalculator> getAggregates() {
		return calculators.values();
	}
	public int getAggregatesCount() {
		return this.calculators.size();
	}
	public void clearAggregates() {
		calculators.clear();
	}
	public AggCalculator removeAggregate(final AggCalculator calc) {
		return this.calculators.remove(calc);
	}

	@Override
	public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack context) {
		AggMethodFactory mf = AGG_METHOD_FACTORIES.get(methodName);
		if (mf != null && mf.getDefinition().canAccept(calcs))
			return getExisting(mf.toMethod(position, methodName, calcs, context));
		return super.toMethod(position, methodName, calcs, context);
	}
	private DerivedCellCalculator getExisting(AggCalculator r) {
		AggCalculator existing = calculators.get(r);
		if (existing == null)
			calculators.put(r, existing = r);
		return existing;
	}

}
