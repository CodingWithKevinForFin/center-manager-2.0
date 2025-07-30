package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.base.IterableAndSize;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.visual.TreemapNode;
import com.f1.suite.web.portal.impl.visual.TreemapPortlet;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.impl.FastArrayList;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.AggregateGroupByColumn;

public class AmiWebTreemapRealtimePortlet extends AmiWebTreemapPortlet implements AmiWebRealtimePortlet {
	private static final Logger log = LH.get();

	public AmiWebTreemapRealtimePortlet(PortletConfig config) {
		super(config);
		//		getService().getWebManagers().addAmiListener(this, Collections.EMPTY_SET);
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebTreemapPortlet> {

		public static final String OLD_ID = "AmiTreemapPortlet";
		public static final String ID = "amirealtimetreemap";

		public Builder() {
			super(AmiWebTreemapPortlet.class);
		}

		@Override
		public AmiWebTreemapPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebTreemapPortlet r = new AmiWebTreemapRealtimePortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Realtime Treemap";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	final public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		aggregator.addAmiObject((AmiWebObject) entity, null);
	}

	@Override
	final public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {
		aggregator.addAmiObject((AmiWebObject) entity, fields);

	}

	@Override
	final public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		aggregator.removeAmiObject(entity);
	}

	protected AmiWebRealtimeTableCustomFilter customFilter = null;
	private final AmiWebRealtimeTableCustomFilter filter = new AmiWebRealtimeTableCustomFilter(getService(), this.getAmiLayoutFullAlias());

	@Override
	public void processFilter(AmiWebDmLink link, Table table) {
		this.currentFilter = link;
		if (AmiWebRealtimeTableCustomFilter.getWhereClause(link) != null) {
			if (table == null) {
				if (customFilter == AmiWebRealtimeTableCustomFilter.KEEP_ALL)
					return;
				this.customFilter = AmiWebRealtimeTableCustomFilter.KEEP_ALL;
				rebuildAmiData();
				clearTreemap();
				for (Row row : this.aggregator.getAggregateTable().getRows())
					onRowAdded(row);
			} else if (table.getSize() == 0) {
				if (customFilter == AmiWebRealtimeTableCustomFilter.HIDE_ALL)
					return;
				this.customFilter = AmiWebRealtimeTableCustomFilter.HIDE_ALL;
				rebuildAmiData();
				clearTreemap();
				for (Row row : this.aggregator.getAggregateTable().getRows())
					onRowAdded(row);
			} else {
				if (customFilter != this.filter)
					this.customFilter = filter;
				if (customFilter.reset(link, table, aggregator.getAggregateTable().getColumnTypesMapping(), null) != AmiWebRealtimeTableCustomFilter.RESET_NOCHANGE) {
					rebuildAmiData();
					clearTreemap();
					for (Row row : this.aggregator.getAggregateTable().getRows()) {
						onRowAdded(row);
					}
				}
			}
			onAmiSelectedChanged();
		} else
			this.customFilter = null;
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		Collection types = (Collection) CH.getOr(Caster_Simple.OBJECT, configuration, "amiTypes", null);
		if (types != null) {//backwards Compatibility
			HashSet<String> set = new HashSet<String>();
			for (Object s : types)
				set.add(AmiWebManagers.FEED + s);
			setDataTypes(set);
		} else {
			Collection<String> types2 = (Collection) CH.getOr(Caster_Simple.OBJECT, configuration, "rtSources", null);
			HashSet<String> adn = new HashSet<String>();
			if (CH.isntEmpty(types2))
				for (String s : types2)
					adn.add(AmiWebUtils.getFullRealtimeId(this.getAmiLayoutFullAlias(), s));
			setDataTypes(adn);
		}
		super.init(configuration, origToNewIdMapping, sb);
		this.downstreamRealtimeModeByte = AmiWebUtils.parseDownstreamMode(CH.getOr(Caster_String.INSTANCE, configuration, "rtDownstreamMode", "SELECTED_OR_ALL"));
		this.downstreamRealtimeMode.set(this.downstreamRealtimeModeByte, true);
	}
	@Override
	public com.f1.base.CalcTypes getLinkableVars() {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		Map<Object, AggregateGroupByColumn> cols = aggregator.getAggregateTable().getGroupbyColumns();
		for (Entry<Object, AggregateGroupByColumn> i : cols.entrySet())
			r.putType((String) i.getKey(), i.getValue().getType());
		return r;
	}
	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		if (customFilter != null) {
			boolean keep = customFilter == null || customFilter.shouldKeepAndCheckForIndex(row);
			if (keep)
				addRow(row);
			else
				removeRow(row);
		}
		super.addRow(row);
	}
	@Override
	public void onRowAdded(Row add) {
		if (customFilter != null && !customFilter.shouldKeepAndCheckForIndex(add))
			return;
		addRow(add);
	}
	@Override
	public void onRowRemoved(Row row, int location) {
		if (customFilter != null && !customFilter.shouldKeepAndCheckForIndex(row))
			return;
		removeRow(row);
	}
	@Override
	public void rebuildAmiData() {
		for (String s : this.getLowerRealtimeIds()) {
			Iterable<AmiWebObject> t = this.getService().getWebManagers().getAmiObjectsByType(s).getAmiObjects();
			if (t != null)
				for (AmiWebObject i : t)
					onAmiEntityAdded(this, i);
		}

	}

	@Override
	public void onLinkingChanged(AmiWebDmLink link) {
		if (this.customFilter != null && this.getDmLinksToThisPortlet().isEmpty()) {
			this.customFilter = null;
			clearAmiData();
			rebuildAmiData();
		}
		super.onLinkingChanged(link);
	}

	private AmiWebDmLink currentFilter;

	@Override
	public AmiWebDmLink getCurrentLinkFilteringThis() {
		return this.currentFilter;
	}

	@Override
	public boolean isRealtime() {
		return true;
	}

	final private Set<String> dataTypes = new HashSet<String>();

	@Override
	final public Set<String> getLowerRealtimeIds() {
		return dataTypes;
	}
	public void setDataTypes(Set<String> selected) {
		if (selected.equals(this.dataTypes))
			return;
		for (String s : this.dataTypes)
			this.getService().getWebManagers().getAmiObjectsByType(s).removeAmiListener(this);
		this.dataTypes.clear();
		this.dataTypes.addAll(selected);
		this.clearAmiData();
		super.updatePanelTypesVar(this.getLowerRealtimeIds());
		for (String s : this.dataTypes)
			this.getService().getWebManagers().getAmiObjectsByType(s).addAmiListener(this);
	}
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		HashSet<String> relative = new HashSet<String>();
		for (String s : this.getLowerRealtimeIds()) {
			s = AmiWebUtils.getRelativeRealtimeId(this.getAmiLayoutFullAlias(), s);
			relative.add(s);
		}
		r.put("rtSources", relative);
		r.put("rtDownstreamMode", AmiWebUtils.formatDownstreamMode(this.downstreamRealtimeMode.getValue()));
		return r;
	}

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
		this.clearAmiData();
		this.rebuildAmiData();
	}

	@Override
	public String getConfigMenuTitle() {
		return "Realtime Heat Map";
	}

	@Override
	public String getPanelType() {
		return "treemap";
	}
	@Override
	public void onClosed() {
		for (String s : this.dataTypes)
			this.getService().getWebManagers().getAmiObjectsByType(s).removeAmiListener(this);
		super.onClosed();
	}
	@Override
	public IterableAndSize<AmiWebObject> getAmiObjects() {
		IntKeyMap<TreemapNode> selected = treemap.getSelected();
		if (selected.isEmpty()) {
			IterableAndSize<TreemapNode> nodes = treemap.getNodes();
			FastArrayList<AmiWebObject> data = new FastArrayList<AmiWebObject>(nodes.size());
			for (TreemapNode n : nodes) {
				Row inner = (Row) n.getCorrelationData();
				if (inner != null)
					data.add((AmiWebObject) getAmiWebObject(inner));
			}
			return data;
		} else {
			FastArrayList<AmiWebObject> data = new FastArrayList<AmiWebObject>(selected.size());
			for (TreemapNode n : selected.values()) {
				Row inner = (Row) n.getCorrelationData();
				if (inner != null)
					data.add((AmiWebObject) getAmiWebObject(inner));
			}
			return data;
		}
	}

	protected AmiWebObject getAmiWebObject(Row row) {
		//		return this.aggregator.getAmiObject(row);
		return (AmiWebObject) row.getAt(0);
	}

	private IdentityHashSet<Row> downstreamRows = new IdentityHashSet<Row>();
	private AmiWebRealtimeObjectListener[] amiListeners = AmiWebRealtimeObjectListener.EMPTY_ARRAY;
	private Set<String> upperRealtimeIds = new HashSet<String>();

	@Override
	public boolean removeAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i == -1)
			return false;
		this.amiListeners = AH.remove(this.amiListeners, i);
		if (this.amiListeners.length == 0)
			downstreamRows.clear();
		this.getService().getWebManagers().onListenerRemoved(this, listener);
		return true;
	}

	@Override
	public boolean addAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i != -1)
			return false;
		this.amiListeners = AH.append(this.amiListeners, listener);
		if (this.amiListeners.length == 1)
			sendAmiRowsDownstream();
		this.getService().getWebManagers().onListenerAdded(this, listener);
		return true;
	}
	@Override
	public boolean hasAmiListeners() {
		return this.amiListeners.length > 0;
	}

	//	@Override
	//	public List<AmiWebRealtimeObjectListener> getListeners() {
	//		return this.amiListeners;
	//	}

	private void sendAmiRowsDownstream() {
		if (isDownstreamEnabled()) {
			final List<AmiWebObject> del = new ArrayList<AmiWebObject>();
			final List<AmiWebObject> add = new ArrayList<AmiWebObject>();
			IterableAndSize<TreemapNode> nodes = treemap.getSelected().values();
			if (nodes.size() == 0)
				nodes = treemap.getNodes();
			final IdentityHashSet<Row> selectedRows = new IdentityHashSet<Row>();//super.getSelectableRows(null, this.hasSelectedRows(null) ? SELECTED : ALL).getRows());
			for (TreemapNode node : nodes) {
				if (node.getCorrelationData() == null)
					continue;
				selectedRows.add((Row) node.getCorrelationData());
			}
			for (final Row r : CH.comm(this.downstreamRows, selectedRows, true, false, false)) {
				this.downstreamRows.remove(r);
				del.add(getAmiWebObject(r));
			}
			for (final Row r : CH.comm(this.downstreamRows, selectedRows, false, true, false)) {
				this.downstreamRows.add(r);
				add.add(getAmiWebObject(r));
			}
			if (!del.isEmpty()) {
				for (final AmiWebObject o : del)
					this.fireOnAmiEntityRemovedDownstream(o);
			}
			if (!add.isEmpty()) {
				for (final AmiWebObject o : add)
					this.fireOnAmiEntityAddedDownstream(o);
			}
		}
	}

	@Override
	protected void updateChildTables() {
		sendAmiRowsDownstream();
		super.updateChildTables();
	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectschema() {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (String type : this.dataTypes)
			r.putAll(this.getService().getWebManagers().getAmiObjectsByType(type).getRealtimeObjectsOutputSchema());
		return r;
	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectsOutputSchema() {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		NameSpaceCalcTypes columnTypesMapping = this.getTable().getColumnTypesMapping();
		for (String e : columnTypesMapping.getVarKeys())
			if (!e.startsWith("#"))
				r.putType(e, columnTypesMapping.getType(e));
		return r;
	}

	@Override
	public String getRealtimeId() {
		return this.getAri();
	}

	@Override
	public Set<String> getUpperRealtimeIds() {
		return AmiWebUtils.updateRealtimeIds(this.amiListeners, this.upperRealtimeIds);
	}

	private AmiWebOverrideValue<Byte> downstreamRealtimeMode = new AmiWebOverrideValue<Byte>(DOWN_STREAM_MODE_SELECTED_OR_ALL);
	private byte downstreamRealtimeModeByte = downstreamRealtimeMode.get();

	private boolean isDownstreamEnabled() {
		return this.downstreamRealtimeModeByte != DOWN_STREAM_MODE_OFF && this.amiListeners.length > 0;
	}
	@Override
	public AmiWebOverrideValue<Byte> getDownstreamRealtimeMode() {
		return this.downstreamRealtimeMode;
	}

	@Override
	public void setDownstreamRealtimeMode(byte mode) {
		if (downstreamRealtimeMode.set(mode, true))
			onDownstreamRealtimeModeChanged();
	}

	private void onDownstreamRealtimeModeChanged() {
		this.downstreamRealtimeModeByte = this.downstreamRealtimeMode.get();
		if (this.downstreamRealtimeModeByte == DOWN_STREAM_MODE_OFF) {
			this.downstreamRows.clear();
			this.fireOnAmiEntitiesClearedDownstream();
		} else {
			sendAmiRowsDownstream();
		}
	}

	@Override
	public void setDownstreamRealtimeModeOverride(byte mode) {
		if (downstreamRealtimeMode.setOverride(mode))
			onDownstreamRealtimeModeChanged();
	}
	public Table getUnderlyingSelectableRows(byte type) {
		List<Row> rows;
		switch (type) {
			case ALL:
				rows = aggregator.getInnerTable().getRows();
				break;
			case NONE:
				rows = Collections.EMPTY_LIST;
				break;
			case SELECTED:
				IntKeyMap<TreemapNode> selected = treemap.getSelected();
				rows = new ArrayList<Row>();
				if (type == SELECTED)
					for (TreemapNode n : selected.values()) {
						Iterable<Row> inners = aggregator.getUnderlyingRows((Row) n.getCorrelationData());
						for (Row inner : inners)
							rows.add(inner);
					}
				break;
			default:
				throw new RuntimeException("Bad type: " + type);
		}
		List<AmiWebObject> data = new ArrayList<AmiWebObject>(rows.size());
		for (Row i : rows)
			data.add((AmiWebObject) i.get("!data"));
		return AmiWebUtils.toTable(this.getRealtimeObjectschema(), data);
	}

	@Override
	protected void onRowUpdateToTreemap(Row add, TreemapNode node) {
		super.onRowUpdateToTreemap(add, node);
		if (isDownstreamEnabled())
			if (treemap.getSelected().isEmpty() || node.isSelected())
				fireOnAmiEntityUpdatedDownstream(null, getAmiWebObject(add));
	}

	@Override
	protected void onRowAddedToTreemap(Row add, TreemapNode node) {
		super.onRowAddedToTreemap(add, node);
		if (isDownstreamEnabled())
			if (treemap.getSelected().isEmpty()) {
				downstreamRows.add(add);
				fireOnAmiEntityAddedDownstream(getAmiWebObject(add));
			}
	}

	@Override
	protected void onRowRemovedFromTreemap(Row row, TreemapNode node, boolean wasSelected) {
		super.onRowRemovedFromTreemap(row, node, wasSelected);
		if (isDownstreamEnabled())
			if (wasSelected || treemap.getSelected().isEmpty()) {
				downstreamRows.remove(row);
				fireOnAmiEntityRemovedDownstream(getAmiWebObject(row));
			}
	}

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebTreemapRealtimeSettingsPortlet(generateConfig(), this);
	}
	private void fireOnAmiEntitiesClearedDownstream() {
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntitiesReset(this);
			} catch (Exception e) {
				LH.warning(log, "Error clearing Ami Entities for " + OH.getSimpleClassName(i), e);
			}
	}
	private void fireOnAmiEntityAddedDownstream(AmiWebObject entity) {
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntityAdded(this, entity);
			} catch (Exception e) {
				LH.warning(log, "Error adding Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
			}
	}
	private void fireOnAmiEntityRemovedDownstream(AmiWebObject entity) {
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntityRemoved(this, entity);
			} catch (Exception e) {
				LH.warning(log, "Error removing Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
			}
	}
	private void fireOnAmiEntityUpdatedDownstream(AmiWebObjectFields fields, AmiWebObject entity) {
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntityUpdated(this, fields, entity);
			} catch (Exception e) {
				LH.warning(log, "Error removing Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
			}
	}
	private void fireOnSchemaChanged() {
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onSchemaChanged(this, i.SCHEMA_ADDED, null);
			} catch (Exception e) {
				LH.warning(log, "Error updating schema for " + OH.getSimpleClassName(i));
			}
	}
	@Override
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		CH.removeOrThrow(this.dataTypes, oldAri);
		CH.addOrThrow(this.dataTypes, newAri);
	}
	@Override
	protected void onAdnChanged(String old, String adn) {
		super.onAdnChanged(old, adn);
		if (old != null)
			for (AmiWebRealtimeObjectListener i : this.amiListeners) {
				try {
					i.onLowerAriChanged(this, old == null ? null : AmiWebDomObject.ARI_TYPE_PANEL + ":" + old, AmiWebDomObject.ARI_TYPE_PANEL + ":" + adn);
				} catch (Exception e) {
					LH.warning(log, logMe(), " Error updating Ari Entity for ", OH.getSimpleClassName(i), " for : ", old + " ==> ", adn);
				}
			}
	}

	@Override
	public void onSelectionChanged(TreemapPortlet treemapPortlet, IntKeyMap<TreemapNode> selected, boolean userDriven) {
		super.onSelectionChanged(treemapPortlet, selected, userDriven);
		if (isDownstreamEnabled())
			sendAmiRowsDownstream();
	}

	@Override
	public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte status, Map<String, Tuple2<Class, Class>> columns) {
		if (status != SCHEMA_DROPPED)
			this.formulas.recompileAmiscript();
	}

	@Override
	protected boolean rebuildFormulas() {
		boolean r = super.rebuildFormulas();
		if (r)
			fireOnSchemaChanged();
		return r;
	}

}
