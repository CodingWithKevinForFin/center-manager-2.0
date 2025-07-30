package com.f1.ami.web;

import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.cloud.AmiWebCloudLayoutTree;
import com.f1.ami.web.cloud.AmiWebCloudManager;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.SH;

public class AmiWebExportToCloudPortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener {

	private final AmiWebService service;
	private final FormPortlet fp;
	private final FormPortletSelectField<String> locationsField;
	private final String configText;
	private final AmiWebCloudManager cloudManager;
	private final String defaultName;
	private final AmiWebCloudLayoutTree layouts;
	private final FormPortletTextField textField;
	private final FormPortletTextField newLocationField;
	private FormPortletButton cxlButton;
	private FormPortletButton okayButton;

	public AmiWebExportToCloudPortlet(PortletConfig config, String configText, AmiWebCloudManager cloudManager, String defaultName, AmiWebService service) {
		super(config);

		AmiWebHeaderPortlet header = new AmiWebHeaderPortlet(generateConfig());
		header.setShowSearch(false);
		header.updateBlurbPortletLayout("Publish to Cloud", "");
		header.setShowLegend(false);
		header.setInformationHeaderHeight(60);
		header.setShowBar(false);
		addChild(header);

		this.service = service;
		this.configText = configText;
		this.cloudManager = cloudManager;
		this.defaultName = defaultName;
		this.layouts = cloudManager.getCloudLayouts();
		addChild(fp = new FormPortlet(generateConfig()), 0, 1);
		fp.addField(new FormPortletTitleField("Location:")).setHeight(10);
		fp.addField(new FormPortletTitleField("")).setHeight(5);
		fp.addField(locationsField = new FormPortletSelectField<String>(String.class, ""));
		addFieldsFromLocation("", layouts);
		fp.addField(new FormPortletTitleField(" "));
		newLocationField = new FormPortletTextField("New Location: ");
		locationsField.addOption(null, "<New Location>");

		fp.addField(new FormPortletTitleField("Layout Name:")).setHeight(20);
		fp.addField(textField = new FormPortletTextField("")).setWidth(200);
		textField.focus();
		fp.addFormPortletListener(this);
		this.okayButton = fp.addButton(new FormPortletButton("Save"));
		this.cxlButton = fp.addButton(new FormPortletButton("Cancel"));
		if (SH.is(defaultName)) {
			String loc = SH.beforeLast(defaultName, ':', "");
			if (!locationsField.setValueNoThrow(loc)) {
				locationsField.setValue(null);
				newLocationField.setValue(loc);
			}
			textField.setValue(SH.afterLast(defaultName, ':', defaultName));
		}
		onFieldValueChanged(fp, locationsField, null);
	}

	private void addFieldsFromLocation(String prefix, AmiWebCloudLayoutTree layouts) {
		String name = layouts.getName();
		if (!prefix.equals(""))
			name = prefix + AmiWebCloudManager.SEPERATOR + name;
		locationsField.addOption(name, "".equals(name) ? "<root>" : name);
		for (Entry<String, AmiWebCloudLayoutTree> i : layouts.getChildren().entrySet()) {
			addFieldsFromLocation(name, i.getValue());
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == cxlButton)
			close();
		else if (button == okayButton) {
			String name = getPathAndName();
			if (name == null) {
				getManager().showAlert("Name field required");
				return;
			}

			String existing = this.cloudManager.loadLayout(name);
			if (existing != null) {
				String message = "Overwrite existing file '<B>" + getPathAndName() + "</B>'?";
				ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), message, ConfirmDialogPortlet.TYPE_YES_NO);
				cdp.addDialogListener(this);
				getManager().showDialog("File exists", cdp);
			} else
				saveAndClose();
		}
	}

	private String getPathAndName() {
		String name = SH.trim(textField.getValue());
		if (SH.isnt(name)) {
			return null;
		}
		String loc = locationsField.getValue();
		if (loc == null)
			loc = SH.trim(newLocationField.getValue());
		if (SH.is(loc))
			name = loc + AmiWebCloudManager.SEPERATOR + name;
		return name;
	}

	private void saveAndClose() {
		String name = getPathAndName();
		if (name == null) {
			getManager().showAlert("Name field required");
			return;
		}
		this.service.getLayoutFilesManager().saveLayoutAs(name, AmiWebConsts.LAYOUT_SOURCE_CLOUD);
		this.service.getLayoutFilesManager().setCurrentLayoutName(name, AmiWebConsts.LAYOUT_SOURCE_CLOUD);
		getManager().showAlert("Layout Saved: " + name);
		close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.locationsField) {
			if (this.locationsField.getValue() == null)
				fp.addFieldAfter(locationsField, newLocationField);
			else
				fp.removeFieldNoThrow(newLocationField);
		}

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == SH.BYTE_ENTER_KEY)
			onButtonPressed(fp, this.okayButton);
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
			saveAndClose();
		}
		return true;
	}

}
