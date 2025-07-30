package com.f1.ami.web.amiscript;

import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.surface.AmiWebSurfaceAxisPortlet;
import com.f1.ami.web.surface.AmiWebSurfacePortlet;
import com.f1.ami.web.surface.AmiWebSurfaceSeries;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_SurfacePanel extends AmiWebScriptBaseMemberMethods<AmiWebSurfacePortlet> {

	private AmiWebScriptMemberMethods_SurfacePanel() {
		super();
		addMethod(GET_LAYER);
		addMethod(GET_X_AXIS, "xAxis");
		addMethod(GET_Y_AXIS, "yAxis");
		addMethod(GET_Z_AXIS, "zAxis");
		addMethod(GET_LAYERS, "layers");
		addMethod(GET_LAYER_AT);
		addMethod(GET_LAYERS_COUNT);
	}

	@Override
	public String getVarTypeName() {
		return "SurfacePanel";
	}

	@Override
	public String getVarTypeDescription() {
		return "Panel for Surface";
	}

	@Override
	public Class<AmiWebSurfacePortlet> getVarType() {
		return AmiWebSurfacePortlet.class;
	}

	@Override
	public Class<AmiWebSurfacePortlet> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebSurfacePortlet> GET_LAYER = new AmiAbstractMemberMethod<AmiWebSurfacePortlet>(AmiWebSurfacePortlet.class, "getLayer",
			AmiWebSurfaceSeries.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfacePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			int position = Caster_Integer.PRIMITIVE.cast(params[0]);
			return targetObject.getSeriesAt(position);
		}

		@Override
		protected String getHelp() {
			return "Returns the layer specified by the position.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "position" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position of the layer (zero-index based)." };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebSurfacePortlet> GET_X_AXIS = new AmiAbstractMemberMethod<AmiWebSurfacePortlet>(AmiWebSurfacePortlet.class, "getXAxis",
			AmiWebSurfaceAxisPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfacePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAxisX();
		}

		@Override
		protected String getHelp() {
			return "Returns the x-axis of this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebSurfacePortlet> GET_Y_AXIS = new AmiAbstractMemberMethod<AmiWebSurfacePortlet>(AmiWebSurfacePortlet.class, "getYAxis",
			AmiWebSurfaceAxisPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfacePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAxisY();
		}

		@Override
		protected String getHelp() {
			return "Returns the y-axis of this panel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebSurfacePortlet> GET_Z_AXIS = new AmiAbstractMemberMethod<AmiWebSurfacePortlet>(AmiWebSurfacePortlet.class, "getZAxis",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfacePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAxisZ();
		}

		@Override
		protected String getHelp() {
			return "Returns the z-axis of this panel.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebSurfacePortlet> GET_LAYERS = new AmiAbstractMemberMethod<AmiWebSurfacePortlet>(AmiWebSurfacePortlet.class, "getLayers",
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfacePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getSeries());
		}

		@Override
		protected String getHelp() {
			return "Returns a list of layers contained by the panel.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebSurfacePortlet> GET_LAYER_AT = new AmiAbstractMemberMethod<AmiWebSurfacePortlet>(AmiWebSurfacePortlet.class, "getLayerAt",
			AmiWebSurfaceSeries.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfacePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			int pos = Caster_Integer.PRIMITIVE.cast(params[0]);
			if (pos >= targetObject.getSeriesCount() || pos < 0)
				return null;
			return targetObject.getSeriesAt(pos);
		}

		@Override
		protected String getHelp() {
			return "Returns the layer specified by the position. Returns null on invalid position.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "position" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position (zero-index based)." };
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebSurfacePortlet> GET_LAYERS_COUNT = new AmiAbstractMemberMethod<AmiWebSurfacePortlet>(AmiWebSurfacePortlet.class,
			"getLayersCount", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebSurfacePortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSeriesCount();
		}

		@Override
		protected String getHelp() {
			return "Returns the number of layers contained in the panel.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public final static AmiWebScriptMemberMethods_SurfacePanel INSTANCE = new AmiWebScriptMemberMethods_SurfacePanel();
}
