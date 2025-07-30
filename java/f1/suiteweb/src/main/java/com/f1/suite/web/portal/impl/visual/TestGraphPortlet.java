package com.f1.suite.web.portal.impl.visual;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.GridPortlet;

public class TestGraphPortlet extends GridPortlet {

	final private GraphPortlet graph;

	public TestGraphPortlet(PortletConfig config) {
		super(config);
		this.graph = new GraphPortlet(generateConfig());

		graph.addNode(200, 200, 50, 50, "dave", "");
		graph.addNode(300, 100, 50, 50, "steve", "");
		graph.addNode(400, 400, 50, 50, "justin", "");
		graph.addNode(500, 400, 50, 50, "eric", "");
		graph.addEdge(1, 2, GraphPortlet.DIRECTION_FORWARD);
		graph.addEdge(2, 4, GraphPortlet.DIRECTION_FORWARD);
		graph.addEdge(4, 3, GraphPortlet.DIRECTION_FORWARD);
		addChild(graph, 0, 0);
	}

	public static class Builder extends AbstractPortletBuilder<TestGraphPortlet> {

		private static final String ID = "testGraph";

		public Builder() {
			super(TestGraphPortlet.class);
		}

		@Override
		public TestGraphPortlet buildPortlet(PortletConfig portletConfig) {
			TestGraphPortlet portlet = new TestGraphPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Test Graph";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}
}
