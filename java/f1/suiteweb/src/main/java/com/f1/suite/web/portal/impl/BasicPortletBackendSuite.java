package com.f1.suite.web.portal.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicSuite;
import com.f1.povo.standard.SubscribeMessage;
import com.f1.povo.standard.TimestampedMessage;
import com.f1.povo.standard.UnsubscribeMessage;
import com.f1.suite.utils.SubscriberSuite;
import com.f1.suite.web.WebState;
import com.f1.suite.web.portal.PortletBackend;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.VH;
import com.f1.utils.structs.Tuple2;

public class BasicPortletBackendSuite extends BasicSuite implements PortletBackend {

	final private Map<String, OutputPort<?>> ports = new HashMap<String, OutputPort<?>>();
	final public OutputPort<TimestampedMessage> toBackendDropCopy = newOutputPort(TimestampedMessage.class);
	final private Map<String, RequestOutputPort<?, Message>> requestPorts = new HashMap<String, RequestOutputPort<?, Message>>();
	final private SubscriberSuite<Message> subscriberSuite;
	final public InputPort<Message> fromBackendInputPort;
	final private FromBackendProcessor fromBackendProcessor;
	private FromBackendResponseProcessor fromBackendResponseProcessor;
	private OutputPort<ResultMessage<Action>> fromBackendResponsePort;

	public BasicPortletBackendSuite() {
		this.toBackendDropCopy.setConnectionOptional(true);
		this.subscriberSuite = new SubscriberSuite<Message>(Message.class);
		this.fromBackendProcessor = new FromBackendProcessor();
		this.fromBackendResponseProcessor = new FromBackendResponseProcessor();
		addChildren(this.subscriberSuite, fromBackendProcessor, fromBackendResponseProcessor);
		fromBackendResponsePort = exposeInputPortAsOutput(fromBackendResponseProcessor, true);
		this.fromBackendInputPort = exposeInputPort(subscriberSuite.inputPort);
		wire(subscriberSuite.outputPort, fromBackendProcessor, true);
		subscriberSuite.applyPartitionResolver(new BasicPartitionResolver<Action>(Action.class, "PORTLET_BACKEND"), true, true);
	}

	@Override
	public void sendMessageToBackend(String backendServiceId, String partitionId, Action m) {
		OutputPort port = CH.getOrThrow(ports, backendServiceId, "backend port");
		port.send(m, null);
		handleDropCopy(backendServiceId, partitionId, m);
	}

	private void handleDropCopy(String backendServiceId, String partitionId, Action m) {
		if (toBackendDropCopy.isConnected()) {
			TimestampedMessage tsm = nw(TimestampedMessage.class);
			tsm.setAction(m);
			tsm.setProcessUid(EH.getProcessUid());
			tsm.setPartitionId(partitionId);
			tsm.setTimestampNanos(getTools().getNowNano());
			tsm.setNotes(backendServiceId);
			toBackendDropCopy.send(tsm, null);
		}

	}

	@Override
	public void sendRequestToBackend(String backendServiceId, String partitionId, Object correlationId, Action m) {
		RequestOutputPort port = (RequestOutputPort) CH.getOrThrow(requestPorts, backendServiceId, "backend request port");
		RequestMessage requestMessage = nw(RequestMessage.class);
		requestMessage.setAction(m);
		requestMessage.setCorrelationId(new Tuple2<String, Object>(partitionId, correlationId));
		requestMessage.setResultPort(fromBackendResponsePort);
		port.send(requestMessage, null);
		handleDropCopy(backendServiceId, partitionId, m);
	}

	public <M extends Message> OutputPort<M> newOutputPort(String id, Class<M> type) {
		assertNotStarted();
		OutputPort<M> r = newOutputPort(type);
		CH.putOrThrow(ports, id, r);
		return r;
	}

	public <M extends Message> RequestOutputPort<M, Message> newRequestOutputPort(String id, Class<M> type) {
		assertNotStarted();
		RequestOutputPort<M, Message> r = newRequestOutputPort(type, Message.class);
		CH.putOrThrow(requestPorts, id, r);
		return r;
	}

	@Override
	public void subscribe(String partitionId) {
		SubscribeMessage msg = nw(SubscribeMessage.class);
		msg.setPartitionId(partitionId);
		subscriberSuite.subscribePort.dispatch(msg);
	}

	@Override
	public void unsubscribe(String partitionId) {
		UnsubscribeMessage msg = nw(UnsubscribeMessage.class);
		msg.setPartitionId(partitionId);
		subscriberSuite.unsubscribePort.dispatch(msg);
	}

	public static class FromBackendProcessor extends BasicProcessor<Action, WebState> {

		public FromBackendProcessor() {
			super(Action.class, WebState.class);
		}

		@Override
		public void processAction(Action action, WebState state, ThreadScope threadScope) throws Exception {
			if (state == null) {
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "Ignoring backend Message for unknown state: ", OH.getClassName(action));
				return;
			}
			PortletManager manager = state.getPortletManager();
			if (manager != null)
				manager.onBackendAction(action);
			else
				LH.info(log, "manager not found for session, may be due to a logout: ", state.getPartitionId(), ", dropping: " + VH.getSimpleClassName(action));
		}

	}

	public static class FromBackendResponseProcessor extends BasicProcessor<ResultMessage<Action>, WebState> implements PartitionResolver<ResultMessage<Action>> {

		public FromBackendResponseProcessor() {
			super((Class) ResultMessage.class, WebState.class);
			setPartitionResolver(this);
		}

		@Override
		public void processAction(ResultMessage<Action> action, WebState state, ThreadScope threadScope) throws Exception {
			if (state == null) {
				LH.info(log, "Ignoring backend Message for unknown state: ", OH.getClassName(action));
				return;
			}
			PortletManager manager = state.getPortletManager();
			if (manager != null)
				manager.onBackendResponse(action, ((Tuple2<String, Object>) action.getRequestMessage().getCorrelationId()).getB());
			else
				LH.info(log, "manager not found for session, may be due to a logout: ", state.getPartitionId(),
						", dropping response: " + VH.getSimpleClassName(action.getActionNoThrowable()));
		}

		@Override
		public Object getPartitionId(ResultMessage<Action> action) {
			Tuple2<String, Object> t = (Tuple2<String, Object>) action.getRequestMessage().getCorrelationId();
			return t.getA();
		}

	}

	@Override
	public void sendMessageToPortletManager(String partitionId, Action nw, long delayMs) {
		this.getContainer().getDispatchController().dispatch(null, this.fromBackendProcessor, nw, partitionId, delayMs, null);
	}

}
