package com.f1.bootstrap.appmonitor;

import com.f1.container.ContainerScope;
import com.f1.povo.f1app.F1AppContainerScope;

public class AppMonitorContainerScopeListener extends AbstractAppMonitorObjectListener<F1AppContainerScope, ContainerScope> {

	public AppMonitorContainerScopeListener(AppMonitorState state, ContainerScope processor) {
		super(state, processor);
		if (processor == null)
			throw new NullPointerException();
	}

	@Override
	public Class<F1AppContainerScope> getAgentType() {
		return F1AppContainerScope.class;
	}

	@Override
	protected void populate(ContainerScope source, F1AppContainerScope sink) {
		populateContainerScope(source, sink);
	}

	public static void populateContainerScope(ContainerScope source, F1AppContainerScope sink) {
		sink.setStartedMs(source.getStartedMs());
		sink.setContainerScopeId(source.getContainerScopeUid());
		sink.setName(source.getName());
		sink.setContainerScopeType(AppMonitorUtils.toContainerScopeType(source));
		final ContainerScope parent = source.getParentContainerScope();
		if (parent != null)
			sink.setParentId(parent.getContainerScopeUid());
		else
			sink.setParentId(-1L);
	}

	@Override
	public byte getListenerType() {
		return TYPE_CONTAINER_SCOPE;
	}

}
