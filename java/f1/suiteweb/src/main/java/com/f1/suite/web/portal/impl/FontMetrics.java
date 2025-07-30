package com.f1.suite.web.portal.impl;

import com.f1.utils.MH;
import com.f1.utils.OH;

public class FontMetrics {

	final private String name;
	final private short[] widths;
	final private short[] savings;
	final private int max;
	final private int savingChar1Max;
	final private int savingChar1Min;
	final private int savingChar2Max;
	final private int savingChar2Min;
	final private int savingChar1Mult;
	final private double fontSize;
	final private double fontSizeBold;
	final private boolean isMonospace;

	public FontMetrics(String name, int fontSize, double boldMultiplier, short[] widths, short[] savings) {
		this.fontSize = fontSize;
		this.fontSizeBold = fontSize / boldMultiplier;
		this.name = name;
		this.widths = widths;
		this.max = MH.maxs(widths);
		int c1Max = 0;
		int c2Max = 0;
		int c1Min = Integer.MAX_VALUE;
		int c2Min = Integer.MAX_VALUE;
		if (savings.length > 0) {
			for (int i = 0; i < savings.length; i += 3) {
				c1Min = Math.min(c1Min, savings[i]);
				c1Max = Math.max(c1Max, savings[i]);
				c2Min = Math.min(c2Min, savings[i + 1]);
				c2Max = Math.max(c2Max, savings[i + 1]);
			}
			this.savingChar1Max = c1Max;
			this.savingChar2Max = c2Max;
			this.savingChar1Min = c1Min;
			this.savingChar2Min = c2Min;
			this.savingChar1Mult = 1 + c2Max - c2Min;
			this.savings = new short[(c1Max - c1Min + 1) * this.savingChar1Mult];
			for (int i = 0; i < savings.length; i += 3)
				this.savings[toSavingsIndex(savings[i], savings[i + 1])] = savings[i + 2];
			this.isMonospace = false;
		} else {
			this.savings = OH.EMPTY_SHORT_ARRAY;
			savingChar1Min = Integer.MAX_VALUE;
			savingChar2Min = Integer.MAX_VALUE;
			savingChar1Max = Integer.MIN_VALUE;
			savingChar2Max = Integer.MIN_VALUE;
			savingChar1Mult = 0;
			boolean b = true;
			for (int i : widths) {
				if (i != 0 && i != this.max) {
					b = false;
					break;
				}
			}
			this.isMonospace = b;
		}

	}
	private int toSavingsIndex(int c1, int c2) {
		if (OH.isBetween(c1, savingChar1Min, savingChar1Max))
			if (OH.isBetween(c2, savingChar2Min, savingChar2Max))
				return (c1 - savingChar1Min) * savingChar1Mult + c2 - savingChar2Min;
		return -1;
	}
	public int getWidth(char c) {
		if (isMonospace || c < 0 || c >= widths.length)
			return max;
		if (isMonospace)
			return this.max;
		return widths[(int) c];
	}
	public int getWidth(char c, char priorChar) {
		if (isMonospace || c < 0 || c >= widths.length)
			return max;
		int t = toSavingsIndex(priorChar, c);
		if (t == -1)
			return widths[(int) c];
		return widths[(int) c] - savings[t];
	}

	public int getMaxWidth() {
		return this.max;
	}

	public int toPx(int fontSizePx, int width, boolean isBold) {
		return (int) (width * (fontSizePx / (isBold ? this.fontSizeBold : this.fontSize)));
	}
	public int getWidth(CharSequence text) {
		int len = text.length();
		if (len == 0)
			return 0;
		if (isMonospace)
			return len * this.max;
		char prior = text.charAt(0);
		int r = getWidth(prior);
		for (int i = 1; i < len; i++) {
			final char t = text.charAt(i);
			r += getWidth(t, prior);
			prior = t;
		}
		return r;
	}
	public String getFontName() {
		return this.name;
	}

}
