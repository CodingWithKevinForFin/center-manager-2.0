package com.f1.utils.validation;

import java.util.List;
import com.f1.base.ToStringable;

public interface IssueLog extends ToStringable {
	public void addWarning(Object field, String message, Exception e);

	public void addError(Object field, String message, Exception e);

	public boolean getHasIssues();

	public boolean getHasErrors();

	public boolean getHasWarnings();

	public List<Issue> getIssues();

	public List<Issue> getWarnings();

	public List<Issue> getErrors();

	StringBuilder toLegibleString(StringBuilder sb, boolean expandExceptions);
}
