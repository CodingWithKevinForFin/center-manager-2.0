package com.vortex.web.messages;

import java.util.Set;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.Tuple2;

public class VortexPidInterPortletMessage implements InterPortletMessage {

	private Set<Tuple2<String, String>> hostAndPids;

	public VortexPidInterPortletMessage(Set<Tuple2<String, String>> hostAndPids) {
		this.hostAndPids = hostAndPids;
	}

	public Set<Tuple2<String, String>> getHostAndPids() {
		return hostAndPids;
	}

}
