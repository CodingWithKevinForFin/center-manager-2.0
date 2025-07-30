package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.AmiWebDmTablesetSchema;
import com.f1.ami.web.dm.portlets.AmiWebSchemaComprisonWarning;
import com.f1.ami.web.dm.portlets.AmiWebSchemaWarningPortlet;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabManager;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.TabPortletStyle;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebEditSchemaPortlet extends GridPortlet implements TabManager, ConfirmDialogListener {

	public static final byte ON_CHANGE_ASK_NEXT_TIME = -1;
	private TabPortlet tabPortlet;
	final private AmiWebService service;
	private Map<String, TableTabPortlet> tabs = new TreeMap<String, TableTabPortlet>();
	private List<AmiWebEditSchemaPortletListener> listeners = new ArrayList<AmiWebEditSchemaPortletListener>();
	final private AmiWebEditAmiScriptCallbackPortlet owner;

	public AmiWebEditSchemaPortlet(AmiWebEditAmiScriptCallbackPortlet owner) {
		super(owner.generateConfig());
		this.owner = owner;
		this.service = AmiWebUtils.getService(getManager());
		this.tabPortlet = new TabPortlet(generateConfig());
		this.tabPortlet.setIsCustomizable(true);
		TabPortletStyle ts = this.tabPortlet.getTabPortletStyle();
		ts.setHasExtraButtonAlways(true);
		ts.setAddButtonText("Add Table");
		this.tabPortlet.setTabManager(this);
		ts.setTabPaddingTop(0);
		ts.setTabPaddingBottom(0);
		ts.setTabPaddingStart(0);
		ts.setTabSpacing(4);
		ts.setFontSize(14);
		ts.setLeftRounding(2);
		ts.setRightRounding(2);
		ts.setTabHeight(22);
		ts.setAddButtonText("Add Table");
		ts.setHasAddButton(true);
		ts.setBackgroundColor("#AAAAAA");
		this.addChild(tabPortlet);
	}

	public void setSchema(AmiWebDmTablesetSchema tableset) {
		OH.assertNotNull(tableset);
		this.tabs.clear();
		this.tabPortlet.removeAllChildren();
		if (tableset != null)
			for (String name : tableset.getTableNamesSorted()) {
				AmiWebDmTableSchema table = tableset.getTable(name);
				addToSchema(name, table).setOnChangedMode(table.getOnChangeMode());
			}
	}
	public void updateSchema(AmiWebDmTablesetSchema tableset) {
		ArrayList<AmiWebSchemaComprisonWarning> warnings = new ArrayList<AmiWebSchemaComprisonWarning>();
		for (String name : tableset.getTableNamesSorted()) {
			AmiWebDmTableSchema nuw = tableset.getTable(name);
			TableTabPortlet existing = this.tabs.get(name);
			if (existing == null) {
				addToSchema(name, nuw).setOnChangedMode(ON_CHANGE_ASK_NEXT_TIME);
			} else {
				if (!existing.getSchema().isSame(nuw)) {
					byte onChangesMode = existing.getOnChangesMode();
					switch (onChangesMode) {
						case ON_CHANGE_ASK_NEXT_TIME:
						case AmiWebDmTableSchema.ON_CHANGE_APPLY:
							updateSchema(name, nuw);
							break;
						case AmiWebDmTableSchema.ON_CHANGE_IGNORE:
							break;
						case AmiWebDmTableSchema.ON_CHANGE_ASK:
							warnings.add(new AmiWebSchemaComprisonWarning(nuw, existing.getSchema()));
							break;
					}
					existing.setOnChangedMode(onChangesMode);
				}
			}
		}
		for (String s : CH.l(tabs.keySet())) {
			if (tableset.getTable(s) == null) {
				AmiWebDmTableSchema schema2 = tabs.get(s).getSchema();
				switch (tabs.get(s).getOnChangesMode()) {
					case ON_CHANGE_ASK_NEXT_TIME:
					case AmiWebDmTableSchema.ON_CHANGE_APPLY:
						removeFromSchema(schema2.getName());
						break;
					case AmiWebDmTableSchema.ON_CHANGE_IGNORE:
						break;
					case AmiWebDmTableSchema.ON_CHANGE_ASK:
						warnings.add(new AmiWebSchemaComprisonWarning(null, schema2));
						break;
				}
			}
		}
		if (warnings.size() > 0) {
			AmiWebSchemaWarningPortlet p = new AmiWebSchemaWarningPortlet(generateConfig(), warnings, this);
			getManager().showDialog("Update Schema", p, 570, 250);
		} else
			onSchemaApplied();
	}

	private void onSchemaApplied() {
		for (AmiWebEditSchemaPortletListener i : this.listeners)
			i.onSchemeUpdated(this);
	}

	public class TableTabPortlet extends GridPortlet implements FormPortletListener {

		private FormPortlet formPortlet;
		private FormPortletTextAreaField textField;
		private String tableName;
		private AmiWebDmTableSchema schema;
		private FormPortletSelectField<Byte> onChangesMode;

		public TableTabPortlet(PortletConfig config, String tableName) {
			super(config);
			this.tableName = tableName;
			this.formPortlet = new FormPortlet(generateConfig());
			addChild(this.formPortlet);
			this.textField = this.formPortlet.addField(new FormPortletTextAreaField(""));
			this.textField.setLeftPosPx(4).setRightPosPx(4).setBottomPosPx(4).setTopPosPx(26);
			this.onChangesMode = this.formPortlet.addField(new FormPortletSelectField<Byte>(Byte.class, "When a test returns a different Schema: "));
			this.onChangesMode.setLabelWidthPx(300);
			this.onChangesMode.addOption(ON_CHANGE_ASK_NEXT_TIME, "Apply Changes Automatically, but just this time");
			this.onChangesMode.addOption(AmiWebDmTableSchema.ON_CHANGE_APPLY, "Apply Changes Automatically");
			this.onChangesMode.addOption(AmiWebDmTableSchema.ON_CHANGE_ASK, "Ask for confirmation");
			this.onChangesMode.addOption(AmiWebDmTableSchema.ON_CHANGE_IGNORE, "Ignore Changes");
			this.onChangesMode.setLeftPosPx(300).setWidthPx(300).setHeightPx(22).setTopPosPx(2).setLabelWidthPx(300);
			this.formPortlet.addFormPortletListener(this);
		}

		public AmiWebDmTableSchema toSchema(AmiWebDmTablesetSchema parent) {
			AmiWebDmTableSchema r = new AmiWebDmTableSchema(service, tableName, parent);
			return r;
		}

		public byte getOnChangesMode() {
			return this.onChangesMode.getValue();
		}

		public AmiWebDmTableSchema getSchema() {
			if (this.schema == null)
				this.schema = parseSchema();
			return this.schema;
		}

		public void setSchema(AmiWebDmTableSchema table) {
			StringBuilder text = new StringBuilder();
			AmiWebScriptManagerForLayout sm = service.getScriptManager(table.getCallback().getAmiLayoutAlias());
			for (String name : table.getColumnNames()) {
				String type = sm.forType(table.getClassTypes().getType(name));
				text.append(type).append(' ').append(name).append('\n');
			}
			this.schema = table;
			this.textField.setValue(text.toString());
			this.onChangesMode.setValue(table.getOnChangeMode());
		}

		public void setOnChangedMode(byte b) {
			this.onChangesMode.setValue(b);
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
			if (field == this.textField)
				this.schema = null;
		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		}

		public AmiWebDmTableSchema parseSchema() {
			String text = this.textField.getValue();
			String[] lines = SH.splitLines(text);
			int pos = 0;
			AmiWebDmTableSchema r = new AmiWebDmTableSchema(service, this.tableName, new AmiWebDmTablesetSchema(service, owner.getCallback()));
			String layoutAlias = owner.getAmiLayoutAlias();
			AmiWebScriptManagerForLayout sl = service.getScriptManager(layoutAlias);
			for (String line : lines) {
				int len = line.length() + 1;
				line = line.trim();
				if (SH.is(line)) {
					String typeStr = SH.beforeFirst(line, ' ', null);
					if (typeStr == null) {
						this.textField.setCursorPosition(pos);
						PortletHelper.ensureVisible(this.formPortlet);
						getManager().showAlert("Table '" + tableName + "' has invalid syntax, expecting: TYPE COL_NAME");
					}
					String col = SH.trim(SH.afterFirst(line, ' '));
					Class type = sl.forName(pos, typeStr);
					//					byte bType = AmiUtils.getTypeForClass(type, AmiDatasourceColumn.TYPE_UNKNOWN);

					if (type == null) {
						PortletHelper.ensureVisible(this.formPortlet);
						this.textField.setCursorPosition(pos + pos + len / 2);
						getManager().showAlert("Table '" + tableName + "' Invalid type: " + typeStr);
					}
					r.addType(col, sl.forType(type));
				}
				pos += len;
			}
			return r;
		}
	}

	public int getTablesCount() {
		return this.tabs.size();
	}

	@Override
	public void onUserMenu(TabPortlet tabPortlet, Tab tab, String menuId) {
		if ("remove".equals(menuId)) {
			TableTabPortlet ttp = (TableTabPortlet) tab.getPortlet();
			this.getManager().showDialog("Remove Table Schema",
					new ConfirmDialogPortlet(generateConfig(), "Remove Table Schema", ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("REMOVE").setCorrelationData(ttp));
		}
	}

	@Override
	public WebMenu createMenu(TabPortlet tabPortlet, Tab tab) {
		BasicWebMenu r = new BasicWebMenu();
		r.addChild(new BasicWebMenuLink("Remove Table", true, "remove"));
		return r;
	}

	@Override
	public void onUserAddTab(TabPortlet tabPortlet) {
		this.getManager().showDialog("Add Table Schema",
				new ConfirmDialogPortlet(generateConfig(), "Add Table Schema", ConfirmDialogPortlet.TYPE_OK_CANCEL, this, new FormPortletTextField("Table: ")).setCallback("ADD"));
	}

	@Override
	public void onUserRenamedTab(TabPortlet tabPortlet, Tab tab, String newName) {
	}

	public void applyWarningAction(AmiWebSchemaComprisonWarning diff) {
		String name = diff.getTableSchemaName();
		switch (diff.getActionSelected()) {
			case AmiWebSchemaComprisonWarning.ACTION_STOP_WARNING:
				tabs.get(name).setOnChangedMode(AmiWebDmTableSchema.ON_CHANGE_IGNORE);
				break;
			case AmiWebSchemaComprisonWarning.ACTION_APPLY_DONT_ASK:
				updateSchema(name, diff.getTableSchemaNuw()).setOnChangedMode(AmiWebDmTableSchema.ON_CHANGE_APPLY);
				break;
			case AmiWebSchemaComprisonWarning.ACTION_APPLY:
				switch (diff.getType()) {
					case AmiWebSchemaComprisonWarning.TABLE_ONLY_IN_NUW: {
						addToSchema(name, diff.getTableSchemaNuw());
						break;
					}
					case AmiWebSchemaComprisonWarning.TABLE_ONLY_IN_OLD:
						removeFromSchema(name);
						break;
					case AmiWebSchemaComprisonWarning.MISMATCH:
						updateSchema(name, diff.getTableSchemaNuw()).setOnChangedMode(AmiWebDmTableSchema.ON_CHANGE_ASK);
						break;
				}
				break;
		}
	}

	private TableTabPortlet updateSchema(String name, AmiWebDmTableSchema schema) {
		TableTabPortlet tab = this.tabs.get(name);
		tab.setSchema(schema);
		return tab;
	}

	private TableTabPortlet removeFromSchema(String name) {
		TableTabPortlet old = this.tabs.remove(name);

		this.tabPortlet.removeChild(old.getPortletId());
		return old;
	}

	private TableTabPortlet addToSchema(String name, AmiWebDmTableSchema schema) {
		TableTabPortlet tab;
		tab = new TableTabPortlet(generateConfig(), name);
		tab.setSchema(schema);
		this.tabs.put(name, tab);
		this.tabPortlet.addChild(name, tab);
		getManager().onPortletAdded(tab);
		return tab;
	}

	public void addSchemaChangeListener(AmiWebEditSchemaPortletListener l) {
		this.listeners.add(l);
	}
	public void removeSchemaChangeListener(AmiWebEditSchemaPortletListener l) {
		this.listeners.remove(l);
	}

	public void applyWarningActions(ArrayList<AmiWebSchemaComprisonWarning> warnings) {
		for (AmiWebSchemaComprisonWarning i : warnings)
			applyWarningAction(i);
		onSchemaApplied();
	}

	public AmiWebDmTablesetSchema createSchema(AmiWebAmiScriptCallback owner) {
		AmiWebDmTablesetSchema r = new AmiWebDmTablesetSchema(this.service, owner);
		for (TableTabPortlet i : this.tabs.values()) {
			byte onChangesMode = i.getOnChangesMode();
			if (onChangesMode == ON_CHANGE_ASK_NEXT_TIME)
				onChangesMode = AmiWebDmTableSchema.ON_CHANGE_ASK;
			AmiWebDmTableSchema schema = new AmiWebDmTableSchema(i.getSchema(), r, onChangesMode);
			r.addTableSchema(i.tableName, schema);
		}
		r.lock();
		return r;
	}
	public boolean parseSchemas() {
		for (TableTabPortlet i : this.tabs.values()) {
			AmiWebDmTableSchema t = i.getSchema();
			if (t == null)
				return false;
		}
		return true;
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
			String callback = source.getCallback();
			if ("REMOVE".equals(callback)) {
				TableTabPortlet ttp = (TableTabPortlet) source.getCorrelationData();
				removeFromSchema(ttp.tableName);
				onSchemaApplied();
				PortletHelper.ensureVisible(this);
			} else if ("ADD".equals(callback)) {
				String table = (String) source.getInputFieldValue();
				if (!AmiUtils.isValidVariableName(table, false, false, false)) {
					getManager().showAlert("Table name not valid");
					return false;
				}
				if (this.tabs.containsKey(table)) {
					getManager().showAlert("Table already exists");
					return false;
				}
				addToSchema(table, new AmiWebDmTableSchema(service, table, new AmiWebDmTablesetSchema(service, owner.getCallback())));
				onSchemaApplied();
				PortletHelper.ensureVisible(this);
			}
		}
		return true;
	}

	public AmiWebService getService() {
		return service;
	}

}
