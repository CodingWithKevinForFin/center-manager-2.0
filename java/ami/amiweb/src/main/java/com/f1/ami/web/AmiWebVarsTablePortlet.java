package com.f1.ami.web;

import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.style.PortletStyleManager;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebVarsTablePortlet extends GridPortlet implements FormPortletListener, WebContextMenuFactory, WebContextMenuListener, ConfirmDialogListener {

	private static final String DASHBOARD = "Dashboard";
	private static final int IDX_NAME = 0;
	private static final int IDX_VALUE = 1;
	private static final int IDX_TYPE = 2;
	private static final int IDX_SOURCE = 3;

	private static final String COL_NAME_NAME = "Name";
	private static final String COL_NAME_VALUE = "Value";
	private static final String COL_NAME_TYPE = "Type";
	private static final String COL_NAME_SOURCE = "Source";
	private static final String COL_NAME_LAYOUT = "Layout";
	private static final Map<Byte, String> SOURCE_BYTES_2_STRINGS = CH.m(AmiWebVarsManager.SOURCE_PROPERTY, "Property", AmiWebVarsManager.SOURCE_USER_PROFILE, "User Profile",
			AmiWebVarsManager.SOURCE_PREDEFINED, "Predefined", AmiWebVarsManager.SOURCE_DASHBOARD, DASHBOARD, AmiWebVarsManager.SOURCE_PLUGIN, "Plugin");
	private FastTablePortlet table;
	private FormPortlet buttonsForm;
	private FormPortletButton addVariablesButton;
	private AmiWebVarsManager varsManager;
	private BasicTable basicTable;
	private FormPortletButton closeButton;
	//	private boolean needsLayoutReboot = false;
	private FormPortletButton applyChangesButton;
	private AmiWebService service;

	public AmiWebVarsTablePortlet(PortletConfig config, AmiWebVarsManager varsManager) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
		this.varsManager = varsManager;
		this.basicTable = new BasicTable(String.class, COL_NAME_NAME, Object.class, COL_NAME_VALUE, Object.class, COL_NAME_TYPE, String.class, COL_NAME_SOURCE, String.class,
				COL_NAME_LAYOUT);
		BasicWebCellFormatter formatter = new AmiWebBasicWebCellFormatter();
		this.table = new FastTablePortlet(generateConfig(), basicTable, "Variables");
		rebuildTable(false);
		table.getTable().addColumn(true, COL_NAME_NAME, COL_NAME_NAME, formatter);
		table.getTable().addColumn(true, COL_NAME_VALUE, COL_NAME_VALUE, formatter);
		table.getTable().addColumn(true, COL_NAME_TYPE, COL_NAME_TYPE, formatter);
		table.getTable().addColumn(true, COL_NAME_SOURCE, COL_NAME_SOURCE, formatter);
		table.getTable().addColumn(true, COL_NAME_LAYOUT, COL_NAME_LAYOUT, formatter);
		table.autoSizeAllColumns();
		PortletStyleManager styleManager = getManager().getStyleManager();
		table.setDialogStyle(styleManager.getDialogStyle());
		table.setFormStyle(styleManager.getFormStyle());
		table.getTable().setMenuFactory(this);
		table.getTable().addMenuListener(this);
		table.getTable().sortRows(COL_NAME_NAME, true, true, false);

		AmiWebHeaderPortlet header = new AmiWebHeaderPortlet(generateConfig());
		header.setShowSearch(false);
		header.updateBlurbPortletLayout("AMI Variables", "");
		header.setShowLegend(false);
		header.setInformationHeaderHeight(60);
		header.setShowBar(false);
		addChild(header);
		this.buttonsForm = new FormPortlet(generateConfig());
		this.closeButton = this.buttonsForm.addButton(new FormPortletButton("Close"));
		this.addVariablesButton = this.buttonsForm.addButton(new FormPortletButton("Add variable"));
		this.applyChangesButton = new FormPortletButton("Apply (reset layout)");
		this.buttonsForm.addFormPortletListener(this);

		addChild(table, 0, 1);
		addChild(buttonsForm, 0, 2);
		setRowSize(2, 100);
		setSuggestedSize(850, 500);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.addVariablesButton == button)
			getManager().showDialog("Add Field", new AmiWebEditVarPortlet(generateConfig(), "", "", "", true, this));
		else if (this.closeButton == button)
			close();
		else if (this.applyChangesButton == button) {
			recompile();
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		boolean isDashboard = true;
		for (Row row : this.table.getTable().getSelectedRows())
			if (!DASHBOARD.equals(row.get(COL_NAME_SOURCE))) {
				isDashboard = false;
				break;
			}
		BasicWebMenu r = new BasicWebMenu();
		int cnt = this.table.getTable().getSelectedRows().size();
		r.add(new BasicWebMenuLink("Edit...", isDashboard && cnt == 1, "edit"));
		r.add(new BasicWebMenuLink("Copy...", cnt == 1, "copy"));
		r.add(new BasicWebMenuLink("Remove", isDashboard && cnt > 0, "remove"));
		r.add(new BasicWebMenuLink("Add new Variable...", true, "add"));
		return r;
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("add".equals(action)) {
			getManager().showDialog("Add Variable", new AmiWebEditVarPortlet(generateConfig(), "", "", "", true, this));
		} else if ("remove".equals(action)) {
			int size = this.table.getTable().getSelectedRows().size();
			getManager().showDialog("Confirm",
					new ConfirmDialogPortlet(generateConfig(), "Delete " + size + " variables?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("DELETE"));
		} else if ("copy".equals(action)) {
			Row row = this.table.getTable().getSelectedRows().get(0);
			String name = row.get(COL_NAME_NAME, Caster_String.INSTANCE);
			String layout = getLayout(row);
			String text;
			if (DASHBOARD.equals(row.get(COL_NAME_SOURCE))) {
				text = service.getScriptManager(layout).getLayoutVariableScripts().get(name);
			} else {
				text = row.get(COL_NAME_VALUE, Caster_String.INSTANCE);
				if (String.class.getSimpleName().equals(row.get(COL_NAME_TYPE)))
					text = SH.quoteToJavaConst('"', text);
			}
			if (layout == null)
				layout = "";
			getManager().showDialog("Copy Variable", new AmiWebEditVarPortlet(generateConfig(), layout, name + "_copy", text, true, this));
		} else if ("edit".equals(action)) {
			Row row = this.table.getTable().getSelectedRows().get(0);
			String name = row.get(COL_NAME_NAME, Caster_String.INSTANCE);
			String layout = getLayout(row);
			String script = service.getScriptManager(layout).getLayoutVariableScripts().get(name);
			getManager().showDialog("Edit Variable", new AmiWebEditVarPortlet(generateConfig(), layout, name, script, false, this));
		}

	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}

	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("DELETE".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				for (Row row : this.table.getTable().getSelectedRows()) {
					String name = row.get(COL_NAME_NAME, Caster_String.INSTANCE);
					String layout = getLayout(row);
					service.getScriptManager(layout).removeLayoutVariable(name);
				}
				rebuildTable(true);
			}
			return true;
		}
		//		} else if ("reset_message".equals(source.getCallback())) {
		//			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
		//				// Reset action 
		//				resetLayout();
		//			}
		//			return true;
		//		}
		return false;
	}

	private void recompile() {
		service.recompileAmiScript();
		//		service.getLayoutFilesManager().rebuildLayout();
		//		this.needsLayoutReboot = false;
		this.buttonsForm.removeButtonNoThrow(this.applyChangesButton);
	}
	public void rebuildTable(boolean needsLayoutRebuild) {
		//		if (needsLayoutRebuild) {
		//			if (!this.needsLayoutReboot) {
		//				this.needsLayoutReboot = true;
		//				this.buttonsForm.addButton(this.applyChangesButton);
		//			}
		//		}
		FastTablePortlet ft = this.table;
		ft.clearRows();
		for (String varname : varsManager.getGlobalVarNames()) {
			String value = AmiUtils.s(varsManager.getGlobalVarValue(varname));
			String type = varsManager.getGlobalVarType(varname).getSimpleName();
			String source = CH.getOr(SOURCE_BYTES_2_STRINGS, varsManager.getGlobalVarSource(varname), null);
			if (DASHBOARD.equals(source))
				continue;
			ft.addRow(varname, value, type, source, "N/A");
		}
		for (String layoutAlias : service.getLayoutFilesManager().getFullAliasesByPriority()) {
			AmiWebScriptManagerForLayout sm = service.getScriptManager(layoutAlias);
			for (String varname : sm.getLayoutVariableScripts().keySet()) {
				String value = AmiUtils.s(sm.getLayoutVariableValues().getValue(varname));
				String type = sm.getLayourVariableTypes().getType(varname).getSimpleName();
				ft.addRow(varname, value, type, DASHBOARD, "".equals(layoutAlias) ? "<root>" : layoutAlias);
			}
		}
		if (needsLayoutRebuild)
			recompile();
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		int size = this.table.getTable().getSelectedRows().size();
		if (size != 1)
			return;
		Row row = this.table.getTable().getSelectedRows().get(0);
		if (!DASHBOARD.equals(row.get(COL_NAME_SOURCE)))
			return;
		String name = row.get(COL_NAME_NAME, Caster_String.INSTANCE);
		String layout = getLayout(row);
		String script = service.getScriptManager(layout).getLayoutVariableScripts().get(name);
		getManager().showDialog("Edit Variable", new AmiWebEditVarPortlet(generateConfig(), layout, name, script, false, this));
	}

	private String getLayout(Row row) {
		String layout = row.get(COL_NAME_LAYOUT, Caster_String.INSTANCE);
		if ("<root>".equals(layout))
			layout = "";
		if ("N/A".equals(layout))
			layout = null;
		return layout;
	}

	//	@Override
	//	public void onClosed() {
	//		if (needsLayoutReboot) {
	//			ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(),
	//					"In order for changes to user settings to take effect, the current layout must be reset. Would you like to reset now, or manually log out and log in later?",
	//					ConfirmDialogPortlet.TYPE_YES_NO).setCallback("reset_message");
	//			dialog.updateButton(ConfirmDialogPortlet.ID_YES, "Reset Now");
	//			dialog.updateButton(ConfirmDialogPortlet.ID_NO, "Reset Later");
	//			dialog.addDialogListener(this);
	//			this.getManager().showDialog("Reset Required", dialog);
	//		}
	//		super.onClosed();
	//	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}

	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
