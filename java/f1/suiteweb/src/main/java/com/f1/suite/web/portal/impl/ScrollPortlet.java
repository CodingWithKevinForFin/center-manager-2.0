package com.f1.suite.web.portal.impl;

import java.util.HashMap;
import java.util.Map;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public class ScrollPortlet extends AbstractPortletContainer {
	public static final PortletSchema<ScrollPortlet> SCHEMA = new BasicPortletSchema<ScrollPortlet>("ScrollPortlet", "ScrollPortlet", ScrollPortlet.class, true, true);
	private static final String JSFUNCTION_SET_SCROLL = "setScroll";
	private static final String JSFUNCTION_SET_INNER_SIZE = "setInnerSize";
	private static final String JSFUNCTION_SET_ALIGN = "setAlign";
	private static final String JSFUNCTION_SET_OPTIONS = "setOptions";
	private static final String JSFUNCTION_SET_SCROLLBARWIDTH = "setScrollBarWidth";
	private static final String CALLBACK_ONSCROLL = "onscroll";
	private static final String CONFIG_LEFT = "left";
	private static final String CONFIG_TOP = "top";
	private static final String CONFIG_INNER_HEIGHT = "innerHeight";
	private static final String CONFIG_INNER_WIDTH = "innerWidth";
	private static final String CONFIG_INNER_MINHEIGHT = "innerMinHeight";
	private static final String CONFIG_INNER_MINWIDTH = "innerMinWidth";
	private static final String CONFIG_INNER_MAXHEIGHT = "innerMaxHeight";
	private static final String CONFIG_INNER_MAXWIDTH = "innerMaxWidth";
	private static final String CONFIG_VERTICAL_ALIGN = "valign";
	private static final String CONFIG_HORIZONTAL_ALIGN = "halign";
	public static final String CONFIG_CHILD = "child";
	public static final int NO_CONSTRAINT = -1;
	public static final byte ALIGN_STRETCH_RELATIVE = 0;
	public static final byte ALIGN_LEFT_TOP = 1;
	public static final byte ALIGN_CENTER_MIDDLE = 2;
	public static final byte ALIGN_RIGHT_BOTTOM = 3;
	public static final String OPTION_BG_CL = "bgcl";
	public static final String OPTION_GRIP_CL = "gripcl";
	public static final String OPTION_TRACK_CL = "trackcl";
	public static final String OPTION_BTN_CL = "btncl";
	public static final String OPTION_ICONS_CL = "iconscl";
	public static final String OPTION_BDR_CL = "bdrcl";
	public static final String OPTION_CORNER_CL = "cornercl";
	public static final String OPTION_HIDE_AW = "hideAw";
	public static final String OPTION_SCR_BDR_RD = "borderRad";
	private Portlet innerPortlet;
	private boolean flagUpdateInnerPortletSize = false;
	private boolean flagUpdateInnerPortletSizeJs = false;
	private boolean flagUpdateScrollJs = false;
	private boolean flagUpdateAlignJs = false;
	private int innerWidth;
	private int innerHeight;
	private int innerLeft;
	private int innerTop;
	private int innerMinWidth;
	private int innerMaxWidth;
	private int innerMinHeight;
	private int innerMaxHeight;
	private double clipHorizontal;
	private double clipVertical;
	private int scrollBarWidth;

	private Map<String, Object> options;
	private Map<String, Object> optionsChanges;

	private byte verticalAlign;
	private byte horizontalAlign;
	private boolean hasVerticalScrollbar = false;
	private boolean hasHorzontalScrollbar = false;
	private boolean flagUpdateHorizontalAlign = false;
	private boolean flagUpdateVerticalAlign = false;
	private boolean flagUpdateSize = false;
	private boolean flagUpdateScrollBarWidth = false;
	private boolean flagNeedsUpdate = false;
	private boolean flagUpdateOptionsChanged = false;

	private String bgColor = null;

	public ScrollPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		this.innerWidth = NO_CONSTRAINT;
		this.innerHeight = NO_CONSTRAINT;
		this.innerMinWidth = NO_CONSTRAINT;
		this.innerMaxWidth = NO_CONSTRAINT;
		this.innerMinHeight = NO_CONSTRAINT;
		this.innerMaxHeight = NO_CONSTRAINT;
		this.setVerticalAlign(ALIGN_LEFT_TOP);
		this.setHorizontalAlign(ALIGN_LEFT_TOP);
		this.setScrollBarWidth(15); //TODO update this with default
		this.options = new HashMap<String, Object>();
		this.optionsChanges = new HashMap<String, Object>();
	}
	protected String portletToConfigSaveId(Portlet portlet) {
		return portlet.getPortletId();
	}
	protected Portlet configSaveIdToPortlet(Map<String, String> origToNewIdMapping, String portletId) {
		return getManager().getPortlet(origToNewIdMapping.get(portletId));
	}
	public ScrollPortlet(Portlet innerPortlet, PortletConfig portletConfig) {
		this(portletConfig);
		this.addChild(innerPortlet);
	}

	public void setInnerPortlet(Portlet child) {
		this.addChild(child);
	}
	public Portlet getInnerPortlet() {
		return this.innerPortlet;
	}
	public void removeInnerPortlet() {
		this.removeChild(this.innerPortlet.getPortletId());
	}
	@Override
	public void addChild(Portlet child) {
		//Has inner portlet remove it
		if (this.innerPortlet != null) {
			this.removeChild(this.innerPortlet.getPortletId());
		}
		this.innerPortlet = child;
		this.setInnerMinWidth(this.innerPortlet.getWidth());
		this.setInnerMinHeight(this.innerPortlet.getHeight());
		super.addChild(this.innerPortlet);

		if (flagUpdateInnerPortletSize == true) {
			this.innerPortlet.setSize(this.innerWidth, this.innerHeight);
			this.flagUpdateInnerPortletSize = false;
		}
	}

	@Override
	public Portlet removeChild(String childId) {
		if (this.innerPortlet != null && childId != null && SH.equals(this.innerPortlet.getPortletId(), childId))
			this.innerPortlet = null;
		return super.removeChild(childId);
	}

	@Override
	protected void layoutChildren() {
		if (getVisible())
			if (this.innerPortlet != null)
				makeChildVisible(this.innerPortlet, true);
		flagPendingAjax();
	}

	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}

	@Override
	protected void initJs() {
		super.initJs();
		this.flagUpdateInnerPortletSizeJs = true;
		this.flagUpdateScrollJs = true;
		this.flagUpdateAlignJs = true;
		this.flagUpdateScrollBarWidth = true;
		this.flagUpdateOptionsChanged = true;
		this.optionsChanges.clear();
		this.optionsChanges.putAll(this.options);
	}

	boolean flagUpdateInnerHeight = false;
	boolean flagUpdateInnerWidth = false;

	private void updateInnerHeight() {
		int newHeight = this.getHeight();
		// Constrain for max height
		if (this.innerMaxHeight != NO_CONSTRAINT && this.innerMaxHeight < newHeight)
			newHeight = this.innerMaxHeight;
		// Constrain for min height
		if (this.innerMinHeight != NO_CONSTRAINT && this.innerMinHeight > newHeight)
			newHeight = this.innerMinHeight;
		// Account for horizontal scrollbar
		if (this.hasHorzontalScrollbar && (this.innerMinHeight == NO_CONSTRAINT || newHeight > this.innerMinHeight)) {
			newHeight = MH.max(this.innerMinHeight, newHeight - this.getScrollBarWidth());
		}
		this.setInnerHeight(newHeight);

	}
	private void updateInnerWidth() {
		int newWidth = this.getWidth();

		// Constrain for max width
		if (this.innerMaxWidth != NO_CONSTRAINT && this.innerMaxWidth < newWidth)
			newWidth = this.innerMaxWidth;
		// Constrain for min width
		if (this.innerMinWidth != NO_CONSTRAINT && this.innerMinWidth > newWidth)
			newWidth = this.innerMinWidth;
		// Account for vertical scrollbar
		if (this.hasVerticalScrollbar && (this.innerMinWidth == NO_CONSTRAINT || newWidth > this.innerMinWidth)) {
			newWidth = MH.max(this.innerMinWidth, newWidth - this.getScrollBarWidth());
		}
		this.setInnerWidth(newWidth);
	}

	public void flagUpdateStyle() {
		this.flagNeedsUpdate = true;
		this.flagUpdateScrollBarWidth = true;
		this.flagPendingAjax();
	}
	private void update() {

		this.hasHorzontalScrollbar = false;
		this.hasVerticalScrollbar = false;
		if (this.flagUpdateInnerHeight || this.flagUpdateSize)
			updateInnerHeight();
		if (this.flagUpdateInnerWidth || this.flagUpdateSize)
			updateInnerWidth();
		if (this.flagUpdateSize || this.flagUpdateInnerHeight || this.flagUpdateInnerWidth)
			updateScrollBar();
		if (this.flagUpdateHorizontalAlign)
			updateInnerLeft();
		if (this.flagUpdateVerticalAlign)
			updateInnerTop();
		updateJsFlags();
		this.flagUpdateInnerHeight = false;
		this.flagUpdateInnerWidth = false;
		this.flagUpdateSize = false;
		this.flagUpdateVerticalAlign = false;
		this.flagUpdateHorizontalAlign = false;

	}
	private void updateJsFlags() {
		if (this.flagUpdateInnerHeight || this.flagUpdateInnerWidth) {
			if (innerPortlet == null)
				this.flagUpdateInnerPortletSize = true;
			else
				this.innerPortlet.setSize(this.innerWidth, this.innerHeight);
			this.flagUpdateInnerPortletSizeJs = true;

		}
	}
	private void updateScrollBar() {
		// Check horizontal scrollbar
		this.hasHorzontalScrollbar = this.innerWidth > this.getWidth();
		// Needs to update the height to account for the bar width
		if (this.hasHorzontalScrollbar)
			this.updateInnerHeight();

		// Check vertical scrollbar
		this.hasVerticalScrollbar = this.innerHeight > this.getHeight();
		// Needs to update the width to account for the bar width
		if (this.hasVerticalScrollbar)
			this.updateInnerWidth();

		// If the horizontal scrollbar was false initially and vertical scroll bar is true
		//	, need to double check the horizontal scrollbar
		if (this.hasHorzontalScrollbar == false && this.hasVerticalScrollbar) {
			// Check horizontal scrollbar
			this.hasHorzontalScrollbar = this.innerWidth > this.getWidth();
			// Needs to update the height to account for the bar width
			if (this.hasHorzontalScrollbar)
				this.updateInnerHeight();
		}
	}
	private void updateInnerTop() {
		int height = this.getHeight();
		int innerHeight = this.getInnerHeight();
		byte align = this.getVerticalAlign();
		if (height >= innerHeight) {
			if (ALIGN_STRETCH_RELATIVE == align) {
				this.setInnerTop(0);
			} else if (ALIGN_LEFT_TOP == align) {
				this.setInnerTop(0);
			} else if (ALIGN_CENTER_MIDDLE == align) {
				this.setInnerTop((height - innerHeight) / 2);
			} else if (ALIGN_RIGHT_BOTTOM == align) {
				this.setInnerTop((height - innerHeight));
			}
		} else
			this.setInnerTop(0);
	}

	private void updateInnerLeft() {
		int width = this.getWidth();
		int innerWidth = this.getInnerWidth();
		byte align = this.horizontalAlign;
		if (width >= innerWidth) {
			if (ALIGN_STRETCH_RELATIVE == align) {
				this.setInnerLeft(0);
			} else if (ALIGN_LEFT_TOP == align) {
				this.setInnerLeft(0);
			} else if (ALIGN_CENTER_MIDDLE == align) {
				this.setInnerLeft((width - innerWidth) / 2);
			} else if (ALIGN_RIGHT_BOTTOM == align) {
				this.setInnerLeft(width - innerWidth);
			}
		} else
			this.setInnerLeft(0);
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			if (this.flagNeedsUpdate) {
				this.update();
				this.flagNeedsUpdate = false;
			}
			if (this.innerPortlet != null) {
				if (this.flagUpdateInnerPortletSizeJs == true) {
					callJsFunction(JSFUNCTION_SET_INNER_SIZE).addParam(this.innerWidth).addParam(this.innerHeight).end();
					this.flagUpdateInnerPortletSizeJs = false;
				}
				if (this.flagUpdateScrollJs == true) {
					callJsFunction_setScroll();
					this.flagUpdateScrollJs = false;
				}
				if (this.flagUpdateAlignJs == true) {
					int left = this.innerLeft;
					int top = this.innerTop;
					if (this.getWidth() < this.innerWidth) {
						if (this.verticalAlign == ALIGN_RIGHT_BOTTOM) {
							top -= this.getScrollBarWidth();
						}
						if (this.verticalAlign == ALIGN_CENTER_MIDDLE) {
							top -= this.getScrollBarWidth() / 2;
						}
					}
					if (this.getHeight() < this.innerHeight) {
						if (this.horizontalAlign == ALIGN_RIGHT_BOTTOM) {
							left -= this.getScrollBarWidth();
						}
						if (this.horizontalAlign == ALIGN_CENTER_MIDDLE) {
							left -= this.getScrollBarWidth() / 2;
						}
					}

					callJsFunction(JSFUNCTION_SET_ALIGN).addParam(left).addParam(top).end();
					this.flagUpdateAlignJs = false;
				}
				if (this.flagUpdateOptionsChanged == true) {
					callJsFunction(JSFUNCTION_SET_OPTIONS).addParamJson(this.optionsChanges).end();
					this.optionsChanges.clear();
					this.flagUpdateOptionsChanged = false;
				}
				if (this.flagUpdateScrollBarWidth == true) {
					callJsFunction(JSFUNCTION_SET_SCROLLBARWIDTH).addParam(this.scrollBarWidth).end();
					this.flagUpdateScrollBarWidth = false;
				}
			}
		}
	}

	private int convertToClip(double clipAlign, int innerLength, int length, byte align) {
		double clipPos;
		if (ALIGN_STRETCH_RELATIVE == align) {
			clipPos = (clipAlign * innerLength) - length / 2;
		} else if (ALIGN_LEFT_TOP == align) {
			clipPos = clipAlign;
		} else if (ALIGN_CENTER_MIDDLE == align) {
			clipPos = (clipAlign - length / 2) + innerLength / 2;
		} else if (ALIGN_RIGHT_BOTTOM == align) {
			clipPos = innerLength - (clipAlign + length);
		} else
			clipPos = 0;
		return Caster_Integer.PRIMITIVE.cast(clipPos);
	}
	private double convertFromClip(int clipPos, int innerLength, int length, byte align) {
		double alignClip;
		if (ALIGN_STRETCH_RELATIVE == align) {
			alignClip = innerLength == 0 ? 0 : (clipPos + length / 2.0) / innerLength;
		} else if (ALIGN_LEFT_TOP == align) {
			alignClip = clipPos;
		} else if (ALIGN_CENTER_MIDDLE == align) {
			alignClip = (clipPos + length / 2) - innerLength / 2;
		} else if (ALIGN_RIGHT_BOTTOM == align) {
			alignClip = innerLength - (clipPos + length);
		} else
			alignClip = 0;
		return alignClip;
	}
	private void callJsFunction_setScroll() {
		int clipLeft = convertToClip(this.clipHorizontal, this.innerWidth, this.getWidth(), this.getHorizontalAlign());
		int clipTop = convertToClip(this.clipVertical, this.innerHeight, this.getHeight(), this.getVerticalAlign());
		callJsFunction(JSFUNCTION_SET_SCROLL).addParam(clipLeft).addParam(clipTop).end();
	}
	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		CH.putNoNull(r, CONFIG_TOP, this.getClipVertical());
		CH.putNoNull(r, CONFIG_LEFT, this.getClipHorizontal());
		CH.putNoNull(r, CONFIG_INNER_WIDTH, this.getInnerWidth());
		CH.putNoNull(r, CONFIG_INNER_HEIGHT, this.getInnerHeight());
		CH.putNoNull(r, CONFIG_INNER_MINWIDTH, this.innerMinWidth);
		CH.putNoNull(r, CONFIG_INNER_MAXWIDTH, this.innerMaxWidth);
		CH.putNoNull(r, CONFIG_INNER_MINHEIGHT, this.innerMinHeight);
		CH.putNoNull(r, CONFIG_INNER_MAXHEIGHT, this.innerMaxHeight);
		CH.putNoNull(r, CONFIG_HORIZONTAL_ALIGN, this.getHorizontalAlign());
		CH.putNoNull(r, CONFIG_VERTICAL_ALIGN, this.getVerticalAlign());
		if (this.innerPortlet != null && !(this.innerPortlet instanceof BlankPortlet))
			CH.putNoNull(r, CONFIG_CHILD, this.portletToConfigSaveId(this.innerPortlet));

		//todo has scroll bars
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToPortletIdMapping, StringBuilder sb) {
		super.init(configuration, origToPortletIdMapping, sb);
		//Need to init innerPortlet first
		String portletId = CH.getOr(Caster_String.INSTANCE, configuration, CONFIG_CHILD, null);
		if (portletId != null) {
			Portlet portlet = this.configSaveIdToPortlet(origToPortletIdMapping, portletId);
			this.setInnerPortlet(portlet);
		}
		this.setClipVertical(CH.getOr(Caster_Integer.PRIMITIVE, configuration, CONFIG_TOP, 0));
		this.setClipHorizontal(CH.getOr(Caster_Integer.PRIMITIVE, configuration, CONFIG_LEFT, 0));
		this.setInnerWidth(CH.getOr(Caster_Integer.PRIMITIVE, configuration, CONFIG_INNER_WIDTH, -1));
		this.setInnerHeight(CH.getOr(Caster_Integer.PRIMITIVE, configuration, CONFIG_INNER_HEIGHT, -1));
		this.setInnerMinWidth(CH.getOr(Caster_Integer.PRIMITIVE, configuration, CONFIG_INNER_MINWIDTH, -1));
		this.setInnerMaxWidth(CH.getOr(Caster_Integer.PRIMITIVE, configuration, CONFIG_INNER_MAXWIDTH, -1));
		this.setInnerMinHeight(CH.getOr(Caster_Integer.PRIMITIVE, configuration, CONFIG_INNER_MINHEIGHT, -1));
		this.setInnerMaxHeight(CH.getOr(Caster_Integer.PRIMITIVE, configuration, CONFIG_INNER_MAXHEIGHT, -1));
		this.setHorizontalAlign(Caster_Byte.PRIMITIVE.cast(CH.getOr(configuration, CONFIG_HORIZONTAL_ALIGN, ALIGN_LEFT_TOP)));
		this.setVerticalAlign(Caster_Byte.PRIMITIVE.cast(CH.getOr(configuration, CONFIG_VERTICAL_ALIGN, ALIGN_LEFT_TOP)));

	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if (CALLBACK_ONSCROLL.equals(callback)) {
			int clipTop = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "t", 0);
			int clipLeft = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "l", 0);
			double clipVertical = convertFromClip(clipTop, this.innerHeight, this.getHeight(), this.getVerticalAlign());
			double clipHorizontal = convertFromClip(clipLeft, this.innerWidth, this.getWidth(), this.getHorizontalAlign());
			this.setClipVerticalNoFire(clipVertical);
			this.setClipHorizontalNoFire(clipHorizontal);
		} else
			super.handleCallback(callback, attributes);
	}
	@Override
	public void bringToFront(String portletId) {
	}

	@Override
	public boolean hasVacancy() {
		return false;
	}
	@Override
	public boolean isCustomizable() {
		return false;
	}
	@Override
	public int getChildOffsetX(String id) {
		return 0;
	}
	@Override
	public int getChildOffsetY(String id) {
		return 0;
	}
	@Override
	public void setSize(int width, int height) {

		if (this.getWidth() != width)
			this.flagUpdateInnerWidth = true;
		if (this.getHeight() != height)
			this.flagUpdateInnerHeight = true;

		super.setSize(width, height);
		this.flagNeedsUpdate = true;
		this.flagUpdateSize = true;
		this.flagUpdateHorizontalAlign = true;
		this.flagUpdateVerticalAlign = true;
		this.flagUpdateAlignJs = true;
		this.flagUpdateScrollJs = true;

		this.flagPendingAjax();
	}
	public double getClipVertical() {
		return clipVertical;
	}
	public void setScrollPositionVertical(int scrollPosition) {
		double clipVertical = convertFromClip(scrollPosition, this.innerHeight, this.getHeight(), this.getVerticalAlign());
		this.setClipVertical(clipVertical);
	}
	public void setScrollPositionHorizontal(int scrollPosition) {
		double clipHorizontal = convertFromClip(scrollPosition, this.innerWidth, this.getWidth(), this.getHorizontalAlign());
		this.setClipHorizontal(clipHorizontal);
	}
	public int getScrollPositionVertical() {
		return convertToClip(this.clipVertical, this.innerHeight, this.getHeight(), this.getVerticalAlign());
	}
	public int getScrollPositionHorizontal() {
		return convertToClip(this.clipHorizontal, this.innerWidth, this.getWidth(), this.getHorizontalAlign());
	}
	public void setClipVertical(double clipVertical) {
		if (getHeight() >= this.innerHeight)
			return;
		if (this.clipVertical == clipVertical)
			return;
		this.clipVertical = clipVertical;
		this.flagUpdateScrollJs = true;
		flagPendingAjax();
	}
	private void setClipVerticalNoFire(double clipVertical) {
		if (getHeight() >= this.innerHeight)
			return;
		if (this.clipVertical == clipVertical)
			return;
		this.clipVertical = clipVertical;
	}
	public double getClipHorizontal() {
		return clipHorizontal;
	}
	public void setClipHorizontal(double clipHorizontal) {
		if (getWidth() >= this.innerWidth)
			return;
		if (this.clipHorizontal == clipHorizontal)
			return;
		this.clipHorizontal = clipHorizontal;
		this.flagUpdateScrollJs = true;
		flagPendingAjax();
	}
	private void setClipHorizontalNoFire(double clipHorizontal) {
		if (getWidth() >= this.innerWidth)
			return;
		if (this.clipHorizontal == clipHorizontal)
			return;
		this.clipHorizontal = clipHorizontal;
	}
	public int getInnerHeight() {
		return innerHeight;
	}
	private void setInnerHeight(int innerPortletHeight) {
		if (this.innerHeight == innerPortletHeight)
			return;
		this.innerHeight = innerPortletHeight;
		this.flagNeedsUpdate = true;
		this.flagUpdateInnerHeight = true;
		flagPendingAjax();

	}
	public int getInnerWidth() {
		return innerWidth;
	}
	private void setInnerWidth(int innerPortletWidth) {
		if (this.innerWidth == innerPortletWidth)
			return;
		this.innerWidth = innerPortletWidth;
		this.flagNeedsUpdate = true;
		this.flagUpdateInnerWidth = true;
		flagPendingAjax();
	}

	private void setInnerLeft(int innerLeft) {
		if (this.innerLeft == innerLeft)
			return;
		this.innerLeft = innerLeft;
		this.flagUpdateAlignJs = true;
		flagPendingAjax();
	}

	private void setInnerTop(int innerTop) {
		if (this.innerTop == innerTop)
			return;
		this.innerTop = innerTop;
		this.flagUpdateAlignJs = true;
		flagPendingAjax();
	}

	public void setInnerFixedWidth(int innerWidth) {
		this.setInnerMinWidth(innerWidth);
		this.setInnerMaxWidth(innerWidth);
	}
	public void setInnerFixedHeight(int innerHeight) {
		this.setInnerMinHeight(innerHeight);
		this.setInnerMaxHeight(innerHeight);
	}
	public void setInnerFixedSize(int innerWidth, int innerHeight) {
		this.setInnerFixedWidth(innerWidth);
		this.setInnerFixedHeight(innerHeight);
	}
	public int getInnerMinWidth() {
		return innerMinWidth;
	}
	public void setInnerMinWidth(int innerPortletMinWidth) {
		if (this.innerMinWidth == innerPortletMinWidth)
			return;
		this.innerMinWidth = innerPortletMinWidth;
		if (this.innerMinWidth == NO_CONSTRAINT || this.innerWidth != this.innerMinWidth) {
			this.flagNeedsUpdate = true;
			this.flagUpdateInnerWidth = true;
			flagPendingAjax();
		}
	}
	public int getInnerMaxWidth() {
		return innerMaxWidth;
	}
	public void setInnerMaxWidth(int innerPortletMaxWidth) {
		if (this.innerMaxWidth == innerPortletMaxWidth)
			return;
		this.innerMaxWidth = innerPortletMaxWidth;
		if (this.innerMaxWidth == NO_CONSTRAINT || this.innerWidth != this.innerMaxWidth) {
			this.flagNeedsUpdate = true;
			this.flagUpdateInnerWidth = true;
			flagPendingAjax();
		}
	}
	public int getInnerMinHeight() {
		return innerMinHeight;
	}
	public void setInnerMinHeight(int innerPortletMinHeight) {
		if (this.innerMinHeight == innerPortletMinHeight)
			return;
		this.innerMinHeight = innerPortletMinHeight;
		if (this.innerMinHeight == NO_CONSTRAINT || this.innerHeight != this.innerMinHeight) {
			this.flagNeedsUpdate = true;
			this.flagUpdateInnerHeight = true;
			flagPendingAjax();
		}
	}
	public int getInnerMaxHeight() {
		return innerMaxHeight;
	}
	public void setInnerMaxHeight(int innerPortletMaxHeight) {
		if (this.innerMaxHeight == innerPortletMaxHeight)
			return;
		this.innerMaxHeight = innerPortletMaxHeight;
		if (this.innerMaxHeight == NO_CONSTRAINT || this.innerHeight != this.innerMaxHeight) {
			this.flagNeedsUpdate = true;
			this.flagUpdateInnerHeight = true;
			flagPendingAjax();

		}
	}
	public byte getVerticalAlign() {
		return verticalAlign;
	}
	public void setVerticalAlign(byte verticalAlign) {
		if (this.verticalAlign == verticalAlign)
			return;
		int clipTop = convertToClip(this.clipVertical, this.innerHeight, this.getHeight(), this.getVerticalAlign());
		double newClipVertical = convertFromClip(clipTop, this.innerHeight, this.getHeight(), verticalAlign);
		this.setClipVerticalNoFire(newClipVertical);
		this.verticalAlign = verticalAlign;
		this.flagNeedsUpdate = true;
		flagPendingAjax();
	}
	public byte getHorizontalAlign() {
		return horizontalAlign;
	}

	public void setHorizontalAlign(byte horizontalAlign) {
		if (this.horizontalAlign == horizontalAlign)
			return;
		int clipLeft = convertToClip(this.clipHorizontal, this.innerWidth, this.getWidth(), this.getHorizontalAlign());
		double newClipHorizontal = convertFromClip(clipLeft, this.innerWidth, this.getWidth(), horizontalAlign);
		this.setClipHorizontalNoFire(newClipHorizontal);
		this.horizontalAlign = horizontalAlign;
		this.flagNeedsUpdate = true;
		flagPendingAjax();
	}

	public int getScrollBarWidth() {
		return scrollBarWidth;
	}
	public void setScrollBarWidth(int scrollBarWidth) {
		this.scrollBarWidth = scrollBarWidth;
	}

	public void setOption(String key, Object value) {
		Object curValue = null;
		if (this.options.containsKey(key)) {
			curValue = this.options.get(key);
		}
		if (curValue == value)
			return;
		this.options.put(key, value);
		this.optionsChanges.put(key, value);
		this.flagUpdateOptionsChanged = true;
		this.flagNeedsUpdate = true;
		this.flagPendingAjax();
	}

	//	public static class Builder extends AbstractPortletBuilder<BlankPortlet> {
	//
	//		public static final String ID = "blank";
	//
	//		public Builder() {
	//			super(BlankPortlet.class);
	//			setIcon("portlet_icon_blank");
	//		}
	//
	//		@Override
	//		public BlankPortlet buildPortlet(PortletConfig portletConfig) {
	//			BlankPortlet r = new BlankPortlet(portletConfig);
	//			return r;
	//		}
	//
	//		@Override
	//		public String getPortletBuilderName() {
	//			return "Blank";
	//		}
	//
	//		@Override
	//		public String getPortletBuilderId() {
	//			return ID;
	//		}
	//
	//	}
}
