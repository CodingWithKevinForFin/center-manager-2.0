/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

import java.util.Collection;
import java.util.List;

import com.f1.base.Action;
import com.f1.container.RequestInputPort;
import com.f1.container.impl.BasicSuite;
import com.f1.utils.structs.BasicMultiMap;

public class WebServiceSuite extends BasicSuite {

	private BasicMultiMap.List<String, RequestInputPort<Action, Action>> outboundInputTopics = new BasicMultiMap.List<String, RequestInputPort<Action, Action>>();

	protected void createInputTopic(String topicName, RequestInputPort<? extends Action, ? extends Action> port) {
		outboundInputTopics.putMulti(topicName, (RequestInputPort<Action, Action>) exposeInputPort(port));
	}

	public Collection<String> getOutboundTopics() {
		return outboundInputTopics.keySet();
	}

	public List<RequestInputPort<Action, Action>> getOutboundTopicInputPort(String topicName) {
		return outboundInputTopics.get(topicName);
	}

	public void initTopics() {

	}

}
