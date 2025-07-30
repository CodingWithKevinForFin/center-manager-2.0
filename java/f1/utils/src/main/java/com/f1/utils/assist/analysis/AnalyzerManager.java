package com.f1.utils.assist.analysis;

import java.util.HashSet;
import java.util.Set;

import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;

public class AnalyzerManager {

	final private Set<Class> ignore = new HashSet<Class>();
	final private HasherMap<Class<?>, ClassAnalyzer> analyzers = new HasherMap<Class<?>, ClassAnalyzer>();

	public void ignore(Class clazz) {
		ignore.add(clazz);
		analyzers.put(clazz, null);
	}
	public ClassAnalyzer getClassAnalyzer(Class<?> clazz) {
		if (clazz == null)
			return null;
		Entry<Class<?>, ClassAnalyzer> e = analyzers.getEntry(clazz);
		if (e == null) {
			for (Class ig : ignore)
				if (ig.isAssignableFrom(clazz)) {
					analyzers.put(clazz, null);
					return null;
				}
			ClassAnalyzer r = new ClassAnalyzer(clazz, this);
			analyzers.put(clazz, r);
			r.initFields();
			return r;
		}
		return e.getValue();
	}

}
