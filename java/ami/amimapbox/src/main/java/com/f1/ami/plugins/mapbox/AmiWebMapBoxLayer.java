package com.f1.ami.plugins.mapbox;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebDomObjectsManager;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebFormulasListener;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebMapBoxLayer implements AmiWebDomObject, AmiWebFormulasListener {
	private static final Logger log = LH.get();

	private static final Row[] EMPTY_ROW_ARRAY = new Row[0];

	private DerivedCellCalculator latitudeCalc;
	private DerivedCellCalculator longitudeCalc;
	private DerivedCellCalculator sizeCalc;
	private DerivedCellCalculator fillColorCalc;
	private DerivedCellCalculator borderColorCalc;
	private DerivedCellCalculator opacityCalc;
	private DerivedCellCalculator tooltipCalc;
	private DerivedCellCalculator labelCalc;
	private DerivedCellCalculator labelFontFamCalc;
	private DerivedCellCalculator labelFontSzCalc;
	private DerivedCellCalculator labelFontClCalc;
	private DerivedCellCalculator labelPosCalc;

	final private AmiWebFormula latitudeFormula;
	final private AmiWebFormula longitudeFormula;
	final private AmiWebFormula sizeFormula;
	final private AmiWebFormula fillColorFormula;
	final private AmiWebFormula borderColorFormula;
	final private AmiWebFormula opacityFormula;
	final private AmiWebFormula tooltipFormula;
	final private AmiWebFormula labelFormula;
	final private AmiWebFormula labelFontFamilyFormula;
	final private AmiWebFormula labelFontSizeFormula;
	final private AmiWebFormula labelFontColorFormula;
	final private AmiWebFormula labelPositionFormula;
	private Integer labelLimit;
	private String title;
	private String dmAliasDotName;
	private String dmTableName;
	final private int id;
	final private AmiWebMapBoxPanel panel;
	private int zPosition;
	private String ari;
	final private AmiWebFormulasImpl formulas;

	public AmiWebMapBoxLayer(AmiWebMapBoxPanel panel, int id) {
		this.formulas = new AmiWebFormulasImpl(this);
		latitudeFormula = this.formulas.addFormula("latitude", Number.class);
		longitudeFormula = this.formulas.addFormula("longitude", Number.class);
		sizeFormula = this.formulas.addFormula("size", Number.class);
		fillColorFormula = this.formulas.addFormula("fillColor", String.class);
		borderColorFormula = this.formulas.addFormula("borderColor", String.class);
		opacityFormula = this.formulas.addFormula("opacity", Number.class);
		tooltipFormula = this.formulas.addFormula("tooltip", Object.class);
		labelFormula = this.formulas.addFormula("label", String.class);
		labelFontFamilyFormula = this.formulas.addFormula("labelFontFamily", String.class);
		labelFontSizeFormula = this.formulas.addFormula("labelSize", Number.class);
		labelFontColorFormula = this.formulas.addFormula("labelColor", String.class);
		labelPositionFormula = this.formulas.addFormula("labelPosition", Number.class);
		this.formulas.addFormulasListener(this);
		this.panel = panel;
		this.id = id;
		this.needsCalc = true;
		updateAri();
	}
	public AmiWebMapBoxLayer(AmiWebMapBoxPanel panel, int id, String dmAliasDotName, String dmTableName) {
		this(panel, id);
		this.dmAliasDotName = dmAliasDotName;
		this.dmTableName = dmTableName;
	}

	public void init(String alias, Map<String, Object> values) {
		this.latitudeFormula.initFormula(CH.getOrThrow(Caster_String.INSTANCE, values, "lat"));
		this.longitudeFormula.initFormula(CH.getOrThrow(Caster_String.INSTANCE, values, "lon"));
		this.sizeFormula.initFormula(CH.getOrThrow(Caster_String.INSTANCE, values, "siz"));
		this.fillColorFormula.initFormula(CH.getOrThrow(Caster_String.INSTANCE, values, "flc"));
		this.borderColorFormula.initFormula(CH.getOrThrow(Caster_String.INSTANCE, values, "brc"));
		this.opacityFormula.initFormula(CH.getOrThrow(Caster_String.INSTANCE, values, "opa"));
		this.tooltipFormula.initFormula(CH.getOrThrow(Caster_String.INSTANCE, values, "tip"));
		this.title = CH.getOrThrow(Caster_String.INSTANCE, values, "ttl");
		this.dmAliasDotName = AmiWebUtils.getFullAlias(alias, CH.getOrThrow(Caster_String.INSTANCE, values, "dmadn"));
		this.dmTableName = CH.getOrThrow(Caster_String.INSTANCE, values, "dtb");
		this.zPosition = CH.getOrThrow(Caster_Integer.INSTANCE, values, "zps");
		this.labelFormula.initFormula(CH.getOr(Caster_String.INSTANCE, values, "lbl", null));
		this.labelLimit = CH.getOr(Caster_Integer.INSTANCE, values, "lbllmt", AmiWebMapBoxLayerSettingsPortlet.DEFAULT_LABEL_LIMIT);
		this.labelFontFamilyFormula.initFormula(CH.getOr(Caster_String.INSTANCE, values, "lblfm", null));
		this.labelFontSizeFormula.initFormula(CH.getOr(Caster_String.INSTANCE, values, "lblsz", null));
		this.labelFontColorFormula.initFormula(CH.getOr(Caster_String.INSTANCE, values, "lblfcl", null));
		this.labelPositionFormula.initFormula(CH.getOr(Caster_String.INSTANCE, values, "lblps", null));
	}
	public Map<String, Object> getConfiguration(String alias) {
		Map<String, Object> r = new HashMap<String, Object>();
		r.put("id", this.id);
		r.put("lat", this.latitudeFormula.getFormulaConfig());
		r.put("lon", this.longitudeFormula.getFormulaConfig());
		r.put("siz", this.sizeFormula.getFormulaConfig());
		r.put("flc", this.fillColorFormula.getFormulaConfig());
		r.put("brc", this.borderColorFormula.getFormulaConfig());
		r.put("opa", this.opacityFormula.getFormulaConfig());
		r.put("tip", this.tooltipFormula.getFormulaConfig());
		r.put("ttl", this.title);
		r.put("dmadn", AmiWebUtils.getRelativeAlias(alias, this.dmAliasDotName));
		r.put("dtb", this.dmTableName);
		r.put("zps", this.zPosition);
		r.put("lbl", this.labelFormula.getFormulaConfig());
		r.put("lbllmt", this.labelLimit);
		r.put("lblfm", this.labelFontFamilyFormula.getFormulaConfig());
		r.put("lblsz", this.labelFontSizeFormula.getFormulaConfig());
		r.put("lblfcl", this.labelFontColorFormula.getFormulaConfig());
		r.put("lblps", this.labelPositionFormula.getFormulaConfig());
		return r;
	}
	public AmiWebFormula getLabelFormula() {
		return labelFormula;
	}
	//	public void setLabelFormula(String labelFormula, boolean noThrow) {
	//		this.labelCalc = parse("Label", labelFormula, String.class, false, noThrow);
	//		this.labelFormula = labelFormula;
	//	}
	public Integer getLabelLimit() {
		return labelLimit;
	}
	public void setLabelLimit(Integer labelLimit) {
		this.labelLimit = labelLimit;
	}
	public AmiWebFormula getLatitudeFormula() {
		return latitudeFormula;
	}
	//	public void setLatitudeFormula(String latitudeFormula, boolean noThrow) {
	//		this.latitudeCalc = parse("Latitude", latitudeFormula, Number.class, true, noThrow);
	//		this.latitudeFormula = latitudeFormula;
	//	}
	public AmiWebFormula getLongitudeFormula() {
		return longitudeFormula;
	}
	//	public void setLongitudeFormula(String longitudeFormula, boolean noThrow) {
	//		this.longitudeCalc = parse("Longitude", longitudeFormula, Number.class, true, noThrow);
	//		this.longitudeFormula = longitudeFormula;
	//	}
	public AmiWebFormula getSizeFormula() {
		return sizeFormula;
	}
	//	public void setSizeFormula(String sizeFormula, boolean noThrow) {
	//		this.sizeCalc = parse("Size", sizeFormula, Number.class, true, noThrow);
	//		this.sizeFormula = sizeFormula;
	//	}
	public AmiWebFormula getFillColorFormula() {
		return fillColorFormula;
	}
	//	public void setFillColorFormula(String fillColorFormula, boolean noThrow) {
	//		this.fillColorCalc = parse("Fill Color", fillColorFormula, String.class, false, noThrow);
	//		this.fillColorFormula = fillColorFormula;
	//	}
	public AmiWebFormula getBorderColorFormula() {
		return borderColorFormula;
	}
	//	public void setBorderColorFormula(String borderColorFormula, boolean noThrow) {
	//		this.borderColorCalc = parse("Fill Color", borderColorFormula, String.class, false, noThrow);
	//		this.borderColorFormula = borderColorFormula;
	//	}
	public AmiWebFormula getOpacityFormula() {
		return opacityFormula;
	}
	//	public void setOpacityFormula(String opacityFormula, boolean noThrow) {
	//		this.opacityCalc = parse("Opacity", opacityFormula, Number.class, true, noThrow);
	//		this.opacityFormula = opacityFormula;
	//	}
	public AmiWebFormula getTooltipFormula() {
		return tooltipFormula;
	}
	//	public void setTooltipFormula(String tooltipFormula, boolean noThrow) {
	//		this.tooltipCalc = parse("Tooltip", tooltipFormula, String.class, false, noThrow);
	//		this.tooltipFormula = tooltipFormula;
	//	}
	public AmiWebFormula getLabelFontFamilyFormula() {
		return labelFontFamilyFormula;
	}
	//	public void setLabelFontFamilyFormula(String labelFontFamilyFormula, boolean noThrow) {
	//		this.labelFontFamCalc = parse("Label Font Family", labelFontFamilyFormula, String.class, false, noThrow);
	//		this.labelFontFamilyFormula = labelFontFamilyFormula;
	//	}
	public AmiWebFormula getLabelFontSizeFormula() {
		return labelFontSizeFormula;
	}
	//	public void setLabelFontSizeFormula(String labelFontSizeFormula, boolean noThrow) {
	//		this.labelFontSzCalc = parse("Label Font Size", labelFontSizeFormula, Number.class, false, noThrow);
	//		this.labelFontSizeFormula = labelFontSizeFormula;
	//	}
	public AmiWebFormula getLabelFontColorFormula() {
		return labelFontColorFormula;
	}
	//	public void setLabelFontColorFormula(String labelFontColorFormula, boolean noThrow) {
	//		this.labelFontClCalc = parse("Label Font Color", labelFontColorFormula, String.class, false, noThrow);
	//		this.labelFontColorFormula = labelFontColorFormula;
	//	}
	public AmiWebFormula getLabelPositionFormula() {
		return labelPositionFormula;
	}
	//	public void setLabelPositionFormula(String labelPositionFormula, boolean noThrow) {
	//		this.labelPosCalc = parse("Label Font Position", labelPositionFormula, String.class, false, noThrow);
	//		this.labelPositionFormula = labelPositionFormula;
	//	}
	public String getDmAliasDotName() {
		return dmAliasDotName;
	}
	public void setDmAliasDotName(String aliasDotName) {
		this.needsCalc = true;
		this.dmAliasDotName = aliasDotName;
	}
	public String getDmTableName() {
		return dmTableName;
	}
	public void setDmTableName(String dmTableName) {
		this.needsCalc = true;
		this.dmTableName = dmTableName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getId() {
		return id;
	}
	public AmiWebMapBoxPanel getPanel() {
		return panel;
	}

	public AmiWebDmTableSchema getDm() {
		AmiWebDm t = this.panel.getService().getDmManager().getDmByAliasDotName(this.dmAliasDotName);
		if (t == null)
			return null;
		return t.getResponseOutSchema().getTable(this.dmTableName);
	}
	//	private DerivedCellCalculator parse(String label, String formula, Class<?> retType, boolean required, boolean noThrow) {
	//		this.needsCalc = true;
	//		AmiWebDmTableSchema dm = getDm();
	//		if (dm == null) {
	//			if (noThrow)
	//				return null;
	//			else
	//				throw new RuntimeException("Error with " + label + ": Invalid dm: " + this.dmAliasDotName + "->" + this.dmTableName);
	//		}
	//		try {
	//			if (SH.isnt(formula)) {
	//				if (!required || noThrow)
	//					return null;
	//				else
	//					throw new RuntimeException("Error with " + label + ": Required Field");
	//			}
	//			com.f1.base.Types variables = dm.getClassTypes();
	//			DerivedCellCalculator r = panel.getScriptManager().toCalc(formula, variables);
	//			if (!retType.isAssignableFrom(r.getReturnType())) {
	//				if (noThrow)
	//					return null;
	//				else
	//					throw new RuntimeException("Error with " + label + ": Must return a " + retType.getSimpleName() + ", not " + r.getReturnType().getSimpleName());
	//			}
	//			return DerivedHelper.reduceConsts(r);
	//		} catch (ExpressionParserException e) {
	//			throw new RuntimeException("Error with " + label + ": " + e.getMessage(), e);
	//		}
	//	}
	public int getzPosition() {
		return zPosition;
	}
	public String getDescription() {
		if (SH.is(title))
			return "Layer #" + (this.zPosition + 1) + " - " + title;
		return "Layer #" + (this.zPosition + 1);
	}
	public void setzPosition(int zPosition) {
		this.zPosition = zPosition;
	}

	private boolean needsCalc = true;
	private int dataSize;
	private Table data;

	private double[] data_lats = OH.EMPTY_DOUBLE_ARRAY;
	private double[] data_lons = OH.EMPTY_DOUBLE_ARRAY;
	private double[] data_opacities = OH.EMPTY_DOUBLE_ARRAY;
	private long[] data_sizes = OH.EMPTY_LONG_ARRAY;
	private Row[] data_rows = EMPTY_ROW_ARRAY;
	private String[] data_borderColors = OH.EMPTY_STRING_ARRAY;
	private String[] data_colors = OH.EMPTY_STRING_ARRAY;
	private String[] data_labels = OH.EMPTY_STRING_ARRAY;
	private String[] data_label_font_families = OH.EMPTY_STRING_ARRAY;
	private int[] data_label_fontsizes = OH.EMPTY_INT_ARRAY;
	private String[] data_label_fontcolors = OH.EMPTY_STRING_ARRAY;
	private String[] data_label_positions = OH.EMPTY_STRING_ARRAY;

	public void setData(Table data) {
		this.data = data;
		this.needsCalc = true;
	}
	public void ensureCalced() {
		if (!needsCalc)
			return;
		this.needsCalc = false;
		//		this.setLatitudeFormula(this.getLatitudeFormula(), false);
		//		this.setLongitudeFormula(this.getLongitudeFormula(), false);
		//		this.setSizeFormula(this.getSizeFormula(), false);
		//		this.setFillColorFormula(this.getFillColorFormula(), false);
		//		this.setBorderColorFormula(this.getBorderColorFormula(), false);
		//		this.setOpacityFormula(this.getOpacityFormula(), false);
		//		this.setTooltipFormula(this.getTooltipFormula(), false);
		//		this.setLabelFormula(this.getLabelFormula(), false);
		//		this.setLabelFontFamilyFormula(this.getLabelFontFamilyFormula(), false);
		//		this.setLabelFontSizeFormula(this.getLabelFontSizeFormula(), false);
		//		this.setLabelFontColorFormula(this.labelFontColorFormula, false);
		//		this.setLabelPositionFormula(this.labelPositionFormula, false);

		if (this.data == null || this.data.getSize() == 0 || this.latitudeCalc == null || this.longitudeCalc == null || this.sizeCalc == null || this.opacityCalc == null) {
			this.dataSize = 0;
			data_lats = OH.EMPTY_DOUBLE_ARRAY;
			data_lons = OH.EMPTY_DOUBLE_ARRAY;
			data_opacities = OH.EMPTY_DOUBLE_ARRAY;
			data_sizes = OH.EMPTY_LONG_ARRAY;
			data_borderColors = OH.EMPTY_STRING_ARRAY;
			data_colors = OH.EMPTY_STRING_ARRAY;
			data_labels = OH.EMPTY_STRING_ARRAY;
			data_label_font_families = OH.EMPTY_STRING_ARRAY;
			data_label_fontsizes = OH.EMPTY_INT_ARRAY;
			data_label_fontcolors = OH.EMPTY_STRING_ARRAY;
			data_label_positions = OH.EMPTY_STRING_ARRAY;
			return;
		}
		int cnt = this.data.getSize();
		double[] lats = new double[cnt];
		double[] lons = new double[cnt];
		double[] opacities = new double[cnt];
		long[] sizes = new long[cnt];
		String[] borderColors = new String[cnt];
		String[] colors = new String[cnt];
		String[] labels = new String[cnt];

		// OPTIMIZATION: only assign size upto labellimit
		int sizeResolved = cnt;
		if (this.getLabelLimit() < sizeResolved)
			sizeResolved = this.getLabelLimit();
		String[] labelFms = new String[sizeResolved];
		int[] labelFszs = new int[sizeResolved];
		String[] labelFCls = new String[sizeResolved];
		String[] labelPss = new String[sizeResolved];

		Row[] rows = new Row[cnt];
		int size = 0;
		ReusableCalcFrameStack sf = new ReusableCalcFrameStack(this.panel.getStackFrame());
		for (Row row : this.data.getRows()) {
			sf.reset(row);
			Number lat = (Number) this.latitudeCalc.get(sf);
			Number lon = (Number) this.longitudeCalc.get(sf);
			Number opc = (Number) this.opacityCalc.get(sf);
			Number sze = (Number) this.sizeCalc.get(sf);
			String brd = (String) (this.borderColorCalc == null ? null : this.borderColorCalc.get(sf));
			String fll = (String) (this.fillColorCalc == null ? null : this.fillColorCalc.get(sf));
			String lbl = (String) (this.labelCalc == null ? null : this.labelCalc.get(sf));
			String lfm = (String) (this.labelFontFamCalc == null ? "Arial" : this.labelFontFamCalc.get(sf));
			Number lfsz = (Number) (this.labelFontSzCalc == null ? AmiWebMapBoxLayerSettingsPortlet.DEFAULT_LABEL_FONT_SIZE : this.labelFontSzCalc.get(sf));
			String lfcl = (String) (this.labelFontClCalc == null ? "#ffffff" : this.labelFontClCalc.get(sf));
			String lps = (String) (this.labelPosCalc == null ? "bottom" : this.labelPosCalc.get(sf));
			if (lat == null || lon == null || sze == null || opc == null)
				continue;
			final double dLat = lat.doubleValue();
			final double dLon = lon.doubleValue();
			final double dOpc = opc.doubleValue();
			final long lSze = sze.longValue();
			if (lSze < 1)
				continue;
			if (MH.isntNumber(dLat))
				continue;
			if (MH.isntNumber(dLon))
				continue;
			if (MH.isntNumber(dOpc))
				continue;
			if (dLat == 0d && dLon == 0d)
				continue;
			if (MH.isntNumber(lfsz))
				lfsz = AmiWebMapBoxLayerSettingsPortlet.DEFAULT_LABEL_FONT_SIZE;
			lats[size] = dLat;
			lons[size] = dLon;
			opacities[size] = dOpc;
			sizes[size] = lSze;
			colors[size] = fll;
			borderColors[size] = brd;
			labels[size] = lbl;

			if (size < sizeResolved) {
				labelFms[size] = lfm;
				labelFszs[size] = lfsz.intValue();
				labelFCls[size] = lfcl;
				labelPss[size] = lps;
			}

			rows[size] = row;
			size++;
		}
		this.data_lats = lats;
		this.data_lons = lons;
		this.data_opacities = opacities;
		this.data_sizes = sizes;
		this.data_borderColors = borderColors;
		this.data_colors = colors;
		this.data_labels = labels;
		this.data_rows = rows;
		this.dataSize = size;
		this.data_label_font_families = labelFms;
		this.data_label_fontsizes = labelFszs;
		this.data_label_fontcolors = labelFCls;
		this.data_label_positions = labelPss;
	}
	public double[] getDataLatitudes() {
		ensureCalced();
		return data_lats;
	}
	public double[] getDataLongitudes() {
		ensureCalced();
		return data_lons;
	}
	public double[] getDataOpacities() {
		ensureCalced();
		return data_opacities;
	}
	public long[] getDataSizes() {
		ensureCalced();
		return data_sizes;
	}
	public String[] getDataBorderColors() {
		ensureCalced();
		return data_borderColors;
	}
	public String[] getDataFillColors() {
		ensureCalced();
		return data_colors;
	}
	public String[] getDataLabels() {
		ensureCalced();
		return data_labels;
	}
	public int getDataSize() {
		ensureCalced();
		return dataSize;
	}
	public String[] getDataLabelFontFamilies() {
		ensureCalced();
		return data_label_font_families;
	}
	public int[] getDataLabelFontSizes() {
		ensureCalced();
		return data_label_fontsizes;
	}
	public String[] getDataLabelFontColors() {
		ensureCalced();
		return data_label_fontcolors;
	}
	public String[] getDataLabelPositions() {
		ensureCalced();
		return data_label_positions;
	}
	public Row getRowAt(int position) {
		if (this.data_rows == null || position >= this.dataSize)
			return null;
		return this.data_rows[position];
	}

	public Table getData() {
		return this.data;
	}
	public String getTooltipAt(int position) {
		if (tooltipCalc == null)
			return null;
		Row row = getRowAt(position);
		if (row == null)
			return null;
		try {
			return (String) this.tooltipCalc.get(new ReusableCalcFrameStack(panel.getStackFrame(), row));
		} catch (Exception e) {
			LH.warning(log, "Error processing tooltip for ", row);
			return null;
		}
	}
	//	public String getLabelAt(int position) {
	//		if (labelCalc == null)
	//			return null;
	//		Row row = getRowAt(position);
	//		if (row == null)
	//			return null;
	//		try {
	//			return (String) this.labelCalc.get(row);
	//		} catch (Exception e) {
	//			LH.warning(log, "Error processing label for ", row);
	//			return null;
	//		}
	//	}
	public void getDependencies(Set<Object> r) {
		DerivedHelper.getDependencyIds(this.latitudeCalc, r);
		DerivedHelper.getDependencyIds(this.longitudeCalc, r);
		DerivedHelper.getDependencyIds(this.sizeCalc, r);
		DerivedHelper.getDependencyIds(this.fillColorCalc, r);
		DerivedHelper.getDependencyIds(this.borderColorCalc, r);
		DerivedHelper.getDependencyIds(this.opacityCalc, r);
		DerivedHelper.getDependencyIds(this.tooltipCalc, r);
		DerivedHelper.getDependencyIds(this.labelCalc, r);
		DerivedHelper.getDependencyIds(this.labelFontFamCalc, r);
		DerivedHelper.getDependencyIds(this.labelFontSzCalc, r);
		DerivedHelper.getDependencyIds(this.labelFontClCalc, r);
		DerivedHelper.getDependencyIds(this.labelPosCalc, r);
	}
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		if (OH.ne(this.dmAliasDotName, oldAliasDotName))
			this.dmAliasDotName = dm.getAmiLayoutFullAliasDotId();
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
	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.panel.getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.panel.getAmiLayoutFullAliasDotId() + "?0,0+" + getDomLabel();
		this.ari = AmiWebDomObject.ARI_TYPE_CHART_LAYER + ":" + this.amiLayoutFullAliasDotId;
		if (OH.ne(this.ari, oldAri)) {
			this.panel.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
		}
	}
	@Override
	public String getAriType() {
		return ARI_TYPE_CHART_LAYER;
	}
	@Override
	public String getDomLabel() {
		return SH.toString(id);
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}
	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.panel;
	}
	@Override
	public Class<?> getDomClassType() {
		return this.getClass();
	}
	@Override
	public Object getDomValue() {
		return this;
	}
	@Override
	public boolean isTransient() {
		return this.panel.isTransient();
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}
	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
	}

	private boolean isManagedByDomManager = false;

	private String amiLayoutFullAlias;

	private String amiLayoutFullAliasDotId;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.panel.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;

		}
	}

	@Override
	public void removeFromDomManager() {
		AmiWebDomObjectsManager domObjectsManager = this.panel.getService().getDomObjectsManager();
		for (AmiWebDomObject i : this.getChildDomObjects())
			domObjectsManager.fireRemoved(i);
		domObjectsManager.fireRemoved(this);

		if (this.isManagedByDomManager == true) {
			//Remove DomValues First

			//Remove Self
			AmiWebService service = this.panel.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
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
	public AmiWebFormulas getFormulas() {
		return this.formulas;
	}
	@Override
	public AmiWebService getService() {
		return panel.getService();
	}
	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.panel.getFormulaVarTypes(f);
	}
	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		if (formula == latitudeFormula)
			this.latitudeCalc = nuw;
		if (formula == longitudeFormula)
			longitudeCalc = nuw;
		if (formula == sizeFormula)
			sizeCalc = nuw;
		if (formula == fillColorFormula)
			fillColorCalc = nuw;
		if (formula == borderColorFormula)
			borderColorCalc = nuw;
		if (formula == opacityFormula)
			opacityCalc = nuw;
		if (formula == tooltipFormula)
			tooltipCalc = nuw;
		if (formula == labelFormula)
			labelCalc = nuw;
		if (formula == labelFontFamilyFormula)
			labelFontFamCalc = nuw;
		if (formula == labelFontSizeFormula)
			labelFontSzCalc = nuw;
		if (formula == labelFontColorFormula)
			labelFontClCalc = nuw;
		if (formula == labelPositionFormula)
			labelPosCalc = nuw;
		this.needsCalc = true;
		if (this.panel.isInitDone())
			this.panel.flagFormulasChanged();
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
