package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.Collection;

import com.f1.suite.web.util.WebHelper;

public class AmiWebImageGenerator_Select extends AmiWebImageGenerator {

	private AmiWebChartShape[] shapes;
	private Color borderColor;
	private Color color;

	public AmiWebImageGenerator_Select(Collection<AmiWebChartShape> shapes, String color) {
		if (color != null) {
			this.shapes = shapes.toArray(new AmiWebChartShape[shapes.size()]);
			this.color = WebHelper.parseColorNoThrow(color);
			this.borderColor = this.color.darker().darker();
		}
	}

	@Override
	public void draw(AmiWebChartGraphicsWrapper gw, int start, int end) {
		gw.setAntialias(true);
		for (int i = start; i < end; i++) {
			if (gw.isAborted())
				return;
			AmiWebChartShape shape = shapes[i];
			shape.draw(gw, color, Math.max(1, shape.getBorderSize()), borderColor);
		}
	}

	@Override
	public int getRenderingItems() {
		return this.shapes.length;
	}

	@Override
	int getOpacity() {
		return 100;
	}

	@Override
	public void drawText(AmiWebChartGraphicsWrapper wrapper) {
	}

	@Override
	public boolean hasText() {
		return false;
	}

	@Override
	public void drawGrid(AmiWebChartGraphicsWrapper wrapper) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasGrid() {
		return false;
	}

}
