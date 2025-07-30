/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.portal.ColorUsingPortlet;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Double;

public class DividerPortlet extends AbstractPortletContainer implements ColorUsingPortlet {

	public static final PortletSchema<DividerPortlet> SCHEMA = new BasicPortletSchema<DividerPortlet>("Divider", "DividerPortlet", DividerPortlet.class, true, true);

	public static final int DEFAULT_THICKNESS = 6;

	protected static final double DEFAULT_EXPAND_BIAS = .5;

	private double offset = .5;
	private boolean isVertical;
	private int thickness = DEFAULT_THICKNESS;
	private Portlet first, second;

	private boolean isCustomizable = true;

	private double expandBias = DEFAULT_EXPAND_BIAS;
	private int currentSize = -1; // previous size

	private int offsetPx = 0; // current offset
	private boolean usePixelOffset = false;
	private boolean lockPosition = false;
	private boolean movable = true;// divider is movable if you are in dev mode
	private boolean updateOffset = true;
	private final List<DividerListener> listeners = new ArrayList<DividerListener>();
	private String color;
	private String hoverColor;
	private boolean offsetClipped = false;
	private int restorePx = offsetPx; // the default divider offset px, dynamically adjust base on alignment; panel area + half of divider thickness
	private int align = 1; // 0 left/top; 1 ratio; 2 right/bottom
	private int prevSize; // tracks the length of panel when default was last set
	private boolean snapped;
	private boolean inEditMode;

	public DividerPortlet(PortletConfig manager, boolean isVertical) {
		super(manager);
		this.isVertical = isVertical;
		//		addChild(new BlankPortlet(generateConfig()));
		//		addChild(new BlankPortlet(generateConfig()));
	}

	public DividerPortlet(PortletConfig manager, boolean isVertical, Portlet first, Portlet second) {
		super(manager);
		this.isVertical = isVertical;
		addChild(first);
		addChild(second);
	}

	@Override
	public void addChild(Portlet portlet) {
		if (first instanceof BlankPortlet && second != null) {
			setFirst(portlet);
		} else if (second instanceof BlankPortlet) {
			setSecond(portlet);
		} else {
			switch (getChildrenCount()) {
				case 0:
					setFirst(portlet);
					break;
				case 1:
					setSecond(portlet);
					break;
				default:
					throw new RuntimeException("Cannot have more than two children");
			}
		}
	}

	public void setFirst(Portlet portlet) {
		if (portlet == null)
			return;
		if (first != null) {
			super.removeChild(first.getPortletId());
			first.onClosed();
		}
		if (second == null || second instanceof BlankPortlet) {
			int sug, tot;
			if (isVertical) {
				sug = portlet.getSuggestedWidth(getManager().getPortletMetrics());
				tot = getWidth();
			} else {
				sug = portlet.getSuggestedHeight(getManager().getPortletMetrics());
				tot = getHeight();
			}
			if (tot > 0 && sug > 0)
				setOffsetInner(MH.between((double) sug / tot, .1, .9));
		}
		first = portlet;
		super.addChild(portlet);
		if (getVisible()) {
			makeChildrenVisible(true);
			layoutChildren();
		}
	}

	public void setSecond(Portlet portlet) {
		if (portlet == null)
			return;
		if (second != null) {
			super.removeChild(second.getPortletId());
			second.onClosed();
		}
		if (first == null || first instanceof BlankPortlet) {
			int sug, tot;
			if (isVertical) {
				sug = portlet.getSuggestedWidth(getManager().getPortletMetrics());
				tot = getWidth();
			} else {
				sug = portlet.getSuggestedHeight(getManager().getPortletMetrics());
				tot = getHeight();
			}
			if (tot > 0 && sug > 0)
				setOffsetInner(MH.between(1d - ((double) sug / tot), .1, .9));
		}
		second = portlet;
		super.addChild(portlet);
		if (getVisible()) {
			makeChildrenVisible(true);
			layoutChildren();
		}
	}

	@Override
	public void replaceChild(String id, Portlet replacement) {
		Portlet child = super.removeChild(id);
		if (first == child) {
			first = null;
			setFirst(replacement);
		} else {
			second = null;
			setSecond(replacement);
		}
		super.addChild(replacement);
		if (getVisible()) {
			makeChildrenVisible(true);
			layoutChildren();
		}
	}

	@Override
	public Portlet removeChild(String id) {
		Portlet r = super.removeChild(id);
		if (r == first)
			first = null;
		if (r == second)
			second = null;
		return r;
	}

	@Override
	protected void makeChildrenVisible(boolean visible) {
		if (visible == false)
			super.makeChildrenVisible(visible);
		else {
			if (first != null)
				this.makeChildVisible(this.first, !this.isAtStart(this.offset));
			if (second != null)
				this.makeChildVisible(this.second, !this.isAtEnd(this.offset));
		}
	}

	@Override
	protected void layoutChildren() {
		if (!getVisible())
			return;
		if (getChildrenCount() > 2)
			throw new RuntimeException("must have at max 2 children: " + getChildrenCount());
		int width = getWidth();
		int height = getHeight();
		if (width <= 0 || height <= 0)
			return;
		int size = getSize();
		if (this.updateOffset) {
			if (isOffsetClipped() && !isSnapped() && hasSpace()) {
				// clipped, not snapped, has space
				restoreCurrentOffsetToDefault();
				setOffsetClipped(false);
			}
			if (this.usePixelOffset) {
				// snapping/set px via amiscript
				// restoring to default
				if (offsetPx < 0)
					this.setOffsetInner(((double) Math.max(size + offsetPx, 0)) / size);
				else
					this.setOffsetInner(((double) Math.min(offsetPx, size)) / size);
				this.usePixelOffset = false;
			} else if (this.expandBias != .5d) {
				//				double padding = (double) this.thickness / 2;
				// calculate ratio for left/right aligned
				//				boolean shouldStick = isSnapped() || (!getLockPosition() && isOffsetClipped() && !hasSpace());
				// not locked && offset clipped && no space
				// stick to the edge when available space increases but there still isn't enough space
				// this serves as a visual cue to user that there is not enough space to place divider
				//				if (!shouldStick) {
				if (this.currentSize > 0) {
					double offset2 = this.expandBias + (offset - this.expandBias) * currentSize / size;
					this.setOffsetInner(offset2);
				} else {
					this.fireOnDividerMoved(this, this.offset);
				}
				this.offsetPx = (int) (this.offset * size);
				//				} else {
				//					double paddingPct = padding / size;
				//					if (this.align == 2) {
				//						// right/bottom
				//						// we need to maintain the offsetPx, so we need to update offset relative to the new size
				//						this.offset = this.offsetPx * 1.0 / size;
				//					}
				//					if (!this.lockPosition) {
				//						// bound offset to account for thickness padding (thickness/2)
				//						this.offset = MH.clip(this.offset, paddingPct, 1.0 - paddingPct);
				//					}
				//				}
				// keep previous offset pct && offset px
				// don't fire divider moved
			} else {
				// ratio
				this.offsetPx = (int) (this.offset * size);
				this.fireOnDividerMoved(this, this.offset);
			}
		}
		this.currentSize = size;
		preparePanelsAndDivider();
		if (this.align != 1 && !hasSpace()) {
			// not ratio and no space
			setOffsetClipped(true);
		}
		this.updateOffset = true;
		makeChildrenVisible(true);
	}
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("dividerMoved".equals(callback)) {
			int currentOffsetPx = CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "offset").intValue();
			// account for thickness before calculating the offset pct
			double currentOffset = (((double) (currentOffsetPx + thickness / 2)) / getSize());

			if (this.offset != currentOffset) {
				setOffset(currentOffset);
			}
		} else if ("onUsrDblClick".equals(callback)) {
			fireOnDividerDblClick(this);
		} else if ("dividerMovingStarted".equals(callback)) {
			callJsFunction("onDividerMovingProcessed").end();
			double currentOffset = ((CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "offset") + thickness / 2) / getSize());
			fireOnDividerMovingStarted(this, currentOffset);
		} else if ("dividerMoving".equals(callback)) {
			callJsFunction("onDividerMovingProcessed").end();
			double currentOffset = ((CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "offset") + thickness / 2) / getSize());
			fireOnDividerMoving(this, currentOffset);
		} else
			super.handleCallback(callback, attributes);

	}

	private void fireOnDividerMoving(DividerPortlet dividerPortlet, double currentOffset) {
		if (getVisible())
			for (DividerListener i : listeners)
				i.onDividerMoving(this, currentOffset);

	}
	private void fireOnDividerMovingStarted(DividerPortlet dividerPortlet, double currentOffset) {
		if (getVisible())
			for (DividerListener i : listeners)
				i.onDividerMoving(this, currentOffset);
	}
	private void fireOnDividerMoved(DividerPortlet dividerPortlet, double currentOffset) {
		if (getVisible())
			for (DividerListener i : listeners)
				i.onDividerMoved(this, currentOffset);
	}

	private void fireOnDividerDblClick(DividerPortlet dividerPortlet) {
		for (DividerListener i : listeners)
			i.onDividerDblClick();
	}

	private void fireOnDividerRestored(DividerPortlet dividerPortlet) {
		if (getVisible())
			for (DividerListener i : listeners)
				i.onDividerRestored(this);
	}
	public void addListener(DividerListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(DividerListener listener) {
		this.listeners.remove(listener);
	}
	public void swap(boolean maintainPosition) {
		final Portlet temp = first;
		int t = getThickness() / 2;
		first = second;
		second = temp;

		if (!maintainPosition) {
			// this.offsetPx contains half of divider thickness, this is for consistency
			setOffsetFromTopPx(isVertical() ? (this.first.getWidth() + t) : (this.first.getHeight() + t)); // Keep respective panel sizes
		}
		if (getVisible()) {
			makeChildrenVisible(false);
			layoutChildren();
		}
	}
	public void setVertical(boolean vertical) {
		if (this.isVertical == vertical)
			return;
		this.isVertical = vertical;
		if (getVisible()) {
			makeChildrenVisible(false);
			callInit();
			layoutChildren();
			//			setOffset(.5);
		}
	}

	public void callInit() {
		callInit(isVertical, thickness, !movable, color, hoverColor);
	}
	protected void callInit(boolean isVertical, int thickness, boolean lockPosition, String color, String hoverColor) {
		callJsFunction("init").addParam(isVertical).addParam(thickness).addParam(lockPosition).addParamQuoted(color).addParamQuoted(hoverColor).end();
	}
	@Override
	protected void initJs() {
		super.initJs();
		callInit();
	}

	@Override
	public PortletSchema<DividerPortlet> getPortletSchema() {
		return SCHEMA;
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		if (this.offset == offset)
			return;
		setOffsetInner(offset);
		this.fireOnDividerMoved(this, this.offset);
		layoutChildren();
	}

	private void setOffsetInner(double offset) {
		if (this.lockPosition)
			this.offset = offset;
		else {
			// offset to account for thickness
			double thicknessOffset = thickness * 1.0 / 2;
			double d = thicknessOffset / getSize();
			if (d > .5 || Double.isInfinite(d))
				d = 0;
			this.offset = MH.clip(offset, d, 1d - d);
		}
	}
	public void setOffsetFromTopPx(int offset) {
		if (offset < 0)
			throw new IllegalArgumentException("Offset must be positive: " + offset);
		if (this.getSize() > 0)
			this.fireOnDividerMoved(this, (double) offset / this.getSize());
		this.offsetPx = offset;
		this.usePixelOffset = true;
		layoutChildren();
	}
	public void setOffsetFromBottomPx(int offset) {
		if (offset < 0)
			throw new IllegalArgumentException("Offset must be positive: " + offset);
		if (this.getSize() > 0)
			this.fireOnDividerMoved(this, (double) (this.getSize() - offset) / this.getSize());
		this.offsetPx = -offset;
		this.usePixelOffset = true;
		layoutChildren();
	}

	public int getOffsetPx() {
		return offsetPx;
	}
	public int getAbsOffsetPx() {
		return MH.abs(offsetPx);
	}
	public void setOffsetPx(int offsetPx) {
		this.offsetPx = offsetPx;
		this.fireOnDividerMoved(this, (double) offsetPx / this.getSize());
		this.usePixelOffset = true;
		layoutChildren();
	}
	@Override
	protected void callJsAddChild(Portlet p) {
		callJsFunction("addChild").addParam(p == first ? '1' : '2').addParamQuoted(p.getPortletId()).end();
	}

	@Override
	protected void onUserDeleteChild(String childPortletId) {
		Portlet removed = getChild(childPortletId);
		if (removed == first) {
			getParent().replaceChild(getPortletId(), second);
		} else {
			getParent().replaceChild(getPortletId(), first);
		}
		removed.onClosed();
	}
	protected String portletToConfigSaveId(Portlet portlet) {
		return portlet.getPortletId();
	}
	protected Portlet configSaveIdToPortlet(Map<String, String> origToNewIdMapping, String portletId) {
		return getManager().getPortlet(origToNewIdMapping.get(portletId));
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("dir", isVertical ? "v" : "h");
		if (first != null && !(first instanceof BlankPortlet))
			r.put("child1", portletToConfigSaveId(first));
		if (second != null && !(second instanceof BlankPortlet))
			r.put("child2", portletToConfigSaveId(second));
		//		if (this.defaultOffsetPct == null) {
		r.put("offset", this.offset);
		//		} else {
		//			r.put("offsetDflt", this.defaultOffsetPct);
		//		}
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		String child1 = (String) configuration.get("child1");
		String child2 = (String) configuration.get("child2");
		String dir = (String) configuration.get("dir");
		if (dir != null)
			setVertical("v".equals(dir));
		Double defaultOffsetPct = CH.getOr(Caster_Double.INSTANCE, configuration, "offsetDflt", null);//backwards compatibility
		if (defaultOffsetPct == null) {
			this.offset = CH.getOr(Caster_Double.INSTANCE, configuration, "offset", 0.5);
		} else {
			this.offset = defaultOffsetPct;
		}
		this.currentSize = getSize();

		if (child1 != null)
			setFirst(configSaveIdToPortlet(origToNewIdMapping, child1));
		if (child2 != null)
			setSecond(configSaveIdToPortlet(origToNewIdMapping, child2));
	}

	public static class Builder extends AbstractPortletBuilder<DividerPortlet> {

		public static final String ID = "div";

		public Builder() {
			super(DividerPortlet.class);
			setIcon("portlet_icon_vdivider");
		}

		@Override
		public DividerPortlet buildPortlet(PortletConfig portletConfig) {
			DividerPortlet r = new DividerPortlet(portletConfig, true);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Divider";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	public static class HBuilder extends Builder {
		public DividerPortlet buildPortlet(PortletConfig portletConfig) {
			DividerPortlet r = super.buildPortlet(portletConfig);
			r.setVertical(false);
			return r;
		};
	}

	public static class VBuilder extends Builder {
		public DividerPortlet buildPortlet(PortletConfig portletConfig) {
			DividerPortlet r = super.buildPortlet(portletConfig);
			r.setVertical(true);
			return r;
		};
	}

	@Override
	public void bringToFront(String portletId) {
		//nothing to do... all children are already in front
	}

	@Override
	public boolean hasVacancy() {
		return first == null || second == null || first instanceof BlankPortlet || second instanceof BlankPortlet;
	}

	@Override
	public boolean isCustomizable() {
		return isCustomizable;
	}

	public void setIsCustomizable(boolean isCustomizable) {
		this.isCustomizable = isCustomizable;
	}

	public void setExpandBias(double i, double j) {
		// 0/1 for left/top
		// 1/0 for right/bottom
		// 0.5/0.5 for ratio
		double sum = i + j;
		double expandBias;
		if (sum == 0)
			expandBias = .5;
		else
			expandBias = i / sum;
		OH.assertBetween(expandBias, 0, 1);
		// 0 for left/top
		// 1 for right/bottom
		// 0.5 for ratio
		this.expandBias = expandBias;
	}

	public double getExpandBias() {
		return this.expandBias;
	}

	public boolean isVertical() {
		return this.isVertical;
	}
	public Portlet getFirstChild() {
		return this.first;
	}
	public Portlet getSecondChild() {
		return this.second;
	}
	public void setLockPosition(Boolean lockPosition) {
		if (lockPosition == null || this.lockPosition == lockPosition)
			return;
		this.lockPosition = lockPosition;
		if (getVisible()) {
			//			callInit();
			this.updateOffset = false;
			layoutChildren();
		}
	}
	public boolean getLockPosition() {
		return lockPosition;
	}
	public int getThickness() {
		return thickness;
	}
	public void setThickness(int thickness) {
		if (this.thickness == thickness)
			return;
		this.thickness = thickness;
		if (getVisible()) {
			callInit();
			layoutChildren();
		}
	}
	@Override
	public int getChildOffsetX(String id) {
		if (this.first != null && OH.eq(first.getPortletId(), id))
			return 0;
		if (this.second != null && OH.eq(second.getPortletId(), id))
			return getWidth() - second.getWidth();
		return -1;
	}
	@Override
	public int getChildOffsetY(String id) {
		if (this.first != null && OH.eq(first.getPortletId(), id))
			return 0;
		if (this.second != null && OH.eq(second.getPortletId(), id))
			return getHeight() - second.getHeight();
		return -1;
	}
	public void setColor(String color) {
		if (OH.eq(this.color, color))
			return;
		this.color = color;
		if (getVisible()) {
			callInit();
			layoutChildren();
		}

	}
	public String getColor() {
		return color;
	}
	public void setHoverColor(String hoverColor) {
		if (OH.eq(this.hoverColor, hoverColor))
			return;
		this.hoverColor = hoverColor;
		if (getVisible()) {
			callInit();
			layoutChildren();
		}
	}
	public String getHoverColor() {
		return hoverColor;
	}
	@Override
	public void getUsedColors(Set<String> sink) {
		if (color != null)
			sink.add(color);
		if (hoverColor != null)
			sink.add(hoverColor);
	}
	public void rotate(boolean clockwise) {
		setVertical(!this.isVertical);
		if (this.isVertical == clockwise)
			swap(false);
	}
	public int getSize() {
		return this.isVertical ? getWidth() : getHeight();
	}
	public boolean isAtStart(double currentOffset) {
		return currentOffset * getSize() <= Math.max(getThickness(), 1);
	}
	public boolean isAtEnd(double currentOffset) {
		return currentOffset * getSize() >= getSize() - Math.max(getThickness(), 1);
	}

	public boolean isOffsetClipped() {
		return offsetClipped;
	}

	public void setOffsetClipped(boolean offsetClipped) {
		this.offsetClipped = offsetClipped;
	}

	// ensure this runs after alignment has been updated
	public void setCurrentAsDefault() {
		// read from layout: should take as is
		// via user setting: should subtract from size
		if (align == 2) {
			if (this.offsetPx < 0)
				restorePx = MH.abs(this.offsetPx);
			else
				restorePx = getSize() - offsetPx;
		} else
			restorePx = this.offsetPx;
		this.prevSize = getSize();
	}

	public boolean hasSpace() {
		return getSize() >= this.restorePx;
	}

	public void restoreCurrentOffsetToDefault() {
		// TODO maybe leave the assignments to AWDP
		if (this.align != 1) {
			this.offset = this.restorePx * 1.0 / getSize();
			if (this.align == 2) {
				this.offset = 1 - this.offset;
			}
		}
		// this is the previous size
		this.currentSize = getSize();
		this.fireOnDividerRestored(this);
	}

	private void reverseDefaultOffset() {
		this.restorePx = prevSize - restorePx;
	}

	public void setAlign(int i) {
		// called from AmiWebDividerPortlet
		if (i == this.align)
			return;
		// 1 for ratio, 0 left/top, 2 right/bottom
		if (i == 2) {
			// reverse when going into right/bottom config
			reverseDefaultOffset();
		} else if (this.align == 2) {
			// need reverse when going from right/bottom to another config
			reverseDefaultOffset();
		}
		this.align = i;
	}

	private void preparePanelsAndDivider() {
		int fwidth, fheight;
		int swidth, sheight;
		int size = getSize();
		//		int mid = (int) MH.round(size * offset, BigDecimal.ROUND_HALF_EVEN); // this is offset px from the left of the window where the divider will be placed
		int mid = (int) MH.round(size * offset, MH.ROUND_HALF_EVEN); // this is offset px from the left of the window where the divider will be placed
		int thickness = getThickness();
		int t = thickness / 2;
		// this is the offset for left/top divider, also used to calculate panel size
		int leftTop = mid - t;
		if (isVertical) {
			fwidth = leftTop;
			fheight = getHeight();
			swidth = size - mid - t;
			sheight = getHeight();
		} else {
			fwidth = getWidth();
			fheight = leftTop;
			swidth = getWidth();
			sheight = size - mid - t;
		}
		if (first != null) {
			first.setSize(fwidth, fheight);
		}
		if (second != null) {
			second.setSize(swidth, sheight);
		}
		callJsFunction("setDividerLocation").addParam(leftTop).end();

	}

	public void setSnapped(boolean isSnapped) {
		if (this.snapped == isSnapped)
			return;
		this.snapped = isSnapped;
	}
	public boolean isSnapped() {
		return this.snapped;
	}

	public void setInEditMode(boolean mode) {
		this.inEditMode = mode;
	}

	public boolean getInEditMode() {
		return this.inEditMode;
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean moveable) {
		this.movable = moveable;
	}
}
