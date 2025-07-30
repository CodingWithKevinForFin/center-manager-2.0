package com.vortex.web.portlet.visuals;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.visual.GraphContextMenuFactory;
import com.f1.suite.web.portal.impl.visual.GraphListener;
import com.f1.suite.web.portal.impl.visual.GraphPortlet;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Edge;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Node;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.agg.IntegerAggregator;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.client.VortexClientManager;
import com.vortex.client.VortexClientNetAddress;
import com.vortex.client.VortexClientNetConnection;
import com.vortex.client.VortexClientProcess;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.messages.VortexMachineIdInterPortletMessage;

public class VortexWebNetConnectionsGraphPortlet extends GridPortlet implements FormPortletListener, GraphListener, VortexClientMachineListener, GraphContextMenuFactory {

	private GraphPortlet graph;
	private HtmlPortlet key;
	private FormPortlet ports;
	private FormPortlet buttons;
	private Map<FormPortletCheckboxField, Integer> portCheckbox = new IdentityHashMap<FormPortletCheckboxField, Integer>();
	private IntKeyMap<Boolean> visiblePorts = new IntKeyMap<Boolean>();
	private VortexWebEyeService service;
	private VortexClientManager agentManager;
	private LongKeyMap<Node> nodesByMachineId;
	private BasicPortletSocket sendMiidSocket;
	private Node othersNode;
	private FormPortletButton selectAllButton;
	private FormPortletButton refresh;
	private FormPortletButton deselectAll;
	private TreeMap<Integer, String> serverPortNames = new TreeMap<Integer, String>();

	public VortexWebNetConnectionsGraphPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		this.graph = new GraphPortlet(generateConfig());
		this.graph.addGraphListener(this);
		this.ports = new FormPortlet(generateConfig());
		this.buttons = new FormPortlet(generateConfig());
		this.key = new HtmlPortlet(generateConfig(), "");
		this.addChild(ports, 0, 0, 1, 1);
		this.addChild(buttons, 0, 1, 1, 2);

		this.addChild(key, 1, 2, 1, 1);
		this.addChild(graph, 1, 0, 1, 2);

		this.setRowSize(1, 100);
		this.setRowSize(2, 20);
		ports.addFormPortletListener(this);
		buttons.addFormPortletListener(this);
		this.setColSize(0, 180);
		service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		agentManager = service.getAgentManager();
		nodesByMachineId = new LongKeyMap<Node>();

		this.selectAllButton = this.buttons.addButton(new FormPortletButton("Select All"));
		this.deselectAll = this.buttons.addButton(new FormPortletButton("Deselect All"));
		this.refresh = this.buttons.addButton(new FormPortletButton("Refresh"));
		this.sendMiidSocket = addSocket(true, "sendMiid", "Send Machine ID", true, CH.s(VortexMachineIdInterPortletMessage.class), null);
		this.service.getAgentManager().addMachineListener(this);
		this.graph.setMenuFactory(this);
		refresh();

	}

	@Override
	public void close() {
		this.service.getAgentManager().removeMachineListener(this);
		super.close();
	}
	public void refresh() {

		this.nodesByMachineId.clear();
		this.graph.clear();
		this.portCheckbox.clear();
		this.visiblePorts.clear();
		this.ports.clearFields();
		int x = 100;
		int y = 100;
		updatePortNames();
		//get server ports
		for (VortexClientMachine i : service.getAgentManager().getAgentMachines()) {
			//if (CH.isEmpty(i.getNetConnections()))
			//continue;
			Node node = graph.addNode(x, y, 80, 30, SH.beforeFirst(i.getHostName(), '.'), "_cna=");
			node.setData(i.getId());

			long id = i.getId();
			nodesByMachineId.put(id, node);
			for (VortexClientNetConnection con : i.getNetConnections()) {
				if (con.getData().getState() != VortexAgentNetConnection.STATE_ESTABLISHED)
					continue;
				VortexClientNetAddress remote = agentManager.getNetAddressByIp(con.getData().getForeignHost(), i);
				//if (serverPorts.containsKey(con.getData().getLocalPort()))
				//CH.incrementValue(serverPorts, con.getData().getLocalPort());
				//if (serverPorts.containsKey(con.getData().getForeignPort()))
				//CH.incrementValue(serverPorts, con.getData().getForeignPort());
			}
			y += 50;
			if (y > 300) {
				y = 50;
				x += 100;
			} else
				x += 20;
		}
		for (Entry<Integer, String> port : serverPortNames.entrySet()) {
			String name;
			if (SH.is(port.getKey())) {
				name = port.getValue() + " (" + SH.toString(port.getKey().intValue()) + ")";
			} else
				name = SH.toString(port.getKey().intValue());
			portCheckbox.put(ports.addField(new FormPortletCheckboxField(name, false)), port.getKey());
		}
		int count = this.graph.getNodesCount();
		if (count > 2) {
			double radius = count * 160 / MH.TAU;
			double inc = MH.TAU / count;
			double t = 0;
			double xscale = .60;
			double yscale = .29;
			for (Node n : this.graph.getNodes()) {
				n.setXY((int) ((150 + radius + radius * Math.cos(t)) * xscale), (int) ((60 + radius + radius * Math.sin(t)) * yscale));
				t += inc;
			}
		}
		this.othersNode = this.graph.addNode(55, 80, 100, 150, "other", "");
		doEdges();
	}
	private void updatePortNames() {
		serverPortNames.clear();
		for (VortexClientMachine i : service.getAgentManager().getAgentMachines())
			for (VortexClientNetConnection con : i.getNetConnections())
				if (con.getData().getState() == VortexAgentNetConnection.STATE_LISTEN) {
					visiblePorts.put(con.getData().getLocalPort(), Boolean.FALSE);
					VortexClientProcess proc = con.getProcess();
					int port = con.getData().getLocalPort();
					String existing = serverPortNames.get(port);
					if (proc != null) {
						String name = SH.dddLast(proc.getName(), 12);
						if (existing == null)
							serverPortNames.put(port, name);
						else if (!name.equals(existing))
							serverPortNames.put(port, "");
					}
				}

	}

	public static class Builder extends AbstractPortletBuilder<VortexWebNetConnectionsGraphPortlet> {
		private static final String ID = "NetConnections";
		public Builder() {
			super(VortexWebNetConnectionsGraphPortlet.class);
			setIcon("portlet_icon_graph");
		}
		@Override
		public VortexWebNetConnectionsGraphPortlet buildPortlet(PortletConfig portletConfig) {
			VortexWebNetConnectionsGraphPortlet portlet = new VortexWebNetConnectionsGraphPortlet(portletConfig);
			return portlet;
		}
		@Override
		public String getPortletBuilderName() {
			return "Connections Topology";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.deselectAll) {
			for (FormPortletCheckboxField f : this.portCheckbox.keySet())
				f.setValue(false);
			for (com.f1.utils.structs.IntKeyMap.Node<Boolean> e : visiblePorts)
				e.setValue(false);
			doEdges();
		} else if (button == this.selectAllButton) {
			for (FormPortletCheckboxField f : this.portCheckbox.keySet())
				f.setValue(true);
			for (com.f1.utils.structs.IntKeyMap.Node<Boolean> e : visiblePorts)
				e.setValue(true);
			doEdges();
		} else if (button == this.refresh) {
			refresh();
		}

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field instanceof FormPortletCheckboxField) {
			Integer port = portCheckbox.get(field);
			if (port != null) {
				visiblePorts.put(port, ((FormPortletCheckboxField) field).getValue());
				doEdges();
			}
		}
	}

	private void doEdges() {
		graph.clearEdges();
		IntKeyMap<String> portColors = new IntKeyMap<String>();
		for (com.f1.utils.structs.IntKeyMap.Node<Boolean> i : this.visiblePorts) {
			if (i.getValue())
				portColors.put(i.getKey(), WebHelper.getUniqueColorNoBlack(portColors.size()));
		}
		BasicMultiMap.Set<Long, Object> connections = new BasicMultiMap.Set<Long, Object>();
		for (VortexClientMachine i : service.getAgentManager().getAgentMachines()) {
			if (CH.isEmpty(i.getNetConnections()))
				continue;
			for (VortexClientNetConnection con : i.getNetConnections()) {
				if (con.getData().getState() != VortexAgentNetConnection.STATE_ESTABLISHED)
					continue;
				Boolean v1 = this.visiblePorts.get(con.getData().getLocalPort());
				Boolean v2 = this.visiblePorts.get(con.getData().getForeignPort());
				if (!(v1 == Boolean.TRUE || v2 == Boolean.TRUE))
					continue;
				VortexClientNetAddress remote = agentManager.getNetAddressByIp(con.getData().getForeignHost(), i);
				boolean include = false;
				int port;
				if (v1 == Boolean.TRUE) {
					port = con.getData().getLocalPort();
				} else if (con.getIsLoopback() && v2 == Boolean.TRUE) {
					port = con.getData().getForeignPort();
				} else if (v2 == Boolean.TRUE && remote == null) {
					port = con.getData().getForeignPort();
				} else
					continue;
				long id = i.getId();
				long id2 = -1;
				if (con.getIsLoopback()) {
					id2 = id;
				} else {
					if (remote != null) {
						id2 = remote.getMachineId();
					}
				}
				//VortexClientNetConnection con2 = con.getRemoteConnection();
				//VortexClientProcess process = con.getProcess();
				//if (con2 != null)
				//System.out.println(con2.getProcess().getData());
				//if ("rcooke".equals(process.getData()))
				//continue;
				Edge edge;
				if (id2 != -1) {
					Node n1 = nodesByMachineId.get(id);
					Node n2 = nodesByMachineId.get(id2);
					edge = graph.addEdge(n2.getId(), n1.getId(), GraphPortlet.DIRECTION_FORWARD);
				} else {
					Node n1 = nodesByMachineId.get(id);
					Node n2 = othersNode;
					edge = graph.addEdge(n2.getId(), n1.getId(), GraphPortlet.DIRECTION_FORWARD);
				}
				String color = portColors.get(port);
				edge.setColor(OH.noNull(color, "#000000"));
			}
		}
		//Set<String> names = new TreeSet<String>();
		//for (Entry<Long, Set<Object>> i : connections.entrySet()) {
		//for (Object v : i.getValue()) {
		//if (v instanceof String) {
		//names.add((String) v);
		//graph.addEdge(nodesByMachineId.get(i.getKey()).getId(), othersNode.getId());
		//} else
		//graph.addEdge(nodesByMachineId.get(i.getKey()).getId(), nodesByMachineId.get((Long) v).getId());
		//}
		//}
		for (Node node : this.graph.getNodes()) {
			Long id = (Long) node.getData();
			if (id == null)
				continue;
			VortexClientMachine am = agentManager.getAgentMachine(id);
			boolean isServer = false;
			if (am != null) {
				for (VortexClientNetConnection conn : am.getNetConnections()) {
					if (conn.getData().getState() == VortexAgentNetConnection.STATE_LISTEN && visiblePorts.get(conn.getData().getLocalPort()) == Boolean.TRUE) {
						isServer = true;
						break;
					}
				}
			}
			node.setCssStyle(isServer ? "_cna=nc_server" : "");

		}
		//updatePortNames();
		StringBuilder keyHtml = new StringBuilder();
		for (int i : portColors.keys()) {
			String color = portColors.get(i);
			keyHtml.append("<span style=\"color:").append(color).append("\">");
			keyHtml.append(serverPortNames.get(i)).append(": ").append(i);
			keyHtml.append("<span>&nbsp;&nbsp;&nbsp;");
		}
		key.setHtml(keyHtml.toString());
		//this.othersNode.setHtml(SH.join("<BR>", names));

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
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		onNode(node);
	}
	private void onNode(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_NET_CONNECTION) {
			VortexClientNetConnection con = (VortexClientNetConnection) node;
			Boolean v1 = this.visiblePorts.get(con.getData().getLocalPort());
			Boolean v2 = this.visiblePorts.get(con.getData().getForeignPort());
			if (v1 != null || v2 != null) {
				if (v1 != null && v2 != null && v1 == Boolean.FALSE && v2 == Boolean.FALSE)
					return;
				if (v1 != Boolean.TRUE && v2 != Boolean.TRUE)
					return;
			} else
				return;
			doEdges();
		}

	}
	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		onNode(node);

	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		onNode(node);

	}
	@Override
	public void onSelectionChanged(GraphPortlet graphPortlet) {
		if (!sendMiidSocket.hasConnections())
			return;
		if (sendMiidSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (GraphPortlet.Node node : graphPortlet.getSelectedNodes()) {
				if (node != this.othersNode)
					selections.add((Long) node.getData());
			}
			sendMiidSocket.sendMessage(new VortexMachineIdInterPortletMessage(selections));
		}
	}
	@Override
	public WebMenu createMenu(GraphPortlet graph) {
		BasicWebMenu r = new BasicWebMenu();
		if (graph.getSelectedCount() > 1) {
			r.addChild(new BasicWebMenuLink("Left Align", true, "aleft"));
			r.addChild(new BasicWebMenuLink("Right Align", true, "aright"));
			r.addChild(new BasicWebMenuLink("Top Align", true, "atop"));
			r.addChild(new BasicWebMenuLink("Bottom Align", true, "abot"));
			r.addChild(new BasicWebMenuLink("Arrange Vertically", true, "svert"));
			r.addChild(new BasicWebMenuLink("Arrange Horizontally", true, "shori"));
		}
		return r;
	}
	@Override
	public void onContextMenu(GraphPortlet graphPortlet, String action) {
		if (graphPortlet.getSelectedCount() > 0) {
			if ("aleft".equals(action) || "svert".equals(action)) {
				IntegerAggregator agg = new IntegerAggregator();
				for (Node i : graphPortlet.getSelectedNodes())
					agg.add(i.getX());
				for (Node i : graphPortlet.getSelectedNodes())
					i.setXY(agg.getMin(), i.getY());
			} else if ("aright".equals(action)) {
				IntegerAggregator agg = new IntegerAggregator();
				for (Node i : graphPortlet.getSelectedNodes())
					agg.add(i.getX());
				for (Node i : graphPortlet.getSelectedNodes())
					i.setXY(agg.getMax(), i.getY());
			} else if ("atop".equals(action) || "shori".equals(action)) {
				IntegerAggregator agg = new IntegerAggregator();
				for (Node i : graphPortlet.getSelectedNodes())
					agg.add(i.getY());
				for (Node i : graphPortlet.getSelectedNodes())
					i.setXY(i.getX(), agg.getMin());
			} else if ("abot".equals(action)) {
				IntegerAggregator agg = new IntegerAggregator();
				for (Node i : graphPortlet.getSelectedNodes())
					agg.add(i.getY());
				for (Node i : graphPortlet.getSelectedNodes())
					i.setXY(i.getX(), agg.getMax());
			}
			if ("svert".equals(action)) {
				IntegerAggregator agg = new IntegerAggregator();
				int height = 0;
				for (Node i : graphPortlet.getSelectedNodes()) {
					agg.add(i.getTop());
					agg.add(i.getBottom());
					height += i.getHeight();
				}
				double padding = 1d * (agg.getMax() - agg.getMin() - height) / ((agg.getCount() / 2) - 1);
				double pos = agg.getMin();
				if (padding < 1) {
					pos -= (height - (agg.getMax() - agg.getMin())) / 2;
					if (pos < 0)
						pos = 0;
					padding = 1;
				}
				List<Node> nodes = CH.sort(graphPortlet.getSelectedNodes(), GraphPortlet.Y_POSITION_COMPARATOR);
				for (Node i : nodes) {
					i.setTop((int) pos);
					pos += i.getHeight() + padding;
				}
			} else if ("shori".equals(action)) {
				IntegerAggregator agg = new IntegerAggregator();
				int width = 0;
				for (Node i : graphPortlet.getSelectedNodes()) {
					agg.add(i.getLeft());
					agg.add(i.getRight());
					width += i.getWidth();
				}
				double padding = 1d * (agg.getMax() - agg.getMin() - width) / ((agg.getCount() / 2) - 1);
				double pos = agg.getMin();
				if (padding < 1) {
					pos -= (width - (agg.getMax() - agg.getMin())) / 2;
					if (pos < 0)
						pos = 0;
					padding = 1;
				}
				List<Node> nodes = CH.sort(graphPortlet.getSelectedNodes(), GraphPortlet.X_POSITION_COMPARATOR);
				for (Node i : nodes) {
					i.setLeft((int) pos);
					pos += i.getWidth() + padding;
				}
			}
		}
	}

	@Override
	public void onMachineActive(VortexClientMachine machine) {
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
