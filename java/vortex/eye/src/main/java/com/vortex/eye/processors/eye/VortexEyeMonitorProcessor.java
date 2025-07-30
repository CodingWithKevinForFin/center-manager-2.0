package com.vortex.eye.processors.eye;

import java.util.concurrent.TimeUnit;

import com.f1.container.OutputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.ThreadScope;
import com.f1.povo.standard.CountMessage;
import com.f1.utils.LH;
import com.f1.vortexcommon.msg.eye.VortexEyeJournalReport;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunScheduledTaskRequest;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeJournal;
import com.vortex.eye.processors.VortexEyeBasicProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeMonitorProcessor extends VortexEyeBasicProcessor<CountMessage> {

	public static final int PERIOD_MS = 250;
	public final OutputPort<CountMessage> loopback = newOutputPort(CountMessage.class);
	public final RequestOutputPort<VortexEyeRequest, VortexEyeResponse> toEyePort = newRequestOutputPort(VortexEyeRequest.class, VortexEyeResponse.class);
	public VortexEyeMonitorProcessor() {
		super(CountMessage.class);
	}

	@Override
	public void processAction(CountMessage action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		long now = getTools().getNow();
		long delay = PERIOD_MS - (now % PERIOD_MS);
		long count = action.getCount();
		action.setCount(count + 1);
		loopback.sendDelayed(action, threadScope, delay, TimeUnit.MILLISECONDS);

		//check once / second
		if (count % (1000 / PERIOD_MS) == 0) {
			for (VortexEyeScheduledTask task : state.getScheduledTasks()) {
				try {
					if (task.getState() != VortexEyeScheduledTask.STATE_ACTIVE)
						continue;
					switch (task.getStatus()) {
						case VortexEyeScheduledTask.STATUS_DISABLED:
						case VortexEyeScheduledTask.STATUS_INVALID:
						case VortexEyeScheduledTask.STATUS_RUNNING:
						case VortexEyeScheduledTask.STATUS_QUEUED_TO_RUN:
							continue;
					}
					if (task.getNextRuntime() <= now) {
						VortexEyeScheduledTask nuwtask = task.clone();
						nuwtask.setStatus(VortexEyeScheduledTask.STATUS_QUEUED_TO_RUN);
						state.addScheduledTask(nuwtask);
						VortexEyeRunScheduledTaskRequest req = nw(VortexEyeRunScheduledTaskRequest.class);
						req.setInvokedBy("#SCHEDULER");
						req.setScheduledTaskId(task.getId());
						//TODO: wire to response processor
						toEyePort.request(req, threadScope);
					}
				} catch (Exception e) {
					LH.warning(log, "error running task: ", task, e);
				}
			}
		}

		//check once / minute
		if (count % (60000 / PERIOD_MS) == 0) {
			try {
				VortexEyeJournal journal = state.getJournal();
				if (journal.needsPing()) {
					journal.logPing(state.getAllMachines().size(), 0);
					if (journal.needsReport()) {
						VortexEyeJournalReport report = journal.logReport();
						VortexEyeChangesMessageBuilder cmb = state.getChangesMessageBuilder();
						cmb.writeTransition(null, report);
						sendToClients(cmb.popToChangesMsg(state.nextSequenceNumber()));
					}
				}
			} catch (Exception e) {
				LH.severe(log, "error running journal", e);
			}
		}

		//		if (count % (250 / PERIOD_MS) == 0) {
		//			VortexEyeChangesMessageBuilder msgBuilder = state.getChangesMessageBuilder();
		//			for (VortexEyeAmiConnection connection : state.getAmiConnections()) {
		//				if (connection.needsStatsUpdate()) {
		//					VortexAmiConnection orig = connection.getConnection();
		//					VortexAmiConnection conn = orig.clone();
		//					connection.applyCounts(conn);
		//					connection.setConnection(conn);
		//					msgBuilder.writeUpdate(orig, conn, null);
		//				}
		//			}
		//
		//			if (now >= state.getMinExpiringAmiAlertTime()) {
		//				for (VortexAmiAlert alert : CH.l(state.getExpiringAmiAlerts())) {
		//					if (MH.toUnsignedInt(alert.getExpiresInSeconds()) * 1000L <= now) {
		//						state.removeExpiringAmiAlert(alert.getId());
		//						VortexEyeAmiApplication app = state.getAmiAppByAppId(alert.getAmiApplicationId());
		//						VortexEyeAmiUtils.poolStringValues(VortexAmiEntity.CATEGORY_ALERT, app.getAppId(), alert.getType(), -1, alert.getParams(), state);
		//						app.removeAmiAlert(alert);
		//						msgBuilder.writeRemove(alert);
		//					}
		//				}
		//			}
		//			if (now >= state.getMinExpiringAmiObjectTime()) {
		//				for (VortexAmiObject object : CH.l(state.getExpiringAmiObjects())) {
		//					if (MH.toUnsignedInt(object.getExpiresInSeconds()) * 1000L <= now) {
		//						state.removeExpiringAmiObject(object.getId());
		//						VortexEyeAmiApplication app = state.getAmiAppByAppId(object.getAmiApplicationId());
		//						VortexEyeAmiUtils.poolStringValues(VortexAmiEntity.CATEGORY_OBJECT, app.getAppId(), object.getType(), -1, object.getParams(), state);
		//						app.removeAmiObject(object);
		//						msgBuilder.writeRemove(object);
		//					}
		//				}
		//			}
		//
		//			if (msgBuilder.hasChanges()) {
		//				VortexEyeChanges toClient = msgBuilder.popToChangesMsg(state.nextSequenceNumber());
		//				sendToClients(toClient);
		//			}
		//		}
	}
}
