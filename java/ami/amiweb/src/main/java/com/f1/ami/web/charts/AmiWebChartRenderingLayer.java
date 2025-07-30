package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebAbstractPortlet;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.charts.AmiWebChartSeries.Grouping;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.AmiWebStyledPortletPeer;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiWebChartRenderingLayer<T extends AmiWebChartSeries> implements AmiWebStyledPortlet, AmiWebChartSeriesContainer<T> {

	public static final char TYPE_BAR = 'B';
	public static final char TYPE_SCATTER = 'S';
	private int id;
	private AmiWebChartPlotPortlet parentPlot;
	final private AmiWebChartGridPortlet chart;
	private int zPosition;
	private String name;
	private int xAxisId = -1;
	private int yAxisId = -1;
	private int opacity = 100;
	private T series;
	private final AmiWebStyledPortletPeer stylePeer;
	private final AmiWebDesktopPortlet desktop;

	protected final static ObjectToJsonConverter JSON_CONVERTER = new ObjectToJsonConverter();
	static {
		JSON_CONVERTER.setIgnoreUnconvertable(true);
		JSON_CONVERTER.setStrictValidation(true);
		JSON_CONVERTER.setTreatNanAsNull(false);
		JSON_CONVERTER.setCompactMode(false);
	}

	public AmiWebChartRenderingLayer(AmiWebChartPlotPortlet parentPlot) {
		this.parentPlot = parentPlot;
		this.chart = this.parentPlot.getChart();

		this.desktop = AmiWebUtils.getService(parentPlot.getManager()).getDesktop();
		this.stylePeer = new AmiWebStyledPortletPeer(this, this.desktop.getService());
		this.updateAri();
	}

	private String dmAliasDotName;
	private String dmTableName;
	private AmiWebImageGenerator imageGenerator;

	public void setDm(String dmAliasDotName, String dmTableName) {
		if (OH.eq(this.dmAliasDotName, dmAliasDotName) && OH.eq(this.dmTableName, dmTableName))
			return;
		String oldDmId = this.dmAliasDotName;
		String oldDmTableName = this.dmTableName;
		this.dmAliasDotName = dmAliasDotName;
		this.dmTableName = dmTableName;
		if (dmAliasDotName == null && dmTableName == null)
			this.chart.onUsedDmRemoved(oldDmId, oldDmTableName, this);
		else
			this.chart.onUsedDmChanged(oldDmId, oldDmTableName, this);

		if (this.series != null)
			this.series.setDatamodel(dmAliasDotName, dmTableName);
	}

	public int getId() {
		return id;
	}

	public AmiWebChartPlotPortlet getPlot() {
		return parentPlot;
	}

	public AmiWebChartGridPortlet getChart() {
		return this.chart;
	}

	public void setParentPlot(AmiWebChartPlotPortlet parentPlot) {
		this.parentPlot = parentPlot;
	}

	public String getDescription() {
		if (SH.is(getName()))
			return "Layer #" + (this.getzPosition() + 1) + " - " + getName();
		else if (SH.is(this.dmTableName))
			return "Layer #" + (this.getzPosition() + 1) + " - " + getType() + " of " + this.dmTableName;
		else
			return "Layer #" + (this.getzPosition() + 1) + " - " + getType();
	}

	public AmiWebChartAxisPortlet getXAxis() {
		return xAxisId == -1 ? null : this.chart.getAxisById(xAxisId);
	}

	public void setXAxis(int xAxisId) {
		if (this.xAxisId == xAxisId)
			return;
		if (this.xAxisId != -1)
			getXAxis().onDataChanged();
		this.xAxisId = xAxisId;
		if (this.xAxisId != -1)
			getXAxis().onDataChanged();
		flagDataStale();
	}
	public int getXAxisId() {
		return this.xAxisId;
	}
	public int getYAxisId() {
		return this.yAxisId;
	}

	public AmiWebChartAxisPortlet getYAxis() {
		return yAxisId == -1 ? null : this.chart.getAxisById(yAxisId);
	}

	public void setYAxis(int yAxisId) {
		if (this.yAxisId == yAxisId)
			return;
		if (this.yAxisId != -1)
			getYAxis().onDataChanged();
		this.yAxisId = yAxisId;
		if (this.yAxisId != -1)
			getYAxis().onDataChanged();
		flagDataStale();
	}
	abstract public String getType();

	public void setId(int id) {
		this.id = id;
	}

	public Map<String, Object> getConfiguration() {
		String alias = getChart().getAmiLayoutFullAlias();
		Map<String, Object> r = new HashMap<String, Object>();
		CH.putNoNull(r, "dmadn", AmiWebUtils.getRelativeAlias(alias, dmAliasDotName));
		CH.putNoNull(r, "dmTableName", dmTableName);
		r.put("id", id);
		r.put("type", getType());
		r.put("name", name);
		CH.putExcept(r, "xAxisId", xAxisId, -1);
		CH.putExcept(r, "yAxisId", yAxisId, -1);
		r.put("opac", opacity);
		r.put("amiStyle", this.stylePeer.getStyleConfiguration());
		return r;
	}
	public void init(String alias, Map<String, Object> layer) {
		this.id = CH.getOrThrow(Caster_Integer.PRIMITIVE, layer, "id");
		this.chart.registerUsedId(this.id);
		this.name = CH.getOr(Caster_String.INSTANCE, layer, "name", "");
		this.xAxisId = CH.getOr(Caster_Integer.PRIMITIVE, layer, "xAxisId", -1);
		this.yAxisId = CH.getOr(Caster_Integer.PRIMITIVE, layer, "yAxisId", -1);
		this.opacity = CH.getOr(Caster_Integer.PRIMITIVE, layer, "opac", 100);
		this.dmAliasDotName = AmiWebUtils.getFullAlias(alias, CH.getOr(Caster_String.INSTANCE, layer, "dmadn", null));
		this.dmTableName = CH.getOr(Caster_String.INSTANCE, layer, "dmTableName", null);
		this.stylePeer.initStyle((Map<String, Object>) layer.get("amiStyle"));
		updateAri();
	}

	abstract public String getJsClassName();

	public int getzPosition() {
		return zPosition;
	}

	public void setzPosition(int zPosition) {
		this.zPosition = zPosition;
	}

	public void flagNeedsRepaint() {
		this.parentPlot.needsRepaint();
	}
	public void flagDataStale() {
		this.imageGenerator = null;
		flagNeedsRepaint();
	}
	public void flagViewStale() {
		flagDataStale();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (OH.eq(this.name, name))
			return;
		this.name = name;
		AmiWebChartSeries s = this.getSeries();
		if (s != null)
			s.setName(name);
		updateAri();
	}

	@Deprecated
	abstract WebMenuItem populateConfigMenu(String prefix);

	@Deprecated
	abstract public void onAmiContextMenu(String id);

	public void setOpacity(int opacity) {
		if (this.opacity == opacity)
			return;
		this.opacity = opacity;
		this.imageGenerator = null;
		this.getPlot().flagViewStale();
	}
	public int getOpacity() {
		return this.opacity;
	}

	public void onSizeChanged(int width, int height) {
		this.zoomChanged = true;
	}

	public void onLinkingChanged(AmiWebDmLink link) {
		getPlot().flagConfigStale();
		getPlot().clearSelected();
		flagDataStale();
	}

	public void getUsedColors(Set<String> sink) {
		this.series.getUsedColors(sink);
	}

	@Override
	final public T getSeries() {
		return this.series;
	}
	@Override
	public void setSeries(T series) {
		CalcFrameStack sf = getPlot().getChart().getStackFrame();
		if (series.getId() < 1)
			series.setId(getChart().getNextId());
		if (this.series != null && OH.eq(this.series.getId(), series.getId())) {
			if (this.series != series)
				throw new IllegalArgumentException();
		}
		this.series = series;
		if (getDm() != null)
			series.buildData(getDataModelSchema(), sf);
		AmiWebChartAxisPortlet xaxis = getXAxis();
		if (xaxis != null)
			xaxis.onDataChanged();
		AmiWebChartAxisPortlet yaxis = getYAxis();
		if (yaxis != null)
			yaxis.onDataChanged();
		flagDataStale();
	}

	public void setSeries(T series, Table table) {
		CalcFrameStack sf = getPlot().getChart().getStackFrame();
		if (series.getId() < 1)
			series.setId(getChart().getNextId());
		if (this.series != null && OH.eq(this.series.getId(), series.getId())) {
			if (this.series != series)
				throw new IllegalArgumentException();
		}
		this.series = series;
		series.buildData(table, sf);
		AmiWebChartAxisPortlet xaxis = getXAxis();
		if (xaxis != null)
			xaxis.onDataChanged();
		AmiWebChartAxisPortlet yaxis = getYAxis();
		if (yaxis != null)
			yaxis.onDataChanged();
		flagDataStale();
	}

	final public AmiWebDmTableSchema getDataModelSchema() {
		AmiWebDm dm = getDm();
		if (dm == null)
			return null;
		return dm.getResponseOutSchema().getTable(this.getDmTableName());
	}

	final public AmiWebDm getDm() {
		return getPlot().getService().getDmManager().getDmByAliasDotName(this.getDmAliasDotName());
	}

	public String getDmTableName() {
		return this.dmTableName;
	}
	public String getDmAliasDotName() {
		return this.dmAliasDotName;
	}

	public AmiWebStyledPortletPeer getStylePeer() {
		return stylePeer;
	}

	abstract public AmiWebChartRenderingLayer<T> copy();
	abstract public String exportToText();
	abstract public void importFromText(String text, StringBuilder errorSink);

	final public AmiWebImageGenerator getCurrentImageGenerator() {
		if (this.imageGenerator == null) {
			this.imageGenerator = createImageGenerator();
		} else if (this.zoomChanged) {
			this.imageGenerator = this.updateForZoom(this.imageGenerator);
		}
		this.zoomChanged = false;
		return this.imageGenerator;
	}
	protected AmiWebImageGenerator updateForZoom(AmiWebImageGenerator current) {
		return current;
	}

	abstract protected AmiWebImageGenerator createImageGenerator();

	abstract public Iterable<AmiWebChartShape> getCurrentShapes();
	abstract List<AmiWebChartShape> getShapesAtGroup(int groupId);
	abstract public void buildData(AmiWebDmTableSchema table, CalcFrameStack sf);
	abstract public void clearData();

	public void buildJs(StringBuilder pjs, String varName) {
	}

	public void onDataModelChanged(AmiWebDm datamodel) {
	}

	private HasherMap<String, Color> colorCache = new HasherMap<String, Color>();
	private String lastColorStr;
	private Color lastColor;
	private boolean zoomChanged;
	private String ari;
	private String amiLayoutFullAlias;
	private String amiLayoutFullAliasDotId;

	public Color parseColor(String colorStr) {
		if (OH.eq(lastColorStr, colorStr))
			return lastColor;
		Color r;
		if (this.colorCache.size() < 1000) {
			Entry<String, Color> entry = this.colorCache.getOrCreateEntry(colorStr);
			r = entry.getValue();
			if (r == null)
				entry.setValue(r = WebHelper.parseColorNoThrow(colorStr));
		} else {
			Entry<String, Color> entry = this.colorCache.getEntry(colorStr);
			r = entry != null ? entry.getValue() : WebHelper.parseColorNoThrow(colorStr);
		}
		this.lastColor = r;
		this.lastColorStr = colorStr;
		return r;
	}

	final public void onZoomChanged() {
		this.zoomChanged = true;
	}

	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		if (OH.eq(oldAliasDotName, this.dmAliasDotName))
			this.dmAliasDotName = dm.getAmiLayoutFullAliasDotId();
		this.series.onDmNameChanged(oldAliasDotName, dm);
	}

	public void resetOverrides() {
		getSeries().clearOverrides();
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return this.amiLayoutFullAlias;
	}
	@Override
	public ColorGradient getStyleColorGradient() {
		return this.getChart().getColorGradient();
	}

	@Override
	public List<String> getStyleColorSeries() {
		return this.getChart().getColorSeries();
	}
	@Override
	public String getAri() {
		return this.ari;
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_CHART_LAYER;
	}

	@Override
	public String getDomLabel() {
		return this.getName();
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.parentPlot;
	}

	@Override
	public Class<?> getDomClassType() {
		return AmiWebChartRenderingLayer.class;
	}
	@Override
	public Object getDomValue() {
		return this;
	}

	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.getChart().getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.getChart().getAmiLayoutFullAliasDotId() + "?" + this.getParentDomObject().getDomLabel() + "+" + this.getDomLabel();
		this.ari = AmiWebDomObject.ARI_TYPE_CHART_LAYER + ":" + this.amiLayoutFullAliasDotId;
		if (isManagedByDomManager)
			chart.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
	}
	public void onClosed() {
		this.removeFromDomManager();
	}
	public Color[] getColors(AmiWebChartFormula_Color cf, List values) {
		ColorGradient dfltGradient = getChart().getColorGradient();
		List<Color> dfltSeries = getChart().getColorSeriesColors();
		Color[] list;
		switch (cf.getColorType()) {
			case AmiWebChartFormula_Color.TYPE_COLOR_DFLT_GRADIENT:
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_GRADIENT: {
				ColorGradient gradient = cf.getColorType() == AmiWebChartFormula_Color.TYPE_COLOR_DFLT_GRADIENT ? dfltGradient : cf.getGradient();
				if (gradient == null || cf.getMin() == null || cf.getMax() == null) {
					list = null;
					break;
				}
				double min = cf.getMin().doubleValue();
				double max = cf.getMax().doubleValue();
				double diff = max - min;
				if (diff == 0 || gradient.getStopsCount() == 1)
					list = new Color[] { ColorHelper.newColor(gradient.toColor(.5)) };
				else {
					list = new Color[values.size()];
					for (int n = 0; n < values.size(); n++) {
						Number val = (Number) values.get(n);
						list[n] = val == null ? null : ColorHelper.newColor(gradient.toColor((val.doubleValue() - min) / (diff)));
					}
				}
				break;
			}
			case AmiWebChartFormula_Color.TYPE_COLOR_DFLT_SERIES:
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES: {
				List<Color> seriesColors = cf.getColorType() == AmiWebChartFormula_Color.TYPE_COLOR_DFLT_SERIES ? dfltSeries : cf.getSeriesColors();
				if (CH.isEmpty(seriesColors)) {
					list = null;
					break;
				}
				if (seriesColors.size() == 1)
					list = new Color[] { seriesColors.get(0) };
				else {
					list = new Color[values.size()];
					for (int n = 0; n < values.size(); n++) {
						Number val = (Number) values.get(n);
						list[n] = val == null ? null : CH.getAtMod(seriesColors, val.intValue());
					}
				}
				break;
			}
			default:
			case AmiWebChartFormula_Color.TYPE_COLOR_CONST:
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM:
			case AmiWebChartFormula_Color.TYPE_COLOR_NONE: {
				list = new Color[values.size()];
				for (int n = 0; n < values.size(); n++) {
					Object val = values.get(n);
					list[n] = val == null ? null : parseColor(AmiUtils.s(val));
				}
				break;
			}
		}
		return list;
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
	public boolean isTransient() {
		return chart.isTransient();
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}

	private boolean isManagedByDomManager = false;
	private com.f1.base.CalcTypes customFormulaTypes;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.chart.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		chart.getService().getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			AmiWebService service = this.chart.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}

	@Override
	public AmiWebFormulasImpl getFormulas() {
		return this.series;
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.amiLayoutFullAliasDotId;
	}

	@Override
	public AmiWebService getService() {
		return this.chart.getService();
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		if (f instanceof AmiWebChartFormula && ((AmiWebChartFormula) f).getType() == AmiWebChartFormula.TYPE_AXIS)
			return new com.f1.utils.structs.table.stack.CalcTypesTuple2(AmiWebChartSeries.VARTYPES, AmiWebChartAxisPortlet.VARTYPES);
		AmiWebDmTableSchema model = getDataModelSchema();
		if (model != null)
			return new com.f1.utils.structs.table.stack.CalcTypesTuple2(AmiWebChartSeries.VARTYPES, model.getClassTypes());
		if (this.customFormulaTypes != null)
			return new com.f1.utils.structs.table.stack.CalcTypesTuple2(AmiWebChartSeries.VARTYPES, customFormulaTypes);
		return AmiWebChartSeries.VARTYPES;
		//		if (table != null) {
		//			List<Column> cols = table.getColumns();
		//			com.f1.utils.BasicTypes colMap = new com.f1.utils.BasicTypes();
		//			String id;
		//			for (Column c : cols) {
		//				id = (String) c.getId();
		//				if (!AmiConsts.TABLE_PARAM_DATA.equals(id) && !AmiConsts.TABLE_PARAM_ID.equals(id)) {
		//					colMap.put(id, c.getType());
		//				}
		//			}
		//			return new com.f1.utils.TypesTuple(false, AmiWebChartSeries.VARTYPES, colMap);
		//		} else
		//			return new com.f1.utils.TypesTuple(false, AmiWebChartSeries.VARTYPES);
	}

	public com.f1.base.CalcTypes getCustomFormulaTypes() {
		return customFormulaTypes;
	}

	public void setCustomFormulaTypes(com.f1.base.CalcTypes customFormulaTypes) {
		this.customFormulaTypes = customFormulaTypes;
	}
	public Table getSelectedRows() {
		Table r = this.getDataModelSchema().newEmptyTable();
		for (AmiWebChartShape i : this.parentPlot.getSelected()) {
			if (i.getLayerPos() == getzPosition()) {
				Grouping group = getSeries().getGroupById(i.getGroupNum());
				List<Row> rows = group.getOrigRows();
				Row row = rows.get(i.getRowNum());
				r.getRows().addRow(row.getValuesCloned());
			}
		}
		return r;
	}
	@Override
	public void onParentStyleChanged(AmiWebStyledPortletPeer amiWebStyledPortletPeer) {
	}

	@Override
	public AmiWebAbstractPortlet getOwner() {
		return this.chart;
	}

	//	@Override
	//	public void onVarConstChanged(String var) {
	//		this.getFormulas().onVarConstChanged(var);
	//	}
	@Override
	final public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
