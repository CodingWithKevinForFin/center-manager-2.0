package com.f1.suite.web.portal.impl.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Caster;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.impl.BasicPortletMetrics;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public abstract class FormPortletField<TYPE> implements WebAbsoluteLocationListener {

	private static final Logger log = LH.get();

	public static final int MASK_STYLE = 1;
	public static final int MASK_LAYOUT = 2;
	public static final int MASK_VALUE = 4;
	public static final int MASK_CONFIG = 8;
	public static final int MASK_HELP = 16;
	public static final int MASK_EXTENSION = 32;
	public static final int MASK_CUSTOM = 64;
	public static final int MASK_SELECTION = 128;
	public static final int MASK_CURSOR = 256;
	public static final int MASK_OPTIONS = 512;
	public static final int MASK_REBUILD = 1024;
	public static final int MASK_LABEL = 2048;
	public static final int MASK_POSITIONS = 4096;
	public static final int MASK_PENDINGJS_UPDATE = 8192;
	public static final int MASK_EXTENSION_PENDINGJS_UPDATE = 16384;

	public StringBuilder pendingUpdateJs = new StringBuilder();

	final private WebAbsoluteLocation locationH;
	final private WebAbsoluteLocation locationV;
	private byte widthMode = SIZE_DEFAULT;
	private byte heightMode = SIZE_DEFAULT;
	private String htmlIdSelector = null;//this would be used for testing

	public static final byte SIZE_STRETCH = -1;
	public static final byte SIZE_DEFAULT = -2;
	private static final byte SIZE_FIXED = -3;
	public final static int DEFAULT_WIDTH = 200;
	public final static int DEFAULT_HEIGHT = 18;
	public final static int DEFAULT_LEFT_POS_PX = 100;
	public final static int DEFAULT_PADDING_PX = 10;

	@Deprecated
	public static final byte WIDTH_STRETCH = SIZE_STRETCH;
	@Deprecated
	public static final byte HEIGHT_STRETCH = SIZE_STRETCH;
	@Deprecated
	public static final byte USE_DEFAULT = SIZE_DEFAULT;

	private String title;
	private String help;
	private String name;
	private String id;
	private int modificationNumber;
	private TYPE value;
	private FormPortlet form;
	private TYPE defaultValue = null;
	private boolean disabled = false;
	private boolean visible = true;
	private boolean labelHidden = false;
	private byte labelSide = LABEL_SIDE_LEFT;
	private byte labelSideAlignment = LABEL_SIDE_ALIGN_CENTER;
	private String labelColor;
	private Integer labelFontSize;
	private Boolean labelBold;
	private Boolean labelItalic;
	private Boolean labelUnderline;
	private String labelFontFamily;
	private String fieldFontFamily;
	private Integer fieldFontSize;
	private String bgColor;
	private String fontColor;
	private String borderColor = "#AAAAAA";
	private Integer borderWidth = 1;
	private String focusedBorderColor = "#4444FF";
	private Integer focusedBorderWidth = 1;
	private Integer borderRadius;
	private int zIndex = 0;
	private final Class<TYPE> type;
	private final Caster<TYPE> caster;
	private boolean titleIsClickable;
	private int labelWidthPx;
	private int labelHeightPx;
	private int calculatedLabelWidthPx;
	private int calculatedLabelHeightPx;
	private int calculatedLabelLeftPx;
	private int calculatedLabelTopPx;

	private int calculatedLeftPx;//this is the actual position on the screen
	private int calculatedTopPx;
	private int calculatedWidthPx;
	private int calculatedHeightPx;
	private Object correlationData;
	private String cssStyle = null;
	private String labelCssStyle = null;
	private String helpCssStyle = null;
	private List<FormPortletFieldExtension> extensions;
	private int labelPaddingPx;
	private int helpTextWidth;
	public final static String NO_COLOR = "";
	private boolean isFocused;
	private int changes;
	protected String style;
	protected String jsObjectName = "f";
	private boolean ignoreDefaultStyle;

	public FormPortletField(Class<TYPE> type, String title) {
		locationH = new WebAbsoluteLocation(this);
		locationV = new WebAbsoluteLocation(this);
		this.title = title;
		this.help = "";
		this.type = type;
		this.caster = OH.getCaster(type);
		this.modificationNumber = 0;
		this.labelWidthPx = DEFAULT_WIDTH;
		this.labelHeightPx = DEFAULT_HEIGHT;
	}

	public int addExtension(FormPortletFieldExtension extension) {
		if (extensions == null) {
			extensions = new ArrayList<FormPortletFieldExtension>();
		}
		int index = extensions.size();
		extensions.add(extension);
		flagConfigChanged();
		return index;
	}
	public FormPortletFieldExtension getExtension(int index) {
		return extensions.get(index);
	}

	public boolean isExportImportSupported() {
		return true;
	}

	public Class<TYPE> getType() {
		return type;
	}

	public Caster<TYPE> getCaster() {
		return caster;
	}

	public FormPortlet getForm() {
		return form;
	}

	public void setForm(FormPortlet form) {
		if (form == this.form)
			return;
		if (this.form != null && form != null)
			throw new IllegalStateException("already member of a form");
		this.form = form;
		this.isFocused = false;
	}

	public String getName() {
		return name == null ? this.getTitle() : name;
	}

	public FormPortletField<TYPE> setName(String name) {
		this.name = name;
		return this;
	}

	abstract public String getjsClassName();

	public FormPortletField<TYPE> setValue(TYPE value) {
		if (OH.eq(this.value, value))
			return this;
		this.value = value;
		this.incrementAndGetModificationNumber();
		flagChange(MASK_VALUE);
		return this;
	}
	public FormPortletField<TYPE> setValueNoFire(TYPE value) {
		this.value = value;
		return this;
	}

	public TYPE getValue() {
		return value;
	}

	public void rebuildJs(StringBuilder pendingJs) {
		if (extensions != null)
			for (int i = 0; i < extensions.size(); i++) {
				pendingJs.append("{");
				extensions.get(i).rebuildJs(pendingJs);
				pendingJs.append("}").append(SH.NEWLINE);
			}
		this.changes = 0xFFFFFFFF ^ (MASK_SELECTION | MASK_CURSOR | MASK_CUSTOM);
	}
	public void updateJs(StringBuilder pendingJs) {
		if (extensions != null) {
			for (int i = 0; i < extensions.size(); i++) {
				pendingJs.append("{");
				pendingJs.append("var e = ").append(jsObjectName).append(".getExtension(").append(i).append(");");
				extensions.get(i).updateJs(pendingJs);
				pendingJs.append("}").append(SH.NEWLINE);
			}
		}

		JsFunction jsFunction = new JsFunction(pendingJs);
		if (hasChanged(MASK_CONFIG)) {

			boolean isTitleClickable = this.titleIsClickable || SH.is(help);
			jsFunction.reset(jsObjectName, "initField").addParamQuoted(title).addParam(isTitleClickable).addParam(isDisabled()).addParam(!isVisible())
					.addParam(!isVisible() || isLabelHidden()).addParamQuoted(this.htmlIdSelector).addParam(getZIndex()).end();
		}
		if (hasChanged(MASK_HELP)) {
			if (!hasChanged(MASK_CONFIG)) {
				boolean isTitleClickable = this.titleIsClickable || SH.is(help);
				jsFunction.reset(jsObjectName, "setTitleClickable").addParam(isTitleClickable).end();
			}
			if (SH.is(help))
				jsFunction.reset(jsObjectName, "setHelpBox").addParamQuoted(help).addParam(this.helpTextWidth).addParamQuoted(this.helpCssStyle).end();
		}
		if (hasChanged(MASK_STYLE)) {
			jsFunction.reset(jsObjectName, "setFieldStyle").addParamQuoted(this.getStyle()).end();
			if (!isLabelHidden())
				jsFunction.reset(jsObjectName, "setLabelStyle").addParamQuoted(this.labelCssStyle).end();
		}
		if (hasChanged(MASK_POSITIONS)) {
			jsFunction.reset(jsObjectName, "setFieldPosition").addParam(this.calculatedLeftPx).addParam(this.calculatedTopPx).addParam(this.calculatedWidthPx)
					.addParam(this.calculatedHeightPx).addParam(getCalculatedLabelLeftPx()).addParam(getCalculatedLabelTopPx()).addParam(getCalculatedLabelWidthPx())
					.addParam(getCalculatedLabelHeightPx()).addParam(getLabelSideAlignment()).addParam(getLabelSide()).addParam(getLabelPaddingPx()).end();
		}
		if (hasChanged(MASK_LABEL)) {
			jsFunction.reset(jsObjectName, "setFieldStyleOptions").addParamQuoted(getLabelColor()).addParam(getLabelBold()).addParam(getLabelItalic()).addParam(getLabelUnderline())
					.addParamQuoted(getLabelFontFamily()).addParam(getLabelFontSize()).end();
		}
		if (hasChanged(MASK_VALUE | MASK_OPTIONS | MASK_CONFIG)) {
			jsFunction.reset(jsObjectName, "setFieldValue").addParamQuoted(getJsValue()).addParam(getModificationNumber()).end();
		}
	}

	public String getTitle() {
		return title;
	}
	public FormPortletField<?> setTitle(String title) {
		if (OH.eq(title, this.title))
			return this;
		this.title = title;
		flagConfigChanged();
		return this;
	}

	public String getId() {
		return id;
	}

	public FormPortletField<TYPE> setId(String id) {
		if (this.id != null)
			throw new IllegalStateException("id already assigned");
		this.id = id;
		return this;
	}

	protected void flagChange(int mask) {
		if (form != null) {
			changes |= mask;
			form.onFieldChanged(this);
		}
	}
	protected void flagFieldExtensionUpdate() {
		if (form != null) {
			changes |= MASK_EXTENSION_PENDINGJS_UPDATE;
			form.onFieldChanged(this);
		}

	}
	protected void flagConfigChanged() {
		flagChange(MASK_CONFIG);
	}
	protected void flagStyleChanged() {
		flagChange(MASK_STYLE);
	}
	protected void flagLayoutChanged() {
		flagChange(MASK_LAYOUT);
	}

	public TYPE getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(TYPE defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public FormPortletField<TYPE> setDisabled(Boolean disabled) {
		if (disabled == null || this.disabled == disabled)
			return this;
		this.disabled = disabled;
		flagConfigChanged();
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public FormPortletField<TYPE> setVisible(boolean visible) {
		if (this.visible == visible)
			return this;
		this.visible = visible;
		if (!visible && this.isFocused) {
			this.isFocused = false;
			if (form != null)
				this.form.onFieldFocusLost(this);
		}
		if (form != null)
			form.flagChange(FormPortlet.MASK_FIELDS);
		flagLayoutChanged();
		return this;
	}

	//return true iff should file value changed. pendingJs is ALWAYS NULL
	public boolean onUserValueChanged(Map<String, String> attributes) {
		// This should be called after the fields's own userValueChanged
		// loop through and call onUserValueChanged on all extensions
		if (extensions != null) {
			for (int i = 0; i < extensions.size(); i++) {
				extensions.get(i).onUserValueChanged(attributes);
			}
		}

		return false;
	}

	final public int getSuggestedWidth() {
		switch (this.widthMode) {
			case SIZE_FIXED:
				return Math.max(0, this.locationH.getRealizedSize());
			case SIZE_DEFAULT:
				return SIZE_DEFAULT;
			case SIZE_STRETCH:
				return SIZE_STRETCH;
			default:
				throw new IllegalArgumentException(SH.toString(this.widthMode));
		}
	}
	final public int getSuggestedHeight() {
		switch (this.heightMode) {
			case SIZE_FIXED:
				return Math.max(0, this.locationV.getRealizedSize());
			case SIZE_DEFAULT:
				return SIZE_DEFAULT;
			case SIZE_STRETCH:
				return SIZE_STRETCH;
			default:
				throw new IllegalArgumentException(SH.toString(this.heightMode));
		}
	}

	final public void updateFormSize() {
		this.locationV.setOuterSize(form.getHeight());
		this.locationH.setOuterSize(form.getWidth());
	}
	public boolean isFixedPosition() {
		return this.locationH.isDefined() && this.locationV.isDefined();
	}

	public FormPortletField<TYPE> setHeight(int height) {
		switch (height) {
			case SIZE_DEFAULT:
				this.locationV.reset();
				this.heightMode = SIZE_DEFAULT;
				break;
			case SIZE_STRETCH:
				this.locationV.reset();
				this.heightMode = SIZE_STRETCH;
				break;
			default:
				if (height < 0)
					throw new IllegalArgumentException(SH.toString(height));
				this.locationV.setSizePx(height);
				break;
		}
		return this;
	}
	public FormPortletField<TYPE> setWidth(int width) {
		switch (width) {
			case SIZE_DEFAULT:
				this.locationH.reset();
				this.widthMode = SIZE_DEFAULT;
				break;
			case SIZE_STRETCH:
				this.locationH.reset();
				this.widthMode = SIZE_STRETCH;
				break;
			default:
				if (width < 0)
					throw new IllegalArgumentException(SH.toString(width));
				this.locationH.setSizePx(width);
				break;
		}
		return this;
	}

	public void reset() {
		setValue(getDefaultValue());
	}
	public String getJsValue() {
		return OH.toString(getValue());
	}

	public String getHelp() {
		return help;
	}

	public FormPortletField<TYPE> setHelp(String help) {
		if (OH.eq(help, this.help))
			return this;
		this.help = help;
		BasicPortletMetrics bm = new BasicPortletMetrics();
		this.helpTextWidth = bm.getWidth(help, null, 13);//TODO: pass helpTextStyle in at 2nd arg
		flagChange(MASK_HELP);
		return this;
	}

	public boolean getTitleIsClickable() {
		return titleIsClickable;
	}

	public FormPortletField<TYPE> setTitleIsClickable(boolean tic) {
		if (this.titleIsClickable == tic)
			return this;
		this.titleIsClickable = tic;
		flagChange(MASK_HELP);
		return this;
	}

	public Object getCorrelationData() {
		return correlationData;
	}

	public FormPortletField<TYPE> setCorrelationData(Object correlationData) {
		this.correlationData = correlationData;
		return this;
	}

	public String toString() {
		return getClass().getSimpleName() + "[" + getId() + "]: " + getTitle() + "=" + getValue();
	}

	public String getHtmlLayoutSignature() {
		return "<span id='formfield_" + this.form.getPortletId() + "_" + getId() + "'></span>";
	}

	public String getCssStyle() {
		return this.cssStyle;
	}

	public FormPortletField<?> setCssStyle(String cssStyle) {
		if (OH.eq(this.cssStyle, cssStyle))
			return this;
		this.cssStyle = cssStyle;
		this.initStyle();
		return this;
	}

	public String getLabelCssStyle() {
		return this.labelCssStyle;
	}

	public FormPortletField<?> setLabelCssStyle(String labelCssStyle) {
		if (OH.eq(this.labelCssStyle, labelCssStyle))
			return this;
		this.labelCssStyle = labelCssStyle;
		flagStyleChanged();
		return this;
	}
	public String getHelpCssStyle() {
		return this.helpCssStyle;
	}
	public FormPortletField<?> setHelpCssStyle(String helpCssStyle) {
		if (OH.eq(this.helpCssStyle, helpCssStyle))
			return this;
		this.helpCssStyle = helpCssStyle;
		flagChange(MASK_HELP);
		return this;
	}
	public void focus() {
		ensureVisible();
		this.form.focusField(this);
	}

	public void handleCallback(String action, Map<String, String> attributes) {

	}

	@Override
	public void onLocationChanged(WebAbsoluteLocation l) {
		if (l == this.locationH) {
			if (l.isSizeDefined()) {
				this.widthMode = SIZE_FIXED;
			} else if (this.widthMode == SIZE_FIXED)
				this.widthMode = SIZE_DEFAULT;
		} else if (l == this.locationV) {
			if (l.isSizeDefined())
				this.heightMode = SIZE_FIXED;
			else if (this.heightMode == SIZE_FIXED)
				this.heightMode = SIZE_DEFAULT;
		}
		if (form != null)
			form.flagChange(FormPortlet.MASK_POSITIONS);
		flagLayoutChanged();
	}
	public WebAbsoluteLocation getHorizontalLocation() {
		return this.locationH;
	}
	public WebAbsoluteLocation getVerticalLocation() {
		return this.locationV;
	}

	///// BEGIN BASIC POSITIONING /////

	// get pixels 
	public int getLeftPosPx() {
		return this.locationH.getStartPx();
	}
	public int getRightPosPx() {
		return this.locationH.getEndPx();
	}
	public int getTopPosPx() {
		return this.locationV.getStartPx();
	}
	public int getBottomPosPx() {
		return this.locationV.getEndPx();
	}
	public int getWidthPx() {
		return this.locationH.getSizePx();
	}
	public int getHeightPx() {
		return this.locationV.getSizePx();
	}
	// get percent
	public double getLeftPosPct() {
		return this.locationH.getStartPct();
	}
	public double getRightPosPct() {
		return this.locationH.getEndPct();
	}
	public double getTopPosPct() {
		return this.locationV.getStartPct();
	}
	public double getBottomPosPct() {
		return this.locationV.getEndPct();
	}
	public double getWidthPct() {
		return this.locationH.getSizePct();
	}
	public double getHeightPct() {
		return this.locationV.getSizePct();
	}
	public double getVerticalOffsetFromCenterPct() {
		return this.locationV.getOffsetFromCenterPct();
	}
	public double getHorizontalOffsetFromCenterPct() {
		return this.locationH.getOffsetFromCenterPct();
	}
	public FormPortletField<?> setLeftPosPx(Integer pos) {
		this.locationH.setStartPx(pos == null ? WebAbsoluteLocation.PX_NA : pos);
		return this;
	}
	public FormPortletField<?> setRightPosPx(Integer pos) {
		this.locationH.setEndPx(pos == null ? WebAbsoluteLocation.PX_NA : pos);
		return this;
	}
	public FormPortletField<?> setTopPosPx(Integer pos) {
		this.locationV.setStartPx(pos == null ? WebAbsoluteLocation.PX_NA : pos);
		return this;
	}
	public FormPortletField<?> setBottomPosPx(Integer pos) {
		this.locationV.setEndPx(pos == null ? WebAbsoluteLocation.PX_NA : pos);
		return this;
	}
	public FormPortletField<?> setWidthPx(Integer width) {
		this.locationH.setSizePx(width == null ? WebAbsoluteLocation.PX_NA : width);
		return this;
	}
	public FormPortletField<?> setHeightPx(Integer height) {
		this.locationV.setSizePx(height == null ? WebAbsoluteLocation.PX_NA : height);
		return this;
	}
	// set percent
	public FormPortletField<?> setLeftPosPct(Double pos) {
		this.locationH.setStartPct(pos == null ? WebAbsoluteLocation.PCT_NA : pos);
		return this;
	}
	public FormPortletField<?> setRightPosPct(Double pos) {
		this.locationH.setEndPct(pos == null ? WebAbsoluteLocation.PCT_NA : pos);
		return this;
	}
	public FormPortletField<?> setTopPosPct(Double pos) {
		this.locationV.setStartPct(pos == null ? WebAbsoluteLocation.PCT_NA : pos);
		return this;
	}
	public FormPortletField<?> setBottomPosPct(Double pos) {
		this.locationV.setEndPct(pos == null ? WebAbsoluteLocation.PCT_NA : pos);
		return this;
	}
	public FormPortletField<?> setWidthPct(Double width) {
		this.locationH.setSizePct(width == null ? WebAbsoluteLocation.PCT_NA : width);
		return this;
	}
	public FormPortletField<?> setHeightPct(Double height) {
		this.locationV.setSizePct(height == null ? WebAbsoluteLocation.PCT_NA : height);
		return this;
	}
	public FormPortletField<?> setVerticalOffsetFromCenterPct(Double offset) {
		this.locationV.setOffsetFromCenterPct(offset == null ? WebAbsoluteLocation.PCT_NA : offset);
		return this;
	}
	public FormPortletField<?> setHorizontalOffsetFromCenterPct(Double offset) {
		this.locationH.setOffsetFromCenterPct(offset == null ? WebAbsoluteLocation.PCT_NA : offset);
		return this;
	}
	// set pixels 
	public FormPortletField<?> setLeftPosPx(int pos) {
		this.locationH.setStartPx(pos);
		return this;
	}
	public FormPortletField<?> setRightPosPx(int pos) {
		this.locationH.setEndPx(pos);
		return this;
	}
	public FormPortletField<?> setTopPosPx(int pos) {
		this.locationV.setStartPx(pos);
		return this;
	}
	public FormPortletField<?> setBottomPosPx(int pos) {
		this.locationV.setEndPx(pos);
		return this;
	}
	public FormPortletField<?> setWidthPx(int width) {
		this.locationH.setSizePx(width);
		return this;
	}
	public FormPortletField<?> setHeightPx(int height) {
		this.locationV.setSizePx(height);
		return this;
	}
	// set percent
	public FormPortletField<?> setLeftPosPct(double pos) {
		this.locationH.setStartPct(pos);
		return this;
	}
	public FormPortletField<?> setRightPosPct(double pos) {
		this.locationH.setEndPct(pos);
		return this;
	}
	public FormPortletField<?> setTopPosPct(double pos) {
		this.locationV.setStartPct(pos);
		return this;
	}
	public FormPortletField<?> setBottomPosPct(double pos) {
		this.locationV.setEndPct(pos);
		return this;
	}
	public FormPortletField<?> setWidthPct(double width) {
		this.locationH.setSizePct(width);
		return this;
	}
	public FormPortletField<?> setHeightPct(double height) {
		this.locationV.setSizePct(height);
		return this;
	}
	public FormPortletField<?> setVerticalOffsetFromCenterPct(double offset) {
		this.locationV.setOffsetFromCenterPct(offset);
		return this;
	}
	public FormPortletField<?> setHorizontalOffsetFromCenterPct(double offset) {
		this.locationH.setOffsetFromCenterPct(offset);
		return this;
	}
	// clear pixels 
	public FormPortletField<?> clearLeftPosPx() {
		this.locationH.setStartPx(WebAbsoluteLocation.PX_NA);
		return this;
	}
	public FormPortletField<?> clearRightPosPx() {
		this.locationH.setEndPx(WebAbsoluteLocation.PX_NA);
		return this;
	}
	public FormPortletField<?> clearTopPosPx() {
		this.locationV.setStartPx(WebAbsoluteLocation.PX_NA);
		return this;
	}
	public FormPortletField<?> clearBottomPosPx() {
		this.locationV.setEndPx(WebAbsoluteLocation.PX_NA);
		return this;
	}
	public FormPortletField<?> clearWidthPx() {
		this.locationH.setSizePx(WebAbsoluteLocation.PX_NA);
		return this;
	}
	public FormPortletField<?> clearHeightPx() {
		this.locationV.setSizePx(WebAbsoluteLocation.PX_NA);
		return this;
	}
	// clear percent
	public FormPortletField<?> clearLeftPosPct() {
		this.locationH.setStartPct(WebAbsoluteLocation.PCT_NA);
		return this;
	}
	public FormPortletField<?> clearRightPosPct() {
		this.locationH.setEndPct(WebAbsoluteLocation.PCT_NA);
		return this;
	}
	public FormPortletField<?> clearTopPosPct() {
		this.locationV.setStartPct(WebAbsoluteLocation.PCT_NA);
		return this;
	}
	public FormPortletField<?> clearBottomPosPct() {
		this.locationV.setEndPct(WebAbsoluteLocation.PCT_NA);
		return this;
	}
	public FormPortletField<?> clearWidthPct() {
		this.locationH.setSizePct(WebAbsoluteLocation.PCT_NA);
		return this;
	}
	public FormPortletField<?> clearHeightPct() {
		this.locationV.setSizePct(WebAbsoluteLocation.PCT_NA);
		return this;
	}
	public FormPortletField<?> clearVerticalOffsetFromCenterPct() {
		this.locationV.setOffsetFromCenterPct(WebAbsoluteLocation.PCT_NA);
		return this;
	}
	public FormPortletField<?> clearHorizontalOffsetFromCenterPct() {
		this.locationH.setOffsetFromCenterPct(WebAbsoluteLocation.PCT_NA);
		return this;
	}
	public void clearHorizontalPositioning() {
		clearLeftPosPx();
		clearRightPosPx();
		clearWidthPx();
		clearLeftPosPct();
		clearRightPosPct();
		clearWidthPct();
		clearHorizontalOffsetFromCenterPct();
	}
	public void clearVerticalPositioning() {
		clearTopPosPx();
		clearBottomPosPx();
		clearHeightPx();
		clearTopPosPct();
		clearBottomPosPct();
		clearHeightPct();
		clearVerticalOffsetFromCenterPct();
	}
	public void clearAllPositioning() {
		clearHorizontalPositioning();
		clearVerticalPositioning();
	}

	public FormPortletField<?> centerHorizontally() {
		this.locationH.center();
		return this;
	}
	public FormPortletField<?> centerVertically() {
		this.locationV.center();
		return this;
	}

	///// END BASIC POSITIONING /////

	public int getModificationNumber() {
		return modificationNumber;
	}

	public int incrementAndGetModificationNumber() {
		return ++modificationNumber;
	}

	public int getRealizedLeftPosPx() {
		return this.locationH.getRealizedStart();
	}
	public int getRealizedTopPosPx() {
		return this.locationV.getRealizedStart();
	}
	public int getRealizedWidthPx() {
		return this.locationH.getRealizedSize();
	}
	public int getRealizedHeightPx() {
		return this.locationV.getRealizedSize();
	}
	public int getRealizedRightPosPx() {
		return this.locationH.getRealizedEnd();
	}
	public int getRealizedBottomPosPx() {
		return this.locationV.getRealizedEnd();
	}

	public WebAbsoluteLocation getAbsLocationH() {
		return locationH;
	}
	public WebAbsoluteLocation getAbsLocationV() {
		return locationV;
	}

	public boolean isLabelHidden() {
		return labelHidden;
	}

	public FormPortletField<?> setLabelHidden(Boolean labelHidden) {
		if (labelHidden == null || this.labelHidden == labelHidden)
			return this;
		this.labelHidden = labelHidden;
		flagConfigChanged();

		return this;
	}

	public int getZIndex() {
		return zIndex;
	}

	public FormPortletField<?> setZIndex(int zIndex) {
		if (this.zIndex == zIndex)
			return this;
		this.zIndex = zIndex;
		flagLayoutChanged();
		return this;
	}

	final public static byte LABEL_SIDE_TOP = 0;
	final public static byte LABEL_SIDE_BOTTOM = 1;
	final public static byte LABEL_SIDE_LEFT = 2;
	final public static byte LABEL_SIDE_RIGHT = 3;
	final public static byte LABEL_SIDE_ALIGN_START = 0;
	final public static byte LABEL_SIDE_ALIGN_CENTER = 1;
	final public static byte LABEL_SIDE_ALIGN_END = 2;

	public void setLabelSideAlignment(byte alignment) {
		if (OH.eq(alignment, this.labelSideAlignment))
			return;
		this.labelSideAlignment = alignment;
		flagChange(MASK_POSITIONS);
	}
	public byte getLabelSideAlignment() {
		return this.labelSideAlignment;
	}
	public void setLabelSide(byte side) {
		if (OH.eq(side, this.labelSide))
			return;
		this.labelSide = side;
		updateLabelPosition();
	}
	public byte getLabelSide() {
		return this.labelSide;
	}

	public void setLabelWidthPx(int labelWidthPx) {
		if (OH.eq(labelWidthPx, this.labelWidthPx))
			return;
		this.labelWidthPx = labelWidthPx;
		updateLabelPosition();
	}
	public int getLabelWidthPx() {
		return labelWidthPx;
	}

	public void setLabelHeightPx(int labelHeightPx) {
		if (OH.eq(labelHeightPx, this.labelWidthPx))
			return;
		this.labelHeightPx = labelHeightPx;
		flagChange(MASK_LABEL);
	}
	public int getLabelHeightPx() {
		return labelHeightPx;
	}

	public int getCalculatedLabelLeftPx() {
		return calculatedLabelLeftPx;
	}

	public int getCalculatedLabelTopPx() {
		return calculatedLabelTopPx;
	}

	public int getDefaultHeight() {
		return DEFAULT_HEIGHT;
	}
	public int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}

	public void setLeftTopWidthHeightPx(int left, int top, int width, int height) {
		setLeftPosPx(left);
		setTopPosPx(top);
		setWidthPx(width);
		setHeightPx(height);
	}
	public void setLeftTopRightBottom(int left, int top, int right, int bottom) {
		setLeftPosPx(left);
		setTopPosPx(top);
		setRightPosPx(right);
		setBottomPosPx(bottom);
	}

	public boolean isInsideFieldPx(int x, int y) { // x and y are positions within form, not with respect to desktop origin
		return this.locationH.isInsidePx(x) && this.locationV.isInsidePx(y);
	}

	public int getLabelPaddingPx() {
		return labelPaddingPx;
	}
	public void setLabelPaddingPx(Integer labelPaddingPx) {
		if (labelPaddingPx == null || labelPaddingPx.equals(this.labelPaddingPx))
			return;
		this.labelPaddingPx = labelPaddingPx;
		flagChange(MASK_POSITIONS);
		updateLabelPosition();
	}
	protected void initStyle() {
		this.style = null;//flag for rebuild
		flagStyleChanged();
	}
	public void setFontColor(String fontColor) {
		if (OH.eq(this.fontColor, fontColor))
			return;
		this.fontColor = fontColor;
		initStyle();
	}

	private static StringBuilder appendPipe(StringBuilder temp) {
		if (temp.length() > 0 && temp.charAt(temp.length() - 1) != '|')
			temp.append('|');
		return temp;
	}

	protected StringBuilder getFieldStyles(StringBuilder temp) {
		if (this.fieldFontSize != null)
			appendPipe(temp).append("_fs=").append(this.fieldFontSize);
		if (this.fieldFontFamily != null)
			appendPipe(temp).append("_fm=").append(this.fieldFontFamily);
		if (this.fontColor != null)
			appendPipe(temp).append("_fg=").append(this.fontColor);
		return temp;
	}

	protected StringBuilder getBorderStyles(StringBuilder temp) {
		if (this.getBorderColorMaterialized() != null)
			appendPipe(temp).append("style.borderColor=").append(this.getBorderColorMaterialized());
		if (this.getBorderWidthMaterialized() != null)
			appendPipe(temp).append("style.borderWidth=").append(this.getBorderWidthMaterialized()).append("px");
		if (this.borderRadius != null)
			appendPipe(temp).append("style.borderRadius=").append(this.borderRadius).append("px");
		return temp;
	}

	protected String getStyle() {
		if (style == null) {
			StringBuilder temp = new StringBuilder();
			if (SH.is(this.cssStyle))
				temp.append(this.cssStyle).append('|');
			temp = getFieldStyles(temp);
			temp = getBorderStyles(temp);
			if (this.bgColor != null) {
				if (temp.length() > 0)
					temp.append("|");
				temp.append("_bg=").append(this.bgColor);
			}
			this.style = temp.toString();
		}
		return style;
	}
	public String getFontColor() {
		return fontColor;
	}

	public void setBorderWidth(Integer borderWidth) {
		if (OH.eq(this.borderWidth, borderWidth))
			return;
		this.borderWidth = borderWidth;
		if (!isFocused || focusedBorderWidth == null)
			initStyle();
	}

	public Integer getBorderWidth() {
		return this.borderWidth;
	}
	public Integer getBorderWidthMaterialized() {
		if (this.isFocused && this.focusedBorderWidth != null)
			return this.focusedBorderWidth;
		return this.borderWidth;
	}

	public Integer getBorderRadius() {
		return borderRadius;
	}

	public void setBorderRadius(Integer borderRadius) {
		if (OH.eq(this.borderRadius, borderRadius))
			return;
		this.borderRadius = borderRadius;
		initStyle();
	}

	public void setBorderColor(String bdrColor) {
		if (OH.eq(this.borderColor, bdrColor))
			return;
		this.borderColor = bdrColor;
		if (!isFocused || focusedBorderColor == null)
			initStyle();
	}
	public String getBorderColor() {
		return this.borderColor;
	}
	public String getBorderColorMaterialized() {
		if (this.isFocused && this.focusedBorderColor != null)
			return this.focusedBorderColor;
		return this.borderColor;
	}
	public void setBgColor(String bgColor) {
		if (OH.eq(this.bgColor, bgColor))
			return;
		this.bgColor = bgColor;
		initStyle();
	}
	public String getBgColor() {
		return bgColor;
	}
	public void setFocusedBorderColor(String bdrColor) {
		if (OH.eq(this.focusedBorderColor, bdrColor))
			return;
		this.focusedBorderColor = bdrColor;
		if (isFocused)
			initStyle();
	}
	public String getFocusedBorderColor() {
		return this.focusedBorderColor;
	}
	public void setFocusedBorderWidth(Integer borderWidth) {
		if (OH.eq(this.focusedBorderWidth, borderWidth))
			return;
		this.focusedBorderWidth = borderWidth;
		if (isFocused)
			initStyle();
	}

	public Integer getFocusedBorderWidth() {
		return this.focusedBorderWidth;
	}

	public String getLabelColor() {
		return labelColor;
	}
	public void setLabelColor(String labelColor) {
		if (OH.eq(labelColor, this.labelColor))
			return;
		this.labelColor = labelColor;
		flagChange(MASK_LABEL);
	}

	public Integer getLabelFontSize() {
		return labelFontSize;
	}
	public void setLabelFontSize(Integer labelFontSize) {
		if (OH.eq(labelFontSize, this.labelFontSize))
			return;
		this.labelFontSize = labelFontSize;
		flagChange(MASK_LABEL);
	}

	public Boolean getLabelBold() {
		return labelBold;
	}
	public void setLabelBold(Boolean labelBold) {
		if (OH.eq(labelBold, this.labelBold))
			return;
		this.labelBold = labelBold;
		flagChange(MASK_LABEL);
	}

	public Boolean getLabelItalic() {
		return labelItalic;
	}
	public void setLabelItalic(Boolean labelItalic) {
		if (OH.eq(labelItalic, this.labelItalic))
			return;
		this.labelItalic = labelItalic;
		flagChange(MASK_LABEL);
	}

	public Boolean getLabelUnderline() {
		return labelUnderline;
	}
	public void setLabelUnderline(Boolean labelUnderline) {
		if (OH.eq(labelUnderline, this.labelUnderline))
			return;
		this.labelUnderline = labelUnderline;
		flagChange(MASK_LABEL);
	}

	public String getLabelFontFamily() {
		return labelFontFamily;
	}
	public void setLabelFontFamily(String labelFontFamily) {
		if (OH.eq(labelFontFamily, this.labelFontFamily))
			return;
		this.labelFontFamily = labelFontFamily;
		flagChange(MASK_LABEL);
	}

	public boolean setValueNoThrow(TYPE object) {
		try {
			setValue(object);
			return true;
		} catch (Exception e) {
			LH.warning(log, "Caught exception for value ", object, ": ", e);
			return false;
		}
	}

	public int getCalculatedLabelHeightPx() {
		return calculatedLabelHeightPx;
	}

	public int getCalculatedLabelWidthPx() {
		return calculatedLabelWidthPx;
	}

	public String getFieldFontFamily() {
		return fieldFontFamily;
	}

	public void setFieldFontFamily(String fieldFontFamily) {
		if (OH.eq(this.fieldFontFamily, fieldFontFamily))
			return;
		this.fieldFontFamily = fieldFontFamily;
		initStyle();
	}

	public void setFieldFontSize(Integer fontSize) {
		if (OH.eq(this.fieldFontSize, fontSize))
			return;
		this.fieldFontSize = fontSize;
		initStyle();
	}

	public Integer getFieldFontSize() {
		return fieldFontSize;
	}

	public void setHtmlIdSelector(String his) {
		if (OH.eq(this.htmlIdSelector, his))
			return;
		this.htmlIdSelector = his;
		flagConfigChanged();
	}

	public String getHtmlIdSelector() {
		return this.htmlIdSelector;
	}

	public boolean canFocus() {
		return true;
	}

	final public boolean isFocused() {
		return isFocused;
	}

	//only called by FormPortet
	final protected void onFocused(boolean isFocused) {
		if (this.isFocused == isFocused)
			return;
		this.isFocused = isFocused;
		this.initStyle();
	}

	final public void ensureVisible() {
		if (this.form == null)
			return;
		int top = Math.max(getCalculatedTopPosPx(), 0);
		if (this.getLabelSide() == LABEL_SIDE_TOP)
			top = Math.max(top - labelHeightPx, 0);
		int clipTop = this.form.getClipTop();
		if (top < clipTop) {
			this.form.setClipTop(top);
		} else {
			int bottom = getCalculatedBottomPx();
			if (this.getLabelSide() == LABEL_SIDE_BOTTOM)
				bottom += labelHeightPx;
			int needsToMove = bottom - this.form.getClipBottom();
			if (needsToMove > 0)
				this.form.setClipTop(clipTop + needsToMove);
		}
		int left = Math.max(getCalculatedLeftPosPx(), 0);
		if (this.getLabelSide() == LABEL_SIDE_LEFT)
			left = Math.max(left - labelWidthPx, 0);
		int clipLeft = this.form.getClipLeft();
		if (left < clipLeft) {
			this.form.setClipLeft(left);
		} else {
			int right = getCalculatedRightPx();
			if (this.getLabelSide() == LABEL_SIDE_RIGHT)
				right += labelWidthPx;
			int needsToMove = right - this.form.getClipRight();
			if (needsToMove > 0)
				this.form.setClipLeft(clipLeft + needsToMove);
		}
	}

	public boolean hasChanged(int mask) {
		return MH.anyBits(this.changes, mask);
	}

	public void clearChanges() {
		this.changes = 0;
	}

	public void setCalculatedPosition(int x, int y, int w, int h) {
		if (this.calculatedLeftPx == x && this.calculatedTopPx == y && this.calculatedWidthPx == w && this.calculatedHeightPx == h)
			return;
		this.calculatedLeftPx = x;
		this.calculatedTopPx = y;
		this.calculatedWidthPx = w;
		this.calculatedHeightPx = h;
		updateLabelPosition();
		flagChange(MASK_POSITIONS);
	}

	private void updateLabelPosition() {
		int oldH = this.calculatedLabelHeightPx;
		int oldW = this.calculatedLabelWidthPx;
		int oldL = this.calculatedLabelLeftPx;
		int oldT = this.calculatedLabelTopPx;
		switch (this.labelSide) {
			case LABEL_SIDE_TOP:
				this.calculatedLabelHeightPx = this.labelHeightPx;
				this.calculatedLabelWidthPx = this.calculatedWidthPx;
				this.calculatedLabelLeftPx = this.calculatedLeftPx;
				this.calculatedLabelTopPx = this.calculatedTopPx - this.labelHeightPx;
				break;

			case LABEL_SIDE_RIGHT:
				this.calculatedLabelHeightPx = this.calculatedHeightPx;
				this.calculatedLabelWidthPx = this.labelWidthPx;
				this.calculatedLabelLeftPx = this.calculatedLeftPx + this.calculatedWidthPx;
				this.calculatedLabelTopPx = this.calculatedTopPx;
				break;

			case LABEL_SIDE_BOTTOM:
				this.calculatedLabelHeightPx = this.labelHeightPx;
				this.calculatedLabelWidthPx = this.calculatedWidthPx;
				this.calculatedLabelLeftPx = this.calculatedLeftPx;
				this.calculatedLabelTopPx = this.calculatedTopPx + this.calculatedHeightPx;
				break;

			case LABEL_SIDE_LEFT:
				this.calculatedLabelHeightPx = this.calculatedHeightPx;
				this.calculatedLabelWidthPx = this.labelWidthPx;
				this.calculatedLabelLeftPx = this.calculatedLeftPx - this.labelWidthPx;
				this.calculatedLabelTopPx = this.calculatedTopPx;
				break;
		}
		if (oldH != this.calculatedLabelHeightPx || oldW != this.calculatedLabelWidthPx || oldL != this.calculatedLabelLeftPx || oldT != this.calculatedLabelTopPx)
			flagChange(MASK_POSITIONS);
	}

	public int getCalculatedRightPx() {
		return this.calculatedLeftPx + this.calculatedWidthPx;
	}
	public int getCalculatedBottomPx() {
		return this.calculatedTopPx + this.calculatedHeightPx;
	}
	public int getCalculatedLeftPosPx() {
		return calculatedLeftPx;
	}

	public int getCalculatedTopPosPx() {
		return calculatedTopPx;
	}

	public int getCalculatedWidthPx() {
		return calculatedWidthPx;
	}

	public int getCalculatedHeightPx() {
		return calculatedHeightPx;
	}

	public void flagRebuild() {
		flagChange(0xFFFFFFFF ^ (FormPortletField.MASK_SELECTION | FormPortletField.MASK_CURSOR | FormPortletField.MASK_CUSTOM));
	}

	public String getJsObjectName() {
		return this.jsObjectName;
	}
	public void setJsObjectName(String newJsObjectName) {
		// Might cause state issues if we implement
		throw new UnsupportedOperationException();
	}

	public void callExtensionUpdateJs(StringBuilder pendingJs) {
		for (FormPortletFieldExtension ext : this.extensions) {
			if (ext.hasUpdate())
				ext.updateJs(pendingJs);
		}

	}
	public FormPortletField<?> setStyle(String style) {
		if (OH.eq(style, this.style))
			return this;
		this.style = style;
		flagStyleChanged();
		return this;
	}

	//return false to stop propagation of menu item click
	public boolean onMenuItem(Map<String, String> attributes) {
		return true;
	}

	public void setIgnoreDefaultStyle(boolean b) {
		this.ignoreDefaultStyle = b;
	}
	public boolean getIgnoreDefaultStyle() {
		return this.ignoreDefaultStyle;
	}
}
