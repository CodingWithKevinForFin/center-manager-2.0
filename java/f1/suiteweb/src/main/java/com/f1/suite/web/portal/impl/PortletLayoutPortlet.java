package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.suite.web.tree.impl.WebTreeHelper;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.structs.Tuple2;

public class PortletLayoutPortlet extends FastTreePortlet implements PortletListener, WebTreeContextMenuListener, WebTreeContextMenuFactory {
	private static final int MODE_COPY = 1;
	private static final int MODE_CUT = 2;
	private static final int MODE_NONE = 3;

	private static final Logger log = Logger.getLogger(PortletLayoutPortlet.class.getName());

	private Map<String, WebTreeNode> treeNodes = new HashMap<String, WebTreeNode>();
	private Map<Tuple2<PortletSocket, PortletSocket>, WebTreeNode> socketNodes = new HashMap<Tuple2<PortletSocket, PortletSocket>, WebTreeNode>();

	public PortletLayoutPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		getTree().setRootLevelVisible(false);
		getTree().setRowHeight(18);
		addNode(getManager().getRoot(), getTreeManager().getRoot(), "Portlet Layout");
		getTree().addMenuContextListener(this);
		getManager().addPortletListener(this);
		getTree().setContextMenuFactory(this);
	}

	private void addNode(Portlet portlet, WebTreeNode treeNode, String name) {
		String icon;
		if (portlet instanceof RootPortlet)
			icon = "portlet_icon_window";
		else if (portlet.getPortletConfig().getBuilderId() == null)
			return;
		else
			icon = getManager().getPortletBuilder(portlet.getPortletConfig().getBuilderId()).getIcon();
		WebTreeNode newNode = getTreeManager().createNode(name, treeNode, portlet instanceof PortletContainer).setData(portlet.getPortletId()).setIcon(icon)
				.setCssClass("clickable");
		treeNodes.put(portlet.getPortletId(), newNode);
		if (portlet instanceof PortletContainer) {
			PortletContainer pc = (PortletContainer) portlet;
			for (Portlet child : pc.getChildren().values()) {
				addNode(child, newNode, child.getTitle());
			}
		}
		for (PortletSocket socket : portlet.getSockets().values())
			for (PortletSocket remote : socket.getRemoteConnections())
				addSocketNode(newNode, socket, remote, socket.getIsInitiator());
	}
	private WebTreeNode addSocketNode(WebTreeNode node, PortletSocket initiator, PortletSocket remote, boolean isInitiator) {
		Tuple2<PortletSocket, PortletSocket> key = new Tuple2<PortletSocket, PortletSocket>(initiator, remote);
		WebTreeNode r = getTreeManager().createNode(remote.getPortlet().getTitle(), node, false)
				.setIcon(isInitiator ? "portlet_icon_portlet_connection" : "portlet_icon_portlet_rconnection").setCssClass("clickable italic green").setData(key);
		socketNodes.put(key, r);
		return r;

	}

	public static class Builder extends AbstractPortletBuilder<PortletLayoutPortlet> {

		public Builder() {
			super(PortletLayoutPortlet.class);
		}

		public static final String ID = "PortletLayout";

		@Override
		public PortletLayoutPortlet buildPortlet(PortletConfig portletConfig) {
			return new PortletLayoutPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "PortletLayout";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onPortletAdded(Portlet newPortlet) {
	}

	@Override
	public void onPortletClosed(Portlet oldPortlet) {
		if (OH.eq(oldPortlet.getPortletId(), clipboardId)) {
			clipboardId = null;
			mode = MODE_NONE;
		}
		if (!treeNodes.containsKey(oldPortlet))
			return;
	}

	@Override
	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent) {
		if (oldParent != null) {
			WebTreeNode remove = treeNodes.remove(newPortlet.getPortletId());
			if (remove == null) {
				return;
			}
			getTreeManager().removeNode(remove);
		} else {
			PortletContainer parent = newPortlet.getParent();
			if (parent == null) {
				return;
			}
			WebTreeNode node = treeNodes.get(parent.getPortletId());
			if (node == null) {
				return;
			}
			addNode(newPortlet, node, newPortlet.getTitle());
		}
	}

	@Override
	public void onJavascriptQueued(Portlet portlet) {
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		List<WebTreeNode> selected = tree.getSelected();
		if ("copy".equals(action)) {
			if (selected.size() != 1)
				throw new IllegalStateException();
			WebTreeNode node = selected.get(0);
			String portletId = (String) node.getData();
			setMode(MODE_COPY, portletId);
		} else if ("cut".equals(action)) {
			if (selected.size() != 1)
				throw new IllegalStateException();
			WebTreeNode node = selected.get(0);
			String portletId = (String) node.getData();
			setMode(MODE_CUT, portletId);
			clipboardId = portletId;
		} else if ("clear".equals(action)) {
			setMode(MODE_NONE, null);
		} else if ("remove".equals(action)) {
			setMode(MODE_NONE, null);
			for (WebTreeNode selection : selected)
				if (selection.getParent() == null || !WebTreeHelper.isInSelection(selection.getParent()))
					getPortlet(selection).close();
		} else if ("add".equals(action)) {
			if (selected.size() != 1)
				throw new IllegalStateException();
			WebTreeNode node = selected.get(0);
			String portletId = (String) node.getData();
			PortletBuilderPortlet pbp = new PortletBuilderPortlet(generateConfig(), false);
			pbp.setPortletIdOfParentToAddPortletTo(portletId);
			getManager().showDialog("Add Portlet", pbp);
		} else if ("paste".equals(action)) {
			Portlet source = getManager().getPortlet(this.clipboardId);
			Map<String, Object> config = getManager().getConfiguration(source.getPortletId());
			if (selected.size() != 1)
				throw new IllegalStateException();
			WebTreeNode node = selected.get(0);
			String target = (String) node.getData();
			StringBuilder warningsSink = new StringBuilder();
			LH.info(log, "Copy / Paste config: ", RootAssister.INSTANCE.toJson(config));
			getManager().init(config, target, warningsSink);
		} else if ("save".equals(action)) {
			if (selected.size() != 1)
				throw new IllegalStateException();
			WebTreeNode node = selected.get(0);
			String portletId = (String) node.getData();
			Map<String, Object> config = getManager().getConfiguration(portletId);
			String text = RootAssister.INSTANCE.toJson(config);
			SaveLayoutPortlet saveDialog = new SaveLayoutPortlet(generateConfig());
			saveDialog.setText(text);
			getManager().showDialog("save", saveDialog);
		} else if ("remove_connection".equals(action)) {
			for (WebTreeNode node : selected) {
				Tuple2<PortletSocket, PortletSocket> key = (Tuple2<PortletSocket, PortletSocket>) node.getData();
				if (key.getA().getIsInitiator())
					key.getA().disconnectFrom(key.getB());
				else
					key.getB().disconnectFrom(key.getA());
			}
		} else
			throw new RuntimeException("unknown action: " + action);
	}
	private Portlet getPortlet(WebTreeNode selection) {
		String portletId = (String) selection.getData();
		return getManager().getPortlet(portletId);
	}

	private void setMode(int mode, String portletId) {
		if (clipboardId != null) {
			WebTreeNode node = treeNodes.get(clipboardId);
			WebTreeHelper.removeCssClass(node, "clipboarder", true);
		}
		this.mode = mode;
		this.clipboardId = portletId;
		if (clipboardId != null) {
			WebTreeNode node = treeNodes.get(clipboardId);
			WebTreeHelper.applyCssClass(node, "clipboarder", true);
			node.setSelected(false);
		}
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (node == null)
			return;
		if (node.getData() instanceof Tuple2) {
			Tuple2<PortletSocket, PortletSocket> tuple = (Tuple2<PortletSocket, PortletSocket>) node.getData();
			WebTreeNode peerNode = socketNodes.get(new Tuple2<PortletSocket, PortletSocket>(tuple.getB(), tuple.getA()));
			if (peerNode != null)
				peerNode.setSelected(true);
		} else {
			Portlet p = getManager().getPortlet((String) node.getData());
			PortletHelper.ensureVisible(p);
		}
	}

	@Override
	public void onPortletRenamed(Portlet portlet, String oldName, String newName) {
		WebTreeNode treeNode = treeNodes.get(portlet.getPortletId());
		if (treeNode != null)
			treeNode.setName(newName);
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		boolean socketSelected = false;
		boolean portletSelected = false;
		for (WebTreeNode node : selected) {
			if (node.getData() instanceof Tuple2)
				socketSelected = true;
			if (node.getData() instanceof String)
				portletSelected = true;
		}
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		if (socketSelected && portletSelected) {
		} else if (socketSelected) {
			children.add(new BasicWebMenuLink("remove connection", true, "remove_connection"));
		} else if (portletSelected) {
			if (selected.size() == 1) {
				children.add(new BasicWebMenuLink("copy layout", true, "copy"));
				children.add(new BasicWebMenuLink("cut layout", true, "cut"));
				String portletId = (String) selected.get(0).getData();
				Portlet portlet = getManager().getPortlet(portletId);
				boolean hasVacancy = portlet instanceof PortletContainer && ((PortletContainer) portlet).hasVacancy();
				if (mode != MODE_NONE) {
					if (hasVacancy || portlet instanceof BlankPortlet)
						children.add(new BasicWebMenuLink("paste layout", true, "paste"));
				}
				if (hasVacancy)
					children.add(new BasicWebMenuLink("add portlet", true, "add"));
			}
			children.add(new BasicWebMenuLink("save layout", true, "save"));
			if (mode != MODE_NONE)
				children.add(new BasicWebMenuLink("clear selection", true, "clear"));
			if (selected.size() >= 1)
				children.add(new BasicWebMenuLink("remove portlet(s)", true, "remove"));
		}
		return new BasicWebMenu("", true, children);
	}

	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}

	private int mode = MODE_NONE;
	private String clipboardId = null;

	@Override
	public void onSocketConnected(PortletSocket initiator, PortletSocket remoteSocket) {
		WebTreeNode node1 = treeNodes.get(initiator.getPortlet().getPortletId());
		WebTreeNode node2 = treeNodes.get(remoteSocket.getPortlet().getPortletId());
		if (node1 == null) {
			log.info("socket for unknown portlet: " + initiator);
			return;
		}
		if (node2 == null) {
			log.warning("socket for unknown (remote) portlet: " + remoteSocket);
			return;
		}
		addSocketNode(node1, initiator, remoteSocket, true);
		addSocketNode(node2, remoteSocket, initiator, false);
	}

	@Override
	public void onSocketDisconnected(PortletSocket initiator, PortletSocket remoteSocket) {
		final WebTreeNode node1 = socketNodes.remove(new Tuple2<PortletSocket, PortletSocket>(initiator, remoteSocket));
		if (node1 == null) {
			log.warning("remove for unknown connection: " + initiator + ", " + remoteSocket);
		} else
			getTreeManager().removeNode(node1);
		final WebTreeNode node2 = socketNodes.remove(new Tuple2<PortletSocket, PortletSocket>(remoteSocket, initiator));
		if (node2 == null) {
			log.warning("remove for unknown (remote) connection: " + initiator + ", " + remoteSocket);
		} else
			getTreeManager().removeNode(node2);
	}

	@Override
	public void onClosed() {
		getManager().removePortletlistener(this);
		super.onClosed();
	}

	@Override
	public void onLocationChanged(Portlet portlet) {
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode row, FastWebTreeColumn col) {
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

}
