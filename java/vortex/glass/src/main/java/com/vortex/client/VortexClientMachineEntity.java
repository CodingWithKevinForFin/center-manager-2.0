package com.vortex.client;

import com.f1.utils.OH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public class VortexClientMachineEntity<T extends VortexAgentEntity> extends VortexClientEntity<T> {

	public static final byte STATE_MATCHED = 1;
	public static final byte STATE_NO_MATCH = 2;
	private VortexClientMachine parent;

	public VortexClientMachineEntity(byte type, T data) {
		super(type, data);
	}

	@Override
	public void setData(T data) {
		if (getData() != null) {
			OH.assertEq(data.getMachineInstanceId(), getData().getMachineInstanceId());
		}
		super.setData(data);
	}

	public void setMachine(VortexClientMachine machine) {
		this.parent = machine;
	}
	public VortexClientMachine getMachine() {
		return parent;
	}

	public long getMachineId() {
		return getData().getMachineInstanceId();
	}

	public String getHostName() {
		if (parent == null)
			return "N/A";
		//throw new NullPointerException("for type: " + getClass().getSimpleName());
		return parent.getData().getHostName();
	}

	@Override
	public void unbind() {
		setMachine(null);
		super.unbind();
	}

}
