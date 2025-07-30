package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.NumericRangeEditFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.RangeQueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormSliderField;

public class AmiWebFormRangeFieldFactory extends AmiWebFormFieldFactory<RangeQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_RANGE;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_RANGE;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_SLIDER;
	}
	@Override
	public String getIcon() {
		return "field-slider.svg";
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormSliderField.INSTANCE;
	}

	@Override
	public RangeQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new RangeQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<RangeQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new NumericRangeEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<RangeQueryField> getClassType() {
		return RangeQueryField.class;
	}
}
