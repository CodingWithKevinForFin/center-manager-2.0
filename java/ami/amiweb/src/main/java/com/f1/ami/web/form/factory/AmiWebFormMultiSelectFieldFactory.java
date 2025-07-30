package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.MultiSelectFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.MultiSelectQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormMultiSelectField;

public class AmiWebFormMultiSelectFieldFactory extends AmiWebFormFieldFactory<MultiSelectQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_MULTI_SELECT;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_MULTI_SELECT;
	}

	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_MULTI_SELECT;
	}
	@Override
	public String getIcon() {
		return "field-multiselect.svg";
	}

	@Override
	public MultiSelectQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new MultiSelectQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<MultiSelectQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new MultiSelectFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<MultiSelectQueryField> getClassType() {
		return MultiSelectQueryField.class;
	}
	@Override
	public int getDefaultHeight() {
		return 100;
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormMultiSelectField.INSTANCE;
	}
}
