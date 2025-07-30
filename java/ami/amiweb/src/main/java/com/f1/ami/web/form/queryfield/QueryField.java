package com.f1.ami.web.form.queryfield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebCustomCssManager;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebDomObjectsManager;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebQueryFieldDomValue;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortletUtils;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.AmiWebStyledPortletPeer;
import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.base.Caster;
import com.f1.suite.web.portal.impl.form.FormPortletAbsctractCalendarField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeSubRangeField;
import com.f1.suite.web.portal.impl.form.WebAbsoluteLocation;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.WebRectangle;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.ToDerivedString;

abstract public class QueryField<T extends FormPortletField<?>> implements ToDerivedString, AmiWebDomObject, AmiWebStyledPortlet {
	public static final String EVENT_ONCHANGE = "onChange";
	public static final String EVENT_ONENTERKEY = "onEnterKey";
	public static final String EVENT_ONAUTOCOMPLETED = "onAutocompleted";
	public static final String EVENT_ONFOCUS = "onFocus";
	public static final ParamsDefinition CALLBACK_DEF_ONCHANGE = new ParamsDefinition(EVENT_ONCHANGE, Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONENTERKEY = new ParamsDefinition(EVENT_ONENTERKEY, Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONAUTOCOMPLETED = new ParamsDefinition(EVENT_ONAUTOCOMPLETED, Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONFOCUS = new ParamsDefinition(EVENT_ONFOCUS, Object.class, "");
	static {
		CALLBACK_DEF_ONCHANGE.addDesc("Called when the value in the field changes, or in the case of action fields such as buttons are clicked");
		CALLBACK_DEF_ONENTERKEY.addDesc("Called when the field has focus on the enter key is pressed");
		CALLBACK_DEF_ONAUTOCOMPLETED.addDesc(
				"Only available on text field. Called when an autocomplete value is selected using left click or enter key. Note that onChange will also fire if the field value was changed as a result.");
		CALLBACK_DEF_ONFOCUS.addDesc("Called when the field gains focus (ex, user tabs-to or mouse-clicks on the field)");
	}
	public static final String FORMULA_DEFAULT_VALUE = "default_value";
	private String name;
	final private AmiWebFormula defaultValueFormula;
	final private AmiWebFormulasImpl formulas;
	private AmiWebOverrideValue<String> help = new AmiWebOverrideValue<String>(null);
	private AmiWebOverrideValue<String> label = new AmiWebOverrideValue<String>(null);

	final private T field;

	//Static Configuration Properties
	private static final String CONFIG_PROPERTY_ZINDEX = "zidx";
	private static final String LEGACY_CONFIG_PROPERTY_HIDDEN = "hidden";
	private static final String CONFIG_PROPERTY_HIDE = "hide";
	//	private static final String CONFIG_PROPERTY_STYLE = "style";
	private static final String CONFIG_PROPERTY_CSSCLASS = "cssClass";
	private static final String CONFIG_PROPERTY_CSSCLASSHELP = "cssClassHelp";
	private static final String CONFIG_PROPERTY_DISABLED = "disabled";
	private static final String CONFIG_PROPERTY_LABEL_HIDDEN = "labelHidden";
	private static final String CONFIG_PROPERTY_LABEL_SIDE_ALIGNMENT = "labelSideAlignment";
	private static final String CONFIG_PROPERTY_LABEL_SIDE = "labelSide";
	private static final String CONFIG_PROPERTY_LABEL_PADDING = "labelPadding";
	private static final String CONFIG_PROPERTY_LABEL_FONT_SIZE = "labelFontSize";
	private static final String CONFIG_PROPERTY_LABEL_BOLD_SETTING = "labelBoldSetting";
	private static final String CONFIG_PROPERTY_LABEL_ITALIC_SETTING = "labelItalicSetting";
	private static final String CONFIG_PROPERTY_LABEL_UNDERLINE_SETTING = "labelUnderlineSetting";
	private static final String CONFIG_PROPERTY_LABEL_COLOR = "labelColor";
	private static final String CONFIG_PROPERTY_LABEL_FONT_FAMILY = "labelFontFamily";
	private static final String CONFIG_PROPERTY_FIELD_FONT_FAMILY = "fieldFontFamily";
	private static final String CONFIG_PROPERTY_FIELD_FONT_SIZE = "fieldFontSize";
	private static final String CONFIG_PROPERTY_BG_COLOR = "bgColor";
	private static final String CONFIG_PROPERTY_FONT_COLOR = "fontColor";
	private static final String CONFIG_PROPERTY_BORDER_COLOR = "borderColor";
	private static final String CONFIG_PROPERTY_BORDER_WIDTH = "borderWidth";
	private static final String CONFIG_PROPERTY_BORDER_RADIUS = "borderRadius";
	private static final String CONFIG_PROPERTY_DMEXPRESSION = "dme";
	private static final String CONFIG_PROPERTY_ONEVENTSSCRIPTS = "onevents";
	private static final String CONFIG_PROPERTY_CALLBACKS = "callbacks";
	private static final String CONFIG_PROPERTY_LABELTITLE = "l";
	private static final String CONFIG_PROPERTY_TYPE = "t";
	private static final String CONFIG_PROPERTY_HELP = "help";
	private static final String CONFIG_PROPERTY_NAME = "n";

	private static final Map<String, Short> BACKWARDS_COMPATIBILITY_STYLES = new HashMap<String, Short>();
	static {
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_CSSCLASS, AmiWebStyleConsts.CODE_FLD_CSS);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_CSSCLASSHELP, AmiWebStyleConsts.CODE_FLD_CSS_HELP);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_HIDDEN, AmiWebStyleConsts.CODE_FLD_LBL_STATUS);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_SIDE_ALIGNMENT, AmiWebStyleConsts.CODE_FLD_LBL_ALIGN);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_SIDE, AmiWebStyleConsts.CODE_FLD_LBL_SIDE);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_PADDING, AmiWebStyleConsts.CODE_LBL_PD);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_FONT_SIZE, AmiWebStyleConsts.CODE_FONT_SZ);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_BOLD_SETTING, AmiWebStyleConsts.CODE_BOLD);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_ITALIC_SETTING, AmiWebStyleConsts.CODE_ITALIC);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_UNDERLINE_SETTING, AmiWebStyleConsts.CODE_UNDERLINE);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_COLOR, AmiWebStyleConsts.CODE_FONT_CL);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_LABEL_FONT_FAMILY, AmiWebStyleConsts.CODE_FONT_FAM);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_FIELD_FONT_FAMILY, AmiWebStyleConsts.CODE_FLD_FONT_FAM);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_FIELD_FONT_SIZE, AmiWebStyleConsts.CODE_FLD_FONT_SZ);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_BG_COLOR, AmiWebStyleConsts.CODE_FLD_BG_CL);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_FONT_COLOR, AmiWebStyleConsts.CODE_FLD_FONT_CL);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_BORDER_COLOR, AmiWebStyleConsts.CODE_FLD_BDR_CL);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_BORDER_RADIUS, AmiWebStyleConsts.CODE_FLD_BDR_RAD);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, CONFIG_PROPERTY_BORDER_WIDTH, AmiWebStyleConsts.CODE_FLD_BDR_WD);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, "innerTrack", AmiWebStyleConsts.CODE_FLD_GRIP_CL);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, "outerTrack", AmiWebStyleConsts.CODE_FLD_TRACK_CL);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, "track", AmiWebStyleConsts.CODE_FLD_TRACK_CL);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, "gripColor", AmiWebStyleConsts.CODE_FLD_GRIP_CL);
		CH.putOrThrow(BACKWARDS_COMPATIBILITY_STYLES, "headerColor", AmiWebStyleConsts.CODE_CAL_WK_FD_CL);
	}

	//Static Type Ids 
	public static final String TYPE_ID_CHECKBOX = "checkbox";
	public static final String TYPE_ID_COLOR_PICKER = "colorpicker";
	public static final String TYPE_ID_COLOR_GRADIENT_PICKER = "colorgradientpicker";
	public static final String TYPE_ID_DATE = "date";
	public static final String TYPE_ID_DATERANGE = "daterange";
	public static final String TYPE_ID_DATETIME = "datetime";
	public static final String TYPE_ID_DATETIMERANGE = "datetimerange";
	public static final String TYPE_ID_TIME = "time";
	public static final String TYPE_ID_TIMERANGE = "timerange";
	public static final String TYPE_ID_DIV = "divField";
	public static final String TYPE_ID_FILE_UPLOAD = "upload";
	public static final String TYPE_ID_BUTTON = "buttonField";
	public static final String TYPE_ID_RANGE = "numericRange";
	public static final String TYPE_ID_SUBRANGE = "numSubRange";
	public static final String TYPE_ID_SELECT = "selectField";
	public static final String TYPE_ID_MULTI_SELECT = "multiSelectField";
	public static final String TYPE_ID_MULTI_CHECKBOX = "multiCheckboxField";
	public static final String TYPE_ID_TEXT_AREA = "textArea";
	public static final String TYPE_ID_TEXT = "textField";
	public static final String TYPE_ID_IMAGE = "image";
	public static final String TYPE_ID_RADIO = "radio";
	public static final String TYPE_ID_PASSWORD = "password";

	//Static User Labels
	public static final String USER_LABEL_CHECKBOX = "Checkbox";
	public static final String USER_LABEL_COLOR_PICKER = "Color Picker";
	public static final String USER_LABEL_COLOR_GRADIENT_PICKER = "Gradient Picker";
	public static final String USER_LABEL_DATE = "Date";
	public static final String USER_LABEL_DATERANGE = "Daterange";
	public static final String USER_LABEL_DATETIME = "Datetime";
	public static final String USER_LABEL_DATETIMERANGE = "Datetimerange";
	public static final String USER_LABEL_TIME = "Time";
	public static final String USER_LABEL_TIMERANGE = "Timerange";
	public static final String USER_LABEL_DIV = "Div";
	public static final String USER_LABEL_UPLOAD = "Upload";
	public static final String USER_LABEL_BUTTON = "Button";
	public static final String USER_LABEL_RANGE = "Range";
	public static final String USER_LABEL_SLIDER = "Slider";
	public static final String USER_LABEL_SELECT = "Select";
	public static final String USER_LABEL_MULTI_SELECT = "Multi Select";
	public static final String USER_LABEL_MULTI_CHECKBOX = "Multi Checkbox";
	public static final String USER_LABEL_TEXT_AREA = "Text Area";
	public static final String USER_LABEL_TEXT = "Text";
	public static final String USER_LABEL_IMAGE = "Image";
	public static final String USER_LABEL_RADIO = "Radio Button";
	public static final String USER_LABEL_PASSWORD = "Password";

	// Field default absolute positioning
	private int leftPosPx = WebAbsoluteLocation.PX_NA;
	private int rightPosPx = WebAbsoluteLocation.PX_NA;
	private int widthPx = WebAbsoluteLocation.PX_NA;
	private int topPosPx = WebAbsoluteLocation.PX_NA;
	private int bottomPosPx = WebAbsoluteLocation.PX_NA;
	private int heightPx = WebAbsoluteLocation.PX_NA;
	private double leftPosPct = WebAbsoluteLocation.PCT_NA;
	private double rightPosPct = WebAbsoluteLocation.PCT_NA;
	private double widthPct = WebAbsoluteLocation.PCT_NA;
	private double topPosPct = WebAbsoluteLocation.PCT_NA;
	private double bottomPosPct = WebAbsoluteLocation.PCT_NA;
	private double heightPct = WebAbsoluteLocation.PCT_NA;
	private double horizontalOffsetFromCenterPct = WebAbsoluteLocation.PCT_NA;
	private double verticalOffsetFromCenterPct = WebAbsoluteLocation.PCT_NA;
	private boolean visible;
	private AmiWebOverrideValue<Boolean> disabled = new AmiWebOverrideValue<Boolean>(false);

	// Override positioning
	private Boolean overrideVisible = null;
	private int overrideLeftPosPx = WebAbsoluteLocation.PX_NA;
	private int overrideTopPosPx = WebAbsoluteLocation.PX_NA;
	private int overrideWidthPx = WebAbsoluteLocation.PX_NA;
	private int overrideHeightPx = WebAbsoluteLocation.PX_NA;
	private final Map<String, Integer> varPositions;
	private AmiWebAmiScriptCallbacks onEventsScript;

	private final AmiWebQueryFormPortlet form;
	private final AmiWebFormFieldFactory<?> factory;
	private boolean isTransient;
	static private Logger log = LH.get();

	public QueryField(AmiWebFormFieldFactory<?> factory, AmiWebQueryFormPortlet form, T field) {
		this.form = form;
		this.field = field;
		this.factory = factory;
		this.stylePeer = new AmiWebStyledPortletPeer(this, form.getService());
		this.formulas = new AmiWebFormulasImpl(this);
		this.defaultValueFormula = this.formulas.addFormula("defaultValue", Object.class);
		this.onEventsScript = new AmiWebAmiScriptCallbacks(form.getService(), this);
		this.onEventsScript.setAmiLayoutAlias(form.getAmiLayoutFullAlias());
		this.field.setCorrelationData(this);
		for (ParamsDefinition s : form.getService().getScriptManager().getCallbackDefinitions(factory.getClassType()))
			this.onEventsScript.registerCallbackDefinition(s);

		//Create map of suffix to position
		varPositions = new HashMap<String, Integer>();
		int vc = this.getVarsCount();
		for (int i = 0; i < vc; i++) {
			varPositions.put(this.getSuffixNameAt(i), i);
		}
		this.setTransient(false);
		this.initDomFieldValues();
		this.stylePeer.initStyle();
	}
	public void init(Map<String, Object> initArgs) {
		this.setVarName(CH.getOrThrow(Caster_String.INSTANCE, initArgs, CONFIG_PROPERTY_NAME));
		this.defaultValueFormula.setFormula(CH.getOr(Caster_String.INSTANCE, initArgs, CONFIG_PROPERTY_DMEXPRESSION, null), false);
		this.setHelp(CH.getOr(Caster_String.INSTANCE, initArgs, CONFIG_PROPERTY_HELP, null));
		this.setLabel(CH.getOr(Caster_String.INSTANCE, initArgs, CONFIG_PROPERTY_LABELTITLE, null));
		Map<String, Object> style = CH.getOrNoThrow(Map.class, initArgs, "style", null);
		if (style != null) {
			this.stylePeer.initStyle(style);
		} else {
			for (Entry<String, Short> entry : BACKWARDS_COMPATIBILITY_STYLES.entrySet()) {
				Object value = initArgs.get(entry.getKey());
				if (value != null) {
					if (entry.getKey() == CONFIG_PROPERTY_LABEL_HIDDEN)
						value = Boolean.FALSE.equals(value);
					else if (entry.getKey() == CONFIG_PROPERTY_LABEL_BOLD_SETTING)
						value = Boolean.TRUE.equals(value);
					AmiWebStyleOption optionByKey = this.stylePeer.getStyleType().getOptionByKey(entry.getValue());
					if (optionByKey == null) {
						LH.warning(log, "Missing key: ", entry.getValue());
					} else {
						Caster<?> caster = optionByKey.getCaster();
						value = caster.cast(value, false, false);
						this.stylePeer.putValue(entry.getValue(), value);
					}
				}
			}
		}
		this.setDisabled(CH.getOr(Caster_Boolean.INSTANCE, initArgs, CONFIG_PROPERTY_DISABLED, false));
		if (initArgs.containsKey(LEGACY_CONFIG_PROPERTY_HIDDEN))
			this.setVisible(CH.getOr(Caster_Boolean.PRIMITIVE, initArgs, LEGACY_CONFIG_PROPERTY_HIDDEN, false));
		else
			this.setVisible(!CH.getOr(Caster_Boolean.PRIMITIVE, initArgs, CONFIG_PROPERTY_HIDE, false));
		this.field.setZIndex(CH.getOr(Caster_Integer.PRIMITIVE, initArgs, CONFIG_PROPERTY_ZINDEX, 0));
		this.setWidthPx(CH.getOr(Caster_Integer.INSTANCE, initArgs, "widthPx", WebAbsoluteLocation.PX_NA));
		this.setLeftPosPx(CH.getOr(Caster_Integer.INSTANCE, initArgs, "leftPosPx", WebAbsoluteLocation.PX_NA));
		this.setRightPosPx(CH.getOr(Caster_Integer.INSTANCE, initArgs, "rightPosPx", WebAbsoluteLocation.PX_NA));
		this.setWidthPct(CH.getOr(Caster_Double.INSTANCE, initArgs, "widthPct", WebAbsoluteLocation.PCT_NA));
		this.setLeftPosPct(CH.getOr(Caster_Double.INSTANCE, initArgs, "leftPosPct", WebAbsoluteLocation.PCT_NA));
		this.setRightPosPct(CH.getOr(Caster_Double.INSTANCE, initArgs, "rightPosPct", WebAbsoluteLocation.PCT_NA));
		this.setHorizontalOffsetFromCenterPct(CH.getOr(Caster_Double.INSTANCE, initArgs, "hCenterOffsetPct", WebAbsoluteLocation.PCT_NA));
		this.setHeightPx(CH.getOr(Caster_Integer.INSTANCE, initArgs, "heightPx", WebAbsoluteLocation.PX_NA));
		this.setTopPosPx(CH.getOr(Caster_Integer.INSTANCE, initArgs, "topPosPx", WebAbsoluteLocation.PX_NA));
		this.setBottomPosPx(CH.getOr(Caster_Integer.INSTANCE, initArgs, "bottomPosPx", WebAbsoluteLocation.PX_NA));
		this.setHeightPct(CH.getOr(Caster_Double.INSTANCE, initArgs, "heightPct", WebAbsoluteLocation.PCT_NA));
		this.setTopPosPct(CH.getOr(Caster_Double.INSTANCE, initArgs, "topPosPct", WebAbsoluteLocation.PCT_NA));
		this.setBottomPosPct(CH.getOr(Caster_Double.INSTANCE, initArgs, "bottomPosPct", WebAbsoluteLocation.PCT_NA));
		this.setVerticalOffsetFromCenterPct(CH.getOr(Caster_Double.INSTANCE, initArgs, "vCenterOffsetPct", WebAbsoluteLocation.PCT_NA));
		Map<String, String> onEventScripts = CH.getOrNoThrow(Map.class, initArgs, CONFIG_PROPERTY_ONEVENTSSCRIPTS, null);
		StringBuilder sb = new StringBuilder();
		if (onEventScripts != null) {
			for (Entry<String, String> e : onEventScripts.entrySet())
				this.onEventsScript.setAmiScriptCallbackNoCompile(e.getKey(), e.getValue());
		} else {
			this.onEventsScript.init(null, this.form.getAmiLayoutFullAlias(), (Map) initArgs.get(CONFIG_PROPERTY_CALLBACKS), sb);
		}

	}
	public void onInitDone() {
		this.onEventsScript.initCallbacksLinkedVariables();
	}
	public Map<String, Object> getJson(Map<String, Object> sink) {
		CH.putNoNull(sink, CONFIG_PROPERTY_TYPE, getFactory().getEditorTypeId());
		CH.putNoNull(sink, CONFIG_PROPERTY_NAME, this.getName());
		AmiWebUtils.putSkipEmpty(sink, CONFIG_PROPERTY_HELP, this.getHelp(false));
		AmiWebUtils.putSkipEmpty(sink, CONFIG_PROPERTY_CALLBACKS, this.onEventsScript.getConfiguration());
		AmiWebUtils.putSkipEmpty(sink, CONFIG_PROPERTY_DMEXPRESSION, this.defaultValueFormula.getFormulaConfig());
		CH.putNoNull(sink, CONFIG_PROPERTY_LABELTITLE, getLabel());
		CH.putExcept(sink, CONFIG_PROPERTY_DISABLED, this.disabled.getValue(), Boolean.FALSE);
		Map<String, Object> style = this.stylePeer.getStyleConfiguration();
		sink.put("style", style);
		CH.putExcept(sink, CONFIG_PROPERTY_HIDE, !field.isVisible(), Boolean.FALSE);
		CH.putNoNull(sink, CONFIG_PROPERTY_ZINDEX, field.getZIndex());
		return sink;
	}

	final public void setVarName(String newVarName) {
		if (OH.eq(this.name, newVarName))
			return;
		if (this.field.getForm() != null)
			this.form.onVarNameChanged(this, newVarName);
		this.name = newVarName;
		updateAri();

	}
	public void setHelp(String help) {
		this.setHelp(help, false);
	}
	public void setHelp(String help, boolean override) {
		this.help.setValue(help, override);
		this.field.setHelp(getService().cleanHtml(help));
	}

	public String getName() {
		return name;
	}
	public T getField() {
		return field;
	}

	public Class<?> getVarTypeAt(int i) {
		if (i == 0)
			return field.getType();
		throw new IndexOutOfBoundsException(SH.toString(i));
	}
	public int getVarsCount() {
		return 1;
	}
	public String getVarNameAt(int i) {
		if (i == 0)
			return name;
		else
			return getName() + this.getSuffixNameAt(i);
	}

	private static final String NO_SUFFIX = "";

	public String getSuffixNameAt(int i) {
		if (i == 0)
			return NO_SUFFIX;
		throw new IndexOutOfBoundsException(SH.toString(i));
	}
	public String getHelp() {
		return this.getHelp(true);
	}
	public String getHelp(boolean override) {
		return this.help.getValue(override);
	}
	public Object getValue(int i) {
		if (i == 0)
			return field.getValue();
		throw new IndexOutOfBoundsException(SH.toString(i));
	}
	public Object getValue() {
		return getValue(0);
	}
	public Class getValueType() {
		return getVarTypeAt(0);
	}
	public boolean setValue(Object value) {
		FormPortletField f = getField();
		if (value == null) {
			f.setValueNoThrow(null);
		} else {
			Object val = f.getCaster().cast(value, false, false);
			if (val == null && !"null".equals(value))
				return false;
			f.setValueNoThrow(val);
		}
		return true;
	}
	public AmiWebAmiScriptCallbacks getOnEventScripts() {
		return onEventsScript;
	}

	public boolean setValue(String key, Object value) {
		if (key != null && OH.ne(getVarNameAt(0), key))
			throw new RuntimeException("bad key: " + key);
		return setValue(value);
	}

	public int getVarPosition(String fieldName) {
		if (this.name == null && !SH.startsWith(fieldName, this.name)) {
			return -1;
		} else
			return this.getPositionFromSuffixName(SH.afterFirst(fieldName, this.name));
	}

	public int getPositionFromSuffixName(String suffix) {
		if (!this.varPositions.containsKey(suffix))
			return -1;
		else
			return this.varPositions.get(suffix);
	}
	public AmiWebOverrideValue<Boolean> getDisabledOverrideValue() {
		return disabled;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
		this.overrideVisible = null;
		this.field.setVisible(visible);
	}
	public boolean getDisabled() {
		if (disabled.getValue() == null)
			return false;
		return disabled.getValue();
	}
	public void setDisabled(boolean isDisabled) {
		disabled.set(isDisabled, true);
		this.field.setDisabled(isDisabled);
	}
	public Boolean getOverrideDisabled() {
		return disabled.getOverride();
	}
	public void setOverrideDisabled(Boolean isDisabled) {
		disabled.setOverride(isDisabled);
		this.field.setDisabled(disabled.getOverride());
	}
	public int getLeftPosPx() {
		return leftPosPx;
	}
	public void setLeftPosPx(int leftPosPx) {
		this.leftPosPx = leftPosPx;
		this.overrideLeftPosPx = this.overrideWidthPx = WebAbsoluteLocation.PX_NA;
		this.field.setLeftPosPx(leftPosPx);
	}

	public int getRightPosPx() {
		return rightPosPx;
	}
	public void setRightPosPx(int rightPosPx) {
		this.rightPosPx = rightPosPx;
		this.overrideLeftPosPx = this.overrideWidthPx = WebAbsoluteLocation.PX_NA;
		this.field.setRightPosPx(rightPosPx);
	}

	public int getWidthPx() {
		return widthPx;
	}
	public void setWidthPx(int widthPx) {
		this.widthPx = widthPx;
		this.overrideLeftPosPx = this.overrideWidthPx = WebAbsoluteLocation.PX_NA;
		this.field.setWidthPx(widthPx);
	}

	public int getTopPosPx() {
		return topPosPx;
	}
	public void setTopPosPx(int topPosPx) {
		this.topPosPx = topPosPx;
		this.overrideTopPosPx = this.overrideHeightPx = WebAbsoluteLocation.PX_NA;
		this.field.setTopPosPx(topPosPx);
	}

	public int getBottomPosPx() {
		return bottomPosPx;
	}
	public void setBottomPosPx(int bottomPosPx) {
		this.bottomPosPx = bottomPosPx;
		this.overrideTopPosPx = this.overrideHeightPx = WebAbsoluteLocation.PX_NA;
		this.field.setBottomPosPx(bottomPosPx);
	}

	public int getHeightPx() {
		return heightPx;
	}
	public void setHeightPx(int heightPx) {
		this.heightPx = heightPx;
		this.overrideTopPosPx = this.overrideHeightPx = WebAbsoluteLocation.PX_NA;
		this.field.setHeightPx(heightPx);
	}

	public double getLeftPosPct() {
		return leftPosPct;
	}
	public void setLeftPosPct(double leftPosPct) {
		this.leftPosPct = leftPosPct;
		this.overrideLeftPosPx = this.overrideWidthPx = WebAbsoluteLocation.PX_NA;
		this.field.setLeftPosPct(leftPosPct);
	}

	public double getRightPosPct() {
		return rightPosPct;
	}
	public void setRightPosPct(double rightPosPct) {
		this.rightPosPct = rightPosPct;
		this.overrideLeftPosPx = this.overrideWidthPx = WebAbsoluteLocation.PX_NA;
		this.field.setRightPosPct(rightPosPct);
	}

	public double getWidthPct() {
		return widthPct;
	}
	public void setWidthPct(double widthPct) {
		this.widthPct = widthPct;
		this.overrideLeftPosPx = this.overrideWidthPx = WebAbsoluteLocation.PX_NA;
		this.field.setWidthPct(widthPct);
	}

	public double getTopPosPct() {
		return topPosPct;
	}
	public void setTopPosPct(double topPosPct) {
		this.topPosPct = topPosPct;
		this.overrideTopPosPx = this.overrideHeightPx = WebAbsoluteLocation.PX_NA;
		this.field.setTopPosPct(topPosPct);
	}

	public double getBottomPosPct() {
		return bottomPosPct;
	}
	public void setBottomPosPct(Double bottomPosPct) {
		this.bottomPosPct = bottomPosPct;
		this.overrideTopPosPx = this.overrideHeightPx = WebAbsoluteLocation.PX_NA;
		this.field.setBottomPosPct(bottomPosPct);
	}

	public double getHeightPct() {
		return heightPct;
	}
	public void setHeightPct(Double heightPct) {
		this.heightPct = heightPct;
		this.overrideTopPosPx = this.overrideHeightPx = WebAbsoluteLocation.PX_NA;
		this.field.setHeightPct(heightPct);
	}

	public double getHorizontalOffsetFromCenterPct() {
		return horizontalOffsetFromCenterPct;
	}
	public void setHorizontalOffsetFromCenterPct(Double horizontalOffsetFromCenterPct) {
		this.horizontalOffsetFromCenterPct = horizontalOffsetFromCenterPct;
		this.overrideLeftPosPx = this.overrideWidthPx = WebAbsoluteLocation.PX_NA;
		this.field.setHorizontalOffsetFromCenterPct(horizontalOffsetFromCenterPct);
	}

	public double getVerticalOffsetFromCenterPct() {
		return verticalOffsetFromCenterPct;
	}
	public void setVerticalOffsetFromCenterPct(Double verticalOffsetFromCenterPct) {
		this.verticalOffsetFromCenterPct = verticalOffsetFromCenterPct;
		this.overrideTopPosPx = this.overrideHeightPx = WebAbsoluteLocation.PX_NA;
		this.field.setVerticalOffsetFromCenterPct(verticalOffsetFromCenterPct);
	}

	public int getOverrideLeftPosPx() {
		return overrideLeftPosPx;
	}
	public void setOverrideLeftPosPx(int overrideLeftPosPx) {
		this.overrideLeftPosPx = overrideLeftPosPx;
		getAbsLocationH().setPositionPxFromStartAndSize(overrideLeftPosPx, getRealizedWidthPx());
	}
	public void setOverrideVisible(boolean overrideVisible) {
		this.overrideVisible = overrideVisible;
		if (this.overrideVisible == null)
			this.getField().setVisible(this.visible);
		else
			this.getField().setVisible(this.overrideVisible);
	}
	public Boolean getOverrideVisible() {
		return overrideVisible;
	}

	public int getOverrideTopPosPx() {
		return overrideTopPosPx;
	}
	public void setOverrideTopPosPx(int overrideTopPosPx) {
		this.overrideTopPosPx = overrideTopPosPx;
		getAbsLocationV().setPositionPxFromStartAndSize(overrideTopPosPx, getRealizedHeightPx());
	}

	public int getOverrideWidthPx() {
		return overrideWidthPx;
	}
	public void setOverrideWidthPx(int overrideWidthPx) {
		this.overrideWidthPx = overrideWidthPx;
		getAbsLocationH().setPositionPxFromStartAndSize(getRealizedLeftPosPx(), overrideWidthPx);
	}

	public int getOverrideHeightPx() {
		return overrideHeightPx;
	}
	public void setOverrideHeightPx(int overrideHeightPx) {
		this.overrideHeightPx = overrideHeightPx;
		getAbsLocationV().setPositionPxFromStartAndSize(getRealizedTopPosPx(), overrideHeightPx);
	}
	public void resetOverridePosition() {
		this.overrideVisible = null;
		this.overrideLeftPosPx = WebAbsoluteLocation.PX_NA;
		this.overrideTopPosPx = WebAbsoluteLocation.PX_NA;
		this.overrideWidthPx = WebAbsoluteLocation.PX_NA;
		this.overrideHeightPx = WebAbsoluteLocation.PX_NA;

		// Reset horizontal position
		getAbsLocationH().clearAllPositioning();

		this.field.setWidthPx(this.widthPx);
		this.field.setLeftPosPx(this.leftPosPx);
		this.field.setRightPosPx(this.rightPosPx);
		this.field.setWidthPct(this.widthPct);
		this.field.setLeftPosPct(this.leftPosPct);
		this.field.setRightPosPct(this.rightPosPct);
		this.field.setHorizontalOffsetFromCenterPct(this.horizontalOffsetFromCenterPct);

		// Reset vertical position
		getAbsLocationV().clearAllPositioning();

		this.field.setHeightPx(this.heightPx);
		this.field.setTopPosPx(this.topPosPx);
		this.field.setBottomPosPx(this.bottomPosPx);
		this.field.setHeightPct(this.heightPct);
		this.field.setTopPosPct(this.topPosPct);
		this.field.setBottomPosPct(this.bottomPosPct);
		this.field.setVerticalOffsetFromCenterPct(this.verticalOffsetFromCenterPct);
	}

	public WebRectangle getPositionAsRect() {
		return new WebRectangle(getRealizedLeftPosPx(), getRealizedTopPosPx(), getRealizedWidthPx(), getRealizedHeightPx());
	}

	public boolean isAtDefaultPosition() {
		return overrideVisible == null && !WebAbsoluteLocation.is(this.overrideLeftPosPx) && !WebAbsoluteLocation.is(this.overrideTopPosPx)
				&& !WebAbsoluteLocation.is(this.overrideWidthPx) && !WebAbsoluteLocation.is(this.overrideHeightPx);
	}
	public void ensureHorizontalPosDefined() {
		ensureHorizontalPosDefined(this.field.getDefaultWidth());
	}
	public void ensureHorizontalPosDefined(int width) {
		if (isHorizontalPosDefined()) {
			return;
		}
		this.widthPx = width;
		this.leftPosPx = FormPortletField.DEFAULT_LEFT_POS_PX;
		getAbsLocationH().clearAllPositioning();
		setWidthPx(width);
		setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
	}
	public void ensureVerticalPosDefined(int top) {
		ensureVerticalPosDefined(top, this.field.getDefaultHeight());
	}
	public void ensureVerticalPosDefined(int top, int height) {
		if (isVerticalPosDefined()) {
			return;
		}
		this.heightPx = height;
		this.topPosPx = top;
		getAbsLocationV().clearAllPositioning();
		setHeightPx(height);
		setTopPosPx(top);
	}

	public boolean isHorizontalPosDefined() {
		return getAbsLocationH().isDefined();
	}
	public boolean isVerticalPosDefined() {
		return getAbsLocationV().isDefined();
	}

	public QueryField<?> setRealizedHorizontalPosPx(int left, int width, byte alignment) {
		getAbsLocationH().setPositionPxFromStartAndSize(left, width, alignment);
		AmiWebQueryFormPortletUtils.copyPositioningToQueryField(this.field, this);
		return this;
	}
	public QueryField<?> setRealizedVerticalPosPx(int top, int height, byte alignment) {
		getAbsLocationV().setPositionPxFromStartAndSize(top, height, alignment);
		AmiWebQueryFormPortletUtils.copyPositioningToQueryField(this.field, this);
		return this;
	}
	public QueryField<?> setRealizedLeftPosPx(int left) {
		getAbsLocationH().setPositionPxFromStartAndSize(left, getRealizedWidthPx());
		AmiWebQueryFormPortletUtils.copyPositioningToQueryField(this.field, this);
		return this;
	}
	public QueryField<?> setRealizedWidthPx(int width) {
		getAbsLocationH().setPositionPxFromStartAndSize(getRealizedLeftPosPx(), width);
		AmiWebQueryFormPortletUtils.copyPositioningToQueryField(this.field, this);
		return this;
	}
	public QueryField<?> setRealizedTopPosPx(int top) {
		getAbsLocationV().setPositionPxFromStartAndSize(top, getRealizedHeightPx());
		AmiWebQueryFormPortletUtils.copyPositioningToQueryField(this.field, this);
		return this;
	}
	public QueryField<?> setRealizedHeightPx(int height) {
		getAbsLocationV().setPositionPxFromStartAndSize(getRealizedTopPosPx(), height);
		AmiWebQueryFormPortletUtils.copyPositioningToQueryField(this.field, this);
		return this;
	}
	public int getRealizedLeftPosPx() {
		return getAbsLocationH().getStartPxFromAlignment();
	}
	public int getRealizedTopPosPx() {
		return getAbsLocationV().getStartPxFromAlignment();
	}
	public int getRealizedWidthPx() {
		return getAbsLocationH().getSizePxFromAlignment();
	}
	public int getRealizedHeightPx() {
		return getAbsLocationV().getSizePxFromAlignment();
	}

	public byte getHorizontalPosAlignment() {
		return getAbsLocationH().getAlignment();
	}
	public byte getVerticalPosAlignment() {
		return getAbsLocationV().getAlignment();
	}
	public void convertHorizontalAlignment(byte newAlignment) {
		getAbsLocationH().convertAlignment(newAlignment);
	}
	public void convertVerticalAlignment(byte newAlignment) {
		getAbsLocationV().convertAlignment(newAlignment);
	}
	public boolean isInsideFieldPx(int x, int y) { // x and y are positions within form, not with respect to desktop origin
		return this.field.isInsideFieldPx(x, y);
	}

	public WebAbsoluteLocation getAbsLocationH() {
		return this.field.getAbsLocationH();
	}
	public WebAbsoluteLocation getAbsLocationV() {
		return this.field.getAbsLocationV();
	}
	final public void updateFormSize() {
		this.field.updateFormSize();
	}

	public Integer getFieldHorizontalCenterPosPx() {
		return getAbsLocationH().getCenterPosPx();
	}
	public Integer getFieldVerticalCenterPosPx() {
		return getAbsLocationV().getCenterPosPx();
	}
	public String getId() {
		return this.field.getId();
	}

	public void getDependencies(Set<String> r) {
		//TODO:
	}

	public void onRemoving() {
		this.onEventsScript.close();
	}
	public String getLabel() {
		return this.getLabel(true);
	}
	public String getLabel(boolean override) {
		return this.label.getValue(override);
	}

	public AmiWebFormFieldFactory<?> getFactory() {
		return this.factory;
	}
	@Override
	public String toString() {
		return toDerivedString();
	}
	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}
	public AmiWebQueryFormPortlet getForm() {
		return this.form;
	}
	public void setLabel(String label) {
		this.setLabel(label, false);
	}
	public void setLabel(String label, boolean override) {
		this.label.setValue(label, override);
		this.field.setTitle(WebHelper.escapeHtml(label));
		//this.field.setHelp(getService().cleanHtml(help));
	}
	//	public void initStyle() {
	//		this.queryFieldStyle.initStyle();
	//	}
	//	public void setCssClassOverride(String cssClass) {
	//		this.queryFieldStyle.setCssClassOverride(cssClass);
	//	}
	//	public String getCssClassOverride() {
	//		return this.queryFieldStyle.getCssClassOverride();
	//	}
	public void setOnEventScripts(AmiWebAmiScriptCallbacks onEventScripts) {
		if (this.onEventsScript == onEventScripts)
			return;
		this.onEventsScript.copyFrom(onEventScripts);
	}

	private Map<String, AmiWebQueryFieldDomValue> domFieldValuesBySuffix = new LinkedHashMap<String, AmiWebQueryFieldDomValue>();

	public Object getValue(String key) {
		return this.getValue(this.getPositionFromSuffixName(key));
	}

	private void initDomFieldValues() {
		this.domFieldValuesBySuffix.clear();
		int cnt = this.getVarsCount();
		for (int i = 0; i < cnt; i++) {
			String varName = this.getSuffixNameAt(i);
			AmiWebQueryFieldDomValue domValue = new AmiWebQueryFieldDomValue(this, varName);
			this.domFieldValuesBySuffix.put(varName, domValue);
		}
	}

	private String ari;
	private String amiLayoutFullAlias;
	private String amiLayoutFullAliasDotId;

	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.getForm().getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.getForm().getAmiLayoutFullAliasDotId() + "?" + getDomLabel();
		this.ari = AmiWebDomObject.ARI_TYPE_FIELD + ":" + amiLayoutFullAliasDotId;
		this.getAmiScriptCallbacks().setAmiLayoutAlias(this.amiLayoutFullAlias);
		if (this.field.getForm() != null) {
			this.form.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
			this.form.fireFieldAriChanged(this, oldAri);
		}
		for (AmiWebQueryFieldDomValue i : this.domFieldValuesBySuffix.values())
			i.updateAri();
		this.field.setHtmlIdSelector(AmiWebUtils.toHtmlIdSelector(this));
	}
	public AmiWebQueryFieldDomValue getQueryFieldDomValueBySuffix(String id) {
		return this.domFieldValuesBySuffix.get(id);
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return this.amiLayoutFullAlias;
	}
	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.amiLayoutFullAliasDotId;
	}
	@Override
	public String getAri() {
		return ari;
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_FIELD;
	}

	@Override
	public String getDomLabel() {
		return this.getName();
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> r = new ArrayList<AmiWebDomObject>();
		CH.addAll(r, this.domFieldValuesBySuffix.values());
		return r;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.getForm();
	}

	@Override
	public Class<?> getDomClassType() {
		return this.getClass();
	}

	@Override
	public Object getDomValue() {
		return this;
	}
	public Set<String> getSuffixes() {//TODO: should not need to build each time
		Set<String> r = new HashSet<String>(this.getVarsCount());
		for (int i = 0; i < this.getVarsCount(); i++)
			r.add(this.getSuffixNameAt(i));
		return r;
	}
	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return this.onEventsScript;
	}
	@Override
	public boolean isTransient() {
		return isTransient;
	}
	@Override
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	private boolean isManagedByDomManager = false;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.form.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;

			for (AmiWebQueryFieldDomValue i : this.domFieldValuesBySuffix.values()) {
				i.addToDomManager();
			}
		}
	}

	@Override
	public void removeFromDomManager() {
		AmiWebDomObjectsManager domObjectsManager = this.form.getService().getDomObjectsManager();
		for (AmiWebDomObject i : this.getChildDomObjects())
			domObjectsManager.fireRemoved(i);
		domObjectsManager.fireRemoved(this);

		if (this.isManagedByDomManager == true) {
			//Remove DomValues First
			for (AmiWebQueryFieldDomValue i : this.domFieldValuesBySuffix.values()) {
				i.removeFromDomManager();
			}

			//Remove Self
			AmiWebService service = this.form.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}
	public Object getDefaultValue() {
		return this.getField().getDefaultValue();
	}
	public void onFieldValueChanged(FormPortletField<?> field2, boolean fire) {
	}
	@Override
	public AmiWebFormulas getFormulas() {
		return this.formulas;
	}
	@Override
	public AmiWebService getService() {
		return this.form.getService();
	}
	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.form.getFormulaVarTypes(f);
	}
	public AmiWebFormula getDefaultValueFormula() {
		return this.defaultValueFormula;
	}

	protected String logMe() {
		return getService().getUserName() + "->" + this.getAri();
	}

	protected Set<String> usedConstVars = new HashSet<String>();

	public Set<String> getUsedConstVars() {
		return this.usedConstVars;
	}

	final private AmiWebStyledPortletPeer stylePeer;
	private String fieldCssClassname = null;
	private String labelCssClassname = null;
	private String helpCssClassname = null;

	public void setStyle(short code, Object value) {
		if (value == null) {
			Object dflt = this.form.getStylePeer().resolveValue(code);
			setStyle(this.field, code, dflt);
		} else
			setStyle(this.field, code, value);
	}
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		setStyle(this.field, key, nuw);
	}

	@Override
	public AmiWebStyledPortletPeer getStylePeer() {
		return this.stylePeer;
	}

	@Override
	public String getStyleType() {
		return factory.getStyleType().getName();
	}

	@Override
	public void onParentStyleChanged(AmiWebStyledPortletPeer amiWebStyledPortletPeer) {
	}

	public void setStyle(FormPortletField<?> target, short code, Object value) {
		switch (code) {
			case AmiWebStyleConsts.CODE_FLD_BG_CL:
				target.setBgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_FONT_CL:
				target.setFontColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_BDR_CL:
				target.setBorderColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_FCS_BDR_CL:
				target.setFocusedBorderColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_BDR_WD:
				target.setBorderWidth(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_FCS_BDR_WD:
				target.setFocusedBorderWidth(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_BDR_RAD:
				target.setBorderRadius(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_FONT_SZ:
				target.setFieldFontSize(Caster_Integer.PRIMITIVE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_FONT_FAM:
				target.setFieldFontFamily(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FONT_CL:
				target.setLabelColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FONT_SZ:
				target.setLabelFontSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_BOLD:
				target.setLabelBold(Caster_Boolean.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_ITALIC:
				target.setLabelItalic(Caster_Boolean.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_UNDERLINE:
				target.setLabelUnderline(Caster_Boolean.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_LBL_PD:
				target.setLabelPaddingPx(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_LBL_STATUS:
				target.setLabelHidden(Boolean.FALSE.equals(Caster_Boolean.INSTANCE.cast(value)));
				break;
			case AmiWebStyleConsts.CODE_FLD_LBL_SIDE:
				target.setLabelSide(Caster_Byte.PRIMITIVE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_LBL_ALIGN:
				target.setLabelSideAlignment(Caster_Byte.PRIMITIVE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FONT_FAM:
				target.setLabelFontFamily(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_CSS:
				if (!SH.is(value))
					target.setCssStyle(removeCssClassname(this.fieldCssClassname));
				else {
					target.setCssStyle(toCssClassname(value));
					this.fieldCssClassname = Caster_String.INSTANCE.cast(value);
				}
				break;
			case AmiWebStyleConsts.CODE_FLD_CSS_LBL:
				if (!SH.is(value))
					target.setLabelCssStyle(removeCssClassname(this.labelCssClassname));
				else {
					target.setLabelCssStyle(toCssClassname(value));
					this.labelCssClassname = Caster_String.INSTANCE.cast(value);
				}
				break;
			case AmiWebStyleConsts.CODE_CAL_WK_FD_CL:

				((FormPortletAbsctractCalendarField<?>) target).setHeaderColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLD_GRIP_CL:
				if (target instanceof FormPortletNumericRangeField) {
					String color = Caster_String.INSTANCE.cast(value);
					FormPortletNumericRangeField nrf = (FormPortletNumericRangeField) target;
					nrf.setScrollGripColor(color);
					nrf.setLeftScrollTrackColor(color);
				} else if (target instanceof FormPortletNumericRangeSubRangeField) {
					String color = Caster_String.INSTANCE.cast(value);
					FormPortletNumericRangeSubRangeField nrf = (FormPortletNumericRangeSubRangeField) target;
					nrf.setScrollGripColor(color);
					nrf.setInnerTrackColor(color);
				}
				break;
			case AmiWebStyleConsts.CODE_FLD_TRACK_CL:
				if (target instanceof FormPortletNumericRangeField) {
					String color = Caster_String.INSTANCE.cast(value);
					FormPortletNumericRangeField nrf = (FormPortletNumericRangeField) target;
					nrf.setScrollTrackColor(color);
				} else if (target instanceof FormPortletNumericRangeSubRangeField) {
					String color = Caster_String.INSTANCE.cast(value);
					FormPortletNumericRangeSubRangeField nrf = (FormPortletNumericRangeSubRangeField) target;
					nrf.setOuterTrackColor(color);
				}
				break;
			case AmiWebStyleConsts.CODE_FLD_CSS_HELP:
				if (!SH.is(value))
					target.setHelpCssStyle(removeCssClassname(this.helpCssClassname));
				else {
					target.setHelpCssStyle(toCssClassname(value));
					this.helpCssClassname = Caster_String.INSTANCE.cast(value);
				}
				break;
			case AmiWebStyleConsts.CODE_CAL_BG_CL:
				((FormPortletAbsctractCalendarField) target).setCalBgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_BTN_BG_CL:
				((FormPortletAbsctractCalendarField) target).setCalBtnBgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_YR_FG_CL:
				((FormPortletAbsctractCalendarField) target).setCalYrFgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_SEL_YR_FG_CL:
				((FormPortletAbsctractCalendarField) target).setCalSelYrFgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_MTN_FG_CL:
				((FormPortletAbsctractCalendarField) target).setCalMtnFgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_SEL_MTN_FG_CL:
				((FormPortletAbsctractCalendarField) target).setCalSelMtnFgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_SEL_MTN_BG_CL:
				((FormPortletAbsctractCalendarField) target).setCalSelMtnBgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_WK_FG_CL:
				((FormPortletAbsctractCalendarField) target).setCalWkFgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_WK_BG_CL:
				((FormPortletAbsctractCalendarField) target).setCalWkBgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_DAY_FG_CL:
				((FormPortletAbsctractCalendarField) target).setCalDayFgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_X_DAY_FG_CL:
				((FormPortletAbsctractCalendarField) target).setCalXDayFgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_BTN_FG_CL:
				((FormPortletAbsctractCalendarField) target).setCalBtnFgColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_CAL_HOV_BG_CL:
				((FormPortletAbsctractCalendarField) target).setCalHoverBgColor(Caster_String.INSTANCE.cast(value));
				break;
		}

	}
	private static String toCssClassname(Object value) {
		if (SH.startsWith((String) value, AmiWebCustomCssManager.PUBLIC))
			return "_cna=" + Caster_String.INSTANCE.cast(value);
		return SH.is(value) ? "_cna=" + AmiWebCustomCssManager.PREFIX + Caster_String.INSTANCE.cast(value) : null;
	}

	private static String removeCssClassname(Object value) {
		if (SH.startsWith((String) value, AmiWebCustomCssManager.PUBLIC))
			return "_cnr=" + Caster_String.INSTANCE.cast(value);
		return SH.is(value) ? "_cnr=" + AmiWebCustomCssManager.PREFIX + Caster_String.INSTANCE.cast(value) : null;
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}