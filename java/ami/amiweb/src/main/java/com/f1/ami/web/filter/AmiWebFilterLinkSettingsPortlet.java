package com.f1.ami.web.filter;

import java.util.Map;

import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;

public class AmiWebFilterLinkSettingsPortlet extends GridPortlet implements FormPortletListener, ChooseDmListener, FormPortletContextMenuListener, FormPortletContextMenuFactory {

	private FormPortlet form;
	private FormPortletButtonField dmButton;
	private FormPortletTextField formulaField;
	private int linkId;
	private AmiWebDmTableSchema usedDm;
	private AmiWebFilterSettingsPortlet settingsPortlet;
	private FormPortletButton deleteButton;
	private FormPortletButton closeButton;
	private FormPortletButton cancelButton;

	public AmiWebFilterLinkSettingsPortlet(PortletConfig config, AmiWebFilterSettingsPortlet settingsPortlet, int linkId, String dmAliasDotName, String dmTableName,
			String formula) {
		super(config);
		this.form = new FormPortlet(generateConfig());
		addChild(form);
		this.linkId = linkId;
		this.settingsPortlet = settingsPortlet;
		form.addField(new FormPortletTitleField("Target Data Model:"));
		dmButton = form.addField(new FormPortletButtonField("")).setHeight(35);
		form.addField(new FormPortletTitleField("Show where:"));
		this.formulaField = this.form.addField(new FormPortletTextField("Formula:").setValue(formula)).setWidth(FormPortletField.WIDTH_STRETCH);
		formulaField.setHasButton(true);
		form.addFormPortletListener(this);
		form.addMenuListener(this);
		form.setMenuFactory(this);
		if (linkId != -1)
			deleteButton = form.addButton(new FormPortletButton("Delete"));
		closeButton = form.addButton(new FormPortletButton("Submit"));
		cancelButton = form.addButton(new FormPortletButton("Cancel"));
		AmiWebDm dm = this.settingsPortlet.getTarget().getService().getDmManager().getDmByAliasDotName(dmAliasDotName);
		if (dm != null) {
			this.usedDm = dm.getResponseOutSchema().getTable(dmTableName);
		}
		setSuggestedSize(500, 300);
		updateDatamodelButton();
	}

	private void updateDatamodelButton() {
		if (usedDm != null) {
			dmButton.setValue(usedDm.getDm().getAmiLayoutFullAliasDotId() + " : " + usedDm.getName());
		} else {
			dmButton.setValue("&lt;No datamodel&gt;");
		}
	}
	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		this.usedDm = selectedDmTable;
		updateDatamodelButton();
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.formulaField) {
			AmiWebFilterPortlet target = this.settingsPortlet.getTarget();
			AmiWebMenuUtils.processContextMenuAction(target.getService(), action, node);
		} else if (node == this.dmButton) {
			String dmName = null;
			if (this.usedDm != null && this.usedDm.getDm() != null) {
				dmName = this.usedDm.getDm().getDmName();
			}
			AmiWebDmChooseDmTablePorlet t = new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.settingsPortlet.getTarget().getAmiLayoutFullAlias());
			getManager().showDialog("Select Datamodel", t);
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton) {
			close();
		} else if (button == this.deleteButton) {
			this.settingsPortlet.removeLink(this.linkId);
			close();
		} else {
			StringBuilder errorSink = new StringBuilder();
			if (!this.settingsPortlet.updateLink(this.linkId, this.usedDm, this.formulaField.getValue(), errorSink)) {
				this.getManager().showAlert(errorSink.toString());
				return;
			}
			this.close();
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		AmiWebFilterPortlet target = this.settingsPortlet.getTarget();
		AmiWebMenuUtils.createOperatorsMenu(r, target.getService(), target.getAmiLayoutFullAlias());
		r.add(new BasicWebMenuDivider());
		r.add(AmiWebMenuUtils.createVariablesMenu(target.getService(), "Source Variables", "Source_", target, this.settingsPortlet.getDm().getDm().getAmiLayoutFullAliasDotId(),
				this.settingsPortlet.getDm().getName()));
		if (this.usedDm != null)
			r.add(AmiWebMenuUtils.createVariablesMenu(target.getService(), "Target Variables", "Target_", target, this.usedDm.getDm().getAmiLayoutFullAliasDotId(),
					this.usedDm.getName()));
		else {
			r.add(new BasicWebMenu("Variables", true, new BasicWebMenuLink("(Select underlying datamodel first)", false, "")));
		}
		return r;
	}
}
