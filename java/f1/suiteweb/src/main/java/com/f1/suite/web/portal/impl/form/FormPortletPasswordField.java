package com.f1.suite.web.portal.impl.form;

import java.util.Map;

import com.f1.base.Password;
import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.encrypt.EncoderUtils;

public class FormPortletPasswordField extends FormPortletField<Password> {
	public static final String JSNAME = "PasswordField";

	public FormPortletPasswordField(Class<Password> type, String title) {
		super(type, title);
		setValue(new Password(""));
	}
	public FormPortletPasswordField(String title) {
		super(Password.class, title);
		setValue(new Password(""));
	}
	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		String type = CH.getOrNoThrow(Caster_String.INSTANCE, attributes, "type", "");
		if ("onchange".equals(type)) {
			String curVal = CH.getOr(attributes, "value", ""); // should be obfuscated
			Password curPass = new Password(getUnobfuscated(curVal));
			setValueNoFire(curPass);
		}
		super.onUserValueChanged(attributes);
		return true;
	}
	@Override
	public String getJsValue() {
		return "";
	}

	private String getUnobfuscated(CharSequence val) {
		return new String(EncoderUtils.decode64(val));
	}
	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_STYLE)) {
			new JsFunction(pendingJs, jsObjectName, "init").addParamQuoted(this.getBgColor()).addParamQuoted(this.getFontColor()).addParam(this.getFieldFontSize())
					.addParamQuoted(this.getBorderColorMaterialized()).addParamQuoted(this.getFieldFontFamily()).addParam(this.getBorderRadius())
					.addParam(this.getBorderWidthMaterialized()).end();
		}
		super.updateJs(pendingJs);
	}
	@Override
	public String getjsClassName() {
		return JSNAME;
	}

}
