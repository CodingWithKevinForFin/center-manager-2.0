package com.f1.bootstrap.appmonitor;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.povo.f1app.F1AppChanges;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppProperty;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.povo.f1app.reqres.F1AppSnapshotRequest;
import com.f1.povo.f1app.reqres.F1AppSnapshotResponse;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.VH;

public class AppMonitorSnapshotRequestProcessor extends BasicRequestProcessor<F1AppSnapshotRequest, AppMonitorState, F1AppSnapshotResponse> {

	final private String appName;
	public final OutputPort<Message> toChangesProcessorPort = newOutputPort(Message.class);

	public AppMonitorSnapshotRequestProcessor(String appName) {
		super(F1AppSnapshotRequest.class, AppMonitorState.class, F1AppSnapshotResponse.class);
		this.appName = appName;
	}

	public void init() {
		super.init();
	}

	@Override
	protected F1AppSnapshotResponse processRequest(RequestMessage<F1AppSnapshotRequest> action, AppMonitorState state, ThreadScope threadScope) throws Exception {
		F1AppSnapshotResponse r = nw(F1AppSnapshotResponse.class);
		AppMonitorClientState clientState = state.getAppMonitorClientNoThrow(action.getAction().getAgentProcessUid());
		if (clientState == null) {
			state.addAppMonitorClient(action.getAction().getAgentProcessUid());
		}
		final long startMs = EH.currentTimeMillis();
		AppMonitorContainer amContainer = (AppMonitorContainer) getContainer();

		//Handle first time this processor is called
		if (!state.isInit()) {
			F1AppInstance snapshot = nw(F1AppInstance.class);
			snapshot.setObjectType(F1AppEntity.TYPE_APP_INSTANCE);
			snapshot.setId(state.generateNextMonitorId());
			AppMonitorUtils.populateSnapshotStatics(amContainer.getBootstrap(), snapshot);
			snapshot.setF1AppInstanceId(snapshot.getId());
			snapshot.setAppName(appName);
			state.init(snapshot);
			for (List<F1AppProperty> props : AppMonitorUtils.toF1AppProperties(amContainer.getBootstrap().getProperties(), getServices().getGenerator()).values())
				for (F1AppProperty prop : props)
					state.addEntity(prop);

			//we can pop these off because its the first sn request
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
			List<F1AppAuditTrailEvent> events = new ArrayList<F1AppAuditTrailEvent>();
			state.startListening();
			for (AppMonitorObjectListener<?, ?> listener : listeners) {
				if (listener.getObject() == null) {
					//TODO:handle removed
				} else if (listener.resetHasChanged()) {
					listener.updateAgentObject();
					F1AppAuditTrailEvent event = null;
					while ((event = listener.popAuditTrailEvent()) != null) {
						events.add(event);
					}
				}
			}
			state.flushChanges();//silly... we should not be recording because this is a snapshot (no deltas).
		}

		F1AppInstance appInstance = state.getSnapshot().clone();

		AppMonitorUtils.populateSnapshotDynamics(appInstance);

		final long endMs = EH.currentTimeMillis();
		appInstance.setMonitorTimeSpentMs((int) (endMs - startMs));
		appInstance.setNowMs(endMs);
		appInstance.setClockNowMs(getServices().getClock().getNow());
		List<F1AppEntity> entities = new ArrayList<F1AppEntity>();
		entities.add(appInstance);
		entities.addAll(VH.cloneListEntries(CH.l(state.getEntities())));
		final F1AppChanges changes = nw(F1AppChanges.class);
		changes.setF1AppEntitiesAdded(entities);
		changes.setSeqNum(state.currentSequenceNumber());
		changes.setF1AppProcessUid(EH.getProcessUid());

		r.setSnapshot(changes);

		if (!state.getPingInQueue()) {
			toChangesProcessorPort.send(nw(Message.class), threadScope);
		}
		r.setOk(true);
		return r;

	}

}
