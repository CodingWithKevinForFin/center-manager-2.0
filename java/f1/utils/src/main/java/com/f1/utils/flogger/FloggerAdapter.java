package com.f1.utils.flogger;

import java.util.logging.Logger;

/**
 * used to adapt various logger implementation into a common method of access
 * 
 * @param <T>
 *            type of underlying logger
 */

public interface FloggerAdapter<T> {

	/**
	 * @return the type of logger adapted
	 */
	Class<T> getLoggerType();

	/**
	 * test for adaptability
	 * 
	 * @param innerLogger
	 *            the object to test for adaptability
	 * @return true if this instance can be used to wrap the supplied logger
	 */
	boolean canAdapt(Object innerLogger);

	/**
	 * @param innerLogger
	 *            logger to inspect for a log level
	 * @return the adapted log level to a level defined in {@link Flogger}. See
	 *         {@link Logger#getLevel()} for an example
	 */
	int getLevel(T innerLogger);

	/**
	 * the id representing the supplied logger.
	 * 
	 * @param innerLogger
	 *            logger to inspect for an id
	 * @return id. See {@link Logger#getName()} for an example of an id on a
	 *         logger
	 */
	String getId(T innerLogger);

	/**
	 * log the given data to the internal logger.
	 * 
	 * @param innerLogger
	 *            logger to log to
	 * @param level
	 *            level to log at. (should be translated back to the logger
	 *            specific implementation)
	 * @param txnId
	 *            the transactionid of the flogger (see
	 *            {@link Flogger#getTransactionId()}
	 * @param message
	 *            message to log
	 * @param timeOfLog
	 *            when log was called in milliseconds
	 * @param now
	 *            current time in milliseconds
	 * @param ste
	 *            the trace element of where the log was called
	 * @param extra
	 *            extra info to log(typically a {@link Throwable})
	 */
	void log(T innerLogger, int level, String txnId, String message, long timeOfLog, long now, StackTraceElement ste, Object extra);
}
