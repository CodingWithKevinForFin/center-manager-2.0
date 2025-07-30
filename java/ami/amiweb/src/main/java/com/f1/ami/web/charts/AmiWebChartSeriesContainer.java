package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.List;

import com.f1.ami.web.AmiWebAbstractPortlet;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.utils.ColorGradient;

public interface AmiWebChartSeriesContainer<T extends AmiWebChartSeries> extends AmiWebDomObject {

	T getSeries();
	void setSeries(T series);

	public void flagNeedsRepaint();
	public String getAmiLayoutFullAlias();
	public ColorGradient getStyleColorGradient();
	public List<String> getStyleColorSeries();
	String getName();
	Color[] getColors(AmiWebChartFormula_Color t, List<Object> l);
	AmiWebAbstractPortlet getOwner();

}
