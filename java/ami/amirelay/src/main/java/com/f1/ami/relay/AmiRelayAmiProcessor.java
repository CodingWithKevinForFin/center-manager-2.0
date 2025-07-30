package com.f1.ami.relay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.msg.AmiRelayChangesMessage;
import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.ami.amicommon.msg.AmiRelayConnectionMessage;
import com.f1.ami.amicommon.msg.AmiRelayErrorMessage;
import com.f1.ami.amicommon.msg.AmiRelayLoginMessage;
import com.f1.ami.amicommon.msg.AmiRelayLogoutMessage;
import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectDeleteMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectMessage;
import com.f1.ami.amicommon.msg.AmiRelayStatusMessage;
import com.f1.ami.amicommon.msg.SingleParamMessage;
import com.f1.ami.relay.fh.AmiFH;
import com.f1.container.MultiProcessor;
import com.f1.container.ThreadScope;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;

public class AmiRelayAmiProcessor extends AmiRelayBasicProcessor<AmiRelayMessage> implements MultiProcessor<AmiRelayMessage, AmiRelayState> {

	public AmiRelayAmiProcessor() {
		super(AmiRelayMessage.class);
	}

	@Override
	public void processAction(AmiRelayMessage action, AmiRelayState state, ThreadScope threadScope) throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	public void processActions(Iterator<AmiRelayMessage> actions, AmiRelayState state, ThreadScope threadScope) throws Exception {
		List<AmiRelayMessage> amiEvents = new ArrayList<AmiRelayMessage>();
		AmiRelayConnectionState amiConnection = null;
		final AmiRelayChangesMessage toEye = nw(AmiRelayChangesMessage.class);
		long seqnum = state.nextSequenceNumber();
		toEye.setSeqNum(seqnum);
		while (amiEvents.size() < 1000 && actions.hasNext()) {
			AmiRelayMessage action = actions.next();
			convertAmitParamsToIds(action, state);
			amiEvents.add(action);
			int connectionId = action.getConnectionId();
			if (amiConnection == null || amiConnection.getConnectionId() != connectionId)
				amiConnection = state.getAmiConnection(connectionId);
			if (action instanceof AmiRelayObjectMessage || action instanceof AmiRelayObjectDeleteMessage || action instanceof AmiRelayErrorMessage)
				continue;
			if (action instanceof AmiRelayConnectionMessage) {
				state.addAmiConnection((AmiRelayConnectionMessage) action);
			} else if (action instanceof AmiRelayLoginMessage) {
				amiConnection.setLogin((AmiRelayLoginMessage) action);
			} else if (action instanceof AmiRelayLogoutMessage) {
				state.removeAmiConnection(connectionId);
			} else if (action instanceof AmiRelayStatusMessage) {
				amiConnection.addStatus((AmiRelayStatusMessage) action);
			} else if (action instanceof AmiRelayCommandDefMessage) {
				amiConnection.addCommand((AmiRelayCommandDefMessage) action);
			} else
				LH.info(log, action);
		}

		toEye.setAmiEvents(amiEvents);
		Map<Short, String> map = state.getPendingNewKeysSink();
		state.getJournal().journal(seqnum, amiEvents, map);
		if (CH.isntEmpty(map)) {
			toEye.setAmiStringPoolMap(new HashMap<Short, String>(map));
			map.clear();
		}
		for (int n = 0, l = amiEvents.size(); n < l; n++) {
			AmiRelayMessage i = amiEvents.get(n);
			if (i.getTransformState() == AmiRelayMessage.TRANSFORM_DUP)
				continue;
			if (i instanceof AmiRelayObjectMessage || i instanceof AmiRelayObjectDeleteMessage || i instanceof AmiRelayStatusMessage) {
				if (amiConnection == null || amiConnection.getConnectionId() != i.getConnectionId())
					amiConnection = state.getAmiConnection(i.getConnectionId());
				try {
					AmiFH c = amiConnection == null ? null : amiConnection.getConnection();
					if (c == null || !c.onAck(i.getOrigSeqNum())) {
						LH.warning(log, "Could not ack for the following message(s): ", i);
					}
				} catch (Exception e) {
					LH.warning(log, "Error on ack:", i, e);
				}
			}
		}

		toEye.setAgentProcessUid(EH.getProcessUid());
		getToCenterPort().send(toEye, threadScope);
	}
	private void convertAmitParamsToIds(AmiRelayMessage action, AmiRelayState state) {
		SingleParamMessage spm = (SingleParamMessage) action;
		byte[] params = spm.getParams();
		params = state.convertAmiParamsToIds(params);
		spm.setParams(params);
	}
}
