package com.f1.sample;

import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;

public class SampleReplyProcessor extends BasicRequestProcessor<SampleMessage, State, SampleResponseMessage> {

	public SampleReplyProcessor(PartitionResolver<SampleMessage> inner) {
		super(SampleMessage.class, State.class, SampleResponseMessage.class, inner);
	}

	@Override
	protected SampleResponseMessage processRequest(RequestMessage<SampleMessage> request, State state, ThreadScope threadScope) {
		System.out.println(getClass().getName() + ": " + request.getAction().getText());
		SampleResponseMessage response = nw(SampleResponseMessage.class);
		response.setResponseText("replying to " + request.getAction().getText());
		request.getAction().ack("from the reply processor!");
		return response;
	}

}
