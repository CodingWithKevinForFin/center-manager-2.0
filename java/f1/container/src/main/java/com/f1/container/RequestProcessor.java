/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.base.Action;

/**
 * A special purpose {@link Processor} which receives {@link RequestAction} instead of the typical {@link Action}.
 */
public interface RequestProcessor<A extends Action, S extends State, R extends Action> extends Processor<RequestMessage<A>, S> {

	/**
	 * @return the type of message in the payload of the request
	 */
	Class<A> getRequestType();
	Class<R> getResponseType();

	RequestInputPort<A, R> getInputPort();
}
