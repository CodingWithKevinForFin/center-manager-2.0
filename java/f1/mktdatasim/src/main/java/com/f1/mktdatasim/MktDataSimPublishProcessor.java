package com.f1.mktdatasim;

import java.util.List;
import java.util.Map;

import com.f1.container.OutputPort;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.mktdata.MktDataListener;
import com.f1.mktdata.MktDataManager;
import com.f1.pofo.mktdata.LevelOneData;

public class MktDataSimPublishProcessor extends BasicProcessor<LevelOneData, State> {

	public final OutputPort<LevelOneData> output = newOutputPort(LevelOneData.class);
	private List<MktDataListener> listeners;
	private Map<Integer, Integer> subscribed;

	public MktDataSimPublishProcessor() {
		super(LevelOneData.class, State.class);
		output.setConnectionOptional(true);
	}

	@Override
	public void processAction(LevelOneData action, State state, ThreadScope threadScope) throws Exception {
		if (subscribed != null && !subscribed.containsKey(action.getSecurityRefId()))
			return;
		for (MktDataListener l : listeners)
			l.onLevelOneData((MktDataManager) getParent(), action);
		if (output.isConnected())
			output.send(action, threadScope);
	}

	public void setListeners(List<MktDataListener> listeners) {
		assertNotStarted();
		this.listeners = listeners;
	}

	public void setSubscribed(Map<Integer, Integer> subscribed) {
		this.subscribed = subscribed;

	}

}
