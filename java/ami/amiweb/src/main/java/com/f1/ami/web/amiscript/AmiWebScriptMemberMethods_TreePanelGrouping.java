package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.tree.AmiWebTreeGroupBy;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_TreePanelGrouping extends AmiWebScriptBaseMemberMethods<AmiWebTreeGroupBy> {

	private AmiWebScriptMemberMethods_TreePanelGrouping() {
		super();
		addMethod(GET_ID, "id");
	}

	@Override
	public String getVarTypeName() {
		return "TreePanelGrouping";
	}

	@Override
	public String getVarTypeDescription() {
		return "Represents a group by level within TreePanel object. It can be accessed using treePanelObj.getGroupBy(groupById).";
	}

	@Override
	public Class<AmiWebTreeGroupBy> getVarType() {
		return AmiWebTreeGroupBy.class;
	}

	@Override
	public Class<AmiWebTreeGroupBy> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebTreeGroupBy> GET_ID = new AmiAbstractMemberMethod<AmiWebTreeGroupBy>(AmiWebTreeGroupBy.class, "getId", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTreeGroupBy targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAmiId();
		}

		@Override
		protected String getHelp() {
			return "Returns the grouping id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	public final static AmiWebScriptMemberMethods_TreePanelGrouping INSTANCE = new AmiWebScriptMemberMethods_TreePanelGrouping();
}
