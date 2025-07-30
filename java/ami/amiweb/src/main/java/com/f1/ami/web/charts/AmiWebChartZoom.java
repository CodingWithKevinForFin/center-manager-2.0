package com.f1.ami.web.charts;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.MH;

public class AmiWebChartZoom {

	private static final Logger log = LH.get();

	public static final double MAX_ZOOM = 2000;

	protected int length = 0;
	protected double offset = 0;
	protected double zoom = 1;

	public boolean setLength(int length) {
		if (this.length == length)
			return false;
		double zoom = (double) length / this.length;
		if (this.length != 0) {
			this.length = length;
			setZoomAndOffset(this.zoom, this.offset * zoom);
		} else
			this.length = length;
		return true;
	}
	public int getLength() {
		return this.length;
	}

	public double getOffset() {
		return offset;
	}

	public double getZoom() {
		return zoom;
	}

	public int scalePos(double x) {
		return AmiWebChartUtils.rd(x * this.zoom + this.offset);
	}
	public double unscalePos(int x) {
		return (x - this.offset) / this.zoom;
	}

	public int scaleDistance(double w) {
		return AmiWebChartUtils.rd(w * this.zoom);
	}

	public double unscaleDistance(int w) {
		return (w / this.zoom);
	}

	public boolean setZoomAndOffset(double zoom, double offset) {
		if (zoom > MAX_ZOOM) {
			offset = offset / zoom * MAX_ZOOM;
			zoom = MAX_ZOOM;
		} else if (zoom < 1.001) {
			zoom = 1;
			offset = 0;
		}
		offset = MH.clip(offset, length - zoom * length, 0);

		if (zoom == this.zoom && offset == this.offset)
			return false;
		if (MH.isntNumber(offset) || MH.isntNumber(zoom)) {
			LH.warning(log, "offset/zoom is invalid: ", offset, "/", zoom);
			return false;
		}
		this.zoom = zoom;
		this.offset = offset;
		return true;
	}

	public boolean moveZoom(int deltaPx) {
		double t = MH.clip(this.offset + deltaPx, length - zoom * length, 0);
		if (t == this.offset)
			return false;
		if (MH.isntNumber(t))
			throw new IllegalArgumentException();
		this.offset = t;
		return true;
	}

	public boolean zoomAtPoint(int posPx, int delta) {
		if (delta == 0)
			return false;
		else if (delta > 0 && zoom >= MAX_ZOOM)
			return false;
		else if (delta < 0 && zoom <= 1)
			return false;
		if (delta < -90)
			return setZoomAndOffset(1, 0);
		double z = Math.pow(1.1, Math.abs(delta)) - 1;
		if (z > 1)
			z = 1;
		else if (delta < 0)
			z = -z / (1 - z);
		double s = posPx * z;
		return zoom((int) (s), (int) (s + (1d - z) * length));
	}
	public boolean zoom(int startPx, int endPx) {
		if (startPx > endPx) {
			final int t = startPx;
			startPx = endPx;
			endPx = t;
		}
		double zoomDelta = (double) length / (endPx - startPx);
		double offset = (this.offset - startPx);
		return setZoomAndOffset(this.zoom * zoomDelta, offset * zoomDelta);
	}

}
