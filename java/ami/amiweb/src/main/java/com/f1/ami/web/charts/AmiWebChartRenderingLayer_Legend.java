package com.f1.ami.web.charts;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.charts.AmiWebChartSeries.Grouping;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_RenderingLayer_Legend;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.utils.CH;
import com.f1.utils.ColorHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebChartRenderingLayer_Legend extends AmiWebChartRenderingLayer<AmiWebChartSeries> implements AmiWebChartSeriesListener, AmiWebStyledPortlet {

	public static final byte KEY_HIDDEN = -1;
	public static final byte KEY_CENTER = 0;
	public static final byte KEY_TOP = 1;
	public static final byte KEY_BOTTOM = 2;
	public static final byte KEY_LEFT = 4;
	public static final byte KEY_RIGHT = 8;
	public static final byte KEY_BOTTOM_LEFT = KEY_BOTTOM | KEY_LEFT;
	public static final byte KEY_BOTTOM_RIGHT = KEY_BOTTOM | KEY_RIGHT;
	public static final byte KEY_TOP_LEFT = KEY_TOP | KEY_LEFT;
	public static final byte KEY_TOP_RIGHT = KEY_TOP | KEY_RIGHT;
	private static final String DEFAULT_FONT = "courier";
	private byte keyPosition;
	private String fontFamily;
	private int maxWidth;
	private int maxHeight;
	private int hPadding;
	private int vPadding;
	private byte namePosition;
	private int nameSize;
	private String nameColor;
	private int labelSize;
	private String borderColor;
	private String backgroundColor;
	private String checkboxColor;
	private String checkboxCheckColor;
	private String checkboxBorderColor;
	private final Set<Integer> series = new HashSet<Integer>();
	private AmiWebOverrideValue<Integer> dragXPos = new AmiWebOverrideValue<Integer>(0);
	private AmiWebOverrideValue<Integer> dragYPos = new AmiWebOverrideValue<Integer>(0);
	private final BasicMultiMap.Set<Integer, Integer> checked = new BasicMultiMap.Set<Integer, Integer>(); //seriesId --> [groupingIds]

	public static final byte BYTE_KEY_BG_CL = 127;
	public static final byte BYTE_KEY_BDR_CL = 126;
	public static final byte BYTE_KEY_FONT_FAM = 125;
	public static final byte BYTE_KEY_HZ_PD = 124;
	public static final byte BYTE_KEY_KEY_POS = 123;
	public static final byte BYTE_KEY_LBL_SZ = 122;
	public static final byte BYTE_KEY_MAX_HT = 121;
	public static final byte BYTE_KEY_MAX_WD = 120;
	public static final byte BYTE_KEY_NM_CL = 119;
	public static final byte BYTE_KEY_NM_POS = 118;
	public static final byte BYTE_KEY_NM_SZ = 117;
	public static final byte BYTE_KEY_VT_PD = 116;
	public static final byte BYTE_KEY_LGD_CHECKBOX_CL = 115;
	public static final byte BYTE_KEY_LGD_CHECKBOX_CHECK = 114;
	public static final byte BYTE_KEY_LGD_CHECKBOX_BDR_CL = 113;

	public static final String USERLABEL_KEY_BG_CL = "Background Color";
	public static final String USERLABEL_KEY_BDR_CL = "Border Color";
	public static final String USERLABEL_KEY_FONT_FAM = "Font Family";
	public static final String USERLABEL_KEY_HZ_PD = "Horizontal Padding";
	public static final String USERLABEL_KEY_KEY_POS = "Legend Position";
	public static final String USERLABEL_KEY_LBL_SZ = "Title Font Size";
	public static final String USERLABEL_KEY_MAX_HT = "Legend Height";
	public static final String USERLABEL_KEY_MAX_WD = "Legend Width";
	public static final String USERLABEL_KEY_NM_CL = "Title Color";
	public static final String USERLABEL_KEY_NM_POS = "Title Position";
	public static final String USERLABEL_KEY_NM_SZ = "Title Font Size";
	public static final String USERLABEL_KEY_VT_PD = "Vertical Padding";

	public static final String TYPE = "Legend";

	private static final Map<String, Byte> LEGEND_STRINGS_TO_BYTES;
	static {
		LEGEND_STRINGS_TO_BYTES = new HashMap<String, Byte>();

		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_BG_CL, BYTE_KEY_BG_CL);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_BDR_CL, BYTE_KEY_BDR_CL);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_FONT_FAM, BYTE_KEY_FONT_FAM);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_HZ_PD, BYTE_KEY_HZ_PD);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_KEY_POS, BYTE_KEY_KEY_POS);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_LBL_SZ, BYTE_KEY_LBL_SZ);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_MAX_HT, BYTE_KEY_MAX_HT);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_MAX_WD, BYTE_KEY_MAX_WD);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_NM_CL, BYTE_KEY_NM_CL);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_NM_POS, BYTE_KEY_NM_POS);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_NM_SZ, BYTE_KEY_NM_SZ);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_VT_PD, BYTE_KEY_VT_PD);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_CHECKBOX_CL, BYTE_KEY_LGD_CHECKBOX_CL);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_CHECKBOX_CHECK, BYTE_KEY_LGD_CHECKBOX_CHECK);
		LEGEND_STRINGS_TO_BYTES.put(AmiWebStyleConsts.PROPERTY_NAME_LGD_CHECKBOX_BDR_CL, BYTE_KEY_LGD_CHECKBOX_BDR_CL);
	}

	final private AmiWebDesktopPortlet desktop;
	private boolean isDataStale = true;

	public AmiWebChartRenderingLayer_Legend(AmiWebChartPlotPortlet parentPlot) {
		super(parentPlot);

		this.desktop = AmiWebUtils.getService(parentPlot.getManager()).getDesktop();
		getStylePeer().initStyle();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	WebMenuItem populateConfigMenu(String prefix) {
		BasicWebMenu menu = new BasicWebMenu(getDescription(), true);
		menu.setCssStyle("_fg=blue");
		menu.add(new BasicWebMenuLink("Edit Layer", true, prefix + "edit"));
		return menu;
	}

	@Override
	public void onAmiContextMenu(String id) {
		if ("edit".equals(id))
			getChart().getManager().showDialog("Edit Legend", new AmiWebChartEditRenderingLayerPortlet_Legend(getChart().generateConfig(), getPlot(), this));
	}

	public byte getKeyPosition() {
		return this.keyPosition;
	}

	public void setKeyPosition(byte keyPosition) {
		if (this.keyPosition == keyPosition)
			return;
		this.keyPosition = keyPosition;
		flagViewStale();
	}

	public Set<Integer> getReferencedSeries() {
		return series;
	}

	public void addSeries(int series) {
		if (!this.series.add(series))
			return;
		getPlot().getSeries(series).addListener(this);
		flagViewStale();
	}

	public void removeSeries(int series) {
		if (!this.series.remove(series))
			return;
		getPlot().getSeries(series).removeListener(this);
		flagViewStale();
	}

	public boolean hasSeries(int series) {
		return this.series.contains(series);
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		if (OH.eq(fontFamily, this.fontFamily))
			return;
		this.fontFamily = fontFamily;
		flagViewStale();
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		if (OH.eq(maxWidth, this.maxWidth))
			return;
		this.maxWidth = maxWidth;
		flagViewStale();
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		if (OH.eq(maxHeight, this.maxHeight))
			return;
		this.maxHeight = maxHeight;
		flagViewStale();
	}

	public int getHPadding() {
		return hPadding;
	}

	public void setHPadding(int hPadding) {
		if (OH.eq(hPadding, this.hPadding))
			return;
		this.hPadding = hPadding;
		flagViewStale();
	}

	public int getVPadding() {
		return vPadding;
	}

	public void setVPadding(int vPadding) {
		if (OH.eq(vPadding, this.vPadding))
			return;
		this.vPadding = vPadding;
		flagViewStale();
	}

	public byte getNamePosition() {
		return namePosition;
	}

	public void setNamePosition(byte namePosition) {
		if (OH.eq(namePosition, this.namePosition))
			return;
		this.namePosition = namePosition;
		flagViewStale();
	}
	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		if (OH.eq(this.borderColor, borderColor))
			return;
		this.borderColor = borderColor;
		flagViewStale();
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		if (OH.eq(this.backgroundColor, backgroundColor))
			return;
		this.backgroundColor = backgroundColor;
		flagViewStale();
	}

	public void setCheckboxColor(String checkboxColor) {
		if (OH.eq(this.checkboxColor, checkboxColor))
			return;
		this.checkboxColor = checkboxColor;
		flagViewStale();

	}

	public void setCheckboxBorderColor(String checkboxBorderColor) {
		if (OH.eq(this.checkboxBorderColor, checkboxBorderColor))
			return;
		this.checkboxBorderColor = checkboxBorderColor;
		flagViewStale();

	}

	public void setCheckboxCheckColor(String checkboxCheckColor) {
		if (OH.eq(this.checkboxCheckColor, checkboxCheckColor))
			return;
		this.checkboxCheckColor = checkboxCheckColor;
		flagViewStale();

	}

	public String getCheckboxColor() {
		return this.checkboxColor;
	}
	public String getCheckboxBorderColor() {
		return this.checkboxBorderColor;
	}
	public String getCheckboxCheckColor() {
		return this.checkboxCheckColor;
	}
	public String getNameColor() {
		return nameColor;
	}

	public void setNameColor(String nameColor) {
		if (OH.eq(this.nameColor, nameColor))
			return;
		this.nameColor = nameColor;
		flagViewStale();
	}
	public int getNameSize() {
		return nameSize;
	}

	public void setNameSize(int nameSize) {
		if (this.nameSize == nameSize)
			return;
		this.nameSize = nameSize;
		flagViewStale();
	}
	public int getLabelSize() {
		return labelSize;
	}

	public void setLabelSize(int labelSize) {
		if (this.labelSize == labelSize)
			return;
		this.labelSize = labelSize;
		flagViewStale();
	}

	public AmiWebOverrideValue<Integer> getDragXPos() {
		return this.dragXPos;
	}

	public AmiWebOverrideValue<Integer> getDragYPos() {
		return this.dragYPos;
	}

	public void clearPositionOverride() {
		if (this.dragXPos.isOverride())
			this.dragXPos.clearOverride();
		if (this.dragYPos.isOverride())
			this.dragYPos.clearOverride();
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("ser", CH.l(this.series));
		return r;
	}

	public void init(String alias, Map<String, Object> layer) {
		super.init(alias, layer);
		this.series.clear();
		if (layer.containsKey("ser"))
			this.series.addAll((List<Integer>) layer.get("ser"));
	}
	@Override
	public void buildJs(StringBuilder pjs, String varName) {
		if (!isDataStale)
			return;
		this.isDataStale = false;
		JsFunction func = new JsFunction(pjs, varName, "init");
		JsonBuilder json = func.startJson();

		json.startMap();
		if (keyPosition != KEY_HIDDEN) {
			int x;
			int y;
			int w = this.maxWidth;
			int h = this.maxHeight;

			// moved x y calcs to helper method
			int[] xy = getXY(w, h);
			x = xy[0];
			y = xy[1];

			json.addKeyValue("x", x);
			json.addKeyValue("y", y);
			json.addKeyValue("w", w);
			json.addKeyValue("h", h);
		}
		json.addKeyValue("pltWd", getPlot().getWidth());
		json.addKeyValue("pltHt", getPlot().getHeight());
		json.addKeyValue("opacity", getOpacity());
		json.addKeyValueQuoted("fontFamily", fontFamily);
		json.addKeyValue("hPadding", hPadding);
		json.addKeyValue("vPadding", vPadding);
		json.addKeyValueQuoted("name", getName());
		json.addKeyValue("namePosition", namePosition);
		json.addKeyValue("nameSize", nameSize);
		json.addKeyValueQuoted("nameColor", nameColor);
		json.addKeyValue("labelSize", labelSize);
		json.addKeyValueQuoted("borderColor", borderColor);
		json.addKeyValueQuoted("backgroundColor", backgroundColor);
		json.addKeyValueQuoted("checkboxColor", checkboxColor);
		json.addKeyValueQuoted("checkboxCheckColor", checkboxCheckColor);
		json.addKeyValueQuoted("checkboxBorderColor", checkboxBorderColor);
		json.addKeyValue("r", ColorHelper.getRFromHex(backgroundColor));
		json.addKeyValue("g", ColorHelper.getGFromHex(backgroundColor));
		json.addKeyValue("b", ColorHelper.getBFromHex(backgroundColor));
		json.addKey("series");
		json.startList();
		for (int i : this.series) {
			AmiWebChartSeries series = this.getChart().getSeries(i);
			if (series != null) {
				IndexedList<String, Grouping> allGroupings = series.getAllGroupings();
				allGroupings.sortByKeys(SH.COMPARATOR_CASEINSENSITIVE_STRING);
				for (Grouping grouping : series.getAllGroupings().values()) {
					json.startMap();
					json.addKeyValueQuoted("shapeColor", series.getLegendShapeColor(grouping));
					json.addKeyValueQuoted("border", series.getLegendShapeBorderColor(grouping));
					json.addKeyValueQuoted("shape", series.getLegendShape(grouping));
					int lineSize = OH.noNull(series.getLegendLineSize(grouping), 0);
					json.addKeyValue("lineSize", lineSize);
					json.addKeyValueQuoted("color", lineSize < 1 ? null : series.getLegendLineColor(grouping));
					json.addKeyValueQuoted("name", series.getLegendName(grouping));
					json.addKeyValue("dash", OH.noNull(series.getLegendLineDash(grouping), 0) > 0);
					json.addKeyValue("grouping", grouping.getId());
					json.addKeyValue("series", series.getId());
					json.addKeyValue("checked", checked.containsMulti(series.getId(), grouping.getId()));
					json.endMap();
				}
			}
		}
		json.endList();
		json.endMap();
		json.end();
		func.end();
		// draw legend
		new JsFunction(pjs, varName, "draw").end();
	}
	@Override
	public String getJsClassName() {
		return "AmiChartLayer_Legend";
	}

	@Override
	public void onDataChanged(AmiWebChartSeries series) {
		flagViewStale();
	}

	@Override
	public void onRemoved(AmiWebChartSeries amiWebChartSeries) {
		this.series.remove(amiWebChartSeries.getId());
	}
	public void onSizeChanged(int width, int height) {
		flagViewStale();
	}

	@Override
	public String getDmAliasDotName() {
		return null;
	}

	@Override
	public String getDmTableName() {
		return null;
	}

	@Override
	public void onStyleValueChanged(short key, Object old, Object value) {
		switch (key) {
			case AmiWebStyleConsts.CODE_LGD_BG_CL:
				setBackgroundColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_LGD_BDR_CL:
				setBorderColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_LGD_FONT_FAM:
				setFontFamily((String) value);
				break;
			case AmiWebStyleConsts.CODE_LGD_HZ_PD:
				clearPositionOverride();
				setHPadding(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_LGD_KEY_POS:
				clearPositionOverride();
				setKeyPosition(Caster_Byte.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_LGD_LBL_SZ:
				setLabelSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_LGD_MAX_HT:
				setMaxHeight(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_LGD_MAX_WD:
				setMaxWidth(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_LGD_NM_CL:
				setNameColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_LGD_NM_POS:
				setNamePosition(Caster_Byte.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_LGD_NM_SZ:
				setNameSize(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_LGD_VT_PD:
				clearPositionOverride();
				setVPadding(Caster_Integer.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_LGD_CHECKBOX_CHECK_CL:
				setCheckboxCheckColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_LGD_CHECKBOX_CL:
				setCheckboxColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_LGD_CHECKBOX_BDR_CL:
				setCheckboxBorderColor((String) value);
				break;
		}
	}

	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_RenderingLayer_Legend.TYPE_LAYER_LEGEND;
	}

	@Override
	public AmiWebChartRenderingLayer<AmiWebChartSeries> copy() {
		AmiWebChartRenderingLayer_Legend output = new AmiWebChartRenderingLayer_Legend(getPlot());
		output.init(getChart().getAmiLayoutFullAlias(), getConfiguration());
		return output;
	}
	@Override
	public String exportToText() {
		Map<String, Object> m = getConfiguration();
		return JSON_CONVERTER.objectToString(m);
	}

	@Override
	public void importFromText(String text, StringBuilder errorSink) {

	}

	@Override
	public AmiWebImageGenerator createImageGenerator() {
		return null;
	}

	@Override
	public List<AmiWebChartShape> getCurrentShapes() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void clearData() {
	}

	@Override
	public void onDataModelChanged(AmiWebDm datamodel) {
		for (int i : this.series) {
			AmiWebChartSeries series = this.getChart().getSeries(i);
			if (OH.eq(series.getDmAliasDotName(), datamodel.getAmiLayoutFullAliasDotId())) {
				isDataStale = true;
				break;
			}
		}
	}

	@Override
	public void flagDataStale() {
		super.flagDataStale();
		this.isDataStale = true;
	};

	@Override
	public void buildData(AmiWebDmTableSchema table, CalcFrameStack sf) {
	}

	@Override
	public void getUsedColors(Set<String> sink) {
	}

	public void onCheckbox(int seriesId, int grouping, boolean isChecked) {
		if (isChecked)
			checked.putMulti(seriesId, grouping);
		else
			checked.removeMultiAndKeyIfEmpty(seriesId, grouping);
		if (checked.isEmpty()) {
			for (int i : this.series) {
				AmiWebChartSeries series = this.getChart().getSeries(i);
				if (series != null)
					series.setSelectedGroupings(null);
			}
		} else {
			for (int i : this.series) {
				AmiWebChartSeries series = this.getChart().getSeries(i);
				if (series != null) {
					// get the selected checkboxes of this plot (series)
					Set<Integer> set = checked.get(series.getId());
					// update the grouping for this plot, then redraw
					series.setSelectedGroupings(set == null ? Collections.EMPTY_SET : set);
				}
			}
		}
	}

	@Override
	List<AmiWebChartShape> getShapesAtGroup(int groupId) {
		return null;
	}

	public int[] getXY(int w, int h) {
		int[] xy = new int[2];
		int x, y;
		if (this.dragXPos.isOverride() && this.dragYPos.isOverride()) {
			x = dragXPos.getOverride();
			y = dragYPos.getOverride();
		} else {
			switch (this.keyPosition & (KEY_TOP | KEY_BOTTOM)) {
				case KEY_TOP:
					y = vPadding;
					break;
				case KEY_BOTTOM:
					y = getPlot().getHeight() - getVPadding() - h;
					break;
				default:
					y = (getPlot().getHeight() - h) / 2;
					break;
			}
			switch (this.keyPosition & (KEY_LEFT | KEY_RIGHT)) {
				case KEY_LEFT:
					x = hPadding;
					break;
				case KEY_RIGHT:
					x = getPlot().getWidth() - getHPadding() - w;
					break;
				default:
					x = (getPlot().getWidth() - w) / 2;
					break;
			}
		}
		xy[0] = x;
		xy[1] = y;
		return xy;
	}

	// is the legend entry checked?
	public boolean checkedContainsMulti(int seriesId, int groupId) {
		return checked.containsMulti(seriesId, groupId);
	}

}