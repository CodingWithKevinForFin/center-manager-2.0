package com.f1.ami.web.amiscript;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.AbstractDmQueryField;
import com.f1.ami.web.form.queryfield.MultiSelectQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField.Option;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormMultiSelectField<T> extends AmiWebScriptBaseMemberMethods<MultiSelectQueryField> {

	private AmiWebScriptMemberMethods_FormMultiSelectField() {
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
		addMethod(MOVE_SELECTED_UP);
		addMethod(MOVE_SELECTED_DOWN);
		addMethod(MOVE_SELECTED_TOP);
		addMethod(MOVE_SELECTED_BOTTOM);
		addMethod(MOVE_SELECTED_TO);
		addMethod(RESET_OPTIONS);
		addMethod(SET_DISPLAY_SORT_OPTION);
		addMethod(GET_DISPLAY_SORT_OPTIOn, "displaySortOption");
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<MultiSelectQueryField> SET_DISPLAY_SORT_OPTION = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"setDisplayValueSortOption", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> GET_DISPLAY_SORT_OPTIOn = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"getDisplayValueSortOption", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> MOVE_SELECTED_UP = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"moveSelectedUp", Boolean.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getField().moveSelectedUp();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Moves the selected options up";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> RESET_OPTIONS = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"resetOptions", Object.class) {
		@Override
		public Map invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> MOVE_SELECTED_DOWN = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"moveSelectedDown", Boolean.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getField().moveSelectedDown();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Moves the selected options down";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> MOVE_SELECTED_TOP = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"moveSelectedTop", Boolean.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getField().moveSelectedTop();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Moves the selected options top";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> MOVE_SELECTED_BOTTOM = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"moveSelectedBottom", Boolean.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getField().moveSelectedBottom();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Moves the selected options bottom";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> MOVE_SELECTED_TO = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"moveSelectedTo", Boolean.class, Integer.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getField().moveSelectedTo((Integer) params[0]);
			return true;
		}
		@Override
		protected String getHelp() {
			return "Moves the selected options to position";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "index" };
		};
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position for the selection options to be moved to" };
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> CLEAR_SELECTED = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"clearSelected", Boolean.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setValue(OH.EMPTY_OBJECT_ARRAY);
			return true;
		}
		@Override
		protected String getHelp() {
			return "Clear selected values";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> GET_SELECTED = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"getSelected", List.class) {
		@Override
		public List invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			LinkedHashSet<?> selected = targetObject.getField().getValue();
			return CH.l(selected);
		}
		@Override
		protected String getHelp() {
			return "Returns ordered list of selected values";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> GET_UNSELECTED = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"getUnselected", List.class) {
		@Override
		public List invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			LinkedHashSet<?> unselected = targetObject.getField().getUnselected();
			return CH.l(unselected);
		}
		@Override
		protected String getHelp() {
			return "Returns ordered list of unselected values";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> GET_OPTIONS = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class, "getOptions",
			Map.class) {
		@Override
		public Map invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			LinkedHashMap r = new LinkedHashMap();
			for (Object o : targetObject.getField().getOptions()) {
				Option op = (Option) o;
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
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> GET_OPTION = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class, "getOption",
			String.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiSelectField f = (FormPortletMultiSelectField) targetObject.getField();
			return f.getOptionByKey(params[0]).getName();
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
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> PUT_OPTION = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class, "putOption",
			Boolean.class, Object.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiSelectField f = (FormPortletMultiSelectField) targetObject.getField();
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
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> REMOVE_OPTION = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"removeOption", Boolean.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiSelectField f = (FormPortletMultiSelectField) targetObject.getField();
			f.removeOptionByKey((String) params[0]);
			return true;
		}

		@Override
		protected String getHelp() {
			return "Removes the option from the multi-select field.";
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
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> CLEAR_OPTIONS = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class,
			"clearOptions", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiSelectField f = (FormPortletMultiSelectField) targetObject.getField();
			f.clear();
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
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> SET_OPTIONS = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class, "setOptions",
			Boolean.class, Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiSelectField f = (FormPortletMultiSelectField) targetObject.getField();
			f.clear();
			f.addOptions((Map<Object, String>) params[0]);
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
	private static final AmiAbstractMemberMethod<MultiSelectQueryField> ADD_OPTIONS = new AmiAbstractMemberMethod<MultiSelectQueryField>(MultiSelectQueryField.class, "addOptions",
			Boolean.class, Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, MultiSelectQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletMultiSelectField f = (FormPortletMultiSelectField) targetObject.getField();
			f.addOptions((Map<Object, String>) params[0]);
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
		return "FormMultiSelectField";
	}

	@Override
	public String getVarTypeDescription() {
		return "A special type of field within a FormPanel that allows multiple options to be selected.";
	}

	@Override
	public Class<MultiSelectQueryField> getVarType() {
		return MultiSelectQueryField.class;
	}

	@Override
	public Class<MultiSelectQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormMultiSelectField INSTANCE = new AmiWebScriptMemberMethods_FormMultiSelectField();
}
