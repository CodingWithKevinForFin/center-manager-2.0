/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.ami.relay;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import com.f1.ami.amicommon.msg.AmiRelayChangesMessage;
import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicState;

public class AmiRelayMulticastProcessor extends BasicProcessor<AmiRelayChangesMessage, BasicState> {

	private final OutputPort<AmiRelayChangesMessage>[] ports;

	private final BitSet buf = new BitSet();
	private final List<AmiRelayMessage>[] messagesByCenter;

	private AmiRelayRoutes router;

	public AmiRelayMulticastProcessor(AmiRelayRoutes router2, int centersCount) {
		super(AmiRelayChangesMessage.class, BasicState.class);
		this.router = router2;
		this.ports = new OutputPort[centersCount];
		this.messagesByCenter = new List[centersCount];
		for (int i = 0; i < centersCount; i++)
			ports[i] = newOutputPort(AmiRelayChangesMessage.class);
	}

	@Override
	public void processAction(AmiRelayChangesMessage action, BasicState state, ThreadScope threadScope) {
		AmiRelayRouter threadSafeRouter = this.router.getThreadSafeRouter();
		if (!threadSafeRouter.hasRules()) {
			if (threadSafeRouter.getDebugMode())
				AmiRelayRouteChain.logSkippingRoutes();
			for (OutputPort<AmiRelayChangesMessage> p : ports)
				p.send(action, threadScope);
		} else {
			for (AmiRelayMessage i : action.getAmiEvents()) {
				buf.clear();
				threadSafeRouter.getRoutes(i, buf);
				buf.nextSetBit(0);
				for (int center = buf.nextSetBit(0); center >= 0; center = buf.nextSetBit(center + 1)) {
					if (center == -1)
						break;
					List<AmiRelayMessage> list = messagesByCenter[center];
					if (list == null)
						messagesByCenter[center] = list = new ArrayList<AmiRelayMessage>();
					list.add(i);
				}
			}
			for (int center = 0; center < messagesByCenter.length; center++) {
				List<AmiRelayMessage> list = messagesByCenter[center];
				if (list != null) {
					AmiRelayChangesMessage action2 = (AmiRelayChangesMessage) action.clone();
					action2.setAmiEvents(list);
					messagesByCenter[center] = null;
					this.ports[center].send(action2, threadScope);
				} else if (action.getAmiStringPoolMap() != null) {
					AmiRelayChangesMessage action2 = (AmiRelayChangesMessage) action.clone();
					action2.setAmiEvents(Collections.EMPTY_LIST);
					this.ports[center].send(action2, threadScope);
				}
			}
		}

	}

	public OutputPort<AmiRelayChangesMessage> getToCenterPort(byte centerId) {
		return this.ports[centerId];
	}

}
