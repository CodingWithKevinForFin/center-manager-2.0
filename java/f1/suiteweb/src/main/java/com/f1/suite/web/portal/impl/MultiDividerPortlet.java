/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.IterableAndSize;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.utils.CH;
import com.f1.utils.IntArrayList;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;

public class MultiDividerPortlet extends AbstractPortletContainer {

	public static final PortletSchema<MultiDividerPortlet> SCHEMA = new BasicPortletSchema<MultiDividerPortlet>("MultiDivider", "MultiDividerPortlet", MultiDividerPortlet.class,
			true, true);

	public static final int DEFAULT_THICKNESS = 6;

	private static final double DEFAULT_EXPAND_BIAS = .5;

	private IndexedList<String, Divider> portlets = new BasicIndexedList<String, Divider>();
	private boolean isVertical;//ORIENTATION OF DIVIDER
	private int thickness = DEFAULT_THICKNESS;

	private boolean isCustomizable = true;

	private int currentSize = -1;

	private int offsetPx = 0;
	private int minSize = 10;

	private boolean lockPosition = false;

	public class Divider {
		private Portlet portlet;
		private double weight;
		private int offsetPx = -1;
		private int preferedSize = -1;

		public Divider(Portlet portlet, double weight) {
			this.portlet = portlet;
			this.weight = weight;
		}
		public double getWeight() {
			return weight;
		}
		private void setWeight(double weight) {
			this.weight = weight;
		}
		public Portlet getPortlet() {
			return portlet;
		}
		private void setPortlet(Portlet portlet) {
			this.portlet = portlet;
		}
		public int getOffsetPx() {
			return offsetPx;
		}
		private void setOffsetPx(int offsetPx) {
			this.offsetPx = offsetPx;
		}
		public int getSizePx() {
			return isVertical ? portlet.getWidth() : portlet.getHeight();
		}
		public int getPreferedSizePx() {
			return this.preferedSize;
		}

		public void setPreferedSizePx(int preferedSize) {
			this.preferedSize = preferedSize;
		}
	}

	public MultiDividerPortlet(PortletConfig manager, boolean isVertical) {
		super(manager);
		this.isVertical = isVertical;
	}

	@Override
	public void addChild(Portlet portlet) {
		addChild(portlet, 1);
	}
	public void addChild(Portlet portlet, double weight) {
		this.portlets.add(portlet.getPortletId(), new Divider(portlet, weight));
		normalizeWeights();
		super.addChild(portlet);
		if (getVisible()) {
			this.layoutChildren();
		}
	}
	public void addChild(int position, Portlet portlet, double weight) {
		this.portlets.add(portlet.getPortletId(), new Divider(portlet, weight), position);
		normalizeWeights();
		super.addChild(portlet);
		if (getVisible()) {
			this.layoutChildren();
		}
	}

	@Override
	public void replaceChild(String id, Portlet replacement) {
		Portlet child = super.removeChild(id);
		int position = this.portlets.getPosition(id);
		Divider innerPortlet = this.portlets.remove(id);
		innerPortlet.setPortlet(replacement);
		this.portlets.add(replacement.getPortletId(), innerPortlet, position);
		super.addChild(replacement);
	}

	@Override
	public Portlet removeChild(String id) {
		Portlet r = super.removeChild(id);
		this.portlets.remove(id);
		if (getVisible()) {
			this.layoutChildren();
		}
		return r;
	}

	@Override
	protected void layoutChildren() {
		if (!getVisible())
			return;
		int dividerSpace = this.thickness * (this.portlets.getSize() - 1);

		int width = getWidth();
		int height = getHeight();
		int sizeForPreferred = 0;
		int preferredCount = 0;
		for (Divider i : this.portlets.values())
			if (i.getPreferedSizePx() != -1) {
				sizeForPreferred += i.getPreferedSizePx();
				preferredCount++;
			}
		int panelSpace = (isVertical ? width : height) - dividerSpace;

		double weights[] = new double[this.portlets.getSize()];//negative means absolute size

		double totalWeight = 0;

		boolean entertainPreferred = false;
		if (panelSpace < 0) {//edge condition... no space for anything!
			panelSpace = 0;
		} else if (panelSpace < sizeForPreferred) {//not enough room so everything else goes to zero.
			for (int i = 0; i < this.portlets.getSize(); i++) {
				int size = this.portlets.getAt(i).getPreferedSizePx();
				if (size == -1)
					size = 0;
				weights[i] = size;
				totalWeight += size;
			}
		} else if (preferredCount == this.portlets.getSize()) {//everything is preferred, so nothing is preferred
			for (int i = 0; i < this.portlets.getSize(); i++) {
				int size = this.portlets.getAt(i).getPreferedSizePx();
				weights[i] = size;
				totalWeight += size;
			}
		} else {//enough room for preferred, leave remaining space for others.
			entertainPreferred = true;
			for (int i = 0; i < this.portlets.getSize(); i++) {
				Divider p = this.portlets.getAt(i);
				int size = p.getPreferedSizePx();
				if (size == -1) {
					weights[i] = p.getWeight();
					totalWeight += p.getWeight();
				} else
					panelSpace -= size;
			}
		}
		int offset = 0;
		double weight = 0;
		int preferedOffset = 0;
		for (int i = 0; i < this.portlets.getSize(); i++) {
			Divider p = this.portlets.getAt(i);
			if (entertainPreferred && p.getPreferedSizePx() != -1) {
				preferedOffset += p.getPreferedSizePx();
				int nextOffset = i == this.portlets.getSize() - 1 ? (panelSpace + preferedOffset) : (offset + p.getPreferedSizePx());
				if (isVertical)
					p.getPortlet().setSize(nextOffset - offset, height);
				else
					p.getPortlet().setSize(width, nextOffset - offset);
				p.setOffsetPx(offset + i * thickness);
				offset = nextOffset;
			} else {
				weight += weights[i];
				int nextOffset = preferedOffset + (i == this.portlets.getSize() - 1 ? panelSpace : (int) Math.round(panelSpace * (weight / totalWeight)));
				if (isVertical)
					p.getPortlet().setSize(nextOffset - offset, height);
				else
					p.getPortlet().setSize(width, nextOffset - offset);
				p.setOffsetPx(offset + i * thickness);
				offset = nextOffset;
			}
		}
		makeChildrenVisible(true);
		callInit();
		for (Portlet p : this.getChildren().values())
			callJsAddChild(p);
		JsFunction func = callJsFunction("setPositions");
		JsonBuilder json = func.startJson();
		json.startList();
		for (Entry<String, Divider> t : this.portlets) {
			json.addEntry(t.getValue().getOffsetPx());
		}
		json.endList();
		func.end();
	}
	@Override
	protected void callJsAddChild(Portlet p) {
		callJsFunction("addChild").addParam(portlets.getPosition(p.getPortletId())).addParamQuoted(p.getPortletId()).end();
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
	}

	@Override
	protected void makeChildrenVisible(boolean visible) {
		super.makeChildrenVisible(visible);
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("dividerMoved".equals(callback)) {

			String[] parts = SH.split(',', attributes.get("positions"));
			IntArrayList offsets = new IntArrayList(parts.length + 1);
			for (int i = 0; i < parts.length; i++)
				offsets.add(SH.parseInt(parts[i].trim()) - this.thickness * i);
			offsets.add((isVertical ? getWidth() : getHeight()) - this.thickness * (this.portlets.getSize() - 1));
			for (int i = 0; i < portlets.getSize(); i++) {
				Divider p = portlets.getAt(i);
				int size = offsets.get(i + 1) - offsets.get(i);
				if (p.getPreferedSizePx() != -1)
					p.setPreferedSizePx(size);
				p.setWeight(size);
			}
			normalizeWeights();
			layoutChildren();
			fireOnDividerMoved();
		} else if ("dividerMovingStarted".equals(callback)) {
			callJsFunction("onDividerMovingProcessed").end();
			double currentOffset = ((CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "offset") + thickness / 2) / (isVertical ? getWidth() : getHeight()));
		} else if ("dividerMoving".equals(callback)) {
			callJsFunction("onDividerMovingProcessed").end();
			double currentOffset = ((CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "offset") + thickness / 2) / (isVertical ? getWidth() : getHeight()));
		} else
			super.handleCallback(callback, attributes);

	}

	private void normalizeWeights() {
		double totalWeight = 0;
		for (Divider i : this.portlets.values())
			totalWeight += i.getWeight();
		final double targetWeight = this.portlets.getSize();
		if (totalWeight == targetWeight)
			return;
		double ratio = targetWeight / totalWeight;
		for (Divider i : this.portlets.values())
			i.setWeight(i.getWeight() * ratio);
	}

	public double[] getWeights() {
		double[] r = new double[portlets.getSize()];
		for (int i = 0; i < portlets.getSize(); i++)
			r[i] = portlets.getAt(i).getWeight();
		return r;
	}
	public void setWeights(double[] weights) {
		OH.assertEq(weights.length, portlets.getSize());
		for (int i = 0; i < portlets.getSize(); i++)
			portlets.getAt(i).setWeight(weights[i]);
		normalizeWeights();
		layoutChildren();
	}
	public int[] getSizes() {
		int[] r = new int[portlets.getSize()];
		if (isVertical)
			for (int i = 0; i < portlets.getSize(); i++)
				r[i] = portlets.getAt(i).getSizePx();
		else
			for (int i = 0; i < portlets.getSize(); i++)
				r[i] = portlets.getAt(i).getSizePx();
		return r;
	}

	private final List<MultiDividerListener> listeners = new ArrayList<MultiDividerListener>();

	private String color;

	private void fireOnDividerMoved() {
		for (MultiDividerListener i : listeners)
			i.onDividerMoved(this);
	}

	public void addListener(MultiDividerListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(MultiDividerListener listener) {
		this.listeners.remove(listener);
	}
	public void setVertical(boolean vertical) {
		if (this.isVertical == vertical)
			return;
		this.isVertical = vertical;
		if (getVisible()) {
			makeChildrenVisible(false);
			callInit();
			layoutChildren();
		}
	}

	private void callInit() {
		callJsFunction("init").addParam(isVertical).addParam(thickness).addParam(lockPosition).addParamQuoted(color).addParam(this.minSize).end();
	}
	@Override
	protected void initJs() {
		super.initJs();
		callInit();

	}

	@Override
	public PortletSchema<MultiDividerPortlet> getPortletSchema() {
		return SCHEMA;
	}

	public double getWeight(int position) {
		return this.portlets.getAt(position).getWeight() / this.portlets.getSize();
	}

	public void setOffsetFromTopPx(int offset) {
		if (offset <= 0)
			throw new IllegalArgumentException("offset must be positive: " + offset);
		this.offsetPx = offset;
		layoutChildren();
	}
	public void setOffsetFromBottomPx(int offset) {
		if (offset <= 0)
			throw new IllegalArgumentException("offset must be positive: " + offset);
		this.offsetPx = -offset;
		layoutChildren();
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		List<Map<String, Object>> children = new ArrayList<Map<String, Object>>(this.portlets.getSize());
		for (Entry<String, Divider> p : this.portlets) {
			Map<String, Object> m = CH.m("p", p.getKey(), "w", p.getValue().getWeight());
			children.add(m);
		}
		r.put("ch", children);
		r.put("isVertical", this.isVertical);
		r.put("lk", this.lockPosition);
		r.put("th", this.thickness);
		r.put("color", this.color);
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);

		List<Map<String, Object>> children = (List<Map<String, Object>>) configuration.get("ch");
		int position = 0;
		for (Map<String, Object> i : children) {
			String portletId = CH.getOrThrow(Caster_String.INSTANCE, i, "p");
			double weight = CH.getOrThrow(Caster_Double.PRIMITIVE, i, "w");
			Portlet portlet = getManager().getPortlet(portletId);
			Divider p = new Divider(portlet, weight);
			super.addChild(p.getPortlet());
			this.portlets.add(portlet.getPortletId(), p);
		}
		this.isVertical = CH.getOrThrow(Caster_Boolean.INSTANCE, configuration, "isVertical");
		this.lockPosition = CH.getOr(Caster_Boolean.INSTANCE, configuration, "lk", false);
		this.thickness = CH.getOr(Caster_Integer.INSTANCE, configuration, "th", DEFAULT_THICKNESS);
		this.color = CH.getOr(Caster_String.INSTANCE, configuration, "color", null);
	}

	public static class HBuilder extends AbstractPortletBuilder<MultiDividerPortlet> {

		public static final String ID = "hdiv";

		public HBuilder() {
			super(MultiDividerPortlet.class);
			setIcon("portlet_icon_hdivider");
		}

		@Override
		public MultiDividerPortlet buildPortlet(PortletConfig portletConfig) {
			MultiDividerPortlet r = new MultiDividerPortlet(portletConfig, false);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Horizontal Divider";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	public static class VBuilder extends AbstractPortletBuilder<MultiDividerPortlet> {

		public static final String ID = "vdiv";

		public VBuilder() {
			super(MultiDividerPortlet.class);
			setIcon("portlet_icon_vdivider");
		}

		@Override
		public MultiDividerPortlet buildPortlet(PortletConfig portletConfig) {
			MultiDividerPortlet r = new MultiDividerPortlet(portletConfig, true);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Vertical Divider";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void bringToFront(String portletId) {
		//nothing to do... all children are already in front
	}

	@Override
	public boolean hasVacancy() {
		return true;
	}

	@Override
	public boolean isCustomizable() {
		return isCustomizable;
	}

	public void setIsCustomizable(boolean isCustomizable) {
		this.isCustomizable = isCustomizable;
	}

	public boolean isVertical() {
		return this.isVertical;
	}
	public Portlet getAt(int position) {
		return this.portlets.getAt(position).getPortlet();
	}
	public void setLockPosition(boolean lockPosition) {
		if (this.lockPosition == lockPosition)
			return;
		this.lockPosition = lockPosition;
		if (getVisible()) {
			makeChildrenVisible(false);
			callInit();
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
			makeChildrenVisible(false);
			callInit();
			layoutChildren();
		}
	}
	@Override
	public int getChildOffsetX(String id) {
		final Divider innerPortlet = this.portlets.get(id);
		if (innerPortlet == null)
			return -1;
		return !isVertical ? 0 : innerPortlet.getOffsetPx();
	}
	@Override
	public int getChildOffsetY(String id) {
		final Divider innerPortlet = this.portlets.get(id);
		if (innerPortlet == null)
			return -1;
		return isVertical ? 0 : innerPortlet.getOffsetPx();
	}

	public Divider getPortletAt(int position) {
		return this.portlets.getAt(position);
	}
	public Divider getPortlet(String portletId) {
		return this.portlets.get(portletId);
	}
	public int getPortletPosition(String portletId) {
		return this.portlets.getPosition(portletId);
	}

	public void setOffsets(MultiDividerPortlet dividerPortlet) {
		OH.assertEq(dividerPortlet.getChildrenCount(), getChildrenCount());
		OH.assertEq(dividerPortlet.isVertical(), isVertical);
		if (isVertical)
			OH.assertEq(dividerPortlet.getWidth(), getWidth());
		else
			OH.assertEq(dividerPortlet.getHeight(), getHeight());
		for (int i = 0; i < this.portlets.getSize(); i++) {
			Divider t = portlets.getAt(i);
			Divider o = dividerPortlet.portlets.getAt(i);
			t.setOffsetPx(o.getOffsetPx());
			t.setWeight(o.getWeight());
			t.setPreferedSizePx(o.getPreferedSizePx());
			if (isVertical)
				t.getPortlet().setSize(o.getPortlet().getWidth(), t.getPortlet().getHeight());
			else
				t.getPortlet().setSize(t.getPortlet().getWidth(), o.getPortlet().getHeight());
		}
		layoutChildren();
	}

	public void setPreferedSizeNoFire(int position, int size) {
		getPortletAt(position).setPreferedSizePx(size);
	}
	public void setPreferedSize(int position, int size) {
		getPortletAt(position).setPreferedSizePx(size);
		layoutChildren();
	}
	public void setWeight(int position, double weight) {
		getPortletAt(position).setWeight(weight);
	}
	public IterableAndSize<Divider> getChildrenInOrder() {
		return this.portlets.values();
	}

	public Portlet removeAt(int i) {
		return this.removeChild(getAt(i).getPortletId());
	}

	public void setColor(String color) {
		if (OH.eq(this.color, color))
			return;
		this.color = color;
		if (getVisible()) {
			makeChildrenVisible(false);
			callInit();
			layoutChildren();
		}

	}
	public String getColor() {
		return color;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		if (this.minSize == minSize)
			return;
		this.minSize = minSize;
		layoutChildren();
	}

}
