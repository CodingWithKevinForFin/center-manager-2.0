/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Action;
import com.f1.base.Valued;
import com.f1.container.OutputPort;
import com.f1.container.Port;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ProcessorException;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.CH;

public class ParamRoutingProcessor<A extends Action> extends BasicProcessor<A, State> {

	private String paramName;
	private byte pid = Valued.NO_PID;
	private Map<Object, OutputPort<A>> ports = new HashMap<Object, OutputPort<A>>();
	private OutputPort<A> defaultPort;

	public ParamRoutingProcessor(Class<A> actionType) {
		super(actionType, State.class);
	}

	public ParamRoutingProcessor(Class<A> actionType, String paramName) {
		super(actionType, State.class);
		this.paramName = paramName;
	}

	public ParamRoutingProcessor(Class<A> actionType, byte pid) {
		super(actionType, State.class);
		this.pid = pid;
	}

	@Override
	public void processAction(A action, State state, ThreadScope threadScope) {
		Action action2 = action;
		Object value = pid == Valued.NO_PID ? action2.ask(paramName) : action2.ask(pid);
		OutputPort<A> port = ports.get(value);
		if (port != null)
			port.send(action, threadScope);
		else if (defaultPort != null)
			defaultPort.send(action, threadScope);
	}

	public Port<A> getDefaultOutputPort() {
		return defaultPort;
	}

	public void setDefaultOutputPort(OutputPort<A> defaultPort) {
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

	public OutputPort<A> addOutputPortForValue(Object actionType) {
		assertNotStarted();
		OutputPort<A> r = newOutputPort(getActionType());
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
