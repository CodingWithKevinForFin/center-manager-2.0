package com.f1.ami.web.scm;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.web.AmiWebFormPortletAmiScriptField;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.Form;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public class AmiWebScmEditorPortlet extends GridPortlet implements FormPortletListener {

	final private FormPortlet form;
	final private FormPortletDivField titleField;
	final private AmiWebFormPortletAmiScriptField textField;
	final private String localFileName;
	final private FormPortletButtonField saveButton;
	final private byte status;
	private boolean isSaved = true;
	private Tab tab;
	final private String name;
	private AmiWebScmBasePortlet owner;

	public AmiWebScmEditorPortlet(PortletConfig config, AmiWebScmBasePortlet owner, String name, String localFileName, String text, byte status) {
		super(config);
		this.owner = owner;
		this.name = name;
		this.form = this.addChild(new FormPortlet(generateConfig()), 0, 0);
		this.titleField = this.form.addField(new FormPortletDivField(""));
		this.textField = this.form.addField(new AmiWebFormPortletAmiScriptField("", this.getManager(), ""));
		this.saveButton = new FormPortletButtonField("").setValue("save (Alt+enter)");
		this.titleField.setTopPosPx(0).setLeftPosPx(0).setRightPosPx(0).setHeightPx(20);
		this.textField.setTopPosPx(20).setLeftPosPx(0).setRightPosPx(0).setBottomPosPx(25);
		this.saveButton.setRightPosPx(20).setWidthPx(150).setBottomPosPx(1).setHeightPx(20);
		this.form.addFormPortletListener(this);
		this.localFileName = localFileName;
		this.titleField.setValue(name);
		this.textField.setValue(text);
		this.status = status;
		switch (status) {
			case AmiScmAdapter.STATUS_HISTORY:
				this.textField.setDisabled(true);
				this.textField.setBgColor("#DDDDDD");
				break;
			case AmiScmAdapter.STATUS_DIRECTORY:
			case AmiScmAdapter.STATUS_MARKED_FOR_DELETE:
				this.textField.setDisabled(true);
				break;
			case AmiScmAdapter.STATUS_PRIVATE:
			case AmiScmAdapter.STATUS_MARKED_FOR_ADD:
			case AmiScmAdapter.STATUS_CHECKED_OUT:
				this.textField.setDisabled(false);
				break;
			case AmiScmAdapter.STATUS_CHECKED_IN:
				this.textField.setDisabled(false);
				break;
		}

	}
	public String getLocalFileName() {
		return this.localFileName;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.saveButton) {
			save();
		} else if (field == this.textField) {
			if (isSaved) {
				switch (status) {
					case AmiScmAdapter.STATUS_HISTORY:
					case AmiScmAdapter.STATUS_DIRECTORY:
					case AmiScmAdapter.STATUS_MARKED_FOR_DELETE:
						throw new IllegalStateException("should be disabled");
					case AmiScmAdapter.STATUS_PRIVATE:
					case AmiScmAdapter.STATUS_MARKED_FOR_ADD:
					case AmiScmAdapter.STATUS_CHECKED_OUT:
						setSaved(false);
						break;
					case AmiScmAdapter.STATUS_CHECKED_IN:
						if (owner.openForEdit(CH.l(this.name))) {
							setSaved(false);
						}
						break;
				}
			}
		}
	}
	private void setSaved(boolean isSaved) {
		if (this.isSaved == isSaved)
			return;
		this.isSaved = isSaved;
		if (isSaved) {
			tab.setTitle(SH.stripPrefix(tab.getTitle(), "*", false));
			this.form.removeField(this.saveButton);
		} else {
			tab.setTitle("*" + tab.getTitle());
			this.form.addField(this.saveButton);
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (field == this.textField) {
			if (keycode == 13 && mask == Form.KEY_ALT)
				if (!isSaved)
					save();
		}

	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}
	public boolean isInEdit() {
		return !isSaved;
	}
	public void save() {
		File file = new File(localFileName);
		try {
			IOH.writeText(file, textField.getValue());
		} catch (IOException e) {
			getManager().showAlert("Failed to save " + IOH.getFullPath(file) + ": " + e.getMessage(), e);
		}
		setSaved(true);

	}

	public String getName() {
		return name;
	}
	public void reloadFromDisk() {
		File file = new File(localFileName);
		try {
			this.textField.setValue(IOH.readText(file));
		} catch (IOException e) {
			getManager().showAlert("Failed to load " + IOH.getFullPath(file) + ": " + e.getMessage(), e);
		}
		setSaved(true);
	}
}
