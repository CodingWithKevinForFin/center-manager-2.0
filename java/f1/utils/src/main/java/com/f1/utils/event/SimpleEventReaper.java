package com.f1.utils.event;

import java.util.Iterator;

import com.f1.utils.EH;
import com.f1.utils.concurrent.IdentityHashSet;

public class SimpleEventReaper {
	public static final long DEFAULT_TIMEOUT = 600000; // TODO: add properties
	private static final long CONST_NO_EVENTS = Long.MAX_VALUE;
	private IdentityHashSet<ExpiresEvent<?>> events; // Look at FASTRANDOMACCESSFILEPOOL for doublylinkedlist
	private long minEventTime;
	private long timeout;

	public SimpleEventReaper(long defaultTimeout) {
		this.events = new IdentityHashSet<SimpleEventReaper.ExpiresEvent<?>>();
		this.timeout = defaultTimeout;
	}
	public SimpleEventReaper() {
		this.events = new IdentityHashSet<SimpleEventReaper.ExpiresEvent<?>>();
		this.timeout = DEFAULT_TIMEOUT;
	}

	public void findAndReapExpiredEvents() {
		if (this.minEventTime == CONST_NO_EVENTS)
			return;
		long now = EH.currentTimeMillis();
		if (this.minEventTime > now)
			return;

		this.minEventTime = CONST_NO_EVENTS;
		Iterator<ExpiresEvent<?>> itr = events.iterator();
		while (itr.hasNext()) {
			ExpiresEvent<?> event = itr.next();
			if (event.isExpired(now)) {
				itr.remove();
				event.getListener().onEventTimedOut(event.getData());
			} else {
				long expiresOn = event.getExpiresOn();
				if (expiresOn < this.minEventTime)
					this.minEventTime = expiresOn;
			}

		}

	}

	public ExpiresEvent<?> newEvent(Object data, ExpiresEventListener listener) {
		ExpiresEvent<?> e = new ExpiresEvent(data, -timeout, listener, this);
		long expiresOn = e.getExpiresOn();
		if (expiresOn < this.minEventTime)
			this.minEventTime = expiresOn;
		events.add(e);
		return e;
	}
	public ExpiresEvent<?> newEvent(Object data, long to, ExpiresEventListener listener) {
		ExpiresEvent<?> e = new ExpiresEvent(data, to, listener, this);
		long expiresOn = e.getExpiresOn();
		if (expiresOn < this.minEventTime)
			this.minEventTime = expiresOn;
		events.add(e);
		return e;
	}
	public void expireNowNoFire(ExpiresEvent<?> event) {
		this.events.remove(event);
	}
	public void expireNow(ExpiresEvent<?> event) {
		this.events.remove(event);
		event.getListener().onEventTimedOut(event.getData());
	}

	public class ExpiresEvent<T> {
		final T data;
		final private long expiresOn;
		final ExpiresEventListener listener;

		private ExpiresEvent(T data, long E, ExpiresEventListener listener, SimpleEventReaper reaper) {
			this.data = data;
			this.expiresOn = E < 0 ? EH.currentTimeMillis() - E : E;
			this.listener = listener;
		}
		public boolean isExpired(long currentTime) {
			return this.getExpiresOn() <= currentTime;
		}
		public ExpiresEventListener getListener() {
			return listener;
		}
		public T getData() {
			return data;
		}
		public long getExpiresOn() {
			return expiresOn;
		}
		public void expiresNow() {
			SimpleEventReaper.this.expireNow(this);
		}
		public void expiresNowNoFire() {
			SimpleEventReaper.this.expireNowNoFire(this);
		}
	}

}
