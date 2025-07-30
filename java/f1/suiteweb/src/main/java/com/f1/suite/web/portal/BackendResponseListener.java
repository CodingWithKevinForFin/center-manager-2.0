package com.f1.suite.web.portal;

import com.f1.base.Action;
import com.f1.container.ResultMessage;

public interface BackendResponseListener {

	public void onBackendResponse(ResultMessage<Action> result);
}
