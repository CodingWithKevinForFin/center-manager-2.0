package com.f1.utils;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class LAH {

	/**
	 * Linear Algebra Helper (from the makers of Math Helper!)
	 */

	private static final double eps0 = MH.eps0;

	public static void main(String[] args) {
		// Test linear algebra stuff
		//		double[] u = { 2.3, -6.8, 0.5 };
		//		double[] v = { 1.4, 5.8, -7.0 };
		//		double[][] A = { { 2.2, -4.6, 9.8 }, { -1.1, -4.2, 7.7 } };
		double[][] B = { { 3.3, 8.1, -2.0 }, { -0.6, 9.9, -3.4 }, { -7.9, 0.2, 1.4 } };
		double[][] D = { { 1.0, 2.0 }, { 3.0, 4.0 } };
		//		System.out.println("A = ");
		//		printMatrix(A);
		//		System.out.println("B = ");
		//		printMatrix(B);
		//		System.out.println("B2 = ");
		//		printMatrix(B2);
		//		System.out.println("a = ");
		//		System.out.println(Arrays.toString(a));
		//		System.out.println("b = ");
		//		System.out.println(Arrays.toString(b));
		//		System.out.println("b2 = ");
		//		System.out.println(Arrays.toString(b2));
		//		System.out.println("b3 = ");
		//		System.out.println(Arrays.toString(b3));
		//		double[][] C = blockDiag(A, B);
		//		System.out.println("C = ");
		//		printMatrix(C);
		assignSection(B, D, 1, 1);
		//		System.out.println("B = ");
		//		printMatrix(B);
		double[] z = { 99, 100 };
		assignRow(B, z, 1, 1);
		assignCol(B, z, 0, 0);
		// TEST
		//		System.out.println("TEST TEST TEST TEST TEST TEST");
		int m = 10;
		int n = 4;
		//		int l = m * n + 1;
		double[][] M = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				M[i][j] = i * n + j;
			}
		}
		//		System.out.println("M = ");
		//		printMatrix(M);
		//		double[][][] MUBV = svd(M);
		//		double[][] MU = MUBV[0];
		//		double[][] MB = MUBV[1];
		//		double[][] MV = MUBV[2];
		//		double[][] M2 = multiply(MU, multiply(MB, transpose(MV)));
		//		System.out.println("M2 = ");
		//		printMatrix(round(M2));
		//		System.out.println("same? " + allClose(M, M2, 1e-6));
		//		System.out.println("pseudoinverse: ");
		//		double[][] pinvM = pseudoinverse(M2);
		//		System.out.println("pinvM = ");
		//		printMatrix(pinvM);

		//		double[][][] svdE = svd(E);
		//		double[][] EU = svdE[0];
		//		double[][] ESigma = svdE[1];
		//		double[][] EV = svdE[2];
		//		double[][] E2 = multiply(EU, multiply(ESigma, transpose(EV)));
		//		System.out.println("E = ");
		//		printMatrix(E);
		//		System.out.println("E2 = ");
		//		printMatrix(E2);
		//		double[][][] svdF = svd(F);
		//		double[][] FU = svdF[0];
		//		double[][] FSigma = svdF[1];
		//		double[][] FV = svdF[2];
		//		double[][] F2 = multiply(FU, multiply(FSigma, transpose(FV)));
		//		System.out.println("F = ");
		//		printMatrix(F);
		//		System.out.println("F2 = ");
		//		printMatrix(F2);

		// Make some random matrices to test svd() function
		//		for (int iter = 0; iter < 10; iter++) {
		//			//			System.out.println("___________________");
		//			//			int nRows = (int) ((rng.nextDouble() * maxDim)) + 1;
		//			//			int nCols = (int) ((rng.nextDouble() * maxDim)) + 1;
		//			int nCols = maxDim++;
		//			int nRows = 3 * nCols;
		//			if (nCols > nRows) {
		//				int tmp = nCols;
		//				nCols = nRows;
		//				nRows = tmp;
		//			}
		//			//			System.out.println("nRows = " + nRows);
		//			//			System.out.println("nCols = " + nCols);
		//			R = new double[nRows][nCols];
		//			for (int i = 0; i < nRows; i++) {
		//				for (int j = 0; j < nCols; j++) {
		//					R[i][j] = rng.nextDouble() * 10;
		//				}
		//			}
		//			//			System.out.println("R = ");
		//			//			printMatrix(R);
		//			svdR = svd(R);
		//			U = svdR[0];
		//			Sigma = svdR[1];
		//			V = svdR[2];
		//			//			System.out.println("U * Sigma * V' = ");
		//			R2 = multiply(U, multiply(Sigma, transpose(V)));
		//			//			printMatrix(R2);
		//			double tol = 1e-6;
		//			//			System.out.println("== SAME ? == " + allClose(R, R2, tol));
		//			//			if (!allClose(R, R2, tol)) {
		//			//				printMatrix(isClose(R, R2, tol));
		//			//			}
		//			//			System.out.println("U'U = ");
		//			//			printMatrix(round(multiply(U, transpose(U))));
		//			//			System.out.println("Sigma = ");
		//			//			printMatrix(Sigma);
		//			//			System.out.println("V'V = ");
		//			//			printMatrix(round(multiply(V, transpose(V))));
		//			//			System.out.println("SAME? " + Arrays.deepEquals(R, R2));
		//		}
		//		printMatrix(pseudoinverse(AA));

		System.out.println("START FFT TESTS");
		double[][] y = new double[2][8];
		y[0][0] = 4.5;
		y[0][1] = 9.8;
		y[0][2] = 2.5;
		y[0][3] = 4.8;
		y[0][4] = 1.4;
		y[0][5] = 0.7;
		y[0][6] = 5.5;
		y[0][7] = 3.4;
		double[][] y_odd = new double[2][7];
		y_odd[0][0] = 4.5;
		y_odd[0][1] = 9.8;
		y_odd[0][2] = 2.5;
		y_odd[0][3] = 4.8;
		y_odd[0][4] = 1.4;
		y_odd[0][5] = 0.7;
		y_odd[0][6] = 5.5;
		double[] y_odd_real = new double[7];
		y_odd_real[0] = 4.5;
		y_odd_real[1] = 9.8;
		y_odd_real[2] = 2.5;
		y_odd_real[3] = 4.8;
		y_odd_real[4] = 1.4;
		y_odd_real[5] = 0.7;
		y_odd_real[6] = 5.5;
		double[] y_odd_imag = new double[7];
		double[] y_odd_real2 = new double[7];
		y_odd_real2[0] = 4.5;
		y_odd_real2[1] = 9.8;
		y_odd_real2[2] = 2.5;
		y_odd_real2[3] = 4.8;
		y_odd_real2[4] = 1.4;
		y_odd_real2[5] = 0.7;
		y_odd_real2[6] = 5.5;
		System.out.println("BEFORE");
		System.out.println("y_odd = ");
		printMatrix(y_odd);
		fft(y_odd);
		System.out.println("AFTER");
		System.out.println("y_odd = ");
		printMatrix(y_odd);
		System.out.println("BEFORE");
		System.out.println("y_odd_real = " + Arrays.toString(y_odd_real));
		System.out.println("y_odd_imag = " + Arrays.toString(y_odd_imag));
		System.out.println("************************");
		System.out.println("MINE");
		//		fft(new double[][] { y_odd_real, y_odd_imag });
		//		transform(y_odd_real, y_odd_imag, true);
		System.out.println("WEB");
		//		transform(y_odd_real2, y_odd_imag2, false);
		System.out.println("************************");
		System.out.println("AFTER");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
		double[] Y_odd_real = { 29.2000000, 7.7413200, 2.0779661, -8.6692861, -8.6692861, 2.0779661, 7.7413200 };
		double[] Y_odd_imag = { 0, -6.59195033, -0.75297225, -3.77315831, 3.77315831, 0.75297225, 6.59195033 };
		System.out.println("y_odd[REAL] = " + Arrays.toString(y_odd[REAL]));
		System.out.println("Y_odd_real = " + Arrays.toString(Y_odd_real));
		System.out.println("y_odd[IMAG] = " + Arrays.toString(y_odd[IMAG]));
		System.out.println("Y_odd_imag = " + Arrays.toString(Y_odd_imag));
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
		double tol = 1e-6;
		System.out.println("same? " + (allClose(y_odd[REAL], Y_odd_real, tol) && allClose(y_odd[IMAG], Y_odd_imag, tol)));
		double[][] z1 = new double[2][8];
		z1[0][0] = 0;
		z1[0][1] = 1;
		z1[0][2] = 2;
		z1[0][3] = 3;
		z1[0][4] = 4;
		z1[0][5] = 5;
		z1[0][6] = 6;
		z1[0][7] = 7;
		z1[1][0] = 8;
		z1[1][1] = 9;
		z1[1][2] = 10;
		z1[1][3] = 11;
		z1[1][4] = 12;
		z1[1][5] = 13;
		z1[1][6] = 14;
		z1[1][7] = 15;
		double[][] z2 = new double[2][8];
		z2[0][0] = 0;
		z2[0][1] = 1;
		z2[0][2] = 2;
		z2[0][3] = 3;
		z2[0][4] = 4;
		z2[0][5] = 5;
		z2[0][6] = 6;
		z2[0][7] = 7;
		z2[1][0] = 8;
		z2[1][1] = 9;
		z2[1][2] = 10;
		z2[1][3] = 11;
		z2[1][4] = 12;
		z2[1][5] = 13;
		z2[1][6] = 14;
		z2[1][7] = 15;
		double[][] f = new double[2][9];
		f[0][0] = 4.5;
		f[0][1] = 9.8;
		f[0][2] = 2.5;
		f[0][3] = 4.8;
		f[0][4] = 1.4;
		f[0][5] = 0.7;
		f[0][6] = 5.5;
		f[0][7] = 3.4;
		f[0][8] = 7.2;
		fft(f);
		double[] Ff_real = new double[] { 39.80000000, 11.42392528, -1.63347411, 2.30000000, -11.74045117, -11.74045117, 2.30000000, -1.63347411, 11.42392528 };
		double[] Ff_imag = new double[] { 0, -0.41811713, -2.40894848, -3.6373067, -0.17217801, 0.17217801, 3.6373067, 2.40894848, 0.41811713 };
		double[][] Ff = new double[][] { Ff_real, Ff_imag };
		System.out.println("same? " + allClose(f, Ff, 1e-6));
		//		System.out.println("z1 before: ");
		//		printMatrix(z1);
		//		fftRadix2(z1);
		//		System.out.println("z1 after: ");
		//		printMatrix(z1);

		//		double[][] Z2 = fftRadix2old(z2);
		//		System.out.println("Z2: ");
		//		printMatrix(Z2);
		//		System.out.println("same? " + Arrays.deepEquals(z1, Z2));
		//		double[][] Y = fft(y);
		//		System.out.println("%%%%%%%%%%%%%%%%%%%");
		//		printMatrix(Y);
		//		System.out.println("%%%%%%%%%%%%%%%%%%%");
		//		System.out.println(Arrays.toString(MH.complexMultiply(new double[] { 3, 2 }, new double[] { 1, 4 })));
		//		System.out.println("%%%%%%%%%%%%%%%%%%%");
		//		double[][] Y_odd = fft(y_odd);
		//		printMatrix(Y_odd);

		//		int mmm = 1000;
		//		int nnn = 1000;
		//		int ppp = 1000;
		//		double[][] XXX = generateRandomMatrix(mmm, nnn, 123L);
		//		double[][] YYY = generateRandomMatrix(nnn, ppp, 123L);
		//		double[][] AAA = { { 1, 2 }, { 6, 7 }, { 11, 12 } };
		//		double[][] BBB = { { 3, 4, 5 }, { 8, 9, 10 }, { 13, 14, 15 } };
		//		double[][] CCC = { { 16, 17 } };
		//		double[][] DDD = { { 18, 19, 20 } };
		//		double[][] MMM = new double[AAA.length + CCC.length][AAA[0].length + BBB[0].length];
		//		composeBlockMatrices(AAA, BBB, CCC, DDD, MMM);
		//		System.out.println("MMM = ");
		//		printMatrix(MMM);
		//		System.out.println("BBB * AAA = ");
		//		printMatrix(multiply(BBB, AAA));
		//		double[][] BBBAAA;//= new double[BBB.length][AAA[0].length];
		//		BBBAAA = strassenMultiply(BBB, AAA);
		//		System.out.println("BBB * AAA = ");
		//		printMatrix(BBBAAA);

		//		double[][] XXXYYY1 = multiply(XXX, YYY);
		//		long startStrassen = System.nanoTime();
		//		double[][] XXXYYY2 = strassenMultiply(XXX, YYY);
		//		long endStrassen = System.nanoTime();
		//		System.out.println("Strassen duration : " + (((double) endStrassen - startStrassen) / 1e9));
		//		System.out.println("((same)) ? " + allClose(XXXYYY1, XXXYYY2, 1e-9));
		//		System.out.println("XXXYYY1 = ");
		//		printMatrix(XXXYYY1);
		//		System.out.println("XXXYYY2 = ");
		//		printMatrix(XXXYYY2);
		//		printMatrix(isClose(XXXYYY1, XXXYYY2, 1e-9));
		//		printDimensions(XXXYYY1, "XXXYYY1");
		//		printDimensions(XXXYYY2, "XXXYYY2");

		double[] vv = { 1, 2, 3, 4, 5, 6 };
		printMatrix(outer(vv, vv));
		System.out.println(getElementOfSelfOuterProd(vv, 4, 2));
		System.out.println("original: " + Arrays.toString(getOrigVectorFromSelfOuterProd(outer(vv, vv))));

		double[][] A = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { 10, 11, 12 } };
		printMatrix(A, "A");
		double[][][] svdA = svdFull(A);
		double[][] UA = svdA[0];
		double[][] SigmaA = svdA[1];
		double[][] VA = svdA[2];
		double[][] Arecon = multiply(UA, multiply(SigmaA, transpose(VA)));
		printMatrix(round(Arecon));
		boolean match = allClose(A, round(Arecon));
		StringBuilder matchSB = new StringBuilder();
		matchSB.append("match? ");
		for (int i = 0; i < 30; i++) {
			matchSB.append(match + " ");
		}
		System.out.println(matchSB);
		printMatrix(UA, "UA");
		printMatrix(SigmaA, "SigmaA");
		printMatrix(VA, "VA");
		double[][][] Ahouse = householderBidiagonalizeExpanded(A);
		printMatrix(Ahouse[0], "U");
		printMatrix(Ahouse[1], "B");
		printMatrix(Ahouse[2], "V");
		double[][] Acomp = householderBidiagonalizeCompact(A)[0];
		printMatrix(Acomp, "Acomp");
		System.out.println(Arrays.toString(getHouseholderVectorU(Acomp, 0, 1)));
		System.out.println(Arrays.toString(getHouseholderVectorU(Acomp, 1, 1)));
		System.out.println(Arrays.toString(getHouseholderVectorU(Acomp, 2, 1)));
		double[][] EE = { { 0, 0, 3, 4 }, { 5, 0, 0, 8 }, { 9, 10, 0, 0 }, { 13, 14, 15, 0 }, { 17, 18, 19, 20 }, { 21, 22, 23, 24 }, { 25, 26, 27, 28 }, };
		printMatrix(EE, "EE");
		System.out.println(Arrays.toString(getHouseholderVectorV(EE, 0, 1)));
		System.out.println(Arrays.toString(getHouseholderVectorV(EE, 1, 1)));
		System.out.println(Arrays.toString(getHouseholderVectorV(EE, 2, 1)));

		double[] yy = { 1, 2, 3, 4 };
		linearRegression(yy, A);
		//		testHouseholderStuff(A);
		double[] aaa = { 1, 2, 3 };
		double[] bbb = copyVector(aaa);
		printVector(aaa, "aaa");
		printVector(bbb, "bbb");
		aaa[0] = 5;
		printVector(aaa, "aaa");
		printVector(bbb, "bbb");

		//		testHouseholderStuff(A);
		double[] ccc = { 11, 12, 13, 14, 15, 16 };
		assignSection(ccc, aaa, 3);
		printVector(aaa, "aaa");
		printVector(ccc, "ccc");

		double[][] X = { { 1, 2, 3 }, { 4, 5, 6 } };
		double[] t = { 7, 8 };
		System.out.println(">>>>>>>>>>>>>>>>>>>>>");
		linearRegression(t, X);

		double[] x_odd = { 1, 7, 3, 9, 0, 2, 1, 5, 6 };
		double[] h_odd = { 1, 2, 3, 4, 5 };
		double[] x_even = { 1, 7, 3, 9, 2, 1, 5, 6 };
		double[] h_even = { 1, 2, 3, 5 };
		double[] y_oo = convolveNaive(x_odd, h_odd, CONVOLUTION_MODE_FULL);
		double[] y_oe = convolveNaive(x_odd, h_even, CONVOLUTION_MODE_FULL);
		double[] y_eo = convolveNaive(x_even, h_odd, CONVOLUTION_MODE_FULL);
		double[] y_ee = convolveNaive(x_even, h_even, CONVOLUTION_MODE_FULL);
		System.out.println("full");
		printVector(y_oo, "y_oo");
		printVector(y_oe, "y_oe");
		printVector(y_eo, "y_eo");
		printVector(y_ee, "y_ee");
		y_oo = convolveNaive(x_odd, h_odd, CONVOLUTION_MODE_SAME);
		y_oe = convolveNaive(x_odd, h_even, CONVOLUTION_MODE_SAME);
		y_eo = convolveNaive(x_even, h_odd, CONVOLUTION_MODE_SAME);
		y_ee = convolveNaive(x_even, h_even, CONVOLUTION_MODE_SAME);
		System.out.println("same");
		printVector(y_oo, "y_oo");
		printVector(y_oe, "y_oe");
		printVector(y_eo, "y_eo");
		printVector(y_ee, "y_ee");

		double[] generateGaussianFilterTimeBased = generateGaussianFilterTimeBased(0.5, new double[] { -7, -4, 0, 1, 5, 14, 24, 25 });
		printVector(generateGaussianFilterTimeBased);
		double sum = 0;
		for (int i = 0; i < generateGaussianFilterTimeBased.length; i++) {
			sum += generateGaussianFilterTimeBased[i];
		}
		System.out.println("sum = " + sum);

		double[][] T = { { 4.5, 2.2, 8.9 }, { 0.7, 4.1, 6.7 }, { 0.0, 1.0, 2.3 } };
		double[] d = { 0.9, 3.3, 5.7 };
		double[] e = solveSquareLinearEquations(T, d);
		double[] e2 = multiply(inverse(T), d);
		printMatrix(T, "T");
		printVector(d, "d");
		printVector(e, "e");
		printVector(e2, "e2");
		System.out.println(allClose(e, e2, 1e-9));

		double[][] Q = generateRandomMatrix(5, 5, 123L);
		double[][] PQ = pivotize(Q);
		double[] pQ = pivotizeCompact(Q);
		printMatrix(PQ, "PQ");
		printVector(pQ, "pQ");

		double[][] Qpiv1 = multiply(PQ, Q);
		double[][] Qpiv2 = applyPermutationIndices(Q, pQ);
		printMatrix(Qpiv1, "Qpiv1");
		printMatrix(Qpiv2, "Qpiv2");
		System.out.println(allClose(Qpiv1, Qpiv2));

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
		double[] splinex2 = { 0, 1, 2, 3, 4, 5, 6 };
		double[] spliney2 = { 0, 1, 2, 3, 4, 5, 6 };
		interpSpline(splinex2, spliney2, 2, 1, true);
		//		double[] xyz = { 1, 2, 3, 4, 5 };
		//		System.out.println("^^^^^^^^^^^^^");
		//		printVector(xyz, "xyz");
		//		xyz[2] = xyz[1];
		//		printVector(xyz, "xyz");
		//		xyz[1] = 8;
		//		printVector(xyz, "xyz");

		double[] x1 = new double[9], y1 = new double[9];
		for (int i = 0; i < 9; i++) {
			x1[i] = i * Math.PI / 4;
			y1[i] = Math.sin(x1[i]);
		}
		printVector(x1, "x1");
		printVector(y1, "y1");
		double[][] spline1 = interpSplineNotAKnot(x1, y1);
		printMatrix(spline1, "spline1");

		System.out.println("-------------------------------");
		double[] resampleX = { 0, 1, 2, 3, 4, 5, 6 };
		double[] resampleY = { 1.661, 0.055, 4.48, 2.347, 1.165, 0.748, 1.704 };
		double[] resampleXq = { 0.928, 1.204, 2.421, 3.092, 4.851, 5.025, 6.934 };
		printVector(resample(resampleX, resampleY, resampleXq, "linear"), "resampled");

	}

	public static void printVector(double[] x) {
		printVector(x, null);
	}
	public static void printVector(double[] x, String name) {
		System.out.println((SH.is(name) ? name + " = " : "") + Arrays.toString(x));
	}
	public static void printVector(int[] x, String name) {
		System.out.println((SH.is(name) ? name + " = " : "") + Arrays.toString(x));
	}
	public static double[] copyVector(double[] x) {
		int n = x.length;
		double[] y = new double[n];
		for (int i = 0; i < n; i++) {
			y[i] = x[i];
		}
		return y;
	}
	public static double[][] copyMatrix(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] B = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				B[i][j] = A[i][j];
			}
		}
		return B;
	}

	public static double inner(double[] u, double[] v) {
		assertVectorsEqualLength(u, v);
		double output = 0;
		for (int i = 0; i < u.length; i++) {
			output += u[i] * v[i];
		}
		return output;
	}
	public static double[][] outer(double[] u, double[] v) {
		double output[][] = new double[u.length][v.length];
		for (int i = 0; i < u.length; i++) {
			for (int j = 0; j < v.length; j++) {
				output[i][j] = u[i] * v[j];
			}
		}
		return output;
	}
	public static double[] add(double[] u, double[] v) {
		assertVectorsEqualLength(u, v);
		double[] output = new double[u.length];
		for (int i = 0; i < u.length; i++) {
			output[i] = u[i] + v[i];
		}
		return output;
	}
	public static double[] subtract(double[] u, double[] v) {
		assertVectorsEqualLength(u, v);
		double[] output = new double[u.length];
		for (int i = 0; i < u.length; i++) {
			output[i] = u[i] - v[i];
		}
		return output;
	}
	public static double[][] multiply(double[][] A, double[][] B) {
		return strassenMultiply(A, B);
	}
	public static double[][] multiplyNaive(double[][] A, double[][] B) {
		if (A[0].length != B.length) {
			throw new IllegalArgumentException("Number of columns in first matrix must equal number of rows in second matrix.");
		}
		double[][] output = new double[A.length][B[0].length];
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < B[0].length; j++) {
				for (int k = 0; k < B.length; k++) {
					output[i][j] += A[i][k] * B[k][j];
				}
			}
		}
		return output;
	}
	public static double[] multiply(double[] u, double[][] A) {
		if (u.length != A.length) {
			throw new IllegalArgumentException("Number of columns in vector must equal number of rows in matrix.");
		}
		double[] output = new double[A[0].length];
		for (int i = 0; i < A[0].length; i++) {
			for (int j = 0; j < u.length; j++) {
				output[i] += u[j] * A[j][i];
			}
		}
		return output;
	}
	public static double[] multiply(double[][] A, double[] u) {
		if (A[0].length != u.length) {
			throw new IllegalArgumentException("Number of columns in matrix must equal number of rows in vector.");
		}
		double[] output = new double[A.length];
		for (int i = 0; i < A.length; i++) {
			output[i] = 0;
			for (int j = 0; j < u.length; j++) {
				output[i] += A[i][j] * u[j];
			}
		}
		return output;
	}
	public static double[] multiply(double c, double[] u) {
		int N = u.length;
		double[] output = new double[N];
		for (int i = 0; i < N; i++) {
			output[i] = c * u[i];
		}
		return output;
	}
	public static double[][] multiply(double c, double[][] A) {
		int M = A.length;
		int N = A[0].length;
		double[][] output = new double[M][N];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				output[i][j] = c * A[i][j];
			}
		}
		return output;
	}
	public static double[][] add(double[][] A, double[][] B) {
		if (A.length != B.length || A[0].length != B[0].length) {
			throw new IllegalArgumentException("Matrix dimensions must match.");
		}
		double[][] output = new double[A.length][A[0].length];
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[0].length; j++) {
				output[i][j] = A[i][j] + B[i][j];
			}
		}
		return output;
	}
	public static double[][] subtract(double[][] A, double[][] B) {
		if (A.length != B.length || A[0].length != B[0].length) {
			throw new IllegalArgumentException("Matrix dimensions must match.");
		}
		double[][] output = new double[A.length][A[0].length];
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[0].length; j++) {
				output[i][j] = A[i][j] - B[i][j];
			}
		}
		return output;
	}
	public static double[][] identity(int N) {
		double[][] identity = new double[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				identity[i][j] = i == j ? 1 : 0;
			}
		}
		return identity;
	}
	public static double[][] pivotize(double[][] A) {
		assertSquare(A);
		int N = A.length;
		double[][] identity = identity(N);
		for (int i = 0; i < N; i++) {
			double maxA = A[i][i];
			int row = i;
			for (int j = i; j < N; j++) {
				if (A[j][i] > maxA) {
					maxA = A[j][i];
					row = j;
				}
			}
			if (i != row) {
				double[] tmp = identity[i];
				identity[i] = identity[row];
				identity[row] = tmp;
			}
		}
		return identity;
	}
	public static double[] pivotizeCompact(double[][] A) {
		assertSquare(A);
		int N = A.length;
		double[] p = new double[N];
		for (int i = 0; i < N; i++) {
			p[i] = i;
		}
		for (int i = 0; i < N; i++) {
			double max = A[i][i];
			int row = i;
			for (int j = i; j < N; j++) {
				if (A[j][i] > max) {
					max = A[j][i];
					row = j;
				}
			}
			if (i != row) {
				int tmp = (int) p[i];
				p[i] = p[row];
				p[row] = tmp;
			}
		}
		return p;
	}

	public static double[][][] lu(double[][] A) {
		return lu(A, false);
	}
	public static double[][][] luCompact(double[][] A) {
		return lu(A, true);
	}
	public static double[][][] lu(double[][] A, boolean compact) {
		assertSquare(A);
		int N = A.length;
		double[][] L = new double[N][N];
		double[][] U = new double[N][N];
		double[][] P;
		//		P = compact ? null : pivotize(A);
		double[] p;
		//		p = compact ? pivotizeCompact(A) : null;
		double[][] A2;
		//		A2 = compact ? applyPermutationIndices(A, p) : multiply(P, A);
		if (compact) {
			P = null;
			p = pivotizeCompact(A);
			A2 = applyPermutationIndices(A, p);
		} else {
			P = pivotize(A);
			p = null;
			A2 = multiply(P, A);
		}

		for (int j = 0; j < N; j++) {
			L[j][j] = 1;
			for (int i = 0; i < j + 1; i++) {
				double s1 = 0;
				for (int k = 0; k < i; k++) {
					s1 += U[k][j] * L[i][k];
				}
				U[i][j] = A2[i][j] - s1;
			}
			for (int i = j; i < N; i++) {
				double s2 = 0;
				for (int k = 0; k < j; k++) {
					s2 += U[k][j] * L[i][k];
				}
				L[i][j] = (A2[i][j] - s2) / U[j][j];
			}
		}
		return new double[][][] { L, U, compact ? new double[][] { p } : P };
	}
	public static double[] applyPermutationIndices(double[] x, double[] p) {
		int N = x.length;
		double[] output = new double[N];
		for (int i = 0; i < N; i++) {
			output[i] = x[(int) p[i]];
		}
		return output;
	}
	public static double[][] applyPermutationIndices(double[][] A, double[] p) {
		assertSquare(A);
		int N = A.length;
		double[][] output = new double[N][N];
		for (int i = 0; i < N; i++) {
			output[i] = A[(int) p[i]];
		}
		return output;
	}

	public static double determinant(double[][] A) {
		assertSquare(A);
		int N = A.length;
		if (N == 1) {
			return A[0][0];
		}
		double[][][] LUP = lu(A);
		double[][] L = LUP[0];
		double[][] U = LUP[1];
		double[][] P = LUP[2];
		double det = 1;
		int swapCnt = 0;
		int[] permIndices = getPermutationIndices(P);
		for (int i = 0; i < N; i++) { // Rows
			det *= L[i][i] * U[i][i];
			while (permIndices[i] != i) {
				swapRows(P, i, permIndices[i]);
				swapElements(permIndices, i, permIndices[i]);
				swapCnt++;
			}
		}
		return swapCnt % 2 == 0 ? det : -det;
	}
	public static void swapRows(double[][] A, int idx1, int idx2) {
		double[] tmp = A[idx1];
		A[idx1] = A[idx2];
		A[idx2] = tmp;
	}
	public static void swapElements(double[] u, int idx1, int idx2) {
		double tmp = u[idx1];
		u[idx1] = u[idx2];
		u[idx2] = tmp;
	}
	public static void swapElements(int[] u, int idx1, int idx2) {
		int tmp = u[idx1];
		u[idx1] = u[idx2];
		u[idx2] = tmp;
	}
	public static int[] getPermutationIndices(double[][] P) {
		assertSquare(P);
		int N = P.length;
		int onesCnt = 0;
		int zerosCnt = 0;
		// Check to make sure it's a permutation matrix
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (P[i][j] == 0)
					zerosCnt++;
				if (P[i][j] == 1)
					onesCnt++;
			}
			// Row should have exactly one 1 and (N-1) zeros
			if (onesCnt != 1 || zerosCnt != N - 1) {
				throw new IllegalArgumentException("Input must be a permutation matrix.");
			}
			onesCnt = 0;
			zerosCnt = 0;
		}
		int[] indices = new int[N];
		int rowIdx = 0;
		for (int i = 0; i < N; i++) {
			columns: for (int j = 0; j < N; j++) {
				if (P[i][j] == 1) {
					rowIdx = j;
					break columns;
				}
			}
			indices[i] = rowIdx;
		}
		return indices;
	}
	public static double[][] inverse(double[][] A) {
		assertSquare(A);
		double detA = determinant(A);
		if (detA == 0 || Double.isNaN(detA)) {
			throw new IllegalArgumentException("Input matrix must be invertible.");
		}
		int N = A.length;
		double[][][] LUP = lu(A);
		double[][] L = LUP[0];
		double[][] U = LUP[1];
		double[][] P = LUP[2];
		double[][] Ainv = new double[N][N];
		double[] x = new double[N];
		double[] p = new double[N];
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < N; i++) { // Take jth column of P
				p[i] = P[i][j];
			}
			x = solveUpperTriangular(U, solveLowerTriangular(L, p));
			for (int i = 0; i < N; i++) { // Set the jth column of Ainv to be x
				Ainv[i][j] = x[i];
			}
		}
		return Ainv;
	}
	public static double[] solveSquareLinearEquations(double[][] A, double[] b) {
		// Solve A * x = b
		assertSquare(A);
		double[][][] LUp = luCompact(A);
		double[][] L = LUp[0];
		double[][] U = LUp[1];
		double[] p = LUp[2][0];
		return solveUpperTriangular(U, solveLowerTriangular(L, applyPermutationIndices(b, p)));
	}
	public static double[] solveLowerTriangular(double[][] L, double[] b) {
		assertSquare(L);
		assertLowerTriangular(L);
		int N = L.length;
		if (N != b.length) {
			throw new IllegalArgumentException("Matrix and vector must have same number of rows.");
		}

		// Solve using forward substitution
		double[] x = new double[b.length];
		double sum;
		for (int i = 0; i < N; i++) {
			sum = 0;
			for (int j = 0; j < i; j++) {
				sum += L[i][j] * x[j];
			}
			x[i] = (b[i] - sum) / L[i][i];
		}
		return x;
	}
	public static double[] solveUpperTriangular(double[][] U, double[] b) {
		assertSquare(U);
		assertUpperTriangular(U);
		int N = U.length;
		if (N != b.length) {
			throw new IllegalArgumentException("Matrix and vector must have same number of rows.");
		}

		// Solve using backward substitution
		double[] x = new double[b.length];
		double sum;
		for (int i = N - 1; i >= 0; i--) {
			sum = 0;
			for (int j = i + 1; j < N; j++) {
				sum += U[i][j] * x[j];
			}
			x[i] = (b[i] - sum) / U[i][i];
		}
		return x;
	}
	public static boolean isSquare(double[][] A) {
		return A.length == A[0].length;
	}
	private static void assertSquare(double[][] A) {
		if (!isSquare(A)) {
			throw new IllegalArgumentException("Input matrix must be square.");
		}
	}
	public static boolean isLowerTriangular(double[][] A) {
		assertSquare(A);
		int firstCol = 1;
		for (int i = 0; i < A.length; i++) {
			for (int j = firstCol; j < A.length; j++) {
				if (A[i][j] != 0) {
					return false;
				}
			}
			firstCol++;
		}
		return true;
	}
	public static boolean isUpperTriangular(double[][] A) {
		assertSquare(A);
		int N = A.length;
		int lastCol = 1;
		for (int i = 1; i < N; i++) {
			for (int j = 0; j < lastCol; j++) {
				if (A[i][j] != 0) {
					return false;
				}
			}
			lastCol++;
		}
		return true;
	}
	private static void assertLowerTriangular(double[][] A) {
		if (!isLowerTriangular(A)) {
			throw new IllegalArgumentException("Input matrix must be lower triangular.");
		}
	}
	private static void assertUpperTriangular(double[][] A) {
		if (!isUpperTriangular(A)) {
			throw new IllegalArgumentException("Input matrix must be upper triangular.");
		}
	}
	private static void assertVectorsEqualLength(double[] u, double[] v) {
		if (u.length != v.length) {
			throw new IllegalArgumentException("Lengths of input vectors must be equal.");
		}
	}
	private static void assertMoreRowsThanCols(double[][] A) {
		if (A.length < A[0].length) {
			throw new IllegalArgumentException("Number of rows in input matrix must be greater than or equal to the number of columns.");
		}
	}
	public static void printMatrix(double[][] A, String name) {
		System.out.println(name + " = ");
		printMatrix(A);
	}
	public static void printMatrix(double[][] A) {
		System.out.print("[");
		int N = A.length;
		for (int i = 0; i < N; i++) {
			System.out.println(Arrays.toString(A[i]) + (i == N - 1 ? "]" : ""));
		}
	}
	public static void printMatrix(boolean[][] A) {
		System.out.print("[");
		int N = A.length;
		for (int i = 0; i < N; i++) {
			System.out.println(Arrays.toString(A[i]) + (i == N - 1 ? "]" : ""));
		}
	}
	public static double[][][] qr(double[][] A) {
		assertMoreRowsThanCols(A);
		int m = A.length;
		int n = A[0].length;
		double[][] Q = identity(m);
		double[][] R = new double[m][n];
		// Copy A into R
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				R[i][j] = A[i][j];
			}
		}
		double normx;
		double s, u1;
		double[] w;
		double tau;
		double[][] Rsec, Qsec;
		for (int j = 0; j < n; j++) {

			// Find Householder matrix, H = I - tau * w * w' to put zeros below R[j][j]
			normx = 0;
			for (int i = j; i < m; i++) { // Rows
				normx += R[i][j] * R[i][j];
			}
			normx = Math.sqrt(normx);
			s = R[j][j] >= 0 ? -1 : 1;
			u1 = R[j][j] - s * normx;
			w = new double[m - j];
			for (int i = j; i < m; i++) {
				w[i - j] = R[i][j] / u1;
			}
			w[0] = 1;
			tau = -s * u1 / normx;

			// R = H * R, Q = Q * H
			Rsec = new double[m - j][n];
			for (int i = j; i < m; i++) {
				for (int col = 0; col < n; col++) {
					Rsec[i - j][col] = R[i][col];
				}
			}
			Rsec = multiply(tau, multiply(outer(w, w), Rsec));
			for (int i = j; i < m; i++) {
				for (int col = 0; col < n; col++) {
					R[i][col] -= Rsec[i - j][col];
				}
			}
			Qsec = new double[m][m - j];
			for (int i = j; i < m; i++) {
				for (int row = 0; row < m; row++) {
					Qsec[row][i - j] = Q[row][i];
				}
			}
			Qsec = multiply(tau, multiply(Qsec, outer(w, w)));
			for (int i = j; i < m; i++) {
				for (int row = 0; row < m; row++) {
					Q[row][i] -= Qsec[row][i - j];
				}
			}
		}
		return new double[][][] { Q, R };
	}
	public static double[][][] svdCompact(double[][] A) {
		return null;
	}
	private static double[][] constructHouseholderFactorU(double[][] Acomp, int idx, double[] betas) {
		return constructHouseholderFactor(Acomp, idx, betas, true);
	}
	private static double[][] constructHouseholderFactorV(double[][] Acomp, int idx, double[] betas) {
		return constructHouseholderFactor(Acomp, idx, betas, false);
	}
	private static double[][] constructHouseholderFactor(double[][] Acomp, int idx, double[] betas, boolean factorU) {
		int p;
		if (factorU) { // U
			p = Acomp.length;
			//			q = Acomp[0].length;
			//			shift = 0;
		} else { // V
			//			q = Acomp.length;
			p = Acomp[0].length;
			//			shift = 1;
		}
		//		int r = Math.min(p, q);
		double[][] Q = identity(p);
		if (idx < 0 || betas.length == 0) {
			return Q;
		}
		double[] v = factorU ? getHouseholderVectorU(Acomp, idx, betas[idx]) : getHouseholderVectorV(Acomp, idx, betas[idx]);
		int j = idx;
		assignSection(Q, subtract(identity(p - j - (factorU ? 0 : 1)), outer(v, v)), j, j);
		return Q;
	}
	@Deprecated
	public static double[][][] svdFull(double[][] A) {
		double[][][] svdCompact = svdFast(A);
		double[][] Ur = svdCompact[0];
		double[][] B = svdCompact[1];
		double[][] Vr = svdCompact[2];
		double[][] Acomp = svdCompact[3];
		double[] betasU = svdCompact[4][0];
		double[] betasV = svdCompact[5][0];
		double[][] Urest = extractBidiagonalizeU(Acomp, betasU, 1);
		double[][] Vrest = extractBidiagonalizeV(Acomp, betasV, 1);
		double[][] U = multiply(Urest, Ur);
		double[][] V = multiply(Vrest, Vr);
		return new double[][][] { U, B, V };
	}
	public static double[][][] svdFast(double[][] A) {
		assertMoreRowsThanCols(A);
		// Algorithm 1a: Householder reduction to bidiagonal form
		int m = A.length;
		int n = A[0].length;
		double[][][] hh = householderBidiagonalizeCompact(A);
		double[][] Acomp = hh[0];
		double[] betasU = hh[1][0];
		double[] betasV = hh[2][0];
		double[][] Ur = constructHouseholderFactorU(Acomp, betasU.length - 1, betasU);
		double[][] Vr = constructHouseholderFactorV(Acomp, betasV.length - 1, betasV);
		double[][] B = extractUpperBidiagonal(Acomp);

		// Algorithm 1b: Golub-Reinsch SVD
		double eps = eps0;
		int max_iterations = 1000;
		int iteration = 0;
		int[] pq;
		int p, q;
		q = 0;
		double[][] Sigma = new double[m][n];
		boolean hasDiagonalZeros = false;
		outer: while (iteration < max_iterations) {
			for (int i = 0; i < n - 1; i++) {
				if (Math.abs(B[i][i + 1]) <= eps * (Math.abs(B[i][i]) + Math.abs(B[i + 1][i + 1]))) {
					B[i][i + 1] = 0;
				}
			}
			pq = getGolubReinschPQ(B);
			p = pq[0];
			q = pq[1];
			if (q == n) {
				for (int i = 0; i < m; i++) { // Copy diagonal portion of B into Sigma
					for (int j = 0; j < n; j++) {
						Sigma[i][j] = i == j ? B[i][j] : 0;
					}
				}
				break outer;
			}

			// Check to see if B22 has any diagonal zeros
			hasDiagonalZeros = false;
			for (int i = p; i < n - q - 1; i++) {
				if (B[i][i] == 0) {
					hasDiagonalZeros = true;
					givensRotateLeft(B, i, i + 1, i + 1);
				}
			}
			if (!hasDiagonalZeros) {
				golubKahanSvdStep(B, Ur, Vr, p, q);
			}
			iteration++;
		}
		return new double[][][] { Ur, Sigma, Vr, Acomp, { betasU }, { betasV } };
	}
	@Deprecated
	public static double[][][] svdFull2(double[][] A) {
		assertMoreRowsThanCols(A);
		// Algorithm 1a: Householder reduction to bidiagonal form
		int m = A.length;
		int n = A[0].length;
		double[][][] UBV = householderBidiagonalizeExpanded(A);
		double[][] U = UBV[0];
		double[][] B = UBV[1];
		double[][] V = UBV[2];

		// Algorithm 1b: Golub-Reinsch SVD
		double eps = eps0;
		int max_iterations = 1000;
		int iteration = 0;
		int[] pq;
		int p, q;
		q = 0;
		double[][] Sigma = new double[m][n];
		boolean hasDiagonalZeros = false;
		outer: while (iteration < max_iterations) {
			for (int i = 0; i < n - 1; i++) {
				if (Math.abs(B[i][i + 1]) <= eps * (Math.abs(B[i][i]) + Math.abs(B[i + 1][i + 1]))) {
					B[i][i + 1] = 0;
				}
			}
			pq = getGolubReinschPQ(B);
			p = pq[0];
			q = pq[1];
			if (q == n) {
				for (int i = 0; i < m; i++) { // Copy diagonal portion of B into Sigma
					for (int j = 0; j < n; j++) {
						Sigma[i][j] = i == j ? B[i][j] : 0;
					}
				}
				break outer;
			}

			// Check to see if B22 has any diagonal zeros
			hasDiagonalZeros = false;
			for (int i = p; i < n - q - 1; i++) { // double-check this inequality...
				if (B[i][i] == 0) {
					hasDiagonalZeros = true;
					givensRotateLeft(B, i, i + 1, i + 1);
				}
			}
			if (!hasDiagonalZeros) {
				golubKahanSvdStep(B, U, V, p, q);
			}
			iteration++;
		}
		return new double[][][] { U, Sigma, V };
	}
	public static void golubKahanSvdStep(double[][] B, double[][] Q, double[][] P, int p, int q) {
		int n = B[0].length;
		double c12 = 0, c22 = 0, c11 = 0;
		int d = n - q;
		for (int k = p; k < d; k++) {
			c22 += B[k][d - 1] * B[k][d - 1];
			c12 += B[k][d - 1] * B[k][d - 2];
			c11 += B[k][d - 2] * B[k][d - 2];
		}
		double diagSum = c11 + c22;
		double sqrt = Math.sqrt(diagSum * diagSum - 4 * (c11 * c22 - c12 * c12));
		double eig1 = (diagSum + sqrt) / 2.0;
		double eig2 = (diagSum - sqrt) / 2.0;
		double mu = Math.abs(eig1 - c22) < Math.abs(eig2 - c22) ? eig1 : eig2;
		double alpha = B[p][p] * B[p][p] - mu;
		double beta = B[p][p] * B[p][p + 1];
		for (int k = p; k < d - 1; k++) {
			givensRotateRight(B, k + 1, k, alpha, beta);
			givensRotateRight(P, k + 1, k, alpha, beta);
			alpha = B[k][k];
			beta = B[k + 1][k];
			givensRotateLeft(B, k + 1, k, alpha, beta);
			givensRotateRight(Q, k + 1, k, alpha, beta);
			if (k < d - 2) {
				alpha = B[k][k + 1];
				beta = B[k][k + 2];
			}
		}
	}
	private static int[] getGolubReinschPQ(double[][] B) {
		int n = B[0].length;
		int p = 0, q = 0;
		boolean foundQ = false;
		double above;
		outer: for (int i = n - 1; i > 0; i--) { // Find q
			above = B[i - 1][i];
			if (!foundQ && above != 0) {
				q = n - i - 1;
				foundQ = true;
			}
			if (foundQ && above == 0) { // Find p
				p = i;
				break outer;
			}
		}
		if (!foundQ) {
			q = n;
		}
		return new int[] { p, q };
	}
	private static double[][][] golubReinschBlocking(double[][] B) {
		int n = B[0].length;
		int[] pq = getGolubReinschPQ(B);
		int p = pq[0];
		int q = pq[1];
		int dim = n - p - q;
		double[][] B22 = new double[dim][dim];
		for (int i = 0; i < dim; i++) { // Copy values from B into B22
			for (int j = 0; j < dim; j++) {
				B22[i][j] = B[p + i][p + j];
			}
		}
		return new double[][][] { B22, { { p } }, { { q } } };
	}
	public static double[][] transpose(double[][] A) {
		int M = A.length;
		int N = A[0].length;
		double[][] output = new double[N][M];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				output[j][i] = A[i][j];
			}
		}
		return output;
	}
	private static double[] givensRotationParams(double a, double b) {
		double c, s, r;
		if (b == 0) {
			c = 1;
			s = 0;
			r = a;
		} else {
			r = Math.hypot(a, b);
			c = a / r;
			s = -b / r;
		}

		//		// Ensure that r is positive
		//		if (b == 0){
		//			c = a >= 0 ? 1 : -1;
		//			s = 0;
		//			r = Math.abs(a);
		//		}
		//		else if (a == 0){
		//			c = 0;
		//			s = b >= 0 ? 1 : -1;
		//			r = Math.abs(b);
		//		}else if (Math.abs(a)>Math.abs(b)){
		//			
		//		}
		return new double[] { c, s, r };
	}
	private static void givensRotateLeft(double[][] A, int zeroedRow, int nonzeroedRow, int col) {
		// [ c -s ][a] = [r]
		// [ s  c ][b]   [0]
		givensRotateLeft(A, zeroedRow, nonzeroedRow, A[nonzeroedRow][col], A[zeroedRow][col]);
	}
	private static void givensRotateRight(double[][] A, int zeroedCol, int nonzeroedCol, int row) {
		// [a b][  c  s ] = [r 0]
		//      [ -s  c ]   
		givensRotateRight(A, zeroedCol, nonzeroedCol, A[row][nonzeroedCol], A[row][zeroedCol]);
	}
	private static void givensRotateLeft(double[][] A, int zeroedRow, int nonzeroedRow, double a, double b) {
		// A = G * A
		double[] csr = givensRotationParams(a, b);
		double c = csr[0];
		double s = csr[1];
		int n = A[0].length;
		double[] origNonzeroedRow = new double[n];
		for (int j = 0; j < n; j++) {
			origNonzeroedRow[j] = A[nonzeroedRow][j];
		}
		for (int j = 0; j < n; j++) {
			A[nonzeroedRow][j] = c * origNonzeroedRow[j] - s * A[zeroedRow][j];
			A[zeroedRow][j] = s * origNonzeroedRow[j] + c * A[zeroedRow][j];
		}
	}
	private static void givensRotateRight(double[][] A, int zeroedCol, int nonzeroedCol, double a, double b) {
		// A = A * G
		double[] csr = givensRotationParams(a, b);
		double c = csr[0];
		double s = csr[1];
		int m = A.length;
		double[] origNonzeroedCol = new double[m];
		for (int i = 0; i < m; i++) {
			origNonzeroedCol[i] = A[i][nonzeroedCol];
		}
		for (int i = 0; i < m; i++) {
			A[i][nonzeroedCol] = c * origNonzeroedCol[i] - s * A[i][zeroedCol];
			A[i][zeroedCol] = s * origNonzeroedCol[i] + c * A[i][zeroedCol];
		}
	}
	private static double[] getHouseholderVectorU(double[][] Acomp, int idx, double beta) {
		int m = Acomp.length;
		int uLen = m - idx;
		double[] u = new double[uLen];
		double sqrtBeta = Math.sqrt(beta);
		u[0] = sqrtBeta;
		for (int i = 1; i < uLen; i++) {
			u[i] = sqrtBeta * Acomp[i + idx][idx];
		}
		return u;
	}
	private static double[] getHouseholderVectorV(double[][] Acomp, int idx, double beta) {
		int n = Acomp[0].length;
		int vLen = n - idx - 1;
		double[] v = new double[vLen];
		double sqrtBeta = Math.sqrt(beta);
		v[0] = sqrtBeta;
		for (int i = 1; i < vLen; i++) {
			v[i] = sqrtBeta * Acomp[idx][i + idx + 1];
		}
		return v;
	}
	private static double getElementOfSelfOuterProd(double[] v, int row, int col) {
		return v[row] * v[col];
	}
	private static double[] getOrigVectorFromSelfOuterProd(double[][] A) {
		assertSymmetric(A);
		int m = A.length;
		double[] v = new double[m];
		for (int i = 0; i < m; i++) {
			v[i] = Math.sqrt(A[i][i]);
		}
		return v;
	}
	private static double[][] round(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] output = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				output[i][j] = Math.round(A[i][j]);
			}
		}
		return output;
	}
	private static double[][] roundSmall(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] output = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				output[i][j] = Math.abs(A[i][j]) < 1e-10 ? Math.round(A[i][j]) : A[i][j];
			}
		}
		return output;
	}
	public static double[] linearRegression(double[] y, double[][] X_) {
		// y - Target (m x 1)
		// X - Data (m x n)
		//
		// V - (n x n)
		// X - (m x m)
		//
		// w - (n x 1)
		//
		// Tall X:
		// w = X+ * y = V * Sigma+ * U' * y
		// (n x 1) = (n x m)(m x 1) = (n x n)(n x m)(m x m)(m x 1)
		// a = U' * y = (Ur' * ... * U2 * U1) * y
		// b = Sigma+ * a = Sigma+ * U' * y
		// w = V * b = (V1 * V2 * ... * Vr) * b = V * Sigma+ * U' * y
		// 
		// Fat X:
		// X <- X'
		// w' = y' * X+ = y' * V * Sigma+ * U'
		// (1 x n) = (1 x m)(m x n) = (1 x m)(m x m)(m x n)(n x n)
		// a' = y' * V = y' * (V1 * V2 * ... * Vr)
		// b' = a' * Sigma+ = y' * V * Sigma+
		// w' = b' * U' = b' * (Ur' * ... * U2 * U1) = y' * V * Sigma+ * U'

		int m = y.length;
		int n = X_[0].length;
		boolean fatX = m < n;
		double[][] X = fatX ? transpose(X_) : X_;
		if (m != X_.length) {
			throw new IllegalArgumentException("Number of target values must match number of training data points.");
		}

		double[][][] pinvComp = pseudoinverseCompact(X);
		double[][] Ur = pinvComp[0];
		double[][] SigmaPlus = pinvComp[1];
		double[][] Vr = pinvComp[2];
		double[][] Acomp = pinvComp[3];
		double[] betasU = pinvComp[4][0];
		double[] betasV = pinvComp[5][0];

		// compute a = U' * y
		double[] a = copyVector(y);
		// Apply rest of U factors (symmetric)
		int numU = betasU.length;
		int numV = betasV.length;
		double[] v;
		int vLen;
		double[] mini_a;
		if (fatX) {
			for (int i = 0; i < numV - 1; i++) {
				v = getHouseholderVectorV(Acomp, i, betasV[i]);
				vLen = v.length;
				mini_a = getSection(a, m - vLen, m - 1);
				assignSection(a, subtract(mini_a, multiply(inner(mini_a, v), v)), m - vLen);
			}
		} else {
			for (int i = 0; i < numU - 1; i++) {
				v = getHouseholderVectorU(Acomp, i, betasU[i]);
				vLen = v.length;
				mini_a = getSection(a, m - vLen, m - 1);
				assignSection(a, subtract(mini_a, multiply(inner(v, mini_a), v)), m - vLen);
			}
		}
		a = fatX ? multiply(a, Vr) : multiply(transpose(Ur), a);
		double[] w = fatX ? multiply(a, SigmaPlus) : multiply(SigmaPlus, a);
		w = fatX ? multiply(w, transpose(Ur)) : multiply(Vr, w);
		double[] mini_w;
		if (fatX) {
			for (int i = numU - 2; i >= 0; i--) {
				v = getHouseholderVectorU(Acomp, i, betasU[i]);
				vLen = v.length;
				mini_w = getSection(w, n - vLen, n - 1);
				assignSection(w, subtract(mini_w, multiply(inner(mini_w, v), v)), n - vLen);
			}
		} else {
			for (int i = numV - 2; i >= 0; i--) {
				v = getHouseholderVectorV(Acomp, i, betasV[i]);
				vLen = v.length;
				mini_w = getSection(w, n - vLen, n - 1);
				assignSection(w, subtract(mini_w, multiply(inner(v, mini_w), v)), n - vLen);
			}
		}
		return w;
	}
	public static double[][][] pseudoinverseCompact(double[][] A) {
		assertMoreRowsThanCols(A);
		int m = A.length;
		int n = A[0].length;
		double[][] SigmaPlus = new double[n][m];
		double[][][] svdComp = svdFast(A);
		double[][] Ur = svdComp[0];
		double[][] Sigma = svdComp[1];
		double[][] Vr = svdComp[2];
		double[][] Acomp = svdComp[3];
		double[] betasU = svdComp[4][0];
		double[] betasV = svdComp[5][0];
		double max = 0;
		for (int i = 0; i < n; i++) {
			if (Math.abs(Sigma[i][i]) > max) {
				max = Math.abs(Sigma[i][i]);
			}
		}
		double tol = eps0 * max * Math.max(m, n);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j && Math.abs(Sigma[i][j]) > tol) {
					SigmaPlus[j][i] = 1.0 / Sigma[i][j];
				} else {
					SigmaPlus[j][i] = 0;
				}
			}
		}
		return new double[][][] { Ur, SigmaPlus, Vr, Acomp, { betasU }, { betasV } };
	}
	public static double[][] pseudoinverseFull(double[][] A) {
		assertMoreRowsThanCols(A);
		int m = A.length;
		int n = A[0].length;
		double[][] SigmaPlus = new double[n][m];
		double[][][] svd = svdFull(A);
		double[][] U = svd[0];
		double[][] Sigma = svd[1];
		double[][] V = svd[2];
		double max = 0;
		for (int i = 0; i < n; i++) {
			if (Math.abs(Sigma[i][i]) > max) {
				max = Math.abs(Sigma[i][i]);
			}
		}
		double tol = eps0 * max * Math.max(m, n);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j && Math.abs(Sigma[i][j]) > tol) {
					SigmaPlus[j][i] = 1.0 / Sigma[i][j];
				} else {
					SigmaPlus[j][i] = 0;
				}
			}
		}
		return multiply(V, multiply(SigmaPlus, transpose(U)));
	}
	private static double[][] house(double[] x) {
		int n = x.length;
		double sigma = 0;
		double[] v = new double[n];
		v[0] = 1;
		double beta;
		for (int i = 1; i < n; i++) {
			sigma += x[i] * x[i];
			v[i] = x[i];
		}
		if (sigma < eps0) {
			beta = 0;
		} else {
			double mu = Math.sqrt(x[0] * x[0] + sigma);
			if (x[0] <= 0) {
				v[0] = x[0] - mu;
			} else {
				v[0] = -sigma / (x[0] + mu);
			}
			double v02 = v[0] * v[0];
			beta = 2 * v02 / (sigma + v02);
			double v0 = v[0];
			for (int i = 0; i < n; i++) {
				v[i] /= v0;
			}
		}
		return new double[][] { v, { beta } };
	}
	private static double[][][] householderBidiagonalizeCompact(double[][] A) {
		assertMoreRowsThanCols(A);
		int m = A.length;
		int n = A[0].length;
		double[][] B = new double[m][n];

		// Copy A into B
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				B[i][j] = A[i][j];
			}
		}

		double[] betasU = new double[n];
		double[] betasV = new double[n - 1];
		double[][] vBeta;
		double[] v;
		double[][] miniB;
		for (int j = 0; j < n; j++) {
			vBeta = house(getColumn(B, j, j, m - 1));
			v = vBeta[0];
			betasU[j] = vBeta[1][0];
			miniB = getSection(B, j, m - 1, j, n - 1);
			assignSection(B, subtract(miniB, outer(multiply(betasU[j], v), multiply(v, miniB))), j, j);
			assignCol(B, getSection(v, 1, m - j - 1), j, j + 1); // Store U information
			if (j < n - 2) {
				vBeta = house(getRow(B, j, j + 1, n - 1));
				v = vBeta[0];
				betasV[j] = vBeta[1][0];
				miniB = getSection(B, j, m - 1, j + 1, n - 1);
				assignSection(B, subtract(miniB, outer(multiply(miniB, v), multiply(betasV[j], v))), j, j + 1);
				assignRow(B, getSection(v, 1, n - j - 2), j, j + 2); // Store V information
			}
		}
		return new double[][][] { B, { betasU }, { betasV } };
	}
	private static double[][][] householderBidiagonalizeExpanded(double[][] A) {
		double[][][] hh = householderBidiagonalizeCompact(A);
		double[][] Acomp = hh[0];
		double[] betasU = hh[1][0];
		double[] betasV = hh[2][0];
		double[][] B = extractUpperBidiagonal(Acomp);
		double[][] U = extractBidiagonalizeU(Acomp, betasU);
		double[][] V = extractBidiagonalizeV(Acomp, betasV);
		return new double[][][] { U, B, V };
	}
	private static double[][] extractUpperBidiagonal(double[][] A) {
		int m = A.length;
		int n = A[0].length;
		double[][] B = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (j == i || j == i + 1) {
					B[i][j] = A[i][j];
				}
			}
		}
		return B;
	}
	private static double[][] extractBidiagonalizeU(double[][] A, double[] betas) {
		return extractBidiagonalizeU(A, betas, -1);
	}
	private static double[][] extractBidiagonalizeU(double[][] A, double[] betas, int start) {
		return extractHouseReflection(A, betas, true, start);
	}
	private static double[][] extractBidiagonalizeV(double[][] A, double[] betas) {
		return extractBidiagonalizeV(A, betas, -1);
	}
	private static double[][] extractBidiagonalizeV(double[][] A, double[] betas, int start) {
		return extractHouseReflection(A, betas, false, start);
	}
	private static double[][] extractHouseReflection(double[][] C, double[] betas, boolean lower) {
		return extractHouseReflection(C, betas, lower, -1);
	}
	private static double[][] extractHouseReflection(double[][] C, double[] betas, boolean lower, int start) {
		int shift;
		if (!lower) { // V
			C = transpose(C);
			shift = 1;
		} else { // U
			shift = 0;
		}

		int p = C.length; // num rows
		int q = C[0].length; // num cols
		int maxRow = p - 1;
		//		int maxCol = q - 1;
		double[][] Q = identity(p);
		int r = Math.min(p, q);
		double[] v;
		double[][] miniQ;
		for (int j = r - 1 - (start == -1 ? 0 : start); lower ? j >= 0 : j > 0; j--) {
			v = getColumn(C, j - shift, j, maxRow);
			v[0] = 1;
			miniQ = subtract(identity(p - j), multiply(betas[j - shift], outer(v, v)));
			assignSection(Q, multiply(miniQ, getSection(Q, j, maxRow, j, maxRow)), j, j);
		}
		return Q;
	}
	public static void printDimensions(double[][] A) {
		printDimensions(A, null);
	}
	public static void printDimensions(double[][] A, String name) {
		System.out.println((SH.is(name) ? name + " : " : "") + "(" + A.length + " x " + A[0].length + ")");
	}
	public static void printDimensions(double[] x, String name) {
		System.out.println((SH.is(name) ? name + " : " : "") + "(" + x.length + " x 1)");
	}
	public static double[] getRow(double[][] A, int row) {
		return getRow(A, row, 0, A[0].length - 1);
	}
	public static double[] getRow(double[][] A, int row, int minCol, int maxCol) {
		double[] x = new double[maxCol - minCol + 1];
		for (int j = minCol; j <= maxCol; j++) {
			x[j - minCol] = A[row][j];
		}
		return x;
	}
	public static double[] getColumn(double[][] A, int col) {
		return getColumn(A, col, 0, A.length - 1);
	}
	public static double[] getColumn(double[][] A, int col, int minRow, int maxRow) {
		double[] x = new double[maxRow - minRow + 1];
		for (int i = minRow; i <= maxRow; i++) {
			x[i - minRow] = A[i][col];
		}
		return x;
	}
	public static double[][] getSection(double[][] A, int minRow, int maxRow, int minCol, int maxCol) {
		double[][] output = new double[maxRow - minRow + 1][maxCol - minCol + 1];
		for (int i = minRow; i <= maxRow; i++) {
			for (int j = minCol; j <= maxCol; j++) {
				output[i - minRow][j - minCol] = A[i][j];
			}
		}
		return output;
	}
	public static double[] getSection(double[] x, int min, int max) {
		double[] output = new double[max - min + 1];
		for (int i = min; i <= max; i++) {
			output[i - min] = x[i];
		}
		return output;
	}
	public static void assignSection(double[][] A, double[][] section, int row, int col) {
		for (int i = 0; i < section.length; i++) {
			for (int j = 0; j < section[0].length; j++) {
				A[i + row][j + col] = section[i][j];
			}
		}
	}
	public static void assignSection(double[] v, double[] section, int start) {
		for (int i = 0; i < section.length; i++) {
			v[i + start] = section[i];
		}
	}
	public static void assignRow(double[][] A, double[] row, int rowIdx) {
		assignRow(A, row, rowIdx, 0);
	}
	public static void assignRow(double[][] A, double[] row, int rowIdx, int colOffset) {
		for (int j = 0; j < row.length; j++) {
			A[rowIdx][j + colOffset] = row[j];
		}
	}
	public static void assignCol(double[][] A, double[] col, int colIdx) {
		assignRow(A, col, colIdx, 0);
	}
	public static void assignCol(double[][] A, double[] col, int colIdx, int rowOffset) {
		for (int i = 0; i < col.length; i++) {
			A[i + rowOffset][colIdx] = col[i];
		}
	}
	private static double[][] blockDiag(double[][] A, double[][] B) {
		int rowsA = A.length;
		int colsA = A[0].length;
		int rowsB = B.length;
		int colsB = B[0].length;
		int rowsC = rowsA + rowsB;
		int colsC = colsA + colsB;
		double[][] C = new double[rowsC][colsC];
		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsA; j++) {
				C[i][j] = A[i][j];
			}
		}
		for (int i = 0; i < rowsB; i++) {
			for (int j = 0; j < colsB; j++) {
				C[i + rowsA][j + colsA] = B[i][j];
			}
		}
		return C;
	}
	private static void assertSameDims(double[][] A, double[][] B) {
		if (A.length != B.length || A[0].length != B[0].length) {
			throw new IllegalArgumentException("Matrices must be same dimesions: A is (" + A.length + " x " + A[0].length + "), B is (" + B.length + " x " + B[0].length + ")");
		}
	}
	private static void assertSameLength(double[] x, double[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException("Vectors must be same length: x is " + x.length + ", y is " + y.length);
		}
	}
	private static boolean allClose(double[][] A, double[][] B, double tol) {
		assertSameDims(A, B);
		int m = A.length;
		int n = A[0].length;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (Math.abs(A[i][j] - B[i][j]) > tol) {
					return false;
				}
			}
		}
		return true;
	}
	private static boolean allClose(double[][] A, double[][] B) {
		return allClose(A, B, eps0);
	}
	private static boolean allClose(double[] x, double[] y, double tol) {
		int m = x.length;
		if (m != y.length) {
			throw new IllegalArgumentException("Mismatch lengths");
		}
		for (int i = 0; i < m; i++) {
			if (Math.abs(x[i] - y[i]) > tol) {
				return false;
			}
		}
		return true;
	}
	private static boolean allClose(double[] x, double[] y) {
		return allClose(x, y, eps0);
	}
	private static boolean[][] isClose(double[][] A, double[][] B, double tol) {
		assertSameDims(A, B);
		int m = A.length;
		int n = A[0].length;
		boolean[][] output = new boolean[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				output[i][j] = Math.abs(A[i][j] - B[i][j]) < tol;
			}
		}
		return output;
	}
	private static boolean[][] isClose(double[][] A, double[][] B) {
		return isClose(A, B, eps0);
	}

	private static final byte REAL = 0;
	private static final byte IMAG = 1;

	private static void assertComplexFormat(double[][] x) {
		if (x.length != 2)
			throw new IllegalArgumentException("First dimension of a complex vector must be of length 2.");
	}

	/**
	 * Performs Fast Fourier Transform (FFT) on complex input signal x. The second dimension of x can be any length, but the first dimension must be two. x[0][i] represents the
	 * real component of the ith element in x. Likewise, x[1][i] represents the imaginary component of the ith element in x.
	 */
	public static void fft(double[][] x) {
		assertComplexFormat(x);
		int N = x[REAL].length;
		if (N == 0) {
			return;
		} else if ((N & (N - 1)) == 0) { // length is power of 2 
			fftRadix2(x);
		} else { // arbitrary length, but more expensive
			fftBluestein(x);
		}
	}
	public static void fftReal(double[] real) {
		fft(new double[][] { real, new double[real.length] });
	}
	public static void fftImag(double[] imag) {
		fft(new double[][] { new double[imag.length], imag });
	}
	public static void ifft(double[][] X) {
		assertComplexFormat(X);
		fft(new double[][] { X[IMAG], X[REAL] });
	}
	private static void fftRadix2(double[][] x) {
		assertComplexFormat(x);
		int N = x[REAL].length;
		int levels = 31 - Integer.numberOfLeadingZeros(N);
		if (1 << levels != N) {
			throw new IllegalArgumentException("Length is not a power of 2");
		}

		// Lookup tables
		double[] cosine = new double[N / 2];
		double[] sine = new double[N / 2];
		for (int i = 0; i < N / 2; i++) {
			cosine[i] = Math.cos(2 * Math.PI * i / N);
			sine[i] = Math.sin(2 * Math.PI * i / N);
		}

		// Bit-reversed addressing permutation
		for (int i = 0; i < N; i++) {
			int j = Integer.reverse(i) >>> (32 - levels);
			if (j > i) {
				double tmp = x[REAL][i];
				x[REAL][i] = x[REAL][j];
				x[REAL][j] = tmp;
				tmp = x[IMAG][i];
				x[IMAG][i] = x[IMAG][j];
				x[IMAG][j] = tmp;
			}
		}

		// Cooley-Tukey
		for (int size = 2; size <= N; size *= 2) {
			int halfSize = size / 2;
			int trigStep = N / size;
			int l;
			double tpre, tpim;
			for (int i = 0; i < N; i += size) {
				for (int j = i, k = 0; j < i + halfSize; j++, k += trigStep) {
					l = j + halfSize;
					tpre = x[REAL][l] * cosine[k] + x[IMAG][l] * sine[k];
					tpim = -x[REAL][l] * sine[k] + x[IMAG][l] * cosine[k];
					x[REAL][l] = x[REAL][j] - tpre;
					x[IMAG][l] = x[IMAG][j] - tpim;
					x[REAL][j] += tpre;
					x[IMAG][j] += tpim;
				}
			}
			if (size == N) { // Prevent overflow in 'size *= 2'
				break;
			}
		}
	}
	private static void fftBluestein(double[][] x) {
		assertComplexFormat(x);
		// Find power-of-2 convolution length M such that M >= 2 * N - 1
		int N = x[REAL].length;
		if (N >= 0x20000000) {
			throw new IllegalArgumentException("Input signal too large");
		}
		int M = Integer.highestOneBit(N) * 4;

		// Lookup tables
		double[] cosine = new double[N];
		double[] sine = new double[N];
		int j;
		for (int i = 0; i < N; i++) {
			j = (int) ((long) i * i % (N * 2)); // more accurate than j = i * i
			cosine[i] = Math.cos(Math.PI * j / N);
			sine[i] = Math.sin(Math.PI * j / N);
		}

		// Temporary vectors and pre-processing
		double[][] a = new double[2][M];
		for (int i = 0; i < N; i++) {
			a[REAL][i] = x[REAL][i] * cosine[i] + x[IMAG][i] * sine[i];
			a[IMAG][i] = -x[REAL][i] * sine[i] + x[IMAG][i] * cosine[i];
		}
		double[][] b = new double[2][M];
		b[REAL][0] = cosine[0];
		b[IMAG][0] = sine[0];
		for (int i = 1; i < N; i++) {
			b[REAL][i] = b[REAL][M - i] = cosine[i];
			b[IMAG][i] = b[IMAG][M - i] = sine[i];
		}

		// Convolution
		double[][] c = new double[2][M];
		circConvolve(a, b, c);

		// Post-processing
		for (int i = 0; i < N; i++) {
			x[REAL][i] = c[REAL][i] * cosine[i] + c[IMAG][i] * sine[i];
			x[IMAG][i] = -c[REAL][i] * sine[i] + c[IMAG][i] * cosine[i];
		}
	}
	public static void circConvolve(double[] x, double[] y, double[] output) {
		int N = x.length;
		if (N != y.length || N != output.length) {
			throw new IllegalArgumentException("Mismatched lengths");
		}
		circConvolve(new double[][] { x, new double[N] }, new double[][] { y, new double[N] }, new double[][] { output, new double[N] });
	}
	public static void circConvolve(double[][] x, double[][] y, double[][] output) {
		assertComplexFormat(x);
		assertComplexFormat(y);
		assertComplexFormat(output);
		int N = x[REAL].length;
		if (N != y[REAL].length || N != output[REAL].length) {
			throw new IllegalArgumentException("Mismatched lengths");
		}

		x = x.clone();
		y = y.clone();
		fft(x);
		fft(y);

		double tmp;
		for (int i = 0; i < N; i++) {
			tmp = x[REAL][i] * y[REAL][i] - x[IMAG][i] * y[IMAG][i];
			x[IMAG][i] = x[IMAG][i] * y[REAL][i] + x[REAL][i] * y[IMAG][i];
			x[REAL][i] = tmp;
		}

		ifft(x);

		for (int i = 0; i < N; i++) { // Scale
			output[REAL][i] = x[REAL][i] / N;
			output[IMAG][i] = x[IMAG][i] / N;
		}
	}

	private static final String CONVOLUTION_MODE_FULL = "full";
	private static final String CONVOLUTION_MODE_SAME = "same";
	//	private static final String CONVOLUTION_MODE_VALID = "valid";

	private static double[] convolveNaive(double[] x, double[] h, String mode) {
		int N = x.length;
		int M = h.length;
		int p1;
		int p2;
		int outputLength;
		if (CONVOLUTION_MODE_SAME.equals(mode)) {
			p2 = M / 2;
			p1 = M % 2 == 0 ? p2 - 1 : p2;
			outputLength = Math.max(M, N);
		} else {
			p1 = 0;
			p2 = 0;
			outputLength = N + M - 1;
		}
		double[] y = new double[outputLength];

		for (int i = p1; i < outputLength + p1; i++) {
			for (int j = 0; j < M; j++) {
				if (i - j >= 0 && i - j < N && MH.isNumber(x[i - j]) && MH.isNumber(h[j])) {
					y[i - p1] += x[i - j] * h[j];
				}
			}
		}
		return y;
	}
	public static double[] convolveNaiveFull(double[] x, double[] h) {
		return convolveNaive(x, h, CONVOLUTION_MODE_FULL);
	}
	public static double[] convolveNaiveSame(double[] x, double[] h) {
		return convolveNaive(x, h, CONVOLUTION_MODE_SAME);
	}
	public static double calculateGaussianFilterVariance(int N, double ratio) {
		int a, b;
		if (N % 2 == 0) { // even
			a = N / 2;
			b = a - 1;
		} else { // odd
			a = (N - 1) / 2;
			b = a;
		}
		return -a * b / (2.0 * Math.log(ratio));
	}
	public static double[] generateGaussianFilterTimeBased(double[] t) {
		return generateGaussianFilterTimeBased(calculateGaussianFilterVariance(t.length, 0.05), t);
	}
	public static double[] generateGaussianFilterTimeBased(double var, double[] t) {
		int N = t.length;
		double[] output = new double[N];
		if (var == 0) {
			output[N / 2] = 1;
			return output;
		}
		double den = 2 * var;
		double weightSum = 0;
		double weight, arg;
		double center = t[N / 2];
		for (int i = 0; i < N; i++) {
			if (MH.isNumber(t[i])) {
				arg = t[i] - center;
				weight = Math.exp(-(arg * arg) / den);
				output[i] = weight;
				weightSum += weight;
			}
		}
		// Normalize
		if (weightSum != 0)
			for (int i = 0; i < N; i++) {
				output[i] = output[i] / weightSum;
			}
		return output;
	}
	public static double[] generateGaussianFilter(int N, double var) {
		double[] output = new double[N];
		int middle = N / 2;
		double den = 2 * var;
		double weightSum = 0;
		double weight, arg;
		if (N % 2 == 0) { // even
			for (int i = 0; i < middle; i++) {
				arg = i + 0.5;
				weight = Math.exp(-(arg * arg) / den);
				output[middle - i - 1] = weight;
				output[middle + i] = weight;
				weightSum += 2 * weight;
			}
		} else { // odd
			// Middle element
			output[middle] = 1;
			weightSum = 1;
			// Rest of elements
			for (int i = 1; i <= middle; i++) {
				weight = Math.exp(-((double) i * i) / den);
				output[middle - i] = weight;
				output[middle + i] = weight;
				weightSum += 2 * weight;
			}
		}
		// Normalize
		if (weightSum != 0)
			for (int i = 0; i < N; i++) {
				output[i] = output[i] / weightSum;
			}
		return output;
	}
	public static double[] generateGaussianFilter(int N) {
		return generateGaussianFilter(N, calculateGaussianFilterVariance(N, 0.05));
	}
	//	private static void testStrassen() {
	//		long seed = 123L;
	//		long start, end;
	//		int maxExp = 10;
	//		int m = 1000;
	//		int n = 1000;
	//		int p = 1000;
	//		double[][] A = generateRandomMatrix(m, n, seed);
	//		double[][] B = generateRandomMatrix(n, p, seed);
	//		long sum;
	//		int numTrials = 25;
	//		double[] results = new double[maxExp];
	//		for (int i = 1; i <= maxExp; i++) { // power of 2
	//			sum = 0;
	//			for (int t = 0; t < numTrials; t++) {
	//				start = System.nanoTime();
	//				strassenMultiply(A, B, i);
	//				end = System.nanoTime();
	//				sum += end - start;
	//			}
	//			results[i - 1] = ((double) sum) / numTrials;
	//		}
	//		// print results
	//		for (int i = 0; i < maxExp; i++) {
	//			System.out.println("2 ^ " + i + " : " + results[i]);
	//		}
	//	}
	//	public static double[][] strassenMultiply(double[][] A, double[][] B) {
	//		return strassenMultiply(A, B, 8);
	//	}
	public static double[][] strassenMultiply(double[][] A, double[][] B) {
		int maxDim = MH.max(A.length, A[0].length, B.length);
		if (maxDim < 256) {
			return multiplyNaive(A, B);
		} else {
			int paddedDim = MH.nextPowerOfTwo(maxDim);
			int halfPaddedDim = paddedDim / 2;
			int lastEntry = paddedDim - 1;
			double[][] Apd = new double[paddedDim][paddedDim];
			double[][] Bpd = new double[paddedDim][paddedDim];
			double[][] Cpd = new double[paddedDim][paddedDim];
			assignSection(Apd, A, 0, 0);
			assignSection(Bpd, B, 0, 0);
			double[][] A11 = getSection(Apd, 0, halfPaddedDim - 1, 0, halfPaddedDim - 1);
			double[][] A12 = getSection(Apd, 0, halfPaddedDim - 1, halfPaddedDim, lastEntry);
			double[][] A21 = getSection(Apd, halfPaddedDim, lastEntry, 0, halfPaddedDim - 1);
			double[][] A22 = getSection(Apd, halfPaddedDim, lastEntry, halfPaddedDim, lastEntry);
			double[][] B11 = getSection(Bpd, 0, halfPaddedDim - 1, 0, halfPaddedDim - 1);
			double[][] B12 = getSection(Bpd, 0, halfPaddedDim - 1, halfPaddedDim, lastEntry);
			double[][] B21 = getSection(Bpd, halfPaddedDim, lastEntry, 0, halfPaddedDim - 1);
			double[][] B22 = getSection(Bpd, halfPaddedDim, lastEntry, halfPaddedDim, lastEntry);
			double[][] M1 = strassenMultiply(add(A11, A22), add(B11, B22));
			double[][] M2 = strassenMultiply(add(A21, A22), B11);
			double[][] M3 = strassenMultiply(A11, subtract(B12, B22));
			double[][] M4 = strassenMultiply(A22, subtract(B21, B11));
			double[][] M5 = strassenMultiply(add(A11, A12), B22);
			double[][] M6 = strassenMultiply(subtract(A21, A11), add(B11, B12));
			double[][] M7 = strassenMultiply(subtract(A12, A22), add(B21, B22));

			double[][] C11 = add(subtract(add(M1, M4), M5), M7);
			double[][] C12 = add(M3, M5);
			double[][] C21 = add(M2, M4);
			double[][] C22 = add(subtract(add(M1, M3), M2), M6);
			composeBlockMatrices(C11, C12, C21, C22, Cpd);
			return getSection(Cpd, 0, A.length - 1, 0, B[0].length - 1);
		}
	}
	public static boolean isSymmetric(double[][] A) {
		int m = A.length; // num rows
		int n = A[0].length; // num rows
		if (m != n) {
			return false;
		}
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j && A[i][j] != A[j][i]) {
					return false;
				}
			}
		}
		return true;
	}
	private static void assertSymmetric(double[][] A) {
		if (!isSymmetric(A)) {
			throw new IllegalArgumentException("Input must be a symmetric matrix");
		}
	}
	public static void composeBlockMatrices(double[][] A, double[][] B, double[][] C, double[][] D, double[][] M) {
		int rowsM = M.length;
		int colsM = M[0].length;
		int rowsA = A.length;
		int colsA = A[0].length;
		int rowsB = B.length;
		int colsB = B[0].length;
		int rowsC = C.length;
		int colsC = C[0].length;
		int rowsD = D.length;
		int colsD = D[0].length;
		if (rowsM != rowsA + rowsC || rowsM != rowsB + rowsD || colsM != colsA + colsB || colsM != colsC + colsD) {
			throw new IllegalArgumentException("Dimension mismatch");
		}
		for (int i = 0; i < rowsA; i++) { // copy A
			for (int j = 0; j < colsA; j++) {
				M[i][j] = A[i][j];
			}
		}
		for (int i = 0; i < rowsB; i++) { // copy B
			for (int j = 0; j < colsB; j++) {
				M[i][j + colsA] = B[i][j];
			}
		}
		for (int i = 0; i < rowsC; i++) { // copy C
			for (int j = 0; j < colsC; j++) {
				M[i + rowsA][j] = C[i][j];
			}
		}
		for (int i = 0; i < rowsD; i++) { // copy D
			for (int j = 0; j < colsD; j++) {
				M[i + rowsA][j + colsA] = D[i][j];
			}
		}
	}
	public static double[][] generateRandomMatrix(int nRows, int nCols) {
		return generateRandomMatrix(nRows, nCols, null);
	}
	public static double[][] generateRandomMatrix(int nRows, int nCols, Long seed) {
		Random rng = seed == null ? new Random() : new Random(seed.longValue());
		double[][] output = new double[nRows][nCols];
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				output[i][j] = rng.nextDouble();
			}
		}
		return output;
	}
	private static double[] interpLinear(double x0, double y0, double x1, double y1) {
		double m = (y1 - y0) / (x1 - x0);
		return new double[] { y0 - m * x0, m };
	}
	private static double[][] interpSplineNotAKnot(double[] x, double[] y) {
		if (x.length < 5) {
			throw new IllegalArgumentException("Input must have at least 5 points for spline interpolation.");
		}
		return interpSpline(x, y, Double.NaN, Double.NaN, true);
	}
	private static double[][] interpSpline(double[] x_, double[] y_, double derivInitial, double derivFinal, boolean notAKnot) {
		assertSameLength(x_, y_);
		double[] x, y;
		if (notAKnot) {
			int m2 = x_.length - 2;
			x = new double[m2];
			y = new double[m2];
			x[0] = x_[0];
			x[m2 - 1] = x_[m2 + 1];
			y[0] = y_[0];
			y[m2 - 1] = y_[m2 + 1];
			for (int i = 1; i < m2 - 1; i++) {
				x[i] = x_[i + 1];
				y[i] = y_[i + 1];
			}
		} else {
			x = x_;
			y = y_;
		}

		// Build matrix
		int m = x.length;
		int n = 4 * (m - 1);
		double[][] A = new double[n][n];
		double x0 = x[0];
		double x1 = x[1];

		// Top 4 rows 
		if (notAKnot) { // not-a-knot
			A[0][0] = 1;
			A[0][1] = x_[1];
			A[0][2] = x_[1] * x_[1];
			A[0][3] = x_[1] * x_[1] * x_[1];
		} else { // derivative at first point provided by user
			A[0][1] = 1;
			A[0][2] = 2 * x0;
			A[0][3] = 3 * x0 * x0;
		}
		A[1][0] = 1;
		A[1][1] = x0;
		A[1][2] = x0 * x0;
		A[1][3] = x0 * x0 * x0;
		A[2][0] = 1;
		A[2][1] = x1;
		A[2][2] = x1 * x1;
		A[2][3] = x1 * x1 * x1;
		A[3][1] = 1;
		A[3][2] = 2 * x1;
		A[3][3] = 3 * x1 * x1;
		A[3][5] = -A[3][1];
		A[3][6] = -A[3][2];
		A[3][7] = -A[3][3];
		int i; // row index
		for (int j = 1; j < m - 2; j++) { // segment index
			i = j * 4;
			A[i][i - 2] = 2;
			A[i][i - 1] = 6 * x[j];
			A[i][i + 2] = -2;
			A[i][i + 3] = -6 * x[j];
			A[i + 1][i] = 1;
			A[i + 1][i + 1] = x[j];
			A[i + 1][i + 2] = x[j] * x[j];
			A[i + 1][i + 3] = x[j] * x[j] * x[j];
			A[i + 2][i] = 1;
			A[i + 2][i + 1] = x[j + 1];
			A[i + 2][i + 2] = x[j + 1] * x[j + 1];
			A[i + 2][i + 3] = x[j + 1] * x[j + 1] * x[j + 1];
			A[i + 3][i + 1] = 1;
			A[i + 3][i + 2] = 2 * x[j + 1];
			A[i + 3][i + 3] = 3 * x[j + 1] * x[j + 1];
			A[i + 3][i + 5] = -A[i + 3][i + 1];
			A[i + 3][i + 6] = -A[i + 3][i + 2];
			A[i + 3][i + 7] = -A[i + 3][i + 3];
		}
		// Last 4 rows
		i = n - 4;
		A[n - 4][n - 6] = 2;
		A[n - 4][n - 5] = 6 * x[m - 2];
		A[n - 4][n - 2] = -A[n - 4][n - 6];
		A[n - 4][n - 1] = -A[n - 4][n - 5];
		A[n - 3][n - 4] = 1;
		A[n - 3][n - 3] = x[m - 2];
		A[n - 3][n - 2] = x[m - 2] * x[m - 2];
		A[n - 3][n - 1] = x[m - 2] * x[m - 2] * x[m - 2];
		A[n - 2][n - 4] = 1;
		A[n - 2][n - 3] = x[m - 1];
		A[n - 2][n - 2] = x[m - 1] * x[m - 1];
		A[n - 2][n - 1] = x[m - 1] * x[m - 1] * x[m - 1];
		if (notAKnot) { // not-a-knot
			int xBarLen = x_.length;
			int skipIdx = xBarLen - 2;
			A[n - 1][n - 4] = 1;
			A[n - 1][n - 3] = x_[skipIdx];
			A[n - 1][n - 2] = x_[skipIdx] * x_[skipIdx];
			A[n - 1][n - 1] = x_[skipIdx] * x_[skipIdx] * x_[skipIdx];
		} else { // derivative at first point provided by user
			A[n - 1][n - 3] = 1;
			A[n - 1][n - 2] = 2 * x[m - 1];
			A[n - 1][n - 1] = 3 * x[m - 1] * x[m - 1];
		}

		// Construct b vector
		double[] b = new double[n];
		for (int j = 0; j < m - 1; j++) {
			b[4 * j + 1] = y[j];
			b[4 * j + 2] = y[j + 1];
		}
		if (notAKnot) { // not-a-knot
			b[0] = y_[1];
			b[n - 1] = y_[y_.length - 2];
		} else { // derivative at first point provided by user
			b[0] = derivInitial;
			b[n - 1] = derivFinal;
		}

		double[] params = solveSquareLinearEquations(A, b);

		// Arrange params into rows
		double[][] output = new double[m - 1][4];
		for (int j = 0; j < m - 1; j++) {
			for (int k = 0; k < 4; k++) {
				//				output[j][k] = params[4 * (j + 1) - k - 1];
				output[j][k] = params[4 * (j) + k];
			}
		}
		return output;
	}

	public static final String INTERP_METHOD_LINEAR = "linear";
	public static final String INTERP_METHOD_SPLINE = "spline";
	public static final Set<String> INTERP_METHODS = CH.s(INTERP_METHOD_LINEAR, INTERP_METHOD_SPLINE);

	public static double[][] interp(double[] x, double[] y, String method) {
		if (!INTERP_METHODS.contains(method)) {
			throw new IllegalArgumentException("Illegal method argument: " + method);
		}
		int N = x.length;
		if (N != y.length) {
			throw new IllegalArgumentException("Data vectors must be same length.");
		}
		if (INTERP_METHOD_LINEAR.equals(method)) {
			double[][] output = new double[N - 1][2];
			double xCur, yPrev;
			for (int i = 0; i < N - 1; i++) {
				if (MH.isntNumber(x[i]) || MH.isntNumber(y[i])) {
					continue;
				}
				xCur = x[i];
				yPrev = y[i];
				output[i] = interpLinear(xCur, yPrev, x[i + 1], y[i + 1]);
			}
			return output;
		} else if (INTERP_METHOD_SPLINE.equals(method)) {
			return interpSplineNotAKnot(x, y);
		}
		throw new IllegalArgumentException("Illegal interpolation method: " + method);
	}
	public static double[] resample(double[] x, double[] y, double[] xq, String method) {
		assertSameLength(x, y);
		assertSameLength(x, xq);
		int n = xq.length;
		double[][] params = interp(x, y, method);
		int numParams = params.length;
		double[] yq = new double[n];
		if (INTERP_METHOD_LINEAR.equals(method)) {
			double m = 0, b = 0;
			for (int i = 0; i < n; i++) {
				if (xq[i] <= x[0]) {
					m = params[0][1];
					b = params[0][0];
				} else if (xq[i] > x[n - 2]) {
					m = params[n - 2][1];
					b = params[n - 2][0];
				} else {
					for (int j = 0; j < n - 1; j++) {
						if (x[j] < xq[i] && xq[i] <= x[j + 1]) {
							m = params[j][1];
							b = params[j][0];
						}
					}
				}
				yq[i] = m * xq[i] + b;
			}
		} else if (INTERP_METHOD_SPLINE.equals(method)) {
			double a = 0, b = 0, c = 0, d = 0;
			for (int i = 0; i < n; i++) {
				if (xq[i] <= x[2]) {
					a = params[0][0];
					b = params[0][1];
					c = params[0][2];
					d = params[0][3];
				} else if (xq[i] > x[n - 3]) {
					a = params[numParams - 1][0];
					b = params[numParams - 1][1];
					c = params[numParams - 1][2];
					d = params[numParams - 1][3];
				} else {
					for (int j = 2; j < n - 2; j++) {
						if (x[j] < xq[i] && xq[i] <= x[j + 1]) {
							a = params[j - 1][0];
							b = params[j - 1][1];
							c = params[j - 1][2];
							d = params[j - 1][3];
						}
					}
				}
				yq[i] = a + b * xq[i] + c * xq[i] * xq[i] + d * xq[i] * xq[i] * xq[i];
			}
		} else {
			throw new IllegalArgumentException("Illegal interpolation method: " + method);
		}
		return yq;
	}
}
