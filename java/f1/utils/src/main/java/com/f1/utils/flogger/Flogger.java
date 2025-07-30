package com.f1.utils.flogger;

import java.util.logging.Logger;

/**
 * The {@link Flogger} can be though of as a transactional sequence of log
 * events, which at any point can be captured for posterity or discarded. It
 * interacts with existing loggers such as {@link Logger} and
 * {@link SpeedLogger} and log4j.
 * <P>
 * The advantage of using a flogger over a standard logger(or even worse,
 * stdout/err) is severalfold: <BR>
 * 1. In a multi-threaded environment with many transactions overlapping in
 * time, log statements will have a tendancy to get intermingled, making it
 * difficult to sort out which log event is for which transaction(often solved
 * by constantly prefixing each log statement with some indicator, such as
 * orderid or account name and then grepping) <BR>
 * 2. It can help to greatly reduce log files, discarding uninformative
 * information for routine processing (cases void of exceptions or logical
 * errors) <BR>
 * 3. Can increase capturing important trace level events in exceptional cases.<BR>
 * 4. Using the transaction Id, users have a reference point for communicating
 * issues. For example, system A sends a message to system B. System B has an
 * issue processing the message so it 'flogs' the details to the log file and
 * sends the associated flogger transaction id back to system A. Now the users
 * of system A and system B can have a conversation and quickly identify the
 * issue <BR>
 * 5. The flogger itself can be used to determine if any intersting events took
 * place by examining {@link Flogger#hasAtleast(int)}. For example, instead of
 * having helper methods through expensive exceptions, they could be fitted <BR>
 * 6. Supports an adaption framework for adapting to a variety of logger
 * implementations (custom ones can also be registered using
 * {@link FloggerManager#registerFloggerAdapter(FloggerAdapter)}. This means
 * business logic does not need to be coded against a particular logging
 * implementation. Note the {@link #log(Object, boolean, int, Object, Object)}
 * method takes a simple {@link Object} as the logger, meaning anything that can
 * be adapted using the {@link FloggerAdapter} can be supplied.
 * <P>
 * Disadvantages: <BR>
 * 1. Slower than logging directly because of the extra layer. <BR>
 * 2. Durring a logger session all 'loggable' events are stored in memory, so
 * care should be taken to not log massive 2. If the system were to exit, log
 * statements stored in memory and not passed through to the underlying
 * framework may be lost.Note, a shutdown hook to capture in flight transactions
 * is in progress<BR>
 * messages / long durations.
 * <P>
 * A logger will typically proceed through the following steps:<BR>
 * 1. a logger would be instantiated at the beginning of some logical
 * transaction using the {@link FloggerManager#createFlogger(String)} method.
 * Each flogger, should have a relatively unique (and human readable)
 * transaction id. <BR>
 * 2.As events of varying importance occur they can be logged to the flogger
 * using the various Flogger methods, such as
 * {@link Flogger#debug(Object, boolean, Object)} and
 * {@link Flogger#info(Object, boolean, Object)}. See
 * {@link Flogger#log(Object, boolean, int, Object, Object)} method for details
 * on usage. If the level of the log statement is greater or equal to that of
 * the 'innerlogger' then the log event will be captured within the flogger. <BR>
 * 3. At any point, but typically at the end of the transaction the condition of
 * the transaction is evaluated and the implementer can choose to capture or
 * discard the flogger's events. This can be done using several differnt
 * methods, providing different flavors and details. These include<BR>
 * &nbsp;&nbsp;&nbsp;{@link #toString()}<BR>
 * &nbsp;&nbsp;&nbsp;{@link #toDetailedString()}<BR>
 * &nbsp;&nbsp;&nbsp;{@link #toSummaryString()}<BR>
 * &nbsp;&nbsp;&nbsp;{@link #getTransactionId()}<BR>
 */
public interface Flogger {
	/**
	 * specifies maximum length of a string returned by
	 * {@link #toSummaryString()}
	 */
	int DEFAULT_SUMMARY_MAX_LENGTH = 255;

	/** see {@link #all(Object, boolean, Object, Object)} */
	int ALL = 0;
	/** see {@link #trace(Object, boolean, Object, Object)} */
	int TRACE = 10;
	/** see {@link #finest(Object, boolean, Object, Object)} */
	int FINEST = 20;
	/** see {@link #finer(Object, boolean, Object, Object)} */
	int FINER = 30;
	/** see {@link #fine(Object, boolean, Object, Object)} */
	int FINE = 40;
	/** see {@link #debug(Object, boolean, Object, Object)} */
	int DEBUG = 50;
	/** see {@link #config(Object, boolean, Object, Object)} */
	int CONFIG = 60;
	/** see {@link #info(Object, boolean, Object, Object)} */
	int INFO = 70;
	/** see {@link #warning(Object, boolean, Object, Object)} */
	int WARNING = 80;
	/** see {@link #error(Object, boolean, Object, Object)} */
	int ERROR = 90;
	/** see {@link #severe(Object, boolean, Object, Object)} */
	int SEVERE = 100;
	/** see {@link #fatal(Object, boolean, Object, Object)} */
	int FATAL = 110;

	/** see {@link #off(Object, boolean, Object, Object)} */
	int OFF = 120;

	/**
	 * @return a (preferably human readable) id uniquely identifying this logger
	 *         event. This would be the transaction id supplied to the manager
	 *         via {@link FloggerManager#createFlogger(String)}
	 */
	String getTransactionId();

	/**
	 * Consider a message (coupled with an optional extra object) for logging.
	 * If the supplied level is equal or greater than the log level of the
	 * supplied inner logger, then the message is captured. In addition, if
	 * passThroughToInner is true then the message and extra object are passed
	 * to the inner logger
	 * 
	 * @param innerLogger
	 *            the inner logger (for example an instance of a {@link Logger}.
	 *            See implementations of {@link FloggerAdapter} for supported
	 *            loggers. Also, see
	 *            {@link FloggerManager#registerFloggerAdapter(FloggerAdapter)}
	 *            for logging to non-standard loggers
	 * @param passThroughToInner
	 *            if true and the level meets the loggers criteria, then pass
	 *            the message and extra to the innerLogger
	 * @param level
	 *            level of the message to log (this is comparied against the
	 *            level of the innerlogger as defined by
	 *            {@link FloggerAdapter#getLevel(Object)}
	 * @param message
	 *            the message to log (see {@link String#valueOf(Object)})
	 * @param extra
	 *            the extra information to log, typically this is an
	 *            {@link Throwable}
	 */
	void log(Object innerLogger, boolean passThroughToInner, int level, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #ALL}
	 */
	void all(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #TRACE}
	 */
	void trace(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #FINEST}
	 */
	void finest(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #FINER}
	 */
	void finer(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #FINE}
	 */
	void fine(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #DEBUG}
	 */
	void debug(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #CONFIG}
	 */
	void config(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #INFO}
	 */
	void info(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #WARNING}
	 */
	void warning(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #ERROR}
	 */
	void error(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #SEVERE}
	 */
	void severe(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #FATAL}
	 */
	void fatal(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #OFF}
	 */
	void off(Object innerLogger, boolean passThroughToInner, Object message, Object extra);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #ALL} and extra is null
	 */
	void all(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #TRACE} and extra is null
	 */
	void trace(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #FINEST} and extra is null
	 */
	void finest(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #FINER} and extra is null
	 */
	void finer(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #FINE} and extra is null
	 */
	void fine(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #DEBUG} and extra is null
	 */
	void debug(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #CONFIG} and extra is null
	 */
	void config(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #INFO} and extra is null
	 */
	void info(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #WARNING} and extra is null
	 */
	void warning(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #ERROR} and extra is null
	 */
	void error(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #SEVERE} and extra is null
	 */
	void severe(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #FATAL} and extra is null
	 */
	void fatal(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * see {@link #log(Object, boolean, int, Object, Object)} where level is
	 * {@link #OFF} and extra is null
	 */
	void off(Object innerLogger, boolean passThroughToInner, Object message);

	/**
	 * returns true iff any event meeting or exceeding the supplied floggerLevel
	 * has been called to this logger. Note that this is regardless of whether
	 * the event was actually logged / captured. Will always return true if
	 * supplied level is {@link #ALL}
	 * 
	 * @param floggerLevel
	 *            flogger level to compare with past log calls
	 * @return true iff said conditions are met
	 */
	boolean hasAtleast(int floggerLevel);

	/**
	 * @return see {@link #toSummaryString()}
	 */
	@Override
	String toString();

	/**
	 * @return a multi-lined 'detailed' description of all captured events.
	 *         Generally, the results would be captured in a log file or some
	 *         persistent store
	 */
	String toDetailedString();

	/**
	 * see {@link #toDetailedString()}
	 * 
	 * @param formatter
	 *            formatter used for formatting the log events
	 * @param sink
	 *            will be populated with the text
	 * @return supplied sink for convenience
	 */
	StringBuilder toDetailedString(FloggerFormatter formatter, StringBuilder sink);

	/**
	 * @return a single line, summarized description including the transaction
	 *         id and the most interesting event (that w/ the highest log level)
	 *         . Generally, this would be included in some sort of user message
	 *         to provide a hint as to what the problem is.
	 */
	String toSummaryString(int maxLength);

	/**
	 * @see Flogger#toSummaryString(int)
	 * @see {@link #DEFAULT_SUMMARY_MAX_LENGTH}
	 */
	String toSummaryString();
}
