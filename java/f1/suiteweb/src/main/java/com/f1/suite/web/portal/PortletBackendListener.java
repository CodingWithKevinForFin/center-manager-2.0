package com.f1.suite.web.portal;

import com.f1.base.Action;

public interface PortletBackendListener {

	void onBackendCalled(PortletManager manager, Action action);
}
