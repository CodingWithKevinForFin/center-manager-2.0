package com.f1.ami.web.amiscript;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.AbstractDmQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.SelectQueryField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField.Option;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormSelectField<T> extends AmiWebScriptBaseMemberMethods<SelectQueryField> {

	private AmiWebScriptMemberMethods_FormSelectField() {
		super();

		addMethod(GET_VALUE, "value");
		addMethod(GET_OPTIONS, "options");
		addMethod(GET_OPTION);
		addMethod(PUT_OPTION);
		addMethod(REMOVE_OPTION);
		addMethod(CLEAR_OPTIONS);
		addMethod(SET_OPTIONS);
		addMethod(ADD_OPTIONS);
		addMethod(RESET_OPTIONS);
		addMethod(SET_DISPLAY_SORT_OPTION);
		addMethod(GET_DISPLAY_SORT_OPTION, "displaySortOption");
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<SelectQueryField> SET_DISPLAY_SORT_OPTION = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class,
			"setDisplayValueSortOption", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			String sortOption = Caster_String.INSTANCE.cast(params[0]).toLowerCase();
			if (AbstractDmQueryField.DISPLAY_VAL_SORT_NONE.equalsIgnoreCase(sortOption) || AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equalsIgnoreCase(sortOption)
					|| AbstractDmQueryField.DISPLAY_VAL_SORT_DESC.equalsIgnoreCase(sortOption)) {
				targetObject.setDisplaySortOption(sortOption, true);
				return true;
			}
			return false;
		}

		@Override
		protected String getHelp() {
			return "Sets the sorting option for the display values in the autocomplete menu.";
		}
		protected String[] buildParamNames() {
			return new String[] { "displaySortOption" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Expected Values: NONE (original ordering), ASC, DESC" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<SelectQueryField> GET_DISPLAY_SORT_OPTION = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class,
			"getDisplayValueSortOption", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDisplaySortOption(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the sort option set for the display values.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<SelectQueryField> GET_VALUE = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class, "getValue", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns the selected value of this field, null if nothing is selected.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<SelectQueryField> GET_OPTIONS = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class, "getOptions", Map.class) {
		@Override
		public Map invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			LinkedHashMap r = new LinkedHashMap();
			for (Object o : targetObject.getField().getOptions()) {
				FormPortletSelectField.Option op = (Option) o;
				r.put(op.getKey(), op.getName());
			}
			return r;
		}
		@Override
		protected String getHelp() {
			return "Returns the map of options";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<SelectQueryField> RESET_OPTIONS = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class, "resetOptions",
			Object.class) {
		@Override
		public Map invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.resetOptions();
			return null;
		}
		@Override
		protected String getHelp() {
			return "If options have been manually set using amiscript, this resets to default values";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<SelectQueryField> GET_OPTION = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class, "getOption", String.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletSelectField f = (FormPortletSelectField) targetObject.getField();
			return f.getOption(params[0]).getName();
		}
		@Override
		protected String getHelp() {
			return "Returns a single option according to input key";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		};
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Key for which the corresponding option is returned" };
		};
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<SelectQueryField> PUT_OPTION = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class, "putOption", Boolean.class,
			Object.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletSelectField f = (FormPortletSelectField) targetObject.getField();
			f.addOption(params[0], (String) params[1], null);
			if (AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equals(targetObject.getDisplaySortOption(true)))
				f.sortOptionsByName();
			return true;
		}

		@Override
		protected String getHelp() {
			return "Adds an option to the multi-select field.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		};
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Key to be added", "Value to be added" };
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<SelectQueryField> REMOVE_OPTION = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class, "removeOption",
			Boolean.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletSelectField f = (FormPortletSelectField) targetObject.getField();
			f.removeOptionNoThrow(params[0]);
			return true;
		}

		@Override
		protected String getHelp() {
			return "Removes the option from the select field.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		};
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Key to be removed" };
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<SelectQueryField> CLEAR_OPTIONS = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class, "clearOptions",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletSelectField f = (FormPortletSelectField) targetObject.getField();
			f.clearOptions();
			return true;
		}

		@Override
		protected String getHelp() {
			return "Removes all options.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<SelectQueryField> SET_OPTIONS = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class, "setOptions", Boolean.class,
			Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletSelectField f = (FormPortletSelectField) targetObject.getField();
			f.clearOptions();
			f.addOptions((Map<?, String>) params[0]);
			if (AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equals(targetObject.getDisplaySortOption(true)))
				f.sortOptionsByName();
			return true;
		}

		@Override
		protected String getHelp() {
			return "Set the field's options using the provided map";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "options" };
		};
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Options to be added" };
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<SelectQueryField> ADD_OPTIONS = new AmiAbstractMemberMethod<SelectQueryField>(SelectQueryField.class, "addOptions", Boolean.class,
			Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletSelectField f = (FormPortletSelectField) targetObject.getField();
			f.addOptions((Map<?, String>) params[0]);
			if (AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equals(targetObject.getDisplaySortOption(true)))
				f.sortOptionsByName();
			return true;
		}

		@Override
		protected String getHelp() {
			return "Add to the field's options based on the provided map";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "options" };
		};
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Options to be added" };
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormSelectField";
	}

	@Override
	public String getVarTypeDescription() {
		return "A special type of field within a FormPanel that allows multiple options as a dropdown";
	}

	@Override
	public Class<SelectQueryField> getVarType() {
		return SelectQueryField.class;
	}

	@Override
	public Class<SelectQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormSelectField INSTANCE = new AmiWebScriptMemberMethods_FormSelectField();
}
