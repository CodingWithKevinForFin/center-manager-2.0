package com.f1.suite.utils;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;

public class RequestToMessageProcessor<REQ extends Message, STATE extends State, RES extends Message> extends BasicRequestProcessor<REQ, STATE, RES> {

	final public OutputPort<REQ> output;

	public RequestToMessageProcessor(Class<REQ> innerActionType, Class<STATE> stateType, Class<RES> responseType) {
		super(innerActionType, stateType, responseType);
		output = newOutputPort(innerActionType);
	}

	@Override
	protected RES processRequest(RequestMessage<REQ> action, STATE state, ThreadScope threadScope) throws Exception {
		output.send(action.getAction(), threadScope);
		return nw(getResponseType());
	}

}
