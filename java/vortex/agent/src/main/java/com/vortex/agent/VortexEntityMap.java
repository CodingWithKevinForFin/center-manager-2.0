package com.vortex.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.VortexEntity;

public class VortexEntityMap {

	private Map<Class<? extends VortexEntity>, LongKeyMap<VortexEntity>> entities = new HashMap<Class<? extends VortexEntity>, LongKeyMap<VortexEntity>>();

	public VortexEntity add(VortexEntity vortexEntity) {
		Class<? extends VortexEntity> ctype = (Class<? extends VortexEntity>) vortexEntity.askSchema().askOriginalType();
		LongKeyMap<VortexEntity> entityMap = entities.get(ctype);
		if (entityMap == null)
			entities.put(ctype, entityMap = new LongKeyMap<VortexEntity>());
		if (vortexEntity.getId() == 0)
			throw new IllegalArgumentException("id must be set: " + vortexEntity);
		return entityMap.put(vortexEntity.getId(), vortexEntity);
	}

	public VortexEntityMap addAll(Iterable<? extends VortexEntity> entities) {
		for (VortexEntity entity : entities)
			add(entity);
		return this;
	}

	public VortexEntity get(Class<? extends VortexEntity> type, long id) {
		LongKeyMap<VortexEntity> entityMap = entities.get(type);
		return entityMap == null ? null : entityMap.get(id);
	}

	public <T extends VortexEntity> LongKeyMap<T> getEntities(Class<T> type) {
		LongKeyMap<VortexEntity> r = entities.get(type);
		return r == null ? LongKeyMap.EMPTY : (LongKeyMap)r;
	}

	public Set<Class<? extends VortexEntity>> getEntityTypes() {
		return entities.keySet();
	}

	public boolean remove(VortexEntity vortexEntity) {
		Class<? extends VortexEntity> ctype = (Class<? extends VortexEntity>) vortexEntity.askSchema().askOriginalType();
		LongKeyMap<VortexEntity> entityMap = entities.get(ctype);
		if (entityMap == null)
			return false;
		if (vortexEntity.getId() == 0)
			throw new IllegalArgumentException("id must be set: " + vortexEntity);
		return entityMap.remove(vortexEntity.getId()) != null;
	}

}
