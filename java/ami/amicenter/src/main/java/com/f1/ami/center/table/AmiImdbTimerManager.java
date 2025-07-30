package com.f1.ami.center.table;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.f1.ami.center.AmiCenterAmiUtilsForTable;
import com.f1.ami.center.AmiCenterSuite;
import com.f1.ami.center.triggers.AmiTimedRunnable;
import com.f1.base.Message;
import com.f1.container.ContainerTools;
import com.f1.container.OutputPort;
import com.f1.utils.LongArrayList;
import com.f1.utils.OH;
import com.f1.utils.Scheduler;

public class AmiImdbTimerManager {
	private int nextTriggerId;
	private static final int MAX_TIMER_POOL_SIZE = 100;
	private Scheduler<TriggerTimer> schedule = new Scheduler<TriggerTimer>();
	private List<TriggerTimer> timerPool = new ArrayList<TriggerTimer>(MAX_TIMER_POOL_SIZE);
	private LongArrayList nextTimerTimeList = new LongArrayList();
	private long lastFireTimer = 0L;
	private OutputPort<Message> timerPort;
	private Message timerAction;
	final private ContainerTools tools;
	final private AmiImdbImpl imdb;

	private static class TriggerTimer {
		public long id;
		public Object correlationId;
		public AmiTimedRunnable trigger;

		public void reset(long id, AmiTimedRunnable trigger, Object correlationId) {
			OH.assertNotNull(trigger);
			this.id = id;
			this.trigger = trigger;
			this.correlationId = correlationId;
		}

		public void fire() {
			try {
				trigger.onTimer(this.id, this.correlationId);
			} catch (Exception e) {
				AmiCenterAmiUtilsForTable.logTriggerError(trigger, "ONTIMER", e, null);
			}
			id = -1L;
			trigger = null;
			this.correlationId = null;
		}
	}

	public AmiImdbTimerManager(AmiImdbImpl imdb) {
		this.tools = imdb.getTools();
		this.imdb = imdb;
		this.timerPort = imdb.getState().getTimerPort();
		this.timerAction = this.timerPort.nw(Message.class);
	}

	public long registerTimer(AmiTimedRunnable trigger, long millis, int priority, Object correlationObject) {
		long now = tools.getNow();
		if (millis < 0)
			millis = now - millis;
		if (millis <= lastFireTimer)
			millis = lastFireTimer + 1;//Force event to be deferred until next fireTimers(), keeps the scheduler's design assumptions happy too.
		int size = this.timerPool.size();
		TriggerTimer t = (size > 0) ? this.timerPool.remove(size - 1) : new TriggerTimer();
		t.reset(nextTriggerId++, trigger, correlationObject);
		if (schedule.getNextTime() > millis) {
			schedule.addEvent(millis, priority, t);
			sendTimerIfNeeded(now);
		} else
			schedule.addEvent(millis, priority, t);
		return t.id;
	}

	public void fireTimers(long now, boolean fromTimer) {
		if (fromTimer) {
			this.nextTimerTimeList.removeAt(this.nextTimerTimeList.size() - 1);
		}
		lastFireTimer = now;
		for (;;) {
			TriggerTimer trigger = schedule.next(now);
			if (trigger == null)
				break;
			trigger.fire();
			if (timerPool.size() < MAX_TIMER_POOL_SIZE)
				timerPool.add(trigger);
		}
	}
	private void sendTimerIfNeeded(long now) {
		final long nextTime = schedule.getNextTime();
		final long nextTimerTime = nextTimerTimeList.isEmpty() ? -1L : nextTimerTimeList.getLong(nextTimerTimeList.size() - 1);
		if (nextTimerTime == -1L || nextTime < nextTimerTime) {
			nextTimerTimeList.add(nextTime);
			if (nextTime <= now) {
				timerPort.send(timerAction, AmiCenterSuite.PARTITIONID_AMI_CENTER, null);
			} else {
				timerPort.sendDelayed(timerAction, AmiCenterSuite.PARTITIONID_AMI_CENTER, null, nextTime - now, TimeUnit.MILLISECONDS);
			}
		}
	}

	public void onProcessedEventsComplete() {
		long now = this.imdb.getNow();
		fireTimers(now, false);
		if (!schedule.isEmpty())
			sendTimerIfNeeded(now);
	}
	public void setTimerPort(OutputPort<Message> timerPort) {
	}
}
