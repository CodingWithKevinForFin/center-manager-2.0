package com.f1.ami.web.style;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.ami.web.style.impl.AmiWebStyleOptionBoolean;
import com.f1.ami.web.style.impl.AmiWebStyleOptionChoices;
import com.f1.ami.web.style.impl.AmiWebStyleOptionRange;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.utils.CH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.MapInMap;

public class AmiWebStyleType implements Lockable {

	private String name;
	private String group;
	private Map<Short, AmiWebStyleOption> optionsByKey = new HasherMap<Short, AmiWebStyleOption>();
	private Map<String, AmiWebStyleOption> optionsByVarname = new HasherMap<String, AmiWebStyleOption>();
	private MapInMap<String, String, AmiWebStyleOption> optionsByLabel = new MapInMap<String, String, AmiWebStyleOption>();
	private String extendsName;
	private String userlabel;
	private boolean locked;

	final public String getName() {
		return this.name;
	}
	final public Set<Short> getKeys() {
		return this.optionsByKey.keySet();
	}
	final public Set<String> getVarnames() {
		return this.optionsByVarname.keySet();
	}
	public Collection<AmiWebStyleOption> getOptions() {
		return this.optionsByKey.values();
	}

	public AmiWebStyleOption getOptionByKey(short key) {
		return this.optionsByKey.get(key);
	}
	public AmiWebStyleOption getOptionByVarname(String key) {
		return this.optionsByVarname.get(key);
	}

	final public String getUserLabel() {
		return this.userlabel;
	}

	final public String getExtendsName() {
		return this.extendsName;
	}

	protected AmiWebStyleType(String name, String userLabel, String extendsName) {
		this.optionsByLabel.setInnerMap(new LinkedHashMap<String, Map<String, AmiWebStyleOption>>());
		this.optionsByLabel.setMapFactory(MapInMap.FACTORY_LINKED_HASHMAP);
		this.userlabel = userLabel;
		this.extendsName = extendsName;
		this.name = name;
	}

	protected void startGroup(String group) {
		this.group = group;
	}
	protected AmiWebStyleOption addFontField(short key, String name) {
		AmiWebStyleOption r = new AmiWebStyleOption(key, AmiWebStyleConsts.GET(key), this.name, group, name, AmiWebStyleConsts.TYPE_FONT);
		add(r);
		return r;
	}
	protected AmiWebStyleOption addFontSizeField(short key, String name) {
		AmiWebStyleOptionRange r = new AmiWebStyleOptionRange(key, AmiWebStyleConsts.GET(key), this.name, group, name, AmiWebStyleConsts.FONT_SIZE_MIN,
				AmiWebStyleConsts.FONT_SIZE_MAX);
		add(r);
		return r;
	}

	protected AmiWebStyleOption addFontSizeField(short key, String name, int maxFontSize) {
		AmiWebStyleOptionRange r = new AmiWebStyleOptionRange(key, AmiWebStyleConsts.GET(key), this.name, group, name, AmiWebStyleConsts.FONT_SIZE_MIN, maxFontSize);
		add(r);
		return r;
	}

	protected AmiWebStyleOptionRange addRangeField(short key, String name, int min, int max) {
		AmiWebStyleOptionRange r = new AmiWebStyleOptionRange(key, AmiWebStyleConsts.GET(key), this.name, group, name, min, max);
		add(r);
		return r;
	}

	//showAlpha is IGNORED!
	@Deprecated
	protected AmiWebStyleOption addColorField(short key, String name, boolean showAlpha) {
		return addColorField(key, name);
	}
	protected AmiWebStyleOption addColorField(short key, String name) {
		AmiWebStyleOption r = new AmiWebStyleOption(key, AmiWebStyleConsts.GET(key), this.name, group, name, AmiWebStyleConsts.TYPE_COLOR);
		add(r);
		return r;
	}
	protected AmiWebStyleOption addColorGradientField(short key, String name) {
		AmiWebStyleOption r = new AmiWebStyleOption(key, AmiWebStyleConsts.GET(key), this.name, group, name, AmiWebStyleConsts.TYPE_COLOR_GRADIENT);
		add(r);
		return r;
	}
	protected AmiWebStyleOption addColorsField(short key, String name, int w, int h) {
		AmiWebStyleOption r = new AmiWebStyleOption(key, AmiWebStyleConsts.GET(key), this.name, group, name, AmiWebStyleConsts.TYPE_COLOR_ARRAY).setFieldSize(w, h);
		add(r);
		return r;
	}
	protected <T> AmiWebStyleOptionChoices addToggleField(short key, String name, Class<T> type, T k1, String sv1, String dv1, T k2, String sv2, String dv2, T k3, String sv3,
			String dv3) {
		AmiWebStyleOptionChoices r = new AmiWebStyleOptionChoices(key, AmiWebStyleConsts.GET(key), this.name, group, name, type);
		r.addOption(k1, sv1, dv1);
		r.addOption(k2, sv2, dv2);
		r.addOption(k3, sv3, dv3);
		add(r);
		return r;
	}
	protected <T> AmiWebStyleOptionChoices addToggleField(short key, String name, Class<T> type, T k1, String sv1, String dv1, T k2, String sv2, String dv2) {
		AmiWebStyleOptionChoices r = new AmiWebStyleOptionChoices(key, AmiWebStyleConsts.GET(key), this.name, group, name, type);
		r.addOption(k1, sv1, dv1);
		r.addOption(k2, sv2, dv2);
		add(r);
		return r;
	}
	protected AmiWebStyleOptionBoolean addTrueFalseToggleField(short key, String name, String truu, String falss) {
		AmiWebStyleOptionBoolean r = new AmiWebStyleOptionBoolean(key, AmiWebStyleConsts.GET(key), this.name, group, name, truu, falss);
		add(r);
		return r;
	}
	protected AmiWebStyleOptionBoolean addFalseTrueToggleField(short key, String name, String falss, String truu) {
		AmiWebStyleOptionBoolean r = new AmiWebStyleOptionBoolean(key, AmiWebStyleConsts.GET(key), this.name, group, name, truu, falss).setShowFalseFirst(true);
		add(r);
		return r;
	}
	protected <T> AmiWebStyleOptionChoices addSelectField(short key, String name, Class<T> type) {
		AmiWebStyleOptionChoices r = new AmiWebStyleOptionChoices(key, AmiWebStyleConsts.GET(key), this.name, group, name, type).setUseSelect(true);
		add(r);
		return r;
	}
	protected void add(AmiWebStyleOption option) {
		LockedException.assertNotLocked(this);
		if (option.getGroupLabel() == null)
			throw new IllegalStateException("Call addTitleField first to set group label");
		this.optionsByLabel.putMultiOrThrow(option.getGroupLabel(), option.getLabel(), option);
		CH.putOrThrow(optionsByKey, option.getKey(), option);
		CH.putOrThrow(optionsByVarname, option.getVarname(), option);
	}
	protected AmiWebStyleOption addCssSelectField(short key, String name) {
		AmiWebStyleOption r = new AmiWebStyleOption(key, AmiWebStyleConsts.GET(key), this.name, group, name, AmiWebStyleConsts.TYPE_CSS_CLASS);
		add(r);
		return r;
	}
	final public MapInMap<String, String, AmiWebStyleOption> getGroupLabels() {
		return this.optionsByLabel;
	}
	protected void addScrollbarFields() {
		startGroup("Scrollbar Options");
		addRangeField(AmiWebStyleConsts.CODE_SCROLL_WD, "Width", 5, 50);
		addColorField(AmiWebStyleConsts.CODE_SCROLL_GRIP_CL, "Grip Color");
		addColorField(AmiWebStyleConsts.CODE_SCROLL_TRACK_CL, "Track Color");
		addColorField(AmiWebStyleConsts.CODE_SCROLL_BTN_CL, "Button Color");
		addColorField(AmiWebStyleConsts.CODE_SCROLL_ICONS_CL, "Icons Color");
		addColorField(AmiWebStyleConsts.CODE_SCROLL_BDR_CL, "Border Color");
		addToggleField(AmiWebStyleConsts.CODE_SCROLL_BAR_RADIUS, "Grip Border Radius", Integer.class, 75, "Rounded", "Rounded", 0, "Flat", "Flat");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_SCROLL_BAR_HIDE_ARROWS, "Hide Arrows", "Hide", "Show");
		addColorField(AmiWebStyleConsts.CODE_SCROLL_BAR_CORNER_CL, "Corner Color");
	}

	protected void addFieldFields(boolean includeFont) {
		startGroup("Field");
		addColorField(AmiWebStyleConsts.CODE_FLD_BG_CL, "Background Color");
		if (includeFont) {
			addColorField(AmiWebStyleConsts.CODE_FLD_FONT_CL, "Font Color");
			addFontField(AmiWebStyleConsts.CODE_FLD_FONT_FAM, "Font Family");
			addFontSizeField(AmiWebStyleConsts.CODE_FLD_FONT_SZ, "Font Size");
		}
		addColorField(AmiWebStyleConsts.CODE_FLD_BDR_CL, "Border Color");
		addRangeField(AmiWebStyleConsts.CODE_FLD_BDR_WD, "Border Width", 0, 10);
		addRangeField(AmiWebStyleConsts.CODE_FLD_BDR_RAD, "Border Radius", 0, 10);
		startGroup("Focus");
		addColorField(AmiWebStyleConsts.CODE_FLD_FCS_BDR_CL, "Focus Border Color");
		addRangeField(AmiWebStyleConsts.CODE_FLD_FCS_BDR_WD, "Focus Border Width", 0, 10);
		startGroup("Label");
		addColorField(AmiWebStyleConsts.CODE_FONT_CL, "Label Font Color");
		addRangeField(AmiWebStyleConsts.CODE_LBL_PD, "Label Padding", 0, 30);
		addFontSizeField(AmiWebStyleConsts.CODE_FONT_SZ, "Font Size");
		addFontField(AmiWebStyleConsts.CODE_FONT_FAM, "Font Family");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_BOLD, "Is Bold", "Normal", "Bold").setTrueStyle("style.fontWeight=900");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_ITALIC, "Is Italic", "Normal", "Italic").setTrueStyle("style.fontStyle=italic");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_UNDERLINE, "Is Underline", "Normal", "Underline").setTrueStyle("style.textDecoration=underline");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_FLD_LBL_STATUS, "Show Label", "Show", "Hide");
		addToggleField(AmiWebStyleConsts.CODE_FLD_LBL_SIDE, "Side", Byte.class, FormPortletField.LABEL_SIDE_LEFT, "left", "Left", FormPortletField.LABEL_SIDE_RIGHT, "right",
				"Right", FormPortletField.LABEL_SIDE_TOP, "top", "Top").addOption(FormPortletField.LABEL_SIDE_BOTTOM, "bottom", "Bottom").setMinButtonWidth(30);
		addToggleField(AmiWebStyleConsts.CODE_FLD_LBL_ALIGN, "Alignment", Byte.class, FormPortletField.LABEL_SIDE_ALIGN_START, "start", "Start",
				FormPortletField.LABEL_SIDE_ALIGN_CENTER, "center", "Center", FormPortletField.LABEL_SIDE_ALIGN_END, "end", "End");
		startGroup("Css");
		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_HELP, "Help CSS Class");
		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS, "Field CSS Class");
		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_LBL, "Label CSS Class");
	}

	protected void addVisualizationFields() {
		startGroup("Visualization Title");
		addFontField(AmiWebStyleConsts.CODE_TITLE_PNL_FONT_FAM, "Font");
		addRangeField(AmiWebStyleConsts.CODE_TITLE_PNL_FONT_SZ, "Font Size", 0, 40);
		addToggleField(AmiWebStyleConsts.CODE_TITLE_PNL_ALIGN, "Alignment", String.class, "left", "left", "Left", "center", "center", "Center", "right", "right", "Right");
		addColorField(AmiWebStyleConsts.CODE_TITLE_PNL_FONT_CL, "Title Color");

		startGroup("Visualization Padding");
		addRangeField(AmiWebStyleConsts.CODE_PD_LF_PX, "Left", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_PD_RT_PX, "Right", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_PD_TP_PX, "Top", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_PD_BTM_PX, "Bottom", 0, 100);

		addRangeField(AmiWebStyleConsts.CODE_PD_RAD_TP_LF_PX, "Top-Left Radius (px)", 0, 30);
		addRangeField(AmiWebStyleConsts.CODE_PD_RAD_TP_RT_PX, "Top-Right Radius (px)", 0, 30);
		addRangeField(AmiWebStyleConsts.CODE_PD_RAD_BTM_LF_PX, "Bottom-Left Radius (px)", 0, 30);
		addRangeField(AmiWebStyleConsts.CODE_PD_RAD_BTM_RT_PX, "Bottom-Right Radius (px)", 0, 30);
		addColorField(AmiWebStyleConsts.CODE_PD_CL, "Color");

		startGroup("Visualization Shadow");
		addRangeField(AmiWebStyleConsts.CODE_PD_SHADOW_HZ_PX, "Horizontal", -20, 20);
		addRangeField(AmiWebStyleConsts.CODE_PD_SHADOW_VT_PX, "Vertical", -20, 20);
		addRangeField(AmiWebStyleConsts.CODE_PD_SHADOW_SZ_PX, "Size", 0, 100);
		addColorField(AmiWebStyleConsts.CODE_PD_SHADOW_CL, "Color");

		startGroup("Visualization Border");
		addRangeField(AmiWebStyleConsts.CODE_PD_BDR_SZ_PX, "Size", 0, 25);
		addColorField(AmiWebStyleConsts.CODE_PD_BDR_CL, "Color");
	}
	@Override
	final public void lock() {
		if (this.locked)
			return;
		this.locked = true;
		for (AmiWebStyleOption i : this.optionsByKey.values())
			i.lock();
	}
	@Override
	final public boolean isLocked() {
		return this.locked;
	}

}
