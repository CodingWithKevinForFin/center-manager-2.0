package com.f1.ami.web.amiscript;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebAliasPortlet;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebScriptManager;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebScriptMemberMethods_Layout extends AmiWebScriptBaseMemberMethods<AmiWebLayoutFile> {

	private AmiWebScriptMemberMethods_Layout() {
		super();
		addMethod(GET_PANEL);
		addMethod(GET_DATAMODEL);
		addMethod(GET_RELATIONSHIP);
		addMethod(GET_PROCESSOR);
		addMethod(GET_FULL_ALIAS);
		addMethod(GET_ALIAS);
		addMethod(GET_PARENT);
		addMethod(GET_CHILD);
		addMethod(GET_CHILD_ALIASES);
		addMethod(GET_FIELD_VALUE);
		addMethod(EXECUTE_SCRIPT);
		addMethod(EXECUTE_SCRIPT2);
		addMethod(EXECUTE_SCRIPT3);
		addMethod(GET_STORAGE_LOCATION, "storageLocation");
		addMethod(GET_STORAGE_TYPE, "storageType");
		addMethod(IS_READ_ONLY, "isReadonly");
		addMethod(IS_RELATIVE, "isRelative");
		addMethod(EXPORT_USER_PREFERENCE_BY_ID);
		addMethod(EXPORT_USER_PREFERENCES);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONSTARTUP);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONSTARTUP_COMPLETE);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONUSERPREFSLOADING);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONUSERPREFSLOADED);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONUSERPREFSSAVING);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONUSERPREFSSAVED);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONNOTIFICATIONHANDLED);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONASYNCCOMMANDRESPONSE);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONURLPARAMS);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONKEY);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_DEF_ONMOUSE);
		registerCallbackDefinition(AmiWebScriptManager.CALLBACK_ONAMIJSCALLBACKINTITLEBAR);
	}

	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> EXPORT_USER_PREFERENCES = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class,
			"exportUserPreferences", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getService().getUserPrefs();
		}
		@Override
		protected String getHelp() {
			return "Exports all the user preferences associated with the layout.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> EXPORT_USER_PREFERENCE_BY_ID = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class,
			"exportUserPreferencesById", Map.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			String upid = (String) params[0];
			return AmiWebLayoutHelper.exportUserPreferences(targetObject.getService(), targetObject.getAmiLayoutFullAlias(), upid);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "upid" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "user preferences id" };
		}
		@Override
		protected String getHelp() {
			return "Exports user preferences associated with the upid (User Preferences Id).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_PROCESSOR = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getProcessor",
			AmiWebRealtimeProcessor.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			String fullAlias = AmiWebManagers.PROCESSOR + AmiWebUtils.getFullAlias(targetObject.getFullAlias(), (String) params[0]);
			return targetObject.getService().getWebManagers().getRealtimeProcessor(fullAlias);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "processorId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "id of the processor to return" };
		}

		@Override
		protected String getHelp() {
			return "Returns the processor given the processorId, or null if not found.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_PANEL = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getPanel", AmiWebPortlet.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			String fullAlias = AmiWebUtils.getFullAlias(targetObject.getFullAlias(), (String) params[0]);
			return targetObject.getService().getPortletByAliasDotPanelId(fullAlias);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "panelId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "id of the panel to return" };
		}

		@Override
		protected String getHelp() {
			return "Returns the panel for the given the panel id, or null if not found.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_DATAMODEL = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getDatamodel",
			AmiWebDm.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			String fullAlias = AmiWebUtils.getFullAlias(targetObject.getFullAlias(), (String) params[0]);
			return targetObject.getService().getDmManager().getDmByAliasDotName(fullAlias);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "datamodelId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "id of the datamodel" };
		}

		@Override
		protected String getHelp() {
			return "Returns the datamodel given its name, or null if not found.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_RELATIONSHIP = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getRelationship",
			AmiWebDmLink.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			String fullAlias = AmiWebUtils.getFullAlias(targetObject.getFullAlias(), (String) params[0]);
			return targetObject.getService().getDmManager().getDmLinkByAliasDotRelationshipId(fullAlias);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "relationshipId" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "id of the relationship" };
		}
		@Override
		protected String getHelp() {
			return "Returns the relationship given the relationshipId, or null if there is no relationship with the given id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_FULL_ALIAS = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getFullAlias",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getFullAlias();
		}

		@Override
		protected String getHelp() {
			return "Returns the full alias (name) of this layout instance. Will be empty string if it is the root (top-level) layout.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_ALIAS = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getAlias", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAlias();
		}

		@Override
		protected String getHelp() {
			return "Returns the alias (name) of this layout instance (relative to the parent alias). Will be empty string if it is the root (top-level) layout.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_PARENT = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getParent",
			AmiWebLayoutFile.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getParent();
		}

		@Override
		protected String getHelp() {
			return "Returns the parent layout of this layout, or null if this is the root (top-level) layout.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_CHILD = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getChild",
			AmiWebLayoutFile.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getChildByAlias((String) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "alias" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "alias of the child relative to this layout" };
		}

		@Override
		protected String getHelp() {
			return "Returns the child layout of this layout based on the relative alias (name), or null if no child alias exists with the given name";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_CHILD_ALIASES = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getChildAliases",
			Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return new HashSet<String>(targetObject.getChildAliases());
		}

		@Override
		protected String getHelp() {
			return "Returns the relative alias (name) of child layouts of this layout.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> EXECUTE_SCRIPT2 = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "exec", Object.class,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile layout, Object[] params, DerivedCellCalculator caller) {
			final String script = (String) params[0];
			final StringBuilder error = new StringBuilder();
			final AmiWebService service = layout.getService();
			final String fullAlias = layout.getFullAlias();
			try {
				AmiWebScriptManagerForLayout scriptManager = service.getScriptManager(fullAlias);
				DerivedCellCalculator calc = scriptManager.parseAmiScript(script, EmptyCalcTypes.INSTANCE, error, service.getDebugManager(), AmiDebugMessage.TYPE_DYNAMIC_AMISCRIPT,
						layout, "exec", true, null);
				return calc == null ? null : calc.get(sf);
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode("ANONYMOUS", script);
				e.addFrame(caller);
				throw e;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "amiScript" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Script to execute" };
		}
		@Override
		protected String getHelp() {
			return "Runs script inline. The provided script is compiled and executed in current thread. Note, within the script 'this' refers to the current layout.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebLayoutFile target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			try {
				return paused.getNext().resume();
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode("ANONYMOUS", (String) params[0]);
				e.addFrame(caller);
				throw e;
			}
		}
		@Override
		public boolean isPausable() {
			return true;
		};
	};

	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> EXECUTE_SCRIPT = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "exec", Object.class,
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile layout, Object[] params, DerivedCellCalculator caller) {
			final Number when = (Number) params[0];
			final String script = (String) params[1];
			final StringBuilder error = new StringBuilder();
			final AmiWebService service = layout.getService();
			final String fullAlias = layout.getFullAlias();
			DerivedCellCalculator calc = service.getScriptManager(fullAlias).parseAmiScript(script, EmptyCalcTypes.INSTANCE, error, service.getDebugManager(),
					AmiDebugMessage.TYPE_DYNAMIC_AMISCRIPT, layout, "exec", true, null);
			if (calc == null)
				throw new FlowControlThrow("Could not compile '" + script + "' ==> " + error);
			long time = AmiUtils.getMillis(when);
			service.addTimedEvent(time, fullAlias, script, calc);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "when", "amiScript" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "time - negative number is milliseconds in the future from now, zero or positive number is absolute time in millis.", "Script to execute" };
		}
		@Override
		protected String getHelp() {
			return "Runs script deferred. The provided script is compiled in the current thread, but executed outside this call. If provided time is in the past, will execute at earliest opportunity. Note, within the script 'this' refers to the current layout.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> EXECUTE_SCRIPT3 = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "exec", Object.class,
			Number.class, String.class, Integer.class, Integer.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile layout, Object[] params, DerivedCellCalculator caller) {
			final Number when = (Number) params[0];
			final String script = (String) params[1];
			final StringBuilder error = new StringBuilder();
			final AmiWebService service = layout.getService();
			final String fullAlias = layout.getFullAlias();
			final int timeout = (int) params[2];
			final int limit = (int) params[3];
			final String defaultDs = (String) params[4];
			DerivedCellCalculator calc = service.getScriptManager(fullAlias).parseAmiScript(script, EmptyCalcTypes.INSTANCE, error, service.getDebugManager(),
					AmiDebugMessage.TYPE_DYNAMIC_AMISCRIPT, layout, "exec", true, null);
			if (calc == null)
				throw new FlowControlThrow("Could not compile '" + script + "' ==> " + error);
			long time = AmiUtils.getMillis(when);
			service.addTimedEvent(time, fullAlias, script, timeout, limit, defaultDs, calc);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "when", "amiScript", "timeout", "limit", "defaultDs" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "time - negative number is milliseconds in the future from now, zero or positive number is absolute time in millis.", "Script to execute",
					"timeout", "limit", "default datasource" };
		}
		@Override
		protected String getHelp() {
			return "Runs script deferred. The provided script is compiled in the current thread, but executed outside this call. If provided time is in the past, will execute at earliest opportunity. Note, within the script 'this' refers to the current layout.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_FIELD_VALUE = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getFieldValue",
			String.class, String.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			String fullAlias = AmiWebUtils.getFullAlias(targetObject.getFullAlias(), (String) params[0]);
			AmiWebAliasPortlet p = targetObject.getService().getPortletByAliasDotPanelId(fullAlias);
			try {
				if (p instanceof AmiWebQueryFormPortlet) {
					AmiWebQueryFormPortlet fP = (AmiWebQueryFormPortlet) p;
					return fP.getFieldValues().get(params[1]);
				} else
					return null;
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "formPanelId", "fieldId" };
		}

		@Override
		protected String getHelp() {
			return "Returns the value from a form. Returns null if form or field doesn't exist.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_STORAGE_LOCATION = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getStorageLocation",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getLocation();
		}

		@Override
		protected String getHelp() {
			return "Returns the location of this layout, usually the file name.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> GET_STORAGE_TYPE = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "getStorageType",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSource();
		}

		@Override
		protected String getHelp() {
			return "Returns the storage type, which is either CLOUD, LOCAL, ABSOLUTE, or WORKSPACE.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> IS_READ_ONLY = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "isReadonly",
			Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isReadonly();
		}

		@Override
		protected String getHelp() {
			return "Returns true if the layout is read-only, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebLayoutFile> IS_RELATIVE = new AmiAbstractMemberMethod<AmiWebLayoutFile>(AmiWebLayoutFile.class, "isRelative", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebLayoutFile targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsLocationRelative();
		}

		@Override
		protected String getHelp() {
			return "Returns true if path is relative, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Layout";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<AmiWebLayoutFile> getVarType() {
		return AmiWebLayoutFile.class;
	}
	@Override
	public Class<AmiWebLayoutFile> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_Layout INSTANCE = new AmiWebScriptMemberMethods_Layout();
}
