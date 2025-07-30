package com.vortex.eye.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.f1.base.Getter;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.utils.CH;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.messages.VortexEyeCloudProcessorMessage;
import com.vortex.eye.messages.VortextEyeCloudMachinesInfo;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeCloudMainProcessor extends VortexEyeBasicProcessor<VortextEyeCloudMachinesInfo> {

	private long interval;
	private HashMap<String, Long> idMap = new HashMap<String, Long>();

	public OutputPort<VortexEyeCloudProcessorMessage> output = newOutputPort(VortexEyeCloudProcessorMessage.class);

	public VortexEyeCloudMainProcessor(long interval) {
		super(VortextEyeCloudMachinesInfo.class);
		this.interval = interval;
	}

	private void ensureId(VortexEyeState state, VortextEyeCloudMachineInfo mi) {
		Long id = idMap.get(mi.getInstanceId());
		if (id == null) {
			id = state.createNextId();
			idMap.put(mi.getInstanceId(), id);
		}

		mi.setId(id);
	}

	@Override
	public void processAction(VortextEyeCloudMachinesInfo mis, VortexEyeState state, ThreadScope threadScope) throws Exception {
		//update state
		if (mis != null && CH.isntEmpty(mis.getMachineInfoList())) {

			VortexEyeChangesMessageBuilder cmb = state.getChangesMessageBuilder();

			//figure out what changed
			for (VortextEyeCloudMachineInfo mi : mis.getMachineInfoList()) {
				ensureId(state, mi); //populate id
				VortextEyeCloudMachineInfo current = state.getCloudMachineInfo(mi.getId());
				state.addCloudMachineInfo(mi);
				if (current == null) {
					cmb.writeAdd(mi);
				} else {
					cmb.writeUpdate(current, mi);
				}
			}

			//check for deletes
			Map<Long, VortextEyeCloudMachineInfo> m = CH.m(mis.getMachineInfoList(), new Getter<VortextEyeCloudMachineInfo, Long>() {
				@Override
				public Long get(VortextEyeCloudMachineInfo key) {
					return key.getId();
				}
			});

			for (VortextEyeCloudMachineInfo mi : state.getCloudMachineInfos()) {
				if (!m.containsKey(mi.getId())) {
					cmb.writeRemove(mi);
					state.removeCloudMachineInfo(mi.getId());
				}
			}

			//send updates to the client
			sendToClients(cmb.popToChangesMsg(state.nextSequenceNumber()));
		}

		//schedule refresh 
		VortexEyeCloudProcessorMessage m = nw(VortexEyeCloudProcessorMessage.class);
		m.setCloudInterfaces(state.getCloudInterfaces());
		output.sendDelayed(m, threadScope, interval, TimeUnit.MILLISECONDS);
	}
}
