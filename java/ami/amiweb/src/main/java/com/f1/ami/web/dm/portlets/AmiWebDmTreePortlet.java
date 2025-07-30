package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.AmiWebAliasPortlet;
import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebCompilerListener;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDividerPortlet;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebDomObjectDependency;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSpecialPortlet;
import com.f1.ami.web.AmiWebTabEntry;
import com.f1.ami.web.AmiWebTabPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.graph.AmiWebGraphHelper;
import com.f1.ami.web.graph.AmiWebGraphListener;
import com.f1.ami.web.graph.AmiWebGraphManager;
import com.f1.ami.web.graph.AmiWebGraphNode;
import com.f1.ami.web.graph.AmiWebGraphNodeRt;
import com.f1.ami.web.graph.AmiWebGraphNode_Datamodel;
import com.f1.ami.web.graph.AmiWebGraphNode_Datasource;
import com.f1.ami.web.graph.AmiWebGraphNode_Link;
import com.f1.ami.web.graph.AmiWebGraphNode_Panel;
import com.f1.ami.web.graph.AmiWebGraphNode_Realtime;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TreeStateCopier;
import com.f1.suite.web.portal.impl.TreeStateCopierIdGetter;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.visual.GraphListener;
import com.f1.suite.web.portal.impl.visual.GraphPortlet;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Node;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiWebDmTreePortlet extends GridPortlet implements AmiWebGraphListener, WebTreeContextMenuListener, AmiWebSpecialPortlet, WebTreeContextMenuFactory, GraphListener,
		TreeStateCopierIdGetter, ConfirmDialogListener, Comparator<WebTreeNode>, AmiWebCompilerListener, AmiWebDomObjectDependency {
	private static final Logger log = LH.get();

	public static class DomObjectWrapper {

		final private AmiWebDomObject inner;

		public DomObjectWrapper(AmiWebDomObject i) {
			this.inner = i;
		}

		public AmiWebDomObject getInner() {
			return this.inner;
		}

	}

	private FastTreePortlet tree;
	private AmiWebService service;
	private WebTreeNode treeNodePanels;
	private WebTreeNode treeNodeDatamodels;
	private WebTreeNode treeNodeDatasources;
	private WebTreeNode treeNodeLinks;
	private WebTreeNode treeNodeFeeds;
	private GraphPortlet graph;
	private AmiWebDmSmartGraph smartGraph;
	private String baseAlias;
	private WebTreeNode treeNodeProcessors;
	private AmiWebDmTreeListener amiDmTreeListener;
	private AmiWebHeaderPortlet header;
	private AmiWebDmHeaderPortlet amiHeader;
	//	private HashSet<String> aliases = new HashSet<String>();
	private boolean changed;
	private boolean graphNeedsRebuild;
	private LongKeyMap<List<WebTreeNode>> nodesByGraphId = new LongKeyMap<List<WebTreeNode>>();
	private boolean showDividers;
	private TabPortlet tabPortlet;
	private AmiWebDmScriptTreePortlet scriptTree;
	final private boolean allowModification;
	private String dmToFocus = null;

	public AmiWebDmTreePortlet(PortletConfig config, AmiWebService service, String baseAlias, boolean allowModification, String dmToFocus) {
		super(config);
		this.baseAlias = baseAlias;
		this.service = service;
		this.allowModification = allowModification;
		if (dmToFocus != null && this.service.getGraphManager().getDatamodels().containsKey(dmToFocus))
			this.setDmToFocus(dmToFocus);
		this.tree = new FastTreePortlet(generateConfig());
		this.tree.getTree().setComparator(this);
		this.tree.getTree().addMenuContextListener(this);
		//add default form and dialog style for this.tree
		this.tree.setFormStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());
		this.tree.setDialogStyle(AmiWebUtils.getService(getManager()).getUserDialogStyleManager());
		this.tree.addOption(this.tree.OPTION_HEADER_BAR_HIDDEN, Boolean.TRUE);
		this.graph = new GraphPortlet(generateConfig());
		this.graph.addGraphListener(this);
		this.smartGraph = new AmiWebDmSmartGraph(this, service, this.graph, allowModification);
		DividerPortlet div = new DividerPortlet(generateConfig(), true);
		div.setOffsetFromTopPx(300);
		this.header = new AmiWebHeaderPortlet(generateConfig());
		this.amiHeader = new AmiWebDmHeaderPortlet(this, header, allowModification);
		addChild(header, 0, 0, 1, 1);
		this.addChild(div, 0, 1, 1, 1);
		div.addChild(this.tree);
		this.scriptTree = new AmiWebDmScriptTreePortlet(generateConfig(), service);
		this.service.getDomObjectsManager().addGlobalListener(this);
		this.tabPortlet = new TabPortlet(generateConfig());
		this.tabPortlet.addChild("Datamodel", this.graph);
		this.tabPortlet.addChild("AmiScript", this.scriptTree);
		this.tabPortlet.setIsCustomizable(false);
		div.addChild(this.tabPortlet);
		AmiWebGraphManager gm = this.service.getGraphManager();
		gm.addListener(this);
		this.tree.getTree().setRootLevelVisible(false);
		this.tree.getTree().setContextMenuFactory(this);
		this.buildNodes();
		applyErrors(this.tree.getRoot());
		this.showTree();
		setSuggestedSize(1000, 800);
		this.service.addCompilerListener(this);
	}

	private void buildNodes() {

		final TreeStateCopier tsc = new TreeStateCopier(this.tree, this);
		AmiWebGraphManager gm = this.service.getGraphManager();

		// reset state, will clear all selected
		this.tree.clear();
		this.nodesByGraphId.clear();
		// initialize node collections
		this.treeNodeDatasources = createNode(this.tree.getRoot(), "Datasources", AmiWebConsts.DM_TREE_ICON_DS, null);
		this.treeNodeDatamodels = createNode(this.tree.getRoot(), "Datamodels", AmiWebConsts.DM_TREE_ICON_DM, null);
		this.treeNodePanels = createNode(this.tree.getRoot(), "Dashboard", AmiWebConsts.DM_TREE_ICON_DESKTOP, null);
		this.treeNodeLinks = createNode(this.tree.getRoot(), "Relationships", AmiWebConsts.DM_TREE_ICON_RELATIONSHIP, null);
		this.treeNodeFeeds = createNode(this.tree.getRoot(), "Realtime Feeds", AmiWebConsts.DM_TREE_ICON_FEED, null);
		this.treeNodeProcessors = createNode(this.tree.getRoot(), "Realtime Processors", AmiWebConsts.DM_TREE_ICON_PROCESSOR, null);
		// create nodes
		for (AmiWebGraphNode_Datasource i : gm.getDatasources().values()) {
			if (!isLayoutVisible(i.getId()))
				continue;
			WebTreeNode node = createNode(this.treeNodeDatasources, i);
			for (AmiWebGraphNode_Datamodel j : i.getTargetDatamodels().values())
				visitDm(node, j);
		}

		for (AmiWebGraphNode_Datamodel i : gm.getDatamodels().values()) {
			if (!isLayoutVisible(i.getId()))
				continue;
			if (!i.getSourceDatamodels().isEmpty())
				continue;
			visitDm(this.treeNodeDatamodels, i);
		}

		for (AmiWebGraphNode_Panel i : gm.getPanels().values()) {
			if (!isLayoutVisible(i.getId()))
				continue;
			if (i.getParentPanel() == null) {
				if (i.getInner() == null) {
					LH.warning(log, getManager().describeUser(), " --> Reference to Missing Panel: ", i.getId());
					continue;
				}
				Window window = service.getDesktop().getDesktop().getWindowNoThrow(i.getInner().getPortletId());
				if (window != null) {
					WebTreeNode windowNode = createNode(this.treeNodePanels, window.getName(), AmiWebConsts.DM_TREE_ICON_WINDOW, window);
					visitPn(windowNode, i, false);
				}
			}
		}

		for (AmiWebGraphNode_Link i : gm.getLinks().values()) {
			if (!isLayoutVisible(i.getId()))
				continue;
			WebTreeNode node = createNode(this.treeNodeLinks, i);
			if (i.getSourcePanel() != null) {
				createNode(node, i.getSourcePanel());
			}
			if (i.getTargetDm() != null) {
				createNode(node, i.getTargetDm());
			}
			if (i.getTargetPanel() != null) {
				createNode(node, i.getTargetPanel());
			}
		}
		for (AmiWebGraphNode_Realtime i : gm.getRealtimes().values()) {
			if (i.isFeed()) {
				String id2 = SH.stripPrefix(i.getId(), AmiWebManagers.FEED, true);
				if (!isLayoutVisible(id2))
					continue;
				visitRt(this.treeNodeFeeds, i);
			} else {
				String id2 = SH.stripPrefix(i.getId(), AmiWebManagers.PROCESSOR, true);
				if (!isLayoutVisible(id2))
					continue;
				visitRt(this.treeNodeProcessors, i);
			}
		}
		tsc.reapplyState();
	}

	private void build() {
		// will rebuild nodes
		buildNodes();
		ensureFocusDm();
		onNodeClicked(this.tree.getTree(), null);
		//		if (showAmiscript)
		//			visitDom(this.treeNodePanels, service.getLayoutFilesManager().getLayout());
		applyErrors(this.tree.getRoot());
	}

	private void showTree() {
		IdentityHashSet<AmiWebGraphNode<?>> allSelected = new IdentityHashSet<AmiWebGraphNode<?>>();
		IdentityHashSet<AmiWebGraphNode<?>> origNodes = new IdentityHashSet<AmiWebGraphNode<?>>();
		AmiWebGraphNode_Datamodel toFocus = ensureFocusDm();
		if (toFocus != null) {
			origNodes.add(toFocus);
			allSelected.add(toFocus);
		}
		this.smartGraph.buildGraph(origNodes, allSelected);
		this.scriptTree.build(new ArrayList<AmiWebDomObject>());
	}

	private AmiWebGraphNode_Datamodel ensureFocusDm() {
		if (this.getDmToFocus() != null) {
			AmiWebGraphNode_Datamodel dmNode = this.service.getGraphManager().getDatamodels().get(this.getDmToFocus());
			// graphManager updates dm list when user hits cancel
			if (dmNode != null) {
				// when user hits finish on new dm editor
				List<WebTreeNode> list = this.nodesByGraphId.get(dmNode.getUid());
				if (CH.isntEmpty(list)) {
					WebTreeNode last = CH.last(list);
					last.setSelected(true);
					tree.getTreeManager().setActiveSelectedNode(last);
					return dmNode;
				}
			}
			// dmNode will be null if user hit cancel on new dm editor. buildNodes() already cleared selection so no op
		}
		return null;
	}

	//returns true if child/grand child has error
	private boolean applyErrors(WebTreeNode node) {
		boolean r = hasError(node);

		for (WebTreeNode i : node.getChildren()) {
			if (applyErrors(i))
				r = true;
		}

		String icon = SH.afterLast(node.getIconCssStyle(), '=');
		if (r) {
			node.setIconCssStyle("_bgi=url('" + AmiWebConsts.DM_TREE_ICON_ERROR + "')," + icon);
		} else {
			node.setIconCssStyle("_bgi=" + icon);
		}
		return r;
	}

	private boolean hasError(WebTreeNode node) {
		Object data = node.getData();
		AmiWebDomObject dom = null;
		if (data instanceof AmiWebGraphNode) {
			if (data instanceof AmiWebGraphNode_Datasource) {
				AmiWebGraphNode_Datasource ds = (AmiWebGraphNode_Datasource) data;
				if (ds.getInner() == null)
					return true;
			}
			// ok to collapse into one?
			if (data instanceof AmiWebGraphNode_Datamodel) {
				AmiWebGraphNode_Datamodel ds = (AmiWebGraphNode_Datamodel) data;
				if (ds.getInner() == null)
					return true;
			}
			Object t = ((AmiWebGraphNode) data).getInner();
			if (t instanceof AmiWebDomObject)
				dom = (AmiWebDomObject) t;
		} else if (data instanceof AmiWebDomObject)
			dom = (AmiWebDomObject) data;
		return dom != null && hasError(dom);
	}

	private boolean hasError(AmiWebDomObject dom) {
		AmiWebAmiScriptCallbacks asc = dom.getAmiScriptCallbacks();
		if (asc != null) {
			for (AmiWebAmiScriptCallback i : asc.getAmiScriptCallbackDefinitionsMap().values())
				if (i.hasError(false))
					return true;
		}
		AmiWebFormulas formulas = dom.getFormulas();
		if (formulas != null) {
			for (String i : formulas.getFormulaIds())
				if (formulas.getFormula(i).getFormulaError(false) != null)
					return true;
		}
		List<AmiWebDomObject> children = dom.getChildDomObjects();
		if (CH.isntEmpty(children))
			for (AmiWebDomObject i : children)
				if (hasError(i))
					return true;

		return false;
	}

	private boolean isLayoutVisible(String id) {

		if (!AmiWebUtils.isParentAlias(this.baseAlias, id))
			return false;
		return true;
		//		return this.aliases.size() == 0 || this.aliases.contains(AmiWebUtils.getAliasFromAdn(id));
	}
	private void visitRt(WebTreeNode parent, AmiWebGraphNodeRt<?> i) {
		switch (i.getType()) {
			case AmiWebGraphNode.TYPE_PANEL:
				visitPn(parent, (AmiWebGraphNode_Panel) i, true);
				return;
			case AmiWebGraphNode.TYPE_DATAMODEL:
				visitDm(parent, (AmiWebGraphNode_Datamodel) i);
				return;
			default:
				break;
		}
		WebTreeNode node = createNode(parent, i);
		for (AmiWebGraphNodeRt<?> dm : i.getTargetRealtimes().values()) {
			visitRt(node, dm);
		}
	}
	private void visitPn(WebTreeNode parent, AmiWebGraphNode_Panel pn, boolean followRealtime) {
		WebTreeNode node;
		if (pn.getInner() instanceof AmiWebDividerPortlet) {
			if (showDividers) {
				node = createNode(parent, pn);
			} else {
				node = parent;
			}
		} else {
			node = createNode(parent, pn);
		}
		if (!followRealtime) {
			if (pn.getInner() instanceof AmiWebTabPortlet) {
				AmiWebTabPortlet tabs = (AmiWebTabPortlet) pn.getInner();
				int n = 1;
				for (AmiWebGraphNode_Panel i : pn.getChildrenPanels().values()) {
					AmiWebTabEntry tab = tabs.getTabFor(i.getInner());
					String name = tab.getNameFormula().getFormula(false);
					WebTreeNode node2 = createNode(node, "Tab" + ((n < 10 ? "0" : "") + n) + ": " + SH.trim('"', name), AmiWebConsts.DM_TREE_ICON_TAB, tab);
					n++;
					visitPn(node2, i, false);
					//					if (showAmiscript)
					//						visitDom(node2, tab);
				}
			} else {
				for (AmiWebGraphNode_Panel i : pn.getChildrenPanels().values()) {
					visitPn(node, i, false);
				}
			}
		} else {
			for (AmiWebGraphNodeRt<?> dm : pn.getTargetRealtimes().values())
				visitRt(node, dm);
		}
	}

	private void visitDm(WebTreeNode parent, AmiWebGraphNode_Datamodel i) {
		WebTreeNode node = createNode(parent, i);
		for (AmiWebGraphNode_Datamodel dm : i.getTargetDatamodels().values())
			visitDm(node, dm);
		for (AmiWebGraphNode_Panel dm : i.getTargetPanels().values())
			createNode(node, dm);
		for (AmiWebGraphNodeRt<?> dm : i.getTargetRealtimes().values())
			visitRt(node, dm);
	}

	public static String getIcon(AmiWebGraphNode node) {
		Object inner = node.getInner();
		switch (node.getType()) {
			case AmiWebGraphNode.TYPE_LINK:
				return AmiWebConsts.DM_TREE_ICON_RELATIONSHIP;
			case AmiWebGraphNode.TYPE_FEED:
				return AmiWebConsts.DM_TREE_ICON_FEED;
			case AmiWebGraphNode.TYPE_PROCESSOR:
				return AmiWebConsts.DM_TREE_ICON_PROCESSOR;
			case AmiWebGraphNode.TYPE_PANEL: {
				AmiWebGraphNode_Panel panel = (AmiWebGraphNode_Panel) node;
				if (inner instanceof AmiWebDividerPortlet) {
					return ((AmiWebDividerPortlet) inner).isVertical() ? AmiWebConsts.DM_TREE_ICON_DIVIDER_V : AmiWebConsts.DM_TREE_ICON_DIVIDER_H;
				} else if (inner instanceof AmiWebTabPortlet)
					return AmiWebConsts.DM_TREE_ICON_TABSPANEL;
				return panel.isRealtime() ? AmiWebConsts.DM_TREE_ICON_PANEL_RT : AmiWebConsts.DM_TREE_ICON_PANEL_ST;
			}
			case AmiWebGraphNode.TYPE_DATASOURCE:
				return AmiWebConsts.DM_TREE_ICON_DS;
			case AmiWebGraphNode.TYPE_DATAMODEL:
				return ((AmiWebGraphNode_Datamodel) node).isBlender() ? AmiWebConsts.DM_TREE_ICON_BLENDER : AmiWebConsts.DM_TREE_ICON_DM;
		}
		return null;
	}

	private WebTreeNode createNode(WebTreeNode parent, String title, String icon, Object data) {
		WebTreeNode r = this.tree.createNode(title, parent, false, data);
		r.setIconCssStyle(icon == null ? null : "_bgi=url('" + icon + "')");
		return r;
	}

	private WebTreeNode createNode(WebTreeNode parent, AmiWebGraphNode node) {
		String icon = getIcon(node);
		String label = node.getLabel();
		String desc = node.getDescription();
		if (SH.is(desc))
			label += " (" + desc + ")";
		WebTreeNode r = parent.getTreeManager().createNode(label, parent, false, node);
		r.setIconCssStyle(icon == null ? null : "_bgi=url('" + icon + "')");
		LongKeyMap.Node<List<WebTreeNode>> entry = this.nodesByGraphId.getNodeOrCreate(node.getUid());
		if (entry.getValue() == null)
			entry.setValue(new ArrayList<WebTreeNode>());
		entry.getValue().add(r);
		//		Object inner = node.getInner();
		//		if (inner instanceof AmiWebDomObject) {
		//			AmiWebDomObject dom = (AmiWebDomObject) inner;
		//			visitDom(r, dom);
		//		}
		return r;
	}

	@Override
	public void onClosed() {
		this.service.getGraphManager().removeListener(this);
		this.service.removeCompilerListener(this);
		this.service.getDomObjectsManager().removeGlobalListener(this);
		super.onClosed();
	}
	@Override
	public void onAdded(AmiWebGraphNode node) {
		onChanged();
		// TODO Auto-generated method stub

	}
	@Override
	public void onRemoved(AmiWebGraphNode removed) {
		onChanged();
		// TODO Auto-generated method stub

	}
	@Override
	public void onIdChanged(AmiWebGraphNode node, String oldId, String newId) {
		onChanged();
		// TODO Auto-generated method stub

	}
	@Override
	public void onInnerChanged(AmiWebGraphNode node, Object old, Object nuw) {
		onChanged();
		// TODO Auto-generated method stub

	}
	@Override
	public void onEdgeAdded(byte type, AmiWebGraphNode src, AmiWebGraphNode tgt) {
		onChanged();
		// TODO Auto-generated method stub

	}
	public void onChanged() {
		this.changed = true;
		flagPendingAjax();
	}

	@Override
	public void onEdgeRemoved(byte type, AmiWebGraphNode src, AmiWebGraphNode tgt) {
		onChanged();

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
			this.graphNeedsRebuild = false;
		}
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		List<WebTreeNode> nodes = this.tree.getTree().getSelected();
		if (amiDmTreeListener != null) {
			ArrayList<AmiWebGraphNode<?>> t = new ArrayList<AmiWebGraphNode<?>>(nodes.size());
			for (WebTreeNode i : nodes) {
				AmiWebGraphNode<?> t2 = getData(i);
				if (t2 != null)
					t.add(t2);
			}
			if (!t.isEmpty())
				amiDmTreeListener.onDoubleClicked(t);
			return;
		}
		if (nodes.size() == 1) {
			WebTreeNode node = nodes.get(0);
			Object data = node.getData();
			if (node == this.treeNodeDatamodels || node == this.treeNodeDatasources || node == this.treeNodeLinks || node == this.treeNodePanels || node == this.treeNodeFeeds
					|| node == this.treeNodeProcessors) {
				if (node.getIsExpandable())
					node.setIsExpanded(!node.getIsExpanded());
			}

			if (data instanceof Window) {
				Window window = (Window) data;
				window.bringToFront();
			} else if (data instanceof AmiWebGraphNode) {
				if (AmiWebGraphHelper.isDefined((AmiWebGraphNode) data))
					AmiWebGraphHelper.openEditor(service, (AmiWebGraphNode) data);
			} else if (data instanceof AmiWebTabEntry) {
				AmiWebTabEntry tab = (AmiWebTabEntry) data;
				PortletHelper.ensureVisible(tab.getTab().getPortlet());
				//			} else if (data instanceof AmiWebAmiScriptCallback) {
				//				AmiWebAmiScriptCallback cb = (AmiWebAmiScriptCallback) data;
				//				AmiWebDomObject dom = cb.getParent().getThis();
				//				AmiWebEditAmiScriptCallbackPortlet editor = this.service.getDomObjectsManager().showCallbackEditor(dom.getAri(), cb.getName());
			} else if (data instanceof DomObjectWrapper) {
				AmiWebDomObject dom = ((DomObjectWrapper) data).getInner();
				this.service.getDomObjectsManager().showFormulaEditor(dom.getAri(), null);
				//			AmiWebEditAmiScriptCallbackPortlet editor = this.getDomObjectsManager().showCallbackEditor(ari, callback);
			} else if (data instanceof AmiWebFormula) {
				Object data2 = node.getParent().getData();
				AmiWebFormula amiWebFormula = (AmiWebFormula) data;
				if (data2 instanceof DomObjectWrapper) {
					AmiWebDomObject dom = ((DomObjectWrapper) data2).getInner();
					this.service.getDomObjectsManager().showFormulaEditor(dom.getAri(), amiWebFormula.getFormulaId());
				} else if (data2 instanceof AmiWebGraphNode) {
					Object object = ((AmiWebGraphNode) data2).getInner();
					if (object instanceof AmiWebDomObject) {
						this.service.getDomObjectsManager().showFormulaEditor(((AmiWebDomObject) object).getAri(), amiWebFormula.getFormulaId());
					}

				}
			}
		}
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		if (SH.startsWith(action, "editformula_")) {
			String ari = SH.stripPrefix(action, "editformula_", true);
			AmiWebFormula formula = service.getDomObjectsManager().getFormula(ari);
			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "edit Formula", ConfirmDialogPortlet.TYPE_OK_CANCEL, this, new FormPortletTextField("Formula: "));
			cdp.setCallback("EDIT_FORMULA");
			cdp.setCorrelationData(formula);
			cdp.setInputFieldValue(formula.getFormula(false));
			getManager().showDialog("Edit Formula", cdp, 500, 200);
		} else if (SH.startsWith(action, "formulaerror_")) {
			String ari = SH.stripPrefix(action, "formulaerror_", true);
			AmiWebFormula formula = service.getDomObjectsManager().getFormula(ari);
			Exception err = formula.getFormulaError(false);
			if (err != null)
				getManager().showAlert("Error for " + ari + ": " + err.getMessage() + " <BR>(click more for details)", err);
		} else if (SH.startsWith(action, "cberror_")) {
			String ari = SH.stripPrefix(action, "cberror_", true);
			AmiWebAmiScriptCallback formula = service.getDomObjectsManager().getCallback(ari);
			Exception err = formula.getError(false);
			if (err != null)
				getManager().showAlert("Error for " + ari + ": " + err.getMessage() + " <BR>(click more for details)", err);
		}
		List<WebTreeNode> selected = this.tree.getTree().getSelected();
		List<AmiWebGraphNode<?>> nodes = new ArrayList<AmiWebGraphNode<?>>(selected.size());

		for (WebTreeNode data : selected) {
			AmiWebGraphNode<?> data2 = getData(data);
			if (data2 != null)
				nodes.add(data2);
		}
		AmiWebDmSmartGraphMenu.onMenuItem(service, action, nodes);

	}
	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		if (selected.size() == 1) {
			WebTreeNode t = selected.get(0);
			if (t == this.treeNodeDatamodels && allowModification) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Datamodel", true, "add_dm"));
				return menu;
			}
			if (t == this.treeNodeDatasources && allowModification) {
				BasicWebMenu menu = new BasicWebMenu();
				menu.addChild(new BasicWebMenuLink("Add Datasource", true, "add_ds"));
				return menu;
			}
			if (t.getData() instanceof AmiWebFormula) {
				BasicWebMenu menu = new BasicWebMenu();
				AmiWebFormula amiWebFormula = (AmiWebFormula) t.getData();
				if (allowModification)
					menu.add(new BasicWebMenuLink("Edit Formula", true, "editformula_" + amiWebFormula.getAri()));
				if (amiWebFormula.getFormulaError(false) != null)
					menu.add(new BasicWebMenuLink("Show Error", true, "formulaerror_" + amiWebFormula.getAri()));
				return menu;
			}
			if (t.getData() instanceof AmiWebAmiScriptCallback) {
				BasicWebMenu menu = new BasicWebMenu();
				AmiWebAmiScriptCallback amiWebFormula = (AmiWebAmiScriptCallback) t.getData();
				if (amiWebFormula.getError(false) != null)
					menu.add(new BasicWebMenuLink("Show Error", true, "cberror_" + amiWebFormula.getAri()));
				//				if (amiWebFormula.getFormulaError(false) != null)
				//					menu.add(new BasicWebMenuLink("Show Error", true, "formulaerror_" + amiWebFormula.getAri()));
				return menu;
			}
		}

		List<AmiWebGraphNode<?>> nodes2 = new ArrayList<AmiWebGraphNode<?>>(selected.size());
		for (WebTreeNode data : selected) {
			AmiWebGraphNode<?> n = getData(data);
			if (n == null)
				return null;
			nodes2.add(n);
		}
		BasicWebMenu menu = AmiWebDmSmartGraphMenu.createContextMenu(service, nodes2, this.allowModification);
		return menu;
	}
	public static AmiWebGraphNode<?> getData(WebTreeNode data) {
		Object r = data.getData();
		return r instanceof AmiWebGraphNode ? (AmiWebGraphNode<?>) r : null;
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		List<AmiWebDomObject> selected = new ArrayList<AmiWebDomObject>();
		IdentityHashSet<AmiWebGraphNode<?>> allSelected = new IdentityHashSet<AmiWebGraphNode<?>>();
		IdentityHashSet<AmiWebGraphNode<?>> origNodes = new IdentityHashSet<AmiWebGraphNode<?>>();
		for (WebTreeNode i : tree.getSelected()) {
			Object t = i.getData();
			if (t instanceof AmiWebGraphNode) {
				AmiWebGraphNode<?> gn = (AmiWebGraphNode<?>) t;
				if (gn.getInner() instanceof AmiWebDomObject)
					selected.add((AmiWebDomObject) gn.getInner());
				if (t instanceof AmiWebGraphNode_Link) {
					AmiWebGraphNode_Link link = (AmiWebGraphNode_Link) t;
					if (link.getSourcePanel() != null) {
						origNodes.add(link.getSourcePanel());
						allSelected.add(link.getSourcePanel());
					}
					if (link.getTargetPanel() != null) {
						origNodes.add(link.getTargetPanel());
						allSelected.add(link.getTargetPanel());
					}
					if (link.getTargetDm() != null) {
						origNodes.add(link.getTargetDm());
						allSelected.add(link.getTargetDm());
					}
				}
				origNodes.add(gn);
				allSelected.add(gn);
			} else if (t instanceof Window) {
				Window w = (Window) t;
				AmiWebAliasPortlet p = (AmiWebAliasPortlet) w.getPortlet();
				AmiWebGraphNode_Panel t2 = service.getGraphManager().getNode(p);
				if (t2 != null) {
					origNodes.add((AmiWebGraphNode<?>) t2);
					allSelected.add((AmiWebGraphNode<?>) t2);
				}
			} else if (t instanceof AmiWebTabEntry) {
				selected.add((AmiWebTabEntry) t);
				AmiWebTabEntry te = (AmiWebTabEntry) t;
				AmiWebAliasPortlet p = (AmiWebAliasPortlet) te.getTab().getPortlet();
				AmiWebGraphNode_Panel t2 = service.getGraphManager().getNode(p);
				if (t2 != null) {
					origNodes.add((AmiWebGraphNode<?>) t2);
					allSelected.add((AmiWebGraphNode<?>) t2);
				}
			} else if (i == this.treeNodeDatamodels || i == this.treeNodeDatasources || i == this.treeNodeLinks || i == this.treeNodeFeeds || i == this.treeNodeProcessors) {
				for (WebTreeNode j : i.getChildren()) {
					Object t2 = j.getData();
					if (t2 instanceof AmiWebGraphNode_Link) {
						AmiWebGraphNode_Link link = (AmiWebGraphNode_Link) t2;
						if (link.getSourcePanel() != null) {
							origNodes.add(link.getSourcePanel());
						}
						if (link.getTargetPanel() != null) {
							origNodes.add(link.getTargetPanel());
						}
						if (link.getTargetDm() != null) {
							origNodes.add(link.getTargetDm());
						}
					}
					if (t2 != null)
						origNodes.add((AmiWebGraphNode<?>) t2);
				}
			} else if (i == this.treeNodePanels) {
				for (WebTreeNode j : i.getChildren()) {
					Object t2 = j.getData();
					if (t2 instanceof Window) {
						Window w = (Window) t2;
						AmiWebAliasPortlet p = (AmiWebAliasPortlet) w.getPortlet();
						AmiWebGraphNode_Panel t3 = service.getGraphManager().getNode(p);
						if (t3 != null)
							origNodes.add((AmiWebGraphNode<?>) t3);
					}
				}
			}
		}

		this.smartGraph.buildGraph(origNodes, allSelected);
		this.scriptTree.build(selected);
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {

	}
	public void showInDataModeler(String p) {
		AmiWebGraphNode_Panel node = this.service.getGraphManager().getPanels().get(p);
		if (node != null) {
			if (node.getInner() instanceof AmiWebDividerPortlet && !this.showDividers) {
				setShowDividers(true);
				build();
				smartGraph.rebuild();
				changed = false;
				this.graphNeedsRebuild = false;
			}
			List<WebTreeNode> tn = this.nodesByGraphId.get(node.getUid());
			if (CH.isntEmpty(tn)) {
				for (WebTreeNode i : tn) {
					i.setSelected(true);
					i.ensureVisible();
				}
				WebTreeNode i = CH.first(tn);
				i.ensureVisible();
				tree.getTreeManager().setActiveSelectedNode(i);
				onNodeClicked(tree.getTree(), i);
			}
		}
	}

	public void setOverrideDblClick(AmiWebDmTreeListener listener) {
		this.amiDmTreeListener = listener;
	}
	public void expandDatamodels() {
		this.treeNodeDatasources.setIsExpanded(true);
		this.treeNodeDatamodels.setIsExpanded(true);
	}
	public void expandRealtimes() {
		this.treeNodeProcessors.setIsExpanded(true);
		this.treeNodeFeeds.setIsExpanded(true);
	}
	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		// TODO Auto-generated method stub
		return false;
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
		if (amiDmTreeListener != null) {
			List<AmiWebGraphNode<?>> nodes = new ArrayList<AmiWebGraphNode<?>>();
			for (Node node : graphPortlet.getSelectedNodes()) {
				AmiWebGraphNode gn = AmiWebDmSmartGraph.getData(node);
				if (gn != null)
					nodes.add(gn);
			}
			if (!nodes.isEmpty())
				amiDmTreeListener.onDoubleClicked(nodes);
		} else {
			for (Node node : graphPortlet.getSelectedNodes()) {
				AmiWebGraphNode gn = AmiWebDmSmartGraph.getData(node);
				AmiWebGraphHelper.openEditor(service, gn);
			}
		}

	}
	@Override
	public void onKeyDown(String keyCode, String ctrl) {
		// TODO Auto-generated method stub

	}
	public List<AmiWebGraphNode<?>> getSelectedNodes() {
		List<AmiWebGraphNode<?>> r = new ArrayList<AmiWebGraphNode<?>>();
		if (this.graph.getSelectedCount() > 0) {
			for (Node i : this.graph.getSelectedNodes()) {
				AmiWebGraphNode t = AmiWebDmSmartGraph.getData(i);
				if (t != null)
					r.add(t);
			}
		} else {
			for (WebTreeNode i : this.tree.getTree().getSelected()) {
				AmiWebGraphNode<?> t = getData(i);
				if (t != null)
					r.add(t);
			}
		}
		return r;
	}
	public AmiWebDmSmartGraph getGraph() {
		return this.smartGraph;
	}
	public AmiWebService getService() {
		return this.service;
	}
	public String getBaseAlias() {
		return this.baseAlias;
	}
	//	public void setLayouts(LinkedHashSet<String> selected) {
	//		this.aliases.clear();
	//		this.aliases.addAll(selected);
	//	}

	@Override
	public Object getId(WebTreeNode node) {
		AmiWebGraphNode<?> data = getData(node);
		if (data != null)
			return data;
		else
			return node.getName();
	}

	public void onGraphNeedsRebuild() {
		this.graphNeedsRebuild = true;
		flagPendingAjax();
	}

	public void setShowDividers(boolean showDividers) {
		if (this.showDividers == showDividers)
			return;
		this.showDividers = showDividers;
		this.amiHeader.updateShowDivButton();
		onChanged();
	}

	public boolean getShowDividers() {
		return this.showDividers;
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
			if ("EDIT_FORMULA".equals(source.getCallback())) {
				AmiWebFormula formula = (AmiWebFormula) source.getCorrelationData();
				formula.setFormula((String) source.getInputFieldValue(), false);
			}
		}
		return true;
	}

	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {
		Object d1 = o1.getData();
		Object d2 = o2.getData();
		int v1 = getSort(d1);
		int v2 = getSort(d2);
		if (v1 == v2)
			return SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(o1.getName(), o2.getName());
		else
			return OH.compare(v1, v2);
	}

	private int getSort(Object d) {
		if (d instanceof AmiWebGraphNode)
			return 0;
		if (d instanceof DesktopPortlet.Window)
			return 1;
		if (d instanceof AmiWebTabEntry)
			return 2;
		if (d instanceof DomObjectWrapper)
			return 3;
		if (d instanceof AmiWebAmiScriptCallback)
			return 4;
		if (d instanceof AmiWebFormula)
			return 5;
		return 100;
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		onChanged();
	}

	@Override
	public void onRecompiled() {
		onChanged();
	}

	@Override
	public void onCallbackChanged(AmiWebAmiScriptCallback callback, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		onChanged();
	}

	public boolean isAllowModification() {
		return allowModification;
	}

	public String getDmToFocus() {
		return dmToFocus;
	}

	public void setDmToFocus(String dmToFocus) {
		this.dmToFocus = dmToFocus;
	}
	public TabPortlet getTabPortlet() {
		return this.tabPortlet;
	}
	public void focusDatamodelTab() {
		this.tabPortlet.setActiveTab(this.tabPortlet.getTabAtLocation(0).getPortlet());
	}

	@Override
	public void initLinkedVariables() {
	}

	@Override
	public void onDomObjectAriChanged(AmiWebDomObject target, String oldAri) {
	}

	@Override
	public void onDomObjectEvent(AmiWebDomObject object, byte eventType) {
	}

	@Override
	public void onDomObjectRemoved(AmiWebDomObject object) {
		onChanged();
	}

	@Override
	public void onDomObjectAdded(AmiWebDomObject object) {
		onChanged();
	}
}
