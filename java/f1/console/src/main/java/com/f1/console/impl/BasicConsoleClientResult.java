package com.f1.console.impl;

import java.util.ArrayList;
import java.util.List;
import com.f1.console.ConsoleClientResult;
import com.f1.console.ConsoleConnection;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class BasicConsoleClientResult implements ConsoleClientResult {

	final private List<String> comments;
	final private String result;
	final private boolean hasError;

	public BasicConsoleClientResult(String result, List<String> comments, boolean hasError) {
		OH.assertNotNull(result);
		OH.assertNotNull(comments);
		this.result = result;
		this.comments = comments;
		this.hasError = hasError;
	}

	public boolean hasErrors() {
		return hasError;
	}

	@Override
	public String getResult() {
		return result;
	}

	@Override
	public String getComments() {
		return SH.join(SH.NEWLINE, comments);
	}

	@Override
	public String[] getCommentsByType(String type) {
		String prefix = ConsoleConnection.PREFIX + type + ":";
		ArrayList<String> r = new ArrayList<String>();
		for (String s : comments)
			if (s.startsWith(prefix))
				r.add(SH.stripPrefix(s, prefix, true));
		if (r.size() == 0)
			return OH.EMPTY_STRING_ARRAY;
		return r.toArray(new String[r.size()]);
	}

	@Override
	public String toString() {
		return getComments() + "\n----\n" + result;
	}

}
