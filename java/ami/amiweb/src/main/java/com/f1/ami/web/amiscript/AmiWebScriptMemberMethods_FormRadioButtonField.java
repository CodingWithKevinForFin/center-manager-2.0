package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.RadioButtonQueryField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletRadioButtonField;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormRadioButtonField extends AmiWebScriptBaseMemberMethods<RadioButtonQueryField> {

	private AmiWebScriptMemberMethods_FormRadioButtonField() {
		super();
		addMethod(GET_GROUP_NAME, "groupName");
		addMethod(GET_CHECKED_RADIO);
		addMethod(GET_RADIOS);
		addMethod(GET_RADIOS_INCLUDE_THIS);
		addMethod(GET_VALUE);
		addMethod(IS_CHECKED); // keeping this for backwards compatibility
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<RadioButtonQueryField> GET_GROUP_NAME = new AmiAbstractMemberMethod<RadioButtonQueryField>(RadioButtonQueryField.class,
			"getGroupName", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, RadioButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getGroupName();
		}

		@Override
		protected String getHelp() {
			return "Returns the groupname associated with this field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<RadioButtonQueryField> GET_CHECKED_RADIO = new AmiAbstractMemberMethod<RadioButtonQueryField>(RadioButtonQueryField.class,
			"getCheckedRadio", RadioButtonQueryField.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, RadioButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletRadioButtonField rbf = targetObject.getField().getForm().getLastCheckedForGroup(targetObject.getField().getGroupNameWithFormPortletId());
			return rbf == null ? null : (RadioButtonQueryField) rbf.getCorrelationData();
		}

		@Override
		protected String getHelp() {
			return "Returns the checked radio button associated with the group name. Null if none are checked.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<RadioButtonQueryField> GET_RADIOS_INCLUDE_THIS = new AmiAbstractMemberMethod<RadioButtonQueryField>(RadioButtonQueryField.class,
			"getRadiosIncludeThis", List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, RadioButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			List<RadioButtonQueryField> output = new ArrayList<RadioButtonQueryField>();
			Iterable<FormPortletField<?>> fields = targetObject.getField().getForm().getFormFields();
			for (FormPortletField<?> f : fields) {
				if (f instanceof FormPortletRadioButtonField) {
					RadioButtonQueryField rbf = (RadioButtonQueryField) f.getCorrelationData();
					if (rbf.getGroupName().equals(targetObject.getGroupName()))
						output.add(rbf);
				}
			}
			return output;
		}

		@Override
		protected String getHelp() {
			return "Returns a list of radio button(s) that are in the same group as the caller, including the caller.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<RadioButtonQueryField> GET_RADIOS = new AmiAbstractMemberMethod<RadioButtonQueryField>(RadioButtonQueryField.class, "getRadios",
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, RadioButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			List<RadioButtonQueryField> output = new ArrayList<RadioButtonQueryField>();
			Iterable<FormPortletField<?>> fields = targetObject.getField().getForm().getFormFields();
			for (FormPortletField<?> f : fields) {
				if (f instanceof FormPortletRadioButtonField && OH.ne(targetObject.getField(), f)) {
					RadioButtonQueryField rbf = (RadioButtonQueryField) f.getCorrelationData();
					if (rbf.getGroupName().equals(targetObject.getGroupName()))
						output.add(rbf);
				}
			}
			return output;
		}

		@Override
		protected String getHelp() {
			return "Returns a list of radio button(s) that are in the same group as the caller, which will NOT be included in the list. NOTE: To include the caller on the list, use getRadiosIncludeThis() instead.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<RadioButtonQueryField> GET_VALUE = new AmiAbstractMemberMethod<RadioButtonQueryField>(RadioButtonQueryField.class, "getValue",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, RadioButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns true if radio button is checked, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<RadioButtonQueryField> IS_CHECKED = new AmiAbstractMemberMethod<RadioButtonQueryField>(RadioButtonQueryField.class, "isChecked",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, RadioButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns true if radio button is checked, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormRadioField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Radio Buttons for Form Panels.";
	}

	@Override
	public Class<RadioButtonQueryField> getVarType() {
		return RadioButtonQueryField.class;
	}

	@Override
	public Class<RadioButtonQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormRadioButtonField INSTANCE = new AmiWebScriptMemberMethods_FormRadioButtonField();
}
