package com.f1.container;

import com.f1.base.Action;

public interface ResultInputPort<RES extends Action> extends InputPort<ResultMessage<RES>> {

	/**
	 * @return the type of actions this port expects to return in the {@link ResultMessage#getAction()}.
	 */
	Class<RES> getResponseActionType();
}
