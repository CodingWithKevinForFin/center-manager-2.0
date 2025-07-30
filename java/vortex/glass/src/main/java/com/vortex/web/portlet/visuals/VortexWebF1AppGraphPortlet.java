package com.vortex.web.portlet.visuals;

import java.util.Map;

import com.f1.povo.f1app.F1AppContainerScope;
import com.f1.povo.f1app.F1AppPort;
import com.f1.povo.f1app.F1AppProcessor;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.visual.GraphListener;
import com.f1.suite.web.portal.impl.visual.GraphPortlet;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Node;
import com.f1.utils.structs.LongKeyMap;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebContainerScope;
import com.vortex.client.VortexClientF1AppState.AgentWebObject;
import com.vortex.client.VortexClientManager;
import com.vortex.web.VortexWebEyeService;

public class VortexWebF1AppGraphPortlet extends GridPortlet implements FormPortletListener, GraphListener {

	private GraphPortlet graph;
	private FormPortlet apps;
	private VortexWebEyeService service;
	private VortexClientManager agentManager;
	private FormPortletSelectField<Long> appsSelect;
	private LongKeyMap<GraphPortlet.Node> ids2nodes = new LongKeyMap<GraphPortlet.Node>();

	public VortexWebF1AppGraphPortlet(PortletConfig config) {
		super(config);
		this.graph = new GraphPortlet(generateConfig());
		this.apps = new FormPortlet(generateConfig());
		this.appsSelect = this.apps.addField(new FormPortletSelectField<Long>(Long.class, "F1 Apps"));
		addChild(apps, 0, 0);
		addChild(graph, 0, 1);
		apps.addFormPortletListener(this);
		graph.addGraphListener(this);
		setRowSize(0, 25);
		service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		agentManager = service.getAgentManager();
		for (VortexClientF1AppState app : agentManager.getJavaAppStates()) {
			appsSelect.addOption(app.getSnapshot().getF1AppInstanceId(), app.getSnapshot().getMainClassName());
			//for (com.f1.utils.structs.LongKeyMap.Node<AgentWebContainerScope<F1AppContainerScope>> container : app.getContainers()) {
			//
			//}
		}
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebF1AppGraphPortlet> {
		private static final String ID = "AppGraph";
		public Builder() {
			super(VortexWebF1AppGraphPortlet.class);
			setIcon("portlet_icon_graph");
		}
		@Override
		public VortexWebF1AppGraphPortlet buildPortlet(PortletConfig portletConfig) {
			VortexWebF1AppGraphPortlet portlet = new VortexWebF1AppGraphPortlet(portletConfig);
			return portlet;
		}
		@Override
		public String getPortletBuilderName() {
			return "F1 Application Topology";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.appsSelect) {
			this.graph.clear();
			this.ids2nodes.clear();
			Long val = appsSelect.getValue();
			VortexClientF1AppState app = agentManager.getJavaAppState(val);
			if (app == null)
				return;
			for (AgentWebContainerScope<F1AppContainerScope> container : app.getContainers().values()) {
				addProcessors(container);
			}
			for (AgentWebContainerScope<F1AppContainerScope> container : app.getContainers().values()) {
				addConnections(container);
			}
		}
	}

	private void addConnections(AgentWebContainerScope<F1AppContainerScope> obj) {
		if (obj.getObject() instanceof F1AppPort) {
			F1AppPort port = (F1AppPort) obj.getObject();
			Node n1 = ids2nodes.get(port.getParentId());
			Node n2 = ids2nodes.get(port.getConnectedTo());
			if (n1 != null && n2 != null)
				graph.addEdge(n1.getId(), n2.getId(), GraphPortlet.DIRECTION_FORWARD);
		}
		for (AgentWebObject<?> child : obj.getChildren().values()) {
			if (child.getObject() instanceof F1AppContainerScope)
				addConnections((AgentWebContainerScope<F1AppContainerScope>) child);
		}

	}

	private void addProcessors(AgentWebObject<F1AppContainerScope> container) {
		if (container.getObject() instanceof F1AppProcessor) {
			String name = container.getObject().getName();
			ids2nodes.put(container.getObject().getContainerScopeId(), graph.addNode(100, ids2nodes.size() * 40 + 40, 200, 30, name, ""));
		}
		for (AgentWebObject<?> child : container.getChildren().values()) {
			if (child.getObject() instanceof F1AppContainerScope)
				addProcessors((AgentWebObject<F1AppContainerScope>) child);
		}
	}

	@Override
	public void onSelectionChanged(GraphPortlet graphPortlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onContextMenu(GraphPortlet graphPortlet, String action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserClick(GraphPortlet graphPortlet, Node nodeOrNull, int button, boolean ctrl, boolean shft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserDblClick(GraphPortlet graphPortlet, Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyDown(String keyCode, String ctrl) {
		// TODO Auto-generated method stub
		
	}
}
