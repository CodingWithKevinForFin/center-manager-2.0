package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.IterableAndSize;
import com.f1.utils.AH;
import com.f1.utils.IterableAndSizeIterator;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebObjects_Union implements AmiWebRealtimeObjectManager {
	final private static Logger log = LH.get();

	private AmiWebRealtimeObjectListener[] amiListeners = AmiWebRealtimeObjectListener.EMPTY_ARRAY;
	private ArrayList<AmiWebRealtimeObjectManager> inners = new ArrayList<AmiWebRealtimeObjectManager>();
	private Listener listener = new Listener();

	private String realtimeId;
	private Set<String> upperRealtimeIds = new HashSet<String>();
	private AmiWebManagers manager;

	public AmiWebObjects_Union(AmiWebManagers manager, String realtimeId) {
		this.manager = manager;
		this.realtimeId = realtimeId;
	}

	public void addInner(AmiWebRealtimeObjectManager manager) {
		this.inners.add(manager);
		manager.addAmiListener(listener);
		for (AmiWebRealtimeObjectListener i : this.amiListeners)
			i.onAmiEntitiesReset(this);
	}

	@Override
	public boolean removeAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i == -1)
			return false;
		this.amiListeners = AH.remove(this.amiListeners, i);
		manager.onListenerRemoved(this, listener);
		LH.fine(log, "ObjectsUnion Remove Ami Listener: ", SH.toObjectStringSimple(listener), " Count: ", AH.length(this.amiListeners));

		return true;
	}

	@Override
	public boolean addAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i != -1)
			return false;
		this.amiListeners = AH.append(this.amiListeners, listener);
		manager.onListenerAdded(this, listener);
		LH.fine(log, "ObjectsUnion Add Ami Listener: ", SH.toObjectStringSimple(listener), " Count: ", AH.length(this.amiListeners));
		return true;
	}

	@Override
	public boolean hasAmiListeners() {
		return this.amiListeners.length > 0;
	}
	//	@Override
	//	public List<AmiWebRealtimeObjectListener> getListeners() {
	//		return this.listeners;
	//	}

	@Override
	public IterableAndSize<AmiWebObject> getAmiObjects() {
		IterableAndSize<AmiWebObject>[] a = new IterableAndSize[this.inners.size()];
		if (this.inners.size() == 1)
			return this.inners.get(0).getAmiObjects();
		for (int n = 0, s = inners.size(); n < s; n++)
			a[n] = inners.get(n).getAmiObjects();
		return IterableAndSizeIterator.create(a);
	}

	private class Listener implements AmiWebRealtimeObjectListener {

		@Override
		public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
			for (AmiWebRealtimeObjectListener i : amiListeners)
				try {
					i.onAmiEntitiesReset(AmiWebObjects_Union.this);
				} catch (Exception e) {
					LH.warning(log, "Error clearing Ami Entities for " + OH.getSimpleClassName(i), e);
				}
		}

		@Override
		public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
			for (AmiWebRealtimeObjectListener i : amiListeners)
				try {
					i.onAmiEntityAdded(AmiWebObjects_Union.this, entity);
				} catch (Exception e) {
					LH.warning(log, "Error adding Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
				}
		}

		@Override
		public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {
			for (AmiWebRealtimeObjectListener i : amiListeners)
				try {
					i.onAmiEntityUpdated(AmiWebObjects_Union.this, fields, entity);
				} catch (Exception e) {
					LH.warning(log, "Error updating Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
				}
		}

		@Override
		public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
			for (AmiWebRealtimeObjectListener i : amiListeners)
				try {
					i.onAmiEntityRemoved(AmiWebObjects_Union.this, entity);
				} catch (Exception e) {
					LH.warning(log, "Error removing Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
				}
		}

		@Override
		public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		}

		@Override
		public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte status, Map<String, Tuple2<Class, Class>> columns) {
			for (AmiWebRealtimeObjectListener i : amiListeners)
				try {
					i.onSchemaChanged(manager, status, columns);
				} catch (Exception e) {
					LH.warning(log, "Error processing Ami column remove to " + OH.getSimpleClassName(i), e);
				}
		}

	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectschema() {
		if (this.inners.size() == 0)
			return EmptyCalcTypes.INSTANCE;
		if (this.inners.size() == 1)
			return this.inners.get(0).getRealtimeObjectschema();
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (AmiWebRealtimeObjectManager o : this.inners)
			r.putAll(o.getRealtimeObjectschema());
		return r;
	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectsOutputSchema() {
		return getRealtimeObjectschema();
	}

	@Override
	public String getRealtimeId() {
		return this.realtimeId;
	}

	@Override
	public Set<String> getLowerRealtimeIds() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<String> getUpperRealtimeIds() {
		return AmiWebUtils.updateRealtimeIds(this.amiListeners, this.upperRealtimeIds);
	}

	public AmiWebRealtimeObjectListener[] getAMIListeners() {
		return this.amiListeners;
	}

}
