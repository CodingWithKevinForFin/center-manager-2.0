package com.vortex.web.portlet.visuals;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.visual.TreemapNode;
import com.f1.suite.web.portal.impl.visual.TreemapPortlet;
import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.client.VortexClientProcess;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.messages.VortexMachineIdInterPortletMessage;

public class VortexWebMachineProcessesTreemapPortlet extends TreemapPortlet implements VortexClientMachineListener {

	private VortexWebEyeService service;
	private BasicPortletSocket miidSocket;
	private LongSet miids;
	private boolean dataChanged;
	private ColorGradient gradient = new ColorGradient();

	public VortexWebMachineProcessesTreemapPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.miidSocket = addSocket(false, "miid", "Machine ID", true, null, CH.s(VortexMachineIdInterPortletMessage.class));
		service.getAgentManager().addMachineListener(this);
		gradient.addStop(0, "#002200");
		gradient.addStop(.75, "#FFFF00");
		gradient.addStop(1, "#FF0000");
		addOption(OPTION_CATEGORY_BORDER_SIZE, "1");
		flagDataChanged();
	}

	@Override
	public void close() {
		service.getAgentManager().removeMachineListener(this);
		super.close();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebMachineProcessesTreemapPortlet> {
		private static final String ID = "ProcessesTreemap";
		public Builder() {
			super(VortexWebMachineProcessesTreemapPortlet.class);
		}
		@Override
		public VortexWebMachineProcessesTreemapPortlet buildPortlet(PortletConfig portletConfig) {
			VortexWebMachineProcessesTreemapPortlet portlet = new VortexWebMachineProcessesTreemapPortlet(portletConfig);
			return portlet;
		}
		@Override
		public String getPortletBuilderName() {
			return "Processes Treemap";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}

	@Override
	public void onMachineAdded(VortexClientMachine machine) {
		for (VortexClientProcess process : machine.getProcesses())
			onMachineEntityAdded(process);
	}
	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
		for (VortexClientProcess process : machine.getProcesses())
			onMachineEntityUpdated(process);
	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
		for (VortexClientProcess process : machine.getProcesses())
			onMachineEntityRemoved(process);
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		for (VortexClientProcess process : machine.getProcesses())
			onMachineEntityRemoved(process);
	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_PROCESS) {
			if (!isFiltered(node.getMachine())) {
				addNode((VortexClientProcess) node);
			}
		}

	}

	private void flagDataChanged() {
		this.dataChanged = true;
		flagPendingAjax();
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_PROCESS) {
			TreemapNode tmnode = this.process2nodes.get(node.getId());
			if (tmnode != null) {
				tmnode.setValue(getValue((VortexClientProcess) node));
				tmnode.setBgColor(getHeat((VortexClientProcess) node));
			}
		}

	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> agentNode) {
		if (agentNode.getType() == VortexAgentEntity.TYPE_PROCESS) {
			TreemapNode node = process2nodes.remove(agentNode.getId());
			if (node != null)
				removeNode(node.getId());
		}
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == miidSocket) {
			VortexMachineIdInterPortletMessage msg = (VortexMachineIdInterPortletMessage) message;
			miids = msg.getMiids();
			flagDataChanged();
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}

	private void redoRows() {
		clearNodes();
		this.process2nodes.clear();
		for (VortexClientMachine machine : service.getAgentManager().getAgentMachines()) {
			if (isFiltered(machine))
				continue;
			for (VortexClientProcess process : machine.getProcesses()) {
				addNode(process);
			}
		}
	}

	private boolean isFiltered(VortexClientMachine machine) {
		return miids != null && miids.size() > 0 && !miids.contains(machine.getMachineId());
	}
	private TreemapNode addNode(VortexClientProcess process) {
		if (process.getName().indexOf("AgentMain") != -1)
			return null;
		TreemapNode node = addNode(getRootNode(), process.getName(), getValue(process), getHeat(process), "#FFFFF", process.getName(),
				process.getHostName() + "-" + process.getName());
		process2nodes.put(process.getId(), node);
		return node;
	}
	private String getHeat(VortexClientProcess process) {
		return gradient.toColorRgb(process.getData().getCpuPercent());
	}
	private double getValue(VortexClientProcess process) {
		return process.getData().getMemory();
	}
	final private LongKeyMap<TreemapNode> process2nodes = new LongKeyMap<TreemapNode>();

	@Override
	public void drainJavascript() {
		if (dataChanged)
			redoRows();
		this.dataChanged = false;
		super.drainJavascript();
	}

	@Override
	public void onMachineActive(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

}
