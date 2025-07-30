package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebDividerPortlet;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_DividerPanel extends AmiWebScriptBaseMemberMethods<AmiWebDividerPortlet> {

	private AmiWebScriptMemberMethods_DividerPanel() {
		super();
		addMethod(GET_FIRST_CHILD, "firstChild");
		addMethod(GET_SECOND_CHILD, "secondChild");
		addMethod(IS_HORIZONTAL);
		addMethod(IS_VERTICAL, "isVertical");
		addMethod(IS_SHAPED, "isSpanned");
		addMethod(SET_SNAP_DIRECTION);
		addMethod(GET_SNAP_DIRECTION, "snapDirection");
		addMethod(SNAP);
		addMethod(UNSNAP);
		addMethod(SET_LOCK);
		addMethod(IS_LOCKED);
		addMethod(GET_DIVIDER_OFFSET_PCT, "dividerOffsetPct");
		addMethod(SET_DIVIDER_OFFSET_PCT);
		addMethod(GET_DIVIDER_OFFSET_PX);
		addMethod(SET_DIVIDER_OFFSET_PX);
		registerCallbackDefinition(AmiWebDividerPortlet.CALLBACK_DEF_ONDIVIDER_MOVED);
		registerCallbackDefinition(AmiWebDividerPortlet.CALLBACK_DEF_ONDIVIDER_MOVING);
		registerCallbackDefinition(AmiWebDividerPortlet.CALLBACK_DEF_ONDIVIDERDOUBLECLICK);
	}

	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> GET_FIRST_CHILD = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class,
			"getFirstChild", AmiWebPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return (AmiWebPortlet) targetObject.getFirstChild();
		}

		@Override
		protected String getHelp() {
			return "Returns the first child of the divider.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> GET_SECOND_CHILD = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class,
			"getSecondChild", AmiWebPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return (AmiWebPortlet) targetObject.getSecondChild();
		}

		@Override
		protected String getHelp() {
			return "Returns the second child of the divider.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> IS_VERTICAL = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class, "isVertical",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isVertical();
		}

		@Override
		protected String getHelp() {
			return "Returns true if it is a vertical divider, false otherwise.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> IS_HORIZONTAL = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class, "isHorizontal",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return !targetObject.isVertical();
		}

		@Override
		protected String getHelp() {
			return "Returns true if it is a horizontal divider, false otherwise.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> IS_SHAPED = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class, "isSnapped",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsMinimized(true);
		}

		@Override
		protected String getHelp() {
			return "Returns true if divider is currently snapped, false otherwise.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> SET_SNAP_DIRECTION = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class,
			"setSnapDirection", Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String direction = Caster_String.INSTANCE.cast(params[0]);
			if (direction == null) {
				targetObject.clearSnapSettingOverride();
				return true;
			}
			boolean isVertical = targetObject.isVertical();
			if (isVertical) {
				if (direction.equalsIgnoreCase("right"))
					targetObject.setSnapSetting(AmiWebDividerPortlet.SNAP_SETTING_END, true);
				else if (direction.equalsIgnoreCase("left"))
					targetObject.setSnapSetting(AmiWebDividerPortlet.SNAP_SETTING_START, true);
				else {
					warning(sf, "Divider is vertical. Accepted values: right/left.", null);
					return false;
				}
			} else {
				if (direction.equalsIgnoreCase("top"))
					targetObject.setSnapSetting(AmiWebDividerPortlet.SNAP_SETTING_START, true);
				else if (direction.equalsIgnoreCase("bottom"))
					targetObject.setSnapSetting(AmiWebDividerPortlet.SNAP_SETTING_END, true);
				else {
					warning(sf, "Divider is horizontal. Accepted values: top/bottom.", null);
					return false;
				}
			}
			return true;
		}
		protected String[] buildParamNames() {
			return new String[] { "snapDirection" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Direction of the snap." };
		}
		@Override
		protected String getHelp() {
			return "Sets the direction of snap. Accepted values: left/right (if divider is vertical), top/bottom (if divider is horizontal). Returns true if successful, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> GET_SNAP_DIRECTION = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class,
			"getSnapDirection", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			byte snapSettingOverride = targetObject.getSnapSetting(true);
			boolean isVertical = targetObject.isVertical();
			if (OH.eq(snapSettingOverride, AmiWebDividerPortlet.SNAP_SETTING_NONE))
				return "none";
			if (isVertical) {
				if (OH.eq(snapSettingOverride, AmiWebDividerPortlet.SNAP_SETTING_END))
					return "right";
				else
					return "left";
			} else {
				if (OH.eq(snapSettingOverride, AmiWebDividerPortlet.SNAP_SETTING_START))
					return "top";
				else
					return "bottom";
			}
		}

		@Override
		protected String getHelp() {
			return "Returns snap direction. Possible return values: none, (right/left (if divider is vertical), top/bottom (if divider is horizontal).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> SNAP = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class, "snap", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.snap();
		}

		@Override
		protected String getHelp() {
			return "Snaps the divider to the position specified in the divider settings.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> UNSNAP = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class, "unsnap",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.unsnap();
		}

		@Override
		protected String getHelp() {
			return "Brings back the divider to the position before it was snapped.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> GET_DIVIDER_OFFSET_PCT = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class,
			"getDividerOffsetPct", Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getInnerContainer().getOffset();
		}

		@Override
		protected String getHelp() {
			return "Returns a Double that is the offset value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> SET_DIVIDER_OFFSET_PCT = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class,
			"setDividerOffsetPct", Boolean.class, Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Double offset = Caster_Double.INSTANCE.cast(params[0]);
			if (offset == null)
				return false;
			if (MH.between(offset, 0, 1) != offset) {
				warning(sf, "Divider Offset not within a valid range", null, null);
				return false;
			} else if (MH.isntNumber(offset)) {
				warning(sf, "Divider Offset must be a number between 0 and 1 inclusive", null, null);
				return false;
			} else {
				targetObject.getInnerContainer().setOffset(offset);
				return true;
			}

		}

		protected String[] buildParamNames() {
			return new String[] { "offset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "numerical offset value between 0 - 1 inlcusive." };
		}
		@Override
		protected String getHelp() {
			return "Sets the offset of the divider. E.g. setting the offset to 0.3 means the left panel of the divider will have 30% of the area, while the right panel will have 70%.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> GET_DIVIDER_OFFSET_PX = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class,
			"getDividerOffsetPx", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getInnerContainer().getOffsetPx();
		}

		@Override
		protected String getHelp() {
			return "Returns an Integer that is the numerical offset value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> SET_DIVIDER_OFFSET_PX = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class,
			"setDividerOffsetPx", Boolean.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer offset = Caster_Integer.INSTANCE.cast(params[0]);
			if (offset == null)
				return false;
			if (MH.isntNumber(offset)) {
				warning(sf, "Divider Offset must be a number", null);
				return false;
			} else {
				targetObject.getInnerContainer().setOffsetPx(offset);
				return true;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "offset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "numerical offset value." };
		}
		@Override
		protected String getHelp() {
			return "Sets the offset of the divider.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> SET_LOCK = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class, "lock",
			Boolean.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Boolean lock = Caster_Boolean.INSTANCE.cast(params[0]);
			if (lock == null)
				return false;
			boolean org = targetObject.getIsLocked(true);
			targetObject.setIsLocked(lock, true);
			boolean now = targetObject.getIsLocked(true);
			return org != now;
		}

		protected String[] buildParamNames() {
			return new String[] { "lock" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "pass in true to lock divider, false otherwise." };
		}
		@Override
		protected String getHelp() {
			return "Locks the divider. A locked divider cannot be moved by the user. Returns true if lock status changed as a result, false otherwise.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebDividerPortlet> IS_LOCKED = new AmiAbstractMemberMethod<AmiWebDividerPortlet>(AmiWebDividerPortlet.class, "isLocked",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDividerPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsLocked(true);
		}
		@Override
		protected String getHelp() {
			return "Checks the lock status of a divider. Returns true if the divider is locked, false otherwise.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "DividerPanel";
	}

	@Override
	public String getVarTypeDescription() {
		return "A panel for Dividers";
	}

	@Override
	public Class<AmiWebDividerPortlet> getVarType() {
		return AmiWebDividerPortlet.class;
	}

	@Override
	public Class<AmiWebDividerPortlet> getVarDefaultImpl() {
		return null;
	}

	public static final AmiWebScriptMemberMethods_DividerPanel INSTANCE = new AmiWebScriptMemberMethods_DividerPanel();
}
