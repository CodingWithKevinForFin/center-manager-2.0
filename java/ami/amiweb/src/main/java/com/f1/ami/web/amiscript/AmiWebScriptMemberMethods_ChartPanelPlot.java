package com.f1.ami.web.amiscript;

import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.charts.AmiWebChartPlotPortlet;
import com.f1.ami.web.charts.AmiWebChartRenderingLayer;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_ChartPanelPlot extends AmiWebScriptBaseMemberMethods<AmiWebChartPlotPortlet> {

	private AmiWebScriptMemberMethods_ChartPanelPlot() {
		super();
		addMethod(GET_ROW, "row");
		addMethod(GET_COLUMN, "column");
		addMethod(GET_LAYERS, "layers");
		addMethod(GET_LAYER);
		addMethod(GET_LAYERS_COUNT);
	}

	@Override
	public String getVarTypeName() {
		return "ChartPanelPlot";
	}

	@Override
	public String getVarTypeDescription() {
		return "Panel for Chart Plot";
	}

	@Override
	public Class<AmiWebChartPlotPortlet> getVarType() {
		return AmiWebChartPlotPortlet.class;
	}

	@Override
	public Class<AmiWebChartPlotPortlet> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebChartPlotPortlet> GET_ROW = new AmiAbstractMemberMethod<AmiWebChartPlotPortlet>(AmiWebChartPlotPortlet.class, "getRow",
			Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartPlotPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRow();
		}

		@Override
		protected String getHelp() {
			return "return the row that this plot is located within the chart (top is zero)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebChartPlotPortlet> GET_COLUMN = new AmiAbstractMemberMethod<AmiWebChartPlotPortlet>(AmiWebChartPlotPortlet.class, "getColumn",
			Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartPlotPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getCol();
		}

		@Override
		protected String getHelp() {
			return "return the column that this plot is located within the chart (left is zero)";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebChartPlotPortlet> GET_LAYER = new AmiAbstractMemberMethod<AmiWebChartPlotPortlet>(AmiWebChartPlotPortlet.class, "getLayer",
			AmiWebChartRenderingLayer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartPlotPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			int zPos = Caster_Integer.PRIMITIVE.cast(params[0]);
			int layersCount = targetObject.getRenderyingLayersCount();
			if (zPos < 0 || zPos >= layersCount)
				return null;
			return targetObject.getRenderyingLayerAt(zPos);
		}

		@Override
		protected String getHelp() {
			return "Returns the layer specified by z-position. Null if position is invalid.";
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

	private static final AmiAbstractMemberMethod<AmiWebChartPlotPortlet> GET_LAYERS = new AmiAbstractMemberMethod<AmiWebChartPlotPortlet>(AmiWebChartPlotPortlet.class, "getLayers",
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartPlotPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getRenderyingLayers());
		}

		@Override
		protected String getHelp() {
			return "Returns a list of layers contained by the plot.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartPlotPortlet> GET_LAYERS_COUNT = new AmiAbstractMemberMethod<AmiWebChartPlotPortlet>(AmiWebChartPlotPortlet.class,
			"getLayersCount", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartPlotPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRenderyingLayersCount();
		}

		@Override
		protected String getHelp() {
			return "Returns the number of layers contained by the plot.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	public static final AmiWebScriptMemberMethods_ChartPanelPlot INSTANCE = new AmiWebScriptMemberMethods_ChartPanelPlot();
}
