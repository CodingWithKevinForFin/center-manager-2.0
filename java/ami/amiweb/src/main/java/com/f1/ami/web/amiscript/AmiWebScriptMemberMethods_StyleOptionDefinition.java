package com.f1.ami.web.amiscript;

import java.util.Collections;
import java.util.Set;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.ami.web.style.impl.AmiWebStyleOptionChoices;
import com.f1.ami.web.style.impl.AmiWebStyleOptionRange;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_StyleOptionDefinition extends AmiWebScriptBaseMemberMethods<AmiWebStyleOption> {

	protected static final Set<Boolean> TRUE_FALSE = CH.s(true, false);

	private AmiWebScriptMemberMethods_StyleOptionDefinition() {
		super();

		addMethod(getName, "name");
		addMethod(getDescription, "description");
		addMethod(getType, "type");
		addMethod(isList);
		addMethod(getMinValue, "minValue");
		addMethod(getMaxValue, "maxValue");
		addMethod(getPermittedValues, "permittedValues");
	}

	private static final AmiAbstractMemberMethod<AmiWebStyleOption> getName = new AmiAbstractMemberMethod<AmiWebStyleOption>(AmiWebStyleOption.class, "getName", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyleOption targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getVarname();
		}
		@Override
		protected String getHelp() {
			return "Returns the variable name.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyleOption> getType = new AmiAbstractMemberMethod<AmiWebStyleOption>(AmiWebStyleOption.class, "getType", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyleOption targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getFormattedType();
		}
		@Override
		protected String getHelp() {
			return "Returns the type.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyleOption> isList = new AmiAbstractMemberMethod<AmiWebStyleOption>(AmiWebStyleOption.class, "isList", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyleOption targetObject, Object[] params, DerivedCellCalculator caller) {
			switch (targetObject.getType()) {
				case AmiWebStyleConsts.TYPE_COLOR_ARRAY:
					return true;
				default:
					return false;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns true if the style option supports a list of values, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyleOption> getDescription = new AmiAbstractMemberMethod<AmiWebStyleOption>(AmiWebStyleOption.class, "getDescription",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyleOption targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDescription();
		}
		@Override
		protected String getHelp() {
			return "Returns the style description.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyleOption> getMinValue = new AmiAbstractMemberMethod<AmiWebStyleOption>(AmiWebStyleOption.class, "getMinValue",
			Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyleOption targetObject, Object[] params, DerivedCellCalculator caller) {
			return (targetObject instanceof AmiWebStyleOptionRange) ? ((AmiWebStyleOptionRange) targetObject).getMin() : null;
		}
		@Override
		protected String getHelp() {
			return "Returns the minimum value allowed for this option, or null if there is no minimum.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyleOption> getMaxValue = new AmiAbstractMemberMethod<AmiWebStyleOption>(AmiWebStyleOption.class, "getMaxValue",
			Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyleOption targetObject, Object[] params, DerivedCellCalculator caller) {
			return (targetObject instanceof AmiWebStyleOptionRange) ? ((AmiWebStyleOptionRange) targetObject).getMax() : null;
		}
		@Override
		protected String getHelp() {
			return "Returns the maximum value allowed for this option, or null if there is no maximum.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyleOption> getPermittedValues = new AmiAbstractMemberMethod<AmiWebStyleOption>(AmiWebStyleOption.class,
			"getPermittedValues", Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyleOption targetObject, Object[] params, DerivedCellCalculator caller) {
			Set r;
			switch (targetObject.getType()) {
				case AmiWebStyleConsts.TYPE_COLOR:
				case AmiWebStyleConsts.TYPE_COLOR_ARRAY:
				case AmiWebStyleConsts.TYPE_COLOR_GRADIENT:
				case AmiWebStyleConsts.TYPE_NUMBER:
					r = null;
					break;
				case AmiWebStyleConsts.TYPE_BOOLEAN:
					r = TRUE_FALSE;
					break;
				case AmiWebStyleConsts.TYPE_CSS_CLASS:
					r = null;
					break;
				case AmiWebStyleConsts.TYPE_ENUM:
					AmiWebStyleOptionChoices c = (AmiWebStyleOptionChoices) targetObject;
					r = c.getOptionsToDisplayValue().getValues();
					break;
				case AmiWebStyleConsts.TYPE_FONT:
					AmiWebService service = AmiWebUtils.getService(sf);
					r = service == null ? null : service.getFontsManager().getFonts();
					break;
				default:
					throw new RuntimeException(SH.toString(targetObject.getType()));
			}
			return r == null ? null : Collections.unmodifiableSet(r);
		}
		@Override
		protected String getHelp() {
			return "Returns a set of permitted values for this particular style.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "StyleOptionDefinition";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents the definition of a particular style option";
	}
	@Override
	public Class<AmiWebStyleOption> getVarType() {
		return AmiWebStyleOption.class;
	}
	@Override
	public Class<AmiWebStyleOption> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_StyleOptionDefinition INSTANCE = new AmiWebScriptMemberMethods_StyleOptionDefinition();
}
