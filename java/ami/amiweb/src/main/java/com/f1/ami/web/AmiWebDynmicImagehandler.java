package com.f1.ami.web;

import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.HttpStateHandler;
import com.f1.suite.web.PortalHttpStateCreator;
import com.f1.suite.web.WebState;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.LH;

public class AmiWebDynmicImagehandler extends HttpStateHandler {

	public AmiWebDynmicImagehandler(PortalHttpStateCreator stateCreator) {
		super(stateCreator);
	}

	@Override
	public Object handle(HttpRequestAction request, WebState state) {
		PortletManager portletManager = (PortletManager) state.getPortletManager();
		if (portletManager == null) {
			LH.warning(log, "No portletManager for ", state.describeUser());
		}
		AmiWebService service = (AmiWebService) portletManager.getService(AmiWebService.ID);
		service.getChartImagesManager().onRequest(request);
		return null;
	}

	@Override
	public void handleAfterUnlock(HttpRequestResponse req, Object data) {
	}

}
