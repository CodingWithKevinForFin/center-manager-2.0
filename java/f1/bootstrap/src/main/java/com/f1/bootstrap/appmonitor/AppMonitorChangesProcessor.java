package com.f1.bootstrap.appmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.f1.base.Message;
import com.f1.bootstrap.F1Constants;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.f1app.F1AppChanges;
import com.f1.povo.f1app.F1AppClass;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppEvent;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.utils.EH;
import com.f1.utils.LH;

public class AppMonitorChangesProcessor extends BasicProcessor<Message, AppMonitorState> {

	private static final int MIN_PING_PERIOD = 20;
	public final OutputPort<F1AppChanges> changesOutputPort = newOutputPort(F1AppChanges.class);
	public final OutputPort<Message> loopback = newOutputPort(Message.class);
	private int pingPeriodMs = MIN_PING_PERIOD;

	public AppMonitorChangesProcessor() {
		super(Message.class, AppMonitorState.class);
	}

	public void init() {
		super.init();
		pingPeriodMs = getTools().getOptional(F1Constants.PROPERTY_AGENT_UPDATE_PERIOD, F1Constants.DEFAULT_AGENT_UPDATE_PERIOD);
		if (pingPeriodMs < MIN_PING_PERIOD) {
			LH.log(log, Level.WARNING, "Period to low, resetting to ", MIN_PING_PERIOD, "ms: ", pingPeriodMs);
			pingPeriodMs = MIN_PING_PERIOD;
		}
	}

	@Override
	public void processAction(Message action, AppMonitorState state, ThreadScope threadScope) throws Exception {
		state.setPingInQueue(false);

		if (state.getAppMonitorClients().isEmpty())
			return;

		//Handle first time this processor is called
		if (!state.isInit())
			throw new IllegalStateException("snapshot not processed yet");

		final long startMs = EH.currentTimeMillis();
		AppMonitorContainer amContainer = (AppMonitorContainer) getContainer();

		List<AppMonitorObjectListener<?, ?>> newListeners = amContainer.getManagersListener().flushNewListeners();
		for (AppMonitorObjectListener<?, ?> listener : newListeners) {
			state.newAgentF1Object(listener);
			for (AppMonitorAuditRule rule : state.getAuditTrailRules()) {
				if (rule.getListenerType() == listener.getClass() && rule.isAuditable(listener)) {
					listener.addAuditRule(rule);
				}
			}
		}

		Iterable<AppMonitorObjectListener<?, ?>> listeners = state.getListeners();
		List<F1AppEvent> events = new ArrayList<F1AppEvent>();
		state.startListening();

		List<F1AppEntity> added = new ArrayList<F1AppEntity>();
		for (AppMonitorObjectListener<?, ?> listener : newListeners) {
			F1AppEntity ao = listener.getAgentObject();
			if (ao != null) {
				added.add(ao);
			}
		}
		for (AppMonitorObjectListener<?, ?> listener : listeners) {
			if (listener.getObject() == null) {
				//TODO:handle removed
			} else if (listener.resetHasChanged()) {
				if (listener.updateAgentObject()) {
					F1AppAuditTrailEvent event = null;
					while ((event = listener.popAuditTrailEvent()) != null) {
						events.add(event);
					}
				}
			}
		}
		F1AppInstance appInstance = state.getSnapshot();
		AppMonitorUtils.populateSnapshotDynamics(appInstance);
		final long endMs = EH.currentTimeMillis();
		appInstance.setMonitorTimeSpentMs((int) (endMs - startMs));
		appInstance.setNowMs(endMs);
		appInstance.setClockNowMs(getServices().getClock().getNow());

		F1AppChanges changesMsg = nw(F1AppChanges.class);
		changesMsg.setF1AppEntitiesUpdated(state.flushChanges());
		for (F1AppClass cl : state.flushAddedClasses())
			added.add(cl);
		changesMsg.setF1AppEntitiesAdded(added);
		changesMsg.setSeqNum(state.nextSequenceNumber());
		changesMsg.setF1AppProcessUid(EH.getProcessUid());

		//TODO: 
		if (events.size() > 0)
			changesMsg.setF1AppEvents(events);
		changesOutputPort.send(changesMsg, threadScope);
		if (!state.getPingInQueue()) {
			state.setPingInQueue(true);
			loopback.sendDelayed(action, threadScope, pingPeriodMs, TimeUnit.MILLISECONDS);
		}

	}
}
