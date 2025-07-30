package com.f1.ami.amicommon.centerclient;

import java.util.logging.Logger;

import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.LH;

public class AmiCenterClientCenterConnectionStatuses {

	private static final Logger log = LH.get();
	private boolean isWebToCenterConnected;
	private boolean isCenterToWebConnected;
	private boolean isCenterToWebWithSuffixConnected;

	public boolean isFullyConnected() {
		return isWebToCenterConnected && isCenterToWebConnected && isCenterToWebWithSuffixConnected;
	}

	public boolean isFullyDisconnected() {
		return !isWebToCenterConnected && !isCenterToWebConnected && !isCenterToWebWithSuffixConnected;
	}

	public void onMessage(MsgStatusMessage action) {
		String topic = action.getTopic();
		if ("web.to.center".equals(topic) && action.getSuffix() == null) {
			this.isWebToCenterConnected = action.getIsConnected();
		} else if ("center.to.web".equals(topic)) {
			if (action.getSuffix() != null)
				this.isCenterToWebWithSuffixConnected = action.getIsConnected();
			else
				this.isCenterToWebConnected = action.getIsConnected();
		} else
			LH.warning(log, "Unknown msg status: ", action);
	}
}
