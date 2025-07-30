package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils.ParsedWhere;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.IterableAndSize;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableListener;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.IntArrayList;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.impl.FastArrayList;
import com.f1.utils.impl.IdentityHasher;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.DerivedTable;

public class AmiWebObjectTablePortlet extends AmiWebAbstractTablePortlet implements AmiWebRealtimePortlet, TableListener {

	final private LongKeyMap<Row> amiRows = new LongKeyMap<Row>();
	private static final Logger log = LH.get();
	final protected int dataLocation;

	public AmiWebObjectTablePortlet(PortletConfig config) {
		super(config);
		DerivedTable dt = getDerivedTable();
		dt.addTableListener(this);
		if (dt.getColumnIds().contains(AmiConsts.TABLE_PARAM_DATA))
			this.dataLocation = dt.getColumn(AmiConsts.TABLE_PARAM_DATA).getLocation();
		else if (dt.getColumnIds().contains("#params"))
			this.dataLocation = dt.getColumn("#params").getLocation();
		else
			this.dataLocation = -1;
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebObjectTablePortlet> {

		public static final String OLD_ID = "VortexWebAmiObjectTablePortlet";
		public static final String ID = "amirealtimetable";

		public Builder() {
			super(AmiWebObjectTablePortlet.class);
		}

		@Override
		public AmiWebObjectTablePortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebObjectTablePortlet r = new AmiWebObjectTablePortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Realtime Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	private Set<String> dataTypes = new HashSet<String>();

	@Override
	final public Set<String> getLowerRealtimeIds() {
		return dataTypes;
	}

	@Override
	public String getConfigMenuTitle() {
		return "Realtime Table";
	}

	protected void setDataTypes(Set<String> selected) {
		if (selected.equals(dataTypes))
			return;
		Set<String> added = CH.comm(selected, dataTypes, true, false, false);
		Set<String> removed = CH.comm(selected, dataTypes, false, true, false);
		dataTypes.clear();
		dataTypes.addAll(selected);
		this.amiRows.clear();
		this.clearAmiData();
		buildingSnapshot = true;
		AmiWebManagers webManagers = this.getService().getWebManagers();
		if (!isHalted())
			for (AmiWebObject i : webManagers.getAmiObjects(selected))
				addAmiObject(i);
		buildingSnapshot = false;
		onAmiRowsChanged();
		super.updatePanelTypesVar(this.getLowerRealtimeIds());
		for (String s : added)
			webManagers.getAmiObjectsByType(s).addAmiListener(this);
		for (String s : removed)
			webManagers.getAmiObjectsByType(s).removeAmiListener(this);
	}
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		HashSet<String> relative = new HashSet<String>();
		for (String s : this.getLowerRealtimeIds()) {
			s = AmiWebUtils.getRelativeRealtimeId(this.getAmiLayoutFullAlias(), s);
			relative.add(s);
		}
		r.put("rtSources", relative);
		r.put("hah", formatHah(isHaltOnHidden));
		AmiWebUtils.putSkipEmpty(r, "drf", defaultRelationshipFilter);
		r.put("rtDownstreamMode", AmiWebUtils.formatDownstreamMode(this.downstreamRealtimeMode.getValue()));
		return r;
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		Collection types = (Collection) CH.getOr(Caster_Simple.OBJECT, configuration, "amiTypes", null);
		HashSet<String> set = new HashSet<String>();
		if (types != null) {//backwards Compatibility
			for (Object s : types)
				set.add(AmiWebManagers.FEED + s);
		} else {
			Collection<String> types2 = (Collection) CH.getOr(Caster_Simple.OBJECT, configuration, "rtSources", null);
			if (CH.isntEmpty(types2))
				for (String s : types2)
					set.add(AmiWebUtils.getFullRealtimeId(this.getAmiLayoutFullAlias(), s));
		}
		this.dataTypes.addAll(set);
		try {
			super.init(configuration, origToNewIdMapping, sb);
		} finally {
			this.dataTypes.clear();
		}
		setDataTypes(set);
		this.isHaltOnHidden = parseHah(CH.getOr(Caster_String.INSTANCE, configuration, "hah", "false"));
		this.defaultRelationshipFilter = CH.getOr(Caster_String.INSTANCE, configuration, "drf", null);
		if ("false".equals(this.defaultRelationshipFilter))
			getTable().setExternalFilter(AmiWebRealtimeTableCustomFilter.HIDE_ALL);
		this.downstreamRealtimeModeByte = AmiWebUtils.parseDownstreamMode(CH.getOr(Caster_String.INSTANCE, configuration, "rtDownstreamMode", "SELECTED_OR_ALL"));
		this.downstreamRealtimeMode.set(this.downstreamRealtimeModeByte, true);
	}
	public static String formatHah(byte hah) {
		switch (hah) {
			case HAH_FALSE:
				return "false";
			case HAH_TRUE:
				return "true";
			case HAH_UNTIL_VISIBLE:
				return "untilVisible";
			default:
				throw new RuntimeException("Unknown hah: " + hah);
		}
	}
	public static byte parseHah(String hah) {
		if ("false".equals(hah))
			return HAH_FALSE;
		if ("true".equals(hah))
			return HAH_TRUE;
		else if ("untilVisible".equals(hah))
			return HAH_UNTIL_VISIBLE;
		throw new RuntimeException("Unknown HAH option: " + hah);

	}
	//	final public void removeAmiObject(AmiWebObject entity) {
	//		if (isHalted())
	//			return;
	//		Row row = this.amiRows.remove(entity.getUniqueId());
	//		if (row != null)
	//			this.tablePortlet.removeRow(row);
	//	}
	protected void removeAmiObject(AmiWebObject entity) {
		if (isHalted())
			return;
		Row row = this.amiRows.remove(entity.getUniqueId());
		if (row != null)
			this.tablePortlet.removeRow(row);
		else
			LH.warning(log, logMe(), ": Received delete for missing row: ", entity.getTypeName(), "@", entity.getId());
		//		handleDownstreamForRemove(getAmiWebObject(row), row);
	}

	@Override
	final public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {
		if (!isHalted())
			updateAmiObject(entity, fields);
	}

	@Override
	final public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		if (!isHalted())
			removeAmiObject(entity);
	}
	@Override
	final public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		if (!isHalted())
			addAmiObject((AmiWebObject) entity);
	}

	public final boolean isHalted() {
		if (this.tablePortlet.isEditing())
			return true;
		switch (isHaltOnHidden) {
			case HAH_FALSE:
				return false;
			case HAH_TRUE:
				return !getVisible();
			case HAH_UNTIL_VISIBLE:
				return !getVisible() && isFirstTimeVisible;
			default:
				throw new RuntimeException("Unknown hah: " + isHaltOnHidden);
		}
	}

	public static final byte HAH_FALSE = 0;
	public static final byte HAH_TRUE = 1;
	public static final byte HAH_UNTIL_VISIBLE = 2;
	private boolean isStoppedProcessingAmiDataDueToHidden = true;
	private byte isHaltOnHidden = HAH_TRUE;

	private boolean isFirstTimeVisible = true;

	@Override
	protected void onVisibilityChanged(boolean isVisible) {
		super.onVisibilityChanged(isVisible);
		if (isHaltOnHidden == HAH_TRUE) {
			if (!isVisible) {
				isStoppedProcessingAmiDataDueToHidden = true;
				stopProcessingAMiData(true);
			} else if (isStoppedProcessingAmiDataDueToHidden) {
				isStoppedProcessingAmiDataDueToHidden = false;
				this.tablePortlet.getTable().removeMenuListener(this);
				try {
					stopProcessingAMiData(false);
					if (isFirstTimeVisible) {
						this.amiRowsChanged = true;
						this.isFirstTimeVisible = false;
					} else
						this.amiRowsChanged = false;//Fixes bug AMI-14 - we don't want to run relationships.
				} finally {
					this.tablePortlet.getTable().addMenuListener(this);
				}
			}
		} else if (isHaltOnHidden == HAH_UNTIL_VISIBLE) {
			if (!isVisible) {
				if (isFirstTimeVisible) {
					isStoppedProcessingAmiDataDueToHidden = true;
					stopProcessingAMiData(true);
				}
			} else if (isStoppedProcessingAmiDataDueToHidden) {
				isStoppedProcessingAmiDataDueToHidden = false;
				this.tablePortlet.getTable().removeMenuListener(this);
				try {
					stopProcessingAMiData(false);
					if (isFirstTimeVisible) {
						this.amiRowsChanged = true;
						this.isFirstTimeVisible = false;
					} else
						this.amiRowsChanged = false;//Fixes bug AMI-14 - we don't want to run relationships.
				} finally {
					this.tablePortlet.getTable().addMenuListener(this);
				}
			}
		}
	}
	protected void clearCachedDataDueToHidden() {
		selectedRowsBeforeHidden.clear();
	}

	private static final Caster<AmiWebObject_Wrapper> AMI_WEB_OBJECT_CASTER = OH.getCaster(AmiWebObject_Wrapper.class);
	private LongSet selectedRowsBeforeHidden = new LongSet();

	public void stopProcessingAMiData(boolean pause) {
		if (pause) {
			selectedRowsBeforeHidden.clear();
			Row ar = getTable().getActiveRow();
			if (ar != null)
				selectedRowsBeforeHidden.add(ar.getAt(2, AMI_WEB_OBJECT_CASTER).getInnerUniqueId());
			for (Row row : getTable().getSelectedRows())
				if (row != ar)
					selectedRowsBeforeHidden.add(row.getAt(2, AMI_WEB_OBJECT_CASTER).getInnerUniqueId());
			clearAmiData();
		} else {
			rebuildAmiData();
			// pass rows downstream: fixes agg panel not calculating when switching to a tab with an agg panel while rt table is visible
			IterableAndSize<AmiWebObject> values = getService().getWebManagers().getAmiObjects(this.getLowerRealtimeIds());
			if (values.size() > 0) {
				for (String s : this.getLowerRealtimeIds()) {
					handleSnapshotProcessed(getService().getWebManagers().getAmiObjectsByType(s), values);
				}
			}
			if (selectedRowsBeforeHidden.size() > 0) {
				IntArrayList uids = new IntArrayList(selectedRowsBeforeHidden.size());
				for (Row r : amiRows.values()) {
					if (selectedRowsBeforeHidden.contains(r.getAt(2, AMI_WEB_OBJECT_CASTER).getInnerUniqueId())) {
						int loc = r.getLocation();
						if (loc != -1)
							uids.add(loc);
					}
				}
				getTable().setSelectedRows(uids.toIntArray());
				selectedRowsBeforeHidden.clear();
			}
		}
	}

	protected void updateAmiObject(AmiWebObject entity, AmiWebObjectFields changes) {
		if (!super.meetsWhereFilter(entity)) {
			Row existing = amiRows.remove(entity.getUniqueId());
			if (existing != null) {
				this.tablePortlet.removeRow(existing);
			}
			return;
		}
		Row existing = amiRows.get(entity.getUniqueId());
		if (existing == null) {
			if (!hasWhereFilter())//if there is a where filter, it's probably just an update made it become visible so do not log warning
				LH.warning(log, logMe(), ": Received update for missing row: ", entity.getTypeName(), "@", entity.getId());
			addAmiObject(entity);
			return;
		}
		SmartTable tbl = getTable().getTable();
		if (entity instanceof AmiWebObject_Feed) {
			AmiWebObject_Feed ef = (AmiWebObject_Feed) entity;
			existing.put("M", ef.getModifiedOn());
			existing.put("V", ef.getRevision());
			existing.put("C", ef.getCreatedOn());
			populateBaseFields(existing, ef);
		}
		if (changes != null) {
			for (int i = 0, n = changes.getChangesCount(); i < n; i++) {
				String key = changes.getChangeField(i);
				Column col = getVariableColumn(key);
				if (col != null) {
					int location = col.getLocation();
					Object value = entity.get(key);
					try {
						value = AmiWebUtils.castNoThrow(value, tbl.getColumnAt(location).getTypeCaster());
					} catch (Exception e2) {
						LH.warning(log, logMe(), ": Error casting data: ", value, e2);
						value = null;
					}
					existing.putAt(location, value);
				}
			}
		} else {
			for (Column e : getVariableColumns()) {
				int location = e.getLocation();
				Object value = entity.get(e.getId());
				try {
					value = AmiWebUtils.castNoThrow(value, e.getTypeCaster());
				} catch (Exception e2) {
					LH.warning(log, logMe(), ": Error casting data: ", value, e2);
					value = null;
				}

				existing.putAt(location, value);
			}
		}
		if (!getTable().hasSelectedRows() || getTable().isSelected(existing))
			onAmiRowsChanged();
		//		handleDownstreamForUpdate(getAmiWebObject(existing), existing, changes);
	}
	protected void addAmiObject(AmiWebObject entity) {
		if (!super.meetsWhereFilter(entity)) {
			Row row = amiRows.get(entity.getUniqueId());
			if (row != null)
				this.tablePortlet.removeRow(row);
			return;
		}
		Row existing = amiRows.get(entity.getUniqueId());
		if (existing != null) {
			LH.w(log, logMe(), ": Duplicate entry: " + entity + " vs " + existing);
			updateAmiObject(entity, null);
			return;
		}

		SmartTable tbl = getTable().getTable();
		Row nuw;
		Object[] row = new Object[tbl.getColumnsCount()];
		row[2] = new AmiWebObject_Wrapper(entity, getService().getNextAmiObjectUId());
		row[4] = MH.roundBy(getManager().getNow(), getCurrentTimeUpdateFrequencyMs(), MH.ROUND_DOWN);
		if (entity instanceof AmiWebObject_Feed) {
			AmiWebObject_Feed ef = (AmiWebObject_Feed) entity;
			row[0] = ef.getCenterName();
			row[1] = ef.getIdBoxed();
			row[3] = ef.getModifiedOn();
			row[5] = ef.getCreatedOn();
			row[6] = ef.getRevision();
			populateBaseFields(row, 7, ef);
		} else
			row[1] = entity.getId();

		for (Column e : getVariableColumns()) {
			int location = e.getLocation();
			Object value;
			value = AmiWebUtils.castNoThrow(entity.get(e.getId()), e.getTypeCaster());
			row[location] = value;
		}
		nuw = this.tablePortlet.addRow(row);
		amiRows.put(entity.getUniqueId(), nuw);
	}

	private void handleDownstreamForAdd(AmiWebObject entity, Row row) {
		if (entity == null)
			LH.warning(log, logMe(), ": Missing entity for: ", row);
		else if (isDownstreamEnabled()) {
			if (!this.getTable().hasSelectedRows() || this.getTable().isSelected(row)) {
				downstreamRows.add(row);
				this.fireOnAmiEntityAddedDownstream(entity);
			}
		}
	}
	protected void handleDownstreamForUpdate(AmiWebObject entity, Row row, AmiWebObjectFields fields) {
		if (entity == null)
			LH.warning(log, logMe(), ": Missing entity for: ", row);
		else if (isDownstreamEnabled()) {
			if (this.downstreamRows.contains(row))
				this.fireOnAmiEntityUpdatedDownstream(fields, entity);
		}
	}
	private void handleDownstreamForRemove(AmiWebObject entity, Row row) {
		if (entity == null)
			LH.warning(log, logMe(), ": Missing entity for: ", row);
		else if (isDownstreamEnabled())
			if (this.downstreamRows.remove(row))
				this.fireOnAmiEntityRemovedDownstream(entity);
	}
	final void populateBaseFields(Row existing, AmiWebObject_Feed data) {
		existing.put("E", data.getExpiresInMillis());
	}

	final void populateBaseFields(Object[] row, int offset, AmiWebObject_Feed data) {
		row[offset + 0] = data.getTypeName();
		row[offset + 1] = data.getObjectId();
		row[offset + 2] = data.getExpiresInMillis();
		row[offset + 3] = data.getAmiApplicationIdName();
	}
	@Override
	public void onClosed() {
		try {
			for (String s : this.getLowerRealtimeIds()) {
				AmiWebRealtimeObjectManager t = getService().getWebManagers().getAmiObjectsByType(s);
				if (t != null)
					t.removeAmiListener(this);
			}
		} catch (Exception e) {
			LH.w(log, logMe(), " Error closing: " + e);
		}
		super.onClosed();
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		super.populateConfigMenu(headMenu);
	}

	@Override
	public boolean onAmiContextMenu(String action) {
		return super.onAmiContextMenu(action);
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		updateChildTables(false);
	}

	@Override
	public boolean addCustomColumn(AmiWebCustomColumn col, StringBuilder errorSink, int columnLocation, AmiWebCustomColumn replacing, com.f1.base.CalcTypes varTypes,
			boolean populateValues) {
		boolean r = super.addCustomColumn(col, errorSink, columnLocation, replacing, varTypes, populateValues);
		if (populateValues && r) {
			for (AmiWebObject i : this.getService().getWebManagers().getAmiObjects(this.getLowerRealtimeIds()))
				addAmiObject(i);
		}
		return r;
	}

	@Override
	public void clearRows() {

		if (this.isDownstreamEnabled()) {
			this.downstreamRows.clear();
			this.fireOnAmiEntitiesClearedDownstream();
		}
		this.amiRows.clear();
		super.clearRows();
	}

	@Override
	protected void rebuildAmiData() {
		IterableAndSize<AmiWebObject> values = getService().getWebManagers().getAmiObjects(this.getLowerRealtimeIds());
		this.amiRows.ensureCapacity(values.size());
		boolean ks = this.getTable().isKeepSorting();
		DerivedTable dt = getDerivedTable();
		dt.removeTableListener(this);
		long now1 = System.nanoTime();
		if (ks)
			getTable().pauseSort(true);
		for (AmiWebObject obj : values) {
			// adds to amiRow only
			onAmiEntityAdded(this, obj);
		}
		if (ks)
			getTable().pauseSort(false);
		dt.addTableListener(this);
		if (log.isLoggable(Level.FINE)) {
			long now2 = System.nanoTime();
			LH.fine(log, logMe(), ": rebuilt ", this.getAri(), ": ", values.size(), " row(s) in ", (now2 - now1), " nanos");
		}
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		super.onContextMenu(table, action);
	}

	protected AmiWebRealtimeTableCustomFilter customFilter = null;

	@Override
	public void processFilter(AmiWebDmLink link, Table table) {
		this.currentFilter = link;
		if (AmiWebRealtimeTableCustomFilter.getWhereClause(link) != null) {
			if (table == null) {
				getTable().setExternalFilter(null);
				getTable().setExternalFilterIndex(null, null);
				return;
			} else if (table.getSize() == 0) {
				if (getTable().getExternalFilter() != AmiWebRealtimeTableCustomFilter.HIDE_ALL)
					getTable().setExternalFilter(AmiWebRealtimeTableCustomFilter.HIDE_ALL);
				getTable().setExternalFilterIndex(null, null);
				return;
			}
			if (customFilter == null)
				this.customFilter = new AmiWebRealtimeTableCustomFilter(getService(), this.getAmiLayoutFullAlias());
			byte resetCode = customFilter.reset(link, table, getTable().getTable().getColumnTypesMapping(), getTable());
			if (this.customFilter != getTable().getExternalFilter()) {
				getTable().setExternalFilter(this.customFilter);
				getTable().setExternalFilterIndex(this.customFilter.getIndex(), this.customFilter.getValuesForIndex());
				onAmiRowsChanged();
			} else if (resetCode == AmiWebRealtimeTableCustomFilter.RESET_INDEX) {
				getTable().setExternalFilterIndex(this.customFilter.getIndex(), this.customFilter.getValuesForIndex());
				onAmiRowsChanged();
			} else if (resetCode == AmiWebRealtimeTableCustomFilter.RESET_INDEX_AND_FILTER) {
				getTable().setExternalFilter(this.customFilter);
				getTable().setExternalFilterIndex(this.customFilter.getIndex(), this.customFilter.getValuesForIndex());
				onAmiRowsChanged();
			}
		} else if (getTable().getExternalFilter() != null || CH.isntEmpty(getTable().getTable().getExternalFilterIndexValues())) {
			getTable().setExternalFilter(null);
			getTable().setExternalFilterIndex(null, null);
			onAmiRowsChanged();
		}
	}
	@Override
	public com.f1.base.CalcTypes getLinkableVars() {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (String column : getTable().getColumnIds()) {
			WebColumn col = getTable().getColumn(column);
			String[] columns = col.getTableColumns();
			r.putType(col.getColumnName(), getTable().getTable().getColumn(columns[0]).getType());
		}
		return r;
	}

	@Override
	public void onLinkingChanged(AmiWebDmLink link) {
		if (this.customFilter != null && this.getDmLinksToThisPortlet().isEmpty()) {
			getTable().setExternalFilter(null);
			getTable().setExternalFilterIndex(null, null);
			this.customFilter = null;
		}

		super.onLinkingChanged(link);
	}
	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		switch (type) {
			case NONE:
				return getValuesForLink(Collections.EMPTY_LIST);
			case SELECTED:
				return getValuesForLink(getTable().getSelectedRows());
			case ALL:
				return getValuesForLink(null);
			default:
				throw new RuntimeException("unknown type: " + type);
		}
	}
	@Override
	protected Table getValuesForLink(List<Row> sel) {
		int cnt = getTable().getColumnIds().size();
		int[] positions = new int[cnt];
		Class[] types = new Class[cnt];
		String[] names = new String[cnt];
		int pos = 0;
		SmartTable t = getTable().getTable();
		for (String column : getTable().getColumnIds()) {
			WebColumn col = getTable().getColumn(column);
			String id = col.getTableColumns()[0];
			Class<?> type = t.getColumn(id).getType();
			if (type == Object.class)
				continue;
			positions[pos] = t.getColumn(id).getLocation();
			names[pos] = col.getColumnName();
			types[pos] = type;
			pos++;
		}
		if (pos < names.length) {
			names = Arrays.copyOf(names, pos);
			positions = Arrays.copyOf(positions, pos);
			types = Arrays.copyOf(types, pos);
		}
		BasicTable values = new BasicTable(types, names);
		if (sel == null)
			sel = t.getRows();
		for (Row row : sel) {
			Row row2 = values.newEmptyRow();
			for (int i = 0; i < positions.length; i++)
				row2.putAt(i, row.getAt(positions[i]));
			values.getRows().add(row2);
		}
		return values;
	}
	protected void onUserChangingColumnSchema() {
		getTable().setExternalFilter(null);
		getTable().getTable().setExternalFilterIndex(null, null);
		this.customFilter = null;
	}

	public byte getIsHaltOnHidden() {
		return isHaltOnHidden;
	}

	public void setIsHaltOnHidden(byte shouldHaltOnHidden) {
		this.isHaltOnHidden = shouldHaltOnHidden;
	}

	private AmiWebDmLink currentFilter;
	private String defaultRelationshipFilter;

	@Override
	public AmiWebDmLink getCurrentLinkFilteringThis() {
		return this.currentFilter;
	}

	public void handleSnapshotProcessed(AmiWebRealtimeObjectManager manager, Iterable<AmiWebObject> entities) {
		for (AmiWebObject entity : entities) {
			handleDownstreamForAdd(entity, amiRows.get(entity.getUniqueId()));
		}
	}

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
		amiRows.clear();
		clearAmiData();
		clearCachedDataDueToHidden();
		rebuildAmiData();
	}

	@Override
	protected void onEditFinished() {
		if (!isHalted()) {
			this.clearAmiData();
			this.rebuildAmiData();
		}
	}

	@Override
	protected void onWhereFormulaChanged() {
		this.filterChanged = true;
		this.clearAmiData();
		this.rebuildAmiData();
	}

	@Override
	public void onEditCell(int x, int y, String v) {
	}

	@Override
	public com.f1.base.CalcTypes getUnderlyingVarTypes() {
		return AmiWebUtils.getAvailableVariables(getService(), this);
		//		com.f1.utils.BasicTypes r = null;
		//		boolean hasLegacy = false;
		//		for (String s : this.getLowerRealtimeIds()) {
		//			com.f1.utils.BasicTypes t = this.getService().getSystemObjectsManager().getTableSchema(s);
		//			if (t == null)
		//				hasLegacy = true;
		//			else if (r == null)
		//				r = new HasherMap((Map) t);
		//			else
		//				for (Entry<String, Class<?>> e : t.entrySet())
		//					r.put(e.getKey(), OH.getWidestIgnoreNull(r.get(e.getKey()), e.getValue()));
		//		}
		//		if (r == null)
		//			return this.getUsedVariables();
		//		else if (hasLegacy)
		//			for (MappingEntry<String, Class<?>> e : this.getUserDefinedVariables().entries())
		//				r.put(e.getKey(), OH.getWidestIgnoreNull(r.get(e.getKey()), e.getValue()));
		//		return r;
	}

	public void setDefaultRelationshipFilter(String value) {
		this.defaultRelationshipFilter = value;
	}
	public String getDefaultRelationshipFilter() {
		return this.defaultRelationshipFilter;
	}
	public boolean checkCanRemoveCustomColumnById(String colId) {
		AmiWebCustomColumn col = getCustomDisplayColumn(colId);
		StringBuilder tmp = new StringBuilder();
		{
			Set<Object> sink2 = new HashSet<Object>();
			for (AmiWebDmLink i : getDmLinksFromThisPortlet()) {
				com.f1.base.CalcTypes target = AmiWebDmUtils.getTarget(getService(), i);
				for (String varname : i.getWhereClauseVarNames()) {
					ParsedWhere calc = new AmiWebDmUtils.ParsedWhere(getService(), target, i, varname, getService().getDebugManager(), tmp);
					DerivedHelper.getDependencyIds(calc.calc, sink2);
				}
			}
			for (Object o : sink2) {
				String s = (String) o;
				if (SH.startsWith(s, AmiWebDmUtils.VARPREFIX_SOURCE))
					if (col.getTitle(false).equals(SH.stripPrefix(s, AmiWebDmUtils.VARPREFIX_SOURCE, true))) {
						getManager().showAlert("This column is participating in a relationship to another panel so it can not be removed");
						return false;
					}
			}
		}
		{
			Set<Object> sink2 = new HashSet<Object>();
			for (AmiWebDmLink i : getDmLinksToThisPortlet()) {
				com.f1.base.CalcTypes target = AmiWebDmUtils.getTarget(getService(), i);
				for (String varname : i.getWhereClauseVarNames()) {
					ParsedWhere calc = new AmiWebDmUtils.ParsedWhere(getService(), target, i, varname, getService().getDebugManager(), tmp);
					DerivedHelper.getDependencyIds(calc.calc, sink2);
				}
			}
			for (Object o : sink2) {
				String s = (String) o;
				if (SH.startsWith(s, AmiWebDmUtils.VARPREFIX_TARGET))
					if (col.getTitle(false).equals(SH.stripPrefix(s, AmiWebDmUtils.VARPREFIX_TARGET, true))) {
						getManager().showAlert("This column is participating in a relationship from another panel so it can not be removed");
						return false;
					}
			}
		}
		return super.checkCanRemoveCustomColumnById(colId);
	}

	@Override
	public IterableAndSize<AmiWebObject> getAmiObjects() {
		if (this.amiListeners.length == 0) {
			List<Row> selected = getTable().getSelectedRows();
			Object[] array = CH.isEmpty(selected) ? getTable().getRows().toArray() : selected.toArray();
			for (int i = 0; i < array.length; i++)
				array[i] = getAmiWebObject((Row) array[i]);
			return new FastArrayList<AmiWebObject>(array);
		} else {
			Object[] array = downstreamRows.toArray();
			for (int i = 0; i < array.length; i++)
				array[i] = getAmiWebObject((Row) array[i]);
			return new FastArrayList<AmiWebObject>(array);
		}
	}

	protected AmiWebObject getAmiWebObject(Row row) {
		return (AmiWebObject) row.getAt(2);
	}

	private AmiWebRealtimeObjectListener[] amiListeners = AmiWebRealtimeObjectListener.EMPTY_ARRAY;
	private HasherSet<Row> downstreamRows = new HasherSet<Row>(IdentityHasher.INSTANCE);
	private Set<String> upperRealtimeIds = new HashSet<String>();

	@Override
	public boolean removeAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i == -1)
			return false;
		this.amiListeners = AH.remove(this.amiListeners, i);
		if (!isDownstreamEnabled())
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
		if (isDownstreamEnabled() && this.amiListeners.length == 1) {
			final IdentityHashSet<Row> selectedRows = new IdentityHashSet<Row>(this.getTable().getSelectedRows());
			if (selectedRows.isEmpty())
				this.downstreamRows.addAll(getTable().getRows());
			else
				this.downstreamRows.addAll(selectedRows);
		}
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

	@Override
	protected void handleOnSelectedChanged() {
		super.handleOnSelectedChanged();
		sendAmiRowsDownstream();
	}

	private void sendAmiRowsDownstream() {
		if (isDownstreamEnabled()) {
			final List<AmiWebObject> add = new ArrayList<AmiWebObject>();
			final IdentityHashSet<Row> selectedRows = new IdentityHashSet<Row>(this.getTable().getSelectedRows());
			if (selectedRows.isEmpty()) {
				this.downstreamRows.clear();
				this.downstreamRows.addAll(getTable().getRows());
				this.fireOnAmiEntitiesClearedDownstream();
				return;
			}

			if (this.downstreamRows.size() - selectedRows.size() > 1000) {//deleting over 1000 records, lets just clear and rebuild
				this.downstreamRows.clear();
				this.downstreamRows.addAll(selectedRows);
				this.fireOnAmiEntitiesClearedDownstream();
			} else {
				final List<AmiWebObject> del = new ArrayList<AmiWebObject>();
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

	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
		if (filterChanged) {
			filterChanged = false;
			if (isDownstreamEnabled()) {
				handleOnSelectedChanged();
				return;
			}
		}
		super.onNoSelectedChanged(fastWebTable);
	}

	private boolean filterChanged = false;

	@Override
	public void onFilterChanging(WebTable fastWebTable) {
		this.filterChanged = true;
		super.onFilterChanging(fastWebTable);
		if (isDownstreamEnabled())
			handleOnSelectedChanged();
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
		return getRealtimeObjectschema();
	}

	@Override
	public String getRealtimeId() {
		return this.getAri();
	}

	@Override
	public Set<String> getUpperRealtimeIds() {
		return AmiWebUtils.updateRealtimeIds(this.amiListeners, this.upperRealtimeIds);
	}

	private final AmiWebObjectFieldsImpl tmpFields = new AmiWebObjectFieldsImpl();

	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		if (!isDownstreamEnabled())
			return;
		if (cell == 2)
			return;
		tmpFields.clear();
		tmpFields.addChange((String) row.getTable().getColumnAt(cell).getId(), oldValue);
		handleDownstreamForUpdate((AmiWebObject) row.getAt(dataLocation), row, tmpFields);
		//		handleDownstreamForUpdate((AmiWebObject) row.getAt(dataLocation), row, null);//tmpFields);
	}

	@Override
	public void onColumnAdded(Column nuw) {
	}

	@Override
	public void onColumnRemoved(Column old) {
	}

	@Override
	public void onColumnChanged(Column old, Column nuw) {
	}

	@Override
	public void onRowAdded(Row add) {
		if (!isDownstreamEnabled())
			return;
		handleDownstreamForAdd((AmiWebObject) add.getAt(dataLocation), add);
	}

	@Override
	public void onRowRemoved(Row removed, int index) {
		if (!isDownstreamEnabled())
			return;
		handleDownstreamForRemove((AmiWebObject) removed.getAt(dataLocation), removed);
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

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebRealTimeTableSettingsPortlet(generateConfig(), this);
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
				LH.warning(log, "Error updating Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
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
					i.onLowerAriChanged(this, AmiWebDomObject.ARI_TYPE_PANEL + ":" + old, AmiWebDomObject.ARI_TYPE_PANEL + ":" + adn);
				} catch (Exception e) {
					LH.warning(log, logMe(), " Error updating Ari Entity for ", OH.getSimpleClassName(i), " for : ", old + " ==> ", adn, e);
				}
			}
	}

	@Override
	public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte status, Map<String, Tuple2<Class, Class>> columns) {
	}

}
