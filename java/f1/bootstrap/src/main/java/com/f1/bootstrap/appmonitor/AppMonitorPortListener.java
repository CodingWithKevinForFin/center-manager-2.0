package com.f1.bootstrap.appmonitor;

import com.f1.base.Action;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.povo.f1app.F1AppPort;
import com.f1.utils.OH;

public class AppMonitorPortListener extends AbstractAppMonitorObjectListener<F1AppPort, Port<?>> {

	public AppMonitorPortListener(AppMonitorState state, Port processor) {
		super(state, processor);
	}

	@Override
	public Class<F1AppPort> getAgentType() {
		return F1AppPort.class;
	}

	private boolean first = true;

	@Override
	protected void populate(Port source, F1AppPort sink) {
		AppMonitorContainerScopeListener.populateContainerScope(source, sink);
		if (first) {
			sink.setActionTypeClassId(getState().resolveClassId(OH.noNull(source.getActionType(), Action.class)));
			sink.setName(source.getName());
			first = false;
		}
		if (source.isStarted() && sink.getConnectedTo() == 0L) {
			Processor dest = source.getProcessor();
			sink.setConnectedTo(F1AppPort.NOT_CONNECTED);
			if (dest != null) {
				for (AppMonitorProcessorListener listener : getState().getListeners(AppMonitorProcessorListener.class)) {
					if (listener.getObject() == dest) {
						sink.setConnectedTo(listener.getAgentObject().getContainerScopeId());
						break;
					}
				}
			}
		}
	}
	@Override
	public byte getListenerType() {
		return TYPE_PORT;
	}

}
