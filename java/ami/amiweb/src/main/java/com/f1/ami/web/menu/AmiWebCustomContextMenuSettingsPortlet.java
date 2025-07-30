package com.f1.ami.web.menu;

import java.util.Map;

import com.f1.ami.web.AmiWebEditAmiScriptCallbacksPortlet;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSpecialPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.FormExportPortlet;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.BasicFormPortletExportImportManager;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebCustomContextMenuSettingsPortlet extends GridPortlet
		implements FormPortletListener, FormPortletContextMenuFactory, FormPortletContextMenuListener, AmiWebSpecialPortlet, ConfirmDialogListener {
	private final FormPortletTextField idField;
	private final FormPortletTextField displayField;
	private final FormPortletSelectField<String> parentField;
	private final FormPortletSelectField<Integer> positionField;
	//	private final AmiWebFormPortletAmiScriptField amiscriptField;
	private final FormPortletToggleButtonsField<Boolean> boldField;
	private final FormPortletToggleButtonsField<Boolean> italicField;
	private final FormPortletToggleButtonsField<Boolean> underlineField;
	private final FormPortletTextField statusField;
	private final FormPortletTextField iconField;

	private final FormPortletButton submitButton;
	private final FormPortletButton cancelButton;
	private final FormPortletButton importerExporter;

	private final AmiWebCustomContextMenu item;
	private final AmiWebCustomContextMenuManager menu;
	private final AmiWebService service;
	private FormPortlet form;
	private FormPortlet buttonForm;
	private TabPortlet tabs;
	private AmiWebEditAmiScriptCallbacksPortlet callbacks;
	private boolean isEdit;
	private String oldParentId;
	private String oldId;
	private String originalEditAri;

	final private static String MENU_SETTING_ID = AmiWebCustomContextMenu.PARAM_ID;
	final private static String MENU_SETTING_DISPLAY = AmiWebCustomContextMenu.PARAM_DISPLAY;
	final private static String MENU_SETTING_PATH = AmiWebCustomContextMenu.PARAM_PATH;
	final private static String MENU_SETTING_BOLD = AmiWebCustomContextMenu.PARAM_BOLD;
	final private static String MENU_SETTING_ITALIC = AmiWebCustomContextMenu.PARAM_ITALIC;
	final private static String MENU_SETTING_UNDERLINE = AmiWebCustomContextMenu.PARAM_UNDERLINE;
	final private static String MENU_SETTING_STATUS = AmiWebCustomContextMenu.PARAM_STATUS;
	final private static String MENU_SETTING_ICON = AmiWebCustomContextMenu.PARAM_ICON;
	private Tab settingsTab;
	private Tab callbacksTab;

	public AmiWebCustomContextMenuSettingsPortlet(PortletConfig config, AmiWebCustomContextMenu item, AmiWebCustomContextMenuManager manager) {
		super(config);
		this.service = AmiWebUtils.getService(getManager());
		this.isEdit = item != null;
		this.menu = manager;
		if (isEdit) {
			this.oldParentId = item.getParentId();
			this.oldId = item.getId();
			this.item = item;
		} else {
			this.item = new AmiWebCustomContextMenu(menu, false);
			this.item.setParentId("");
			this.item.setParent(this.menu.getRootMenu());
			this.item.setId(this.menu.generateNextId("MENU1"));
			this.item.getStatusFormula().setFormula("\"enabled\"", false);
			this.item.getDisplayFormula().setFormula("\"" + this.item.getId() + "\"", false);
			this.oldParentId = this.item.getParentId();
			this.oldId = this.item.getId();
			this.menu.addChild(this.item);
		}
		this.originalEditAri = this.item.getAri();
		this.form = new FormPortlet(generateConfig());
		this.buttonForm = new FormPortlet(generateConfig());
		this.tabs = new TabPortlet(generateConfig());
		this.callbacks = new AmiWebEditAmiScriptCallbacksPortlet(generateConfig(), this.item.getAmiScript());
		this.addChild(buttonForm, 0, 1);
		this.settingsTab = tabs.addChild("Settings", this.form);
		this.callbacksTab = tabs.addChild("Callbacks", this.callbacks);
		this.addChild(tabs, 0, 0);
		this.setRowSize(1, this.buttonForm.getButtonPanelHeight());

		int fieldsRightPosPx = 80;
		this.parentField = form.addField(new FormPortletSelectField<String>(String.class, "Parent Menu:")).setName(MENU_SETTING_PATH);
		StringBuilder buf = new StringBuilder();
		for (AmiWebCustomContextMenu i : this.menu.getChildren(true)) {
			if (this.item != null && (i == this.item || this.item.isNestedChild(i)))
				continue;
			this.parentField.addOption(i.getId(), i.getPathDescription(SH.clear(buf)).toString());
		}
		this.parentField.setValue(this.item.getParentId());
		this.parentField.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
		this.parentField.setRightPosPx(fieldsRightPosPx);
		this.positionField = form.addField(new FormPortletSelectField<Integer>(Integer.class, "Position:")).setName(MENU_SETTING_PATH);
		this.positionField.setWidth(350);
		this.idField = form.addField(new FormPortletTextField("Menu Id:")).setName(MENU_SETTING_ID);
		this.idField.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
		this.idField.setRightPosPx(fieldsRightPosPx);
		this.idField.setValue(this.item.getId());
		this.displayField = form.addField(new FormPortletTextField("Display:")).setHasButton(true).setName(MENU_SETTING_DISPLAY);
		this.displayField.setValue(this.item.getDisplayFormula().getFormula(false));
		this.displayField.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
		this.displayField.setRightPosPx(fieldsRightPosPx);
		this.displayField.setCssStyle("_fm=courier");
		//		this.callbacks.setCallbacks(this.item.getAmiScript()); //redundant from AmiWebEditAmiScriptCallbacksPortlet constructor
		this.boldField = form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "")).setName(MENU_SETTING_BOLD);
		this.boldField.addOption(false, "Normal");
		this.boldField.addOption(true, "Bold", "style.fontWeight=bold");
		this.boldField.setValue(this.item.isBold());
		this.italicField = form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "")).setName(MENU_SETTING_ITALIC);
		this.italicField.addOption(false, "Normal");
		this.italicField.addOption(true, "Italic", "style.fontStyle=italic");
		this.italicField.setValue(this.item.isItalic());
		this.underlineField = form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "")).setName(MENU_SETTING_UNDERLINE);
		this.underlineField.addOption(false, "Normal");
		this.underlineField.addOption(true, "Underline", "style.textDecoration=underline");
		this.underlineField.setValue(this.item.isUnderline());
		this.statusField = form.addField(new FormPortletTextField("Status:")).setHasButton(true).setName(MENU_SETTING_STATUS);
		this.statusField.setValue(this.item.getStatusFormula().getFormula(false));
		this.statusField.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
		this.statusField.setRightPosPx(fieldsRightPosPx);
		this.statusField.setCssStyle("_fm=courier");
		this.iconField = form.addField(new FormPortletTextField("Icon:")).setHasButton(true).setName(MENU_SETTING_ICON);
		this.iconField.setValue(this.item.getIconFormula().getFormula(false));
		this.iconField.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
		this.iconField.setRightPosPx(fieldsRightPosPx);
		this.iconField.setCssStyle("_fm=courier");

		this.submitButton = buttonForm.addButton(new FormPortletButton((item == null ? "Add" : "Update") + " Item"));
		this.cancelButton = buttonForm.addButton(new FormPortletButton("Cancel"));
		this.importerExporter = buttonForm.addButton(new FormPortletButton("Import/Export"));

		form.addFormPortletListener(this);
		buttonForm.addFormPortletListener(this);
		form.addMenuListener(this);
		form.setMenuFactory(this);
		onFieldValueChanged(this.form, this.parentField, null);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {

			// Ensure field has ID
			String id = SH.trim(this.idField.getValue());
			if (SH.isnt(id)) {
				getManager().showAlert("Please enter an ID for this field item.");
				return;
			} else if (OH.ne(this.item.getId(), id) && this.menu.getMenuNoThrow(id) != null) { // Check for duplicate id
				getManager().showAlert("Item id <B>" + id + "</B> already exists in this menu.");
				return;
			} else {
				this.item.setId(id);
			}

			// Evaluate display, status, and icon; check if they are valid expressions
			String display = this.displayField.getValue();
			String status = this.statusField.getValue();
			String icon = this.iconField.getValue();
			Map<String, String> evaluatedParams = CH.m(AmiWebCustomContextMenu.FORMULA_DISPLAY, display, AmiWebCustomContextMenu.FORMULA_STATUS, status,
					AmiWebCustomContextMenu.FORMULA_ICON, icon);
			for (String k : evaluatedParams.keySet()) {
				Exception e = this.item.testParam(evaluatedParams.get(k), k);
				if (e != null) {
					getManager().showAlert("Error evaluating <B>" + k + "</B> expression: <BR><B>" + e.getMessage() + "</B>", e);
					return;
				}
			}
			if (SH.isnt(status)) {
				getManager().showAlert("Field status cannot be blank. Please enter an expression that will evaluate to a valid field status.");
				return;
			}

			if (!this.callbacks.apply())
				return;
			this.item.getDisplayFormula().setFormula(this.displayField.getValue(), false);
			this.callbacks.applyTo(this.item.getAmiScript(), null);
			this.item.setBold(this.boldField.getValue());
			this.item.setItalic(this.italicField.getValue());
			this.item.setUnderline(this.underlineField.getValue());
			this.item.getStatusFormula().setFormula(status, false);
			this.item.getIconFormula().setFormula(this.iconField.getValue(), false);
			this.item.setParentId(this.parentField.getValue());
			this.item.setPosition(this.positionField.getValue());
			//			if (isEdit) {
			this.menu.updateChild(this.oldParentId, this.oldId, this.item);
			//			} else
			//				this.menu.addChild(this.item);
			close();
		} else if (button == this.cancelButton) { // restore original settings
			if (this.isEdit || this.callbacks.hasChanged()) {
				ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to revert changes made?", ConfirmDialogPortlet.TYPE_YES_NO, this);
				cdp.setCallback("CLOSE");
				getManager().showDialog("Revert Changes", cdp);
			} else {
				menu.removeMenu(item);
				closeMe();
			}

		} else if (button == this.importerExporter) {
			getManager().showDialog("Export/Import", new FormExportPortlet(this.form, new BasicFormPortletExportImportManager(null), false));
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.parentField) {
			AmiWebCustomContextMenu parent = this.menu.getMenu(this.parentField.getValue());
			this.positionField.clearOptions();
			final int existingPos, n;
			if (this.isEdit) {
				n = parent.getChildrenCount();
				existingPos = this.item.getPosition();
			} else {
				n = parent.getChildrenCount() + 1;
				existingPos = Integer.MAX_VALUE;
			}
			for (int i = 0; i < n; i++) {
				String s = (i + 1) + ") ";
				if (i == 0)
					s += " Top";
				else if (i == n - 1)
					s += " Bottom";
				else if (i == existingPos)
					s += "Between " + parent.getChildItemAt(i - 1).getId() + " and " + parent.getChildItemAt(i + 1).getId();
				else if (i < existingPos)
					s += "Between " + parent.getChildItemAt(i - 1).getId() + " and " + parent.getChildItemAt(i).getId();
				else
					s += "Between " + parent.getChildItemAt(i).getId() + " and " + parent.getChildItemAt(i + 1).getId();

				if (i == existingPos)
					s += " (current)";
				this.positionField.addOption(i, s);
			}
			this.positionField.setValueNoThrow(this.item.getPosition());
		}

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey())) {
			return true;
		}
		return super.onUserKeyEvent(keyEvent);
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.iconField || node == this.statusField || node == this.displayField) {
			AmiWebMenuUtils.processContextMenuAction(this.service, action, node);
		}
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		if (field == this.statusField || field == this.iconField || field == this.displayField) {
			if (field == this.statusField)
				r.add(createCustomContextMenuItemStatusMenu());
			if (field == this.iconField)
				r.add(AmiWebMenuUtils.createIconsMenu(false));
			AmiWebMenuUtils.createOperatorsMenu(r, this.service, this.menu.getAmiLayoutFullAlias());
		}
		return r;
	}
	public static WebMenu createCustomContextMenuItemStatusMenu() {
		WebMenu r = new BasicWebMenu("Status", true);
		String varPrefix = "co_";
		r.add(new BasicWebMenuLink("Enabled", true, varPrefix + AmiWebCustomContextMenu.STATUS_ENABLED));
		r.add(new BasicWebMenuLink("Disabled", true, varPrefix + AmiWebCustomContextMenu.STATUS_DISABLED));
		r.add(new BasicWebMenuLink("Invisible", true, varPrefix + AmiWebCustomContextMenu.STATUS_INVISIBLE));
		r.add(new BasicWebMenuLink("Divider", true, varPrefix + AmiWebCustomContextMenu.STATUS_DIVIDER));
		return r;
	}
	public String getEditedAri() {
		return this.originalEditAri;
	}

	public void showEditCallback(String callback) {
		this.tabs.selectTab(this.callbacksTab.getLocation());
		this.callbacks.setActiveTab(callback);
	}

	public AmiWebEditAmiScriptCallbacksPortlet getCallbacksEditor() {
		return this.callbacks;
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("CLOSE".contentEquals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.contentEquals(id))
				closeMe();
		}
		return true;
	}

	private void closeMe() {
		if (this.isEdit == false)
			this.item.close();
		close();
	}

	public void showFormulasEditor() {
		this.tabs.setActiveTab(this.form);
	}
}
