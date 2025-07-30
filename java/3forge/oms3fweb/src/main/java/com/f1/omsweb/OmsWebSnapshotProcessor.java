package com.f1.omsweb;

import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fixomsclient.OmsClientNotification;
import com.f1.suite.web.WebState;

public class OmsWebSnapshotProcessor extends BasicProcessor<ResultMessage<OmsClientNotification>, WebState> implements PartitionResolver<ResultMessage<OmsClientNotification>> {

	public OutputPort<OmsClientNotification> output = newOutputPort(OmsClientNotification.class);

	public OmsWebSnapshotProcessor() {
		super((Class) ResultMessage.class, WebState.class);
		setPartitionResolver(this);
	}

	@Override
	public void processAction(ResultMessage<OmsClientNotification> action, WebState state, ThreadScope threadScope) throws Exception {
		output.send(action.getAction(), state.getPartitionId(), threadScope);
	}

	@Override
	public Object getPartitionId(ResultMessage<OmsClientNotification> action) {
		return action.getRequestMessage().getCorrelationId();
	}
}
