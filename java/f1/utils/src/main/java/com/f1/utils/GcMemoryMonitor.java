package com.f1.utils;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

public class GcMemoryMonitor implements NotificationFilter, NotificationListener {

	private static final GcMemoryMonitor INSTANCE = new GcMemoryMonitor();
	static private long count;
	static private long lastTotalMemory;
	static private long lastFreeMemory;
	static private long lastMaxMemory;
	static private long lastUsedMemory;
	static private long lastTime;

	private GcMemoryMonitor() {
		GarbageCollectorMXBean bean = null;
		for (GarbageCollectorMXBean i : ManagementFactory.getGarbageCollectorMXBeans()) {
			NotificationEmitter t = (NotificationEmitter) i;
			//			if (i.getName().indexOf("Sweep") != -1)
			t.addNotificationListener(this, this, this);
		}
		lastTotalMemory = (EH.getTotalMemory());
		lastFreeMemory = EH.getFreeMemory();
		lastMaxMemory = EH.getMaxMemory();
		lastUsedMemory = getLastTotalMemory() - lastFreeMemory;
		lastTime = EH.currentTimeMillis();
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		//		System.out.println("Running: " + notification);
		if (notification.getMessage().indexOf("Scavenge") != -1)
			return;
		lastTotalMemory = (EH.getTotalMemory());
		lastFreeMemory = EH.getFreeMemory();
		lastMaxMemory = EH.getMaxMemory();
		lastUsedMemory = getLastTotalMemory() - lastFreeMemory;
		lastTime = EH.currentTimeMillis();
		count++;
	}

	@Override
	public boolean isNotificationEnabled(Notification notification) {
		return true;
	}

	public static long getLastTotalMemory() {
		return lastTotalMemory;
	}
	public static long getLastFreeMemory() {
		return lastFreeMemory;
	}
	public static long getLastUsedMemory() {
		return lastUsedMemory;
	}
	public static long getLastMaxMemory() {
		return lastMaxMemory;
	}
	public static long getLastTime() {
		return lastTime;
	}
	public static long getCount() {
		return count;
	}

}
