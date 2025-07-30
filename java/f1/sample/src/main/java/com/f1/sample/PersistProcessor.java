package com.f1.sample;

import com.f1.base.Action;
import com.f1.container.PartitionResolver;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.GuidHelper;

public class PersistProcessor extends BasicProcessor<Action, State> implements PartitionResolver<Action> {

	public PersistProcessor() {
		super(Action.class, State.class);
		setPartitionResolver(this);
	}

	@Override
	public void processAction(Action action, State state, ThreadScope threadScope) throws Exception {
		SampleAccount account = (SampleAccount) state.getPersistedRoot();
		account.setCreatedOn(getTools().getNowNanoDate());
		account.setId((String) state.getPartition().getPartitionId());
		account.setName("blah");
		account.setAccessCount(1);
		commit(state);
	}

	@Override
	public Object getPartitionId(Action action) {
		return GuidHelper.getGuid();
	}

}
