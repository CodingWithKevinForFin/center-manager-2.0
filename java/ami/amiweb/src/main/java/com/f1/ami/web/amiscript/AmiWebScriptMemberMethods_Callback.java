package com.f1.ami.web.amiscript;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Caster;
import com.f1.utils.CH;
import com.f1.utils.DetailedException;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_Callback extends AmiWebScriptBaseMemberMethods<AmiWebAmiScriptCallback> {

	private AmiWebScriptMemberMethods_Callback() {
		super();
		addMethod(GET_NAME, "name");
		addMethod(GET_DRI, "dri");
		addMethod(GET_PARAMS, "params");
		addMethod(GET_PARAM_TYPES, "paramTypes");
		addMethod(GET_OWNER, "owner");
		addMethod(GET_RETURN_TYPE, "returnType");
		addMethod(GET_AMISCRIPT, "amiscript");
		addMethod(GET_TABLESET, "tableset");
		addMethod(SET_AMISCRIPT);
		addMethod(RESET_AMISCRIPT);
		addMethod(INVOKE);
		addMethod(INVOKE2);
	}

	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> GET_NAME = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class, "getName",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getParamsDef().getMethodName();
		}

		@Override
		protected String getHelp() {
			return "name of this callback";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> GET_DRI = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class, "getDRI",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAri();
		}

		@Override
		protected String getHelp() {
			return "DRI of this callback";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> GET_OWNER = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class,
			"getOwner", AmiWebDomObject.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getParent().getThis();
		}

		@Override
		protected String getHelp() {
			return "Returns the DashboardResource that this callback is owned by.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> GET_AMISCRIPT = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class,
			"getAmiScript", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAmiscript(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the amiscript defined for this callback as a string, null if not defined.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> GET_TABLESET = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class,
			"getTableset", Tableset.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTableset();
		}

		@Override
		protected String getHelp() {
			return "Returns the Tableset object which contains column names and types of the underlying table.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> SET_AMISCRIPT = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class,
			"setAmiScript", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			String script = (String) params[0];
			targetObject.setAmiscript(script, true);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "amiscript" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "amiscript to set on this callback" };
		}

		@Override
		protected String getHelp() {
			return "Sets the amiscript for this callback.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> RESET_AMISCRIPT = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class,
			"resetAmiScript", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.clearAmiscriptOverride();
			return null;
		}

		@Override
		protected String getHelp() {
			return "Resets the amiscript for this callback to the layout config default.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> GET_RETURN_TYPE = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class,
			"getReturnType", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			return sf.getFactory().forType(targetObject.getParamsDef().getReturnType());
		}

		@Override
		protected String getHelp() {
			return "Returns the return type of this callback.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> GET_PARAMS = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class,
			"getParams", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			List<String> r = CH.l(targetObject.getParamsDef().getParamNames());
			return r;
		}

		@Override
		protected String getHelp() {
			return "Returns an ordered list of param names for this callback.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> GET_PARAM_TYPES = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class,
			"getParamTypes", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			TreeMap<String, String> r = new TreeMap<String, String>();
			MethodFactoryManager mf = sf.getFactory();
			CalcTypes paramTypesMapping = targetObject.getParamsDef().getParamTypesMapping();
			for (String i : paramTypesMapping.getVarKeys())
				r.put(i, mf.forType(paramTypesMapping.getType(i)));
			return r;
		}

		@Override
		protected String getHelp() {
			return "Returns an ordered map of (key, type) pair for this callback.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> INVOKE = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class, "invoke",
			Object.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.hasCode()) {
				Map<?, ?> m = (Map<?, ?>) params[0];
				CalcTypes paramTypesMapping = targetObject.getParamsDef().getParamTypesMapping();
				final CalcFrame values = new BasicCalcFrame(paramTypesMapping);
				if (CH.isntEmpty(m)) {
					for (Entry<?, ?> i : m.entrySet()) {
						String k = Caster_String.INSTANCE.cast(i.getKey());
						if (SH.isnt(k))
							continue;
						Class<?> castTo = paramTypesMapping.getType(k);
						if (castTo == null)
							continue;
						Object v = OH.cast(i.getValue(), castTo);
						values.putValue(k, v);
					}
				}
				try {
					return targetObject.executeInBlock(stackFrame, values);
				} catch (FlowControlThrow e) {
					e.getTailFrame().setOriginalSourceCode(targetObject.getAri(), targetObject.getAmiscript(true));
					e.addFrame(caller);
					throw e;
				}
			}
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "map_of_params" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {
					"map of params, where the name should match the param name (see getParams method). Missing params are mapped to null, misspelled params are ignored." };
		}

		@Override
		protected String getHelp() {
			return "Executes the callback, given a map of arguments. Map should contain (param_name, value) pairs.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			try {
				return paused.getNext().resume();
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode(targetObject.getAri(), targetObject.getAmiscript(true));
				e.addFrame(caller);
				throw e;
			}
		}
		@Override
		public boolean isPausable() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebAmiScriptCallback> INVOKE2 = new AmiAbstractMemberMethod<AmiWebAmiScriptCallback>(AmiWebAmiScriptCallback.class, "invoke",
			Object.class, List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebAmiScriptCallback targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.hasCode()) {
				List<?> m = (List<?>) params[0];
				if (m == null)
					m = Collections.EMPTY_LIST;
				ParamsDefinition paramsDef = targetObject.getParamsDef();
				CalcFrame values = new BasicCalcFrame(paramsDef.getParamTypesMapping());
				if (m.size() != paramsDef.getParamsCount())
					throw new FlowControlThrow(caller, paramsDef.toString(stackFrame.getFactory()) + " Expecting " + paramsDef.getParamsCount() + " elements in the list, not: " + m.size());
				for (int i = 0; i < paramsDef.getParamsCount(); i++) {
					Caster<?> paramCaster = paramsDef.getParamCaster(i);
					try {
						values.putValue(paramsDef.getParamName(i), paramCaster.cast(m.get(i)));
					} catch (DetailedException e) {
						throw new FlowControlThrow(caller, "could not cast parameter " + (i + 1) + " to " + stackFrame.getFactory().forType(paramCaster.getCastToClass()));
					}
				}
				try {
					return targetObject.executeInBlock(stackFrame, values);
				} catch (FlowControlThrow e) {
					e.getTailFrame().setOriginalSourceCode(targetObject.getAri(), targetObject.getAmiscript(true));
					e.addFrame(caller);
					throw e;
				}
			}
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "list_of_params" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "list of params, must be same size as the number of params" };
		}

		@Override
		protected String getHelp() {
			return "Executes the callback given a list of arguments. The list should contain the values in the order of the params.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebAmiScriptCallback targetObject, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			try {
				return paused.getNext().resume();
			} catch (FlowControlThrow e) {
				e.getTailFrame().setOriginalSourceCode(targetObject.getAri(), targetObject.getAmiscript(true));
				e.addFrame(caller);
				throw e;
			}
		}
		@Override
		public boolean isPausable() {
			return true;
		};
	};

	@Override
	public Class<AmiWebAmiScriptCallback> getVarType() {
		return AmiWebAmiScriptCallback.class;
	}

	@Override
	public Class<AmiWebAmiScriptCallback> getVarDefaultImpl() {
		return AmiWebAmiScriptCallback.class;
	}

	@Override
	public String getVarTypeName() {
		return "Callback";
	}

	@Override
	public String getVarTypeDescription() {
		return "AMI Script Callback";
	}

	public static final AmiWebScriptMemberMethods_Callback INSTANCE = new AmiWebScriptMemberMethods_Callback();
}
