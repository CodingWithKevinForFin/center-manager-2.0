package com.f1.http.handler;

import java.util.HashSet;
import java.util.Set;

import com.f1.utils.SH;

public class JspBuilderSession {

	private Set<String> imports = new HashSet<String>();
	private StringBuilder body = new StringBuilder();

	public void addImport(String imprt) {
		imports.add(imprt);
	}

	public Set<String> getImports() {
		return imports;
	}

	public StringBuilder getBody() {
		return body;
	}

	public void appendIndent(int indent) {
		SH.repeat(' ', indent, body);
	}

}
