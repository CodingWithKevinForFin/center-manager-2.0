package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Divider;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.BlankPortlet;
import com.f1.suite.web.portal.impl.DividerListener;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebDividerPortlet extends AmiWebAbstractContainerPortlet implements AmiWebStyledPortlet, AmiWebDesktopListener, DividerListener {

	private static final Logger log = LH.get();

	public static final ParamsDefinition CALLBACK_DEF_ONDIVIDERDOUBLECLICK = new ParamsDefinition("onDividerDoubleClick", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONDIVIDER_MOVING = new ParamsDefinition("onDividerMoving", Object.class, "Double offsetPct,Integer offsetPx");
	public static final ParamsDefinition CALLBACK_DEF_ONDIVIDER_MOVED = new ParamsDefinition("onDividerMoved", Object.class, "Double offsetPct,Integer offsetPx");

	static {
		CALLBACK_DEF_ONDIVIDERDOUBLECLICK.addDesc("Called when a user double-clicks on the divider with the left mouse button");
		CALLBACK_DEF_ONDIVIDER_MOVING.addDesc("Called as a user is moving (dragging) the divider");
		CALLBACK_DEF_ONDIVIDER_MOVING.addParamDesc(0, "the percent distance of the divider from the left/top (0=left/top ... 1 = right/bottom)");
		CALLBACK_DEF_ONDIVIDER_MOVING.addParamDesc(1, "the distance of the divider from the left/top in pixels");
		CALLBACK_DEF_ONDIVIDER_MOVED.addDesc("Called when the user has moved (dragged) the divider");
		CALLBACK_DEF_ONDIVIDER_MOVED.addParamDesc(0, "the percent distance of the divider from the left/top (0=left/top ... 1 = right/bottom)");
		CALLBACK_DEF_ONDIVIDER_MOVED.addParamDesc(1, "the distance of the divider from the left/top in pixels");
	}

	private AmiWebOverrideValue<Boolean> locked = new AmiWebOverrideValue<Boolean>(Boolean.FALSE);//used if align is off
	private AmiWebOverrideValue<Double> offsetPct = new AmiWebOverrideValue<Double>(.5d);//used if align is off
	private AmiWebOverrideValue<Integer> offsetPx = new AmiWebOverrideValue<Integer>(100);//use if align
	private AmiWebOverrideValue<Byte> align = new AmiWebOverrideValue<Byte>(ALIGN_RATIO);
	private AmiWebOverrideValue<Double> unsnapMinPct = new AmiWebOverrideValue<Double>(0d);
	private AmiWebOverrideValue<Byte> snapSetting = new AmiWebOverrideValue<Byte>(SNAP_SETTING_NONE);
	private AmiWebOverrideValue<Boolean> isMinimized = new AmiWebOverrideValue<Boolean>(false);

	final private AmiWebDesktopPortlet amiDesktop;
	private AmiWebInnerDividerPortlet divider;

	public static final byte SNAP_SETTING_NONE = 0;
	public static final byte SNAP_SETTING_START = 1;
	public static final byte SNAP_SETTING_END = 2;
	public static final byte ALIGN_START = 0;
	public static final byte ALIGN_RATIO = 1;
	public static final byte ALIGN_END = 2;

	private double snapRestorePct = Double.NaN;
	private int snapRestorePx = -1;
	private boolean inCrazyBackwardsCompatibilityMode;//The configuration is a legacy format containing a % offset but it's locked top/bottom.Once the panel is visible we can determine the px offset

	public AmiWebDividerPortlet(PortletConfig config, boolean isVertical) {
		super(config);
		this.setShowConfigButtons(false);
		this.divider = new AmiWebInnerDividerPortlet(generateConfig(), isVertical, this);
		this.setChild(this.divider);
		this.amiDesktop = getService().getDesktop();
		this.divider.addListener(this);
		this.getStylePeer().initStyle();
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebDividerPortlet> implements AmiWebPortletContainerBuilder<AmiWebDividerPortlet> {

		public static final String ID = "div";

		public Builder() {
			super(AmiWebDividerPortlet.class);
			setIcon("portlet_icon_vdivider");
		}

		@Override
		public AmiWebDividerPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebDividerPortlet r = new AmiWebDividerPortlet(portletConfig, true);
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
		@Override
		public void extractChildPorletIds(Map<String, Object> config, Map<String, Map> sink) {
			CH.putNoKeyNull(sink, CH.getOr(String.class, config, "child1", null), null);
			CH.putNoKeyNull(sink, CH.getOr(String.class, config, "child2", null), null);
		}

		@Override
		public boolean removePortletId(Map<String, Object> portletConfig, String amiPanelId) {
			if (OH.eq(amiPanelId, portletConfig.get("child1")))
				portletConfig.remove("child1");
			else if (OH.eq(amiPanelId, portletConfig.get("child2")))
				portletConfig.remove("child2");
			else
				return false;
			return true;
		}
		@Override
		public boolean replacePortletId(Map<String, Object> portletConfig, String oldPanelId, String nuwPanelId) {
			if (OH.eq(oldPanelId, portletConfig.get("child1")))
				portletConfig.put("child1", nuwPanelId);
			else if (OH.eq(oldPanelId, portletConfig.get("child2")))
				portletConfig.put("child2", nuwPanelId);
			else
				return false;
			return true;
		}

	}

	public static class HBuilder extends Builder {
		public AmiWebDividerPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebDividerPortlet r = super.buildPortlet(portletConfig);
			r.getInnerContainer().setVertical(false);
			return r;
		};
	}

	public static class VBuilder extends Builder {
		public AmiWebDividerPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebDividerPortlet r = super.buildPortlet(portletConfig);
			r.getInnerContainer().setVertical(true);
			return r;
		};
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		//		getInnerContainer().init(configuration, origToNewIdMapping, sb);
		String child1 = (String) configuration.get("child1");
		String child2 = (String) configuration.get("child2");
		String dir = (String) configuration.get("dir");
		if (dir != null)
			this.getInnerContainer().setVertical("v".equals(dir));
		boolean isOffsetSet = false;
		if (configuration.containsKey("startOffsetPx")) {
			int offset = CH.getOr(Caster_Integer.INSTANCE, configuration, "startOffsetPx", 0);
			this.setAlign(ALIGN_START, false);
			this.setOffsetPx(offset, false);
			isOffsetSet = true;
		} else if (isValidEndOffset(configuration.get("endOffsetPx"))) {
			int offset = CH.getOr(Caster_Integer.INSTANCE, configuration, "endOffsetPx", 0);
			this.setAlign(ALIGN_END, false);
			this.setOffsetPx(offset, false);
			isOffsetSet = true;
		} else if (configuration.containsKey("offset")) {
			Double offset = CH.getOr(Caster_Double.INSTANCE, configuration, "offset", 0.5);
			this.setAlign(ALIGN_RATIO, false);
			this.setOffsetPct(offset, false);
		} else if (configuration.containsKey("offsetDflt")) {
			Double defaultOffsetPct = CH.getOr(Caster_Double.INSTANCE, configuration, "offsetDflt", null);//backwards compatibility
			this.setAlign(ALIGN_RATIO, false);
			this.setOffsetPct(defaultOffsetPct, false);
		}
		// take as it is
		this.divider.setCurrentAsDefault();
		Boolean locked = CH.getOr(Caster_Boolean.INSTANCE, configuration, "locked", null);
		if (locked == null) {
			//backwards compatibility: pull from style
			locked = AmiWebLayoutVersionHelper.get(Caster_Boolean.INSTANCE, configuration, "amiStyle.vl.div.divLock", Boolean.FALSE);
			String snap = AmiWebLayoutVersionHelper.get(Caster_String.INSTANCE, configuration, "amiStyle.vl.div.divSnapSetting", null);
			String align = AmiWebLayoutVersionHelper.get(Caster_String.INSTANCE, configuration, "amiStyle.vl.div.divAlign", null);
			if (OH.eq("start", snap)) {
				if (!isOffsetSet) {
					this.inCrazyBackwardsCompatibilityMode = true;
					LH.warning(log, "DIVIDER IN BACKWARDS COMPATABILITY MODE: " + this.getAri());
				}
				this.setSnapSetting(SNAP_SETTING_START, false);
			} else if (OH.eq("end", snap)) {
				if (!isOffsetSet) {
					this.inCrazyBackwardsCompatibilityMode = true;
					LH.warning(log, "DIVIDER IN BACKWARDS COMPATABILITY MODE: " + this.getAri());
				}
				this.setSnapSetting(SNAP_SETTING_END, false);
			} else
				this.setSnapSetting(SNAP_SETTING_NONE, false);
			if (OH.eq("start", align))
				this.setAlign(ALIGN_START, false);
			else if (OH.eq("end", align))
				this.setAlign(ALIGN_END, false);
			if (this.snapSetting.get() != SNAP_SETTING_NONE) {
				Double snapPct = AmiWebLayoutVersionHelper.get(Caster_Double.INSTANCE, configuration, "amiStyle.vl.div.divSnapPosPct", 0d);
				this.unsnapMinPct.setValue(snapPct / 100d, false);
			}
		} else {
			String snap = CH.getOr(String.class, configuration, "snap", null);
			if (OH.eq("start", snap))
				this.setSnapSetting(SNAP_SETTING_START, false);
			else if (OH.eq("end", snap))
				this.setSnapSetting(SNAP_SETTING_END, false);
			else
				this.setSnapSetting(SNAP_SETTING_NONE, false);
			if (this.snapSetting.get() != SNAP_SETTING_NONE)
				this.unsnapMinPct.setValue(CH.getOr(Caster_Double.INSTANCE, configuration, "unsnapMinPct", 0d), false);
		}
		if (CH.getOr(Boolean.class, configuration, "isMin", Boolean.FALSE)) {
			switch (this.align.getValue(false)) {
				case ALIGN_RATIO:
					this.snapRestorePct = this.offsetPct.get();
					break;
				case ALIGN_START:
					this.snapRestorePx = this.offsetPx.get();
					break;
				case ALIGN_END:
					this.snapRestorePx = this.offsetPx.get();
					break;
			}
			snap();
			this.snapSetting.setOverrideToDefault();
			this.isMinimized.setValue(true, false);
		}
		if (child1 != null) {
			Portlet child = this.divider.configSaveIdToPortlet(origToNewIdMapping, child1);
			this.divider.setFirst(child);
		}
		if (child2 != null) {
			Portlet child = this.divider.configSaveIdToPortlet(origToNewIdMapping, child2);
			this.divider.setSecond(child);
		}
		setIsLocked(locked, false);
	}

	private void setOffsetPct(Double offset, boolean override) {
		// shouldn't round this, because we will have an override issue where the value with one precision overriding a value that has a different precision but both values are essentially the same.
		this.offsetPct.set(offset, override);
		if (this.align.get() == ALIGN_RATIO)
			this.getInnerContainer().setOffset(offset);
	}
	private void setOffsetPx(int offset, boolean override) {
		if (offset < 0)
			offset = 0;
		this.offsetPx.set(offset, override);
		if (this.align.get() == ALIGN_START) {
			this.getInnerContainer().setOffsetFromTopPx(offset);
		} else if (this.align.get() == ALIGN_END)
			this.getInnerContainer().setOffsetFromBottomPx(offset);
	}

	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
		if (this.divider.getFirstChild() instanceof BlankPortlet)
			this.divider.setFirst(this.getService().getDesktop().newAmiWebAmiBlankPortlet(this.getAmiLayoutFullAlias()));
		if (this.divider.getSecondChild() instanceof BlankPortlet)
			this.divider.setSecond(this.getService().getDesktop().newAmiWebAmiBlankPortlet(this.getAmiLayoutFullAlias()));
	}
	/**
	 * called when the dev/user mode changes, in which case we change the movability of divider accordingly
	 */
	@Override
	public void onEditModeChanged(AmiWebDesktopPortlet desktop, boolean inEditMode) {
		if (getVisible()) {
			this.divider.setMovable(inEditMode || !this.getIsLocked(true));
			this.divider.callInit();
		}
	}

	/**
	 * called when divider lock status changes regardless of edit mode. <br>
	 * <br>
	 * This covers: <br>
	 * 1. <b>NEW </b> amiscript to dynamically set divider to lock/unlock <br>
	 * 2. divider setting changes <br>
	 * 3. divider initialization
	 */
	public void onLockChanged() {
		if (getVisible()) {
			this.divider.setMovable(!this.getIsLocked(true));
			this.divider.callInit();
		}
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		super.onStyleValueChanged(key, old, nuw);
		switch (key) {
			case AmiWebStyleConsts.CODE_DIV_SZ:
				this.divider.setThickness(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_DIV_CL:
				this.divider.setColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DIV_HOVER_CL:
				this.divider.setHoverColor((String) nuw);
				break;
		}
	}

	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Divider.TYPE_DIVIDER;
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("dir", divider.isVertical() ? "v" : "h");
		r.put("child1", divider.portletToConfigSaveId(divider.getFirstChild()));
		r.put("child2", divider.portletToConfigSaveId(divider.getSecondChild()));
		if (this.isMinimized.getValue(false)) {
			r.put("isMin", true);
		}
		if (this.inCrazyBackwardsCompatibilityMode) {
			String snap2 = this.snapSetting.getValue(false) == SNAP_SETTING_START ? "start" : this.snapSetting.getValue() == SNAP_SETTING_END ? "end" : null;
			String align2 = this.align.getValue(false) == ALIGN_START ? "start" : this.align.getValue() == ALIGN_END ? "end" : null;
			Map m = getMap(r, "amiStyle");
			Map m2 = getMap(m, "vl");
			Map m3 = getMap(m2, "div");
			m3.put("divLock", this.locked.getValue(false));
			m3.put("divSnapSetting", snap2);
			m3.put("divAlign", align2);
			r.put("offset", ((int) (100000 * this.offsetPct.getValue(false)) / 100000d));
		} else {
			switch (this.align.getValue(false)) {
				case ALIGN_START:
					r.put("startOffsetPx", offsetPx.getValue(false));
					break;
				case ALIGN_END:
					r.put("endOffsetPx", offsetPx.getValue(false));
					break;
				case ALIGN_RATIO:
					r.put("offset", ((int) (100000 * this.offsetPct.getValue(false)) / 100000d));
					break;
			}
			switch (this.snapSetting.getValue(false)) {
				case SNAP_SETTING_START:
					r.put("snap", "start");
					CH.putExcept(r, "unsnapMinPct", this.unsnapMinPct.getValue(false), 0d);
					break;
				case SNAP_SETTING_END:
					r.put("snap", "end");
					CH.putExcept(r, "unsnapMinPct", this.unsnapMinPct.getValue(false), 0d);
					break;
			}
			r.put("locked", this.locked.getValue(false));
		}
		return r;
	}
	static private Map getMap(Map<String, Object> map, String key) {
		Map r = (Map) map.get(key);
		if (r == null)
			map.put(key, r = new HashMap());
		return r;
	}
	@Override
	public void clearAmiData() {
	}

	@Override
	public void clearUserSelection() {
	}

	@Override
	public boolean isRealtime() {
		return false;
	}

	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		return null;
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		return false;
	}

	@Override
	public String getConfigMenuTitle() {
		return "Divider";
	}

	@Override
	public String getPanelType() {
		return "divider";
	}
	@Override
	public void getUsedColors(Set<String> sink) {
	}

	@Override
	public AmiWebInnerDividerPortlet getInnerContainer() {
		return this.divider;
	}

	public boolean isVertical() {
		return this.divider.isVertical();
	}

	public Portlet getFirstChild() {
		return this.divider.getFirstChild();
	}

	public Portlet getSecondChild() {
		return this.divider.getSecondChild();
	}

	@Override
	public void onDividerMovingStarted(DividerPortlet dividerPortlet, double currentOffset) {

	}

	@Override
	public void onDividerMoving(DividerPortlet dividerPortlet, double currentOffset) {
		this.callbacks.execute("onDividerMoving", currentOffset, calculateOffsetPx(currentOffset));
	}

	@Override
	public void onDividerMoved(DividerPortlet dividerPortlet, double currentOffset) {
		// current offset already accounted for thickness, already stored in dividerPortlet
		int px = calculateOffsetPx(currentOffset);
		this.callbacks.execute("onDividerMoving", currentOffset, px);
		this.callbacks.execute("onDividerMoved", currentOffset, px);
		byte materializedSnapSetting = this.getSnapSetting(true);
		boolean isSnap = (materializedSnapSetting == SNAP_SETTING_START && dividerPortlet.isAtStart(currentOffset))
				|| (materializedSnapSetting == SNAP_SETTING_END && dividerPortlet.isAtEnd(currentOffset));
		this.isMinimized.setValue(isSnap, true);
		boolean override;
		if (this.inCrazyBackwardsCompatibilityMode) {
			override = false;
			this.inCrazyBackwardsCompatibilityMode = false;
		} else
			override = true;
		if (!isSnap) {
			switch (this.align.getValue(true)) {
				case ALIGN_RATIO:
					// shouldn't round this, because we will have an override issue where the value with one precision overriding a value that has a different precision but both values are essentially the same.

					this.offsetPct.setValue(currentOffset, true);
					break;
				case ALIGN_START:
					this.offsetPx.setValue(px, override);
					break;
				case ALIGN_END:
					this.offsetPx.setValue(this.getInnerContainer().getSize() - px, override);
					break;
			}
		}
	}

	private double roundHalfEven(double val) {
		return MH.round(val, MH.ROUND_HALF_EVEN);
	}

	@Override
	public void onDividerDblClick() {
		this.callbacks.execute("onDividerDoubleClick");
		if (this.getSnapSetting(true) != SNAP_SETTING_NONE) {
			if (getIsMinimized(true))
				unsnap();
			else
				snap();

		}
	}
	public boolean getIsMinimized(boolean override) {
		return isMinimized.getValue(override);
	}
	public boolean snap() {
		if (!getIsMinimized(true)) {
			this.doSnap();
			return true;
		}
		return false;
	}

	private void doSnap() {
		byte snapSetting = this.getSnapSetting(true);
		if (snapSetting == SNAP_SETTING_NONE) {
			return;
		}
		this.divider.setSnapped(true);
		switch (this.align.get()) {
			case ALIGN_RATIO:
				this.snapRestorePct = this.offsetPct.get();
				break;
			case ALIGN_START:
				this.snapRestorePx = this.offsetPx.get();
				break;
			case ALIGN_END:
				this.snapRestorePx = this.offsetPx.get();
				break;
		}
		if (snapSetting == SNAP_SETTING_START) {
			this.divider.setOffsetFromTopPx(Math.max(this.divider.getThickness() / 2, 1));
			this.divider.setExpandBias(0, 1);
		} else {
			this.divider.setOffsetFromBottomPx(Math.max(this.divider.getThickness() / 2, 1));
			this.divider.setExpandBias(1, 0);
		}
		this.isMinimized.setValue(true, true);
	}

	public boolean unsnap() {
		if (this.isMinimized.getValue(true)) {
			doUnsnap();
			return true;
		}
		return false;
	}

	private void doUnsnap() {
		this.divider.setSnapped(false);
		switch (this.align.getValue(false)) {
			case ALIGN_RATIO: {
				this.divider.setExpandBias(.5d, .5d);
				double t;
				if (this.getSnapSetting(true) == SNAP_SETTING_START)
					t = MH.maxAvoidNan(this.unsnapMinPct.get(), snapRestorePct);
				else
					t = MH.minAvoidNan(1 - this.unsnapMinPct.get(), snapRestorePct);
				this.divider.setOffset(t);
				break;
			}
			case ALIGN_START: {
				this.divider.setExpandBias(0d, 1d);
				int t;
				if (this.getSnapSetting(true) == SNAP_SETTING_START)
					t = MH.max((int) (roundHalfEven(this.unsnapMinPct.get() * this.divider.getSize())), snapRestorePx);
				else
					t = MH.min((int) (roundHalfEven((1 - this.unsnapMinPct.get()) * this.divider.getSize())), snapRestorePx);
				this.divider.setOffsetFromTopPx(t);
				break;
			}
			case ALIGN_END: {
				this.divider.setExpandBias(1d, 0);
				int t;
				if (this.getSnapSetting(true) == SNAP_SETTING_START)
					t = MH.min((int) (roundHalfEven((1 - this.unsnapMinPct.get()) * this.divider.getSize())), snapRestorePx);
				else
					t = MH.max((int) (roundHalfEven(this.unsnapMinPct.get() * this.divider.getSize())), snapRestorePx);
				this.divider.setOffsetFromBottomPx(t);
				break;
			}
		}
		this.snapRestorePct = Double.NaN;
		this.isMinimized.setValue(false, true);
	}

	private int calculateOffsetPx(double offsetPct) {
		return (int) (roundHalfEven(offsetPct * this.divider.getSize()));
	}

	@Override
	public Map<String, Object> getUserPref() {
		Map<String, Object> r = super.getUserPref();
		if (!this.getIsLocked(false)) {
			if (this.getAlign(false) == ALIGN_RATIO) {
				if (this.offsetPct.isOverride())
					r.put("divOffsetPct", this.offsetPct.getValue(true));
			} else {
				if (this.offsetPx.isOverride())
					r.put("divOffsetPx", this.offsetPx.getValue(true));
			}
		}
		if (getIsMinimized(false))
			r.put("isMin", true);
		return r;
	}

	@Override
	public void applyUserPref(Map<String, Object> values) {
		if (!this.getIsLocked(false)) {
			Double pct = CH.getOr(Caster_Double.INSTANCE, values, "divOffsetPct", null);
			if (pct != null)
				this.setOffsetPct(pct, true);
			Integer offsetPx = CH.getOr(Caster_Integer.INSTANCE, values, "divOffsetPx", null);
			if (offsetPx != null)
				this.setOffsetPx(offsetPx, true);
		}
		boolean isMinimized = CH.getOr(Caster_Boolean.PRIMITIVE, values, "isMin", false);
		this.isMinimized.setValue(isMinimized, false);
		super.applyUserPref(values);
	}
	public void setDefaultOffsetPctToCurrent() {
		this.align.setOverrideToDefault();
		this.snapSetting.setOverrideToDefault();
		this.isMinimized.setOverrideToDefault();
		switch (this.align.get()) {
			case ALIGN_RATIO:
				this.offsetPct.setOverrideToDefault();
				break;
			case ALIGN_START:
			case ALIGN_END:
				this.offsetPx.setOverrideToDefault();
				break;
		}
		this.divider.setCurrentAsDefault();
		// reset clipping state
		this.divider.setOffsetClipped(false);
	}
	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		boolean changed = !this.isCurrentOffsetDefault();
		headMenu.add(new BasicWebMenuLink("Set Current as Default", changed, "set_cur_offset_default").setBackgroundImage(AmiWebConsts.ICON_LOCK));
		headMenu.add(new BasicWebMenuLink("Reset to Default", changed, "reset_cur_offset_default").setBackgroundImage(AmiWebConsts.ICON_LOCK));
		headMenu.add(new BasicWebMenuLink("Flip Panels", true, "swap_div").setBackgroundImage(AmiWebConsts.ICON_FLIP));
		headMenu.add(new BasicWebMenuLink("Rotate Panels Clockwise", true, "rotate_clockwise").setBackgroundImage(AmiWebConsts.ICON_ROTATE_CLOCKWISE));
		headMenu.add(new BasicWebMenuLink("Rotate Panels Counterclockwise", true, "rotate_counter_clockwise").setBackgroundImage(AmiWebConsts.ICON_ROTATE_COUNTER));
		headMenu.add(new BasicWebMenuLink("Change to 2 Tabs", true, "change_div_to_tabs").setBackgroundImage(AmiWebConsts.ICON_CHANGE_2_TABS));
		headMenu.add(new BasicWebMenuLink("Distribute Dividers", true, "distribute_dividers"));

	}

	@Override
	public boolean onAmiContextMenu(String id) {
		if ("set_cur_offset_default".equals(id)) {
			this.setDefaultOffsetPctToCurrent();
			return true;
		} else if ("reset_cur_offset_default".equals(id)) {
			this.resetOffsetPctToDefault();
			return true;
		} else if ("rotate_clockwise".equals(id)) {
			this.getInnerContainer().rotate(true);
			return true;
		} else if ("rotate_counter_clockwise".equals(id)) {
			this.getInnerContainer().rotate(false);
			return true;
		} else if ("swap_div".equals(id)) {
			this.getInnerContainer().swap(false);
			return true;
		} else if ("change_div_to_tabs".equals(id)) {
			AmiWebDividerPortlet removed = this;
			DividerPortlet divider = removed.getInnerContainer();
			Portlet c1 = divider.getFirstChild();
			Portlet c2 = divider.getSecondChild();
			divider.removeAllChildren();
			AmiWebTabPortlet newChild = this.getService().getDesktop().newTabPortlet(removed.getAmiLayoutFullAlias());
			this.getService().getDesktop().replacePortlet(removed.getPortletId(), newChild);
			TabPortlet innerContainer = newChild.getInnerContainer();
			if (divider.isVertical()) {
				innerContainer.addChild("Left", c1);
				innerContainer.addChild("Right", c2);
			} else {
				innerContainer.addChild("Upper", c1);
				innerContainer.addChild("Lower", c2);
			}
			if (AmiWebConsts.STYLE_EDITOR_SHOW.equals(this.getService().getVarsManager().getSetting(AmiWebConsts.USER_SETTING_SHOW_STYLE_EDITOR_TABS)))
				AmiWebUtils.showStyleDialog("Tab Style", newChild, new AmiWebEditStylePortlet(((AmiWebTabPortlet) newChild).getStylePeer(), generateConfig()), generateConfig());
			return true;
		} else if ("distribute_dividers".equals(id)) {
			AmiWebUtils.distributeDividers(AmiWebUtils.getRootDividerAlongAxis(this));
			return true;
		} else
			return super.onAmiContextMenu(id);
	}

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebDivSettingsPortlet(generateConfig(), this);
	}

	public void resetOffsetPctToDefault() {
		this.align.clearOverride();
		if (!this.isMinimized.getValue(false)) {
			if (this.isMinimized.isOverride()) {//was overridden to false
				doUnsnap();
				this.snapSetting.clearOverride();
			} else {
				switch (this.align.get()) {
					case ALIGN_RATIO:
						this.divider.setExpandBias(.5d, .5d);
						this.divider.setOffset(this.offsetPct.getValue(false));
						this.offsetPct.clearOverride();
						break;
					case ALIGN_START:
						this.divider.setExpandBias(0d, 1d);
						this.divider.setOffsetFromTopPx(this.offsetPx.getValue(false));
						this.offsetPx.clearOverride();
						break;
					case ALIGN_END:
						this.divider.setExpandBias(1d, 0);
						this.divider.setOffsetFromBottomPx(this.offsetPx.getValue(false));
						this.offsetPx.clearOverride();
						break;
				}
			}
		} else {
			if (this.isMinimized.isOverride())//was overridden to true
				doSnap();
			this.snapSetting.clearOverride();
		}
	}

	public void setIsLocked(boolean n, boolean override) {
		boolean changed = this.locked.setValue(n, override);
		this.getInnerContainer().setLockPosition(this.locked.get());
		if (changed) {
			// notify frontend
			onLockChanged();
		}
	}
	public boolean getIsLocked(boolean override) {
		return this.locked.getValue(override);
	}

	public void setAlign(byte n, boolean override) {
		this.align.setValue(n, override);
		switch (this.align.get()) {
			case ALIGN_RATIO:
				this.divider.setExpandBias(.5d, .5d);
				this.offsetPct.setValue(this.divider.getOffset(), override);
				this.divider.setAlign(ALIGN_RATIO);
				break;
			case ALIGN_START:
				// left/top
				this.divider.setExpandBias(0d, 1d);
				this.divider.setAlign(ALIGN_START);
				this.offsetPx.setValue(this.divider.getAbsOffsetPx(), override);
				break;
			case ALIGN_END:
				// right/bottom
				this.divider.setExpandBias(1d, 0);
				this.divider.setAlign(ALIGN_END);
				if (this.divider.getOffsetPx() < 0) {
					this.offsetPx.setValue(this.divider.getAbsOffsetPx(), override);
				} else {
					this.offsetPx.setValue(this.divider.getSize() - this.divider.getOffsetPx(), override);
				}
				break;
		}
	}
	public byte getAlign(boolean override) {
		return this.align.getValue(override);
	}

	public double getUnsnapMinPct(boolean override) {
		return this.unsnapMinPct.getValue(override);
	}
	public void setUnsnapMinPct(double n, boolean override) {
		this.unsnapMinPct.setValue(n, override);
	}

	public byte getSnapSetting(boolean override) {
		return this.snapSetting.getValue(override);
	}
	public void setSnapSetting(byte snapSetting, boolean override) {
		this.snapSetting.setValue(snapSetting, override);
	}
	public void clearSnapSettingOverride() {
		this.snapSetting.clearOverride();
	}

	public boolean isCurrentOffsetDefault() {
		if (this.align.isOverride())
			return false;
		if (this.snapSetting.isOverride())
			return false;
		if (this.isMinimized.isOverride())
			return false;
		//		if (this.isMinimized.getValue(true))
		//			return true;
		if (this.divider.isOffsetClipped()) {
			return false;
		}
		switch (this.align.get()) {
			case ALIGN_RATIO:
				return !this.offsetPct.isOverride();
			case ALIGN_START:
			case ALIGN_END:
			default:
				return !this.offsetPx.isOverride();
		}
	}
	static private boolean isValidEndOffset(Object o) {
		return o instanceof Number && ((Number) o).intValue() > 0;
	}

	@Override
	public void onDividerRestored(DividerPortlet dividerPortlet) {
		// this call comes from dividerPortlet
		this.align.clearOverride();
		this.offsetPx.clearOverride();
		this.offsetPct.clearOverride();
	}

}
