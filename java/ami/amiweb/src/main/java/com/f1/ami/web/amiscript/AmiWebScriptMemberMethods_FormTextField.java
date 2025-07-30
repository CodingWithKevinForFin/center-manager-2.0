package com.f1.ami.web.amiscript;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.AbstractDmQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.TextQueryField;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormTextField extends AmiWebScriptBaseMemberMethods<TextQueryField> {

	private AmiWebScriptMemberMethods_FormTextField() {
		super();
		addMethod(GET_VALUE, "value");
		addMethod(SET_OPTIONS);
		addMethod(CLEAR_OPTIONS);
		addMethod(RESET_OPTIONS);
		addMethod(GET_OPTIONS, "options");
		addMethod(ADD_OPTIONS);
		addMethod(PUT_OPTION);
		addMethod(REMOVE_OPTION);
		addMethod(SET_AUTOCOMPLETE_DELIMITER);
		addMethod(GET_AUTOCOMPLETE_DELIMITER, "delimiter");
		addMethod(SET_DISPLAY_SORT_OPTION);
		addMethod(GET_DISPLAY_SORT_OPTION, "displaySortOption");
		addMethod(IS_SHOW_OPTIONS_IMMEDIATELY, "isShowOptionsImmediately");
		addMethod(SET_SHOW_OPTIONS_IMMEDIATELY);
		addMethod(IS_SUBSTRING_MATCHING, "isSubstringMatching");
		addMethod(SET_SUBSTRING_MATCHING);
		addMethod(GET_CURSOR, "cursor");
		addMethod(SET_CURSOR);
		addMethod(SET_SELECTED);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONAUTOCOMPLETED);
	}

	public static final AmiAbstractMemberMethod<TextQueryField> SET_SHOW_OPTIONS_IMMEDIATELY = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class,
			"setShowOptionsImmediately", Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Boolean showOptionsImmediately = Caster_Boolean.PRIMITIVE.cast(params[0]);
				targetObject.setShowOptionsImmediately(showOptionsImmediately, true);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		@Override
		protected String getHelp() {
			return "Setting it to true will cause the field to shows dropdown with options immediately after the field is focused. Setting to false will show the dropdown as characters are being typed. Returns true on successful set, false otherwise.";
		}
		protected String[] buildParamNames() {
			return new String[] { "showOptionsImmediately" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "true or false" };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<TextQueryField> IS_SHOW_OPTIONS_IMMEDIATELY = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class,
			"isShowOptionsImmediately", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isShowOptionsImmediately(true);
		}

		@Override
		protected String getHelp() {
			return "Returns true if dropdown options are being shown immediately after the field is focused, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	public static final AmiAbstractMemberMethod<TextQueryField> SET_SUBSTRING_MATCHING = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "setSubstringMatching",
			Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Boolean performSubstringMatching = Caster_Boolean.PRIMITIVE.cast(params[0]);
				targetObject.setPerformSubstringMatching(performSubstringMatching, true);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		@Override
		protected String getHelp() {
			return "Setting it to true will cause the field to use substring matching when searching for values in the field. Setting to false will perform searching based on starting characters. Returns true on successful set, false otherwise.";
		}
		protected String[] buildParamNames() {
			return new String[] { "performSubstringMatching" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "true or false" };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<TextQueryField> IS_SUBSTRING_MATCHING = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "isSubstringMatching",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPerformSubstringMatching(true);
		}

		@Override
		protected String getHelp() {
			return "Returns true if substring matching is used for searching in the field, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	public static final AmiAbstractMemberMethod<TextQueryField> SET_DISPLAY_SORT_OPTION = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class,
			"setDisplayValueSortOption", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	public static final AmiAbstractMemberMethod<TextQueryField> GET_DISPLAY_SORT_OPTION = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class,
			"getDisplayValueSortOption", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	public static final AmiAbstractMemberMethod<TextQueryField> SET_AUTOCOMPLETE_DELIMITER = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class,
			"setAutocompleteDelimiter", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			String delim = Caster_String.INSTANCE.cast(params[0]);
			if (SH.length(delim) > TextQueryField.AUTOCOMPLETE_DELIM_MAXLEN)
				return false;
			targetObject.setAutocompleteDelimiter(delim, true);
			return true;
		}

		@Override
		protected String getHelp() {
			return "Sets the delimiter for autocomplete values. (i.e. setting delimiter to \"|\" will reset the autocomplete after each occurence of \"|\").";
		}
		protected String[] buildParamNames() {
			return new String[] { "delimiter" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Delimiter for autocomplete values. Length cannot be greater than 5 characters." };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	public static final AmiAbstractMemberMethod<TextQueryField> GET_AUTOCOMPLETE_DELIMITER = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class,
			"getAutocompleteDelimiter", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAutocompleteDelimeter(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the autocomplete delimiter.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	public static final AmiAbstractMemberMethod<TextQueryField> GET_VALUE = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "getValue", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns this field's value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<TextQueryField> SET_OPTIONS = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "setOptions", TextQueryField.class,
			Collection.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Collection<?> options = (Collection<?>) params[0];
			if (options == null)
				return null;
			List<String> strings = CH.castAll(options, String.class, false);
			targetObject.getAutocompleteExtension().setSuggestions(strings);
			return targetObject;
		}
		protected String[] buildParamNames() {
			return new String[] { "options" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "list of options" };
		}

		@Override
		protected String getHelp() {
			return "sets the options for textfield";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	public static final AmiAbstractMemberMethod<TextQueryField> RESET_OPTIONS = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "resetOptions", Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.resetOptions();
			return null;
		}

		@Override
		protected String getHelp() {
			return "If options have been manually set using amiscript, this resets to the default values";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	public static final AmiAbstractMemberMethod<TextQueryField> CLEAR_OPTIONS = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "clearOptions", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getAutocompleteExtension().clearSuggestions();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Removes all options";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	public static final AmiAbstractMemberMethod<TextQueryField> GET_OPTIONS = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "getOptions", Collection.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return new HashSet(targetObject.getAutocompleteExtension().getSuggestions());
		}
		@Override
		protected String getHelp() {
			return "Returns a set of all options";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	public static final AmiAbstractMemberMethod<TextQueryField> ADD_OPTIONS = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "addOptions", Boolean.class,
			Collection.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Collection<?> options = (Collection<?>) params[0];
			if (options == null)
				return null;
			List<String> strings = CH.castAll(options, String.class, false);
			targetObject.getAutocompleteExtension().addSuggestions(strings);
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "options" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Options to be added" };
		}

		@Override
		protected String getHelp() {
			return "Adds to the field's options the values in the list";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	public static final AmiAbstractMemberMethod<TextQueryField> PUT_OPTION = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "putOption", Boolean.class,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			String option = (String) params[0];
			targetObject.getAutocompleteExtension().putSuggestion(option);
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "option" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Option to be added" };
		}

		@Override
		protected String getHelp() {
			return "Adds the option to the textfield";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	public static final AmiAbstractMemberMethod<TextQueryField> REMOVE_OPTION = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "removeOption", Boolean.class,
			Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			String option = (String) params[0];
			targetObject.getAutocompleteExtension().removeSuggestion(option);
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "option" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Option to be removed" };
		}

		@Override
		protected String getHelp() {
			return "Removes the option from the textfield";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	public static final AmiAbstractMemberMethod<TextQueryField> GET_CURSOR = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "getCursor", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getCursorPosition();
		}

		@Override
		protected String getHelp() {
			return "Returns this cursors position";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TextQueryField> SET_CURSOR = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "setCursor", Integer.class,
			Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer i = (Integer) params[0];
			int r = targetObject.getField().getCursorPosition();
			targetObject.getField().setCursorPosition(i);
			return r;
		}
		protected String[] buildParamNames() {
			return new String[] { "position" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position, with zero being on the left)" };
		}

		@Override
		protected String getHelp() {
			return "Sets this cursors position";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<TextQueryField> SET_SELECTED = new AmiAbstractMemberMethod<TextQueryField>(TextQueryField.class, "setSelected", Object.class,
			Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer start = (Integer) params[0];
			Integer end = (Integer) params[1];
			String value = (String) targetObject.getValue();
			if (start == null && end == null) {
				targetObject.getField().setSelection(-1, -1);
				return true;
			}
			if (value != null) {
				int s = start == null ? 0 : MH.clip((int) start, 0, value.length());
				int e = end == null ? value.length() : MH.clip((int) end, 0, value.length());
				targetObject.getField().setSelection(s, e);
				return true;
			}
			return false;
		}
		protected String[] buildParamNames() {
			return new String[] { "start", "end" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "start position which is inclusive", "end position, which is exclusive" };
		}

		@Override
		protected String getHelp() {
			return "selects  a range of text";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormTextField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Text Field";
	}

	@Override
	public Class<TextQueryField> getVarType() {
		return TextQueryField.class;
	}

	@Override
	public Class<TextQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormTextField INSTANCE = new AmiWebScriptMemberMethods_FormTextField();

}
