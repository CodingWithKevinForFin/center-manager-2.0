/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.Action;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.Connectable;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.utils.OH;

public class BasicRequestOutputPort<A extends Action, RES extends Action> extends BasicOutputPort<RequestMessage<A>> implements RequestOutputPort<A, RES> {

	final private Class<A> requestType;
	final private Class<RES> responseType;
	final private BasicOutputPort<ResultMessage<RES>> resultPort;

	// input of a processor
	@SuppressWarnings("unchecked")
	public BasicRequestOutputPort(Class<A> requestType, Class<RES> responseType, Connectable parent) {
		super((Class) RequestMessage.class, parent);
		OH.assertNotNull(requestType);
		OH.assertNotNull(responseType);
		this.requestType = requestType;
		this.responseType = responseType;
		this.resultPort = new BasicOutputPort<ResultMessage<RES>>((Class) ResultMessage.class, parent);
		this.resultPort.setConnectionOptional(true);
		initName();
	}

	@Override
	public ResultActionFuture<RES> requestWithFuture(A a, ThreadScope threadScope) {
		return requestWithFuture(a, null, threadScope);
	}

	@Override
	public ResultActionFuture<RES> requestWithFuture(A a, Object partitionId, ThreadScope threadScope) {
		RequestMessage<A> req = makeRequest(a);
		req.setFuture(getContainer().getResultActionFutureController().createFuture(this));
		if (resultPort.isConnected())
			req.setResultPort(resultPort);
		send(req, partitionId, threadScope);
		return (ResultActionFuture<RES>) req.getFuture();
	}

	@Override
	public ResultActionFuture<RES> requestWithFuture(A a, Object partitionId, Object correlationId, ThreadScope threadScope) {
		RequestMessage<A> req = makeRequest(a);
		req.setFuture(getContainer().getResultActionFutureController().createFuture(this));
		req.setCorrelationId(correlationId);
		if (resultPort.isConnected())
			req.setResultPort(resultPort);
		send(req, partitionId, threadScope);
		return (ResultActionFuture<RES>) req.getFuture();
	}

	@Override
	public void request(A a, ThreadScope ts) {
		request(a, null, ts);
	}

	@Override
	public void request(A a, Object partitionId, ThreadScope threadScope) {
		RequestMessage<A> req = makeRequest(a);
		if (resultPort.isConnected())
			req.setResultPort(resultPort);
		send(req, partitionId, threadScope);
	}

	@Override
	public void request(A a, Object partitionId, Object correlationId, ThreadScope threadScope) {
		RequestMessage<A> req = makeRequest(a);
		req.setCorrelationId(correlationId);
		if (resultPort.isConnected())
			req.setResultPort(resultPort);
		send(req, partitionId, threadScope);
	}

	protected RequestMessage<A> makeRequest(A action) {
		if (requestActionGenerator == null)
			requestActionGenerator = (ObjectGeneratorForClass) getGenerator(RequestMessage.class);
		RequestMessage<A> r = requestActionGenerator.nw();
		r.setAction(action);
		return r;
	}

	private ObjectGeneratorForClass<RequestMessage<A>> requestActionGenerator;

	@Override
	public Class<A> getRequestActionType() {
		return requestType;
	}

	@Override
	public Class<RES> getResponseActionType() {
		return responseType;
	}

	@Override
	public BasicRequestOutputPort<A, RES> setName(String name) {
		super.setName(name);
		return this;
	}

	protected void initName() {
		if (requestType != null)
			setName(requestType.getSimpleName() + "RequestOutputPort");
	}

	@Override
	public OutputPort<ResultMessage<RES>> getResponsePort() {
		return resultPort;
	}

}
