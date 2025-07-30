package com.vortex.ssoweb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.sso.messages.SsoUpdateEvent;

public class SsoEventsTablePortlet extends FastTablePortlet implements WebContextMenuListener, WebContextMenuFactory {

	private SsoService service;
	private PortletManager manager;
	private Map<SsoUpdateEvent, Row> eventToRow;
	protected Logger log;

	public SsoEventsTablePortlet(PortletConfig config) {
		super(config, null);
		String className = SH.afterFirst("" + this.getClass(), " ");
		log = Logger.getLogger(className);
		manager = getManager();
		service = (SsoService) manager.getService(SsoService.ID);
		LH.info(log, "SsoEventsTablePortlet created");
		Class[] clazz = { Byte.class, Boolean.class, String.class, String.class, String.class, Long.class, String.class, String.class };
		String[] ids = { "type", "ok", "msg", "session", "name", "now", "nspace", "loc" };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Events");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, manager.getTextFormatter());
		MapWebCellFormatter<Boolean> stateFormatter = new MapWebCellFormatter<Boolean>(getManager().getTextFormatter());
		stateFormatter.addEntry(true, "Okay", "_cna=portlet_icon_okay", "");
		stateFormatter.addEntry(false, "Failed", "_cna=portlet_icon_error", "");
		stateFormatter.setDefaultWidth(20).lock();

		table.addColumn(true, "Status", "ok", stateFormatter);
		table.addColumn(true, "sso.now", "now", service.getTimeWebCellFormatter());
		table.addColumn(true, "Event", "type", service.getEventTypeFormatter()).setWidth(100);
		table.addColumn(true, "UserName", "name", service.getBasicFormatter()).setWidth(100);
		table.addColumn(true, "Namespace", "nspace", service.getBasicFormatter()).setWidth(100);
		table.addColumn(true, "Remote Address", "loc", service.getBasicFormatter()).setWidth(100);
		table.addColumn(true, "Message", "msg", service.getBasicNotNullFormatter("Success")).setWidth(150);
		table.addColumn(false, "Session Id", "session", service.getBasicFormatter());
		table.sortRows("now", false, true, false);

		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);

		eventToRow = new HashMap<SsoUpdateEvent, Row>();
		service.addPortlet(this);
	}

	public void onClosed() {
		super.onClosed();
	}

	public void add(SsoUpdateEvent event) {
		eventToRow.put(event,
				addRow(event.getType(), event.getOk(), event.getMessage(), event.getSession(), event.getName(), event.getNow(), event.getNamespace(), event.getClientLocation()));
	}

	//	public void update(SsoUpdateEvent event) {
	//		Row row = eventToRow.get(user);
	//		if (null == row)
	//			LH.warning( log ,getClass() , " trying to update non-existent user " , user.toString());
	//		else {
	//			row.put("id", user.getId());
	//			row.put("expire", user.getExpires());
	//			row.put("user", user.getUserName());
	//			row.put("first", user.getFirstName());
	//			row.put("last", user.getLastName());
	//			row.put("phone", user.getPhoneNumber());
	//			row.put("psw", user.getPassword());
	//			row.put("email", user.getEmail());
	//			row.put("comp", user.getCompany());
	//			row.put("resetq", user.getResetQuestion());
	//			row.put("reseta", user.getResetAnswer());
	//			row.put("status", user.getStatus());
	//			row.put("encode", user.getEncodingAlgorithm());
	//			row.put("rev", user.getRevision());
	//			row.put("attempts", user.getMaxBadAttempts());
	//		}
	//	}

	//	public void remove(SsoUser user) {
	//		Row row = eventToRow.get(user);
	//		if (null != row) {
	//			int loc = row.getLocation();
	//			if (loc > -1) {
	//				super.removeRow(row);
	//			} else {
	//				LH.warning( log ,row , " not present in table");
	//			}
	//			eventToRow.remove(user);
	//		} else
	//			LH.warning( log ,"Tried to delete non-existant " , user.getClass());
	//	}

	public static class Builder extends AbstractPortletBuilder<SsoEventsTablePortlet> {

		public static final String ID = "ssoEventsTablePortlet";

		public Builder() {
			super(SsoEventsTablePortlet.class);
		}

		@Override
		public SsoEventsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new SsoEventsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Events Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		return null;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		List<Row> selectedRows = table.getSelectedRows();

	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}

	public Map<SsoUpdateEvent, Row> getUserToRow() {
		return eventToRow;
	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
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
