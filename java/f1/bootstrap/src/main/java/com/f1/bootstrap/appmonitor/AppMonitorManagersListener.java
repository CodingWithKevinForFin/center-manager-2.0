package com.f1.bootstrap.appmonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.f1.container.Container;
import com.f1.container.ContainerListener;
import com.f1.container.ContainerRuntimeListener;
import com.f1.container.ContainerScope;
import com.f1.container.ContainerServices;
import com.f1.container.DispatchController;
import com.f1.container.Partition;
import com.f1.container.PartitionController;
import com.f1.container.Port;
import com.f1.container.Processor;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.ThreadScopeController;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgConnectionListener;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgManager;
import com.f1.msg.MsgManagerListener;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerManager;
import com.f1.speedlogger.SpeedLoggerManagerListener;
import com.f1.speedlogger.SpeedLoggerSink;
import com.f1.utils.concurrent.FastSemaphore;
import com.f1.utils.db.Database;
import com.f1.utils.db.DatabaseManager;
import com.f1.utils.db.DatabaseManagerListener;

public class AppMonitorManagersListener implements ContainerListener, ContainerRuntimeListener, SpeedLoggerManagerListener, DatabaseManagerListener, MsgConnectionListener,
		MsgManagerListener {
	/**
	 * This class is simply responsible for ensuring that container items have listeners attached as early as possible.
	 */

	final private AppMonitorState state;

	public AppMonitorManagersListener(AppMonitorState state) {
		this.state = state;
	}

	@Override
	public void onPartitionCreated(PartitionController partitionController, Partition partition) {
		if (AppMonitorUtils.getExistingListener(state, partition.getProcessActionListeners(), AppMonitorProcessorListener.class) == null)
			partition.addProcessActionListener(onNewListener(new AppMonitorPartitionListener(state, partition, partitionController.getContainerScopeUid())));
	}

	final private FastSemaphore fs = new FastSemaphore();
	volatile private List<AppMonitorObjectListener<?, ?>> listeners = new ArrayList<AppMonitorObjectListener<?, ?>>();
	volatile private List<AppMonitorObjectListener<?, ?>> listenersSwap = new ArrayList<AppMonitorObjectListener<?, ?>>();
	private List<SpeedLoggerManager> speedLoggerManagers = new CopyOnWriteArrayList<SpeedLoggerManager>();

	private <T extends AppMonitorObjectListener> T onNewListener(T listener) {
		fs.aquire();
		try {
			listeners.add(listener);
		} finally {
			fs.release();
		}
		return listener;
	}

	//SHOULD ONLY BE CALLED BY ONE THREAD AT A TIME!!!
	public List<AppMonitorObjectListener<?, ?>> flushNewListeners() {
		if (listeners.size() == 0)
			return Collections.EMPTY_LIST;
		listenersSwap.clear();
		final List<AppMonitorObjectListener<?, ?>> t = listenersSwap;
		fs.aquire();
		try {
			listenersSwap = listeners;
			listeners = t;
		} finally {
			fs.release();
		}
		return listenersSwap;
	}
	@Override
	public void onPartitionRemoved(PartitionController partitionController, Partition partition) {

	}

	@Override
	public void onStateCreated(PartitionController partitionController, State state) {

	}

	@Override
	public void onStateRemoved(PartitionController partitionController, State state) {

	}

	@Override
	public void onThreadScopeCreated(ThreadScopeController threadScopeController, ThreadScope threadScope) {
		if (AppMonitorUtils.getExistingListener(state, threadScope.getProcessActionListeners(), AppMonitorProcessorListener.class) == null)
			threadScope.addProcessActionListener(onNewListener(new AppMonitorThreadscopeListener(state, threadScope, threadScopeController.getContainerScopeUid())));

	}

	@Override
	public void onThreadScopeRemoved(ThreadScopeController threadScopeController, ThreadScope threadScope) {

	}

	public void addLogManager(SpeedLoggerManager manager) {
		this.speedLoggerManagers.add(manager);
		manager.addSpeedLoggerManagerListener(this);
		for (String sink : manager.getSinkIds())
			onNewSink(manager.getSink(sink));
		for (String id : manager.getLoggerIds())
			onNewLogger(manager.getLogger(id));
	}

	public void addContainer(Container container) {
		container.addListener(this);
		container.addRuntimeListener(this);
		onContainerScopeAdded(container);
	}
	@Override
	public void onPreStart(Container container) {

	}

	private void ensureListener(ContainerScope cs) {
	}
	private void addDatabaseManager(DatabaseManager databaseManager) {
		databaseManager.addDatabaseManagerListener(this);
		for (String databaseName : databaseManager.getDatabases())
			onNewDatabase(databaseName, databaseManager.getDatabase(databaseName));
	}
	@Override
	public void onPostStart(Container container) {
	}

	@Override
	public void onPreStop(Container container) {

	}

	@Override
	public void onPostStop(Container container) {

	}

	@Override
	public void onPreStartDispatching(Container container) {

	}

	@Override
	public void onPostStartDispatching(Container container) {

	}

	@Override
	public void onPreStopDispatching(Container container) {
	}

	@Override
	public void onPostStopDispatching(Container container) {
	}

	@Override
	public void onNewLogger(SpeedLogger logger) {
		if (logger.getId() != null && logger.getId().startsWith(AppMonitorContainer.APPMONITOR))
			return;
		logger.addSpeedLoggerEventListener(onNewListener(new AppMonitorLoggerListener(state, logger)));
	}

	@Override
	public void onNewSink(SpeedLoggerSink sink) {
		sink.addSpeedLoggerEventListener(onNewListener(new AppMonitorLoggerSinkListener(state, sink)));
	}

	@Override
	public void onNewDatabase(String databaseId, Database database) {
		database.addDatabaseListener(onNewListener(new AppMonitorDatabaseListener(state, databaseId, database)));
	}

	@Override
	public void onContainerScopeAdded(ContainerScope containerScope) {
		if (containerScope instanceof Processor) {
			Processor processor = (Processor) containerScope;
			if (AppMonitorUtils.getExistingListener(state, processor.getProcessActionListeners(), AppMonitorProcessorListener.class) == null)
				processor.addProcessActionListener(onNewListener(new AppMonitorProcessorListener(state, processor)));
		} else if (containerScope instanceof DispatchController) {
			DispatchController dc = (DispatchController) containerScope;
			if (AppMonitorUtils.getExistingListener(state, dc.getProcessActionListeners(), AppMonitorDispatcherListener.class) == null)
				dc.addProcessActionListener(onNewListener(new AppMonitorDispatcherListener(state, dc)));
		} else if (containerScope instanceof Port) {
			onNewListener(new AppMonitorPortListener(state, (Port) containerScope));
		} else {
			onNewListener(new AppMonitorContainerScopeListener(state, containerScope));
			if (containerScope instanceof ContainerServices) {
				ContainerServices services = (ContainerServices) containerScope;
				addDatabaseManager(services.getDatabaseManager());
				addMsgManager(services.getMsgManager());
				//TODO:add msgManager listeners
			} else if (containerScope instanceof ThreadScopeController) {
				ThreadScopeController tsc = (ThreadScopeController) containerScope;
				for (ThreadScope threadScopes : tsc.getThreadScopes())
					onThreadScopeCreated(tsc, threadScopes);
			} else if (containerScope instanceof PartitionController) {
				PartitionController pc = (PartitionController) containerScope;
				for (Object partitionId : pc.getPartitions()) {
					Partition partition = pc.getPartition(partitionId);
					if (partition != null)
						onPartitionCreated(pc, partition);
				}
			}
		}
		for (ContainerScope child : containerScope.getChildContainerScopes()) {
			onContainerScopeAdded(child);
		}
	}

	private void addMsgManager(MsgManager msgManager) {
		if (msgManager.getListeners().contains(this))
			return;
		msgManager.addMsgManagerListener(this);
		for (String connectionName : msgManager.getConnections()) {
			MsgConnection connection = msgManager.getConnection(connectionName);
			onConnectionAdded(msgManager, connection);
		}
	}

	@Override
	public void onConnectionAdded(MsgManager manager, MsgConnection connection) {
		if (!connection.getListeners().contains(this))
			connection.addMsgConnectionListener(this);
		for (String inputTopicName : connection.getInputTopicNames()) {
			MsgInputTopic topic = connection.getInputTopic(inputTopicName);
			onNewInputTopic(connection, topic);
		}
		for (String inputTopicName : connection.getOutputTopicNames()) {
			MsgOutputTopic topic = connection.getOutputTopic(inputTopicName);
			onNewOutputTopic(connection, topic);
		}
	}

	@Override
	public void onDisconnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection) {
	}

	@Override
	public void onConnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection) {

	}

	@Override
	public void onNewInputTopic(MsgConnection connection, MsgInputTopic msgTopic) {
		msgTopic.getExternalConnections().size();
		if (AppMonitorUtils.getExistingListener(state, msgTopic.getMsgTopicListeners(), AppMonitorMsgTopicListener.class) == null) {
			msgTopic.addListener(onNewListener(new AppMonitorMsgTopicListener(state, msgTopic, connection, false)));
		}
	}

	@Override
	public void onNewOutputTopic(MsgConnection connection, MsgOutputTopic msgTopic) {
		if (AppMonitorUtils.getExistingListener(state, msgTopic.getMsgTopicListeners(), AppMonitorMsgTopicListener.class) == null) {
			msgTopic.addListener(onNewListener(new AppMonitorMsgTopicListener(state, msgTopic, connection, true)));
		}
	}

	public Iterable<SpeedLoggerManager> getSpeedLoggerManagers() {
		return this.speedLoggerManagers;
	}
}
