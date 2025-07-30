package com.f1.ami.relay;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;

public interface AmiRelayOut {

	//E (execute command) See real-time message api for details. If there was an unexpected error write the details to errorSink
	public void call(AmiRelayServer server, AmiRelayRunAmiCommandRequest action, StringBuilder errorSink);
}
