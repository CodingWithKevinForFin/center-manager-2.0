package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.Table;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_Processor extends AmiWebScriptBaseMemberMethods<AmiWebRealtimeProcessor> {

	private AmiWebScriptMemberMethods_Processor() {
		super();

		addMethod(GET_ID, "id");
		addMethod(GET_LAYOUT, "layout");
		addMethod(TO_TABLE, "table");
	}

	private static final AmiAbstractMemberMethod<AmiWebRealtimeProcessor> GET_ID = new AmiAbstractMemberMethod<AmiWebRealtimeProcessor>(AmiWebRealtimeProcessor.class, "getId",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRealtimeProcessor targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAdn();
		}
		@Override
		protected String getHelp() {
			return "Returns the user-assigned ID for this relationship.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRealtimeProcessor> GET_LAYOUT = new AmiAbstractMemberMethod<AmiWebRealtimeProcessor>(AmiWebRealtimeProcessor.class,
			"getLayout", AmiWebLayoutFile.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRealtimeProcessor targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getService().getLayoutFilesManager().getLayoutByFullAlias(targetObject.getAlias());
		}
		@Override
		protected String getHelp() {
			return "Returns the layout that owns this relationship.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRealtimeProcessor> TO_TABLE = new AmiAbstractMemberMethod<AmiWebRealtimeProcessor>(AmiWebRealtimeProcessor.class, "toTable",
			Table.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRealtimeProcessor targetObject, Object[] params, DerivedCellCalculator caller) {
			return AmiWebUtils.toTable(targetObject.getService().getWebManagers(), targetObject.getRealtimeId());
		}
		@Override
		protected String getHelp() {
			return "Returns the underlying table that this processor is handling.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Processor";
	}
	@Override
	public String getVarTypeDescription() {
		return "A Realtime Event Processor.";
	}
	@Override
	public Class<AmiWebRealtimeProcessor> getVarType() {
		return AmiWebRealtimeProcessor.class;
	}
	@Override
	public Class<AmiWebRealtimeProcessor> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_Processor INSTANCE = new AmiWebScriptMemberMethods_Processor();
}
