package com.vortex.ssoweb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletBuilder;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.BlankPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.agg.BooleanAggregator;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoResponse;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupRequest;

public class SsoEditEntitlementsDialogPortlet extends GridPortlet implements WebTreeContextMenuListener, FormPortletListener {

	private SsoWebGroup group;
	private SsoUser user;
	private FastTreePortlet treePortlet;

	private Map<String, Boolean> isEnabled = new HashMap<String, Boolean>();
	private FormPortlet buttonsPortlet;
	private FormPortletButton saveButton;
	private ObjectToJsonConverter converter;
	private Map<String, Boolean> enabledPortlets;
	private boolean enabledByDefault = true;
	private SsoService service;

	public SsoEditEntitlementsDialogPortlet(PortletConfig config, SsoWebGroup group) {
		super(config);
		this.service = (SsoService) getManager().getService(SsoService.ID);
		this.group = group;
		this.user = (SsoUser) group.getPeer();
		this.converter = new ObjectToJsonConverter();
		SsoGroupAttribute attribute = group.getGroupAttributes().get("portlets_entitled");
		this.enabledPortlets = new HashMap<String, Boolean>();
		if (attribute != null) {
			//			enabledPortlets.putAll((Map) converter.stringToObject(attribute.getValue()));
			enabledPortlets.putAll((Map) attribute.getValue());
			enabledByDefault = CH.getOr(enabledPortlets, "DEFAULT", true);

		}

		treePortlet = new FastTreePortlet(generateConfig());
		buttonsPortlet = new FormPortlet(generateConfig());
		saveButton = buttonsPortlet.addButton(new FormPortletButton("Save"));
		addChild(new HtmlPortlet(generateConfig(), "Enable / Disable portlets for <B>" + user.getUserName() + "</B>"), 0, 0);
		addChild(treePortlet, 0, 1);
		addChild(buttonsPortlet, 0, 2);
		setRowSize(0, 20);
		setRowSize(2, 35);
		Set<String> builderIds = getManager().getBuilders();
		WebTreeManager tm = treePortlet.getTreeManager();
		treePortlet.getTree().setRowHeight(18);
		treePortlet.getTree().setRootLevelVisible(false);
		treePortlet.getTree().addMenuContextListener(this);
		for (String builderId : builderIds) {
			PortletBuilder builder = getManager().getPortletBuilder(builderId);
			if (builder instanceof BlankPortlet.Builder)
				continue;
			if (builder.getIsUserCreatable()) {
				builder.getPath();
				addNode(tm.getRoot(), 0, builder);
			}
		}
		WebTreeNode dfltNode = tm.createNode("* Unknown Portlets *", tm.getRoot(), false).setData("DEFAULT");
		setEnabled(dfltNode, enabledByDefault);
		initStyle(tm.getRoot());
		buttonsPortlet.addFormPortletListener(this);
	}

	private void addNode(WebTreeNode parent, int depth, PortletBuilder builder) {
		WebTreeManager tm = treePortlet.getTree().getTreeManager();
		if (AH.length(builder.getPath()) <= depth) {
			WebTreeNode node = tm.createNode(formatText(builder.getPortletBuilderName()), parent, false).setData(builder.getPortletBuilderId()).setIcon(builder.getIcon());
			Boolean enabled = this.enabledPortlets.get(builder.getPortletBuilderId());
			if (enabled == null)
				setEnabled(node, enabledByDefault);
			else
				setEnabled(node, enabled);
		} else {
			final String name = formatText(builder.getPath()[depth]);
			for (final WebTreeNode node : parent.getChildren()) {
				if (OH.eq(name, node.getName())) {
					addNode(node, depth + 1, builder);
					return;
				}
			}
			WebTreeNode node = tm.createNode(name, parent, false);
			addNode(node, depth + 1, builder);
		}

	}
	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (node == null)
			return;
		boolean enable = !Boolean.TRUE.equals(getEnabled(node));
		setEnabled(node, enable);
		if (node.getIsExpandable()) {
			if (enable) {
				node.setCssClass("blue pointer");
			} else
				node.setCssClass("red pointer strike");
		}
		node.setSelected(false);
		while (node.getParent() != null) {
			node = node.getParent();
			Boolean value = getEnabled(node);
			if (value == null)
				node.setCssClass("italic gray");
			else if (value)
				node.setCssClass("blue pointer");
			else
				node.setCssClass("red pointer strike");

		}
	}
	private void initStyle(WebTreeNode node) {
		if (!node.getIsExpandable())
			return;
		Boolean value = getEnabled(node);
		if (value == null)
			node.setCssClass("italic gray");
		else if (value)
			node.setCssClass("blue pointer");
		else
			node.setCssClass("red pointer strike");
		for (WebTreeNode child : node.getChildren())
			initStyle(child);
	}

	private Boolean getEnabled(WebTreeNode node) {
		if (node.getIsExpandable()) {
			BooleanAggregator ba = new BooleanAggregator();
			for (WebTreeNode i : node.getChildren()) {
				ba.add(getEnabled(i));
				if (ba.hasBoth() || ba.hasNull())
					return null;
			}
			if (ba.getCount() == 0)
				return null;
			return ba.hasTrue();
		} else {
			String id = (String) node.getData();
			return isEnabled.get(id);
		}
	}

	private void setEnabled(WebTreeNode node, boolean enable) {
		if (node.getIsExpandable()) {
			for (WebTreeNode i : node.getChildren())
				setEnabled(i, enable);
		} else {
			String id = (String) node.getData();
			isEnabled.put(id, enable);
		}
		if (enable) {
			node.setCssClass("blue bold pointer");
		} else {
			node.setCssClass("red bold pointer strike");
		}
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		// TODO Auto-generated method stub
		if (button == this.saveButton) {
			UpdateSsoGroupRequest updateRequest = getManager().getGenerator().nw(UpdateSsoGroupRequest.class);
			updateRequest.setGroupId(this.group.getGroup().getId());
			SsoGroupAttribute attr = getManager().getGenerator().nw(SsoGroupAttribute.class);
			attr.setGroupId(updateRequest.getGroupId());
			SsoWebGroup group = service.getSsoTree().getGroup(updateRequest.getGroupId());
			if (group == null) {
				getManager().showAlert("Group not found: " + updateRequest.getGroupId());
				return;
			}
			attr.setKey("portlets_entitled");
			attr.setType(SsoGroupAttribute.TYPE_JSON);
			attr.setValue(converter.objectToString(this.isEnabled));
			updateRequest.setGroupAttributes(CH.l(attr));
			service.sendRequestToBackend(getPortletId(), updateRequest);
		}

	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		SsoResponse response = (SsoResponse) result.getAction();
		if (response.getOk()) {
			close();
		} else {
			getManager().showAlert(response.getMessage());
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
}
