package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.container.impl.BasicState;
import com.f1.container.impl.BasicSuite;
import com.f1.povo.standard.BatchMessage;
import com.f1.povo.standard.TimestampedMessage;

public class DropcopyCaptureSuite extends BasicSuite {

	final public InputPort<TimestampedMessage> onActionInputPort;
	final public OutputPort<TimestampedMessage> onActionOutputPort;
	final public RequestInputPort<Message, BatchMessage> getDropcopysRequestPort;
	final private OnTimestampedActionProcessor onTimestampActionProcessor;
	final private GetDropcopysProcessor getDropcopysProcessor;

	public DropcopyCaptureSuite() {
		onTimestampActionProcessor = new OnTimestampedActionProcessor();
		getDropcopysProcessor = new GetDropcopysProcessor();
		addChildren(onTimestampActionProcessor, getDropcopysProcessor);
		onTimestampActionProcessor.bindToPartition("DROPCOPY_CAPTURE");
		getDropcopysProcessor.bindToPartition("DROPCOPY_CAPTURE");

		onActionInputPort = exposeInputPort(onTimestampActionProcessor);
		onActionOutputPort = exposeOutputPort(onTimestampActionProcessor.onActionOutputPort);
		getDropcopysRequestPort = exposeInputPort(getDropcopysProcessor);
	}

	public static class OnTimestampedActionProcessor extends BasicProcessor<TimestampedMessage, DropCopyCaptureState> {

		final public OutputPort<TimestampedMessage> onActionOutputPort;

		public OnTimestampedActionProcessor() {
			super(TimestampedMessage.class, DropCopyCaptureState.class);
			onActionOutputPort = newOutputPort(TimestampedMessage.class);
			onActionOutputPort.setConnectionOptional(true);
		}

		@Override
		public void processAction(TimestampedMessage action, DropCopyCaptureState state, ThreadScope threadScope) throws Exception {
			state.messages.add(action);
			if (onActionOutputPort.isConnected())
				onActionOutputPort.send(action, threadScope);
		}

	}

	public static class GetDropcopysProcessor extends BasicRequestProcessor<Message, DropCopyCaptureState, BatchMessage> {

		public GetDropcopysProcessor() {
			super(Message.class, DropCopyCaptureState.class, BatchMessage.class);
		}

		@Override
		protected BatchMessage processRequest(RequestMessage<Message> action, DropCopyCaptureState state, ThreadScope threadScope) throws Exception {
			final BatchMessage r = nw(BatchMessage.class);
			r.setMessages(new ArrayList<Message>(state.messages));
			return r;
		}

	}

	public static class DropCopyCaptureState extends BasicState {
		public List<TimestampedMessage> messages = new ArrayList<TimestampedMessage>();
	}

}
