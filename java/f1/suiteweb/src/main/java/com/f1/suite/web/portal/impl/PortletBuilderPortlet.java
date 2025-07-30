package com.f1.suite.web.portal.impl;

import java.util.Map;
import java.util.Set;

import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletBuilder;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.AH;
import com.f1.utils.OH;

public class PortletBuilderPortlet extends FastTreePortlet implements WebTreeContextMenuListener {

	private String portletIdOfParentToAddPortletTo;
	private String portletIdOfPortletToWrap;
	private boolean first = true;

	public PortletBuilderPortlet(PortletConfig portletConfig, boolean onlyContainers) {
		super(portletConfig);
		Set<String> builderIds = getManager().getBuilders();
		WebTreeManager tm = getTree().getTreeManager();
		getTree().setRowHeight(18);
		getTree().setRootLevelVisible(false);
		getTree().addMenuContextListener(this);
		for (String builderId : builderIds) {
			PortletBuilder builder = getManager().getPortletBuilder(builderId);
			if (onlyContainers && (!PortletContainer.class.isAssignableFrom(builder.getPortletType()) || GridPortlet.class.isAssignableFrom(builder.getPortletType())))
				continue;
			if (builder.getIsUserCreatable()) {
				builder.getPath();
				addNode(tm.getRoot(), 0, builder);
			}
		}
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (first) {
			first = false;
			callJsFunction("focusSearch").end();
		}
	}

	private void addNode(WebTreeNode parent, int depth, PortletBuilder builder) {
		WebTreeManager tm = getTree().getTreeManager();
		if (AH.length(builder.getPath()) <= depth) {
			tm.createNode(formatText(builder.getPortletBuilderName()), parent, false).setCssClass("blue bold clickable").setData(builder).setIcon(builder.getIcon());
		} else {
			final String name = formatText(builder.getPath()[depth]);
			for (final WebTreeNode node : parent.getChildren()) {
				if (OH.eq(name, node.getName())) {
					addNode(node, depth + 1, builder);
					return;
				}
			}
			WebTreeNode node = tm.createNode(name, parent, false);
			node.setCssClass("clickable");
			addNode(node, depth + 1, builder);
		}

	}

	public static class Builder extends AbstractPortletBuilder<PortletBuilderPortlet> {

		public static final String ID = "PortletAdder";

		public Builder() {
			super(PortletBuilderPortlet.class);
		}

		@Override
		public PortletBuilderPortlet buildPortlet(PortletConfig portletConfig) {
			return new PortletBuilderPortlet(portletConfig, false);
		}

		@Override
		public String getPortletBuilderName() {
			return "PortletAdder";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 300;
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (node == null)
			return;
		Object data = node.getData();
		if (data instanceof PortletBuilder) {
			PortletBuilder builder = (PortletBuilder) data;
			final Portlet newPortlet = getManager().buildPortlet(builder.getPortletBuilderId());
			if (portletIdOfParentToAddPortletTo == null) {
				DesktopPortlet desktop = PortletHelper.findParentByType(this, DesktopPortlet.class);
				desktop.addChild(newPortlet);
			} else {
				Portlet portlet = getManager().getPortlet(this.portletIdOfParentToAddPortletTo);
				close();
				if (portlet instanceof BlankPortlet) {
					portlet.getParent().replaceChild(portlet.getPortletId(), newPortlet);
					portlet.close();
				} else if (portlet instanceof PortletContainer) {
					PortletContainer container = (PortletContainer) portlet;
					if (portletIdOfPortletToWrap != null) {
						PortletContainer newContainer = (PortletContainer) newPortlet;
						Portlet removed = container.getChild(this.portletIdOfPortletToWrap);
						container.replaceChild(this.portletIdOfPortletToWrap, newContainer);
						newContainer.addChild(removed);
					} else {
						container.addChild(newPortlet);
					}
				} else
					throw new RuntimeException("cant add child to portlet: " + portlet);
			}
		} else if (data == null) {
			node.setIsExpanded(!node.getIsExpanded());
		}
		node.setSelected(false);
	}
	public void setPortletIdOfParentToAddPortletTo(String portletId) {
		this.portletIdOfParentToAddPortletTo = portletId;
	}

	public void setPortletIdOfPortletToWrap(String portletId) {
		this.portletIdOfPortletToWrap = portletId;
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode row, FastWebTreeColumn col) {
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
}
