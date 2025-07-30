package com.f1.ami.web;

import java.util.Map;

import com.f1.utils.structs.Tuple2;

public interface AmiWebRealtimeObjectListener {
	public static final byte SCHEMA_ADDED = 0;
	public static final byte SCHEMA_DROPPED = 1;
	public static final byte SCHEMA_MODIFIED = 2;

	AmiWebRealtimeObjectListener[] EMPTY_ARRAY = new AmiWebRealtimeObjectListener[0];

	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager);
	public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity);
	public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity);
	public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity);
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri);
	public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte schemaStatus, Map<String, Tuple2<Class, Class>> columns);
}
