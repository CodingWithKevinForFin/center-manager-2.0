package com.f1.omsweb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.pofo.oms.Side;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
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
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.suite.web.tree.impl.WebTreeHelper;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.MapBackedSet;
import com.vortex.ssoweb.NodeSelectionInterPortletMessage;
import com.vortex.ssoweb.NodeSelectionInterPortletMessage.Mask;

public class OmsTreePortlet extends FastTreePortlet implements OmsPortlet, WebTreeContextMenuFactory, WebTreeContextMenuListener {

	private Map<Object, WebTreeNode> treeNodes = new IdentityHashMap<Object, WebTreeNode>();
	private Set<WebTreeNode> selectedLeafs = new MapBackedSet<WebTreeNode>(new IdentityHashMap<WebTreeNode, WebTreeNode>());
	private OrdersService service;
	private BasicPortletSocket showParentOrdersSocket;
	private BasicPortletSocket maskSocket;

	public OmsTreePortlet(PortletConfig portletConfig) {
		super(portletConfig);
		service = (OrdersService) getManager().getService(OrdersService.ID);
		getTree().getTreeManager().getRoot().setName("All Client Orders").setCssClass("clickable");
		getTree().addMenuContextListener(this);
		getTree().setContextMenuFactory(this);
		this.showParentOrdersSocket = addSocket(true, "parentOrders", "Show selected Parent orders", true, CH.s(ShowChildOrdersInterPortletMessage.class), null);
		service.addOmsPortlet(this);
		this.maskSocket = addSocket(false, "selection", "Node Selection", false, null, CH.s(NodeSelectionInterPortletMessage.class));
	}
	public void onClosed() {
		service.removeOmsPortlet(this);
		super.onClosed();
	}
	@Override
	public void onOrder(WebOmsOrder order) {
		if (order.getIsClientOrder()) {
			WebTreeNode node = treeNodes.get(order);
			if (node == null) {
				WebTreeNode parent = getTreeManager().getRoot();

				boolean buy = false;
				for (int i = 0; i < 4; i++) {
					Object key = getData(order, i);
					WebTreeNode value = parent.getChildByKey(key);
					if (value == null) {
						value = getTreeManager().createNode(OH.toString(key), parent, false);
						value.setKey(key);
						value.setCssClass(getStyle(order, i));

					}
					parent = value;
				}
				List<WebOmsOrder> orders = (List<WebOmsOrder>) parent.getData();
				if (orders == null)
					parent.setData(orders = new ArrayList<WebOmsOrder>());
				orders.add(order);
				if (WebTreeHelper.isInSelection(parent) && this.showParentOrdersSocket.hasConnections()) {
					showParentOrdersSocket.sendMessage(new ShowChildOrdersInterPortletMessage(CH.l(order), null));
				}

			} else {
				node.setName(toName(order));
			}
		}
	}
	@Override
	public void onExecution(WebOmsExecution execution) {
		//WebTreeNode node = treeNodes.get(execution);
		//if (node != null) {
		//node.setName(toName(execution));
		//}
		//WebOmsOrder order = execution.getParent();
		//if (order != null) {
		//WebTreeNode pnode = treeNodes.get(order);
		//if (pnode != null) {
		//treeNodes.put(execution, getTreeManager().createNode(toName(execution), pnode, false).setData(execution));
		//}
		//}
	}
	//private String toName(WebOmsExecution execution) {
	//return "broker:" + execution.getExecution().getExecBroker();
	//}
	private String toName(WebOmsOrder order) {
		return order.getOrder().getSymbol();
	}

	public static class Builder extends AbstractPortletBuilder<OmsTreePortlet> {

		public static final String ID = "OmsTreePortlet";

		public Builder() {
			super(OmsTreePortlet.class);
		}

		@Override
		public OmsTreePortlet buildPortlet(PortletConfig portletConfig) {
			return new OmsTreePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Order Tree";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	public Object getData(WebOmsOrder order, int level) {
		switch (level) {
			case 0:
				return order.getOrder().getAccount();
			case 1:
				return order.getFundamentals().getSector();
			case 2:
				return order.getOrder().getSide();
			case 3:
				return order.getOrder().getSymbol();
			default:
				throw new RuntimeException("invalid level: " + level);
		}
	}
	private String getStyle(WebOmsOrder order, int level) {
		switch (level) {
			case 0:
				return "green clickable";
			case 1:
				return "italic clickable";
			case 2:
				return order.getOrder().getSide() == Side.BUY ? "blue clickable" : "red clickable";
			case 3:
				return order.getOrder().getSide() == Side.BUY ? "bold blue clickable" : "bold red clickable";
			default:
				throw new RuntimeException("invalid level: " + level);
		}
	}
	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		children.add(new BasicWebMenuLink("Load A Basket", true, "loadBasket"));
		WebMenu r = new BasicWebMenu("", true, children);
		return r;
	}

	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}
	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		if ("loadBasket".equals(action)) {
			getManager().showDialog("load basket", new NewBasketFormPortlet(generateConfig()));
		}
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		if (WebTreeHelper.isInSelection(node.getParent()))
			return;//who cares, parent is in selection. so adjusting this nodes selection status is mute
		List<WebTreeNode> sink = new ArrayList<WebTreeNode>();
		WebTreeHelper.getAllLeafs(node, sink);
		if (node.getSelected()) {
			for (WebTreeNode leaf : sink)
				if (leaf.getDepth() == 4)
					addLeafSelected(leaf);
		} else {
			for (WebTreeNode leaf : sink)
				if (leaf.getDepth() == 4 && !WebTreeHelper.isInSelection(leaf))
					removeLeafSelected(leaf);
		}
	}
	private void addLeafSelected(WebTreeNode leaf) {
		if (!(leaf.getData() instanceof List))
			return;
		if (!selectedLeafs.add(leaf))
			return;
		if (this.showParentOrdersSocket.hasConnections()) {
			List<WebOmsOrder> orders = (List<WebOmsOrder>) leaf.getData();
			showParentOrdersSocket.sendMessage(new ShowChildOrdersInterPortletMessage(orders, null));
		}
	}
	private void removeLeafSelected(WebTreeNode leaf) {
		if (!(leaf.getData() instanceof List))
			return;
		if (!selectedLeafs.remove(leaf))
			return;
		if (this.showParentOrdersSocket.hasConnections()) {
			List<WebOmsOrder> orders = (List<WebOmsOrder>) leaf.getData();
			showParentOrdersSocket.sendMessage(new ShowChildOrdersInterPortletMessage(null, orders));
		}
	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		if (localSocket == maskSocket) {
			Map<String, String> map = CH.m("process", "main");//TODO:make static
			NodeSelectionInterPortletMessage msg = (NodeSelectionInterPortletMessage) message;
			Set<String> names = new HashSet<String>();
			for (Mask i : msg.getMasks()) {
				String name = null;
				while (i != null) {
					if ("account".equals(i.key)) {
						name = Caster_String.INSTANCE.cast(i.mask);
					}
					i = i.next;
				}
				if (name != null)
					names.add(name);
			}
			WebTreeHelper.search(getTree().getTreeManager().getRoot(), names);
		} else {
			super.onMessage(localSocket, origin, message);
		}
	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		// TODO Auto-generated method stub

	}

}
