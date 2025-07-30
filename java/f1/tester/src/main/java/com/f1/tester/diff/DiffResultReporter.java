package com.f1.tester.diff;

import java.util.ArrayList;
import java.util.List;
import com.f1.base.ValuedEnum;
import com.f1.utils.CH;
import com.f1.utils.Formatter;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class DiffResultReporter {

	private int tabSize = 2;
	private int maxLineSize = 2048;

	public DiffResultReporter() {

	}

	private List<Formatter> customFormatters = new ArrayList<Formatter>();

	public void addCustomFormatter(Formatter f) {
		customFormatters.add(f);
	}

	public String report(DiffResult result) {
		if (result == null)
			return "#### NO MISMATCHES ####";
		StringBuilder sb = new StringBuilder();
		sb.append("#### FOUND " + result.getDiffsCount() + " MISMATCHES(S) ####").append(SH.NEWLINE);
		return report(result.getKey(), result, sb, 0).toString();
	}

	protected StringBuilder report(String path, DiffResult result, StringBuilder sb, int indent) {
		indent(indent, sb);
		if (SH.is(result.getKey()))
			sb.append("ON ").append(path).append(":");
		sb.append(result.getResult());
		if (SH.is(result.getDetails()))
			sb.append(":").append(result.getDetails());
		sb.append(SH.NEWLINE);
		if (CH.isntEmpty(result.getChildren())) {
			for (DiffResult child : result.getChildren()) {
				report(SH.path('.', path, child.getKey()), child, sb, indent + 1);
			}
		} else {
			String s1 = SH.ddd(toString(result.getLeft()), maxLineSize);
			String s2 = SH.ddd(toString(result.getRight()), maxLineSize);
			if (OH.eq(s1, s2)) {
				s1 = toString(result.getLeft());
				s2 = toString(result.getRight());
			}
			final boolean includeDetails = (OH.eq(s1, s2));
			if (result.getLeft() != DifferConstants.MISSING && result.getResult() != DifferConstants.LEFT_IS_NULL
					&& result.getResult() != DifferConstants.LEFT_KEY_ABSENT) {
				indent(indent, sb).append("<<");
				if (includeDetails)
					sb.append("(class=").append(OH.getClass(result.getLeft()).getName()).append(")");
				sb.append(' ');
				if (s1 instanceof CharSequence)
					SH.quote('\"', s1, sb);
				else
					sb.append(s1);
				sb.append("");
				sb.append(SH.NEWLINE);
			}
			if (result.getRight() != DifferConstants.MISSING && result.getResult() != DifferConstants.RIGHT_IS_NULL
					&& result.getResult() != DifferConstants.RIGHT_KEY_ABSENT) {
				indent(indent, sb).append(">>");
				if (includeDetails)
					sb.append("(class=").append(OH.getClass(result.getRight()).getName()).append(")");
				sb.append(' ');
				if (s2 instanceof CharSequence)
					SH.quote('\"', s2, sb);
				else
					sb.append(s2);
				sb.append(SH.NEWLINE);
			}
		}
		return sb;

	}

	private String toString(Object o) {
		for (Formatter f : customFormatters)
			if (f.canFormat(o))
				return f.format(o);
		if (o instanceof ValuedEnum && o.getClass().isEnum()) {
			ValuedEnum ve = (ValuedEnum) o;
			Enum e = (Enum) o;
			return "Valued Enum: " + e.name() + "(" + toString(ve.getEnumValue()) + ")";
		}
		return SH.toString(o);
	}

	private StringBuilder indent(int indent, StringBuilder sb) {
		return SH.repeat(' ', indent * tabSize, sb);
	}
}
