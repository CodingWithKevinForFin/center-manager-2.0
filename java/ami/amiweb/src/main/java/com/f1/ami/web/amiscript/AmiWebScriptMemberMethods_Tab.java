package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebTabEntry;
import com.f1.ami.web.AmiWebTabPortlet;
import com.f1.ami.web.AmiWebWindow;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.TabPlaceholderPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.WebRectangle;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_Tab extends AmiWebScriptBaseMemberMethods<AmiWebTabEntry> {

	private AmiWebScriptMemberMethods_Tab() {
		super();
		addMethod(GET_ID, "id");
		addMethod(GET_POSITION, "position");
		addMethod(SET_POSITION);
		addMethod(GET_TITLE, "title");
		addMethod(GET_TABS_PANEL);
		addMethod(GET_INNER_PANEL, "innerPanel");
		addMethod(HIDE_TAB);
		addMethod(SHOW_TAB);
		addMethod(IS_HIDDEN);
		addMethod(SET_HIDDEN);
		addMethod(POPOUT);
		addMethod(POPOUT2);
		addMethod(UNDOCK);
		addMethod(UNDOCK2);
		addMethod(REDOCK);
		addMethod(GET_UNDOCKED_WINDOW, "undockedWindow");
		addMethod(GET_STATUS, "status");
		addMethod(GET_BLINK_COLOR_FORMULA, "blinkColorFormula");
		addMethod(SET_BLINK_COLOR_FORMULA);
		addMethod(GET_BLINK_PERIOD_FORMULA, "blinkPeriodFormula");
		addMethod(SET_BLINK_PERIOD_FORMULA);
		addMethod(SET_TITLE);
		addMethod(GET_SELECT_COLOR_FORMULA, "selectColorFormula");
		addMethod(SET_SELECT_COLOR_FORMULA);
		addMethod(GET_UNSELECT_COLOR_FORMULA, "unselectColorFormula");
		addMethod(SET_UNSELECT_COLOR_FORMULA);
		addMethod(GET_SELECT_TEXT_COLOR_FORMULA, "selectTextColorFormula");
		addMethod(SET_SELECT_TEXT_COLOR_FORMULA);
		addMethod(GET_UNSELECT_TEXT_COLOR_FORMULA, "unselectTextColorFormula");
		addMethod(SET_UNSELECT_TEXT_COLOR_FORMULA);
	}

	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_UNSELECT_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"getUnselectColorFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getUnselectColorFormula().getFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the background color formula of the tab when not selected/active.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_SELECT_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"getSelectColorFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSelectColorFormula().getFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the background color formula of the tab when selected/active.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_SELECT_TEXT_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"getSelectTextColorFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSelectTextColorFormula().getFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the font color formula of the tab title when selected/active.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_UNSELECT_TEXT_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"getUnselectTextColorFormula", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getUnselectTextColorFormula().getFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Gets the font color formula of the tab title when not selected/active.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SET_UNSELECT_TEXT_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"setUnselectTextColorFormula", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String unselectTextColorFormula = SH.doubleQuote(Caster_String.INSTANCE.cast(params[0]));
				targetObject.getUnselectTextColorFormula().setFormula(unselectTextColorFormula, true);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Could not set unselect text color formula", params[0], e);
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "unselect text color formula" };
		}
		@Override
		protected String getHelp() {
			return "Sets the font color formula of the tab title when not selected/active.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SET_SELECT_TEXT_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"setSelectTextColorFormula", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String selectTextColorFormula = SH.doubleQuote(Caster_String.INSTANCE.cast(params[0]));
				targetObject.getSelectTextColorFormula().setFormula(selectTextColorFormula, true);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Could not set select text color formula", params[0], e);
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "select text color formula" };
		}
		@Override
		protected String getHelp() {
			return "Sets the font color formula of the tab title when selected/active.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SET_UNSELECT_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"setUnselectColorFormula", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String unselectColorFormula = SH.doubleQuote(Caster_String.INSTANCE.cast(params[0]));
				targetObject.getUnselectColorFormula().setFormula(unselectColorFormula, true);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Could not set unselect color formula", params[0], e);
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "unselect color formula" };
		}
		@Override
		protected String getHelp() {
			return "Sets the background color formula of the tab when not selected/active.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SET_SELECT_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"setSelectColorFormula", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String selectColorFormula = SH.doubleQuote(Caster_String.INSTANCE.cast(params[0]));
				targetObject.getSelectColorFormula().setFormula(selectColorFormula, true);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Could not set select color formula", params[0], e);
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "select color formula" };
		}
		@Override
		protected String getHelp() {
			return "Sets the background color formula of the tab when selected/active.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SET_TITLE = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "setTitle", Boolean.class,
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String titleFormula = SH.doubleQuote(Caster_String.INSTANCE.cast(params[0]));
				targetObject.getNameFormula().setFormula(titleFormula, true);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Could not set title formula", params[0], e);
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "title formula" };
		}
		@Override
		protected String getHelp() {
			return "Sets the title formula with the given name.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_BLINK_PERIOD_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"getBlinkPeriodFormula", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return Caster_Integer.INSTANCE.cast(targetObject.getBlinkPeriodFormula().getFormula(true));
		}

		@Override
		protected String getHelp() {
			return "Returns an Integer that is the blink period formula.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SET_BLINK_PERIOD_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class,
			"setBlinkPeriodFormula", Boolean.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String blinkPeriodFormula = Caster_String.INSTANCE.cast(params[0]);
				targetObject.getBlinkPeriodFormula().setFormula(blinkPeriodFormula, true);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Could not apply blink period formula", params[0], e);
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "blink period formula in milliseconds" };
		}
		@Override
		protected String getHelp() {
			return "Sets the blink period formula (i.e myTab.setBlinkPeriodFormula(500) would blink myTab every 500 milliseconds). Passing null or a number less than 100 defaults to 100.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_BLINK_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "getBlinkColorFormula",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getBlinkColorFormula().getFormula(true);
		}

		@Override
		protected String getHelp() {
			return "Returns the blink color formula.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SET_BLINK_COLOR_FORMULA = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "setBlinkColorFormula",
			Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String colorFormula = SH.doubleQuote(Caster_String.INSTANCE.cast(params[0]));
				targetObject.getBlinkColorFormula().setFormula(colorFormula, true);
				return true;
			} catch (Exception e) {
				LH.warning(log, "Could not apply blink color formula", params[0], e);
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "formula" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "blink color formula" };
		}
		@Override
		protected String getHelp() {
			return "Sets the blink color formula. Pass null or empty string to stop blinking.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> IS_HIDDEN = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "isHidden", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTab().isHidden();
		}

		@Override
		protected String getHelp() {
			return "Returns true if the tab is hidden, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> HIDE_TAB = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "hideTab", AmiWebTabEntry.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebTabPortlet awTabPortlet = targetObject.getOwner();
			TabPortlet tabPortlet = awTabPortlet.getTabPortlet();
			targetObject.setHidden(true, true);
			int location = targetObject.getTab().getLocation();
			for (int i = location + 1; i < tabPortlet.getTabsCount(); i++)
				if (!tabPortlet.getTabAtLocation(i).isHidden()) {
					tabPortlet.selectTab(i);
					return targetObject;
				}
			for (int i = location - 1; i >= 0; i--) {
				if (!tabPortlet.getTabAtLocation(i).isHidden()) {
					tabPortlet.selectTab(i);
					return targetObject;
				}
			}
			tabPortlet.selectTab(-1);
			return targetObject;
		}

		@Override
		protected String getHelp() {
			return "Hides this tab from the tabs bar and selects the nearest visible tab.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SHOW_TAB = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "showTab", AmiWebTabEntry.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setHidden(false, true);
			TabPortlet tabPortlet = targetObject.getTab().getTabPortlet();
			tabPortlet.selectTab(targetObject.getTab().getLocation());
			return targetObject;
		}

		@Override
		protected String getHelp() {
			return "Shows this tab in the tabs bar and makes it the selected tab.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SET_HIDDEN = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "setHidden", AmiWebTabEntry.class,
			Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			Boolean hidden = (Boolean) params[0];
			if (hidden == null)
				targetObject.setHidden(targetObject.getHidden(false), false);
			else
				targetObject.setHidden(hidden, true);
			return targetObject;
		}
		protected String[] buildParamNames() {
			return new String[] { "isHidden" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "true=hidden, false=visible,null=default for this tab" };
		}

		@Override
		protected String getHelp() {
			return "Changes the visiblity of this tab in the tab bar. Note, this does NOT change the currently selected tab, see showTab() and hideTab() for also updating tab selection.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_POSITION = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "getPosition", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			Tab tab = targetObject.getTab();
			return tab.getLocation();
		}

		@Override
		protected String getHelp() {
			return "Returns the zero-indexed position of this tab.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_TITLE = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "getTitle", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTab().getTitle();
		}

		@Override
		protected String getHelp() {
			return "Returns the title of the tab.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_ID = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "getId", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getId();
		}

		@Override
		protected String getHelp() {
			return "Returns the id of the tab.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> SET_POSITION = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "setPosition", Integer.class,
			Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer position = (Integer) params[0];
			if (position == null)
				position = targetObject.getLocation(false);
			position = MH.clip((int) position, 0, targetObject.getOwner().getTabsCount() - 1);
			targetObject.setLocation(position, true);
			targetObject.getOwner().onTabLocationChanged(true);
			return targetObject.getLocation(true);
		}

		protected String[] buildParamNames() {
			return new String[] { "position" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero indexed position to move tab to, null or negative will set to first location and numbers exceeding tab count will move to end" };
		}
		@Override
		protected String getHelp() {
			return "Set the zero-indexed position of the tab.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_TABS_PANEL = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "getTabsPanel",
			AmiWebTabPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getOwner();
		}

		@Override
		protected String getHelp() {
			return "Returns the tab panel that this tab is a member of.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_INNER_PANEL = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "getInnerPanel",
			AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			Portlet r = targetObject.getTab().getPortlet();
			if (r instanceof TabPlaceholderPortlet) {
				r = ((TabPlaceholderPortlet) r).getTearoutPortlet();
			}
			return r;
		}

		@Override
		protected String getHelp() {
			return "Returns the panel that lives in this tab.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> POPOUT = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "popout", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			Tab tab = targetObject.getTab();
			targetObject.getOwner().popoutTab(tab);
			return null;
		}

		@Override
		protected String getHelp() {
			return "Pops out the tab.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> POPOUT2 = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "popout", Object.class, false,
			WebRectangle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			WebRectangle rec = (WebRectangle) params[0];
			Tab tab = targetObject.getTab();
			if (rec == null)
				targetObject.getOwner().popoutTab(tab);
			else
				targetObject.getOwner().popoutTab(tab, rec.getLeft(), rec.getTop(), rec.getWidth(), rec.getHeight());
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "position" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "absolute position on the screen" };
		}

		@Override
		protected String getHelp() {
			return "Pops out the tab at a specific location.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> UNDOCK2 = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "undock", Object.class, false,
			WebRectangle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			WebRectangle t = (WebRectangle) params[0];
			Tab tab = targetObject.getTab();
			targetObject.getOwner().undockTab(tab);
			if (t != null && tab.getPortlet() instanceof TabPlaceholderPortlet) {
				TabPlaceholderPortlet tpp = (TabPlaceholderPortlet) tab.getPortlet();
				Window w = tpp.getTearoutWindow();
				w.setLeft(Math.max(0, t.getLeft()));
				w.setTop(Math.max(0, t.getTop()));
				w.setWidth(Math.min(t.getWidth(), w.getDesktop().getWidth()));
				w.setHeight(Math.min(t.getHeight(), w.getDesktop().getHeight()));
			}
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "position" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position on desktop" };
		}

		@Override
		protected String getHelp() {
			return "Undocks the tab at a specific location.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebTabEntry> UNDOCK = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "undock", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			Tab tab = targetObject.getTab();
			targetObject.getOwner().undockTab(tab);
			return null;
		}

		@Override
		protected String getHelp() {
			return "Undocks the tab.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> REDOCK = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "redock", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			Tab tab = targetObject.getTab();
			targetObject.getOwner().redockTab(tab);
			return null;
		}

		@Override
		protected String getHelp() {
			return "Redocks the tab.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_STATUS = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "getStatus", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			Tab tab = targetObject.getTab();
			if (tab.getPortlet() instanceof TabPlaceholderPortlet) {
				TabPlaceholderPortlet tpp = (TabPlaceholderPortlet) tab.getPortlet();
				if (tpp.getTearoutWindow().isPoppedOut())
					return "POPPEDOUT";
				else
					return "UNDOCKED";
			} else
				return "DOCKED";
		}

		@Override
		protected String getHelp() {
			return "Returns the status of the tab, either: DOCKED, UNDOCKED or POPPEDOUT.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebTabEntry> GET_UNDOCKED_WINDOW = new AmiAbstractMemberMethod<AmiWebTabEntry>(AmiWebTabEntry.class, "getUndockedWindow",
			AmiWebWindow.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebTabEntry targetObject, Object[] params, DerivedCellCalculator caller) {
			Tab tab = targetObject.getTab();
			if (tab.getPortlet() instanceof TabPlaceholderPortlet) {
				TabPlaceholderPortlet tpp = (TabPlaceholderPortlet) tab.getPortlet();
				Window r = tpp.getTearoutWindow();
				if (r instanceof AmiWebWindow)
					return (AmiWebWindow) r;
			}
			return null;
		}

		@Override
		protected String getHelp() {
			return "Returns the window that this undocked tab is contained in. If not undocked returns null";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	@Override
	public String getVarTypeName() {
		return "Tab";
	}

	@Override
	public String getVarTypeDescription() {
		return "A Panel of Tabs for containing sub panels";
	}
	@Override
	public Class<AmiWebTabEntry> getVarType() {
		return AmiWebTabEntry.class;
	}
	@Override
	public Class<AmiWebTabEntry> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_Tab INSTANCE = new AmiWebScriptMemberMethods_Tab();
}
