package com.f1.container;

import com.f1.base.Action;

public interface ProcessActionListener {

	void onProcessAction(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch);

	void onProcessActionDone(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch);

	void onHandleThrowable(Processor processor, Partition partition, Action action, State state, ThreadScope thread, Throwable thrown);

	void onQueueAction(Processor processor, Partition partition, Action action, ThreadScope threadScope);
}
