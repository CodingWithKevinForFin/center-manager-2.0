package com.f1.mktdatasim;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.mktdata.LevelOneUnsubscribeRequest;

public class MktDataSimUnsubscribeProcessor extends BasicProcessor<LevelOneUnsubscribeRequest, MktDataSimState> {

	public MktDataSimUnsubscribeProcessor() {
		super(LevelOneUnsubscribeRequest.class, MktDataSimState.class);
	}

	@Override
	public void processAction(LevelOneUnsubscribeRequest action, MktDataSimState state, ThreadScope threadScope) throws Exception {
		if (state.getSubscribed().remove(action.getSecurityRefId()) != null) {
			if (!state.getSubscribed().isEmpty()) {
				state.stop();
			}
		}
	}
}
