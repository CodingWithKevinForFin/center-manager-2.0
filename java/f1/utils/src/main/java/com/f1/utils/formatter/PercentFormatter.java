/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.formatter;

public class PercentFormatter extends AbstractFormatter {

	final private BasicNumberFormatter formatter;
	final private String prefix;

	public PercentFormatter(BasicNumberFormatter formatter, String prefix) {
		this.formatter = formatter;
		this.prefix = prefix;
	}

	@Override
	public void format(Object value, StringBuilder sb) {
		Number n = (Number) value;
		double percent = n.doubleValue() * 100;
		formatter.format(percent, sb);
		sb.append(prefix);
	}

	@Override
	public PercentFormatter clone() {
		return new PercentFormatter(formatter.clone(), prefix);
	}

	@Override
	public boolean canParse(String text) {
		return text.startsWith(prefix) && formatter.canParse(text.substring(1));
	}

	@Override
	public Object parse(String text) {
		if (!text.startsWith(prefix))
			throw new RuntimeException("not in percentage format: " + text);
		return formatter.parse(text.substring(prefix.length())).doubleValue() / 100;
	}
}
