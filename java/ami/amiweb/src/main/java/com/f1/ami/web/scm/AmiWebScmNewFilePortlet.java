package com.f1.ami.web.scm;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmException;
import com.f1.ami.web.AmiWebService;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public class AmiWebScmNewFilePortlet extends GridPortlet implements FormPortletListener {

	final private FormPortlet form;
	final private FormPortletTextAreaField pathField;
	final private FormPortletTextAreaField fileNameField;
	final private FormPortletCheckboxField addToScmField;
	final private FormPortletButton cancelButton;
	final private FormPortletButton submitButton;
	final private AmiWebService service;
	private String path;

	public AmiWebScmNewFilePortlet(PortletConfig config, String path, AmiWebService service) {
		super(config);
		this.service = service;
		this.form = addChild(new FormPortlet(generateConfig()), 0, 0);
		path = SH.trim(path);
		if (!path.endsWith("/"))
			path = path + "/";

		this.path = path;
		this.pathField = this.form.addField(new FormPortletTextAreaField("Path:"));
		this.fileNameField = this.form.addField(new FormPortletTextAreaField("File Name:"));
		this.addToScmField = this.form.addField(new FormPortletCheckboxField("Add To Source Control:"));
		this.pathField.setValue(path);
		this.cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
		this.submitButton = this.form.addButton(new FormPortletButton("Create"));
		this.form.addFormPortletListener(this);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			String path = SH.trim(this.pathField.getValue());
			if (!SH.startsWith(path, this.path)) {
				getManager().showAlert("Path not under " + this.path);
			}

			File file = new File(path, SH.trim(this.fileNameField.getValue()));
			if (file.exists()) {
				getManager().showAlert("File already exists");
				return;
			}
			try {
				IOH.ensureDir(new File(path));
				if (!file.createNewFile())
					throw new RuntimeException("File arlready exists");
				if (this.addToScmField.getBooleanValue())
					try {
						this.service.getScmAdapter().addFiles(CH.l(IOH.getFullPath(file)), AmiScmAdapter.TYPE_TEXT);
					} catch (AmiScmException e) {
						getManager().showAlert("Could not add to source control: " + e.getMessage(), e);
						return;
					}

			} catch (IOException e) {
				getManager().showAlert("Could not create file: " + e.getMessage(), e);
				return;
			}
			close();
		} else if (button == this.cancelButton) {
			close();
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

}
