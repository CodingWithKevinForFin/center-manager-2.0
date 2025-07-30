package com.f1.ami.center;

import java.util.logging.Logger;

import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiCenterRelayConnectionStatuses {

	private static final Logger log = LH.get();
	private final String remoteProcessUid;
	private boolean isRelayToCenterConnected;
	private boolean isCenterToRelayConnected;
	private boolean isCenterToRelayWithSuffixConnected;

	public AmiCenterRelayConnectionStatuses(String remoteProcessUid) {
		this.remoteProcessUid = remoteProcessUid;
	}

	public boolean isFullyConnected() {
		return isRelayToCenterConnected && isCenterToRelayConnected && isCenterToRelayWithSuffixConnected;
	}

	public boolean isFullyDisconnected() {
		return !isRelayToCenterConnected && !isCenterToRelayConnected && !isCenterToRelayWithSuffixConnected;
	}

	public void onMessage(MsgStatusMessage action) {
		OH.assertEq(this.remoteProcessUid, action.getRemoteProcessUid());
		String topic = action.getTopic();
		if ("relay.to.center".equals(topic) && action.getSuffix() == null) {
			this.isRelayToCenterConnected = action.getIsConnected();
		} else if ("center.to.relay".equals(topic)) {
			if (action.getSuffix() != null)
				this.isCenterToRelayWithSuffixConnected = action.getIsConnected();
			else
				this.isCenterToRelayConnected = action.getIsConnected();
		} else if (!"web.to.center".equals(topic) && !"center.to.web".equals(topic))
			LH.warning(log, "For ", remoteProcessUid, " unknown msg status: ", action);
	}
}
