package com.f1.ami.web;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.suite.web.HttpRequestAction;

public interface AmiWebEntitlementsPlugin extends AmiPlugin {

	AmiAuthUser processEntitlements(AmiAuthUser user, HttpRequestAction req) throws Exception;

}
