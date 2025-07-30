package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebDevTools;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_DevTools extends AmiWebScriptBaseMemberMethods<AmiWebDevTools> {

	private AmiWebScriptMemberMethods_DevTools() {
		super();
		addMethod(OPEN_DATAMODELER_EDITOR);
	}

	private static final AmiAbstractMemberMethod<AmiWebDevTools> OPEN_DATAMODELER_EDITOR = new AmiAbstractMemberMethod<AmiWebDevTools>(AmiWebDevTools.class, "openDatamodelEditor",
			String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDevTools targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.openDatamodelEditor((String) params[0]);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "aliasDotName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "datamodel alias.dmname" };
		}

		@Override
		protected String getHelp() {
			return "Opens the datamodel editor with the supplied datamodel name.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "DevTools";
	}

	@Override
	public String getVarTypeDescription() {
		return "developer tools";
	}

	@Override
	public Class<AmiWebDevTools> getVarType() {
		return AmiWebDevTools.class;
	}

	@Override
	public Class<AmiWebDevTools> getVarDefaultImpl() {
		return null;
	}

	public static final AmiWebScriptMemberMethods_DevTools INSTANCE = new AmiWebScriptMemberMethods_DevTools();
}
