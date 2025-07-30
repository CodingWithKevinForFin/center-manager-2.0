package com.f1.ami.relay;

import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.container.OutputPort;
import com.f1.container.impl.BasicState;

public class AmiRelayTransformState extends BasicState {

	final private AmiRelayTransformManager mapper;

	public AmiRelayTransformState(AmiRelayTransformManager mapper) {
		super();
		this.mapper = mapper;
	}

	public AmiRelayTransformManager getMapper() {
		return mapper;
	}

	public void map(AmiRelayMessage action, OutputPort<AmiRelayMessage> out) {
		mapper.getThreadSafeTransforms().map(action, out);
	}

}
