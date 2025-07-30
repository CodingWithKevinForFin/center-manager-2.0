package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.FileUploadFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.UploadQueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormUploadField;

public class AmiWebFormFileUploadFieldFactory extends AmiWebFormFieldFactory<UploadQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_FILE_UPLOAD;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_FILE_UPLOAD;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_UPLOAD;
	}
	@Override
	public String getIcon() {
		return "upload.svg";
	}

	@Override
	public UploadQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new UploadQueryField(this, form);
	}
	@Override
	public BaseEditFieldPortlet<UploadQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new FileUploadFormPortlet(this, form, x, y);
	}
	@Override
	public Class<UploadQueryField> getClassType() {
		return UploadQueryField.class;
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormUploadField.INSTANCE;
	}
}
