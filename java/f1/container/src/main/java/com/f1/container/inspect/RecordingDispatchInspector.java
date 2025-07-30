package com.f1.container.inspect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.f1.base.Action;
import com.f1.base.DateNanos;
import com.f1.container.Partition;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicThreadScope;
import com.f1.container.wrapper.InspectingDispatchController;
import com.f1.utils.BitMaskDescription;
import com.f1.utils.EH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.TextMatcherFactory;

public class RecordingDispatchInspector implements DispatchInspector {
	public static final BitMaskDescription EVENT_TYPES = new BitMaskDescription("EventTypes", ',', Integer.SIZE);
	public static final int DISPATCH = EVENT_TYPES.define(1, "dispatch");
	public static final int FORWARD = EVENT_TYPES.define(2, "forward");
	public static final int THROWN = EVENT_TYPES.define(4, "thrown");
	public static final int REPLY = EVENT_TYPES.define(8, "reply");
	public static final int PROCESS = EVENT_TYPES.define(16, "process");
	public static final int PARTITION_ADD = EVENT_TYPES.define(32, "partitionAdd");
	public static final int START = EVENT_TYPES.define(64, "start");
	public static final int STOP = EVENT_TYPES.define(128, "stop");

	final private List<RecordedEvent> events = new ArrayList<RecordedEvent>();
	final private List<Filter> ignoreFilters = new CopyOnWriteArrayList<Filter>();
	final private List<Filter> recordFilters = new CopyOnWriteArrayList<Filter>();

	private ContainerUidStats dispatchStats = new ContainerUidStats(1000);
	private ContainerUidStats forwardStats = new ContainerUidStats(1000);
	private ContainerUidStats processStats = new ContainerUidStats(1000);
	private ContainerUidStats thrownStats = new ContainerUidStats(1000);

	public RecordingDispatchInspector() {

	}

	public ContainerUidStats getDispatchStats() {
		return dispatchStats;
	}
	public ContainerUidStats getForwardStats() {
		return forwardStats;
	}
	public ContainerUidStats getThrownStats() {
		return thrownStats;
	}
	public ContainerUidStats getProcessStats() {
		return processStats;
	}

	public void ignoreEventsMatching(int type, String expression) {

		Filter f = new Filter(expression == null ? null : TextMatcherFactory.DEFAULT.toMatcher(expression), type);
		recordFilters.remove(f);
		ignoreFilters.remove(f);
		ignoreFilters.add(f);
	}

	public void recordEventsMatching(int type, String expression) {
		Filter f = new Filter(expression == null ? null : TextMatcherFactory.DEFAULT.toMatcher(expression), type);
		ignoreFilters.remove(f);
		recordFilters.remove(f);
		recordFilters.add(f);
	}

	private void record(RecordedEvent recordedEvent) {
		synchronized (events) {
			events.add(recordedEvent);
			events.notify();
		}
	}

	public List<RecordedEvent> flushEvents(int maxCount) {

		List<RecordedEvent> r;
		synchronized (events) {
			if (events.size() > maxCount) {

				r = new ArrayList<RecordedEvent>(events.subList(0, maxCount));
				ArrayList<RecordedEvent> r2 = new ArrayList<RecordedEvent>(events.subList(maxCount, events.size()));
				events.clear();
				events.addAll(r2);
			} else {
				r = new ArrayList<RecordedEvent>(events);
				events.clear();
			}
		}
		return r;
	}

	public int waitForEvents(int count, long timeoutMs) {
		synchronized (events) {
			long start = EH.currentTimeMillis();
			while (events.size() < count && OH.waitFromStart(events, start, timeoutMs))
				;
			return events.size();
		}

	}

	public boolean shouldRecord(int type, Processor<?, ?> p) {
		IF: if (recordFilters.size() > 0) {
			for (Filter f : recordFilters)
				if (f.matches(type, p))
					break IF;
			return false;
		}
		if (ignoreFilters.size() > 0) {
			for (Filter f : ignoreFilters)
				if (f.matches(type, p))
					return false;
		}
		return true;
	}

	@Override
	public void onDispatch(InspectingDispatchController dc, Port<?> optionalSourcePort, Processor<?, ?> p, Action a, ThreadScope t, Partition partition, long delayMs) {
		//TODO: handle delayed events
		if (delayMs <= 0) {
			dispatchStats.inc(optionalSourcePort);
			dispatchStats.inc(dc);
			dispatchStats.inc(p);
			dispatchStats.inc(partition);
			dispatchStats.inc(t);
		}
		if (shouldRecord(DISPATCH, p))
			record(new RecordedEvent(DISPATCH, p, a, partition.getPartitionId(), null, delayMs));
	}

	@Override
	public void onForward(InspectingDispatchController dc, Port<?> optionalSourcePort, Processor<?, ?> p, Action a, ThreadScope t) {
		forwardStats.inc(optionalSourcePort);
		forwardStats.inc(dc);
		forwardStats.inc(p);
		forwardStats.inc(t);
		BasicThreadScope bts = (BasicThreadScope) t;
		if (bts.state != null) {
			forwardStats.inc(bts.state.getPartition());
		} else
			throw new RuntimeException("state should not be null!");
		if (shouldRecord(FORWARD, p))
			record(new RecordedEvent(FORWARD, p, a, null, null, 0));
	}

	@Override
	public void onThrown(InspectingDispatchController dc, Processor<?, ?> p, Action a, ThreadScope t, State state, Throwable ex) {
		thrownStats.inc(dc);
		thrownStats.inc(p);
		thrownStats.inc(t);
		if (state != null) {
			thrownStats.inc(state);
			thrownStats.inc(state.getPartition());
		}
		if (shouldRecord(THROWN, p))
			record(new RecordedEvent(THROWN, p, a, null, null, 0));
	}

	@Override
	public void onReply(InspectingDispatchController dc, Port<?> optionalSourcePort, RequestMessage<?> request, ResultMessage<?> result, ThreadScope t) {
	}

	@Override
	public void onPartitionAdded(InspectingDispatchController dc, Partition partition) {
		if (shouldRecord(PARTITION_ADD, null))
			record(new RecordedEvent(PARTITION_ADD, null, null, partition.getPartitionId(), null, 0));
	}

	@Override
	public void onProcess(InspectingDispatchController dc, Port optionalSourcePort, Processor p, Action a, State s, ThreadScope t) {
		processStats.inc(dc);
		processStats.inc(p);
		processStats.inc(optionalSourcePort);
		Partition partition;
		if (s != null) {
			partition = s.getPartition();
			processStats.inc(s);
		} else {
			BasicThreadScope bts = (BasicThreadScope) t;
			partition = bts.state.getPartition();
		}
		processStats.inc(partition);
		if (shouldRecord(PROCESS, p))
			record(new RecordedEvent(PROCESS, p, a, s == null ? null : s.getPartition().getPartitionId(), null, 0));
	}

	@Override
	public void onStart(InspectingDispatchController dc) {
		if (shouldRecord(START, null))
			record(new RecordedEvent(START, null, null, null, null, 0));

	}

	@Override
	public void onStop(InspectingDispatchController dc) {
		if (shouldRecord(STOP, null))
			record(new RecordedEvent(STOP, null, null, null, null, 0));
	}

	private static class Filter {
		public final TextMatcher processorPath;
		public final int type;

		public Filter(TextMatcher textMatcher, int type) {
			this.processorPath = textMatcher;
			this.type = type;
		}

		public boolean matches(int type, Processor<?, ?> p) {
			if (!MH.areAnyBitsSet(type, this.type))
				return false;
			if (p != null && processorPath != null && !processorPath.matches(p.getFullName()))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			return OH.hashCode(processorPath, type);
		}

		@Override
		public boolean equals(final Object obj) {
			if (OH.getClass(obj) != getClass())
				return false;
			final Filter o = (Filter) obj;
			return OH.eq(processorPath.toString(), o.processorPath.toString()) && OH.eq(type, o.type);
		}

	}

	public static class RecordedEvent {
		@Override
		public String toString() {
			return "RecordedEvent [timeNs=" + new DateNanos(timeNs).toLegibleString() + ", type=" + type + ", processor=" + processor + ", action=" + action + ", partitionId="
					+ partitionId + ", thrown=" + thrown + ", delay=" + delay + "]";
		}

		final private long timeNs;
		final private int type;
		final private Processor processor;
		final private Action action;
		final private Object partitionId;
		final private Throwable thrown;
		final private long delay;

		public RecordedEvent(int type, Processor processor, Action action, Object partitionId, Throwable thrown, long delay) {
			this.timeNs = EH.currentTimeNanos();
			this.type = type;
			this.processor = processor;
			this.action = action;
			this.partitionId = partitionId;
			this.thrown = thrown;
			this.delay = delay;
		}

		public long getTimeNs() {
			return timeNs;
		}

		public int getType() {
			return type;
		}

		public Processor getProcessor() {
			return processor;
		}

		public Action getAction() {
			return action;
		}

		public Object getPartitionId() {
			return partitionId;
		}

		public Throwable getThrown() {
			return thrown;
		}

		public long getDelay() {
			return delay;
		}

	}

	public void resetRecoding() {
		recordFilters.clear();
		ignoreFilters.clear();
		events.clear();
	}

}
