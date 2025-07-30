/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

import java.io.IOException;

/**
 * capable of converting a stack trace into a user legible string sequence.
 */
public interface StackTraceFormatter {

	/**
	 * 
	 * @param prefix
	 *            the prefix to include at the start of each line
	 * @param indent
	 *            the indent before stack trace elements
	 * @param exception
	 *            the exception containing the stack trace
	 * @param sb
	 *            the sink to write to
	 * @throws IOException
	 *             if the sink could not be written to
	 */
	public void printStackTrace(final String prefix, final String indent, Throwable exception, final Appendable sb) throws IOException;
}
