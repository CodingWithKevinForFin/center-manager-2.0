package com.f1.ami.web.amiscript;

import com.f1.ami.amicommon.customobjects.AmiScriptAccessible;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.rt.AmiWebRealtimeProcessor_GRPC;

@AmiScriptAccessible(name = "GRPCProcessor")
public class AmiWebScriptMemberMethods_GRPCProcessor {
	private final AmiWebRealtimeProcessor_GRPC processor;

	@AmiScriptAccessible
	public AmiWebScriptMemberMethods_GRPCProcessor(final AmiWebRealtimeProcessor p) {
		if (!(p instanceof AmiWebRealtimeProcessor_GRPC))
			throw new RuntimeException("Could not construct processor, please pass in a GRPC Processor");
		this.processor = (AmiWebRealtimeProcessor_GRPC) p;
	}

	@AmiScriptAccessible(name = "run", params = { "command" })
	public void run(final String command) {
		this.processor.run(command);
	}

	@AmiScriptAccessible(name = "stop")
	public void stop() {
		this.processor.stop();
	}
}