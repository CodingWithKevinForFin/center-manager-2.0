package com.f1.container;

import com.f1.base.Action;

public interface RequestInputPort<REQ extends Action, RES extends Action> extends InputPort<RequestMessage<REQ>> {

	/**
	 * @return the type of actions this port expects to receive in the {@link RequestMessage#getAction()}.
	 */
	Class<REQ> getRequestActionType();

	/**
	 * @return the type of actions this port expects to return in the {@link ResultMessage#getAction()}.
	 */
	Class<RES> getResponseActionType();
}
