/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.formatter;

import java.text.NumberFormat;
import java.text.ParsePosition;

import com.f1.base.Complex;
import com.f1.utils.OH;

public class BasicNumberFormatter extends AbstractFormatter {

	private static final char[] SPECIAL = "-.,%".toCharArray();

	private NumberFormat numberFormat;

	private String prefix, suffix;

	public BasicNumberFormatter(NumberFormat nf) {
		this(nf, null, null);
	}

	public BasicNumberFormatter(NumberFormat nf, String prefix, String suffix) {
		this.numberFormat = nf;
		if (numberFormat == null)
			throw new NullPointerException();
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public void format(Object value, StringBuilder sb) {
		if (prefix != null)
			sb.append(prefix);
		if (value != null) {
			try {
				if (value instanceof Complex) {
					Complex c = (Complex) value;
					double imaginary = c.imaginary();
					double real = c.real();
					if (imaginary == 0)
						sb.append(numberFormat.format(real));
					else if (real == 0)
						sb.append(numberFormat.format(imaginary)).append('i');
					else if (imaginary > 0) {
						sb.append(numberFormat.format(real)).append('+');
						sb.append(numberFormat.format(imaginary)).append('i');
					} else if (imaginary < 0) {
						sb.append(numberFormat.format(real));
						sb.append(numberFormat.format(imaginary)).append('i');
					} else {
						sb.append(numberFormat.format(real)).append('+');
						sb.append(numberFormat.format(imaginary)).append("i");
					}
				} else if (value instanceof Number)
					sb.append(numberFormat.format(value));
			} catch (Exception e) {
				sb.append("Exception: ").append(e.getMessage()).append(" value type=").append(value.getClass().getName());
			}
		} else
			sb.append("null");
		if (suffix != null)
			sb.append(suffix);
	}
	@Override
	public BasicNumberFormatter clone() {
		return new BasicNumberFormatter((NumberFormat) numberFormat.clone(), prefix, suffix);
	}

	@Override
	public boolean canParse(String text) {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (!OH.isBetween(c, '0', '9') && !OH.in(c, SPECIAL))
				return false;
		}
		return true;
	}

	@Override
	public Number parse(String text) {
		ParsePosition pos = new ParsePosition(0);
		Number r = (Number) numberFormat.parseObject(text, pos);
		if (pos.getIndex() != text.length())
			throw new RuntimeException("trailing text after char " + pos.getIndex() + ": " + text);
		if (pos.getErrorIndex() != -1)
			throw new RuntimeException("could not parse, error at char " + pos.getErrorIndex() + ": " + text);
		return r;
	}
	public NumberFormat getNumberFormat() {
		return this.numberFormat;
	}
}
