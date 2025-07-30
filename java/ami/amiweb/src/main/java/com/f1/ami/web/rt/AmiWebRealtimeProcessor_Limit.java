package com.f1.ami.web.rt;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebAbstractRealtimeProcessor;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebObjectFields;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.IterableAndSize;
import com.f1.base.IterableAndSizeWrapper;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.CompactLongKeyMap;
import com.f1.utils.structs.CompactLongKeyMap.KeyGetter;
import com.f1.utils.structs.SkipList;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiWebRealtimeProcessor_Limit extends AmiWebAbstractRealtimeProcessor
		implements KeyGetter<AmiWebObject_WrapperSkipListEntry>, Comparator<AmiWebObject_WrapperSkipListEntry> {

	final private AmiWebOverrideValue<Integer> offset = new AmiWebOverrideValue<Integer>(0);
	final private AmiWebOverrideValue<Integer> count = new AmiWebOverrideValue<Integer>(0);
	final private AmiWebOverrideValue<Boolean> ascending = new AmiWebOverrideValue<Boolean>(Boolean.TRUE);
	private String lowerId;
	private AmiWebRealtimeObjectManager lower;
	private Set<String> lowerIdSet;
	private CompactLongKeyMap<AmiWebObject_WrapperSkipListEntry> rows = new CompactLongKeyMap<AmiWebObject_WrapperSkipListEntry>("", this, 1024);
	private AmiWebFormula orderByFormula;
	private DerivedCellCalculator orderByCalc;

	private SkipList<AmiWebObject_WrapperSkipListEntry> rowsOrdered = new SkipList<AmiWebObject_WrapperSkipListEntry>(1024);
	private boolean attached;

	public AmiWebRealtimeProcessor_Limit(AmiWebService service) {
		super(service);
		this.orderByFormula = this.getFormulasImpl().addFormula("orderBy", Comparable.class);
	}
	public AmiWebRealtimeProcessor_Limit(AmiWebService service, String alias) {
		super(service, alias);
		this.orderByFormula = this.getFormulasImpl().addFormula("orderBy", Comparable.class);
	}

	public void setLowerId(String id) {
		if (OH.eq(this.lowerId, id))
			return;
		if (attached) {
			this.lower.removeAmiListener(this);
			this.attached = false;
		}
		this.lowerId = id;
		this.lower = this.getService().getWebManagers().getAmiObjectsByType(lowerId);
		this.lowerIdSet = Collections.singleton(this.lowerId);
	}

	@Override
	public String getType() {
		return AmiWebRealtimeProcessorPlugin_Limit.PLUGIN_ID;
	}

	@Override
	public Set<String> getLowerRealtimeIds() {
		return lowerIdSet;
	}

	@Override
	public IterableAndSize<AmiWebObject> getAmiObjects() {
		int count = this.count.getValue(true);
		int offset = this.offset.getValue(true);
		int size = this.rowsOrdered.size();
		if (offset == 0 && size < count)
			return new IterableAndSizeWrapper(this.rowsOrdered);
		else
			return new IterableAndSizeWrapper(this.rowsOrdered.subList(Math.min(size, offset), Math.min(size, offset + count)));
	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectschema() {
		return this.lower.getRealtimeObjectsOutputSchema();
	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectsOutputSchema() {
		return this.lower.getRealtimeObjectsOutputSchema();
	}

	@Override
	public void rebuild() {
		if (!attached) {
			this.lower.addAmiListener(this);
			this.attached = true;
		}
		this.rows.clear();
		this.rowsOrdered.clear();
		AmiWebService service = this.getService();
		for (AmiWebObject i : this.lower.getAmiObjects()) {
			Comparable key = getOrderBy(i);
			AmiWebObject_WrapperSkipListEntry nuw = new AmiWebObject_WrapperSkipListEntry(i, service.getNextAmiObjectUId(), key);
			CH.insertSorted(this.rowsOrdered, nuw, this, false);
			this.rows.put(nuw);
		}
		fireOnAmiEntitiesCleared();
	}

	private Comparable getOrderBy(AmiWebObject i) {
		if (this.orderByCalc == null)
			return i.getUniqueId();
		return (Comparable) this.orderByCalc.get(getStackFrame().reset(i));
	}

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
		rebuild();
	}

	@Override
	public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		AmiWebService service = this.getService();
		Comparable key = getOrderBy(entity);
		AmiWebObject_WrapperSkipListEntry nuw = new AmiWebObject_WrapperSkipListEntry(entity, service.getNextAmiObjectUId(), key);
		this.rows.put(nuw);
		CH.insertSorted(this.rowsOrdered, nuw, this, false);
		int count = this.count.getValue(true);
		int start = this.offset.getValue(true);
		int end = start + count;
		int location = nuw.getLocation();
		if (location < start) {
			if (this.rowsOrdered.size() > start)
				fireAmiEntityAdded(this.rowsOrdered.get(start));
			if (this.rowsOrdered.size() > end)
				fireAmiEntityRemoved(this.rowsOrdered.get(end));
		} else if (location < end) {
			fireAmiEntityAdded(nuw);
			if (this.rowsOrdered.size() > end)
				fireAmiEntityRemoved(this.rowsOrdered.get(end));
		}
	}

	@Override
	public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {
		AmiWebObject_WrapperSkipListEntry updated = this.rows.get(entity.getUniqueId());
		Comparable key = getOrderBy(entity);
		int count = this.count.getValue(true);
		int start = this.offset.getValue(true);
		int end = start + count;
		if (compare(key, updated.getKey()) != 0) {
			int oldLoc = updated.getLocation();
			this.rowsOrdered.remove(updated.getLocation());
			updated.setKey(key);
			CH.insertSorted(this.rowsOrdered, updated, this, false);
			int nuwLoc = updated.getLocation();
			int wasVis = oldLoc < start ? -1 : oldLoc < end ? 0 : 1;
			int isVis = nuwLoc < start ? -1 : nuwLoc < end ? 0 : 1;
			if (wasVis == -1) {
				if (isVis == -1) {//before -> before
					return;
				} else if (isVis == 0) {//before -> visible 
					fireAmiEntityRemoved(this.rowsOrdered.get(start - 1));
					fireAmiEntityAdded(updated);
				} else {//before -> after 
					fireAmiEntityRemoved(this.rowsOrdered.get(start - 1));
					fireAmiEntityAdded(this.rowsOrdered.get(end - 1));
				}
			} else if (wasVis == 0) {
				if (isVis == -1) {//visible -> before 
					fireAmiEntityRemoved(updated);
					fireAmiEntityAdded(this.rowsOrdered.get(start));
				} else if (isVis == 0) {//visible -> visible 
					fireAmiEntityUpdated(fields, updated);
				} else {//visible -> after 
					fireAmiEntityRemoved(updated);
					fireAmiEntityAdded(this.rowsOrdered.get(end - 1));
				}
			} else {
				if (isVis == -1) {//after -> before 
					fireAmiEntityRemoved(this.rowsOrdered.get(end));
					fireAmiEntityAdded(this.rowsOrdered.get(start));
				} else if (isVis == 0) {//after -> visible
					fireAmiEntityRemoved(this.rowsOrdered.get(end));
					fireAmiEntityAdded(updated);
				} else {//after -> after
					return;
				}
			}
		} else {
			if (updated.getLocation() >= start && updated.getLocation() < end)
				fireAmiEntityUpdated(fields, updated);
		}

	}

	@Override
	public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		AmiWebObject_WrapperSkipListEntry removed = this.rows.remove(entity.getUniqueId());
		int location = removed.getLocation();
		this.rowsOrdered.remove(location);
		int count = this.count.getValue(true);
		int start = this.offset.getValue(true);
		int end = start + count;
		if (location < start) {
			fireAmiEntityRemoved(this.rowsOrdered.get(start - 1));
			if (this.rowsOrdered.size() >= end)
				fireAmiEntityAdded(this.rowsOrdered.get(end - 1));
		} else if (location < end) {
			fireAmiEntityRemoved(removed);
			if (this.rowsOrdered.size() >= end)
				fireAmiEntityAdded(this.rowsOrdered.get(end - 1));
		}
	}

	@Override
	public void init(String alias, Map<String, Object> configuration) {
		super.init(alias, configuration);
		this.offset.setValue(CH.getOrThrow(Caster_Integer.INSTANCE, configuration, "offset"), false);
		this.ascending.setValue(CH.getOr(Caster_Boolean.INSTANCE, configuration, "ascending", true), false);
		this.count.setValue(CH.getOrThrow(Caster_Integer.INSTANCE, configuration, "count"), false);
		String id = CH.getOrThrow(Caster_String.INSTANCE, configuration, "lowerId");
		setLowerId(AmiWebUtils.getFullRealtimeId(alias, id));
		setOrderBy(CH.getOrThrow(Caster_String.INSTANCE, configuration, "orderBy"), false);
		this.lower.addAmiListener(this);
		this.attached = true;
	}

	public Exception testOrderBy(String orderBy) {
		return this.orderByFormula.testFormula(orderBy);
	}
	public void setOrderBy(String orderBy, boolean b) {
		this.orderByFormula.setFormula(orderBy, b);
		this.orderByCalc = this.orderByFormula.getFormulaCalc();
	}
	public void setCount(int count, boolean override) {
		this.count.setValue(count, override);
	}
	public void setOffset(int offset, boolean override) {
		this.offset.setValue(offset, override);
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("orderBy", this.orderByFormula.getFormula(false));
		r.put("offset", this.offset.getValue(false));
		r.put("ascending", this.ascending.getValue(false));
		r.put("count", this.count.getValue(false));
		r.put("lowerId", AmiWebUtils.getRelativeRealtimeId(getAlias(), this.lowerId));
		return r;
	}

	@Override
	public long getKey(AmiWebObject_WrapperSkipListEntry object) {
		return object.getInnerUniqueId();
	}

	@Override
	public int compare(AmiWebObject_WrapperSkipListEntry o1, AmiWebObject_WrapperSkipListEntry o2) {
		Comparable n1 = o1.getKey();
		Comparable n2 = o2.getKey();
		return compare(n1, n2);
	}

	private int compare(Comparable n1, Comparable n2) {
		if (this.orderByCalc != null && this.orderByCalc.getReturnType() == String.class)
			return ascending.getValue(true) ? SH.COMPARATOR_CASEINSENSITIVE_STRING.compare((String) n1, (String) n2)
					: SH.COMPARATOR_CASEINSENSITIVE_STRING_REVERSE.compare((String) n1, (String) n2);
		return ascending.getValue(true) ? OH.compare(n1, n2) : OH.compare(n2, n1);
	}
	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.getRealtimeObjectsOutputSchema();
	}

	public String getOrderBy(boolean override) {
		return this.orderByFormula.getFormula(override);
	}

	public int getCount(boolean override) {
		return this.count.getValue(override);
	}
	public int getOffset(boolean override) {
		return this.offset.getValue(override);
	}
	@Override
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		if (this.lowerId != null && oldAri != null)
			OH.assertEq(this.lowerId, oldAri);
		this.lowerId = newAri;
		this.lowerIdSet = Collections.singleton(this.lowerId);
	}

	public boolean getAscending(boolean override) {
		return this.ascending.getValue(override);
	}
	public void setAscending(boolean value, boolean override) {
		this.ascending.setValue(value, override);
	}
}
