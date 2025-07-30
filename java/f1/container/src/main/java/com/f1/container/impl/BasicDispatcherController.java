/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.base.Table;
import com.f1.container.DispatchController;
import com.f1.container.MultiProcessor;
import com.f1.container.OutputPort;
import com.f1.container.Partition;
import com.f1.container.PersistenceController;
import com.f1.container.Port;
import com.f1.container.ProcessActionListener;
import com.f1.container.Processor;
import com.f1.container.RequestMessage;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadPoolController;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ContainerException;
import com.f1.container.exceptions.PartitionResolverException;
import com.f1.container.exceptions.ProcessorException;
import com.f1.container.impl.dispatching.ActionProcessorRunnable;
import com.f1.container.impl.dispatching.ConflatedPartitionActionRunner;
import com.f1.container.impl.dispatching.DelayedActionEvent;
import com.f1.container.impl.dispatching.HighPriorityPartitionActionRunner;
import com.f1.container.impl.dispatching.IterableActionProcessor;
import com.f1.container.impl.dispatching.LowPriorityPartitionActionRunner;
import com.f1.container.impl.dispatching.PartitionActionRunner;
import com.f1.container.impl.dispatching.QueuedPartitionActionRunner;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.container.impl.dispatching.RootPartitionStats;
import com.f1.utils.AH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.Labeler;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.structs.table.BasicTable;

public class BasicDispatcherController extends AbstractContainerScope implements DispatchController {
	final private ConcurrentMap<Object, RootPartitionActionRunner> stateQueues = new ConcurrentHashMap<Object, RootPartitionActionRunner>();
	private DispatchController dispatcher;
	private PersistenceController persistenceController;
	final private AtomicLong actionsProcessedCount = new AtomicLong(0);
	final private AtomicLong actionsSubmittedCount = new AtomicLong(0);
	final private AtomicLong exceptionsCount = new AtomicLong(0);
	private int queueTimeoutCheckFrequency = DEFAULT_QUEUE_TIMEOUT_CHECK_FREQUENCY;
	private long queueTimeoutMs = DEFAULT_QUEUE_TIMEOUT;
	private Level performanceLoggingLevel = null;

	public static final int DEFAULT_QUEUE_TIMEOUT_CHECK_FREQUENCY = 1000;
	public static final long DEFAULT_QUEUE_TIMEOUT = 1000;
	private static final int MAX_LEGIBLE_LENGTH = 1024 * 1024;

	@Override
	public Partition dispatch(Port optionalSourcePort, Processor processor, Action action, Object partitionId, long delayMs, ThreadScope threadScope) {
		if (delayMs == 0L) {
			return dispatch(optionalSourcePort, processor, action, partitionId, threadScope);
		} else {
			if (partitionId == null) {
				partitionId = processor.getPartitionResolver().getPartitionId(action);
				if (partitionId == null) {
					throw new PartitionResolverException("partitionId can not be null (when dispatching)").setTargetProcessor(processor).setAction(action).set("partion Resolver",
							processor.getPartitionResolver());
				}
			}
			RootPartitionActionRunner saq = stateQueues.get(partitionId);
			if (saq == null) {
				Partition partition = getContainer().getPartitionController().getOrCreatePartition(partitionId);
				dispatcher.onPartitionAdded(partition);
				saq = stateQueues.get(partitionId);
			}
			Runnable runnable = new DelayedActionEvent(optionalSourcePort, dispatcher, processor, action, partitionId, EH.currentTimeMillis() + delayMs);
			if (performanceLoggingLevel != null && log.isLoggable(performanceLoggingLevel))
				LH.log(log, performanceLoggingLevel, "PERFORMANCE: DispatchDelay: pid=", partitionId, ", action=", OH.getSimpleClassName(action), "^" + identity(action),
						", proc= ", OH.getSimpleClassName(processor), ", delay=", delayMs);
			getContainer().getThreadPoolController().execute(runnable, ThreadPoolController.POOLKEY_DELAYED, delayMs, TimeUnit.MILLISECONDS);
			return saq.getPartition();
		}
	}

	@Override
	public Partition dispatch(Port optionalSourcePort, Processor processor, Action action, Object partitionId, ThreadScope threadScope) {
		if (partitionId == null) {
			partitionId = processor.getPartitionResolver().getPartitionId(action);
			if (partitionId == null) {
				dispatchNoPartition(optionalSourcePort, processor, action, threadScope);
				return null;
			}

		}
		RootPartitionActionRunner saq = null;
		if (threadScope instanceof BasicThreadScope) {
			BasicThreadScope bts = (BasicThreadScope) threadScope;
			if (bts.state != null && bts.state.getPartition().getPartitionId().equals(partitionId)) {
				saq = bts.stripeActionQueue;
			} else
				saq = stateQueues.get(partitionId);
		} else
			saq = stateQueues.get(partitionId);

		if (saq == null) {
			Partition partition = getContainer().getPartitionController().getOrCreatePartition(partitionId);
			dispatcher.onPartitionAdded(partition);
			saq = stateQueues.get(partitionId);
		}
		if (performanceLoggingLevel != null && log.isLoggable(performanceLoggingLevel))
			LH.log(log, performanceLoggingLevel, "PERFORMANCE: Dispatch: pid=", partitionId, ", action=", OH.getSimpleClassName(action), "^" + identity(action), ", proc= ",
					OH.getSimpleClassName(processor));
		Partition partition = saq.getPartition();
		if (processor.getProcessActionListeners().length > 0)
			fireQueueAction(processor, partition, action, threadScope, processor.getProcessActionListeners());
		if (getProcessActionListeners().length > 0)
			fireQueueAction(processor, partition, action, threadScope, getProcessActionListeners());
		if (partition.getProcessActionListeners().length > 0)
			fireQueueAction(processor, partition, action, threadScope, partition.getProcessActionListeners());
		if (threadScope != null && threadScope.getProcessActionListeners().length > 0)
			fireQueueAction(processor, partition, action, threadScope, threadScope.getProcessActionListeners());
		if (this.processListeners.length > 0)
			fireQueueAction(processor, partition, action, threadScope, this.processListeners);
		actionsSubmittedCount.incrementAndGet();
		saq.addAction(processor, action, (BasicThreadScope) threadScope);
		return partition;
	}

	private void dispatchNoPartition(Port optionalSourcePort, Processor processor, Action action, ThreadScope threadScope) {
		if (performanceLoggingLevel != null && log.isLoggable(performanceLoggingLevel))
			LH.log(log, performanceLoggingLevel, "PERFORMANCE: Dispatch No Partition: action=", OH.getSimpleClassName(action), "^" + identity(action), ", proc= ",
					OH.getSimpleClassName(processor));

		actionsSubmittedCount.incrementAndGet();
		getContainer().getThreadPoolController().execute(new ActionProcessorRunnable(this, optionalSourcePort, processor, action));
		if (processor.getProcessActionListeners().length > 0)
			fireQueueAction(processor, null, action, threadScope, processor.getProcessActionListeners());
		if (getProcessActionListeners().length > 0)
			fireQueueAction(processor, null, action, threadScope, getProcessActionListeners());
		if (threadScope != null && threadScope.getProcessActionListeners().length > 0)
			fireQueueAction(processor, null, action, threadScope, threadScope.getProcessActionListeners());
		if (this.processListeners.length > 0)
			fireQueueAction(processor, null, action, threadScope, this.processListeners);

	}
	@Override
	public void forward(Port optionalSourcePort, Processor processor, Action action, ThreadScope threadScope) {
		final Object type = processor.getStateType();
		if (type == null) {
			try {
				actionsSubmittedCount.incrementAndGet();
				dispatcher.safelyProcess(optionalSourcePort, processor, action, null, threadScope, false);
			} catch (Exception e) {
				dispatcher.handleThrowableFromProcessor(processor, action, null, threadScope, e);
			}
		} else {
			if (threadScope == null)
				throw new RuntimeException("Must supply threadscope when forwarding!");
			BasicThreadScope basicThreadScope = (BasicThreadScope) threadScope;
			State existingState = basicThreadScope.state;
			try {
				Object partitionId = processor.getPartitionResolver().getPartitionId(action);
				State state = basicThreadScope.state;
				if (state != null && partitionId != null && !state.getPartition().getPartitionId().equals(partitionId))
					throw new ContainerException("can not cross partions when forwarding").setSourcePartition(state.getPartition()).setTargetPartitionId(partitionId)
							.setTargetProcessor(processor).setAction(action).setTargetState(state).setTargetProcessor(processor).setSourcePort(optionalSourcePort);
				else if (state != null && state.getType() != type && !state.getType().equals(type)) {
					final Partition partition = state.getPartition();
					if (partition.getStateTypes().contains(type))
						state = partition.getState(type);
					else
						state = dispatcher.createState(state.getPartition(), processor, action);
				}
				dispatcher.safelyProcess(optionalSourcePort, processor, action, state, threadScope, false);
			} catch (Exception e) {
				dispatcher.handleThrowableFromProcessor(processor, action, null, threadScope, e);
			} finally {
				basicThreadScope.state = existingState;
			}
		}
	}

	@Override
	public <A extends Action, S extends State> void safelyProcess(Port<?> optionalSourcePort, Processor<? super A, ? super S> processor, A action, S state, ThreadScope threadScope,
			boolean isDispatch) {
		final BasicThreadScope ts = threadScope instanceof BasicThreadScope ? (BasicThreadScope) threadScope : null;
		try {
			if (state != null)
				((BasicStatePeer) state.getPeer()).countProcessAction++;
			if (log.isLoggable(Level.FINER)) {
				if (log.isLoggable(Level.FINEST)) {
					LH.finer(log, "Sending to: \n  ** Processor Name: ", processor.getFullName(), "\n  ** Action: \n",
							SH.prefixLines(RootAssister.INSTANCE.toLegibleString(action, MAX_LEGIBLE_LENGTH), "  **    "));
				} else
					LH.finer(log, "Sending to ", processor, ": ", action);
			}
			try {
				if (ts != null)
					ts.onProcessActionBegin(processor, action, state);
				Partition partition = threadScope == null ? null : ((BasicThreadScope) threadScope).partition;
				if (processor.getProcessActionListeners().length > 0)
					fireProcessAction(processor, partition, action, state, threadScope, processor.getProcessActionListeners(), isDispatch);
				if (getProcessActionListeners().length > 0)
					fireProcessAction(processor, partition, action, state, threadScope, getProcessActionListeners(), isDispatch);
				if (partition != null && partition.getProcessActionListeners().length > 0)
					fireProcessAction(processor, partition, action, state, threadScope, partition.getProcessActionListeners(), isDispatch);
				if (threadScope != null && threadScope.getProcessActionListeners().length > 0)
					fireProcessAction(processor, partition, action, state, threadScope, threadScope.getProcessActionListeners(), isDispatch);
				if (performanceLoggingLevel != null && log.isLoggable(performanceLoggingLevel)) {
					Object partitionId = state != null && state.getPartition() != null ? state.getPartition().getPartitionId() : null;
					long start = System.nanoTime();
					try {
						processor.processAction(action, state, threadScope);
					} catch (Exception e) {
						long now = System.nanoTime();
						LH.log(log, performanceLoggingLevel, "PERFORMANCE: processActionException: pid=", partitionId, ", action=", OH.getSimpleClassName(action), "^",
								identity(action), ", proc= ", OH.getSimpleClassName(processor), ", nanos=", (now - start));
						throw e;
					}
					long now = System.nanoTime();
					LH.log(log, performanceLoggingLevel, "PERFORMANCE: processAction: pid=", partitionId, ", action=", OH.getSimpleClassName(action), "^", identity(action),
							", proc= ", OH.getSimpleClassName(processor), ", nanos=", (now - start));
				} else {
					processor.processAction(action, state, threadScope);
				}
				if (processor.getProcessActionListeners().length > 0)
					fireProcessActionDone(processor, partition, action, state, threadScope, processor.getProcessActionListeners(), isDispatch);
				if (getProcessActionListeners().length > 0)
					fireProcessActionDone(processor, partition, action, state, threadScope, getProcessActionListeners(), isDispatch);
				if (partition != null && partition.getProcessActionListeners().length > 0)
					fireProcessActionDone(processor, partition, action, state, threadScope, partition.getProcessActionListeners(), isDispatch);
				if (threadScope != null && threadScope.getProcessActionListeners().length > 0)
					fireProcessActionDone(processor, partition, action, state, threadScope, threadScope.getProcessActionListeners(), isDispatch);

				if (persistenceController.getIsAutoCommit())
					persistenceController.commitState(state);
			} catch (ProcessorException t) {
				if (t.getTargetProcessor() == null)
					t.setTargetProcessor(processor);
				if (t.getAction() == null)
					t.setAction(action);
				if (t.getTargetState() == null)
					t.setTargetState(state);
				if (t.getSourcePort() == null)
					t.setSourcePort(optionalSourcePort);
				throw t;
			}
			actionsProcessedCount.incrementAndGet();
		} catch (Throwable t) {
			actionsProcessedCount.incrementAndGet();
			exceptionsCount.incrementAndGet();
			if (ts != null)
				ts.onProcessActionError(t);
			if (state != null)
				((BasicStatePeer) state.getPeer()).countThrowables++;
			dispatcher.handleThrowableFromProcessor(processor, action, state, threadScope, t);
		} finally {
			if (ts != null)
				ts.onProcessActionEnd();
		}
	}

	private static final void fireProcessAction(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, ProcessActionListener[] targets,
			boolean isDispatching) {
		for (int i = 0; i < targets.length; i++) {
			targets[i].onProcessAction(processor, partition, action, state, threadScope, isDispatching);
		}
	}
	private static final void fireProcessActionDone(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, ProcessActionListener[] targets,
			boolean isDispatching) {
		for (int i = 0; i < targets.length; i++) {
			targets[i].onProcessActionDone(processor, partition, action, state, threadScope, isDispatching);
		}
	}
	private static final void fireHandleThrowable(Processor processor, Partition partition, Action action, State state, ThreadScope threadScope, Throwable thrown,
			ProcessActionListener[] targets) {
		for (int i = 0; i < targets.length; i++) {
			targets[i].onHandleThrowable(processor, partition, action, state, threadScope, thrown);
		}
	}
	private static final void fireQueueAction(Processor processor, Partition partition, Action action, ThreadScope threadScope, ProcessActionListener[] targets) {
		for (int i = 0; i < targets.length; i++) {
			targets[i].onQueueAction(processor, partition, action, threadScope);
		}
	}

	@Override
	public boolean onPartitionAdded(Partition partition) {
		return null == stateQueues.putIfAbsent(partition.getPartitionId(), newPartitionActionRunner(partition));
	}

	@Override
	public boolean onPartitionRemoved(Partition partition) {
		if (partition == null)
			return false;
		RootPartitionActionRunner removed = this.stateQueues.remove(partition.getPartitionId());
		if (removed == null)
			return false;
		if (removed.inThreadPool.get()) {
			LH.warning(log, "Partition removed while in threadpool: ", partition.getPartitionId());
		}
		return true;
	}

	private RootPartitionActionRunner newPartitionActionRunner(Partition partition) {
		int partitionOptions = partition.getOptions();
		int size = 1;
		if (MH.anyBits(partitionOptions, Partition.OPTION_SUPPORT_CONFLATION))
			size++;
		if (MH.anyBits(partitionOptions, Partition.OPTION_SUPPORT_HIGH_PRIORITY))
			size++;
		if (MH.anyBits(partitionOptions, Partition.OPTION_SUPPORT_LOW_PRIORITY))
			size++;

		PartitionActionRunner[] runners = new PartitionActionRunner[size];
		int pos = 0;
		if (MH.anyBits(partitionOptions, Partition.OPTION_SUPPORT_CONFLATION))
			runners[pos++] = new ConflatedPartitionActionRunner(true, dispatcher);
		if (MH.anyBits(partitionOptions, Partition.OPTION_SUPPORT_HIGH_PRIORITY))
			runners[pos++] = new HighPriorityPartitionActionRunner(dispatcher, 99);
		runners[pos++] = new QueuedPartitionActionRunner(dispatcher);
		if (MH.anyBits(partitionOptions, Partition.OPTION_SUPPORT_LOW_PRIORITY))
			runners[pos++] = new LowPriorityPartitionActionRunner(dispatcher, 101);
		return new RootPartitionActionRunner(runners, dispatcher, partition);
	}
	@Override
	public void handleThrowableFromProcessor(Processor processor, Action action, State state, ThreadScope thread, Throwable t) {

		try {
			processor.handleThrowable(action, state, thread, t);
			Partition partition = state == null ? null : state.getPartition();
			if (processor.getProcessActionListeners().length > 0)
				fireHandleThrowable(processor, partition, action, state, thread, t, processor.getProcessActionListeners());
			if (getProcessActionListeners().length > 0)
				fireHandleThrowable(processor, partition, action, state, thread, t, getProcessActionListeners());
			if (partition != null && partition.getProcessActionListeners().length > 0)
				fireHandleThrowable(processor, partition, action, state, thread, t, partition.getProcessActionListeners());
			if (thread != null && thread.getProcessActionListeners().length > 0)
				fireHandleThrowable(processor, partition, action, state, thread, t, thread.getProcessActionListeners());
			if (processListeners.length > 0)
				fireHandleThrowable(processor, partition, action, state, thread, t, processListeners);
			return;
		} catch (Throwable t2) {

			Throwable i = t2;
			for (;;) {
				if (t == i) {
					t = t2;
					break;
				} else if (i.getCause() == null) {
					try {
						i.initCause(new RuntimeException("*** binding param of handleThrowable to thrown ***", t));
					} catch (Exception e) {
						throw new RuntimeException("*** binding cause failed for inner exception ***", t2);
					}
					break;
				}
				i = i.getCause();
			}
		}
		try {
			if (action instanceof RequestMessage) {
				LH.log(log, Level.WARNING, "Error processing request, automated reply", t);
				ResultMessage<?> resultAction = nw(ResultMessage.class);
				resultAction.setError(t);
				dispatcher.reply(null, (RequestMessage<?>) action, resultAction, thread);
			} else if (t instanceof ProcessorException) {
				LH.warning(log, "Error processing action", t);
			} else if (t instanceof Exception)
				LH.severe(log, "Error processing event ", action, " on processor ", processor, t);
			else
				LH.severe(log, "Error processing event ", action, " on processor ", processor, t);
		} catch (Throwable e2) {
			LH.severe(log, "Error raised while processing throwable from ", processor, "  E2:", e2);
			LH.severe(log, "Original Error:", t);
		}

	}

	@Override
	public <A extends Action, S extends State> State createState(Partition partition, Processor<? super A, S> processor, A action) {
		Class<S> type = processor.getStateType();
		if (type == null)
			throw new NullPointerException("state type can not be null");
		State r = getContainer().getPartitionController().createState(partition, action, processor);
		if (r == null)
			return null;
		r.setType(type);
		r.setPartition(partition);
		r.setPeer(new BasicStatePeer());
		partition.putState(r);
		return r;
	}

	@Override
	public void reply(Port<?> optionalSourcePort, RequestMessage<?> request, ResultMessage<?> result, ThreadScope threadScope) {
		OutputPort<?> port = request.getResultPort();
		result.setRequestMessage(request);

		ResultActionFuture<Action> future = (ResultActionFuture<Action>) request.getFuture();

		if (future != null)
			future.provideResult((ResultMessage<Action>) result);
		else if (port == null && log.isLoggable(Level.INFO))
			LH.info(log, "Action dropped due to no processor nor future. Request: ", request, "  Result: ", result);

		if (port != null) {
			Processor processor = port.getProcessor();
			if (processor == null)
				throw new ContainerException("processor is null for port, don't know were to dispatch reply to").setSourcePort(port);
			dispatcher.dispatch(optionalSourcePort, processor, result, null, threadScope);
		}
	}

	public String getStats() {
		Table table = new BasicTable(String.class, "Partition", Date.class, "Created", Long.class, "Actions Exec", Long.class, "Actions Added", Long.class, "Time(ms)", Long.class,
				"Thread Visits", Boolean.class, "In Pool");
		table.setTitle("Dispatcher Statistics");
		RootPartitionStats sink = new RootPartitionStats();
		for (Map.Entry<Object, RootPartitionActionRunner> e : stateQueues.entrySet()) {
			RootPartitionActionRunner runner = e.getValue();
			runner.getStats(sink);
			table.getRows().addRow(e.getKey(), new Date(runner.getStartTime()), sink.getActionsProcessed(), sink.getActionsAdded(), sink.getTimeSpentMs(), sink.getQueueRuns(),
					sink.getInThreadPool());
		}
		return table.toString();
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void init() {
		super.init();
		this.dispatcher = getContainer().getDispatchController();
		this.persistenceController = getContainer().getPersistenceController();
	}

	// TODO: this is not full proof. It is possible for this to return true,
	// despite events still being in flight
	@Override
	public boolean isIdle() {
		int preSize = stateQueues.size();
		long preActionsAdded = 0;
		for (RootPartitionActionRunner queue : stateQueues.values()) {
			preActionsAdded += queue.getActionsAdded();
			if (queue.inThreadPool.get() || queue.hasActions())
				return false;
		}

		long postActionsAdded = 0;
		int postSize = stateQueues.size();
		for (RootPartitionActionRunner queue : stateQueues.values())
			postActionsAdded += queue.getActionsAdded();
		return preSize == postSize && preActionsAdded == postActionsAdded;
	}

	@Override
	public void diagnose(Labeler labeler) {
		super.diagnose(labeler);
		labeler.addItem("Is Idle", isIdle());
		labeler.addItem("Partitions", getStats());
	}

	@Override
	public Level getPerformanceLoggingLevel() {
		return performanceLoggingLevel;
	}

	public void setPerformanceLoggingLevel(Level performanceLoggingLevel) {
		this.performanceLoggingLevel = performanceLoggingLevel;
	}

	static public String identity(Object object) {
		if (object == null)
			return "";
		return SH.toString(System.identityHashCode(object) % SH.MAX_CACHED_INTS_SIZE);
	}

	@Override
	public Collection<RootPartitionActionRunner> getRootParititionRunners() {
		return this.stateQueues.values();
	}

	private ProcessActionListener[] processListeners = ContainerHelper.EMPTY_PROCESS_EVENT_LISTENER_ARRAY;

	@Override
	public void addProcessActionListener(ProcessActionListener listener) {
		processListeners = AH.insert(processListeners, processListeners.length, listener);
	}

	@Override
	public ProcessActionListener[] getProcessActionListeners() {
		return processListeners;
	}

	@Override
	public void safelyProcess(IterableActionProcessor iterator, MultiProcessor processor, State state, BasicThreadScope ts) {
		try {
			if (log.isLoggable(Level.FINER)) {
				LH.finer(log, "Sending to: \n  ** MultiProcessor Name: ", processor.getFullName(), "\n");
			}
			try {
				if (performanceLoggingLevel != null && log.isLoggable(performanceLoggingLevel)) {
					Object partitionId = state != null && state.getPartition() != null ? state.getPartition().getPartitionId() : null;
					long start = System.nanoTime();
					try {
						processor.processActions(iterator, state, ts);
						Action lra = iterator.getLastReturnedAction();
						if (lra != null)
							iterator.fireProcessActionDone(lra);
					} catch (Exception e) {
						Action action = iterator.getLastReturnedAction();
						iterator.fireHandleThrowable(action, e);
						long now = System.nanoTime();
						LH.log(log, performanceLoggingLevel, "PERFORMANCE: processActionException: pid=", partitionId, ", action=", OH.getSimpleClassName(action), "^",
								identity(action), ", proc= ", OH.getSimpleClassName(processor), ", nanos=", (now - start));
						throw e;
					}
					long now = System.nanoTime();
					LH.log(log, performanceLoggingLevel, "PERFORMANCE: processAction: pid=", partitionId, ", actions=", iterator.getReturnedCount(), ", proc= ",
							OH.getSimpleClassName(processor), ", nanos=", (now - start));
				} else {
					processor.processActions(iterator, state, ts);
				}

				actionsProcessedCount.addAndGet(iterator.getReturnedCount());
				if (state != null)
					((BasicStatePeer) state.getPeer()).countProcessAction += iterator.getReturnedCount();

				if (persistenceController.getIsAutoCommit())
					persistenceController.commitState(state);
			} catch (ProcessorException t) {
				if (t.getTargetProcessor() == null)
					t.setTargetProcessor(processor);
				if (t.getAction() == null)
					t.setAction(iterator.getLastReturnedAction());
				if (t.getTargetState() == null)
					t.setTargetState(state);
				if (t.getSourcePort() == null)
					t.setSourcePort(null);
				throw t;
			}
		} catch (Throwable t) {
			exceptionsCount.incrementAndGet();
			if (ts != null)
				ts.onProcessActionError(t);
			if (state != null)
				((BasicStatePeer) state.getPeer()).countThrowables++;
			dispatcher.handleThrowableFromProcessor(processor, iterator.getLastReturnedAction(), state, ts, t);
		} finally {
			if (ts != null)
				ts.onProcessActionEnd();
		}
	}
	@Override
	public RootPartitionActionRunner getRootPartitionRunner(String partitionId) {
		return this.stateQueues.get(partitionId);
	}

	@Override
	public void setDefaultFutureTimeoutMs(long ms) {
		getContainer().getResultActionFutureController().setDefaultFutureTimeoutMs(ms);
	}

	@Override
	public long getActionsInQueueCount() {
		long t = actionsProcessedCount.get();
		return actionsSubmittedCount.get() - t;
	}

	@Override
	public long getExceptionsCount() {
		return exceptionsCount.get();
	}
	@Override
	public long getActionsProcessedCount() {
		return actionsProcessedCount.get();
	}

	@Override
	public long getQueueTimeoutMs() {
		assertInit();
		return queueTimeoutMs;
	}

	@Override
	public int getQueueTimeoutCheckFrequency() {
		assertInit();
		return queueTimeoutCheckFrequency;
	}

	@Override
	public void setQueueTimeoutMs(long timeout) {
		if (this.stateQueues.size() > 0)
			throw new IllegalStateException("can not change after queues are already established");
		this.queueTimeoutMs = timeout;
	}

	@Override
	public void setQueueTimeoutCheckFrequency(int frequency) {
		if (this.stateQueues.size() > 0)
			throw new IllegalStateException("can not change after queues are already established");
		this.queueTimeoutCheckFrequency = frequency;
	}

}
