package com.f1.ami.relay.fh.mgmt;

import java.util.Map;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.relay.AmiRelayServer;
import com.f1.ami.relay.fh.AmiFH;
import com.f1.utils.CH;

public class AmiStopFHCommand extends AmiCommandBase {

	@Override
	public void exec(AmiRelayServer server, AmiCommandManager fhMgr, AmiRelayRunAmiCommandRequest action, StringBuilder msgSink) throws Exception {
		if (CH.isEmpty(action.getFields())) {
			msgSink.append("Failed: no fields present");
			return;
		}

		for (Map<String, Object> m : action.getFields()) {
			Object o = m.get("fh_id");
			if (o == null) {
				msgSink.append("Failed: fh_id field is missing");
				continue;
			}

			int fhId = (Integer) o;
			AmiFH fh = server.getSession(fhId);
			if (fh != null) {
				if (fh.getStatus() == AmiFH.STATUS_STOPPED) {
					msgSink.append("Feed handler with id ").append(fhId).append(" is already stopped...Please try starting it first");
					continue;
				}

				fh.stop();

				if (fh.getStatus() == AmiFH.STATUS_STOPPED)
					msgSink.append("Feed handler with id ").append(fhId).append(" stopped successfully");
			}
		}
	}
}
