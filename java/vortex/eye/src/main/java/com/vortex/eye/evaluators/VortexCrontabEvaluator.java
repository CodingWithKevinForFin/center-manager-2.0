package com.vortex.eye.evaluators;

import java.util.HashMap;
import java.util.Map;

import com.f1.povo.db.DbRequestMessage;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeUtils;

public class VortexCrontabEvaluator extends VortexAbstractRevisionEvaluator<VortexAgentCron> {

	@Override
	public DbRequestMessage insertToDatabase(VortexAgentCron cron) {
		VortexEyeUtils.assertValid(cron);
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("id", cron.getId());
		params.put("active", VortexEyeUtils.isActive(cron));
		params.put("now", cron.getNow());
		params.put("user_name", cron.getUser());
		params.put("machine_instance_id", cron.getMachineInstanceId());
		params.put("revision", cron.getRevision());
		params.put("second", 0);
		params.put("minute", cron.getMinute());
		params.put("hour", cron.getHour());
		params.put("day_of_month", cron.getDayOfMonth());
		params.put("month", cron.getMonth());
		params.put("day_of_week", cron.getDayOfWeek());
		params.put("timezone", cron.getTimeZone());
		params.put("command", cron.getCommand());
		return execute("insert_job_schedule", params);
	}

	@Override
	public DbRequestMessage insertStatsToDatabase(VortexAgentCron value) {
		return null;
	}

	@Override
	public Map<String, VortexAgentCron> getFromSnapshot(VortexAgentSnapshot snapshot) {
		return snapshot.getCron();
	}

	@Override
	public Class<VortexAgentCron> getAgentType() {
		return VortexAgentCron.class;
	}

	@Override
	public byte getPidType(byte pid) {
		return FUNDAMENTAL_PID;
	}
	@Override
	String getKey(VortexAgentCron value) {
		return VortexAgentUtils.getKey(value);
	}
}
