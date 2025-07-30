package com.f1.container.impl;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.base.ValuedListenable;
import com.f1.container.State;

@VID("F1.CN.PR")
public interface PersistenceRoot extends Message, ValuedListenable {

	@PID(1)
	Object getPartitionId();
	void setPartitionId(Object partitionId);

	@PID(2)
	Class<? extends State> getType();
	void setType(Class<? extends State> type);

	@PID(3)
	public ValuedListenable getPersistedRoot();
	public void setPersistedRoot(ValuedListenable root);

	@PID(4)
	Class<? extends State> getStateType();
	void setStateType(Class<? extends State> type);

}
