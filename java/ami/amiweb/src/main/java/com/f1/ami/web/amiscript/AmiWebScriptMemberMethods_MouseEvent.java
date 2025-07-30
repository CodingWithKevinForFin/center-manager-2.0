package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.utils.OH;
import com.f1.utils.WebPoint;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_MouseEvent extends AmiWebScriptBaseMemberMethods<MouseEvent> {

	private AmiWebScriptMemberMethods_MouseEvent() {
		super();

		addMethod(GET_X, "x");
		addMethod(GET_Y, "y");
		addMethod(GET_POSITION, "position");
		addMethod(GET_RELATIVE_POSITION);
		addMethod(GET_BUTTON, "button");
		addMethod(GET_EVENT, "event");
		addMethod(GEY_ALT_KEY_DOWN, "altKeyDown");
		addMethod(GET_SHIFT_KEY_DOWN, "shiftKeyDown");
		addMethod(GET_CTRL_KEY_DOWN, "ctrlKeyDown");
	}

	private static final AmiAbstractMemberMethod<MouseEvent> GET_POSITION = new AmiAbstractMemberMethod<MouseEvent>(MouseEvent.class, "getPosition", WebPoint.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MouseEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return new WebPoint(targetObject.getMouseX(), targetObject.getMouseY());
		}
		@Override
		protected String getHelp() {
			return "Returns the absolute x, y position.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<MouseEvent> GET_RELATIVE_POSITION = new AmiAbstractMemberMethod<MouseEvent>(MouseEvent.class, "getRelativePosition",
			WebPoint.class, AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MouseEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebPortlet portlet = (AmiWebPortlet) params[0];
			if (portlet == null)
				return null;
			int left = PortletHelper.getAbsoluteLeft(portlet);
			int top = PortletHelper.getAbsoluteTop(portlet);
			return new WebPoint(targetObject.getMouseX() - left, targetObject.getMouseY() - top);
		}

		public String[] buildParamNames() {
			return new String[] { "panel" };
		};
		public String[] buildParamDescriptions() {
			return new String[] { "panel to get the relative position from" };
		};
		@Override
		protected String getHelp() {
			return "Returns the x,y position relative to a panel. Typically, in the onMouse(...) callback you would do:  Point p=event.getRelativePosition(this);";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<MouseEvent> GET_X = new AmiAbstractMemberMethod<MouseEvent>(MouseEvent.class, "getX", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MouseEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMouseX();
		}
		@Override
		protected String getHelp() {
			return "Returns the absolute x position. Same as getPosition().getX()";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<MouseEvent> GET_Y = new AmiAbstractMemberMethod<MouseEvent>(MouseEvent.class, "getY", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MouseEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMouseY();
		}
		@Override
		protected String getHelp() {
			return "Returns the absolute y position. Same as getPosition().getY()";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<MouseEvent> GET_BUTTON = new AmiAbstractMemberMethod<MouseEvent>(MouseEvent.class, "getButton", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MouseEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			switch (targetObject.getButton()) {
				case 0:
					return "LEFT";
				case 1:
					return "MIDDLE";
				case 2:
					return "RIGHT";
				default:
					return OH.toString(targetObject.getButton());
			}
		}
		@Override
		protected String getHelp() {
			return "Returns LEFT, MIDDLE or RIGHT.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<MouseEvent> GET_CTRL_KEY_DOWN = new AmiAbstractMemberMethod<MouseEvent>(MouseEvent.class, "getCtrlKeyDown", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MouseEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isCtrlKey();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the ctrl key was pressed during the mouse event.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<MouseEvent> GEY_ALT_KEY_DOWN = new AmiAbstractMemberMethod<MouseEvent>(MouseEvent.class, "getAltKeyDown", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MouseEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isAltKey();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the alt key was pressed during the mouse event.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<MouseEvent> GET_SHIFT_KEY_DOWN = new AmiAbstractMemberMethod<MouseEvent>(MouseEvent.class, "getShiftKeyDown", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MouseEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isShiftKey();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the shift key was pressed during the mouse event.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<MouseEvent> GET_EVENT = new AmiAbstractMemberMethod<MouseEvent>(MouseEvent.class, "getEvent", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, MouseEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			switch (targetObject.getMouseEvent()) {
				case MouseEvent.CLICK:
					return "CLICK";
				case MouseEvent.CONTEXTMENU:
					return "CONTEXTMENU";
				case MouseEvent.DOUBLECLICK:
					return "DOUBLECLICK";
				case MouseEvent.MOUSEDOWN:
					return "DOWN";
				case MouseEvent.MOUSEUP:
					return "UP";
				default:
					return OH.toString(targetObject.getButton());
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the type of mouse event. The available types (code) are CLICK (0), CONTEXTMENU (1), DOUBLECLICK (2), MOUSE DOWN (3), or MOUSE UP (4). Note that each mouse click will fire this 3 times (mouse down -> mouse up -> click)";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "MouseEvent";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<MouseEvent> getVarType() {
		return MouseEvent.class;
	}
	@Override
	public Class<MouseEvent> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_MouseEvent INSTANCE = new AmiWebScriptMemberMethods_MouseEvent();
}
