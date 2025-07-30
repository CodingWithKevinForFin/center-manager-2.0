package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebScrollPortlet;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.ScrollPortlet;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_ScrollPanel extends AmiWebScriptBaseMemberMethods<AmiWebScrollPortlet> {

	private AmiWebScriptMemberMethods_ScrollPanel() {
		super();
		addMethod(GET_INNER_PANEL, "innerPanel");
		addMethod(GET_SCROLL_POSITION_HORIZONTAL, "scrollPositionHorizontal");
		addMethod(GET_SCROLL_POSITION_VERTICAL, "scrollPositionVertical");
		addMethod(SET_SCROLL_POSITION_HORIZONTAL);
		addMethod(SET_SCROLL_POSITION_VERTICAL);
	}

	private static final AmiAbstractMemberMethod<AmiWebScrollPortlet> GET_INNER_PANEL = new AmiAbstractMemberMethod<AmiWebScrollPortlet>(AmiWebScrollPortlet.class, "getInnerPanel",
			AmiWebPortlet.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebScrollPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Portlet panel = targetObject.getAmiInnerPanel();
			if (panel instanceof AmiWebPortlet)
				return panel;
			else
				return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "Returns the inner panel of the Scroll Panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebScrollPortlet> GET_SCROLL_POSITION_HORIZONTAL = new AmiAbstractMemberMethod<AmiWebScrollPortlet>(AmiWebScrollPortlet.class,
			"getScrollPositionHorizontal", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebScrollPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			ScrollPortlet sp = (ScrollPortlet) targetObject.getInnerPortlet();
			return sp.getScrollPositionHorizontal();
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "Returns the horizontal scroll position.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebScrollPortlet> SET_SCROLL_POSITION_HORIZONTAL = new AmiAbstractMemberMethod<AmiWebScrollPortlet>(AmiWebScrollPortlet.class,
			"setScrollPositionHorizontal", String.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebScrollPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			ScrollPortlet sp = (ScrollPortlet) targetObject.getInnerPortlet();
			sp.setScrollPositionHorizontal(Caster_Integer.PRIMITIVE.cast(params[0]));
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "positionPx" };
		}
		@Override
		protected String getHelp() {
			return "Sets the horizontal scroll position.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebScrollPortlet> GET_SCROLL_POSITION_VERTICAL = new AmiAbstractMemberMethod<AmiWebScrollPortlet>(AmiWebScrollPortlet.class,
			"getScrollPositionVertical", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebScrollPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			ScrollPortlet sp = (ScrollPortlet) targetObject.getInnerPortlet();
			return sp.getScrollPositionVertical();
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "Returns the vertical scroll position.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebScrollPortlet> SET_SCROLL_POSITION_VERTICAL = new AmiAbstractMemberMethod<AmiWebScrollPortlet>(AmiWebScrollPortlet.class,
			"setScrollPositionVertical", String.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebScrollPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			ScrollPortlet sp = (ScrollPortlet) targetObject.getInnerPortlet();
			sp.setScrollPositionVertical(Caster_Integer.PRIMITIVE.cast(params[0]));
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "positionPx" };
		}
		@Override
		protected String getHelp() {
			return "Sets the vertical scroll position.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "ScrollPanel";
	}

	@Override
	public String getVarTypeDescription() {
		return "A panel for ScrollPane";
	}

	@Override
	public Class<AmiWebScrollPortlet> getVarType() {
		return AmiWebScrollPortlet.class;
	}

	@Override
	public Class<AmiWebScrollPortlet> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_ScrollPanel INSTANCE = new AmiWebScriptMemberMethods_ScrollPanel();
}
