package com.f1.suite.web.portal.impl.form;

import com.f1.base.ToStringable;
import com.f1.utils.MH;

/**
 * 
 * If left & right are set width is ignored. <BR>
 * If top & bottom are set height is ignored
 * 
 */
public class WebAbsoluteLocation implements ToStringable {

	static final public int PX_NA = Integer.MIN_VALUE;
	static final public double PCT_NA = Double.NaN;

	private int startPx = PX_NA;
	private int endPx = PX_NA;
	private int sizePx = PX_NA;
	private double startPct = PCT_NA;
	private double endPct = PCT_NA;
	private double sizePct = PCT_NA;
	private double offsetFromCenterPct = PCT_NA;

	final public static byte ALIGN_START_LOCK = 0;
	final public static byte ALIGN_END_LOCK = 1;
	final public static byte ALIGN_CENTER_LOCK = 2;
	final public static byte ALIGN_START_STRETCH = 3;
	final public static byte ALIGN_END_STRETCH = 4;
	final public static byte ALIGN_CENTER_STRETCH = 5;
	final public static byte ALIGN_OUTER_STRETCH = 6;
	final public static byte ALIGN_ADVANCED = 7;

	final private WebAbsoluteLocationListener listener;

	public WebAbsoluteLocation reset() {
		if (endPx == PX_NA && startPx == PX_NA && sizePx == PX_NA && !is(endPct) && !is(startPct) && !is(sizePct))
			return this;
		endPx = startPx = sizePx = PX_NA;
		endPct = startPct = sizePct = PCT_NA;
		flagChanged();
		return this;
	}
	private void flagChanged() {
		needsCalc = true;
		if (this.listener != null)
			this.listener.onLocationChanged(this);
	}

	public WebAbsoluteLocation() {
		this.listener = null;
	}
	public WebAbsoluteLocation(WebAbsoluteLocationListener l) {
		this.listener = l;
	}

	public int getEndPx() {
		return endPx;
	}
	public WebAbsoluteLocation setEndPx(int endPx) {
		if (this.endPx == endPx)
			return this;
		this.endPx = endPx;
		flagChanged();
		return this;
	}
	public int getStartPx() {
		return startPx;
	}
	public WebAbsoluteLocation setStartPx(int startPx) {
		if (this.startPx == startPx)
			return this;
		this.startPx = startPx;
		flagChanged();
		return this;
	}
	public int getSizePx() {
		return sizePx;
	}
	public WebAbsoluteLocation setSizePx(int sizePx) {
		if (this.sizePx == sizePx)
			return this;
		this.sizePx = sizePx;
		flagChanged();
		return this;
	}
	public double getOffsetFromCenterPct() {
		return offsetFromCenterPct;
	}
	public WebAbsoluteLocation setOffsetFromCenterPct(double offsetFromCenterPct) {
		this.offsetFromCenterPct = offsetFromCenterPct;
		if (is(offsetFromCenterPct)) {
			int sizePx = this.sizePx;
			double startPct = this.startPct;
			int startPx = this.startPx;
			this.sizePx = WebAbsoluteLocation.addPxAndPct(this.sizePx, this.sizePct, this.outerSizeCache);
			this.startPx = -this.sizePx / 2;
			this.startPct = 0.5 + offsetFromCenterPct;
			if (same(startPx, this.startPx) && same(sizePx, this.sizePx) && same(startPct, this.startPct) && same(offsetFromCenterPct, this.offsetFromCenterPct))
				return this;
		} else if (same(offsetFromCenterPct, this.offsetFromCenterPct))
			return this;
		flagChanged();
		return this;
	}
	public double getEndPct() {
		return endPct;
	}
	public WebAbsoluteLocation setEndPct(double endPct) {
		if (same(this.endPct, endPct))
			return this;
		this.endPct = endPct;
		flagChanged();
		return this;
	}
	public double getStartPct() {
		return startPct;
	}
	public WebAbsoluteLocation setStartPct(double startPct) {
		if (same(this.startPct, startPct))
			return this;
		this.startPct = startPct;
		flagChanged();
		return this;
	}
	public double getSizePct() {
		return sizePct;
	}
	public WebAbsoluteLocation setSizePct(double sizePct) {
		if (same(this.sizePct, sizePct))
			return this;
		this.sizePct = sizePct;
		flagChanged();
		return this;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	private int outerSizeCache = PX_NA;

	private int realizedStart;
	private int realizedEnd;
	private int realizedSize;
	private boolean needsCalc = true;

	public WebAbsoluteLocation setOuterSize(int size) {
		if (this.outerSizeCache == size)
			return this;
		this.outerSizeCache = size;
		flagChanged();
		return this;
	}
	public int getOuterSize() {
		return outerSizeCache;
	}

	public int getRealizedStart() {
		ensureCalced();
		return realizedStart;
	}

	public int getRealizedEnd() {
		ensureCalced();
		return realizedEnd;
	}

	public int getRealizedSize() {
		ensureCalced();
		if (!is(realizedSize))
			throw new IllegalStateException();
		return this.realizedSize;
	}

	public boolean isSizeDefined() {
		if (!needsCalc)
			return is(this.realizedStart);
		return is(addPxAndPct(this.sizePx, this.sizePct, 0)) || (is(addPxAndPct(this.startPx, this.startPct, 0)) && is(addPxAndPct(this.endPx, this.endPct, 0)));
	}
	public boolean isStartDefined() {
		return is(addPxAndPct(this.startPx, this.startPct, 0));
	}
	public boolean isEndDefined() {
		return is(addPxAndPct(this.endPx, this.endPct, 0));
	}
	public boolean isDefined() {
		if (!needsCalc)
			return is(this.realizedStart);
		int l = addPxAndPct(this.startPx, this.startPct, 0);
		int r = addPxAndPct(this.endPx, this.endPct, 0);
		int w = addPxAndPct(this.sizePx, this.sizePct, 0);
		if (is(w))
			return is(l) || is(r);
		else
			return is(r) && is(l);
	}

	private void assertValid() {
		if (!isDefined())
			throw new IllegalStateException("not valid, check with isValid() first");
	}
	private void ensureCalced() {
		if (!needsCalc)
			return;
		this.needsCalc = false;
		if (!is(this.outerSizeCache))
			throw new IllegalStateException("must callSetOuterSize(...) first");
		int size = this.outerSizeCache;
		int l = addPxAndPct(this.startPx, this.startPct, size);
		int r = addPxAndPct(this.endPx, this.endPct, size);
		int w = addPxAndPct(this.sizePx, this.sizePct, size);
		this.realizedSize = w;
		if (is(r))
			realizedEnd = r = (size - r);
		else if (is(l) && is(w))
			realizedEnd = l + w;
		else {
			realizedStart = realizedEnd = PX_NA;
			return;
		}

		if (is(l))
			realizedStart = l;
		else if (is(r) && is(w))
			realizedStart = r - w;
		else {
			realizedStart = realizedEnd = PX_NA;
			return;
		}
		realizedSize = this.realizedEnd - this.realizedStart;
	}

	public static boolean is(int px) {
		return px != PX_NA;
	}
	public static boolean is(double pct) {
		return MH.isNumber(pct);
	}
	public static int addPxAndPct(int px, double pct, int w) {
		if (is(px))
			return is(pct) ? px + (int) (pct * w) : px;
		else
			return is(pct) ? (int) (pct * w) : PX_NA;
	}
	public static int convertPctToPx(double pct, int w) {
		return addPxAndPct(0, pct, w);
	}

	//	public void addListener(WebAbsoluteLocationListener listener) {
	//		CH.addIdentityOrThrow(this.listeners, listener);
	//	}
	//	public void removeListener(WebAbsoluteLocationListener listener) {
	//		CH.removeOrThrow(this.listeners, listener);
	//	}

	public Integer getCenterPosPx() {
		int size = WebAbsoluteLocation.addPxAndPct(this.sizePx, this.sizePct, this.outerSizeCache);
		int start = WebAbsoluteLocation.addPxAndPct(this.startPx, this.startPct, this.outerSizeCache);
		int end = WebAbsoluteLocation.addPxAndPct(this.endPx, this.endPct, this.outerSizeCache);
		boolean hasSize = WebAbsoluteLocation.is(size);
		boolean hasStart = WebAbsoluteLocation.is(start);
		boolean hasEnd = WebAbsoluteLocation.is(end);
		if (hasSize && hasStart && hasEnd) {
			int pos = (start + this.outerSizeCache - end) / 2;
			return pos > start ? pos : start;
		} else if (hasSize && hasStart && !hasEnd) {
			return start + size / 2;
		} else if (hasSize && !hasStart && hasEnd) {
			return this.outerSizeCache - (end + size / 2);
		} else if (!hasSize && hasStart && hasEnd) {
			return (start + this.outerSizeCache - end) / 2;
		} else
			return null;
	}

	public boolean isInsidePx(int x) {
		int size = WebAbsoluteLocation.addPxAndPct(this.sizePx, this.sizePct, this.outerSizeCache);
		int start = WebAbsoluteLocation.addPxAndPct(this.startPx, this.startPct, this.outerSizeCache);
		int end = WebAbsoluteLocation.addPxAndPct(this.endPx, this.endPct, this.outerSizeCache);
		boolean hasSize = WebAbsoluteLocation.is(size);
		boolean hasStart = WebAbsoluteLocation.is(start);
		boolean hasEnd = WebAbsoluteLocation.is(end);
		int xCompliment = this.outerSizeCache - x;
		if (hasSize && hasStart) {
			return x > start && x < start + size;
		} else if (hasSize && hasEnd) {
			return xCompliment > end && xCompliment < end + size;
		} else if (hasStart && hasEnd) {
			return x > start && xCompliment > end;
		} else {
			throw new IllegalStateException("Object must have absolute positioning defined");
		}
	}

	public byte getAlignment() {
		boolean isStartPx = is(this.startPx);
		boolean isStartPct = is(this.startPct);
		boolean isEndPx = is(this.endPx);
		boolean isEndPct = is(this.endPct);
		boolean isSizePx = is(this.sizePx);
		boolean isSizePct = is(this.sizePct);
		if (is(this.offsetFromCenterPct)) {
			return ALIGN_CENTER_LOCK;
		} else if ((isStartPx && isStartPct) || (isEndPx && isEndPct) || (isSizePx && isSizePct)) {
			return ALIGN_ADVANCED;
		} else if (isStartPx && isSizePx) {
			return ALIGN_START_LOCK;
		} else if (isEndPx && isSizePx) {
			return ALIGN_END_LOCK;
		} else if (isStartPx && isEndPct) {
			return ALIGN_START_STRETCH;
		} else if (isEndPx && isStartPct) {
			return ALIGN_END_STRETCH;
		} else if (isStartPct && isEndPct) {
			return ALIGN_CENTER_STRETCH;
		} else if (isStartPx && isEndPx) {
			return ALIGN_OUTER_STRETCH;
		} else {
			return ALIGN_ADVANCED;
		}
	}
	public WebAbsoluteLocation center() {
		int sizePx = this.sizePx;
		double startPct = this.startPct;
		int startPx = this.startPx;
		this.sizePx = WebAbsoluteLocation.addPxAndPct(this.sizePx, this.sizePct, this.outerSizeCache);
		this.startPct = 0.5;
		this.startPx = -this.sizePx / 2;
		if (same(startPx, this.startPx) && same(sizePx, this.sizePx) && same(startPct, this.startPct))
			return this;
		flagChanged();
		return this;
	}

	public int getStartPxFromAlignment() {
		byte alignment = getAlignment();
		switch (alignment) {
			case ALIGN_START_LOCK: // startPx, sizePx
				return this.startPx;
			case ALIGN_START_STRETCH: // startPx, endPct
				return this.startPx;
			case ALIGN_END_LOCK: // endPx, sizePx
				return this.outerSizeCache - (this.sizePx + this.endPx);
			case ALIGN_END_STRETCH: // endPx, startPct 
				return addPxAndPct(0, this.startPct, this.outerSizeCache);
			case ALIGN_OUTER_STRETCH: // startPx, endPx
				return this.startPx;
			case ALIGN_CENTER_LOCK: // sizePx, offsetFromCenterPct (startPct)
				return addPxAndPct(this.startPx, this.startPct, this.outerSizeCache);
			case ALIGN_CENTER_STRETCH: // startPct, endPct
				return addPxAndPct(0, this.startPct, this.outerSizeCache);
			case ALIGN_ADVANCED: // all
				return addPxAndPct(this.startPx, this.startPct, this.outerSizeCache);
			default:
				return this.startPx;
		}
	}
	public int getSizePxFromAlignment() {
		byte alignment = getAlignment();
		switch (alignment) {
			case ALIGN_START_LOCK: // startPx, sizePx
				return this.sizePx;
			case ALIGN_START_STRETCH: // startPx, endPct
				return this.outerSizeCache - addPxAndPct(this.startPx, this.endPct, this.outerSizeCache);
			case ALIGN_END_LOCK: // endPx, sizePx
				return this.sizePx;
			case ALIGN_END_STRETCH: // endPx, startPct 
				return this.outerSizeCache - addPxAndPct(this.endPx, this.startPct, this.outerSizeCache);
			case ALIGN_OUTER_STRETCH: // startPx, endPx
				return this.outerSizeCache - this.startPx - this.endPx;
			case ALIGN_CENTER_LOCK: // sizePx, offsetFromCenterPct (startPct)
				return this.sizePx;
			case ALIGN_CENTER_STRETCH: // startPct, endPct
				return this.outerSizeCache - addPxAndPct(0, this.startPct + this.endPct, this.outerSizeCache);
			case ALIGN_ADVANCED: // all
				return addPxAndPct(this.sizePx, this.sizePct, this.outerSizeCache);
			default:
				return this.sizePx;
		}
	}
	public WebAbsoluteLocation setPositionPxFromStartAndSize(int start, int size) {
		return setPositionPxFromStartAndSize(start, size, getAlignment());
	}

	public WebAbsoluteLocation setPositionPxFromStartAndSize(int start, int size, byte alignment) {
		int startPx = this.startPx;
		int endPx = this.endPx;
		int sizePx = this.sizePx;
		double startPct = this.startPct;
		double endPct = this.endPct;
		double sizePct = this.sizePct;
		double offsetFromCenterPct = this.offsetFromCenterPct;
		switch (alignment) {
			case ALIGN_START_LOCK: // startPx, sizePx
				this.startPx = start;
				this.sizePx = size;
				// unused
				this.endPx = PX_NA;
				this.startPct = PCT_NA;
				this.endPct = PCT_NA;
				this.sizePct = PCT_NA;
				this.offsetFromCenterPct = PCT_NA;
				break;
			case ALIGN_START_STRETCH: // startPx, endPct
				this.startPx = start;
				this.endPct = ((double) (this.outerSizeCache - start - size)) / this.outerSizeCache;
				// unused
				this.endPx = PX_NA;
				this.sizePx = PX_NA;
				this.startPct = PCT_NA;
				this.sizePct = PCT_NA;
				this.offsetFromCenterPct = PCT_NA;
				break;
			case ALIGN_END_LOCK: // endPx, sizePx
				this.sizePx = size;
				this.endPx = this.outerSizeCache - this.sizePx - start;
				// unused
				this.startPx = PX_NA;
				this.startPct = PCT_NA;
				this.endPct = PCT_NA;
				this.sizePct = PCT_NA;
				this.offsetFromCenterPct = PCT_NA;
				break;
			case ALIGN_END_STRETCH: // endPx, startPct 
				this.startPct = ((double) start) / this.outerSizeCache;
				this.endPx = this.outerSizeCache - start - size;
				// unused
				this.startPx = PX_NA;
				this.sizePx = PX_NA;
				this.endPct = PCT_NA;
				this.sizePct = PCT_NA;
				this.offsetFromCenterPct = PCT_NA;
				break;
			case ALIGN_OUTER_STRETCH: // startPx, endPx
				this.startPx = start;
				this.endPx = this.outerSizeCache - this.startPx - size;
				// unused
				this.sizePx = PX_NA;
				this.startPct = PCT_NA;
				this.endPct = PCT_NA;
				this.sizePct = PCT_NA;
				this.offsetFromCenterPct = PCT_NA;
				break;
			case ALIGN_CENTER_LOCK: // sizePx, offsetFromCenterPct (startPx, startPct)
				this.sizePx = size;
				int startCenter = addPxAndPct(-this.sizePx / 2, 0.5, this.outerSizeCache);
				int offset = start - startCenter;
				setOffsetFromCenterPct(((double) offset) / this.outerSizeCache);
				// unused
				this.endPx = PX_NA;
				this.endPct = PCT_NA;
				this.sizePct = PCT_NA;
				break;
			case ALIGN_CENTER_STRETCH: // startPct, endPct
				this.startPct = ((double) start) / this.outerSizeCache;
				this.endPct = 1.0 - (this.startPct + ((double) size) / this.outerSizeCache);
				// unused
				this.startPx = PX_NA;
				this.endPx = PX_NA;
				this.sizePx = PX_NA;
				this.sizePct = PCT_NA;
				this.offsetFromCenterPct = PCT_NA;
				break;
			case ALIGN_ADVANCED: // all
				this.startPx = start;
				this.sizePx = size;
				this.offsetFromCenterPct = PCT_NA;
				break;
			default:
				this.startPx = start;
				this.sizePx = size;
				this.offsetFromCenterPct = PCT_NA;
				break;
		}
		if (same(startPx, this.startPx) && same(endPx, this.endPx) && same(sizePx, this.sizePx) && same(startPct, this.startPct) && same(endPct, this.endPct)
				&& same(sizePct, this.sizePct) && same(offsetFromCenterPct, this.offsetFromCenterPct))
			return this;
		flagChanged();
		return this;
	}

	private boolean same(int a, int b) {
		return a == b;
	}
	private boolean same(double a, double b) {
		return MH.eq(a, b);
	}
	/**
	 * Convert alignment setting while preserving position.
	 * 
	 * @param newAlignment
	 */
	public void convertAlignment(byte newAlignment) {
		if (newAlignment == getAlignment()) {
			return;
		}
		int start = getStartPxFromAlignment();
		int size = getSizePxFromAlignment();
		setPositionPxFromStartAndSize(start, size, newAlignment);
	}

	public WebAbsoluteLocation clearStartPx() {
		if (this.startPx == PX_NA)
			return this;
		this.startPx = PX_NA;
		flagChanged();
		return this;
	}
	public WebAbsoluteLocation clearEndPx() {
		if (this.endPx == PX_NA)
			return this;
		this.endPx = PX_NA;
		flagChanged();
		return this;
	}
	public WebAbsoluteLocation clearSizePx() {
		if (this.sizePx == PX_NA)
			return this;
		this.sizePx = PX_NA;
		flagChanged();
		return this;
	}
	public WebAbsoluteLocation clearStartPct() {
		if (MH.eq(this.startPct, PCT_NA))
			return this;
		this.startPct = PCT_NA;
		flagChanged();
		return this;
	}
	public WebAbsoluteLocation clearEndPct() {
		if (MH.eq(this.endPct, PCT_NA))
			return this;
		this.endPct = PCT_NA;
		flagChanged();
		return this;
	}
	public WebAbsoluteLocation clearSizePct() {
		if (MH.eq(this.sizePct, PCT_NA))
			return this;
		this.sizePct = PCT_NA;
		flagChanged();
		return this;
	}
	//	public void keepInsideBorders() {
	//		if (this.outerSizeCache == PX_NA || isUnderdefined())
	//			return;
	//		int start = getStartPxFromAlignment();
	//		if (start < 0) {
	//			start = 0;
	//			int size = getSizePxFromAlignment();
	//			setPositionPxFromStartAndSize(start, size);
	//		}
	//	}
	private boolean isUnderdefined() {
		int count = 0;
		if (is(this.startPx) || is(this.startPct)) {
			count++;
		}
		if (is(this.endPx) || is(this.endPct)) {
			count++;
		}
		if (is(this.sizePx) || is(this.sizePct)) {
			count++;
		}
		return count < 2;
	}
	public void clearAllPositioning() {
		this.sizePx = PX_NA;
		this.startPx = PX_NA;
		this.endPx = PX_NA;
		this.sizePct = PCT_NA;
		this.startPct = PCT_NA;
		this.endPct = PCT_NA;
		this.offsetFromCenterPct = PCT_NA;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("[");
		boolean needsComma = false;
		needsComma = toString('s', this.startPct, this.startPx, sink, needsComma);
		needsComma = toString('l', this.sizePct, this.sizePx, sink, needsComma);
		needsComma = toString('e', this.endPct, this.endPx, sink, needsComma);
		needsComma = toString('o', this.offsetFromCenterPct, PX_NA, sink, needsComma);
		sink.append("]");

		return sink;
	}
	private static boolean toString(char description, double pct, int px, StringBuilder sink, boolean needsComma) {
		if (is(px)) {
			if (needsComma)
				sink.append(',');
			sink.append(description).append('=').append(px);
			if (is(pct))
				sink.append('+').append(pct).append('%');
			return true;
		} else if (is(pct)) {
			if (needsComma)
				sink.append(',');
			sink.append(description).append('=').append(pct).append('%');
			return true;
		} else
			return needsComma;
	}
}
