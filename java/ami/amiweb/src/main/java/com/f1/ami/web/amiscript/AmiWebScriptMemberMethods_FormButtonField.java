package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.FormButtonQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormButtonField extends AmiWebScriptBaseMemberMethods<FormButtonQueryField> {

	private AmiWebScriptMemberMethods_FormButtonField() {
		super();
		addMethod(CLICK);
		addMethod(GET_AUTO_DISABLE);
		addMethod(IS_AUTO_DISABLED);
		addMethod(RESET_AUTO_DISABLED);
		registerCallbackDefinition(FormButtonQueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<FormButtonQueryField> CLICK = new AmiAbstractMemberMethod<FormButtonQueryField>(FormButtonQueryField.class, "click",
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, FormButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getDisabled() || targetObject.getField().isDisabledDueToClick())
				return Boolean.FALSE;
			targetObject.getForm().onFieldValueChanged(targetObject.getForm().getEditableForm(), targetObject.getField(), null);
			if (targetObject.getField().shouldDisableAfterFirstClick())
				targetObject.getField().setDisabledDueToClick();
			return Boolean.TRUE;
		}
		@Override
		protected String getHelp() {
			return "Fires an on-user-clicked event. Returns true if succeeded, false if failed due to button being disabled. If this button is configured for auto-disable after first click then calling this method will auto-disable this button";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<FormButtonQueryField> GET_AUTO_DISABLE = new AmiAbstractMemberMethod<FormButtonQueryField>(FormButtonQueryField.class,
			"getAutoDisable", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, FormButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletButtonField field = targetObject.getField();
			return field.shouldDisableAfterFirstClick();
		}
		@Override
		protected String getHelp() {
			return "Is this button configured to automatically disable after being clicked (to enforce itempotent behaviou and avoid multiple clicks from user-thrashing)";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<FormButtonQueryField> IS_AUTO_DISABLED = new AmiAbstractMemberMethod<FormButtonQueryField>(FormButtonQueryField.class,
			"isAutoDisabled", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, FormButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletButtonField field = targetObject.getField();
			return field.isDisabledDueToClick();
		}
		@Override
		protected String getHelp() {
			return "Has this button been auto-disabled because it has been clicked by a user. See also: resetAutoDisabled";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<FormButtonQueryField> RESET_AUTO_DISABLED = new AmiAbstractMemberMethod<FormButtonQueryField>(FormButtonQueryField.class,
			"resetAutoDisabled", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, FormButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletButtonField field = targetObject.getField();
			field.resetDisabledDueToClick();
			return null;
		}
		@Override
		protected String getHelp() {
			return "Reenable a button that has been auto-disabled";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormButtonField";
	}

	@Override
	public String getVarTypeDescription() {
		return "A button within a FormPanel.";
	}

	@Override
	public Class<FormButtonQueryField> getVarType() {
		return FormButtonQueryField.class;
	}

	@Override
	public Class<FormButtonQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormButtonField INSTANCE = new AmiWebScriptMemberMethods_FormButtonField();
}
