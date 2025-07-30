package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiService;
import com.f1.ami.web.AmiWebAliasPortlet;
import com.f1.ami.web.AmiWebManagerWindowsPortlet;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebWindow;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.utils.CH;
import com.f1.utils.WebRectangle;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_Window extends AmiWebScriptBaseMemberMethods<AmiWebWindow> {

	private AmiWebScriptMemberMethods_Window() {
		super();

		addMethod(GET_LOCATION, "location");
		addMethod(SET_LOCATION);
		addMethod(MINIMIZE);
		addMethod(MAXIMIZE);
		addMethod(RESTORE);
		addMethod(BRING_TO_FRONT);
		addMethod(POPOUT);
		addMethod(POPIN);
		addMethod(POPOUT2);
		addMethod(GET_STATUS, "status");
		addMethod(GET_NAME, "name");
		addMethod(GET_INNER_PANEL);
		addMethod(GET_TYPE);
		addMethod(SET_TYPE);
		addMethod(RESET_TYPE);
	}

	private static final AmiAbstractMemberMethod<AmiWebWindow> GET_INNER_PANEL = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "getInnerPanel",
			AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			Portlet p = targetObject.getPortlet();
			if (p == null)
				return null;
			if (AmiWebAliasPortlet.class.isInstance(p))
				return AmiWebAliasPortlet.class.cast(p);
			else
				return null;
		}
		@Override
		protected String getHelp() {
			return "Returns the root panel.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebWindow> GET_LOCATION = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "getLocation", WebRectangle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.isPoppedOut()) {
				RootPortlet rp = (RootPortlet) targetObject.getPortletForPopout().getParent();
				return new WebRectangle(rp.getScreenX(), rp.getScreenY(), rp.getWidth(), rp.getHeight());
			}
			return new WebRectangle(targetObject.getLeft(), targetObject.getTop(), targetObject.getWidth(), targetObject.getHeight());
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Returns the inner location of the window in pixels (not including header and border).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> SET_LOCATION = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "setLocation", Object.class, false,
			WebRectangle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			DesktopPortlet desktop = targetObject.getDesktop();
			WebRectangle t = (WebRectangle) params[0];
			if (targetObject.isPoppedOut()) {
				RootPortlet rp = (RootPortlet) targetObject.getPortletForPopout().getParent();
				rp.resizeTo(t.getLeft(), t.getTop(), t.getWidth(), t.getHeight());
				return null;
			}
			if (targetObject.isHidden(true) || targetObject.isMinimized())
				targetObject.floatWindow();
			targetObject.setLeft(Math.max(0, t.getLeft()));
			targetObject.setTop(Math.max(0, t.getTop()));
			targetObject.setWidth(Math.min(t.getWidth(), desktop.getWidth()));
			targetObject.setHeight(Math.min(t.getHeight(), desktop.getHeight()));
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] { "location" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "location in pixels" };
		}

		@Override
		protected String getHelp() {
			return "Sets the inner location of the window (not including header and border).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> BRING_TO_FRONT = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "bringToFront", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.bringToFront();
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Brings the window to the front of the desktop (in front of all other windows on the desktop).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> MINIMIZE = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "minimize", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.minimizeWindowForce();
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Minimizes the window (hide it from the desktop).";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> RESTORE = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "restore", Object.class, false) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.floatWindow();
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] {};
		};

		protected String[] buildParamDescriptions() {
			return new String[] {};
		};

		@Override
		protected String getHelp() {
			return "Restore window to original dimensions.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> MAXIMIZE = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "maximize", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.maximizeWindow();
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Maximizes the window on the desktop.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> POPOUT = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "popout", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.popoutWindow();
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Pops the window out of the browser's main window.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> POPIN = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "popin", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.closePopup();
			return null;
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Restores the popped out window back to the browser's main window.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> POPOUT2 = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "popout", Object.class, false,
			WebRectangle.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			WebRectangle rec = (WebRectangle) params[0];
			if (rec == null)
				targetObject.popoutWindow();
			else
				targetObject.popoutWindow(rec.getLeft(), rec.getTop(), rec.getWidth(), rec.getHeight());
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
			return "Pops the window out of the browser's main window.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> GET_STATUS = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "getStatus", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.isPoppedOut())
				return "POPPEDOUT";
			if (targetObject.isWindowMinimized())
				return "MINIMIZED";
			if (targetObject.isWindowMaximized())
				return "MAXIMIZED";
			if (targetObject.isWindowFloating())
				return "FLOATING";
			return "UNKNOWN";
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Returns the status of the window, either: POPPEDOUT, MINIMIZED, MAXIMIZED, or FLOATING.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> GET_NAME = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "getName", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getName();
		}

		@Override
		protected String getHelp() {
			return "Returns the name of the window.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebWindow> GET_TYPE = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "getType", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			return AmiWebManagerWindowsPortlet.formatType(AmiWebManagerWindowsPortlet.getType(targetObject, true));
		}
		@Override
		protected String getHelp() {
			return "Returns the current type, aka the behaviour of this window. Either " + AmiWebManagerWindowsPortlet.HIDDEN + ", "
					+ AmiWebManagerWindowsPortlet.MAXIMIZED_NO_HEADER + " or " + AmiWebManagerWindowsPortlet.REGULAR + ".";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> SET_TYPE = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "setType", Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {
			String type = (String) params[0];
			char c = AmiWebManagerWindowsPortlet.parseType(type, (char) 0);
			if (c == 0)
				return false;
			AmiWebManagerWindowsPortlet.applyType(c, targetObject, false); // this will force window to be closeable
			AmiWebService service = targetObject.getDesktop().getService();
			boolean dev = service.getWebState().isDev(); // check if ISDEV is true
			boolean inEdit = service.getDesktop().getInEditMode();
			if (!(dev && inEdit)) {
				targetObject.setCloseable(false, false);
			}
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "type" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {
					"Either " + AmiWebManagerWindowsPortlet.HIDDEN + ", " + AmiWebManagerWindowsPortlet.MAXIMIZED_NO_HEADER + " or " + AmiWebManagerWindowsPortlet.REGULAR };
		}

		@Override
		protected String getHelp() {
			return "Sets the current type, aka the behaviour of this window. Either " + AmiWebManagerWindowsPortlet.HIDDEN + ", " + AmiWebManagerWindowsPortlet.MAXIMIZED_NO_HEADER
					+ " or " + AmiWebManagerWindowsPortlet.REGULAR + ". Case sensitive.";
		}

		@Override
		public java.util.Map<String, String> getAutocompleteOptions(AmiService service) {
			return CH.m('"' + AmiWebManagerWindowsPortlet.HIDDEN + "\")", AmiWebManagerWindowsPortlet.HIDDEN, //
					'"' + AmiWebManagerWindowsPortlet.REGULAR + "\")", AmiWebManagerWindowsPortlet.REGULAR, //
					'"' + AmiWebManagerWindowsPortlet.MAXIMIZED_NO_HEADER + "\")", AmiWebManagerWindowsPortlet.MAXIMIZED_NO_HEADER);
		};
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	private static final AmiAbstractMemberMethod<AmiWebWindow> RESET_TYPE = new AmiAbstractMemberMethod<AmiWebWindow>(AmiWebWindow.class, "resetType", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebWindow targetObject, Object[] params, DerivedCellCalculator caller) {

			char dflt = AmiWebManagerWindowsPortlet.getType(targetObject, false);
			AmiWebManagerWindowsPortlet.applyType(dflt, targetObject, true);
			return AmiWebManagerWindowsPortlet.formatType(dflt);
		}
		@Override
		protected String getHelp() {
			return "Resets the type to this window's default type. Returns the default mode.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};

	@Override
	public String getVarTypeName() {
		return "Window";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<AmiWebWindow> getVarType() {
		return AmiWebWindow.class;
	}
	@Override
	public Class<AmiWebWindow> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_Window INSTANCE = new AmiWebScriptMemberMethods_Window();
}
