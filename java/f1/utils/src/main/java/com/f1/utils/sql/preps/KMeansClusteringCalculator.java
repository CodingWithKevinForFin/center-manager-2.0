package com.f1.utils.sql.preps;

import java.util.Random;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class KMeansClusteringCalculator extends AbstractClusteringCalculator {

	public static final String METHOD_NAME = "kmeansCluster";
	public final static ParamsDefinition paramsDefinition;
	Random rng = new Random();
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Comparable.class, "Integer nClusters,Long seed,Object ... values");
		paramsDefinition.addDesc(
				"Clusters data using the k-means clustering algorithm. Takes one or more expressions, followed by int nClusters specifying the number of clusters and long seed specifying the random seed for initializing the model parameters. The seed argument may be set to null if no specific seed is desired. Returns a column of integers labeling each record according to its learned cluster. Input expressions must evaluate to numbers.");
		paramsDefinition.addParamDesc(0, "the number of clusters");
		paramsDefinition.addParamDesc(1, "the seed for initializing the model parameters, can be null");
		paramsDefinition.addParamDesc(2, "the array of all the numerical columns to evaluate");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(clientGroup String, clientId int, loanAmount double, applicantIncome double);\nINSERT INTO input VALUES (\"Client Group 1\", 0, 4583, 128),(\"Client Group 1\", 1, 3000, 66),(\"Client Group 1\", 2, 2583, 120),(\"Client Group 1\", 3, 6000, 141),(\"Client Group 1\", 4, 2333, 95);\nINSERT INTO input VALUES (\"Client Group 2\", 0, 2571, 28),(\"Client Group 2\", 1, 6000, 16),(\"Client Group 2\", 2, 1583, 1200),(\"Client Group 2\", 3, 4500, 1410),(\"Client Group 2\", 4, 1133, 5);\nCREATE TABLE result AS PREPARE *, kmeansCluster(4, 1000L, loanAmount, applicantIncome) as cluster FROM input PARTITION BY clientGroup;\nTable input = SELECT * FROM input;\nTable result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new KMeansClusteringCalculator(position, calcs);
		}
	};

	public KMeansClusteringCalculator(int position, DerivedCellCalculator[] inners) {
		super(position, inners);
	}
	@Override
	public Object get(CalcFrameStack lcvs, int pos) {
		int value = getClusters()[pos];
		return isNull() || value == -1 ? null : value;
	}
	@Override
	protected void visit(ReusableCalcFrameStack sf, PrepRows values) {
		if (values.isEmpty()) {
			setNull(true);
		} else {
			super.visit(sf, values);
			setNull(false);

			int D = getInputs().length; // Dimensionality of data
			int K = getNClusters(); // Number of clusters
			int N = values.size(); // Number of samples
			int max_iterations = 500;

			if (getSeed() != null) {
				rng.setSeed(getSeed());
			}

			// Load data into array
			double[][] data = getData(sf, values);

			// Mean vectors by cluster
			double[][] means = new double[D][K];

			// Randomly initialize means
			int randSample;
			Double component;
			clusterLoop: for (int k = 0; k < K; k++) {
				randSample = rng.nextInt(N);
				for (int d = 0; d < D; d++) {
					component = data[randSample][d];
					if (component == Double.NaN) { // If any component in sample is null, just set all components to random numbers. In other words, throw away whole point. 
						for (int dd = 0; dd < D; dd++) {
							means[dd][k] = rng.nextDouble();
						}
						break clusterLoop;
					} else {
						means[d][k] = component.doubleValue();
					}
				}
			}

			// test input
			//			means[0][0] = 1;
			//			means[0][1] = 3;
			//			means[0][2] = 6;
			//			means[0][3] = 8;
			//			means[0][4] = 2;
			//			means[1][0] = 2;
			//			means[1][1] = 3;
			//			means[1][2] = 4;
			//			means[1][3] = 0;
			//			means[1][4] = 8;

			int iteration = 0;
			double[][] meansPrevIter = new double[D][K];
			int[] nearestCluster = new int[N];
			for (int n = 0; n < N; n++) { // Initialize all points to "no cluster" (-1)
				nearestCluster[n] = -1;
			}
			double distSquared = 0;
			double distSquaredLeast;
			boolean converged = false;
			while (!converged && iteration < max_iterations) {
				int[] meanCnts = new int[K];
				// Save previous means to check for convergence
				// Set all means and counts to zero
				for (int k = 0; k < K; k++) {
					for (int d = 0; d < D; d++) {
						meansPrevIter[d][k] = means[d][k];
						means[d][k] = 0;
					}
					meanCnts[k] = 0;
				}

				double[] sample = new double[D];
				double diff;
				sampleLoop: for (int n = 0; n < N; n++) {
					distSquaredLeast = Double.MAX_VALUE;
					sample = data[n];
					if (sampleContainsNaN(sample)) {
						continue sampleLoop;
					}
					// Assignment step
					for (int k = 0; k < K; k++) {
						distSquared = 0;
						for (int d = 0; d < D; d++) { // Calculate squared distance
							diff = sample[d] - meansPrevIter[d][k];
							distSquared += diff * diff;
						}
						if (distSquared < distSquaredLeast) { // If distance to current cluster is less than previous least distance, then update
							distSquaredLeast = distSquared;
							nearestCluster[n] = k;
						}
					}
					// Update step
					int nearest = nearestCluster[n];
					// Incrementally update average for nearest cluster to include the current sample
					for (int d = 0; d < D; d++) {
						means[d][nearest] += sample[d];
					}
					meanCnts[nearest]++;
				}

				// Divide sums by counts in each cluster to get mean for each point
				for (int k = 0; k < K; k++) {
					for (int d = 0; d < D; d++) {
						means[d][k] /= meanCnts[k];
					}
				}

				// Check for convergence
				converged = true;
				outer: for (int k = 0; k < K; k++) {
					for (int d = 0; d < D; d++) {
						if (Math.abs(means[d][k] - meansPrevIter[d][k]) > 1e-3) {
							converged = false;
							break outer;
						}
					}
				}

				// Increment iteration count
				iteration += 1;
			}
			setClusters(nearestCluster);
		}
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new KMeansClusteringCalculator(getPosition(), copyInners());
	}

}
