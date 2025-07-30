package com.f1.ami.web.charts;

public class AmiWebChartFormula_Simple extends AmiWebChartFormula {

	public AmiWebChartFormula_Simple(AmiWebChartSeries series, String labelGroup, String name, String label, byte type) {
		super(series, labelGroup, name, label, type);
	}

	public AmiWebChartFormula_Simple setXBound() {
		super.setXBound();
		return this;
	}
	public AmiWebChartFormula_Simple setYBound() {
		super.setYBound();
		return this;
	}
	public AmiWebChartFormula_Simple setHidden() {
		super.setHidden();
		return this;
	}
}
