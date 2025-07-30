package com.f1.ami.web.charts;

public abstract class AmiWebImageGenerator {

	abstract public void draw(AmiWebChartGraphicsWrapper wrapper, int itemsStart, int itemsEnd);
	abstract public void drawGrid(AmiWebChartGraphicsWrapper wrapper);
	abstract public void drawText(AmiWebChartGraphicsWrapper wrapper);
	abstract public int getRenderingItems();
	abstract public boolean hasGrid();
	abstract public boolean hasText();
	abstract int getOpacity();
}
