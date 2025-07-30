package com.f1.ami.web;

import com.f1.ami.amicommon.AmiPlugin;

public interface AmiWebGuiServicePlugin extends AmiPlugin {

	AmiWebGuiServiceAdapter createGuiIntegrationAdapter(AmiWebService service);
}
