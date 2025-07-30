package com.vortex.web.portlet.trees;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeFilter;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.client.VortexClientManager;
import com.vortex.client.VortexClientManagerListener;
import com.vortex.client.VortexClientNetConnection;
import com.vortex.client.VortexClientProcess;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.messages.VortexPidInterPortletMessage;

public class VortexWebConnectionTreePortlet extends FastTreePortlet implements VortexClientMachineListener, VortexClientManagerListener, WebTreeContextMenuListener,
		WebTreeContextMenuFactory, Comparator<WebTreeNode>, WebTreeFilter {

	private static final Logger log = LH.get(VortexWebConnectionTreePortlet.class);

	private VortexWebEyeService service;
	//private BasicPortletSocket maskSocket;
	//private BasicPortletSocket mask2Socket;
	private LongKeyMap<WebTreeNode> processParentNodes = new LongKeyMap<WebTreeNode>();
	private MapInMap<Long, Long, WebTreeNode> processChildNodesByChildParentId = new MapInMap<Long, Long, WebTreeNode>();
	private LongKeyMap<WebTreeNode> connectionNodes = new LongKeyMap<WebTreeNode>();
	//private List<Matcher> treeMasks = Collections.EMPTY_LIST;

	final private BasicPortletSocket pidSocket;
	final private BasicPortletSocket sendPidSocket;

	private Set<Tuple2<String, String>> hostAndPidsFilter = Collections.EMPTY_SET;

	public VortexWebConnectionTreePortlet(PortletConfig portletConfig) {
		super(portletConfig);
		service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		//getTree().getTreeManager().getRoot().setName("All Network Processes").setCssClass("clickable");
		getTree().addMenuContextListener(this);
		getTree().setContextMenuFactory(this);
		getTree().getTreeManager().setComparator(this);
		getTree().setRootLevelVisible(false);
		getTree().setAutoExpandUntilMultipleNodes(false);
		//this.maskSocket = addSocket(false, "selection", "Node Selection", false, null, CH.s(NodeSelectionInterPortletMessage.class));
		//this.mask2Socket = addSocket(true, "pushselection", "Node Selection:Push", true, CH.s(NodeSelectionInterPortletMessage.class), null);

		this.pidSocket = addSocket(false, "pid", "Pid", false, null, CH.s(VortexPidInterPortletMessage.class));
		this.sendPidSocket = addSocket(true, "sendPid", "Pid", true, CH.s(VortexPidInterPortletMessage.class), null);
		service.getAgentManager().addClientConnectedListener(this);
		service.getAgentManager().addMachineListener(this);

		for (VortexClientMachine machine : service.getAgentManager().getAgentMachines()) {
			for (VortexClientProcess process : machine.getProcesses()) {
				if (process.hasConnections()) {
					WebTreeNode pnode = addParentProcessNode(process, true);
				}
			}
		}
	}
	private WebTreeNode addConnectionNode(WebTreeNode anode, VortexClientNetConnection con) {
		WebTreeNode r = getTreeManager().createNode(describe(con), anode, false, con)
				.setIcon(con.getData().getState() == VortexAgentNetConnection.STATE_LISTEN ? "portlet_icon_socket" : "portlet_icon_connection").setCssClass("darkgray");
		connectionNodes.put(con.getId(), r);
		return r;

	}

	@Override
	public void onClosed() {
		super.onClosed();
		service.getAgentManager().removeMachineListener(this);
		service.getAgentManager().removeClientConnectedListener(this);
	}
	private String describe(VortexClientNetConnection con) {
		VortexAgentNetConnection data = con.getData();
		String port = SH.toString(data.getLocalPort());
		if (data.getState() == VortexAgentNetConnection.STATE_LISTEN) {
			if ("*".equals(data.getLocalHost()))
				return "server socket " + port;
			else
				return "server socket " + data.getLocalHost() + ":" + port;
		} else {
			return data.getLocalHost() + ":" + port + " -> " + data.getForeignHost() + ":" + SH.toString(data.getForeignPort());
		}
	}

	private String describe(VortexClientProcess remoteProcess) {
		return remoteProcess.getName() + " - " + remoteProcess.getData().getUser() + '@' + remoteProcess.getHostName() + " - " + remoteProcess.getPid();
	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (node == null)
			return;
		Object data = node.getData();
		if (data instanceof VortexClientProcess) {
			VortexClientProcess wap = (VortexClientProcess) data;
			WebTreeNode target = processParentNodes.get(wap.getId());
			if (target != null) {
				node.setSelected(false);
				target.setSelected(true);
			}
		}

	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		if (this.sendPidSocket.hasConnections()) {
			Set<Tuple2<String, String>> selected = new HashSet<Tuple2<String, String>>();
			for (WebTreeNode sel : fastWebTree.getSelected()) {
				Object data = sel.getData();
				if (data instanceof VortexClientProcess) {
					VortexClientProcess wap = (VortexClientProcess) data;
					selected.add(new Tuple2<String, String>(wap.getHostName(), SH.toString(wap.getPid())));
				}
			}
			this.sendPidSocket.sendMessage(new VortexPidInterPortletMessage(selected));
		}
	}

	@Override
	public void onMachineAdded(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_NET_CONNECTION:
				onConnectionAdded((VortexClientNetConnection) node);
				break;
			case VortexAgentEntity.TYPE_PROCESS:
				onProcessAdded((VortexClientProcess) node);
				break;
		}

	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_NET_CONNECTION:
				onConnectionRemoved((VortexClientNetConnection) node);
				break;
			case VortexAgentEntity.TYPE_PROCESS:
				onProcessRemoved((VortexClientProcess) node);
				break;
		}
	}

	private void onProcessAdded(VortexClientProcess node) {
		if (!node.hasConnections())
			return;
		WebTreeNode parentProcessNode = processParentNodes.get(node.getId());
		if (parentProcessNode == null) {
			parentProcessNode = addParentProcessNode(node, true);
		}
	}

	private WebTreeNode addParentProcessNode(VortexClientProcess process, boolean walkConnections) {
		WebTreeNode pnode = getTreeManager().createNode(describe(process), getTreeManager().getRoot(), false, process).setIcon("portlet_icon_process");
		processParentNodes.put(process.getId(), pnode);
		if (walkConnections)
			for (VortexClientNetConnection con : process.getConnections()) {
				onConnectionAdded(con);
			}
		return pnode;

	}
	private void onConnectionAdded(VortexClientNetConnection con) {
		if (connectionNodes.containsKey(con.getId()))
			return;

		final VortexClientProcess process = con.getProcess();
		if (process == null)
			return;
		WebTreeNode pnode = processParentNodes.get(process.getId());
		if (pnode == null)
			pnode = addParentProcessNode(process, false);
		if (con.getData().getState() == VortexAgentNetConnection.STATE_LISTEN) {
			WebTreeNode snode = addConnectionNode(pnode, con);
		} else {
			VortexClientNetConnection remoteConnection = con.getRemoteConnection();
			if (remoteConnection != null) {
				VortexClientProcess remoteProcess = remoteConnection.getProcess();
				if (remoteProcess != null) {
					createChildProcess(remoteProcess, process, con);
				} else
					addConnectionNode(pnode, con);
				WebTreeNode existingRemoteNode = connectionNodes.remove(remoteConnection.getId());
				if (existingRemoteNode != null) {
					//the addition of the supplied connection has caused the remote connection to now be linked... we need to remove and re-add.
					getTreeManager().removeNode(existingRemoteNode);
					if (remoteProcess != null)
						createChildProcess(process, remoteProcess, remoteConnection);
					else
						LH.warning(log, "TODO: what do we do when the remote connection is null?: ", process, ", ", remoteConnection);

				}
			} else {
				WebTreeNode cnode = addConnectionNode(pnode, con);
			}
		}
	}

	private void createChildProcess(VortexClientProcess remoteProcess, VortexClientProcess process, VortexClientNetConnection con) {
		WebTreeNode pnode = processParentNodes.get(process.getId());
		WebTreeNode anode = processChildNodesByChildParentId.getMulti(remoteProcess.getId(), process.getId());
		if (anode == null) {
			anode = getTreeManager().createNode(describe(remoteProcess), pnode, false, remoteProcess).setIcon("portlet_icon_process").setCssClass("clickable darkgray");
			processChildNodesByChildParentId.putMulti(remoteProcess.getId(), process.getId(), anode);
		}
		WebTreeNode cnode = addConnectionNode(anode, con);

	}
	private void onProcessRemoved(VortexClientProcess node) {
		WebTreeNode parentNode = processParentNodes.remove(node.getId());
		if (parentNode != null)
			getTreeManager().removeNode(parentNode);
		Map<Long, WebTreeNode> nodes = processChildNodesByChildParentId.remove(node.getId());
		if (nodes != null) {
			for (WebTreeNode childNode : nodes.values())
				getTreeManager().removeNode(childNode);
		}
		for (VortexClientNetConnection i : node.getConnections()) {
			WebTreeNode connectionNode = connectionNodes.remove(i.getId());
			if (connectionNode != null)
				getTreeManager().removeNode(connectionNode);
		}
	}

	private void onConnectionRemoved(VortexClientNetConnection webNode) {
		WebTreeNode node = connectionNodes.remove(webNode.getId());
		if (node != null)
			getTreeManager().removeNode(node);
		VortexClientProcess parentProcess = webNode.getProcess();
		if (parentProcess != null) {
			//was this the only connection in this parent?
			if (!parentProcess.hasConnections()) {
				WebTreeNode processNode = processParentNodes.remove(parentProcess.getId());
				if (processNode != null)
					getTreeManager().removeNode(processNode);
			}
		}
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebConnectionTreePortlet> {

		public static final String ID = "ConnectionTreePortlet";

		public Builder() {
			super(VortexWebConnectionTreePortlet.class);
		}

		@Override
		public VortexWebConnectionTreePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebConnectionTreePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Process Connections Tree";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {
		Class<? extends Object> clazz1 = o1.getData().getClass();
		Class<? extends Object> clazz2 = o2.getData().getClass();
		if (clazz1 != clazz2) {
			if (clazz1 == VortexClientProcess.class)
				return -1;
			else if (clazz2 == VortexClientProcess.class)
				return 1;
		}
		if (clazz1 == VortexClientNetConnection.class) {
			VortexClientNetConnection c1 = (VortexClientNetConnection) o1.getData();
			VortexClientNetConnection c2 = (VortexClientNetConnection) o2.getData();
			boolean l1 = c1.getIsLoopback();
			boolean l2 = c2.getIsLoopback();
			if (l1 != l2)
				return l1 ? -1 : 1;
		}
		return SH.COMPARATOR_CASEINSENSITIVE.compare(o1.getName(), o2.getName());
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == pidSocket) {
			VortexPidInterPortletMessage msg = (VortexPidInterPortletMessage) message;
			hostAndPidsFilter = msg.getHostAndPids();
			getTreeManager().setFilter(hostAndPidsFilter.size() == 0 ? null : this);
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}

	final private Tuple2<String, String> tmpHostAndPid = new Tuple2<String, String>();
	@Override
	public boolean shouldKeep(WebTreeNode node) {
		if (hostAndPidsFilter.size() > 0) {
			Object data = node.getData();
			if (data instanceof VortexClientProcess && node.getParent() == getTreeManager().getRoot()) {
				VortexClientProcess process = (VortexClientProcess) data;
				process.getMachine().getHostName();
				tmpHostAndPid.setAB(process.getHostName(), SH.toString(process.getPid()));
				return hostAndPidsFilter.contains(tmpHostAndPid);
			}

		}
		return true;
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVortexEyeDisconnected() {
		getTree().getTreeManager().clear();

	}
	@Override
	public void onVortexEyeSnapshotProcessed() {
	}
	@Override
	public void onVortexClientListenerAdded(Object listener) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onMachineActive(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onVortexConnectionStateChanged(VortexClientManager vortexClientManager, VortexWebEyeService vortexWebEyeService) {
	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
		// TODO Auto-generated method stub

	}
}
