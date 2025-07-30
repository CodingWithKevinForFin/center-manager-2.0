/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.bootstrap;

import java.io.IOException;

import com.f1.speedlogger.StackTraceFormatter;
import com.f1.utils.SH;

public class ConvertedStackTraceFormatter implements StackTraceFormatter {

	public static final ConvertedStackTraceFormatter INSTANCE = new ConvertedStackTraceFormatter();

	@Override
	public void printStackTrace(String prefix, String indent, Throwable exception, Appendable sb) throws IOException {
		SH.printStackTrace(prefix, indent, exception, sb);
	}

}
