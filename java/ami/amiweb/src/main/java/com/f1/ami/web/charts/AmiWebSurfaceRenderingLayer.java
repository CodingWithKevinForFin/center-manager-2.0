package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebAbstractPortlet;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebFormulasListener;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.surface.AmiWebSurfacePortlet;
import com.f1.ami.web.surface.AmiWebSurfaceSeries;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebSurfaceRenderingLayer implements AmiWebChartSeriesContainer<AmiWebSurfaceSeries>, AmiWebFormulasListener {

	private AmiWebSurfaceSeries series;
	private AmiWebSurfacePortlet chart;
	private String amiLayoutFullAlias;
	private String amiLayoutFullAliasDotId;
	private String ari;

	public AmiWebSurfaceRenderingLayer(AmiWebSurfacePortlet chart) {
		this.chart = chart;
	}

	@Override
	public void flagNeedsRepaint() {
		this.chart.flagNeedsRepaint();

	}

	@Override
	public String getAmiLayoutFullAlias() {
		return this.amiLayoutFullAlias;
	}

	@Override
	public ColorGradient getStyleColorGradient() {
		return this.chart.getStyleColorGradient();
	}

	@Override
	public List<String> getStyleColorSeries() {
		return this.chart.getStyleColorSeries();
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.amiLayoutFullAliasDotId;
	}

	@Override
	public String getAri() {
		return this.ari;
	}

	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.getChart().getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.getChart().getAmiLayoutFullAliasDotId() + "?0,0+" + this.getDomLabel();
		this.ari = AmiWebDomObject.ARI_TYPE_CHART_LAYER + ":" + this.amiLayoutFullAliasDotId;
		if (isManagedByDomManager)
			chart.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
	}

	public AmiWebSurfacePortlet getChart() {
		return this.chart;
	}

	@Override
	public AmiWebAbstractPortlet getOwner() {
		return this.chart;
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_CHART_LAYER;
	}

	@Override
	public String getDomLabel() {
		return SH.toString(this.series.getId());
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.chart;
	}

	@Override
	public Class<?> getDomClassType() {
		return AmiWebChartSeriesContainer.class;
	}

	@Override
	public Object getDomValue() {
		return this;
	}

	@Override
	public boolean isTransient() {
		return chart.isTransient();
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
	public AmiWebService getService() {
		return this.chart.getService();
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
	public AmiWebSurfaceSeries getSeries() {
		return this.series;
	}

	@Override
	public void setSeries(AmiWebSurfaceSeries series) {
		this.series = series;
		updateAri();
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return this.series;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		AmiWebDmTableSchema model = this.series.getDataModelSchema();
		if (model != null)
			return new com.f1.utils.structs.table.stack.CalcTypesTuple2(AmiWebChartSeries.VARTYPES, model.getClassTypes());
		return EmptyCalcTypes.INSTANCE;
	}

	public void setId(int i) {
		series.setId(i);
	}
	public int getId() {
		return series.getId();
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		this.chart.flagViewStale();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
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

	private static Color parseColor(String colorStr) {
		return WebHelper.parseColorNoThrow(colorStr);
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
