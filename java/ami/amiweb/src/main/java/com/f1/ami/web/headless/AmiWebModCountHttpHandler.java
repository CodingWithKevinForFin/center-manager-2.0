package com.f1.ami.web.headless;

import java.io.IOException;

import com.f1.ami.web.auth.AmiWebStatesManager;
import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.WebStatesManager;

public class AmiWebModCountHttpHandler extends AbstractHttpHandler {

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		super.handle(request);
		final AmiWebStatesManager wsm = (AmiWebStatesManager) WebStatesManager.get(request.getSession(false));
		int hmc = 0;
		if (wsm != null && wsm.isDev()) {
			AmiWebHeadlessManager hm = wsm.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
			hmc = hm.getModCount();
		}
		if (wsm != null && wsm.isLoggedIn())
			request.getOutputStream().print(toModCount(wsm.getModCount(), hmc));
		else
			request.getOutputStream().print("none");
	}

	public static int toModCount(int mc1, int mc2) {
		return mc1 + mc2 * 10000;
	}

}
