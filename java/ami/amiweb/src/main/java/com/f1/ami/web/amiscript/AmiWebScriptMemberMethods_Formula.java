package com.f1.ami.web.amiscript;

import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcFrameStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiWebScriptMemberMethods_Formula extends AmiWebScriptBaseMemberMethods<AmiWebFormula> {

	private AmiWebScriptMemberMethods_Formula() {
		super();
		addMethod(GET_NAME, "name");
		addMethod(GET_DRI, "dri");
		addMethod(GET_OWNER, "owner");
		addMethod(GET_AMISCRIPT, "amiscript");
		addMethod(SET_AMISCRIPT);
		addMethod(RESET_AMISCRIPT);
		addMethod(INVOKE);
	}

	public static final AmiAbstractMemberMethod<AmiWebFormula> GET_NAME = new AmiAbstractMemberMethod<AmiWebFormula>(AmiWebFormula.class, "getName", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFormula targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getFormulaId();
		}

		@Override
		protected String getHelp() {
			return "Returns the name of this formula.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebFormula> GET_DRI = new AmiAbstractMemberMethod<AmiWebFormula>(AmiWebFormula.class, "gettDRI", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFormula targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAri();
		}

		@Override
		protected String getHelp() {
			return "Returns the DRI of this formula.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebFormula> GET_OWNER = new AmiAbstractMemberMethod<AmiWebFormula>(AmiWebFormula.class, "getOwner", AmiWebDomObject.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFormula targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getOwnerFormulas().getThis();
		}

		@Override
		protected String getHelp() {
			return "Returns the Dashboard Resource that this formula is owned by.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebFormula> GET_AMISCRIPT = new AmiAbstractMemberMethod<AmiWebFormula>(AmiWebFormula.class, "getAmiScript", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFormula targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the amiscript defined for this formula, null if not defined.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebFormula> SET_AMISCRIPT = new AmiAbstractMemberMethod<AmiWebFormula>(AmiWebFormula.class, "setAmiScript", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFormula targetObject, Object[] params, DerivedCellCalculator caller) {
			String script = (String) params[0];
			targetObject.setFormula(script, true);
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "amiscript" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "amiscript to set on this formula" };
		}

		@Override
		protected String getHelp() {
			return "Sets the amiscript for this formula.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebFormula> RESET_AMISCRIPT = new AmiAbstractMemberMethod<AmiWebFormula>(AmiWebFormula.class, "resetAmiScript", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFormula targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.clearFormulaOverride();
			return null;
		}

		@Override
		protected String getHelp() {
			return "Resets the amiscript for this formula to the layout's default configuration.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<AmiWebFormula> INVOKE = new AmiAbstractMemberMethod<AmiWebFormula>(AmiWebFormula.class, "invoke", Object.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebFormula targetObject, Object[] params, DerivedCellCalculator caller) {
			DerivedCellCalculator calc = targetObject.getFormulaCalc();
			Map<Object, Object> values = (Map) params[0];
			if (calc == null)
				return null;
			MutableCalcFrame frame = new MutableCalcFrame();
			if (values != null) {
				for (Entry<Object, Object> i : values.entrySet()) {
					if (i.getKey() == null)
						continue;
					String key = AmiUtils.s(i.getKey());
					frame.putTypeValue(key, i.getValue() == null ? Object.class : i.getValue().getClass(), i.getValue());
				}

			}
			try {
				return calc.get(new ChildCalcFrameStack(caller, false, sf, frame));
			} catch (Exception e) {
				throw new FlowControlThrow(null, null, e);
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "map_of_params" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {
					"map of params, where the name should match the param name (see getParams method).Missing params are mapped to null, misspelled params are ignored" };
		}

		@Override
		protected String getHelp() {
			return "Executes the formula, using the supplied a map of arguments. The key should be param name.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public Class<AmiWebFormula> getVarType() {
		return AmiWebFormula.class;
	}

	@Override
	public Class<AmiWebFormula> getVarDefaultImpl() {
		return AmiWebFormula.class;
	}

	@Override
	public String getVarTypeName() {
		return "Formula";
	}

	@Override
	public String getVarTypeDescription() {
		return "AMI Script Formula";
	}

	public final static AmiWebScriptMemberMethods_Formula INSTANCE = new AmiWebScriptMemberMethods_Formula();
}
