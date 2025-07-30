/**
 * 
 */
package com.f1.tcartsim.verify.util;

import java.util.ArrayList;

import com.f1.utils.MH;
import com.f1.utils.SH;

/**
 * @author george
 * 
 */
public class VerifyMath {

	public static boolean eq(String l, String r, char type) {
		switch (type) {
			case 'I':
				return SH.parseInt(l) == SH.parseInt(r);
			case 'L':
				return SH.parseLong(l) == SH.parseLong(r);
			case 'F':
				return SH.parseFloat(l) == SH.parseFloat(r);
			case 'D':
				return SH.parseDouble(l) == SH.parseDouble(r);
			default:
				throw new UnsupportedOperationException();

		}
	}

	public static boolean gt(String l, String r, char type) {
		switch (type) {
			case 'I':
				return SH.parseInt(l) > SH.parseInt(r);
			case 'L':
				return SH.parseLong(l) > SH.parseLong(r);
			case 'F':
				return SH.parseFloat(l) > SH.parseFloat(r);
			case 'D':
				return SH.parseDouble(l) > SH.parseDouble(r);
			default:
				throw new UnsupportedOperationException();

		}
	}

	public static boolean gte(String l, String r, char type) {
		switch (type) {
			case 'I':
				return SH.parseInt(l) >= SH.parseInt(r);
			case 'L':
				return SH.parseLong(l) >= SH.parseLong(r);
			case 'F':
				return SH.parseFloat(l) >= SH.parseFloat(r);
			case 'D':
				return SH.parseDouble(l) >= SH.parseDouble(r);
			default:
				throw new UnsupportedOperationException();

		}
	}

	public static boolean lt(String l, String r, char type) {
		return !gte(l, r, type);
	}

	public static boolean lte(String l, String r, char type) {
		return !gt(l, r, type);
	}

	public static boolean between(String t, String l, String r, char type) {
		return gte(t, l, type) && lt(t, r, type);
	}

	public static boolean nbetween(String t, String l, String r, char type) {
		return !between(t, l, r, type);
	}

	public static double percent(int part, int total) {
		return 100d * (double) part / (double) total;
	}

	public static double percent(long part, long total) {
		return 100d * (double) part / (double) total;
	}

	public static double percent(float part, float total) {
		return 100d * (double) part / (double) total;
	}

	public static double percent(double part, double total) {
		return 100d * part / total;
	}

	public static float percentF(int part, int total) {
		return 100f * (float) part / (float) total;
	}

	public static float percentF(long part, long total) {
		return 100f * (float) part / (float) total;
	}

	public static float percentF(float part, float total) {
		return 100f * part / total;
	}

	public static float percentF(double part, double total) {
		return 100f * (float) part / (float) total;
	}

	public static long sum(long[] a) {
		return MH.sum(a);
	}

	public static long sum(int[] a) {
		long r = 0;
		int i = a.length;
		while (i > 0)
			r += a[--i];
		return r;
	}

	public static int sumI(int[] a) {
		return MH.sum(a);
	}

	public static double sum(double[] a) {
		return MH.sum(a);
	}

	public static double sum(float[] a) {
		float r = 0;
		int i = a.length;
		while (i > 0)
			r += a[--i];
		return r;
	}

	public static float sumF(float[] a) {
		return MH.sum(a);
	}

	public static double avg(long[] a) {
		return (double) sum(a) / (double) a.length;
	}

	public static double avg(int[] a) {
		return (double) sum(a) / (double) a.length;
	}

	public static double avg(double[] a) {
		return sum(a) / (double) a.length;
	}

	public static double avg(float[] a) {
		return (double) sum(a) / (double) a.length;
	}
	public static ArrayList<String[]> limitDataUsingPosition(ArrayList<String[]> data, String limiter, int position) {
		ArrayList<String[]> truncData = new ArrayList<String[]>();
		for (int i = 0; i < data.size(); i++) {
			String parts[] = data.get(i);
			assert (position >= 0);
			String thisLimiter = parts[position];
			assert (SH.equals(thisLimiter, limiter) == SH.equalsIgnoreCase(thisLimiter, limiter));
			if (SH.equals(thisLimiter, limiter)) {
				truncData.add(parts);
			}
		}
		return truncData;
	}

	// Compares strings
	public static ArrayList<String[]> limitData(ArrayList<String[]> data, String[] limiter) {
		ArrayList<String[]> truncData = new ArrayList<String[]>();
		for (int j = 0; j < data.size(); j++) {
			String parts[] = data.get(j);
			// Limiter's length is required to be less than parts.length
			assert limiter.length < parts.length;
			boolean pass = true;
			for (int i = 0; i < limiter.length; i++) {
				assert (SH.equals(limiter[i], parts[i]) == SH.equalsIgnoreCase(limiter[i], parts[i]));
				if (SH.equals(limiter[i], "")) {
					continue;
				} else if (!SH.equals(limiter[i], parts[i])) {
					pass = false;
					break;
				}
			}
			if (pass) {
				truncData.add(parts);
			}
		}
		return truncData;
	}

	public static ArrayList<String[]> limitDataNot(ArrayList<String[]> data, String[] limiter) {
		ArrayList<String[]> truncData = new ArrayList<String[]>();
		for (int j = 0; j < data.size(); j++) {
			String parts[] = data.get(j);
			// Limiter's length is required to be less than parts.length
			assert limiter.length < parts.length;
			boolean pass = true;
			for (int i = 0; i < limiter.length; i++) {
				assert (SH.equals(limiter[i], parts[i]) == SH.equalsIgnoreCase(limiter[i], parts[i]));
				if (SH.equals(limiter[i], "")) {
					continue;
				} else if (SH.equals(limiter[i], parts[i])) {
					pass = false;
					break;
				}
			}
			if (pass) {
				truncData.add(parts);
			}
		}
		return truncData;
	}

	public static ArrayList<String[]> limitDataEquals(ArrayList<String[]> data, String[] limiter, char type) {
		ArrayList<String[]> truncData = new ArrayList<String[]>();
		for (int j = 0; j < data.size(); j++) {
			String parts[] = data.get(j);
			// Limiter's length is required to be less than parts.length
			assert limiter.length < parts.length;
			boolean pass = true;
			for (int i = 0; i < limiter.length; i++) {
				if (SH.equals(limiter[i], "")) {
					continue;
				} else if (SH.equals(parts[i], "")) {
					pass = false;
					break;
				} else if (!VerifyMath.eq(parts[i], limiter[i], type)) {
					pass = false;
					break;
				}
			}

			if (pass) {
				truncData.add(parts);
			}
		}
		return truncData;
	}

	public static ArrayList<String[]> limitDataGT(ArrayList<String[]> data, String[] limiter, char type) {
		ArrayList<String[]> truncData = new ArrayList<String[]>();
		for (int j = 0; j < data.size(); j++) {
			String parts[] = data.get(j);
			// Limiter's length is required to be less than parts.length
			assert limiter.length < parts.length;
			boolean pass = true;
			for (int i = 0; i < limiter.length; i++) {
				if (SH.equals(limiter[i], "")) {
					continue;
				} else if (SH.equals(parts[i], "")) {
					pass = false;
					break;
				} else if (!VerifyMath.gt(parts[i], limiter[i], type)) {
					pass = false;
					break;
				}
			}

			if (pass) {
				truncData.add(parts);
			}
		}
		return truncData;
	}

	public static ArrayList<String[]> limitDataGTE(ArrayList<String[]> data, String[] limiter, char type) {
		ArrayList<String[]> truncData = new ArrayList<String[]>();
		for (int j = 0; j < data.size(); j++) {
			String parts[] = data.get(j);
			// Limiter's length is required to be less than parts.length
			assert limiter.length < parts.length;
			boolean pass = true;
			for (int i = 0; i < limiter.length; i++) {
				if (SH.equals(limiter[i], "")) {
					continue;
				} else if (SH.equals(parts[i], "")) {
					pass = false;
					break;
				} else if (!VerifyMath.gte(parts[i], limiter[i], type)) {
					pass = false;
					break;
				}
			}

			if (pass) {
				truncData.add(parts);
			}
		}
		return truncData;
	}

	// Lower bound is inclusive, upper bound is not.
	public static ArrayList<String[]> limitDataBetween(ArrayList<String[]> data, String[] limiterLeft, String[] limiterRight, char type) {
		ArrayList<String[]> truncData = new ArrayList<String[]>();
		assert limiterLeft.length == limiterRight.length;
		for (int j = 0; j < data.size(); j++) {
			String parts[] = data.get(j);
			// Limiter's length is required to be less than parts.length
			assert limiterLeft.length < parts.length;
			boolean pass = true;
			for (int i = 0; i < limiterLeft.length; i++) {
				if (SH.equals(limiterLeft[i], "")) {
					continue;
				} else if (SH.equals(parts[i], "")) {
					pass = false;
					break;
				} else if (!VerifyMath.between(parts[i], limiterLeft[i], limiterRight[i], type)) {
					pass = false;
					break;
				}
			}

			if (pass) {
				truncData.add(parts);
			}
		}
		return truncData;
	}
}
