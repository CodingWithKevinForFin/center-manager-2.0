package com.f1.ami.web.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebLayoutManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;

public class AmiWebImportFieldsPortlet extends FormPortlet implements ConfirmDialogListener {

	final private AmiWebService service;
	private FormPortletButton importButton;
	private FormPortletButton cancelButton;
	private FormPortletTextAreaField textField;
	private AmiWebLayoutManager layoutManager;
	private AmiWebQueryFormPortlet target;

	public AmiWebImportFieldsPortlet(PortletConfig config, AmiWebQueryFormPortlet target) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
		this.target = target;
		this.getFormPortletStyle().setLabelsWidth(0);
		FormPortletTitleField title;
		title = addField(new FormPortletTitleField("Enter your configuration in the text area below"));
		textField = addField(new FormPortletTextAreaField(""));
		textField.focus();
		importButton = addButton(new FormPortletButton("Import"));
		cancelButton = addButton(new FormPortletButton("Cancel"));
		textField.setLabelHidden(true);
		textField.setTopPosPx(36);
		textField.setBottomPosPct(0.10);
		textField.setLeftPosPx(16);
		textField.setRightPosPx(16);
		title.setLabelHidden(true);
		title.setLeftPosPx(16);
		title.setRightPosPct(0.0);
		title.setTopPosPx(8);
		title.setHeightPx(24);
	}

	@Override
	protected void onUserPressedButton(FormPortletButton formPortletButton) {
		if (formPortletButton == cancelButton)
			close();
		else if (formPortletButton == importButton) {
			onImportButton();
		}
	}

	private void onImportButton() {
		String configText = textField.getValue();
		Map<String, Object> configuration = AmiWebLayoutHelper.parseJsonSafe(configText, getManager());
		if (configuration == null)
			return;
		List<Map> fieldsMap = (List) configuration.get("fields");

		List<String> fieldIds = new ArrayList<String>();
		if (fieldsMap != null) {
			for (Map m : fieldsMap)
				fieldIds.add(this.target.importField(m, true, false).getId());
		} else
			fieldIds.add(this.target.importField(configuration, true, false).getId());

		close();
		return;
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_YES.equals(id))
			close();
		return true;
	}
}
