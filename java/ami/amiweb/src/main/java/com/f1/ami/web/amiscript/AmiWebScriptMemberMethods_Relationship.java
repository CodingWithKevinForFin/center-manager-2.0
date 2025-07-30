package com.f1.ami.web.amiscript;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmLinkImpl;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.base.Table;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_Relationship extends AmiWebScriptBaseMemberMethods<AmiWebDmLink> {

	private AmiWebScriptMemberMethods_Relationship() {
		super();

		addMethod(GET_ID, "id");
		addMethod(GET_SOURCE, "source");
		addMethod(GET_TARGET, "target");
		addMethod(EXECUTE);
		addMethod(GET_LAYOUT, "layout");
		addMethod(GET_CLAUSES, "clauses");
		addMethod(EXECUTE_ON_ALL_ROWS);
		registerCallbackDefinition(AmiWebDmLinkImpl.CALLBACK_DEF_ONPROCESS);
	}

	private static final AmiAbstractMemberMethod<AmiWebDmLink> GET_ID = new AmiAbstractMemberMethod<AmiWebDmLink>(AmiWebDmLink.class, "getId", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmLink targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRelationshipId();
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
	private static final AmiAbstractMemberMethod<AmiWebDmLink> GET_SOURCE = new AmiAbstractMemberMethod<AmiWebDmLink>(AmiWebDmLink.class, "getSource", AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmLink targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSourcePanelNoThrow();
		}
		@Override
		protected String getHelp() {
			return "Returns the source panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public final AmiAbstractMemberMethod<AmiWebDmLink> GET_TARGET = new AmiAbstractMemberMethod<AmiWebDmLink>(AmiWebDmLink.class, "getTarget", AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmLink targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTargetPanelNoThrow();
		}
		@Override
		protected String getHelp() {
			return "Returns the target panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDmLink> EXECUTE_ON_ALL_ROWS = new AmiAbstractMemberMethod<AmiWebDmLink>(AmiWebDmLink.class, "executeOnAllRows",
			AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmLink i, Object[] params, DerivedCellCalculator caller) {
			if (!AmiWebDmUtils.checkShouldRunRequest(i))
				return null;
			AmiWebService service = i.getService();
			AmiWebDm targetDm = i.getTargetDmAliasDotName() == null ? null : service.getDmManager().getDmByAliasDotName(i.getTargetDmAliasDotName());
			AmiWebPortlet panel = i.getSourcePanel();
			Table values = panel.getSelectableRows(i, AmiWebPortlet.ALL);
			AmiWebDmUtils.sendRequest(service, i, values, targetDm);
			return null;
		}
		@Override
		protected String getHelp() {
			return "Forcefully runs this relationship on all rows from source table, regardless of selection.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDmLink> EXECUTE = new AmiAbstractMemberMethod<AmiWebDmLink>(AmiWebDmLink.class, "execute", AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmLink targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebService service = targetObject.getService();
			AmiWebDmUtils.sendRequest(service, targetObject);
			return null;
		}
		@Override
		protected String getHelp() {
			return "Forcefully runs this relationship.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDmLink> GET_LAYOUT = new AmiAbstractMemberMethod<AmiWebDmLink>(AmiWebDmLink.class, "getLayout", AmiWebLayoutFile.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmLink targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getService().getLayoutFilesManager().getLayoutByFullAlias(targetObject.getAmiLayoutFullAlias());
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
	private static final AmiAbstractMemberMethod<AmiWebDmLink> GET_CLAUSES = new AmiAbstractMemberMethod<AmiWebDmLink>(AmiWebDmLink.class, "getClauses", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmLink targetObject, Object[] params, DerivedCellCalculator caller) {
			Map<String, String> r = new HashMap<String, String>();
			for (String s : targetObject.getWhereClauseVarNames())
				r.put(s, targetObject.getWhereClause(s));
			return r;
		}
		@Override
		protected String getHelp() {
			return "Returns a map of the clauses for this relationship.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Relationship";
	}
	@Override
	public String getVarTypeDescription() {
		return "A relationship between two panels (the green arrows between panels) used to dictate user work flows.";
	}
	@Override
	public Class<AmiWebDmLink> getVarType() {
		return AmiWebDmLink.class;
	}
	@Override
	public Class<AmiWebDmLink> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_Relationship INSTANCE = new AmiWebScriptMemberMethods_Relationship();
}
