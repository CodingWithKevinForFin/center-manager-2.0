package com.f1.strategy;

import com.f1.container.PartitionResolver;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.standard.TextMessage;

public class OnOmsResponseProcessor extends BasicProcessor<ResultMessage<TextMessage>, StrategyState> implements PartitionResolver<ResultMessage<TextMessage>> {

	private String system;

	public OnOmsResponseProcessor() {
		super((Class) ResultMessage.class, StrategyState.class);
		setPartitionResolver(this);
	}

	@Override
	public void processAction(ResultMessage<TextMessage> action, StrategyState state, ThreadScope threadScope) throws Exception {
		state.getStrategy().onOmsResponse(action.getRequestMessage().getAction(), action.getAction().getText());
	}

	@Override
	public Object getPartitionId(ResultMessage<TextMessage> action) {
		return action.getRequestMessage().getCorrelationId();
	}

}
