/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.IOException;

import com.f1.speedlogger.StackTraceFormatter;
import com.f1.utils.SH;

public class BasicStackTraceFormatter implements StackTraceFormatter {

	public static final String NEWLINE = System.getProperty("line.separator");

	@Override
	public void printStackTrace(final String prefix, final String indent, Throwable exception, final Appendable sb) throws IOException {
		SH.printStackTrace(prefix, indent, exception, sb);
	}

}
