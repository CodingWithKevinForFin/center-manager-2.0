package com.vortex.ssoweb;

import com.f1.suite.web.portal.Portlet;
import com.sso.messages.SsoUser;

public interface SsoUserDialog extends Portlet {

	public void setUser(SsoWebGroup group, SsoUser user);
}
