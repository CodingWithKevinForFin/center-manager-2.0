package com.vortex.web.portlet.trees;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.f1.container.ContainerTools;
import com.f1.msg.MsgEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailF1Event;
import com.f1.povo.f1app.audit.F1AppAuditTrailLoggerEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailMsgEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailSqlEvent;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.speedlogger.impl.SpeedLoggerUtils;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.utils.CH;
import com.f1.utils.ConvertedException;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple2;
import com.vortex.web.messages.ShowAuditTrailInterPortletMessage;
import com.vortex.web.portlet.tables.VortexWebAuditTrailEventsTablePortlet;

public class VortexWebAuditTrailEventsTreePortlet extends FastTreePortlet {

	private ObjectToJsonConverter converter;
	private String json;
	private Object showJsonSocket;
	private long rangeStart;
	private long rangeEnd;
	private ContainerTools tools;
	private StringBuilder buf;

	public VortexWebAuditTrailEventsTreePortlet(PortletConfig manager) {
		super(manager, new FastWebTree(manager.getPortletManager().getTextFormatter()));
		WebTreeManager tm = getTree().getTreeManager();
		this.converter = new ObjectToJsonConverter();
		this.getTree().setRootLevelVisible(false);
		this.getTreeManager().setComparator(null);//no sorting
		this.showJsonSocket = addSocket(false, "showEventsTree", "Show selected events in Tree", true, null, CH.s(ShowAuditTrailInterPortletMessage.class));
		this.tools = manager.getPortletManager().getState().getWebState().getPartition().getContainer().getServices().getTools();
		long now = tools.getNow();
		this.rangeStart = now - TimeUnit.DAYS.toMillis(365 * 10);
		this.rangeEnd = now + TimeUnit.DAYS.toMillis(365 * 10);
		//setJson("[{name:'!Column Menu options!',items:[{title:'Sort Ascending Once',help:'Sort Ascending Once.HELP'},{title:'Sort Descending Once',help:'Sort Descending Once.HELP'},{title:'Sort Ascending',help:'Sort Ascending.HELP'},{title:'Sort Descending',help:'Sort Descending.HELP'},{title:'Sub-Sort Ascending',help:'Sub-Sort Ascending.HELP'},{title:'Sub-Sort Descending',help:'Sub-Sort Descending.HELP'},{title:'Arrange Columns',help:'Arrange Columns.HELP'},{title:'!Adjust Filter!',help:'!Adjust Filter!.HELP'},{title:'!Clear Filter!',help:'!Clear Filter!.HELP'},{title:'Filter this out now',help:'Filter this out now.HELP'}]},{name:'!Visible Columns!',items:[{title:'Show',help:'Show.HELP'},{title:'Symbol',help:'Symbol.HELP'},{title:'Side',help:'Side.HELP'},{title:'Target',help:'Target.HELP'},{title:'Limit Px',help:'Limit Px.HELP'},{title:'Percent',help:'Percent.HELP'},{title:'Exec Qty',help:'Exec Qty.HELP'},{title:'Exec Value',help:'Exec Value.HELP'},{title:'Status',help:'Status.HELP'},{title:'Created',help:'Created.HELP'},{title:'Updated',help:'Updated.HELP'},{title:'Session',help:'Session.HELP'},{title:'Type',help:'Type.HELP'},{title:'Tif',help:'Tif.HELP'}]},{name:'!Hidden Columns!',items:[{title:'Slice',help:'Slice.HELP'},{title:'Ext Id',help:'Ext Id.HELP'},{title:'Cap',help:'Cap.HELP'},{title:'Broker Locate?',help:'Broker Locate?.HELP'},{title:'Locate Broker',help:'Locate Broker.HELP'},{title:'Locate ID',help:'Locate ID.HELP'},{title:'Rule80A',help:'Rule80A.HELP'},{title:'Dest',help:'Dest.HELP'},{title:'Orig Req ID',help:'Orig Req ID.HELP'},{title:'User Data',help:'User Data.HELP'},{title:'Account',help:'Account.HELP'},{title:'Pass-Thru Tags',help:'Pass-Thru Tags.HELP'},{title:'Text',help:'Text.HELP'},{title:'Instructions',help:'Instructions.HELP'},{title:'ID',help:'ID.HELP'},{title:'Sym suffix',help:'Sym suffix.HELP'},{title:'Grp ID',help:'Grp ID.HELP'},{title:'Req ID',help:'Req ID.HELP'},{title:'Sec ID',help:'Sec ID.HELP'},{title:'Greatest',help:'Greatest.HELP'}]}]");
		this.buf = new StringBuilder();
	}

	private Map<F1AppAuditTrailEvent, WebTreeNode> nodes = new IdentityHashMap<F1AppAuditTrailEvent, WebTreeNode>();

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		ShowAuditTrailInterPortletMessage showMessage = (ShowAuditTrailInterPortletMessage) message;
		List<Tuple2<F1AppAuditTrailEvent, Object>> removed = showMessage.getRemoved();
		if (removed.size() == nodes.size()) {
			getTreeManager().clear();
			nodes.clear();
		} else
			for (Tuple2<F1AppAuditTrailEvent, Object> e : removed) {
				WebTreeNode node = nodes.remove(e.getA());
				if (node != null)
					getTreeManager().removeNode(node);
			}
		WebTreeNode root = getTreeManager().getRoot();
		for (Tuple2<F1AppAuditTrailEvent, Object> add : showMessage.getAdded()) {
			WebTreeNode node = addAgentNode(root, null, add.getA(), add.getB());
			nodes.put(add.getA(), node);
			//nodes.put(add, getTreeManager().createNode(add.getAgentF1ObjectId() + " " + add.getTimeMs(), getTreeManager().getRoot(), false));
		}
		//setJson(showMessage.getJson());
	}

	public WebTreeNode addAgentNode(WebTreeNode parent, String prefix, F1AppAuditTrailEvent event, Object data) {
		if (event instanceof F1AppAuditTrailSqlEvent)
			return addSqlEventNode(parent, prefix, (F1AppAuditTrailSqlEvent) event, (String) data);
		if (event instanceof F1AppAuditTrailLoggerEvent)
			return addLoggerEventNode(parent, prefix, (F1AppAuditTrailLoggerEvent) event, data);
		if (event instanceof F1AppAuditTrailMsgEvent)
			return addMsgEventNode(parent, prefix, (F1AppAuditTrailMsgEvent) event, (Map) data);
		if (event instanceof F1AppAuditTrailF1Event)
			return addF1EventNode(parent, prefix, (F1AppAuditTrailF1Event) event, data);
		else
			throw new RuntimeException("unsupported type: " + event);

	}
	private WebTreeNode addNode(WebTreeNode parent, String prefix, Object obj) {
		if (obj instanceof List)
			return addListNode(parent, prefix, (List) obj);
		else if (obj instanceof Map)
			return addMapNode(parent, prefix, (Map) obj);
		else if (obj instanceof Long) {
			return addLongNode(parent, prefix, (Long) obj);
		} else {
			return addNodeObject(parent, prefix, obj);
		}
		//throw new ToDoException();
	}

	private WebTreeNode addMsgEventNode(WebTreeNode parent, String prefix, F1AppAuditTrailMsgEvent obj, Map<String, String> data) {
		WebTreeManager tm = getTreeManager();
		SH.clear(buf);
		if (prefix != null)
			buf.append(prefix).append(": ");
		getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(obj.getTimeMs(), buf);
		buf.append(' ');
		switch (obj.getMsgType()) {
			case MsgEvent.TYPE_FIX:
				//buf.append(AuditTrailEventsTablePortlet.getFixType(body));
				WebTreeNode node = tm.createNode(buf.toString(), parent, false, obj).setIcon("portlet_icon_connection");
				//for (int i = 0; i < body.length;) {
				//int breakIndex = AH.indexOf((byte) 1, body, i);
				//if (breakIndex == -1) {
				//addNode(node, null, new String(body, i, body.length - i));
				//break;
				//} else {
				//addNode(node, null, new String(body, i, breakIndex - i));
				//i = breakIndex + 1;
				//}
				//}
				addNode(node, VortexWebAuditTrailEventsTablePortlet.getFixType(data), data);
				return node;
		}
		return null;
	}
	private WebTreeNode addF1EventNode(WebTreeNode parent, String prefix, F1AppAuditTrailF1Event obj, Object data) {
		WebTreeManager tm = getTreeManager();
		SH.clear(buf);
		if (prefix != null)
			buf.append(prefix).append(": ");
		getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(obj.getTimeMs(), buf);
		buf.append(' ');
		WebTreeNode node = tm.createNode(buf.toString(), parent, false, obj).setIcon("portlet_icon_db_database");
		addNode(node, prefix, data);
		return node;
	}

	private WebTreeNode addSqlEventNode(WebTreeNode parent, String prefix, F1AppAuditTrailSqlEvent event, String data) {
		WebTreeManager tm = getTreeManager();
		SH.clear(buf);
		if (prefix != null)
			buf.append(prefix).append(": ");
		getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(event.getTimeMs(), buf);
		buf.append(": ").append((String) data);
		WebTreeNode node = tm.createNode(buf.toString(), parent, false, event).setIcon("portlet_icon_db_database");
		if (event.getParams() != null)
			addListNode(node, "params", event.getParams());
		return node;
	}

	private WebTreeNode addNodeObject(WebTreeNode parent, String prefix, Object obj) {
		SH.clear(buf);
		if (prefix != null)
			buf.append(prefix).append(": ");
		buf.append(obj);
		return getTreeManager().createNode(buf.toString(), parent, false).setIcon("portlet_icon_field");

	}

	private WebTreeNode addLongNode(WebTreeNode parent, String prefix, long value) {
		SH.clear(buf);
		if (prefix != null)
			buf.append(prefix).append(": ");
		buf.append(value);
		boolean isDate = value > rangeStart && value < rangeEnd;
		if (isDate) {
			buf.append(" (");
			getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(value, buf);
			buf.append(')');
		}
		return getTreeManager().createNode(buf.toString(), parent, false).setIcon(isDate ? "portlet_icon_clock" : "portlet_icon_field");

	}

	private WebTreeNode addLoggerEventNode(WebTreeNode parent, String prefix, F1AppAuditTrailLoggerEvent event, Object data) {
		WebTreeManager tm = getTreeManager();
		StringBuilder buf = new StringBuilder();
		if (prefix != null)
			buf.append(prefix).append(": ");
		getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(event.getTimeMs(), buf);
		int level = event.getLogLevel();
		WebTreeNode node = tm.createNode("", parent, false, event);
		buf.append(' ').append(SpeedLoggerUtils.getLevelAsString(level));
		buf.append(" - ");
		int elements = 0;
		if (data != null) {
			Object dom = data;
			if (dom instanceof List) {
				List<?> obj = (List<?>) dom;
				for (Object part : obj) {
					if (part instanceof List || part instanceof Map) {
						buf.append(" [see below] ");
						if (part instanceof Map) {
							addNode(node, null, part);
						} else {
							addNode(node, null, part);
						}
					} else if (part instanceof ConvertedException) {
						buf.append(" [see below] ");
						addExceptionNode(node, null, (ConvertedException) part, true);
					} else {
						if (part instanceof Long) {
							long value = (Long) part;
							buf.append(value);
							if (value > rangeStart && value < rangeEnd) {
								buf.append(" (");
								getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL).format(value, buf);
								buf.append(')');
							}
						} else
							SH.s(part, buf);
					}
				}
			} else
				buf.append(dom);
		}
		node.setName(buf.toString());
		if (level > SpeedLoggerLevels.WARNING)
			node.setIcon("portlet_icon_error");
		else if (level > SpeedLoggerLevels.INFO)
			node.setIcon("portlet_icon_warning");
		else
			node.setIcon("portlet_icon_info");
		return node;
	}

	private WebTreeNode addListNode(WebTreeNode parent, String prefix, List list) {
		SH.clear(buf);
		if (prefix != null)
			buf.append(prefix).append(": ");
		WebTreeNode listNode = getTreeManager().createNode(buf.append(SH.toString(list.size())).append(" element(s)").toString(), parent, false).setIcon("portlet_icon_list");
		int i = 0;
		for (Object o : list) {
			SH.clear(buf);
			String prefix2 = buf.append('[').append(SH.toString(i++)).append(']').toString();
			addNode(listNode, prefix2, o);
		}
		return listNode;
	}

	private WebTreeNode addMapNode(WebTreeNode parent, String prefix, Map<?, ?> map) {
		SH.clear(buf);
		if (prefix != null)
			buf.append(prefix).append(": ");
		String name = OH.toString(map.get("_"));
		if (name == null) {
			buf.append(SH.toString(map.size())).append(" element(s)").toString();
		} else
			buf.append(name);
		WebTreeNode mapNode = getTreeManager().createNode(buf.toString(), parent, false).setIcon("portlet_icon_map");
		for (Entry<?, ?> e : map.entrySet()) {
			if ("_".equals(e.getKey()))
				continue;
			addNode(mapNode, OH.toString(e.getKey()), e.getValue());
		}
		return mapNode;
	}
	private WebTreeNode addExceptionNode(WebTreeNode parent, String prefix, ConvertedException part, boolean isTop) {

		SH.clear(buf);
		if (prefix != null)
			buf.append(prefix).append(": ");
		String name = part.getExceptionClassName();
		buf.append(name);
		WebTreeNode mapNode = getTreeManager().createNode(buf.toString(), parent, false).setIcon("portlet_icon_exception");
		StackTraceElement[] callstack = part.getStackTrace();
		ConvertedException cause = (ConvertedException) part.getCause();
		String message = part.getMessage();
		if (message != null)
			addNode(mapNode, "message", message);
		Set<String> keys = part.getKeys();
		if (CH.isntEmpty(keys)) {
			WebTreeNode detailsNode = addNode(mapNode, "details", "");
			for (String key : keys) {
				List<String> values = part.getValues(key);
				if (values.size() > 0) {
					if (values.size() == 1)
						addNode(detailsNode, key, values.get(0));
					else
						addNode(detailsNode, key, values);
				}
			}
		}
		if (callstack != null)
			addCallStackNode(mapNode, "Call Stack", callstack);
		if (isTop)
			while (cause != null) {
				addExceptionNode(mapNode, "Caused By", cause, false);
				cause = (ConvertedException) cause.getCause();
			}

		return mapNode;
	}
	private WebTreeNode addCallStackNode(WebTreeNode parent, String prefix, StackTraceElement[] callstack) {
		SH.clear(buf);
		if (prefix != null)
			buf.append(prefix).append(": ");
		WebTreeNode listNode = getTreeManager().createNode(buf.toString(), parent, false).setIcon("portlet_icon_list");
		int i = 0;
		int digits = SH.toString(callstack.length - 1).length();
		for (StackTraceElement o : callstack) {
			getTreeManager().createNode(o.toString(), listNode, false).setIcon("portlet_icon_stack");
		}
		return listNode;
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebAuditTrailEventsTreePortlet> {

		private static final String ID = "AuditTrailEventsTree";

		public Builder() {
			super(VortexWebAuditTrailEventsTreePortlet.class);
		}

		@Override
		public VortexWebAuditTrailEventsTreePortlet buildPortlet(PortletConfig portletManager) {
			return new VortexWebAuditTrailEventsTreePortlet(portletManager);
		}

		@Override
		public String getPortletBuilderName() {
			return "Audit Events Tree";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}
}
