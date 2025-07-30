package com.f1.ami.web;

import java.util.Map;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.auth.AmiWebState;
import com.f1.ami.web.auth.AmiWebStatesManager;
import com.f1.ami.web.headless.AmiWebHeadlessManager;
import com.f1.ami.web.headless.AmiWebHeadlessWebState;
import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.style.PortletStyleManager;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.utils.VH;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebManageSessionsPortlet extends GridPortlet
		implements WebContextMenuListener, FormPortletListener, ConfirmDialogListener {
	
	private static final String COLUMN_USERNAME = "Username";
	private static final String COLUMN_TYPE = "Type";
	private static final String COLUMN_SESSION_NAME = "Session Name";
	private static final String COLUMN_SESSION_ID = "Session ID";
	private static final String COLUMN_CLOSE = "Close";

	final private AmiWebService service;
	final private FormPortlet subHeader;
	private String user;
	private FastTablePortlet table;
	private BasicWebColumn sessionColumn;
	private BasicWebColumn closeColumn;
	private BasicTable sessionTable;
	private FormPortletTitleField titleField;
	private FormPortletButton refreshButton;
	
	public AmiWebManageSessionsPortlet(PortletConfig config, AmiWebAdminToolPortlet parent) {
		super(config);
		this.service = AmiWebUtils.getService(config.getPortletManager());
		this.user = AmiWebUtils.getService(getManager()).getVarsManager().getUsername();
		AmiWebHeaderPortlet header = new AmiWebHeaderPortlet(generateConfig());
		header.setShowSearch(false);
		header.updateBlurbPortletLayout("Manage User Sessions", "");
		header.setShowLegend(false);
		header.setInformationHeaderHeight(80);
		header.getBarFormPortlet().addFormPortletListener(this);
		this.refreshButton = header.getBarFormPortlet().addButton(new FormPortletButton("Refresh").setCssStyle("_bg=#86cf8c|_fg=#000000"));
		addChild(header, 0, 0);

		subHeader = new FormPortlet(generateConfig());
		addChild(subHeader, 0, 1);
		titleField = subHeader.addField(new FormPortletTitleField(String.format("Sessions Owned by %s", user)));
		titleField.setLeftPosPx(20);
		titleField.setRightPosPx(20);
		titleField.setTopPosPx(10);
		titleField.setBottomPosPx(10);

		this.sessionTable = new BasicTable(new String[] { COLUMN_TYPE, COLUMN_USERNAME, COLUMN_SESSION_NAME, COLUMN_SESSION_ID, COLUMN_CLOSE });
		addChild(this.table = new FastTablePortlet(generateConfig(), this.sessionTable, "Sessions"), 0, 2);
		AmiWebFormatterManager fmm = service.getFormatterManager();
		this.table.getTable().addColumn(true, COLUMN_TYPE, COLUMN_TYPE, fmm.getBasicFormatter());
		this.table.getTable().addColumn(true, COLUMN_USERNAME, COLUMN_USERNAME, fmm.getBasicFormatter());
		this.table.getTable().addColumn(true, COLUMN_SESSION_NAME, COLUMN_SESSION_NAME, fmm.getBasicFormatter()).setWidth(200);
		this.sessionColumn = table.getTable().addColumn(true, COLUMN_SESSION_ID, COLUMN_SESSION_ID, fmm.getBasicFormatter())
				.setIsClickable(true).setWidth(300);
		this.closeColumn = table.getTable().addColumn(true, COLUMN_CLOSE, COLUMN_CLOSE, new AmiWebHtmlFormatter(service))
				.setCssColumn("manage_users_delete_button").setIsClickable(true).setWidth(50);
		PortletStyleManager styleManager = getManager().getStyleManager();
		this.table.setDialogStyle(styleManager.getDialogStyle());
		this.table.setFormStyle(getManager().getStyleManager().getFormStyle());
		AmiWebUtils.applyEndUserTableStyle(this.table);
		this.table.getTable().sortRows(sessionColumn.getColumnId(), true, true, true);
		this.table.getTable().addMenuListener(this);
		
		populateTable(this.user);
		setRowSize(1, 40);
	}

	private void switchToSession(String pgId) {
		getManager().getPendingJs()
				.append("window.location='" + BasicPortletManager.URL_PORTAL + "?" + BasicPortletManager.PAGEID + "=")
				.append(pgId).append("';");
	}

	private void closeSession(String sname, String pgid) {
		ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(),
				"Closing session '<B>" + sname + "</B>' for user \"" + this.user + "\". Are you sure?",
				ConfirmDialogPortlet.TYPE_OK_CANCEL);
		dialog.setCallback("ENDSESSION");
		dialog.setCorrelationData(pgid);
		dialog.addDialogListener(this);
		getManager().showDialog("Close Session", dialog);
	}

	public void populateTable(String user) {
		this.user = user;
		titleField.setValueNoThrow(String.format("Sessions Owned by %s", user));
		FastTablePortlet ft = this.table;
		ft.clearRows();
		AmiWebStatesManager wsm = (AmiWebStatesManager)(getManager().getTools().getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class).getSessionManagerByUser(user));
		if (wsm != null) {
			for (String pgid : wsm.getPgIds()) {
				AmiWebState ws = (AmiWebState) wsm.getState(pgid);
				if (ws instanceof AmiWebHeadlessWebState) {
					ft.addRow("HEADLESS", wsm.getUserName(), VH.getNestedValue(ws, "name", false), pgid, null);
				} else {
					ft.addRow("USER", wsm.getUserName(), VH.getNestedValue(ws, "name", false), pgid, null);
				}
			}
		}
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		String pgid = (String) source.getCorrelationData();
		AmiWebStatesManager wsm = (AmiWebStatesManager) (getManager().getTools().getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class).getSessionManagerByUser(this.user));
		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
			if ("ENDSESSION".equals(source.getCallback())) {
				AmiWebState ws = (AmiWebState) wsm.getState(pgid);
				ws.getPortletManager().close();
				populateTable(this.user);
			}
		}
		return true;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.refreshButton)
			populateTable(this.user);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask,
			int cursorPosition) {
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		String pgid = (String) row.get(COLUMN_SESSION_ID);
		if (col == this.sessionColumn) {
			switchToSession(pgid);
		} else if (col == this.closeColumn) {
			String sname = (String) row.get(COLUMN_SESSION_NAME);
			closeSession(sname, pgid);
		}
	}

	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
