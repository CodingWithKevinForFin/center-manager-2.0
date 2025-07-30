package com.f1.ami.web.form.field;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormFileUploadFieldFactory;
import com.f1.ami.web.form.queryfield.UploadQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.SH;

public class FileUploadFormPortlet extends BaseEditFieldPortlet<UploadQueryField> {
	private FormPortletTextField uploadFileButtonText;
	private FormPortletTextField uploadUrlButtonText;

	public FileUploadFormPortlet(AmiWebFormFileUploadFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		FormPortletTitleField titleField = getSettingsForm().addField(new FormPortletTitleField("File Upload Field Options"));
		this.uploadFileButtonText = getSettingsForm().addField(new FormPortletTextField("Upload File Button:"));
		this.uploadUrlButtonText = getSettingsForm().addField(new FormPortletTextField("Upload Url Button:"));
		titleField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		titleField.setTopPosPx(420).setHeightPx(25);
		this.uploadFileButtonText.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.uploadFileButtonText.setTopPosPx(455).setHeightPx(25);
		this.uploadUrlButtonText.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.uploadUrlButtonText.setTopPosPx(487).setHeightPx(25);
	}
	@Override
	public void readFromField(UploadQueryField field) {
		this.uploadFileButtonText.setValue(field.getField().getUploadFileButtonText());
		this.uploadUrlButtonText.setValue(field.getField().getUploadUrlButtonText());
	}

	@Override
	public void writeToField(UploadQueryField queryField) {
		FormPortletFileUploadField field = queryField.getField();
		if (SH.is(uploadFileButtonText.getValue()))
			field.setUploadFileButtonText(uploadFileButtonText.getValue());
		if (SH.is(uploadUrlButtonText.getValue()))
			field.setUploadUrlButtonText(uploadUrlButtonText.getValue());
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field.getId().contentEquals(this.uploadFileButtonText.getId())) {
			getField().getField().setUploadFileButtonText(field.getValue().toString());
			this.uploadFileButtonText.setValue(field.getValue().toString());
		} else if (field.getId().contentEquals(this.uploadUrlButtonText.getId())) {
			getField().getField().setUploadUrlButtonText(field.getValue().toString());
			this.uploadUrlButtonText.setValue(field.getValue().toString());
		}
		super.onFieldValueChanged(portlet, field, attributes);
	}

}
