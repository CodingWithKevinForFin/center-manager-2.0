package com.f1.ami.web;

import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebEditVarPortlet extends GridPortlet implements FormPortletListener {

	private FormPortlet editVarForm;
	private FormPortletTextField nameField;
	private AmiWebFormPortletAmiScriptField valueField;
	private FormPortletButton submitButton;
	private FormPortletButton cancelButton;
	private AmiWebVarsTablePortlet varsTable;
	private boolean isAdd;
	private FormPortletSelectField<String> layoutField;
	private String origName;
	private String origLayout;

	public AmiWebEditVarPortlet(PortletConfig config, String layout, String name, String script, boolean isAdd, AmiWebVarsTablePortlet amiWebVarsTablePortlet) {
		super(config);
		this.editVarForm = new FormPortlet(generateConfig());
		this.nameField = this.editVarForm.addField(new FormPortletTextField("Name:"));
		this.nameField.setLeftPosPx(105);
		this.nameField.setTopPosPx(50);
		this.nameField.setRightPosPx(20);
		this.nameField.setHeightPx(20);
		this.nameField.setValue(name);
		this.layoutField = this.editVarForm.addField(new FormPortletSelectField<String>(String.class, "Owning Layout:"));
		this.layoutField.setLeftPosPx(105);
		this.layoutField.setTopPosPx(20);
		this.layoutField.setRightPosPx(20);
		this.layoutField.setHeightPx(20);
		AmiWebService service = AmiWebUtils.getService(this.getManager());
		for (String s : service.getLayoutFilesManager().getFullAliasesByPriority())
			this.layoutField.addOption(s, "".equals(s) ? "<root>" : s);
		if (!isAdd && service.getLayoutFilesManager().getLayoutByFullAlias(layout).isReadonly()) {
			this.layoutField.setDisabled(true);
		}
		this.layoutField.setValue(layout);
		this.valueField = this.editVarForm.addField(new AmiWebFormPortletAmiScriptField("Value:", this.getManager(), ""));
		this.valueField.setLeftTopRightBottom(105, 100, 20, 60);
		this.valueField.setValue(script);
		this.isAdd = isAdd;
		this.origName = name;
		this.origLayout = layout;
		this.editVarForm.addFormPortletListener(this);
		this.addChild(this.editVarForm);
		this.submitButton = this.editVarForm.addButton(new FormPortletButton("Submit"));
		this.cancelButton = this.editVarForm.addButton(new FormPortletButton("Cancel"));
		this.setSuggestedSize(500, 500);
		this.varsTable = amiWebVarsTablePortlet;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			close();
		else if (button == this.submitButton) {
			String name = SH.trim(this.nameField.getValue());
			String layout = layoutField.getValue();
			AmiWebService service = AmiWebUtils.getService(this.getManager());
			AmiWebScriptManagerForLayout sm = service.getScriptManager(layout);
			if (service.getLayoutFilesManager().getLayoutByFullAlias(layout).isReadonly()) {
				getManager().showAlert("Can not submit variable, layout is readonly");
				return;
			}
			String amiscript = this.valueField.getValue();
			if (!AmiUtils.isValidVariableName(name, false, false)) {
				getManager().showAlert("Name has invalid syntax");
				return;
			}
			if (isAdd) {
				if (sm.getLayoutVariableScripts().containsKey(name)) {
					getManager().showAlert("Variable name already defined: " + name);
					return;
				}
			}
			StringBuilder errorSink = new StringBuilder();
			if (!sm.putLayoutVariableScript(name, amiscript, errorSink)) {
				getManager().showAlert("Value is not valid ami script: " + errorSink);
				return;
			}
			if (!isAdd && (OH.ne(this.origName, name) || OH.ne(this.origLayout, layout))) {
				service.getScriptManager(origLayout).removeLayoutVariable(origName);
				this.varsTable.rebuildTable(true);
			} else
				this.varsTable.rebuildTable(true);
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
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		return true;
	}
	public void setScript(String text) {
		this.valueField.setValue(text);
	}
	public void setKey(String name) {
		this.nameField.setValue(name);
	}

}
