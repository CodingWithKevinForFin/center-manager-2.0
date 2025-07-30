package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_KeyEvent extends AmiWebScriptBaseMemberMethods<KeyEvent> {

	private AmiWebScriptMemberMethods_KeyEvent() {
		super();

		addMethod(GET_KEY, "key");
		addMethod(GET_EVENT_TYPE, "eventType");
		addMethod(GET_ALT_KEY_DOWN, "altKeyDown");
		addMethod(GET_CTRL_KEY_DOWN, "ctrlKeyDown");
		addMethod(GET_SHIFT_KEY_DOWN, "shiftKeyDown");
		addMethod(GET_TARGET_PANEL, "targetPanel");
		addMethod(GET_TARGET_FIELD, "targetField");
		addMethod(GET_JUST_ALT_KEY_DOWN);
		addMethod(GET_JUST_CTRL_KEY_DOWN);
		addMethod(GET_JUST_SHIFT_KEY_DOWN);
	}

	public static final AmiAbstractMemberMethod<KeyEvent> GET_KEY = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getKey", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getKey();
		}
		@Override
		protected String getHelp() {
			return "Returns the key pressed.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	public static final AmiAbstractMemberMethod<KeyEvent> GET_ALT_KEY_DOWN = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getAltKeyDown", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isAltKey();
		}
		@Override
		protected String getHelp() {
			return "Returns true if alt key was pressed in a Key Event.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	public static final AmiAbstractMemberMethod<KeyEvent> GET_CTRL_KEY_DOWN = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getCtrlKeyDown", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isCtrlKey();
		}
		@Override
		protected String getHelp() {
			return "Returns true if ctrl key was pressed in a Key Event.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<KeyEvent> GET_SHIFT_KEY_DOWN = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getShiftKeyDown", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isShiftKey();
		}
		@Override
		protected String getHelp() {
			return "Returns true if shift key was pressed in a Key Event.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<KeyEvent> GET_TARGET_PANEL = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getTargetPanel", AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			final Portlet portlet = targetObject.getTargetPortlet();
			return PortletHelper.findParentByType(portlet, AmiWebPortlet.class);
		}
		@Override
		protected String getHelp() {
			return "Returns the panel that has focus at the time of this key event.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<KeyEvent> GET_TARGET_FIELD = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getTargetField", QueryField.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.getTargetAttachmentId() != null) {
				final AmiWebPortlet p = PortletHelper.findParentByType(targetObject.getTargetPortlet(), AmiWebPortlet.class);
				if (p instanceof AmiWebQueryFormPortlet)
					return ((AmiWebQueryFormPortlet) p).getFieldsById().get(targetObject.getTargetAttachmentId());
			}
			return null;
		}
		@Override
		protected String getHelp() {
			return "Returns the field that has focus at the time of this key event(or null if no field has focus).";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<KeyEvent> GET_JUST_ALT_KEY_DOWN = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getJustAltKeyDown", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isJustAltKey();
		}
		@Override
		protected String getHelp() {
			return "Returns true if just the alt key was pressed in a Key Event.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<KeyEvent> GET_JUST_CTRL_KEY_DOWN = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getJustCtrlKeyDown", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isJustCtrlKey();
		}
		@Override
		protected String getHelp() {
			return "Returns true if just the ctrl key was pressed in a Key Event.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<KeyEvent> GET_JUST_SHIFT_KEY_DOWN = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getJustShiftKeyDown", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isJustShiftKey();
		}
		@Override
		protected String getHelp() {
			return "Returns true if just the shift key was pressed in a Key Event.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<KeyEvent> GET_EVENT_TYPE = new AmiAbstractMemberMethod<KeyEvent>(KeyEvent.class, "getEventType", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, KeyEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			switch (targetObject.getKeyEvent()) {
				case KeyEvent.KEYDOWN:
					return "KEYDOWN";
				case KeyEvent.KEYUP:
					return "KEYUP";
				case KeyEvent.KEYPRESS:
					return "KEYPRESS";
				default:
					return "UNKNOWN: " + targetObject.getKeyEvent();
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the type of event, either KEYUP, KEYDOWN, or KEYPRESS.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "KeyEvent";
	}
	@Override
	public String getVarTypeDescription() {
		return "An event triggered by pressing a key.";
	}
	@Override
	public Class<KeyEvent> getVarType() {
		return KeyEvent.class;
	}
	@Override
	public Class<KeyEvent> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_KeyEvent INSTANCE = new AmiWebScriptMemberMethods_KeyEvent();
}
