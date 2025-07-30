package com.f1.utils.sql.preps;

import java.util.Random;

import com.f1.utils.LAH;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class EmGmmClusteringCalculator extends AbstractClusteringCalculator {

	public final static String METHOD_NAME = "emGmmCluster";
	Random rng = new Random();
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Integer.class, "Integer nClusters,Long seed,Object ... values");
		paramsDefinition.addDesc("Same functionality as kmeansCluster(), but instead uses the expectation-maximization algorithm to train a Gaussian mixture model.");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addParamDesc(1, "");
		paramsDefinition.addParamDesc(2, "");
		//TODO:find out why emGmmCluster is returning nulls
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new EmGmmClusteringCalculator(position, calcs);
		}
	};

	public EmGmmClusteringCalculator(int position, DerivedCellCalculator[] inners) {
		super(position, inners);
	}

	@Override
	public Object get(CalcFrameStack lcvs, int pos) {
		int value = getClusters()[pos];
		return isNull() || value == -1 ? null : value;
	}

	@Override
	public void visit(ReusableCalcFrameStack sf, PrepRows values) {
		if (values.isEmpty()) {
			setNull(true);
		} else {
			super.visit(sf, values);
			setNull(false);

			int D = getInputs().length; // Dimensionality of data
			int K = getNClusters(); // Number of clusters
			int N = values.size(); // Number of samples
			int[] clusters = new int[N];
			for (int n = 0; n < N; n++) { // Initialize all points to "no cluster" (-1)
				clusters[n] = -1;
			}
			int max_iterations = 100;

			if (getSeed() != null) {
				rng.setSeed(getSeed());
			}

			// Load data into array
			double[][] data = getData(sf, values);

			// Initialize parameters
			double[] mixingCoeffs = new double[K];
			double[][] means = new double[K][D];
			double[][][] covariances = new double[K][D][D];
			double c;
			double coeffSum = 0;
			for (int k = 0; k < K; k++) {
				c = rng.nextDouble();
				mixingCoeffs[k] = c;
				coeffSum += c;
				for (int d = 0; d < D; d++) {
					means[k][d] = rng.nextDouble();
					for (int d2 = 0; d2 < D; d2++) {
						covariances[k][d][d2] = d == d2 ? 1 + rng.nextDouble() : 0;
					}
				}
			}
			// Normalize mixing coefficients
			for (int k = 0; k < K; k++) {
				mixingCoeffs[k] /= coeffSum;
			}

			// INITIAL TEST PARAMETERS
			//			means[0][0] = 0.29;
			//			means[0][1] = 0.74;
			//			means[1][0] = 0.21;
			//			means[1][1] = 0.02;
			//			means[2][0] = 0.68;
			//			means[2][1] = 0.31;
			//			covariances[0][0][0] = 0.58;
			//			covariances[0][0][1] = 0.05;
			//			covariances[0][1][0] = 0.23;
			//			covariances[0][1][1] = 0.53;
			//
			//			covariances[1][0][0] = 1.0;
			//			covariances[1][0][1] = 0.1;
			//			covariances[1][1][0] = 0.38;
			//			covariances[1][1][1] = 0.79;
			//
			//			covariances[2][0][0] = 1.0;
			//			covariances[2][0][1] = 0.01;
			//			covariances[2][1][0] = 0.22;
			//			covariances[2][1][1] = 0.7;
			//			mixingCoeffs[0] = 0.24;
			//			mixingCoeffs[1] = 0.43;
			//			mixingCoeffs[2] = 0.33;

			int iteration = 0;
			double[][] responsibilities = new double[N][K];
			double respNum;
			double normFactor;
			double[] Nk = new double[K]; // Approximate number of points per cluster
			while (iteration < max_iterations) {

				// E step
				double[] sample = new double[D];
				sampleLoop: for (int n = 0; n < N; n++) {
					// Get nth sample
					sample = data[n];
					normFactor = 0;
					if (sampleContainsNaN(sample)) {
						continue sampleLoop;
					}
					for (int k = 0; k < K; k++) {
						try {
							respNum = mixingCoeffs[k] * MH.gaussianPdf(sample, means[k], covariances[k]);
						} catch (IllegalArgumentException e) { // Singularity
							//							throw new ExpressionParserException(this.position,
							//									"EM algorithm found singularity in mixture model. Gaussian mixture model may be ill-suited for this dataset.");
							for (int i = 0; i < D; i++) { // Reset mean to random value, covariances to something large
								means[k][i] = rng.nextDouble();
								for (int j = 0; j < D; j++) {
									covariances[k][i][j] = 1000 + (1.0 + rng.nextDouble());
								}
							}
							respNum = mixingCoeffs[k] * MH.gaussianPdf(sample, means[k], covariances[k]);
						}
						responsibilities[n][k] = respNum;
						normFactor += respNum;
					}
					for (int k = 0; k < K; k++) {
						responsibilities[n][k] /= normFactor;
					}
				}

				// M step
				for (int k = 0; k < K; k++) {
					double sum = 0;
					for (int n = 0; n < N; n++) {
						sum += responsibilities[n][k];
					}
					Nk[k] = sum;
				}
				for (int k = 0; k < K; k++) {
					// Reset previous parameters
					for (int d = 0; d < D; d++) {
						means[k][d] = 0;
						for (int d2 = 0; d2 < D; d2++) {
							covariances[k][d][d2] = 0;
						}
					}
					// MEAN LOOP
					sampleLoop: for (int n = 0; n < N; n++) {
						// Get nth sample
						sample = data[n];
						if (sampleContainsNaN(sample)) {
							continue sampleLoop;
						}

						double r = responsibilities[n][k];
						// Recalculate means & covariances
						for (int d = 0; d < D; d++) {
							means[k][d] += r * sample[d];
						}
					}
					for (int d = 0; d < D; d++) {
						means[k][d] /= Nk[k];
					}
					// COVARIANCE LOOP
					sampleLoop: for (int n = 0; n < N; n++) {
						// Get nth sample
						sample = data[n];
						if (sampleContainsNaN(sample)) {
							continue sampleLoop;
						}

						double r = responsibilities[n][k];
						// Recalculate means & covariances
						double[] diff = new double[D];
						diff = LAH.subtract(sample, means[k]);
						double[][] outerProd = LAH.outer(diff, diff);
						for (int d = 0; d < D; d++) {
							for (int d2 = 0; d2 < D; d2++) {
								covariances[k][d][d2] += r * outerProd[d][d2];
							}
						}

					}
					for (int d = 0; d < D; d++) {
						for (int d2 = 0; d2 < D; d2++) {
							covariances[k][d][d2] /= Nk[k];
						}
					}
					mixingCoeffs[k] = Nk[k] / N;
				}
				iteration++;
			}

			// Maximum likelihood step
			double max;
			for (int n = 0; n < N; n++) {
				max = 0;
				for (int k = 0; k < K; k++) {
					if (responsibilities[n][k] > max) {
						max = responsibilities[n][k];
						clusters[n] = k;
					}
				}
			}
			setClusters(clusters);
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
		return new EmGmmClusteringCalculator(getPosition(), copyInners());
	}

}
