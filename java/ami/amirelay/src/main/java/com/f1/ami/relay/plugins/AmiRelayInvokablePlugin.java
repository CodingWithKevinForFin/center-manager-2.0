package com.f1.ami.relay.plugins;

import java.util.Map;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFH;

public interface AmiRelayInvokablePlugin extends AmiPlugin {

	//true for success, false for error
	public boolean invoke(AmiRelayIn server, Map<String, Object> params, AmiFH session, StringBuilder messageSink);

}
