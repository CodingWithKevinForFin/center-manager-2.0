/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

import com.f1.container.Partition;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.StateGenerator;
import com.f1.container.impl.AbstractContainerScope;

public class WebStateGenerator extends AbstractContainerScope implements StateGenerator<RequestMessage<HttpRequestAction>, WebState> {

	public WebStateGenerator() {
	}

	@Override
	public WebState createState(Partition partition, RequestMessage<HttpRequestAction> a, Processor<? extends RequestMessage<HttpRequestAction>, ?> processor) {
		WebState r = nw(WebState.class);
		//		r.setFormatter(getServices().getLocaleFormatter());
		return r;
	}

	@Override
	public Class getStateType() {
		return WebState.class;
	}
}
