package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.surface.AmiWebSurfaceAxisPortlet;
import com.f1.ami.web.surface.AmiWebSurfacePortlet;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_SurfacePanelAxis extends AmiWebScriptBaseMemberMethods<AmiWebSurfaceAxisPortlet> {

	private AmiWebScriptMemberMethods_SurfacePanelAxis() {
		super();

		addMethod(getAxisTitle, "axisTitle");
		addMethod(getSurface, "surface");
		addMethod(getMinValue, "minValue");
		addMethod(getMaxValue, "maxValue");
		addMethod(isReverse, "isReverse");
	}

	@Override
	public String getVarTypeName() {
		return "SurfacePanelAxis";
	}

	@Override
	public String getVarTypeDescription() {
		return "Axis for Surface Panel";
	}

	@Override
	public Class<AmiWebSurfaceAxisPortlet> getVarType() {
		return AmiWebSurfaceAxisPortlet.class;
	}

	@Override
	public Class<AmiWebSurfaceAxisPortlet> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet> getAxisTitle = new AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet>(AmiWebSurfaceAxisPortlet.class,
			"getAxisTitle", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfaceAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTitle();
		}

		@Override
		protected String getHelp() {
			return "Returns the title of the axis.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet> getSurface = new AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet>(AmiWebSurfaceAxisPortlet.class,
			"getSurface", AmiWebSurfacePortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfaceAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getChart();
		}

		@Override
		protected String getHelp() {
			return "Returns the surface panel this axis belongs to.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet> getMinValue = new AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet>(AmiWebSurfaceAxisPortlet.class,
			"getMinValue", Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfaceAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinValue();
		}

		@Override
		protected String getHelp() {
			return "Returns the minimum value set for the axis.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet> getMaxValue = new AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet>(AmiWebSurfaceAxisPortlet.class,
			"getMaxValue", Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfaceAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMaxValue();
		}

		@Override
		protected String getHelp() {
			return "Returns the maximum value set for the axis.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet> isReverse = new AmiAbstractMemberMethod<AmiWebSurfaceAxisPortlet>(AmiWebSurfaceAxisPortlet.class,
			"isReverse", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfaceAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isReverse();
		}

		@Override
		protected String getHelp() {
			return "Returns true if the axis is reversed, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	public final static AmiWebScriptMemberMethods_SurfacePanelAxis INSTANCE = new AmiWebScriptMemberMethods_SurfacePanelAxis();
}
