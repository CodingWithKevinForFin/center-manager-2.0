package com.f1.sample;

import com.f1.container.PartitionResolver;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.standard.TextMessage;

public class IncAccessProcessor extends BasicProcessor<TextMessage, State> implements PartitionResolver<TextMessage> {

	public IncAccessProcessor() {
		super(TextMessage.class, State.class);
		setPartitionResolver(this);
	}

	@Override
	public void processAction(TextMessage action, State state, ThreadScope threadScope) throws Exception {
		SampleAccount account = (SampleAccount) state.getPersistedRoot();
		System.out.println("IN  ACCOUNT: " + account);
		account.setModifiedOn(getTools().getNowNanoDate());
		account.setAccessCount(account.getAccessCount() + 1);
		commit(state);// TODO:make this work
		System.out.println("OUT ACCOUNT: " + account);
	}

	@Override
	public Object getPartitionId(TextMessage action) {
		return action.getText();
	}

}
