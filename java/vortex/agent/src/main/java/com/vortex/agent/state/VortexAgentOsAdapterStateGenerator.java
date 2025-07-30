package com.vortex.agent.state;

import com.f1.base.Action;
import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.impl.BasicStateGenerator;

public class VortexAgentOsAdapterStateGenerator extends BasicStateGenerator<VortexAgentOsAdapterState> {

	public static final String SERVICE_ID_OS_ADAPTER = "OS_ADAPTER";

	public VortexAgentOsAdapterStateGenerator() {
		super(VortexAgentOsAdapterState.class);
	}

	@Override
	public VortexAgentOsAdapterState createState(Partition partition, Action a, Processor<? extends Action, ?> processor) {
		VortexAgentOsAdapterState r = super.createState(partition, a, processor);
		r.setManager((VortexAgentOsAdapterManager) processor.getServices().getService(SERVICE_ID_OS_ADAPTER));
		return r;
	}
}
