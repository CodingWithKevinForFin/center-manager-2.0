package com.f1.base;

import java.lang.instrument.Instrumentation;

public class InstrumentationReference {
	private static Instrumentation INSTRUMENTATION;

	public static Instrumentation getInstrumentation() {
		return INSTRUMENTATION;
	}

	private InstrumentationReference() {
	}

	public static void setInstrumentation(Instrumentation instrumentation) {
		if (instrumentation == null)
			System.err.println(InstrumentationReference.class.getName() + ": Instrumentation is null");
		if (INSTRUMENTATION != null)
			throw new IllegalStateException("setInstrumentation already called");
		INSTRUMENTATION = instrumentation;
	}
}
