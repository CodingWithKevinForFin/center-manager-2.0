package com.f1.ami.web.amiscript;

import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebAbstractTablePortlet;
import com.f1.ami.web.charts.AmiWebChartAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartGridPortlet;
import com.f1.ami.web.charts.AmiWebChartPlotPortlet;
import com.f1.base.Bytes;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_ChartPanel extends AmiWebScriptBaseMemberMethods<AmiWebChartGridPortlet> {

	private AmiWebScriptMemberMethods_ChartPanel() {
		super();

		addMethod(TO_IMAGE_PNG_FOR_PLOT);
		addMethod(TO_IMAGE_PNG_FOR_PLOT_WITH_BG);
		addMethod(GET_ROWS_COUNT, "rowCount");
		addMethod(GET_COLUMNS_COUNT, "columnsCount");
		addMethod(GET_AXISES, "axises");
		addMethod(GET_PLOTS, "plots");
		addMethod(GET_PLOT);
		addMethod(GET_AXIS);
		addMethod(GET_AXIS_COUNT);
		addMethod(TO_IMAGE_PNG);
		registerCallbackDefinition(AmiWebAbstractTablePortlet.CALLBACK_DEF_ONSELECTED);
		registerCallbackDefinition(AmiWebChartGridPortlet.CALLBACK_DEF_ONZOOM);
	}

	@Override
	public String getVarTypeName() {
		return "ChartPanel";
	}
	@Override
	public String getVarTypeDescription() {
		return "A visualization Panel of type Chart";
	}
	@Override
	public Class<AmiWebChartGridPortlet> getVarType() {
		return AmiWebChartGridPortlet.class;
	}
	@Override
	public Class<AmiWebChartGridPortlet> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> TO_IMAGE_PNG_FOR_PLOT = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(AmiWebChartGridPortlet.class,
			"toImagePngForPlot", Bytes.class, false, Integer.class, Integer.class, Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer px = (Integer) params[0];
			Integer py = (Integer) params[1];
			Number w = (Number) params[2];
			Number h = (Number) params[3];
			if (px == null || py == null)
				return null;
			if (w == null)
				w = targetObject.getWidth();
			if (h == null)
				h = targetObject.getWidth();
			byte[] b = targetObject.toPng(px.intValue(), py.intValue(), w.intValue(), h.intValue(), false);
			return b == null ? null : new Bytes(b);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "plotX", "plotY", "widthPx", "heightPx" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x location of plot to get an image for(0=left most)", "y location of plot to get an image for(0=bottom most)", "width in px", "hieght in px" };
		}
		@Override
		protected String getHelp() {
			return "Converts a plot within this chart to an image with a clear white background.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> TO_IMAGE_PNG_FOR_PLOT_WITH_BG = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(
			AmiWebChartGridPortlet.class, "toImagePngForPlotWithBg", Bytes.class, false, Integer.class, Integer.class, Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer px = (Integer) params[0];
			Integer py = (Integer) params[1];
			Number w = (Number) params[2];
			Number h = (Number) params[3];
			if (px == null || py == null)
				return null;
			if (w == null)
				w = targetObject.getWidth();
			if (h == null)
				h = targetObject.getWidth();
			byte[] b = targetObject.toPng(px.intValue(), py.intValue(), w.intValue(), h.intValue(), true);
			return b == null ? null : new Bytes(b);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "plotX", "plotY", "widthPx", "heightPx" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x location of plot to get an image for(0=left most)", "y location of plot to get an image for(0=bottom most)", "width in px", "hieght in px" };
		}
		@Override
		protected String getHelp() {
			return "Converts a plot within this chart to an image with the bacgkround color as configured.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> GET_ROWS_COUNT = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(AmiWebChartGridPortlet.class,
			"getRowsCount", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRowsCount();
		}

		@Override
		protected String getHelp() {
			return "Returns the number of rows in this chart.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> GET_COLUMNS_COUNT = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(AmiWebChartGridPortlet.class,
			"getColumnsCount", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getColsCount();
		}

		@Override
		protected String getHelp() {
			return "Returns the number of columns in this chart."; //TODO
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> GET_AXISES = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(AmiWebChartGridPortlet.class, "getAxises",
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getAxises());
		}

		@Override
		protected String getHelp() {
			return "Returns a list of axes this panel holds.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> GET_PLOTS = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(AmiWebChartGridPortlet.class, "getPlots",
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			return CH.l(targetObject.getPlots());
		}

		@Override
		protected String getHelp() {
			return "Returns a list of plots this panel holds.";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> GET_PLOT = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(AmiWebChartGridPortlet.class, "getPlot",
			AmiWebChartPlotPortlet.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer x = (Integer) params[0];
			Integer y = (Integer) params[1];
			if (x == null || y == null || x < 0 || x >= targetObject.getColsCount() || y < 0 || y >= targetObject.getRowsCount())
				return null;
			return targetObject.getPlot(x, y);
		}

		@Override
		protected String getHelp() {
			return "Returns the plot specified by the x and y position.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "x", "y" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x position", "y position" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> GET_AXIS = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(AmiWebChartGridPortlet.class, "getAxis",
			AmiWebChartAxisPortlet.class, String.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String position = Caster_String.INSTANCE.cast(params[0]);
			int rowOrColumn = Caster_Integer.PRIMITIVE.cast(params[1]);
			int offset = Caster_Integer.PRIMITIVE.cast(params[2]);
			if ("LEFT".equalsIgnoreCase(position))
				return targetObject.getAxis(AmiWebChartGridPortlet.POS_L, rowOrColumn, offset);
			else if ("RIGHT".equalsIgnoreCase(position))
				return targetObject.getAxis(AmiWebChartGridPortlet.POS_R, rowOrColumn, offset);
			else if ("BOTTOM".equalsIgnoreCase(position))
				return targetObject.getAxis(AmiWebChartGridPortlet.POS_B, rowOrColumn, offset);
			else if ("TOP".equalsIgnoreCase(position))
				return targetObject.getAxis(AmiWebChartGridPortlet.POS_T, rowOrColumn, offset);
			else
				return null;
		}

		@Override
		protected String getHelp() {
			return "Returns the axis located in the given position.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "position", "rowOrColumn", "offset" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Expected Values: LEFT|TOP|BOTTOM|RIGHT", "usually 0, unless there are multiple plots.", "Usually 0, unless multiple axes side by side." }; //TODO: verify description for offset
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> GET_AXIS_COUNT = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(AmiWebChartGridPortlet.class,
			"getAxisCount", Integer.class, String.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			String position = Caster_String.INSTANCE.cast(params[0]);
			int rowOrColumn = Caster_Integer.PRIMITIVE.cast(params[1]);
			if ("LEFT".equalsIgnoreCase(position))
				return targetObject.getAxis(AmiWebChartGridPortlet.POS_L, rowOrColumn).size();
			else if ("RIGHT".equalsIgnoreCase(position))
				return targetObject.getAxis(AmiWebChartGridPortlet.POS_R, rowOrColumn).size();
			else if ("BOTTOM".equalsIgnoreCase(position))
				return targetObject.getAxis(AmiWebChartGridPortlet.POS_B, rowOrColumn).size();
			else if ("TOP".equalsIgnoreCase(position))
				return targetObject.getAxis(AmiWebChartGridPortlet.POS_T, rowOrColumn).size();
			else
				return null;
		}

		@Override
		protected String getHelp() {
			return "Returns the number of axes in a given position.";
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "position", "rowOrColumn" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Expected Values: LEFT|TOP|BOTTOM|RIGHT", "Usually 0, unless there are multiple plots." }; //TODO: better desc for rowOrColumn parameter
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebChartGridPortlet> TO_IMAGE_PNG = new AmiAbstractMemberMethod<AmiWebChartGridPortlet>(AmiWebChartGridPortlet.class,
			"toImagePng", Bytes.class, false, Number.class, Number.class, Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebChartGridPortlet targetObject, Object[] params, DerivedCellCalculator caller) {
			Number width = (Number) params[0];
			Number height = (Number) params[1];
			Boolean printBg = (Boolean) params[2];

			if (width == null)
				width = targetObject.getWidth();
			if (height == null)
				height = targetObject.getHeight();
			if (printBg == null)
				printBg = false;

			byte b[] = targetObject.toPngAxis(width.intValue(), height.intValue(), printBg.booleanValue());

			return b == null ? null : new Bytes(b);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "widthPx", "heightPx", "printBg" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "width of PNG image in px", "height of PNG image in px", "whether to print background color as seen or clear" };
		}

		@Override
		protected String getHelp() {
			return "Converts this chart into a PNG with an option to print the actual background color (printBg=true) or clear background (printBg=false)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiWebScriptMemberMethods_ChartPanel INSTANCE = new AmiWebScriptMemberMethods_ChartPanel();
}
