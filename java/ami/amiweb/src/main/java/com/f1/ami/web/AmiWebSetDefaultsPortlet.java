package com.f1.ami.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.tree.WebTreeColumnMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public class AmiWebSetDefaultsPortlet extends GridPortlet
		implements FormPortletListener, WebTreeContextMenuListener, WebTreeColumnMenuFactory, ConfirmDialogListener, WebTreeContextMenuFactory {

	private static final String ACTION_KEEP_DEFAULT = "KEEP_DEFAULT";
	private static final String ACTION_SET_CURRENT_TO_DEFAULT = "SET_CURRENT_TO_DEFAULT";
	private static final String ACTION_NO_DEFAULT = "NO_DEFAULT";
	private static final String ACTION_REVERT = "REVERT";
	private static final String KEEP_DEFAULT_HTML = "<B>Keep Default</B>";
	private static final String SET_CURRENT_TO_DEFAULT_HTML = "<B>Set Current as Default</B>";
	private static final String NO_DEFAULT_HTML = "<B>No Default</B>";
	private static final String HEADER_DESC = "To set defaults for a component, choose an action from its corresponding dropdown menu. " + "Choose " + KEEP_DEFAULT_HTML
			+ " to keep the existing default for the component, " + SET_CURRENT_TO_DEFAULT_HTML + " to set the component's current position as default, " + NO_DEFAULT_HTML
			+ " to clear the components existing default.";

	private static final Logger log = LH.get();

	private final AmiWebHeaderPortlet header;
	private final FormPortlet form;
	private final DesktopPortlet desktop;
	private final DividerPortlet dividerPortlet;
	private final GridPortlet positionFormContainer;
	private final FormPortlet positionForm;
	private final FastTreePortlet treePortlet;
	private final FastWebTree fastWebTree;
	private final WebTreeManager treeManager;
	private WebTreeNode lastDoubleClickedNode;
	private final Map<String, Tab> tabsProneToChange;
	private WebTreeNode nodeCurrentlyInPositionForm;
	private final FormPortletButton setDefaultButton;
	private final FormPortletButton cancelDefaultButton;

	public AmiWebSetDefaultsPortlet(PortletConfig config, DesktopPortlet desktop) {
		super(config);
		this.form = new FormPortlet(generateConfig());
		this.desktop = desktop;

		this.header = new AmiWebHeaderPortlet(generateConfig());
		// vertical divider
		this.dividerPortlet = new DividerPortlet(generateConfig(), true);

		// form on the right
		this.positionFormContainer = new GridPortlet(generateConfig());
		this.positionForm = new FormPortlet(generateConfig());
		this.positionFormContainer.addChild(positionForm);

		// tree
		this.treePortlet = new FastTreePortlet(generateConfig());
		this.fastWebTree = this.treePortlet.getTree();

		this.treeManager = treePortlet.getTreeManager();
		this.lastDoubleClickedNode = null;
		this.tabsProneToChange = new HashMap<String, Tab>();

		// two children for vertical divider
		this.dividerPortlet.addChild(treePortlet);
		this.dividerPortlet.addChild(positionFormContainer);

		// buttons
		this.setDefaultButton = new FormPortletButton("Submit").setId("set_defaults");
		this.cancelDefaultButton = new FormPortletButton("Cancel").setId("cancel_defaults");
		this.form.addButton(setDefaultButton);
		this.form.addButton(cancelDefaultButton);
		this.form.setSize(0, 0);

		configureHeaderPortlet();
		loadDataIntoTreePortlet();
		addStylesToTreePortlet();

		// listeners and factories
		this.form.addFormPortletListener(this);
		this.positionForm.addFormPortletListener(this);
		this.fastWebTree.addMenuContextListener(this);
		this.fastWebTree.setContextMenuFactory(this);
		this.fastWebTree.setColumnMenuFactory(this);

		setTreeColumn();
		addChild(this.header, 0, 0);
		addChild(this.dividerPortlet, 0, 1);
		addChild(this.form, 0, 2);
		this.setRowSize(2, 40);
	}

	protected class CustomNode {
		private static final String TYPE_WINDOW = "window";
		private static final String TYPE_TAB = "tab";
		private static final String TYPE_DIVIDER = "divider";
		private static final String STATE_DEFAULT = "default_state";
		private static final String STATE_NO_DEFAULT = "no_default";
		private static final String STATE_DIFFERENT_DEFAULT_AND_CURRENT = "different";

		private String state;
		private String initialState;
		private Object portlet;
		private String portletType;
		private boolean isStateChanged;
		private boolean stateNotChangedAtAll;
		private boolean willBeAffected;
		private boolean flashNeeded;
		private String action;
		private WebTreeNode webTreeNode;
		int formatterId;
		WebTreeNodeFormatter formatter;
		Map<String, Object> initialPositions;
		private boolean isReadonly;

		public CustomNode(Object portlet, WebTreeNode node) {
			this.state = null;
			this.initialState = null;
			this.portlet = portlet;
			this.portletType = null;
			this.isStateChanged = false;
			this.stateNotChangedAtAll = true;
			this.willBeAffected = false;
			this.action = null;
			this.webTreeNode = node;
			this.formatterId = node.getUid();
			this.formatter = new ActionFormatter();
			this.initialPositions = new HashMap<String, Object>();
			this.isReadonly = false;

			// order is important!
			configureAndSetPortletType();
			configureAndSetStates();
		}

		public void configureAndSetPortletType() {
			if (this.portlet instanceof Window)
				setPortletType(TYPE_WINDOW);
			else if (this.portlet instanceof AmiWebPortlet) {
				String panelType = ((AmiWebPortlet) this.portlet).getPanelType();
				if (OH.eq(panelType, "tabs"))
					setPortletType(TYPE_TAB);
				else if (OH.eq(panelType, "divider"))
					setPortletType(TYPE_DIVIDER);
				else {
				}
			}
		}
		public void configureAndSetStates() {
			if (OH.eq(getPortletType(), TYPE_WINDOW)) {
				Window window = (Window) this.portlet;
				setInitialStateForWindow(window);
			} else if (OH.eq(getPortletType(), TYPE_TAB)) {
				TabPortlet tabPortlet = ((AmiWebTabPortlet) this.portlet).getInnerContainer();
				setInitialStateForTab(tabPortlet);
			} else if (OH.eq(getPortletType(), TYPE_DIVIDER)) {
				AmiWebDividerPortlet dividerPorlet = ((AmiWebDividerPortlet) this.portlet);
				setInitialStateForDivider(dividerPorlet);
			}
		}

		public void setInitialStateForWindow(Window window) {
			if (window.hasDefaultLocation()) {
				if (isSameDefaultAndCurrent(window)) {
					setState(STATE_DEFAULT);
					setInitialState(STATE_DEFAULT);
				} else {
					setState(STATE_DIFFERENT_DEFAULT_AND_CURRENT);
					setInitialState(STATE_DIFFERENT_DEFAULT_AND_CURRENT);
				}
			} else {
				initialPositions.put("initialTop", window.getTop());
				initialPositions.put("initialLeft", window.getLeft());
				initialPositions.put("initialWidth", window.getWidth());
				initialPositions.put("initialHeight", window.getHeight());
				setState(STATE_NO_DEFAULT);
				setInitialState(STATE_NO_DEFAULT);
			}
		}

		public void setInitialStateForTab(TabPortlet tabPortlet) {
			if (tabPortlet.hasDefaultTab()) {
				if (tabPortlet.getSelectedTab().getIsDefault()) {
					setState(STATE_DEFAULT);
					setInitialState(STATE_DEFAULT);
				} else {
					setState(STATE_DIFFERENT_DEFAULT_AND_CURRENT);
					setInitialState(STATE_DIFFERENT_DEFAULT_AND_CURRENT);
				}
			} else {
				initialPositions.put("initialTab", tabPortlet.getSelectedTab());
				setState(STATE_NO_DEFAULT);
				setInitialState(STATE_NO_DEFAULT);
			}
		}

		public void setInitialStateForDivider(AmiWebDividerPortlet dividerPortlet) {
			//			if (OH.eq(dividerPortlet.getDefaultOffsetPct(), null)) {
			//				initialPositions.put("initialOffset", dividerPortlet.getOffset());
			//				setState(STATE_NO_DEFAULT);
			//				setInitialState(STATE_NO_DEFAULT);
			//			} else {
			//if (dividerPortlet.getOffset() == dividerPortlet.getDefaultOffsetPct()) {
			if (dividerPortlet.isCurrentOffsetDefault()) {
				setState(STATE_DEFAULT);
				setInitialState(STATE_DEFAULT);
			} else {
				setState(STATE_DIFFERENT_DEFAULT_AND_CURRENT);
				setInitialState(STATE_DIFFERENT_DEFAULT_AND_CURRENT);
			}
			//			}
		}

		public void setStateAndAction(String state, String action) {
			setState(state);
			setAction(action);
		}

		public boolean isDesiredPortlet() {
			return (OH.ne(getPortletType(), null));
		}

		public void flashPortlet() {
			if (this.flashNeeded)
				return;
			this.flashNeeded = true;
			flagPendingAjax();
		}

		public String getState() {
			return this.state;
		}

		public String getInitialState() {
			return this.initialState;
		}

		public Object getPortlet() {
			return this.portlet;
		}

		public String getPortletType() {
			return this.portletType;
		}

		public boolean getIsStateChanged() {
			return this.isStateChanged;
		}

		public boolean getStateNotChangedAtAll() {
			return this.stateNotChangedAtAll;
		}

		public boolean getWillBeAffected() {
			return this.willBeAffected;
		}

		public String getAction() {
			return this.action;
		}

		public WebTreeNode getWebTreeNode() {
			return this.webTreeNode;
		}

		public WebTreeNodeFormatter getFormatter() {
			return this.formatter;
		}

		public void setState(String newState) {
			this.state = newState;
		}

		public void setInitialState(String newInitialState) {
			this.initialState = newInitialState;
		}

		public void setPortlet(Object newPortlet) {
			this.portlet = newPortlet;
		}

		public void setPortletType(String newType) {
			this.portletType = newType;
		}

		public void setIsStateChanged(boolean flag) {
			this.isStateChanged = flag;
		}

		public void setStateNotChangedAtAll(boolean flag) {
			this.stateNotChangedAtAll = flag;
		}

		public void setWillBeAffected(boolean flag) {
			this.willBeAffected = flag;
		}

		public void setAction(String newAction) {
			this.action = newAction;
		}

		public boolean isReadonly() {
			return isReadonly;
		}

		public void setReadonly(boolean isReadonly) {
			this.isReadonly = isReadonly;
		}

	}

	private void configureHeaderPortlet() {
		this.header.setShowSearch(false);
		this.header.setShowBar(true);
		this.header.updateBlurbPortletLayout("Set Defaults", AmiWebSetDefaultsPortlet.HEADER_DESC);
	}

	private void loadDataIntoTreePortlet() {
		this.treeManager.getRoot().setName("AMI DESKTOP");
		for (Window window : getWindows()) {
			try {
				if (window.getPortlet() instanceof AmiWebPortlet) {
					AmiWebPortlet portlet = (AmiWebPortlet) window.getPortlet();
					if (portlet.isTransient())
						continue;
					boolean isReadonly = portlet.isReadonlyLayout();
					WebTreeNode windowNode = this.treeManager.createNode(window.getName(), this.treeManager.getRoot(), true);
					CustomNode custNode = new CustomNode(window, windowNode);
					custNode.setReadonly(isReadonly);
					windowNode.setData(custNode);
					applyIconIfNeeded(windowNode);
					createTree(portlet, AmiWebPortlet.class, windowNode);
				}
			} catch (Exception e) {
				LH.warning(log, logMe(), ": Skipping entry for unnecessary windows", e);
			}
		}
	}
	private <T> void createTree(AmiWebAliasPortlet child2, Class<T> type, WebTreeNode root) {
		try {
			if (child2.isTransient())
				return;
			String amiPanelId = child2.getAmiPanelId();
			boolean isReadonly = child2.isReadonlyLayout();
			WebTreeNode node = this.treeManager.createNode(amiPanelId, root, true);
			CustomNode custNode = new CustomNode(child2, node);
			custNode.setReadonly(isReadonly);
			node.setData(custNode);
			applyIconIfNeeded(node);
			if (child2 instanceof AmiWebAbstractContainerPortlet) {
				AmiWebAbstractContainerPortlet amiWebContainer = (AmiWebAbstractContainerPortlet) child2;
				Collection<AmiWebAliasPortlet> childPortlets = amiWebContainer.getAmiChildren();
				for (AmiWebAliasPortlet child : childPortlets)
					createTree(child, type, node);
			}
		} catch (Exception e) {
			LH.warning(log, logMe(), ": Invalid portlet opened: " + child2, e);
		}
	}

	private void setTreeColumn() {
		FastWebTree fwt = this.treePortlet.getTree();
		FastWebTreeColumn actionColumn;
		FastWebTreeColumn permissionColumn;
		FastWebTreeColumn componentsColumn = fwt.getColumn(0);
		fwt.getColumn(0).setColumnName("Components");
		fwt.addColumnAt(true, permissionColumn = new FastWebTreeColumn(1, new PermissionFormatter(), "Permissions", "Indicates if the panel is read or write only", false), 0);
		fwt.addColumnAt(true, actionColumn = new FastWebTreeColumn(2, new ActionFormatter(), "Action", "Indicates what action to apply", false), 1);
		componentsColumn.setWidth(215);
		permissionColumn.setWidth(80);
		actionColumn.setWidth(200);
	}

	private void addStylesToTreePortlet() {
		this.treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BUTTONS_COLOR, "#007608");
		this.treePortlet.addOption(FastTreePortlet.OPTION_GRIP_COLOR, "_bg=#ffffff");
		this.treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BUTTON_COLOR, "_bg=#ffffff");
		this.treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_ICONS_COLOR, "#007608");
	}

	private class PermissionFormatter implements WebTreeNodeFormatter {
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return 0;
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			CustomNode custNode = (CustomNode) node.getData();
			if (custNode != null)
				if (custNode.isReadonly())
					sink.append("<b>Read</b>");
				else
					sink.append("Write");
		}

		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}

		@Override
		public Object getValue(WebTreeNode node) {
			CustomNode custNode = (CustomNode) node.getData();
			if (custNode == null)
				return null;
			if (custNode.isReadonly())
				return "Read";
			else
				return "Write";
		}
		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}
		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}

	}

	private class ActionFormatter implements WebTreeNodeFormatter {
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return 0;
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			CustomNode custNode = (CustomNode) node.getData();
			if (OH.ne(custNode, null)) {
				if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_WINDOW))
					createDropDown(node, sink);
				else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_TAB))
					createDropDown(node, sink);
				else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_DIVIDER))
					createDropDown(node, sink);
			} else {
			}
		}
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}

		@Override
		public Object getValue(WebTreeNode node) {
			StringBuilder sink = new StringBuilder();
			formatToText(node, sink);
			return sink.toString();
		}
		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}
		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	private void createDropDown(WebTreeNode node, StringBuilder sink) {
		CustomNode custNode = (CustomNode) node.getData();
		String action = custNode.getAction();
		String state = custNode.getState();
		if (OH.ne(action, null)) {
			if (OH.eq(action, ACTION_REVERT))
				createOptionsForKeepDefault(sink, node);
			else if (OH.eq(action, ACTION_SET_CURRENT_TO_DEFAULT))
				createOptionsForSetCurrentToDefault(sink, node);
			else if (OH.eq(action, ACTION_KEEP_DEFAULT))
				createOptionsForKeepDefault(sink, node);
			else if (OH.eq(action, ACTION_NO_DEFAULT))
				createOptionsForNoDefault(sink, node);
		} else { // show dropdown based on state
			if (OH.eq(state, CustomNode.STATE_DEFAULT))
				createOptionsForKeepDefault(sink, node);
			else if (OH.eq(state, CustomNode.STATE_DIFFERENT_DEFAULT_AND_CURRENT))
				createOptionsForKeepDefault(sink, node);
			else if (OH.eq(state, CustomNode.STATE_NO_DEFAULT))
				createOptionsForNoDefault(sink, node);
		}

	}

	private void createOptionsForSetCurrentToDefault(StringBuilder sink, WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();
		if (!custNode.isReadonly()) {
			sink.append("<div class='ami_treepanels ami_treepanels_formatter'><select onchange='var that = this; onActionChanged(that, ");
			sink.append(Caster_String.INSTANCE.cast(node.getUid()));
			sink.append(" , ");
			SH.doubleQuote(this.getPortletId(), sink);
			sink.append(")'>");

			sink.append("<option value = ");
			sink.append(ACTION_SET_CURRENT_TO_DEFAULT);
			sink.append(" selected > Set Current as Default </option>");

			if (OH.ne(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT)) {
				sink.append("<option value = ");
				sink.append(ACTION_KEEP_DEFAULT);
				sink.append("> Keep Default </option>");
			}

			sink.append("<option style='background-color:#F8D7DA; color:#721C24; font-weight:bold;' value = ");
			sink.append(ACTION_NO_DEFAULT);
			sink.append("> No Default </option>");
			sink.append("</select></div>");
		}
	}
	private void createOptionsForKeepDefault(StringBuilder sink, WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();
		if (!custNode.isReadonly()) {
			sink.append("<div class='ami_treepanels ami_treepanels_formatter'><select onchange='var that = this; onActionChanged(that, ");
			sink.append(Caster_String.INSTANCE.cast(node.getUid()));
			sink.append(" , ");
			SH.doubleQuote(this.getPortletId(), sink);
			sink.append(")'>");

			if (OH.ne(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT)) {
				sink.append("<option value = ");
				sink.append(ACTION_KEEP_DEFAULT);
				sink.append(" selected> Keep Default </option>");
			}

			sink.append("<option value = ");
			sink.append(ACTION_SET_CURRENT_TO_DEFAULT);
			sink.append("> Set Current as Default </option>");

			sink.append("<option style='background-color:#F8D7DA; color:#721C24; font-weight:bold;' value = ");
			sink.append(ACTION_NO_DEFAULT);
			sink.append("> No Default </option>");
			sink.append("</select></div>");
		}
	}

	private void createOptionsForNoDefault(StringBuilder sink, WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();
		if (!custNode.isReadonly()) {
			sink.append(
					"<div class='ami_treepanels ami_treepanels_formatter'><select  style='background-color:#F8D7DA; color:#721C24; font-weight:bold;' onchange='var that = this; onActionChanged(that, ");
			sink.append(Caster_String.INSTANCE.cast(node.getUid()));
			sink.append(" , ");
			SH.doubleQuote(this.getPortletId(), sink);
			sink.append(")'>");

			if (OH.ne(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT)) {
				sink.append("<option value = ");
				sink.append(ACTION_KEEP_DEFAULT);
				sink.append("> Keep Default </option>");
			}

			sink.append("<option value = ");
			sink.append(ACTION_SET_CURRENT_TO_DEFAULT);
			sink.append("> Set Current as Default </option>");

			sink.append("<option style='background-color:#F8D7DA; color:#721C24; font-weight:bold;' value = ");
			sink.append(ACTION_NO_DEFAULT);
			sink.append(" selected> No Default </option>");
			sink.append("</select></div>");
		}
	}

	private void applyIconIfNeeded(WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();
		if (custNode.isDesiredPortlet()) {
			if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_WINDOW))
				node.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_PLACE_WINDOW + ")");
			else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_TAB))
				node.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_PLACE_TAB + ")");
			else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_DIVIDER))
				node.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_SPLIT_VERT + ")");
			else
				LH.warning(log, logMe(), ": Could not set icon for portlet: ", node);
		} else {
		}
	}
	private Collection<Window> getWindows() {
		return this.desktop.getWindows();
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (OH.eq(button.getId(), "set_defaults")) {
			setDefaults();
			this.close();
		} else if (OH.eq(button.getId(), "cancel_defaults")) {
			this.close();
		}
	}

	private void setDefaults() {
		Iterable<WebTreeNode> allChildren = treeManager.getRoot().getAllChildren();
		for (WebTreeNode childNode : allChildren) {
			CustomNode custNode = (CustomNode) childNode.getData();
			if (custNode.getWillBeAffected() && custNode.isDesiredPortlet()) {
				if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_WINDOW))
					setDefaultForWindow(childNode);
				else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_TAB))
					setDefaultForTab(childNode);
				else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_DIVIDER))
					setDefaultForDivider(childNode);
				else
					LH.warning(log, logMe(), ": Illegal portlet type: could not set defaults for " + custNode.getPortletType());
			}
		}
	}

	private void setDefaultForWindow(WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();
		Window window = (Window) custNode.getPortlet();
		String action = custNode.getAction();
		if (OH.eq(action, ACTION_KEEP_DEFAULT)) {
			// nothing to do.
		} else if (OH.eq(action, ACTION_SET_CURRENT_TO_DEFAULT)) {
			window.setDefaultLocationToCurrent();
			window.setDefaultStateToCurrent();
		} else if (OH.eq(action, ACTION_NO_DEFAULT)) {
			window.clearDefaultLocation();
			window.clearDefaultState();
		} else {
			LH.warning(log, logMe(), ": Invalid Action for window: could not set default: ", action, " for ", node);
		}
	}

	private void setDefaultForDivider(WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();
		AmiWebDividerPortlet dividerPortlet = (AmiWebDividerPortlet) custNode.getPortlet();
		String action = custNode.getAction();
		if (OH.eq(action, ACTION_KEEP_DEFAULT)) {
			// nothing to do.
		} else if (OH.eq(action, ACTION_SET_CURRENT_TO_DEFAULT)) {
			dividerPortlet.setDefaultOffsetPctToCurrent();
		} else if (OH.eq(action, ACTION_NO_DEFAULT)) {
			dividerPortlet.resetOffsetPctToDefault();
		} else {
			LH.warning(log, logMe(), ": Invalid Action for divider: could not set default ", action, " for ", node);
		}

	}

	private void setDefaultForTab(WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();
		TabPortlet tabPortlet = ((AmiWebTabPortlet) custNode.getPortlet()).getInnerContainer();
		String action = custNode.getAction();
		if (OH.eq(action, ACTION_KEEP_DEFAULT)) {
			// nothing to do.
		} else if (OH.eq(action, ACTION_SET_CURRENT_TO_DEFAULT)) {
			Tab s = tabPortlet.getSelectedTab();
			if (s != null)
				s.setIsDefault(true);
		} else if (OH.eq(action, ACTION_NO_DEFAULT)) {
			tabPortlet.clearDefaultTab();
		} else {
			LH.warning(log, logMe(), ": Invalid Action for tab: could not set default: ", action, " for ", node);
		}
	}

	private String logMe() {
		return getManager().getUserName();
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		try {
			WebTreeNode clickedNode = node;
			CustomNode custNode = (CustomNode) clickedNode.getData();
			if (custNode == null)
				return;

			if (OH.ne(getLastClickedNode(), clickedNode) && this.tabsProneToChange.size() > 0) {
				for (String portletId : this.tabsProneToChange.keySet()) {
					TabPortlet tp = (TabPortlet) getManager().getPortlet(portletId);
					tp.setActiveTab(this.tabsProneToChange.get(portletId).getPortlet());
				}
				this.tabsProneToChange.clear();
			}

			if (custNode.isDesiredPortlet()) {
				this.positionForm.clearFields();
				if (OH.ne(getLastClickedNode(), clickedNode)) {
					if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_WINDOW)) {
						Window window = (Window) custNode.getPortlet();
						window.bringToFront();
						this.nodeCurrentlyInPositionForm = clickedNode;
						showWindowForm(custNode);
					} else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_DIVIDER)) {
						AmiWebDividerPortlet awdp = (AmiWebDividerPortlet) custNode.getPortlet();
						DividerPortlet dp = awdp.getInnerContainer();
						// save tabs that might be changed before bringing the desired portlet to front.
						saveChangedTabs(clickedNode.getParent());
						PortletHelper.ensureVisible(dp);
						this.nodeCurrentlyInPositionForm = clickedNode;
						showDividerForm(custNode);
					} else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_TAB)) {
						AmiWebTabPortlet awtp = (AmiWebTabPortlet) custNode.getPortlet();
						TabPortlet tp = awtp.getInnerContainer();
						saveChangedTabs(clickedNode.getParent());
						PortletHelper.ensureVisible(tp);
						this.nodeCurrentlyInPositionForm = clickedNode;
						showTabForm(custNode);
					}
				} else { // show the form regardless of how many times user clicked on the same node.
					if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_WINDOW)) {
						this.nodeCurrentlyInPositionForm = clickedNode;
						showWindowForm(custNode);
					} else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_DIVIDER)) {
						this.nodeCurrentlyInPositionForm = clickedNode;
						showDividerForm(custNode);
					} else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_TAB)) {
						this.nodeCurrentlyInPositionForm = clickedNode;
						showTabForm(custNode);
					}
				}
				if (custNode.isReadonly())
					disableForm();
			} else { // not a desired portlet
				if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_WINDOW))
					showWindowForm(custNode);
				else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_DIVIDER))
					showDividerForm(custNode);
				else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_TAB))
					showTabForm(custNode);
			}
			setLastClickedNode(clickedNode);
		} catch (Exception e) {
			LH.warning(log, logMe(), ": Exception on node click, NODE: " + node + " : ", e);
		}
	}
	public void disableForm() {
		for (FormPortletField<?> field : this.positionForm.getFormFields())
			field.setDisabled(true);
	}
	@Override
	public void handleCallback(String callBack, Map<String, String> attributes) {
		if (OH.eq(callBack, "onActionMenuChanged")) {
			String action = attributes.get("action");
			WebTreeNode node = this.treeManager.getTreeNode(Caster_Integer.PRIMITIVE.cast(attributes.get("nodeUid")));
			CustomNode custNode = (CustomNode) node.getData();
			if (OH.eq(action, ACTION_KEEP_DEFAULT))
				updateStateAndAction(custNode, ACTION_KEEP_DEFAULT);
			else if (OH.eq(action, ACTION_SET_CURRENT_TO_DEFAULT)) {
				updateStateAndAction(custNode, ACTION_SET_CURRENT_TO_DEFAULT);
			} else if (OH.eq(action, ACTION_NO_DEFAULT))
				updateStateAndAction(custNode, ACTION_NO_DEFAULT);
		}
	}

	private void moveBackToDefault(String action, CustomNode custNode) {
		if (OH.eq(action, ACTION_KEEP_DEFAULT)) {
			if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_DIVIDER)) {
				AmiWebDividerPortlet dividerPortlet = ((AmiWebDividerPortlet) custNode.getPortlet());
				dividerPortlet.resetOffsetPctToDefault();
				// refresh the form if it is currently in view
				if (this.positionForm.hasField("revert_to_default_divider"))
					showDividerForm(custNode);

			} else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_TAB)) {
				TabPortlet tabPortlet = ((AmiWebTabPortlet) custNode.getPortlet()).getInnerContainer();
				tabPortlet.setActiveTab(tabPortlet.getDefaultTab().getPortlet());
				if (this.positionForm.hasField("revert_to_default_tab"))
					showTabForm(custNode);
			}
		}
	}

	private void moveBackToInitial(CustomNode custNode) {
		if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_WINDOW)) {
			// not anything for now.
		} else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_DIVIDER)) {
			DividerPortlet dividerPortlet = ((AmiWebDividerPortlet) custNode.getPortlet()).getInnerContainer();
			double initialOffset = (Double) custNode.initialPositions.get("initialOffset");
			dividerPortlet.setOffset(initialOffset);
			showDividerForm(custNode);
		} else if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_TAB)) {
			TabPortlet tabPortlet = ((AmiWebTabPortlet) custNode.getPortlet()).getInnerContainer();
			Tab initialTab = (Tab) custNode.initialPositions.get("initialTab");
			tabPortlet.setActiveTab(initialTab.getPortlet());
			showTabForm(custNode);
		}
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		return false;
	}

	private void applyChangesToChildren(WebTreeNode node) {
		String actionToApplyToAllChildren = ((CustomNode) node.getData()).getAction();
		for (WebTreeNode childNode : node.getAllChildren()) {
			CustomNode custNode = (CustomNode) childNode.getData();
			if (custNode.isDesiredPortlet()) {
				if (OH.eq(actionToApplyToAllChildren, ACTION_REVERT))
					updateStateAndAction(custNode, ACTION_REVERT);
				else if (OH.eq(actionToApplyToAllChildren, ACTION_SET_CURRENT_TO_DEFAULT)) {
					updateStateAndAction(custNode, ACTION_SET_CURRENT_TO_DEFAULT);
				} else if (OH.eq(actionToApplyToAllChildren, ACTION_NO_DEFAULT))
					updateStateAndAction(custNode, ACTION_NO_DEFAULT);
				else if (OH.eq(actionToApplyToAllChildren, ACTION_KEEP_DEFAULT)) {
					if (OH.ne(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT)) {
						moveBackToDefault(ACTION_KEEP_DEFAULT, custNode);
						updateStateAndAction(custNode, ACTION_KEEP_DEFAULT);
					}
				}
			}
		}
	}
	private void revertChangesToChildren(WebTreeNode node) {
		for (WebTreeNode childNode : node.getAllChildren()) {
			CustomNode custNode = (CustomNode) childNode.getData();
			if (custNode.isDesiredPortlet())
				updateStateAndAction(custNode, ACTION_REVERT);
		}
	}

	private void highlightNode(WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();
		if (custNode.isDesiredPortlet())
			node.setCssClass("ami_set_default");
	}

	private void deHighlightNode(WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();
		if (custNode.isDesiredPortlet())
			node.setCssClass("");
	}

	private void showTabForm(CustomNode custNode) {
		this.positionForm.clearFields();
		TabPortlet tabPortlet = ((AmiWebTabPortlet) custNode.getPortlet()).getInnerContainer();
		Tab selectedTab = tabPortlet.getSelectedTab();
		Tab defaultTab = tabPortlet.getDefaultTab();
		FormPortletTitleField title = new FormPortletTitleField("");
		title.setCssStyle("style.marginLeft=-20%");
		FormPortletToggleButtonsField<Object> defaultTabToggle = new FormPortletToggleButtonsField<Object>(Object.class, "Default Tab: ");
		FormPortletSelectField<Object> tabsSelectMenu = new FormPortletSelectField<Object>(Object.class, "Current Tab: ");
		FormPortletButtonField revertToDefaultButton = (FormPortletButtonField) new FormPortletButtonField("").setValue("Revert to Default");

		for (Tab tab : tabPortlet.getTabs()) {
			if (OH.eq(tab, defaultTab))
				tabsSelectMenu.addOption(tab, tab.getTitle(), "disabled=true");
			else
				tabsSelectMenu.addOption(tab, tab.getTitle());
		}
		tabsSelectMenu.setValue(selectedTab);

		defaultTabToggle.setDisabled(true);
		tabsSelectMenu.setDisabled(false);
		tabsSelectMenu.setId("tabs_select_menu_changed");
		revertToDefaultButton.setId("revert_to_default_tab");

		Map<String, Object> correlationDataForRevertButton = new HashMap<String, Object>();
		correlationDataForRevertButton.put("tabPortlet", tabPortlet);
		correlationDataForRevertButton.put("defaultTabToggle", defaultTabToggle);
		correlationDataForRevertButton.put("selectedTab", selectedTab);
		correlationDataForRevertButton.put("tabsSelectMenu", tabsSelectMenu);
		correlationDataForRevertButton.put("customNode", custNode);
		revertToDefaultButton.setCorrelationData(correlationDataForRevertButton);

		Map<String, Object> correlationDataForTabsToggle = new HashMap<String, Object>();
		correlationDataForTabsToggle.put("tabPortlet", tabPortlet);
		correlationDataForTabsToggle.put("customNode", custNode);
		correlationDataForTabsToggle.put("tabsSelectMenu", tabsSelectMenu);
		tabsSelectMenu.setCorrelationData(correlationDataForTabsToggle);

		if (tabPortlet.hasDefaultTab()) {
			defaultTabToggle.addOption(defaultTab, defaultTab.getTitle());
			this.positionForm.addField(defaultTabToggle);
			this.positionForm.addField(tabsSelectMenu);
			this.positionForm.addField(revertToDefaultButton);
		} else {
			title.setValue("Default Tab: <i>no default</i> defined");
			this.positionForm.addField(title);
			this.positionForm.addField(tabsSelectMenu);
		}
	}

	private void showWindowForm(CustomNode custNode) {
		this.positionForm.clearFields();
		Window window = (Window) custNode.getPortlet();
		FormPortletTitleField title = new FormPortletTitleField("").setId("window_form");
		FormPortletTitleField title2 = new FormPortletTitleField("");
		title.setCssStyle("style.marginLeft=-20%");
		title2.setCssStyle("style.marginLeft=-20%");
		FormPortletTextField top = new FormPortletTextField("Top ").setValue(SH.toString(window.getTop()));
		FormPortletTextField left = new FormPortletTextField("Left ").setValue(SH.toString(window.getLeft()));
		FormPortletTextField width = new FormPortletTextField("Width ").setValue(SH.toString(window.getWidth()));
		FormPortletTextField height = new FormPortletTextField("Height ").setValue(SH.toString(window.getHeight()));

		top.setDisabled(true);
		left.setDisabled(true);
		width.setDisabled(true);
		height.setDisabled(true);

		FormPortletTitleField divider = new FormPortletTitleField("-------------------------------------------------");

		FormPortletTextField top1 = new FormPortletTextField("Default Top ").setValue(SH.toString(window.getDefaultTop()));
		FormPortletTextField left1 = new FormPortletTextField("Default Left ").setValue(SH.toString(window.getDefaultLeft()));
		FormPortletTextField width1 = new FormPortletTextField("Default Width ").setValue(SH.toString(window.getDefaultWidth()));
		FormPortletTextField height1 = new FormPortletTextField("Default Height ").setValue(SH.toString(window.getDefaultHeight()));

		top1.setDisabled(true);
		left1.setDisabled(true);
		width1.setDisabled(true);
		height1.setDisabled(true);

		if (window.hasDefaultLocation()) {
			if (isSameDefaultAndCurrent(window)) {
				title.setValue("Default and current positions for window \"" + window.getName() + "\" are the same.");
				this.positionForm.addField(title);
				this.positionForm.addField(top1);
				this.positionForm.addField(left1);
				this.positionForm.addField(width1);
				this.positionForm.addField(height1);
			} else {
				title.setValue("Default position for window: " + window.getName());
				this.positionForm.addField(title);
				this.positionForm.addField(top1);
				this.positionForm.addField(left1);
				this.positionForm.addField(width1);
				this.positionForm.addField(height1);
				this.positionForm.addField(divider);
				title2.setValue("Current position for window: " + window.getName());
				this.positionForm.addField(title2);
				this.positionForm.addField(top);
				this.positionForm.addField(left);
				this.positionForm.addField(width);
				this.positionForm.addField(height);
			}

		} else {
			title.setValue("Default Positions: No default defined for window \"" + window.getName() + "\"");
			title2.setValue("<br>Current position for window: " + window.getName());
			this.positionForm.addField(title);
			this.positionForm.addField(title2);
			this.positionForm.addField(top);
			this.positionForm.addField(left);
			this.positionForm.addField(width);
			this.positionForm.addField(height);
		}

	}

	private boolean isSameDefaultAndCurrent(Window window) {
		return (OH.eq(window.getTop(), window.getDefaultTop()) && OH.eq(window.getLeft(), window.getDefaultLeft()) && OH.eq(window.getWidth(), window.getDefaultWidth())
				&& OH.eq(window.getHeight(), window.getDefaultHeight()));
	}

	//TODO: This needs to be redone
	private void showDividerForm(CustomNode custNode) {
		this.positionForm.clearFields();
		AmiWebDividerPortlet portlet = (AmiWebDividerPortlet) custNode.getPortlet();
		DividerPortlet dividerPortlet = portlet.getInnerContainer();
		//		Double defaultOffset = portlet.getDefaultOffsetPct();
		Double currentOffset = dividerPortlet.getOffset();
		FormPortletTitleField title = new FormPortletTitleField("");
		title.setCssStyle("style.marginLeft=-20%");
		FormPortletNumericRangeField defaultSlider = new FormPortletNumericRangeField("Default Offset (%)", 0, 100, 0);
		FormPortletNumericRangeField currentSlider = new FormPortletNumericRangeField("Current Offset (%)", 0, 100, 0);
		FormPortletButtonField revertToDefaultButton = (FormPortletButtonField) new FormPortletButtonField("").setValue("Revert to Default");
		currentSlider.setId("current_slider_changed");
		revertToDefaultButton.setId("revert_to_default_divider");

		Map<String, Object> correlationDataForRevertButton = new HashMap<String, Object>();
		correlationDataForRevertButton.put("dividerPortlet", dividerPortlet);
		correlationDataForRevertButton.put("defaultSlider", defaultSlider);
		correlationDataForRevertButton.put("currentSlider", currentSlider);
		correlationDataForRevertButton.put("customNode", custNode);
		correlationDataForRevertButton.put("title", title);
		revertToDefaultButton.setCorrelationData(correlationDataForRevertButton);

		Map<String, Object> correlationDataForCurrentSlider = new HashMap<String, Object>();
		correlationDataForCurrentSlider.put("dividerPortlet", dividerPortlet);
		correlationDataForCurrentSlider.put("customNode", custNode);
		correlationDataForCurrentSlider.put("currentSlider", currentSlider);
		correlationDataForCurrentSlider.put("title", title);
		currentSlider.setCorrelationData(correlationDataForCurrentSlider);

		defaultSlider.setDisabled(true);
		currentSlider.setDisabled(false);

		//		if (OH.ne(defaultOffset, null)) {
		//			this.positionForm.addField(title);
		//			defaultSlider.setValue(defaultOffset * 100);
		//			this.positionForm.addField(defaultSlider);
		//			currentSlider.setValue(currentOffset * 100);
		//			this.positionForm.addField(currentSlider);
		//			this.positionForm.addField(revertToDefaultButton);
		//		} else {
		//			title.setValue("No default defined for the divider");
		//			this.positionForm.addField(title);
		//			currentSlider.setValue(currentOffset * 100);
		//			this.positionForm.addField(defaultSlider);
		//			this.positionForm.addField(currentSlider);
		//		}

	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (OH.eq(field.getId(), "defaultTabToggle"))
			((Tab) field.getValue()).setIsDefault(true);
		else if (OH.eq(field.getId(), "current_slider_changed")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> correlationData = (Map<String, Object>) field.getCorrelationData();
			FormPortletNumericRangeField currentSlider = (FormPortletNumericRangeField) correlationData.get("currentSlider");
			DividerPortlet dp = (DividerPortlet) correlationData.get("dividerPortlet");
			CustomNode custNode = (CustomNode) correlationData.get("customNode");
			dp.setOffset((Double) currentSlider.getValue() / 100);
			updateStateAndAction(custNode, ACTION_SET_CURRENT_TO_DEFAULT);
		} else if (OH.eq(field.getId(), "revert_to_default_divider")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> correlationData = (Map<String, Object>) field.getCorrelationData();
			CustomNode custNode = (CustomNode) correlationData.get("customNode");
			DividerPortlet dp = (DividerPortlet) correlationData.get("dividerPortlet");
			FormPortletNumericRangeField currentSlider = (FormPortletNumericRangeField) correlationData.get("currentSlider");
			FormPortletNumericRangeField defaultSlider = (FormPortletNumericRangeField) correlationData.get("defaultSlider");
			currentSlider.setValue(defaultSlider.getValue());
			AmiWebDividerPortlet adp = (AmiWebDividerPortlet) custNode.getPortlet();
			adp.resetOffsetPctToDefault();
			updateStateAndAction(custNode, ACTION_REVERT);
		} else if (OH.eq(field.getId(), "tabs_select_menu_changed")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> correlationData = (Map<String, Object>) field.getCorrelationData();
			TabPortlet tabPortlet = (TabPortlet) correlationData.get("tabPortlet");
			CustomNode custNode = (CustomNode) correlationData.get("customNode");
			@SuppressWarnings("unchecked")
			FormPortletSelectField<Object> tabsSelectMenu = (FormPortletSelectField<Object>) correlationData.get("tabsSelectMenu");
			Tab activeTab = (Tab) tabsSelectMenu.getValue();
			tabPortlet.setActiveTab(activeTab.getPortlet());
			custNode.setStateAndAction(CustomNode.STATE_DEFAULT, ACTION_SET_CURRENT_TO_DEFAULT); // need to do this.
			custNode.setIsStateChanged(true);
			custNode.setWillBeAffected(true);
			deHighlightNode(custNode.getWebTreeNode());
			highlightNode(custNode.getWebTreeNode());
		} else if (OH.eq(field.getId(), "revert_to_default_tab")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> correlationData = (Map<String, Object>) field.getCorrelationData();
			TabPortlet tabPortlet = (TabPortlet) correlationData.get("tabPortlet");
			@SuppressWarnings("unchecked")
			FormPortletSelectField<Object> tabsSelectMenu = (FormPortletSelectField<Object>) correlationData.get("tabsSelectMenu");
			CustomNode custNode = (CustomNode) correlationData.get("customNode");
			tabsSelectMenu.setValue(tabPortlet.getDefaultTab());
			tabPortlet.setActiveTab(tabPortlet.getDefaultTab().getPortlet());
			updateStateAndAction(custNode, ACTION_REVERT);
		}

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	private WebTreeNode getLastClickedNode() {
		return this.lastDoubleClickedNode;
	}

	private void setLastClickedNode(WebTreeNode node) {
		this.lastDoubleClickedNode = node;
	}

	private void saveChangedTabs(WebTreeNode node) {
		CustomNode custNode = (CustomNode) node.getData();

		if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_WINDOW) || OH.eq(this.treeManager.getRoot(), node))
			return;
		if (OH.eq(custNode.getPortletType(), CustomNode.TYPE_TAB)) {
			AmiWebTabPortlet awtp = (AmiWebTabPortlet) custNode.getPortlet();
			TabPortlet tp = awtp.getInnerContainer();
			this.tabsProneToChange.put(tp.getPortletId(), tp.getSelectedTab());
			saveChangedTabs(node.getParent());
		} else
			saveChangedTabs(node.getParent());
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		WebTreeNode node = tree.getSelected().get(0);
		handleMenuCallback(action, node);
	}

	private void handleMenuCallback(String action, WebTreeNode node) {
		if (OH.eq(node, this.treeManager.getRoot())) {
			if (OH.eq(action, "current_as_default_all_root")) { // handlers for the Root
				actionCurrentAsDefaultFromRoot(node);
			} else if (OH.eq(action, "keep_default_all_root"))
				if (isAllNoDefault(node))
					getManager().showAlert("Could not perform action: All components do not have any defaults defined");
				else
					actionKeepDefaultFromRoot(node);
			else if (OH.eq(action, "no_default_all_root")) {
				actionNoDefaultFromRoot(node);
			} else if (OH.eq(action, "revert_all_root"))
				actionRevertFromRoot(node);
		} else { // handlers for non-root nodes
			CustomNode custNode = (CustomNode) node.getData();
			if (OH.eq(action, "no_default_only"))
				updateStateAndAction(custNode, ACTION_NO_DEFAULT);
			else if (OH.eq(action, "no_default_apply_all"))
				actionNoDefaultApplyAll(custNode);
			else if (OH.eq(action, "current_as_default_only")) {
				updateStateAndAction(custNode, ACTION_SET_CURRENT_TO_DEFAULT);
			} else if (OH.eq(action, "current_as_default_apply_all")) {
				actionCurrentAsDefaultApplyAll(custNode);
			} else if (OH.eq(action, "revert_initial_only"))
				updateStateAndAction(custNode, ACTION_REVERT);
			else if (OH.eq(action, "revert_initial_apply_all"))
				actionRevertToInitialApplyAll(custNode);
			else if (OH.eq(action, "keep_default_only"))
				updateStateAndAction(custNode, ACTION_KEEP_DEFAULT);
			else if (OH.eq(action, "keep_default_apply_all"))
				actionKeepDefaultApplyAll(custNode);
		}
	}

	private void updateStateAndAction(CustomNode custNode, String action) {
		if (OH.eq(action, ACTION_SET_CURRENT_TO_DEFAULT)) {
			custNode.setStateAndAction(CustomNode.STATE_DEFAULT, ACTION_SET_CURRENT_TO_DEFAULT);
			custNode.setIsStateChanged(true);
			custNode.setWillBeAffected(true);
			deHighlightNode(custNode.getWebTreeNode());
			highlightNode(custNode.getWebTreeNode());
		} else if (OH.eq(action, ACTION_KEEP_DEFAULT)) {
			if (OH.ne(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT)) {
				custNode.setStateAndAction(CustomNode.STATE_DEFAULT, ACTION_KEEP_DEFAULT);
				moveBackToDefault(action, custNode);
			} else
				custNode.setStateAndAction(CustomNode.STATE_DEFAULT, ACTION_KEEP_DEFAULT);
			updateFrontEnd(custNode);
		} else if (OH.eq(action, ACTION_NO_DEFAULT)) {
			custNode.setStateAndAction(CustomNode.STATE_NO_DEFAULT, ACTION_NO_DEFAULT);
			updateFrontEnd(custNode);
		} else if (OH.eq(action, ACTION_REVERT)) {
			if (OH.eq(custNode.getInitialState(), CustomNode.STATE_DEFAULT))
				moveBackToDefault(ACTION_KEEP_DEFAULT, custNode);
			else if (OH.eq(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT))
				moveBackToInitial(custNode);
			custNode.setStateAndAction(custNode.getInitialState(), null);
			updateFrontEnd(custNode);
		}
	}

	private void updateFrontEnd(CustomNode custNode) {
		custNode.setStateNotChangedAtAll(false);
		if (OH.ne(custNode.getAction(), null)) {
			if (OH.eq(custNode.getState(), custNode.getInitialState())) {
				custNode.setIsStateChanged(false);
				custNode.setWillBeAffected(false);
			} else {
				custNode.setIsStateChanged(true);
				custNode.setWillBeAffected(true);
			}
			deHighlightNode(custNode.getWebTreeNode());
			highlightNode(custNode.getWebTreeNode());
		} else { // always dehighlight on revert
			highlightNode(custNode.getWebTreeNode());
			deHighlightNode(custNode.getWebTreeNode());
		}
	}

	private void actionKeepDefaultFromRoot(WebTreeNode rootNode) {
		for (WebTreeNode node : rootNode.getAllChildren()) {
			CustomNode custNode = (CustomNode) node.getData();
			if (custNode.isDesiredPortlet()) {
				if (OH.ne(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT)) {
					moveBackToDefault(ACTION_KEEP_DEFAULT, custNode);
					updateStateAndAction(custNode, ACTION_KEEP_DEFAULT);
				}
			}
		}
	}

	private boolean isAllNoDefault(WebTreeNode rootNode) {
		for (WebTreeNode node : rootNode.getAllChildren()) {
			CustomNode custNode = (CustomNode) node.getData();
			if (custNode.isDesiredPortlet()) {
				if (OH.ne(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT))
					return false;
			}
		}
		return true;
	}
	private void actionCurrentAsDefaultFromRoot(WebTreeNode rootNode) {
		for (WebTreeNode node : rootNode.getAllChildren()) {
			CustomNode custNode = (CustomNode) node.getData();
			if (custNode.isDesiredPortlet())
				updateStateAndAction(custNode, ACTION_SET_CURRENT_TO_DEFAULT);
		}

	}

	private void actionNoDefaultFromRoot(WebTreeNode rootNode) {
		for (WebTreeNode node : rootNode.getAllChildren()) {
			CustomNode custNode = (CustomNode) node.getData();
			if (custNode.isDesiredPortlet())
				updateStateAndAction(custNode, ACTION_NO_DEFAULT);
		}
	}

	private void actionRevertFromRoot(WebTreeNode rootNode) {
		for (WebTreeNode node : rootNode.getAllChildren()) {
			CustomNode custNode = (CustomNode) node.getData();
			if (custNode.isDesiredPortlet())
				updateStateAndAction(custNode, ACTION_REVERT);
		}
	}

	private void actionKeepDefaultApplyAll(CustomNode custNode) {
		updateStateAndAction(custNode, ACTION_KEEP_DEFAULT);
		applyChangesToChildren(custNode.getWebTreeNode());
	}

	private void actionNoDefaultApplyAll(CustomNode custNode) {
		updateStateAndAction(custNode, ACTION_NO_DEFAULT);
		applyChangesToChildren(custNode.getWebTreeNode());
	}

	private void actionCurrentAsDefaultApplyAll(CustomNode custNode) {
		custNode.setAction(ACTION_SET_CURRENT_TO_DEFAULT);
		updateStateAndAction(custNode, ACTION_SET_CURRENT_TO_DEFAULT);
		applyChangesToChildren(custNode.getWebTreeNode());
	}

	private void actionRevertToInitialApplyAll(CustomNode custNode) {
		updateStateAndAction(custNode, ACTION_REVERT);
		revertChangesToChildren(custNode.getWebTreeNode());
	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		if (selected.isEmpty())
			return null;
		BasicWebMenu menu = new BasicWebMenu();
		WebTreeNode nodeRightClicked = selected.get(0);
		CustomNode custNode = (CustomNode) nodeRightClicked.getData();

		if (custNode != null && custNode.isReadonly())
			return null;

		// menu for root node
		if (OH.eq(this.treeManager.getRoot(), nodeRightClicked)) {
			WebMenuLink keepDefaultForAll = new BasicWebMenuLink("Keep default for all children", true, "keep_default_all_root");
			WebMenuLink setCurrentAsDefaultForAll = new BasicWebMenuLink("Set current as default for all children", true, "current_as_default_all_root");
			WebMenuLink noDefaultForAll = new BasicWebMenuLink("No default for all children", true, "no_default_all_root");
			WebMenuLink revertForAll = new BasicWebMenuLink("Revert to initial for all children", true, "revert_all_root");
			menu.add(keepDefaultForAll);
			menu.add(setCurrentAsDefaultForAll);
			menu.add(noDefaultForAll);
			menu.add(revertForAll);
		} else if (custNode.isDesiredPortlet()) { // menu for non-root node

			WebMenuLink keepDefault = OH.eq(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT) ? new BasicWebMenuLink("Keep default", false, "keep_default_only")
					: new BasicWebMenuLink("Keep default", true, "keep_default_only");
			WebMenuLink keepDefaultWithChildren = OH.eq(custNode.getInitialState(), CustomNode.STATE_NO_DEFAULT)
					? new BasicWebMenuLink("Keep default and apply to all children", false, "keep_default_apply_all")
					: new BasicWebMenuLink("Keep default and apply to all children", true, "keep_default_apply_all");
			WebMenuLink noDefault = new BasicWebMenuLink("No default", true, "no_default_only");
			WebMenuLink noDefaultWithChildren = new BasicWebMenuLink("No default and apply to all children", true, "no_default_apply_all");
			WebMenuLink currentAsDefault = new BasicWebMenuLink("Set current as default", true, "current_as_default_only");
			WebMenuLink currentAsDefaultWithChildren = new BasicWebMenuLink("Set current as default and apply to all children", true, "current_as_default_apply_all");
			WebMenuLink revertToInitial = new BasicWebMenuLink("Revert to initial", true, "revert_initial_only");
			WebMenuLink revertToInitialWithChildren = new BasicWebMenuLink("Revert to initial and apply to all children", true, "revert_initial_apply_all");

			menu.add(keepDefault);
			menu.add(keepDefaultWithChildren);
			menu.add(currentAsDefault);
			menu.add(currentAsDefaultWithChildren);
			menu.add(noDefault);
			menu.add(noDefaultWithChildren);
			menu.add(revertToInitial);
			menu.add(revertToInitialWithChildren);
		}
		return menu;
	}
	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}

	@Override
	public WebMenu createColumnMenu(FastWebTree tree, FastWebTreeColumn column, WebMenu defaultMenu) {
		return new BasicWebMenu();
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

}
