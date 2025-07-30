package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.ImageFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.ImageQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormImageField;

public class AmiWebFormImageFieldFactory extends AmiWebFormFieldFactory<ImageQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_IMAGE;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_IMAGE;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_IMAGE;
	}
	@Override
	public String getIcon() {
		return "field-image.svg";
	}

	@Override
	public ImageQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new ImageQueryField(this, form);
	}
	@Override
	public BaseEditFieldPortlet<ImageQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new ImageFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<ImageQueryField> getClassType() {
		return ImageQueryField.class;
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormImageField.INSTANCE;
	}
}
