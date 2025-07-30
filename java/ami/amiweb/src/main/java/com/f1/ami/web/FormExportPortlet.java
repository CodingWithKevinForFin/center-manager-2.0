package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletExportImportManager;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.utils.OH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class FormExportPortlet extends GridPortlet implements FormPortletListener {

	static final ObjectToJsonConverter JSON = new ObjectToJsonConverter();
	static {
		JSON.setIgnoreUnconvertable(true);
		JSON.setStrictValidation(true);
		JSON.setTreatNanAsNull(false);
		JSON.setCompactMode(ObjectToJsonConverter.MODE_CLEAN);
		JSON.lock();
	}
	final private FormPortlet target;
	final private FormPortlet form;
	final private FormPortletButton okay;
	final private FormPortletButton cancel;
	final private FormPortletTextAreaField textField;
	final private String origText;
	final private boolean showErrorMessage;
	private FormPortletExportImportManager exportManager;

	public FormExportPortlet(FormPortlet target, FormPortletExportImportManager manager, boolean showErrorMessage) {
		super(target.generateConfig());
		this.target = target;
		this.exportManager = manager;
		this.showErrorMessage = showErrorMessage;
		this.form = addChild(new FormPortlet(generateConfig()), 0, 0);
		this.textField = this.form.addField(new FormPortletTextAreaField(""));
		this.okay = this.form.addButton(new FormPortletButton("Apply"));
		this.cancel = this.form.addButton(new FormPortletButton("Cancel"));
		this.form.addFormPortletListener(this);
		Map<String, Object> exportToText = this.exportManager.exportToText(target);
		this.textField.setValue(this.origText = JSON.objectToString(exportToText));
		this.textField.setSelection(0, this.origText.length());
		this.form.getFormPortletStyle().setLabelsWidth(15);
		this.okay.setEnabled(true);
		this.setSuggestedSize(this.target.getWidth() + 80, this.target.getHeight());
		this.textField.setHeight(FormPortletField.HEIGHT_STRETCH);
		this.textField.setWidth(FormPortletField.WIDTH_STRETCH);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.okay) {
			if (OH.ne(this.textField.getValue(), this.origText)) {
				String text = this.textField.getValue();
				StringBuilder errorSink = new StringBuilder();
				try {
					Object object = JSON.stringToObject(text);
					if (object instanceof Map) {
						Map<String, Object> values = (Map<String, Object>) object;
						this.exportManager.importFromText(this.target, values, errorSink);
					} else {
						if (object == null)
							return;
						errorSink.append("Expecting json map not " + OH.getSimpleClassName(object));
					}
				} catch (Exception e) {
					getManager().showAlert("Error Importing: " + e.getMessage(), e);
					return;
				}
				if (errorSink.length() > 0 && this.showErrorMessage)
					getManager().showAlert(errorSink.toString());
				else
					close();
			} else
				close();
		} else if (button == this.cancel)
			close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	public void setTextFieldSize(int width, int height) {
	}

}
