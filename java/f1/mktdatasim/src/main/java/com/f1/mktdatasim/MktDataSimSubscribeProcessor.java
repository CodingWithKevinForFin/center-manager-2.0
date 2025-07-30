package com.f1.mktdatasim;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.mktdata.LevelOneData;
import com.f1.pofo.mktdata.LevelOneSubscribeRequest;

public class MktDataSimSubscribeProcessor extends BasicProcessor<LevelOneSubscribeRequest, MktDataSimState> {

	OutputPort<Message> onSubscribe = newOutputPort(Message.class);
	OutputPort<LevelOneData> output = newOutputPort(LevelOneData.class);

	public MktDataSimSubscribeProcessor() {
		super(LevelOneSubscribeRequest.class, MktDataSimState.class);
	}

	@Override
	public void processAction(LevelOneSubscribeRequest action, MktDataSimState state, ThreadScope threadScope) throws Exception {
		int name = action.getSecurityRefId();
		MktDataSimNameSettings config = state.getConfiguration().get(name);
		if (config == null) {
			LevelOneData a = nw(LevelOneData.class);
			a.setSecurityRefId(name);
			a.setLastStatus(LevelOneData.STATUS_REJECTED);
			output.send(a, threadScope);
		} else {
			if (!state.getSubscribed().containsKey(name))
				state.getSubscribed().put(name, config);
			Double openPrice = state.getOpenPrices().get(name);
			if (openPrice != null) {
				LevelOneData a = nw(LevelOneData.class);
				a.setSecurityRefId(name);
				a.setLastStatus(LevelOneData.STATUS_OPENED);
				a.setOpenPrice(openPrice);
				System.out.println("Sending opening price to OFR for " + name);
				output.send(a, threadScope);
			}
			if (!state.isRunning()) {
				state.start(getTools().getNow());
				onSubscribe.send(nw(Message.class), threadScope);
			}
		}
	}
}
