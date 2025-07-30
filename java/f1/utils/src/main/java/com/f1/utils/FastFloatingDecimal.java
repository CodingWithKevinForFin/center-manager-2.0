package com.f1.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.utils.fputils.DoubleConsts;
import com.f1.utils.fputils.FloatConsts;
import com.f1.utils.fputils.FpUtils;

public class FastFloatingDecimal {
	private static final String STRING_ZERO = "0.0";
	private static final String STRING_NAN = "NaN";
	private static final String STRING_POSITIVE_INFINITY = "Infinity";
	private static final String STRING_NEGATIVE_INFINITY = "-Infinity";
	static final long SIGN_MASK = 0x8000000000000000L;
	static final long EXP_MASK = 0x7ff0000000000000L;
	static final long FRACT_MASK = ~(SIGN_MASK | EXP_MASK);
	static final int EXP_SHIFT = 52;
	static final int EXP_BIAS = 1023;
	static final long FRACT_HOB = (1L << EXP_SHIFT);
	static final long EXP_ONE = ((long) EXP_BIAS) << EXP_SHIFT;
	static final int MAX_SMALL_BIN_EXP = 62;
	static final int MIN_SMALL_BIN_EXP = -(63 / 3);
	static final int MAX_DECIMAL_DIGITS = 15;
	static final int MAX_DECIMAL_EXPONENT = 308;
	static final int MIN_DECIMAL_EXPONENT = -324;
	static final int BIG_DECIMAL_EXPONENT = 324;

	static final long HIGH_BYTE = 0xff00000000000000L;
	static final long HIBH_BIT = 0x8000000000000000L;
	static final long LOW_BYTES = ~HIGH_BYTE;

	static final int SINGLE_SIGN_MASK = 0x80000000;
	static final int SINGLE_EXP_MASK = 0x7f800000;
	static final int SINGLE_FRACT_MASK = ~(SINGLE_SIGN_MASK | SINGLE_EXP_MASK);
	static final int SINGLE_EXP_SHIFT = 23;
	static final int SINGLE_FRACT_HDB = 1 << SINGLE_EXP_SHIFT;
	static final int SINGLE_EXP_BIAS = 127;
	static final int SINGLE_MAX_DECIMAL_DIGITS = 7;
	static final int SINGLE_AMX_DECIMAL_EXPONENT = 38;
	static final int SINGLE_MIN_DECIMAL_EXPONENT = -45;

	static final int INT_DECIMAL_DIGITS = 9;
	boolean isExceptional;
	boolean isNegative;
	int decExponent;
	char digits[];
	int nDigits;
	int bigIntExp;
	int bigIntNBits;
	boolean mustSetRoundDir = false;
	boolean fromHex = false;
	int roundDir = 0; // set by doubleValue

	private FastFloatingDecimal(boolean negSign, int decExponent, char[] digits, int n, boolean e) {
		isNegative = negSign;
		isExceptional = e;
		this.decExponent = decExponent;
		this.digits = digits;
		this.nDigits = n;
	}

	private static int countBits(long v) {
		if (v == 0L)
			return 0;

		while ((v & HIGH_BYTE) == 0L) {
			v <<= 8;
		}
		while (v > 0L) { // i.e. while ((v&highbit) == 0L )
			v <<= 1;
		}

		int n = 0;
		while ((v & LOW_BYTES) != 0L) {
			v <<= 8;
			n += 8;
		}
		while (v != 0L) {
			v <<= 1;
			n += 1;
		}
		return n;
	}

	private FDBigInt b5p[];

	private FDBigInt big5pow(int p) {
		assert p >= 0 : p;
		if (b5p == null) {
			b5p = new FDBigInt[p + 1];
		} else if (b5p.length <= p) {
			FDBigInt t[] = new FDBigInt[p + 1];
			System.arraycopy(b5p, 0, t, 0, b5p.length);
			b5p = t;
		}
		if (b5p[p] != null)
			return b5p[p];
		else if (p < small5pow.length)
			return b5p[p] = new FDBigInt(small5pow[p]);
		else if (p < long5pow.length)
			return b5p[p] = new FDBigInt(long5pow[p]);
		else {
			int q, r;
			q = p >> 1;
			r = p - q;
			FDBigInt bigq = b5p[q];
			if (bigq == null)
				bigq = big5pow(q);
			if (r < small5pow.length) {
				return (b5p[p] = bigq.mult(small5pow[r]));
			} else {
				FDBigInt bigr = b5p[r];
				if (bigr == null)
					bigr = big5pow(r);
				return (b5p[p] = bigq.mult(bigr));
			}
		}
	}

	private FDBigInt multPow52(FDBigInt v, int p5, int p2) {
		if (p5 != 0) {
			if (p5 < small5pow.length) {
				v = v.mult(small5pow[p5]);
			} else {
				v = v.mult(big5pow(p5));
			}
		}
		if (p2 != 0) {
			v.lshiftMe(p2);
		}
		return v;
	}

	private FDBigInt constructPow52(int p5, int p2) {
		FDBigInt v = new FDBigInt(big5pow(p5));
		if (p2 != 0) {
			v.lshiftMe(p2);
		}
		return v;
	}

	private FDBigInt doubleToBigInt(double dval) {
		long lbits = Double.doubleToLongBits(dval) & ~SIGN_MASK;
		int binexp = (int) (lbits >>> EXP_SHIFT);
		lbits &= FRACT_MASK;
		if (binexp > 0) {
			lbits |= FRACT_HOB;
		} else {
			assert lbits != 0L : lbits; // doubleToBigInt(0.0)
			binexp += 1;
			while ((lbits & FRACT_HOB) == 0L) {
				lbits <<= 1;
				binexp -= 1;
			}
		}
		binexp -= EXP_BIAS;
		int nbits = countBits(lbits);
		/*
		 * We now know where the high-order 1 bit is,
		 * and we know how many there are.
		 */
		int lowOrderZeros = EXP_SHIFT + 1 - nbits;
		lbits >>>= lowOrderZeros;

		bigIntExp = binexp + 1 - nbits;
		bigIntNBits = nbits;
		return new FDBigInt(lbits);
	}

	private static double ulp(double dval, boolean subtracting) {
		long lbits = Double.doubleToLongBits(dval) & ~SIGN_MASK;
		int binexp = (int) (lbits >>> EXP_SHIFT);
		double ulpval;
		if (subtracting && (binexp >= EXP_SHIFT) && ((lbits & FRACT_MASK) == 0L)) {
			binexp -= 1;
		}
		if (binexp > EXP_SHIFT) {
			ulpval = Double.longBitsToDouble(((long) (binexp - EXP_SHIFT)) << EXP_SHIFT);
		} else if (binexp == 0) {
			ulpval = Double.MIN_VALUE;
		} else {
			ulpval = Double.longBitsToDouble(1L << (binexp - 1));
		}
		if (subtracting)
			ulpval = -ulpval;

		return ulpval;
	}

	float stickyRound(double dval) {
		long lbits = Double.doubleToLongBits(dval);
		long binexp = lbits & EXP_MASK;
		if (binexp == 0L || binexp == EXP_MASK) {
			return (float) dval;
		}
		lbits += (long) roundDir; // hack-o-matic.
		return (float) Double.longBitsToDouble(lbits);
	}

	private void developLongDigits(int decExponent, long lvalue, long insignificant) {
		char digits[];
		int ndigits;
		int digitno;
		int c;
		//
		// Discard non-significant low-order bits, while rounding,
		// up to insignificant value.
		int i;
		for (i = 0; insignificant >= 10L; i++)
			insignificant /= 10L;
		if (i != 0) {
			long pow10 = long5pow[i] << i; // 10^i == 5^i * 2^i;
			long residue = lvalue % pow10;
			lvalue /= pow10;
			decExponent += i;
			if (residue >= (pow10 >> 1)) {
				// round up based on the low-order bits we're discarding
				lvalue++;
			}
		}
		if (lvalue <= Integer.MAX_VALUE) {
			assert lvalue > 0L : lvalue; // lvalue <= 0
			// even easier subcase!
			// can do int arithmetic rather than long!
			int ivalue = (int) lvalue;
			ndigits = 10;
			digits = (char[]) (perThreadBuffer.get());
			digitno = ndigits - 1;
			c = ivalue % 10;
			ivalue /= 10;
			while (c == 0) {
				decExponent++;
				c = ivalue % 10;
				ivalue /= 10;
			}
			while (ivalue != 0) {
				digits[digitno--] = (char) (c + '0');
				decExponent++;
				c = ivalue % 10;
				ivalue /= 10;
			}
			digits[digitno] = (char) (c + '0');
		} else {
			// same algorithm as above (same bugs, too )
			// but using long arithmetic.
			ndigits = 20;
			digits = (char[]) (perThreadBuffer.get());
			digitno = ndigits - 1;
			c = (int) (lvalue % 10L);
			lvalue /= 10L;
			while (c == 0) {
				decExponent++;
				c = (int) (lvalue % 10L);
				lvalue /= 10L;
			}
			while (lvalue != 0L) {
				digits[digitno--] = (char) (c + '0');
				decExponent++;
				c = (int) (lvalue % 10L);
				lvalue /= 10;
			}
			digits[digitno] = (char) (c + '0');
		}
		char result[];
		ndigits -= digitno;
		result = new char[ndigits];
		System.arraycopy(digits, digitno, result, 0, ndigits);
		this.digits = result;
		this.decExponent = decExponent + 1;
		this.nDigits = ndigits;
	}

	private void roundup() {
		int i;
		int q = digits[i = (nDigits - 1)];
		if (q == '9') {
			while (q == '9' && i > 0) {
				digits[i] = '0';
				q = digits[--i];
			}
			if (q == '9') {
				decExponent += 1;
				digits[0] = '1';
				return;
			}
		}
		digits[i] = (char) (q + 1);
	}

	public FastFloatingDecimal(double d) {
		long dBits = Double.doubleToLongBits(d);
		long fractBits;
		int binExp;
		int nSignificantBits;

		if ((dBits & SIGN_MASK) != 0) {
			isNegative = true;
			dBits ^= SIGN_MASK;
		} else {
			isNegative = false;
		}
		binExp = (int) ((dBits & EXP_MASK) >> EXP_SHIFT);
		fractBits = dBits & FRACT_MASK;
		if (binExp == (int) (EXP_MASK >> EXP_SHIFT)) {
			isExceptional = true;
			if (fractBits == 0L) {
				digits = infinity;
			} else {
				digits = notANumber;
				isNegative = false; // NaN has no sign!
			}
			nDigits = digits.length;
			return;
		}
		isExceptional = false;
		if (binExp == 0) {
			if (fractBits == 0L) {
				decExponent = 0;
				digits = zero;
				nDigits = 1;
				return;
			}
			while ((fractBits & FRACT_HOB) == 0L) {
				fractBits <<= 1;
				binExp -= 1;
			}
			nSignificantBits = EXP_SHIFT + binExp + 1; // recall binExp is  - shift count.
			binExp += 1;
		} else {
			fractBits |= FRACT_HOB;
			nSignificantBits = EXP_SHIFT + 1;
		}
		binExp -= EXP_BIAS;
		dtoa(binExp, fractBits, nSignificantBits);
	}

	public FastFloatingDecimal(float f) {
		int fBits = Float.floatToIntBits(f);
		int fractBits;
		int binExp;
		int nSignificantBits;

		if ((fBits & SINGLE_SIGN_MASK) != 0) {
			isNegative = true;
			fBits ^= SINGLE_SIGN_MASK;
		} else {
			isNegative = false;
		}
		binExp = (int) ((fBits & SINGLE_EXP_MASK) >> SINGLE_EXP_SHIFT);
		fractBits = fBits & SINGLE_FRACT_MASK;
		if (binExp == (int) (SINGLE_EXP_MASK >> SINGLE_EXP_SHIFT)) {
			isExceptional = true;
			if (fractBits == 0L) {
				digits = infinity;
			} else {
				digits = notANumber;
				isNegative = false; // NaN has no sign!
			}
			nDigits = digits.length;
			return;
		}
		isExceptional = false;
		if (binExp == 0) {
			if (fractBits == 0) {
				decExponent = 0;
				digits = zero;
				nDigits = 1;
				return;
			}
			while ((fractBits & SINGLE_FRACT_HDB) == 0) {
				fractBits <<= 1;
				binExp -= 1;
			}
			nSignificantBits = SINGLE_EXP_SHIFT + binExp + 1; // recall binExp is  - shift count.
			binExp += 1;
		} else {
			fractBits |= SINGLE_FRACT_HDB;
			nSignificantBits = SINGLE_EXP_SHIFT + 1;
		}
		binExp -= SINGLE_EXP_BIAS;
		dtoa(binExp, ((long) fractBits) << (EXP_SHIFT - SINGLE_EXP_SHIFT), nSignificantBits);
	}

	private void dtoa(int binExp, long fractBits, int nSignificantBits) {
		int nFractBits; // number of significant bits of fractBits;
		int nTinyBits; // number of these to the right of the point.
		int decExp;

		nFractBits = countBits(fractBits);
		nTinyBits = Math.max(0, nFractBits - binExp - 1);
		if (binExp <= MAX_SMALL_BIN_EXP && binExp >= MIN_SMALL_BIN_EXP) {
			if ((nTinyBits < long5pow.length) && ((nFractBits + n5bits[nTinyBits]) < 64)) {
				long halfULP;
				if (nTinyBits == 0) {
					if (binExp > nSignificantBits) {
						halfULP = 1L << (binExp - nSignificantBits - 1);
					} else {
						halfULP = 0L;
					}
					if (binExp >= EXP_SHIFT) {
						fractBits <<= (binExp - EXP_SHIFT);
					} else {
						fractBits >>>= (EXP_SHIFT - binExp);
					}
					developLongDigits(0, fractBits, halfULP);
					return;
				}
			}
		}
		double d2 = Double.longBitsToDouble(EXP_ONE | (fractBits & ~FRACT_HOB));
		decExp = (int) Math.floor((d2 - 1.5D) * 0.289529654D + 0.176091259 + (double) binExp * 0.301029995663981);
		int B2, B5; // powers of 2 and powers of 5, respectively, in B
		int S2, S5; // powers of 2 and powers of 5, respectively, in S
		int M2, M5; // powers of 2 and powers of 5, respectively, in M
		int Bbits; // binary digits needed to represent B, approx.
		int tenSbits; // binary digits needed to represent 10*S, approx.
		FDBigInt Sval, Bval, Mval;

		B5 = Math.max(0, -decExp);
		B2 = B5 + nTinyBits + binExp;

		S5 = Math.max(0, decExp);
		S2 = S5 + nTinyBits;

		M5 = B5;
		M2 = B2 - nSignificantBits;
		fractBits >>>= (EXP_SHIFT + 1 - nFractBits);
		B2 -= nFractBits - 1;
		int common2factor = Math.min(B2, S2);
		B2 -= common2factor;
		S2 -= common2factor;
		M2 -= common2factor;
		if (nFractBits == 1)
			M2 -= 1;
		if (M2 < 0) {
			B2 -= M2;
			S2 -= M2;
			M2 = 0;
		}
		char digits[] = this.digits = new char[18];
		int ndigit = 0;
		boolean low, high;
		long lowDigitDifference;
		int q;
		Bbits = nFractBits + B2 + ((B5 < n5bits.length) ? n5bits[B5] : (B5 * 3));
		tenSbits = S2 + 1 + (((S5 + 1) < n5bits.length) ? n5bits[(S5 + 1)] : ((S5 + 1) * 3));
		if (Bbits < 64 && tenSbits < 64) {
			if (Bbits < 32 && tenSbits < 32) {
				// wa-hoo! They're all ints!
				int b = ((int) fractBits * small5pow[B5]) << B2;
				int s = small5pow[S5] << S2;
				int m = small5pow[M5] << M2;
				int tens = s * 10;
				ndigit = 0;
				q = b / s;
				b = 10 * (b % s);
				m *= 10;
				low = (b < m);
				high = (b + m > tens);
				assert q < 10 : q; // excessively large digit
				if ((q == 0) && !high) {
					// oops. Usually ignore leading zero.
					decExp--;
				} else {
					digits[ndigit++] = (char) ('0' + q);
				}
				if (decExp < -3 || decExp >= 8) {
					high = low = false;
				}
				while (!low && !high) {
					q = b / s;
					b = 10 * (b % s);
					m *= 10;
					assert q < 10 : q; // excessively large digit
					if (m > 0L) {
						low = (b < m);
						high = (b + m > tens);
					} else {
						low = true;
						high = true;
					}
					digits[ndigit++] = (char) ('0' + q);
				}
				lowDigitDifference = (b << 1) - tens;
			} else {
				// still good! they're all longs!
				long b = (fractBits * long5pow[B5]) << B2;
				long s = long5pow[S5] << S2;
				long m = long5pow[M5] << M2;
				long tens = s * 10L;
				ndigit = 0;
				q = (int) (b / s);
				b = 10L * (b % s);
				m *= 10L;
				low = (b < m);
				high = (b + m > tens);
				assert q < 10 : q; // excessively large digit
				if ((q == 0) && !high) {
					// oops. Usually ignore leading zero.
					decExp--;
				} else {
					digits[ndigit++] = (char) ('0' + q);
				}
				if (decExp < -3 || decExp >= 8) {
					high = low = false;
				}
				while (!low && !high) {
					q = (int) (b / s);
					b = 10 * (b % s);
					m *= 10;
					assert q < 10 : q; // excessively large digit
					if (m > 0L) {
						low = (b < m);
						high = (b + m > tens);
					} else {
						low = true;
						high = true;
					}
					digits[ndigit++] = (char) ('0' + q);
				}
				lowDigitDifference = (b << 1) - tens;
			}
		} else {
			FDBigInt tenSval;
			int shiftBias;

			Bval = multPow52(new FDBigInt(fractBits), B5, B2);
			Sval = constructPow52(S5, S2);
			Mval = constructPow52(M5, M2);

			Bval.lshiftMe(shiftBias = Sval.normalizeMe());
			Mval.lshiftMe(shiftBias);
			tenSval = Sval.mult(10);
			ndigit = 0;
			q = Bval.quoRemIteration(Sval);
			Mval = Mval.mult(10);
			low = (Bval.cmp(Mval) < 0);
			high = (Bval.add(Mval).cmp(tenSval) > 0);
			assert q < 10 : q; // excessively large digit
			if ((q == 0) && !high) {
				// oops. Usually ignore leading zero.
				decExp--;
			} else {
				digits[ndigit++] = (char) ('0' + q);
			}
			if (decExp < -3 || decExp >= 8) {
				high = low = false;
			}
			while (!low && !high) {
				q = Bval.quoRemIteration(Sval);
				Mval = Mval.mult(10);
				assert q < 10 : q; // excessively large digit
				low = (Bval.cmp(Mval) < 0);
				high = (Bval.add(Mval).cmp(tenSval) > 0);
				digits[ndigit++] = (char) ('0' + q);
			}
			if (high && low) {
				Bval.lshiftMe(1);
				lowDigitDifference = Bval.cmp(tenSval);
			} else
				lowDigitDifference = 0L; // this here only for flow analysis!
		}
		this.decExponent = decExp + 1;
		this.digits = digits;
		this.nDigits = ndigit;
		if (high) {
			if (low) {
				if (lowDigitDifference == 0L) {
					if ((digits[nDigits - 1] & 1) != 0)
						roundup();
				} else if (lowDigitDifference > 0) {
					roundup();
				}
			} else {
				roundup();
			}
		}
	}

	public String toString() {
		StringBuilder result = new StringBuilder(nDigits + 8);
		if (isNegative) {
			result.append('-');
		}
		if (isExceptional) {
			result.append(digits, 0, nDigits);
		} else {
			result.append("0.");
			result.append(digits, 0, nDigits);
			result.append('e');
			result.append(decExponent);
		}
		return new String(result);
	}

	static public String toJavaFormatString(float f) {
		if (f == 0)
			return STRING_ZERO;
		else if (f != f)
			return STRING_NAN;
		else if (f == Float.POSITIVE_INFINITY)
			return STRING_POSITIVE_INFINITY;
		else if (f == Float.NEGATIVE_INFINITY)
			return STRING_NEGATIVE_INFINITY;
		return new FastFloatingDecimal(f).toJavaFormatString();
	}
	static public String toJavaFormatString(double d) {
		if (d == 0)
			return STRING_ZERO;
		else if (d != d)
			return STRING_NAN;
		else if (d == Double.POSITIVE_INFINITY)
			return STRING_POSITIVE_INFINITY;
		else if (d == Double.NEGATIVE_INFINITY)
			return STRING_NEGATIVE_INFINITY;
		return new FastFloatingDecimal(d).toJavaFormatString();
	}
	static public StringBuilder toJavaFormatString(float f, StringBuilder sink) {
		if (f == 0)
			sink.append(STRING_ZERO);
		else
			new FastFloatingDecimal(f).toJavaFormatString(sink);
		return sink;
	}
	static public StringBuilder toJavaFormatString(double d, StringBuilder sink) {
		if (d == 0)
			sink.append(STRING_ZERO);
		else
			new FastFloatingDecimal(d).toJavaFormatString(sink);
		return sink;
	}
	public String toJavaFormatString() {
		char result[] = (char[]) (perThreadBuffer.get());
		int i = getChars(result);
		return new String(result, 0, i);
	}
	public void toJavaFormatString(StringBuilder sink) {
		getChars(sink);
	}

	private void getChars(StringBuilder result) {
		if (isNegative) {
			result.append('-');
		}
		if (isExceptional) {
			result.append(digits, 0, nDigits);
		} else {
			if (decExponent > 0 && decExponent < 8) {
				int charLength = Math.min(nDigits, decExponent);
				result.append(digits, 0, charLength);
				if (charLength < decExponent) {
					charLength = decExponent - charLength;
					result.append(zero, 0, charLength);
					result.append('.');
					result.append('0');
				} else {
					result.append('.');
					if (charLength < nDigits) {
						int t = nDigits - charLength;
						result.append(digits, charLength, t);
					} else {
						result.append('0');
					}
				}
			} else if (decExponent <= 0 && decExponent > -3) {
				result.append('0');
				result.append('.');
				if (decExponent != 0) {
					result.append(zero, 0, -decExponent);
				}
				result.append(digits, 0, nDigits);
			} else {
				result.append(digits[0]);
				result.append('.');
				if (nDigits > 1) {
					result.append(digits, 1, nDigits - 1);
				} else {
					result.append('0');
				}
				result.append('E');
				int e;
				if (decExponent <= 0) {
					result.append('-');
					e = -decExponent + 1;
				} else {
					e = decExponent - 1;
				}
				// decExponent has 1, 2, or 3, digits
				if (e <= 9) {
					result.append((char) (e + '0'));
				} else if (e <= 99) {
					result.append((char) (e / 10 + '0'));
					result.append((char) (e % 10 + '0'));
				} else {
					result.append((char) (e / 100 + '0'));
					e %= 100;
					result.append((char) (e / 10 + '0'));
					result.append((char) (e % 10 + '0'));
				}
			}
		}
	}
	private int getChars(char[] result) {
		int i = 0;
		if (isNegative) {
			result[0] = '-';
			i = 1;
		}
		if (isExceptional) {
			System.arraycopy(digits, 0, result, i, nDigits);
			i += nDigits;
		} else {
			if (decExponent > 0 && decExponent < 8) {
				// print digits.digits.
				int charLength = Math.min(nDigits, decExponent);
				System.arraycopy(digits, 0, result, i, charLength);
				i += charLength;
				if (charLength < decExponent) {
					charLength = decExponent - charLength;
					System.arraycopy(zero, 0, result, i, charLength);
					i += charLength;
					result[i++] = '.';
					result[i++] = '0';
				} else {
					result[i++] = '.';
					if (charLength < nDigits) {
						int t = nDigits - charLength;
						System.arraycopy(digits, charLength, result, i, t);
						i += t;
					} else {
						result[i++] = '0';
					}
				}
			} else if (decExponent <= 0 && decExponent > -3) {
				result[i++] = '0';
				result[i++] = '.';
				if (decExponent != 0) {
					System.arraycopy(zero, 0, result, i, -decExponent);
					i -= decExponent;
				}
				System.arraycopy(digits, 0, result, i, nDigits);
				i += nDigits;
			} else {
				result[i++] = digits[0];
				result[i++] = '.';
				if (nDigits > 1) {
					System.arraycopy(digits, 1, result, i, nDigits - 1);
					i += nDigits - 1;
				} else {
					result[i++] = '0';
				}
				result[i++] = 'E';
				int e;
				if (decExponent <= 0) {
					result[i++] = '-';
					e = -decExponent + 1;
				} else {
					e = decExponent - 1;
				}
				// decExponent has 1, 2, or 3, digits
				if (e <= 9) {
					result[i++] = (char) (e + '0');
				} else if (e <= 99) {
					result[i++] = (char) (e / 10 + '0');
					result[i++] = (char) (e % 10 + '0');
				} else {
					result[i++] = (char) (e / 100 + '0');
					e %= 100;
					result[i++] = (char) (e / 10 + '0');
					result[i++] = (char) (e % 10 + '0');
				}
			}
		}
		return i;
	}

	// Per-thread buffer for string/stringbuffer conversion
	private static ThreadLocal perThreadBuffer = new ThreadLocal() {
		protected synchronized Object initialValue() {
			return new char[26];
		}
	};

	public void appendTo(Appendable buf) {
		char result[] = (char[]) (perThreadBuffer.get());
		int i = getChars(result);
		if (buf instanceof StringBuilder)
			((StringBuilder) buf).append(result, 0, i);
		else if (buf instanceof StringBuffer)
			((StringBuffer) buf).append(result, 0, i);
		else
			assert false;
	}

	public static FastFloatingDecimal readJavaFormatString(CharSequence cs, int start, int end) throws NumberFormatException {
		boolean isNegative = false;
		boolean signSeen = false;
		int decExp;
		char c;

		parseNumber: try {
			if (end <= start)
				throw new NumberFormatException("empty String");
			int l = end;
			int i = start;
			switch (c = cs.charAt(i)) {
				case '-':
					isNegative = true;
					//FALLTHROUGH
				case '+':
					i++;
					signSeen = true;
			}

			// Check for NaN and Infinity strings
			c = cs.charAt(i);
			if (c == 'N' || c == 'I') { // possible NaN or infinity
				boolean potentialNaN = false;
				char targetChars[] = null; // char array of "NaN" or "Infinity"

				if (c == 'N') {
					targetChars = notANumber;
					potentialNaN = true;
				} else {
					targetChars = infinity;
				}

				// compare Input string to "NaN" or "Infinity"
				int j = 0;
				while (i < l && j < targetChars.length) {
					if (cs.charAt(i) == targetChars[j]) {
						i++;
						j++;
					} else
						// something is amiss, throw exception
						break parseNumber;
				}

				// For the candidate string to be a NaN or infinity,
				// all characters in input string and target char[]
				// must be matched ==> j must equal targetChars.length
				// and i must equal l
				if ((j == targetChars.length) && (i == l)) { // return NaN or infinity
					return (potentialNaN ? new FastFloatingDecimal(Double.NaN) // NaN has no sign
							: new FastFloatingDecimal(isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY));
				} else { // something went wrong, throw exception
					break parseNumber;
				}

			} else if (c == '0') { // check for hexadecimal floating-point number
				if (l > i + 1) {
					char ch = cs.charAt(i + 1);
					if (ch == 'x' || ch == 'X') // possible hex string
						return parseHexString(cs, start, end);
				}
			} // look for and process decimal floating-point string

			char[] digits = new char[end - start];
			int nDigits = 0;
			boolean decSeen = false;
			int decPt = 0;
			int nLeadZero = 0;
			int nTrailZero = 0;
			digitLoop: while (i < l) {
				switch (c = cs.charAt(i)) {
					case '0':
						if (nDigits > 0) {
							nTrailZero += 1;
						} else {
							nLeadZero += 1;
						}
						break; // out of switch.
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						while (nTrailZero > 0) {
							digits[nDigits++] = '0';
							nTrailZero -= 1;
						}
						digits[nDigits++] = c;
						break; // out of switch.
					case '.':
						if (decSeen) {
							// already saw one ., this is the 2nd.
							throw new NumberFormatException("multiple points: " + cs.subSequence(start, end));
						}
						decPt = i - start;
						if (signSeen) {
							decPt -= 1;
						}
						decSeen = true;
						break; // out of switch.
					default:
						break digitLoop;
				}
				i++;
			}
			if (nDigits == 0) {
				digits = zero;
				nDigits = 1;
				if (nLeadZero == 0) {
					break parseNumber; // go throw exception
				}

			}

			if (decSeen) {
				decExp = decPt - nLeadZero;
			} else {
				decExp = nDigits + nTrailZero;
			}

			/*
			 * Look for 'e' or 'E' and an optionally signed integer.
			 */
			if ((i < l) && (((c = cs.charAt(i)) == 'e') || (c == 'E'))) {
				int expSign = 1;
				int expVal = 0;
				int reallyBig = Integer.MAX_VALUE / 10;
				boolean expOverflow = false;
				switch (cs.charAt(++i)) {
					case '-':
						expSign = -1;
						//FALLTHROUGH
					case '+':
						i++;
				}
				int expAt = i;
				expLoop: while (i < l) {
					if (expVal >= reallyBig) {
						// the next character will cause integer
						// overflow.
						expOverflow = true;
					}
					switch (c = cs.charAt(i++)) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							expVal = expVal * 10 + ((int) c - (int) '0');
							continue;
						default:
							i--; // back up.
							break expLoop; // stop parsing exponent.
					}
				}
				int expLimit = BIG_DECIMAL_EXPONENT + nDigits + nTrailZero;
				if (expOverflow || (expVal > expLimit)) {
					decExp = expSign * expLimit;
				} else {
					decExp = decExp + expSign * expVal;
				}

				if (i == expAt)
					break parseNumber; // certainly bad
			}
			if (i < l && ((i != l - 1) || (cs.charAt(i) != 'f' && cs.charAt(i) != 'F' && cs.charAt(i) != 'd' && cs.charAt(i) != 'D'))) {
				break parseNumber; // go throw exception
			}

			return new FastFloatingDecimal(isNegative, decExp, digits, nDigits, false);
		} catch (StringIndexOutOfBoundsException e) {
		}
		throw new NumberFormatException("For input string: \"" + cs.subSequence(start, end) + "\"");
	}

	public double doubleValue() {
		int kDigits = Math.min(nDigits, MAX_DECIMAL_DIGITS + 1);
		long lValue;
		double dValue;
		double rValue, tValue;

		// First, check for NaN and Infinity values
		if (digits == infinity || digits == notANumber) {
			if (digits == notANumber)
				return Double.NaN;
			else
				return (isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
		} else {
			if (mustSetRoundDir) {
				roundDir = 0;
			}
			/*
			 * convert the lead kDigits to a long integer.
			 */
			// (special performance hack: start to do it using int)
			int iValue = (int) digits[0] - (int) '0';
			int iDigits = Math.min(kDigits, INT_DECIMAL_DIGITS);
			for (int i = 1; i < iDigits; i++) {
				iValue = iValue * 10 + (int) digits[i] - (int) '0';
			}
			lValue = (long) iValue;
			for (int i = iDigits; i < kDigits; i++) {
				lValue = lValue * 10L + (long) ((int) digits[i] - (int) '0');
			}
			dValue = (double) lValue;
			int exp = decExponent - kDigits;
			/*
			 * lValue now contains a long integer with the value of
			 * the first kDigits digits of the number.
			 * dValue contains the (double) of the same.
			 */

			if (nDigits <= MAX_DECIMAL_DIGITS) {
				/*
				 * possibly an easy case.
				 * We know that the digits can be represented
				 * exactly. And if the exponent isn't too outrageous,
				 * the whole thing can be done with one operation,
				 * thus one rounding error.
				 * Note that all our constructors trim all leading and
				 * trailing zeros, so simple values (including zero)
				 * will always end up here
				 */
				if (exp == 0 || dValue == 0.0)
					return (isNegative) ? -dValue : dValue; // small floating integer
				else if (exp >= 0) {
					if (exp <= maxSmallTen) {
						/*
						 * Can get the answer with one operation,
						 * thus one roundoff.
						 */
						rValue = dValue * small10pow[exp];
						if (mustSetRoundDir) {
							tValue = rValue / small10pow[exp];
							roundDir = (tValue == dValue) ? 0 : (tValue < dValue) ? 1 : -1;
						}
						return (isNegative) ? -rValue : rValue;
					}
					int slop = MAX_DECIMAL_DIGITS - kDigits;
					if (exp <= maxSmallTen + slop) {
						/*
						 * We can multiply dValue by 10^(slop)
						 * and it is still "small" and exact.
						 * Then we can multiply by 10^(exp-slop)
						 * with one rounding.
						 */
						dValue *= small10pow[slop];
						rValue = dValue * small10pow[exp - slop];

						if (mustSetRoundDir) {
							tValue = rValue / small10pow[exp - slop];
							roundDir = (tValue == dValue) ? 0 : (tValue < dValue) ? 1 : -1;
						}
						return (isNegative) ? -rValue : rValue;
					}
					/*
					 * Else we have a hard case with a positive exp.
					 */
				} else {
					if (exp >= -maxSmallTen) {
						/*
						 * Can get the answer in one division.
						 */
						rValue = dValue / small10pow[-exp];
						tValue = rValue * small10pow[-exp];
						if (mustSetRoundDir) {
							roundDir = (tValue == dValue) ? 0 : (tValue < dValue) ? 1 : -1;
						}
						return (isNegative) ? -rValue : rValue;
					}
					/*
					 * Else we have a hard case with a negative exp.
					 */
				}
			}

			/*
			 * Harder cases:
			 * The sum of digits plus exponent is greater than
			 * what we think we can do with one error.
			 *
			 * Start by approximating the right answer by,
			 * naively, scaling by powers of 10.
			 */
			if (exp > 0) {
				if (decExponent > MAX_DECIMAL_EXPONENT + 1) {
					/*
					 * Lets face it. This is going to be
					 * Infinity. Cut to the chase.
					 */
					return (isNegative) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
				}
				if ((exp & 15) != 0) {
					dValue *= small10pow[exp & 15];
				}
				if ((exp >>= 4) != 0) {
					int j;
					for (j = 0; exp > 1; j++, exp >>= 1) {
						if ((exp & 1) != 0)
							dValue *= big10pow[j];
					}
					/*
					 * The reason for the weird exp > 1 condition
					 * in the above loop was so that the last multiply
					 * would get unrolled. We handle it here.
					 * It could overflow.
					 */
					double t = dValue * big10pow[j];
					if (Double.isInfinite(t)) {
						/*
						 * It did overflow.
						 * Look more closely at the result.
						 * If the exponent is just one too large,
						 * then use the maximum finite as our estimate
						 * value. Else call the result infinity
						 * and punt it.
						 * ( I presume this could happen because
						 * rounding forces the result here to be
						 * an ULP or two larger than
						 * Double.MAX_VALUE ).
						 */
						t = dValue / 2.0;
						t *= big10pow[j];
						if (Double.isInfinite(t)) {
							return (isNegative) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
						}
						t = Double.MAX_VALUE;
					}
					dValue = t;
				}
			} else if (exp < 0) {
				exp = -exp;
				if (decExponent < MIN_DECIMAL_EXPONENT - 1) {
					/*
					 * Lets face it. This is going to be
					 * zero. Cut to the chase.
					 */
					return (isNegative) ? -0.0 : 0.0;
				}
				if ((exp & 15) != 0) {
					dValue /= small10pow[exp & 15];
				}
				if ((exp >>= 4) != 0) {
					int j;
					for (j = 0; exp > 1; j++, exp >>= 1) {
						if ((exp & 1) != 0)
							dValue *= tiny10pow[j];
					}
					/*
					 * The reason for the weird exp > 1 condition
					 * in the above loop was so that the last multiply
					 * would get unrolled. We handle it here.
					 * It could underflow.
					 */
					double t = dValue * tiny10pow[j];
					if (t == 0.0) {
						/*
						 * It did underflow.
						 * Look more closely at the result.
						 * If the exponent is just one too small,
						 * then use the minimum finite as our estimate
						 * value. Else call the result 0.0
						 * and punt it.
						 * ( I presume this could happen because
						 * rounding forces the result here to be
						 * an ULP or two less than
						 * Double.MIN_VALUE ).
						 */
						t = dValue * 2.0;
						t *= tiny10pow[j];
						if (t == 0.0) {
							return (isNegative) ? -0.0 : 0.0;
						}
						t = Double.MIN_VALUE;
					}
					dValue = t;
				}
			}

			/*
			 * dValue is now approximately the result.
			 * The hard part is adjusting it, by comparison
			 * with FDBigInt arithmetic.
			 * Formulate the EXACT big-number result as
			 * bigD0 * 10^exp
			 */
			FDBigInt bigD0 = new FDBigInt(lValue, digits, kDigits, nDigits);
			exp = decExponent - nDigits;

			correctionLoop: while (true) {
				/* AS A SIDE EFFECT, THIS METHOD WILL SET THE INSTANCE VARIABLES
				 * bigIntExp and bigIntNBits
				 */
				FDBigInt bigB = doubleToBigInt(dValue);

				/*
				 * Scale bigD, bigB appropriately for
				 * big-integer operations.
				 * Naively, we multiply by powers of ten
				 * and powers of two. What we actually do
				 * is keep track of the powers of 5 and
				 * powers of 2 we would use, then factor out
				 * common divisors before doing the work.
				 */
				int B2, B5; // powers of 2, 5 in bigB
				int D2, D5; // powers of 2, 5 in bigD
				int Ulp2; // powers of 2 in halfUlp.
				if (exp >= 0) {
					B2 = B5 = 0;
					D2 = D5 = exp;
				} else {
					B2 = B5 = -exp;
					D2 = D5 = 0;
				}
				if (bigIntExp >= 0) {
					B2 += bigIntExp;
				} else {
					D2 -= bigIntExp;
				}
				Ulp2 = B2;
				// shift bigB and bigD left by a number s. t.
				// halfUlp is still an integer.
				int hulpbias;
				if (bigIntExp + bigIntNBits <= -EXP_BIAS + 1) {
					// This is going to be a denormalized number
					// (if not actually zero).
					// half an ULP is at 2^-(expBias+expShift+1)
					hulpbias = bigIntExp + EXP_BIAS + EXP_SHIFT;
				} else {
					hulpbias = EXP_SHIFT + 2 - bigIntNBits;
				}
				B2 += hulpbias;
				D2 += hulpbias;
				// if there are common factors of 2, we might just as well
				// factor them out, as they add nothing useful.
				int common2 = Math.min(B2, Math.min(D2, Ulp2));
				B2 -= common2;
				D2 -= common2;
				Ulp2 -= common2;
				// do multiplications by powers of 5 and 2
				bigB = multPow52(bigB, B5, B2);
				FDBigInt bigD = multPow52(new FDBigInt(bigD0), D5, D2);
				//
				// to recap:
				// bigB is the scaled-big-int version of our floating-point
				// candidate.
				// bigD is the scaled-big-int version of the exact value
				// as we understand it.
				// halfUlp is 1/2 an ulp of bigB, except for special cases
				// of exact powers of 2
				//
				// the plan is to compare bigB with bigD, and if the difference
				// is less than halfUlp, then we're satisfied. Otherwise,
				// use the ratio of difference to halfUlp to calculate a fudge
				// factor to add to the floating value, then go 'round again.
				//
				FDBigInt diff;
				int cmpResult;
				boolean overvalue;
				if ((cmpResult = bigB.cmp(bigD)) > 0) {
					overvalue = true; // our candidate is too big.
					diff = bigB.sub(bigD);
					if ((bigIntNBits == 1) && (bigIntExp > -EXP_BIAS + 1)) {
						// candidate is a normalized exact power of 2 and
						// is too big. We will be subtracting.
						// For our purposes, ulp is the ulp of the
						// next smaller range.
						Ulp2 -= 1;
						if (Ulp2 < 0) {
							// rats. Cannot de-scale ulp this far.
							// must scale diff in other direction.
							Ulp2 = 0;
							diff.lshiftMe(1);
						}
					}
				} else if (cmpResult < 0) {
					overvalue = false; // our candidate is too small.
					diff = bigD.sub(bigB);
				} else {
					// the candidate is exactly right!
					// this happens with surprising frequency
					break correctionLoop;
				}
				FDBigInt halfUlp = constructPow52(B5, Ulp2);
				if ((cmpResult = diff.cmp(halfUlp)) < 0) {
					// difference is small.
					// this is close enough
					if (mustSetRoundDir) {
						roundDir = overvalue ? -1 : 1;
					}
					break correctionLoop;
				} else if (cmpResult == 0) {
					// difference is exactly half an ULP
					// round to some other value maybe, then finish
					dValue += 0.5 * ulp(dValue, overvalue);
					// should check for bigIntNBits == 1 here??
					if (mustSetRoundDir) {
						roundDir = overvalue ? -1 : 1;
					}
					break correctionLoop;
				} else {
					// difference is non-trivial.
					// could scale addend by ratio of difference to
					// halfUlp here, if we bothered to compute that difference.
					// Most of the time ( I hope ) it is about 1 anyway.
					dValue += ulp(dValue, overvalue);
					if (dValue == 0.0 || dValue == Double.POSITIVE_INFINITY)
						break correctionLoop; // oops. Fell off end of range.
					continue; // try again.
				}

			}
			return (isNegative) ? -dValue : dValue;
		}
	}

	/*
	 * Take a FloatingDecimal, which we presumably just scanned in,
	 * and find out what its value is, as a float.
	 * This is distinct from doubleValue() to avoid the extremely
	 * unlikely case of a double rounding error, wherein the conversion
	 * to double has one rounding error, and the conversion of that double
	 * to a float has another rounding error, IN THE WRONG DIRECTION,
	 * ( because of the preference to a zero low-order bit ).
	 */

	public float floatValue() {
		int kDigits = Math.min(nDigits, SINGLE_MAX_DECIMAL_DIGITS + 1);
		int iValue;
		float fValue;

		// First, check for NaN and Infinity values
		if (digits == infinity || digits == notANumber) {
			if (digits == notANumber)
				return Float.NaN;
			else
				return (isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY);
		} else {
			/*
			 * convert the lead kDigits to an integer.
			 */
			iValue = (int) digits[0] - (int) '0';
			for (int i = 1; i < kDigits; i++) {
				iValue = iValue * 10 + (int) digits[i] - (int) '0';
			}
			fValue = (float) iValue;
			int exp = decExponent - kDigits;
			/*
			 * iValue now contains an integer with the value of
			 * the first kDigits digits of the number.
			 * fValue contains the (float) of the same.
			 */

			if (nDigits <= SINGLE_MAX_DECIMAL_DIGITS) {
				/*
				 * possibly an easy case.
				 * We know that the digits can be represented
				 * exactly. And if the exponent isn't too outrageous,
				 * the whole thing can be done with one operation,
				 * thus one rounding error.
				 * Note that all our constructors trim all leading and
				 * trailing zeros, so simple values (including zero)
				 * will always end up here.
				 */
				if (exp == 0 || fValue == 0.0f)
					return (isNegative) ? -fValue : fValue; // small floating integer
				else if (exp >= 0) {
					if (exp <= singleMaxSmallTen) {
						/*
						 * Can get the answer with one operation,
						 * thus one roundoff.
						 */
						fValue *= singleSmall10pow[exp];
						return (isNegative) ? -fValue : fValue;
					}
					int slop = SINGLE_MAX_DECIMAL_DIGITS - kDigits;
					if (exp <= singleMaxSmallTen + slop) {
						/*
						 * We can multiply dValue by 10^(slop)
						 * and it is still "small" and exact.
						 * Then we can multiply by 10^(exp-slop)
						 * with one rounding.
						 */
						fValue *= singleSmall10pow[slop];
						fValue *= singleSmall10pow[exp - slop];
						return (isNegative) ? -fValue : fValue;
					}
					/*
					 * Else we have a hard case with a positive exp.
					 */
				} else {
					if (exp >= -singleMaxSmallTen) {
						/*
						 * Can get the answer in one division.
						 */
						fValue /= singleSmall10pow[-exp];
						return (isNegative) ? -fValue : fValue;
					}
					/*
					 * Else we have a hard case with a negative exp.
					 */
				}
			} else if ((decExponent >= nDigits) && (nDigits + decExponent <= MAX_DECIMAL_DIGITS)) {
				/*
				 * In double-precision, this is an exact floating integer.
				 * So we can compute to double, then shorten to float
				 * with one round, and get the right answer.
				 *
				 * First, finish accumulating digits.
				 * Then convert that integer to a double, multiply
				 * by the appropriate power of ten, and convert to float.
				 */
				long lValue = (long) iValue;
				for (int i = kDigits; i < nDigits; i++) {
					lValue = lValue * 10L + (long) ((int) digits[i] - (int) '0');
				}
				double dValue = (double) lValue;
				exp = decExponent - nDigits;
				dValue *= small10pow[exp];
				fValue = (float) dValue;
				return (isNegative) ? -fValue : fValue;

			}
			/*
			 * Harder cases:
			 * The sum of digits plus exponent is greater than
			 * what we think we can do with one error.
			 *
			 * Start by weeding out obviously out-of-range
			 * results, then convert to double and go to
			 * common hard-case code.
			 */
			if (decExponent > SINGLE_AMX_DECIMAL_EXPONENT + 1) {
				/*
				 * Lets face it. This is going to be
				 * Infinity. Cut to the chase.
				 */
				return (isNegative) ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
			} else if (decExponent < SINGLE_MIN_DECIMAL_EXPONENT - 1) {
				/*
				 * Lets face it. This is going to be
				 * zero. Cut to the chase.
				 */
				return (isNegative) ? -0.0f : 0.0f;
			}

			/*
			 * Here, we do 'way too much work, but throwing away
			 * our partial results, and going and doing the whole
			 * thing as double, then throwing away half the bits that computes
			 * when we convert back to float.
			 *
			 * The alternative is to reproduce the whole multiple-precision
			 * algorithm for float precision, or to try to parameterize it
			 * for common usage. The former will take about 400 lines of code,
			 * and the latter I tried without success. Thus the semi-hack
			 * answer here.
			 */
			mustSetRoundDir = !fromHex;
			double dValue = doubleValue();
			return stickyRound(dValue);
		}
	}

	/*
	 * All the positive powers of 10 that can be
	 * represented exactly in double/float.
	 */
	private static final double small10pow[] = { 1.0e0, 1.0e1, 1.0e2, 1.0e3, 1.0e4, 1.0e5, 1.0e6, 1.0e7, 1.0e8, 1.0e9, 1.0e10, 1.0e11, 1.0e12, 1.0e13, 1.0e14, 1.0e15, 1.0e16,
			1.0e17, 1.0e18, 1.0e19, 1.0e20, 1.0e21, 1.0e22 };

	private static final float singleSmall10pow[] = { 1.0e0f, 1.0e1f, 1.0e2f, 1.0e3f, 1.0e4f, 1.0e5f, 1.0e6f, 1.0e7f, 1.0e8f, 1.0e9f, 1.0e10f };

	private static final double big10pow[] = { 1e16, 1e32, 1e64, 1e128, 1e256 };
	private static final double tiny10pow[] = { 1e-16, 1e-32, 1e-64, 1e-128, 1e-256 };

	private static final int maxSmallTen = small10pow.length - 1;
	private static final int singleMaxSmallTen = singleSmall10pow.length - 1;

	private static final int small5pow[] = { 1, 5, 5 * 5, 5 * 5 * 5, 5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 };

	private static final long long5pow[] = { 1L, 5L, 5L * 5, 5L * 5 * 5, 5L * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, 5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, };

	// approximately ceil( log2( long5pow[i] ) )
	private static final int n5bits[] = { 0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 24, 26, 28, 31, 33, 35, 38, 40, 42, 45, 47, 49, 52, 54, 56, 59, 61, };

	private static final char infinity[] = { 'I', 'n', 'f', 'i', 'n', 'i', 't', 'y' };
	private static final char notANumber[] = { 'N', 'a', 'N' };
	private static final char zero[] = { '0', '0', '0', '0', '0', '0', '0', '0' };

	/*
	 * Grammar is compatible with hexadecimal floating-point constants
	 * described in section 6.4.4.2 of the C99 specification.
	 */
	private static Pattern hexFloatPattern = Pattern.compile(
			//1           234                   56                7                   8      9
			"([-+])?0[xX](((\\p{XDigit}+)\\.?)|((\\p{XDigit}*)\\.(\\p{XDigit}+)))[pP]([-+])?(\\p{Digit}+)[fFdD]?");

	/*
	 * Convert string s to a suitable floating decimal; uses the
	 * double constructor and set the roundDir variable appropriately
	 * in case the value is later converted to a float.
	 */
	static FastFloatingDecimal parseHexString(CharSequence cs, int start, int end) {
		// Verify string is a member of the hexadecimal floating-point
		// string language.
		Matcher m = hexFloatPattern.matcher(cs.subSequence(start, end));
		boolean validInput = m.matches();

		if (!validInput) {
			// Input does not match pattern
			throw new NumberFormatException("For input string: \"" + cs.subSequence(start, end) + "\"");
		} else { // validInput
			/*
			 * We must isolate the sign, significand, and exponent
			 * fields.  The sign value is straightforward.  Since
			 * floating-point numbers are stored with a normalized
			 * representation, the significand and exponent are
			 * interrelated.
			 *
			 * After extracting the sign, we normalized the
			 * significand as a hexadecimal value, calculating an
			 * exponent adjust for any shifts made during
			 * normalization.  If the significand is zero, the
			 * exponent doesn't need to be examined since the output
			 * will be zero.
			 *
			 * Next the exponent in the input string is extracted.
			 * Afterwards, the significand is normalized as a *binary*
			 * value and the input value's normalized exponent can be
			 * computed.  The significand bits are copied into a
			 * double significand; if the string has more logical bits
			 * than can fit in a double, the extra bits affect the
			 * round and sticky bits which are used to round the final
			 * value.
			 */

			//  Extract significand sign
			String group1 = m.group(1);
			double sign = ((group1 == null) || group1.equals("+")) ? 1.0 : -1.0;

			//  Extract Significand magnitude
			/*
			 * Based on the form of the significand, calculate how the
			 * binary exponent needs to be adjusted to create a
			 * normalized *hexadecimal* floating-point number; that
			 * is, a number where there is one nonzero hex digit to
			 * the left of the (hexa)decimal point.  Since we are
			 * adjusting a binary, not hexadecimal exponent, the
			 * exponent is adjusted by a multiple of 4.
			 *
			 * There are a number of significand scenarios to consider;
			 * letters are used in indicate nonzero digits:
			 *
			 * 1. 000xxxx       =>      x.xxx   normalized
			 *    increase exponent by (number of x's - 1)*4
			 *
			 * 2. 000xxx.yyyy =>        x.xxyyyy        normalized
			 *    increase exponent by (number of x's - 1)*4
			 *
			 * 3. .000yyy  =>   y.yy    normalized
			 *    decrease exponent by (number of zeros + 1)*4
			 *
			 * 4. 000.00000yyy => y.yy normalized
			 *    decrease exponent by (number of zeros to right of point + 1)*4
			 *
			 * If the significand is exactly zero, return a properly
			 * signed zero.
			 */

			String significandString = null;
			int signifLength = 0;
			int exponentAdjust = 0;
			{
				int leftDigits = 0; // number of meaningful digits to
									// left of "decimal" point
									// (leading zeros stripped)
				int rightDigits = 0; // number of digits to right of
										// "decimal" point; leading zeros
										// must always be accounted for
										/*
										 * The significand is made up of either
										 *
										 * 1. group 4 entirely (integer portion only)
										 *
										 * OR
										 *
										 * 2. the fractional portion from group 7 plus any
										 * (optional) integer portions from group 6.
										 */
				String group4;
				if ((group4 = m.group(4)) != null) { // Integer-only significand
					// Leading zeros never matter on the integer portion
					significandString = stripLeadingZeros(group4);
					leftDigits = significandString.length();
				} else {
					// Group 6 is the optional integer; leading zeros
					// never matter on the integer portion
					String group6 = stripLeadingZeros(m.group(6));
					leftDigits = group6.length();

					// fraction
					String group7 = m.group(7);
					rightDigits = group7.length();

					// Turn "integer.fraction" into "integer"+"fraction"
					significandString = group6 + // is the null
					// check necessary?
							group7;
				}

				significandString = stripLeadingZeros(significandString);
				signifLength = significandString.length();

				/*
				 * Adjust exponent as described above
				 */
				if (leftDigits >= 1) { // Cases 1 and 2
					exponentAdjust = 4 * (leftDigits - 1);
				} else { // Cases 3 and 4
					exponentAdjust = -4 * (rightDigits - signifLength + 1);
				}

				// If the significand is zero, the exponent doesn't
				// matter; return a properly signed zero.

				if (signifLength == 0) { // Only zeros in input
					return new FastFloatingDecimal(sign * 0.0);
				}
			}

			//  Extract Exponent
			/*
			 * Use an int to read in the exponent value; this should
			 * provide more than sufficient range for non-contrived
			 * inputs.  If reading the exponent in as an int does
			 * overflow, examine the sign of the exponent and
			 * significand to determine what to do.
			 */
			String group8 = m.group(8);
			boolean positiveExponent = (group8 == null) || group8.equals("+");
			long unsignedRawExponent;
			try {
				unsignedRawExponent = Integer.parseInt(m.group(9));
			} catch (NumberFormatException e) {
				// At this point, we know the exponent is
				// syntactically well-formed as a sequence of
				// digits.  Therefore, if an NumberFormatException
				// is thrown, it must be due to overflowing int's
				// range.  Also, at this point, we have already
				// checked for a zero significand.  Thus the signs
				// of the exponent and significand determine the
				// final result:
				//
				//                      significand
				//                      +               -
				// exponent     +       +infinity       -infinity
				//              -       +0.0            -0.0
				return new FastFloatingDecimal(sign * (positiveExponent ? Double.POSITIVE_INFINITY : 0.0));
			}

			long rawExponent = (positiveExponent ? 1L : -1L) * // exponent sign
					unsignedRawExponent; // exponent magnitude

			// Calculate partially adjusted exponent
			long exponent = rawExponent + exponentAdjust;

			// Starting copying non-zero bits into proper position in
			// a long; copy explicit bit too; this will be masked
			// later for normal values.

			boolean round = false;
			boolean sticky = false;
			int bitsCopied = 0;
			int nextShift = 0;
			long significand = 0L;
			// First iteration is different, since we only copy
			// from the leading significand bit; one more exponent
			// adjust will be needed...

			// IMPORTANT: make leadingDigit a long to avoid
			// surprising shift semantics!
			long leadingDigit = getHexDigit(significandString, 0);

			/*
			 * Left shift the leading digit (53 - (bit position of
			 * leading 1 in digit)); this sets the top bit of the
			 * significand to 1.  The nextShift value is adjusted
			 * to take into account the number of bit positions of
			 * the leadingDigit actually used.  Finally, the
			 * exponent is adjusted to normalize the significand
			 * as a binary value, not just a hex value.
			 */
			if (leadingDigit == 1) {
				significand |= leadingDigit << 52;
				nextShift = 52 - 4;
				/* exponent += 0 */} else if (leadingDigit <= 3) { // [2, 3]
				significand |= leadingDigit << 51;
				nextShift = 52 - 5;
				exponent += 1;
			} else if (leadingDigit <= 7) { // [4, 7]
				significand |= leadingDigit << 50;
				nextShift = 52 - 6;
				exponent += 2;
			} else if (leadingDigit <= 15) { // [8, f]
				significand |= leadingDigit << 49;
				nextShift = 52 - 7;
				exponent += 3;
			} else {
				throw new AssertionError("Result from digit conversion too large!");
			}
			// The preceding if-else could be replaced by a single
			// code block based on the high-order bit set in
			// leadingDigit.  Given leadingOnePosition,

			// significand |= leadingDigit << (SIGNIFICAND_WIDTH - leadingOnePosition);
			// nextShift = 52 - (3 + leadingOnePosition);
			// exponent += (leadingOnePosition-1);

			/*
			 * Now the exponent variable is equal to the normalized
			 * binary exponent.  Code below will make representation
			 * adjustments if the exponent is incremented after
			 * rounding (includes overflows to infinity) or if the
			 * result is subnormal.
			 */

			// Copy digit into significand until the significand can't
			// hold another full hex digit or there are no more input
			// hex digits.
			int i = 0;
			for (i = 1; i < signifLength && nextShift >= 0; i++) {
				long currentDigit = getHexDigit(significandString, i);
				significand |= (currentDigit << nextShift);
				nextShift -= 4;
			}

			// After the above loop, the bulk of the string is copied.
			// Now, we must copy any partial hex digits into the
			// significand AND compute the round bit and start computing
			// sticky bit.

			if (i < signifLength) { // at least one hex input digit exists
				long currentDigit = getHexDigit(significandString, i);

				// from nextShift, figure out how many bits need
				// to be copied, if any
				switch (nextShift) { // must be negative
					case -1:
						// three bits need to be copied in; can
						// set round bit
						significand |= ((currentDigit & 0xEL) >> 1);
						round = (currentDigit & 0x1L) != 0L;
						break;

					case -2:
						// two bits need to be copied in; can
						// set round and start sticky
						significand |= ((currentDigit & 0xCL) >> 2);
						round = (currentDigit & 0x2L) != 0L;
						sticky = (currentDigit & 0x1L) != 0;
						break;

					case -3:
						// one bit needs to be copied in
						significand |= ((currentDigit & 0x8L) >> 3);
						// Now set round and start sticky, if possible
						round = (currentDigit & 0x4L) != 0L;
						sticky = (currentDigit & 0x3L) != 0;
						break;

					case -4:
						// all bits copied into significand; set
						// round and start sticky
						round = ((currentDigit & 0x8L) != 0); // is top bit set?
						// nonzeros in three low order bits?
						sticky = (currentDigit & 0x7L) != 0;
						break;

					default:
						throw new AssertionError("Unexpected shift distance remainder.");
					// break;
				}

				// Round is set; sticky might be set.

				// For the sticky bit, it suffices to check the
				// current digit and test for any nonzero digits in
				// the remaining unprocessed input.
				i++;
				while (i < signifLength && !sticky) {
					currentDigit = getHexDigit(significandString, i);
					sticky = sticky || (currentDigit != 0);
					i++;
				}

			}
			// else all of string was seen, round and sticky are
			// correct as false.

			// Check for overflow and update exponent accordingly.

			if (exponent > DoubleConsts.MAX_EXPONENT) { // Infinite result
				// overflow to properly signed infinity
				return new FastFloatingDecimal(sign * Double.POSITIVE_INFINITY);
			} else { // Finite return value
				if (exponent <= DoubleConsts.MAX_EXPONENT && // (Usually) normal result
						exponent >= DoubleConsts.MIN_EXPONENT) {

					// The result returned in this block cannot be a
					// zero or subnormal; however after the
					// significand is adjusted from rounding, we could
					// still overflow in infinity.

					// AND exponent bits into significand; if the
					// significand is incremented and overflows from
					// rounding, this combination will update the
					// exponent correctly, even in the case of
					// Double.MAX_VALUE overflowing to infinity.

					significand = ((((long) exponent + (long) DoubleConsts.EXP_BIAS) << (DoubleConsts.SIGNIFICAND_WIDTH - 1)) & DoubleConsts.EXP_BIT_MASK)
							| (DoubleConsts.SIGNIF_BIT_MASK & significand);

				} else { // Subnormal or zero
					// (exponent < DoubleConsts.MIN_EXPONENT)

					if (exponent < (DoubleConsts.MIN_SUB_EXPONENT - 1)) {
						// No way to round back to nonzero value
						// regardless of significand if the exponent is
						// less than -1075.
						return new FastFloatingDecimal(sign * 0.0);
					} else { //  -1075 <= exponent <= MIN_EXPONENT -1 = -1023
						/*
						 * Find bit position to round to; recompute
						 * round and sticky bits, and shift
						 * significand right appropriately.
						 */

						sticky = sticky || round;
						round = false;

						// Number of bits of significand to preserve is
						// exponent - abs_min_exp +1
						// check:
						// -1075 +1074 + 1 = 0
						// -1023 +1074 + 1 = 52

						int bitsDiscarded = 53 - ((int) exponent - DoubleConsts.MIN_SUB_EXPONENT + 1);
						assert bitsDiscarded >= 1 && bitsDiscarded <= 53;

						// What to do here:
						// First, isolate the new round bit
						round = (significand & (1L << (bitsDiscarded - 1))) != 0L;
						if (bitsDiscarded > 1) {
							// create mask to update sticky bits; low
							// order bitsDiscarded bits should be 1
							long mask = ~((~0L) << (bitsDiscarded - 1));
							sticky = sticky || ((significand & mask) != 0L);
						}

						// Now, discard the bits
						significand = significand >> bitsDiscarded;

						significand = ((((long) (DoubleConsts.MIN_EXPONENT - 1) + // subnorm exp.
								(long) DoubleConsts.EXP_BIAS) << (DoubleConsts.SIGNIFICAND_WIDTH - 1)) & DoubleConsts.EXP_BIT_MASK) | (DoubleConsts.SIGNIF_BIT_MASK & significand);
					}
				}

				// The significand variable now contains the currently
				// appropriate exponent bits too.

				/*
				 * Determine if significand should be incremented;
				 * making this determination depends on the least
				 * significant bit and the round and sticky bits.
				 *
				 * Round to nearest even rounding table, adapted from
				 * table 4.7 in "Computer Arithmetic" by IsraelKoren.
				 * The digit to the left of the "decimal" point is the
				 * least significant bit, the digits to the right of
				 * the point are the round and sticky bits
				 *
				 * Number       Round(x)
				 * x0.00        x0.
				 * x0.01        x0.
				 * x0.10        x0.
				 * x0.11        x1. = x0. +1
				 * x1.00        x1.
				 * x1.01        x1.
				 * x1.10        x1. + 1
				 * x1.11        x1. + 1
				 */
				boolean incremented = false;
				boolean leastZero = ((significand & 1L) == 0L);
				if ((leastZero && round && sticky) || ((!leastZero) && round)) {
					incremented = true;
					significand++;
				}

				FastFloatingDecimal fd = new FastFloatingDecimal(FpUtils.rawCopySign(Double.longBitsToDouble(significand), sign));

				/*
				 * Set roundingDir variable field of fd properly so
				 * that the input string can be properly rounded to a
				 * float value.  There are two cases to consider:
				 *
				 * 1. rounding to double discards sticky bit
				 * information that would change the result of a float
				 * rounding (near halfway case between two floats)
				 *
				 * 2. rounding to double rounds up when rounding up
				 * would not occur when rounding to float.
				 *
				 * For former case only needs to be considered when
				 * the bits rounded away when casting to float are all
				 * zero; otherwise, float round bit is properly set
				 * and sticky will already be true.
				 *
				 * The lower exponent bound for the code below is the
				 * minimum (normalized) subnormal exponent - 1 since a
				 * value with that exponent can round up to the
				 * minimum subnormal value and the sticky bit
				 * information must be preserved (i.e. case 1).
				 */
				if ((exponent >= FloatConsts.MIN_SUB_EXPONENT - 1) && (exponent <= FloatConsts.MAX_EXPONENT)) {
					// Outside above exponent range, the float value
					// will be zero or infinity.

					/*
					 * If the low-order 28 bits of a rounded double
					 * significand are 0, the double could be a
					 * half-way case for a rounding to float.  If the
					 * double value is a half-way case, the double
					 * significand may have to be modified to round
					 * the the right float value (see the stickyRound
					 * method).  If the rounding to double has lost
					 * what would be float sticky bit information, the
					 * double significand must be incremented.  If the
					 * double value's significand was itself
					 * incremented, the float value may end up too
					 * large so the increment should be undone.
					 */
					if ((significand & 0xfffffffL) == 0x0L) {
						// For negative values, the sign of the
						// roundDir is the same as for positive values
						// since adding 1 increasing the significand's
						// magnitude and subtracting 1 decreases the
						// significand's magnitude.  If neither round
						// nor sticky is true, the double value is
						// exact and no adjustment is required for a
						// proper float rounding.
						if (round || sticky) {
							if (leastZero) { // prerounding lsb is 0
								// If round and sticky were both true,
								// and the least significant
								// significand bit were 0, the rounded
								// significand would not have its
								// low-order bits be zero.  Therefore,
								// we only need to adjust the
								// significand if round XOR sticky is
								// true.
								if (round ^ sticky) {
									fd.roundDir = 1;
								}
							} else { // prerounding lsb is 1
										// If the prerounding lsb is 1 and the
										// resulting significand has its
										// low-order bits zero, the significand
										// was incremented.  Here, we undo the
										// increment, which will ensure the
										// right guard and sticky bits for the
										// float rounding.
								if (round)
									fd.roundDir = -1;
							}
						}
					}
				}

				fd.fromHex = true;
				return fd;
			}
		}
	}

	/**
	 * Return <code>s</code> with any leading zeros removed.
	 */
	static String stripLeadingZeros(String s) {
		return s.replaceFirst("^0+", "");
	}

	/**
	 * Extract a hexadecimal digit from position <code>position</code> of string <code>s</code>.
	 */
	static int getHexDigit(String s, int position) {
		int value = Character.digit(s.charAt(position), 16);
		if (value <= -1 || value >= 16) {
			throw new AssertionError("Unexpected failure of digit conversion of " + s.charAt(position));
		}
		return value;
	}

}

/*
 * A really, really simple bigint package
 * tailored to the needs of floating base conversion.
 */
class FDBigInt {
	int nWords; // number of words used
	int data[]; // value: data[0] is least significant

	public FDBigInt(int v) {
		nWords = 1;
		data = new int[1];
		data[0] = v;
	}

	public FDBigInt(long v) {
		data = new int[2];
		data[0] = (int) v;
		data[1] = (int) (v >>> 32);
		nWords = (data[1] == 0) ? 1 : 2;
	}

	public FDBigInt(FDBigInt other) {
		data = new int[nWords = other.nWords];
		System.arraycopy(other.data, 0, data, 0, nWords);
	}

	private FDBigInt(int[] d, int n) {
		data = d;
		nWords = n;
	}

	public FDBigInt(long seed, char digit[], int nd0, int nd) {
		int n = (nd + 8) / 9; // estimate size needed.
		if (n < 2)
			n = 2;
		data = new int[n]; // allocate enough space
		data[0] = (int) seed; // starting value
		data[1] = (int) (seed >>> 32);
		nWords = (data[1] == 0) ? 1 : 2;
		int i = nd0;
		int limit = nd - 5; // slurp digits 5 at a time.
		int v;
		while (i < limit) {
			int ilim = i + 5;
			v = (int) digit[i++] - (int) '0';
			while (i < ilim) {
				v = 10 * v + (int) digit[i++] - (int) '0';
			}
			multaddMe(100000, v); // ... where 100000 is 10^5.
		}
		int factor = 1;
		v = 0;
		while (i < nd) {
			v = 10 * v + (int) digit[i++] - (int) '0';
			factor *= 10;
		}
		if (factor != 1) {
			multaddMe(factor, v);
		}
	}

	/*
	 * Left shift by c bits.
	 * Shifts this in place.
	 */
	public void lshiftMe(int c) throws IllegalArgumentException {
		if (c <= 0) {
			if (c == 0)
				return; // silly.
			else
				throw new IllegalArgumentException("negative shift count");
		}
		int wordcount = c >> 5;
		int bitcount = c & 0x1f;
		int anticount = 32 - bitcount;
		int t[] = data;
		int s[] = data;
		if (nWords + wordcount + 1 > t.length) {
			// reallocate.
			t = new int[nWords + wordcount + 1];
		}
		int target = nWords + wordcount;
		int src = nWords - 1;
		if (bitcount == 0) {
			// special hack, since an anticount of 32 won't go!
			System.arraycopy(s, 0, t, wordcount, nWords);
			target = wordcount - 1;
		} else {
			t[target--] = s[src] >>> anticount;
			while (src >= 1) {
				t[target--] = (s[src] << bitcount) | (s[--src] >>> anticount);
			}
			t[target--] = s[src] << bitcount;
		}
		while (target >= 0) {
			t[target--] = 0;
		}
		data = t;
		nWords += wordcount + 1;
		// may have constructed high-order word of 0.
		// if so, trim it
		while (nWords > 1 && data[nWords - 1] == 0)
			nWords--;
	}

	/*
	 * normalize this number by shifting until
	 * the MSB of the number is at 0x08000000.
	 * This is in preparation for quoRemIteration, below.
	 * The idea is that, to make division easier, we want the
	 * divisor to be "normalized" -- usually this means shifting
	 * the MSB into the high words sign bit. But because we know that
	 * the quotient will be 0 < q < 10, we would like to arrange that
	 * the dividend not span up into another word of precision.
	 * (This needs to be explained more clearly!)
	 */
	public int normalizeMe() throws IllegalArgumentException {
		int src;
		int wordcount = 0;
		int bitcount = 0;
		int v = 0;
		for (src = nWords - 1; src >= 0 && (v = data[src]) == 0; src--) {
			wordcount += 1;
		}
		if (src < 0) {
			// oops. Value is zero. Cannot normalize it!
			throw new IllegalArgumentException("zero value");
		}
		/*
		 * In most cases, we assume that wordcount is zero. This only
		 * makes sense, as we try not to maintain any high-order
		 * words full of zeros. In fact, if there are zeros, we will
		 * simply SHORTEN our number at this point. Watch closely...
		 */
		nWords -= wordcount;
		/*
		 * Compute how far left we have to shift v s.t. its highest-
		 * order bit is in the right place. Then call lshiftMe to
		 * do the work.
		 */
		if ((v & 0xf0000000) != 0) {
			// will have to shift up into the next word.
			// too bad.
			for (bitcount = 32; (v & 0xf0000000) != 0; bitcount--)
				v >>>= 1;
		} else {
			while (v <= 0x000fffff) {
				// hack: byte-at-a-time shifting
				v <<= 8;
				bitcount += 8;
			}
			while (v <= 0x07ffffff) {
				v <<= 1;
				bitcount += 1;
			}
		}
		if (bitcount != 0)
			lshiftMe(bitcount);
		return bitcount;
	}

	/*
	 * Multiply a FDBigInt by an int.
	 * Result is a new FDBigInt.
	 */
	public FDBigInt mult(int iv) {
		long v = iv;
		int r[];
		long p;

		// guess adequate size of r.
		r = new int[(v * ((long) data[nWords - 1] & 0xffffffffL) > 0xfffffffL) ? nWords + 1 : nWords];
		p = 0L;
		for (int i = 0; i < nWords; i++) {
			p += v * ((long) data[i] & 0xffffffffL);
			r[i] = (int) p;
			p >>>= 32;
		}
		if (p == 0L) {
			return new FDBigInt(r, nWords);
		} else {
			r[nWords] = (int) p;
			return new FDBigInt(r, nWords + 1);
		}
	}

	/*
	 * Multiply a FDBigInt by an int and add another int.
	 * Result is computed in place.
	 * Hope it fits!
	 */
	public void multaddMe(int iv, int addend) {
		long v = iv;
		long p;

		// unroll 0th iteration, doing addition.
		p = v * ((long) data[0] & 0xffffffffL) + ((long) addend & 0xffffffffL);
		data[0] = (int) p;
		p >>>= 32;
		for (int i = 1; i < nWords; i++) {
			p += v * ((long) data[i] & 0xffffffffL);
			data[i] = (int) p;
			p >>>= 32;
		}
		if (p != 0L) {
			data[nWords] = (int) p; // will fail noisily if illegal!
			nWords++;
		}
	}

	/*
	 * Multiply a FDBigInt by another FDBigInt.
	 * Result is a new FDBigInt.
	 */
	public FDBigInt mult(FDBigInt other) {
		// crudely guess adequate size for r
		int r[] = new int[nWords + other.nWords];
		int i;
		// I think I am promised zeros...

		for (i = 0; i < this.nWords; i++) {
			long v = (long) this.data[i] & 0xffffffffL; // UNSIGNED CONVERSION
			long p = 0L;
			int j;
			for (j = 0; j < other.nWords; j++) {
				p += ((long) r[i + j] & 0xffffffffL) + v * ((long) other.data[j] & 0xffffffffL); // UNSIGNED CONVERSIONS ALL 'ROUND.
				r[i + j] = (int) p;
				p >>>= 32;
			}
			r[i + j] = (int) p;
		}
		// compute how much of r we actually needed for all that.
		for (i = r.length - 1; i > 0; i--)
			if (r[i] != 0)
				break;
		return new FDBigInt(r, i + 1);
	}

	/*
	 * Add one FDBigInt to another. Return a FDBigInt
	 */
	public FDBigInt add(FDBigInt other) {
		int i;
		int a[], b[];
		int n, m;
		long c = 0L;
		// arrange such that a.nWords >= b.nWords;
		// n = a.nWords, m = b.nWords
		if (this.nWords >= other.nWords) {
			a = this.data;
			n = this.nWords;
			b = other.data;
			m = other.nWords;
		} else {
			a = other.data;
			n = other.nWords;
			b = this.data;
			m = this.nWords;
		}
		int r[] = new int[n];
		for (i = 0; i < n; i++) {
			c += (long) a[i] & 0xffffffffL;
			if (i < m) {
				c += (long) b[i] & 0xffffffffL;
			}
			r[i] = (int) c;
			c >>= 32; // signed shift.
		}
		if (c != 0L) {
			// oops -- carry out -- need longer result.
			int s[] = new int[r.length + 1];
			System.arraycopy(r, 0, s, 0, r.length);
			s[i++] = (int) c;
			return new FDBigInt(s, i);
		}
		return new FDBigInt(r, i);
	}

	/*
	 * Subtract one FDBigInt from another. Return a FDBigInt
	 * Assert that the result is positive.
	 */
	public FDBigInt sub(FDBigInt other) {
		int r[] = new int[this.nWords];
		int i;
		int n = this.nWords;
		int m = other.nWords;
		int nzeros = 0;
		long c = 0L;
		for (i = 0; i < n; i++) {
			c += (long) this.data[i] & 0xffffffffL;
			if (i < m) {
				c -= (long) other.data[i] & 0xffffffffL;
			}
			if ((r[i] = (int) c) == 0)
				nzeros++;
			else
				nzeros = 0;
			c >>= 32; // signed shift
		}
		assert c == 0L : c; // borrow out of subtract
		assert dataInRangeIsZero(i, m, other); // negative result of subtract
		return new FDBigInt(r, n - nzeros);
	}

	private static boolean dataInRangeIsZero(int i, int m, FDBigInt other) {
		while (i < m)
			if (other.data[i++] != 0)
				return false;
		return true;
	}

	/*
	 * Compare FDBigInt with another FDBigInt. Return an integer
	 * >0: this > other
	 *  0: this == other
	 * <0: this < other
	 */
	public int cmp(FDBigInt other) {
		int i;
		if (this.nWords > other.nWords) {
			// if any of my high-order words is non-zero,
			// then the answer is evident
			int j = other.nWords - 1;
			for (i = this.nWords - 1; i > j; i--)
				if (this.data[i] != 0)
					return 1;
		} else if (this.nWords < other.nWords) {
			// if any of other's high-order words is non-zero,
			// then the answer is evident
			int j = this.nWords - 1;
			for (i = other.nWords - 1; i > j; i--)
				if (other.data[i] != 0)
					return -1;
		} else {
			i = this.nWords - 1;
		}
		for (; i > 0; i--)
			if (this.data[i] != other.data[i])
				break;
		// careful! want unsigned compare!
		// use brute force here.
		int a = this.data[i];
		int b = other.data[i];
		if (a < 0) {
			// a is really big, unsigned
			if (b < 0) {
				return a - b; // both big, negative
			} else {
				return 1; // b not big, answer is obvious;
			}
		} else {
			// a is not really big
			if (b < 0) {
				// but b is really big
				return -1;
			} else {
				return a - b;
			}
		}
	}

	/*
	 * Compute
	 * q = (int)( this / S )
	 * this = 10 * ( this mod S )
	 * Return q.
	 * This is the iteration step of digit development for output.
	 * We assume that S has been normalized, as above, and that
	 * "this" has been lshift'ed accordingly.
	 * Also assume, of course, that the result, q, can be expressed
	 * as an integer, 0 <= q < 10.
	 */
	public int quoRemIteration(FDBigInt S) throws IllegalArgumentException {
		// ensure that this and S have the same number of
		// digits. If S is properly normalized and q < 10 then
		// this must be so.
		if (nWords != S.nWords) {
			throw new IllegalArgumentException("disparate values");
		}
		// estimate q the obvious way. We will usually be
		// right. If not, then we're only off by a little and
		// will re-add.
		int n = nWords - 1;
		long q = ((long) data[n] & 0xffffffffL) / (long) S.data[n];
		long diff = 0L;
		for (int i = 0; i <= n; i++) {
			diff += ((long) data[i] & 0xffffffffL) - q * ((long) S.data[i] & 0xffffffffL);
			data[i] = (int) diff;
			diff >>= 32; // N.B. SIGNED shift.
		}
		if (diff != 0L) {
			// damn, damn, damn. q is too big.
			// add S back in until this turns +. This should
			// not be very many times!
			long sum = 0L;
			while (sum == 0L) {
				sum = 0L;
				for (int i = 0; i <= n; i++) {
					sum += ((long) data[i] & 0xffffffffL) + ((long) S.data[i] & 0xffffffffL);
					data[i] = (int) sum;
					sum >>= 32; // Signed or unsigned, answer is 0 or 1
				}
				/*
				 * Originally the following line read
				 * "if ( sum !=0 && sum != -1 )"
				 * but that would be wrong, because of the
				 * treatment of the two values as entirely unsigned,
				 * it would be impossible for a carry-out to be interpreted
				 * as -1 -- it would have to be a single-bit carry-out, or
				 * +1.
				 */
				assert sum == 0 || sum == 1 : sum; // carry out of division correction
				q -= 1;
			}
		}
		// finally, we can multiply this by 10.
		// it cannot overflow, right, as the high-order word has
		// at least 4 high-order zeros!
		long p = 0L;
		for (int i = 0; i <= n; i++) {
			p += 10 * ((long) data[i] & 0xffffffffL);
			data[i] = (int) p;
			p >>= 32; // SIGNED shift.
		}
		assert p == 0L : p; // Carry out of *10
		return (int) q;
	}

	public long longValue() {
		// if this can be represented as a long, return the value
		assert this.nWords > 0 : this.nWords; // longValue confused

		if (this.nWords == 1)
			return ((long) data[0] & 0xffffffffL);

		assert dataInRangeIsZero(2, this.nWords, this); // value too big
		assert data[1] >= 0; // value too big
		return ((long) (data[1]) << 32) | ((long) data[0] & 0xffffffffL);
	}

	public String toString() {
		StringBuilder r = new StringBuilder(30);
		r.append('[');
		int i = Math.min(nWords - 1, data.length - 1);
		if (nWords > data.length) {
			r.append("(" + data.length + "<" + nWords + "!)");
		}
		for (; i > 0; i--) {
			r.append(Integer.toHexString(data[i]));
			r.append(' ');
		}
		r.append(Integer.toHexString(data[0]));
		r.append(']');
		return new String(r);
	}
}
