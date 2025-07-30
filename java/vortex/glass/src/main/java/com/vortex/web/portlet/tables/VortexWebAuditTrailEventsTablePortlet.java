package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Row;
import com.f1.bootstrap.appmonitor.AppMonitorUtils;
import com.f1.bootstrap.appmonitor.marshalling.GenericObjectToByteArrayConverter;
import com.f1.msg.MsgEvent;
import com.f1.povo.f1app.F1AppEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailF1Event;
import com.f1.povo.f1app.audit.F1AppAuditTrailLoggerEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailMsgEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.povo.f1app.audit.F1AppAuditTrailSqlEvent;
import com.f1.speedlogger.impl.SpeedLoggerUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.LargeStringWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.OfflineConverter;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.fix.FixDictionary;
import com.f1.utils.fix.impl.BasicFixDictionary;
import com.f1.utils.fix.impl.BasicFixParser;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.vortex.client.VortexClientAuditEventsListener;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.web.messages.ShowAuditTrailInterPortletMessage;

public class VortexWebAuditTrailEventsTablePortlet extends VortexWebTablePortlet implements VortexClientAuditEventsListener, WebContextMenuFactory, WebContextMenuListener {

	private BasicPortletSocket showJsonSocket;
	private ObjectToJsonConverter jsonConverter;
	private ObjectToByteArrayConverter converter;
	private BasicFixParser fixParser;

	public VortexWebAuditTrailEventsTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { "seq", "host", "appName", "msg", "time", "location", "atype", "mtype", "data", "object" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Audit Trail");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Sequence #", "seq", service.getNumberFormatter());
		table.addColumn(true, "Host Name", "host", service.getHostnameFormatter());
		table.addColumn(true, "Event Time", "time", service.getFullDateTimeWebCellFormatter());
		table.addColumn(true, "App Name", "appName", service.getBasicFormatter()).setWidth(120);
		table.addColumn(true, "Audit Type", "atype",
				new BasicWebCellFormatter().addConditionalString("style.color=blue", "FIX-INBOUND", "FIX-OUTBOUND").addConditionalString("style.color=green", "SQL")
						.addConditionalString("style.color=purple", "LOG"));
		table.addColumn(true, "Message Type", "mtype", service.getBasicFormatter()).setWidth(150).setCssColumn("bold");
		table.addColumn(true, "Location", "location", service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "Message", "msg", new LargeStringWebCellFormatter().setMaxLength(25)).setWidth(150);
		table.setMenuFactory(this);
		//table.addMenuListener(this);
		setTable(table);
		this.showJsonSocket = addSocket(true, "showEventsTree", "Show selected events in Tree", true, null, CH.s(ShowAuditTrailInterPortletMessage.class));
		this.jsonConverter = new ObjectToJsonConverter();
		this.jsonConverter.setIgnoreUnconvertable(true);
		this.converter = new GenericObjectToByteArrayConverter();// (ObjectToByteArrayConverter) getManager().getState().getWebState().getPartition().getContainer().getServices().getConverter();
		FixDictionary dictionary = new BasicFixDictionary();
		this.fixParser = new BasicFixParser(dictionary, (char) 1, '=');
		this.fixParser.setAllowUnknownTags(true);
		agentManager.addAuditEventsListener(this);
	}
	private void addEventRow(VortexClientF1AppState javaApp, String msg, String location, String atype, String mtype, F1AppAuditTrailEvent data, Object object) {
		addRow(data.getAuditSequenceNumber(), javaApp.getSnapshot().getHostName(), javaApp.getSnapshot().getAppName(), msg, data.getTimeMs(), location, atype, mtype, data, object);
	}

	@Override
	public void close() {
		agentManager.removeAuditEventsListener(this);
		super.close();
	}

	private Set<Row> currentSelections = new IdentityHashSet<Row>();

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
		if (showJsonSocket.hasConnections()) {

			Set<Row> newSelections = new IdentityHashSet<Row>();
			for (Row row : fastWebTable.getSelectedRows()) {
				newSelections.add(row);
			}
			Set<Row> rem = CH.comm(currentSelections, newSelections, true, false, false);
			List<Tuple2<F1AppAuditTrailEvent, Object>> removed = new ArrayList<Tuple2<F1AppAuditTrailEvent, Object>>(rem.size());
			for (Row remRow : rem)
				removed.add(new Tuple2<F1AppAuditTrailEvent, Object>((F1AppAuditTrailEvent) remRow.get("data"), remRow.get("object")));

			Set<Row> add = CH.comm(currentSelections, newSelections, false, true, false);
			List<Tuple2<F1AppAuditTrailEvent, Object>> added = new ArrayList<Tuple2<F1AppAuditTrailEvent, Object>>(add.size());
			for (Row addRow : add)
				added.add(new Tuple2<F1AppAuditTrailEvent, Object>((F1AppAuditTrailEvent) addRow.get("data"), addRow.get("object")));

			currentSelections = newSelections;
			showJsonSocket.sendMessage(new ShowAuditTrailInterPortletMessage(removed, added));
		}

	}

	public void onAgentAuditEvent(F1AppAuditTrailEvent event) {
		VortexClientF1AppState javaApp = service.getAgentManager().getJavaAppState(event.getF1AppInstanceId());
		if (javaApp != null) {
			Tuple2<String, Object> sink = new Tuple2<String, Object>();
			AppMonitorUtils.convertPayloadToJson(event, converter, jsonConverter, sink);
			String json = sink.getA();
			Object object = sink.getB();
			switch (event.getType()) {
				case F1AppAuditTrailRule.EVENT_TYPE_LOG: {
					F1AppAuditTrailLoggerEvent logEvent = (F1AppAuditTrailLoggerEvent) event;
					String location = null;
					if (logEvent.getLineNumber() != -1)
						location = logEvent.getFileName() + ":" + logEvent.getLineNumber();
					String type = SpeedLoggerUtils.getFullLevelAsString(logEvent.getLogLevel());
					addEventRow(javaApp, json, location, "LOG", type, event, object);
					break;
				}
				case F1AppAuditTrailRule.EVENT_TYPE_SQL: {
					F1AppAuditTrailSqlEvent sqlEvent = (F1AppAuditTrailSqlEvent) event;
					String type = null;
					if (event.getPayloadFormat() == F1AppAuditTrailEvent.FORMAT_STRING_TEXT) {
						String sql = sqlEvent.getPayloadAsString();
						if (sql != null) {
							int i = sql.indexOf(' ');
							if (i < 0)
								type = sql.toUpperCase();
							else
								type = sql.substring(0, i).toUpperCase();
						}
					}
					addEventRow(javaApp, json, null, "SQL", type, event, object);
					break;
				}
				case F1AppAuditTrailRule.EVENT_TYPE_MSG: {
					F1AppAuditTrailMsgEvent msgEvent = (F1AppAuditTrailMsgEvent) event;
					String type = null;
					switch (msgEvent.getType()) {
						case MsgEvent.TYPE_FIX:
							type = getFixType((Map) object);
							break;
					}
					addEventRow(javaApp, json, msgEvent.getTopic(), msgEvent.getIsIncoming() ? "FIX-INBOUND" : "FIX-OUTBOUND", type, event, object);
					break;
				}
				case F1AppAuditTrailRule.EVENT_TYPE_F1: {
					F1AppAuditTrailF1Event f1Event = (F1AppAuditTrailF1Event) event;
					//String text = SH.toString(f1Event.getMessage().length);
					OfflineConverter converter = getManager().getState().getWebState().getPartition().getContainer().getServices().getConverter();
					addEventRow(javaApp, json, f1Event.getStateClassName(), "F1 MESSAGE", f1Event.getMessageClassName(), event, object);
					break;
				}
			}
		}
	}
	private static final byte[] MSG_CODE = new byte[] { 1, '3', '5', '=' };
	private static final byte[] EXEC_CODE = new byte[] { 1, '1', '5', '0', '=' };
	public static String getFixType(Map<String, String> msg) {
		String msgTypeString = msg.get("35");
		if (msgTypeString != null && msgTypeString.length() == 1) {
			char msgType = msgTypeString.charAt(0);
			switch (msgType) {
				case '0':
					return "HEART_BEAT";
				case '1':
					return "TEST_ REQUEST";
				case '2':
					return "RESET_REQUEST";
				case '3':
					return "REJECT";
				case '4':
					return "SEQ_RESET";
				case '5':
					return "LOGOUT";
				case '6':
					return "IOI";
				case '7':
					return "ADVERTISEMENT";
				case '8':
					String exeTypeString = msg.get("150");
					if (exeTypeString != null && exeTypeString.length() == 1) {
						char exeType = exeTypeString.charAt(0);
						switch (exeType) {
							case '0':
								return "EXEC(NEW)";
							case '1':
								return "EXEC(PARTIAL_FILL)";
							case '2':
								return "EXEC(FILL)";
							case '3':
								return "EXEC(DFD)";
							case '4':
								return "EXEC(CANCELED)";
							case '5':
								return "EXEC(REPLACE)";
							case '6':
								return "EXEC(PENDING_CXL)";
							case '7':
								return "EXEC(STOPPED)";
							case '8':
								return "EXEC(REJECTED)";
							case '9':
								return "EXEC(SUSPENDED)";
							case 'A':
								return "EXEC(PENDING_NEW)";
							case 'B':
								return "EXEC(CALCULATED)";
							case 'C':
								return "EXEC(EXPIRED)";
							case 'D':
								return "EXEC(RESTATED)";
							case 'E':
								return "EXEC(PENDING_REPLACE)";
						}
					}
					return "EXEC";
				case '9':
					return "CXL_REJECT";
				case 'A':
					return "LOGON";
				case 'B':
					return "NEWS";
				case 'C':
					return "EMAIL";
				case 'D':
					return "ORD_SINGLE";
				case 'E':
					return "ORD_LIST";
				case 'F':
					return "ORD_CXL_REQ";
				case 'G':
					return "ORD_CXL_REPLACE_REQ";
				case 'H':
					return "ORD_STATUS_REQUEST";
				case 'J':
					return "ALLOCATION";
				case 'K':
					return "LIST_CXL_REQ";
				case 'L':
					return "LIST_EXEC";
				case 'M':
					return "LIS_STATUS_REQ";
				case 'N':
					return "LIST_STATUS";
				case 'O':
					return "ALLOC_ACK";
				case 'P':
					return "DON_KNOW_TRADE";
				case 'Q':
					return "QUOTE_REQ";
				case 'R':
					return "QUOTE";
				default:
					return SH.toString(msgType);
			}
		}
		return "FIX";
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebAuditTrailEventsTablePortlet> {

		public static final String ID = "AgentF1AuditTablePortlet";

		public Builder() {
			super(VortexWebAuditTrailEventsTablePortlet.class);
		}

		@Override
		public VortexWebAuditTrailEventsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebAuditTrailEventsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Audit Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("clear".equals(action)) {
			getTable().clear();
		}
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>(1);
		children.add(new BasicWebMenuLink("Clear", true, "clear"));
		BasicWebMenu menu = new BasicWebMenu("", true, children);
		return menu;
	}
	@Override
	public void onAgentAuditEvents(List<F1AppEvent> list) {
		for (F1AppEvent event : list) {
			if (event instanceof F1AppAuditTrailEvent)
				onAgentAuditEvent((F1AppAuditTrailEvent) event);
		}

	}

}
