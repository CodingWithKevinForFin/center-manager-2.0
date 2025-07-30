package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.AmiWebButton;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormRelationshipButton extends AmiWebScriptBaseMemberMethods<AmiWebButton> {

	private AmiWebScriptMemberMethods_FormRelationshipButton() {
		super();
		addMethod(GET_NAME, "name");
		registerCallbackDefinition(AmiWebButton.PARAM_DEF);
	}

	private static final AmiAbstractMemberMethod<AmiWebButton> GET_NAME = new AmiAbstractMemberMethod<AmiWebButton>(AmiWebButton.class, "getName", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebButton targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getName();
		}
		@Override
		protected String getHelp() {
			return "Return the name associated with this button.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormRelationshipButton";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents a relationship button within a FormPanel";
	}
	@Override
	public Class<AmiWebButton> getVarType() {
		return AmiWebButton.class;
	}
	@Override
	public Class<AmiWebButton> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormRelationshipButton INSTANCE = new AmiWebScriptMemberMethods_FormRelationshipButton();
}
