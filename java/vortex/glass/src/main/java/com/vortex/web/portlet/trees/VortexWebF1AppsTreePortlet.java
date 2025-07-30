package com.vortex.web.portlet.trees;

import java.util.Map;

import com.f1.povo.f1app.F1AppContainerScope;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppProperty;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebObject;
import com.vortex.client.VortexClientF1AppState.AgentWebProperty;
import com.vortex.client.VortexClientF1AppStateListener;
import com.vortex.client.VortexClientManager;
import com.vortex.client.VortexClientManagerListener;
import com.vortex.web.VortexWebEyeService;

public class VortexWebF1AppsTreePortlet extends FastTreePortlet implements VortexClientF1AppStateListener, VortexClientManagerListener {

	private VortexWebEyeService service;
	private LongKeyMap<WebTreeNode> uidsToNodes = new LongKeyMap<WebTreeNode>();

	public VortexWebF1AppsTreePortlet(PortletConfig portletConfig) {
		super(portletConfig, new FastWebTree(portletConfig.getPortletManager().getTextFormatter()));
		getTreeManager().getRoot().setName("F1 Enable Applications");
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		service.getAgentManager().addClientConnectedListener(this);
		service.getAgentManager().addF1AppListener(this);
	}

	@Override
	public void close() {
		service.getAgentManager().removeClientConnectedListener(this);
		service.getAgentManager().removeF1AppListener(this);
		super.close();
	}

	@Override
	public void onF1AppAdded(VortexClientF1AppState appState) {
		onJavaApp(appState, getTree().getTreeManager().getRoot());
	}
	private void onJavaApp(VortexClientF1AppState appState, WebTreeNode parent) {

		F1AppInstance app = appState.getSnapshot();
		WebTreeNode node = createNode(app.getHostName() + "::" + SH.afterLast(app.getAppName(), '.'), parent, false);
		uidsToNodes.put(appState.getSnapshot().getId(), node);

		WebTreeNode propertiesNode = createNode("Properties", node, false);

		LongKeyMap<AgentWebProperty> properties = appState.getProperties();
		for (AgentWebProperty wprop : properties.values()) {
			F1AppProperty prop = wprop.getObject();
			if (prop.getPosition() == 0) {
				createNode(prop.getKey() + "=" + (prop.getIsSecure() ? "[password not displayed]" : prop.getValue()), propertiesNode, false);
			}
		}

		WebTreeNode pathsNode = createNode("Paths", node, false);

		createNode("Pwd = " + app.getPwd(), pathsNode, false);
		createNode("Java Home = " + app.getJavaHome(), pathsNode, false);
		WebTreeNode bootclassPath = createNode("Classpath", pathsNode, false);
		for (String s : app.getBootClasspath())
			createNode(s, bootclassPath, false);

		WebTreeNode classPath = createNode("Boot Classpath", pathsNode, false);
		for (String s : app.getClasspath())
			createNode(s, classPath, false);

		WebTreeNode javaExternalDirs = createNode("Java External Directories", pathsNode, false);
		for (String s : app.getJavaExternalDirs())
			createNode(s, javaExternalDirs, false);

		WebTreeNode argumentsNode = createNode("Arguments", node, false);
		WebTreeNode jvmArguments = createNode("Jvm Arguments", argumentsNode, false);
		for (String s : app.getJvmArguments())
			createNode(s, jvmArguments, false);
		WebTreeNode mainClassArguments = createNode("Main Class Arguments", argumentsNode, false);
		for (String s : app.getMainClassArguments())
			createNode(s, mainClassArguments, false);

		WebTreeNode memoryPools = createNode("Memory", node, false);
		for (String key : CH.sort(app.getMemoryPools().keySet()))
			createNode(key + " Pool = " + app.getMemoryPools().get(key), memoryPools, false);
		createNode("Total Memory = " + app.getTotalMemory(), memoryPools, false);
		createNode("Free Memory = " + app.getFreeMemory(), memoryPools, false);
		createNode("Max Memory = " + app.getMaxMemory(), memoryPools, false);

		WebTreeNode detailsNode = createNode("Details", node, false);
		createNode("Java Vendor = " + app.getJavaVendor(), detailsNode, false);
		createNode("Java Version = " + app.getJavaVersion(), detailsNode, false);
		createNode("Pid = " + app.getPid(), detailsNode, false);
		createNode("ProcesssUid = " + app.getProcessUid(), detailsNode, false);
		createNode("StartTime = " + app.getStartTimeMs(), detailsNode, false);
		WebTreeNode containersNode = createNode("ContainersNode", node, false);
		for (AgentWebObject<?> root : appState.getContainers().values()) {
			onContainerScope(appState, root, containersNode);
		}

	}
	private void onContainerScope(VortexClientF1AppState appState, AgentWebObject<?> object, WebTreeNode parent) {
		String name = object.getClassName();
		WebTreeNode node = createNode(name, parent, false);
		for (AgentWebObject<?> child : object.getChildren().values())
			onContainerScope(appState, child, node);

	}

	private void onContainerScope(F1AppContainerScope container, WebTreeNode node, Map<Long, F1AppEntity> map) {
		WebTreeNode containerNode = createNode(container.getName(), node, false);
		//for (long childId : container.getChildrenId()) {
		//AgentF1Object child = map.get(childId);
		//if (child instanceof AgentF1ContainerScope)
		//onContainerScope((AgentF1ContainerScope) child, containerNode, map);
		//}
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebF1AppsTreePortlet> {

		public static final String ID = "F1AppsTreePortlet";

		public Builder() {
			super(VortexWebF1AppsTreePortlet.class);
		}

		@Override
		public VortexWebF1AppsTreePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebF1AppsTreePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Apps Tree";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onF1AppRemoved(VortexClientF1AppState existing) {
		WebTreeNode node = uidsToNodes.removeOrThrow(existing.getSnapshot().getId());
		getTreeManager().removeNode(node);
	}

	@Override
	public void onVortexEyeDisconnected() {
		this.uidsToNodes.clear();
		getTreeManager().clear();
		getTreeManager().getRoot().setName("F1 Enable Applications");
	}

	@Override
	public void onF1AppEntityAdded(AgentWebObject<?> added) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onF1AppEntityUpdated(AgentWebObject<?> updated) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onF1AppEntityRemoved(AgentWebObject<?> removed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVortexEyeSnapshotProcessed() {

	}

	@Override
	public void onVortexClientListenerAdded(Object listener) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onVortexConnectionStateChanged(VortexClientManager vortexClientManager, VortexWebEyeService vortexWebEyeService) {
	}

}
