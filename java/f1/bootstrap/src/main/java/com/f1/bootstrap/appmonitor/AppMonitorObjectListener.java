package com.f1.bootstrap.appmonitor;

import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;

public interface AppMonitorObjectListener<T extends F1AppEntity, O> {

	byte TYPE_THREADSCOPE = 1;
	byte TYPE_PROCESSOR = 2;
	byte TYPE_PARTITION = 3;
	byte TYPE_MSGTOPIC = 4;
	byte TYPE_LOGGER = 5;
	byte TYPE_LOGGER_SINK = 6;
	byte TYPE_DISPATCHER = 7;
	byte TYPE_DATABASE = 8;
	byte TYPE_CONTAINER_SCOPE = 9;
	byte TYPE_PORT = 10;

	public byte getListenerType();
	public boolean resetHasChanged();

	public Class<T> getAgentType();

	public AppMonitorState getState();

	public T getAgentObject();
	public void setAgentObject(T o);

	public O getObject();

	public boolean updateAgentObject();
	F1AppAuditTrailEvent popAuditTrailEvent();

	public void addAuditRule(AppMonitorAuditRule<?> rule);
	public boolean removeAuditRule(AppMonitorAuditRule<?> rule);
	public boolean getIsAudited();

	public Class<?> getObjectClass();
}
