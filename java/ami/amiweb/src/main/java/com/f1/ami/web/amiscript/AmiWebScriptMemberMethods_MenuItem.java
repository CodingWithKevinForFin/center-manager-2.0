package com.f1.ami.web.amiscript;

import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.menu.AmiWebCustomContextMenu;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_MenuItem extends AmiWebScriptBaseMemberMethods<AmiWebCustomContextMenu> {

	private AmiWebScriptMemberMethods_MenuItem() {
		super();
		addMethod(GET_PANEL, "panel");
		addMethod(GET_ID, "id");
		addMethod(GET_SUB_MENU_ITEMS, "subMenuItems");
		addMethod(GET_PARENT_MENU_ITEM, "parentMenuItem");
		registerCallbackDefinition(AmiWebCustomContextMenu.PARAMDEF_ON_SELECTED);
	}

	private static final AmiAbstractMemberMethod<AmiWebCustomContextMenu> GET_ID = new AmiAbstractMemberMethod<AmiWebCustomContextMenu>(AmiWebCustomContextMenu.class, "getId",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomContextMenu targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getId();
		}

		@Override
		protected String getHelp() {
			return "Returns the id of the menu.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebCustomContextMenu> GET_PANEL = new AmiAbstractMemberMethod<AmiWebCustomContextMenu>(AmiWebCustomContextMenu.class, "getPanel",
			AmiWebPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomContextMenu targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebDomObject r = targetObject.getTargetPortlet();
			if (r instanceof AmiWebPortlet)
				return r;
			else
				return null;
		}

		@Override
		protected String getHelp() {
			return "Returns the underlying panel..";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebCustomContextMenu> GET_SUB_MENU_ITEMS = new AmiAbstractMemberMethod<AmiWebCustomContextMenu>(AmiWebCustomContextMenu.class,
			"getSubMenuItems", List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomContextMenu targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getChildren());
		}

		@Override
		protected String getHelp() {
			return "Returns a list of child menu items.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebCustomContextMenu> GET_PARENT_MENU_ITEM = new AmiAbstractMemberMethod<AmiWebCustomContextMenu>(AmiWebCustomContextMenu.class,
			"getParentMenuItem", AmiWebCustomContextMenu.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCustomContextMenu targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getParent();
		}

		@Override
		protected String getHelp() {
			return "Returns the parent menu item, or null if this is the root.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "MenuItem";
	}

	@Override
	public String getVarTypeDescription() {
		return "A custom menu item. Can implement AmiScript and take on the following modes: enabled, disabled, invisible, and divider.";
	}

	@Override
	public Class<AmiWebCustomContextMenu> getVarType() {
		return AmiWebCustomContextMenu.class;
	}

	@Override
	public Class<AmiWebCustomContextMenu> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_MenuItem INSTANCE = new AmiWebScriptMemberMethods_MenuItem();
}
