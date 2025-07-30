package com.f1.ami.web.centermanager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.AmiWebAmiDbShellPortlet;
import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebCenterGraphListener;
import com.f1.ami.web.AmiWebCompilerListener;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebDomObjectDependency;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSpecialPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.centermanager.AmiWebCenterGraphManager;
import com.f1.ami.web.centermanager.editor.AmiCenterManagerAddMethodPortlet;
import com.f1.ami.web.centermanager.graph.AmiCenterManagerEntityRelationGraph;
import com.f1.ami.web.centermanager.graph.AmiCenterManagerSmartGraph;
import com.f1.ami.web.centermanager.graph.AmiCenterManagerSmartGraphMenu;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode_Center;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode_Dbo;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode_Index;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode_Method;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode_Procedure;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode_Table;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode_Timer;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode_Trigger;
import com.f1.ami.web.centermanager.utils.AmiCenterEntityConsts;
import com.f1.ami.web.centermanager.utils.AmiCenterManagerUtils;
import com.f1.ami.web.dm.portlets.AmiWebDmScriptTreePortlet;
import com.f1.ami.web.graph.AmiWebGraphListener;
import com.f1.ami.web.graph.AmiWebGraphManager;
import com.f1.ami.web.graph.AmiWebGraphNode;
import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ResultMessage;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TreeStateCopier;
import com.f1.suite.web.portal.impl.TreeStateCopierIdGetter;
import com.f1.suite.web.portal.impl.visual.GraphListener;
import com.f1.suite.web.portal.impl.visual.GraphPortlet;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Node;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.string.sqlnode.AdminNode;
import com.f1.utils.string.sqlnode.CreateTableNode;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiWebCenterManagerPortlet extends GridPortlet implements AmiWebGraphListener, WebTreeContextMenuListener, AmiWebSpecialPortlet, WebTreeContextMenuFactory,
		GraphListener, TreeStateCopierIdGetter, ConfirmDialogListener, Comparator<WebTreeNode>, AmiWebCompilerListener, AmiWebDomObjectDependency, AmiWebCenterGraphListener {
	private FastTreePortlet tree;
	protected AmiWebService service;
	private String baseAlias;
	final private boolean allowModification;

	//consts
	public static final String TRIGGER_CATEGORY_NAME_UNDER_TABLE = "Triggers";
	public static final String INDEX_CATEGORY_NAME_UNDER_TABLE = "Indexes";

	//graph
	private GraphPortlet graph;
	private AmiCenterManagerSmartGraph smartGraph;
	//add
	private GraphPortlet erGraph;
	private AmiCenterManagerEntityRelationGraph smartErGraph;

	private AmiWebHeaderPortlet header;
	private AmiWebCenterManagerHeaderPortlet amiHeader;
	private AmiWebDmScriptTreePortlet scriptTree;
	private TabPortlet tabPortlet;
	private LongKeyMap<List<WebTreeNode>> nodesByGraphId = new LongKeyMap<List<WebTreeNode>>();
	private WebTreeNode treeNodeCurCenter;
	private WebTreeNode treeNodeTables;
	private WebTreeNode treeNodeTriggers;
	private WebTreeNode treeNodeTimers;
	private WebTreeNode treeNodeProcedures;
	private WebTreeNode treeNodeMethods;
	private WebTreeNode treeNodeIndexes;
	private WebTreeNode treeNodeDBOs;
	private byte permissions = AmiCenterQueryDsRequest.PERMISSIONS_FULL;
	HashMap<String, WebTreeNode> externalCenterTreeNodesByName = new HashMap<String, WebTreeNode>();
	public HashMap<String, AmiCenterGraphNode_Center> centerNodeByNames = new HashMap<String, AmiCenterGraphNode_Center>();
	public HashMap<String, AmiCenterGraphNode_Trigger> triggerNodeByNames = new HashMap<String, AmiCenterGraphNode_Trigger>();
	public HashMap<String, AmiCenterGraphNode_Table> tableNodeByNames = new HashMap<String, AmiCenterGraphNode_Table>();
	public HashMap<String, AmiCenterGraphNode_Timer> timerNodeByNames = new HashMap<String, AmiCenterGraphNode_Timer>();
	public HashMap<String, AmiCenterGraphNode_Procedure> procedureNodeByNames = new HashMap<String, AmiCenterGraphNode_Procedure>();
	public HashMap<String, AmiCenterGraphNode_Dbo> dboNodeByNames = new HashMap<String, AmiCenterGraphNode_Dbo>();
	public HashMap<String, AmiCenterGraphNode_Index> indexNodeByNames = new HashMap<String, AmiCenterGraphNode_Index>();
	public HashMap<String, AmiCenterGraphNode_Method> methodNodeByNames = new HashMap<String, AmiCenterGraphNode_Method>();

	//check if need to rebuild js
	private boolean changed;
	private boolean graphNeedsRebuild;

	//should show user defined only objects
	private boolean showUserDefinedOnlyObjects = false;

	//DB obeject consts
	public static final byte DB_CENTER = 0;
	public static final byte DB_TABLE = 1;
	public static final byte DB_TIMER = 2;
	public static final byte DB_TRIGGER = 4;
	public static final byte DB_PROCEDURE = 8;
	public static final byte DB_DBO = 16;
	public static final byte DB_METHOD = 32;
	public static final byte DB_INDEX = 64;
	public byte SHOW_ALL_DB_TYPES = DB_CENTER | DB_TABLE | DB_TIMER | DB_TRIGGER | DB_PROCEDURE | DB_DBO | DB_METHOD | DB_INDEX;
	public static LinkedHashSet<Byte> ALL_TYPES = new LinkedHashSet<Byte>();
	public static final String AMI_DS_NAME = "AMI";

	//DB String
	public static final String ID_CENTER = "CENTERS";
	public static final String ID_TABLE = "TABLES";
	public static final String ID_TRIGGER = "TRIGGERS";
	public static final String ID_TIMER = "TIMERS";
	public static final String ID_PROCEDURE = "PROCEDURES";
	public static final String ID_METHOD = "METHODS";
	public static final String ID_INDEX = "INDEXES";
	public static final String ID_DBO = "DBOS";

	private final Object dbsemephore = new Object();
	//tree state copier,
	TreeStateCopier tsc = null;

	static {
		ALL_TYPES.add(DB_CENTER);
		ALL_TYPES.add(DB_TABLE);
		ALL_TYPES.add(DB_TRIGGER);
		ALL_TYPES.add(DB_INDEX);
		ALL_TYPES.add(DB_TIMER);
		ALL_TYPES.add(DB_PROCEDURE);
		ALL_TYPES.add(DB_DBO);
		ALL_TYPES.add(DB_METHOD);
	}

	public AmiWebCenterManagerPortlet(PortletConfig config, AmiWebService service, String baseAlias, boolean allowModification, String dmToFocus) {
		super(config);
		this.allowModification = allowModification;
		this.baseAlias = baseAlias;
		this.service = service;

		this.tree = new FastTreePortlet(generateConfig());
		this.tree.getTree().setComparator(this);
		this.tree.getTree().addMenuContextListener(this);
		//add default form and dialog style for this.tree
		this.tree.setFormStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());
		this.tree.setDialogStyle(AmiWebUtils.getService(getManager()).getUserDialogStyleManager());

		//add
		this.erGraph = new GraphPortlet(generateConfig());
		this.erGraph.addGraphListener(this);
		this.smartErGraph = new AmiCenterManagerEntityRelationGraph(this, service, this.erGraph, allowModification);

		this.graph = new GraphPortlet(generateConfig());
		this.graph.addGraphListener(this);
		this.smartGraph = new AmiCenterManagerSmartGraph(this, service, this.graph, allowModification);

		DividerPortlet div = new DividerPortlet(generateConfig(), true);
		div.setOffsetFromTopPx(300);

		//the header 
		this.header = new AmiWebHeaderPortlet(generateConfig());
		this.amiHeader = new AmiWebCenterManagerHeaderPortlet(this, header, allowModification);

		addChild(header, 0, 0, 1, 1);
		addChild(div, 0, 1, 1, 1);
		div.addChild(this.tree);

		this.scriptTree = new AmiWebDmScriptTreePortlet(generateConfig(), service);
		this.service.getDomObjectsManager().addGlobalListener(this);

		//add tab
		this.tabPortlet = new TabPortlet(generateConfig());
		this.tabPortlet.addChild("Entity Relation Graph", this.erGraph);
		this.tabPortlet.addChild("Graph", this.graph);
		this.tabPortlet.setIsCustomizable(false);
		div.addChild(this.tabPortlet);

		AmiWebCenterGraphManager gm = this.service.getCenterGraphManager();
		gm.addListener(this);
		gm.init();
		this.tree.getTree().setRootLevelVisible(false);
		this.tree.getTree().setContextMenuFactory(this);

		this.buildNodes();
		this.showTree();
		setSuggestedSize(1000, 800);
		this.service.addCompilerListener(this);

		//TODO:send authentication to the backend
		sendAuth();

	}

	public boolean getShowUserDefinedOnlyObjects() {
		return this.showUserDefinedOnlyObjects;
	}

	public void setShowUserDefinedOnlyObjects(boolean showUserDefinedOnly) {
		if (this.showUserDefinedOnlyObjects == showUserDefinedOnly)
			return;
		this.showUserDefinedOnlyObjects = showUserDefinedOnly;
		this.amiHeader.updateShowUsrDefinedBtn();
		onChanged();
	}

	private void showTree() {
		IdentityHashSet<AmiWebGraphNode<?>> allSelected = new IdentityHashSet<AmiWebGraphNode<?>>();
		IdentityHashSet<AmiWebGraphNode<?>> origNodes = new IdentityHashSet<AmiWebGraphNode<?>>();

		this.scriptTree.build(new ArrayList<AmiWebDomObject>());
	}

	private void sendAuth() {
		AmiCenterQueryDsRequest request = prepareRequest();
		if (request == null)
			return;
		service.sendRequestToBackend(this, request);
	}

	/**
	 * This is for building tree nodes for triggers,timers,tables..etc which reside in AMICenterDB Step1: authenticate the user, this is similar to @link
	 * {@link AmiWebAmiDbShellPortlet#sendAuth} Only the user with dev and admin permission is allowed to authenticate and connect to the backend Step2: build the tree nodes for
	 * tables, triggers, timers, etc...., this is equivalent to run "SHOW <OBJECT TYPE>" on the backend, and we process the query result into the tree node.
	 */
	private void prepareDbObjectNode() {
		//sendAuth();
		StringBuilder sb = new StringBuilder();
		for (byte b : ALL_TYPES)
			initDbObjectNode(sb, b);
		System.out.println(sb.toString());
		prepareRequestToBackend(sb.toString());
	}

	private void prepareRequestToBackend(String query) {
		AmiCenterQueryDsRequest request = prepareRequest();
		request.setQuery(query);
		//request.setQuerySessionId(this.sessionId);
		service.sendRequestToBackend(this, request);
	}

	private StringBuilder initDbObjectNode(StringBuilder sb, byte type) {
		switch (type) {
			case DB_CENTER:
				sb.append("SHOW ").append(ID_CENTER).append(';');
				break;
			case DB_TABLE:
				sb.append("SHOW ").append(ID_TABLE).append(';');
				break;
			case DB_TIMER:
				sb.append("SHOW ").append(ID_TIMER).append(';');
				break;
			case DB_TRIGGER:
				sb.append("SHOW ").append(ID_TRIGGER).append(';');
				break;
			case DB_PROCEDURE:
				sb.append("SHOW ").append(ID_PROCEDURE).append(';');
				break;
			case DB_METHOD:
				sb.append("SHOW ").append(ID_METHOD).append(';');
				break;
			case DB_INDEX://making sure there is only one indexNode created for composite indexes
				sb.append("select * from (show indexes) group by IndexName, TableName;");
			case DB_DBO:
				sb.append("SHOW ").append(ID_DBO).append(';');
				break;
		}
		return sb;
	}

	private void buildNodes() {
		final TreeStateCopier tsc = new TreeStateCopier(this.tree, this);
		this.tsc = tsc;
		AmiWebGraphManager gm = this.service.getGraphManager();
		// reset state, will clear all selected
		this.tree.clear();
		this.nodesByGraphId.clear();
		// initialize node collections
		this.treeNodeCurCenter = createNode(this.tree.getRoot(), "Current Center", AmiWebConsts.CENTER_GRAPH_NODE_CENTER, null);
		this.treeNodeTables = createNode(treeNodeCurCenter, "Tables", AmiWebConsts.CENTER_GRAPH_NODE_TABLE, null);
		this.treeNodeTriggers = createNode(treeNodeCurCenter, "Triggers", AmiWebConsts.CENTER_GRAPH_NODE_TRIGGER, null);
		this.treeNodeTimers = createNode(treeNodeCurCenter, "Timers", AmiWebConsts.CENTER_GRAPH_NODE_TIMER, null);
		this.treeNodeProcedures = createNode(treeNodeCurCenter, "Procedures", AmiWebConsts.CENTER_GRAPH_NODE_PROCEDURE, null);
		this.treeNodeMethods = createNode(treeNodeCurCenter, "Methods", AmiWebConsts.CENTER_GRAPH_NODE_METHOD, null);
		this.treeNodeIndexes = createNode(treeNodeCurCenter, "Indexes", AmiWebConsts.CENTER_GRAPH_NODE_INDEX, null);
		this.treeNodeDBOs = createNode(treeNodeCurCenter, "DBOs", AmiWebConsts.CENTER_GRAPH_NODE_DBO, null);

		prepareDbObjectNode();

	}

	private WebTreeNode createNode(WebTreeNode parent, String title, String icon, Object data) {
		WebTreeNode r = this.tree.createNode(title, parent, false, data);
		r.setIconCssStyle(icon == null ? null : "_bgi=url('" + icon + "')");
		return r;
	}

	private WebTreeNode createNode(WebTreeNode parent, AmiCenterGraphNode node) {
		String icon = getIcon(node);
		String label = node.getLabel();
		//		String desc = node.getDescription();
		//		if (SH.is(desc))
		//			label += " (" + desc + ")";
		WebTreeNode r = parent.getTreeManager().createNode(label, parent, false, node);
		r.setIconCssStyle(icon == null ? null : "_bgi=url('" + icon + "')");
		LongKeyMap.Node<List<WebTreeNode>> entry = this.nodesByGraphId.getNodeOrCreate(node.getUid());
		if (entry.getValue() == null)
			entry.setValue(new ArrayList<WebTreeNode>());
		entry.getValue().add(r);

		return r;
	}

	public static String getIcon(AmiCenterGraphNode node) {
		switch (node.getType()) {
			case AmiCenterGraphNode.TYPE_CENTER:
				return AmiWebConsts.CENTER_GRAPH_NODE_CENTER;
			case AmiCenterGraphNode.TYPE_TABLE:
				return AmiWebConsts.CENTER_GRAPH_NODE_TABLE;
			case AmiCenterGraphNode.TYPE_TRIGGER:
				return AmiWebConsts.CENTER_GRAPH_NODE_TRIGGER;
			case AmiCenterGraphNode.TYPE_TIMER:
				return AmiWebConsts.CENTER_GRAPH_NODE_TIMER;
			case AmiCenterGraphNode.TYPE_PROCEDURE:
				return AmiWebConsts.CENTER_GRAPH_NODE_PROCEDURE;
			case AmiCenterGraphNode.TYPE_INDEX:
				return AmiWebConsts.CENTER_GRAPH_NODE_INDEX;
			case AmiCenterGraphNode.TYPE_DBO:
				return AmiWebConsts.CENTER_GRAPH_NODE_DBO;
			case AmiCenterGraphNode.TYPE_METHOD:
				return AmiWebConsts.CENTER_GRAPH_NODE_METHOD;
		}
		return null;
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initLinkedVariables() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDomObjectAriChanged(AmiWebDomObject target, String oldAri) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDomObjectEvent(AmiWebDomObject object, byte eventType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDomObjectRemoved(AmiWebDomObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDomObjectAdded(AmiWebDomObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCallbackChanged(AmiWebAmiScriptCallback callback, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRecompiled() {
		// TODO Auto-generated method stub

	}

	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getId(WebTreeNode node) {
		// TODO Auto-generated method stub
		return null;
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
	public static AmiCenterGraphNode getData(WebTreeNode data) {
		Object r = data.getData();
		return r instanceof AmiCenterGraphNode ? (AmiCenterGraphNode) r : null;
	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		if (selected.size() == 1) {
			WebTreeNode t = selected.get(0);
			if (t == this.treeNodeCurCenter || this.externalCenterTreeNodesByName.containsValue(t)) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Center", true, "add_center"));
				if (this.externalCenterTreeNodesByName.containsValue(t))
					menu.addChild(new BasicWebMenuLink("Remove Center", true, "delete_center"));
				return menu;
			} else if (t == this.treeNodeTables && allowModification) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Table", true, "add_table"));
				return menu;
			} else if (t == this.treeNodeTriggers && allowModification) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Trigger", true, "add_trigger"));
				return menu;
			} else if (t == this.treeNodeTimers && allowModification) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Timer", true, "add_timer"));
				return menu;
			} else if (t == this.treeNodeProcedures && allowModification) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Procedure", true, "add_procedure"));
				return menu;
			} else if (t == this.treeNodeIndexes && allowModification) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Index", true, "add_index"));
				return menu;
			} else if (t == this.treeNodeMethods && allowModification) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Method", true, "add_method"));
				return menu;
			} else if (t == this.treeNodeDBOs && allowModification) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Dbo", true, "add_dbo"));
				return menu;
			}

		}
		List<AmiCenterGraphNode> nodes2 = new ArrayList<AmiCenterGraphNode>(selected.size());
		for (WebTreeNode data : selected) {
			AmiCenterGraphNode n = getData(data);
			if (n == null)
				return null;
			nodes2.add(n);
		}
		BasicWebMenu menu = AmiCenterManagerSmartGraphMenu.createContextMenu(service, nodes2, this.allowModification);
		return menu;
	}

	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onContextMenuOnNodes(String action, List<AmiCenterGraphNode> nodes) {
		if ("edit_trigger".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			String query = "DESCRIBE TRIGGER " + n.getLabel();
			//String query = "SHOW FULL TRIGGERS";// WHERE TriggerName==\"" + n.getLabel() + "\"";
			prepareRequestToBackend(query);
		} else if ("delete_trigger".equals(action)) {
			String query = "DROP TRIGGER ";
			if (nodes.size() == 1)
				query += nodes.get(0).getLabel();
			else {
				List<String> triggerNames = new ArrayList<String>();
				for (AmiCenterGraphNode tn : nodes)
					triggerNames.add(tn.getLabel());
				query += SH.join(",", triggerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_timer".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			String query = "DESCRIBE TIMER " + n.getLabel();
			prepareRequestToBackend(query);
		} else if ("delete_timer".equals(action)) {
			String query = "DROP TIMER ";
			if (nodes.size() == 1)
				query += nodes.get(0).getLabel();
			else {
				List<String> timerNames = new ArrayList<String>();
				for (AmiCenterGraphNode tn : nodes)
					timerNames.add(tn.getLabel());
				query += SH.join(",", timerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_procedure".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			String query = "DESCRIBE PROCEDURE " + n.getLabel();
			prepareRequestToBackend(query);
		} else if ("delete_procedure".equals(action)) {
			String query = "DROP PROCEDURE ";
			if (nodes.size() == 1)
				query += nodes.get(0).getLabel();
			else {
				List<String> timerNames = new ArrayList<String>();
				for (AmiCenterGraphNode tn : nodes)
					timerNames.add(tn.getLabel());
				query += SH.join(",", timerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_method".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			if (n.isReadonly()) {
				AmiCenterManagerUtils.popDialog(service, "CANNOT edit a read-only object", "EDIT FAIL");
				return;
			}
			String query = "DESCRIBE METHOD " + n.getLabel();
			prepareRequestToBackend(query);
		} else if ("delete_method".equals(action)) {
			String query = "DROP METHOD ";
			if (nodes.size() == 1)
				query += nodes.get(0).getLabel();
			else {
				List<String> timerNames = new ArrayList<String>();
				for (AmiCenterGraphNode tn : nodes)
					timerNames.add(tn.getLabel());
				query += SH.join(",", timerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_table".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			if (n.isReadonly()) {
				AmiCenterManagerUtils.popDialog(service, "CANNOT edit a read-only object", "EDIT FAIL");
				return;
			}
			String query = "DESCRIBE TABLE " + n.getLabel();
			//String query = "SHOW FULL TABLES WHERE TableName==\"" + n.getLabel() + "\"";
			prepareRequestToBackend(query);
		} else if ("delete_table".equals(action)) {
			String query = "DROP TABLE ";
			if (nodes.size() == 1)
				query += nodes.get(0).getLabel();
			else {
				List<String> timerNames = new ArrayList<String>();
				for (AmiCenterGraphNode tn : nodes)
					timerNames.add(tn.getLabel());
				query += SH.join(",", timerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_index".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			String[] tableNamePlusIndexName = n.getLabel().split("::");
			if (n.isReadonly()) {
				AmiCenterManagerUtils.popDialog(service, "CANNOT edit a read-only object", "EDIT FAIL");
				return;
			}
			String query = "DESCRIBE INDEX " + tableNamePlusIndexName[1] + " ON " + tableNamePlusIndexName[0];
			prepareRequestToBackend(query);
		} else if ("delete_index".equals(action)) {
			StringBuilder query = new StringBuilder();
			query.append("DROP INDEX ");
			if (nodes.size() == 1) {
				String label = nodes.get(0).getLabel();
				String[] tableNamePlusIndexName = label.split("::");
				query.append(tableNamePlusIndexName[1]).append(" ");
				query.append("ON").append(" ").append(tableNamePlusIndexName[0]);
				prepareRequestToBackend(query.toString());
			} else {
				throw new RuntimeException("cannot drop multiple indexes ");
			}

		} else if ("add_center".equals(action)) {

		}
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		List<WebTreeNode> selected = this.tree.getTree().getSelected();
		List<AmiCenterGraphNode> nodes = new ArrayList<AmiCenterGraphNode>(selected.size());
		for (WebTreeNode data : selected) {
			AmiCenterGraphNode data2 = getData(data);
			if (data2 != null)
				nodes.add(data2);
		}
		if (SH.startsWith(action, "add")) {
			AmiCenterManagerSmartGraphMenu.onMenuItemAddAction(service, action);
			return;
		} else if ("edit_trigger".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			String query = "DESCRIBE TRIGGER " + n.getLabel();
			//String query = "SHOW FULL TRIGGERS";// WHERE TriggerName==\"" + n.getLabel() + "\"";
			prepareRequestToBackend(query);
		} else if ("delete_trigger".equals(action)) {
			String query = "DROP TRIGGER ";
			if (selected.size() == 1)
				query += selected.get(0).getName();
			else {
				List<String> triggerNames = new ArrayList<String>();
				for (WebTreeNode tn : selected)
					triggerNames.add(tn.getName());
				query += SH.join(",", triggerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_timer".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			String query = "DESCRIBE TIMER " + n.getLabel();
			prepareRequestToBackend(query);
		} else if ("delete_timer".equals(action)) {
			String query = "DROP TIMER ";
			if (selected.size() == 1)
				query += selected.get(0).getName();
			else {
				List<String> timerNames = new ArrayList<String>();
				for (WebTreeNode tn : selected)
					timerNames.add(tn.getName());
				query += SH.join(",", timerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_procedure".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			String query = "DESCRIBE PROCEDURE " + n.getLabel();
			prepareRequestToBackend(query);
		} else if ("delete_procedure".equals(action)) {
			String query = "DROP PROCEDURE ";
			if (selected.size() == 1)
				query += selected.get(0).getName();
			else {
				List<String> timerNames = new ArrayList<String>();
				for (WebTreeNode tn : selected)
					timerNames.add(tn.getName());
				query += SH.join(",", timerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_method".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			if (n.isReadonly()) {
				AmiCenterManagerUtils.popDialog(service, "CANNOT edit a read-only object", "EDIT FAIL");
				return;
			}
			String query = "DESCRIBE METHOD " + n.getLabel();
			prepareRequestToBackend(query);
		} else if ("delete_method".equals(action)) {
			String query = "DROP METHOD ";
			if (selected.size() == 1)
				query += selected.get(0).getName();
			else {
				List<String> timerNames = new ArrayList<String>();
				for (WebTreeNode tn : selected)
					timerNames.add(tn.getName());
				query += SH.join(",", timerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_table".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			if (n.isReadonly()) {
				AmiCenterManagerUtils.popDialog(service, "CANNOT edit a read-only object", "EDIT FAIL");
				return;
			}
			String query = "DESCRIBE TABLE " + n.getLabel();
			//String query = "SHOW FULL TABLES WHERE TableName==\"" + n.getLabel() + "\"";
			prepareRequestToBackend(query);
		} else if ("delete_table".equals(action)) {
			String query = "DROP TABLE ";
			if (selected.size() == 1)
				query += selected.get(0).getName();
			else {
				List<String> timerNames = new ArrayList<String>();
				for (WebTreeNode tn : selected)
					timerNames.add(tn.getName());
				query += SH.join(",", timerNames);
			}
			prepareRequestToBackend(query);
		} else if ("edit_index".equals(action)) {
			AmiCenterGraphNode n = nodes.get(0);
			String[] tableNamePlusIndexName = n.getLabel().split("::");
			if (n.isReadonly()) {
				AmiCenterManagerUtils.popDialog(service, "CANNOT edit a read-only object", "EDIT FAIL");
				return;
			}
			String query = "DESCRIBE INDEX " + tableNamePlusIndexName[1] + " ON " + tableNamePlusIndexName[0];
			prepareRequestToBackend(query);
		} else if ("delete_index".equals(action)) {
			StringBuilder query = new StringBuilder();
			query.append("DROP INDEX ");
			if (selected.size() == 1) {
				String label = selected.get(0).getName();
				String[] tableNamePlusIndexName = label.split("::");
				query.append(tableNamePlusIndexName[1]).append(" ");
				query.append("ON").append(" ").append(tableNamePlusIndexName[0]);
				prepareRequestToBackend(query.toString());
			} else {
				throw new RuntimeException("cannot drop multiple indexes ");
			}

		}

	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		List<AmiWebDomObject> selected = new ArrayList<AmiWebDomObject>();
		IdentityHashSet<AmiCenterGraphNode> allSelected = new IdentityHashSet<AmiCenterGraphNode>();
		IdentityHashSet<AmiCenterGraphNode> origNodes = new IdentityHashSet<AmiCenterGraphNode>();
		for (WebTreeNode i : tree.getSelected()) {
			Object t = i.getData();
			if (t instanceof AmiCenterGraphNode) {
				AmiCenterGraphNode gn = (AmiCenterGraphNode) t;
				origNodes.add(gn);
				allSelected.add(gn);
			} else if (i == this.treeNodeTables || i == this.treeNodeTimers || i == this.treeNodeIndexes || i == this.treeNodeMethods || i == this.treeNodeTriggers
					|| i == this.treeNodeDBOs || i == this.treeNodeProcedures) {
				for (WebTreeNode j : i.getChildren()) {
					Object t2 = j.getData();
					if (t2 != null)
						origNodes.add((AmiCenterGraphNode) t2);
				}
			} else if (t == null && (TRIGGER_CATEGORY_NAME_UNDER_TABLE.equals(i.getName()) || INDEX_CATEGORY_NAME_UNDER_TABLE.equals(i.getName()))) {
				for (WebTreeNode j : i.getChildren()) {
					Object t2 = j.getData();
					if (t2 != null)
						origNodes.add((AmiCenterGraphNode) t2);
				}
			}
		}
		this.smartGraph.buildGraph(origNodes, allSelected);
		this.smartErGraph.buildGraph(origNodes, allSelected);
		this.scriptTree.build(selected);
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdded(AmiWebGraphNode<?> node) {
		onChanged();
	}

	@Override
	public void onRemoved(AmiWebGraphNode<?> removed) {
		onChanged();
	}

	@Override
	public void onIdChanged(AmiWebGraphNode<?> node, String oldId, String newId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInnerChanged(AmiWebGraphNode<?> node, Object old, Object nuw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEdgeAdded(byte type, AmiWebGraphNode<?> src, AmiWebGraphNode<?> tgt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEdgeRemoved(byte type, AmiWebGraphNode<?> src, AmiWebGraphNode<?> tgt) {
		// TODO Auto-generated method stub

	}

	public AmiWebService getService() {
		return this.service;
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		if (result.getError() != null) {
			getManager().showAlert("Internal Error:" + result.getError().getMessage(), result.getError());
			return;
		}
		AmiCenterQueryDsResponse response = (AmiCenterQueryDsResponse) result.getAction();

		//******************TODO: Better way to get the original query?
		Action a = result.getRequestMessage().getAction();
		String query = null;
		if (a instanceof AmiCenterQueryDsRequest) {
			AmiCenterQueryDsRequest request = (AmiCenterQueryDsRequest) a;
			query = request.getQuery();
		}
		//*******************
		if (response.getOk() && SH.is(query)) {
			List<Table> tables = response.getTables();
			if (tables != null) {
				if (query.contains("SHOW")) {
					//Depending on what the inbound query is, generate different nodes
					for (Table t : tables) {
						String title = t.getTitle();
						AmiWebCenterGraphManager gm = this.service.getCenterGraphManager();
						if (title.startsWith("TABLES FROM")) {
							String centerName = SH.afterFirst(title, "TABLES FROM ");
							AmiCenterGraphNode_Center parent = gm.getCenter(centerName);
							WebTreeNode parentNode = externalCenterTreeNodesByName.get(centerName);
							WebTreeNode tableParentNode = createNode(parentNode, "Tables", AmiWebConsts.CENTER_GRAPH_NODE_TABLE, null);
							for (Row r : t.getRows()) {
								String tableName = (String) r.get("TableName");
								boolean readonly = !((String) r.get("DefinedBy")).equals("USER");
								AmiCenterGraphNode_Table tableNode = gm.getOrCreateTableFromExternalCenter(parent, tableName, readonly);
								createNode(tableParentNode, tableNode);
							}
						}
						switch (title) {
							case ID_CENTER:
								for (Row r : t.getRows()) {
									String centerName = (String) r.get("CenterName");
									String url = (String) r.get("Url");
									String nodeName = centerName + "_" + url;
									String status = (String) r.get("Status");
									AmiCenterGraphNode_Center target = gm.getCenter(centerName);

									this.centerNodeByNames.put(nodeName, target);
									WebTreeNode node = createNode(this.tree.getRoot(), target);
									externalCenterTreeNodesByName.put(centerName, node);
									if ("NOT_CONNECTED".equals(status)) {
										node.setName(nodeName + " (DISCONNECTED)");
									} else {
										node.setName(nodeName);
										prepareRequestToBackend("SHOW TABLES FROM " + centerName);
									}
								}
								break;
							case ID_TABLE:
								for (Row r : t.getRows()) {
									String tableName = (String) r.get("TableName");
									boolean isUserDefined = "USER".equals((String) r.get("DefinedBy"));
									if (!this.showUserDefinedOnlyObjects || isUserDefined) {
										AmiCenterGraphNode_Table target = gm.getTable(tableName);
										WebTreeNode node = createNode(this.treeNodeTables, target);
										WebTreeNode triggerCategoryNode, indexCategoryNode = null;
										if (target.hasIndex()) {
											indexCategoryNode = createIndexCategory(node);
											for (AmiCenterGraphNode_Index j : target.getTargetIndexes().values())
												visitIndex(indexCategoryNode, j);
										}
										if (target.hasTrigger()) {
											triggerCategoryNode = createTriggerCategory(node);
											for (AmiCenterGraphNode_Trigger j : target.getTargetTriggers().values())
												visitTrigger(triggerCategoryNode, j);
										}

										this.tableNodeByNames.put(tableName, target);
									}
								}
								break;
							case ID_TRIGGER:
								for (Row r : t.getRows()) {
									boolean isUserDefined = "USER".equals((String) r.get("DefinedBy"));
									if (!this.showUserDefinedOnlyObjects || isUserDefined) {
										String[] tableNames = null;// (String) r.get("TableName");
										String triggerName = (String) r.get("TriggerName");
										String triggerType = (String) r.get("TriggerType");
										AmiCenterGraphNode_Trigger target = gm.getTrigger(triggerName);
										createNode(this.treeNodeTriggers, target);
										//AGGREGATION(2), PROJECTION(2), JOIN(3), DECORATE(2)
										if (AmiCenterEntityConsts.TRIGGER_TYPE_RELAY.equals(triggerType) || AmiCenterEntityConsts.TRIGGER_TYPE_AMISCRIPT.equals(triggerType)) {
											tableNames = new String[] { (String) r.get("TableName") };
											AmiCenterGraphNode_Table owner = this.tableNodeByNames.get(tableNames[0]);
											//TODO:I believe this is no longer needed
											target.setBindingTable(owner);
											owner.bindTargetTrigger(target.getLabel(), target);

										} else {
											//tableNames will be more than 2
											tableNames = SH.split(',', (String) r.get("TableName"));
											for (String name : tableNames) {
												AmiCenterGraphNode_Table owner = this.tableNodeByNames.get(name);
												target.setBindingTable(owner);
												owner.bindTargetTrigger(target.getLabel(), target);
											}
										}
										this.triggerNodeByNames.put(triggerName, target);
									}
								}
								break;
							case ID_TIMER:
								for (Row r : t.getRows()) {
									boolean isUserDefined = "USER".equals((String) r.get("DefinedBy"));
									if (!this.showUserDefinedOnlyObjects || isUserDefined) {
										String timerName = (String) r.get("TimerName");
										AmiCenterGraphNode_Timer target = gm.getTimer(timerName);
										createNode(this.treeNodeTimers, target);
										this.timerNodeByNames.put(timerName, target);
									}
								}
								break;
							case ID_PROCEDURE:
								for (Row r : t.getRows()) {
									boolean isUserDefined = "USER".equals((String) r.get("DefinedBy"));
									if (!this.showUserDefinedOnlyObjects || isUserDefined) {
										String procedureName = (String) r.get("ProcedureName");
										AmiCenterGraphNode_Procedure target = gm.getProcedure(procedureName);
										createNode(this.treeNodeProcedures, target);
										this.procedureNodeByNames.put(procedureName, target);
									}
								}
								break;
							case ID_METHOD:
								for (Row r : t.getRows()) {
									boolean isUserDefined = "USER".equals((String) r.get("DefinedBy"));
									if (!this.showUserDefinedOnlyObjects || isUserDefined) {
										String methodDef = (String) r.get("Definition");
										AmiCenterGraphNode_Method target = gm.getOrCreateMethod(methodDef, !isUserDefined);
										createNode(this.treeNodeMethods, target);
										this.methodNodeByNames.put(methodDef, target);
									}

								}
								break;
							case ID_INDEX:
								for (Row r : t.getRows()) {
									boolean isUserDefined = "USER".equals((String) r.get("DefinedBy"));
									if (!this.showUserDefinedOnlyObjects || isUserDefined) {
										String indexName = (String) r.get("IndexName");
										String indexOn = (String) r.get("TableName");
										String formattedName = AmiCenterManagerUtils.formatIndexNames(indexOn, indexName);
										AmiCenterGraphNode_Table owner = this.tableNodeByNames.get(indexOn);
										AmiCenterGraphNode_Index target = gm.getIndex(formattedName);
										createNode(this.treeNodeIndexes, target);
										target.setBindingTable(owner);
										owner.bindTargetIndex(target.getLabel(), target);
										this.indexNodeByNames.put(formattedName, target);
									}
								}
								break;
							case ID_DBO:
								for (Row r : t.getRows()) {
									boolean isUserDefined = "USER".equals((String) r.get("DefinedBy"));
									if (!this.showUserDefinedOnlyObjects || isUserDefined) {
										String dboName = (String) r.get("DboName");
										AmiCenterGraphNode_Dbo target = gm.getDbo(dboName);
										createNode(this.treeNodeDBOs, target);
										this.dboNodeByNames.put(dboName, target);
									}
								}
								break;
						}

					}

				} else if (query.contains("DESCRIBE")) {
					//describe [entity] [entity_name]
					String temp = SH.afterFirst(query, " ");
					String target = SH.beforeFirst(temp, " ");
					String name = SH.afterFirst(temp, " ");
					Table t = tables.get(0);
					Row r = t.getRow(0);
					PortletManager manager = service.getPortletManager();
					AdminNode n = null;
					switch (target) {
						case "TABLE":
							String tableScript = Caster_String.INSTANCE.cast(r.get("SQL"));
							boolean hasIndex = tableScript.contains("CREATE INDEX");
							String createTableScript = null;
							if (hasIndex)
								createTableScript = SH.beforeFirst(tableScript, "CREATE INDEX");
							else
								createTableScript = tableScript;
							CreateTableNode ctn = AmiCenterManagerUtils.scriptToCreateTableNode(createTableScript);
							Map<String, String> tableConfig = AmiCenterManagerUtils.parseAdminNode_Table(ctn);
							//manager.showDialog("Edit Table", new AmiCenterManagerAddTablePortlet(manager.generateConfig(), tableConfig, AmiCenterEntityTypeConsts.EDIT), 500, 550);
							Map<String, AmiCenterGraphNode_Trigger> triggerBinding = this.tableNodeByNames.get(name).getTargetTriggers();
							Map<String, AmiCenterGraphNode_Index> indexBinding = this.tableNodeByNames.get(name).getTargetIndexes();
							AmiCenterGraphNode_Table correlationNodeTable = tableNodeByNames.get(name);
							this.service.getAmiCenterManagerEditorsManager().showEditTablePortlet(tableConfig, triggerBinding, indexBinding, correlationNodeTable);
							//							manager.showDialog("Rich Table Editor",
							//									new AmiCenterManagerRichTableEditorPortlet(manager.generateConfig(), tableConfig, triggerBinding, indexBinding, correlationNodeTable), 1350,
							//									1500);

							break;
						case "TRIGGER":
							String triggerSql = Caster_String.INSTANCE.cast(r.get("SQL"));
							AmiCenterGraphNode_Trigger correlationNodeTrigger = triggerNodeByNames.get(name);
							this.service.getAmiCenterManagerEditorsManager().showEditCenterObjectPortlet(triggerSql, correlationNodeTrigger);
							//manager.showDialog("Edit Trigger", new AmiCenterManagerEditTriggerPortlet(manager.generateConfig(), triggerSql, correlationNode), 800, 850);
							break;
						case "TIMER":
							String timerSql = Caster_String.INSTANCE.cast(r.get("SQL"));
							timerSql = SH.beforeFirst(timerSql, "DISABLE TIMER");
							AmiCenterGraphNode_Timer correlationNodeTimer = timerNodeByNames.get(name);
							this.service.getAmiCenterManagerEditorsManager().showEditCenterObjectPortlet(timerSql, correlationNodeTimer);
							//manager.showDialog("Edit Timer", new AmiCenterManagerEditTimerPortlet(manager.generateConfig(), timerSql), 800, 850);
							break;
						case "PROCEDURE":
							String procedureSql = Caster_String.INSTANCE.cast(r.get("SQL"));
							AmiCenterGraphNode_Procedure correlationNodeProcedure = procedureNodeByNames.get(name);
							this.service.getAmiCenterManagerEditorsManager().showEditCenterObjectPortlet(procedureSql, correlationNodeProcedure);
							//manager.showDialog("Edit Procedure", new AmiCenterManagerEditProcedurePortlet(manager.generateConfig(), procedureSql), 800, 850);
							break;
						case "METHOD":
							String methodScript = Caster_String.INSTANCE.cast(r.get("SQL"));
							n = AmiCenterManagerUtils.scriptToAdminNode(methodScript);
							Map<String, String> methodConfig = AmiCenterManagerUtils.parseAdminNode_Method(n);
							manager.showDialog("Edit Method", new AmiCenterManagerAddMethodPortlet(manager.generateConfig(), methodConfig, AmiCenterEntityConsts.EDIT), 500, 550);
							break;
						case "INDEX":
							String indexScript = Caster_String.INSTANCE.cast(r.get("SQL"));
							n = AmiCenterManagerUtils.scriptToAdminNode(indexScript);
							Map<String, String> indexConfig = AmiCenterManagerUtils.parseAdminNode_Index(n);
							manager.showDialog("Edit Method", new AmiCenterManagerAddMethodPortlet(manager.generateConfig(), indexConfig, AmiCenterEntityConsts.EDIT), 500, 550);
							break;
						case "DBO":
							break;
					}

				}

			}
		}

	}

	private AmiCenterQueryDsRequest prepareRequest() {
		AmiCenterQueryDsRequest request = getManager().getTools().nw(AmiCenterQueryDsRequest.class);

		int timeout = 60;
		try {
			timeout = (int) (timeout * 1000);
		} catch (Exception e) {
			getManager().showAlert("Timeout is not in a valid format");
			return null;
		}

		request.setTimeoutMs(timeout);
		request.setQuerySessionKeepAlive(true);
		request.setInvokedBy(service.getUserName());
		request.setSessionVariableTypes(null);
		request.setSessionVariables(null);
		request.setAllowSqlInjection(false);//String template, dflt to false
		request.setPermissions(permissions);
		request.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
		request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_FRONTEND_SHELL);
		request.setDatasourceName(AMI_DS_NAME);
		request.setLimit(-1);
		return request;
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (changed) {
			changed = false;
			build();
		}
		if (graphNeedsRebuild) {
			smartGraph.rebuild();
			smartErGraph.rebuild();
			this.graphNeedsRebuild = false;
		}
	}

	private void build() {
		// will rebuild nodes
		buildNodes();
		//ensureFocusDm();
		onNodeClicked(this.tree.getTree(), null);
		//		if (showAmiscript)
		//			visitDom(this.treeNodePanels, service.getLayoutFilesManager().getLayout());
		//applyErrors(this.tree.getRoot());
	}

	public void onGraphNeedsRebuild() {
		this.graphNeedsRebuild = true;
		flagPendingAjax();
	}

	public void onChanged() {
		this.changed = true;
		flagPendingAjax();
	}

	private void visitTrigger(WebTreeNode parent, AmiCenterGraphNode_Trigger i) {
		WebTreeNode node = createNode(parent, i);
	}

	private void visitIndex(WebTreeNode parent, AmiCenterGraphNode_Index i) {
		WebTreeNode node = createNode(parent, i);
	}

	private WebTreeNode createIndexCategory(WebTreeNode tableNode) {
		WebTreeNode node = createNode(tableNode, AmiCenterGraphNode.TYPE_INDEX);
		return node;
	}

	private WebTreeNode createTriggerCategory(WebTreeNode tableNode) {
		WebTreeNode node = createNode(tableNode, AmiCenterGraphNode.TYPE_TRIGGER);
		return node;
	}

	private WebTreeNode createNode(WebTreeNode parent, byte group) {
		String icon = null;
		;
		String label = null;
		switch (group) {
			case AmiCenterGraphNode.TYPE_TRIGGER:
				icon = AmiWebConsts.CENTER_GRAPH_NODE_TRIGGER;
				label = TRIGGER_CATEGORY_NAME_UNDER_TABLE;
				break;
			case AmiCenterGraphNode.TYPE_INDEX:
				icon = AmiWebConsts.CENTER_GRAPH_NODE_INDEX;
				label = INDEX_CATEGORY_NAME_UNDER_TABLE;
				break;
			default:
				throw new UnsupportedOperationException();
		}

		//WebTreeNode r = parent.getTreeManager().createNode(label, parent, false, node);
		WebTreeNode r = this.tree.createNode(label, parent, false, null);
		r.setIconCssStyle(icon == null ? null : "_bgi=url('" + icon + "')");
		//		LongKeyMap.Node<List<WebTreeNode>> entry = this.nodesByGraphId.getNodeOrCreate(node.getUid());
		//		if (entry.getValue() == null)
		//			entry.setValue(new ArrayList<WebTreeNode>());
		//		entry.getValue().add(r);
		//		Object inner = node.getInner();
		//		if (inner instanceof AmiWebDomObject) {
		//			AmiWebDomObject dom = (AmiWebDomObject) inner;
		//			visitDom(r, dom);
		//		}
		return r;
	}

	@Override
	public void onCenterNodeAdded(AmiCenterGraphNode node) {
		onChanged();

	}

	@Override
	public void onCenterNodeRemoved(AmiCenterGraphNode node) {
		onChanged();

	}

	public FastTreePortlet getFastTreePortlet() {
		return this.tree;
	}
}
