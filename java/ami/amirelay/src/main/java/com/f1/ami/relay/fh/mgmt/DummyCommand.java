package com.f1.ami.relay.fh.mgmt;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.relay.AmiRelayServer;

public class DummyCommand extends AmiCommandBase {
	@Override
	public void exec(AmiRelayServer server, AmiCommandManager fhMgr, AmiRelayRunAmiCommandRequest action, StringBuilder msgSink) throws Exception {
		msgSink.append("For a dummy command this is pretty good");
	}

}
