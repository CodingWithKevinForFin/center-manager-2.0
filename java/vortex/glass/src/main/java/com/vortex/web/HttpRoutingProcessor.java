package com.vortex.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.WebState;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.structs.Tuple2;

public class HttpRoutingProcessor extends BasicProcessor<HttpRequestAction, WebState> {

	private final List<Tuple2<TextMatcher, OutputPort<HttpRequestAction>>> ports = new ArrayList<Tuple2<TextMatcher, OutputPort<HttpRequestAction>>>();
	private final Map<String, OutputPort<HttpRequestAction>> portsMap = new CopyOnWriteHashMap<String, OutputPort<HttpRequestAction>>();

	public HttpRoutingProcessor() {
		super(HttpRequestAction.class, WebState.class);
	}

	@Override
	public void processAction(HttpRequestAction action, WebState state, ThreadScope threadScope) throws Exception {
		String target = action.getTarget();
		OutputPort<HttpRequestAction> port = portsMap.get(target);
		if (port == null) {
			for (Tuple2<TextMatcher, OutputPort<HttpRequestAction>> p : ports)
				if (p.getA().matches(target)) {
					portsMap.put(target, port = p.getB());
					break;
				}
			if (port == null) {
				LH.warning(log, "No destination for: ", target);
				return;
			}

		}
		port.send(action, threadScope);
	}

	public OutputPort<HttpRequestAction> newOutputPort(String targetExpression) {
		assertNotInit();
		final OutputPort<HttpRequestAction> r = newOutputPort(HttpRequestAction.class);
		ports.add(new Tuple2<TextMatcher, OutputPort<HttpRequestAction>>(SH.m(targetExpression), r));
		return r;
	}

}
