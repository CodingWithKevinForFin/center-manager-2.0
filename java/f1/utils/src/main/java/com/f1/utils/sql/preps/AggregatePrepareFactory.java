package com.f1.utils.sql.preps;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.MethodFactoryManagerWrapper;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class AggregatePrepareFactory extends MethodFactoryManagerWrapper {
	static private void addAggMethodFactory(PrepMethodFactory mf) {
		PREP_METHOD_FACTORIES.put(mf.getDefinition().getMethodName(), mf);
	}

	public final static Map<String, PrepMethodFactory> PREP_METHOD_FACTORIES = new HasherMap<String, PrepMethodFactory>(CaseInsensitiveHasher.INSTANCE);

	static {

		addAggMethodFactory(CountAggCalculator.FACTORY);
		addAggMethodFactory(DeltaNormalizeAggCalculator.FACTORY);
		addAggMethodFactory(EmGmmClusteringCalculator.FACTORY);
		addAggMethodFactory(GenerateIdAggCalculator.FACTORY);
		addAggMethodFactory(InterpolationCalculator.FACTORY);
		addAggMethodFactory(KMeansClusteringCalculator.FACTORY);
		addAggMethodFactory(LastAggCalculator.FACTORY);
		addAggMethodFactory(NormalizeAggCalculator.FACTORY);
		addAggMethodFactory(OffsetAggCalculator.FACTORY);
		addAggMethodFactory(RankAggCalculator.FACTORY);
		addAggMethodFactory(ResampleCalculator.FACTORY);
		addAggMethodFactory(RunningSumAggCalculator.FACTORY);
		addAggMethodFactory(SegmentByLocalExtremaCalculator.FACTORY);
		addAggMethodFactory(ShuffleAggCalculator.FACTORY);
		addAggMethodFactory(UniqueRankAggCalculator.FACTORY);
		addAggMethodFactory(VariationalInferenceGmmClusteringCalculator.FACTORY);
		addAggMethodFactory(MovingAvgAggCalculator.FACTORY);

	}
	final private Map<AbstractPrepCalculator, AbstractPrepCalculator> calculators = new LinkedHashMap<AbstractPrepCalculator, AbstractPrepCalculator>();

	public AggregatePrepareFactory(MethodFactoryManager inner) {
		super(inner);
	}
	public Collection<AbstractPrepCalculator> getAggregates() {
		return calculators.values();
	}
	public void clearAggregates() {
		calculators.clear();
	}

	@Override
	public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack context) {
		PrepMethodFactory mf = PREP_METHOD_FACTORIES.get(methodName);
		if (mf != null && mf.getDefinition().canAccept(calcs))
			return getExisting(mf.toMethod(position, methodName, calcs, context));
		return super.toMethod(position, methodName, calcs, context);
	}
	private DerivedCellCalculator getExisting(AbstractPrepCalculator r) {
		AbstractPrepCalculator existing = calculators.get(r);
		if (existing == null)
			calculators.put(r, existing = r);
		return existing;
	}

}
