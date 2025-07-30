package com.f1.fixomsclient;

import java.util.List;

import com.f1.container.OutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.oms.OmsNotification;
import com.f1.utils.LH;

/**
 * 
 * Receives the OMS Snapshot and builds the local SOW. Following that, all pending updates will be applied in the sequence they were received
 * 
 */
public class OmsClientSnapshotResponseProcessor extends BasicProcessor<ResultMessage<OmsNotification>, OmsClientState> {

	final public OutputPort<OmsNotification> output = newOutputPort(OmsNotification.class);

	public OmsClientSnapshotResponseProcessor() {
		super((Class) ResultMessage.class, OmsClientState.class);
	}

	@Override
	public void processAction(ResultMessage<OmsNotification> action, OmsClientState state, ThreadScope threadScope) throws Exception {
		LH.info(log, "Received snapshot response");
		List<OmsNotification> queue = state.snapshotProcessed();
		OmsNotification ocn = action.getAction();
		for (OmsNotification notification : ocn.getSnapshotNotifications()) {
			output.send(notification, threadScope);
		}
		for (OmsNotification m : queue) {
			output.send(m, threadScope);
		}
	}
}
