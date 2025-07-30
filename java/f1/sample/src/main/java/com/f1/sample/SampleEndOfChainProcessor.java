package com.f1.sample;

import com.f1.container.Port;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;

public class SampleEndOfChainProcessor extends BasicProcessor<SampleMessage, State> {

	public final Port out = newOutputPort();

	public SampleEndOfChainProcessor() {
		super(SampleMessage.class, State.class);
	}

	@Override
	public void processAction(SampleMessage action, State state, ThreadScope threadScope) {
		// debug
		System.out.println(getClass().getName() + ": " + action.getText());

	}

}
