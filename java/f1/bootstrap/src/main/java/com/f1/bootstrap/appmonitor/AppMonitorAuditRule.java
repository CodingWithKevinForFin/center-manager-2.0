package com.f1.bootstrap.appmonitor;

public interface AppMonitorAuditRule<T extends AppMonitorObjectListener<?, ?>> {

	public boolean isAuditable(T listener);

	public Class<T> getListenerType();

	public long getId();
}
