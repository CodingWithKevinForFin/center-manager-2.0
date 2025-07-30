package com.f1.sample;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.standard.TextMessage;
import com.f1.utils.casters.Caster_Integer;

public class SampleStateProcessor extends BasicProcessor<TextMessage, SampleState> {

	public SampleStateProcessor() {
		super(TextMessage.class, SampleState.class);
		bindToPartition("SAMPLESTATEPARTITION");
	}

	@Override
	public void processAction(TextMessage action, SampleState state, ThreadScope threadScope) throws Exception {
		System.out.println("Sample state count: " + state.incrementCount(Caster_Integer.INSTANCE.cast(action.getText())));
	}

}
