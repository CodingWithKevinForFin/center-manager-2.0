package com.f1.sample;

import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;

public class SampleResponseProcessor extends BasicProcessor<ResultMessage<SampleResponseMessage>, State> {

	public SampleResponseProcessor() {
		super((Class) ResultMessage.class, State.class);
		bindToPartition("partition1");
	}

	@Override
	public void processAction(ResultMessage<SampleResponseMessage> action, State state, ThreadScope threadScope) {
		// debug
		System.out.println(getClass().getName() + ": " + action.getAction().getResponseText());

	}
}
