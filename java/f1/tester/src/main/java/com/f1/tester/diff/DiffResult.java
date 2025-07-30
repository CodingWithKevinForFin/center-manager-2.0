package com.f1.tester.diff;

import java.util.ArrayList;
import java.util.List;
import com.f1.base.Legible;
import com.f1.base.ToStringable;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class DiffResult implements ToStringable, Legible {

	final private Object left;
	final private Object right;
	final private String result, details;
	final private List<DiffResult> children = new ArrayList<DiffResult>();
	private String key;

	public DiffResult(Object left, Object right, String result) {
		this(left, right, result, null);
	}

	public DiffResult(Object left, Object right, String result, String details) {
		this.left = left;
		this.right = right;
		this.result = result;
		this.details = details;
	}

	public String getDetails() {
		return details;
	}

	public void addChild(DiffResult child) {
		this.children.add(child);
	}

	public Object getLeft() {
		return left;
	}

	public Object getRight() {
		return right;
	}

	public String getResult() {
		return result;
	}

	public List<DiffResult> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append(result);
		if (key != null)
			sb.append('[').append(key).append(']');
		sb.append('{');
		if (SH.is(details)) {
			sb.append(details);
		} else if (CH.isEmpty(children)) {
			sb.append(left).append("!=").append(right);
		} else {
			SH.join(',', children, sb);
		}
		sb.append('}');
		return sb;
	}

	public DiffResult setKey(String key) {
		this.key = key;
		return this;
	}

	public int getDiffsCount() {
		if (children.size() == 0)
			return 1;
		int i = 0;
		for (DiffResult d : children)
			i += d.getDiffsCount();
		return i;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toLegibleString() {
		return toLegibleString(new StringBuilder(), 0).toString();
	}

	private StringBuilder toLegibleString(StringBuilder sb, int indent) {
		SH.repeat(' ', indent * 4, sb);
		if (key == null)
			sb.append("diff {");
		else
			sb.append(result).append(" for ").append(key).append(": {");
		sb.append(SH.NEWLINE);
		if (CH.isEmpty(children)) {
			if (left != DifferConstants.MISSING) {
				SH.repeat(' ', (1 + indent) * 4, sb);
				sb.append("< ").append(left).append(SH.NEWLINE);
			}
			if (right != DifferConstants.MISSING) {
				SH.repeat(' ', (1 + indent) * 4, sb);
				sb.append("> ").append(right).append(SH.NEWLINE);
			}
		} else {
			for (DiffResult child : children)
				child.toLegibleString(sb, indent + 1);
		}
		SH.repeat(' ', indent * 4, sb);
		sb.append("}").append(SH.NEWLINE);
		return sb;
	}
}
