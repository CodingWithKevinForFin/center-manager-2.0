package com.f1.bootstrap.appmonitor;

import java.util.concurrent.atomic.AtomicLong;

import com.f1.base.Action;
import com.f1.base.NestedAction;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.Partition;
import com.f1.container.ProcessActionListener;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.povo.f1app.F1AppProcessor;
import com.f1.povo.f1app.audit.F1AppAuditTrailF1Event;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.utils.EH;
import com.f1.utils.OH;

public class AppMonitorProcessorListener extends AbstractAppMonitorObjectListener<F1AppProcessor, Processor> implements ProcessActionListener {
	private final AtomicLong processEventsCount = new AtomicLong(0);
	private final AtomicLong thrownEventsCount = new AtomicLong(0);
	private final AtomicLong queueEventsPoped = new AtomicLong(0);
	private final AtomicLong queueEventsPushed = new AtomicLong(0);
	private ObjectGeneratorForClass<F1AppAuditTrailF1Event> generator;

	public AppMonitorProcessorListener(AppMonitorState state, Processor processor) {
		super(state, processor);
		this.generator = state.getPartition().getContainer().getGenerator(F1AppAuditTrailF1Event.class);
	}

	@Override
	public void onProcessAction(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch) {
		if (isDispatch)
			queueEventsPoped.incrementAndGet();
		else
			processEventsCount.incrementAndGet();
		flagChanged();
		handleAudit(processor, partition, action, state);
	}
	private void handleAudit(Processor processor, Partition partition, Action data, State state) {
		AppMonitorAuditRule[] rules = getAuditRuleIdsOrNull();
		if (rules != null) {
			int matchCount = 0;
			boolean found[] = null;
			for (int i = 0; i < rules.length; i++) {
				AppMonitorF1EventRule f1Rule = (AppMonitorF1EventRule) rules[i];
				if (f1Rule.isAuditable(data, state)) {
					if (found == null)
						found = new boolean[rules.length];
					found[i] = true;
					matchCount++;
				}
			}

			if (found != null) {
				F1AppAuditTrailF1Event event = generator.nw();
				long[] ruleIds = new long[matchCount];
				for (int i = 0, j = 0; i < rules.length; i++) {
					if (found[i])
						ruleIds[j++] = rules[i].getId();
				}
				event.setType(F1AppAuditTrailRule.EVENT_TYPE_F1);
				event.setAgentRuleIds(ruleIds);
				event.setTimeMs(EH.currentTimeMillis());
				event.setAuditSequenceNumber(getState().nextAuditSequenceNumber());
				String messageName = data == null ? null : data.askSchema().askOriginalType().getSimpleName();
				if (data instanceof NestedAction) {
					NestedAction<?> na = (NestedAction<?>) data;
					if (na.getAction() != null)
						messageName += '(' + na.getAction().askSchema().askOriginalType().getSimpleName() + ')';
				}
				String stateName = OH.getSimpleClassName(state);
				event.setMessageClassName(messageName);
				event.setStateClassName(stateName);
				event.setPayloadAsBytes(getState().getGenericConverter().object2Bytes(data));
				event.setPayloadFormat(F1AppAuditTrailF1Event.FORMAT_BYTES_F1);
				if (partition != null)
					event.setPartitionId(partition.getPartitionId());
				event.setAgentF1ObjectId(getAgentObject().getId());
				onAuditEvent(event);
			}
		}
	}
	@Override
	public void onHandleThrowable(Processor processor, Partition partition, Action action, State state, ThreadScope thread, Throwable thrown) {
		thrownEventsCount.incrementAndGet();
		flagChanged();
	}

	public long getProcessEventsCount() {
		return processEventsCount.get();
	}

	public long getThrownEventsCount() {
		return thrownEventsCount.get();
	}

	@Override
	public Class<F1AppProcessor> getAgentType() {
		return F1AppProcessor.class;
	}
	@Override
	public void onProcessActionDone(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, boolean isDispatch) {
	}

	private boolean first = true;

	@Override
	protected void populate(Processor source, F1AppProcessor sink) {
		AppMonitorContainerScopeListener.populateContainerScope(source, sink);
		if (first) {
			sink.setActionTypeClassId(getState().resolveClassId(OH.noNull(source.getActionType(), Action.class)));
			sink.setStateTypeClassId(getState().resolveClassId(OH.noNull(source.getStateType(), State.class)));
			sink.setName(source.getName());
			first = false;
		}
		sink.setThrownStats(getThrownEventsCount());
		sink.setQueuePopStats(queueEventsPoped.get());
		sink.setProcessStats(getProcessEventsCount() + sink.getQueuePopStats());
		sink.setQueuePushStats(queueEventsPushed.get());
	}

	@Override
	public void onQueueAction(Processor processor, Partition partition, Action action, ThreadScope threadScope) {
		queueEventsPushed.incrementAndGet();
		flagChanged();
	}

	@Override
	public byte getListenerType() {
		return TYPE_PROCESSOR;
	}

}
