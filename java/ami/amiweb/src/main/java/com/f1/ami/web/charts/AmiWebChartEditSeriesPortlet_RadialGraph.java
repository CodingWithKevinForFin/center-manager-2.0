package com.f1.ami.web.charts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditSeriesPortlet_RadialGraph extends AmiWebChartEditSeriesPortlet<AmiWebChartSeries_RadialGraph> {
	protected static final Set<String> fieldCheck = new HashSet<String>(Arrays.asList("orderBy", "Angle", "Radius", "Label", "Label Color", "Label Size", "Label Position",
			"Hover over", "mShape", "mColor", "mWidth", "mHeight", "mBorderColor", "mBorderSize"));

	public AmiWebChartEditSeriesPortlet_RadialGraph(PortletConfig config) {
		super(config);
	}

	@Override
	protected void initForm() {
		super.initForm();
	}
	@Override
	public String getEditorLabel() {
		return "Advanced Radial";
	}

	@Override
	public String getEditorTypeId() {
		return TYPE_RADIAL_ADVANCED;
	}

	@Override
	public void fillDefaultFields() {
		//		getEditor(series.getmShapeFormula()).setValueIfNotPopulated("\"wedge\"");
		//		getEditor(series.getmColorFormula()).setValueIfNotPopulated("this.getStyle(\"seriesCls\", __row_num)");
		//		getEditor(series.getTopField()).setValueIfNotPopulated("1");
		//		getEditor(series.getBottomField()).setValueIfNotPopulated("0");
		//		getEditor(series.getHorizontalStackMin()).setValueIfNotPopulated("0");
		//		getEditor(series.getHorizontalStackMax()).setValueIfNotPopulated("360");

	}

}
