/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Action;
import com.f1.base.Valued;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ProcessorException;
import com.f1.container.impl.AbstractRequestProcessor;
import com.f1.utils.CH;
import com.f1.utils.LH;

public class ParamRoutingRequestProcessor<A extends Action, RES extends Action> extends AbstractRequestProcessor<A, State, RES> {

	private String paramName;
	private byte pid = Valued.NO_PID;
	private Map<Object, RequestOutputPort<A, RES>> ports = new HashMap<Object, RequestOutputPort<A, RES>>();
	private RequestOutputPort<A, RES> defaultPort;

	public ParamRoutingRequestProcessor(Class<A> actionType, Class<RES> responseType) {
		super(actionType, State.class, responseType);
	}

	public ParamRoutingRequestProcessor(Class<A> actionType, Class<RES> responseType, String paramName) {
		super(actionType, State.class, responseType);
		this.paramName = paramName;
	}

	public ParamRoutingRequestProcessor(Class<A> actionType, Class<RES> responseType, byte pid) {
		super(actionType, State.class, responseType);
		this.pid = pid;
	}

	@Override
	public void processAction(RequestMessage<A> action, State state, ThreadScope threadScope) {
		A action2 = action.getAction();
		Object value = pid == Valued.NO_PID ? action2.ask(paramName) : action2.ask(pid);
		RequestOutputPort<A, RES> port = ports.get(value);
		if (port != null)
			port.send(action, threadScope);
		else if (defaultPort != null)
			defaultPort.send(action, threadScope);
		else
			LH.warning(log, "No route defined for value: ", value);
	}

	public RequestOutputPort<A, RES> getDefaultOutputPort() {
		return defaultPort;
	}

	public void setDefaultOutputPort(RequestOutputPort<A, RES> defaultPort) {
		assertStarted();
		this.defaultPort = defaultPort;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		assertNotStarted();
		this.paramName = paramName;
	}

	public byte getPid() {
		return pid;
	}

	public void setPid(byte pid) {
		assertNotStarted();
		this.pid = pid;
	}

	public RequestOutputPort<A, RES> addOutputPortForValue(Object actionType) {
		assertNotStarted();
		RequestOutputPort<A, RES> r = newRequestOutputPort(getRequestType(), getResponseType());
		CH.putOrThrow(ports, actionType, r);
		return r;
	}

	@Override
	public void start() {
		super.start();
		if (pid == -1 && paramName == null)
			throw new ProcessorException("must supply pid or paramname");
		if (pid != -1 && paramName != null)
			throw new ProcessorException("cant supply both pid and paramname");
	}

}
