package com.f1.utils.validation;

import java.util.ArrayList;
import java.util.List;
import com.f1.base.ToStringable;
import com.f1.utils.SH;

public class BasicIssueLog implements IssueLog, ToStringable {
	private List<Issue> issues = new ArrayList<Issue>(2);
	private List<Issue> warnings = new ArrayList<Issue>(2);
	private List<Issue> errors = new ArrayList<Issue>(2);

	@Override
	public void addWarning(Object field, String message, Exception e) {
		Issue issue = new Issue(Issue.WARNING, field, message, e);
		issues.add(issue);
		warnings.add(issue);
	}

	@Override
	public void addError(Object field, String message, Exception e) {
		Issue issue = new Issue(Issue.ERROR, field, message, e);
		issues.add(issue);
		errors.add(issue);
	}

	public boolean getHasIssues() {
		return !issues.isEmpty();
	}

	public boolean getHasErrors() {
		return !errors.isEmpty();
	}

	public boolean getHasWarnings() {
		return !warnings.isEmpty();
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public List<Issue> getWarnings() {
		return warnings;
	}

	public List<Issue> getErrors() {
		return errors;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		boolean first = true;
		for (int i = 0, l = issues.size(); i < l; ++i) {
			if (i != 0)
				sb.append(',');
			sb.append('[');
			issues.get(i).toString(sb);
			sb.append(']');
		}
		return sb;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toLegibleString(StringBuilder sb, boolean expandExceptions) {
		for (int i = 0, l = issues.size(); i < l; ++i)
			issues.get(i).toString(sb, expandExceptions).append(SH.NEWLINE);
		return sb;
	}

	public static void main(String a[]) {
		BasicIssueLog il = new BasicIssueLog();
		il.addError(1, "error on field", new RuntimeException("this is a test"));
		il.addError(2, "error on field", new RuntimeException());
		il.addWarning(null, "error on field", new RuntimeException());
		il.addWarning(4, "what on field", null);
		System.out.println(il.toLegibleString(new StringBuilder(), true));
	}
}
