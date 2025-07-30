package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.TextAreaQueryField;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormTextareaField extends AmiWebScriptBaseMemberMethods<TextAreaQueryField> {

	private AmiWebScriptMemberMethods_FormTextareaField() {
		super();
		addMethod(GET_VALUE, "value");
		addMethod(GET_CURSOR, "cursor");
		addMethod(SET_CURSOR);
		addMethod(SET_SELECTED);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	public static final AmiAbstractMemberMethod<TextAreaQueryField> GET_VALUE = new AmiAbstractMemberMethod<TextAreaQueryField>(TextAreaQueryField.class, "getValue",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextAreaQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns this field's value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TextAreaQueryField> GET_CURSOR = new AmiAbstractMemberMethod<TextAreaQueryField>(TextAreaQueryField.class, "getCursor",
			Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextAreaQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getCursorPosition();
		}

		@Override
		protected String getHelp() {
			return "Returns this cursors position";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TextAreaQueryField> SET_CURSOR = new AmiAbstractMemberMethod<TextAreaQueryField>(TextAreaQueryField.class, "setCursor",
			Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextAreaQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer i = (Integer) params[0];
			int r = targetObject.getField().getCursorPosition();
			targetObject.getField().setCursorPosition(i);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "position" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position, with zero being on the left)" };
		}

		@Override
		protected String getHelp() {
			return "Sets this cursors position";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<TextAreaQueryField> SET_SELECTED = new AmiAbstractMemberMethod<TextAreaQueryField>(TextAreaQueryField.class, "setSelected",
			Object.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextAreaQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer start = (Integer) params[0];
			Integer end = (Integer) params[1];
			String value = (String) targetObject.getValue();
			if (start == null && end == null) {
				targetObject.getField().setSelection(-1, -1);
				return true;
			}
			if (value != null) {
				int s = start == null ? 0 : MH.clip((int) start, 0, value.length());
				int e = end == null ? value.length() : MH.clip((int) end, 0, value.length());
				targetObject.getField().setSelection(s, e);
				return true;
			}
			return false;
		}
		protected String[] buildParamNames() {
			return new String[] { "start", "end" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "start position which is inclusive", "end position, which is exclusive" };
		}

		@Override
		protected String getHelp() {
			return "selects  a range of text";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormTextareaField";
	}

	@Override
	public String getVarTypeDescription() {
		return "AMI Script Class to represent Text Area Field";
	}

	@Override
	public Class<TextAreaQueryField> getVarType() {
		return TextAreaQueryField.class;
	}

	@Override
	public Class<? extends TextAreaQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormTextareaField INSTANCE = new AmiWebScriptMemberMethods_FormTextareaField();
}
