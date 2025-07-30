package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.NumericSubRangeEditFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.SubRangeQueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormRangeSliderField;

public class AmiWebFormSubRangeFieldFactory extends AmiWebFormFieldFactory<SubRangeQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_SUBRANGE;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_SUBRANGE;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_RANGE;
	}
	@Override
	public String getIcon() {
		return "field-rangeslider.svg";
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormRangeSliderField.INSTANCE;
	}

	@Override
	public SubRangeQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new SubRangeQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<SubRangeQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new NumericSubRangeEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<SubRangeQueryField> getClassType() {
		return SubRangeQueryField.class;
	}
}
