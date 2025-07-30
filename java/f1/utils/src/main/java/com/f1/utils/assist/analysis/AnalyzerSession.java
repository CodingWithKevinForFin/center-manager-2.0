package com.f1.utils.assist.analysis;

import java.util.Set;

import com.f1.utils.concurrent.IdentityHashSet;

public class AnalyzerSession {

	final private Set<Object> visited = new IdentityHashSet<Object>();
	final private AnalyzerManager manager;

	public AnalyzerSession(AnalyzerManager manager) {
		this.manager = manager;
	}

	public boolean visit(Object obj) {
		return visited.add(obj);
	}

	public AnalyzerManager getManager() {
		return manager;
	}

}
