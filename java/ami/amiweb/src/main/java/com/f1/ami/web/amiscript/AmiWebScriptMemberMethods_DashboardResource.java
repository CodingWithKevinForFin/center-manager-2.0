package com.f1.ami.web.amiscript;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiService;
import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.base.CalcFrame;
import com.f1.base.Caster;
import com.f1.utils.DetailedException;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_DashboardResource extends AmiWebScriptBaseMemberMethods<AmiWebDomObject> {

	private AmiWebScriptMemberMethods_DashboardResource() {
		super();

		addMethod(GET_FORMULA_NAMES);
		addMethod(GET_FORMULAS, "formulas");
		addMethod(GET_FORMULA);
		addMethod(GET_DRI, "dri");
		addMethod(GET_LRI, "lri");
		addMethod(GET_DASHBOARD_RESOURCE_TYPE, "dashboardResourceType");
		addMethod(GET_CHILDREN, "children");
		addMethod(GET_OWNER, "owner");
		addMethod(GET_CALLBACKS, "callbacks");
		addMethod(GET_CALLBACK);
		addMethod(IS_TRANSIENT, "transient");
		addMethod(INVOKE_CALLBACK);
	}

	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_DRI = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "getDRI", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAri();
		}

		@Override
		protected String getHelp() {
			return "Returns the Dashboard Resource Indicator (DRI) as a string. DRI is the unique identifier of this resources within the entire dashboard.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_DASHBOARD_RESOURCE_TYPE = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class,
			"getDashboardResourceType", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAriType();
		}

		@Override
		protected String getHelp() {
			return "Returns the Dashboard Resource Type as a string";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_LRI = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "getLRI", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAri();
		}

		@Override
		protected String getHelp() {
			return "Returns the Localized Resource Indicator (LRI) as a string. LRI is the unique identifier of this resource with the parent element.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_CHILDREN = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "getChildren", List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getChildDomObjects();
		}

		@Override
		protected String getHelp() {
			return "Returns a list of the child dashboard resources.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_OWNER = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "getOwner",
			AmiWebDomObject.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getParentDomObject();
		}

		@Override
		protected String getHelp() {
			return "Returns the DashboardResource that owns this object.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_CALLBACKS = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "getCallbacks", Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebAmiScriptCallbacks callbacks = targetObject.getAmiScriptCallbacks();
			return callbacks == null ? null : new TreeMap<String, AmiWebAmiScriptCallback>(callbacks.getAmiScriptCallbackDefinitionsMap());
		}

		@Override
		protected String getHelp() {
			return "Returns a map of all callback definitions.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> INVOKE_CALLBACK = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "invokeCallback",
			Object.class, true, String.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			if (name == null)
				return null;
			AmiWebAmiScriptCallbacks callbacks = targetObject.getAmiScriptCallbacks();
			AmiWebAmiScriptCallback cb = callbacks.getCallback(name);
			if (cb == null)
				return new FlowControlThrow(caller, "CALLBACK_NOT_FOUND: " + cb);
			ParamsDefinition paramsDef = cb.getParamsDef();

			if (params.length - 1 != paramsDef.getParamsCount())
				throw new FlowControlThrow(caller,
						paramsDef.toString(stackFrame.getFactory()) + " Expecting " + paramsDef.getParamsCount() + " params, not: " + (params.length - 1));
			if (cb.hasError(true))
				return new FlowControlThrow(caller, "COMPILE_ERROR: " + cb.toDerivedString());
			CalcFrame values = new BasicCalcFrame(paramsDef.getParamTypesMapping());
			for (int i = 0; i < paramsDef.getParamsCount(); i++) {
				Caster<?> paramCaster = paramsDef.getParamCaster(i);
				try {
					values.putValue(paramsDef.getParamName(i), paramCaster.cast(params[i + 1]));
				} catch (DetailedException e) {
					throw new FlowControlThrow(caller, "could not cast parameter " + (i + 1) + " to " + stackFrame.getFactory().forType(paramCaster.getCastToClass()));
				}
			}

			try {
				return cb.executeInBlock(stackFrame, values);
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode(cb.getAri(), cb.getAmiscript(true));
				e.addFrame(caller);
				throw e;
			} catch (ExpressionParserException e) {
				throw new FlowControlThrow(caller, "Runtime Error: " + e.getMessage(), e);
			} catch (Exception e) {
				throw new FlowControlThrow(caller, "Internal Error", e);
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "callbackName", "args" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "method name of callback", "arguments" };
		}

		@Override
		protected String getHelp() {
			return "Executes the specified callback definition and returns the result from the callback.";
		}
		@Override
		public java.util.Map<String, String> getAutocompleteOptions(AmiService service) {
			return Collections.EMPTY_MAP;//CH.m("\"onChange\")", "onChange)");
		}

		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			try {
				return paused.getNext().resume();
			} catch (FlowControlThrow e) {
				String name = (String) params[0];
				AmiWebAmiScriptCallbacks callbacks = targetObject.getAmiScriptCallbacks();
				AmiWebAmiScriptCallback cb = callbacks.getCallback(name);
				e.getTailFrame().setOriginalSourceCode(cb.getAri(), cb.getAmiscript(true));
				e.addFrame(caller);
				throw e;
			} catch (ExpressionParserException e) {
				throw new FlowControlThrow(caller, "Runtime Error: " + e.getMessage(), e);
			} catch (Exception e) {
				throw new FlowControlThrow(caller, "Internal Error", e);
			}
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public boolean isPausable() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_CALLBACK = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "getCallback",
			AmiWebAmiScriptCallback.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			if (name == null)
				return null;
			AmiWebAmiScriptCallbacks callbacks = targetObject.getAmiScriptCallbacks();
			return callbacks.getCallback(name);
		}

		protected String[] buildParamNames() {
			return new String[] { "callbackName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "method name of callback" };
		}

		@Override
		protected String getHelp() {
			return "Returns a callback by name";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> IS_TRANSIENT = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "isTransient", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isTransient();
		}

		@Override
		protected String getHelp() {
			return "Returns true if this is transient, false otherwise. Transient objects are not saved to the layout.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_FORMULA_NAMES = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "getFormulaNames",
			Set.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebFormulas f = targetObject.getFormulas();
			if (f == null)
				return new TreeSet();
			else
				return new TreeSet(f.getFormulaIds());
		}

		@Override
		protected String getHelp() {
			return "Returns a set of the names of all formulas for this object.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_FORMULAS = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "getFormulas", Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			TreeMap<String, AmiWebFormula> r = new TreeMap<String, AmiWebFormula>();
			AmiWebFormulas f = targetObject.getFormulas();
			if (f != null)
				for (String fid : f.getFormulaIds())
					r.put(fid, f.getFormula(fid));
			return r;
		}

		@Override
		protected String getHelp() {
			return "Returns a map of all available formulas for this object. The key is the formula name.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDomObject> GET_FORMULA = new AmiAbstractMemberMethod<AmiWebDomObject>(AmiWebDomObject.class, "getFormula",
			AmiWebFormula.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDomObject targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebFormulas f = targetObject.getFormulas();
			if (f == null)
				return null;
			String formulaId = (String) params[0];
			return f.getFormula(formulaId);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "formulaName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the formula to return" };
		}
		@Override
		protected String getHelp() {
			return "Returns a Formula given the formula name.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "DashboardResource";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents all dashboard resources within the AMI dashboard, these include panels, menus, datamodels, relationships, layouts, etc";
	}
	@Override
	public Class<AmiWebDomObject> getVarType() {
		return AmiWebDomObject.class;
	}
	@Override
	public Class<AmiWebDomObject> getVarDefaultImpl() {
		return null;
	}

	public static final AmiWebScriptMemberMethods_DashboardResource INSTANCE = new AmiWebScriptMemberMethods_DashboardResource();
}
