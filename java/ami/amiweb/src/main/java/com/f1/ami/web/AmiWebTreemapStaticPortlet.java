package com.f1.ami.web;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableListener;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.visual.TreemapNode;
import com.f1.suite.web.portal.impl.visual.WebTreemapContextMenuFactory;
import com.f1.suite.web.portal.impl.visual.WebTreemapContextMenuListener;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.structs.Tuple2;

public class AmiWebTreemapStaticPortlet extends AmiWebTreemapPortlet
		implements TableListener, WebTreemapContextMenuFactory, WebTreemapContextMenuListener, AmiWebDmPortlet, AmiWebDmListener {
	private static final Logger log = LH.get();

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebTreemapStaticPortlet> implements AmiWebDmPortletBuilder<AmiWebTreemapStaticPortlet> {

		public static final String OLD_ID = "AmiTreemapStaticPortlet";
		public static final String ID = "amistatictreemap";

		public Builder() {
			super(AmiWebTreemapStaticPortlet.class);
		}

		@Override
		public AmiWebTreemapStaticPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebTreemapStaticPortlet r = new AmiWebTreemapStaticPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Heat Map";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

		@Override
		public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig) {
			return AmiWebUsedDmSingleton.extractUsedDmAndTables(portletConfig);
		}

		@Override
		public void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name) {
			AmiWebUsedDmSingleton.replaceUsedDmAndTable(portletConfig, position, name);
		}

	}

	final private AmiWebUsedDmSingleton dmSingleton;
	private boolean clearOnDataStale;

	public AmiWebTreemapStaticPortlet(PortletConfig config) {
		super(config);
		this.clearOnDataStale = true;
		this.dmSingleton = new AmiWebUsedDmSingleton(getService().getDmManager(), this);
		this.rebuildFormulas();
		this.rebuildAmiData();
	}

	@Override
	public void rebuildAmiData() {
		if (isInitDone() && this.dmSingleton != null) {
			AmiWebDm dm = this.dmSingleton.getDm();
			if (dm != null)
				onDmDataChanged(dm);
		}
	}

	@Override
	public boolean isRealtime() {
		return false;
	}

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		showWaitingSplash(false);
		HashSet<Tuple2<Object, Object>> selectedKeys = new HashSet<Tuple2<Object, Object>>();
		for (TreemapNode n : treemap.getSelected().values())
			selectedKeys.add(new Tuple2<Object, Object>(n.getParent().getGroupId(), n.getGroupId()));
		Table table = this.dmSingleton.getTable();
		clearAmiData();
		if (table != null)
			addRowsFromTable(table, 0, table.getRows(), null);
		if (selectedKeys.size() > 0) {
			Tuple2<Object, Object> key = new Tuple2<Object, Object>();
			for (TreemapNode n : treemap.getNodes()) {
				key.setAB(n.getParent().getGroupId(), n.getGroupId());
				if (selectedKeys.remove(key)) {
					// prevent firing onSelected cb
					n.setSelectedNoFire(true);
				}
			}
		}
		// if not empty, one or more of the selected keys was removed as a result of data change
		if (!selectedKeys.isEmpty()) {
			this.onSelectionChanged(treemap, null, false);
		}
	}
	private void addRowsFromTable(Table tbl, long startingAmoId, List<Row> rows, List<Row> existing) {
		int colCount = tbl.getColumnsCount();
		int[] mapping = new int[colCount];
		Map<String, Column> tableCols = aggregator.getInnerTable().getColumnsMap();
		int idLoc = tableCols.get(AmiConsts.TABLE_PARAM_ID).getLocation();
		for (int i = 0; i < colCount; i++) {
			Column columnAt = tbl.getColumnAt(i);
			Column col2 = tableCols.get(columnAt.getId());
			if (col2 == null)
				col2 = aggregator.getInnerTable().addColumn(columnAt.getType(), columnAt.getId());
			mapping[i] = col2 == null ? -1 : col2.getLocation();
		}
		for (int j = 0; j < rows.size(); j++) {
			Row row = rows.get(j);
			Row row2 = existing == null ? null : existing.get(j);
			if (row2 == null) {
				long id = startingAmoId++;
				Object[] vals = new Object[tableCols.size()];
				vals[idLoc] = id;
				for (int i = 0; i < colCount; i++) {
					int pos = mapping[i];
					if (pos != -1)
						vals[pos] = row.getAt(i);
				}
				row2 = aggregator.getInnerTable().getRows().addRow(vals);
			} else {
				for (int i = 0; i < colCount; i++) {
					int pos = mapping[i];
					if (pos != -1)
						row2.putAt(pos, row.getAt(i));
				}
			}
		}
	}
	public void setUsedDatamodel(String dmName, String tableName) {
		this.dmSingleton.setUsedDm(dmName, tableName);
		for (AmiWebDmLink i : getDmLinksFromThisPortlet())
			i.setSourceDm(dmName, tableName);
		for (AmiWebDmLink i : getDmLinksToThisPortlet())
			i.setTargetDm(dmName);
	}
	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		onRowAdded(row);
	}
	@Override
	public void onRowAdded(Row add) {
		addRow(add);
	}
	@Override
	public void onRowRemoved(Row row, int location) {
		removeRow(row);
	}
	protected void updateChildTables() {
		for (AmiWebDmLink link : getDmLinksFromThisPortlet())
			if (link.isRunOnSelect())
				AmiWebDmUtils.sendRequest(getService(), link);
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		return this.treemap.getSelected().size() > 0;
	}

	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		return getUnderlyingSelectableRows(type);
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		this.dmSingleton.init(getAmiLayoutFullAlias(), configuration);
		//		this.resetOverrides();
		//		applyFormulas(sb);
		//				this.setFormulas(this.getGroupingFormula().get(), this.getLabelFormula().get(), this.getSizeFormula().get(), this.getHeatFormula().get(), this.getTooltipFormula().get(),
		//						, sb);
		this.clearOnDataStale = CH.getOrNoThrow(Caster_Boolean.INSTANCE, configuration, "cods", true); // If null or not in configuration true
		super.init(configuration, origToNewIdMapping, sb);
	}
	@Override
	public Map<String, Object> getConfiguration() {
		final Map<String, Object> r = super.getConfiguration();
		this.dmSingleton.getConfiguration(getAmiLayoutFullAlias(), r);
		if (this.clearOnDataStale == false) //Only put in configuration if false
			r.put("cods", this.clearOnDataStale);
		return r;
	}
	@Override
	public String getConfigMenuTitle() {
		return "Static Heat Map";
	}

	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return getVisible();
	}

	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
		if (isRequery)
			return;
		showWaitingSplash(true);
		if (clearOnDataStale)
			clearAmiData();
	}

	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {
		clearAmiData();
		showWaitingSplash(false);
	}

	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
	}
	public AmiWebDmTableSchema getDm() {
		return dmSingleton.getDmTableSchema();
	}

	@Override
	public Set<String> getUsedDmVariables(String dmAliasDotName, String dmTable, Set<String> r) {
		if (this.dmSingleton.matches(dmAliasDotName, dmTable)) {
			for (String i : this.getUsedVariables().getVarKeys())
				r.add(i);
		}
		return r;
	}
	@Override
	public Set<String> getUsedDmTables(String aliasDotNames) {
		return this.dmSingleton.getUsedDmTables(aliasDotNames);
	};

	@Override
	public Set<String> getUsedDmAliasDotNames() {
		return this.dmSingleton.getUsedDmAliasDotNames();
	}
	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		this.dmSingleton.onDmNameChanged(oldAliasDotName, dm);
	}

	@Override
	public String getPanelType() {
		return "treemap";
	}
	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
		this.onDmDataChanged(null);
	}
	public boolean isClearOnDataStale() {
		return clearOnDataStale;
	}

	public void setClearOnDataStale(boolean clearOnDataStale) {
		this.clearOnDataStale = clearOnDataStale;
	}

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebStaticTreemapSettingsPortlet(generateConfig(), this);
	}

}
