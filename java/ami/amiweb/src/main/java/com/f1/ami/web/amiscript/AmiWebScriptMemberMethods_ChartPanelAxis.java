package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.charts.AmiWebChartAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartGridPortlet;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_ChartPanelAxis extends AmiWebScriptBaseMemberMethods<AmiWebChartAxisPortlet> {

	private AmiWebScriptMemberMethods_ChartPanelAxis() {
		super();

		addMethod(GET_AXIS_TITLE, "axisTitle");
		addMethod(SET_AXIS_TITLE);
		addMethod(GET_CHART, "chart");
		addMethod(IS_REVERSE, "isReverse");
		addMethod(IS_VERTICAL, "isVertical");
		addMethod(IS_ZOOMED, "isZoomed");
		addMethod(GET_OFFSET, "offset");
		addMethod(GET_PLOT_POSITIOn, "plotPosition");
		addMethod(GET_MIN_VALUE, "minValue");
		addMethod(GET_MAX_VALUE, "maxValue");
		addMethod(GET_ZOOM, "zoom");
		addMethod(GET_VISIBLE_OFFSET, "visibleOffset");
		addMethod(GET_VISIBLE_LENGTH, "visibleLength");
		addMethod(GET_VISIBLE_MAX_VALUE, "visibleMaxValue");
		addMethod(GET_VISIBLE_MIN_VALUE, "visibleMinValue");
		addMethod(SET_ZOOM);
		addMethod(SET_VISIBLE_OFFSET);
		addMethod(SET_ZOOM_BY_MIN_MAX_VALUE);
		addMethod(SET_MIN_VALUE);
		addMethod(SET_MAX_VALUE);
		addMethod(SET_MAJOR_TICKS_UNIT);
		addMethod(SET_MINOR_TICKS_UNIT);
	}

	@Override
	public String getVarTypeName() {
		return "ChartPanelAxis";
	}

	@Override
	public String getVarTypeDescription() {
		return "Panel for Chart Axis";
	}

	@Override
	public Class<AmiWebChartAxisPortlet> getVarType() {
		return AmiWebChartAxisPortlet.class;
	}

	@Override
	public Class<AmiWebChartAxisPortlet> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_AXIS_TITLE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"getAxisTitle", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTitleOverride();
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
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> SET_AXIS_TITLE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"setAxisTitle", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String title = (String) params[0];
			StringBuilder sink = new StringBuilder();
			try {
				targetObject.setTitleOverride(title);
				return true;
			} catch (Exception e) {
				warning(sf, "Could not set Axis Title", CH.m("title", title, "Error", sink), e);
				return false;

			}
		}

		protected String[] buildParamNames() {
			return new String[] { "title" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Axis title, ex: Country" };
		}
		@Override
		protected String getHelp() {
			return "Sets the title of the axis to specified value.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_CHART = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class, "getChart",
			AmiWebChartGridPortlet.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getChart();
		}

		@Override
		protected String getHelp() {
			return "Returns the chart panel this axis belongs to.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> IS_REVERSE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class, "isReverse",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isReverse();
		}

		@Override
		protected String getHelp() {
			return "Returns true if axis is reversed, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> IS_VERTICAL = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"isVertical", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isVertical();
		}

		@Override
		protected String getHelp() {
			return "Returns true if axis is vertical, false if horizontal.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> IS_ZOOMED = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class, "isZoomed",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.isZoomed();
		}

		@Override
		protected String getHelp() {
			return "Returns true is axis has been zoomed, false otherwise.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_ZOOM = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class, "getZoom",
			Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getZoom();
		}

		@Override
		protected String getHelp() {
			return "Returns a Double that is the zoom of the axis";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_VISIBLE_OFFSET = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"getVisibleOffset", Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getZoomLocation();
		}

		@Override
		protected String getHelp() {
			return "Returns the Double offset of the visible part of the axis in pixels";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_VISIBLE_LENGTH = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"getVisibleLength", Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getVisibleLength();
		}

		@Override
		protected String getHelp() {
			return "Returns a Double that is the visible length of the axis in pixels";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_VISIBLE_MIN_VALUE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"getVisibleMinValue", Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getVisibleMinValue();
		}

		@Override
		protected String getHelp() {
			return "Returns a Double that is the minimum visible value on the axis";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_VISIBLE_MAX_VALUE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"getVisibleMaxValue", Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getVisibleMaxValue();
		}

		@Override
		protected String getHelp() {
			return "Returns a Double that is the maximum visible value of the axis";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> SET_ZOOM = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class, "setZoom",
			Boolean.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Double zoom = Caster_Double.INSTANCE.cast(params[0]);
			if (zoom == null)
				return false;
			targetObject.setZoom(zoom);
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "zoom" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "new zoom" };
		}
		@Override
		protected String getHelp() {
			return "Sets the zoom of the axis to specified value.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> SET_VISIBLE_OFFSET = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"setVisibleOffset", Boolean.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Double zoomLoc = Caster_Double.INSTANCE.cast(params[0]);
			if (zoomLoc == null)
				return false;
			targetObject.setZoomLocation(zoomLoc);
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "offset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "new offset" };
		}
		@Override
		protected String getHelp() {
			return "Sets the visible offset the axis to the specified value.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> SET_ZOOM_BY_MIN_MAX_VALUE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(
			AmiWebChartAxisPortlet.class, "setZoomByMinMax", Boolean.class, Number.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Double min = Caster_Double.INSTANCE.cast(params[0]);
			Double max = Caster_Double.INSTANCE.cast(params[1]);
			if (min == null || max == null)
				return false;
			targetObject.setZoomByMinMax(min, max);
			return true;
		}

		protected String[] buildParamNames() {
			return new String[] { "min", "max" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "new min value", "new max value" };
		}
		@Override
		protected String getHelp() {
			return "Sets the zoom and the zoom location by inferring based on min and max value";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_OFFSET = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class, "getOffset",
			Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getOffset();
		}

		@Override
		protected String getHelp() {
			return "Returns the integer offset of the axis.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_PLOT_POSITIOn = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"getPlotPosition", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRowOrCol();
		}

		@Override
		protected String getHelp() {
			return "Returns the column position if the axis is horizontal, otherwise returns the row position.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_MIN_VALUE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"getMinValue", Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinValue();
		}

		@Override
		protected String getHelp() {
			return "Returns a Double that is the minimum value of the axis.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> GET_MAX_VALUE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"getMaxValue", Double.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMaxValue();
		}

		@Override
		protected String getHelp() {
			return "Returns a Double that is the maximum value of the axis.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> SET_MIN_VALUE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"setMinValue", Boolean.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Number new_min = (Number) (params[0]);
			try {
				targetObject.setAutoMinValueOverride(false);
				targetObject.setMinValueOverride(new_min);
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "min" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "new axis min value" };
		}

		@Override
		protected String getHelp() {
			return "Sets the min value of the axis to a specified value.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> SET_MAX_VALUE = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"setMaxValue", Boolean.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Number new_max = (Number) (params[0]);
			try {
				targetObject.setAutoMaxValueOverride(false);
				targetObject.setMaxValueOverride(new_max);
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "max" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "new axis max value" };
		}

		@Override
		protected String getHelp() {
			return "Sets the max value of the axis to a specified value.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> SET_MAJOR_TICKS_UNIT = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"setMajorTickUnit", Boolean.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Number new_major = (Number) (params[0]);
			try {
				targetObject.setAutoMajorValueOverride(false);
				targetObject.setMajorUnitOverride(new_major.doubleValue());
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "unit" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "major tick unit on the axis" };
		}

		@Override
		protected String getHelp() {
			return "Sets the major tick unit on the axis to a specified value.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static final AmiAbstractMemberMethod<AmiWebChartAxisPortlet> SET_MINOR_TICKS_UNIT = new AmiAbstractMemberMethod<AmiWebChartAxisPortlet>(AmiWebChartAxisPortlet.class,
			"setMinorTickUnit", Boolean.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartAxisPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Number new_minor = (Number) (params[0]);
			try {
				targetObject.setAutoMinorValueOverride(false);
				targetObject.setMinorUnitOverride(new_minor.doubleValue());
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		protected String[] buildParamNames() {
			return new String[] { "unit" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "minor tick unit on the axis" };
		}

		@Override
		protected String getHelp() {
			return "Sets the minor tick unit on the axis to a specified value.";
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

	};
	public static final AmiWebScriptMemberMethods_ChartPanelAxis INSTANCE = new AmiWebScriptMemberMethods_ChartPanelAxis();
}
