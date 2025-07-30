package com.f1.ami.web.amiscript;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiService;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.style.AmiWebStyle;
import com.f1.ami.web.style.AmiWebStyleManager;
import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_StyleSet extends AmiWebScriptBaseMemberMethods<AmiWebStyle> {

	private AmiWebScriptMemberMethods_StyleSet() {
		super();
		addMethod(SET_PARENT);
		addMethod(GET_PARENT, "parent");
		addMethod(GET_STYLE_ID, "styleId");
		addMethod(GET_RESOLVED_VALUE);
		addMethod(GET_VALUE);
		addMethod(SET_VALUE);
		addMethod(GET_KEYS);
		addMethod(GET_DEFINED_KEYS);
		addMethod(RESET);
		addMethod(RESET_VALUE);
		addMethod(GET_DEFINITION);
		addCustomDebugProperty("values", Map.class);
		addCustomDebugProperty("resolvedValues", Map.class);
	}

	@Override
	protected Object getCustomDebugProperty(String name, AmiWebStyle value) {
		if ("resolvedValues".equals(name)) {
			Map r = new HashMap<String, Object>();
			AmiWebStyleManager styleManager = value.getStyleManager();
			AmiWebService service = styleManager.getService();

			for (String s : value.getTypes()) {
				for (String varname : styleManager.getStyleType(s).getVarnames()) {
					AmiWebStyleOption option = styleManager.getOptionsByVarname().get(varname);
					Object val = option == null ? null : option.toAmiscriptValue(service, value.resolveValue(option.getNamespace(), option.getKey()));
					r.put(varname, val);
				}
			}
			return r;
		}
		if ("values".equals(name)) {
			AmiWebStyleManager styleManager = value.getStyleManager();
			Map r = new HashMap<String, Object>();
			for (String s : value.getTypes()) {
				for (short varname : value.getDeclaredKeys(s)) {
					AmiWebStyleOption option = styleManager.getOption(s, varname);
					Object val = value.getValue(s, varname);
					r.put(option.getVarname(), val);
				}
			}
			return r;
		}
		return super.getCustomDebugProperty(name, value);
	}
	private static Map<String, String> autoComplete(AmiService service, boolean open) {
		Map<String, String> autoComplete = new TreeMap<String, String>();
		for (Entry<String, AmiWebStyleOption> e : ((AmiWebService) service).getStyleManager().getOptionsByVarname().entrySet()) {
			autoComplete.put('"' + e.getValue().getVarname() + (open ? "\"," : "\")"), e.getValue().getDescription());
		}
		return autoComplete;
	};

	private static final AmiAbstractMemberMethod<AmiWebStyle> GET_PARENT = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "getParent", AmiWebStyle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStyleManager().getStyleById(targetObject.getParentStyle());
		}
		@Override
		protected String getHelp() {
			return "Returns the style that this style set inherits from.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> SET_PARENT = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "setParent", Boolean.class, false,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			String value = (String) params[0];
			if (value == null) {
				targetObject.resetParentStyleOverride();
				return true;
			}
			AmiWebStyle t = targetObject.getStyleManager().getStyleById(value);
			if (t == null)
				return false;
			if (t.inheritsFrom(targetObject.getId()))
				return false;
			return targetObject.setParentStyleOverride(value);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "parentStyle" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "parentStyle" };
		}
		@Override
		protected String getHelp() {
			return "Sets the parent style.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> GET_STYLE_ID = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "getStyleId", AmiWebStyle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getId();
		}
		@Override
		protected String getHelp() {
			return "Returns the style id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> GET_RESOLVED_VALUE = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "getResolvedValue", Object.class,
			false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			AmiWebStyleOption option = targetObject.getStyleManager().getOptionsByVarname().get(name);
			return option == null ? null : option.toAmiscriptValue(targetObject.getStyleManager().getService(), targetObject.resolveValue(option.getNamespace(), option.getKey()));
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with the supplied style key, considering inherited parent styles if its not defined by this style. The returned value will be null only when the key is invalid.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}

		@Override
		public java.util.Map<String, String> getAutocompleteOptions(AmiService service) {
			return autoComplete(service, false);
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> GET_VALUE = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "getValue", Object.class, false,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			AmiWebStyleOption option = targetObject.getStyleManager().getOptionsByVarname().get(name);
			AmiWebService service = targetObject.getStyleManager().getService();
			if (option == null)
				return null;
			if (targetObject.isValueOverride(option.getNamespace(), option.getKey()))
				return option.toAmiscriptValue(service, targetObject.getValueOverride(option.getNamespace(), option.getKey()));
			else
				return option.toAmiscriptValue(service, targetObject.getValue(option.getNamespace(), option.getKey()));
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with the supplied style key or null if the value is not defined for this key.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}

		@Override
		public java.util.Map<String, String> getAutocompleteOptions(AmiService service) {
			return autoComplete(service, false);
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> SET_VALUE = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "setValue", Object.class, false, String.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			Object value = (Object) params[1];
			AmiWebStyleOption option = targetObject.getStyleManager().getOptionsByVarname().get(name);
			if (option == null)
				return Boolean.FALSE;
			if (value != null) {
				AmiWebService service = targetObject.getStyleManager().getService();
				value = option.toInternalStorageValue(service, value);
				if (value == null)
					return Boolean.FALSE;
			} else if (targetObject.getParentStyle() == null)
				return Boolean.FALSE;
			targetObject.putValueOverride(option.getNamespace(), option.getKey(), value);
			return Boolean.TRUE;
		}
		@Override
		protected String getHelp() {
			return "Sets the value associated with the supplied style, if value is null, then the value will use the parent's inherited value. Setting null on the root style will return false. Returns true if the set succeeded, such that the key exists and the value is valid for the given key, false otherwise.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key", "value" };
		}
		@Override
		public java.util.Map<String, String> getAutocompleteOptions(AmiService service) {
			return autoComplete(service, true);
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> GET_KEYS = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "getKeys", Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			HashSet<String> r = new HashSet<String>();
			for (String s : targetObject.getTypes())
				r.addAll(targetObject.getStyleManager().getStyleType(s).getVarnames());
			return r;
		}
		@Override
		protected String getHelp() {
			return "Returns a set of all permissible keys for style options.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> GET_DEFINED_KEYS = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "getDefinedKeys", Set.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			return Collections.unmodifiableCollection(targetObject.getDeclaredVarnames());
		}
		@Override
		protected String getHelp() {
			return "Returns a set of keys for style options that are set on this styleset.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> RESET = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "reset", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.resetParentStyleOverride();
			targetObject.resetOverrides();
			return Boolean.TRUE;
		}
		@Override
		protected String getHelp() {
			return "Resets all values on this style to the default (values configured via the gui). Always succeeds.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> RESET_VALUE = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "resetValue", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			AmiWebStyleOption option = targetObject.getStyleManager().getOptionsByVarname().get(name);
			if (option == null)
				return Boolean.FALSE;
			targetObject.removeValueOverride(option.getNamespace(), option.getKey());
			return Boolean.TRUE;
		}
		@Override
		public java.util.Map<String, String> getAutocompleteOptions(AmiService service) {
			return autoComplete(service, false);
		};
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Resets value for the given key on this style to the default (values configured via the gui). Returns true if operation succeeds, false if key is invalid.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebStyle> GET_DEFINITION = new AmiAbstractMemberMethod<AmiWebStyle>(AmiWebStyle.class, "getDefinition", AmiWebStyleOption.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebStyle targetObject, Object[] params, DerivedCellCalculator caller) {
			String name = (String) params[0];
			return targetObject.getStyleManager().getOptionsByVarname().get(name);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Returns the definition for a particular style option, including type, name and constaints.";
		}
		@Override
		public java.util.Map<String, String> getAutocompleteOptions(AmiService service) {
			return autoComplete(service, false);
		};
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	@Override
	public String getVarTypeName() {
		return "StyleSet";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents a set of attributes that makes up a style";
	}
	@Override
	public Class<AmiWebStyle> getVarType() {
		return AmiWebStyle.class;
	}
	@Override
	public Class<AmiWebStyle> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_StyleSet INSTANCE = new AmiWebScriptMemberMethods_StyleSet();
}
