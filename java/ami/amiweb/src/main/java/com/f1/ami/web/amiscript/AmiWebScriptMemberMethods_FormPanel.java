package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.queryfield.CheckboxQueryField;
import com.f1.ami.web.form.queryfield.ColorGradientPickerQueryField;
import com.f1.ami.web.form.queryfield.ColorPickerQueryField;
import com.f1.ami.web.form.queryfield.DateQueryField;
import com.f1.ami.web.form.queryfield.DateRangeQueryField;
import com.f1.ami.web.form.queryfield.DateTimeQueryField;
import com.f1.ami.web.form.queryfield.DivQueryField;
import com.f1.ami.web.form.queryfield.FormButtonQueryField;
import com.f1.ami.web.form.queryfield.ImageQueryField;
import com.f1.ami.web.form.queryfield.MultiCheckboxQueryField;
import com.f1.ami.web.form.queryfield.MultiSelectQueryField;
import com.f1.ami.web.form.queryfield.PasswordQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.RadioButtonQueryField;
import com.f1.ami.web.form.queryfield.RangeQueryField;
import com.f1.ami.web.form.queryfield.SelectQueryField;
import com.f1.ami.web.form.queryfield.SubRangeQueryField;
import com.f1.ami.web.form.queryfield.TextQueryField;
import com.f1.ami.web.form.queryfield.TimeQueryField;
import com.f1.ami.web.form.queryfield.TimeRangeQueryField;
import com.f1.ami.web.form.queryfield.UploadQueryField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormPanel extends AmiWebScriptBaseMemberMethods<AmiWebQueryFormPortlet> {

	private static final AddField ADD_CHECKBOX_FIELD = new AddField(CheckboxQueryField.class, "addCheckboxField", QueryField.TYPE_ID_CHECKBOX);
	private static final AddField ADD_FILE_UPLOAD_FIELD = new AddField(UploadQueryField.class, "addFileUploadField", QueryField.TYPE_ID_FILE_UPLOAD);
	private static final AddField ADD_RADIO_BUTTON_FIELD = new AddField(RadioButtonQueryField.class, "addRadioButtonField", QueryField.TYPE_ID_RADIO);
	private static final AddField ADD_PASSWORD_FIELD = new AddField(PasswordQueryField.class, "addPasswordField", QueryField.TYPE_ID_PASSWORD);
	private static final AddField ADD_MULTI_SELECT_FIELD = new AddField(MultiSelectQueryField.class, "addMultiSelectField", QueryField.TYPE_ID_MULTI_SELECT);
	private static final AddField ADD_MULTI_CHECKBOX_FIELD = new AddField(MultiCheckboxQueryField.class, "addMultiCheckboxField", QueryField.TYPE_ID_MULTI_CHECKBOX);
	private static final AddField ADD_IMAGE_FIELD = new AddField(ImageQueryField.class, "addImageField", QueryField.TYPE_ID_IMAGE);
	private static final AddField ADD_TIME_RANGE_FIELD = new AddField(TimeRangeQueryField.class, "addTimeRangeField", QueryField.TYPE_ID_TIMERANGE);
	private static final AddField ADD_TIME_FIELD = new AddField(TimeQueryField.class, "addTimeField", QueryField.TYPE_ID_TIME);
	private static final AddField ADD_DATE_TIME_FIELD = new AddField(DateTimeQueryField.class, "addDateTimeField", QueryField.TYPE_ID_DATETIME);
	private static final AddField ADD_DATE_RANGE_FIELD = new AddField(DateRangeQueryField.class, "addDateRangeField", QueryField.TYPE_ID_DATERANGE);
	private static final AddField ADD_DATE_FIELD = new AddField(DateQueryField.class, "addDateField", QueryField.TYPE_ID_DATE);
	private static final AddField ADD_COLOR_PICKER_FIELD = new AddField(ColorPickerQueryField.class, "addColorPickerField", QueryField.TYPE_ID_COLOR_PICKER);
	private static final AddField ADD_COLOR_GRADIENT_PICKER_FIELD = new AddField(ColorGradientPickerQueryField.class, "addColorGradientPickerField",
			QueryField.TYPE_ID_COLOR_GRADIENT_PICKER);
	private static final AddField ADD_DIV_FIELD = new AddField(DivQueryField.class, "addDivField", QueryField.TYPE_ID_DIV);
	private static final AddField ADD_SELECT_FIELD = new AddField(SelectQueryField.class, "addSelectField", QueryField.TYPE_ID_SELECT);
	private static final AddField ADD_TEXT_AREA_FIELD = new AddField(DivQueryField.class, "addTextAreaField", QueryField.TYPE_ID_TEXT_AREA);
	private static final AddField ADD_TEXT_FIELD = new AddField(TextQueryField.class, "addTextField", QueryField.TYPE_ID_TEXT);
	private static final AddField ADD_BUTTON_FIELD = new AddField(FormButtonQueryField.class, "addButtonField", QueryField.TYPE_ID_BUTTON);

	private AmiWebScriptMemberMethods_FormPanel() {
		super();

		addMethod(SET_HTML);
		addMethod(GET_HTML, "html");
		addMethod(RESET_FIELDS);
		addMethod(GET_FIELD_VALUES);
		addMethod(GET_FIELD_VALUE);
		addMethod(GET_FIELD);
		addMethod(RESET_FIELD_POSITIONS);
		addMethod(CREATE_JS_CALLBACK);
		addMethod(ADD_SLIDER_FIELD);
		addMethod(ADD_RANGE_SLIDER_FIELD);
		addMethod(ADD_BUTTON_FIELD);
		addMethod(ADD_TEXT_FIELD);
		addMethod(ADD_TEXT_AREA_FIELD);
		addMethod(ADD_SELECT_FIELD);
		addMethod(ADD_DIV_FIELD);
		addMethod(ADD_COLOR_GRADIENT_PICKER_FIELD);
		addMethod(ADD_COLOR_PICKER_FIELD);
		addMethod(ADD_DATE_FIELD);
		addMethod(ADD_DATE_RANGE_FIELD);
		addMethod(ADD_DATE_TIME_FIELD);
		addMethod(ADD_TIME_FIELD);
		addMethod(ADD_TIME_RANGE_FIELD);
		addMethod(ADD_IMAGE_FIELD);
		addMethod(ADD_MULTI_CHECKBOX_FIELD);
		addMethod(ADD_MULTI_SELECT_FIELD);
		addMethod(ADD_PASSWORD_FIELD);
		addMethod(ADD_RADIO_BUTTON_FIELD);
		addMethod(ADD_FILE_UPLOAD_FIELD);
		addMethod(ADD_CHECKBOX_FIELD);
		addMethod(GET_FIELDS);
		addMethod(GET_FIELDS_VARIABLE_NAMES);
		addMethod(GET_TRANSIENT_FIELDS);
		addMethod(GET_FOCUSED_FIELD);
		addMethod(REMOVE_FIELD);
		addMethod(IMPORT_FIELD);
		registerCallbackDefinition(AmiWebQueryFormPortlet.CALLBACK_ONAMIJSCALLBACK);
	}

	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> GET_HTML = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class, "getHtml",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHtmlTemplate(true);
		}
		@Override
		protected String getHelp() {
			return "get the HTML for this panel";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> SET_HTML = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class, "setHtml",
			String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String r = targetObject.getHtmlTemplate(true);
				targetObject.setHtmlTemplate((String) params[0], true);
				return r;
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "html" };
		}
		@Override
		protected String getHelp() {
			return "Set the HTML for this panel, returns old html";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> RESET_FIELDS = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"resetFields", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				targetObject.resetFormFields();
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		@Override
		protected String getHelp() {
			return "Resets the field value to its initial state. For example, a Radio Button field that is set to ON will be reset to its initial state, which is OFF; a Check Box field that is checked will have its box unchecked.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> GET_FIELD_VALUES = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"getFieldValues", LinkedHashMap.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return new LinkedHashMap(targetObject.getFieldValues());
			} catch (Exception e) {
				return false;
			}
		}
		@Override
		protected String getHelp() {
			return "Return a map of IDs to values for all fields of this form.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> GET_FIELD_VALUE = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"getFieldValue", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Set<String> m = targetObject.getQueryFieldNames();
				if (m.contains((String) params[0])) {
					QueryField<?> queryField = targetObject.getFieldByVarName((String) params[0]);
					return queryField.getValue();
				} else
					return targetObject.getFieldValues().get(params[0]);
			} catch (Exception e) {
				return false;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "varName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "fieldVarName" };
		}
		@Override
		protected String getHelp() {
			return "Return the value of the field with the associated varName.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> GET_FIELD = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class, "getField",
			QueryField.class, String.class) {
		@Override
		public QueryField<?> invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getFieldValueNoThrow((String) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "varName" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "fieldVarName" };
		}
		@Override
		protected String getHelp() {
			return "Return the field associate varName, or null.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> RESET_FIELD_POSITIONS = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"resetFieldPositions", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.resetAllQueryFieldPositions();
			return true;
		}

		@Override
		protected String getHelp() {
			return "Reset all fields in form to default positions specified in field editor.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> CREATE_JS_CALLBACK = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"createJsCallback", String.class, true, String.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String name = (String) params[0];
				StringBuilder r = new StringBuilder();
				r.append("portletManager.getPortlet('");
				r.append(targetObject.getPortletId()).append("').callBack('").append(name).append("',{");
				for (int i = 1; i < params.length; i += 2) {
					WebHelper.quote((String) params[i], r);
					r.append(':');
					Object value = params[i + 1];
					if (value instanceof String)
						WebHelper.quote((String) value, r);
					else
						r.append(params[i + 1]);
				}
				r.append("});");
				return r.toString();
			} catch (Exception e) {
				return false;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "buttonId", "attributes" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "id of the button to execute button press on", "list of attributes to include " };
		}
		@Override
		protected String getHelp() {
			return "Generates javascript that can be embedded in the HTML and used to make callbacks.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> GET_FIELDS = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class, "getFields",
			List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getFieldsById().values());
		}

		@Override
		protected String getHelp() {
			return "Returns a list of fields.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> GET_TRANSIENT_FIELDS = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"getTransientFields", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			List<QueryField<?>> l = new ArrayList<QueryField<?>>();
			for (QueryField<?> f : targetObject.getFieldsById().values()) {
				if (f.isTransient()) {
					l.add(f);
				}
			}
			return l;
		}

		@Override
		protected String getHelp() {
			return "Returns a list of transient fields.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> GET_FIELDS_VARIABLE_NAMES = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(
			AmiWebQueryFormPortlet.class, "getFieldsVariableNames", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getQueryFieldNames());
		}

		@Override
		protected String getHelp() {
			return "Returns a list of fields' variable names.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> ADD_SLIDER_FIELD = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"addSliderField", RangeQueryField.class, String.class, String.class, Number.class, Number.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebQueryFormPortlet form = targetObject;
			String varname = Caster_String.INSTANCE.cast(params[0]);
			String label = Caster_String.INSTANCE.cast(params[1]);
			Double min = Caster_Double.INSTANCE.cast(params[2]);
			Double max = Caster_Double.INSTANCE.cast(params[3]);
			Double step = Caster_Double.INSTANCE.cast(params[4]);

			AmiWebFormFieldFactory<?> factory = form.getService().getFormFieldFactory(QueryField.TYPE_ID_RANGE);
			QueryField<?> field = factory.createQueryField(form);
			field.setTransient(true);
			try {
				varname = SH.getNextId(targetObject.getService().getVarsManager().toTransientId(AmiWebUtils.toValidVarname(varname)), form.getQueryFieldNames(), 2);
				field.setVarName(varname);
				field.setLabel(label);
				field.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
				field.setTopPosPx(20);
				field.setWidthPx(factory.getDefaultWidth());
				field.setHeightPx(factory.getDefaultHeight());
				RangeQueryField range = (RangeQueryField) field;
				if (min != null && max != null)
					range.setRange(min, max);
				if (step != null)
					range.setStep(step);
				range.getField().setValue(min);
				field.updateAri();
				form.addQueryField(field, true);
			} catch (Exception e) {
				return null;
			}

			return field;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "varname", "label", "min", "max", "step" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "varname", "label", "min", "max", "step" };
		}
		@Override
		protected String getHelp() {
			return "Adds a transient Slider Field.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> ADD_RANGE_SLIDER_FIELD = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"addRangeSliderField", SubRangeQueryField.class, String.class, String.class, Number.class, Number.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebQueryFormPortlet form = targetObject;
			String varname = Caster_String.INSTANCE.cast(params[0]);
			String label = Caster_String.INSTANCE.cast(params[1]);
			Double min = Caster_Double.INSTANCE.cast(params[2]);
			Double max = Caster_Double.INSTANCE.cast(params[3]);
			Double step = Caster_Double.INSTANCE.cast(params[4]);

			AmiWebFormFieldFactory<?> factory = form.getService().getFormFieldFactory(QueryField.TYPE_ID_SUBRANGE);
			QueryField<?> field = factory.createQueryField(form);
			field.setTransient(true);
			try {
				varname = SH.getNextId(targetObject.getService().getVarsManager().toTransientId(AmiWebUtils.toValidVarname(varname)), form.getQueryFieldNames(), 2);
				field.setVarName(varname);
				field.setLabel(label);
				field.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
				field.setTopPosPx(20);
				field.setWidthPx(factory.getDefaultWidth());
				field.setHeightPx(factory.getDefaultHeight());
				SubRangeQueryField range = (SubRangeQueryField) field;
				if (min != null && max != null)
					range.setRange(min, max);
				if (step != null)
					range.setStep(step);
				field.updateAri();
				range.getField().setValue(new Tuple2<Double, Double>(min, max));
				form.addQueryField(field, true);
			} catch (Exception e) {
				return null;
			}

			return field;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "varname", "label", "min", "max", "step" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "varname", "label", "min", "max", "step" };
		}
		@Override
		protected String getHelp() {
			return "Adds a transient Range Slider Field.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> REMOVE_FIELD = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"removeField", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebQueryFormPortlet form = targetObject;
			String varname = Caster_String.INSTANCE.cast(params[0]);

			QueryField<?> field = form.getFieldByVarName(varname);
			if (field == null || !field.isTransient())
				return false;
			else {
				form.removeQueryField(field, true);
				field.onRemoving();
				targetObject.removeFieldByVarName(varname);
				return true;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "varname" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the field's name" };
		}
		@Override
		protected String getHelp() {
			return "Removes a transient field given the field's name.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormPanel";
	}
	@Override
	public String getVarTypeDescription() {
		return "A visualization Panel of type Form";
	}
	@Override
	public Class<AmiWebQueryFormPortlet> getVarType() {
		return AmiWebQueryFormPortlet.class;
	}
	@Override
	public Class<AmiWebQueryFormPortlet> getVarDefaultImpl() {
		return null;
	}

	static private class AddField extends AmiAbstractMemberMethod<AmiWebQueryFormPortlet> {

		private String queryFieldId;

		private AddField(Class<? extends QueryField> type, String methodName, String queryFieldId) {
			super(AmiWebQueryFormPortlet.class, methodName, type, String.class, String.class);
			this.queryFieldId = queryFieldId;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebQueryFormPortlet form = targetObject;
			String varname = Caster_String.INSTANCE.cast(params[0]);
			String label = Caster_String.INSTANCE.cast(params[1]);

			AmiWebFormFieldFactory<?> factory = form.getService().getFormFieldFactory(queryFieldId);
			QueryField<?> field = factory.createQueryField(form);
			field.setTransient(true);
			try {
				field.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
				field.setTopPosPx(20);
				field.setWidthPx(factory.getDefaultWidth());
				field.setHeightPx(factory.getDefaultHeight());
				varname = SH.getNextId(targetObject.getService().getVarsManager().toTransientId(AmiWebUtils.toValidVarname(varname)), form.getQueryFieldNames(), 2);
				field.setVarName(varname);
				field.setLabel(label);
				field.updateAri();
				form.addQueryField(field, true);
			} catch (Exception e) {
				LH.warning(log, form.logMe() + ": ", e);
				return null;
			}

			return field;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "varname", "label" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "varname", "label" };
		}
		@Override
		protected String getHelp() {
			return "Adds a transient field. Not the varname will be prefixed with the transient prefix (default is TRANSIENT_)";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> IMPORT_FIELD = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"importField", QueryField.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Map config = (Map) params[0];
			if (CH.isEmpty(config))
				return null;
			try {
				return targetObject.importField(config, true, true);
			} catch (Exception e) {
				LH.warning(log, targetObject.logMe() + ": ", e);
				return null;
			}

		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "config" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the field's config, see FormField::exportConfig()" };
		}
		@Override
		protected String getHelp() {
			return "add a transient field to this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebQueryFormPortlet> GET_FOCUSED_FIELD = new AmiAbstractMemberMethod<AmiWebQueryFormPortlet>(AmiWebQueryFormPortlet.class,
			"getFocusedField", QueryField.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebQueryFormPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			QueryField<?> field = targetObject.getFocusedField();
			return field;
		}
		@Override
		protected String getHelp() {
			return "get the HTML for this panel";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiWebScriptMemberMethods_FormPanel INSTANCE = new AmiWebScriptMemberMethods_FormPanel();
}
