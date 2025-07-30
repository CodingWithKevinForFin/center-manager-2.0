package com.f1.ami.sample;

import java.util.Map;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.client.AmiClient;

public interface SampleCommandPlugin extends AmiPlugin {

	public String getCommandDef();

	public void onCommand(AmiClient client, String requestId, String cmd, String userName, String objectType, String objectId, Map<String, Object> params);

}
