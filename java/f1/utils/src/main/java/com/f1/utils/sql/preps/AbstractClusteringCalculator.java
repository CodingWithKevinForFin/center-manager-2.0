package com.f1.utils.sql.preps;

import com.f1.base.CalcFrame;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

abstract public class AbstractClusteringCalculator extends AbstractPrepCalculator {
	private final DerivedCellCalculator nClustersCalc;
	private int nClusters;
	private boolean isNull;
	private final DerivedCellCalculator[] inputs;
	private int[] clusters;
	private final Long seed;
	private final DerivedCellCalculator seedCalc;

	public AbstractClusteringCalculator(int position, DerivedCellCalculator[] inners) {
		super(position, inners);
		if (this.inners.length < 3) {
			throw new ExpressionParserException(position, getMethodName() + "(...) takes at least three parameters: (Object values ... , int nClusters, long seed)");
		}
		this.nClustersCalc = inners[0];
		if (this.nClustersCalc.getReturnType() != Integer.class || (!this.nClustersCalc.isConst() && !(this.nClustersCalc instanceof DerivedCellCalculatorRef))) {
			throw new ExpressionParserException(position, getMethodName() + "(...) param nClusters must be integer constant");
		}
		if (this.nClustersCalc.isConst()) {
			this.nClusters = (Integer) this.nClustersCalc.get(null);
		}
		this.seedCalc = inners[1];
		if (this.seedCalc.get(null) != null
				&& (this.seedCalc.getReturnType() != Long.class || (!this.seedCalc.isConst() && !(this.seedCalc instanceof DerivedCellCalculatorRef)))) {
			throw new ExpressionParserException(position, getMethodName() + "(...) param seed must be long constant");
		}
		this.seed = this.seedCalc.isConst() ? (Long) this.seedCalc.get(null) : null;
		int nParams = inners.length;
		int nInputs = nParams - 2;
		this.inputs = new DerivedCellCalculator[nInputs];
		for (int i = 0; i < nInputs; i++) {
			inputs[i] = inners[i + 2];
		}
	}
	@Override
	protected void visit(ReusableCalcFrameStack sf, PrepRows values) {
		if (this.nClustersCalc instanceof DerivedCellCalculatorRef) {
			setNClusters((Integer) this.nClustersCalc.get(sf));
		}
	}
	protected boolean isNull() {
		return isNull;
	}
	protected void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	protected int getNClusters() {
		return nClusters;
	}
	protected void setNClusters(int nClusters) {
		this.nClusters = nClusters;
	}

	protected int[] getClusters() {
		return clusters;
	}

	protected void setClusters(int[] clusters) {
		this.clusters = clusters;
	}

	protected DerivedCellCalculator[] getInputs() {
		return this.inputs;
	}
	protected boolean sampleContainsNaN(double[] sample) {
		for (int d = 0; d < sample.length; d++) {
			if (Double.isNaN(sample[d])) {
				return true;
			}
		}
		return false;
	}

	protected double[][] getData(ReusableCalcFrameStack sf, PrepRows values) {
		// Load data into array
		int N = values.size();
		int D = this.inputs.length;
		double[][] data = new double[N][D];
		CalcFrame row;
		Double element;
		sampleLoop: for (int n = 0; n < N; n++) {
			sf.reset(values.get(n));
			// Get nth sample
			for (int d = 0; d < D; d++) { // Get sample
				element = (Double) inputs[d].get(sf);
				if (element == null) { // Skip if any elements in sample are null
					for (int dd = 0; dd < D; dd++) {
						data[n][dd] = Double.NaN;
					}
					continue sampleLoop;
				} else {
					data[n][d] = element;
				}
			}
		}
		return data;
	}
	public Long getSeed() {
		return seed;
	}
}
