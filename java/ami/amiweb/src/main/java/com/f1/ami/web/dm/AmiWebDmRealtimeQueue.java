package com.f1.ami.web.dm;

import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebObjectFields;
import com.f1.ami.web.AmiWebRealtimeObjectListener;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.utils.OH;
import com.f1.utils.structs.CompactLongKeyMap;
import com.f1.utils.structs.Tuple2;

public class AmiWebDmRealtimeQueue implements AmiWebRealtimeObjectListener {

	private AmiWebDmRealtimeEvent realtimeEventsHead, realtimeEventsTail;
	private CompactLongKeyMap<AmiWebDmRealtimeEvent> realtimeEvents = new CompactLongKeyMap<AmiWebDmRealtimeEvent>("realtime", AmiWebDmRealtimeEvent.GETTER, 32);
	private AmiWebDmsImpl owner;

	public AmiWebDmRealtimeQueue(AmiWebDmsImpl owner) {
		this.owner = owner;
	}

	private void addRealtimeEvent(AmiWebDmRealtimeEvent event) {
		if (realtimeEventsHead == null)
			this.realtimeEventsHead = event;
		else {
			this.realtimeEventsTail.next = event;
			event.prior = realtimeEventsTail;
		}
		this.realtimeEventsTail = event;
		this.realtimeEvents.put(event);
		owner.onRtEvent();
	}

	private void removeRealtimeEvent(AmiWebDmRealtimeEvent event) {
		if (realtimeEventsHead == event) {
			realtimeEventsHead = realtimeEventsHead.next;
			if (realtimeEventsHead != null)
				realtimeEventsHead.prior = null;
		} else {
			event.prior.next = event.next;
		}
		if (realtimeEventsTail == event) {
			realtimeEventsTail = realtimeEventsTail.prior;
			if (realtimeEventsTail != null)
				realtimeEventsTail.next = null;
		} else {
			event.next.prior = event.prior;
		}
		this.realtimeEvents.remove(event.getData().getUniqueId());
	}

	public AmiWebDmRealtimeEvent drainRealtimeEventsBuffer() {
		AmiWebDmRealtimeEvent r = this.realtimeEventsHead;
		if (r != null) {
			this.realtimeEventsHead = this.realtimeEventsTail = null;
			this.realtimeEvents.clear();
		}
		return r;
	}

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
		boolean hadRecords = false;
		String realtimeId = manager.getRealtimeId();
		for (AmiWebDmRealtimeEvent event = this.realtimeEventsHead; event != null; event = event.next) {
			if (OH.eq(event.getRealtimeId(), realtimeId)) {
				hadRecords = true;
				break;
			}
		}
		if (hadRecords) {
			AmiWebDmRealtimeEvent events = drainRealtimeEventsBuffer();
			while (events != null) {
				AmiWebDmRealtimeEvent next = events.next;
				events.next = null;
				events.prior = null;
				if (OH.ne(events.getRealtimeId(), realtimeId))
					addRealtimeEvent(events);
				events = next;
			}
		}
		addRealtimeEvent(new AmiWebDmRealtimeEvent(realtimeId, realtimeId, null, null, AmiWebDmRealtimeEvent.TRUNCATE));
		for (AmiWebObject i : manager.getAmiObjects())
			onAmiEntityAdded(manager, i, AmiWebDmRealtimeEvent.SNAPSHOT);
	}

	@Override
	public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		onAmiEntityAdded(manager, entity, AmiWebDmRealtimeEvent.INSERT);
	}

	private void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity, byte status) {
		AmiWebDmRealtimeEvent existing = realtimeEvents.get(entity.getUniqueId());
		if (existing == null)
			addRealtimeEvent(new AmiWebDmRealtimeEvent(manager.getRealtimeId(), entity, status));
		else {//this shouldn't really happen
			OH.assertEqIdentity(existing.getData(), entity);
			existing.status = status;
		}
	}

	@Override
	public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {
		AmiWebDmRealtimeEvent existing = realtimeEvents.get(entity.getUniqueId());
		if (existing == null)
			addRealtimeEvent(new AmiWebDmRealtimeEvent(manager.getRealtimeId(), entity, AmiWebDmRealtimeEvent.UPDATE));
		else {
			OH.assertEqIdentity(existing.getData(), entity);
		}
	}

	@Override
	public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		AmiWebDmRealtimeEvent existing = realtimeEvents.get(entity.getUniqueId());
		if (existing == null)
			addRealtimeEvent(new AmiWebDmRealtimeEvent(manager.getRealtimeId(), entity, AmiWebDmRealtimeEvent.DELETE));
		else {
			OH.assertEqIdentity(existing.getData(), entity);
			removeRealtimeEvent(existing);
			if (existing.status == AmiWebDmRealtimeEvent.UPDATE)
				addRealtimeEvent(new AmiWebDmRealtimeEvent(manager.getRealtimeId(), entity, AmiWebDmRealtimeEvent.DELETE));
		}
	}

	public AmiWebDmRealtimeEvent getRealtimeEventsBuffer() {
		return this.realtimeEventsHead;
	}

	public AmiWebDm getDm() {
		return this.owner;
	}

	@Override
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		this.owner.onRealtimeLowerAriChanged(manager, oldAri, newAri);

	}

	@Override
	public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte status, Map<String, Tuple2<Class, Class>> columns) {
		for (Entry<String, Tuple2<Class, Class>> i : columns.entrySet()) {
			String columnName = i.getKey();
			Class old = i.getValue().getA();
			Class nuw = i.getValue().getB();
			if (old == null)
				addRealtimeEvent(new AmiWebDmRealtimeEvent(manager.getRealtimeId(), manager.getRealtimeId(), columnName, nuw, AmiWebDmRealtimeEvent.ADD_COLUMN));
			else if (nuw == null)
				addRealtimeEvent(new AmiWebDmRealtimeEvent(manager.getRealtimeId(), manager.getRealtimeId(), columnName, old, AmiWebDmRealtimeEvent.DROP_COLUMN));
			else
				addRealtimeEvent(new AmiWebDmRealtimeEvent(manager.getRealtimeId(), manager.getRealtimeId(), columnName, nuw, AmiWebDmRealtimeEvent.MODIFY_COLUMN));
		}
	}
}
