package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;

public class VortexClientNetConnection extends VortexClientMachineEntity<VortexAgentNetConnection> {

	private VortexClientNetConnection remoteConnection;

	private VortexClientProcess process;

	final private int localPid;

	public VortexClientNetConnection(VortexAgentNetConnection data) {
		super(VortexAgentEntity.TYPE_NET_CONNECTION, data);
		if (data.getLocalPid() != null)
			this.localPid = Integer.parseInt(data.getLocalPid());
		else
			this.localPid = -1;
	}

	public VortexClientNetConnection getRemoteConnection() {
		return remoteConnection;
	}
	public void setRemoteConnection(VortexClientNetConnection remoteConnection) {
		this.remoteConnection = remoteConnection;
	}

	public VortexClientProcess getProcess() {
		return process;
	}
	public void setProcess(VortexClientProcess process) {
		this.process = process;
	}

	public int getLocalPid() {
		return localPid;
	}

	public boolean getIsLoopback() {
		String t = getData().getForeignHost();
		return VortexClientManager.isLoopback(t);
		//VortexClientNetAddress address = getMachine().getManager().getNetAddressByIp(t, getMachine());
		//if (address == null)
		//return false;
		//if (address.getMachine() == getMachine())
		//return true;
		//return false;
	}

}
