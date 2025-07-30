package com.vortex.web.portlet.trees;

import java.util.ArrayList;
import java.util.List;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
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
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.vortex.client.VortexClientDbColumn;
import com.vortex.client.VortexClientDbDatabase;
import com.vortex.client.VortexClientDbObject;
import com.vortex.client.VortexClientDbPrivilege;
import com.vortex.client.VortexClientDbServer;
import com.vortex.client.VortexClientDbTable;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.client.VortexClientManager;
import com.vortex.client.VortexClientManagerListener;
import com.vortex.ssoweb.NodeSelectionInterPortletMessage;
import com.vortex.ssoweb.NodeSelectionInterPortletMessage.Mask;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.diff.DiffableDbColumn;
import com.vortex.web.diff.DiffableDbDatabase;
import com.vortex.web.diff.DiffableDbObject;
import com.vortex.web.diff.DiffableDbPriviledge;
import com.vortex.web.diff.DiffableDbServer;
import com.vortex.web.diff.DiffableDbTable;
import com.vortex.web.diff.DiffableNode;

public class VortexWebDatabasesTreePortlet extends FastTreePortlet implements VortexClientMachineListener, WebTreeFilter, WebTreeContextMenuListener, WebTreeContextMenuFactory,
		VortexClientManagerListener {

	private VortexWebEyeService service;
	private BasicPortletSocket maskSocket;
	private List<Matcher> treeMasks;
	private LongKeyMap<WebTreeNode> nodes = new LongKeyMap<WebTreeNode>();

	public VortexWebDatabasesTreePortlet(PortletConfig portletConfig) {
		super(portletConfig);
		service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		service.getAgentManager().addMachineListener(this);
		service.getAgentManager().addClientConnectedListener(this);
		getTree().addMenuContextListener(this);
		getTree().setRootLevelVisible(false);
		getTree().setAutoExpandUntilMultipleNodes(false);
		getTree().setContextMenuFactory(this);

		WebTreeNode root = getTreeManager().getRoot();
		for (VortexClientDbServer dbs : service.getAgentManager().getDbServers()) {
			onMachineEntityAdded(dbs);
			for (VortexClientDbDatabase db : dbs.getDatabases()) {
				onMachineEntityAdded(db);
				for (VortexClientDbTable table : db.getTables()) {
					onMachineEntityAdded(table);
					for (VortexClientDbColumn column : table.getColumns())
						onMachineEntityAdded(column);
				}
				for (VortexClientDbObject obj : db.getObjects()) {
					onMachineEntityAdded(obj);
				}
				for (VortexClientDbPrivilege obj : db.getPrivileges()) {
					onMachineEntityAdded(obj);
				}
			}
		}
	}

	@Override
	public void close() {
		service.getAgentManager().removeMachineListener(this);
		service.getAgentManager().removeClientConnectedListener(this);
		super.close();
	}

	private WebTreeNode createNode(String name, WebTreeNode parent, boolean expanded, VortexClientEntity<?> data) {
		WebTreeNode existing = nodes.get(data.getId());
		if (existing != null) {
			return existing;
		}

		WebTreeNode r = getTreeManager().createNode(name, parent, expanded, data);
		nodes.putOrThrow(data.getId(), r);
		return r;
	}

	@Override
	public void onMachineAdded(VortexClientMachine machine) {
		//for (WebAgentDbServer i : machine.getDbServers())
		//onMachineNodeAdded(i);
	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {

	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
		//for (WebAgentDbServer i : machine.getDbServers())
		//onMachineNodeRemoved(i);
	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_DB_SERVER: {
				VortexClientDbServer server = (VortexClientDbServer) node;
				WebTreeNode pnode = getTreeManager().getRoot();
				createNode(server.getData().getDescription(), pnode, false, node).setIcon("portlet_icon_connection").setCssClass("clickable");
				break;
			}
			case VortexAgentEntity.TYPE_DB_DATABASE: {
				VortexClientDbDatabase database = (VortexClientDbDatabase) node;
				WebTreeNode pnode = nodes.get(database.getDbServer().getId());
				WebTreeNode dbnode = createNode(database.getData().getName(), pnode, false, node).setIcon("portlet_icon_db_database").setCssClass("clickable");
				getTreeManager().createNode("Tables", dbnode, false).setIcon("portlet_icon_folder").setKey("T");
				getTreeManager().createNode("Triggers", dbnode, false).setIcon("portlet_icon_folder").setKey("R");
				getTreeManager().createNode("Constraints", dbnode, false).setIcon("portlet_icon_folder").setKey("C");
				getTreeManager().createNode("Indexes", dbnode, false).setIcon("portlet_icon_folder").setKey("I");
				getTreeManager().createNode("Stored Procedures", dbnode, false).setIcon("portlet_icon_folder").setKey("P");
				break;
			}
			case VortexAgentEntity.TYPE_DB_TABLE: {
				VortexClientDbTable table = (VortexClientDbTable) node;
				WebTreeNode pnode = nodes.get(table.getDatabase().getId());
				createNode(table.getData().getName(), pnode.getChildByKey("T"), false, node).setIcon("portlet_icon_db_table").setCssClass("clickable");
				break;
			}
			case VortexAgentEntity.TYPE_DB_COLUMN: {
				VortexClientDbColumn column = (VortexClientDbColumn) node;
				WebTreeNode pnode = nodes.get(column.getTable().getId());
				createNode(column.getData().getName(), pnode, false, node).setIcon("portlet_icon_db_column").setCssClass("clickable");
				break;
			}
			case VortexAgentEntity.TYPE_DB_OBJECT: {
				VortexClientDbObject object = (VortexClientDbObject) node;
				WebTreeNode pnode = nodes.get(object.getData().getDatabaseId());
				String key;
				switch (object.getData().getType()) {
					case VortexAgentDbObject.TRIGGER:
						key = "R";
						break;
					case VortexAgentDbObject.CONSTRAINT:
						key = "C";
						break;
					case VortexAgentDbObject.INDEX:
						key = "I";
						break;
					case VortexAgentDbObject.PROCEDURE:
						key = "P";
						break;
					default:
						throw new RuntimeException("Bad type: " + object.getData());
				}
				createNode(object.getData().getName(), pnode.getChildByKey(key), false, node).setIcon("portlet_icon_db_object").setCssClass("clickable");
				break;
			}

		}

	}
	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_DB_SERVER: {
				VortexClientDbServer server = (VortexClientDbServer) node;
				WebTreeNode treeNode = nodes.get(server.getId());
				treeNode.setName(server.getData().getDescription());
				break;
			}
		}

	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		WebTreeNode treeNode = nodes.get(node.getId());
		if (treeNode != null)
			getTreeManager().removeNode(treeNode);

	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDatabasesTreePortlet> {

		public static final String ID = "DatabasesTreePortlet";

		public Builder() {
			super(VortexWebDatabasesTreePortlet.class);
		}

		@Override
		public VortexWebDatabasesTreePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDatabasesTreePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Databases Tree";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == maskSocket) {
			List<Matcher> treeMasks = new ArrayList<Matcher>();
			NodeSelectionInterPortletMessage msg = (NodeSelectionInterPortletMessage) message;
			for (Mask mask : msg.getMasks()) {
				Matcher m = null;
				for (; mask != null; mask = mask.next) {
					byte type;
					if ("host".equals(mask.key))
						type = Matcher.TYPE_HOST;
					//else if ("process".equals(mask.key))
					//type = Matcher.TYPE_PROC;
					else if ("user".equals(mask.key))
						type = Matcher.TYPE_USER;
					else
						continue;
					m = new Matcher(type, SH.m(mask.mask.toString()), m);
				}
				if (m != null)
					treeMasks.add(m);
			}
			setHostMasks(treeMasks);
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}
	private void setHostMasks(List<Matcher> treeMasks) {
		this.treeMasks = treeMasks;
		getTreeManager().setFilter(treeMasks.size() == 0 ? null : this);
	}

	private static class Matcher {
		public static final byte TYPE_HOST = 1;
		public static final byte TYPE_USER = 2;
		//public static final byte TYPE_PROC = 3;

		final byte type;
		final TextMatcher textMatcher;
		private Matcher next;

		public Matcher(byte type, TextMatcher textMatcher, Matcher next) {
			this.type = type;
			this.textMatcher = textMatcher;
			this.next = next;
		}

		public boolean matches(VortexClientDbDatabase proc) {
			String s;
			switch (type) {
				case TYPE_HOST:
					s = proc.getHostName();
					break;
				//case TYPE_USER:
				//s = proc.getData().getUser();
				//break;
				//case TYPE_PROC:
				//s = proc.getData().getCommand();
				//break;
				default:
					throw new RuntimeException(SH.toString(type));
			}
			return (textMatcher.matches(s) && (next == null || next.matches(proc)));
		}

	}

	@Override
	public boolean shouldKeep(WebTreeNode node) {
		if (treeMasks.size() > 0) {
			Object data = node.getData();
			if (data instanceof VortexClientDbDatabase && node.getParent() == getTreeManager().getRoot()) {
				VortexClientDbDatabase db = (VortexClientDbDatabase) data;
				for (Matcher m : treeMasks) {
					if (m.matches(db))
						return true;
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		List<WebTreeNode> selected = tree.getSelected();
		if ("diff".equals(action)) {
			if (selected.size() == 2 && selected.get(0).getData().getClass() == selected.get(1).getData().getClass()) {
				DiffableNode d1 = toDiffable((VortexClientEntity<?>) selected.get(0).getData());
				DiffableNode d2 = toDiffable((VortexClientEntity<?>) selected.get(1).getData());
				VortexWebDiffTreePortlet diffTree = new VortexWebDiffTreePortlet(generateConfig(), describe(selected.get(0).getData()), d1, describe(selected.get(1).getData()), d2);
				getManager().showDialog("Diff", diffTree);
			} else {
				getManager().showAlert("Must select 2 elements of the same type");
			}
		}
		// TODO Auto-generated method stub

	}

	private String describe(Object data) {
		if (data instanceof VortexClientDbColumn)
			return ((VortexClientDbColumn) data).getDbName();
		else if (data instanceof VortexClientDbTable)
			return ((VortexClientDbTable) data).getDbName();
		else if (data instanceof VortexClientDbDatabase)
			return ((VortexClientDbDatabase) data).getDbName();
		else if (data instanceof VortexClientDbObject)
			return ((VortexClientDbObject) data).getDbName();
		else if (data instanceof VortexClientDbPrivilege)
			return ((VortexClientDbPrivilege) data).getDbName();
		else if (data instanceof VortexClientDbServer)
			return ((VortexClientDbServer) data).getDescription();
		else
			throw new RuntimeException("can not diff: " + data.getClass().getName());
	}

	private DiffableNode toDiffable(VortexClientEntity<?> data) {
		if (data instanceof VortexClientDbColumn)
			return new DiffableDbColumn((VortexClientDbColumn) data);
		else if (data instanceof VortexClientDbTable)
			return new DiffableDbTable((VortexClientDbTable) data);
		else if (data instanceof VortexClientDbDatabase)
			return new DiffableDbDatabase((VortexClientDbDatabase) data);
		else if (data instanceof VortexClientDbObject)
			return new DiffableDbObject((VortexClientDbObject) data);
		else if (data instanceof VortexClientDbPrivilege)
			return new DiffableDbPriviledge((VortexClientDbPrivilege) data);
		else if (data instanceof VortexClientDbServer)
			return new DiffableDbServer((VortexClientDbServer) data);
		else
			throw new RuntimeException("can not diff: " + data.getClass().getName());
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		NodeSelectionInterPortletMessage msg = new NodeSelectionInterPortletMessage();
		List<Mask> masks = new ArrayList<NodeSelectionInterPortletMessage.Mask>();
		for (WebTreeNode node2 : fastWebTree.getSelected()) {
			VortexClientEntity<?> machineNode = (VortexClientEntity<?>) node2.getData();
			if (machineNode == null)
				continue;
			switch (machineNode.getType()) {
				case VortexAgentEntity.TYPE_DB_SERVER:
					masks.add(new Mask("dsid", machineNode.getId()));
					break;
				case VortexAgentEntity.TYPE_DB_DATABASE:
					masks.add(new Mask("dbid", machineNode.getId()));
					break;
				case VortexAgentEntity.TYPE_DB_TABLE:
					masks.add(new Mask("tbid", machineNode.getId()));
					break;
				case VortexAgentEntity.TYPE_DB_COLUMN:
					masks.add(new Mask("clid", machineNode.getId()));
					break;
				case VortexAgentEntity.TYPE_DB_OBJECT:
					masks.add(new Mask("obid", machineNode.getId()));
					break;
				case VortexAgentEntity.TYPE_DB_PRIVILEDGE:
					masks.add(new Mask("pvid", machineNode.getId()));
					break;
			}
		}
		msg.setMasks(masks);
	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		if (selected.size() == 2) {
			if (selected.get(0).getData().getClass() == selected.get(1).getData().getClass()) {
				children.add(new BasicWebMenuLink("Compare", true, "diff"));
			}
		}
		BasicWebMenu r = new BasicWebMenu("", true, children);
		return r;
	}

	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVortexEyeDisconnected() {
		getTreeManager().clear();
		nodes.clear();
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
