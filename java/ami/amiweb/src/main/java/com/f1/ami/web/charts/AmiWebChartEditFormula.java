package com.f1.ami.web.charts;

import java.util.Set;

import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public abstract class AmiWebChartEditFormula<T extends AmiWebChartFormula> {

	private boolean required;
	protected final int pos;
	protected final AmiWebChartEditSeriesPortlet target;
	protected final T formula;

	public AmiWebChartEditFormula(int pos, AmiWebChartEditSeriesPortlet target, T formula) {
		this.pos = pos;
		this.target = target;
		this.formula = formula;
	}

	final public T getFormula() {
		return this.formula;
	}

	abstract public FormPortletField<?> getField();

	abstract public boolean test(StringBuilder sb);

	abstract public void applyValue();

	abstract public void resetFromFormula();

	public void setRequired(boolean b) {
		this.required = b;
	}
	public boolean getRequired() {
		return this.required;
	}

	abstract public void setTitle(String string);
	abstract public String getTitle();

	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		switch (getFormula().getType()) {
			case AmiWebChartSeries.TYPE_SHAPE:
				WebMenu shapes = new BasicWebMenu("Shapes", true);
				if (this.target.getSeries().getSeriesType() == AmiWebChartSeries.SERIES_TYPE_RADIAL)
					shapes.add(new BasicWebMenuLink("wedge", true, "co_wedge"));
				else if (this.target.getSeries().getSeriesType() == AmiWebChartSeries.SERIES_TYPE_SURFACE)
					shapes.add(new BasicWebMenuLink("pyramid", true, "co_pyramid")); 
				else
					shapes.add(new BasicWebMenuLink("circle", true, "co_circle"));
				if (this.target.getSeries().getSeriesType() != AmiWebChartSeries.SERIES_TYPE_RADIAL) {
					shapes.add(new BasicWebMenuLink("diamond", true, "co_diamond"));
					shapes.add(new BasicWebMenuLink("square", true, "co_square"));
					shapes.add(new BasicWebMenuLink("triangle", true, "co_triangle"));
				}
				if (this.target.getSeries().getSeriesType() == AmiWebChartSeries.SERIES_TYPE_XY) {
					shapes.add(new BasicWebMenuLink("cross", true, "co_cross"));
					shapes.add(new BasicWebMenuLink("tick", true, "co_tick"));
					shapes.add(new BasicWebMenuLink("hexagon", true, "co_hexagon"));
					shapes.add(new BasicWebMenuLink("pentagon", true, "co_pentagon"));
					shapes.add(new BasicWebMenuLink("Horizontal Bar", true, "co_hbar"));
					shapes.add(new BasicWebMenuLink("Vertical Bar", true, "co_vbar"));
				}
				r.add(shapes);
				return r;
			case AmiWebChartSeries.TYPE_POSITION:
				WebMenu positions = new BasicWebMenu("Positions", true);
				positions.add(new BasicWebMenuLink("center", true, "co_center"));
				positions.add(new BasicWebMenuLink("top", true, "co_top"));
				positions.add(new BasicWebMenuLink("bottom", true, "co_bottom"));
				positions.add(new BasicWebMenuLink("left", true, "co_left"));
				positions.add(new BasicWebMenuLink("right", true, "co_right"));
				positions.add(new BasicWebMenuLink("topleft", true, "co_topleft"));
				positions.add(new BasicWebMenuLink("topright", true, "co_topright"));
				positions.add(new BasicWebMenuLink("bottomleft", true, "co_bottomleft"));
				positions.add(new BasicWebMenuLink("bottomright", true, "co_bottomright"));
				r.add(positions);
				break;
		}
		WebMenu variables;

		if (getFormula().getType() == AmiWebChartSeries.TYPE_AXIS) {
			variables = new BasicWebMenu("Variables", true);
			variables.add(new BasicWebMenuLink("n", true, "var_n"));
		} else
			variables = createVariablesMenu("Variables", "", this.target.getSeries().getDataModelSchema());
		r.add(variables);
		AmiWebMenuUtils.createAggOperatorsMenu(r, false);

		if (formula != this.target.getSeries().getNameFormula()) {
			variables.add(new BasicWebMenuDivider());
			variables.add(new BasicWebMenuLink(AmiWebChartSeries.VARNAME_ROWNUM + " (Row Number)", true, "var_" + AmiWebChartSeries.VARNAME_ROWNUM).setAutoclose(false)
					.setCssStyle("_fm=courier"));
			variables.add(new BasicWebMenuLink(AmiWebChartSeries.VARNAME_SERIESNUM + " (Series Number)", true, "var_" + AmiWebChartSeries.VARNAME_SERIESNUM).setAutoclose(false)
					.setCssStyle("_fm=courier"));
			variables.add(new BasicWebMenuLink(AmiWebChartSeries.VARNAME_SERIESCNT + " (Series Count)", true, "var_" + AmiWebChartSeries.VARNAME_SERIESCNT).setAutoclose(false)
					.setCssStyle("_fm=courier"));
		}
		AmiWebMenuUtils.createOperatorsMenu(r, target.getService(), this.target.getContainer().getAmiLayoutFullAlias());
		return r;
	}
	public static WebMenu createVariablesMenu(String menuName, String prefix, AmiWebDmTableSchema amiWebDmTableSchema) {
		WebMenu variables = new BasicWebMenu(menuName, true);

		if (amiWebDmTableSchema == null) {
			variables.add(new BasicWebMenuLink("Table missing from datamodel", false, "var").setCssStyle("_fg=#000000|_bg=#FFaaaa"));
			return variables;
		}

		Set<String> columns = amiWebDmTableSchema.getColumnNames();
		for (String column : CH.sort(columns, SH.COMPARATOR_CASEINSENSITIVE_STRING)) {
			variables.add(new BasicWebMenuLink(column, true, "var_" + prefix + column).setAutoclose(false).setCssStyle("_fm=courier"));
		}
		return variables;
	}

	public void onContextMenu(FormPortletField field, String action) {
		AmiWebMenuUtils.processContextMenuAction(this.target.getService(), action, field);
	}

	abstract public boolean isPopulated();

	public boolean isVisible() {
		return pos != -1;
	}
}
