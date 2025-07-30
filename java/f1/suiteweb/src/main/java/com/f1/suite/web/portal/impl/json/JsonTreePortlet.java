package com.f1.suite.web.portal.impl.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.f1.container.ContainerTools;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class JsonTreePortlet extends GridPortlet implements WebTreeContextMenuListener {

	private ObjectToJsonConverter converter;
	private String json;
	private Object showJsonSocket;
	private long rangeStart;
	private long rangeEnd;
	private ContainerTools tools;
	private StringBuilder buf;
	private boolean allowTypes = false;
	private boolean allowDates = false;
	private FastTreePortlet treePortlet;
	private WebTreeManager treeManager;
	private FastWebTree tree;
	private SimpleFastTextPortlet contentPortlet;

	public JsonTreePortlet(PortletConfig manager) {
		super(manager);
		DividerPortlet divider = new DividerPortlet(generateConfig(), true);
		this.addChild(divider, 0, 0);
		this.treePortlet = new FastTreePortlet(generateConfig());
		this.contentPortlet = new SimpleFastTextPortlet(generateConfig());
		divider.addChild(treePortlet);
		divider.addChild(contentPortlet);
		this.tree = treePortlet.getTree();
		this.treeManager = treePortlet.getTree().getTreeManager();
		this.converter = new ObjectToJsonConverter();
		this.tree.setRootLevelVisible(false);
		this.treeManager.setComparator(null);
		this.showJsonSocket = addSocket(false, "showEventsTree", "Show selected events in Tree", true, null, CH.s(ShowJsonInterPortletMessage.class));
		this.tools = manager.getPortletManager().getTools();
		this.tree.addMenuContextListener(this);
		long now = tools.getNow();
		this.rangeStart = now - TimeUnit.DAYS.toMillis(365 * 10);
		this.rangeEnd = now + TimeUnit.DAYS.toMillis(365 * 10);
		this.buf = new StringBuilder();
	}
	public void setJson(String json) {
		this.treeManager.clear();
		this.treeManager.getRoot().setName("JSON");
		this.json = json;
		Object obj = this.converter.stringToObject(json);
		addJsonObject(this.treeManager.getRoot(), "", obj);
	}

	private void addJsonObject(WebTreeNode parent, String key, Object obj) {
		key = WebHelper.escapeHtml(key, SH.clear(buf)).toString();
		if (obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) obj;
			Object type = allowTypes ? map.get("_") : null;
			WebTreeNode child;
			if (type != null) {
				child = this.treeManager.createNode(SH.clear(buf).append(key).append(':').append(type).toString(), parent, true).setCssClass("clickable").setData(obj).setKey(key);
			} else {
				child = this.treeManager.createNode(key, parent, true).setCssClass("clickable").setData(obj).setKey(key);
			}
			child.setIcon("portlet_icon_map");
			for (Map.Entry<?, ?> e : map.entrySet()) {
				if ("_".equals(e.getKey()))
					continue;
				addJsonObject(child, e.getKey().toString(), e.getValue());
			}
		} else if (obj instanceof List) {

			WebTreeNode child = key == null ? parent : this.treeManager.createNode(key, parent, true).setCssClass("clickable").setData(obj).setKey(key);
			List<?> l = (List<?>) obj;
			int digits = SH.toString(l.size() - 1).length();
			child.setIcon("portlet_icon_list");
			for (int i = 0; i < l.size(); i++) {
				Object val = l.get(i);
				SH.clear(buf).append('[');
				SH.rightAlign('0', SH.toString(i), digits, false, buf);
				buf.append(']');
				addJsonObject(child, buf.toString(), val);
			}
		} else {
			WebTreeNode node = this.treeManager.createNode(toValueString(key, obj), parent, true).setCssClass("clickable").setData(obj).setKey(key);
			node.setIcon("portlet_icon_field");
		}

	}
	private String toValueString(String key, Object obj) {
		SH.clear(buf).append(key).append(": <B>");
		if (obj instanceof Long && allowDates) {
			long value = (Long) obj;
			buf.append(value);
			if (value > rangeStart && value < rangeEnd) {
				buf.append(" (");
				getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(value, buf);
				buf.append(')');
			}
		} else if (obj instanceof CharSequence) {
			WebHelper.escapeHtml((CharSequence) obj, buf.append('"')).append('"');
		} else {
			SH.s(obj, buf);
		}
		buf.append("</B>");

		return buf.toString();
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		ShowJsonInterPortletMessage showMessage = (ShowJsonInterPortletMessage) message;
		setJson(showMessage.getJson());
	}

	public boolean isAllowTypes() {
		return allowTypes;
	}

	public void setAllowTypes(boolean allowTypes) {
		this.allowTypes = allowTypes;
	}

	public boolean isAllowDates() {
		return allowDates;
	}

	public void setAllowDates(boolean allowDates) {
		this.allowDates = allowDates;
	}

	public static class Builder extends AbstractPortletBuilder<JsonTreePortlet> {

		private static final String ID = "JsonTree";

		public Builder() {
			super(JsonTreePortlet.class);
		}

		@Override
		public JsonTreePortlet buildPortlet(PortletConfig portletManager) {
			return new JsonTreePortlet(portletManager);
		}

		@Override
		public String getPortletBuilderName() {
			return "Json Tree";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (node != null) {
			StringBuilder sb = new StringBuilder();
			List<WebTreeNode> nodes = new ArrayList<WebTreeNode>();
			for (WebTreeNode n = node; SH.is(n.getKey()); n = n.getParent()) {
				nodes.add(n);
			}
			Collections.reverse(nodes);
			boolean wasArray = treeManager.getRoot().getChildAt(0).getData() instanceof List;
			for (WebTreeNode n : nodes) {
				if (!wasArray)
					sb.append('.');
				sb.append(n.getKey());
				wasArray = n.getData() instanceof List;
			}

			String text = this.converter.objectToString(node.getData());
			this.contentPortlet.clearLines();

			int cnt = 0;
			this.contentPortlet.appendLine("Path:", sb.toString(), "_fg=black|_fm=bold,underline");
			for (String line : SH.splitLines(text)) {
				this.contentPortlet.appendLine(SH.toString(++cnt) + ":", line, "");
			}
			this.contentPortlet.ensureLineVisible(0);
		}
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
}
