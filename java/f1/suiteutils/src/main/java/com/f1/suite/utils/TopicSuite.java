/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Action;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.BasicSuite;
import com.f1.utils.CH;

public class TopicSuite extends BasicSuite {

	private Map<String, MulticastProcessor<Action>> topics = new HashMap<String, MulticastProcessor<Action>>();
	private Map<String, InputPort<Action>> inputs = new HashMap<String, InputPort<Action>>();
	private String partitionName;

	public TopicSuite(String partitionName) {
		this.partitionName = partitionName;
	}

	public InputPort<Action> getChannel(String name) {
		InputPort<Action> r = inputs.get(name);
		if (r != null)
			return r;
		MulticastProcessor<Action> mcp = new MulticastProcessor<Action>(Action.class);
		mcp.setPartitionResolver(new BasicPartitionResolver<Action>(Action.class, partitionName));
		CH.putOrThrow(topics, name, mcp);
		addChild(mcp);
		inputs.put(name, r = exposeInputPort(mcp));
		return r;
	}

	public OutputPort<Action> createOutputForChannel(String name) {
		getChannel(name);
		assertNotStarted();
		MulticastProcessor<Action> mcp = CH.getOrThrow(topics, name, "topic not found");
		return exposeOutputPort(mcp.newOutputPort());
	}
}
