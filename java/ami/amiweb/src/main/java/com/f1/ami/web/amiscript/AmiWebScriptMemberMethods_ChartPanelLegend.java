package com.f1.ami.web.amiscript;

import java.util.Set;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.charts.AmiWebChartRenderingLayer_Legend;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_ChartPanelLegend extends AmiWebScriptBaseMemberMethods<AmiWebChartRenderingLayer_Legend> {

	private AmiWebScriptMemberMethods_ChartPanelLegend() {
		super();
		addMethod(GET_WIDTH, "width");
		addMethod(GET_HEIGHT, "height");
		addMethod(GET_TITLE, "title");
		addMethod(GET_REFERENCE_SERIES);

	}

	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer_Legend> GET_WIDTH = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer_Legend>(
			AmiWebChartRenderingLayer_Legend.class, "getWidth", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer_Legend targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMaxWidth();

		}

		@Override
		protected String getHelp() {
			return "Gets the width of the legend";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer_Legend> GET_HEIGHT = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer_Legend>(
			AmiWebChartRenderingLayer_Legend.class, "getHeight", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer_Legend targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMaxHeight();

		}

		@Override
		protected String getHelp() {
			return "Gets the height of the legend";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer_Legend> GET_TITLE = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer_Legend>(
			AmiWebChartRenderingLayer_Legend.class, "getTitle", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer_Legend targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getName();
		}

		@Override
		protected String getHelp() {
			return "Gets the title of the legend";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebChartRenderingLayer_Legend> GET_REFERENCE_SERIES = new AmiAbstractMemberMethod<AmiWebChartRenderingLayer_Legend>(
			AmiWebChartRenderingLayer_Legend.class, "getReferencedSeries", Set.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartRenderingLayer_Legend targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getReferencedSeries();
		}

		@Override
		protected String getHelp() {
			return "Returns the referenced series from the legend";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	@Override
	public String getVarTypeName() {
		return "ChartPanelLegend";
	}

	@Override
	public String getVarTypeDescription() {
		return "Panel for Chart Legend";
	}

	@Override
	public Class<AmiWebChartRenderingLayer_Legend> getVarType() {
		return AmiWebChartRenderingLayer_Legend.class;
	}

	@Override
	public Class<? extends AmiWebChartRenderingLayer_Legend> getVarDefaultImpl() {
		return null;
	}

	public static final AmiWebScriptMemberMethods_ChartPanelLegend INSTANCE = new AmiWebScriptMemberMethods_ChartPanelLegend();
}
