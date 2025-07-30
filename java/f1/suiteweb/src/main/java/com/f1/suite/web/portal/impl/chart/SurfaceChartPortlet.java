package com.f1.suite.web.portal.impl.chart;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.utils.Formatter;
import com.f1.utils.MH;
import com.f1.utils.agg.DoubleAggregator;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.math.tridt.DtPoint;
import com.f1.utils.math.tridt.DtTriangle;
import com.f1.utils.math.tridt.DtTriangulation;

public class SurfaceChartPortlet extends Basic3dPortlet {

	private DoubleAggregator xValues = new DoubleAggregator();
	private DoubleAggregator yValues = new DoubleAggregator();
	private DoubleAggregator zValues = new DoubleAggregator();

	public SurfaceChartPortlet(PortletConfig portletConfig) {
		super(portletConfig);
	}

	private List<DtPoint> points = new ArrayList<DtPoint>();

	public void addSurfaceData(Table data, String xColumn, String yColumn, String zColumn) {
		int xloc = data.getColumn(xColumn).getLocation();
		int yloc = data.getColumn(yColumn).getLocation();
		int zloc = data.getColumn(zColumn).getLocation();
		for (Row row : data.getRows()) {
			addSurfaceValue(row.getAt(xloc, Caster_Double.INSTANCE), row.getAt(yloc, Caster_Double.INSTANCE), row.getAt(zloc, Caster_Double.INSTANCE));
		}
	}

	@Override
	public List<Triangle> getTriangles() {
		final double xScale = MH.noNan(300 / xValues.getRange(), 10);
		final double yScale = MH.noNan(300 / yValues.getRange(), 10);
		final double zScale = MH.noNan(300 / zValues.getRange(), 10);
		final double xShift = -xValues.getMiddle();
		final double yShift = -yValues.getMiddle();
		final double zShift = -zValues.getMiddle();

		List<Triangle> r = super.getTriangles();
		if (r.isEmpty() && !this.points.isEmpty()) {
			DtTriangulation dtt = new DtTriangulation();
			for (DtPoint p : points) {
				DtPoint p2 = new DtPoint((p.getX() + xShift) * xScale, -(p.getY() + yShift) * yScale, (p.getZ() + zShift) * zScale);
				dtt.addPoint(p2);
			}
			for (DtTriangle t : dtt.getTriangles()) {
				if (!t.isHalfplane())
					addTriangle(new Triangle(t.getPoint1().getX(), t.getPoint1().getZ(), t.getPoint1().getY(), toColor(t.getPoint1()), t.getPoint2().getX(), t.getPoint2().getZ(),
							t.getPoint2().getY(), toColor(t.getPoint2()), t.getPoint3().getX(), t.getPoint3().getZ(), t.getPoint3().getY(), toColor(t.getPoint3())));
			}
		}
		return r;
	}

	@Override
	public List<Line> getLines() {
		List<Line> r = new ArrayList<Line>(super.getLines());
		int lft = -150;
		int rht = 150;
		int top = -150;
		int btm = 150;
		int frn = 150;
		int bak = -150;
		r.add(new Line(lft, top, frn, rht, top, frn, 0, 1));
		r.add(new Line(lft, btm, frn, rht, btm, frn, 0, 1));
		r.add(new Line(lft, top, frn, lft, btm, frn, 0, 1));
		r.add(new Line(rht, top, frn, rht, btm, frn, 0, 1));

		r.add(new Line(lft, top, bak, rht, top, bak, 0, 1));
		r.add(new Line(lft, btm, bak, rht, btm, bak, 0, 1));
		r.add(new Line(lft, top, bak, lft, btm, bak, 0, 1));
		r.add(new Line(rht, top, bak, rht, btm, bak, 0, 1));

		r.add(new Line(lft, top, frn, lft, top, bak, 0, 1));
		r.add(new Line(rht, top, frn, rht, top, bak, 0, 1));
		r.add(new Line(lft, btm, frn, lft, btm, bak, 0, 1));
		r.add(new Line(rht, btm, frn, rht, btm, bak, 0, 1));

		if (zValues.getCount() > 0) {
			addArrow(rht + 20, top + 10, bak, rht + 20, btm - 10, bak, r);
			addArrow(rht - 10, top - 20, frn, lft + 10, top - 20, frn, r);
			addArrow(lft - 20, btm, frn - 10, lft - 20, btm, bak + 10, r);
		}
		return r;
	}

	private void addArrow(int x1, int y1, int z1, int x2, int y2, int z2, List<Line> r) {
		r.add(new Line(x1, y1, z1, x2, y2, z2, 0, 1));
	}

	@Override
	public List<Text> getTexts() {
		Formatter formatter = getManager().getLocaleFormatter().getNumberFormatter(3);
		ArrayList<Text> r = new ArrayList<Text>();
		int lft = -150;
		int rht = 150;
		int top = -150;
		int btm = 150;
		int frn = 150;
		int bak = -150;
		if (zValues.getCount() > 0) {
			r.add(new Text(rht + 20, top, bak, formatter.format(zValues.getMax())));
			r.add(new Text(rht + 20, btm, bak, formatter.format(zValues.getMin())));
			r.add(new Text(rht, top - 20, frn, formatter.format(xValues.getMax())));
			r.add(new Text(lft, top - 20, frn, formatter.format(xValues.getMin())));
			r.add(new Text(lft - 20, btm, frn, formatter.format(yValues.getMax())));
			r.add(new Text(lft - 20, btm, bak, formatter.format(yValues.getMin())));
		}
		return r;
	}

	private int toColor(DtPoint point2) {
		int z = (int) point2.getZ();
		int r = 0, g = 0, b = 0;
		if (z < -75) {
			r = -z * 2;
		} else if (z < 75) {
			g = z + 100;
		} else
			b = z;
		r = MH.between(r, 0, 255);
		g = MH.between(g, 0, 255);
		b = MH.between(b, 0, 255);
		return (r << 16) + (g << 8) + b;
	}

	public void addSurfaceValue(double x, double y, double z) {
		xValues.add(x);
		yValues.add(y);
		zValues.add(z);
		points.add(new DtPoint(x, y, z));
		clearTriangles();
	}

	public void clearPoints() {
		xValues.reset();
		yValues.reset();
		zValues.reset();
		this.points.clear();
		super.clearTriangles();
	}

}
