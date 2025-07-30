package com.f1.sample;

import com.f1.container.OutputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;

public class SampleChainingProcessor extends BasicProcessor<SampleMessage, State> {

	public final OutputPort<SampleMessage> out = newOutputPort(SampleMessage.class);
	public final RequestOutputPort<SampleMessage, SampleResponseMessage> out2 = newRequestOutputPort(SampleMessage.class, SampleResponseMessage.class);
	public final OutputPort<ResultMessage<SampleResponseMessage>> resultOut = out2.getResponsePort();

	public SampleChainingProcessor() {
		super(SampleMessage.class, State.class);
	}

	@Override
	public void processAction(SampleMessage action, State state, ThreadScope threadScope) {
		// debug
		System.out.println(getClass().getName() + ": " + action.getText());

		// mutate the incoming message
		action.setText(action.getText() + ",appending some stuff");

		// create a brand new message
		SampleMessage action2 = nw(SampleMessage.class);
		action2.setText("A new message from the chaining processor, inspired by " + action.getText());

		// send a request & wait for the response
		out.send(action, threadScope);
		ResultActionFuture<SampleResponseMessage> response = out2.requestWithFuture(action2, threadScope);
		System.out.println(getClass().getName() + " result is : " + response.getResult().getAction().getResponseText());

		SampleMessage action3 = nw(SampleMessage.class);
		action3.setText("A second message from the chaining processor, inspired by " + action.getText());
		action.transferAckerTo(action3);
		out2.requestWithFuture(action3, threadScope);
	}
}
