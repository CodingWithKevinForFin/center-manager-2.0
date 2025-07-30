package com.f1.utils.sql.preps;

import java.util.Random;

import com.f1.utils.LAH;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class VariationalInferenceGmmClusteringCalculator extends AbstractClusteringCalculator {
	public final static String METHOD_NAME = "viGmmCluster";
	public final static ParamsDefinition paramsDefinition;
	Random rng = new Random();
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Integer.class, "Integer nClusters,Long seed,Object ... values");
		paramsDefinition.addDesc("Same functionality as kmeansCluster(), but instead uses the variational inference algorithm to train a Gaussian mixture model.");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addParamDesc(1, "");
		paramsDefinition.addParamDesc(2, "");
		//TODO:find out why viGmmCluster is throwing errors
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new VariationalInferenceGmmClusteringCalculator(position, calcs);
		}
	};

	public VariationalInferenceGmmClusteringCalculator(int position, DerivedCellCalculator[] inners) {
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

			DerivedCellCalculator[] inputs = getInputs();
			int D = inputs.length; // Dimensionality of data
			int K = getNClusters(); // Number of clusters
			int N = values.size(); // Number of samples
			int[] clusters = new int[N];
			for (int n = 0; n < N; n++) {
				clusters[n] = -1;
			}
			int max_iterations = 100;

			if (getSeed() != null) {
				rng.setSeed(getSeed());
			}

			// Load data into array
			double[][] data = getData(sf, values);

			// Initialize prior parameters
			double alpha0 = 10;
			double beta0 = 1;
			double[] m0 = new double[D];
			double nu0 = 3;
			double[][] W0 = new double[D][D];
			// Initialize W0 to all zeros
			for (int i = 0; i < D; i++) {
				m0[i] = 0;
				for (int j = 0; j < D; j++) {
					W0[i][j] = 0;
				}
			}
			// Calculate sample covariance matrix, scale by D / 10
			double[] sample = new double[D];
			double cov_ij;
			sampleLoop: for (int n = 0; n < N; n++) {
				sample = data[n];
				if (sampleContainsNaN(sample)) {
					continue sampleLoop;
				}
				for (int i = 0; i < D; i++) {
					for (int j = i; j < D; j++) {
						cov_ij = sample[i] * sample[j];
						W0[i][j] += cov_ij;
						if (j != i) {
							W0[j][i] += cov_ij;
						}
					}
				}
			}
			// Normalize W0
			for (int i = 0; i < D; i++) {
				for (int j = 0; j < D; j++) {
					W0[i][j] *= ((double) D) / (10.0 * N);
				}
			}
			double[][] W0inv = LAH.inverse(W0);
			double[][] responsibilities = new double[N][K];
			double sum, rand;
			for (int n = 0; n < N; n++) {
				sum = 0;
				for (int k = 0; k < K; k++) {
					rand = rng.nextDouble();
					responsibilities[n][k] = rand;
					sum += rand;
				}
				for (int k = 0; k < K; k++) {
					responsibilities[n][k] /= sum;
				}
			}
			//			boolean test = false;
			//			if (test) {
			//				// TEST VALUES
			//				responsibilities[0][0] = 0.36723938973425035;
			//				responsibilities[0][1] = 0.5142639443435452;
			//				responsibilities[0][2] = 0.11849666592220438;
			//				responsibilities[1][0] = 0.4555198184925083;
			//				responsibilities[1][1] = 0.40326834737881734;
			//				responsibilities[1][2] = 0.14121183412867439;
			//				responsibilities[2][0] = 0.32852818143965246;
			//				responsibilities[2][1] = 0.2646048267710092;
			//				responsibilities[2][2] = 0.40686699178933844;
			//				responsibilities[3][0] = 0.24432955761489036;
			//				responsibilities[3][1] = 0.20642977278611532;
			//				responsibilities[3][2] = 0.5492406695989943;
			//				responsibilities[4][0] = 0.5789875574506813;
			//				responsibilities[4][1] = 0.2745234509774395;
			//				responsibilities[4][2] = 0.14648899157187922;
			//				responsibilities[5][0] = 0.24732231576855354;
			//				responsibilities[5][1] = 0.4891957851828883;
			//				responsibilities[5][2] = 0.2634818990485581;
			//				responsibilities[6][0] = 0.39770430248504857;
			//				responsibilities[6][1] = 0.19857758147018148;
			//				responsibilities[6][2] = 0.4037181160447698;
			//				responsibilities[7][0] = 0.3746475556274551;
			//				responsibilities[7][1] = 0.5881904671308089;
			//				responsibilities[7][2] = 0.037161977241736074;
			//				responsibilities[8][0] = 0.4981344481392762;
			//				responsibilities[8][1] = 0.4341415493665046;
			//				responsibilities[8][2] = 0.06772400249421928;
			//				responsibilities[9][0] = 0.48530988587424384;
			//				responsibilities[9][1] = 0.14899120110618835;
			//				responsibilities[9][2] = 0.36569891301956775;
			//			}

			int iteration = 0;
			double[] Nk = new double[K]; // Approximate/effective number of points per cluster
			double[][] xbar = new double[K][D];
			double[][][] S = new double[K][D][D];
			double[] x;
			double[] diff;
			double[][] outerProd;
			double[] alpha = new double[K];
			double[] beta = new double[K];
			double[][] m = new double[K][D];
			double[][][] W = new double[K][D][D];
			double[] nu = new double[K];
			double[][] expectationMuLambda = new double[N][K];
			double[] expectationLambda = new double[K];
			double[] expectationPi = new double[K];
			double alphaHat;
			double k_terms;
			double respNum;
			double[] normFactor = new double[N];
			while (iteration < max_iterations) {

				// M step
				for (int k = 0; k < K; k++) {
					Nk[k] = 0;
					for (int n = 0; n < N; n++) {
						Nk[k] += responsibilities[n][k];
					}
				}
				for (int k = 0; k < K; k++) {
					for (int d = 0; d < D; d++) {
						xbar[k][d] = 0;
					}
					for (int n = 0; n < N; n++) {
						for (int d = 0; d < D; d++) {
							xbar[k][d] += responsibilities[n][k] * data[n][d];
						}
					}
					for (int d = 0; d < D; d++) {
						xbar[k][d] /= Nk[k];
					}
				}
				for (int k = 0; k < K; k++) {
					for (int i = 0; i < D; i++) {
						for (int j = 0; j < D; j++) {
							S[k][i][j] = 0;
						}
					}
					for (int n = 0; n < N; n++) {
						x = data[n];
						diff = LAH.subtract(x, xbar[k]);
						outerProd = LAH.outer(diff, diff);
						for (int i = 0; i < D; i++) {
							for (int j = 0; j < D; j++) {
								S[k][i][j] += responsibilities[n][k] * outerProd[i][j];
							}
						}
					}
					for (int i = 0; i < D; i++) {
						for (int j = 0; j < D; j++) {
							S[k][i][j] /= Nk[k];
						}
					}
				}
				for (int k = 0; k < K; k++) {
					double N_k = Nk[k];
					alpha[k] = alpha0 + N_k;
					beta[k] = beta0 + N_k;
					diff = LAH.subtract(xbar[k], m0);
					outerProd = LAH.outer(diff, diff);
					for (int i = 0; i < D; i++) {
						m[k][i] = (beta0 * m0[i] + N_k * xbar[k][i]) / beta[k];
						for (int j = 0; j < D; j++) {
							W[k][i][j] = W0inv[i][j] + N_k * S[k][i][j] + beta0 * N_k / (beta0 + N_k) * outerProd[i][j];
						}
					}
					W[k] = LAH.inverse(W[k]);
					nu[k] = nu0 + N_k + 1;
				}

				// E step
				alphaHat = 0;
				for (int k = 0; k < K; k++) {
					alphaHat += alpha[k];
				}
				for (int k = 0; k < K; k++) {
					expectationLambda[k] = 0;
					for (int i = 1; i <= D; i++) {
						expectationLambda[k] += MH.digamma((nu[k] + 1 - i) / 2.0);
					}
					expectationLambda[k] += D * Math.log(2) + Math.log(LAH.determinant(W[k]));
					expectationPi[k] = MH.digamma(alpha[k]) - MH.digamma(alphaHat);
					for (int n = 0; n < N; n++) {
						diff = LAH.subtract(data[n], m[k]);
						expectationMuLambda[n][k] = D / beta[k] + nu[k] * LAH.inner(LAH.multiply(diff, W[k]), diff);
					}
				}
				for (int n = 0; n < N; n++) {
					normFactor[n] = 0;
				}
				// Recalculate responsibilities
				for (int k = 0; k < K; k++) {
					k_terms = expectationPi[k] + 0.5 * expectationLambda[k] - 0.5 * D * Math.log(2 * Math.PI);
					for (int n = 0; n < N; n++) {
						respNum = Math.exp(k_terms - 0.5 * expectationMuLambda[n][k]);
						responsibilities[n][k] = respNum;
						normFactor[n] += respNum;
					}
				}
				// Normalize
				for (int n = 0; n < N; n++) {
					for (int k = 0; k < K; k++) {
						responsibilities[n][k] /= normFactor[n];
					}
				}

				iteration++;
			}

			// Maximum a-posteriori step
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
		return new VariationalInferenceGmmClusteringCalculator(getPosition(), copyInners());
	}

}
