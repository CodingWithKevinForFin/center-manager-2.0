package com.f1.container;

import com.f1.base.Action;
import com.f1.container.impl.BasicProcessor;

/**
 * This used to handle exceptions thrown by {@link Processor}s. Please note, that [re]throwing and exception is permissible, & will be handled in a default
 * manner by the container framework. see {@link ContainerServices#setDefaultThrowableHandler(ThrowableHandler)} and
 * {@link BasicProcessor#setThrowableHandler(ThrowableHandler)}
 * 
 */
public interface ThrowableHandler<A extends Action, S extends State> {
	/**
	 * called by the f1 framework when an exceptions are thrown by {@link Processor}s. Please note, that [re]throwing and exception is permissible, & will be
	 * handled in a default manner by the container framework
	 * 
	 * @param p
	 *            processor that threw the exception (may be null)
	 * @param a
	 *            action that threw the exception (may be null)
	 * @param s
	 *            state that threw the exception (may be null)
	 * @param t
	 *            threadscope that threw the exception (may be null)
	 * @param thrown
	 *            exception that was thrown (and should be evaluated by this method for further action)
	 * @throws Throwable
	 *             may [re]throw the exception
	 */
	void handleThrowable(Processor<? extends A, ? extends S> p, A a, S s, ThreadScope t, Throwable thrown) throws Throwable;

}
