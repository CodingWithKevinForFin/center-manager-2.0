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
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.json.JsonBuilder;

public class LineChartPortlet extends AbstractPortlet implements TableListener {

	public static final PortletSchema<LineChartPortlet> SCHEMA = new BasicPortletSchema<LineChartPortlet>("LineChart", "LineChartPortlet", LineChartPortlet.class, true, true);

	private TableListenable table;
	private boolean changed = true;
	private String labelColumnId;
	private String valueColumnId;

	public LineChartPortlet(PortletConfig portletConfig) {
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
			TableHelper.sort(table, this.labelColumnId);
			JsonBuilder json = js.startJson();
			json.startList();
			int last = 0;
			for (Row row : table.getRows()) {
				json.startMap();
				json.addKeyValue("x", row.getAt(labelLoc, Caster_Integer.INSTANCE));
				json.addKeyValue("y1", row.getAt(valueLoc, Caster_Integer.INSTANCE));
				json.endMap();
			}
			json.endList();
			json.end();
			js.end();
		}

	}

	@Override
	public PortletSchema<LineChartPortlet> getPortletSchema() {
		return SCHEMA;
	}
}
