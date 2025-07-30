/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.flogger;

import java.io.IOException;

public interface FloggerFormatter {

	/**
	 * convert a log event into a human readable string.
	 * 
	 * @param sink
	 *            sink to have text appended to
	 * @param loggerId
	 *            id of the logger that was called
	 * @param txnId
	 *            the transactionid of the flogger (see
	 *            {@link Flogger#getTransactionId()}
	 * @param message
	 *            message to log
	 * @param level
	 *            level to log at. (should be translated back to the logger
	 *            specific implementation)
	 * @param extras
	 *            extra info to log(typically a {@link Throwable})
	 * @param timeMs
	 *            when log was called in milliseconds
	 * @param ste
	 *            the trace element of where the log was called
	 * @param thread
	 *            the thread that called log
	 * @throws IOException
	 *             if the supplied sink could not be appended to
	 */
	void append(StringBuilder sink, String loggerId, String txnId, Object message, int level, Object extras, long timeMs, StackTraceElement ste, Thread thread)
			throws IOException;

}
