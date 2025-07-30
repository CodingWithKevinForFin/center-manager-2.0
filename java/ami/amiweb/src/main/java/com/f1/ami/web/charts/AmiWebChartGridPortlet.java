package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAbstractDmPortlet;
import com.f1.ami.web.AmiWebAbstractPortletBuilder;
import com.f1.ami.web.AmiWebDmPortletBuilder;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Chart;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.MultiDividerListener;
import com.f1.suite.web.portal.impl.MultiDividerPortlet;
import com.f1.suite.web.portal.impl.MultiDividerPortlet.Divider;
import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.TwoDimensionArray;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebChartGridPortlet extends AmiWebAbstractDmPortlet implements MultiDividerListener, ConfirmDialogListener {

	public static final String CALLBACK_NAME_ONZOOM = "onZoom";
	public static final ParamsDefinition CALLBACK_DEF_ONSELECTED = new ParamsDefinition("onSelected", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONZOOM = new ParamsDefinition(CALLBACK_NAME_ONZOOM, Object.class, "Integer r,Integer c");

	static {
		CALLBACK_DEF_ONSELECTED.addDesc("Called when the selection status of a point (or points) in the chart has changed");
		CALLBACK_DEF_ONZOOM.addDesc("Zoom");
		CALLBACK_DEF_ONZOOM.addParamDesc(0, "r");
		CALLBACK_DEF_ONZOOM.addParamDesc(1, "c");

	}
	public static final byte POS_L = 1;
	public static final byte POS_R = 2;
	public static final byte POS_T = 3;
	public static final byte POS_B = 4;
	public static final byte[] POSITIONS = new byte[] { POS_L, POS_R, POS_T, POS_B };
	public static final int DEFAULT_AXIS_SIZE = 70;
	public static final long DEFAULT_MAX_POINTS_VISIBLE_LIMIT = 100000;
	private static final Logger log = LH.get();
	final private MultiDividerPortlet rowsPortlet;
	private int rowsCount;
	private int colsCount;

	//ALL GO LEFT TO RIGHT, TOP TO BOTTOM, DEPENDING ON ORIENTATION
	final private TwoDimensionArray<AmiWebChartPlotPortlet> plots = new TwoDimensionArray<AmiWebChartPlotPortlet>(0, 0);
	final private List<List<AmiWebChartAxisPortlet>> axisL = new ArrayList<List<AmiWebChartAxisPortlet>>();
	final private List<List<AmiWebChartAxisPortlet>> axisR = new ArrayList<List<AmiWebChartAxisPortlet>>();
	final private List<List<AmiWebChartAxisPortlet>> axisT = new ArrayList<List<AmiWebChartAxisPortlet>>();
	final private List<List<AmiWebChartAxisPortlet>> axisB = new ArrayList<List<AmiWebChartAxisPortlet>>();
	final private ArrayList<AmiWebChartZoom> zoomX = new ArrayList<AmiWebChartZoom>();
	final private ArrayList<AmiWebChartZoom> zoomY = new ArrayList<AmiWebChartZoom>();

	private boolean hasL = false;
	private boolean hasR = false;
	private boolean hasT = false;
	private boolean hasB = false;

	private String backgroundColor;
	private String optionsSlidersColor;
	private String dividerColor;
	private int dividerThicknessH;
	private int dividerThicknessV;
	final private AmiWebService service;

	public int getRowsCount() {
		return rowsCount;
	}
	public int getColsCount() {
		return colsCount;
	}
	public int getLeftAxisCountAtRow(int row) {
		return axisL.get(row).size();
	}
	public int getRightAxisCountAtRow(int row) {
		return axisR.get(row).size();
	}
	public int getTopAxisCountAtCol(int col) {
		return axisT.get(col).size();
	}
	public int getBottomAxisCountAtCol(int col) {
		return axisB.get(col).size();
	}

	public AmiWebChartGridPortlet(PortletConfig config) {
		super(config);
		setAmiTitle("Chart", false);
		this.service = AmiWebUtils.getService(getManager());
		this.rowsCount = 0;
		this.colsCount = 0;
		this.rowsPortlet = newMultiDividerPortlet(false);
		this.rowsPortlet.addListener(this);
		setChild(this.rowsPortlet);
		this.getStylePeer().initStyle();
		//		this.maxPointsVisibleLimit = DEFAULT_MAX_POINTS_VISIBLE_LIMIT;
	}

	protected void initJs() {
		super.initJs();
	}

	@Override
	public void onDividerMovingStarted(MultiDividerPortlet dividerPortlet) {
	}

	@Override
	public void onDividerMoving(MultiDividerPortlet dividerPortlet) {
	}

	@Override
	public void onDividerMoved(MultiDividerPortlet dividerPortlet) {
		if (dividerPortlet != this.rowsPortlet) {
			for (Divider t : this.rowsPortlet.getChildrenInOrder()) {
				MultiDividerPortlet i = (MultiDividerPortlet) t.getPortlet();
				if (i != dividerPortlet)
					i.setOffsets(dividerPortlet);
			}
		}
	}

	@Override
	public boolean onAmiContextMenu(String id) {
		if (CH.size(this.getPlots()) == 1) {
			AmiWebChartPlotPortlet plot = getPlot(0, 0);
			if (plot.onAmiContextMenu(id))
				return true;
		}
		if (id.equals("edit_chart")) {
			AmiWebUtils.showStyleDialog("Chart Style", this);
			return true;
		} else if ("schema_warnings".equals(id)) {
			getManager().showDialog("Schema Warnings", new HtmlPortlet(generateConfig(), SH.join("<P>", getSchemaWarnings())), 600, 400);
			return true;
		} else {
			return super.onAmiContextMenu(id);
		}
	}

	int nextDatasourceId = 1;

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		return super.onButton(source, id);
	}
	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		List<String> warnings = getSchemaWarnings();
		int numWarnings = warnings.size();
		boolean hasWarnings = numWarnings > 0;
		if (hasWarnings) {
			headMenu.add(new BasicWebMenuDivider());
			headMenu.add(new BasicWebMenuLink("View " + numWarnings + " Schema Warning(s)", true, "schema_warnings").setCssStyle("className=ami_warning_menu"));
		}
		if (CH.size(this.getPlots()) == 1) {
			BasicWebMenu plotMenu = new BasicWebMenu("Plot", true);
			AmiWebChartPlotPortlet plot = getPlot(0, 0);
			if (!hasWarnings) {
				headMenu.add(new BasicWebMenuDivider());
			}
			plot.populateLowerConfigMenu(plotMenu);
			headMenu.add(plotMenu);
		}

	}
	private List<String> getSchemaWarnings() {
		List<String> output = new ArrayList<String>();
		AmiWebChartFormula f;
		StringBuilder sink = new StringBuilder();
		for (AmiWebChartPlotPortlet plot : getPlots()) {
			for (AmiWebChartRenderingLayer<?> layer : plot.getRenderyingLayers()) {
				AmiWebChartSeries series = layer.getSeries();
				if (series != null)
					for (int i = 0; i < series.getFormulasCount(); i++) {
						f = series.getFormulaAt(i);
						if (!f.testValue(sink)) {
							output.add(sink.toString());
							sink = new StringBuilder();
						}
					}
			}
		}
		return output;
	}
	@Override
	public String getConfigMenuTitle() {
		return "Chart";
	}

	@Override
	public boolean getIsFreeFloatingPortlet() {
		return true;
	}
	public AmiWebChartAxisPortlet addAxis(byte position, int rowOrCol, int offset) {
		AmiWebChartAxisPortlet r = newAmiWebChartAxisPortlet(position, rowOrCol, offset);
		List<AmiWebChartAxisPortlet> axis;
		if (position == POS_L) {
			if (!hasL) {
				if (offset != 0)
					throw new IndexOutOfBoundsException();
				for (Divider d : this.rowsPortlet.getChildrenInOrder()) {
					MultiDividerPortlet i = (MultiDividerPortlet) d.getPortlet();
					MultiDividerPortlet t = newMultiDividerPortlet(true);
					i.addChild(0, t, 1);
					t.addChild(newOptionsPortlet());
					i.setPreferedSize(0, DEFAULT_AXIS_SIZE);
				}
				hasL = true;
			}
			MultiDividerPortlet t2 = (MultiDividerPortlet) this.rowsPortlet.getAt(hasT ? rowOrCol + 1 : rowOrCol);
			MultiDividerPortlet t1 = (MultiDividerPortlet) t2.getAt(0);

			if (this.axisL.get(rowOrCol).size() == 0)
				t1.replaceChild(t1.getAt(0).getPortletId(), r);
			else
				t1.addChild(offset, r, 1);
			axis = this.axisL.get(rowOrCol);
			this.axisL.get(rowOrCol).add(offset, r);
		} else if (position == POS_R) {
			if (!hasR) {
				if (offset != 0)
					throw new IndexOutOfBoundsException();
				for (Divider e : this.rowsPortlet.getChildrenInOrder()) {
					MultiDividerPortlet i = (MultiDividerPortlet) e.getPortlet();
					MultiDividerPortlet t = newMultiDividerPortlet(true);
					i.addChild(t, 1);
					t.addChild(newOptionsPortlet());
					i.setPreferedSize(i.getChildrenCount() - 1, DEFAULT_AXIS_SIZE);
				}
				hasR = true;
			}
			MultiDividerPortlet t2 = (MultiDividerPortlet) this.rowsPortlet.getAt(hasT ? rowOrCol + 1 : rowOrCol);
			MultiDividerPortlet t1 = (MultiDividerPortlet) t2.getAt(t2.getChildrenCount() - 1);
			if (this.axisR.get(rowOrCol).size() == 0)
				t1.replaceChild(t1.getAt(0).getPortletId(), r);
			else
				t1.addChild(offset, r, 1);
			axis = this.axisR.get(rowOrCol);
			this.axisR.get(rowOrCol).add(offset, r);
		} else if (position == POS_T) {
			if (!hasT) {
				if (offset != 0)
					throw new IndexOutOfBoundsException();
				int totCols = (hasL && hasR ? 2 : (hasL || hasR ? 1 : 0)) + colsCount;
				MultiDividerPortlet t = newMultiDividerPortlet(true);
				t.addListener(this);
				for (int i = 0; i < totCols; i++) {
					MultiDividerPortlet t2 = newMultiDividerPortlet(false);
					t.addChild(t2);
					t2.addChild(newOptionsPortlet());
				}
				this.rowsPortlet.addChild(0, t, 1);
				t.setOffsets((MultiDividerPortlet) this.rowsPortlet.getAt(1));
				this.rowsPortlet.setPreferedSize(0, DEFAULT_AXIS_SIZE);
				hasT = true;
			}
			MultiDividerPortlet t2 = (MultiDividerPortlet) this.rowsPortlet.getAt(0);
			MultiDividerPortlet t1 = (MultiDividerPortlet) t2.getAt(hasL ? rowOrCol + 1 : rowOrCol);
			if (this.axisT.get(rowOrCol).size() == 0)
				t1.replaceChild(t1.getAt(0).getPortletId(), r);
			else
				t1.addChild(offset, r, 1);
			axis = this.axisT.get(rowOrCol);
			this.axisT.get(rowOrCol).add(offset, r);
		} else if (position == POS_B) {
			if (!hasB) {
				if (offset != 0)
					throw new IndexOutOfBoundsException();
				int totCols = (hasL && hasR ? 2 : (hasL || hasR ? 1 : 0)) + colsCount;
				MultiDividerPortlet t = newMultiDividerPortlet(true);
				t.addListener(this);
				for (int i = 0; i < totCols; i++) {
					MultiDividerPortlet t2 = newMultiDividerPortlet(false);
					t.addChild(t2);
					t2.addChild(newOptionsPortlet());
				}
				this.rowsPortlet.addChild(t, 1);
				t.setOffsets((MultiDividerPortlet) this.rowsPortlet.getAt(0));
				this.rowsPortlet.setPreferedSize(this.rowsPortlet.getChildrenCount() - 1, DEFAULT_AXIS_SIZE);
				hasB = true;
			}
			MultiDividerPortlet t2 = (MultiDividerPortlet) this.rowsPortlet.getAt(this.rowsPortlet.getChildrenCount() - 1);
			MultiDividerPortlet t1 = (MultiDividerPortlet) t2.getAt(hasL ? rowOrCol + 1 : rowOrCol);
			if (this.axisB.get(rowOrCol).size() == 0)
				t1.replaceChild(t1.getAt(0).getPortletId(), r);
			else
				t1.addChild(offset, r, 1);
			axis = this.axisB.get(rowOrCol);
			this.axisB.get(rowOrCol).add(offset, r);
		} else
			throw new RuntimeException("Bad position: " + position);
		for (int i = axis.size() - 1; i >= 0; i--)
			axis.get(i).setOffset(i);
		if (getVisible())
			layoutChildren();
		rebuildIdMappings();
		return r;
	}
	public List<List<AmiWebChartAxisPortlet>> getAxis(byte position) {
		switch (position) {
			case POS_L:
				return this.axisL;
			case POS_R:
				return this.axisR;
			case POS_T:
				return this.axisT;
			case POS_B:
				return this.axisB;
			default:
				throw new RuntimeException("bad position: " + position);
		}
	}
	public List<AmiWebChartAxisPortlet> getAxis(byte position, int rowOrColumn) {
		switch (position) {
			case POS_L:
				return this.axisL.get(rowOrColumn);
			case POS_R:
				return this.axisR.get(rowOrColumn);
			case POS_T:
				return this.axisT.get(rowOrColumn);
			case POS_B:
				return this.axisB.get(rowOrColumn);
			default:
				throw new RuntimeException("bad position: " + position);
		}
	}
	public void removeAxis(byte position, int rowOrCol, int offset) {
		List<List<AmiWebChartAxisPortlet>> axisList = getAxis(position);
		List<AmiWebChartAxisPortlet> list = axisList.get(rowOrCol);
		AmiWebChartAxisPortlet portlet = list.remove(offset);
		MultiDividerPortlet div = (MultiDividerPortlet) portlet.getParent();
		div.removeChild(portlet.getPortletId());
		portlet.close();
		for (int i = list.size() - 1; i >= offset; i--)
			list.get(i).setOffset(i);
		if (div.getChildrenCount() == 0) {
			div.addChild(newOptionsPortlet());
			cleanupEmptyAxis();
		}
		rebuildIdMappings();
	}
	private void cleanupEmptyAxis() {
		OUTER: for (byte position : POSITIONS) {
			List<List<AmiWebChartAxisPortlet>> axisList = getAxis(position);
			for (List<AmiWebChartAxisPortlet> t : axisList)
				if (!t.isEmpty())
					continue OUTER;
			if (hasL && position == POS_L) {
				for (Divider i : this.rowsPortlet.getChildrenInOrder())
					((MultiDividerPortlet) i.getPortlet()).removeAt(0);
				hasL = false;
			} else if (hasR && position == POS_R) {
				for (Divider i : this.rowsPortlet.getChildrenInOrder()) {
					MultiDividerPortlet t = (MultiDividerPortlet) i.getPortlet();
					((MultiDividerPortlet) t).removeAt(t.getChildrenCount() - 1);
				}
				hasR = false;
			} else if (hasT && position == POS_T) {
				this.rowsPortlet.removeAt(0);
				hasT = false;
			} else if (hasB && position == POS_B) {
				this.rowsPortlet.removeAt(this.rowsPortlet.getChildrenCount() - 1);
				hasB = false;
			}
		}
	}
	public AmiWebChartPlotPortlet addPlot(int x, int y) {
		if (plots.getNoThrow(x, y) != null)
			throw new RuntimeException("plot already exists at: " + x + "," + y);
		// x is columns/width, y is rows/height
		if (plots.getWidth() <= x)
			plots.setWidth(x + 1);
		if (plots.getHeight() <= y)
			plots.setHeight(y + 1);
		int totRows = (hasT && hasB ? 2 : (hasT || hasB ? 1 : 0)) + y;
		int totCols = (hasL && hasR ? 2 : (hasL || hasR ? 1 : 0)) + x;

		while (rowsCount <= totRows) {
			MultiDividerPortlet rowPortlet = newMultiDividerPortlet(true);
			rowPortlet.addListener(this);
			this.rowsPortlet.addChild(rowPortlet);
			this.rowsCount++;
			this.axisL.add(new ArrayList<AmiWebChartAxisPortlet>());
			this.axisR.add(new ArrayList<AmiWebChartAxisPortlet>());
			this.zoomY.add(new AmiWebChartZoom());
			for (int i = 0; i < colsCount; i++)
				rowPortlet.addChild(newOptionsPortlet());
		}
		while (colsCount <= totCols) {
			for (Divider i : this.rowsPortlet.getChildrenInOrder())
				((MultiDividerPortlet) i.getPortlet()).addChild(newOptionsPortlet());
			this.axisT.add(new ArrayList<AmiWebChartAxisPortlet>());
			this.axisB.add(new ArrayList<AmiWebChartAxisPortlet>());
			this.zoomX.add(new AmiWebChartZoom());
			colsCount++;

		}

		MultiDividerPortlet row = (MultiDividerPortlet) rowsPortlet.getAt(y);
		Portlet existing = row.getAt(x);
		AmiWebChartPlotPortlet plot = newAmiWebChartPlotPortlet(x, y);
		row.replaceChild(existing.getPortletId(), plot);
		// x is columns/width, y is rows/height
		plots.set(x, y, plot);
		//		plot.updateAri();
		plot.addToDomManager();
		// below method already updates ARI for every plot
		rebuildIdMappings();
		return plot;
	}

	private int nextId = 1;
	//	private AmiWebChartOptionsPortlet optionsPortlet;
	private List<AmiWebChartOptionsPortlet> optionsPortlets = new ArrayList<AmiWebChartOptionsPortlet>();

	private AmiWebChartAxisPortlet newAmiWebChartAxisPortlet(byte position, int rowOrCol, int offset) {
		AmiWebChartAxisPortlet r = new AmiWebChartAxisPortlet(this, getNextId(), generateConfig(), position, rowOrCol, offset);
		r.setBgColor(this.backgroundColor);
		r.updateAri();
		r.addToDomManager();
		getManager().onPortletAdded(r);
		return r;
	}
	private AmiWebChartPlotPortlet newAmiWebChartPlotPortlet(int col, int row) {
		AmiWebChartPlotPortlet r = new AmiWebChartPlotPortlet(this, getNextId(), generateConfig(), col, row);
		r.setBgColor(this.backgroundColor);
		r.setSelectionBoxBorderColor(this.selectionBoxBorderColor);
		r.setSelectionBoxColor(this.selectionBoxColor);
		r.setSelectionColor(this.selectionColor);
		getManager().onPortletAdded(r);
		return r;
	}
	private MultiDividerPortlet newMultiDividerPortlet(boolean isVertical) {
		MultiDividerPortlet r = new MultiDividerPortlet(generateConfig(), isVertical);
		r.setThickness(isVertical ? this.dividerThicknessV : this.dividerThicknessH);
		r.setColor(this.dividerColor);
		r.setMinSize(0);
		getManager().onPortletAdded(r);
		return r;
	}

	private AmiWebChartOptionsPortlet newOptionsPortlet() {
		AmiWebChartOptionsPortlet r = new AmiWebChartOptionsPortlet(this, generateConfig());
		getManager().onPortletAdded(r);
		this.optionsPortlets.add(r);
		return r;
	}
	public void removePlotsRow(int row) {
		if (this.rowsCount <= 1)
			throw new IllegalStateException("must have at least one row");
		int pos = this.hasT ? row + 1 : row;
		//Remove from dom first
		for (int y = row; y < plots.getHeight(); y++) {
			for (int x = 0; x < plots.getWidth(); x++) {
				AmiWebChartPlotPortlet plot = plots.getAt(x, y);
				plot.removeFromDomManager();
			}
			for (AmiWebChartAxisPortlet t : this.axisL.get(y))
				t.removeFromDomManager();
			for (AmiWebChartAxisPortlet t : this.axisR.get(y))
				t.removeFromDomManager();
		}
		this.rowsPortlet.removeAt(pos);
		this.rowsCount--;
		plots.removeRow(row);
		this.axisL.remove(row);
		this.axisR.remove(row);
		this.zoomY.remove(row);
		for (int y = row; y < plots.getHeight(); y++) {
			for (int x = 0; x < plots.getWidth(); x++) {
				AmiWebChartPlotPortlet plot = plots.getAt(x, y);
				plot.setRow(y);
			}
			for (AmiWebChartAxisPortlet t : this.axisL.get(y))
				t.setRowOrCol(y);
			for (AmiWebChartAxisPortlet t : this.axisR.get(y))
				t.setRowOrCol(y);
		}
		cleanupEmptyAxis();
		rebuildIdMappings();
		if (getVisible())
			layoutChildren();
	}
	public void removePlotsColumn(int col) {
		if (this.colsCount <= 1)
			throw new IllegalStateException("must have atleast one col");
		int pos = this.hasL ? col + 1 : col;
		//Remove from dom first
		for (int x = col; x < plots.getWidth(); x++) {
			for (int y = 0; y < plots.getHeight(); y++) {
				AmiWebChartPlotPortlet plot = plots.getAt(x, y);
				plot.removeFromDomManager();
			}
			for (AmiWebChartAxisPortlet t : this.axisT.get(x))
				t.removeFromDomManager();
			for (AmiWebChartAxisPortlet t : this.axisB.get(x))
				t.removeFromDomManager();
		}
		for (Divider r : this.rowsPortlet.getChildrenInOrder()) {
			((MultiDividerPortlet) r.getPortlet()).removeAt(pos);
		}
		this.colsCount--;
		plots.removeCol(col);
		this.axisT.remove(col);
		this.axisB.remove(col);
		this.zoomX.remove(col);
		for (int x = col; x < plots.getWidth(); x++) {
			for (int y = 0; y < plots.getHeight(); y++) {
				AmiWebChartPlotPortlet plot = plots.getAt(x, y);
				plot.setCol(x);
			}
			for (AmiWebChartAxisPortlet t : this.axisT.get(x))
				t.setRowOrCol(x);
			for (AmiWebChartAxisPortlet t : this.axisB.get(x))
				t.setRowOrCol(x);
		}
		cleanupEmptyAxis();
		rebuildIdMappings();
		if (getVisible())
			layoutChildren();
	}
	public void addPlotsRow(int row) {
		this.zoomY.add(row, new AmiWebChartZoom());
		MultiDividerPortlet newRow = newMultiDividerPortlet(true);
		List<AmiWebChartAxisPortlet> l = new ArrayList<AmiWebChartAxisPortlet>();
		this.axisL.add(row, l);
		if (hasL) {
			MultiDividerPortlet t = newMultiDividerPortlet(true);
			AmiWebChartAxisPortlet div = newAmiWebChartAxisPortlet(POS_L, row, 0);
			l.add(div);
			t.addChild(div);
			newRow.addChild(t);
		}
		this.rowsCount++;
		this.plots.insertRow(row);
		for (int x = 0; x < this.colsCount; x++) {
			AmiWebChartPlotPortlet plot = newAmiWebChartPlotPortlet(x, row);
			newRow.addChild(plot);
			// x is column/width, y is row/height
			this.plots.set(x, row, plot);
			plot.updateAri();
			plot.addToDomManager();
			for (int y = row + 1; y < this.rowsCount; y++) {
				this.plots.getAt(x, y).setRow(y);
			}
		}
		l = new ArrayList<AmiWebChartAxisPortlet>();
		this.axisR.add(row, l);
		if (hasR) {
			MultiDividerPortlet t = newMultiDividerPortlet(true);
			AmiWebChartAxisPortlet div = newAmiWebChartAxisPortlet(POS_R, row, 0);
			l.add(div);
			t.addChild(div);
			newRow.addChild(t);
		}
		newRow.addListener(this);
		int pos = hasT ? row + 1 : row;
		this.rowsPortlet.addChild(pos, newRow, 1);
		newRow.setOffsets((MultiDividerPortlet) this.rowsPortlet.getAt(pos == 0 ? 1 : 0));
		for (int y = row + 1; y < this.rowsCount; y++) {
			if (hasL)
				for (AmiWebChartAxisPortlet i : this.axisL.get(y))
					i.setRowOrCol(y);
			if (hasR)
				for (AmiWebChartAxisPortlet i : this.axisR.get(y))
					i.setRowOrCol(y);
		}
		rebuildIdMappings();
		layoutChildren();
	}
	public void addPlotsCol(int col) {
		this.zoomX.add(col, new AmiWebChartZoom());
		int pos = hasL ? col + 1 : col;
		List<AmiWebChartAxisPortlet> l = new ArrayList<AmiWebChartAxisPortlet>();
		this.axisT.add(col, l);
		if (hasT) {
			MultiDividerPortlet t = newMultiDividerPortlet(false);
			AmiWebChartAxisPortlet div = newAmiWebChartAxisPortlet(POS_T, col, 0);
			l.add(div);
			t.addChild(div);
			MultiDividerPortlet rowPortlet = (MultiDividerPortlet) this.rowsPortlet.getAt(0);
			rowPortlet.addChild(pos, t, 1);
		}

		this.colsCount++;
		this.plots.insertCol(col);
		for (int y = 0; y < this.rowsCount; y++) {
			AmiWebChartPlotPortlet plot = newAmiWebChartPlotPortlet(col, y);
			MultiDividerPortlet rowPortlet = (MultiDividerPortlet) this.rowsPortlet.getAt(hasT ? y + 1 : y);
			rowPortlet.addChild(pos, plot, 1);
			this.plots.set(col, y, plot);
			plot.updateAri();
			plot.addToDomManager();
			for (int x = col + 1; x < this.colsCount; x++)
				this.plots.getAt(x, y).setCol(x);
		}

		l = new ArrayList<AmiWebChartAxisPortlet>();
		this.axisB.add(col, l);
		if (hasB) {
			MultiDividerPortlet t = newMultiDividerPortlet(false);
			AmiWebChartAxisPortlet div = newAmiWebChartAxisPortlet(POS_B, col, 0);
			l.add(div);
			t.addChild(div);
			MultiDividerPortlet rowPortlet = (MultiDividerPortlet) this.rowsPortlet.getAt(this.rowsPortlet.getChildrenCount() - 1);
			rowPortlet.addChild(pos, t, 1);
		}
		for (int x = col + 1; x < this.colsCount; x++) {
			if (hasT)
				for (AmiWebChartAxisPortlet i : this.axisT.get(x))
					i.setRowOrCol(x);
			if (hasB)
				for (AmiWebChartAxisPortlet i : this.axisB.get(x))
					i.setRowOrCol(x);
		}
		rebuildIdMappings();
		layoutChildren();
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("rowsCount", this.rowsCount);
		r.put("colsCount", this.colsCount);
		//		r.put("mxPtsVLmt", this.getMaxPointsVisibleLimit());
		List<Integer> axisLcnts = new ArrayList<Integer>(this.rowsCount);
		List<Integer> axisRcnts = new ArrayList<Integer>(this.rowsCount);
		List<Integer> axisTcnts = new ArrayList<Integer>(this.colsCount);
		List<Integer> axisBcnts = new ArrayList<Integer>(this.colsCount);
		for (int y = 0; y < this.rowsCount; y++) {
			axisLcnts.add(this.axisL.get(y).size());
			axisRcnts.add(this.axisR.get(y).size());
		}
		for (int x = 0; x < this.colsCount; x++) {
			axisTcnts.add(this.axisT.get(x).size());
			axisBcnts.add(this.axisB.get(x).size());
		}
		r.put("axisLcnts", axisLcnts);
		r.put("axisRcnts", axisRcnts);
		r.put("axisTcnts", axisTcnts);
		r.put("axisBcnts", axisBcnts);
		Map<String, Object> axisConfig = new HashMap<String, Object>();
		Map<String, Object> plotConfig = new HashMap<String, Object>();
		for (int pos = 0; pos < this.colsCount; pos++) {
			List<AmiWebChartAxisPortlet> t = this.axisT.get(pos);
			for (int offset = 0; offset < t.size(); offset++)
				axisConfig.put("T_" + pos + "_" + offset, t.get(offset).getConfiguration());
		}
		for (int pos = 0; pos < this.colsCount; pos++) {
			List<AmiWebChartAxisPortlet> t = this.axisB.get(pos);
			for (int offset = 0; offset < t.size(); offset++)
				axisConfig.put("B_" + pos + "_" + offset, t.get(offset).getConfiguration());
		}
		for (int pos = 0; pos < this.rowsCount; pos++) {
			List<AmiWebChartAxisPortlet> t = this.axisL.get(pos);
			for (int offset = 0; offset < t.size(); offset++)
				axisConfig.put("L_" + pos + "_" + offset, t.get(offset).getConfiguration());
		}
		for (int pos = 0; pos < this.rowsCount; pos++) {
			List<AmiWebChartAxisPortlet> t = this.axisR.get(pos);
			for (int offset = 0; offset < t.size(); offset++)
				axisConfig.put("R_" + pos + "_" + offset, t.get(offset).getConfiguration());
		}
		for (int y = 0; y < this.plots.getHeight(); y++) {
			for (int x = 0; x < this.plots.getWidth(); x++) {
				// x is column/width, y is row/height
				// column first, then row
				AmiWebChartPlotPortlet acpp = plots.getAt(x, y);
				plotConfig.put(SH.toString(x) + "_" + SH.toString(y), acpp.getConfiguration());
			}
		}
		r.put("axisConfig", axisConfig);
		r.put("plotConfig", plotConfig);

		getMultiDividerConfiguration(r, this.rowsPortlet);
		return r;
	}

	private void getMultiDividerConfiguration(Map<String, Object> sink, MultiDividerPortlet divider) {
		List<Map<String, Object>> children = new ArrayList<Map<String, Object>>(divider.getChildrenCount());
		for (Divider i : divider.getChildrenInOrder()) {
			Map<String, Object> m = CH.m("p", i.getPreferedSizePx(), "w", i.getWeight());
			children.add(m);
			if (i.getPortlet() instanceof MultiDividerPortlet)
				getMultiDividerConfiguration(m, (MultiDividerPortlet) i.getPortlet());
		}
		sink.put("mdivs", children);
	}

	private void initMultiDividerConfiguration(Map<String, Object> configuration, MultiDividerPortlet div, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		List<Map<String, Object>> children = (List<Map<String, Object>>) configuration.get("mdivs");
		if (children == null)
			return;
		int position = 0;
		for (Map<String, Object> i : children) {
			int preferedSize = CH.getOrThrow(Caster_Integer.PRIMITIVE, i, "p");
			double weight = CH.getOrThrow(Caster_Double.PRIMITIVE, i, "w");
			if (preferedSize != -1)
				div.setPreferedSizeNoFire(position, preferedSize);
			div.setWeight(position, weight);
			if (div.getPortletAt(position).getPortlet() instanceof MultiDividerPortlet)
				initMultiDividerConfiguration(i, (MultiDividerPortlet) div.getPortletAt(position).getPortlet(), origToNewIdMapping, sb);
			position++;
		}
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		int colsCount = CH.getOrThrow(Caster_Integer.INSTANCE, configuration, "colsCount");
		int rowsCount = CH.getOrThrow(Caster_Integer.INSTANCE, configuration, "rowsCount");
		//		this.setMaxPointsVisibleLimit(CH.getOr(Caster_Long.INSTANCE, configuration, "mxPtsVLmt", this.DEFAULT_MAX_POINTS_VISIBLE_LIMIT));
		List<Integer> axisLcnts = (List<Integer>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "axisLcnts");
		List<Integer> axisRcnts = (List<Integer>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "axisRcnts");
		List<Integer> axisTcnts = (List<Integer>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "axisTcnts");
		List<Integer> axisBcnts = (List<Integer>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "axisBcnts");
		Map<String, Object> axisConfig = (Map<String, Object>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "axisConfig");
		Map<String, Object> plotConfig = (Map<String, Object>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "plotConfig");
		for (int y = 0; y < rowsCount; y++) {
			for (int x = 0; x < colsCount; x++) {
				// x is columns/width, y is rows/height
				// add cols then rows (conform to 2d array iteration order)
				// see TwoDimensionArray::getXIndex(...) for an example ^^
				addPlot(x, y);
			}
		}

		for (int x = 0; x < colsCount; x++) {
			for (int y = 0; y < axisTcnts.get(x); y++)
				addAxis(AmiWebChartGridPortlet.POS_T, x, y);
			for (int y = 0; y < axisBcnts.get(x); y++)
				addAxis(AmiWebChartGridPortlet.POS_B, x, y);
		}
		for (int y = 0; y < rowsCount; y++) {
			for (int x = 0; x < axisLcnts.get(y); x++)
				addAxis(AmiWebChartGridPortlet.POS_L, y, x);
			for (int x = 0; x < axisRcnts.get(y); x++)
				addAxis(AmiWebChartGridPortlet.POS_R, y, x);
		}
		for (Entry<String, Object> e : axisConfig.entrySet()) {
			String[] parts = SH.split('_', e.getKey());
			char loc = SH.parseChar(parts[0]);
			byte posT;
			switch (loc) {
				case 'T':
					posT = POS_T;
					break;
				case 'B':
					posT = POS_B;
					break;
				case 'L':
					posT = POS_L;
					break;
				case 'R':
					posT = POS_R;
					break;
				default:
					continue;
			}
			int pos = SH.parseInt(parts[1]);
			int offset = SH.parseInt(parts[2]);
			getAxis(posT, pos, offset).init((Map<String, Object>) e.getValue(), origToNewIdMapping, sb);
		}
		for (Entry<String, Object> e : plotConfig.entrySet()) {
			String[] parts = SH.split('_', e.getKey());
			int x = SH.parseInt(parts[0]);
			int y = SH.parseInt(parts[1]);
			getPlot(x, y).init((Map<String, Object>) e.getValue(), origToNewIdMapping, sb);
		}
		initMultiDividerConfiguration(configuration, this.rowsPortlet, origToNewIdMapping, sb);
		rebuildIdMappings();

		removeUnusedDmAndDmTables();
		super.init(configuration, origToNewIdMapping, sb);

	}
	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
		// onAmiInitDone Axis
		Iterable<AmiWebChartAxisPortlet> axesIter = getAllAxis();
		for (AmiWebChartAxisPortlet axis : axesIter) {
			axis.onAmiInitDone();
		}
		Iterable<AmiWebChartPlotPortlet> allplots = getAllPlots();
		for (AmiWebChartPlotPortlet plot : allplots) {
			plot.onAmiInitDone();
		}
	}
	private void removeUnusedDmAndDmTables() {
		Set<Tuple2<String, String>> usedDmAndDmTables = new HashSet<Tuple2<String, String>>();

		// Add all used Dm and DmTables to the set usedDmAndDmTables
		for (String dmName : this.getUsedDmAliasDotNames()) {
			for (String dmTableName : this.getUsedDmTables(dmName)) {
				usedDmAndDmTables.add(new Tuple2<String, String>(dmName, dmTableName));
			}
		}

		// Remove all used Dm and DmTables from the set usedDmAndDmTables
		for (AmiWebChartPlotPortlet i : this.getPlots()) {
			for (AmiWebChartRenderingLayer j : i.getRenderyingLayers()) {
				Tuple2<String, String> key = new Tuple2<String, String>(j.getDmAliasDotName(), j.getDmTableName());
				if (usedDmAndDmTables.contains(key)) {
					usedDmAndDmTables.remove(key);
				}
			}
		}

		// Prevailing elements in the set usedDmAndDmTables are unused and are removed
		for (Tuple2<String, String> k : usedDmAndDmTables) {
			this.removeUsedDm(k.getA(), k.getB());
		}

	}
	private void rebuildIdMappings() {
		this.plotPortletsById.clear();
		this.axisPortletsById.clear();
		for (List<AmiWebChartAxisPortlet> i : this.axisT)
			for (AmiWebChartAxisPortlet p : i)
				this.axisPortletsById.put(p.getAxisId(), p);
		for (List<AmiWebChartAxisPortlet> i : this.axisB)
			for (AmiWebChartAxisPortlet p : i)
				this.axisPortletsById.put(p.getAxisId(), p);
		for (List<AmiWebChartAxisPortlet> i : this.axisL)
			for (AmiWebChartAxisPortlet p : i)
				this.axisPortletsById.put(p.getAxisId(), p);
		for (List<AmiWebChartAxisPortlet> i : this.axisR)
			for (AmiWebChartAxisPortlet p : i)
				this.axisPortletsById.put(p.getAxisId(), p);

		for (int x = 0; x < this.plots.getWidth(); x++) {
			for (int y = 0; y < this.plots.getHeight(); y++) {
				AmiWebChartPlotPortlet plot = this.plots.getAt(x, y);
				if (plot != null) {
					this.plotPortletsById.put(plot.getPlotId(), plot);
				}
			}
		}
		updateAri();
		flagConfigStale();
	}
	public void flagConfigStale() {
		for (List<AmiWebChartAxisPortlet> i : this.axisT)
			for (AmiWebChartAxisPortlet p : i)
				p.onDataChanged();
		for (List<AmiWebChartAxisPortlet> i : this.axisB)
			for (AmiWebChartAxisPortlet p : i)
				p.onDataChanged();
		for (List<AmiWebChartAxisPortlet> i : this.axisL)
			for (AmiWebChartAxisPortlet p : i)
				p.onDataChanged();
		for (List<AmiWebChartAxisPortlet> i : this.axisR)
			for (AmiWebChartAxisPortlet p : i)
				p.onDataChanged();
		for (int x = 0; x < this.plots.getWidth(); x++) {
			for (int y = 0; y < this.plots.getHeight(); y++) {
				AmiWebChartPlotPortlet plot = this.plots.getAt(x, y);
				if (plot != null) {
					plot.flagConfigStale();
				}
			}
		}
	}

	private IntKeyMap<AmiWebChartPlotPortlet> plotPortletsById = new IntKeyMap<AmiWebChartPlotPortlet>();
	private IntKeyMap<AmiWebChartAxisPortlet> axisPortletsById = new IntKeyMap<AmiWebChartAxisPortlet>();
	private String descFontFamily;
	private String descFontStyle;
	private List<String> colorSeries;
	private List<Color> colorSeriesColors;
	private ColorGradient colorGradient;
	private String selectionBoxBorderColor;
	private String selectionBoxColor;
	private String selectionColor;

	public AmiWebChartPlotPortlet getPlot(int x, int y) {
		return this.plots.getAt(x, y);
	}
	public AmiWebChartPlotPortlet getPlotNoThrow(int x, int y) {
		if (x >= 0 && x < this.plots.getWidth() && y >= 0 && y < this.plots.getHeight())
			return this.plots.getAt(x, y);
		else
			return null;
	}
	public AmiWebChartAxisPortlet getAxis(byte pos, int colOrRow, int offset) {
		switch (pos) {
			case POS_T:
				return this.axisT.get(colOrRow).get(offset);
			case POS_B:
				return this.axisB.get(colOrRow).get(offset);
			case POS_L:
				return this.axisL.get(colOrRow).get(offset);
			case POS_R:
				return this.axisR.get(colOrRow).get(offset);
			default:
				throw new RuntimeException("bad position: " + pos);
		}
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebChartGridPortlet> implements AmiWebDmPortletBuilder<AmiWebChartGridPortlet> {

		public static final String OLD_ID = "amichartgride";
		public static final String ID = "amichartgrid";
		public static final String TYPE_CHART = "chart";

		public Builder() {
			super(AmiWebChartGridPortlet.class);
		}

		@Override
		public AmiWebChartGridPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebChartGridPortlet r = new AmiWebChartGridPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Chart";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}
		@Override
		public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig) {
			Map<String, Object> plotConfig = (Map<String, Object>) CH.getOrThrow(Caster_Simple.OBJECT, portletConfig, "plotConfig");
			List<Map<String, Object>> dmConfig = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, portletConfig, "dm");
			List<String> r = new ArrayList<String>();
			for (Map<String, Object> i : dmConfig)
				r.add((String) i.get("dmadn"));

			for (Entry<String, Object> e : plotConfig.entrySet()) {
				Map<String, Object> pc = (Map<String, Object>) e.getValue();
				List<Map<String, Object>> layers = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, pc, "layers");
				for (Map<String, Object> layer : layers)
					r.add((String) layer.get("dmadn"));
			}
			return r;
		}
		@Override
		public void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name) {
			Map<String, Object> plotConfig = (Map<String, Object>) CH.getOrThrow(Caster_Simple.OBJECT, portletConfig, "plotConfig");
			List<Map<String, Object>> dmConfig = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, portletConfig, "dm");
			List<String> r = new ArrayList<String>();
			int pos = 0;
			for (Map<String, Object> i : dmConfig) {
				if (pos == position) {
					i.put("dmadn", name);
					return;
				} else
					pos++;
			}
			for (Entry<String, Object> e : plotConfig.entrySet()) {
				Map<String, Object> pc = (Map<String, Object>) e.getValue();
				List<Map<String, Object>> layers = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, pc, "layers");
				for (Map<String, Object> layer : layers) {
					if (pos == position) {
						layer.put("dmadn", name);
						return;
					} else
						pos++;
				}
			}
			throw new IndexOutOfBoundsException(position + " >= " + pos);
		}

	}

	@Override
	public void clearAmiData() {
	}

	@Override
	public boolean isRealtime() {
		return false;
	}

	private Iterable<AmiWebChartAxisPortlet> getAllAxis() {
		return this.axisPortletsById.values();
	}

	private Iterable<AmiWebChartPlotPortlet> getAllPlots() {
		return this.plotPortletsById.values();
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setSelectionColor(String s) {
		this.selectionColor = s;
		for (AmiWebChartPlotPortlet i : getAllPlots()) {
			i.setSelectionColor(s);
		}
	}
	public void setSelectionBoxColor(String s) {
		this.selectionBoxColor = s;
		for (AmiWebChartPlotPortlet i : getAllPlots()) {
			i.setSelectionBoxColor(s);
		}
	}
	public void setSelectionBoxBorderColor(String s) {
		this.selectionBoxBorderColor = s;
		for (AmiWebChartPlotPortlet i : getAllPlots()) {
			i.setSelectionBoxBorderColor(s);
		}
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
		for (AmiWebChartAxisPortlet i : getAllAxis())
			i.setBgColor(backgroundColor);
		for (AmiWebChartPlotPortlet i : getAllPlots()) {
			i.setBgColor(backgroundColor);
		}
		for (AmiWebChartOptionsPortlet op : optionsPortlets) {
			op.setCssStyle("style.background-color=" + backgroundColor);
		}

	}

	public String getOptionsSlidersColor() {
		return optionsSlidersColor;
	}
	public void setOptionsSlidersColor(String optionsSlidersColor) {
		this.optionsSlidersColor = optionsSlidersColor;
	}

	public int getNextId() {
		return this.nextId++;
	}
	public AmiWebChartAxisPortlet getAxisById(int axisId) {
		return this.axisPortletsById.get(axisId);
	}
	public AmiWebChartPlotPortlet getPlotById(int plotId) {
		return this.plotPortletsById.get(plotId);
	}

	public Iterable<AmiWebChartPlotPortlet> getPlots() {
		return this.plotPortletsById.values();
	}
	public Iterable<AmiWebChartAxisPortlet> getAxises() {
		return this.axisPortletsById.values();
	}

	public AmiWebChartSeries getSeries(int i) {
		for (AmiWebChartPlotPortlet plot : this.plotPortletsById.values()) {
			AmiWebChartSeries r = plot.getSeries(i);
			if (r != null)
				return r;
		}
		return null;
	}
	@Override
	public String getPanelType() {
		return "chart";
	}

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		super.onDmDataChanged(datamodel);
		for (AmiWebChartPlotPortlet i : this.plotPortletsById.values())
			i.processDataModel(datamodel, getStackFrame());
	}
	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
		if (!isRequery && this.isClearOnDataStale())
			for (AmiWebChartPlotPortlet i : this.plotPortletsById.values())
				i.processDataModelRunningQuery(datamodel, isRequery);
		super.onDmRunningQuery(datamodel, isRequery);
	}
	@Override
	public void clearUserSelection() {
		for (AmiWebChartPlotPortlet p : this.plotPortletsById.values())
			p.clearSelected();
	}

	@Override
	public void onLinkingChanged(AmiWebDmLink link) {
		if (OH.eq(getAmiLayoutFullAliasDotId(), link.getSourcePanelAliasDotId())) {
			for (AmiWebChartPlotPortlet i : this.plotPortletsById.values())
				for (AmiWebChartRenderingLayer rl : i.getRenderyingLayers())
					if (OH.eq(link.getSourceDmAliasDotName(), rl.getDmAliasDotName()) && OH.eq(link.getSourceDmTableName(), rl.getDmTableName()))
						rl.onLinkingChanged(link);
		}
		if (OH.eq(getAmiLayoutFullAliasDotId(), link.getTargetPanelAliasDotId())) {
			for (AmiWebChartPlotPortlet i : this.plotPortletsById.values())
				for (AmiWebChartRenderingLayer rl : i.getRenderyingLayers())
					if (OH.eq(link.getTargetDmAliasDotName(), rl.getDmAliasDotName()))
						rl.onLinkingChanged(link);
		}
		super.onLinkingChanged(link);
	}
	@Override
	protected boolean isDatamodelHaveDependencies(String dmName, String tbName) {
		for (AmiWebChartPlotPortlet i : this.plotPortletsById.values())
			for (AmiWebChartRenderingLayer rl : i.getRenderyingLayers())
				if (OH.eq(dmName, rl.getDmAliasDotName()) && OH.eq(tbName, rl.getDmTableName()))
					return true;
		return false;
	}
	public void setDividerColor(String value) {
		this.dividerColor = value;
		for (DividerPortlet i : PortletHelper.findPortletsByType(this, DividerPortlet.class))
			i.setColor(this.dividerColor);
		for (MultiDividerPortlet i : PortletHelper.findPortletsByType(this, MultiDividerPortlet.class))
			i.setColor(this.dividerColor);
	}
	public String getDividerColor() {
		return this.dividerColor;
	}
	public void setDividerThicknessH(int thickness) {
		setDividerThickness(thickness, false);
	}
	public void setDividerThicknessV(int thickness) {
		setDividerThickness(thickness, true);
	}
	private void setDividerThickness(int thickness, boolean vertical) {
		if ((vertical && thickness == this.dividerThicknessV) || (!vertical && thickness == this.dividerThicknessH)) {
			return;
		}
		if (vertical) {
			this.dividerThicknessV = thickness;
		} else {
			this.dividerThicknessH = thickness;
		}
		for (DividerPortlet i : PortletHelper.findPortletsByType(this, DividerPortlet.class)) {
			if (vertical == i.isVertical()) {
				i.setThickness(thickness);
			}
		}
		for (MultiDividerPortlet i : PortletHelper.findPortletsByType(this, MultiDividerPortlet.class)) {
			if (vertical == i.isVertical()) {
				i.setThickness(thickness);
			}
		}
	}
	public int getDividerThicknessH() {
		return this.dividerThicknessH;
	}
	public int getDividerThicknessV() {
		return this.dividerThicknessV;
	}
	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return getVisible();
	}
	@Override
	public void getUsedColors(Set<String> sink) {
		for (AmiWebChartPlotPortlet i : this.getAllPlots())
			i.getUsedColors(sink);
	}

	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Chart.TYPE_CHART;
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		super.onStyleValueChanged(key, old, nuw);

		CalcFrameStack sf = getStackFrame();
		switch (key) {
			case AmiWebStyleConsts.CODE_BG_CL:
				setBackgroundColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DIV_CL:
				setDividerColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SCR_CL:
				setOptionsSlidersColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DIV_THCK_H:
				if (nuw != null)
					setDividerThicknessH(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_DIV_THCK_V:
				if (nuw != null)
					setDividerThicknessV(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_SERIES_CLS:
				this.colorSeries = (List<String>) nuw;
				this.colorSeriesColors = ColorHelper.parseColorsNoThrow(this.colorSeries);
				if (nuw != null)
					for (AmiWebChartPlotPortlet i : this.plotPortletsById.values())
						for (AmiWebChartRenderingLayer n : i.getRenderyingLayers()) {
							if (n.getDataModelSchema() != null)
								n.buildData(n.getDataModelSchema(), sf);
						}
				break;
			case AmiWebStyleConsts.CODE_SEL_CL:
				setSelectionColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_BOX_BDR_CL:
				setSelectionBoxBorderColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_BOX_CL:
				setSelectionBoxColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_FONT_FAM:
				this.setDescFontFamily((String) nuw);
				for (Node<AmiWebChartPlotPortlet> i : this.plotPortletsById)
					i.getValue().flagConfigStale();
				break;
			case AmiWebStyleConsts.CODE_FONT_STYLE:
				this.setDescFontStyle((String) nuw);
				for (Node<AmiWebChartPlotPortlet> i : this.plotPortletsById)
					i.getValue().flagConfigStale();
				break;
			case AmiWebStyleConsts.CODE_GRADIENT:
				this.colorGradient = (ColorGradient) nuw;
				if (nuw != null)
					for (AmiWebChartPlotPortlet i : this.plotPortletsById.values())
						for (AmiWebChartRenderingLayer n : i.getRenderyingLayers()) {
							if (n.getDataModelSchema() != null)
								n.buildData(n.getDataModelSchema(), sf);
						}
				break;
		}
	}
	private void setDescFontFamily(String nuw) {
		this.descFontFamily = nuw;
	}
	public void setDescFontStyle(String nuw) {
		this.descFontStyle = nuw;
	}
	public void onUsedDmRemoved(String oldDmAliasDotName, String oldDmTableName, AmiWebChartRenderingLayer layer) {
		boolean remove = oldDmAliasDotName != null;

		// If another layer has the same dm and table, do not remove the datamodel from the container of used datamodels
		for (AmiWebChartPlotPortlet i : this.getPlots()) {
			for (AmiWebChartRenderingLayer j : i.getRenderyingLayers()) {
				if (j != layer && OH.eq(oldDmAliasDotName, j.getDmAliasDotName()) && OH.eq(oldDmTableName, j.getDmTableName()))
					remove = false;
			}
		}
		if (remove)
			removeUsedDm(oldDmAliasDotName, oldDmTableName);
	}

	public void onUsedDmChanged(String oldDmAliasDotName, String oldDmTableName, AmiWebChartRenderingLayer layer) {
		boolean remove = oldDmAliasDotName != null;
		boolean add = true;
		for (AmiWebChartPlotPortlet i : this.getPlots()) {
			for (AmiWebChartRenderingLayer j : i.getRenderyingLayers()) {
				if (OH.eq(oldDmAliasDotName, j.getDmAliasDotName()) && OH.eq(oldDmTableName, j.getDmTableName()))
					remove = false;
				if (j != layer && OH.eq(layer.getDmAliasDotName(), j.getDmAliasDotName()) && OH.eq(layer.getDmTableName(), j.getDmTableName()))
					add = false;
			}
		}
		if (add)
			addUsedDm(layer.getDmAliasDotName(), layer.getDmTableName());
		if (remove)
			removeUsedDm(oldDmAliasDotName, oldDmTableName);
	}
	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		Table table = link.getSourceTableData();
		Table r = new BasicTable(table.getColumns());
		if (type != NONE)
			for (AmiWebChartPlotPortlet plot : this.getAllPlots())
				plot.getSelectedRows(link, type, r);
		return r;
	}
	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		for (AmiWebChartPlotPortlet plot : this.getAllPlots())
			if (plot.hasSelectedRows(link))
				return true;
		return false;
	}
	@Override
	public Set<String> getUsedDmVariables(String dmName, String dmTable, Set<String> r) {
		for (AmiWebChartPlotPortlet plot : this.plotPortletsById.values()) {
			for (AmiWebChartRenderingLayer<AmiWebChartSeries> layer : plot.getRenderyingLayers()) {
				if (OH.eq(dmName, layer.getDmAliasDotName()) && OH.eq(dmTable, layer.getDmTableName())) {
					AmiWebChartSeries s = layer.getSeries();
					if (s != null)
						for (int i = 0, l = s.getFormulasCount(); i < l; i++)
							s.getFormulaAt(i).getDependencies((Set) r);
				}
			}
		}
		return r;
	}

	public AmiWebChartZoom getZoomX(int col) {
		return this.zoomX.get(col);
	}
	public AmiWebChartZoom getZoomY(int row) {
		return this.zoomY.get(row);
	}

	public void moveZoom(int col, int row, int xoffsetDelta, int yoffsetDelta) {
		boolean xChanged = col != -1 && this.zoomX.get(col).moveZoom(xoffsetDelta);
		boolean yChanged = row != -1 && this.zoomY.get(row).moveZoom(yoffsetDelta);
		fireZoomChanged(col, row, xChanged, yChanged);
	}
	public void setZoomAndOffset(int col, int row, double xzoom, double xoffset, double yzoom, double yoffset) {
		boolean xChanged = col != -1 && this.zoomX.get(col).setZoomAndOffset(xzoom, xoffset);
		boolean yChanged = row != -1 && this.zoomY.get(row).setZoomAndOffset(yzoom, yoffset);
		fireZoomChanged(col, row, xChanged, yChanged);
	}
	public void setZoomClip(int col, int row, int xstart, int xend, int ystart, int yend) {
		boolean xChanged = col != -1 && this.zoomX.get(col).zoom(xstart, xend);
		boolean yChanged = row != -1 && this.zoomY.get(row).zoom(ystart, yend);
		fireZoomChanged(col, row, xChanged, yChanged);
	}

	public void onPlotSizeChanged(int col, int row, int width, int height) {
		boolean xChanged = col != -1 && this.getZoomX(col).setLength(width);
		boolean yChanged = row != -1 && this.getZoomY(row).setLength(height);
		fireZoomChanged(col, row, xChanged, yChanged);
	}
	public void zoomAtPoint(int col, int row, int x, int xdelta, int y, int ydelta) {
		boolean xChanged = col != -1 && this.getZoomX(col).zoomAtPoint(x, xdelta);
		boolean yChanged = row != -1 && this.getZoomY(row).zoomAtPoint(y, ydelta);
		fireZoomChanged(col, row, xChanged, yChanged);
	}

	private void fireZoomChanged(int col, int row, boolean xChanged, boolean yChanged) {
		if (xChanged) {
			final AmiWebChartZoom zx = getZoomX(col);
			for (AmiWebChartAxisPortlet axis : CH.getOr(this.axisT, col, (List<AmiWebChartAxisPortlet>) Collections.EMPTY_LIST))
				if (axis != null)
					axis.onUserZoomed(zx);
			for (AmiWebChartAxisPortlet axis : CH.getOr(this.axisB, col, (List<AmiWebChartAxisPortlet>) Collections.EMPTY_LIST))
				if (axis != null)
					axis.onUserZoomed(zx);
			for (int i = 0; i < rowsCount; i++) {
				if (!yChanged || i != row) {//we want to avoid repeat update
					AmiWebChartPlotPortlet plot = this.plots.getAt(col, i);
					if (plot != null)
						plot.onUsedZoomed(zx, getZoomY(i));
				}
			}
		}
		if (yChanged) {
			final AmiWebChartZoom zy = getZoomY(row);
			for (AmiWebChartAxisPortlet axis : CH.getOr(this.axisL, row, (List<AmiWebChartAxisPortlet>) Collections.EMPTY_LIST))
				if (axis != null)
					axis.onUserZoomed(zy);
			for (AmiWebChartAxisPortlet axis : CH.getOr(this.axisR, row, (List<AmiWebChartAxisPortlet>) Collections.EMPTY_LIST))
				if (axis != null)
					axis.onUserZoomed(zy);
			for (int i = 0; i < colsCount; i++) {
				AmiWebChartPlotPortlet plot = this.plots.getAt(i, row);
				if (plot != null)
					plot.onUsedZoomed(getZoomX(i), zy);
			}
		}
	}

	public AmiWebChartZoomMetrics getZoom(int col, int row) {
		final AmiWebChartZoom zx = getZoomX(col);
		final AmiWebChartZoom zy = getZoomY(row);
		return new AmiWebChartZoomMetrics(zx.getLength(), zx.getZoom(), zx.getOffset(), zy.getLength(), zy.getZoom(), zy.getOffset());
	}

	public void onSelectChanged(AmiWebChartPlotPortlet plot) {
		if (plot.getSelectedCount() == 0)
			return;
		for (int x = 0; x < colsCount; x++) {
			for (int y = 0; y < rowsCount; y++) {
				AmiWebChartPlotPortlet p = this.plots.getAt(x, y);
				if (plot != p)
					p.clearSelected();
			}
		}
	}

	public void registerUsedId(int id) {
		if (this.nextId <= id)
			this.nextId = id + 1;
	}

	public byte[] toPngAxis(int w, int h, boolean printBg) {

		/*
		if (!this.getVisible()) {
			LH.warning(this.log, "Unable to draw non-visible chart ", this.getAri(), ", please ensure the chart is visible on screen.");
			return AmiWebChartGraphicsWrapper.emptyImage(w, h);
		}
		*/
		try {
			double sFArray[] = calculateScalingFactor(w, h);
			double scalingFactor = OH.min(sFArray[0], sFArray[1]);

			// calculate proportions of axis dividers/chart dividers
			double[][] props = getChartProportions();
			double[] rows = props[0];
			double[] cols = props[1];
			int origWidth = AmiWebChartUtils.rd(props[2][0]);
			int origHeight = AmiWebChartUtils.rd(props[2][1]);

			// axesDims - dimensions of all axes: [left, top, right, bottom] with scalingFactor applied
			double[] axesDims = getAllAxesDims(scalingFactor);

			int plotW = AmiWebChartUtils.rd(w - axesDims[0] - axesDims[2]);
			int plotH = AmiWebChartUtils.rd(h - axesDims[1] - axesDims[3]);

			Map<Integer, Integer> originalDims = resizeAxes(plotW, plotH, scalingFactor, rows, cols);

			int[] newAxesDims = getAllAxesDims();

			AmiWebImages l = getVerticalAxesImagesForPosition(POS_L, scalingFactor, newAxesDims, printBg);
			AmiWebImages r = getVerticalAxesImagesForPosition(POS_R, scalingFactor, newAxesDims, printBg);
			AmiWebImages t = getHorizontalAxesImagesForPosition(POS_T, scalingFactor, newAxesDims, printBg);
			AmiWebImages b = getHorizontalAxesImagesForPosition(POS_B, scalingFactor, newAxesDims, printBg);

			int[][][] plotOffsets = getPlotOffsets(scalingFactor);

			AmiWebImages[][] plots = getPlotImages(plotW, plotH, props, plotOffsets, sFArray, printBg);

			try {
				// swapping height and width when passing to combineAxesWithPlot to follow conventional row-col format
				byte[] image = AmiWebChartGraphicsWrapper.combineAxesWithPlot(plots, this.plots.getHeight(), this.plots.getWidth(), plotW, plotH, plotOffsets, b, l, t, r,
						this.dividerColor, this.backgroundColor, printBg);

				revertAxes(originalDims, origWidth, origHeight, rows, cols);

				return image;
			} catch (Exception e) {
				LH.warning(AmiWebChartGridPortlet.log, e.getMessage(), e);
			}

		} catch (Exception e) {
			LH.warning(AmiWebChartGridPortlet.log, e.getMessage(), e);
		}
		return AmiWebChartGraphicsWrapper.emptyImage(w, h);
	}

	private Map<Integer, Integer> resizeAxes(int plotW, int plotH, double scalingFactor, double[] rows, double[] cols) {
		Map<Integer, Integer> dims = new HashMap<>();

		// use proportions to appropriately scale all axes
		for (AmiWebChartAxisPortlet axis : this.getAxises()) {
			int axisHeight = axis.getHeight();
			int axisWidth = axis.getWidth();

			if (axis.isVertical()) {
				int width = OH.max(axisWidth, axis.getRequiredSpace());
				int updatedHeight = AmiWebChartUtils.rd(plotH * rows[axis.getRowOrCol()]);
				int updatedWidth = AmiWebChartUtils.rd(width * scalingFactor);
				dims.put(axis.getAxisId(), axisWidth);
				axis.setSize(updatedWidth, updatedHeight);
				axis.getPositionAt(0, 0);
			} else {
				int height = OH.max(axisHeight, axis.getRequiredSpace());
				int updatedWidth = AmiWebChartUtils.rd(plotW * cols[axis.getRowOrCol()]);
				int updatedHeight = AmiWebChartUtils.rd(height * scalingFactor);
				dims.put(axis.getAxisId(), axisHeight);
				axis.setSize(updatedWidth, updatedHeight);
				axis.getPositionAt(0, 0);
			}
		}
		return dims;

	}

	private void revertAxes(Map<Integer, Integer> originalDims, int totalWidth, int totalHeight, double[] rows, double[] cols) {
		// revert axes back to original.
		for (AmiWebChartAxisPortlet axis : this.getAxises()) {
			if (axis.isVertical()) {
				int originalWidth = originalDims.get(axis.getAxisId());
				int originalHeight = AmiWebChartUtils.rd(rows[axis.getRowOrCol()] * totalHeight);
				axis.setSize(originalWidth, originalHeight);
			} else {
				int originalHeight = originalDims.get(axis.getAxisId());
				int originalWidth = AmiWebChartUtils.rd(cols[axis.getRowOrCol()] * totalWidth);
				axis.setSize(originalWidth, originalHeight);
			}
		}
	}

	private AmiWebImages[][] getPlotImages(int width, int height, double[][] proportions, int[][][] offsets, double[] scalingFactor, boolean printBg) {
		double totalWidth = width;
		double totalHeight = height;

		AmiWebImages[][] images = new AmiWebImages[this.plots.getHeight()][this.plots.getWidth()];

		for (int i = 0; i < this.plots.getWidth(); i++) {
			for (int j = 0; j < this.plots.getHeight(); j++) {
				AmiWebChartPlotPortlet plot = this.plots.getAt(i, j);
				int layers = plot.getRenderyingLayersCount();
				AmiWebImageGenerator[] igs = new AmiWebImageGenerator[layers + 1];

				int w = AmiWebChartUtils.rd(proportions[1][i] * totalWidth) - offsets[j][i][0];
				int h = AmiWebChartUtils.rd(proportions[0][j] * totalHeight) - offsets[j][i][1];

				for (int k = 0; k < plot.getRenderyingLayersCount(); k++) {
					AmiWebChartRenderingLayer<?> rl = plot.getRenderyingLayerAt(k);
					if (rl.getType() == AmiWebChartRenderingLayer_Legend.TYPE) {
						AmiWebChartRenderingLayer_Legend lrl = (AmiWebChartRenderingLayer_Legend) rl;
						AmiWebImageGenerator_Legend legend = new AmiWebImageGenerator_Legend(lrl, scalingFactor, w, h, printBg);
						igs[layers] = legend;
					} else {
						igs[k] = rl.createImageGenerator();
					}
				}

				images[j][i] = service.getChartImagesManager().generateAmiWebImages(igs, new AmiWebChartZoomMetrics(w, 1, 0, h, 1, 0));
			}
		}
		return images;
	}

	private int[][][] getPlotOffsets(double scalingFactor) {

		int[][][] offsets = new int[plots.getHeight()][plots.getWidth()][2];

		for (int i = 0; i < plots.getHeight(); i++) {
			for (int j = 0; j < this.plots.getWidth(); j++) {
				offsets[i][j][0] = -1;
				offsets[i][j][1] = -1;
			}
		}

		for (int i = 0; i < plots.getHeight(); i++) {
			for (int j = 0; j < this.plots.getWidth(); j++) {
				AmiWebChartPlotPortlet plot = this.plots.getAt(j, i);
				for (AmiWebChartRenderingLayer rl : plot.getRenderyingLayers()) {
					if (rl instanceof AmiWebChartRenderingLayer_Graph) {
						AmiWebChartRenderingLayer_Graph grl = (AmiWebChartRenderingLayer_Graph) rl;
						int x = AmiWebChartUtils.rd(grl.getXAxis().getStartPadding() * scalingFactor);
						int y = AmiWebChartUtils.rd(grl.getYAxis().getStartPadding() * scalingFactor);

						if (offsets[j][i][0] == -1) {
							offsets[j][i][0] = x;
						} else {
							offsets[j][i][0] = OH.min(x, offsets[j][i][0]);
						}

						if (offsets[j][i][1] == -1) {
							offsets[j][i][1] = y;
						} else {
							offsets[j][i][1] = OH.min(y, offsets[j][i][1]);
						}

					}
				}
			}
		}

		for (int i = 0; i < plots.getHeight(); i++) {
			for (int j = 0; j < plots.getWidth(); j++) {
				if (offsets[i][j][0] == -1)
					offsets[i][j][0] = 0;
				if (offsets[i][j][1] == -1)
					offsets[i][j][1] = 0;
			}
		}

		return offsets;
	}

	private double[][] getChartProportions() {
		int totalWidth = 0, totalHeight = 0;

		double[] rows = new double[this.plots.getHeight()];
		double[] cols = new double[this.plots.getWidth()];

		for (int i = 0; i < this.plots.getHeight(); i++) {
			AmiWebChartPlotPortlet plot = this.getPlot(0, i);
			totalHeight += plot.getHeight();
			rows[i] = plot.getHeight();
		}

		for (int j = 0; j < this.plots.getWidth(); j++) {
			AmiWebChartPlotPortlet plot = this.getPlot(j, 0);
			totalWidth += plot.getWidth();
			cols[j] = plot.getWidth();
		}

		for (int i = 0; i < this.plots.getHeight(); i++) {
			rows[i] /= totalHeight;
		}

		for (int j = 0; j < this.plots.getWidth(); j++) {
			cols[j] /= totalWidth;
		}
		double[] dims = new double[] { totalWidth, totalHeight };
		return new double[][] { rows, cols, dims };
	}

	private static final ToIntFunction<AmiWebChartAxisPortlet> axisToWidth = new ToIntFunction<AmiWebChartAxisPortlet>() {
		@Override
		public int applyAsInt(AmiWebChartAxisPortlet axis) {
			return axis.getWidth();
		}
	};

	private static final ToIntFunction<AmiWebChartAxisPortlet> axisToHeight = new ToIntFunction<AmiWebChartAxisPortlet>() {
		@Override
		public int applyAsInt(AmiWebChartAxisPortlet axis) {
			return axis.getHeight();
		}
	};

	private AmiWebImages getVerticalAxesImagesForPosition(byte position, double scalingFactor, int[] axesDims, boolean printBg) {
		AmiWebImageGenerator[] axesIGs = new AmiWebImageGenerator[this.axisPortletsById.size()];
		int height = 0;
		int count = 0;
		int totalWidth = OH.eq(position, AmiWebChartGridPortlet.POS_L) ? axesDims[0] : axesDims[2];
		for (List<AmiWebChartAxisPortlet> axes : this.getAxis(position)) {
			int width = 0;

			int h = 0;
			int rowWidth = axes.parallelStream().mapToInt(axisToWidth).sum();

			if (OH.eq(position, AmiWebChartGridPortlet.POS_L)) {
				width = totalWidth - rowWidth;
			}

			for (AmiWebChartAxisPortlet axis : axes) {
				axesIGs[count++] = new AmiWebImageGenerator_Axis(axis, width, height, scalingFactor, printBg);
				width += axis.getWidth();

				if (axis.getOffset() == 0)
					h = axis.getHeight();
			}
			height += h;
		}

		return service.getChartImagesManager().generateAmiWebImages(axesIGs, new AmiWebChartZoomMetrics(totalWidth, 1, 0, height, 1, 0));
	}

	private AmiWebImages getHorizontalAxesImagesForPosition(byte position, double scalingFactor, int[] axesDims, boolean printBg) {
		AmiWebImageGenerator[] axesIGs = new AmiWebImageGenerator[this.axisPortletsById.size()];
		int width = 0;
		int count = 0;
		int totalHeight = OH.eq(position, AmiWebChartGridPortlet.POS_T) ? axesDims[1] : axesDims[3];
		for (List<AmiWebChartAxisPortlet> axes : this.getAxis(position)) {
			int height = 0;
			int colHeight = axes.stream().mapToInt(axisToHeight).sum();
			if (OH.eq(position, AmiWebChartGridPortlet.POS_T)) {
				height = totalHeight - colHeight;
			}
			int w = 0;
			for (AmiWebChartAxisPortlet axis : axes) {
				axesIGs[count++] = new AmiWebImageGenerator_Axis(axis, width, height, scalingFactor, printBg);
				height += axis.getHeight();
				if (axis.getOffset() == 0)
					w = axis.getWidth();
			}
			width += w;
		}

		return service.getChartImagesManager().generateAmiWebImages(axesIGs, new AmiWebChartZoomMetrics(width, 1, 0, totalHeight, 1, 0));
	}

	private double[] calculateScalingFactor(int w, int h) {
		// tuned to be as close as possible to the actual in browser chart
		final double SCREEN_SCALAR = 2.0;

		double height = this.service.getDesktop().getHeight();
		double width = this.service.getDesktop().getWidth();

		return new double[] { w / width * SCREEN_SCALAR, h / height * SCREEN_SCALAR };
	}

	private double[] getAllAxesDims(double scalingFactor) {
		int[] dims = getAllAxesDims();
		double[] scaledDims = new double[dims.length];

		for (int i = 0; i < dims.length; i++) {
			scaledDims[i] = dims[i] * scalingFactor;
		}
		return scaledDims;
	}

	private int[] getAllAxesDims() {

		int top = 0, left = 0, right = 0, bottom = 0;

		for (List<AmiWebChartAxisPortlet> axisList : this.getAxis(AmiWebChartGridPortlet.POS_T)) {
			int sumAll = axisList.stream().mapToInt(new ToIntFunction<AmiWebChartAxisPortlet>() {
				@Override
				public int applyAsInt(AmiWebChartAxisPortlet axis) {
					return axis.getHeight();
				}
			}).sum();
			top = OH.max(top, sumAll);
		}

		for (List<AmiWebChartAxisPortlet> axisList : this.getAxis(AmiWebChartGridPortlet.POS_B)) {
			int sumAll = axisList.stream().mapToInt(new ToIntFunction<AmiWebChartAxisPortlet>() {
				@Override
				public int applyAsInt(AmiWebChartAxisPortlet axis) {
					return axis.getHeight();
				}
			}).sum();
			bottom = OH.max(bottom, sumAll);
		}

		for (List<AmiWebChartAxisPortlet> axisList : this.getAxis(AmiWebChartGridPortlet.POS_L)) {
			int sumAll = axisList.stream().mapToInt(new ToIntFunction<AmiWebChartAxisPortlet>() {
				@Override
				public int applyAsInt(AmiWebChartAxisPortlet axis) {
					return axis.getWidth();
				}
			}).sum();
			left = OH.max(left, sumAll);
		}

		for (List<AmiWebChartAxisPortlet> axisList : this.getAxis(AmiWebChartGridPortlet.POS_R)) {
			int sumAll = axisList.stream().mapToInt(new ToIntFunction<AmiWebChartAxisPortlet>() {
				@Override
				public int applyAsInt(AmiWebChartAxisPortlet axis) {
					return axis.getWidth();
				}
			}).sum();
			right = OH.max(right, sumAll);
		}

		return new int[] { left, top, right, bottom };
	}

	public byte[] toPng(int plotX, int plotY, int w, int h, boolean printBg) {
		AmiWebChartPlotPortlet plot = this.getPlot(plotX, plotY);
		int origW = plot.getWidth();
		int origH = plot.getHeight();
		AmiWebImageGenerator[] imageGenerators = new AmiWebImageGenerator[plot.getRenderyingLayersCount()];
		Set<AmiWebChartAxisPortlet> xAxes = new LinkedHashSet<AmiWebChartAxisPortlet>();
		Set<AmiWebChartAxisPortlet> yAxes = new LinkedHashSet<AmiWebChartAxisPortlet>();
		for (int i = 0; i < plot.getRenderyingLayersCount(); i++) {
			AmiWebChartRenderingLayer<?> rl = plot.getRenderyingLayerAt(i);
			AmiWebChartAxisPortlet xAxis = rl.getXAxis();
			if (xAxis != null && xAxes.add(xAxis))
				xAxis.setSize(w, xAxis.getHeight());
			AmiWebChartAxisPortlet yAxis = rl.getYAxis();
			if (yAxis != null && yAxes.add(yAxis))
				yAxis.setSize(yAxis.getWidth(), h);

			AmiWebImageGenerator cig = rl.createImageGenerator();
			imageGenerators[imageGenerators.length - 1 - i] = cig;
		}
		AmiWebImages t = service.getChartImagesManager().generateAmiWebImages(imageGenerators, new AmiWebChartZoomMetrics(w, 1, 0, h, 1, 0));

		for (AmiWebChartAxisPortlet xAxis : xAxes)
			xAxis.setSize(origW, xAxis.getHeight());
		for (AmiWebChartAxisPortlet yAxis : yAxes)
			yAxis.setSize(yAxis.getWidth(), origH);

		if (printBg) {
			String bgColor = plot.getBgColor();
			return AmiWebChartGraphicsWrapper.addBgToImage(t, bgColor);
		}

		if (printBg) {
			String bgColor = plot.getBgColor();
			return AmiWebChartGraphicsWrapper.addBgToImage(t, bgColor);
		}

		return t.getImage();
	}

	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		super.onDmNameChanged(oldAliasDotName, dm);
		for (AmiWebChartPlotPortlet i : this.plotPortletsById.values())
			i.onDmNameChanged(oldAliasDotName, dm);
	}

	public String getDescFontFamily() {
		return this.descFontFamily;
	}
	public String getDescFontStyle() {
		return this.descFontStyle;
	}
	public ColorGradient getColorGradient() {
		return this.colorGradient;
	}

	public List<String> getColorSeries() {
		return this.colorSeries;
	}

	public List<Color> getColorSeriesColors() {
		return this.colorSeriesColors;
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> r = super.getChildDomObjects();
		CH.addAll(r, this.getPlots());
		CH.addAll(r, this.getAxises());
		return r;
	}

	public int getXPos(AmiWebChartPlotPortlet o) {
		return this.plots.getXIndex(o);
	}
	public int getYPos(AmiWebChartPlotPortlet o) {
		return this.plots.getYIndex(o);
	}

	@Override
	public void updateAri() {
		super.updateAri();
		for (AmiWebChartAxisPortlet i : this.getAllAxis())
			i.updateAri();
		for (AmiWebChartPlotPortlet i : this.getAllPlots())
			i.updateAri();
	}
	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebChartSettingsPortlet(generateConfig(), this);
	}
	@Override
	public void close() {
		super.close();
		for (AmiWebChartOptionsPortlet i : this.optionsPortlets) {
			i.close();
		}
	}
}
