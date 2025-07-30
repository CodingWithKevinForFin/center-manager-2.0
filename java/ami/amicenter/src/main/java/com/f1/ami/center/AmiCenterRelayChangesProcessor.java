package com.f1.ami.center;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.f1.ami.amicommon.msg.AmiRelayAckMessage;
import com.f1.ami.amicommon.msg.AmiRelayChangesMessage;
import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.EmptyIterator;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiCenterRelayChangesProcessor extends AmiCenterMultiProcessor<AmiRelayChangesMessage> {

	public final OutputPort<AmiRelayAckMessage> ackPort = newOutputPort(AmiRelayAckMessage.class);

	private int realtimeBatchSize = 1000;

	@Override
	public void init() {
		super.init();
		this.realtimeBatchSize = getTools().getOptional(AmiCenterProperties.PROPERTY_AMI_CENTER_RELAY_BATCH_MESSAGES_MAX, realtimeBatchSize);
	}
	public AmiCenterRelayChangesProcessor() {
		super(AmiRelayChangesMessage.class);
	}

	@Override
	public void processAction(AmiRelayChangesMessage action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		processActions(action, EmptyIterator.INSTANCE, state, threadScope);
	}

	@Override
	public void processActions(Iterator<AmiRelayChangesMessage> actions, AmiCenterState state, ThreadScope threadScope) throws Exception {
		if (!actions.hasNext())
			return;
		processActions(actions.next(), actions, state, threadScope);
	}

	private List<AmiRelayMessage> buffer = new ArrayList<AmiRelayMessage>();
	private IdentityHashSet<AmiCenterRelayState> otherAgentStates = new IdentityHashSet<AmiCenterRelayState>();

	public void processActions(AmiRelayChangesMessage action, Iterator<AmiRelayChangesMessage> actions, AmiCenterState state, ThreadScope threadScope) throws Exception {
		AmiCenterChangesMessageBuilder msgBuilder = state.getChangesMessageBuilder();
		final long now = EH.currentTimeMillis();
		AmiCenterRelayState agentState = null;
		AmiImdbSession session = state.getRtFeedSession();
		TopCalcFrameStack sf = session.getReusableTopStackFrame();
		if (otherAgentStates.size() > 0)
			this.otherAgentStates.clear();
		AmiCenterGlobalProcess proc = state.getAmiImdb().getGlobalProcess();
		proc.setProcessStatus(AmiCenterProcess.PROCESS_RUN_RT);
		try {
			session.lock(proc, null);
			while (action != null) {
				this.buffer.clear();
				if (agentState == null)
					agentState = state.getAgentByPuidNoThrow(action.getAgentProcessUid());
				else if (OH.ne(agentState.getProcessUid(), action.getAgentProcessUid())) {
					otherAgentStates.add(agentState);
					agentState = state.getAgentByPuidNoThrow(action.getAgentProcessUid());
				}
				if (agentState == null) {
					LH.info(log, "Ignoring changes prior to snapshot from relay: ", action.getAgentProcessUid());
					break;
				}
				if (agentState.getCurrentSeqNum() == -1L) {
					LH.info(log, "Received update, prior to  snapshot from: ", action.getAgentProcessUid());
					break;
				}
				if (agentState.getCurrentSeqNum() + 1 > action.getSeqNum()) {//sequence numbers may be skipped to to relay routing filters
					if (agentState.getCurrentSeqNum() == action.getSeqNum()) {
						LH.info(log, "Ignoring changes prior to snapshot from relay: ", action.getAgentProcessUid() + ", received: " + action.getSeqNum());
						return;
					} else
						throw new RuntimeException("SeqNum mismatch at " + agentState.getCurrentSeqNum() + ", received: " + action.getSeqNum());
				}
				agentState.setCurrentSeqNum(action.getSeqNum());

				AmiCenterAmiUtils.processRelayNewKeys(action.getAmiStringPoolMap(), state, agentState);
				List<AmiRelayMessage> buf = action.getAmiEvents();
				for (;;) {
					if (buf != null && buf.size() >= realtimeBatchSize) {
						AmiCenterAmiUtils.processAgentAmiEvents(state, agentState, buf, null, this.getTools(), now, msgBuilder, sf);
						this.buffer.clear();
						action = null;
						buf = null;
						break;
					}

					if (!actions.hasNext()) {
						action = null;
						AmiCenterAmiUtils.processAgentAmiEvents(state, agentState, buf, null, this.getTools(), now, msgBuilder, sf);
						break;
					}
					action = actions.next();
					if (OH.ne(action.getAgentProcessUid(), agentState.getProcessUid())) {
						AmiCenterAmiUtils.processAgentAmiEvents(state, agentState, buf, null, this.getTools(), now, msgBuilder, sf);
						break;
					}
					if (agentState.getCurrentSeqNum() + 1 > action.getSeqNum()) {
						AmiCenterAmiUtils.processAgentAmiEvents(state, agentState, buf, null, this.getTools(), now, msgBuilder, sf);
						throw new RuntimeException("SeqNum mismatch at " + agentState.getCurrentSeqNum() + ", received: " + action.getSeqNum());
					}
					agentState.setCurrentSeqNum(action.getSeqNum());
					AmiCenterAmiUtils.processRelayNewKeys(action.getAmiStringPoolMap(), state, agentState);
					if (CH.isntEmpty(action.getAmiEvents())) {
						if (buf == this.buffer) {
							buf.addAll(action.getAmiEvents());
						} else if (buf == null) {
							buf = action.getAmiEvents();
						} else {
							this.buffer.addAll(buf);
							this.buffer.addAll(action.getAmiEvents());
							buf = this.buffer;
						}
					}
				}
			}
		} finally {
			proc.setProcessStatus(AmiCenterProcess.PROCESS_IDLE);
			session.unlock();
		}
		this.buffer.clear();

		String warnings = session.drainWarnings();
		if (SH.is(warnings)) {
			LH.info(log, "Encounter errors while processing realtime events: ", warnings);
		}
		state.onProcessedEventsComplete();
		if (agentState != null)
			sendAck(threadScope, agentState);
		if (!this.otherAgentStates.isEmpty()) {
			for (AmiCenterRelayState i : this.otherAgentStates)
				if (i != agentState)
					sendAck(threadScope, i);
			this.otherAgentStates.clear();
		}
	}

	private void sendAck(ThreadScope threadScope, AmiCenterRelayState agentState) {
		if (agentState.getGuaranteedMessaging()) {
			AmiRelayAckMessage ack = nw(AmiRelayAckMessage.class);
			ack.setAgentProcessUid(agentState.getProcessUid());
			ack.setSeqNum(agentState.getCurrentSeqNum());
			ack.setCenterId(agentState.getCenterId());
			ackPort.send(ack, threadScope);
			agentState.saveSeqnumToDisk();
		}
	}
}
