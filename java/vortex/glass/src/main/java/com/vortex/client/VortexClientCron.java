package com.vortex.client;

import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public class VortexClientCron extends VortexClientMachineEntity<VortexAgentCron> {

	private String scheduleText;

	public VortexClientCron(VortexAgentCron data) {
		super(VortexAgentEntity.TYPE_CRON, data);
		update(data);
	}

	public void update(VortexAgentCron data) {
		super.update(data);
		this.scheduleText = SH.join(' ', data.getSecond(), data.getMinute(), data.getHour(), data.getDayOfMonth(), data.getMonth(), data.getDayOfWeek());
	}

	public String getScheduleText() {
		return this.scheduleText;
	}

}
