package com.f1.omsweb;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.f1.base.Message;
import com.f1.base.Row;
import com.f1.povo.standard.TimestampedMessage;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.json.ShowJsonInterPortletMessage;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.converter.json2.MessageToJsonConverter;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;

public class UserEventsTablePortlet extends FastTablePortlet implements WebContextMenuListener {

	private ObjectToJsonConverter converter;
	private BasicPortletSocket showJsonSocket;
	private UserEventsService service;

	public UserEventsTablePortlet(PortletConfig config) {
		super(config, null);
		this.converter = new ObjectToJsonConverter();
		MessageToJsonConverter mtjc = (MessageToJsonConverter) this.converter.getConverter(Message.class);
		//mtjc.setUseIdeableName(false);
		BasicTable inner = new BasicTable(new String[] { "Time", "Type", "Session", "Action" });
		inner.setTitle("User Events");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Time", "Time", new NumberWebCellFormatter(getManager().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL))).setWidth(150);
		table.addColumn(true, "Type", "Type", new BasicWebCellFormatter()).setWidth(80);
		table.addColumn(true, "Session", "Session", new BasicWebCellFormatter()).setWidth(140);
		table.addColumn(true, "Action", "Action", new BasicWebCellFormatter()).setWidth(600);
		setTable(table);
		this.service = (UserEventsService) getManager().getService(UserEventsService.ID);
		service.addUserEventsTable(this);
		this.showJsonSocket = addSocket(true, "showEventsTree", "Show selected events in Tree", true, null, CH.s(ShowJsonInterPortletMessage.class));
		//getTable().addMenuListener(this);
	}
	public void addMessages(List<TimestampedMessage> messages) {
		for (TimestampedMessage message : messages) {
			onTimestampedMessage(message);
		}
	}

	public static class Builder extends AbstractPortletBuilder<UserEventsTablePortlet> {

		private static final String ID = "UserEventsTablePortlet";

		public Builder() {
			super(UserEventsTablePortlet.class);
		}

		@Override
		public UserEventsTablePortlet buildPortlet(PortletConfig portletManager) {
			return new UserEventsTablePortlet(portletManager);
		}

		@Override
		public String getPortletBuilderName() {
			return "User Events Table Portlet";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	public void onTimestampedMessage(TimestampedMessage tsm) {
		String text = converter.objectToString(tsm.getAction());
		getTable().getTable().getRows().addRow(TimeUnit.NANOSECONDS.toMillis(tsm.getTimestampNanos()), tsm.getNotes(), tsm.getPartitionId(), text);
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		if (showJsonSocket.hasConnections()) {
			StringBuilder js = new StringBuilder();
			JsonBuilder builder = new JsonBuilder(js);
			builder.startList();
			for (Row row : fastWebTable.getSelectedRows())
				builder.addEntryQuoted(row.get("Action"));
			builder.endList();
			builder.end();
			showJsonSocket.sendMessage(new ShowJsonInterPortletMessage(js.toString()));
		}

	}

	@Override
	public void onClosed() {
		service.removePortlet(this);
		super.onClosed();
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
