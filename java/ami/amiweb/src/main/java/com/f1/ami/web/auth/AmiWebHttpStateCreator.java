package com.f1.ami.web.auth;

import com.f1.suite.web.PortalHttpStateCreator;
import com.f1.suite.web.WebState;
import com.f1.suite.web.WebStatesManager;

public class AmiWebHttpStateCreator extends PortalHttpStateCreator {

	public AmiWebHttpStateCreator(int n) {
		super(n);
	}

	@Override
	protected WebState newState(WebStatesManager wsm, String pgid) {
		AmiWebStatesManager awsm = (AmiWebStatesManager) wsm;
		return new AmiWebState(awsm, pgid, "Session " + awsm.getNextId());
	}
}
