package com.f1.ami.web.menu;

import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebCustomContextMenuImportPortlet extends GridPortlet implements FormPortletListener {

	final private AmiWebCustomContextMenuManager menu;
	final private FormPortletButton submitButton = new FormPortletButton("Submit");
	final private FormPortlet form;
	final private FormPortletTextAreaField textAreaField = new FormPortletTextAreaField("");
	private FormPortletSelectField<String> parentField;
	private FormPortletSelectField<Integer> positionField;

	public AmiWebCustomContextMenuImportPortlet(PortletConfig config, AmiWebCustomContextMenuManager target) {
		super(config);
		this.menu = target;
		this.form = addChild(new FormPortlet(generateConfig()), 0, 0);
		this.parentField = form.addField(new FormPortletSelectField<String>(String.class, "Parent Menu:"));
		StringBuilder buf = new StringBuilder();
		for (AmiWebCustomContextMenu i : this.menu.getChildren(true)) {
			this.parentField.addOption(i.getId(), i.getPathDescription(SH.clear(buf)).toString());
		}
		this.positionField = form.addField(new FormPortletSelectField<Integer>(Integer.class, "Position:"));
		this.form.addField(this.textAreaField);

		this.textAreaField.setTopPosPx(80);
		this.textAreaField.setBottomPosPx(55);
		this.textAreaField.setLeftPosPx(15);
		this.textAreaField.setRightPosPx(15);
		this.form.addButton(this.submitButton);
		this.form.addButton(new FormPortletButton("Cancel"));
		this.form.addFormPortletListener(this);
		this.onFieldValueChanged(form, this.parentField, null);
		setSuggestedSize(400, 500);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			String configText = this.textAreaField.getValue();
			try {
				List<Map<String, Object>> config = (List<Map<String, Object>>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(configText);
				menu.importConfig(this.parentField.getValue(), this.positionField.getValue(), config);
				close();
			} catch (Exception e) {
				getManager().showAlert("Error importing custom menu. See More for details.", e);
			}
		} else {
			close();
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.parentField) {
			AmiWebCustomContextMenu parent = this.menu.getMenu(this.parentField.getValue());
			int n = parent.getChildrenCount();
			this.positionField.clearOptions();
			this.positionField.addOption(0, "1) Top");
			for (int i = 1; i < n; i++)
				this.positionField.addOption(i, (i + 1) + ") Between " + parent.getChildItemAt(i - 1).getId() + " and " + parent.getChildItemAt(i).getId());
			if (n > 1)
				this.positionField.addOption(n, (n + 1) + ") Bottom");
		}
	}

}
