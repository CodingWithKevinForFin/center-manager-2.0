package com.f1.suite.web.portal.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.form.WebAbsoluteLocation;
import com.f1.utils.CH;
import com.f1.utils.IntArrayList;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.TwoDimensionArray;

public class GridPortlet extends AbstractPortletContainer {

	private static final Logger log = LH.get();
	final private TwoDimensionArray<InnerPortlet> portlets = new TwoDimensionArray<GridPortlet.InnerPortlet>(0, 0);
	final private Map<String, InnerPortlet> portletsById = new HashMap<String, InnerPortlet>();
	final private IntArrayList constraintRowSizes = new IntArrayList(10);
	final private IntArrayList constraintColSizes = new IntArrayList(10);
	final private IntArrayList rowOffsets = new IntArrayList(10);
	final private IntArrayList colOffsets = new IntArrayList(10);
	private String cssClass;
	private String cssStyle;
	private boolean cssClassChanged;
	private boolean anyChanged;
	public static final PortletSchema<GridPortlet> SCHEMA = new BasicPortletSchema<GridPortlet>("GridPortlet", "GridPortlet", GridPortlet.class, true, true);
	private static final int NO_CONSTRAINT = -1;

	public GridPortlet(PortletConfig config) {
		super(config);
	}

	public <T extends Portlet> T addChildOverlay(T child, WebAbsoluteLocation horizontal, WebAbsoluteLocation vertical, int zIndex) {
		InnerPortlet innerPortlet = new InnerPortlet(child, horizontal, vertical, zIndex);
		portletsById.put(child.getPortletId(), innerPortlet);
		super.addChild(child);
		return child;
	}

	public <T extends Portlet> T addChild(T child, int x, int y) {
		this.addChild(child, x, y, 1, 1);
		return child;
	}

	public <T extends Portlet> InnerPortlet addChild(T child, int x, int y, int w, int h) {
		if (w < 1)
			throw new IllegalArgumentException("width must be positive number: " + w);
		if (h < 1)
			throw new IllegalArgumentException("height must be positive number: " + h);
		return addChild(child, new Position(x, y, w, h));
	}
	public <T extends Portlet> InnerPortlet addChild(T child, Position position) {
		final int gx = position.getGridx();
		final int gy = position.getGridy();
		final int gxx = gx + position.getGridWidth();
		final int gyy = gy + position.getGridHeight();
		for (int x = gx; x < gxx; x++)
			for (int y = gy; y < gyy; y++)
				if (portlets.getNoThrow(x, y) != null)
					throw new RuntimeException("portlet exists at grid location: " + x + "," + y);
		final InnerPortlet innerPortlet = new InnerPortlet(child, position);
		if (gxx > portlets.getWidth()) {
			portlets.setWidth(gxx);
			while (constraintColSizes.size() < gxx)
				constraintColSizes.add(NO_CONSTRAINT);
			constraintColSizes.setSize(gxx);
		}

		if (gyy > portlets.getHeight()) {
			portlets.setHeight(gyy);
			while (constraintRowSizes.size() < gyy)
				constraintRowSizes.add(NO_CONSTRAINT);
			constraintRowSizes.setSize(gyy);
		}

		for (int x = gx; x < gxx; x++)
			for (int y = gy; y < gyy; y++)
				portlets.set(x, y, innerPortlet);

		portletsById.put(child.getPortletId(), innerPortlet);
		super.addChild(child);
		return innerPortlet;
	}
	public void setRowSize(int rowIndex, int rowSize) {
		if (rowSize < 0)
			throw new RuntimeException("invalid size: " + rowSize);
		this.constraintRowSizes.set(rowIndex, rowSize);
		if (getVisible())
			layoutChildren();
	}
	public void clearRowSizes() {
		for (int i = 0; i < this.constraintRowSizes.size(); i++) {
			constraintRowSizes.set(i, -1);
		}
		flagChanged();
	}

	private void flagChanged() {
		flagPendingAjax();
		if (getVisible())
			layoutChildren();
	}
	public void setColSize(int colIndex, int colSize) {
		if (colSize < 0)
			throw new RuntimeException("invalid size: " + colSize);
		this.constraintColSizes.set(colIndex, colSize);
		if (getVisible())
			layoutChildren();
	}
	public void clearColumnSizes() {
		for (int i = 0; i < this.constraintColSizes.size(); i++) {
			constraintColSizes.set(i, -1);
		}
		flagChanged();
	}

	@Override
	public Portlet removeChild(String child) {
		InnerPortlet innerPortlet = CH.removeOrThrow(portletsById, child);
		if (!innerPortlet.isOverlay()) {
			Position position = innerPortlet.getPosition();
			final int gx = position.getGridx();
			final int gy = position.getGridy();
			final int gxx = gx + position.getGridWidth();
			final int gyy = gy + position.getGridHeight();
			for (int x = gx; x < gxx; x++)
				for (int y = gy; y < gyy; y++)
					portlets.set(x, y, null);
			int x = gxx;
			outer: while (x > 0 && x == portlets.getWidth()) {
				x--;
				for (int i = 0; i < portlets.getHeight(); i++)
					if (portlets.getAt(x, i) != null)
						break outer;
				portlets.removeCol(x);
			}
			int y = gyy;
			outer: while (y > 0 && y == portlets.getHeight()) {
				y--;
				for (int i = 0; i < portlets.getWidth(); i++)
					if (portlets.getAt(i, y) != null)
						break outer;
				portlets.removeRow(y);
			}
		}

		return super.removeChild(child);
	}

	@Override
	protected void initJs() {
		super.initJs();
		for (InnerPortlet value : this.portletsById.values())
			value.setChanged(true);
		this.cssClassChanged = true;
		int debugDepth;
		if (getManager().getDebugLayout()) {
			debugDepth = 0;
			for (PortletContainer i = getParent(); i != null; i = i.getParent())
				if (i instanceof GridPortlet)
					debugDepth++;
		} else
			debugDepth = -1;
		callJsFunction("init").addParam(debugDepth).end();
		callJsFunction("setStyle").addParamQuoted(getCssClass()).addParamQuoted(getCssStyle()).end();
	}
	@Override
	public void addChild(Portlet child) {
		if (getChildrenCount() == 0)
			addChild(child, 0, 0);
		else
			throw new UnsupportedOperationException("multiple children can only be added programtically");
	}
	@Override
	public void bringToFront(String portletId) {
	}

	@Override
	public boolean hasVacancy() {
		return false;
	}

	@Override
	public void setSize(int width, int height) {
		calcGrid();
		super.setSize(width, height);
	}

	@Override
	protected void layoutChildren() {
		calcGrid();
		if (getVisible()) {
			for (InnerPortlet value : this.portletsById.values()) {
				if (value.isOverlay()) {
					final WebAbsoluteLocation hpos = value.getOverlayHorizontalPosition();
					final WebAbsoluteLocation vpos = value.getOverlayVerticalPosition();
					hpos.setOuterSize(this.getWidth());
					vpos.setOuterSize(this.getHeight());
					value.setOuterLeftTopWidthHeight(hpos.getRealizedStart(), vpos.getRealizedStart(), hpos.getRealizedSize(), vpos.getRealizedSize());
				} else {
					Position pos = value.getPosition();
					final int left = colOffsets.get(pos.getGridx());
					final int top = rowOffsets.get(pos.getGridy());
					final int right = colOffsets.get(pos.getGridx() + pos.getGridWidth());
					final int bottom = rowOffsets.get(pos.getGridy() + pos.getGridHeight());
					value.setOuterLeftTopWidthHeight(left, top, right - left, bottom - top);
				}
				makeChildVisible(value.getPortlet(), true);
			}
		}
		flagPendingAjax();
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			if (portlets.getWidth() > 0 && portlets.getHeight() > 0) {
				JsFunction js = null;
				if (anyChanged) {
					for (InnerPortlet value : this.portletsById.values()) {
						if (!value.isChanged())
							continue;
						if (js == null) {
							js = new JsFunction(getManager().getPendingJs());
							js.getStringBuilder().append("{var t=").append(getJsObjectName()).append(";\n");
						}
						Portlet portlet = value.getPortlet();
						js.reset("t", "setGridLocation").addParamQuoted(portlet.getPortletId()).addParam(value.getPortletLeft()).addParam(value.getPortletTop())
								.addParam(value.getPortletWidth()).addParam(value.getPortletHeight()).addParam(value.getZIndex()).addParamQuoted(value.getCssStyle())
								.addParamQuoted(value.getOverlayCssStyle()).addParamQuoted(value.getOverlayHtml()).addParamQuoted(value.getDebug()).end();
						value.setChanged(false);
					}
					anyChanged = false;
				}
				if (cssClassChanged) {
					if (js == null) {
						js = new JsFunction(getManager().getPendingJs());
						getManager().getPendingJs().append("{var t=").append(getJsObjectName()).append(";\n");
					}
					js.reset("t", "setStyle").addParamQuoted(getCssClass()).addParamQuoted(getCssStyle()).end();
					cssClassChanged = false;
				}
				if (js != null)
					getManager().getPendingJs().append("}\n");
			}
		}
	}
	private void calcGrid() {
		calcOffsets(this.constraintColSizes, getWidth(), this.colOffsets);
		calcOffsets(this.constraintRowSizes, getHeight(), this.rowOffsets);
	}

	//sink will be populated with the offsets.
	private void calcOffsets(IntArrayList constraintSizes, int totalSize, IntArrayList sink) {
		sink.setSize(constraintSizes.size() + 1);
		int expandingCnt = 0;
		int constSize = 0;

		//first sweep, determine space remaining for expanding cells
		for (int i = 0; i < constraintSizes.size(); i++) {
			int value = constraintSizes.getInt(i);
			if (value != NO_CONSTRAINT) {//is this a constant cell size?
				constSize += value;
				sink.set(i, value);//temporarily store the size here
			} else {
				expandingCnt++;
			}
		}

		//set the sizes for the expanding columns
		if (expandingCnt > 0) {
			int totSizeForExpansion = totalSize - constSize;
			int expandingCellSize = totSizeForExpansion / expandingCnt;
			int remainder = totSizeForExpansion - expandingCellSize * expandingCnt;
			for (int i = 0, expandingCellsRemaining = expandingCnt; expandingCellsRemaining > 0; i++)
				if (constraintSizes.getInt(i) == NO_CONSTRAINT) //is this an expanding cell?
					if (--expandingCellsRemaining == 0)
						sink.set(i, expandingCellSize + remainder);//Arbitrarily, the last cell gets the remainder
					else
						sink.set(i, expandingCellSize);
		}

		//turn sizes into offsets
		int position = 0;
		for (int i = 0; i < constraintSizes.size(); i++) {
			int w = sink.get(i);
			sink.set(i, position);
			position += w;
		}
		sink.set(constraintSizes.size(), position);

	}

	public class InnerPortlet {
		private Portlet portlet;
		private final Position position;
		private final int zIndex;
		private int outerLeft;
		private int outerTop;
		private int paddingL, paddingT, paddingR, paddingB;
		private String cssStyle;
		private String overlayCssStyle;
		private String overlayHtml;
		private int outerWidth;
		private int outerHeight;
		private boolean changed = true;
		final private WebAbsoluteLocation verticalPos;
		final private WebAbsoluteLocation horizontalPos;

		public InnerPortlet(Portlet portlet, Position position) {
			this.portlet = portlet;
			this.position = position;
			this.zIndex = 0;
			this.verticalPos = null;
			this.horizontalPos = null;
			GridPortlet.this.anyChanged = true;
		}
		public String getDebug() {
			if (isOverlay())
				return zIndex + "@" + horizontalPos.toString() + "," + verticalPos.toString();
			else
				return "[" + position.getGridx() + "," + position.getGridy() + ":" + position.getGridWidth() + "x" + position.getGridHeight() + "]";
		}
		public int getZIndex() {
			return zIndex;
		}
		public WebAbsoluteLocation getOverlayHorizontalPosition() {
			return this.horizontalPos;
		}
		public WebAbsoluteLocation getOverlayVerticalPosition() {
			return this.verticalPos;
		}
		public boolean isOverlay() {
			return this.zIndex > 0;
		}
		public InnerPortlet(Portlet portlet, WebAbsoluteLocation horizontal, WebAbsoluteLocation vertical, int zIndex) {
			OH.assertGt(zIndex, 0);
			this.portlet = portlet;
			this.position = null;
			this.zIndex = zIndex;
			this.verticalPos = vertical;
			this.horizontalPos = horizontal;
			GridPortlet.this.anyChanged = true;
		}
		public int getPortletHeight() {
			return this.portlet.getHeight();
		}
		public int getPortletWidth() {
			return this.portlet.getWidth();
		}
		public int getPortletTop() {
			return this.outerTop + this.paddingT;
		}
		public int getPortletLeft() {
			return this.outerLeft + this.paddingL;
		}
		public Portlet getPortlet() {
			return portlet;
		}
		private Position getPosition() {
			return position;
		}
		public int getOuterLeft() {
			return outerLeft;
		}
		//		private void setOuterLeft(int left) {
		//			if (this.outerLeft == left)
		//				return;
		//			this.outerLeft = left;
		//		}
		public int getOuterTop() {
			return outerTop;
		}
		//		private void setOuterTop(int top) {
		//			if (this.outerTop == top)
		//				return;
		//			this.outerTop = top;
		//			updatePortletSize();
		//		}
		//		private void setOuterSize(int width, int height) {
		//			if (this.outerWidth == width && this.outerHeight == height)
		//				return;
		//			this.outerHeight = height;
		//			this.outerWidth = width;
		//			updatePortletSize();
		//		}
		private void setOuterLeftTopWidthHeight(int left, int top, int width, int height) {
			if (this.outerLeft == left && this.outerTop == top && this.outerWidth == width && this.outerHeight == height)
				return;
			this.outerLeft = left;
			this.outerTop = top;
			this.outerHeight = height;
			this.outerWidth = width;
			updatePortletSize();
		}
		public int getPaddingB() {
			return paddingB;
		}
		public InnerPortlet setPaddingB(int paddingB) {
			paddingB = Math.max(0, paddingB);
			if (this.paddingB == paddingB)
				return this;
			this.paddingB = paddingB;
			updatePortletSize();
			return this;
		}
		public int getPaddingR() {
			return paddingR;
		}
		public InnerPortlet setPaddingR(int paddingR) {
			paddingR = Math.max(0, paddingR);
			if (this.paddingR == paddingR)
				return this;
			this.paddingR = paddingR;
			updatePortletSize();
			return this;
		}
		public int getPaddingT() {
			return paddingT;
		}
		public InnerPortlet setPaddingT(int paddingT) {
			paddingT = Math.max(0, paddingT);
			if (this.paddingT == paddingT)
				return this;
			this.paddingT = paddingT;
			updatePortletSize();
			return this;
		}
		public int getPaddingL() {
			return paddingL;
		}
		public InnerPortlet setPaddingL(int paddingL) {
			paddingL = Math.max(0, paddingL);
			if (this.paddingL == paddingL)
				return this;
			this.paddingL = paddingL;
			updatePortletSize();
			return this;
		}
		public String getCssStyle() {
			return cssStyle;
		}
		public InnerPortlet setCssStyle(String cssStyle) {
			if (OH.eq(this.cssStyle, cssStyle))
				return this;
			this.cssStyle = cssStyle;
			this.changed = true;
			GridPortlet.this.anyChanged = true;
			flagChanged();
			return this;
		}
		public InnerPortlet setPadding(int padding) {
			return this.setPadding(padding, padding, padding, padding);
		}
		public InnerPortlet setPadding(int top, int right, int bottom, int left) {
			top = Math.max(0, top);
			bottom = Math.max(0, bottom);
			left = Math.max(0, left);
			right = Math.max(0, right);
			if (this.paddingT == top && this.paddingR == right && this.paddingB == bottom && this.paddingL == left)
				return this;
			this.paddingT = top;
			this.paddingR = right;
			this.paddingB = bottom;
			this.paddingL = left;
			updatePortletSize();
			return this;
		}
		private void updatePortletSize() {
			this.portlet.setSize(Math.max(0, this.outerWidth - this.paddingL - this.paddingR), Math.max(0, this.outerHeight - this.paddingT - this.paddingB));
			this.changed = true;
			GridPortlet.this.anyChanged = true;
			flagChanged();
		}
		public String getPortletId() {
			return this.portlet.getPortletId();
		}
		public InnerPortlet setPortlet(Portlet portlet) {
			if (this.portlet == portlet)
				return this;
			if (portlet == null)
				throw new NullPointerException("portlet");
			Portlet old = this.portlet;
			this.portlet = portlet;
			portletsById.remove(old);
			portletsById.put(portlet.getPortletId(), this);
			replaceChild(old.getPortletId(), portlet);
			updatePortletSize();
			return this;
		}
		private void setPortletInner(Portlet portlet) {
			if (this.portlet == portlet)
				return;
			this.portlet = portlet;
		}
		public String getOverlayCssStyle() {
			return overlayCssStyle;
		}
		public void setOverlayCssStyle(String overlayCssStyle) {
			if (SH.isnt(overlayCssStyle))
				overlayCssStyle = null;
			if (OH.eq(this.overlayCssStyle, overlayCssStyle))
				return;
			this.overlayCssStyle = overlayCssStyle;
			this.changed = true;
			GridPortlet.this.anyChanged = true;
			flagChanged();
		}
		public String getOverlayHtml() {
			return overlayHtml;
		}
		public void setOverlayHtml(String overlayHtml) {
			if (SH.isnt(overlayHtml))
				overlayHtml = null;
			if (OH.eq(this.overlayHtml, overlayHtml))
				return;
			this.overlayHtml = overlayHtml;
			this.changed = true;
			GridPortlet.this.anyChanged = true;
			flagChanged();
		}
		public boolean isChanged() {
			return changed;
		}
		public void setChanged(boolean changed) {
			this.changed = changed;
			if (changed)
				GridPortlet.this.anyChanged = true;
		}

	}

	public static class Position {
		private final int gridx;
		private final int gridy;
		private final int gridWidth;
		private final int gridHeight;

		public Position(int gridx, int gridy, int gridWidth, int gridHeight) {
			this.gridx = gridx;
			this.gridy = gridy;
			this.gridWidth = gridWidth;
			this.gridHeight = gridHeight;
		}

		public int getGridx() {
			return gridx;
		}

		public int getGridy() {
			return gridy;
		}

		public int getGridWidth() {
			return gridWidth;
		}

		public int getGridHeight() {
			return gridHeight;
		}
	}

	public static class GridMode {
		public static byte TYPE_CONST = 1;
		public static byte TYPE_PERCENT = 2;

	}

	@Override
	public PortletSchema<GridPortlet> getPortletSchema() {
		return SCHEMA;
	}

	public String getCssClass() {
		return cssClass;
	}

	public GridPortlet setCssClass(String cssClass) {
		if (OH.eq(this.cssClass, cssClass))
			return this;
		this.cssClass = cssClass;
		this.cssClassChanged = true;
		flagChanged();
		return this;
	}
	public String getCssStyle() {
		return cssStyle;
	}

	public GridPortlet setCssStyle(String cssStyle) {
		if (OH.eq(this.cssStyle, cssStyle))
			return this;
		this.cssStyle = cssStyle;
		this.cssClassChanged = true;
		flagChanged();
		return this;
	}

	@Override
	public boolean isCustomizable() {
		return false;
	}

	private int suggestedWidth = -1;
	private int suggestedHeight = -1;

	public void setSuggestedSize(int suggestedWidth, int suggestedHeight) {
		this.suggestedWidth = suggestedWidth;
		this.suggestedHeight = suggestedHeight;
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return suggestedWidth == -1 ? super.getSuggestedWidth(pm) : suggestedWidth;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return suggestedHeight == -1 ? super.getSuggestedHeight(pm) : suggestedHeight;
	}

	private Map<String, Portlet> childrenToSave = new HashMap<String, Portlet>();

	protected void registerChildPortletToBeSaved(String uniqueId, Portlet portlet) {
		CH.putOrThrow(childrenToSave, uniqueId, portlet);
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		Map<String, Object> childConfigs = new HashMap<String, Object>(childrenToSave.size());
		if (!childrenToSave.isEmpty()) {
			for (Entry<String, Portlet> e : childrenToSave.entrySet())
				childConfigs.put(e.getKey(), e.getValue().getConfiguration());
			r.put("children", childConfigs);
		}
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		Map<String, Object> children = (Map<String, Object>) CH.getOr(Caster_Simple.OBJECT, configuration, "children", null);
		if (CH.isntEmpty(children)) {
			for (Entry<String, Tuple2<Portlet, Object>> e : CH.join(childrenToSave, children).entrySet()) {
				String id = e.getKey();
				Portlet child = e.getValue().getA();
				Object config = e.getValue().getB();
				if (child == null)
					sb.append("config for unknown child for grid ").append(getClass().getName()).append(". Unique id: ").append(id);
				else if (config != null) {
					child.init((Map<String, Object>) config, origToNewIdMapping, sb);
				}
			}
		}

	}

	@Override
	public int getChildOffsetX(String id) {
		InnerPortlet p = portletsById.get(id);
		if (p == null)
			return -1;
		return p.outerLeft + p.getPaddingL();
	}

	@Override
	public int getChildOffsetY(String id) {
		InnerPortlet p = portletsById.get(id);
		if (p == null)
			return -1;
		return p.outerTop + p.getPaddingT();
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		return super.onUserKeyEvent(keyEvent);
	}

	public Portlet removeChildAt(int x, int y) {
		return removeChild(getPanelAt(x, y).getPortletId());
	}
	public Portlet getChildAt(int x, int y) {
		return getPanelAt(x, y).getPortlet();
	}
	public InnerPortlet getPanelAt(int x, int y) {
		return portlets.getAt(x, y);
	}

	public int getChildsWidth() {
		return this.portlets.getWidth();
	}
	public int getChildsHeight() {
		return this.portlets.getHeight();
	}
	@Override
	public void replaceChild(String removed, Portlet replacement) {
		super.removeChild(removed);
		InnerPortlet panel = this.portletsById.remove(removed);
		panel.setPortletInner(replacement);
		super.addChild(replacement);
		this.portletsById.put(replacement.getPortletId(), panel);
		panel.updatePortletSize();
	}

	@Override
	public void onUserRequestFocus(MouseEvent e) {
		if (this.getChildrenCount() == 1)
			CH.first(this.getChildren().values()).onUserRequestFocus(e);
	}

}
