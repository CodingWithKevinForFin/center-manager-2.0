package com.f1.ami.center;

import com.f1.base.Message;
import com.f1.container.ThreadScope;

public class AmiCenterTimerProcessor extends AmiCenterBasicProcessor<Message> {

	public AmiCenterTimerProcessor() {
		super(Message.class);
	}

	@Override
	public void init() {
		super.init();
	}
	@Override
	public void processAction(Message action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		state.getAmiImdb().fireTimersFromTimerEvent();
		state.onProcessedEventsComplete();
	}
}
