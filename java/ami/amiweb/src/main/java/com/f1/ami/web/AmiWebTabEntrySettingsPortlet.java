package com.f1.ami.web;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebTabEntrySettingsPortlet extends GridPortlet implements FormPortletContextMenuListener, FormPortletContextMenuFactory, ChooseDmListener, FormPortletListener {
	public static final Logger log = LH.get();
	final private FormPortlet form;
	private AmiWebDmTableSchema usedDm;
	final private FormPortletButtonField dmButton;
	final private FormPortletTextField nameField;
	final private AmiWebTabPortlet target;
	final private FormPortletButton cancelButton;
	final private FormPortletButton submitButton;
	final private AmiWebTabEntry tab;
	final private FormPortletTextField selectColorField;
	final private FormPortletTextField unselectColorField;
	final private FormPortletTextField selectTextColorField;
	final private FormPortletTextField unselectTextColorField;
	final private FormPortletTextField blinkColorField;
	final private FormPortletTextField blinkPeriodField;
	final private FormPortletCheckboxField hiddenField;
	private FormPortletTextField idField;

	public AmiWebTabEntrySettingsPortlet(PortletConfig config, AmiWebTabPortlet target, AmiWebTabEntry tab) {
		super(config);
		this.target = target;
		this.tab = tab;
		this.form = this.addChild(new FormPortlet(generateConfig()), 0, 0);
		this.form.getFormPortletStyle().setLabelsWidth(100);
		this.form.addField(new FormPortletTitleField("").setValue(""));
		this.form.addField(new FormPortletTitleField("").setValue("Tab Identifier:"));
		this.nameField = this.form.addField(new FormPortletTextField("Title:"));
		this.nameField.setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.nameField.setValue(tab.getNameFormula().getFormula(false));
		this.idField = this.form.addField(new FormPortletTextField("Tab Id:"));
		this.idField.setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.idField.setValue(tab.getId());
		this.hiddenField = this.form.addField(new FormPortletCheckboxField("Hide Tab:"));
		this.hiddenField.setValue(tab.getHidden(false));
		RootPortlet root = (RootPortlet) getManager().getRoot();
		int width = MH.min(AmiWebDesktopPortlet.MAX_WIDTH, (int) (root.getWidth() * 0.4));
		int height = MH.min(AmiWebDesktopPortlet.MAX_HEIGHT, (int) (root.getHeight() * 0.8));
		this.form.addField(new FormPortletTitleField("").setValue(""));
		this.form.addField(new FormPortletTitleField("").setValue("Select Color:"));
		this.selectColorField = this.form.addField(new FormPortletTextField("Background:"));
		this.selectColorField.setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.selectColorField.setValue(tab.getSelectColorFormula().getFormula(false));
		this.selectTextColorField = this.form.addField(new FormPortletTextField("Text:"));
		this.selectTextColorField.setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.selectTextColorField.setValue(tab.getSelectTextColorFormula().getFormula(false));

		this.form.addField(new FormPortletTitleField("").setValue(""));
		this.form.addField(new FormPortletTitleField("").setValue("Unselected Color:"));
		this.unselectColorField = this.form.addField(new FormPortletTextField("Background:"));
		this.unselectColorField.setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.unselectColorField.setValue(tab.getUnselectColorFormula().getFormula(false));
		this.unselectTextColorField = this.form.addField(new FormPortletTextField("Text:"));
		this.unselectTextColorField.setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.unselectTextColorField.setValue(tab.getUnselectTextColorFormula().getFormula(false));

		this.form.addField(new FormPortletTitleField("").setValue(""));
		this.form.addField(new FormPortletTitleField("").setValue("Tab Blink"));
		this.blinkColorField = form.addField(new FormPortletTextField("Color:").setWidth(FormPortletTextField.WIDTH_STRETCH));
		this.blinkColorField.setValue(tab.getBlinkColorFormula().getFormula(false));
		this.blinkPeriodField = form.addField(new FormPortletTextField("Period (millis):").setWidth(FormPortletTextField.WIDTH_STRETCH));
		this.blinkPeriodField.setValue(tab.getBlinkPeriodFormula().getFormula(false));

		this.form.addField(new FormPortletTitleField("").setValue(""));
		this.form.addField(new FormPortletTitleField("").setValue("Datamodel (For dynamic tab formatting):"));
		this.dmButton = form.addField(new FormPortletButtonField("")).setHeight(35);

		AmiWebDm dm = tab.getDmAliasDotName() == null ? null : target.getService().getDmManager().getDmByAliasDotName(tab.getDmAliasDotName());
		if (dm != null)
			this.usedDm = dm.getResponseOutSchema().getTable(tab.getDmTableName());

		updateDatamodelButton();

		form.addFormPortletListener(this);
		form.addMenuListener(this);
		form.setMenuFactory(this);
		this.submitButton = form.addButton(new FormPortletButton("Submit"));
		this.cancelButton = form.addButton(new FormPortletButton("Cancel"));
		this.nameField.setHasButton(true);
		this.selectColorField.setHasButton(true);
		this.unselectColorField.setHasButton(true);
		this.selectTextColorField.setHasButton(true);
		this.unselectTextColorField.setHasButton(true);
		this.blinkColorField.setHasButton(true);
		this.blinkPeriodField.setHasButton(true);
		setSuggestedSize(width, height);
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.dmButton) {
			String dmName = null;
			if (this.usedDm != null && this.usedDm.getDm() != null) {
				dmName = this.usedDm.getDm().getDmName();
			}
			AmiWebDmChooseDmTablePorlet t = new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.target.getAmiLayoutFullAlias());
			t.setAllowNoSelection(true);
			getManager().showDialog("Select Datamodel", t);
		} else if (node == this.nameField || node == this.selectColorField || node == this.unselectColorField || node == this.selectTextColorField || node == unselectTextColorField
				|| node == this.blinkColorField || node == blinkPeriodField) {
			AmiWebMenuUtils.processContextMenuAction(this.target.getService(), action, node);
		}

	}

	private void updateDatamodelButton() {
		if (usedDm != null) {
			dmButton.setValue(usedDm.getDm().getAmiLayoutFullAliasDotId() + " : " + usedDm.getName());
		} else {
			dmButton.setValue("&lt;No datamodel&gt;");
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.cancelButton == button) {
			ConfirmDialogPortlet.confirmAndCloseWindow(this, "Cancel without applying changes?");
		} else if (this.submitButton == button) {
			String dmId = this.usedDm == null ? null : this.usedDm.getDm().getAmiLayoutFullAliasDotId();
			String dmTableName = this.usedDm == null ? null : this.usedDm.getName();
			if (SH.isnt(nameField.getValue())) {
				getManager().showAlert("Tab Title required");
				this.nameField.focus();
				return;
			}
			StringBuilder errorsink = new StringBuilder();
			if (!this.target.testFormula(dmId, this.tab, dmTableName, this.nameField.getValue(), errorsink, Object.class, AmiWebTabPortlet.FORMULA_TITLE)) {
				getManager().showAlert("Tab Title is invalid: " + errorsink);
				this.nameField.focus();
				return;
			}
			if (!this.target.testFormula(dmId, this.tab, dmTableName, this.selectColorField.getValue(), errorsink, String.class, AmiWebTabPortlet.FORMULA_SELECT_COLOR)) {
				getManager().showAlert("Background Select color invalid: " + errorsink);
				this.selectColorField.focus();
				return;
			}
			if (!this.target.testFormula(dmId, this.tab, dmTableName, this.unselectColorField.getValue(), errorsink, String.class, AmiWebTabPortlet.FORMULA_UNSELECT_COLOR)) {
				getManager().showAlert("Background Unselect color invalid: " + errorsink);
				this.unselectColorField.focus();
				return;
			}
			if (!this.target.testFormula(dmId, this.tab, dmTableName, this.selectTextColorField.getValue(), errorsink, String.class, AmiWebTabPortlet.FORMULA_SELECT_TEXT_COLOR)) {
				getManager().showAlert("Text Select color invalid: " + errorsink);
				this.selectTextColorField.focus();
				return;
			}
			if (!this.target.testFormula(dmId, this.tab, dmTableName, this.unselectTextColorField.getValue(), errorsink, String.class,
					AmiWebTabPortlet.FORMULA_UNSELECT_TEXT_COLOR)) {
				getManager().showAlert("Text Unselect color invalid: " + errorsink);
				this.unselectTextColorField.focus();
				return;
			}
			if (!this.target.testFormula(dmId, this.tab, dmTableName, this.blinkColorField.getValue(), errorsink, String.class, AmiWebTabPortlet.FORMULA_BLINK_COLOR)) {
				getManager().showAlert("Tab blink color invalid: " + errorsink);
				this.blinkColorField.focus();
				return;
			}
			if (!this.target.testFormula(dmId, this.tab, dmTableName, this.blinkPeriodField.getValue(), errorsink, Integer.class, AmiWebTabPortlet.FORMULA_BLINK_PERIOD)) {
				getManager().showAlert("Tab blink period is invalid: " + errorsink);
				this.blinkPeriodField.focus();
				return;
			}
			String id = SH.trim(this.idField.getValue());
			if (OH.ne(this.tab.getId(), id)) {
				if (!AmiUtils.isValidVariableName(id, false, false)) {
					getManager().showAlert("Tab Id must be valid alpha-numeric variable name");
					this.idField.focus();
					return;
				}
				if (this.target.getTabById(id) != null) {
					getManager().showAlert("Tab Id already exists for another tab");
					this.idField.focus();
					return;
				}
			}

			this.tab.getSelectColorFormula().setFormula(this.selectColorField.getValue(), false);
			this.tab.getUnselectColorFormula().setFormula(this.unselectColorField.getValue(), false);
			this.tab.getSelectTextColorFormula().setFormula(this.selectTextColorField.getValue(), false);
			this.tab.getUnselectTextColorFormula().setFormula(this.unselectTextColorField.getValue(), false);
			this.tab.getBlinkColorFormula().setFormula(this.blinkColorField.getValue(), false);
			this.tab.getBlinkPeriodFormula().setFormula(this.blinkPeriodField.getValue(), false);
			this.tab.getNameFormula().setFormula(this.nameField.getValue(), false);
			this.tab.setId(id);
			this.tab.setDmId(dmId, dmTableName);
			this.tab.setHidden(this.hiddenField.getValue(), false);
			this.target.updateTab(this.tab);
			close();
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		this.usedDm = selectedDmTable;
		updateDatamodelButton();
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();

		AmiWebDmTableSchema dm = this.usedDm;
		com.f1.base.CalcTypes types = dm == null ? EmptyCalcTypes.INSTANCE : dm.getClassTypes();
		AmiWebMenuUtils.createVariablesMenu(r, false, this.target);

		if (field == this.selectColorField || field == this.unselectColorField || field == this.selectTextColorField || field == this.unselectTextColorField
				|| field == this.blinkColorField)
			AmiWebMenuUtils.createColorsMenu(r, this.target.getStylePeer());
		AmiWebMenuUtils.createOperatorsMenu(r, this.target.getService(), this.target.getAmiLayoutFullAlias());
		return r;
	}
}
