package com.vortex.eye.processors;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicState;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;
import com.vortex.eye.cloud.CloudAdapter;
import com.vortex.eye.itinerary.VortexEyeRunCloudInterfaceActionItinerary;
import com.vortex.eye.messages.VortexEyeCloudProcessorMessage;
import com.vortex.eye.messages.VortextEyeCloudMachinesInfo;

public class VortexEyeCloudProcessor extends BasicProcessor<VortexEyeCloudProcessorMessage, BasicState> {

	public OutputPort<VortextEyeCloudMachinesInfo> output = newOutputPort(VortextEyeCloudMachinesInfo.class);
	private static Logger log = Logger.getLogger(VortexEyeCloudProcessor.class.getName());

	public VortexEyeCloudProcessor() {
		super(VortexEyeCloudProcessorMessage.class, BasicState.class);
	}

	@Override
	public void processAction(VortexEyeCloudProcessorMessage action, BasicState state, ThreadScope threadScope) throws Exception {
		List<VortextEyeCloudMachineInfo> l = new LinkedList<VortextEyeCloudMachineInfo>();
		for (VortexEyeCloudInterface ci : action.getCloudInterfaces()) {
			try {
				CloudAdapter adapter = VortexEyeRunCloudInterfaceActionItinerary.getCloudAdapter(ci.getCloudVendorType());
				if (adapter != null) {
					List<VortextEyeCloudMachineInfo> il = adapter.getMachineInfoList(this, ci);
					if (il != null)
						CH.addAll(l, il);
				}
			} catch (Exception e) {
				LH.severe(log, "Failed to retrieve machine info from CI - ", ci.getDescription(), e.getMessage());
			}
		}

		VortextEyeCloudMachinesInfo mi = state.nw(VortextEyeCloudMachinesInfo.class);
		mi.setMachineInfoList(l);
		output.send(mi, threadScope);
	}
}
