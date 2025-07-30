package com.f1.ami.web.amiscript;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.AbstractDmQueryField;
import com.f1.ami.web.form.queryfield.MultiCheckboxQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.portal.impl.form.FormPortletMultiCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletMultiCheckboxField.Option;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormMultiCheckboxField<T> extends AmiWebScriptBaseMemberMethods<MultiCheckboxQueryField> {

	private AmiWebScriptMemberMethods_FormMultiCheckboxField() {
		super();

		addMethod(GET_OPTIONS, "options");
		addMethod(GET_OPTION);
		addMethod(PUT_OPTION);
		addMethod(REMOVE_OPTION);
		addMethod(CLEAR_OPTIONS);
		addMethod(SET_OPTIONS);
		addMethod(ADD_OPTIONS);
		addMethod(GET_SELECTED);
		addMethod(GET_UNSELECTED);
		addMethod(CLEAR_SELECTED);
		addMethod(RESET_OPTIONS);
		addMethod(SET_DISPLAY_SORT_FORMULA);
		addMethod(GET_DISPLAY_SORT_FORMULA, "displaySortOption");
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> SET_DISPLAY_SORT_FORMULA = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(
			MultiCheckboxQueryField.class, "setDisplayValueSortOption", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> GET_DISPLAY_SORT_FORMULA = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(
			MultiCheckboxQueryField.class, "getDisplayValueSortOption", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> GET_OPTIONS = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"getOptions", Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			LinkedHashMap r = new LinkedHashMap();
			for (Object o : targetObject.getField().getOptions()) {
				FormPortletMultiCheckboxField.Option op = (Option) o;
				r.put(op.getKey(), op.getName());
			}
			return r;
		}

		@Override
		protected String getHelp() {
			return "Returns a map of options.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> GET_OPTION = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"getOption", String.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiCheckboxField field = (FormPortletMultiCheckboxField) targetObject.getField();
			try {
				return field.getOption(params[0]).getName();
			} catch (Exception e) {
				LH.warning(log, e);
				return null;
			}
		}

		@Override
		protected String getHelp() {
			return "Returns the option mapped to the specified key. NUll on invalid key.";
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

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> PUT_OPTION = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"putOption", Boolean.class, Object.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiCheckboxField field = (FormPortletMultiCheckboxField) targetObject.getField();
			try {
				Option op = field.addOption2(params[0], (String) params[1]);
				if (AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equals(targetObject.getDisplaySortOption(true)))
					field.sortOptionsByName();
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Adds an option to the multi-checkbox field. Returns false if operation fails, true otherwise.";
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

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> REMOVE_OPTION = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"removeOption", Boolean.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiCheckboxField field = (FormPortletMultiCheckboxField) targetObject.getField();
			try {
				field.removeOption(params[0]);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Removes the option from the multi-checkbox field. Returns false if operation fails, true otherwise.";
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

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> CLEAR_OPTIONS = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"clearOptions", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiCheckboxField field = (FormPortletMultiCheckboxField) targetObject.getField();
			field.clear();
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

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> SET_OPTIONS = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"setOptions", Boolean.class, Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiCheckboxField f = (FormPortletMultiCheckboxField) targetObject.getField();
			f.clear();
			try {
				f.addOptions((Map<Object, String>) params[0]);
				if (AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equals(targetObject.getDisplaySortOption(true)))
					f.sortOptionsByName();
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Set the field's options using the provided map. Removes any previous options before setting it to the new one. Note: previous options will still be cleared even if the operation fails. Returns true if the operation is successful, false otherwise.";
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

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> ADD_OPTIONS = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"addOptions", Boolean.class, Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				FormPortletMultiCheckboxField f = (FormPortletMultiCheckboxField) targetObject.getField();
				f.addOptions((Map<Object, String>) params[0]);
				if (AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equals(targetObject.getDisplaySortOption(true)))
					f.sortOptionsByName();
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Adds to the field's existing options.";
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

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> GET_SELECTED = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"getSelected", List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiCheckboxField field = (FormPortletMultiCheckboxField) targetObject.getField();
			return CH.l(field.getSelectedValueKeys());
		}

		@Override
		protected String getHelp() {
			return "Returns ordered list of keys corresponding to the selected items.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> GET_UNSELECTED = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"getUnselected", List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiCheckboxField field = (FormPortletMultiCheckboxField) targetObject.getField();
			return CH.l(field.getUnselected());
		}

		@Override
		protected String getHelp() {
			return "Returns ordered list of keys corresponding to the unselected items.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> CLEAR_SELECTED = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"clearSelected", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiCheckboxField field = (FormPortletMultiCheckboxField) targetObject.getField();
			field.clearSelected();
			return true;
		}

		@Override
		protected String getHelp() {
			return "Clears all the selected options.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<MultiCheckboxQueryField> RESET_OPTIONS = new AmiAbstractMemberMethod<MultiCheckboxQueryField>(MultiCheckboxQueryField.class,
			"resetOptions", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiCheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.resetOptions();
			return true;
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

	@Override
	public String getVarTypeName() {
		return "FormMultiCheckboxField";
	}

	@Override
	public String getVarTypeDescription() {
		return "A field that allows for multiple check boxes";
	}

	@Override
	public Class<MultiCheckboxQueryField> getVarType() {
		return MultiCheckboxQueryField.class;
	}

	@Override
	public Class<MultiCheckboxQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormMultiCheckboxField INSTANCE = new AmiWebScriptMemberMethods_FormMultiCheckboxField();
}
