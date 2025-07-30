package com.f1.ami.web.amiscript;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebAbstractPortlet;
import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyle;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.utils.WebRectangle;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormField extends AmiWebScriptBaseMemberMethods<QueryField> {

	private AmiWebScriptMemberMethods_FormField() {
		super();
		addMethod(GET_VALUE, "value");
		addMethod(GET_FIELD_VALUE);
		addMethod(SET_FIELD_VALUE);
		addMethod(SET_VALUE);
		addMethod(GET_PANEL);
		addMethod(GET_STYLE_SET);
		addMethod(SET_POSITION);
		addMethod(SET_POSITION2);
		addMethod(GET_POSITION, "position");
		addMethod(SET_DISPLAY_MODE);
		addMethod(GET_DISPLAY_MODE, "displayMode");
		addMethod(RESET_POSITION);
		addMethod(IS_AT_DEFAULT_POSITION);
		addMethod(SET_CSS_CLASS);
		addMethod(RESET_CSS_CLASS);
		addMethod(GET_CSS_CLASS);
		addMethod(SET_DISABLED);
		addMethod(RESET_DISABLED);
		addMethod(GET_DISABLED, "disabled");
		addMethod(GET_LABEL, "label");
		addMethod(GET_VARIABLE_NAME, "variableName");
		addMethod(IS_TRANSIENT);
		addMethod(GET_LABEL_TOOLTIP, "labelTooltip");
		addMethod(SET_LABEL_TOOLTIP);
		addMethod(EXPORT_CONFIG);
		addMethod(SET_VISIBLE);
		addMethod(IS_VISIBLE);
		addMethod(IS_FOCUSED);
		addMethod(FOCUS);
		//addMethod(this.setCallback); //TODO:need to make setCallback transient
	}

	private static final AmiAbstractMemberMethod<QueryField> FOCUS = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "focus", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			FormPortletField field = targetObject.getField();
			if (field.canFocus()) {
				PortletHelper.ensureVisible(targetObject.getForm().getInnerPortlet());
				field.focus();
				return true;
			}
			return false;
		}
		@Override
		protected String getHelp() {
			return "Requests user input be focused on this field, will make panel visible if necessary. Returns false if this field can not be focused. Note that the focus is not immediate, meaning a call to isFocused() directly after calling focus may still return false";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> GET_LABEL = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getLabel", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getLabel();
		}
		@Override
		protected String getHelp() {
			return "Returns the label associated with this field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> GET_VARIABLE_NAME = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getVariableName", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getName();
		}
		@Override
		protected String getHelp() {
			return "Returns the variable name associated with this field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> GET_VALUE = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getValue", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getValue();
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with this field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> GET_PANEL = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getPanel", AmiWebQueryFormPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return PortletHelper.findParentByType(targetObject.getField().getForm(), AmiWebAbstractPortlet.class);
		}
		@Override
		protected String getHelp() {
			return "Returns the panel that contains this field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> SET_VALUE = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setValue", Boolean.class, Object.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.setValue(params[0]);
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value " };
		}
		@Override
		protected String getHelp() {
			return "Set this field's value. This is a generic set method that is available for every field in AMI. For FormRangeSlider and FormImage field, it is advisable to use their own setValue() method.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> SET_FIELD_VALUE = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setFieldValue", Boolean.class,
			String.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String name = (String) params[0];
				Object value = params[1];
				targetObject.setValue(name, value);
			} catch (Exception e) {
				return false;
			}
			return true;
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
		protected String getHelp() {
			return "Sets the key value pair to this portlet's attributes and returns true if successful. For key, put in the field's variable name, then set the value.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> SET_POSITION = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setPosition", Boolean.class, Integer.class,
			Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer x = (Integer) params[0];
			Integer y = (Integer) params[1];
			Integer w = (Integer) params[2];
			Integer h = (Integer) params[3];
			if (x != null)
				targetObject.setOverrideLeftPosPx(x);
			if (y != null)
				targetObject.setOverrideTopPosPx(y);
			if (w != null)
				targetObject.setOverrideWidthPx(w);
			if (h != null)
				targetObject.setOverrideHeightPx(h);
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "w", "h" };
		};

		protected String[] buildParamDescriptions() {
			return new String[] { "x-coordinate", "y-coordinate", "Width", "Height" };
		};

		@Override
		protected String getHelp() {
			return "Sets the field position. Specify (x,y) position, width, and height in pixels. Passing null to an argument is equivalent to not setting a value for that specific argument.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> SET_POSITION2 = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setPosition", Boolean.class,
			WebRectangle.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			WebRectangle rect = (WebRectangle) params[0];
			if (rect == null) {
				targetObject.resetOverridePosition();
			} else {
				targetObject.setOverrideLeftPosPx(rect.getLeft());
				targetObject.setOverrideTopPosPx(rect.getTop());
				targetObject.setOverrideWidthPx(rect.getWidth());
				targetObject.setOverrideHeightPx(rect.getHeight());
			}
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "rect" };
		};

		protected String[] buildParamDescriptions() {
			return new String[] { "rectangle" };
		};

		@Override
		protected String getHelp() {
			return "Sets the position, if the position is null then resets the overrides";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> GET_POSITION = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getPosition", WebRectangle.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getPositionAsRect();
		}

		@Override
		protected String getHelp() {
			return "Returns a Rectangle object representing the field's position as (x,y,w,h).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> SET_DISPLAY_MODE = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setDisplayMode", Boolean.class,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			String input = (String) params[0];
			if ("ABSOLUTE".equals(input)) {
				targetObject.setOverrideVisible(true);
				return null;
			} else if ("HTML".equals(input)) {
				targetObject.setOverrideVisible(false);
				return null;
			} else {
				throw new RuntimeException("Invalid argument: Must pass either \"ABSOLUTE\" or \"HTML\" as input to setDisplayMode().");
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "mode" };
		};

		protected String[] buildParamDescriptions() {
			return new String[] { "Display mode" };
		};

		@Override
		protected String getHelp() {
			return "DEPRECATED, use setVisible instead. Sets display mode of field. Setting to \"ABSOLUTE\" will display field using absolute positioning. Setting to \"HTML\" will allow field to be displayed using the form HTML. Any other input will throw an exception.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> GET_DISPLAY_MODE = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getDisplayMode", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().isVisible() ? "ABSOLUTE" : "HTML";
		}

		@Override
		protected String getHelp() {
			return "DEPRECATED, use isVisible instead. Returns display type of field. Will return \"ABSOLUTE\" if field is displayed using absolute positioning. Will return \"HTML\" if field is displayed using form HTML.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> RESET_POSITION = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "resetPosition", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.resetOverridePosition();
			return true;
		}

		@Override
		protected String getHelp() {
			return "Resets field position to default position specified in field editor.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> IS_AT_DEFAULT_POSITION = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "isAtDefaultPosition",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isAtDefaultPosition();
		}

		@Override
		protected String getHelp() {
			return "Returns whether field is at the default position specified in field editor.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> GET_FIELD_VALUE = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getFieldValue", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				AmiWebQueryFormPortlet form = targetObject.getForm();
				return form.getFieldValues().get(params[0]);
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
			return new String[] { "fieldVarName " };
		}
		@Override
		protected String getHelp() {
			return "Returns the value of the field with the associated varName for the form that owns this field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> SET_CSS_CLASS = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setCssClass", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			String cssClass = Caster_String.INSTANCE.cast(params[0]);
			if (cssClass == null || !targetObject.getService().getCustomCssManager().getClassNames(targetObject.getForm().getStylePeer().getParentStyle()).contains(cssClass)) {
				return false;
			}
			targetObject.getStylePeer().putValueOverride(AmiWebStyleConsts.CODE_FLD_CSS, cssClass);
			return true;
		}

		@Override
		protected String getHelp() {
			return "Sets CSS class. Returns empty string for no css.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "cssStyle" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> GET_CSS_CLASS = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getCssClass", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStylePeer().resolveValue(AmiWebStyleConsts.CODE_FLD_CSS);
		}

		@Override
		protected String getHelp() {
			return "Returns the CSS class.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<QueryField> RESET_CSS_CLASS = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "resetCssClass", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStylePeer().removeValueOverride(AmiWebStyleConsts.CODE_FLD_CSS);
		}

		@Override
		protected String getHelp() {
			return "Resets CSS class to default (as configured in AMI editor).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<QueryField> GET_STYLE_SET = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getStyleSet", AmiWebStyle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStylePeer();
		}
		@Override
		protected String getHelp() {
			return "Returns the StyleSet for this form field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> GET_DISABLED = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getDisabled", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getOverrideDisabled() == null ? targetObject.getDisabled() : targetObject.getOverrideDisabled();
		}

		@Override
		protected String getHelp() {
			return "Returns true if field is disabled, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<QueryField> SET_DISABLED = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setDisabled", Boolean.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			boolean flag = Caster_Boolean.INSTANCE.cast(params[0]);
			targetObject.setOverrideDisabled(flag);
			return true;
		}
		@Override
		protected String getHelp() {
			return "Set the disabled status of the field (true for disabling, false for enabling).";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "disabled" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "true to disable, false to enable" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<QueryField> RESET_DISABLED = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "resetDisabled", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setDisabled(targetObject.getDisabled());
			return true;
		}

		@Override
		protected String getHelp() {
			return "Resets the disabled flag to default (as configured in AMI editor).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<QueryField> IS_TRANSIENT = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "isTransient", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isTransient();
		}

		@Override
		protected String getHelp() {
			return "Returns whether a field is transient.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<QueryField> setCallback = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setCallback", Boolean.class, String.class,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			String callbackType = Caster_String.INSTANCE.cast(params[0]);
			String callbackScript = Caster_String.INSTANCE.cast(params[1]);

			AmiWebAmiScriptCallback callback = targetObject.getAmiScriptCallbacks().getCallback(callbackType);
			//			String origScript = callback.getAmiscript(true);
			callback.setAmiscript(callbackScript, true);

			//			AmiWebService service = targetObject.getForm().getService();
			//			AmiDebugManager debugManager = execInstance == null ? service.getDebugManager() : execInstance.getDebugManager();//service.getDebugManager();
			//
			//			byte success = callback.ensureCompiled(debugManager);
			//			if (success == AmiWebAmiScriptCallback.COMPILE_ERROR) {
			//				callback.setAmiscript(origScript, b);
			//				callback.ensureCompiled(debugManager);
			//				return false;
			//			}
			return true;
		}

		@Override
		protected String getHelp() {
			return "Sets the field callback.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "callbackType", "callbackScript" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<QueryField> GET_LABEL_TOOLTIP = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "getLabelTooltip", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHelp();
		}
		@Override
		protected String getHelp() {
			return "Returns the tooltip associated with this field's label.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<QueryField> SET_LABEL_TOOLTIP = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setLabelTooltip", Boolean.class,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			String tooltip = Caster_String.INSTANCE.castOr(params[0], null);
			targetObject.setHelp(tooltip, true);
			return true;
		}

		@Override
		protected String getHelp() {
			return "Sets the label tooltip.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "tooltip" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<QueryField> EXPORT_CONFIG = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "exportConfig", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Map<String, Object> sink = new HashMap<String, Object>();
			targetObject.getForm().getFieldJson(sink, targetObject);
			return sink;
		}
		@Override
		protected String getHelp() {
			return "Exports this field's configuration to a map.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<QueryField> SET_VISIBLE = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "setVisible", Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Boolean visible = Caster_Boolean.INSTANCE.castOr(params[0], null);
			if (visible == null)
				return false;
			targetObject.getField().setVisible(visible);
			return true;
		}

		@Override
		protected String getHelp() {
			return "Sets the visibility of this field. Returns false iff the argument is null, true otherwise.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "visible" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "true for visible, false for hidden." };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> IS_VISIBLE = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "isVisible", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().isVisible();
		}

		@Override
		protected String getHelp() {
			return "Returns true if this field is visible, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<QueryField> IS_FOCUSED = new AmiAbstractMemberMethod<QueryField>(QueryField.class, "isFocused", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, QueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().isFocused();
		}

		@Override
		protected String getHelp() {
			return "Returns true if this field has focus, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormField";
	}
	@Override
	public String getVarTypeDescription() {
		return "Represents a field within a FormPanel.";
	}
	@Override
	public Class<QueryField> getVarType() {
		return QueryField.class;
	}
	@Override
	public Class<QueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormField INSTANCE = new AmiWebScriptMemberMethods_FormField();
}
