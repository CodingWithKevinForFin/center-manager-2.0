package com.f1.suite.web.portal.impl.visual;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.base.TableListener;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.utils.TableHelper;

public class PieChartPortlet extends AbstractPortlet implements TableListener {

	public static final PortletSchema<PieChartPortlet> SCHEMA = new BasicPortletSchema<PieChartPortlet>("PieChart", "PieChartPortlet", PieChartPortlet.class, true, true);

	private TableListenable table;
	private boolean changed = true;
	private String labelColumnId;
	private String valueColumnId;

	public PieChartPortlet(PortletConfig portletConfig) {
		super(portletConfig);
	}

	public TableListenable getTable() {
		return table;
	}

	public void setTable(TableListenable table, String labelColumnId, String valueColumnId) {
		this.table = table;
		this.table.addTableListener(this);
		this.labelColumnId = labelColumnId;
		this.valueColumnId = valueColumnId;
	}

	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		flagPendingAjax();
		changed = true;
	}

	@Override
	public void onColumnAdded(Column nuw) {
		flagPendingAjax();
		changed = true;
	}

	@Override
	public void onColumnRemoved(Column old) {
		flagPendingAjax();
		changed = true;
	}

	@Override
	public void onColumnChanged(Column old, Column nuw) {
		flagPendingAjax();
		changed = true;
	}

	@Override
	public void onRowAdded(Row add) {
		flagPendingAjax();
		changed = true;
	}

	@Override
	public void onRowRemoved(Row removed, int location) {
		flagPendingAjax();
		changed = true;
	}
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			flagPendingAjax();
			changed = true;
		}
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible() && changed) {
			JsFunction js = callJsFunction("updateData");
			int labelLoc = table.getColumn(this.labelColumnId).getLocation();
			int valueLoc = table.getColumn(this.valueColumnId).getLocation();
			TableHelper.sortDesc(table, this.labelColumnId);
			for (Row row : table.getRows())
				js.addParamQuoted(row.getAt(labelLoc)).addParam(row.getAt(valueLoc));
			js.end();
		}

	}

	@Override
	public PortletSchema<PieChartPortlet> getPortletSchema() {
		return SCHEMA;
	}
}
