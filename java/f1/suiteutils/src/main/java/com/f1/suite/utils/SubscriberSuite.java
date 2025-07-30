package com.f1.suite.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.RequestInputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicState;
import com.f1.container.impl.BasicSuite;
import com.f1.povo.standard.SubscribeMessage;
import com.f1.povo.standard.UnsubscribeMessage;

public class SubscriberSuite<M extends Message> extends BasicSuite {

	final public InputPort<SubscribeMessage> subscribePort;
	final public InputPort<UnsubscribeMessage> unsubscribePort;
	final public InputPort<M> inputPort;
	final public RequestInputPort<SubscribeMessage, Message> subscribeRequestPort;
	final public RequestInputPort<UnsubscribeMessage, Message> unsubscribeRequestPort;
	final public OutputPort<M> outputPort;

	final private SubscribeProcessor subscribeProcessor;
	final private UnsubscribeProcessor unsubscribeProcessor;
	final private RequestToMessageProcessor<SubscribeMessage, SubscriberState, Message> subscribeRequestProcessor;
	final private RequestToMessageProcessor<UnsubscribeMessage, SubscriberState, Message> unsubscribeRequestProcessor;
	final private PublishProcessor<M> publishProcessor;

	public SubscriberSuite(Class<M> type) {
		this.publishProcessor = new PublishProcessor<M>(type);
		this.subscribeProcessor = new SubscribeProcessor();
		this.unsubscribeProcessor = new UnsubscribeProcessor();
		this.subscribeRequestProcessor = new RequestToMessageProcessor<SubscribeMessage, SubscriberState, Message>(SubscribeMessage.class, SubscriberState.class, Message.class);
		this.unsubscribeRequestProcessor = new RequestToMessageProcessor<UnsubscribeMessage, SubscriberState, Message>(UnsubscribeMessage.class, SubscriberState.class,
				Message.class);
		addChildren(subscribeProcessor, unsubscribeProcessor, subscribeRequestProcessor, unsubscribeRequestProcessor, publishProcessor);
		wire(subscribeRequestProcessor.output, subscribeProcessor, false);
		wire(unsubscribeRequestProcessor.output, unsubscribeProcessor, false);
		this.subscribePort = this.exposeInputPort(this.subscribeProcessor);
		this.unsubscribePort = this.exposeInputPort(this.unsubscribeProcessor);
		this.subscribeRequestPort = this.exposeInputPort(this.subscribeRequestProcessor);
		this.unsubscribeRequestPort = this.exposeInputPort(this.unsubscribeRequestProcessor);
		this.inputPort = exposeInputPort(publishProcessor);
		this.outputPort = exposeOutputPort(publishProcessor.output);
	}

	public static class SubscriberState extends BasicState {
		public Set<Object> partitionIds = new LinkedHashSet<Object>();
	}

	public static class SubscribeProcessor extends BasicProcessor<SubscribeMessage, SubscriberState> {
		public SubscribeProcessor() {
			super(SubscribeMessage.class, SubscriberState.class);
		}

		@Override
		public void processAction(SubscribeMessage action, SubscriberState state, ThreadScope threadScope) throws Exception {
			state.partitionIds.add(action.getPartitionId());
		}
	}

	public static class UnsubscribeProcessor extends BasicProcessor<UnsubscribeMessage, SubscriberState> {
		public UnsubscribeProcessor() {
			super(UnsubscribeMessage.class, SubscriberState.class);
		}

		@Override
		public void processAction(UnsubscribeMessage action, SubscriberState state, ThreadScope threadScope) throws Exception {
			state.partitionIds.remove(action.getPartitionId());
		}
	}

	public static class PublishProcessor<M extends Message> extends BasicProcessor<M, SubscriberState> {

		public final OutputPort<M> output;

		public PublishProcessor(Class<M> actionType) {
			super(actionType, SubscriberState.class);
			output = newOutputPort(actionType);
		}

		@Override
		public void processAction(M action, SubscriberState state, ThreadScope threadScope) throws Exception {
			for (Object partitionId : state.partitionIds)
				output.send(action, partitionId, threadScope);
		}

	}
}
